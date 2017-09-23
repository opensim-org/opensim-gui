/* -------------------------------------------------------------------------- *
 * OpenSim: OneMotionDataNode.java                                            *
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
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.opensim.modeling.Model;
import org.opensim.modeling.Storage;
import org.opensim.view.experimentaldata.AnnotatedMotion;
import org.opensim.view.experimentaldata.ExperimentalDataItemType;
import org.opensim.view.experimentaldata.ExperimentalDataObject;
import org.opensim.view.experimentaldata.ExperimentalForceSetNode;
import org.opensim.view.experimentaldata.ExperimentalMarkerSetNode;
import org.opensim.view.experimentaldata.ExperimentalOtherDataSetNode;
import org.opensim.view.experimentaldata.MotionEditMotionObjectsAction;
import org.opensim.view.experimentaldata.MotionReclassifyAction;
import org.opensim.view.nodes.*;

/**
 *
 * @author Ayman
 */
public class OneMotionDataNode extends OneMotionNode {
    private boolean experimental=true;
    /** Creates a new instance of OneMotionNode */
   public OneMotionDataNode(Storage motion) {
      super(motion);
      //setChildren(Children.LEAF);
      if (motion instanceof AnnotatedMotion){
          // Motion is experimental data, may need to add some levels underneath.
          AnnotatedMotion dMotion= (AnnotatedMotion) motion;
          createChildren(dMotion);
      }

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
                isExperimental()?null:
                (MotionRenameAction) MotionRenameAction.findObject(
                     (Class)Class.forName("org.opensim.view.motions.MotionRenameAction"), true),
                 isExperimental()?null:
                (MotionAssociateMotionAction) MotionAssociateMotionAction.findObject(
                     (Class)Class.forName("org.opensim.view.motions.MotionAssociateMotionAction"), true),
                (MotionsSynchronizeAction) MotionsSynchronizeAction.findObject(
                     (Class)Class.forName("org.opensim.view.motions.MotionsSynchronizeAction"), true),
                (MotionsSaveAsAction) MotionsSaveAsAction.findObject(
                     (Class)Class.forName("org.opensim.view.motions.MotionsSaveAsAction"), true),
                 isExperimental()?null:
                (MotionsCloseAction) MotionsCloseAction.findObject(
                     (Class)Class.forName("org.opensim.view.motions.MotionsCloseAction"), true),
                (isExperimental())?
                    (MotionReclassifyAction) MotionReclassifyAction.findObject(
                     (Class)Class.forName("org.opensim.view.experimentaldata.MotionReclassifyAction"), true)
                     :null,
               (isExperimental())?
                    (MotionEditMotionObjectsAction) MotionEditMotionObjectsAction.findObject(
                     (Class)Class.forName("org.opensim.view.experimentaldata.MotionEditMotionObjectsAction"), true)
                     :null,
                
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
        /*
        // Things other than markers and forces
        Vector<ExperimentalDataObject> all = dMotion.getClassified();
        if (names !=null&& names.size()>0){  // Everything else
            for(ExperimentalDataObject obj:all){
                boolean other = (obj.getObjectType()!=ExperimentalDataItemType.MarkerData) &&
                        (obj.getObjectType()!=ExperimentalDataItemType.ForceAndPointData);
                if (other){
                    // Create a parent node for OtherData
                    getChildren().add(new Node[]{new ExperimentalOtherDataSetNode(dMotion)}); 
                    break;
               }
            }
        }*/
    }

    public boolean isExperimental() {
        return experimental;
    }

    public void setExperimental(boolean experimental) {
        this.experimental = experimental;
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
