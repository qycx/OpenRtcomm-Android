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


int testEncA_main1(int index_qdcUnit, char * dir,char *fileName_out,int cntof_fileName_out) {
    const char *filename;
    int i, j, k, ret;
    FILE *f_i = NULL;
    FILE *f=NULL;
    uint16_t *samples;
    float t, tincr;
    mybool bDbg=true;
    char * pcmBuf=NULL;
    char * rawBuf=NULL;

    //
    std::string filename_in;
    filename_in = std::string(dir) + "/" + "test0x0.android_dump.pcm";
    f_i = fopen(filename_in.c_str(), "rb");
    if (f_i == NULL) return -1;

    //
    snprintf(fileName_out, cntof_fileName_out, "%s/%s", dir, "testAndroid.mp3");

    //
    filename = fileName_out;//argv[1];

    f = fopen(filename, "wb");
    if (!f) {
        fprintf(stderr, "Could not open %s\n", filename);
        exit(1);
    }

    if(ffmpeg_qdc_init()) goto errLabel;

    //
    byte ucCompressors;  ucCompressors=0;

    if(encA_init(index_qdcUnit,ucCompressors,WAVE_FORMAT_MPEGLAYER3,32000,bDbg))goto errLabel;

    //
    QDC_unit * pUnit;
    pUnit=getQdcUnitByIndex(index_qdcUnit);
    pUnit->encA_var.f=f;

    //
    int pcmSize;
    pcmSize=1280;
    pcmBuf=(char*)mymalloc(pcmSize);
    if(!pcmBuf)goto errLabel;
    int rawSize;
    rawSize=500000;
    rawBuf=(char*)mymalloc(rawSize);
    if(!rawSize)goto errLabel;

    //
    for ( i=0;i<200;i++){
        fread(pcmBuf,pcmSize,1,f_i);
        int len_raw=rawSize;
        if(encA_data(index_qdcUnit,(byte*)pcmBuf,pcmSize,(byte*)rawBuf,&len_raw,bDbg))goto errLabel;
        int ii = 0;
        fwrite(rawBuf,len_raw,1,f);
        continue;
    }




errLabel:

    if(pcmBuf)myfree(pcmBuf);
    if(rawBuf)myfree(rawBuf);

    encA_exit(index_qdcUnit,bDbg);

    ffmpeg_qdc_exit1();

    if(f_i){
        fclose(f_i);
    }
    if(f){
        fclose(f);
    }
    return 0;
}
