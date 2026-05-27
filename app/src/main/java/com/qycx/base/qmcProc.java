package com.qycx.base;

import static com.qycx.base.dlgTalk_videoConference.qyFuncs_Droid.dlgTalk_requestToSpeak;
import static com.qycx.base.isTalkerQProc.findIsTalker;
import static com.qycx.base.qyWndContentType.Consts.CONST_fgType_talk_av2;
import static com.qycx.base.qyWndContentType.Consts.CONST_qyWndContentType_talker;

import android.opengl.EGLContext;

import com.qycx.Activity_dlgTalk;
import com.qycx.Activity_dlgTalk_av;
import com.qycx.Fragment_dlgTalk_av2;
import com.qycx.MainActivity;
import com.qycx.base.util.Log;

import java.lang.ref.WeakReference;

public class qmcProc {

    //
    public static boolean isGroup(dlgTalkProc.DLG_TALK_var m_var){
        //Activity_dlgTalk_av owner = this;//this.mOwner_wnd;
        //Activity_dlgTalk talk = this;//owner.mOwner_hTalk.get();
        //dlgTalkProc.DLG_TALK_var m_var = talk.m_var;
        return m_var.addr.uiObjType == qyDefs.Consts.CONST_objType_imGrp;
    }

    //
    //
    public static int old_save_dlgTalk_share_eglContext(dlgTalkProc.DLG_TALK_var m_var,Object act_av, android.opengl.EGLContext share_eglContent, String hint) {

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) {
            Log.e("pQyMc == null");
            return -1;
        }
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        //
        if  (  !pProcInfo.cfg.bUse_off2on)  {
            return save_dlgTalk_share_eglContext_func(m_var,act_av, share_eglContent,hint);
        }

        //
        return  0;
    }

    //
    public static int useFbo_save_dlgTalk_share_eglContext(dlgTalkProc.DLG_TALK_var m_var, Object act_av,android.opengl.EGLContext share_eglContent, String hint) {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) {
            Log.e("pQyMc == null");
            return -1;
        }
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        if  (  pProcInfo.cfg.bUse_off2on)  {
            return save_dlgTalk_share_eglContext_func(m_var,act_av, share_eglContent,hint);
        }

        return  0;
    }


    //
    public static int save_dlgTalk_share_eglContext_func(dlgTalkProc.DLG_TALK_var m_var, Object act_av, android.opengl.EGLContext share_eglContent, String hint) {
        int iErr = -1;
        String str;

        if (hint == null) hint = "";

        //
        //Activity_dlgTalk dlgTalk = null;
        //Activity_dlgTalk_av act = (Activity_dlgTalk_av) this;// mOwner.getActivity();
        //if (act != null)
        {
            //dlgTalk = act.mOwner_hTalk.get();
            //dlgTalkProc.DLG_TALK_var m_var = dlgTalk.m_var;
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (pQyMc == null) {
                Log.e("pQyMc == null");
                return -1;
            }
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

            //

            try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                if (0 != syncObj.sync("", pMisCnt.hMutex_syncShareEglContext,"save_dlgTalk_share_eglContext.1012")) return iErr;
                int i;
                for (i = 0; i < m_var.tmp_hTools_dlgTalk_av.length; i++) {
                    if (m_var.tmp_hTools_dlgTalk_av[i] == act_av) {
                        //
                        m_var.tmp_share_eglContexts[i] = share_eglContent;
                        //
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }
        }
        //
        iErr = 0;


        str = String.format("save_dlgTalk_share_eglContext called, %s %s %s", (share_eglContent != null) ? "egl" : "null", (iErr == 0) ? "ok" : "failed", hint);
        Log.d(str);

        return iErr;
    }






    //
    public static int doChkTalkDraw(dlgTalkProc.DLG_TALK_var m_var) {
        int iErr = -1;
        boolean bDbg = false;
        String str;

        int i;
        int cnt = 0;
        for (i = 0; i < m_var.tmp_hTools_dlgTalk_av.length; i++) {
            if (m_var.tmp_hTools_dlgTalk_av[i] != null) {
                cnt++;
            }
        }
        //
        if (cnt > 1) {
            int ii = 0;
        }
        //
        if (cnt == 0) {
            m_var.m_hTool_dlgTalk_av = null;
            m_var.m_share_eglContext = null;
            iErr = 0;
            return iErr;
        }
        //
        if (cnt == 1) {
            for (i = 0; i < m_var.tmp_hTools_dlgTalk_av.length; i++) {
                if (m_var.tmp_hTools_dlgTalk_av[i] != null && m_var.tmp_share_eglContexts[i] != null) {
                    //
                    Object tmp_hTool_dlgTalk_av = m_var.tmp_hTools_dlgTalk_av[i];
                    EGLContext tmp_share_eglContext = m_var.tmp_share_eglContexts[i];
                    //
                    if (tmp_hTool_dlgTalk_av != m_var.m_hTool_dlgTalk_av || tmp_share_eglContext != m_var.m_share_eglContext) {
                        m_var.m_hTool_dlgTalk_av = tmp_hTool_dlgTalk_av;
                        m_var.m_share_eglContext = tmp_share_eglContext;
                        //
                        Fragment_dlgTalk_av2 fg = null;
                        if (m_var.m_hTool_dlgTalk_av instanceof Activity_dlgTalk_av) {
                            Activity_dlgTalk_av hTalk_av = (Activity_dlgTalk_av) m_var.m_hTool_dlgTalk_av;
                            fg = hTalk_av.m_fg_dlgTalk_av2;
                        }
                        else if ( m_var.m_hTool_dlgTalk_av instanceof MainActivity) {
                            MainActivity mainWnd = (MainActivity) m_var.m_hTool_dlgTalk_av;
                            if ( mainWnd.m_floatingView_fg instanceof Fragment_dlgTalk_av2) {
                                fg = (Fragment_dlgTalk_av2) mainWnd.m_floatingView_fg;
                            }
                        }
                        //
                        if (fg != null) {
                            fg.waitUntilTextureViewAvailable(null, null, null);
                        }

                    }
                    //
                    break;
                }
            }
            iErr = 0;
            return iErr;
        }

        iErr = 0;
        return iErr;
    }


    //
    //
    public static int tmpHandler_pauseTaskAv(ctxCaller.CTX_caller ctxCaller, qnmCommProc_mis.MIS_MSGU p0, qyMcMainProc.PARAM_findOrgMsg p1, Object p2) {
        int iErr = -1;
        int iTaskId = (int) p1.iTaskId;
        //
        Log.e("anc","tmpHandler_pauseTaskAv start");
        //p1
        taskCommon.TASK_common taskMem = (taskCommon.TASK_common) p2;

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(ctxCaller, pProcInfo, taskMem.m_index_taskInfo, "tmpHandler_pauseTaskAv");
        if (pTaskInfo == null) return iErr;
        qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
        if (pTaskData == null) return iErr;

        if (pTaskData.msgU.uiType == qnmCommProc_mis.Consts.CONST_misMsgType_task && ((qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU).iTaskId == iTaskId) {
            taskAv.TASK_transferAv task = (taskAv.TASK_transferAv) taskMem;
            //
            Log.e("anc","tmpHandler_pauseTaskAv stopTransformThreads 1");

            qmcTransform.qyFuncs.stopTransformThreads(pProcInfo, task.myTask, "tmpHandler_pauseTaskAv");

            //
            Log.e("anc","tmpHandler_pauseTaskAv stopTransformThreads 2");

            iErr = 1;
            return iErr;

        }

        iErr = 0;


        return iErr;
    }

    ///////////////////////////
    public static  int pauseTaskAv(Object hWnd, int iWndContentType, boolean bForece,  String hint) {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) {
            Log.e("pQyMc == null");
            return -1;
        }
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        //
        String str;

        //
        if (hint == null) hint = "";

        //
        /*
        dlgTalkProc.DLG_TALK_var m_var = mOwner_hTalk.get().m_var;
        Object hTalk = mOwner_hTalk;
        int iWndContentType = qyWndContentType.Consts.CONST_qyWndContentType_talker;

         */

        if  (  hWnd  ==  null  )  {
            return  -1;
        }
        //
        if (hWnd instanceof WeakReference<?>) {
            hWnd=((WeakReference<?>) hWnd).get();
        }
        if  (  hWnd  ==  null  )  {
            return  -1;
        }

        //
        dlgTalkProc.DLG_TALK_var m_var = null;
        if  (  iWndContentType ==  CONST_qyWndContentType_talker )  {
            Activity_dlgTalk dlgTalk=(Activity_dlgTalk)hWnd;
            m_var  =dlgTalk.m_talker.m_var;
        }
        else  if  (  iWndContentType==CONST_fgType_talk_av2)  {
                  Fragment_dlgTalk_av2 fg  =  (Fragment_dlgTalk_av2) hWnd;
                  CIsTalker talker = findIsTalker(pProcInfo.pMisCnt,fg.talker_ui64Id);
                  if  (  talker==null)  {
                      return  -1;
                  }
                  m_var  =  talker.m_var;
        }
        else  {
              Log.d("at","pauseTaskAv failed, iWndType err");
              return  -1;
        }



        //
        if (!m_var.av.taskInfo.bTaskExists) {
            Log.d("pauseTaskAv returns when no meeting");
            return -1;
        }

        //
        Log.d("qy","pauseTaskAv.851");

        //
        if (m_var.av.ucbGot_iIndex_sharedObj_localAv) {
            qmcSharedObj.QY_SHARED_OBJ pSharedObj = qmcSharedObj.qyFuncs.getSharedObjByIndex(pProcInfo, m_var.av.iIndex_sharedObj_localAv);
            if (pSharedObj == null) return iErr;
            if (pSharedObj.var.bCapDev) {
                qmcSharedObj.CAP_procInfo_bmpCommon pCapBmp = qmcSharedObj.qyFuncs.getCapBmpBySth(pProcInfo, pSharedObj.var.iIndex_capBmp, 0);
                if (pCapBmp == null) return iErr;

                qmcVideoCapture.qyFuncs.stopCapture(hWnd, iWndContentType, (qmcSharedObj.CAP_procInfo_video) pCapBmp);
                qmcVideoCapture.qyFuncs.closeLocalVideo(hWnd, iWndContentType, m_var.av.iIndex_sharedObj_localAv);

            }

            //
            Log.d("qy","pauseTaskAv.867");

            // audio
            pProcInfo.stopLocalAudioRecorder(m_var.av.iIndex_sharedObj_localAv, 100);

        }

        //
        Log.e("anc","pauseTaskAv--start");

        //
        qnmCommProc_mis.MIS_MSGU p0 = new qnmCommProc_mis.MIS_MSGU();
        qyMcMainProc.PARAM_findOrgMsg param = new qyMcMainProc.PARAM_findOrgMsg();
        param.iTaskId = m_var.av.taskInfo.iTaskId;
        pProcInfo.processQ_media.qTraverse_mono(null, qmcProc::tmpHandler_pauseTaskAv, p0, param);
        m_var.m_bNeedResumeTaskAv = true;

        //
        Log.e("anc","pauseTaskAv--end");



        //
        iErr = 0;
        return iErr;
    }

////////////////////////////////////

    public static int tmpHandler_resumeTaskAv(ctxCaller.CTX_caller ctxCaller, qnmCommProc_mis.MIS_MSGU p0, qyMcMainProc.PARAM_findOrgMsg p1, Object p2) {
        int iErr = -1;
        int iTaskId = (int) p1.iTaskId;
        //p1
        taskCommon.TASK_common taskMem = (taskCommon.TASK_common) p2;

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        qmcTaskInfo.QMC_TASK_INFO pTaskInfo = (qmcTaskInfo.QMC_TASK_INFO) qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(ctxCaller, pProcInfo, taskMem.m_index_taskInfo, "tmpHandler_resumeTaskAv");
        if (pTaskInfo == null) return iErr;
        qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
        if (pTaskData == null) return iErr;

        if (pTaskData.msgU.uiType == qnmCommProc_mis.Consts.CONST_misMsgType_task && ((qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU).iTaskId == iTaskId) {
            taskAv.TASK_transferAv task = (taskAv.TASK_transferAv) taskMem;
            qmcTransform_start.qyFuncs.startTransformThreads(pProcInfo, task.myTask.uiTaskType, task.myTask, "tmpHandler_resumeTaskAv");
            iErr = 1;
            return iErr;

        }

        iErr = 0;


        return iErr;
    }

    //
    public static int resumeTaskAv(Object hWnd, int iWndContentType, String hint) {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if(pQyMc==null) return -1;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        dlgTalkProc.DLG_TALK_var m_var = null;
        Fragment_dlgTalk_av2 m_fg_dlgTalk_av2=null;

        //
        if  (  iWndContentType==CONST_qyWndContentType_talker) {
            Activity_dlgTalk hTalk = null;
            //
            hTalk = (Activity_dlgTalk) hWnd;//mOwner_hTalk.get();
            //int iWndContentType = qyWndContentType.Consts.CONST_qyWndContentType_talker;
            m_var = hTalk.m_talker.m_var;

            //
            if  (  m_var.m_hTool_dlgTalk_av==null)  return  -1;
            //
            Activity_dlgTalk_av  dlgTalk_av  =  (Activity_dlgTalk_av) m_var.m_hTool_dlgTalk_av;
            m_fg_dlgTalk_av2  = dlgTalk_av.m_fg_dlgTalk_av2;
            if  (  m_fg_dlgTalk_av2==null)  {
                return  -1;
            }

        }
        else if ( iWndContentType==CONST_fgType_talk_av2)  {
            m_fg_dlgTalk_av2  =  (  Fragment_dlgTalk_av2) hWnd;
            //
            CIsTalker talker = findIsTalker(pProcInfo.pMisCnt,m_fg_dlgTalk_av2.talker_ui64Id);
            if  (  talker==null)  {
                return  -1;
            }
            m_var = talker.m_var;
        }
        else  {
              Log.d("at","resumeTaskAv: wndType err");
              return  -1;
        }


            //
        String str;

        if (hint == null) hint = "";

        m_var.m_bNeedResumeTaskAv = false;

        //
        if (!m_var.av.taskInfo.bTaskExists) {
            Log.d("resumeTaskAv returns when no meeting ");
            return -1;
        }

        //
        qmcTaskInfo.QMC_TASK_INFO pTaskInfo = qmcTaskInfo.qyFuncs.getQmcTaskInfoByIndex(null, pProcInfo, m_var.av.taskInfo.iIndex_taskInfo, "");
        if (pTaskInfo == null) return iErr;
        qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
        if (pTaskData == null) return iErr;
        qmcTaskInfo.QMC_taskData_conf pTc = (qmcTaskInfo.QMC_taskData_conf) pTaskData;

        //
        pTc.shared_eglContext = m_var.m_share_eglContext;

        //
        //
        if (m_var.av.ucbGot_iIndex_sharedObj_localAv) {
            qmcSharedObj.QY_SHARED_OBJ pSharedObj = qmcSharedObj.qyFuncs.getSharedObjByIndex(pProcInfo, m_var.av.iIndex_sharedObj_localAv);
            if (pSharedObj == null) return iErr;

            //
            int iIndex_sharedObjUsr = 0;
            qmcSharedObj.SHARED_OBJ_USR pSharedObjUsr = qmcVideoFunc.qyFuncs.getSharedObjUsr(pSharedObj, iIndex_sharedObjUsr);
            if (null == pSharedObjUsr) return iErr;
            pSharedObjUsr.uiTranNo_openAvDev_v = 0;

            //
            if (pSharedObj.var.bCapDev) {
                qmcSharedObj.CAP_procInfo_bmpCommon pCapBmp = qmcSharedObj.qyFuncs.getCapBmpBySth(pProcInfo, pSharedObj.var.iIndex_capBmp, 0);
                if (pCapBmp == null) return iErr;
                //
                int uiCamCapType = qmcSharedObj.Consts.CONST_camCapType_android;
                qnmCommProc_mis.VIDEO_COMPRESSOR_CFG compressor = pCapBmp.compressVideo.compressor;
                compressor.shared_eglContext = pTc.shared_eglContext;

                if (0 != qmcVideoCapture.qyFuncs.openLocalVideo(pProcInfo, uiCamCapType, "", hWnd, iWndContentType, compressor, m_var.av.iIndex_sharedObj_localAv)) {
                    str = "resumeTaskAv failed, openLocalVideo failed";
                    Log.e(str);
                    return iErr;
                }
                //
                qmcVideoCapture.qyFuncs.startCapture(hWnd, iWndContentType, (qmcSharedObj.CAP_procInfo_video) pCapBmp);

                //
                qmcSharedObj.COMPRESS_VIDEO pCompressVideo = pCapBmp.compressVideo;

                if (0 != pCompressVideo.uiTranNo_openAvDev_org) {   //
                    pSharedObjUsr.uiTranNo_openAvDev_v = (int) (pCompressVideo.uiTranNo_openAvDev_org + iIndex_sharedObjUsr); //  pSharedObj->iIndex_curUsr;
                }

                //
                m_var.av.taskInfo.local_avStream.tranInfo.video.uiTranNo_openAvDev = pSharedObjUsr.uiTranNo_openAvDev_v;
                m_var.av.taskInfo.local_avStream.tranInfo.video.compressor = pCompressVideo.compressor.common.head;
                m_var.av.taskInfo.local_avStream.tranInfo.video.vh_decompress = pCompressVideo.vh_decompress;
                m_var.av.taskInfo.local_avStream.tranInfo.video.vh_compress = pCompressVideo.vh_compress;
                m_var.av.taskInfo.local_avStream.tranInfo.video.vh_stream = pCompressVideo.vh_stream;

                //
            }

            // audio
            qmcSharedObj.CAP_procInfo_audioCommon pCapAudio = qmcSharedObj.qyFuncs.getCapAudioBySth(pProcInfo, pSharedObj.var.iIndex_capAudio, 0);
            qnmCommProc_mis.AUDIO_COMPRESSOR_CFG aCompressor = pCapAudio.compressAudio.compressor;
            if (0 == audioRecorder_dsound.qyFuncs_Droid.startLocalAudioRecorder(pProcInfo.cfg.policy.audio.uiRecordType, null, "", m_var.av.iIndex_sharedObj_localAv, aCompressor)) {
            }


        }


        //
        qnmCommProc_mis.CMIS_MSGU p0 = new qnmCommProc_mis.CMIS_MSGU();
        qyMcMainProc.PARAM_findOrgMsg p1 = new qyMcMainProc.PARAM_findOrgMsg();
        p1.iTaskId = m_var.av.taskInfo.iTaskId;
        pProcInfo.processQ_media.qTraverse_mono(null, qmcProc::tmpHandler_resumeTaskAv, p0.msgU, p1);


        //
        //Activity_dlgTalk dlgTalk = mOwner_hTalk.get();
        boolean canSpeak = dlgTalkProc_help.qyFuncs_Droid.dlgTalk_canSpeak(m_var.addr.idInfo.ui64Id);

        if (canSpeak) {
            //
            dlgTalk_requestToSpeak(m_var.addr.idInfo.ui64Id, canSpeak);

            boolean bState_canSpeak = canSpeak;

            //
            m_fg_dlgTalk_av2.updateRequestButtonStatus();

        }


        //
        iErr = 0;


        Log.d("resumeTaskAv leaves with " + iErr);

        //
        return iErr;
    }



}
