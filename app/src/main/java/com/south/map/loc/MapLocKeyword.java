package com.south.map.loc;

/**
 * 活动组定位加解密口令：加密开启时仅驻留内存，进地图时输入。
 */
public final class MapLocKeyword {

    private static volatile String userKeyword;
    private static volatile boolean keywordEntered;

    private MapLocKeyword() {
    }

    public static void setUserKeyword(String keyword) {
        if (keyword == null) {
            userKeyword = "";
            keywordEntered = false;
            MapLocStrCrypto.clearDerivedKeyCache();
            return;
        }
        userKeyword = keyword.trim();
        keywordEntered = !userKeyword.isEmpty();
        MapLocStrCrypto.clearDerivedKeyCache();
    }

    /** 是否已输入口令，可用于发送加密与解密他人位置。 */
    public static boolean canDecryptOthers() {
        return keywordEntered && userKeyword != null && !userKeyword.isEmpty();
    }

    public static String getDecryptKeyword() {
        return canDecryptOthers() ? userKeyword : null;
    }

    public static void clearOnLogoff() {
        userKeyword = null;
        keywordEntered = false;
        MapLocStrCrypto.clearDerivedKeyCache();
    }
}
