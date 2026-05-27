
#include    "myTypes.h"
#include    <pthread.h>
#include    <string>
#include    "tmpAecPublic.h"
#include    "ctxQmThread.h"
#include    "ctxQmThread.h"
#include "../../tmpQMgr.h"

#include "../../../jniLibs/include/audioProcPublic.h"
#include "../../tmpQFunc.h"

//
int my_inStream4_toMix(void* p0, void* p1, void* p2, void* p3)
{
    int  nRead = 0;
    int  iErr = -1;
    int index_source = (int)(long)p1;
    char* buf_o = (char*)p2;
    int size = (int)(long long)p3;
    //
    CTmpQMgr * pMgr  =  &g_tmpQMgr;


    //
    AEC_layout* pAecLayout = &pMgr->m_aecLayout;
    int wBits = 16;

    do  {
    //
    if (index_source < 0 || index_source >= pAecLayout->usCntLimit_activeMems_from) {
        //showInfo_open(0, 0, _T("my_inStream4_toMix failed, index_source err"));
        break;
    }
    int  index_qMem=  index_source  +  1;
    TmpQMem * pMem = &pMgr->mems[index_qMem];
#if  0
    if (!pMem->idInfo.ui64Id) goto  errLabel;
    if (!pMem->tn_a) {
        goto  errLabel;
    }
    if (!pMem->playerId.uiTranNo_player)goto  errLabel;
    QY_PLAYER* pPlayer;
    pPlayer = getPlayerByIndex(pProcInfo, pMem->playerId.index_player);
    if (!pPlayer)goto  errLabel;
    if (pPlayer->uiTranNo_player != pMem->playerId.uiTranNo_player)goto  errLabel;
#endif

    //
    if  (  pMem->startIndex)  {
        memcpy(  pMem->buf,  pMem->buf  +  pMem->startIndex,  pMem->nLeft);
        pMem->startIndex  =  0;
    }

    //
    TmpAPkt  pkt;

    //
    for  (  ;  ;  ) {
        if (pMem->nLeft>=size)break;
        //
        GENERIC_Q   *  pQ  =  &pMem->q;
        //pQ  =  &pMem->tmp_q;
        //
        unsigned  int  len  =  sizeof(pkt);
        if  (  qGetMsg(pQ,&pkt,&len)  ) {
            break;
        }

        //
        if  (  0  )  {
            LOGD("my_inStream4_toMix: index_qMem %d: getMsg: pts %d",  index_qMem,  pkt.uiPts);
        }

        //
        len  =  pkt.len;
        if  (  sizeof(pMem->buf)  -  (  pMem->startIndex  +  pMem->nLeft )  <  len  ) {
            LOGE("my_inStream4_toMix: too big audio data, skipped");
            break;
        }
        memcpy(  pMem->buf  +  pMem->startIndex  +  pMem->nLeft,  pkt.buf,  len);
        pMem->nLeft  +=  len;
        //
        continue;
    }

    if  (  pMem->nLeft  <  size  )  {
        //  填空
        memset(  buf_o,  0,  size);
        nRead  =  size;
    }
    else  {
          memcpy(  buf_o,  pMem->buf  +  pMem->startIndex,  size );
          pMem->startIndex  +=  size;
          pMem->nLeft  -=  size;
          //
          nRead  =  size;
    }



    //
    iErr = 0;

    }  while ( false  );


    //errLabel:

    if (iErr) {
#if  0
        fillSilence(wBits, buf, size);
		nRead = size;
#endif
        //
        nRead = 0;
    }

    //
    return  nRead;
}


//
int  my_preMix(void*p0,void*p1,void*p2)
{
    AEC_layout   *  pLayout  =  (  AEC_layout  *  )p0;
    int  tickCnt  = myGetTickCount(mynull);
    int  iDiffInMs  =  tickCnt  -  pLayout->mixInfo.tickCnt_startToMix;
    int  nTimes_mix_need  =  (  iDiffInMs  +  0  )  /  10;

    //
    pLayout->mixInfo.nTimes_mix  ++  ;

    //
    //LOGD("my_preMix: nTimes_mix_need %d, layout->nTimes_mix %d, tickCnt_start %d",  nTimes_mix_need,  pLayout->nTimes_mix,  (int)pLayout->tickCnt_startToMix);

    //
    if  (  pLayout->mixInfo.nTimes_mix  <  nTimes_mix_need)  {
        //
        //LOGD("my_preMix: nTimes_mix %d, layout->nTimes_mix %d",  nTimes_mix_need,  pLayout->nTimes_mix);
        //
        return  0;
    }

    //
    CTmpQMgr  *  pMgr  =  &g_tmpQMgr;
    int  i;

    iDiffInMs  =  tickCnt  -  pLayout->mixInfo.tickCnt_last_adjustQNodes_mix;
    if  (  abs(iDiffInMs)  >  100) {
        pLayout->mixInfo.tickCnt_last_adjustQNodes_mix  =  tickCnt;
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
            pLayout->mixInfo.tickCnt_startToMix  -=  10;
            //
            LOGD("my_preMix: qNodes too big, reduce tickCnt_startToMix, to adjust qNodes");
        }
    }

    //
    mySleep(10);

    //
    return  0;
}




//
int my_outStream(void* p0, void* p1, void* p2)
{
    int  iErr = -1;
    AEC_layout   *  pLayout  =  (  AEC_layout  *  )p0;
    char* buf_o = (char*)p1;
    int size = (int)(long long)p2;

    //
    CTmpQMgr * pMgr  =  &g_tmpQMgr;

    //
    int     tickCnt  = myGetTickCount(mynull);
    int  iDiffInMs  =  tickCnt  -  pLayout->mixInfo.tickCnt_lastMixed;
    pLayout->mixInfo.tickCnt_lastMixed  =  tickCnt;
    if  (  abs(iDiffInMs)  <=  2  )  {
        pLayout->mixInfo.nTimes_mix_err  ++  ;
    }
    else  {
          pLayout->mixInfo.nTimes_mix_err  =  0;
    }
    //
    if  (  1  )  {
    }
    //
    if  (  pLayout->mixInfo.nTimes_mix_err  >  25  )  {
        LOGE("my_outStream: nTimes_mix_err>5, set stop to true");
        pLayout->tmpMixer.bStop  =  true;
    }

    //
    //LOGD("my_outStream: nTimes_mix %d, iDiffInMs %d, nTimes_mix_err %d",  pLayout->nTimes_mix,  iDiffInMs,  pLayout->nTimes_mix_err);

    //
    do {
        //
        if  (  pLayout->bDbg1) {
            if (!(pLayout->mixInfo.nTimes_mix % 500)) {
                //
                //LOGD("my_outStream: nTimes_mix %d",  pLayout->nTimes_mix);
                //
                char buf[256];
                //snprintf(buf, mycountof(buf),"my_outStream: nTimes_mix %d",  pLayout->nTimes_mix);
                //
                snprintf(buf, mycountof(buf), "aecPlayQ %d, aecQ %d, recQ %d",
                         pMgr->m_tmpRec.aecPlayQ.uiQNodes, pMgr->m_tmpRec.aecThreadQ.uiQNodes,
                         pMgr->m_tmpRec.recQ.uiQNodes);
                LOGD("%s", buf);
                int i;
                buf[0] = 0;
                for (i = 0; i < pMgr->m_usMaxCnt; i++) {
                    snprintf(buf, mycountof(buf), "%smem[%d] %d, ", buf, i,
                             pMgr->mems[i].q.uiQNodes);
                }
                //
                LOGD("%s", buf);

            }
        }

        //
        if (0) {
            printf("my_outStream: %dbytes", size);

            //
            char fileName[256];
            snprintf(fileName, mycountof(fileName), "/sdcard/Download/kkmixer.pcm");
            FILE *fp = fopen(fileName, "ab");
            if (fp) {
                fwrite(buf_o, size, 1, fp);
                fclose(fp);
            }
        }


        int  tmpiRet;

#if  0
        tmpiRet  = nsObj_do(pMgr->m_aecLayout.pNsObj,preBuf,size);
        if  (  tmpiRet  )  {
            LOGE("my_outStream failed, nsObj_do failed");
            break;
        }
#endif


#if 0
        tmpiRet  = aec3Obj_doAec(pMgr->m_aecLayout.pAecObj,buf_o,size,preBuf,size);
        if  (  tmpiRet  )  {
            LOGE("my_outStream failed, aec3Obj_doAec failed");
            break;
        }
#endif

        //
        int uiPts  =  pLayout->mixInfo.nTimes_mix;

        //
        TmpAPkt pkt = {0};
        int pktSize;

        //
        TmpQMem *pMem = getTmpQMem(mynull, 0);
        if (!pMem) break;
        //
        memset(  &pkt, 0, offsetof(TmpAPkt,buf));

        //
        pkt.uiPts  =  uiPts;

        //
        if (size > sizeof(pkt.buf)) {
                LOGE("my_outStream failed, size > sizeof(buf) ");
                break;
        }
        memcpy(  pkt.buf,buf_o, size);
        pkt.len  =  size;
        pktSize  = offsetof(TmpAPkt,buf)  +  pkt.len;
        tmpiRet  =  qPostMsg(&pkt, pktSize,&pMem->q, (TCHAR*)_T( ""  )  );
        if  (  tmpiRet  )  {
            //
            int  tmp_nQNodes  =  pMem->q.uiQNodes;
            if  (  g_bPrintErr) {
                LOGE("my_outStream failed, qPostMsg failed, tmpiRet %d. nQNodes %d, dst.index_tmpQMem %d",
                     tmpiRet, tmp_nQNodes, pMem->index_tmpQMem);
            }
            //
            break;
        }

        //
        if  (  pLayout->bDbg1  )  {
            //
            LOGD("my_outStream:qPostMsg, index_tmpQMem %d, nQNodes %d. l306",  pMem->index_tmpQMem,  pMem->q.uiQNodes);

        }



        //
    } while ( false );


    //
    iErr = 0;
    //
    return  iErr;

}


//
void *mcThreadProc_mix(void*data)
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
    LOGD("thread_mix: starts");

    //
    //int iDiffInMs  =  myGetTickCount(mynull)  -  counter->tickCnt0;
    //PF_inStream4  pf = my_inStream4_toMix;
    //void  *  pObj  = mynull;
    int nSources  =  MAX_tmpQMems  -  1;//
    bool  tmp_bErr  =  false;

    //
    pLayout->status.nStep_mix  =  1;

    //
    do {

        //
        int  sampleRate  =  16000;

        //
        //int  nSources = MAX_fps;
        void* p0 = pLayout;
        //
        PF_inStream4 pf = my_inStream4_toMix;
        PF_preMix pf_preMix = my_preMix;
        PF_outStream  pf_o = my_outStream;

        //
        for  (  ;  ;  ) {
            //
            if  (  pLayout->bQuit)  {

                LOGD("mix: bQuit is true, break");

                break;
            }

            //
            pLayout->mixInfo.tickCnt_startToMix = myGetTickCount(mynull);
            pLayout->mixInfo.nTimes_mix = 0;

            //
            pLayout->status.nStep_mix = 10;

            //
            LOGD("mix: before mixer_main");

            //
            mixer_main(&pLayout->tmpMixer, nSources, pf, p0, pf_preMix, p0, pf_o, p0);

            //
            LOGD("mix: after mixer_main");

            //
            if  (  pLayout->tmpMixer.bStop)  {
                //
                LOGD("mix: bStop is true, reset it ");
                //
                pLayout->tmpMixer.bStop  = false;
            }

            //
            continue;
        }


        //
        iErr  =  0;

    } while  ( false  );


    //
    LOGD("thread_mix: ends");

    //
    pLayout->status.nStep_mix  =  100;

    //
    return  NULL;
}


