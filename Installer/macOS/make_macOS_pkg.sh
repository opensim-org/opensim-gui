#!/bin/sh

./set_folder_icon.py OpenSimFolder.icns '../../Gui/opensim/dist/pkgroot/OpenSim @VERSION@' 

# TODO edit relative paths once these files are configured.

# http://blog.biicode.com/bii-internals-automating-macos-pkg-generation/index.html

#pkgbuild --analyze \
#    --root '../../Gui/opensim/dist/OpenSim 4.0' \
#    OpenSimAppComponents.plist
## The internal PKG should not have spaces, as this causes the "Show Files"
## window to not function properly. 
## https://stackoverflow.com/questions/43031272/macos-installer-show-files-only-the-file-listing-isnt-available
pkgbuild \
    --identifier org.opensim.app.pkg.@VERSION@ \
    --version @VERSION@ \
    --root '../../Gui/opensim/dist/pkgroot/' \
    --scripts './scripts' \
    --install-location '/Applications/' \
    'OpenSim-@VERSION@-App.pkg'
    # --component-plist OpenSimAppComponents.plist \
# productbuild --synthesize --package 'OpenSim-4.0-App.pkg' ./Distribution.xml
productbuild --distribution ./Distribution.xml \
    --version @VERSION@ \
    --package-path . \
    --resources ./Resources \
    './OpenSim-@VERSION@.pkg'

# How to open the app after finishing the installation:
# https://stackoverflow.com/questions/35619036/open-app-after-installation-from-pkg-file-in-mac
