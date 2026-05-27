//
// Created by qycx on 2021/2/6.
//
#include "tmpFfmpegCommon.h"



int testEncV_main1(int index_qdcUnit,char * dir,char * fileName_o,int cntof_fileName_o)
{
    int i, ret, got_output;
    FILE *fp_in;
    FILE *fp_out;
    int y_size;
    int framecnt=0;
    bool bDbg=true;

    int iFourcc = CONST_fourcc_h264;

    //char filename_in[]="../ds_480x272.yuv";
    char name_in[]="ds_480x272.yuv";
    char filename_in[1024]="";
    snprintf(filename_in,sizeof(filename_in),"%s/%s",dir,name_in);

#if TEST_HEVC
    AVCodecID codec_id=AV_CODEC_ID_HEVC;
	char filename_out[]="ds.hevc";
#else
    AVCodecID codec_id=AV_CODEC_ID_H264;
    char name_out[]="newds.h264";
    //char filename_out[]="ds.h264";
#endif
    char filename_out[1024];
    snprintf(filename_out,sizeof(filename_out),"%s/%s",dir,name_out);


    //
    int in_w=480,in_h=272;
    int framenum=100;

    //
    //Input raw data
    fp_in = fopen(filename_in, "rb");
    if (!fp_in) {
        printf("Could not open %s\n", filename_in);
        return -1;
    }
    //Output bitstream
    fp_out = fopen(filename_out, "wb");
    if (!fp_out) {
        printf("Could not open %s\n", filename_out);
        return -1;
    }



    /*
    y_size = pCodecCtx->width * pCodecCtx->height;
    //Encode
    for (i = 0; i < framenum; i++) {
        av_init_packet(&pkt);
        pkt.data = NULL;    // packet data will be allocated by the encoder
        pkt.size = 0;
        //Read raw YUV data
        if (fread(pFrame->data[0],1,y_size,fp_in)<= 0||		// Y
            fread(pFrame->data[1],1,y_size/4,fp_in)<= 0||	// U
            fread(pFrame->data[2],1,y_size/4,fp_in)<= 0){	// V
            return -1;
        }else if(feof(fp_in)){
            break;
        }

        pFrame->pts = i;
        // encode the image
        ret = avcodec_encode_video2(pCodecCtx, &pkt, pFrame, &got_output);
        if (ret < 0) {
            printf("Error encoding frame\n");
            return -1;
        }
        if (got_output) {
            printf("Succeed to encode frame: %5d\tsize:%5d\n",framecnt,pkt.size);
            framecnt++;
            fwrite(pkt.data, 1, pkt.size, fp_out);
            av_free_packet(&pkt);
        }
    }
    //Flush Encoder
    for (got_output = 1; got_output; i++) {
        ret = avcodec_encode_video2(pCodecCtx, &pkt, NULL, &got_output);
        if (ret < 0) {
            printf("Error encoding frame\n");
            return -1;
        }
        if (got_output) {
            printf("Flush Encoder: Succeed to encode 1 frame!\tsize:%5d\n",pkt.size);
            fwrite(pkt.data, 1, pkt.size, fp_out);
            av_free_packet(&pkt);
        }
    }
    */

    int w=in_w;
    int h=in_h;
    int yuvLen=w*h * 3/2;
    byte * yuv=(byte *)mymalloc(yuvLen);
    byte * raw=(byte*)mymalloc(yuvLen);
    int rawLen=0;

    //
    if (ffmpeg_qdc_init()) goto errLabel;

    if(encV_init(index_qdcUnit, iFourcc, in_w,in_h,bDbg) ) goto errLabel;

    //
    {
        byte sps[512];
        byte pps[512];
        byte vps[512];
        int spsLen = sizeof(sps);
        int ppsLen = sizeof(pps);
        int vpsLen = sizeof(vps);

        //int enc_getSpsPps(int index_qdcUnit, byte * spsBuf, int * puiSpsSize, byte * ppsBuf, int * puiPpsSize, byte * vps, int * puiVpsSize)
        //if ( enc_getSpsPps(index_qdcUnit,sps,&spsLen,pps,&ppsLen,vps,&vpsLen) ) goto errLabel;

        int ii;
        ii=0;

    }

    //
    for ( i=0;i<100;i++){
        if ( fread(yuv,1,yuvLen,fp_in)<=0 ) goto errLabel;
        //enc_data()
        int rawLen=yuvLen;
        if(encV_data( index_qdcUnit,yuv,yuvLen,raw,&rawLen,NULL)){
            goto errLabel;
        }
        if ( rawLen){
            fwrite(raw,1,rawLen,fp_out);
        }
    }


    errLabel:

    encV_exit(index_qdcUnit,bDbg);

    if ( yuv!=NULL)myfree(yuv);
    if ( raw!=NULL)myfree(raw);
    fclose(fp_out);
    fclose(fp_in);

    //
    strncpy(fileName_o,filename_out,cntof_fileName_o);

    return 0;
}
