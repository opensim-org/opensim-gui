IF(OPENSIM_INSTALL_PACKAGE_BUILD)
FILE(GLOB BIN_FILES opensim/build/launcher/bin/*.*)
FILE(GLOB ETC_FILES opensim/build/launcher/etc/*.*)
FILE(GLOB INTERNAL_FILES Internal_openSimlab.osim)

INSTALL_FILES(/bin/ FILES  ${BIN_FILES})
INSTALL_FILES(/etc/ FILES  ${ETC_FILES})
INSTALL_FILES(/Models/Internal/ FILES  ${INTERNAL_FILES})

# This to be replaced with a CMake Variable
# Check if directory exists first to more gracefully handle case where it doesn't exist (otherwise cmake prints an error message)
INSTALL(DIRECTORY ${NETBEANS_INSTALL_DIR}/platform6/ DESTINATION platform6)
# IF(EXISTS opensim/build/cluster/)
INSTALL(DIRECTORY opensim/build/cluster/ DESTINATION opensim)
INSTALL(DIRECTORY opensim/Scripts DESTINATION Scripts PATTERN ".svn" EXCLUDE)
# ENDIF(EXISTS opensim/build/cluster/)

# This to be replaced with a CMake Variable
FILE(GLOB VTK_LIBS ${VTK_LIB_DIR}/*.dll)
INSTALL_FILES(/bin/ FILES  ${VTK_LIBS})

INSTALL(DIRECTORY opensim/Models/ DESTINATION Models PATTERN ".svn" EXCLUDE)

# Find Java and create a jdk/jre directory and copy jre to it
INCLUDE(${CMAKE_ROOT}/Modules/FindJava.cmake)
INSTALL(DIRECTORY ${JRE_TO_PACKAGE} DESTINATION jdk)

#etc/opensim.conf should have jdkhome=jdk, 
# Should also have -J-Dsun.java2d.ddoffscreen=false -J-Dsun.java2d.gdiblit=false
# in addition first line should have
# default_userdir="${HOME}/.${APPNAME}/1.0/dev"
ENDIF(OPENSIM_INSTALL_PACKAGE_BUILD)
