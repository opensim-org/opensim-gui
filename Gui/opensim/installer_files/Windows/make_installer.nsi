;NSIS Modern User Interface
;Basic Example Script

;--------------------------------
;Include Modern UI

  !include "MUI2.nsh"

;--------------------------------
;General

  ;Name and file
  Name "OpenSim 4.0 Beta"
  OutFile "opensim-4.0-win64.exe"

  ;Default installation folder
  InstallDir "c:\opensim 4.0Beta"
  
  ;Get installation folder from registry if available
  InstallDirRegKey HKCU "Software\OpenSim 4.0Beta" ""

  ;Request application privileges for Windows Vista
  RequestExecutionLevel user

;--------------------------------
;Interface Settings
  !define MUI_ABORTWARNING

;--------------------------------
;Pages

  !insertmacro MUI_PAGE_LICENSE "opensim\LICENSE.txt"
  !insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY
  !insertmacro MUI_PAGE_INSTFILES
  
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  
;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"

;--------------------------------
;Installer Sections

Section "OpenSim Application" SecMain
  SetOutPath "$INSTDIR"
  File /r opensim\*.*
  
  ;Store installation folder
  WriteRegStr HKCU "Software\OpenSim4.0Beta" "" $INSTDIR
 
;Create shortcuts
  CreateShortCut "$DESKTOP\opensim 4.0.Beta.lnk" "$INSTDIR\bin\opensim64.exe" ""

  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"

SectionEnd

;--------------------------------
;Descriptions

  ;Language strings
  LangString DESC_SecMain ${LANG_ENGLISH} "Install OpenSim Application."

  ;Assign language strings to sections
  !insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
    !insertmacro MUI_DESCRIPTION_TEXT ${SecMain} $(DESC_SecMain)
  !insertmacro MUI_FUNCTION_DESCRIPTION_END

;--------------------------------
;Uninstaller Section

Section "Uninstall"

  ;ADD YOUR OWN FILES HERE...

  Delete "$INSTDIR\Uninstall.exe"

  RMDir "$INSTDIR"

  DeleteRegKey /ifempty HKCU "Software\OpenSim4.0Beta"

SectionEnd