/* ================================================================== */
/*                                                                    */ 
/*    Microsoft Speech coder     ANSI-C Source Code                   */
/*    SC1200 1200 bps speech coder                                    */
/*    Fixed Point Implementation      Version 7.0                     */
/*    Copyright (C) 2000, Microsoft Corp.                             */
/*    All rights reserved.                                            */
/*                                                                    */ 
/* ================================================================== */

/* ========================================= */
/* melp.c: Mixed Excitation LPC speech coder */
/* ========================================= */

#include <stdio.h>
#include <string.h>
#include <stdlib.h>

#include "sc1200.h"
#include "mat_lib.h"
#include "global.h"
#include "macro.h"
#include "mathhalf.h"
#include "dsp_sub.h"
#include "melp_sub.h"
#include "constant.h"
#include "math_lib.h"
#include "math.h"
#include "transcode.h"

#if NPP
#include "npp.h"
#endif

//
#include    "myMelpPublic.h"
//#include    "tmpFfmpegCommon.h"
#include	"tmpLogFunc_open.h"

//
#define X05_Q7				64         /* 0.5 * (1 << 7) */
#define THREE_Q7			384        /* 3 * (1 << 7) */

/* ====== External memory ====== */

#if  0
#define		Shortword	int16_t
#define		Longword	int32_t


//Shortword	mode;
//Shortword   chwordsize;
#endif

/* ========== Static definations ========== */

#define PROGRAM_NAME			"SC1200 1200 bps speech coder"
#define PROGRAM_VERSION			"Version 7 / 42 Bits"
#define PROGRAM_DATE			"10/25/2000"

/* ========== Static Variables ========== */

//static char in_name[100], out_name[100];

/* ========== Local Private Prototypes ========== */

static void		parseCommandLine(int argc, char *argv[],EncAMelp*pEncObj);
static void		printHelpMessage(char *argv[]);



//int16_t tmp_readbl(int16_t input[], FILE* fp_in, int16_t size)
int16_t tmp_readbl(int16_t input[], TransMelp  *  pEncObj, int16_t size)
{
	int16_t	length;

	//FILE* fp_in = pEncObj->fp_in;
	//length = (int16_t)fread(input, sizeof(int16_t), size, fp_in);

	//
	length = (int16_t)tmp_fread(input, sizeof(int16_t), size, pEncObj);
	//
	v_zap(&(input[length]), (int16_t)(size - length));

	return(length);
}

//void tmp_writebl(int16_t output[], FILE* fp_out, int16_t size)
void tmp_writebl(int16_t output[], TransMelp* pEncObj, int16_t size)
{
	//fwrite(output, sizeof(int16_t), size, pEncObj->fp_out);
	tmp_fwrite(output, sizeof(int16_t), size, pEncObj);
}






/****************************************************************************
**
** Function:        main
**
** Description:     The main function of the speech coder
**
** Arguments:
**
**  int     argc    ---- number of command line parameters
**  char    *argv[] ---- command line parameters
**
** Return value:    None
**
*****************************************************************************/
int melp_enc_main(int argc, char *argv[],EncAMelp  *  pEncObj)
{
	Longword	length;
	Shortword	speech_in[BLOCK], speech_out[BLOCK];
	Shortword	bitBufSize, bitBufSize12, bitBufSize24;
                                          /* size of the bitstream buffer */
	BOOLEAN		eof_reached = FALSE;
	//FILE	*fp_in, *fp_out;

	/* ====== Get input parameters from command line ====== */
	parseCommandLine(argc, argv,pEncObj);

	/* ====== Open input, output, and parameter files ====== */
#if  0
	if ((pEncObj->fp_in = fopen(pEncObj->common.in_name,"rb")) == NULL){
		fprintf(stderr, "  ERROR: cannot read file %s.\n", pEncObj->common.in_name);
		exit(1);
	}
	if ((pEncObj->fp_out = fopen(pEncObj->common.out_name, "wb")) == NULL) {
		fprintf(stderr, "  ERROR: cannot write file %s.\n", pEncObj->common.out_name);
		exit(1);
	}

#endif 
	//
	if (tmp_fopen_r(pEncObj)) {
		goto  errLabel;
	}
	if (tmp_fopen_w(pEncObj)) {
		goto  errLabel;
	}

	/* ====== Initialize MELP analysis and synthesis ====== */
	if (pEncObj->common.rate == RATE2400)
		pEncObj->common.frameSize = (Shortword) FRAME;
	else
		pEncObj->common.frameSize = (Shortword) BLOCK;
	/* Computing bit=Num = rate * frameSize / FSAMP.  Note that bitNum        */
	/* computes the number of bytes written to the channel and it has to be   */
	/* exact.  We first carry out the division and then have the multiplica-  */
	/* tion with round1ing.                                                    */
    pEncObj->common.bitNum12 = 81;
    pEncObj->common.bitNum24 = 54;
    if( pEncObj->common.chwordsize == 8 ){
        // packing the bitstream
        bitBufSize12 = 11;
        bitBufSize24 = 7;
    }else if( pEncObj->common.chwordsize == 6 ){
        bitBufSize12 = 14;
        bitBufSize24 = 9;
    }else{
        fprintf(stderr,"Channel word size is wrong!\n");
        exit(-1);
    }

    if (pEncObj->common.rate == RATE2400){
		pEncObj->common.frameSize = FRAME;
		bitBufSize = bitBufSize24;
	} else {
		pEncObj->common.frameSize = BLOCK;
		bitBufSize = bitBufSize12;
	}

	if (pEncObj->common.mode != SYNTHESIS)
		melp_ana_init(&pEncObj->common);
	if (pEncObj->common.mode != ANALYSIS)
		melp_syn_init(&pEncObj->common);


	/* ====== Run MELP coder on input signal ====== */

	pEncObj->common.frame_count = 0;
	eof_reached = FALSE;
	while (!eof_reached){
        //
		fprintf(stderr, "Frame = %ld\r", pEncObj->common.frame_count);
        //
        g_frameCnt  = pEncObj->common.frame_count;
        if  (  g_bDbg  )  {
            LOGD("frame_count %d", g_frameCnt);
        }

        //
#if  0
		if (pEncObj->common.mode == DOWN_TRANS){
			/* --- Read 2.4 channel input --- */
            if( pEncObj->common.chwordsize == 8 ){
			    length = fread(pEncObj->common.chbuf, sizeof(unsigned char),
						   (bitBufSize24*NF), fp_in);
            }else{
    			int i, readNum;
				unsigned int bitData;
				for(i = 0; i < bitBufSize24*NF; i++){
					readNum = fread(&bitData,sizeof(unsigned int),1,fp_in);
					if( readNum != 1 )	break;
					pEncObj->common.chbuf[i] = (unsigned char)bitData;
				}	
				length = i;		
            }

			if (length < (bitBufSize24*NF)){
				eof_reached = TRUE;
				break;
			}
			transcode_down(&pEncObj->common);
			/* --- Write 1.2 channel output --- */
            if( pEncObj->common.chwordsize == 8 ){
			    fwrite(pEncObj->common.chbuf, sizeof(unsigned char), bitBufSize12, fp_out);
            }else{
				int i;
				unsigned int bitData;
				for(i = 0; i < bitBufSize12; i++){
					bitData = (unsigned int)(pEncObj->common.chbuf[i]);
					fwrite(&bitData, sizeof(unsigned int), 1, fp_out);
				}
			}
		} else if (pEncObj->common.mode == UP_TRANS){
			/* --- Read 1.2 channel input --- */
            if( pEncObj->common.chwordsize == 8 ){
			    length = fread(pEncObj->common.chbuf, sizeof(unsigned char), bitBufSize12, fp_in);
            }else{
    			int i, readNum;
				unsigned int bitData;
				for(i = 0; i < bitBufSize12; i++){
					readNum = fread(&bitData,sizeof(unsigned int),1,fp_in);
					if( readNum != 1 )	break;
					pEncObj->common.chbuf[i] = (unsigned char)bitData;
				}	
				length = i;		
            }

			if (length < bitBufSize12){
				eof_reached = TRUE;
				break;
			}
			transcode_up(&pEncObj->common);
			/* --- Write 2.4 channel output --- */
            if( pEncObj->common.chwordsize == 8 ){
			    fwrite(pEncObj->common.chbuf, sizeof(unsigned char), (bitBufSize24*NF), fp_out);
            }else{
				int i;
				unsigned int bitData;
				for(i = 0; i < bitBufSize24*NF; i++){
					bitData = (unsigned int)(pEncObj->common.chbuf[i]);
					fwrite(&bitData, sizeof(unsigned int), 1, fp_out);
				}
			}

		} else 
#endif 
		{
			/* Perform MELP analysis */
			if (pEncObj->common.mode != SYNTHESIS){
				/* read input speech */
				//length = readbl(speech_in, pEncObj->fp_in, pEncObj->common.frameSize);
				length = tmp_readbl(speech_in, pEncObj, pEncObj->common.frameSize);
				if (length < pEncObj->common.frameSize){
					v_zap(&speech_in[length], (Shortword) (FRAME - length));
					eof_reached = TRUE;
				}

				/* ---- Noise Pre-Processor ---- */
#if NPP
				if (pEncObj->common.rate == RATE1200){
					npp(speech_in, speech_in,&pEncObj->common);
					npp(&(speech_in[FRAME]), &(speech_in[FRAME]),&pEncObj->common);
					npp(&(speech_in[2*FRAME]), &(speech_in[2*FRAME]),&pEncObj->common);
				} else
					npp(speech_in, speech_in,&pEncObj->common);
#endif
				analysis(speech_in, pEncObj->common.melp_par,&pEncObj->common);

				/* ---- Write channel output if needed ---- */
                if (pEncObj->common.mode == ANALYSIS){
                    if( pEncObj->common.chwordsize == 8 ){
					    //fwrite(pEncObj->common.chbuf, sizeof(unsigned char), bitBufSize, pEncObj->fp_out);
						tmp_fwrite(pEncObj->common.chbuf, sizeof(unsigned char), bitBufSize, pEncObj);
                    }else{
        				int i;
		        		unsigned int bitData;
				        for(i = 0; i < bitBufSize; i++){
					        bitData = (unsigned int)(pEncObj->common.chbuf[i]);
					        //fwrite(&bitData, sizeof(unsigned int), 1, pEncObj->fp_out);
							tmp_fwrite(&bitData, sizeof(unsigned int), 1, pEncObj);
				        }
			        }
                }
			}

			/* ====== Perform MELP synthesis (skip first frame) ====== */
			if (pEncObj->common.mode != ANALYSIS){

				/* Read channel input if needed */
				if (pEncObj->common.mode == SYNTHESIS){
                    if( pEncObj->common.chwordsize == 8 ){
					    //length = fread(pEncObj->common.chbuf, sizeof(unsigned char), bitBufSize, pEncObj->fp_in);
						length = tmp_fread(pEncObj->common.chbuf, sizeof(unsigned char), bitBufSize, pEncObj);
                    }else{
    		        	int i, readNum;
        				unsigned int bitData;
		        		for(i = 0; i < bitBufSize; i++){
				        	//readNum = fread(&bitData,sizeof(unsigned int),1,pEncObj->fp_in);
							readNum = tmp_fread(&bitData, sizeof(unsigned int), 1, pEncObj);
        					if( readNum != 1 )	break;
		        			pEncObj->common.chbuf[i] = (unsigned char)bitData;
				        }	
        				length = i;		
                    }
					if (length < bitBufSize){
						eof_reached = TRUE;
						break;
					}
				}
				synthesis(pEncObj->common.melp_par, speech_out,&pEncObj->common);
				//writebl(speech_out, pEncObj->fp_out, pEncObj->common.frameSize);
				tmp_writebl(speech_out, pEncObj, pEncObj->common.frameSize);
			}
		}
		pEncObj->common.frame_count ++;
	}

errLabel:

	//fclose(pEncObj->fp_in);
	//fclose(pEncObj->fp_out);
	tmp_fclose_r(pEncObj);
	tmp_fclose_w(pEncObj);
	//
	fprintf(stderr, "\n\n");

	return(0);
}


/****************************************************************************
**
** Function:        parseCommandLine
**
** Description:     Translate command line parameters
**
** Arguments:
**
**  int     argc    ---- number of command line parameters
**  char    *argv[] ---- command line parameters
**
** Return value:    None
**
*****************************************************************************/
static void		parseCommandLine(int argc, char *argv[],EncAMelp  * pEncObj)
{
	BOOLEAN		error_flag = FALSE;

	if (argc != 3)
		error_flag = TRUE;

	/* Setting default values. */
	pEncObj->common.mode = ANALYSIS;
	pEncObj->common.rate = RATE2400;
    pEncObj->common.chwordsize = 8;         // this is for packed bitstream
	pEncObj->common.in_name[0] = '\0';
	pEncObj->common.out_name[0] = '\0';

	if( argc == 3 ){
		strcpy(pEncObj->common.in_name, argv[1]);
		strcpy(pEncObj->common.out_name, argv[2]);
	}

    if ((pEncObj->common.in_name[0] == '\0') || (pEncObj->common.out_name[0] == '\0'))
		error_flag = TRUE;

	if (error_flag){
		printHelpMessage(argv);
		exit(1);
	}

	fprintf(stderr, "\n\n\t%s %s, %s\n\n", PROGRAM_NAME, PROGRAM_VERSION,
			PROGRAM_DATE);
	switch (pEncObj->common.mode){
	case ANA_SYN:
	case ANALYSIS:
	case SYNTHESIS:
		if (pEncObj->common.rate == RATE2400)
			fprintf(stderr, " ---- 2.4kbps mode.\n");
		else
			fprintf(stderr, " ---- 1.2kbps mode.\n");
		break;
	}
	switch (pEncObj->common.mode){
	case ANA_SYN:
		fprintf(stderr, " ---- Analysis and Synthesis.\n"); break;
	case ANALYSIS:
		fprintf(stderr, " ---- Analysis only.\n"); break;
	case SYNTHESIS:
		fprintf(stderr, " ---- Synthesis only.\n"); break;
	case UP_TRANS:
		fprintf(stderr, " ---- Transcoding from 1.2kbps to 2.4kbps.\n"); break;
	case DOWN_TRANS:
		fprintf(stderr, " ---- Transcoding from 2.4kbps to 1.2kbps.\n"); break;
	}

	fprintf(stderr, " ---- input from %s.\n", pEncObj->common.in_name);
	fprintf(stderr, " ---- output to %s.\n", pEncObj->common.out_name);
}


/****************************************************************************
**
** Function:        printHelpMessage
**
** Description:     Print Command Line Usage
**
** Arguments:
**
** Return value:    None
**
*****************************************************************************/
static void		printHelpMessage(char *argv[])
{
	fprintf(stderr, "\n\n\t%s %s, %s\n\n", PROGRAM_NAME, PROGRAM_VERSION,
			PROGRAM_DATE);
	fprintf(stdout, "Usage:\n");
	fprintf(stdout, "\t%s infile outfile\n\n", argv[0]);
}
