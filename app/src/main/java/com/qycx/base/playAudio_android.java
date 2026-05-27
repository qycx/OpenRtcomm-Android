package com.qycx.base;

import static com.qycx.base.Consts.AUDIO_DOWNLOAD;
import static com.qycx.base.qmcFunc_android.qyFuncs_Droid.startQThread_android;
import static com.qycx.base.qmcFunc_android.qyFuncs_Droid.stopQThread_android;
import static com.qycx.base.qmcPlayAudio.qyFuncs_Droid.mcThreadProc_playAudio;
import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_transferAudioData;
import static com.qycx.base.qyDefs_open.qyFuncs.MACRO_safeFree;

import android.util.Log;

public class playAudio_android
{
	private static final String TAG = playAudio_android.class.getSimpleName();

	public static class qyFuncs_Droid
	{
		public static guiShare.PF_startPlayAudio_android startPlayAudio_android=(qmcCommFunc_is_open.QY_PLAYER pPlayerParam)->
		{
			int iErr = -1;
			qmcCommFunc_is_open.QY_PLAYER pPlayer = (qmcCommFunc_is_open.QY_PLAYER)pPlayerParam;
			qmcCommFunc_is_open.PLAY_AUDIO_procInfo pPlayAudio = pPlayer.audio;
			ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
			ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
			String str;

			//
			if  (  pPlayer.audio.index_tmpQMem  ==  0  )  {
				Log.d("qy","kk");
			}

			//
			if ( pProcInfo.cfg.mSpecialDbg.bNo_audioPlayer)
            {
				str = String.format("for test, startPlayAudio_mediaCodec returns -1");
				Log.e(TAG,str);
				return -1;
            }

			//
			String pName = pPlayer.audio.q2.cfg.name;
			{
				str = String.format("startPlayAudio_android called, %s", new String(pName));
				Log.d(TAG, "startPlayAudio_android: "+str);
			}

			//pPlayAudio.cache.uiBufSize = Math.max((bufSize_transferAudioData + 256) * 2, pPlayer.audio.ah.wfx.nAvgBytesPerSec * 2);
			pPlayAudio.cache.uiBufSize = Math.max(16000  *  2, pPlayer.audio.ah.wfx.nAvgBytesPerSec * 2);
			pPlayAudio.cache.pBuf = new byte[pPlayAudio.cache.uiBufSize];
			if (0==pPlayAudio.cache.uiBufSize || null==pPlayAudio.cache.pBuf) return iErr;

//			pPlayAudio.cache.pLeft = pPlayAudio.cache.pBuf;
			pPlayAudio.cache.indexStart = 0;
			pPlayAudio.cache.nLeft = 0;
			pPlayAudio.cache.dwTickCnt_lastAudioData = tmpFunc.qyFuncs.myGetTickCount(null);
			Log.d(AUDIO_DOWNLOAD, "startPlayAudio_android  player true");

			//
			boolean  bStartThread  =  true;

			//
			if  (  pProcInfo.cfg.ucb_USE_audioMixer) {   //  0用作混音器的播放线程。其他的都不需要启动了
				if (0 != pPlayer.audio.index_tmpQMem) {
					bStartThread = false;
				}
			}

			//
			if  (  bStartThread) {
				//
				pPlayAudio.bRunning = true;
				//
				startQThread_android(mcThreadProc_playAudio, pPlayer, null, pPlayAudio);
			}

			iErr = 0;

			return iErr;
		};

		public static guiShare.PF_stopPlayAudio_android stopPlayAudio_android=(qmcCommFunc_is_open.QY_PLAYER pPlayerParam)->
		{
			int iErr = -1;
			qmcCommFunc_is_open.QY_PLAYER pPlayer = (qmcCommFunc_is_open.QY_PLAYER)pPlayerParam;
			qmcCommFunc_is_open.PLAY_AUDIO_procInfo pPlayAudio = pPlayer.audio;
			String str;

			//
			//
			String pName = pPlayer.audio.q2.cfg.name;
			{
				str = String.format("stopPlayAudio_android called, %s", new String(pName));
			}
			showInfo_open.qyFuncs.showInfo_open(0,null,str);

			//
			// qmcFunc.qyFuncs.stopQThread(pPlayAudio, "");
			stopQThread_android(pPlayAudio, "");

			//
			//
//			byte[] pBuf = pPlayAudio.cache.pBuf;
//			MACRO_safeFree( pBuf);
			pPlayAudio.cache.pBuf = null;

			//
			iErr = 0;
			return iErr;
		};
	}

}


