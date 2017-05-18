/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view.nodes;

import java.awt.Color;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openide.nodes.Node;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.Appearance;
import org.opensim.modeling.Component;
import org.opensim.modeling.DecorativeGeometry;
import org.opensim.modeling.Model;
import org.opensim.modeling.Vec3;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman-NMBL
 */
public class AppearanceHelper {

    Appearance appearance = null;
    OneComponentNode compNode = null;
    Model model;
    
    public AppearanceHelper(Model model, OneComponentNode node, Appearance dAppearance){
        this.model = model;
        this.compNode = node;
        this.appearance = dAppearance;
    }
    
    void setAppearanceDisplayPrefProperty(int pref) {
        final AbstractProperty ap = appearance.get_SurfaceProperties().getPropertyByName("representation");
        final DecorativeGeometry.Representation oldRep = appearance.get_representation();
        final int newRep = pref;
        final PropertyEditorAdaptor pea = new PropertyEditorAdaptor(model, appearance.get_SurfaceProperties(), ap, compNode);
        ap.setValueIsDefault(false);
        pea.setValueInt(newRep, false);
        ViewDB.getInstance().updateComponentDisplay(model, compNode.comp, ap);
        updateObjectDisplay(compNode.comp);
        AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {
            @Override
            public void undo() throws CannotUndoException {
                super.undo();
                pea.setValueInt(oldRep.swigValue(), false);
                updateObjectDisplay(compNode.comp);
                ViewDB.getInstance().updateComponentDisplay(model, compNode.comp, ap);
            }

            @Override
            public String getUndoPresentationName() {
                return "Undo representation change";
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                pea.setValueInt(newRep, false);
                ViewDB.getInstance().updateComponentDisplay(model, compNode.comp, ap);
                updateObjectDisplay(compNode.comp);
            }

            @Override
            public String getRedoPresentationName() {
                return "Redo representation change";
            }
        };
        ExplorerTopComponent.addUndoableEdit(auEdit);
    }

    void setAppearanceVisilibityProperty(Boolean newValue) {
        final AbstractProperty ap = appearance.getPropertyByName("visible");
        final boolean oldVis = appearance.get_visible();
        final boolean newVis = newValue;
        final Model model = this.model;
        final PropertyEditorAdaptor pea = new PropertyEditorAdaptor(model, appearance, ap, compNode);
        ap.setValueIsDefault(false);
        pea.setValueBool(newVis, false);
        ViewDB.getInstance().updateComponentDisplay(model, compNode.comp, ap);
        AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {
            @Override
            public void undo() throws CannotUndoException {
                super.undo();
                pea.setValueBool(oldVis, false);
                ViewDB.getInstance().updateComponentDisplay(model, compNode.comp, ap);
            }

            @Override
            public String getUndoPresentationName() {
                return "Undo visibility change";
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                pea.setValueBool(newVis, false);
                ViewDB.getInstance().updateComponentDisplay(model, compNode.comp, ap);
            }

            @Override
            public String getRedoPresentationName() {
                return "Redo visibility change";
            }
        };
        ExplorerTopComponent.addUndoableEdit(auEdit);
    }

    void setAppearanceColorProperty(Color newColor) {
        float[] colorComp = new float[3];
        newColor.getColorComponents(colorComp);
        final AbstractProperty ap = appearance.getPropertyByName("color");
        final Model model = this.model;
        final Vec3 oldValue = new Vec3(appearance.get_color());
        final PropertyEditorAdaptor pea = new PropertyEditorAdaptor(model, appearance, ap, compNode);
        ap.setValueIsDefault(false);
        final Vec3 newColorVec3 = new Vec3(colorComp[0], colorComp[1], colorComp[2]);
        pea.setValueVec3(newColorVec3, false);
        ViewDB.getInstance().updateComponentDisplay(model, compNode.comp, ap);
        System.out.println("p, c"+ap.toString()+compNode.comp.dump());
        AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {
            @Override
            public void undo() throws CannotUndoException {
                super.undo();
                pea.setValueVec3(oldValue, false);
                ViewDB.getInstance().updateComponentDisplay(model, compNode.comp, ap);
            }

            @Override
            public String getUndoPresentationName() {
                return "Undo color change";
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                pea.setValueVec3(newColorVec3, false);
                ViewDB.getInstance().updateComponentDisplay(model, compNode.comp, ap);
            }

            @Override
            public String getRedoPresentationName() {
                return "Redo color change";
            }
        };
        ExplorerTopComponent.addUndoableEdit(auEdit);
    }

    protected void updateObjectDisplay(Component obj) {
        // Tell the world that the owner modelComponent need to regenerate
        ViewDB.getInstance().getModelVisuals(model).upateDisplay(obj);
        //ViewDB.getInstance().repaintAll();
    }

    void setAppearanceOpacityProperty(double opacity) {
        final AbstractProperty ap = appearance.getPropertyByName("opacity");
        final double oldOpacity = appearance.get_opacity();
        final double newOpacity = opacity;
        final Model model = this.model;
        final PropertyEditorAdaptor pea = new PropertyEditorAdaptor(model, appearance, ap, compNode);
        ap.setValueIsDefault(false);
        pea.setValueDouble(newOpacity, false);
        ViewDB.getInstance().updateComponentDisplay(model, compNode.comp, ap);
        AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {
            @Override
            public void undo() throws CannotUndoException {
                super.undo();
                pea.setValueDouble(oldOpacity, false);
                ViewDB.getInstance().updateComponentDisplay(model, compNode.comp, ap);
            }

            @Override
            public String getUndoPresentationName() {
                return "Undo opacity change";
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                pea.setValueDouble(newOpacity, false);
                ViewDB.getInstance().updateComponentDisplay(model, compNode.comp, ap);
            }

            @Override
            public String getRedoPresentationName() {
                return "Redo opacity change";
            }
        };
        ExplorerTopComponent.addUndoableEdit(auEdit);
    }
    
}
