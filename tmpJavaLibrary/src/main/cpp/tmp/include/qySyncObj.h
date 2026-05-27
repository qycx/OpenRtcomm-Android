

#ifndef  __QYSYNCOBJ_H__
#define  __QYSYNCOBJ_H__	//  {

//
#include    <string.h>

#ifndef  __LINUX__

#else
#include    <pthread.h>
#endif 


//
#include    "myTypes.h"
#include    "qyDefs_open.h"

//
//
typedef  struct  __myMutex_t    {


#ifndef  __LINUX__
         HANDLE  hMutex;
#else
         pthread_mutex_t mutex;
#endif 
         //
         mybool  bInited;

		 //
		 TCHAR  lockThreadName[64];
		 int	lockThreadId;
		 TCHAR  lockWhere[64];
		 TCHAR  lockMutexName[64];

		 //
}        MyMutex;



//
#define		DEFAULT_milliseconds_syncQmObjQ			10  *  1000


#if  0
 HANDLE  syncQmObjQ(  LPCTSTR  mutexName,  unsigned  int  uiTimeoutInMilliseconds  ); 
 void  cancelSyncQmObjQ(  HANDLE  hMutex  );
#endif

//

  //
  //
#ifdef  __cplusplus
  extern  "C"  {
#endif

int  syncQmObjQ(  LPCTSTR  mutexName,  MyMutex  *  pMutex,  unsigned  int  uiTimeoutInMilliseconds  );
void  cancelSyncQmObjQ(  MyMutex  *  pMutex  );

int initMyMutex(LPCTSTR  mutexName,  MyMutex  *  pMutex);
int  exitMyMutex(  MyMutex  *  pMutex  );

__declspec(dllexport)  int  setThreadName(HANDLE  hThread, TCHAR* name);
      __declspec(dllexport)  int  getThreadName(HANDLE  hThread, TCHAR* name, int cnt);

#ifdef  __cplusplus
  };
#endif





 //
typedef  struct  __syncObjParam_t					{
#if  0
#ifndef  __LINUX__
				 LPCRITICAL_SECTION					lpCriticalSection;					 
#endif
#endif 
				 //
				 TCHAR							*	pMutexName;
}		 SYNC_OBJ_PARAM;


#ifdef  __cplusplus

class  CQySyncObj  {
 
		struct					{
#if  0
#ifndef  __LINUX__
			LPCRITICAL_SECTION	lpCriticalSection;	
#endif
#endif 
			//
			MyMutex				mutex_internal;

            //HANDLE			hMutex;
            MyMutex       *     pMutex;
			//
			BOOL				bLocked;
		}						m_var;

 public:
	    CQySyncObj(  )  {  
			memset(  &m_var,  0,  sizeof(  m_var  )  );	  
#ifdef  __DEBUG__
			lstrcpyn(m_var.mutex_internal.lockWhere, (TCHAR*)_T("nothing"), mycountof(m_var.mutex_internal.lockWhere));
#endif 
		}
		
		//  int  sync(  LPCTSTR  mutexName  )  {  m_var.hMutex  =  syncQmObjQ(  mutexName,  DEFAULT_milliseconds_syncQmObjQ  );  return  m_var.hMutex  ?  0  :  -1;  };
		int  sync(LPCTSTR  mutexName, MyMutex* pMutex, LPCTSTR hintParam, unsigned  int  uiMilliSeconds = DEFAULT_milliseconds_syncQmObjQ) {
			//
			TCHAR* hint = (TCHAR*)hintParam;
			if (!hint)  hint = (TCHAR*)_T("");

			//
			if (m_var.pMutex)  return  -1;
			//
			if (!pMutex) {
				pMutex = &m_var.mutex_internal;
			}
			//
			int  tmpiRet;
			tmpiRet = syncQmObjQ(mutexName, pMutex, uiMilliSeconds);
			if (tmpiRet)  return  -1;
			m_var.pMutex = pMutex;
			//
			m_var.bLocked = true;

			//
#ifdef  __DEBUG__
			if (0) {
#ifndef  __LINUX__
                getThreadName(GetCurrentThread(), pMutex->lockThreadName, mycountof(pMutex->lockThreadName));
				pMutex->lockThreadId = GetCurrentThreadId();
				lstrcpyn(pMutex->lockWhere, hint, mycountof(pMutex->lockWhere));
				lstrcpyn(pMutex->lockMutexName, mutexName, mycountof(pMutex->lockMutexName));

				//
				traceLog((TCHAR*)_T("sync: [%s]. %d"), mutexName, GetCurrentThreadId());
#endif
			}
#endif 

				//
				return  m_var.pMutex  ?  0  :  -1;  
		}

        int  sync(  SYNC_OBJ_PARAM  *  p,  MyMutex  *  pMutex, LPCTSTR  hintParam,  unsigned  int  uiMilliSeconds  =  DEFAULT_milliseconds_syncQmObjQ  )  {
				//
				TCHAR* hint = (TCHAR*)hintParam;
				if  (  !hint  )  hint  =  (TCHAR*)_T(  ""  );

				//
				if  (  !p  )  return  -1;
				if  (  m_var.bLocked  )  return  -1;
#ifndef  __LINUX__
#if  0
				if  (  p->lpCriticalSection  )  {
					if  (  !uiMilliSeconds  )  {
						if  (  !TryEnterCriticalSection(  p->lpCriticalSection  )  )  return  -1;
						}
					else  {
						  EnterCriticalSection(  p->lpCriticalSection  );
					}
					m_var.lpCriticalSection  =  p->lpCriticalSection;
					m_var.bLocked  =  TRUE;
					return  0;
				}
#endif 
#endif
                return  sync(  p->pMutexName,  pMutex,  hint,  uiMilliSeconds  );
		}

		~CQySyncObj(  )  {  
#ifndef  __LINUX__
#if  0
			if  (  m_var.lpCriticalSection  )  LeaveCriticalSection(  m_var.lpCriticalSection  );
#endif 
#endif
			if (m_var.pMutex) {
				if (m_var.bLocked) {
					cancelSyncQmObjQ(m_var.pMutex);
					//
#ifdef  __DEBUG__
					if (0) {
#ifndef  __LINUX__
						traceLog((TCHAR*)_T("cancel [%s]. %d"), m_var.pMutex->lockMutexName, GetCurrentThreadId());
#endif
					}
#endif 
				}
			}
		}
 };

#endif


///////////
typedef  struct  __qyMemory_t	{
				 int			iAllocType;
				 unsigned  int	uiBufSize;				//  2011/12/04
				 //  int			iIndex_tmpMemory;		//  2010/09/18
				 char	*		m_pVirtualData;
				 char	*		m_pBuf;
}		 QY_MEMORY;


#endif  //  }


