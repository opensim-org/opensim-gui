/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view.dataObjects;

import java.io.IOException;
import org.openide.cookies.CloseCookie;
import org.openide.cookies.OpenCookie;
import org.openide.loaders.OpenSupport;
import org.openide.util.Exceptions;
import org.openide.windows.CloneableTopComponent;
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
            //AbcTopComponent tc = new AbcTopComponent();
            //tc.setDisplayName(dobj.getName());
            FileOpenOsimModelAction act = ((FileOpenOsimModelAction) FileOpenOsimModelAction.findObject(
                            (Class)Class.forName("org.opensim.view.FileOpenOsimModelAction"), true));
            act.loadModel(dobj.getPrimaryFile().getPath());
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