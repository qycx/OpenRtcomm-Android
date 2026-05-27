

#include	"stdafx.h"

#include	<time.h>
#ifndef  __LINUX__

#include	<windows.h>
#include	<tchar.h>
#include	<stdlib.h>
#include	<WinSock2.h>

#else

#include    <unistd.h>
#ifdef  __QT__
#include    <qdatetime.h>
#endif

#endif

#if  0
#include	"qyCommon.h"
#include	"qyCommCommon.h"
#include	"qyPreCustom.h"
#include	"qyDefs_open.h"
#include	"qyCustom.h"
#include	"qyCommProc.h"
#include	"qnmCustom.h"
#include	"qyTCharCommProc.h"

#include	"tmpCeLib.h"
#endif 

//

#include	"ctxQmThread.h"

#ifndef  __LINUX__
LARGE_INTEGER g_tFreq={0};
LARGE_INTEGER g_t0={0};
#endif
unsigned  __int64		  g_tickCnt0_us  =  0;

//
extern  "C"  __declspec(  dllexport  )  int init_tickCnt()
{
#ifndef  __LINUX__
	QueryPerformanceFrequency(  &g_tFreq  );
	QueryPerformanceCounter(  &g_t0  );
	g_tickCnt0_us  =  ((unsigned  __int64)GetTickCount(  ))  * 1000;
#else
    //
    struct timespec time;
    if(clock_gettime(CLOCK_MONOTONIC, &time) == -1){
        return  -1;
    }
    long long time_ns = (long long)time.tv_sec * 1000000000LL + (long long)time.tv_nsec;
    long long time_us = time_ns / 1000;
    //
    g_tickCnt0_us  =  time_us;
    //
#endif

	return  0;
}


extern  "C"  __declspec(  dllexport  )  unsigned  __int64  myGetMicroseconds()
{
#ifndef  __LINUX__
	if (!g_tFreq.QuadPart)  return  0;
	LARGE_INTEGER t1;
	QueryPerformanceCounter(&t1  );

	unsigned __int64 elapseInMs = ( t1.QuadPart  -  g_t0.QuadPart  )  *  1000000  /  g_tFreq.QuadPart;

	return elapseInMs  +  g_tickCnt0_us;
#else
	//
#ifdef  __QT__

	//
#if  0
	quint64 timestamp = QDateTime::currentMSecsSinceEpoch();
	QDateTime dt3 = QDateTime::fromMSecsSinceEpoch(timestamp);

#endif 
    //
    __int64 milliseconds = QDateTime::currentMSecsSinceEpoch();

    //
     __int64 us  =  milliseconds  *  1000;
    return  us;

#else

    struct timespec time;
    if(clock_gettime(CLOCK_MONOTONIC, &time) == -1){
	    return  -1;
    }
    long long time_ns = (long long)time.tv_sec * 1000000000LL + (long long)time.tv_nsec;
   long long time_us = time_ns / 1000; 

   long long  iDiff_us  =  time_us  -  g_tickCnt0_us;
   //
    return  iDiff_us;

#endif 

#endif
}


 extern  "C"  __declspec(  dllexport  )  DWORD  myGetTickCount(  CTX_qm_thread  *  pCtx  )
 {
	 unsigned  __int64  us  =  myGetMicroseconds(  );
	 DWORD  dwTickCnt  =  us  /  1000;
	 //
	 if  (  pCtx  )  {
		 if  (  dwTickCnt  <=  pCtx->dwTickCnt_lastGot  )  {
			 dwTickCnt  =  pCtx->dwTickCnt_lastGot  +  1;
		 }
		 pCtx->dwTickCnt_lastGot  =  dwTickCnt;
	 
		 //
		 #ifdef  __DEBUG__
                 if  (  myGetCurrentThreadId()  !=  pCtx->dwThreadId  )  {
					 MACRO_qyAssert(  0,  _T(  "myGetTickCount: threadId not matched"  )  );	 
				 }
		 #endif
	 }


	 return  dwTickCnt;
 }

//
extern  "C"  __declspec(dllexport ) void  mySleep(  DWORD dwMilliseconds  )
{
#ifndef  __LINUX__
    //
		 Sleep(dwMilliseconds);
#else
    //int nSec  =  dwMilliseconds  /  1000;
    int nu  =  dwMilliseconds  *  1000;

    usleep(nu);
#endif

    return;
}



 

 
 
