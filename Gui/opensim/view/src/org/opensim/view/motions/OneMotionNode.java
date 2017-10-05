/* -------------------------------------------------------------------------- *
 * OpenSim: OneMotionNode.java                                                *
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

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.Action;
import org.opensim.modeling.Model;
import org.opensim.modeling.Storage;
import org.opensim.view.nodes.*;

/**
 *
 * @author Ayman
 */
public class OneMotionNode extends OpenSimObjectNode {
     /** Creates a new instance of OneMotionNode */
   public OneMotionNode(Storage motion) {
      super(motion);
      //setChildren(Children.LEAF);
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
                (MotionsSetCurrentAction) MotionsSetCurrentAction.findObject(
                     (Class)Class.forName("org.opensim.view.motions.MotionsSetCurrentAction"), true),
                (MotionRenameAction) MotionRenameAction.findObject(
                     (Class)Class.forName("org.opensim.view.motions.MotionRenameAction"), true),
                (MotionAssociateMotionAction) MotionAssociateMotionAction.findObject(
                     (Class)Class.forName("org.opensim.view.motions.MotionAssociateMotionAction"), true),
                (MotionCustomColorByAction) MotionCustomColorByAction.findObject(
                     (Class)Class.forName("org.opensim.view.motions.MotionCustomColorByAction"), true),                
                (MotionsSynchronizeAction) MotionsSynchronizeAction.findObject(
                     (Class)Class.forName("org.opensim.view.motions.MotionsSynchronizeAction"), true),
                (MotionsSaveAsAction) MotionsSaveAsAction.findObject(
                     (Class)Class.forName("org.opensim.view.motions.MotionsSaveAsAction"), true),
                (MotionsCloseAction) MotionsCloseAction.findObject(
                     (Class)Class.forName("org.opensim.view.motions.MotionsCloseAction"), true),
                /*
                (isExperimental())?
                    (MotionReclassifyAction) MotionReclassifyAction.findObject(
                     (Class)Class.forName("org.opensim.view.experimentaldata.MotionReclassifyAction"), true)
                     :null,
               (isExperimental())?
                    (MotionEditMotionObjectsAction) MotionEditMotionObjectsAction.findObject(
                     (Class)Class.forName("org.opensim.view.experimentaldata.MotionEditMotionObjectsAction"), true)
                     :null,
                */
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
    
    public Action getPreferredAction() {
      Action act=null;
      try {
         act =(MotionsSetCurrentAction) MotionsSetCurrentAction.findObject(
                    (Class)Class.forName("org.opensim.view.motions.MotionsSetCurrentAction"), true);
      } catch(ClassNotFoundException e){
         
 }
      return act;
   }
}
