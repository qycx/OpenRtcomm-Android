package com.qycx;

/**
 * 活动组（IM 群 id 列表）统一入口。
 * <p>
 * 地图通过 {@link Fragment_map#getTaskGrp()} 间接调用 {@link #getTaskGrp()}。
 * 优先从 {@link Fragment_imObjList#getGroupIds()} 动态读取；无数据时兜底 {@link #ACTIVE_GRP_ID}。
 * </p>
 */
public final class TaskActiveGrp {

    /** 动态列表为空时的兜底活动组 IM 群 id。 */
    public static final long ACTIVE_GRP_ID = 103L;

    private TaskActiveGrp() {
    }

    /**
     * @return 活动组群 id 列表，非 null
     */
    public static long[] getTaskGrp() {
        long[] groupIds = Fragment_imObjList.getGroupIds();
      //  long[] groupIds = null;
        if (groupIds == null || groupIds.length == 0) {
            return new long[]{ACTIVE_GRP_ID};
        }
        return groupIds;
    }
}
