package com.qycx.tmpjavalibrary;

import java.nio.ByteBuffer;

public class TmpJniUtil {
    static {
        System.loadLibrary("tmpJni-lib");
    }

    //
    public static native int hiFromJni(int[]pResult);
    public static native String sayHello(String kk);
    public static native double[] sumAvg(int[] nums);
    public static native int procByteArray(byte[] bb,int bufSize);

    //
    public static native ByteBuffer allocNativeByteBuffer(int size);
    public static native void freeNativeByteBuffer(ByteBuffer bb);


        //
    public static native int  i420ToRgb(ByteBuffer  yuv, ByteBuffer rgb, int rgbBits, boolean bBgr, int  width, int  height, boolean  bFlipImg  );
    public static native int  rgbToI420(  ByteBuffer  rgb,  int rgbBits, boolean bBgr, ByteBuffer  yuv,  int  width,  int  height,  boolean  bFlipImg  );
    public static native int rgb32To24(ByteBuffer rgb32,boolean bBgr_32, ByteBuffer rgb,boolean bBgr_24, int w,int h);

    //
    public static native int rotateI420(ByteBuffer src, ByteBuffer dst, int width_src, int height_src, int degree,boolean bFlipImg);

    //
    public static native int newTmpQMemIndex(int playerIndex);
    public static native  int freeTmpQMemIndex(int index);
    //
    public static native int getQNodes(int index_player);

    //
    public static native int playAudioData( int iSampleTimeInMs, int uiPts, byte[] pInput, int inputLen, int iIndex_player);
    public static native int getAudioData(int [] piSampleTimeInMs, int [] puiPts, byte[] data, int [] pLen, int [] pibMixFailed, int iIndex_player,String hint);

    //
    public static native int AEC_layout_init(int  iTaskId, short  usCntLimit_activeMems_from,  boolean ucb_USE_audioMixer);
    public static native int AEC_layout_exit();

    //
    public static native int postRecData( int nAvgBytesPerSec, byte[] pbInput1, int dwNumBytes,  int  uiSampleTimeInMs);
    public static native int getRecData(int [] piSampleTimeInMs, byte[] data, int [] pLen);

    //
    public static native int getAecStatus(int[]nStep_mix,int[]nStep_aec);







}
