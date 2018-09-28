/* -------------------------------------------------------------------------- *
 * OpenSim: OneFrameNode.java                                                 *
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
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensim.view.nodes;

import java.awt.Image;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.opensim.modeling.Body;
import org.opensim.modeling.Component;
import org.opensim.modeling.ComponentIterator;
import org.opensim.modeling.ComponentsList;
import org.opensim.modeling.Frame;
import org.opensim.modeling.FrameGeometry;
import org.opensim.modeling.Geometry;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.PhysicalOffsetFrame;
import org.opensim.modeling.State;
import org.opensim.modeling.Transform;
import org.opensim.view.FrameToggleVisibilityAction;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman
 */
public class OneFrameNode extends OneModelComponentNode {
    private static ResourceBundle bundle = NbBundle.getBundle(OneFrameNode.class);
    Frame frame;
    State state;
    OpenSimContext context;
    
    public OneFrameNode(Frame comp) {
        super(comp);
        frame = comp;
        setShortDescription(bundle.getString("HINT_FrameNode"));
        createGeometryNodes();        
        
    }
   
    protected final void createGeometryNodes() {
        // attached_geometry doesn't reach geometry mounted on 
        // offset frames, use iterator instead
        // Caveat if nested bodies or components have geometry 
        // it may appear multiple times
        ComponentsList compList = frame.getComponentsList();
        ComponentIterator compIter = compList.begin();
        Children children = getChildren();
        while (!compIter.equals(compList.end())) {
            Component comp = compIter.__deref__();
            Geometry oneG = Geometry.safeDownCast(comp);
            if (oneG != null && FrameGeometry.safeDownCast(oneG) == null) {
                Frame gFrame = oneG.getFrame();
                if (gFrame.equals(frame)){
                    OneGeometryNode node = new OneGeometryNode(oneG);
                    Node[] arrNodes = new Node[1];
                    arrNodes[0] = node;
                    children.add(arrNodes);
                }
            }
            compIter.next();
        }
    }
    
   @Override
    public Node cloneNode() {
        return new OneFrameNode((Frame)getOpenSimObject());
    }
    /**
     * Icon for the body node 
     **/
   @Override
    public Image getIcon(int i) {
        URL imageURL = this.getClass().getResource("/org/opensim/view/nodes/icons/body.png");
        if (imageURL != null) {
            return new ImageIcon(imageURL, "Frame").getImage();
        } else {
            return null;
        }
    }

   @Override
    public Image getOpenedIcon(int i) {
        return getIcon(i);
    }
    
   @Override
    public Action[] getActions(boolean b) {
        Action[] superActions = (Action[]) super.getActions(b);
        // Arrays are fixed size, onvert to a List
        List<Action> actions = java.util.Arrays.asList(superActions);
        // Create new Array of proper size
        Action[] retActions = new Action[actions.size() + 1];
        actions.toArray(retActions);
        try {
            // append new command to the end of the list of actions
            retActions[actions.size()] = (FrameToggleVisibilityAction) FrameToggleVisibilityAction.findObject(
                    (Class) Class.forName("org.opensim.view.FrameToggleVisibilityAction"), true);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return retActions;
    }

    public String getTranslationString() {
        Frame frame = Frame.safeDownCast(comp);
        PhysicalOffsetFrame offsetFrame = PhysicalOffsetFrame.safeDownCast(frame);
        Transform transform = offsetFrame.getOffsetTransform();
        String translationVec3AsString = transform.p().toString();
        return translationVec3AsString.substring(2, translationVec3AsString.length()-1).replace(',', ' ');
    }
    public void setTranslationString(String string) {
        Frame frame = Frame.safeDownCast(comp);
        PhysicalOffsetFrame offsetFrame = PhysicalOffsetFrame.safeDownCast(frame);
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
        PhysicalOffsetFrame offsetFrame = PhysicalOffsetFrame.safeDownCast(frame);
        Transform transform = offsetFrame.getOffsetTransform();
        String rotationVec3AsString = transform.R().convertRotationToBodyFixedXYZ().toString();
        return rotationVec3AsString.substring(2, rotationVec3AsString.length()-1).replace(',', ' ');
    }
    public void setRotationString(String string) {
        Frame frame = Frame.safeDownCast(comp);
        PhysicalOffsetFrame offsetFrame = PhysicalOffsetFrame.safeDownCast(frame);
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
        Model model = getModelForNode();
        Frame frame = Frame.safeDownCast(comp);
        ViewDB.getInstance().updateDecorations(model, frame);
        ViewDB.getInstance().updateModelDisplay(model);
        
    }
    
   @Override
    public Sheet createSheet() {
        Sheet sheet;
        sheet = super.createSheet();
        addFrameProperties(sheet);
        return sheet;
    }
    /*
     Custom handlers of Edits to translation and orientation to force updates to scenegraph
    */
    private void addFrameProperties(Sheet sheet) {
        Frame frame = Frame.safeDownCast(comp);
        PhysicalOffsetFrame offsetFrame = PhysicalOffsetFrame.safeDownCast(frame);
        Sheet.Set sheetSet = sheet.get(Sheet.PROPERTIES);
        if (offsetFrame!=null){ 
            sheetSet.remove("translation");
            sheetSet.remove("orientation");
            try {
                // Expose traslations and rotatiosn
                PropertySupport.Reflection translationProp = new PropertySupport.Reflection(this,
                        String.class, "getTranslationString", "setTranslationString");
                translationProp.setValue("canEditAsText", Boolean.TRUE);
                translationProp.setDisplayName("Translation");
                translationProp.setValue("suppressCustomEditor", Boolean.TRUE);
                sheetSet.put(translationProp);
               
                PropertySupport.Reflection rotationProp = new PropertySupport.Reflection(this,
                        String.class, "getRotationString", "setRotationString");
                rotationProp.setValue("canEditAsText", Boolean.TRUE);
                rotationProp.setDisplayName("Rotation");
                rotationProp.setValue("suppressCustomEditor", Boolean.TRUE);
                sheetSet.put(rotationProp);
            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
