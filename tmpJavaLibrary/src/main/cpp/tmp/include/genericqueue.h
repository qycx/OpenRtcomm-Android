

#ifndef  __GENERICQUEUE_H__ 
#define  __GENERICQUEUE_H__

#include    "myTypes.h"


#ifdef  __cplusplus
  extern  "C"  {
#endif

//
#include	"ctx_q_showInfo.h"


//
#define  CONST_qType_decA		1
#define  CONST_qType_playA		2
//
#define	CONST_qType_showInfo	10



//
#define __DUAL_LINK_LIST__

 #define  OBJ_T		void
 
 #define  Q_ELEM_T	void

typedef  struct  __q_node_t							{

				  //  Q_ELEM_T						*	pQElem;
				  QY_MEMORY							qElemMemory;
				  unsigned  int						size;
				  //
				  unsigned  char					ucbQElemInited;

				  #ifdef  __DUAL_LINK_LIST__
						  struct  __q_node_t	*	pPrev;	
				  #endif

				  struct  __q_node_t			*	pNext;

				  //  unsigned  int						uiSeqNo;				//  ÏÂ´Î¼ÓÈëÁ¬±íµÄ½ÚµãµÄÐòºÅ£»

}		 Q_NODE;


//  sun½éÉÜ£º¶ÓÁÐÖÐÃ¿¸ö½ÚµãÓÐÒ»¸ö²»ÖØ¸´µÄÐòºÅ£¬´ËÐòºÅºÍmutexName_prefixÒ»Æð£¬ÓÃÓÚÔÚ¶ÓÁÐ½ÚµãÔöÉ¾²Ù×÷Ê±µÄÍ¬²½
//  ¿ÉÏ§´ËÏî¼¼Êõ»¹ÓÐµãÓÃ²»ÉÏ¡£2007/04/17


//  typedef  Q_ELEM_T  *  (  *  PF_qElemNew  )(  unsigned  int  );
typedef  Q_ELEM_T  *  (  *  PF_qElemNew  )(  size_t  );
typedef  void  (  *  PF_qElemFree  )(  Q_ELEM_T  *  p  );
//
typedef  char  *  (  *  PF_qElemNewEx  )(  int  iAllocType,  size_t  size,  QY_MEMORY  *  p  );	//  2010/04/24
typedef  void  (  *  PF_qElemFreeEx  )(  QY_MEMORY  *  p  );								//  2010/04/24
//
typedef  int  (  *  PF_qElemInit  )(  Q_ELEM_T  *  p  );
typedef  void  (  *  PF_qElemExit  )(  Q_ELEM_T  *  p  );
typedef  void  (  *  PF_qElemRemove  )(  Q_ELEM_T  *  p,  LPCTSTR  hint  );
//
typedef  void  (  *  PF_qElemRemoveEx  )(  Q_ELEM_T  *  p,  size_t  size,  LPCTSTR  hint  );	//  2012/11/10



typedef  struct  __genericQCfg_t					{
				 TCHAR								name[24];					//  ¶ÓÁÐÃû³Æ, ÓÃÔÚµ÷ÊÔµÈ¹¤×÷ÓÃ
				 unsigned  int						uiMaxQNodes;				//  ¶Ô¶ÓÁÐÖÐµÄ½ÚµãÊý×öÏÞÖÆ£¬Îª0Ê±²»ÏÞÖÆ

				 //
				 int								m_qType;

				 //
				 TCHAR								mutexName_prefix[32];		//  ¶ÓÁÐÖÐµÄÔöÉ¾´¦Àí½ÚµãÍ¬²½ÓÃ

				 //
				 unsigned  int						uiMaxCnt_semaTrigger;		//  ¶ÔÓÃÔÚÏß³Ì´¦Àí×ö´¥·¢ÓÃµÄsemaphore, Îª0Ê±²»ÓÃ
				 				 
				 //
				 BOOL								bUseEvt;
				 TCHAR								evt0Name[24];				//  2009/12/22
				 
}		 GENERIC_Q_CFG;


//  2012/11/10
typedef  struct  __genericQCfgEx_t					{
				 GENERIC_Q_CFG						common;
				 //
				 PF_qElemNewEx						pfQElemNewEx;
				 PF_qElemNew						pfQElemNew;
				 PF_qElemInit						pfQElemInit;
				 PF_qElemExit						pfQElemExit;
				 PF_qElemFreeEx						pfQElemFreeEx;
				 PF_qElemFree						pfQElemFree;
				 PF_qElemRemoveEx					pf_QElemRemoveEx;
				 PF_qElemRemove						pfQElemRemove;

}		 GENERIC_Q_cfgEx;




//
typedef  struct  __generic_q_t						{

				 GENERIC_Q_CFG						cfg;

				 Q_NODE								front;
				 Q_NODE							*	pRear;
				 //				 
				 PF_qElemNew						pfQElemNew;
				 PF_qElemFree						pfQElemFree;
				 //
				 PF_qElemNewEx						pfQElemNewEx;				//  2010/04/24
				 PF_qElemFreeEx						pfQElemFreeEx;				//  2010/04/24
				 //
				 PF_qElemInit						pfQElemInit;				//  2007/06/10
				 PF_qElemExit						pfQElemExit;				//  
				 //
				 PF_qElemRemove						pfQElemRemove;				//  2009/12/08
				 //
				 PF_qElemRemoveEx					pfqElemRemoveEx;			//  2012/11/10

				 //
				 unsigned  int						uiQNodes;

				 //
				 HANDLE								hSemaTrigger;				//  2007/05/08				 
				 long								lPrevCnt_semaTrigger;		//  2007/05/08, Ã»ÓÐÑÏ¸ñÒâÒå£¬ÓÃÀ´debugÓÃ¡£

				 //
				 HANDLE								hEvent;

				 //				 	
				 unsigned  char						ucbTriggerErr;				//  2008/11/21. ÔÚÊ¹ÓÃ´¥·¢»úÖÆµÄ¶ÓÁÐÖÐ£¬µ±´¥·¢Æ÷ÂúÁË£¬ÉèÖÃ´ËÎ»¡£ÓÐÀûÓÚ¼°Ê±µ÷ÓÃÏà¹Øº¯Êý½«´¥·¢Æ÷»Ö¸´
				 
				 //
				 BOOL								bNeedSync;

#if  0
#ifndef  __LINUX__
				 CRITICAL_SECTION					cs;
#endif
				 BOOL								bCsInited;
#endif 

				 //
				 SYNC_OBJ_PARAM						syncObjParam;
                 MyMutex                        *   pMutex;


				 //  2012/07/14
				 BOOL								bInited;

				 //  2009/09/20 
				 Q_NODE							*	pLastQNode;

				 //
				 CTX_q_showInfo						ctxQShowInfo;

				 //
 }		GENERIC_Q;


//
__declspec(  dllexport  )  int  set_q_showInfo(  PF_showInfo_open  pf_showInfo  );
__declspec(  dllexport  )  void  clear_q_showInfo(  );
//
__declspec(  dllexport  )  int  tmp_showInfo_open_internal(  void  *  p0,  LPCTSTR  tWhere,  LPCTSTR  hint  );



//
 extern __declspec(dllexport) OBJ_T *objNew( int size );
 extern __declspec(dllexport) void objFree( OBJ_T *p );
 #define qNodeNew( ) objNew( sizeof( Q_NODE ) )

//  void qNodeFree(  GENERIC_Q  *  pQ,  Q_NODE *pQNode  );


__declspec(  dllexport  )  GENERIC_Q  *  genericQNew(  LPCTSTR  qName,  LPCTSTR  mutexName_prefix,  unsigned  int  uiMaxCnt_sema,  unsigned  int  uiMaxQNodes,  PF_qElemNew  pfQElemNew,  PF_qElemFree  pfQElemFree  );
__declspec(  dllexport  )  void genericQFree( GENERIC_Q *pQ );
__declspec(  dllexport  )  void exitGenericQ( GENERIC_Q *pQ );
//
int  qPostMsgEx(  Q_ELEM_T  *  pQElem,  int  iAllocType,  unsigned  int  size,  GENERIC_Q  *  pQ,  LPCTSTR  hint  );
//
__declspec(  dllexport  )  int  qPostMsg(  Q_ELEM_T  *  pQElem,  unsigned  int  size,  GENERIC_Q  *  pQ,  LPCTSTR  hint  );
__declspec(  dllexport  )  int  qPostMsgAndTrigger(  Q_ELEM_T  *  pQElem,  unsigned  int  size,  GENERIC_Q  *  pQ,  LPCTSTR  hint  );
__declspec(  dllexport  )  int  qAvoidTriggerTooHigh(  LPCTSTR  mutexName_toAvoidTriggerTooHigh,  GENERIC_Q  *  pQ  );

int  qSendMsg(  Q_ELEM_T  *  pQElem,  int  size,  GENERIC_Q  *  pQ  );
__declspec(  dllexport  )  int  qGetMsg(  GENERIC_Q  *  pQ,  Q_ELEM_T  *  pQElem,  unsigned  int  *puiSize  );
__declspec(  dllexport  )  int  qGetMsgs(  GENERIC_Q  *  pQ,  Q_ELEM_T  *  pQElems[],  unsigned  int  *puiSizes,  unsigned  int  *  pnQElems  );	//  2011/07/15
__declspec(  dllexport  )  int  qGetMsgPkg(  GENERIC_Q  *  pQ,  void  *  pPkg,  unsigned  int  uiSize_pkg,  unsigned  int  *uiLens,  unsigned  int  *  pnLens  );
__declspec(  dllexport  )  int  qRemoveMsg(  GENERIC_Q  *  pQ,  PF_bCommonHandler pf_bRemoveCond,  void  *  p0,  void  *  p1  );
__declspec(  dllexport  )  void  emptyGenericQ( GENERIC_Q *pQ );
//
//  __declspec(  dllexport  )  int  qTraverse(  GENERIC_Q  *  pQ,  PF_commonHandler pf_visit,  void  *  p0,  void  *  p1  );
__declspec(  dllexport  )  int  qTraverse(  GENERIC_Q  *  pQ,  PF_commonHandler pf_visit,  void  *  p0,  void  *  p1  );
//
#if  0
	BOOL  isQFull(  GENERIC_Q  *  pQ  );
	BOOL  isQWarning(  GENERIC_Q  *  pQ,  unsigned  int  uiQNodes_warningInterval  );		//  ÅÐ¶ÏÊÇ·ñ¶ÓÁÐµÄ³ÉÔ±¿ÕÏÐ¿Õ¼ä¡´uiQNodes_interval
	BOOL  isQEmpty(  GENERIC_Q  *  pQ  );
#endif

#define  isQFull(  pQ  )  (  !(  pQ  )  ?  FALSE  :  (  (  pQ  )->cfg.uiMaxQNodes  &&  (  pQ  )->uiQNodes  >=  (  pQ  )->cfg.uiMaxQNodes  )  )
#define  isQWarning(  pQ,  uiQNodes_warningInterval  )  (  !(  pQ  )  ?  FALSE  :  (  (  pQ  )->cfg.uiMaxQNodes  &&  (  pQ  )->uiQNodes  +  uiQNodes_warningInterval  >=  (  pQ  )->cfg.uiMaxQNodes  )  )
#define  isQEmpty(  pQ  )  (  (  (  pQ  )->front.pNext  )  ?  FALSE  :  TRUE  )



/*
 extern __declspec(dllexport) unsigned int qLength( GENERIC_Q *pQ );
 extern __declspec(dllexport) int peekMsg( GENERIC_Q *pQ, Q_ELEM_T *pQElem, int size );
 */

#define getpLastQNode( pQ ) pQ->pRear

__declspec(  dllexport  )  int removeAQNode( GENERIC_Q *pQ, Q_NODE *pQNode );
__declspec(  dllexport  )  int preserveLastQNode( GENERIC_Q *pQ );
__declspec(  dllexport  )  int restoreLastQNode( GENERIC_Q *pQ );
__declspec(  dllexport  )  void stopPreserveLastQNode( GENERIC_Q *pQ );
__declspec(  dllexport  )  void link2Q( GENERIC_Q *pSrcQ, GENERIC_Q *pDesQ );
 /*
 extern __declspec(dllexport) int insertMsg( GENERIC_Q *pQ, Q_NODE *pFollowed, Q_ELEM_T *pInserted, int size );
*/




#ifdef __cplusplus
 }
#endif


//  2012/11/10
__declspec(  dllexport  )  int  initGenericQ(  LPCTSTR  qName,  LPCTSTR  mutexName_prefix,  unsigned  int  uiMaxCnt_semaTrigger,  unsigned  int  uiMaxQNodes,  PF_qElemNewEx  pfQElemNewEx,  PF_qElemNew  pfQElemNew,  PF_qElemInit  pfQElemInit,  PF_qElemExit  pfQElemExit,  PF_qElemFreeEx  pfQElemFreeEx,  PF_qElemFree  pfQElemFree,  PF_qElemRemoveEx  pfQElemRemoveEx,  PF_qElemRemove  pfQElemRemove,  GENERIC_Q  *  pQ  );

__declspec(  dllexport  )  int  initGenericQ(  LPCTSTR  qName,  LPCTSTR  mutexName_prefix,  unsigned  int  uiMaxCnt_semaTrigger,  unsigned  int  uiMaxQNodes,  PF_qElemNewEx  pfQElemNewEx,  PF_qElemNew  pfQElemNew,  PF_qElemInit  pfQElemInit,  PF_qElemExit  pfQElemExit,  PF_qElemFreeEx  pfQElemFreeEx,  PF_qElemFree  pfQElemFree,  PF_qElemRemove  pfQElemRemove,  GENERIC_Q  *  pQ  );

__declspec(  dllexport  )  int  initGenericQ(  LPCTSTR  qName,  LPCTSTR  mutexName_prefix,  unsigned  int  uiMaxCnt_semaTrigger,  unsigned  int  uiMaxQNodes,  PF_qElemNew  pfQElemNew,  PF_qElemFree  pfQElemFree,  GENERIC_Q  *  pQ  );
//
__declspec(  dllexport  )  int  initGenericQ(  GENERIC_Q_CFG  *  pCfg,  PF_qElemNew  pfQElemNew,  PF_qElemInit  pfQElemInit,  PF_qElemExit  pfQElemExit,  PF_qElemFree  pfQElemFree,  PF_qElemRemove  pfQElemRemove,  GENERIC_Q  *  pQ  );

//  2010/04/25
__declspec(  dllexport  )  int  initGenericQ(  GENERIC_Q_CFG  *  pCfg,  PF_qElemNewEx  pfQElemNewEx,  PF_qElemNew  pfQElemNew,  PF_qElemInit  pfQElemInit,  PF_qElemExit  pfQElemExit,  PF_qElemFreeEx  pfQElemFreeEx,  PF_qElemFree  pfQElemFree,  PF_qElemRemove  pfQElemRemove,  GENERIC_Q  *  pQ  );

//  2012/11/10
__declspec(  dllexport  )  int  initGenericQ(  GENERIC_Q_cfgEx  *  pCfg,  GENERIC_Q  *  pQ  );


//  2014/06/20
typedef  int  (  *  PF_qPostMsg  )(  Q_ELEM_T  *  pQElem,  unsigned  int  size,  GENERIC_Q  *  pQ  );
typedef  int  (  *  PF_qGetMsg  )(  GENERIC_Q  *  pQ,  Q_ELEM_T  *  pQElem,  unsigned  int  *  puiSize  );


#ifdef  __DEBUG__
 extern  "C"  __declspec(  dllexport  )  int  q_doTest(  );
#endif



#endif /*} end of __GENERICQUEUE_H__ */



