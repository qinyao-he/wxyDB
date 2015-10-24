cmake_minimum_required(VERSION 3.3)
project(wxyDB)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

aux_source_directory(src/filesystem FILESYSTEM_FILES)
aux_source_directory(src/framework FRAMEWORK_FILES)

add_executable(wxyDB ${FRAMEWORK_FILES})
add_executable(testfilesystem ${FILESYSTEM_FILES})
