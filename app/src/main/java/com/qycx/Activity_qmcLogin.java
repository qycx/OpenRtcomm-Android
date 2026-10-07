package com.qycx;


import static com.qycx.base.qyMcMain_global.g.g_pQyMc;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.mySaveSecret;
import com.qycx.base.qyDefs;
import com.qycx.base.qyMcLogon;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.showInfo_open;
import com.qycx.base.util.Log;
import com.qycx.base.util.StringUtils;
import com.qycx.base.util.ToastUtils;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

//    [Activity(Label = "Activity_qmcLogin")]
public class Activity_qmcLogin extends qmcBaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {
    //    private static final String TAG = Activity_qmcLogin.class.getSimpleName();
    private static int REQUEST_CODE_STORAGE_PERMISSION = 10;
    //

    private static int RC_CAMERA_PERM = 123;
    EditText edit_server;
    EditText edit_usrName;
    EditText edit_passwd;
    TextView btn_login;
    TextView change_passwd;
    LinearLayout linear_layout;
    Context mContext;
    AppSettingsDialog appSettingsDialog;
    ValueAnimator loginBgBreathAnim;
    //
    AlertDialog m_alert = null;
    //
    String[] mPermissionList = new String[]{
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            android.Manifest.permission.VIBRATE,
            android.Manifest.permission.RECEIVE_BOOT_COMPLETED,
            android.Manifest.permission.WAKE_LOCK,
            android.Manifest.permission.ACCESS_NOTIFICATION_POLICY,
            android.Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
    };
    SharedPreferences sp;

    //
    private void isPermissionOk() {
        if (hasStoragePermission()) {
            startLogin();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    requestAllFilesAccessOnLaunchIfNeeded();
                } else {
                    startLogin();
                }
            } else {
                // Ask for one permission
                EasyPermissions.requestPermissions(
                        this,
                        getResources().getString(R.string.permission_content_storage_first),
                        REQUEST_CODE_STORAGE_PERMISSION,
                        mPermissionList);
            }
        }
    }

    //判断手机有没有存储权限
    private boolean hasStoragePermission() {
        return EasyPermissions.hasPermissions(this, mPermissionList);
    }


    //授权结果，分发下去
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        //跳转到onPermissionsGranted或者onPermissionsDenied去回调授权结果
        int[] gr = new int[grantResults.length];
        for (int i = 0; i < gr.length; i++) {
            gr[i] = (int) grantResults[i];
        }
        //
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, gr, this);
    }

    @Override
    public void onPermissionsGranted(int p0, List<String> p1) {
        startLogin();
    }

    //拒绝授权
    @Override
    public void onPermissionsDenied(int p0, List<String> p1) {
        //Log.i(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        //    ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;

        //   GuiShare.pf_PostMessage(pQyMc.gui.hMainWnd, Consts.CONST_qyWm_postComm, Consts.CONST_qyWmParam_login_canceled, 0);

        //
        if (EasyPermissions.somePermissionPermanentlyDenied(this, p1)) {
            AppSettingsDialog.Builder buider = new AppSettingsDialog.Builder(this);
            buider.setRationale(getResources().getString(R.string.permission_content_storage));
            appSettingsDialog = buider.build();
            appSettingsDialog.show();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //  create your application here
        setContentView(R.layout.layout_qmclogin);
        applyLoginTechSystemBars();
        ImageView ivLoginBg = findViewById(R.id.iv_login_bg);
        bindLoginBackground(ivLoginBg);
        startLoginBgBreathAnim(ivLoginBg);
        requestAllFilesAccessOnLaunchIfNeeded();
        sp = getSharedPreferences("SP", MODE_PRIVATE);

        Log.d("qmcLogin: onCreate called");

        mContext = this;

        //
        boolean  bDbg  = false;
        ctxQyMc.CCtxQyMc pQyMc =g_pQyMc;
        ctxQmc.CCtxQmc  pProcInfo  =  null;
        if  (  pQyMc  !=  null  ) {
            pProcInfo = pQyMc.get_pProcInfo();
            if (pProcInfo != null) {
                bDbg=pProcInfo.cfg.b_dbg;
            }
        }



        //
        edit_server = (EditText) findViewById(R.id.editText_server);
        edit_usrName = (EditText) findViewById(R.id.editText_usrName);
        edit_passwd = (EditText) findViewById(R.id.editText_passwd);
        btn_login = (TextView) findViewById(R.id.button_login);
        change_passwd = (TextView) findViewById(R.id.change_passwd);
        change_passwd.setOnClickListener(this);
        if (btn_login != null) {
            btn_login.setOnClickListener(this);
        }

        //
        TextView txt_title  =  (TextView)findViewById(R.id.textView_title);


        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        boolean  b640x480  =  false;

        //
        if  (  screenWidth  *  screenHeight  <=  640  *  480  )  b640x480  =  true;

        //
        if  (  b640x480) {
            ViewGroup.MarginLayoutParams layoutParams;
            layoutParams = (ViewGroup.MarginLayoutParams) txt_title.getLayoutParams();
            layoutParams.topMargin = 5;
            txt_title.setLayoutParams(layoutParams);
            //
            //layoutParams = (ViewGroup.MarginLayoutParams) edit_server.getLayoutParams();
        }

        //
        String str_server = "", str_name = "", str_passwd = "";
        // qyFuncs_Droid.readLoginInfo( str_server,  str_name,  str_passwd);
        //
        String servInitVal  =  "";      //""103.85.174.150";
        String usrNameInitVal  =  "";   //""yuwm";
        String pwdInitVal  =  "";       //""123456";

        //
        if  (  bDbg  )  {
            servInitVal="103.85.174.150";
            usrNameInitVal="yuwm";
            pwdInitVal="123456";

            //
            servInitVal="192.168.1.116";
            servInitVal="192.168.1.108";
            servInitVal="192.168.1.40";


            usrNameInitVal="phone";
        }

        //
        if  (  true  ) {
            servInitVal = "104.208.73.5";
            usrNameInitVal = "zk";
            pwdInitVal = "123456";
        }


        //
        if  (  false  ) {
            servInitVal = "111.26.176.179";
            usrNameInitVal = "yanfa01";
            pwdInitVal = "123456";
        }

        //
        str_server = sp.getString("str_server", servInitVal);
        str_name = sp.getString("str_name", usrNameInitVal);
        str_passwd = sp.getString("str_passwd", pwdInitVal);
        edit_server.setText(str_server);
        edit_usrName.setText(str_name);
        edit_passwd.setText(str_passwd);

        //
        setTimer(1000,gui_onTimer);

        //
        return;
    }

    /** 沉浸式：内容延伸到状态栏/导航栏，导航栏透明以显示底图。 */
    private void applyLoginTechSystemBars() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;
        Window window = getWindow();
        View decor = window.getDecorView();
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        decor.setSystemUiVisibility(flags);
        window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
        window.setNavigationBarColor(android.graphics.Color.TRANSPARENT);
    }

    /** 按屏幕比例选择 1080x1920 或 1080x2340 底图。 */
    private void bindLoginBackground(ImageView bg) {
        if (bg == null) return;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        float screenRatio = dm.heightPixels / (float) Math.max(1, dm.widthPixels);
        float ratio1920 = 1920f / 1080f;
        float ratio2340 = 2340f / 1080f;
        int resId = Math.abs(screenRatio - ratio2340) < Math.abs(screenRatio - ratio1920)
                ? R.drawable.bg_login_tech_main_2340
                : R.drawable.bg_login_tech_main;
        bg.setImageResource(resId);
        bg.setScaleType(ImageView.ScaleType.MATRIX);
        bg.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) ->
                applyLoginBgMatrix(bg, getLoginBgBreathScale()));
        bg.post(() -> applyLoginBgMatrix(bg, 1f));
    }

    private float getLoginBgBreathScale() {
        if (loginBgBreathAnim != null && loginBgBreathAnim.isRunning()) {
            return (float) loginBgBreathAnim.getAnimatedValue();
        }
        return 1f;
    }

    private void applyLoginBgMatrix(ImageView bg, float breathScale) {
        Drawable d = bg.getDrawable();
        if (d == null) return;
        int viewW = bg.getWidth();
        int viewH = bg.getHeight();
        int imgW = d.getIntrinsicWidth();
        int imgH = d.getIntrinsicHeight();
        if (viewW <= 0 || viewH <= 0 || imgW <= 0 || imgH <= 0) return;

        float scale = Math.max(viewW / (float) imgW, viewH / (float) imgH) * breathScale;
        float scaledW = imgW * scale;
        float dx = (viewW - scaledW) * 0.5f;
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        matrix.postTranslate(dx, 0f);
        bg.setImageMatrix(matrix);
    }

    /** 以地球区域为中心轻微呼吸缩放。 */
    private void startLoginBgBreathAnim(ImageView bg) {
        if (bg == null) return;
        stopLoginBgBreathAnim();
        bg.post(() -> {
            applyLoginBgMatrix(bg, 1f);
            loginBgBreathAnim = ValueAnimator.ofFloat(1f, 1.03f);
            loginBgBreathAnim.setDuration(14000);
            loginBgBreathAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            loginBgBreathAnim.setRepeatMode(ValueAnimator.REVERSE);
            loginBgBreathAnim.setRepeatCount(ValueAnimator.INFINITE);
            loginBgBreathAnim.addUpdateListener(a ->
                    applyLoginBgMatrix(bg, (float) a.getAnimatedValue()));
            loginBgBreathAnim.start();
        });
    }

    private void stopLoginBgBreathAnim() {
        if (loginBgBreathAnim != null) {
            loginBgBreathAnim.cancel();
            loginBgBreathAnim = null;
        }
    }

    /** Android 11+：首次打开登录页即引导「所有文件访问」权限。 */
    private void requestAllFilesAccessOnLaunchIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return;
        }
        if (Environment.isExternalStorageManager()) {
            return;
        }
        try {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivity(intent);
        }
    }


    @Override
    protected void onDestroy() {
        //
        stopLoginBgBreathAnim();
        killTimer("login:onDestroy");
        //
        if (m_alert != null) {
            m_alert.dismiss();
        }
        super.onDestroy();

        //
        //Log.d("Activity_qmcLogin  OnDestroy");
    }

    @Override
    public void onClick(View v) {

        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (pQyMc == null) {
            Log.d("qmcLogin.onClick err:pQyMc is null");
            return;
        }
        final int id = v.getId();
        if (id == R.id.button_login) {
            //
            Log.d("anc","登录按钮被点击");
            //
            isPermissionOk();
        } else if (id == R.id.button_close) {
            if (m_alert != null) {
                m_alert.dismiss();
                m_alert = null;
            }
        } else if (id == R.id.change_passwd) {
            Intent intent = new Intent(this, Activity_changePassword.class);
            intent.putExtra("params", "");
            startActivity(intent);
        }
    }

    //
    public int loopCtrl  =  0;

    //
    public qyDefs.PF_commonHandler gui_onTimer = (Object p0, Object p1, Object p2) -> {
        ctxQyMc.CCtxQyMc pQyMc =g_pQyMc;
        if  (  pQyMc  ==  null  )  return  -1;
        ctxQmc.CCtxQmc  pProcInfo  =  pQyMc.get_pProcInfo();
        if  (  pProcInfo==null)  return  -1;

        //
        //Log.d("qy","login:gui_onTimer");

        //
        loopCtrl  ++  ;

        if  (  loopCtrl  <  5
                ||  0==  (  loopCtrl  %  20  )  )
        {      //  间隔放长是为了在登录错误时，能修改用户名密码
            if (pProcInfo.cfg.bAutoLogin) {
                Log.d("qy", "login.gui_onTimer: to audio login");
                isPermissionOk();
            }

        }

        //
        if  (  true  )  {
            //showInfo_open.qyFuncs.showInfo_open(0,null,"login: gui_onTimer");
        }


        //
        return 0;
    };


        //开始进行登录
    private void startLogin() {
        String str;

        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        if (pQyMc == null) {
            Log.d("qmcLogin.onClick err:pQyMc is null");
            return;
        }
        ctxQmc.CCtxQmc pProcInfo  = pQyMc.get_pProcInfo();




        //
        String str_server = edit_server.getText().toString();
        String str_usrName = edit_usrName.getText().toString();
        String str_passwd = edit_passwd.getText().toString();
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("str_server", str_server);
        edit.putString("str_name", str_usrName);
        edit.putString("str_passwd", str_passwd);
        edit.apply();

        if (pQyMc.bLogon) {
            //
            Log.d("qy","Activity_qmcLogin:startLogin: pQyMc.bLogon is true");
            //
            return;
        }

        if (!StringUtils.isValidIPAddress(str_server)) {
            ToastUtils.showToast("服务器地址不合法");
            return;
        }

        if (0 != qyMcLogon.qyFuncs.doMcClientLogonOK(null, str_server, str_usrName, str_passwd)) {
            //displayHint(this, "登录失败", "服务器地址错，或者用户名密码错");
            ToastUtils.showToast("登录失败：服务器地址错，或者用户名密码错");
            return;
        }

        Log.d("Activity_qmcLogin: logon ok");
        showInfo_open.qyFuncs.showInfo_open("qmcLogin.logon ok");

        //
        mySaveSecret.qyFuncs_Droid.saveLoginInfo(str_server, str_usrName, str_passwd);
        doFinish("after isPermissionOk");
    }

    public void displayHint(View.OnClickListener lsn, String title, String hint) {
        AlertDialog.Builder builder = null;

        //
        if (m_alert != null) return;

        //
        builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view_custom = inflater.inflate(R.layout.layout_alert1, null, false);
        builder.setView(view_custom);
        builder.setCancelable(false);
        m_alert = builder.create();
        Window wnd = m_alert.getWindow();
        wnd.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        //this.WindowManager.LayoutParams lp = wnd.Attributes;

        TextView txt_title = (TextView) view_custom.findViewById(R.id.textView_title);
        if (txt_title != null) {
            txt_title.setText(title);
        }

        TextView txt_hint = view_custom.findViewById(R.id.textView_hint);
        if (txt_hint != null) {
            txt_hint.setText(hint);
        }

        //
        Button btn_close = (Button) view_custom.findViewById(R.id.button_close);
        if (btn_close != null) btn_close.setOnClickListener(lsn);

        m_alert.show();
    }

    @Override
    public void onBackPressed() {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        Log.d("qmcLogin. onBackPressed");
        super.onBackPressed();
    }
}

