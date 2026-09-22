package com.qycx.anc;

import static com.qycx.anc.Consts.CONST_atCommVer_1;
import static com.qycx.anc.Consts.CONST_atCommVer_null;
import static com.qycx.anc.ancCommProc_defs.Consts.CONST_anCommType_refreshRecentFriendsReq1;
import static com.qycx.base.postMsg2MgrFunc.qyFuncs.postMsg2Mgr_mc;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_outputReq;
import static com.qycx.base.qnmCommProc_mis.Consts.CONST_misMsgType_req;
import static com.qycx.base.qnmCommProc_mis.Consts.cntof_imHtmlContent_wBuf;
import static com.qycx.base.qnmMainFunc_isCli.qyFuncs.getChannelByType;
import static com.qycx.base.qyComm.Consts.CONST_qyCmd_refreshRecentFriendsReq;
import static com.qycx.base.qyComm.Consts.CONST_qyCmd_sendReq;
import static com.qycx.base.qyMcMainDaemon_isCli.qyFuncs.postMsg2OutputQ_mc;
import static com.qycx.base.qyMcMain_global.g.g_pQyMc;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qy_getuiNextTranNo;
import com.qycx.base.tmpFunc;
import com.qycx.base.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class atFuncs {

    //
    public static int DEFAULT_commVer = CONST_atCommVer_1;


    public static boolean M_isCommVer1() {
        return anc_globals.g.g_iCommVer == CONST_atCommVer_1;
    }


    //
    public static int sendToRefreshRecentFriends_old(qnmCommProc_mis.REFRESH_RECENTFRIENDS_REQ rrr, String hint) {
        //
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

        //
        Log.d("anc", "sendToRefreshRecentFriends called " + hint);

        //
        do {


            //
            int len = 10;
            //len  =  offsetof(  REFRESH_RECENTFRIENDS_REQ,  mems  )  +  req.usCnt  *  sizeof(  req.mems[0]  );

            //
            int uiMisMsgType = CONST_misMsgType_req;
            byte ucFlg = 0;
            short usCode = CONST_qyCmd_refreshRecentFriendsReq;


            //MACRO_prepareForTran();
            long tStartTime;
            int uiTranNo;
            short usSeqNo = 0;
            int dataLen = len;
            //
            tStartTime = tmpFunc.qyFuncs.mytime();
            uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();


            //
            qnmCommProc_mis.MIS_MSG_REQ req = new qnmCommProc_mis.MIS_MSG_REQ();
            //req.buffer = ByteBuffer.allocate(bufSize_transferVideoData);
            req.buffer = ByteBuffer.allocate(cntof_imHtmlContent_wBuf);
            req.uiType = uiMisMsgType;
            req.ucFlg = ucFlg;
            req.usCode = usCode;
            req.tStartTime = tStartTime;
            req.uiTranNo = uiTranNo;
            req.usSeqNo = usSeqNo;
            //req.lenInBytes = dataLen;
            req.data = rrr;

            {
                //qnmCommProc_mis.REFRESH_RECENTFRIENDS_REQ rrr  =  (qnmCommProc_mis.REFRESH_RECENTFRIENDS_REQ) data;
                req.buffer.putInt(rrr.uiType);
                byte ucReqType = 0;
                req.buffer.put(ucReqType);
                req.buffer.putLong(rrr.tLastRefreshedTime_misServ);
                req.buffer.putShort(rrr.usCnt);
                //
                int i;
                for (i = 0; i < rrr.usCnt; i++) {
                    req.buffer.putLong(rrr.mems[i].ui64Id);
                    req.buffer.putLong(rrr.mems[i].tLastRefreshedTime_misServ);
                }
                //
            }
            //
            req.lenInBytes = req.buffer.position();


            //
            qmCommFunc_mis.MIS_CHANNEL pChannel = getChannelByType(pMisCnt, 0);
            postMsg2OutputQ_mc(pMisCnt, req, 10, pChannel);

            //
            pProcInfo.m_lastTickCnt_sendToRefreshRecentFriends = myGetTickCount(null);

            //
        } while (false);

        //
        return 0;
    }



    //////////////////////////////////////////////////////////////////////

    public static int sendToRefreshRecentFriends_new(qnmCommProc_mis.AnRefreshRecentFriendsReq1_h rrr, String hint) {
        //
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

        //
        Log.d("anc", "sendToRefreshRecentFriends_new called " + hint);

        //
        do {

            //
            rrr.uiType=CONST_anCommType_refreshRecentFriendsReq1;



            //
            int len = 10;
            //len  =  offsetof(  REFRESH_RECENTFRIENDS_REQ,  mems  )  +  req.usCnt  *  sizeof(  req.mems[0]  );

            //
            int uiMisMsgType = CONST_misMsgType_req;
            byte ucFlg = 0;
            short usCode = CONST_qyCmd_refreshRecentFriendsReq;


            //MACRO_prepareForTran();
            long tStartTran;
            int uiTranNo;
            short usSeqNo = 0;
            int dataLen = len;
            //
            tStartTran = tmpFunc.qyFuncs.mytime();
            uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();

            //
            if  (  pQyMc.appParams.bDbg)  {
                Log.d("Test, turn off please","sendToRefreshRecentFriends_new: set tn to 1025");
                uiTranNo=1025;
            }


            //
            int channelType =  0;

            //
            postMsg2Mgr_mc(null,pMisCnt, null, CONST_misMsgType_outputReq, (byte)0, usCode, tStartTran, uiTranNo, 0, rrr, len, 0, 0, channelType, null, false);

            //
            pProcInfo.m_lastTickCnt_sendToRefreshRecentFriends = myGetTickCount(null);

            //
        } while (false);

        //
        return 0;
    }















    /////////////////////////////////////////////////////

    // 从 assets 目录复制文件到指定目录
    // atFuncs.java

    public static void copyFileFromAssets(Context context, String assetFileName, String destDir) {
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            // 从 assets 目录读取文件
            in = assetManager.open(assetFileName);

            // 定义目标文件对象
            File outFile = new File(destDir, assetFileName);

            // ---------------------------------------------------------
            // 将创建目录的代码移到 new FileOutputStream 之前
            // ---------------------------------------------------------
            File parentDir = outFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean mkSuccess = parentDir.mkdirs();
                if(!mkSuccess) {
                    Log.e("anc", "Failed to create directory: " + parentDir.getAbsolutePath());
                }
            }
            // ---------------------------------------------------------

            // 只有目录存在了，这一行才能成功执行，否则会报错
            out = new FileOutputStream(outFile);

            // 复制文件内容
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            out.flush();

            //
            Log.d("anc","copy "  +  assetFileName  +  " ok");

            //
        } catch (IOException e) {
            Log.e("anc", "Copy error: " + e.getMessage()); // 建议把错误打印出来，方便调试
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
