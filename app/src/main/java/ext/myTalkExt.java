package ext;

import static com.qycx.base.dlgTalkProc.qyFuncs_Droid.dlgTalk_bConfCompere;
import static com.qycx.base.isTalkerQProc.findIsTalker;
import static com.qycx.base.qyMcMain_global.g.g_pQyMc;

import com.qycx.anc.TalkExtTmpl;
import com.qycx.base.CIsTalker;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.qmCommFunc_mis;

public class myTalkExt extends TalkExtTmpl {


    @Override
    public int switchTransmissionMode(long talkerId, byte  bNoVDownloadVal)
    {
        ctxQyMc.CCtxQyMc pQyMc =  g_pQyMc;
        ctxQmc.CCtxQmc pProcInfo=pQyMc.get_pProcInfo();
        qmCommFunc_mis.MIS_CNT pMisCnt=pProcInfo.pMisCnt;
        if  (  pMisCnt==null)  return  -1;

        CIsTalker talker = findIsTalker(pMisCnt,talkerId);
        if  (  talker ==  null  )  return  -1;

        if  (  !talker.m_var.av.taskInfo.bTaskExists)  return  -1;

        //
        m_var.confCtrl.iTaskId=talker.m_var.av.taskInfo.iTaskId;

        //
        if  (  dlgTalk_bConfCompere(talkerId,pMisCnt.idInfo.ui64Id))  {
            m_var.confCtrl.bNoVDownload=0;
        }
        else  {
              m_var.confCtrl.bNoVDownload = bNoVDownloadVal;

        }



        return  0;
    }


    @Override
    public  boolean bNoVDownload(int iTaskId)
    {
        {
            //
            if (m_var.confCtrl.iTaskId != iTaskId)  return  false;

            //
            return m_var.confCtrl.bNoVDownload!=0;
        }
        //return false;
    }


}
