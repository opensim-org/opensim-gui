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
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import static org.openide.nodes.Sheet.createExpertSet;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.Appearance;
import org.opensim.modeling.Component;
import org.opensim.modeling.DecorativeGeometry;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.SurfaceProperties;
import org.opensim.modeling.Vec3;
import org.opensim.view.ColorableInterface;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman
 */
public abstract class OneComponentWithGeometryNode extends OneComponentNode implements ColorableInterface {
    
    Appearance appearance=null;
    public OneComponentWithGeometryNode(OpenSimObject obj) {
        super(obj);
        OpenSimObject appearanceObj = obj.getPropertyByName("Appearance").getValueAsObject();
        appearance = Appearance.safeDownCast(appearanceObj);
        
    }

    @Override
    public Sheet createSheet() {
        Sheet sheet;
        sheet = super.createSheet();
        Sheet.Set set = sheet.get("properties");
        // Add property for appearance
        if (appearance == null) {
            return sheet;
        }
        addAppearanceProperties(sheet);
        return sheet;
    }

    protected void addAppearanceProperties(Sheet sheet) {
        try {
            sheet.remove("Appearance");
            Sheet.Set appearanceSheet = createExpertSet();
            appearanceSheet.setDisplayName("Appearance");
            sheet.put(appearanceSheet);
            // Visible boolean property
            PropertySupport.Reflection nextNodePropeVis;
            nextNodePropeVis = new PropertySupport.Reflection(this, Boolean.class, "getVisible", "setVisible");
            nextNodePropeVis.setName("Visible");
            nextNodePropeVis.setShortDescription(appearance.getPropertyByName("visible").getComment());
            appearanceSheet.put(nextNodePropeVis);
            // Opacity
            PropertySupport.Reflection nextNodeProp5;
            nextNodeProp5 = new PropertySupport.Reflection(this, double.class, "getOpacity", "setOpacity");
            nextNodeProp5.setName("Opacity");
            nextNodeProp5.setShortDescription(appearance.getPropertyByName("opacity").getComment());
            appearanceSheet.put(nextNodeProp5);
            // Color
            PropertySupport.Reflection nextNodeProp4;
            nextNodeProp4 = new PropertySupport.Reflection(this, Color.class, "getColor", "setColor");
            nextNodeProp4.setName("Color");
            appearanceSheet.put(nextNodeProp4);
            // Representation
            PropertySupport.Reflection nextNodePropRepresentation;
            SurfaceProperties surfApp = appearance.get_SurfaceProperties();
            nextNodePropRepresentation = new PropertySupport.Reflection(this, int.class, "getDisplayPreference", "setDisplayPreference");
            //nextNodePropRepresentation.setPropertyEditorClass(DisplayPreferenceEditor.class);
            nextNodePropRepresentation.setName("DisplayPreference");
            nextNodePropRepresentation.setShortDescription(surfApp.getPropertyByName("representation").getComment());
            appearanceSheet.put(nextNodePropRepresentation);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

    public Color getColor() {
        Vec3 c3 = appearance.get_color();
        return new Color((float) c3.get(0), (float) c3.get(1), (float) c3.get(2));
    }

    public void setColor(Color newColor) {
        float[] colorComp = new float[3];
        newColor.getColorComponents(colorComp);
        final AbstractProperty ap = appearance.getPropertyByName("color");
        final Model model = getModelForNode();
        final Vec3 oldValue = new Vec3(appearance.get_color());
        final PropertyEditorAdaptor pea = new PropertyEditorAdaptor(getModelForNode(), appearance, ap, this);
        final Vec3 newColorVec3 = new Vec3(colorComp[0], colorComp[1], colorComp[2]);
        pea.setValueVec3(newColorVec3, false);
        ViewDB.getInstance().updateComponentDisplay(model, comp, ap);
        AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {
            @Override
            public void undo() throws CannotUndoException {
                super.undo();
                pea.setValueVec3(oldValue, false);
                ViewDB.getInstance().updateComponentDisplay(model, comp, ap);
            }

            @Override
            public String getUndoPresentationName() {
                return "Undo color change";
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                pea.setValueVec3(newColorVec3, false);
                ViewDB.getInstance().updateComponentDisplay(model, comp, ap);
            }

            @Override
            public String getRedoPresentationName() {
                return "Redo color change";
            }
        };
        ExplorerTopComponent.addUndoableEdit(auEdit);
    }

    public double getOpacity() {
        return appearance.get_opacity();
    }

    public void setOpacity(double opacity) {
        final AbstractProperty ap = appearance.getPropertyByName("opacity");
        final double oldOpacity = appearance.get_opacity();
        final double newOpacity = opacity;
        final Model model = getModelForNode();
        final PropertyEditorAdaptor pea = new PropertyEditorAdaptor(getModelForNode(), appearance, ap, this);
        pea.setValueDouble(newOpacity, false);
        ViewDB.getInstance().updateComponentDisplay(model, comp, ap);
        AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {
            @Override
            public void undo() throws CannotUndoException {
                super.undo();
                pea.setValueDouble(oldOpacity, false);
                ViewDB.getInstance().updateComponentDisplay(model, comp, ap);
            }

            @Override
            public String getUndoPresentationName() {
                return "Undo opacity change";
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                pea.setValueDouble(newOpacity, false);
                ViewDB.getInstance().updateComponentDisplay(model, comp, ap);
            }

            @Override
            public String getRedoPresentationName() {
                return "Redo opacity change";
            }
        };
        ExplorerTopComponent.addUndoableEdit(auEdit);
    }

    public Boolean getVisible() {
        return appearance.get_visible();
    }

    public void setVisible(Boolean newValue) {
        final AbstractProperty ap = appearance.getPropertyByName("visible");
        final boolean oldVis = appearance.get_visible();
        final boolean newVis = newValue;
        final Model model = getModelForNode();
        final PropertyEditorAdaptor pea = new PropertyEditorAdaptor(model, appearance, ap, this);
        pea.setValueBool(newVis, false);
        ViewDB.getInstance().updateComponentDisplay(model, comp, ap);
        AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {
            @Override
            public void undo() throws CannotUndoException {
                super.undo();
                pea.setValueBool(oldVis, false);
                ViewDB.getInstance().updateComponentDisplay(model, comp, ap);
            }

            @Override
            public String getUndoPresentationName() {
                return "Undo visibility change";
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                pea.setValueBool(newVis, false);
                ViewDB.getInstance().updateComponentDisplay(model, comp, ap);
            }

            @Override
            public String getRedoPresentationName() {
                return "Redo visibility change";
            }
        };
        ExplorerTopComponent.addUndoableEdit(auEdit);
    }

    @Override
    public int getDisplayPreference() {
        return appearance.get_representation().swigValue();
    }

    public void setDisplayPreference(int pref) {
        final AbstractProperty ap = appearance.get_SurfaceProperties().getPropertyByName("representation");
        final Model model = getModelForNode();
        final DecorativeGeometry.Representation oldRep = appearance.get_representation();
        final int newRep = pref;
        final PropertyEditorAdaptor pea = new PropertyEditorAdaptor(getModelForNode(), appearance.get_SurfaceProperties(), ap, this);
        pea.setValueInt(newRep, false);
        ViewDB.getInstance().updateComponentDisplay(model, comp, ap);
        updateObjectDisplay(comp);
        AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {
            @Override
            public void undo() throws CannotUndoException {
                super.undo();
                pea.setValueInt(oldRep.swigValue(), false);
                updateObjectDisplay(comp);
                ViewDB.getInstance().updateComponentDisplay(model, comp, ap);
            }

            @Override
            public String getUndoPresentationName() {
                return "Undo representation change";
            }

            @Override
            public void redo() throws CannotRedoException {
                super.redo();
                pea.setValueInt(newRep, false);
                ViewDB.getInstance().updateComponentDisplay(model, comp, ap);
                updateObjectDisplay(comp);
            }

            @Override
            public String getRedoPresentationName() {
                return "Redo representation change";
            }
        };
        ExplorerTopComponent.addUndoableEdit(auEdit);
    }

    protected void updateObjectDisplay(Component obj) {
        // Tell the world that the owner modelComponent need to regenerate
        Model mdl = getModelForNode();
        ViewDB.getInstance().getModelVisuals(mdl).upateDisplay(obj);
        //ViewDB.getInstance().repaintAll();
    }
    
}
