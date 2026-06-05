package com.qysd.base;

import static com.qycx.base.Consts.CONST_channelType_robot;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_locDataReq;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_procLocReq;
import static com.qycx.base.postMsg2MgrFunc.qyFuncs.postMsg2Mgr_mc;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_sendVDevReq;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_outputTask;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_req;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_procLocReqSubtype_getCfgs;
import static com.qycx.base.qyMcMain_global.g.g_pQyMc;

import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qyDefs;
import com.qycx.base.qy_getuiNextTranNo;
import com.qycx.base.tmpFunc;
import com.qycx.base.util.Log;

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



}
