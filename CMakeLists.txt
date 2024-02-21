# Allow specifying VERSION for the project.
cmake_policy(SET CMP0048 NEW)

project(OpenSimApp VERSION 4.6)

cmake_minimum_required(VERSION 3.1.3)

set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} "${CMAKE_SOURCE_DIR}/cmake")

add_subdirectory(Gui/opensim)
