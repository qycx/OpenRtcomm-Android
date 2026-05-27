
#ifndef  __myMelpPublic_h__
#define  __myMelpPublic_h__     //  {

//
#include    "myTypes_basic.h"

//
#include "sc1200.h"
#include    "npp.h"
#include    "cprv.h"

//
#include    <stdio.h>

//

//
//
typedef  struct  __transMelp_t {

    mybool         bQuit;

    //
#if  10
#define		Shortword	short   //int16_t
#define		Longword	int     //int32_t


    Shortword	mode;
    Shortword   chwordsize;
#endif

    /* ====== Entities from Enhanced_Data ====== */
    int16_t	enh_i;// = 0;                               /* formerly D->I */
    int16_t	lambdaD[ENH_VEC_LENF];             /* overestimated noise */
                                               /* psd(noise_bias * noisespect)*/
    int16_t	lambdaD_shift[ENH_VEC_LENF];
    int16_t	SN_LT;                                   /* long term SNR */
    int16_t	SN_LT_shift;
    int16_t	n_pwr;
    int16_t	n_pwr_shift;
    int16_t    YY[ENH_VEC_LENF];  /* signal periodogram of current frame */
    int16_t	YY_shift[ENH_VEC_LENF];
    int16_t	sm_shift[ENH_VEC_LENF];
    int16_t	noise_shift[ENH_VEC_LENF];
    int16_t	noise2_shift[ENH_VEC_LENF];

    //
    myint16_t	av_shift[ENH_VEC_LENF];
    myint16_t	av2_shift[ENH_VEC_LENF];

    //
    myint16_t	act_min[ENH_VEC_LENF];  /* current minimum of long window */
    myint16_t	act_min_shift[ENH_VEC_LENF];
    //
    myint16_t	act_min_sub[ENH_VEC_LENF];
    /* current minimum of sub-window */
    myint16_t	act_min_sub_shift[ENH_VEC_LENF];

    //
    int16_t	vk[ENH_VEC_LENF];
    int16_t	vk_shift[ENH_VEC_LENF];
    int16_t	ksi[ENH_VEC_LENF];                        /* a priori SNR */
    int16_t	ksi_shift[ENH_VEC_LENF];

    int16_t	var_rel[ENH_VEC_LENF];
                                       /* est. relative var. of smoothedspect */
    int16_t	var_rel_av;     /* average relative var. of smoothedspect */

    //
    int16_t	smoothedspect[ENH_VEC_LENF];  /* smoothed signal spectrum */
    int16_t    var_sp_av[ENH_VEC_LENF];
                                           /* estimated mean of smoothedspect */
    int16_t    var_sp_2[ENH_VEC_LENF];
                                     /* esitmated 2nd moment of smoothedspect */
    int16_t	noisespect[ENH_VEC_LENF];          /* estimated noise psd */
    int16_t	noisespect2[ENH_VEC_LENF];


    //
    myint16_t	alphacorr;        /* correction factor for alpha_var, Q15 */
    myint16_t	alpha_var[ENH_VEC_LENF];            /* variable smoothing */
    
    int16_t	circb[NUM_MINWIN][ENH_VEC_LENF];           /* ring buffer */
    int16_t	circb_shift[NUM_MINWIN][ENH_VEC_LENF];

    //
    int16_t	initial_noise[ENH_WINLEN];            /* look ahead noise */
                                                              /* samples (Q0) */
    int16_t	speech_in_npp[ENH_WINLEN];          /* input of one frame */
    int16_t	ybuf[2*ENH_WINLEN + 2];
                                 /* buffer for FFT, this can be eliminated if */
                            /* we can write a better real-FFT program for DSP */
    int32_t		temp_yy[ENH_WINLEN + 2];


    //
    unsigned char	bit_buffer[NUM_CH_BITS];


    //
    struct melp_param	melp_par[NF];                 /* melp analysis parameters */
    //
    unsigned char	chbuf[CHSIZE];                     /* channel bit data buffer */
    int16_t	frameSize, frameSize12, frameSize24; /* frame size 2.4 = 180 1.2 = 540 */
    myint16_t	bitNum, bitNum12, bitNum24;                /* number of bits */

    //
    classParam classStat[TRACK_NUM];                 /* class of subframes */

    //
    int16_t	codewd74[7];
    int16_t	codewd84[8];
    int16_t	codewd_13x9[13];

    //
    struct quant_param	quant_par;

    /* ====== Buffers ====== */
    int16_t	hpspeech[IN_BEG + BLOCK];       /* input speech buffer dc removed */
    int16_t	dcdel[DC_ORD];
    int16_t	dcdelin[DC_ORD];
    int16_t	dcdelout_hi[DC_ORD];
    int16_t	dcdelout_lo[DC_ORD];

    /* ====== Classifier ====== */
    int16_t	voicedEn, silenceEn;                                       /* Q11 */
    int32_t	voicedCnt;

    /* ====== Fourier Harmonics Weights ====== */
    int16_t	w_fs[NUM_HARM];                                            /* Q14 */
    int16_t	w_fs_inv[NUM_HARM];
    BOOLEAN		w_fs_init;// = FALSE;


    //
    /* ====== Data I/O for high level language implementation ====== */
    long	frame_count;
    short	rate;

    int32_t	saturation; // = 0;
    int32_t	temp_saturation;



    //
    struct melp_param	prev_par;
    int16_t	sigsave[PITCHMAX];
    int16_t	syn_begin;
    BOOLEAN	erase;


#define SIG_LENGTH			(LPF_ORD + PITCH_FR) /* 327 */

    //
    int16_t sigbuf[SIG_LENGTH];
    /////////////////////////////static classParam classStat[TRACK_NUM];                 /* class of subframes */
    pitTrackParam pitTrack[TRACK_NUM];               /* pitch of subframes */

    int16_t	top_lpc[LPC_ORD];

    //
    int16_t	sync_bit;   // = 0;                                 /* sync bit */

    //
    /* Memory definition */
    int16_t		wr_array[FFTLENGTH/2];
    int16_t		wi_array[FFTLENGTH/2];

    ///////////////////////////////

    //
    mybool          io_bUse_q;      //  true, 用q进行输入输出。 false, 用file输入输出

    //
    mybool          bUse_dump;


    //
    void        *   p_q_in;
    void        *   p_q_out;

    //
    mybool          bDec;

    //
    int             enc_lastPts;
    //
    int             enc_n_in;
    int             enc_n_out;

    //
    struct      {
        char buf[500000];   //  500k buf
        int len;
    }           aBuf;

    //
    char in_name[100], out_name[100];

    //
    FILE        *   fp_in1;
    FILE         *  fp_out1;

    //
    char pcm_dump_name_encA_data_melp[100];
    char pcm_dump_name_tmpRead[100];
    char enc_dump_name_tmpWrite[100];
    char enc_dump_name[100];

    //
    char  decA_dumpEncData_name[100];

    //
    FILE        *   fp_pcm_dump_encA_data_melp;         //
    FILE        *   fp_pcm_dump_tmpRead;                //
    FILE        *   fp_enc_dump_tmpWrite;               //
    FILE        *   fp_enc_dump;                        //  压缩数据的存储

    //
    FILE        *   fp_decA_dumpEncData;




    //
};


//
typedef  struct  __encAMelp_t  {
    //
    TransMelp      common;


    //
#ifdef  __ANDROID__
    pthread_t      tid;
#endif 
    //

};


//
typedef  struct  __decAMelp_t  {
    //
    TransMelp       common;

    //
    //
#ifdef  __ANDROID__
    pthread_t      tid;
#endif

};


//
typedef  struct  __melpAPkt_t  {
    //
    int    pts;
    int    uiSampleTimeInMs;
    //
    int    len;
    char   buf[160  *  2  *  4];        //  160  *  2为10ms的数据长度，放大4倍

}        MelpAPkt;




//
#ifdef  __cplusplus
extern "C" {
#endif



//
extern int g_bDbg;
extern int g_bPrint;
extern int g_frameCnt;


//
//
int  testMelp(int  *  p_i);

//
int   do8kTo16k(char *input, int nBytes, char  *  output,int *p_nBytes_output);
int   do16kTo8k(char *input, int nBytes, char  *  output, int *p_nBytes_output);


//
int melp_enc_main(int argc, char *argv[],EncAMelp  *  pEncObj);
int melp_dec_main(int argc, char *argv[],DecAMelp*pDecObj);




//
//
int encA_init_melp(int index_qdcUnit, mybyte ucCompressors, short wFormatTag, int bitrate, mybool bDbg);
int encA_exit_melp(int index_qdcUnit,mybool bDbg);
int encA_data_melp(int index_qdcUnit,mybyte * pcm,int pcmLen,mybyte * raw,int *pLen_raw, mybool bDbg);

//
int decA_init_melp(int index_qdcUnit, byte ucCompressors,short wFormatTag, mybool bDbg);
int decA_exit_melp(int index_qdcUnit,mybool bDbg);
int decA_data_melp(int index_qdcUnit, byte * encData,int encData_size, int iInputBufferPaddingSize, int *pnPktDeced, int * piFmt_dec, byte * decBuf, int *piDecBufSize, mybool *pbBufTooSmall, mybool bDbg);




//
#ifdef  __cplusplus
}
#endif


//
#endif  //  }


