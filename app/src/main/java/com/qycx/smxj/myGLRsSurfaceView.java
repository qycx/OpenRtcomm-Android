package com.qycx.smxj;

import android.content.Context;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;

import com.intel.realsense.librealsense.Frame;
import com.intel.realsense.librealsense.FrameSet;

import java.util.Map;

public class myGLRsSurfaceView extends GLSurfaceView implements AutoCloseable{

    private final myGLRenderer mRenderer;
    private float mPreviousX = 0;
    private float mPreviousY = 0;

    public myGLRsSurfaceView(Context context) {
        super(context);
        mRenderer = new myGLRenderer();
        setRenderer(mRenderer);
    }

    public myGLRsSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRenderer = new myGLRenderer();
        setRenderer(mRenderer);
    }

    public Map<Integer, Pair<String,Rect>> getRectangles() {
        return mRenderer.getRectangles();
    }

    public void upload(FrameSet frames) {
        mRenderer.upload(frames);
    }

    public void upload(Frame frame) {
        mRenderer.upload(frame);
    }

    public void clear() {
        mRenderer.clear();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;
                mRenderer.onTouchEvent(dx, dy);
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }

    public void showPointcloud(boolean showPoints) {
        mRenderer.showPointcloud(showPoints);
    }

    @Override
    public void close() {
        if(mRenderer != null)
            mRenderer.close();
    }
}
