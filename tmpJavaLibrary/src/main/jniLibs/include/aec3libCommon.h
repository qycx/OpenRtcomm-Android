
#ifndef  __aec3libCommon_h__
#define  __aec3libCommon_h__	//  {

#include	"aec3libPublic.h"


typedef  struct  __aecObj_t {
			
				 //
				 int sample_rate;
	
				 //  
				 void* pEchoCanceller3Tester;  
				 void* pEchoCanceller3;

} AEC_obj;


//
typedef  struct  __nsObj_t {

				 //
				 int	sample_rate;

				 //
				 void  *  pNoiseSuppressor;
				 //
				 void* pAudioBuffer;

}  NS_obj;




//
#endif  //  }


