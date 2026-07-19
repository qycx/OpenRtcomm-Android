
package com.qycx.anc;


public class hgCommProc {



    public class Consts {


//
        public static short CONST_hgCmd_ulAnHgData			=					3021;		//	上传数据到hg. 从file通道传
        public static short CONST_hgCmd_dlAnHgData			=					3022;		//	从hg下载数据到cli. 从file通道传


        //
        public static short CONST_hgCmd_requestAFile		=					3023;		//  从hg求取一个文件，从file通道传


        //
        public static short CONST_hgCmd_xt			=							3050;		//  心跳


        //
        public static short CONST_hgType_curConf 		=				        206;
        public static short CONST_hgType_terminal_type	=		                207;
        public static short CONST_hgType_bdaemonrestart =                        208;
        public static short CONST_hgType_task_type  =                            209;
        public static short CONST_hgType_share_device_grp_index  =               210;
        public static short CONST_hgType_grp_usIndex	 =						211;

        //* TransferLocData → 融合中心（hgCommProc.h）
        public static short CONST_hgType_grpIdInfo      =                       80;
        public static short CONST_hgType_tldm_index     =                       230;
        public static short CONST_hgType_msgrIdInfo     =                       231;
        public static short CONST_hgType_locStr         =                       232;

        //* 融合中心嵌套原始 hg 载荷（hgCommProc.h: CONST_hgType_rawData）
        public static short CONST_hgType_rawData        =                       240;
        //* 融合中心广播时的外层包装类型（实测 obj=250）
        public static short CONST_hgType_anHgPayload    =                       250;

        //* 与 C++ respBuf[256*64] 一致
        public static int MAX_hg_cliDataLen             =                       256 * 64;

    }


}
