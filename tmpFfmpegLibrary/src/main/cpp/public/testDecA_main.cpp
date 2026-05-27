//
// Created by qycx on 2021/3/3.
//

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <string>

#include "tmpFfmpegCommon.h"

#define AUDIO_INBUF_SIZE    15000

int testDecA_main1(int index_qdcUnit, char * dir,char *fileName_out,int cntof_fileName_out)
{
    char *filename;
    char *outfilename;
    FILE *f;
    FILE *outfile;
    uint8_t inbuf[AUDIO_INBUF_SIZE + AV_INPUT_BUFFER_PADDING_SIZE];

    std::string file_in = std::string(dir) + "/test0X0.dump_mp3_dec.mp3";
    std::string file_out = std::string(dir) + "/dec.pcm";

    if (10) {
        file_in = std::string(dir) + "/test0X0.dump_mp3_enc.mp3";
        file_out = std::string(dir) + "/enc.pcm";
    }

    //
    filename = (char *) file_in.c_str();
    outfilename = (char *) file_out.c_str();

    f = fopen(filename, "rb");
    if (!f) {
        fprintf(stderr, "Could not open %s\n", filename);
        exit(1);
    }
    outfile = fopen(outfilename, "wb");
    if (!outfile) {
        //av_free(pUnit->decA_var.c);
        exit(1);
    }

    QDC_unit *pUnit = getQdcUnitByIndex(index_qdcUnit);
    if (!pUnit) return -1;


    ffmpeg_qdc_init();

    //
    byte ucCompressors;  ucCompressors  =  0;

    //
    if (decA_init(index_qdcUnit, ucCompressors,  WAVE_FORMAT_MPEGLAYER3, true)) goto errLabel;

    //
    char decBuf[200000];
    pUnit->decA_var.outfile = outfile;

    for (;;) {
        int nRead;
        nRead = fread(inbuf, 1, AUDIO_INBUF_SIZE, f);
        if (nRead <= 0)break;
        //
        int nPktDeced = 0;
        int iFmt_dec = 0;
        int iDecBufSize = sizeof(decBuf);
        mybool bBufTooSmall = false;

        //
        //int encA_data(int index_qdcUnit,byte * pcm,int pcmLen,byte * raw,int *pLen_raw, bool bDbg);

        if (decA_data(index_qdcUnit, (byte *) inbuf, nRead, 0, &nPktDeced, &iFmt_dec,
                      (byte *) decBuf, &iDecBufSize, &bBufTooSmall, false)) {
            goto errLabel;
        }
        if (nPktDeced > 0) {
            //if ( iFourcc_dec!=pUnit->decV_var.iFourcc_dec)pUnit->decV_var.iFourcc_dec=iFourcc_dec;
            //
            if (nPktDeced != 1) {
                int ii = 0;
            }
            //
            if (bBufTooSmall) {
                goto errLabel;
            }
            {
                fwrite(decBuf, iDecBufSize, 1, pUnit->decA_var.outfile);
            }

            //
            if (bBufTooSmall || nPktDeced > 1) {
                int i;
                for (i = 0; i < pUnit->decBufs.maxCnt_decBufMems; i++) {
                    DEC_buf *pMem = &pUnit->decBufs.mems[i];
                    if (!pMem->bUsed)break;
                    //
                    fwrite(pMem->pBuf, pMem->len, 1, pUnit->decA_var.outfile);
                    pMem->bUsed = false;
                }
            }

        }
        }


        errLabel:

        decA_exit(index_qdcUnit, true);

        ffmpeg_qdc_exit1();

        if (f) {
            fclose(f);
        }
        if (outfile) {
            fclose(outfile);
        }

        return 0;
    }

