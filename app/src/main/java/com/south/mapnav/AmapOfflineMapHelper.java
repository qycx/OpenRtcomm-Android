package com.south.mapnav;

import android.content.Context;
import android.text.TextUtils;

/** 高德离线下载占位：本工程 Phase2 不集成高德 SDK。 */
public final class AmapOfflineMapHelper {

    public interface DownloadUi {
        void onProgress(int percent, String cityName);

        void onComplete(String cityName);

        void onError(String message);
    }

    private AmapOfflineMapHelper() {
    }

    /**
     * @return null；立即回调 onError。
     */
    public static Object ensureCityOffline(Context ctx, String cityName, DownloadUi ui) {
        if (ui == null) {
            return null;
        }
        if (ctx == null || TextUtils.isEmpty(cityName)) {
            ui.onError("参数无效");
            return null;
        }
        ui.onError("本版本未集成高德离线下载，请直接使用在线导航");
        return null;
    }
}
