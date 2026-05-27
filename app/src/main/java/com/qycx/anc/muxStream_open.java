package com.qycx.anc;

public class muxStream_open {

    public static class Consts {

        //
        public static short  CONST_muxStreamType_null	 =								0;
        public static short  CONST_muxStreamType_conf		=							1;
        public static short  CONST_muxStreamType_screen  =							2;
        public static short  CONST_muxTreamType_map	=								3;

        //
        public static int MAX_muxStreams			=								10;								//

    }

    //
    //
    public static class  MuxStreamOutputCfg{


        //  音频的采样率. 缺省为16000
        //  音频编码
        public static class MuxStreamOutputCfg_a{
            //
            //
            public byte										ucCompressors;					//
            public short										wFormatTag;
        };

        //
        public MuxStreamOutputCfg_a a=new MuxStreamOutputCfg_a();

        //  视频分辨率.
        //  编码
        public static class MuxStreamOutputCfg_v {
            //
            public short										usW;
            public short										usH;
            //
            public byte										ucCompressors;					//
            public int													iFourcc;
        };
        public MuxStreamOutputCfg_v v=new MuxStreamOutputCfg_v();


    };

    //
    public static class MuxStreamCfg  {
        //
        public short        											usIndex;

        //
        public short													usType;

        //
        public MuxStreamOutputCfg										output;

        //
        public MuxStreamCfg()
        {
            output=new MuxStreamOutputCfg();
        }

    }		 ;


    //
    //  集中的策略定义
    public static class MuxStreamsCfg {

        //
        public short											usCnt;
        public MuxStreamCfg							[]				mems;

        //
        public short											default_usIndex;				//  缺省使用的uxIndex

        //
        //void* tmp_pInsternal;
        public int		tmp_usCnt_mems;

        //
        public MuxStreamsCfg()
        {
            mems=new MuxStreamCfg[Consts.MAX_muxStreams];
            //
            int i;
            for  (  i=0;i<mems.length;i++) {
                mems[i]=new MuxStreamCfg();
            }

        }

        //
        public void memset0()
        {
            this.usCnt=0;
            this.default_usIndex=0;
            //
            this.tmp_usCnt_mems=0;
        }

    }		 ;






}
