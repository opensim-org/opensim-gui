/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view.dataObjects;

import java.io.IOException;
import org.openide.cookies.CloseCookie;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.OpenSupport;
import org.openide.util.Exceptions;
import org.openide.windows.CloneableTopComponent;
import org.opensim.utils.FileUtils;
import org.opensim.view.FileOpenOsimModelAction;

/**
 *
 * @author ayman
 */
class OsimOpenSupport extends OpenSupport implements OpenCookie, CloseCookie {

    public OsimOpenSupport(OsimModelDataObject.Entry entry) {
        super(entry);
    }

    @Override
    public void open() {
        try {
            OsimModelDataObject dobj = (OsimModelDataObject) entry.getDataObject();
            FileOpenOsimModelAction act = ((FileOpenOsimModelAction) FileOpenOsimModelAction.findObject(
                            (Class)Class.forName("org.opensim.view.FileOpenOsimModelAction"), true));
            FileObject fobj = dobj.getPrimaryFile();
            // make sure default directory for future GUI browsing is set to the folder containing model
            String parentPath = fobj.getParent().getPath();
            FileUtils.getInstance().setWorkingDirectoryPreference(parentPath);
            act.loadModel(fobj.getPath());
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    protected CloneableTopComponent createCloneableTopComponent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
 
}