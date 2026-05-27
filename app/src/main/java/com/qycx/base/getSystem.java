package com.qycx.base;

import static com.qycx.base.qyMcMain_global.g.g_pQyMc;
import static com.qycx.base.systemDefs.Consts.manufacturer_A10ST;
import static com.qycx.base.systemDefs.Consts.manufacturer_tjcxzx;
import static com.qycx.base.systemDefs.Consts.manufacturer_yuerin;
import static com.qycx.base.systemDefs.Consts.manufacturer_alps;
import static com.qycx.base.systemDefs.Consts.manufacturer_huaWei;
import static com.qycx.base.systemDefs.Consts.manufacturer_mbk;
import static com.qycx.base.systemDefs.Consts.manufacturer_oppo;
import static com.qycx.base.systemDefs.Consts.manufacturer_unione;
import static com.qycx.base.systemDefs.Consts.manufacturer_vivo;
import static com.qycx.base.systemDefs.Consts.manufacturer_xiaoMi;
import static com.qycx.base.systemDefs.Consts.model_A10ST_A10ST;
import static com.qycx.base.systemDefs.Consts.model_alps_k6853v1_64_6360;
import static com.qycx.base.systemDefs.Consts.model_huaWei_BRA_AL00;
import static com.qycx.base.systemDefs.Consts.model_huaWei_TAHAN00m;
import static com.qycx.base.systemDefs.Consts.model_mbk_t2101;
import static com.qycx.base.systemDefs.Consts.model_tjcxzx_rk3588_s;
import static com.qycx.base.systemDefs.Consts.model_unione_ua100;
import static com.qycx.base.systemDefs.Consts.model_xiaoMi_redMi10;
import static com.qycx.base.systemDefs.Consts.model_yuerin_aMeetingBox_A1Pro;

import android.os.Build;

import com.qycx.base.util.Log;
import com.qycx.tmpffmpeglibrary.TmpCusSystem;
import com.qycx.tmpffmpeglibrary.TmpFfmpeg;

import java.lang.reflect.Method;

public class getSystem
{
    public static class qyFuncs_Droid
    {

        //
        static qyTypes.QY_DMITEM[] manufacturerTable = new qyTypes.QY_DMITEM[]
{
            new qyTypes.QY_DMITEM(manufacturer_xiaoMi,"Xiaomi"),
            new qyTypes.QY_DMITEM(manufacturer_huaWei,"HUAWEI"),
            new qyTypes.QY_DMITEM(manufacturer_mbk,"MBK"),
            new qyTypes.QY_DMITEM(manufacturer_unione,"UniOne"),
            new qyTypes.QY_DMITEM(manufacturer_vivo,"vivo"),
            new qyTypes.QY_DMITEM(manufacturer_oppo,"OPPO"),
            new qyTypes.QY_DMITEM(manufacturer_alps,"alps"),
            new qyTypes.QY_DMITEM(manufacturer_yuerin,"yuerin"),
            new qyTypes.QY_DMITEM(manufacturer_A10ST,"A10ST"),
            //
            new qyTypes.QY_DMITEM(manufacturer_tjcxzx,"rockchip"),

        //



        new qyTypes.QY_DMITEM(-1,""),


};

        static qyTypes.QY_DMITEM[] modelTable_xiaoMi = new qyTypes.QY_DMITEM[]
        {
            new qyTypes.QY_DMITEM(model_xiaoMi_redMi10,"M2004J7AC"),
            new qyTypes.QY_DMITEM(-1,"M2004J7AC"),

        };

        static qyTypes.QY_DMITEM[] modelTable_huaWei = new qyTypes.QY_DMITEM[]
        {
            new qyTypes.QY_DMITEM(model_huaWei_TAHAN00m,"TAH-AN00m"),
            new qyTypes.QY_DMITEM(model_huaWei_BRA_AL00,"BRA-AL00"),
            new qyTypes.QY_DMITEM(-1,"M2004J7AC"),

        };

        static qyTypes.QY_DMITEM[] modelTable_mbk = new qyTypes.QY_DMITEM[]
        {
            new qyTypes.QY_DMITEM(model_mbk_t2101,"MBK-T2101"),
            new qyTypes.QY_DMITEM(-1,"M2004J7AC"),
        };

        static qyTypes.QY_DMITEM[] modelTable_unione = new qyTypes.QY_DMITEM[]
        {
            new qyTypes.QY_DMITEM(model_unione_ua100,"UA100"),
            new qyTypes.QY_DMITEM(-1,"M2004J7AC"),
        };


        static qyTypes.QY_DMITEM[] modelTable_alps = new qyTypes.QY_DMITEM[]
                {
                        new qyTypes.QY_DMITEM(model_alps_k6853v1_64_6360,"k6853v1_64_6360"),
                        new qyTypes.QY_DMITEM(-1,"M2004J7AC"),
                };

        static  qyTypes.QY_DMITEM[] modelTable_yuerin=new qyTypes.QY_DMITEM[]
                {
                        new qyTypes.QY_DMITEM(model_yuerin_aMeetingBox_A1Pro,"aMeetingBox_A1Pro"),
                        new qyTypes.QY_DMITEM(-1,"M2004J7AC"),
                };

        static  qyTypes.QY_DMITEM[] modelTable_A10ST=new qyTypes.QY_DMITEM[]
                {
                        new qyTypes.QY_DMITEM(model_A10ST_A10ST,"A10ST"),
                        new qyTypes.QY_DMITEM(-1,"M2004J7AC"),
                };

        //
        //manufacturer = "rockchip"
        //model = "rk3588_s"

        //
        static  qyTypes.QY_DMITEM[] modelTable_tjczax=new qyTypes.QY_DMITEM[]
                {
                        new qyTypes.QY_DMITEM(model_tjcxzx_rk3588_s,"rk3588_s"),
                        new qyTypes.QY_DMITEM(-1,"M2004J7AC"),
                };



        //
        private static String HARMONY_OS = "harmony";

        //判断是否为鸿蒙
        public static boolean isHarmonyOSa()
        {
            try
            {
                Class clz = Class.forName("com.huawei.system.BuildEx");
                Method method = clz.getMethod("getOsBrand");

                ClassLoader classLoader = clz.getClassLoader();

                //Log.d(TAG,"classLoader: " + classLoader);

                //BootClassLoader的parent为null
                if (classLoader != null && classLoader.getParent() == null)
                {


                    //return method.invoke(clz).toString();//返回获取的名称
                    String name = method.invoke(clz).toString();
                    boolean b = HARMONY_OS.equals(name);


                    //****返回是否是鸿蒙系统 自行注释并将方法返回值改为boolean
                    //return HARMONY_OS.Equals(method.Invoke(clz));
                    return b;
                }
            }
            catch (ClassNotFoundException e)
            {
            }
            catch (NoSuchMethodException e)
            {
            }
            catch (Exception e)
            {
            }
            return false;

        }
        //
        public static guiShare.PF_getSystemInfo getSystemInfo=()->
        {
            int iManufactor;
            int iModel;
            boolean b_isHarmony;

            String versionCode = "";

            String manufacturer = Build.MANUFACTURER;

            String model = Build.MODEL;

            //
            Log.d("qy","getSystemInfo: manufacturer is ["  +  manufacturer  +  "]");
            Log.d("qy","getSystemInfo: model is ["  +  model  +  "]");

            //
            if  (  false  )  {
                //
                Log.d("anc","for test. to get model");

                //
                manufacturer = "rockchip";
                model = "rk3588_s";
            }


            //
            iManufactor = 0;
            iModel = 0;
            b_isHarmony = false;
            b_isHarmony = qyFuncs_Droid.isHarmonyOSa();

            //
            if (null != manufacturer)
            {
                int iVal;
                //
                char[] m1 = manufacturer.toLowerCase().toCharArray();
                String ss = "mbk";
                char[] s1 = ss.toLowerCase().toCharArray();
                int i;
                boolean bErr = false;
                for  (  i  =  0;  i  <  ss.length() ; i++)
                {
                    if (s1[i] != m1[i])
                    {
                        bErr = true;
                        break;
                    }
                    continue;
                }
                if (!bErr)
                {
                    iManufactor = manufacturer_mbk;
                }
                else
                {
                    //
                    iVal = qyDmItemCommProc.qyFuncs.qyGetTypeByDes(manufacturerTable, manufacturer);
                    if (iVal < 0) iVal = 0;
                    iManufactor = iVal;
                }
                //
                qyTypes.QY_DMITEM[] pModelTable = null;
                switch (iManufactor)
                {
                    case manufacturer_xiaoMi:
                        pModelTable = modelTable_xiaoMi;
                        break;
                    case manufacturer_huaWei:
                        pModelTable = modelTable_huaWei;
                        break;
                    case manufacturer_mbk:
                        pModelTable = modelTable_mbk;
                        break;
                    case manufacturer_unione:
                        pModelTable = modelTable_unione;
                        break;
                    case  manufacturer_alps:
                          pModelTable  =  modelTable_alps;
                          break;
                    case manufacturer_yuerin:
                         pModelTable  =  modelTable_yuerin;
                         break;
                    case  manufacturer_A10ST:
                          pModelTable  =  modelTable_A10ST;
                          break;
                    case  manufacturer_tjcxzx:
                          pModelTable=modelTable_tjczax;
                          break;
                    default:
                        break;
                } 
                if (pModelTable != null)
                {
                    iVal = qyDmItemCommProc.qyFuncs.qyGetTypeByDes(pModelTable, model);
                    if (iVal < 0) iVal = 0;
                    //
                    iModel = iVal;
                }

                

                //versionCode = SystemProperty("ro.miui.version.code_time");

            }

            //
            Log.d("qy","getSystemInfo: iManufacturer "  +  iManufactor  +  "."  +  " iModel "  +  iModel);

            //
            ctxQyMc.CCtxQyMc pQyMc  =  g_pQyMc;
            pQyMc.iManufacturer  =  iManufactor;
            pQyMc.iModel  =  iModel;
            pQyMc.b_isHarmony  =   b_isHarmony;

            //
            return;
        };


        //
        public static guiShare.PF_customSystem customSystem=()->
        {
            ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            //
            switch(pQyMc.iManufacturer)
            {
                case manufacturer_xiaoMi:
                     switch (pQyMc.iModel)
                    {
                        case model_xiaoMi_redMi10:
                            pProcInfo.cfg.b_no_KeyLowLatency_a = true;
                            break;
                        default:
                            break;
                    }
                    break;
                case manufacturer_mbk:
                     switch(pQyMc.iModel)
                    {
                        case model_mbk_t2101:
                            pProcInfo.cfg.b_no_KeyLowLatency_a = true;
                            break;
                        default:
                            break;
                    }
                    break;
                case manufacturer_unione:
                    switch (pQyMc.iModel)
                    {
                        case model_unione_ua100:
                            pProcInfo.cfg.b_no_4k = true;
                            break;
                        default:
                            break;
                    }
                    break;
                case  manufacturer_alps:
                case  manufacturer_tjcxzx:
                    switch (pQyMc.iModel) {
                        case model_alps_k6853v1_64_6360:
                        default:

                            //
                            break;
                    }

                    break;
                //
                case  manufacturer_yuerin:
                    pProcInfo.cfg.bUseHorizontalScreen  =  true;
                    break;

                case  manufacturer_A10ST:
                      pProcInfo.cfg.b_no_rotateScreen  =  true;         //
                      break;
                default:
                    break;

            }

            //  如果要运行debug的功能，要打开此开关量
            if  (  true  ) {
                Log.d("qy","如果要运行debug的功能，要打开此开关量");
                pProcInfo.cfg.b_dbg = true;
            }

            //
            set100kParam();


            // 使用双目相机
            if  (  false )  {
                //
                Log.d("anc","这里打开了双目相机的功能");

                //
                pProcInfo.bUseSmxj  =  true;
                //
                pProcInfo.smxj.cfg.iW  =  640;
                pProcInfo.smxj.cfg.iH  =  480;
            }

            //  使用头盔
            if  (  false  ) {
                Log.d("qy", "setSpeakerOn_false. 主要对应用在头盔");
                //
                //pProcInfo.cfg.b_setSpeakerphoneOn_false = true;
                pProcInfo.cfg.bUse_asTk = true;

                //
            }

            //
            if  (  pProcInfo.cfg.bUse_asTk)  {
                //
                pProcInfo.cfg.bUseAs_SCREEN_ORIENTATION_LANDSCAPE  =  true;
                //
                pProcInfo.cfg.bUse8kSampleRatePcm  =  true;
                //
                pProcInfo.cfg.bAutoAcceptTaskAv  =  true;
                //
                pProcInfo.cfg.bSupport_autoLogin=true;      //  如果bSupport_autoLogin为true
                pProcInfo.cfg.bAutoLogin  =  true;

            }



            //
            if  (  false  )  {
                if  (  false  ) {
                    Log.e("for test: use8kSampleRatePcm set to true");
                    //pProcInfo.cfg.bUse8kSampleRatePcm  =  true;
                }
                if  (  true  )  {
                    Log.e("for test: bAutoAcceptTaskAv set to true");
                    pProcInfo.cfg.bAutoAcceptTaskAv  =  true;
                }
                if  (  true  )  {
                    Log.e("for test: bAutoLogin set to true");
                    pProcInfo.cfg.bSupport_autoLogin=true;
                    pProcInfo.cfg.bAutoLogin  =  true;
                }
            }

            //
            return 0;
        };

        //
        public static int set100kParam()
        {
            ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
            if  (  pQyMc ==  null) return -1;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if  (  pProcInfo==null)  return  -1;


            //
            switch ( pQyMc.iManufacturer)  {

                case  manufacturer_huaWei:
                default:

                     /*
                      pProcInfo.use100k_v_w  =  240;    //480;
                      pProcInfo.use100k_v_h  =  320;    //640;
                      //
                      pProcInfo.use100k_enc_sleepInMs  =  4000;//2000;
                      //
                      pProcInfo.use100k_v_biterate  =  10000;    //10000;
                      //
                      pProcInfo.use100k_v_frameRate  =  1;

                      pProcInfo.use100k_v_perSnd_sleepInMs  =  80;

                      //
                      pProcInfo.use100k_v_w  =  480;    //480;
                      pProcInfo.use100k_v_h  =  640;    //640;
                      //
                      pProcInfo.use100k_enc_sleepInMs  =  1000;//2000;
                      //
                      pProcInfo.use100k_v_biterate  =  25000;    //10000;
                      //
                      pProcInfo.use100k_v_perSnd_sleepInMs=1;

                    //
                    pProcInfo.use100k_enc_sleepInMs  =  333;//2000;

                      //
                      pProcInfo.use100k_v_frameRate  =  3;
                      pProcInfo.use100k_v_I_FRAME_INTERVAL  =  10;
                      //
                    pProcInfo.use100k_v_frameRate  =  20;//5;
                    pProcInfo.use100k_v_I_FRAME_INTERVAL  =  3  *  pProcInfo.use100k_v_frameRate;

                    //
                    pProcInfo.use100k_enc_sleepInMs  =  (int)(1000.  /   pProcInfo.use100k_v_frameRate);//2000;

                    //
                      pProcInfo.use100k_v_biterate  =  35000;    //10000;
                    pProcInfo.use100k_v_biterate  =  50000;    //10000;

                      //
                      if  (  false  ) {
                          pProcInfo.use100k_v_w = 144;
                          pProcInfo.use100k_v_h = 176;
                      }

                      */

                    //
                      com.qycx.tmpffmpeglibrary.TmpCusSystem.doCus100k(false, 0, 0,  pProcInfo.cfgInfo100k);

                      //
                      if  (  true  )  {


                      }


                      //
                      break;
            }


            //
            return  0;
        }

    }
}


