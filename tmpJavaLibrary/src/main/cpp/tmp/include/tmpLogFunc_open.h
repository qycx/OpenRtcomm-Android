
#ifndef  __tmpLogFunc_open_h__
#define  __tmpLogFunc_open_h__	//  {



#ifdef  __ANDROID__

#include <android/log.h>
//const char *LOG_TGA="LOG_TGA";
#define LOG_TAG "anc"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG ,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG ,__VA_ARGS__)

#else 

#define     LOGD    printf
#define     LOGE    printf

#endif 


#endif 
