/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view.dataObjects;

import java.io.IOException;
import javax.swing.SwingUtilities;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.CloseCookie;
import org.openide.cookies.OpenCookie;
import org.openide.loaders.OpenSupport;
import org.openide.util.Exceptions;
import org.openide.windows.CloneableTopComponent;
import org.opensim.logger.OpenSimLogger;
import org.opensim.modeling.Model;
import org.opensim.view.pub.OpenSimDB;

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
            loadModel(dobj.getPrimaryFile().getPath());
            return;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    public boolean loadModel(final Model aModel, boolean loadInForground) throws IOException {
        boolean retValue = false;
        if (OpenSimDB.getInstance().hasModel(aModel)){   // If model is already loaded, complain and return.
            OpenSimLogger.logMessage("Model is already loaded\n", OpenSimLogger.ERROR);
            return retValue;            
           
        }
        // Make the window
        if (loadInForground){
            OpenSimDB.getInstance().addModel(aModel);
            return true;
        }
        else 
            SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                    try {
                        OpenSimDB.getInstance().addModel(aModel);
                    } catch (IOException ex) {
                        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(ex.getMessage()));
                    }
            }});
        retValue = true;
        return retValue;        
    }
    /**
     * This is the function that does the real work of loading a model file into the GUI
     * @param fileName is the absolute path to the file to be used.
     * @returns true on success else failure
     */
      public boolean loadModel(final String fileName) throws IOException {
         return loadModel(fileName, false);
         
     }
     public boolean loadModel(final String fileName, boolean loadInForground) throws IOException {
        boolean retValue = false;
        Model aModel=null;
        aModel= new Model(fileName);
        if (aModel == null){
             OpenSimLogger.logMessage("Failed to construct model from file "+fileName+"\n", OpenSimLogger.ERROR);
            return retValue;
        }

        return loadModel(aModel, loadInForground);
    }

    @Override
    protected CloneableTopComponent createCloneableTopComponent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
 
}