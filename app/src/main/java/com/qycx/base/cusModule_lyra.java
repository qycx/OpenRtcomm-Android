package com.qycx.base;

import static com.qycx.base.qnmCommProc_mis.Consts.bufSize_transferAudioData;

import com.qycx.anc.TmpLyra;
import com.qycx.MainActivity;
import com.qycx.base.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
//

public class cusModule_lyra {

    //
    static class CQdcObj_lyra {
        public int m_index_qdcUnit;
        //
        public boolean bDec;

        //
        public CQdcObj_lyra() {
            m_index_qdcUnit = -1;
            //
            bDec = false;
            //
            //memset(output, 0, sizeof(output));
        }

        //


    }

    //
    public static class qyFuncs_Droid {

        //
        public static qdcCommProc_open.PF_qdcInit qdcInit_lyra = (qdcCommProc_open.QDC_MGR p) -> TmpLyra.qdc_init();

        public static qdcCommProc_open.PF_qdcExit qdcExit_lyra = (qdcCommProc_open.QDC_MGR p) -> TmpLyra.qdc_exit();

        //
        public static int newQdcUnitIndex_lyra() {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            int iRet = -1;
            boolean bDbg = true;

            try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                if (syncObj.sync("", pProcInfo.hMutex_syncQdcUnit, "newQdcUnitIndex_ffmpeg.52") != 0) {
                    return iRet;
                }
                iRet = TmpLyra.newQdcUnitIndex(bDbg);
                syncObj.Dispose();
            } catch (Exception e) {
                Log.e(e.getMessage());
                return -1;
            }
            return iRet;

        }

        public static void freeQdcUnitIndex_lyra(int index) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            boolean bDbg = true;

            try (qySyncObj.CQySyncObj syncObj = new qySyncObj.CQySyncObj()) {
                if (syncObj.sync("", pProcInfo.hMutex_syncQdcUnit, "freeQdcUnitIndex_ffmpeg.71") != 0)
                    return;

                TmpLyra.freeQdcUnitIndex(index, bDbg);
            } catch (Exception e) {
                Log.e(e.getMessage());
            }
        }


        ///////////
        public static qdcCommProc_open.PF_qdcExitDecompressAudio qdcExitDecompressAudio_lyra = (qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            boolean bDbg = true;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();


            if (null == pQdcObjInfo.var.pObj) return 0;
            cusModule_lyra.CQdcObj_lyra pObj = (cusModule_lyra.CQdcObj_lyra) pQdcObjInfo.var.pObj;

            //
            TmpLyra.decA_exit(pObj.m_index_qdcUnit, bDbg);

            //
            pQdcObjInfo.bb_input = null;
            pQdcObjInfo.bb_output = null;

            freeQdcUnitIndex_lyra(pObj.m_index_qdcUnit);
            pObj.m_index_qdcUnit = -1;

            pQdcObjInfo.var.pObj = null;

            return 0;
        };

        public static qdcCommProc_open.PF_qdcInitDecompressAudio qdcInitDecompressAudio_lyra = (qnmCommProc_mis.AUDIO_COMPRESSOR_CFG pAudioCompressorCfg,
                                                                                                  qyCommProc_open.QY_AUDIO_HEADER pAh_compress,
                                                                                                  qyCommProc_open.QY_AUDIO_HEADER pAh_decompress,
                                                                                                  qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            int iErr = -1;
            boolean bDbg = true;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            //
            if (pQdcObjInfo.var.pObj != null) return -1;

            //
            pQdcObjInfo.cfg.a = pAudioCompressorCfg.common;


            //
            pQdcObjInfo.var.pObj = new cusModule_lyra.CQdcObj_lyra();

            cusModule_lyra.CQdcObj_lyra pObj = (cusModule_lyra.CQdcObj_lyra) pQdcObjInfo.var.pObj;

            pObj.m_index_qdcUnit = newQdcUnitIndex_lyra();
            if (pObj.m_index_qdcUnit < 0) {
                Log.e("qdcInitDeCompressA failed, newQdcUnitIndex failed");
                qdcExitDecompressAudio_lyra.call(pQdcObjInfo);
                return iErr;
            }

            //
            pObj.bDec = true;

            //

            String model_file_dir  = MainActivity.g_model_file_dir;
            //
            if (0 != TmpLyra.decA_init(pObj.m_index_qdcUnit, pAudioCompressorCfg.common.head.ucCompressors,(short) pAudioCompressorCfg.wf_compress.wFormatTag, model_file_dir,bDbg))
                return -1;

            //
            pQdcObjInfo.bHeadLen = 1 + 7 * Integer.BYTES;
            pQdcObjInfo.nPktsDeced = 0;


            iErr = 0;


            if (iErr != 0) {
                qdcExitDecompressAudio_lyra.call(pQdcObjInfo);
            }

            return iErr;
        };

        public static qdcCommProc_open.PF_qdcDecompressAudio qdcDecompressAudio_lyra = (qdcCommProc_open.QOI_common pQdcObjInfo, qyCommProc_open.QY_AUDIO_HEADER pAh_compress, byte[] pInput,
                                                                                          int uiInputLen, int iSampleTimeInMs, int uiPts) -> {
            int iErr = -1;
            int iPaddingSize = 128;

            boolean bDbg = false;

            cusModule_lyra.CQdcObj_lyra pObj = (cusModule_lyra.CQdcObj_lyra) pQdcObjInfo.var.pObj;
            //
            if (pQdcObjInfo.bb_input == null) {
                pQdcObjInfo.bb_input = ByteBuffer.allocateDirect(bufSize_transferAudioData);
                if (pQdcObjInfo.bb_input == null) return iErr;
                //
                //Log.d("qdcDecAudio_android: bb_input alloc");
            }
            if (pQdcObjInfo.bb_output == null) {
                //int nAvgBytesPerSec = (int) pAh_compress.wfx.nSamplesPerSec * pAh_compress.wfx.wBitsPerSample / 8 * pAh_compress.wfx.nChannels;
                int nAvgBytesPerSec = (int) 16000 * 16 / 8 * 1;
                //
                if  (  false )  {
                    nAvgBytesPerSec=8000  *  16  /  8  *  1;
                }
                else
                {
                    if (nAvgBytesPerSec == 0) {
                        Log.d("qdcDecA_ffmpeg faile, nAvgBytesPerSec is 0");
                        return iErr;
                    }
                }
                pQdcObjInfo.bb_output = ByteBuffer.allocateDirect(nAvgBytesPerSec + pQdcObjInfo.bHeadLen);
                if (pQdcObjInfo.bb_output == null) return iErr;
                //
                //Log.d("qdcDecAudio_android: bb_outpt alloc");
            }
            //
            ByteBuffer bb_input = (ByteBuffer) pQdcObjInfo.bb_input;
            ByteBuffer bb_output = (ByteBuffer) pQdcObjInfo.bb_output;
            byte[] pBbInput = bb_input.array();
            if (null == pBbInput) return iErr;
            byte[] pBbOutput = bb_output.array();
            if (null == pBbOutput) return iErr;

            //
            if (uiInputLen + iPaddingSize > bb_input.capacity()) {
                //Log.d("qdcDecVideo failed: bb_input is too small");
                return iErr;
            }
            //qyMemoryFunc.qyFuncs.mymemcpy( pBbInput, pInput, (int) uiInputLen);
            bb_input.position(0);
            bb_input.put(pInput, 0, uiInputLen);
            bb_input.position(0);


            //
            int total_nums = 7;
            byte len = (byte) (1 + total_nums * 4);
            //int head_array[7];
            if (pQdcObjInfo.bHeadLen < len) {
                //Log.d("qdcDecVideo failed, bHeadLen error");
                return iErr;
            }

            //
            //pBbOutput = pQdcObjInfo.bHeadLen;

            int  tmpiRet;
            tmpiRet= TmpLyra.decA_data(pObj.m_index_qdcUnit, bb_input, (int) uiInputLen, iPaddingSize, iSampleTimeInMs,uiPts,bb_output, bb_output.capacity(), bDbg);
            if  (  tmpiRet!=0) {
                Log.d("anc","TmpLyra.decA_data failed. tmpiRet "  +  tmpiRet);
                return iErr;
            }


            //
            iErr = 0;


            return iErr;
        };


        //
        //  2016/12/05
        public static qdcCommProc_open.PF_qdcDecompressAudioRun qdcDecompressAudioRun_lyra = (qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            //Log.d("not finished");
            return 0;
        };

        //
        //
        public static qdcCommProc_open.PF_qdcGetOutputByIndexa qdcGetOutputByIndexa_lyra = (qdcCommProc_open.QOI_common pQdcObjInfo, int index, byte[] pbOutData,qnmCommProc_mis.TRANSFER_AUDIO_dataMemHead head) -> {
            int iErr = -1;
            boolean bDbg = false;

            cusModule_lyra.CQdcObj_lyra pObj = (cusModule_lyra.CQdcObj_lyra) pQdcObjInfo.var.pObj;

            //
            if (pQdcObjInfo.bb_output == null) {
                //Log.d("qdcGetOutputByIndex_android failed, bb_output is null");
                return -1;
            }

            //
            do {
                //
                if (pObj.bDec) {

                    //
                    int[] lens = new int[1];
                    int[] sampleTimeInMses = new int[1];
                    int[] ptses = new int[1];
                    lens[0] = head.uiLen;//bb_output.capacity();

                    //
                    //ByteBuffer bb=ByteBuffer.allocateDirect(4096);
                    //TmpLyra.dec_getOutputByIndex(pObj.m_index_qdcUnit,index,bb,lens,sampleTimeInMses,ptses,bDbg);

                    //TmpLyra.dec_getOutputByIndexa(pObj.m_index_qdcUnit,index,pbOutData,lens,sampleTimeInMses,ptses,false);

                    //
                    if (0 != TmpLyra.dec_getOutputByIndexa(pObj.m_index_qdcUnit, index, pbOutData, lens, sampleTimeInMses, ptses, bDbg))
                        return iErr;
                    //
                    head.uiLen = lens[0];
                    head.uiPts = ptses[0];
                    //
                    if (true) {
                        //Log.d("anc", "dec.getOutputByIndex: index " +  index  +  " "  +   head.uiLen + " bytes" + " pts " + head.uiPts);
                    }
                    //
                    iErr = 0;
                    break;

                } else {

                    //
                    int[] lens = new int[1];
                    int[] sampleTimeInMses = new int[1];
                    int[] ptses = new int[1];
                    lens[0] = head.uiLen;//bb_output.capacity();

                    //
                    //ByteBuffer bb=ByteBuffer.allocateDirect(4096);
                    //TmpLyra.dec_getOutputByIndex(pObj.m_index_qdcUnit,index,bb,lens,sampleTimeInMses,ptses,bDbg);

                    //TmpLyra.dec_getOutputByIndexa(pObj.m_index_qdcUnit,index,pbOutData,lens,sampleTimeInMses,ptses,false);

                    //
                    //bDbg=true;

                    //
                    if (0 != TmpLyra.dec_getOutputByIndexa(pObj.m_index_qdcUnit, index, pbOutData, lens, sampleTimeInMses, ptses, bDbg))
                        return iErr;
                    //
                    head.uiLen = lens[0];
                    head.uiPts = ptses[0];
                    //
                    if (true) {
                        //Log.d("anc", "dec.getOutputByIndex: index " +  index  +  " "  +   head.uiLen + " bytes" + " pts " + head.uiPts);
                    }

                    //
                    iErr = 0;
                    break;
                    //
                }

                //
                //iErr = 0;

            } while  (  false  );

            //
            return iErr;
        };


        //
        public static qdcCommProc_open.PF_qdcClearOutputs qdcClearOutputs_lyra = (qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            boolean bDbg = false;
            cusModule_lyra.CQdcObj_lyra pObj = (cusModule_lyra.CQdcObj_lyra) pQdcObjInfo.var.pObj;

            //
            if (pObj.bDec) {
                TmpLyra.dec_clearOutputs(pObj.m_index_qdcUnit, bDbg);

            } else {

            }

            //
            return 0;
        };


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //
        public static qdcCommProc_open.PF_qdcExitCompressAudio qdcExitCompressAudio_lyra = (qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            boolean bDbg = true;

            if (null == pQdcObjInfo.var.pObj) return 0;
            cusModule_lyra.CQdcObj_lyra pObj = (cusModule_lyra.CQdcObj_lyra) pQdcObjInfo.var.pObj;

            TmpLyra.encA_exit(pObj.m_index_qdcUnit, bDbg);

            //
            pQdcObjInfo.bb_input = null;
            pQdcObjInfo.bb_output = null;


            freeQdcUnitIndex_lyra(pObj.m_index_qdcUnit);
            pObj.m_index_qdcUnit = -1;

            //
            pQdcObjInfo.var.pObj = null;

            return 0;
        };


        //
        public static qdcCommProc_open.PF_qdcInitCompressAudio qdcInitCompressAudio_lyra = (qnmCommProc_mis.AUDIO_COMPRESSOR_CFG pAudioCompressorCfg,
                                                                                              qyCommProc_open.QY_AUDIO_HEADER pAh_decompress,
                                                                                              qyCommProc_open.QY_AUDIO_HEADER pAh_compress,
                                                                                              qdcCommProc_open.QOI_common pQdcObjInfo) -> {
            int iErr = -1;

            boolean bDbg = true;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            if (pQdcObjInfo.var.pObj != null) {
                qdcExitCompressAudio_lyra.call(pQdcObjInfo);
                return -1;
            }

            pQdcObjInfo.var.pObj = new cusModule_lyra.CQdcObj_lyra();

            cusModule_lyra.CQdcObj_lyra pObj = (cusModule_lyra.CQdcObj_lyra) pQdcObjInfo.var.pObj;

            pObj.m_index_qdcUnit = newQdcUnitIndex_lyra();
            if (pObj.m_index_qdcUnit < 0) {
                Log.d("qdcInitCompressA failed, newQdcUnitIndex failed");
                qdcExitCompressAudio_lyra.call(pQdcObjInfo);
                return iErr;
            }

            if (pAh_decompress.wfx.nAvgBytesPerSec == 0) {
                Log.d("initEncA failed, nAvgBytesPerSec is 0");
                qdcExitCompressAudio_lyra.call(pQdcObjInfo);
                return -1;
            }

            //
            int bitrate  =  3200;
            if  (  pAudioCompressorCfg.common.head.iAvgBitrateInKbps!=0) {
                //bitrate=pAudioCompressorCfg.common.head.iAvgBitrateInKbps  *  1000;
            }
            //
            String model_path  =  MainActivity.g_model_file_dir;
            //
            if (0 != TmpLyra.encA_init(pObj.m_index_qdcUnit, pAudioCompressorCfg.common.head.ucCompressors,(short) pAudioCompressorCfg.wf_compress.wFormatTag, bitrate,  model_path,bDbg)) {
                qdcExitCompressAudio_lyra.call(pQdcObjInfo);
                return -1;
            }

            pAh_compress = pAh_decompress;
            pAh_compress.wfx.wFormatTag = pAudioCompressorCfg.wf_compress.wFormatTag;//WAVE_FORMAT_MPEGLAYER3;

            pQdcObjInfo.bb_input = ByteBuffer.allocateDirect((int) pAh_decompress.wfx.nAvgBytesPerSec);
            pQdcObjInfo.bb_output = ByteBuffer.allocateDirect(bufSize_transferAudioData);
            pQdcObjInfo.bb_input.order(ByteOrder.LITTLE_ENDIAN);
            pQdcObjInfo.bb_output.order(ByteOrder.LITTLE_ENDIAN);
            pQdcObjInfo.nLeft_output = 0;
            pQdcObjInfo.bHeadLen = 1 + 12;

            iErr = 0;
            return iErr;
        };


        public static qdcCommProc_open.PF_qdcCompressAudio qdcCompressAudio_lyra = (qdcCommProc_open.QOI_common pQdcObjInfo,
                                                                                      qyCommProc_open.QY_AUDIO_HEADER pAh_decompress, byte[] pInput,
                                                                                      int uiInputSize, int iSampleTimeInMs) -> {

            int iErr = -1;
            //
            boolean bDbg = false;
            String str;

            cusModule_lyra.CQdcObj_lyra pObj = (cusModule_lyra.CQdcObj_lyra) pQdcObjInfo.var.pObj;

            //
            if  (  true  )  {
                //  for test
                //bDbg=true;
            }


            //
            /*
            if (pQdcObjInfo.bb_input == null || pQdcObjInfo.bb_output == null) return -1;
            ByteBuffer bb_input = (ByteBuffer) pQdcObjInfo.bb_input;
            ByteBuffer bb_output = (ByteBuffer) pQdcObjInfo.bb_output;
            byte[] pBbInput = bb_input.array();
            if (null == pBbInput) return iErr;
            byte[] pBbOutput = bb_output.array();
            if (null == pBbOutput) return iErr;

            if (uiInputSize > bb_input.capacity()) return iErr;
            //qyMemoryFunc.qyFuncs.mymemcpy( pBbInput, pInput, (int) uiInputSize);
            bb_input.position(0);
            bb_input.put(pInput, 0, uiInputSize);
            bb_input.position(0);

            if (pQdcObjInfo.nLeft_output != 0) {
                showTaskStatusFunc.qyFuncs.showNotification_open(0, 0, 0, "Err: qdcCompressVideo_android; nLeft is not 0");
                return iErr;
            }
            int bufSize_output = bb_output.capacity();
            if (bufSize_output <= pQdcObjInfo.bHeadLen) return iErr;
            int len_output0;
            int len_output1;
            int len_output2;
            //
            len_output0 = bufSize_output - pQdcObjInfo.bHeadLen;
            //
            int len = 0;

            bb_output.position(0);
            bb_output.put(pQdcObjInfo.bHeadLen);
            bb_output.putInt(len_output0);
            bb_output.position(0);

             */

            //
            if  (  false  )  {
                    //
                    String fn = "l496.pcm";
                    //GuiShare.pf_dumpData1.call(0, 0, 0, 0, false, false, fn);
                    guiShare.GuiShare.pf_dumpData1.call(pInput, (int) uiInputSize, 0, 0, true, false, fn);

            }


            //
            if (0 != TmpLyra.encA_data(pObj.m_index_qdcUnit, pInput,(int) uiInputSize, iSampleTimeInMs, bDbg)) {
                //
                Log.e("qdcCompressAudio_lyra: enc_data failed");
                //
                return iErr;
            }
            //

            iErr = 0;
            return iErr;
        };

        //  2016/12/05
        public static qdcCommProc_open.PF_qdcCompressAudioRun qdcCompressAudioRun_lyra = (qdcCommProc_open.QOI_common pQdcObjInfo) -> {

            return 0;
        };






    }




    }
