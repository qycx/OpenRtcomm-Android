package ext;



import static com.qysd.base.mapFunc.ancSndLocation;
import static com.qysd.base.mapFunc.ancSndMarkToHg;



import androidx.fragment.app.Fragment;



import com.qycx.Fragment_map;

import com.qycx.MainActivity;

import com.qycx.TaskActiveGrp;

import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.genericQueue;
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qyDefs;
import com.qycx.base.myDb;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.util.Log;

import com.qysd.base.MapExtTmpl;

import com.qysd.base.MapLocKeyword;

import com.qysd.base.MapLocSettings;

import com.qysd.base.MapLocStrCrypto;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;



public class myMapExt extends MapExtTmpl {



    @Override

    public boolean bExists_locDataReq() {

        return true;

    }



    @Override

    public int Application_onCreate_init() {

        return 0;

    }



    @Override

    public Fragment new_Fragment_map() {

        Log.d("at", "need to return real Fragment_map");

        return new Fragment_map();

    }



    @Override

    public int sendLocation(Object p0, String locStr, long imGrp_related_ui64Id) {

        if (locStr == null || locStr.isEmpty()) {

            return -1;

        }

        boolean encryptionEnabled = MapLocSettings.isEncryptionEnabled();

        if (encryptionEnabled && !MapLocKeyword.canDecryptOthers()) {

            return 0;

        }

        String keyword = encryptionEnabled ? MapLocKeyword.getDecryptKeyword() : null;

        String payload = MapLocStrCrypto.prepareLocStrForSend(locStr, keyword, encryptionEnabled);

        if (payload == null || payload.isEmpty()) {

            return -1;

        }

        int laM10000 = 0;
        int loM10000 = 0;
        if (!encryptionEnabled) {
            int[] coords = parsePlainCoordsM10000(locStr);
            if (coords != null) {
                laM10000 = coords[0];
                loM10000 = coords[1];
            }
        }

        ancSndLocation(0, payload, imGrp_related_ui64Id, laM10000, loM10000);

        return 0;

    }



    @Override

    public int onRecv_locations(CParam_onRecv_locations param) {

        if (param == null || param.pTransferLocData == null) {

            return -1;

        }

        boolean encryptionEnabled = MapLocSettings.isEncryptionEnabled();

        if (encryptionEnabled && !MapLocKeyword.canDecryptOthers()) {

            return 0;

        }

        String keyword = encryptionEnabled ? MapLocKeyword.getDecryptKeyword() : null;

        if (!(param.pTransferLocData instanceof qnmCommProc_mis.TransferLocData)) {

            return -1;

        }

        qnmCommProc_mis.TransferLocData tld = (qnmCommProc_mis.TransferLocData) param.pTransferLocData;

        if (!isActiveGroup(tld.idInfo_imGrp_related)) {

            return 0;

        }

        int cnt = tld.usCnt;

        if (cnt <= 0) {

            return 0;

        }

        if (cnt > tld.mems.length) {

            cnt = tld.mems.length;

        }

        for (int i = 0; i < cnt; i++) {

            qnmCommProc_mis.TransferLocDataMem mem = tld.mems[i];

            if (mem == null || mem.idInfo.ui64Id == 0) {

                continue;

            }

            String locStr = mem.locData.locStr;

            String plain = resolvePlainLocStr(locStr, mem, keyword, encryptionEnabled);

            if (plain == null) {

                continue;

            }

            dispatchRemoteLocation(mem.idInfo.ui64Id, plain);

        }

        return 0;

    }



    private static boolean isActiveGroup(long grpId) {

        if (grpId <= 0) {

            return false;

        }

        long[] grps = TaskActiveGrp.getTaskGrp();

        if (grps == null) {

            return false;

        }

        for (long id : grps) {

            if (id == grpId) {

                return true;

            }

        }

        return false;

    }

    private static long getSelfUserId() {
        ctxQmc.CCtxQmc pProcInfo = qyMcMain_global.g.g_pQyMc != null
                ? qyMcMain_global.g.g_pQyMc.get_pProcInfo() : null;
        if (pProcInfo == null || pProcInfo.pMisCnt == null) {
            return 0;
        }
        return pProcInfo.pMisCnt.idInfo.ui64Id;
    }

    private static boolean isMemberOfGroup(long grpId) {
        if (grpId <= 0) {
            return false;
        }
        long selfId = getSelfUserId();
        if (selfId <= 0) {
            return false;
        }
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (pQyMc == null || pQyMc.gui == null || !(pQyMc.gui.pDb instanceof myDb.CMyDb)) {
            return false;
        }
        myDb.CMyDb db = (myDb.CMyDb) pQyMc.gui.pDb;
        if (db.m_var_pQ_qyImGrpMemTab == null) {
            return false;
        }
        Set<Long> members = new HashSet<>();
        genericQueue.qyFuncs.qTraverse(db.m_var_pQ_qyImGrpMemTab, tmpHandler_collectGrpMemberIds, grpId, members);
        return members.contains(selfId);
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

    /**
     * 融合中心投递规则：
     * - 外层 [msgr=用户id]：点对点投递，仅该用户接收；
     * - 外层 [msgr=组id]：组播投递，组内成员都应接收；
     * - 内层 [obj=80] 为业务组 id，需与当前活动组/成员关系匹配。
     */
    private static boolean shouldAcceptHgGroupMessage(long grpId, long outerMsgrId) {
        if (grpId <= 0) {
            return false;
        }
        long selfId = getSelfUserId();
        if (outerMsgrId > 0 && selfId > 0 && outerMsgrId == selfId) {
            return true;
        }
        if (outerMsgrId > 0 && outerMsgrId == grpId && isMemberOfGroup(grpId)) {
            return true;
        }
        if (isActiveGroup(grpId)) {
            return true;
        }
        return isMemberOfGroup(grpId);
    }



    private void dispatchRemoteLocation(long userId, String plainLocStr) {

        ctxQmc.CCtxQmc pProcInfo = m_var.pProcInfo;

        if (pProcInfo == null || pProcInfo.pQyMc == null) {

            return;

        }

        Object hMain = pProcInfo.pQyMc.gui.hMainWnd;

        if (!(hMain instanceof MainActivity)) {

            return;

        }

        MainActivity main = (MainActivity) hMain;

        main.runOnUiThread(() -> main.applyMapRemoteLocation(userId, plainLocStr));

    }

    private static String resolvePlainLocStr(
            String locStr,
            qnmCommProc_mis.TransferLocDataMem mem,
            String keyword,
            boolean encryptionEnabled) {
        String plain = MapLocStrCrypto.decodeLocStr(locStr, keyword, encryptionEnabled);
        if (plain != null) {
            return plain;
        }
        if (!shouldUseNumericFallback(locStr, encryptionEnabled)) {
            return null;
        }
        return buildPlainFromNumericFields(mem);
    }

    private static boolean shouldUseNumericFallback(String locStr, boolean encryptionEnabled) {
        if (locStr == null || locStr.isEmpty()) {
            return true;
        }
        String trimmed = locStr.trim();
        if (trimmed.startsWith(MapLocStrCrypto.PLAIN_PREFIX)) {
            return false;
        }
        if (encryptionEnabled
                && (trimmed.startsWith(MapLocStrCrypto.ENC_PREFIX_SEND)
                || trimmed.startsWith(MapLocStrCrypto.ENC_PREFIX)
                || trimmed.startsWith(MapLocStrCrypto.ENC_PREFIX_V1))) {
            return false;
        }
        return true;
    }

    private static int[] parsePlainCoordsM10000(String locStr) {
        if (locStr == null || !locStr.startsWith(MapLocStrCrypto.PLAIN_PREFIX)) {
            return null;
        }
        try {
            String[] arr = locStr.split(",");
            if (arr.length < 3) {
                return null;
            }
            double lat = Double.parseDouble(arr[1]);
            double lng = Double.parseDouble(arr[2]);
            return new int[] {
                    (int) Math.round(lat * 10000.0),
                    (int) Math.round(lng * 10000.0)
            };
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String buildPlainFromNumericFields(qnmCommProc_mis.TransferLocDataMem mem) {
        if (mem == null || mem.locData == null) {
            return null;
        }
        int la = mem.locData.iLaInM10000f;
        int lo = mem.locData.iLoInM10000f;
        if (la == 0 && lo == 0) {
            return null;
        }
        return String.format(
                Locale.US,
                "%s,%f,%f,%f,%d",
                MapLocStrCrypto.PLAIN_PREFIX,
                la / 10000.0,
                lo / 10000.0,
                0f,
                System.currentTimeMillis());
    }



    @Override
    public int sendMark(String markStr, long imGrp_related_ui64Id) {
        if (markStr == null || markStr.isEmpty()) {
            return -1;
        }
        if (imGrp_related_ui64Id <= 0) {
            return -1;
        }
        if (!isActiveGroup(imGrp_related_ui64Id)) {
            return -1;
        }
        return ancSndMarkToHg(null, markStr, imGrp_related_ui64Id);
    }



    @Override
    public int onRecv_marks(Param_onRecv_marks param) {
        if (param == null || param.m_marks == null || param.m_marks.isEmpty()) {
            return -1;
        }
        if (!shouldAcceptHgGroupMessage(param.imGrp_ui64Id, param.outerMsgrId)) {
            Log.d("at", "onRecv_marks skipped grp=" + param.imGrp_ui64Id
                    + " outerMsgr=" + param.outerMsgrId + " self=" + getSelfUserId());
            return 0;
        }
        ctxQmc.CCtxQmc pProcInfo = m_var.pProcInfo;
        if (pProcInfo == null || pProcInfo.pQyMc == null) {
            return -1;
        }
        Object hMain = pProcInfo.pQyMc.gui.hMainWnd;
        if (!(hMain instanceof MainActivity)) {
            return -1;
        }
        MainActivity main = (MainActivity) hMain;
        for (MapExtTmpl.AncMark mark : param.m_marks) {
            if (mark == null || mark.markStr == null || mark.markStr.isEmpty()) {
                continue;
            }
            long fromId = mark.ui64Id;
            String markStr = mark.markStr;
            main.runOnUiThread(() -> main.applyMapRemoteMark(fromId, markStr));
        }
        return 0;
    }



    @Override

    public int onRecv_plottings(Param_onRecv_plottings param) {

        return 0;

    }

}


