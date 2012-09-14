/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view.nodes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditorSupport;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman
 */
public class OpenSimCustomEditor extends PropertyEditorSupport
        implements PropertyChangeListener {

    public OpenSimCustomEditor(AbstractProperty ap, OpenSimObjectNode osNode) {
        this.ap = ap;
        opensimNode = osNode;
        model = opensimNode.getModelForNode();
        viewdb = ViewDB.getInstance();
        context = OpenSimDB.getInstance().getContext(model);

    }

    @Override
    public String getJavaInitializationString() {
        return ""; //super.getJavaInitializationString();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        //super.setAsText(text);
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
//        System.out.println("PropertyChanged");
//        System.out.println("New:"+((OpenSimObject)pce.getNewValue()).dump());
//        System.out.println("Old:"+((OpenSimObject)pce.getOldValue()).dump());
        PropertyEditorAdaptor pea = new PropertyEditorAdaptor(ap, opensimNode);
        pea.setValueObj((OpenSimObject) pce.getNewValue(), ((OpenSimObject) pce.getOldValue()));
//        context.recreateSystemKeepStage();
//        viewdb.updateModelDisplay(model, objectToEdit);
//        if (opensimNode!= null) opensimNode.refreshNode();
//        SingleModelGuiElements guiElem = viewdb.getModelGuiElements(model);
//        guiElem.setUnsavedChangesFlag(true);

    }
    
    AbstractProperty ap;
    OpenSimContext context;
    Model model;
    OpenSimObjectNode opensimNode;
    ViewDB viewdb;
}
