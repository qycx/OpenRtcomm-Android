package com.qycx.base.camera;


import static com.qycx.base.guiShare.Consts.CONST_msg_cameraOpen;
import static com.qycx.base.guiShare.Consts.CONST_msg_cameraLost;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.util.Range;
import android.util.Size;
import android.view.Surface;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.qycx.CompareSizesByArea;
import com.qycx.MyApplication;
import com.qycx.base.showInfo_open;
import com.qycx.base.util.Log;
import com.qycx.base.util.ToastUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class MyCamera {
    //
    public String mCameraId;
    public CameraDevice mCameraDevice;
    public CameraCaptureSession mPreviewSession;
    //  2026/09/14: 预览用的 Surface 要留引用才能释放。旧实现每次 startPreview 都
    //  new Surface(st) 且从不 release——断流重建/旋转重启一次漏一个，漏几个之后
    //  相机就可能因为 BufferQueue 分配失败而打不开。
    public Surface mPreviewSurface;
    public boolean m_camera_isOpen;

    //
    public Size mPreviewSize;
    public Size mVideoSize;

    //
    public Semaphore mCameraOpenCloseLock = new Semaphore(1);

    //
    MyCameraStateCallback mStateCallback;

    //
    public int mSensorOrientation;
    CaptureRequest.Builder mPreviewBuilder;

    //
    Handler mBackgroundHandler;


    //
    public MyCamera(Handler mBackgroundHandler) {
        this.mBackgroundHandler = mBackgroundHandler;

        //
        this.mStateCallback = new MyCameraStateCallback(this);

    }

    //


    //
    static Size chooseOptimalSize(Size[] choices, int width1, int height1, Size aspectRatio1) {
        List<Size> bigEnough = new ArrayList<>();
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio1.getWidth();
        int h = aspectRatio1.getHeight();

        //
        int width = height1;
        int height = width1;


        //
        if (w != 0) {
            //
            for (int i = 0; i < choices.length; i++) {
                Size option = choices[i];
                if (option.getHeight() == option.getWidth() * h / w
                        && option.getWidth() >= width && option.getHeight() >= height) {
                    bigEnough.add(option);
                }
            }
        }

        //
        if (bigEnough.size() > 0) {
            return (Size) Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            //
            if  (  w!=0) {
                for (int i = 0; i < choices.length; i++) {
                    Size option = choices[i];
                    if (option.getHeight() == option.getWidth() * h / w
                    ) {
                        bigEnough.add(option);
                    }
                }
            }
            //
            if  (  bigEnough.size()>0)  {
                 return(Size)Collections.max(bigEnough,new CompareSizesByArea());
            }
            else {

                //
                return choices[0];
            }
        }


    }


    //
    public String selectCamera(CameraManager manager, boolean bFront) {
        String sel_id = null;

        String[] cameraIdList = new String[0];
        try {
            cameraIdList = manager.getCameraIdList();
        } catch (CameraAccessException e) {
            Log.e(e.getMessage());
            return null;
        }
        if (cameraIdList == null) return null;

        for (String cameraId : cameraIdList) {
            CameraCharacteristics characteristics = null;
            try {
                characteristics = manager.getCameraCharacteristics(cameraId);
            } catch (CameraAccessException e) {
                Log.e(e.getMessage());
                return null;
            }
            if (bFront) {
                if (((Integer) (characteristics.get(CameraCharacteristics.LENS_FACING))).intValue() == CameraMetadata.LENS_FACING_FRONT) {
                    sel_id = cameraId;
                    break;
                }
            } else {
                if (((Integer) (characteristics.get(CameraCharacteristics.LENS_FACING))).intValue() == CameraMetadata.LENS_FACING_BACK) {
                    sel_id = cameraId;
                    break;
                }
            }
        }
        if (sel_id == null) {
            if (cameraIdList.length > 0) {
                sel_id = cameraIdList[0];
            }
        }

        return sel_id;
    }

    //  2026/09/14: 打开相机前的重试参数。旋转/断流重建时，上一台 CameraDevice 的
    //  close() 是异步的（camera 服务侧还没真正放开），紧接着 openCamera 很容易拿到
    //  CameraAccessException（"Camera 0 is already in use" / CAMERA_IN_USE）。
    //  旧实现一次失败就 return，而调用方 initVideoCapDev 会一直等就绪——结果就是
    //  "点旋转几次后摄像头再也打不开"。这里退避重试。
    static final int OPEN_MAX_ATTEMPTS = 4;
    static final long OPEN_RETRY_DELAY_MS = 400;

    /**
     * 打开相机（带退避重试）。
     *
     * @return 0=已提交 open 请求（结果由 onOpened/onDisconnected/onError 回调决定）；
     *         -1=所有尝试都没提交成功（本轮相机没打开）。
     */
    public int openCamera(int width, int height, boolean bFront) {
        for (int attempt = 1; attempt <= OPEN_MAX_ATTEMPTS; attempt++) {
            int iRet = openCameraOnce(width, height, bFront);
            if (iRet == 0) {
                return 0;
            }
            if (attempt < OPEN_MAX_ATTEMPTS) {
                long delayMs = OPEN_RETRY_DELAY_MS * attempt;
                Log.e("atdx", "openCamera: attempt " + attempt + "/" + OPEN_MAX_ATTEMPTS
                        + " failed, retry in " + delayMs + "ms");
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException e) {
                    return -1;
                }
            }
        }
        Log.e("atdx", "openCamera: all " + OPEN_MAX_ATTEMPTS + " attempts failed, camera NOT opened");
        return -1;
    }

    private int openCameraOnce(int width, int height, boolean bFront) {
        Context context = MyApplication.getContext();

        //
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                Log.e("MyCamera.openCamera: mCameraOpenCloseLock tryAcquire timeout");
                return -1;

            }
            //  2026/09/14 信号量泄漏修复：正常路径由 onOpened/onDisconnected/onError 回调
            //  release；openCamera 自身失败（提前 return/抛异常）时回调不会来，必须自行
            //  release——否则锁永久被占，之后每次 openCamera tryAcquire 都超时，摄像头
            //  永远打不开（表现为"断流重建后再也没恢复"）。
            boolean bSubmitted = false;
            try {
            String[] cameraIdList = manager.getCameraIdList();
            //boolean bFront = true;
            String cameraId = selectCamera(manager, bFront);// manager.getCameraIdList()[0];
            //
            if (cameraId == null) {
                Log.e("MyCamera.openCamera: selectCamera null");
                return -1;
            }
            mCameraId = cameraId;
            //
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = (StreamConfigurationMap) characteristics
                    .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                if (map == null) {
                    Log.e("MyCamera.openCamera: SCALER_STREAM_CONFIGURATION_MAP null");
                    return -1;
                }

            if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                mVideoSize = new Size(height, width);

            } else {
                mVideoSize = new Size(width, height);
            }

            mVideoSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), width, height, mVideoSize);

            //
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Log.e("MyCamera.openCamera: no CAMERA permission");
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return -1;
            }
            manager.openCamera(cameraId, mStateCallback, null);
            bSubmitted = true;

            //
            this.m_camera_isOpen = true;
            } finally {
                //  2026/09/14: openCamera 提交失败（异常/提前 return）时回调不会来，
                //  这里自行释放；提交成功（bSubmitted=true）则由回调释放。
                if (!bSubmitted) {
                    mCameraOpenCloseLock.release();
                }
            }

            //
        } catch (CameraAccessException e) {
            //  2026/09/14: 这里最常见的就是 CAMERA_IN_USE（上一台设备还没被 camera
            //  服务放开）/ MAX_CAMERAS_IN_USE。打错误码，交给上层退避重试。
            Log.e("atdx", "MyCamera.openCamera: CameraAccessException " + e);
            return -1;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return -1;

        } catch (InterruptedException e) {
            e.printStackTrace();
            return -1;

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        return 0;
    }

    public void closeCamera() {
        //  2026/09/14: 原来是 mCameraOpenCloseLock.acquire() 无限等。若上一轮 openCamera
        //  已提交但相机回调一直不来（相机服务异常/设备被占），acquire() 会把 camera
        //  线程永久卡住，进而 exitVideoCapDev 的 join() 卡住主线程——表现就是"旋转几次后
        //  整机卡住、摄像头打不开"。这里改成带超时，超时也继续关，绝不卡死。
        boolean bGotLock = false;
        try {
            bGotLock = mCameraOpenCloseLock.tryAcquire(2000, TimeUnit.MILLISECONDS);
            if (!bGotLock) {
                Log.e("atdx", "MyCamera.closeCamera: tryAcquire timeout(2000ms), force close to avoid hang");
            }
            //
            this.m_camera_isOpen = false;
            //
            closePreviewSession();
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
                /*
                if (null != mImageReader)
                {
                    mImageReader.close();
                    mImageReader = null;
                }
                */
        } catch (InterruptedException e) {
            Log.e(e.getMessage());
        } catch (Exception e) {
            int ii = 0;
        } finally {
            if (bGotLock) {
                mCameraOpenCloseLock.release();
            }
        }

        //
        Log.d( "MyCamera.closeCamera");

    }

    //
    public void closePreviewSession() {
        if (mPreviewSession != null) {
            mPreviewSession.close();
            mPreviewSession = null;
        }
        //  2026/09/14: session 关了，预览 Surface 也就没用了——顺手释放，避免泄漏。
        if (mPreviewSurface != null) {
            mPreviewSurface.release();
            mPreviewSurface = null;
        }
    }


    public void startPreview(SurfaceTexture st) {
        if (null == mCameraDevice
            //|| null==mPreviewSize
        ) {
            return;
        }
        try {
            closePreviewSession();
            mPreviewBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            List<Surface> surfaces = new ArrayList<>();

            //
            //  2026/09/14: 用成员引用（closePreviewSession 里释放），不再每轮 new 一个丢弃。
            mPreviewSurface = new Surface(st);
            //
            surfaces.add(mPreviewSurface);
            mPreviewBuilder.addTarget(mPreviewSurface);
            mCameraDevice.createCaptureSession(surfaces, new PreviewCaptureStateCallback(this), mBackgroundHandler);


        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.e("MyCamera.startPreview: CameraAccessException " + e);
        }

    }


    public void updatePreview() {
        if (null == mCameraDevice) {
            return;
        }
        try {
            setupCaptureRequestBuilder(mPreviewBuilder);
            //
            //HandlerThread thread = new HandlerThread("cameraPreview");
            //thread.start();
            //
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            //  2026/09/14: repeating request 起不来=采集永远无帧。留日志，
            //  由 camera_RenderThread 看门狗无帧超时兜底重建。
            e.printStackTrace();
            Log.e("MyCamera.updatePreview: setRepeatingRequest failed " + e);
        }
    }

    //
    public Range getRange() {
        Context context = MyApplication.getContext();

        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        CameraCharacteristics chars = null;
        try {
            chars = manager.getCameraCharacteristics(mCameraId);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Range[] ranges = chars.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
        Range result = null;

        for(Range range : ranges)
        {
            int min_fps = 7;// 10;

            if ((int) range.getLower() < min_fps)
                continue;
            if (result == null) result = range;
            else if ((int) range.getLower() <= 15 && (int) range.getUpper() - (int)range.getLower() > (int) result.getUpper() - (int)result.getLower())
                result = range;
        }
        return result;
    }


    //
    public void setupCaptureRequestBuilder(CaptureRequest.Builder builder) {
        builder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
        Range range = getRange();
        if (range != null) {
            builder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, range);
        }
        builder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_IDLE);
    }

    public static class MyCameraStateCallback extends CameraDevice.StateCallback {
        private MyCamera owner;

        public MyCameraStateCallback(MyCamera owner) {
            if (owner == null) {
                Log.e("owner");
                return;
            }
            this.owner = owner;
        }

        @Override
        public void onOpened(CameraDevice cameraDevice) {
            boolean bDbg = false;
            String str;

            owner.mCameraDevice = cameraDevice;
            //owner.startPreview();
            owner.mCameraOpenCloseLock.release();

            //
            owner.mBackgroundHandler.sendEmptyMessage(CONST_msg_cameraOpen);

        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            //throw new NotImplementedException();
            //  2026/09/14: 之前只 close+置 null，无人重启——摄像头被系统回收/被其他
            //  应用抢占/media server 重启后采集静默停止（"运行一段时间采集不到"主因）。
            //  现在通知采集线程立即重建；mBackgroundHandler 即 camera_RenderHandler。
            Log.e("MyCamera.onDisconnected: camera device lost, notify rebuild");
            owner.mCameraOpenCloseLock.release();
            camera.close();
            owner.mCameraDevice = null;
            owner.m_camera_isOpen = false;
            if (owner.mBackgroundHandler != null) {
                owner.mBackgroundHandler.sendEmptyMessage(CONST_msg_cameraLost);
            }
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            //throw new NotImplementedException();
            //  2026/09/14: 同 onDisconnected——加日志并触发重建，不再静默死亡。
            Log.e("MyCamera.onError: error=" + error + ", notify rebuild");
            owner.mCameraOpenCloseLock.release();
            camera.close();
            owner.mCameraDevice = null;
            owner.m_camera_isOpen = false;
            if (owner.mBackgroundHandler != null) {
                owner.mBackgroundHandler.sendEmptyMessage(CONST_msg_cameraLost);
            }
        }
    }

    //
    public static class PreviewCaptureStateCallback extends CameraCaptureSession.StateCallback {
        MyCamera owner;

        public PreviewCaptureStateCallback(MyCamera owner) {
            this.owner = owner;
        }

        @Override
        public void onConfigured(CameraCaptureSession session) {
            boolean bDbg = false;
            String str;

            owner.mPreviewSession = session;
            owner.updatePreview();
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
            //  2026/09/14: 配置失败之前只弹 toast——session 没起来，采集永远无帧，
            //  只能靠 camera_RenderThread 的看门狗兜底重建。打日志留痕。
            Log.e("MyCamera.PreviewCaptureStateCallback.onConfigureFailed: session 配置失败");
            Context context = MyApplication.getContext()    ;
            {
//                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                ToastUtils.showToast("Failed");
            }
        }
    }

}
