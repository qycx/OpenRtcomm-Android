

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

#include <string.h>
#include <string>

//
#include    "myMelpPublic.h"
#include    "tmpFfmpegCommon.h"
#include "genericqueue.h"


//
void *mcThreadProc_encA_melp(void*data)
{
    int index_qdcUnit  = ( int)(long)data;
    QDC_unit *pUnit = getQdcUnitByIndex(index_qdcUnit);
    if (!pUnit) return mynull;
    EncAMelp  * pEncObj =  (EncAMelp*)pUnit->pTransObj;
    if  (  !pEncObj)  return  mynull;


    do  {
        static char* enc_cmd_line[] = { "melpe", "/storage/emulated/0/Download/250710.pcm", "/storage/emulated/0/Download/kk1_24.raw", 0 };
        int argc = 3;

        //
        melp_enc_main(argc,enc_cmd_line,pEncObj);


    }  while(false);



    //
    return  mynull;

}


//
int encA_init_melp(int index_qdcUnit, mybyte ucCompressors, short wFormatTag, int bitrate, mybool bDbg) {
    int iErr = -1;
    QDC_unit *pUnit = getQdcUnitByIndex(index_qdcUnit);
    if (!pUnit) return -1;
    int i, j, k, ret;

    //AVCodecID codec_id = AV_CODEC_ID_MP3;

    if (!g_qdc.bDone_qdc_init) return -1;

    //
    pUnit->encA_var.wFormatTag = wFormatTag;
    switch (wFormatTag) {
        case WAVE_FORMAT_MELP:
            break;
        default:
            return -1;
    }

    //
    LOGD("encA_init_melp: bitrate %d", bitrate);

    do {
        //
        int size = sizeof(EncAMelp);
        EncAMelp *pEncObj = mynull;

        pEncObj = (EncAMelp *) mymalloc(size);
        if (!pEncObj)  break;

        memset(pEncObj, 0, size);
        //
        pUnit->pTransObj =  pEncObj;

        //
        TransMelp  * pTransObj = &pEncObj->common;

        //
        pEncObj->common.io_bUse_q=true;

        //
        if  (  1  ) {
            pEncObj->common.bUse_dump = true;
            LOGD("encA_init_melp: bUse_dump set to true");

            //
            if  (  pTransObj->bUse_dump  )  {
                LOGD("Note: tmp_fopen_r, dump open");

                //
                //"/storage/emulated/0/Download/250710.pcm"
                _snprintf(pTransObj->enc_dump_name,mycountof(pTransObj->enc_dump_name),"%s", "/storage/emulated/0/Download/enc_dump.melp"  );
                pTransObj->fp_enc_dump  = fopen(pTransObj->enc_dump_name,"wb");
                if  (  !pTransObj->fp_enc_dump)  {
                    LOGE("open enc dump file failed");
                    break;
                }

                //
                _snprintf(pTransObj->pcm_dump_name_encA_data_melp,mycountof(pTransObj->pcm_dump_name_encA_data_melp),"%s", "/storage/emulated/0/Download/encA_dump8k.pcm"  );
                pTransObj->fp_pcm_dump_encA_data_melp  = fopen(pTransObj->pcm_dump_name_encA_data_melp,"wb");
                if  (  !pTransObj->fp_pcm_dump_encA_data_melp)  {
                    LOGE("open pcm dump encA file failed");
                    break;
                }

                //
                _snprintf(pTransObj->pcm_dump_name_tmpRead,mycountof(pTransObj->pcm_dump_name_tmpRead),"%s", "/storage/emulated/0/Download/tmpRead_dump8k.pcm"  );
                pTransObj->fp_pcm_dump_tmpRead  = fopen(pTransObj->pcm_dump_name_tmpRead,"wb");
                if  (  !pTransObj->fp_pcm_dump_tmpRead)  {
                    LOGE("open pcm dump tmpRead file failed");
                    break;
                }

                //
                _snprintf(pTransObj->enc_dump_name_tmpWrite,mycountof(pTransObj->enc_dump_name_tmpWrite),"%s", "/storage/emulated/0/Download/tmpWrite_dump8k.melp"  );
                pTransObj->fp_enc_dump_tmpWrite  = fopen(pTransObj->enc_dump_name_tmpWrite,"wb");
                if  (  !pTransObj->fp_enc_dump_tmpWrite)  {
                    LOGE("open dump tmpWrite file failed");
                    break;
                }


            }

        }

        //
        TCHAR  tBuf[128];
        _sntprintf(tBuf,mycountof(tBuf),  _T("encA.%d.q_in"),index_qdcUnit);
        //
        unsigned int uiMaxQNodes  =  20;        //  10ms一个. 20个就是200ms
        //
        GENERIC_Q *p_q_in=genericQNew(tBuf,tBuf,0,uiMaxQNodes,mymalloc, myfree);
        if  (  !p_q_in) {
            break;
        }
        pEncObj->common.p_q_in=p_q_in;

        //
        _sntprintf(tBuf,mycountof(tBuf),  _T("encA.%d.q_out"),index_qdcUnit);
        GENERIC_Q *p_q_out=genericQNew(tBuf,tBuf,0,uiMaxQNodes,mymalloc, myfree);
        if  (  !p_q_out) {
            break;
        }
        pEncObj->common.p_q_out=p_q_out;



        //
        pthread_t tid;
        if  (  pthread_create(&tid,mynull,mcThreadProc_encA_melp,(void*)index_qdcUnit)  )  {
            break;
        }
        pEncObj->tid=tid;

        //
        iErr  =  0;


    } while ( false  );


errLabel:

    //
    if  (  iErr )  {
        encA_exit_melp(index_qdcUnit,bDbg);
    }

    //
    return  iErr;
}


int encA_exit_melp(int index_qdcUnit,mybool bDbg)
{
    int iErr = -1;
    QDC_unit *pUnit = getQdcUnitByIndex(index_qdcUnit);
    if (!pUnit) return -1;

    //
    EncAMelp  * pEncObj  =  (EncAMelp*)pUnit->pTransObj;
    if  ( pEncObj)  {
        //
        pEncObj->common.bQuit=true;
        //
        if  (  pEncObj->tid  )  {
            pthread_join(pEncObj->tid,mynull);
            pEncObj->tid=0;
            //
            LOGD("encA_exit_melp: tid waited");
        }
        //
        if  (  pEncObj->common.p_q_in)  {
            genericQFree((GENERIC_Q *)pEncObj->common.p_q_in);
            pEncObj->common.p_q_in=mynull;
        }
        if  (  pEncObj->common.p_q_out)  {
            genericQFree((GENERIC_Q*)pEncObj->common.p_q_out);
            pEncObj->common.p_q_out=mynull;
        }

        //
        if  (  pEncObj->common.fp_enc_dump)  {
            fclose(pEncObj->common.fp_enc_dump);
            pEncObj->common.fp_enc_dump=mynull;
        }
        if  (  pEncObj->common.fp_pcm_dump_encA_data_melp)  {
            fclose(pEncObj->common.fp_pcm_dump_encA_data_melp);
            pEncObj->common.fp_pcm_dump_encA_data_melp=mynull;
        }
        if  (  pEncObj->common.fp_pcm_dump_tmpRead)  {
            fclose(pEncObj->common.fp_pcm_dump_tmpRead);
            pEncObj->common.fp_pcm_dump_tmpRead=mynull;
        }
        if  (  pEncObj->common.fp_enc_dump_tmpWrite)  {
            fclose(pEncObj->common.fp_enc_dump_tmpWrite);
            pEncObj->common.fp_enc_dump_tmpWrite=mynull;
        }

        //
        MACRO_safeFree(pUnit->pTransObj);
    }


    //
    if(pUnit->encA_var.cache.pBuf){
        myfree(pUnit->encA_var.cache.pBuf);
        pUnit->encA_var.cache.pBuf=NULL;
    }


    iErr = 0;
    errLabel:

    return iErr;
}


//
int encA_data_melp(int index_qdcUnit,byte * pcm,int pcmLen,byte * raw,int *pLen_raw, mybool bDbg)
{
    int  iErr  =  -1;

    QDC_unit *pUnit = getQdcUnitByIndex(index_qdcUnit);
    if (!pUnit) return -1;

    //
    EncAMelp  * pEncObj  =  (EncAMelp*)pUnit->pTransObj;
    if  ( !pEncObj)  return  -1;

    if  (  !pcm  ||  !pcmLen )  {
        LOGD("encA_data_melp failed, pcm is null or pcmLen is 0");
        return -1;
    }
    if  (  !raw  ||  !pLen_raw ||  !*pLen_raw  )  {
        LOGD("encA_data_melp failed, raw is null or pLen_raw is null, or *pLen_raw is 0");
        return  -1;
    }

    //
    do {
        // 输入是16k的。要转成8k
        char  buf_8k[128000];
        int  nBytes_output  =  0;
        //
        if  (  sizeof(buf_8k)<pcmLen/2)  {
            LOGE("encA_data_melp: buf too small. l203");
            return  -1;
        }


        //
        do16kTo8k((char*)pcm,pcmLen,buf_8k,&nBytes_output);
        pcm =  (byte*)buf_8k;
        pcmLen=nBytes_output;

        //
        if  (  0  ) {
            if (pEncObj->common.fp_pcm_dump_encA_data_melp) {
                fwrite(pcm, pcmLen, 1, pEncObj->common.fp_pcm_dump_encA_data_melp);
            }
        }


        //
        MelpAPkt  pkt  =  {0};
        if  (  pcmLen > mycountof(pkt.buf))  {
            LOGE("encA_data_melp failed: pcmLen > sizeof(pkt.buf)");
            break;
        }
        pkt.len  =  pcmLen;
        memcpy(pkt.buf,pcm,pcmLen);

        //
        int  pktSize  = offsetof(MelpAPkt,buf)  +  pkt.len;
        int  tmpiRet;
        GENERIC_Q  *  p_q_in  =  (  GENERIC_Q*)pEncObj->common.p_q_in;
        tmpiRet  =  qPostMsg(&pkt, pktSize,p_q_in, _T(""  ) );
        if  (  tmpiRet  )  {
            LOGE("encA_data_melp:qPostMsg failed. tmpiRet %d",  tmpiRet);
            break;
        }
        if  (  0  ) {
            char buf[128];
            _snprintf(buf, mycountof(buf),
                      ("encA_data_melp: post pkt to q_in ok, pcmLen %d, uiQNodes %d"), pcmLen,
                      p_q_in->uiQNodes);
            LOGD("%s", buf);
        }


        //  这里要从p_q_out里取包，然后填入raw里
        int  nGot  =  0;




        //
        if(pLen_raw){
            *pLen_raw=nGot;
        }


        //
        iErr  =  0;
    } while(false);

    //
    return  iErr;
}

//
 int melp_encA_getOutputByIndex(int index_qdcUnit,int index, byte *buf, int *piBufLen, int *piPts,int *piSampleTimeInMs, mybool bDbg)
{
    int  iErr  =  -1;

    QDC_unit *pUnit = getQdcUnitByIndex(index_qdcUnit);
    if (!pUnit) return -1;

    //
    EncAMelp  * pEncObj  =  (EncAMelp*)pUnit->pTransObj;
    if  ( !pEncObj)  return  -1;


    //
    do {
        MelpAPkt  pkt={0};
        GENERIC_Q  * p_q_out  =  (GENERIC_Q*)pEncObj->common.p_q_out;

        unsigned  int uiLen;
        uiLen  =  sizeof(pkt);
        if  (qGetMsg(p_q_out,&pkt,&uiLen))  {
            break;
        }

        int iBufSize =  piBufLen[0];
        if  (  iBufSize<pkt.len)  {
            LOGE("melp_encA_getOutputByIndex failed, iBufSize < pkt.len");
            break;
        }
        memcpy(buf,pkt.buf,pkt.len);
        *piBufLen=pkt.len;
        *piPts=pkt.pts;
        *piSampleTimeInMs=pkt.uiSampleTimeInMs;


        //
        iErr  =  0;
    } while(false);

    //
    return  iErr;
}



