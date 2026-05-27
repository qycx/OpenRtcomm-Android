

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

#include <string.h>
#include <string>

//
#include    "myMelpPublic.h"
#include    "tmpFfmpegCommon.h"
#include "genericqueue.h"

void *mcThreadProc_decA_melp(void*data)
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




int decA_init_melp(int index_qdcUnit, byte ucCompressors, short wFormatTag, mybool bDbg) {
    int iErr = -1;
    QDC_unit *pUnit = getQdcUnitByIndex(index_qdcUnit);
    if (!pUnit) return -1;

    //AVCodecID codec_id = AV_CODEC_ID_MP3;

    if (!g_qdc.bDone_qdc_init) return -1;


    //
    //LOGD("decA_init_melp: bitrate %d", bitrate);

    do {
        //
        int size = sizeof(DecAMelp);
        DecAMelp *pDecObj = mynull;

        pDecObj = (DecAMelp *) mymalloc(size);
        if (!pDecObj)  break;

        memset(pDecObj, 0, size);
        //
        pUnit->pTransObj =  pDecObj;

        //
        TransMelp  * pTransObj = &pDecObj->common;

        //
        pDecObj->common.io_bUse_q=true;

        //
        if  (  1  ) {
            pDecObj->common.bUse_dump = true;
            LOGD("encA_init_melp: bUse_dump set to true");

            //
            if  (  pTransObj->bUse_dump  )  {
                LOGD("Note: tmp_fopen_r, dump open");

                //
                //"/storage/emulated/0/Download/250710.pcm"
                _snprintf(pTransObj->enc_dump_name,mycountof(pTransObj->enc_dump_name),"%s", "/storage/emulated/0/Download/decA.enc_dump.melp"  );
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

                //
                _snprintf(pTransObj->decA_dumpEncData_name,mycountof(pTransObj->decA_dumpEncData_name),"%s", "/storage/emulated/0/Download/decA_dumpEncData.melp"  );
                pTransObj->fp_decA_dumpEncData  = fopen(pTransObj->decA_dumpEncData_name,"wb");
                if  (  !pTransObj->fp_decA_dumpEncData)  {
                    LOGE("open decA_dumpEncData file failed");
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
        pDecObj->common.p_q_in=p_q_in;

        //
        _sntprintf(tBuf,mycountof(tBuf),  _T("encA.%d.q_out"),index_qdcUnit);
        GENERIC_Q *p_q_out=genericQNew(tBuf,tBuf,0,uiMaxQNodes,mymalloc, myfree);
        if  (  !p_q_out) {
            break;
        }
        pDecObj->common.p_q_out=p_q_out;



        //
        pthread_t tid;
        if  (  pthread_create(&tid,mynull, mcThreadProc_decA_melp,(void*)index_qdcUnit)  )  {
            break;
        }
        pDecObj->tid=tid;

        //
        iErr  =  0;


    } while ( false  );


    errLabel:

    //
    if  (  iErr )  {
        decA_exit_melp(index_qdcUnit,bDbg);
    }


    return iErr;

}

int decA_exit_melp(int index_qdcUnit,mybool bDbg) {
    int iErr = -1;
    QDC_unit *pUnit = getQdcUnitByIndex(index_qdcUnit);
    if (!pUnit) return -1;


    //
    DecAMelp * pDecObj  =  (DecAMelp *)pUnit->pTransObj;
    if  ( pDecObj)  {
        //
        pDecObj->common.bQuit=true;
        //
        if  (  pDecObj->tid  )  {
            pthread_join(pDecObj->tid,mynull);
            pDecObj->tid=0;
            //
            LOGD("decA_exit_melp: tid waited");
        }
        //
        if  (  pDecObj->common.p_q_in)  {
            genericQFree((GENERIC_Q *)pDecObj->common.p_q_in);
            pDecObj->common.p_q_in=mynull;
        }
        if  (  pDecObj->common.p_q_out)  {
            genericQFree((GENERIC_Q*)pDecObj->common.p_q_out);
            pDecObj->common.p_q_out=mynull;
        }

        //
        if  (  pDecObj->common.fp_enc_dump)  {
            fclose(pDecObj->common.fp_enc_dump);
            pDecObj->common.fp_enc_dump=mynull;
        }
        if  (  pDecObj->common.fp_pcm_dump_encA_data_melp)  {
            fclose(pDecObj->common.fp_pcm_dump_encA_data_melp);
            pDecObj->common.fp_pcm_dump_encA_data_melp=mynull;
        }
        if  (  pDecObj->common.fp_pcm_dump_tmpRead)  {
            fclose(pDecObj->common.fp_pcm_dump_tmpRead);
            pDecObj->common.fp_pcm_dump_tmpRead=mynull;
        }
        if  (  pDecObj->common.fp_enc_dump_tmpWrite)  {
            fclose(pDecObj->common.fp_enc_dump_tmpWrite);
            pDecObj->common.fp_enc_dump_tmpWrite=mynull;
        }

        //
        if  (  pDecObj->common.fp_decA_dumpEncData)  {
            fclose(pDecObj->common.fp_decA_dumpEncData);
            pDecObj->common.fp_decA_dumpEncData  =  mynull;
        }

        //
        MACRO_safeFree(pUnit->pTransObj);
    }

    //
    if(pUnit->decA_var.cache.pBuf){
        myfree(pUnit->decA_var.cache.pBuf); pUnit->decA_var.cache.pBuf=NULL;
    }

    //
    freeDecBufs(index_qdcUnit);

    //
    iErr = 0;
    errLabel:
    return iErr;
}

int decA_data_melp(int index_qdcUnit, byte * encData,int encData_size, int iInputBufferPaddingSize, int *pnPktDeced, int * piFmt_dec, byte * decBuf, int *piDecBufSize, mybool *pbBufTooSmall, mybool bDbg) {
    int iErr = -1;

    QDC_unit *pUnit = getQdcUnitByIndex(index_qdcUnit);
    if (!pUnit) return -1;

//
    DecAMelp *pDecObj = (DecAMelp *) pUnit->pTransObj;
    if (!pDecObj) return -1;

    if (!encData || !encData_size) {
        LOGD("decA_data_melp failed, encData is null or encLen is 0");
        return -1;
    }
    if (!decBuf || !piDecBufSize || !*piDecBufSize) {
        LOGD("decA_data_melp failed, decBuf is null or pLen_raw is null, or *pLen_raw is 0");
        return -1;
    }

//
    do {



//
        if (10) {
            if (pDecObj->common.fp_decA_dumpEncData) {
                fwrite(encData, encData_size, 1, pDecObj->common.fp_decA_dumpEncData);
                //
                //LOGD("decA: ")
            }
        }


//
        MelpAPkt pkt = {0};
        if (encData_size > mycountof(pkt.buf)) {
            LOGE("decA_data_melp failed: pcmLen > sizeof(pkt.buf)");
            break;
        }
        pkt.len = encData_size;
        memcpy(pkt.buf, encData, pkt.len);

//
        int pktSize = offsetof(MelpAPkt, buf) + pkt.len;
        int tmpiRet;
        GENERIC_Q *p_q_in = (GENERIC_Q *) pDecObj->common.p_q_in;
        tmpiRet = qPostMsg(&pkt, pktSize, p_q_in, _T(""));
        if (tmpiRet) {
            LOGE("decA_data_melp:qPostMsg failed. tmpiRet %d", tmpiRet);
            break;
        }
        if (0) {
            char buf[128];
            _snprintf(buf, mycountof(buf),
                      ("decA_data_melp: post pkt to q_in ok, encLen %d, uiQNodes %d"), encData_size,
                      p_q_in->uiQNodes);
            LOGD("%s", buf);
        }


        //  这里要从p_q_out里取包，然后填入raw里
        int nGot = 0;


//
        if (piDecBufSize) {
            *piDecBufSize = nGot;
        }


//
        iErr = 0;
    } while (false);

//
    return iErr;
}

