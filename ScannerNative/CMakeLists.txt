# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)


add_library( # Sets the name of the library.
             scanner_jni
             SHARED
             src/main/cpp/android/native_lib.cpp)


target_link_libraries( # Specifies the target library.
                       scanner_jni
                       android
                       log )