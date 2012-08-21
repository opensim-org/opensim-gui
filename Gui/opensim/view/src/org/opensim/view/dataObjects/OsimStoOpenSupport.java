/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view.dataObjects;

import org.openide.cookies.CloseCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.OpenSupport;
import org.openide.util.Exceptions;
import org.openide.windows.CloneableTopComponent;
import org.opensim.utils.FileUtils;
import org.opensim.view.motions.FileLoadMotionAction;

/**
 *
 * @author ayman
 */
class OsimStoOpenSupport extends OpenSupport implements OpenCookie, CloseCookie {

    public OsimStoOpenSupport(OsimModelDataObject.Entry entry) {
        super(entry);
    }

    @Override
    public void open() {
        try {
            OsimMotDataObject dobj = (OsimMotDataObject) entry.getDataObject();
            FileObject fobj = dobj.getPrimaryFile();
            // make sure default directory for future GUI browsing is set to the folder containing model
            String parentPath = fobj.getParent().getPath();
            FileUtils.getInstance().setWorkingDirectoryPreference(parentPath);

            ((FileLoadMotionAction) FileLoadMotionAction.findObject(
                            (Class<FileLoadMotionAction>)Class.forName("org.opensim.view.motions.FileLoadMotionAction"))).loadMotion(fobj.getPath());
            return;
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    protected CloneableTopComponent createCloneableTopComponent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
 
}