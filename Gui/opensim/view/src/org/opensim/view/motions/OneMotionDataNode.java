/*
 *
 * OneMotionNode
 * Author(s): Ayman Habib & Jeff Reinbolt
 * Copyright (c)  2005-2006, Stanford University, Ayman Habib & Jeff Reinbolt
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
