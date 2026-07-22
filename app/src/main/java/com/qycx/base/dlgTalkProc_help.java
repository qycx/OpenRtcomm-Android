


package com.qycx.base;

import static com.qycx.Fragment_dlgTalk_msgList.talk_addNewMsg;
import static com.qycx.base.dlgTalkProc.qyFuncs_Droid.isTalkerShadowMgr;
import static com.qycx.base.isTalkerQProc.addToIsTalkerQ;
import static com.qycx.base.isTalkerQProc.findIsTalker;
import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_conf;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_applyForTalkerShadow_qmc;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_talk;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_task;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_taskStatus;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_recvFile;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_remoteAssist;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_sendFile;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_shareScreen;
import static com.qycx.base.qyCusResPublic.Consts.CONST_imTaskType_transferAvInfo;
import static com.qycx.base.qyMcMain_global.g.g_pQyMc;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_doTask;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWm_postComm;
import static com.qycx.base.qyWndContentType.Consts.CONST_qyWndContentType_mainWnd;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;

import com.qycx.Activity_dlgTalk;
import com.qycx.Fragment_dlgTalk_av2;
import com.qycx.MainActivity;
import com.qycx.MyApplication;
import com.qycx.base.util.Log;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

public class dlgTalkProc_help {
    public static class qyFuncs_Droid {

        //
        public static Object findTalker(qmCommFunc_mis.MIS_CNT pMisCntParam, qyDefs.QY_MESSENGER_ID pIdInfo) {
            if(pMisCntParam.talkingFriendQ == null || pMisCntParam.talkingFriendQ.isEmpty()) return null;
            Optional<qmCommFunc_mis.MIS_MSG_TALKINGFRIEND_QMC> obj = pMisCntParam.talkingFriendQ.stream().filter(x -> x.addr.idInfo.ui64Id == pIdInfo.ui64Id).findFirst();
            if (obj.orElse(null) != null)
                return Objects.requireNonNull(obj.orElse(null)).hWnd;
            else
                return null;
        }

        //
        public static guiShare.PF_findTalker findTalker = (ctxQyMc.CCtxQyMc pQyMcParam, qmCommFunc_mis.MIS_CNT pMisCntParam, qyDefs.QY_MESSENGER_ID pIdInfo) -> {

            int iErr = -1;

            qmCommFunc_mis.MIS_CNT pMisCnt = pMisCntParam;

            if (null == pMisCnt || pIdInfo.ui64Id == 0) return null;

            int index = IntStream.range(0, pMisCnt.talkingFriendQ.size()).filter(x -> pMisCnt.talkingFriendQ.get(x).addr.idInfo.ui64Id == pIdInfo.ui64Id).findFirst().orElse(-1);

            if (index < 0) return null;
            return (Activity_dlgTalk) pMisCnt.talkingFriendQ.get(index).hWnd;

        };

        //
        public static int dlgTalk_get_usLayoutType_local(CIsTalker talker) {

            int usLayoutType_local = 0;


            return usLayoutType_local;
        }


        //
        public static TableItem.TableItem_dlgTalk_msg findTaskInProcessQ(dlgTalkProc.DLG_TALK_var m_var, int iTaskId, boolean bNextAv, boolean bToShareScreen) {
            ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            //
            TableItem.TableItem_dlgTalk_msg pMsg = null;
            //TableItem_dlgTalk_msg pQNode = null;

            if (null == m_var.m_pProcessQ) return null;
            List<TableItem.TableItem_dlgTalk_msg> m_pProcessQ = m_var.m_pProcessQ;
            boolean bFound = false;


            for (var pQNode : m_pProcessQ) {
                pMsg = pQNode;
                if (pMsg.uiMsgType == CONST_misMsgType_task) {
                    if (iTaskId != 0) {
                        if (iTaskId == pMsg.iTaskId) {
                            bFound = true;
                            break;
                        }
                    } else if (bNextAv) {
                        if (pMsg.uiTaskType == CONST_imTaskType_transferAvInfo
                            //&& qyFuncs.bTaskAlive(pMsg.iStatus)
                        ) {
                            bFound = true;
                            break;
                        }
                    } else if (bToShareScreen) {       //  2011/03/12
                        if ((pMsg.uiTaskType == CONST_imTaskType_shareScreen
                                || pMsg.uiTaskType == CONST_imTaskType_remoteAssist       //  2015/11/05
                        )
                                //&& qyFuncs.bTaskAlive(pMsg.iStatus)
                                && pMsg.ui64Id_from == pMisCnt.idInfo.ui64Id) {
                            bFound = true;
                            break;
                        }
                    }
                }
            }
            //if (!pQNode) return NULL;
            if (!bFound) return null;

            //return pMsg->task;
            return pMsg;
        }


        //

        public static guiShare.PF_dlgTalk_qPostMsg dlgTalk_qPostMsg = (Object hWndParam, Object pQElem, int size) -> {
            int iErr = -1;

            ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;

            //
            qyDefs.QY_MESSENGER_ID idInfo_from = new qyDefs.QY_MESSENGER_ID();
            idInfo_from.ui64Id = 0;
            long tStartTime = 0;
            int uiTranNo = 0;
            int usCode = 0;
            int uiContentType = 0;
            String msg = "";
            int iTaskId = 0;
            int iStatus = 0;


            //
            //help_getDlgTalkVar.CHelp_getDlgTalkVar help_getDlgTalkVar = new help_getDlgTalkVar.CHelp_getDlgTalkVar();

            if (hWndParam == null) {
                showInfo_open.qyFuncs.showInfo_open("dlgTalk_qPostMsg failed, hDlgTalk is null");
                return -1;
            }

            dlgTalkProc.DLG_TALK_var m_var = null;
            if  (  hWndParam instanceof  Activity_dlgTalk) {
                ;
                Activity_dlgTalk hDlgTalk = (Activity_dlgTalk) hWndParam;
                if (hDlgTalk.m_talker == null) {
                    return -1;
                }
                m_var = (dlgTalkProc.DLG_TALK_var) hDlgTalk.m_talker.m_var;// help_getDlgTalkVar.getVar(hDlgTalk);
            }
            else if ( hWndParam instanceof Fragment_dlgTalk_av2)  {
                Fragment_dlgTalk_av2 fg = (Fragment_dlgTalk_av2) hWndParam;
                if  (  fg.m_talker  == null) return -1;
                m_var=fg.m_talker.m_var;
            }
            if (null == m_var) {
                //  2014/01/24
                Log.d("at","l73: m_var is null");
                return -1;
            }
            if (null == m_var.m_pProcessQ) {
                //  2014/01/24
                //qyShowInfo1(CONST_qyShowType_qwmComm, 0, "", pProcInfo->who_showInfo, NULL, "", "", ("dlgTalk_qPostMsg failed,  pVar->m_ProcessQ is null"));
                return -1;
            }
            if (null == pQElem || 0 == size) return -1;

            qnmCommProc_mis.MIS_MSGU pMsg = (qnmCommProc_mis.MIS_MSGU) pQElem;
            switch (pMsg.uiType) {
                case CONST_misMsgType_task:
                    qnmCommProc_mis.MIS_MSG_TASK task = (qnmCommProc_mis.MIS_MSG_TASK) pMsg;
                    idInfo_from.ui64Id = task.data.route.idInfo_from.ui64Id;
                    tStartTime = task.tStartTime;
                    uiTranNo = task.uiTranNo;
                    iTaskId = task.iTaskId;
                    iStatus = task.iStatus;

                    //
                    if (0 != task.iTaskId) {
                        switch (task.uiTaskType) {
                            case CONST_imTaskType_sendFile:
                            case CONST_imTaskType_recvFile: {

                            }
                            break;
                            default: {
                                //  2016/02/17
                                TableItem.TableItem_dlgTalk_msg tmppMsg = findTaskInProcessQ(m_var, task.iTaskId, false, false);
                                //
                                if (tmppMsg != null) {

                                    //
                                    return -1;
                                }
                                break;

                            }
                        }
                    }
                    //
                    qnmCommProc_mis.IM_CONTENTU pContent;
                    pContent = qnmCommProc_mis.qyFuncs.M_getMsgContent(task.uiType, task.ucFlg, task.data);
                    //
                    uiContentType = pContent.uiType;
                    //
                    switch (pContent.uiType) {
                        case imCommType_defs.Consts.CONST_imCommType_transferAvInfo:
                            msg = "视频通话";
                            break;
                        default:
                            break;
                    }
                    //
                    break;
                case CONST_misMsgType_talk:
                    break;
                case CONST_misMsgType_taskStatus:

                    break;
                case CONST_misMsgType_applyForTalkerShadow_qmc:
                    break;
                default:

                    break;
            }
            //return  ::qPostMsg(pQElem, size, pVar->m_pProcessQ);
            talk_addNewMsg(m_var.addr.idInfo.ui64Id, idInfo_from, tStartTime, uiTranNo, usCode, uiContentType, msg, iTaskId);


            iErr = 0;

            return iErr;
        };


        //
        public static int removeTalker(Object pQyMcParam, Object hDlgTalk, int unused) {

            ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            if (null == pMisCnt) return -1;

            //
            qyDefs.COMMON_PARAM commonParam1;
            //MACRO_makeCommonParam3((Object)pAddr, (Object)unused, 0, commonParam1);

            //qRemoveMsg(pMisCnt->talkingFriendQ, tmpHandler_findTalker, NULL,commonParam1);
//            pMisCnt.talkingFriendQ.RemoveAll(item = > item.hWnd == hDlgTalk);

            pMisCnt.talkingFriendQ.removeIf(x -> x.hWnd == hDlgTalk);
            return 0;
        }


        //
        public static boolean bDone_retrieveAllImObjRules(qmCommFunc_mis.MIS_CNT pMisCnt) {
            //
            if  (  pMisCnt  ==  null )  {
                return false;
            }
            ctxQmc.CCtxQmc pProcInfo = pMisCnt.pProcInfoParam;
            if  (  pProcInfo==null)  {
                return false;
            }
            //
            if (!pProcInfo.status.retrieveImObjList.ulbAllContactsRetrieved1) {
                return  false;
            }

            //
            return true;
        }


        //
        public static boolean bDlgTalkActive(Object hDlgTalk) {
            ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

            if (pQyMc.gui.hLastActiveTalk == hDlgTalk) return true;
            return false;
        }

        //
        //  bStay_toAvCall为true时，不生成一个activity_dlgTalk，通常为本页面悬浮窗的方式来使用
        //  为false时，生成一个activity_dlgTalk. 在dlgTalk里进行视频通话
        //
        //public static int talkToMessenger(Object pQyMcParam, qnmCommProc_mis.MSGR_ADDR pAddr, boolean bNeedNotShowWnd, boolean bActivateWnd, boolean bStay_toAvCall, isCliHelpPublic.PARAM_talkToMessenger param)
        public static int talkToMessenger(Object pQyMcParam, long talker_ui664Id, boolean bNeedNotShowWnd, boolean bActivateWnd, boolean bStay_toAvCall, isCliHelpPublic.PARAM_talkToMessenger param)
        {
            int iErr = -1;

            if (0 == talker_ui664Id) return -1;

            ctxQyMc.CCtxQyMc pQyMc = (ctxQyMc.CCtxQyMc) pQyMcParam;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (null == pProcInfo) return -1;

            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

            //
            if (!bDone_retrieveAllImObjRules(pMisCnt)) {
                Log.e("talkToMessenger failed: not all imObjRules retrieved");
                return -1;
            }

            //
            int index = -1;
            for (int i = 0; i < pMisCnt.talkingFriendQ.size(); i++) {
                if (pMisCnt.talkingFriendQ.get(i).addr.idInfo.ui64Id == talker_ui664Id) {
                    index = i;
                    break;
                }
            }

            if (index >= 0) {
                if (bNeedNotShowWnd) {
                    iErr = 0;
                    return iErr;
                }

                if (bDlgTalkActive(pMisCnt.talkingFriendQ.get(index).hWnd)) {
                    Log.e("need to show dlgTalk");
                    iErr = 0;
                    return iErr;
                }

                Activity_dlgTalk dlgTalk = (Activity_dlgTalk) pMisCnt.talkingFriendQ.get(index).hWnd;
                dlgTalk.doFinish("talkToMessenger, ");
            }

            do {

                //
                CIsTalker isTalker  =  null;
                isTalker = findIsTalker(pMisCnt,talker_ui664Id);
                if  (  isTalker==null) {
                    //
                    isTalker = new CIsTalker();
                    if (0 != isTalker.init(pMisCnt,talker_ui664Id)) {
                        Log.d("at", "talkToMessenger. isTalker.init failed");
                        break;
                    }
                }
                if  (  0!=addToIsTalkerQ(pMisCnt,isTalker)  )  {
                    Log.d("at","talkToMessenger. addToIsTalkerQ failed");
                    break;
                }

                //
                //
                if  (  bStay_toAvCall )  {

                    if  (  param!=null) {

                        //
                        if  (  param.wParam==CONST_qyWmParam_doTask) {

                            //
                            ((MyApplication) (mainActivity.getApplication())).mediaPlayer.pause();
                            //guiShare.GuiShare.pf_PostMessage_new.get().call(mainActivity,CONST_qyWm_postComm,CONST_qyWmParam_doTask,usOp);
                            guiShare.GuiShare.pf_PostMessage_new.get().call(mainActivity, CONST_qyWndContentType_mainWnd, CONST_qyWm_postComm, param.wParam, param.lParam);

                            //
                            iErr =  0;  break;
                        }
                    }

                    //
                    iErr  =  0;  break;
                }

                //
                Intent intent = new Intent(mainActivity, Activity_dlgTalk.class);
                intent.putExtra("ui64Id", talker_ui664Id);
                intent.putExtra("info", "haha");

                intent.putExtra("wParam", param.wParam);
                intent.putExtra("lParam", param.lParam);

                mainActivity.startActivity(intent);

                //
                iErr = 0;
            } while(false);

            //
            return iErr;
        }


        public static int dlgTalk_set_newMsgArrives(Object hDlgTalk, dlgTalkProc.DLG_TALK_var pm_var, boolean bOn) {
            return 0;

        }


        //
        //public static boolean dlgTalk_canSpeak(Object hDlgTalk_mgrParam)
        public static boolean dlgTalk_canSpeak(long talker_ui64Id)
        {
            boolean bRet = false;

            //
            ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
            if(pQyMc==null) return false;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();  //  (  MC_VAR_isCli  *  )m_var.pMisCnt->pProcInfoParam;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;


            //help_getDlgTalkVar.CHelp_getDlgTalkVar help_getDlgTalkVar = new help_getDlgTalkVar.CHelp_getDlgTalkVar();
            {
                //Activity_dlgTalk hDlgTalk_mgr=(Activity_dlgTalk)hDlgTalk_mgrParam;
                CIsTalker isTalker = findIsTalker(pMisCnt, talker_ui64Id);
                if  (  isTalker==null)  return false;
                //
                dlgTalkProc.DLG_TALK_var pm_var = isTalker.m_var;//(dlgTalkProc.DLG_TALK_var) help_getDlgTalkVar.getVar(hDlgTalk_mgr, ("dlgTalk_canSpeak"));
                if (null == pm_var) return false;
                dlgTalkProc.DLG_TALK_var m_var = pm_var;
                //
                if (!isTalkerShadowMgr(m_var.addr)) return false;
                //
                qmcSharedObj.QY_SHARED_OBJ pSharedObj = null;
                qmcSharedObj.SHARED_OBJ_USR pSharedObjUsr = null;

                if (!m_var.av.taskInfo.bTaskExists) return false;
                qmcTaskInfo.QMC_TASK_INFO pTaskInfo = (qmcTaskInfo.QMC_TASK_INFO) qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, pProcInfo, m_var.av.taskInfo.iIndex_taskInfo, "dlgTalk_canSpeak");
                if (null == pTaskInfo) return bRet;
                qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
                if (pTaskData == null) return bRet;
                if (pTaskData.uiType != CONST_taskDataType_conf) return bRet;
                qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;
                //qmcTaskInfo.DLG_TALK_videoConference pVc = pTc.videoConference;


                if (null == (pSharedObj = qmcSharedObj.qyFuncs.getSharedObjByIndex(pProcInfo, m_var.av.iIndex_sharedObj_localAv)))
                    return bRet;
                if (pSharedObj.var.iIndex_curUsr != m_var.av.iIndex_usr_localAv)
                    return bRet;        //  2010/09/07
                if (null == (pSharedObjUsr = qmcVideoFunc.qyFuncs.getSharedObjUsr(pSharedObj, m_var.av.iIndex_usr_localAv)))
                    return bRet;

                //  if  (  m_var.av.taskInfo.ucbVideoConference  )
                {   //  2010/09/04
                    if (!dlgTalk_videoConference_public.qyFuncs.isMemFromActive(pTc, pMisCnt.idInfo, null, pSharedObjUsr.uiTranNo_openAvDev_a,pSharedObjUsr.uiTranNo_openAvDev_v)
                        //|| pTc.videoConference.bNeedRefresh_activeMems_from
                    ) return bRet;
                }

                if (pSharedObj.var.ucbNotSendData) return bRet;

                if (pTaskInfo.var.curRoute_sendLocalAv.videoConference_idInfo_to.ui64Id == 0 && pTaskInfo.var.curRoute_sendLocalAv.route.idInfo_to.ui64Id == 0)
                    return bRet;
            }

            bRet = true;

            return bRet;

        }

        public static int askToRefreshLayout_mgr(CIsTalker talker) {
            return -1;
        }
    }
}
