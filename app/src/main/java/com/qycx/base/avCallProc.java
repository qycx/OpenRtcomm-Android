package com.qycx.base;

import static com.qycx.base.confReq.qyFuncs_Droid.sendConfReq;
import static com.qycx.base.isTalkerQProc.findIsTalker;
import static com.qycx.base.qmcProc.doChkTalkDraw;
import static com.qycx.base.qmcProc.isGroup;
import static com.qycx.base.qmcTaskInfo.qyFuncs.freeTaskInfo;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_imOp_applyForConf;
import static com.qycx.base.qyMcMain_global.g.g_pQyMc;
import static com.qycx.base.qyWmComm.Consts.CONST_do_1080P_AV_Call;
import static com.qycx.base.qyWmComm.Consts.CONST_do_480P_AV_Call;

import android.app.Activity;
import android.os.Build;
import android.view.View;

import com.qycx.Activity_dlgTalk;
import com.qycx.Activity_dlgTalk_av;
import com.qycx.MainActivity;
import com.qycx.R;
import com.qycx.base.util.Log;

import pub.devrel.easypermissions.EasyPermissions;

public class avCallProc {

    //
    //static String[] mPermissionList = new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO};

    //
    public static boolean anc_hasAllPermission(Activity act) {
        return EasyPermissions.hasPermissions(act, Consts.mPermissionList);
    }


    //
    public static void anc_isPermissionOk(Activity act) {

        // boolean bRet = false;
        //int ii = 0;
        //
        if ((int) Build.VERSION.SDK_INT >= 23) {
            if (!anc_hasAllPermission(act )) {
                // Ask for one permission
                EasyPermissions.requestPermissions(act, act.getResources().getString(R.string.permission_content_camera_mic), Consts.REQUEST_CODE_CAMERA_RECORDAUDIO_PERMISSION, Consts.mPermissionList);
            }
        }
    }


    //
    public static int addTo_tmpTools(Object  act_av, dlgTalkProc.DLG_TALK_var pMgrVar, String hint) {
        int iErr = -1;
        String str;

        if (hint == null) hint = "";

        //
        if  (  act_av==null )  {
            Log.d("at","addTo_tmpTools failed, act_av is null");
            return  -1;
        }

        //
        boolean bOk = false;
        if  (  act_av instanceof Activity_dlgTalk_av)  {
            bOk=true;
        }
        else  if  (  act_av instanceof  MainActivity)  {
            bOk =true;
        }
        if  (  !bOk)  {
            Log.d("at","addTo_tmpTools failed, act_av err");
            return  -1;
        }


        //
        int i;
        for (i = 0; i < pMgrVar.tmp_hTools_dlgTalk_av.length; i++) {
            if (pMgrVar.tmp_hTools_dlgTalk_av[i] == null) {
                pMgrVar.tmp_hTools_dlgTalk_av[i] = act_av;//this;
                break;
            }
        }
        if (i == pMgrVar.tmp_hTools_dlgTalk_av.length) {
            str = String.format("dlgTalk_av.OnCreate failed, too many tmp_hTools");
            Log.d(str);
            //
            return iErr;
        }


        iErr = 0;

        return iErr;
    }


    //
    public static int removeFrom_tmpTools(Object act_av, dlgTalkProc.DLG_TALK_var pMgrVar, String hint) {
        int iErr = -1;

        String str;

        if (hint == null) hint = "";

        int i;
        for (i = 0; i < pMgrVar.tmp_hTools_dlgTalk_av.length; i++) {
            if (pMgrVar.tmp_hTools_dlgTalk_av[i] == act_av) {
                pMgrVar.tmp_hTools_dlgTalk_av[i] = null;
                pMgrVar.tmp_share_eglContexts[i] = null;
                //
                break;
            }
        }

        //
        //mOwner_hTalk.gui_PostMessage(Consts.CONST_qyWm_postComm, Consts.CONST_qyWmParam_chkTalkDraw, 0);

        //
        doChkTalkDraw(pMgrVar);

        //
        iErr = 0;

        return iErr;
    }



 //
 public static int act_toAvCall(Activity act,long talker_ui64Id )
{
    int  iErr =  -1;
    ctxQyMc.CCtxQyMc  pQyMc  = g_pQyMc;

    do {
        //
        audioRecorder_android.qyFuncs_Droid.setMicrophoneOn(true);

        //
        int level;
        level = CONST_do_1080P_AV_Call;

        //
        if (pQyMc.appParams.bDbg) {
            level = CONST_do_480P_AV_Call;
        }

        //
        if (anc_hasAllPermission(act)) {
            //
            Log.d("at","hasPermission");
            talk_doAvCall(act, talker_ui64Id,level,false);

        } else {
            anc_isPermissionOk(act);
        }

        //
        iErr =  0;
    } while ( false);
    return  iErr;
};


    public static int removeTaskAvOfPeer(qyDefs.QY_MESSENGER_ID idInfo_peer)
    {
        ctxQyMc.CCtxQyMc pQyMc  =  g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo  =  pQyMc.get_pProcInfo();
        int index_taskInfo;
        qmcTaskInfo.QMC_TASK_INFO pTaskInfo;

        if  (  idInfo_peer.ui64Id==0)return  -1;

        for  (  index_taskInfo  =  0;  index_taskInfo  <  pProcInfo.cfg.usMaxCnt_taskInfos;index_taskInfo++)  {
            pTaskInfo= pProcInfo.getQmcTaskInfoByIndex(null,index_taskInfo,"removeTaskOfPeer");
            if(!pTaskInfo.bUsed)continue;
            qmcTaskInfo.QMC_taskData_common pTaskData=pTaskInfo.getTaskData();
            if  (  pTaskData==null)continue;;
            qnmCommProc_mis.MIS_MSG_TASK pMsgTask  =  (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
            if  (  pMsgTask.uiType!= qnmCommProc_mis.Consts.CONST_misMsgType_task) continue;
            qnmCommProc_mis.IM_CONTENTU pContent=pMsgTask.data.buf;
            if  (  pContent.uiType!= imCommType_defs.Consts.CONST_imCommType_transferAvInfo)continue;
            //
            qyDefs.QY_MESSENGER_ID tmp_idInfo_peer  =  pMsgTask.addr_logicalPeer.idInfo;
            //
            //Log.d("qy","removeTaskAvOfPeer: idInfo_peer "  +  tmp_idInfo_peer.ui64Id);
            //
            if  (  tmp_idInfo_peer.ui64Id==idInfo_peer.ui64Id)  {
                freeTaskInfo(null,pProcInfo,index_taskInfo,"removeTaskOfPeer");
            }

            continue;
        }


        return  0;
    }


    //
    public static int doTask_startAvCall(Object actParam, dlgTalkProc.DLG_TALK_var m_var, int level) {
        int iErr = -1;

        //
        if (actParam == null) return -1;
        Activity act = null;
        if (actParam instanceof Activity) {
            act = (Activity) actParam;
        }
        if  (  act ==null)  {
            Log.d("at","doTask_startAvCall failed, act is null");
            return  -1;
        }



        //
        m_var.av.bNeedDoTaskAv = true;

        //
        int  iCmd = tmpResource.IDC_BUTTON_av;
        int  index_taskInfo = -1;


        //
        if  (  act instanceof  Activity_dlgTalk) {
            Activity_dlgTalk dlgTalk = (Activity_dlgTalk) act;
            //
            if (anc_hasAllPermission(act)) {
                dlgTalk.startActivity_dlgTalk_av(iCmd, index_taskInfo, level, "doTask_startAvCall");
            } else {
                anc_isPermissionOk(act);
            }
        }
        //
        if  (  act instanceof MainActivity)  {
            MainActivity mainWnd = (MainActivity) act;
            //
            if (anc_hasAllPermission(act)) {
                mainWnd.makeFloatingView(m_var.addr.idInfo.ui64Id, iCmd, index_taskInfo,level);
            }
            else  {
                  anc_isPermissionOk(act);
            }
        }

        //
        iErr = 0;
        return iErr;

    }


    //
    public static int talk_doAvCall(Object act, long talker_ui64Id, int level,boolean bStartConf) {
        int  iErr  =  -1;

        //
        ctxQyMc.CCtxQyMc pQyMc =g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo  =  pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt=pProcInfo.pMisCnt;

        //
        qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
        idInfo.ui64Id=  talker_ui64Id;

        //
        CIsTalker talker=findIsTalker(pMisCnt,talker_ui64Id);
        if  (  talker==null)  {
            Log.d("at","talk_doAvCall: findIsTalker failed");
            return  -1;
        }

        //
        dlgTalkProc.DLG_TALK_var m_var = talker.m_var;

        //
        do {
            //
            if (isGroup(m_var)) {

                //
                if (!bStartConf) {
                    //
                    removeTaskAvOfPeer(idInfo);

                    //
                    pProcInfo.av.talk_doAv_info.usAvLevel  =  level;

                    //
                    short usOp = CONST_imOp_applyForConf;
                    //
                    sendConfReq(null, idInfo, (short)usOp, (short) level, (short) 0,null,null,null);
                    //
                    iErr  =  0;  break;
                }
                else  {
                    Log.d("qy","here");
                }

            }

            //
            if  (  true   ) {
                doTask_startAvCall(act,m_var, level);
            }

            //
            iErr  =  0;

            //
        } while(false);

        return  iErr;
    }


    //
    public static int doTask_av_accept(Activity act, dlgTalkProc.DLG_TALK_var m_var, boolean bStay_toAvCall) {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

        if (!(mainActivity.var_common.notifyTaskStatus.bExists_task && mainActivity.var_common.notifyTaskStatus.bAvCall)) {
            Log.d("Can't start a avTask, no task needs be accepted.");
            return iErr;
        }

        qmcTaskInfo.QMC_TASK_INFO pTaskInfo = pProcInfo.getQmcTaskInfoByIndex(mainActivity.var_common.ctxCaller, mainActivity.var_common.notifyTaskStatus.index_taskInfo, "doTask_av_accept");
        if (pTaskInfo == null) return iErr;
        qmcTaskInfo.QMC_taskData_common pTaskData = pTaskInfo.getTaskData();
        if (pTaskData == null) return iErr;

        //
        if (pTaskData.msgU.uiType != qnmCommProc_mis.Consts.CONST_misMsgType_task) {
            return iErr;
        }
        qnmCommProc_mis.MIS_MSG_TASK task = (qnmCommProc_mis.MIS_MSG_TASK) pTaskData.msgU;
        if (task.addr_logicalPeer.idInfo.ui64Id != m_var.addr.idInfo.ui64Id) {
            Log.d("Can't start a avTask, because logicalPeer != talker.");
            return iErr;
        }

        //
        m_var.av.bNeedDoTaskAv = true;

        do {
            //
            if (bStay_toAvCall) {

                //
                dlgTalkProc.DLG_TALK_var pMgrVar = m_var;

                //
                int  iCmd = tmpResource.IDC_BUTTON_av_accept;
                int level = qyWmComm.Consts.CONST_do_1080P_AV_Call;
                int index_task  =  mainActivity.var_common.notifyTaskStatus.index_taskInfo;

                //  下面相当于生成dlgTalk_av
                if  (  false  ) {
                    pMgrVar.m_talkAvVar.reset();

                    //
                    pMgrVar.m_talkAvVar.m_iCmd = iCmd;
                    pMgrVar.m_talkAvVar.m_Level = level;
                    pMgrVar.m_talkAvVar.m_index_taskInfo1 = index_task;

                    //
                    if (addTo_tmpTools(act, pMgrVar, "") != 0) {
                        Log.e("at", "doTask_av_accept l337: addTo_tmpTools failed");
                        break;
                    }
                }

                //  生成fg
                mainActivity.makeFloatingView(m_var.addr.idInfo.ui64Id,iCmd,index_task,level);

                //
                break;
            }


            //
            if (anc_hasAllPermission(act)) {
                if (act instanceof Activity_dlgTalk) {
                    Activity_dlgTalk hTalk = (Activity_dlgTalk) act;
                    hTalk.startActivity_dlgTalk_av(tmpResource.IDC_BUTTON_av_accept, mainActivity.var_common.notifyTaskStatus.index_taskInfo, qyWmComm.Consts.CONST_do_1080P_AV_Call, "doTask_av_accept");
                }
            } else {
                anc_isPermissionOk(act);
            }

            iErr = 0;

        }  while  (false);

        return iErr;
    }



}
