package com.qycx.base;

import static com.qycx.anc.ancCommProc_defs.Consts.CONST_anCommType_refreshRecentFriendsReq1;
import static com.qycx.base.avCallProc.talk_doAvCall;
import static com.qycx.base.dlgTalkProc.qyFuncs_Droid.dlgTalk_OnQyComm;
import static com.qycx.base.dlgTalkProc.qyFuncs_Droid.isTalkerShadowMgr;
import static com.qycx.base.dlgTalkProc.qyFuncs_Droid.tmpHandler_showMsg_input;
import static com.qycx.base.dlgTalkProc.qyFuncs_Droid.tmpHandler_showMsg_talk;
import static com.qycx.base.dlgTalkProc.qyFuncs_Droid.tmpHandler_showMsg_task;
import static com.qycx.base.dlgTalkProc.qyFuncs_Droid.tmpHandler_showMsg_taskStatus;
import static com.qycx.base.dlgTalkProc_help.qyFuncs_Droid.findTalker;
import static com.qycx.base.doApplyForPlayer.qyFuncs.chkPlayers;
import static com.qycx.base.isTalkerQProc.addToIsTalkerQ;
import static com.qycx.base.isTalkerQProc.removeFromIsTalkerQ;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_applyForTalkerShadow_qmc;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_applyForWork;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_input;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_talk;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_taskStatus;
import static com.qycx.base.qyComm.qyFuncs.isRcOk;
import static com.qycx.base.qyMcMainMediaReceiver.qyFuncs.chkMixerPlayer;
import static com.qycx.base.qyMcMain_global.g.g_pQyMc;
import static com.qycx.base.tmpFunc.qyFuncs.myGetCurrentThreadId;
import static com.qycx.base.tmpFunc.qyFuncs.mytime;

import android.util.Log;

import com.qycx.Activity_dlgTalk;
import com.qycx.Activity_dlgTalk_av;
import com.qycx.Fragment_dlgTalk_av2;
import com.qycx.MainActivity;

import java.util.ArrayList;

//
public class CIsTalker {

    //
    public dlgTalkProc.DLG_TALK_var m_var;

    //
    public qmCommFunc_mis.IsTalkUsage[]mems;

    //
    CIsTalker()
    {
        //
        m_var = new dlgTalkProc.DLG_TALK_var();

        //
        mems = new qmCommFunc_mis.IsTalkUsage[10];

        int  i;
        for  (  i  =  0;i  <  mems.length;i++)  {
            mems[i]=new qmCommFunc_mis.IsTalkUsage();
        }

        return;
    }


    //
    public int init(qmCommFunc_mis.MIS_CNT pMisCnt, long ui64Id)
    {
        int  iErr  =  -1;
        if  (  pMisCnt==null)  return  -1;
        ctxQmc.CCtxQmc  pProcInfo = pMisCnt.pProcInfoParam;
        if  (  pProcInfo==null)  return  -1;
        ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;
        if  (  pQyMc==null)  return  -1;

        String str;

        //
        if  (  ui64Id==0)  return  -1;

        //
        do {

            //
            m_var.m_pMisCnt = pMisCnt;

            //
            m_var.tickCountOnCreate = tmpFunc.qyFuncs.myGetTickCount(null);
            m_var.m_pProcessQ = new ArrayList<>();
            m_var.addr.idInfo.ui64Id = ui64Id;

            ///////////////////////
            if  (  true  ) {
                myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
                qyDefs.QY_MESSENGER_ID idInfo = m_var.addr.idInfo;


                //
                qnmCommProc_mis.QY_MESSENGER_INFO msgrInfo = new qnmCommProc_mis.QY_MESSENGER_INFO();
                qnmCommProc_mis.QY_MESSENGER_REGINFO regInfo = new qnmCommProc_mis.QY_MESSENGER_REGINFO();
                if (!myDb.QM_dbFuncs.pf_bGetMessengerInfoBySth.call(pDbFuncs, pQyMc.gui.pDb, pQyMc.cfg.db.iDbType, qnmCommProc.qyFuncs.CONST_fieldIdTable_en, "", idInfo, 0, msgrInfo, regInfo, null, null)) {
                    str = "dlgTalk.OnCreate failed, bGetMessengerInfoBySth failed, " + idInfo.ui64Id;
                    Log.e("at", str);
                    break;
                }

                m_var.addr.uiObjType = msgrInfo.uiType;

                if (m_var.addr.uiObjType == qyDefs.Consts.CONST_objType_imGrp) {
                    qnmCommProc_mis.IM_GRP_INFO grpInfo = new qnmCommProc_mis.IM_GRP_INFO();
                    if (!myDb.QM_dbFuncs.pf_bGetImGrpInfoBySth.call(pQyMc.gui.pDb, pQyMc.cfg.db.iDbType, "", idInfo, grpInfo)) {

                    }
                    m_var.m_str_title = grpInfo.name;
                } else {
                    str = regInfo.syr;
                    m_var.m_str_title = str;
                }
            }



            //
            iErr  =  0;

        } while (false);

        //
        return   iErr;
    }


    public int exit()
    {
        //
        if  (  m_var !=null) {
            //
            Log.d("at","talker.exit l128:  idInfo "  +  m_var.addr.idInfo.ui64Id);

            //
            m_var.m_pProcessQ = null;

        }

        //
        return  0;
    }

    //
    //
    public int addIsTalkUsage(qmCommFunc_mis.MIS_CNT pMisCnt, Object obj_usr, int obj_usr_iType)
    {
        int  iErr =  -1;

        ctxQyMc.CCtxQyMc  pQyMc  =  g_pQyMc;
        //
        if (  pQyMc.appParams.bDbg) {
            int cur_threadId = myGetCurrentThreadId();
            if (cur_threadId != pQyMc.gui.dwThreadId) {
                return -1;
            }
        }

        do  {
            if  (  obj_usr  ==  null  )  return  -1;

            //
            CIsTalker talker=this;//findIsTalker(pMisCnt,ui64Id);
            if  (  talker==null)break;

            boolean tmp_bFound = false;
            int  i;
            for  (  i  =0;  i<talker.mems.length;i++)  {
                qmCommFunc_mis.IsTalkUsage  mem=talker.mems[i];
                if  (  mem.obj_usr==  obj_usr  ) {
                    tmp_bFound=true;
                    break;
                }
            }
            if  (  tmp_bFound)  {
                com.qycx.base.util.Log.d("at","addToIsTalkUsage failed, already added");
                break;
            }
            qmCommFunc_mis.IsTalkUsage mem=null;
            for  (  i  =  0;  i  <talker.mems.length;i++ )  {
                mem =talker.mems[i];
                //
                if  (  mem.obj_usr == null)  {
                    break;
                }
            }
            if  (  i  ==  talker.mems.length)  {
                com.qycx.base.util.Log.d("at","addToIsTalkUsage failed, mems full");
                break;
            }
            //
            mem.obj_usr=obj_usr;
            mem.obj_usr_iType=obj_usr_iType;

            //
            iErr  =  0;
        } while  (  false  );


        return  iErr;
    }

    //
    public int removeIsTalkUsage(qmCommFunc_mis.MIS_CNT pMisCnt, Object obj_usr)
    {
        int  iErr =  -1;

        ctxQyMc.CCtxQyMc  pQyMc  =  g_pQyMc;
        //
        if (  pQyMc.appParams.bDbg) {
            int cur_threadId = myGetCurrentThreadId();
            if (cur_threadId != pQyMc.gui.dwThreadId) {
                return -1;
            }
        }

        do  {
            if  (  obj_usr  ==  null  )  return  -1;


            //
            CIsTalker talker=this;//findIsTalker(pMisCnt,ui64Id);
            if  (  talker==null)break;

            boolean tmp_bFound = false;
            int  i;
            for  (  i  =0;  i<talker.mems.length;i++)  {
                qmCommFunc_mis.IsTalkUsage  mem=talker.mems[i];
                //
                if  (  mem.obj_usr==  obj_usr  ) {
                    tmp_bFound=true;
                    //
                    mem.obj_usr=null;
                    mem.obj_usr_iType=0;
                }
                //
                continue;
            }

            //  当没有usage时，应从isTalkerQ里移除.
            for  (  i  =  0;  i  <  talker.mems.length;i++)  {
                qmCommFunc_mis.IsTalkUsage  mem=talker.mems[i];
                //
                if  (  mem.obj_usr!=null)  {
                    break;
                }
            }
            if  (  i  ==  talker.mems.length)  {  //  没有使用者了
                removeFromIsTalkerQ(pMisCnt,m_var.addr.idInfo.ui64Id);
            }


            //
            iErr  =  0;
        } while  (  false  );


        return  iErr;
    }

    //
    public int gui_onTimer(Object p0,Object p1,Object p2)
    {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo=pQyMc.get_pProcInfo();
        MainActivity mainWnd = (MainActivity)pQyMc.gui.hMainWnd;
        String str;

        //
        if  (  false  ) {
            //Log.d("talker.gui_onTimer", "mainWnd.loopCtrl"  + mainWnd.var_common.loopCtrl  +  " idInfo "  +  m_var.addr.idInfo.ui64Id);
        }

        //
        long dwTickCnt_start_myTimerProc = tmpFunc.qyFuncs.myGetTickCount(null);


        m_var.loopCtrl_timer++;


        //
        if (isTalkerShadowMgr(m_var.addr)) {
            if (pProcInfo.cfg.debugStatusInfo.ucbShowDlgTalkStatus) {
                long dwTickCnt_end_myTimerProc = tmpFunc.qyFuncs.myGetTickCount(null);
                long iDiffInMs = (dwTickCnt_end_myTimerProc - dwTickCnt_start_myTimerProc);
                str = String.format("dlgTalk_myTimeProc: talker%d, nElapseInMs %sms", m_var.addr.idInfo.ui64Id, iDiffInMs);
                com.qycx.base.util.Log.d(str);
            }
        }

        //
        if  (  0  ==  (  m_var.loopCtrl_timer  %  5)  ) {
            chkPlayers(pProcInfo);
        }
        //
        if (m_var.av.taskInfo.bTaskExists)  {
            //
            if  (  pProcInfo.cfg.ucb_USE_audioMixer)  {
                //
                chkMixerPlayer(m_var.av.taskInfo.iTaskId);
            }

        }


        //
        int  i;
        for  (  i  =  0;i  <  mems.length;i++)  {
            qmCommFunc_mis.IsTalkUsage mem = mems[i];
            if  (mem==null)continue;
            if  (mem.obj_usr==null) continue;;
            //
            if  ( mem.obj_usr instanceof Activity_dlgTalk )  {
                Activity_dlgTalk hTalk = (Activity_dlgTalk) mem.obj_usr;
                hTalk.gui_onTimer.call(null,null,null);
            }

        }

        //  这里悬浮窗的定时器
        //
        if  (  mainWnd.m_floatingView_fg!=null)  {
            if  (  mainWnd.m_floatingView_fg instanceof Fragment_dlgTalk_av2) {
                Fragment_dlgTalk_av2 fg = (Fragment_dlgTalk_av2) mainWnd.m_floatingView_fg;
                if ( fg.talker_ui64Id== m_var.addr.idInfo.ui64Id) {
                    fg.gui_onTimer(p0, p1, p2);
                }
            }
        }

        //
        pProcInfo.do_talk_OnTimer(this);


        //
        return  0;
    }


    //
    public int gui_procMsg(qnmCommProc_mis.MIS_MSGU pMsgU) {

            int iErr = -1;
            ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
            myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            //
            boolean bDbg = false;
            String str;

            //
            if (null == pMsgU) {
                return iErr;
            }

            //
            if (pMsgU.uiType == qnmCommProc_mis.Consts.CONST_misMsgType_input) {
                qnmCommProc_mis.MIS_MSG_INPUT pMsg = (qnmCommProc_mis.MIS_MSG_INPUT) pMsgU;
                if (pMsg == null) {
                    com.qycx.base.util.Log.d("pMsg is null ");
                    return iErr;
                }
                qnmCommProc_mis.IM_CONTENTU pContent = pMsg.data.buf;
                if (pContent == null) {
                    com.qycx.base.util.Log.d("pContent is null ");
                    return iErr;
                }
                switch (pContent.uiType) {

                    case imCommType_defs.Consts.CONST_imCommType_confLayout: {
                        if (!m_var.av.taskInfo.bTaskExists) return iErr;
                        if (m_var.av.taskInfo.ucbStarter) break;

                        //  以下为参会人
                        qnmCommProc_mis.CONF_LAYOUT confLayout = (qnmCommProc_mis.CONF_LAYOUT) pContent;
                        if (0 != confLayout.ucbResp) {
                            //
                            qnmCommProc_mis.IM_CONTENTU tmpContent = pContent;
                            if (confLayout.usOp == qnmCommProc_mis.Consts.CONST_imOp_phone_respCurr) {

                                qyDefs.QY_MESSENGER_ID idInfo_curr = new qyDefs.QY_MESSENGER_ID();
                                idInfo_curr.ui64Id = confLayout.confLayoutParam.resp.curr_idinfo;
                                int ii = 0;
                            }

                            if (confLayout.usOp == qnmCommProc_mis.Consts.CONST_imOp_respChannel) {
                                int ichannel = confLayout.confLayoutParam.ipcParam.iChannel;
                                str = confLayout.confLayoutParam.ipcParam.channelName;
                                int ii = 0;
                            }
                        }
                    }
                    break;
                    case imCommType_defs.Consts.CONST_imCommType_statusConfLayoutParam: {
                        //这里接收 放大缩小的改变变化
                        qnmCommProc_mis.StatusConfLayoutParam statusConfLayoutParam = (qnmCommProc_mis.StatusConfLayoutParam) pContent;
                        int bAmplification = statusConfLayoutParam.confLayoutParam.respVideoStatus.bAmplification;
                        com.qycx.base.util.Log.d("通用通道放大缩小变化 1 为放大:" + bAmplification);
                        int qiuji = statusConfLayoutParam.confLayoutParam.ipcParam.iChannel;
                        String str1 = statusConfLayoutParam.confLayoutParam.ipcParam.channelName;
                        int i = 0;


                        Activity_dlgTalk hTalk = null;
                        if  (  hTalk==null) break;
                        //
                        if (null != hTalk.m_talker.m_var.m_hTool_dlgTalk_av) {
                            Activity_dlgTalk_av hTalk_av = (Activity_dlgTalk_av) hTalk.m_talker.m_var.m_hTool_dlgTalk_av;
                            hTalk_av.m_fg_dlgTalk_av2.changeCurrentVideoStatus(bAmplification);
                            com.qycx.base.util.Log.d("##球机放大缩小变化通道数从>=1开始,其他为非球机通道画面,当前通道:" + qiuji);
                            hTalk.m_talker.m_var.currentSelectQiuJiChannel = qiuji;
                            long qiuji_ScaleId = ((qnmCommProc_mis.StatusConfLayoutParam) pContent).confLayoutParam.enlargeParam.ui64Id;
                            //这里拿到那个ID号了哈
                            com.qycx.base.util.Log.d("##球机qiuji_ScaleId  qiuji" + qiuji_ScaleId + "    " + qiuji);
                            hTalk.m_talker.m_var.currentSelectQiuJiIdInfo = qiuji_ScaleId;

                            hTalk_av.m_fg_dlgTalk_av2.changeNvrControlStatus(qiuji);

                        }


                    }
                    break;
                    case imCommType_defs.Consts.CONST_imCommType_confNvrInfo: {
                        //if (!m_var.av.taskInfo.bTaskExists) return iErr;
                        if (!m_var.av.taskInfo.bTaskExists) return iErr;
                        //
                        if (m_var.av.taskInfo.ucbStarter) break;

                        if (0 != ((qnmCommProc_mis.ConfNvrInfo) pContent).ucbResp) { //  发给requester

                            qnmCommProc_mis.ConfNvrInfo confNvrInfo = (qnmCommProc_mis.ConfNvrInfo) pContent;
                            qyDefs.QY_MESSENGER_ID idInfo_to = confNvrInfo.idInfo_requester;
//                        idInfo_to = confNvrInfo.idInfo_requester;
                            qyDefs.QY_MESSENGER_ID idInfo_requester = confNvrInfo.idInfo_requester;
//                        idInfo_requester = confNvrInfo.idInfo_requester;
                            //

                            pProcInfo.m_ipcProc.op.nvrInfo = confNvrInfo.nvrInfo;

                            com.qycx.base.util.Log.d("pProcInfo.m_ipcProc.op.nvrInfo:" + pProcInfo.m_ipcProc.op.nvrInfo);

                            //Activity_dlgTalk hTalk = this.mhTalk;
                            if (null != m_var.m_hTool_dlgTalk_av) {
                                //
                                if  (  m_var.m_hTool_dlgTalk_av instanceof  Activity_dlgTalk_av) {
                                    Activity_dlgTalk_av hTalk_av = (Activity_dlgTalk_av) m_var.m_hTool_dlgTalk_av;
                                    hTalk_av.m_fg_dlgTalk_av2.showNvrList();
                                }
                                else  if  (  m_var.m_hTool_dlgTalk_av instanceof MainActivity)  {
                                    MainActivity mainWnd = (MainActivity) m_var.m_hTool_dlgTalk_av;
                                    if  (  null!=mainWnd.m_floatingView_fg  )  {
                                        if  (  mainWnd.m_floatingView_fg instanceof  Fragment_dlgTalk_av2)  {
                                            Fragment_dlgTalk_av2 fg_av2 = ( Fragment_dlgTalk_av2) mainWnd.m_floatingView_fg;
                                            fg_av2.showNvrList();
                                        }
                                    }
                                }

                            }

                        }
                    }
                    break;
                    case imCommType_defs.Consts.CONST_imCommType_taskProcReq:
                        qnmCommProc_mis.TASK_PROC_REQ taskProcReq = (qnmCommProc_mis.TASK_PROC_REQ) pContent;
                        if (taskProcReq.usOp == qnmCommProc_mis.Consts.CONST_imOp_recv_applyForOrgReq) {
                            qySyncCommProc.CQyMalloc malloc_pMsgTask_o = new qySyncCommProc.CQyMalloc();
                            {
                                int size = 128;//sizeof(MIS_MSG_TASK);
                                qnmCommProc_mis.CMIS_MSGU pMsgTask_o = new qnmCommProc_mis.CMIS_MSGU();// (qnmCommProc_mis.MIS_MSG_TASK) malloc_pMsgTask_o.mallocf(size);
                                if (null == pMsgTask_o) return iErr;

                                if (pMsg.iTaskId == 0) return iErr;
                                int index_taskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoIndexBySth(mainActivity.var_common.ctxCaller, pProcInfo, pMsg.iTaskId);
                                if (index_taskInfo < 0) return iErr;
                                qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, pProcInfo, index_taskInfo, "fg_dlgTalk_msgList.gui_procMsg");
                                if (pTaskInfo == null) return iErr;
                                qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
                                if (pTaskData == null) return iErr;

                                //MIS_MSG_TASK pMsgTask_i = findTaskInProcessQ(&m_var, pMsg->iTaskId);
                                qnmCommProc_mis.MIS_MSG_TASK pMsgTask_i = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
                                {
                                    if (null == pMsgTask_i) return iErr;
                                    if (pMsgTask_i.uiType != qnmCommProc_mis.Consts.CONST_misMsgType_task)
                                        return iErr;
                                    //
                                    qnmCommProc_mis.IM_CONTENTU pContentTask_i = pMsgTask_i.data.buf;// (qnmCommProc_mis.IM_CONTENTU) pMsgTask_i.data.buf;
                                    if (pContentTask_i.uiType != imCommType_defs.Consts.CONST_imCommType_transferAvInfo)
                                        return iErr;
                                    //
                                    if (true) {   //  2012/05/25
                                        boolean bPermitted = false;
                                        bPermitted = true;
                                        //
                                        if (!bPermitted) {
                                            return iErr;
                                        }
                                    }
                                    //
                                    qyMcMainProc.PARAM_findOrgMsg param = new qyMcMainProc.PARAM_findOrgMsg();
//                                    qyFuncs.mymemset((Object) (byte) param, 0, sizeof(PARAM_findOrgMsg));
                                    param.pMsgTask_o = pMsgTask_o;
                                    if (pProcInfo.processQ_media.qTraverse(mainActivity.var_common.ctxCaller, findOrgMsgAndProcess_is.qyFuncs.tmpHandler_findOrgMsgAndProc, pMsgTask_i, param) <= 0) {
                                        return iErr;
                                    }

                                    if (pMsgTask_o.msgU.uiType != qnmCommProc_mis.Consts.CONST_misMsgType_task) {
                                        return iErr;
                                    }
                                    qnmCommProc_mis.IM_CONTENTU pContentTask_o = ((qnmCommProc_mis.MIS_MSG_TASK) pMsgTask_o.msgU).data.buf;
                                    if (pContentTask_o.uiType != imCommType_defs.Consts.CONST_imCommType_transferAvInfo)
                                        return iErr;

                                    int lenInBytes = ((qnmCommProc_mis.MIS_MSG_TASK) pMsgTask_o.msgU).lenInBytes;// (int) (pMsgTask_o.lenInBytes Marshal.OffsetOf < MIS_MSG_routeTalkData > ("buf"));
                                    if (lenInBytes < 0) return iErr;
                                    //
                                    qyDefs.QY_MESSENGER_ID idInfo_dst = new qyDefs.QY_MESSENGER_ID();
                                    idInfo_dst.ui64Id = pMsg.data.route.idInfo_from.ui64Id;
                                    //  set idInfo_logicalPeer temporiary.
                                    if (m_var.addr.idInfo.ui64Id != idInfo_dst.ui64Id) {
                                        ((qnmCommProc_mis.TRANSFER_AV_INFO) pContent).idInfo_logicalPeer.ui64Id = m_var.addr.idInfo.ui64Id;
                                    }
                                    qyDefs.QY_MESSENGER_ID idInfo_logicalPeer = m_var.addr.idInfo;
//                                postMsg2MgrFunc.qyFuncs.postMsgTask2Mgr_mc(mainActivity.var_common.ctxCaller, pMisCnt, qnmCommProc_mis.Consts.CONST_misMsgType_outputTask, (byte) 0, pMsgTask_o.usCode, pMsgTask_o.tStartTime, pMsgTask_o.uiTranNo, (short) 0, pMsgTask_o.iTaskId, pMsgTask_o.uiTaskType, pMsgTask_o.data.buf.array(), (int) lenInBytes, idInfo_logicalPeer, pMsgTask_o.idInfo_taskSender, pMsgTask_o.idInfo_taskReceiver, idInfo_dst, 0, (qnmCommProc_mis.MIS_MSGU) null, false);
                                    //pContentTask_o->transferAvInfo.idInfo_logicalPeer.ui64Id  =  0;
                                    //
                                    str = String.format("dlgTalk: send transferAvInfo again to %s. tn %s", idInfo_dst.ui64Id, ((qnmCommProc_mis.MIS_MSG_TASK) pMsgTask_o.msgU).uiTranNo);
                                    showInfo_open.qyFuncs.showInfo_open( str);

                                    //
                                    break;
                                }
                            }
                        }
                        //  2014/10/09
                        break;
                    case imCommType_defs.Consts.CONST_imCommType_taskProcResp:
                        if (((qnmCommProc_mis.TASK_PROC_RESP) pContent).usOp_org == qnmCommProc_mis.Consts.CONST_imOp_recv_applyForChkTaskAlive) {
                            /**/
                        }
                        break;
                    case  imCommType_defs.Consts.CONST_imCommType_confReq:  {
                        //
                        qnmCommProc_mis.CONF_req  pReq=(qnmCommProc_mis.CONF_req) pContent;
                        //
                        if  (  !isRcOk(pMsg.usCode)) {

                            //
                            Object  hWnd  =  findTalker(pMisCnt,pReq.idInfo_imGrp_related);
                            if  (  hWnd!=null)  {
                                Activity_dlgTalk hTalk=(Activity_dlgTalk) hWnd;
                                talk_doAvCall(hTalk,hTalk.m_talker.m_var.addr.idInfo.ui64Id,pProcInfo.av.talk_doAv_info.usAvLevel,true);
                                break;
                            }
                            //
                            talk_doAvCall(mainActivity,pReq.idInfo_imGrp_related.ui64Id,pProcInfo.av.talk_doAv_info.usAvLevel,true);

                        }

                    }
                    break;
                    case  imCommType_defs.Consts.CONST_imCommType_refreshRecentFriendsResp:  {
                        //
                          this.talker_OnQyComm(m_var,0,pMsg);

                          }
                          break;
                    case  CONST_anCommType_refreshRecentFriendsReq1:

                          this.talker_OnQyComm(m_var,0,pMsg);

                          break;
                    default:
                        break;
                }

                test.testCls_Droid.testChk("talk_msgList.gui_procMsg.402");
            }

            //
            test.testCls_Droid.testChk("talk_msgList.gui_procMsg.406");

            dlgTalkProc.PARAM_PROC_ROW ppr = new dlgTalkProc.PARAM_PROC_ROW();
            this.tmpHandler_showMsg_new(ppr, (Object) pMsgU);

            //
            test.testCls_Droid.testChk("talk_msgList.gui_procMsg.407");


            //
            iErr = 0;

            return iErr;

        }

    //
    public int tmpHandler_showMsg_new(Object p1, Object pMsgParam) {

        int iErr = -1;
        //Activity_dlgTalk hDlg = (Activity_dlgTalk)  hDlgTalkParam;
        boolean bDbg = false;

        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

        //help_getDlgTalkVar.CHelp_getDlgTalkVar help_getDlgTalkVar = new help_getDlgTalkVar.CHelp_getDlgTalkVar();
        {
            ;
            //dlgTalkProc.DLG_TALK_var pm_var = talker.m_var;//(dlgTalkProc.DLG_TALK_var) help_getDlgTalkVar.getVar(hDlg);
            //if (null == pm_var) return -1;
            ///dlgTalkProc.DLG_TALK_var m_var = pm_var;

            dlgTalkProc.PARAM_PROC_ROW pParam = (dlgTalkProc.PARAM_PROC_ROW) p1;
            qnmCommProc_mis.MIS_MSGU pMsg = (qnmCommProc_mis.MIS_MSGU) pMsgParam;

            long t = 0;
            int tmpiRet = 0;
            //
            //TCHAR tBuf[1024] = "";
            int i;
            //

            if ( null == pParam || null == pMsg) return -1;

            if (!isTalkerShadowMgr(m_var.addr)) return iErr;
            //TALKER_shadow_mgr pShadowMgr = (TALKER_shadow_mgr)m_var.pShadowInfo;
            //if (!pShadowMgr) return iErr;

            //if (!bReady_toShowMsg(hDlg)) return iErr;

            t = mytime();

            switch (pMsg.uiType) {
                case CONST_misMsgType_talk:
                    qnmCommProc_mis.MIS_MSG_TALK talk = (qnmCommProc_mis.MIS_MSG_TALK) pMsg;
                    if (0 != talk.tStartTime_processed) break;
                    talk.tStartTime_processed = mytime();   //  2007/10/13
                    m_var.nMsgTalks_processed_inProcessQ++;             //  2012/05/20

                    pParam.bMsgNeedRemoved = true;     //  2009/10/18

                    //  2011/03/15
                    //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, "", "", ("show msg_talk"));

                    //
                    if (0 != tmpHandler_showMsg_talk_new( m_var, p1, pMsgParam)) {

                        return iErr;
                    }


                    break;
                case CONST_misMsgType_task:
                    qnmCommProc_mis.MIS_MSG_TASK task = (qnmCommProc_mis.MIS_MSG_TASK) pMsg;
                    if (0 != task.tStartTime_processed) break;
                    task.tStartTime_processed = mytime();  //  2007/10/13, ´Ë¸³ÖµÊÇÎªÁË±ÜÃâÏûÏ¢±»ÖØ¸´´¦Àí¡£Ô­À´ÊÇÔÚ´ËcaseµÄ×îÏÂÃæ£¬ÏÖÔÚÒÆµ½ÕâÀï¡£ÊÇÒòÎª£¬Èç¹ûÖÐ¼ä´¦Àí³ö´í£¬break»ògoto µÄ»°£¬¾ÍÎÞ·¨¸³ÖµÁË£¬ÓÚÊÇ¾ÍÒªÒ»ÔÙÖØ¸´´¦Àí´ËÏûÏ¢£¬ÓÖÖØ¸´Ìø³ö£¬Ôì³ÉºóÐøÏûÏ¢ÎÞ·¨´¦ÀíµÄÏÖÏó¡£

                    //
                    if (0 == task.iTaskId)
                        pParam.bMsgNeedRemoved = true;        //  2009/10/18

                    //
                    if (0 != tmpHandler_showMsg_task_new( m_var, p1, pMsgParam))
                        return iErr;

                    break;

                case CONST_misMsgType_input:    //  2014/09/05
                    qnmCommProc_mis.MIS_MSG_INPUT input = (qnmCommProc_mis.MIS_MSG_INPUT) pMsg;
                    if (0 != input.tStartTime_processed) break;
                    input.tStartTime_processed = mytime();

                    //
                    pParam.bMsgNeedRemoved = true;

                    //
                    test.testCls_Droid.testChk("tmpHandler_showMsg.1941");

                    //
                    if (0 != tmpHandler_showMsg_input( this, p1, pMsgParam)) {
                        //
                        test.testCls_Droid.testChk("th_showMsg.1946");
                        //
                        return iErr;
                    }

                    test.testCls_Droid.testChk("tmpHandler_showMsg.1951");

                    //
                    break;

                case CONST_misMsgType_taskStatus: {
                    qnmCommProc_mis.MIS_MSG_taskStatus taskStatus = (qnmCommProc_mis.MIS_MSG_taskStatus) pMsg;
                    if (0 != taskStatus.tStartTime_processed) break;
                    taskStatus.tStartTime_processed = mytime(); //  2007/10/13,
                    pParam.bMsgNeedRemoved = true;     //  2009/10/18
                    if (0 != tmpHandler_showMsg_taskStatus_new( m_var, p1, pMsgParam))
                        return iErr;
                }
                break;

                case qnmCommProc_mis.Consts.CONST_misMsgType_notifyTaskEnd_qmc:
                    /**/
                    break;

                case CONST_misMsgType_applyForWork:

                    break;

                case CONST_misMsgType_applyForTalkerShadow_qmc: {  //  2012/04/23

                }
                break;
                default:
                    //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo.who_showInfo, 0, "", "", ("tmpHandler_showMsg failed, unprocessed msgType %d"), pMsg->uiType);
                    return iErr;
            }

        }

        iErr = 0;
        return iErr;
    }


    //
    public int tmpHandler_showMsg_talk_new(dlgTalkProc.DLG_TALK_var m_var, Object p1, Object pMsgParam) {

        CIsTalker talker = this;

        int i;
        for  (  i =  0;  i  <  talker.mems.length;i++)  {
            qmCommFunc_mis.IsTalkUsage mem = talker.mems[i];
            if  (  mem==null)continue;
            if  (  mem.obj_usr==null) continue;;
            if  ( mem.obj_usr instanceof  Activity_dlgTalk)  {
                Activity_dlgTalk hTalk = (Activity_dlgTalk) mem.obj_usr;
                tmpHandler_showMsg_talk(hTalk,m_var,p1,pMsgParam);
            }
        }
        return 0;
    }

    public int tmpHandler_showMsg_task_new(dlgTalkProc.DLG_TALK_var m_var, Object p1, Object pMsgParam) {

        CIsTalker talker = this;

        int i;
        for  (  i =  0;  i  <  talker.mems.length;i++)  {
            qmCommFunc_mis.IsTalkUsage mem = talker.mems[i];
            if  (  mem==null)continue;
            if  (  mem.obj_usr==null) continue;;
            if  ( mem.obj_usr instanceof  Activity_dlgTalk)  {
                Activity_dlgTalk hTalk = (Activity_dlgTalk) mem.obj_usr;
                tmpHandler_showMsg_task(hTalk,m_var,p1,pMsgParam);
            }
        }
        return 0;
    }

    //
    public int talker_OnQyComm(dlgTalkProc.DLG_TALK_var m_var, Object p1, qnmCommProc_mis.MIS_MSGU pMsgU) {

        CIsTalker talker = this;

        int i;
        for  (  i =  0;  i  <  talker.mems.length;i++)  {
            qmCommFunc_mis.IsTalkUsage mem = talker.mems[i];
            if  (  mem==null)continue;
            if  (  mem.obj_usr==null) continue;;
            if  ( mem.obj_usr instanceof  Activity_dlgTalk)  {
                Activity_dlgTalk hTalk = (Activity_dlgTalk) mem.obj_usr;
                //tmpHandler_showMsg_input(hTalk,m_var,p1,pMsgParam);
                //hTalk.gui_procMsg()
                dlgTalk_OnQyComm(hTalk,0,pMsgU);
            }
            if  (  mem.obj_usr instanceof MainActivity) {
                int  ii  =  0;
                MainActivity mainWnd = (MainActivity) mem.obj_usr;
                if  (  mainWnd.m_floatingView_fg!=null)  {
                    if  (  mainWnd.m_floatingView_fg instanceof Fragment_dlgTalk_av2) {
                        Fragment_dlgTalk_av2 fg_av2 = (Fragment_dlgTalk_av2)mainWnd.m_floatingView_fg;
                        fg_av2.fg_OnQyComm(pMsgU);
                    }
                }
            }
        }
        return 0;
    }




    public int tmpHandler_showMsg_taskStatus_new(dlgTalkProc.DLG_TALK_var m_var, Object p1, Object pMsgParam) {

        CIsTalker talker = this;

        int i;
        for  (  i =  0;  i  <  talker.mems.length;i++)  {
            qmCommFunc_mis.IsTalkUsage mem = talker.mems[i];
            if  (  mem==null)continue;
            if  (  mem.obj_usr==null) continue;;
            if  ( mem.obj_usr instanceof  Activity_dlgTalk)  {
                Activity_dlgTalk hTalk = (Activity_dlgTalk) mem.obj_usr;
                tmpHandler_showMsg_taskStatus(hTalk,m_var,p1,pMsgParam);
            }
        }
        return 0;
    }






}
