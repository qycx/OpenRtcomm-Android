package com.qycx;

import static com.qycx.base.qyMcMain_global.g.g_pQyMc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import com.qycx.base.SimpleTexture;
import com.qycx.base.camera.MyCamera;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.guiShare;
import com.qycx.base.myrecycleFunc;
import com.qycx.base.qdc.decObj_mediaCodec;
import com.qycx.base.qmcSharedObj;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.qySyncObj;
import com.qycx.base.saveTexture;
import com.qycx.base.showInfo_open;
import com.qycx.base.tmpFunc;
import com.qycx.base.util.CameraUtils;
import com.qycx.base.util.Log;
import com.qycx.base.util.ScaledDrawable2d;
import com.qycx.base.util.javatuples.Triplet;
import com.qycx.gleslibrary.Drawable2d;
import com.qycx.gleslibrary.EglCore;
import com.qycx.gleslibrary.GlUtil;
import com.qycx.gleslibrary.Sprite2d;
import com.qycx.gleslibrary.Texture2dProgram;
import com.qycx.gleslibrary.WindowSurface;

import java.io.IOException;

/**
 * Thread that handles all rendering and camera operations.
 */
public class camera_RenderThread extends Thread implements SurfaceTexture.OnFrameAvailableListener {

    //
    private static int DEFAULT_ZOOM_PERCENT = 0;      // 0-100
    private static int DEFAULT_SIZE_PERCENT = 50;     // 0-100
    private static int DEFAULT_ROTATE_PERCENT = 0;    // 0-100

    // Requested values; actual may differ.
    private static int REQ_CAMERA_WIDTH = 480;//1280;
    private static int REQ_CAMERA_HEIGHT = 640;//720;
    private static int REQ_CAMERA_FPS = 30;

    private final static int MSG_SEND_CAMERA_PARAMS0 = 0;
    private final static int MSG_SEND_CAMERA_PARAMS1 = 1;
    private final static int MSG_SEND_RECT_SIZE = 2;
    private final static int MSG_SEND_ZOOM_AREA = 3;
    private final static int MSG_SEND_ROTATE_DEG = 4;


    // Object must be created on render thread to get correct Looper, but is used from
    // UI thread, so we need to declare it volatile to ensure the UI thread sees a fully
    // constructed Object.
    public camera_RenderHandler mHandler;

    // Used to wait for the thread to start.
    private Object mStartLock = new Object();
    private boolean mReady = false;

    //
    private boolean mThreadEnd = false;

    //
    private Handler mMainHandler;
    EGLContext mEglContext_shared;
    int miIndex_sharedObj;

    //
    //boolean m_bOrientation_landscape;
    boolean m_bFront;
    //
    int m_surfaceOrientation;

    //
    boolean m_bUse_camera1 = false;

    //
    private Camera mCamera1;

    //
    public MyCamera myCamera;


    //
    public int mCameraPreviewWidth, mCameraPreviewHeight;

    //
    //  2026/09/14: 摄像头断流自愈——看门狗 + 重建。
    //  背景：camera2 设备断开/出错/静默断流（repeating request 失效、surface 失效等）
    //  之后 onFrameAvailable 永远不再来，采集静默停止。三层防线：
    //  ① MyCamera.onDisconnected/onError 直接发 CONST_msg_cameraLost 触发重建；
    //  ② 看门狗每 5s 检查，距最后成功帧超 10s 自动重建（兜一切静默断流）；
    //  ③ frameAvailable 的瞬时失败不再整线程 shutdown（防误杀）。
    int m_reqW, m_reqH;                          // run() 里算好的申请尺寸，重建时复用
    volatile long m_tick_lastFrameAvail = 0;     // 最后一次成功处理帧的时间戳（0=首帧未到）
    volatile long m_tick_lastRebuild = 0;        // 上次触发重建的时间戳（防抖）
    volatile long m_tick_surfaceReady = 0;       // surfaceAvailable 完成时间戳（首帧宽限用）
    volatile boolean m_bCameraRestarting = false;// 重建中（openCamera 已提交、等 onOpened）
    static final long WATCHDOG_INTERVAL_MS = 5000;
    static final long WATCHDOG_NOFRAME_MS = 10000;
    static final long REBUILD_DEBOUNCE_MS = 5000;
    //  2026/09/14: 放在外部类——notifyCameraOpen（外部类方法）也要用，
    //  内部类成员不会进入外部类作用域（简单名不可见，编译报"找不到符号"）。
    static final int MSG_WATCHDOG = 10;

    //
    private EglCore mEglCore;
    private WindowSurface mWindowSurface;
    private int mWindowSurfaceWidth;
    private int mWindowSurfaceHeight;

    // Receives the output from the camera preview.
    private SurfaceTexture mCameraTexture;

    // Orthographic projection matrix.
    private float[] mDisplayProjectionMatrix = new float[16];
    private float[] mDisplayProjectionMatrix_invert = new float[16];


    private Texture2dProgram mTexProgram;
    private static ScaledDrawable2d mRectDrawable = new ScaledDrawable2d(Drawable2d.Prefab.RECTANGLE);
    private Sprite2d mRect = new Sprite2d(mRectDrawable);

    private int mZoomPercent = DEFAULT_ZOOM_PERCENT;
    private int mSizePercent = DEFAULT_SIZE_PERCENT;
    private int mRotatePercent = DEFAULT_ROTATE_PERCENT;
    private float mPosX, mPosY;

    public decObj_mediaCodec.TMP_frameBuffer tmpFrameBuffer_0 = new decObj_mediaCodec.TMP_frameBuffer();        // camera_w*camera_h
    public decObj_mediaCodec.TMP_frameBuffer tmpFrameBuffer_int = new decObj_mediaCodec.TMP_frameBuffer();      // camera_big*camera_big
    public decObj_mediaCodec.TMP_frameBuffer tmpFrameBuffer_rotate = new decObj_mediaCodec.TMP_frameBuffer();  //
    public decObj_mediaCodec.TMP_frameBuffer tmpFrameBuffer_o = new decObj_mediaCodec.TMP_frameBuffer();       // h*w

    //
    public SimpleTexture m_simpleTex_0_int;
    public SimpleTexture m_simpleTex_rotate;
    public SimpleTexture m_simpleTex_o;


    /**
     * Constructor.  Pass in the MainHandler, which allows us to send stuff back to the
     * Activity.
     */
    public camera_RenderThread(Handler mainHandler, EGLContext eglContext_shared, int index_sharedObj, boolean bFront, int surfaceOrientation) {
        mMainHandler = mainHandler;
        mEglContext_shared = eglContext_shared;
        //
        miIndex_sharedObj = index_sharedObj;
        //
        m_bFront = bFront;
        //
        m_surfaceOrientation = surfaceOrientation;

        return;
    }

    /** 屏幕旋转时更新预览方向，避免 teardown 摄像头造成数秒卡顿。 */
    public void updateSurfaceOrientation(int surfaceOrientation) {
        m_surfaceOrientation = surfaceOrientation;
    }

    /**
     * Thread entry point.
     */
    @Override
    public void run() {
        ctxQyMc.CCtxQyMc pQyMc  =  g_pQyMc;
        if (pQyMc == null) {
            Log.e("qyMcMain_global.g.g_pQyMc == null");
            releaseCamera();
            return;
        }
        ctxQmc.CCtxQmc  pProcInfo  =  pQyMc.get_pProcInfo();
        if  (  pProcInfo==null)  {
            Log.e("pProcInfo == null");
            releaseCamera();
            return;
        }


        //
        Looper.prepare();

        // We need to create the Handler before reporting ready.
        mHandler = new camera_RenderHandler(this);

        // Prepare EGL and open the camera before we start handling messages.
        EGLContext eglContext = null;
        eglContext = this.mEglContext_shared;
        try {
            mEglCore = new EglCore(eglContext, 0);
        } catch (Exception e) {
            Log.e("camera_Render.Run failed, new EglCore exception");
            free();
            return;
        }
        ///
        int reqW = REQ_CAMERA_WIDTH;
        int reqH = REQ_CAMERA_HEIGHT;
        int reqFps = REQ_CAMERA_FPS;

        //
        reqW = 1080;
        reqH = 1920;

        //
        if  (  pProcInfo.cfg.bUseHorizontalScreen)  {
            int  tmp_iVal  =  reqW;
            reqW  =  reqH;
            reqH  =  tmp_iVal;
        }

        //

        if  (  pProcInfo.bUse100k) {

            reqW = pProcInfo.cfgInfo100k.use100k_v_w;
            reqH = pProcInfo.cfgInfo100k.use100k_v_h;


        }



        //  2026/09/14: 记录申请尺寸，断流看门狗重建摄像头时复用同一套参数。
        this.m_reqW = reqW;
        this.m_reqH = reqH;

        //
        openCamera(reqW, reqH, reqFps, m_bFront);

        Looper.loop();
        free();
    }

    private void free()
    {
        Log.d("looper quit");
        if (this.tmpFrameBuffer_0.bDone_createEnv)
        {
            this.tmpFrameBuffer_0.deleteEnv();
            this.tmpFrameBuffer_0.bDone_createEnv = false;
        }
        if (this.tmpFrameBuffer_o.bDone_createEnv)
        {
            this.tmpFrameBuffer_o.deleteEnv();
            this.tmpFrameBuffer_o.bDone_createEnv = false;
        }
//
        if (this.tmpFrameBuffer_rotate.bDone_createEnv)
        {
            this.tmpFrameBuffer_rotate.deleteEnv();
            this.tmpFrameBuffer_rotate.bDone_createEnv = false;
        }

//
        if (this.tmpFrameBuffer_int.bDone_createEnv)
        {
            this.tmpFrameBuffer_int.deleteEnv();
            this.tmpFrameBuffer_int.bDone_createEnv = false;
        }

//
        if (this.m_simpleTex_0_int != null)
        {
            this.m_simpleTex_0_int.destroy(); this.m_simpleTex_0_int = null;
        }
        if (this.m_simpleTex_rotate != null)
        {
            this.m_simpleTex_rotate.destroy(); this.m_simpleTex_rotate = null;
        }
        if (this.m_simpleTex_o != null)
        {
            this.m_simpleTex_o.destroy(); this.m_simpleTex_o = null;
        }

//
        releaseCamera();
        releaseGl();
        if (mEglCore != null)
        {
            mEglCore.release();
        }

//synchronized(mStartLock)
        {
            mReady = false;
        }

//
        mThreadEnd = true;

//
        showInfo_open.qyFuncs.showInfo_open(0, null, "renderThread.run ends");
    }
    public int notifyCameraOpen() {

        //
        //  2026/09/14: 断流重建路径——onOpened 之后主流程不会再发 surfaceAvailable
        //  （me_st/WindowSurface/mCameraTexture 都还在），这里直接走 finishSurfaceSetup
        //  重启预览，采集即刻恢复。
        if (this.m_bCameraRestarting) {
            this.m_bCameraRestarting = false;
            Log.d("atdx", "notifyCameraOpen: camera rebuilt, restart preview directly");
            if (!this.m_bUse_camera1 && myCamera != null) {
                //  刷新预览尺寸（同参数重选理论上一致，防 ROM 差异）
                mCameraPreviewWidth = myCamera.mVideoSize.getWidth();
                mCameraPreviewHeight = myCamera.mVideoSize.getHeight();
            }
            if (this.mCameraTexture != null) {
                finishSurfaceSetup();
            }
            return 0;
        }

        //
        if (!this.m_bUse_camera1) {
            mCameraPreviewWidth = myCamera.mVideoSize.getWidth();
            mCameraPreviewHeight = myCamera.mVideoSize.getHeight();
            int thousandFps = 30000;
            //
            sendCameraParams(mMainHandler, mCameraPreviewWidth, mCameraPreviewHeight,
                    thousandFps / 1000.0f);
        }

        //
        mReady = true;

        //  2026/09/14: 首次就绪后启动无帧看门狗（handler 消息自续，looper 退出即停）。
        this.mHandler.removeMessages(MSG_WATCHDOG);
        this.mHandler.sendEmptyMessageDelayed(MSG_WATCHDOG, WATCHDOG_INTERVAL_MS);

        //
        return 0;
    }

    //
    //  2026/09/14: 无帧看门狗 tick——由 camera_RenderHandler 的 MSG_WATCHDOG 调用
    //  （camera 线程）。距最后一次成功处理帧超过 WATCHDOG_NOFRAME_MS 即重建摄像头。
    //  兜底一切静默断流：设备被回收、repeating request 失效、surface 失效等。
    public void watchdogTick() {
        if (this.m_bUse_camera1) return;              // camera1 路径维持原状不动

        long now = SystemClock.uptimeMillis();

        if (this.mCameraTexture == null) return;      // surfaceAvailable 尚未到达（首启时序由 initVideoCapDev 管）

        if (this.m_tick_lastFrameAvail == 0) {
            //  2026/09/14: 首帧从未到达——preview 起来但一直没出图（如 session
            //  onConfigureFailed 静默失败、设备在 surface 就绪前丢失）。给启动链路
            //  20s 宽限，超时即重建，避免"从启动起就采集不到"的死区。
            if (this.m_tick_surfaceReady != 0 && now - this.m_tick_surfaceReady > 20000) {
                Log.e("atdx", "watchdog: first camera frame never arrived (>20s after surface ready), rebuild camera");
                rebuildCamera();
            }
            return;
        }

        if (this.m_bCameraRestarting) {
            //  重建已提交但 onOpened 超 15s 不来（camera 服务异常）——强制再来一轮。
            if (now - this.m_tick_lastRebuild > 15000) {
                Log.e("atdx", "watchdog: rebuild pending >15s, force retry");
                rebuildCamera();
            }
            return;
        }

        long noFrameMs = now - this.m_tick_lastFrameAvail;
        if (noFrameMs > WATCHDOG_NOFRAME_MS) {
            Log.e("atdx", "watchdog: no camera frame for " + noFrameMs + "ms, rebuild camera");
            rebuildCamera();
        }
    }

    //
    //  2026/09/14: 重建摄像头（必须在 camera 线程调用：MSG_cameraLost / MSG_WATCHDOG
    //  / MSG_SHUTDOWN 都在本线程 handler 处理）。5s 防抖防重建风暴；重建期间帧回调
    //  自行丢弃。onOpened → notifyCameraOpen 走 restart 分支直接重启预览。
    public void rebuildCamera() {
        if (this.m_bUse_camera1) return;

        long now = SystemClock.uptimeMillis();
        long sinceLast = now - this.m_tick_lastRebuild;
        if (this.m_tick_lastRebuild != 0 && sinceLast < REBUILD_DEBOUNCE_MS) {
            //  防抖：太密集则推迟，避免 camera 服务异常时重建风暴。
            this.mHandler.removeMessages(guiShare.Consts.CONST_msg_cameraLost);
            this.mHandler.sendEmptyMessageDelayed(guiShare.Consts.CONST_msg_cameraLost,
                    REBUILD_DEBOUNCE_MS - sinceLast);
            return;
        }
        this.m_tick_lastRebuild = now;
        this.m_bCameraRestarting = true;

        try {
            Log.d("atdx", "rebuildCamera: release old camera, lastFrame " +
                    (this.m_tick_lastFrameAvail == 0 ? -1 : (now - this.m_tick_lastFrameAvail)) + "ms ago");
            releaseCamera();
            this.myCamera = new MyCamera(this.mHandler);
            //  MyCamera.openCamera 是 3 参（宽/高/前后置）——fps 仅 camera1 路径使用
            this.myCamera.openCamera(this.m_reqW, this.m_reqH, this.m_bFront);
            Log.d("atdx", "rebuildCamera: openCamera submitted, waiting onOpened");
        } catch (Exception e) {
            Log.e("atdx", "rebuildCamera: exception " + e);
            //  openCamera 抛异常=本轮没提交成功，允许看门狗下轮再试。
            this.m_bCameraRestarting = false;
        }
    }


    /**
     * Waits until the render thread is ready to receive messages.
     * <p>
     * Call from the UI thread.
     */
    public int waitUntilReady() {
        //synchronized(mStartLock)
        {
            while (!mReady) {
                //
                if (mThreadEnd) return -1;

                //
                try {
                    //mStartLock.wait();
                    tmpFunc.qyFuncs.mySleep(1);
                } catch (Exception ie) { /* not expected */ }
            }
        }

        return 0;
    }

    /**
     * Shuts everything down.
     */
    public void shutdown() {
        Log.d("shutdown");
        Looper.myLooper().quit();
    }

    /**
     * Returns the render thread's Handler.  This may be called from any thread.
     */
    public camera_RenderHandler getHandler() {
        return mHandler;
    }

    /**
     * Handles the surface-created callback from SurfaceView.  Prepares GLES and the Surface.
     */
    public void surfaceAvailable(SurfaceTexture st, boolean newSurface) {
        //Surface surface = holder.getSurface();
        //mWindowSurface = new WindowSurface(mEglCore, surface, false);
        mWindowSurface = new WindowSurface(mEglCore, st);
        //
        mWindowSurface.makeCurrent();

        //  create and configure the SurfaceTexture, which will receive frames from the
        // camera.  We set the textured rect's program to render from it.
        mTexProgram = new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT);
        int textureId = mTexProgram.createTextureObject();
        mCameraTexture = new SurfaceTexture(textureId);
        mRect.setTexture(textureId);

        mCameraTexture.setDefaultBufferSize(this.mCameraPreviewWidth, this.mCameraPreviewHeight);

        //if (!newSurface)
        {
            // This Surface was established on a previous run, so no surfaceChanged()
            // message is forthcoming.  Finish the surface setup now.
            //
            // We could also just call this unconditionally, and perhaps do an unnecessary
            // bit of reallocating if a surface-changed message arrives.
            mWindowSurfaceWidth = mWindowSurface.getWidth();
            mWindowSurfaceHeight = mWindowSurface.getHeight();
            finishSurfaceSetup();
        }
        mCameraTexture.setOnFrameAvailableListener(this);
        //  2026/09/14: 记录 surface 就绪时刻，看门狗首帧宽限判断用。
        this.m_tick_surfaceReady = SystemClock.uptimeMillis();
    }

    /**
     * Releases most of the GL resources we currently hold (anything allocated by
     * surfaceAvailable()).
     * <p>
     * Does not release EglCore.
     */
    private void releaseGl() {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;

        if (pQyMc!=null && !pQyMc.b_isHarmony) {
            GlUtil.checkGlError("releaseGl start");
        }

        //
        if (mWindowSurface != null) {
            mWindowSurface.release();
            mWindowSurface = null;
        }
        if (mTexProgram != null) {
            mTexProgram.release();
            mTexProgram = null;
        }
        if (pQyMc!=null && !pQyMc.b_isHarmony) {
            GlUtil.checkGlError("releaseGl done");
        }

        //
        if (mEglCore != null) {
            mEglCore.makeNothingCurrent();
        }
    }

    /**
     * Handles the surfaceChanged message.
     * <p>
     * We always receive surfaceChanged() after surfaceCreated(), but surfaceAvailable()
     * could also be called with a Surface created on a previous run.  So this may not
     * be called.
     */
    public void surfaceChanged(int width, int height) {
        Log.d("RenderThread surfaceChanged " + width + "x" + height);

        mWindowSurfaceWidth = width;
        mWindowSurfaceHeight = height;
        finishSurfaceSetup();
    }

    /**
     * Handles the surfaceDestroyed message.
     */
    public void surfaceDestroyed() {
        // In practice this never appears to be called -- the activity is always paused
        // before the surface is destroyed.  In theory it could be called though.
        Log.d("RenderThread surfaceDestroyed");
        releaseGl();
    }

    /**
     * Sets up anything that depends on the window size.
     * <p>
     * Open the camera (to set mCameraAspectRatio) before calling here.
     */
    private void finishSurfaceSetup() {
        int width = mWindowSurfaceWidth;
        int height = mWindowSurfaceHeight;
        Log.d("finishSurfaceSetup size=" + width + "x" + height +
                " camera=" + mCameraPreviewWidth + "x" + mCameraPreviewHeight);

        // Use full window.
        GLES20.glViewport(0, 0, width, height);

        // Simple orthographic projection, with (0,0) in lower-left corner.
        Matrix.orthoM(mDisplayProjectionMatrix, 0, 0, width, 0, height, -1, 1);
        //
        Matrix.orthoM(mDisplayProjectionMatrix_invert, 0, 0, width, height, 0, -1, 1);


        // Default position is center of screen.
        mPosX = width / 2.0f;
        mPosY = height / 2.0f;

        updateGeometry();

        // Ready to go, start the camera.
        Log.d("starting camera preview");
        if (m_bUse_camera1) {
            try {
                mCamera1.setPreviewTexture(mCameraTexture);
            } catch (IOException ioe) {
                Log.e(ioe.getMessage());
                return;
            }
            mCamera1.startPreview();
        } else {
            Log.d("not finished, startPreview");
            myCamera.startPreview(mCameraTexture);
        }
    }

    /**
     * Updates the geometry of mRect, based on the size of the window and the current
     * values set by the UI.
     */
    private void updateGeometry() {
        int width = mWindowSurfaceWidth;
        int height = mWindowSurfaceHeight;

        int smallDim = Math.min(width, height);
        // max scale is a bit larger than the screen, so we can show over-size.
        float scaled = smallDim * (mSizePercent / 100.0f) * 1.25f;
        float cameraAspect = (float) mCameraPreviewWidth / mCameraPreviewHeight;
        int newWidth = Math.round(scaled * cameraAspect);
        int newHeight = Math.round(scaled);

        float zoomFactor = 1.0f - (mZoomPercent / 100.0f);
        int rotAngle = Math.round(360 * (mRotatePercent / 100.0f));

        newWidth = width;
        newHeight = height;

        mRect.setScale(newWidth, newHeight);
        mRect.setPosition(mPosX, mPosY);
        mRect.setRotation(rotAngle);
        mRectDrawable.setScale(zoomFactor);

        sendRectSize(mMainHandler, newWidth, newHeight);
        sendZoomArea(mMainHandler, Math.round(mCameraPreviewWidth * zoomFactor),
                Math.round(mCameraPreviewHeight * zoomFactor));
        sendRotateDeg(mMainHandler, rotAngle);
    }

    int cnt;

    // SurfaceTexture.OnFrameAvailableListener; runs on arbitrary thread
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        //
        boolean bDbg = false;
        String str;

        cnt++;

        //
        mHandler.sendFrameAvailable();
    }

    public int m_loopCtrl  =  0;
    //  2026/09/14: 瞬时失败连续计数——frameAvailable 返回值语义改为
    //  0=成功 / 1=瞬时失败（打日志等下一帧）/ -1=致命（线程退出）。
    //  之前任何一步失败都 sendShutdown 整线程退出=摄像头被释放无人重启。
    int m_nTransientFail = 0;
    static final int TRANSIENT_FAIL_LIMIT = 300;

    /**
     * Handles incoming frame of data from the camera.
     */
    public int frameAvailable() {
        int iErr = -1;

        boolean bDbg = false;
        String str;
        boolean bDumpTex = false;

        //
        mCameraTexture.updateTexImage();

        //
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (pQyMc == null) {
            Log.e("frameAvailable pQyMc == null");
            return iErr;
        }
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmcSharedObj.QY_SHARED_OBJ pSharedObj = qmcSharedObj.qyFuncs.getSharedObjByIndex(pProcInfo, this.miIndex_sharedObj);
        if (pSharedObj == null) {
            //  2026/09/14: 共享对象瞬时取不到（上游结构重建中）不该杀死整个采集线程。
            //  连续超过 TRANSIENT_FAIL_LIMIT 次才判定致命。
            this.m_nTransientFail++;
            if (this.m_nTransientFail >= TRANSIENT_FAIL_LIMIT) {
                Log.e("frameAvailable: pSharedObj null x" + this.m_nTransientFail + ", fatal");
                return iErr;
            }
            if (this.m_nTransientFail % 30 == 1) {
                Log.d("atdx", "frameAvailable: pSharedObj null x" + this.m_nTransientFail + " (transient)");
            }
            return 1;
        }
        //  取到共享对象即清瞬时计数
        this.m_nTransientFail = 0;

        //
        m_loopCtrl  ++  ;
        if ( true )  {
            if  (  (  m_loopCtrl  %  100  )  ==  0  ) {
                //Log.d("anc", "camera_render.frameAvail "  +  m_loopCtrl);
            }
        }

        //
        if  (  true ) {
        }


        //
        int w = this.mWindowSurfaceWidth;// mCameraPreviewWidth;
        int h = this.mWindowSurfaceHeight;// mCameraPreviewHeight;

        //
        //qyFuncs_Droid.myGLClearError();
        boolean bRtt = true;

        //
        if (!this.tmpFrameBuffer_0.bDone_createEnv) {
            this.tmpFrameBuffer_0.createEnv(w, h, "camera 0");
            this.tmpFrameBuffer_0.bDone_createEnv = true;
        }

        //
        try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {

            //
            if (bRtt) {
                this.tmpFrameBuffer_0.bindFrameBufferInfo();
                //
                GLES20.glViewport(0, 0, w, h);

            }

            //
            draw(bRtt);

            //
            if (bRtt) {
                this.tmpFrameBuffer_0.unbindFrameBufferInfo();
            }

            //

            //
            if (bDumpTex) {
                Bitmap bmp = saveTexture.qyFuncs_Droid.texture2Bmp(this.tmpFrameBuffer_0.mTexture[0], w, h);
                guiShare.GuiShare.pf_dumpBmp.call(bmp, "tmpTex_0");
                bmp.recycle();
            }

            //
            if (bRtt) {
                int wh = Math.max(w, h);

                if (!this.tmpFrameBuffer_int.bDone_createEnv) {
                    this.tmpFrameBuffer_int.createEnv(wh, wh, "camera.int");
                    this.tmpFrameBuffer_int.bDone_createEnv = true;
                }

                this.tmpFrameBuffer_int.bindFrameBufferInfo();
                GLES20.glViewport(0, 0, wh, wh);

                //
                Context context = MyApplication.getContext();
                int texId = tmpFrameBuffer_0.mTexture[0];

                //
                //
                if (this.m_simpleTex_0_int == null) {
                    this.m_simpleTex_0_int = new SimpleTexture(context, texId, 0,0,false, "camera0->int");
                    //
                    this.m_simpleTex_0_int.resize(wh, wh, 0);
                }
                //
                this.m_simpleTex_0_int.draw(0);


                //
                this.tmpFrameBuffer_int.unbindFrameBufferInfo();

                //
                if (bDumpTex) {
                    Bitmap bmp = saveTexture.qyFuncs_Droid.texture2Bmp(this.tmpFrameBuffer_int.mTexture[0], wh, wh);
                    guiShare.GuiShare.pf_dumpBmp.call(bmp, "tmpTex_int");
                    bmp.recycle();
                }

                //////////////////////////////
                //
                if (!this.tmpFrameBuffer_rotate.bDone_createEnv) {
                    this.tmpFrameBuffer_rotate.createEnv(wh, wh, "camera.rotate");
                    this.tmpFrameBuffer_rotate.bDone_createEnv = true;
                }

                this.tmpFrameBuffer_rotate.bindFrameBufferInfo();
                GLES20.glViewport(0, 0, wh, wh);

                //
                texId = tmpFrameBuffer_int.mTexture[0];

                //
                int tex_o_w = 0;
                int tex_o_h = 0;
                //
                {
                    if (this.m_simpleTex_rotate == null) {
                        this.m_simpleTex_rotate = new SimpleTexture(context, texId, 0,0,false, "camera.rotate");
                    }
                    //
                    int angle_z = 90;
                    //
                    int ii = this.myCamera.mSensorOrientation;

                    Triplet<Integer, Integer, Integer> o_info = myrecycleFunc.qyFuncs_Droid.get_tex_o_info(myCamera.mSensorOrientation, m_surfaceOrientation, mCameraPreviewWidth, mCameraPreviewHeight);
                    angle_z = o_info.getValue0();
                    tex_o_w = o_info.getValue1();
                    tex_o_h = o_info.getValue2();

                    this.m_simpleTex_rotate.resize(wh, wh, angle_z);
                    this.m_simpleTex_rotate.draw(angle_z);

                }

                //
                this.tmpFrameBuffer_rotate.unbindFrameBufferInfo();


                if (bDumpTex) {
                    Bitmap bmp = saveTexture.qyFuncs_Droid.texture2Bmp(this.tmpFrameBuffer_rotate.mTexture[0], wh, wh);
                    guiShare.GuiShare.pf_dumpBmp.call(bmp, "tmpTex_rotate");
                    bmp.recycle();
                }

                if (0 != syncObj.sync("", pSharedObj.var.hMutex_syncEncFrame,"render.frameAvail.833")) {
                    str = String.format("camera.frameAvail: sync failed (transient)");
                    Log.e(str);
                    this.m_nTransientFail++;
                    return this.m_nTransientFail >= TRANSIENT_FAIL_LIMIT ? iErr : 1;
                }

                //
                int o_w = tex_o_w;// mCameraPreviewHeight;
                int o_h = tex_o_h;// mCameraPreviewWidth;

                //
                if (!this.tmpFrameBuffer_o.bDone_createEnv) {
                    this.tmpFrameBuffer_o.createEnv(o_w, o_h, "camera.o");
                    this.tmpFrameBuffer_o.bDone_createEnv = true;
                }

                this.tmpFrameBuffer_o.bindFrameBufferInfo();
                GLES20.glViewport(0, 0, o_w, o_h);

                //
                texId = tmpFrameBuffer_rotate.mTexture[0];

                //
                //

                if (this.m_simpleTex_o == null) {
                    this.m_simpleTex_o = new SimpleTexture(context, texId, 0,0,false, "camera->o");
                }

                //
                // 在这里设置灰度化程度
                // 设置 50% 的灰度效果。
                // 在实际应用中，iHd 的值应该从外部配置
                int iHd = 0;//100; // 这是一个示例值，范围 0-100
                if  (  pProcInfo.bUse100k)  {
                    iHd=pProcInfo.use100k_ucHd;
                }
                this.m_simpleTex_o.setGrayscale(iHd);

                //
                this.m_simpleTex_o.resize(o_w, o_h, 0);
                this.m_simpleTex_o.draw(0);
                //
                this.tmpFrameBuffer_o.unbindFrameBufferInfo();


                if (bDumpTex) {
                    Bitmap bmp = saveTexture.qyFuncs_Droid.texture2Bmp(this.tmpFrameBuffer_o.mTexture[0], o_w, o_h);
                    guiShare.GuiShare.pf_dumpBmp.call(bmp, "tmpTex_o");
                    bmp.recycle();
                }


                //
                //
                if (pSharedObj.var.m_o_texId != this.tmpFrameBuffer_o.mTexture[0]) {
                    pSharedObj.var.m_o_texId = this.tmpFrameBuffer_o.mTexture[0];
                }
                if (pSharedObj.var.m_o_iW != tex_o_w) {
                    pSharedObj.var.m_o_iW = tex_o_w;
                }
                if (pSharedObj.var.m_o_iH != tex_o_h) {
                    pSharedObj.var.m_o_iH = tex_o_h;
                }


                //
            }
            syncObj.Dispose();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(e.getLocalizedMessage());
            //  2026/09/14: GL 处理异常多为瞬时（surface/EGL 抖动），连续超限才致命。
            this.m_nTransientFail++;
            return this.m_nTransientFail >= TRANSIENT_FAIL_LIMIT ? iErr : 1;
        }

        //
        pSharedObj.var.bFrameAvail = true;
        tmpFunc.qyFuncs.mySetEvent(pSharedObj.var.hEvt_notifyFrameAvail);

        //
        //this.tmpFrameBuffer.deleteEnv();

        //
        //  2026/09/14: 记录最后成功帧时间戳，供无帧看门狗判断断流。
        this.m_tick_lastFrameAvail = SystemClock.uptimeMillis();
        this.m_nTransientFail = 0;
        iErr = 0;


        return iErr;

    }

    /**
     * Draws the scene and submits the buffer.
     */
    public void draw(boolean bRtt) {
        GlUtil.checkGlError("draw start");

        //
        ctxQyMc.CCtxQyMc  pQyMc = g_pQyMc;
        if  (  pQyMc==null) {
            Log.e("anc","camera.render.draw: failed, pQyMc is null");
            return;
        }
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if  (  pProcInfo==null)  {
            Log.e("anc","camera.render.draw: failed, pProcInfo is null");
            return;
        }

        //
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //
        if (!bRtt) {
            mRect.draw(mTexProgram, mDisplayProjectionMatrix);
        } else {
            //mRect.draw(mTexProgram, mDisplayProjectionMatrix_invert);
            mRect.draw(mTexProgram, mDisplayProjectionMatrix);
        }
        //
        boolean tmp_bRet;
        tmp_bRet=true;
        if  (  !bRtt) {
            //
            Log.d("anc","camera.render.draw: 这个似乎没用，但是暂时保留. l784"  );
            //
            tmp_bRet  =  mWindowSurface.swapBuffers();
        }
        if  (  !tmp_bRet) {
            //
            Log.d("anc", "camera_RenderThread: swapBuffer failed");
            //
            pProcInfo.m_camera_n_swapBuffersFailed++;

        }
        else  {
              //
              if  (  pProcInfo.m_camera_n_swapBuffersFailed!=0) {
                  //
                  Log.d("anc", "camera_RenderThread: swapBuffer recovered");
                  //
                  pProcInfo.m_camera_n_swapBuffersFailed = 0;
              }
        }
        //
        GlUtil.checkGlError("draw done");
    }

    public void setZoom(int percent) {
        mZoomPercent = percent;
        updateGeometry();
    }

    public void setSize(int percent) {
        mSizePercent = percent;
        updateGeometry();
    }

    public void setRotate(int percent) {
        mRotatePercent = percent;
        updateGeometry();
    }

    public void setPosition(int x, int y) {
        mPosX = x;
        mPosY = mWindowSurfaceHeight - y;   // GLES is upside-down
        updateGeometry();
    }

    private void openCamera(int desiredWidth, int desiredHeight, int desiredFps, boolean bFront) {
        if (m_bUse_camera1) {
            Log.d("openCamera1(desiredWidth, desir");
            openCamera1(desiredWidth, desiredHeight, desiredFps);
            //
            this.notifyCameraOpen();
        } else {
            Log.d("openCamera(desiredWidth, desiredHeight");
            try {
                this.myCamera = new MyCamera(this.mHandler);
                //
                this.myCamera.openCamera(desiredWidth, desiredHeight, bFront);
            } catch (Exception e) {
                Log.d("camera_RenderThread.openCamera, exception");
            }
        }
    }

    private void releaseCamera() {
        if (m_bUse_camera1) {
            Log.d(" releaseCamera1();");
            releaseCamera1();
        } else {
            if (this.myCamera != null) {
                Log.d(".closeCamera();");

                myCamera.closeCamera();
                myCamera = null;
            }
        }
    }


    /**
     * Opens a camera, and attempts to establish preview mode at the specified width
     * and height with a fixed frame rate.
     * <p>
     * Sets mCameraPreviewWidth / mCameraPreviewHeight.
     */
    private void openCamera1(int desiredWidth, int desiredHeight, int desiredFps) {
        if (mCamera1 != null) {
            Log.e("camera already initialized");
            return;
        }


        Camera.CameraInfo info = new Camera.CameraInfo();

        // Try to find a front-facing camera (e.g. for videoconferencing).
        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mCamera1 = Camera.open(i);
                break;
            }
        }
        if (mCamera1 == null) {
            Log.d("No front-facing camera found; opening default");
            mCamera1 = Camera.open();    // opens first back-facing camera
        }
        if (mCamera1 == null) {
            Log.e("Unable to open camera");
            return;
        }

        Camera.Parameters parms = mCamera1.getParameters();

        CameraUtils.choosePreviewSize(parms, desiredWidth, desiredHeight);

        // Try to set the frame rate to a constant value.
        int thousandFps = CameraUtils.chooseFixedPreviewFps(parms, desiredFps * 1000);

        // Give the camera a hint that we're recording video.  This can have a big
        // impact on frame rate.
        parms.setRecordingHint(true);

        mCamera1.setParameters(parms);

        int[] fpsRange = new int[2];
        Camera.Size mCameraPreviewSize = parms.getPreviewSize();
        parms.getPreviewFpsRange(fpsRange);
        String previewFacts = mCameraPreviewSize.width + "x" + mCameraPreviewSize.height;
        if (fpsRange[0] == fpsRange[1]) {
            previewFacts += " @" + (fpsRange[0] / 1000.0) + "fps";
        } else {
            previewFacts += " @[" + (fpsRange[0] / 1000.0) +
                    " - " + (fpsRange[1] / 1000.0) + "] fps";
        }
        Log.i("Camera config: " + previewFacts);

        mCameraPreviewWidth = mCameraPreviewSize.width;
        mCameraPreviewHeight = mCameraPreviewSize.height;
        sendCameraParams(mMainHandler, mCameraPreviewWidth, mCameraPreviewHeight,
                thousandFps / 1000.0f);
    }

    /**
     * Stops camera preview, and releases the camera to the system.
     */
    private void releaseCamera1() {
        if (mCamera1 != null) {
            mCamera1.stopPreview();
            mCamera1.release();
            mCamera1 = null;
            Log.d("releaseCamera -- done");
        }
    }

    //
    public void sendCameraParams(Handler mainHandler, int width, int height, float fps) {
        // The right way to do this is to bundle them up into an Object.  The lazy
        // way is to send two messages.
        mainHandler.sendMessage(mainHandler.obtainMessage(MSG_SEND_CAMERA_PARAMS0, width, height));
        mainHandler.sendMessage(mainHandler.obtainMessage(MSG_SEND_CAMERA_PARAMS1, (int) (fps * 1000), 0));
    }

    /**
     * Sends the updated rect size to the main thread.
     * <p>
     * Call from render thread.
     */
    public void sendRectSize(Handler mainHandler, int width, int height) {
        mainHandler.sendMessage(mainHandler.obtainMessage(MSG_SEND_RECT_SIZE, width, height));
    }

    /**
     * Sends the updated zoom area to the main thread.
     * <p>
     * Call from render thread.
     */
    public void sendZoomArea(Handler mainHandler, int width, int height) {
        mainHandler.sendMessage(mainHandler.obtainMessage(MSG_SEND_ZOOM_AREA, width, height));
    }

    /**
     * Sends the updated zoom area to the main thread.
     * <p>
     * Call from render thread.
     */
    public void sendRotateDeg(Handler mainHandler, int rot) {
        mainHandler.sendMessage(mainHandler.obtainMessage(MSG_SEND_ROTATE_DEG, rot, 0));
    }


    //


    //

    /**
     * Handler for RenderThread.  Used for messages sent from the UI thread to the render thread.
     * <p>
     * The Object is created on the render thread, and the various "send" methods are called
     * from the UI thread.
     */
    public class camera_RenderHandler extends Handler {
        String TAG = "camera_RenderHandler";

        //
        private final static int MSG_SURFACE_AVAILABLE = 0;
        private final static int MSG_SURFACE_CHANGED = 1;
        private final static int MSG_SURFACE_DESTROYED = 2;
        private final static int MSG_SHUTDOWN = 3;
        private final static int MSG_FRAME_AVAILABLE = 4;
        private final static int MSG_ZOOM_VALUE = 5;
        private final static int MSG_SIZE_VALUE = 6;
        private final static int MSG_ROTATE_VALUE = 7;
        private final static int MSG_POSITION = 8;
        private final static int MSG_REDRAW = 9;
        //  MSG_WATCHDOG = 10 已上移到外部类（notifyCameraOpen 也要用）

        // This shouldn't need to be a weak , since we'll go away when the Looper quits,
        // but no real harm in it.
        private camera_RenderThread mWeakRenderThread;

        /**
         * Call from render thread.
         */
        public camera_RenderHandler(camera_RenderThread rt) {
            mWeakRenderThread = rt;// new WeakReference<RenderThread>(rt);
        }

        /**
         * Sends the "surface available" message.  If the surface was newly created (i.e.
         * this is called from surfaceCreated()), set newSurface to true.  If this is
         * being called during Activity startup for a previously-existing surface, set
         * newSurface to false.
         * <p>
         * The flag tells the caller whether or not it can expect a surfaceChanged() to
         * arrive very soon.
         * <p>
         * Call from UI thread.
         */
        public void sendSurfaceAvailable(SurfaceTexture holder, boolean newSurface) {
            sendMessage(obtainMessage(MSG_SURFACE_AVAILABLE,
                    newSurface ? 1 : 0, 0, holder));
        }

        /**
         * Sends the "surface changed" message, forwarding what we got from the SurfaceHolder.
         * <p>
         * Call from UI thread.
         */
        public void sendSurfaceChanged(int format, int width,
                                       int height) {
            // ignore format
            sendMessage(obtainMessage(MSG_SURFACE_CHANGED, width, height));
        }

        /**
         * Sends the "shutdown" message, which tells the render thread to halt.
         * <p>
         * Call from UI thread.
         */
        public void sendSurfaceDestroyed() {
            sendMessage(obtainMessage(MSG_SURFACE_DESTROYED));
        }

        /**
         * Sends the "shutdown" message, which tells the render thread to halt.
         * <p>
         * Call from UI thread.
         */
        public void sendShutdown() {
            sendMessage(obtainMessage(MSG_SHUTDOWN));
        }

        /**
         * Sends the "frame available" message.
         * <p>
         * Call from UI thread.
         */
        public void sendFrameAvailable() {
            sendMessage(obtainMessage(MSG_FRAME_AVAILABLE));
        }

        /**
         * Sends the "zoom value" message.  "progress" should be 0-100.
         * <p>
         * Call from UI thread.
         */
        public void sendZoomValue(int progress) {
            sendMessage(obtainMessage(MSG_ZOOM_VALUE, progress, 0));
        }

        /**
         * Sends the "size value" message.  "progress" should be 0-100.
         * <p>
         * Call from UI thread.
         */
        public void sendSizeValue(int progress) {
            sendMessage(obtainMessage(MSG_SIZE_VALUE, progress, 0));
        }

        /**
         * Sends the "rotate value" message.  "progress" should be 0-100.
         * <p>
         * Call from UI thread.
         */
        public void sendRotateValue(int progress) {
            sendMessage(obtainMessage(MSG_ROTATE_VALUE, progress, 0));
        }

        /**
         * Sends the "position" message.  Sets the position of the rect.
         * <p>
         * Call from UI thread.
         */
        public void sendPosition(int x, int y) {
            sendMessage(obtainMessage(MSG_POSITION, x, y));
        }

        /**
         * Sends the "redraw" message.  Forces an immediate redraw.
         * <p>
         * Call from UI thread.
         */
        public void sendRedraw() {
            sendMessage(obtainMessage(MSG_REDRAW));
        }

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            //Log.d("RenderHandler [" + this + "]: what=" + what);

            camera_RenderThread renderThread = mWeakRenderThread;
            if (renderThread == null) {
                Log.w("RenderHandler.handleMessage: weak  is null");
                return;
            }

//            Log.d("camera_RenderThread: msg.what: " + msg.what + " msg.arg1: " + msg.arg1);
            switch (what) {
                case MSG_SURFACE_AVAILABLE:
                    renderThread.surfaceAvailable((SurfaceTexture) msg.obj, msg.arg1 != 0);
                    break;
                case MSG_SURFACE_CHANGED:
                    renderThread.surfaceChanged(msg.arg1, msg.arg2);
                    break;
                case MSG_SURFACE_DESTROYED:
                    renderThread.surfaceDestroyed();
                    break;
                case MSG_SHUTDOWN:
                    renderThread.shutdown();
                    break;
                case MSG_FRAME_AVAILABLE:
                    //  2026/09/14: 0=成功 / 1=瞬时失败（等下一帧，不退出）/ -1=致命。
                    //  之前任何非 0 都 sendShutdown，瞬时失败会误杀采集线程。
                    int ret = renderThread.frameAvailable();
                    if(ret < 0)
                    {
                        Log.e("atdx", "frameAvailable fatal, shutdown camera thread");
                        sendShutdown();
                    }
                    break;
                case MSG_WATCHDOG:
                    //  2026/09/14: 无帧看门狗——检查+自续
                    renderThread.watchdogTick();
                    sendEmptyMessageDelayed(MSG_WATCHDOG, renderThread.WATCHDOG_INTERVAL_MS);
                    break;

                case guiShare.Consts.CONST_msg_cameraLost:
                    //  2026/09/14: MyCamera.onDisconnected/onError 触发的快速重建
                    renderThread.rebuildCamera();
                    break;

                case guiShare.Consts.CONST_msg_cameraOpen:
                    renderThread.notifyCameraOpen();
                    break;
                case MSG_ZOOM_VALUE:
                    renderThread.setZoom(msg.arg1);
                    break;
                case MSG_SIZE_VALUE:
                    renderThread.setSize(msg.arg1);
                    break;
                case MSG_ROTATE_VALUE:
                    renderThread.setRotate(msg.arg1);
                    break;
                case MSG_POSITION:
                    renderThread.setPosition(msg.arg1, msg.arg2);
                    break;
                case MSG_REDRAW:
                    boolean bRtt = true;
                    //
                    renderThread.draw(bRtt);
                    break;

                default:
                    Log.e("unknown message " + what);
            }
        }
    }

}

