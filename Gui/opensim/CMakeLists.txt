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

# http://xit0.org/2013/04/cmake-use-git-branch-and-commit-details-in-project/
# Get the latest abbreviated commit hash of the working branch
find_package(Git)
if(NOT Git_FOUND)
  set(VERSION "000")
else()
    # This command provides the annotated tag for the current commit, if it
    # exists, and returns an error if a tag does not exist.
    execute_process(
      COMMAND git describe --exact-match
      WORKING_DIRECTORY ${CMAKE_SOURCE_DIR}
      RESULT_VARIABLE GIT_DESCRIBE_EXACT_MATCH_RETVAL
      OUTPUT_VARIABLE GIT_TAG
      OUTPUT_STRIP_TRAILING_WHITESPACE
      OUTPUT_QUIET
      ERROR_QUIET
    )
    if(${GIT_DESCRIBE_EXACT_MATCH_RETVAL} EQUAL 0)
        # A tag exists.
        set(VERSION "${GIT_TAG}")
    else()
        execute_process(
            COMMAND git log -1 --format=%h
            WORKING_DIRECTORY ${CMAKE_SOURCE_DIR}
            OUTPUT_VARIABLE GIT_COMMIT_HASH
            OUTPUT_STRIP_TRAILING_WHITESPACE
            )
        # prepend date so that build ids can meaningfully be sorted
        execute_process(
            COMMAND git show -s --format=%cd --date=short HEAD
            WORKING_DIRECTORY ${CMAKE_SOURCE_DIR}
            OUTPUT_VARIABLE GIT_COMMIT_DATE
            OUTPUT_STRIP_TRAILING_WHITESPACE
            )
        set(VERSION "${GIT_COMMIT_DATE}-${GIT_COMMIT_HASH}")
    endif()
endif()
set(OPENSIMGUI_BUILD_VERSION "${VERSION}" CACHE STRING
    "<date>-<commit>, or <tag> if this commit is tagged." FORCE)
# inform user of created build version.
message(STATUS "Build VERSION: ${VERSION}")

# copy build_id to .properties file to accompany build/package
configure_file(${CMAKE_CURRENT_SOURCE_DIR}/nbproject/project.properties.in
    "${CMAKE_CURRENT_SOURCE_DIR}/nbproject/project.properties" @ONLY)
# TODO pass app.version on Ant command line?

add_custom_target(CopyOpenSimCore ALL
                  COMMAND ${Ant_EXECUTABLE} copy-java-bindings -DapiJava.dir="${OpenSim_JAVA_FILES_DIR}"
                  ${ANT_ARGS}
                  DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/build.xml"
                  WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
                  COMMENT "Copy opensim-core Java Bindings to GUI.")
if (WIN32)
    # InstallRequiredSystemLibraries defines MSVC_REDIST_DIR,
    # which is not really intended for use outside of 
    # InstallRequiredSystemLibraries, but it's the best mechanism
    # we have for now.
    # https://docs.microsoft.com/en-us/cpp/ide/deployment-in-visual-cpp
    # https://gitlab.kitware.com/cmake/cmake/issues/17725
    include(InstallRequiredSystemLibraries)
    # TODO: We only support 64-bit now. If building for 32-bit, must
    # use vcredist_x86.exe (could use CMAKE_MSVC_ARCH).
    set(MSVC_REDIST_EXE "${MSVC_REDIST_DIR}/vcredist_x64.exe")

    add_custom_target(PrepareInstaller ALL
                  COMMAND ${Ant_EXECUTABLE} copy-installer-files -Dapi.dir="${OpenSim_ROOT_DIR}" -Dmsvc.redist.exe="${MSVC_REDIST_EXE}"
                  ${ANT_ARGS}
                  DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/build.xml"
                  WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
                  COMMENT "Merge GUI build and opensim-core install into one folder.")

    add_custom_target(CopyJRE ALL
                  COMMAND ${Ant_EXECUTABLE} copy-jre
                  ${ANT_ARGS}
                  DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/build.xml"
                  WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
                  COMMENT "Copy default JRE to installer folder.")
endif()

if (NOT APPLE)
add_custom_target(CopyVisualizer ALL
                  COMMAND ${Ant_EXECUTABLE} copy-visualizer
                  ${ANT_ARGS}
                  DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/build.xml"
                  WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
                  COMMENT "Copy Visualizer to installer folder.")
endif()

set(MODELS_REPO "${CMAKE_SOURCE_DIR}/opensim-models" CACHE
    PATH "The location of the Models repository to use for placing models into installation.")

set(MODELS_INSTALL_DIR "${CMAKE_BINARY_DIR}/models_install")
include(ExternalProject)
ExternalProject_Add(opensim-models
    DOWNLOAD_COMMAND ""
    SOURCE_DIR "${MODELS_REPO}"
    INSTALL_DIR "${MODELS_INSTALL_DIR}"
    CONFIGURE_COMMAND ${CMAKE_COMMAND} <SOURCE_DIR> -DCMAKE_INSTALL_PREFIX=<INSTALL_DIR>)


if (MODELS_REPO AND NOT APPLE)
add_custom_target(CopyModels ALL
                  COMMAND ${Ant_EXECUTABLE} copy-models -Dmodels.dir="${MODELS_INSTALL_DIR}"
                  ${ANT_ARGS}
                  DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/build.xml"
                  WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
                  COMMENT "Copy models and examples-to installer folder.")
add_dependencies(CopyModels opensim-models)
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
                  -Dmodels.dir="${MODELS_INSTALL_DIR}"
                  ${ANT_ARGS}
                  DEPENDS "${CMAKE_CURRENT_SOURCE_DIR}/build.xml"
                  WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
                  COMMENT "Build OpenSim.app.")
    add_dependencies(PrepareInstaller opensim-models)
endif()
