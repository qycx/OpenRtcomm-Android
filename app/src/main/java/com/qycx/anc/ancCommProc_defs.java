package com.qycx.anc;

public class ancCommProc_defs {

    public static class Consts {

        /*
                public final static short CONST_ancCfgId_base_common = 1000;


         */

        //
        public final static short CONST_anCfgId_base_common = 4100;

        //
        public final static int CONST_anCommType_base  =  4000;


        //  这里定义重要的cfgId < 4096
        public final static short CONST_ancCfgId_start = 1; //(CONST_qnmCfgId_base_common + 100);
        public final static short CONST_ancCfgId_mem = 2;   //(CONST_qnmCfgId_base_common + 101);               //  2007/08/05, 只用在小而快的数据包。一般的包的成员要使用CONST_imCommType_mem
        public final static short CONST_ancCfgId_messengerId_from = 3;  //(CONST_qnmCfgId_base_common + 2640);             //  2007/11/29
        public final static short CONST_ancCfgId_messengerId_to = 4;    //(CONST_qnmCfgId_base_common + 2641);               //  2007/11/29
        //
        public final static short CONST_ancCfgId_uiMsgRouteId = 5;      //(CONST_qnmCfgId_base_common + 2650);             //  2008/05/27
        public final static short CONST_ancCfgId_messengerId = 6;      //(CONST_qnmCfgId_base_common + 2650);             //  2008/05/27

        //

        //
        public final static short CONST_ancCfgId_uiTranNo_openAvDev  =  20;
        public final static short CONST_ancCfgId_usCnt              =  21;

        //
        public final static short CONST_ancCfgId_uiSampleTimeInMs  =  22;
        public final static short CONST_ancCfgId_uiPts  =  23;
        public final static short CONST_ancCfgId_uiLen  =  24;
        public final static short CONST_ancCfgId_rawData  =  25;
        public final static short CONST_ancCfgId_usElapseInMs_fromLastPkt  =  26;

        //
        public final static short CONST_ancCfgId_uiTranNo_openVideoDev  =  30;
        public final static short CONST_ancCfgId_ucbKeyFrame  =  31;

        //
        public final static short CONST_anCfgId_conf_ui64Id  =  40;             //  2026/09/28, 对应 c++ anCommProcDefs.h，与 CONST_ancCfgId_conf_ui64Id 同值

        //
        public final static short CONST_anCfgId_lfcs_tn  =  50;             //  2026/09/23, 对应 c++ anCommProc_defs.h
        public final static short CONST_anCfgId_mfcs_tn  =  51;
        public final static short CONST_anCfgId_hfcs_tn  =  52;
        public final static short CONST_anCfgId_hfcs_cliNetstats  =  53;    //
        public final static short CONST_anCfgId_hfcs_cliPktLoss  =  54;

        //








        //  这里定义普通的cfgId CONST_qncCfgId_base_common  +  n

        //  这里定义最重要的ancCommType < 256
        public final static int CONST_anCommType_msgRoute = 1;//(CONST_imCommType_base + 4);       //  2007/11/29
        public final static int CONST_anCommType_mem = 2;
        //
        public final static int CONST_anCommType_transferAudioData  =  4;  //
        public final static int CONST_anCommType_transferVideoData  =  5;  //

        public final static int CONST_anCommType_refreshRecentFriendsReq1  =  8;   //



        //  这里定义重要的ancCommType < 4000

        //  这里定义普通的ancCommType  CONST_ancCommType_base  + m



    }


    }
