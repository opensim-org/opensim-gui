/* -------------------------------------------------------------------------- *
 * OpenSim: OneGeometryNode.java                                              *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib                                                     *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain a  *
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0          *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 * -------------------------------------------------------------------------- */
/*
 *
 * OneContactGeometryNode
 * Author(s): Ayman Habib
 */
package org.opensim.view.nodes;

import java.awt.Image;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import static org.openide.nodes.Sheet.createExpertSet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.opensim.modeling.Frame;
import org.opensim.modeling.Geometry;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.PhysicalOffsetFrame;
import org.opensim.modeling.State;
import org.opensim.modeling.Transform;
import org.opensim.view.nodes.OpenSimObjectNode.displayOption;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman Habib
 */
public class OneGeometryNode extends OneComponentWithGeometryNode {
    
    private static ResourceBundle bundle = NbBundle.getBundle(OneGeometryNode.class);
    State state;
    OpenSimContext context;
    /**
    * Creates a new instance of OneContactForceNode
    */
    public OneGeometryNode(Geometry cg) {
        super(cg);
        String gName=cg.getName();
        if (cg.getName().equalsIgnoreCase("")){
            gName=cg.getName();
            setDisplayName(cg.getAbsolutePathString());
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
    public Sheet createSheet() {
        Sheet sheet;
        sheet = super.createSheet();
        addFrameProperties(sheet);
        return sheet;
    }

    private void addFrameProperties(Sheet sheet) {
        Sheet.Set frameSheet = createExpertSet();
        frameSheet.setDisplayName("Attachment Frame");
        sheet.put(frameSheet);
        Geometry g = Geometry.safeDownCast(comp);
        Frame frame = g.getFrame();
        PhysicalOffsetFrame offsetFrame = PhysicalOffsetFrame.safeDownCast(frame);
        // Add dropdown of existing Frames

        PropertySupport.Reflection nextNodeProp;
        try {
            nextNodeProp = new PropertySupport.Reflection(this,
                    String.class,
                    "getFrameName",
                    "setFrameName");
            nextNodeProp.setValue("canEditAsText", Boolean.TRUE);
            nextNodeProp.setValue("suppressCustomEditor", Boolean.TRUE);
            nextNodeProp.setName("Frame");
            PropertyEditorSupport editor = EditorRegistry.getEditor("Frame");
            if (editor != null)
                nextNodeProp.setPropertyEditorClass(editor.getClass());
            frameSheet.put(nextNodeProp);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (offsetFrame!=null){ 
            try {
                // Expose traslations and rotatiosn
                PropertySupport.Reflection translationProp = new PropertySupport.Reflection(this,
                        String.class, "getTranslationString", "setTranslationString");
                translationProp.setValue("canEditAsText", Boolean.TRUE);
                translationProp.setDisplayName("Translation");
                translationProp.setValue("suppressCustomEditor", Boolean.TRUE);
                frameSheet.put(translationProp);
               
                PropertySupport.Reflection rotationProp = new PropertySupport.Reflection(this,
                        String.class, "getRotationString", "setRotationString");
                rotationProp.setValue("canEditAsText", Boolean.TRUE);
                rotationProp.setDisplayName("Rotation");
                rotationProp.setValue("suppressCustomEditor", Boolean.TRUE);
                frameSheet.put(rotationProp);
            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        sheet.put(frameSheet);
    }
    
    public String getFrameName() {
        Geometry g = Geometry.safeDownCast(comp);
        return g.getFrame().getName();
    }
    public void setFrameName(String frame) {
        Geometry g = Geometry.safeDownCast(comp);
        //g.setFrameName(frame);
    }
    
    public String getTranslationString() {
        Geometry g = Geometry.safeDownCast(comp);
        PhysicalOffsetFrame offsetFrame = PhysicalOffsetFrame.safeDownCast(g.getFrame());
        Transform transform = offsetFrame.getOffsetTransform();
        String translationVec3AsString = transform.p().toString();
        return translationVec3AsString.substring(2, translationVec3AsString.length()-1).replace(',', ' ');
    }
    public void setTranslationString(String string) {
        Geometry g = Geometry.safeDownCast(comp);
        PhysicalOffsetFrame offsetFrame = PhysicalOffsetFrame.safeDownCast(g.getFrame());
        String vals[] = string.split(" ");
        context = OpenSimDB.getInstance().getContext(getModelForNode());
        context.cacheModelAndState();
        //offsetFrame.upd_orientation(0);
        for (int i=0; i<3; i++)
            offsetFrame.upd_translation().set(i, Double.valueOf(vals[i]));
        refreshDisplay();
        return;
    }
    
    public String getRotationString() {
        Geometry g = Geometry.safeDownCast(comp);
        PhysicalOffsetFrame offsetFrame = PhysicalOffsetFrame.safeDownCast(g.getFrame());
        Transform transform = offsetFrame.getOffsetTransform();
        String rotationVec3AsString = transform.R().convertRotationToBodyFixedXYZ().toString();
        return rotationVec3AsString.substring(2, rotationVec3AsString.length()-1).replace(',', ' ');
    }
    public void setRotationString(String string) {
        Geometry g = Geometry.safeDownCast(comp);
        PhysicalOffsetFrame offsetFrame = PhysicalOffsetFrame.safeDownCast(g.getFrame());
        String vals[] = string.split(" ");
        context = OpenSimDB.getInstance().getContext(getModelForNode());
        context.cacheModelAndState();
        //offsetFrame.upd_orientation(0);
        for (int i=0; i<3; i++)
            offsetFrame.upd_orientation().set(i, Double.valueOf(vals[i]));
        refreshDisplay();
        return;
    }

    private void refreshDisplay() {
        try {
            context.restoreStateFromCachedModel();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        ViewDB.getInstance().updateModelDisplay(getModelForNode());
        ViewDB.repaintAll();
    }
}
