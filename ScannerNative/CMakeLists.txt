# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -stdlib=libc++ -std=c++11 -fPIC -w")
cmake_minimum_required(VERSION 3.4.1)


INCLUDE_DIRECTORIES(
        "src/main/cpp/mediascan/include"
        "src/main/cpp/utils"
        "src/main/cpp/3part/cjson"
        "src/main/cpp/3part/ffmpeg"
        "src/main/cpp/3part/ffmpeg/include"
)

file(GLOB native_srcs
        "src/main/cpp/mediascan/src/*.cpp"
        "src/main/cpp/3part/cjson/*.c"
        "src/main/cpp/utils/*.cpp"
        )

add_library(libyuv
        SHARED
        IMPORTED)
set_target_properties(libyuv
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_CURRENT_SOURCE_DIR}/libs/arm64_v8a/libffmpeg.so)

add_library( # Sets the name of the library.
        scanner_jni
        SHARED
        ${native_srcs})


target_link_libraries( # Specifies the target library.
        scanner_jni
        libyuv
        android
        log)
