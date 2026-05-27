//
// Created by qycx on 2021/3/3.
//

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <string>

#include "tmpFfmpegCommon.h"
#include    "myMelpPublic.h"

//
#define myAUDIO_REFILL_THRESH 4096


int decA_init(int index_qdcUnit, byte ucCompressors, short wFormatTag, mybool bDbg) {
    int iErr = -1;
    QDC_unit *pUnit = getQdcUnitByIndex(index_qdcUnit);
    if (!pUnit) return -1;

    //
    if (!g_qdc.bDone_qdc_init) return -1;

    //
    //
    pUnit->ucAudioCompressors  =  ucCompressors;

    //
    switch  (  ucCompressors)  {
        case  CONST_audioCompressors_melp:
            return  decA_init_melp(index_qdcUnit,ucCompressors,wFormatTag,bDbg);
            break;
        default:
            break;
    }

    //
    AVCodecID codec_id = AV_CODEC_ID_MP3;


    //
    pUnit->decA_var.pkt = av_packet_alloc();

    /* find the MPEG audio decoder */
    pUnit->decA_var.codec = avcodec_find_decoder(codec_id);
    if (!pUnit->decA_var.codec) {
        fprintf(stderr, "Codec not found\n");
        goto errLabel;
    }

    pUnit->decA_var.parser = av_parser_init(pUnit->decA_var.codec->id);
    if (!pUnit->decA_var.parser) {
        fprintf(stderr, "Parser not found\n");
        //exit(1);
        goto errLabel;
    }

    pUnit->decA_var.c = avcodec_alloc_context3(pUnit->decA_var.codec);
    if (!pUnit->decA_var.c) {
        fprintf(stderr, "Could not allocate audio codec context\n");
        goto errLabel;
    }

    /* open it */
    if (avcodec_open2(pUnit->decA_var.c, pUnit->decA_var.codec, NULL) < 0) {
        fprintf(stderr, "Could not open codec\n");
        goto errLabel;
    }

    /*
    f = fopen(filename, "rb");
    if (!f) {
        fprintf(stderr, "Could not open %s\n", filename);
        exit(1);
    }
    outfile = fopen(outfilename, "wb");
    if (!outfile) {
        av_free(c);
        exit(1);
    }
     */

    //
    pUnit->decA_var.cache.iBufSize=200000;
    pUnit->decA_var.cache.pBuf=(char*)mymalloc(pUnit->decA_var.cache.iBufSize+AV_INPUT_BUFFER_PADDING_SIZE);
    if(pUnit->decA_var.cache.pBuf==NULL)goto errLabel;

    //
    pUnit->decBufs.maxCnt_decBufMems=CONST_cntof_decBuf_mems_a;


    //
    iErr = 0;
    errLabel:
    if(iErr){
        decA_exit(index_qdcUnit,bDbg);
    }

    return iErr;

}

int decA_exit(int index_qdcUnit,mybool bDbg) {
    int iErr = -1;
    QDC_unit *pUnit = getQdcUnitByIndex(index_qdcUnit);
    if (!pUnit) return -1;

    //
    switch  (  pUnit->ucAudioCompressors  )  {
        case  CONST_audioCompressors_melp:
            return decA_exit_melp(index_qdcUnit,bDbg);
            break;
        default:
            break;
    }
    
    //
    if(pUnit->decA_var.cache.pBuf){
        myfree(pUnit->decA_var.cache.pBuf); pUnit->decA_var.cache.pBuf=NULL;
    }

    //
    freeDecBufs(index_qdcUnit);

    //
    avcodec_free_context(&pUnit->decA_var.c);
    av_parser_close(pUnit->decA_var.parser);
    av_frame_free(&pUnit->decA_var.decoded_frame);
    av_packet_free(&pUnit->decA_var.pkt);

    iErr = 0;
    errLabel:
    return iErr;
}

//
int frame2Buf_a(AVCodecContext*dec_ctx,AVFrame * frame, int bytes_per_sample, byte * pcm,FILE * outfile)
{
    int i;
    int ch;

    /*
    int data_size=bytes_per_sample;
    for (i = 0; i < frame->nb_samples; i++)
        for (ch = 0; ch < dec_ctx->channels; ch++)
            fwrite(frame->data[ch] + data_size*i, 1, data_size, outfile);
            */


    byte *p=pcm;
    for ( i=0;i<frame->nb_samples;i++){
        for (ch=0;ch<dec_ctx->channels;ch ++){
            //
            memcpy(p,frame->data[ch]+bytes_per_sample*i,bytes_per_sample);
            p+=bytes_per_sample;
        }
    }


    return 0;
}


int mysaveFrame_a(int index_qdcUnit, AVCodecContext *dec_ctx, AVFrame * frame,int bytes_per_sample,
                  int *pnPktDeced,
                  byte * decBuf, int *piDecBufSize, mybool *pbBufTooSmall,
                  FILE * fo,
                  mybool bDbg)
{
    int iErr = -1;
    QDC_unit *pUnit = getQdcUnitByIndex(index_qdcUnit);
    if (!pUnit) return -1;

    //
    byte * p;

    /*
    fwrite(picture->data[0],yLine,1,fo);
    fwrite(picture->data[1],yLine/4,1,fo);
    fwrite(picture->data[2],yLine/4,1,fo);
     */
    (*pnPktDeced)++;


    //
    int imgSize=frame->nb_samples*dec_ctx->channels*bytes_per_sample;//frame-> w*h*3/2;
    if ( decBuf==NULL || imgSize>*piDecBufSize) {
        *pbBufTooSmall=true;
    }


    if ( *pbBufTooSmall||*pnPktDeced>1) {
        int i;
        //
        for (i = 0; i < pUnit->decBufs.maxCnt_decBufMems; i++) {
            DEC_buf *pMem = &pUnit->decBufs.mems[i];
            if (!pMem->bUsed) break;
        }
        if (i == pUnit->decBufs.maxCnt_decBufMems) {
            LOGD("maxCnt_decBufMems too small");
            goto errLabel;
        }

        DEC_buf *pMem = &pUnit->decBufs.mems[i];

        if( pMem->iBufSize<imgSize) {
            myfree(pMem->pBuf);pMem->pBuf=NULL;
        }

        if (!pMem->pBuf) {
            pMem->iBufSize=imgSize;
            pMem->pBuf = (byte *) mymalloc(pMem->iBufSize);
            if (!pMem->pBuf)goto errLabel;
        }
        p = pMem->pBuf;
        //
        /*
        memcpy(p, picture->data[0], yLine);
        memcpy(p + yLine, picture->data[1], yLine / 4);
        memcpy(p + yLine + yLine / 4, picture->data[2], yLine / 4);
         */
        frame2Buf_a(pUnit->decA_var.c,frame,bytes_per_sample,p,fo);
        //
        if(bDbg){
            LOGE("mySaveFrame: after frame2Buf");
        }
        //
        pMem->len=imgSize;
        //
        pMem->bUsed = true;
    }
    else {
        p=decBuf;
        //
        /*
         memcpy(p,picture->data[0],yLine);
         memcpy(p+yLine,picture->data[1],yLine/4);
         memcpy(p+yLine+yLine/4,picture->data[2],yLine/4);
         */
        frame2Buf_a(pUnit->decA_var.c,frame,bytes_per_sample,p,fo);
        //
        *piDecBufSize=imgSize;

        //
        //fwrite(decBuf,imgSize,1,fo);
    }

    iErr = 0;
    errLabel:
    return iErr;
}



//
static int mydecodeA(int index_qdcUint, AVCodecContext *dec_ctx, AVPacket *pkt, AVFrame *frame,
                      int *pnPktDeced, byte * decBuf, int *piDecBufSize, mybool *pbBufTooSmall,
                      FILE *outfile,
                      mybool bDbg )
{
    int iErr = -1;
    int i, ch;
    int ret, data_size;

    //
    if(bDbg){
        LOGE("mydecodeA enters");
    }

    /* send the packet with the compressed data to the decoder */
    ret = avcodec_send_packet(dec_ctx, pkt);
    if (ret < 0) {
        fprintf(stderr, "Error submitting the packet to the decoder\n");
        //exit(1);
        iErr = 0; goto errLabel;
        //return 0;
    }

    /* read all the output frames (in general there may be any number of them */
    while (ret >= 0) {
        ret = avcodec_receive_frame(dec_ctx, frame);
        if (ret == AVERROR(EAGAIN) || ret == AVERROR_EOF) {
            iErr = 0; goto errLabel;
            //return 0;
        }
        else if (ret < 0) {
            fprintf(stderr, "Error during decoding\n");
            goto errLabel;
        }
        data_size = av_get_bytes_per_sample(dec_ctx->sample_fmt);
        if (data_size < 0) {
            /* This should not occur, checking just for paranoia */
            fprintf(stderr, "Failed to calculate data size\n");
            goto errLabel;
        }
        /*
        for (i = 0; i < frame->nb_samples; i++)
            for (ch = 0; ch < dec_ctx->channels; ch++)
                fwrite(frame->data[ch] + data_size*i, 1, data_size, outfile);
                */
        mysaveFrame_a(index_qdcUint,dec_ctx,frame,data_size,pnPktDeced,decBuf,piDecBufSize,pbBufTooSmall,outfile,bDbg);

    }

    iErr = 0;

errLabel:
    //
    if(bDbg){
        LOGE("mydecodeA leaves with %d",iErr);
    }

    return iErr;
}



//
int decA_data(int index_qdcUnit, byte * encData,int encData_size, int iInputBufferPaddingSize, int *pnPktDeced, int * piFmt_dec, byte * decBuf, int *piDecBufSize, mybool *pbBufTooSmall, mybool bDbg)
{
    int iErr = -1;
    QDC_unit *pUnit = getQdcUnitByIndex(index_qdcUnit);
    if (!pUnit) return -1;

    uint8_t *data;
    size_t   data_size;
    int ret;

    //
    //
    switch  (  pUnit->ucAudioCompressors  )  {
        case  CONST_audioCompressors_melp:
              //
              return decA_data_melp( index_qdcUnit, encData,encData_size, iInputBufferPaddingSize, pnPktDeced,  piFmt_dec, decBuf, piDecBufSize, pbBufTooSmall, bDbg);

              //
              break;
        default:
            break;
    }



    //
    if ( !encData||!encData_size) {
        LOGE("decA_data failed, encData NULL or encData_size 0");
        return -1;
    }

    //
    if(encData_size+pUnit->decA_var.cache.len>pUnit->decA_var.cache.iBufSize){
        LOGE("decA_data failed, cache.iBufSize too small");
        return -1;
    }
    memcpy(pUnit->decA_var.cache.pBuf+pUnit->decA_var.cache.len,encData,encData_size);
    pUnit->decA_var.cache.len+=encData_size;

    //
    data=(uint8_t*)pUnit->decA_var.cache.pBuf;
    data_size=pUnit->decA_var.cache.len;

    while (data_size > 0) {
        if (!pUnit->decA_var.decoded_frame) {
            if (!(pUnit->decA_var.decoded_frame = av_frame_alloc())) {
                fprintf(stderr, "Could not allocate audio frame\n");
                goto errLabel;
            }
        }

        ret = av_parser_parse2(pUnit->decA_var.parser, pUnit->decA_var.c, &pUnit->decA_var.pkt->data, &pUnit->decA_var.pkt->size,
                               data, data_size,
                               AV_NOPTS_VALUE, AV_NOPTS_VALUE, 0);
        if (ret < 0) {
            fprintf(stderr, "Error while parsing\n");
            goto errLabel;
        }
        data      += ret;
        data_size -= ret;

        //
        int tmpiRet=0;

        //
        if (pUnit->decA_var.pkt->size) {
            tmpiRet=mydecodeA(index_qdcUnit,pUnit->decA_var.c, pUnit->decA_var.pkt, pUnit->decA_var.decoded_frame,pnPktDeced,decBuf,piDecBufSize,pbBufTooSmall,
                      pUnit->decA_var.outfile,
                      bDbg);
            if(tmpiRet) {
                LOGE("decA_data: mydecodeA failed");
            }
        }

        if (tmpiRet|| data_size < myAUDIO_REFILL_THRESH) {
            memmove(pUnit->decA_var.cache.pBuf, data, data_size);
            pUnit->decA_var.cache.len=data_size;
            //
            if(tmpiRet) {
                goto errLabel;
            }
            //
            /*
            data = inbuf;
            len = fread(data + data_size, 1,
                        AUDIO_INBUF_SIZE - data_size, f);
            if (len > 0)
                data_size += len;
                */
            break;
        }
    }

    //


    iErr = 0;
    errLabel:

    return iErr;
}