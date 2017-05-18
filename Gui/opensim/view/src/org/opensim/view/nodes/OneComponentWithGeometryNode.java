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
            sheet.get(Sheet.PROPERTIES).remove("Appearance");
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
        setAppearanceColorProperty(newColor);
    }

    private void setAppearanceColorProperty(Color newColor) {
        AppearanceHelper helper = new AppearanceHelper(getModelForNode(), this, appearance);
        helper.setAppearanceColorProperty(newColor);
    }

    public double getOpacity() {
        return appearance.get_opacity();
    }

    public void setOpacity(double opacity) {
        setAppearanceOpacityProperty(opacity);
    }

    private void setAppearanceOpacityProperty(double opacity) {
        AppearanceHelper helper = new AppearanceHelper(getModelForNode(), this, appearance);
        helper.setAppearanceOpacityProperty(opacity);
    }

    @Override
    public Boolean getVisible() {
        return appearance.get_visible();
    }

    @Override
    public void setVisible(Boolean newValue) {
        setAppearanceVisilibityProperty(newValue);
    }

    private void setAppearanceVisilibityProperty(Boolean newValue) {
        AppearanceHelper helper = new AppearanceHelper(getModelForNode(), this, appearance);
        helper.setAppearanceVisilibityProperty(newValue);
    }

    @Override
    public int getDisplayPreference() {
        return appearance.get_representation().swigValue();
    }

    @Override
    public void setDisplayPreference(int pref) {
        setAppearanceDisplayPrefProperty(pref);
    }

    private void setAppearanceDisplayPrefProperty(int pref) {
        AppearanceHelper helper = new AppearanceHelper(getModelForNode(), this, appearance);
        helper.setAppearanceDisplayPrefProperty(pref);
    }

    protected void updateObjectDisplay(Component obj) {
        // Tell the world that the owner modelComponent need to regenerate
        Model mdl = getModelForNode();
        ViewDB.getInstance().getModelVisuals(mdl).upateDisplay(obj);
        //ViewDB.getInstance().repaintAll();
    }
    
}
