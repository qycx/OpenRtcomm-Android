package com.qycx;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.genericQueue;
import com.qycx.base.myDb;
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qyDefs;
import com.qycx.base.qyMcMain_global;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 活动组（IM 群 id）统一入口。
 * <p>
 * 地图通过 {@link com.south.map.ui.Fragment_map#getTaskGrp()} 间接调用 {@link #getTaskGrp()}。
 * 当前版本只返回用户选择的一个活动组；未选择时返回空数组，不再使用固定群兜底。
 * </p>
 */
public final class TaskActiveGrp {

    private static final String PREF_NAME = "config";
    private static final String PREF_SELECTED_GROUP_PREFIX = "selected_active_group_v2_";

    private static volatile long selectedGroupId;
    private static volatile boolean autoSelectedSingleGroup;

    private TaskActiveGrp() {
    }

    /**
     * @return 当前活动组群 id 列表，非 null。未选择时为空数组。
     */
    public static long[] getTaskGrp() {
        long groupId = getSelectedGroupId();
        if (groupId <= 0 || !isSelfMemberOfGroup(groupId)) {
            return new long[0];
        }
        return new long[]{groupId};
    }

    public static long getSelectedGroupId() {
        return selectedGroupId;
    }

    public static boolean hasSelectedGroup() {
        return selectedGroupId > 0 && isSelfMemberOfGroup(selectedGroupId);
    }

    public static void setSelectedGroupId(long groupId) {
        selectedGroupId = groupId > 0 ? groupId : 0;
        autoSelectedSingleGroup = false;
        persistSelectedGroup();
    }

    public static void clearSelectedGroup() {
        selectedGroupId = 0;
        autoSelectedSingleGroup = false;
        persistSelectedGroup();
    }

    public static String getSelectedGroupDisplayName() {
        return getGroupDisplayName(selectedGroupId);
    }

    public static String getGroupDisplayName(long groupId) {
        if (groupId <= 0) return "";
        String name = Fragment_imObjList.getGroupName(groupId);
        if (!TextUtils.isEmpty(name)) return name;
        return "群" + groupId;
    }

    public static List<GroupItem> getMyGroups() {
        long[] ids = Fragment_imObjList.getGroupIds();
        List<GroupItem> out = new ArrayList<>();
        if (ids == null) return out;
        Set<Long> seen = new HashSet<>();
        for (long id : ids) {
            if (id <= 0 || seen.contains(id)) continue;
            seen.add(id);
            if (!isSelfMemberOfGroup(id)) continue;
            out.add(new GroupItem(id, getGroupDisplayName(id)));
        }
        return out;
    }

    public static long[] getMyGroupIds() {
        List<GroupItem> groups = getMyGroups();
        long[] ids = new long[groups.size()];
        for (int i = 0; i < groups.size(); i++) {
            ids[i] = groups.get(i).groupId;
        }
        return ids;
    }

    /**
     * 登录/群列表刷新后解析当前选择。返回 true 表示当前选择发生变化。
     */
    public static boolean resolveSelectionAfterLogin() {
        List<GroupItem> groups = getMyGroups();
        long old = selectedGroupId;
        if (groups.isEmpty()) {
            selectedGroupId = 0;
            autoSelectedSingleGroup = false;
            persistSelectedGroup();
            return old != selectedGroupId;
        }

        if (groups.size() > 1 && autoSelectedSingleGroup) {
            selectedGroupId = 0;
            autoSelectedSingleGroup = false;
            persistSelectedGroup();
            return old != selectedGroupId;
        }

        if (containsGroup(groups, selectedGroupId)) {
            return false;
        }

        long saved = loadPersistedSelectedGroup();
        if (containsGroup(groups, saved)) {
            selectedGroupId = saved;
            autoSelectedSingleGroup = false;
            return old != selectedGroupId;
        }

        if (groups.size() == 1) {
            selectedGroupId = groups.get(0).groupId;
            autoSelectedSingleGroup = true;
            return old != selectedGroupId;
        }

        selectedGroupId = 0;
        autoSelectedSingleGroup = false;
        persistSelectedGroup();
        return old != selectedGroupId;
    }

    private static boolean containsGroup(List<GroupItem> groups, long groupId) {
        if (groupId <= 0 || groups == null) return false;
        for (GroupItem group : groups) {
            if (group != null && group.groupId == groupId) return true;
        }
        return false;
    }

    public static boolean isSelfMemberOfGroup(long grpId) {
        if (grpId <= 0) return false;
        long selfId = getSelfUserId();
        if (selfId <= 0) return false;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null || pQyMc.gui == null || !(pQyMc.gui.pDb instanceof myDb.CMyDb)) return false;
        myDb.CMyDb db = (myDb.CMyDb) pQyMc.gui.pDb;
        if (db.m_var_pQ_qyImGrpMemTab == null) return false;
        Set<Long> members = new HashSet<>();
        genericQueue.qyFuncs.qTraverse(db.m_var_pQ_qyImGrpMemTab, tmpHandler_collectGrpMemberIds, grpId, members);
        return members.contains(selfId);
    }

    public static long getSelfUserId() {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc != null ? pQyMc.get_pProcInfo() : null;
        if (pProcInfo == null || pProcInfo.pMisCnt == null) return 0;
        return pProcInfo.pMisCnt.idInfo.ui64Id;
    }

    private static final qyDefs.PF_commonHandler tmpHandler_collectGrpMemberIds = (Object p0, Object p1, Object p2) -> {
        long grpIdInfo = (long) p0;
        @SuppressWarnings("unchecked")
        Set<Long> out = (Set<Long>) p1;
        qnmCommProc_mis.IM_GRP_MEM pQMem = (qnmCommProc_mis.IM_GRP_MEM) p2;
        if (pQMem != null && pQMem.idInfo_grp.ui64Id == grpIdInfo) {
            out.add(pQMem.idInfo_mem.ui64Id);
        }
        return 0;
    };

    private static void persistSelectedGroup() {
        Context ctx = getContext();
        long selfId = getSelfUserId();
        if (ctx == null || selfId <= 0) return;
        ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putLong(PREF_SELECTED_GROUP_PREFIX + selfId, selectedGroupId)
                .apply();
    }

    private static long loadPersistedSelectedGroup() {
        Context ctx = getContext();
        long selfId = getSelfUserId();
        if (ctx == null || selfId <= 0) return 0;
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sp.getLong(PREF_SELECTED_GROUP_PREFIX + selfId, 0);
    }

    private static Context getContext() {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc != null && pQyMc.gui != null && pQyMc.gui.hMainWnd instanceof Context) {
            return (Context) pQyMc.gui.hMainWnd;
        }
        return MyApplication.mContext;
    }

    public static final class GroupItem {
        public final long groupId;
        public final String groupName;

        public GroupItem(long groupId, String groupName) {
            this.groupId = groupId;
            this.groupName = TextUtils.isEmpty(groupName) ? "群" + groupId : groupName;
        }
    }
}
