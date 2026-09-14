package com.qycx.base;

import static com.qycx.base.guiShare.Consts.CONST_fz_me;
import static com.qycx.base.guiShare.Consts.CONST_fz_peer;
import static com.qycx.base.isTalkerQProc.findIsTalker;
import static com.qycx.base.qmcProc.old_save_dlgTalk_share_eglContext;
import static com.qycx.base.qyDefs.Consts.CONST_objType_imGrp;
import static com.qycx.base.qyDefs.Consts.CONST_objType_mosaicStream_resource;
import static com.qycx.base.qyDefs.Consts.CONST_objType_mosaicStream_video;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWmParam_chkTalkDraw;
import static com.qycx.base.qyWmComm.Consts.CONST_qyWm_postComm;
import static com.qycx.base.qyWndContentType.Consts.CONST_fgType_talk_av2;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;
import static com.qycx.base.tmpFunc.qyFuncs.mySleep;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import androidx.fragment.app.Fragment;

//import com.qycx.Activity_dlgTalk;
//import com.qycx.Activity_dlgTalk_av;

//
import com.qycx.Fragment_dlgTalk_av2;
import com.qycx.MainActivity;
import com.qycx.base.isCliGl.t1Frm;
import com.qycx.base.util.Log;
import com.qycx.gleslibrary.EglCore;
import com.qycx.gleslibrary.EglSurfaceBase;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

//
public class talkDraw {
    public static class CTalkDraw extends qmcDrawTmpl.CQmcDrawTmpl {

        //
        public dlgTalkProc.DLG_TALK_var talker_m_var  =  null;

        //
        public dlgTalkProc.TALK_drawLayout m_drawLayout;
        public int tn_modified_drawLayout;

        //  2026/09/12
        public boolean m_bDx=false;
        public boolean m_bDx_prev=false;

        //  2026/09/13: bDx 切入时若 me 未显示则自动显示，切回时自动隐藏。
        //  该标记记录"这次显示是 bDx 自动打开的"，仅此情况切回时才隐藏——
        //  用户手动开的 me 切换前后都保持可见。
        boolean m_bMeAutoShown_byDx=false;

        //  2026/09/13: draw_off2on 日志节流（状态变化时必打，否则每 300 帧打 1 条，
        //  防止每帧日志刷爆 logcat 缓冲、冲掉其他关键日志）
        int dbg_o2n_cnt=0;
        int dbg_o2n_tex=-1, dbg_o2n_w=-1, dbg_o2n_h=-1;
        boolean dbg_o2n_bdx=false;

        //  2026/09/13: bDx 时绘制顺序为 me 全屏(背景) -> peer 左下 1/4(前景)，
        //  peer 的 FBO 纹理名由 draw_off2on 暂存、draw_meOnly 取用。
        //  工程未开深度测试，纯画家算法——谁后画谁在上。
        int m_texId_off_last = 0;


        //
        TalkTextureLoader mTextureLoader_me;
        TalkTextureLoader mTextureLoader_peer;

        //
        public static int me_texId;
        public static SurfaceTexture me_st;


        //
        public static class Talk_tex {
            public int m_textureId_0;
            public int m_textureId_1;
            //
            public int m_cur_textureId;
        }

        ;
        public Talk_tex tex_me, tex_peer;

        public FloatBuffer mVertexBuffer_me;

        //
        public float[] VERTEX_me = {
                //1,1,0,
                //-3.27258f,15.18121f,-100.2942f,                
                1.105982f, -37.78366f, -85.60948f,
                //-1, 1, 0,
                //-52.47561f,15.18121f,-85.53316f,
                -15.27864f, -37.78366f, -80.69443f,
                //-1, -1, 0,
                //-45.90787f,-64.36166f,-63.64063f,
                -13.08971f, -64.29439f, -73.39761f,
                //1, -1, 0,
                //3.29516f,-64.36166f,-78.40168f,
                3.294913f, -64.29439f, -78.31304f,


        };


        //
        public t1Frm.T1_frm tf_me;
        public t1Frm.T1_frm tf_peer;
        //  2026/09/13: bDx 时 peer 左下角 1/4 的 blit 布局专用。
        //  tf_peer 被 FBO 源渲染(off)与全屏 blit(on)共用且必须保持全屏，
        //  pip 布局单独用一个 T1_frm，避免双重缩小。
        public t1Frm.T1_frm tf_peer_pip;
        //  2026/09/13: me 画面动态文字叠加的布局 T1_frm
        public t1Frm.T1_frm tf_me_text;
        public t1Frm.T1_frm centerSurfaceViewPortRect;

        //
        //  2026/09/13: me 画面动态文字（实时变化）。
        //  任何线程任何时候直接赋值即可，如：
        //      talkDraw.CTalkDraw.s_meOverlayText = "我是好朋友";
        //  渲染层在文字变化时才重建纹理（懒更新），置空串 "" 即隐藏。
        //  默认给一行演示文字，验证通过后改为 "" 即默认隐藏。
        public static volatile String s_meOverlayText = "我是好朋友";

        //  文字纹理缓存（GL 线程读写）
        int m_overlayTexId = 0;
        String m_overlayTexText = null;
        int m_overlayTexW = 0, m_overlayTexH = 0;

        //
        public static class TalkTextureLoader extends TextureLoader {
            //
            public dlgTalkProc.TALK_drawMem m_drawMem;
            //
            public boolean m_bMe;

            public TalkTextureLoader(EglCore eglCore_shared, Context androidContext, CTalkDraw myDraw, boolean bMe) {
                super(eglCore_shared, androidContext, myDraw);
                this.m_bMe = bMe;
            }

            //
            public boolean bDrawModified() {
                ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
                ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
                CTalkDraw mDraw = (CTalkDraw) this.mMyDraw;

                //
                //Fragment_dlgTalk_av fg = (Fragment_dlgTalk_av)mDraw.mOwner;
                //Activity_dlgTalk_av hTalk_av = (Activity_dlgTalk_av) mDraw.mOwner.getActivity();
                //Activity_dlgTalk hTalk = (Activity_dlgTalk) hTalk_av.mOwner_hTalk.get();
                dlgTalkProc.DLG_TALK_var m_var = mDraw.talker_m_var;//hTalk.m_var;

                if (m_bMe) {
                    return (m_drawMem.tn_modified != m_var.m_drawLayout.param.me.tn_modified);
                }

                //                  
                return (m_drawMem.tn_modified != m_var.m_drawLayout.param.peer.tn_modified);
            }

            //
            public int updateDrawMem() {
                ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
                ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
                CTalkDraw mDraw = (CTalkDraw) this.mMyDraw;

                //
                //Activity_dlgTalk_av hTalk_av = (Activity_dlgTalk_av) mDraw.mOwner.getActivity();
                //Activity_dlgTalk hTalk = hTalk_av.mOwner_hTalk.get();
                dlgTalkProc.DLG_TALK_var m_var = mDraw.talker_m_var;//hTalk.m_var;

                if (m_bMe) {
                    if (m_drawMem.tn_modified != m_var.m_drawLayout.param.me.tn_modified) {
                        m_drawMem.tn_modified = m_var.m_drawLayout.param.me.tn_modified;
                        m_drawMem.param = m_var.m_drawLayout.param.me.param;
                    }
                } else {
                    if (m_drawMem.tn_modified != m_var.m_drawLayout.param.peer.tn_modified) {
                        m_drawMem.tn_modified = m_var.m_drawLayout.param.peer.tn_modified;
                        m_drawMem.param = m_var.m_drawLayout.param.peer.param;
                    }
                }
                return 0;
            }

            @Override
            public int fillTex_run() {
                ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
                ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
                CTalkDraw mDraw = (CTalkDraw) this.mMyDraw;

                boolean bDbg = false;
                String str;

                //
                //Activity_dlgTalk_av hTalk_av = (Activity_dlgTalk_av) mDraw.mOwner.getActivity();
                //Activity_dlgTalk hTalk = hTalk_av.mOwner_hTalk.get();
                dlgTalkProc.DLG_TALK_var m_var = mDraw.talker_m_var;//hTalk.m_var;

                //
                //mDraw.tn_modified_drawLayout = m_var.tn_modified_drawLayout;
                //mDraw.m_drawLayout = m_var.m_drawLayout;

                if (!this.m_bMe) {
                    int ii = 0;

                }


                //
                for (; !mDraw.bQuit; ) {
                    //
                    updateDrawMem();

                    //
                    if (m_drawMem.param.playerId.uiTranNo_player == 0) {
                        mySleep(200);
                        continue;
                    }
                    qmcCommFunc_is_open.QY_PLAYER player = getPlayerByIndex.qyFuncs.getPlayerByIndex(pProcInfo, m_drawMem.param.playerId.index_player);
                    if (player == null) {
                        mySleep(200);
                        continue;
                    }
                    //
                    if (player.video.vh.bih.biWidth == 0 || player.video.vh.bih.biHeight == 0) {
                        //
                        str = String.format("fillTex_run: %s, bih err %sx%s", this.m_tag, player.video.vh.bih.biWidth, player.video.vh.bih.biHeight);
                        Log.d("fillTex_run: " + str);
                        mySleep(200);
                        continue;
                    }
                    //
                    ttl_readPkt.qyFuncs_Droid.ttl_readPkt(this);

                }


                //
                if (m_bMe) {
                    int ii = 0;
                }

                return 0;
            }

            //
            public int tmpFillTex(byte[] rgb, int w, int h) {
                return 0;
            }


            //
            public int tmpFillTex(int w, int h) {
                CTalkDraw myDraw = (CTalkDraw) this.mMyDraw;
                //byte[] rgb_array = rgb;
                boolean bDbg = false;
                if (!m_bMe) {
                    //bDbg = true;
                }

                //
                Talk_tex myTex = null;
                if (m_bMe) {
                    myTex = myDraw.tex_me;
                } else {
                    myTex = myDraw.tex_peer;
                }

                //                  
                if (myTex.m_textureId_0 == 0) {
                    int texId = loadTexture(w, h, m_texId_0);
                    if (texId != 0) {
                        myTex.m_textureId_0 = texId;
                    }
                } else if (myTex.m_textureId_1 == 0) {
                    int texId = loadTexture(w, h, m_texId_1);
                    if (texId != 0) {
                        myTex.m_textureId_1 = texId;
                    }
                }
                return 0;

            }


        }


        //
        public CTalkDraw(Fragment owner, int iFgType, EglSurfaceBase eglSurface) {
            super(owner, iFgType, eglSurface);
            //base(owner, windowSurface);

            int ii = 0;


        }

        @Override
        public int init() {
            int iErr = -1;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if (pQyMc == null) {
                Log.e("CTalkDraw.init: pQyMc is null");
                return iErr;
            }
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();

            //
            if (0 != super.init()) return -1;

            //
            //
            mVertexBuffer_me = ByteBuffer.allocateDirect(VERTEX_me.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(VERTEX_me);
            mVertexBuffer_me.position(0);


            //
            this.tf_me = new t1Frm.T1_frm();

            //            
            qyDefs_open.CAP_RECT capRect = new qyDefs_open.CAP_RECT();
            capRect.iX = 0;
            capRect.iY = this.mEglSurface.getHeight() * 2 / 3;
            capRect.iW = (int) this.mEglSurface.getWidth() / 3;
            capRect.iH = (int) this.mEglSurface.getHeight() / 3;

            if (capRect.iH == 0) return iErr;
            float fAspectRatio_img = capRect.iW / (float) capRect.iH;
            if (0 != tf_me.T1_frm_init(null, capRect, fAspectRatio_img)) {
                return iErr;
            }

            //
            this.tf_peer = new t1Frm.T1_frm();

            //
            //            
            capRect.iX = 0;
            capRect.iY = 0;
            capRect.iW = (int) this.mEglSurface.getWidth();
            capRect.iH = (int) this.mEglSurface.getHeight();

            if (capRect.iH == 0) return iErr;
            fAspectRatio_img = capRect.iW / (float) capRect.iH;
            if (0 != tf_peer.T1_frm_init(null, capRect, fAspectRatio_img)) {
                return iErr;
            }

            //
            //  2026/09/13: bDx 时 peer 左下角 1/4 的 blit 布局专用 T1_frm，
            //  初始给全屏矩形，实际矩形由 handleOff2OnRender 按 bDx 设置。
            this.tf_peer_pip = new t1Frm.T1_frm();
            if (0 != tf_peer_pip.T1_frm_init(null, capRect, fAspectRatio_img)) {
                return iErr;
            }

            //
            //  2026/09/13: me 画面动态文字叠加的布局 T1_frm
            this.tf_me_text = new t1Frm.T1_frm();
            if (0 != tf_me_text.T1_frm_init(null, capRect, fAspectRatio_img)) {
                return iErr;
            }

            this.tex_me = new Talk_tex();
            this.tex_peer = new Talk_tex();

            //
            if (pProcInfo.cfg.ucb_USE_Fragment_dlgTalk_av) {
                //
                mTextureLoader_me = new TalkTextureLoader(this.mEglCore_shared1, mOwner.getActivity(), this, true);
                if (mTextureLoader_me.init() != 0) {
                    return iErr;
                }
                mTextureLoader_me.iTestNo = 1;
                mTextureLoader_me.m_tag = "tex_me";
                mTextureLoader_me.start();


                mTextureLoader_peer = new TalkTextureLoader(this.mEglCore_shared1, mOwner.getActivity(), this, false);
                if (mTextureLoader_peer.init() != 0) {
                    return iErr;
                }
                mTextureLoader_peer.iTestNo = 2;
                mTextureLoader_peer.m_tag = "tex_peer";
                mTextureLoader_peer.start();
            }

            //
            //  2026/09/13 fix: me_st/me_texId 在一次通话生命周期内只创建一次（me_st==null 才新建）。
            //  off2on 模式下 myDraw_off/myDraw_on 两个实例都会 init()，直接覆盖会
            //  (a) 泄漏上一份 (b) 让 camera_RenderThread 持有的 me_st 与静态引用失联；
            //  渲染线程重启(needRestart/尺寸变化)时也必须复用。
            //  释放统一移到 Fragment_dlgTalk_av2.exitVideoCapDev()（相机线程 join 之后）。
            if (me_st == null) {
                int[] textureHandle = new int[1];
                GLES20.glGenTextures(1, textureHandle, 0);
                int tmpiRet = GLES20.glGetError();
                if (textureHandle[0] == 0) {
                    //Log.d(TAG,"TextureLoader.Run failed, recycleGenTextures failed");
                    return iErr;
                }
                //
                me_texId = textureHandle[0];
                me_st = new SurfaceTexture(me_texId);
                if (me_st == null) return iErr;
            }


            //
            do {

                //
                if (mOwner instanceof Fragment_dlgTalk_av2) {
                    Fragment_dlgTalk_av2 fg = (Fragment_dlgTalk_av2) mOwner;
                    //Activity_dlgTalk hTalk = hTalk_av.mOwner_hTalk.get();

                    /*
                    Activity_dlgTalk_av hTalk_av = (Activity_dlgTalk_av) mOwner.getActivity();
                    if (hTalk_av == null)  {
                        break;
                    }

                     */


                    //
                    dlgTalkProc.DLG_TALK_var m_var = talker_m_var;//fg.talkmOwner hTalk.m_var;

                    //
                    Object act_av = mOwner.getActivity();

                    //
                    old_save_dlgTalk_share_eglContext(m_var, act_av, this.mEglCore_shared1.mEGLContext, "talkDraw.init");

                    //
                    switch (miFgType) {
                        case CONST_fgType_talk_av2: {

                            //hTalk_av.mOwner_hTalk.get().gui_PostMessage(CONST_qyWm_postComm, CONST_qyWmParam_chkTalkDraw, 0);
                            //
                            guiShare.GuiShare.pf_PostMessage_new.get().call(fg,miFgType,CONST_qyWm_postComm, CONST_qyWmParam_chkTalkDraw, 0);
                        }

                        break;
                        default:
                            break;
                    }
                }

            } while  (  false  );



            //
            iErr = 0;


            return iErr;
        }


        @Override
        public void exit() {
            this.bQuit = true;
            //
            if (mTextureLoader_me != null) {
                mTextureLoader_me.halt();
                mTextureLoader_me = null;
            }
            if (mTextureLoader_peer != null) {
                mTextureLoader_peer.halt();
                mTextureLoader_peer = null;
            }
            //
            //Activity_dlgTalk_av hTalk_av = (Activity_dlgTalk_av) mOwner.getActivity();
            //if (hTalk_av != null)
            {
                //Activity_dlgTalk hTalk = hTalk_av.mOwner_hTalk.get();
                dlgTalkProc.DLG_TALK_var m_var =this.talker_m_var;//hTalk.m_var;
                if  (  m_var!=null) {
                    //
                    Object act_av = mOwner.getActivity();
                    //
                    old_save_dlgTalk_share_eglContext(m_var, act_av, null, "talkDraw.exit");
                }
            }

            //
            //  2026/09/13 fix: 这里严禁释放 me_st/me_texId！
            //  通话中 camera_RenderThread 正持有并使用 me_st（updateTexImage），
            //  渲染线程重启(needRestart/尺寸变化)触发本 exit() 时若 release，
            //  相机线程会撞上已销毁的 mutex -> SIGABRT
            //  (FORTIFY: pthread_mutex_lock called on a destroyed mutex)。
            //  生命周期：init() 复用（me_st==null 才新建），
            //  Fragment_dlgTalk_av2.exitVideoCapDev() 在相机线程 join 之后统一释放。

            //
            if (this.tf_me != null) {
                tf_me.T1_frm_exit(null);
                tf_me = null;
            }
            //
            if (this.tf_peer != null) {
                tf_peer.T1_frm_exit(null);
                tf_peer = null;
            }
            //
            //  2026/09/13: tf_peer_pip 同样释放（bDx 时 peer 左下 1/4 的 blit 布局），
            //  渲染线程重启时 init() 会重建。
            if (this.tf_peer_pip != null) {
                tf_peer_pip.T1_frm_exit(null);
                tf_peer_pip = null;
            }
            //
            //  2026/09/13: me 动态文字的布局与纹理一并释放（GL 线程），
            //  重启后首次绘制时按 s_meOverlayText 重建。
            if (this.tf_me_text != null) {
                tf_me_text.T1_frm_exit(null);
                tf_me_text = null;
            }
            if (m_overlayTexId != 0) {
                GLES20.glDeleteTextures(1, new int[]{m_overlayTexId}, 0);
                m_overlayTexId = 0;
                m_overlayTexText = null;
            }

            //
            super.exit();
            //
            return;
        }


        @Override
        public int onSize(int w, int h) {
            int iErr = -1;

            super.onSize(w, h);


            //
            iErr = 0;

            return iErr;
        }


        //
        public ctxQmc.TMP_tex_info get_tti_peer(ctxQmc.CCtxQmc pProcInfo, Object hTalk_unused, ctxQmc.TMP_tex_info tti) {

            dlgTalkProc.DLG_TALK_var m_var = this.talker_m_var;//hTalk.get_pm_var();

            //
            ctxQmc.TMP_texes texes = pProcInfo.av.texes;
            if (texes == null) return null;

            ctxQmc.TMP_task_texes ttt = texes.taskAvTexes;
            int i;
            int usCnt_activeMems = ttt.usCnt_activeMems;
            if (usCnt_activeMems > ttt.tti_peers.length) return null;
            if (m_var.addr.uiObjType == CONST_objType_imGrp) {
                for (i = 0; i < usCnt_activeMems; i++) {
                    if (ttt.tti_peers[i].uiObjType == CONST_objType_mosaicStream_video
                            || ttt.tti_peers[i].uiObjType == CONST_objType_mosaicStream_resource) {
                        return ttt.tti_peers[i];
                    }
                }
            } else {
                // 暂时不处理屏幕共享
                for (i = 0; i < usCnt_activeMems; i++) {
                    if (ttt.tti_peers[i].idInfo.ui64Id == m_var.addr.idInfo.ui64Id
                            && ttt.tti_peers[i].uiObjType == 0) {
                        return ttt.tti_peers[i];
                    }
                }

            }

            return null;
        }


        //
        public void myPreRender(Object pCtx) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

            int width = this.mEglSurface.getWidth();
            int height = this.mEglSurface.getHeight();

            if (height == 0) return;

            //Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) mOwner.getActivity();
            //if (dlgTalk_av == null) return;

            //Activity_dlgTalk dlgTalk = (Activity_dlgTalk) dlgTalk_av.mOwner_hTalk.get();
            dlgTalkProc.DLG_TALK_var m_var = this.talker_m_var;//dlgTalk.m_var;
            if (!m_var.av.taskInfo.bTaskExists) return;

            handleSharedObjRender(pCtx, pProcInfo, m_var, width, height);
            handleMainRender(pCtx, mainActivity, width, height, m_var,false);

            return;
        }

        //
        public void myPreRender_off(Object pCtx) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

            int width = this.mEglSurface.getWidth();
            int height = this.mEglSurface.getHeight();

            if (height == 0) return;

            //
            //Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) mOwner.getActivity();
            //if (dlgTalk_av == null) return;

            //Activity_dlgTalk dlgTalk = (Activity_dlgTalk) dlgTalk_av.mOwner_hTalk.get();
            dlgTalkProc.DLG_TALK_var m_var = this.talker_m_var;//dlgTalk.m_var;
            if (!m_var.av.taskInfo.bTaskExists) return;

            handleMainRender(pCtx, mainActivity, width, height, m_var,true);

            return;
        }


        //
        public void myPreRender_off2on(Object pCtx, int off_w, int off_h) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

            int width = this.mEglSurface.getWidth();
            int height = this.mEglSurface.getHeight();

            if (height == 0) return;

            //
            //Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) mOwner.getActivity();
            //if (dlgTalk_av == null) return;

            //Activity_dlgTalk dlgTalk = (Activity_dlgTalk) dlgTalk_av.mOwner_hTalk.get();
            dlgTalkProc.DLG_TALK_var m_var = this.talker_m_var;//dlgTalk.m_var;
            if (!m_var.av.taskInfo.bTaskExists) return;

            //
            boolean bDx=false;
            if  (  pProcInfo.m_pTalkExt.bNoVDownload(m_var.av.taskInfo.iTaskId)  )  bDx=true;

            //
            if (bDx != m_bDx_prev) {
                Log.d("atdx", String.format("talkDraw: bDx %b -> %b, taskId=%d",
                        m_bDx_prev, bDx, m_var.av.taskInfo.iTaskId));

                //
                //  2026/09/13: bDx 切换时 me 自动显隐——
                //  切入 bDx：me 未显示则自动显示（记标记）；
                //  切回非 bDx：仅当是 bDx 自动打开的才隐藏，
                //  用户手动开启的 me 保持一直可见。
                try {
                    if (bDx && !m_var.av.taskInfo.bShow_localVideo) {
                        m_var.av.taskInfo.bShow_localVideo = true;
                        m_bMeAutoShown_byDx = true;
                        Log.d("atdx", "bDx on: auto-show me");
                    } else if (!bDx && m_bMeAutoShown_byDx) {
                        m_var.av.taskInfo.bShow_localVideo = false;
                        m_bMeAutoShown_byDx = false;
                        Log.d("atdx", "bDx off: auto-hide me");
                    } else if (!bDx) {
                        m_bMeAutoShown_byDx = false;
                    }

                    //  按钮图标同步（bShow_localVideo 被渲染线程改动，UI 图标
                    //  post 到主线程刷新，避免与 changeLocalVideo() 的手动
                    //  切换路径图标状态脱节）
                    if (mOwner instanceof Fragment_dlgTalk_av2) {
                        final Fragment_dlgTalk_av2 fg = (Fragment_dlgTalk_av2) mOwner;
                        if (fg.mMainHandler != null) {
                            fg.mMainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    fg.syncLocalVideoIcon();
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    Log.e("bDx auto-show/hide me failed, " + e);
                }

                //  2026/09/13: bDx 切换时 me/peer 布局互换（me 全屏、peer 左下 1/4），
                //  两个矩形都强制重建（capRect.iX=-1 触发 T1_frm_set），两个方向都要做，
                //  防止 needsUpdate 状态残留导致一侧不重建。
                if (this.tf_peer != null) {
                    this.tf_peer.capRect.iX = -1;
                }
                if (this.tf_me != null) {
                    this.tf_me.capRect.iX = -1;
                }
                if (this.tf_peer_pip != null) {
                    this.tf_peer_pip.capRect.iX = -1;
                }
                m_bDx_prev = bDx;
            }
            m_bDx = bDx;

            //
            handleOff2OnRender(pCtx, off_w,off_h, width, height,m_var);

            //
            return;
        }


        public void myPreRender_meOnly(Object pCtx) {
            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;

            int width = this.mEglSurface.getWidth();
            int height = this.mEglSurface.getHeight();

            if (height == 0) return;

            //Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) mOwner.getActivity();
            //if (dlgTalk_av == null) return;

            //Activity_dlgTalk dlgTalk = (Activity_dlgTalk) dlgTalk_av.mOwner_hTalk.get();
            dlgTalkProc.DLG_TALK_var m_var = this.talker_m_var;//dlgTalk.m_var;
            if (!m_var.av.taskInfo.bTaskExists) return;

            handleSharedObjRender(pCtx, pProcInfo, m_var, width, height);

            return;
        }



        //
        private void handleSharedObjRender(Object pCtx, ctxQmc.CCtxQmc pProcInfo, dlgTalkProc.DLG_TALK_var m_var, int width, int height) {
            if (m_var.av.ucbGot_iIndex_sharedObj_localAv) {
                qmcSharedObj.QY_SHARED_OBJ pSharedObj = qmcSharedObj.qyFuncs.getActiveSharedObjByIndex(
                        pProcInfo, m_var.av.iIndex_sharedObj_localAv);
                if (pSharedObj == null) return;

                int img_w = pSharedObj.var.m_o_iW;
                int img_h = pSharedObj.var.m_o_iH;

                if (img_h != 0) {
                    float fAspectRatio_img = img_w / (float) img_h;

                    qyDefs_open.CAP_RECT capRect = new qyDefs_open.CAP_RECT();
                    capRect.iX = width * 2 / 3;
                    capRect.iY = 0;
                    capRect.iW = width / 3;
                    capRect.iH = height / 3;

                    //
                    //  2026/09/13: bDx（单向）时 me 占满全窗口，peer 缩到左下角 1/4
                    boolean  bDx = m_bDx;//
                    if  (  bDx)  {
                        capRect.iX=0;
                        capRect.iY=0;
                        capRect.iW=width;
                        capRect.iH=height;
                    }

                    //
                    if (needsUpdate(this.tf_me.capRect, capRect, fAspectRatio_img, this.tf_me.fAspectRatio_img)) {
                        qyTimestamp.QY_timestamp ts = new qyTimestamp.QY_timestamp();
                        boolean ucbAutoClip = false;
                        float me_z_1 = CONST_fz_me;

                        if (0 != this.tf_me.T1_frm_set(pCtx, ts, capRect, fAspectRatio_img, ucbAutoClip, false, mMats, me_z_1, "", false,false)) {
                            return;
                        }
                    }
                }
            }
        }


        //
        private void handleMainRender(Object pCtx, MainActivity mainActivity, int width, int height, dlgTalkProc.DLG_TALK_var m_var,boolean bOff) {
            int img_w;
            int img_h;

            if (mainActivity.textureId != 0) {
                img_w = mainActivity.textureIw;
                img_h = mainActivity.textureIh;
            } else {
                img_w = this.mTexName_default_iW;
                img_h = this.mTexName_default_iH;
            }

            if (img_h != 0) {
                float fAspectRatio_img = img_w / (float) img_h;

                qyDefs_open.CAP_RECT capRect = new qyDefs_open.CAP_RECT();
                capRect.iX = 0;
                capRect.iY = 0;
                capRect.iW = width;
                capRect.iH = height;

                //
                //  2026/09/13: 直连屏幕路径 bDx 时 peer 缩到左下角 1/4（与 off2on 的
                //  tf_peer_pip 一致）。注意 bOff=true 是 FBO 源渲染（myPreRender_off），
                //  必须保持全屏——否则 FBO 里先缩 1/4、blit 再缩 1/4 会双重缩小。
                if  (  m_bDx && !bOff)  {
                    capRect.iX = 0;
                    capRect.iY = height / 2;
                    capRect.iW = width / 2;
                    capRect.iH = height / 2;
                }

                //
                if (needsUpdate(this.tf_peer.capRect, capRect, fAspectRatio_img, this.tf_peer.fAspectRatio_img)) {
                    qyTimestamp.QY_timestamp ts = new qyTimestamp.QY_timestamp();
                    boolean ucbAutoClip = false;
                    float me_z_1 = CONST_fz_peer;

                    if (0 != this.tf_peer.T1_frm_set(pCtx, ts, capRect, fAspectRatio_img, ucbAutoClip, false, mMats, me_z_1, "", true,false)) {
                        return;
                    }
                    //
                    if  (  !bOff) {
                        m_var.av.hDc_av_rect = tf_peer.centerSurfaceViewPortRect;
                    }
                }
            }
        }


        //
        private void handleOff2OnRender(Object pCtx, int img_w, int img_h, int width, int height, dlgTalkProc.DLG_TALK_var m_var) {

            if (img_h != 0) {
                float fAspectRatio_img = img_w / (float) img_h;

                qyDefs_open.CAP_RECT capRect = new qyDefs_open.CAP_RECT();
                capRect.iX = 0;
                capRect.iY = 0;
                capRect.iW = width;
                capRect.iH = height;

                //
                //  2026/09/13: tf_peer（FBO blit 源的全屏目标）保持全屏不变——
                //  bDx 时 peer 的左下角 1/4 布局改由 tf_peer_pip 承担，
                //  避免 FBO 源渲染与 blit 共用 tf_peer 造成双重缩小。
                if (needsUpdate(this.tf_peer.capRect, capRect, fAspectRatio_img, this.tf_peer.fAspectRatio_img)) {
                    qyTimestamp.QY_timestamp ts = new qyTimestamp.QY_timestamp();
                    boolean ucbAutoClip = false;
                    float me_z_1 = CONST_fz_peer;

                    if (0 != this.tf_peer.T1_frm_set(pCtx, ts, capRect, fAspectRatio_img, ucbAutoClip, false, mMats, me_z_1, "", true,true)) {
                        return;
                    }
                    //
                    if  (  !m_bDx) {
                        m_var.av.hDc_av_rect = tf_peer.centerSurfaceViewPortRect;
                    }
                }

                //
                //  2026/09/13: bDx（单向）时 peer 画在左下角 1/4（宽 1/2 × 高 1/2），
                //  me 占满全窗口。y 轴原点在顶部（与 me 默认右上 iY=0 一致），
                //  左下角即 iX=0、iY=height/2。
                if  (  m_bDx)  {
                    qyDefs_open.CAP_RECT capRect_pip = new qyDefs_open.CAP_RECT();
                    capRect_pip.iX = 0;
                    capRect_pip.iY = height / 2;
                    capRect_pip.iW = width / 2;
                    capRect_pip.iH = height / 2;

                    if (this.tf_peer_pip != null && needsUpdate(this.tf_peer_pip.capRect, capRect_pip, fAspectRatio_img, this.tf_peer_pip.fAspectRatio_img)) {
                        qyTimestamp.QY_timestamp ts_pip = new qyTimestamp.QY_timestamp();
                        boolean ucbAutoClip_pip = false;
                        float peer_pip_z_1 = CONST_fz_peer;

                        if (0 != this.tf_peer_pip.T1_frm_set(pCtx, ts_pip, capRect_pip, fAspectRatio_img, ucbAutoClip_pip, false, mMats, peer_pip_z_1, "", true, true)) {
                            return;
                        }
                        //
                        m_var.av.hDc_av_rect = this.tf_peer_pip.centerSurfaceViewPortRect;
                    }
                }
            }
        }

        //
        private boolean needsUpdate(qyDefs_open.CAP_RECT oldRect, qyDefs_open.CAP_RECT newRect, float newAspectRatio, float oldAspectRatio) {
            return oldRect.iX != newRect.iX || oldRect.iY != newRect.iY || oldRect.iW != newRect.iW || oldRect.iH != newRect.iH || Math.abs(newAspectRatio - oldAspectRatio) > 0.001;
        }

        @Override
        public void draw() {
            boolean bDbg = false;
            String str;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if(pQyMc == null) return;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
            // draw()/draw_off()/draw_off2on()/draw_meOnly() 开头统一加：
            if (mainActivity == null) return;
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (pProcInfo == null) return;

            dlgTalkProc.DLG_TALK_var m_var = this.talker_m_var;//dlgTalk.m_var;
if (m_var == null) return;   // myPreRender 同理

            if (pProcInfo.cfg.ucb_USE_specialDbg) {
                return;
            }

            myPreRender(null);

            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            //
            if  (  pProcInfo.cfg.b_dbg) {
                //
            }
            //
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            //
            GLES20.glUseProgram(mProgram);

            //
            GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMMatrix, 0);
            GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, mVMatrix, 0);
            GLES20.glUniformMatrix4fv(pMatrixHandle, 1, false, mPMatrix, 0);

            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 12, mVertexBuffer);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

            boolean bShow_peer = true;

            //  2026/09/13: bDx 时 me 全屏当背景先画、peer 左下 1/4 后画压上面
            //  （无深度测试，纯画家算法）——直连路径 tf_peer 即为 1/4 布局
            if  (  m_bDx)  {
                bShow_peer = false;
            }

            if (bShow_peer) {
                drawTexture(mainActivity.textureId, this.tf_peer.mVertexBuffer, this.tf_peer.mTexVertexBuffer);
            }

            boolean bShow_me = m_var.av.taskInfo.bTaskExists && m_var.av.taskInfo.bShow_localVideo;
            if (bShow_me) {
                if (pProcInfo.cfg.ucb_USE_Fragment_dlgTalk_av) {
                    //  2026/09/13: bDx（单向）时 me 占全窗口——改用 tf_me 的顶点缓冲
                    //  （其 capRect 已在 handleSharedObjRender 中设为全窗口），
                    //  不再用硬编码小窗顶点 mVertexBuffer_me。
                    if (m_bDx) {
                        drawTexture(m_var.av.ucbGot_iIndex_sharedObj_localAv ? getSharedTexture(pProcInfo, m_var.av.iIndex_sharedObj_localAv) : 0, this.tf_me.mVertexBuffer, this.tf_me.mTexVertexBuffer);
                    } else {
                        drawTexture(m_var.av.ucbGot_iIndex_sharedObj_localAv ? getSharedTexture(pProcInfo, m_var.av.iIndex_sharedObj_localAv) : 0, mVertexBuffer_me, null);
                    }
                } else {
                    //
                    if  (  false  )  {
                        int me_texId  =  m_var.av.ucbGot_iIndex_sharedObj_localAv ? getSharedTexture(pProcInfo, m_var.av.iIndex_sharedObj_localAv) : 0;
                        Log.d("qy","talkDraw.draw: me_texId "  +  me_texId);
                    }
                    //
                    drawTexture(m_var.av.ucbGot_iIndex_sharedObj_localAv ? getSharedTexture(pProcInfo, m_var.av.iIndex_sharedObj_localAv) : 0, this.tf_me.mVertexBuffer, this.tf_me.mTexVertexBuffer);
                }
            }

            //  2026/09/13: bDx 时 peer 显示区域缩到 0——不画（与 off2on 路径一致，
            //  恢复 1/4 画中画把 bShow_peer_pip 改回 true）。
            final boolean bShow_peer_pip = false;
            if (m_bDx && bShow_peer_pip) {
                drawTexture(mainActivity.textureId, this.tf_peer.mVertexBuffer, this.tf_peer.mTexVertexBuffer);
            }

            //  2026/09/13: 在 me 画面上叠加动态文字（与 draw_meOnly 一致，
            //  直连路径 bUse_off2on=false 时也要显示）
            if (bShow_me) {
                drawMeOverlayText(null);
            }

            updateFpsCounter();

            return;
        }


        //
        public void draw_off() {
            boolean bDbg = false;
            String str;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if(pQyMc == null) return;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
            if (mainActivity == null) {
                Log.e("mainActivity == null");
                return;
            }
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (pProcInfo == null) return;

            //Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) this.mOwner.getActivity();
            //Activity_dlgTalk dlgTalk = dlgTalk_av.mOwner_hTalk.get();
            dlgTalkProc.DLG_TALK_var m_var = this.talker_m_var;//dlgTalk.m_var
            if  (  m_var==null) {// ;
                return;
            }

            //
            if (pProcInfo.cfg.ucb_USE_specialDbg) {
                return;
            }

            myPreRender_off(null);

            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            //
            if  (  pProcInfo.cfg.b_dbg) {
                //
            }
            //
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            //
            GLES20.glUseProgram(mProgram);

            //
            GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMMatrix, 0);
            GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, mVMatrix, 0);
            GLES20.glUniformMatrix4fv(pMatrixHandle, 1, false, mPMatrix, 0);

            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 12, mVertexBuffer);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

            boolean bShow_peer = true;

            if (bShow_peer) {
                drawTexture(mainActivity.textureId, this.tf_peer.mVertexBuffer, this.tf_peer.mTexVertexBuffer);
            }

            //
            updateFpsCounter();

            return;
        }




        //
        public void draw_off2on(int texId_off, int off_w,int off_h) {
            boolean bDbg = false;
            String str;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if(pQyMc == null) return;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
            if (mainActivity == null) {
                Log.e("mainActivity == null");
                return;
            }
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (pProcInfo == null) return;

            //Activity_dlgTalk_av dlgTalk_av = (Activity_dlgTalk_av) this.mOwner.getActivity();
            //Activity_dlgTalk dlgTalk = dlgTalk_av.mOwner_hTalk.get();
            dlgTalkProc.DLG_TALK_var m_var = this.talker_m_var;//dlgTalk.m_var;
            if  (  m_var==null)  return;

            if (pProcInfo.cfg.ucb_USE_specialDbg) {
                return;
            }

            if (texId_off != dbg_o2n_tex || off_w != dbg_o2n_w || off_h != dbg_o2n_h || m_bDx != dbg_o2n_bdx
                    || (++dbg_o2n_cnt >= 300)) {
                dbg_o2n_cnt = 0;
                dbg_o2n_tex = texId_off; dbg_o2n_w = off_w; dbg_o2n_h = off_h; dbg_o2n_bdx = m_bDx;
                Log.d("atdx", String.format("draw_off2on: texId_off=%d %dx%d bDx=%b", texId_off, off_w, off_h, m_bDx));
            }


            //
            myPreRender_off2on(null,off_w,off_h);

            //
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            //
            if  (  pProcInfo.cfg.b_dbg) {
                //
            }
            //
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            //
            GLES20.glUseProgram(mProgram);

            //
            GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMMatrix, 0);
            GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, mVMatrix, 0);
            GLES20.glUniformMatrix4fv(pMatrixHandle, 1, false, mPMatrix, 0);

            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 12, mVertexBuffer);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

            boolean bShow_peer = true;

            //  2026/09/13: bDx 时 me 全屏、peer 左下 1/4——me 必须先画当背景，
            //  peer 改由 draw_meOnly 后画压在上面（无深度测试，纯画家算法）。
            //  这里只暂存 FBO 纹理名，不画 peer。
            if  (  m_bDx)  {
                m_texId_off_last = texId_off;
                bShow_peer = false;
            }

            if (bShow_peer) {
                drawTexture(texId_off, this.tf_peer.mVertexBuffer, this.tf_peer.mTexVertexBuffer);
            }


            return;
        }

        //
        public void draw_meOnly() {
            boolean bDbg = false;
            String str;

            ctxQyMc.CCtxQyMc pQyMc = qyMcMain_global.g.g_pQyMc;
            if(pQyMc == null) return;
            MainActivity mainActivity = (MainActivity) pQyMc.gui.hMainWnd;
            if (mainActivity == null) {
                Log.e("mainActivity == null");
                return;
            }
            ctxQmc.CCtxQmc pProcInfo = pQyMc.get_pProcInfo();
            if (pProcInfo == null) return;

            dlgTalkProc.DLG_TALK_var m_var = this.talker_m_var;//dlgTalk.m_var;
            if  (  m_var==null)  {
                return;
            }

            if (pProcInfo.cfg.ucb_USE_specialDbg) {
                return;
            }

            myPreRender_meOnly(null);

            //
            if  (  false  ) {
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                //
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            }

            //
            GLES20.glUseProgram(mProgram);

            //
            GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMMatrix, 0);
            GLES20.glUniformMatrix4fv(vMatrixHandle, 1, false, mVMatrix, 0);
            GLES20.glUniformMatrix4fv(pMatrixHandle, 1, false, mPMatrix, 0);

            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 12, mVertexBuffer);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);


            boolean bShow_me = m_var.av.taskInfo.bTaskExists && m_var.av.taskInfo.bShow_localVideo;
            if (bShow_me) {
                if (pProcInfo.cfg.ucb_USE_Fragment_dlgTalk_av) {
                    //  2026/09/13: bDx（单向）时 me 占全窗口——改用 tf_me 的顶点缓冲
                    //  （其 capRect 已在 handleSharedObjRender 中设为全窗口），
                    //  不再用硬编码小窗顶点 mVertexBuffer_me。
                    if (m_bDx) {
                        drawTexture(m_var.av.ucbGot_iIndex_sharedObj_localAv ? getSharedTexture(pProcInfo, m_var.av.iIndex_sharedObj_localAv) : 0, this.tf_me.mVertexBuffer, this.tf_me.mTexVertexBuffer);
                    } else {
                        drawTexture(m_var.av.ucbGot_iIndex_sharedObj_localAv ? getSharedTexture(pProcInfo, m_var.av.iIndex_sharedObj_localAv) : 0, mVertexBuffer_me, null);
                    }
                } else {
                    //
                    if  (  false  )  {
                        int me_texId  =  m_var.av.ucbGot_iIndex_sharedObj_localAv ? getSharedTexture(pProcInfo, m_var.av.iIndex_sharedObj_localAv) : 0;
                        Log.d("qy","talkDraw.draw: me_texId "  +  me_texId);
                    }
                    //
                    drawTexture(m_var.av.ucbGot_iIndex_sharedObj_localAv ? getSharedTexture(pProcInfo, m_var.av.iIndex_sharedObj_localAv) : 0, this.tf_me.mVertexBuffer, this.tf_me.mTexVertexBuffer);
                }
            }

            //  2026/09/13: bDx 时 peer 显示区域缩到 0——不画 peer（me 全屏铺满）。
            //  bDx=true 期间 MCU 停推流、解码器销毁，FBO 内容本就是无效纹理（黑），
            //  画不画都不可见，干脆不画。
            //  如需恢复"peer 左下角 1/4 画中画"，把 bShow_peer_pip 改回 true 即可
            //  （布局矩形 tf_peer_pip 与 FBO 纹理暂存逻辑均保留）。
            final boolean bShow_peer_pip = false;
            if (m_bDx && bShow_peer_pip && m_texId_off_last != 0 && this.tf_peer_pip != null) {
                drawTexture(m_texId_off_last, this.tf_peer_pip.mVertexBuffer, this.tf_peer_pip.mTexVertexBuffer);
            }

            //
            //  2026/09/13: 在 me 画面上叠加动态文字（s_meOverlayText，
            //  任何线程实时改，文字变化才重建纹理，空串隐藏）
            if (bShow_me) {
                drawMeOverlayText(null);
            }

            //
            return;
        }



        //
        //  2026/09/13: me 动态文字——文字内容变化时用 Canvas 生成 ARGB bitmap，
        //  经 GLUtils.texImage2D 上传为纹理（白字黑描边，任意背景可读）。
        //  仅在 GL 线程调用（draw_meOnly / exit）。
        private void updateMeOverlayTexture(String strText) {
            if (strText == null) strText = "";
            if (strText.equals(m_overlayTexText)) return;

            //  内容变了：先删旧纹理
            if (m_overlayTexId != 0) {
                GLES20.glDeleteTextures(1, new int[]{m_overlayTexId}, 0);
                m_overlayTexId = 0;
            }
            m_overlayTexText = strText;
            if (strText.length() == 0) return;

            //  测量并生成 bitmap（白字黑描边）
            float fFontSize = 48f;
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setTextSize(fFontSize);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            int iW = (int) (paint.measureText(strText) + fFontSize * 0.8f);
            int iH = (int) (fFontSize * 1.6f);
            if (iW <= 0 || iH <= 0) return;

            Bitmap bmp = Bitmap.createBitmap(iW, iH, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            paint.setTextAlign(Paint.Align.CENTER);
            float cx = iW / 2f;
            float cy = iH / 2f - (paint.descent() + paint.ascent()) / 2f;
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(fFontSize / 7f);
            paint.setColor(Color.BLACK);
            canvas.drawText(strText, cx, cy, paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            canvas.drawText(strText, cx, cy, paint);

            //  上传纹理
            int[] tex = new int[1];
            GLES20.glGenTextures(1, tex, 0);
            if (tex[0] == 0) {
                bmp.recycle();
                return;
            }
            m_overlayTexId = tex[0];
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, m_overlayTexId);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

            m_overlayTexW = iW;
            m_overlayTexH = iH;
            bmp.recycle();
        }

        //
        //  2026/09/13: 在 me 画面（当前主画面）左上角绘制动态文字。
        //  bDx=true 时 me 全屏，文字正好落在 me 上；布局用 tf_me_text
        //  （与 me/peer 同一套 mMats 投影管线），矩形按纹理宽高比适配。
        private void drawMeOverlayText(Object pCtx) {
            updateMeOverlayTexture(s_meOverlayText);
            if (m_overlayTexId == 0 || this.tf_me_text == null) return;

            int width = this.mEglSurface.getWidth();
            int height = this.mEglSurface.getHeight();
            if (height == 0 || width == 0) return;
            if (m_overlayTexW <= 0 || m_overlayTexH <= 0) return;

            //  左上角显示：宽占屏 40%，按文字纹理宽高比缩放，边距 16px
            float fDispW = width * 0.4f;
            float fDispH = fDispW * m_overlayTexH / (float) m_overlayTexW;
            int iMargin = 16;

            qyDefs_open.CAP_RECT capRect = new qyDefs_open.CAP_RECT();
            capRect.iX = iMargin;
            capRect.iY = iMargin;
            capRect.iW = (int) fDispW;
            capRect.iH = (int) fDispH;
            float fAspectRatio_img = m_overlayTexW / (float) m_overlayTexH;

            if (needsUpdate(this.tf_me_text.capRect, capRect, fAspectRatio_img, this.tf_me_text.fAspectRatio_img)) {
                qyTimestamp.QY_timestamp ts = new qyTimestamp.QY_timestamp();
                boolean ucbAutoClip = false;
                float text_z_1 = CONST_fz_me;

                if (0 != this.tf_me_text.T1_frm_set(pCtx, ts, capRect, fAspectRatio_img, ucbAutoClip, false, mMats, text_z_1, "", false, false)) {
                    return;
                }
            }

            //
            //  2026/09/13: 文字纹理背景是透明的，必须开混合，否则透明区域画成黑块。
            //  画完立即还原，不影响 me/peer 的绘制路径。
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            drawTexture(m_overlayTexId, this.tf_me_text.mVertexBuffer, this.tf_me_text.mTexVertexBuffer);
            GLES20.glDisable(GLES20.GL_BLEND);
        }



        //
        private void drawTexture(int textureId, Buffer vertexBuffer, Buffer texVertexBuffer) {
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);
            if (texVertexBuffer != null) {
                GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texVertexBuffer);
            }

            if (textureId != 0) {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            } else {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexName_default);
            }

            GLES20.glUniform1i(mTexSmaplerHandle, 0);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length, GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);
        }

        public int getSharedTexture(ctxQmc.CCtxQmc pProcInfo, int index) {
            qmcSharedObj.QY_SHARED_OBJ pSharedObj = qmcSharedObj.qyFuncs.getSharedObjByIndex(pProcInfo, index);
            return (pSharedObj != null) ? pSharedObj.var.m_o_texId : 0;
        }

        private void updateFpsCounter() {
            this.m_loop_ctrl++;
            long curTickCnt = myGetTickCount(null);
            int iElapseInMs = Math.abs((int) (this.m_lastFps_tickCnt - curTickCnt));
            if (iElapseInMs >= 1000) {
                this.m_lastFps_loop_ctrl = m_loop_ctrl;
                this.m_lastFps_tickCnt = curTickCnt;
            }
        }

    }
}
