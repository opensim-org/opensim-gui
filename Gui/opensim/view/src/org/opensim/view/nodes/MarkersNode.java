/* -------------------------------------------------------------------------- *
 * OpenSim: MarkersNode.java                                                  *
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
package org.opensim.view.nodes;

import java.awt.Color;
import java.awt.Image;
import java.net.URL;
import java.util.List;
import javax.swing.ImageIcon;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.opensim.modeling.Marker;
import org.opensim.modeling.MarkerSet;
import org.opensim.modeling.Model;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.MarkersDisplayer;
import org.opensim.view.markerEditor.MarkersSaveToFileAction;
import org.opensim.view.markerEditor.MarkersLoadFromFileAction;
import org.opensim.view.markerEditor.NewMarkerAction;
import org.opensim.view.nodes.OpenSimObjectNode.displayOption;
import org.opensim.view.pub.ViewDB;

/**
 * Node class to wrap Model's collection of markers
 */
public class MarkersNode extends OpenSimObjectSetNode {
    private static ResourceBundle bundle = NbBundle.getBundle(MarkersNode.class);
    //private MarkersDisplayer markersDisplayer;
    private Model model;
    public MarkersNode(MarkerSet markerSet, Model m) {
        super(markerSet);
        setDisplayName(NbBundle.getMessage(MarkersNode.class, "CTL_Markers"));
        updateChildNodes(markerSet);
        model=m;
        //markersDisplayer=null;
        addDisplayOption(displayOption.Isolatable);
        addDisplayOption(displayOption.Showable);
    }

    public void updateChildNodes(MarkerSet markerSet) {
        for (int markerNum=0; markerNum < markerSet.getSize(); markerNum++ ){

            Marker marker = markerSet.get(markerNum);
            Children children = getChildren();

            OneMarkerNode node = new OneMarkerNode(marker);
            Node[] arrNodes = new Node[1];
            arrNodes[0] = node;
            children.add(arrNodes);
        }
    }

      public Image getIcon(int i) {
      URL imageURL=null;
      try {
         imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/markersNode.png");
      } catch (ClassNotFoundException ex) {
         ex.printStackTrace();
      }
      if (imageURL != null) {
         return new ImageIcon(imageURL, "").getImage();
      } else {
         return null;
      }
   }
   
   public Image getOpenedIcon(int i) {
      URL imageURL=null;
      try {
         imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/markersNode.png");
      } catch (ClassNotFoundException ex) {
         ex.printStackTrace();
      }
      if (imageURL != null) {
         return new ImageIcon(imageURL, "").getImage();
      } else {
         return null;
      }
   }
   
    /**
     * Display name 
     */
    public String getHtmlDisplayName() {
        return "Markers";
    }

    public Action[] getActions(boolean b) {
        // Get actions from parent (generic object menu for review, display)
        Action[] superActions = (Action[]) super.getActions(b);        
        // Arrays are fixed size, onvert to a List
        List<Action> actions = java.util.Arrays.asList(superActions);
        // Create new Array of proper size
        Action[] retActions = new Action[actions.size()+3];
        actions.toArray(retActions);
        // append new command to the end of the list of actions
        retActions[actions.size()] = new NewMarkerAction();
        retActions[actions.size()+1] = new MarkersSaveToFileAction();
        retActions[actions.size()+2] = new MarkersLoadFromFileAction();
        return retActions;
    }
    /*
    @Override
    public Sheet createSheet() {
        Sheet defaultSheet = super.createSheet();
        try {
            Sheet.Set set = defaultSheet.get(Sheet.PROPERTIES);
            PropertySupport.Reflection nextNodeProp = new PropertySupport.Reflection(this, Color.class, "getColor", "setColorUI");
            nextNodeProp.setName("marker color");
            set.put(nextNodeProp);

            PropertySupport.Reflection nextNodeProp2= new PropertySupport.Reflection(this, double.class, "getScaleFactor", "setScaleFactorUI");
            nextNodeProp2.setName("marker size");
            set.put(nextNodeProp2);

            return defaultSheet;
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        return defaultSheet;
   }
   
    void setColorUI(final Color color, boolean allowUndo) {
        final Color oldColor = getColor();
        if (allowUndo){
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit(){
                @Override
               public void undo() throws CannotUndoException {
                   super.undo();
                   setColorUI(oldColor, false);
               }
                @Override
               public void redo() throws CannotRedoException {
                   super.redo();
                   setColorUI(color, true);
               }
            };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }
        markersDisplayer.setColor(color);
        ViewDB.repaintAll();
        refreshNode();
    }
    public void setColorUI(final Color color) {
        setColorUI(color, true);
    }
    
    public Color getColor()
    {
        if (markersDisplayer==null)
            markersDisplayer = ViewDB.getInstance().getModelVisuals(model).getMarkersRep();
        return markersDisplayer.getColor();
    }
   
    public void setScaleFactorUI(double newFactor) {
        setScaleFactorUI(newFactor, true);
    }
    
    void setScaleFactorUI(final double newFactor, boolean allowUndo)
    {
        final double oldScaleFactor = getScaleFactor();
        if (allowUndo){
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit(){
               @Override
               public void undo() throws CannotUndoException {
                   super.undo();
                   setScaleFactorUI(oldScaleFactor, false);
               }
               @Override
               public void redo() throws CannotRedoException {
                   super.redo();
                   setScaleFactorUI(newFactor, true);
               }
            };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }       
        markersDisplayer.setScaleFactor(newFactor);
        ViewDB.repaintAll();
        refreshNode();
    }

    public double getScaleFactor()
    {
        if (markersDisplayer==null)
            markersDisplayer = ViewDB.getInstance().getModelVisuals(model).getMarkersRep();
        return markersDisplayer.getScaleFactor();
    }
    */
} // class MarkersNode
