# Allow specifying VERSION for the project.
cmake_policy(SET CMP0048 NEW)

project(OpenSimApp VERSION 4.6)

if (NOT APPLE)
	cmake_minimum_required(VERSION 3.15)
endif()
set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} "${CMAKE_SOURCE_DIR}/cmake")

add_subdirectory(Gui/opensim)
