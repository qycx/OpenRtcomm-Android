package com.south.mapnav;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

/** 系统级悬浮窗测试（地图 Phase2 默认不启用）。 */
public final class SystemOverlayTestWindow {

    private final Context appContext;
    private final WindowManager wm;
    private View overlayRoot;
    private WindowManager.LayoutParams lp;
    private float downRawX;
    private float downRawY;
    private int downX;
    private int downY;
    private ImageView ivMirror;
    private TextView tvStatus;
    private Bitmap lastMirror;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable mirrorRunnable = new Runnable() {
        @Override
        public void run() {
            if (overlayRoot == null) return;
            TextureView tv = DlgTalkAv2Bridge.getActiveTextureView();
            if (tv == null || !tv.isAvailable() || tv.getWidth() <= 0 || tv.getHeight() <= 0) {
                if (tvStatus != null) {
                    tvStatus.setText("等待视频流...");
                }
                handler.postDelayed(this, 300);
                return;
            }
            Bitmap b = tv.getBitmap();
            if (b != null && !b.isRecycled()) {
                if (ivMirror != null) {
                    ivMirror.setImageBitmap(b);
                }
                if (lastMirror != null && !lastMirror.isRecycled() && lastMirror != b) {
                    lastMirror.recycle();
                }
                lastMirror = b;
                if (tvStatus != null) {
                    tvStatus.setText("视频通话中");
                }
            }
            handler.postDelayed(this, 200);
        }
    };

    public SystemOverlayTestWindow(@NonNull Context context) {
        this.appContext = context.getApplicationContext();
        this.wm = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
    }

    public boolean isShowing() {
        return overlayRoot != null;
    }

    public void show() {
        if (overlayRoot != null || wm == null) return;
        overlayRoot = buildOverlayView();
        int type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;
        lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT
        );
        lp.gravity = Gravity.TOP | Gravity.START;
        lp.x = dp(20);
        lp.y = dp(120);
        wm.addView(overlayRoot, lp);
        handler.removeCallbacks(mirrorRunnable);
        handler.post(mirrorRunnable);
    }

    public void dismiss() {
        if (overlayRoot == null || wm == null) return;
        handler.removeCallbacks(mirrorRunnable);
        if (ivMirror != null) {
            ivMirror.setImageBitmap(null);
        }
        if (lastMirror != null && !lastMirror.isRecycled()) {
            lastMirror.recycle();
            lastMirror = null;
        }
        try {
            wm.removeView(overlayRoot);
        } catch (Exception ignore) {
        }
        overlayRoot = null;
        lp = null;
        ivMirror = null;
        tvStatus = null;
    }

    private View buildOverlayView() {
        FrameLayout root = new FrameLayout(appContext);
        FrameLayout.LayoutParams rootLp = new FrameLayout.LayoutParams(dp(220), dp(170));
        root.setLayoutParams(rootLp);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xE01A1A1A);
        bg.setCornerRadius(dp(10));
        root.setBackground(bg);
        root.setPadding(dp(8), dp(8), dp(8), dp(8));

        LinearLayout header = new LinearLayout(appContext);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        FrameLayout.LayoutParams hlp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        hlp.gravity = Gravity.TOP;
        root.addView(header, hlp);

        TextView title = new TextView(appContext);
        title.setText("视频通话");
        title.setTextColor(Color.WHITE);
        title.setTextSize(13f);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        header.addView(title, titleLp);

        TextView close = new TextView(appContext);
        close.setText("✕");
        close.setTextColor(Color.WHITE);
        close.setTextSize(16f);
        close.setPadding(dp(8), dp(2), dp(4), dp(2));
        close.setOnClickListener(v -> dismiss());
        header.addView(close);

        ivMirror = new ImageView(appContext);
        ivMirror.setBackgroundColor(0xFF202020);
        ivMirror.setScaleType(ImageView.ScaleType.CENTER_CROP);
        FrameLayout.LayoutParams mlp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mlp.topMargin = dp(34);
        root.addView(ivMirror, mlp);

        tvStatus = new TextView(appContext);
        tvStatus.setText("等待视频流...");
        tvStatus.setTextColor(0xFFDDDDDD);
        tvStatus.setTextSize(12f);
        tvStatus.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams slp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        slp.topMargin = dp(34);
        root.addView(tvStatus, slp);

        root.setOnTouchListener((v, event) -> {
            if (lp == null || wm == null) return false;
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    downRawX = event.getRawX();
                    downRawY = event.getRawY();
                    downX = lp.x;
                    downY = lp.y;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    int dx = Math.round(event.getRawX() - downRawX);
                    int dy = Math.round(event.getRawY() - downRawY);
                    lp.x = downX + dx;
                    lp.y = downY + dy;
                    wm.updateViewLayout(v, lp);
                    return true;
                default:
                    return false;
            }
        });
        return root;
    }

    private int dp(int dp) {
        float d = appContext.getResources().getDisplayMetrics().density;
        return Math.round(dp * d);
    }
}
