
#ifndef  __QYPRECOMP_H__
#define  __QYPRECOMP_H__	/* { */


//#define  __WITHOUT_SOCK__

#ifdef	WINCE
	#define		__WINCE__
#endif

#ifdef		_DEBUG
	#define		__DEBUG__
#endif


#ifdef		__DEBUG__								//  Debug版本开关量

#endif



#ifndef		__DEBUG__								//  Release版本开关量
	//#define		__WITHOUT_DEBUG_LOG__	
#endif


#endif	/* } */


