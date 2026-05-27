#ifndef QYDEFS_OPEN_H
#define QYDEFS_OPEN_H



//
#include    "myTypes.h"

//
#include	"tmpDefs_open.h"

//
#define		CONST_qyTimeLen											14


//  2014/05/02

#define		CONST_objType_null								0
#define		CONST_objType_qySwitch							1
#define		CONST_objType_qySwitchPort						2
#define		CONST_objType_col2								3
#define		CONST_objType_col3								4
#define		CONST_objType_col4								5
#define		CONST_objType_unRegGrp							6
#define		CONST_objType_othersGrp							7
#define		CONST_objType_unRegPc							8
#define		CONST_objType_pc								CONST_objType_unRegPc						//  2007/06/09￡?±í?÷ê???PC
#define		CONST_objType_otherPc							9
#define		CONST_objType_mac								CONST_objType_otherPc						//  2007/06/09, ±í?÷??óDê2?′íê??μ?PCD??￠￡???±í?÷ò???MAC
#define		CONST_objType_os								10											//  PC?ú2ù×÷?μí3
#define		CONST_objType_pcEnv								11											//  PC?úèíó2?t?·?3
#define		CONST_objType_cfgSpOs							12											//  21??????à?μ?2ù×÷?μí3, 2005/01/23
#define		CONST_objType_cfgSpServicePack					13											//  21??????à?μ?ServicePack, 2005/01/23
#define		CONST_objType_zb								14
#define		CONST_objType_bb								15
#define		CONST_objType_dw								CONST_objType_col2
#define		CONST_objType_bm								CONST_objType_col3
#define		CONST_objType_syr								CONST_objType_col4
#define		CONST_objType_pcStatus							16											//  			2006/08/14
#define		CONST_objType_pcUsrInfo							17											//				2006/08/14
#define		CONST_objType_pcOsClass							18											//				2006/08/14
#define		CONST_objType_pcOpNode							19											//  			2006/08/15
#define		CONST_objType_procedObj							20											//  			2007/01/02
#define		CONST_objType_android							21


                                                                                                        //  2007/11/24
                                                                                                        //  注：网络管理的objType常数最好是另外起一个范围来定义。1000以内最好留给更重要的通用常数。
                                                                                                        //
#define		CONST_objType_browser							501											//  devType, browser
//  #define		CONST_objType_phone							502											//  qnmCommProc_open.h
//  #define		CONST_objType_dev							503											//  qnmCommProc_open.h
#define		CONST_objType_toPath							504											//
#define		CONST_objType_webcam							505											//  2009/08/28
#define		CONST_objType_screen							506											//  2009/08/28
//
#define		CONST_objType_rtspStream						507											//  2014/05/02
//


//
#define		CONST_objType_smallStream						530											//  2014/09/25. 通用的stream, 非具体指定
//
#define		CONST_objType_mosaicStream_video				535											//  2014/10/30
#define		CONST_objType_mosaicStream_resource				536											//  2014/10/30

//
#define		CONST_objType_proxiedStream_video				550											//  2014/11/20

//
//#define		CONST_objType_cosult_mem						560											//  2017/08/30

//
#define		CONST_objType_qvcf								580											//  2015/05/09

//
#define		CONST_objType_offlineFile						600											//  2015/08/03


//  2007/05/12, mis
#define		CONST_objType_mis_base							1000
//
#define		CONST_objType_admin								(  CONST_objType_mis_base  +  10  )			//  2007/08/23, 系统管理员
//
#define		CONST_objType_me								(  CONST_objType_mis_base  +  20  )
//
#define		CONST_objType_messenger							(  CONST_objType_mis_base  +  21  )
//
#define		CONST_objType_webGuest							(  CONST_objType_mis_base  +  22  )			//  2007/11/21, webGuest
//
#define		CONST_objType_phoneMessenger					(  CONST_objType_mis_base  +  23  )			//  2008/01/30, 用手机注册，但是没有客户端的
#define		CONST_objType_phoneGuest						(  CONST_objType_mis_base  +  24  )			//  2008/01/07
//
#define		CONST_objType_newDevGuest						(  CONST_objType_mis_base  +  50  )			//  2008/01/07, newDev


//																										
#define		CONST_objType_imGrps							(  CONST_objType_mis_base  +  100  )		
#define		CONST_objType_imGrp								(  CONST_objType_mis_base  +  101  )
//
#define		CONST_objType_tmpGrp							(  CONST_objType_mis_base  +  102  )		//  2008/01/22, 临时组。通常在于临时组成一个Q, 然后传递给dlgTalk
#define		CONST_objType_tmpMsgr							(  CONST_objType_mis_base  +  103  )		//  2018/11/11. 临时对象，可能是messenger,也可能是组
//

//  2013/02/13. 以下不能存储，但是可以用在内存中处理
#define		CONST_objType_tmp_base							(  CONST_objType_mis_base  +  5000  )
//
#if  0		//  objType_tmp.h
#define		CONST_objType_selectRemoteVideo					(  CONST_objType_tmp_base  +  1  )
#define		CONST_objType_selectADev						(  CONST_objType_tmp_base  +  2  )			//  2013/04/04
#define		CONST_objType_selectVDev						(  CONST_objType_tmp_base  +  3  )
#define		CONST_objType_selectLayoutType					(  CONST_objType_tmp_base  +  4  )			//  2013/06/24
#define		CONST_objType_imGrps_av							(  CONST_objType_tmp_base  +  5  )			//  2014/01/31
#define		CONST_objType_imGrps_msg						(  CONST_objType_tmp_base  +  6  )			//  2014/01/31
#endif


//
//  #define		CONST_objType_custom_base		32000										//  qnmCommProc_open.h
//  #define		CONST_objType_newDev			(  CONST_objType_custom_base  +  1  )		//  qnmCommProc_open.h


//  2014/01/31
#define		CONST_imGrpSubtype_null				0
#define		CONST_imGrpSubtype_av				1
#define		CONST_imGrpSubtype_msg				2
#define		CONST_imGrpSubtype_resource			3			//  2018/07/09
#define		CONST_imGrpSubtype_initConf			4			//  2018/10/23
#define		CONST_imGrpSubtype_mgr				5



//  2015/07/28
#define		CONST_imGrpMemRole_null				0
#define		CONST_imGrpMemRole_mgr				1


//  2017/08/14
#define		CONST_imObjRole_null				0
#define		CONST_imObjRole_superAdmin			100


//



///////////////////////////////

//
#ifndef  isHandleValid_open
         #define		isHandleValid_open(  h  )									(  (  h  )  !=  NULL  &&  (  h  )  !=  INVALID_HANDLE_VALUE  )
#endif


//
#define   MACRO_safeRelease(x)      if((x)){ (x)->Release(); (x) = NULL;}
#define   MACRO_safeFree(x)         if((x)){ free(x);        (x) = NULL;}
#define   MACRO_safeDelete(x)       if((x)){ delete    (x);  (x) = NULL;}
//
#define   MACRO_mysafeFree(x)         if((x)){ myfree(x);        (x) = NULL;}



//  2014/03/05
typedef  int  (  *  PF_commonHandler  )(  LPVOID  p0,  LPVOID  p1,  LPVOID  p2  );
typedef  int  (  *  PF_bCommonHandler  )(  LPVOID  p0,  LPVOID  p1,  LPVOID  p2  );		//  TRUE or FALSE
typedef  void  *  (  *  PF_pCommonHandler  )(  LPVOID  p0,  LPVOID  p1,  LPVOID  p2  );	//  ?011/07/10

//
typedef int (*PF_commonHandler4)(void* p0, void* p1, void* p2, void* p3);  //


//
#ifndef  M_sizeImage_open
         #define		M_sizeImage_open(  bits,  biWidth,  biHeight  )		(  (  (  biWidth  )  *  (  bits  )  +  31  )  /  32  *  4  *  (  biHeight  )  )
#endif

//  2011/12/17
#define		M_lineBytes_rgb(  bits,  biWidth  )				(  (  biWidth  )  *  (  bits  )  +  31  )  /  32  *  4


//  2014/03/30
//  #define		M_nalType_h264(  p  )	p[2]  ==  1  ?  (  p[3]  &  0x1f  )  :  (  p[4]  &  0x1f  )
#define		M_nalType_h264(  p  )	(p)[2]  ==  1  ?  (  (p)[3]  &  0x1f  )  :  (  (p)[4]  &  0x1f  )

//
#define		M_nalType_h265(  p  )	((p)[2]  ==  1  ?  (  (p)[3]  &  0x7e  )  :  (  (p)[4]  &  0x7e  ))>>1


//
#define		NAL_TRAIL_N		0
#define		NAL_TRAIL_R		1
#define		NAL_TSA_N		2
#define		NAL_TSA_R		3
#define		NAL_STSA_N		4
#define		NAL_STSA_R		5
#define		NAL_RADL_N		6
#define		NAL_RADL_R		7
#define		NAL_RASL_N		8
#define		NAL_RASL_R		9
#define		NAL_BLA_W_LP	16
#define		NAL_BLA_W_RADL	17
#define		NAL_BLA_N_LP	18
#define		NAL_IDR_W_RADL	19
#define		NAL_IDR_N_LP	20
#define		NAL_CRA_NUT		21
#define		NAL_VPS			32
#define		NAL_SPS			33
#define		NAL_PPS			34
#define		NAL_AUD			35
#define		NAL_EOS_NUT		36
#define		NAL_EOB_NUT		37
#define		NAL_FD_NUT		38
#define		NAL_SEI_PREFIX	39
#define		NAL_SEI_SUFFIX	40


//  2007/07/18
//
#define		CONST_qyStatus_ok										0
//  #define		CONST_qyStatus_err									-1
#define		CONST_qyStatus_err										255		//  2007/08/02,
//
#define		CONST_qyStatus_del										32		//  2007/05/12,
#define		CONST_qyStatus_update									33		//  2007/05/12

//  		//  2009/01/01

#define		CONST_qyStatus_is_base									500		//
//
#define		CONST_imTaskStatus_null									0		//
//
#define		CONST_imTaskStatus_req									(  CONST_qyStatus_is_base  +  11  )		//
#define		CONST_imTaskStatus_resp									(  CONST_qyStatus_is_base  +  12  )		//  same as applyToRecv
//
#define		CONST_imTaskStatus_applyToSend							(  CONST_qyStatus_is_base  +  21  )		//
#define		CONST_imTaskStatus_waitToSend							(  CONST_qyStatus_is_base  +  22  )		//
#define		CONST_imTaskStatus_applyToRecv							(  CONST_qyStatus_is_base  +  23  )		//
#define		CONST_imTaskStatus_waitToRecv							(  CONST_qyStatus_is_base  +  24  )		//  after accepted
///
#define		CONST_imTaskStatus_sending								(  CONST_qyStatus_is_base  +  27  )		//
#define		CONST_imTaskStatus_receiving							(  CONST_qyStatus_is_base  +  28  )		//
//
#define		CONST_imTaskStatus_sendBreak							(  CONST_qyStatus_is_base  +  31  )		//
#define		CONST_imTaskStatus_recvBreak							(  CONST_qyStatus_is_base  +  32  )		//
//
#define		CONST_imTaskStatus_recvFinished							(  CONST_qyStatus_is_base  +  50  )		//
#define		CONST_imTaskStatus_sendFinished							(  CONST_qyStatus_is_base  +  51  )		//
//
#define		CONST_imTaskStatus_acceptedByReceiver					(  CONST_qyStatus_is_base  +  60  )		//  2010/07/19
#define		CONST_imTaskStatus_dualByReceiver						(  CONST_qyStatus_is_base  +  61  )		//  2010/07/19
//
#define		CONST_imTaskStatus_err_missingOrgReq					(  CONST_qyStatus_is_base  +  100  )	//
#define		CONST_imTaskStatus_err_cantGet_index_activeMems_from	(  CONST_qyStatus_is_base  +  101  )	//

//
#define		CONST_imTaskStatus_canceledBySender						(  CONST_qyStatus_is_base  +  200  )	//
#define		CONST_imTaskStatus_canceledByReceiver					(  CONST_qyStatus_is_base  +  201  )	//
#define		CONST_imTaskStatus_deniedByReceiver						(  CONST_qyStatus_is_base  +  203  )	//
#define		CONST_imTaskStatus_autoCanceledByReceiver				(  CONST_qyStatus_is_base  +  204  )	//  2014/07/30

//
#define		CONST_imTaskStatusTable		CONST_qyStatusTable

//
//
#define		CONST_ruleCmd_null										0		//  2005/10/10
#define		CONST_ruleCmd_prohibit									1		//  2005/10/10
#define		CONST_ruleCmd_prohibitAll								2		//  2005/10/10
#define		CONST_ruleCmd_permit									3		//  2005/10/10
#define		CONST_ruleCmd_permitAll									4		//  2005/10/10
//
#define		CONST_ruleCmd_manual									10		//  2016/02/15
//
#define		CONST_ruleCmd_permitAndFullscreen						51		//  2010/11/28

//
#define		isRuleCmdPermit(  uiRuleCmd  )							(  (  uiRuleCmd  )  ==  CONST_ruleCmd_permit  ||  (  uiRuleCmd  )  ==  CONST_ruleCmd_permitAndFullscreen  )		//  2011/04/09

//  2010/08/13
#define		CONST_ruleType_null										0
#define		CONST_ruleType_autoAnswer_av							1
#define		CONST_ruleType_autoAnswer_screen						2		//  2010/12/19
#define		CONST_ruleType_autoAnswer_mediaFile						3		//  2010/12/19
//
#define		CONST_ruleType_conferenceId_shareWebcam					101		//  2011/11/13
#define		CONST_ruleType_ucbNoLocalVideoInThisConference			102		//  是否在这个会议中启动视频. 2011/10/22
#define		CONST_ruleType_ucbDistinguishSharedWebcams				103		//  是否区分被共享的摄像机，（是否使用序号）


//  2010/12/21
#define		CONST_usLayoutType_null									0		//  2010/12/21. if usLayoutType_local is 0, usLayoutType_starter is adopted.
#define		CONST_usLayoutType_dialog								1
#define		CONST_usLayoutType_conference							2
#define		CONST_usLayoutType_training								3
#define		CONST_usLayoutType_videoOnly							4
#define		CONST_usLayoutType_resourceOnly							5


//
typedef  struct  __qyMessengerId_t {								//
                 myuint64                   						ui64Id;
}		 QY_MESSENGER_ID;


//
//  2016/02/18
typedef  struct  __playerId_t {
    int												index_player;
    unsigned  int										uiTranNo_player;
}		 PLAYER_ID;

//
#define		isSame_playerId(p1,p2)		( (p1)->index_player==(p2)->index_player && (p1)->uiTranNo_player  ==  (p2)->uiTranNo_player  )


//  2014/10/11
typedef  struct  __capRect_t										{
                 int												iX,  iY;				//
                 unsigned  int										iW,  iH;
}		 CAP_RECT;


//
typedef  struct  __recordSoundCbVar_t							{
                 void										*	pProcInfo;
                 unsigned  int									uiCapType;									//  reserved
                 int											iIndex_capProcInfo;							 //  CAP_procInfo_recordSound	*	pRecord;

                 BOOL											bStarted;
                 DWORD											dwTickCnt_startToEndThread;

                 //  WAVEFORMATEX							*	pWfx;
                 void										*	pWfx;
                 //
                 unsigned  int									uiBytesRecorded;
                 //
                 int											m_iSoundBuffers;

                 //
                 BYTE										*	pPkt;
                 unsigned  int									uiBufSize_pPkt;

                 void										*	pMsgBuf;
                 unsigned  int									uiBufSize_pMsgBuf;

}		RECORD_soundCb_var;




//
//
typedef  struct  __sampleGrabberCbCache_t						{
                 BYTE										*	pBuf;				//  2009/12/24. cache audio data to make output pkt the same size
                 unsigned  int									uiBufSize;
                 unsigned  int									nAvgBytesPerSec;
                 unsigned  int									nBytes_pkt;
                 unsigned  int									nLeft;
                 int											iSampleTimeInMs_left;
}		 SAMPLE_grabberCb_cache;

//
typedef  HRESULT  (  *  PF_BufferCB_av  )(  void  *  pProcInfoParam,  int  iIndex_capProcInfo,  void  *  pSampleGrabberCbVar,  int  iSampleTimeInMs,  BYTE  *  pBuffer,  long  BufferLen  );

//
typedef  struct  __sampleGrabberCbVar_t							{
                 void										*	pProcInfo;
                 unsigned  int									uiCapType;
                 int											iIndex_capProcInfo;
                 BOOL											bAudio;
                 //
                 void										*	pDmoInfo;				//  2010/06/14
                 //
#if  0
                 BYTE										*	pBuf;					//  2009/12/07, for making myDRAW_VIDEO_DATA, myPLAY_AUDIO_DATA
                 unsigned  int									uiBufSize_pBuf;
#endif

                 void										*	pMsgBuf;				//  2009/12/12
                 unsigned  int									uiBufSize_pMsgBuf;

                 //
                 SAMPLE_grabberCb_cache							cache;

                 //
                 PF_BufferCB_av									pf_BufferCB;

                 // the following is not usable
                 DWORD											firstTickCnt;
                 int											iFrames;
                 int											iSampleTimeInMs;

                 //  2012/06/29. 用来计算frame的时间戳
                 //  int										iOldVal_iSampleTimeInMs;
                 //  int										iOldVal_timeGetTime;
                 int											iSampleTimeInMs_org_start;
                 int											iSampleTimeInMs_start;
                 int											iSampleTimeInMs_org_expected;

                 //  2012/11/11
                 int											iSampleTimeInMs_org_last;
                 int											iSampleTimeInMs_last;


                 //  2012/11/10
                 long											tn_pktNo;
                 long											lLastPktNo;			//  2012/11/09

}		 SAMPLE_grabberCb_var;



typedef  int  (  *  PF_showNotification  )(  mytime_t  tStartTime,  unsigned  int  uiTranNo,  unsigned  int  uiContentType,  LPCTSTR  hint  );
typedef  int  (  *  PF_showInfo  )(  void  *  p0,  LPCTSTR  tWhere,  LPCTSTR  hint  );


//
//  2015/10/30
#ifndef  __LINUX__
typedef  HRESULT  ( WINAPI *PF_DXUTSaveTextureToFile  )( _In_ void  *  pID3D11DeviceContext, _In_ void  *  pID3D11Resource_pSource, _In_ BOOL usedds, _In_z_ const wchar_t* szFileName );
#else
#define PF_DXUTSaveTextureToFile  void  *
#endif


//  2014/06/20
typedef  struct  __qisGuiOpen_t									{
#ifndef  __LINUX__
                 HINSTANCE										g_hInst;
#else
                 void  *                                        g_hInst;
#endif
                 LPCTSTR										pCusModuleDir;
                 PF_showNotification							pf_showNotification;
                 PF_showInfo									pf_showInfo;				//  2011/12/14
                 //
                 void  *										pf_qPostMsg;				//  2014/06/20
                 void  *  										pf_qGetMsg;
                 //
                 PF_commonHandler								pf_setDebugStep;			//  void  *  p0,  DEBUG_step_common  *  pDebugStepCommon,  nMyStep  //  2014/07/17
                 PF_pCommonHandler								pf_getDebugStep_sth;		//  void  *  p0,  int  iType,  void  *  pDebugStepTotal  )
                 //
                 unsigned  char									ucbAppX64;					//  2014/07/20

                 //  2015/10/03
                 void  *										pf_CreateDXGIFactory1;		//  2015/10/03

                 //
                 PF_DXUTSaveTextureToFile						pf_DXUTSaveTextureToFile;	//  2015/10/30

}		 QIS_guiOpen;


//
typedef  struct  __commonParam_t {
    void* p0;
    void* p1;
    void* p2;
    //  void	*			p3;
    //  void	*			p4;
}		 COMMON_PARAM;
#define		MACRO_makeCommonParam3(  p0Param,  p1Param,  p2Param,  commonParam  )	{  commonParam.p0  =  p0Param;  commonParam.p1  =  p1Param;  commonParam.p2  =  p2Param;  }




//
#ifdef  __cplusplus
extern  "C"  {
#endif


#ifdef  __cplusplus
}
#endif

//  2016/04/29
#define  M_safeTcsnCpy(  strIn,  strOut,  size  )		lstrcpyn(  strOut,  strIn,  size  )
#define	 M_qtc_tcsCat(  pQtc,  strIn  )		if  (  pQtc  &&  strIn  )  _sntprintf_s(  pQtc->errBuf,  mycountof(  pQtc->errBuf  ),  _TRUNCATE,  _T(  "%s%s%s"  ),  pQtc->errBuf,  (  pQtc->errBuf[0]  ?  _T(  " | "  )  :  _T(  ""  )  ),  strIn  );


//
__declspec(  dllexport  )  TCHAR  *  getResStr(  void  *  p0,  void  *  pQnmCusResInfo,  int  iResId  );
//
int  cusDlgRes(  void  *  p0,  void  *  pQnmCusResInfo,  myHWND  hDlg,  int  IDD  );





#endif // QYDEFS_OPEN_H
