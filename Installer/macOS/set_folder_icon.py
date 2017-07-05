#!/usr/bin/python

# http://apple.stackexchange.com/questions/6901/how-can-i-change-a-file-or-folder-icon-using-the-terminal
import Cocoa
import sys

Cocoa.NSWorkspace.sharedWorkspace().setIcon_forFile_options_(
        Cocoa.NSImage.alloc().initWithContentsOfFile_(
            sys.argv[1].decode('utf-8')),
        sys.argv[2].decode('utf-8'), 0) or sys.exit("Unable to set file icon")

# App Store's Folders Factory takes an image and makes a new folder icon with
# it, using an "embossed" effect.
# https://itunes.apple.com/us/app/folders-factory/id442153412?mt=12

# Also, Image2Icon.
