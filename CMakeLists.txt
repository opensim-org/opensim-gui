PROJECT (OpenSimApp)
#
# Build of OpenSim application. There are 4 steps:
#    (1) Locate OpenSim API install (including Java Bindings)
#    (2) Copy java files (or jar file) to proper location under opensim-gui
#    (3) Build OpenSim application using ant build.xml, include"platform specific" 
#            application launcher and JRE 
#    (4) Merge/install API install from (1) with launcher from (3) to make final installer 
#----------------------------------------------------

cmake_minimum_required(VERSION 3.3)
# (1)
find_package(OpenSim REQUIRED)


# (2)
file(GLOB OPENSIM_JAVA_FILES
            ${OpenSim_ROOT_DIR}/sdk/Java/org/opensim/modeling/*.java)

# This target depends on the destination copies of the Simbody files
# (in the binary build directory).  Those are produced by the OUTPUT
# custom command below, which depends on the source files from the
# Simbody installation directory.
add_custom_target(CopyJavaSourceFiles ALL DEPENDS ${COPIED_JAVA_FILES})
set_target_properties(CopyJavaSourceFiles
    PROPERTIES PROJECT_LABEL "GUI - API Java Files")
