package com.qycx;


import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qycx.base.TableItem;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.dlgTalkProc_help;
import com.qycx.base.genericQueue;
import com.qycx.base.guiShare;
import com.qycx.base.imObj_proc;
import com.qycx.base.isCliHelpPublic;
import com.qycx.base.myDb;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.qnmCommProc;
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qyCusResPublic;
import com.qycx.base.qyDefs;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.qyWmComm;
import com.qycx.base.qyWndContentType;
import com.qycx.base.showPopupMenu;
import com.qycx.base.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Fragment_imObjList extends Fragment {
    private static final int CATEGORY_GRP = 0;
    private static final int CATEGORY_FRIEND = 1;

    MainActivity mainWnd;
    RecyclerView listView;
    TextView tv_topMenu;
    TextView m_tv_txtTitle;
    Adapter_imObjTreeList treeAdapter;

    public List<TableItem.TableItem_imObj> mTableItems_imObj = new ArrayList<>();

    /** 供 {@link TaskActiveGrp} 读取 IM 群 id；与 {@link #mTableItems_imObj} 在 fillList 后同步。 */
    private static final List<TableItem.TableItem_imObj> sTableItems_imObj = new ArrayList<>();

    private boolean grpCategoryExpanded = true;
    private boolean friendCategoryExpanded = true;
    private final Set<Long> expandedGroupIds = new HashSet<>();

    private final qyDefs.PF_commonHandler tmpHandler_collectGrpMemberIds = (Object p0, Object p1, Object p2) -> {
        long grpIdInfo = (long) p0;
        @SuppressWarnings("unchecked")
        Set<Long> out = (Set<Long>) p1;
        qnmCommProc_mis.IM_GRP_MEM pQMem = (qnmCommProc_mis.IM_GRP_MEM) p2;
        if (pQMem != null && pQMem.idInfo_grp.ui64Id == grpIdInfo) {
            out.add(pQMem.idInfo_mem.ui64Id);
        }
        return 0;
    };

    int th_fillList_imObj(imObj_proc.QMem_imObj_intQ pMem) {
        int iErr = -1;

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;

        qnmCommProc_mis.QY_MESSENGER_INFO msgrInfo = new qnmCommProc_mis.QY_MESSENGER_INFO();
        qnmCommProc_mis.QY_MESSENGER_REGINFO regInfo = new qnmCommProc_mis.QY_MESSENGER_REGINFO();
        if (!myDb.QM_dbFuncs.pf_bGetMessengerInfoBySth.call(pDbFuncs, pQyMc.gui.pDb, pQyMc.cfg.db.iDbType, qnmCommProc.qyFuncs.CONST_fieldIdTable_en, "", pMem.idInfo, 0, msgrInfo, regInfo, null, null)) {
            Log.e("th_fillList_imObj pf_bGetMessengerInfoBySth failed");
            return iErr;
        }

        TableItem.TableItem_imObj item = new TableItem.TableItem_imObj();
        item.ui64Id = msgrInfo.idInfo.ui64Id;
        item.uiObjType = msgrInfo.uiType;

        if (msgrInfo.uiType == qyDefs.Consts.CONST_objType_imGrp) {
            qnmCommProc_mis.IM_GRP_INFO grpInfo = new qnmCommProc_mis.IM_GRP_INFO();
            if (!myDb.QM_dbFuncs.pf_bGetImGrpInfoBySth.call(pQyMc.gui.pDb, pQyMc.cfg.db.iDbType, "", msgrInfo.idInfo, grpInfo)) {
                Log.e("th_fillList_imObj pf_bGetImGrpInfoBySth failed");
            }
            item.grpName = grpInfo.name;
            Log.i(item.grpName);

            item.ImageResourceId = grpInfo.idInfo_creator.ui64Id == 0 ? R.drawable.grp : R.drawable.tmpgrp;
        } else {
            item.dw = regInfo.dw;
            item.bm = regInfo.bm;
            item.syr = regInfo.syr;
            item.ImageResourceId = R.drawable.contact;
        }
        int existIndex = -1;
        for (int i = 0; i < mTableItems_imObj.size(); i++) {
            if (mTableItems_imObj.get(i).ui64Id == item.ui64Id) {
                existIndex = i;
                break;
            }
        }
        if (existIndex >= 0) {
            mTableItems_imObj.set(existIndex, item);
        } else {
            mTableItems_imObj.add(item);
        }

        iErr = 0;
        return iErr;
    }

    public int fillList_imObj() {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

        //
        Log.d("anc","fillList_imObj: intQ.nQNodes "  +  pMisCnt.imObj_intQ.uiQNodes);

        //
        imObj_proc.QMem_imObj_intQ mem;
        while (pMisCnt.imObj_intQ.uiQNodes > 0){
//            Log.i("fillList_imObj: "+pMisCnt.imObj_intQ.uiQNodes);
            mem = (imObj_proc.QMem_imObj_intQ) genericQueue.qyFuncs.qGetMsg(pMisCnt.imObj_intQ);
            if (mem == null) {
                Log.i("get QMem_imObj_intQ finish");
                break;
            }
            th_fillList_imObj(mem);
        }

        sTableItems_imObj.clear();
        sTableItems_imObj.addAll(mTableItems_imObj);
        rebuildTreeRows();
        return 0;
    }

    /**
     * 返回联系人列表中所有 IM 群（{@link qyDefs.Consts#CONST_objType_imGrp}）的 id。
     */
    public static long[] getGroupIds() {
        ArrayList<Long> groupIds = new ArrayList<>();
        for (TableItem.TableItem_imObj item : sTableItems_imObj) {
            if (item != null && item.uiObjType == qyDefs.Consts.CONST_objType_imGrp && item.ui64Id > 0) {
                groupIds.add(item.ui64Id);
            }
        }
        long[] result = new long[groupIds.size()];
        for (int i = 0; i < groupIds.size(); i++) {
            result[i] = groupIds.get(i);
        }
        return result;
    }

    //
    public int display_imObjList() {
        rebuildTreeRows();
        return 0;
    }

    private void rebuildTreeRows() {
        if (treeAdapter == null) return;

        List<TableItem.TableItem_imObj> groups = new ArrayList<>();
        List<TableItem.TableItem_imObj> friends = new ArrayList<>();
        for (TableItem.TableItem_imObj item : mTableItems_imObj) {
            if (item == null) continue;
            if (item.uiObjType == qyDefs.Consts.CONST_objType_imGrp) {
                groups.add(item);
            } else {
                friends.add(item);
            }
        }
        groups.sort(Comparator.comparingLong(a -> a.ui64Id));
        friends.sort(Comparator.comparingLong(a -> a.ui64Id));

        List<Adapter_imObjTreeList.TreeRow> rows = new ArrayList<>();

        Adapter_imObjTreeList.TreeRow grpCat = new Adapter_imObjTreeList.TreeRow();
        grpCat.type = Adapter_imObjTreeList.TYPE_CATEGORY;
        grpCat.id = CATEGORY_GRP;
        grpCat.title = "群组";
        grpCat.expanded = grpCategoryExpanded;
        grpCat.indentDp = 8;
        rows.add(grpCat);

        if (grpCategoryExpanded) {
            for (TableItem.TableItem_imObj grp : groups) {
                boolean grpExpanded = expandedGroupIds.contains(grp.ui64Id);
                Adapter_imObjTreeList.TreeRow grpRow = new Adapter_imObjTreeList.TreeRow();
                grpRow.type = Adapter_imObjTreeList.TYPE_GROUP;
                grpRow.id = grp.ui64Id;
                grpRow.title = grp.grpName != null && !grp.grpName.isEmpty() ? grp.grpName : String.valueOf(grp.ui64Id);
                grpRow.subtitle = String.format("(分组) %s", grp.ui64Id);
                grpRow.imageRes = grp.ImageResourceId;
                grpRow.expanded = grpExpanded;
                grpRow.indentDp = 24;
                rows.add(grpRow);

                if (grpExpanded) {
                    List<TableItem.TableItem_imObj> members = loadGroupMembers(grp.ui64Id);
                    for (TableItem.TableItem_imObj mem : members) {
                        rows.add(buildContactRow(mem, 48));
                    }
                }
            }
        }

        Adapter_imObjTreeList.TreeRow friendCat = new Adapter_imObjTreeList.TreeRow();
        friendCat.type = Adapter_imObjTreeList.TYPE_CATEGORY;
        friendCat.id = CATEGORY_FRIEND;
        friendCat.title = "好友";
        friendCat.expanded = friendCategoryExpanded;
        friendCat.indentDp = 8;
        rows.add(friendCat);

        if (friendCategoryExpanded) {
            for (TableItem.TableItem_imObj friend : friends) {
                rows.add(buildContactRow(friend, 24));
            }
        }

        treeAdapter.setRows(rows);
    }

    private Adapter_imObjTreeList.TreeRow buildContactRow(TableItem.TableItem_imObj item, int indentDp) {
        Adapter_imObjTreeList.TreeRow row = new Adapter_imObjTreeList.TreeRow();
        row.type = Adapter_imObjTreeList.TYPE_CONTACT;
        row.id = item.ui64Id;
        row.title = item.syr != null && !item.syr.isEmpty() ? item.syr : String.valueOf(item.ui64Id);
        row.subtitle = String.format("%s %s %s", item.dw, item.bm, item.ui64Id);
        row.imageRes = item.ImageResourceId != 0 ? item.ImageResourceId : R.drawable.contact;
        row.online = isUserOnline(item.ui64Id);
        row.indentDp = indentDp;
        return row;
    }

    private List<TableItem.TableItem_imObj> loadGroupMembers(long grpId) {
        Set<Long> memberIds = loadGroupMemberIds(grpId);
        List<TableItem.TableItem_imObj> members = new ArrayList<>();
        for (Long memberId : memberIds) {
            if (memberId == null || memberId <= 0) continue;
            TableItem.TableItem_imObj item = findOrLoadContactItem(memberId);
            if (item != null) {
                members.add(item);
            }
        }
        members.sort(Comparator.comparingLong(a -> a.ui64Id));
        return members;
    }

    private Set<Long> loadGroupMemberIds(long grpId) {
        Set<Long> members = new HashSet<>();
        if (grpId <= 0) return members;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return members;
        try {
            myDb.CMyDb db = (myDb.CMyDb) pQyMc.gui.pDb;
            if (db == null || db.m_var_pQ_qyImGrpMemTab == null) return members;
            genericQueue.qyFuncs.qTraverse(db.m_var_pQ_qyImGrpMemTab, tmpHandler_collectGrpMemberIds, grpId, members);
        } catch (Exception ignored) {
        }
        return members;
    }

    private TableItem.TableItem_imObj findOrLoadContactItem(long ui64Id) {
        for (TableItem.TableItem_imObj item : mTableItems_imObj) {
            if (item != null && item.ui64Id == ui64Id && item.uiObjType != qyDefs.Consts.CONST_objType_imGrp) {
                return item;
            }
        }
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return null;
        myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
        qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
        idInfo.ui64Id = ui64Id;
        qnmCommProc_mis.QY_MESSENGER_INFO msgrInfo = new qnmCommProc_mis.QY_MESSENGER_INFO();
        qnmCommProc_mis.QY_MESSENGER_REGINFO regInfo = new qnmCommProc_mis.QY_MESSENGER_REGINFO();
        if (!myDb.QM_dbFuncs.pf_bGetMessengerInfoBySth.call(
                pDbFuncs, pQyMc.gui.pDb, pQyMc.cfg.db.iDbType,
                qnmCommProc.qyFuncs.CONST_fieldIdTable_en, "", idInfo, 0,
                msgrInfo, regInfo, null, null)) {
            TableItem.TableItem_imObj fallback = new TableItem.TableItem_imObj();
            fallback.ui64Id = ui64Id;
            fallback.syr = String.valueOf(ui64Id);
            fallback.ImageResourceId = R.drawable.contact;
            return fallback;
        }
        if (msgrInfo.uiType == qyDefs.Consts.CONST_objType_imGrp) {
            return null;
        }
        TableItem.TableItem_imObj item = new TableItem.TableItem_imObj();
        item.ui64Id = ui64Id;
        item.uiObjType = msgrInfo.uiType;
        item.dw = regInfo.dw;
        item.bm = regInfo.bm;
        item.syr = regInfo.syr;
        item.ImageResourceId = R.drawable.contact;
        return item;
    }

    private boolean isUserOnline(long userId) {
        if (userId <= 0) return false;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return false;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo != null ? pProcInfo.pMisCnt : null;
        if (pMisCnt != null && userId == pMisCnt.idInfo.ui64Id) {
            return pMisCnt.status.usRunningStatus == qyCusResPublic.Consts.CONST_usRunningStatus_online;
        }
        myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
        if (pDbFuncs == null || pQyMc.gui.pDb == null) return false;
        qyDefs.QY_MESSENGER_ID idInfo = new qyDefs.QY_MESSENGER_ID();
        idInfo.ui64Id = userId;
        qnmCommProc_mis.QY_MESSENGER_INFO msgrInfo = new qnmCommProc_mis.QY_MESSENGER_INFO();
        qnmCommProc_mis.QY_MESSENGER_REGINFO regInfo = new qnmCommProc_mis.QY_MESSENGER_REGINFO();
        if (!myDb.QM_dbFuncs.pf_bGetMessengerInfoBySth.call(
                pDbFuncs, pQyMc.gui.pDb, pQyMc.cfg.db.iDbType,
                qnmCommProc.qyFuncs.CONST_fieldIdTable_en, "", idInfo, 0,
                msgrInfo, regInfo, null, null)) {
            return false;
        }
        return msgrInfo.usRunningStatus == qyCusResPublic.Consts.CONST_usRunningStatus_online;
    }

    private void talkToMessenger(long ui64Id) {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return;
        Log.i(ui64Id + " clicked ");
        boolean bStay_toAvCall = false;
        isCliHelpPublic.PARAM_talkToMessenger param = new isCliHelpPublic.PARAM_talkToMessenger();
        dlgTalkProc_help.qyFuncs_Droid.talkToMessenger(pQyMc, ui64Id, false, true, bStay_toAvCall, param);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainWnd = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fg_imobjlist, container, false);
        view.findViewById(R.id.img_back).setOnClickListener(v -> quitApp());
        tv_topMenu = view.findViewById(R.id.txt_topMenu);
        tv_topMenu.setOnClickListener(Tv_topMenu_Click);

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return view;

        m_tv_txtTitle = view.findViewById(R.id.txt_title);
        m_tv_txtTitle.setText(mainWnd.m_str_title);

        listView = view.findViewById(R.id.list);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        treeAdapter = new Adapter_imObjTreeList(inflater);
        treeAdapter.setListener(new Adapter_imObjTreeList.Listener() {
            @Override
            public void onCategoryClick(int categoryKey) {
                if (categoryKey == CATEGORY_GRP) {
                    grpCategoryExpanded = !grpCategoryExpanded;
                } else if (categoryKey == CATEGORY_FRIEND) {
                    friendCategoryExpanded = !friendCategoryExpanded;
                }
                rebuildTreeRows();
            }

            @Override
            public void onGroupClick(long grpId) {
                if (expandedGroupIds.contains(grpId)) {
                    expandedGroupIds.remove(grpId);
                } else {
                    expandedGroupIds.add(grpId);
                }
                rebuildTreeRows();
            }

            @Override
            public void onGroupLongClick(long grpId) {
                talkToMessenger(grpId);
            }

            @Override
            public void onContactClick(long userId) {
                talkToMessenger(userId);
            }
        });
        listView.setAdapter(treeAdapter);
        rebuildTreeRows();
        return view;
    }

    private void quitApp() {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null) return;
        guiShare.GuiShare.pf_PostMessage_new.get().call(
                pQyMc.gui.hMainWnd,
                qyWndContentType.Consts.CONST_qyWndContentType_mainWnd,
                qyWmComm.Consts.CONST_qyWm_postComm,
                qyWmComm.Consts.CONST_qyWmParam_closeWnd,
                0);
    }

    showPopupMenu.qyFuncs_Droid.PF_procMenu procMenu = (Context context, int menuId) -> {
        if (menuId == R.id.quit) {
            quitApp();
        }
        return 0;
    };

    //
    public int gui_onTimer(Object p0, Object p1, Object p2) {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        MainActivity mainWnd = (MainActivity) getActivity();
        if (mainWnd == null) return 0;

        if (pProcInfo.av.talk.bChannelReady) {
            m_tv_txtTitle.setText(mainWnd.m_str_title);
        } else {
            m_tv_txtTitle.setText(mainWnd.m_str_title + "(离线)");
        }
        return 0;
    }


    //
    private View.OnClickListener Tv_topMenu_Click = (View view) -> {
        Context context = getActivity();
        showPopupMenu.qyFuncs_Droid.showPopupMenu(context, view, R.menu.layout_imobjlist_topmenu, procMenu);
    };
}
