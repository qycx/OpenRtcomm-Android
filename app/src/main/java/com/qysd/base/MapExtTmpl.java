package com.qysd.base;

import static com.qycx.base.traceLog.qyFuncs.traceLog;
import static com.qysd.base.mapFunc.ancSndProcLocReq;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.qyMcMainWndProc;

import java.util.List;
import java.util.Objects;

//
public abstract class MapExtTmpl {

    //
    public static class AncLocation {
        long ui64Id;
        String  locStr;
    };

    //
    public static class CParam_onRecv_locations
    {
        public Object  pTransferLocData;
    };

    /////////////////////////
    //
    public static class AncMark {
        long ui64Id;
        String markStr;
    };

    //
    public static class Param_onRecv_marks {
        long  imGrp_ui64Id;
        List<AncMark>  m_marks;
    };

    /////////////////////////////////////
    //
    public static class AncPlotting {
        long ui64Id;
        String plottingStr;
    };

    public static class  Param_onRecv_plottings
    {
        long imGrp_ui64Id;
        List<AncPlotting> m_plottings;
    }

    //
    public static class MapExtVar{

        //
        public ctxQmc.CCtxQmc          pProcInfo;

        //
        public long                    locServIdInfo;
        //
        public String                  mapServCfgStr;     //  map server的配置字符串，建议 ip:port7777


        //
    };

    //
    public MapExtVar  m_var=new MapExtVar();

    //
    public  int  gui_onTimer(Object p0, Object pVar, Object p2) {

        if (null==pVar)  return  -1;

        qyMcMainWndProc.QY_MC_mainWndVar var = (qyMcMainWndProc.QY_MC_mainWndVar)pVar;

        ctxQmc.CCtxQmc pProcInfo = m_var.pProcInfo;
        if (null==pProcInfo)  return  -1;
        ctxQyMc.CCtxQyMc pQyMc = pProcInfo.pQyMc;
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
        if (null==pMisCnt)  return  -1;

        //
        if (!pQyMc.bLogon)  return  -1;
        //
        /*
        if (pProcInfo.getMcuType()==CONST_mcuType_locServ ) {  //  定位服务器不能使用以下功能
            return  -1;
        }
        */


        //
        if (true) {
            //traceLog((TCHAR*)_T("map.timer: mainWnd.loopCtrl %d"), var.loopCtrl);
        }

        //
        if (0==m_var.locServIdInfo  ||  0==m_var.mapServCfgStr.length()) {
            if (0==(var.loopCtrl % 5)) {

                //
                ancSndProcLocReq(pMisCnt, 0);
            }
        }



        //
        return  0;


    }

    //
    abstract public boolean bExists_locDataReq();


    //
    abstract public int Application_onCreate_init();

    //
    abstract  public Fragment new_Fragment_map();

    //
    // 定位信息，一个字符串，关联一个组号，发送到 locServer_idInfo
    abstract public int sendLocation( Object p0, String locStr, long imGrp_related_ui64Id   );


    //  接收很多定位信息的接口
    abstract public int onRecv_locations(CParam_onRecv_locations param);


    // //  标注：一个字符串，关联一个组号, 发送到locServer_idInfo
    abstract public int sendMark( String markStr, long imGrp_related_ui64Id   );


    // 接收很多并标注信息的接口
    abstract public int onRecv_marks( Param_onRecv_marks param);


    // 接收标绘信息的接口
    abstract public int onRecv_plottings(Param_onRecv_plottings param);




}
