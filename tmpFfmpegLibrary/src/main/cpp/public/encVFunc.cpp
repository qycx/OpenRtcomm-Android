

#include "tmpFfmpegCommon.h"

int myencode(AVCodecContext *enc_ctx, AVFrame *frame, AVPacket *pkt,
        //FILE *outfile
             byte * raw,int *pLen_raw,bool bDbg
);

//
int encV_init(int index_qdcUnit, int iFourcc, int in_w,int in_h,mybool bDbg) {
    int iErr = -1;
    QDC_unit *pUnit = getQdcUnitByIndex(index_qdcUnit);
    if (!pUnit) return -1;

    AVCodecID codec_id = AV_CODEC_ID_H264;

    if (!g_qdc.bDone_qdc_init) return -1;

    //
    pUnit->encV_var.iFourcc = iFourcc;
    switch (iFourcc) {
        case CONST_fourcc_h264:
            break;
        default:
            return -1;
    }

    //const AVCodec *codec;
    //AVCodecContext *c= NULL;
    int i, ret, x, y;
    //FILE *f;
    //AVFrame *frame;
    //AVPacket *pkt;
    uint8_t endcode[] = { 0, 0, 1, 0xb7 };


    /* find the mpeg1video encoder */
    //codec = avcodec_find_encoder_by_name(codec_name);
    pUnit->encV_var.codec=avcodec_find_encoder(codec_id);
    if (!pUnit->encV_var.codec) {
        //fprintf(stderr, "Codec '%s' not found\n", codec_name);
        goto errLabel;
    }

    pUnit->encV_var.c = avcodec_alloc_context3(pUnit->encV_var.codec);
    if (!pUnit->encV_var.c) {
        fprintf(stderr, "Could not allocate video codec context\n");
        goto errLabel;
    }

    pUnit->encV_var.pkt = av_packet_alloc();
    if (!pUnit->encV_var.pkt) {
        goto errLabel;
    }

    /* put sample parameters */
    pUnit->encV_var.c->bit_rate = 400000;
    /* resolution must be a multiple of two */
    pUnit->encV_var.c->width = in_w;//352;
    pUnit->encV_var.c->height = in_h;//288;
    /* frames per second */
    pUnit->encV_var.c->time_base = (AVRational){1, 25};
    pUnit->encV_var.c->framerate = (AVRational){25, 1};

    /* emit one intra frame every ten frames
     * check frame pict_type before passing frame
     * to encoder, if frame->pict_type is AV_PICTURE_TYPE_I
     * then gop_size is ignored and the output of encoder
     * will always be I frame irrespective to gop_size
     */
    pUnit->encV_var.c->gop_size = 10;
    pUnit->encV_var.c->max_b_frames = 1;
    pUnit->encV_var.c->pix_fmt = AV_PIX_FMT_YUV420P;

    //
    pUnit->encV_var.c->flags|=AV_CODEC_FLAG_GLOBAL_HEADER;

    //
    if (codec_id == AV_CODEC_ID_H264) {
        int ret;
        ret = av_opt_set(pUnit->encV_var.c->priv_data, "preset", "fast", 0);
        //
        ret = av_opt_set(pUnit->encV_var.c->priv_data, "tune","zerolatency",0);
    }


    /* open it */
    ret = avcodec_open2(pUnit->encV_var.c, pUnit->encV_var.codec, NULL);
    if (ret < 0) {
        fprintf(stderr, "Could not open codec: %s\n", av_err2str(ret));
        goto errLabel;
    }


    pUnit->encV_var.frame = av_frame_alloc();
    if (!pUnit->encV_var.frame) {
        fprintf(stderr, "Could not allocate video frame\n");
        goto errLabel;
    }
    pUnit->encV_var.frame->format = pUnit->encV_var.c->pix_fmt;
    pUnit->encV_var.frame->width  = pUnit->encV_var.c->width;
    pUnit->encV_var.frame->height = pUnit->encV_var.c->height;

    ret = av_frame_get_buffer(pUnit->encV_var.frame, 0);
    if (ret < 0) {
        fprintf(stderr, "Could not allocate the video frame data\n");
        goto errLabel;
    }

    iErr = 0;

errLabel:
        if ( iErr !=0){
            encV_exit(index_qdcUnit,bDbg);
        }
        return iErr;
}


int encV_exit(int index_qdcUnit,mybool bDbg)
{
    int iErr = -1;
    QDC_unit *pUnit = getQdcUnitByIndex(index_qdcUnit);
    if (!pUnit) return -1;

    /*
    if ( myencode(pUnit->encV_var.c, NULL, pUnit->encV_var.pkt, NULL,NULL,bDbg) ) {
    }
     */


    /* add sequence end code to have a real MPEG file */
    /*
    if (codec->id == AV_CODEC_ID_MPEG1VIDEO || codec->id == AV_CODEC_ID_MPEG2VIDEO)
        fwrite(endcode, 1, sizeof(endcode), f);
    fclose(f);
     */

    avcodec_free_context(&pUnit->encV_var.c);
    av_frame_free(&pUnit->encV_var.frame);
    av_packet_free(&pUnit->encV_var.pkt);


    iErr = 0;
    errLabel:

    return iErr;
}


int encV_getSpsPps(int index_qdcUnit, byte * spsBuf, int * puiSpsSize, byte * ppsBuf, int * puiPpsSize, byte * vps, int * puiVpsSize,mybool bDbg)
{
    int iErr = -1;
    QDC_unit * pUnit=getQdcUnitByIndex(index_qdcUnit);
    if ( !pUnit) return -1;

    if ( pUnit->encV_var.c->extradata == NULL ) goto errLabel;


    if ( 0!=parseSpsPps(pUnit->encV_var.iFourcc,(char*)pUnit->encV_var.c->extradata,pUnit->encV_var.c->extradata_size,
                        (char*)spsBuf,(unsigned int *)puiSpsSize,(char*)ppsBuf,(unsigned int *)puiPpsSize,(char*)vps,(unsigned int *)puiVpsSize))
    {
        goto errLabel;
    }


    iErr = 0;
    errLabel:
    return iErr;
}


int myencodeV(AVCodecContext *enc_ctx, AVFrame *frame, AVPacket *pkt,
                   //FILE *outfile
                    byte * raw,int *pLen_raw,
                    bool bDbg
                   )
{
    int iErr = -1;
    int ret;
    int nGot=0;
    bool tmpbErr=false;

    //
    if ( bDbg){
        LOGD("myencodeV: enters");
    }

    /* send the frame to the encoder */
    if (frame)
        printf("Send frame %d\n", frame->pts);

    ret = avcodec_send_frame(enc_ctx, frame);
    if (ret < 0) {
        fprintf(stderr, "Error sending a frame for encoding\n");
        goto errLabel;
    }

    while (ret >= 0) {
        ret = avcodec_receive_packet(enc_ctx, pkt);
        if (ret == AVERROR(EAGAIN) || ret == AVERROR_EOF) {
            //return;
            break;
        }
        else if (ret < 0) {
            fprintf(stderr, "Error during encoding\n");
            goto errLabel;
            //exit(1);
        }

        printf("Write packet %d (size=%5d)\n", pkt->pts, pkt->size);
        //fwrite(pkt->data, 1, pkt->size, outfile);
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
        LOGD("myencodeV: leaves iErr %d", iErr);
    }

    return iErr;
}



int encV_data(int index_qdcUnit,byte * yuv,int yuvLen,byte * raw,int *pLen_raw, mybool bDbg)
{
    int iErr = -1;
    QDC_unit *pUnit = getQdcUnitByIndex(index_qdcUnit);
    if (!pUnit) return -1;

    int i, ret, x, y;

    /* make sure the frame data is writable */
    ret = av_frame_make_writable(pUnit->encV_var.frame);
    if (ret < 0) {
        goto errLabel;
    }

#if 0
    /* prepare a dummy image */
    /* Y */
    for (y = 0; y < c->height; y++) {
        for (x = 0; x < c->width; x++) {
            frame->data[0][y * frame->linesize[0] + x] = x + y + i * 3;
        }
    }

    /* Cb and Cr */
    for (y = 0; y < c->height/2; y++) {
        for (x = 0; x < c->width/2; x++) {
            frame->data[1][y * frame->linesize[1] + x] = 128 + y + i * 2;
            frame->data[2][y * frame->linesize[2] + x] = 64 + x + i * 5;
        }
    }
#endif
    //
    AVCodecContext *c;
    AVFrame *frame;
    c=pUnit->encV_var.c;
    frame=pUnit->encV_var.frame;

    int y_size,u_size,v_size;
    y_size = c->width * c->height;
    u_size=y_size/4;
    v_size=y_size/4;


    //memcpy(pUnit->var.frame->data[0],yuv,y_size);
    for ( y=0;y<c->height;y++){
        memcpy(frame->data[0]+y*frame->linesize[0], yuv+y*c->width,c->width);
    }
   // memcpy(pUnit->var.frame->data[1],yuv+y_size,y_size/4);
    //memcpy(pUnit->var.frame->data[2],yuv+y_size+y_size/4,y_size/4);
    for ( y=0;y<c->height/2;y++) {
        memcpy(frame->data[1]+y*frame->linesize[1],yuv+y_size+y*c->width/2,c->width/2);
        memcpy(frame->data[2]+y*frame->linesize[2],yuv+y_size+u_size+y*c->width/2,c->width/2);
    }

    //
    pUnit->encV_var.frame->pts = pUnit->encV_var.pts; pUnit->encV_var.pts ++;

    /* encode the image */
    if ( myencodeV(pUnit->encV_var.c, pUnit->encV_var.frame, pUnit->encV_var.pkt, raw,pLen_raw,bDbg) ) {
        goto errLabel;
    }

    iErr = 0;
    errLabel:
    return iErr;

}
