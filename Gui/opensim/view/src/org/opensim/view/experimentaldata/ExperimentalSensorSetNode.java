/* -------------------------------------------------------------------------- *
 * OpenSim: ExperimentalSensorSetNode.java                                    *
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
 * ExperimentalDataObject.java
 *
 * Created on March 10, 2009, 10:56 AM
 *
 *
 *
 */
package org.opensim.view.experimentaldata;

import java.awt.Color;
import java.awt.Image;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.opensim.modeling.Vec3;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.motions.MotionDisplayer;
import org.opensim.view.nodes.*;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author ayman
 */
public class ExperimentalSensorSetNode extends OpenSimNode {
    private static ResourceBundle bundle = NbBundle.getBundle(ExperimentalSensorSetNode.class);
    private static String nodeName;
    AnnotatedMotion dMotion;
    private MotionDisplayer motionDisplayer;

    /** Creates a new instance of ExperimentalSensorNode */
    public ExperimentalSensorSetNode(AnnotatedMotion dMotion) {
        nodeName=bundle.getString("SENSORSET_NODE_NAME");
        setName(nodeName);
        setDisplayName(nodeName);
        setShortDescription(bundle.getString("HINT_ExperimentalSensorSetNode"));
        this.dMotion=dMotion;
        createChildren();
        //motionDisplayer= dMotion.getMotionDisplayer();
        //markersDisplayer = motionDisplayer.getSensorsRep();
    }
    
    public String getHtmlDisplayName() {
        
        return nodeName;
    }
   /**
    * Icon for the node, same as OpenSimNode
    **/
   public Image getIcon(int i) {
      URL imageURL=null;
      try {
         imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/motionsNode.png");
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
         imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/motionsNode.png");
      } catch (ClassNotFoundException ex) {
         ex.printStackTrace();
      }
      if (imageURL != null) {
         return new ImageIcon(imageURL, "").getImage();
      } else {
         return null;
      }
   }

    private void createChildren() {
        Vector<ExperimentalDataObject> allMotionObjects = dMotion.getClassified();
        // Create an ExperimentalSensorNode for each 
        if (allMotionObjects==null) return;
        for(ExperimentalDataObject dObject:allMotionObjects){
            if (dObject.getObjectType()==ExperimentalDataItemType.OrientationData){
                getChildren().add(new Node[]{new ExperimentalSensorNode(dObject, dMotion)});
            }
        }
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

            PropertySupport.Reflection nextNodeProp2= new PropertySupport.Reflection(this, double.class, "getSensorRadius", "setSensorRadiusUI");
            nextNodeProp2.setName("marker radius (mm)");
            nextNodeProp2.setShortDescription("Number to scale current visualization with");
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
        motionDisplayer.setDefaultExperimentalSensorColor(color);
        
        refreshNode();
    }
    public void setColorUI(final Color color) {
        setColorUI(color, true);
    }
    
    public Color getColor()
    {
        if (motionDisplayer==null){
            motionDisplayer = dMotion.getMotionDisplayer();
        }
        Vec3 colorAsVec3 =  motionDisplayer.getDefaultExperimentalSensorColor();
        return new Color((float)colorAsVec3.get(0), (float)colorAsVec3.get(1), (float)colorAsVec3.get(2));
    }
   
    public void setSensorRadiusUI(double newRadius) {
        setSensorRadiusUI(newRadius, true);
    }
    
    void setSensorRadiusUI(final double newRadius, boolean allowUndo)
    {
        final double oldSensorSize = getSensorRadius();
        if (allowUndo){
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit(){
               @Override
               public void undo() throws CannotUndoException {
                   super.undo();
                   setSensorRadiusUI(oldSensorSize, false);
               }
               @Override
               public void redo() throws CannotRedoException {
                   super.redo();
                   setSensorRadiusUI(newRadius, false);
               }
            };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }       
        motionDisplayer.setExperimentalSensorRadius(newRadius);
        
        refreshNode();
    }

    public double getSensorRadius()
    {
       if (motionDisplayer==null){
            motionDisplayer = dMotion.getMotionDisplayer();
       }
        return motionDisplayer.getExperimentalSensorRadius();
    }
*/
}
