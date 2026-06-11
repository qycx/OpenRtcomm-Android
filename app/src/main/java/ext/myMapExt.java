package ext;



import static com.qysd.base.mapFunc.ancSndLocation;



import androidx.fragment.app.Fragment;



import com.qycx.Fragment_map;

import com.qycx.MainActivity;

import com.qycx.TaskActiveGrp;

import com.qycx.base.ctxQmc;

import com.qycx.base.qnmCommProc_mis;

import com.qycx.base.util.Log;

import com.qysd.base.MapExtTmpl;

import com.qysd.base.MapLocKeyword;

import com.qysd.base.MapLocSettings;

import com.qysd.base.MapLocStrCrypto;

import java.util.Locale;



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

}


