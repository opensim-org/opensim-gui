IF(OPENSIM_INSTALL_PACKAGE_BUILD)


SET(OPENSIM_INSTALL_PACKAGE_BUILD OFF CACHE BOOL "Build an installer (to post on simtk.org)")
# The following lines assume that these steps are done from inside netbeans first
# build zip distribution
# extract all from zip 
# icons modified and etc files changed
INSTALL(DIRECTORY opensim/dist/opensim/opensim/bin DESTINATION ${CMAKE_INSTALL_PREFIX})
INSTALL(DIRECTORY opensim/dist/opensim/opensim/ide DESTINATION ${CMAKE_INSTALL_PREFIX})
INSTALL(DIRECTORY opensim/dist/opensim/opensim/platform DESTINATION ${CMAKE_INSTALL_PREFIX})
INSTALL(DIRECTORY opensim/dist/opensim/opensim/etc DESTINATION ${CMAKE_INSTALL_PREFIX})
INSTALL(DIRECTORY opensim/dist/opensim/opensim/opensim DESTINATION ${CMAKE_INSTALL_PREFIX})
INSTALL(DIRECTORY opensim/Scripts DESTINATION ${CMAKE_INSTALL_PREFIX} PATTERN ".svn" EXCLUDE)

# This to be replaced with a CMake Variable
FILE(GLOB VTK_LIBS ${VTK_LIB_DIR}/*.dll)
INSTALL_FILES(/bin/ FILES  ${VTK_LIBS})

INSTALL(DIRECTORY Internal DESTINATION Models PATTERN ".svn" EXCLUDE)

# Find Java and create a jdk/jre directory and copy jre to it
INCLUDE(${CMAKE_ROOT}/Modules/FindJava.cmake)
SET(JRE_TO_PACKAGE "" CACHE PATH "jdk folder to include with distro")
INSTALL(DIRECTORY ${JRE_TO_PACKAGE} DESTINATION jdk)

#etc/opensim.conf should have jdkhome=jdk, 
# Should also have -J-Dsun.java2d.ddoffscreen=false -J-Dsun.java2d.gdiblit=false
# in addition first line should have
# default_userdir="${HOME}/.${APPNAME}/1.0/dev"
ENDIF(OPENSIM_INSTALL_PACKAGE_BUILD)
