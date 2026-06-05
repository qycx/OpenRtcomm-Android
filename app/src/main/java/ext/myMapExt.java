package ext;

import static com.qysd.base.mapFunc.ancSndLocation;

import androidx.fragment.app.Fragment;

import com.qycx.Fragment_null;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qyMcMainWndProc;
import com.qycx.base.util.Log;
import com.qysd.base.MapExtTmpl;
import com.qysd.base.MapExtTmpl;

public class myMapExt extends MapExtTmpl {

    public  int  gui_onTimer(Object p0, Object pVar, Object p2) {

        qyMcMainWndProc.QY_MC_mainWndVar var = (qyMcMainWndProc.QY_MC_mainWndVar)pVar;
        //
        MapExtTmpl pBase = this;
        //
        ctxQmc.CCtxQmc pProcInfo = m_var.pProcInfo;
        if (null==pProcInfo)  return  -1;
        ctxQyMc.CCtxQyMc pQyMc = (ctxQyMc.CCtxQyMc)pProcInfo.pQyMc;
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
        if (null==pMisCnt)  return  -1;


        //
        if (0!=super.gui_onTimer(p0, pVar, p2))  return  -1;

        //
        if (  pQyMc.appParams.bDbg) {

            if  (  true  ) {
                //
                Log.d("at", "l36: test to sendLocation");

                if (0 == (var.loopCtrl % 5)) {
                    //
                    String str;
                    str = String.format("x=%d,y=%d,z=%d", var.loopCtrl, var.loopCtrl, var.loopCtrl);
                    long grp_idInfo = 107;
                    //
                    this.sendLocation(p0, str, grp_idInfo);
                }

            }

        }



        //
        return  0;
    }


        @Override
    public boolean bExists_locDataReq() {
        //
        return true;
    }

    @Override
    public int Application_onCreate_init()
    {
        //
        return  0;
    }


    //
    @Override
    public Fragment new_Fragment_map()
    {
        //
        Log.d("at","need to return real Fragment_map");
        //
        return  new Fragment_null();

        //  return new Fragment_map();
    }

    @Override
    public int sendLocation(Object p0, String locStr, long imGrp_related_ui64Id) {

        //
        ancSndLocation(0, locStr, imGrp_related_ui64Id);

        return 0;
    }

    @Override
    public int onRecv_locations(CParam_onRecv_locations param) {
        if  (  param==null)  return  -1;
        if  (  param.pTransferLocData==null)  return  -1;
        if  (  param.pTransferLocData instanceof qnmCommProc_mis.TransferLocData  )  {
            qnmCommProc_mis.TransferLocData  tld = (qnmCommProc_mis.TransferLocData) param.pTransferLocData;
            //
            Log.d("at","onRecv_locations: cnt "  +  tld.usCnt);

        }
        //
        return 0;
    }

    @Override
    public int sendMark(String markStr, long imGrp_related_ui64Id) {
        return 0;
    }

    @Override
    public int onRecv_marks(Param_onRecv_marks param) {
        return 0;
    }

    @Override
    public int onRecv_plottings(Param_onRecv_plottings param) {
        return 0;
    }

    //




}
