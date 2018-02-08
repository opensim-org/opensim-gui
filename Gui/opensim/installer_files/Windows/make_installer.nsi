!define ZIP2EXE_COMPRESSOR_ZLIB
!define ZIP2EXE_INSTALLDIR "c:\opensim 4.0"
!define ZIP2EXE_NAME "OpenSim 4.0 Beta"
!define ZIP2EXE_OUTFILE "opensim-4.0-win64.exe"

!include "${NSISDIR}\Contrib\zip2exe\Base.nsh"
!include "${NSISDIR}\Contrib\zip2exe\Classic.nsh"

!insertmacro SECTION_BEGIN
File /r opensim\*.*
!insertmacro SECTION_END