package com.south.mapnav;

import android.graphics.Bitmap;
import android.view.TextureView;

import androidx.annotation.Nullable;

/** 地图侧视频纹理桥接占位（不依赖 Activity_dlgTalk_av 扩展）。 */
public final class DlgTalkAv2Bridge {

    private DlgTalkAv2Bridge() {
    }

    @Nullable
    public static TextureView getActiveTextureView() {
        return null;
    }

    @Nullable
    public static Bitmap captureLatestFrame() {
        return null;
    }
}
