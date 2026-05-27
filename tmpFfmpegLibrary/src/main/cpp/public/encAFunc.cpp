//
// Created by qycx on 2021/3/1.
//

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

#include <string.h>
#include <string>

//
#include "tmpFfmpegCommon.h"
#include    "myMelpPublic.h"


/* check that a given sample format is supported by the encoder */
static int mycheck_sample_fmt(const AVCodec *codec, enum AVSampleFormat sample_fmt)
{
    const enum AVSampleFormat *p = codec->sample_fmts;

    while (*p != AV_SAMPLE_FMT_NONE) {
        if (*p == sample_fmt)
            return 1;
        p++;
    }
    return 0;
}

/* just pick the highest supported samplerate */
static int myselect_sample_rate(const AVCodec *codec)
{
    const int *p;
    int best_samplerate = 0;

    if (!codec->supported_samplerates)
        return 44100;

    p = codec->supported_samplerates;
    while (*p) {
        if (!best_samplerate || abs(44100 - *p) < abs(44100 - best_samplerate))
            best_samplerate = *p;
        p++;
    }
    return best_samplerate;
}

/* select layout with the highest channel count */
static int myselect_channel_layout(const AVCodec *codec)
{
    const uint64_t *p;
    uint64_t best_ch_layout = 0;
    int best_nb_channels   = 0;

    if (!codec->channel_layouts)
        return AV_CH_LAYOUT_STEREO;

    p = codec->channel_layouts;
    while (*p) {
        int nb_channels = av_get_channel_layout_nb_channels(*p);

        if (nb_channels > best_nb_channels) {
            best_ch_layout    = *p;
            best_nb_channels = nb_channels;
        }
        p++;
    }
    return best_ch_layout;
}



//
int encA_init(int index_qdcUnit, byte ucCompressors, short wFormatTag, int bitrate, mybool bDbg)
{
    int iErr = -1;
    QDC_unit *pUnit = getQdcUnitByIndex(index_qdcUnit);
    if (!pUnit) return -1;
    int i, j, k, ret;


    if (!g_qdc.bDone_qdc_init) return -1;

    //
    pUnit->ucAudioCompressors  =  ucCompressors;

    //
    switch  (  ucCompressors)  {
        case  CONST_audioCompressors_melp:
            return  encA_init_melp(index_qdcUnit,ucCompressors,wFormatTag,bitrate,bDbg);
            break;
        default:
            break;
    }

    //
    AVCodecID codec_id = AV_CODEC_ID_MP3;

    //
    pUnit->encA_var.wFormatTag = wFormatTag;
    switch (wFormatTag) {
        case WAVE_FORMAT_MPEGLAYER3:
            break;
        default:
            return -1;
    }

    //
    switch  (  bitrate)  {
        case  4000:
        case  8000:
        case  16000:
        case  24000:
        case  32000:
        case  64000:
              break;
        default:
            //
            LOGD("encA_init failed: unsupported: bitrate %d", bitrate);
            return  -1;
    }
    //
    LOGD("encA_init: bitrate %d", bitrate);

    //
    /* find the MP2 encoder */
    pUnit->encA_var.codec = avcodec_find_encoder(codec_id);
    if (!pUnit->encA_var.codec) {
        fprintf(stderr, "Codec not found\n");
        //exit(1);
        goto errLabel;
    }

    pUnit->encA_var.c = avcodec_alloc_context3(pUnit->encA_var.codec);
    if (!pUnit->encA_var.c) {
        fprintf(stderr, "Could not allocate audio codec context\n");
        //exit(1);
        goto errLabel;
    }

    /* put sample parameters */
    pUnit->encA_var.c->bit_rate = bitrate;//32000;//64000;

    /* check that the encoder supports s16 pcm input */
    pUnit->encA_var.c->sample_fmt = AV_SAMPLE_FMT_S16P;
    if (!mycheck_sample_fmt(pUnit->encA_var.codec, pUnit->encA_var.c->sample_fmt)) {
        fprintf(stderr, "Encoder does not support sample format %s",
                av_get_sample_fmt_name(pUnit->encA_var.c->sample_fmt));
        //exit(1);
        goto errLabel;
    }

    /* select other audio parameters supported by the encoder */
    pUnit->encA_var.c->sample_rate    = myselect_sample_rate(pUnit->encA_var.codec);
    pUnit->encA_var.c->channel_layout = myselect_channel_layout(pUnit->encA_var.codec);
    pUnit->encA_var.c->channels       = av_get_channel_layout_nb_channels(pUnit->encA_var.c->channel_layout);

    //
    pUnit->encA_var.c->sample_rate=16000;
    pUnit->encA_var.c->channel_layout=AV_CH_LAYOUT_MONO;
    pUnit->encA_var.c->channels=av_get_channel_layout_nb_channels(pUnit->encA_var.c->channel_layout);
    //pUnit->encA_var.c->bit_rate=4*pUnit->encA_var.c->sample_rate;



    /* open it */
    if (avcodec_open2(pUnit->encA_var.c, pUnit->encA_var.codec, NULL) < 0) {
        fprintf(stderr, "Could not open codec\n");
        //exit(1);
        goto errLabel;
    }

    /*
    f = fopen(filename, "wb");
    if (!f) {
        fprintf(stderr, "Could not open %s\n", filename);
        exit(1);
    }
     */

    /* packet for holding encoded output */
    pUnit->encA_var.pkt = av_packet_alloc();
    if (!pUnit->encA_var.pkt) {
        fprintf(stderr, "could not allocate the packet\n");
        //exit(1);
        goto errLabel;
    }

    /* frame containing input raw audio */
    pUnit->encA_var.frame = av_frame_alloc();
    if (!pUnit->encA_var.frame) {
        fprintf(stderr, "Could not allocate audio frame\n");
        //exit(1);
        goto errLabel;
    }

    pUnit->encA_var.frame->nb_samples     = pUnit->encA_var.c->frame_size;
    pUnit->encA_var.frame->format         = pUnit->encA_var.c->sample_fmt;
    pUnit->encA_var.frame->channel_layout = pUnit->encA_var.c->channel_layout;

    /* allocate the data buffers */
    ret = av_frame_get_buffer(pUnit->encA_var.frame, 0);
    if (ret < 0) {
        fprintf(stderr, "Could not allocate audio data buffers\n");
        //exit(1);
        goto errLabel;
    }


    iErr = 0;
    errLabel:
    if ( iErr){
        encA_exit(index_qdcUnit,bDbg);
    }

    return iErr;
}

int encA_exit(int index_qdcUnit,mybool bDbg)
{
    int iErr = -1;
    QDC_unit *pUnit = getQdcUnitByIndex(index_qdcUnit);
    if (!pUnit) return -1;

    //
    switch  (  pUnit->ucAudioCompressors  )  {
            case  CONST_audioCompressors_melp:
                  return encA_exit_melp(index_qdcUnit,bDbg);
                  break;
            default:
                    break;
    }


    //
    av_frame_free(&pUnit->encA_var.frame);
    av_packet_free(&pUnit->encA_var.pkt);
    avcodec_free_context(&pUnit->encA_var.c);

    //
    if(pUnit->encA_var.cache.pBuf){
        myfree(pUnit->encA_var.cache.pBuf);
        pUnit->encA_var.cache.pBuf=NULL;
    }


    iErr = 0;
    errLabel:

    return iErr;
}


static int myencodeA(AVCodecContext *ctx, AVFrame *frame, AVPacket *pkt,
                     byte * raw,int *pLen_raw, bool bDbg)
{
    int iErr = -1;
    int ret;
    int nGot=0;
    bool tmpbErr=false;

    /* send the frame for encoding */
    ret = avcodec_send_frame(ctx, frame);
    if (ret < 0) {
        fprintf(stderr, "Error sending the frame to the encoder\n");
        goto errLabel;
        //exit(1);
    }

    /* read all the available output packets (in general there may be any
     * number of them */
    while (ret >= 0) {
        ret = avcodec_receive_packet(ctx, pkt);
        if (ret == AVERROR(EAGAIN) || ret == AVERROR_EOF) {
            //return;
            break;
        }
        else if (ret < 0) {
            fprintf(stderr, "Error encoding audio frame\n");
            //exit(1);
            goto errLabel;
        }

        //fwrite(pkt->data, 1, pkt->size, output);
        if ( raw !=NULL && pLen_raw!=NULL ) {
            if (pkt->size > *pLen_raw - nGot) {
                tmpbErr=true;
            }
            else {
                memcpy(raw + nGot, pkt->data, pkt->size);
                nGot += pkt->size;
            }
        }

        //
        av_packet_unref(pkt);

        //
        if ( tmpbErr) {
            goto errLabel;
        }

    }

    if ( pLen_raw!=NULL) {
        *pLen_raw = nGot;
    }

    iErr = 0;
    errLabel:
    //
    if ( bDbg){
        LOGD("myencodeA: leaves iErr %d", iErr);
    }

    return iErr;
}


//
int encA_data(int index_qdcUnit,byte * pcm,int pcmLen,byte * raw,int *pLen_raw, mybool bDbg) {
    int iErr = -1;
    QDC_unit *pUnit = getQdcUnitByIndex(index_qdcUnit);
    if (!pUnit) return -1;

    int i, j, k, ret;
    uint16_t *samples;
    char * pTmp=NULL;

    //
    int nGot=0;

    //
    switch  (  pUnit->ucAudioCompressors  )  {
            case  CONST_audioCompressors_melp:
                  return encA_data_melp(index_qdcUnit,pcm,pcmLen,raw,pLen_raw,bDbg);
                  break;
        default:
                break;
    }


    //
    if(bDbg)
    {
        LOGD("encA_data: mp3, enters lllllllllllllll");
    }

    //
    ret = av_frame_make_writable(pUnit->encA_var.frame);
    //
    if (ret < 0) {
        //exit(1);
        //goto errLabel;
        return -1;
    }
    samples = (uint16_t*)pUnit->encA_var.frame->data[0];

    //


    //
    int size_needed=pUnit->encA_var.cache.len+pcmLen;
    if(size_needed>pUnit->encA_var.cache.iBufSize||!pUnit->encA_var.cache.pBuf){
        if(pUnit->encA_var.cache.len){
            pTmp=(char*)mymalloc(pUnit->encA_var.cache.len);
            if ( !pTmp)goto errLabel;
            //
            LOGE("encA_data: malloc for pTmp");
            //
            memcpy(pTmp,pUnit->encA_var.cache.pBuf,pUnit->encA_var.cache.len);
        }
        if(pUnit->encA_var.cache.pBuf) {
            myfree(pUnit->encA_var.cache.pBuf);
            pUnit->encA_var.cache.pBuf=NULL;
        }
#define CONST_maxEncACacheBufSize 3000000
        if(size_needed>CONST_maxEncACacheBufSize) {
            LOGE("encA_data failed: too big size_needed %d",size_needed);
            goto errLabel;
        }
        pUnit->encA_var.cache.pBuf=(char*)mymalloc(size_needed);
        if(!pUnit->encA_var.cache.pBuf) {
            LOGE("encA_data failed, malloc failed");
            goto errLabel;
        }
        pUnit->encA_var.cache.iBufSize=size_needed;
        //
        LOGE("encA_data: malloc for cached");
        //
        if(pTmp) {
            memcpy(pUnit->encA_var.cache.pBuf,pTmp,pUnit->encA_var.cache.len);
        }
    }
    //
    memcpy(pUnit->encA_var.cache.pBuf+pUnit->encA_var.cache.len, pcm,pcmLen);
    pUnit->encA_var.cache.len+=pcmLen;


    /*
    for (j = 0; j < c->frame_size; j++) {
        samples[2*j] = (int)(sin(t) * 10000);

        for (k = 1; k < c->channels; k++)
            samples[2*j + k] = samples[2*j];
        t += tincr;
    }
     */

    for ( ;;) {
        //
        int i_bufsize = 0;
        int i_linesize = 0;
        int i_nb_channels = pUnit->encA_var.c->channels;
        int i_nb_smaples = pUnit->encA_var.frame->nb_samples;
        enum AVSampleFormat i_sample_fmt = (enum AVSampleFormat) pUnit->encA_var.frame->format;

        i_bufsize = av_samples_get_buffer_size(&i_linesize, i_nb_channels,
                                               i_nb_smaples, i_sample_fmt, 1);

        if(i_bufsize>pUnit->encA_var.cache.len) {
            break;
        }
        if(!i_bufsize) {
            LOGE("encA_data failed, i_bufSize is 0");
            goto errLabel;
        }

        //
        //fread(samples, i_bufsize, 1, pUnit->encA_var.f_i);
        memcpy(samples,pUnit->encA_var.cache.pBuf,i_bufsize);
        pUnit->encA_var.cache.len-=i_bufsize;
        if(pUnit->encA_var.cache.len) {
            memcpy(pUnit->encA_var.cache.pBuf, pUnit->encA_var.cache.pBuf + i_bufsize,pUnit->encA_var.cache.len);
        }
        //
        byte * tmp_raw=NULL;
        int tmp_len_raw=0;
        if(raw && pLen_raw){
            tmp_len_raw=*pLen_raw-nGot;
            if(tmp_len_raw<0) {
                LOGE("encA_data failed, tmp_len_raw <0");
                goto errLabel;
            }
            tmp_raw=raw+nGot;
        }
        //
        if (myencodeA(pUnit->encA_var.c, pUnit->encA_var.frame, pUnit->encA_var.pkt,tmp_raw,&tmp_len_raw,bDbg)) {
            goto errLabel;
        }
        nGot+=tmp_len_raw;
        continue;
    }

    if(pLen_raw){
        *pLen_raw=nGot;
    }

    iErr = 0;

    errLabel:

    if(pTmp){
        myfree(pTmp);
    }

    if(bDbg){
        LOGD("encA_data leaves with %d",iErr);
    }

    return iErr;
}


