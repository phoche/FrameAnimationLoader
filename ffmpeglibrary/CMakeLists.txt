# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             libs/include/cmdutils.c
             libs/include/ffmpeg.c
             libs/include/ffmpeg_filter.c
             libs/include/ffmpeg_hw.c
             libs/include/ffmpeg_opt.c
             src/main/cpp/native-lib.cpp )

add_library( avcodec
             SHARED
             IMPORTED )
set_target_properties( avcodec
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/libs/armeabi-v7a/libavcodec.so )

add_library( avfilter
             SHARED
             IMPORTED )
set_target_properties( avfilter
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/libs/armeabi-v7a/libavfilter.so )

add_library( avformat
             SHARED
             IMPORTED )
set_target_properties( avformat
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/libs/armeabi-v7a/libavformat.so )

add_library( avutil
             SHARED
             IMPORTED )
set_target_properties( avutil
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/libs/armeabi-v7a/libavutil.so )

add_library( swresample
             SHARED
             IMPORTED )
set_target_properties( swresample
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/libs/armeabi-v7a/libswresample.so )

add_library( swscale
             SHARED
             IMPORTED )
set_target_properties( swscale
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/libs/armeabi-v7a/libswscale.so )


include_directories( libs/include )

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       native-lib

                       avcodec

                       avfilter

                       avformat

                       avutil

                       swresample

                       swscale

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )