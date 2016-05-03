include(FindPackageHandleStandardArgs)

find_program(Ant_EXECUTABLE 
             NAMES ant 
             PATHS $ENV{ANT_HOME}/bin
                   /usr/bin
                   /usr/local/bin)
find_package_handle_standard_args(Ant DEFAULT_MSG Ant_EXECUTABLE)
mark_as_advanced(Ant_EXECUTABLE)
