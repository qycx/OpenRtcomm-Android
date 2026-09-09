
package com.qycx.base;


import static com.qycx.anc.hgCommProc.Consts.CONST_hgCmd_xt;
import static com.qycx.anc.hgCommProc.Consts.CONST_hgType_curConf;
import static com.qycx.base.Consts.CONST_channelType_talking;
import static com.qycx.base.ctxQmc.Consts.CONST_intervalInS_xt;
import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_qisCaReq;
import static com.qycx.base.postMsg2MgrFunc.qyFuncs.postMsg2Mgr_mc;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_req;
import static com.qycx.base.qyComm.Consts.CONST_qyCmd_ca;
import static com.qycx.base.qyMcMain_global.g.g_pQyMc;

//
public  class qmcCaProc_public {

//
public static class qyFuncs {


	//
public static int send_xt(int mainWnd_loopCtrl)
{


	ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
	ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
	qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
	boolean  bSkip = false;

	//
	//Ctx_sm pCtxSm = pProcInfo->getCtxSm();
	//if (!pCtxSm)  return  -1;



	//
	boolean bNeedXtNow = false;

	//
	int intervalInS = CONST_intervalInS_xt;
	//







	//
	//MACRO_prepareForTran();
	long tStartTime = tmpFunc.qyFuncs.mytime();
	int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();

	//
	qnmCommProc_mis.QIS_ca_req req = new qnmCommProc_mis.QIS_ca_req();



	//
	//memset(&req, 0, sizeof(req));
	req.uiType = CONST_imCommType_qisCaReq;

	//  将电话告知的设备授权码+证书的唯一标识，送到会管服务器
	String str;
	str  =  String.format("[cmd=%d]", CONST_hgCmd_xt);
	//_snprintf(req.ca_cliData, mycountof(req.ca_cliData), "[cmd=%d]", CONST_hgCmd_xt);
	/*
	char  statusBuf[128];
	_snprintf(statusBuf, mycountof(statusBuf), "%d", pProcInfo->xt.bUsrLogined);
	_snprintf(req.ca_cliData, mycountof(req.ca_cliData), "%s [obj=%d len=%d]%s", req.ca_cliData, CONST_hgType_bUsrLogin, strlen(statusBuf), statusBuf);

	char  ukeyStatusBuf[128];
	_snprintf(ukeyStatusBuf, mycountof(ukeyStatusBuf), "%d", pProcInfo->xt.bUsrUkeyed);
	_snprintf(req.ca_cliData, mycountof(req.ca_cliData), "%s [obj=%d len=%d]%s", req.ca_cliData, CONST_hgType_bUsrUkey, strlen(ukeyStatusBuf), ukeyStatusBuf);
	char  videoStatusBuf[128];

#ifdef  __DEBUG__
	pProcInfo->xt.bVideoDevOk = true;
#endif


	_snprintf(videoStatusBuf, mycountof(videoStatusBuf), "%d", pProcInfo->xt.bVideoDevOk);
	_snprintf(req.ca_cliData, mycountof(req.ca_cliData), "%s [obj=%d len=%d]%s", req.ca_cliData, CONST_hgType_bUsrVideo, strlen(videoStatusBuf), videoStatusBuf);
	char  audioStatusBuf[128];
	_snprintf(audioStatusBuf, mycountof(audioStatusBuf), "%d", pProcInfo->xt.bAudioDevOk);
	_snprintf(req.ca_cliData, mycountof(req.ca_cliData), "%s [obj=%d len=%d]%s", req.ca_cliData, CONST_hgType_bUsrAudio, strlen(audioStatusBuf), audioStatusBuf);

	char  speakStatusBuf[128];
	_snprintf(speakStatusBuf, mycountof(speakStatusBuf), "%d", pProcInfo->xt.bSpeak);
	_snprintf(req.ca_cliData, mycountof(req.ca_cliData), "%s [obj=%d len=%d]%s", req.ca_cliData, CONST_hgType_bSpeak, strlen(speakStatusBuf), speakStatusBuf);
	*/

	//char  curConfBuf[128];
	//_snprintf(curConfBuf, mycountof(curConfBuf), "%I64u", pProcInfo->av.localAv.idInfo_curConference.ui64Id);
	//_snprintf(req.ca_cliData, mycountof(req.ca_cliData), "%s [obj=%d len=%d]%s", req.ca_cliData, CONST_hgType_curConf, strlen(curConfBuf), curConfBuf);
	String curConfBuf;
	curConfBuf=String.valueOf(pProcInfo.av.localAv.idInfo_curConference.ui64Id);
	str  +=  " [obj="  +  CONST_hgType_curConf + " len="  +  curConfBuf.length()  +  "]"  + curConfBuf;


	/*
	char buf_terminalType[10];
	safeStrnCpy(_ltoa(pProcInfo->uiTerminalType, buf_terminalType, 10), buf_terminalType, mycountof(buf_terminalType));

	_snprintf(req.ca_cliData, mycountof(req.ca_cliData), "%s [obj=%d len=%d]%s", req.ca_cliData, CONST_hgType_terminal_type, strlen(buf_terminalType), buf_terminalType);
	*/
	/*
	public static native void arraycopy(Object src,  int srcPos,
	Object dest, int destPos,
	int length);

	 */

	byte[] kk  =  str.getBytes();
	System.arraycopy(kk,0, req.ca_cliData,0,str.length());

	//
	req.ca_cliDataLen = str.length();//strlen(req.ca_cliData);

	//
	int len = 0;//sizeof(req);
	//postMsg2Mgr_mc(null,pMisCnt, NULL, CONST_misMsgType_req, 0, CONST_qyCmd_ca, tStartTran, uiTranNo, 0, (char*)&req, len, NULL, 0, 0, NULL, FALSE);

	postMsg2Mgr_mc(null, pMisCnt, null, CONST_misMsgType_req, (byte) 0, CONST_qyCmd_ca, tStartTime, uiTranNo, 0, req, len, 0, 0, CONST_channelType_talking, null, false);




		//
	return  0;

}


}

}

