package com.qycx.anc;

import static com.qycx.anc.Consts.__USE_atbyte_short__;
import static com.qycx.anc.ancCommProc_open.Consts.CONST_atCommFlg_msb;
import static com.qycx.anc.ancCommProc_open.Consts.MAX_12bits;
import static com.qycx.base.isProtocolFunc.Consts.cntof_ctx_uiStreamIds;
import static com.qycx.base.isProtocolFunc.qyFuncs.getFieldData_long;
import static com.qycx.base.qnmCommProc.Consts.CONST_cntof_cfgItemStr;
import static com.qycx.base.qnmCommProc.Consts.CONST_qnmCfgId_null;
import static com.qycx.base.qnmCommProc.Consts.CONST_qnmCfgId_start;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyDataType_byte;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyDataType_char;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyDataType_data;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyDataType_l32;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyDataType_l64;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyDataType_lData;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyDataType_long;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyDataType_short;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyDataType_str;
import static com.qycx.base.qnmCommProc.Consts.CONST_qyDataType_utf8Str;
import static com.qycx.base.qnmCommProc.Consts.bufSize_cfgItemData;
import static com.qycx.base.qnmCommProc.Consts.bufSize_cfgItemLData;
import static com.qycx.base.qyComm.qyFuncs.isUcFlgMoreData;

import com.qycx.base.isProtocolFunc;
import com.qycx.base.qnmCommProc;
import com.qycx.base.qyMsgFunc;
import com.qycx.base.util.Log;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class anProtocol {

    public static final int  INT24_MIN  =   -8388608;
    public static final int INT24_MAX  =  8388607;


    //
    public static int to_int24(int val)
    {
        if  (  val >INT24_MAX) return INT24_MAX;
        if  (  val < INT24_MIN)return INT24_MIN;
        return val;
    }

    //
    /**
     * 将32位整数转换为24位整数
     * 截取32位整数的低24位，高位补0
     * @param int32 32位整数
     * @return 24位整数（用32位int表示，高8位为0）
     */
    public static int int32_to_int24(int int32) {
        //
        if  (  int32  <  INT24_MIN  ||  int32  >  INT24_MAX)  {
            int32  =  to_int24(int32);
        }

        //
        // 使用掩码0x00FFFFFF截取低24位，高8位清零
        return int32 & 0x00FFFFFF;
    }


    /**
     * 将24位整数转换为32位整数（符号扩展）
     * 把24位整数的最高位视为符号位，进行符号扩展
     * @param int24 24位整数（用32位int表示，高8位应为0）
     * @return 32位整数（进行符号扩展）
     */
    public static int int24_to_int32(int int24) {
        // 确保输入是有效的24位整数（高8位为0）
        int valid24 = int24 & 0x00FFFFFF;

        // 检查24位整数的符号位（第23位）
        if ((valid24 & 0x00800000) != 0) {
            // 符号位为1，进行符号扩展（高8位设为1）
            return valid24 | 0xFF000000;
        } else {
            // 符号位为0，高8位保持0
            return valid24;
        }
    }



    //
    public static int athtonl3(int  x, byte[] buf)
    {
        if(buf.length  <  3)  return  -1;

        x  =  int32_to_int24(x);

        //
        buf[0]  =  (byte)((x&0xff0000)>>16);
        buf[1]  =  (byte)((x&0xff00)>>8);
        buf[2]  =  (byte)((x&0xff));

        return  0;
    }

    public static int atntohl3(byte[]buf)
    {
        if(buf.length<3)return  -1;

        int l3  =  0;
        l3  =  ((buf[0]&  0xff)<<16)
                +((buf[1]&  0xff)<<8)
                +(buf[2]  &  0xff);

        //
        l3  =  int24_to_int32(l3);

        //
        return l3;
    }

    //
    public static boolean g_bUseMsb_cfgHead_toh = true;//false;

    // ok
    public static boolean g_bUseMsb_cfgHead_ton = true;//false;



    public static byte[] shortToBytes(short value) {
        return new byte[] {
                (byte) (value >> 8),    // 高字节
                (byte) value            // 低字节
        };
    }

    public static short bytesToShort(byte[] bytes) {
        return (short) ((bytes[0] << 8) | (bytes[1] & 0xFF));
    }

    //
    public static int ton_cfgHead(int  iDataType,short usCfgId, ByteBuffer o_buf)
    {
        int iErr  =  -1;
        int  headLen  =  0;

        do  {

            if  (  iDataType <  0  ||  iDataType  >  127)  {
                Log.e("ton_cfgHead");
                return  -1;
            }

            if  (  !g_bUseMsb_cfgHead_ton) {
                o_buf.put((byte) iDataType);
                headLen += Byte.BYTES;
                //
                o_buf.putShort(usCfgId);
                headLen += Short.BYTES;

            }
            else  {
                  if  (  iDataType  >=0  &&  iDataType  <  8  &&usCfgId <=MAX_12bits)  {
                      byte buf[]  =  shortToBytes(usCfgId);
                      //
                      byte b0=(byte)((iDataType<<4) |CONST_atCommFlg_msb);
                      buf[0]|=b0;
                      //
                      o_buf.put(buf,0,2);
                      //
                      headLen+=2;

                  }
                  else  {

                      o_buf.put((byte) iDataType);
                      headLen += Byte.BYTES;
                      //
                      o_buf.putShort(usCfgId);
                      headLen += Short.BYTES;

                  }

            }

            //
            iErr  =  0;
        }  while( false);


        return  (  iErr  <  0  )  ?  -1  :  headLen;

    }



    //
    public static int atData2Stream(short iDataType, short usCfgId, Object orgData, int orgDataSize, ByteBuffer buffer) {

        //
        int iErr = -1;
        if (null == buffer || 0 == buffer.capacity() || 0 == buffer.remaining()) return -1;
        int posVal = buffer.position();
        int len;
        int tmpLen = 0;

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
        buf = buffer;

        //
        boolean bProcessed = false;

        //
        if  (  iDataType==CONST_qyDataType_l64) {
            long ul64;  ul64=(long)orgData;
            int  high4Bytes;
            int  low4Bytes;
            //
            high4Bytes=(int)(ul64>>32);
            low4Bytes=(int)ul64;
            if  (  high4Bytes==0)  {
                return  atData2Stream(CONST_qyDataType_l32,usCfgId,low4Bytes,0,buffer);
            }
        }
        if(iDataType==CONST_qyDataType_l32) {
            int ul32; ul32=(orgData == null ? 0 : (int) orgData);
            short high2Bytes;
            short low2Bytes;
            //
            high2Bytes=(short)(ul32>>16);
            low2Bytes=(short)ul32;
            if(high2Bytes==0){
                return  atData2Stream(CONST_qyDataType_short,usCfgId,low2Bytes,0,buffer);
            }

        }
        if  (iDataType==CONST_qyDataType_short) {
            short ul16; ul16=(orgData == null ? 0 : (short)  orgData);
            byte high1Bytes;
            byte low1Bytes;
            //
            high1Bytes=(byte)(ul16>>8);
            low1Bytes=(byte)ul16;
            if(high1Bytes==0) {
                return  atData2Stream(CONST_qyDataType_byte,usCfgId,low1Bytes,0,buffer);
            }
        }


        //
        tmpLen = 0;
        int headLen = 0;
        //
        if  (  false ) {
            buf.putShort(usCfgId);
            headLen = Short.BYTES;
            if (__USE_atbyte_short__) {
                buf.putShort(iDataType);
                headLen += Short.BYTES;
            } else {
                buf.put((byte) iDataType);
                headLen += Byte.BYTES;
            }
        }
        else  {
            if  (  false  ) {
                if (__USE_atbyte_short__) {
                    buf.putShort(iDataType);
                    headLen += Short.BYTES;
                } else {
                    buf.put((byte) iDataType);
                    headLen += Byte.BYTES;
                }
                //
                buf.putShort(usCfgId);
                headLen += Short.BYTES;
            }
            else  {
                 headLen=  ton_cfgHead(iDataType,usCfgId,buf);
                 if  (  headLen < 0)  {
                     Log.e("anc","atData2Stream: ton_cfgHead failed");
                     return  -1;
                 }
            }
        }
        //
        switch (iDataType) {
            case CONST_qyDataType_l32:
                buf.putInt(orgData == null ? 0 : (int) orgData);
                tmpLen = headLen + Integer.BYTES;
                //
                bProcessed = true;

                //
                break;
            case CONST_qyDataType_char: {
                //buf = ByteBuffer.allocate(Short.BYTES * 2 + Byte.BYTES);
                buf.put(orgData == null ? 0 : (byte) orgData);
                tmpLen = headLen + Byte.BYTES;
                //
                bProcessed = true;
            }
            break;
            case CONST_qyDataType_short: {
                buf.putShort(orgData == null ? 0 : (short) orgData);
                tmpLen = headLen + Short.BYTES;
                //
                bProcessed = true;
            }
            break;
            case CONST_qyDataType_data: {
                if (orgDataSize > bufSize_cfgItemData) {
                    Log.e("data2Stream: dataType_data: too big dataSize");
                    return iErr;
                }
                //buf = ByteBuffer.allocate(6 + orgDataSize);
                buf.putShort((short) orgDataSize);
                buf.put((byte[]) orgData, 0, orgDataSize);
                tmpLen = headLen + Short.BYTES + orgDataSize;
                //
                bProcessed = true;
            }
            break;


            case CONST_qyDataType_lData: {
                if (orgDataSize > bufSize_cfgItemLData) {
                    Log.e("data2Stream: dataType_lData: too big dataSize");
                    return iErr;
                }
                //buf = ByteBuffer.allocate(Short.BYTES * 2 + Integer.BYTES + orgDataSize);
                buf.putInt(orgDataSize);
                buf.put((byte[]) orgData, 0, orgDataSize);
                tmpLen = headLen + Integer.BYTES + orgDataSize;
                //
                bProcessed = true;
            }
            break;

            case CONST_qyDataType_l64: {

                //buf = ByteBuffer.allocate(Short.BYTES * 2 + Long.BYTES);
                buf.putLong((Long) orgData);
                tmpLen = headLen + Long.BYTES;
                //
                bProcessed = true;
            }
            break;

            case CONST_qyDataType_str:
            case CONST_qyDataType_utf8Str: {
                byte[] data = ((String) orgData).getBytes(StandardCharsets.UTF_8);
                //buf = ByteBuffer.allocate(Short.BYTES * 3 + data.length);
                //
                buf.putShort((short) Math.min(CONST_cntof_cfgItemStr, orgDataSize));
                buf.put(data);
                tmpLen = headLen + Short.BYTES + data.length;
                //
                bProcessed = true;
            }
            break;


            //
            default:
                break;
        }


        //
        if (!bProcessed) {

            //
            buffer.position(posVal);
            //
            tmpLen = 0;
            buf = buffer;

            //
            switch (iDataType) {
                case CONST_qyDataType_l32: {
                    //buf = ByteBuffer.allocate(Short.BYTES * 2 + Integer.BYTES);
                    buf.putShort(usCfgId);
                    buf.putShort(iDataType);
                    buf.putInt(orgData == null ? 0 : (int) orgData);
                    tmpLen = Short.BYTES * 2 + Integer.BYTES;
                }
                break;
                case CONST_qyDataType_char: {
                    //buf = ByteBuffer.allocate(Short.BYTES * 2 + Byte.BYTES);
                    buf.putShort(usCfgId);
                    buf.putShort(iDataType);
                    buf.put(orgData == null ? 0 : (byte) orgData);
                    tmpLen = Short.BYTES * 2 + Byte.BYTES;
                }
                break;
                case CONST_qyDataType_short: {
                    //buf = ByteBuffer.allocate(Short.BYTES * 3);
                    buf.putShort(usCfgId);
                    buf.putShort(iDataType);
                    buf.putShort(orgData == null ? 0 : (short) orgData);
                    tmpLen = Short.BYTES * 3;
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
                    tmpLen = 6 + orgDataSize;
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
                    tmpLen = Short.BYTES * 2 + Integer.BYTES + orgDataSize;
                }
                break;
                case CONST_qyDataType_l64: {

                    //buf = ByteBuffer.allocate(Short.BYTES * 2 + Long.BYTES);
                    buf.putShort(usCfgId);
                    buf.putShort(iDataType);
                    buf.putLong((Long) orgData);
                    tmpLen = Short.BYTES * 2 + Long.BYTES;
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
                    tmpLen = Short.BYTES * 3 + data.length;
                }
                break;
                default:
                    return iErr;
            }

        }
            /*
            if  (  buf.capacity()  !=  tmpLen )  {
                Log.e("qy", "data2Stream failed, tmpLen err");
            }

             */
        //len = buf.capacity();
        len = tmpLen;
        try {
            //buffer.put(buf.array());
        } catch (Exception e) {
            Log.e(e.getLocalizedMessage());
        }
        //buf.clear();
        //buf = null;
        return len;
    }

    public static class Param_toh_cfgHead {
        public int iDataType;
        public short usCfgId;
    };

    //
    public static int toh_cfgHead(ByteBuffer p, Param_toh_cfgHead param)
    {
        int  iErr = -1;
        int headLen = 0;
        int tmpLen;

        do  {
            if  (  !g_bUseMsb_cfgHead_toh)  {
                param.iDataType = p.get();
                param.usCfgId = p.getShort();
                //
                headLen=1+2;
            }
            else  {
                  int pos0=p.position();
                  byte b0=p.get();
                  p.position(pos0);
                //
                if ( (b0&CONST_atCommFlg_msb)!=0) {
                    b0&=~CONST_atCommFlg_msb;
                    param.iDataType=b0>>4;
                    byte low4  =  (byte)(b0&0x0f);
                    short sVal = p.getShort();
                    param.usCfgId  =  (short)((0xff  &  sVal)  +  (low4  <<  8));
                    //
                    headLen=2;
                }
                else  {
                    param.iDataType = p.get();
                    param.usCfgId = p.getShort();
                    //
                    headLen=1+2;
                }

            }

            //
            iErr  =  0;
        } while(false);


        return (  iErr  <  0  )  ?  -1  :  headLen;
    }


    //
    public static qnmCommProc.QY_CFGITEM atntohCfgItem(ByteBuffer p) {
        int len = 0;
        int headLen = 0;

        /*
        if  (  __USE_atbyte_short__)  {
            headLen  =  2  +  2;
        }
        else  {
              headLen  =  2  +  1;
        }

        //
        if (p.capacity() < headLen) {
            return null;
        }

         */

        //
        short cfgId = 0;
        short dataType = 0;
        //
        if  (  false  ) {
            cfgId = p.getShort();
            //
            if (__USE_atbyte_short__) {
                dataType = p.getShort();
            } else {
                dataType = p.get();
            }
        }
        else  {
            //
            if  (  false ) {
                if (__USE_atbyte_short__) {
                    dataType = p.getShort();
                } else {
                    dataType = p.get();
                }
                //
                cfgId = p.getShort();
            }
            //
            Param_toh_cfgHead param=new Param_toh_cfgHead();
            headLen = toh_cfgHead(p,param);
            if  (  headLen<0) return null;
            dataType=(short)param.iDataType;
            cfgId=param.usCfgId;
        }

        //
        qnmCommProc.QY_CFGITEM pItem;
        switch (dataType) {
            case CONST_qyDataType_long: {
                pItem = new qnmCommProc.QY_CFGITEM_L32();
                ((qnmCommProc.QY_CFGITEM_L32)pItem).ulVal = p.getInt();
                len = Integer.BYTES;
            }
            break;
            case CONST_qyDataType_char: {
                pItem = new qnmCommProc.QY_CFGITEM_BYTE();
                ((qnmCommProc.QY_CFGITEM_BYTE)pItem).ucVal = p.get();
                len = Byte.BYTES;
            }
            break;
            case CONST_qyDataType_short: {
                pItem = new qnmCommProc.QY_CFGITEM_SHORT();
                ((qnmCommProc.QY_CFGITEM_SHORT)pItem).usVal = p.getShort();
                len = Short.BYTES;
            }
            break;
            case CONST_qyDataType_l64: {
                pItem = new qnmCommProc.QY_CFGITEM_L64();
                ((qnmCommProc.QY_CFGITEM_L64)pItem).ulVal = p.getLong();
                len = Long.BYTES;
            }
            break;
            case CONST_qyDataType_data:{
                pItem = new qnmCommProc.QY_CFGITEM_DATA();
                short data_len = p.getShort();
                byte[] data = new byte[data_len];
                p.get(data, 0, data_len);
                ((qnmCommProc.QY_CFGITEM_DATA)pItem).buf = ByteBuffer.wrap(data);
                len = Short.BYTES + data_len;
                break;
            }
            case CONST_qyDataType_str:
            case CONST_qyDataType_utf8Str: {
                pItem = new qnmCommProc.QY_CFGITEM_STR();
                short str_len = p.getShort();
                byte[] buf = new byte[str_len];
                p.get(buf, 0, str_len);
                ((qnmCommProc.QY_CFGITEM_STR) pItem).str = new String(buf, StandardCharsets.UTF_8);
                len = Short.BYTES + str_len;
            }
            break;
            case CONST_qyDataType_lData: {
                pItem = new qnmCommProc.QY_CFGITEM_LDATA();
                int ulLen = p.getInt();
                byte[] data = new byte[ulLen];
                p.get(data, 0, ulLen);
                ((qnmCommProc.QY_CFGITEM_LDATA)pItem).ulLen = ulLen;
                ((qnmCommProc.QY_CFGITEM_LDATA)pItem).p = ByteBuffer.wrap(data);
                len = Integer.BYTES + ulLen;
            }
            break;
            default:
                Log.e("unknown dataType:"+dataType);
                return  null;
        }
        pItem.cfgId = cfgId;
        pItem.dataType = dataType;
        pItem.len = len + headLen;
        return pItem;
    }



    //
    //字节流转换结构
    public static int atStream2Data(ByteBuffer pCfgsBuf_usable, int pSize_usable, isProtocolFunc.PF_handler_stream2Data pf_filter, isProtocolFunc.PF_handler_stream2Data pf, Object p0, Object p1) {

        //


        //
        int iErr = -1;
        int len = 0;

        isProtocolFunc.CTX_stream2Data ctx = new isProtocolFunc.CTX_stream2Data();

        int nStarts = 0;
        int tmpiRet;

        if (null == pCfgsBuf_usable || null == pf_filter || null == pf || pSize_usable == 0) {
            return -1;
        }
        int size = pSize_usable;

        for (; size != 0; ) {
//                Log.d("size:"+size);
            qnmCommProc.QY_CFGITEM pItem = atntohCfgItem(pCfgsBuf_usable);     //  这里要注意：这里qyntohCfgItem(  )返回的是转换前的字节流长度。和qyhtonCfgItem(  )返回的同
            if (pItem == null) {
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
                //
                if  (  false  ) {
                    if (pItem.dataType != CONST_qyDataType_long) {
                        Log.e("3");
                        return iErr;
                    }
                }
            }

            if (pItem.cfgId == CONST_qnmCfgId_start) {
                nStarts++;
                if (nStarts > cntof_ctx_uiStreamIds) {
                    Log.e("4");
                    return iErr;
                }

                //
                int  uiSteamId  =  getFieldData_long(pItem);
                ctx.uiStreamIds[nStarts - 1] = uiSteamId;//((qnmCommProc.QY_CFGITEM_L32) pItem).ulVal;
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
                Log.e("l473 8 size:" + size);
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
    public static int M_ton_l64(long value,byte[] buf)
    {
        byte[] bytes = buf;//new byte[8];
        bytes[0] = (byte) (value >> 56);
        bytes[1] = (byte) (value >> 48);
        bytes[2] = (byte) (value >> 40);
        bytes[3] = (byte) (value >> 32);
        bytes[4] = (byte) (value >> 24);
        bytes[5] = (byte) (value >> 16);
        bytes[6] = (byte) (value >> 8);
        bytes[7] = (byte) value;

        return  0;
    }

    public static int M_ton_long(int value,byte[] buf)
    {
        byte[] bytes;
        bytes = buf;
        bytes[0] = (byte) (value >> 24);
        bytes[1] = (byte) (value >> 16);
        bytes[2] = (byte) (value >> 8);
        bytes[3] = (byte) value;

        return  0;
    }

    public static  int M_ton_short(short svalue, byte[] buf)
    {
        byte [] bytes;
        bytes = buf;

        bytes[0] = (byte) (svalue >> 8); // 高8位
        bytes[1] = (byte) svalue;        // 低8位

        return 0;
    }
    //
    public static int  AT_MSG_HEAD22_hton(long   i64StartTime_base, qyMsgFunc.AT_MSG_HEAD_h pH, qyMsgFunc.AT_MSG_HEAD22_n pN)
    {
        //unsigned  char* tmp_p;
        int  tmpLen;


        //
        pN.ucFlg_buf[0]=pH.ucFlg;
        //
        byte[] bytes;
        bytes= pN.usCode_buf;
        short svalue = pH.usCode;
        M_ton_short(svalue,bytes);

        //
        //tmp_p = pN->uiLen_buf;
        //M_ton_long(pH->uiLen, tmp_p, tmpLen);
        //
        bytes = pN.uiLen_buf;
        int value = pH.uiLen;
        M_ton_long(value,bytes);

        //
        //bytes = pN.i64StartTime_buf;
        M_ton_l64(pH.i64StartTime, pN.i64StartTime_buf);
        //
        //tmp_p = pN->uiTranNo_buf;
        M_ton_long(pH.uiTranNo, pN.uiTranNo_buf);
        //
        //tmp_p = pN->usSeqNo_buf;
        M_ton_short(pH.usSeqNo, pN.usSeqNo_buf);

        //
        pN.nBytes = 22;
        if (!isUcFlgMoreData(pH.ucFlg)) {
            pN.nBytes -= 2;
        }



        //
        return  0;
    }



    //
}

