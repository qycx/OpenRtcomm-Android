
package com.qycx.base;

import static com.qycx.anc.ancCommProc_defs.Consts.CONST_ancCfgId_conf_ui64Id;
import static com.qycx.anc.ancCommProc_defs.Consts.CONST_ancCommType_mem;
import static com.qycx.anc.ancCommProc_defs.Consts.CONST_ancCommType_transferAudioData;
import static com.qycx.anc.ancCommProc_defs.Consts.CONST_ancCommType_transferVideoData;
import static com.qycx.anc.ancProtocolFunc.atData2Stream;
import static com.qycx.anc.ancProtocolFunc.atStream2Data;
import static com.qycx.anc.ancProtocolFunc.atntohCfgItem;
import static com.qycx.anc.atFuncs.M_isCommVer1;
import static com.qycx.base.QYThreadCtx.getCtxThreadTmpVar;
import static com.qycx.base.imCommType_defs.*;
import static com.qycx.base.imCommType_defs.Consts.*;
import static com.qycx.base.isProtocolFunc.Consts.*;
import static com.qycx.base.parseSpsPps.qyFuncs.makeSpsPps;
import static com.qycx.base.parseSpsPps.qyFuncs.parseSpsPps1;
import static com.qycx.base.qnmCommProc.*;
import static com.qycx.base.qnmCommProc.Consts.*;
import static com.qycx.base.qnmCommProc.qyFuncs.myUtf82WChar;
import static com.qycx.base.qnmCommProc.qyFuncs.myWChar2Utf8;
import static com.qycx.base.qnmCommProc_mis.Consts.*;
import static com.qycx.base.qnmCommProc_mis.qyFuncs.*;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgRouteTalkData;

//import static com.qycx.base.qyComm.qyFuncs.isUcFlgTalkData;

import static com.qycx.base.qyCommProc.Consts.*;
import static com.qycx.base.qyCommProc_open.Consts.*;
import static com.qycx.base.qyCommonFunc9.qyFuncs.*;
import static com.qycx.base.qyCommonSock.qyFuncs.bIpValid;
import static com.qycx.base.qyCommonSock.qyFuncs.isValidIPAddress;
import static com.qycx.base.qyCommonSock.qyFuncs.myinet_addr;
import static com.qycx.base.qyCommonSock.qyFuncs.ulIp2Str;
import static com.qycx.base.qyDefs.Consts.*;
import static com.qycx.base.qyFuncs.Consts.CONST_qyMaxIpLen;
import static com.qycx.base.qyMcMainDaemon_isCli.Consts.bufSize_1024;
import static com.qycx.base.tmpFunc.qyFuncs.*;

import com.qycx.anc.muxStream_open;
import com.qycx.base.util.Log;
import com.qycx.base.util.javatuples.Pair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import kotlin.UShort;

public class isProtocolFunc {
//    private static final String TAG = isProtocolFunc.class.getSimpleName();

    public static class Consts {
        public final static String CONST_filterChars = "'";

        public final static int CONST_cntof_utf8Str = 32 * 1024;       //  2010/12/05
        public final static int CONST_cntof_wStr = 32 * 1024;       //  2010/12/05
        public final static int bufSize_vh = 4096;
        public final static int bufSize_ah = 4096;

        //
        public final static int cntof_ctx_uiStreamIds = 5;


    }


    //
    public static class CTX_stream2Data {
        //
        public int uiStreamIds[] = new int[cntof_ctx_uiStreamIds];//[];
        //
        public int index_streamId;
    }

    public interface PF_handler_stream2Data {
        int call(isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem);
    }

    public interface PF_handler_IM_CONTENTUstream2Data {
        int call(isProtocolFunc.CTX_stream2Data pCtx, Object p0, qnmCommProc_mis.IM_CONTENTU p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem);
    }

    public static class qyFuncs {

        public static boolean bMessengerIdValid(qyDefs.QY_MESSENGER_ID pIdInfo) {
            //  if  (  pIdInfo  &&  pIdInfo->ui64Id  &&  pIdInfo->ui64Id  !=  CONST_invalidMessengerId  )  return  true;
            long invalid = CONST_invalidMessengerId;
            if (pIdInfo != null && pIdInfo.ui64Id != 0) return true;
            return false;
        }

        public static boolean bSupported_sendVhDec() {
            return true;
        }


        //  对orgData是WCHAR的情况，orgDataSize表示WCHAR的count
        //  对这个流要求用CONST_qnmCfgId_null结尾
        //  pptr指向下一个空间，而不是输出串
        //  pSize为剩余的字节数，不是一共得到了多少
        /*
        public static int iidata2Stream(short iDataType, short usCfgId, Object orgData, int orgDataSize, ByteBuffer buffer) {
            int iErr = -1;
            if (null == buffer || 0 == buffer.capacity() || 0 == buffer.remaining()) return -1;
            int pos = buffer.position();
            int len;

            if (null == orgData) {
                switch (iDataType) {
                    case CONST_qyDataType_l32:
                    case CONST_qyDataType_char:
                    case CONST_qyDataType_short:
                        break;
                    default:
                        return -1;  //  非数值的值为地址，不能为空
                }
            }
            if (iDataType == CONST_qyDataType_l64) {
                if (orgDataSize != 0 && orgDataSize < 8) {       //  传入0也算了，早期代码都用0。2007/12/11
                    Log.e("data2Stream: l64需要传入数据地址，故建议输入数据长度应>=sizeof(  __int64  )");
                    return -1;
                }
            }
            ByteBuffer buf = null;
            switch (iDataType) {
                case CONST_qyDataType_l32: {
                    buf = ByteBuffer.allocate(Short.BYTES * 2 + Integer.BYTES);
                    buf.putShort(usCfgId);
                    buf.putShort(iDataType);
                    buf.putInt(orgData == null ? 0 : (int) orgData);
                }
                break;
                case CONST_qyDataType_char: {
                    buf = ByteBuffer.allocate(Short.BYTES * 2 + Byte.BYTES);
                    buf.putShort(usCfgId);
                    buf.putShort(iDataType);
                    buf.put(orgData == null ? 0 : (byte) orgData);
                }
                break;
                case CONST_qyDataType_short: {
                    buf = ByteBuffer.allocate(Short.BYTES * 3);
                    buf.putShort(usCfgId);
                    buf.putShort(iDataType);
                    buf.putShort(orgData == null ? 0 : (short) orgData);
                }
                break;
                case CONST_qyDataType_data: {
                    if (orgDataSize > bufSize_cfgItemData) {
                        Log.e("data2Stream: dataType_data: too big dataSize");
                        return iErr;
                    }
                    buf = ByteBuffer.allocate(6 + orgDataSize);
                    buf.putShort(usCfgId);
                    buf.putShort(iDataType);
                    buf.putShort((short) orgDataSize);
                    buf.put((byte[]) orgData, 0, orgDataSize);
                }
                break;
                case CONST_qyDataType_lData: {
                    if (orgDataSize > bufSize_cfgItemLData) {
                        Log.e("data2Stream: dataType_lData: too big dataSize");
                        return iErr;
                    }
                    buf = ByteBuffer.allocate(Short.BYTES * 2 + Integer.BYTES + orgDataSize);
                    buf.putShort(usCfgId);
                    buf.putShort(iDataType);
                    buf.putInt(orgDataSize);
                    buf.put((byte[]) orgData, 0, orgDataSize);
                }
                break;
                case CONST_qyDataType_l64: {

                    buf = ByteBuffer.allocate(Short.BYTES * 2 + Long.BYTES);
                    buf.putShort(usCfgId);
                    buf.putShort(iDataType);
                    buf.putLong((Long) orgData);
                }
                break;
                case CONST_qyDataType_str:
                case CONST_qyDataType_utf8Str: {
                    byte[] data = ((String) orgData).getBytes(StandardCharsets.UTF_8);
                    buf = ByteBuffer.allocate(Short.BYTES * 3 + data.length);
                    buf.putShort(usCfgId);
                    buf.putShort(iDataType);
                    buf.putShort((short) Math.min(CONST_cntof_cfgItemStr, orgDataSize));
                    buf.put(data);
                }
                break;
                default:
                    return iErr;
            }
            len = buf.capacity();
            try {
                buffer.put(buf.array());
            } catch (Exception e) {
                Log.e(e.getLocalizedMessage());
            }
            buf.clear();
            buf = null;
            return len;
        }
*/

        public static int data2Stream(short iDataType, short usCfgId, Object orgData, int orgDataSize, ByteBuffer buffer) {

            //
            if  (  M_isCommVer1())  {

                return  atData2Stream(iDataType,usCfgId,orgData,orgDataSize,buffer);

            }


            //
            int iErr = -1;
            if (null == buffer || 0 == buffer.capacity() || 0 == buffer.remaining()) return -1;
            int pos = buffer.position();
            int len;
            int  tmpLen  =  0;

            if (null == orgData) {
                switch (iDataType) {
                    case CONST_qyDataType_l32:
                    case CONST_qyDataType_char:
                    case CONST_qyDataType_short:
                        break;
                    default:
                        return -1;  //  非数值的值为地址，不能为空
                }
            }
            if (iDataType == CONST_qyDataType_l64) {
                if (orgDataSize != 0 && orgDataSize < 8) {       //  传入0也算了，早期代码都用0。2007/12/11
                    Log.e("data2Stream: l64需要传入数据地址，故建议输入数据长度应>=sizeof(  __int64  )");
                    return -1;
                }
            }
            ByteBuffer buf = null;
            buf  =  buffer;
            switch (iDataType) {
                case CONST_qyDataType_l32: {
                    //buf = ByteBuffer.allocate(Short.BYTES * 2 + Integer.BYTES);
                    buf.putShort(usCfgId);
                    buf.putShort(iDataType);
                    buf.putInt(orgData == null ? 0 : (int) orgData);
                    tmpLen  =  Short.BYTES * 2 + Integer.BYTES;
                }
                break;
                case CONST_qyDataType_char: {
                    //buf = ByteBuffer.allocate(Short.BYTES * 2 + Byte.BYTES);
                    buf.putShort(usCfgId);
                    buf.putShort(iDataType);
                    buf.put(orgData == null ? 0 : (byte) orgData);
                    tmpLen=Short.BYTES * 2 + Byte.BYTES;
                }
                break;
                case CONST_qyDataType_short: {
                    //buf = ByteBuffer.allocate(Short.BYTES * 3);
                    buf.putShort(usCfgId);
                    buf.putShort(iDataType);
                    buf.putShort(orgData == null ? 0 : (short) orgData);
                    tmpLen  =  Short.BYTES * 3;
                }
                break;
                case CONST_qyDataType_data: {
                    if (orgDataSize > bufSize_cfgItemData) {
                        Log.e("data2Stream: dataType_data: too big dataSize");
                        return iErr;
                    }
                    //buf = ByteBuffer.allocate(6 + orgDataSize);
                    buf.putShort(usCfgId);
                    buf.putShort(iDataType);
                    buf.putShort((short) orgDataSize);
                    buf.put((byte[]) orgData, 0, orgDataSize);
                    tmpLen  =  6 + orgDataSize;
                }
                break;
                case CONST_qyDataType_lData: {
                    if (orgDataSize > bufSize_cfgItemLData) {
                        Log.e("data2Stream: dataType_lData: too big dataSize");
                        return iErr;
                    }
                    //buf = ByteBuffer.allocate(Short.BYTES * 2 + Integer.BYTES + orgDataSize);
                    buf.putShort(usCfgId);
                    buf.putShort(iDataType);
                    buf.putInt(orgDataSize);
                    buf.put((byte[]) orgData, 0, orgDataSize);
                    tmpLen  =  Short.BYTES * 2 + Integer.BYTES + orgDataSize;
                }
                break;
                case CONST_qyDataType_l64: {

                    //buf = ByteBuffer.allocate(Short.BYTES * 2 + Long.BYTES);
                    buf.putShort(usCfgId);
                    buf.putShort(iDataType);
                    buf.putLong((Long) orgData);
                    tmpLen  =  Short.BYTES * 2 + Long.BYTES;
                }
                break;
                case CONST_qyDataType_str:
                case CONST_qyDataType_utf8Str: {
                    byte[] data = ((String) orgData).getBytes(StandardCharsets.UTF_8);
                    //buf = ByteBuffer.allocate(Short.BYTES * 3 + data.length);
                    buf.putShort(usCfgId);
                    buf.putShort(iDataType);
                    buf.putShort((short) Math.min(CONST_cntof_cfgItemStr, orgDataSize));
                    buf.put(data);
                    tmpLen  =  Short.BYTES * 3 + data.length;
                }
                break;
                default:
                    return iErr;
            }
            /*
            if  (  buf.capacity()  !=  tmpLen )  {
                Log.e("qy", "data2Stream failed, tmpLen err");
            }

             */
            //len = buf.capacity();
            len  =  tmpLen;
            try {
                //buffer.put(buf.array());
            } catch (Exception e) {
                Log.e(e.getLocalizedMessage());
            }
            //buf.clear();
            //buf = null;
            return len;
        }





        //字节流转换结构
        public static int stream2Data(ByteBuffer pCfgsBuf_usable, int pSize_usable, PF_handler_stream2Data pf_filter, PF_handler_stream2Data pf, Object p0, Object p1) {

            //
            if  (  M_isCommVer1())  {
                int  tmpiRet   =  -1;

                tmpiRet  =  atStream2Data( pCfgsBuf_usable, pSize_usable, pf_filter, pf, p0, p1);
                //
                if  (  tmpiRet  <  0  )  {
                    Log.d("anc","atStream2Data failed");
                }
                //
                return  tmpiRet;


            }



            //
            int iErr = -1;
            int len = 0;

            CTX_stream2Data ctx = new CTX_stream2Data();

            int nStarts = 0;
            int tmpiRet;

            if (null == pCfgsBuf_usable || null == pf_filter || null == pf || pSize_usable == 0)
                return -1;
            int size = pSize_usable;

            for (; size != 0; ) {
//                Log.d("size:"+size);
                QY_CFGITEM pItem = qnmCommProc.qyFuncs.qyntohCfgItem(pCfgsBuf_usable);     //  这里要注意：这里qyntohCfgItem(  )返回的是转换前的字节流长度。和qyhtonCfgItem(  )返回的同
                if(pItem == null) {
                    return iErr;
                }
                len = pItem.len;
                if (len <= 0 || len > size) {
                    Log.e("1 size:" + size + "  len:" + len);
                    return iErr;
                }
                size -= len;

                if (0 == nStarts) {
                    if (pItem.cfgId != CONST_qnmCfgId_start) {
                        Log.e("2");
                        return iErr;
                    }
                    if (pItem.dataType != CONST_qyDataType_long) {
                        Log.e("3");
                        return iErr;
                    }
                }

                if (pItem.cfgId == CONST_qnmCfgId_start) {
                    nStarts++;
                    if (nStarts > cntof_ctx_uiStreamIds) {
                        Log.e("4");
                        return iErr;
                    }

                    ctx.uiStreamIds[nStarts - 1] = ((QY_CFGITEM_L32)pItem).ulVal;
                    ctx.index_streamId = nStarts - 1;
                    if (0 != (tmpiRet = pf.call(ctx, p0, p1, ctx.uiStreamIds[nStarts - 1], pItem))) {
                        iErr = tmpiRet;
                        Log.e("5  size:" + size);
                        return iErr;
                    }
                    continue;
                }

                if (pItem.cfgId == CONST_qnmCfgId_null) {
                    ctx.index_streamId = nStarts - 1;
                    if (0 != (tmpiRet = pf.call(ctx, p0, p1, ctx.uiStreamIds[nStarts - 1], pItem))) {       //  这里有利于处理结构结束时做结尾工作，2007/08/01
                        iErr = tmpiRet;
                        Log.e("6");
                        return iErr;
                    }
                    nStarts--;
                    if (0 == nStarts) break;
                    continue;
                }

                ctx.index_streamId = nStarts - 1;
                if (0 != (tmpiRet = pf_filter.call(ctx, p0, p1, ctx.uiStreamIds[nStarts - 1], pItem))) {       //  2008/12/03, 主要是对pItem里的数据进行过滤。不是对结果的
                    iErr = tmpiRet;
                    Log.e("7");
                    return iErr;
                }
                ctx.index_streamId = nStarts - 1;
                if (0 != (tmpiRet = pf.call(ctx, p0, p1, ctx.uiStreamIds[nStarts - 1], pItem))) {
                    iErr = tmpiRet;
                    Log.e("8 size:" + size);
                    return iErr;
                }
            }


            iErr = 0;

            if (0 == iErr) {
//                pCfgsBuf_usable = ptr;
                pSize_usable = size;
            }
            return pSize_usable;
        }

        //
        public static int getFieldData_l32(QY_CFGITEM pItem) {
            if (null == pItem) return -1;
            //if (pItem.dataType != CONST_qyDataType_long) return -1;
            switch (pItem.dataType) {
                case CONST_qyDataType_l32:
                    return ((QY_CFGITEM_L32) pItem).ulVal;
                default: {
                    short sVal  =  getFieldData_short(pItem);
                    int iVal  =  (  sVal  &  0xffff  );
                    return iVal;
                }
            }
        }



        //调试发现部分字段会解析出负值，因此调用 getFieldData_long和getFieldData_l32时，
        // 判断返回是否正确，不能简单的判断返回值是否小于0,应该用是否等于-1来判断
        public static int getFieldData_long(qnmCommProc.QY_CFGITEM pItem) {
            return getFieldData_l32(pItem);
        }

        public static short getFieldData_short(qnmCommProc.QY_CFGITEM pItem) {
            if (null == pItem) return -1;
            //
            //if (pItem.dataType != CONST_qyDataType_short) { return -1; }
            switch (pItem.dataType) {
                case CONST_qyDataType_short:
                    return ((QY_CFGITEM_SHORT) pItem).usVal;
                default: {
                    byte bVal  =  getFieldData_byte(pItem);
                    int iVal  = (bVal & 0xff);
                    return (short)iVal;
                }
            }
        }

        public static byte getFieldData_byte(qnmCommProc.QY_CFGITEM pItem) {
            if (null == pItem) return -1;
            if (pItem.dataType != CONST_qyDataType_char) return -1;
            return ((QY_CFGITEM_BYTE)pItem).ucVal;
        }

        public static long getFieldData_l64(qnmCommProc.QY_CFGITEM pItem) {
            //
            if (null == pItem) return -1;
            //if (pItem.dataType != CONST_qyDataType_l64) return -1;
            switch (pItem.dataType) {
                case CONST_qyDataType_l64:
                    return ((QY_CFGITEM_L64) pItem).ulVal;
                    //break;
                default: {
                    int iVal  =  getFieldData_long(pItem);
                    long lVal  =  (iVal  &  0xffffffff);
                    return  lVal;
                    //break;
                }
            }
        }


        public static ByteBuffer getFieldData_data(qnmCommProc.QY_CFGITEM pItem) {
            if (null == pItem) return null;
            if (pItem.dataType != CONST_qyDataType_data) return null;
            return ((QY_CFGITEM_DATA)pItem).buf;
        }

        public static ByteBuffer getFieldData_lData(QY_CFGITEM pItem) {
            if (null == pItem) return null;
            if (pItem.dataType != CONST_qyDataType_lData) return null;
            return ((QY_CFGITEM_LDATA)pItem).p;
        }

        public static String getFieldData_str(QY_CFGITEM pItem) {
            if(pItem ==null) return  "";
//            if (pItem.dataType != CONST_qyDataType_str || pItem.dataType != CONST_qyDataType_utf8Str) return "";
            return ((QY_CFGITEM_STR)pItem).str;
        }

        public static String getFieldData_wStr(qnmCommProc.QY_CFGITEM pItem) {
            return getFieldData_str(pItem);
        }


        //  2008/12/03, 为通信加一个过滤规则。

        public static PF_handler_stream2Data filterStream_is = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) -> {
            int iErr = -1;
            //  QY_CFGITEM				*	pItem		=	(  QY_CFGITEM  *  )p2;
            int tmp_len;

            int tmp_dataType;
            //memcpy(tmp_dataType,pItem->dataType, sizeof(tmp_dataType));
            tmp_dataType = pItem.dataType;

            iErr = 0;
            return iErr;
        };


        public static int commAuthInfo2Stream_mis(qnmCommProc_mis.QY_COMM_AUTHINFO_MIS pAuthInfo, ByteBuffer buf) {
            //
            //Log.d("commAuthInfo2Stream_mis: ");
            //
            int iErr = -1;
            int total_len = 0;
            int len = 0;

            if (0 > (len = data2Stream(CONST_qyDataType_l32, CONST_qnmCfgId_start, CONST_imCommType_auth, 0, buf))) return iErr;
            total_len += len;

            if (pAuthInfo.ver != null && !pAuthInfo.ver.isEmpty()) {
                if (0 > (len = data2Stream(CONST_qyDataType_str, CONST_qnmCfgId_clientVer, pAuthInfo.ver, mystrlen(pAuthInfo.ver.getBytes()), buf))) return iErr;
                total_len += len;
            }

            if (pAuthInfo.usAuthCmd != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_authCmd, pAuthInfo.usAuthCmd, 0, buf))) return iErr;
                total_len += len;
            }

            if (pAuthInfo.iAppType != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_iAppType, pAuthInfo.iAppType, 0, buf))) return iErr;
                total_len += len;
            }

            if (pAuthInfo.iCustemId != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_iCustemId, pAuthInfo.iCustemId, 0, buf))) return iErr;
                total_len += len;
            }

            if (pAuthInfo.uiChannelType != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiChannelType, pAuthInfo.uiChannelType, 0, buf))) return iErr;
                total_len += len;
            }

            if (pAuthInfo.usLangId != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_langId, pAuthInfo.usLangId, 0, buf))) return iErr;
                total_len += len;
            }

            if (pAuthInfo.uiDevType != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiDevType_from, pAuthInfo.uiDevType, 0, buf))) return iErr;
                total_len += len;
            }

            if (pAuthInfo.usAuthType != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usAuthType, pAuthInfo.usAuthType, 0, buf))) return iErr;
                total_len += len;
            }

            if (isValidIPAddress(pAuthInfo.ip)) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_ulIp, myinet_addr(pAuthInfo.ip), 0, buf))) return iErr;
                total_len += len;
            }

            if (pAuthInfo.messengerName != null && !pAuthInfo.messengerName.isEmpty()) {
                if (0 > (len = data2Stream(CONST_qyDataType_utf8Str, CONST_qnmCfgId_messengerName, pAuthInfo.messengerName, mylstrlen(pAuthInfo.messengerName), buf))) return iErr;
                total_len += len;
            }

            if (pAuthInfo.messengerPasswd != null && !pAuthInfo.messengerPasswd.isEmpty()) {
                if (0 > (len = data2Stream(CONST_qyDataType_str, CONST_qnmCfgId_messengerPasswd, pAuthInfo.messengerPasswd, mystrlen(pAuthInfo.messengerPasswd.getBytes()), buf))) return iErr;
                total_len += len;
            }

            if (pAuthInfo.wRndName_serv != null && !pAuthInfo.wRndName_serv.isEmpty()) {
                if (0 > (len = data2Stream(CONST_qyDataType_utf8Str, CONST_qnmCfgId_wRndName_serv, pAuthInfo.wRndName_serv, mylstrlen(pAuthInfo.wRndName_serv), buf))) return iErr;
                total_len += len;
            }

            if (pAuthInfo.uiRnd_cli != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiRnd_cli, pAuthInfo.uiRnd_cli, 0, buf))) return iErr;
                total_len += len;
            }

            if (pAuthInfo.uiLogonId != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiLogonId, pAuthInfo.uiLogonId, 0, buf))) return iErr;
                total_len += len;
            }

            if (pAuthInfo.uiBlobLen_sessionKey != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_data, CONST_qnmCfgId_sessionKeyBlob, pAuthInfo.sessionKeyBlob, pAuthInfo.uiBlobLen_sessionKey, buf))) return iErr;
                total_len += len;
            }

            if (pAuthInfo.usSessionEncType != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usSessionEncType, pAuthInfo.usSessionEncType, 0, buf))) return iErr;
                total_len += len;
            }

            if (pAuthInfo.usLen_sessionIv != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_data, CONST_qnmCfgId_sessionIv, pAuthInfo.sessionIv, pAuthInfo.usLen_sessionIv, buf))) return iErr;
                total_len += len;
            }

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, buf))) return iErr;
            total_len += len;

            return total_len;
        }

        public static PF_handler_stream2Data tmpHandler_stream2AuthResp_mis = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) -> {
            int iErr = -1;

            qnmCommProc_mis.AUTH_RESP_MIS pAuthResp = (qnmCommProc_mis.AUTH_RESP_MIS) p1;

            if (uiStreamId != CONST_imCommType_authResp) return iErr;
            short dataType = pItem.dataType;
            switch (pItem.cfgId) {
                case CONST_qnmCfgId_uiCommEncCtxType: {
                    //if (dataType != CONST_qyDataType_long) return iErr;
                    //pAuthResp.uiCommEncCtxType = ((QY_CFGITEM_L32)pItem).ulVal;
                    pAuthResp.uiCommEncCtxType = getFieldData_long(pItem);
                }
                break;
                case CONST_qnmCfgId_publicKeyBlob: {
                    ByteBuffer fieldDataData = getFieldData_data(pItem);
                    pAuthResp.publicKeyBlob = fieldDataData.array();
                    pAuthResp.uiBlobLen_publicKey = fieldDataData.capacity();
                }
                break;
                case CONST_qnmCfgId_messengerId: {
                    //if (dataType != CONST_qyDataType_l64) return iErr;
                    //pAuthResp.idInfo.ui64Id = ((QY_CFGITEM_L64)pItem).ulVal;
                    pAuthResp.idInfo.ui64Id = getFieldData_l64(pItem);
                }
                break;
                case CONST_qnmCfgId_uiLogonId: {
                    //if (dataType != CONST_qyDataType_long) return iErr;
                    //pAuthResp.uiLogonId = ((QY_CFGITEM_L32)pItem).ulVal;
                    pAuthResp.uiLogonId = getFieldData_long(pItem);
                }
                break;
                case CONST_qnmCfgId_wRndName_serv: {
                    if (dataType != CONST_qyDataType_utf8Str) return -1;
                    pAuthResp.wRndName_serv = ((QY_CFGITEM_STR)pItem).str;
                }
                break;
                case CONST_qnmCfgId_ucbDaemonDemo: {
                    if (dataType != CONST_qyDataType_char) return -1;
                    pAuthResp.ucbDaemonDemo = (byte) (((QY_CFGITEM_BYTE)pItem).ucVal & 0xFF);
                }
                break;
                case CONST_qnmCfgId_usMaxVideoConferenceMems: {
                    //if (dataType != CONST_qyDataType_short) return -1;
                    //pAuthResp.usMaxVideoConferenceMems = ((QY_CFGITEM_SHORT)pItem).usVal;
                    pAuthResp.usMaxVideoConferenceMems = getFieldData_short(pItem);//
                }
                break;
                case CONST_qnmCfgId_ucDays_left: {
                    if (dataType != CONST_qyDataType_char) return -1;
                    pAuthResp.ucDays_left = ((QY_CFGITEM_BYTE)pItem).ucVal;
                }
                break;
                case CONST_qnmCfgId_subSystemId: {
                    //if (dataType != CONST_qyDataType_short) return -1;
                    //pAuthResp.usSubSystemId_daemon = ((QY_CFGITEM_SHORT)pItem).usVal;
                    pAuthResp.usSubSystemId_daemon = getFieldData_short(pItem);
                }
                break;
                case CONST_qnmCfgId_talkerDesc: {
                    if (dataType != CONST_qyDataType_utf8Str) return -1;
                    pAuthResp.talkerDesc = ((QY_CFGITEM_STR)pItem).str;
                }
                break;
                case CONST_qnmCfgId_clientWelcomeMsg: {
                    if (dataType != CONST_qyDataType_utf8Str) return -1;
                    pAuthResp.clientWelcomeMsg = ((QY_CFGITEM_STR)pItem).str;
                }
                break;
                case CONST_qnmCfgId_hint: {
                    if (dataType != CONST_qyDataType_utf8Str) return -1;
                    pAuthResp.wBuf_hint = ((QY_CFGITEM_STR)pItem).str;
                }
                break;
                default:
                    break;

            }
            iErr = 0;

            return iErr;
        };


        ///////////////////////////////////////////////////////

        public static int msgRoute2Stream(int uiStreamId, qnmCommProc_mis.MSG_ROUTE pReq, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;
//            ByteBuffer ptr = ByteBuffer.allocate(1024);
            int len = -1;
            int total_len = 0;
            if (null == pReq) return -1;
//            if (null == buf) return -1;

            if (0 > (len = data2Stream(CONST_qyDataType_long, (short) CONST_qnmCfgId_start, (Object) uiStreamId, 0, ptr)))
                return iErr;
            total_len += len;
            if (pReq.idInfo_to.ui64Id != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_messengerId_to, (Object) pReq.idInfo_to.ui64Id, Long.BYTES, ptr)))
                    return iErr;
                total_len += len;
            }
            if (pReq.idInfo_from.ui64Id != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_messengerId_from, (Object) pReq.idInfo_from.ui64Id, Long.BYTES, ptr)))
                    return iErr;
                total_len += len;
            }
            //
            if (0 != pReq.uiMsgRouteId) {       //  2008/05/27
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiMsgRouteId, (Object) pReq.uiMsgRouteId, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            //
            if (0 != pReq.uiLogonId) {           //  2007/12/22
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiLogonId, (Object) pReq.uiLogonId, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            //
            if (0 != pReq.usLangId_from) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_langId, (Object) pReq.usLangId_from, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            //
            if (0 != pReq.uiDevType_to) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiDevType_to, (Object) pReq.uiDevType_to, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            if (0 != pReq.uiDevType_from) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiDevType_from, (Object) pReq.uiDevType_from, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            //  2009/09/12
            for (i = 0; i < CONST_maxMsgrs_sendTo; i++) {
                if (0 != pReq.mems_to_idInfo_ui64Id[i]) {
                    if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_messengerId_to, (Object) pReq.mems_to_idInfo_ui64Id[i], Long.BYTES, ptr)))
                        return iErr;
                    total_len += len;
                }
            }
            //
            if (0 != pReq.uiObjType_src) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiObjType, (Object) pReq.uiObjType_src, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            if (0 != pReq.ulDetectedIp_from) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_ulDetectedIp, (Object) pReq.ulDetectedIp_from, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;
            total_len += len;

            return total_len;

        }


        public static PF_handler_stream2Data tmpHandler_stream2MsgRoute = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) -> {
            int iErr = -1;
            //  p0;
            qnmCommProc_mis.MSG_ROUTE pContent = (qnmCommProc_mis.MSG_ROUTE) p1;
            //  QY_CFGITEM				*	pItem		=	(  QY_CFGITEM  *  )p2;
            //  long						lVal;

            if (null == pContent) {
                return iErr;
            }

            if (uiStreamId != CONST_imCommType_msgRoute) {
                return iErr;
            }

            int tmp_cfgId;
            //memcpy(tmp_cfgId,pItem->cfgId, sizeof(short));
            tmp_cfgId = pItem.cfgId;
            short dataType = pItem.dataType;
            switch (tmp_cfgId) {
                case CONST_qnmCfgId_ulDetectedIp:
                    if (-1 == (pContent.ulDetectedIp_from = getFieldData_long(pItem))) {
                        return iErr;
                    }
                    break;
                case CONST_qnmCfgId_uiObjType:
                    if (-1 == (pContent.uiObjType_src = getFieldData_long(pItem))) {
                        return iErr;
                    }
                    break;
                case CONST_qnmCfgId_uiDevType_from:
                    if (-1 == (pContent.uiDevType_from = getFieldData_long(pItem))) {
                        return iErr;
                    }

                case CONST_qnmCfgId_uiDevType_to:
                    if (-1 == (pContent.uiDevType_to = getFieldData_long(pItem))) {
                        return iErr;
                    }
                    break;

                case CONST_qnmCfgId_langId:
                    if (0 > (pContent.usLangId_from = getFieldData_short( pItem))) {
                        return iErr;
                    }
                    break;
                case CONST_qnmCfgId_uiLogonId:
                    pContent.uiLogonId = getFieldData_long( pItem);
                    break;
                case CONST_qnmCfgId_uiMsgRouteId:
                    if (-1 == (pContent.uiMsgRouteId = getFieldData_long(pItem))) {
                        return iErr;
                    }
                    break;
                case CONST_qnmCfgId_messengerId_from: {
                    if (0 > (pContent.idInfo_from.ui64Id = getFieldData_l64( pItem))) {
                        return iErr;
                    }
                }
                break;
                case CONST_qnmCfgId_messengerId_to:
                     //
                    if (pContent.idInfo_to.ui64Id == 0) {
                        if (0 > (pContent.idInfo_to.ui64Id = getFieldData_l64( pItem))) {
                            return iErr;
                        }
                    } else {
                        if (pContent.tmpInternal_usCnt_mems_to >= CONST_maxMsgrs_sendTo) {
                            Log.i("stream2MsgRoute: usCnt_mems_to >= mycountof(  mems_to  )");
                            break;
                        }
                        if (0 > (pContent.mems_to_idInfo_ui64Id[pContent.tmpInternal_usCnt_mems_to] = getFieldData_l64( pItem))) {
                            return iErr;
                        }
                        pContent.tmpInternal_usCnt_mems_to++;
                    }
                    break;
                default:
                    break;
            }
            iErr = 0;

            return iErr;
        };


        public static int imHtmlContent2Stream(int uiStreamId, qnmCommProc_mis.IM_HTML_CONTENT pReq, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;
            int len = -1;
            if (null == ptr) return -1;
            if (null == pReq) return -1;
            int total_len = ptr.position();

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;
            total_len += len;
            //  2014/07/11
            if (0 != pReq.ucbResp) {
                if (0 > (len = data2Stream(CONST_qyDataType_char, CONST_qnmCfgId_ucbResp, pReq.ucbResp, 0, ptr))) {
                    return iErr;
                }
                total_len += len;
            }
            //  2015/02/16
            if (pReq.idInfo_logicalPeer.ui64Id != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_idInfo_logicalPeer, pReq.idInfo_logicalPeer.ui64Id, Long.BYTES, ptr))) {
                    return iErr;
                }
                total_len += len;
            }

            if (0 != pReq.uiSerialNo) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiSerialNo, pReq.uiSerialNo, 0, ptr))) {
                    return iErr;
                }
                total_len += len;
            }
            if (null != pReq.wBuf) {
                if (0 > (len = data2Stream(CONST_qyDataType_utf8Str, CONST_qnmCfgId_hint, pReq.wBuf, mylstrlen(pReq.wBuf), ptr))) {
                    return iErr;
                }
                total_len += len;
            }
            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;
            total_len += len;

            return total_len;
        }


        public static PF_handler_stream2Data tmpHandler_stream2ImHtmlContent = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) -> {
            int iErr = -1;
            //  p0;
            qnmCommProc_mis.IM_HTML_CONTENT pContent = (qnmCommProc_mis.IM_HTML_CONTENT) p1;
            //  QY_CFGITEM				*	pItem		=	(  QY_CFGITEM  *  )p2;
            //  long						lVal;

            if (null == pContent) return iErr;

            if (uiStreamId != CONST_imCommType_htmlContent) return iErr;

            short dataType = pItem.dataType;
            switch (pItem.cfgId) {
                case CONST_qnmCfgId_start:
                    pContent.uiType = uiStreamId;
                    break;
                case CONST_qnmCfgId_ucbResp:
                    if (dataType != CONST_qyDataType_byte) return -1;
                    pContent.ucbResp = ((QY_CFGITEM_BYTE)pItem).ucVal;
                    break;
                case CONST_qnmCfgId_idInfo_logicalPeer:
                    if (dataType != CONST_qyDataType_l64) return -1;
                    pContent.idInfo_logicalPeer.ui64Id = ((QY_CFGITEM_L64)pItem).ulVal;
                    break;
                case CONST_qnmCfgId_hint:
                    if (dataType != CONST_qyDataType_utf8Str) return -1;
                    pContent.wBuf = ((QY_CFGITEM_STR)pItem).str;
                    break;
                case CONST_qnmCfgId_uiSerialNo:
                    if (dataType != CONST_qyDataType_long) return -1;
                    pContent.uiSerialNo = ((QY_CFGITEM_L32)pItem).ulVal;
                    break;
                default:
                    break;
            }
            iErr = 0;

            return iErr;
        };


        //
        public static PF_handler_stream2Data tmpHandler_stream2RefreshRecentFriendsResp = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) -> {
            int iErr = -1;
            //  p0;
            qnmCommProc_mis.REFRESH_RECENTFRIENDS_RESP pResp = (qnmCommProc_mis.REFRESH_RECENTFRIENDS_RESP) p1;
            //  QY_CFGITEM				*	pItem		=	(  QY_CFGITEM  *  )p2;
            //  long						lVal;

            if (null == pResp) return iErr;

            if (uiStreamId != CONST_imCommType_refreshRecentFriendsResp) return iErr;
            if  (  pResp.usCnt_mems  >  pResp.mems.length  )  return iErr;

            int  index =  0;
            int lVal;

            short dataType = pItem.dataType;
            switch (pItem.cfgId) {
                case CONST_qnmCfgId_start:
                    pResp.uiType = uiStreamId;
                    break;
                case  CONST_qnmCfgId_tLastRefreshedTime:  //  Ö»ÔÚ½áÊø°üÖÐÆð×÷ÓÃ
                      pResp.tLastRefreshedTime_misServ=getFieldData_l64(pItem);
                    break;
                case  CONST_qnmCfgId_usCnt:
                    pResp.usCnt=getFieldData_short(  pItem);
                    //
                    if  (  0!=pResp.usCnt)  {
                        Log.d("anc","mmm l1022");
                    }
                    //
                    if  (  pResp.usCnt  >  pResp.mems.length  )  {
                        return  iErr;
                    }
                    //
                    break;
                case  CONST_qnmCfgId_mem:
                    if  (  pResp.usCnt_mems  >=  pResp.usCnt  ) {
                        return  iErr;
                    }
                    pResp.usCnt_mems  ++  ;
                    break;
                case  CONST_qnmCfgId_messengerId:
                    index  =  pResp.usCnt_mems  -  1;
                    if  (  index  <  0  ) {
                        return  iErr;
                    }
                    pResp.mems[index].ui64Id=getFieldData_l64(  pItem);
                    break;
                case  CONST_qnmCfgId_usRunningStatus:
                    index  =  pResp.usCnt_mems  -  1;
                    if  (  index  <  0  ) {
                        return  iErr;
                    }
                    pResp.mems[index].usRunningStatus=getFieldData_short(  pItem);
                    break;
                case  CONST_qnmCfgId_ulIp:
                    index  =  pResp.usCnt_mems  -  1;
                    if  (  index  <  0  ) {
                        return iErr;
                    }
                    lVal = getFieldData_long(  pItem);
                    pResp.mems[index].ulIp  =  lVal;
                    break;
                case  CONST_qnmCfgId_ulDetectedIp:
                    index = pResp.usCnt_mems - 1;
                    if (index < 0) {
                        return iErr;
                    }
                    lVal = getFieldData_long(pItem);
                    pResp.mems[index].ulDetectedIp = lVal;

                    break;
                case  CONST_ancCfgId_conf_ui64Id:
                    index = pResp.usCnt_mems - 1;
                    if (index < 0) {
                        return  iErr;
                    }
                    long l64;
                    l64 = getFieldData_l64(pItem);
                    if  (  l64  <  0)  {
                        return  iErr;
                    }
                    //
                    pResp.mems[index].conf_ui64Id  =  l64;
                    //
                    break;


                default:
                    break;
            }
            iErr = 0;

            return iErr;
        };



        /// <summary>
        /// ///////////////////////////////////////////////////////////////////////////////
        /// </summary>
        /// <param name="pConfCfg"></param>
        /// <param name="ppBuf"></param>
        /// <param name="puiBufSize"></param>
        /// <returns></returns>
        ///


        public static int qyhtonVideoHeader(qyCommProc_open.QY_VIDEO_HEADER pVh, ByteBuffer ptr) {
            if (null == pVh) return -1;
            qyCommProc_open.BITMAPINFOHEADER p = pVh.bih;
            byte[] data = p.toByteArray();
            if (0 == p.biSize)
                p.biSize = data.length;

            ptr.put(data);
            return p.biSize;
        }

        public static qyCommProc_open.BITMAPINFOHEADER qyntohVideoHeader(ByteBuffer buffer) {
            if (null == buffer) return null;
            qyCommProc_open.BITMAPINFOHEADER p = new qyCommProc_open.BITMAPINFOHEADER();
            p.biSize =  buffer.getInt();
            p.biWidth = buffer.getInt();
            p.biHeight = buffer.getInt();
            p.biPlanes = buffer.getShort();
            p.biBitCount = buffer.getShort();
            p.biCompression = buffer.getInt();
            p.biSizeImage = buffer.getInt();
            p.biXPelsPerMeter = buffer.getInt();
            p.biYPelsPerMeter = buffer.getInt();
            p.biClrUsed = buffer.getInt();
            p.biClrImportant = buffer.getInt();
            return p;
        }


        /////////////////

        public static int qyhtonAudioHeader(qyCommProc_open.QY_AUDIO_HEADER pAh) {
            if (null == pAh) return -1;
            qyCommProc_open.WAVEFORMATEX p = pAh.wfx;
            if (p.cbSize > bufSize_bExtraData) {
                Log.e("qyhtonAudioHeader failed: cbSize too large, " + p.cbSize);
                return -1;
            }
            int biSize = p.cbSize + 18;//sizeof(WAVEFORMATEX);

//            MACRO_htons(p.wFormatTag);        // format type
//            MACRO_htons(p.nChannels);         // number of channels (i.e. mono, stereo...)
//            MACRO_htoni(p.nSamplesPerSec);    // sample rate
//            MACRO_htoni(p.nAvgBytesPerSec);   // for buffer estimation
//            MACRO_htons(p.nBlockAlign);       // block size of data
//            MACRO_htons(p.wBitsPerSample);    // Number of bits per sample of mono data
//            MACRO_htons(p.cbSize);            // The count in bytes of the size of

            return biSize;
        }

        public static qyCommProc_open.WAVEFORMATEX qyntohAudioHeader(ByteBuffer buffer) {
            if (null == buffer) return null;
            qyCommProc_open.WAVEFORMATEX p = new qyCommProc_open.WAVEFORMATEX();
            p.wFormatTag = buffer.getShort();
            p.nChannels = buffer.getShort();
            p.nSamplesPerSec = buffer.getInt();
            p.nAvgBytesPerSec = buffer.getInt();
            p.nBlockAlign = buffer.getShort();
            p.wBitsPerSample = buffer.getShort();
            p.cbSize = buffer.getShort();
            return p;
        }


        //  2014/11/07
        public static int tmp_htonTranVideo(qnmCommProc_mis.TRAN_video pTranVideo, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;
            int total_len = 0;

            if (0 != pTranVideo.uiTranNo_openAvDev) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiTranNo_openVideoDev, (Object) pTranVideo.uiTranNo_openAvDev, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            //
            if (0 != pTranVideo.compressor.ucCompressors) {  //  200903/30
                if (0 > (len = data2Stream(CONST_qyDataType_char, CONST_qnmCfgId_ucVideoCompressors, (Object) pTranVideo.compressor.ucCompressors, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            if (0 != pTranVideo.compressor.iFourcc) {
                if (0 > (len = data2Stream(CONST_qyDataType_l32, CONST_qnmCfgId_iFourcc, (Object) pTranVideo.compressor.iFourcc, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            if (0 != pTranVideo.compressor.ucbNeedKeyFrameFirst) {
                if (0 > (len = data2Stream(CONST_qyDataType_byte, CONST_qnmCfgId_ucbNeedKeyFrameFirst, (Object) pTranVideo.compressor.ucbNeedKeyFrameFirst, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            //
            if (0 != pTranVideo.compressor.usMaxFps_toShareBmp) {   //  2014/08/07
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usMaxFps_toShareBmp, (Object) pTranVideo.compressor.usMaxFps_toShareBmp, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            int tmpLen;
            qyCommProc_open.QY_VIDEO_HEADER vh = pTranVideo.vh_compress;
            byte[] data = vh.toByteArray();
            if (0 > (len = data2Stream(CONST_qyDataType_data, CONST_qnmCfgId_bih_compress, data, data.length, ptr)))
                return iErr;
            total_len += len;

            //  2014/09/23
            if (bSupported_sendVhDec()) {
                data = pTranVideo.vh_decompress.toByteArray();
                if (0 > (len = data2Stream(CONST_qyDataType_data, CONST_qnmCfgId_bih_decompress, data, data.length, ptr)))
                    return iErr;
                total_len += len;
            }

            //  2014/03/30
            if (0 != pTranVideo.vh_stream.usCnt) {
                ByteBuffer spsPps = ByteBuffer.allocate(bufSize_1024);
                int spsLen = 0;
                if (0 > (spsLen = makeSpsPps(pTranVideo.vh_stream, spsPps))) {
                    Log.e("htonAvTranInfo failed: makeSpsPps failed");
                    return iErr;
                }
                if (0 > (len = data2Stream(CONST_qyDataType_data, CONST_qnmCfgId_spsPps, spsPps.array(), spsLen, ptr)))
                    return iErr;
                total_len += len;
            }
            return total_len;
        }

        public static class TMP_buf_vh {
            public byte[] buf = new byte[bufSize_vh];
        }

        public static int tmp_ntohTranVideo(QY_CFGITEM pItem, qnmCommProc_mis.TRAN_video pTranVideo, boolean pbProcessed) {
            int iErr = -1;
            int tmpLen;
            int bufSize = bufSize_vh;
            ByteBuffer buf_vh = ByteBuffer.allocate(bufSize);
            boolean bProcessed = false;

            int tmp_cfgId = pItem.cfgId;
            short dataType = pItem.dataType;
            if (null != pTranVideo) {
                bProcessed = true;
                switch (tmp_cfgId) {
                    case CONST_qnmCfgId_uiTranNo_openVideoDev:
                        if (-1 == (pTranVideo.uiTranNo_openAvDev = getFieldData_long(pItem))) {
                            return iErr;
                        }
                        break;

                    case CONST_qnmCfgId_ucVideoCompressors: //  2009/03/30
                        if (0 > (pTranVideo.compressor.ucCompressors = getFieldData_byte(pItem))) {
                            return iErr;
                        }
                        break;
                    case CONST_qnmCfgId_iFourcc:
                        if (0 > (pTranVideo.compressor.iFourcc = getFieldData_l32(pItem))) {
                            return iErr;
                        }
                        break;
                    case CONST_qnmCfgId_ucbNeedKeyFrameFirst:
                        if (0 > (pTranVideo.compressor.ucbNeedKeyFrameFirst = getFieldData_byte(pItem))) {
                            return iErr;
                        }
                        break;
                    case CONST_qnmCfgId_usMaxFps_toShareBmp:    //  2014/08/07
                        if (0 > (pTranVideo.compressor.usMaxFps_toShareBmp = getFieldData_short(pItem))) {
                            return iErr;
                        }
                        break;
                    case CONST_qnmCfgId_bih_compress:
                        if (null  == ( buf_vh = getFieldData_data(pItem))) {
                            return iErr;
                        }
                        tmpLen = buf_vh.capacity();
                        if (tmpLen > Consts.bufSize_vh) {
                            return iErr;
                        }
                        if ((pTranVideo.vh_compress.bih = qyntohVideoHeader(buf_vh)) == null) {
                            return iErr;
                        }
                        break;
                    case CONST_qnmCfgId_bih_decompress:
                        if (null  == ( buf_vh = getFieldData_data(pItem))) {
                            return iErr;
                        }
                        tmpLen = buf_vh.capacity();
                        if (tmpLen > bufSize_vh) {
                            return iErr;                                            //  impossible
                        }
                        if ((pTranVideo.vh_decompress.bih = qyntohVideoHeader(buf_vh))==null) {
                            return iErr;
                        }
                        break;
                    case CONST_qnmCfgId_spsPps: {   //  2014/03/30
                        if (null  == ( buf_vh = getFieldData_data(pItem))) {
                            return iErr;
                        }
                        tmpLen = buf_vh.capacity();
                        if (tmpLen > bufSize_vh) {
                            return iErr;
                        }
                        //  2017/10/09
                        int iFourcc = pTranVideo.compressor.iFourcc;// fourccStr2i(pTranVideo->compressor.common.fourccStr);
                        if (0 == iFourcc) {
                            iFourcc = (int) pTranVideo.vh_stream.dwFourcc;
                        }
                        //
                        qyCommProc_open.VH_stream pVh_stream = pTranVideo.vh_stream;
                        //
                        //  2017/10/10
                        if (0 >  parseSpsPps1(iFourcc, buf_vh, pVh_stream)) {
                            Log.e("tmp_ntohTranVideo: parseSpsPps1 failed");
                            return iErr;
                        }
                    }
                    break;
                    default:
                        //
                        bProcessed = false;

                        break;
                }
            }


            iErr = 0;

            if (0 == iErr) {
                if (false != pbProcessed)
                {
                    pbProcessed = bProcessed;
                }
            }
            return iErr;
        }


        /// <summary>
        /// ////////////////////////////////////////////////////////////
        /// </summary>
        /// <param name="pConfCfg"></param>
        /// <param name="ppBuf"></param>
        /// <param name="puiBufSize"></param>
        /// <returns></returns>
        ///

        public static int tmp_htonTranAudio(qnmCommProc_mis.TRAN_audio pTranAudio, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;
            int total_len = 0;
            //  2014/11/07

            if (0 != pTranAudio.uiTranNo_openAvDev) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiTranNo_openAudioDev, (Object) pTranAudio.uiTranNo_openAvDev, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            if (0 != pTranAudio.compressor.ucCompressors) {       //  2009/05/18
                if (0 > (len = data2Stream(CONST_qyDataType_char, CONST_qnmCfgId_ucAudioCompressors, (Object) pTranAudio.compressor.ucCompressors, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            byte[] data = pTranAudio.ah_compress.wfx.toByteArray();
            if ((0 > (len = data2Stream(CONST_qyDataType_data, CONST_qnmCfgId_wf_compress, data, data.length, ptr)))) {
                return iErr;
            }
            total_len += len;

            data = pTranAudio.ah_decompress.wfx.toByteArray();
            if ((0 > (len = data2Stream(CONST_qyDataType_data, CONST_qnmCfgId_wf_decompress, data, data.length, ptr)))) {
                return iErr;
            }
            total_len += len;

            return total_len;
        }

        public static int tmp_ntohTranAudio(QY_CFGITEM pItem, qnmCommProc_mis.TRAN_audio pTranAudio, boolean pbProcessed) {
            int iErr = -1;
            int tmpLen;
            int bufSize = bufSize_ah;
            ByteBuffer buf = ByteBuffer.allocate(bufSize);

            int tmp_cfgId = pItem.cfgId;
            if (null != pTranAudio) {
                switch (tmp_cfgId) {
                    case CONST_qnmCfgId_uiTranNo_openAudioDev:
                        if (-1 == (pTranAudio.uiTranNo_openAvDev = getFieldData_long(pItem)))
                            return iErr;
                        break;
                    case CONST_qnmCfgId_ucAudioCompressors:     //  2009/05/18
                        if (0 >( pTranAudio.compressor.ucCompressors = getFieldData_byte(pItem)))
                            return iErr;
                        break;
                    case CONST_qnmCfgId_wf_compress:
                        if (null  == ( buf = getFieldData_data(pItem))) return iErr;
                        tmpLen = buf.capacity();
                        if (tmpLen > bufSize) return iErr;

                        if ( (pTranAudio.ah_compress.wfx = qyntohAudioHeader(buf)) == null)  return iErr;
                        break;
                    case CONST_qnmCfgId_wf_decompress:  //  2009/04/27
                        if (null  == ( buf = getFieldData_data(pItem))) return iErr;
                        tmpLen = buf.capacity();
                        if (tmpLen > bufSize) return iErr;
                        if ((pTranAudio.ah_decompress.wfx = qyntohAudioHeader(buf)) == null) return iErr;
                        break;
                    default:
                        break;
                }
            }
            iErr = 0;
            return iErr;
        }


        /////////////////////
        public static int tmp_htonConfHgInfo(qnmCommProc_mis.CONF_hg_info pReq_hgInfo, ByteBuffer ptr) {
            int len = -1;
            int total_len = 0;
            if (pReq_hgInfo.bFlow512k) {
                if (0 > (len = data2Stream(CONST_qyDataType_char, CONST_qnmCfgId_bFlow512k_hg, (Object) (pReq_hgInfo.bFlow512k ? 1 : 0), 0, ptr)))
                    return len;
                total_len += len;
            }
            return total_len;
        }

        //
        public static int tmp_ntohConfHgInfo(QY_CFGITEM pItem, qnmCommProc_mis.CONF_hg_info pContent, boolean pbProcessed) {
            int iErr = -1;

            boolean bProcessed = false;

            if (null == pContent) return iErr;

            int p_cfgId = pItem.cfgId;
            switch (p_cfgId) {
                case  CONST_qnmCfgId_zcr_hg:
                    if (pContent.tmpInternal.cnt_zcrs > pContent.zcrs.mems_idInfo.length) return  iErr;
                    long lVal;
                    lVal  =  getFieldData_l64(pItem);
                    //, (__int64*)&pContent->zcrs.mems[pContent->tmpInternal.cnt_zcrs].idInfo.ui64Id))goto  errLabel;
                    pContent.zcrs.mems_idInfo[pContent.tmpInternal.cnt_zcrs]=lVal;
                    pContent.tmpInternal.cnt_zcrs++;
                    break;

                case CONST_qnmCfgId_bFlow512k_hg: {
                    byte tmp_c = 0;
                    if (0 >(tmp_c = qyFuncs.getFieldData_byte(pItem))) return iErr;
                    pContent.bFlow512k = tmp_c != 0;
                }
                break;
                default:
                    break;
            }

            iErr = 0;


            return iErr;
        }


        //////////////////
        public static int tmp_htonTranConfCfg(qnmCommProc_mis.TRAN_conf_cfg pConfCfg, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;
            int len = -1;
            int total_len = 0;
            if (null != pConfCfg) {
                //  2014/09/23
                if (0 != pConfCfg.usMaxSpeakers) {
                    if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usMaxSpeakers, (Object) pConfCfg.usMaxSpeakers, 0, ptr)))
                        return iErr;
                    total_len += len;
                }

                //
                if (0 != pConfCfg.conf.usMaxFps_toShareBmp_conf) {
                    if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usMaxFps_toShareBmp_conf, (Object) pConfCfg.conf.usMaxFps_toShareBmp_conf, 0, ptr)))
                        return iErr;
                    total_len += len;
                }
                //  2014/08/15
                if (0 != pConfCfg.conf.iWidth_pic_conf) {
                    if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_iWidth_pic_conf, (Object) pConfCfg.conf.iWidth_pic_conf, 0, ptr)))
                        return iErr;
                    total_len += len;
                }
                if (0 != pConfCfg.conf.iHeight_pic_conf) {
                    if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_iHeight_pic_conf, (Object) pConfCfg.conf.iHeight_pic_conf, 0, ptr)))
                        return iErr;
                    total_len += len;
                }

                //
                //
                if (0!=pConfCfg.conf.conf_ucAudioCompressors) {
                    if (0>(len = data2Stream(CONST_qyDataType_byte, CONST_qnmCfgId_conf_ucAudioCompressors, pConfCfg.conf.conf_ucAudioCompressors, 0, ptr)) )                     {
                        return iErr;
                    }
                    //
                    total_len   += len;
                }

                //
                if (0 != pConfCfg.conf.conf_iFourcc) {
                    if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_conf_iFourcc, (Object) pConfCfg.conf.conf_iFourcc, 0, ptr)))
                        return iErr;
                    total_len += len;
                }
                if (0!=pConfCfg.conf.conf_bitrateInKbps_ul) {
                    if (0>(len=data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_conf_bitrateInKbps_ul, (Object)pConfCfg.conf.conf_bitrateInKbps_ul, 0, ptr)))
                        return iErr;
                    total_len+=len;
                }

                //
                if  (  0!=pConfCfg.conf.conf_ucb100k)  {
                    if(0>(len=data2Stream(CONST_qyDataType_byte,CONST_qnmCfgId_conf_ucb100k,(Object) pConfCfg.conf.conf_ucb100k,0,ptr))) {
                        return iErr;
                    }
                    total_len+=len;
                }

                //
                if (0 != pConfCfg.conf.confCtrl.ucbConfCtrl_starter) {
                    if (0 > (len = data2Stream(CONST_qyDataType_char, CONST_qnmCfgId_ucbConfCtrl_starter, (Object) pConfCfg.conf.confCtrl.ucbConfCtrl_starter, 0, ptr)))
                        return iErr;
                    total_len += len;
                }

                //  2017/08/02
                if (0 != pConfCfg.ucbVideoConference) {
                    if (0 > (len = data2Stream(CONST_qyDataType_char, CONST_qnmCfgId_ucbVideoConference, pConfCfg.ucbVideoConference, 0, ptr)))
                        return iErr;
                    total_len += len;
                }
                //  2017/08/25
                if (0 != pConfCfg.usConfType) {
                    if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usConfType, pConfCfg.usConfType, 0, ptr)))
                        return iErr;
                    total_len += len;
                }

                if (pConfCfg.idInfo_initiator.ui64Id != 0) {
                    if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_idInfo_initiator, pConfCfg.idInfo_initiator.ui64Id, Long.BYTES, ptr)))
                        return iErr;
                    total_len += len;
                }

                if (0 > (len = tmp_htonConfHgInfo(pConfCfg.hgInfo, ptr))) return iErr;
                total_len += len;
            }
            return total_len;

        }


        public static int tmp_ntohTranConfCfg(QY_CFGITEM pItem, qnmCommProc_mis.TRAN_conf_cfg pConfCfg, boolean pbProcessed) {
            int iErr = -1;
            int tmpLen;
            //char buf[sizeof(QY_VIDEO_HEADER) + 1024];
            boolean bProcessed = false;

            if (null != pConfCfg) {
                bProcessed = true;
                short dataType = pItem.dataType;
                switch (pItem.cfgId) {
                    case CONST_qnmCfgId_usMaxSpeakers:  //  2009/05/31
                        if  (  false ) {
                            if (dataType != CONST_qyDataType_short) {
                                return -1;
                            }
                        }
                        //pConfCfg.usMaxSpeakers = ((QY_CFGITEM_SHORT)pItem).usVal;
                        pConfCfg.usMaxSpeakers = getFieldData_short(pItem);
                        break;
                    case CONST_qnmCfgId_usMaxFps_toShareBmp_conf:
                        if  (  false  ) {
                            if (dataType != CONST_qyDataType_short) {
                                return -1;
                            }
                        }
                        //pConfCfg.conf.usMaxFps_toShareBmp_conf = ((QY_CFGITEM_SHORT)pItem).usVal;
                        pConfCfg.conf.usMaxFps_toShareBmp_conf = getFieldData_short(pItem);
                        break;
                    case CONST_qnmCfgId_iWidth_pic_conf:
                        if  (  false  ) {
                            if (dataType != CONST_qyDataType_long) {
                                return -1;
                            }
                        }
                        //pConfCfg.conf.iWidth_pic_conf = ((QY_CFGITEM_L32)pItem).ulVal;
                        pConfCfg.conf.iWidth_pic_conf = getFieldData_l32(pItem);
                        break;
                    case CONST_qnmCfgId_iHeight_pic_conf:
                        if  (  false ) {
                            if (dataType != CONST_qyDataType_long) {
                                return -1;
                            }
                        }
                        //pConfCfg.conf.iHeight_pic_conf = ((QY_CFGITEM_L32)pItem).ulVal;
                        pConfCfg.conf.iHeight_pic_conf = getFieldData_l32(pItem);
                        break;
                    case  CONST_qnmCfgId_conf_ucAudioCompressors:
                        pConfCfg.conf.conf_ucAudioCompressors=getFieldData_byte(pItem);
                        break;
                    case CONST_qnmCfgId_conf_iFourcc:
                        if  (  false ) {
                            if (dataType != CONST_qyDataType_long) {
                                return -1;
                            }
                        }
                        //pConfCfg.conf.conf_iFourcc = ((QY_CFGITEM_L32)pItem).ulVal;
                        pConfCfg.conf.conf_iFourcc = getFieldData_l32(pItem);
                        break;
                    //
                    case  CONST_qnmCfgId_conf_bitrateInKbps_ul:
                        if  (  false  ) {
                            if (dataType != CONST_qyDataType_l32) {
                                return -1;
                            }
                        }
                        //pConfCfg.conf.conf_bitrateInKbps_ul  =((QY_CFGITEM_L32)pItem).ulVal;
                        pConfCfg.conf.conf_bitrateInKbps_ul  =getFieldData_l32(pItem);
                        break;

                    case  CONST_qnmCfgId_conf_ucb100k:
                        pConfCfg.conf.conf_ucb100k=getFieldData_byte(pItem);
                        break;
                    case CONST_qnmCfgId_ucbConfCtrl_starter:
                        if(  false ) {
                            if (dataType != CONST_qyDataType_byte) {
                                return -1;
                            }
                        }
                        //pConfCfg.conf.confCtrl.ucbConfCtrl_starter = ((QY_CFGITEM_BYTE)pItem).ucVal;
                        pConfCfg.conf.confCtrl.ucbConfCtrl_starter = getFieldData_byte(pItem);
                        break;
                    case CONST_qnmCfgId_ucbVideoConference:
                        if  (  false  ) {
                            if (dataType != CONST_qyDataType_byte) {
                                return -1;
                            }
                        }
                        //pConfCfg.ucbVideoConference = ((QY_CFGITEM_BYTE)pItem).ucVal;
                        pConfCfg.ucbVideoConference = getFieldData_byte(pItem);
                        break;
                    case CONST_qnmCfgId_usConfType:
                        if  (  false ) {
                            if (dataType != CONST_qyDataType_short) {
                                return -1;
                            }
                        }
                        //pConfCfg.usConfType = ((QY_CFGITEM_SHORT)pItem).usVal;
                        pConfCfg.usConfType = getFieldData_short(pItem);
                        break;
                    case CONST_qnmCfgId_idInfo_initiator:
                        if  (  false ) {
                            if (dataType != CONST_qyDataType_l64) {
                                return -1;
                            }
                            pConfCfg.idInfo_initiator.ui64Id = ((QY_CFGITEM_L64) pItem).ulVal;
                        }
                        //
                        pConfCfg.idInfo_initiator.ui64Id = getFieldData_l64(pItem);
                        //
                        break;
                    default:
                        tmp_ntohConfHgInfo(pItem, pConfCfg.hgInfo, bProcessed);
                        break;
                }
            }
            iErr = 0;
            return iErr;
        }


        //
        public static int  tmp_htonMuxStreamsCfg(muxStream_open.MuxStreamsCfg pObj, ByteBuffer ptr)
        {
            int					iErr = -1;
            int					i = 0;
            int len = -1;
            int total_len = 0;

            //
            if (0==pObj.usCnt )
            {
                return  0;
            }

            do {
                //
                len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, CONST_ancCommType_muxStreamsCfg, 0, ptr);
                if (len <= 0)  break;
                total_len += len;


                if (pObj.usCnt!=0) {

                    if (pObj.usCnt > pObj.mems.length) {
                        break;
                    }

                    len =data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usCnt, pObj.usCnt, 0, ptr);
                    if(len<=0)  break;
                    total_len += len;


                    //
                    if (pObj.default_usIndex >= pObj.usCnt)  {
                        break;
                    }
                    if (pObj.default_usIndex!=0) {
                        len =data2Stream(CONST_qyDataType_short, CONST_ancCfgId_default, pObj.default_usIndex, 0, ptr);
                        if  (  len < 0)  break;
                        total_len += len;
                    }

                    //
                    for (i = 0; i < pObj.usCnt; i++) {
                        muxStream_open.MuxStreamCfg pMem = pObj.mems[i];

                        len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, CONST_imCommType_mem, 0, ptr);
                        if  (  len  < 0) break;
                        total_len += len;

                        //
                        if (pMem.usIndex!=0) {
                            len=data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_index,  pMem.usIndex, 0, ptr);
                            if  ( len < 0)break;
                            total_len += len;
                        }

                        if (pMem.usType!=0) {
                            len = data2Stream(CONST_qyDataType_short, CONST_ancCfgId_type, (short)pMem.usType, 0, ptr);
                            if( len < 0)  break;
                            total_len += len;
                        }


                        //
                        if (pMem.output.a.ucCompressors!=0) {
                            len = data2Stream(CONST_qyDataType_byte, CONST_qnmCfgId_ucAudioCompressors, pMem.output.a.ucCompressors, 0, ptr);
                            if  ( len<0)break;
                            total_len += len;
                        }
                        if (pMem.output.a.wFormatTag!=0) {
                            len=data2Stream(CONST_qyDataType_short, CONST_ancCfgId_wFormatTag, pMem.output.a.wFormatTag, 0, ptr);
                            if(len<0)break;
                            total_len += len;
                        }
                        if (pMem.output.v.usW!=0) {
                            len=data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_width, pMem.output.v.usW, 0, ptr);
                            if(len<0)break;
                            total_len += len;
                        }
                        if (pMem.output.v.usH!=0) {
                            len=data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_height, pMem.output.v.usH, 0, ptr);
                            if(len<0)break;
                            total_len += len;
                        }
                        if (pMem.output.v.ucCompressors!=0) {
                            len=data2Stream(CONST_qyDataType_byte, CONST_qnmCfgId_ucVideoCompressors, pMem.output.v.ucCompressors, 0, ptr);
                            if(len<0)break;
                            total_len += len;
                        }
                        if (pMem.output.v.iFourcc!=0) {
                            len=data2Stream(CONST_qyDataType_l32, CONST_qnmCfgId_iFourcc, pMem.output.v.iFourcc, 0, ptr);
                            if(len<0)break;
                            total_len += len;

                        }

                        //
                        len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, 0, 0, ptr);
                        if(len<0) break;
                        total_len += len;



                    }


                    //
                }


                //
                len =data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, 0, 0, ptr);
                if  (  len < 0 )break;
                total_len += len;

                //
                iErr = 0;

            } while(false);

            //
            return  total_len;
        }


        //
        //int  tmp_stream2MuxStreamsCfg(CTX_stream2Data pCtx, Object p0, Object p1, int  uiStreamId, QY_CFGITEM_ntoh_U pItem)
        public static int  tmp_stream2MuxStreamsCfg(CTX_stream2Data pCtx, Object p0, Object p1, int  uiStreamId, QY_CFGITEM pItem)
        {
            int  iErr = -1;
            muxStream_open.MuxStreamsCfg pObj = (muxStream_open.MuxStreamsCfg)p1;

            short  tmp_cfgId;
            //memcpy(&tmp_cfgId, &pItem->head.cfgId, sizeof(short));
            tmp_cfgId=pItem.cfgId;
            short tmp_dataType =pItem.dataType;

            //
            boolean tmp_bErr = false;

                    //
            switch (uiStreamId) {
                case  CONST_ancCommType_muxStreamsCfg:
                    switch (tmp_cfgId) {
                        case  CONST_qnmCfgId_usCnt:
                            pObj.usCnt=getFieldData_short(pItem);
                            //
                            if (pObj.usCnt > pObj.mems.length) {
                                //showInfo_open(0, 0, 0, _T("tmp_stream2MuxStreamsCfg failed, usCnt > cntof(mems)"));
                                tmp_bErr=true;
                                break;
                            }
                            //
                            pObj.tmp_usCnt_mems = 0;
                            //
                            break;
                        case  CONST_ancCfgId_default:
                            pObj.default_usIndex=getFieldData_short(pItem);
                            //
                            if (pObj.default_usIndex >= pObj.usCnt) {
                                //showInfo_open(0, 0, 0, _T("default_usIndex >= usCnt. l3052"));
                                tmp_bErr=true;
                                break;
                            }
                            //
                            break;
                        default:
                            break;
                    }
                    //
                    if  (tmp_bErr)  {
                        break;
                    }
                    break;
                case  CONST_ancCommType_mem:

                    if (tmp_cfgId == CONST_qnmCfgId_start) {
                        pObj.tmp_usCnt_mems++;
                        break;
                    }
                    muxStream_open.MuxStreamCfg pMem;
                    if (pObj.tmp_usCnt_mems > (pObj.mems.length)) {
                        tmp_bErr=true;
                        break;
                    }
                    pMem = pObj.mems[pObj.tmp_usCnt_mems - 1];
                    //
                    switch (tmp_cfgId) {

                        case  CONST_qnmCfgId_index:
                            pMem.usIndex=getFieldData_short(pItem);
                            break;

                        case  CONST_ancCfgId_type:
                            pMem.usType=getFieldData_short(pItem);
                            break;
                        case  CONST_qnmCfgId_ucAudioCompressors:
                            pMem.output.a.ucCompressors=getFieldData_byte(pItem);
                            break;
                        case  CONST_ancCfgId_wFormatTag:
                            pMem.output.a.wFormatTag=getFieldData_short(pItem);
                            break;
                        case  CONST_qnmCfgId_width:
                            pMem.output.v.usW=getFieldData_short(pItem);
                            break;
                        case  CONST_qnmCfgId_height:
                            pMem.output.v.usH=getFieldData_short(pItem);
                            break;
                        case  CONST_qnmCfgId_ucVideoCompressors:
                            pMem.output.v.ucCompressors=getFieldData_byte(pItem);
                            break;
                        case  CONST_qnmCfgId_iFourcc:
                            pMem.output.v.iFourcc=getFieldData_long(pItem);
                            break;
                        //

                        default:
                            break;
                    }
                    //
                    break;
                default:
                    break;

            }

            iErr = 0;

            errLabel:

            return  iErr;
        }






        //
        public static int tmp_htonAvTranInfo(qnmCommProc_mis.AV_TRAN_INFO pTranInfo, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = 0;
            int total_len = 0;
            qnmCommProc_mis.AV_tranInfo_head pHead = pTranInfo.head;
            qnmCommProc_mis.TRAN_audio pTranAudio = pTranInfo.audio;
            qnmCommProc_mis.TRAN_video pTranVideo = pTranInfo.video;

            if (null != pHead) {
                if (0 != pHead.shClientVer) {
                    if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_shVer, pHead.shClientVer, 0, ptr)))
                        return iErr;
                    total_len += len;
                }
            }

            //  2014/11/07
            if (null != pTranVideo) {
                if (0 != pTranVideo.uiTranNo_openAvDev) {
                    if (0 > (len = tmp_htonTranVideo(pTranVideo, ptr)))
                        return iErr;
                    total_len += len;
                }
            }

            //  2014/11/07
            if (null != pTranAudio) {
                if (0 != pTranAudio.uiTranNo_openAvDev) {
                    if (0 > (len = tmp_htonTranAudio(pTranAudio, ptr)))
                        return iErr;
                    total_len += len;
                }
            }

            //  2015/03/12
            if (0 != pTranInfo.taskAvProps.v.ucAvFlg) {
                if (0 > (len = data2Stream(CONST_qyDataType_char, CONST_qnmCfgId_ucAvFlg, pTranInfo.taskAvProps.v.ucAvFlg, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            return total_len;

        }

        //
        public static int tmp_ntohAvTranInfo(QY_CFGITEM pItem, qnmCommProc_mis.AV_TRAN_INFO pTranInfo, boolean pbProcessed) {
            int iErr = -1;
            int tmpLen;
            //char buf[sizeof(QY_VIDEO_HEADER) + 1024];
            boolean bProcessed = false;

            //
            qnmCommProc_mis.AV_tranInfo_head pHead = pTranInfo.head;
            qnmCommProc_mis.TRAN_audio pTranAudio = pTranInfo.audio;
            qnmCommProc_mis.TRAN_video pTranVideo = pTranInfo.video;


            //
            int tmp_cfgId;
            //memcpy(tmp_cfgId,pItem->cfgId, sizeof(short));
            tmp_cfgId = pItem.cfgId;

            if (null != pHead) {
                if (tmp_cfgId == CONST_qnmCfgId_shVer) {
                    if (0 > (pHead.shClientVer = getFieldData_short(pItem))) {
                        return iErr;
                    }
                }
            }

            //
            if (null != pTranVideo) {
                //
                bProcessed = false;
                if (0 != tmp_ntohTranVideo(pItem, pTranVideo, bProcessed)) {
                    //
                    Log.d("qy","tmp_ntohTranVideo 1480 failed");
                    //
                    return iErr;
                }
                if (bProcessed) {
                    iErr = 0;
                    return iErr;
                }
            }


            if (null != pTranAudio) {
                //
                bProcessed = false;
                if (0 != tmp_ntohTranAudio(pItem, pTranAudio, bProcessed)) {
                    return iErr;
                }
                if (bProcessed) {
                    iErr = 0;
                    return iErr;
                }
            }

            //  2015/03/12
            if (tmp_cfgId == CONST_qnmCfgId_ucAvFlg) {
                if (0 != getFieldData_byte(pItem))
                    return iErr;
            }


            iErr = 0;
            if (!pbProcessed) pbProcessed = bProcessed;

            return iErr;
        }


        //
        public static int tmp_htonAvStream(qnmCommProc_mis.AV_stream pObj, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = 0;
            int total_len = 0;
            //
            if (pObj.idInfo.ui64Id != 0
                    && 0 == pObj.obj.resObj.uiObjType
                    && 0 == pObj.obj.tranInfo.audio.uiTranNo_openAvDev
                    && 0 == pObj.obj.tranInfo.video.uiTranNo_openAvDev) {
                return 0;
            }

            //
            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, CONST_imCommType_avStream, 0, ptr)))
                return iErr;
            total_len += len;

            if (pObj.idInfo.ui64Id != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_messengerId, pObj.idInfo.ui64Id, Long.BYTES, ptr)))
                    return iErr;
                total_len += len;
            }

            if (0 != pObj.obj.resObj.uiObjType) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiObjType, pObj.obj.resObj.uiObjType, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            if (0 != pObj.obj.resObj.usIndex_obj) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_index, pObj.obj.resObj.usIndex_obj, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            if (0 > (len = tmp_htonAvTranInfo(pObj.obj.tranInfo, ptr)))
                return iErr;
            total_len += len;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;
            total_len += len;

            return total_len;
        }

        //
        public static int tmp_ntohAvStream(QY_CFGITEM pItem, qnmCommProc_mis.AV_stream pObj, boolean pbProcessed) {
            int iErr = -1;
            int tmpLen;
            //char						buf[sizeof(  QY_VIDEO_HEADER  )  +  1024];
            boolean bProcessed = false;

            bProcessed = true;
            short dataType = pItem.dataType;
            switch (pItem.cfgId) {
                case CONST_qnmCfgId_messengerId:
                    if (0 > (pObj.idInfo.ui64Id = getFieldData_l64(pItem))) {
                        return iErr;
                    }
                    break;
                case CONST_qnmCfgId_uiObjType:
                    if (0 > (pObj.obj.resObj.uiObjType = getFieldData_long(pItem))) {
                        return iErr;
                    }
                    break;
                case CONST_qnmCfgId_index:
                    if (0 > (pObj.obj.resObj.usIndex_obj = getFieldData_short(pItem))) {
                        return iErr;
                    }
                    break;
                default:
                    bProcessed = false;
                    if (0 != tmp_ntohAvTranInfo(pItem, pObj.obj.tranInfo, bProcessed)) {
                        Log.d("qy","tmp_ntohAvTranInfo 1590kkk failed");
                        return iErr;
                    }

                    if (bProcessed) {
                        iErr = 0;
                        return iErr;
                    }
                    break;
            }

            iErr = 0;
            if (!pbProcessed) pbProcessed = bProcessed;

            return iErr;
        }


        //
        public static int transferAvInfo2Stream(int uiStreamId, qnmCommProc_mis.TRANSFER_AV_INFO pReq, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;

            int len = -1;
            int total_len = 0;

            if (null == pReq) return -1;
            if (null == ptr) return -1;

            do {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                    return iErr;
                total_len += len;

                if (0 != pReq.ucbResp) {
                    if (0 > (len = data2Stream(CONST_qyDataType_char, CONST_qnmCfgId_ucbResp, pReq.ucbResp, 0, ptr)))
                        return iErr;
                    total_len += len;
                }

                if (0 != pReq.uiTaskType) {
                    if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiTaskType, pReq.uiTaskType, 0, ptr)))
                        return iErr;
                    total_len += len;
                }

                //  2014/11/07
                if (0 > (len = tmp_htonTranConfCfg(pReq.confCfg, ptr))) return iErr;
                total_len += len;

                //
                len = tmp_htonMuxStreamsCfg(pReq.muxStreamsCfg, ptr);
                if (0 > len) break;
                total_len+=len;

                //
                qnmCommProc_mis.AV_stream tmpAs = new qnmCommProc_mis.AV_stream();
                tmpAs.obj = pReq.ass;
                if (0 > (len = tmp_htonAvStream(tmpAs, ptr))) return iErr;
                total_len += len;

                for (i = 0; i < 2; i++) {
                    qnmCommProc_mis.MOSAIC_TRAN_INFO pMosaic = null;
                    switch (i) {
                        case 0:
                            pMosaic = pReq.confMosaicTranInfo_video;
                            break;
                        case 1:
                            pMosaic = pReq.confMosaicTranInfo_resource;
                            break;
                        default:
                            break;
                    }
                    if (null == pMosaic) break;
                    //
                    if (0 != pMosaic.resObj.uiObjType && 0 != pMosaic.video.uiTranNo_openAvDev) {
                        tmpAs.obj.resObj = pMosaic.resObj;
                        tmpAs.obj.tranInfo.video = pMosaic.video;
                        if (0 > (len = tmp_htonAvStream(tmpAs, ptr))) {
                            return iErr;
                        }
                        total_len += len;
                    }
                }

                if (0 != pReq.viewDynBmp.ucbSaveVideo) {
                    if (0 > (len = data2Stream(CONST_qyDataType_char, CONST_qnmCfgId_ucbSaveVideo, pReq.viewDynBmp.ucbSaveVideo, 0, ptr)))
                        return iErr;
                    total_len += len;
                }

                //  2009/10/18
                if (pReq.idInfo_logicalPeer.ui64Id != 0) {
                    if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_idInfo_logicalPeer, pReq.idInfo_logicalPeer.ui64Id, Long.BYTES, ptr)))
                        return iErr;
                    total_len += len;
                }
                //  2014/11/22
                if (0 != pReq.uiTranNo_avTask_starter) {
                    if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiTranNo_avTask_starter, pReq.uiTranNo_avTask_starter, 0, ptr)))
                        return iErr;
                    total_len += len;
                }

                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                    return iErr;
                total_len += len;

            } while(false);

            return total_len;
        }





        //
        public static PF_handler_stream2Data tmpHandler_stream2TransferAvInfo = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) -> {
            int iErr = -1;
            //  p0;
            qnmCommProc_mis.TRANSFER_AV_INFO pContent = (qnmCommProc_mis.TRANSFER_AV_INFO) p1;
            //  QY_CFGITEM				*	pItem		=	(  QY_CFGITEM  *  )p2;
            //  int						tmpLen;
            //  char						buf[sizeof(  QY_VIDEO_HEADER  )  +  1024];

            if (null == pContent) {
                return iErr;
            }

            int tmp_cfgId;
            //memcpy(tmp_cfgId,pItem->cfgId, sizeof(short));
            tmp_cfgId = pItem.cfgId;
            short dataType = pItem.dataType;

            //
            //
            int  uiStreamId_parent; uiStreamId_parent = 0;
            if  (  pCtx.index_streamId  >  0  )  {
                uiStreamId_parent  =  pCtx.uiStreamIds[pCtx.index_streamId  -  1];
            }

            //
            if (uiStreamId  ==  CONST_ancCommType_muxStreamsCfg  ||  uiStreamId_parent == CONST_ancCommType_muxStreamsCfg) {

                //
                if (0!=tmp_stream2MuxStreamsCfg(pCtx, p0, pContent.muxStreamsCfg, uiStreamId, pItem)) {
                    return  -1;
                }

                //
                int  ii = 0;
                iErr = 0;  return  iErr;//goto  errLabel;
            }




            //
            switch (uiStreamId) {
                case imCommType_defs.Consts.CONST_imCommType_transferAvInfo:
                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_start:
                            pContent.uiType = uiStreamId;
                            break;
                        case CONST_qnmCfgId_null: {
                            int ii = 0;
                        }
                        break;
                        case CONST_qnmCfgId_ucbResp: {
                            if (dataType != CONST_qyDataType_l64) {
                                return -1;
                            }
                        }
                        if (0 > (pContent.ucbResp = getFieldData_byte(pItem))) {
                            return iErr;
                        }
                        break;
                        case CONST_qnmCfgId_uiTaskType: {
//                            if (dataType != CONST_qyDataType_l64) return -1;
                            if (-1 == (pContent.uiTaskType = getFieldData_long(pItem))) {
                                return iErr;
                            }
                        }
//                        if (0 != getFieldData_long(pItem.uiTaskType))
//                            return iErr;
                        break;

                        case CONST_qnmCfgId_ucbSaveVideo: {
//                            if (dataType != CONST_qyDataType_l64) return -1;
                            if (0 > (pContent.viewDynBmp.ucbSaveVideo = getFieldData_byte(pItem))) {
                                return iErr;
                            }
                        }
//                        if (0 != getFieldData_byte(pItem.viewDynBmp.ucbSaveVideo))
//                            return iErr;
                        break;
                        //
                        case CONST_qnmCfgId_idInfo_logicalPeer:
//                            if (dataType != CONST_qyDataType_l64) return -1;
//                            ByteBuffer buf = ByteBuffer.wrap(((QY_CFGITEM_L64)pItem).buf_l64Val);
//                            pContent.idInfo_logicalPeer.ui64Id = buf.getLong();
                            if (0 > (pContent.idInfo_logicalPeer.ui64Id = getFieldData_l64(pItem))) {
                                return iErr;
                            }
                            break;
                        //
                        case CONST_qnmCfgId_uiTranNo_avTask_starter: {
//                            if (dataType != CONST_qyDataType_l64) return -1;
                            if (-1 == (pContent.uiTranNo_avTask_starter = getFieldData_long(pItem))) {
                                return iErr;
                            }
                        }
//                        if (0 != getFieldData_long(pItem.uiTranNo_avTask_starter))
//                            return iErr;
                        break;
                        //
                        default:
                            //
                            boolean bProcessed = false;
                            //
                            if (0 != tmp_ntohTranConfCfg(pItem,pContent.confCfg, bProcessed)) {
                                //
                                Log.d("anc","th_stream2TransferAvInfo failed, tmp_ntohTranConfCfg failed");
                                //
                                return iErr;
                            }
                            if (bProcessed) break;

                            break;
                    }
                    break;
                case CONST_imCommType_avStream:
                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_start:
                            //mymemset((byte[])pContent->tmp_internal_tmpAs, 0, sizeof(AV_stream));
                            pContent.tmp_internal_tmpAs = new qnmCommProc_mis.AV_stream();
                            break;
                        case CONST_qnmCfgId_null:
                            switch (pContent.tmp_internal_tmpAs.obj.resObj.uiObjType) {
                                case CONST_objType_mosaicStream_video:
                                    pContent.confMosaicTranInfo_video.resObj = pContent.tmp_internal_tmpAs.obj.resObj;
                                    pContent.confMosaicTranInfo_video.video = pContent.tmp_internal_tmpAs.obj.tranInfo.video;
                                    break;
                                case CONST_objType_mosaicStream_resource:
                                    pContent.confMosaicTranInfo_resource.resObj = pContent.tmp_internal_tmpAs.obj.resObj;
                                    pContent.confMosaicTranInfo_resource.video = pContent.tmp_internal_tmpAs.obj.tranInfo.video;
                                    break;
                                default:
                                    pContent.ass = pContent.tmp_internal_tmpAs.obj;
                                    break;
                            }
                            break;
                        default:
                            boolean bProcessed = false;
                            if (0 != tmp_ntohAvStream(pItem,pContent.tmp_internal_tmpAs, bProcessed)) {
                                return iErr;
                            }
                            if (bProcessed) {
                                iErr = 0;
                                return iErr;
                            }
                            break;
                    }
                    break;

                default:
                    break;
            }


            iErr = 0;
            return iErr;
        };


        //  2011/12/24
        public static int transferAvReplyInfo2Stream(int uiStreamId, qnmCommProc_mis.
                TRANSFER_AV_replyInfo pReq, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;
            int len = -1;
            int total_len = 0;

            if (null == pReq) return -1;
            if (null == ptr) return -1;

            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;
            total_len += len;

            if (0 != pReq.usOp) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_usOp, pReq.usOp, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            if (0 != pReq.tStartTime_org) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_tStartTime_org, pReq.tStartTime_org, Long.BYTES, ptr)))
                    return iErr;
                total_len += len;
            }
            if (0 != pReq.uiTranNo_org) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiTranNo_org, pReq.uiTranNo_org, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            //
            qnmCommProc_mis.AV_stream tmpAs = new qnmCommProc_mis.AV_stream();
            //mymemset((byte[])tmpAs, 0, sizeof(AV_stream));
            tmpAs.obj.tranInfo = pReq.tranInfo;
            if (0 > (len = tmp_htonAvStream(tmpAs, ptr)))
                return iErr;
            total_len += len;

            //  2014/09/04
            if (0 != pReq.viewDynBmpReply.ucbSaveVideo) {   //  2014/09/07
                if (0 > (len = data2Stream(CONST_qyDataType_char, CONST_qnmCfgId_ucbSaveVideo, pReq.viewDynBmpReply.ucbSaveVideo, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            //
            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;
            total_len += len;

            iErr = 0;

            return total_len;

        }


        public static PF_handler_stream2Data tmpHandler_stream2TransferAvReplyInfo = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) -> {
            int iErr = -1;

            qnmCommProc_mis.TRANSFER_AV_replyInfo pContent = (qnmCommProc_mis.TRANSFER_AV_replyInfo) p1;
            int tmpLen;
            boolean bProcessed = false;
            if (null == pContent) return iErr;
            int tmp_cfgId  = pItem.cfgId;
            short dataType = pItem.dataType;
            switch (uiStreamId) {
                case CONST_imCommType_transferAvReplyInfo:
                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_start:
                            pContent.uiType = uiStreamId;
                            break;
                        case CONST_qnmCfgId_usOp:
                            if (0 > (pContent.usOp = getFieldData_short(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_tStartTime_org:
                            if (0 > (pContent.tStartTime_org = getFieldData_l64(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_uiTranNo_org:
                            if (-1 == (pContent.uiTranNo_org = getFieldData_long(pItem)))
                                return iErr;
                            break;
                        case CONST_qnmCfgId_ucbSaveVideo:
                            if (0 > (pContent.viewDynBmpReply.ucbSaveVideo = getFieldData_byte(pItem)))
                                return iErr;
                            break;
                        default:

                            break;
                    }
                    break;
                case CONST_imCommType_avStream:
                    switch (tmp_cfgId) {
                        case CONST_qnmCfgId_start:
                            pContent.tmp_internal.tmpAs = new qnmCommProc_mis.AV_stream();
                            break;
                        case CONST_qnmCfgId_null:
                            pContent.tranInfo = pContent.tmp_internal.tmpAs.obj.tranInfo;
                            break;
                        default:
                            if (0 != tmp_ntohAvStream(pItem,pContent.tmp_internal.tmpAs, bProcessed))
                                return iErr;
                            break;
                    }
                    break;

                default:
                    break;
            }
            iErr = 0;
            return iErr;
        };


        public static int qyhtonRouteTalkData(qyShowInfoCommProc.Ctx_thread_tmpVar  tmpVar,qnmCommProc_mis.MSG_ROUTE pAddr,
                                              qnmCommProc_mis.IM_CONTENTU pContent, int lenInBytes_content, boolean bNeedContentConvrted, byte ucFlg,
                                              ByteBuffer ptr, String tHintBuf_showInfo, int uiCnt_tHintBuf_showInfo) {
            int iErr = -1;

            int lenInBytes = 0;
            int lenInBytes_addr  =  0;
            String str;


            if (null == ptr) return -1;

            if (isUcFlgRouteTalkData(ucFlg)) {
                if (0 > (lenInBytes_addr = msgRoute2Stream(CONST_imCommType_msgRoute, pAddr, ptr))) {
                    return iErr;
                }
            }
            /*
            else if (isUcFlgTalkData(ucFlg)) {       //  仅用来talkData
                if (null == pAddr) return iErr;

                qnmCommProc_mis.MSG_SIMPLE_ROUTE pTalkDataAddr = new qnmCommProc_mis.MSG_SIMPLE_ROUTE();
                pTalkDataAddr.idInfo_from.ui64Id = pAddr.idInfo_from.ui64Id;
                pTalkDataAddr.idInfo_to.ui64Id = pAddr.idInfo_to.ui64Id;

                ptr.putLong(pTalkDataAddr.idInfo_to.ui64Id);
                ptr.putLong(pTalkDataAddr.idInfo_from.ui64Id);
                lenInBytes_addr = Long.BYTES * 2;
            }

             */

            //  要求不转换的
            //
            if (!bNeedContentConvrted) {

//                if (lenInBytes_content > uiBufSize - lenInBytes_addr) {
//                    Log.i("lenInBytes_content is too large");
//                    return iErr;
//                }
//                if (null == pContent) lenInBytes = lenInBytes_addr;
//                else {
//                    //qyMemoryFunc.qyFuncs.mymemcpy(ptr, pContent_content);
//                    lenInBytes = lenInBytes_addr + lenInBytes_content;
//                }

                iErr = 0;
                return iErr;
            }

            if (null == pContent) return iErr;

            switch (pContent.uiType) {
                case 0: {
                    lenInBytes = Integer.BYTES;
                    ptr.putInt(pContent.uiType);
                    lenInBytes += Integer.BYTES;
                    lenInBytes  +=  lenInBytes_addr;
                    //iErr = 0;
                    //return iErr;
                    return  lenInBytes;
                }
                case CONST_imCommType_simpleResp: {
                    //qnmCommProc_mis.IM_SIMPLE_RESP simpleResp = new qnmCommProc_mis.IM_SIMPLE_RESP();
                    //qyMemoryFunc.qyFuncs.mymemcpy((byte[]) simpleResp,  pContent.simpleResp, (int) sizeof(IM_SIMPLE_RESP));
                    //if ((lenInBytes = qyhtonImSimpleResp(0, simpleResp,ptr)) < 0) return iErr;
                    if ((lenInBytes = qyhtonImSimpleResp(0, (qnmCommProc_mis.IM_SIMPLE_RESP) pContent,ptr)) < 0) return iErr;

                    //qyMemoryFunc.qyFuncs.mymemcpy(ptr,  simpleResp);
                    lenInBytes += lenInBytes_addr;
                    //iErr = 0;
                    //return iErr;
                    return  lenInBytes;
                }
                default:
                    break;
            }

            //
            ByteBuffer content_buffer = tmpVar.m_ctxThreadtoSend.tmp_content_buffer;
            content_buffer.position(0);

            //
            if  (  true )  {
                switch  (  pContent.uiType)  {
                    case  CONST_ancCommType_transferAudioData:

                        //
                    {
                        qnmCommProc_mis.TRANSFER_AUDIO_DATA tad = (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pContent;

                        int tmpLen = tad.uiLen;
                        //
                        if (0 > (lenInBytes = transferAudioData2Stream(pContent.uiType, (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pContent, content_buffer))) {
                            return iErr;
                        }
                        //
                        if  (  false  ) {
                            int wrappingLen = lenInBytes  -  tmpLen;
                            str = String.format("hton: tad.len %d. cnt %d, n.lenInBytes %d. wappingLen %d, lenInBytes_addr %d. l2304", tmpLen, tad.usCnt,  lenInBytes, wrappingLen, lenInBytes_addr);
                            Log.d("anc", str);
                        }

                        //
                        ptr.limit(ptr.capacity());
                        //
                        ptr.put(content_buffer.array(), 0, lenInBytes);

                        //
                        //qyMemoryFunc.qyFuncs.mymemcpy(ptr,  simpleResp);
                        lenInBytes += lenInBytes_addr;
                        //iErr = 0;
                        //return iErr;
                        return  lenInBytes;

                    }
                    //
                    //
                    //break;


                    default:
                            break;
                }
            }

           // qnmCommProc_mis.IM_L_STREAM_nh stream_hton = new qnmCommProc_mis.IM_L_STREAM_nh();
//            ptr.position(ptr.position() + 12); //ptr + offsetof(IM_L_STREAM, buf);
//            byte[] stream_pBuf = new byte[1024];//ptr.array();
//            ByteBuffer stream_pBuf = ByteBuffer.allocate(1024);
            //  pStream  =  (  IM_L_STREAM  *  )ptr;

//            if (uiBufSize - lenInBytes_addr < 0) {
//                Log.i("qyhtonRouteTalkData failed: buf is too small");
//                return iErr;  //  怕空间不够了
//            }
            //mymemset((byte[])stream_hton, 0, 0);
            //stream_hton.uiType = CONST_imCommType_lStream;
            //stream_hton.uiContentType = pContent.uiType;
//            stream_pBuf = ptr;
//            lenInBytes = uiBufSize - lenInBytes_addr;       //  sizeof(  pStream->buf  );

            //qyShowInfoCommProc.Ctx_thread_tmpVar tmpVar = getCtxThreadTmpVar(com.qycx.base.Consts.CONST_iParamType_main);
            //ByteBuffer content_buffer = ByteBuffer.allocate(909600);

            //
            content_buffer.position(0);
            switch (pContent.uiType) {

                case CONST_imCommType_htmlContent:
                    if (0 > (lenInBytes = imHtmlContent2Stream(pContent.uiType, (qnmCommProc_mis.IM_HTML_CONTENT) pContent, content_buffer)))
                        return iErr;
                    break;

                case CONST_imCommType_transferFileReq:
                    if (0 > (lenInBytes = transferFileReq2Stream(pContent.uiType, (qnmCommProc_mis.TRANSFER_FILE_REQ) pContent, content_buffer)))
                        return iErr;
                    break;

                case CONST_imCommType_taskProcReq:
                    if (0 > (lenInBytes = taskProcReq2Stream(pContent.uiType, (qnmCommProc_mis.TASK_PROC_REQ) pContent, content_buffer)))
                        return iErr;
                    break;
                case CONST_imCommType_taskProcResp: {
                    qnmCommProc_mis.TASK_PROC_RESP pTaskProcResp  =  (qnmCommProc_mis.TASK_PROC_RESP) pContent;
                    //
                    if (0 > (lenInBytes = taskProcResp2Stream(pContent.uiType, (qnmCommProc_mis.TASK_PROC_RESP) pContent, content_buffer))) {
                        return iErr;
                    }
                    //
                    if  (lenInBytes  == 0)  {
                        int  jj =  0 ;
                    }
                    else {
                         int  ii  =0;

                    }
                    //
                    }
                    break;

                case CONST_imCommType_transferFileReplyReq:
                    if (0 > (lenInBytes = transferFileReplyReq2Stream(pContent.uiType, (qnmCommProc_mis.TRANSFER_FILE_REPLYREQ) pContent, content_buffer)))
                        return iErr;
                    break;

                case CONST_imCommType_transferFileDataReq:
                    if (0 > (lenInBytes = transferFileDataReq2Stream(pContent.uiType, (qnmCommProc_mis.TRANSFER_FILEDATA_REQ) pContent, content_buffer)))
                        return iErr;
                    break;
                case CONST_imCommType_transferFileDataResp:
                    if (0 > (lenInBytes = transferFileDataResp2Stream(pContent.uiType, (qnmCommProc_mis.TRANSFER_FILEDATA_RESP) pContent, content_buffer)))
                        return iErr;
                    break;
                case CONST_imCommType_transferFileEndReq:
                    if (0 > (lenInBytes = transferFileEndReq2Stream(pContent.uiType, (qnmCommProc_mis.TRANSFER_FILEEND_REQ) pContent, content_buffer)))
                        return iErr;
                    break;
                case CONST_imCommType_transferFileEndResp:
                    if (0 > (lenInBytes = transferFileEndResp2Stream(pContent.uiType, (qnmCommProc_mis.TRANSFER_FILEEND_RESP) pContent, content_buffer)))
                        return iErr;
                    break;

                case imCommType_defs.Consts.CONST_imCommType_transferAvInfo:
                    if (0 > (lenInBytes = transferAvInfo2Stream(pContent.uiType, (qnmCommProc_mis.TRANSFER_AV_INFO) pContent, content_buffer)))
                        return iErr;
                    break;
                case CONST_imCommType_transferAvReplyInfo:
                    if (0 > (lenInBytes = transferAvReplyInfo2Stream(pContent.uiType, (qnmCommProc_mis.TRANSFER_AV_replyInfo) pContent, content_buffer)))
                        return iErr;
                    break;

                case CONST_imCommType_transferVideoData:
                    //
                {
                    qnmCommProc_mis.TRANSFER_VIDEO_DATA tvd = (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pContent;

                    int  tmpLen  = tvd.uiLen;

                    if (0 > (lenInBytes = transferVideoData2Stream(pContent.uiType, (qnmCommProc_mis.TRANSFER_VIDEO_DATA) pContent, content_buffer))) {
                        return iErr;
                    }
                    //
                    if  (  false  ) {
                        str = String.format("hton: tvd.len %d. n.lenInBytes %d. lenInBytes_addr %d", tmpLen, lenInBytes, lenInBytes_addr);
                        Log.d("anc", str);
                    }

                }
                    break;

                case CONST_imCommType_transferVideoDataResp:
                    if (0 > (lenInBytes = transferVideoDataResp2Stream(pContent.uiType, (qnmCommProc_mis.TRANSFER_VIDEO_dataResp) pContent, content_buffer)))
                        return iErr;
                    break;

                case CONST_imCommType_transferAudioData:
                    //
                {
                    qnmCommProc_mis.TRANSFER_AUDIO_DATA tad = (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pContent;

                    int tmpLen = tad.uiLen;
                    //
                    if (0 > (lenInBytes = transferAudioData2Stream(pContent.uiType, (qnmCommProc_mis.TRANSFER_AUDIO_DATA) pContent, content_buffer))) {
                        return iErr;
                    }
                    //
                    if  (  true  ) {
                        int wrappingLen = lenInBytes  -  tmpLen;
                        str = String.format("hton: tad.len %d. n.lenInBytes %d. wappingLen %d, lenInBytes_addr %d. l2454", tmpLen, lenInBytes, wrappingLen, lenInBytes_addr);
                        Log.d("anc", str);
                    }
                }
                    //
                    //
                    break;
                case CONST_imCommType_transferAudioDataResp:
                    if (0 > (lenInBytes = transferAudioDataResp2Stream(pContent.uiType, (qnmCommProc_mis.TRANSFER_AUDIO_dataResp) pContent, content_buffer)))
                        return iErr;
                    break;

                case CONST_imCommType_taskInteractionReq:
                    if (0 > (lenInBytes = taskInteractionReq2Stream(pContent.uiType, (qnmCommProc_mis.TASK_INTERACTION_REQ) pContent, content_buffer)))
                        return iErr;
                    break;

                case CONST_imCommType_confKey:
                    if (0 > (lenInBytes = confKey2Stream(pContent.uiType, (qnmCommProc_mis.CONF_KEY) pContent, content_buffer)))
                        return iErr;
                    break;

                //
                case CONST_imCommType_statusConfLayoutParam:
                    if (0 > (lenInBytes = statusConfLayoutParam2Stream(pContent.uiType, (qnmCommProc_mis.StatusConfLayoutParam) pContent, content_buffer)))
                        return iErr;
                    break;

                case CONST_imCommType_confLayout:
                    if (0 > (lenInBytes = confLayout2Stream(pContent.uiType, (qnmCommProc_mis.CONF_LAYOUT) pContent, content_buffer)))
                        return iErr;
                    break;

                case CONST_imCommType_confReq:
                    if (0 > (lenInBytes = confReq2Stream(pContent.uiType, (qnmCommProc_mis.CONF_req) pContent, content_buffer)))
                        return iErr;
                    break;
                case CONST_imCommType_getCfgsReq:   //  2011/01/30
                {
                    if (0 > (lenInBytes = qisGetCfgsReq2Stream(pContent.uiType, (qnmCommProc_mis.QIS_getCfgs_req) pContent, content_buffer))) {
                        Log.e("qisGetCfgsReq2Stream failed");
                        return iErr;
                    }
                    break;
                }
                case CONST_imCommType_ptzControlCmd:    //  2012/08/05
                    if (0 > (lenInBytes = ptzControlCmd2Stream(pContent.uiType, (qnmCommProc_mis.PTZ_control_cmd) pContent, content_buffer)))
                        return iErr;
                    break;

                    //
                case  CONST_imCommType_procLocReq:
                    //
                    if (0>(lenInBytes=procLocReqU2Stream(pContent.uiType, (qnmCommProc_mis.ProcLocReq_common) pContent, content_buffer) ) )  {
                        return iErr;
                    }
                    break;
                case  CONST_imCommType_locDataReq:
                    //Log.d("at","locDataReq.l2827");
                    if (0>(lenInBytes=locDataReq2Stream(pContent.uiType, (qnmCommProc_mis.LocDataReq) pContent, content_buffer)) ) {
                        return  iErr;
                    }
                    break;
                //
                case  CONST_imCommType_qisCaReq:
                    if (0>(lenInBytes=qisCaReq2Stream(pContent.uiType, (qnmCommProc_mis.QIS_ca_req)  pContent, content_buffer))) {
                        return iErr;
                    }
                    break;
                case  CONST_imCommType_anHgData:
                    if (0>(lenInBytes=anHgData2Stream(pContent.uiType, (qnmCommProc_mis.AnHg_req) pContent, content_buffer))) {
                        return iErr;
                    }
                    //
                    break;


                default: {
                    //
                    Log.e("anc", "qyhtonRouteTalkData: uiContentType " + pContent.uiType + " can't be supported");

                    return iErr;
                }
            }
            try {
                //
                if  (  true )  {
                    if  (  pContent.uiType==CONST_imCommType_taskProcResp  )  {
                        if  (  lenInBytes  ==  0) {
                            int ii = 0;
                            Log.d("anc","l2457");
                        }

                    }
                }


                //stream_hton.ulStreamLen = lenInBytes;
                ptr.limit(ptr.capacity());
                //ptr.putInt(stream_hton.uiType);
                //ptr.putInt(stream_hton.uiContentType);
                //ptr.putInt(stream_hton.ulStreamLen);

                ptr.putInt(CONST_imCommType_lStream);
                ptr.putInt(pContent.uiType);
                ptr.putInt(lenInBytes);
                //
                ptr.put(content_buffer.array(), 0, lenInBytes);
                //
            } catch (Exception e) {
                e.printStackTrace();
            }
            //return 16 + 12 + lenInBytes;
            return  lenInBytes_addr  +  12  +  lenInBytes;

        }


        public static int ptzControlCmd2Stream(int uiStreamId, qnmCommProc_mis.
                PTZ_control_cmd pReq, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;
            int len = -1;
            long ulIp = 0;
            int total_len = 0;


            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;
            total_len += len;
            //  2014/09/13
            if (0 != pReq.cmdInfo.obj.uiObjType) {
                if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_uiObjType, pReq.cmdInfo.obj.uiObjType, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            if (0 != pReq.cmdInfo.obj.usIndex_obj) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_index, pReq.cmdInfo.obj.usIndex_obj, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            //  2016/07/24
            if (0 != pReq.cmdInfo.obj.usHelp_subIndex) {
                if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_help_subIndex, pReq.cmdInfo.obj.usHelp_subIndex, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            //
            if (pReq.cmdInfo.idInfo.ui64Id != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_messengerId, pReq.cmdInfo.idInfo.ui64Id, Long.BYTES, ptr)))
                    return iErr;
                total_len += len;
            }
            if (0 != pReq.cmdInfo.iChannel) {
                if (0 > (len = data2Stream(CONST_qyDataType_l32, CONST_qnmCfgId_iChannel, pReq.cmdInfo.iChannel, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            //
            if (0 != pReq.cmdInfo.ucCmd) {
                if (0 > (len = data2Stream(CONST_qyDataType_char, CONST_qnmCfgId_nCmd, pReq.cmdInfo.ucCmd, 0, ptr)))
                    return iErr;
                total_len += len;
            }
            //
            if (0 != pReq.cmdInfo.ucMouseStatus) {
                if (0 > (len = data2Stream(CONST_qyDataType_char, CONST_qnmCfgId_mouseStatus, pReq.cmdInfo.ucMouseStatus, 0, ptr)))
                    return iErr;
                total_len += len;
            }

            //
            switch  (  pReq.cmdInfo.ucCmd)  {
                case  CONST_ptzCmd_3ddw:
                    if  (  pReq.paramU!=null&&pReq.paramU.param3ddw!=null) {
                        if (0 != pReq.paramU.param3ddw.sW_video) {
                            if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_width, pReq.paramU.param3ddw.sW_video, 0, ptr)))
                                return iErr;
                            total_len += len;
                        }
                        if (0 != pReq.paramU.param3ddw.sH_video) {
                            if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_height, pReq.paramU.param3ddw.sH_video, 0, ptr)))
                                return iErr;
                            total_len += len;
                        }
                        if (0 != pReq.paramU.param3ddw.xTop) {
                            if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_xTop, pReq.paramU.param3ddw.xTop, 0, ptr)))
                                return iErr;
                            total_len += len;
                        }
                        if (0 != pReq.paramU.param3ddw.yTop) {
                            if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_yTop, pReq.paramU.param3ddw.yTop, 0, ptr)))
                                return iErr;
                            total_len += len;
                        }
                        if (0 != pReq.paramU.param3ddw.xBottom) {
                            if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_xBottom, pReq.paramU.param3ddw.xBottom, 0, ptr)))
                                return iErr;
                            total_len += len;
                        }
                        if (0 != pReq.paramU.param3ddw.yBottom) {
                            if (0 > (len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_yBottom, pReq.paramU.param3ddw.yBottom, 0, ptr)))
                                return iErr;
                            total_len += len;
                        }
                    }
                    //
                    break;
                default:
                    break;
            }





            //
            if (pReq.idInfo_imGrp_related.ui64Id != 0) {
                if (0 > (len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_idInfo_grp_related, pReq.idInfo_imGrp_related.ui64Id, Long.BYTES, ptr)))
                    return iErr;
                total_len += len;
            }


            //
            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;
            total_len += len;

            iErr = 0;
            if (0 == iErr) {

            }

            //
            if  (  false  ) {
                String result = "";
                int pos = ptr.position();
                for (int j = 0; j < pos; j++) {
                    byte b = ptr.array()[j];
                    String hexString = String.format("%02X", b);
                    result += hexString + " ";
                }
                Log.d("byte ", "result :" + result);
            }

            //
            return total_len;
        }

        /////////////////////////////////////////
        ////////////////////////////////////////

        public  static int  procLocReqU2Stream(int  uiStreamId, qnmCommProc_mis.ProcLocReq_common pReq, ByteBuffer ptr)
        {
            int				iErr = -1;
            int				i = 0;
            int		len = -1;//*uiBufSize;
            int total_len = 0;
            boolean tmp_bErr=false;


            if (null==pReq)  return  -1;
            if (null==ptr)  return  -1;

            do {
                len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr);
                if (len <= 0)  break;
                total_len+=len;

                if (pReq.ucbResp!=0) {
                    len = data2Stream(CONST_qyDataType_char, CONST_qnmCfgId_ucbResp, pReq.ucbResp, 0, ptr );
                    if  (  len<=0) break;
                    total_len+=len;
                }

                //
                if (pReq.usSubtype!=0) {
                    len = data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_subtype, pReq.usSubtype, 0, ptr);
                    if(len<=0)break;
                    total_len+=len;
                }

                //
                switch (pReq.usSubtype) {
                    case CONST_procOfflineResSubtype_getCfgs: {
                        qnmCommProc_mis.ProcLocReq_getCfgs getCfgs = (qnmCommProc_mis.ProcLocReq_getCfgs) pReq;

                        //
                        if (getCfgs.resp.idInfo_locServ!=0) {
                            len=data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_idInfo_serv, getCfgs.resp.idInfo_locServ, Long.BYTES, ptr);
                            if  (  len<=0)  {
                                tmp_bErr=true;
                                break;
                            }
                            total_len+=len;
                        }
                        //
                        if (getCfgs.resp.mapServCfgStr.length()!=0) {
                            len = data2Stream(CONST_qyDataType_str, CONST_qnmCfgId_mapServCfgStr, getCfgs.resp.mapServCfgStr, getCfgs.resp.mapServCfgStr.length(), ptr);
                            if  (  len <=0  ) {
                                tmp_bErr=true;
                                break;
                            }
                            total_len+=len;
                        }

                    }
                        //
                        break;

                    default:
                        break;
                }
                if  ( tmp_bErr) {
                    break;
                }


                len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, 0, 0, ptr  );
                if  (  len <=0)  {
                    break;
                }
                total_len+=len;

                //
                iErr = 0;


            } while (false);


            return  (  iErr!=0 )  ?  iErr  :  total_len;

        }



        public static PF_handler_stream2Data tmpHandler_stream2ProcLocReqU = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) ->
        {

            int						iErr = -1;
            //  p0;
            qnmCommProc_mis.ProcLocReq_common pContent = (qnmCommProc_mis.ProcLocReq_common) p1;
            //  QY_CFGITEM				*	pItem		=	(  QY_CFGITEM  *  )p2;
            //  long						lVal;

            if (null==pContent)  return -1;//goto  errLabel;

            short  tmp_cfgId;
            //memcpy(&tmp_cfgId, &pItem->head.cfgId, sizeof(short));
            tmp_cfgId=pItem.cfgId;

            do {
                //
                switch (uiStreamId) {
                    case CONST_imCommType_procLocReq:
                        switch (tmp_cfgId) {
                            case CONST_qnmCfgId_start:
                                pContent.uiType = uiStreamId;
                                break;
                            case CONST_qnmCfgId_ucbResp:
                                pContent.ucbResp = getFieldData_byte(pItem);
                                break;
                            case CONST_qnmCfgId_subtype:
                                if (pContent.usSubtype != 0) return -1;
                                pContent.usSubtype = getFieldData_short(pItem);
                                break;
                            default:
                                break;
                        }
                    default:
                        break;
                }

                //
                if (pContent.usSubtype == CONST_procLocReqSubtype_getCfgs) {
                    qnmCommProc_mis.ProcLocReq_getCfgs getCfgs = (qnmCommProc_mis.ProcLocReq_getCfgs) pContent;
                    switch (uiStreamId) {
                        case CONST_imCommType_procLocReq:
                            //
                            switch (tmp_cfgId) {
                                case CONST_qnmCfgId_idInfo_serv:
                                    getCfgs.resp.idInfo_locServ = getFieldData_l64(pItem);
                                    break;
                                case CONST_qnmCfgId_mapServCfgStr: {
                                    getCfgs.resp.mapServCfgStr = getFieldData_str(pItem);

                                }
                                break;
                                default:
                                    break;
                            }
                            //
                            break;
                        default:
                            break;

                    }
                    //
                    iErr = 0;  break;
                }


                iErr = 0;

            } while(false);

            errLabel:
            return  iErr;
        };




        //public  static int  procLocReqU2Stream(int  uiStreamId, qnmCommProc_mis.ProcLocReq_common pReq, ByteBuffer ptr)

        /////
        public static int  locDataReq2Stream(int  uiStreamId, qnmCommProc_mis.LocDataReq pReq, ByteBuffer ptr)
        {
            int				iErr = -1;
            int				i = 0;
            int		len = -1;//*uiBufSize;
            int total_len = 0;
            boolean tmp_bErr=false;



            if (null==pReq)  return  -1;
            if (null==ptr)  return  -1;

            do {
                len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0,  ptr);
                if  (  len<=0) {
                    break;
                }
                total_len  +=  len;

                /*
                if (pReq.ucbResp) {
                    if (data2Stream(CONST_qyDataType_char, CONST_qnmCfgId_ucbResp, ( void*)
                    pReq -> common.ucbResp, 0, &ptr, &len))  goto errLabel;
                }

                 */

                //
                if (pReq.idInfo_imGrp_related !=0) {
                    len = data2Stream(CONST_qyDataType_l64, CONST_qnmCfgId_idInfo_grp_related, pReq.idInfo_imGrp_related, Long.BYTES, ptr);
                    if  (  len<=0)  {
                        break;
                    }
                    total_len+=len;
                }

                //
                if (pReq.locData.locStr.length()!=0) {
                    len = data2Stream(CONST_qyDataType_str, CONST_qnmCfgId_locStr, pReq.locData.locStr, pReq.locData.locStr.length(), ptr);
                    if  (  len  <=0)  {
                        break;
                    }
                    total_len  +=  len;
                }

                if (pReq.locData.iLaInM10000f != 0 || pReq.locData.iLoInM10000f != 0) {
                    len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_iLaInM10000f, pReq.locData.iLaInM10000f, 0, ptr);
                    if (len <= 0) {
                        break;
                    }
                    total_len += len;
                    len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_iLoInM10000f, pReq.locData.iLoInM10000f, 0, ptr);
                    if (len <= 0) {
                        break;
                    }
                    total_len += len;
                }


                //
                len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, 0, 0, ptr);
                if  ( len  <=0)  {
                    break;
                }
                total_len+=len;

                //
                iErr = 0;


            } while(false);

            return  iErr!=0  ?  iErr  :  total_len  ;

        }


        /*


        int  tmpHandler_stream2LocDataReq(CTX_stream2Data* pCtx, void* p0, void* p1, unsigned  int  uiStreamId, QY_CFGITEM_ntoh_U* pItem)
        {
            int						iErr = -1;
            //  p0;
            LocDataReq* pContent = (LocDataReq*)p1;
            //  QY_CFGITEM				*	pItem		=	(  QY_CFGITEM  *  )p2;
            //  long						lVal;

            if (!pContent)  goto  errLabel;

            unsigned  short  tmp_cfgId;
            memcpy(&tmp_cfgId, &pItem->head.cfgId, sizeof(short));

            switch (uiStreamId) {
                case  CONST_imCommType_locDataReq:
                    switch (tmp_cfgId) {
                        case  CONST_qnmCfgId_start:
                            pContent->uiType = uiStreamId;
                            break;
                        case  CONST_qnmCfgId_idInfo_grp_related:
                            if (getFieldData_l64(pItem, (__int64*) & pContent->idInfo_imGrp_related.ui64Id))    goto  errLabel;
                            break;
                        case  CONST_qnmCfgId_locStr:
                            if (getFieldData_str(pItem, pContent->locData.locStr,  mycountof(pContent->locData.locStr)))  goto  errLabel;
                            break;
                        default:
                            break;
                    }
                default:
                    break;
            }

            //



            iErr = 0;
            errLabel:
            return  iErr;
        }

         */

        public static isProtocolFunc.PF_handler_stream2Data tmpHandler_stream2TransferLocData = (isProtocolFunc.CTX_stream2Data pCtx, Object p0, Object p1, int uiStreamId, qnmCommProc.QY_CFGITEM pItem) ->
        {
            int iErr = -1;
            //  p0;
            qnmCommProc_mis.TransferLocData pContent = (qnmCommProc_mis.TransferLocData) p1;
            //  QY_CFGITEM					*	pItem		=	(  QY_CFGITEM  *  )p2;
            //  long						lVal;

            if (null == pContent) return iErr;

            int tmp_cfgId;
            tmp_cfgId = pItem.cfgId;
            //
            int  uiStreamId_parent; uiStreamId_parent = 0;
            if (pCtx.index_streamId > 0) {
                uiStreamId_parent = pCtx.uiStreamIds[pCtx.index_streamId - 1];
            }

            boolean  tmp_bErr=false;

            //
            do {
                //
                switch (uiStreamId) {
                    case CONST_imCommType_transferLocData:
                        switch (tmp_cfgId) {
                            case CONST_qnmCfgId_start:
                                pContent.uiType = uiStreamId;
                                break;
                            case CONST_qnmCfgId_idInfo_grp_related: {

                                //pContent.idInfo_imGrp_related.ui64Id =((qnmCommProc.QY_CFGITEM_L64)pItem).ulVal;
                                pContent.idInfo_imGrp_related = getFieldData_l64(pItem);
                            }
                            break;
                            case CONST_qnmCfgId_usCnt:
                                pContent.usCnt = getFieldData_short(pItem);

                                if (pContent.usCnt > pContent.mems.length) {
                                    tmp_bErr=true;
                                    break;

                                }

                                //  ÏÂÃæÒª½øÐÐ³õÊ¼»¯£¬·ÅÔÚÕâÀïÊÇÒòÎªµ£ÐÄÏÂÃæµÄÊý¾ÝÈç¹û³ö´í£¬usCnt½«°üº¬ÁËÒ»Ð©´íÎóÊý¾Ý
                                int i;
                                for (i = 0; i < pContent.usCnt; i++) {
                                    //memset( & pContent -> mems[i], 0, sizeof(pContent -> mems[i])  )
                                    ;
                                }
                                break;

                            default:
                                break;
                        }
                        break;
                    case  CONST_imCommType_mem:  {
                        qnmCommProc_mis.TransferLocDataMem pMem =null;

                        if  (  pContent.tmpInternal_usCnt_mems  >=  pContent.mems.length  )  {
                            Log.d("at","tmpHandler_stream2TransferAudioData failed: usCnt_mems is too big"  );
                            tmp_bErr=true;
                            break;
                        }
                        //
                        pMem  =  pContent.mems[pContent.tmpInternal_usCnt_mems];
                        switch  (  tmp_cfgId  )  {
                            case  CONST_qnmCfgId_messengerId:
                                pMem.idInfo.ui64Id=getFieldData_l64(pItem );
                                break;
                            case  CONST_qnmCfgId_iLaInM10000f:
                                  pMem.locData.iLaInM10000f=getFieldData_long(  pItem );
                                break;
                            case  CONST_qnmCfgId_iLoInM10000f:
                                pMem.locData.iLoInM10000f=getFieldData_long(  pItem);
                                break;
                            case  CONST_qnmCfgId_locStr:
                                pMem.locData.locStr= getFieldData_str(pItem);
                                break;
                            //
                            case  CONST_qnmCfgId_null:
                                pContent.tmpInternal_usCnt_mems  ++  ;
                                break;
                            default:
                                break;
                        }


                    }
                          break;
                    default:
                        break;
                }
                //
                if  (  tmp_bErr)  {
                    break;
                }

                iErr = 0;

            } while(false);

            return iErr;
        };




        /*
        public static int messengerAccount2Stream(int uiStreamId, qnmCommProc_mis.QY_MESSENGER_ACCOUNT pPcInfo, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;
            int len = -1;
            long ulIp = 0;
            if (null == ptr || null == pPcInfo) return -1;
            int total_len = 0;


            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;
            total_len += len;

         */




            //
        public static int  qisCaReq2Stream(int  uiStreamId, qnmCommProc_mis.QIS_ca_req pReq, ByteBuffer ptr)
        {
            int				iErr = -1;
            int				i = 0;

            //char* ptr = buf;
            int		len = -1;
            int total_len = 0;

            //if (!pReq)  return  -1;
            //if (!buf)  return  -1;


            if (0>(len=data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
            {
                return  iErr;
            }
            total_len += len;


            //
            if (0!=pReq.ucbResp) {
                if (0>(len=data2Stream(CONST_qyDataType_char, CONST_qnmCfgId_ucbResp, pReq.ucbResp, 0, ptr))  )
                {
                    return  iErr;
                }
            }
            //  2014/01/31
            if (0!=pReq.ca_sendDataLen) {	//
                if (0>(len=data2Stream(CONST_qyDataType_data, CONST_qnmCfgId_ca_sendData, pReq.ca_sendData, pReq.ca_sendDataLen, ptr))) {
                    return  iErr;
                }
                total_len += len;
            }

            //
            if (0!=pReq.ca_servDataLen) {	//
                if (0>(len=data2Stream(CONST_qyDataType_data, CONST_qnmCfgId_sxrz_servReply, pReq.ca_servData, pReq.ca_servDataLen, ptr))) {
                    return iErr;
                }
                total_len += len;
            }

            //
            if (0!=pReq.ca_cliDataLen) {
                if (0>(len=data2Stream(CONST_qyDataType_data, CONST_qnmCfgId_sxrz_cliData, pReq.ca_cliData, pReq.ca_cliDataLen, ptr)) ){
                    return  iErr;
                }
                total_len += len;
            }

            //
            if (0!=pReq.sHgCmd) {
                if (0>(len=data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_sHgCmd, pReq.sHgCmd, 0, ptr))  ){
                    return  iErr;
                }
                total_len += len;

            }

            //
            if (0>(len=data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, 0, 0, ptr)) ){
                return iErr;
            }
            total_len += len;

            iErr = 0;
            errLabel:
            if (0!=iErr) {
	 //*uiBufSize = *uiBufSize - len;
            }

            //
            return  total_len;
        }






        /// <summary>
        //  QY_MESSENGER_ACCOUNT
        public static int messengerAccount2Stream(int uiStreamId, qnmCommProc_mis.QY_MESSENGER_ACCOUNT pPcInfo, ByteBuffer ptr) {
            int iErr = -1;
            int i = 0;
            int len = -1;
            long ulIp = 0;
            if (null == ptr || null == pPcInfo) return -1;
            int total_len = 0;


            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr)))
                return iErr;
            total_len += len;
            //
            if (0 > (len = data2Stream(CONST_qyDataType_str, CONST_qnmCfgId_messengerPasswd, pPcInfo.messengerPasswd, mystrlen(pPcInfo.messengerPasswd.getBytes()), ptr)))
                return iErr;
            total_len += len;
            //
            if (0 > (len = data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, null, 0, ptr)))
                return iErr;
            total_len += len;

            iErr = 0;
            return total_len;
        }

        //  其中ucFlg用来表示要转换成talkData或addrTalkData
        public static qnmCommProc_mis.IM_CONTENTU qyntohRouteTalkData(byte ucFlg, ByteBuffer data, int dataLen, qnmCommProc_mis.MSG_ROUTE pAddr, String tHintBuf_showInfo, int uiCnt_tHintBuf_showInfo) {
            int iErr = -1;
            int lenInBytes = 0;

            int size = dataLen;
            int uiType = 0;
            int uiContentType = 0;

            if (null == data) return null;

            if (isUcFlgRouteTalkData(ucFlg)) {
                if (null == pAddr) return null;
                if  ( null==pAddr.idInfo_to) {
                    //
                    Log.e("anc","pAddr.idInfo_to is null. l2607");
                    //
                    return null;
                }
                //
                if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2MsgRoute, null, pAddr)) {
                    //
                    Log.e("anc","qyntohRouteTalkData failed: stream2Data 1 failed. l2525");
                    //
                    return null;
                }
                //  剩下的size就是pStream
            }
            /*
            else if (isUcFlgTalkData(ucFlg)) {       //  仅用来talkData
                if (null == pAddr) return null;
                if  (null==pAddr.idInfo_to) {
                    //
                    Log.e("anc","qyntohRouteTalkData failed: pAddr.idInfo_to is null. l2634");
                    //
                    return null;
                }

                pAddr.idInfo_to.ui64Id = data.getLong();
                pAddr.idInfo_from.ui64Id = data.getLong();

                int lenInBytes_addr = 16;//sizeof(MSG_SIMPLE_ROUTE);

                size = dataLen - lenInBytes_addr;
            }

             */

            //
            //
            //  这里应该做一个将contentU转换guo来的工作。
            //  注意：在qyMc里，有所有的input的数据转换时，都要修改
            //  另外：要特别注意和老程序的通信的兼容性。老程序的talk和task等结构都是用MIS_MSG_TALKDATA发的。
            //

            if (size < Integer.BYTES) return null;

            //
            int pos_start  =  data.position();

            //
            qnmCommProc_mis.IM_CONTENTU pContent = null;
            uiType = data.getInt();
            switch (uiType) {
                case 0:
                    pContent = new qnmCommProc_mis.IM_SIMPLE_RESP();
                    pContent.uiType = uiType;//data.getInt();
                    lenInBytes = Integer.BYTES;
                    return pContent;
                case CONST_imCommType_simpleResp:
                    pContent = new qnmCommProc_mis.IM_SIMPLE_RESP();
                    pContent.uiType=uiType;
                    if ((lenInBytes = qyntohImSimpleResp(0, data,  (qnmCommProc_mis.IM_SIMPLE_RESP) pContent)) < 0)
                        return null;
                    iErr = 0;
                    return pContent;
                default:
                    break;
            }


            //
            if (uiType == CONST_imCommType_lStream) {
                //
                qnmCommProc_mis.IM_L_STREAM_nh stream_ntoh = new qnmCommProc_mis.IM_L_STREAM_nh();
                qnmCommProc_mis.IM_L_STREAM_nh pStream = stream_ntoh;

                pStream.uiType = uiType;
                lenInBytes = qyntohImLStream(0, data, pStream);
                if (lenInBytes < 0) {
                    return null;
                }

                size = pStream.ulStreamLen;
                uiContentType = pStream.uiContentType;
                //
                if  (  true  )  {
                    if (  uiContentType==CONST_imCommType_taskProcResp  &&  size ==  0)  {
                        Log.d("anc","kkk");
                    }

                }
                //
            } else {

                //
                data.position(pos_start);

                //
                if  (  true  ) {
                    int len;
                    qnmCommProc.QY_CFGITEM pItem = atntohCfgItem(data);     //  这里要注意：这里qyntohCfgItem(  )返回的是转换前的字节流长度。和qyhtonCfgItem(  )返回的同
                    if (pItem == null) {
                        return null;
                    }

                    len = pItem.len;
                    if (len <= 0 || len > size) {
                        Log.e("1 size:" + size + "  len:" + len);
                        return null;
                    }
                    size -= len;


                    if (pItem.cfgId == CONST_qnmCfgId_start) {

                        //
                        int uiSteamId = getFieldData_long(pItem);
                        uiContentType = uiSteamId;

                    }
                }

                //
                switch (uiContentType) {
                    case CONST_ancCommType_transferAudioData:
                        if  (  true  ) {
                            //Log.d("anc", "ntoh: tad");
                        }
                        break;
                        //
                    case  CONST_ancCommType_transferVideoData:
                          break;
                          //
                    default:
                        //
                        String str = "qyntohRouteTalkData 得到了非长流的数据包, uiType " + uiType + ", size " + size;
                        Log.e(str);
                        return null;
                }

                //
                //
                data.position(pos_start);

            }

            {
                switch (uiContentType) {
                    case CONST_imCommType_htmlContent:
                        pContent = new qnmCommProc_mis.IM_HTML_CONTENT();
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2ImHtmlContent, null, pContent))
                            return null;
                        break;
                    case  CONST_imCommType_refreshRecentFriendsResp: {
                        //
                        qnmCommProc_mis.REFRESH_RECENTFRIENDS_RESP resp = new qnmCommProc_mis.REFRESH_RECENTFRIENDS_RESP();
                        if (0 > stream2Data(data, size, filterStream_is, tmpHandler_stream2RefreshRecentFriendsResp, 0, resp)) {        //  这里，对万一有数据包下发不完整的情况下，做截断处理。不简单退出

                            //
                            if (resp.usCnt_mems != 0) resp.usCnt_mems--;    //  因为最后一个包不完整，所以要去掉
                            resp.usCnt = resp.usCnt_mems;
                            //
                        }

                        if (resp.usCnt > resp.mems.length) return null;

                        //
                        pContent=resp;

                    }

                    break;

                    case CONST_imCommType_getCfgsReq: {
                        pContent = new qnmCommProc_mis.QIS_getCfgs_req();
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2QisGetCfgsReq, null, pContent)) {
                            return null;
                        }
                        break;
                    }
                    case CONST_imCommType_retrieveImGrpListResp: {
                        pContent = new qnmCommProc_mis.RETRIEVE_IMGRPLIST_RESP();
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2RetrieveImGrpListResp, null, pContent)) {
                            return null;
                        }
                        if (((qnmCommProc_mis.RETRIEVE_IMGRPLIST_RESP) pContent).usCnt > CONST_maxContactUpdateInfos_grp) {
                            return null;
                        }
                        break;
                    }
                    case CONST_imCommType_retrieveImGrpMemListResp: {
                        pContent = new qnmCommProc_mis.RETRIEVE_IMGRPMEMLIST_RESP();
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2RetrieveImGrpMemListResp, null, pContent)) {
                            Log.e("CONST_imCommType_retrieveImGrpMemListResp stream2Data failed");
                            return null;
                        }
                        if (((qnmCommProc_mis.RETRIEVE_IMGRPMEMLIST_RESP) pContent).usCnt > CONST_maxContactUpdateInfos_grpMem)
                            return null;
                        break;
                    }
                    case CONST_imCommType_retrieveContactListResp: {
                        pContent = new qnmCommProc_mis.RETRIEVE_CONTACTLIST_RESP();
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2RetrieveContactListResp, null, pContent)) {
                            return null;
                        }
                        if (((qnmCommProc_mis.RETRIEVE_CONTACTLIST_RESP) pContent).usCnt > CONST_maxContactUpdateInfos_contact)
                            return null;
                        break;
                    }
                    case CONST_imCommType_retrieveImObjListEndResp: {
                        pContent = new qnmCommProc_mis.RETRIEVE_IMOBJLISTEND_RESP();
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2RetrieveImObjListEndResp, null, pContent)) {
                            return null;
                        }
                        break;
                    }
                    case CONST_imCommType_transferFileReq:
                        pContent = new qnmCommProc_mis.TRANSFER_FILE_REQ();
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2TransferFileReq, null, pContent))
                            return null;
                        break;
                    case CONST_imCommType_transferFileReplyReq:
                        pContent = new qnmCommProc_mis.TRANSFER_FILE_REPLYREQ();
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2TransferFileReplyReq, null, pContent))
                            return null;
                        break;
                    case CONST_imCommType_taskProcReq:
                        pContent = new qnmCommProc_mis.TASK_PROC_REQ();
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2TaskProcReq, null, pContent))
                            return null;
                        break;
                    case CONST_imCommType_taskProcResp:
                        if  (  size  ==  0)  {
                            Log.d("anc","qyntohRouteTalkData failed, taskProcResp, size  is  0");
                            return  null;
                        }
                        pContent = new qnmCommProc_mis.TASK_PROC_RESP();
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2TaskProcResp, null, pContent)) {
                            return null;
                        }
                        break;
                    case CONST_imCommType_transferFileDataReq:
                        pContent = new qnmCommProc_mis.TRANSFER_FILEDATA_REQ();
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2TransferFileDataReq, null, pContent)) {
                            return null;
                        }
                        //
                        if  (  false)  {
                            //Log.d("qy","kkkkkk");
                        }
                        //
                        break;
                    case CONST_imCommType_transferFileDataResp:
                        pContent = new qnmCommProc_mis.TRANSFER_FILEDATA_RESP();
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2TransferFileDataResp, null, pContent))
                            return null;
                        break;
                    case CONST_imCommType_transferFileEndReq:
                        pContent = new qnmCommProc_mis.TRANSFER_FILEEND_REQ();
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2TransferFileEndReq, null, pContent))
                            return null;
                        break;
                    case imCommType_defs.Consts.CONST_imCommType_transferAvInfo:
                        pContent = new qnmCommProc_mis.TRANSFER_AV_INFO();
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2TransferAvInfo, null, pContent)) {
                            Log.d("anc","qyntohRouteTalkData failed, transferAvInfo: stream2Data failed");
                            return null;
                        }
                        break;
                    //老俞说，点对点时，sps、pps也可能在这里
                    case CONST_imCommType_transferAvReplyInfo:
                        pContent = new qnmCommProc_mis.TRANSFER_AV_replyInfo();
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2TransferAvReplyInfo, null, pContent))
                            return null;
                        break;
                    case CONST_imCommType_transferVideoData:
                        pContent = new qnmCommProc_mis.TRANSFER_VIDEO_DATA();
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2TransferVideoData, null, pContent))
                            return null;
                        //
                        if  (  false  ) {
                            int  ii  =0;
                            String str;
                            for (ii =0;ii<((qnmCommProc_mis.TRANSFER_VIDEO_DATA) pContent).usCnt;ii++){
                                qnmCommProc_mis.TRANSFER_VIDEO_dataMemHead pMem  =  ((qnmCommProc_mis.TRANSFER_VIDEO_DATA) pContent).mems[ii];
                                //
                                //Log.e("tn_msgInput", "ntoh: transferVideo.mem[" + ii + "] pts " + pMem.uiPts );
                                str  =  "ntoh: transferVideo.mem[" + ii + "] pts " + pMem.uiPts;
                                Log.d("qy",str);
                                //showInfo_open.qyFuncs.showInfo_open(str);
                            }
                            //

                        }
                        //
                        break;
                    case CONST_imCommType_transferVideoDataResp:
                        pContent = new qnmCommProc_mis.TRANSFER_VIDEO_dataResp();
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2TransferVideoDataResp, null, pContent))
                            return null;
                        break;
                    case CONST_imCommType_transferAudioData: {
                        pContent = new qnmCommProc_mis.TRANSFER_AUDIO_DATA();
                        //
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2TransferAudioData, null, pContent)) {
                            return null;
                        }
                        //
                        if (false) {
                        }
                        //
                        }
                        break;
                    case CONST_imCommType_transferAudioDataResp:
                        pContent = new qnmCommProc_mis.TRANSFER_AUDIO_dataResp();
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2TransferAudioDataResp, null, pContent))
                            return null;
                        break;
                    //点对点场景在此处解析sps、pps
                    case CONST_imCommType_taskInteractionReq:
                        pContent = new qnmCommProc_mis.TASK_INTERACTION_REQ();
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2TaskInteractionReq, null, pContent))
                            return null;
                        //
                        if  (  true  )  {
                            if  (  ((qnmCommProc_mis.TASK_INTERACTION_REQ) pContent).usOp  == CONST_imOp_pleaseSpeak)  {
                                Log.d("anc","pleaseSpeak");
                            }
                        }
                        //
                        break;
                    //会议场景在此处解析sps、pps
                    case CONST_imCommType_confKey:
                        pContent = new qnmCommProc_mis.CONF_KEY();
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2confKey, null, pContent)) {
                            return null;
                        }
                        //
                        if  (  false ) {
                            Log.d("anc", "qyntohRouteTalkData, confKey. 2639");
                        }
                        //
                        break;
                    case CONST_imCommType_statusConfLayoutParam:
                        pContent = new qnmCommProc_mis.StatusConfLayoutParam();
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2statusConfLayoutParam, null, pContent))
                            return null;
                        break;
                    case CONST_imCommType_confLayout:
                        pContent = new qnmCommProc_mis.CONF_LAYOUT();
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2confLayout, null, pContent)) {
                            return null;
                        }
                        break;
                    case CONST_imCommType_confNvrInfo:
                        pContent = new qnmCommProc_mis.ConfNvrInfo();
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2confNvrInfo, null, pContent))
                            return null;
                        break;
                    case CONST_imCommType_confReq:
                        pContent = new qnmCommProc_mis.CONF_req();
                        if (0 > stream2Data(data, size, isProtocolFunc.qyFuncs.filterStream_is, tmpHandler_stream2ConfReq, null, pContent))
                            return null;
                        break;

                    case  CONST_imCommType_transferLocData:
                        pContent=new qnmCommProc_mis.TransferLocData();
                        if  (  0>stream2Data(  data,  size,  filterStream_is,  tmpHandler_stream2TransferLocData,  null,  pContent  )  ) {
                            return null;
                        }
                        break;

                    case CONST_imCommType_procLocReq:
                         pContent=new qnmCommProc_mis.ProcLocReq_getCfgs();
                         if  (  0  >  stream2Data(data,size,filterStream_is,tmpHandler_stream2ProcLocReqU,null,pContent)) {
                             return null;
                         }
                         break;

                    case CONST_imCommType_anHgData:
                        pContent = new qnmCommProc_mis.AnHg_req();
                        if (0 > stream2Data(data, size, filterStream_is, tmpHandler_stream2AnHgData, null, pContent)) {
                            return null;
                        }
                        break;

                    default: {
                        String str = "qyntohRouteTalkData failed: uiContentType " + uiContentType + " error";
                        Log.w(str);
                        showInfo_open.qyFuncs.showInfo_open(0, null,str);
                        return null;
                    }
                }
                return pContent;
            }
        }
    }
    public static int  anHgData2Stream(int  uiStreamId, qnmCommProc_mis.AnHg_req pReq, ByteBuffer ptr)
    {
        int				iErr = -1;
        int				i = 0;
        //char* ptr = buf;
        int		len = -1;//*uiBufSize;
        int total_len=0;

        //if (!pReq)  return  -1;
        //if (!buf)  return  -1;

        if (0>(len=data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_start, uiStreamId, 0, ptr))) {
            return -1;
        }
        total_len += len;


        //
            /*
            if (0!=pReq.ucbResp) {
                if (0>(len=data2Stream(CONST_qyDataType_char, CONST_qnmCfgId_ucbResp, pReq.ucbResp, 0, ptr)) )  {
                    return -1;
                }
            }

             */


        //
        if (0!=pReq.hg_cliDataLen) {
            if (0>(len= qyFuncs.data2Stream(CONST_qyDataType_lData, CONST_qnmCfgId_hg_cliData, pReq.hg_cliData, pReq.hg_cliDataLen, ptr)) )  {
                return  -1;
            }
            total_len += len;
        }

        //
        if (0!=pReq.sHgCmd) {
            if (0>(len= data2Stream(CONST_qyDataType_short, CONST_qnmCfgId_sHgCmd, pReq.sHgCmd, 0, ptr)) )  {
                return -1;
            }
            total_len += len;
        }

        //
        if (0>(len= data2Stream(CONST_qyDataType_long, CONST_qnmCfgId_null, 0, 0, ptr)) ) {
            return -1;
        }
        total_len += len;

        iErr = 0;
            /*
            errLabel:
            if (!iErr) {
		 *uiBufSize = *uiBufSize - len;
            }

             */
        return  total_len;
    }

    public static int data2Stream(short iDataType, short usCfgId, Object orgData, int orgDataSize, ByteBuffer buffer) {

        //
        if  (  M_isCommVer1())  {

            return  atData2Stream(iDataType,usCfgId,orgData,orgDataSize,buffer);

        }


        //
        int iErr = -1;
        if (null == buffer || 0 == buffer.capacity() || 0 == buffer.remaining()) return -1;
        int pos = buffer.position();
        int len;
        int  tmpLen  =  0;

        if (null == orgData) {
            switch (iDataType) {
                case CONST_qyDataType_l32:
                case CONST_qyDataType_char:
                case CONST_qyDataType_short:
                    break;
                default:
                    return -1;  //  非数值的值为地址，不能为空
            }
        }
        if (iDataType == CONST_qyDataType_l64) {
            if (orgDataSize != 0 && orgDataSize < 8) {       //  传入0也算了，早期代码都用0。2007/12/11
                Log.e("data2Stream: l64需要传入数据地址，故建议输入数据长度应>=sizeof(  __int64  )");
                return -1;
            }
        }
        ByteBuffer buf = null;
        buf  =  buffer;
        switch (iDataType) {
            case CONST_qyDataType_l32: {
                //buf = ByteBuffer.allocate(Short.BYTES * 2 + Integer.BYTES);
                buf.putShort(usCfgId);
                buf.putShort(iDataType);
                buf.putInt(orgData == null ? 0 : (int) orgData);
                tmpLen  =  Short.BYTES * 2 + Integer.BYTES;
            }
            break;
            case CONST_qyDataType_char: {
                //buf = ByteBuffer.allocate(Short.BYTES * 2 + Byte.BYTES);
                buf.putShort(usCfgId);
                buf.putShort(iDataType);
                buf.put(orgData == null ? 0 : (byte) orgData);
                tmpLen=Short.BYTES * 2 + Byte.BYTES;
            }
            break;
            case CONST_qyDataType_short: {
                //buf = ByteBuffer.allocate(Short.BYTES * 3);
                buf.putShort(usCfgId);
                buf.putShort(iDataType);
                buf.putShort(orgData == null ? 0 : (short) orgData);
                tmpLen  =  Short.BYTES * 3;
            }
            break;
            case CONST_qyDataType_data: {
                if (orgDataSize > bufSize_cfgItemData) {
                    Log.e("data2Stream: dataType_data: too big dataSize");
                    return iErr;
                }
                //buf = ByteBuffer.allocate(6 + orgDataSize);
                buf.putShort(usCfgId);
                buf.putShort(iDataType);
                buf.putShort((short) orgDataSize);
                buf.put((byte[]) orgData, 0, orgDataSize);
                tmpLen  =  6 + orgDataSize;
            }
            break;
            case CONST_qyDataType_lData: {
                if (orgDataSize > bufSize_cfgItemLData) {
                    Log.e("data2Stream: dataType_lData: too big dataSize");
                    return iErr;
                }
                //buf = ByteBuffer.allocate(Short.BYTES * 2 + Integer.BYTES + orgDataSize);
                buf.putShort(usCfgId);
                buf.putShort(iDataType);
                buf.putInt(orgDataSize);
                buf.put((byte[]) orgData, 0, orgDataSize);
                tmpLen  =  Short.BYTES * 2 + Integer.BYTES + orgDataSize;
            }
            break;
            case CONST_qyDataType_l64: {

                //buf = ByteBuffer.allocate(Short.BYTES * 2 + Long.BYTES);
                buf.putShort(usCfgId);
                buf.putShort(iDataType);
                buf.putLong((Long) orgData);
                tmpLen  =  Short.BYTES * 2 + Long.BYTES;
            }
            break;
            case CONST_qyDataType_str:
            case CONST_qyDataType_utf8Str: {
                byte[] data = ((String) orgData).getBytes(StandardCharsets.UTF_8);
                //buf = ByteBuffer.allocate(Short.BYTES * 3 + data.length);
                buf.putShort(usCfgId);
                buf.putShort(iDataType);
                buf.putShort((short) Math.min(CONST_cntof_cfgItemStr, orgDataSize));
                buf.put(data);
                tmpLen  =  Short.BYTES * 3 + data.length;
            }
            break;
            default:
                return iErr;
        }
            /*
            if  (  buf.capacity()  !=  tmpLen )  {
                Log.e("qy", "data2Stream failed, tmpLen err");
            }

             */
        //len = buf.capacity();
        len  =  tmpLen;
        try {
            //buffer.put(buf.array());
        } catch (Exception e) {
            Log.e(e.getLocalizedMessage());
        }
        //buf.clear();
        //buf = null;
        return len;
    }
}
