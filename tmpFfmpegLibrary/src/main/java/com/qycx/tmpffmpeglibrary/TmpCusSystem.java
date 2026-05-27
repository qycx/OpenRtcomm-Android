package com.qycx.tmpffmpeglibrary;

//
public class TmpCusSystem {

    //
    public static class  CfgInfo_100k  {
        //
        public int use100k_enc_sleepInMs;
        public int use100k_v_w;
        public int use100k_v_h;
        public int use100k_v_biterate;
        public int use100k_v_perSnd_sleepInMs;

        //
        public int use100k_v_frameRate;
        public int use100k_v_I_FRAME_INTERVAL;

    }

    //
    //  b240p. 即采用320x240. 如false, 采用640x480
    //  fps, 视频输出的帧速。如0，则采用15
    //  v_bitrate, 视频码流，如0，则50000
    public static int doCus100k(boolean b240p,  int fps,  int v_bitrate, CfgInfo_100k p)
    {
        CfgInfo_100k pProcInfo  =  p;

        //
        //
        pProcInfo.use100k_enc_sleepInMs  =  4000;//2000;
        //
        pProcInfo.use100k_v_biterate  =  10000;    //10000;
        //
        pProcInfo.use100k_v_frameRate  =  1;

        pProcInfo.use100k_v_perSnd_sleepInMs  =  80;

        //
        pProcInfo.use100k_v_w = 480;    //480;
        pProcInfo.use100k_v_h = 640;    //640;
        if  (  b240p)  {
            pProcInfo.use100k_v_w  =  240;    //480;
            pProcInfo.use100k_v_h  =  320;    //640;
        }
        //
        pProcInfo.use100k_enc_sleepInMs  =  1000;//2000;
        //
        pProcInfo.use100k_v_biterate  =  25000;    //10000;
        //
        pProcInfo.use100k_v_perSnd_sleepInMs=1;

        //
        pProcInfo.use100k_enc_sleepInMs  =  333;//2000;

        //
        pProcInfo.use100k_v_frameRate  =  3;
        pProcInfo.use100k_v_I_FRAME_INTERVAL  =  10;
        //
        pProcInfo.use100k_v_frameRate  =  15;//20;//5;
        if  (  fps!=0)  {
            pProcInfo.use100k_v_frameRate=Math.min(pProcInfo.use100k_v_frameRate,fps);
        }
        //
        pProcInfo.use100k_v_I_FRAME_INTERVAL  =  3  *  pProcInfo.use100k_v_frameRate;

        //
        pProcInfo.use100k_enc_sleepInMs  =  (int)(1000.  /   pProcInfo.use100k_v_frameRate);//2000;

        //
        pProcInfo.use100k_v_biterate  =  35000;    //10000;
        pProcInfo.use100k_v_biterate  =  50000;    //10000;
        pProcInfo.use100k_v_biterate  =  40000;    //10000;
        //
        if  ( v_bitrate!=0 )  {
            pProcInfo.use100k_v_biterate=Math.min(pProcInfo.use100k_v_biterate,v_bitrate);
        }

        //
        if  (  false  ) {
            pProcInfo.use100k_v_w = 144;
            pProcInfo.use100k_v_h = 176;
        }

        //
        return  0;
    }

}


