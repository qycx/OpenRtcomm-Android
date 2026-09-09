package com.qycx.anc;


import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_anKeepaliveReq;
import static com.qycx.base.postMsg2MgrFunc.qyFuncs.postMsg2Mgr_mc;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_outputReq;
import static com.qycx.base.qyComm.Consts.CONST_qyCmd_sendReq;
import static com.qycx.base.showInfo_open.qyFuncs.showInfo_open;

import com.qycx.base.ctxCaller;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.postMsg2MgrFunc;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.qnmCommProc;
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qy_getuiNextTranNo;
import com.qycx.base.tmpFunc;

public class ancKeepalive {


    public static int  sendKeepaliveReq(ctxQmc.CCtxQmc pProcInfo, boolean  bResp, int channelType)
    {
        int  len;
        qmCommFunc_mis.MIS_CNT pMisCnt;
        ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;

        pMisCnt = pProcInfo.pMisCnt;


        //MACRO_prepareForTran();
        long tStartTran = tmpFunc.qyFuncs.mytime();
        int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();

        qnmCommProc_mis.AnKeepaliveReq req;

        //
        if  (  pQyMc.appParams.bDbg  ) {
            uiTranNo = 9876;
        }



        //
        req = new qnmCommProc_mis.AnKeepaliveReq();
        req.uiType = CONST_imCommType_anKeepaliveReq;
        req.ucbResp = (byte)(bResp  ?  1  :  0);
        //
        len = 10;//sizeof(req);
        postMsg2Mgr_mc(null,pMisCnt, null, CONST_misMsgType_outputReq, (byte)0, CONST_qyCmd_sendReq, tStartTran, uiTranNo, 0, req, len, 0, 0, channelType, null, false);

        //
        if  (  pProcInfo.cfg.debugStatusInfo.bDbgDetail_keepAlive) {
            String str;
            str = String.format("sendKeepaliveReq called. channelType %d. tn %d", channelType, uiTranNo);
            showInfo_open(0, 0, "", str);
        }


        //
        return  0;

    }
}
