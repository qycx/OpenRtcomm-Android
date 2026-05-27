package com.qycx;

//import android.app.Fragment;
import static com.qycx.base.qyMcMain_global.g.g_pQyMc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.qycx.base.ctxQyMc;
import com.qycx.base.util.Log;


//
public class Fragment_null extends Fragment {

    TextView tv_title;
    //
    public TextView tv_content;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  create your fragment here
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Use this to return your custom view for this Fragment
        View view = inflater.inflate(R.layout.layout_fg_null, container, false);

        //
        tv_title=view.findViewById(R.id.txt_title);
        tv_content=view.findViewById(R.id.txt_content);

        //
        tv_title.setText("地图");

        //
        tv_content.setOnClickListener(new View.OnClickListener() {

                                          @Override
                                          public void onClick(View view) {

                                              //
                                              ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
                                              MainActivity mainWnd = (MainActivity) pQyMc.gui.hMainWnd;
                                              //
                                              mainWnd.testClickOnMap();
                                          }
                                      }
        );

        //
        return view;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

