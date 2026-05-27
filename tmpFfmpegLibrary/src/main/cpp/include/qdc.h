//
// Created by qycx on 2021/1/30.
//

#ifndef TMPFFMPEG_QDC_H
#define TMPFFMPEG_QDC_H

#ifdef  __cplusplus
extern "C" {
#endif

#include "libavcodec/avcodec.h"
#include "libavutil/opt.h"
#include "libavutil/imgutils.h"

#ifdef  __cplusplus
}
#endif

#include "tmpFfmpegDefs.h"
#include "qySyncObj.h"

//
typedef struct __decBuf_t {
     byte * pBuf;
     int iBufSize;
     int len;
     //
     int bUsed;
} DEC_buf;

//
typedef struct __decBufs_t {
    int maxCnt_decBufMems;

    //
#define CONST_cntof_decBuf_mems2  120
#define CONST_cntof_decBuf_mems_v  30
#define CONST_cntof_decBuf_mems_a  CONST_cntof_decBuf_mems2
    //
    DEC_buf mems[CONST_cntof_decBuf_mems2];
} DEC_bufs;

//
typedef struct __qdc_uint_t {
    mybool bUsed;

    //
    struct {
        //
        int iFourcc;

        //
        AVCodec *codec;
        AVCodecContext *c;
        AVFrame *frame;
        AVPacket *pkt;
        //
        int pts;
        int framecnt;
    }  encV_var;

    //
    struct {
        //
        int iFourcc;

        //
        const AVCodec *codec;
        AVCodecParserContext *parser;
        AVCodecContext *c;
        FILE *f;
        AVFrame *frame;
        AVPacket *pkt;

        //
        int iFourcc_dec;
        int iW;
        int iH;

        //
        //int iBufSize_decBuf;

        //
        FILE * fo;

    } decV_var;

    //
    struct {
        ushort        wFormatTag;         /* format type */

        AVCodec *codec;
        AVCodecContext *c;
        AVFrame *frame;
        AVPacket *pkt;

        //
        struct {
            char * pBuf;
            int iBufSize;
            int len;
        }cache;

        //int i, j, k, ret;
        //FILE *f_i;
        FILE *f;


    } encA_var;

    //
    struct {
        AVCodec *codec;
        AVCodecContext *c;
        AVCodecParserContext *parser;
        //int len, ret;
        //FILE *f, *outfile;
        //uint8_t inbuf[AUDIO_INBUF_SIZE + AV_INPUT_BUFFER_PADDING_SIZE];
        //uint8_t *data;
        //size_t   data_size;
        AVPacket *pkt;
        AVFrame *decoded_frame;

        //
        struct {
            char *pBuf;//[AUDIO_INBUF_SIZE + AV_INPUT_BUFFER_PADDING_SIZE];
            int iBufSize;
            int len;
        } cache;

        //
        FILE * outfile;


    }decA_var;

    //
    DEC_bufs    decBufs;

    //
    mybyte  ucAudioCompressors;     //  采用哪种编解码方式

    //
    //MyMutex     mtx_syncInput;

    //
    void    *   pTransObj;

    //
} QDC_unit;

//
#ifdef  __cplusplus

//
class CQdc {
public:
    QDC_unit *m_pMems;
    ushort m_usMaxCnt;

    //
    bool bDone_qdc_init;

    //
    CQdc();
    ~CQdc();

private:
    int init();
    void exit();
};

extern  CQdc g_qdc;

#endif


#endif //TMPFFMPEG_QDC_H
