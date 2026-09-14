
package com.qycx.base;

import static com.qycx.anc.anProtocol.AT_MSG_HEAD14_hton;
import static com.qycx.anc.anProtocol.AT_MSG_HEAD22_hton;
import static com.qycx.anc.anProtocol.INT24_MAX;
import static com.qycx.anc.anProtocol.INT24_MIN;
import static com.qycx.anc.anProtocol.atntohl3;
import static com.qycx.anc.ancCommProc_open.Consts.CONST_atCommFlg_head14;
import static com.qycx.base.commonSock.qyFuncs.qyRecvEx;
import static com.qycx.base.commonSock_android.qyFuncs_Droid.qySend_android;
import static com.qycx.base.qyComm.Consts.CONST_qyRc_user;
import static com.qycx.base.qyComm.Consts.bufSize_qyCommServiceData;
import static com.qycx.base.qyComm.qyFuncs.isRcOk;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgHead14;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgMoreData;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgResp;
import static com.qycx.base.qyCommFunc.Consts.CONST_maxReqDataLen_enc_is;
import static com.qycx.base.qyDefs.Consts.CONST_qyMaxVerLen;
import static com.qycx.base.qyEncFunc.Consts.CONST_qyEncType_qwm;
import static com.qycx.base.qyEncFunc.Consts.CONST_qyEncType_rsa1;

import com.qycx.base.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class qyMsgFunc {

    //
    public static class AT_MSG_HEAD_obj  {

    };


    //
    public static class AT_MSG_HEAD_h extends AT_MSG_HEAD_obj {
        public byte ucFlg;
        public byte reserved;
        //
        public short usCode;
        //
        public int uiLen;
        public long i64StartTime;
        public int uiTranNo;
        public short usSeqNo;
        public byte[] reserved1 = new byte[2];

        //
        public int l3_elapse;
        public long  dbg_i64StartTime;      //  仅为了调试用
    }


    //
// 在usCode<=255, uiLen用3個字節夠了，用14.如果沒有moreData. 則沒有seqNo
// 對usSeqNo, 最高位改成結束位。所以，seqNo為8  +  7個bits.
//
    public static class AT_MSG_HEAD14_n extends AT_MSG_HEAD_obj {

        //
        public byte []						ucFlg_buf = new byte[1];
        public byte []						ucCode_buf = new byte[1];				//
        public byte []                      uiLen3_buf  =  new byte[3];				//
        public byte []						l3_elapse_buf= new byte[3];
        public byte []						uiTranNo_buf = new byte[4];
        public byte []                      usSeqNo_buf = new byte[2];

        //
        public ByteBuffer head_buffer = ByteBuffer.allocate(14);
        //
        public int nBytes;

    }		 ;


    // 如果不用14, 就用22. 如果沒有moreData. 則沒有seqNo
// 在相對時間超過3個字節后（大約200天)，要采用22，這裏的i64StartTime是絕對時間。如果沒有moreData. 則沒有seqNo
//
    public static class AT_MSG_HEAD22_n{

        //
        public byte []					ucFlg_buf = new byte[1];				//  2007/04/22
        public byte []					reserved = new byte[1];				//
        public byte []                  usCode_buf = new byte[2];				//
        public byte []  				uiLen_buf  =  new byte[4];				//
        public byte []  				i64StartTime_buf = new byte[8];		//  絕對時間
        public byte []                  uiTranNo_buf = new byte[4];			//  2007/05/02/
        public byte []  				usSeqNo_buf  = new byte[2];				//  2007/05/02

        //
        public ByteBuffer head_buffer = ByteBuffer.allocate(22);
        //
        public int nBytes;


    }		 ;



    //
    public static class qyFuncs {


        //
        private static void parseHead14(ByteBuffer buf,
                                        qyMsgFunc.AT_MSG_HEAD_h h,
                                        long startTimeBase) {

            buf.order(ByteOrder.BIG_ENDIAN);

            h.ucFlg = buf.get();

            h.usCode = (short) (buf.get() & 0xff);

            byte[] tmp = new byte[3];

            buf.get(tmp);
            h.uiLen = atntohl3(tmp);

            buf.get(tmp);
            int l3 = atntohl3(tmp);

            h.l3_elapse = l3;
            h.i64StartTime = startTimeBase + l3;

            h.uiTranNo = buf.getInt();

            if (isUcFlgMoreData(h.ucFlg)) {
                h.usSeqNo = buf.getShort();
            } else {
                h.usSeqNo = 0;
            }
        }

        private static void parseHead22(ByteBuffer buf,
                                        qyMsgFunc.AT_MSG_HEAD_h h) {

            buf.order(ByteOrder.BIG_ENDIAN);

            h.ucFlg = buf.get();

            buf.get();          // reserved

            h.usCode = buf.getShort();

            h.uiLen = buf.getInt();

            h.i64StartTime = buf.getLong();

            h.uiTranNo = buf.getInt();

            if (isUcFlgMoreData(h.ucFlg)) {
                h.usSeqNo = buf.getShort();
            } else {
                h.usSeqNo = 0;
            }
        }


        public static ByteBuffer qyRecvMsg(qyComm.QY_COMM_SESSION pSession, commonSock.QY_SOCK pSock, commonSock.SOCK_TIMEOUT pTo,qyMsgFunc.AT_MSG_HEAD_h pMsgHead) {
            int iErr = -1;
            byte[] encBuf = null;
            int encLen = CONST_maxReqDataLen_enc_is;

            //
            int recv_size;

            //
            int MAX_msgHead_n_len = 22;
            //
            int headLen = MAX_msgHead_n_len;
            ByteBuffer headBuf = ByteBuffer.allocate(headLen);
            headBuf.order(ByteOrder.BIG_ENDIAN);

            //
            recv_size = qyRecvEx(pSock, headBuf, 0,1, pTo.iRead);
            if (recv_size != 1) {
                return null;
            }
            byte ucFlg = headBuf.get(0);

            //
            headLen = isUcFlgHead14(ucFlg) ? 14 : 22;

            if (!isUcFlgMoreData(ucFlg)) {
                headLen -= 2;
            }

            //
            int flgLen;
            flgLen = 1;


            headBuf.position(flgLen);

            //
            recv_size = qyRecvEx(pSock, headBuf, flgLen,headLen - flgLen, pTo.iRead);
            if (recv_size != headLen - flgLen) {
                Log.e("qy", "wyRecvMsg failed: recive_size:" + recv_size);
                return null;
            }
            headBuf.flip();


            // 解析 Header
            if (isUcFlgHead14(ucFlg)) {
                parseHead14(headBuf,
                        pMsgHead,
                        pSession.service.i64StartTime_base);
            } else {
                parseHead22(headBuf,
                        pMsgHead);
            }

            //
            if (false) {
                Log.d("qy", "qyRecvMsg: tn " + pMsgHead.uiTranNo + " msg.uiLen " + pMsgHead.uiLen);
            }
            if (false) {
                String str;
                str = "qyRecvMsg: l3 " +  "(" + pMsgHead.l3_elapse + ")"
                        + " base " + pSession.service.i64StartTime_base + ": st " + pMsgHead.i64StartTime + " tn " + pMsgHead.uiTranNo;
                showInfo_open.qyFuncs.showInfo_open(0, null, str);

            }

            //
            //ByteBuffer buffer;
            if (pSession.service.encType == CONST_qyEncType_qwm) {
                Log.d("qy", "qyRecvMsg failed, enc_qwm not supported");
                return null;
            }

            //校验数据长度的合法性
            if (pMsgHead.uiLen <= 0 || pMsgHead.uiLen > (qnmCommProc_mis.Consts.bufSize_imLStream + 1024)) {
                Log.e("qy", "msg.uiLen = " + pMsgHead.uiLen + "  error length");
                return null;
            }
            ByteBuffer data = ByteBuffer.allocate(pMsgHead.uiLen);
            //
            int recv = qyRecvEx(pSock, data, 0, pMsgHead.uiLen, pTo.iRead);
            if (recv != pMsgHead.uiLen) {
                Log.e("qy", "qyRecvEx failed");
                return null;
            }

            data.flip();


            if (isUcFlgResp(pMsgHead.ucFlg)) pSession.usLastRespCode_i = pMsgHead.usCode;
            else pSession.usLastReqCode_i = pMsgHead.usCode;

            //
            pSession.lastRecv_headLen  =  headLen;

            //
            return data;
        }


        //
        public static int qySendMsg(qyComm.QY_COMM_SESSION pSession, commonSock.QY_SOCK pSock, commonSock.SOCK_TIMEOUT pTo, byte ucFlg, int cmdCode, long lStartTime, int uiTranNo, int usSeqNo, byte[] data, int offset,  int dataLen) {
            int iErr = -1;

            //
            ByteBuffer head_buffer  = null;
            int len  =  0;

            //
            ucFlg &= ~CONST_atCommFlg_head14;

            //
            do {

                AT_MSG_HEAD_h tmpHead = new AT_MSG_HEAD_h();
                //byte[] encBuf = null;
                //int encLen = CONST_maxReqDataLen_enc_is;
                tmpHead.usCode = (short) cmdCode;
                tmpHead.ucFlg = ucFlg;
                if (null == data) {
                    dataLen = 0;
                }
                tmpHead.uiLen = dataLen;

                //
                //
                tmpHead.i64StartTime = lStartTime;
                tmpHead.uiTranNo = uiTranNo;
                tmpHead.usSeqNo = (short) usSeqNo;
                //
                int nBytes;
                nBytes = 0;
                //
                boolean bHead14;
                bHead14 = false;
                //
                if (tmpHead.usCode < 255
                        && tmpHead.uiLen < INT24_MAX) {
                    long l3 = lStartTime - pSession.service.i64StartTime_base;
                    if (l3 <= INT24_MAX && l3 >= INT24_MIN) {
                        bHead14 = true;
                    }
                }
                //
                if (false) {
                    bHead14 = false;
                    Log.d("at", "Test: bHead14 set to false. l276");
                }
                //
                if (bHead14) {
                    AT_MSG_HEAD14_n tmpHead14_n = new AT_MSG_HEAD14_n();
                    if (0 != AT_MSG_HEAD14_hton(pSession.service.i64StartTime_base, tmpHead, tmpHead14_n)) {
                        break;
                    }
                    //
                    head_buffer=tmpHead14_n.head_buffer;
                    //
                    head_buffer.put(tmpHead14_n.ucFlg_buf);
                    head_buffer.put(tmpHead14_n.ucCode_buf);
                    //
                    head_buffer.put(tmpHead14_n.uiLen3_buf);
                    //
                    head_buffer.put(tmpHead14_n.l3_elapse_buf);
                    head_buffer.put(tmpHead14_n.uiTranNo_buf);
                    //
                    if  ( isUcFlgMoreData(tmpHead14_n.ucFlg_buf[0])) {
                        head_buffer.put(tmpHead14_n.usSeqNo_buf);
                    }
                    //
                    head_buffer.flip();
                    len=head_buffer.limit();

                    //
                    pSession.lastSnd_headLen  =  tmpHead14_n.nBytes;

                } else {  //
                    AT_MSG_HEAD22_n tmpHead22_n = new AT_MSG_HEAD22_n();
                    if (0 != AT_MSG_HEAD22_hton(pSession.service.i64StartTime_base, tmpHead, tmpHead22_n)) {
                        break;
                    }

                    //
                    head_buffer = tmpHead22_n.head_buffer;
                    //
                    head_buffer.put(tmpHead22_n.ucFlg_buf);
                    head_buffer.put(tmpHead22_n.reserved);
                    head_buffer.put(tmpHead22_n.usCode_buf);
                    //
                    head_buffer.put(tmpHead22_n.uiLen_buf);
                    //
                    head_buffer.put(tmpHead22_n.i64StartTime_buf);
                    head_buffer.put(tmpHead22_n.uiTranNo_buf);
                    //
                    if  (  isUcFlgMoreData(tmpHead22_n.ucFlg_buf[0])) {
                        head_buffer.put(tmpHead22_n.usSeqNo_buf);
                    }
                    //
                    head_buffer.flip();
                    len = head_buffer.limit();

                    //
                    pSession.lastSnd_headLen=tmpHead22_n.nBytes;

                }
                if  (  head_buffer==null)  {
                    break;
                }
                //
                //if (guiShare.GuiShare.pf_qySend.call(pSock, head_buffer.array(), 0, len, pTo.iWrite) != len)
                if (qySend_android.call(pSock, head_buffer.array(), 0, len, pTo.iWrite) != len)
                {
                    return iErr;
                }

                //
                if (dataLen > 0) {
                    try {
                        //ByteBuffer buffer = ByteBuffer.wrap(data, 0, dataLen);
                        if (qySend_android.call(pSock, data, offset, dataLen, pTo.iWrite) != dataLen) {
                            return iErr;
                        }
                    } catch (Exception e) {
                        Log.e(e.getLocalizedMessage());
                        return iErr;
                    }
                } else {
                    Log.e("data size must > 0 !");
                    return -1;
                }


                //
                iErr = 0;

            } while(false);

            //
            return iErr;
        }


        public static int qyTellService_msg(int serviceId, String version, qyEncFunc.QY_ENC_CTX pCommEncCtx, long i64StartTime_base,  int usCmd, long tStartTime, int uiTranNo, ByteBuffer buf, commonSock.QY_SOCK pSock, commonSock.SOCK_TIMEOUT pTo, qyComm.QY_COMM_SESSION pSession, String base64PublicKey) {
            //
            //Log.d("qyTellService_msg: ");
            //
            int iErr = -1;
            int len;

            if (null == buf || buf.remaining() <= 0) return -1;
            qyComm.QY_COMM_SERVICE service = new qyComm.QY_COMM_SERVICE();

            service.serviceId = serviceId;
            service.version = version.getBytes();
            service.encType = pCommEncCtx.common.type;

            switch (service.encType) {
                case CONST_qyEncType_rsa1: {
                    qmCrypt.ENC_CTX_session pEncCtx = (qmCrypt.ENC_CTX_session) pCommEncCtx;

                    if (null == pEncCtx) return iErr;

                    buf.flip();
                    byte[] tmp_data = new byte[buf.limit()];
                    buf.get(tmp_data);
                    byte[] encData = RSAEncrypt.encrypt_byte(tmp_data, base64PublicKey);

                    service.data = encData;
                    len = encData.length;
                }
                break;
                default:
                    len = Math.min(buf.position(), bufSize_qyCommServiceData);
                    System.arraycopy(buf.array(), 0, service.data, 0, len);
                    break;
            }

            //
            service.ucCommServiceType  =  0;
            service.i64StartTime_base  =  i64StartTime_base;

            //
            int service_len  =  Integer.BYTES * 2 + CONST_qyMaxVerLen + len;
            service_len  =  Integer.BYTES  +  CONST_qyMaxVerLen  +  Integer.BYTES  +  Byte.BYTES  +  Long.BYTES  +  len;

            //
            ByteBuffer service_buf = ByteBuffer.allocate(service_len);
            service_buf.putInt(service.serviceId);
            service_buf.put(service.version);
            service_buf.putInt(service.encType);
            //
            service_buf.put(service.ucCommServiceType);
            service_buf.putLong(service.i64StartTime_base);
            //
            service_buf.put(service.data, 0, len);

            // 将service赋入pSession中
            service.serviceId = serviceId;
            service.encType = pCommEncCtx.common.type;
            pSession.service = service;
            pSession.commEncCtx = pCommEncCtx;

            //
            if  (  false  ) {
                Log.d(Consts.IO_TAG, "qyTellService_msg");
            }

            //
            byte[] data = service_buf.array();
            if (qySendMsg(pSession, pSock, pTo, (byte) 0, usCmd, tStartTime, uiTranNo, 0, data, 0,  data.length) != 0)
                return iErr;

            //
            iErr = 0;
            return iErr;
        }


        //关闭会话
        public static void qnmCloseSession_msg(qyComm.QY_COMM_SESSION pSession, commonSock.QY_SOCK pSock, commonSock.SOCK_TIMEOUT pTo, String hint) {
            //
            Log.d("anc", hint  +  " qnmCloseSession_msg called");
            //QY_MSG_HEAD tmpResp = new QY_MSG_HEAD();

            if (guiShare.GuiShare.pf_bQySockOpen.call(pSock)) {
                if (isRcOk(pSession.usLastRespCode_i) || pSession.usLastRespCode_i == CONST_qyRc_user) {
                    /*
                    if (0 == qySendMsg(pSession, pSock, pTo, (byte) 0, CONST_qyCmd_end, 0, 0, 0, null, 0)) {
                        qyRecvMsg(pSession, pSock, pTo, tmpResp);
                    }
                     */
                }
                //
                pSock.buf_r = null;
                pSock.buf_w = null;
                //
                guiShare.GuiShare.pf_qyDisconnect.call(pSock);
            }
            return;
        }
    }
}
