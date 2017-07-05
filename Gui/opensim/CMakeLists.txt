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

if (NOT APPLE)
add_custom_target(CopyVisualizer ALL
                  COMMAND ${Ant_EXECUTABLE} copy-visualizer
                  DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/build.xml"
                  WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
                  COMMENT "Copy Visualizer to installer folder.")
endif()

set(MODELS_REPO "${CMAKE_SOURCE_DIR}/opensim-models" CACHE
    PATH "The location of the Models repository to copy into installation.")

if (MODELS_REPO AND NOT APPLE)
add_custom_target(CopyModels ALL
                  COMMAND ${Ant_EXECUTABLE} copy-models -Dmodels.dir="${MODELS_REPO}"
                  DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/build.xml"
                  WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
                  COMMENT "Copy models and examples-to installer folder.")
endif()

if (APPLE)
    if (OpenSim_FOUND)
        set(PLATFORM_PRIVATE_PROPERTIES_FILE 
            "${CMAKE_CURRENT_SOURCE_DIR}/nbproject/private/platform-private.properties")
        if (NOT EXISTS ${PLATFORM_PRIVATE_PROPERTIES_FILE})
            file(WRITE ${PLATFORM_PRIVATE_PROPERTIES_FILE}
                "run.args.extra=-J-Djava.library.path=${OpenSim_LIB_DIR}")
        endif()
    endif()
    add_custom_target(PrepareInstaller ALL
                  COMMAND ${Ant_EXECUTABLE} build-mac-installer
                  -Dapi.dir="${OpenSim_ROOT_DIR}"
                  -Dmodels.dir="${MODELS_REPO}"
                  DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/build.xml"
                  WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
                  COMMENT "Build OpenSim.app.")
endif()
