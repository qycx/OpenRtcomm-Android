package com.qycx.base;




import static com.qycx.anc.ancProtocolFunc.athtonl3;
import static com.qycx.anc.ancProtocolFunc.atntohl3;
import static com.qycx.base.qmcTaskInfo.Consts.CONST_taskDataType_conf;
import static com.qycx.base.qyMcMain_global.g.g_pQyMc;

import com.alt.mysmxjlibrary.testCls;
import com.qycx.base.util.Log;
import com.qycx.tmpffmpeglibrary.TmpFfmpeg;
import com.qycx.tmpjavalibrary.FileUtil;
import com.qycx.tmpjavalibrary.TmpJniUtil;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Debug;
import android.os.Environment;

import com.qycx.MainActivity;
import com.qycx.R;

import java.io.File;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.RandomAccess;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

//
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;




//
public class test {

    //
    public static String tag_100k  =  "100k";


        //
    public static class Person1 {
        private String name;
        private int age;

        //

        public Person1(String name, int age) {
            this.name = name;
            this.age = age;
        }



        public String getName() { return name; }
        public int getAge() { return age; }
    }


    //
    public static class testCls_Droid {


        //
        static qySyncCommProc.CMutexRW mtxRw = new qySyncCommProc.CMutexRW();


        //
        public static int testBreak()
        {

            return  0;
        }

        public static int testChk(String hint)
        {
            ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;

            ctxQmc.CCtxQmc pProcInfo  =  pQyMc.get_pProcInfo();

            //
            //FileUtil.saveBytes(null,false,"",false);
            String str;
            str  =  TmpJniUtil.sayHello("kk");




            //
            return  0;

        }


        //
        public static int testPrintTasks(boolean bShow,  String tag,  String hint)
        {
            ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo  =  pQyMc.get_pProcInfo();
            String str;

            return  0;

        }



        //
        public static int testInShow()
        {
            //

            //
            return  0;
        }



        //
        public static guiShare.PF_testInMain testInMain = (ctxQyMc.CCtxQyMc pQyMc) -> {
            if (pQyMc == null) return -1;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (pProcInfo == null) return -1;
            qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;






            return 0;
        };

        static public void test1()
        {
            //
            ctxQyMc.CCtxQyMc pQyMc =g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo =pQyMc.get_pProcInfo();


            //
            if  (  false ) {
                String str;
                str = String.format("a.in %dkpbs, out %dkbps. v.in %dkbps, out %dkbps", pProcInfo.status.netStat.uiInSpeedInKbps_a, pProcInfo.status.netStat.uiOutSpeedInKbps_a,
                        pProcInfo.status.netStat.uiInSpeedInKbps_v, pProcInfo.status.netStat.uiOutSpeedInKbps_v);
                Log.d("anc", str);
            }


        }






        //
        public static int testTime() {
            String str;

            //
//            SimpleDateFormat alldate = new SimpleDateFormat("yyyyMMdd_HHmmss");
//            Java.Util.Date date = new Java.Util.Date();
//            str = alldate.format(date);
//            TimeZoneInfo tzi = TimeZoneInfo.Utc;
//            DateTime dt = new DateTime(date.Time);
//
//            long l = JavaSystem.CurrentTimeMillis();
//            DateTime dt0 = new DateTime(1970, 1, 1);
//            DateTime dt1 = dt0.AddMilliseconds(l);
//
//            dt = DateTime.Now;
//            String str2 = dt.ToLocalTime().toString("yyyyMMdd_HHmmss");
//            String str3 = dt.ToUniversalTime().toString("yyyyMMdd_HHmmss");
//
//
//            //
//            Java.Util.Date d1 = new Java.Util.Date(l);
//            str = alldate.format(d1);

            return 0;
        }


        //
        public static int testRwLock() {
            threadProc_testR[] trs = new threadProc_testR[10];
            threadProc_testW[] tws = new threadProc_testW[1];

            int i;
            for (i = 0; i < trs.length; i++) {
                trs[i] = new threadProc_testR();
                threadProc_testR pR = (threadProc_testR) trs[i];
                pR.m_id = i;
                trs[i].start();
            }
            for (i = 0; i < tws.length; i++) {
                tws[i] = new threadProc_testW();
                threadProc_testW pW = (threadProc_testW) tws[i];
                pW.m_id = i;
                tws[i].start();
            }

            //
            for (i=0; i<100;i++ ) {
                //qyFuncs.mySleep(5000);
                tmpFunc.qyFuncs.mySleep(1000);
            }

            return 0;

        }


        //
        public static class threadProc_testR extends Thread {
            public int m_id;


            @Override
            public void run() {
                int id = (int) m_id;
                String str;
                int i;

                //
                //qyFuncs.mySleep(1000);

                //
                for (i = 0; ; i++) {

                    //
                    try (qySyncCommProc.CQyRWLock rLock = new qySyncCommProc.CQyRWLock()) {

                        //
                        //rLock.m_bDbg=true;

                        //
                        Log.d("tn_msgInput","thread_r"  +  id  +  " before rlock");

                        //
                        if (rLock.rLock(testCls_Droid.mtxRw, 1000, "testR.370") != 0) continue;

                        str = String.format("rlock %s ok", id);
                        Log.d("tn_msgInput",str);

                        tmpFunc.qyFuncs.mySleep(3000);

                    } catch (Exception e) {
                        Log.e(e.getMessage());
                        return;
                    }


                    Log.d("tn_msgInput","r"  +  id  +  " rLock end");

                    tmpFunc.qyFuncs.mySleep(100);

                    //
                    continue;
                }

            }
        }

        //
        public static class threadProc_testW extends Thread {
            public int m_id;


            @Override
            public void run() {
                int id = (int) m_id;
                String str;
                int i;

                for (i = 0; ; i++) {

                    //
                    try (qySyncCommProc.CQyRWLock rwLock = new qySyncCommProc.CQyRWLock()) {
                        String w_hint = "threadProc_testW";

                        Log.d("tn_msgInput","thread_w"  +  m_id  +  "  before wLock");
                        if (rwLock.wLock(mtxRw, 1000, w_hint) != 0) {
                            Log.d("tn_msgInput","thread_w"  +  id  +  "wLock failed");
                            continue;
                        }
                        Log.d("tn_msgInput","thread_w"  +  id  +  "wLock ok");

                        //
                        tmpFunc.qyFuncs.mySleep(3000);

                    } catch (Exception e) {
                        Log.e(e.getMessage());
                        return;
                    }

                    str = String.format("wlock %s ends", id);
                    Log.d("tn_msgInput",str);

                    //
                    tmpFunc.qyFuncs.mySleep(100);
                    continue;
                }

            }

        }


    }
}
