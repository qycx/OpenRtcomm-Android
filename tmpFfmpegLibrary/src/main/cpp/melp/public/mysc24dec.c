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
#include <errno.h>

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

#define X05_Q7				64         /* 0.5 * (1 << 7) */
#define THREE_Q7			384        /* 3 * (1 << 7) */

//
#include	"myMelpPublic.h"

/* ====== External memory ====== */
#if  0
#define		Shortword	int16_t
#define		Longword	int32_t

//
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

static void		parseCommandLine(int argc, char *argv[],DecAMelp * pDecObj);
static void		printHelpMessage(char *argv[]);

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
int melp_dec_main(int argc, char *argv[],DecAMelp*pDecObj)
{
	Longword	length;
	Shortword	speech_in[BLOCK], speech_out[BLOCK];
	Shortword	bitBufSize, bitBufSize12, bitBufSize24;
                                          /* size of the bitstream buffer */
	BOOLEAN		eof_reached = FALSE;
	//FILE	*fp_in, *fp_out;

	/* ====== Get input parameters from command line ====== */
	parseCommandLine(argc, argv,pDecObj);

	/* ====== Open input, output, and parameter files ====== */
#if  0
	if ((fp_in = fopen(pDecObj->common.in_name,"rb")) == NULL){
		fprintf(stderr, "  ERROR: cannot read file %s.\n", pDecObj->common.in_name);
		exit(1);
	}
	if ((fp_out = fopen(pDecObj->common.out_name,"wb")) == NULL){
        //
        char  *  str  =  strerror(errno); // 打印错误原因
        //
		fprintf(stderr, "  ERROR: cannot write file %s.\n", pDecObj->common.out_name);
		exit(1);
	}
#endif 
	if (tmp_fopen_r(&pDecObj->common)) {
		goto  errLabel;
	}
	if (tmp_fopen_w(&pDecObj->common)) {
		goto  errLabel;
	}

	/* ====== Initialize MELP analysis and synthesis ====== */
	if (pDecObj->common.rate == RATE2400)
		pDecObj->common.frameSize = (Shortword) FRAME;
	else
		pDecObj->common.frameSize = (Shortword) BLOCK;
	/* Computing bit=Num = rate * frameSize / FSAMP.  Note that bitNum        */
	/* computes the number of bytes written to the channel and it has to be   */
	/* exact.  We first carry out the division and then have the multiplica-  */
	/* tion with round1ing.                                                    */
    pDecObj->common.bitNum12 = 81;
    pDecObj->common.bitNum24 = 54;
    if( pDecObj->common.chwordsize == 8 ){
        // packing the bitstream
        bitBufSize12 = 11;
        bitBufSize24 = 7;
    }else if( pDecObj->common.chwordsize == 6 ){
        bitBufSize12 = 14;
        bitBufSize24 = 9;
    }else{
        fprintf(stderr,"Channel word size is wrong!\n");
        exit(-1);
    }

    if (pDecObj->common.rate == RATE2400){
		pDecObj->common.frameSize = FRAME;
		bitBufSize = bitBufSize24;
	} else {
		pDecObj->common.frameSize = BLOCK;
		bitBufSize = bitBufSize12;
	}

	if (pDecObj->common.mode != SYNTHESIS)
		melp_ana_init(&pDecObj->common);
	if (pDecObj->common.mode != ANALYSIS)
		melp_syn_init(&pDecObj->common);


	/* ====== Run MELP coder on input signal ====== */

	pDecObj->common.frame_count = 0;
	eof_reached = FALSE;
	while (!eof_reached){
		fprintf(stderr, "Frame = %ld\r", pDecObj->common.frame_count);

		//
#if  0
		if (pDecObj->common.mode == DOWN_TRANS){
			/* --- Read 2.4 channel input --- */
            if( pDecObj->common.chwordsize == 8 ){
			    length = fread(pDecObj->common.chbuf, sizeof(unsigned char),
						   (bitBufSize24*NF), fp_in);
            }else{
    			int i, readNum;
				unsigned int bitData;
				for(i = 0; i < bitBufSize24*NF; i++){
					readNum = fread(&bitData,sizeof(unsigned int),1,fp_in);
					if( readNum != 1 )	break;
					pDecObj->common.chbuf[i] = (unsigned char)bitData;
				}	
				length = i;		
            }

			if (length < (bitBufSize24*NF)){
				eof_reached = TRUE;
				break;
			}
			transcode_down(&pDecObj->common);
			/* --- Write 1.2 channel output --- */
            if( pDecObj->common.chwordsize == 8 ){
			    fwrite(pDecObj->common.chbuf, sizeof(unsigned char), bitBufSize12, fp_out);
            }else{
				int i;
				unsigned int bitData;
				for(i = 0; i < bitBufSize12; i++){
					bitData = (unsigned int)(pDecObj->common.chbuf[i]);
					fwrite(&bitData, sizeof(unsigned int), 1, fp_out);
				}
			}
		} else if (pDecObj->common.mode == UP_TRANS){
			/* --- Read 1.2 channel input --- */
            if( pDecObj->common.chwordsize == 8 ){
			    length = fread(pDecObj->common.chbuf, sizeof(unsigned char), bitBufSize12, fp_in);
            }else{
    			int i, readNum;
				unsigned int bitData;
				for(i = 0; i < bitBufSize12; i++){
					readNum = fread(&bitData,sizeof(unsigned int),1,fp_in);
					if( readNum != 1 )	break;
					pDecObj->common.chbuf[i] = (unsigned char)bitData;
				}	
				length = i;		
            }

			if (length < bitBufSize12){
				eof_reached = TRUE;
				break;
			}
			transcode_up(&pDecObj->common);
			/* --- Write 2.4 channel output --- */
            if( pDecObj->common.chwordsize == 8 ){
			    fwrite(pDecObj->common.chbuf, sizeof(unsigned char), (bitBufSize24*NF), fp_out);
            }else{
				int i;
				unsigned int bitData;
				for(i = 0; i < bitBufSize24*NF; i++){
					bitData = (unsigned int)(pDecObj->common.chbuf[i]);
					fwrite(&bitData, sizeof(unsigned int), 1, fp_out);
				}
			}

		} else 
#endif 
		{
			/* Perform MELP analysis */
			if (pDecObj->common.mode != SYNTHESIS){
				/* read input speech */
				//length = readbl(speech_in, fp_in, pDecObj->common.frameSize);
				length = tmp_readbl(speech_in, &pDecObj->common, pDecObj->common.frameSize);

				if (length < pDecObj->common.frameSize){
					v_zap(&speech_in[length], (Shortword) (FRAME - length));
					eof_reached = TRUE;
				}

				/* ---- Noise Pre-Processor ---- */
#if NPP
				if (pDecObj->common.rate == RATE1200){
					npp(speech_in, speech_in,&pDecObj->common);
					npp(&(speech_in[FRAME]), &(speech_in[FRAME]),&pDecObj->common);
					npp(&(speech_in[2*FRAME]), &(speech_in[2*FRAME]),&pDecObj->common);
				} else
					npp(speech_in, speech_in,&pDecObj->common);
#endif
				analysis(speech_in, pDecObj->common.melp_par,&pDecObj->common);

				/* ---- Write channel output if needed ---- */
                if (pDecObj->common.mode == ANALYSIS){
                    if( pDecObj->common.chwordsize == 8 ){
					    //fwrite(pDecObj->common.chbuf, sizeof(unsigned char), bitBufSize, fp_out);
						tmp_fwrite(pDecObj->common.chbuf, sizeof(unsigned char), bitBufSize, &pDecObj->common);
                    }else{
        				int i;
		        		unsigned int bitData;
				        for(i = 0; i < bitBufSize; i++){
					        bitData = (unsigned int)(pDecObj->common.chbuf[i]);
					        //fwrite(&bitData, sizeof(unsigned int), 1, fp_out);
							tmp_fwrite(&bitData, sizeof(unsigned int), 1, &pDecObj->common);
				        }
			        }
                }
			}

			/* ====== Perform MELP synthesis (skip first frame) ====== */
			if (pDecObj->common.mode != ANALYSIS){

				/* Read channel input if needed */
				if (pDecObj->common.mode == SYNTHESIS){
                    if( pDecObj->common.chwordsize == 8 ){
					    //length = fread(pDecObj->common.chbuf, sizeof(unsigned char), bitBufSize, fp_in);
						length = tmp_fread(pDecObj->common.chbuf, sizeof(unsigned char), bitBufSize, &pDecObj->common);
                    }else{
    		        	int i, readNum;
        				unsigned int bitData;
		        		for(i = 0; i < bitBufSize; i++){
				        	//readNum = fread(&bitData,sizeof(unsigned int),1,fp_in);
							readNum = tmp_fread(&bitData, sizeof(unsigned int), 1, &pDecObj->common);
        					if( readNum != 1 )	break;
		        			pDecObj->common.chbuf[i] = (unsigned char)bitData;
				        }	
        				length = i;		
                    }
					if (length < bitBufSize){
						eof_reached = TRUE;
						break;
					}
				}
				synthesis(pDecObj->common.melp_par, speech_out,&pDecObj->common);
				//writebl(speech_out, fp_out, pDecObj->common.frameSize);
				tmp_writebl(speech_out, &pDecObj->common, pDecObj->common.frameSize);
			}
		}
		pDecObj->common.frame_count ++;
	}

errLabel:

#if  0
	fclose(fp_in);
	fclose(fp_out);
#endif 
	tmp_fclose_r(&pDecObj->common);
	tmp_fclose_w(&pDecObj->common);

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
static void		parseCommandLine(int argc, char *argv[],DecAMelp*pDecObj)
{
	BOOLEAN		error_flag = FALSE;

	if (argc != 3)
		error_flag = TRUE;

	/* Setting default values. */
	pDecObj->common.mode = SYNTHESIS;
	pDecObj->common.rate = RATE2400;
    pDecObj->common.chwordsize = 8;         // this is for packed bitstream
	pDecObj->common.in_name[0] = '\0';
	pDecObj->common.out_name[0] = '\0';

	if( argc == 3 ){
		strcpy(pDecObj->common.in_name, argv[1]);
		strcpy(pDecObj->common.out_name, argv[2]);
	}

    if ((pDecObj->common.in_name[0] == '\0') || (pDecObj->common.out_name[0] == '\0'))
		error_flag = TRUE;

	if (error_flag){
		printHelpMessage(argv);
		exit(1);
	}

	fprintf(stderr, "\n\n\t%s %s, %s\n\n", PROGRAM_NAME, PROGRAM_VERSION,
			PROGRAM_DATE);
	switch (pDecObj->common.mode){
	case ANA_SYN:
	case ANALYSIS:
	case SYNTHESIS:
		if (pDecObj->common.rate == RATE2400)
			fprintf(stderr, " ---- 2.4kbps mode.\n");
		else
			fprintf(stderr, " ---- 1.2kbps mode.\n");
		break;
	}
	switch (pDecObj->common.mode){
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

	fprintf(stderr, " ---- input from %s.\n", pDecObj->common.in_name);
	fprintf(stderr, " ---- output to %s.\n", pDecObj->common.out_name);
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
