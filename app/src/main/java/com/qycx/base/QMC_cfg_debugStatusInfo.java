package com.qycx.base;


//
public class QMC_cfg_debugStatusInfo {                                                           //  2009/02/27
    //
    public boolean ucbViewAudioStatus;
    public boolean ucbViewVideoStatus;                                           //  2009/04/16
    public boolean ucbViewIoStatus;                                          //  2009/04/16
    //
    public boolean ucbViewMgrStatus;                                         //  2017/10/11
    //
    public boolean ucbPrint_a_in;                                                //  2010/06/25
    public boolean ucbPrint_a_out;
    public boolean ucbPrint_v_in;
    public boolean ucbPrint_v_out;
    //
    public boolean ucbPrint_v_draw;                                          //  2010/09/12
    //
    public boolean ucbPrint_a_mt;                                                //  2013/03/21
    //
    public boolean ucbShowVideoCapStatus;                                        //  2012/11/11
    public boolean ucbShowAudioCapStatus;                                        //  2012/11/11
    //
    public boolean ucbShowPreDrawStatus;                                     //  2012/01/26
    public boolean ucbShowToDrawStatus;                                      //  2012/01/26
    //
    public boolean ucbShowRenderStatus;                                      //  2015/10/07
    //
    public boolean ucbShowPlayAudioStatus;                                       //  2012/11/11

    //
    public boolean ucb_chkInputPts;

    //																			//
    public boolean ucbShowPktInputInfo;                                      //  2012/03/16
    public boolean ucbShowEncVStatus;                                            //  2014/06/23
    //
    public boolean ucbShowDec_pipeStatus;                                        //  2016/04/13
    //
    public boolean ucbShowPreDecV_pts;                                           //  2015/06/18
    public boolean ucbShowPreDecAStatus;                                     //  2015/09/11
    public boolean ucbShowPreDecVStatus;                                     //  2014/06/23
    public boolean ucbShowPostDecAStatus;                                        //  2015/09/11
    public boolean ucbShowPostDecVStatus;                                        //  2014/06/23
    //
    public boolean ucbShowDec_sharedTexStatus;                                   //  2016/04/08
    //
    public boolean ucbPrint_rtt;                                             //  2014/11/26
    //
    public boolean ucbPrint_d3d;                                             //  2014/12/09
    //
    public boolean ucbShowDlgTalkStatus;                                     //  2012/05/25

    //
    public boolean ucbShow_onvif;                                                //  2016/07/31
    public boolean ucbPrint_ptz;                                             //  2015/09/19

    //
    public boolean ucbShowRtspCliControl;                                        //  2014/03/23

    //
    public boolean ucbPrint_rtsp;                                                //  2014/09/29

    //
    public boolean ucbDebugSubtitle;                                         //  2014/11/07
    public boolean ucbDebugIa;                                                   //  2014/11/30
    //
    public boolean ucbDebug_newMsgArrives;                                       //  2014/12/03
    public boolean ucbDebug_fillData_d3d;                                        //  2014/12/03

    //
    public boolean ucbViewFileStatus;


    //
    //#define		CONST_fn_dump_h264_enc		"dump_h264_enc.264"
    //#define CONST_fn_dump_h264_enc		"c:\\tttbbb\\test\\dump_h264_enc.264"
    public boolean ucb__Dump_h264_enc__;                                     //  2014/03/30

    //
//#define CONST_fn_dump_h264_dec	"dump_h264_dec.264"
    public boolean ucb__Dump_h264_dec__;                                     //  2014/03/30

    //#define CONST_fn_dump_pcm		"c:\\tttbbb\\test\\dump_pcm.pcm"
    public boolean ucb__Dump_pcm__;

    public boolean ucb__Dump_mp3_enc__ = false;
    public boolean ucb__Dump_mp3_dec__ = false;

    //
    public boolean ucb__Dump_deced_pcm__;

    //
    public boolean ucb_draw_talkers_frm;                                     //  2016/06/02
    public boolean ucb_draw_walls_frm;

    //
    public boolean ucb_notPrint_my_inStream;

    //
    public boolean bPrint_encObj_mediaCodec;

    //
    public boolean bNoRemove_err_missing;

    //
    public boolean b_print_test  =  false;


    //
    public boolean  bDbg_no_freeTaskInfo;

    //
    public boolean bDbg_v  = false;

    //
    public boolean bDbg_startToBreak;

    //
    public boolean bTest_noMapExt;
    public boolean bTest_noLostVideo;

    //
    public boolean bDbgDetail_pOutputQBuf2_local_a;
    public boolean bDbgDetail_pOutputQBuf2_local_v;

    //
    public boolean bDbgDetail_toShareAudio;

    //
    public boolean bDbgDetail_v_r;

    //
    public boolean bDbgDetail_keepAlive;

    //
    public boolean bTraceRt_encA;

}

;
