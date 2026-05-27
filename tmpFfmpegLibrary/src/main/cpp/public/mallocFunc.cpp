//
// Created by qycx on 2021/1/29.
//
#include <stdlib.h>
#include "tmpFfmpegCommon.h"

void * mymalloc(size_t size)
{
    return malloc(size);
}

void myfree(void * p)
{
    if(p) {
        free(p);
    }
    return;
}