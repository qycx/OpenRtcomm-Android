
#include    <string.h>

#include    "myTypes.h"
#include    "tmpAecPublic.h"
#include "../../tmpQMgr.h"
#include    <pthread.h>
#include    "audioProcPublic.h"
#include    "aec3libPublic.h"
#include "ctxQmThread.h"
#include "../../tmpQMgr.h"
#include    "webrtc_audio_core.h"
#include    <stdlib.h>

//
int tmp_getPreAecData(GENERIC_Q   *  pQ,  TmpCache   *  pCache,  char  *  buf_o,  int size);


//
int my_inStream4_mic(void* p0, void* p1, void* p2, void* p3) {
    int nRead = 0;
    int iErr = -1;
    int index_source = (int) (long) p1;
    char *buf_o = (char *) p2;
    int size1 = (int) (long long) p3;

    //
    CTmpQMgr  *pMgr  =  &g_tmpQMgr;

    //
    int  size  =  160  *  2;

    //
    char  recBuf[4096];
    if  (tmp_getPreAecData(&pMgr->m_tmpRec.aecThreadQ,  &pMgr->m_tmpRec.aecThreadCache,  recBuf,size))  {
        //
        memset(recBuf,0,size);
        //
        if  (  0  ) {
            //LOGD("aec: get aecThreadQ failed, memset 0");
        }
        //
        nRead=size;
    }
    else  {
          nRead  =  size;
    }

    memcpy(buf_o,recBuf,size);



    //
    return  nRead;

}

int my_inStream4_rev(void* p0, void* p1, void* p2, void* p3) {
    int nRead = 0;
    int iErr = -1;
    int index_source = (int) (long) p1;
    char *buf_o = (char *) p2;
    int size = (int) (long long) p3;

    CTmpQMgr  *pMgr  =  &g_tmpQMgr;

    char  aecPlayBuf[4096];
    if  (tmp_getPreAecData(&pMgr->m_tmpRec.aecPlayQ,&pMgr->m_tmpRec.aecPlayCache,aecPlayBuf,size))  {
        //
        memset(aecPlayBuf,0,size);
        //
        nRead = size;
        //
        int  tmp_nQNodes  =  pMgr->m_tmpRec.aecPlayQ.uiQNodes;
        if  (  g_bPrintErr) {
            LOGD("my_inStream4_rev: get aecPlayQ failed, nQNodes %d. memset 0. l74. ", tmp_nQNodes);
        }

    }
    else  {
        nRead  =  size;
    }

    memcpy(  buf_o, aecPlayBuf,size);

    //
    return  nRead;

}


int  my_preAp(void*p0,void*p1,void*p2)
{
    AEC_layout   *  pLayout  =  (  AEC_layout  *  )p0;
    int  tickCnt  = myGetTickCount(mynull);
    int  iDiffInMs  =  tickCnt  -  pLayout->apInfo.tickCnt_startToAp;
    int  nTimes_mix_need  =  (  iDiffInMs  +  0  )  /  10;

    //
    pLayout->apInfo.nTimes_ap  ++  ;

    //
    //LOGD("my_preAp: nTimes_mix_need %d, layout->nTimes_mix %d, tickCnt_start %d",  nTimes_mix_need,  pLayout->apInfo.nTimes_ap,  (int)pLayout->apInfo.tickCnt_startToAp);

    //
    if  (  pLayout->apInfo.nTimes_ap  <  nTimes_mix_need)  {
        //
        //LOGD("my_preMix: nTimes_mix %d, layout->nTimes_mix %d",  nTimes_mix_need,  pLayout->nTimes_mix);
        //
        return  0;
    }

    //
    CTmpQMgr  *  pMgr  =  &g_tmpQMgr;
    int  i;

#if  0
    iDiffInMs  =  tickCnt  -  pLayout->tickCnt_last_adjustQNodes_mix;
    if  (  abs(iDiffInMs)  >  100) {
        pLayout->tickCnt_last_adjustQNodes_mix  =  tickCnt;
        bool  bExists  = false;
        for (i = 1; i < pMgr->m_usMaxCnt; i++) {
            TmpQMem *pQMem = getTmpQMem(0, i);
            if (pQMem->q.uiQNodes > pQMem->q.cfg.uiMaxQNodes / 2) {
                bExists  =  true;
                break;
            }
        }
        if  (  bExists)  {
            //  把tickCnt_startToMix提前，就可以加快处理
            pLayout->tickCnt_startToMix  -=  10;
            //
            LOGD("my_preMix: qNodes too big, reduce tickCnt_startToMix, to adjust qNodes");
        }
    }
#endif

    //
    mySleep(10);

    //
    return  0;
}


int my_outStream_ap(void* p0, void* p1, void* p2) {
    int iErr = -1;
    AEC_layout *pLayout = (AEC_layout *) p0;
    char *buf_o = (char *) p1;
    int size = (int) (long long) p2;

    CTmpQMgr  *pMgr  =  &g_tmpQMgr;

    //
    int loopCtrl  =  pLayout->apInfo.nTimes_ap;

    //
    TmpAPkt  pkt;
    memset(&pkt,0, offsetof(TmpAPkt,buf));
    pkt.iSampleTimeInMs  =  loopCtrl  *  10;
    pkt.uiPts  =  loopCtrl;
    pkt.len  = size;
    memcpy(  pkt.buf, buf_o,size);

    int  pktSize  = offsetof(TmpAPkt,buf)  +  pkt.len;
    int  tmpiRet;
    tmpiRet  =  qPostMsg(&pkt, pktSize,&pMgr->m_tmpRec.recQ, _T(""  ) );
    if  (  tmpiRet  )  {
        //
        if  (  g_bPrintErr) {
            LOGE("my_outStream_ap: recQ: qPostMsg failed. tmpiRet %d. l168", tmpiRet);
        }

    }

    //
    /*
    if  (  bTest)  {
        tickCnt_afterAec  = myGetTickCount(mynull);
        //
        iDiffInMs  =  tickCnt_afterAec  -  tickCnt_beforeAec;
        LOGD("aec: costs %dms. aecThreadQ.nQNodes %d. bSleep %d, loopCtrl %d, nTimes_elapse %d, tickCnt_start %d, tickCnt %d",
             iDiffInMs,  pMgr->m_tmpRec.aecThreadQ.uiQNodes, bSleep,  loopCtrl,  nTimes_elapse,  tickCnt_start,  tickCnt);
    }*/

    return  0;

}


void *mcThreadProc_ap(void*data)
{
    int  iErr   =  -1;
    pthread_t  tid;
    AEC_layout  *pLayout;
    //unsigned int i;

    tid = pthread_self();
    //counter = (t_counter*)data;
    //t_m  *  pm  =  (t_m*)data;
    //counter  =  pm->pcounter;
    pLayout  =  (AEC_layout*)data;

    //
    LOGD("thread_ap: starts");

    //
    //int iDiffInMs  =  myGetTickCount(mynull)  -  counter->tickCnt0;
    //PF_inStream4  pf = my_inStream4_toMix;
    //void  *  pObj  = mynull;
    int nSources  =  MAX_tmpQMems  -  1;//
    bool  tmp_bErr  =  false;

    //
    pLayout->status.nStep_ap  =  1;

    //
    do {

        //
        int  sampleRate  =  16000;

        //
        //int  nSources = MAX_fps;
        void* p0 = pLayout;
        //
        PF_inStream4 pf = my_inStream4_mic;
        PF_inStream4 pf_rev  =  my_inStream4_rev;
        PF_preMix pf_preMix = my_preAp;
        PF_outStream  pf_o = my_outStream_ap;

        //
        for  (  ;  ;  ) {
            //
            if  (  pLayout->bQuit)  {

                LOGD("ap: bQuit is true, break");

                break;
            }

            //
            pLayout->apInfo.tickCnt_startToAp = myGetTickCount(mynull);
            pLayout->apInfo.nTimes_ap = 0;

            //
            pLayout->status.nStep_mix = 10;

            //
            LOGD("mix: before mixer_main");

            //
            ap_main(&pLayout->tmpAp, nSources, pf, p0, pf_rev,p0,pf_preMix, p0, pf_o, p0);

            //
            LOGD("ap: after apmain");

            //
            if  (  pLayout->tmpAp.bStop)  {
                //
                LOGD("mix: bStop is true, reset it ");
                //
                pLayout->tmpAp.bStop  = false;
            }

            //
            continue;
        }


        //
        iErr  =  0;

    } while  ( false  );


    //
    LOGD("thread_ap: ends");

    //
    pLayout->status.nStep_ap  =  100;

    //
    return  NULL;
}
