package com.qycx;


import static com.qycx.base.avCallProc.addTo_tmpTools;
import static com.qycx.base.avCallProc.removeFrom_tmpTools;
import static com.qycx.base.dlgTalk_videoConference.qyFuncs_Droid.dlgTalk_requestToSpeak;
import static com.qycx.base.qmcProc.doChkTalkDraw;
import static com.qycx.base.qmcProc.pauseTaskAv;
import static com.qycx.base.qmcProc.resumeTaskAv;
import static com.qycx.base.qmcProc.save_dlgTalk_share_eglContext_func;
import static com.qycx.base.qyWmComm.Consts.CONST_do_1080P_AV_Call;
import static com.qycx.base.qyWndContentType.Consts.CONST_fgType_talk_av2;
import static com.qycx.base.qyWndContentType.Consts.CONST_qyWndContentType_talker;
import static com.qycx.base.systemDefs.Consts.manufacturer_alps;
import static com.qycx.base.systemDefs.Consts.model_huaWei_TAHAN00m;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.opengl.EGLContext;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.qycx.base.audioRecorder_dsound;
import com.qycx.base.ctxCaller;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.dlgTalkProc;
import com.qycx.base.dlgTalkProc_help;
import com.qycx.base.dlgTalkPublic;
import com.qycx.base.dlgTalk_procTask;
import com.qycx.base.dlgTalk_videoConference;
import com.qycx.base.guiShare;
import com.qycx.base.network.NetworkChange;
import com.qycx.base.network.NetworkWatcher;
import com.qycx.base.network.Network;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.qmcProc;
import com.qycx.base.qmcSharedObj;
import com.qycx.base.qmcTaskInfo;
import com.qycx.base.qmcTransform;
import com.qycx.base.qmcTransform_start;
import com.qycx.base.qmcVideoCapture;
import com.qycx.base.qmcVideoFunc;
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qnmResDefs;
import com.qycx.base.qyCommProc_open;
import com.qycx.base.qyCusResHelp;
import com.qycx.base.qyDefs;
import com.qycx.base.qyMcMainProc;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.qySyncObj;
import com.qycx.base.qyWmComm;
import com.qycx.base.qyWndContentType;
import com.qycx.base.qy_getuiNextTranNo;
import com.qycx.base.showInfo_open;
import com.qycx.base.statuscenter.MeetingStatusCenter;
import com.qycx.base.systemDefs;
import com.qycx.base.taskAv;
import com.qycx.base.taskCommon;
import com.qycx.base.tmpFunc;
import com.qycx.base.util.Log;

import java.lang.ref.WeakReference;
import java.util.Observable;

import me.jessyan.autosize.AutoSizeCompat;

//    [Activity(Label = "Activity_dlgTalk_av", ConfigurationChanges = ConfigChanges.ScreenSize | ConfigChanges.Orientation | ConfigChanges.Keyboard)]
public class Activity_dlgTalk_av extends BaseActivity implements PopupWindow.OnDismissListener {

    private static String TAG = "Activity_dlgTalk_av";

    public WeakReference<Activity_dlgTalk> mOwner_hTalk;
    public boolean showBackSheetDialog = true;
    public Fragment_dlgTalk_av2 m_fg_dlgTalk_av2;
    BottomSheetDialog m_bsd_askToStopAv;

    //public int m_iCmd;
    //public int m_Level;
    //public int m_index_taskInfo1;

    //
    public long uiLastTickCnt_touch;

    //
    public MyHandler mHandler;
    //public Handler bottomBarHandler = new Handler(Looper.getMainLooper());
    //public Runnable runnableHideBottomBar;
    //public Runnable runnableShowBottomBar;

    //
    public int tn_dlg;


    //
    private long lastRotationTime = 0;
    private static final long MIN_ROTATION_INTERVAL = 1000;
    //  2026/09/14: 旋转触发的"关相机+重开"最小间隔（与 Fragment_dlgTalk_av2 同一策略）。
    //  连点旋转时不要排出多次 pause/resume，否则上一台相机没放开就重开 → CAMERA_IN_USE。
    private static final long ROTATION_RESTART_MIN_INTERVAL_MS = 1500;
    private long tickLastRestartCameraForRotation = 0;
    private MeetingStatusCenter messageCenter;
    boolean up = true;

    //
    private NetworkWatcher watcher = new NetworkWatcher() {
        @Override
        public void update(Observable observable, Object data) {
            super.update(observable, data);
            //观察者接受到被观察者的通知，来更新自己的数据操作。
            Network network = (Network) data;
            Log.d("***************network status changed ！sigb2828 ***********************");
            if(network.isConnected()){
                //Glide.with(MainActivity.this).load("url").into(iv);
                //            iv.setText("网络连接上了");
                Log.d("***************network is connected！sigb2828 ***********************");
            }
            else{
                //            iv.setText("网络断了");
                Log.d("***************network is disconnected！sigb2828 ***********************");
            }
        }
    };

    /**
     * up 为true 则为portrait模式，为false则是landscpe模式
     */
    public void upsetPortraitOrLandscapeLayout() {
        Log.d("qy","rotate screen: " + up);
        up = !up;
        if(up){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }


    //
    public class MyHandler extends Handler {
        Activity_dlgTalk_av mOwner;

        //
        public MyHandler(Activity_dlgTalk_av owner) {
            mOwner = owner;
        }

        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);

            Log.d("Activity_dlgTalk_av: msg.what: " + msg.what + " msg.arg1: " + msg.arg1);
            switch (msg.what) {
                case qyWmComm.Consts.CONST_qyWm_postComm:

                    //
                    switch (msg.arg1) {
                        case qyWmComm.Consts.CONST_qyWmParam_closeWnd:
                        {

                            Log.d("get a msg: closeWnd. ### CONST_qyWmParam_closeWnd ");
                            Log.d("收到退出消息");

                            /*
                            mOwner.m_fg_dlgTalk_av2.do_request(false);
                            Log.d("发送取消发言");
                            //
                            if (mOwner.mOwner_hTalk.get().m_talker.m_var.av.bAutoCancel_lostVideo) {
                                mOwner.mOwner_hTalk.get().m_talker.m_var.av.bDoneOnce_autoCancel_lostVideo = true;
                            }

                            //
                            if (mOwner.mOwner_hTalk.get().m_talker.m_var.av.taskInfo.bTaskExists) {
                                //
                                Log.d("qy","bTaskExists is true, 192, before pauseTaskAv");
                                //
                                Object hWnd = mOwner.mOwner_hTalk;
                                int  iWndType  =  CONST_qyWndContentType_talker;
                                //
                                pauseTaskAv( hWnd, iWndType,  true,"dlgTalk_av.handler.HandleMessage, get a msg closeWnd");
                                //
                            }

                             */

                            //
                            showInfo_open.qyFuncs.showInfo_open("dlgTalk_av.handleMsg doClean");
                            //
                            if (!this.mOwner.mOwner_hTalk.get().m_talker.m_var.av.taskInfo.bTaskExists) {
                                Log.d("qy","bTaskExists is false");
                            }

                            //
                            if  (  mOwner.m_fg_dlgTalk_av2!=null)  {
                                mOwner.m_fg_dlgTalk_av2.exitTaskAv();
                            }

                            //
                            Log.d("收到Activity_dlgTalk_av  doFinish 消息");
                            this.mOwner.doFinish("dlgTalk_av.doClean");

                        }
                        break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;

            }

            return;
        }

        //
    }


    //
    public qyDefs.PF_commonHandler gui_onTimer1 = (Object p0, Object p1, Object p2) -> {
        String str;

        if (m_fg_dlgTalk_av2 != null) {
            m_fg_dlgTalk_av2.gui_onTimer(p0, p1, p2);
        }
        //
        return 0;
    };

// 当用户进行任何操作时，重置计时器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Activity_dlgTalk_av  OnCreate");
        int iErr = -1;

        messageCenter = MeetingStatusCenter.getInstance();

        this.mHandler = new MyHandler(this);
        this.tn_dlg = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
        //
        Intent intent = getIntent();
        int iCmd = intent.getIntExtra("iCmd", 0);
        long ui64Id = intent.getLongExtra("ui64Id", 0);
        int level = intent.getIntExtra("level", CONST_do_1080P_AV_Call);
        int index_task = intent.getIntExtra("index_qmcTaskInfo", 0);


        //
        Log.d("ui64Id " + ui64Id);
        qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
        idInfo.ui64Id = ui64Id;
        Activity_dlgTalk hTalk = null;
        if (null == (hTalk = guiShare.GuiShare.pf_findTalker.call(pQyMc, pMisCnt, idInfo))) {
            Log.e("pf_findTalker return null");
            if (0 != iErr)
            {
                doFinish("dlgTalk_av.OnCreate failed");
            }
            return;
        }
        mOwner_hTalk = new WeakReference<>(hTalk);
        dlgTalkProc.DLG_TALK_var pMgrVar = mOwner_hTalk.get().m_talker.m_var;
        //
        if (0 != addTo_tmpTools(this, pMgrVar, "dlgTalk_av.OnCreate")) {
            Log.e("addTo_tmpTools failed");
            if (0 != iErr)
            {
                doFinish("dlgTalk_av.OnCreate failed");
            }
            return;
        }

        pQyMc.fake_bFolder = false;
        //
        if (pQyMc.iManufacturer == systemDefs.Consts.manufacturer_huaWei && pQyMc.iModel == model_huaWei_TAHAN00m) {
            DisplayMetrics dm = new DisplayMetrics();

            getWindowManager().getDefaultDisplay().getMetrics(dm);
            //
            if (0 != dm.widthPixels && 0 != dm.heightPixels) {
                float fAspect = 0.0f;
                if (dm.widthPixels > dm.heightPixels) {
                    fAspect = dm.widthPixels / (dm.heightPixels * 1.0f);
                } else {
                    fAspect = dm.heightPixels / (dm.widthPixels * 1.0f);
                }
                //
                int ii = 0;
                if (fAspect > 1.6) {
                    pQyMc.fake_bFolder = true;
                }
            }
        }

        //
        pMgrVar.m_talkAvVar.reset();
        //  这里有重要的参数需要设置
        pMgrVar.m_talkAvVar.m_iCmd = iCmd;
        pMgrVar.m_talkAvVar.m_Level = level;
        pMgrVar.m_talkAvVar.m_index_taskInfo1 = index_task;

        //
        //        AutoSizeCompat.autoConvertDensityOfGlobal(getResources());
        //  create your application here
        setContentView(R.layout.layout_activity_dlgtalk_av);
        m_fg_dlgTalk_av2 = Fragment_dlgTalk_av2.NewInstance(pMgrVar.addr.idInfo.ui64Id);



        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }

        FragmentManager fragmentManager  =  getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        if (m_fg_dlgTalk_av2 != null) {
            ft.replace(R.id.container, m_fg_dlgTalk_av2);
        }
        //              
        ft.commit();

        this.m_tn_dbg = 999;
        //
        setTimer(1000, gui_onTimer1);

        //
        pMgrVar.m_talkAvVar.m_nErrs_hTalk_av_not_matched = 0;

        //
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        NetworkChange.getInstance().addObserver(watcher);//zzq


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        MeetingStatusCenter.getInstance().registerEventBus(this);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged");
        Log.e("openRtComm", "onConfigurationChanged");
//        long currentTime = System.currentTimeMillis();
//        if (currentTime - lastRotationTime < MIN_ROTATION_INTERVAL) {
//            setRequestedOrientation(this.getResources().getConfiguration().orientation);
//            return;
//        }
//        lastRotationTime = currentTime;
//        AutoSizeCompat.autoConvertDensityOfGlobal(getResources());
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        //
        Activity_dlgTalk hTalk=mOwner_hTalk.get();
        dlgTalkProc.DLG_TALK_var m_var = hTalk.m_talker.m_var;
        if  (  m_var.av.taskInfo.bNeedRestart_camera)  {
            m_var.av.taskInfo.bNeedRestart_camera=false;
            //
            if  (  m_var.av.taskInfo.bTaskExists)  {
                //  2026/09/14: 旋转重开相机的最小间隔节流。连点"屏幕旋转"会连着来好几次
                //  onConfigurationChanged → 每次都是 pause(关相机)+resume(立刻开相机)，
                //  上一台设备还没被 camera 服务放开就又去 open → CAMERA_IN_USE，之后
                //  再也打不开。这里与 Fragment_dlgTalk_av2.restartLocalCameraForHostRotation
                //  用同一套节流策略，密集连点只保留最后一次。
                long tickNow = tmpFunc.qyFuncs.myGetTickCount(null);
                if (tickLastRestartCameraForRotation != 0
                        && tickNow - tickLastRestartCameraForRotation < ROTATION_RESTART_MIN_INTERVAL_MS) {
                    Log.e("atdx", "onConfigurationChanged: rotate restart throttled("
                            + (tickNow - tickLastRestartCameraForRotation) + "ms), skip");
                    return;
                }
                tickLastRestartCameraForRotation = tickNow;
                //
                Object  hWnd  = hTalk;
                int  iWndType  =  CONST_qyWndContentType_talker;
                //
                pauseTaskAv(  hWnd, iWndType,false,"onConfigurationChanged: bNeedRestartCamera");
                resumeTaskAv(hWnd,iWndType,"onConfigurationChanged: bNeedRestartCamera");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Activity_dlgTalk_av  OnResume");

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;

        if(pQyMc==null) return;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        pProcInfo.av.m_bInSleepMode = false;

        //
        Object hWnd=mOwner_hTalk.get();
        int  iWndType = CONST_qyWndContentType_talker;

        //
        dlgTalkProc.DLG_TALK_var m_var = mOwner_hTalk.get().m_talker.m_var;

        //
        if (m_var.m_bNeedResumeTaskAv) {
            resumeTaskAv( hWnd, iWndType, "OnResume");
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        //
        dlgTalkProc.DLG_TALK_var m_var  = mOwner_hTalk.get().get_pm_var();



        //
        uiLastTickCnt_touch = tmpFunc.qyFuncs.myGetTickCount(null);
        return super.dispatchTouchEvent(e);

    }

    //
    int doClean(String str) {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        killTimer("Act_dlgTalk_av.doClean");
        if (m_fg_dlgTalk_av2 != null) {
            m_fg_dlgTalk_av2.doClean_ifActivityIsFinishing();
        }
        return 0;
    }

    @Override
    protected void onStop() {
        /**/

        //  手工取消要清除状态
        //this.mOwner_hTalk.get().m_var.av.m_bState_canSpeak = false;
        if (null != mHandler) {
            //  mHandler.removeCallbacksAndMessages(null);

        }

        guiShare.GuiShare.pf_PostMessage_new.get().call(this, qyWndContentType.Consts.CONST_qyWndContentType_talk_av, qyWmComm.Consts.CONST_qyWm_postComm, qyWmComm.Consts.CONST_qyWmParam_closeWnd, 0);
        Log.d("activity_dlgTalk_av 进入后台 发送退会消息");
        Log.d(TAG, "Activity_dlgTalk_av  OnStop");

        //
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.d("Activity_dlgTalk_av  OnPause");
        //
        String str;
        str = String.format("dlgTalk_av: onPause, %s", isFinishing() ? "isFinishing()" : "");
        Log.d(str);
        if (isFinishing()) {
            //
            doClean("OnPause,isFinishing");
        }

        //
        super.onPause();


    }

    protected void onDestroy() {
        dlgTalkProc.DLG_TALK_var m_var = mOwner_hTalk.get().get_pm_var();

        //

        //
        if  (  false ) {
            Log.d(TAG, "Activity_dlgTalk_av  OnDestroy");
            Log.d("dlgTalk_av.OnDestroy enters");
        }

        //
        doClean("onDestroy");
        MeetingStatusCenter.getInstance().unRegisterEventBus(this);

        //
        if (mOwner_hTalk != null) {
            removeFrom_tmpTools(this, mOwner_hTalk.get().m_talker.m_var, "OnDestroy");
        }
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            Log.d("mHandler.removeCallbacksAndMessages(null);");
            mHandler = null;
        }

        Log.d("dlgTalk_av.OnDestroy leaves");
        //
        super.onDestroy();
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) {
            Log.e("pQyMc == null");
            return;
        }
        ((MainActivity) pQyMc.gui.hMainWnd).canReceiveMessage = true;
        NetworkChange.getInstance().deleteObserver(watcher);
    }

    //
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");
        //
        //横竖屏切换前调用，保存用户想要保存的数据，以下是样例
        outState.putString("name", "yoosir");
        int ii = 0;

        //
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if(pQyMc == null)//for once crash, zzq
            return;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        //
        pProcInfo.av.m_bInSleepMode = true;

        //
        //pauseTaskAv("OnSaveInstanceState");

    }


    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //
        if (savedInstanceState != null) {
            String name = savedInstanceState.getString("name");
            //
            int ii = 0;

            //
        }
    }

    //


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if  (  pQyMc==null) return false;
        if  (  this.mOwner_hTalk==null)  return  false;
        Activity_dlgTalk  hTalk  = ( Activity_dlgTalk)this.mOwner_hTalk.get();
        if  (  hTalk  ==  null  )  return  false;
        if  (  this.m_fg_dlgTalk_av2==null) return false;

        //
        Log.d("qy","onKeyDown,keyCode="  +  keyCode);

        if  (pQyMc.iManufacturer  ==  manufacturer_alps)  {
            switch  (  keyCode  )  {
                case  135:  //  SOS
                    break;
                case  136:  //  对讲
                    //
                    this.m_fg_dlgTalk_av2.btn_request_click();;
                    //
                    break;
                default:
                    break;
            }
        }
        //
        return super.onKeyDown(keyCode, event);
    }

    /***
     * 关闭窗口！
     * @return
     */
    /*
    public int doAutoCancel() {

        dlgTalkProc.DLG_TALK_var m_var = this.mOwner_hTalk.get().get_pm_var();
        String str;

        //
        str = "dlgTalk_av.doAutoCancel, " + this.tn_dlg;
        Log.d(str);

        //  自动关闭视频窗口
        guiShare.GuiShare.pf_PostMessage_new.get().call(this, qyWndContentType.Consts.CONST_qyWndContentType_dlgTalk_av, qyWmComm.Consts.CONST_qyWm_postComm, qyWmComm.Consts.CONST_qyWmParam_closeWnd, 0);

        return 0;
    }

     */


    //
    public void onBackPressed() {
        Log.d("onBackPressed" + m_fg_dlgTalk_av2.currentVideoStatus);

        if (m_fg_dlgTalk_av2.currentVideoStatus == 0) {
            //
            exitDlgTalkAv();


            // super.onBackPressed();
        } else {
            m_fg_dlgTalk_av2.resetCurrentVideoChanel();
        }

    }

    //
    public void exitDlgTalkAv() {
        //
        if (!showBackSheetDialog) {
            BtnYes_Click.onClick(null);
            return;
        }

        dlgTalkProc.DLG_TALK_var m_var = this.mOwner_hTalk.get().get_pm_var();
        String str;

        Log.d("exitDlgTalkAv()点击退出");

        //
        // if (m_var.av.taskInfo.bTaskExists)
        //    {
        Log.d("m_bsd_askToStopAv.setContentView(view)点击退出");

        View view = this.getLayoutInflater().inflate(R.layout.layout_yescancel, null);
        this.m_bsd_askToStopAv = new BottomSheetDialog(this);
        m_bsd_askToStopAv.setContentView(view);
        //
        BottomSheetBehavior behavior = BottomSheetBehavior.from((View) view.getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        //
        TextView tvTitle = (TextView) view.findViewById(R.id.title);
        tvTitle.setText("清扬视频");
        TextView tvContent = (TextView) view.findViewById(R.id.content);
        tvContent.setText("要停止视频通话吗？");
        //
        Button btnYes = view.findViewById(R.id.yes);
        btnYes.setOnClickListener(BtnYes_Click);
        Button btnCancel = view.findViewById(R.id.cancel);
        btnCancel.setOnClickListener(BtnCancel_Click);

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc != null) {
            btnYes.setText(qyCusResHelp.qyFuncs.getResStr(0, pQyMc.cusRes, (int) qnmResDefs.CONST_resId_yes));
            btnCancel.setText(qyCusResHelp.qyFuncs.getResStr(0, pQyMc.cusRes, (int) qnmResDefs.CONST_resId_cancel));
            m_bsd_askToStopAv.show();
        }
    }


    //
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //
        if (hasFocus) {
            hideSystemUI();
        } else {
            showSystemUI();
        }
    }

    public void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            /*     View decorView = this.Window.DecorView;
                 decorView.SystemUiVisibility=(StatusBarVisibility)(
                         SystemUiFlags.Immersive
                         // Set the content to appear under the system bars so that the
                         // content doesn't resize when the system bars hide and show.
                         | SystemUiFlags.LayoutStable
                         | SystemUiFlags.LayoutHideNavigation
                         | SystemUiFlags.LayoutFullscreen
                         // Hide the nav bar and status bar
                         | SystemUiFlags.HideNavigation
                         | SystemUiFlags.Fullscreen);*/
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    public void showSystemUI() {
            /*  View decorView = Window.DecorView;
              decorView.SystemUiVisibility=(StatusBarVisibility)(
                      SystemUiFlags.LayoutStable
                      | SystemUiFlags.LayoutHideNavigation
                      | SystemUiFlags.LayoutFullscreen);*/
    }


    //
    private View.OnClickListener BtnCancel_Click = (View view) -> {
        //
        Log.d("BtnCancel_Click");
        showInfo_open.qyFuncs.showInfo_open("dlgTalk_av.askToStopAv, BtnCancel_Click...BBBBBBBBBBBBB");

        //
        m_bsd_askToStopAv.dismiss();
    };

    private View.OnClickListener BtnYes_Click = (View view) -> {

        //
        Log.d("BtnYes_Click");
        showInfo_open.qyFuncs.showInfo_open("dlgTalk_av.askToStopAv, BtnYes_Click...AAAAAAAAAAAAAAAAAAA");


        //
        if (showBackSheetDialog) {
            m_bsd_askToStopAv.dismiss();
        }
        //  手工取消要清除状态
        this.mOwner_hTalk.get().m_talker.m_var.av.m_bState_canSpeak = false;
        if (null != mHandler) {
            //  mHandler.removeCallbacksAndMessages(null);
        }
        //

        guiShare.GuiShare.pf_PostMessage_new.get().call(this, qyWndContentType.Consts.CONST_qyWndContentType_talk_av, qyWmComm.Consts.CONST_qyWm_postComm, qyWmComm.Consts.CONST_qyWmParam_closeWnd, 0);
        Log.d("发送点击退出msg");
    };







    //
    public static guiShare.PF_initVideoCapDev initVideoCapDev = (Object hWnd, int iWndContentType, String displayName_selected, qyCommProc_open.BITMAPINFOHEADER pBih_suggested, qmcSharedObj.CAP_procInfo_bmpCommon pCapBmp) -> {
        //
        if (iWndContentType == qyWndContentType.Consts.CONST_qyWndContentType_talker) {
            //
            Activity_dlgTalk dlgTalk;
            if (hWnd instanceof Activity_dlgTalk) {
                dlgTalk = (Activity_dlgTalk) hWnd;
            } else {
                dlgTalk = (Activity_dlgTalk) ((WeakReference<?>) hWnd).get();
            }

            if (!guiShare.GuiShare.pf_IsWindow.call(dlgTalk.m_talker.m_var.m_hTool_dlgTalk_av)) return -1;
            Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) dlgTalk.m_talker.m_var.m_hTool_dlgTalk_av;

            if (dlgTalk_av.m_fg_dlgTalk_av2 != null) {
                return Fragment_dlgTalk_av2.initVideoCapDev(dlgTalk, iWndContentType, displayName_selected, pBih_suggested, pCapBmp);
            }
        }
        else  if  (  iWndContentType  ==  CONST_fgType_talk_av2){
                  //
            /*
                  Fragment_dlgTalk_av2 fg = (Fragment_dlgTalk_av2) hWnd;
                  //
                  Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) fg.getActivity();
                  if  (  dlgTalk_av==null)  return  -1;

                  //
                  Activity_dlgTalk  dlgTalk;
                  dlgTalk  =  (Activity_dlgTalk) dlgTalk_av.mOwner_hTalk.get();
                  iWndContentType =  CONST_qyWndContentType_talker;

             */

                  //
            //if (dlgTalk_av.m_fg_dlgTalk_av2 != null)
            {
                return Fragment_dlgTalk_av2.initVideoCapDev(hWnd, iWndContentType, displayName_selected, pBih_suggested, pCapBmp);
            }

        }

        //
        return -1;
    };

    public static guiShare.PF_exitVideoCapDev exitVideoCapDev = (Object hWnd, int iWndContentType) -> {
        int iErr = -1;

        //
        if  (  hWnd  ==  null  )  {
            return  -1;
        }
        //
        if (hWnd instanceof WeakReference<?>) {
            hWnd=((WeakReference<?>) hWnd).get();
        }
        if  (  hWnd  ==  null  )  {
            return  -1;
        }

        //
        if (iWndContentType == qyWndContentType.Consts.CONST_qyWndContentType_talker) {


            //
            Activity_dlgTalk dlgTalk = (Activity_dlgTalk) hWnd;
            if (null == dlgTalk.m_talker.m_var.m_hTool_dlgTalk_av) return -1;
            Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) dlgTalk.m_talker.m_var.m_hTool_dlgTalk_av;

            //
            if (dlgTalk_av.m_fg_dlgTalk_av2 != null) {
                return Fragment_dlgTalk_av2.exitVideoCapDev(hWnd, iWndContentType);
            }
        }
        else  if  (  iWndContentType==CONST_fgType_talk_av2)  {
                  return  Fragment_dlgTalk_av2.exitVideoCapDev(hWnd,iWndContentType);
        }

        //
        return -1;

    };


    public static guiShare.PF_bGetCapturePara bGetCapturePara = (Object hWnd, int iWndContentType, qyCommProc_open.QY_VIDEO_HEADER pVh_org) -> {
        boolean bRet = false;

        //
        if (iWndContentType == qyWndContentType.Consts.CONST_qyWndContentType_talker) {
            Activity_dlgTalk dlgTalk = (Activity_dlgTalk) hWnd;//((WeakReference<Activity_dlgTalk>) hTalk).get();
            if (!guiShare.GuiShare.pf_IsWindow.call(dlgTalk.m_talker.m_var.m_hTool_dlgTalk_av)) return false;
            Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) dlgTalk.m_talker.m_var.m_hTool_dlgTalk_av;

            if (dlgTalk_av.m_fg_dlgTalk_av2 != null) {
                return Fragment_dlgTalk_av2.bGetCapturePara(dlgTalk, iWndContentType, pVh_org);
            }
        }
        else if  (  iWndContentType  ==  CONST_fgType_talk_av2)  {
            Fragment_dlgTalk_av2 fg = (Fragment_dlgTalk_av2) hWnd;
            //
            /*
            Activity_dlgTalk_av  dlgTalk_av=(Activity_dlgTalk_av) fg.mOwner_wnd;
            //
            Object  dlgTalk=dlgTalk_av.mOwner_hTalk.get();
            iWndContentType=CONST_qyWndContentType_talker;

             */
            //
            //if (dlgTalk_av.m_fg_dlgTalk_av2 != null)
            {
                return Fragment_dlgTalk_av2.bGetCapturePara(hWnd, iWndContentType, pVh_org);
            }

        }

        return false;

    };


    public static guiShare.PF_startCapture startCapture = (Object hWnd, int iWndContentType) -> {

        if (iWndContentType == qyWndContentType.Consts.CONST_qyWndContentType_talker) {
            Activity_dlgTalk dlgTalk = (Activity_dlgTalk) hWnd;//((WeakReference<Activity_dlgTalk>) hTalk).get();
            if (!guiShare.GuiShare.pf_IsWindow.call(dlgTalk.m_talker.m_var.m_hTool_dlgTalk_av)) return -1;
            Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) dlgTalk.m_talker.m_var.m_hTool_dlgTalk_av;


            if (dlgTalk_av.m_fg_dlgTalk_av2 != null) {
                return Fragment_dlgTalk_av2.startCapture(hWnd, iWndContentType);
            }
        }
        else  if  (  iWndContentType==CONST_fgType_talk_av2)  {
                  Fragment_dlgTalk_av2 fg = (Fragment_dlgTalk_av2) hWnd;

                  //
                  return Fragment_dlgTalk_av2.startCapture(hWnd, iWndContentType);

        }

        //
        return 0;
    };


    //
    public static guiShare.PF_stopCapture stopCapture = (Object hWnd, int iWndContentType) -> {

        //
        if (hWnd == null) return -1;
        if (hWnd instanceof WeakReference<?>) {
            hWnd = ((WeakReference<?>) hWnd).get();
        }
        if (hWnd == null) return -1;

        //
        if (iWndContentType == qyWndContentType.Consts.CONST_qyWndContentType_talker) {

            Activity_dlgTalk dlgTalk = (Activity_dlgTalk) hWnd;
            if (!guiShare.GuiShare.pf_IsWindow.call(dlgTalk.m_talker.m_var.m_hTool_dlgTalk_av)) return -1;
            Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) dlgTalk.m_talker.m_var.m_hTool_dlgTalk_av;

            //

            if (dlgTalk_av.m_fg_dlgTalk_av2 != null) {
                return Fragment_dlgTalk_av2.stopCapture(hWnd, iWndContentType);
            }
        }
        else if  (  iWndContentType==CONST_fgType_talk_av2)  {

            return Fragment_dlgTalk_av2.stopCapture(hWnd, iWndContentType);

        }
        else  {
            Log.d("at","stopCapture failed, iWndType err");
            return  -1;
        }

        return 0;
    };

    //
    /*
    public int doTask_av(int iCmd, int iTaskId) {
        Activity_dlgTalk_av dlgTalk_av = this;
        //

        if (dlgTalk_av.m_fg_dlgTalk_av2 != null) {
            return dlgTalk_av.m_fg_dlgTalk_av2.doTask_av(iCmd, iTaskId);
        }

        return -1;
    }

     */

    public void onDismiss() {
        m_fg_dlgTalk_av2.onDismiss();
    }
}


