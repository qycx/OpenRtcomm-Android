package com.qycx;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.TextureView;

import androidx.fragment.app.Fragment;

public class MyTextureView extends TextureView {
    //
    Fragment  m_fg;

    //
    public MyTextureView(Context context)
    {
        super(context);
    }

    public MyTextureView(Context context, AttributeSet attrs)
    {
        super(context,attrs,0);
    }

    public MyTextureView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void SetAspectRatio(int width, int height) {

    }

    //
    //
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        //Log.e("TOUCH", "dispatch");
        if  (  m_fg!=null)  {
            if  ( m_fg instanceof  Fragment_dlgTalk_av2)  {
                Fragment_dlgTalk_av2 fg=(Fragment_dlgTalk_av2) m_fg;
                fg.myTextureView_dispatchTouchEvent(event);
            }
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //Log.e("TOUCH", "touch");

        return super.onTouchEvent(event);
    }


}
