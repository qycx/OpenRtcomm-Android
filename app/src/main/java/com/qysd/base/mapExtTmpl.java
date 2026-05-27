package com.qysd.base;

import androidx.fragment.app.Fragment;

import java.util.List;

//
public abstract class mapExtTmpl {

    //
    public static class AncLocation {
        long ui64Id;
        String  locStr;
    };

    //
    public static class Param_onRecv_locations
    {
        long imGrp_related_ui64Id;
        List<AncLocation> m_locs;
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
    abstract public int Application_onCreate_init();

    //
    abstract  public Fragment new_Fragment_map();

    //
    // 定位信息，一个字符串，关联一个组号，发送到 locServer_idInfo
    abstract public int sendLocation( String locStr, long imGrp_related_ui64Id   );


    //  接收很多定位信息的接口
    abstract public int onRecv_locations(Param_onRecv_locations param);


    // //  标注：一个字符串，关联一个组号, 发送到locServer_idInfo
    abstract public int sendMark( String markStr, long imGrp_related_ui64Id   );


    // 接收很多并标注信息的接口
    abstract public int onRecv_marks( Param_onRecv_marks param);


    // 接收标绘信息的接口
    abstract public int onRecv_plottings(Param_onRecv_plottings param);




}
