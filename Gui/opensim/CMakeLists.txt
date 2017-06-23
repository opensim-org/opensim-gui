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


find_package(Ant REQUIRED)

add_custom_target(CopyOpenSimCore ALL
                  COMMAND ${Ant_EXECUTABLE} copy-java-bindings -DapiJava.dir="${OpenSim_JAVA_FILES_DIR}"
                  DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/build.xml"
                  WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
                  COMMENT "Copy opensim-core Java Bindings to GUI.")
if (WIN32)
    add_custom_target(PrepareInstaller ALL
                  COMMAND ${Ant_EXECUTABLE} copy-installer-files -Dapi.dir="${OpenSim_ROOT_DIR}"
                  DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/build.xml"
                  WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
                  COMMENT "Merge GUI build and opensim-core install into one folder.")

    add_custom_target(CopyJRE ALL
                  COMMAND ${Ant_EXECUTABLE} copy-jre
                  DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/build.xml"
                  WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
                  COMMENT "Copy default JRE to installer folder.")
endif()
if (APPLE)
    add_custom_target(PrepareInstaller ALL
                  COMMAND ${Ant_EXECUTABLE} build-mac-installer -Dlib.dir="${OpenSim_LIB_DIR}"
                  DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/build.xml"
                  WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
                  COMMENT "Build opensim.app.")
    # On macOS, copying the JRE is handled by build-mac-installer.
endif()

set(VIS_REPO "" CACHE
    PATH "The location of the opensim-threejs repository to copy into installation.")

if (VIS_REPO)
add_custom_target(CopyVisualizer ALL
                  COMMAND ${Ant_EXECUTABLE} copy-visualizer -Dvisualizer.dir="${VIS_REPO}"
                  DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/build.xml"
                  WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
                  COMMENT "Copy Visualizer to installer folder.")
endif()

set(MODELS_REPO "" CACHE
    PATH "The location of the Models repository to copy into installation.")

if (MODELS_REPO)
add_custom_target(CopyModels ALL
                  COMMAND ${Ant_EXECUTABLE} copy-models -Dmodels.dir="${MODELS_REPO}"
                  DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/build.xml"
                  WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
                  COMMENT "Copy models and examples-to installer folder.")
endif()
