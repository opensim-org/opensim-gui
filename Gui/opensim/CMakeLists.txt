#
# Build of OpenSim application. There are 4 steps:
#    (1) Locate OpenSim API install (including Java Bindings)
#    (2) Copy java files (or jar file) to proper location under opensim-gui
#    (3) Build OpenSim application using ant build.xml, include"platform specific" 
#            application launcher and JRE 
#    (4) Merge/install API install from (1) with launcher from (3) to make final installer 
#----------------------------------------------------

find_package(OpenSim REQUIRED)

if(NOT OpenSim_JAVA_WRAPPING)
    message(FATAL_ERROR "OpenSim core needs be compiled with Java wrapping ON.")
endif()

message(STATUS "Replacing exising Java files with ones from opensim-core.")
#file(REMOVE_RECURSE "${CMAKE_CURRENT_SOURCE_DIR}/modeling/src/org")
#file(COPY ${OpenSim_JAVA_FILES_DIR}
#    DESTINATION "${CMAKE_CURRENT_SOURCE_DIR}/modeling/src"
#    FILES_MATCHING PATTERN "*.java")

find_package(Ant REQUIRED)

get_filename_component(PARENT_DIR ${OpenSim_DIR} DIRECTORY)

MESSAGE(STATUS, ${PARENT_DIR})
add_custom_target(CopyOpenSimCore ALL
                  COMMAND ${Ant_EXECUTABLE} copy-java-bindings -Dapi.dir="${PARENT_DIR}"
                  DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/build.xml"
                  WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
                  COMMENT "Copy opensim-core Java Bindings to GUI.")

add_custom_target(PrepareInstaller ALL
                  COMMAND ${Ant_EXECUTABLE} copy-installer-files -Dapi.dir="${PARENT_DIR}"
                  DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/build.xml"
                  WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
                  COMMENT "Merge GUI build and opensim-core install into one folder.")
