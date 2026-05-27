
//#include	"stdafx.h"

#include	<stdlib.h>
//#include	<tchar.h>
#include	<stddef.h>
//#include	<mmsystem.h>
#include <string.h>

#include "tmpFfmpegDefs.h"
//#include	"qdcOpenCommon.h"
#include "qyDefs_open.h"
#include "qyCommProc_open.h"
//#include	<assert.h>


//
 int  parseSpsPps_h264(  char  *  spsPps,  int  spsPpsLen,  char  *  spsBuf,  unsigned  int  *  puiSpsSize,  char  *  ppsBuf,  unsigned  int  *  puiPpsSize  );
 int  parseSpsPps_h265(  char  *  spsPps,  int  spsPpsLen,  char  *  spsBuf,  unsigned  int  *  puiSpsSize,  char  *  ppsBuf,  unsigned  int  *  puiPpsSize,  char  *  vps,  unsigned  int  *  puiVpsSize  );






//
 //int  parseSpsPps(  int  iFourcc,  char  *  spsPps,  int  spsPpsLen,  char  *  spsBuf,  unsigned  int  *  puiSpsSize,  char  *  ppsBuf,  unsigned  int  *  puiPpsSize,  char  *  kk,  unsigned  int  *  puiKkSize  )
 int  parseSpsPps(  int  iFourcc,  char  *  spsPps,  int  spsPpsLen,  char  *  spsBuf,  unsigned  int  *  puiSpsSize,  char  *  ppsBuf,  unsigned  int  *  puiPpsSize,  char  *  vps,  unsigned  int  *  puiVpsSize  )
 {
	 int  tmpiRet  =  -1;
	 //
	 switch  (  iFourcc  )  {
			 case  CONST_fourcc_h264:
				   tmpiRet  =  parseSpsPps_h264(  spsPps,  spsPpsLen,  spsBuf,  puiSpsSize,  ppsBuf,  puiPpsSize  );
				   if  (  puiVpsSize  )  *puiVpsSize  =  0;
				   return  tmpiRet;
				   break;
			 case  CONST_fourcc_HEVC:
				   return  parseSpsPps_h265(  spsPps,  spsPpsLen,  spsBuf,  puiSpsSize,  ppsBuf,  puiPpsSize,  vps,  puiVpsSize  );
				   break;
			 default:
					#ifdef  _DEBUG
							//assert(  0  );
							//
							OutputDebugString(  _T(  "parseSpsPps warning, unprocessed iFourcc\n"  )  );
					#endif
					//
					//
					break;
	 }

	 //
	 if  (  puiVpsSize  )  *puiVpsSize  =  0;
	 if  (  puiSpsSize  )  *puiSpsSize  =  0;
	 if  (  puiPpsSize  )  *puiPpsSize  =  0;

	 return  0;
 }



 //
#if 0
 int  parseSpsPps1(  int  iFourcc,  char  *  spsPps,  int  spsPpsLen,  VH_stream  *  pVh_stream  )
 {
	 int  iErr  =  -1;
	 //
	 if  (  !spsPps  )  return  -1;
	 if  (  !pVh_stream  )  return  -1;

	 //
	 memset(  pVh_stream,  0,  sizeof(  pVh_stream[0]  )  );

	 //
	 pVh_stream->dwFourcc  =  iFourcc;

	 //
	 unsigned  int  vpsLen  =  0;
	 char  *  pVps  =  NULL;
	 unsigned  int  spsLen  =  0;
	 char  *  pSps  =  NULL;
	 unsigned  int  ppsLen  =  0;
	 char  *  pPps  =  NULL;

	 //
	 switch  (  iFourcc  )  {		
			 case  CONST_fourcc_HEVC:  {				
				   //
				   int  k  =  0;					
				   vpsLen  =  sizeof(  pVh_stream->mems[k].spsPps  );  pVps  =  pVh_stream->mems[k].spsPps;  k  ++  ;								
				   spsLen  =  sizeof(  pVh_stream->mems[k].spsPps  );  pSps  =  pVh_stream->mems[k].spsPps;  k  ++  ;								
				   ppsLen  =  sizeof(  pVh_stream->mems[k].spsPps  );  pPps  =  pVh_stream->mems[k].spsPps;				  								
				   //								
				   if  (  parseSpsPps(  iFourcc,  spsPps,  spsPpsLen,  pSps,  &spsLen,  pPps,  &ppsLen,  pVps,  &vpsLen  )  )  goto  errLabel;					
				   //							
				   k  =  0;					
				   pVh_stream->mems[k].usLen  =  vpsLen;  k  ++  ;							
				   pVh_stream->mems[k].usLen  =  spsLen;  k  ++  ;				  								
				   pVh_stream->mems[k].usLen  =  ppsLen;  k  ++  ;				  								
				   pVh_stream->usCnt  =  k;				  
				   //
				   }
				   break;
			 case  CONST_fourcc_h264:
			 default:
				    {							
						int  k  =  0;					
						spsLen  =  sizeof(  pVh_stream->mems[k].spsPps  );  pSps  =  pVh_stream->mems[k].spsPps;  k  ++  ;								
						ppsLen  =  sizeof(  pVh_stream->mems[k].spsPps  );  pPps  =  pVh_stream->mems[k].spsPps;				  							
						//				  
						if  (  parseSpsPps(  iFourcc,  spsPps,  spsPpsLen,  pSps,  &spsLen,  pPps,  &ppsLen,  pVps,  &vpsLen  )  )  goto  errLabel;									   
						//						
						pVh_stream->mems[0].usLen  =  spsLen;				  							
						pVh_stream->mems[1].usLen  =  ppsLen;				  								
						pVh_stream->usCnt  =  2;				  						

					}
					 break;
	 }

	 
	 iErr  =  0;
errLabel:


	 return  iErr;
 }
#endif



//
 int  parseSpsPps_h264(  char  *  spsPps,  int  spsPpsLen,  char  *  spsBuf,  unsigned  int  *  puiSpsSize,  char  *  ppsBuf,  unsigned  int  *  puiPpsSize  )
{
	int			iErr	=	-1;
	 unsigned  char  buf[2048];
	 unsigned  int	naluLen  =  0;
	 unsigned  int	uiSpsLen	=	0;
	 unsigned  int	uiPpsLen	=	0;

	 //
	 if  (  !spsPps  )  return  -1;
	 if  (  !spsBuf  ||  !puiSpsSize  ||  !ppsBuf  ||  !puiPpsSize  )  {
		 return  -1;
	 }

	 //
	 if  (  spsPpsLen  >  sizeof(  buf  )  )  {
		 //  return  -1;
		 //OutputDebugString(  _T(  "parseSpsPps :inputLen > sizeof(  buf  )"  )  );
	 }
	 //  int  len  =  spsPpsLen;
	 int  len  =  min(  spsPpsLen,  sizeof(  buf  )  );
	 if  (  !len  )  {
		 *puiSpsSize  =  0;
		 *puiPpsSize  =  0;
		 return  0;
	 }
	 memcpy(  buf,  spsPps,  len  );
	 
	 
	 int  ii;

	 unsigned  char  *  pStart  =  buf;
	 BOOL				bStart	=	FALSE;
	 BOOL			   bNextNalu  =  FALSE;
	 BOOL				bStartCode_4	=	FALSE;
	 for  (  ii  =  0;  ii  <  len  -  3;  ii  ++  )  {
		  if  (  buf[ii]  ==  0
			  &&  buf[ii  +  1]  ==  0
			  &&  buf[ii  +  2]  ==  0
			  &&  buf[ii  +  3]  ==  1  )
		  {
			  if  (  !bStart  )  {
				  bStart  =  TRUE;
				  pStart  =  buf  +  ii;
				  ii  +=  3;
				  continue;
				  }
			  else  {
					bNextNalu  =  TRUE;
					bStartCode_4  =  TRUE;
			  }
		  }
		  if  (  buf[ii]  ==  0
			  &&  buf[ii  +  1]  ==  0
			  &&  buf[ii  +  2]  ==  1  )
		  {
			  if  (  !bStart  )  {
				  bStart  =  TRUE;
				  pStart  =  buf  +  ii;
				  ii  +=  2;
				  continue;
				  }
			  else  {
				    bNextNalu  =  TRUE;
					bStartCode_4  =  FALSE;
			  }
		  }
		  //
		  if  (  !bNextNalu  )  continue;
		  //
		  naluLen  =  buf  +  ii  -  pStart;
		  if  (  naluLen  >  3  )  {
			  int  nalType  =  M_nalType_h264(  pStart  );
			  switch  (  nalType  )  {
					  case  7:
						     if  (  naluLen  >  *puiSpsSize  )  goto  errLabel;
						     memcpy(  spsBuf,  pStart,  naluLen  );
							 uiSpsLen  =  naluLen;
							 break;
					  case  8:
						    if  (  naluLen  >  *puiPpsSize  )  goto  errLabel;
							memcpy(  ppsBuf,  pStart,  naluLen  );
							uiPpsLen  =  naluLen;
						    break;
					  default:
						     break;
			  }
		  }
		  //
		  bNextNalu  =  FALSE;
		  bStart  =  TRUE;
		  pStart  =  buf  +  ii;
		  if  (  bStartCode_4  )  ii  +=  3;
		  else  ii  +=  2;
		  continue;
	 }

	 //
	 if  (  bStart  )  {
		 naluLen  =  buf  +  len  -  pStart;
		  if  (  naluLen  >  3  )  {
			  int  nalType  =  M_nalType_h264(  pStart  );
			  switch  (  nalType  )  {
					  case  7:
						     if  (  naluLen  >  *puiSpsSize  )  goto  errLabel;
						     memcpy(  spsBuf,  pStart,  naluLen  );
							 uiSpsLen  =  naluLen;
							 break;
					  case  8:
						    if  (  naluLen  >  *puiPpsSize  )  goto  errLabel;
							memcpy(  ppsBuf,  pStart,  naluLen  );
							uiPpsLen  =  naluLen;
						    break;
					  default:
						     break;
			  }
		  }
	 }

	 *puiSpsSize  =  uiSpsLen;
	 *puiPpsSize  =  uiPpsLen;

	 iErr  =  0;
errLabel:
	 
	 //
	 return  iErr;

}


//  2017/10/10
int  nalType_h265(  unsigned  char  *  pBuf,  int  bufLen  )
{
	unsigned  int  code  =  -1;
	//
	if  (  bufLen  <=  3  )  return  0;
	int  iType  =  0;
	int  i;

	for  (  i  =  0;  i  <  bufLen  -  1;  i  ++  )  {
		 code  =  (  code  <<  8  )  +  pBuf[i];
		 if  (  (  code  &  0xffffff00  )  ==  0x100  )  {
			 char  na12  =  pBuf[i  +  1];
			 //
			 int  type  =  (  code  &  0x7E  )  >>  1;
			 //
			 if  (  code  &  0x81  )  return  0;  //  forbidden and reserved zero bits
			 //
			 if  (  na12  &  0xf8  )  return  0;  //  reserved zero

			 //
			 return  type;
		 }
	}


	return  iType;
}


//
#define		CONST_h264nalType_idr			5
#define		CONST_h264nalType_sps			7
#define		CONST_h264nalType_pps			8

//
#define		CONST_h265naleType_CODED_SLICE_IDR			19		//  NAL_UNIT_CODED_SLICE_IDR, // 19 // Current name in the spec: IDR_W_DLP
#define		CONST_h265nalType_CODED_SLICE_IDR_N_LP		20		//  NAL_UNIT_CODED_SLICE_IDR_N_LP, // 20
//
#define		CONST_h265nalType_vps			32
#define		CONST_h265nalType_sps			33
#define		CONST_h265nalType_pps			34



//
//  2017/10/10
int  parseSpsPps_h265(  char  *  spsPps,  int  spsPpsLen,  char  *  spsBuf,  unsigned  int  *  puiSpsSize,  char  *  ppsBuf,  unsigned  int  *  puiPpsSize,  char  *  vpsBuf,  unsigned  int  *  puiVpsSize  )
{
	int			iErr	=	-1;
	 unsigned  char  buf[2048];
	 unsigned  int	naluLen  =  0;
	 //
	 unsigned  int  uiVpsLen	=	0;
	 unsigned  int	uiSpsLen	=	0;
	 unsigned  int	uiPpsLen	=	0;

	 //
	 if  (  !spsPps  )  return  -1;
	 if  (  !spsBuf  ||  !puiSpsSize  ||  !ppsBuf  ||  !puiPpsSize  ||  !vpsBuf  ||  !puiVpsSize  )  {
		 return  -1;
	 }

	 //
	 if  (  spsPpsLen  >  sizeof(  buf  )  )  {
		 //  return  -1;
		 //OutputDebugString(  _T(  "parseSpsPps :inputLen > sizeof(  buf  )"  )  );
	 }
	 //  int  len  =  spsPpsLen;
	 int  len  =  min(  spsPpsLen,  sizeof(  buf  )  );
	 if  (  !len  )  {
		 *puiSpsSize  =  0;
		 *puiPpsSize  =  0;
		 return  0;
	 }
	 memcpy(  buf,  spsPps,  len  );
	 
	 
	 int  ii;

	 unsigned  char  *  pStart  =  buf;
	 BOOL				bStart	=	FALSE;
	 BOOL			   bNextNalu  =  FALSE;
	 BOOL				bStartCode_4	=	FALSE;
	 for  (  ii  =  0;  ii  <  len  -  3;  ii  ++  )  {
		  if  (  buf[ii]  ==  0
			  &&  buf[ii  +  1]  ==  0
			  &&  buf[ii  +  2]  ==  0
			  &&  buf[ii  +  3]  ==  1  )
		  {
			  if  (  !bStart  )  {
				  bStart  =  TRUE;
				  pStart  =  buf  +  ii;
				  ii  +=  3;
				  continue;
				  }
			  else  {
					bNextNalu  =  TRUE;
					bStartCode_4  =  TRUE;
			  }
		  }
		  if  (  buf[ii]  ==  0
			  &&  buf[ii  +  1]  ==  0
			  &&  buf[ii  +  2]  ==  1  )
		  {
			  if  (  !bStart  )  {
				  bStart  =  TRUE;
				  pStart  =  buf  +  ii;
				  ii  +=  2;
				  continue;
				  }
			  else  {
				    bNextNalu  =  TRUE;
					bStartCode_4  =  FALSE;
			  }
		  }
		  //
		  if  (  !bNextNalu  )  continue;
		  //
		  naluLen  =  buf  +  ii  -  pStart;
		  if  (  naluLen  >  3  )  {
			  int  nalType  =  nalType_h265(  pStart,  naluLen  );
			  switch  (  nalType  )  {
					  case  CONST_h265nalType_vps:
						     if  (  naluLen  >  *puiVpsSize  )  goto  errLabel;
						     memcpy(  vpsBuf,  pStart,  naluLen  );
							 uiVpsLen  =  naluLen;
							 break;
					  case  CONST_h265nalType_sps:
						     if  (  naluLen  >  *puiSpsSize  )  goto  errLabel;
						     memcpy(  spsBuf,  pStart,  naluLen  );
							 uiSpsLen  =  naluLen;
							 break;
					  case  CONST_h265nalType_pps:
						    if  (  naluLen  >  *puiPpsSize  )  goto  errLabel;
							memcpy(  ppsBuf,  pStart,  naluLen  );
							uiPpsLen  =  naluLen;
						    break;
					  default:
						     break;
			  }
		  }
		  //
		  bNextNalu  =  FALSE;
		  bStart  =  TRUE;
		  pStart  =  buf  +  ii;
		  if  (  bStartCode_4  )  ii  +=  3;
		  else  ii  +=  2;
		  continue;
	 }

	 //
	 if  (  bStart  )  {
		 naluLen  =  buf  +  len  -  pStart;
		  if  (  naluLen  >  3  )  {
			  int  nalType  =  nalType_h265(  pStart,  naluLen  );
			  switch  (  nalType  )  {
				      case  CONST_h265nalType_vps:
						     if  (  naluLen  >  *puiVpsSize  )  goto  errLabel;
						     memcpy(  vpsBuf,  pStart,  naluLen  );
							 uiVpsLen  =  naluLen;
							 break;					
					  case  CONST_h265nalType_sps:
						     if  (  naluLen  >  *puiSpsSize  )  goto  errLabel;
						     memcpy(  spsBuf,  pStart,  naluLen  );
							 uiSpsLen  =  naluLen;
							 break;
					  case  CONST_h265nalType_pps:
						    if  (  naluLen  >  *puiPpsSize  )  goto  errLabel;
							memcpy(  ppsBuf,  pStart,  naluLen  );
							uiPpsLen  =  naluLen;
						    break;
					  default:
						     break;
			  }
		  }
	 }

	 //
	 *puiVpsSize  =  uiVpsLen;
	 *puiSpsSize  =  uiSpsLen;
	 *puiPpsSize  =  uiPpsLen;

	 iErr  =  0;
errLabel:
	 
	 //
	 return  iErr;

}





//
/*
int  makeSpsPps(  VH_stream  *  pVh_stream,  char  *  spsPpsParam,  unsigned  int  *  puiSize  )
{
	int  iErr  =  -1;
	char  spsPps[512]  =  "";
	int	spsLen  =  0;
	int  i;

	if  (  !pVh_stream  ||  !spsPps  ||  !puiSize  )  return  -1;

	if  (  pVh_stream->usCnt  )   {
		 if  (  pVh_stream->usCnt  >  mycountof(  pVh_stream->mems  )  )  goto  errLabel;
		 for  (  i  =  0;  i  <  pVh_stream->usCnt;  i  ++  )  {
			  if  (  spsLen  +  pVh_stream->mems[i].usLen  >  sizeof(  spsPps  )  )  {
				  //qyShowInfo1(  CONST_qyShowType_qwmComm,  NULL,  (  ""  ),  _T(  "IsClient"  ),  0,  _T(  ""  ),  _T(  ""  ),  _T(  "htonAvTranInfo failed: spsLen too small"  )  );
				  goto  errLabel;
			  }
			  memcpy(  spsPps  +  spsLen,  pVh_stream->mems[i].spsPps,  pVh_stream->mems[i].usLen  );
			  spsLen  +=  pVh_stream->mems[i].usLen;
		 }
	}
	if  (  *puiSize  <  spsLen  )  goto  errLabel;
	memcpy(  spsPpsParam,  spsPps,  spsLen  );
	*puiSize  =  spsLen;

	iErr  =  0;
errLabel:

	if  (  !iErr  )  {

	}

	return  iErr;

}
 */


