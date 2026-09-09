package ext;

import static com.qycx.base.isTalkerQProc.findIsTalker;
import static com.qycx.base.setQmDbFuncs.qyFuncs.setQmDbFuncs_qm;

import com.qycx.base.CIsTalker;
import com.qycx.base.ctxCaller;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.genericQueue;
import com.qycx.base.myDb;
import com.qycx.base.postMsg2MgrFunc;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.qmCommonDbFunc_mis;
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qyDefs;
import com.qycx.base.qyMcMainObj;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.qyMsgFunc;
import com.qycx.base.qyTypes;
import com.qycx.base.util.Log;

import java.nio.IntBuffer;

public class ctxQmc_gui {
    public static class CCtxQmc_gui extends ctxQmc.CCtxQmc {


        public static ctxQyMc.PF_newVar newVar_isCli_gui=(ctxQyMc.CCtxQyMc pQyMc, qyMcMainObj.QY_SERVICEGUI_INFO pSci, IntBuffer p2)-> {

            ctxQmc.CCtxQmc p = new CCtxQmc_gui();
            pSci.pVar = p;
            return 0;
        };

        public static ctxQyMc.PF_freeVar freeVar_isCli_gui=(ctxQyMc.CCtxQyMc pQyMc, qyMcMainObj.QY_SERVICEGUI_INFO pSci, IntBuffer p2)-> {
            if (pSci.pVar != null) {
                ctxQmc.CCtxQmc p = (ctxQmc.CCtxQmc) pSci.pVar;
                //delete p;
                pSci.pVar = null;
            }
            return 0;
        };


        @Override
        public int setQmDbFuncs(int iDbType, myDb.QM_dbFuncs pDbFuncs) {
            return setQmDbFuncs_qm(iDbType, pDbFuncs);
        }


        @Override
        public int postMsg2Mgr_mc(ctxCaller.CTX_caller ctxCaller, qmCommFunc_mis.MIS_CNT pMIS_CNT, qnmCommProc_mis.MSG_ROUTE pRoute, int uiMisMsgType, byte ucFlg, short usCode, long tStartTime, int uiTranNo, int uiSeqNo, qnmCommProc_mis.IM_CONTENTU data, int dataLen, long pIdInfo_logicalPeer, long pIdInfo_dst, int uiChannelType, qnmCommProc_mis.CMIS_MSGU pMsgBuf, boolean bLog) {
            return postMsg2MgrFunc.qyFuncs.postMsg2Mgr_mc(ctxCaller, pMIS_CNT, pRoute, uiMisMsgType, ucFlg, usCode, tStartTime, uiTranNo, uiSeqNo, data, dataLen, pIdInfo_logicalPeer, pIdInfo_dst, uiChannelType, pMsgBuf, bLog);
        }

        @Override
        public int postReciveMsg2Mgr_mc(ctxCaller.CTX_caller ctxCaller, qmCommFunc_mis.MIS_CNT pMIS_CNT, qnmCommProc_mis.MSG_ROUTE pRoute, int uiMisMsgType, qyMsgFunc.AT_MSG_HEAD_obj msgHead, int uiSeqNo, qnmCommProc_mis.MIS_MSGU data, long pIdInfo_logicalPeer, long pIdInfo_dst, int uiChannelType, qnmCommProc_mis.CMIS_MSGU pMsgBuf, boolean bLog)
        {
            return postMsg2MgrFunc.qyFuncs.postReciveMsg2Mgr(ctxCaller, pMIS_CNT, pRoute, uiMisMsgType, msgHead, uiSeqNo, data, pIdInfo_logicalPeer, pIdInfo_dst, uiChannelType, pMsgBuf, bLog);
        }

        @Override
        public int postImMsg2Log_isClient(qnmCommProc_mis.MIS_MSGU pMsg, int lenInBytes_msg) {
            return postMsg2MgrFunc.qyFuncs.postImMsg2Log_isClient(pMsg, lenInBytes_msg);
        }

        @Override
        public int recoverMessenger(myDb.QM_dbFuncs pDbFuncs, Object pDb, int iDbType, qyTypes.QY_DMITEM[] pFieldIdTable, qnmCommProc_mis.QY_MESSENGER_INFO pObj, qnmCommProc_mis.QY_MESSENGER_REGINFO pRegInfo, long tLastModifiedTime, boolean bLog, genericQueue.GENERIC_Q pLogQ) {
            return qmCommonDbFunc_mis.qyFuncs.recoverMessenger(pDbFuncs, pDb, iDbType, pFieldIdTable, pObj, pRegInfo, tLastModifiedTime, bLog, pLogQ);
        }

        @Override
        public int recoverImGrp(myDb.QM_dbFuncs pDbFuncs, Object pDb, int iDbType, qyTypes.QY_DMITEM[] pFieldIdTable, int uiObjType, qnmCommProc_mis.IM_GRP_INFO pGrpInfo, long tLastModifiedTime, boolean bNoGrpName) {
            return qmCommonDbFunc_mis.qyFuncs.recoverImGrp(pDbFuncs, pDb, iDbType, pFieldIdTable, uiObjType, pGrpInfo, tLastModifiedTime, bNoGrpName);
        }

        @Override
        public int recoverImGrpMem(myDb.QM_dbFuncs pDbFuncs, Object pDb, int iDbType, qnmCommProc_mis.IM_GRP_MEM pGrpMem, long tLastModifiedTime) {
            return qmCommonDbFunc_mis.qyFuncs.recoverImGrpMem(pDbFuncs, pDb, iDbType, pGrpMem, tLastModifiedTime);
        }


        @Override
        public CIsTalker tryToTalkToMessenger_any(long ui64Id, boolean bNeedNotShowWnd, boolean bActivateWnd) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            //if (GuiShare.pf_gui_findTalker(ui64Id, 0,  phWnd) != 0) return -1;
            qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
            idInfo.ui64Id = ui64Id;

            return findIsTalker(pMisCnt,ui64Id);

//            if (null == (phWnd = guiShare.GuiShare.pf_findTalker.call(pQyMc, pMisCnt, idInfo))) return -1;
            //return guiShare.GuiShare.pf_findTalker.call(pQyMc, pMisCnt, idInfo);
//            return 0;
        }

        @Override public int  initVar_post(Object p0, Object p1, Object p2)
        {
            ctxQmc.CCtxQmc pBase = this;
            //
            if  (  pQyMc.appParams.bDbg) {
                //
                pBase.cfg.debugStatusInfo.bTest_noMapExt = true;
                Log.d("at","test: noMapExt set to true");
                //
                pBase.cfg.debugStatusInfo.bTest_noLostVideo=true;
                Log.d("at","test: noLostVideo set to true");
                //
                pBase.cfg.debugStatusInfo.bBbg_traceRt_pOutputQBuf2_local_a=true;
                Log.d("at","test: traceRt_pOutputQBuf2_local_a set to true");
                //
                //pBase.cfg.debugStatusInfo.bDbgDetail_keepAlive=false;

                //
                pBase.cfg.debugStatusInfo.bDbgDetail_v_r=true;
                Log.d("at","test: bDbgDetail_v_r set to true");

            }

            //
            return  0;
        }
        @Override public int  exitVar_pre(Object p0, Object p1, Object p2)
        {
            return  0;
        }
        @Override public int  exitVar_post(Object p0, Object p1, Object p2)
        {
            return  0;
        }


    }

}
