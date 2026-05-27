

#ifndef  __ctx_q_showInfo_h__
#define  __ctx_q_showInfo_h__	//  {


//
typedef  int  (*PF_showInfo_open)(  void  *  p0,  LPCTSTR  tWhere,  LPCTSTR  hint  );


//
typedef  struct  __ctx_q_showInfo_t					{
				 PF_showInfo_open					pf_showInfo_open;
}		 CTX_q_showInfo;




#endif  //  }


