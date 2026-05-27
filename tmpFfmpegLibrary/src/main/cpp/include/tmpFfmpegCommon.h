
#ifndef __tmpFfmpegCommon_h__
#define __tmpFfmpegCommon_h__ // {

#ifdef  __cplusplus
extern "C" {
#endif

#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libavutil/opt.h"
#include "libavutil/imgutils.h"
#include "libavutil//mathematics.h"

    //
#include "libavutil/channel_layout.h"
#include "libavutil/samplefmt.h"
#include "libswresample/swresample.h"

    //
#ifdef  __cplusplus
}
#endif

#include <android/log.h>
//const char *LOG_TGA="LOG_TGA";
#define LOG_TAG "anc"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG ,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG ,__VA_ARGS__)

//
#include    "qyDefs_open.h"
#include    "qyCommProc_open.h"

#include    "tmpFfmpegDefs.h"

//
#include "qdc.h"

//
int  parseSpsPps(  int  iFourcc,  char  *  spsPps,  int  spsPpsLen,  char  *  spsBuf,  unsigned  int  *  puiSpsSize,  char  *  ppsBuf,  unsigned  int  *  puiPpsSize,  char  *  vps,  unsigned  int  *  puiVpsSize  );


//
int newQdcUnitIndex(mybool bDbg);
void freeQdcUnitIndex(int index,mybool bDbg);
QDC_unit * getQdcUnitByIndex(int index);

//
int doTest(int index_qdcUnit, char * i_str, char *o_str, int bufSize_o_str );
int saveFrame(AVFrame * picture,int w,int h, FILE * fo);
int freeDecBufs(int index_qdcUnit);


//
int ffmpeg_qdc_init();
//int ffmpeg_qdc_exit();

//
int ffmpeg_qdc_exit1();


//
int encV_init(int index_qdcUnit, int iFourcc, int in_w,int in_h,mybool bDbg);
int encV_exit(int index_qdcUnit,mybool bDbg);
int encV_getSpsPps(int index_qdcUnit, byte * spsBuf, int * puiSpsSize, byte * ppsBuf, int * puiPpsSize, byte * vps, int * puiVpsSize,mybool bDbg);
int encV_data(int index_qdcUnit,byte * yuv,int yuvLen,byte * raw,int *pLen_raw, mybool bDbg);

//
int decV_init(int index_qdcUnit, int iFourcc, mybool bDbg);
int decV_exit(int index_qdcUnit,mybool bDbg);
int decV_data(int index_qdcUnit, byte * data,int data_size, int iInputBufferPaddingSize, int *pnPktDeced, int * piFourcc_dec, int *piW,int *piH, byte * decBuf, int *piDecBufSize, mybool *pbBufTooSmall, mybool bDbg);

//
int dec_getOutputByIndex(int index_qdcUnit, int index, byte * pbOutData, uint * puiDataSize,mybool bDbg);
int dec_clearOutputs(int index_qdcUnit,mybool bDbg);

//
int encA_init(int index_qdcUnit, byte ucCompressors, short wFormatTag, int bitrate, mybool bDbg);
int encA_exit(int index_qdcUnit,mybool bDbg);
int encA_data(int index_qdcUnit,byte * pcm,int pcmLen,byte * raw,int *pLen_raw, mybool bDbg);

//
int decA_init(int index_qdcUnit, byte ucCompressors,short wFormatTag, mybool bDbg);
int decA_exit(int index_qdcUnit,mybool bDbg);
int decA_data(int index_qdcUnit, byte * encData,int encData_size, int iInputBufferPaddingSize, int *pnPktDeced, int * piFmt_dec, byte * decBuf, int *piDecBufSize, mybool *pbBufTooSmall, mybool bDbg);

//
int melp_encA_getOutputByIndex(int index_qdcUnit,int index, byte *buf, int *piBufLen, int *piPts,int *piSampleTimeInMs, mybool bDbg);



#endif // }


