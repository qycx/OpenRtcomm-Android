package com.qycx.anc;

public class ancCommProc_open {

    public static class Consts {

        public final static byte CONST_atDataType_byte = 1;
        public final static byte CONST_atDataType_short = 2;
        public final static byte CONST_atDataType_l32 = 3;
        public final static byte CONST_atDataType_l64 = 4;
        public final static byte CONST_atDataType_str = 5;
        public final static byte CONST_atDataType_utf8Str = 6;
        public final static byte CONST_atDataType_lData = 7;

        public final static byte CONST_atDataType_wStr = 10;
        public final static byte CONST_atDataType_data = 11;


        // 最高有效位（Most Significant Bit, MSB）：这是指在二进制表示中权重最高的那位（即2^7 = 128）。它通常被视为字节的“第一个”bit。
        //  msb 空出来，提供最重要的包结构区分
        //
        public final static int CONST_atCommFlg_msb=(1<<7);
        public final static int CONST_atCommFlg_resp = (1 << 6);
        public final static int CONST_atCommFlg_moreData = (1 << 5);
        //
        public final static int CONST_atCommFlg_head14 = (1 << 4);                                  //  数据区的格式为MIS_MSG_TALKDATA，否则为IM_MSG_CONTENTU
        //
        public final static int CONST_atCommFlg_routeTalkData = (1 << 3);                                    //  数据区的格式为用MSG_ROUTE+IM_MSG_CONTENTU

        //
        public final static int CONST_endFlg_usSeqNo  =					(  1  <<  15  );


        //
        public final static int  MAX_12bits=(16  *  256  -  1);







    }


}
