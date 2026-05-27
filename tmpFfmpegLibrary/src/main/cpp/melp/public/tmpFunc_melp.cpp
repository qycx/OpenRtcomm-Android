
//#include	<stdio.h>
#include    "myTypes.h"
#include	"myMelpPublic.h"
#include    "tmpFfmpegCommon.h"
#include "genericqueue.h"
#ifdef  __arm__
#include    "ctxQmThread.h"
#endif


//size_t tmp_fread(char* buf, size_t elementSize, size_t elementCount, FILE* fp)
extern  "C"  size_t tmp_fread(char* buf, size_t elementSize, size_t elementCount, TransMelp* pTransObj)
{
	int cnt  =  0;

    //
    //

    if  (  pTransObj->io_bUse_q  )  {
        //
        GENERIC_Q  *  p_q_in  =  (GENERIC_Q*)pTransObj->p_q_in;

        MelpAPkt pkt={0};
        unsigned  int  uiLen;

        //
        int  nNeeded;
        nNeeded  =  elementSize  *  elementCount;
        if  (  nNeeded  ==  0)  {
            LOGD("tmp_fread: nNeeded is 0");
            return  0;
        }

        //
        for  (  ; ;  ) {

            if (  pTransObj->bQuit  )  {
                break;
            }

            //  数据充足，就取走
            if  (  pTransObj->aBuf.len  >=  nNeeded  )  {
                memcpy(  buf,  pTransObj->aBuf.buf,nNeeded);
                //
                if  (  pTransObj->bUse_dump )  {
                    if  (  0  ) {
                        fwrite(buf, elementSize, elementCount, pTransObj->fp_pcm_dump_tmpRead);
                    }
                }
                //
                int nLeft;
                nLeft  =  pTransObj->aBuf.len  -  nNeeded;
                if  (  nLeft) {
                    memcpy(pTransObj->aBuf.buf, pTransObj->aBuf.buf + nNeeded, nLeft);
                }
                pTransObj->aBuf.len  =  nLeft;

                //
                pTransObj->enc_n_in  ++  ;

                //
                cnt  =  elementCount;
                break;
            }

            //  不够，就取一个包
            for  (  ;  ;  )  {
                if  (  pTransObj->bQuit)  {
                    break;
                }
                uiLen = sizeof(pkt);
                if  (  qGetMsg(p_q_in,&pkt,&uiLen)  )  {
                    if  (!isQEmpty(p_q_in)){
                        LOGE("tmp_fread: qGetMsg failed, l61");
                    }
                    //
                    mySleep(20);
                    //
                    continue;
                }
                if  (  pTransObj->aBuf.len  +  pkt.len  >  sizeof(pTransObj->aBuf.buf))  {
                    LOGE("tmp_fread failed: aBuf.len + pkt.len > sizeof(buf)");
                    break;
                }
                //
                memcpy(  pTransObj->aBuf.buf  +  pTransObj->aBuf.len, pkt.buf, pkt.len);
                pTransObj->aBuf.len  +=  pkt.len;
                //
                break;
            }

            //
            continue;
        }

        //
        return  cnt;
    }




    //
	cnt = fread(buf, elementSize, elementCount, pTransObj->fp_in1);
	//
	if (cnt != elementCount) {
		int  ii = 0;
	}
	//
	return  cnt;
}

//size_t tmp_fwrite(char* buf, size_t  elementSize, size_t      elementCount, FILE*fp)
extern  "C"  size_t tmp_fwrite(char* buf, size_t  elementSize, size_t      elementCount, TransMelp* pTransObj)

{
	int  cnt = 0;

    //
    if  (  pTransObj->io_bUse_q  )  {

        if  (  0  ) {
            if (pTransObj->bUse_dump) {
                cnt =  fwrite(buf,elementSize,elementCount,pTransObj->fp_enc_dump_tmpWrite);
                //
                if  (  cnt  != elementCount)  {
                    int ii  =  0;
                }
                //
                return cnt;
            }
        }



        //
        GENERIC_Q  *  p_q_out  =  ( GENERIC_Q*)pTransObj->p_q_out;
        //
        MelpAPkt pkt={0};

        //
        int  dataLen  =  elementSize  *  elementCount;
        if  (  !dataLen)  {
            LOGE("tmp_fwrite failed: dataLen is 0");
            return  0;
        }

        //
        if  (  !pTransObj->bDec  )  {
            pTransObj->enc_lastPts  ++  ;
        }

        //
        pkt.pts  =  pTransObj->enc_lastPts;
        //
        if  (  dataLen >  sizeof(pkt.buf))  {
            LOGE("tmp_fwrite failed, dataLen > sizeof(pkt.buf).l113");
            return  0;
        }
        pkt.len  =  dataLen;
        memcpy(pkt.buf,buf,dataLen);

        //
        if  (  0) {
            if (pTransObj->fp_enc_dump) {
                fwrite(pkt.buf, 1, pkt.len, pTransObj->fp_enc_dump);
                //
                LOGD("tmp_fwrite: dump %d bytes, pts %d", pkt.len, pkt.pts);
            }
        }

        //
        if  (  10  ) {
            //
            int pktSize = offsetof(MelpAPkt, buf) + pkt.len;
            int  tmpiRet;
            tmpiRet  =  qPostMsg(&pkt, pktSize, p_q_out, _T(""));
            if  (  tmpiRet) {
                LOGE("tmp_fwrite failed, qPostMsg to p_q_out failed, iRet %d. qNodes %d",  tmpiRet, p_q_out->uiQNodes);
            }
        }

        //
        pTransObj->enc_n_out  ++  ;

        //
        if  (  1  )  {
            LOGD("tmpWrite: n_in %d, n_out %d", pTransObj->enc_n_in, pTransObj->enc_n_out);
        }


        //
        cnt  =  elementCount;
        return  cnt;
    }




    //fwrite(pEncObj->common.chbuf, sizeof(unsigned char), bitBufSize, pEncObj->fp_out);
	cnt = fwrite(buf, elementSize, elementCount, pTransObj->fp_out1);
	//
	if (cnt != elementCount) {
		int  ii = 0;
	}

	//
	return  cnt;
}


extern  "C"  int  tmp_fopen_r(TransMelp* pTransObj)
{
    //
    if (  pTransObj->io_bUse_q  )  {
        return  0;
    }

    if ((pTransObj->fp_in1 = fopen(pTransObj->in_name, "rb")) == NULL) {
		fprintf(stderr, "  ERROR: cannot read file %s.\n", pTransObj->in_name);
		return  -1;

	}

	//
	return  0;
}

//
extern  "C"  int  tmp_fopen_w(TransMelp* pTransObj)
{
    //
    if (  pTransObj->io_bUse_q  )  {
        return  0;
    }

    if ((pTransObj->fp_out1 = fopen(pTransObj->out_name, "wb")) == NULL) {
		return  -1;
	}
	return  0;

}

extern  "C"  int  tmp_fclose_r(TransMelp* pTransObj)
{
    //
    if (  pTransObj->io_bUse_q  )  {
        return  0;
    }

    if (pTransObj->fp_in1) {
		fclose(pTransObj->fp_in1);
		pTransObj->fp_in1 = 0;
	}
	return  0;
}
extern  "C"  int  tmp_fclose_w(TransMelp* pTransObj)
{
    //
    if (  pTransObj->io_bUse_q  )  {
        return  0;
    }

    if (pTransObj->fp_out1) {
		fclose(pTransObj->fp_out1);
		pTransObj->fp_out1 = 0;
	}
	return  0;
}


//  糊弄一下
extern "C" int   do8kTo16k(char *input, int nBytes, char  *  output, int *p_nBytes_output)
{
    //
    int numSamples = nBytes / 2;
    //
    short  *  sInput=(short*)input;
    short  *  sOutput=(short*)output;

    //
    for (int i = 0; i < numSamples; i++) {
        short sample = sInput[i];
        //
        sOutput[2*i]  =  sample;
        sOutput[2*i+1]=sample;
    }

    if  (  p_nBytes_output)  {
        *p_nBytes_output  =  nBytes  *  2;
    }

    //
    return 0;
}


//
#if 0
#include <vector>
#include <cmath>
#include <algorithm>


#include <vector>
#include <algorithm>
#endif


class SimpleAntiAliasDownsampler {
private:
    static constexpr float lpfCoefficients[4] = {
            0.2f, 0.3f, 0.3f, 0.2f
    };

    static const int FILTER_TAPS = 4;

public:
    static int downsample16kTo8k(char  *  input16k_1,int nBytes_input16k,char  * output8k_1, int *p_nBytes_output8k) {

        int16_t  * input16k  =  (int16_t *)input16k_1;
        int input16k_size =  nBytes_input16k/2;

        if (input16k_size < FILTER_TAPS) {
            // 对于太短的输入，使用简单均值
            return -1;
        }

        int outputSamples = input16k_size / 2;
        //std::vector<int16_t> output8k(outputSamples);
        int16_t * output8k = (int16_t*)output8k_1;

        for (int i = 0; i < outputSamples; i++) {
            float sum = 0.0f;
            int startIdx = i * 2 - 1; // 中心在 i*2 和 i*2+1 之间

            for (int j = 0; j < FILTER_TAPS; j++) {
                int sampleIdx = startIdx + j;
                // 边界处理
                if (sampleIdx < 0) sampleIdx = 0;
                if (sampleIdx >= static_cast<int>(input16k_size))
                    sampleIdx = input16k_size - 1;

                sum += input16k[sampleIdx] * lpfCoefficients[j];
            }

            sum = mymax(-32768.0f, mymin(32767.0f, sum));
            output8k[i] = static_cast<int16_t>(sum);
        }

        if  (  p_nBytes_output8k)  {
            *p_nBytes_output8k=outputSamples  *  2;
        }

        return 0;
    }
};
//
extern "C" int   do16kTo8k(char *input_16k, int nBytes, char  *  output_8k, int *p_nBytes_output)
{
    //
#if  0
    short  *  sInput  =  (short*)input_16k;
    short  *  sOutput  =  (short*)output_8k;
    //
    int numSamples = nBytes / 2  /  2;      //

    //
    for (int i = 0; i < numSamples; i++) {
        short sample = sInput[2* i];
        //
        sOutput[i]  =  sample;
    }

    //
    if  (  p_nBytes_output)  {
        *p_nBytes_output  =  nBytes  /  2;
    }

    //
    return 0;
#endif
    return  SimpleAntiAliasDownsampler::downsample16kTo8k(input_16k,nBytes,output_8k,p_nBytes_output);

}
