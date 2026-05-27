#include <jni.h>
#include <string>
#include "tmpJni.h"
#include "tmpQFunc.h"
#include "tmpQMgr.h"
#include "tmpAecPublic.h"

//
//
extern "C" JNIEXPORT jstring JNICALL
Java_introduction_android_myapplication_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

//
extern "C" int  testMelp(int  *  p_i);


extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_hiFromJni(JNIEnv *env, jclass clazz, jintArray p_result) {
    // TODO: implement hiFromJni()

    int bb[9];
    hi(bb);

    //
    int  iVal  =  0;
    //testMelp(&iVal);



    //
    return 653;
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_sayHello(JNIEnv *env, jclass clazz, jstring inStr) {
    // TODO: implement sayHello()
    char buf[128]="initStr";

    const char * inCStr=env->GetStringUTFChars(inStr,NULL);
    //LOGI("-------------- %s", inCStr);
    sayHello((char*)inCStr,buf,sizeof(buf));
    //
    env->ReleaseStringUTFChars(inStr,inCStr);

    //
    return env->NewStringUTF(buf);
}


extern "C"
JNIEXPORT jdoubleArray JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_sumAvg(JNIEnv *env, jclass clazz, jintArray nums) {
    // TODO: implement sumAvg()
    jint * inCArray=env->GetIntArrayElements(nums,NULL);
    if ( inCArray==NULL)return NULL;
    jsize length=env->GetArrayLength(nums);

    jint sum=0;
    int i;
    for (i=0;i<length;i++) {
        sum+=inCArray[i];
    }
    jdouble avg=(jdouble)sum/length;
    env->ReleaseIntArrayElements(nums,inCArray,0);

    jdouble outArray[]={(jdouble )sum,avg};

    jdoubleArray outJArray=env->NewDoubleArray(2);
    if ( NULL==outJArray) return NULL;
    env->SetDoubleArrayRegion(outJArray,0,2,outArray);
    return outJArray;

}

extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_procByteArray(JNIEnv *env, jclass clazz, jbyteArray bb,
                                                      jint buf_size) {
    // TODO: implement procByteArray()
    jbyte * bufferIn;
    int len=env->GetArrayLength(bb);
    bufferIn=(jbyte*)env->GetPrimitiveArrayCritical(bb,NULL);

    //
    procByteArray((char*)bufferIn, buf_size);

    //


    env->ReleasePrimitiveArrayCritical(bb,bufferIn,0);

    return 0;

}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_allocNativeByteBuffer(JNIEnv *env, jclass clazz,
                                                              jint size) {
    // TODO: implement allocNativeByteBuffer()
    if ( size==0) return NULL;
    void * buffer=new char [size];
    if ( buffer==NULL) return NULL;

    //
    if(size > 10) {
        strncpy((char*)buffer,"hello",size);
    }

    //
    jobject directBuffer=env->NewDirectByteBuffer(buffer,size);



    return directBuffer;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_freeNativeByteBuffer(JNIEnv *env, jclass clazz,
                                                             jobject bb) {
    // TODO: implement freeNativeByteBuffer()
    if ( bb==NULL) return;


    void * buffer=env->GetDirectBufferAddress(bb);

    //
    if ( buffer!=NULL ) {
        free(buffer);
    }
    return;
}


//

extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_i420ToRgb(JNIEnv *env, jclass clazz, jobject yuv,
                                                  jobject rgb, jint rgb_bits, jboolean b_bgr,
                                                  jint width, jint height, jboolean b_flip_img) {
    // TODO: implement i420ToRgb()
    int iErr = -1;
    char * pRgb=NULL;
    jbyte * pYuv=NULL;
    int rgbSize;
    int yuvSize;

    if ( 0!=env->PushLocalFrame(2) ) {
        return -1;
    }

    //pYuv=env->GetByteArrayElements(yuv,NULL);
    yuvSize=env->GetDirectBufferCapacity(yuv);
    pYuv=(jbyte *)env->GetDirectBufferAddress(yuv);
    if ( pYuv==NULL) goto errLabel;

    rgbSize=env->GetDirectBufferCapacity(rgb);
    pRgb=(char*)env->GetDirectBufferAddress(rgb);
    if ( pRgb==NULL)goto errLabel;

    //
    i420ToRgb((unsigned char *)pYuv,(unsigned char *)pRgb,rgb_bits, b_bgr, (int)width,(int)height,(BOOL)b_flip_img);

    //
    //
    iErr = 0;
    errLabel:

    /*
    if ( pRgb!=NULL) {
        env->ReleasePrimitiveArrayCritical(rgb, pRgb, 0);
        //env->DeleteLocalRef(rgb);
    }

    if(pYuv!=NULL) {
        //env->ReleaseByteArrayElements(yuv, pYuv, 0);
        env->ReleasePrimitiveArrayCritical(yuv,pYuv,0);
        env->DeleteLocalRef(yuv);
    }
     */

    env->PopLocalFrame(NULL);

    return iErr;



}







extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_rgbToI420(JNIEnv *env, jclass clazz, jobject rgb,
                                                  jint rgb_bits, jboolean b_bgr, jobject yuv,
                                                  jint width, jint height, jboolean b_flip_img) {
    // TODO: implement rgbToI420()
    int iErr = -1;
    jbyte * pRgb=NULL;
    char * pYuv=NULL;

    pRgb=(jbyte*)env->GetDirectBufferAddress(rgb);
    if ( pRgb==NULL)return -1;
    pYuv=(char *)env->GetDirectBufferAddress(yuv);
    if ( pYuv==NULL) goto errLabel;


    rgbToI420((unsigned char * )pRgb,rgb_bits,b_bgr, (unsigned char * )pYuv,width,height,b_flip_img);

    iErr = 0;
    errLabel:


    return iErr;

}

extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_rgb32To24(JNIEnv *env, jclass clazz, jobject rgb32,
                                                  jboolean b_bgr_32, jobject rgb, jboolean b_bgr_24,
                                                  jint w, jint h) {
    // TODO: implement rgb32To24()
    int iErr = -1;
    jbyte * pRgb32=NULL;
    jbyte *pRgb=NULL;

    pRgb32=(jbyte*)env->GetDirectBufferAddress(rgb32);
    if(pRgb32==NULL) goto errLabel;
    pRgb=(jbyte*)env->GetDirectBufferAddress(rgb);
    if(pRgb==NULL)goto errLabel;

    //
    rgb32To24((char*)pRgb32,b_bgr_32,(char*)pRgb,b_bgr_24,w,h);

    iErr =0;
    errLabel:


    return iErr;

}


extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_rotateI420(JNIEnv *env, jclass clazz, jobject src,
                                                   jobject dst, jint width_src, jint height_src,
                                                   jint degree,jboolean b_flip_img) {
    // TODO: implement rotateI420()
    int iErr = -1;
    jbyte * pSrc=NULL;
    jbyte * pDst=NULL;

    pSrc=(jbyte*)env->GetDirectBufferAddress(src);
    if ( pSrc==NULL) goto errLabel;
    pDst=(jbyte *)env->GetDirectBufferAddress(dst);
    if ( pDst==NULL) goto errLabel;

    //
    if ( 0!=rotateYUV420P((unsigned char*)pSrc,(unsigned char *)pDst,(int)width_src,(int)height_src,(int)degree,b_flip_img) ) {
        goto errLabel;
    }

    iErr = 0;
    errLabel:
    return iErr;
}





extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_newTmpQMemIndex(JNIEnv *env, jclass clazz,
                                                        jint player_index) {
    // TODO: implement newTmpQMemIndex()
    int iErr  =  -1;

    int  tmpiRet;
    tmpiRet  =  newTmpQMemIndex(player_index);
    if  (  tmpiRet  <  0  )  return  -1;

    return  tmpiRet;

}
extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_freeTmpQMemIndex(JNIEnv *env, jclass clazz, jint index) {
    // TODO: implement freeTmpQMemIndex()
    int  iErr  =  -1;

    freeTmpQMemIndex(index);

    return  0;

}

extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_getQNodes(JNIEnv *env, jclass clazz, jint index_player) {
    // TODO: implement getQNodes()
    int  iErr  =  -1;
    //
    TmpQMem   *  pQMem  = getTmpQMem(mynull,index_player);
    if  (  !pQMem)  {
        LOGE("jni.playAudioData failed, getTmpQMem returns null");
        return  -1;
    }

    return  pQMem->q.uiQNodes;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_playAudioData(JNIEnv *env, jclass clazz,
                                                       jint i_sample_time_in_ms, jint ui_pts,
                                                       jbyteArray p_input, jint input_len,
                                                       jint i_index_player) {
    // TODO: implement playAudioData1()
    int  iErr  =  -1;

    //
    CTmpQMgr  *  pMgr  =  &g_tmpQMgr;

    //
    TmpQMem   *  pQMem  = getTmpQMem(mynull,i_index_player);
    if  (  !pQMem)  {
        LOGE("jni.playAudioData failed, getTmpQMem returns null");
        return  -1;
    }


    if  (  p_input  ==  mynull  )  {
        LOGE("jni.playAudioData failed, p_input is null");
        return  -1;
    }
    if  (  input_len  <=  0)  {
        LOGE("jni.playAudioData failed, input_len <= 0");
        return  -1;
    }

    int len = env->GetArrayLength (p_input);
    if  (  len  <  input_len)  {
        LOGE("jni.playAudioData failed: len < input_len");
        return  -1;
    }

    //unsigned char* buf = mynull;
    //unsigned  char  buf[4096];
    TmpAPkt  pkt  =  {0};
    if  (  sizeof(pkt.buf)<input_len)    {
        LOGE("jni.playAudioData failed, sizeof(buf)<input_len");
        return  -1;
    }

    //
    int  iDiffInMs  =  i_sample_time_in_ms  -  pQMem->iSampleTimeInMs_last;
    pQMem->iSampleTimeInMs_last  =  i_sample_time_in_ms;
    if  (  iDiffInMs  >  10  *    36  )  {
        pQMem->iSampleTimeInMs_start  =  i_sample_time_in_ms;
    }
    ui_pts  =  (  i_sample_time_in_ms  -  pQMem->iSampleTimeInMs_start  )  /  36;
    //  这里编一个ui_pts，方便后面校验


    //
    do {

        env->GetByteArrayRegion(p_input, 0, input_len, reinterpret_cast<jbyte *>(pkt.buf));
        pkt.len  =  input_len;

        //
        pkt.iSampleTimeInMs  =  i_sample_time_in_ms;
        pkt.uiPts  =  ui_pts;

        //
        int  pktSize  = offsetof(TmpAPkt,buf)  +  pkt.len;


        //LOGD("playAudioData: [%s]", buf);
        int  tmpiRet;
        tmpiRet  =  qPostMsg(&pkt,  pktSize,  &pQMem->q,  _T("jni.playAudioData")  );
        if  (  tmpiRet  )  {
            LOGE("jni.playAudioData： qPostMsg failed, tmpiRet %d,  index_tmpQMem %d",  tmpiRet,  pQMem->index_tmpQMem);
            break;
        }

        //
#if  0
        if  (  qPostMsg(&pkt,  pktSize,  &pQMem->tmp_q,  _T("jni.playAudioData:tmp_q")  )  )  {
            LOGE("jni.playAudioData： tmp_q: qPostMsg failed");
        }
#endif

        if  (  pMgr->m_aecLayout.bDbg1) {
            //
            LOGD("jni.playAudioData: index_tmpQMem %d, q.nQNodes %d， pts %d, iSampleTimeInMs %d, iDiffInMs %dms",  i_index_player,  pQMem->q.uiQNodes,  ui_pts,  i_sample_time_in_ms,  iDiffInMs);
        }

        //
        iErr  =  0;
    }
    while (false);


    //
    return  0;
}

//public static native int getAudioData(int [] piSampleTimeInMs, int [] puiPts, byte[] data, int [] pLen, int [] pibMixFailed, int iIndex_player);

extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_getAudioData(JNIEnv *env, jclass clazz,
                                                   jintArray pi_sample_time_in_ms,
                                                   jintArray pui_pts, jbyteArray data,
                                                   jintArray p_len,
                                                   jintArray p_ibMixFailed,
                                                   jint i_index_player,
                                                   jstring hint) {
    // TODO: implement getAudioData()
    int iErr = -1;
    int len;
    //
    TmpQMem *pQMem = getTmpQMem(mynull, i_index_player);
    if (!pQMem) {
        LOGE("jni.playAudioData failed, getTmpQMem returns null");
        return -1;

    }

    if (pi_sample_time_in_ms == mynull) {
        LOGE("jni.getAudioData failed, pi_sampleTimeInMs is null");
        return -1;
    }
    if (pui_pts == mynull) {
        LOGE("jni.getAudioData failed, pui_pts is null");
        return -1;
    }
    if (data == mynull) {
        LOGE("jni.getAudioData failed, data is null");
        return -1;
    }
    if (p_len == mynull) {
        LOGE("jni.getAudioData failed, p_len is null");
        return -1;
    }
    if  (  p_ibMixFailed  ==  mynull )  {
        LOGE("jni.getAudioData failed, p_ibMixFailed is null");
        return  -1;
    }

    len = (env)->GetArrayLength(pi_sample_time_in_ms);
    if (len < 1) {
        LOGE("jni.getAudioData failed, len of pi_sampleTimeInMs < 1>");
        return -1;
    }
    len = env->GetArrayLength(pui_pts);
    if (len < 1) {
        LOGE("jni.getAudioData failed, len of pi_pts < 1>");
        return -1;
    }
    len = (env)->GetArrayLength(p_len);
    if (len < 1) {
        LOGE("jni.getAudioData failed, len of p_len < 1>");
        return -1;
    }
    len = (env)->GetArrayLength(p_ibMixFailed);
    if  (  len < 1) {
        LOGE("jni.getAudiodata failed, len of p_ibMixFailed < 1");
        return  -1;
    }
    len = env->GetArrayLength(data);
    if (len < 4000) {
        LOGE("jni.getAudioData failed: len of data < 4000");
        return -1;
    }

    //
    if (hint == nullptr) {
        LOGE("jni.getAudioData failed: hint isnull");
        return  -1;
    }

    //
    char  hint_buf[128];
    hint_buf[0]  =  0;

    //
    if  (  g_tmpQMgr.m_aecLayout.bDbg1  )  {
        // 1. 将 jstring 转换为 C 风格字符串 (const char*)
        // 注意: GetStringUTFChars 可能返回 nullptr，需要检查
        const char *cStr_hint = env->GetStringUTFChars(hint, nullptr);
        if (cStr_hint == nullptr) {
            LOGE("jni_getAudioData failed, for hint, 字符串转换失败");
            return -1;
        } else {

            // 2. 使用转换后的 C 字符串
            _snprintf(hint_buf, mycountof(hint_buf), "%s", cStr_hint);
            hint_buf[mycountof(hint_buf) - 1] = 0;

            // 3. 释放资源 (必须调用，避免内存泄漏)
            env->ReleaseStringUTFChars(hint, cStr_hint);
        }

        //
        //LOGE("jni.getAudioData: mmmmmmmmmmmmmmmmmm hint: [%s]", hint_buf);
    }

    //
    pQMem->nTimes_getAudioData  ++  ;

    //
    int  tmpiRet;
    jboolean isCopy;
    // 可能复制也可能不复制
    tmpiRet  =  -1;

    //
    CTmpQMgr * pMgr  =  &g_tmpQMgr;
    AEC_layout   *  pLayout =  &pMgr->m_aecLayout;
    int ib_mixFailed = false;

    //
#if  0
    if  (  pLayout->nTimes_mix_err  >  5  )  {
        ib_mixFailed  =  true;
    }
#endif

    //
    tmpiRet  =  -1;
    jint  * buf_ib_mixFailed  =  env->GetIntArrayElements(p_ibMixFailed,&isCopy);
    if  (  buf_ib_mixFailed  )  {
        //
        buf_ib_mixFailed[0]  =  ib_mixFailed;//len;
        tmpiRet  =  0;
        //
        env->ReleaseIntArrayElements(p_ibMixFailed,buf_ib_mixFailed,0);
    }
    if  (  tmpiRet  !=  0)  {
        LOGE("jni.getAudioData: set ibMixFailed failed");
        return  -1;
    }

    //
    TmpAPkt  pkt;
    len  =  sizeof(pkt);
    tmpiRet  = qGetMsg(&pQMem->q,&pkt,(unsigned  int*)&len);
    if  (  tmpiRet  !=  0)  {
        //
        LOGE("jni.getAudioData: qGetMsg faieled, nQNodes %d, index_tmpQMem %d. l560. %s",  pQMem->q.uiQNodes,  pQMem->index_tmpQMem, hint_buf);
        //
        return  -1;
    }

    //
    if  (  !(  pQMem->nTimes_getAudioData  %  500  )  ) {
        //
        //LOGD("jni.getAudioData: qGetMsg ok, index_tmpQMem %d: pts %d, len %d. nTimes_getAudioData %d",  pQMem->index_tmpQMem,  pkt.uiPts,  pkt.len,  pQMem->nTimes_getAudioData  );
    }
    else  {
          //
          if  (  pMgr->m_aecLayout.bDbg1  ) {
              //
              LOGD("jni.getAudioData: qGetMsg ok, index_tmpQMem %d: pts %d, len %d. nTimes_getAudioData %d. nQNodes %d. l574. %s",
                   pQMem->index_tmpQMem, pkt.uiPts, pkt.len, pQMem->nTimes_getAudioData,
                   pQMem->q.uiQNodes,
                   hint_buf
              );
          }
    }

    //
    if  (  i_index_player  ==  0  )  {
        CTmpQMgr  *  pMgr  =  &g_tmpQMgr;
        if  (isQFull(&pMgr->m_tmpRec.aecPlayQ)  )  {
            emptyGenericQ(&pMgr->m_tmpRec.aecPlayQ);
            //
            if  (  1  ) {
                //LOGD("jni.getAudioData: aecPlayQ is full, empty it");
            }
        }
        tmpiRet  =  qPostMsg(&pkt,len,&pMgr->m_tmpRec.aecPlayQ,_T(""));
        if  (  tmpiRet  )  {
            LOGE("jni.getAudioData err: qPostMsg to aecPlayQ aa failed");
        }
    }



    //
    jbyte* buffer = env->GetByteArrayElements(data, &isCopy);
    if (buffer) {
        //
        //tmpiRet  =  qGetMsg(&pQMem->q,buffer, (unsigned int *)&len);
        memcpy(buffer,pkt.buf,pkt.len);

        // 必须释放
        env->ReleaseByteArrayElements(data, buffer, 0);
    }

    tmpiRet  =  -1;
    jint  * buf_len  =  env->GetIntArrayElements(p_len,&isCopy);
    if  (  buf_len  )  {
        //
        buf_len[0]  =  pkt.len;//len;
        tmpiRet  =  0;
        //
        env->ReleaseIntArrayElements(p_len,buf_len,0);
    }
    if  (  tmpiRet  !=  0)  {
        LOGE("jni.getAudioData: set len failed");
        return  -1;
    }

    //
    iErr  =  0;


    //
    return  iErr;
}



extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_AEC_1layout_1init(JNIEnv *env, jclass clazz, jint i_task_id,
                                                          jshort us_cnt_limit_active_mems_from, jboolean ucb_USE_audioMixer) {
    // TODO: implement AEC_layout_init()
    //
    CTmpQMgr  *  pMgr  =  &g_tmpQMgr;

    int  iErr  =  -1;

    if  (  AEC_layout_init(i_task_id,us_cnt_limit_active_mems_from,ucb_USE_audioMixer,&pMgr->m_aecLayout))  {
        LOGE("aec_layout_init failed");
        return  -1;
    }

    iErr  =  0;

    return  iErr;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_AEC_1layout_1exit(JNIEnv *env, jclass clazz) {
    // TODO: implement AEC_layout_exit()
    CTmpQMgr  *  pMgr  =  &g_tmpQMgr;

    int  iErr  =  -1;

    AEC_layout_exit(&pMgr->m_aecLayout);

    iErr  =  0;

    return  iErr;
}



extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_postRecData(JNIEnv *env, jclass clazz,
                                                    jint n_avg_bytes_per_sec, jbyteArray pb_input1,
                                                    jint dw_num_bytes, jint ui_sample_time_in_ms) {
    // TODO: implement postRecData()
    int  iErr  =  -1;

    int  input_len  = dw_num_bytes;

    //
    CTmpQMgr * pMgr  =  &g_tmpQMgr;


    TmpAPkt  pkt  =  {0};
    if  (  sizeof(pkt.buf)<input_len)    {
        LOGE("jni.postRecData failed, sizeof(buf)<input_len");
        return  -1;
    }

    //
    do {

        env->GetByteArrayRegion(pb_input1, 0, input_len, reinterpret_cast<jbyte *>(pkt.buf));
        pkt.len = input_len;

        //
        pkt.iSampleTimeInMs = ui_sample_time_in_ms;
        //pkt.uiPts  =  ui_pts;

        //
        int pktSize = offsetof(TmpAPkt, buf) + pkt.len;


        //
        int  iDiffInMs  =  ui_sample_time_in_ms  -  pMgr->m_tmpRec.iSampleTimeInMs_last_toAecThreadQ;
        pMgr->m_tmpRec.iSampleTimeInMs_last_toAecThreadQ  =  ui_sample_time_in_ms;
        //
        int uiPts  =  (  ui_sample_time_in_ms  +  10  )  /  40;
        pkt.uiPts  =  uiPts;

        //
        //LOGD("postRecData: sampleTimeInMs %d, iDiffInms %d, len %d. pts %d", ui_sample_time_in_ms, iDiffInMs,  input_len,  uiPts);

        //
        int tmpiRet;

        //
        bool  bUse_audioMixer  =  true;

        //
        if  (  !bUse_audioMixer) {
            tmpiRet = qPostMsg(&pkt, pktSize, &pMgr->m_tmpRec.recQ, _T("jni.postRecData"));
            if (tmpiRet) {
                LOGE("jni.postRecData： qPostMsg failed, recQ, tmpiRet %d,  ", tmpiRet);
                break;
            }
        }
        else  {
            tmpiRet = qPostMsg(&pkt, pktSize, &pMgr->m_tmpRec.aecThreadQ, _T("jni.postRecData"));
            if (tmpiRet) {
                LOGE("jni.postRecData： qPostMsg failed, preAecQ, tmpiRet %d,  ", tmpiRet);
                break;
            }

            //
            if  (  0  )  {
                pkt.len  =  0;
                tmpiRet = qPostMsg(&pkt, pktSize, &pMgr->m_tmpRec.aecThreadQ, _T("jni.postRecData"));
                if (tmpiRet) {
                    LOGE("jni.postRecData： qPostMsg failed, preAecQ, tmpiRet %d,  ", tmpiRet);
                    break;
                }
                tmpiRet = qPostMsg(&pkt, pktSize, &pMgr->m_tmpRec.aecThreadQ, _T("jni.postRecData"));
                if (tmpiRet) {
                    LOGE("jni.postRecData： qPostMsg failed, preAecQ, tmpiRet %d,  ", tmpiRet);
                    break;
                }
                tmpiRet = qPostMsg(&pkt, pktSize, &pMgr->m_tmpRec.aecThreadQ, _T("jni.postRecData"));
                if (tmpiRet) {
                    LOGE("jni.postRecData： qPostMsg failed, preAecQ, tmpiRet %d,  ", tmpiRet);
                    break;
                }
                tmpiRet = qPostMsg(&pkt, pktSize, &pMgr->m_tmpRec.aecThreadQ, _T("jni.postRecData"));
                if (tmpiRet) {
                    LOGE("jni.postRecData： qPostMsg failed, preAecQ, tmpiRet %d,  ", tmpiRet);
                    break;
                }
                tmpiRet = qPostMsg(&pkt, pktSize, &pMgr->m_tmpRec.aecThreadQ, _T("jni.postRecData"));
                if (tmpiRet) {
                    LOGE("jni.postRecData： qPostMsg failed, preAecQ, tmpiRet %d,  ", tmpiRet);
                    break;
                }
                tmpiRet = qPostMsg(&pkt, pktSize, &pMgr->m_tmpRec.aecThreadQ, _T("jni.postRecData"));
                if (tmpiRet) {
                    LOGE("jni.postRecData： qPostMsg failed, preAecQ, tmpiRet %d,  ", tmpiRet);
                    break;
                }

            }
        }


        //
        iErr = 0;
    } while ( false );

    //
    return  iErr;
}







extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_getRecData(JNIEnv *env, jclass clazz,
                                                   jintArray pi_sample_time_in_ms, jbyteArray data,
                                                   jintArray p_len) {
    int iErr = -1;
    int len;

    CTmpQMgr *pMgr = &g_tmpQMgr;

    // TODO: implement getRecData()
    if (pi_sample_time_in_ms == mynull) {
        LOGE("jni.getRecData failed, pi_sampleTimeInMs is null");
        return -1;
    }
    if (data == mynull) {
        LOGE("jni.getRecData failed, data is null");
        return -1;
    }
    if (p_len == mynull) {
        LOGE("jni.getRecData failed, p_len is null");
        return -1;
    }

    len = (env)->GetArrayLength(pi_sample_time_in_ms);
    if (len < 1) {
        LOGE("jni.getRecData failed, len of pi_sampleTimeInMs < 1>");
        return -1;
    }
    len = (env)->GetArrayLength(p_len);
    if (len < 1) {
        LOGE("jni.getRecData failed, len of p_len < 1>");
        return -1;
    }
    len = env->GetArrayLength(data);
    if (len < 4000) {
        LOGE("jni.getRecData failed: len of data < 4000");
        return -1;
    }

    int tmpiRet;
    jboolean isCopy;
    // 可能复制也可能不复制
    tmpiRet = -1;

    char buf[4000];
    int len_buf = 0;
    int  iSampleTimeInMs  =  0;
    int i;


    //
    for (i = 0; i < 4; i++) {
        TmpAPkt pkt;
        len = sizeof(pkt);
        tmpiRet = qGetMsg(&pMgr->m_tmpRec.recQ, &pkt, (unsigned int *) &len);
        if (tmpiRet != 0) {
            break;
        }
        //
        if  (  pkt.len  >  sizeof(buf)  -  len_buf)  {
            LOGE("jni.getRecData failed, pkt.len too big");
            return  -1;
        }
        //
        memcpy(  buf  +  len_buf,  pkt.buf,  pkt.len);
        len_buf  +=  pkt.len;
        //
        if  (  i  ==  0  )  {
            iSampleTimeInMs  =  pkt.iSampleTimeInMs;
        }
    }
    //
    if  (  len_buf  ==  0)  {
        if  (  0  ) {
            LOGE("jni.getRecData failed, nothing got ");
        }
        //
        return  -1;
    }

    //
    jbyte *buffer = env->GetByteArrayElements(data, &isCopy);
    if (buffer) {
        //
        //tmpiRet  =  qGetMsg(&pQMem->q,buffer, (unsigned int *)&len);
        //memcpy(buffer, pkt.buf, pkt.len);
        memcpy( buffer, buf, len_buf);

        // 必须释放
        env->ReleaseByteArrayElements(data, buffer, 0);
    }

    tmpiRet = -1;
    jint *buf_len = env->GetIntArrayElements(p_len, &isCopy);
    if (buf_len) {
        //
        buf_len[0] = len_buf;//pkt.len;//len;
        tmpiRet = 0;
        //
        env->ReleaseIntArrayElements(p_len, buf_len, 0);
    }
    if (tmpiRet != 0) {
        LOGE("jni.getRecData: set len failed");
        return -1;
    }

    tmpiRet = -1;
    jint *buf_iSampleTimeInMs = env->GetIntArrayElements(pi_sample_time_in_ms, &isCopy);
    if (buf_iSampleTimeInMs) {
        //
        buf_iSampleTimeInMs[0] = iSampleTimeInMs;//len;
        tmpiRet = 0;
        //
        env->ReleaseIntArrayElements(pi_sample_time_in_ms, buf_iSampleTimeInMs, 0);
    }
    if (tmpiRet != 0) {
        LOGE("jni.getRecData: set iSampleTimeInMs failed");
        return -1;
    }

    //
    if  (  0  ) {
        int nQNodes = pMgr->m_tmpRec.recQ.uiQNodes;
        LOGD("jni.getRecData: get %dbytes, iSampleTimeInMs %d. nQNodes %d", len_buf,  iSampleTimeInMs,  nQNodes);
    }

    //
    iErr = 0;

    //
    return  iErr;


}

extern "C"
JNIEXPORT jint JNICALL
Java_com_qycx_tmpjavalibrary_TmpJniUtil_getAecStatus(JNIEnv *env, jclass clazz,
                                                   jintArray pi_nStep_mix,
                                                   jintArray pi_nStep_aec) {
    int iErr = -1;
    int len;

    CTmpQMgr *pMgr = &g_tmpQMgr;

    // TODO: implement getRecData()
    if (pi_nStep_mix == mynull) {
        LOGE("jni.getAecStatus failed, pi_nStep_mix is null");
        return -1;
    }
    if (pi_nStep_aec == mynull) {
        LOGE("jni.getAecStatus failed, pi_nStep_aec is null");
        return -1;
    }

    len = (env)->GetArrayLength(pi_nStep_mix);
    if (len < 1) {
        LOGE("jni.getAecStatus failed, len of pi_nStep_mix < 1>");
        return -1;
    }
    len = (env)->GetArrayLength(pi_nStep_aec);
    if (len < 1) {
        LOGE("jni.getAecStatus failed, len of pi_nStep_aec < 1>");
        return -1;
    }

    //
    AEC_layout   *  pLayout  =  &pMgr->m_aecLayout;


    int tmpiRet;
    jboolean isCopy;
    // 可能复制也可能不复制
    tmpiRet = -1;

    tmpiRet = -1;
    jint *buf_len = env->GetIntArrayElements(pi_nStep_mix, &isCopy);
    if (buf_len) {
        //
        buf_len[0] = pLayout->status.nStep_mix;
        tmpiRet = 0;
        //
        env->ReleaseIntArrayElements(pi_nStep_mix, buf_len, 0);
    }
    if (tmpiRet != 0) {
        LOGE("jni.getAecStatus: set nStep_mix failed");
        return -1;
    }

    tmpiRet = -1;
    jint *buf_nStep_aec = env->GetIntArrayElements(pi_nStep_aec, &isCopy);
    if (buf_nStep_aec) {
        //
        buf_nStep_aec[0] = pLayout->status.nStep_aec;//len;
        tmpiRet = 0;
        //
        env->ReleaseIntArrayElements(pi_nStep_aec, buf_nStep_aec, 0);
    }
    if (tmpiRet != 0) {
        LOGE("jni.getAecStatus: set nStep_aec failed");
        return -1;
    }


    //
    iErr = 0;

    //
    return  iErr;


}



