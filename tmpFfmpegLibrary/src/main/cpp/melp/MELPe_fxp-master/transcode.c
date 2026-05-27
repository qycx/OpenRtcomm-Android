/* ================================================================== */
/*                                                                    */ 
/*    Microsoft Speech coder     ANSI-C Source Code                   */
/*    SC1200 1200 bps speech coder                                    */
/*    Fixed Point Implementation      Version 7.0                     */
/*    Copyright (C) 2000, Microsoft Corp.                             */
/*    All rights reserved.                                            */
/*                                                                    */ 
/* ================================================================== */

/* ===================================== */
/* transcode.c: Conversion				 */
/* ===================================== */

/*	compiler include files	*/
#include "sc1200.h"
#include "mathhalf.h"
#include "mat_lib.h"
#include "math_lib.h"
#include "constant.h"
#include "global.h"
#include "dsp_sub.h"
#include "qnt12_cb.h"
#include "qnt12.h"
#include "msvq_cb.h"
#include "fsvq_cb.h"
#include "melp_sub.h"
#include "dsp_sub.h"
#include "coeff.h"
#include "macro.h"
#include "transcode.h"
#include "lpc_lib.h"
#include "fs_lib.h"
#include "cprv.h"
#include "vq_lib.h"

//
#include	"myMelpPublic.h"


/* compiler constants */
#define SIG_LENGTH			(LPF_ORD + PITCH_FR) /* 327 */
#define BUFSIZE24			7
#define X025_Q15			8192                          /* 0.25 * (1 << 15) */

// Variables
static struct melp_param	prev_par;
static int16_t top_lpc[LPC_ORD];


void transcode_down(TransMelp*pTransObj)
{
	register int16_t	i;
	int16_t	num_frames;


	num_frames = NF;

	/* Read and decode channel input buffer. */
	melp_chn_read(&quant_par, &pTransObj->melp_par[0], &prev_par	, &pTransObj->chbuf[0*BUFSIZE24],pTransObj);
	melp_chn_read(&quant_par, &pTransObj->melp_par[1], &pTransObj->melp_par[0], &pTransObj->chbuf[1*BUFSIZE24],pTransObj);
	melp_chn_read(&quant_par, &pTransObj->melp_par[2], &pTransObj->melp_par[1], &pTransObj->chbuf[2*BUFSIZE24],pTransObj);
	prev_par = pTransObj->melp_par[2];
	
	/* ---- New routine to refine the parameters for block ---- */
	sc_ana(pTransObj->melp_par,pTransObj);
	/* ======== Quantization ======== */
	lsf_vq(pTransObj->melp_par);
	pitch_vq(pTransObj->melp_par);
	gain_vq(pTransObj->melp_par);

	for (i = 0; i < NF; i++)
		quant_u(&pTransObj->melp_par[i].jitter, &(quant_par.jit_index[i]), 0,
					MAX_JITTER_Q15, 2, ONE_Q15, 1, 7);

	quant_bp(pTransObj->melp_par, num_frames);

	quant_jitter(pTransObj->melp_par);

	quant_fsmag(pTransObj->melp_par);

	for (i = 0; i < num_frames; i++)
		quant_par.uv_flag[i] = pTransObj->melp_par[i].uv_flag;

	/* Write channel bitstream */
	low_rate_chn_write(&quant_par,pTransObj);
}


void transcode_up(TransMelp*pTransObj)
{
	register int16_t	frame;
	int16_t	lpc[LPC_ORD + 1], weights[LPC_ORD];


	/* Read and decode channel input buffer. */
	low_rate_chn_read(&quant_par, &pTransObj->melp_par[0], &prev_par,pTransObj);
	prev_par = pTransObj->melp_par[2];

	/* ======== Quantization ======== */

	for (frame=0; frame<NF; frame++)
	{
		
		lpc[0] = ONE_Q12;

		/* Quantize LSF's with MSVQ */
		v_equ(&(lpc[1]), top_lpc, LPC_ORD);
		vq_lspw(weights, pTransObj->melp_par[frame].lsf, &(lpc[1]), LPC_ORD);

		/*	msvq_enc(par->lsf, weights, par->lsf, vq_par); */
		vq_ms4(msvq_cb, pTransObj->melp_par[frame].lsf, msvq_cb_mean, msvq_levels, MSVQ_M, 4,
			   LPC_ORD, weights, pTransObj->melp_par[frame].lsf, quant_par.msvq_index, MSVQ_MAXCNT);

		/* Force minimum LSF bandwidth (separation) */
		lpc_clamp(pTransObj->melp_par[frame].lsf, BWMIN_Q15, LPC_ORD);

		/* Quantize logarithmic pitch period */
		/* Reserve all zero code for completely unvoiced */
		pTransObj->melp_par[frame].pitch = log10_fxp(pTransObj->melp_par[frame].pitch, 7);           /* par->pitch in Q12 */
		quant_u(&pTransObj->melp_par[frame].pitch, &(quant_par.pitch_index), PIT_QLO_Q12,
				PIT_QUP_Q12, PIT_QLEV_M1, PIT_QLEV_M1_Q8, 1, 7);
		/* convert pitch back to linear in Q7 */
		pTransObj->melp_par[frame].pitch = pow10_fxp(pTransObj->melp_par[frame].pitch, 7);

		/* Quantize gain terms with uniform log quantizer   */
		q_gain(pTransObj->melp_par[frame].gain, quant_par.gain_index, GN_QLO_Q8, GN_QUP_Q8,
			   GN_QLEV_M1, GN_QLEV_M1_Q10, 0, 5);

		/*	quant_u(&par->jitter, &par->jit_index, 0, MAX_JITTER_Q15, 2);     */
		if (pTransObj->melp_par[frame].jitter < shr(MAX_JITTER_Q15, 1)){
			pTransObj->melp_par[frame].jitter = 0;
			quant_par.jit_index[0] = 0;
		} else {
			pTransObj->melp_par[frame].jitter = MAX_JITTER_Q15;
			quant_par.jit_index[0] = 1;
		}

		/* Quantize bandpass voicing */
		pTransObj->melp_par[frame].uv_flag = q_bpvc(pTransObj->melp_par[frame].bpvc, &(quant_par.bpvc_index[0]), 
													NUM_BANDS);

		/* quantize Fourier coefficients */
		/* pre-weight vector, then use Euclidean distance */
		window_Q(pTransObj->melp_par[frame].fs_mag, w_fs, pTransObj->melp_par[frame].fs_mag, NUM_HARM, 14);

		/*	fsvq_enc(par->fs_mag, par->fs_mag, fs_vq_par); */
		/* Later it is found that we do not need the structured variable      */
		/* fs_vq_par at all.  References to its individual fields can be      */
		/* replaced directly with constants or other variables.               */

		vq_enc(fsvq_cb, pTransObj->melp_par[frame].fs_mag, FS_LEVELS, NUM_HARM, pTransObj->melp_par[frame].fs_mag,
			   &(quant_par.fsvq_index));

		quant_par.uv_flag[0] = pTransObj->melp_par[frame].uv_flag;

		/* Write channel bitstream */
		melp_chn_write(&quant_par, &pTransObj->chbuf[frame*BUFSIZE24],pTransObj);
	}
}

