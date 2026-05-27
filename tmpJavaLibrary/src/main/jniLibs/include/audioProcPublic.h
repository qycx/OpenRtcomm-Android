
#ifndef  __audioProcPublic_h__
#define  __audioProcPublic_h__  //  {

#include    <stdio.h>
#include 	"myTypes.h"

#ifdef  __WEBRTC__

#ifdef  RTC_EXPORT 
#undef  RTC_EXPORT
#endif
//
#define  RTC_EXPORT __attribute__((visibility("default")))

#define 	MY_EXPORT	RTC_EXPORT

#else

#define     MY_EXPORT 

#endif

//
typedef int (*PF_inStream4)(void* p0, void* p1, void* p2,void *p3);     //play

//
typedef int (*PF_outStream)(void* p0, void* p1, void* p2);

//
typedef  int  (*PF_preMix)(void* p0, void* p1, void* p2);


//
// AGC≈‰÷√Ω·ππÃÂ
typedef struct {
    int16_t targetLevelDbfs;
    int16_t compressionGaindB;
    uint8_t limiterEnable;
} WebRtcAgcConfigWrapper;

// AECM≈‰÷√Ω·ππÃÂ
typedef struct {
    int16_t cngMode;
    int16_t echoMode;
} WebRtcAecmConfigWrapper;





//
extern "C" __declspec(dllexport) int aec_doTest();

extern "C" __declspec(dllexport) int initAecObj(int sampleRate, void** ppObj);
extern "C" __declspec(dllexport) int exitAecObj(void** ppObj);
extern "C" __declspec(dllexport) int aecObj_doAec(void* pObjParam,
                  char* render_data,
                  unsigned int render_dataLen,
                  char* mic_data,
                  unsigned int mic_dataLen);



                             //
#define     MAX_fps     25
//
typedef  struct  __tmpMixer_t {
    FILE* fps[MAX_fps];
    bool  bQuit;        //  mixÕ£÷π
    bool  bStop;        //  mix‘›Õ£  
}        TmpMixer;

//
extern  "C"  MY_EXPORT int  mixer_main(TmpMixer * pTm, int  nSources, PF_inStream4 pf_in, void* p0_in, PF_preMix  pf_preMix, void* p0_preMix, PF_outStream  pf_out, void* p0_out);

//
extern  "C"  MY_EXPORT  void* myWebRtcAecm_Create();
extern  "C"  MY_EXPORT  int myWebRtcAecm_Init(void* aecmInst, int sampFreq);
extern  "C"  MY_EXPORT  void myWebRtcAecm_Free(void* aecmInst);
extern  "C"  MY_EXPORT  int myWebRtcAecm_BufferFarend(void* aecmInst, const int16_t * farend, int nrOfSamples);
extern  "C"  MY_EXPORT  int myWebRtcAecm_Process(void* aecmInst, const int16_t * nearendNoisy, const int16_t * nearendClean,  int16_t* out, int nrOfSamples, int msInSndCardBuf);
extern  "C"  MY_EXPORT  int myWebRtcAecm_set_config(void* aecmInst, const WebRtcAecmConfigWrapper * config);



//
typedef  struct  __tmpAp_t {
    bool  bQuit;        //  apÕ£÷π
    bool  bStop;        //  ap‘›Õ£  

}        TmpAp;


//
extern  "C"  MY_EXPORT  int  ap_main(TmpAp * pTm, int  nSources_unused, PF_inStream4 pf_in, void* p0_in, PF_inStream4 pf_in_rev, void* p0_in_rev, PF_preMix  pf_preMix, void* p0_preMix, PF_outStream  pf_out, void* p0_out);





#ifdef  __ANDROID__
//#include 	"jni.h"
#include 	"android/log.h"
#define  LOG_TAG  "MyTag"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__ )
#else
#define  LOG_TAG  "MyTag"
#define  LOGI(...)  

#endif 


#endif  //  }



