
#ifndef  __aec3libPublic_h__
#define  __aec3libPublic_h__  //  {

//
#include    "audioProcPublic.h"

//
int aec3lib_doTest();

//
int aec3lib_doTest888();
//



//
#ifdef  __cplusplus
extern  "C" {
#endif 

    MY_EXPORT  int initAec3Obj(int sampleRate, void** ppObj);
    MY_EXPORT  int exitAec3Obj(void** ppObj);
    MY_EXPORT  int aec3Obj_doAec(void* pObjParam,
        char* render_data,
        unsigned int render_dataLen,
        char* mic_data,
        unsigned int mic_dataLen);


    MY_EXPORT  int initNsObj(int sampleRate, void** ppObj);
    MY_EXPORT  int exitNsObj(void** ppObj);
    MY_EXPORT  int nsObj_do(void* pObjParam,
        char* mic_data,
        unsigned int mic_dataLen);





#ifdef  __cplusplus
}
#endif 


#endif  //  }



