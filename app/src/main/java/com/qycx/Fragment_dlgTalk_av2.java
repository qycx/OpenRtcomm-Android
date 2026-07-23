package com.qycx;

import static com.qycx.anc.Consts.CONST_confMemType_grpMem;
import static com.qycx.anc.Consts.CONST_confMemType_speaker;
import static com.qycx.anc.Consts.CONST_usConfType_emergencyCommand;
import static com.qycx.anc.atFuncs.sendToRefreshRecentFriends;
import static com.qycx.base.Consts.CONST_channelType_media;
import static com.qycx.base.Consts.IO_TAG;
import static com.qycx.base.avCallProc.doTask_av_accept;
import static com.qycx.base.dlgTalkProc.qyFuncs_Droid.dlgTalk_bConfCompere;
import static com.qycx.base.dlgTalkProc_help.qyFuncs_Droid.dlgTalk_canSpeak;
import static com.qycx.base.dlgTalk_videoConference.qyFuncs_Droid.confOthers_sendStreamInfo;
import static com.qycx.base.dlgTalk_videoConference_public.qyFuncs.isActiveMem;
import static com.qycx.base.dlgTalk_videoConference_public.qyFuncs.isActiveMemsFull;
import static com.qycx.base.getTalkerDesc.Consts.*;
import static com.qycx.base.guiShare.Consts.*;
import static com.qycx.base.imCommType_defs.Consts.*;
import static com.qycx.base.isTalkerQProc.findIsTalker;
import static com.qycx.base.postMsg2MgrFunc.qyFuncs.postMsg2Mgr_mc;
import static com.qycx.base.postMsg2MgrFunc.qyFuncs.postMsgTask2Mgr_mc;
import static com.qycx.base.procSendFunc.qyFuncs.procSendTaskProcReq;
import static com.qycx.base.qmcProc.doChkTalkDraw;
import static com.qycx.base.qmcProc.isGroup;
import static com.qycx.base.qmcProc.pauseTaskAv;
import static com.qycx.base.qmcProc.resumeTaskAv;
import static com.qycx.base.qmcTaskInfo.Consts.*;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_refreshRecentFriendsReq;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_sendMedia;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_sendReq;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_sendTask;
import static com.qycx.base.qnmCommProc_mis.Consts.*;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_switchTransmissionMode;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.getChannelByType;
import static com.qycx.base.qyCommProc_open.Consts.CONST_fourcc_I420;
import static com.qycx.base.qyCusResHelp.qyFuncs.getResStr;
import static com.qycx.base.qyCusResPublic.Consts.CONST_usRunningStatus_online;
import static com.qycx.base.qyDefs.Consts.*;
import static com.qycx.base.qyMcMainDaemon_isCli.qyFuncs.postMsg2OutputQ_mc;
import static com.qycx.base.qyMcMain_global.g.g_pQyMc;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_closeTaskAv;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWm_postComm;
import static com.qycx.base.qyWndContentType.Consts.*;
import static com.qycx.base.showTaskStatusFunc.qyFuncs.showNotification;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;

import org.greenrobot.eventbus.EventBus;


import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.util.Size;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qycx.anc.ConfMember;
import com.qycx.anc.ConfMemberAdapter;
import com.qycx.anc.Consts;
import com.qycx.anc.atFuncs;
import com.qycx.base.CIsTalker;
import com.qycx.base.audioRecorder_android;
import com.qycx.base.ctxCaller;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.dlgTalkProc;
import com.qycx.base.dlgTalkProc_help;
import com.qycx.base.dlgTalkPublic;
import com.qycx.base.dlgTalk_procTask;
import com.qycx.base.dlgTalk_videoConference;
import com.qycx.base.doCmd_startAvCall;
import com.qycx.base.genericQueue;
import com.qycx.base.getTalkerDesc;
import com.qycx.base.guiShare;
import com.qycx.base.imCommType_defs;
import com.qycx.base.myDb;
import com.qycx.base.myrecycleFunc;
import com.qycx.base.postMsg2MgrFunc;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.qmcProc;
import com.qycx.base.qmcSharedObj;
import com.qycx.base.qmcTaskInfo;
import com.qycx.base.qmcVideoCapture;
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qnmResDefs;
import com.qycx.base.qyCommProc_open;
import com.qycx.base.qyCusResHelp;
import com.qycx.base.qyDefs;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.qyWmComm;
import com.qycx.base.qyWndContentType;
import com.qycx.base.qy_getuiNextTranNo;
import com.qycx.base.showInfo_open;
import com.qycx.base.statuscenter.messagebean.NetConnectEvent;
import com.qycx.base.statuscenter.messagebean.NetConnectStatus;
import com.qycx.base.talkDraw;
import com.qycx.base.test;
import com.qycx.base.tmpFunc;
import com.qycx.base.tmpResource;
import com.qycx.base.util.Log;
import com.qycx.base.util.ToastUtils;
import com.qycx.base.util.javatuples.Pair;
import com.qycx.base.util.javatuples.Triplet;
import com.qycx.smxj.myGLRsSurfaceView;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;


//
public class Fragment_dlgTalk_av2 extends Fragment implements PopupWindow.OnDismissListener {
    //private static String TAG = "Camera2VideoFragment";
    public static int REQUEST_VIDEO_PERMISSIONS = 1;
    public static String FRAGMENT_DIALOG = "dialog";
    public static String TAG = "Fragment_dlgTalk_av2";

    /** 供地图页查询组内在线状态；无活动会议时返回 null。 */
    @Nullable
    public static Map<Long, Short> tryGetGrpMemberRunningStatusMap(long grpId) {
        return null;
    }

    private int statusBarHeight;

    private boolean isChangeCamera = false;
    boolean canCloseCameraThread = false;

    //
    //public Activity_dlgTalk_av mOwner_wnd;
    public Activity mOwner_wnd;

    //
    public long talker_ui64Id;

    //
    public MainHandler mMainHandler;

    //  m_talker是由talker_ui64Id决定的
    public CIsTalker           m_talker = null;

    //
    public Handler m_bottomBarHandler = new Handler(Looper.getMainLooper());
    public Runnable m_runnableHideBottomBar;
    public Runnable m_runnableShowBottomBar;


    //
    public boolean isFirstOpenCamera;
    public int mLoopCtrl;

    //
    public MyTextureView mTextureView;
    private dlgTalk_av_Renderer mRenderer;

    //
    public ConstraintLayout bottomMenu;
    public ConstraintLayout topMenu;
    ImageView m_btn_request;
    TextView m_tv_ioStatusOut;
    TextView m_tv_ioStatusIn;
    TextView nvr_channel_name;
    ImageView nvr_control;
    ImageView back_main_video;
    ImageView back_to_video;
    TextView up_bt;
    TextView down_bt;
    TextView left_bt;
    TextView right_bt;
    TextView long_focus_bt;
    TextView short_focus_bt;
    ImageView m_btn_av;
    //
    TextView meeting_info;
    TextView meeting_launch;
    TextView meeting_account;
    TextView meeting_screen_info;
    TextView meeting_id;

    //
    // 模式切换整体布局
    RelativeLayout m_confMem_way_btn;
    // 图标
    ImageView m_confMem_way_iv;
    // 文字
    TextView m_confMem_way_tv;
    boolean ubNoVDownload = false;

    //
    LinearLayout m_confMem_rightMenu;
    RelativeLayout m_confMem_list_btn;      //  显示成员列表
    ImageView m_confMem_list_iv;            //  image
    TextView m_confMem_list_tv;             //  textView

    //  confMem_list_menu
    LinearLayout m_confMem_list_menu;
    RelativeLayout m_confMem_hide_list_btn;
    ImageView m_confMem_hide_list_iv;
    TextView m_confMem_hide_list_tv;
    //
    RelativeLayout m_confMem_request_btn;
    ImageView m_confMem_request_iv;         //  image
    TextView m_confMem_request_tv;          //  textView


    //
    FrameLayout min_viewport;
    Drawable pull_downDrawable;
    Drawable pull_upDrawable;
    //
    ConstraintLayout left_menu_relative;
    public ConstraintLayout more_info_menu;
    ConstraintLayout right_menu_relative;
    public PopupWindow popupWindow;
    NvrListAdapter nvrListAdapter;
    public ImageView btnLocalVideo;
    public ImageView more_tv;
    public ImageView btnChangeCamera;
    public TextView btnRotateScreen;

    //
    LinearLayout m_confMemListContainer;
    //
    // 切换人员列表显示/隐藏
    boolean m_isListVisible  =  false;

    //
    private GridView m_gv_confMemList;
    private ConfMemberAdapter m_confMemAdapter; // 保存适配器引用
    private List<ConfMember> m_confMems;        //  m_confMems  =  m_speakers  +  m_grpMems
    //
    private List<ConfMember>m_grpMems;          // 所有的组成员
    private List<ConfMember>m_speakers;         //  所有的发言人
    //
    private int m_gv_confMem_selectedPosition = -1;



    //zzy add 2024/10/15
    //增加语音视频切换按钮
    private TextView tvTips;
    private ImageView ivVoiceVideoSwitcher;

    camera_RenderThread mCameraRenderThread;
    //
    public int mSensorOrientation;
    public NetConnectStatus mNetStatus;

    //public com.qycx.base.qdc.decObj_a_mediaCodec decObj_a_mediaCodec;

    //
    public int m_st_iW;
    public int m_st_iH;

    //
    public int m_o_iW;
    public int m_o_iH;


    // These values are passed to us by the camera/render thread, and displayed in the UI.
    // We could also just peek at the values in the RenderThread Object, but we'd need to
    // synchronize access carefully.

    private int mCameraPreviewWidth_unused, mCameraPreviewHeight_unused;
    private float mCameraPreviewFps_unused;
    //
    private int mRectWidth, mRectHeight;
    private int mZoomWidth, mZoomHeight;
    private int mRotateDeg;

    //
    public class Tmp_autoSpeak {
        public boolean m_bAutoSpeak;
        public int m_nTimes_autoSpeak;
    }

    Tmp_autoSpeak m_autoSpeak;

    public static volatile boolean sReleaseInCallback = true;

    //
    public boolean  m_b3ddw  =  false;

    //
    public int showBottomMenu() {
        if (this.bottomMenu.getVisibility() != android.view.View.VISIBLE) {
            this.bottomMenu.setVisibility(android.view.View.VISIBLE);
        }
        return 0;
    }

    //
    long m_dwLastModifiedTickCnt_speakers;

    //
    public int gui_onTimer(Object p0, Object p1, Object p2) {
        //
        //Activity_dlgTalk_av owner = (Activity_dlgTalk_av) getActivity();
        //if (owner == null) return -1;
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (pQyMc == null) {
            Log.e("anc","pQyMc == null");
            return -1;
        }
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt   =  pProcInfo.pMisCnt;

        //
        if  ( this.m_talker==null)  return  -1;
        dlgTalkProc.DLG_TALK_var m_var = this.m_talker.m_var;//hTalk.m_var;
        if  (  m_var==null)  return  -1;

        //
        mLoopCtrl++;

        //
        {
            @SuppressLint("DefaultLocale") String strOut = String.format("↑%.2fM", pProcInfo.status.netStat.ins.uiOutSpeedInKbps / 1000.0);
            @SuppressLint("DefaultLocale") String strIn = String.format("↓%.2fM",  pProcInfo.status.netStat.ins.uiInSpeedInKbps  / 1000.0);
//            String strOut = "↑" + pProcInfo.status.netStat.ins.uiOutSpeedInKbps / 1024.0 + "M";
//            String strIn = "↓" + pProcInfo.status.netStat.ins.uiInSpeedInKbps / 1024.0 + "M";

            int outSpeed = pProcInfo.status.netStat.ins.uiOutSpeedInKbps;
            int inSpeed  = pProcInfo.status.netStat.ins.uiInSpeedInKbps;

            //      EventBus.getDefault().post(new ChangeNetspeedEvent(inSpeed, outSpeed));

            m_tv_ioStatusOut.setText(strOut);
            m_tv_ioStatusIn.setText(strIn);
            //
            if (m_var.is4k) {
//                Toast.makeText(owner, "不支持4K会议", Toast.LENGTH_SHORT).show();
                ToastUtils.showToast("不支持4K会议");
            }

            //if (0 == (mLoopCtrl % 3))
            {
                chkIfAvAlive();
                chkAutoSpeak(mLoopCtrl);
            }
            initMinButtonStates();
        }


        //
        //Log.d("anc","fg: loopCtrl "  +  mLoopCtrl);

        //
        if  (  m_dwLastModifiedTickCnt_speakers  !=  m_var.av.taskInfo.lastModifiedTickCnt_confKey )  {
            m_dwLastModifiedTickCnt_speakers =m_var.av.taskInfo.lastModifiedTickCnt_confKey;
            //
            List<ConfMember> tmp_list=new ArrayList<>();
            reloadSpeakers(tmp_list,m_var.addr.idInfo.ui64Id);
            //
            if  (  !m_speakers.equals(tmp_list))  {

                //
                gv_clearSelection();

                //
                m_speakers.clear();
                m_speakers.addAll(tmp_list);
                //
                m_confMems.clear();
                m_confMems.addAll(m_speakers);
                m_confMems.addAll(m_grpMems);
                //
                confMem_removeDup();

                //
                confMem_addTail();

                //
                sortMems();

                //
                m_confMemAdapter.notifyDataSetChanged();
            }
        }

        //
        if  (  pProcInfo.cfg.b_dbg) {
        }

        //
        //Log.d("anc","fg_av2:timer called "  +  mLoopCtrl);

        //
        int nInterval = 3;
        if  (0==( mLoopCtrl%nInterval)) {
            //
            int n = mLoopCtrl  /  nInterval;
            int nInterval_all  = 7;
            //
            if ( m_grpMems!=null )  {
                if  (  m_grpMems.size()  <  10  )  {
                    nInterval_all  =  3;        //  对人数较少的组，加快刷新速度
                }
            }
            //
            if  (  0== (  n %  nInterval_all  )  )  {
                //
                confMem_startToRefreshRecentFriends_all();
            }
            else {
                //
                confMem_startToRefreshRecentFriends();
            }
        }

        //
        if  (  false  ) {
            int i;
            for (i = 0; i < m_confMems.size(); i++) {
                ConfMember mem = m_confMems.get(i);
                Log.d("anc", "confMem " + i + ": type " + mem.iConfMemType + ": index " + mem.usIndex_activeMem);
            }
        }

        //
        return 0;
    }


    //////////////////////

    //
    int confMem_startToRefreshRecentFriends_all() {
        // 1. 基础检查
        if (m_confMems == null || m_confMems.isEmpty()) return -1;
        // GridView gridView = m_gv_confMemList; // 不再依赖 GridView 的可视范围

        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        // ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo(); // 如果需要 logging 可以留着

        Log.d("anc", "confMem_startToRefresh: Start to retrieve all members status (" + m_confMems.size() + ")");

        // 2. 初始化请求包
        qnmCommProc_mis.REFRESH_RECENTFRIENDS_REQ rrr = new qnmCommProc_mis.REFRESH_RECENTFRIENDS_REQ();
        rrr.uiType = CONST_imCommType_refreshRecentFriendsReq;
        rrr.usCnt = 0; // 当前包计数器

        // 获取单个包最大容量 (取决于 mems 数组的长度，通常在结构体定义中固定，例如 20 或 50)
        int maxPerBatch = rrr.mems.length;
        int batchIndex = 0; // 用于日志显示的批次号

        // 3. 遍历完整列表
        for (int i = 0; i < m_confMems.size(); i++) {
            ConfMember mem = m_confMems.get(i);
            if (mem == null) continue;

            // 过滤条件：
            // A. 跳过发言人 (ConfMemType_speaker)，因为发言人状态由 AV 流决定
            // B. 跳过无效 ID (如 footer "没有更多了")
            if (mem.iConfMemType == CONST_confMemType_speaker || mem.ui64Id == 0) {
                continue;
            }

            // 4. 将成员加入当前包
            if (rrr.usCnt < maxPerBatch) {
                rrr.mems[rrr.usCnt].ui64Id = mem.ui64Id;
                // 如果需要记录上次刷新时间，可以在这里赋值
                // rrr.mems[rrr.usCnt].tLastRefreshedTime_misServ = mem.tLastRefreshedTickCnt;
                rrr.usCnt++;
            }

            // 5. 检查是否满包
            // 如果当前包已满，立即发送
            if (rrr.usCnt >= maxPerBatch) {
                // 调用 atFuncs 发送
                atFuncs.sendToRefreshRecentFriends(rrr, "Batch_" + batchIndex);
                batchIndex++;

                // 重置请求包，准备下一批
                rrr = new qnmCommProc_mis.REFRESH_RECENTFRIENDS_REQ();
                rrr.uiType = CONST_imCommType_refreshRecentFriendsReq;
                rrr.usCnt = 0;
            }
        }

        // 6. 发送剩余的数据 (尾包)
        // 循环结束后，如果 rrr 中还有未发送的数据 (cnt > 0)，发送最后一批
        if (rrr.usCnt > 0) {
            atFuncs.sendToRefreshRecentFriends(rrr, "Batch_Last");
        }

        return 0;
    }




    ///////////////////
    //
    int confMem_startToRefreshRecentFriends()
    {
        //
        GridView gridView = m_gv_confMemList;
        if  (  gridView==null)  return -1;

        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt=pProcInfo.pMisCnt;

        //
        Log.d("anc","confMem_refreshState called");

        //
        do {
            //
            qnmCommProc_mis.REFRESH_RECENTFRIENDS_REQ rrr = new qnmCommProc_mis.REFRESH_RECENTFRIENDS_REQ();
            rrr.uiType = CONST_imCommType_refreshRecentFriendsReq;
            //req.tLastRefreshedTime_misServ  =  pMisCnt->refreshRecentFriends.tLastRefreshedTime_misServ;

            // 获取第一个和最后一个可见条目的位置
            int firstVisiblePosition = gridView.getFirstVisiblePosition();
            int lastVisiblePosition = gridView.getLastVisiblePosition();

            // 遍历所有可见条目
            for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                // 方法1：通过适配器获取数据
                Object item = gridView.getAdapter().getItem(i);
                //
                ConfMember mem = m_confMems.get(i);
                if (mem == null) break;
                //
                Log.d("anc", "可见条目 " + i + ": " + mem.ui64Id + " " + mem.desc);

                //
                if (mem.iConfMemType == CONST_confMemType_speaker) {  //  发言人肯定在线
                    continue;
                }

                //
                if (rrr.usCnt < rrr.mems.length) {
                    rrr.mems[rrr.usCnt].ui64Id = mem.ui64Id;
                    //req.mems[req.usCnt].tLastRefreshedTime_misServ = pQMemObj -> tLastRefreshedTickCnt_misServ;
                    rrr.usCnt++;
                }

                //
                continue;
            }

            //
            if (0==rrr.usCnt) break;


            //
            sendToRefreshRecentFriends(rrr,"confMem_startToRefresh.l520");



            //
        } while (false);

        //
        return  0;
    }




    //
    int m_nLostVideo = 0;

    //
    int chkIfAvAlive() {
        //
        //
        do {
            ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT  pMisCnt  =  pProcInfo.pMisCnt;

            //
            /*
            Activity_dlgTalk_av owner = this.mOwner_wnd;
            if  (  owner==null) break;
            if  (  owner.mOwner_hTalk==null)break;
            Activity_dlgTalk talk = owner.mOwner_hTalk.get();
            if(talk==null)break;
             */
            //
            CIsTalker talk=findIsTalker(pMisCnt,this.talker_ui64Id);
            if  (  talk==null)  break;

            //
            dlgTalkProc.DLG_TALK_var m_var = talk.m_var;
            if  (  m_var==null)break;

            //
            if  (  pProcInfo.bUseSmxj)  {
                //  对双目相机，不需要检查音视频是否收到
                return  0;
            }

            //
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, pProcInfo, m_var.m_talkAvVar.m_index_taskInfo1, "");
            if (pTaskInfo == null) {
                m_nLostVideo  ++  ;
            }
            else {
                qmcTaskInfo.QMC_taskData_common taskData = pTaskInfo.getTaskData();
                if (taskData == null) break;


                //
                long dwTickCnt = myGetTickCount(null);
                long iElapseInMs_a = dwTickCnt - taskData.tmpStatus.lastTickCnt_dec_a;
                long iElapseInMs_v  =  dwTickCnt  -  taskData.tmpStatus.lastTickCnt_dec_v;
                if (Math.abs(iElapseInMs_a) > 3000  &&  Math.abs(iElapseInMs_v)>3000)
                {
                    //
                    if  (  true  ) {
                        qnmCommProc_mis.MSGR_ADDR addr = new qnmCommProc_mis.MSGR_ADDR();
                        addr.idInfo.ui64Id = m_var.av.taskInfo.idInfo_starter.ui64Id;
                        procSendTaskProcReq(pMisCnt, CONST_qyCmd_sendMedia, CONST_imOp_recv_applyForChkTaskAlive, m_var.av.taskInfo.tStartTime_org, m_var.av.taskInfo.uiTranNo_org, CONST_imCommType_transferAvInfo, 0, 0, null, 0, true, "", addr);
                    }

                    //
                    m_nLostVideo++;
                }
                else {
                    m_nLostVideo = 0;
                }
            }


            //
            int max_nLostVideo  =  10;
            if  (  pProcInfo.bUse100k)  {
                max_nLostVideo=40;
            }

            //
            if (m_var.m_talkAvVar.m_nErrs_hTalk_av_not_matched > 130 || m_nLostVideo > max_nLostVideo) {

                //
                if  (  true  ) {
                    //
                    m_var.av.bAutoCancel_lostVideo = true;
                    m_var.av.dwTickCnt_autoCancel_lostVideo = myGetTickCount(null);
                    m_var.av.bDoneOnce_autoCancel_lostVideo = false;

                    //
                    String str;
                    str = "m_nLostVideo (" + m_nLostVideo + ") > 10, autoCancel";
                    showInfo_open.qyFuncs.showInfo_open(str);

                    //
                    this.doAutoCancel();
                }
                else  {
                    Log.d("qy","for test: cancel checking m_nLostVideo");
                }

                //
            }

            //
            int  maxTimes_swapBuffersFailed = 5;
            //
            if  (  pProcInfo.cfg.b_dbg)  {
                if  (  false  ) {
                    maxTimes_swapBuffersFailed = 50000;
                    Log.d("anc", "for test: maxTimes_swapBuffersFailed set to " + maxTimes_swapBuffersFailed);
                }
            }
            //
            if  (  pProcInfo.m_camera_n_swapBuffersFailed  >  maxTimes_swapBuffersFailed)  {
                //
                pProcInfo.m_camera_n_swapBuffersFailed=0;

                //
                String str;
                str =  "fg_talk_av2: m_bSwapBuffersFailed true, autoCancel";
                Log.e("anc",str);
                Toast.makeText(MyApplication.getContext(),"swapBufferFailed, autoCancel", Toast.LENGTH_SHORT).show();
                //
                this.doAutoCancel();
            }


        } while (false);

        //
        return 0;
    }


    //
    int chkAutoSpeak(int loopCtrl) {

        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if(pQyMc==null) return -1;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if(pProcInfo==null) return -1;
        qmCommFunc_mis.MIS_CNT pMisCnt  =  pProcInfo.pMisCnt;
        //
        boolean bDbg = false;
        String str;

        if (pProcInfo.av.m_bInSleepMode) {
            Log.d("chkAutoSpeak: bInSleep true, do nothing");
            return 0;
        }

        do {
            //
            long dwTickCnt = myGetTickCount(null);
            long iDiffInMs = dwTickCnt - pProcInfo.av.localAv.dwLastTickCnt_vCap;
            if (iDiffInMs > 10000) {
                if (loopCtrl > 30) {       //  忽略掉前面的错误判断
                    str = "chkAutoSpeak: 摄像头没有正常工作. 忽略";
                    Context context = MyApplication.getContext();
                    Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
                }
                //
                return 0;
            }
            iDiffInMs = dwTickCnt - pProcInfo.av.localAv.dwLastTickCnt_aCap;
            if (iDiffInMs > 10000) {
                if (loopCtrl > 30) {       //  忽略掉前面的错误判断
                    str = "chkAutoSpeak: 麦克风没有正常工作. 忽略";
                    Context context = MyApplication.getContext();
                    Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
                }
                //
                return 0;
            }

            //
            //Activity_dlgTalk dlgTalk = mOwner_wnd.mOwner_hTalk.get();
            CIsTalker talker = findIsTalker(pMisCnt, this.talker_ui64Id);
            if (talker == null) break;
            dlgTalkProc.DLG_TALK_var m_var = talker.m_var;
            //
            if (m_autoSpeak.m_bAutoSpeak) {
                if (m_autoSpeak.m_nTimes_autoSpeak > 30) {
                    m_autoSpeak = new Tmp_autoSpeak();
                    //
                    m_var.av.m_bState_canSpeak = false;
                } else {

                    //
                    m_autoSpeak.m_nTimes_autoSpeak++;

                    //
                    boolean canSpeak = dlgTalk_canSpeak(m_var.addr.idInfo.ui64Id);
                    if (!canSpeak) {
                        //
                        if (isActiveMem(null, m_var.av.taskInfo.iTaskId, pMisCnt.idInfo)) {
                            //
                            Log.d("anc", "已经在话筒中，却tn不匹配，所以需要主动发言");
                            //
                            do_request(true);

                        } else {
                            //
                            if (isActiveMemsFull(null, m_var.av.taskInfo.iTaskId, true)) {
                                if (pMisCnt.idInfo.ui64Id == 0)
                                    Log.e("anc", "chkAutoSpeak: too many speakers, don't call do_request");
                            } else {
                                //
                                Log.d("anc", "主动发言");
                                //
                                do_request(true);
                            }
                        }
                    } else {
                        m_autoSpeak = new Tmp_autoSpeak();
                    }

                }
            } else {
                //
                if (m_var.av.m_bState_canSpeak) {
                    boolean canSpeak = dlgTalk_canSpeak(m_var.addr.idInfo.ui64Id);
                    if (!canSpeak) {
                        m_autoSpeak.m_bAutoSpeak = true;
                    }
                } else {
                    if (m_var.addr.uiObjType != CONST_objType_imGrp) {
                        boolean canSpeak = dlgTalk_canSpeak(m_var.addr.idInfo.ui64Id);
                        if (!canSpeak) {
                            do_request(true);
                        }
                    }

                }
            }

        }  while(false);

        //
        return 0;

    }


    //
    /***
     * 关闭窗口！
     * @return
     */

    public int doAutoCancel() {
        int  iErr  =  -1;
        ctxQyMc.CCtxQyMc  pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt=pProcInfo.pMisCnt;

        do {
            CIsTalker talker = findIsTalker(pMisCnt, this.talker_ui64Id);
            if  (  null==talker )  break;

            dlgTalkProc.DLG_TALK_var m_var = talker.m_var;//this.mOwner_hTalk.get().get_pm_var();
            String str;

            //
            //str = "dlgTalk_av.doAutoCancel, " + this.tn_dlg;
            //Log.d(str);

            //
            Object  hWnd  =  this.getActivity();
            int  iWndType  =  0;
            if  (  (hWnd instanceof  Activity_dlgTalk_av  )  ) {
                iWndType = CONST_qyWndContentType_talk_av;
                //  自动关闭视频窗口
                guiShare.GuiShare.pf_PostMessage_new.get().call(hWnd, iWndType, CONST_qyWm_postComm, qyWmComm.Consts.CONST_qyWmParam_closeWnd, 0);
            }
            else  if  (  hWnd instanceof MainActivity) {
                iWndType = CONST_qyWndContentType_mainWnd;
                //  自动关闭视频窗口
                guiShare.GuiShare.pf_PostMessage_new.get().call(hWnd, iWndType, CONST_qyWm_postComm, qyWmComm.Consts.CONST_qyWmParam_closeTaskAv, 0);

            }
            else  {

                Log.d("at","fg_talk_av2.doAutoCancel failed, hWnd err. l825");
                break;
            }


            //
            iErr  =  0;
        } while (false);

        return iErr;
    }






    //
    public int waitUntilTextureViewAvailable(Object p0, Object p1, Object p2) {
        int iErr = -1;
        //
        //Activity_dlgTalk_av owner = (Activity_dlgTalk_av) getActivity();
        //Activity_dlgTalk hTalk = owner.mOwner_hTalk.get();

        String str;

        long tickCnt  = myGetTickCount(null);
        long  iDiffInMs  =  tickCnt  -  tickCnt_create;
        str = "fg_dlgTalk_av2, waitTextureViewAvailable called";
        Log.d(str);

        //
        if (!mTextureView.isAvailable()) {
            Log.d("fg_dlgTalk_av,gui_onTimer, textureView.isAvail is false");

            return 0;
        }

        //
        dlgTalkProc.DLG_TALK_var m_var = this.m_talker.m_var;//hTalk.m_var;

        //
        if (m_var.m_share_eglContext == null) {
            Log.d("fg_dlgTalk_av,gui_onTimer, talk.m_share_eglContext is null");
            return 0;
        }

        Object hWnd  =  this;//owner.mOwner_hTalk;
        int  iWndType  =  CONST_fgType_talk_av2;//CONST_qyWndContentType_talker;

        if (m_var.av.taskInfo.bTaskExists) {
            if (!m_var.m_bNeedResumeTaskAv) {
                //
                pauseTaskAv( hWnd, iWndType,false,"waitTexViewAvail");


            }

            if (m_var.m_bNeedResumeTaskAv) {
                //
                resumeTaskAv(hWnd,iWndType,"waitTexViewAvail");
                iErr = 0;
                return iErr;
            }

            //
            iErr = 0;
            return iErr;
        }

        //
        if (m_var.av.bNeedDoTaskAv) {
            //
            m_var.av.bNeedDoTaskAv = false;

            //
            /*
            if  (  m_var.m_talkAvVar.m_index_taskInfo1  ==  0)  {
                Log.e("at","waitUntilTextureViewAvailable failed, l917: index_taskInfo is 0");
                return  -1;
            }

             */


            //
            int iTaskId = 0;
            ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, pProcInfo, m_var.m_talkAvVar.m_index_taskInfo1, "");
            if (pTaskInfo != null) {
                if  (  pTaskInfo.bUsed) {
                    iTaskId = pTaskInfo.var.iTaskId;
                }
            }

            //
            doTask_av(m_var.m_talkAvVar.m_iCmd, iTaskId);

            //
            if  (  pProcInfo.cfg.bUseAs_SCREEN_ORIENTATION_LANDSCAPE) {
                //
                Activity owner = getActivity();
                if  (  owner instanceof  Activity_dlgTalk_av) {
                    Activity_dlgTalk_av hTalk_av = (Activity_dlgTalk_av)owner;
                    //
                    hTalk_av.upsetPortraitOrLandscapeLayout();
                }
            }

        }

        //
        iErr = 0;

        str = String.format("fg_dlgTalk_av2, waitTextureViewAvailable leaves " + iErr);
        showInfo_open.qyFuncs.showInfo_open(str);
        //
        return iErr;
    }

    //
    public int doTask_av(int iCmd, int iTaskId) {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

        //boolean bDbg = false;
        String str;

        //
        //boolean b3D = false;
        //  2017/07/17
        boolean ucbAvConsole = false;
        //Activity_dlgTalk_av owner = (Activity_dlgTalk_av) getActivity();
        //decObj_a_mediaCodec = new decObj_a_mediaCodec();
        //decObj_a_mediaCodec.m_bTest = true;
        //decObj_a_mediaCodec.testExtractMpegFrames(this.mOwner_wnd);

        if (!mTextureView.isAvailable()) {
            str = "fg_dlgTalk_av.doTask_av failed, textureView.isAvail is false";
            Log.d(str);

            return iErr;
        }

        //
        test.testCls_Droid.testChk("doTask_av");

        //
        CIsTalker talker = findIsTalker(pMisCnt,this.talker_ui64Id);
        if  (  talker==null)  {
            return  -1;
        }
        dlgTalkProc.DLG_TALK_var m_var = talker.m_var;

        //
        switch (iCmd) {
            case tmpResource.IDC_BUTTON_av: {
                doCmd_startAvCall.qyFuncs_Droid.confCli_doCmd_startAvCall(null, this, m_var.m_talkAvVar.m_Level, false, false, null);
                //if  (  mOwner_wnd.mOwner_hTalk!=null)
                {
                    //Activity_dlgTalk hTalk  =  mOwner_wnd.mOwner_hTalk.get();
                    //if  (  hTalk!=null)
                    {
                        //dlgTalkProc.DLG_TALK_var  m_var  =  hTalk.m_var;
                        if  (  m_var.av.taskInfo.bTaskExists  ) {
                            m_var.m_talkAvVar.m_index_taskInfo1  =  m_var.av.taskInfo.iIndex_taskInfo;
                            //
                            str  =  "fg_dlgTalk_av2.doTask_av: startAvCall ok, set index_taskInfo " + m_var.m_talkAvVar.m_index_taskInfo1;
                            showInfo_open.qyFuncs.showInfo_open(str);
                        }
                    }
                }
            }
            break;
            case tmpResource.IDC_BUTTON_av_accept: {
                int index_taskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth(null, pProcInfo, iTaskId);
                qmcTaskInfo.QMC_TASK_INFO pTaskInfo = pProcInfo.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, index_taskInfo, "doTask_av");
                if (pTaskInfo == null) return iErr;

                dlgTalkPublic.PARAM_dlgTalk_procTask param = new dlgTalkPublic.PARAM_dlgTalk_procTask();
                //memset(&param, 0, sizeof(param));
                param.bManualCmd = true;        //  2017/02/12
                //pFuncs->pf_dlgTalk_procTask1(  hDlgTalk,  pDlgTalkVar->guiData.av.iTaskId,  CONST_imOp_dual_permit,  NULL,  &param  );
                Fragment fg = this;
                dlgTalk_procTask.qyFuncs_Droid.dlgTalk_procTask1(fg, m_var, pTaskInfo.var.iTaskId, (short) CONST_imOp_dual_permit, null, param);

                //Activity_dlgTalk dlgTalk = mOwner_wnd.mOwner_hTalk.get();
                //if (dlgTalk != null)
                {
                    //Activity_dlgTalk hTalk  =  dlgTalk;
                    //dlgTalkProc.DLG_TALK_var m_var = hTalk.m_var;
                    //
                    if  (  m_var.av.taskInfo.bTaskExists  ) {
                        m_var.m_talkAvVar.m_index_taskInfo1  =  m_var.av.taskInfo.iIndex_taskInfo;
                        //
                        str  =  "fg_dlgTalk_av2.doTask_av: dlgTalk_procTask1.dual_permit ok, set index_taskInfo " + m_var.m_talkAvVar.m_index_taskInfo1;
                        showInfo_open.qyFuncs.showInfo_open(str);
                    }
                    //
                    if (m_var.av.bAutoCancel_lostVideo) {
                        m_var.av.bAutoCancel_lostVideo = false;

                        //
                        if (m_var.av.m_bState_canSpeak) {
                            m_autoSpeak.m_bAutoSpeak = true;
                            m_autoSpeak.m_nTimes_autoSpeak++;

                            //
                            do_request(true);
                        }
                    }
                    if (!m_var.av.m_bState_canSpeak) {
                        //
                        do_request(false);
                    }
                }
            }
            break;
            default:

                Log.d("not finished, unknown iCmd");
                break;
        }

        //
        confMem_list_btn_update();

        //
        iErr = 0;
        return iErr;
    }


    //
    public int updateRequestButtonStatus() {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt =pProcInfo.pMisCnt;

        //Activity_dlgTalk dlgTalk = mOwner_wnd.mOwner_hTalk.get();
        //if  (  dlgTalk==null)  return -1;
        //dlgTalkProc.DLG_TALK_var m_var = dlgTalk.m_var;
        //
        CIsTalker talker = findIsTalker(pMisCnt,this.talker_ui64Id);
        if  (  talker==null)  {
            return  -1;
        }
        dlgTalkProc.DLG_TALK_var m_var = talker.m_var;


        //this.m_btn_request
        boolean bState_canSpeak = dlgTalk_canSpeak(m_var.addr.idInfo.ui64Id);

        if (bState_canSpeak) {
            m_btn_request.setImageResource(R.drawable.stop_saying);
        } else {
            m_btn_request.setImageResource(R.drawable.saying);

        }
        return 0;
    }


    //
    public static int initVideoCapDev(Object hWnd, int iWndContentType, String displayName_selected, qyCommProc_open.BITMAPINFOHEADER pBih_suggested, qmcSharedObj.CAP_procInfo_bmpCommon pCapBmp) {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        String str;
        Fragment_dlgTalk_av2 fg = null;

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
        if (iWndContentType == CONST_qyWndContentType_talker) {
            Activity_dlgTalk dlgTalk = (Activity_dlgTalk) hWnd;//((WeakReference<Activity_dlgTalk>) hTalk).get();
            dlgTalkProc.DLG_TALK_var m_var = dlgTalk.m_talker.m_var;
            if (!guiShare.GuiShare.pf_IsWindow.call(m_var.m_hTool_dlgTalk_av)) return -1;
            Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) m_var.m_hTool_dlgTalk_av;
            if (dlgTalk_av.m_fg_dlgTalk_av2 == null) return -1;
             fg = dlgTalk_av.m_fg_dlgTalk_av2;
        }
        else  if  (iWndContentType ==  CONST_fgType_talk_av2  )  {
            fg = ( Fragment_dlgTalk_av2) hWnd;
        }
        else  {
              Log.d("at","initVideoCapDev failed, wndType err");
              return  -1;
        }
        //
        CIsTalker talker = findIsTalker(pProcInfo.pMisCnt,fg.talker_ui64Id);
        if  (  talker == null)  {
            return  -1;
        }
        dlgTalkProc.DLG_TALK_var m_var  =  talker.m_var;


        //
        if (!fg.mTextureView.isAvailable()) return -1;

        //
        int w, h;
        //w = fg.mTextureView.Width;
        //h = fg.mTextureView.Height;
        w = pBih_suggested.biWidth;
        h = pBih_suggested.biHeight;

        if (null == m_var.m_share_eglContext) return iErr;
        int surfaceOrientation = fg.mOwner_wnd.getWindowManager().getDefaultDisplay().getRotation();

        boolean bFront = true;
        if (m_var.av.taskInfo.bTaskExists) bFront = !m_var.av.taskInfo.bUse_backCamera;
        pCapBmp.m_bFront = bFront;

        fg.mCameraRenderThread = new camera_RenderThread(fg.mMainHandler, m_var.m_share_eglContext, pCapBmp.iIndex_sharedObj, bFront, surfaceOrientation);
        fg.mCameraRenderThread.start();
        //
        if (0 != fg.mCameraRenderThread.waitUntilReady()) {
            return iErr;
        }
        //
        fg.mSensorOrientation = fg.mCameraRenderThread.myCamera.mSensorOrientation;
        //
        //pCapBmp.mSensorOrientation = fg.mSensorOrientation;


        //
        //
        Log.d("initVideoCapDev.851");


        //fg.me_tex
        //
        SurfaceTexture st = null;// fg.m_st_test;
        //st=new SurfaceTexture(CTalkDraw.me_texId);
        st = talkDraw.CTalkDraw.me_st;
        if (st == null) {
            Log.d("initVideoCapDev failed, me_st is null");
            return iErr;
        }

        //
        fg.m_st_iW = fg.mCameraRenderThread.mCameraPreviewWidth;
        fg.m_st_iH = fg.mCameraRenderThread.mCameraPreviewHeight;

        //
        int angle_z = 0;
        Triplet<Integer, Integer, Integer> o_info = myrecycleFunc.qyFuncs_Droid.get_tex_o_info(fg.mSensorOrientation, surfaceOrientation, fg.mCameraRenderThread.mCameraPreviewWidth, fg.mCameraRenderThread.mCameraPreviewHeight);
        angle_z = o_info.getValue0();
        fg.m_o_iW = o_info.getValue1();
        fg.m_o_iH = o_info.getValue2();

        //
        st.setDefaultBufferSize(fg.m_st_iW, fg.m_st_iH);

        if (bFront && fg.isFirstOpenCamera) {
            fg.mCameraRenderThread.mHandler.sendSurfaceAvailable(st, true);

        } else {

            fg.mCameraRenderThread.mHandler.sendSurfaceAvailable(st, false);

            fg.isFirstOpenCamera = false;
        }

        //
        Log.d("initVideoCapDev.910");

        //
        iErr = 0;

        //
        Log.d("initVideoCapDev leaves");

        //
        return iErr;
    }

    public static int exitVideoCapDev(Object hWnd, int iWndContentType) {
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
        Fragment_dlgTalk_av2 fg = null;

        //
        if (iWndContentType == CONST_qyWndContentType_talker) {
            Activity_dlgTalk dlgTalk = (Activity_dlgTalk) hWnd;
            if (null == dlgTalk.m_talker.m_var.m_hTool_dlgTalk_av) return -1;
            Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) dlgTalk.m_talker.m_var.m_hTool_dlgTalk_av;
            if (dlgTalk_av.m_fg_dlgTalk_av2 == null) return -1;
            fg = dlgTalk_av.m_fg_dlgTalk_av2;
        }
        else if ( iWndContentType==CONST_fgType_talk_av2){
            fg = (Fragment_dlgTalk_av2) hWnd;
        }
        else  {
              Log.d("at","exitVideoCapDev failed, iWndType err");
              return  -1;
        }

        if (fg.mCameraRenderThread != null) {
            camera_RenderThread.camera_RenderHandler rh = fg.mCameraRenderThread.getHandler();
            if (rh != null) {
                rh.sendShutdown();
            }
            try {
                fg.mCameraRenderThread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
            fg.mCameraRenderThread = null;

        }


        iErr = 0;
        return iErr;
    }

    //
    public static boolean bGetCapturePara(Object hWnd, int iWndContentType, qyCommProc_open.QY_VIDEO_HEADER pVh_org) {
        boolean bRet = false;
        Fragment_dlgTalk_av2 fg = null;

        do {
            if (iWndContentType == CONST_qyWndContentType_talker) {
                Activity_dlgTalk dlgTalk = (Activity_dlgTalk) hWnd;//((WeakReference<Activity_dlgTalk>) hTalk).get();
                if (!guiShare.GuiShare.pf_IsWindow.call(dlgTalk.m_talker.m_var.m_hTool_dlgTalk_av))
                    return false;
                Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) dlgTalk.m_talker.m_var.m_hTool_dlgTalk_av;
                if (dlgTalk_av.m_fg_dlgTalk_av2 == null) return false;
                fg = dlgTalk_av.m_fg_dlgTalk_av2;
            } else if (iWndContentType == CONST_fgType_talk_av2) {
                fg = (Fragment_dlgTalk_av2) hWnd;
            } else {
                break;
            }

            //
            qyCommProc_open.BITMAPINFOHEADER bih_org = new qyCommProc_open.BITMAPINFOHEADER();
//        qyMemoryFunc.qyFuncs.mymemset((Object) ( byte)bih_org, 0, sizeof(BITMAPINFOHEADER));
            /**/

            if (fg.mCameraRenderThread == null) return bRet;

            bih_org.biWidth = fg.m_o_iW;
            bih_org.biHeight = fg.m_o_iH;
            bih_org.biCompression = CONST_fourcc_I420;

            pVh_org.bih = bih_org;


            //
            bRet = true;

        } while(false);

        return bRet;
    }

    public static int startCapture(Object hWnd, int iWndContentType) {

        return 0;
    }

    public static int stopCapture(Object hWnd, int iWndContentType) {
        return 0;
    }


    //
    public static Fragment_dlgTalk_av2 NewInstance(long ui64Id) {
        //
        if  (  ui64Id==0)  return  null;
        //
        Fragment_dlgTalk_av2  fg  =  new Fragment_dlgTalk_av2();
        //
        fg.talker_ui64Id=ui64Id;

        //
        if  (  false  ) {
            String str;
            str = "fg_av2.NewInstance called " + ui64Id;
            showInfo_open.qyFuncs.showInfo_open(0, 0, "", str);
        }

        //
        return  fg;
    }


    //
    public ViewStub  m_viewStub_myGlRsSurfaceView=null;
    public myGLRsSurfaceView mGLSurfaceView=null;
    public Tmp_rs  m_rs  =  null;//new Tmp_rs();


    //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        Log.d(TAG, "onCreate");

        isFirstOpenCamera = true;
        mOwner_wnd = getActivity();
    }


    private void getMeetingInfo() {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if(pQyMc == null) return;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;


        CIsTalker talker = findIsTalker(pMisCnt,this.talker_ui64Id);
        if  (  talker == null)  return;


        int iIndex_taskInfo = -1;

        //
        dlgTalkProc.DLG_TALK_var m_var = talker.m_var;
        if (m_var.av.taskInfo.bTaskExists) {
            iIndex_taskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth(null, pProcInfo, m_var.av.taskInfo.iTaskId);
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, pProcInfo, iIndex_taskInfo, "");
            if (pTaskInfo != null) {
                qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
                if (pTaskData.uiType == CONST_taskDataType_conf) {
                    qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
                    qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();

                    //
                    String str_meeting_info;
                    str_meeting_info="会议信息";
                    if  (  m_var.av.taskInfo.usConfType  ==  CONST_usConfType_emergencyCommand  )  {
                        str_meeting_info="应急指挥";
                    }
                    str_meeting_info  +=  ": "  +  videoConference.usMaxSpeakers;
                    //
                    meeting_info.setText(str_meeting_info);

                    //
                    //  pTc.grp.idInfo_initiator;//发起人id
                    //qyFuncs.getTalkerDesc(pMisCnt, pActiveMem_from.avStream.idInfo, pDesc, Consts.cntof_desc, null, 0);

                    qmcTaskInfo.TMP_desc tmpDesc = new qmcTaskInfo.TMP_desc();
                    String desc = tmpDesc.desc;


                    qyCommProc_open.getTalkerDescParams getTalkerDescParams = new qyCommProc_open.getTalkerDescParams();
                    getTalkerDescParams.pMisCnt = pMisCnt;
                    getTalkerDescParams.idInfo = m_var.av.taskInfo.idInfo_initiator;
                    getTalkerDescParams.talkerDesc = desc;
                    getTalkerDescParams.talkerDescCnt = cntof_desc;
                    getTalkerDescParams.shortName = null;
                    getTalkerDescParams.shortNameCnt = 0;
                    getTalkerDesc.qyFuncs.getTalkerDesc(getTalkerDescParams);
                    //
                    Log.d("召集人:" + getTalkerDescParams.talkerDesc);
                    //
                    // 修改为只显示名字部分
                    String meeting_launch_people_str = "召集人:";
                    Pair<Integer, Object> result = getTalkerDesc.qyFuncs.getTalkerDesc(m_var.av.taskInfo.idInfo_initiator);
                    if (result != null && result.getValue1() instanceof qnmCommProc_mis.QY_MESSENGER_REGINFO) {
                        qnmCommProc_mis.QY_MESSENGER_REGINFO regInfo = (qnmCommProc_mis.QY_MESSENGER_REGINFO) result.getValue1();
                        if (regInfo.syr != null && !regInfo.syr.isEmpty()) {
                            meeting_launch_people_str += regInfo.syr;
                        } else {
                            // 如果没有名字信息，则使用原来的方式
                            meeting_launch_people_str += getTalkerDescParams.talkerDesc;
                        }
                    } else {
                        // 如果获取失败，则使用原来的方式
                        meeting_launch_people_str += getTalkerDescParams.talkerDesc;
                    }
                    meeting_launch.setText(meeting_launch_people_str);
                    //
                    Log.d("召集人ID:" + m_var.av.taskInfo.idInfo_initiator.ui64Id);
                    //
                    // 修改判断条件，不仅检查ui64Id，还要检查任务是否存在
                    if (m_var.av.taskInfo.idInfo_initiator.ui64Id == 0 && !m_var.av.taskInfo.bTaskExists) {
                        meeting_id.setVisibility(android.view.View.INVISIBLE);
                        meeting_launch.setVisibility(android.view.View.INVISIBLE);
                        meeting_screen_info.setVisibility(android.view.View.INVISIBLE);
                    } else {
                        // 确保任务存在时，这些控件是可见的
                        meeting_id.setVisibility(android.view.View.VISIBLE);
                        meeting_launch.setVisibility(android.view.View.VISIBLE);
                        meeting_screen_info.setVisibility(android.view.View.VISIBLE);
                    }

                    //
                    String meeting_id_str = "ID:" + m_var.av.taskInfo.idInfo_initiator.ui64Id;
                    meeting_id.setText(meeting_id_str);
                    //
                    getTalkerDescParams = new qyCommProc_open.getTalkerDescParams();
                    getTalkerDescParams.pMisCnt = pMisCnt;
                    getTalkerDescParams.idInfo = pMisCnt.idInfo;
                    getTalkerDescParams.talkerDesc = desc;
                    getTalkerDescParams.talkerDescCnt = cntof_desc;
                    getTalkerDescParams.shortName = null;
                    getTalkerDescParams.shortNameCnt = 0;
                    getTalkerDesc.qyFuncs.getTalkerDesc(getTalkerDescParams);

                    //
                    Log.d("当前账号:" + getTalkerDescParams.talkerDesc);
                    //
                    String meeting_account_str = "当前账号:" + getTalkerDescParams.talkerDesc;
                    meeting_account.setText(meeting_account_str);
                    qnmCommProc_mis.MIS_MSG_TASK pMsgTask = (qnmCommProc_mis.MIS_MSG_TASK) pTc.msgU;

                    {
                        if (pMsgTask.uiType == CONST_misMsgType_task) {
                            qnmCommProc_mis.IM_CONTENTU pContent = qnmCommProc_mis.qyFuncs.M_getMsgContent(pMsgTask.uiType, pMsgTask.ucFlg, pMsgTask.data);
                            if (null != pContent) {
                                qnmCommProc_mis.IM_CONTENTU tmp_pContent = pContent;
                                //
                                if (pContent.uiType == CONST_imCommType_transferAvInfo) {
                                    qnmCommProc_mis.TRANSFER_AV_INFO transferAvInfo = (qnmCommProc_mis.TRANSFER_AV_INFO) pContent;
                                    int width = transferAvInfo.confMosaicTranInfo_video.video.vh_decompress.bih.biWidth;
                                    int height = transferAvInfo.confMosaicTranInfo_video.video.vh_decompress.bih.biHeight;
                                    String meeting_screen_info_str = width + "x" + height;
                                    meeting_screen_info.setText(meeting_screen_info_str);
                                    //
                                    Log.d("px py 分辨率:" + width + "--" + height);
                                }
                            }
                        }

                    }

                }

            }

        }
    }

    //
    public long tickCnt_create;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //
        Log.d(TAG, "onCreateView.l1275");

        //
        View view = null;

        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (pQyMc == null) return null;
        ctxQmc.CCtxQmc pProcInfo  =  pQyMc.get_pProcInfo();

        //
        Log.d(TAG, "onCreateView. before inflate. l1285");

        //
        view = inflater.inflate(R.layout.layout_fg_dlgtalk_av, container, false);

        //
        Log.d(TAG, "onCreateView. after inflate. l1291");

        //
        this.mMainHandler = new MainHandler(this);

        mTextureView = view.findViewById(R.id.texture);
        //
        mTextureView.m_fg=this;

        //
        bottomMenu = view.findViewById(R.id.bottomMenu);
        topMenu = view.findViewById(R.id.topMenu);
        more_info_menu = view.findViewById(R.id.more_info_menu);
        m_btn_request = view.findViewById(R.id.btn_request);
        m_tv_ioStatusOut = view.findViewById(R.id.tv_ioStatus_out);
        m_tv_ioStatusIn = view.findViewById(R.id.tv_ioStatus_in);
        // m_btn_more = (Button)view.findViewById(R.id.btn_more);
        up_bt = view.findViewById(R.id.up_bt);
        down_bt = view.findViewById(R.id.down_bt);
        left_bt = view.findViewById(R.id.left_bt);
        right_bt = view.findViewById(R.id.right_bt);
        short_focus_bt = view.findViewById(R.id.short_focus_bt);
        long_focus_bt = view.findViewById(R.id.long_focus_bt);
        m_btn_av = view.findViewById(R.id.btn_av);
        nvr_channel_name = view.findViewById(R.id.nvr_channel_name_title);
        nvr_control = view.findViewById(R.id.nvr_control);
        back_main_video = view.findViewById(R.id.back_main_video);
        back_to_video = view.findViewById(R.id.back_to_video);
        nvr_control.setOnClickListener(M_Nvr_control_Click);
        back_main_video.setOnClickListener(M_Back_Main_Video_Click);
        back_to_video.setOnClickListener(M_Back_To_Video_Click);
        min_viewport = view.findViewById(R.id.min_viewport);
        btnChangeCamera = view.findViewById(R.id.changeCamera);
        btnChangeCamera.setOnClickListener(BtnChangeCamera_Click);
        view.findViewById(R.id.rotatescreen).setOnClickListener(BtnRotateScreen_Click);
        btnLocalVideo = view.findViewById(R.id.localVideo);
        more_tv = view.findViewById(R.id.more_tv);
        //
        meeting_info=view.findViewById(R.id.meeting_info);
        meeting_launch = view.findViewById(R.id.meeting_launch);
        meeting_account = view.findViewById(R.id.meeting_account);
        meeting_screen_info = view.findViewById(R.id.meeting_screen_info);
        meeting_id = view.findViewById(R.id.meeting_id);
        btnLocalVideo.setOnClickListener(BtnLocalVideo_Click);
        more_tv.setOnClickListener(BtnMoreTV_Click);



        left_menu_relative = view.findViewById(R.id.left_menu_relative);
        right_menu_relative = view.findViewById(R.id.right_menu_relative);


        do {
            CIsTalker talker = findIsTalker(pProcInfo.pMisCnt, this.talker_ui64Id);
            if (talker == null) {
                break;
            }
            m_talker = talker;

            //
            mRenderer = new dlgTalk_av_Renderer(this, CONST_fgType_talk_av2);
            if  (  mRenderer.init() !=0  )  {
                Log.d("at","fg_av2.onCreateView failed, mRender.init failed");
                break;
            }
            mRenderer.start();

            //
            dlgTalkProc.DLG_TALK_var m_var = m_talker.m_var;

            //
            m_runnableHideBottomBar = () -> {
                if (!isAdded() || bottomMenu == null || topMenu == null || more_info_menu == null) return;
                if (useMenuAlwaysDocked()) {
                    return;
                }
                if (m_var.m_talkAvVar.menuInScreen) {
                    more_tv.setActivated(false);

                    more_info_menu.setVisibility(View.INVISIBLE);
                    animatorHideMenu();
                    showOperationRelativeLayout(false);
                    nvr_controlSetNormal();
                }
            };
            m_runnableShowBottomBar = () -> {
                if (!isAdded() || bottomMenu == null || topMenu == null) return;
                if (useMenuAlwaysDocked()) {
                    applyDockedMenuBars();
                    return;
                }
                if (!m_var.m_talkAvVar.menuInScreen) {
                    m_var.m_talkAvVar.menuInScreen = true;
                    if (m_var.m_talkAvVar.firstStartMenuInScreen) {
                        animatorShowMenu(0);
                    } else {
                        animatorShowMenu(500);
                    }
                    m_var.m_talkAvVar.firstStartMenuInScreen = false;

                }
            };




            //
            m_var.m_talkAvVar.menuInScreen = false;
            updateAvTopMenuOverlayVisibility();
            if (useMenuAlwaysDocked()) {
                min_viewport.post(this::applyDockedMenuBars);
            } else {
                min_viewport.post(m_runnableShowBottomBar);
                m_bottomBarHandler.postDelayed(m_runnableHideBottomBar, 8000);
            }
            mTextureView.setSurfaceTextureListener(mRenderer);
            mTextureView.setOnTouchListener(Texture_View_OnTouchListener);
            nvr_channel_name.setOnClickListener(Channel_num_tv_ClickListener);
            up_bt.setOnTouchListener(Up_bt_OnTouchListener);
            down_bt.setOnTouchListener(Down_bt_OnTouchListener);
            left_bt.setOnTouchListener(Left_bt_OnTouchListener);
            right_bt.setOnTouchListener(Right_bt_OnTouchListener);
            short_focus_bt.setOnTouchListener(Short_focus_bt_OnTouchListener);
            long_focus_bt.setOnTouchListener(Long_focus_OnTouchListener);

            m_btn_request.setOnClickListener(M_btn_request_Click);
            m_btn_av.setOnClickListener(M_btn_av_Click);
            min_viewport.setOnClickListener(M_min_viewport_Click);


            //
            pull_downDrawable = mOwner_wnd.getDrawable(R.drawable.pull_down);
            pull_downDrawable.setBounds(0, 0, pull_downDrawable.getIntrinsicWidth(), pull_downDrawable.getIntrinsicHeight());

            pull_upDrawable = mOwner_wnd.getDrawable(R.drawable.pull_up);
            pull_upDrawable.setBounds(0, 0, pull_upDrawable.getIntrinsicWidth(), pull_upDrawable.getIntrinsicHeight());


//        m_btn_av.setText(qyCusResHelp.qyFuncs.getResStr(0, pQyMc.cusRes, qnmResDefs.CONST_resId_cancel_AV));//"Cancel";

            bindViews(view);

            //
            Log.d(TAG, "onCreateView.l1379");

            //
            //Activity_dlgTalk hTalk = (mOwner_wnd.mOwner_hTalk.get());
            //dlgTalkProc.DLG_TALK_var m_var = hTalk.get_pm_var();

            if (m_var.addr.uiObjType != CONST_objType_imGrp) {
                m_btn_request.setVisibility(android.view.View.INVISIBLE);
            }
            this.m_autoSpeak = new Tmp_autoSpeak();

            //
            if (pQyMc != null) {
                if (pProcInfo.bUse100k) {
                    //
                    // pProcInfo.use100k_onlyV=true;
                    // back_main_video.setText("仅音频");
                }
            }

            //
            Log.d(TAG, "onCreateView.l1400 befoe glSurfaceView");

            //
            m_viewStub_myGlRsSurfaceView = view.findViewById(R.id.view_stub_smxj);
            //
            if (pProcInfo.bUseSmxj) {
                loadDelayedLayout_myGlRsSurfaceView();

                //
                mGLSurfaceView = view.findViewById(R.id.glSurfaceView);
                if (mGLSurfaceView != null) {
                    //mGLSurfaceView.setVisibility(View.GONE);
                }

                m_rs = new Tmp_rs();
            }
            //
            if (m_rs != null) {
                m_rs.onCreateView((Fragment) this, view);
            }

            //
            m_confMem_rightMenu = view.findViewById(R.id.confMem_rightMenu);
            //
            m_confMem_list_btn = view.findViewById(R.id.confMem_list_btn);
            m_confMem_list_iv = view.findViewById(R.id.confMem_list_iv);
            m_confMem_list_tv = view.findViewById(R.id.confMem_list_tv);


            //
            m_confMem_list_menu = view.findViewById(R.id.confMem_list_menu);
            m_confMem_hide_list_btn = view.findViewById(R.id.confMem_hide_list_btn);
            m_confMem_hide_list_iv = view.findViewById(R.id.confMem_hide_list_iv);
            m_confMem_hide_list_tv = view.findViewById(R.id.confMem_hide_list_tv);
            //
            m_confMem_request_btn = view.findViewById(R.id.confMem_request_btn);
            m_confMem_request_iv = m_confMem_request_btn.findViewById(R.id.confMem_request_iv);
            m_confMem_request_tv = m_confMem_request_btn.findViewById(R.id.confMem_request_tv);
            //
            m_confMem_way_btn = view.findViewById(R.id.confMem_way_btn);
            m_confMem_way_iv = view.findViewById(R.id.confMem_way_iv);
            m_confMem_way_tv = view.findViewById(R.id.confMem_way_tv);

            // 设置点击监听（点整个区域都能触发切换）
            m_confMem_way_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //toggleMode();
                    confMem_way_btn_Click();
                }
            });


            //
            m_confMemListContainer = view.findViewById(R.id.confMemListContainer);
            m_confMem_list_btn.setOnClickListener(confMem_list_btn_Click);
            m_confMem_hide_list_btn.setOnClickListener(confMem_hide_list_btn_Click);
            m_confMem_request_btn.setOnClickListener(confMem_request_btn_Click);


            //
            m_gv_confMemList = view.findViewById(R.id.confMemList);


            // 获取人员数据
            m_confMems = new ArrayList<>();
            m_speakers = new ArrayList<>();
            m_grpMems = new ArrayList<>();


            // 初始化适配器
            m_confMemAdapter = new ConfMemberAdapter(this.getActivity(), R.layout.item_confmem, m_confMems);
            //
            m_gv_confMemList.setAdapter(m_confMemAdapter);


            //
            // 设置GridView的点击事件
            m_gv_confMemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // 更新选中状态
                    gv_updateSelection(position);
                }
            });


            //
            tickCnt_create = myGetTickCount(null);

        } while (false);

        //
        return view;
    }


    //
    private void up_way_status(){
        //

        ctxQyMc.CCtxQyMc  pQyMc  =  g_pQyMc;
        ctxQmc.CCtxQmc      	pProcInfo			=		pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt=pProcInfo.pMisCnt;

        //
        //Activity_dlgTalk  hTalk  =  (Activity_dlgTalk) hDlgTalk_mgr;//mOwner_wnd.mOwner_hTalk.get();
        //if  (  hTalk ==null )  return  -1;
        CIsTalker talker = findIsTalker(pMisCnt,talker_ui64Id);
        if  ( talker==null)  return  ;

        dlgTalkProc.DLG_TALK_var m_var  =  talker.m_var;
        if  (  m_var  ==  null  )  return  ;


        //
        if(m_var.confCtrl.bNoVDownload){
            // 切换单向模式
            m_confMem_way_tv.setText("当前单向");
            // 替换成你单向对应的drawable图片资源，没有就先注释
            m_confMem_way_iv.setImageResource(R.drawable.conf_dan);

        }else{
            // 切回双向模式
            m_confMem_way_tv.setText("当前多向");
            m_confMem_way_iv.setImageResource(R.drawable.conf_duo);
        }
    }

    private void confMem_way_btn_Click() {
        //

        ctxQyMc.CCtxQyMc  pQyMc  =  g_pQyMc;
        ctxQmc.CCtxQmc      	pProcInfo			=		pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt=pProcInfo.pMisCnt;

        //
        //Activity_dlgTalk  hTalk  =  (Activity_dlgTalk) hDlgTalk_mgr;//mOwner_wnd.mOwner_hTalk.get();
        //if  (  hTalk ==null )  return  -1;
        CIsTalker talker = findIsTalker(pMisCnt,talker_ui64Id);
        if  ( talker==null)  return  ;

        dlgTalkProc.DLG_TALK_var m_var  =  talker.m_var;
        if  (  m_var  ==  null  )  return  ;


        if (m_var.confCtrl.bNoVDownload) {
            // 切回双向模式
            m_confMem_way_tv.setText("当前多向");
              m_confMem_way_iv.setImageResource(R.drawable.conf_duo);
            // 这里可以加你业务：比如发送指令、修改参数、通知后端/硬件

            confMen_bNoVDoVDownload(false);
        } else {
            // 切换单向模式
            m_confMem_way_tv.setText("当前单向");
            // 替换成你单向对应的drawable图片资源，没有就先注释
            m_confMem_way_iv.setImageResource(R.drawable.conf_dan);
            confMen_bNoVDoVDownload(true);
        }
    }

    //
    public boolean myTextureView_dispatchTouchEvent(MotionEvent e) {

        //
        dlgTalkProc.DLG_TALK_var m_var = m_talker.m_var;

        if (!useMenuAlwaysDocked()) {
            m_bottomBarHandler.removeCallbacks(m_runnableHideBottomBar);
        }
        //
        if (e.getAction() == MotionEvent.ACTION_UP) {
            //
            Log.d("anc","DispatchTouchEvent.459");
            //
            if (!useMenuAlwaysDocked()) {
                m_bottomBarHandler.removeCallbacks(m_runnableHideBottomBar);
                m_bottomBarHandler.postDelayed(m_runnableHideBottomBar, 8000);
            }
            //
            if (!useMenuAlwaysDocked() && !m_var.m_talkAvVar.menuInScreen) {
                //
                Log.d("anc","bottomMenu显示.462");

                animatorShowMenu(500);
//                return true;
            }
        }

        //
        return false;
    }


    /** 非地图悬浮窗，或悬浮窗全屏模式：保留上下栏滑动动画。 */
    private boolean useMenuSlideAnimation() {
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            return ((MainActivity) activity).useMapFloatingAvMenuSlideAnimation();
        }
        return true;
    }

    /** 地图浮窗/分屏：上下栏固定贴边，不滑出屏幕。 */
    private boolean useMenuAlwaysDocked() {
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            return ((MainActivity) activity).isMapFloatingAvMenuDocked();
        }
        return false;
    }

    private boolean shouldHideAvTopMenuOverlay() {
        Activity activity = getActivity();
        return activity instanceof MainActivity && ((MainActivity) activity).shouldHideAvTopMenuOverlay();
    }

    private void updateAvTopMenuOverlayVisibility() {
        if (topMenu == null) {
            return;
        }
        if (shouldHideAvTopMenuOverlay()) {
            topMenu.setVisibility(View.GONE);
            topMenu.setTranslationY(0f);
        } else {
            topMenu.setVisibility(View.VISIBLE);
        }
    }

    private void showMapFloatingEnlargeReturnButton() {
        if (topMenu != null && shouldHideAvTopMenuOverlay()) {
            topMenu.setVisibility(View.VISIBLE);
            topMenu.setTranslationY(0f);
        }
        if (min_viewport != null) {
            min_viewport.setVisibility(View.VISIBLE);
        }
    }

    /** 浮窗/分屏模式切换后由 MainActivity 调用。 */
    public void onMapFloatingAvDisplayModeChanged() {
        m_bottomBarHandler.removeCallbacks(m_runnableHideBottomBar);
        if (!isAdded() || bottomMenu == null || topMenu == null) {
            return;
        }
        // 分屏/全屏与浮窗之间切换时，中控按钮在标题栏与右侧菜单之间迁移
        confMem_list_btn_update();
        updateAvTopMenuOverlayVisibility();
        if (useMenuAlwaysDocked()) {
            applyDockedMenuBars();
            applyFloatingAvVideoLayoutForHost();
            return;
        }
        if (!useMenuSlideAnimation()) {
            return;
        }
        dlgTalkProc.DLG_TALK_var m_var = m_talker != null ? m_talker.m_var : null;
        if (m_var == null) {
            return;
        }
        if (m_var.m_talkAvVar.menuInScreen) {
            applyMenuVisibleTranslation();
        } else {
            applyMenuHiddenTranslation();
        }
        m_bottomBarHandler.postDelayed(m_runnableHideBottomBar, 8000);
    }

    private void applyDockedMenuBars() {
        if (!isAdded() || bottomMenu == null || topMenu == null) {
            return;
        }
        updateAvTopMenuOverlayVisibility();
        applyMenuVisibleTranslation();
        if (m_talker != null) {
            m_talker.m_var.m_talkAvVar.menuInScreen = false;
        }
        confMem_rightMenu_show(false);
    }

    /** 横屏分屏拖动改变视频区宽度后由 MainActivity 调用，避免 TextureView 仍按 wrap_content 被裁切。 */
    public void onMapFloatingAvShellResized() {
        applyFloatingAvVideoLayoutForHost();
    }

    /** 地图宿主旋转完成后：按最终窗体尺寸重排，再重启本地摄像头采集链路。 */
    public void restartCameraAfterHostRotationSettled() {
        applyFloatingAvVideoLayoutForHost();
        updateCameraSurfaceOrientationFromHost();
        restartLocalCameraForHostRotation();
    }

    private void applyFloatingAvVideoLayoutForHost() {
        if (!isAdded() || mTextureView == null) {
            return;
        }
        boolean mapEmbeddedHost = false;
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            mapEmbeddedHost = true;
        }
        ViewGroup.LayoutParams raw = mTextureView.getLayoutParams();
        if (raw instanceof android.widget.LinearLayout.LayoutParams) {
            android.widget.LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) raw;
            if (mapEmbeddedHost) {
                lp.width = android.widget.LinearLayout.LayoutParams.MATCH_PARENT;
                lp.height = 0;
                lp.weight = 1f;
            } else if (useMenuAlwaysDocked()) {
                lp.width = android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;
                lp.height = 0;
                lp.weight = 1f;
            }
            mTextureView.setLayoutParams(lp);
        }
        View root = getView();
        if (root != null) {
            root.requestLayout();
        }
        mTextureView.post(() -> {
            if (!isAdded() || mTextureView == null) {
                return;
            }
            mTextureView.requestLayout();
            if (m_talker != null && m_talker.m_var != null) {
                doChkTalkDraw(m_talker.m_var);
            }
        });
    }

    private void updateCameraSurfaceOrientationFromHost() {
        Activity activity = getActivity();
        if (activity == null || mCameraRenderThread == null) {
            return;
        }
        int surfaceOrientation = activity.getWindowManager().getDefaultDisplay().getRotation();
        mCameraRenderThread.updateSurfaceOrientation(surfaceOrientation);
        if (mCameraRenderThread.mCameraPreviewWidth > 0 && mCameraRenderThread.mCameraPreviewHeight > 0) {
            Triplet<Integer, Integer, Integer> oInfo = myrecycleFunc.qyFuncs_Droid.get_tex_o_info(
                    mSensorOrientation,
                    surfaceOrientation,
                    mCameraRenderThread.mCameraPreviewWidth,
                    mCameraRenderThread.mCameraPreviewHeight);
            m_o_iW = oInfo.getValue1();
            m_o_iH = oInfo.getValue2();
        }
    }

    private void restartLocalCameraForHostRotation() {
        dlgTalkProc.DLG_TALK_var v = m_talker != null ? m_talker.m_var : null;
        if (v == null || v.av == null || !v.av.taskInfo.bTaskExists) {
            return;
        }
        pauseTaskAv(this, CONST_fgType_talk_av2, false, "map host rotation settled");
        resumeTaskAv(this, CONST_fgType_talk_av2, "map host rotation settled");
        doChkTalkDraw(v);
    }

    private void applyMenuHiddenTranslation() {
        bottomMenu.setTranslationY(bottomMenu.getHeight());
        if (!shouldHideAvTopMenuOverlay()) {
            topMenu.setTranslationY(-topMenu.getHeight());
        }
    }

    private void applyMenuVisibleTranslation() {
        bottomMenu.setTranslationY(0f);
        updateAvTopMenuOverlayVisibility();
        if (!shouldHideAvTopMenuOverlay()) {
            topMenu.setTranslationY(0f);
        }
    }

    private void animateMenuTranslation(float bottomFrom, float bottomTo,
                                      float topFrom, float topTo, int durationMs) {
        ObjectAnimator bottomAnim = ObjectAnimator.ofFloat(bottomMenu, "translationY", bottomFrom, bottomTo);
        bottomAnim.setDuration(durationMs);
        bottomAnim.start();
        if (!shouldHideAvTopMenuOverlay()) {
            ObjectAnimator topAnim = ObjectAnimator.ofFloat(topMenu, "translationY", topFrom, topTo);
            topAnim.setDuration(durationMs);
            topAnim.start();
        }
    }

    //
    public void animatorShowMenu(int animiatorDuration) {
        if (!isAdded() || bottomMenu == null || topMenu == null || more_tv == null) {
            return;
        }
        if (useMenuAlwaysDocked()) {
            applyDockedMenuBars();
            return;
        }
        //
        Log.d("anc", "bottomMenu显示.479");
        //
        dlgTalkProc.DLG_TALK_var m_var = m_talker.m_var;//mOwner_hTalk.get().get_pm_var();
        //
        m_var.m_talkAvVar.menuInScreen = true;
        // 添加这两行代码，确保底部菜单出现时设置more_tv图标
        more_tv.setActivated(false);  // 设为非激活状态
        more_tv.setImageResource(R.drawable.more);  // 设置为默认的more图标
        if (animiatorDuration <= 0) {
            applyMenuVisibleTranslation();
        } else {
            animateMenuTranslation(bottomMenu.getHeight(), 0f, -topMenu.getHeight(), 0f, animiatorDuration);
        }

        //
        confMem_rightMenu_show(true);

        //
        return;
    }

    public void animatorHideMenu() {

        if (popupWindow != null) {
            popupWindow.dismiss();
        }

        if (!isAdded() || bottomMenu == null || topMenu == null) {
            return;
        }

        if (useMenuAlwaysDocked()) {
            applyDockedMenuBars();
            return;
        }

        //
        Log.d("anc","bottomMenu隐藏");
        //
        dlgTalkProc.DLG_TALK_var m_var = m_talker.m_var;//mOwner_hTalk.get().get_pm_var();

        //
        m_var.m_talkAvVar.menuInScreen = false;

        //
        animateMenuTranslation(0f, bottomMenu.getHeight(), 0f, -topMenu.getHeight(), 500);

        //
        confMem_rightMenu_show(false);

    }




    //
    boolean isInflated =false;
    //
    int loadDelayedLayout_myGlRsSurfaceView()
    {
        if (!isInflated) {
            // This inflates the layout and replaces the ViewStub
            View inflatedView = m_viewStub_myGlRsSurfaceView.inflate();
            // OR: viewStub.setVisibility(View.VISIBLE);
            isInflated = true;

            // Now you can access views in the inflated layout
            mGLSurfaceView  = inflatedView.findViewById(R.id.glSurfaceView);
            /*
            button.setOnClickListener(v1 -> {
                // Handle button click
                android.util.Log.d("mm","");

            });

             */
        }
        return  0;
    }

    private void bindViews(View view) {
        bindVoiceVideoSwitcher(view);
        bindBottomMenu(view);
    }

    private void bindBottomMenu(View view) {

        /*
        if  (  this.mOwner_wnd==null)return;
        if (  this.mOwner_wnd.mOwner_hTalk==null)return;
        Activity_dlgTalk hTalk  =  this.mOwner_wnd.mOwner_hTalk.get();
        if  (  hTalk  == null)return;

         */
        ctxQyMc.CCtxQyMc pQyMc =g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();


        CIsTalker talker = findIsTalker(pProcInfo.pMisCnt,this.talker_ui64Id);
        if  (  talker==null)  return;
        dlgTalkProc.DLG_TALK_var m_var = talker.m_var;
        //
        if  (!isGroup(m_var)) {
            back_main_video.setVisibility(View.GONE);
        }
    }

    //
    private void gv_updateSelection(int position) {
        // 如果之前有选中的项，取消其选中状态
        if (m_gv_confMem_selectedPosition != -1 && m_gv_confMem_selectedPosition != position) {
            m_confMems.get(m_gv_confMem_selectedPosition).setSelected1(false);
        }

        // 设置当前项的选中状态（切换）
        ConfMember currentItem = m_confMems.get(position);
        currentItem.setSelected1(!currentItem.isSelected());

        // 如果取消选中，则重置selectedPosition
        if (!currentItem.isSelected()) {
            m_gv_confMem_selectedPosition = -1;
        } else {
            m_gv_confMem_selectedPosition = position;
        }

        //
        gv_updateMenu();

        // 通知适配器数据发生变化
        m_confMemAdapter.notifyDataSetChanged();
    }

    private void gv_clearSelection() {
        if (m_gv_confMem_selectedPosition != -1) {
            m_confMems.get(m_gv_confMem_selectedPosition).setSelected1(false);
            m_gv_confMem_selectedPosition = -1;
        }
    }

    //
    int  gv_updateMenu()
    {
        //

        do {
            //
            if (m_gv_confMem_selectedPosition == -1) {
                m_confMem_request_btn.setVisibility(View.GONE);
            } else {
                ConfMember mem = m_confMems.get(m_gv_confMem_selectedPosition);
                if (mem == null) break;

                boolean confMem_request_btn_bShow  =  false;
                String confMem_request_btn_desc  =  "";
                int  confMem_request_iv_resId  =  0;

                if  (  mem.iConfMemType  ==0)  {
                    confMem_request_btn_bShow=false;
                }
                else  {
                    //
                    confMem_request_btn_bShow=true;
                    //
                    if  (  mem.iConfMemType  ==  CONST_confMemType_speaker    )  {
                        if  ( mem.uiObjType!=0)  {
                            confMem_request_btn_bShow=false;
                            //  对mcu的idInfo, 不需要处理
                        }
                        else {
                            confMem_request_btn_desc = "停止";
                            //
                            confMem_request_iv_resId  =  R.drawable.confmem_offmic;
                        }
                    }
                    else  if  (  mem.iConfMemType==Consts.CONST_confMemType_grpMem)  {
                        confMem_request_btn_desc  =  "调取";
                        //
                        confMem_request_iv_resId  =  R.drawable.confmem_request;
                    }

                }

                //
                int iVisible = 0;
                iVisible = m_confMem_request_btn.getVisibility();

                //
                if  (  confMem_request_btn_bShow)  {
                    if  (  iVisible!=View.VISIBLE) {
                        m_confMem_request_btn.setVisibility(View.VISIBLE);
                    }
                    //
                    m_confMem_request_iv.setImageResource(confMem_request_iv_resId);
                    //
                    m_confMem_request_tv.setText(confMem_request_btn_desc);
                }
                else {
                    if (iVisible == View.VISIBLE) {
                        m_confMem_request_btn.setVisibility(View.GONE);
                    }
                }
                //
            }

        } while(false);

        //
        return  0;
    }




    //
    @SuppressLint("ClickableViewAccessibility")
    private void bindVoiceVideoSwitcher(View view) {
        ctxQmc.CCtxQmc pProcInfo  = g_pQyMc.get_pProcInfo();
        /*
        if  (  this.mOwner_wnd==null)return;
        if (  this.mOwner_wnd.mOwner_hTalk==null)return;
        Activity_dlgTalk hTalk  =  this.mOwner_wnd.mOwner_hTalk.get();
        if  (  hTalk  == null)return;


         */


        //仅100k,才显示音视频切换按钮
        //群组不显示
        //if(!pProcInfo.bUse100k || hTalk.isGroup())  return;
        if(!pProcInfo.use100k_ifP2p )  return;
        if  (!pProcInfo.use100k_bDj)  return;

        //
        tvTips = view.findViewById(R.id.tvTips);
        ivVoiceVideoSwitcher = view.findViewById(R.id.ivVoiceVideoSwitcher);
        ivVoiceVideoSwitcher.setVisibility(View.VISIBLE);
        ivVoiceVideoSwitcher.setOnTouchListener((v, event) -> {
            //
            //  这里是对讲功能的切换
            //
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                //按下 "仅音频"
                pProcInfo.use100k_noA1=false;
                pProcInfo.use100k_noV1=true;
                //
                tvTips.setVisibility(View.VISIBLE);
                return true;
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                //弹起
                tvTips.setVisibility(View.GONE);
                // 仅视频
                pProcInfo.use100k_noA1=true;
                pProcInfo.use100k_noV1=false;
                //
                return false;
            }
            return false;
        });
    }



    //
    @org.greenrobot.eventbus.Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void OnReceiveNetStatusEvent(NetConnectEvent bean) {
        NetConnectStatus status = bean.getNetStatus();
        //
        Log.d(IO_TAG, "OnReceiveNetStatusEvent mNetStatus:" + mNetStatus + " status:" + status);
        //
        if(mNetStatus == NetConnectStatus.DISCONNECTED && status ==  NetConnectStatus.CONNECTED)
        {
            //Activity_dlgTalk dlgTalk = mOwner_wnd.mOwner_hTalk.get();
            ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            CIsTalker talker=findIsTalker(pProcInfo.pMisCnt,this.talker_ui64Id);
            //
            if(talker !=null && talker.m_var.av.m_bState_canSpeak){
                do_request(true);
            }
        }
        mNetStatus = status;
    }

    /**
     * 回到主页
     */

    private final View.OnClickListener M_Back_To_Video_Click = (View view) -> {
        resetCurrentVideoChanel();
    };

    private final View.OnClickListener M_Back_Main_Video_Click = (View view) -> {
        ctxQyMc.CCtxQyMc  pQyMc  =  g_pQyMc;
        if  (  pQyMc==null) return;
        ctxQmc.CCtxQmc pProcInfo  =  pQyMc.get_pProcInfo();

        //
        /*
        Activity_dlgTalk_av owner = this.mOwner_wnd;
        if  (  this.mOwner_wnd==null)return;
        if (  this.mOwner_wnd.mOwner_hTalk==null)return;
        Activity_dlgTalk hTalk  =  this.mOwner_wnd.mOwner_hTalk.get();
        if  (  hTalk  == null)return;

         */
        CIsTalker talker = findIsTalker(pProcInfo.pMisCnt,this.talker_ui64Id);
        if  (  talker  ==  null  )  return;

        dlgTalkProc.DLG_TALK_var m_var  =  talker.m_var;//.get_pm_var();

        //
        if  (!isGroup(m_var))  {
            //
//            pProcInfo.use100k_onlyV=!pProcInfo.use100k_onlyV;
//            back_main_video.setText(pProcInfo.use100k_onlyV  ?  "仅音频"  :  "仅视频");

            //
            return;
        }


        //
        audioRecorder_android.qyFuncs_Droid.changeMicrophoneStatus(null);
        confOthers_sendStreamInfo(m_var.addr.idInfo.ui64Id);

        // EventBus.getDefault().post(new MicrophoneMutedEvent(audioRecorder_android.qyFuncs_Droid.isMicrophoneMuted()));
        if (audioRecorder_android.qyFuncs_Droid.isMicrophoneMuted()){
            back_main_video.setImageResource(R.drawable.open_microphone);
        }
        else {
            back_main_video.setImageResource(R.drawable.close_microphone);
        }
    };

    private final View.OnClickListener Channel_num_tv_ClickListener = (View view) -> {
        Log.d("Channel_num_tv_ClickListener");
        if (popupWindow.isShowing()) {

            popupWindow.dismiss();
        } else {
            popupWindow.showAsDropDown(nvr_channel_name);
            nvr_channel_name.setCompoundDrawables(null, null, pull_downDrawable, null);
        }
    };

    private final View.OnTouchListener Long_focus_OnTouchListener = (View v, MotionEvent event) -> {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            long_focus_bt.setPressed(true);
            dlg_YTBtn_remote(IDC_BUTTON_ZOOM_IN, (byte) CONST_mouseStatus_lBtnDown);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            long_focus_bt.setPressed(false);

            dlg_YTBtn_remote(IDC_BUTTON_ZOOM_IN, (byte) CONST_mouseStatus_lBtnUp);
        }
        return true;
    };

    private final View.OnTouchListener Short_focus_bt_OnTouchListener = (View v, MotionEvent event) -> {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            short_focus_bt.setPressed(true);

            dlg_YTBtn_remote(IDC_BUTTON_ZOOM_OUT, (byte) CONST_mouseStatus_lBtnDown);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            short_focus_bt.setPressed(false);
            dlg_YTBtn_remote(IDC_BUTTON_ZOOM_OUT, (byte) CONST_mouseStatus_lBtnUp);
        }
        return true;
    };

    private final View.OnTouchListener Right_bt_OnTouchListener = (View v, MotionEvent event) -> {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            right_bt.setPressed(true);

            dlg_YTBtn_remote(IDC_BUTTON_RIGHT, (byte) CONST_mouseStatus_lBtnDown);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            right_bt.setPressed(false);
            dlg_YTBtn_remote(IDC_BUTTON_RIGHT, (byte) CONST_mouseStatus_lBtnUp);
        }
        return true;
    };

    private final View.OnTouchListener Left_bt_OnTouchListener = (View v, MotionEvent event) -> {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            left_bt.setPressed(true);

            dlg_YTBtn_remote(IDC_BUTTON_LEFT, (byte) CONST_mouseStatus_lBtnDown);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            left_bt.setPressed(false);

            dlg_YTBtn_remote(IDC_BUTTON_LEFT, (byte) CONST_mouseStatus_lBtnUp);

        }
        return true;
    };

    private final View.OnTouchListener Up_bt_OnTouchListener = (View v, MotionEvent event) -> {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            up_bt.setPressed(true);

            dlg_YTBtn_remote(IDC_BUTTON_UP, (byte) CONST_mouseStatus_lBtnDown);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            up_bt.setPressed(false);

            dlg_YTBtn_remote(IDC_BUTTON_UP, (byte) CONST_mouseStatus_lBtnUp);

        }
        return true;
    };

    private final View.OnTouchListener Down_bt_OnTouchListener = (View v, MotionEvent event) -> {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            down_bt.setPressed(true);

            dlg_YTBtn_remote(IDC_BUTTON_DOWN, (byte) CONST_mouseStatus_lBtnDown);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            down_bt.setPressed(false);

            dlg_YTBtn_remote(IDC_BUTTON_DOWN, (byte) CONST_mouseStatus_lBtnUp);

        }
        return true;
    };


    //
    public String confMem_getStatusStr(long ui64Id, ConfMember mem_dst)
    {
        String statusStr = "";

        do {
            //
            ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
            if (pQyMc == null) break;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (pProcInfo == null) break;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            /*
            if (mOwner_wnd == null) break;
            if (mOwner_wnd.mOwner_hTalk == null) break;
            Activity_dlgTalk hTalk = mOwner_wnd.mOwner_hTalk.get();
            if (hTalk == null) break;

             */
            CIsTalker talker = findIsTalker(pMisCnt,this.talker_ui64Id);
            if  (  talker==null)  return  "";
            //
            dlgTalkProc.DLG_TALK_var m_var = talker.m_var;

            //
            if (!m_var.av.taskInfo.bTaskExists) break;

            //
            if (ui64Id == pMisCnt.idInfo.ui64Id) {
                statusStr += "我,";
            }
            int  i;
            qnmCommProc_mis.HgZcrs zcrs=m_var.av.taskInfo.hgInfo.zcrs;
            for  (  i  =  0;  i  <  zcrs.mems_idInfo.length;i++) {
                if  (zcrs.mems_idInfo[i]==0)continue;
                if  (zcrs.mems_idInfo[i]==ui64Id)  {
                    statusStr+="主持人";
                    break;
                }
            }
            //  从列表种取出状态
            for  (  i=0;i<m_confMems.size();i++)  {
                ConfMember mem = m_confMems.get(i);
                if  (  mem.ui64Id  ==  ui64Id)  {
                    //
                    if ( mem_dst!=null)  {
                        mem_dst.usRunningStatus  =  mem.usRunningStatus;
                    }
                    //
                    break;
                }
            }


            //
        } while( false);


        //
        return  statusStr;
    }


    //
    public int reloadSpeakers(List<ConfMember>m_memberList,long grpIdInfo)
    {
        //
        int  iErr  =  -1;

        //
        m_memberList.clear();

        //
        ctxQyMc.CCtxQyMc pQyMc =g_pQyMc;
        if  (  pQyMc==null)return  -1;
        ctxQmc.CCtxQmc pProcInfo=pQyMc.get_pProcInfo();
        if  (  pProcInfo==null) return  -1;
        qmCommFunc_mis.MIS_CNT pMisCnt=pProcInfo.pMisCnt;

        //
        /*
        if  (  mOwner_wnd==null)  return  -1;
        if  (  mOwner_wnd.mOwner_hTalk==null)return  -1;
        Activity_dlgTalk  hTalk  =  mOwner_wnd.mOwner_hTalk.get();
        if  (  hTalk==null)  return  -1;

         */
        CIsTalker talker=findIsTalker(pMisCnt,this.talker_ui64Id);
        if  (  talker==null)  return  -1;
        dlgTalkProc.DLG_TALK_var m_var = talker.m_var;

        //
        if  (  !m_var.av.taskInfo.bTaskExists  )  return  -1;

        int  i;


        //m_var.av.taskInfo.iIndex_taskInfo
        qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, pProcInfo, m_var.av.taskInfo.iIndex_taskInfo, "tmpHandler_showMsg_input_confKey");
        if (null == pTaskInfo) {
            return iErr;
        }
        qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
        if (pTaskData == null) {
            return iErr;
        }
        if (pTaskData.uiType != CONST_taskDataType_conf) {
            return iErr;
        }
        qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
        qmcTaskInfo.DLG_TALK_videoConference videoConference = pTc.getVideoConference();


        qyDefs.QY_MESSENGER_ID idInfo  = new qyDefs.QY_MESSENGER_ID();
        String desc = "";
        for (i = 0; i < videoConference.usCntLimit_activeMems_from; i++) {

            qmcTaskInfo.DLG_TALK_videoConferenceActiveMemFrom pActiveMem_from = videoConference.activeMems_from[i];

            if  (  pActiveMem_from.avStream.idInfo.ui64Id  ==  0  )  continue;

            //  将融合流的显示忽略掉
            boolean bMosaic  = false;
            switch (pActiveMem_from.avStream.obj.resObj.uiObjType  ) {
                case CONST_objType_mosaicStream_video:
                case CONST_objType_mosaicStream_resource:
                    bMosaic =true;
                    break;
                default:
                    break;
            }
            if  ( bMosaic)  continue;

            //
            idInfo.ui64Id  =  pActiveMem_from.avStream.idInfo.ui64Id;

            //M_getActiveMemDesc(pMisCnt, pActiveMem_from.avStream.idInfo, tBuf_desc.tBuf, cntof_desc);
            qyCommProc_open.getTalkerDescParams getTalkerDescParams = new qyCommProc_open.getTalkerDescParams();
            getTalkerDescParams.pMisCnt = pMisCnt;
            //getTalkerDescParams.idInfo = pMisCnt.idInfo;
            getTalkerDescParams.idInfo = idInfo;
            getTalkerDescParams.talkerDesc = desc;
            getTalkerDescParams.talkerDescCnt = 128;
            getTalkerDescParams.shortName = null;
            getTalkerDescParams.shortNameCnt = 0;
            getTalkerDesc.qyFuncs.getTalkerDesc(getTalkerDescParams);


            //
            ConfMember mem=new ConfMember();
            //
            mem.iConfMemType  = CONST_confMemType_speaker;
            //
            mem.ui64Id  =  idInfo.ui64Id;
            mem.desc =  idInfo.ui64Id  +  " "  +  getTalkerDescParams.talkerDesc;
            mem.iAvatar  =  R.mipmap.avatar_speaker;
            //
            mem.uiObjType=pActiveMem_from.avStream.obj.resObj.uiObjType;

            //
            mem.usIndex_activeMem  =  i;

            //
            String  str  =  "";
            str  =  confMem_getStatusStr(mem.ui64Id,null);
            //
            mem.statusStr=str;

            //
            m_memberList.add(mem);

            //
            continue;

        }



        //
        return  0;
    }


    //
    public static qyDefs.PF_commonHandler tmpHandler_reloadGrpMemList = (Object p0, Object p1, Object p2) -> {
        long grpIdInfo = (long) p0;
        //p1
        List<ConfMember> tmp_list = (List<ConfMember>)p1;
        qnmCommProc_mis.IM_GRP_MEM pQMem = (qnmCommProc_mis.IM_GRP_MEM) p2;

        //
        ctxQyMc.CCtxQyMc  pQyMc=g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo  =  pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt  =  pProcInfo.pMisCnt;

        //
        if  (  pQMem.idInfo_grp.ui64Id  ==  grpIdInfo)  {
            ConfMember mem=new ConfMember();
            //
            mem.iConfMemType  =  Consts.CONST_confMemType_grpMem;
            //
            mem.ui64Id  = pQMem.idInfo_mem.ui64Id;
            mem.desc  =  "";
            mem.iAvatar  =  R.mipmap.avatar1;
            //
            String  str  =  "";
            //
            mem.statusStr=str;

            //
            tmp_list.add(mem);
        }

        //
        return 0;
    };



    //
    public int reloadGrpMemList(List<ConfMember>m_memberList,long grpIdInfo)
    {
        //
        m_memberList.clear();

        //
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        myDb.CMyDb myDb = (com.qycx.base.myDb.CMyDb) pQyMc.gui.pDb;
        ctxQmc.CCtxQmc pProcInfo  =  pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt  =  pProcInfo.pMisCnt;

        //
        genericQueue.qyFuncs.qTraverse(myDb.m_var_pQ_qyImGrpMemTab, tmpHandler_reloadGrpMemList, grpIdInfo, m_memberList);

        //
        int  i;
        qyDefs.QY_MESSENGER_ID idInfo  =  new qyDefs.QY_MESSENGER_ID();
        String desc="";
        for  (i  =  0;i  <  m_memberList.size();i++  )  {
            ConfMember mem  =  m_memberList.get(i);
            //
            idInfo.ui64Id=mem.ui64Id;
            //
            if  (idInfo.ui64Id==905)  {
                Log.d("anc","");
            }

            //
            qyCommProc_open.getTalkerDescParams getTalkerDescParams = new qyCommProc_open.getTalkerDescParams();
            getTalkerDescParams.pMisCnt = pMisCnt;
            //getTalkerDescParams.idInfo = pMisCnt.idInfo;
            getTalkerDescParams.idInfo = idInfo;
            getTalkerDescParams.talkerDesc = desc;
            getTalkerDescParams.talkerDescCnt = 128;
            getTalkerDescParams.shortName = null;
            getTalkerDescParams.shortNameCnt = 0;
            getTalkerDesc.qyFuncs.getTalkerDesc(getTalkerDescParams);


            //
            mem.desc =  idInfo.ui64Id  +  " "  + getTalkerDescParams.talkerDesc;

            //
            //
            mem.statusStr= confMem_getStatusStr(mem.ui64Id,mem);

            //
            continue;
        }


        //
        return  0;
    }

    //
    int confMem_removeDup()
    {
        // 删除所有偶数（条件：num % 2 == 0）
        Iterator<ConfMember> iterator = m_confMems.iterator();
        //
        while (iterator.hasNext()) {
            ConfMember mem = iterator.next();
            if (mem.iConfMemType  ==  Consts.CONST_confMemType_grpMem  ) { // 满足条件
                boolean  bMatched  = false;
                //
                int  i;
                for  (  i  =  0;  i< m_speakers.size();i++) {
                    ConfMember speaker = m_speakers.get(i);
                    if  (  speaker.ui64Id==0)  continue;
                    if  (  mem.ui64Id ==  speaker.ui64Id  )  {
                        bMatched=true;
                        break;
                    }
                }
                //
                if  (  bMatched) {
                    iterator.remove(); // 使用迭代器的remove()方法
                }
            }
        }

        return  0;
    }

    //
    int confMem_addTail()
    {
        //
        ConfMember mem=new ConfMember();
        mem.ui64Id  = 0;
        mem.desc  =  "没有更多了";
        mem.iAvatar  =  0;
        mem.statusStr="";

        m_confMems.add(mem);

        return  0;
    }

    //
    public int reloadConfMemList()
    {
        //
        m_confMems.clear();

        //
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        myDb.CMyDb myDb = (com.qycx.base.myDb.CMyDb) pQyMc.gui.pDb;
        ctxQmc.CCtxQmc pProcInfo  =  pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt  =  pProcInfo.pMisCnt;

        long grpIdInfo  =  0;

        //
        /*
        if  (  mOwner_wnd==null)  return  -1;
        if  (  mOwner_wnd.mOwner_hTalk==null)return  -1;
        Activity_dlgTalk  hTalk  =  mOwner_wnd.mOwner_hTalk.get();
        if  (  hTalk==null)  return  -1;

         */
        CIsTalker talker = findIsTalker(pMisCnt,this.talker_ui64Id);
        if  ( talker == null)  return  -1;
        dlgTalkProc.DLG_TALK_var m_var = talker.m_var;

        //
        grpIdInfo=m_var.addr.idInfo.ui64Id;


        //
        reloadSpeakers(m_speakers,grpIdInfo);
        //
        reloadGrpMemList(m_grpMems,grpIdInfo);

        //
        m_confMems.addAll(m_speakers);
        m_confMems.addAll(m_grpMems);

        //
        confMem_removeDup();
        //
        confMem_addTail();

        //
        sortMems();

        //
        return  0;
    }


    //
    int  sendRemotePtzCmd_3ddw(qnmCommProc_mis.PTZ_cmdParam_3ddw pParam)
    {
        int  iErr = -1;
        ctxQyMc.CCtxQyMc  pQyMc  = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

        //
        int  uiObjType = 0;
        short  usIndex_obj = 0;
        short  usHelp_subIndex = 0;
        //
        byte	ucCmd = 0;
        byte   ucParam = 0;
        //char 	tBuf[128];
        //unsigned  char  ucMouseStatus = CONST_mouseStatus_lBtnDown;

        //  2016/07/19


        //
        qyDefs.QY_MESSENGER_ID idInfo=new qyDefs.QY_MESSENGER_ID(); idInfo.ui64Id = pProcInfo.m_ipcProc.op.nvrInfo.ui64Id;
        int  iChannel = 0;//pProcInfo.m_ipcProc.op.iChannel;
        /*
        if  (  mOwner_wnd==null)  return  -1;
        if  (  mOwner_wnd.mOwner_hTalk==null)return  -1;
        Activity_dlgTalk  hTalk  =  mOwner_wnd.mOwner_hTalk.get();
        if  (  hTalk==null)  return  -1;

         */
        CIsTalker talker = findIsTalker(pMisCnt,this.talker_ui64Id);
        if  (talker==null)  return  -1;
        dlgTalkProc.DLG_TALK_var m_var = talker.m_var;
        iChannel = m_var.currentSelectQiuJiChannel;


        int nResourceID = pProcInfo.m_ipcProc.op.nID;


        //
        qyDefs.QY_MESSENGER_ID idInfo_imGrp_related  =  new qyDefs.QY_MESSENGER_ID();
        idInfo_imGrp_related.ui64Id  =  m_var.addr.idInfo.ui64Id;

        //
        //DLG_TALK_var* pm_var = get_pm_var();
        //if (!pm_var)  return  -1;

        //
        //if  (  mOwner_wnd.mOwner_hTalk==null)  return  -1;
        qyDefs.QY_MESSENGER_ID idInfo_to = new qyDefs.QY_MESSENGER_ID();
        idInfo_to.ui64Id = pProcInfo.m_ipcProc.op.nvrInfo.ui64Id;

        do {

            //
            /*
            if (idInfo_to.ui64Id == pMisCnt->idInfo.ui64Id) {
                //  发送到mcu上去
                //HWND  hCurTalk = (HWND)m_pParent;
                HWND  hCurTalk = (HWND)this->winId();
                CHelp_getDlgTalkVar help_getDlgTalkVar;
                DLG_TALK_var* pCurVar = (DLG_TALK_var*)help_getDlgTalkVar.getVar(hCurTalk);
                if (!pCurVar)  break;
                HWND  hMgr = nullptr;
                if (isTalkerShadowMgr(pCurVar->addr)) {
                    hMgr = hCurTalk;
                }
                else {
                    TALKER_shadow* pShadow = (TALKER_shadow*)pCurVar->pShadowInfo;
                    if (!pShadow) break;
                    hMgr = pShadow->hMgr;
                }
                CHelp_getDlgTalkVar  help_getDlgTalkVar_mgr;
                DLG_TALK_var* pMgrVar = (DLG_TALK_var*)help_getDlgTalkVar_mgr.getVar(hMgr);
                if (!pMgrVar)  break;
                //
                if (!pMgrVar->av.taskInfo.bTaskExists)  break;
                if (!pMgrVar->av.taskInfo.ucbVideoConference)  break;
                //
                idInfo_imGrp_related.ui64Id = pMgrVar->addr.idInfo.ui64Id;
                //
                idInfo_to = pMgrVar->av.taskInfo.idInfo_starter;

                //
            }

             */

            //
            byte  ucMouseStatus = 0;

            //
            qnmCommProc_mis.PTZ_cmdParamU  paramU  =  new qnmCommProc_mis.PTZ_cmdParamU();
            paramU.param3ddw=pParam;

            //
            ucCmd = CONST_ptzCmd_3ddw;
            sendPtzReq(uiObjType, usIndex_obj, usHelp_subIndex, idInfo, iChannel, ucCmd, ucParam, ucMouseStatus, paramU, idInfo_imGrp_related, idInfo_to);

            //
            iErr = 0;

        }  while (false);



        return  iErr;
    }



    //
    private final View.OnTouchListener Texture_View_OnTouchListener = (View v, MotionEvent event) -> {

        //
        //
        ctxQyMc.CCtxQyMc  pQyMc  =  g_pQyMc;
        if  (  pQyMc==null)  return  false;
        ctxQmc.CCtxQmc  pProcInfo  =  pQyMc.get_pProcInfo();
        if  (  pProcInfo==null)  return false;

        MainActivity  mainWnd  =  (MainActivity) pQyMc.gui.hMainWnd;
        if  (  mainWnd==null)return false;

        //
        //Activity_dlgTalk_av talk_av = (Activity_dlgTalk_av) getActivity();
        //Activity_dlgTalk hTalk=talk_av.mOwner_hTalk.get();
        dlgTalkProc.DLG_TALK_var m_var = this.m_talker.m_var;//hTalk.m_var;

        //
        if  ( event.getAction()==MotionEvent.ACTION_DOWN) {
            //Log.d("qy" ,"down");
            if  (  useMenuAlwaysDocked() || m_var.m_talkAvVar.menuInScreen) {
                m_var.m_talkAvVar.bCmdStart=true;
            }
        }
        //
        if (event.getAction() == MotionEvent.ACTION_UP) {

            if  (  m_var.m_talkAvVar.bCmdStart) {
                m_var.m_talkAvVar.bCmdStart = false;

                //
                Rect centerSurfaceViewPortRect = (Rect) m_var.av.hDc_av_rect;
                //
                if (centerSurfaceViewPortRect == null) {
                    return false;
                }

                //
                if (centerSurfaceViewPortRect.contains((int) (event.getX()), (int) (event.getY() - statusBarHeight))) {

                    int centerViewPortWidth = centerSurfaceViewPortRect.right - centerSurfaceViewPortRect.left;
                    int centerViewPortHeight = centerSurfaceViewPortRect.bottom - centerSurfaceViewPortRect.top;

                    //
                    Log.d("zzstar tex_view_onTouchListener: org ord: " + event.getX() + "- " + event.getY());

                    //
                    int  video_w  =  mainWnd.textureIw;// 1920;
                    int  video_h  =  mainWnd.textureIh;//1080;

                    //
                    if (  video_w  ==  0  ||  video_h  ==  0)  return  false;
                    if  (  centerViewPortWidth==0  ||  centerViewPortHeight==0)  return false;

                    //
                    int realX = (int) ((event.getX() -
                            centerSurfaceViewPortRect.left) / centerViewPortWidth * video_w);
                    int realY = (int) ((event.getY() -
                            centerSurfaceViewPortRect.top - statusBarHeight) / centerViewPortHeight * video_h);

                    //
                    //  这里关于realX和realY的计算还是有问题，因为在离屏渲染转到textureview上去时，因为分辨率比例的不同，会造成离屏区和视频不完全是吻合的
                    //  所以这里需要更精细的修正

                    //
                    qnmCommProc_mis.ConfLayoutParam layoutParam = new qnmCommProc_mis.ConfLayoutParam();
                    layoutParam.phoneEnlargeParam.x = (short) realX;
                    layoutParam.phoneEnlargeParam.y = (short) realY;

                    //
                    Log.d("anc","zzstar transPnt：" + layoutParam.phoneEnlargeParam.x + "- " + layoutParam.phoneEnlargeParam.y);

                    //组名
                    qyDefs.QY_MESSENGER_ID idInfo_imGrp_related = new qyDefs.QY_MESSENGER_ID();
                    idInfo_imGrp_related.ui64Id = m_var.addr.idInfo.ui64Id;

                    // 我
                    qyDefs.QY_MESSENGER_ID idInfo_requester = new qyDefs.QY_MESSENGER_ID();
                    idInfo_requester.ui64Id = 0;

                    //mcu 的
                    qyDefs.QY_MESSENGER_ID idInfo_to = new qyDefs.QY_MESSENGER_ID();
                    idInfo_to.ui64Id = m_var.av.taskInfo.idInfo_starter.ui64Id;

                    dlgTalk_videoConference.qyFuncs_Droid.sendConfLayout(m_var.addr.idInfo.ui64Id, (byte) 0, (short) 0, (short) CONST_imOp_phone_enlargeImg, layoutParam, idInfo_imGrp_related, idInfo_requester, idInfo_to, "");
                    currentVideoStatus = 1;
                    showMapFloatingEnlargeReturnButton();

                    //
                    if  (  m_b3ddw)  {
                        //
                        //
                        qnmCommProc_mis.PTZ_cmdParam_3ddw param = new qnmCommProc_mis.PTZ_cmdParam_3ddw();

                        //
                        param.sW_video = (short)video_w;
                        param.sH_video = (short)video_h;

                        //
                        param.xTop = (short)(realX - 20);  if (param.xTop < 0)  param.xTop = 0;
                        param.yTop = (short)(realY - 20);  if (param.yTop < 0)  param.yTop = 0;
                        param.xBottom = (short)(realX + 20);  if (param.xBottom > param.sW_video)  param.xBottom = param.sW_video;
                        param.yBottom = (short)(realY + 20);  if (param.yBottom > param.sH_video)  param.yBottom = param.sH_video;//方框结束点的y坐标


                        //send_selLayoutType(realX, realY);

                        sendRemotePtzCmd_3ddw(param);


                    }

                    //
                }
            }
        }
        return true;
    };


    private final View.OnClickListener M_min_viewport_Click = (View view) -> {
        resetCurrentVideoChanel();
    };


    //
    public void resetCurrentVideoChanel() {

        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

        CIsTalker talker = findIsTalker(pMisCnt,this.talker_ui64Id);
        if  (  talker ==null) return;
        dlgTalkProc.DLG_TALK_var m_var = talker.m_var;

        qnmCommProc_mis.ConfLayoutParam layoutParam = new qnmCommProc_mis.ConfLayoutParam();
        layoutParam.phoneEnlargeParam.x = 0;
        layoutParam.phoneEnlargeParam.y = 0;

        //组名
        qyDefs.QY_MESSENGER_ID idInfo_imGrp_related = new qyDefs.QY_MESSENGER_ID();
        idInfo_imGrp_related.ui64Id = m_var.addr.idInfo.ui64Id;

        // 我
        qyDefs.QY_MESSENGER_ID idInfo_requester = new qyDefs.QY_MESSENGER_ID();
        idInfo_requester.ui64Id = 0;

        //mcu 的
        qyDefs.QY_MESSENGER_ID idInfo_to = new qyDefs.QY_MESSENGER_ID();
        idInfo_to.ui64Id = m_var.av.taskInfo.idInfo_starter.ui64Id;
        Log.d("缩小画面");
        dlgTalk_videoConference.qyFuncs_Droid.sendConfLayout(m_var.addr.idInfo.ui64Id, (byte) 0, (short) 0, (short) CONST_imOp_phone_enlargeChannel, layoutParam, idInfo_imGrp_related, idInfo_requester, idInfo_to, "");
        currentVideoStatus = 0;
        min_viewport.setVisibility(android.view.View.INVISIBLE);
        updateAvTopMenuOverlayVisibility();

        return;
    }

    //
    public int exitTaskAv()
    {
        int  iErr =  -1;
        dlgTalkProc.DLG_TALK_var m_var = this.m_talker.m_var;

        //
        if (m_var.av.taskInfo.bTaskExists) {

            //
            Log.d("at","fg_av2.exitTaskAv enters");

            //
            if  (  dlgTalk_canSpeak(m_var.addr.idInfo.ui64Id) ) {
                Log.d("at","发送取消发言");
                do_request(false);
            }

            //
            if (m_var.av.bAutoCancel_lostVideo) {
                m_var.av.bDoneOnce_autoCancel_lostVideo = true;
            }


            //
            Object hWnd = this;//mOwner_hTalk;
            int iWndType = CONST_fgType_talk_av2;//qyWndContentType_talker;
            //
            pauseTaskAv(hWnd, iWndType, true, "dlgTalk_av.handler.HandleMessage, get a msg doClean");

            //qyFuncs_Droid.dlgTalk_cancelTasks(mOwner_hTalk, mOwner_hTalk.m_var, mOwner_hTalk.m_var.av.taskInfo.iTaskId, true);
            short usOp = 0;
            if (m_var.av.taskInfo.ucbStarter)
                usOp = qnmCommProc_mis.Consts.CONST_imOp_send_cancel;
            else usOp = qnmCommProc_mis.Consts.CONST_imOp_recv_cancel;
            //
            dlgTalkPublic.PARAM_dlgTalk_procTask param = new dlgTalkPublic.PARAM_dlgTalk_procTask();
            if (m_var.av.bAutoCancel_lostVideo) {
                param.bAutoCancel = true;
            }
            Log.d("调用 dlgTalk_procTask1 开始");
            //
            Fragment fg = this;//mOwner.m_fg_dlgTalk_av2;
            //dlgTalkProc.DLG_TALK_var m_var = this.mOwner.mOwner_hTalk.get().get_pm_var();
            //
            dlgTalk_procTask.qyFuncs_Droid.dlgTalk_procTask1(fg, m_var, m_var.av.taskInfo.iTaskId, usOp, "", param);
            //
            Log.d("调用 dlgTalk_procTask1 结束");

            //
            Log.d("at","fg_av2.exitTaskAv leaves");
        }

        iErr  = 0;

        return  iErr;

    }





    //
    public void changeLocalVideo() {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        CIsTalker talker = findIsTalker(pProcInfo.pMisCnt,this.talker_ui64Id);
        if  ( talker==null)  return;

        //Activity_dlgTalk dlgTalk = this.mOwner_hTalk.get();
        dlgTalkProc.DLG_TALK_var m_var = talker.m_var;

        //如果是竖屏切换到全屏
        if (m_var.av.taskInfo.bTaskExists) {
            m_var.av.taskInfo.bShow_localVideo = !m_var.av.taskInfo.bShow_localVideo;
        }
        if (m_var.av.taskInfo.bShow_localVideo) {
            btnLocalVideo.setImageResource(R.drawable.close_loacl_video);

        } else {
            btnLocalVideo.setImageResource(R.drawable.local_video);

        }

        return;
    }




    //
    private final View.OnClickListener BtnLocalVideo_Click = (View view) -> {
        //throw new NotImplementedException();
        Log.d("changeCamera clicked");
        changeLocalVideo();
    };

    private final View.OnClickListener BtnMoreTV_Click = (View view) -> {

        getMeetingInfo();
        if (more_info_menu.getVisibility() == View.VISIBLE) {
            more_tv.setActivated(false);
            more_tv.setImageResource(R.drawable.more);
            more_info_menu.setVisibility(android.view.View.INVISIBLE);
        } else {
            more_tv.setActivated(true);
            more_tv.setImageResource(R.drawable.close_more);
            more_info_menu.setVisibility(View.VISIBLE);
        }
    };


    //
    public void changeCamera() {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        CIsTalker talker =findIsTalker(pProcInfo.pMisCnt,this.talker_ui64Id);
        if  ( talker==null)  return;

        //Activity_dlgTalk dlgTalk = this.mOwner_hTalk.get();
        dlgTalkProc.DLG_TALK_var m_var = talker.m_var;//dlgTalk.m_var;

        //如果是竖屏切换到全屏
        if (m_var.av.taskInfo.bTaskExists) {
            //
            Object hWnd  = null;//dlgTalk;
            int iWndType  =  CONST_qyWndContentType_talker;
            //
            if  (  pProcInfo.cfg.bUse_fgAsWndStarter)  {
                hWnd = this;
                iWndType=CONST_fgType_talk_av2;
            }
            //
            pauseTaskAv(  hWnd, iWndType, false,"changeCamera");

            //
            m_var.av.taskInfo.bUse_backCamera = !m_var.av.taskInfo.bUse_backCamera;

            //
            resumeTaskAv( hWnd, iWndType,"changeCamera");

            if (m_var.av.taskInfo.bUse_backCamera) {
                btnChangeCamera.setImageResource(R.drawable.change_camera);

            } else {
                btnChangeCamera.setImageResource(R.drawable.change_camera);

            }
        }

        return;
    }



    //
    long  lastTickCnt_changeCamera  =  0;

    //
    private final View.OnClickListener BtnChangeCamera_Click = (View view) -> {
        //
        long tickCnt  =  myGetTickCount(null);
        int  iDiffInMs  =  (int)(tickCnt  -  lastTickCnt_changeCamera);
        if  (  iDiffInMs  <  500)  {
            //  不让太频繁的切换
            return;
        }
        lastTickCnt_changeCamera  =  tickCnt;

        //throw new NotImplementedException();
        Log.d("changeCamera clicked");
        if (!isChangeCamera) {
            isChangeCamera = true;
            changeCamera();
            btnChangeCamera.postDelayed(() -> isChangeCamera = false, 1000);
        }
    };

    private final View.OnClickListener BtnRotateScreen_Click = (View view) -> {
        //
        ctxQyMc.CCtxQyMc  pQyMc  =  g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo  =  pQyMc.get_pProcInfo();
        if  (  pProcInfo.cfg.b_no_rotateScreen)  {
            ToastUtils.showToast("该设备不能旋转屏幕");
            return;
        }

        //
        CIsTalker talker = findIsTalker(pProcInfo.pMisCnt,this.talker_ui64Id);
        if  ( talker==null)  return;
        dlgTalkProc.DLG_TALK_var m_var = talker.m_var;


        //
        long tickCnt  =  myGetTickCount(null);
        int  iDiffInMs  =  (int)(tickCnt  -  lastTickCnt_changeCamera);
        if  (  iDiffInMs  <  500)  {
            //  不让太频繁的切换
            return;
        }
        lastTickCnt_changeCamera  =  tickCnt;

        //throw new NotImplementedException();
        Log.d("anc","Rotate screen clicked");

        Activity host = getActivity();
        if (host instanceof Activity_dlgTalk_av) {
            ((Activity_dlgTalk_av) host).upsetPortraitOrLandscapeLayout();
        } else if (host instanceof MainActivity) {
            int current = getResources().getConfiguration().orientation;
            if (current == Configuration.ORIENTATION_LANDSCAPE) {
                host.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                host.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

        //
        if  (  true  ) {
            if (pProcInfo.cfg.bUse_off2on) {
                //
                Log.d("anc", "rotateScreen: so needRestartCamera");

                //如果是竖屏切换到全屏
                if (m_var.av.taskInfo.bTaskExists) {

                    //
                    m_var.av.taskInfo.bNeedRestart_camera=true;

                }


            }
        }


    };


    //
    private final View.OnClickListener confMem_list_btn_Click=(View view)->{

        //
        Log.d("anc","confMem_list_btn clicked");
        //
        toggleConfMemList();

    };

    private final View.OnClickListener confMem_hide_list_btn_Click=(View view)->{

        //
        Log.d("anc","confMem_hide_list_btn clicked");

        //
        toggleConfMemList();

    };



    //
    private final View.OnClickListener confMem_request_btn_Click=(View view)->{

        //
        Log.d("anc","confMem_request_btn clicked");

        ctxQyMc.CCtxQyMc pQyMc=g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo  =  pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt   =  pProcInfo.pMisCnt;
        //
        CIsTalker talker = findIsTalker(pMisCnt,this.talker_ui64Id);
        if  (  talker==null)  return;
        dlgTalkProc.DLG_TALK_var m_var  =  talker.m_var;
        if  (  m_var  ==  null  )  return;

        String str;

        //
        do {

            //
            if (m_gv_confMem_selectedPosition == -1) {
                Log.d("anc", "no selection");
                //
                str  =  "需要选择列表中一个联系人，才能启停视频";
                showNotification(0, null, null, 0, 0, 0, str);
                break;
            }
            //
            if (m_gv_confMem_selectedPosition != -1) {
                ConfMember mem = m_confMems.get(m_gv_confMem_selectedPosition);
                if (  mem ==null)  break;
                //
                Log.d("anc", "sel: " + mem.ui64Id + " " + mem.desc);
                //
                if (mem.iConfMemType  ==  Consts.CONST_confMemType_grpMem)  {
                    confCompere_pleaseSpeak(talker_ui64Id,mem.ui64Id,true);
                }
                else  {
                    confCompere_pleaseSpeak(talker_ui64Id,mem.ui64Id,false);
                    //
                    if  (  mem.ui64Id  == pMisCnt.idInfo.ui64Id)  {
                        //  停止自动发言
                        m_var.av.m_bState_canSpeak  =  false;
                    }
                }
            }

        }  while  ( false  );

        //
        return;
    };


    //
    int  confCompere_pleaseSpeak(long talker_ui64IdParam, long pIdInfo_speaker, boolean  bSpeak  )
    {
        int						iErr				=		-1;

        //
        ctxQyMc.CCtxQyMc  pQyMc  =  g_pQyMc;
        ctxQmc.CCtxQmc      	pProcInfo			=		pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt=pProcInfo.pMisCnt;

        //
        //Activity_dlgTalk  hTalk  =  (Activity_dlgTalk) hDlgTalk_mgr;//mOwner_wnd.mOwner_hTalk.get();
        //if  (  hTalk ==null )  return  -1;
        CIsTalker talker = findIsTalker(pMisCnt,talker_ui64IdParam);
        if  ( talker==null)  return  -1;

        dlgTalkProc.DLG_TALK_var m_var  =  talker.m_var;
        if  (  m_var  ==  null  )  return  -1;


        //
        qnmCommProc_mis.TASK_INTERACTION_REQ req    =       new qnmCommProc_mis.TASK_INTERACTION_REQ();
        int						lenInBytes			=		0;

        //
        if  (  0==pIdInfo_speaker  )  return  -1;
        if  (  !m_var.av.taskInfo.bTaskExists
                ||  !m_var.av.taskInfo.ucbVideoConference
        )
        {
            return  -1;
        }

        //
        if  (  bSpeak)  {
            String str;
            if  (  isActiveMemsFull(null,m_var.av.taskInfo.iTaskId,true))  {
                //
                str = "发言人已满";
                str  +=  "(最多"  +  m_var.av.taskInfo.usMaxSpeakers  +  "人).";
                str+="请移除其他发言人";
                //
                showNotification(0, null, null, 0, 0, 0, str);
                //
                return  -1;
            }
        }


        //memset(  &req,  0,  sizeof(  req  )  );
        req.uiType  =  CONST_imCommType_taskInteractionReq;
        req.usOp  =  bSpeak  ?  CONST_imOp_pleaseSpeak  :  CONST_imOp_pleaseStopSpeaking;
        req.tStartTime_org  =  m_var.av.taskInfo.tStartTime_org;
        req.uiTranNo_org  =  m_var.av.taskInfo.uiTranNo_org;
        //req.uiContentType_org  =  m_var.av.taskInfo.uiContentType_org;
        //
        //  2015/02/17
        //
        //req.idInfo_speaker.ui64Id  =  pIdInfo_speaker->ui64Id;
        //
        if  (  m_var.av.taskInfo.ucbVideoConference  )  req.idInfo_imGrp_related.ui64Id  =  m_var.addr.idInfo.ui64Id;
        //
        lenInBytes  =  128;//sizeof(  req  );
        //
        //MACRO_prepareForTran(  );
        long tStartTran = tmpFunc.qyFuncs.mytime();
        int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();

        //
        qyDefs.QY_MESSENGER_ID idInfo_dst = new qyDefs.QY_MESSENGER_ID();
        idInfo_dst.ui64Id  =  pIdInfo_speaker;
        //
        if (0 != postMsgTask2Mgr_mc(null, pMisCnt, CONST_misMsgType_task, (byte) 0, (short) CONST_qyCmd_sendTask, tStartTran, uiTranNo, (short) 0, 0, 0,  req, lenInBytes, 0, 0, 0, idInfo_dst.ui64Id,
                0, (qnmCommProc_mis.MIS_MSGU) null, false)) {
            return iErr;
        }

        //
        //qyShowInfo1(  CONST_qyShowType_qwmComm,  0,  (char*)  (  ""  ),  pProcInfo->who_showInfo,  0,  _T(  ""  ),  _T(  ""  ),  _T(  "confCtrl: let %I64u %s"  ),  pIdInfo_speaker->ui64Id,  bPermitToSpeak  ?  _T(  "permit to speak"  )  :  _T(  "prohibit speaking"  )  );

        iErr  =  0;
        errLabel:
        return  iErr;
    }

    int confMen_bNoVDoVDownload( boolean  bNoVDownload ){
        int						iErr				=		-1;


        //
        ctxQyMc.CCtxQyMc  pQyMc  =  g_pQyMc;
        ctxQmc.CCtxQmc      	pProcInfo			=		pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt=pProcInfo.pMisCnt;

        //
        //Activity_dlgTalk  hTalk  =  (Activity_dlgTalk) hDlgTalk_mgr;//mOwner_wnd.mOwner_hTalk.get();
        //if  (  hTalk ==null )  return  -1;
        CIsTalker talker = findIsTalker(pMisCnt,talker_ui64Id);
        if  ( talker==null)  return  -1;

        dlgTalkProc.DLG_TALK_var m_var  =  talker.m_var;
        if  (  m_var  ==  null  )  return  -1;


        //
        //qnmCommProc_mis.TASK_INTERACTION_REQ req    =       new qnmCommProc_mis.TASK_INTERACTION_REQ();
        qnmCommProc_mis.TASK_PROC_REQ req = new qnmCommProc_mis.TASK_PROC_REQ();
        int						lenInBytes			=		0;

        //
/*
        if  (  0==pIdInfo_speaker  )  return  -1;
        if  (  !m_var.av.taskInfo.bTaskExists
                ||  !m_var.av.taskInfo.ucbVideoConference
        )
        {
            return  -1;
        }
*/
//
        // memset(  &taskProcReq,  0,  sizeof(  taskProcReq  )  );
        req.uiType  =  CONST_imCommType_taskProcReq;
        req.usOp  =  CONST_imOp_switchTransmissionMode;
        req.tStartTime_org  =  m_var.av.taskInfo.tStartTime_org;
        req.uiTranNo_org  =  m_var.av.taskInfo.uiTranNo_org;

        req.uiContentType_org  =  CONST_imCommType_transferAvInfo;
//
        req.idInfo_imGrp_related.ui64Id = m_var.addr.idInfo.ui64Id;
//
        req.uiTaskType  =  0;

//
        if(bNoVDownload){
            req.ucbNoVDownload = 1;
        }else{
            req.ucbNoVDownload = 0;
        }

//
        //  lenInBytes  =  sizeof(  req  );


        //memset(  &req,  0,  sizeof(  req  )  );
      /*  req.uiType  =  CONST_imCommType_taskInteractionReq;
        req.usOp  =  bSpeak  ?  CONST_imOp_pleaseSpeak  :  CONST_imOp_pleaseStopSpeaking;
        req.tStartTime_org  =  m_var.av.taskInfo.tStartTime_org;
        req.uiTranNo_org  =  m_var.av.taskInfo.uiTranNo_org;*/
        //req.uiContentType_org  =  m_var.av.taskInfo.uiContentType_org;
        //
        //  2015/02/17
        //
        //req.idInfo_speaker.ui64Id  =  pIdInfo_speaker->ui64Id;
        //
        if  (  m_var.av.taskInfo.ucbVideoConference  )  req.idInfo_imGrp_related.ui64Id  =  m_var.addr.idInfo.ui64Id;
        //
        lenInBytes  =  128;//sizeof(  req  );


        //
        //MACRO_prepareForTran(  );
        long tStartTran = tmpFunc.qyFuncs.mytime();
        int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();

        //
        // qyDefs.QY_MESSENGER_ID idInfo_dst = new qyDefs.QY_MESSENGER_ID();
        // idInfo_dst.ui64Id  =  pIdInfo_speaker;
        //
        if (0 != postMsgTask2Mgr_mc(null, pMisCnt, CONST_misMsgType_outputTask, (byte) 0, (short) CONST_qyCmd_sendMedia, tStartTran, uiTranNo, (short) 0,0, 0,  req, lenInBytes, m_var.addr.idInfo.ui64Id, 0, 0, m_var.av.taskInfo.idInfo_starter.ui64Id,
                0, (qnmCommProc_mis.MIS_MSGU) null, false)) {
            return iErr;
        }
        //if  (  postMsgTask2Mgr_mc(  pMisCnt,  uiMisMsgType,  0,  usCode,  tStartTran,  uiTranNo,  0,  iTaskId,  uiTaskType,  (  char  *  )&taskProcReq,  lenInBytes,  &idInfo_peer1,  NULL,  NULL,  &idInfo_dst1,  uiChannelType,  NULL,  FALSE  )  )  goto  errLabel;

        //
        //qyShowInfo1(  CONST_qyShowType_qwmComm,  0,  (char*)  (  ""  ),  pProcInfo->who_showInfo,  0,  _T(  ""  ),  _T(  ""  ),  _T(  "confCtrl: let %I64u %s"  ),  pIdInfo_speaker->ui64Id,  bPermitToSpeak  ?  _T(  "permit to speak"  )  :  _T(  "prohibit speaking"  )  );

        //
        m_var.confCtrl.bNoVDownload =  bNoVDownload;
        iErr  =  0;
        errLabel:
        return  iErr;
    }


    //
    public int  confMem_rightMenu_show(boolean bShow)
    {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        //Activity_dlgTalk hTalk = (mOwner_wnd.mOwner_hTalk.get());
        CIsTalker talker = findIsTalker(pProcInfo.pMisCnt,talker_ui64Id);
        if  (  talker == null )  return -1;
        dlgTalkProc.DLG_TALK_var m_var = talker.m_var;

        //
        if  (  bShow)  {
            //
            confMem_list_btn_update();
            //
            m_confMem_rightMenu.setVisibility(View.VISIBLE);
        }
        else  {
            m_confMem_rightMenu.setVisibility(View.GONE);
        }

        //
        return  0;
    }

    /** 浮窗/分屏/全屏时中控按钮移到浮窗外壳标题栏左侧，由 MainActivity 承载。 */
    private MainActivity getTitleBarConfMemHost() {
        Activity activity = getActivity();
        if (activity instanceof MainActivity
                && ((MainActivity) activity).useTitleBarConfMemBtn()) {
            return (MainActivity) activity;
        }
        return null;
    }

    /** 标题栏中控按钮点击入口，功能与右侧菜单中控按钮一致。 */
    public void onTitleBarConfMemBtnClick() {
        Log.d("anc","titleBar confMem btn clicked");
        toggleConfMemList();
    }

    //
    int confMem_list_btn_update()
    {
        ctxQyMc.CCtxQyMc pQyMc =g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo  =  (ctxQmc.CCtxQmc )pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt  =  pProcInfo.pMisCnt;

        MainActivity titleBarHost = getTitleBarConfMemHost();

        CIsTalker talker = findIsTalker(pProcInfo.pMisCnt,talker_ui64Id);
        if  (  talker == null )  {
            if  (  titleBarHost != null )  {
                titleBarHost.setTitleBarConfMemBtnVisible(false);
            }
            return -1;
        }
        dlgTalkProc.DLG_TALK_var m_var = talker.m_var;

        //
        if  (  !m_var.av.taskInfo.bTaskExists)  {
            if  (  titleBarHost != null )  {
                titleBarHost.setTitleBarConfMemBtnVisible(false);
            }
            return  -1;
        }

        //  对非应急指挥，或者非主持人。confMem_list_btn暂时隐藏
        //  有列表时也不显示
        boolean bShow  =  dlgTalk_bConfCompere(m_var.addr.idInfo.ui64Id,pMisCnt.idInfo.ui64Id)
                &&  m_var.av.taskInfo.usConfType==CONST_usConfType_emergencyCommand
                &&  !m_isListVisible;

        //  浮窗/分屏/全屏：中控放到标题栏左侧，右侧菜单里的原按钮隐藏
        if  (  titleBarHost != null )  {
            m_confMem_list_btn.setVisibility(View.GONE);
            titleBarHost.setTitleBarConfMemBtnVisible(bShow);
            return  bShow  ?  0  :  -1;
        }
        Activity activity = getActivity();
        if  (  activity instanceof MainActivity )  {
            ((MainActivity) activity).setTitleBarConfMemBtnVisible(false);
        }

        //
        if  (  !bShow )  {
            m_confMem_list_btn.setVisibility(View.GONE);
            return  -1;
        }
        //
        int  iVal;
        iVal  =  m_confMem_list_btn.getVisibility();
        if  (  iVal!=View.VISIBLE)  {
            m_confMem_list_btn.setVisibility(View.VISIBLE);
        }

        //
        int resId  =  R.drawable.confmem_list;
        if  (  m_var.av.taskInfo.bTaskExists
                &&  m_var.av.taskInfo.usConfType  ==  CONST_usConfType_emergencyCommand)
        {
            resId=R.drawable.confmem_list;
        }
        //
        m_confMem_list_iv.setImageResource(resId);

        return  0;
    }

    //
    private void toggleConfMemList() {

        ctxQyMc.CCtxQyMc pQyMc=g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        /*
        Activity_dlgTalk_av talk_av=mOwner_wnd;
        if  (  talk_av==null)return;
        Activity_dlgTalk hDlgTalk=talk_av.mOwner_hTalk.get();
         */
        CIsTalker talker=findIsTalker(pProcInfo.pMisCnt,talker_ui64Id);
        if  (  talker  ==  null)  return;
        dlgTalkProc.DLG_TALK_var m_var = talker.m_var;

        //
        if (m_isListVisible) {
            // 隐藏人员列表
            m_confMemListContainer.setVisibility(View.GONE);

            //
            m_confMem_request_btn.setVisibility(View.GONE);

        } else {


            //
            reloadConfMemList();



            // 显示人员列表
            m_confMemListContainer.setVisibility(View.VISIBLE);

            //
            m_confMemAdapter.notifyDataSetChanged();

            //  将peerId存入pProcInfo中，以便从服务器得到recentFriendsResp时，能送过来
            pProcInfo.m_peerId_toShowConfMem  = m_var.addr.idInfo.ui64Id;

            //
            confMem_startToRefreshRecentFriends_all();

        }



        //
        m_isListVisible = !m_isListVisible;

        //
        confMem_list_btn_update();

        //
        up_way_status();

    }

    //
    public int procRefreshRecentFriendsResp(qnmCommProc_mis.REFRESH_RECENTFRIENDS_RESP resp)
    {
        int i ;
        boolean bChanged =false;

        //
        if  (  true  )  {
            for  (  i=0;i<resp.usCnt;i++) {
                qnmCommProc_mis.REFRESH_RECENTFRIEND_RESP mem  =  resp.mems[i];
                //
                Log.d("refreshRecentFriendsResp.mem["  + i  +  "]: "  +  mem.ui64Id  +  ", conf "  +  mem.conf_ui64Id );

            }

        }

        //
        for  (  i=0;i<resp.usCnt;i++) {

            int j;
            for (j = 0; j < m_confMems.size(); j++) {
                ConfMember mem = m_confMems.get(j);
                if (mem.ui64Id == resp.mems[i].ui64Id) {
                    if (mem.usRunningStatus != resp.mems[i].usRunningStatus) {
                        mem.usRunningStatus = resp.mems[i].usRunningStatus;
                        //
                        bChanged=true;
                    }
                    if (  mem.conf_ui64Id!=resp.mems[i].conf_ui64Id )  {
                        mem.conf_ui64Id=resp.mems[i].conf_ui64Id;
                        bChanged=true;
                    }
                    mem.conf_ui64Id_dwTickCnt_refreshed  =  myGetTickCount(null);
                }
            }
        }

        //
        if  (  bChanged)  {

            if  (  true  )  {
                ConfMember mem = m_confMems.get(0);
                if  (  mem.iConfMemType==CONST_confMemType_grpMem)  {
                    //
                    Log.d("anc","mmmmmm");
                }
            }

            //
            sortMems();

            //
            if  (  true  )  {
                ConfMember mem = m_confMems.get(0);
                if  (  mem.iConfMemType==CONST_confMemType_grpMem)  {
                    //
                    Log.d("anc","mmmmmm");
                }
            }

            //
            m_confMemAdapter.notifyDataSetChanged();
        }

        //
        return  0;
    }

    //
    public int  sortMems()
    {
        //
        Log.d("anc","sortMems called");

        //
        Collections.sort(m_confMems, new Comparator<ConfMember>() {
            @Override
            public int compare(ConfMember o1, ConfMember o2) {

                // null 处理
                if (o1 == o2) return 0;
                if (o1 == null) return -1;
                if (o2 == null) return 1;

                // 1) speaker 排最前
                boolean o1Speaker = (o1.iConfMemType == CONST_confMemType_speaker);
                boolean o2Speaker = (o2.iConfMemType == CONST_confMemType_speaker);

                if (o1Speaker && !o2Speaker) return -1;
                if (!o1Speaker && o2Speaker) return 1;

                // 如果都是 speaker，则按 activeMem 排序
                if (o1Speaker && o2Speaker) {
                    return Integer.compare(o1.usIndex_activeMem, o2.usIndex_activeMem);
                }

                // 2) 非 speaker: 在线排前
                boolean o1Online = (o1.usRunningStatus == CONST_usRunningStatus_online);
                boolean o2Online = (o2.usRunningStatus == CONST_usRunningStatus_online);

                if (o1Online && !o2Online) return -1;
                if (!o1Online && o2Online) return 1;

                // 3) 其他情况：认为相等（稳定排序）
                return 0;
            }
        });



        //
        return  0;
    }


    //
    private final View.OnClickListener M_btn_request_Click = (View view) -> {
        btn_request_click();
    };

    //
    public void btn_request_click()
    {
        /*
        Activity_dlgTalk_av owner = mOwner_wnd;
        Activity_dlgTalk dlgTalk = owner.mOwner_hTalk.get();
        if  (  dlgTalk==null)  return;

         */
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        CIsTalker talker = findIsTalker(pProcInfo.pMisCnt,talker_ui64Id);
        if  (  talker  == null)  return;
        dlgTalkProc.DLG_TALK_var m_var = talker.m_var;

        //
        boolean canSpeak = dlgTalk_canSpeak(m_var.addr.idInfo.ui64Id);
        do_request(!canSpeak);

        //
        m_var.av.m_bState_canSpeak = !canSpeak;

        //
        m_autoSpeak = new Tmp_autoSpeak();  //  clear
    };

    private final View.OnClickListener M_Nvr_control_Click = (View view) -> {
        if (left_menu_relative.getVisibility() == View.INVISIBLE) {
            Log.d("left_menu_relative:Visible");
            left_menu_relative.setVisibility(View.VISIBLE);
            right_menu_relative.setVisibility(View.VISIBLE);
            nvr_controlSetActive();
        } else {
            Log.d("left_menu_relative:Invisible");
            left_menu_relative.setVisibility(android.view.View.INVISIBLE);
            right_menu_relative.setVisibility(android.view.View.INVISIBLE);
            nvr_controlSetNormal();
        }
    };

    public void nvr_controlSetNormal() {
        nvr_control.setImageResource(R.drawable.nvr_control);

    }

    public void nvr_controlSetActive() {
        nvr_control.setImageResource(R.drawable.close_nvr_control);

    }

    //
    public int do_request(boolean bEnable) {
        //throw new NotImplementedException();
        //mIsShutter = true;

        int  iErr  =  -1;

        //
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (pQyMc == null) {
            Log.e("anc", "pQyMc == null");
            return  -1;
        }
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        //
        //Activity_dlgTalk_av owner = mOwner_wnd;
        //Activity_dlgTalk dlgTalk = owner.mOwner_hTalk.get();

        /*
        CIsTalker talker = findIsTalker(pProcInfo.pMisCnt,this.talker_ui64Id);
        if  ( talker==null) {
            return  -1;
        }

         */

        if  (  m_talker==null)  return  -1;
        dlgTalkProc.DLG_TALK_var m_var = m_talker.m_var;

        //
        if (!m_var.av.taskInfo.bTaskExists) return -1;

        //
        if  (  false  ) {
            if (isActiveMemsFull(null, m_var.av.taskInfo.iTaskId, true)) {
                Log.e("anc", "do_request failed: activeMemsFull");
                return -1;
            }
        }

        //
        Object  hWnd;
        hWnd  = null;
        //hWnd = dlgTalk;
        //
        if  (  pProcInfo.cfg.bUse_fgAsWndStarter)  {
            hWnd  =  this;
        }

        //
        qmcVideoCapture.qyFuncs.setCurSharedObjUsr_localAv(pProcInfo, m_var.av.iIndex_sharedObj_localAv, m_var.av.iIndex_usr_localAv);

        //
        //boolean canSpeak = qyFuncs_Droid.dlgTalk_canSpeak(dlgTalk);

        //
        if (0 != dlgTalk_videoConference.qyFuncs_Droid.dlgTalk_requestToSpeak(m_var.addr.idInfo.ui64Id, bEnable)) {
            return  -1;
        }

        //
        boolean bState_canSpeak = bEnable;// !canSpeak;


        if (bState_canSpeak) {
            m_btn_request.setImageResource(R.drawable.stop_saying);
        } else {
            m_btn_request.setImageResource(R.drawable.saying);
        }

        //
        iErr  =  0;
        return  iErr;
    }


    private final View.OnClickListener M_btn_av_Click = (View view) -> {
        //Activity_dlgTalk_av owner = this.mOwner_wnd;
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        CIsTalker talker = findIsTalker(pProcInfo.pMisCnt,talker_ui64Id);
        if  (  talker == null)  return;
        dlgTalkProc.DLG_TALK_var m_var = talker.m_var;

        //
        m_var.av.bNeedAvCall = false;

        //
        m_var.av.bAutoCancel_lostVideo = false;

        //
        m_var.av.m_bState_canSpeak = false;


        //
        Activity act = getActivity();
        if  (  act instanceof  Activity_dlgTalk_av) {
            Activity_dlgTalk_av owner = (Activity_dlgTalk_av) act;
            Log.d("M_btn_av_Click()点击退出");
            owner.exitDlgTalkAv();
        }
        else  {
            Object hWnd =       mOwner_wnd;
            int iWndType =0;
            //      int call(Object wnd, int iWndContenType, int msgId, int wParm, int lParam);
            guiShare.GuiShare.pf_PostMessage_new.get().call(hWnd,iWndType,CONST_qyWm_postComm, CONST_qyWmParam_closeTaskAv,0);
        }

    };


    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (mTextureView.isAvailable()) {
            int ii = 0;
        } else {
            int jj = 0;
        }

        //
        if  ( m_rs!=null)  {
            m_rs.onResume();
        }

    }
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause");
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        //
        if  (  m_rs!=null)  {
            m_rs.onPause();
        }
    }

    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");

        //
        exitTaskAv();

        //
        m_bottomBarHandler.removeCallbacksAndMessages(null);
        m_bottomBarHandler=null;
        m_runnableHideBottomBar  = null;
        m_runnableShowBottomBar=null;

        //
        canCloseCameraThread = true;
        if (min_viewport != null) {
            min_viewport.removeCallbacks(m_runnableShowBottomBar);
        }
        doClean("fg.OnDestroyView");
        super.onDestroyView();

        //
        if  (  m_rs!=null)  {
            m_rs.onDestroyView();
        }

        //
        if  (  mTextureView!=null)  {
            mTextureView.setOnTouchListener(null);
            mTextureView.setSurfaceTextureListener(null);
        }
        if  (  mMainHandler!=null){
            mMainHandler.removeCallbacksAndMessages(null);
        }

        //
        if ( mGLSurfaceView!=null){
            //
            mGLSurfaceView.onPause();
        }

        //
        mTextureView=null;
        bottomMenu=null;
        topMenu=null;
    }


    public int doClean(String hint) {
        String str;

        //
        if (hint == null) hint = "";

        //Activity_dlgTalk_av owner = (Activity_dlgTalk_av) getActivity();
        //if (owner != null)
        if  (  m_talker!=null)
        {
            dlgTalkProc.DLG_TALK_var m_var = m_talker.m_var;
            if  (  m_var !=null) {

                //
                if (!m_var.m_bNeedResumeTaskAv) {
                    Fragment fg = this;
                    dlgTalkProc.qyFuncs_Droid.dlgTalk_closeTaskAv(fg, m_var, "fg_dlgTalk_av.doClean_ifActivityIsFinishing");
                }
            }
        }

        if (mRenderer != null) {
            mRenderer.halt();
            try {
                mRenderer.join();
                mRenderer.exit();
                //  mRenderer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mRenderer = null;// (dlgTalk_av_Renderer)h;
        }
        return 0;
    }


    public int doClean_ifActivityIsFinishing() {
        return doClean("doClean_ifActivityIfFinishing");
    }

    //
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        //

        super.onDestroy();
        Log.d("Fragment_dlgTalk_av2  OnDestroy");
        canCloseCameraThread = true;
        if (mMainHandler != null) {
            Log.d(" mMainHandler.removeCallbacksAndMessages(null);");
        }
    }


    private void getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        statusBarHeight = getResources().getDimensionPixelSize(resourceId);

    }

    public boolean hasPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    static Size chooseVideoSize(Size[] choices) {
        if (choices.length == 0) {
            return new Size(0, 0);
        }
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }

        return choices[choices.length - 1];

    }

    static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        List<Size> bigEnough = new ArrayList<>();
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();

        for (int i = 0; i < choices.length; i++) {
            Size option = choices[i];
            if (option.getHeight() == option.getWidth() * h / w
                    && option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            return choices[0];
        }
    }

    //
    public String selectCamera(CameraManager manager, boolean bFront) {
        String sel_id = null;

        String[] cameraIdList = new String[0];
        try {
            cameraIdList = manager.getCameraIdList();
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return null;
        }
        if (cameraIdList == null) return null;

        for (String cameraId : cameraIdList) {
            CameraCharacteristics characteristics = null;
            try {
                characteristics = manager.getCameraCharacteristics(cameraId);
            } catch (CameraAccessException e) {
                e.printStackTrace();
                return null;
            }
            if (bFront) {
                if (characteristics.get(CameraCharacteristics.LENS_FACING).intValue() == CameraMetadata.LENS_FACING_FRONT) {
                    sel_id = cameraId;
                    break;
                }
            } else {
                if (characteristics.get(CameraCharacteristics.LENS_FACING).intValue() == CameraMetadata.LENS_FACING_BACK) {
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


    //
    private void updateControls() {

    }

    public List<qnmCommProc_mis.IpcInfo> ipcInfoList = new ArrayList<>();

    public void showNvrList() {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        int i;
        qnmCommProc_mis.NvrInfo nvrInfo = pProcInfo.m_ipcProc.op.nvrInfo;

        ipcInfoList.clear();
        if (nvrInfo.usCnt != 0) {
            initPopWindows();


            for (i = 0; i < nvrInfo.usCnt; i++) {
                qnmCommProc_mis.IpcInfo pMem = nvrInfo.mems[i];
                ipcInfoList.add(pMem);
            }
            if (nvrInfo.usCnt >= 1) {
                nvr_channel_name.setVisibility(View.VISIBLE);

                qnmCommProc_mis.IpcInfo pMem = new qnmCommProc_mis.IpcInfo();
                pMem.iChannel = -1;
                pMem.name = "切换布局";//, 128);
                ipcInfoList.add(0, pMem);

                nvrListAdapter.notifyDataSetChanged();
                popupWindow.showAsDropDown(nvr_channel_name);
            }
            nvr_channel_name.setCompoundDrawables(null, null, pull_downDrawable, null);
        }
    }

    public void initPopWindows() {

        Activity act = getActivity();
        if  (  act instanceof  Activity_dlgTalk_av) {
            Activity_dlgTalk_av mOwner_wnd=(Activity_dlgTalk_av)act;
            //
            View view = LayoutInflater.from(mOwner_wnd).inflate(R.layout.popwin_layout, null);
            RecyclerView popListView = view.findViewById(R.id.pop_list_view);

            popListView.addItemDecoration(new DividerItemDecoration(mOwner_wnd, 1));
            nvrListAdapter = new NvrListAdapter(mOwner_wnd, ipcInfoList);
            popListView.setLayoutManager(new LinearLayoutManager(mOwner_wnd));
            popListView.setAdapter(nvrListAdapter);
            popupWindow = new PopupWindow(view, nvr_channel_name.getWidth(), mOwner_wnd.getWindow().getDecorView().getHeight() / 4, true);
            popupWindow.setAnimationStyle(R.anim.anim_pop);
            //popupWindow.Touchable = true;
            popupWindow.setOutsideTouchable(false);
            popupWindow.setFocusable(false);
//        popupWindow.setTouchInterceptor((v, event) -> true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#50000000")));
            popupWindow.setOnDismissListener(this);
        }
    }

    public static class NvrListAdapter extends RecyclerView.Adapter {
        private final Activity_dlgTalk_av mOwner_wnd;
        private final List<qnmCommProc_mis.IpcInfo> ipcInfoList;

        public NvrListAdapter(Activity_dlgTalk_av mOwner_wnd, List<qnmCommProc_mis.IpcInfo> ipcInfoList) {
            this.ipcInfoList = ipcInfoList;
            this.mOwner_wnd = mOwner_wnd;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder");
            View view = LayoutInflater.from(mOwner_wnd).inflate(R.layout.popwin_item, parent, false);
            return new CloudViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder");

            qnmCommProc_mis.IpcInfo ipcInfo = ipcInfoList.get(position);

            String nvrName = ipcInfo.name;

            ((CloudViewHolder) holder).nvr_name_tv.setText(nvrName);

            Log.d("nvr_name_tv.Click");
            ((CloudViewHolder) holder).nvr_name_tv.setOnClickListener(v -> {
                qnmCommProc_mis.IpcInfo ipcInfo1 = ipcInfoList.get(position);
                int clickItemChannel = ipcInfo1.iChannel;

                Log.d(position + "position");

                mOwner_wnd.m_fg_dlgTalk_av2.sendCurrentQiuJIEnableControl(clickItemChannel);
                if (nvrName.contains("(球机)")) {
                    mOwner_wnd.m_fg_dlgTalk_av2.showOperationRelativeLayout(true);
                } else {
                    mOwner_wnd.m_fg_dlgTalk_av2.showOperationRelativeLayout(false);
                    mOwner_wnd.m_fg_dlgTalk_av2.nvr_control.setVisibility(View.INVISIBLE);
                }
                mOwner_wnd.m_fg_dlgTalk_av2.popupWindow.dismiss();
                mOwner_wnd.m_fg_dlgTalk_av2.nvr_channel_name.setText(nvrName);

                //
                if  (  nvrName.contains("(3D)"))  {
                    //

                    //
                    mOwner_wnd.m_fg_dlgTalk_av2.m_b3ddw  =  true;
                }
                else  {

                    //
                    mOwner_wnd.m_fg_dlgTalk_av2.m_b3ddw = false;
                }

                //
            });
        }

        @Override
        public int getItemCount() {
            return ipcInfoList.size();
        }
    }

    public void sendCurrentQiuJIEnableControl(int clickItemChannel) {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        CIsTalker talker = findIsTalker(pProcInfo.pMisCnt,talker_ui64Id);
        if  (  talker  ==  null)  return;
        dlgTalkProc.DLG_TALK_var m_var = talker.m_var;

        Activity act = getActivity();
        if ( act instanceof  Activity_dlgTalk_av) {
            Activity_dlgTalk_av mOwner_wnd = (Activity_dlgTalk_av)act;
            //组名
            qyDefs.QY_MESSENGER_ID idInfo_imGrp_related = new qyDefs.QY_MESSENGER_ID();
            idInfo_imGrp_related.ui64Id = mOwner_wnd.mOwner_hTalk.get().m_talker.m_var.addr.idInfo.ui64Id;

            // 我
            qyDefs.QY_MESSENGER_ID idInfo_requester = new qyDefs.QY_MESSENGER_ID();
            idInfo_requester.ui64Id = 0;

            //mcu 的
            qyDefs.QY_MESSENGER_ID idInfo_to = new qyDefs.QY_MESSENGER_ID();
            idInfo_to.ui64Id = mOwner_wnd.mOwner_hTalk.get().m_talker.m_var.av.taskInfo.idInfo_starter.ui64Id;




            qnmCommProc_mis.ConfLayoutParam layoutParam = new qnmCommProc_mis.ConfLayoutParam();
            layoutParam.enlargeParam.usEnlargeType = CONST_enlargeType_img;

            layoutParam.enlargeParam.ui64Id = pProcInfo.m_ipcProc.op.nvrInfo.ui64Id;
            layoutParam.ipcParam.iChannel = clickItemChannel;

            mOwner_wnd.mOwner_hTalk.get().m_talker.m_var.currentSelectQiuJiChannel = clickItemChannel;

            dlgTalk_videoConference.qyFuncs_Droid.sendConfLayout(m_var.addr.idInfo.ui64Id, (byte) 0, (short) 0, (short) CONST_imOp_enlargeChannel, layoutParam, idInfo_imGrp_related, idInfo_requester, idInfo_to, "");
            Log.d("##球机sendConfLayout sendCurrentQiuJIEnableControl:" + clickItemChannel);
        }
    }

    public void showOperationRelativeLayout(boolean show) {
        Activity act = getActivity();
        if  (  act instanceof  Activity_dlgTalk_av) {
            Activity_dlgTalk_av mOwner_wnd = (Activity_dlgTalk_av) act;
            //
            if (show) {
                nvr_control.setImageResource(R.drawable.nvr_control);
                mOwner_wnd.m_fg_dlgTalk_av2.nvr_control.setVisibility(View.VISIBLE);
                mOwner_wnd.m_fg_dlgTalk_av2.left_menu_relative.setVisibility(View.VISIBLE);
                mOwner_wnd.m_fg_dlgTalk_av2.right_menu_relative.setVisibility(View.VISIBLE);
            } else {
                //  mOwner_wnd.m_fg_dlgTalk_av2.nvr_control.setVisibility(android.view.View.INVISIBLE);
                nvr_control.setImageResource(R.drawable.close_nvr_control);
                mOwner_wnd.m_fg_dlgTalk_av2.left_menu_relative.setVisibility(android.view.View.INVISIBLE);
                mOwner_wnd.m_fg_dlgTalk_av2.right_menu_relative.setVisibility(android.view.View.INVISIBLE);
            }
        }
    }

    public static class CloudViewHolder extends RecyclerView.ViewHolder {
        public TextView nvr_name_tv;

        public CloudViewHolder(View itemView) {
            super(itemView);
            nvr_name_tv = itemView.findViewById(R.id.nvr_name_tv);
        }
    }

    public void dlg_YTBtn_remote(int nResourceID, byte ucMouseStatus) {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

        CIsTalker talker = findIsTalker(pMisCnt,talker_ui64Id);
        if  (  talker  ==  null) return;
        dlgTalkProc.DLG_TALK_var m_var = talker.m_var;


        int uiObjType = 0;
        short usIndex_obj = 0;
        short usHelp_subIndex = 0;
        //
        byte ucCmd = 0;
        byte ucParam = 0;

        qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
        idInfo.ui64Id = pProcInfo.m_ipcProc.op.nvrInfo.ui64Id;

        int iChannel = m_var.currentSelectQiuJiChannel;
        Log.d("##球机dlg_YTBtn_remote" + iChannel);
        qyDefs.QY_MESSENGER_ID idInfo_imGrp_related = new qyDefs.QY_MESSENGER_ID();
        idInfo_imGrp_related.ui64Id = m_var.addr.idInfo.ui64Id;
        qyDefs.QY_MESSENGER_ID idInfo_to = new qyDefs.QY_MESSENGER_ID();
        if (pProcInfo.m_ipcProc.op.nvrInfo.ui64Id == 0) {
            idInfo_to.ui64Id = m_var.currentSelectQiuJiIdInfo;
        } else {
            idInfo_to.ui64Id = pProcInfo.m_ipcProc.op.nvrInfo.ui64Id;
        }

        do {
            switch (nResourceID) {
                case IDC_BUTTON_UP:
                    ucCmd = CONST_ptzCmd_up;
                    sendPtzReq(uiObjType, usIndex_obj, usHelp_subIndex, idInfo, iChannel, ucCmd, ucParam, ucMouseStatus, null,idInfo_imGrp_related, idInfo_to);
                    break;
                case IDC_BUTTON_DOWN:
                    ucCmd = CONST_ptzCmd_down;
                    sendPtzReq(uiObjType, usIndex_obj, usHelp_subIndex, idInfo, iChannel, ucCmd, ucParam, ucMouseStatus, null,idInfo_imGrp_related, idInfo_to);
                    break;
                case IDC_BUTTON_LEFT:
                    ucCmd = CONST_ptzCmd_left;
                    sendPtzReq(uiObjType, usIndex_obj, usHelp_subIndex, idInfo, iChannel, ucCmd, ucParam, ucMouseStatus, null,idInfo_imGrp_related, idInfo_to);
                    break;
                case IDC_BUTTON_RIGHT:
                    ucCmd = CONST_ptzCmd_right;
                    sendPtzReq(uiObjType, usIndex_obj, usHelp_subIndex, idInfo, iChannel, ucCmd, ucParam, ucMouseStatus, null,idInfo_imGrp_related, idInfo_to);
                    break;
                case IDC_BUTTON_ZOOM_IN:
                    ucCmd = CONST_ptzCmd_zoomIn;
                    sendPtzReq(uiObjType, usIndex_obj, usHelp_subIndex, idInfo, iChannel, ucCmd, ucParam, ucMouseStatus, null,idInfo_imGrp_related, idInfo_to);
                    break;
                case IDC_BUTTON_ZOOM_OUT:
                    ucCmd = CONST_ptzCmd_zoomOut;
                    sendPtzReq(uiObjType, usIndex_obj, usHelp_subIndex, idInfo, iChannel, ucCmd, ucParam, ucMouseStatus, null,idInfo_imGrp_related, idInfo_to);
                    break;
                default:
                    break;
            }
        } while (false);
    }


    //int  sendPtzReq(unsigned  int  uiObjType, unsigned  short  usIndex_obj, unsigned  short  usHelp_subIndex, QY_MESSENGER_ID  idInfo, int iChannel, unsigned  char  ucCmd, unsigned  char  ucParam, unsigned  char  ucMouseStatus, PTZ_cmdParamU  *  pParam,  QY_MESSENGER_ID  idInfo_imGrp_related, QY_MESSENGER_ID idInfo_to)
    public int sendPtzReq(int uiObjType, short usIndex_obj, short usHelp_subIndex, qyDefs.QY_MESSENGER_ID idInfo, int iChannel, byte ucCmd, byte ucParam, byte ucMouseStatus, qnmCommProc_mis.PTZ_cmdParamU pParamU, qyDefs.QY_MESSENGER_ID idInfo_imGrp_related, qyDefs.QY_MESSENGER_ID idInfo_to) {
        String str;
        str  =  uiObjType + " eeeee  " + usIndex_obj + "   " + usHelp_subIndex + "   " + idInfo.ui64Id + "   " + iChannel + "   " + ucCmd + "   " + ucParam + "   " + ucMouseStatus;
        if  (  idInfo_imGrp_related!=null)  str+= "   " + idInfo_imGrp_related.ui64Id;
        if  (  idInfo_to!=null) str+= "   " + idInfo_to.ui64Id;
        Log.d("qy",str);


        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();


        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

        int uiChannelType = CONST_channelType_media;

        qnmCommProc_mis.PTZ_control_cmd req = new qnmCommProc_mis.PTZ_control_cmd();
        req.uiType = imCommType_defs.Consts.CONST_imCommType_ptzControlCmd;
        req.idInfo_imGrp_related = idInfo_imGrp_related;
        req.cmdInfo.obj.uiObjType = uiObjType;
        req.cmdInfo.obj.usIndex_obj = usIndex_obj;
        req.cmdInfo.obj.usHelp_subIndex = usHelp_subIndex;
        req.cmdInfo.idInfo = idInfo;
        req.cmdInfo.iChannel = iChannel;
        req.cmdInfo.ucCmd = ucCmd;
        req.cmdInfo.ucParam = ucParam;
        req.cmdInfo.ucMouseStatus = ucMouseStatus;
        //
        req.paramU=pParamU;
        //
        long tStartTran = tmpFunc.qyFuncs.mytime();
        int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
//        byte[] data = req.toByteArray();
        int len = 0;//(uint)sizeof(PTZ_control_cmd)
        if (0 != postMsg2Mgr_mc((ctxCaller.CTX_caller) null, pMisCnt, (qnmCommProc_mis.MSG_ROUTE) null, qnmCommProc_mis.Consts.CONST_misMsgType_outputTask, (byte) 0, (short) CONST_qyCmd_sendMedia, tStartTran, uiTranNo, (short) 0, req, len, 0, idInfo_to.ui64Id, (int) uiChannelType, null, false))
            ;

        iErr = 0;
        return iErr;
    }

    public void onDismiss() {
        nvr_channel_name.setCompoundDrawables(null, null, pull_upDrawable, null);
    }

    public int currentVideoStatus = 0;

    //
    public void changeCurrentVideoStatus(int bAmplification) {

        Log.d("收到窗口状态:" + bAmplification);
        currentVideoStatus = bAmplification;
        if (bAmplification != 0)//response 画面放大大于0  缩小=0
        {

            min_viewport.setVisibility(android.view.View.VISIBLE);

        } else {
            if (popupWindow != null) {
                popupWindow.dismiss();
            }

            nvr_channel_name.setVisibility(android.view.View.INVISIBLE);
            nvr_channel_name.setText(mOwner_wnd.getString(R.string.nvr_channel_name));
            min_viewport.setVisibility(android.view.View.INVISIBLE);

            //
            Fragment_dlgTalk_av2  fg = this;

            fg.nvr_control.setVisibility(android.view.View.INVISIBLE);
            fg.showOperationRelativeLayout(false);
            //
            changeNvrControlStatus(0);
        }

    }

    void initMinButtonStates() {
        //
        //if  (  mOwner_wnd instanceof  Activity_dlgTalk_av)
        {
            //Activity_dlgTalk_av talk_av = (Activity_dlgTalk_av) mOwner_wnd;
            //Activity_dlgTalk hTalk = talk_av.mOwner_hTalk.get();
            //
            if (this.m_talker.m_var.respVideoStatusIsenlargement != -1) {
                if (this.m_talker.m_var.respVideoStatusIsenlargement == qyWmComm.Consts.Video_Status_Enlargement) {
                    min_viewport.setVisibility(android.view.View.VISIBLE);
                } else {
                    currentVideoStatus = 0;//初始画面
                    min_viewport.setVisibility(android.view.View.INVISIBLE);

                }
                this.m_talker.m_var.respVideoStatusIsenlargement = -1;
            }
        }


    }

    public void changeNvrControlStatus(int qiujiChannel) {
        Log.d(" changeNvrControlStatus:" + qiujiChannel);

        if (qiujiChannel >= 1) {
            nvr_control.setVisibility(android.view.View.VISIBLE);
        } else {
            nvr_control.setVisibility(android.view.View.GONE);
            showOperationRelativeLayout(false);
        }
    }

    public class MainHandler extends Handler {
        private final static int MSG_SEND_CAMERA_PARAMS0 = 0;
        private final static int MSG_SEND_CAMERA_PARAMS1 = 1;
        private final static int MSG_SEND_RECT_SIZE = 2;
        private final static int MSG_SEND_ZOOM_AREA = 3;
        private final static int MSG_SEND_ROTATE_DEG = 4;
        //
        private final static int MSG_talkDraw_needRestart = 10;

        //private WeakReference<TextureFromCameraActivity> mWeakActivity;
        Fragment_dlgTalk_av2 m_fg;

        public MainHandler(Fragment_dlgTalk_av2 fg) {
            //mWeakActivity = new WeakReference<TextureFromCameraActivity>(activity);
            m_fg = fg;
        }

        /**
         * Sends the updated camera parameters to the main thread.
         * <p>
         * Call from render thread.
         */
        public void sendCameraParams(int width, int height, float fps) {
            // The right way to do this is to bundle them up into an Object.  The lazy
            // way is to send two messages.
            sendMessage(obtainMessage(MSG_SEND_CAMERA_PARAMS0, width, height));
            sendMessage(obtainMessage(MSG_SEND_CAMERA_PARAMS1, (int) (fps * 1000), 0));
        }

        /**
         * Sends the updated rect size to the main thread.
         * <p>
         * Call from render thread.
         */
        public void sendRectSize(int width, int height) {
            sendMessage(obtainMessage(MSG_SEND_RECT_SIZE, width, height));
        }

        /**
         * Sends the updated zoom area to the main thread.
         * <p>
         * Call from render thread.
         */
        public void sendZoomArea(int width, int height) {
            sendMessage(obtainMessage(MSG_SEND_ZOOM_AREA, width, height));
        }

        /**
         * Sends the updated zoom area to the main thread.
         * <p>
         * Call from render thread.
         */
        public void sendRotateDeg(int rot) {
            sendMessage(obtainMessage(MSG_SEND_ROTATE_DEG, rot, 0));
        }

        //
        public void send_MSG_talkDraw_needRestart() {
            sendMessage(obtainMessage(MSG_talkDraw_needRestart, 0, 0));
        }


        @Override
        public void handleMessage(Message msg) {

            Fragment_dlgTalk_av2 activity = m_fg;
            if (activity == null) {
                //Log.d( "Got message for dead activity");
                return;
            }

            //
            Log.d("anc","Fragment_dlgTalk_av2: msg.what: " + msg.what + " msg.arg1: " + msg.arg1);

            //
            if  (  m_fg.m_talker==null)  {
                Log.e("at","m_fg.m_talker is null");
                return;
            }
            dlgTalkProc.DLG_TALK_var m_var = m_fg.m_talker.m_var;
            if  (  m_var==null)  {
                Log.d("at","Fg_av2 err: m_var is null");
                return;
            }

            //
            switch (msg.what) {
                case MSG_SEND_CAMERA_PARAMS0: {
                    activity.mCameraPreviewWidth_unused = msg.arg1;
                    activity.mCameraPreviewHeight_unused = msg.arg2;
                    break;
                }
                case MSG_SEND_CAMERA_PARAMS1: {
                    activity.mCameraPreviewFps_unused = msg.arg1 / 1000.0f;
                    activity.updateControls();
                    break;
                }
                case MSG_SEND_RECT_SIZE: {
                    activity.mRectWidth = msg.arg1;
                    activity.mRectHeight = msg.arg2;
                    activity.updateControls();
                    break;
                }
                case MSG_SEND_ZOOM_AREA: {
                    activity.mZoomWidth = msg.arg1;
                    activity.mZoomHeight = msg.arg2;
                    activity.updateControls();
                    break;
                }
                case MSG_SEND_ROTATE_DEG: {
                    activity.mRotateDeg = msg.arg1;
                    activity.updateControls();
                    break;
                }
                case MSG_talkDraw_needRestart: {
                    //Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) activity.getActivity();
                    //if (dlgTalk_av != null)
                    {
                        if (m_var.m_talkAvVar.m_bTalkDraw_needRestart) {
                            //
                            Object  hWnd  =  null;//dlgTalk_av.mOwner_hTalk;
                            int iWndContentType  =  CONST_qyWndContentType_talker;
                            hWnd  = m_fg;
                            iWndContentType=CONST_fgType_talk_av2;
                            //
                            pauseTaskAv(hWnd,iWndContentType, false,"fg_dlgTalk_av2.Handler, MSG_talkDraw_needRestart");
                            //
                            m_var.m_talkAvVar.m_bDlgTalk_av_readyFor_talkDrawRestart = true;
                        }
                    }
                    break;
                }
                case CONST_qyWm_postComm: {
                    //
                    int wParam = msg.arg1;
                    int lParam = msg.arg2;
                    //
                    switch (wParam) {

                        case qyWmComm.Consts.CONST_qyWmParam_chkTalkDraw: {
                            doChkTalkDraw(m_var);
                            m_var.dwTickCnt_waitForOk_dlgTalk_av1 = 0;
                            break;
                        }
                        /*
                        case  qyWmComm.Consts.CONST_qyWmParam_doTask: {
                              Log.d("at","l4168: doTask");
                              //
                              short usOp = (short)lParam;
                              //
                              if  (  usOp==CONST_imOp_recv_accept) {
                                  //
                                  Log.d("at","accept");
                                  //
                                  Activity act = m_fg.getActivity();
                                  doTask_av_accept(act, m_var);
                              }


                            //
                            break;
                        }

                         */
                        //
                        default:
                            break;
                    }
                    //
                    break;
                }
                default:
                    Log.e("Unknown message " + msg.what);
                    break;
            }
        }
    }
}




