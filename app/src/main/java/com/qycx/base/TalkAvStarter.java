package com.qycx.base;

import static com.qycx.base.avCallProc.doTask_startAvCall;
import static com.qycx.base.confReq.qyFuncs_Droid.sendConfReq;
import static com.qycx.base.qmcProc.isGroup;
import static com.qycx.base.qmcTaskInfo.qyFuncs.freeTaskInfo;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_applyForConf;
import static com.qycx.base.qyMcMain_global.g.g_pQyMc;

import android.util.Log;

import com.qycx.Activity_dlgTalk;

/**
 * 抽出的 talk_doAv 入口，供会话页共用（地图页请用 sendConfReq / sendGroupConfReqFromMap）。
 */
public final class TalkAvStarter {

    private TalkAvStarter() {
    }

    /** 地图重新发起组会前清理该对端旧 AV task，避免占用失效通道。 */
    public static void removeTaskAvForPeer(long peerUi64Id) {
        if (peerUi64Id <= 0) return;
        qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
        idInfo.ui64Id = peerUi64Id;
        removeTaskAvOfPeer(idInfo);
    }

    private static int removeTaskAvOfPeer(qyDefs.QY_MESSENGER_ID idInfoPeer) {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (pQyMc == null) return -1;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if (pProcInfo == null || pProcInfo.cfg == null) return -1;
        if (idInfoPeer == null || idInfoPeer.ui64Id == 0) return -1;
        for (int indexTaskInfo = 0; indexTaskInfo < pProcInfo.cfg.usMaxCnt_taskInfos; indexTaskInfo++) {
            qmcTaskInfo.QMC_TASK_INFO pTaskInfo = pProcInfo.getQmcTaskInfoByIndex(null, indexTaskInfo, "removeTaskOfPeer");
            if (pTaskInfo == null || !pTaskInfo.bUsed) continue;
            qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
            if (pTaskData == null || pTaskData.msgU == null) continue;
            if (!(pTaskData.msgU instanceof qnmCommProc_mis.MIS_MSG_TASK)) continue;
            qnmCommProc_mis.MIS_MSG_TASK pMsgTask = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
            if (pMsgTask.uiType != qnmCommProc_mis.Consts.CONST_misMsgType_task) continue;
            if (pMsgTask.data == null || pMsgTask.data.buf == null) continue;
            qnmCommProc_mis.IM_CONTENTU pContent = pMsgTask.data.buf;
            if (pContent.uiType != imCommType_defs.Consts.CONST_imCommType_transferAvInfo) continue;
            qyDefs.QY_MESSENGER_ID tmpIdInfoPeer = pMsgTask.addr_logicalPeer.idInfo;
            if (tmpIdInfoPeer.ui64Id == idInfoPeer.ui64Id) {
                freeTaskInfo(null, pProcInfo, indexTaskInfo, "removeTaskOfPeer");
            }
        }
        return 0;
    }

    public static void talkDoAv(Activity_dlgTalk dlgTalk, int level, boolean startConf) {
        if (dlgTalk == null) {
            Log.d("qy", "talkDoAv: dlgTalk is null");
            return;
        }
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (pQyMc == null) return;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if (pProcInfo == null) return;
        dlgTalkProc.DLG_TALK_var pmVar = dlgTalk.get_pm_var();
        if (pmVar == null || pmVar.addr == null || pmVar.addr.idInfo == null) {
            Log.d("qy", "talkDoAv: pm_var or addr is null");
            return;
        }
        qyDefs.QY_MESSENGER_ID idInfo = pmVar.addr.idInfo;
        audioRecorder_android.qyFuncs_Droid.setMicrophoneOn(true);
        do {
            if (isGroup(pmVar)) {
                if (!startConf) {
                    removeTaskAvOfPeer(idInfo);
                    pProcInfo.av.talk_doAv_info.usAvLevel = level;
                    sendConfReq(null, idInfo, (short) CONST_imOp_applyForConf,
                            (short) level, (short) 0, null, null, null);
                    break;
                } else {
                    Log.d("qy", "talkDoAv startConf branch");
                }
            }
            // Activity_dlgTalk#doTask_startAvCall 在本工程已注释，走 avCallProc 统一入口
            doTask_startAvCall(dlgTalk, pmVar, level);
        } while (false);
    }

    /**
     * 不依赖 Activity_dlgTalk 的组会议发起（与 {@link confReq#qyFuncs_Droid#sendConfReq} 一致）。
     */
    public static boolean talkDoAvByContext(long targetId, int level) {
        if (targetId <= 0) return false;
        qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
        idInfo.ui64Id = targetId;
        audioRecorder_android.qyFuncs_Droid.setMicrophoneOn(true);
        int rc = sendConfReq(null, idInfo, (short) CONST_imOp_applyForConf,
                (short) level, (short) 0, null, null, null);
        Log.d("qy", "talkDoAvByContext target=" + targetId + " level=" + level + " rc=" + rc);
        return rc == 0;
    }
}
