//
// Created by qycx on 2021/1/30.
//

#ifndef TMPFFMPEG_TMPFFMPEGDEFS_H
#define TMPFFMPEG_TMPFFMPEGDEFS_H

//
#include    "myTypes.h"

//
typedef unsigned short ushort;
typedef unsigned char byte;
typedef unsigned char BYTE;
//typedef unsigned int DWORD;
//

//
typedef mybool BOOL;


//
#ifndef  FALSE
#define FALSE false
#endif
#ifndef  TRUE
#define TRUE true
#endif

//
#ifndef max
#define max(a,b)            (((a) > (b)) ? (a) : (b))
#endif

#ifndef min
#define min(a,b)            (((a) < (b)) ? (a) : (b))
#endif

//
void * mymalloc(size_t size);
void myfree(void * p);

//
#define M_myfree(p) if(p){myfree(p);p=NULL;}



#endif //TMPFFMPEG_TMPFFMPEGDEFS_H
