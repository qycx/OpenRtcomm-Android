# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.

project("tmpFfmpeg")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_definitions(-D__LINUX__=1  -DUNICODE=1 -D__isCli__=1  -D__arm__ )

set(JNI_LIBS_DIR                ${CMAKE_SOURCE_DIR}/../jniLibs)
set(TMP_LIBS_DIR                ${JNI_LIBS_DIR}/${ANDROID_ABI}/lib)
set(TmpJavaLibraryDir_tmp       ${CMAKE_SOURCE_DIR}/../../../../tmpJavaLibrary/src/main/cpp/tmp)


add_library( # Sets the name of the library.
             tmpFfmpeg-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             native-lib.cpp

            public/encVFunc.cpp
            public/decVFunc.cpp
            public/encAFunc.cpp
            public/decAFunc.cpp
            public/mallocFunc.cpp
            public/qdc.cpp
            public/qdcFunc.cpp
            public/testFunc.cpp
            public/testEncV.cpp
            public/testEncV_main1.cpp
            public/testDecV1.cpp
            public/testDecV_main1.cpp
            public/testDumpAudio.cpp
            public/testEncA.cpp
            public/testEncA_main.cpp
            public/testDecA.cpp
            public/testDecA_main.cpp
            public/parseSpsPps.cpp

        #
        ${TmpJavaLibraryDir_tmp}/public/qySyncObj.cpp
        ${TmpJavaLibraryDir_tmp}/public/fake_tmpFunc.cpp
        ${TmpJavaLibraryDir_tmp}/public/qy_myGetTickCount.cpp
        ${TmpJavaLibraryDir_tmp}/public/genericqueue.cpp
        ${TmpJavaLibraryDir_tmp}/public/qyMemoryFunc.cpp


        #
        ./melp/public/testMelp.cpp

        ./melp/public/encA_melp.cpp
        ./melp/public/decA_melp.cpp
        ./melp/public/mysc24enc.c
        ./melp/public/mysc24dec.c
        ./melp/public/tmpFunc_melp.cpp


        #
        #./melp/MELPe_fxp-master/sc24enc.c
        #./melp/MELPe_fxp-master/sc24dec.c

        ./melp/MELPe_fxp-master/classify.c
        ./melp/MELPe_fxp-master/coeff.c
        ./melp/MELPe_fxp-master/dsp_sub.c
        ./melp/MELPe_fxp-master/fec_code.c
        ./melp/MELPe_fxp-master/fft_lib.c
        ./melp/MELPe_fxp-master/fs_lib.c
        ./melp/MELPe_fxp-master/fsvq_cb.c
        ./melp/MELPe_fxp-master/global.c
        ./melp/MELPe_fxp-master/harm.c
        ./melp/MELPe_fxp-master/lpc_lib.c
        ./melp/MELPe_fxp-master/mat_lib.c
        ./melp/MELPe_fxp-master/math_lib.c
        ./melp/MELPe_fxp-master/mathdp31.c
        #./melp/MELPe_fxp-master/Win32/mathhalf.c
        ./melp/MELPe_fxp-master/ARM/mathhalf.c
        ./melp/MELPe_fxp-master/melp_ana.c
        ./melp/MELPe_fxp-master/melp_chn.c
        ./melp/MELPe_fxp-master/melp_sub.c
        ./melp/MELPe_fxp-master/melp_syn.c
        ./melp/MELPe_fxp-master/msvq_cb.c
        ./melp/MELPe_fxp-master/npp.c
        ./melp/MELPe_fxp-master/pit_lib.c
        ./melp/MELPe_fxp-master/pitch.c
        ./melp/MELPe_fxp-master/postfilt.c
        ./melp/MELPe_fxp-master/qnt12.c
        ./melp/MELPe_fxp-master/qnt12_cb.c

        #./melp/MELPe_fxp-master/transcode.c

        ./melp/MELPe_fxp-master/vq_lib.c

        #./melp/MELPe_fxp-master/qnt12.c



)


include_directories(${JNI_LIBS_DIR}/${ANDROID_ABI}/include)
include_directories(include
        ${CMAKE_SOURCE_DIR}/melp/MELPe_fxp-master/
        ${CMAKE_SOURCE_DIR}/melp/CMSIS_4-master/CMSIS/Include
        ${CMAKE_SOURCE_DIR}/melp/include
        ${TmpJavaLibraryDir_tmp}/include

)

add_library(avutil
        SHARED
        IMPORTED)
set_target_properties(avutil
        PROPERTIES IMPORTED_LOCATION
        ${TMP_LIBS_DIR}/libavutil.so)

add_library(swresample
        SHARED
        IMPORTED)
set_target_properties(swresample
        PROPERTIES IMPORTED_LOCATION
        ${TMP_LIBS_DIR}/libswresample.so)

add_library(swscale
        SHARED
        IMPORTED)
set_target_properties(swscale
        PROPERTIES IMPORTED_LOCATION
        ${TMP_LIBS_DIR}/libswscale.so)

add_library(avcodec
        SHARED
        IMPORTED)
set_target_properties(avcodec
        PROPERTIES IMPORTED_LOCATION
        ${TMP_LIBS_DIR}/libavcodec.so)

add_library(avformat
        SHARED
        IMPORTED)
set_target_properties(avformat
        PROPERTIES IMPORTED_LOCATION
        ${TMP_LIBS_DIR}/libavformat.so )




# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       tmpFfmpeg-lib
                        avutil swresample swscale avcodec avformat
                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )