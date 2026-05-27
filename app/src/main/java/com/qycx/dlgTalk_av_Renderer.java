package com.qycx;


//import android.app.Fragment;
import static com.qycx.base.isTalkerQProc.findIsTalker;
import static com.qycx.base.qmcProc.useFbo_save_dlgTalk_share_eglContext;
import static com.qycx.base.qyMcMain_global.g.g_pQyMc;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.GLES20;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.qycx.base.CIsTalker;
import com.qycx.base.SimpleTexture;
import com.qycx.base.ctxQmc;
import com.qycx.base.dlgTalkProc;
import com.qycx.base.isCliGl.t1Frm;
import com.qycx.base.myrecycleFunc;
import com.qycx.base.qdc.decObj_mediaCodec;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.qmcDrawTmpl;
import com.qycx.base.EventResetMode;
import com.qycx.base.EventWaitHandle;
import com.qycx.base.ctxQyMc;
import com.qycx.base.guiShare;
import com.qycx.base.qySyncObj;
import com.qycx.base.qyWndContentType;
import com.qycx.base.qy_getuiNextTranNo;
import com.qycx.base.saveTexture;
import com.qycx.base.talkDraw;
import com.qycx.base.tmpFunc;
import com.qycx.base.util.Log;
import com.qycx.base.util.javatuples.Triplet;
import com.qycx.gleslibrary.EglCore;
import com.qycx.gleslibrary.EglSurfaceBase;
import com.qycx.gleslibrary.OffscreenSurface;
import com.qycx.gleslibrary.WindowSurface;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.concurrent.Semaphore;


public class dlgTalk_av_Renderer extends Thread implements TextureView.SurfaceTextureListener,SurfaceTexture.OnFrameAvailableListener {
    Fragment mOwner;
    int miFgType;

    //public Object mLock = new Object();
    public Semaphore hMutex = new Semaphore(1);
    private SurfaceTexture mSurfaceTexture;
    private EglCore mEglCore;
    private boolean mDone;

    //
    EventWaitHandle mhEvt_toNotifyTexAvail;

    //
    dlgTalkProc.DLG_TALK_var talker_m_var  =  null;

    //
    public dlgTalk_av_Renderer(Fragment fg, int iFgType) {
        super("Renderer333");
        mOwner = fg;
        miFgType = iFgType;
        //
        return;
    }

    public int init() {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo  = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

        //
        if  (  !(  mOwner instanceof Fragment_dlgTalk_av2)  )  {
            return  -1;
        }
        Fragment_dlgTalk_av2 fg = (Fragment_dlgTalk_av2) mOwner;

        //
        do {
            String pName = "notifiTexAvail" + qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
            mhEvt_toNotifyTexAvail = new EventWaitHandle(false, EventResetMode.AutoReset, pName);

            CIsTalker talker  = findIsTalker(pMisCnt,fg.talker_ui64Id);
            if  (  talker ==null)  {
                break;
            }
            talker_m_var=talker.m_var;

            //
            iErr = 0;

        }  while (false);

        //
        return iErr;
    }

    public int exit() {
        if (null != this.mhEvt_toNotifyTexAvail) {
            this.mhEvt_toNotifyTexAvail.Close();
            this.mhEvt_toNotifyTexAvail = null;
        }

        return 0;
    }


    //
    @Override
    public void run() {


        //
        EGLContext eglContext_shared = null;
        mEglCore = new EglCore(eglContext_shared, EglCore.FLAG_TRY_GLES3);

        //
        OffscreenSurface offscreenEglSurface=null;

        //
        if  (  mOwner==null)  {
            Log.e("anc","talk_av_render.run: err: mOwner is null");
            return;
        }

        /*
        Activity_dlgTalk_av dlgTalk_av=(Activity_dlgTalk_av) mOwner.getActivity();
        if  (  dlgTalk_av==null)  {
            return;
        }
        Activity_dlgTalk hDlgTalk=dlgTalk_av.mOwner_hTalk.get();

         */

        //
        dlgTalkProc.DLG_TALK_var m_var  =talker_m_var;
        if  (  m_var==null)  {
            return;
        }
        Activity  act_av = mOwner.getActivity();
        if  (  act_av==null)  {
            return;
        }
        //
        int  iW_off= m_var.OFFSCREEN_WIDTH;
        int  iH_off=m_var.OFFSCREEN_HEIGHT;
        if  (  iW_off==0  ||  iH_off==0)  {
            Log.e("anc","talk_av_render.run: err: iW_off or iH_off is 0");
            return;
        }

        //
        useFbo_save_dlgTalk_share_eglContext(m_var,act_av,mEglCore.mEGLContext,"dlgTalk_av_render.run: save eglContext");

        //
        while (true) {
            ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
            if (pQyMc == null) {
                Log.e("dlgTalk_av_Renderer::run pQyMc == null");
                break;
            }
            SurfaceTexture surfaceTexture = null;

            //synchronized(mLock)
            boolean bReady = true;
            try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                if (syncObj.sync("", this.hMutex,"dlgTalk_av_Render.83") != 0) {
                    return;
                }
                if (!mDone && (surfaceTexture = mSurfaceTexture) == null) {
                    bReady = false;
                }
                if (mDone) break;
            } catch (Exception e) {
                Log.e(e.getMessage());
                return;
            }
            if (!bReady) {
                int ms = 100;
                tmpFunc.qyFuncs.myWaitOne(this.mhEvt_toNotifyTexAvail, ms);
                continue;
            }

            //
            if  (  offscreenEglSurface==null) {
                offscreenEglSurface = new OffscreenSurface(mEglCore, iW_off, iH_off);
                //
            }

            //
            // 2. 【关键步骤】将 EGL 上下文绑定到当前线程。
            // 任何 OpenGL ES 函数的调用都需要一个当前上下文。
            // 如果没有这一步，接下来的 initGL() 将会失败。
            // 我们使用离屏 Surface 作为初始化的目标表面。
            //
            offscreenEglSurface.makeCurrent();

            // 3. 上下文激活后，现在可以安全地初始化所有 OpenGL ES 相关的资源了。
            initGL();

            //
            mStartTime = System.currentTimeMillis();

            //


            //  当textureview大小变化时，onscreenEglSurface要重新创建。但是视频任务是用offscreen处理的，所以不需要重新启动
            //  对offscreen, 如果需要修改OFFSCREEN_WIDTH,才需要重新创建offscreen
            //
            WindowSurface onscreenEglSurface = new WindowSurface(mEglCore, surfaceTexture);

            //
            doAnimation(offscreenEglSurface,onscreenEglSurface);

            //
            onscreenEglSurface.release();  onscreenEglSurface=null;

            //
            exitGL();

            //
            if (!Fragment_dlgTalk_av2.sReleaseInCallback) {
                //Log.i(TAG, "Releasing SurfaceTexture in renderer thread");
                surfaceTexture.release();
            }
        }

        //
        if (  offscreenEglSurface!=null) {
            offscreenEglSurface.release();
            offscreenEglSurface = null;
        }

        //
        useFbo_save_dlgTalk_share_eglContext(m_var,act_av,null,"dlgTalk_av_render.run will exit: save null");

        //
        if (mEglCore!=null) {
            mEglCore.release();
            mEglCore = null;
        }

        //
        return;
    }



    //

    /** 用于模拟动态内容的颜色分量 */
    private float mRed = 0.0f, mGreen = 0.0f, mBlue = 0.0f;
    /** 用于计算动态颜色的起始时间 */
    private long mStartTime;

    //
    /**
     * 初始化 OpenGL ES 相关的资源。
     * 必须在 EGL 上下文被激活后调用。
     */
    private int initGL() {

        //
        //Activity_dlgTalk_av dlgTalk_av=(Activity_dlgTalk_av)mOwner.getActivity();
        //Activity_dlgTalk hTalk  =  (Activity_dlgTalk)dlgTalk_av.mOwner_hTalk.get();
        dlgTalkProc.DLG_TALK_var m_var  =  talker_m_var;//hTalk.m_var;

        //
        mOffscreenWidth= m_var.OFFSCREEN_WIDTH;;
        mOffscreenHeight= m_var.OFFSCREEN_HEIGHT;
        if  (  mOffscreenWidth==0  ||  mOffscreenHeight==0)  {
            Log.e("anc","talk_av_render.initGL: failed, mOffscreenW or h is 0");
            return  -1;
        }

        //
        useFbo_initGL();

        return  0;

    }

    //
    void exitGL()
    {
        //
        useFbo_exitGL();

    }


    //
    private int mProgram;
    private int mTextureId;
    private int mFramebuffer;
    private int mOffscreenTexture;
    private int mOffscreenWidth;
    private int mOffscreenHeight;



    //
    private int useFbo_initGL() {

        int  iErr  =  -1;

        do {
            // 创建纹理
            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            mTextureId = textures[0];
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);

            // 设置纹理参数
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            // 创建纹理数据 (简单的渐变色)
            int width = 256;
            int height = 256;
            //
            ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(width * height * 4);
            pixelBuffer.order(ByteOrder.nativeOrder());
            //
            // 填充纹理数据（示例：创建渐变纹理）
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // 计算渐变颜色
                    float r = (float) x / width;
                    float g = (float) y / height;
                    float b = 0.5f;
                    float a = 1.0f;

                    // 将浮点颜色值转换为字节 (0-255)
                    pixelBuffer.put((byte) (r * 255));
                    pixelBuffer.put((byte) (g * 255));
                    pixelBuffer.put((byte) (b * 255));
                    pixelBuffer.put((byte) (a * 255));
                }
            }

            // 重置位置以便读取
            pixelBuffer.position(0);

            // 加载纹理
            //GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, pixels, 0, width, height, 0);
            GLES20.glTexImage2D(
                    GLES20.GL_TEXTURE_2D,
                    0,
                    GLES20.GL_RGBA, // Internal format (e.g., GL_RGBA)
                    width,
                    height,
                    0,
                    GLES20.GL_RGBA, // Format of pixel data
                    GLES20.GL_UNSIGNED_BYTE, // Data type
                    pixelBuffer // ByteBuffer containing image data
            );

            // 创建离屏纹理
            int[] offscreenTextures = new int[1];
            GLES20.glGenTextures(1, offscreenTextures, 0);
            mOffscreenTexture = offscreenTextures[0];
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mOffscreenTexture);

            // 设置离屏纹理参数
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            // 分配离屏纹理内存
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                    mOffscreenWidth, mOffscreenHeight, 0,
                    GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

            // 创建帧缓冲区
            int[] framebuffers = new int[1];
            GLES20.glGenFramebuffers(1, framebuffers, 0);
            mFramebuffer = framebuffers[0];
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFramebuffer);

            // 将纹理附加到帧缓冲区
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D, mOffscreenTexture, 0);

            // 检查帧缓冲区状态
            int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
            if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
                //
                //throw new RuntimeException("Framebuffer not complete: " + status);
                Log.e("anc", "Framebuffer not complete: " + status);
            }

            // 恢复默认帧缓冲区
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);


            // 创建着色器程序
            String vertexShaderCode =
                    "attribute vec4 aPosition;" +
                            "attribute vec2 aTexCoord;" +
                            "varying vec2 vTexCoord;" +
                            "void main() {" +
                            "  gl_Position = aPosition;" +
                            "  vTexCoord = aTexCoord;" +
                            "}";

            String fragmentShaderCode =
                    "precision mediump float;" +
                            "varying vec2 vTexCoord;" +
                            "uniform sampler2D uTexture;" +
                            "void main() {" +
                            "  gl_FragColor = texture2D(uTexture, vTexCoord);" +
                            "}";

            int vertexShader = useFbo_loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
            int fragmentShader = useFbo_loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

            mProgram = GLES20.glCreateProgram();
            GLES20.glAttachShader(mProgram, vertexShader);
            GLES20.glAttachShader(mProgram, fragmentShader);
            GLES20.glLinkProgram(mProgram);

            // 检查链接状态
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                String infoLog = GLES20.glGetProgramInfoLog(mProgram);
                //throw new RuntimeException("Could not link program: " + infoLog);
                Log.e("anc", "Could not link program: " + infoLog);
            }

            //
            GLES20.glDeleteShader(vertexShader);
            GLES20.glDeleteShader(fragmentShader);

            //
            iErr  =  0;

        } while(false);

        //
        if  (  iErr !=0)  {
            useFbo_exitGL();
        }

        //
        return  iErr;

    }

    int useFbo_exitGL()
    {
        // 删除OpenGL资源
        if (mTextureId != 0) {
            GLES20.glDeleteTextures(1, new int[]{mTextureId}, 0);
            mTextureId=0;
        }
        if (mOffscreenTexture != 0) {
            GLES20.glDeleteTextures(1, new int[]{mOffscreenTexture}, 0);
            mOffscreenTexture=0;
        }
        if (mFramebuffer != 0) {
            GLES20.glDeleteFramebuffers(1, new int[]{mFramebuffer}, 0);
            mFramebuffer=0;
        }
        if (mProgram != 0) {
            GLES20.glDeleteProgram(mProgram);
            mProgram=0;
        }

        return  0;
    }

    private int useFbo_loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        // 检查编译状态
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            String infoLog = GLES20.glGetShaderInfoLog(shader);
            GLES20.glDeleteShader(shader);
            //
            //throw new RuntimeException("Could not compile shader " + type + ":" + infoLog);
            Log.e("anc","Could not compile shader " + type + ":" + infoLog);
            //
            return  0;
        }

        return shader;
    }


    //
    private void useFbo_render(EglSurfaceBase offscreenEglSurface, EglSurfaceBase onscreenEglSurface, talkDraw.CTalkDraw myDraw_off,talkDraw.CTalkDraw myDraw_on)
    {
        // 1. 渲染到离屏表面 (FBO)
        //EGL14.eglMakeCurrent(mEglDisplay, mOffscreenSurface, mOffscreenSurface, mEglContext);
        offscreenEglSurface.makeCurrent();

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFramebuffer);
        GLES20.glViewport(0, 0, mOffscreenWidth, mOffscreenHeight);

        //
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //

        //
        myDraw_off.draw_off();
        //myDraw.test_draw();


        // 解绑FBO
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);


        //
        // 2. 渲染到窗口表面 (使用离屏纹理)
        //EGL14.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext);
        onscreenEglSurface.makeCurrent();

        //
        int  viewWidth=onscreenEglSurface.getWidth();
        int  viewHeight=onscreenEglSurface.getHeight();

        // 获取TextureView的尺寸
        if  ( false  ) {
            int[] viewport = new int[4];
            GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, viewport, 0);
            viewWidth = viewport[2];
            viewHeight = viewport[3];
        }
        //


        GLES20.glViewport(0, 0, viewWidth, viewHeight);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);


        //
        int texId;
        //
        texId=mOffscreenTexture;

        //texId = tmpFrameBuffer_int.mTexture[0];

        myDraw_on.draw_off2on(texId,mOffscreenWidth,mOffscreenHeight);

        //
        myDraw_on.draw_meOnly();


        //EGL14.eglSwapBuffers(mEglDisplay, mEglSurface);
        onscreenEglSurface.swapBuffers();

        // 等待一段时间以便观察结果
        if  (  false  ) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // 线程被中断，退出
            }
        }
    }




    private void doAnimation(EglSurfaceBase offscreenEglSurface,  EglSurfaceBase onscreenEglSurface) {

        ctxQyMc.CCtxQyMc pQyMc =g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo  =  pQyMc.get_pProcInfo();


        //
        if (pProcInfo.cfg.bUse_off2on) {
            //
            useFbo_doAnimation(offscreenEglSurface, onscreenEglSurface);
            //
            return;
        }

        //
        old_doAnimation(onscreenEglSurface);

        //
        return;

    }



    //
    //private void doAnimation(WindowSurface eglSurface)
    private void old_doAnimation( EglSurfaceBase onscreenEglSurface)
    {
        int BLOCK_WIDTH = 80;
        int BLOCK_SPEED = 2;
        float clearColor = 0.0f;
        int xpos = -BLOCK_WIDTH / 2;
        int xdir = BLOCK_SPEED;
        int width = onscreenEglSurface.getWidth();
        int height = onscreenEglSurface.getHeight();

        String str;

        //

        //
        onscreenEglSurface.makeCurrent();

        //
        talkDraw.CTalkDraw myDraw = new talkDraw.CTalkDraw(mOwner, miFgType, onscreenEglSurface);

        //
        //Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) mOwner.getActivity();
        dlgTalkProc.DLG_TALK_var m_var = talker_m_var;


        //
        if (0 != myDraw.init()) {
            if ( myDraw!=null)
            {
                myDraw.exit();
            }
            return;
        }
        if (0 != myDraw.onSize(width, height)) {
            if ( myDraw!=null)
            {
                myDraw.exit();
            }
            return;
        }

        //
        while (true) {
            ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
            if (pQyMc == null) {
                Log.e("doAnimation pQyMc == null");
                if ( myDraw!=null)
                {
                    myDraw.exit();
                }
                break;
            }
            try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                if (syncObj.sync("", this.hMutex,"Render.doAnimation.163") != 0) {
                    if ( myDraw!=null)
                    {
                        myDraw.exit();
                    }
                    return;
                }
                SurfaceTexture surfaceTexture = mSurfaceTexture;
                if (mDone || surfaceTexture == null) {
                    if ( myDraw!=null)
                    {
                        myDraw.exit();
                    }
                    return;
                }
            } catch (Exception e) {
                Log.e(e.getMessage());
                if ( myDraw!=null)
                {
                    myDraw.exit();
                }
                return;
            }
            //
            if (m_var.m_talkAvVar.m_bSurfaceTextureSizeChanged) {
                //
                m_var.m_talkAvVar.m_bSurfaceTextureSizeChanged = false;

                //
                Log.d("anc","doAnimation: bSurfaceTextureSizeChanged is true,so needRestart. l645");

                //
                m_var.m_talkAvVar.m_bDlgTalk_av_readyFor_talkDrawRestart = false;
                m_var.m_talkAvVar.m_bTalkDraw_needRestart = true;
                //
                if (miFgType == qyWndContentType.Consts.CONST_fgType_talk_av2) {
                    Fragment_dlgTalk_av2 fg = (Fragment_dlgTalk_av2) mOwner;
                    fg.mMainHandler.send_MSG_talkDraw_needRestart();
                }
            }
            if (m_var.m_talkAvVar.m_bTalkDraw_needRestart) {
                if (m_var.m_talkAvVar.m_bDlgTalk_av_readyFor_talkDrawRestart) {
                    Log.d("doAnimation will stop: m_bDlgTalk_av_readyFor_talkDrawRestart is true");
                    m_var.m_talkAvVar.m_bDlgTalk_av_readyFor_talkDrawRestart = false;
                    m_var.m_talkAvVar.m_bTalkDraw_needRestart = false;
                    if ( myDraw!=null)
                    {
                        myDraw.exit();
                    }
                    return;
                }
            }

            //
            myDraw.draw();
            //myDraw.test_draw();

            //
            boolean tmp_bRet;
            tmp_bRet  =  onscreenEglSurface.swapBuffers();
            if  (  !tmp_bRet)  {
                Log.d("qy","doAnimation: swapBuffers failed");
            }
            //
            clearColor += 0.015625f;
            if (clearColor > 1.0f) {
                clearColor = 0.0f;

            }
            xpos += xdir;
            if (xpos <= -BLOCK_WIDTH / 2 || xpos >= width - BLOCK_WIDTH / 2) {
                xdir = -xdir;
            }
        }
    }


    /////////////////////////////////
    private void useFbo_doAnimation(EglSurfaceBase offscreenEglSurface,  EglSurfaceBase onscreenEglSurface)
    {
        int BLOCK_WIDTH = 80;
        int BLOCK_SPEED = 2;
        float clearColor = 0.0f;
        int xpos = -BLOCK_WIDTH / 2;
        int xdir = BLOCK_SPEED;
        //
        String str;

        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (pQyMc == null) {
            Log.e("doAnimation pQyMc == null");
            return;
        }

        //
        //Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) mOwner.getActivity();
        dlgTalkProc.DLG_TALK_var m_var = this.talker_m_var;

        //
        offscreenEglSurface.makeCurrent();

        //
        int width_off = offscreenEglSurface.getWidth();
        int height_off = offscreenEglSurface.getHeight();
        //
        int width_on=onscreenEglSurface.getWidth();
        int height_on=onscreenEglSurface.getHeight();

        //
        talkDraw.CTalkDraw myDraw_off = new talkDraw.CTalkDraw(mOwner, miFgType, offscreenEglSurface);
        myDraw_off.talker_m_var  =  m_var;

        //  将offscreen的纹理写到onscreen去的工具
        talkDraw.CTalkDraw myDraw_on =new talkDraw.CTalkDraw(mOwner,miFgType,onscreenEglSurface);
        myDraw_on.talker_m_var=m_var;


        do {

            //
            if (0 != myDraw_off.init()) {
                break;
            }
            if (0 != myDraw_off.onSize(width_off, height_off)) {
                break;
            }

            //
            if  (  0!=myDraw_on.init())  {
                break;
            }
            if  (  0!=myDraw_on.onSize(width_on,height_on)) {
                break;
            }

            //
            int loopCtrl  =  0;

            //
            while (true) {

                loopCtrl  ++;

                try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                    if (syncObj.sync("", this.hMutex, "Render.doAnimation.163") != 0) {
                        break;
                    }
                    SurfaceTexture surfaceTexture = mSurfaceTexture;
                    if (mDone || surfaceTexture == null) {
                        break;
                    }
                } catch (Exception e) {
                    Log.e(e.getMessage());
                    break;
                }
                //
                if (m_var.m_talkAvVar.m_bSurfaceTextureSizeChanged) {
                    //
                    m_var.m_talkAvVar.m_bSurfaceTextureSizeChanged = false;

                    //
                    if  (  false  ) {
                        //
                        Log.d("anc", "doAnimation: bSurfaceTextureSizeChanged is true,so needRestart");
                        //
                        m_var.m_talkAvVar.m_bDlgTalk_av_readyFor_talkDrawRestart = false;
                        m_var.m_talkAvVar.m_bTalkDraw_needRestart = true;
                        //
                        if (miFgType == qyWndContentType.Consts.CONST_fgType_talk_av2) {
                            Fragment_dlgTalk_av2 fg = (Fragment_dlgTalk_av2) mOwner;
                            fg.mMainHandler.send_MSG_talkDraw_needRestart();
                        }
                    }

                    //
                    //  不需要重启任务，因为现在用了fbo,
                    Log.d("anc", "doAnimation: bSurfaceTextureSizeChanged is true,break now");

                    break;
                }
                if (m_var.m_talkAvVar.m_bTalkDraw_needRestart) {
                    if (m_var.m_talkAvVar.m_bDlgTalk_av_readyFor_talkDrawRestart) {
                        //
                        Log.d("anc","doAnimation will stop: m_bDlgTalk_av_readyFor_talkDrawRestart is true");
                        //
                        m_var.m_talkAvVar.m_bDlgTalk_av_readyFor_talkDrawRestart = false;
                        m_var.m_talkAvVar.m_bTalkDraw_needRestart = false;

                        //
                        break;
                    }
                }

                //
                useFbo_render(offscreenEglSurface, onscreenEglSurface,myDraw_off,myDraw_on);

                //
                if  (  false  ) {
                    Log.d("anc", "useFbo_doAnimation: after useFbo_render. loopCtrl " + loopCtrl);
                }

                //
                try {
                    Thread.sleep(33);
                } catch (InterruptedException e) {
                    //throw new RuntimeException(e);
                    Log.e("anc","useFbo_doAnimation: sleep.InterruptedException");
                }

                //
                continue;

            }

            //
        } while(false);


        //
        if (myDraw_off != null) {
            myDraw_off.exit();
        }
        if  (  myDraw_on!=null){
            myDraw_on.exit();
        }


        return;
    }





    //
    public void halt() {
        Log.d("dlgTalk_av_renderer.halt");
        try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
            if (syncObj.sync("", this.hMutex,"render.halt.232") != 0) return;
            //
            mDone = true;

        } catch (Exception e) {
            Log.e(e.getMessage());
        }
    }

    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        //throw new NotImplementedException();
        try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
            if (syncObj.sync("", this.hMutex,"render.onSurfaceTexAvail.242") != 0) {
                return;
            }
            mSurfaceTexture = surface;
        } catch (Exception e) {
            Log.e(e.getMessage());
            return;
        }

        tmpFunc.qyFuncs.mySetEvent(this.mhEvt_toNotifyTexAvail);

        //

        return;
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
            if (syncObj.sync("", this.hMutex,"onSurfaceTexDestroyed.261") != 0) {
                return Fragment_dlgTalk_av2.sReleaseInCallback;
            }
            mSurfaceTexture = null;
        } catch (Exception e) {
            Log.e(e.getMessage());
            return false;
        }
        return Fragment_dlgTalk_av2.sReleaseInCallback;
    }

    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
        //Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) this.mOwner.getActivity();
        dlgTalkProc.DLG_TALK_var m_var = talker_m_var;
        m_var.m_talkAvVar.m_bSurfaceTextureSizeChanged = true;
    }

    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

    }

    /**
     * SurfaceTexture.OnFrameAvailableListener 回调。
     * 当有新的帧数据可用时被调用（例如相机预览）。
     * 在本例中，我们是手动驱动渲染循环（通过 postDelayed），所以不依赖这个回调。
     */

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {

    }
}
