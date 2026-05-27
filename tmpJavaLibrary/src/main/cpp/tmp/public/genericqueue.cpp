

#include	"stdafx.h"

#include	"qyPrecomp.h"

#include	<stdio.h>
#include	<stdlib.h>
#include	<string.h>

#ifndef  __LINUX__

#include	<assert.h>

#include	<windows.h>
#include	<WinSock2.h>
#include	<tchar.h>


#else

#include    <wchar.h>

#endif

#include	"myTypes.h"



#include    "qyDefs_open.h"
#include    "commondefs.h"
#include	"qySyncCommProc.h"
#include	"genericqueue.h"

//#include	"qyCommProc.h"
//#include	"qyCommon/qyErrFunc.h"



//
static CTX_q_showInfo  sCtxQShowInfo  =  {NULL};


// just called by qyShowInfo.dll
extern  "C"  __declspec(  dllexport  )  int  set_q_showInfo(  PF_showInfo_open  pf_showInfo  )
{
	if  (  sCtxQShowInfo.pf_showInfo_open  )  return  -1;
	//
	memset(  &sCtxQShowInfo,  0,  sizeof(  sCtxQShowInfo  )  );
	//
	sCtxQShowInfo.pf_showInfo_open  =  pf_showInfo;
	return  0;
}


// just called by qyShowInfo.dll
extern  "C"  __declspec(  dllexport  )  void  clear_q_showInfo(  )
{
	//
	memset(  &sCtxQShowInfo,  0,  sizeof(  sCtxQShowInfo  )  );
	
	return;
}


extern  "C"  __declspec(  dllexport  )  int  tmp_showInfo_open_internal(  void  *  p0,  LPCTSTR  tWhere,  LPCTSTR  hint  )
{
	if (sCtxQShowInfo.pf_showInfo_open ) {
		sCtxQShowInfo.pf_showInfo_open(p0,tWhere,hint);
	}
	return 0;
}




//
 OBJ_T  *  objNew( int size )
{
	 OBJ_T *p = NULL;

	 p = mymalloc( size );
	 if ( p ) memset( p, 0, size );
	 
	 return p;

}


  void  objFree( OBJ_T *p ) 
 {
	 free( p );
	 return;
 }


/* #define qNodeNew( ) objNew( sizeof( Q_NODE ) ) */

 void  qNodeFree(  GENERIC_Q  *  pQ,  Q_NODE *pQNode,  BOOL  bElemRemove  )
{
	 
	 if  (  !pQ  ||  !pQNode  )  return;

	 //  2007/04/17, qNodeFreeÒªÊÇÊÍ·ÅËùÓÐºóÐøµÄ½Úµã»áÔì³É¶Ô¶ÑÕ»²»Àû£¬ËùÒÔ¸ÄÎª½öÊÍ·ÅÒ»¸ö

	 if  (  pQNode->qElemMemory.m_pBuf  )  {
		 if  (  pQNode->ucbQElemInited  )  {
             if  (  pQ->pfQElemExit  )  (  pQ->pfQElemExit  )(  pQNode->qElemMemory.m_pBuf  );
			 pQNode->ucbQElemInited  =  FALSE;
		 }
		 if  (  bElemRemove  )  {	//  2009/12/08
			 if  (  pQ->pfqElemRemoveEx  )  (  pQ->pfqElemRemoveEx  )(  pQNode->qElemMemory.m_pBuf,  pQNode->size,  pQ->cfg.name  );	//  2012/11/10
			 else  {
				   if  (  pQ->pfQElemRemove  )  (  pQ->pfQElemRemove  )(  pQNode->qElemMemory.m_pBuf,  pQ->cfg.name  );
			 }
		 }
		 if  (  pQ->pfQElemFreeEx  )  (  *pQ->pfQElemFreeEx  )(  &pQNode->qElemMemory  );
		 else  {
			   if  (  pQ->pfQElemFree  )  (  *pQ->pfQElemFree )(  pQNode->qElemMemory.m_pBuf  );
		 }
	 }
	 free( pQNode );

	 return;

}

 //  ×¢Òâ£ºÕâÀïÖ»ÓÐgenericQNew(  ), genericQFree(  ),  initGenericQ(  ),  exitGenericQ(  ),  postMsg(  ),  getMsg(  )
 //  Õâ6¸öº¯ÊýÊÇÓÐÍ¬²½µÄ£¬ÆäËûº¯Êý¶¼²»ÄÜÓÃ


  //  uiMaxCnt_semaÎª0Ê±£¬²»Ê¹ÓÃsema
 extern  "C"  __declspec(  dllexport  )  GENERIC_Q  *  genericQNew(  LPCTSTR  qName,  LPCTSTR  mutexName_prefix,  unsigned  int  uiMaxCnt_sema,  unsigned  int  uiMaxQNodes,  PF_qElemNew  pfQElemNew,  PF_qElemFree  pfQElemFree  )
{
	int				iErr	=	-1;
	GENERIC_Q	*	pQ		=	NULL;

	pQ  =  (  GENERIC_Q  *  )mymalloc(  sizeof(  GENERIC_Q  )  );
	if  (  !pQ  )  return  NULL;

	if  (  initGenericQ(  qName,  mutexName_prefix,  uiMaxCnt_sema,  uiMaxQNodes,  pfQElemNew,  pfQElemFree,  pQ  )  )  goto  errLabel;


	iErr  =  0;

errLabel:
	if  (  iErr  )  free(  pQ  );

	return  iErr  ?  NULL  :  pQ;

}


 extern  "C"  __declspec(  dllexport  )  void  genericQFree(  GENERIC_Q  *  pQ  )
{
	if  (  !pQ  )  return;
	
	exitGenericQ(  pQ  );

	free(  pQ  );

	return;

}


 //  uiMaxCnt_semaÎª0Ê±£¬²»Ê¹ÓÃsema
 

 __declspec(  dllexport  )  int  initGenericQ(  LPCTSTR  qName,  LPCTSTR  mutexName_prefix,  unsigned  int  uiMaxCnt_semaTrigger,  unsigned  int  uiMaxQNodes,  PF_qElemNewEx  pfQElemNewEx,  PF_qElemNew  pfQElemNew,  PF_qElemInit  pfQElemInit,  PF_qElemExit  pfQElemExit,  PF_qElemFreeEx  pfQElemFreeEx,  PF_qElemFree  pfQElemFree,  PF_qElemRemoveEx  pfQElemRemoveEx,  PF_qElemRemove  pfQElemRemove,  GENERIC_Q  *  pQ  )
{
	int	iErr	=	-1;

	if  (  !pQ  )  return  -1;

	memset(  pQ,  0,  sizeof(  GENERIC_Q  )  );
	
	if  (  qName  )  _tcsncpy(  pQ->cfg.name,  qName,  mycountof(  pQ->cfg.name  )  );
	if (mutexName_prefix) {
		_tcsncpy(pQ->cfg.mutexName_prefix, mutexName_prefix, mycountof(pQ->cfg.mutexName_prefix));

		//  2009/05/14
		pQ->bNeedSync = TRUE;

		//
#if  0
#ifndef  __LINUX__
		__try {
			InitializeCriticalSection(&pQ->cs);
		}
		__except (GetExceptionCode() == STATUS_NO_MEMORY) {
			traceLogA("initGenericQ: InitializeCriticalSection failed.");
			goto  errLabel;
		}
#endif
		pQ->bCsInited = TRUE;
#endif 

		//
		pQ->syncObjParam.pMutexName = pQ->cfg.mutexName_prefix;
#ifndef  __LINUX__
		//
#if  0
		pQ->syncObjParam.lpCriticalSection = &pQ->cs;
#endif 
		//
#endif
		//
		//static int  scnt_q = 0;
		//scnt_q++;
		//
		//if (scnt_q == 1) 
		{
#if  10
			int  size;
			size = sizeof(MyMutex);
			pQ->pMutex = (MyMutex*)mymalloc(size);
			if (!pQ->pMutex)  goto  errLabel;
			memset(pQ->pMutex, 0, size);
			if (initMyMutex(pQ->cfg.mutexName_prefix, pQ->pMutex)) {
				goto  errLabel;
			}
#endif 
	}

		//
	}
	if  (  uiMaxCnt_semaTrigger  )  {
		pQ->cfg.uiMaxCnt_semaTrigger  =  uiMaxCnt_semaTrigger;
        //
#ifndef  __LINUX__
		if  (  !(  pQ->hSemaTrigger  =  CreateSemaphore(  NULL,  0,  pQ->cfg.uiMaxCnt_semaTrigger,  NULL  )  )  )  goto  errLabel;
#endif
		traceLog(  (TCHAR*)_T(  "CreateSemaphore: %s sema"  ),  pQ->cfg.name  );
	}
	pQ->cfg.uiMaxQNodes  =  uiMaxQNodes;

	pQ->pfQElemNewEx  =  pfQElemNewEx;
	pQ->pfQElemFreeEx  =  pfQElemFreeEx;
	//
	pQ->pfQElemNew = pfQElemNew;
	pQ->pfQElemFree = pfQElemFree;
	//
	pQ->pfQElemInit  =  pfQElemInit;
	pQ->pfQElemExit  =  pfQElemExit;
	//
	pQ->pfqElemRemoveEx  =  pfQElemRemoveEx;	//  2012/11/10
	//
	pQ->pfQElemRemove  =  pfQElemRemove;

	//
	pQ->pRear  =  &pQ->front;
	
	pQ->uiQNodes  =  0;			//  ×¢Òâ£ºÕâÀï²»ÐèÒª¿¼ÂÇfront½Úµã£¬ËùÒÔ¼ÇÊýÆðµãÎª0

	//  2012/07/14
	pQ->bInited  =  TRUE;

	//
	pQ->ctxQShowInfo  =  sCtxQShowInfo;

	//
	iErr  =  0;

errLabel:

	if  (  iErr  )  {
		exitGenericQ(  pQ  );
	}

	traceLogA(  (char*)"initGenericQ: %S %s",  pQ->cfg.name,  iErr  ?  "failed"  :  "ok"  );

	return  iErr;
	
}


 __declspec(  dllexport  )  int  initGenericQ(  LPCTSTR  qName,  LPCTSTR  mutexName_prefix,  unsigned  int  uiMaxCnt_semaTrigger,  unsigned  int  uiMaxQNodes,  PF_qElemNewEx  pfQElemNewEx,  PF_qElemNew  pfQElemNew,  PF_qElemInit  pfQElemInit,  PF_qElemExit  pfQElemExit,  PF_qElemFreeEx  pfQElemFreeEx,  PF_qElemFree  pfQElemFree,  PF_qElemRemove  pfQElemRemove,  GENERIC_Q  *  pQ  )
{
	PF_qElemRemoveEx  pfQElemRemoveEx  =  NULL;

	return  initGenericQ(  qName,  mutexName_prefix,  uiMaxCnt_semaTrigger,  uiMaxQNodes,  pfQElemNewEx,  pfQElemNew,  pfQElemInit,  pfQElemExit,  pfQElemFreeEx,  pfQElemFree,  pfQElemRemoveEx,  pfQElemRemove,  pQ  );
}


__declspec(  dllexport  )   int  initGenericQ(  LPCTSTR  qName,  LPCTSTR  mutexName_prefix,  unsigned  int  uiMaxCnt_semaTrigger,  unsigned  int  uiMaxQNodes,  PF_qElemNew  pfQElemNew,  PF_qElemFree  pfQElemFree,  GENERIC_Q  *  pQ  )
{
	return  initGenericQ(  qName,  mutexName_prefix,  uiMaxCnt_semaTrigger,  uiMaxQNodes,  NULL,  pfQElemNew,  NULL,  NULL,  NULL,  pfQElemFree,  NULL,  pQ  );
}


__declspec(  dllexport  )   int  initGenericQ(  GENERIC_Q_CFG  *  pCfg,  PF_qElemNew  pfQElemNew,  PF_qElemInit  pfQElemInit,  PF_qElemExit  pfQElemExit,  PF_qElemFree  pfQElemFree,  PF_qElemRemove  pfQElemRemove,  GENERIC_Q  *  pQ  )
{
	 return  initGenericQ(  pCfg->name,  pCfg->mutexName_prefix,  pCfg->uiMaxCnt_semaTrigger,  pCfg->uiMaxQNodes,  NULL,  pfQElemNew,  pfQElemInit,  pfQElemExit,  NULL,  pfQElemFree,  pfQElemRemove,  pQ  );
}


 __declspec(  dllexport  )  int  initGenericQ(  GENERIC_Q_CFG  *  pCfg,  PF_qElemNewEx  pfQElemNewEx,  PF_qElemNew  pfQElemNew,  PF_qElemInit  pfQElemInit,  PF_qElemExit  pfQElemExit,  PF_qElemFreeEx  pfQElemFreeEx,  PF_qElemFree  pfQElemFree,  PF_qElemRemove  pfQElemRemove,  GENERIC_Q  *  pQ  )
{
	return  initGenericQ(  pCfg->name,  pCfg->mutexName_prefix,  pCfg->uiMaxCnt_semaTrigger,  pCfg->uiMaxQNodes,  pfQElemNewEx,  pfQElemNew,  pfQElemInit,  pfQElemExit,  pfQElemFreeEx,  pfQElemFree,  pfQElemRemove,  pQ  );
}


 #if  0  //  2015/08/16
 //  2012/11/10
 __declspec(  dllexport  )  int  initGenericQ(  GENERIC_Q_cfgEx  *  pCfg,  GENERIC_Q  *  pQ  )
{
	if  (  initGenericQ(  &pCfg->common,  pCfg->pfQElemNewEx,  pCfg->pfQElemNew,  pCfg->pfQElemInit,  pCfg->pfQElemExit,  pCfg->pfQElemFreeEx,  pCfg->pfQElemFree,  pCfg->pfQElemRemove,  pQ  )  )  return  -1;
	pQ->pfqElemRemoveEx  =  pCfg->pf_QElemRemoveEx;
	return  0;
}
#endif

 __declspec(  dllexport  )  int  initGenericQ(  GENERIC_Q_cfgEx  *  pCfg,  GENERIC_Q  *  pQ  )
{
	//if  (  initGenericQ(  &pCfg->common,  pCfg->pfQElemNewEx,  pCfg->pfQElemNew,  pCfg->pfQElemInit,  pCfg->pfQElemExit,  pCfg->pfQElemFreeEx,  pCfg->pfQElemFree,  pCfg->pfQElemRemove,  pQ  )  )  return  -1;

	//
	// __declspec(  dllexport  )  int  initGenericQ(  LPCTSTR  qName,  LPCTSTR  mutexName_prefix,  unsigned  int  uiMaxCnt_semaTrigger,  unsigned  int  uiMaxQNodes,  PF_qElemNewEx  pfQElemNewEx,  PF_qElemNew  pfQElemNew,  PF_qElemInit  pfQElemInit,  PF_qElemExit  pfQElemExit,  PF_qElemFreeEx  pfQElemFreeEx,  PF_qElemFree  pfQElemFree,  PF_qElemRemoveEx  pfQElemRemoveEx,  PF_qElemRemove  pfQElemRemove,  GENERIC_Q  *  pQ  )

	
	int	iErr	=	-1;

	//
	LPCTSTR  qName  =  pCfg->common.name;
	LPCTSTR  mutexName_prefix  =  pCfg->common.mutexName_prefix;
	unsigned  int  uiMaxCnt_semaTrigger  =  pCfg->common.uiMaxCnt_semaTrigger;
	unsigned  int  uiMaxQNodes  =  pCfg->common.uiMaxQNodes;
	PF_qElemNewEx  pfQElemNewEx  =  pCfg->pfQElemNewEx;
	PF_qElemNew  pfQElemNew  =  pCfg->pfQElemNew;
	PF_qElemInit  pfQElemInit  =  pCfg->pfQElemInit;
	PF_qElemExit  pfQElemExit  =  pCfg->pfQElemExit;
	PF_qElemFreeEx  pfQElemFreeEx  =  pCfg->pfQElemFreeEx;
	PF_qElemFree  pfQElemFree  =  pCfg->pfQElemFree;
	PF_qElemRemoveEx  pfQElemRemoveEx  =  pCfg->pf_QElemRemoveEx;
	PF_qElemRemove  pfQElemRemove  =  pCfg->pfQElemRemove;


	if  (  !pQ  )  return  -1;

	memset(  pQ,  0,  sizeof(  GENERIC_Q  )  );
	
	if  (  qName  )  _tcsncpy(  pQ->cfg.name,  qName,  mycountof(  pQ->cfg.name  )  );
	pQ->cfg.uiMaxQNodes  =  uiMaxQNodes;

	//
	if  (  mutexName_prefix  )  {
		_tcsncpy(  pQ->cfg.mutexName_prefix,  mutexName_prefix,  mycountof(  pQ->cfg.mutexName_prefix  )  );

		//  2009/05/14
		pQ->bNeedSync  =  TRUE;

#if  0
#ifndef  __LINUX__
		__try  {
			   InitializeCriticalSection(  &pQ->cs  );
			   }
		__except  (  GetExceptionCode(  )  ==  STATUS_NO_MEMORY  )  {
			      traceLogA(  "initGenericQ: InitializeCriticalSection failed."  );
				  goto  errLabel;
		}
#endif
        //
		pQ->bCsInited  =  TRUE;
#endif 

		//
		pQ->syncObjParam.pMutexName  =  pQ->cfg.mutexName_prefix;
#ifndef  __LINUX__
#if  0
		pQ->syncObjParam.lpCriticalSection  =  &pQ->cs;
#endif 
#endif
		//
#if  10
		int  size;
		size = sizeof(MyMutex);
		pQ->pMutex = (MyMutex*)mymalloc(size);
		if (!pQ->pMutex)  goto  errLabel;
		memset(pQ->pMutex, 0, size);
		if (initMyMutex(pQ->cfg.mutexName_prefix, pQ->pMutex)) {
			goto  errLabel;
		}
#endif 
	}
	if  (  uiMaxCnt_semaTrigger  )  {
		pQ->cfg.uiMaxCnt_semaTrigger  =  uiMaxCnt_semaTrigger;
        //
#ifndef  __LINUX__
		if  (  !(  pQ->hSemaTrigger  =  CreateSemaphore(  NULL,  0,  pQ->cfg.uiMaxCnt_semaTrigger,  NULL  )  )  )  goto  errLabel;
#endif
		traceLog(  (TCHAR*)_T(  "CreateSemaphore: %s sema"  ),  pQ->cfg.name  );
	}

	//  2015/08/16
	if  (  pCfg->common.bUseEvt  )  {
		pQ->cfg.bUseEvt  =  pCfg->common.bUseEvt;
		//
#ifndef  __LINUX__
		pQ->hEvent  =  CreateEvent(  NULL,  FALSE,  FALSE,  (  pQ->cfg.evt0Name[0]  ?  pQ->cfg.evt0Name  :  NULL  )  );
#endif
		if ( NULL == pQ->hEvent ) goto  errLabel;
	}


	//
	pQ->pfQElemNewEx  =  pfQElemNewEx;
	pQ->pfQElemFreeEx  =  pfQElemFreeEx;
	//
	pQ->pfQElemNew = pfQElemNew;
	pQ->pfQElemFree = pfQElemFree;
	//
	pQ->pfQElemInit  =  pfQElemInit;
	pQ->pfQElemExit  =  pfQElemExit;
	//
	pQ->pfqElemRemoveEx  =  pfQElemRemoveEx;	//  2012/11/10
	//
	pQ->pfQElemRemove  =  pfQElemRemove;

	//
	pQ->pfqElemRemoveEx  =  pCfg->pf_QElemRemoveEx;


	//
	pQ->pRear  =  &pQ->front;
	
	pQ->uiQNodes  =  0;			//  ×¢Òâ£ºÕâÀï²»ÐèÒª¿¼ÂÇfront½Úµã£¬ËùÒÔ¼ÇÊýÆðµãÎª0

	//  2012/07/14
	pQ->bInited  =  TRUE;

	//
	pQ->ctxQShowInfo  =  sCtxQShowInfo;

	//
	iErr  =  0;

errLabel:

	if  (  iErr  )  {
		exitGenericQ(  pQ  );
	}

	traceLogA(  (char*)"initGenericQ: %S %s",  pQ->cfg.name,  iErr  ?  "failed"  :  "ok"  );

	return  iErr;
	
}





 //
 extern  "C"  __declspec(  dllexport  )  void  exitGenericQ(  GENERIC_Q  *  pQ  )
{
	Q_NODE  *  pQNode,  *  tmppQNode;

	if  (  !pQ  )  return;
	
	for  (  pQNode  =  pQ->front.pNext;  pQNode;  )  {
		 tmppQNode  =  pQNode->pNext;
		 qNodeFree(  pQ,  pQNode,  TRUE  );
		 pQNode  =  tmppQNode;	 
	}

	//  2015/08/16
	if  (  pQ->hEvent  )  {			  
#ifndef  __LINUX__
        CloseHandle(  pQ->hEvent  );  pQ->hEvent  =  NULL;
#endif
    }

	//
	if  (  pQ->hSemaTrigger  )  {
#ifndef  __LINUX__
		CloseHandle(  pQ->hSemaTrigger  );	pQ->hSemaTrigger  =  NULL;
#endif
		traceLog(  (TCHAR*)_T(  "CloseHandle: %s sema"  ),  pQ->cfg.name  );
	}

#if  0
	if  (  pQ->bCsInited  )  {
#ifndef  __LINUX__
		DeleteCriticalSection(  &pQ->cs  );
#endif
	}
#endif 
	if (pQ->pMutex) {
		exitMyMutex(pQ->pMutex);
		MACRO_mysafeFree(pQ->pMutex);
	}

	//
	traceLogA(  (char*)"exitGenericQ: %S",  pQ->cfg.name  );

	memset(  pQ,  0,  sizeof(  GENERIC_Q  )  );
	
	return;
	
}

 extern  "C"  __declspec(  dllexport  )  int  qPostMsg(  Q_ELEM_T  *  pQElem,  unsigned  int  size,  GENERIC_Q  *  pQ,  LPCTSTR  hint  )
{
	return  qPostMsgEx(  pQElem,  0,  size,  pQ,  hint  );
}

 extern  "C"  int  qPostMsgEx(  Q_ELEM_T  *  pQElem,  int  iAllocType,  unsigned  int  size,  GENERIC_Q  *  pQ,  LPCTSTR  hint  )
{
	int					iErr		=	-1;
	Q_NODE			*	pQNode		=	NULL;
	//unsigned  int		uiSeqNo		=	0;
	CQySyncObj			syncObj;
	//
	if ( hint ==  NULL  )  hint  =  (TCHAR*)_T(  ""  );

	//
	if  (  !pQ  )  return  -1;
	
	if  (  isQFull(  pQ  )  )  {
		#ifdef  __DEBUG__
				traceLogA(  (char*)"qPostMsg failed: %S is full",  pQ->cfg.name  );
		#endif
		#ifdef  _UNICODE
				TCHAR  tBuf[128];
				//_sntprintf(  tBuf,  mycountof(  tBuf  ),  _T(  "qPostMsg failed: q is full. pid %d, threadId %d"  ),  GetCurrentProcessId(  ),  GetCurrentThreadId(  )  );
				_sntprintf(  tBuf,  mycountof(  tBuf  ),  _T(  "qPostMsg failed: q is full. nodes %d. %s"  ),  pQ->uiQNodes,  hint  );
				qyShowInfo_internal(  &pQ->ctxQShowInfo,  CONST_qyShowType_warning,  0,  "",  pQ->cfg.name,  NULL,  NULL,  _T(  ""  ),  _T(  "%s"  ),  tBuf  );
		#endif
		return  CONST_qyRet_qFull;
	}

	if  (  !(  pQNode  =  (  Q_NODE  *  )qNodeNew(  )  )  )  return  -1;

	if  (  pQ->pfQElemNewEx  )  {
		if  (  !(  pQ->pfQElemNewEx(  iAllocType,  size,  &pQNode->qElemMemory  )  )  )  goto  errLabel;
		}
	else  {
		  if  (  pQ->pfQElemNew  )  {			//  2007/08/15, Ôö¼Ó¶ÔpfQElemNewµÄ·Ç0µÄÐ£Ñé
			  if  (  !(  pQNode->qElemMemory.m_pBuf  =  (  char  *  )pQ->pfQElemNew(  size  )  )  )  goto  errLabel;
		  }
	}
	if  (  pQNode->qElemMemory.m_pBuf  &&  size  )  {
		memcpy(  pQNode->qElemMemory.m_pBuf,  pQElem,  size  );
		pQNode->size  =  size;
	}
	//
	if  (  pQ->pfQElemInit  &&  pQ->pfQElemInit(  pQNode->qElemMemory.m_pBuf  )  )  goto  errLabel;		
	pQNode->ucbQElemInited  =  TRUE;

	//
#ifdef  __DEBUG__
	//
    if  (  0  )  {
	if (!_tcsicmp(pQ->cfg.name, _T("guiProcQ"))) {
		//int  ii = 0;
		TCHAR  tBuf[128];
        _sntprintf(tBuf, mycountof(tBuf), _T("qPostMsgEx: threadId: %d, %s"), myGetCurrentThreadId(), hint);
        //qyShowInfo_internal(&pQ->ctxQShowInfo, CONST_qyShowType_warning, 0, "", pQ->cfg.name, NULL, NULL, _T(""), tBuf);
	}
    }
	//
#endif 


	//
    if  (  pQ->bNeedSync  &&  syncObj.sync(  &pQ->syncObjParam,  pQ->pMutex,  (LPCTSTR)_T(  "qPostMsgEx.550"  )  )) {
		#ifdef  _UNICODE
				qyShowInfo_internal(  &pQ->ctxQShowInfo,  CONST_qyShowType_warning,  0,  "",  pQ->cfg.name,  NULL,  NULL,  _T(  ""  ),  _T(  "qPostMsg failed: sync failed"  )  );
		#endif
		goto  errLabel;
	}

	if  (  isQFull(  pQ  )  )  {
		#ifdef  _UNICODE
				qyShowInfo_internal(  &pQ->ctxQShowInfo,  CONST_qyShowType_warning,  0,  "",  pQ->cfg.name,  NULL,  NULL,  _T(  ""  ),  _T(  "qPostMsg failed: q is full"  )  );
		#endif
		iErr  =  CONST_qyRet_qFull;  goto  errLabel;
	}

#ifdef  __DEBUG__		//  2009/12/12
		if  (  10  )  {
			if  (  size  ==  51742
				||  size  ==  51354  )  
			{
				traceLogA(  (char*)"qPostMsg: size %d",  size  );
			}
		}
#endif

#ifdef  __DUAL_LINK_LIST__
		pQNode->pPrev  =  pQ->pRear;
#endif
	pQNode->pNext  =  NULL;
	
	pQ->pRear->pNext  =  pQNode;
	pQ->pRear  =  pQNode;

	pQ->uiQNodes  ++  ;

	return  0;

errLabel:
	
	if  (  pQNode  )  qNodeFree(  pQ,  pQNode,  FALSE  );
	
	return  iErr;

}

 __declspec(  dllexport  )  int  qPostMsgAndTrigger(  Q_ELEM_T  *  pQElem,  unsigned  int  size,  GENERIC_Q  *  pQ,  LPCTSTR  hint  )
{
	 int				tmpiRet			=	-1;
	 unsigned  char		ucbTriggerErr	=	FALSE;

	 if  (  pQElem  &&  size  )  {
         if  (  (  tmpiRet  =  qPostMsg(  pQElem,  size,  pQ,  hint  )  )  )  return  tmpiRet;
	 }
#ifndef  __LINUX__
	 if  (  pQ->hSemaTrigger  &&  !ReleaseSemaphore(  pQ->hSemaTrigger,  1,  &pQ->lPrevCnt_semaTrigger  )  )  {
		 qyDisplayLastError(  "ReleaseSemaphore failed"  );
		 
		 ucbTriggerErr  =  TRUE;

		 traceLogA(  "%S qPostMsgAndTrigger err: uiQNodes %d, lPrevCnt_semaTrigger %d",  pQ->cfg.name,  pQ->uiQNodes,  pQ->lPrevCnt_semaTrigger  );
		 #ifdef  _UNICODE
				 qyShowInfo_internal(  &pQ->ctxQShowInfo,  CONST_qyShowType_warning,  0,  "",  pQ->cfg.name,  NULL,  NULL,  _T(  ""  ),  _T(  "qPostMsgAndTrigger failed: trigger err, lPrevCnt %d"  ),  pQ->lPrevCnt_semaTrigger  );
		 #endif
		 }
	 else  {
		   #ifdef  __DEBUG__
				   /*
				   TCHAR	tBuf[255  +  1]	=	_T(  ""  );
				   _sntprintf(  tBuf,  mycountof(  tBuf  ),  _T(  "%s qPostMsgAndTrigger : uiQNodes %d, lPrevCnt_semaTrigger %d\n"  ),  pQ->cfg.name,  pQ->uiQNodes,  pQ->lPrevCnt_semaTrigger  );
				   OutputDebugString(  tBuf  );
				   */
		   #endif
	 }
	 //  2015/08/16
	 if  (  pQ->hEvent  )  {
		 SetEvent(  pQ->hEvent  );
	 }
#endif

	 //  2008/11/21
	 if  (  ucbTriggerErr  !=  pQ->ucbTriggerErr  )  {
		 pQ->ucbTriggerErr  =  ucbTriggerErr;
	 }

	 return  0;
}


 //  Õâ¸ömutexName_toAvoidTriggerTooHighÊÇ×¨ÃÅÔÚ¶àÏß³ÌµÄÇé¿öÏÂÓÃÀ´·ÀÖ¹¶à¸öÏß³ÌÍ¬Ê±½øÐÐµ÷½ÚtriggerÓÃµÄ
 extern  "C" __declspec(  dllexport  ) int  qAvoidTriggerTooHigh(  LPCTSTR  mutexName_toAvoidTriggerTooHigh,  GENERIC_Q  *  pQ  )
{
	if  (  !pQ  ||  !pQ->hSemaTrigger  )  return  0;

	long  lPrevCnt_semaTrigger;	lPrevCnt_semaTrigger  =  pQ->lPrevCnt_semaTrigger;
    lPrevCnt_semaTrigger  =  mymin(  (  int  )pQ->cfg.uiMaxCnt_semaTrigger,  lPrevCnt_semaTrigger  );
    long	minlPrevCnt		=	mymax(  10,  pQ->cfg.uiMaxCnt_semaTrigger  /  10  );
    
	if  (  lPrevCnt_semaTrigger  >  minlPrevCnt  )  {
		CQySyncObj		syncObj;

		if  (  mutexName_toAvoidTriggerTooHigh  &&  mutexName_toAvoidTriggerTooHigh[0]  )  {
			if  (  syncObj.sync(  mutexName_toAvoidTriggerTooHigh,  0,  (LPCTSTR)_T(  "qAvoidTriggerToHigh.651"  )))  return  0;
		}
	
#ifndef  __LINUX__
		long	maxlPrevCnt		=	mymax(  50,  pQ->cfg.uiMaxCnt_semaTrigger  /  2  );

		if  (  lPrevCnt_semaTrigger  >  maxlPrevCnt  )  {				//  ÒªÔÚÕâÀïÓÃwaitForSingleObject°Ñ¼ÆÊý½µÏÂÀ´¡£·ñÔò£¬ÆäËûµØ·½»áÒòÎª¼ÆÊý¹ý´ó£¬¶ø´¥·¢³ö´í±äÂý¡£
			int	i;
			i  =  lPrevCnt_semaTrigger  -  minlPrevCnt;
            i  =  mymin(  i,  50  );										//  Ò»´Î×î¶à50¸ö£¬·ñÔò£¬ÈÝÒ×Ê±¼äÌ«³¤
			for  (  ;  i;  i  --  )  {
				 WaitForSingleObject(  pQ->hSemaTrigger,  0  );			
				 pQ->lPrevCnt_semaTrigger  --  ;				 		
			}			  			   
			}
		else  {  
			  WaitForSingleObject(  pQ->hSemaTrigger,  0  );  pQ->lPrevCnt_semaTrigger  --  ;			
		}
#endif

	}

	return  0;
}


 extern  "C"  int  qSendMsg(  Q_ELEM_T  *  pQElem,  int  size,  GENERIC_Q  *  pQ  )
{
	Q_NODE  *  pQNode	=	NULL;
	CQySyncObj			syncObj;

	if  (  !pQ  )  return  -1;

	if  (  pQ->cfg.uiMaxQNodes  &&  pQ->uiQNodes  >=  pQ->cfg.uiMaxQNodes  )  {
		traceLogA(  (char*)"%S ÒÑ´ï½Úµã×ÜÊý×î´óÏÞ£¬qPostMsg failed",  pQ->cfg.name  );
		return  -1;
	}

	if  (  !(  pQNode  =  (  Q_NODE  *  )qNodeNew(  )  )  )  return  -1;

	if  (  pQ->pfQElemNewEx  )  {
		if  (  !(  pQ->pfQElemNewEx(  0,  size,  &pQNode->qElemMemory  )  )  )  goto  errLabel;
		}
	else  {
		  if  (  pQ->pfQElemNew  )  {
			  if  (  !(  pQNode->qElemMemory.m_pBuf = (  char  *  )( *( pQ->pfQElemNew ) )( size )  )  )  goto  errLabel;
		  }
	}
	if  (  pQNode->qElemMemory.m_pBuf  &&  size  )  {
		memcpy( pQNode->qElemMemory.m_pBuf, pQElem, size );
		pQNode->size  =  size;
	}
	//
	if  (  pQ->pfQElemInit  &&  pQ->pfQElemInit(  pQNode->qElemMemory.m_pBuf  )  )  goto  errLabel;	
	pQNode->ucbQElemInited  =  TRUE;

	//  if  (  pQ->cfg.mutexName_prefix[0]  &&  syncObj.sync(  pQ->cfg.mutexName_prefix  )  )  goto  errLabel;
    if  (  pQ->bNeedSync  &&  syncObj.sync(  &pQ->syncObjParam,  pQ->pMutex,  (TCHAR*)_T(  "qSendMsg.706"  )))  goto  errLabel;

#ifdef  __DUAL_LINK_LIST__
	    pQNode->pPrev  =  &pQ->front;
#endif
	pQNode->pNext  =  pQ->front.pNext;
	pQ->front.pNext  =  pQNode;

	pQ->uiQNodes  ++  ;

	return  0;

errLabel:

	if  (  pQNode  )  qNodeFree(  pQ,  pQNode,  FALSE  );
	return  -1;

}



#if  0	//  2011/07/16
 extern  "C"  int  qGetMsg(  GENERIC_Q  *  pQ,  Q_ELEM_T  *  pQElem,  unsigned  int  *  puiSize  )
{
	Q_NODE		*	pQNode		=	NULL;
	
	if  (  !pQ  ||  !pQElem  ||  !puiSize  )  return  -1;

	{
		CQySyncObj		syncObj;

		//  if  (  pQ->cfg.mutexName_prefix[0]  &&  syncObj.sync(  pQ->cfg.mutexName_prefix  )  )  return  -1;
		if  (  pQ->bNeedSync  &&  syncObj.sync(  &pQ->syncObjParam  )  )  return  -1;

		if  (  !pQ  ||  !pQ->front.pNext  )  return  -1;
	
		pQNode  =  pQ->front.pNext;
		//
		if  (  *puiSize  <  pQNode->size  )  {
			TCHAR	tBuf[255  +  1]		=	_T(  ""  );
			_sntprintf(  tBuf,  mycountof(  tBuf  ),  _T(  "qGetMsg failed:  *puiSize %d < pQNode->size %d\n"  ),  *puiSize,  pQNode->size  );
			OutputDebugString(  tBuf  );
			return  CONST_qyRet_bufTooSmall;	//  -1;
		}
		//
		if  (  pQ->front.pNext->pNext  )  {
			pQ->front.pNext  =  pQ->front.pNext->pNext;
		    #ifdef  __DUAL_LINK_LIST__
					pQ->front.pNext->pPrev = &pQ->front;
			#endif
			}
		else  {
			  pQ->front.pNext  =  NULL;
			  pQ->pRear  =  &pQ->front;
		}

		pQ->uiQNodes --;

	}

	memcpy(  pQElem,  pQNode->qElemMemory.m_pBuf,  pQNode->size );
	*puiSize  =  pQNode->size;
	
	pQNode->ucbQElemInited  =  FALSE;		//  2007/06/10, ÕâÀï½«ucbQElemInitedÖÃÎªFALSE£¬¾ÍÊÇ½«qElemExitµÄ¹¤×÷½»¸øÈ¡³ömsgµÄ³ÌÐòÀ´½øÐÐ

	qNodeFree(  pQ,  pQNode,  FALSE  );

	return  0;

}
#endif

 static int  tmp_qGetMsg(  GENERIC_Q  *  pQ,  Q_ELEM_T  *  pQElem,  unsigned  int  *  puiSize,  BOOL  bNotShowInfo  )
{
	Q_NODE		*	pQNode		=	NULL;
	
	if  (  !pQ  ||  !pQElem  ||  !puiSize  )  return  -1;

	{
#if  0
		CQySyncObj		syncObj;

		//  if  (  pQ->cfg.mutexName_prefix[0]  &&  syncObj.sync(  pQ->cfg.mutexName_prefix  )  )  return  -1;
		if  (  pQ->bNeedSync  &&  syncObj.sync(  &pQ->syncObjParam  )  )  return  -1;
#endif

		if  (  !pQ  ||  !pQ->front.pNext  )  return  -1;
	
		pQNode  =  pQ->front.pNext;
		//
		if  (  *puiSize  <  pQNode->size  )  {
			if  (  !bNotShowInfo  )  {
				#ifdef  __DEBUG__
						TCHAR	tBuf[255  +  1]		=	_T(  ""  );
						_sntprintf(  tBuf,  mycountof(  tBuf  ),  _T(  "qGetMsg failed:  *puiSize %d < pQNode->size %d\n"  ),  *puiSize,  pQNode->size  );
						OutputDebugString(  tBuf  );
				#endif
				#ifdef  _UNICODE		//  2011/07/22
						qyShowInfo_internal(  &pQ->ctxQShowInfo,  CONST_qyShowType_warning,  0,  "",  pQ->cfg.name,  NULL,  NULL,  _T(  ""  ),  _T(  "qGetMsg failed:  *puiSize %d < pQNode->size %d"  ),  *puiSize,  pQNode->size  );
				#endif
			}
			return  CONST_qyRet_bufTooSmall;	//  -1;
		}
		//
		if  (  pQ->front.pNext->pNext  )  {
			pQ->front.pNext  =  pQ->front.pNext->pNext;
		    #ifdef  __DUAL_LINK_LIST__
					pQ->front.pNext->pPrev = &pQ->front;
			#endif
			}
		else  {
			  pQ->front.pNext  =  NULL;
			  pQ->pRear  =  &pQ->front;
		}

		pQ->uiQNodes --;

	}

	memcpy(  pQElem,  pQNode->qElemMemory.m_pBuf,  pQNode->size );
	*puiSize  =  pQNode->size;
	
	pQNode->ucbQElemInited  =  FALSE;		//  2007/06/10, ÕâÀï½«ucbQElemInitedÖÃÎªFALSE£¬¾ÍÊÇ½«qElemExitµÄ¹¤×÷½»¸øÈ¡³ömsgµÄ³ÌÐòÀ´½øÐÐ

	qNodeFree(  pQ,  pQNode,  FALSE  );

	return  0;

}


  extern  "C"  __declspec(  dllexport  )  int  qGetMsg(  GENERIC_Q  *  pQ,  Q_ELEM_T  *  pQElem,  unsigned  int  *  puiSize  )
{
	if  (  !pQ  ||  !pQElem  ||  !puiSize  )  return  -1;

	CQySyncObj		syncObj;

    if  (  pQ->bNeedSync  &&  syncObj.sync(  &pQ->syncObjParam,  pQ->pMutex,  (TCHAR*)_T(  "qGetMsg.843"  )))  return  -1;

	return  tmp_qGetMsg(  pQ,  pQElem,  puiSize,  FALSE  );
}


 //  2011/07/15. 一次性读取多个包。

 extern  "C"  __declspec(  dllexport  )  int  qGetMsgs(  GENERIC_Q  *  pQ,  Q_ELEM_T  *  pQElems[],  unsigned  int  *puiSizes,  unsigned  int  *  pnQElems  )	
{
	if  (  !pQ  ||  !pQElems  ||  !puiSizes  ||  !pnQElems  )  return  -1;

	int  i;

	CQySyncObj		syncObj;

    if  (  pQ->bNeedSync  &&  syncObj.sync(  &pQ->syncObjParam,  pQ->pMutex,  (TCHAR*)_T(  "qGetMsgs.859"  )))  return  -1;

	for  (  i  =  0;  i  <  (int)*pnQElems;  i  ++  )  {
		 if  (  tmp_qGetMsg(  pQ,  pQElems[i],  &puiSizes[i],  FALSE  )  )  break;
	}
	*pnQElems  =  i;

	//  OutputDebugString(  _T(  "qGetMsgs: Not finished\n"  )  );
	return  i  ?  0  :  -1;
}


//  2011/12/10, 一次性读取多个包，并存放在一个pkg里
 //  *puiSizes[i]输出为长度
 //  *pnQElems为puiSizes的成员数，也因此表示最多一次性读取多少个包，输出真实读取的包数. 
 extern  "C"  __declspec(  dllexport  )  int  qGetMsgPkg(  GENERIC_Q  *  pQ,  void  *  pPkg,  unsigned  int  uiSize_pkg,  unsigned  int  *uiLens,  unsigned  int  *  pnLens  )
{
	if  (  !pQ  ||  !pPkg  ||  !uiSize_pkg  ||  !uiLens  ||  !pnLens  )  return  -1;

	int			i;
	BOOL		bNotShowInfo  =  FALSE;

	CQySyncObj		syncObj;

    if  (  pQ->bNeedSync  &&  syncObj.sync(  &pQ->syncObjParam,  pQ->pMutex,  (TCHAR*)_T(  "qGetMsgPkg.883"  )))  return  -1;
	
	for  (  i  =  0;  i  <  (int)*pnLens  &&  uiSize_pkg;  i  ++  )  {
		 uiLens[i]  =  uiSize_pkg;
		 if  (  tmp_qGetMsg(  pQ,  pPkg,  &uiLens[i],  bNotShowInfo  )  )  break;
		 pPkg  =  (  (  char  *  )pPkg  )  +  uiLens[i];
		 uiSize_pkg  -=  uiLens[i];
		 bNotShowInfo  =  TRUE;
	}
	*pnLens  =  i;

	//  OutputDebugString(  _T(  "qGetMsgs: Not finished\n"  )  );
	return  i  ?  0  :  -1;
}


 //
 static  void  tmpRemoveAQNode(  GENERIC_Q  *  pQ,  Q_NODE  *  pQNode  )
{
	 if  (  !pQ  ||  !pQNode  )  return;

	 if  (  pQNode  ==  &pQ->front  )  return;		//  ¶Ôfront½Úµã£¬²»ÄÜÒÆ³ý

	 if  (  pQNode->pNext  )  {
		 pQNode->pNext->pPrev  =  pQNode->pPrev;
		 if  (  pQNode->pPrev  )  pQNode->pPrev->pNext  =  pQNode->pNext;
		 else  pQ->front.pNext  =  pQNode->pNext;				
		}
	 else  {
		   if  (  pQNode->pPrev  )  {
				pQNode->pPrev->pNext  =  NULL;
				pQ->pRear  =  pQNode->pPrev;
				}
		   else  {
			     pQ->front.pNext = NULL;
				 pQ->pRear  =  &pQ->front;
		   }
	 }

	 pQNode->pNext  =  NULL;
	 qNodeFree(  pQ,  pQNode,  TRUE  );

	 pQ->uiQNodes  -- ;

	 return;

}


 extern  "C"  __declspec(  dllexport  )  int  qRemoveMsg(  GENERIC_Q  *  pQ,  PF_bCommonHandler pf_bRemoveCond,  void  *  p0,  void  *  p1  )
{
	int					iErr		=	-1;
	Q_NODE			*	pQNode		=  NULL;
	CQySyncObj			syncObj;
	BOOL				tmpbRet		=	FALSE;
	Q_NODE			*	tmppQNode	=	NULL;


	if  (  !pQ  ||  !pf_bRemoveCond  )  return  -1;

	//  if  (  pQ->cfg.mutexName_prefix[0]  &&  syncObj.sync(  pQ->cfg.mutexName_prefix  )  )  return  -1;
    if  (  pQ->bNeedSync  &&  syncObj.sync(  &pQ->syncObjParam,  pQ->pMutex,  (TCHAR*)_T(  "qRemoveMsg.944"  )))  return  -1;

	for  (  pQNode  =  pQ->front.pNext;  pQNode;  )  {
		 tmpbRet  =  (  *  pf_bRemoveCond  )(  p0,  p1,  pQNode->qElemMemory.m_pBuf  );
		 if  (  !tmpbRet  )  {
			 pQNode  =  pQNode->pNext;  continue;
		 }
		 tmppQNode  =  pQNode;
		 pQNode  =  pQNode->pNext;
		 tmpRemoveAQNode(  pQ,  tmppQNode  );
		 continue;
	}
	
	iErr  =  0;
//  errLabel:
	return  iErr;

}

 extern  "C"  __declspec(  dllexport  )  void  emptyGenericQ(  GENERIC_Q  *  pQ  )
{
	Q_NODE		*	pQNode,  *  tmppQNode;
	CQySyncObj		syncObj;

	if  (  !pQ  )  return;
	
	//  if  (  pQ->cfg.mutexName_prefix[0]  &&  syncObj.sync(  pQ->cfg.mutexName_prefix  )  )  goto  errLabel;
    if  (  pQ->bNeedSync  &&  syncObj.sync(  &pQ->syncObjParam,  pQ->pMutex,  (TCHAR*)_T(  "emptyGenericQ.971"  )))  goto  errLabel;

	for  (  pQNode  =  pQ->front.pNext;  pQNode;  )  {
		 //
         if  (  pQ->hSemaTrigger  ) {

#ifndef  __LINUX__
             WaitForSingleObject(  pQ->hSemaTrigger,  0  );		//  ¶ÔÓÚÊ¹ÓÃ´¥·¢»úÖÆµÄ¶ÓÁÐ£¬ÏÈÒª°Ñ´¥·¢¸ø¼õÉÙÁË
#endif
         }
		 //
		 tmppQNode  =  pQNode->pNext;
		 qNodeFree(  pQ,  pQNode,  TRUE );
		 pQNode  =  tmppQNode;	 
	}

	pQ->front.pNext  =  NULL;
	pQ->pRear  =  &pQ->front;
	pQ->uiQNodes  =  0;
	
errLabel:

	return;
	
}

 extern  "C"  __declspec(  dllexport  )  int  qTraverse(  GENERIC_Q  *  pQ,  PF_commonHandler pf_visit,  void  *  p0,  void  *  p1  )
{
	int					iErr		=	-1;
	Q_NODE			*	pQNode		=  NULL;
	CQySyncObj			syncObj;
	int					tmpiRet		=	-1;


	if  (  !pQ  ||  !pf_visit  )  return  -1;

	//  if  (  pQ->cfg.mutexName_prefix[0]  &&  syncObj.sync(  pQ->cfg.mutexName_prefix  )  )  return  -1;
    if  (  pQ->bNeedSync  &&  syncObj.sync(  &pQ->syncObjParam,  pQ->pMutex,  (TCHAR*)_T(  "qTraverse.1003"  )))  return  -1;

	for  (  pQNode  =  pQ->front.pNext;  pQNode;  pQNode  =  pQNode->pNext  )  {
		 tmpiRet  =  (  *  pf_visit  )(  p0,  p1,  pQNode->qElemMemory.m_pBuf  );
		 if  (  tmpiRet  )  {
			 iErr  =  tmpiRet;  goto  errLabel;
		 }
	}
	
	iErr  =  0;
errLabel:
	return  iErr;

}

#if  0

 BOOL  isQFull(  GENERIC_Q  *  pQ  )	//  ÒòÎªÖ»ÒªÅÐ¶ÏpQ->uiQNodesÒ»¸öÊýÖµ£¬ËùÒÔ£¬²»ÐèÒªÓÃÍ¬²½ÊÖ¶Î¡£
{
	if  (  !pQ  )  return  FALSE;
	return  (  pQ->cfg.uiMaxQNodes  &&  pQ->uiQNodes  >=  pQ->cfg.uiMaxQNodes  );
 }

BOOL  isQWarning(  GENERIC_Q  *  pQ,  unsigned  int  uiQNodes_warningInterval  )		//  ÅÐ¶ÏÊÇ·ñ¶ÓÁÐµÄ³ÉÔ±¿ÕÏÐ¿Õ¼ä¡´uiQNodes_interval
{
	if  (  !pQ  )  return  FALSE;
	return  (  pQ->cfg.uiMaxQNodes  &&  pQ->uiQNodes  +  uiQNodes_warningInterval  >=  pQ->cfg.uiMaxQNodes  );
}

 BOOL  isQEmpty(  GENERIC_Q  *  pQ  )	//  ÒòÎªÖ»ÒªÅÐ¶ÏpQ->front.pNextÒ»¸öÊýÖµ£¬ËùÒÔ£¬²»ÐèÒªÊ¹ÓÃÍ¬²½ÊÖ¶Î¡£2008/03/21
{
	BOOL			bRet	=	FALSE;

	if  (  pQ->front.pNext  )  goto  errLabel;
	
	bRet  =  TRUE;

errLabel:
	return  bRet;
}
#endif




/*

 unsigned int qLength( GENERIC_Q *pQ )
{
	Q_NODE *pQNode;
	unsigned int len = 0;

	for ( pQNode = pQ->pFront; pQNode; pQNode = pQNode->pNext )
		len ++;

	return len;
}

int peekMsg( GENERIC_Q *pQ, Q_ELEM_T *pQElem, int size )
{
	if ( !pQ || !pQ->pFront) return -1;
	
	memcpy( pQElem, pQ->pFront->qElemMemory.m_pBuf, size );

	return 0;

}

*/

/////////////////////
 

 __declspec(  dllexport  )  int removeAQNode( GENERIC_Q *pQ, Q_NODE *pQNode )
{
	CQySyncObj			syncObj;

    if  (  pQ->bNeedSync  &&  syncObj.sync(  &pQ->syncObjParam,  pQ->pMutex,  (TCHAR*)_T(  "removeAQNode.1085"  )))  return  -1;

	tmpRemoveAQNode(  pQ,  pQNode  );
	return  0;
}

 
 __declspec(  dllexport  )  int  preserveLastQNode( GENERIC_Q *pQ )
{
	if  (  !pQ  )  return  -1;
	pQ->pLastQNode = pQ->pRear;
	return  0;
}

 __declspec(  dllexport  )  int restoreLastQNode( GENERIC_Q *pQ )
{
	 int		iErr		=	-1;

	 if  (  !pQ->pLastQNode  )  {
		 //  qNodeFree( pQ->pFront, pQ->pfQElemFree );
		 //  pQ->pFront = pQ->pRear = NULL;
		 emptyGenericQ(  pQ  );
		 }
	 else  if  (  pQ->pLastQNode->pNext  )  {
			   Q_NODE		*	pQNode,  *  tmppQNode;
			   CQySyncObj		syncObj;

			   //  qNodeFree( pQ->pLastQNode->pNext, pQ->pfQElemFree );

               if  (  pQ->bNeedSync  &&  syncObj.sync(  &pQ->syncObjParam,  pQ->pMutex,  (TCHAR*)_T(  "restoreLastQNodes.1114"  )))  goto  errLabel;

			   for  (  pQNode  =  pQ->pLastQNode->pNext;  pQNode;  )  {
				    //
                    if  (  pQ->hSemaTrigger  )  {
#ifndef  __LINUX__
                        WaitForSingleObject(  pQ->hSemaTrigger,  0  );		//  ¶ÔÓÚÊ¹ÓÃ´¥·¢»úÖÆµÄ¶ÓÁÐ£¬ÏÈÒª°Ñ´¥·¢¸ø¼õÉÙÁË
#endif
                    }
					//
					tmppQNode  =  pQNode->pNext;
					qNodeFree(  pQ,  pQNode,  TRUE );
					pQNode  =  tmppQNode;	 
			   }

			   pQ->pLastQNode->pNext = NULL;
			   // pLastQNode will keep the real last QNode value of pQ, 
			   // so pLastQNode->pNext is NULL 
			   pQ->pRear = pQ->pLastQNode;
			   }
	 else 
		 // do nothing;

	 iErr  =  0;
errLabel:
	 return  iErr;
}


 __declspec(  dllexport  )  void stopPreserveLastQNode( GENERIC_Q *pQ )
{
	 pQ->pLastQNode = pQ->pRear;
}


 __declspec(  dllexport  )  void link2Q( GENERIC_Q *pSrcQ, GENERIC_Q *pDesQ )
{
	 if ( !pSrcQ || !pDesQ ) return;

	 if ( !pSrcQ->front.pNext ) return;

	 if ( !pDesQ->pRear ) {
		 pDesQ->front.pNext = pSrcQ->front.pNext;
		}
	 else {
		    pDesQ->pRear->pNext = pSrcQ->front.pNext;
			pSrcQ->front.pNext->pPrev = pDesQ->pRear;
	 }
	 
	 pDesQ->pRear = pSrcQ->pRear;
	 pSrcQ->pRear = pSrcQ->front.pNext = NULL;

	 pDesQ->uiQNodes = pDesQ->uiQNodes + pSrcQ->uiQNodes;

	 return;

}

 #ifdef  __DEBUG__

/*
 int insertMsg( GENERIC_Q *pQ, Q_NODE *pFollowed, Q_ELEM_T *pQElem, int size )
{
	Q_NODE *pQNode = NULL;

	if ( !pQ ) return -1;
	
	if ( !pFollowed ) return sendMsg( pQ, pQElem, size );
	
	if ( !( pQNode = (  Q_NODE  *  )qNodeNew( ) ) ) return -1;
	if ( !( pQNode->qElemMemory.m_pBuf = pQ->pfQElemNew( size ) ) ) goto errLabel;
	memcpy( pQNode->qElemMemory.m_pBuf, pQElem, size );

	if ( pFollowed->pNext ) {
		pQNode->pNext = pFollowed->pNext;
		pFollowed->pNext->pPrev = pQNode;
		pFollowed->pNext = pQNode;
		}
	else {
		pFollowed->pNext = pQNode;
		pQ->pRear = pQNode;
	}

	pQ->nQNoes ++;

	return 0;

errLabel:

	qNodeFree( pQNode, pQ->pfQElemFree );

	return -1;

}



*/


 extern  "C"  __declspec(  dllexport  )  int  q_doTest(  )
 {
	 //qyShowInfo_internal(  &sCtxQShowInfo,  0,  NULL,  "",  NULL,  NULL,  NULL,  _T(  "K987k"  ),  _T(  "k789k"  )  );
	 return  0;
 }


#endif


