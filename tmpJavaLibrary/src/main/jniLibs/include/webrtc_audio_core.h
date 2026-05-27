#ifndef WEBRTC_AUDIO_CORE_H
#define WEBRTC_AUDIO_CORE_H

#include <stdint.h>
#include <stddef.h>

#ifdef __cplusplus
extern "C" {
#endif

#if  0
// AGC配置结构体
typedef struct {
    int16_t targetLevelDbfs;
    int16_t compressionGaindB;
    uint8_t limiterEnable;
} WebRtcAgcConfigWrapper;

// AECM配置结构体
typedef struct {
    int16_t cngMode;
    int16_t echoMode;
} WebRtcAecmConfigWrapper;
#endif

// 导出符号宏定义
#if defined(_WIN32)
    #define AUDIO_CORE_API __declspec(dllexport)
#else
    #define AUDIO_CORE_API __attribute__((visibility("default")))
#endif

// ====== NS/NSX 接口 ======
AUDIO_CORE_API void* WebRTC_NS_Create();
AUDIO_CORE_API int WebRTC_NS_Init(void* nsInst, int frequency);
AUDIO_CORE_API int WebRTC_NS_SetPolicy(void* nsInst, int mode);
AUDIO_CORE_API void WebRTC_NS_Process(void* nsInst, float* inFrame, int num_bands, float* outFrame);
AUDIO_CORE_API void WebRTC_NS_Free(void* nsInst);

AUDIO_CORE_API void* WebRTC_NSX_Create();
AUDIO_CORE_API int WebRTC_NSX_Init(void* nsxInst, int frequency);
AUDIO_CORE_API int WebRTC_NSX_SetPolicy(void* nsxInst, int mode);
AUDIO_CORE_API void WebRTC_NSX_Process(void* nsxInst, int16_t* speechFrame, int num_bands, int16_t* outFrame);
AUDIO_CORE_API void WebRTC_NSX_Free(void* nsxInst);

// ====== AECM 接口 ======
AUDIO_CORE_API void* WebRTC_AECM_Create();
AUDIO_CORE_API int WebRTC_AECM_Init(void* aecmInst, int sampFreq);
AUDIO_CORE_API void WebRTC_AECM_Free(void* aecmInst);
AUDIO_CORE_API int WebRTC_AECM_BufferFarend(void* aecmInst, const int16_t* farend, int nrOfSamples);
AUDIO_CORE_API int WebRTC_AECM_Process(void* aecmInst, const int16_t* nearendNoisy, const int16_t* nearendClean,
                                      int16_t* out, int nrOfSamples, int msInSndCardBuf);
AUDIO_CORE_API int WebRTC_AECM_SetConfig(void* aecmInst, const WebRtcAecmConfigWrapper * config);

// ====== AGC 接口 ======
AUDIO_CORE_API void* WebRTC_AGC_Create();
AUDIO_CORE_API void WebRTC_AGC_Free(void* agcInst);
AUDIO_CORE_API int WebRTC_AGC_Init(void* agcInst, int minLevel, int maxLevel, int agcMode, int fs);
AUDIO_CORE_API int WebRTC_AGC_SetConfig(void* agcInst, const WebRtcAgcConfigWrapper * config);
AUDIO_CORE_API int WebRTC_AGC_Process(void* agcInst, const int16_t* inNear, int num_bands, int samples,
                                     int16_t* out, int inMicLevel, int* outMicLevel,
                                     int echo, uint8_t* saturationWarning);
AUDIO_CORE_API int WebRTC_AGC_AddFarend(void* agcInst, const int16_t* inFar, int samples);
AUDIO_CORE_API int WebRTC_AGC_AddMic(void* agcInst, const int16_t* inMic, int num_bands, int samples);
AUDIO_CORE_API int WebRTC_AGC_VirtualMic(void* agcInst, const int16_t* inMic, int num_bands,
                                        int samples, int micLevelIn, int* micLevelOut);

#ifdef __cplusplus
}
#endif

#endif // WEBRTC_AUDIO_CORE_H 