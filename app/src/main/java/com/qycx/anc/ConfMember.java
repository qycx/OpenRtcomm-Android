package com.qycx.anc;

import static com.qycx.Fragment_dlgTalk_msgList.talk_addNewMsg;
import static com.qycx.base.qmcCommFunc_mis.qyFuncs.M_getActiveMemDesc;

import com.qycx.Activity_dlgTalk;
import com.qycx.R;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.dlgTalkProc;
import com.qycx.base.genericQueue;
import com.qycx.base.getTalkerDesc;
import com.qycx.base.myDb;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qyCommProc_open;
import com.qycx.base.qyDefs;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.util.Log;

import java.util.ArrayList;
import java.util.List;

//

//
public class ConfMember {

    public int iConfMemType  =  0;
    //
    public long ui64Id  =  0;
    public String desc  =  "";
    public Integer iAvatar  =  0;
    //
    public int uiObjType  =  0;

    //
    public int usIndex_activeMem  =  0;       //  在activeMems里的index


    //
    public String statusStr  =  "";

    //
    public short usRunningStatus  =  0;

    //
    public long conf_ui64Id;
    public long conf_ui64Id_dwTickCnt_refreshed;

    //
    private boolean m_isSelected;

    //
    public ConfMember(){

    }

    public ConfMember(Integer avatar, String name, String status) {
        this.iAvatar = avatar;
        this.desc = name;
        this.statusStr = status;
    }

    //
    //private boolean m_bInited  =  false;
    //private List<ConfMember> m_memberList  = new ArrayList<>();



    //
    /*
    public List<ConfMember> getMemberList() {
        //List<Member> memberList = new ArrayList<>();
        if  (  !m_bInited) {
            m_memberList.add(new ConfMember(R.mipmap.avatar, "Jessica", "online"));
            m_memberList.add(new ConfMember(R.mipmap.avatar1, "Lucy", "online"));
            m_memberList.add(new ConfMember(R.mipmap.avatar2, "Sandy", "online"));
            m_memberList.add(new ConfMember(R.mipmap.avatar, "aaa", "online"));
            m_memberList.add(new ConfMember(R.mipmap.avatar1, "bbb", "online"));
            m_memberList.add(new ConfMember(R.mipmap.avatar2, "ccc", "online"));
            m_memberList.add(new ConfMember(R.mipmap.avatar, "Jessica", "online"));
            m_memberList.add(new ConfMember(R.mipmap.avatar1, "Lucy", "online"));
            m_memberList.add(new ConfMember(R.mipmap.avatar2, "Sandy", "online"));
            m_memberList.add(new ConfMember(R.mipmap.avatar, "aaa", "online"));
            m_memberList.add(new ConfMember(R.mipmap.avatar1, "bbb", "online"));
            m_memberList.add(new ConfMember(R.mipmap.avatar2, "ccc", "online"));
            //
            m_bInited=true;
        }
        return m_memberList;
    }

     */

    //
    public boolean isSelected() {
        return  m_isSelected;
    }

    //
    public void setSelected1(boolean selected)
    {
        m_isSelected=selected;
    }


    //
    public Integer getAvatar() {
        return iAvatar;
    }

    public void setAvatar(Integer avatar) {
        this.iAvatar = avatar;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }




}


