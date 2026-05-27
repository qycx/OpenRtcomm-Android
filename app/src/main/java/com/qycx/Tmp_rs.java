package com.qycx;

import static com.qycx.base.qmcSharedObj.qyFuncs.getSharedObjByIndex;
import static com.qycx.base.qyMcMain_global.g.g_pQyMc;
import static com.qycx.base.tmpFunc.qyFuncs.mySetEvent;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.intel.realsense.librealsense.Colorizer;
import com.intel.realsense.librealsense.Config;
import com.intel.realsense.librealsense.DeviceList;
import com.intel.realsense.librealsense.DeviceListener;
import com.intel.realsense.librealsense.FrameSet;
import com.intel.realsense.librealsense.GLRsSurfaceView;
import com.intel.realsense.librealsense.Pipeline;
import com.intel.realsense.librealsense.PipelineProfile;
import com.intel.realsense.librealsense.RsContext;
import com.intel.realsense.librealsense.StreamType;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.qmcSharedObj;
import com.qycx.smxj.myGLRsSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

//
////////////////////////////////
//77
//
public class Tmp_rs {
    private static final int PERMISSIONS_REQUEST_CAMERA = 0;

    private boolean mPermissionsGranted = false;

    private Context mAppContext;
    private TextView mBackGroundText;
    private myGLRsSurfaceView mGLSurfaceView;
    private boolean mIsStreaming = false;
    //private final Handler mHandler = new Handler();
    private Handler mHandler;

    private Pipeline mPipeline;
    private Colorizer mColorizer;
    private RsContext mRsContext;


    //
    protected void onCreateView(Fragment fg, View view) {

        Activity act = (Activity) fg.getActivity();

        //
        int  tmp_id=0;
        tmp_id  =  R.id.glSurfaceView;


        //
        mAppContext = act.getApplicationContext();
        //mBackGroundText = act.findViewById(R.id.connectCameraText);
        mGLSurfaceView = view.findViewById(tmp_id);
        mGLSurfaceView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        // Android 9 also requires camera permissions
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.O &&
                ContextCompat.checkSelfPermission(act, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
            return;
        }

        mPermissionsGranted = true;

        //
        mHandler = new Handler();//fg.mMainHandler;

        //
        ctxQyMc.CCtxQyMc  pQyMc  =  g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo  =  pQyMc.get_pProcInfo();
        pProcInfo.smxj.newBuf1(pProcInfo.smxj.cfg.iW,pProcInfo.smxj.cfg.iH);


    }

    protected void onDestroyView() {
        mGLSurfaceView.close();

        //
        ctxQyMc.CCtxQyMc  pQyMc  =  g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo  =  pQyMc.get_pProcInfo();
        pProcInfo.smxj.freeBuf1();


    }


    protected void onResume() {
        if (mPermissionsGranted)
            init();
        else {
            //android.util.Log.e(TAG, "missing permissions");
        }
    }

    protected void onPause() {
        if (mRsContext != null)
            mRsContext.close();
        stop();
        mColorizer.close();
        mPipeline.close();
    }

    private void init() {
        //RsContext.init must be called once in the application lifetime before any interaction with physical RealSense devices.
        //For multi activities applications use the application context instead of the activity context
        RsContext.init(mAppContext);

        //Register to notifications regarding RealSense devices attach/detach events via the DeviceListener.
        mRsContext = new RsContext();
        mRsContext.setDevicesChangedCallback(mListener);

        mPipeline = new Pipeline();
        mColorizer = new Colorizer();

        try (DeviceList dl = mRsContext.queryDevices()) {
            //
            int  cnt  =  dl.getDeviceCount();
            //
            if (dl.getDeviceCount() > 0) {
                showConnectLabel(false);
                start();
            }
        }
    }


    private void showConnectLabel(final boolean state) {
            /*
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBackGroundText.setVisibility(state ? View.VISIBLE : View.GONE);
                }
            });

             */
    }

    private DeviceListener mListener = new DeviceListener() {
        @Override
        public void onDeviceAttach() {
            showConnectLabel(false);
        }

        @Override
        public void onDeviceDetach() {
            showConnectLabel(true);
            stop();
        }
    };

    //
    int loopCtrl = 0;

    //

    Runnable mStreaming = new Runnable() {
        @Override
        public void run() {
            ctxQyMc.CCtxQyMc  pQyMc  =  g_pQyMc;
            ctxQmc.CCtxQmc  pProcInfo  =  pQyMc.get_pProcInfo();

            //
            try {
                try (FrameSet frames = mPipeline.waitForFrames()) {
                    //
                    String str;
                    str = "mStreaming. loopCtrl  " + loopCtrl + " size " + frames.getSize();
                    Log.d("qysmxj", str);
                    loopCtrl++;
                    //
                    try (FrameSet processed = frames.applyFilter(mColorizer)) {
                        mGLSurfaceView.upload(processed);

                        //
                        int index_sharedObj  =  pProcInfo.smxj.index_sharedObj;
                        qmcSharedObj.QY_SHARED_OBJ  pSharedObj  =  getSharedObjByIndex(pProcInfo,index_sharedObj);
                        if  (  pSharedObj!=null) {
                            if  (  pSharedObj.bUsed) {
                                pSharedObj.var.bFrameAvail = true;
                                mySetEvent(pSharedObj.var.hEvt_notifyFrameAvail);
                            }
                        }

                    }


                }
                mHandler.post(mStreaming);
            } catch (Exception e) {
                android.util.Log.e("", "streaming, error: " + e.getMessage());
            }
        }
    };





    //

    private void configAndStart() throws Exception {
        //
        ctxQyMc.CCtxQyMc  pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo  = pQyMc.get_pProcInfo();
        int  iW  =  pProcInfo.smxj.cfg.iW;  //  640
        int  iH  =  pProcInfo.smxj.cfg.iH;  //  480

        //
        try (Config config = new Config()) {
            config.enableStream(StreamType.DEPTH, iW, iH);
            config.enableStream(StreamType.COLOR, iW, iH);
            // try statement needed here to release resources allocated by the Pipeline:start() method
            try (PipelineProfile pp = mPipeline.start(config)) {
            }
        }
    }

    public String TAG = "";


    private synchronized void start() {
        if (mIsStreaming)
            return;
        try {
            android.util.Log.d(TAG, "try start streaming");
            mGLSurfaceView.clear();
            configAndStart();
            mIsStreaming = true;
            mHandler.post(mStreaming);
            android.util.Log.d(TAG, "streaming started successfully");
        } catch (Exception e) {
            android.util.Log.d(TAG, "failed to start streaming");
        }
    }

    private synchronized void stop() {
        if (!mIsStreaming)
            return;
        try {
            android.util.Log.d(TAG, "try stop streaming");
            mIsStreaming = false;
            mHandler.removeCallbacks(mStreaming);
            mPipeline.stop();
            mGLSurfaceView.clear();
            android.util.Log.d(TAG, "streaming stopped successfully");
        } catch (Exception e) {
            android.util.Log.d(TAG, "failed to stop streaming");
        }
    }
}



