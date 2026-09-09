
package com.qycx.base;

public class ctxQmcTmpl {

    public static class Consts {
        public final static String CONST_fn_dump_h264_enc = "dump_h264_enc.264";
        public final static String CONST_fn_dump_pcm_enc = "dump_pcm_enc.pcm";

        //
        public final static String CONST_fn_dump_h264_dec = "dump_h264_dec.264";

        //
        public final static String CONST_fn_dump_mp3_enc = "dump_mp3_enc.mp3";
        public final static String CONST_fn_dump_mp3_dec = "dump_mp3_dec.mp3";


    }

    public static class QMC_threads_status {
        public boolean ulbMgrQuit;                                             //
        public boolean ulbDisplayQuit;                                         //  display
        public boolean ulbWorkQuit;                                                //  work
        public boolean ulbSchedulerQuit;                                           //  scheduler
        public boolean ulbRobotQuit;                                               //  svr
        public boolean ulbMediaQuit;                                               //  media
        public boolean ulbRealTimeMediaQuit;                                       //  realTimeMedia
        public boolean ulbRtOpQuit;                                                //  2016/12/26
        public boolean ulbLogQuit;                                             //  log

    }

    public static class QMC_status_retrieveImObjList {
        public boolean ulbImGrpExists;                                            //  是否下载了分组信息。2011/04/10.
        public boolean ulbContactExists;                                           //  是否下载了contact信息
        //
        public boolean ulbAllContactsRetrieved1;                                    //  是否用户信息都下载了. 2013/08/01
        //
        public int nTimes_refresh;                                              //  因为access写入较慢,所以,在所有用户信息得到后,需要自动的刷新几次. 比如3次
        public int dwTickCnt_lastRefreshed;
    }

    ;


    public static class QMC_status_progress {
        public Object hWnd;
        public int uiTranNo;
        //
        public boolean ucbProgessEnd;
        public int nResult;
        //
    }

    ;


    public static class QmcStatus_ptsStat {
        public qnmCommProc_mis.TRANS_pts_info transPtsInfo_sum;
        public qnmCommProc_mis.TRANS_pts_info transPtsInfo_sum_last;
        //
        public boolean bTooManyPtsErr;
        public byte ucCnt_bTooManyPtsErr;
        public int dwTickCnt_lastModified_bTooManyPtsErr;
        //
        public boolean bWarning;

    }

    ;

    public static class QMC_status_cpu {                                                            //  for cpu usage. 2009/10/10

        public int iCpuUsage;
    }

    ;


    //
    public static class RENDER_status {
        public float fps;
        public int dwTickCnt_calcFps;
    }

    ;


    public static class QMC_status {
        public boolean ulbThreadProcInited;                                        //  mcThreadProc_mis 2007/09/02

        public QMC_threads_status threadsStatus = new QMC_threads_status();                                                                                    //

        //
        public QMC_status_retrieveImObjList retrieveImObjList = new QMC_status_retrieveImObjList();

        //
        public long dwTickCnt_toStat;                                         //  2009/10/11


        //
        public QMC_status_cpu cpu = new QMC_status_cpu();

        public qyCommProc.QMC_net_stat netStat = new qyCommProc.QMC_net_stat();                                                    //  2009/10/10
        //MEMORYSTATUS memory;                                                        //  201007/28
        public RENDER_status render;

        public QMC_status_progress progress = new QMC_status_progress();
        public boolean bInSequence_showDlgProgress;           //  2014/05/07


        //  2012/03/25
        public int nThreads_dec;
        public int nThreads_dispatch;                     //  2013/08/26
        public int nThreads_player;
        public int nThreads_aTrans;                       //  2014/03/15
        public int nThreads_vTrans;                       //  2014/03/15
        public int nThreads_talkerProc;                   //  2016/03/15
        public int nThreads_shareDynBmps;                 //  2016/06/15

        //  2013/03/07
        public int dwTickCnt_chkDb;


        //  2017/10/03
        public long dwTickCnt_sendTo_applyForOrgReq;      //  2017/10/03

        //  2015/06/18
        public QmcStatus_ptsStat ptsStat;

        //
        public ctxQmc.AtTasksStatus tasksStatus = new ctxQmc.AtTasksStatus();


//  2018/10/30
//char vName_1st_webcam[256];	

    };




    //
    public abstract static class CCtxQmcTmpl extends ctxQyTmpl.CCtxQyTmpl {
        //
        public ctxQyMc.CCtxQyMc pQyMc;
        //Object pSgiParam;

        public CCtxQmcTmpl() {

        }

        public abstract int setQmDbFuncs(int iDbType, myDb.QM_dbFuncs pDbFuncs);

    }


}
