#
# Build of OpenSim application. There are 4 steps:
#    (1) Locate OpenSim API install (including Java Bindings)
#    (2) Copy java files (or jar file) to proper location under opensim-gui
#    (3) Build OpenSim application using ant build.xml, include"platform specific" 
#            application launcher and JRE 
#    (4) Merge/install API install from (1) with launcher from (3) to make final installer 
#----------------------------------------------------

# Step (1) -- Locate OpenSim package.
find_package(OpenSim REQUIRED)

# Step (2) -- Copy OpenSim's jar file to opensim-gui.
file(REMOVE_RECURSE "${CMAKE_CURRENT_SOURCE_DIR}/modeling/src/org")
file(COPY ${OpenSim_JAVA_FILES_DIR}
    DESTINATION "${CMAKE_CURRENT_SOURCE_DIR}/modeling/src"
    FILES_MATCHING PATTERN "*.java")

# add_custom_command()