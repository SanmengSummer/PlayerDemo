set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -g -Wall ")
find_package(PkgConfig)

pkg_check_modules(ffmpeg REQUIRED IMPORTED_TARGET libavutil libavformat)
include_directories(
        ../3part/ffmpeg
        ../3part/cjson
        ../utils
        ../mediascan/include
)

add_executable(example_retriever example_retriever.cpp)
add_executable(example_config example_config.cpp)
add_executable(example_travel example_travel.cpp)
add_executable(example example.cpp)
target_link_libraries(example_retriever PRIVATE PkgConfig::ffmpeg MediaScan)
target_link_libraries(example_config PRIVATE PkgConfig::ffmpeg MediaScan)
target_link_libraries(example_travel PRIVATE PkgConfig::ffmpeg MediaScan)
target_link_libraries(example PRIVATE PkgConfig::ffmpeg MediaScan)