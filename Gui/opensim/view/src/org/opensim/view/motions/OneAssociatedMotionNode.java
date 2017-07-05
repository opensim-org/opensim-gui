/*
 *
 * OneMotionNode
 * Author(s): Ayman Habib & Jeff Reinbolt
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
