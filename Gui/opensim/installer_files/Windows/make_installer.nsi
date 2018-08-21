;NSIS Modern User Interface
;Build Windows Installer Script

;--------------------------------
;Include Modern UI

  !include "MUI2.nsh"

;--------------------------------
;General

  ;Name and file
  Name "OpenSim @VERSION@"
  OutFile "OpenSim-@VERSION@-win64.exe"

  ;Set compression
  SetCompressor lzma

  ;Default installation folder
  InstallDir "C:\OpenSim @VERSION@"
  
  ;Get installation folder from registry if available
  InstallDirRegKey HKCU "Software\OpenSim @VERSION@" ""

  ;Request application privileges for Windows Vista
  RequestExecutionLevel user

;--------------------------------
;Interface Settings

  !define MUI_HEADERIMAGE
  !define MUI_ABORTWARNING

;--------------------------------
; Installation types


;--------------------------------
; Component sections


;--------------------------------
; Define some macro setting for the gui

;TODO update this icon to not just be the OpenSim logo
; (to include a cardboard box; something to indicate this
; is the installer, not the application itself).
;!define MUI_ICON "OpenSimLogoWindows.ico"
!define MUI_HEADERIMAGE_BITMAP "OpenSimInstallerIcon.bmp"



;--------------------------------
;Pages
  !insertmacro MUI_PAGE_WELCOME

  !insertmacro MUI_PAGE_LICENSE "opensim\LICENSE.txt"
  ;We have only 1 component; no need to prompt.
  ;!insertmacro MUI_PAGE_COMPONENTS
  !insertmacro MUI_PAGE_DIRECTORY
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_PAGE_FINISH

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
  WriteRegStr HKCU "Software\OpenSim@VERSION@" "" $INSTDIR
 
  ;Create shortcuts
  ;TODO the uninstaller does not remove this shortcut.
  CreateShortCut "$DESKTOP\OpenSim @VERSION@.lnk" "$INSTDIR\bin\opensim64.exe" ""
  ;Commented out for now because the Uninstaller does not yet remove these shortcuts.
  CreateDirectory "$SMPROGRAMS\OpenSim"
  CreateShortCut "$SMPROGRAMS\OpenSim\OpenSim @VERSION@.lnk" "$INSTDIR\bin\opensim64.exe"
  CreateShortCut "$SMPROGRAMS\OpenSim\Uninstall OpenSim @VERSION@.lnk" "$INSTDIR\Uninstall.exe"

  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"
  
  ;Run the Visual C++ redistributable installer.
  ;https://docs.microsoft.com/en-us/cpp/ide/deployment-in-visual-cpp
  ; install: install the redist (as opposed to uninstall or repair)
  ; passive: show a UI but do not require user input (as opposed to 'quiet'
  ;          which causes no UI to be shown).
  ; norestart: do not prompt to restart the computer.
  ExecWait '"$INSTDIR\bin\vcredist_x64.exe" /install /passive /norestart'

SectionEnd

;--------------------------------
;Descriptions
  ;Commented out this section because MUI_PAGE_COMPONENTS
  ;above is commented out.
  
  ;;Language strings
  ;LangString DESC_SecMain ${LANG_ENGLISH} "Install OpenSim Application."

  ;;Assign language strings to sections
  ;!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
  ;  !insertmacro MUI_DESCRIPTION_TEXT ${SecMain} $(DESC_SecMain)
  ;!insertmacro MUI_FUNCTION_DESCRIPTION_END
;--------------------------------
;Uninstaller Section

Section "Uninstall"

  ;ADD YOUR OWN FILES HERE...

  Delete "$INSTDIR\Uninstall.exe"

  RMDir /r "$INSTDIR"

  DeleteRegKey /ifempty HKCU "Software\OpenSim@VERSION@"

SectionEnd
