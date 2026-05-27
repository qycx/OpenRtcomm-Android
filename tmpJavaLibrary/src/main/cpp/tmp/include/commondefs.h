#ifndef COMMONDEFS_H
#define COMMONDEFS_H


#if  0
#if !defined( BOOL )
 #define BOOL int
#endif
#endif

#if  0	//  2013/12/10
#if !defined( bool )
    #define bool BOOL
#endif
#endif

#if !defined( TRUE ) && !defined( FALSE )
    #define TRUE 1
    #define FALSE 0
#endif

#if !defined( ON ) && !defined( OFF )
    #define ON 0
    #define OFF -1
#endif

#if !defined( SUCCESS ) && !defined( FAILURE )
    #define SUCCESS 0
    #define FAILURE -1
#endif

#if !defined( INVALID_PID )
    #define INVALID_PID -1
#endif

#ifndef mymin
    #define mymin(x,y)	((x)<(y)?(x):(y))
#endif
#ifndef mymax
    #define mymax(x,y)	((x)>(y)?(x):(y))
#endif

# define bitset(bit, word)	(((word) & (bit)) != 0)

#ifndef MAX_IP_ADDR_LEN
    #define MAX_IP_ADDR_LEN	15
#endif

#define OCTET_STR	0

#ifndef SIG_HANDLER
    typedef void (*SIG_HANDLER)(int);
#endif

#define TYPE_SIZE(type,x) ( sizeof(x)/sizeof(type) )
#define STRUCT_MEM_SIZE(struct_typ,mem)	sizeof( ((struct_typ *)0)->mem )

typedef struct __msg_buf_t {

    int mType;
    char mText[1];

 }		MSG_BUF;

#define		MAX_DIR_LEN						255
#define		MAX_FULL_FILE_NAME_LEN			255
#define		MAX_FILE_NAME_LEN_WITHOUT_DIR	255


#define		RC_SUCCESS						0
#define		RC_FAILURE						-1
#define		RC_TIMEOUT						-2
#define		RC_EOF							-3
#define		RC_INTERRUPTED					-4
#define		RC_TIMEOUTX						-5


#define		CONST_qyPlatform_unknown		1
#define		CONST_qyPlatform_win32			2
#define		CONST_qyPlatform_win95			3
#define		CONST_qyPlatform_win98			4
#define		CONST_qyPlatform_winme			5
#define		CONST_qyPlatform_nt				6
#define		CONST_qyPlatform_winnt			CONST_qyPlatform_nt		//  2005/07/03
#define		CONST_qyPlatform_win2k			7
#define		CONST_qyPlatform_winxp			8
#define		CONST_qyPlatform_win2003		9
#define		CONST_qyPlatform_winVista		10						//  2006/07/16
#define		CONST_qyPlatform_win2008		11						//  2009/11/06
#define		CONST_qyPlatform_win7			12						//  2009/11/06
#define		CONST_qyPlatform_win2008R2		13						//  2009/11/06
#define		CONST_qyPlatform_win8			14						//  2012/12/20
#define		CONST_qyPlatform_win2012		15						//  2012/12/20

//
#define		CONST_qyPlatform_winCe			101						//  2010/04/06


//  #define		PLATFORM_UNKNOWN				CONST_qyPlatform_unknown
#define		PLATFORM_WIN32					CONST_qyPlatform_win32
#define		PLATFORM_WIN95					CONST_qyPlatform_win95
#define		PLATFORM_WIN98					CONST_qyPlatform_win98
#define		PLATFORM_WINME					CONST_qyPlatform_winme
#define		PLATFORM_NT						CONST_qyPlatform_nt
#define		PLATFORM_WIN2K					CONST_qyPlatform_win2k
#define		PLATFORM_WINXP					CONST_qyPlatform_winxp


#define		CONST_qyTimeFormat				"%04d%02d%02d%02d%02d%02d"

//
#if  0	//  2014/03/23.     Ҫ Ƶ defs_open.h  ȥ
typedef  int  (  *  PF_commonHandler  )(  LPVOID  p0,  LPVOID  p1,  LPVOID  p2  );
typedef  int  (  *  PF_bCommonHandler  )(  LPVOID  p0,  LPVOID  p1,  LPVOID  p2  );		//   TRUE or FALSE
typedef  void  *  (  *  PF_pCommonHandler  )(  LPVOID  p0,  LPVOID  p1,  LPVOID  p2  );	//  ?011/07/10

#ifndef  mycountof		//  2005/11/16,
         #define mycountof(array) (sizeof(array)/sizeof(array[0]))
#endif
#endif


//       ֵΪ     ䷵  ֵ    CONST_qyRc_ok  ͬ
//
#define		CONST_qyRet_err											-1
#define		CONST_qyRet_ok											0
#define		CONST_qyRet_redirect									1
#define		CONST_qyRet_update										2
#define		CONST_qyRet_quit										3
#define		CONST_qyRet_hint										4
#define		CONST_qyRet_needMoreLicense								5
#define		CONST_qyRet_accessDenied								6		//  2003/11/17
#define		CONST_qyRet_skip										7		//	2004/03/10
#define		CONST_qyRet_needRefresh									1000	//  2004/06/14
#define		CONST_qyRet_syncConflict								1001	//  2004/06/20
#define		CONST_qyRet_outOfService								1002	//  2006/05/07
#define		CONST_qyRet_needVerified								1003	//  2007/04/03,   Ҫ
#define		CONST_qyRet_serverHello									1004	//  2008/06/09, serverҪ 󷵻ع Կ    Ϣ
//#define		CONST_qyRet_getRandomOk									1005
#define		CONST_qyRet_authRespOk									1005	//  2023/05/02

//
#define		CONST_qyRet_qFull										-2000	//  2007/05/29,
//
#define		CONST_qyRet_bufTooSmall									-2101	//  2010/06/18
#define		CONST_qyRet_exception									-2102	//  2014/08/11

//
//  -3000
//
#define		CONST_qyRet_cantGet_index_activeMems_from				-3101
#define		CONST_qyRet_needKeyFrameFirst							-3102




#endif // COMMONDEFS_H
