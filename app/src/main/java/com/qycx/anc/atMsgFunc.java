package com.qycx.anc;

import com.qycx.base.qyMsgFunc;

public class atMsgFunc {

    public static class AT_MSG_HEAD_n extends qyMsgFunc.AT_MSG_HEAD_obj {
        public byte ucFlg;
        public short usCode;
        public int uiLen;
        public byte []l3_elapse_buf = new byte[3];
        public int uiTranNo;
        public short usSeqNo;

        //
        public long  dbg_i64StartTime;      //  仅为了调试用
    }


}
