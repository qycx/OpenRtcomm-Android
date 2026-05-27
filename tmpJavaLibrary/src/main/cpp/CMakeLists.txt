# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)
project(tmpJni-lib)
# Declares and names the project.

#
add_definitions(-D__LINUX__=1  -DUNICODE=1 -D__isCli__=1  -D__arm__ )


#
set(JNI_LIBS_DIR ${CMAKE_SOURCE_DIR}/../jniLibs)
set(TMP_LIBS_DIR ${JNI_LIBS_DIR}/${ANDROID_ABI}/lib)


include_directories( ${CMAKE_SOURCE_DIR}/tmp/include/
                        ${JNI_LIBS_DIR}/include/
                    )

#
add_library(mymixer SHARED IMPORTED)
set_target_properties(mymixer PROPERTIES IMPORTED_LOCATION    ${JNI_LIBS_DIR}/${ANDROID_ABI}/lib/libmymixer.so)

add_library(myaecm SHARED IMPORTED)
set_target_properties(myaecm PROPERTIES IMPORTED_LOCATION    ${JNI_LIBS_DIR}/${ANDROID_ABI}/lib/libmyaecm.so)

add_library(myns SHARED IMPORTED)
set_target_properties(myns PROPERTIES IMPORTED_LOCATION    ${JNI_LIBS_DIR}/${ANDROID_ABI}/lib/libmyns.so)

#add_library(dlthwebrtc SHARED IMPORTED)
#set_target_properties(dlthwebrtc PROPERTIES IMPORTED_LOCATION    ${JNI_LIBS_DIR}/${ANDROID_ABI}/lib/libwebrtc_audio_core.so)

add_library(myap1 SHARED IMPORTED)
set_target_properties(myap1 PROPERTIES IMPORTED_LOCATION    ${JNI_LIBS_DIR}/${ANDROID_ABI}/lib/libmyap1.so)



# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             tmpJni-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             tmpJni-lib.cpp
             tmpJniFunc.cpp
            yv12rgb.cpp
            rotate.cpp
            tmpQFunc.cpp
            tmpQMgr.cpp

            #
            ./tmp/public/qyMemoryFunc.cpp
            ./tmp/public/genericqueue.cpp
            ./tmp/public/qy_myGetTickCount.cpp
            ./tmp/public/qySyncObj.cpp
            ./tmp/public/tmpAecProc.cpp
            ./tmp/public/tmpMixProc.cpp
            ./tmp/public/tmpApProc.cpp
            ./tmp/public/fake_tmpFunc.cpp

            #
            tmpJava_tmpFunc.cpp


)

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
                       tmpJni-lib
                       mymixer
                       myaecm
                       myns
                       #dlthwebrtc
                       myap1

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )
