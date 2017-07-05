#!/bin/sh

# http://blog.biicode.com/bii-internals-automating-macos-pkg-generation/index.html

#pkgbuild --analyze \
#    --root '../../Gui/opensim/dist/OpenSim 4.0' \
#    OpenSimAppComponents.plist
## The internal PKG should not have spaces, as this causes the "Show Files"
## window to not function properly. 
## https://stackoverflow.com/questions/43031272/macos-installer-show-files-only-the-file-listing-isnt-available
pkgbuild \
    --identifier org.opensim.app.pkg \
    --version 4.0 \
    --root '../../Gui/opensim/dist/OpenSim 4.0' \
    --install-location '/Applications/OpenSim 4.0-beta' \
    'OpenSim-4.0-beta-App.pkg'
    # --component-plist OpenSimAppComponents.plist \
# productbuild --synthesize --package 'OpenSim-4.0-App.pkg' ./Distribution.xml
productbuild --distribution ./Distribution.xml \
    --version 4.0 \
    --package-path . \
    --resources ./Resources \
    './OpenSim-4.0-beta.pkg'
