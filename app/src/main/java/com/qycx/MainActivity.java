package com.qycx;

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
import static com.qycx.base.isTalkerQProc.addToIsTalkerQ;
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
import static com.qycx.base.qyWmComm.Consts.CONST_qyWm_postComm;
import static com.qycx.base.qyWndContentType.Consts.CONST_fgType_talk_av2;
import static com.qycx.base.qyWndContentType.Consts.CONST_qyWndContentType_mainWnd;
import static com.qycx.base.tmpFunc.qyFuncs.myGetCurrentProcessId;
import static com.qycx.base.tmpFunc.qyFuncs.myGetCurrentThreadId;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;
import static com.qycx.base.tmpProc.qyFuncs.bTaskActive;

import static org.aviran.cookiebar2.CookieBarDismissListener.DismissType.DURATION_COMPLETE;
import static org.aviran.cookiebar2.CookieBarDismissListener.DismissType.USER_ACTION_CLICK;
import static org.aviran.cookiebar2.CookieBarDismissListener.DismissType.USER_DISMISS;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.qycx.anc.TmpLyra;
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
import com.qycx.base.qyMcMain_global;
import com.qycx.base.qyMcMain_qyMc_proc;
import com.qycx.base.qyWmComm;
import com.qycx.base.qyWndContentType;
import com.qycx.base.showInfo_open;
import com.qycx.base.test;
import com.qycx.base.tmpFunc;
import com.qycx.base.tmpResource;
import com.qycx.base.util.Log;
import com.qycx.base.util.javatuples.Pair;

import org.aviran.cookiebar2.*;

import java.io.File;
import java.lang.ref.WeakReference;
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
    private TextView txt_map;
    private TextView txt_msg;
    private TextView txt_contact;
    private TextView txt_me;
    private ViewPager vPager;

    //Fragment Object
//private MyFragment fg1, fg2, fg3, fg4;
    private FragmentPagerAdapter_main mAdapter;

    public final static int PAGE_ONE = 0;
    public final static int PAGE_TWO = 1;
    public final static int PAGE_THREE = 2;
    public final static int PAGE_FOUR=3;

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
            boolean bStay_toAvCall = pProcInfo.bStay_toAvCall;
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
                audioRecorder_android.qyFuncs_Droid.setMicrophoneOn(true);

                //
                Activity_dlgTalk hTalk = null;
                if ((hTalk = guiShare.GuiShare.pf_findTalker.call(pQyMc, pMisCnt, peer_addr.idInfo)) != null
                        && dlgTalkProc_help.qyFuncs_Droid.bDlgTalkActive(hTalk))
                {
                    Log.d("GuiShare.pf_findTalker.call(pQyMc, pMisCnt, addr.idInfo,  hTalk) == 0 && qyFuncs_Droid.bDlgTalkActive(hTalk) 为ture");

                    ((MyApplication) (mainActivity.getApplication())).mediaPlayer.pause();
                    hTalk.gui_PostMessage(qyWmComm.Consts.CONST_qyWm_postComm, qyWmComm.Consts.CONST_qyWmParam_doTask, usOp);

                    //
                    break;
                }

                //
                boolean bStay_toAvCall = pProcInfo.bStay_toAvCall;
                //
                isCliHelpPublic.PARAM_talkToMessenger param = new isCliHelpPublic.PARAM_talkToMessenger();
                param.wParam = qyWmComm.Consts.CONST_qyWmParam_doTask;
                param.lParam = usOp;

                //
                dlgTalkProc_help.qyFuncs_Droid.talkToMessenger(pQyMc, peer_addr.idInfo.ui64Id, false, true, bStay_toAvCall,  param);

                //
                if  (  pQyMc.appParams.bDbg) {
                    pProcInfo.cfg.debugStatusInfo.bDbg_startToBreak = true;
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
                if (mAdapter.mFragment_map instanceof  Fragment_null)  {
                    Fragment_null fg = (Fragment_null) mAdapter.mFragment_map;
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
        pProcInfo.m_mapExt = ((MyApplication)getApplication()).m_mapExt;

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
        if  (  true ) {
            //
            pProcInfo.bStay_toAvCall = true;
            showInfo_open.qyFuncs.showInfo_open("test: bStay_toAv set to true");
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
        m_floatingView = findViewById(R.id.floating_fragment_container);
        if  ( m_floatingView!=null) {
            m_floatingView.setVisibility(View.GONE);
        }

        //
        this.m_str_title = getString(R.string.CONST_resId_appName);
        startActivity_login();
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

    private FrameLayout m_floatingView;
    //
    private float fv_lastX, fv_lastY;
    private float fv_downX, fv_downY;
    //
    public Fragment     m_floatingView_fg;


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
                    guiShare.GuiShare.pf_PostMessage.call(pQyMc.gui.hMainWnd, qyWmComm.Consts.CONST_qyWm_postComm, qyWmComm.Consts.CONST_qyWmParam_login_canceled, 0);
                    break;
                }

                //  logon

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
            return -1;
        }

        //
        do {

            //
            if (!pProcInfo.cfg.bTest_floatingView) return -1;

            if (this.m_floatingView_fg != null) return 0;

            //
            if (this.m_bReadyToQuit) {
                return -1;
            }

            //
            if (m_floatingView.getVisibility() != View.VISIBLE) {
                m_floatingView.setVisibility(View.VISIBLE);
            }

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
                break;

            }

            //
            if (0 != talker.addIsTalkUsage(pMisCnt, this, 0)) {
                break;
            }
            //
            //
            if (0 != addTo_tmpTools(this, talker.m_var, "makeFloatingView")) {
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

            //
            fg = Fragment_dlgTalk_av2.NewInstance(talker_ui64Id);
            if (fg == null) {
                break;
            }

            //
            this.m_floatingView_fg = fg;

            // 添加悬浮窗 Fragment77
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.floating_fragment_container, fg)
                    .commit();


            //
            test_initFloatingDrag();

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
        Log.d("at","closeFloatingView called");

        //
        if  (  this.m_floatingView_fg!=null)  {


            //  这里要取消会议
            if  (  this.m_floatingView_fg instanceof Fragment_dlgTalk_av2) {
                Fragment_dlgTalk_av2 fg  = (Fragment_dlgTalk_av2)this.m_floatingView_fg;
                fg.exitTaskAv();
            }

            // 先移除悬浮窗 Fragment77
            getSupportFragmentManager().beginTransaction()
                    .remove( this.m_floatingView_fg)
                    .commit();

            //
            if  (  this.m_floatingView_fg instanceof Fragment_dlgTalk_av2) {
                Fragment_dlgTalk_av2 fg  = (Fragment_dlgTalk_av2)this.m_floatingView_fg;

                dlgTalkProc.DLG_TALK_var m_var = fg.m_talker.m_var;

                //
                removeFrom_tmpTools(this,fg.m_talker.m_var, "closeFloatingView" );

                //
                if  (  fg.m_talker!=null){
                    fg.m_talker.removeIsTalkUsage(m_var.m_pMisCnt, this);
                }

            }

            //
            this.m_floatingView_fg=null;

            //
            m_floatingView.setVisibility(View.GONE);

        }

        return  0;
    }

    //
    private void test_initFloatingDrag() {
        //
        m_floatingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        fv_lastX = event.getRawX();
                        fv_lastY = event.getRawY();
                        fv_downX = fv_lastX;
                        fv_downY = fv_lastY;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float nowX = event.getRawX();
                        float nowY = event.getRawY();

                        float dx = nowX - fv_lastX;
                        float dy = nowY - fv_lastY;

                        // 移动View
                        v.setX(v.getX() + dx);
                        v.setY(v.getY() + dy);

                        fv_lastX = nowX;
                        fv_lastY = nowY;
                        return true;

                    case MotionEvent.ACTION_UP:
                        // 判断是否是点击（防止拖动影响点击）
                        if (Math.abs(event.getRawX() - fv_downX) < 10 &&
                                Math.abs(event.getRawY() - fv_downY) < 10) {
                            v.performClick();
                        }
                        return true;
                }

                return false;
            }
        });
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

    private void bindViews() {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;

        //
        m_layout_total = findViewById(R.id.layout_total);
        //txt_topbar = findViewById(R.id.txt_topbar);

        txt_map = findViewById(R.id.txt_map);
        txt_msg = findViewById(R.id.txt_msg);
        txt_contact = findViewById(R.id.txt_contact);
        txt_me = findViewById(R.id.txt_me);

        //
        if (pQyMc.appParams.bENABLE_MAP_TAB) {
            txt_map.setOnClickListener(this);
        } else {
            txt_map.setVisibility(View.GONE);
        }
        //
        txt_msg.setOnClickListener(this);
        txt_contact.setOnClickListener(this);
        txt_me.setOnClickListener(this);

        vPager = findViewById(R.id.vpager);
        vPager.setAdapter(mAdapter);
        vPager.addOnPageChangeListener(this);
        //
        if (pQyMc.appParams.bENABLE_MAP_TAB) {
            txt_map.performClick();
        } else {
            txt_msg.performClick();
        }
    }

    private void clearSelected() {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;

        txt_msg.setSelected(false);
        txt_contact.setSelected(false);
        txt_me.setSelected(false);
        //
        if (pQyMc.appParams.bENABLE_MAP_TAB) {
            txt_map.setSelected(false);
        }
    }

    public void onClick(View v) {
        ctxQyMc.CCtxQyMc pQyMc  = g_pQyMc;

        if  (  pQyMc.appParams.bENABLE_MAP_TAB  &&  v.getId()==R.id.txt_map) {
            navigateToTab(PAGE_FOUR);
        }
        else if (v.getId() == R.id.txt_msg) {
            navigateToTab(PAGE_ONE);
        } else if (v.getId() == R.id.txt_contact) {
            navigateToTab(PAGE_TWO);
        } else if (v.getId() == R.id.txt_me) {
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
        clearSelected();
        if (pQyMc.appParams.bENABLE_MAP_TAB && page == PAGE_FOUR) {

            txt_map.setSelected(true);
            vPager.setCurrentItem(PAGE_FOUR, false);
        } else if (page == PAGE_ONE) {
            txt_msg.setSelected(true);
            vPager.setCurrentItem(PAGE_ONE, false);
        } else if (page == PAGE_TWO) {
            txt_contact.setSelected(true);
            vPager.setCurrentItem(PAGE_TWO, false);
        } else if (page == PAGE_THREE) {
            txt_me.setSelected(true);
            vPager.setCurrentItem(PAGE_THREE, false);
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
    protected void onResume() {
        super.onResume();

        //
        isActive = true;

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
        int i = 1;
        super.onStop();
        //
        //Log.d( "qy","MainActivity  OnStop kk   k");
    }

    @Override
    protected void onDestroy() {

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
            switch (vPager.getCurrentItem()) {
                case PAGE_ONE:
                    clearSelected();
                    txt_msg.setSelected(true);
                    break;
                case PAGE_TWO:
                    clearSelected();
                    txt_contact.setSelected(true);
                    break;
                case PAGE_THREE:
                    clearSelected();
                    txt_me.setSelected(true);
                    break;
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {

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
