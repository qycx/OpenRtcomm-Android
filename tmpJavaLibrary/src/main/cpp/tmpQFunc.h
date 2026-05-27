//
// Created by qycx1 on 2024/12/31.
//

#ifndef OPENRTCOMM_TMPQFUNC_H
#define OPENRTCOMM_TMPQFUNC_H

//
#include    "tmpQMgr.h"

//
int newTmpQMemIndex(int playerIndex);
int freeTmpQMemIndex(int index);
TmpQMem *  getTmpQMem(void  *  p0,  int index );

//




#endif //OPENRTCOMM_TMPQFUNC_H
