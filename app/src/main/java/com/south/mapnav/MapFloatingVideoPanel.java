package com.south.mapnav;

import android.graphics.Bitmap;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/** 地图视频浮层占位（Phase2 不启用视频）。 */
public final class MapFloatingVideoPanel {

    public MapFloatingVideoPanel(@NonNull View root) {
    }

    public boolean isVisible() {
        return false;
    }

    public void show(@Nullable String titleText) {
    }

    public void hide() {
    }

    public void setMirrorBitmap(@Nullable Bitmap bitmap) {
    }

    public void clearMirrorBitmap() {
    }

    public void release() {
    }
}
