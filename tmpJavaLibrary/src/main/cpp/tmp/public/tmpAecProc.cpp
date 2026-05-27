
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
void *mcThreadProc_mix(void*data);
void *mcThreadProc_aec(void*data);
void *mcThreadProc_ap(void*data);


//
extern "C"  int doTest_5();


//
int AEC_layout_init(int  iTaskId, unsigned  short  usCntLimit_activeMems_from, bool  bUse_audioMixer,  AEC_layout* pLayout)
{
    int  iErr = -1;

    //
    if (!pLayout)return  -1;
    if (iTaskId == 0)return  -1;
    //if (usCntLimit_activeMems_from > mycountof(pLayout->mems)) return  -1;

    //
    memset(pLayout, 0, sizeof(AEC_layout));
    pLayout->iTaskId = iTaskId;
    //pLayout->usCntLimit_activeMems_from = usCntLimit_activeMems_from;
    pLayout->usCntLimit_activeMems_from  =  MAX_tmpQMems  -  1;

    //
    pLayout->bUse_audioMixer=bUse_audioMixer;

    //
    pLayout->bDbg1  =  false;

    //
    int  tmpiRet;
    //tmpiRet  =  doTest_5();


    //
    do {
        //
        if  (  !pLayout->bUse_audioMixer)  {
            break;
        }

        pthread_t tid1;

        if  (  pthread_create(&tid1, NULL, mcThreadProc_mix, pLayout)  )  break;
        pLayout->tid_mix  =  tid1;
        LOGD("AEC_layout_init: mix thread created, tid %d",  (int)tid1);

        //
        if  (  0  ) {
            //
#if 0
            if (pthread_create(&tid1, NULL, mcThreadProc_aec, pLayout)) break;
#endif
            //
        }
        else  {
            if (pthread_create(&tid1, NULL, mcThreadProc_ap, pLayout)) break;
        }
        pLayout->tid_aec  =  tid1;
        LOGD("AEC_layout_init: aec thread created, tid %d",  (int)tid1);


        //
        iErr = 0;
    }  while (  false );

    if (iErr) {
        AEC_layout_exit(pLayout);
    }


    //
    return  iErr;
}

//
int AEC_layout_exit(AEC_layout* pLayout)
{
    //
    pLayout->bQuit=true;
    LOGD("AEC_layout_exit: pLayout.bQuit set to true");

    //
    pLayout->tmpMixer.bQuit  =  true;
    pLayout->tmpAp.bQuit  =  true;

    //
    int  tmpiRet;

    if  (  pLayout->tid_aec) {
        tmpiRet = pthread_join(pLayout->tid_aec, mynull);
        LOGD("AEC_layout_exit: aec thread join, tid %d", (int) pLayout->tid_aec);
        pLayout->tid_aec  =  0;
    }


    //
    if  (  pLayout->tid_mix) {
        tmpiRet = pthread_join(pLayout->tid_mix, mynull);
        LOGD("AEC_layout_exit: mix thread join, tid %d", (int) pLayout->tid_mix);
        pLayout->tid_mix  =  0;
    }

    //
    memset(pLayout, 0, sizeof(AEC_layout));

    return  0;
}



//
int tmp_getPreAecData(GENERIC_Q   *  pQ,  TmpCache   *  pCache,  char  *  buf_o,  int size)
{
    //int  nRead = 0;
    int  iErr = -1;
    //
    CTmpQMgr * pMgr  =  &g_tmpQMgr;


    //
    AEC_layout* pAecLayout = &pMgr->m_aecLayout;
    int wBits = 16;

    TmpCache   *  pMem  =  pCache;//&pMgr->m_tmpRec.cache;

    do {
        //

        //
        if (pMem->startIndex) {
            memcpy(pMem->buf, pMem->buf + pMem->startIndex, pMem->nLeft);
            pMem->startIndex = 0;
        }

        //
        TmpAPkt pkt;

        //
        for (;;) {
            if (pMem->nLeft >= size)break;
            //
            unsigned int len = sizeof(pkt);
            if (qGetMsg(pQ, &pkt, &len)) {
                break;
            }

            //
            if  (  1  )  {
                //LOGD("getPreAecData: [%S]: getMsg: pts %d,  len %d",  pQ->cfg.name,  pkt.uiPts,  pkt.len  );
            }

            //
            len = pkt.len;
            if (sizeof(pMem->buf) - (pMem->startIndex + pMem->nLeft) < len) {
                LOGE("tmp_getPreAecData: too big audio data, skipped");
                break;
            }
            memcpy(pMem->buf + pMem->startIndex + pMem->nLeft, pkt.buf, len);
            pMem->nLeft += len;
            //
            continue;
        }

        if (pMem->nLeft < size) {
            //  填空
            break;
        }

        //
        memcpy(buf_o, pMem->buf + pMem->startIndex, size);
        pMem->startIndex += size;
        pMem->nLeft -= size;
        //
        //nRead = size;


        //
        iErr = 0;

    }  while ( false  );


    //errLabel:

    if (iErr) {
        //
        //nRead = 0;
    }

    //
    return  iErr;
}


//
#if  0
void *iimcThreadProc_aec(void*data)
{
    pthread_t  tid;
    AEC_layout  *pLayout;
    //unsigned int i;

    tid = pthread_self();
    //counter = (t_counter*)data;
    //t_m  *  pm  =  (t_m*)data;
    //counter  =  pm->pcounter;
    pLayout  =  (AEC_layout*)data;

    //
    CTmpQMgr  *  pMgr  =  &g_tmpQMgr;

    //
    LOGD("thread_aec: starts");

    //
    //int iDiffInMs  =  myGetTickCount(mynull)  -  counter->tickCnt0;
    //PF_inStream4  pf = my_inStream4_toMix;
    //void  *  pObj  = mynull;
    int nSources  =  MAX_tmpQMems  -  1;//
    bool  tmp_bErr  =  false;

    //
    void  *  aecmInst  =  mynull;
    void  *  nsxInst  =  mynull;
    void  *  agcmInst  =  mynull;



    const int frameSize = 160;  // 10ms@16kHz
    int16_t* inBuffer = new int16_t[frameSize];
    int16_t* outBuffer = new int16_t[frameSize];

    //
    do {

        //
        int  sampleRate  =  16000;
        if  (initNsObj(sampleRate,&pLayout->pNsObj))  {
            LOGE("mcThreadProc_mix: initNsObj failed");
            break;
        }

        //
        aecmInst  =  myWebRtcAecm_Create();
        if  (  !aecmInst)  {
            break;
        }
        int  tmpiRet;
        tmpiRet  = myWebRtcAecm_Init(aecmInst,16000);
        if  (  tmpiRet)  {
            break;
        }
        WebRtcAecmConfigWrapper config;
        config.cngMode  =  1;
        config.echoMode  =  4;
        tmpiRet  = myWebRtcAecm_set_config(aecmInst,&config);
        if  (  tmpiRet  )  {
            break;
        }

        agcmInst = WebRTC_AGC_Create();

        if  (  !agcmInst)  {
            break;
        }

        int minLevel = 0;
        int maxLevel = 255;
        int agcMode  = 3;
        tmpiRet = WebRTC_AGC_Init(agcmInst, minLevel, maxLevel, agcMode, sampleRate);
        if  (  tmpiRet)  {
            break;
        }

        WebRtcAgcConfigWrapper agcConfig;
        agcConfig.compressionGaindB = 9;
        agcConfig.limiterEnable = 1;
        agcConfig.targetLevelDbfs = 3;

        tmpiRet  = WebRTC_AGC_SetConfig(agcmInst,&agcConfig);
        if  (  tmpiRet  )  {
            break;
        }

        int outMicLevel = 0;

        //
#if  0
        nsxInst  =  WebRTC_NSX_Create();
        if  (  !nsxInst)  {
            break;
        }
        int nsResult = WebRTC_NSX_Init(nsxInst, 16000);
        if  (  nsResult)  {
            break;
        }
        /**
     * 设置NSX策略
     * @param nsxHandler NSX实例句柄
     * @param mode 噪声抑制级别（0-3）：
     *             0 - 温和
     *             1 - 轻度
     *             2 - 中度
     *             3 - 激进
     * @return 0表示成功，其他值表示失败
     */

        int mode  =  2;
        tmpiRet  =  WebRTC_NSX_SetPolicy(nsxInst,2);
        if  (  tmpiRet)  {
            break;
        }
#endif

        //
        //int  nSources = MAX_fps;
        void* p0 = pLayout;
        int  loopCtrl  =  0;
        long tickCnt_start  = myGetTickCount(mynull);

        //
        long  tickCnt_beforeAec=  0;
        long tickCnt_afterAec  =  0;
        bool  bTest  =  false;//true;

        //
        for  (  ;  ;  )  {

            if  (  pLayout->bQuit)  break;

            loopCtrl  ++  ;
            long tickCnt  = myGetTickCount(mynull);
            int  iDiffInMs  =  tickCnt  -  tickCnt_start;
            int  nTimes_elapse  =  iDiffInMs  /  10;
            //
            bool  bSleep  =  false;
            //
            if  (  loopCtrl  >  nTimes_elapse)  {
                mySleep(10);
                //
                bSleep  =  true;
            }

            //
            GENERIC_Q   *  pQ  =  &pMgr->m_tmpRec.aecThreadQ;
            if  (  !(  loopCtrl  %  2  )) {
                //
                bool  bExists  = false;
                if (pQ->uiQNodes > pQ->cfg.uiMaxQNodes / 2)
                {
                        bExists  =  true;
                }
                if  (  bExists)  {
                    //  把tickCnt_startToMix提前，就可以加快处理
                    tickCnt_start  -=  10;
                    //
                    LOGD("aec: qNodes %d too big, reduce tickCnt_start, to adjust qNodes, loopCtrl %d, nTimes_elapse %d, tickCnt_start %d, tickCnt %d",
                         pQ->uiQNodes,  loopCtrl,nTimes_elapse,tickCnt_start,  tickCnt);
                }
            }
            //


            //
            if  (  bTest) {
                tickCnt_beforeAec = myGetTickCount(mynull);
            }

            //
            int  size  =  160  *  2;

            //
            char  recBuf[4096];
            if  (tmp_getPreAecData(&pMgr->m_tmpRec.aecThreadQ,  &pMgr->m_tmpRec.aecThreadCache,  recBuf,size))  {
                //
                if  (  0  ) {
                    LOGD("aec: get aecThreadQ failed, continue");
                }
                //
                continue;
            }
            //
            if  (  0  ) {
                LOGD("aec: get rcdBuf %dbytes, loopCtrl %d, nTimes_elapse %d", size, loopCtrl,
                     nTimes_elapse);
            }

            //
            char  aecPlayBuf[4096];
            if  (tmp_getPreAecData(&pMgr->m_tmpRec.aecPlayQ,&pMgr->m_tmpRec.aecPlayCache,aecPlayBuf,size))  {
                memset(aecPlayBuf,0,size);
                //
                LOGD("aec: get aecPlayQ failed, memset 0. l406");
            }


            //
            //aec3Obj_doAec(pLayout->pAecObj,aecPlayBuf,size,recBuf,size);

            int num_sam  =  size /  2;

            myWebRtcAecm_BufferFarend(aecmInst,(int16_t *)aecPlayBuf, size /  2);

            myWebRtcAecm_Process(aecmInst,(int16_t *)recBuf,mynull,(int16_t *)recBuf,num_sam,50);


            //
            nsObj_do(pLayout->pNsObj,recBuf,size);

            //
            //WebRTC_NSX_Process(nsxInst,(int16_t *)recBuf,1,(int16_t *)recBuf);

            //  agc
            int currentLevel = 10;


            for (int i = 0; i < frameSize; i++) {
                inBuffer[i] = static_cast<int16_t>(
                        (static_cast<uint8_t>(recBuf[i * 2 + 1]) << 8) |
                        static_cast<uint8_t>(recBuf[i * 2])
                );
            }


            int addMicResult = WebRTC_AGC_AddMic(agcmInst, (int16_t *)inBuffer, 1,
                                                 frameSize);
            if (addMicResult != 0) {
                printf("failed in WebRtcAgc_Process WebRtcAgc_Process\n");
            } else {
                //printf("success in WebRtcAgc_Process WebRtcAgc_Process\n");


               //int virtualMicResult = WebRTC_AGC_VirtualMic(agcmInst, (int16_t *)recBuf, 1,
               //                                             size, currentLevel, &outMicLevel);
               //if (virtualMicResult != 0) {
               //    printf("failed in WebRtcAgc_Process WebRTC_AGC_VirtualMic\n");
               //    break;
               //}


                uint8_t saturationWarning;
                int inMicLevel  = 120;
                //int inMicLevel  = outMicLevel;

                int nAgcRet = WebRTC_AGC_Process(agcmInst, (int16_t *)inBuffer, 1,
                                                 frameSize, (int16_t *)outBuffer, inMicLevel,
                                                 &outMicLevel, 0, &saturationWarning);
                if (nAgcRet != 0) {
                    printf("failed in WebRtcAgc_Process\n");
                } else {
                    //printf("success in WebRtcAgc_Process\n");

                    for (int i = 0; i < frameSize; i++) {
                        recBuf[i * 2] = static_cast<char>(outBuffer[i] & 0xFF);
                        recBuf[i * 2 + 1] = static_cast<char>((outBuffer[i] >> 8) & 0xFF);
                    }

                }
            }

            //
            TmpAPkt  pkt;
            memset(&pkt,0, offsetof(TmpAPkt,buf));
            pkt.iSampleTimeInMs  =  loopCtrl  *  10;
            pkt.uiPts  =  loopCtrl;
            pkt.len  = size;
            memcpy(  pkt.buf, recBuf,size);

            int  pktSize  = offsetof(TmpAPkt,buf)  +  pkt.len;
            int  tmpiRet;
            tmpiRet  =  qPostMsg(&pkt, pktSize,&pMgr->m_tmpRec.recQ, _T(""  ) );
            if  (  tmpiRet  )  {
                LOGE("aec:qPostMsg failed");

            }

            //
            if  (  bTest)  {
                tickCnt_afterAec  = myGetTickCount(mynull);
                //
                iDiffInMs  =  tickCnt_afterAec  -  tickCnt_beforeAec;
                LOGD("aec: costs %dms. aecThreadQ.nQNodes %d. bSleep %d, loopCtrl %d, nTimes_elapse %d, tickCnt_start %d, tickCnt %d",
                     iDiffInMs,  pMgr->m_tmpRec.aecThreadQ.uiQNodes, bSleep,  loopCtrl,  nTimes_elapse,  tickCnt_start,  tickCnt);
            }



            //
            continue;

        }




    } while  ( false  );

    //
#if  0
    if  (  nsxInst)  {
        WebRTC_NSX_Free(nsxInst);
    }
#endif

    //
    if  (  aecmInst)  {
        myWebRtcAecm_Free(aecmInst);
    }

    if  (  agcmInst)  {
        WebRTC_AGC_Free(agcmInst);
    }

    if(inBuffer != nullptr) {
        delete []inBuffer;
    }

    if(outBuffer != nullptr) {
        delete []outBuffer;
    }

    //
    if  (  pLayout->pNsObj)  {
        exitNsObj(&pLayout->pNsObj);
    }
    //
    if  (  pLayout->pAecObj)  {
    }

    //
    LOGD("thread_aec: ends");

    //
    return  NULL;
}
#endif

