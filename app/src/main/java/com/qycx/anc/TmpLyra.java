package com.qycx.anc;

import android.util.Log;

import java.nio.ByteBuffer;

public class TmpLyra {

    //
    static {
        System.loadLibrary("tmpLyra_jni");
        //
        Log.d("anc","tmpLyra_jni loaed ok");
    }

    //
    public static native String sayHello();
    public static native String sayHello1(String model_file_path);

    //
    public static native String sayHello2();




    //

    public static native int qdc_init();
    public static native int qdc_exit();

    //
    public static native int newQdcUnitIndex(boolean bDbg);
    public static native void freeQdcUnitIndex(int index,boolean bDbg);

    //
    public static native int dec_getOutputByIndex(int index_qdcUnit, int index, ByteBuffer dec, int [] pLen, int [] piSampleTimeInMs, int [] puiPts, boolean bDbg);

    //
    public static native int dec_getOutputByIndexa(int index_qdcUnit, int index, byte[] data, int [] pLen, int [] piSampleTimeInMs, int [] puiPts, boolean bDbg);
    //
    //public static native int dec_getOutputByIndex1(int index_qdcUnit, int index, byte[] dec, int [] pLen, int [] piSampleTimeInMs, int [] puiPts, boolean bDbg);

    //
    public static native int dec_clearOutputs(int index_qdcUnit,boolean bDbg);

    //
    public static native int decfunc(int index_qdcUnit,boolean bDbg);

    //
    public static native int encA_init(int index_qdcUnit, byte ucCompressors, short wFormatTag, int bitrate, String modelFilePath,  boolean bDbg);
    public static native int encA_exit(int index_qdcUnit,boolean bDbg);
    //public static native int encA_data(int index_qdcUnit,ByteBuffer bb_pcm, byte[]pcm, int pcmLen,int iSampleTimeInMs, boolean bDbg);
    public static native int encA_data(int index_qdcUnit,byte[]pcm, int pcmLen,int iSampleTimeInMs, boolean bDbg);

    //
    public static native int decA_init(int index_qdcUnit, byte ucCompressors, short wFormatTag, String modelFilePath, boolean bDbg);
    public static native int decA_exit(int index_qdcUnit,boolean bDbg);
    public static native int decA_data(int index_qdcUnit, ByteBuffer encData,int encData_size, int iInputBufferPaddingSize, int iSampleTimeInMs, int uiPts, ByteBuffer decBuf, int iDecBufSize, boolean bDbg);




}
