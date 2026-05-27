
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "tmpFfmpegCommon.h"



  //
  int ffmpeg_qdc_init()
  {
      avcodec_register_all();

      g_qdc.bDone_qdc_init = true;

      return 0;
  }

#if  0
  int ffmpeg_qdc_exit()
  {
      g_qdc.bDone_qdc_init=false;

      return 0;
  }
#endif

  //
  int  ffmpeg_qdc_exit1()
  {
      g_qdc.bDone_qdc_init = false;

      return 0;
  }


// need sync by mutex before called
int newQdcUnitIndex(mybool bDbg)
{
    int  iErr  =  -1;
    int i;

    if ( g_qdc.m_pMems==NULL)return -1;

    //
    int  iStart  =  0;
    //
    iStart  =  1;   //  略掉0, 以便在清空时，0表示无效
    //
    for ( i=iStart;i<g_qdc.m_usMaxCnt;i++){
        if ( g_qdc.m_pMems[i].bUsed)continue;
        break;
    }
    if ( i==g_qdc.m_usMaxCnt){
        LOGE("newQdcUnitIndex failed, full");
        return -1;
    }
    QDC_unit *pUnit=&g_qdc.m_pMems[i];
    //
    pUnit->bUsed=true;

    do {

        memset(&pUnit->encV_var, 0, sizeof(pUnit->encV_var));
        memset(&pUnit->decV_var, 0, sizeof(pUnit->decV_var));
        memset(&pUnit->encA_var, 0, sizeof(pUnit->encA_var));
        memset(&pUnit->decA_var, 0, sizeof(pUnit->decA_var));

        //
        memset(&pUnit->decBufs, 0, sizeof(pUnit->decBufs));

        //
        pUnit->ucAudioCompressors = 0;

        //
#if  0
        if (initMyMutex(_T(""), &pUnit->mtx_syncInput)) {
            LOGE("newQdcUnitIndex failed, initMyMutex failed");
            break;
        }
#endif

        //
        iErr  =  0;

    } while (false );

    if  (  iErr )  {
        freeQdcUnitIndex(i,false);
        return  -1;
    }

    //
    return i;
}

// need sync by mutex before called
void freeQdcUnitIndex(int index,mybool bDbg)
{
    if ( g_qdc.m_pMems==NULL)return;
    if(index <0||index>=g_qdc.m_usMaxCnt)return;
    QDC_unit *pUnit=&g_qdc.m_pMems[index];

    //
    freeDecBufs(index);

    //
    pUnit->ucAudioCompressors  =  0;

    //
#if  0
    exitMyMutex(&pUnit->mtx_syncInput);
#endif

    //
    pUnit->bUsed=false;

    // do clean

    return;
}

QDC_unit * getQdcUnitByIndex(int index)
{
    if ( g_qdc.m_pMems==NULL)return NULL;
    if(index <0||index>=g_qdc.m_usMaxCnt)return NULL;
    return &g_qdc.m_pMems[index];
}

//
int freeDecBufs(int index_qdcUnit)
{
    QDC_unit *pUnit = getQdcUnitByIndex(index_qdcUnit);
    if (!pUnit) return -1;
    int i;
    for ( i=0;i<pUnit->decBufs.maxCnt_decBufMems;i++){
        DEC_buf  *pMem=&pUnit->decBufs.mems[i];
        if ( pMem->pBuf) myfree(pMem->pBuf);
        memset(pMem,0,sizeof(DEC_buf));
    }
    return 0;
}




