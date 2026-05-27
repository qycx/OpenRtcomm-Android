
#ifndef  __QYSYNCCOMMPROC_H__
#define	 __QYSYNCCOMMPROC_H__	//  {

//
#include	"myTypes.h"

#include	"qySyncObj.h"




 void  qyExitProcess(  LPCTSTR  hint  );


class  CQySyncFlg  {			//  2011/03/28. 一般只在一个线程中使用，在某些窗口函数要经过很多消息处理才能完成时，依靠这个来做同步。
								//  hWnd是包含bFlg成员的窗口, 在bQuitDlg时从有效变成无效,所以需要判断下

		struct			{
			BOOL	*	pbFlg;
		}				m_var;

public:
		CQySyncFlg  (  ){  memset(  &m_var,  0,  sizeof(  m_var  )  );  }
		~CQySyncFlg(  )  {  
			if  (  m_var.pbFlg  )  (  *m_var.pbFlg  )  =  0;  
		}

		int  sync(  BOOL  *  pbFlg  )	{  
			if  (  !pbFlg
				||  *pbFlg  
				||  m_var.pbFlg  )  
			{
				#ifdef  __DEBUG__
                        //OutputDebugString(  _T(  "syncFlg failed\n"  )  );
				#endif
				return  -1;
			}

			m_var.pbFlg  =  pbFlg;
			//
			(  *pbFlg  )  =  1;

			return  0;
		}

};		

#define		M_syncFlg_bQuitDlg_start(  pbFlg,  hWnd,  hint  )		BOOL  *  tmp_pbFlg_bQuitDlg  =  NULL;  HWND  tmp_hWnd_bQuitDlg  =  NULL;	\
                                                                    if  (  !pbFlg  ||  *pbFlg  ||  !myIsWindow(  hWnd  )  )  {					\
																		if  (  hint  )  qyShowInfo1(  CONST_qyShowType_qwmComm,  0,  (  ""  ),  _T(  "IsClient"  ),  0,  _T(  ""  ),  _T(  ""  ),  _T(  "%s"  ),  hint  );	\
																		goto  errLabel;															\
																	}																			\
																	tmp_pbFlg_bQuitDlg  =  pbFlg;  tmp_hWnd_bQuitDlg  =  hWnd;					\
																	*pbFlg  =  TRUE;

#define		M_syncFlg_bQuitDlg_end									if  (  myIsWindow(  tmp_hWnd_bQuitDlg  )  )  {  if  (  tmp_pbFlg_bQuitDlg  )  *tmp_pbFlg_bQuitDlg  =  FALSE;  }


//  2013/07/03
class  CQySyncCnt  {					//  2013/07/03. 多线程同步计数用

		struct			{
			long	*	plCnt;
			//
			TCHAR		tHint[32];		//  2013/08/06. fot debug
			BOOL		bDebug;
		}				m_var;

public:
		CQySyncCnt  (  ){  memset(  &m_var,  0,  sizeof(  m_var  )  );  }
		~CQySyncCnt(  )  {  
			if  (  m_var.plCnt  )  {
                //long  lRet  =  myInterlockedDecrement(  m_var.plCnt  );
                  myInterlockedDecrement(  m_var.plCnt  );

				#ifdef  __DEBUG__
						if  (  m_var.bDebug  )  {
							if  (  m_var.tHint[0]  )  {
                                TCHAR	tBuf[256];  tBuf[0]  =  0;//  =  _T(  ""  );
#ifndef  __LINUX__
								long lRet = 0;
								_sntprintf(  tBuf,  mycountof(  tBuf  ),  _T(  "--syncCnt: %d. %s\n"  ),  lRet,  m_var.tHint  );						
#endif
								OutputDebugString(  tBuf  );
							}
						}
				#endif

			}
		}

		int  sync(  long  *  plCnt,  LPCTSTR  hint  =  NULL  )	{  
			if  (  !plCnt
				||  m_var.plCnt  )  
			{
				#ifdef  __DEBUG__
						OutputDebugString(  _T(  "syncCnt failed\n"  )  );
				#endif
				return  -1;
			}

			m_var.plCnt  =  plCnt;
			//
            long  lRet  =  myInterlockedIncrement(  m_var.plCnt  );

			//
			#ifdef  __DEBUG__
					if  (  lRet  >=  4  )  {
						//  m_var.bDebug  =  TRUE;	
					}
					if  (  m_var.bDebug  )  {
                        if  (  hint  )  mylstrcpyn(  m_var.tHint,  hint,  mycountof(  m_var.tHint  )  );
						//
						if  (  m_var.tHint[0]  )  {
#ifndef  __LINUX__
							TCHAR	tBuf[256]  =  _T(  ""  );
							_sntprintf(  tBuf,  mycountof(  tBuf  ),  _T(  "++syncCnt: %d. %s\n"  ),  lRet,  m_var.tHint  );
							OutputDebugString(  tBuf  );					
#endif
						}
					}
			#endif

			return  0;
		}

};		


//
#define		CONST_uiInitCnt_sema_q2SyncFlg						20									//  2009/05/13

//
typedef  struct  __rwLock_param_t								{	
				 unsigned  int									uiMaxCnt_sema;		
				 unsigned  int									uiInitCnt_sema;												//  2009/05/10				 		
				 unsigned  int									uiMilliSeconds_mutex_r;
				 unsigned  int									uiMilliSeconds_sema_r;
				 unsigned  int									uiMilliSeconds_mutex_w;
				 unsigned  int									uiMilliSeconds_sema_w;					
}		 RW_lock_param;

//
int  RW_lock_param_init(  RW_lock_param  * p  );


//  2007/04/22, ÎªqmObjQÌá¹©¶ÁËøºÍÐ´Ëø
/*
class  CQyRWLock  {
 
		struct				{
			
			//  HANDLE			hMutex;				//  ÓÃÀ´Í¬²½Ð´Ëø£¬±£Ö¤Í¬Ê±¼äÖ»ÓÐÒ»¸öÐ´Ëø
			
			HANDLE			hSema;
			unsigned  int	uiInitCnt_sema;
			//			
			unsigned  int	nWaited;			//  ÔÚÐ´ËøÖÐ£¬nWaited±íÊ¾WaitForSingleObject³É¹¦µÄ´ÎÊý£¬ÔÚÎö¹¹ÀïÓÃÍ¬ÑùµÄReleaseÀ´»Ö¸´¡£

			BOOL			bLocked;

		}					m_var;
 public:
	    CQyRWLock(  );
		~CQyRWLock(  );

		int  rLock(  LPCTSTR  mutexName,  unsigned  int  uiMilliSeconds_mutex,  HANDLE  hSema,  unsigned  int  uiMilliSeconds_sema  );
		int  wLock(  LPCTSTR  mutexName,  unsigned  int  uiMilliSeconds_mutex,  HANDLE  hSema,  unsigned  int  uiMilliSeconds_sema,  unsigned  int  uiInitCnt_sema  );  
	
		void  unlock(  );
 };
 */

#include	<assert.h>


 class  CMutexRW  
{
protected:
	void	*	m_pEnv;
	BOOL		m_bSupported_srw;		//  2009/07/12

	HANDLE		m_semReaders;
	HANDLE		m_semWriters;
	int			m_nReaders;

	//  2009/07/12
#if  0
	SRWLOCK		m_srw;
#endif

#ifdef  __DEBUG__
		char  buf[333];
#endif

public:
	CMutexRW(  );
	virtual ~CMutexRW(  );
	
	//  2012/03/24. CMutexRW主要作为CQyRWLock的数据存储,所以成员函数不能被独立调用
	friend class  CQyRWLock;

private:
	int  Lock_DataRead(  unsigned  int  uiMilliSeconds_sema  =  INFINITE  );  
	void  Unlock_DataRead(  );  
	int  Lock_DataWrite(  unsigned  int  uiMilliSeconds_sema  =  INFINITE  );
 	void  Unlock_DataWrite(  );

};



class  CQyRWLock  {
		
		struct					{
			CMutexRW		*	m_pMutexRW;
			BOOL				m_bIsLocked;

			BOOL				m_bIsRlocked;
		}					m_var;

public:
	    CQyRWLock(  );
		~CQyRWLock(  );

		// int  rLock(  LPCTSTR  mutexName,  unsigned  int  uiMilliSeconds_mutex,  HANDLE  hSema,  unsigned  int  uiMilliSeconds_sema  );
		//  int  wLock(  LPCTSTR  mutexName,  unsigned  int  uiMilliSeconds_mutex,  HANDLE  hSema,  unsigned  int  uiMilliSeconds_sema,  unsigned  int  uiInitCnt_sema  );  

		int  rLock(  CMutexRW  *  p,  unsigned  int  uiMilliSeconds_sema  );
		int  wLock(  CMutexRW  *  p,  unsigned  int  uiMilliSeconds_sema  );  

		void  unlock(  );

};





//





//
#define		CONST_iAllocType_null								0		//  don't used.
//  #define		CONST_iAllocType_tmpMemory							10		//  2010/09/18
#define		CONST_iAllocType_user								1000	//  2010/09/18



///////////////////

//  2010/07/16

#if  0	//  2015/02/07
#ifdef  __DEBUG__
		void *  mymalloc( size_t size );
		void myfree( void *memblock );
#else
		#define  mymalloc		::malloc
		#define	 myfree			::free
#endif
#endif


//
extern  "C"  __declspec(  dllexport  )  void *  mymalloc( size_t size );
extern  "C"  __declspec(  dllexport  )  void myfree( void *memblock );

//
extern  "C"  __declspec(  dllexport  )  char  *  mallocMemory(  int  iAllocType,  size_t  size,  QY_MEMORY  *  p  );
extern  "C"  __declspec(  dllexport  )  void freeMemory(  QY_MEMORY  *  p  );

#ifdef  __DEBUG__
	    int  printShmMemory(  int  iAllocType,  LPCTSTR  hint,  BOOL  bDetail  =  FALSE  );
#endif

class  CQyMalloc  {									//  2008/03/29, ¿ÉÒÔ×Ô¶¯ÊÍ·ÅµÄ¶¯Ì¬·ÖÅäÀà¡£
 
private:
		QY_MEMORY		m_var;
public:
	    CQyMalloc(  )  {  memset(  &m_var,  0,  sizeof(  m_var  )  );	  }
		char  *  mallocf(  size_t  size  )  
		{
			return  mallocMemory(  0,  size,  &m_var  );
		}
		char  *  mallocf(  int  iAllocType,  size_t  size  )
		{
			return  mallocMemory(  iAllocType,  size,  &m_var  );
		}
		
		void  freef(  )  
		{
			freeMemory(  &m_var  );
		}

		int  get(  QY_MEMORY  *  p  )		//  2010/04/23
		{
			if  (  !p  )  return  -1;
			if  (  !m_var.m_pBuf  )  return  -1;
			memcpy(  p,  &m_var,  sizeof(  p[0]  )  );
			return  0;
		}

		int  detach(  )  //  2009/05/03
		{  
			if  (  !m_var.m_pBuf  )  return  -1;
			memset(  &m_var,  0,  sizeof(  m_var  )  );
			return  0;
		}

		int  attach(  QY_MEMORY  *  p  )  //  2009/12/07
		{
			if  (  !p  )  return  -1;
			if  (  m_var.m_pBuf  ||  m_var.m_pVirtualData  )  return  -1;
			memcpy(  &m_var,  p,  sizeof(  m_var  )  );
			return  0;
		}

		~CQyMalloc(  )  
		{ 
			freeMemory(  &m_var  );
		}
};


//
typedef  unsigned  int	(  *PF_getuiNextTranNo  )(  void  *  p0,  long  val,  long  *  puiTranNo  );  //  2014/11/28


#endif  //  }

