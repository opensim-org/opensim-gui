/* -------------------------------------------------------------------------- *
 * OpenSim: OneAssociatedMotionNode.java                                      *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Jeff Reinbolt                                      *
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
 * OneMotionNode
 * Author(s): Ayman Habib & Jeff Reinbolt
 */
package org.opensim.view.motions;

import java.awt.Color;
import java.awt.Image;
import java.net.URL;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.opensim.modeling.Model;
import org.opensim.modeling.Storage;
import org.opensim.view.experimentaldata.AnnotatedMotion;
import org.opensim.view.experimentaldata.ExperimentalForceSetNode;
import org.opensim.view.experimentaldata.ExperimentalMarkerSetNode;
import org.opensim.view.experimentaldata.ExperimentalSensorSetNode;
import org.opensim.view.experimentaldata.MotionEditMotionObjectsAction;

/**
 *
 * @author Ayman 
 * 
 * A class representing a dataset to be associated with an existing motion, usetrs can control how tro display 
 * the contents of the dataset but are not allowed to change it.
 */
public class OneAssociatedMotionNode extends OneMotionNode {
    /** Creates a new instance of OneMotionNode */
   public OneAssociatedMotionNode(AnnotatedMotion motion) {
      super(motion);
       createChildren(motion);
   }
   
   public Image getIcon(int i) {
      URL imageURL=null;
      try {
         imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/motionNode.png");
      } catch (ClassNotFoundException ex) {
         ex.printStackTrace();
      }
      if (imageURL != null) {
         return new ImageIcon(imageURL, "").getImage();
      } else {
         return null;
      }
//      Image retValue;
//      
//      retValue = super.getIcon(i);
//      return retValue;
   }
   
   public Model getModel()
   {
       return getModelForNode();
   }

   public Storage getMotion()
   {
      return (Storage)getOpenSimObject();
   }

    public Action[] getActions(boolean b) {
        Action[] retValue=null;
        try {
            //boolean isCurrent = MotionsDB.getInstance().isModelMotionPairCurrent(new MotionsDB.ModelMotionPair(getModel(), getMotion()));
            retValue = new Action[]{
                (MotionsCloseAction) MotionsCloseAction.findObject(
                     (Class)Class.forName("org.opensim.view.motions.MotionsCloseAction"), true),
                (MotionEditMotionObjectsAction) MotionEditMotionObjectsAction.findObject(
                     (Class)Class.forName("org.opensim.view.experimentaldata.MotionEditMotionObjectsAction"), true)
            };
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
        return retValue;
    }
    
    public String getHtmlDisplayName() {
        String retValue;
        
        retValue = super.getHtmlDisplayName();
        MotionsDB.ModelMotionPair modelMotionPair = new MotionsDB.ModelMotionPair(getModel(), getMotion());
        if (MotionsDB.getInstance().isModelMotionPairCurrent(modelMotionPair))
           retValue = "<b>"+retValue+"</b>";
        return retValue;
    }

    private void createChildren(AnnotatedMotion dMotion) {
        Vector<String> names=null;
        names =dMotion.getMarkerNames();
        if (names != null && names.size()>0){ // File had markers
            getChildren().add(new Node[]{new ExperimentalMarkerSetNode(dMotion)});
        }
        // Now Forces
        names =dMotion.getForceNames();
        if (names !=null && names.size()>0){ // File had forces
             getChildren().add(new Node[]{new ExperimentalForceSetNode(dMotion)});
        }
        // Sensors
        names = dMotion.getSensorNames();
        if (names !=null && names.size()>0){ // File had sensors
             getChildren().add(new Node[]{new ExperimentalSensorSetNode(dMotion)});
        }
    }

   public Action getPreferredAction() {
      Action act=null;
      try {
         act =(MotionsSetCurrentAction) MotionsSetCurrentAction.findObject(
                    (Class)Class.forName("org.opensim.view.motions.MotionsSetCurrentAction"), true);
      } catch(ClassNotFoundException e){
         
 }
      return act;
   }

    @Override
    public Sheet createSheet() {
        Sheet defaultSheet = super.createSheet();
        AnnotatedMotion dMotion = (AnnotatedMotion)this.getOpenSimObject();
        try {
            Sheet.Set set = defaultSheet.get(Sheet.PROPERTIES);
            PropertySupport.Reflection nextNodeProp = new PropertySupport.Reflection(dMotion.getMotionDisplayer(), Color.class, "getDefaultForceColor", "setDefaultForceColor");
            nextNodeProp.setName("force color");
            set.put(nextNodeProp);

            PropertySupport.Reflection nextNodeProp2= new PropertySupport.Reflection(dMotion, double.class, "getDisplayForceScale", "setDisplayForceScale");
            nextNodeProp2.setName("Force display size");
            set.put(nextNodeProp2);

            PropertySupport.Reflection nextNodeProp3= new PropertySupport.Reflection(dMotion, String.class, "getDisplayForceShape", "setDisplayForceShape");
            nextNodeProp3.setName("Force display shape");
            set.put(nextNodeProp3);

            return defaultSheet;
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        return defaultSheet;
    }
   
}
