package com.qycx;


import static android.content.Context.MODE_PRIVATE;

import static com.qycx.anc.Consts.Str_use100k_bDj;
import static com.qycx.anc.Consts.Str_use100k_ifP2p;
import static com.qycx.base.dlgTalkProc_help.qyFuncs_Droid.bDone_retrieveAllImObjRules;
import static com.qycx.base.qyComm.Consts.CONST_qyCmd_refreshImObjListReq;

import android.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.imCommType_defs;
import com.qycx.base.isProtocolFunc;
import com.qycx.base.myDb;
import com.qycx.base.mySaveSecret;
import com.qycx.base.postMsg2MgrFunc;
import com.qycx.base.qmCommFunc_mis;
import com.qycx.base.guiShare;
import com.qycx.base.qnmCommProc;
import com.qycx.base.qnmCommProc_mis;
import com.qycx.base.qnmResDefs;
import com.qycx.base.qwmMd5;
import com.qycx.base.qyCusResHelp;
import com.qycx.base.qyCustom;
import com.qycx.base.qyDefs;
import com.qycx.base.qyMcMain_global;
import com.qycx.base.qySyncCommProc;
import com.qycx.base.qyWmComm;
import com.qycx.base.qyWndContentType;
import com.qycx.base.qy_getuiNextTranNo;
import com.qycx.base.showPopupMenu;
import com.qycx.base.tmpFunc;
import com.qycx.base.util.Log;
import com.qycx.base.util.ToastUtils;
import com.qycx.base.qyShowInfoCommProc;
import com.south.map.loc.MapLocSettings;

public class Fragment_me extends Fragment {
    boolean m_bInited;

    TextView tv_usrName;
    TextView tv_idInfo;
    TextView tv_io;
    TextView tv_server;
    TextView tvActiveGroup;
    EditText edt_statusServer;
    TextView btn_apply;
    TextView tv_statusQInfo;
    TextView tv_ver;
    Switch use100k_ifP2p;
    Switch use100k_bDj;
    Switch cb_autoLogin;
    Switch cb_mapLocEncryption;
    View layoutAdvancedHeader;
    View layoutAdvancedBody;
    ImageView ivAdvancedChevron;

    AlertDialog m_alert;
    String pendingNewPassword;

    boolean m_bUpdated_afterLogon;
    boolean advancedExpanded;
    SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fg_me, container, false);
        sp = inflater.getContext().getSharedPreferences("config", MODE_PRIVATE);
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        TextView tv_txtTitle = view.findViewById(R.id.txt_title);
        tv_txtTitle.setText("我的");

        TextView tvTopMenu = view.findViewById(R.id.txt_topMenu);
        tvTopMenu.setOnClickListener(v -> {
            if (getActivity() != null) {
                showPopupMenu.qyFuncs_Droid.showPopupMenu(getActivity(), v, R.menu.layout_msglist_topmenu, procMenu);
            }
        });

        tv_usrName = view.findViewById(R.id.usrName);
        tv_idInfo = view.findViewById(R.id.idInfo);
        tv_io = view.findViewById(R.id.io);
        tv_server = view.findViewById(R.id.server);
        tvActiveGroup = view.findViewById(R.id.tvActiveGroup);
        edt_statusServer = view.findViewById(R.id.edt_statusServer);
        btn_apply = view.findViewById(R.id.btn_apply);
        tv_statusQInfo = view.findViewById(R.id.statusQInfo);
        layoutAdvancedHeader = view.findViewById(R.id.layoutAdvancedHeader);
        layoutAdvancedBody = view.findViewById(R.id.layoutAdvancedBody);
        ivAdvancedChevron = view.findViewById(R.id.ivAdvancedChevron);

        btn_apply.setOnClickListener(Btn_apply_Click);
        btn_apply.setText(qyCusResHelp.qyFuncs.getResStr(0, pQyMc.cusRes, (int) qnmResDefs.CONST_resId_apply));

        view.findViewById(R.id.rowChangePassword).setOnClickListener(v -> showChangePasswordDialog());

        view.findViewById(R.id.rowChangeLocKeyword).setOnClickListener(v -> {
            if (!MapLocSettings.isEncryptionEnabled()) {
                ToastUtils.showToast("请先开启地图口令");
                return;
            }
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showChangeLocKeywordDialog();
            }
        });

        layoutAdvancedHeader.setOnClickListener(v -> toggleAdvancedSettings());

        tv_usrName.setText("");

        String statusServer = sp.getString("statusServer", pQyMc.statusServInitVal);
        edt_statusServer.setText(statusServer);
        tv_statusQInfo.setText("");

        String verBuf = qyCustom.qyFuncs.qnmVerStr(pQyMc.iServiceId);
        int iVer = Integer.parseInt(verBuf);
        String verText = String.format("V%d.%d.%d.%d",
                iVer / 1000000, (iVer / 10000) % 100, (iVer / 100) % 100, iVer % 100);
        tv_ver = view.findViewById(R.id.ver);
        tv_ver.setText(verText);

        use100k_ifP2p = view.findViewById(R.id.use100k_ifP2p);
        use100k_ifP2p.setChecked(pProcInfo.use100k_ifP2p);
        use100k_ifP2p.setOnCheckedChangeListener((buttonView, isChecked) -> {
            pProcInfo.use100k_ifP2p = isChecked;
            ToastUtils.showToast(isChecked ? "点对点限流100k" : "不限流");
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(Str_use100k_ifP2p, isChecked);
            editor.commit();
        });

        use100k_bDj = view.findViewById(R.id.use100k_bDj);
        use100k_bDj.setChecked(pProcInfo.use100k_bDj);
        use100k_bDj.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            pProcInfo.use100k_bDj = isChecked;
            ToastUtils.showToast(isChecked ? "限流时使用对讲模式" : "不使用对讲模式");
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(Str_use100k_bDj, isChecked);
            editor.commit();
        });

        cb_autoLogin = view.findViewById(R.id.autoLogin);
        cb_autoLogin.setChecked(pProcInfo.cfg.bAutoLogin);
        cb_autoLogin.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (!pProcInfo.cfg.bSupport_autoLogin) {
                ToastUtils.showToast("不支持设置自动登录");
                cb_autoLogin.setChecked(false);
                return;
            }
            pProcInfo.cfg.bAutoLogin = isChecked;
            ToastUtils.showToast(isChecked ? "自动登录" : "正常登录");
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("autoLogin", pProcInfo.cfg.bAutoLogin);
            editor.commit();
        });

        cb_mapLocEncryption = view.findViewById(R.id.mapLocEncryption);
        MapLocSettings.loadFrom(inflater.getContext());
        cb_mapLocEncryption.setChecked(MapLocSettings.isEncryptionEnabled());
        cb_mapLocEncryption.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            MapLocSettings.setEncryptionEnabled(getContext(), isChecked);
            if (getActivity() instanceof MainActivity) {
                MainActivity main = (MainActivity) getActivity();
                main.clearMapRemoteLocations();
                if (isChecked) {
                    main.requestMapLocKeywordIfNeeded();
                }
            }
            ToastUtils.showToast(isChecked ? "地图口令已开启" : "地图口令已关闭");
        });

        updateActiveGroupDisplay();
        updateCtrls();
        m_bInited = true;
        return view;
    }

    private void toggleAdvancedSettings() {
        advancedExpanded = !advancedExpanded;
        layoutAdvancedBody.setVisibility(advancedExpanded ? View.VISIBLE : View.GONE);
        ivAdvancedChevron.setImageResource(advancedExpanded
                ? R.drawable.ic_imobj_tree_chevron_down
                : R.drawable.ic_imobj_tree_chevron_right);
    }

    private void showChangePasswordDialog() {
        if (getActivity() == null) return;
        View content = getLayoutInflater().inflate(R.layout.dialog_me_change_password, null);
        content.setClipToOutline(true);
        content.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        EditText edtNewPassword = content.findViewById(R.id.edtMeNewPassword);
        AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.MapStyledDialogTheme)
                .setView(content)
                .setCancelable(true)
                .create();
        content.findViewById(R.id.btnMeChangePwdCancel).setOnClickListener(v -> dialog.dismiss());
        content.findViewById(R.id.btnMeChangePwdConfirm).setOnClickListener(v -> {
            String newPw = edtNewPassword.getText().toString();
            if (newPw == null || newPw.isEmpty()) {
                ToastUtils.showToast("密码不能为空");
                return;
            }
            pendingNewPassword = newPw;
            dialog.dismiss();
            showChangePasswordConfirmDialog();
        });
        dialog.setOnShowListener(d -> applyStyledDialogWindow(dialog, content));
        dialog.show();
    }

    /** 透明窗口 + 去掉内层白底，避免圆角弹窗外漏白边。 */
    private void applyStyledDialogWindow(AlertDialog dialog, View content) {
        if (getActivity() == null || dialog == null) return;
        Window w = dialog.getWindow();
        if (w == null) return;
        w.setGravity(Gravity.CENTER);
        w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View decor = w.getDecorView();
        if (decor != null) {
            decor.setBackgroundResource(android.R.color.transparent);
        }
        View panel = w.findViewById(android.R.id.content);
        if (panel != null) {
            panel.setBackgroundResource(android.R.color.transparent);
        }
        ViewParent parent = content.getParent();
        while (parent instanceof View) {
            View wrapper = (View) parent;
            wrapper.setBackgroundResource(android.R.color.transparent);
            wrapper.setPadding(0, 0, 0, 0);
            if (wrapper.getId() == android.R.id.content) {
                break;
            }
            parent = wrapper.getParent();
        }
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.84f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        w.setAttributes(lp);
    }

    private void showChangePasswordConfirmDialog() {
        if (getActivity() == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view1 = getLayoutInflater().inflate(R.layout.layout_yescancel, null);
        builder.setView(view1);
        builder.setCancelable(false);
        m_alert = builder.create();
        m_alert.show();
        Window wnd = m_alert.getWindow();
        if (wnd != null) {
            wnd.setGravity(Gravity.CENTER);
        }
        TextView tvTitle = view1.findViewById(R.id.title);
        tvTitle.setText("清扬视频");
        TextView tvContent = view1.findViewById(R.id.content);
        tvContent.setText("确定要修改密码吗？");
        Button btnYes = view1.findViewById(R.id.yes);
        btnYes.setText("确定");
        btnYes.setOnClickListener(BtnYes_Click);
        Button btnCancel = view1.findViewById(R.id.cancel);
        btnCancel.setText("取消");
        btnCancel.setOnClickListener(BtnCancel_Click);
    }

    private void updateActiveGroupDisplay() {
        if (tvActiveGroup == null || !isAdded()) return;
        TaskActiveGrp.resolveSelectionAfterLogin();
        long groupId = TaskActiveGrp.getSelectedGroupId();
        if (groupId > 0) {
            String name = TaskActiveGrp.getGroupDisplayName(groupId);
            tvActiveGroup.setText(TextUtils.isEmpty(name) ? ("群" + groupId) : name);
        } else {
            tvActiveGroup.setText("未选择");
        }
    }

    /** 地图切组或群列表刷新后由 MainActivity 调用。 */
    public void onActiveGroupChanged() {
        if (m_bInited) {
            updateActiveGroupDisplay();
        }
    }

    private final View.OnClickListener BtnYes_Click = view -> {
        m_alert.dismiss();
        String new_pw = pendingNewPassword;
        if (new_pw == null || new_pw.isEmpty()) return;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;
        qySyncCommProc.CQyMalloc mallocObj = new qySyncCommProc.CQyMalloc();
        qnmCommProc_mis.IM_L_STREAM pStream = new qnmCommProc_mis.IM_L_STREAM();
        if (pStream == null) {
            Log.d("sndDevInfo_qmc . malloc failed.");
        }
        qnmCommProc_mis.QY_MESSENGER_ACCOUNT reqPw = new qnmCommProc_mis.QY_MESSENGER_ACCOUNT();
        reqPw.uiType = imCommType_defs.Consts.CONST_imCommType_messengerAccount;
        reqPw.messengerPasswd = qwmMd5.qyFuncs.bQyGetStrMd5_x(new_pw);
        pStream.uiType = imCommType_defs.Consts.CONST_imCommType_lStream;
        pStream.uiContentType = (int) reqPw.uiType;
        int len = isProtocolFunc.qyFuncs.messengerAccount2Stream((int) pStream.uiContentType, reqPw, pStream.buf);
        if (len > 0) ;
        pStream.ulStreamLen = len;
        long tStartTran = tmpFunc.qyFuncs.mytime();
        int uiTranNo = qy_getuiNextTranNo.qyFuncs.getuiNextTranNo.call();
        postMsg2MgrFunc.qyFuncs.postMsg2Mgr_mc(null, pMisCnt, null, qnmCommProc_mis.Consts.CONST_misMsgType_req, (byte) 0, CONST_qyCmd_refreshImObjListReq, tStartTran, uiTranNo, (short) 0, pStream, 0, 0, 0, 0, null, false);
        pendingNewPassword = null;
    };

    private final View.OnClickListener BtnCancel_Click = view -> {
        m_alert.dismiss();
        pendingNewPassword = null;
    };

    private final showPopupMenu.qyFuncs_Droid.PF_procMenu procMenu = (Context context, int menuId) -> {
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        if (menuId == R.id.quit) {
            guiShare.GuiShare.pf_PostMessage_new.get().call(pQyMc.gui.hMainWnd, qyWndContentType.Consts.CONST_qyWndContentType_mainWnd, qyWmComm.Consts.CONST_qyWm_postComm, qyWmComm.Consts.CONST_qyWmParam_closeWnd, 0);
        }
        return 0;
    };

    int updateCtrls() {
        int iErr = -1;
        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
        myDb.QM_dbFuncs pDbFuncs = pQyMc.p_g_dbFuncs;
        qmCommFunc_mis.MIS_CNT pMisCnt = pProcInfo.pMisCnt;

        if (pMisCnt == null) return -1;
        if (pDbFuncs == null) return -1;
        if (pQyMc == null) return -1;
        if (null == pQyMc.gui.pDb) {
            if (pQyMc.bLogon) {
                Log.d("pQyMc.gui.pDb  null");
            }
            return -1;
        }

        qnmCommProc_mis.QY_MESSENGER_INFO msgrInfo = new qnmCommProc_mis.QY_MESSENGER_INFO();
        qnmCommProc_mis.QY_MESSENGER_REGINFO regInfo = new qnmCommProc_mis.QY_MESSENGER_REGINFO();
        qyDefs.QY_MESSENGER_ID idInfo = pMisCnt.idInfo;

        if (!myDb.QM_dbFuncs.pf_bGetMessengerInfoBySth.call(pDbFuncs, pQyMc.gui.pDb, pQyMc.cfg.db.iDbType, qnmCommProc.qyFuncs.CONST_fieldIdTable_en, "", idInfo, 0, msgrInfo, regInfo, null, null)) {
            return iErr;
        }

        tv_usrName.setText(regInfo.dw + " " + regInfo.bm + " " + regInfo.syr);
        tv_idInfo.setText(String.valueOf(idInfo.ui64Id));
        tv_server.setText(pMisCnt.server.ip + ":" + pMisCnt.server.port);
        updateActiveGroupDisplay();

        if (!this.m_bUpdated_afterLogon) {
            if (bDone_retrieveAllImObjRules(pMisCnt)) {
                this.m_bUpdated_afterLogon = true;
            }
        }

        return 0;
    }

    private final View.OnClickListener Btn_apply_Click = view -> {
        String statusServer = edt_statusServer.getText().toString();
        mySaveSecret.qyFuncs_Droid.saveDbgCfg(statusServer);
        qyShowInfoCommProc.qyFuncs.showInfo_cfgServer(statusServer);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("statusServer", statusServer);
        edit.apply();
    };

    public int gui_onTimer(Object p0, Object p1, Object p2) {
        if (!m_bInited) return -1;

        ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

        if (!this.m_bUpdated_afterLogon) {
            updateCtrls();
        }

        String strOut = String.format("↑ %d Kbps", pProcInfo.status.netStat.ins.uiOutSpeedInKbps);
        String strIn = String.format("↓ %d Kbps", pProcInfo.status.netStat.ins.uiInSpeedInKbps);
        if (tv_io != null) {
            tv_io.setText(strOut + "  " + strIn);
        }

        String status = qyShowInfoCommProc.qyFuncs.showInfo_getStatusText();
        if (tv_statusQInfo != null) {
            tv_statusQInfo.setText(status);
        }
        updateActiveGroupDisplay();
        return 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (m_bInited) {
            updateActiveGroupDisplay();
            String status = qyShowInfoCommProc.qyFuncs.showInfo_getStatusText();
            if (tv_statusQInfo != null) {
                tv_statusQInfo.setText(status);
            }
        }
    }

    @Override
    public void onDestroyView() {
        m_bInited = false;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Fragment_me  OnDestroy");
    }
}
