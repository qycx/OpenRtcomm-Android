//
// Created by qycx1 on 2024/12/31.
//

#include    <string.h>
#include    <wchar.h>

#include    <android/log.h>

//
#include    "myTypes.h"
#include    "qySyncCommProc.h"
#include    "tmpQMgr.h"
#include "tmpQFunc.h"



//
int newTmpQMemIndex(int playerIndex)
{
    int  iErr  =  -1;

    int  index  =  playerIndex;

    if  (  index <  0  ||  index  >=  MAX_tmpQMems  )  return  -1;

    CTmpQMgr * pMgr  =  &g_tmpQMgr;
    TmpQMem   *  pMem  =  &pMgr->mems[index];
    TCHAR  tBuf[128];

    //
    if  (  pMem->bQInited)  return  -1;

    do {
        memset(pMem, 0, sizeof(TmpQMem));

        unsigned  int uiMaxQNodes  =  25  *  2;     //  2 seconds, 25 pkts per second
        uiMaxQNodes  =  M_maxQNodes_playAudioQ;//                 //  1 second


        //
        _sntprintf(tBuf, mycountof(tBuf), _T("tmpQ%d"), index);
        if (initGenericQ(tBuf, tBuf, 0, uiMaxQNodes, mymalloc, myfree, &pMem->q)) {
            break;
        }
        //
#if 0
        if (initGenericQ(tBuf, tBuf, 0, uiMaxQNodes, mymalloc, myfree, &pMem->tmp_q)) {
            break;
        }
#endif
        //
        pMem->bQInited = true;

        //



        //
        pMem->index_tmpQMem  =  index;


        //
        iErr  =  0;

    }  while(  false  );

    if  (  iErr  !=0  )  return  -1;

    //
    return  index;
}


//
int freeTmpQMemIndex(int index)
{
    int  iErr  =  -1;

    if  (  index <  0  ||  index  >=  MAX_tmpQMems  )  return  -1;

    CTmpQMgr * pMgr  =  &g_tmpQMgr;
    TmpQMem   *  pMem  =  &pMgr->mems[index];
    TCHAR  tBuf[128];


    //
    do {
        //
        if  (  !pMem->bQInited)  {
            LOGE("qy：freeTmpQMemIndex err, bQInited false");
        }

        if (pMem->bQInited) {

            //
            LOGD("qy： %s %d","freeTmpQMemIndex",    index);

            //
#if  0
            exitGenericQ(&pMem->tmp_q);
#endif

            //
            exitGenericQ(&pMem->q);

            //
            pMem->bQInited=false;
        }
        // reset cache fields even when queue was already released
        pMem->startIndex = 0;
        pMem->nLeft = 0;
        pMem->iSampleTimeInMs_last = 0;
        pMem->iSampleTimeInMs_start = 0;
        pMem->nTimes_getAudioData = 0;
        memset(pMem->buf, 0, sizeof(pMem->buf));


        //


        //
        iErr  =  0;
    } while ( false  );

    return  0;
}

//
TmpQMem *  getTmpQMem(void  *  p0,  int index )
{
    if  (  index <  0  ||  index  >=  MAX_tmpQMems  )  return  mynull;

    CTmpQMgr  *  pMgr  =  &g_tmpQMgr;
    TmpQMem   *  pMem  =  &pMgr->mems[index];

    return  pMem;
}





