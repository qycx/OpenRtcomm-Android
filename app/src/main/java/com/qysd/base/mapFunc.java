package com.qysd.base;

import static com.qycx.anc.hgCommProc.Consts.CONST_hgCmd_ulAnHgData;
import static com.qycx.base.Consts.CONST_channelType_robot;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_anHgData;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_locDataReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_procLocReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_transferMarkData;
import static com.qycx.base.postMsg2MgrFunc.qyFuncs.postMsg2Mgr_mc;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_hg;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_sendVDevReq;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_outputTask;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_req;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_procLocReqSubtype_getCfgs;
import static com.qycx.base.qyMcMain_global.g.g_pQyMc;

import com.qycx.anc.hgCommProc;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qyDefs;
import com.qycx.base.qy_getuiNextTranNo;
import com.qycx.base.tmpFunc;
import com.qycx.base.util.Log;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class mapFunc {

    //
    public static int  ancSndProcLocReq(qmCommFunc_mis.MIS_CNT pMisCnt, int puiTranNo)
    {
        int				iErr = -1;
        if (null==pMisCnt)  return  -1;
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if (null==pProcInfo)  return  -1;
        /*
        FUNCS_for_isCliHelp* pFuncs = QY_GET_FUNCS_for_isCliHelp(pProcInfo);
        if (!pFuncs)  return  -1;

         */

        do {

            long tStartTran = tmpFunc.qyFuncs.mytime();
            int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();

            int len;

            //
            if  (  pQyMc.appParams.bDbg)  {
            }

            //
            qnmCommProc_mis.ProcLocReq_getCfgs req = new qnmCommProc_mis.ProcLocReq_getCfgs();

            req.uiType = CONST_imCommType_procLocReq;
            req.usSubtype = CONST_procLocReqSubtype_getCfgs;
            //
            len = 10;//sizeof(ProcLocReq_u);  //offsetof(  PROC_offline_res,  mems  )  +  req.usCnt  *  sizeof(  req.mems[0]  );
            //
            postMsg2Mgr_mc(null, pMisCnt, null, CONST_misMsgType_req, (byte)0, CONST_qyCmd_sendVDevReq, tStartTran, uiTranNo, 0, req, len, 0, 0, 0, null, false);


            //
            iErr = 0;
            //errLabel:


            /*
            if (!iErr) {
                if (puiTranNo)  *puiTranNo = uiTranNo;
            }

             */
        } while (false);



        return  iErr;
    }


    //
    public static int ancSndLocation(Object p0, String locStr, long imGrp_related_ui64Id)
    {
        return ancSndLocation(p0, locStr, imGrp_related_ui64Id, 0, 0);
    }

    public static int ancSndLocation(Object p0, String locStr, long imGrp_related_ui64Id, int laM10000, int loM10000)
    {
        //
        int  iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = (ctxQmc.CCtxQmc)pQyMc.get_pProcInfo();
        MapExtTmpl pMapExt = pProcInfo.m_pMapExt;
        if (null==pMapExt)  return  -1;
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
        if (null==pMisCnt)  return  -1;

        //
        if  (  pProcInfo.m_pMapExt.m_var.locServIdInfo==0)  {
            Log.d("at","l92: ancSndLocation failed, locServIdInfo is 0");
            return  -1;
        }

        //
        do {

            //
            byte        		ucFlg;
            qnmCommProc_mis.LocDataReq content =new qnmCommProc_mis.LocDataReq();
            int					lenInBytes;
            //
            ucFlg = 0;
            //
            //memset(&content, 0, sizeof(content));
            content.uiType = CONST_imCommType_locDataReq;
            content.idInfo_imGrp_related = imGrp_related_ui64Id;
            //safeStrnCpy(locStr, content.locData.locStr, mycountof(content.locData.locStr));
            content.locData.locStr=locStr;
            if (laM10000 != 0 || loM10000 != 0) {
                content.locData.iLaInM10000f = laM10000;
                content.locData.iLoInM10000f = loM10000;
            }
            lenInBytes = 10;//sizeof(content);


            //
            qnmCommProc_mis.MSG_ROUTE route=new qnmCommProc_mis.MSG_ROUTE();
            //memset(&route, 0, sizeof(route));
            //
            route.idInfo_from.ui64Id = pMisCnt.idInfo.ui64Id;
            //
            route.idInfo_to.ui64Id = pMapExt.m_var.locServIdInfo;

            //
            qyDefs.QY_MESSENGER_ID idInfo_peer=new qyDefs.QY_MESSENGER_ID();  idInfo_peer.ui64Id = pMapExt.m_var.locServIdInfo;
            qyDefs.QY_MESSENGER_ID idInfo_dst=new qyDefs.QY_MESSENGER_ID();  idInfo_dst.ui64Id = pMapExt.m_var.locServIdInfo;

            //
            int  channelType = 0;
            channelType = CONST_channelType_robot;

            {
                //MACRO_prepareForTran();
                long tStartTran = tmpFunc.qyFuncs.mytime();
                int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();


                if (0!=postMsg2Mgr_mc(null,pMisCnt, route, CONST_misMsgType_outputTask, ucFlg, CONST_qyCmd_sendVDevReq, tStartTran, uiTranNo, 0, content, lenInBytes, idInfo_peer.ui64Id, idInfo_dst.ui64Id, channelType, null, true)) {
                    break;
                }
            }

            iErr = 0;
        } while (false);


        //
        return  iErr;

    }

    /**
     * 将标注字符串经融合中心上报（对应 C++ sndTransferLocDataToHg）。
     * markStr 写入 TransferLocDataMem.locStr，content 类型为 {@link CONST_imCommType_transferLocData}。
     */
    public static int ancSndMarkToHg(Object p0, String markStr, long imGrp_related_ui64Id) {
        if (markStr == null || markStr.isEmpty() || imGrp_related_ui64Id <= 0) {
            return -1;
        }
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (pQyMc == null) {
            return -1;
        }
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if (pProcInfo == null) {
            return -1;
        }
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
        if (pMisCnt == null || pMisCnt.idInfo.ui64Id == 0) {
            return -1;
        }
        long msgrId = pMisCnt.idInfo.ui64Id;
        String hgCliData = hgCommProc.buildTransferLocDataHgCliData(
                msgrId,
                CONST_imCommType_transferMarkData,
                imGrp_related_ui64Id,
                new long[]{msgrId},
                new String[]{markStr});
        if (hgCliData == null || hgCliData.isEmpty()) {
            Log.d("at", "ancSndMarkToHg failed: hgCliData empty");
            return -1;
        }
        byte[] payloadBytes = hgCliData.getBytes();
        if (payloadBytes.length >= com.qycx.anc.hgCommProc.Consts.MAX_hg_cliDataLen) {
            Log.d("at", "ancSndMarkToHg failed: hgCliData too long, len=" + payloadBytes.length);
            return -1;
        }
        qnmCommProc_mis.AnHg_req req = new qnmCommProc_mis.AnHg_req();
        req.uiType = CONST_imCommType_anHgData;
        req.sHgCmd = CONST_hgCmd_ulAnHgData;
        System.arraycopy(payloadBytes, 0, req.hg_cliData, 0, payloadBytes.length);
        req.hg_cliDataLen = payloadBytes.length;

        long tStartTran = tmpFunc.qyFuncs.mytime();
        int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
        int len = 0;
        //postMsg2Mgr_mc(null,pMisCnt, route, CONST_misMsgType_outputTask, ucFlg, CONST_qyCmd_sendVDevReq, tStartTran, uiTranNo, 0, content, lenInBytes, idInfo_peer.ui64Id, idInfo_dst.ui64Id, channelType, null, true)
        if (0 != postMsg2Mgr_mc(null, pMisCnt, null, CONST_misMsgType_req, (byte) 0,
                CONST_qyCmd_hg, tStartTran, uiTranNo, 0, req, len, 0, imGrp_related_ui64Id,
                CONST_channelType_robot, null, false)) {
            return -1;
        }
        return 0;
    }

    /** 对应 PC {@code m_pQmcExt->doAnHgData}：解析融合中心下发的 hg_cliData 并分发给地图扩展。 */
    public static int doAnHgData(ctxQmc.CCtxQmc pProcInfo, qnmCommProc_mis.AnHg_req req) {
        if (pProcInfo == null || req == null || req.hg_cliDataLen <= 0) {
            return -1;
        }
        MapExtTmpl pMapExt = pProcInfo.m_pMapExt;
        if (pMapExt == null) {
            return -1;
        }
        hgCommProc.ParsedHgCliData parsed = hgCommProc.parseHgCliData(req.hg_cliData, req.hg_cliDataLen);
        if (parsed == null || parsed.grpId <= 0) {
            Log.d("at", "doAnHgData failed: parseHgCliData");
            return -1;
        }
        MapExtTmpl.Param_onRecv_marks param = new MapExtTmpl.Param_onRecv_marks();
        param.imGrp_ui64Id = parsed.grpId;
        param.outerMsgrId = parsed.outerMsgrId;
        param.m_marks = new ArrayList<>();
        for (hgCommProc.ParsedHgCliData.Mem mem : parsed.mems) {
            if (mem == null || mem.locStr == null || mem.locStr.isEmpty()) {
                continue;
            }
            MapExtTmpl.AncMark mark = new MapExtTmpl.AncMark();
            mark.ui64Id = mem.userId > 0 ? mem.userId : parsed.msgrId;
            mark.markStr = mem.locStr;
            param.m_marks.add(mark);
        }
        if (param.m_marks.isEmpty()) {
            return -1;
        }
        Log.d("at1111", "doAnHgData grp=" + parsed.grpId + " outerMsgr=" + parsed.outerMsgrId
                + " sender=" + parsed.msgrId + " marks=" + param.m_marks.size());
        return pMapExt.onRecv_marks(param);
    }



    /*
    //
    __declspec(dllexport) int ancSndTransferLocData(void* p0, void* pTransferLocData, __int64 imGrp_related_ui64Id, __int64  ui64Id_dst)
    {
        //
        int  iErr = -1;
        CCtxQyMc* pQyMc = g_pQyMc;
        CCtxQmc* pProcInfo = (CCtxQmc*)pQyMc->get_pProcInfo();
        MapExtTmpl* pMapExt = pProcInfo->m_pMapExt;
        if (!pMapExt)  return  -1;
        MIS_CNT* pMisCnt = pProcInfo->getMisCntByIndex(0);
        if (!pMisCnt)  return  -1;

        //
        if (!pTransferLocData)  return  -1;
        TransferLocData& content = *(TransferLocData*)pTransferLocData;


        //
        if (content.usCnt == 0)  return  -1;

        //
        do {

            //
            unsigned  char		ucFlg;
            int					lenInBytes;
            //
            ucFlg = 0;
            //
            content.uiType = CONST_imCommType_transferLocData;
            content.idInfo_imGrp_related.ui64Id = imGrp_related_ui64Id;
            //
            lenInBytes = sizeof(content);


            //
            MSG_ROUTE	route;
            memset(&route, 0, sizeof(route));
            //
            route.idInfo_from.ui64Id = pMisCnt->idInfo.ui64Id;
            //
            route.idInfo_to.ui64Id = ui64Id_dst;

            //
            QY_MESSENGER_ID  idInfo_peer;  idInfo_peer.ui64Id = 0;// pMapExt->m_var.locServIdInfo.ui64Id;
            QY_MESSENGER_ID  idInfo_dst;  idInfo_dst.ui64Id = ui64Id_dst;

            //
            int  channelType = 0;
            channelType = CONST_channelType_robot;

            {
                MACRO_prepareForTran();

                if (postMsg2Mgr_mc(pMisCnt, &route, CONST_misMsgType_outputTask, ucFlg, CONST_qyCmd_sendVDevReq, tStartTran, uiTranNo, 0, (char*)&content, lenInBytes, &idInfo_peer, &idInfo_dst, channelType, mynull, TRUE)) {
                break;
            }
            }

            iErr = 0;
        } while (false);


        //
        return  iErr;

    }

     */


    //
    /**
     * 将标注字符串经融合中心上报（对应 C++ sndTransferLocDataToHg）。
     * markStr 写入 TransferLocDataMem.locStr，content 类型为 {@link CONST_imCommType_transferLocData}。
     */
    public static int test_ancSndMarkToHg(Object p0, String markStr, long imGrp_related_ui64Id) {
        if (markStr == null || markStr.isEmpty() || imGrp_related_ui64Id <= 0) {
            return -1;
        }
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (pQyMc == null) {
            return -1;
        }
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        if (pProcInfo == null) {
            return -1;
        }
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
        if (pMisCnt == null || pMisCnt.idInfo.ui64Id == 0) {
            return -1;
        }
        long msgrId = pMisCnt.idInfo.ui64Id;
        /*
        String hgCliData = hgCommProc.buildTransferLocDataHgCliData(
                msgrId,
                CONST_imCommType_transferLocData,
                imGrp_related_ui64Id,
                new long[]{msgrId},
                new String[]{markStr});
        if (hgCliData == null || hgCliData.isEmpty()) {
            Log.d("at", "ancSndMarkToHg failed: hgCliData empty");
            return -1;
        }
        byte[] payloadBytes = hgCliData.getBytes();
        if (payloadBytes.length >= com.qycx.anc.hgCommProc.Consts.MAX_hg_cliDataLen) {
            Log.d("at", "ancSndMarkToHg failed: hgCliData too long, len=" + payloadBytes.length);
            return -1;
        }

         */
        String hgCliData = "[obj=80 len=12]123456789012";
        byte []payloadBytes=hgCliData.getBytes(StandardCharsets.UTF_8);

        qnmCommProc_mis.AnHg_req req = new qnmCommProc_mis.AnHg_req();
        req.uiType = CONST_imCommType_anHgData;
        req.sHgCmd = CONST_hgCmd_ulAnHgData;
        System.arraycopy(payloadBytes, 0, req.hg_cliData, 0, payloadBytes.length);
        req.hg_cliDataLen = payloadBytes.length;

        long tStartTran = tmpFunc.qyFuncs.mytime();
        int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
        int len = 0;
        //postMsg2Mgr_mc(null,pMisCnt, route, CONST_misMsgType_outputTask, ucFlg, CONST_qyCmd_sendVDevReq, tStartTran, uiTranNo, 0, content, lenInBytes, idInfo_peer.ui64Id, idInfo_dst.ui64Id, channelType, null, true)
        if (0 != postMsg2Mgr_mc(null, pMisCnt, null, CONST_misMsgType_req, (byte) 0,
                CONST_qyCmd_hg, tStartTran, uiTranNo, 0, req, len, 0, 0,
                CONST_channelType_robot, null, false)) {
            return -1;
        }
        return 0;
    }




}
