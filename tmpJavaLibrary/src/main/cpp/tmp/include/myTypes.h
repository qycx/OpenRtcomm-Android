
#ifndef  __myTypes_h__
#define  __myTypes_h__  //  {

//
#include    "myTypes_basic.h"


#if  10

//
#ifdef  __LINUX__

//#include    "myTypes_linux.h"

//
//typedef  char16_t   TCHAR;
typedef  wchar_t    TCHAR;
typedef  TCHAR  *   LPCTSTR;
typedef  TCHAR  *   LPTSTR;

//
typedef     TCHAR      WCHAR;

//
typedef  char  *    LPCSTR;

//
typedef unsigned long       DWORD;
typedef int                 BOOL;
typedef unsigned char       BYTE;
typedef unsigned short      WORD;

//
#define  __int64            long long


//
typedef  DWORD *            LPDWORD;

//
typedef  void  *            LPVOID;
typedef  unsigned  char     BYTE;

typedef  WCHAR  *  LPCWSTR;

typedef  WCHAR  *  LPWSTR;

//
typedef char *LPSTR, *PSTR;


//
typedef  byte  *  PBYTE;

//
typedef  unsigned  int      UINT;

typedef  unsigned  int      uint32_t;
typedef  unsigned  char     uint8_t;

//

//
typedef myint64             LONG_PTR;
typedef myuint64    ULONG_PTR;

typedef myint64 INT_PTR, *PINT_PTR;
typedef myuint64 UINT_PTR, *PUINT_PTR;

//
typedef long HRESULT;

typedef UINT_PTR            WPARAM;
typedef LONG_PTR            LPARAM;
typedef LONG_PTR            LRESULT;

typedef DWORD   COLORREF;


//
typedef  void  *            HKEY;
typedef void *HANDLE;
//typedef /* [wire_marshal] */ void *HWND;
typedef  void  *  myHWND;

//
typedef  void *  HWND;

//
typedef  void  *  myHINSTANCE;

typedef  void  *  HFONT;

typedef  void  *  LPSECURITY_ATTRIBUTES;

//
typedef  myint64        mytime_t;

//
typedef UINT_PTR        SOCKET;


typedef WORD   LANGID;

typedef myuint64 ULONG64, *PULONG64;
typedef myuint64 DWORD64, *PDWORD64;

typedef struct _FILETIME {
    DWORD dwLowDateTime;
    DWORD dwHighDateTime;
} FILETIME, *PFILETIME, *LPFILETIME;

typedef struct _SYSTEMTIME
{
    WORD wYear;
    WORD wMonth;
    WORD wDayOfWeek;
    WORD wDay;
    WORD wHour;
    WORD wMinute;
    WORD wSecond;
    WORD wMilliseconds;
} SYSTEMTIME, *PSYSTEMTIME, *LPSYSTEMTIME;


typedef  BOOL  *  PBOOL;


//
#ifndef  FALSE
    #define  FALSE      0
    #define  TRUE       1
#endif

#define MAX_PATH          260


#define     WINAPI
#define     IN
#define     OUT

//
#define     _cdecl
#define     __cdecl

//
#define     CALLBACK

//
#define     __declspec(x)
#define     _declspec(x)

#define BI_RGB        0L

#ifndef MAKEFOURCC
    #define MAKEFOURCC(ch0, ch1, ch2, ch3)                              \
                ((uint32_t)(uint8_t)(ch0) | ((uint32_t)(uint8_t)(ch1) << 8) |       \
                ((uint32_t)(uint8_t)(ch2) << 16) | ((uint32_t)(uint8_t)(ch3) << 24 ))
#endif /* defined(MAKEFOURCC) */


//
typedef long LONG;

typedef  void*          myHTREEITEM;
typedef  void *         myHMENU;
typedef HANDLE HDRAWDIB; /* hdd */
typedef void  *  HDC;
typedef /* [wire_marshal] */ void *HBITMAP;
typedef HANDLE  HIC;

//
typedef  void  *  HICON;
typedef  void  *  HMENU;
typedef  void  *  HINSTANCE;
typedef  void  *  HWAVEOUT;
typedef  void  *  HCURSOR;



typedef struct mytagBITMAPINFOHEADER{
        DWORD      biSize;
        LONG       biWidth;
        LONG       biHeight;
        WORD       biPlanes;
        WORD       biBitCount;
        DWORD      biCompression;
        DWORD      biSizeImage;
        LONG       biXPelsPerMeter;
        LONG       biYPelsPerMeter;
        DWORD      biClrUsed;
        DWORD      biClrImportant;
} myBITMAPINFOHEADER;

typedef struct mytWAVEFORMATEX
{
    WORD        wFormatTag;         /* format type */
    WORD        nChannels;          /* number of channels (i.e. mono, stereo...) */
    DWORD       nSamplesPerSec;     /* sample rate */
    DWORD       nAvgBytesPerSec;    /* for buffer estimation */
    WORD        nBlockAlign;        /* block size of data */
    WORD        wBitsPerSample;     /* number of bits per sample of mono data */
    WORD        cbSize;             /* the count in bytes of the size of */
                                    /* extra information (after cbSize) */
} myWAVEFORMATEX;

typedef struct tagRGBQUAD {
        BYTE    rgbBlue;
        BYTE    rgbGreen;
        BYTE    rgbRed;
        BYTE    rgbReserved;
} RGBQUAD;

//
typedef struct mytagBITMAPINFO {
    myBITMAPINFOHEADER    bmiHeader;
    RGBQUAD             bmiColors[1];
} myBITMAPINFO;

//
#define __T(x)      L ## x

//
#define _T(x)       __T(x)
#define _TEXT(x)    __T(x)

//
long  myInterlockedDecrement(    long volatile *Addend  );
long  myInterlockedIncrement(    long volatile *Addend  );
LONG myInterlockedExchangeAdd(  LONG volatile *pAddend,  LONG          Value  );

//
#ifdef  __cplusplus
extern  "C"  {
#endif

    unsigned  int  getNextSessionId_linux(  );

#ifdef  __cplusplus
};
#endif



//
LPWSTR  mylstrcpyn(  LPWSTR lpString1,  LPCWSTR lpString2,  int iMaxLength  );
BOOL  myIsWindow(  HWND hWnd);
int  myGetCurrentProcessId();
int myGetCurrentThreadId();
LRESULT  mySendMessage(  HWND hWnd,  UINT Msg,  WPARAM wParam,  LPARAM lParam);
BOOL myPostMessage(  HWND hWnd,  UINT Msg,  WPARAM wParam,  LPARAM lParam);
BOOL  mySetForegroundWindow(  HWND hWnd);

//
mytime_t mytime(mytime_t *pt);

DWORD  mytimeGetTime();

LANGID  myGetSystemDefaultLangID(void);

wchar_t* myltow(  long    _Value,  wchar_t  * _Buffer,  int     _Radix  );




//
#define     mylocaltime     localtime


//
#define INFINITE            0xFFFFFFFF  // Infinite timeout

typedef myint64 LONGLONG;
typedef myuint64 ULONGLONG;

typedef  void  *  LPTHREAD_START_ROUTINE;

//
typedef ULONG_PTR HCRYPTPROV;
typedef ULONG_PTR HCRYPTKEY;
typedef ULONG_PTR HCRYPTHASH;

//
typedef  BYTE  *  LPBYTE;
typedef void *HMODULE;

#define     VOID    void

//
typedef ULONG_PTR DWORD_PTR, *PDWORD_PTR;

typedef struct tagPOINT

{

        LONG  x;

            LONG  y;

} POINT, *PPOINT;



//
typedef struct tagSIZE
{
    LONG        cx;
    LONG        cy;
} SIZE, *PSIZE, *LPSIZE;


//
typedef struct tagRECT
{
    LONG    left;
    LONG    top;
    LONG    right;
    LONG    bottom;
} RECT, *PRECT;

typedef struct _GUID {
    unsigned long  Data1;
    unsigned short Data2;
    unsigned short Data3;
    unsigned char  Data4[ 8 ];
} GUID;

typedef union _LARGE_INTEGER {
    struct {
        DWORD LowPart;
        LONG HighPart;
    } DUMMYSTRUCTNAME;
    struct {
        DWORD LowPart;
        LONG HighPart;
    } u;
    LONGLONG QuadPart;
} LARGE_INTEGER;

//
#define     _sntprintf      swprintf
#define     lstrlen         wcslen
#define     _tcsrchr        wcsrchr
#define     lstrcpyn        mylstrcpyn
#define     _istspace       iswspace
#define     lstrcpy         wcscpy
#define     _snprintf       snprintf
#define     _tcslen         wcslen
#define     _strnicmp       strnicmp
#define     _tcsncpy        wcsncpy
//
#define     _tcsstr         wcsstr

//
#define     _vsnprintf      vsnprintf
#define     _vsntprintf     vswprintf

#define     _stricmp strcasecmp
//#define     strnicmp strncasecmp
#define     lstrcmpi        wcscasecmp
#define     _tcsicmp        wcscasecmp
#define     _tcsnicmp       wcsncasecmp
#define     _wcsicmp        wcscasecmp
#define     _tcscmp         wcscmp


#define     _tcschr         wcschr
#define     _tcstoul        wcstoul
#define     _tcstol         wcstol
#define     _ltot           myltow





//
#define IDOK                1
#define IDCANCEL            2
#define IDABORT             3
#define IDRETRY             4
#define IDIGNORE            5
#define IDYES               6
#define IDNO                7

//
#define WM_SIZE                         0x0005
//
#define WM_USER                         0x0400
#define WM_CLOSE                        0x0010
//
#define WM_COMMAND                      0x0111
#define WM_SYSCOMMAND                   0x0112

//
#define SC_RESTORE      0xF120

#define SC_MINIMIZE     0xF020



//
#define STATUS_WAIT_0                           ((DWORD   )0x00000000L)



//
#define WAIT_TIMEOUT                     258L    // dderror

//
#define WAIT_FAILED ((DWORD)0xFFFFFFFF)
#define WAIT_OBJECT_0       ((STATUS_WAIT_0 ) + 0 )


//
#define S_OK                            ((HRESULT)0L)


//
#define MB_OK                       0x00000000L
#define MB_OKCANCEL                 0x00000001L
#define MB_ABORTRETRYIGNORE         0x00000002L
#define MB_YESNOCANCEL              0x00000003L
#define MB_YESNO                    0x00000004L
#define MB_RETRYCANCEL              0x00000005L
//
#define MB_NOFOCUS                  0x00008000L
#define MB_SETFOREGROUND            0x00010000L
#define MB_DEFAULT_DESKTOP_ONLY     0x00020000L

#define MB_TOPMOST                  0x00040000L
#define MB_RIGHT                    0x00080000L
#define MB_RTLREADING               0x00100000L

#define MB_TASKMODAL                0x00002000L



//
#define INVALID_HANDLE_VALUE ((HANDLE)(LONG_PTR)-1)

#define CP_UTF8                   65001       // UTF-8 translation

//
#define HKEY_CURRENT_USER                   (( HKEY ) (ULONG_PTR)((LONG)0x80000001) )
#define HKEY_LOCAL_MACHINE                  (( HKEY ) (ULONG_PTR)((LONG)0x80000002) )


//
#define INVALID_SOCKET  (SOCKET)(~0)
#define SOCKET_ERROR            (-1)


#define     SOCKADDR    sockaddr
#define     SOCKADDR_IN sockaddr_in


//
//
#define     _tfopen         mywfopen

//
#define     _fgetts         fgetws
#define     _ftprintf       fwprintf

//
long mywtol(  wchar_t const* _String  );
#define     _ttol           mywtol

//
#define  mynull				0

#define NOPARITY            0
#define ODDPARITY           1
#define EVENPARITY          2
#define MARKPARITY          3
#define SPACEPARITY         4

#define ONESTOPBIT          0
#define ONE5STOPBITS        1
#define TWOSTOPBITS         2


#define SQL_WCHAR           (-8)
#define SQL_WVARCHAR        (-9)
#define SQL_WLONGVARCHAR    (-10)
#define SQL_C_WCHAR         SQL_WCHAR

#define SW_HIDE             0
#define SW_SHOWNORMAL       1
#define SW_NORMAL           1
#define SW_SHOWMINIMIZED    2
#define SW_SHOWMAXIMIZED    3
#define SW_MAXIMIZE         3
#define SW_SHOWNOACTIVATE   4
#define SW_SHOW             5
#define SW_MINIMIZE         6
#define SW_SHOWMINNOACTIVE  7
#define SW_SHOWNA           8
#define SW_RESTORE          9
#define SW_SHOWDEFAULT      10
#define SW_FORCEMINIMIZE    11
#define SW_MAX              11


//
#define     ID_qyProperties     -1
#define     ID_qyTalk           -1

//
#define WAVE_FORMAT_PCM     1
#define  WAVE_FORMAT_ADPCM                      0x0002 /* Microsoft Corporation */
#define  WAVE_FORMAT_MPEGLAYER3                 0x0055 /* ISO/MPEG Layer3 Format Tag */
#define  WAVE_FORMAT_WMAUDIO2                   0x0161 /* Microsoft Corporation */


//
#define     OutputDebugString(lpOutputString  )
void  *  GetProcAddress(
    HMODULE hModule,
    LPCSTR lpProcName
    );
HWND GetDlgItem( HWND hDlg, int nIDDlgItem);
BOOL WINAPI  SetDlgItemText( HWND hDlg, int nIDDlgItem, LPCTSTR lpString  );
BOOL MessageBeep( UINT uType);
BOOL  SetWindowText(  HWND hWnd,  LPCWSTR lpString);
int GetWindowText(  HWND hWnd,  LPWSTR lpString,  int nMaxCount);
BOOL IsWindowVisible(HWND hWnd);
HWND  GetForegroundWindow( VOID);
BOOL SetForegroundWindow( HWND hWnd  );
BOOL InvalidateRect(  HWND       hWnd,  const RECT *lpRect,  BOOL       bErase  );
BOOL  ShowWindow(  HWND hWnd,  int nCmdShow);
BOOL  IsIconic( HWND hWnd);


//
#define		MACRO_qyAssert(  x,  hint  )




//
#else


//
//#define __USE_winTypes__

//

//
#include    <time.h>
#include	<WinSock2.h>

#include	<windows.h>
#include	<tchar.h>
#include	<stdio.h>



//
typedef  time_t             mytime_t;
typedef  HWND               myHWND;
typedef  HINSTANCE          myHINSTANCE;
//
#define  myHTREEITEM        HTREEITEM   
#define  myHMENU            HMENU
//
typedef  BITMAPINFOHEADER   myBITMAPINFOHEADER;
//
#define  myWAVEFORMATEX     WAVEFORMATEX 
//
typedef  BITMAPINFO         myBITMAPINFO;



//
#define     myInterlockedDecrement          InterlockedDecrement
#define     myInterlockedIncrement          InterlockedIncrement
#define     myInterlockedExchangeAdd        InterlockedExchangeAdd

//
#define     mylstrcpyn                      lstrcpyn

#define     myIsWindow                      IsWindow
#define     myGetCurrentProcessId           GetCurrentProcessId
#define     myGetCurrentThreadId            GetCurrentThreadId

#define     mySendMessage                   SendMessage
#define     myPostMessage                   PostMessage
#define     mySetForegroundWindow           SetForegroundWindow
//
#define     mySleep                         Sleep

//
#define     myGetSystemDefaultLangID        GetSystemDefaultLangID


//
typedef  int  socklen_t;


//
#define  mynull				NULL


#endif

//
#ifdef  __cplusplus
extern  "C"  {
#endif 

int  traceLog(TCHAR * fmt, ...);
int  traceLogA(char* fmt, ...);
void qyDisplayLastError(char* szAPI);

#ifdef  __cplusplus
    }
#endif 


#endif


#endif  //  }


