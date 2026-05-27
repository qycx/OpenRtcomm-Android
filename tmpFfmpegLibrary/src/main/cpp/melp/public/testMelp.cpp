

#include    "myTypes.h"

#include    "myMelpPublic.h"

#include    "qySyncObj.h"
//#include    "ctxQmThread.h"
#include    "tmpLogFunc_open.h"


//
int enc_main(int argc, char *argv[]);
int dec_main(int argc, char *argv[]);




//
int g_bDbg=0;
int g_bPrint=0;
int g_bPrint1 =  0;
int  g_tmpCnt  =  0;
int g_bPrint17  =  0;
//
int g_frameCnt = 0;

#if  0
//
typedef  struct  __testInfo_t  {
                 mybool  bQuit;

                 MyMutex  kk;

                 //
                 pthread_t               tid_0;

                 //
                 pthread_t               tid_1;

                 //
                 int                     loopCtrl;

} TestInfo;



//
void *mcThreadProc_0(void*data)
{
    TestInfo  * p = (TestInfo*)data;

    //
    for  (  ;  ;  )  {
         if  (  p->bQuit  )  break;
         //
        {
            CQySyncObj syncObj;
            if (syncObj.sync(_T(""), &p->kk, _T(""))) continue;
            //
            LOGD("tid_0: sync ok");

            //
            for (;;) {
                if  (  p->bQuit)break;
                if  (  0!=(  p->loopCtrl  %5  )  )  break;
                //
                LOGD("tid_0: here. loopCtrl %d", p->loopCtrl);
                mySleep(100);

            }

        }

        continue;

    }

    //
    return mynull;
}

//
void *mcThreadProc_1(void*data)
{
    TestInfo  * p = (TestInfo*)data;

    //
    mySleep(100);

    //
    for  (  ;  ;  )  {
        if  (  p->bQuit  )  break;
        //
        {
            CQySyncObj syncObj;
            if (syncObj.sync(_T(""), &p->kk, _T(""))) continue;
            //
            LOGD("tid_1: sync ok");

            //
            for (;;) {
                if  (  p->bQuit)break;
                if  (  0==(  p->loopCtrl  %5  )  )  break;
                //
                LOGD("tid_1: here. loopCtrl %d", p->loopCtrl);
                mySleep(100);

            }

        }

        continue;

    }

    //
    return mynull;
}




//
int  testMelp(int  *  p_i)
{
    //      public static final String SAVE_DIR = "/sdcard/DCIM/Camera2GetPreview/";

    static char* enc_cmd_line[] = { "melpe", "/storage/emulated/0/Download/250710.pcm", "/storage/emulated/0/Download/kk_24.raw", 0 };
    static char* dec_cmd_line[] = { "melpe", "/storage/emulated/0/Download/kk_24.raw", "/storage/emulated/0/Download/kkqqq.pcm", 0 };
    int argc = 3;

    //
    TestInfo ti;
    memset(&ti,0,sizeof(ti));

    //
    init_tickCnt();

    //
    if  (  initMyMutex(mynull,&ti.kk))  {
        goto  errLabel;
    }

    //
    pthread_t tid1;

    do {

        //
        if (pthread_create(&tid1, NULL, mcThreadProc_0, &ti)) break;
        ti.tid_0 = tid1;
        LOGD("AEC_layout_init: mix thread created, tid %d", (int) tid1);

        //
        if (pthread_create(&tid1, NULL, mcThreadProc_1, &ti)) break;
        ti.tid_1 = tid1;

        //
        int  i;
        for  (  i  =  0;  i  <  100;  i  ++  )  {
             ti.loopCtrl  ++  ;
             //
             long tickCnt0  = myGetTickCount(mynull);

             //
            mySleep(1000);

            //
            long tickCnt1  = myGetTickCount(mynull);
            //
            long iDiffInMs  = tickCnt1  -  tickCnt0;
            //LOGD("iDiffInMs ");



        }


    } while ( 0 );


    //
    LOGD("testHelp: will quit now");
    ti.bQuit=true;
    //
    pthread_join(ti.tid_0,mynull);
    LOGD("testMelp:tid_0 waited");
    pthread_join(ti.tid_1,mynull);
    LOGD("testMelp:tid_1 waited");

errLabel:

    exitMyMutex(&ti.kk);

    //
    return  0;
}
#endif

//
#ifdef  __arm__
int  testMelp(int  *  p_i)
#else
int main(int argc,char *argv[])
#endif
{
    //      public static final String SAVE_DIR = "/sdcard/DCIM/Camera2GetPreview/";

    //
    return  0;

    //
#ifdef  __arm__
    static char *enc_cmd_line[] = {"melpe", "/storage/emulated/0/Download/331.pcm",
                                   "/storage/emulated/0/Download/kk_24.raw", 0};

    //
    static char* dec_cmd_line[] = { "melpe", "/storage/emulated/0/Download/kk_24.raw",
                                   "/storage/emulated/0/Download/kkqqq.pcm", 0 };

    //
    static char* dec_dump_cmd_line[] = { "melpe", "/storage/emulated/0/Download/decA_dumpEncData.melp",
                                    "/storage/emulated/0/Download/hy_dumpEncData.pcm", 0 };

    //
        //
    int argc;
    char **argv;


#else


    static char* enc_cmd_line[] = { "melpe", "D:\\tttbbb\\331.pcm",
                               "D:\\tttbbb\\33bz.raw", 0 };
    static char* dec_cmd_line[] = { "melpe", "d:\\tttbbb\\33bz.raw",
                                   "d:\\tttbbb\\33hy.pcm", 0 };
#endif



    int argc1 = 3;


    //
    g_bDbg=true;

    //
    if (0) {
        EncAMelp encObj;
        memset(&encObj, 0, sizeof(encObj));

        argc = argc1;
        argv = enc_cmd_line;

        //
        melp_enc_main(argc, argv, &encObj);

        //
        printf("hh\n");
    }
    else  {
        DecAMelp decObj;
        memset(&decObj, 0, sizeof(decObj));

        argc = argc1;
        argv = dec_cmd_line;

        //
        argv = dec_dump_cmd_line;

        //
        //melp_dec_main(argc,dec_cmd_line,&decObj);
        melp_dec_main(argc, argv, &decObj);

        //
        printf("hh\n");
    }



    //
    return  0;
}





