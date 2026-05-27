//
// Created by qycx on 2021/1/30.
//

#include "qdc.h"



#define  MAX_cntof_qdcUnits     100




CQdc::CQdc() {
    m_pMems=NULL;
    m_usMaxCnt=MAX_cntof_qdcUnits;
    //
    init();
}
CQdc::~CQdc()
{
    exit();
}


int CQdc::init()
{
    int iErr=-1;
    int size=sizeof(QDC_unit) * m_usMaxCnt;
    m_pMems=(QDC_unit *)mymalloc(size);
    if ( m_pMems==NULL)goto errLabel;
    memset(m_pMems,0,size);

    iErr = 0;
    errLabel:
    if ( iErr!=0){
        exit();
    }
    return iErr;
}

void CQdc::exit() {
    int  i;

    //
    if ( m_pMems!=NULL) {
        for ( i=0;i<m_usMaxCnt;i++){
            if ( this->m_pMems[i].bUsed) {
                printf("error\n");
            }
        }
        M_myfree(m_pMems);
    }
    m_usMaxCnt=0;

    return;
}

//
CQdc g_qdc;

