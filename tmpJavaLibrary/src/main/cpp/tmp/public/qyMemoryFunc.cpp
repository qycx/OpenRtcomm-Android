


#include	"stdafx.h"

#include	"qyPrecomp.h"

#ifndef  __LINUX__

#include	<winsock2.h>
#include	<windows.h>
#include	<assert.h>
#include	<tchar.h>

#include	"qyCommon.h"

#endif



//  #include	<IPTypes.h>
#include	<stdlib.h>

#if  0
#include	"qyPreCustom.h"
#include	"qyCustom.h"
#include	"qyWmComm.h"
#include	"qyCommCommon.h"
#include	"qyCommProc.h"
#include	"qnmCommProc.h"
#endif

#include	"qySyncCommProc.h"



///////////////////  2010/07/16, mymalloc is designed for debug.

//  2015/02/07. ×¨ÃÅÔÚÕâÀïÐ´Õâ2¸öº¯Êý£¬ÊÇÎªÁË°Ñ¸÷Ä£¿éÖÐµÄÄÚ´æ·ÖÅä¶¼¾¡¿ÉÄÜ¼¯ÖÐµ½±¾Ä£¿éÖÐ£¬¼õÉÙÒ»¸öÄ£¿é·ÖÅä£¬ÁíÒ»¸öÄ£¿éÊÍ·ÅµÄ´íÎó¡£
//
extern  "C"  __declspec(  dllexport  ) void *  mymalloc( size_t size )
{
	void  *	p	=	NULL;
	int		i	=	0;

	//
	p  =  malloc(  size  );

#ifdef  __DEBUG__
	#if  10
	switch  (  size  )  {
			case  24:
				    i  =  size;
					//traceLog(  _T(  "p is 0x%p"  ),  p  );
					break;
			case  632:
				  i  =  size;
				  break;
			case  8528:
				  i  =  size;
				  break;
			case  424:
				    i  =  size;
					break;
			case  1055704:
				  i  =  size;
				  break;
			case  1048636:
				  i  =  size;
				  break;
			case  2216:
				  i  =  size;
				  break;
			case  76064:
				  i  =  size;
				  break;
			case  432:
				  i  =  size;
				  break;
			case  925696:
				  i  =  size;
				  break;
			case  44:
				  i  =  size;
				  break;
			case  448:
				  i  =  size;
				  break;
			case  1959520:
				  i  =  size;
				  break;
			case  960224:
				  i  =  size;
				  break;
			case  1048640:
				  i  =  size;
				  break;
			case  84:
				  i  =  size;
				  break;
			case  12:
				  i  =  size;
				  break;
			case  8:
				  i  =  size;
				  break;
			case  1:
				  i  =  size;
				  break;
			case  720:
				  i  =  size;
				  break;
			case  3944:
				  i  =  size;
				  break;
			case  360:
				  i  =  size;
				  break;
			case  2349200:
				  i  =  size;
				  break;
			case  2350888:
				  i  =  size;
				  break;
			case  2446136:
				  i  =  size;
				  break;
			case  384:
				  i  =  size;
				  break;
			case  768:
				  i  =  size;
				  break;
			case  3072:
				  i  =  size;
				  break;
			default:
					break;
	}
	//
	//
	int  kk  =  i  /  1000;
	switch  (  kk  )  {
			case 231:
			case  230:
					i  =  size;
					break;
			default:
					break;
					
	}

	#endif
	//
#endif


	return  p;
}


 //  2014/10/04
extern  "C"  __declspec(  dllexport  ) void  myfree( void *memblock )
 {
	 free(  memblock  );
 }






///////////////////////////////////////////////
//
//
 extern  "C"  __declspec(  dllexport  )  char  *  mallocMemory(  int  iAllocType,  size_t  size,  QY_MEMORY  *  p  )  
{  
	if  (  !size  )  {			
		OutputDebugString(  _T(  "CQyMalloc::malloc failed, size 0.\n"  )  );			
		return  NULL;
	}
	if  (  p->m_pBuf  ||  p->m_pVirtualData  )  {
		OutputDebugString(  _T(  "CQyMalloc::malloc failed, m_pBuf is allocated already.\n"  )  );
		return  NULL;            
	}

	p->iAllocType  =  0;	//  must be reset

	#ifdef  __DEBUG__
			if  (  size  >  10000  &&  size  !=  2764800  )  {
				//  traceLogA(  "QyMalloc %d",  size  );
			}
	#endif

	//  2011/12/04
	p->uiBufSize  =  size;

	#ifndef  __WINCE__
			 p->m_pBuf  =  (  char  *  )mymalloc(  size  );  return  p->m_pBuf;  
	#else
			 if  (  !iAllocType  )  {			 
				 #if  0
					 p->m_pVirtualData  =  (  char  *  )VirtualAlloc(  NULL,  size,  MEM_RESERVE,  PAGE_READWRITE  );
					 if  (  !p->m_pVirtualData  )  {			
						 return  NULL;
					 }
					 p->m_pBuf  =  (  char  *  )VirtualAlloc(  p->m_pVirtualData,  size,  MEM_COMMIT,  PAGE_READWRITE  );
					 if  (  !p->m_pBuf  )  {
						 #if  0
							 if  (  !VirtualFree(  p->m_pVirtualData,  size,  MEM_DECOMMIT  )  ){
								 qyDisplayLastError(  "mallocMemory failed: VirtualFree failed: MEM_DECOMMIT"  );
							 }
						 #endif
						 if  (  !VirtualFree(  p->m_pVirtualData,  0,  MEM_RELEASE  )  )  {
							 qyDisplayLastError(  "VirtualFree failed:  MEM_RELEASE"  );
							 assert(  0  );
						 }
						 p->m_pVirtualData  =  NULL;
						 return  NULL;
					 }
				 #else
					p->m_pBuf  =  (  char  *  )mymalloc(  size  );
				 #endif
				 return  p->m_pBuf;
			 }
			 MC_SHM_MEMORY	*	pShmMemory	=	(  MC_SHM_MEMORY  *  )g_pEnv->pShmMemory;
			 if  (  !pShmMemory  )  return  NULL;

			 int  index  =  iAllocType  -  CONST_iAllocType_user;						//  2011/02/17. ÕâÀï×¢Òâ£¬ÒªÇóiAllocType²»ÊÇ0µÄ»°£¬Ó¦¸ÃÊÇindex+CONST_iAllocType_user.
			 if  (  index  <  0  ||  index  >=  mycountof(  pShmMemory->mems  )  )  {
				 #ifdef  __DEBUG__
						 traceLog(  _T(  "mallocMemory failed, index %d err"  ),  index  );
				 #endif
				 return  NULL;
			 }
			 MC_SHM_memoryMem  *  pMem  =  &pShmMemory->mems[index];
			 if  (  pMem->uiBufSize_packet  <  size  )  {
				 #ifdef  __DEBUG__
					     traceLogA(  "mallocMemory failed, uiBufSize_packet %d < size %d",  pMem->uiBufSize_packet,  size  );
				 #endif
				 return  NULL;
			 }
			 {				 
				 CQySyncObj	syncObj;
				 if  (  syncObj.sync(  pMem->mutexName  )  )  return  NULL;

				 for  (  int  i  =  0;  i  <  pMem->usMaxCnt;  i  ++  )  {
					  if  (  pMem->ucbUseds[i]  )  continue;
					  pMem->ucbUseds[i]  =  TRUE;
					  p->iAllocType  =  iAllocType;
					  p->m_pBuf  =  pShmMemory->pBuf  +  pMem->offset_start  +  i  *  pMem->uiBufSize_packet;
					  return  p->m_pBuf;
					  break;
				 }

			 }
			 #ifdef  __DEBUG__
					 printShmMemory(  iAllocType,  _T(  "mallocMemory failed"  )  );
		     #endif
			 return  NULL;
	#endif
 }


 extern  "C"  __declspec(  dllexport  )  void freeMemory(  QY_MEMORY  *  p  )
{
	if  (  !p  )  return;

	if  (  !p->iAllocType  )  {
		#if  0
			if  (  p->m_pVirtualData  )  {			
				if  (  !VirtualFree(  p->m_pVirtualData,  0,  MEM_RELEASE  )  )  {
					qyDisplayLastError(  "VirtualFree failed"  );
					assert(  0  );
				}
				memset(  p,  0,  sizeof(  p[0]  )  );				
				return;
			}		
		#endif

				if (p->m_pBuf) {
					::free(p->m_pBuf);  p->m_pBuf = NULL;
				}		

			//
		return;		
	}

	//  2010/09/18
#if  0
	if  (  p->iAllocType  ==  CONST_iAllocType_tmpMemory  )  {
		freeTmpMemoryIndex(  p->iIndex_tmpMemory  );
		memset(  p,  0,  sizeof(  p[0]  )  );
		return;
	}
#endif

	MACRO_qyAssert(  0,  _T(  "freeMemory: iAllocType is not 0"  )  );
	//  2015/01/18. ²»ÔÙÐèÒªpShmMemory.
#if  0
	MC_SHM_MEMORY	*	pShmMemory	=	(  MC_SHM_MEMORY  *  )g_pEnv->pShmMemory;
	if  (  !pShmMemory  )  return;

	int  index  =  p->iAllocType  -  CONST_iAllocType_user;
	if  (  index  <  0  ||  index  >=  mycountof(  pShmMemory->mems  )  )  {
		#ifdef  __DEBUG__
			    traceLog(  _T(  "freeMemory failed, index %d err"  ),  index  );
		#endif
		return;
	}
	MC_SHM_memoryMem  *  pMem  =  &pShmMemory->mems[index];
	{				 
		CQySyncObj	syncObj;
		if  (  syncObj.sync(  pMem->mutexName  )  )  {
			assert(  0  );
			qyExitProcess(  _T(  "freeMemory failed: can't syncObj."  )  );
		}

		int  i;
		i  =  (  p->m_pBuf  -  pShmMemory->pBuf  -  pMem->offset_start  )  /  pMem->uiBufSize_packet;
#ifdef  __DEBUG__
		assert(  !(  (  p->m_pBuf  -  pShmMemory->pBuf  -  pMem->offset_start  )  %  pMem->uiBufSize_packet  )  );
#endif
		pMem->ucbUseds[i]  =  FALSE;
		memset(  p,  0,  sizeof(  p[0]  )  );
	}
#endif

	return;
 }

