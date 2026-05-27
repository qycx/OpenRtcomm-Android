#include <jni.h>
#include <string>

extern "C" {
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libavfilter/avfilter.h"
#include "libswscale/swscale.h"
#include "libswresample/swresample.h"
#include "libavutil//opt.h"
#include "libavutil//imgutils.h"


}

#include "tmpFfmpegCommon.h"
#include    "myMelpPublic.h"


#if  0
extern "C" JNIEXPORT jstring JNICALL
Java_com_qycx_tmpFfmpeg_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
#endif


extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_hiFromJni(JNIEnv *env, jclass clazz, jintArray p_result) {
    // TODO: implement hiFromJni()

    //
    LOGD("ffmpeg.hiFromJni called");

    int  iVal  =  0;
    testMelp(&iVal);


    //
    return 888;
}


//
extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_getHello(JNIEnv *env, jclass clazz) {
    // TODO: implement getHello()

    //

    //
    return 789;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_urlProtocolInfo(JNIEnv *env, jclass clazz) {
    // TODO: implement urlProtocolInfo()
    //char info[40000]="ll";


    av_register_all();

    /*
    struct URLProtocol *pup=NULL;

    struct URLProtocol **p_temp=(struct URLProtocol **)&pup;
    avio_enum_protocols((void**)p_temp,0);

    while((*p_temp)!=NULL){
        sprintf(info,"%sInput: %s\n",info,avio_enum_protocols((void **)p_temp,0));
    }
    pup=NULL;
    avio_enum_protocols((void**)p_temp,1);
    while((*p_temp)!=NULL){
        sprintf(info,"%sInput: %s\n",info,avio_enum_protocols((void**)p_temp,1));
    }
     */
    std::string info;

    struct URLProtocol *pup = NULL;

    // input
    avio_enum_protocols((void**)&pup, 0);
    while (pup != NULL) {
        const char *t = avio_enum_protocols((void**)&pup, 0);
        info += std::string("[In ][") + std::string(t==NULL?"": t) + std::string("]\n");
    }
    pup = NULL;

    avio_enum_protocols((void**)&pup, 1);
    while (pup != NULL) {
        const char *t = avio_enum_protocols((void**)&pup, 1);
        info += std::string("[Out ][") + std::string(t==NULL?"": t) + std::string("]\n");
    }

    //
    int ii = 0;
    std::string hello="hhh";

    return env->NewStringUTF(info.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_avFormatInfo(JNIEnv *env, jclass clazz) {
    // TODO: implement avFormatInfo()
    std::string info;

    av_register_all();

    AVInputFormat *if_temp = av_iformat_next(NULL);
    AVOutputFormat *of_temp = av_oformat_next(NULL);
    while (if_temp != NULL) {
        info += std::string ("Input: ") + if_temp->name + "\n";
        if_temp = if_temp->next;
    }
    while (of_temp != NULL) {
        //sprintf(info, "%sOutput: %s\n", info, of_temp->name);
        info +=std::string ("Output: ")+of_temp->name + "\n";
        of_temp = of_temp->next;
    }

    return env->NewStringUTF(info.c_str());

}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_avCodecInfo(JNIEnv *env, jclass clazz) {
    // TODO: implement avCodecInfo()
    std::string info;


    av_register_all();

    AVCodec *c_temp = av_codec_next(NULL);

    while (c_temp != NULL) {
        if (c_temp->decode != NULL) {
            info += std::string ("decode:");
        } else {
            info+=std::string ("encode:");
        }
        switch (c_temp->type) {
            case AVMEDIA_TYPE_VIDEO:
                info+="(video):";
                break;
            case AVMEDIA_TYPE_AUDIO:
                info+="(audio):";
                break;
            case AVMEDIA_TYPE_SUBTITLE:
                info+="(subtitle):";
                break;
            default:
                info+="(other):";
                break;
        }
        info+=std::string(c_temp->name)+"\n";
        c_temp = c_temp->next;
    }

    return env->NewStringUTF(info.c_str());

}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_avFilterInfo(JNIEnv *env, jclass clazz) {
    // TODO: implement avFilterInfo()

    /*
    std::string info;
    avfilter_register_all();

    AVFilter *f_temp = (AVFilter *) avfilter_next(NULL);
    while (f_temp != NULL) {
        info+= std::string(f_temp->name) + "\n";
        f_temp = f_temp->next;
    }
   // ALOGI("%s", info);
    return env->NewStringUTF(info.c_str());
     */
    return NULL;

}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_doTest(JNIEnv *env, jclass clazz, jint index_qdc_unit,
                                                jstring str) {
    // TODO: implement doTest()
    char oBuf[1024]="";

    const char * i_str=env->GetStringUTFChars(str,NULL);

    //std::string i_str=std::string(str);
    //
    doTest(index_qdc_unit,(char *)i_str, oBuf,sizeof(oBuf));
    oBuf[sizeof(oBuf)-1]=0;

    env->ReleaseStringUTFChars(str,i_str);

    return env->NewStringUTF(oBuf);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_qdc_1init(JNIEnv *env, jclass clazz) {
    // TODO: implement qdc_init()
    return ffmpeg_qdc_init();
}

//
int  ffmpeg_doSth();


extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_qdc_1exit(JNIEnv *env, jclass clazz) {
    // TODO: implement qdc_exit()

    //ffmpeg_qdc_exit();

    //
    ffmpeg_qdc_exit1();

    int  ii  =  0;
    return  0;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_newQdcUnitIndex(JNIEnv *env, jclass clazz,jboolean bDbg) {
    // TODO: implement newQdcUintIndex()
    return newQdcUnitIndex((mybool)bDbg);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_freeQdcUnitIndex(JNIEnv *env, jclass clazz, jint index,jboolean bDbg) {
    // TODO: implement freeQdcUnitIndex()
    freeQdcUnitIndex(index,bDbg);
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_encV_1init(JNIEnv *env, jclass clazz, jint index_qdc_unit,
                                                   jint iFourcc, jint in_w, jint in_h,jboolean bDbg) {
    // TODO: implement enc_init()
    int iRet=encV_init(index_qdc_unit,iFourcc, in_w,in_h,(mybool)bDbg);
    return iRet;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_encV_1exit(JNIEnv *env, jclass clazz, jint index_qdc_unit,jboolean bDbg) {
    // TODO: implement enc_exit()
    int iRet=encV_exit(index_qdc_unit,bDbg);
    return iRet;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_encV_1getSpsPps(JNIEnv *env, jclass clazz,
                                                        jint index_qdc_unit, jobject sps_pps,
                                                        jintArray p_sps_pps_len,jboolean bDbg) {
    // TODO: implement enc_getSpsPps()
    int iErr = -1;
    char * pSpsPps=NULL;
    int *sps_pps_len_array=NULL;
    int n;

    pSpsPps=(char*)env->GetDirectBufferAddress(sps_pps);
    if ( pSpsPps==NULL)goto errLabel;
    int bufSize;
    bufSize=env->GetDirectBufferCapacity(sps_pps);

    sps_pps_len_array=env->GetIntArrayElements(p_sps_pps_len,NULL);
    if ( sps_pps_len_array==NULL)goto errLabel;
    n=env->GetArrayLength(p_sps_pps_len);
    if ( n<3)goto errLabel;
    int i;
    for ( i = 0; i < 3; i++){
        sps_pps_len_array[i]=0;
    }

    //
    byte sps[256];
    byte pps[256];
    byte vps[256];
    int spsLen;
    int ppsLen;
    int vpsLen;
    spsLen=sizeof(sps);
    ppsLen=sizeof(pps);
    vpsLen=sizeof(vps);

    //
    if ( encV_getSpsPps(index_qdc_unit,sps,&spsLen,pps,&ppsLen,vps,&vpsLen,bDbg) ) goto errLabel;

    //
    if ( spsLen+ppsLen+vpsLen>bufSize)goto errLabel;
    QDC_unit  *pUnit;
    pUnit=getQdcUnitByIndex(index_qdc_unit);
    if ( !pUnit)goto errLabel;

    int k;
    char * p;

    k=0;
    p=pSpsPps;
    switch (pUnit->encV_var.iFourcc) {
        case CONST_fourcc_h264:
             sps_pps_len_array[k]=spsLen;
             memcpy(p,sps,spsLen);
             k++; p+=spsLen;
            sps_pps_len_array[k]=ppsLen;
            memcpy(p,pps,ppsLen);
            k++;

            break;
        default:
                goto errLabel;
    }

    iErr = 0;
    errLabel:
    if(sps_pps_len_array)env->ReleaseIntArrayElements(p_sps_pps_len,sps_pps_len_array,0);
    return iErr;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_encV_1data(JNIEnv *env, jclass clazz, jint index_qdc_unit,
                                                   jobject yuv, jint yuv_len, jobject raw,jint rawSize,
                                                   jboolean b_dbg) {
    // TODO: implement enc_data()
    int iErr = -1;
    char * pYuv=NULL;
    char * pRaw=NULL;
    int rawLen;

    pYuv=(char *)env->GetDirectBufferAddress(yuv);
    if ( pYuv==NULL) goto errLabel;
    pRaw=(char*)env->GetDirectBufferAddress(raw);
    if ( pRaw==NULL)goto errLabel;

    //
    if ( b_dbg) {
        //LOGD("bufSize_raw is %d", rawSize);
    }

    //
    byte bHeadLen;
    int  len_raw_array[3];
    int n;

    bHeadLen=*pRaw;
    if ( rawSize<bHeadLen) goto errLabel;
    memcpy(&len_raw_array,pRaw+1,12);

    rawLen=len_raw_array[0];
    if (rawSize <rawLen+bHeadLen) goto errLabel;

    //
    if ( b_dbg) {
        LOGD("ffmpeg.enc_data: rawSize %d,rawLen %d,bHeadLen %d", rawSize, rawLen, bHeadLen);
    }
    //
    if ( encV_data(index_qdc_unit,(byte*)pYuv,yuv_len,(byte*)pRaw+bHeadLen,&rawLen,b_dbg) ) goto errLabel;
    len_raw_array[0]=rawLen;
    //
    if ( n>1){
        len_raw_array[1]=0;
    }
    memcpy(pRaw+1,len_raw_array,12);

    //
    iErr = 0;
    errLabel:


    if(b_dbg){
        LOGD("ffmpeg.enc_data leaves, iErr %d",iErr);
    }

    return iErr;

}

extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_decV_1init(JNIEnv *env, jclass clazz, jint index_qdc_unit,
                                                   jint i_fourcc, jboolean b_dbg) {
    // TODO: implement dec_init()
    int iErr = -1;

    if(b_dbg){
        LOGD("jni: dec_init enters");
    }

    if ( decV_init(index_qdc_unit,i_fourcc,b_dbg) ) goto errLabel;

    //
    iErr = 0;
    errLabel:

    if ( b_dbg){
        LOGD("jni: dec_init leaves");
    }

    return iErr;
}




extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_decV_1exit(JNIEnv *env, jclass clazz, jint index_qdc_unit,
                                                   jboolean b_dbg) {
    // TODO: implement dec_exit()
    decV_exit(index_qdc_unit,b_dbg);
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
 Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_decV_1data(JNIEnv *env, jclass clazz, jint index_qdc_unit,
                                                   jobject data, jint data_size,
                                                   jint i_input_buffer_padding_size, jobject dec,
                                                   jint dec_size, jboolean b_dbg)
                                                   {
    // TODO: implement dec_data()
    int iErr=-1;
    char * pEnc=NULL;
    char * pDec=NULL;

    pEnc=(char *)env->GetDirectBufferAddress(data);
    if ( pEnc==NULL)goto errLabel;
    pDec=(char*)env->GetDirectBufferAddress(dec);
    if(pDec==NULL)goto errLabel;

    //
    byte bHeadLen;
    int  head_array[7];


    bHeadLen=*pDec;
    if(dec_size<bHeadLen) goto errLabel;

    //
    //int dec_data(int index_qdcUnit, byte * data,int data_size, int iInputBufferPaddingSize, int *pnPktDeced, int * piFourcc_dec, int *piW,int *piH, byte * decBuf, int *piDecBufSize, bool *pbBufTooSmall, bool bDbg);
    // int iInputBufferPaddingSize, int *pnPktDeced, int * piFourcc_dec, int *piW,int *piH, int *piDecBufSize, bool *pbBufTooSmall);
    {
        int nPktDeced = 0;
        int iFourcc_dec = 0;
        int iW, iH;
        iW = 0;
        iH = 0;
        int iDecBufSize = dec_size - bHeadLen;
        mybool bBufTooSmall = false;

        if (decV_data(index_qdc_unit, (byte *) pEnc, data_size, i_input_buffer_padding_size,
                     &nPktDeced, &iFourcc_dec, &iW, &iH, (byte *) pDec + bHeadLen, &iDecBufSize,
                     &bBufTooSmall, b_dbg)) {
            goto errLabel;
        }
        int k;
        k = 0;
        head_array[k] = AV_INPUT_BUFFER_PADDING_SIZE;
        k++;
        head_array[k] = nPktDeced;
        k++;
        head_array[k] = iFourcc_dec;
        k++;
        head_array[k] = iW;
        k++;
        head_array[k] = iH;
        k++;
        head_array[k] = iDecBufSize;
        k++;
        head_array[k] = bBufTooSmall;
        k++;
        //
        if(b_dbg) {
            LOGD("dec_data: k is %d",k);
        }
        //
        memcpy(pDec+1,head_array,4*k);
    }

    iErr=0;
    errLabel:
    return iErr;
}



extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_dec_1getOutputByIndex(JNIEnv *env, jclass clazz,
                                                               jint index_qdc_unit, jint index,
                                                               jobject dec, jint dec_size, jboolean b_dbg) {
    // TODO: implement dec_getOutputByIndex()
    int iErr = -1;
    char * pDec=NULL;

    pDec=(char*)env->GetDirectBufferAddress(dec);
    if(pDec==NULL)goto errLabel;

    //
    byte bHeadLen;
    int  head_array[7];


    bHeadLen=*pDec;
    if(dec_size<bHeadLen) goto errLabel;

    uint iDecDataSize;
    iDecDataSize=dec_size-bHeadLen;
    if(dec_getOutputByIndex(index_qdc_unit,index,(byte*)pDec+bHeadLen,&iDecDataSize,(mybool)b_dbg))goto errLabel;

    int k;
    k=0;
    head_array[k]=iDecDataSize;
    k++;
    memcpy(pDec+1,head_array,4*k);

    iErr = 0;
errLabel:
    return iErr;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_dec_1clearOutputs(JNIEnv *env, jclass clazz,
                                                           jint index_qdc_unit, jboolean b_dbg) {
    // TODO: implement dec_clearOutputs()

    dec_clearOutputs(index_qdc_unit,(mybool)b_dbg);
    return 0;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_encA_1init(JNIEnv *env, jclass clazz, jint index_qdc_unit,
                                                    jbyte ucCompressors,
                                                    jshort w_format_tag, jint bitrate, jboolean b_dbg) {
    // TODO: implement encA_init()
    //
    int  tmpiVal  =  ucCompressors;


    //
    return encA_init(index_qdc_unit,ucCompressors, w_format_tag,bitrate,b_dbg);
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_encA_1exit(JNIEnv *env, jclass clazz, jint index_qdc_unit,
                                                    jboolean b_dbg) {
    // TODO: implement encA_exit()
    encA_exit(index_qdc_unit,b_dbg);
    return 0;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_encA_1data(JNIEnv *env, jclass clazz, jint index_qdc_unit,
                                                    jobject pcm, jint pcm_len, jobject raw,
                                                    jint rawSize, jboolean b_dbg) {
    // TODO: implement encA_data()
    int iErr = -1;
    char * pPcm=NULL;
    char * pRaw=NULL;
    int rawLen;



    //
    pPcm=(char *)env->GetDirectBufferAddress(pcm);
    if ( pPcm==NULL) goto errLabel;
    pRaw=(char*)env->GetDirectBufferAddress(raw);
    if ( pRaw==NULL)goto errLabel;

    //
    if ( b_dbg) {
        LOGD("jni.encA_data: bufSize_raw is %d", rawSize);
    }


    //
    byte bHeadLen;
    int  len_raw_array[3];
    int n;

    bHeadLen=*pRaw;
    if ( rawSize<bHeadLen) goto errLabel;
    memcpy(&len_raw_array,pRaw+1,12);

    rawLen=len_raw_array[0];
    if (rawSize <rawLen+bHeadLen) goto errLabel;

    //
    if ( b_dbg) {
        LOGD("jni.encA_data: rawSize %d,rawLen %d,bHeadLen %d", rawSize, rawLen, bHeadLen);
    }

    //
    if ( encA_data(index_qdc_unit,(byte*)pPcm,pcm_len,(byte*)pRaw+bHeadLen,&rawLen,(mybool)b_dbg) ) {
        //
        LOGD("encA_data failed. l612");
        //
        goto errLabel;
    }
    len_raw_array[0]=rawLen;
    //
    if ( n>1){
        len_raw_array[1]=0;
    }
    memcpy(pRaw+1,len_raw_array,12);

    //
    iErr = 0;
    errLabel:


    if(b_dbg){
        LOGD("jni.encA_data leaves, iErr %d",iErr);
    }

    return iErr;


}

/*
int decA_init(int index_qdcUnit, short wFormatTag, bool bDbg);
int decA_exit(int index_qdcUnit,bool bDbg);
*/

extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_decA_1init(JNIEnv *env, jclass clazz, jint index_qdc_unit,
                                                    jbyte ucCompressors,
                                                    jshort w_format_tag, jboolean b_dbg) {
    // TODO: implement decA_init()
    if(decA_init(index_qdc_unit,ucCompressors,w_format_tag,b_dbg)) return -1;
    return 0;
}





extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_decA_1exit(JNIEnv *env, jclass clazz, jint index_qdc_unit,
                                                    jboolean b_dbg) {
    // TODO: implement decA_exit()
    decA_exit(index_qdc_unit,b_dbg);
    return 0;
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_decA_1data(JNIEnv *env, jclass clazz, jint index_qdc_unit,
                                                    jobject enc_data, jint enc_data_size,
                                                    jint i_input_buffer_padding_size,
                                                    jobject dec_buf, jint dec_size,
                                                    jboolean b_dbg) {
    // TODO: implement decA_data()

        // TODO: implement dec_data()
        int iErr=-1;
        char * pEnc=NULL;
        char * pDec=NULL;

        pEnc=(char *)env->GetDirectBufferAddress(enc_data);
        if ( pEnc==NULL)goto errLabel;
        pDec=(char*)env->GetDirectBufferAddress(dec_buf);
        if(pDec==NULL)goto errLabel;

        //
        byte bHeadLen;
        int  head_array[7];


        bHeadLen=*pDec;
        if(dec_size<bHeadLen) goto errLabel;

        //
        //int decA_data(int index_qdcUnit, byte * encData,int encData_size, int iInputBufferPaddingSize, int *pnPktDeced, int * piFmt_dec, byte * decBuf, int *piDecBufSize, bool *pbBufTooSmall, bool bDbg);
        {
            int nPktDeced = 0;
            int iFmt_dec = 0;
            int iDecBufSize = dec_size - bHeadLen;
            mybool bBufTooSmall = false;

            if (decA_data(index_qdc_unit, (byte *) pEnc, enc_data_size, i_input_buffer_padding_size,
                          &nPktDeced, &iFmt_dec, (byte *) pDec + bHeadLen, &iDecBufSize,
                          &bBufTooSmall, b_dbg)) {
                goto errLabel;
            }
            int k;
            k = 0;
            head_array[k] = AV_INPUT_BUFFER_PADDING_SIZE;
            k++;
            head_array[k] = nPktDeced;
            k++;
            head_array[k] = iFmt_dec;
            k++;
            head_array[k] = iDecBufSize;
            k++;
            head_array[k] = bBufTooSmall;
            k++;
            //
            if(b_dbg) {
                LOGD("dec_data: k is %d",k);
            }
            //
            memcpy(pDec+1,head_array,4*k);
        }

        iErr=0;
        errLabel:
        return iErr;
}



extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpffmpeglibrary_TmpFfmpeg_melp_1encA_1getOutputByIndex(JNIEnv *env, jclass clazz,
                                                                      jint index_qdc_unit,
                                                                      jint index, jbyteArray aBuf,
                                                                      jintArray pi_buf_len,
                                                                      jintArray pi_pts,
                                                                      jintArray pi_sample_type_in_ms,
                                                                      jboolean b_dbg)
                                                                      {
    // TODO: implement melp_encA_getOutputByIndex()
    int  iErr  =  -1;

    if (aBuf == mynull) {
        LOGE("jni.melp_encA_getOutputByIndex failed, buf is null");
        return -1;
    }

    //
    byte  tmpBuf[100000];
    int  iBufLen;
    iBufLen=sizeof(tmpBuf);
    int iPts  =  0;
    int iSampleTimeInMs  =  0;

    //
    if  (melp_encA_getOutputByIndex(index_qdc_unit,index,tmpBuf,&iBufLen,&iPts,&iSampleTimeInMs,b_dbg)) {
        return  -1;
    }

    int len;

    len = env->GetArrayLength(aBuf);
    if (len < sizeof(tmpBuf)) {

        LOGE("jni.melp_get failed: l769, len of data < 4000");
        return -1;
    }


    jbyte* buffer = env->GetByteArrayElements(aBuf, mynull);
    if (buffer) {
        //
        //tmpiRet  =  qGetMsg(&pQMem->q,buffer, (unsigned int *)&len);
        memcpy(buffer,tmpBuf,iBufLen);

        // 必须释放
        env->ReleaseByteArrayElements(aBuf, buffer, 0);
    }


    int  tmpiRet;

    tmpiRet  =  -1;

    jint  * buf_len  =  env->GetIntArrayElements(pi_buf_len,mynull);
    if  (  buf_len  ) {
        //
        buf_len[0] = iBufLen;//len;
        tmpiRet = 0;
        //
        env->ReleaseIntArrayElements(pi_buf_len, buf_len, 0);
    }

    if  (  tmpiRet  !=  0) {
        LOGE("jni.melp_get: set len failed");
        return -1;

    }

    tmpiRet=-1;
    jint * buf_pts = env->GetIntArrayElements(pi_pts,mynull);
    if(buf_pts) {
        buf_pts[0]=iPts;
        tmpiRet=0;
        //
        env->ReleaseIntArrayElements(pi_pts,buf_pts,0);
    }

    if  (  tmpiRet  !=  0) {
        LOGE("jni.melp_get: set pts failed");
        return -1;
    }






    iErr  =  0;
                                                                          iErr  =  0;
                                                                          //
    return  iErr;
}