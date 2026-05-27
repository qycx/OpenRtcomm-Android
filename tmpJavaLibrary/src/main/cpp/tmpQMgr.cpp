//
// Created by qycx1 on 2024/12/31.
//

#include    <string.h>

#include "tmpQMgr.h"
#include "ctxQmThread.h"
#include    "myTypes.h"
#include "qySyncCommProc.h"
#include    <string>


//
//  在MAX_tmpQMems里，0作为混音播放器，其他的是单独播放器


bool g_bPrintErr  = false;


//
CTmpQMgr::CTmpQMgr()
{
    this->bDone_init=false;
    memset(&this->mems,0,sizeof(this->mems));
    this->m_usMaxCnt  =  MAX_tmpQMems;

    //
    memset(&m_aecLayout,0,sizeof(m_aecLayout));

    //
    memset(  &m_tmpRec,  0,  sizeof(  m_tmpRec));

    //
    init();

}

//
CTmpQMgr::~CTmpQMgr()
{
    exit();

    //

}



//
int  CTmpQMgr::init()
{
    int  iErr  =  -1;
    TCHAR tBuf[128];
    unsigned  int uiMaxQNodes;

    do  {
        //
        init_tickCnt();

        //
        uiMaxQNodes  =  25  *  2;     //  2 seconds, 25 pkts per second
        uiMaxQNodes  =  M_maxQNodes_aecPlayQ;

        //
        _sntprintf(tBuf, mycountof(tBuf), _T("aecPlayQ"));
        if (initGenericQ(tBuf, tBuf, 0, uiMaxQNodes, mymalloc, myfree, &m_tmpRec.aecPlayQ)) {
            break;
        }

        //
        uiMaxQNodes  =  M_maxQNodes_aecQ;

        //
        _sntprintf(tBuf, mycountof(tBuf), _T("aecThreadQ"));
        if (initGenericQ(tBuf, tBuf, 0, uiMaxQNodes, mymalloc, myfree, &m_tmpRec.aecThreadQ)) {
            break;
        }

        //
        _sntprintf(tBuf, mycountof(tBuf), _T("tmpRecQ"));
        if (initGenericQ(tBuf, tBuf, 0, uiMaxQNodes, mymalloc, myfree, &m_tmpRec.recQ)) {
            break;
        }
        m_tmpRec.bQInited  =  true;

        //
        iErr  =  0;
    }  while( false);

    return  iErr;
}

void CTmpQMgr::exit()
{
    //
    exitGenericQ(&m_tmpRec.recQ);
    //
    exitGenericQ(&m_tmpRec.aecThreadQ);
    //
    exitGenericQ(&m_tmpRec.aecPlayQ);


    //
    return;
}

//
CTmpQMgr g_tmpQMgr;



