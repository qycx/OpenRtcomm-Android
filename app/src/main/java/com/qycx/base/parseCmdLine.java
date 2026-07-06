package com.qycx.base;

import com.qycx.base.util.Log;
import com.qycx.gleslibrary.BuildConfig;

public class parseCmdLine {


    public static int  parseCmdLine_qyMc(String  pCmdLine, qmcCmdParams.QMC_APP_PARAMS pParams)
    {
        //
        boolean bVal;
        bVal  =  BuildConfig.DEBUG;     //  没用

        //
        if  (  true  )  {
            pParams.bSupport_lyra = false;
        }

        //
        if  (  false  ) {
            pParams.bENABLE_MAP_TAB=true;

            //
            pParams.bStay_toAvCall = true;
            showInfo_open.qyFuncs.showInfo_open("Note: bStay_toAv set to true");
        }

        //
        if  (true) {
            pParams.bDbg = true;
            Log.d("qy","Note: parseCmdLine_qyMc: set bDbg to true");
        }

        //
        if  (  pParams.bDbg)  {

            //
            if  (  false ) {
                pParams.bTest_ancSndMarkToHg = true;
                Log.d("qy","Note: parseCmdLine_qyMc: set bTest_andSndMarkToHg to true");
            }
            //
            if  (  true  )  {
                pParams.bDbg_traceRt_a_rec=true;
                Log.d("at","Note: parseCmdLine_qyMc: set bDbg_traceRt_a_rec to true");
                //
                pParams.bDbg_traceRt_a_play=true;
                Log.d("at","Note: parseCmdLine_qyMc: set bDbg_traceRt_a_play to true");

            }


        }



        //
        return  0;
    }

}
