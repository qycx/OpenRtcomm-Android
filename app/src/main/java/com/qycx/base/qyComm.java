

package com.qycx.base;

import static com.qycx.anc.ancCommProc_open.Consts.CONST_atCommFlg_head14;
import static com.qycx.anc.ancCommProc_open.Consts.CONST_atCommFlg_moreData;
import static com.qycx.anc.ancCommProc_open.Consts.CONST_atCommFlg_resp;
import static com.qycx.anc.ancCommProc_open.Consts.CONST_atCommFlg_routeTalkData;
//import static com.qycx.anc.ancCommProc_open.Consts.CONST_atCommFlg_talkData;
import static com.qycx.anc.ancCommProc_open.Consts.CONST_endFlg_usSeqNo;
import static com.qycx.base.qyComm.Consts.*;
import static com.qycx.base.qyDefs.Consts.CONST_qyMaxVerLen;
import static com.qycx.base.qyDefs.Consts.CONST_qyTimeLen;
import static com.qycx.base.qyFuncs.Consts.CONST_qyMaxIpLen;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class qyComm {


    public static class Consts {
        //  all service_ids
        //  #define		CONST_qyServiceId_rasMonoring			ASR_SERVICE_RAS_MONITORING
        public final static int CONST_qyServiceId_base = 10;                       // service_id的基数
        //  public final static int 		CONST_qyServiceId_imc					(  CONST_qyServiceId_base  +  1  )
        public final static int CONST_qyServiceId_netMc = (CONST_qyServiceId_base + 2);        //  2007/03/31
        public final static int CONST_qyServiceId_qwm = CONST_qyServiceId_netMc;
        //  public final static int 		CONST_qyServiceId_internetMgr			(  CONST_qyServiceId_base  +  110  )
        public final static int CONST_qyServiceId_is = (CONST_qyServiceId_base + 120); //  2007/03/07
        public final static int CONST_qyServiceId_mis = CONST_qyServiceId_is;

        public final static int CONST_qyServiceId_showInfo = (CONST_qyServiceId_base + 130);


        //  命令码, !!! 严重警告：以下命令码，只能添加，不能修改，以避免出现各产品不兼容的局面
        //			 类型：short
        //			 低端保留：
        public final static int CONST_qyCmd_tellService = 1;                       //  "启动服务"
        public final static int CONST_qyCmd_end = 255;                 //  "结束会话"

        //
        public final static short CONST_qyCmd_getCfgs = ( 2);
        //  2007/04/23, mis
        public final static short CONST_qyCmd_showInfo = (10);   //
        public final static short CONST_qyCmd_showInfo_small = (11);   //
        public final static short CONST_qyCmd_showInfo_small_java = (12);   //


        //
        public final static short CONST_qyCmd_refreshImObjListReq = (13);    //  ??????????2007/05/06????????????imCommType???
        //
        public final static short CONST_qyCmd_refreshRecentFriendsReq = (14);    //  ??????????2007/05/10
        //
        public final static short CONST_qyCmd_mcComm = (15); //  2011/01/08
        //
        public final static short CONST_qyCmd_sendReq = (16);

        //
        public final static short CONST_qyCmd_ca  =  (  17  );	//

        //
        public final static short CONST_qyCmd_hg  =		(  18  );	//

        //
        public final static short CONST_qyCmd_talkTo = (19); //  ???2007/04/09

        public final static short CONST_qyCmd_sendTask = (20);   //  ???????2007/06/23
        public final static short CONST_qyCmd_sendTaskReply = (21);  //  ???????2007/06/23
        public final static short CONST_qyCmd_sendRobotTask = (22);  //  ????????
        public final static short CONST_qyCmd_sendRobotTaskReply = (23); //  ????????
        public final static short CONST_qyCmd_sendRobotTaskData = (24);  //  ????????????????channel????????????????????????channel????, ????????channel???; ?????????????channel, ???channel????
        //
        public final static short CONST_qyCmd_sendMedia = (25);  //  ??????
        //
        public final static short CONST_qyCmd_lastMsgInSession = (26);   //  webMessenger?cgi????????????????????????daemon?????????????????????????????????????2007/12/11
        //
        public final static short CONST_qyCmd_sendVDevReq = (27);    //  ??????????????????,2008/01/24


        //			 用户定义:
        public final static int CONST_qyCmd_base = 2000;
        //					  其他定义在qyCommProc.h中，须>CONST_qyCmd_base大，<CONST_qyCmd_upReserved

        //			 高端保留：
        public final static int CONST_qyCmd_upReserved = ((int) 0xe000);    //  57344
        public final static int CONST_qyCmd_update = ((int) 0xe001);
        public final static int CONST_qyCmd_getFile = ((int) 0xe002);
        public final static int CONST_qyCmd_getDigest = ((int) 0xe003);


        //
        //			 short
        //
        public final static int CONST_qyRc_ok = ((int) 0);
        public final static int CONST_qyRc_err = ((int) 255);
        //
        public final static int CONST_qyRc_user = ((int) 10);
        public final static int CONST_qyRc_redirect = ((int) (11));
        public final static int CONST_qyRc_needAutoReg = ((int) (12));
        //  public final static int 		CONST_qyRc_notFound						(  (  short  )(  13  )  )	//  2005/02/01
        public final static int CONST_qyRc_needVerified = ((int) (14));     //  2007/04/22
        public final static int CONST_qyRc_anotherLogonExists = ((int) (15));       //  2011/02/02


        public final static int CONST_qyRc_peerOffline = ((int) 16);          //  2007/05/29
        public final static int CONST_qyRc_servBusy = ((int) 17);         //  2007/05/29
        public final static int CONST_qyRc_unknown = ((int) 18);          //  2007/07/01


        //////////////////////////////////////



        //
        public final static int CONST_qyRc_upReserved = ((int) 0xe000);  //  57344
        public final static int CONST_qyRc_quit = ((int) 0xe001);
        public final static int CONST_qyRc_update = ((int) 0xe002);
        public final static int CONST_qyRc_hint = ((int) 0xe003);

        /*
        public final static int CONST_commFlg_resp = (1 << 7);
        public final static int CONST_commFlg_moreData = (1 << 6);                                 //  2007/05/07
        public final static int CONST_commFlg_talkData = (1 << 5);                                  //  数据区的格式为MIS_MSG_TALKDATA，否则为IM_MSG_CONTENTU
        public final static int CONST_commFlg_routeTalkData = (1 << 4);                                    //  数据区的格式为用MSG_ROUTE+IM_MSG_CONTENTU
         */
        public final static int CONST_commFlg_resp = CONST_atCommFlg_resp;
        public final static int CONST_commFlg_moreData = CONST_atCommFlg_moreData;                                 //  2007/05/07
        //
        //
        public final static int CONST_commFlg_routeTalkData = CONST_atCommFlg_routeTalkData;                                    //  数据区的格式为用MSG_ROUTE+IM_MSG_CONTENTU


        //
        public final static int bufSize_qyCommServiceData = 8192;

    }

    public static class QY_COMM_SERVICE {
        public int serviceId;  //服务id
        public byte[] version = new byte[CONST_qyMaxVerLen];
        public int encType;
        //
        public byte ucCommServiceType;  //  anc
        public long i64StartTime_base;  //  anc
        //
        public byte[] data = new byte[bufSize_qyCommServiceData];

        public byte[] toArrayBuffer(){
            //
            int len  =  Integer.BYTES * 2 + CONST_qyMaxVerLen + bufSize_qyCommServiceData;
            len  =  Integer.BYTES  +  CONST_qyMaxVerLen  +  Integer.BYTES  +  Byte.BYTES  +  Long.BYTES  +  bufSize_qyCommServiceData;
            //
            ByteBuffer buffer = ByteBuffer.allocate(len);
            buffer.putInt(serviceId);
            buffer.put(version);
            buffer.putInt(encType);
            //
            buffer.put(ucCommServiceType);
            buffer.putLong(i64StartTime_base);
            //
            buffer.put(data);
            return buffer.array();
        }
    }

    ;


    public static class QY_COMM_BUFSIZE {
        int len;
        byte[] data;
    }

    ;

    //  2007/04/22,
    public static class QY_COMM_HEAD {
        public byte ucFlg;
        public byte reserved;
        //
        public short usCode;
        //
        public int len;

        public void memset0()
        {
            usCode  =  0;
            ucFlg  =  0;
            reserved  =  0;
            len  =  0;

        }

        public ByteBuffer toArrayBuffer(ByteBuffer tmp_bb){
            //ByteBuffer buffer=ByteBuffer.allocateDirect(8);
            if  (  tmp_bb==null)  return  null;
            if  (  tmp_bb.capacity()  <  8  )  return  null;
            tmp_bb.clear();
            ByteBuffer buffer  =  tmp_bb;

            //buffer=ByteBuffer.allocateDirect(8);

            //
            buffer.put(ucFlg);
            buffer.put(reserved);
            buffer.putShort(usCode);
            buffer.putInt(len);
            return buffer;
        }
    }

    ;


//#define		QY_COMM_REQHEAD							QY_COMM_HEAD
//#define		QY_COMM_RESPHEAD						QY_COMM_HEAD


    public static class QY_COMM_REQ {
        public QY_COMM_HEAD head;
        //union			{
        qyComm.QY_COMM_SERVICE service = new qyComm.QY_COMM_SERVICE();       //  这个service好象没有什么用处。2007/04/18
        //}	u;
    }

    ;


    public static class QY_COMM_RESP {
        public QY_COMM_HEAD head=new QY_COMM_HEAD();

        public byte[] toByteArray(ByteBuffer tmp_bb){
            ByteBuffer buf1 = head.toArrayBuffer(tmp_bb);
            if  (  buf1==null) return null;
            ByteBuffer buffer=ByteBuffer.allocateDirect(buf1.capacity());
            buffer.put(buf1);
            return buffer.array();
        }
    }

    ;


    public static class QY_COMM_SESSION {
        //
        public int uiType;                                //  2015/08/24
        //
        public qyComm.QY_COMM_SERVICE service;
        public int lenInBytes_service;                    //  接收到的QY_COMM_SERVICE 的字节熟. 2008/06/10

        public qyEncFunc.QY_ENC_CTX commEncCtx;                          //  2004/01/03

        //
        public byte[] startTime = new byte[CONST_qyTimeLen + 1];//[];    //  2004/07/26
        public String clientIp;
        //
        public int uiSessionId;                       //  会话序号，2004/09/11
        //
        public int usLastReqCode_i;                 //  2007/04/24, 输入的
        public int usLastRespCode_i;                    //  最后一个响应的响应码
        public int usLastReqCode_o;                 //  2007/04/24, 输出的
        public int usLastRespCode_o;                    //  最后一个响应的响应码
        //
        public int nTalks;                                //  与客户机交流的次数
        //
        public boolean bAdmin;                                //  2011/01/10

        //
        public int lastSnd_headLen;
        public int lastRecv_headLen;

    }

    ;


    public static class qyFuncs {

        //
        public static boolean isRcQuit(int x) {
            return ((x) == CONST_qyRc_quit);
        }

        public static boolean isRcOk(int x) {
            return ((x) == CONST_qyRc_ok);
        }

        public static boolean isRcRedirect(int x) {
            return ((x) == CONST_qyRc_redirect);
        }

        public static boolean isRcUpdate(int x) {
            return ((x) == CONST_qyRc_update);
        }

        public static boolean isRcHint(int x) {
            return ((x) == CONST_qyRc_hint);
        }

        public static boolean isRcNeedVerified(int x) {
            return ((x) == CONST_qyRc_needVerified);
        }

        //
        public static boolean isUcFlgResp(byte ucFlg) {
            return (0 != (ucFlg & CONST_commFlg_resp));
        }                //  2007/04/22

        public static boolean isUcFlgMoreData(byte ucFlg) {
            return (0 != (ucFlg & CONST_commFlg_moreData));
        }           //  2007/05/07

        public static boolean isUcFlgRouteTalkData(byte ucFlg) {
            return (0 != (ucFlg & CONST_commFlg_routeTalkData));
        }        //  2007/11/29

        public static boolean isUcFlgHead14(byte ucFlg) {
            return (0 != (ucFlg & CONST_atCommFlg_head14));
        }           //  2007/07/11

        //
        public static boolean isMoreDataEnd(short real_usSeqNo) {
            return (0 != (real_usSeqNo & CONST_endFlg_usSeqNo));
        }


    }
}


