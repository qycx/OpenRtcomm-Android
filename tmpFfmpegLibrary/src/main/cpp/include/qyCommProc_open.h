//
// Created by qycx on 2021/1/31.
//

#ifndef TMPFFMPEG_QYCOMMPROC_OPEN_H
#define TMPFFMPEG_QYCOMMPROC_OPEN_H

//
//
#define     CONST_audioCompressors_melp             23     //




//
#define MAKEFOURCC(ch0, ch1, ch2, ch3)                              \
                ((DWORD)(BYTE)(ch0) | ((DWORD)(BYTE)(ch1) << 8) |   \
                ((DWORD)(BYTE)(ch2) << 16) | ((DWORD)(BYTE)(ch3) << 24 ))



//
#define		CONST_fourcc_YV12       			MAKEFOURCC(  'Y',  'V',  '1',  '2'  )		//  2010/07/11
#define		CONST_fourcc_I420		        	MAKEFOURCC(  'I',  '4',  '2',  '0'  )		//  2010/07/11

//
#define		CONST_fourcc_h264	        		MAKEFOURCC(  'h',  '2',  '6',  '4'  )		//  2014/02/24
//
#define		CONST_fourcc_AVC			        MAKEFOURCC('A','V','C',' ')					//  2017/10/08
#define		CONST_fourcc_HEVC			        MAKEFOURCC('H','E','V','C')					//  2017/10/08

//
#define     WAVE_FORMAT_MPEGLAYER3              0x0055 /* ISO/MPEG Layer3 Format Tag */


//
#define     WAVE_FORMAT_MELP                    0x0100  +  1      //  makeFOURCC('M','E',‘L’，‘P')     //  这个可能不是标准定义




//
#endif //TMPFFMPEG_QYCOMMPROC_OPEN_H
