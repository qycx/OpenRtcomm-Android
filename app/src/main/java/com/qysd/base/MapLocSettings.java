package com.qysd.base;

import android.content.Context;

/**
 * 活动组定位应用层加密总开关（持久化在 SharedPreferences config）。
 */
public final class MapLocSettings {

    public static final String PREF_NAME = "config";
    public static final String PREF_KEY_ENCRYPTION_ENABLED = "map_loc_encryption_enabled";

    private static volatile boolean encryptionEnabled = false;

    private MapLocSettings() {
    }

    public static void loadFrom(Context context) {
        if (context == null) {
            return;
        }
        encryptionEnabled = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getBoolean(PREF_KEY_ENCRYPTION_ENABLED, false);
    }

    public static boolean isEncryptionEnabled() {
        return encryptionEnabled;
    }

    public static void setEncryptionEnabled(Context context, boolean enabled) {
        encryptionEnabled = enabled;
        if (context != null) {
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(PREF_KEY_ENCRYPTION_ENABLED, enabled)
                    .apply();
        }
        if (!enabled) {
            MapLocKeyword.clearOnLogoff();
            MapLocStrCrypto.clearDerivedKeyCache();
        }
    }

    /** 加密关闭时可直接收发；加密开启时需已输入口令。 */
    public static boolean canShareLocation() {
        if (!encryptionEnabled) {
            return true;
        }
        return MapLocKeyword.canDecryptOthers();
    }
}
