package com.qycx.base;


import static com.qycx.base.qyMcMain_global.g.g_pQyMc;

import android.util.Log;

import java.util.concurrent.Semaphore;

//
public class qmObjQ {


    //
    class 	QM_OBJQ_MEM						{
        int									iType;
        //void								*	pQMemObj;				//  节点包含指向详细信息的指针

        //
        public int init() {
            return  0;
        }

        //
        public int exit(){
            return  0;
        }
    }		 ;



    //  暂时这个类没用处
    //
    public static class QM_OBJQ {
        int iType;

        String name;

        //
        int maxCnt;                    //  最多容纳个数；这同时意味着分配的空间大小=maxCnt  *  sizeof(  QM_OBJQ_MEMU  )

        int cnt;                    //  有效个数

        //
        QM_OBJQ_MEM						[]pMems;                    //  是个数组


        //  注：这里应该用读写锁。暂时先用mutex. 2026/05/03
        //CMutexRW							*p
        public Semaphore hMutex_syncQmObjQ = new Semaphore(1);



        //
        public int initQmObjQ(int iObjQType, String qName, String mutexName, int iMaxCnt) {

            this.maxCnt=iMaxCnt;
            this.cnt=0;
            this.pMems=new QM_OBJQ_MEM[this.maxCnt];

            return 0;
        }

        //
        public void exitQmObjQ() {
            int i;
            for  (  i = 0;  i  <  this.maxCnt;i++  )  {
                QM_OBJQ_MEM  mem  =  this.pMems[i];
                if ( mem!=null)  {
                    mem.exit();
                    mem=null;
                }
            }
        }







    /*
    __declspec(  dllexport  )  int  printQmObjQ(  QM_OBJQ  *  pQ  );
    int  procQmObjQ_netMc(  void  *  pSubThreadInfoParam,  void  *  pSessionParam  );
    //
    __declspec(  dllexport  )  int  procQmObjQ(  void  *  pServiceInfo,  QM_OBJQ  *  pObjQ,  void  *  pDb,  int  iDbType,  int  iObjType,  void  *  pClient,  QM_OBJQ_MEM  *  pMem  );

    //
    __declspec(  dllexport  )  int  qmObjQTraverse(  void  *  pServiceInfo,  void  *  pQParam,  PF_commonHandler pf_visit,  void  *  p0,  void  *  p1  );


    __declspec(  dllexport  )  int  tmp_findMisQMemById(  void  *  p0,  void  *  pQParam,  void  *  pKeyInfo,  QM_OBJQ_MEM  *  pMemParam  );


     */

    }

}
