package com.qycx.base;

import static com.qycx.base.qyMcMain_global.g.g_pQyMc;
import static com.qycx.base.tmpFunc.qyFuncs.myGetCurrentThreadId;
import static com.qycx.base.tmpFunc.qyFuncs.myGetTickCount;

import com.qycx.base.util.Log;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;

public class isTalkerQProc {

    //
    //  注意：isTalkerQ只能在界面线程被访问. 所以是顺序访问的


    //
    public static CIsTalker findIsTalker(qmCommFunc_mis.MIS_CNT pMisCntParam, long ui64Id) {
        ctxQyMc.CCtxQyMc  pQyMc  =  g_pQyMc;
        //
        if (  pQyMc.appParams.bDbg) {
            int cur_threadId = myGetCurrentThreadId();
            if (cur_threadId != pQyMc.gui.dwThreadId) {
                return null;
            }
        }

        //
        if  (  false  ) {
            //
            if (pMisCntParam.isTalkerQ == null || pMisCntParam.isTalkerQ.isEmpty()) return null;
            Optional<qmCommFunc_mis.IsTalkerQMem> obj = pMisCntParam.isTalkerQ.stream().filter(x -> x.m_talker.m_var.addr.idInfo.ui64Id == ui64Id).findFirst();
            if (obj.orElse(null) != null)
                return Objects.requireNonNull(obj.orElse(null)).m_talker;
            else
                return null;
        }

        //
        qmCommFunc_mis.IsTalkerQMem obj=findIsTalkerQMem(pMisCntParam,ui64Id);
        if  (  obj==null)  return  null;
        return obj.m_talker;
    }


    //
    public static qmCommFunc_mis.IsTalkerQMem findIsTalkerQMem(qmCommFunc_mis.MIS_CNT  pMisCntParam, long ui64Id) {
        ctxQyMc.CCtxQyMc pQyMc = g_pQyMc;
        //
        if (pQyMc.appParams.bDbg) {
            int cur_threadId = myGetCurrentThreadId();
            if (cur_threadId != pQyMc.gui.dwThreadId) {
                return null;
            }
        }

        //
        if (pMisCntParam.isTalkerQ == null || pMisCntParam.isTalkerQ.isEmpty()) return null;
        Optional<qmCommFunc_mis.IsTalkerQMem> obj = pMisCntParam.isTalkerQ.stream().filter(x -> x.m_talker.m_var.addr.idInfo.ui64Id == ui64Id).findFirst();
        if (obj.orElse(null) != null)
            return Objects.requireNonNull(obj.orElse(null));
        //
        return null;
    }




    //
    //  在加入队列前，应该初始化完毕。
    //  在移除队列时，应该调用exit()来清理.

    //
    public static int addToIsTalkerQ(qmCommFunc_mis.MIS_CNT  pMisCnt, CIsTalker talker)
    {
        ctxQyMc.CCtxQyMc  pQyMc  =  g_pQyMc;
        //
        if (  pQyMc.appParams.bDbg) {
            int cur_threadId = myGetCurrentThreadId();
            if (cur_threadId != pQyMc.gui.dwThreadId) {
                return -1;
            }
        }

        //
        CIsTalker  obj;
        obj =  findIsTalker(pMisCnt,talker.m_var.addr.idInfo.ui64Id);
        if  (  obj!=null)  {
            Log.d("at","addToIsTalkerQ ok, already in q");
            return 0;
        }
        //
        qmCommFunc_mis.IsTalkerQMem qMem = new qmCommFunc_mis.IsTalkerQMem();
        qMem.m_talker=talker;
        qMem.tickCnt_created=myGetTickCount(null);

        //
        pMisCnt.isTalkerQ.add(qMem);

        //
        Log.d("at","addToIsTalkerQ ok, newly added");

        //
        return  0;
    }


    //
    public static int removeFromIsTalkerQ(qmCommFunc_mis.MIS_CNT  pMisCnt, long ui64Id)
    {
        ctxQyMc.CCtxQyMc  pQyMc  =  g_pQyMc;
        //
        if (  pQyMc.appParams.bDbg) {
            int cur_threadId = myGetCurrentThreadId();
            if (cur_threadId != pQyMc.gui.dwThreadId) {
                return -1;
            }
        }

        //
        Iterator<qmCommFunc_mis.IsTalkerQMem> it = pMisCnt.isTalkerQ.iterator();

        while (it.hasNext()) {
            qmCommFunc_mis.IsTalkerQMem s = it.next();

            if (s.m_talker.m_var.addr.idInfo.ui64Id==  ui64Id) {
                it.remove();
                //
                s.m_talker.exit();
            }
        }


        //
        return  0;
    }

    //
    public static int isTalkerQ_doClean(qmCommFunc_mis.MIS_CNT pMisCnt)
    {
        //
        Log.d("at","isTalkerQ.doClean enters");

        ctxQyMc.CCtxQyMc  pQyMc  =  g_pQyMc;
        //
        if (  pQyMc.appParams.bDbg) {
            int cur_threadId = myGetCurrentThreadId();
            if (cur_threadId != pQyMc.gui.dwThreadId) {
                return -1;
            }
        }

        //
        Log.d("at","isTalkerQ.size "  +  pMisCnt.isTalkerQ.size());

        //
        Iterator<qmCommFunc_mis.IsTalkerQMem> it = pMisCnt.isTalkerQ.iterator();

        while (it.hasNext()) {
            qmCommFunc_mis.IsTalkerQMem s = it.next();

            {
                it.remove();
                //
                s.m_talker.exit();
            }
        }

        //
        Log.d("at","isTalkerQ.size "  +  pMisCnt.isTalkerQ.size());

        //
        Log.d("at","isTalkerQ.doClean leaves");

        //
        return  0;
    }




}
