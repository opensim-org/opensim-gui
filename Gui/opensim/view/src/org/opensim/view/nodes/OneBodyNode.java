/* -------------------------------------------------------------------------- *
 * OpenSim: OneBodyNode.java                                                  *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Paul Mitiguy                                       *
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

import java.awt.Image;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.ArrayDouble;
import org.opensim.modeling.Body;
import org.opensim.modeling.Frame;
import org.opensim.modeling.Geometry;
import org.opensim.modeling.Marker;
import org.opensim.modeling.Model;
import org.opensim.modeling.WrapObject;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.Vec3;
import org.opensim.modeling.WrapObjectSet;
import org.opensim.threejs.BodyVisualizationJson;
import org.opensim.view.ExplorerTopComponent;

import org.opensim.view.nodes.OpenSimObjectNode.displayOption;
import org.opensim.view.pub.ViewDB;

/** Node class to wrap Body objects */
public class OneBodyNode extends OneFrameNode{
   private static ResourceBundle bundle = NbBundle.getBundle(OneBodyNode.class);
   public OneBodyNode(OpenSimObject b) {
      super(Frame.safeDownCast(b));
      // Create children for wrap objects associated with body
      Body bdy = (Body) b;
      setShortDescription(bdy.getAbsolutePathString());
      Children children = getChildren();
      // Create nodes for wrap objects      
      WrapObjectSet wrapObjects = bdy.getWrapObjectSet();
      for (int index=0; index < wrapObjects.getSize(); index++ ){
         WrapObject wrapObject = wrapObjects.get(index);
         OneWrapObjectNode node = new OneWrapObjectNode(wrapObject);
         Node[] arrNodes = new Node[1];
         arrNodes[0] = node;
         children.add(arrNodes);
         
      }
      createFrameNodes(children);
      if(children.getNodesCount()==0) setChildren(Children.LEAF);      
      addDisplayOption(displayOption.Colorable);
      addDisplayOption(displayOption.Isolatable);
      addDisplayOption(displayOption.Showable);
   }


   
   //----------------------------------------------------------------
   public static int  GetNumberOfWrapObjectsForBody( OpenSimObject openSimObject )           
   { 
      WrapObjectSet wrapObjectSet = OneBodyNode.GetWrapObjectSetOrNullForBody( openSimObject ); 
      return wrapObjectSet == null ? 0 : wrapObjectSet.getSize();
   }

   
   //----------------------------------------------------------------
   public static WrapObjectSet  GetWrapObjectSetOrNullForBody( OpenSimObject openSimObject )
   {
      if( openSimObject instanceof Body )
      {
         Body body = (Body)openSimObject;
	 WrapObjectSet wrapObjectSet = body.getWrapObjectSet();
         return wrapObjectSet;
      }
      return null;
   }

   
    /**
     * Icon for the body node 
     **/
   @Override
    public Image getIcon(int i) {
        URL imageURL = this.getClass().getResource("/org/opensim/view/nodes/icons/bodyNode.png");
        if (imageURL != null) {
            return new ImageIcon(imageURL, "Body").getImage();
        } else {
            return null;
        }
    }

   @Override
    public Image getOpenedIcon(int i) {
        return getIcon(i);
    }
    
    public Action[] getActions(boolean b) {
        Action[] superActions = (Action[]) super.getActions(b);        
        // Arrays are fixed size, onvert to a List
        
        List<Action> actions = java.util.Arrays.asList(superActions);
        // Create new Array of proper size
        Action[] retActions = new Action[actions.size()+1];
        actions.toArray(retActions);
        try {
            // append new command to the end of the list of actions
            retActions[actions.size()] = (BodyToggleCOMAction) BodyToggleCOMAction.findObject(
                     (Class)Class.forName("org.opensim.view.nodes.BodyToggleCOMAction"), true);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return retActions;
    }

    @Override
    public Sheet createSheet() {
        Sheet sheet;

        sheet = super.createSheet();
        Sheet.Set set = sheet.get(Sheet.PROPERTIES);
        // Add property for Location
        Body obj = Body.safeDownCast(getOpenSimObject());
        Model theModel = getModelForNode();
        try {
            
            // customize com
            set.remove("mass_center");
            PropertySupport.Reflection massCenterNodeProp;
            massCenterNodeProp = new PropertySupport.Reflection(this, String.class, "getCOMString", "setCOMString");
            ((Node.Property) massCenterNodeProp).setValue("oneline", Boolean.TRUE);
            ((Node.Property) massCenterNodeProp).setValue("suppressCustomEditor", Boolean.TRUE);
            massCenterNodeProp.setName("mass_center");
            massCenterNodeProp.setShortDescription(getPropertyComment("mass_center"));
            set.put(massCenterNodeProp);
   
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }

        return sheet;
    }
    public String getCOMString() {
        Body body = Body.safeDownCast(comp);
        AbstractProperty com= body.getPropertyByName("mass_center");
        return com.toString();
    }

    /**
     * @param offset the com to set
     */
    public void setCOMString(String offsetString) {
       ArrayDouble d = new ArrayDouble();
       d.fromString(offsetString);
       setCOMfromArray(d, true);
    }
    private void setCOMfromArray(final ArrayDouble newOffset, boolean enableUndo) {
        Body body = Body.safeDownCast(getOpenSimObject());
        Vec3 rOffest = body.get_mass_center();
        final ArrayDouble oldOffset = new ArrayDouble(3);
        for(int i=0; i<3; i++) oldOffset.set(i, rOffest.get(i));
        body.setMassCenter(newOffset.getAsVec3());
        Model model = body.getModel();
        UUID comUuid = ViewDB.getInstance().getModelVisualizationJson(model).getBodyRep(comp).getComObjectUUID();
        ViewDB.getInstance().setObjectTranslationInParentByUuid(comUuid, body.getMassCenter());
        refreshNode();
        if (enableUndo){
             AbstractUndoableEdit auEdit = new AbstractUndoableEdit(){
               public void undo() throws CannotUndoException {
                   super.undo();
                   setCOMfromArray(oldOffset, false);
               }
               public void redo() throws CannotRedoException {
                   super.redo();
                   setCOMfromArray(newOffset, true);
               }
            };
            ExplorerTopComponent.addUndoableEdit(auEdit);            
        }
    }
    
 

}
