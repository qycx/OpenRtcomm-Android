package com.qycx.base;

import static com.qycx.base.imCommType_defs.Consts.CONST_imCommType_confReq;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyCmd_sendReq;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_req;
import static com.qycx.base.videoFunc_open.Consts.cntof_buf5;

import com.qycx.Activity_dlgTalk;
import com.qycx.MainActivity;
import com.qycx.anc.muxStream_open;

public class confReq
{
	public static class qyFuncs_Droid
{
		//public static  int sendConfReq(Object p, Object hDlgTalkParam, qyDefs.QY_MESSENGER_ID idInfo_conf, short usOp, short usAvLevel, int conf_iFourcc, short conf_usMaxSpeakers)
		public static  int sendConfReq(Object p, qyDefs.QY_MESSENGER_ID idInfo_conf, short usOp, short usAvLevel, short conf_usMaxSpeakers, qnmCommProc_mis.TALKER_policy tp, muxStream_open.MuxStreamsCfg pMuxStreamsCfg, qnmCommProc_mis.CONF_hg_info pHgInfo)
	    // ConfReq(void* p, HWND  hDlgTalk, QY_MESSENGER_ID  idInfo_conf, unsigned  short  usOp, short  usAvLevel, short  conf_usMaxSpeakers, TALKER_policy* pTalkerPolicy, MuxStreamsCfg* pMuxStreamsCfg, CONF_hg_info* pHgInfo)
	{
			int iErr = -1;
			ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
			ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
			qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
			MainActivity mainActivity = (MainActivity)pQyMc.gui.hMainWnd;

			//help_getDlgTalkVar.CHelp_getDlgTalkVar help_getDlgTalkVar = new help_getDlgTalkVar.CHelp_getDlgTalkVar();
			{
				/*
				if (  hDlgTalkParam==null)return -1;
				Activity_dlgTalk hDlgTalk=(Activity_dlgTalk)hDlgTalkParam;
				dlgTalkProc.DLG_TALK_var pm_var = hDlgTalk.m_var;//(dlgTalkProc.DLG_TALK_var)help_getDlgTalkVar.getVar(hDlgTalk);
				if (null==pm_var) return -1;

				 */

				//
				if (0 == idInfo_conf.ui64Id) return -1;

				qnmCommProc_mis.CONF_req req=new qnmCommProc_mis.CONF_req();
				req.uiType = CONST_imCommType_confReq;
				req.idInfo_imGrp_related = idInfo_conf;
				req.usOp = usOp;
				req.policy.usAvLevel = usAvLevel;
				//
				req.policy.usMaxSpeakers = conf_usMaxSpeakers;
				//
				//req.policy.conf_iFourcc = conf_iFourcc;
				if (tp !=null) {
					req.policy.talkerPolicy = tp;
				}
				if  ( pMuxStreamsCfg!=null) {
					req.muxStreamsCfg = pMuxStreamsCfg;
				}
				if  (  pHgInfo!=null) {
					req.hgInfo = pHgInfo;
				}

				//
				int lenInBytes = 2280;

				long tStartTran = tmpFunc.qyFuncs.mytime();
				int uiTranNo = (int) (tStartTran / 1000);

				pProcInfo.postMsg2Mgr_mc(mainActivity.var_common.ctxCaller,pMisCnt, null, CONST_misMsgType_req, (byte) 0, (short) CONST_qyCmd_sendReq, tStartTran, uiTranNo, 0, req, lenInBytes, 0, 0, 0, null, false);
			}

			iErr = 0;
			return iErr;
		}


	}
}
