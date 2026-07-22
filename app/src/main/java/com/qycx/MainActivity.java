package com.qycx;

import com.south.map.ui.Fragment_map;

import static com.qycx.anc.Consts.Str_use100k_bDj;
import static com.qycx.anc.Consts.Str_use100k_ifP2p;
import static com.qycx.anc.atFuncs.sendToRefreshRecentFriends;
import static com.qycx.base.avCallProc.act_toAvCall;
import static com.qycx.base.avCallProc.addTo_tmpTools;
import static com.qycx.base.avCallProc.doTask_av_accept;
import static com.qycx.base.avCallProc.removeFrom_tmpTools;
import static com.qycx.base.ctxCaller.Consts.CONST_ctxCallerType_thread;
import static com.qycx.base.ctxQyTmpl.Consts.CONST_ctxType_qmc;
import static com.qycx.base.dlgTalkProc_help.qyFuncs_Droid.bDone_retrieveAllImObjRules;
import static com.qycx.base.dlgTalkProc_help.qyFuncs_Droid.talkToMessenger;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_refreshRecentFriendsReq;
import static com.qycx.base.isTalkerQProc.findIsTalker;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_recv_accept;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_recv_cancel;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_input;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_talk;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_taskStatus;
import static com.qycx.base.qyMcMain_global.g.g_pQyMc;
import static com.qycx.base.qyMcMain_qyMc_proc.qyFuncs_Droid.InitInstance;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_chkTalkDraw;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_closeTaskAv;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_closeWnd;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_doTask;
import static com.qycx.base.qyWndContentType.Consts.CONST_fgType_talk_av2;
import static com.qycx.base.tmpFunc.qyFuncs.myGetCurrentProcessId;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;
import static com.qycx.base.tmpProc.qyFuncs.bTaskActive;

import android.Manifest;

import static org.aviran.cookiebar2.CookieBarDismissListener.DismissType.DURATION_COMPLETE;
import static org.aviran.cookiebar2.CookieBarDismissListener.DismissType.USER_ACTION_CLICK;
import static org.aviran.cookiebar2.CookieBarDismissListener.DismissType.USER_DISMISS;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.qycx.anc.atFuncs;
import com.qycx.base.CIsTalker;
import com.qycx.base.GuiShareProc;
import com.qycx.base.MyThread;
import com.qycx.base.audioRecorder_android;
import com.qycx.base.ctxCaller;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.dlgTalkProc;
import com.qycx.base.dlgTalkProc_help;
import com.qycx.base.getTalkerDesc;
import com.qycx.base.guiShare;
import com.qycx.base.isCliHelpPublic;
import com.qycx.base.mainWnd_procMsg;
import com.qycx.base.prepare;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.qmcTaskInfo;
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qnmResDefs;
import com.qycx.base.qyComm;
import com.qycx.base.qyCusResHelp;
import com.qycx.base.qyDefs;
import com.qycx.base.qyFuncs;
import com.qycx.base.qyMcMainRealTimeMediaProc;
import com.qycx.base.qyMcMainWndProc;
import com.qycx.base.qyMcMainWndProc_isCli;
import com.qycx.base.qyMcMain_qyMc_proc;
import com.qycx.base.qyWmComm;
import com.qycx.base.qyWndContentType;
import com.qycx.base.showInfo_open;
import com.qycx.base.test;
import com.qycx.base.tmpFunc;
import com.qycx.base.util.Log;
import com.qycx.base.util.javatuples.Pair;
import com.south.map.loc.MapLocKeyword;
import com.south.map.loc.MapLocSettings;
import com.south.map.loc.MapLocStrCrypto;

import org.aviran.cookiebar2.*;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;


//    [Activity(Label = "@String/appName", Icon = "@drawable/icon", Theme = "@style/MainTheme", MainLauncher = true, ConfigurationChanges = ConfigChanges.ScreenSize | ConfigChanges.Orientation | ConfigChanges.UiMode | ConfigChanges.ScreenLayout | ConfigChanges.SmallestScreenSize)]
//public class MainActivity : global::Xamarin.Forms.Platform.Android.FormsAppCompatActivity
public class MainActivity extends qmcBaseActivity implements View.OnClickListener, androidx.viewpager.widget.ViewPager.OnPageChangeListener {
    public qyMcMainWndProc.QY_MC_mainWndVar var_common;
//ZONE_objs_info wall;

    //
    public String m_str_title;

    //
    public RelativeLayout m_layout_total;
    //private TextView txt_topbar;
    //
    private View tab_map;
    private View tab_msg;
    private View tab_contact;
    private View tab_me;
    private View tab_map_indicator;
    private View tab_msg_indicator;
    private View tab_contact_indicator;
    private View tab_me_indicator;
    private ViewPager vPager;
    private BroadcastReceiver screenOffReceiver;
    private boolean locKeywordDialogShowing;
    private int lastPagerPosition = -1;

    //Fragment Object
//private MyFragment fg1, fg2, fg3, fg4;
    private FragmentPagerAdapter_main mAdapter;

    public final static int PAGE_ONE = 0;
    public final static int PAGE_TWO = 1;
    public final static int PAGE_THREE = 2;
    public final static int PAGE_FOUR=3;

    public FragmentPagerAdapter_main getMainPagerAdapter() {
        return mAdapter;
    }

    public void notifyMeActiveGroupChanged() {
        if (mAdapter != null && mAdapter.mFragment_me != null) {
            mAdapter.mFragment_me.onActiveGroupChanged();
        }
    }

    //
    public MyHandler mHandler = null;
    //
//public DLG_TALK_videoConference[] m_tmp_videoConferences = new DLG_TALK_videoConference[Consts.DEFAULT_usMaxCnt_taskInfos];
    public qmcTaskInfo.QMC_taskData_common[] m_tmp_taskDatas = new qmcTaskInfo.QMC_taskData_common[qyMcMainRealTimeMediaProc.Consts.DEFAULT_usMaxCnt_taskInfos];
    public MyThread[] m_tmp_threads = new MyThread[qyMcMainRealTimeMediaProc.Consts.DEFAULT_usMaxCnt_androidThreads];
    public Semaphore hMutex_syncThreads = new Semaphore(1);

    //
    public static String g_model_file_dir;


    //
    public class MyHandler extends Handler {

        MainActivity mOwner;

        public MyHandler(MainActivity owner) {
            mOwner = owner;
        }

        @Override
        public void handleMessage(Message msg) {

            ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
            if (pQyMc == null) {
                if (msg.what == qyWmComm.Consts.CONST_qyWm_postComm
                        && msg.arg1 == CONST_qyWmParam_closeWnd) {
                    Log.d("get a msg: closeWnd. to doFinish if pQyMc is null");
                    mOwner.doFinish("MyHandler.get a msg closeWnd");
                }
                return;
            }
            MainActivity mainWnd = (MainActivity) pQyMc.gui.hMainWnd;
            //
            //Log.d("mainWnd.MyHandler.HandleMessage: msg.what: " + msg.what + " msg.arg1: " + msg.arg1);
            //
            switch (msg.what) {
                case qyWmComm.Consts.CONST_qyWm_postComm:
                    //
                    int wParam = msg.arg1;
                    int lParam = msg.arg2;
                    //
                    switch (msg.arg1) {
                        case qyWmComm.Consts.CONST_qyWmParam_login_canceled:
                            //if ( !pQyMc.bLogon )


                            Log.d("get a msg: login canceled");
                            mainWnd.doFinish("login canceled");
                            break;
                        case CONST_qyWmParam_closeWnd: {
                            //
                            Log.d("qy","mainActivity: get a msg: CONST_qyWmParam_closeWnd");
                            //
                            if (!pQyMc.bLogon) {
                                guiShare.GuiShare.pf_PostMessage_new.get().call(mainWnd, qyWndContentType.Consts.CONST_qyWndContentType_mainWnd, qyWmComm.Consts.CONST_qyWm_postComm, qyWmComm.Consts.CONST_qyWmParam_login_canceled, 0);
                                break;
                            }
                            //
                            closeFloatingView();
                            //
                            if (!mainWnd.m_bReadyToQuit) {
                                if (mainWnd.isActive) {
                                    if (mainWnd.m_bsd_askToQuit != null) {
                                        mainWnd.m_bsd_askToQuit.dismiss();
                                        mainWnd.m_bsd_askToQuit = null;
                                    }
                                    View view = mainWnd.getLayoutInflater().inflate(R.layout.layout_yescancel, null);
                                    mainWnd.m_bsd_askToQuit = new BottomSheetDialog(mainWnd);
                                    mainWnd.m_bsd_askToQuit.setContentView(view);
                                    //
                                    BottomSheetBehavior behavior = BottomSheetBehavior.from((View) view.getParent());
                                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                    //
                                    TextView tvTitle = (TextView) view.findViewById(R.id.title);
                                    tvTitle.setText("清扬视频");
                                    TextView tvContent = (TextView) view.findViewById(R.id.content);
                                    tvContent.setText("要退出吗？");
                                    //
                                    Button btnYes = view.findViewById(R.id.yes);
                                    btnYes.setOnClickListener(BtnYes_Click);
                                    Button btnCancel = (Button) view.findViewById(R.id.cancel);
                                    btnCancel.setOnClickListener(BtnCancel_Click);
                                    ;

                                    btnYes.setText(qyCusResHelp.qyFuncs.getResStr(0, pQyMc.cusRes, qnmResDefs.CONST_resId_yes));
                                    btnCancel.setText(qyCusResHelp.qyFuncs.getResStr(0, pQyMc.cusRes, qnmResDefs.CONST_resId_cancel));

                                    mainWnd.m_bsd_askToQuit.show();
                                }
                                break;
                            }

                            if (mainWnd.m_progressDialog != null) break;
                            MainActivity.cookieBar = null;
//                            android.os.Process.killProcess(android.os.Process.myPid());
                            if(mOwner.var_common.notifyTaskStatus.curNotify.hParent != null)
                                tmp_notify_cancel_ifForegrounded((Activity) mOwner.var_common.notifyTaskStatus.curNotify.hParent.get());
                            ctxQyMc.qyFuncs.startToExitQyMc(pQyMc);
                            mainWnd.m_progressDialog = new ProgressDialog(mainWnd);
                            mainWnd.m_progressDialog.setMax(100);// Progress Dialog max Value
                            mainWnd.m_progressDialog.setMessage(qyCusResHelp.qyFuncs.getResStr(0, pQyMc.cusRes, qnmResDefs.CONST_resId_waitToQuit)); // setting Message
                            mainWnd.m_progressDialog.setTitle(qyCusResHelp.qyFuncs.getResStr(0, pQyMc.cusRes, qnmResDefs.CONST_resId_progress)); // setting Title
                            mainWnd.m_progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // Progress Dialog Style Horizontal
                            mainWnd.m_progressDialog.show(); // Display Progress Dialog
                            mainWnd.m_progressDialog.setCancelable(false);

                            mainWnd.m_progressHandler = new Tmp_progressHandler(mainWnd.m_progressDialog);
                            mainWnd.m_progressThread = new Tmp_progressThread(mainWnd.m_progressHandler);
                            mainWnd.m_progressThread.start();
                        }
                        break;
                        case qyWmComm.Consts.CONST_qyWmParam_threadProc_isCli_end: {
                            mainWnd.doClean("after msg threadProc_isCli_end");

                            if (mainWnd.m_progressThread != null) {
                                mainWnd.m_progressThread.bQuit = true;

                                try {
                                    mainWnd.m_progressThread.stop();
                                } catch (UnsupportedOperationException ie) {

                                }

                                mainWnd.m_progressThread = null;
                            }
                            //
                            if (mainWnd.m_progressDialog != null) {
                                mainWnd.m_progressDialog.dismiss();
                                mainWnd.m_progressDialog = null;
                            }
                            //

                            mainWnd.m_progressHandler = null;
                            //
                            mainWnd.doFinish("mainAct.after msg threadProc_isCli_end");
                        }
                        break;

                        case qyWmComm.Consts.CONST_qyWmParam_imObj_needRefreshed:
                            //
                            Log.d("anc","mainWnd.handler: imObj_needRefreshed");

                            //
                            if (mainWnd.mAdapter.mFragment_imObj1 != null) {
                                //
                                mainWnd.mAdapter.mFragment_imObj1.fillList_imObj();
                                mainWnd.mAdapter.mFragment_imObj1.display_imObjList();
                            }
                            if (mainWnd.mAdapter.mFragment_map instanceof Fragment_map) {
                                ((Fragment_map) mainWnd.mAdapter.mFragment_map).onActiveGroupCandidatesChanged();
                            }
                            if (mainWnd.mAdapter.mFragment_me != null) {
                                mainWnd.notifyMeActiveGroupChanged();
                            }
                            break;
                        case qyWmComm.Consts.CONST_qyWmParam_allContactsRetrieved:
                            //
                            // qyFuncs.showNotification_open(0, 0, 0, qyFuncs.getResStr(0, pQyMc.cusRes, (int)qnmResDefs.CONST_resId_allImObjectsRetrieved));
                            //
                            Log.d("anc", "mainWnd.handler: allContactsRetrieved "  +  qyCusResHelp.qyFuncs.getResStr(0, pQyMc.cusRes, R.string.CONST_resId_allImObjectsRetrieved));

                            //
                            if (null != mainWnd.mAdapter && mainWnd.mAdapter.mFragment_msg1 != null) {
                                mainWnd.mAdapter.mFragment_msg1.loadRecentContacts();
                            }
                            if (mainWnd.mAdapter != null && mainWnd.mAdapter.mFragment_map instanceof Fragment_map) {
                                ((Fragment_map) mainWnd.mAdapter.mFragment_map).onActiveGroupCandidatesChanged();
                            }
                            if (mainWnd.mAdapter != null) {
                                mainWnd.notifyMeActiveGroupChanged();
                            }

                            //
                            break;
                            //
                        case  qyWmComm.Consts.CONST_qyWmParam_doTask: {
                            Log.d("at","l4168: doTask");
                            //
                            short usOp = (short)lParam;
                            //
                            if  (  usOp==CONST_imOp_recv_accept) {
                                //
                                Log.d("at","accept");
                                //
                                mainWnd.doTask(usOp);
                                break;
                            }
                            //
                            break;
                        }
                        //
                        case  CONST_qyWmParam_closeTaskAv:{
                            //
                            //
                            closeFloatingView();
                            //
                            break;
                        }

                        //
                        case qyWmComm.Consts.CONST_qyWmParam_msgArrive:
                        default:
                            qyMcMainWndProc_isCli.qyFuncs_Droid.mainWnd_OnQyPostComm(pQyMc.gui.hMainWnd, mainWnd.var_common, msg.arg1, msg.arg2);
                            break;
                    }

                    break;
                default:
                    break;
            }
            //
            return;
        }

        private View.OnClickListener BtnCancel_Click = (View view) ->
        {
            //throw new NotImplementedException();
            mOwner.m_bsd_askToQuit.dismiss();
        };

        private View.OnClickListener BtnYes_Click = (View view) ->
        {
            //
            String str;
            str = "mainActivity.BtnYes_Click";
            showInfo_open.qyFuncs.showInfo_open(str);
            Log.d("qy",str);

            //throw new NotImplementedException();
            mOwner.m_bsd_askToQuit.dismiss();
            mOwner.m_bReadyToQuit = true;
            //
            guiShare.GuiShare.pf_PostMessage_new.get().call(mOwner, qyWndContentType.Consts.CONST_qyWndContentType_mainWnd, qyWmComm.Consts.CONST_qyWm_postComm, CONST_qyWmParam_closeWnd, 0);
            //  System.Environment.Exit(0);
        };
    }

    //
    public guiShare.PF_PostMessage_new gui_PostMessage_new = (Object wnd, int iWndContentType, int msgId, int wParam, int lParam) -> {

        Handler handler = null;

        //
        if  (  true  )  {
            if  (  wParam  ==  CONST_qyWmParam_closeWnd)  {
                Log.d("qy","closeWnd");
            }
            if  (  wParam  ==  CONST_qyWmParam_doTask)  {
                Log.d("qy","doTask");
            }
            if  (  wParam  ==  CONST_qyWmParam_closeTaskAv)  {
                Log.d("qy","closeTaskAv");
            }
            if  (  wParam==CONST_qyWmParam_chkTalkDraw)  {
                Log.d("at","chkTalkDraw");
            }
            //
        }


        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (pQyMc != null) {
            if (wnd == pQyMc.gui.hMainWnd) {
                MainActivity mainWnd = (MainActivity) wnd;
                handler = mainWnd.mHandler;
            }
        }



        //
        if (handler == null) {
            switch (iWndContentType) {
                case qyWndContentType.Consts.CONST_qyWndContentType_mainWnd: {
                    MainActivity mainWnd = (MainActivity) wnd;
                    handler = mainWnd.mHandler;
                }
                break;
                case qyWndContentType.Consts.CONST_qyWndContentType_talk_av: {
                    Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) wnd;
                    handler = dlgTalk_av.mHandler;
                }
                break;
                case qyWndContentType.Consts.CONST_qyWndContentType_talker: {
                    Activity_dlgTalk dlgTalk = (Activity_dlgTalk) wnd;
                    handler = dlgTalk.mHandler;
                }
                break;
                case  CONST_fgType_talk_av2: {
                      Fragment_dlgTalk_av2 fg =(Fragment_dlgTalk_av2) wnd;
                      handler=fg.mMainHandler;
                }
                break;
                default:
                    break;
            }
        }

        //
        if (handler != null) {
            Message msg = handler.obtainMessage();
            msg.what = msgId;
            msg.arg1 = wParam;
            msg.arg2 = lParam;
            handler.sendMessage(msg);
            return 0;
        }

        return 0;
    };

    public guiShare.PF_PostMessage gui_PostMessage = (Object wnd, int msgId, int wParam, int lParam) -> {
        return gui_PostMessage_new.call(wnd, 0, msgId, wParam, lParam);
    };


    public guiShare.PF_IsWindow gui_IsWindow = (Object hWnd) -> {
        if (hWnd == null) return false;
        if (hWnd instanceof WeakReference) {
            Activity activity = ((WeakReference<Activity>) hWnd).get();
            return !activity.isFinishing();
        } else if (hWnd instanceof Activity) {
            Activity wnd = (Activity) hWnd;
            return !wnd.isFinishing();
        } else if(hWnd instanceof  Fragment) {
            //
            return true;
        }

        //
        return false;
//        WeakReference<Activity> wnd = (WeakReference<Activity>) hWnd;
    };

    public guiShare.PF_mainWnd_getVar mainWnd_getVar = () -> {
        return this.var_common;
    };


    /////////////////////
//
    public static guiShare.PF_gui_procMsg gui_procMsg = (Object pMsgParam) -> {
        int iErr = -1;

        String str;
        boolean bDbg = false;

        //
        qnmCommProc_mis.MIS_MSGU pMsg = (qnmCommProc_mis.MIS_MSGU) pMsgParam;
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
        qyDefs.QY_MESSENGER_ID addr_logicalPeer_idInfo = new qyDefs.QY_MESSENGER_ID();
        addr_logicalPeer_idInfo.ui64Id = 0;

        qnmCommProc_mis.MIS_MSGU tmp_pMsg = pMsg;
        //
        switch (pMsg.uiType) {
            case CONST_misMsgType_talk:
                qnmCommProc_mis.MIS_MSG_TALK talk = (qnmCommProc_mis.MIS_MSG_TALK) tmp_pMsg;
                addr_logicalPeer_idInfo = talk.addr_logicalPeer.idInfo;
                break;
            case CONST_misMsgType_input: {
                qnmCommProc_mis.MIS_MSG_INPUT input = (qnmCommProc_mis.MIS_MSG_INPUT) tmp_pMsg;
                if (!(
                        //qyComm.qyFuncs.isUcFlgTalkData(input.ucFlg) ||
                                qyComm.qyFuncs.isUcFlgRouteTalkData(input.ucFlg))) {
                    return iErr;
                }
                addr_logicalPeer_idInfo = input.addr_logicalPeer.idInfo;
            }
            break;
            case CONST_misMsgType_taskStatus: {
                qnmCommProc_mis.MIS_MSG_taskStatus taskStatus = (qnmCommProc_mis.MIS_MSG_taskStatus) tmp_pMsg;
                addr_logicalPeer_idInfo = taskStatus.idInfo_logicalPeer;
            }
            break;
            default:
                return iErr;
        }

        //
        test.testCls_Droid.testChk("mainWnd.gui_procMsg.398");

        //
        if (mainActivity.mAdapter.mFragment_msg1 != null) {
            mainActivity.mAdapter.mFragment_msg1.gui_procMsg(pMsg);
        }

        //
        test.testCls_Droid.testChk("mainWnd.gui_procMsg.406");

        do {

            //
            CIsTalker talker = null;
            talker = findIsTalker(pMisCnt, addr_logicalPeer_idInfo.ui64Id);
            if (talker==null)break;

            //
            Activity_dlgTalk hTalk = null;
            if (null != (hTalk = guiShare.GuiShare.pf_findTalker.call(pQyMc, pMisCnt, addr_logicalPeer_idInfo))) {
                hTalk.gui_procMsg.call(pMsg);
                break;
            }
            //
            talker.gui_procMsg(pMsg);

            //
        } while(false);


        //
        //
        test.testCls_Droid.testChk("mainWnd.gui_procMsg.417");

        //
        iErr = 0;
        return iErr;
    };


    public static guiShare.PF_gui_dlgTalk_procMsg gui_dlgTalk_procMsg = (CIsTalker talker, Object pMsgParam) -> {
        //
        if (talker == null) return -1;

        int i;
        for  (  i =  0;  i  <  talker.mems.length;i++  ) {
            qmCommFunc_mis.IsTalkUsage mem = talker.mems[i];
            if  (  mem==null) continue;;
            if  (  mem.obj_usr==null)continue;;
            if  (mem.obj_usr instanceof Activity_dlgTalk) {
                Activity_dlgTalk hTalk = (Activity_dlgTalk) mem.obj_usr;
                return hTalk.gui_procMsg.call(pMsgParam);
            }
            //
        }
        //
        if  (  pMsgParam instanceof qnmCommProc_mis.MIS_MSGU) {
            qnmCommProc_mis.MIS_MSGU pMsg = ( qnmCommProc_mis.MIS_MSGU)pMsgParam;
            talker.gui_procMsg(pMsg);
        }
        return  0;
    };



    //
    public int doTask( short usOp )
    {
        int  iErr =  -1;
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo =pQyMc.get_pProcInfo();
        MainActivity mainActivity=this;

        //
        if (!mainActivity.var_common.notifyTaskStatus.bExists_task) return -1;
        int index_taskInfo = mainActivity.var_common.notifyTaskStatus.index_taskInfo;
        qmcTaskInfo.QMC_TASK_INFO pTaskInfo = pProcInfo.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, index_taskInfo, "gui_notify_doCmd");
        if (pTaskInfo == null) return iErr;
        qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
        if (pTaskData == null) return iErr;
        if (pTaskData.msgU.uiType != qnmCommProc_mis.Consts.CONST_misMsgType_task) return iErr;

        //
        qnmCommProc_mis.MSGR_ADDR peer_addr = ((qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU).addr_logicalPeer;

        do {
            //
            CIsTalker talker = findIsTalker(pProcInfo.pMisCnt, peer_addr.idInfo.ui64Id);
            if  (  talker==null)  break;

            dlgTalkProc.DLG_TALK_var m_var = talker.m_var;

            //
            boolean bStay_toAvCall = pQyMc.appParams.bStay_toAvCall;
            //
            doTask_av_accept(this, m_var, bStay_toAvCall);

            //
            iErr =  0;
        } while (false);
        //
        return  iErr;
    }




    //
    public static int tmp_notify_avCall_ifBackgrounded() {
        // int iErr = -1;
        // ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        //  MainActivity mainActivity = (MainActivity)pQyMc.gui.hMainWnd;
        Log.d("后台开启通知");
        return 0;
    }

    public static int tmp_notify_cancel_ifBackgrounded() {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
        NotificationManager manager = (NotificationManager) mainActivity.getSystemService(NOTIFICATION_SERVICE);
        manager.cancelAll();
        iErr = 0;
        return iErr;

    }


    public static int tmp_notify_avCall_onlySound() {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
        String title = "Notification";
        String text = "notification text";

        //Intent intent=new Intent(this,)

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mainActivity);
        builder.setSmallIcon(R.drawable.vegetables);
        //builder.setTicker("Notification Ticker");
        //builder.setContentTitle("Notification");
        //builder.setContentText("Notification Text");

        builder.setAutoCancel(false);
        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        builder.setOngoing(true);

        NotificationManager manager = (NotificationManager) mainActivity.getSystemService(NOTIFICATION_SERVICE);
        Notification notification = builder.build();

        notification.flags |= Notification.FLAG_INSISTENT;
        manager.notify(0, notification);

        return 0;

    }

    public static int tmp_notify_avCall_onlySound_cancel() {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

        NotificationManager manager = (NotificationManager) mainActivity.getSystemService(NOTIFICATION_SERVICE);
        manager.cancelAll();
        return 0;
    }


    static class CTmpDismiss implements CookieBarDismissListener {
        public void onDismiss(int p0) {
            //throw new NotImplementedException();
            switch (p0) {
                case DURATION_COMPLETE:
                case USER_DISMISS:
                    gui_notify_doCmd.call(CONST_imOp_recv_cancel);
                    break;
                case USER_ACTION_CLICK:
                    break;
                default:
                    break;
            }
        }
    }

    static class TmpCustomViewInitializer implements CookieBar.CustomViewInitializer {
        class Tmp_OnClickListener implements View.OnClickListener {
            public void onClick(View v) {
                ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
                if(pQyMc==null) return;
                MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

                if (v.getId() == R.id.btn_accept) {
                    //
                    showInfo_open.qyFuncs.showInfo_open("CONST_imOp_recv_accept");
                    gui_notify_doCmd.call(CONST_imOp_recv_accept);
                } else if (v.getId() == R.id.btn_cancel) {
                    //
                    gui_notify_doCmd.call(CONST_imOp_recv_cancel);
                }
            }
        }

        @Override
        public void initView(View p0) {
            Button btnAccept = (Button) p0.findViewById(R.id.btn_accept);
            Button btnCancel = (Button) p0.findViewById(R.id.btn_cancel);
            //
            ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
            if (pQyMc == null) {
                Log.e("pQyMc == null");
                return;
            }
            btnAccept.setText(R.string.CONST_resId_accept);
            btnCancel.setText(R.string.CONST_resId_cancel);

            Tmp_OnClickListener btn_listener = new Tmp_OnClickListener();
            Tmp_OnClickListener btn_listener1 = new Tmp_OnClickListener();

            btnAccept.setOnClickListener(btn_listener);
            //
            btnCancel.setOnClickListener(btn_listener1);

            return;
        }
    }

    //
    public static Pair<String,String> getCallerDesc() {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (pQyMc == null) return null;
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
        if (mainActivity == null) return null;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        //
        qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, pProcInfo, mainActivity.var_common.notifyTaskStatus.index_taskInfo, "");
        if (pTaskInfo == null) return null;

        qmcTaskInfo.QMC_taskData_common taskData = pTaskInfo.getTaskData();
        if (taskData == null) return null;
        if (taskData.uiType != qmcTaskInfo.Consts.CONST_taskDataType_conf) return null;
        qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) taskData;
        if (pTc.msgU.uiType != qnmCommProc_mis.Consts.CONST_misMsgType_task) return null;
        qyDefs.QY_MESSENGER_ID idInfo_logicalPeer = ((qnmCommProc_mis.MIS_MSG_TASK) pTc.msgU).addr_logicalPeer.idInfo;

        int uiObjType = 0;
        String grpName = "";
        long idInfo_creator = 0;

        Pair<Integer,Object> pair = null;
        if (null ==(pair = getTalkerDesc.qyFuncs.getTalkerDesc(idInfo_logicalPeer)))
            return null;

        uiObjType = pair.getValue0();


        String title = "";

        if (uiObjType == qyDefs.Consts.CONST_objType_imGrp) {
            qnmCommProc_mis.IM_GRP_INFO grpInfo = (qnmCommProc_mis.IM_GRP_INFO) pair.getValue1();
            title = grpInfo.name;
        } else {
            qnmCommProc_mis.QY_MESSENGER_REGINFO reginfo = (qnmCommProc_mis.QY_MESSENGER_REGINFO) pair.getValue1();
            String dw = reginfo.dw;
            String bm = reginfo.bm;
            String syr = reginfo.syr;
            //title = dw + bm + syr;
            title = syr;
        }

        String desc = " ";//""视频通话";
        return new Pair<>(title,desc);
    }

    private static WeakReference<CookieBar> cookieBar;

    /** 当前 notify 任务对应的 logicalPeer id（组 id 或用户 id）。 */
    @Nullable
    public static Long getNotifyAvLogicalPeerId() {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (pQyMc == null) return null;
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
        if (mainActivity == null || mainActivity.var_common == null
                || mainActivity.var_common.notifyTaskStatus == null
                || !mainActivity.var_common.notifyTaskStatus.bExists_task) {
            return null;
        }
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if (pProcInfo == null) return null;
        qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(
                null, pProcInfo, mainActivity.var_common.notifyTaskStatus.index_taskInfo, "");
        if (pTaskInfo == null) return null;
        qmcTaskInfo.QMC_taskData_common taskData = pTaskInfo.getTaskData();
        if (taskData == null || taskData.uiType != qmcTaskInfo.Consts.CONST_taskDataType_conf) return null;
        if (!(taskData.msgU instanceof qnmCommProc_mis.MIS_MSG_TASK)) return null;
        qnmCommProc_mis.MIS_MSG_TASK task = (qnmCommProc_mis.MIS_MSG_TASK) taskData.msgU;
        if (task.addr_logicalPeer == null || task.addr_logicalPeer.idInfo == null) return null;
        return task.addr_logicalPeer.idInfo.ui64Id;
    }

    public static boolean shouldSuppressAvNotifyUi(@NonNull MainActivity mainActivity) {
        if (mainActivity.suppressAvNotifyUiForGroupId <= 0) return false;
        Long peerId = getNotifyAvLogicalPeerId();
        return peerId != null && peerId == mainActivity.suppressAvNotifyUiForGroupId;
    }

    //
    public static int tmp_notify_avCall_ifForegrounded(Activity hParent) {
        if(hParent == null)
        {
            Log.e("hParent == null");
            return -1;
        }
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if(pQyMc == null)
        {
            Log.e("pQyMc == null");
            return -1;
        }
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if(pProcInfo == null)
        {
            Log.e("pProcInfo==null");
            return -1;
        }
        if (!((MainActivity) pQyMc.gui.hMainWnd).canReceiveMessage) {
            return -1;
        }
        Log.d("响铃通知avCall");
        if (null != ((MyApplication) (hParent.getApplication())).mediaPlayer) {

            Log.d("播放铃声");
            ((MyApplication) (hParent.getApplication())).mediaPlayer.start();


        }
        if (cookieBar != null) {
            cookieBar = null;
        }

        CookieBar.dismiss(hParent);
        CookieBar.Builder builder = CookieBar.build(hParent);
        builder.setCustomView(R.layout.notify_avcall);
        builder.setCustomViewInitializer(new TmpCustomViewInitializer());

        //
        Log.d("qy","after builder.setCustomViewInitializer");

        //
        String title  =  "";
        String desc  =  "";
        {
            Pair<String, String> pair = getCallerDesc();
            if (pair == null) {
                Log.e("qy", "err: getCallerDesc returns null");
            }
            if (pair != null) {
                title = pair.getValue0();
                desc = pair.getValue1();
            }
        }

        //
        builder.setTitle(title);
        builder.setMessage(desc);
        builder.setCookiePosition(CookieBar.TOP);  // Cookie will be displayed at the bottom
        builder.setDuration(pProcInfo.cfg.usTimeoutInMs_waitForProcessingOf_avTask + 5000);
        builder.setCookieListener(new CTmpDismiss());
        builder.setEnableAutoDismiss(true);
        cookieBar = new WeakReference<>(builder.show());                              // of the screen

        //
        Log.d("qy","after builder.show");

        if (!pProcInfo.cfg.notify_avCall_bNoSound) {
            //
            Log.d("qy","before tmp_notify_avCall_onlySound");

            //
            tmp_notify_avCall_onlySound();
        }


        return 0;
    }

    public static int tmp_notify_cancel_ifForegrounded(Activity activity) {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if(pQyMc == null) return -1;
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
        if(mainActivity == null || activity == null) return -1;
        CookieBar.dismiss(activity);
        if (null != ((MyApplication) (mainActivity.getApplication())).mediaPlayer) {
            Log.d("停止铃声");
            ((MyApplication) (mainActivity.getApplication())).mediaPlayer.pause();
        }
        tmp_notify_avCall_onlySound_cancel();

        return 0;
    }

    public static guiShare.PF_gui_notify_clear gui_notify_clear = () -> {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        //
        if  (  pProcInfo.cfg.debugStatusInfo.bDbg_startToBreak  )  {
            Log.d("at","notify_clear called");
        }

        //
        if (mainActivity.var_common.notifyTaskStatus.curNotify.bShown) {
            //
            if (mainActivity.var_common.notifyTaskStatus.curNotify.bForegrounded)
                tmp_notify_cancel_ifForegrounded((Activity) mainActivity.var_common.notifyTaskStatus.curNotify.hParent.get());
            else tmp_notify_cancel_ifBackgrounded();
            //
            mainActivity.var_common.notifyTaskStatus.curNotify.bShown = false;
        }
        //
        mainActivity.var_common.notifyTaskStatus.bExists_task  =  false;
        mainActivity.var_common.notifyTaskStatus.bAvCall=false;

        //
        return 0;
    };

    //
    public static guiShare.PF_gui_notify_chk gui_notify_chk = () -> {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (pQyMc == null) return -1;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
        //
        Activity hParent = (Activity) pQyMc.gui.hActiveWnd;
        if (null == hParent) hParent = mainActivity;

        //
        if  (  pQyMc.appParams.bDbg)  {
            if  (  pProcInfo.cfg.debugStatusInfo.bDbg_startToBreak)  {
                Log.d("at","kkk");
            }
        }

        //
        boolean bNeedShow = false;
        //
        if (mainActivity.var_common.notifyTaskStatus.bExists_task
                && mainActivity.var_common.notifyTaskStatus.bAvCall
                && qmcTaskInfo.qyFuncs.qmcTaskInfo_bAlive(null, pProcInfo, mainActivity.var_common.notifyTaskStatus.index_taskInfo)
                &&  !bTaskActive(null,mainActivity.var_common.notifyTaskStatus.iTaskId)
        ) {
            bNeedShow = true;
        }
        Log.d("gui_notify_chk enters, bNeedShow :" + bNeedShow);
        //
        if (!bNeedShow) {
            if (mainActivity.var_common.notifyTaskStatus.curNotify.bShown) {
                Log.d("gui_notify_cancel(); 852");
                gui_notify_clear.call();
            }
            iErr = 0;
            return iErr;
        }
        //
        if (mainActivity.var_common.notifyTaskStatus.curNotify.bShown) {
            //
            iErr = 0;
            return iErr;
        }
        //
        if (shouldSuppressAvNotifyUi(mainActivity)) {
            Log.d("MAP_AV_DBG", "gui_notify_chk suppressed for map group="
                    + mainActivity.suppressAvNotifyUiForGroupId);
            if (mainActivity.var_common.notifyTaskStatus.curNotify.bShown) {
                tmp_notify_cancel_ifForegrounded(hParent);
                mainActivity.var_common.notifyTaskStatus.curNotify.bShown = false;
            }
            iErr = 0;
            return iErr;
        }
        //
        mainActivity.var_common.notifyTaskStatus.curNotify.bForegrounded = pQyMc.gui.bForeground;
        mainActivity.var_common.notifyTaskStatus.curNotify.hParent = new WeakReference<>(hParent);

        //
        if (mainActivity.var_common.notifyTaskStatus.curNotify.bForegrounded) {
            tmp_notify_avCall_ifForegrounded(hParent);
        }
        else {
             tmp_notify_avCall_ifBackgrounded();
        }
        //
        mainActivity.var_common.notifyTaskStatus.curNotify.bShown = true;

        //
        iErr = 0;
        return iErr;
    };


    public static guiShare.PF_gui_notify_doCmd gui_notify_doCmd = (int usOp) -> {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

        //
        Log.d("gui_notify_doCmd: usOp " + usOp);

        //
        if (!mainActivity.var_common.notifyTaskStatus.bExists_task) return -1;
        int index_taskInfo = mainActivity.var_common.notifyTaskStatus.index_taskInfo;
        qmcTaskInfo.QMC_TASK_INFO pTaskInfo = pProcInfo.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, index_taskInfo, "gui_notify_doCmd");
        if (pTaskInfo == null) return iErr;
        qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
        if (pTaskData == null) return iErr;
        if (pTaskData.msgU.uiType != qnmCommProc_mis.Consts.CONST_misMsgType_task) return iErr;

        //
        qnmCommProc_mis.MSGR_ADDR peer_addr = ((qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU).addr_logicalPeer;

        //
        switch (usOp) {
            case CONST_imOp_recv_cancel:
                mainWnd_procMsg.qyFuncs_Droid.do_cancelTask1(pTaskInfo.var.iTaskId, false, "gui_notify_doCmd");
                mainActivity.var_common.notifyTaskStatus.bExists_task = false;

                gui_notify_chk.call();

                break;
            case CONST_imOp_recv_accept: {

                Log.d("at","收到会议通知Consts.CONST_imOp_recv_accept");

                test.testCls_Droid.testChk("after 收到会议通知");

                //
                if  (  true  ) {
                    //
                    audioRecorder_android.qyFuncs_Droid.setMicrophoneOn(true);

                    //
                    Activity_dlgTalk hTalk = null;
                    if ((hTalk = guiShare.GuiShare.pf_findTalker.call(pQyMc, pMisCnt, peer_addr.idInfo)) != null
                            && dlgTalkProc_help.qyFuncs_Droid.bDlgTalkActive(hTalk)) {
                        Log.d("GuiShare.pf_findTalker.call(pQyMc, pMisCnt, addr.idInfo,  hTalk) == 0 && qyFuncs_Droid.bDlgTalkActive(hTalk) 为ture");

                        ((MyApplication) (mainActivity.getApplication())).mediaPlayer.pause();
                        hTalk.gui_PostMessage(qyWmComm.Consts.CONST_qyWm_postComm, qyWmComm.Consts.CONST_qyWmParam_doTask, usOp);

                        //
                        break;
                    }

                    //
                    boolean bStay_toAvCall = pQyMc.appParams.bStay_toAvCall;
                    //
                    isCliHelpPublic.PARAM_talkToMessenger param = new isCliHelpPublic.PARAM_talkToMessenger();
                    param.wParam = qyWmComm.Consts.CONST_qyWmParam_doTask;
                    param.lParam = usOp;

                    //
                    dlgTalkProc_help.qyFuncs_Droid.talkToMessenger(pQyMc, peer_addr.idInfo.ui64Id, false, true, bStay_toAvCall, param);

                    //
                    if (pQyMc.appParams.bDbg) {
                        pProcInfo.cfg.debugStatusInfo.bDbg_startToBreak = true;
                    }

                }

            }
            break;

            default:
                break;
        }

        iErr = 0;
        return iErr;

    };

    //
    long m_test_talker_ui64Id = 107;//101;//107;


    //
    public qyDefs.PF_commonHandler gui_onTimer = (Object p0, Object p1, Object p2) -> {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if  (  pProcInfo==null)  {
            return -1;
        }
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

        //
        qyMcMainWndProc_isCli.qyFuncs_Droid.mainWnd_OnTimer(mainActivity, mainActivity.var_common, 0);

        //
        if (pProcInfo.bEnd_threadProc_isCli) {
            Log.d("gui_onTimer: threadProc_isCli_end is true, trigger to doClean");

            //
            this.gui_PostMessage.call(this, qyWmComm.Consts.CONST_qyWm_postComm, qyWmComm.Consts.CONST_qyWmParam_threadProc_isCli_end, 0);
        }

        //                
        mAdapter.mFragment_msg1.gui_onTimer(null, null, null);
        mAdapter.mFragment_imObj1.gui_onTimer(null, null, null);
        mAdapter.mFragment_me.gui_onTimer(null, null, null);

        //
        com.qycx.base.QYThreadCtx.chkCtxThreadTmpVar();

        //
        long tickCnt=myGetTickCount(null);
        if  (  pProcInfo.av.talk.bChannelReady)  {
            long iDiffInMs = tickCnt - pProcInfo.m_lastTickCnt_sendToRefreshRecentFriends;
            if  (  Math.abs(iDiffInMs)  >  10000)  {
                qnmCommProc_mis.REFRESH_RECENTFRIENDS_REQ rrr = new qnmCommProc_mis.REFRESH_RECENTFRIENDS_REQ();
                rrr.uiType = CONST_imCommType_refreshRecentFriendsReq;
                //
                sendToRefreshRecentFriends(rrr,"mainWnd.send to refresh online state");

            }


        }


        //
        //  这里枚举所有的talker, 模拟定时器
        {
            //
            Iterator<qmCommFunc_mis.IsTalkerQMem> it = pMisCnt.isTalkerQ.iterator();

            while (it.hasNext()) {
                qmCommFunc_mis.IsTalkerQMem s = it.next();
                //
                s.m_talker.gui_onTimer(p0,p1,p2);
            }
        }



        //
        //Log.d("at","mainWnd.gui_onTimer: loopCtrl "  +  this.var_common.loopCtrl );
        if  (  pProcInfo.cfg.bTest_floatingView)  {


            //

        }


        //
        if  (  true )  {
            //import static com.qycx.base.test.testCls_Droid.test1;
            com.qycx.base.test.testCls_Droid.test1();

            //
            if  (  false  )  {
                showInfo_open.qyFuncs.showInfo_open("123");
            }

            //
            if  (  mAdapter.mFragment_map !=null )  {
                if (mAdapter.mFragment_map instanceof Fragment_map)  {
                    Fragment_map fg = (Fragment_map) mAdapter.mFragment_map;
                    String str;
                    str = ""  +  m_test_talker_ui64Id  +  ", click me to start an av call";
                    if  (  fg.tv_content!=null) {
                        fg.tv_content.setText(str);
                    }
                }
            }

        }

        //
        if  (  pQyMc.appParams.bDbg)  {
            //
            if  ( pProcInfo.cfg.debugStatusInfo.bDbg_v) {

                //
                int i;
                for (i = 0; i < pProcInfo.cfg.usMaxCnt_taskInfos; i++) {
                    qmcTaskInfo.QMC_TASK_INFO pTaskInfo = pProcInfo.getQmcTaskInfoByIndex(null, i, "");
                    if (!pTaskInfo.bUsed) continue;
                    //
                    int iTaskId = pTaskInfo.var.iTaskId;
                    boolean bActive = bTaskActive(null, iTaskId);
                    //
                    Log.d("at", "task" + i + ": " + iTaskId + " " + (bActive ? "active" : "inactive"));

                }

                //
            }

        }


        //
        iErr = 0;

        return iErr;
    };


    //
    int testClickOnMap()
    {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;

        //  这里要发起对m_test_talker_ui64Id的视频呼叫.
        //
        long talker_ui64Id;
        talker_ui64Id=m_test_talker_ui64Id;

        do {
            //
            boolean bStay_toAvCall = true;
            isCliHelpPublic.PARAM_talkToMessenger param = new isCliHelpPublic.PARAM_talkToMessenger();

            //
            int tmpiRet = -1;
            tmpiRet = talkToMessenger(pQyMc, talker_ui64Id, false, false, bStay_toAvCall, param);
            if  (  tmpiRet !=0)  {
                break;
            }

            //
            act_toAvCall(this, talker_ui64Id);

        } while(false);

        //
        return  0;
    }


    //
    int doTest() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        //ByteBuffer bb=ByteBuffer.allocate(5);

        Log.d("qy","app start");
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        //
        setContentView(R.layout.activity_main);
        mHandler = new MyHandler(this);
        GuiShareProc.qyFuncs_Droid.GuiShare_init(this);
        prepare.qyFuncs_Droid.perpare_init();

        this.var_common = new qyMcMainWndProc.QY_MC_mainWndVar();
        qyFuncs.test();

        //
        if  (  g_pQyMc  ==  null) {
            InitInstance(0, 0);
        }

        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        pQyMc.gui.hMainWnd = this;
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);

        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        //
        MapLocSettings.loadFrom(this);
        pProcInfo.m_pMapExt = ((MyApplication)getApplication()).m_pMapExt;
        pProcInfo.m_pMapExt.m_var.pProcInfo=pProcInfo;

        //
        if(sp.getBoolean(Str_use100k_ifP2p,false)){
            pProcInfo.use100k_ifP2p = true;
        } else{
            pProcInfo.use100k_ifP2p = false;
        }
        if(sp.getBoolean(Str_use100k_bDj,false)) {
            pProcInfo.use100k_bDj=true;
        }
        else  {
              pProcInfo.use100k_bDj=false;
        }
        //
        if  (  pProcInfo.cfg.bSupport_autoLogin) {
            pProcInfo.cfg.bAutoLogin = sp.getBoolean("autoLogin", false);
        }

        //
        if  (  false  ) {
            //
            pProcInfo.bUse100k = true;
            showInfo_open.qyFuncs.showInfo_open("For test: bUse100k set to true");
        }

        //
        if  (  false ) {
            //
            //pProcInfo.bStay_toAvCall = true;
            //showInfo_open.qyFuncs.showInfo_open("test: bStay_toAv set to true");
        }

        //
        if  (  pQyMc.appParams.bDbg)  {
            //
            if  (  false ) {
                pProcInfo.cfg.debugStatusInfo.bDbg_no_freeTaskInfo = true;
                Log.d("at","dbg: bDbg_no_freeTaskInfo set to true");
            }
        }

        //
        mAdapter = new FragmentPagerAdapter_main(getSupportFragmentManager());
        bindViews();

        //
        initVar_onCreate_mainFrame(0, this.var_common);

        //
        String tmpDir;
        Context context  =  getApplicationContext();
        //
        File modelFileRootDir = context.getFilesDir();
        //File modelFileRootDir = context.getExternalFilesDir(null);

        //
        String dstDirName;
        dstDirName=modelFileRootDir.getAbsolutePath();
        //
        ///data/data/com.qycx/files/lyra/model_coeffs/lyragan.tflite
        //dstDirName="/data/data/com.alt.testjni/files/";
        //dstDirName="/data/data/com.alt.testjni/files/";

        //
        String model_file_path = "lyra/model_coeffs/";
        String fn_modelFile =  model_file_path  +  "lyragan.tflite";
        //
        atFuncs.copyFileFromAssets(context,fn_modelFile,dstDirName);
        //
        fn_modelFile =  model_file_path  +  "quantizer.tflite";
        atFuncs.copyFileFromAssets(context,fn_modelFile,dstDirName);
        //
        fn_modelFile =  model_file_path  +  "soundstream_encoder.tflite";
        atFuncs.copyFileFromAssets(context,fn_modelFile,dstDirName);

        fn_modelFile =  model_file_path  +  "lyra_config.binarypb";
        atFuncs.copyFileFromAssets(context,fn_modelFile,dstDirName);


        //
        g_model_file_dir=modelFileRootDir  + "/"  + model_file_path;

        //
        if  (  true  )  {
            guiShare.GuiShare.pf_testInMain.call(pQyMc);
            //test.testCls_Droid.testRwLock();

            //
            String str;
            str  = "";
            //
            if  (  true ) {
                //str = TmpLyra.sayHello();
                //
                //str = TmpLyra.sayHello1("kk");
                //
                Log.d("anc","sayHello1 returns "  +  str);
            }
            //

            //
            android.util.Log.d("kk","nnn");


        }

        //
        initFloatingAvShell();

        //
        this.m_str_title = getString(R.string.CONST_resId_appName);
        registerMapLocScreenOffReceiver();
        startActivity_login();
    }

    private void registerMapLocScreenOffReceiver() {
        if (screenOffReceiver != null) {
            return;
        }
        screenOffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null && Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                    clearMapLocKeywordAndRemoteLocations();
                }
            }
        };
        registerReceiver(screenOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    private void unregisterMapLocScreenOffReceiver() {
        if (screenOffReceiver == null) {
            return;
        }
        try {
            unregisterReceiver(screenOffReceiver);
        } catch (Exception ignored) {
        }
        screenOffReceiver = null;
    }

    /** 锁屏、退后台、登出时清除定位口令；加密开启时同时清除地图他人位置。 */
    public void clearMapLocKeywordAndRemoteLocations() {
        MapLocKeyword.clearOnLogoff();
        if (MapLocSettings.isEncryptionEnabled()) {
            clearMapRemoteLocations();
        }
        locKeywordDialogShowing = false;
    }

    /** 进入地图且尚未输入口令时弹窗；加密关闭时不提示。 */
    public void requestMapLocKeywordIfNeeded() {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (pQyMc == null || !pQyMc.bLogon || isFinishing()) {
            return;
        }
        if (!MapLocSettings.isEncryptionEnabled()) {
            return;
        }
        if (MapLocKeyword.canDecryptOthers() || locKeywordDialogShowing) {
            return;
        }
        showLocKeywordDialog();
    }

    /** 离开地图 Tab：不清口令（锁屏/退后台/登出时仍会清除）。 */
    public void onMapTabHidden() {
    }

    /** 进入地图 Tab：加密开启且未输入口令时弹窗。 */
    public void onMapTabSelected() {
        requestMapLocKeywordIfNeeded();
    }

    /** 口令输入成功后：立即上报本机位置，便于组员解密显示。 */
    public void notifyMapLocKeywordReady() {
        if (mAdapter == null || !(mAdapter.mFragment_map instanceof Fragment_map)) {
            return;
        }
        ((Fragment_map) mAdapter.mFragment_map).flushSelfLocationToGroupNow();
    }

    private void handleMapTabPagerChange(int position) {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (pQyMc == null || !pQyMc.appParams.bENABLE_MAP_TAB) {
            lastPagerPosition = position;
            return;
        }
        if (lastPagerPosition == PAGE_FOUR && position != PAGE_FOUR) {
            onMapTabHidden();
        }
        if (position == PAGE_FOUR) {
            onMapTabSelected();
        }
        lastPagerPosition = position;
    }

    private void showTipNotification() {
        Pair<String, String> pair = getCallerDesc();
        if(pair == null) return;
        String title = pair.getValue0();
        String desc = pair.getValue1();
        var builder = new NotificationCompat.Builder(this, "清扬通信")
                .setContentTitle(title)
                .setContentText(desc)
                .setSmallIcon(R.drawable.notify_avcall)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notify_avcall))
                .setTimeoutAfter(30000)
                .setAutoCancel(true)
                .setDeleteIntent(deletePendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(builderAccept)
                //       .setContentIntent(reorderToFrontIntent)
                .addAction(builderCancel);

        var notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(notificationAvCallId, builder.build());

    }

    private AcceptReceiver acceptReceiver = new AcceptReceiver();
    private CancelReceiver cancelReceiver = new CancelReceiver();
    //
    public boolean isActive;
    //
    public boolean canReceiveMessage = true;
    private int notificationAvCallId = 0;
    private PendingIntent acceptPendingIntent;
    private PendingIntent cancelPendingIntent;
    private PendingIntent deletePendingIntent;
    //   private PendingIntent reorderToFrontIntent;
    private NotificationCompat.Action builderAccept;
    private NotificationCompat.Action builderCancel;
    private static final int REQUEST_CODE_POST_LOGIN_PERMISSIONS = 7921;
    private boolean requestedPostLoginPermissions;

    /** 登录成功后一次性申请相机、麦克风、定位等运行时权限。 */
    private void requestPostLoginPermissionsIfNeeded() {
        if (requestedPostLoginPermissions) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        ArrayList<String> missing = new ArrayList<>(5);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            missing.add(Manifest.permission.CAMERA);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            missing.add(Manifest.permission.RECORD_AUDIO);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            missing.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            missing.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            missing.add(Manifest.permission.POST_NOTIFICATIONS);
        }
        if (missing.isEmpty()) {
            return;
        }
        requestedPostLoginPermissions = true;
        ActivityCompat.requestPermissions(
                this,
                missing.toArray(new String[0]),
                REQUEST_CODE_POST_LOGIN_PERMISSIONS);
    }

    private void bindNotificationIntent() {
        var acceptIntent = new Intent(this, AcceptIntentService.class);
        acceptIntent.putExtra("notificationId", notificationAvCallId);
        acceptPendingIntent = PendingIntent.getService(this, 0, acceptIntent, PendingIntent.FLAG_IMMUTABLE);

        var cancelIntent = new Intent(this, CancelIntentService.class);
        cancelIntent.putExtra("notificationId", notificationAvCallId);

        cancelPendingIntent = PendingIntent.getService(this, 0, cancelIntent, PendingIntent.FLAG_IMMUTABLE);

        var deleteIntent = new Intent(this, NotificationDismissedReceiver.class);
        deleteIntent.putExtra("notificationId", notificationAvCallId);

        deletePendingIntent = PendingIntent.getBroadcast(this, 0, deleteIntent, PendingIntent.FLAG_IMMUTABLE);

        builderAccept = new NotificationCompat.Action.Builder(R.drawable.icon, "接受", acceptPendingIntent).build();
        builderCancel = new NotificationCompat.Action.Builder(R.drawable.icon, "取消", cancelPendingIntent).build();


        // reorderToFrontIntent = PendingIntent.GetActivity(this, 0, intent, 0);

    }

    ////////////////////////////

    private FrameLayout m_floatingAvShell;
    /** 内嵌 {@link Fragment_dlgTalk_av2} 的容器 */
    private FrameLayout m_floatingView;
    private View m_floatingAvTitleBar;
    private TextView m_btnFloatingAvFloat;
    private TextView m_btnFloatingAvSplit;
    private TextView m_btnFloatingAvFull;
    private TextView m_btnFloatingAvConfMem;
    //
    private float fv_lastX, fv_lastY;
    private float fv_downX, fv_downY;
    //
    public Fragment     m_floatingView_fg;

    private static final int FLOATING_AV_MODE_FLOAT = 0;
    private static final int FLOATING_AV_MODE_SPLIT = 1;
    private static final int FLOATING_AV_MODE_FULL = 2;
    private static final float FLOATING_AV_MAP_HEIGHT_RATIO = 0.52f;
    /** 横屏分屏：视频区宽度占内容区比例，可拖动，范围 30%～55%。 */
    private static final float LANDSCAPE_SPLIT_VIDEO_MIN_RATIO = 0.30f;
    private static final float LANDSCAPE_SPLIT_VIDEO_MAX_RATIO = 0.55f;
    private static final float LANDSCAPE_SPLIT_VIDEO_DEFAULT_RATIO = 0.48f;
    /** 竖屏分屏：视频区高度占内容区比例，可拖动，范围 30%～55%。 */
    private static final float PORTRAIT_SPLIT_VIDEO_MIN_RATIO = 0.30f;
    private static final float PORTRAIT_SPLIT_VIDEO_MAX_RATIO = 0.55f;
    private static final float PORTRAIT_SPLIT_VIDEO_DEFAULT_RATIO = 0.48f;
    /** 地图左侧工具条占位：marginStart 14 + 按钮 42 + 间距 16（与 layout_fg_map 一致）。 */
    private static final float FLOATING_AV_MAP_LEFT_TOOLBAR_DP = 72f;
    private View m_landscapeSplitDivider;
    private View m_portraitSplitDivider;
    private float landscapeSplitVideoWidthRatio = LANDSCAPE_SPLIT_VIDEO_DEFAULT_RATIO;
    private float portraitSplitVideoHeightRatio = PORTRAIT_SPLIT_VIDEO_DEFAULT_RATIO;
    private float splitDividerDragStartRawX;
    private float splitDividerDragStartRawY;
    private int splitDividerDragStartVideoW;
    private int splitDividerDragStartVideoH;
    /** 用户通过浮窗/分屏/全屏按钮选择的模式。 */
    private int floatingAvUserMode = FLOATING_AV_MODE_FLOAT;
    /** 当前实际应用的布局模式（离开地图 Tab 时分屏/全屏会临时降为浮窗）。 */
    private int floatingAvDisplayMode = FLOATING_AV_MODE_FLOAT;
    private boolean floatingAvTabBarHidden;
    private long lastMapFloatingCameraRestartMs;

    /** 地图页发起/加入组视频会议时抑制顶部 CookieBar 来电提示（>0 为组 id）。 */
    public long suppressAvNotifyUiForGroupId = 0;

    /** 地图悬浮视频会议：仅全屏保留点击视频时上下功能区滑动动画；浮窗/分屏为瞬时切换。 */
    public boolean useMapFloatingAvMenuSlideAnimation() {
        if (m_floatingView_fg == null) {
            return true;
        }
        return floatingAvDisplayMode == FLOATING_AV_MODE_FULL;
    }

    /** 浮窗/分屏：上下功能区始终贴顶、贴底，不参与沉浸式滑出。 */
    public boolean isMapFloatingAvMenuDocked() {
        return m_floatingView_fg != null && floatingAvDisplayMode != FLOATING_AV_MODE_FULL;
    }

    /** 地图 Tab 内嵌视频会议：去掉 Fragment 顶部半透明 topMenu 蒙层。 */
    public boolean shouldHideAvTopMenuOverlay() {
        return m_floatingView_fg != null;
    }

    /** 地图内嵌视频会议 UI 是否正在显示，用于拦截重复打开视频会议。 */
    public boolean isMapFloatingAvUiVisible() {
        return m_floatingView_fg != null
                && m_floatingAvShell != null
                && m_floatingAvShell.getVisibility() == View.VISIBLE;
    }

    /** 当前为地图分屏嵌入（左/上视频 + 地图）。 */
    public boolean isMapFloatingAvSplitEmbedded() {
        return m_floatingView_fg != null && floatingAvDisplayMode == FLOATING_AV_MODE_SPLIT;
    }

    /** 视频会议分屏（横屏/竖屏）：地图 overlay 使用分屏字号。 */
    public boolean isMapOverlayTextCompactEnabled() {
        return isMapFloatingAvSplitEmbedded();
    }

    /** 分屏 overlay 字号资源：竖屏 10sp，横屏 15sp。 */
    public int getMapOverlayTextCompactDimenResId() {
        if (isMapFloatingAvSplitEmbedded() && !isLandscapeConfiguration()) {
            return R.dimen.map_overlay_text_sp_split_portrait;
        }
        return R.dimen.map_overlay_text_sp_compact;
    }

    private void refreshMapFragmentOverlayTextScale() {
        if (mAdapter == null || !(mAdapter.mFragment_map instanceof Fragment_map)) {
            return;
        }
        Fragment_map mapFg = (Fragment_map) mAdapter.mFragment_map;
        Runnable apply = () -> {
            if (mapFg.isAdded()) {
                mapFg.refreshMapOverlayTextScale();
            }
        };
        if (mapFg.isAdded()) {
            apply.run();
            if (vPager != null) {
                vPager.post(apply);
                vPager.postDelayed(apply, 250);
            }
        } else if (vPager != null) {
            vPager.postDelayed(apply, 150);
        }
    }


    /////////////////////////


    //
    int requestCode_qmcLogin = 7829;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        do {
            if (requestCode == requestCode_qmcLogin) {
                ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
                ctxQmc.CCtxQmc  pProcInfo = pQyMc.get_pProcInfo();
                qmCommFunc_mis.MIS_CNT pMisCnt =pProcInfo.pMisCnt;

                //
                if (!pQyMc.bLogon) {
                    Log.d("OnActivityResult: not login, so will quit. resultCode " + resultCode);
                    clearMapLocKeywordAndRemoteLocations();
                    guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, qyWmComm.Consts.CONST_qyWm_postComm, qyWmComm.Consts.CONST_qyWmParam_login_canceled, 0);
                    break;
                }
                requestPostLoginPermissionsIfNeeded();

            }
        } while ( false  );

        //
        return;
    }



    //
    public int makeFloatingView(long talker_ui64Id, int  iCmd,int  index_taskInfo,   int  level  )
    {
        int  iErr =  -1;
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc  pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt =pProcInfo.pMisCnt;

        //
        if  (  !bDone_retrieveAllImObjRules(pProcInfo.pMisCnt )  ) {
            Log.d("MAP_AV_DBG", "makeFloatingView failed: im obj rules not ready");
            return -1;
        }

        //
        do {

            //
            /*
            if  (  false  ) {
                if (!pProcInfo.cfg.bTest_floatingView) {
                    Log.d("MAP_AV_DBG", "makeFloatingView failed: bTest_floatingView false");
                    return -1;
                }
            }
            */

            //
            String str;
            str  =  "makeFloatingView called "  +  talker_ui64Id;
            showInfo_open.qyFuncs.showInfo_open(0,0,"fv",str );

            //
            if (this.m_floatingView_fg != null) {
                closeFloatingView();
            }

            //
            if (this.m_bReadyToQuit) {
                return -1;
            }

            if (index_taskInfo >= 0
                    && !qmcTaskInfo.qyFuncs.qmcTaskInfo_bAlive(null, pProcInfo, index_taskInfo)) {
                Log.d("MAP_AV_DBG", "makeFloatingView failed: task not alive, idx=" + index_taskInfo);
                return -1;
            }

            ensureMapTabForFloatingAv(pQyMc);

            //
            if (m_floatingAvShell.getVisibility() != View.VISIBLE) {
                m_floatingAvShell.setVisibility(View.VISIBLE);
            }
            applyFloatingAvDisplayMode(getDefaultFloatingAvModeForOrientation());

            //
            Fragment fg;

            //
            //long ui64Id = 107;
            //ui64Id=1269;

            //
            //
            //
            CIsTalker talker = null;
            talker = findIsTalker(pMisCnt, talker_ui64Id);
            if (talker == null) {
                Log.d("MAP_AV_DBG", "makeFloatingView failed: talker not found, id=" + talker_ui64Id);
                break;

            }

            //
            clearMapFloatingTalkUsage(talker);
            if (0 != talker.addIsTalkUsage(pMisCnt, this, 0)) {
                Log.d("MAP_AV_DBG", "makeFloatingView failed: addIsTalkUsage, id=" + talker_ui64Id);
                break;
            }
            //
            //
            if (0 != addTo_tmpTools(this, talker.m_var, "makeFloatingView")) {
                Log.d("MAP_AV_DBG", "makeFloatingView failed: addTo_tmpTools, id=" + talker_ui64Id);
                break;
            }

            //
            dlgTalkProc.DLG_TALK_var pMgrVar = talker.m_var;
            //
            /*
            int  iCmd =  tmpResource.IDC_BUTTON_av;
            int  index_task = -1;
            int  level  =  pProcInfo.av.talk_doAv_info.usAvLevel;
             */

            //  下面相当于生成dlgTalk_av
            //
            pMgrVar.m_talkAvVar.reset();
            //  这里有重要的参数需要设置
            pMgrVar.m_talkAvVar.m_iCmd = iCmd;
            pMgrVar.m_talkAvVar.m_Level = level;
            pMgrVar.m_talkAvVar.m_index_taskInfo1 = index_taskInfo;
            pMgrVar.m_bNeedResumeTaskAv = false;
            if (pMgrVar.av != null) {
                pMgrVar.av.bNeedDoTaskAv = true;
                pProcInfo.av.localAv.dwLastTickCnt_vCap = tmpFunc.qyFuncs.myGetTickCount(null);
                pProcInfo.av.localAv.dwLastTickCnt_aCap = pProcInfo.av.localAv.dwLastTickCnt_vCap;
            }

            //
            fg = Fragment_dlgTalk_av2.NewInstance(talker_ui64Id);
            if (fg == null) {
                Log.d("MAP_AV_DBG", "makeFloatingView failed: NewInstance null, id=" + talker_ui64Id);
                break;
            }

            //
            this.m_floatingView_fg = fg;

            // 添加悬浮窗 Fragment77（同步提交，避免连续关闭/打开竞态）
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.floating_fragment_container, fg)
                    .commitNowAllowingStateLoss();

            // 入会时须在 Fragment 就绪后应用浮窗布局（此前 sync 因 m_floatingView_fg 为空会跳过，分屏关闭后再开会仍残留分屏 shell 尺寸）
            syncFloatingAvLayoutForCurrentTab();

            //
            initFloatingAvDrag();

            //
            //talk_toAv(ui64Id, this);

            //
            iErr = 0;

        } while (false);


        return  iErr;
    }


    //
    public int closeFloatingView()
    {
        //
        showInfo_open.qyFuncs.showInfo_open(0,0,"fv","closeFloatingView called");

        //
        if  (  this.m_floatingView_fg!=null)  {

            // 先移除悬浮窗 Fragment（onDestroyView 内会 exitTaskAv，避免重复取消）
            getSupportFragmentManager().beginTransaction()
                    .remove( this.m_floatingView_fg)
                    .commitNowAllowingStateLoss();

            //
            if  (  this.m_floatingView_fg instanceof Fragment_dlgTalk_av2) {
                Fragment_dlgTalk_av2 fg  = (Fragment_dlgTalk_av2)this.m_floatingView_fg;

                //
                resetMapFloatingAutoSpeakState(fg);
                removeFrom_tmpTools(this,fg.m_talker.m_var, "closeFloatingView" );
                if (fg.m_talker != null) {
                    clearMapFloatingTalkUsage(fg.m_talker);
                }

            }

            //
            this.m_floatingView_fg=null;

            //
            restoreMainContentAfterFloatingAv();
            if (m_floatingAvShell != null) {
                m_floatingAvShell.setVisibility(View.GONE);
            }

        }

        return  0;
    }

    /**
     * 地图内嵌视频关闭时只释放 MainActivity 的 usage 槽位。
     * CIsTalker.removeIsTalkUsage() 在最后一个 usage 被清空时会移除 talker，
     * 组会议再次进入还需要这个 talker，因此这里不能调用它。
     */
    private void clearMapFloatingTalkUsage(@Nullable CIsTalker talker) {
        if (talker == null || talker.mems == null) {
            return;
        }
        for (qmCommFunc_mis.IsTalkUsage mem : talker.mems) {
            if (mem != null && mem.obj_usr == this) {
                mem.obj_usr = null;
                mem.obj_usr_iType = 0;
            }
        }
    }

    /** 关闭地图视频窗时清掉上一次发言意图，避免下次进入被 chkAutoSpeak 自动申请发言。 */
    private void resetMapFloatingAutoSpeakState(@Nullable Fragment_dlgTalk_av2 fg) {
        if (fg == null || fg.m_talker == null || fg.m_talker.m_var == null) {
            return;
        }
        dlgTalkProc.DLG_TALK_var mVar = fg.m_talker.m_var;
        try {
            if (dlgTalkProc_help.qyFuncs_Droid.dlgTalk_canSpeak(mVar.addr.idInfo.ui64Id)) {
                fg.do_request(false);
            }
        } catch (Exception e) {
            Log.d("MAP_AV_DBG", "resetMapFloatingAutoSpeakState: stop speak ignored " + e);
        }
        if (mVar.av != null) {
            mVar.av.m_bState_canSpeak = false;
        }
    }

    private void initFloatingAvShell() {
        m_floatingAvShell = findViewById(R.id.floating_av_shell);
        m_floatingView = findViewById(R.id.floating_fragment_container);
        m_floatingAvTitleBar = findViewById(R.id.floating_av_title_bar);
        m_btnFloatingAvFloat = findViewById(R.id.btn_floating_av_float);
        m_btnFloatingAvSplit = findViewById(R.id.btn_floating_av_split);
        m_btnFloatingAvFull = findViewById(R.id.btn_floating_av_full);
        m_btnFloatingAvConfMem = findViewById(R.id.btn_floating_av_confmem);
        TextView btnClose = findViewById(R.id.btn_floating_av_close);
        if (m_floatingAvShell != null) {
            m_floatingAvShell.setVisibility(View.GONE);
        }
        if (m_btnFloatingAvFloat != null) {
            m_btnFloatingAvFloat.setOnClickListener(v -> applyFloatingAvDisplayMode(FLOATING_AV_MODE_FLOAT));
        }
        if (m_btnFloatingAvSplit != null) {
            m_btnFloatingAvSplit.setOnClickListener(v -> {
                ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
                if (pQyMc != null) {
                    ensureMapTabForFloatingAv(pQyMc);
                }
                applyFloatingAvDisplayMode(FLOATING_AV_MODE_SPLIT);
            });
        }
        if (m_btnFloatingAvFull != null) {
            m_btnFloatingAvFull.setOnClickListener(v -> {
                ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
                if (pQyMc != null) {
                    ensureMapTabForFloatingAv(pQyMc);
                }
                applyFloatingAvDisplayMode(FLOATING_AV_MODE_FULL);
            });
        }
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> closeFloatingView());
        }
        if (m_btnFloatingAvConfMem != null) {
            m_btnFloatingAvConfMem.setOnClickListener(v -> {
                if (m_floatingView_fg instanceof Fragment_dlgTalk_av2) {
                    Fragment_dlgTalk_av2 fg = (Fragment_dlgTalk_av2) m_floatingView_fg;
                    if (fg.isAdded()) {
                        fg.onTitleBarConfMemBtnClick();
                    }
                }
            });
        }
        initLandscapeSplitDivider();
        initPortraitSplitDivider();
    }

    /** 浮窗/分屏/全屏时中控按钮放在标题栏左侧；是否可见由 Fragment 按主持人/会议类型等条件驱动。 */
    public boolean useTitleBarConfMemBtn() {
        return m_floatingView_fg != null;
    }

    public void setTitleBarConfMemBtnVisible(boolean visible) {
        if (m_btnFloatingAvConfMem == null) {
            return;
        }
        m_btnFloatingAvConfMem.setVisibility(
                visible && useTitleBarConfMemBtn() ? View.VISIBLE : View.GONE);
    }

    private int clampPortraitSplitVideoHeightPx(int embeddedContentH, int requestedH) {
        int minH = Math.max(dpToPx(160), (int) (embeddedContentH * PORTRAIT_SPLIT_VIDEO_MIN_RATIO));
        int maxH = (int) (embeddedContentH * PORTRAIT_SPLIT_VIDEO_MAX_RATIO);
        return Math.max(minH, Math.min(maxH, requestedH));
    }

    private void initPortraitSplitDivider() {
        if (m_floatingAvShell == null) {
            return;
        }
        ViewGroup root = (ViewGroup) m_floatingAvShell.getParent();
        if (root == null || m_portraitSplitDivider != null) {
            return;
        }
        int touchH = dpToPx(30);
        m_portraitSplitDivider = LayoutInflater.from(this)
                .inflate(R.layout.layout_portrait_split_divider, root, false);
        m_portraitSplitDivider.setVisibility(View.GONE);
        FrameLayout.LayoutParams dlp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, touchH);
        dlp.gravity = Gravity.START | Gravity.TOP;
        root.addView(m_portraitSplitDivider, dlp);
        m_portraitSplitDivider.setOnTouchListener((v, event) -> {
            if (floatingAvDisplayMode != FLOATING_AV_MODE_SPLIT || isLandscapeConfiguration()) {
                return false;
            }
            if (m_floatingAvShell == null || vPager == null) {
                return false;
            }
            FrameLayout.LayoutParams shellLp = (FrameLayout.LayoutParams) m_floatingAvShell.getLayoutParams();
            RelativeLayout.LayoutParams vpLp = (RelativeLayout.LayoutParams) vPager.getLayoutParams();
            if (shellLp == null || vpLp == null) {
                return false;
            }
            int embeddedContentH = getEmbeddedContentHeightPx();
            if (embeddedContentH <= 0) {
                return false;
            }
            switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                    ViewParent parent = v.getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    splitDividerDragStartRawY = event.getRawY();
                    splitDividerDragStartVideoH = shellLp.height;
                        return true;
                case MotionEvent.ACTION_MOVE: {
                    int dy = (int) (event.getRawY() - splitDividerDragStartRawY);
                    int newH = clampPortraitSplitVideoHeightPx(embeddedContentH, splitDividerDragStartVideoH + dy);
                    if (newH != shellLp.height) {
                        int contentW = getFloatingAvContentWidthPx();
                        applyPortraitSplitVideoLayout(newH, contentW, embeddedContentH, vpLp, shellLp);
                        vPager.post(this::notifyMapFloatingLayoutChanged);
                    }
                    return true;
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    ViewParent p = v.getParent();
                    if (p != null) {
                        p.requestDisallowInterceptTouchEvent(false);
                    }
                    notifyMapFloatingLayoutChanged();
                    return true;
                default:
                    break;
            }
            return false;
        });
    }

    private void applyPortraitSplitVideoLayout(int videoShellH, int contentW, int embeddedContentH,
            @NonNull RelativeLayout.LayoutParams vpLp,
            @NonNull FrameLayout.LayoutParams shellLp) {
        videoShellH = clampPortraitSplitVideoHeightPx(embeddedContentH, videoShellH);
        portraitSplitVideoHeightRatio = videoShellH / (float) embeddedContentH;

        vpLp.topMargin = videoShellH;
        vpLp.leftMargin = 0;
        vpLp.rightMargin = 0;
        vPager.setVisibility(View.VISIBLE);
        vPager.setLayoutParams(vpLp);

        shellLp.width = contentW;
        shellLp.height = videoShellH;
        shellLp.gravity = Gravity.TOP | Gravity.START;
        m_floatingAvShell.setLayoutParams(shellLp);
        m_floatingAvShell.setX(0f);
        m_floatingAvShell.setY(0f);

        positionPortraitSplitDivider(videoShellH, contentW);
        if (m_floatingAvShell != null) {
            m_floatingAvShell.requestLayout();
        }
        notifyMapFloatingAvShellResized();
    }

    private void positionPortraitSplitDivider(int videoShellH, int contentW) {
        if (m_portraitSplitDivider == null) {
            return;
        }
        FrameLayout.LayoutParams dlp = (FrameLayout.LayoutParams) m_portraitSplitDivider.getLayoutParams();
        if (dlp == null) {
            return;
        }
        int touchH = dlp.height > 0 ? dlp.height : dpToPx(30);
        dlp.width = contentW;
        dlp.gravity = Gravity.START | Gravity.TOP;
        dlp.topMargin = Math.max(0, videoShellH - touchH / 2);
        dlp.setMarginStart(0);
        m_portraitSplitDivider.setLayoutParams(dlp);
    }

    private void updatePortraitSplitDividerVisibility() {
        boolean show = floatingAvDisplayMode == FLOATING_AV_MODE_SPLIT
                && !isLandscapeConfiguration()
                && m_floatingView_fg != null;
        if (!show) {
            if (m_portraitSplitDivider != null) {
                m_portraitSplitDivider.setVisibility(View.GONE);
            }
            return;
        }
        initPortraitSplitDivider();
        if (m_portraitSplitDivider == null || m_floatingAvShell == null) {
            return;
        }
        m_portraitSplitDivider.setVisibility(View.VISIBLE);
        ViewGroup parent = (ViewGroup) m_portraitSplitDivider.getParent();
        if (parent != null) {
            parent.bringChildToFront(m_portraitSplitDivider);
        }
        FrameLayout.LayoutParams shellLp = (FrameLayout.LayoutParams) m_floatingAvShell.getLayoutParams();
        if (shellLp != null && shellLp.height > 0) {
            positionPortraitSplitDivider(shellLp.height, getFloatingAvContentWidthPx());
        }
    }

    private void updateSplitDividersVisibility() {
        updateLandscapeSplitDividerVisibility();
        updatePortraitSplitDividerVisibility();
    }

    private boolean isLandscapeConfiguration() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    private int clampLandscapeSplitVideoWidthPx(int contentW, int requestedW) {
        int minW = Math.max(dpToPx(120), (int) (contentW * LANDSCAPE_SPLIT_VIDEO_MIN_RATIO));
        int maxW = (int) (contentW * LANDSCAPE_SPLIT_VIDEO_MAX_RATIO);
        return Math.max(minW, Math.min(maxW, requestedW));
    }

    private void initLandscapeSplitDivider() {
        if (m_floatingAvShell == null) {
            return;
        }
        ViewGroup root = (ViewGroup) m_floatingAvShell.getParent();
        if (root == null) {
            return;
        }
        if (m_landscapeSplitDivider != null) {
            return;
        }
        int touchW = dpToPx(30);
        m_landscapeSplitDivider = LayoutInflater.from(this)
                .inflate(R.layout.layout_landscape_split_divider, root, false);
        m_landscapeSplitDivider.setVisibility(View.GONE);
        FrameLayout.LayoutParams dlp = new FrameLayout.LayoutParams(touchW, FrameLayout.LayoutParams.WRAP_CONTENT);
        dlp.gravity = Gravity.START | Gravity.TOP;
        root.addView(m_landscapeSplitDivider, dlp);
        m_landscapeSplitDivider.setOnTouchListener((v, event) -> {
            if (floatingAvDisplayMode != FLOATING_AV_MODE_SPLIT || !isLandscapeConfiguration()) {
                return false;
            }
            if (m_floatingAvShell == null || vPager == null) {
                return false;
            }
            FrameLayout.LayoutParams shellLp = (FrameLayout.LayoutParams) m_floatingAvShell.getLayoutParams();
            RelativeLayout.LayoutParams vpLp = (RelativeLayout.LayoutParams) vPager.getLayoutParams();
            if (shellLp == null || vpLp == null) {
                return false;
            }
            int contentW = getFloatingAvContentWidthPx();
            if (contentW <= 0) {
                return false;
            }
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    ViewParent parent = v.getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    splitDividerDragStartRawX = event.getRawX();
                    splitDividerDragStartVideoW = shellLp.width;
                    return true;
                case MotionEvent.ACTION_MOVE: {
                    int dx = (int) (event.getRawX() - splitDividerDragStartRawX);
                    int newW = clampLandscapeSplitVideoWidthPx(contentW, splitDividerDragStartVideoW + dx);
                    if (newW != shellLp.width) {
                        applyLandscapeSplitVideoLayout(newW, contentW, getEmbeddedContentHeightPx(), vpLp, shellLp);
                        vPager.post(this::notifyMapFloatingLayoutChanged);
                    }
                    return true;
                }
                    case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    ViewParent p = v.getParent();
                    if (p != null) {
                        p.requestDisallowInterceptTouchEvent(false);
                    }
                    notifyMapFloatingLayoutChanged();
                        return true;
                default:
                    break;
            }
            return false;
        });
    }

    private void applyLandscapeSplitVideoLayout(int videoShellW, int contentW, int embeddedContentH,
            @NonNull RelativeLayout.LayoutParams vpLp,
            @NonNull FrameLayout.LayoutParams shellLp) {
        videoShellW = clampLandscapeSplitVideoWidthPx(contentW, videoShellW);
        landscapeSplitVideoWidthRatio = videoShellW / (float) contentW;

        vpLp.leftMargin = videoShellW;
        vpLp.topMargin = 0;
        vPager.setVisibility(View.VISIBLE);
        vPager.setLayoutParams(vpLp);

        shellLp.width = videoShellW;
        shellLp.height = embeddedContentH;
        shellLp.gravity = Gravity.START | Gravity.TOP;
        m_floatingAvShell.setLayoutParams(shellLp);
        m_floatingAvShell.setX(0f);
        m_floatingAvShell.setY(0f);

        positionLandscapeSplitDivider(videoShellW, embeddedContentH);
        if (m_floatingAvShell != null) {
            m_floatingAvShell.requestLayout();
        }
        notifyMapFloatingAvShellResized();
    }

    private void positionLandscapeSplitDivider(int videoShellW, int embeddedContentH) {
        if (m_landscapeSplitDivider == null) {
            return;
        }
        FrameLayout.LayoutParams dlp = (FrameLayout.LayoutParams) m_landscapeSplitDivider.getLayoutParams();
        if (dlp == null) {
            return;
        }
        int touchW = dlp.width > 0 ? dlp.width : dpToPx(30);
        dlp.height = embeddedContentH;
        dlp.gravity = Gravity.START | Gravity.TOP;
        dlp.setMarginStart(Math.max(0, videoShellW - touchW / 2));
        dlp.topMargin = 0;
        m_landscapeSplitDivider.setLayoutParams(dlp);
    }

    private void updateLandscapeSplitDividerVisibility() {
        boolean show = floatingAvDisplayMode == FLOATING_AV_MODE_SPLIT
                && isLandscapeConfiguration()
                && m_floatingView_fg != null;
        if (!show) {
            if (m_landscapeSplitDivider != null) {
                m_landscapeSplitDivider.setVisibility(View.GONE);
            }
            return;
        }
        initLandscapeSplitDivider();
        if (m_landscapeSplitDivider == null || m_floatingAvShell == null) {
            return;
        }
        m_landscapeSplitDivider.setVisibility(View.VISIBLE);
        ViewGroup parent = (ViewGroup) m_landscapeSplitDivider.getParent();
        if (parent != null) {
            parent.bringChildToFront(m_landscapeSplitDivider);
        }
        FrameLayout.LayoutParams shellLp = (FrameLayout.LayoutParams) m_floatingAvShell.getLayoutParams();
        if (shellLp != null && shellLp.width > 0) {
            positionLandscapeSplitDivider(shellLp.width, getEmbeddedContentHeightPx());
        }
    }

    private void ensureMapTabForFloatingAv(@NonNull ctxQyMc.CCtxQyMc pQyMc) {
        if (pQyMc.appParams != null && pQyMc.appParams.bENABLE_MAP_TAB && tab_map != null) {
            tab_map.performClick();
        }
    }

    private void setMainTabBarVisible(boolean visible) {
        View lyTab = findViewById(R.id.ly_tab_bar);
        View divTab = findViewById(R.id.div_tab_bar);
        int v = visible ? View.VISIBLE : View.GONE;
        if (lyTab != null) lyTab.setVisibility(v);
        if (divTab != null) divTab.setVisibility(v);
        floatingAvTabBarHidden = !visible;
    }

    private boolean isOnMapTab() {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (pQyMc == null || pQyMc.appParams == null || !pQyMc.appParams.bENABLE_MAP_TAB) {
                return false;
            }
        return vPager != null && vPager.getCurrentItem() == PAGE_FOUR;
    }

    /** 入会/旋转时默认布局：竖屏浮窗，横屏分屏（避免与左侧工具条、活动组条重叠）。 */
    private int getDefaultFloatingAvModeForOrientation() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return FLOATING_AV_MODE_SPLIT;
        }
        return FLOATING_AV_MODE_FLOAT;
    }

    /**
     * 分屏/全屏仅在地图 Tab 嵌入；切到其他 Tab 时临时显示为悬浮窗，回到地图后恢复。
     */
    private int resolveEffectiveFloatingAvMode() {
        if (isOnMapTab()) {
            return floatingAvUserMode;
        }
        if (floatingAvUserMode == FLOATING_AV_MODE_SPLIT
                || floatingAvUserMode == FLOATING_AV_MODE_FULL) {
            return FLOATING_AV_MODE_FLOAT;
        }
        return floatingAvUserMode;
    }

    /** 用户切换浮窗/分屏/全屏，或 Tab/方向变化后调用。 */
    private void applyFloatingAvDisplayMode(int mode) {
        floatingAvUserMode = mode;
        syncFloatingAvLayoutForCurrentTab();
    }

    private void syncFloatingAvLayoutForCurrentTab() {
        if (m_floatingAvShell == null || vPager == null) {
            return;
        }
        if (m_floatingView_fg == null
                && m_floatingAvShell.getVisibility() != View.VISIBLE) {
            return;
        }
        applyFloatingAvLayoutImpl(resolveEffectiveFloatingAvMode());
        m_floatingAvShell.post(this::enforceFloatingAvSafeState);
        m_floatingAvShell.postDelayed(this::enforceFloatingAvSafeState, 120);
        m_floatingAvShell.postDelayed(this::enforceFloatingAvSafeState, 280);
    }

    /**
     * 浮窗 / 分屏（竖屏上视频下地图，横屏左视频右地图）/ 全屏（覆盖整屏）。
     * 仅在地图 Tab 下对分屏/全屏生效。
     */
    private void resetViewPagerForEmbeddedAv(RelativeLayout.LayoutParams vpLp) {
        vpLp.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        vpLp.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        vpLp.topMargin = 0;
        vpLp.leftMargin = 0;
        vpLp.rightMargin = 0;
        vpLp.bottomMargin = 0;
        vpLp.addRule(RelativeLayout.ABOVE, R.id.div_tab_bar);
        vpLp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        vpLp.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        vpLp.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
    }

    private void resetFloatingAvShellFrameParams(FrameLayout.LayoutParams shellLp) {
        shellLp.gravity = Gravity.START | Gravity.TOP;
        shellLp.setMargins(0, 0, 0, 0);
    }

    private void applyFloatingAvLayoutImpl(int mode) {
        if (m_floatingAvShell == null || vPager == null) return;
        floatingAvDisplayMode = mode;
        m_floatingAvShell.setTranslationX(0f);
        m_floatingAvShell.setTranslationY(0f);
        m_floatingAvShell.bringToFront();

        int contentH = getFloatingAvContentHeightPx();
        int embeddedContentH = getEmbeddedContentHeightPx();
        int contentW = getFloatingAvContentWidthPx();
        if ((contentH <= 0 || contentW <= 0) && mode != FLOATING_AV_MODE_FULL) {
            m_floatingAvShell.post(() -> applyFloatingAvLayoutImpl(mode));
            return;
        }
        RelativeLayout.LayoutParams vpLp = (RelativeLayout.LayoutParams) vPager.getLayoutParams();
        FrameLayout.LayoutParams shellLp = (FrameLayout.LayoutParams) m_floatingAvShell.getLayoutParams();
        if (vpLp == null || shellLp == null) return;

        setMainTabBarVisible(true);
        resetViewPagerForEmbeddedAv(vpLp);
        resetFloatingAvShellFrameParams(shellLp);
        if (mode != FLOATING_AV_MODE_FULL) {
            clearFloatingAvShellSystemInsets();
        }
        switch (mode) {
            case FLOATING_AV_MODE_SPLIT: {
                boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
                if (isLandscape) {
                    int videoShellW = clampLandscapeSplitVideoWidthPx(contentW,
                            (int) (contentW * landscapeSplitVideoWidthRatio));
                    applyLandscapeSplitVideoLayout(videoShellW, contentW, embeddedContentH, vpLp, shellLp);
                } else {
                    int videoShellH = clampPortraitSplitVideoHeightPx(embeddedContentH,
                            (int) (embeddedContentH * portraitSplitVideoHeightRatio));
                    applyPortraitSplitVideoLayout(videoShellH, contentW, embeddedContentH, vpLp, shellLp);
                }
                break;
            }
            case FLOATING_AV_MODE_FULL: {
                setMainTabBarVisible(false);
                vpLp.height = 0;
                vpLp.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                vpLp.topMargin = 0;
                vpLp.addRule(RelativeLayout.ABOVE, R.id.div_tab_bar);
                vPager.setVisibility(View.GONE);
                vPager.setLayoutParams(vpLp);

                shellLp.width = FrameLayout.LayoutParams.MATCH_PARENT;
                shellLp.height = FrameLayout.LayoutParams.MATCH_PARENT;
                shellLp.gravity = Gravity.FILL;
                shellLp.setMargins(0, 0, 0, 0);
                m_floatingAvShell.setLayoutParams(shellLp);
                m_floatingAvShell.setX(0f);
                m_floatingAvShell.setY(0f);
                applyFloatingAvShellSystemInsets();
                m_floatingAvShell.post(this::ensureFloatingAvShellFillParent);
                m_floatingAvShell.postDelayed(this::ensureFloatingAvShellFillParent, 120);
                break;
            }
            default: {
                vPager.setVisibility(View.VISIBLE);
                vPager.setLayoutParams(vpLp);

                boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
                int shellW;
                int shellH;
                int topMargin = isLandscape ? dpToPx(56) : dpToPx(88);
                int bottomMargin = getFloatingAvBottomInsetPx();
                if (isLandscape) {
                    shellW = Math.max(dpToPx(220), (int) (contentW * 0.34f));
                    int desiredH = (int) (shellW * 4f / 3f);
                    int maxH = Math.max(dpToPx(220), contentH - topMargin);
                    shellH = Math.max(dpToPx(220), Math.min(desiredH, maxH));
                } else {
                    shellW = Math.max(dpToPx(240), (int) (contentW * 0.44f));
                    shellH = Math.max(dpToPx(300), (int) (shellW * 4f / 3f));
                    int maxH = Math.max(dpToPx(220), contentH - topMargin);
                    shellH = Math.min(shellH, maxH);
                }
                shellLp.width = shellW;
                shellLp.height = shellH;
                shellLp.gravity = Gravity.NO_GRAVITY;
                shellLp.leftMargin = 0;
                shellLp.topMargin = 0;
                shellLp.rightMargin = 0;
                shellLp.bottomMargin = 0;
                m_floatingAvShell.setLayoutParams(shellLp);
                m_floatingAvShell.setX(getDefaultFloatingAvShellX(shellW, contentW));
                m_floatingAvShell.setY(getDefaultFloatingAvShellY(shellH));
                m_floatingAvShell.post(this::clampFloatingAvShellToSafeBounds);
                m_floatingAvShell.postDelayed(this::clampFloatingAvShellToSafeBounds, 120);
                break;
            }
        }
        updateFloatingAvModeButtons();
        updateSplitDividersVisibility();
        notifyMapFloatingAvFragmentLayoutChanged();
        notifyMapFloatingLayoutChanged();
        if (m_floatingAvShell != null) {
            m_floatingAvShell.post(this::refreshMapFragmentOverlayTextScale);
            m_floatingAvShell.postDelayed(this::refreshMapFragmentOverlayTextScale, 200);
        } else {
            refreshMapFragmentOverlayTextScale();
        }
    }

    /** 全屏时避开系统导航栏，避免底部 AV 工具栏被裁切。 */
    private void applyFloatingAvShellSystemInsets() {
        if (m_floatingAvShell == null) return;
        ViewCompat.setOnApplyWindowInsetsListener(m_floatingAvShell, (v, insets) -> {
            if (floatingAvDisplayMode != FLOATING_AV_MODE_FULL) {
                v.setPadding(0, 0, 0, 0);
                return insets;
            }
            int navBottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(0, 0, 0, navBottom);
            return insets;
        });
        ViewCompat.requestApplyInsets(m_floatingAvShell);
    }

    private void clearFloatingAvShellSystemInsets() {
        if (m_floatingAvShell == null) return;
        ViewCompat.setOnApplyWindowInsetsListener(m_floatingAvShell, null);
        m_floatingAvShell.setPadding(0, 0, 0, 0);
    }

    private int getMainTabInsetPx() {
        int inset = 0;
        if (findViewById(R.id.ly_tab_bar) != null) {
            inset += findViewById(R.id.ly_tab_bar).getHeight();
        }
        if (findViewById(R.id.div_tab_bar) != null) {
            inset += findViewById(R.id.div_tab_bar).getHeight();
        }
        if (inset <= 0) {
            inset = dpToPx(56);
        }
        return inset;
    }

    private int getFloatingAvBottomInsetPx() {
        return getMainTabInsetPx() + dpToPx(12);
    }

    private int getFloatingAvContentHeightPx() {
        if (m_layout_total != null && m_layout_total.getHeight() > 0) {
            return m_layout_total.getHeight() - getFloatingAvBottomInsetPx();
        }
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return dm.heightPixels - getFloatingAvBottomInsetPx();
    }

    private int getEmbeddedContentHeightPx() {
        if (m_layout_total != null && m_layout_total.getHeight() > 0) {
            return m_layout_total.getHeight() - getMainTabInsetPx();
        }
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return dm.heightPixels - getMainTabInsetPx();
    }

    private int getFloatingAvContentWidthPx() {
        if (m_layout_total != null && m_layout_total.getWidth() > 0) {
            return m_layout_total.getWidth();
        }
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    private int getFloatingAvRootHeightPx() {
        if (m_layout_total != null && m_layout_total.getHeight() > 0) {
            return m_layout_total.getHeight();
        }
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    private int getFloatingAvMapLeftToolbarReservePx() {
        return dpToPx(FLOATING_AV_MAP_LEFT_TOOLBAR_DP);
    }

    /** 默认浮窗贴在左侧工具条右侧，避免挡住 zoom/标注 等按钮。 */
    private float getDefaultFloatingAvShellX(int shellW, int contentW) {
        float minX = getFloatingAvMapLeftToolbarReservePx();
        float maxX = Math.max(minX, contentW - shellW - dpToPx(12));
        return Math.min(minX, maxX);
    }

    /** 默认浮窗在可用区域内垂直居中（中间偏左）。 */
    private float getDefaultFloatingAvShellY(int shellH) {
        int rootH = getFloatingAvRootHeightPx();
        int bottomInset = getFloatingAvBottomInsetPx();
        boolean isLandscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
        float minY = isLandscape ? dpToPx(56) : dpToPx(88);
        float maxY = Math.max(minY, rootH - shellH - bottomInset);
        float centerY = (rootH - shellH) / 2f;
        return Math.max(minY, Math.min(centerY, maxY));
    }

    private void clampFloatingAvShellToSafeBounds() {
        if (m_floatingAvShell == null || floatingAvDisplayMode != FLOATING_AV_MODE_FLOAT) return;
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) m_floatingAvShell.getLayoutParams();
        if (lp == null) return;
        int contentW = getFloatingAvContentWidthPx();
        int rootH = getFloatingAvRootHeightPx();
        int topAnchor = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                ? dpToPx(56) : dpToPx(88);
        int maxH = Math.max(dpToPx(180), rootH - topAnchor - getFloatingAvBottomInsetPx());
        if (lp.height > maxH) {
            lp.height = maxH;
            m_floatingAvShell.setLayoutParams(lp);
        }
        float minX = getFloatingAvMapLeftToolbarReservePx();
        float maxX = Math.max(minX, contentW - m_floatingAvShell.getWidth() - dpToPx(12));
        float minY = dpToPx(8);
        float maxY = Math.max(minY, rootH - m_floatingAvShell.getHeight() - getFloatingAvBottomInsetPx());
        m_floatingAvShell.setX(Math.max(minX, Math.min(m_floatingAvShell.getX(), maxX)));
        m_floatingAvShell.setY(Math.max(minY, Math.min(m_floatingAvShell.getY(), maxY)));
    }

    private void enforceFloatingAvSafeState() {
        if (m_floatingAvShell == null) return;
        if (floatingAvDisplayMode == FLOATING_AV_MODE_FULL) {
            ensureFloatingAvShellFillParent();
            return;
        }
        if (floatingAvDisplayMode == FLOATING_AV_MODE_SPLIT) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) m_floatingAvShell.getLayoutParams();
            if (lp == null) return;
            int contentH = getEmbeddedContentHeightPx();
            boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
            if (isLandscape) {
                if (lp.height != contentH) {
                    lp.height = contentH;
                    lp.bottomMargin = 0;
                    lp.gravity = Gravity.START | Gravity.TOP;
                    m_floatingAvShell.setLayoutParams(lp);
                }
                positionLandscapeSplitDivider(lp.width, contentH);
            } else {
                if (lp.width > 0 && lp.height > 0) {
                    positionPortraitSplitDivider(lp.height, getFloatingAvContentWidthPx());
                }
                lp.leftMargin = 0;
                lp.bottomMargin = 0;
                m_floatingAvShell.setLayoutParams(lp);
            }
            m_floatingAvShell.setX(0f);
            m_floatingAvShell.setY(0f);
            return;
        }
        if (floatingAvDisplayMode != FLOATING_AV_MODE_FLOAT) {
            return;
        }
        clampFloatingAvShellToSafeBounds();
    }

    private void ensureFloatingAvShellFillParent() {
        if (m_floatingAvShell == null || floatingAvDisplayMode != FLOATING_AV_MODE_FULL) return;
        View parent = (View) m_floatingAvShell.getParent();
        if (parent == null) return;
        int w = parent.getWidth();
        int h = parent.getHeight();
        if (w <= 0 || h <= 0) return;
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) m_floatingAvShell.getLayoutParams();
        if (lp == null) return;
        if (lp.width != w || lp.height != h) {
            lp.width = w;
            lp.height = h;
            lp.gravity = Gravity.FILL;
            lp.setMargins(0, 0, 0, 0);
            m_floatingAvShell.setLayoutParams(lp);
        }
        m_floatingAvShell.setX(0f);
        m_floatingAvShell.setY(0f);
    }

    private int dpToPx(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private void updateFloatingAvModeButtons() {
        if (m_btnFloatingAvFloat == null || m_btnFloatingAvSplit == null || m_btnFloatingAvFull == null) {
            return;
        }
        m_btnFloatingAvFloat.setVisibility(
                floatingAvUserMode == FLOATING_AV_MODE_FLOAT ? View.GONE : View.VISIBLE);
        m_btnFloatingAvSplit.setVisibility(
                floatingAvUserMode == FLOATING_AV_MODE_SPLIT ? View.GONE : View.VISIBLE);
        m_btnFloatingAvFull.setVisibility(
                floatingAvUserMode == FLOATING_AV_MODE_FULL ? View.GONE : View.VISIBLE);
    }

    private void restoreMainContentAfterFloatingAv() {
        floatingAvUserMode = FLOATING_AV_MODE_FLOAT;
        floatingAvDisplayMode = FLOATING_AV_MODE_FLOAT;
        setTitleBarConfMemBtnVisible(false);
        clearFloatingAvShellSystemInsets();
        setMainTabBarVisible(true);
        if (m_floatingAvShell != null && vPager != null) {
            applyFloatingAvLayoutImpl(FLOATING_AV_MODE_FLOAT);
        }
        if (vPager != null) {
            vPager.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams vpLp = (RelativeLayout.LayoutParams) vPager.getLayoutParams();
            if (vpLp != null) {
                vpLp.height = RelativeLayout.LayoutParams.MATCH_PARENT;
                vpLp.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                vpLp.topMargin = 0;
                vpLp.leftMargin = 0;
                vpLp.rightMargin = 0;
                vpLp.bottomMargin = 0;
                vpLp.addRule(RelativeLayout.ABOVE, R.id.div_tab_bar);
                vpLp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                vpLp.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
                vpLp.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
                vPager.setLayoutParams(vpLp);
                vPager.requestLayout();
            }
        }
        if (m_floatingAvShell != null) {
            m_floatingAvShell.setTranslationX(0f);
            m_floatingAvShell.setTranslationY(0f);
        }
        updateSplitDividersVisibility();
        notifyMapFloatingLayoutChanged();
        refreshMapFragmentOverlayTextScale();
    }

    private void notifyMapFloatingAvFragmentLayoutChanged() {
        if (m_floatingView_fg instanceof Fragment_dlgTalk_av2) {
            Fragment_dlgTalk_av2 avFg = (Fragment_dlgTalk_av2) m_floatingView_fg;
            if (avFg.isAdded()) {
                avFg.onMapFloatingAvDisplayModeChanged();
                avFg.onMapFloatingAvShellResized();
            } else if (m_floatingAvShell != null) {
                m_floatingAvShell.post(() -> {
                    if (m_floatingView_fg instanceof Fragment_dlgTalk_av2) {
                        Fragment_dlgTalk_av2 fg = (Fragment_dlgTalk_av2) m_floatingView_fg;
                        if (fg.isAdded()) {
                            fg.onMapFloatingAvDisplayModeChanged();
                            fg.onMapFloatingAvShellResized();
                        }
                    }
                });
            }
        }
    }

    private void notifyMapFloatingAvShellResized() {
        if (m_floatingView_fg instanceof Fragment_dlgTalk_av2) {
            Fragment_dlgTalk_av2 avFg = (Fragment_dlgTalk_av2) m_floatingView_fg;
            if (avFg.isAdded()) {
                avFg.onMapFloatingAvShellResized();
            } else if (m_floatingAvShell != null) {
                m_floatingAvShell.post(() -> {
                    if (m_floatingView_fg instanceof Fragment_dlgTalk_av2) {
                        Fragment_dlgTalk_av2 fg = (Fragment_dlgTalk_av2) m_floatingView_fg;
                        if (fg.isAdded()) {
                            fg.onMapFloatingAvShellResized();
                        }
                    }
                });
            }
        }
    }

    private void notifyMapFloatingLayoutChanged() {
        if (mAdapter == null || !(mAdapter.mFragment_map instanceof Fragment_map)) {
            return;
        }
        Fragment_map mapFg = (Fragment_map) mAdapter.mFragment_map;
        if (mapFg.isAdded()) {
            mapFg.onHostEmbeddedLayoutChanged();
        } else {
            vPager.postDelayed(() -> {
                if (mAdapter.mFragment_map instanceof Fragment_map) {
                    Fragment_map fg = (Fragment_map) mAdapter.mFragment_map;
                    if (fg.isAdded()) {
                        fg.onHostEmbeddedLayoutChanged();
                    }
                }
            }, 120);
        }
    }

    /** 仅在浮窗模式下拖动标题栏，避免与视频区点击放大冲突。 */
    private void initFloatingAvDrag() {
        if (m_floatingAvTitleBar == null || m_floatingAvShell == null) return;
        final int touchSlop = dpToPx(4);
        final boolean[] moved = {false};
        View.OnTouchListener dragTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (floatingAvDisplayMode != FLOATING_AV_MODE_FLOAT) {
                    return false;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        fv_lastX = event.getRawX();
                        fv_lastY = event.getRawY();
                        fv_downX = fv_lastX;
                        fv_downY = fv_lastY;
                        moved[0] = false;
                        return true;
                    case MotionEvent.ACTION_MOVE: {
                        float dx = event.getRawX() - fv_lastX;
                        float dy = event.getRawY() - fv_lastY;
                        if (!moved[0]) {
                            moved[0] = Math.abs(event.getRawX() - fv_downX) > touchSlop
                                    || Math.abs(event.getRawY() - fv_downY) > touchSlop;
                        }
                        m_floatingAvShell.setX(m_floatingAvShell.getX() + dx);
                        m_floatingAvShell.setY(m_floatingAvShell.getY() + dy);
                        clampFloatingAvShellToSafeBounds();
                        fv_lastX = event.getRawX();
                        fv_lastY = event.getRawY();
                        return true;
                    }
                    case MotionEvent.ACTION_UP:
                        if (!moved[0]) {
                            v.performClick();
                        }
                        return true;
                    default:
                        break;
                }
                return false;
            }
        };
        m_floatingAvTitleBar.setOnTouchListener(dragTouchListener);
        if (m_btnFloatingAvFloat != null) m_btnFloatingAvFloat.setOnTouchListener(dragTouchListener);
        if (m_btnFloatingAvSplit != null) m_btnFloatingAvSplit.setOnTouchListener(dragTouchListener);
        if (m_btnFloatingAvFull != null) m_btnFloatingAvFull.setOnTouchListener(dragTouchListener);
        if (m_btnFloatingAvConfMem != null) m_btnFloatingAvConfMem.setOnTouchListener(dragTouchListener);
        View btnClose = findViewById(R.id.btn_floating_av_close);
        if (btnClose != null) btnClose.setOnTouchListener(dragTouchListener);
    }


    //
    int startActivity_login() {
        int iErr = -1;

        Intent intent = new Intent(this, Activity_qmcLogin.class);
        Log.d("startActivity_login called");
        try {
            startActivityForResult(intent, requestCode_qmcLogin);
        } catch (Exception e) {
            return iErr;
        }
        iErr = 0;
        return iErr;

    }

    /** 进入地图时输入口令；未输入则不发送、不显示他人位置。 */
    private void showLocKeywordDialog() {
        if (isFinishing()) {
            return;
        }
        locKeywordDialogShowing = true;

        View content = LayoutInflater.from(this).inflate(R.layout.dialog_map_loc_keyword, null, false);
        content.setClipToOutline(true);
        content.setOutlineProvider(android.view.ViewOutlineProvider.BACKGROUND);
        EditText input = content.findViewById(R.id.edtMapLocKeyword);
        TextView btnCancel = content.findViewById(R.id.btnMapLocKeywordCancel);
        TextView btnConfirm = content.findViewById(R.id.btnMapLocKeywordConfirm);

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.MapStyledDialogTheme)
                .setView(content)
                .setCancelable(true)
                .create();
        dialog.setOnDismissListener(d -> locKeywordDialogShowing = false);
        dialog.setOnShowListener(d -> {
            Window w = dialog.getWindow();
            if (w != null) {
                w.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
                View decor = w.getDecorView();
                if (decor != null) {
                    decor.setBackgroundResource(android.R.color.transparent);
                }
                View panel = w.findViewById(android.R.id.content);
                if (panel != null) {
                    panel.setBackgroundResource(android.R.color.transparent);
                }
                android.view.ViewParent parent = content.getParent();
                while (parent instanceof View) {
                    View wrapper = (View) parent;
                    wrapper.setBackgroundResource(android.R.color.transparent);
                    wrapper.setPadding(0, 0, 0, 0);
                    if (wrapper.getId() == android.R.id.content) {
                        break;
                    }
                    parent = wrapper.getParent();
                }
                WindowManager.LayoutParams lp = w.getAttributes();
                lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.84f);
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                w.setAttributes(lp);
            }
        });
        btnCancel.setOnClickListener(v -> {
            clearMapLocKeywordAndRemoteLocations();
            dialog.dismiss();
        });
        btnConfirm.setOnClickListener(v -> {
            String kw = input.getText() != null ? input.getText().toString().trim() : "";
            if (kw.isEmpty()) {
                Toast.makeText(this, "未输入地图口令", Toast.LENGTH_SHORT).show();
                clearMapLocKeywordAndRemoteLocations();
                dialog.dismiss();
                return;
            }
            MapLocKeyword.setUserKeyword(kw);
            if (!MapLocStrCrypto.verifyRoundTrip(kw)) {
                Toast.makeText(this, "地图口令校验失败，请重新输入", Toast.LENGTH_SHORT).show();
                clearMapLocKeywordAndRemoteLocations();
                dialog.dismiss();
                return;
            }
            notifyMapLocKeywordReady();
            dialog.dismiss();
        });
        dialog.show();
    }

    /** 更换口令：无需先关闭再开启，直接输入新口令即可（需两次一致）。 */
    public void showChangeLocKeywordDialog() {
        if (isFinishing()) {
            return;
        }
        if (!MapLocSettings.isEncryptionEnabled()) {
            Toast.makeText(this, "请先开启地图口令", Toast.LENGTH_SHORT).show();
            return;
        }

        View content = LayoutInflater.from(this).inflate(R.layout.dialog_map_loc_change_keyword, null, false);
        content.setClipToOutline(true);
        content.setOutlineProvider(android.view.ViewOutlineProvider.BACKGROUND);
        EditText inputNew = content.findViewById(R.id.edtMapLocNewKeyword);
        TextView btnCancel = content.findViewById(R.id.btnMapLocChangeKeywordCancel);
        TextView btnConfirm = content.findViewById(R.id.btnMapLocChangeKeywordConfirm);

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.MapStyledDialogTheme)
                .setView(content)
                .setCancelable(true)
                .create();
        dialog.setOnShowListener(d -> {
            Window w = dialog.getWindow();
            if (w != null) {
                w.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
                View decor = w.getDecorView();
                if (decor != null) {
                    decor.setBackgroundResource(android.R.color.transparent);
                }
                View panel = w.findViewById(android.R.id.content);
                if (panel != null) {
                    panel.setBackgroundResource(android.R.color.transparent);
                }
                android.view.ViewParent parent = content.getParent();
                while (parent instanceof View) {
                    View wrapper = (View) parent;
                    wrapper.setBackgroundResource(android.R.color.transparent);
                    wrapper.setPadding(0, 0, 0, 0);
                    if (wrapper.getId() == android.R.id.content) {
                        break;
                    }
                    parent = wrapper.getParent();
                }
                WindowManager.LayoutParams lp = w.getAttributes();
                lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.84f);
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                w.setAttributes(lp);
            }
        });
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            String kw = inputNew.getText() != null ? inputNew.getText().toString().trim() : "";
            if (kw.isEmpty()) {
                Toast.makeText(this, "未输入新地图口令", Toast.LENGTH_SHORT).show();
                return;
            }
            MapLocKeyword.setUserKeyword(kw);
            if (!MapLocStrCrypto.verifyRoundTrip(kw)) {
                Toast.makeText(this, "地图口令校验失败，请重新输入", Toast.LENGTH_SHORT).show();
                clearMapLocKeywordAndRemoteLocations();
                dialog.dismiss();
                return;
            }
            // 旧口令解出的他人位置作废，改用新口令重新解密并重报本机位置。
            clearMapRemoteLocations();
            notifyMapLocKeywordReady();
            Toast.makeText(this, "地图口令已修改", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        dialog.show();
    }

    /** 供 myMapExt 回调：在地图上显示他人定位。 */
    public void applyMapRemoteLocation(long userId, @NonNull String plainLocStr) {
        if (mAdapter == null || !(mAdapter.mFragment_map instanceof Fragment_map)) {
            return;
        }
        ((Fragment_map) mAdapter.mFragment_map).applyRemoteLocation(userId, plainLocStr);
    }

    /** 供 myMapExt 回调：在地图上显示他人同步注记。 */
    public void applyMapRemoteMark(long userId, @NonNull String markStr) {
        if (mAdapter == null || !(mAdapter.mFragment_map instanceof Fragment_map)) {
            return;
        }
        ((Fragment_map) mAdapter.mFragment_map).applyRemoteMark(userId, markStr);
    }

    /** 加密开关切换等场景：清除地图上他人定位点。 */
    public void clearMapRemoteLocations() {
        if (mAdapter == null || !(mAdapter.mFragment_map instanceof Fragment_map)) {
            return;
        }
        ((Fragment_map) mAdapter.mFragment_map).clearRemoteLocations();
    }

    private void bindViews() {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;

        //
        m_layout_total = findViewById(R.id.layout_total);
        //txt_topbar = findViewById(R.id.txt_topbar);

        tab_map = findViewById(R.id.tab_map);
        tab_msg = findViewById(R.id.tab_msg);
        tab_contact = findViewById(R.id.tab_contact);
        tab_me = findViewById(R.id.tab_me);
        tab_map_indicator = findViewById(R.id.tab_map_indicator);
        tab_msg_indicator = findViewById(R.id.tab_msg_indicator);
        tab_contact_indicator = findViewById(R.id.tab_contact_indicator);
        tab_me_indicator = findViewById(R.id.tab_me_indicator);

        if (pQyMc.appParams.bENABLE_MAP_TAB) {
            tab_map.setOnClickListener(this);
        } else {
            tab_map.setVisibility(View.GONE);
        }
        tab_msg.setOnClickListener(this);
        tab_contact.setOnClickListener(this);
        tab_me.setOnClickListener(this);

        vPager = findViewById(R.id.vpager);
        vPager.setAdapter(mAdapter);
        // 保留全部 Tab 的 Fragment/View，避免切到消息/联系人时地图 onDestroyView 后再次进入全量重载。
        if (pQyMc.appParams.bENABLE_MAP_TAB) {
            vPager.setOffscreenPageLimit(PAGE_FOUR);
        }
        vPager.addOnPageChangeListener(this);
        //
        if (pQyMc.appParams.bENABLE_MAP_TAB) {
            tab_map.performClick();
        } else {
            tab_msg.performClick();
        }
    }

    private void setTabSelected(View tab, View indicator, boolean selected) {
        if (tab != null) {
            tab.setSelected(selected);
        }
        if (indicator != null) {
            indicator.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void clearSelected() {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;

        setTabSelected(tab_msg, tab_msg_indicator, false);
        setTabSelected(tab_contact, tab_contact_indicator, false);
        setTabSelected(tab_me, tab_me_indicator, false);
        if (pQyMc.appParams.bENABLE_MAP_TAB) {
            setTabSelected(tab_map, tab_map_indicator, false);
        }
    }

    private void applyTabSelection(int page) {
        clearSelected();
        if (g_pQyMc.appParams.bENABLE_MAP_TAB && page == PAGE_FOUR) {
            setTabSelected(tab_map, tab_map_indicator, true);
        } else if (page == PAGE_ONE) {
            setTabSelected(tab_msg, tab_msg_indicator, true);
        } else if (page == PAGE_TWO) {
            setTabSelected(tab_contact, tab_contact_indicator, true);
        } else if (page == PAGE_THREE) {
            setTabSelected(tab_me, tab_me_indicator, true);
        }
    }

    public void onClick(View v) {
        ctxQyMc.CCtxQyMc pQyMc  = g_pQyMc;

        if  (  pQyMc.appParams.bENABLE_MAP_TAB  &&  v.getId()==R.id.tab_map) {
            navigateToTab(PAGE_FOUR);
        }
        else if (v.getId() == R.id.tab_msg) {
            navigateToTab(PAGE_ONE);
        } else if (v.getId() == R.id.tab_contact) {
            navigateToTab(PAGE_TWO);
        } else if (v.getId() == R.id.tab_me) {
            navigateToTab(PAGE_THREE);
        }
    }

    /**
     * 与底部 Tab 点击一致：同时更新选中态与 ViewPager，避免只切了页面而导航条仍高亮其它项。
     */
    public void navigateToTab(int page) {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;

        if (vPager == null) {
            return;
        }
        applyTabSelection(page);
        if (pQyMc.appParams.bENABLE_MAP_TAB && page == PAGE_FOUR) {
            vPager.setCurrentItem(PAGE_FOUR, false);
        } else if (page == PAGE_ONE) {
            vPager.setCurrentItem(PAGE_ONE, false);
        } else if (page == PAGE_TWO) {
            vPager.setCurrentItem(PAGE_TWO, false);
        } else if (page == PAGE_THREE) {
            vPager.setCurrentItem(PAGE_THREE, false);
        }
        if (m_floatingView_fg != null) {
            vPager.post(this::syncFloatingAvLayoutForCurrentTab);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        this.gui_PostMessage_new.call(this, qyWndContentType.Consts.CONST_qyWndContentType_mainWnd, qyWmComm.Consts.CONST_qyWm_postComm, CONST_qyWmParam_closeWnd, 0);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 竖屏浮窗、横屏分屏：旋转时按方向恢复默认视频布局，避免竖屏仍保持横屏分屏导致地图字号/布局异常
        if (m_floatingView_fg != null && isOnMapTab()) {
            floatingAvUserMode = getDefaultFloatingAvModeForOrientation();
        }
        notifyMapFragmentConfigurationChanged(newConfig);
        scheduleMapFragmentOverlayRefreshAfterRotation(newConfig);
        if (m_floatingView_fg == null || m_floatingAvShell == null) {
            return;
        }
        m_floatingAvShell.post(() -> {
            syncFloatingAvLayoutForCurrentTab();
            clampFloatingAvShellToSafeBounds();
            ensureFloatingAvShellFillParent();
            notifyMapFloatingLayoutChanged();
            refreshMapFragmentOverlayTextScale();
        });
        m_floatingAvShell.postDelayed(() -> {
            notifyMapFloatingLayoutChanged();
            refreshMapFragmentOverlayTextScale();
        }, 280L);
        m_floatingAvShell.postDelayed(this::restartMapFloatingCameraAfterRotationSettled, 320L);
    }

    private void scheduleMapFragmentOverlayRefreshAfterRotation(@NonNull Configuration newConfig) {
        refreshMapFragmentOverlayTextScale();
        if (mAdapter == null || !(mAdapter.mFragment_map instanceof Fragment_map)) {
            return;
        }
        Fragment_map mapFg = (Fragment_map) mAdapter.mFragment_map;
        Runnable refresh = () -> {
            if (mapFg.isAdded()) {
                mapFg.scheduleMapOverlayTextRefresh(newConfig);
            }
        };
        if (vPager != null) {
            vPager.post(refresh);
            vPager.postDelayed(refresh, 150);
            vPager.postDelayed(refresh, 350);
        } else {
            refresh.run();
        }
    }

    private void notifyMapFragmentConfigurationChanged(@NonNull Configuration newConfig) {
        if (mAdapter == null || !(mAdapter.mFragment_map instanceof Fragment_map)) {
            return;
        }
        Fragment_map mapFg = (Fragment_map) mAdapter.mFragment_map;
        if (mapFg.isAdded()) {
            mapFg.onHostConfigurationChanged(newConfig);
        } else if (vPager != null) {
            vPager.postDelayed(() -> {
                if (mAdapter.mFragment_map instanceof Fragment_map) {
                    Fragment_map fg = (Fragment_map) mAdapter.mFragment_map;
                    if (fg.isAdded()) {
                        fg.onHostConfigurationChanged(newConfig);
                    }
                }
            }, 120);
        }
    }

    private void restartMapFloatingCameraAfterRotationSettled() {
        long now = android.os.SystemClock.elapsedRealtime();
        if (now - lastMapFloatingCameraRestartMs < 800L) {
            return;
        }
        lastMapFloatingCameraRestartMs = now;
        if (m_floatingView_fg instanceof Fragment_dlgTalk_av2) {
            ((Fragment_dlgTalk_av2) m_floatingView_fg).restartCameraAfterHostRotationSettled();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //
        isActive = true;

        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (pQyMc != null && pQyMc.bLogon) {
            requestPostLoginPermissionsIfNeeded();
        }

        //
        gui_notify_chk.call();
    }

    //
    int saveRecentContacts() {
        if (null != this.mAdapter.mFragment_msg1) {
            this.mAdapter.mFragment_msg1.saveRecentContacts();
        }

        //
        return 0;
    }


    //
    public int initVar_onCreate_mainFrame(long p0, qyMcMainWndProc.QY_MC_mainWndVar pVar) {
        int iErr = -1;
        //
        //Object hMainWnd;
        //if (null == hMainWnd) return -1;

        //
        Log.d("at","initVar_onCreate_mainFrame enters");

        //
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (null == pQyMc) return -1;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if (null == pProcInfo) return -1;
        if (pProcInfo.m_iCtxType != CONST_ctxType_qmc) return -1;

        pProcInfo.dwThreadId_gui = myGetCurrentProcessId();
        pVar.ctxCaller = new ctxCaller.CTX_caller();
        pVar.ctxCaller.type = CONST_ctxCallerType_thread;
        pVar.ctxCaller.dwThreadId = tmpFunc.qyFuncs.myGetCurrentThreadId();
        pVar.pMsgBuf_msgArrive = new qnmCommProc_mis.MIS_MSGU();

        //
        //guiShare.GuiShare.pf_mainWnd_setTimer.call();
        setTimer(1000, gui_onTimer);
        Log.d("at","SetTimer");

        //
        Log.d("at","initVar_onCreate_mainFrame leaves");

        //
        iErr = 0;
        return iErr;
    }

    //
    public int exitVar_onDestroy_mainFrame(long p0, qyMcMainWndProc.QY_MC_mainWndVar pVar) {
        int iErr = -1;

        //
        Log.d("at","exitVar_onDestroy_mainFrame, enters");

        /*
        Object hMainWnd,
        if (null == hMainWnd) {
            Log.i("exitVar_onDestroy_mainFrame failed: pMainWnd or pVar is null");
            return -1;
        }
         */

        //
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (null == pQyMc) return -1;


        Log.i("exitVar_onDestroy_mainFrame enters.");
        Log.d(("exitVar_onDestroy_mainFrame enters."));

        //
        //if (pVar->uiTimerId) KillTimer(hMainWnd, pVar->uiTimerId);
        //guiShare.GuiShare.pf_mainWnd_killTimer.call();
        this.killTimer("exitVar_onDestroy_mainFrame");
        Log.d("at","exitVar_onDestroy_mainFrame, killTimer");

        //
        if (pVar.pMsgBuf_msgArrive != null) {
//                qyMemoryFunc.qyFuncs.myfree( pVar.pMsgBuf_msgArrive);
            pVar.pMsgBuf_msgArrive = null;
        }

        //
        Log.d("at","exitVar_onDestroy_mainFrame, leaves");

        //
        return 0;
    }


    //
    void doClean(String hint) {
        String str;

        if (hint == null) hint = "";

        str = String.format("doClean %s", hint);

        showInfo_open.qyFuncs.showInfo_open(str);
        Log.d(str + " enters");

        //
        if (g_pQyMc != null) {
            //
            saveRecentContacts();

            //
            exitVar_onDestroy_mainFrame(0, var_common);
            //
            //qy.Droid.qyFuncs_Droid.ExitInstance((Object)null);
        }

        Log.d(str + " leaves");

        return;
    }

    @Override
    protected void onPause() {
        //
        Log.d("mainWnd: OnPause");

        super.onPause();
        isActive = false;

    }

    @Override
    protected void onStop() {
        clearMapLocKeywordAndRemoteLocations();
        super.onStop();
        //
        //Log.d( "qy","MainActivity  OnStop kk   k");
    }

    @Override
    protected void onDestroy() {

        unregisterMapLocScreenOffReceiver();
        if  (  false  ) {
            Log.d("qy", "MainActivity  OnDestroy");
            showInfo_open.qyFuncs.showInfo_open("MainActivity  OnDestroy");
        }


        //
        doClean("mainWnd.OnDestroy");

        //
        qyMcMain_qyMc_proc.qyFuncs_Droid.ExitInstance(0);
        prepare.qyFuncs_Droid.prepare_exit();

        if (this.mAdapter != null) {
            this.mAdapter.doClean();
            this.mAdapter = null;
        }


        //清理全局变量持有
        GuiShareProc.qyFuncs_Droid.GuiShare_exit();

        super.onDestroy();
        //
        Log.e("qy","call System.exit to clear up");
        System.exit(0);
        Log.e("qy","after System.exit");
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //throw new NotImplementedException();
        if (state == 2) {
            applyTabSelection(vPager.getCurrentItem());
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        handleMapTabPagerChange(position);
        if (m_floatingView_fg != null) {
            syncFloatingAvLayoutForCurrentTab();
            refreshMapFragmentOverlayTextScale();
        }
    }

    BottomSheetDialog m_bsd_askToQuit;
    boolean m_bReadyToQuit = false;
    ProgressDialog m_progressDialog;
    Tmp_progressThread m_progressThread;
    Tmp_progressHandler m_progressHandler;

    public volatile int textureId;
    public volatile int textureIw;
    public volatile int textureIh;


    static class Tmp_progressHandler extends Handler {
        public ProgressDialog mOwner;

        public Tmp_progressHandler(ProgressDialog owner) {
            mOwner = owner;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //
            mOwner.incrementProgressBy(2);
        }

    }


    static class Tmp_progressThread extends Thread {
        public Tmp_progressHandler m_progressHandler;
        public boolean bQuit;

        public Tmp_progressThread(Tmp_progressHandler progressHandler) {
            m_progressHandler = progressHandler;
        }

        @Override
        public void run() {
            //base.Run();
            for (; ; ) {
                if (bQuit) break;
                //
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Log.e(e.getMessage());
                    bQuit = false;
                    return;
                }
                //m_progressHandler.sendMessage(m_progressHandler.obtainMessage());
                m_progressHandler.mOwner.incrementProgressBy(2);
                //
                if (m_progressHandler.mOwner.getProgress() == m_progressHandler.mOwner.getMax()) {

                }
            }
        }
    }
}
