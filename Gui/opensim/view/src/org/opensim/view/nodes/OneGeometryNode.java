/*
 *
 * OneContactGeometryNode
 * Author(s): Ayman Habib
 * Copyright (c)  2005-2006, Stanford University, Ayman Habib
* Use of the OpenSim software in source form is permitted provided that the following
* conditions are met:
* 	1. The software is used only for non-commercial research and education. It may not
*     be used in relation to any commercial activity.
* 	2. The software is not distributed or redistributed.  Software distribution is allowed 
*     only through https://simtk.org/home/opensim.
* 	3. Use of the OpenSim software or derivatives must be acknowledged in all publications,
*      presentations, or documents describing work in which OpenSim or derivatives are used.
* 	4. Credits to developers may not be removed from executables
*     created from modifications of the source.
* 	5. Modifications of source code must retain the above copyright notice, this list of
*     conditions and the following disclaimer. 
* 
*  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
*  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
*  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
*  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
*  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
*  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
*  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
*  OR BUSINESS INTERRUPTION) OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
*  WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.opensim.view.nodes;

import java.awt.Color;
import java.awt.Image;
import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import static org.openide.nodes.Sheet.createExpertSet;
import org.openide.util.NbBundle;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.Appearance;
import org.opensim.modeling.DecorativeGeometry;
import org.opensim.modeling.Geometry;
import org.opensim.modeling.Model;
import org.opensim.modeling.SurfaceProperties;
import org.opensim.modeling.Vec3;
import org.opensim.view.ColorableInterface;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.editors.DisplayPreferenceEditor;
import org.opensim.view.nodes.OpenSimObjectNode.displayOption;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman Habib
 */
public class OneGeometryNode extends OneComponentNode implements ColorableInterface {
    
    private static ResourceBundle bundle = NbBundle.getBundle(OneGeometryNode.class);
    /**
    * Creates a new instance of OneContactForceNode
    */
    public OneGeometryNode(Geometry cg) {
        super(cg);
        String gName=cg.getName();
        if (cg.getName().equalsIgnoreCase("")){
            gName=cg.getName();
            setDisplayName(cg.getAbsolutePathName());
            setName(cg.getName());
        }
        setChildren(Children.LEAF);
        addDisplayOption(displayOption.Colorable);
        addDisplayOption(displayOption.Isolatable);
        addDisplayOption(displayOption.Showable);
    }
    public Image getIcon(int i) {
        URL imageURL = this.getClass().getResource("icons/displayGeometryNode.png");
        if (imageURL != null) {
            return new ImageIcon(imageURL, "Display Geometry").getImage();
        } else {
            return null;
        }
    }
   public Image getOpenedIcon(int i) {
        return getIcon(i);
    }

    public Action[] getActions(boolean b) {
        // Get actions from parent (generic object menu for review, display)
        Action[] superActions = (Action[]) super.getActions(b);
        return superActions;
    }

    public String getHtmlDisplayName() {
        String retValue;
        
        retValue = super.getHtmlDisplayName();
        if (retValue.equalsIgnoreCase("")){
            retValue = ((Geometry)getOpenSimObject()).getName();
        }
        return retValue;
    }
    
    @Override
    public Sheet createSheet() {
        Sheet sheet;
        
        sheet = super.createSheet();
        Sheet.Set set = sheet.get("properties");
        // Add property for Location
        Geometry obj = Geometry.safeDownCast(getOpenSimObject());
        Appearance disp = obj.get_Appearance();
        if (disp==null) return sheet;
        addAppearanceProperties(disp, sheet);
        
        return sheet;
    }

    private void addAppearanceProperties(Appearance disp,Sheet sheet) {
        try {
            sheet.remove("Appearance");
            Sheet.Set appearanceSheet = createExpertSet();
            appearanceSheet.setDisplayName("Appearance");
            sheet.put(appearanceSheet);
            // Visible boolean property
            PropertySupport.Reflection nextNodePropeVis;
            nextNodePropeVis = new PropertySupport.Reflection(this, Boolean.class, "getVisible", "setVisible");
            nextNodePropeVis.setName("Visible");        
            appearanceSheet.put(nextNodePropeVis);
            // Opacity
             PropertySupport.Reflection nextNodeProp5;
            nextNodeProp5 = new PropertySupport.Reflection(this, double.class, "getOpacity", "setOpacity");
            nextNodeProp5.setName("Opacity");        
            appearanceSheet.put(nextNodeProp5);
            // Color
            PropertySupport.Reflection nextNodeProp4;
            nextNodeProp4 = new PropertySupport.Reflection(this, Color.class, "getColor", "setColor");
            nextNodeProp4.setName("Color");        
            appearanceSheet.put(nextNodeProp4);
            // Representation
            PropertySupport.Reflection nextNodePropRepresentation;
            SurfaceProperties surfApp = disp.get_SurfaceProperties();
            nextNodePropRepresentation = new PropertySupport.Reflection(surfApp, int.class, 
                    "get_representation", "set_representation");
            //nextNodePropRepresentation.setPropertyEditorClass(DisplayPreferenceEditor.class);
            nextNodePropRepresentation.setName("DisplayPreference");        
            appearanceSheet.put(nextNodePropRepresentation);
        }
        catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }
    
    public Color getColor() {
        Geometry obj = Geometry.safeDownCast(getOpenSimObject());
        Vec3 c3 = obj.getColor();
        return new Color((float)c3.get(0), (float)c3.get(1), (float)c3.get(2));
    }

    public void setColor(Color newColor) {
        float[] colorComp = new float[3];
        newColor.getColorComponents(colorComp);
        Geometry obj = Geometry.safeDownCast(getOpenSimObject());
        final AbstractProperty ap = obj.get_Appearance().getPropertyByName("color");
        final Vec3 oldValue = new Vec3(obj.get_Appearance().get_color());
        final PropertyEditorAdaptor pea = new PropertyEditorAdaptor(getModelForNode(), obj.get_Appearance(),
                ap, this
                );
        final Vec3 newColorVec3 = new Vec3(colorComp[0], colorComp[1], colorComp[2]);
        pea.setValueVec3(newColorVec3, false);
        AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {

                @Override
                public void undo() throws CannotUndoException {
                    super.undo();
                    pea.setValueVec3(oldValue, false);
                }

                @Override
                public String getUndoPresentationName() {
                    return "Undo color change";
                }

                @Override
                public void redo() throws CannotRedoException {
                    super.redo();
                    pea.setValueVec3(newColorVec3, false);
                }

                @Override
                public String getRedoPresentationName() {
                    return "Redo color change";
                }
                
            };
        ExplorerTopComponent.addUndoableEdit(auEdit);
    }
    
    public double getOpacity() {
        Geometry obj = Geometry.safeDownCast(getOpenSimObject());
        return obj.get_Appearance().get_opacity();
    }

    public void setOpacity(double opacity) {
        Geometry obj = Geometry.safeDownCast(getOpenSimObject());
        final AbstractProperty ap = obj.get_Appearance().getPropertyByName("opacity");
        final double oldOpacity = obj.get_Appearance().get_opacity();
        final double newOpacity = opacity;
        final PropertyEditorAdaptor pea = new PropertyEditorAdaptor(getModelForNode(), obj.get_Appearance(),
        ap, this
        );
        pea.setValueDouble(newOpacity, false);
        AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {

                @Override
                public void undo() throws CannotUndoException {
                    super.undo();
                    pea.setValueDouble(oldOpacity, false);
                }

                @Override
                public String getUndoPresentationName() {
                    return "Undo opacity change";
                }

                @Override
                public void redo() throws CannotRedoException {
                    super.redo();
                    pea.setValueDouble(newOpacity, false);
                }

                @Override
                public String getRedoPresentationName() {
                    return "Redo opacity change";
                }
                
            };
        ExplorerTopComponent.addUndoableEdit(auEdit);
        
    }
    public Boolean getVisible() {
        Geometry obj = Geometry.safeDownCast(getOpenSimObject());
        return obj.get_Appearance().get_visible();
    }

    public void setVisible(Boolean newValue) {
        Geometry obj = Geometry.safeDownCast(getOpenSimObject());
        final AbstractProperty ap = obj.get_Appearance().getPropertyByName("visible");
        final boolean oldVis = obj.get_Appearance().get_visible();
        final boolean newVis = newValue;
        final Model model = getModelForNode();
        final PropertyEditorAdaptor pea = new PropertyEditorAdaptor(model, obj.get_Appearance(),
        ap, this
        );
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

    private void updateObjectDisplay(Geometry obj) {
        // Tell the world that the owner modelComponent need to regenerate
        Model mdl = getModelForNode();
        ViewDB.getInstance().getModelVisuals(mdl).upateDisplay(obj);
        //ViewDB.getInstance().repaintAll();
    }

    public DecorativeGeometry.Representation getDisplayPreference() {
        Geometry obj = Geometry.safeDownCast(getOpenSimObject());
        return obj.getRepresentation();
   }

    public void setDisplayPreference(DecorativeGeometry.Representation pref) {
        final Geometry obj = Geometry.safeDownCast(getOpenSimObject());
        final AbstractProperty ap = obj.get_Appearance().getPropertyByName("representation");
        final DecorativeGeometry.Representation oldRep = obj.get_Appearance().get_representation();
        final int newRep = pref.swigValue();
        final PropertyEditorAdaptor pea = new PropertyEditorAdaptor(getModelForNode(), obj.get_Appearance(),
        ap, this
        );
        pea.setValueInt(newRep, false);
        updateObjectDisplay(obj);
                AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {

                @Override
                public void undo() throws CannotUndoException {
                    super.undo();
                    pea.setValueInt(oldRep.swigValue(), false);
                    updateObjectDisplay(obj);
                }

                @Override
                public String getUndoPresentationName() {
                    return "Undo representation change";
                }

                @Override
                public void redo() throws CannotRedoException {
                    super.redo();
                    pea.setValueInt(newRep, false);
                    updateObjectDisplay(obj);
                }

                @Override
                public String getRedoPresentationName() {
                    return "Redo representation change";
                }
                
            };
        ExplorerTopComponent.addUndoableEdit(auEdit);

    }
}
