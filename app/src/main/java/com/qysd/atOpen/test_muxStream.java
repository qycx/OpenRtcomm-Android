package com.qysd.atOpen;

import static com.qycx.anc.muxStream_open.Consts.CONST_muxStreamType_conf;
import static com.qycx.anc.muxStream_open.Consts.CONST_muxStreamType_screen;
import static com.qycx.base.qyCommProc_open.Consts.CONST_fourcc_HEVC;
import static com.qycx.base.qyCommProc_open.Consts.WAVE_FORMAT_MPEGLAYER3;
import static com.qycx.base.qyCusResPublic.Consts.CONST_policyAvLevel_1080p;
import static com.qycx.base.qyCusResPublic.Consts.CONST_policyAvLevel_2160p;
import static com.qycx.base.qyCusResPublic.Consts.CONST_policyAvLevel_240p;
import static com.qycx.base.qyCusResPublic.Consts.CONST_policyAvLevel_480p;
import static com.qycx.base.qyCusResPublic.Consts.CONST_policyAvLevel_720p;

import com.qycx.anc.muxStream_open;

public class test_muxStream {


    //
    public static int  testInit_getMuxStreamsCfg(  int avLevel, muxStream_open.MuxStreamsCfg pCfg)
    {
        if (null==pCfg)  return  0;

        //memset(pCfg, 0, sizeof(pCfg[0]));
        pCfg.memset0();

        //
        short  index;

        //
        muxStream_open.MuxStreamCfg pMem;
        muxStream_open.MuxStreamOutputCfg pOutput;

        //
        index = 0;
        pMem = pCfg.mems[index];
        pOutput = pMem.output;

        //
        pMem.usIndex = index;
        pMem.usType = (short)CONST_muxStreamType_conf;

        //
        pOutput.a.wFormatTag = WAVE_FORMAT_MPEGLAYER3;
        //
        pOutput.v.usW = 1280;
        pOutput.v.usH = 720;
        //
        switch (avLevel)  {
            case  CONST_policyAvLevel_2160p:
                pOutput.v.usW =  1920  *  2;
                pOutput.v.usH = 1080 * 2;
                break;
            case  CONST_policyAvLevel_1080p:
                pOutput.v.usW = 1920;
                pOutput.v.usH = 1080;
                break;
            case  CONST_policyAvLevel_720p:
                pOutput.v.usW = 1280;
                pOutput.v.usH = 720;
                break;
            case  CONST_policyAvLevel_480p:
                pOutput.v.usW = 848;
                pOutput.v.usH = 480;
                break;
            case  CONST_policyAvLevel_240p:
                pOutput.v.usW = 424;
                pOutput.v.usH = 240;
                break;
            default:
                break;

        }
        //
        pOutput.v.iFourcc = CONST_fourcc_HEVC;


        //
        short usCnt = (short)(index + 1);
        if (usCnt > pCfg.mems.length) {
            return -1;
        }
        pCfg.usCnt=usCnt;

        //
        pCfg.default_usIndex = 0;



        //
        return  0;
    }



}
