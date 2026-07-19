package com.south.anc;

import com.qycx.anc.hgCommProc;
import com.qycx.base.qnmCommProc_mis;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class my_hgCommProc {



    /**
     * 将 {@link com.qycx.base.qnmCommProc_mis.TransferLocData} 序列化为融合中心 hg_cliData 字符串。
     */
    public static String buildTransferLocDataHgCliData(long msgrId,
                                                       int contentUiType,
                                                       long grpId,
                                                       long[] memUserIds,
                                                       String[] memLocStrs) {
        if (memUserIds == null || memLocStrs == null || memUserIds.length != memLocStrs.length) {
            return null;
        }
        StringBuilder sb = new StringBuilder(512);
        sb.append("[msgr=").append(msgrId).append(']');
       // sb.append(" [content=").append(contentUiType).append(']');
        appendHgObj(sb, hgCommProc.Consts.CONST_hgType_grpIdInfo, String.valueOf(grpId));
        for (int i = 0; i < memUserIds.length; i++) {
            String locStr = memLocStrs[i] != null ? memLocStrs[i] : "";
            StringBuilder tldm = new StringBuilder(256);
            appendHgObj(tldm, hgCommProc.Consts.CONST_hgType_tldm_index, String.valueOf(i));
            appendHgObj(tldm, hgCommProc.Consts.CONST_hgType_msgrIdInfo, String.valueOf(memUserIds[i]));
            appendHgObj(tldm, hgCommProc.Consts.CONST_hgType_locStr, locStr);
            if (sb.length() + 1 + tldm.length() >= qnmCommProc_mis.Consts.MAX_hg_cliDataLen) {
                return null;
            }
            sb.append(' ').append(tldm);
        }
        return sb.toString();
    }

    private static void appendHgObj(StringBuilder sb, short objType, String value) {
        String safe = value != null ? value : "";
        sb.append(" [obj=").append(objType)
                .append(" len=").append(safe.length())
                .append(']').append(safe);
    }

    /** 解析 {@link #buildTransferLocDataHgCliData} 序列化结果。 */
    public static class ParsedHgCliData {
        /** 融合中心外层投递目标（[msgr=104] 中的 104，可能是用户 id 或组 id） */
        public long outerMsgrId;
        /** 内层载荷发送者（[msgr=106] 中的 106） */
        public long msgrId;
        public long grpId;
        public final List<Mem> mems = new ArrayList<>();

        public static class Mem {
            public long userId;
            public String locStr;
        }
    }

    public static ParsedHgCliData parseHgCliData(byte[] data, int dataLen) {
        if (data == null || dataLen <= 0) {
            return null;
        }
        String s = new String(data, 0, dataLen, StandardCharsets.UTF_8);
        ParsedHgCliData result = parseHgCliDataString(s, 0, s.length());
        if (result == null || result.grpId <= 0 || result.mems.isEmpty()) {
            return null;
        }
        return result;
    }

    private static ParsedHgCliData parseHgCliDataString(String s, int start, int end) {
        ParsedHgCliData result = new ParsedHgCliData();
        ParsedHgCliData.Mem currentMem = null;
        int pos = start;

        while (pos < end) {
            while (pos < end && Character.isWhitespace(s.charAt(pos))) {
                pos++;
            }
            if (pos >= end) {
                break;
            }

            if (s.regionMatches(pos, "[msgr=", 0, 6)) {
                int msgrEnd = s.indexOf(']', pos);
                if (msgrEnd < 0 || msgrEnd >= end) {
                    break;
                }
                try {
                    long parsedMsgr = Long.parseLong(s.substring(pos + 6, msgrEnd));
                    if (start == 0 && result.outerMsgrId == 0) {
                        result.outerMsgrId = parsedMsgr;
                    } else {
                        result.msgrId = parsedMsgr;
                    }
                } catch (NumberFormatException ignored) {
                    return null;
                }
                pos = msgrEnd + 1;
                continue;
            }

            if (s.regionMatches(pos, "[cmd=", 0, 5)) {
                int cmdEnd = s.indexOf(']', pos);
                if (cmdEnd < 0 || cmdEnd >= end) {
                    break;
                }
                pos = cmdEnd + 1;
                continue;
            }

            if (!s.regionMatches(pos, "[obj=", 0, 5)) {
                pos++;
                continue;
            }

            int lenIdx = s.indexOf("len=", pos);
            int valStart = s.indexOf(']', pos);
            if (lenIdx < 0 || valStart < 0 || lenIdx > valStart) {
                break;
            }
            int objType;
            int valueLen;
            try {
                objType = Integer.parseInt(s.substring(pos + 5, lenIdx).trim());
                valueLen = Integer.parseInt(s.substring(lenIdx + 4, valStart).trim());
            } catch (NumberFormatException ignored) {
                break;
            }
            int valueBegin = valStart + 1;
            int valueEnd = valueBegin + valueLen;
            if (valueEnd > end) {
                break;
            }
            String value = s.substring(valueBegin, valueEnd);
            pos = valueEnd;

            if (objType == hgCommProc.Consts.CONST_hgType_rawData || objType == hgCommProc.Consts.CONST_hgType_anHgPayload) {
                ParsedHgCliData inner = parseHgCliDataString(value, 0, value.length());
                mergeParsedHgCliData(result, inner);
                currentMem = null;
                continue;
            }

            if (objType == hgCommProc.Consts.CONST_hgType_grpIdInfo) {
                try {
                    result.grpId = Long.parseLong(value.trim());
                } catch (NumberFormatException ignored) {
                    return null;
                }
            } else if (objType == hgCommProc.Consts.CONST_hgType_tldm_index) {
                currentMem = new ParsedHgCliData.Mem();
                result.mems.add(currentMem);
            } else if (objType == hgCommProc.Consts.CONST_hgType_msgrIdInfo) {
                if (currentMem == null) {
                    currentMem = new ParsedHgCliData.Mem();
                    result.mems.add(currentMem);
                }
                try {
                    currentMem.userId = Long.parseLong(value.trim());
                } catch (NumberFormatException ignored) {
                    return null;
                }
            } else if (objType == hgCommProc.Consts.CONST_hgType_locStr) {
                if (currentMem == null) {
                    currentMem = new ParsedHgCliData.Mem();
                    result.mems.add(currentMem);
                }
                currentMem.locStr = value;
            }
        }

        return result;
    }

    private static void mergeParsedHgCliData(ParsedHgCliData dst, ParsedHgCliData src) {
        if (dst == null || src == null) {
            return;
        }
        if (src.grpId > 0) {
            dst.grpId = src.grpId;
        }
        if (src.msgrId > 0) {
            dst.msgrId = src.msgrId;
        }
        dst.mems.addAll(src.mems);
    }

}
