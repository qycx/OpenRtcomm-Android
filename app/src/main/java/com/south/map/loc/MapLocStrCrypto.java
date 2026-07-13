package com.south.map.loc;

import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 活动组定位 locStr 应用层 AES 加解密（口令 PBKDF2 派生密钥，AES-256-GCM）。
 */
public final class MapLocStrCrypto {

    private static final String TAG = "MapLocStrCrypto";

    public static final String ENC_PREFIX_V1 = "__QY_LOC_ENC_V1__:";
    /** 发送用短前缀（定位服务器 locStr 约 127 字节上限）。 */
    public static final String ENC_PREFIX_SEND = "Q2:";
    /** 旧版长前缀，仅用于兼容解密。 */
    public static final String ENC_PREFIX = "__QY_LOC_ENC_V2__:";
    public static final String PLAIN_PREFIX = "__QY_LOC_V1__";
    /** 定位服务器侧 locStr 实测上限约 127 字符。 */
    public static final int LOC_STR_SERVER_MAX = 127;
    /** 旧版发送端在未输入口令时使用的占位词，仅用于兼容解密。 */
    private static final String LEGACY_PLACEHOLDER_KEYWORD = "ww";

    private static final String TRANSFORMATION_GCM = "AES/GCM/NoPadding";
    private static final String TRANSFORMATION_CBC = "AES/CBC/PKCS5Padding";
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final int NONCE_LENGTH = 12;
    private static final int AES_KEY_LENGTH = 32;
    private static final int PBKDF2_ITERATIONS = 10_000;
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String KDF_SALT = "QY_LOC_V2_SALT";

    private static final Object DERIVE_LOCK = new Object();
    private static String cachedKeywordForKey;
    private static byte[] cachedAesKey;

    private MapLocStrCrypto() {
    }

    public static void clearDerivedKeyCache() {
        synchronized (DERIVE_LOCK) {
            cachedKeywordForKey = null;
            cachedAesKey = null;
        }
    }

    public static String encrypt(String plainText, String keyword) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        if (isEncryptedLocStr(plainText)) {
            return plainText;
        }
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }
        try {
            byte[] key = deriveKeyBytes(keyword);
            SecureRandom random = new SecureRandom();
            byte[] nonce = new byte[NONCE_LENGTH];
            random.nextBytes(nonce);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION_GCM);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(GCM_TAG_LENGTH_BITS, nonce));
            byte[] enc = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[nonce.length + enc.length];
            System.arraycopy(nonce, 0, combined, 0, nonce.length);
            System.arraycopy(enc, 0, combined, nonce.length, enc.length);
            // URL-safe Base64（无 +/），配合短前缀 Q2:，总长控制在 127 以内。
            String payload = Base64.encodeToString(combined, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
            String out = ENC_PREFIX_SEND + payload;
            if (out.length() > LOC_STR_SERVER_MAX) {
                Log.w(TAG, "encrypted locStr too long: " + out.length());
                return null;
            }
            return out;
        } catch (Exception ignored) {
            return null;
        }
    }

    /** 调试用：验证本机加解密是否正常。 */
    public static boolean verifyRoundTrip(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return false;
        }
        String sample = PLAIN_PREFIX + ",39.904200,116.407400,45.500000,0";
        String enc = encrypt(sample, keyword);
        if (enc == null || enc.length() > LOC_STR_SERVER_MAX) {
            return false;
        }
        String dec = decrypt(enc, keyword);
        return sample.equals(dec);
    }

    /**
     * @return 解密后的明文；关键词错误或数据损坏时返回 null。
     */
    public static String decrypt(String cipherText, String keyword) {
        if (cipherText == null || cipherText.isEmpty()) {
            return null;
        }
        String trimmed = cipherText.trim();
        if (trimmed.startsWith(PLAIN_PREFIX)) {
            return null;
        }
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }
        if (trimmed.startsWith(ENC_PREFIX_SEND)) {
            return decryptV2(trimmed.substring(ENC_PREFIX_SEND.length()), keyword);
        }
        if (trimmed.startsWith(ENC_PREFIX)) {
            return decryptV2(trimmed.substring(ENC_PREFIX.length()), keyword);
        }
        if (trimmed.startsWith(ENC_PREFIX_V1)) {
            return decryptV1(trimmed.substring(ENC_PREFIX_V1.length()), keyword);
        }
        return null;
    }

    /** 按加密开关发送：关闭时传明文，开启时 AES 加密。 */
    public static String prepareLocStrForSend(String plainText, String keyword, boolean encryptionEnabled) {
        if (plainText == null || plainText.isEmpty()) {
            return null;
        }
        if (!encryptionEnabled) {
            return plainText.startsWith(PLAIN_PREFIX) ? plainText : null;
        }
        return encrypt(plainText, keyword);
    }

    /** 按加密开关解析 locStr：关闭时只接受明文；开启时只接受密文。 */
    public static String decodeLocStr(String locStr, String keyword, boolean encryptionEnabled) {
        if (locStr == null || locStr.isEmpty()) {
            return null;
        }
        String trimmed = locStr.trim();
        if (!encryptionEnabled) {
            if (isEncryptedLocStr(trimmed)) {
                return null;
            }
            return trimmed.startsWith(PLAIN_PREFIX) ? trimmed : null;
        }
        if (trimmed.startsWith(PLAIN_PREFIX)) {
            return null;
        }
        return decrypt(trimmed, keyword);
    }

    private static boolean isEncryptedLocStr(String s) {
        if (s == null) {
            return false;
        }
        return s.startsWith(ENC_PREFIX_SEND)
                || s.startsWith(ENC_PREFIX)
                || s.startsWith(ENC_PREFIX_V1);
    }

    private static String decryptV2(String payloadPart, String keyword) {
        byte[] combined = decodePayloadBytes(payloadPart);
        if (combined == null || combined.length <= NONCE_LENGTH) {
            if (payloadPart != null) {
                Log.d(TAG, "V2 payload decode failed, rawLen=" + payloadPart.trim().length());
            }
            return null;
        }
        if (combined.length < 80) {
            Log.d(TAG, "V2 payload too short (truncated?), bytes=" + combined.length);
        }
        String plain = tryDecryptV2Combined(combined, keyword);
        if (plain != null) {
            return plain;
        }
        if (!LEGACY_PLACEHOLDER_KEYWORD.equals(keyword)) {
            plain = tryDecryptV2Combined(combined, LEGACY_PLACEHOLDER_KEYWORD);
        }
        return plain;
    }

    private static String tryDecryptV2Combined(byte[] combined, String keyword) {
        try {
            byte[] nonce = Arrays.copyOfRange(combined, 0, NONCE_LENGTH);
            byte[] enc = Arrays.copyOfRange(combined, NONCE_LENGTH, combined.length);

            byte[] key = deriveKeyBytes(keyword);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION_GCM);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new GCMParameterSpec(GCM_TAG_LENGTH_BITS, nonce));
            byte[] plain = cipher.doFinal(enc);
            return validatePlain(new String(plain, StandardCharsets.UTF_8));
        } catch (javax.crypto.AEADBadTagException e) {
            Log.d(TAG, "GCM auth failed, payloadBytes=" + combined.length + ", keywordLen="
                    + (keyword != null ? keyword.length() : 0));
            return null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String decryptV1(String b64Part, String keyword) {
        byte[] enc = decodeBase64Safe(b64Part);
        if (enc == null || enc.length == 0) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION_CBC);
            cipher.init(Cipher.DECRYPT_MODE, deriveKeyV1(keyword), deriveIvV1(keyword));
            byte[] plain = cipher.doFinal(enc);
            return validatePlain(new String(plain, StandardCharsets.UTF_8));
        } catch (Exception ignored) {
            return null;
        }
    }

    /** 先尝试 hex（V2 新格式），再回退 Base64（旧格式）。 */
    private static byte[] decodePayloadBytes(String payloadPart) {
        if (payloadPart == null) {
            return null;
        }
        String s = payloadPart.trim();
        if (s.isEmpty() || s.startsWith(PLAIN_PREFIX)) {
            return null;
        }
        // V2 发送端用 hex 编码；纯 hex 字符串绝不能走 Base64（会抛 bad base-64）。
        if (isHexOnlyPayload(s)) {
            byte[] hex = hexToBytes(s);
            if (hex == null) {
                Log.d(TAG, "hex payload invalid or truncated, len=" + s.length());
            }
            return hex;
        }
        return decodeBase64Safe(s);
    }

    private static String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    private static byte[] hexToBytes(String hex) {
        if (!looksLikeHexPayload(hex)) {
            return null;
        }
        int len = hex.length();
        byte[] out = new byte[len / 2];
        for (int i = 0; i < out.length; i++) {
            int hi = Character.digit(hex.charAt(i * 2), 16);
            int lo = Character.digit(hex.charAt(i * 2 + 1), 16);
            if (hi < 0 || lo < 0) {
                return null;
            }
            out[i] = (byte) ((hi << 4) | lo);
        }
        return out;
    }

    private static boolean isHexOnlyPayload(String s) {
        if (s == null || s.isEmpty()) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (Character.digit(s.charAt(i), 16) < 0) {
                return false;
            }
        }
        return true;
    }

    private static boolean looksLikeHexPayload(String s) {
        return isHexOnlyPayload(s) && s.length() >= 2 && (s.length() & 1) == 0;
    }

    /**
     * 安全 Base64 解码：非法字符时不抛异常，返回 null。
     * 若后缀实为明文坐标（误带密文前缀），也不尝试解码。
     */
    private static byte[] decodeBase64Safe(String b64Part) {
        if (b64Part == null) {
            return null;
        }
        String s = b64Part.trim();
        if (s.isEmpty()) {
            return null;
        }
        if (s.startsWith(PLAIN_PREFIX)) {
            return null;
        }
        if (isHexOnlyPayload(s)) {
            return null;
        }
        if (!looksLikeBase64Payload(s)) {
            return null;
        }
        // 传输层有时把 Base64 的 '+' 变成空格。
        String normalized = s.replace(' ', '+');
        byte[] decoded = tryBase64Decode(normalized);
        if (decoded != null) {
            return decoded;
        }
        return tryBase64Decode(s);
    }

    private static byte[] tryBase64Decode(String s) {
        try {
            return Base64.decode(s, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
        } catch (IllegalArgumentException ignored) {
            try {
                return Base64.decode(s, Base64.URL_SAFE | Base64.NO_WRAP);
            } catch (IllegalArgumentException ignored2) {
                try {
                    return Base64.decode(s, Base64.NO_WRAP);
                } catch (IllegalArgumentException ignored3) {
                    return null;
                }
            }
        }
    }

    private static boolean looksLikeBase64Payload(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ((c >= 'A' && c <= 'Z')
                    || (c >= 'a' && c <= 'z')
                    || (c >= '0' && c <= '9')
                    || c == '+' || c == '/' || c == '='
                    || c == '-' || c == '_') {
                continue;
            }
            return false;
        }
        return true;
    }

    private static String validatePlain(String out) {
        if (out == null || !out.startsWith(PLAIN_PREFIX)) {
            return null;
        }
        return out;
    }

    private static byte[] deriveKeyBytes(String keyword) throws Exception {
        String normalized = normalizeKeyword(keyword);
        synchronized (DERIVE_LOCK) {
            if (cachedAesKey != null && normalized.equals(cachedKeywordForKey)) {
                return cachedAesKey.clone();
            }
            PBEKeySpec spec = new PBEKeySpec(
                    normalized.toCharArray(),
                    KDF_SALT.getBytes(StandardCharsets.UTF_8),
                    PBKDF2_ITERATIONS,
                    AES_KEY_LENGTH * 8);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            byte[] key = factory.generateSecret(spec).getEncoded();
            cachedKeywordForKey = normalized;
            cachedAesKey = key.clone();
            return key;
        }
    }

    private static String normalizeKeyword(String keyword) {
        return keyword == null ? "" : keyword.trim();
    }

    private static SecretKeySpec deriveKeyV1(String keyword) throws Exception {
        String normalized = normalizeKeyword(keyword);
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest((normalized + "-loc-key").getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(key, "AES");
    }

    private static IvParameterSpec deriveIvV1(String keyword) throws Exception {
        String normalized = normalizeKeyword(keyword);
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] full = sha.digest((normalized + "-loc-iv").getBytes(StandardCharsets.UTF_8));
        return new IvParameterSpec(Arrays.copyOf(full, 16));
    }
}
