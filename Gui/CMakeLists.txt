SET(OPENSIM_INSTALL_PACKAGE_BUILD OFF CACHE BOOL "Build an installer (to post on simtk.org)")
# The following lines assume that these steps are done from inside netbeans first
# build zip distribution
# extract all from zip under Gui/opensim/dist
# icons modified and etc files changed
if (WIN32)
    if(${PLATFORM_ABI} MATCHES "x64")
            INSTALL_FILES(/bin/ FILES opensim/dist/opensim/opensim/bin/opensim64.exe)
    ELSE()
            INSTALL_FILES(/bin/ FILES opensim/dist/opensim/opensim/bin/opensim.exe)
    ENDIF()
endif()
# etc
INSTALL(DIRECTORY opensim/dist/opensim/opensim/etc/ DESTINATION etc)
# Scripts
INSTALL(DIRECTORY opensim/Scripts/ DESTINATION Scripts/GUI_Scripting PATTERN ".svn" EXCLUDE)

# remaining folders
if (WIN32)
   INSTALL(DIRECTORY opensim/dist/opensim/opensim/ide/ DESTINATION ide)
   INSTALL(DIRECTORY opensim/dist/opensim/opensim/platform/ DESTINATION platform)
   INSTALL(DIRECTORY opensim/dist/opensim/opensim/opensim/ DESTINATION opensim)
endif()
if (APPLE)
   INSTALL(DIRECTORY opensim/dist/opensim.app/Contents/Resources/opensim/ide/ DESTINATION ide)
   INSTALL(DIRECTORY opensim/dist/opensim.app/Contents/Resources/opensim/platform/ DESTINATION platform)
   INSTALL(DIRECTORY opensim/dist/opensim.app/Contents/Resources/opensim/opensim/ DESTINATION opensim)
endif()
# This to be replaced with a CMake Variable
FILE(GLOB VTK_LIBS ${VTK_LIB_DIR}/*.dll)
INSTALL_FILES(/bin/ FILES  ${VTK_LIBS})

INSTALL_FILES(/plugins/ FILES plugins/readme.txt)

# Install Tutorials to doc folder
FILE(GLOB TUTORIAL_FILES Documentation/Tutorials/*.pdf)
INSTALL_FILES(/doc/ FILES  ${TUTORIAL_FILES})
# MESSAGE("TUTORIAL_FILES="${TUTORIAL_FILES})

# Find Java and create a jdk/jre directory and copy jre to it
INCLUDE(${CMAKE_ROOT}/Modules/FindJava.cmake)
SET(JRE_TO_PACKAGE "" CACHE PATH "jdk folder to include with distro")
INSTALL(DIRECTORY ${JRE_TO_PACKAGE} DESTINATION jdk)
