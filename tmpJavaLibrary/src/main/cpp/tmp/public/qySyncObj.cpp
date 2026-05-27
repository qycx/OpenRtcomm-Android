
#include	"stdafx.h"

#ifndef  __LINUX__

#include	<windows.h>

#else

#include    <sys/time.h>

#endif


//
#include	"qySyncObj.h"
#include    "ctxQmThread.h"


#if  0
 HANDLE  unused_syncQmObjQ(  LPCTSTR  mutexName,  unsigned  int  uiTimeoutInMilliSeconds  )
{
	 int		iErr			=	-1;
	 HANDLE		hMutex			=	NULL;
	 DWORD		dwRet			=	0;
	 
	 if  (  !mutexName  )  goto  errLabel;

#ifndef  __LINUX__

	 //  _sntprintf(  mutexName,  mycountof(  mutexName  ),  _T(  "syncQmObjQ"  )  );
	 if  (  !uiTimeoutInMilliSeconds  )  {				//  2008/05/22, ÔÚuiTimeoutInMilliSecondsÎª0Ê±£¬ÒâÎ¶×ÅÐèÒªÒ»¸ö¿ìËÙ»ñÈ¡¿ØÖÆÈ¨µÄ·½·¨¡£Èç¹û²»ÄÜ£¬¾Í·ÅÆú
		 hMutex  =  CreateMutex(  NULL,  TRUE,  mutexName  );
		 if  (  !hMutex  ||  GetLastError(  )  ==  ERROR_ALREADY_EXISTS  )  {
			 //  traceLogA(  "CreateMutex failed: %s,  lastError is %d",  (  hMutex  ?  "hMutex is ok"  :  "hMutex is null"  ),  GetLastError(  )  );  //  2004/05/13
			 goto  errLabel;
		 }
		}
	 else  {
		   hMutex = CreateMutex(  NULL,  0,  mutexName  );
		   if  (  !hMutex  )  {
			   #ifdef  __DEBUG__
					   TCHAR  tBuf[256];
					   _sntprintf(  tBuf,  sizeof(  tBuf  )  /  sizeof(  tBuf[0]  ),  _T(  "CreateMutex failed: %s,  lastError is %d\n"  ),  (  hMutex  ?  _T(  "hMutex is ok"  )  :  _T(  "hMutex is null"  )  ),  GetLastError(  )  );  //  2004/05/13
					   OutputDebugString(  tBuf  );
			   #endif
			   goto  errLabel;
		   }

		   dwRet  =  WaitForSingleObject(  hMutex,  uiTimeoutInMilliSeconds  );		
		   if ( dwRet == WAIT_FAILED || dwRet == WAIT_TIMEOUT )  goto  errLabel;
	 }

#endif

	 iErr  =  0;

errLabel:

	 if  (  iErr  )  {

#ifndef  __LINUX__
		 if  (  hMutex  )  {
			 ReleaseMutex(  hMutex  );  CloseHandle(  hMutex  );  hMutex  =  NULL;
		 }
#endif

	 }

	 return  hMutex;
}


 void  unused_cancelSyncQmObjQ(  HANDLE  hMutex  )
{

#ifndef  __LINUX__
	 if  (  hMutex  )  {
		 ReleaseMutex(  hMutex  );  CloseHandle(  hMutex  );  hMutex  =  NULL;
	 }
#endif

}
#endif



 //
 extern "C" int initMyMutex(LPCTSTR  mutexName,  MyMutex  *  pMutex)
 {
     int  iErr  =  -1;

     if  (  !pMutex  )  return  -1;

     //
     memset(pMutex,0,sizeof(MyMutex));

     //
     do  {


#ifndef  __LINUX__
         HANDLE  hMutex = mynull;
         hMutex = CreateMutex(NULL, 0, mutexName);
         if (!hMutex || GetLastError() == ERROR_ALREADY_EXISTS) {
             break;
         }
         pMutex->hMutex = hMutex;
         pMutex->bInited = true;
#else

         if  (  pthread_mutex_init(&pMutex->mutex,NULL)  )  {
             break;
         }
         pMutex->bInited  =  true;

#endif        
         //       
         iErr  =  0;

     }  while ( false  );

     if  (  iErr  )  {
         exitMyMutex(pMutex);
     }

     return  iErr;

 }

 int  exitMyMutex(  MyMutex  *  pMutex  )
 {
     if  (  !pMutex )  return  -1;

     //
     cancelSyncQmObjQ(pMutex);
#ifndef  __LINUX__
     HANDLE  hMutex = pMutex->hMutex;
     if (hMutex) {
         CloseHandle(hMutex);  hMutex = NULL;
     }
     pMutex->hMutex = mynull;
     pMutex->bInited = false;
#else
     if  (  pMutex->bInited)  {
        pthread_mutex_destroy(&pMutex->mutex);
        pMutex->bInited=false;
     }
#endif

     return  0;
 }


 //
// HANDLE  syncQmObjQ(  LPCTSTR  mutexName,  unsigned  int  uiTimeoutInMilliSeconds  )
 int  syncQmObjQ(  LPCTSTR  mutexName,  MyMutex  *  pMutex,  unsigned  int  uiTimeoutInMilliSeconds  )
{
     int		iErr			=	-1;
#ifndef  __LINUX__
     HANDLE		hMutex			=	NULL;
     DWORD		dwRet			=	0;

#endif

     if (!mutexName)  return  -1;// goto  errLabel;

     //
#ifdef  _DEBUG
     DWORD  tickCnt0 = myGetTickCount(mynull);
#endif 
     
     //
     do {

#ifndef  __LINUX__
         if (!pMutex) goto  errLabel;

         if  (  !pMutex->hMutex  ||  !pMutex->bInited) {

             //  _sntprintf(  mutexName,  mycountof(  mutexName  ),  _T(  "syncQmObjQ"  )  );
             if (!uiTimeoutInMilliSeconds) {				//  2008/05/22, ÔÚuiTimeoutInMilliSecondsÎª0Ê±£¬ÒâÎ¶×ÅÐèÒªÒ»¸ö¿ìËÙ»ñÈ¡¿ØÖÆÈ¨µÄ·½·¨¡£Èç¹û²»ÄÜ£¬¾Í·ÅÆú
                 hMutex = CreateMutex(NULL, TRUE, mutexName);
                 if (!hMutex || GetLastError() == ERROR_ALREADY_EXISTS) {
                     //  traceLogA(  "CreateMutex failed: %s,  lastError is %d",  (  hMutex  ?  "hMutex is ok"  :  "hMutex is null"  ),  GetLastError(  )  );  //  2004/05/13
                     goto  errLabel;
                 }
             }
             else {
                 hMutex = CreateMutex(NULL, 0, mutexName);
                 if (!hMutex) {
#ifdef  __DEBUG__
                     TCHAR  tBuf[256];
                     _sntprintf(tBuf, sizeof(tBuf) / sizeof(tBuf[0]), _T("CreateMutex failed: %s,  lastError is %d\n"), (hMutex ? _T("hMutex is ok") : _T("hMutex is null")), GetLastError());  //  2004/05/13
                     OutputDebugString(tBuf);
#endif
                     goto  errLabel;
                 }

                 dwRet = WaitForSingleObject(hMutex, uiTimeoutInMilliSeconds);
                 if (dwRet == WAIT_FAILED || dwRet == WAIT_TIMEOUT)  goto  errLabel;
             }

             //
             pMutex->hMutex = hMutex;  hMutex = mynull;
         }
         else {
              //

             dwRet = WaitForSingleObject(pMutex->hMutex, uiTimeoutInMilliSeconds);
             if (dwRet == WAIT_FAILED || dwRet == WAIT_TIMEOUT)  goto  errLabel;

         }

         
         
#else
     if  (  !pMutex)  break;
     //

     //struct timespec ts;
     //struct timespec now;


     struct timespec timeoutTime;

     clock_gettime(CLOCK_REALTIME, &timeoutTime);
     timeoutTime.tv_sec += uiTimeoutInMilliSeconds  /  1000;
     timeoutTime.tv_nsec  +=  (uiTimeoutInMilliSeconds%1000)*1000000;


     int retVal = pthread_mutex_timedlock(&pMutex->mutex, &timeoutTime);

#if  0
     if(retVal != 0) {
            const char* errVal = NULL;
            switch(retVal) {
            case EINVAL: errVal = "EINVAL"; break;
            case EAGAIN: errVal = "EAGAIN"; break;
            case ETIMEDOUT: errVal = "ETIMEDOUT"; break;
            case EDEADLK: errVal = "EDEADLK"; break;
            default: errVal = "unknown.."; break;
            }
            printf("Error taking lock in thread %lu: %s (%s)\n", pthread_self(), errVal , strerror(retVal));
        }
#endif
     if  (  retVal  )  {
         break;
     }
     //
#endif

     } while  (false);

     //
     iErr  =  0;

#ifndef  __LINUX__
errLabel:
#endif 

     //
     if  (  iErr  )  {

#ifndef  __LINUX__
         if  (  hMutex  )  {
             ReleaseMutex(  hMutex  );  CloseHandle(  hMutex  );  hMutex  =  NULL;
         }
#else
         if  (  pMutex)  {
             pthread_mutex_unlock(&pMutex->mutex);
         }
#endif

     }

     //
#ifdef  _DEBUG
     DWORD  tickCnt1 = myGetTickCount(mynull);
     int  iDiffInMs = tickCnt1 - tickCnt0;
     if (iDiffInMs > 1000) {
         //int  ii = 0;
     }
     //
     if (pMutex->bInited) {
         //int  ii = 0;
     }

#endif 


     //
     return  iErr;//hMutex;
}


 void  cancelSyncQmObjQ(  MyMutex  *  pMutex  )
{

#ifndef  __LINUX__
     if (!pMutex)  return;
     if (!pMutex->bInited) {
         HANDLE  hMutex = pMutex->hMutex;
         if (hMutex) {
             ReleaseMutex(hMutex);
             CloseHandle(hMutex);
             hMutex = NULL;
         }
         pMutex->hMutex = mynull;
     }
     else {
          ReleaseMutex(pMutex->hMutex);
     }
#else
     //
     if  (  pMutex  )  {
         int tmpiRet;
         tmpiRet  =  pthread_mutex_unlock(&pMutex->mutex);
         if  (  tmpiRet  )  {
             traceLog((TCHAR*)_T("err\n"));
         }
     }
#endif

}


