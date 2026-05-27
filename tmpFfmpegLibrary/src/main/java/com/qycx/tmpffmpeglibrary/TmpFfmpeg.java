package com.qycx.tmpffmpeglibrary;

import java.nio.ByteBuffer;

public class TmpFfmpeg {
    static {
        System.loadLibrary("tmpFfmpeg-lib");
    }

    public static native int hiFromJni(int[]pResult);
    public static native int getHello();
    public static native String urlProtocolInfo();
    public static native String avFormatInfo();
    public static native String avCodecInfo();
    public static native String avFilterInfo();



    //
    public static native String doTest(int index_qdcUnit,String str );
    //
    public static native int qdc_init();
    public static native int qdc_exit();

    //
    public static native int newQdcUnitIndex(boolean bDbg);
    public static native void freeQdcUnitIndex(int index,boolean bDbg);

    //
    public static native int encV_init(int index_qdcUnit, int iFourcc, int in_w,int in_h,boolean bDbg);
    public static native int encV_exit(int index_qdcUnit,boolean bDbg);
    public static native int encV_getSpsPps(int index_qdcUnit,ByteBuffer spsPps,int[]pSpsPpsLens,boolean bDbg);
    public static native int encV_data(int index_qdcUnit, ByteBuffer yuv, int yuvLen, ByteBuffer raw, int rawSize,boolean bDbg );

    //
    public static native int decV_init(int index_qdcUnit, int iFourcc, boolean bDbg);
    public static native int decV_exit(int index_qdcUnit,boolean bDbg);
    public static native int decV_data(int index_qdcUnit, ByteBuffer data,int data_size, int iInputBufferPaddingSize, ByteBuffer dec, int decSize, boolean bDbg);

    //
    public static native int dec_getOutputByIndex(int index_qdcUnit, int index, ByteBuffer dec, int decSize,boolean bDbg);
    public static native int dec_clearOutputs(int index_qdcUnit,boolean bDbg);

    //
    public static native int encA_init(int index_qdcUnit, byte ucCompressors, short wFormatTag, int bitrate, boolean bDbg);
    public static native int encA_exit(int index_qdcUnit,boolean bDbg);
    public static native int encA_data(int index_qdcUnit,ByteBuffer pcm,int pcmLen,ByteBuffer raw,int rawSize, boolean bDbg);

    //
    public static native int decA_init(int index_qdcUnit, byte ucCompressors, short wFormatTag, boolean bDbg);
    public static native int decA_exit(int index_qdcUnit,boolean bDbg);
    public static native int decA_data(int index_qdcUnit, ByteBuffer encData,int encData_size, int iInputBufferPaddingSize, ByteBuffer decBuf, int iDecBufSize, boolean bDbg);

    //
    public static native int melp_encA_getOutputByIndex(int index_qdcUnit, int index, byte[] aBuf, int []piBufLen,int []piPts,int []piSampleTypeInMs, boolean bDbg);




}


