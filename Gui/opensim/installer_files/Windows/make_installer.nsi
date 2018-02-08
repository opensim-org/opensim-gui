!define ZIP2EXE_COMPRESSOR_ZLIB
!define ZIP2EXE_INSTALLDIR "c:\opensim 4.0"
!define ZIP2EXE_NAME "OpenSim 4.0 Beta"
!define ZIP2EXE_OUTFILE "opensim-4.0-win64.exe"

!include "${NSISDIR}\Contrib\zip2exe\Base.nsh"
!include "${NSISDIR}\Contrib\zip2exe\Classic.nsh"

!insertmacro SECTION_BEGIN
  File /r opensim\*.*
  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"
!insertmacro SECTION_END

Section "Installer Section"
;Create shortcuts
  CreateDirectory "$SMPROGRAMS\$STARTMENU_FOLDER"
  CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\OpenSim.lnk" "$INSTDIR\bin\opensim64.exe"
  CreateShortCut "$DESKTOP\opensim 4.0.Beta.lnk" "$INSTDIR\bin\opensim64.exe" ""
  CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\Uninstall.lnk" "$INSTDIR\Uninstall.exe"
; Run redistributable, test silent mode
;ExecWait '"$INSTDIR\bin\vcredist_x64.exe"'
SectionEnd

Section "un.Uninstaller Section"
  Delete "$INSTDIR\*"
  Delete "$DESKTOP\opensim 4.0.Beta.lnk"
SectionEnd
