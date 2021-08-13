set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -g -Wall")
find_package(PkgConfig)

pkg_check_modules(ffmpeg REQUIRED IMPORTED_TARGET libavutil libavformat)

include_directories(
        ../3part/ffmpeg
        ../3part/cjson
        ../utils
        include
)

file(GLOB SOURCE src/*.cpp ../3part/cjson/*.c ../utils/*.cpp )

add_library(MediaScan STATIC ${SOURCE})

#set(THREADS_PREFER_PTHREAD_FLAG ON)
#find_package(Threads REQUIRED)

target_link_libraries(MediaScan PRIVATE PkgConfig::ffmpeg)