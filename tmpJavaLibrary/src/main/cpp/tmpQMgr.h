//
// Created by qycx1 on 2024/12/31.
//

#ifndef OPENRTCOMM_TMPQMGR_H
#define OPENRTCOMM_TMPQMGR_H

//
#include <android/log.h>
#define LOG_TAG1 "LOG_TAG"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG1 ,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG1 ,__VA_ARGS__)

//
#include    "qySyncObj.h"
#include    "genericqueue.h"
#include "tmpAecPublic.h"


//
typedef  struct  __tmpAPkt_t  {
                 int            iSampleTimeInMs;
                 int            uiPts;
                 //
                 int            len;
                 char           buf[4096];
}        TmpAPkt;



//
typedef  struct  __tmpQMem_t  {

                 //
                 int64_t        ui64Id;
                 int            tn;
                 //
                 int            index_tmpQMem;

                 //
                 GENERIC_Q      q;

                 //
                 //GENERIC_Q      tmp_q;

                 //
                 char           buf[4096];
                 int            startIndex;
                 int            nLeft;

                 //
                 bool           bQInited;

                 //
                 int            iSampleTimeInMs_last;
                 int            iSampleTimeInMs_start;

                 //
                 int            nTimes_getAudioData;

}        TmpQMem;

//
typedef  struct     {
                    char buf[4096];
                    int startIndex;
                    int nLeft;
}        TmpCache;

//
typedef  struct  __tmpRec_t     {

                 //
                 GENERIC_Q      aecPlayQ;           //  已播放的数据.

                 //
                 GENERIC_Q      aecThreadQ;         //  为aec准备数据的q. 麦克风的原始数据，先发送到这里，然后aec结束后，发送到recQ

                 //
                 GENERIC_Q      recQ;               //

                 //
                 TmpCache       aecPlayCache;

                 //
                 TmpCache       aecThreadCache;

                 //
                 int            iSampleTimeInMs_last_toAecThreadQ;


                 //
                 bool           bQInited;

}        TmpRec;




//
#define     MAX_tmpQMems    25


//
class CTmpQMgr {
public:
    int         m_usMaxCnt;
    TmpQMem     mems[MAX_tmpQMems];

    //
    bool        bDone_init;

    //
    AEC_layout  m_aecLayout;

    //
    TmpRec      m_tmpRec;

    //
    CTmpQMgr();
    ~CTmpQMgr();

    //
private:
    int  init();
    void exit();

};


extern  CTmpQMgr g_tmpQMgr;

//
extern  bool g_bPrintErr;




//
#endif //OPENRTCOMM_TMPQMGR_H
