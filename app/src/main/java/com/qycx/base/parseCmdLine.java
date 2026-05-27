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
        if  (  true  ) {
            pParams.bENABLE_MAP_TAB=true;
        }

        //
        if  (true) {
            pParams.bDbg = true;
            Log.d("qy","Note: parseCmdLine_qyMc: set bDbg to true");
        }

        //
        return  0;
    }

}
