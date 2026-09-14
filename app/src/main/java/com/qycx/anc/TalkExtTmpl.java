package com.qycx.anc;


import com.qycx.base.ctxQmc;

public abstract class TalkExtTmpl {

    public class  TalkExtVar {

        //
        public ctxQmc.CCtxQmc pProcInfo;


        //
        public class ConfCtrl {										//  这个结构是从mcu下发下来的策略
            //
            public byte											bNoVDownload;
            //
            public int                                             iTaskId;

        };

        public ConfCtrl confCtrl = new ConfCtrl();



        //
    } ;

    public TalkExtVar m_var=new TalkExtVar();

    //
    // TalkAddlVarTmpl* new_talkAddlVar();

    //
    public  int  gui_onTimer(Object p0, Object pVar, Object p2)
    {
        return  0;
    }

    //
    //virtual int confData_init();
    //virtual int confData_exit();

    //
    public abstract int switchTransmissionMode(long talkerId, byte  bNoVDownloadVal);

    //
    public abstract boolean bNoVDownload(int iTaskId);


}
