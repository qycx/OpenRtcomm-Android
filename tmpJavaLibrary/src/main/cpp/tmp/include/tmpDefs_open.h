

#ifndef  __tmpDefs_open_h__
#define  __tmpDefs_open_h__		//  {

#ifndef mymin
#define mymin(x,y)	((x)<(y)?(x):(y))
#endif
#ifndef mymax
#define mymax(x,y)	((x)>(y)?(x):(y))
#endif

//
#define mymin(x,y)	((x)<(y)?(x):(y))




//
#ifndef  mycountof		//  2005/11/16, 
#define mycountof(array) (sizeof(array)/sizeof(array[0]))
#endif




#endif  //  }


