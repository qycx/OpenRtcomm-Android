//
// Created by qycx on 2021/2/15.
//
#include "tmpFfmpegCommon.h"

#define INBUF_SIZE 8096

//
int testDec_main1(int index_qdcUnit, char * dir,char *fileName_out,int cntof_fileName_out)
{
    const char *filename, *outfilename;
    /*
    const AVCodec *codec;
    AVCodecParserContext *parser;
    AVCodecContext *c= NULL;
    AVFrame *frame;
    uint8_t *data;
    size_t   data_size;
    int ret;
    AVPacket *pkt;
     */
    uint8_t inbuf[INBUF_SIZE + AV_INPUT_BUFFER_PADDING_SIZE];
    FILE *f;

    /*
    if (argc <= 2) {
        fprintf(stderr, "Usage: %s <input file> <output file>\n", argv[0]);
        exit(0);
    }
    filename    = argv[1];
    outfilename = argv[2];
*/
    char * name_out="deckk_main1.yuv";
    snprintf(fileName_out,cntof_fileName_out,"%s/%s",dir,name_out);

    char filename_in[1024];
    char *name_in="kk.264";
    snprintf(filename_in,sizeof(filename_in),"%s/%s",dir,name_in);
    FILE * fo;
    int iFourcc=CONST_fourcc_h264;
    mybool bDbg=true;

    int iFourcc_dec;
    int iW,iH;
    byte *decBuf=NULL;
    int iDecBufSize=0;
    mybool bBufTooSmall=false;
    int nPktDeced=0;

    //
    filename=filename_in;
    outfilename=fileName_out;

    //
    QDC_unit *pUnit = getQdcUnitByIndex(index_qdcUnit);
    if (!pUnit) return -1;

    //

    /* set end of buffer to 0 (this ensures that no overreading happens for damaged MPEG streams) */
    memset(inbuf + INBUF_SIZE, 0, AV_INPUT_BUFFER_PADDING_SIZE);


    f = fopen(filename, "rb");
    if (!f) {
        fprintf(stderr, "Could not open %s\n", filename);
        exit(1);
    }
    fo=fopen(fileName_out,"w");
    if ( !fo){
        goto errLabel;
    }

    //
    if (ffmpeg_qdc_init()) goto errLabel;

    //
    if ( decV_init(index_qdcUnit,iFourcc,bDbg)) goto errLabel;

    //
    pUnit->decV_var.fo=fo;

    //
    while (!feof(f)) {
        /* read raw data from the input file */
        int data_size = fread(inbuf, 1, INBUF_SIZE, f);
        if (!data_size)
            break;

        /* use the parser to split the data into frames */
        byte * data = inbuf;
        /*
        while (data_size > 0) {
            ret = av_parser_parse2(parser, c, &pkt->data, &pkt->size,
                                   data, data_size, AV_NOPTS_VALUE, AV_NOPTS_VALUE, 0);
            if (ret < 0) {
                fprintf(stderr, "Error while parsing\n");
                exit(1);
            }
            data      += ret;
            data_size -= ret;

            if (pkt->size)
                decode(c, frame, pkt, outfilename,fo);
        }
         */
        iFourcc_dec=pUnit->decV_var.iFourcc_dec;
        iW=pUnit->decV_var.iW;iH=pUnit->decV_var.iH;
        bBufTooSmall=false;
        nPktDeced=0;
        //int dec_data(int index_qdcUnit, byte * data,int data_size, int iInputBufferPaddingSize, bool bDbg)
        if ( decV_data(index_qdcUnit,data,INBUF_SIZE,AV_INPUT_BUFFER_PADDING_SIZE, &nPktDeced, &iFourcc_dec,&iW,&iH, decBuf, &iDecBufSize, &bBufTooSmall, bDbg ) )goto errLabel;
        if (nPktDeced>0) {
            if ( iFourcc_dec!=pUnit->decV_var.iFourcc_dec)pUnit->decV_var.iFourcc_dec=iFourcc_dec;
            if ( iW!=pUnit->decV_var.iW)pUnit->decV_var.iW=iW;
            if ( iH!=pUnit->decV_var.iH)pUnit->decV_var.iH=iH;
            //
            if ( nPktDeced!=1){
                int ii  = 0;
            }
            //
            if (bBufTooSmall) {
                if (decBuf) {
                    myfree(decBuf);
                    decBuf = NULL;
                }
                iDecBufSize = pUnit->decV_var.iW * pUnit->decV_var.iH * 3 / 2;
                decBuf = (byte *) mymalloc(iDecBufSize);
            } else {
                fwrite(decBuf, iDecBufSize, 1, pUnit->decV_var.fo);
            }

            //
            if ( bBufTooSmall|| nPktDeced>1){
                int i;
                for ( i=0;i < pUnit->decBufs.maxCnt_decBufMems;i++){
                    DEC_buf  *pMem=&pUnit->decBufs.mems[i];
                    if ( !pMem->bUsed)break;
                    //
                    fwrite(pMem->pBuf,pMem->len,1,pUnit->decV_var.fo);
                    pMem->bUsed=false;
                }
            }
        }

        //
        continue;
    }

    /* flush the decoder */
    iFourcc_dec=pUnit->decV_var.iFourcc_dec;
    iW=pUnit->decV_var.iW;iH=pUnit->decV_var.iH;
    bBufTooSmall=false;
    nPktDeced=0;

    //decode(c, frame, NULL, outfilename,fo);
    decV_data(index_qdcUnit,NULL,0,AV_INPUT_BUFFER_PADDING_SIZE,&nPktDeced, &iFourcc_dec,&iW,&iH,decBuf,&iDecBufSize,&bBufTooSmall,bDbg);

    errLabel:


    //
    decV_exit(index_qdcUnit,bDbg);

    if ( decBuf) myfree(decBuf);
    fclose(f);
    fclose(fo);

    ffmpeg_qdc_exit1();

    return 0;
}
