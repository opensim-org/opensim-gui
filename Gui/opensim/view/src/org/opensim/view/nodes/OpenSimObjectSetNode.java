/* -------------------------------------------------------------------------- *
 * OpenSim: OpenSimObjectSetNode.java                                         *
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
 * OpenSimObjectSetNode
 * Author(s): Ayman Habib
 */
package org.opensim.view.nodes;

import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.ObjectDisplayHideAction;
import org.opensim.view.ObjectSetDisplayMenuAction;
import org.opensim.view.actions.ObjectDisplaySelectAction;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman. A node backed by an OpenSim Set
 */
public class OpenSimObjectSetNode extends OpenSimObjectNode {
    
    private OpenSimObject openSimObject;
    
    /** Creates a new instance of OpenSimObjectNode */
    public OpenSimObjectSetNode(OpenSimObject obj) {
        super(obj);
       this.openSimObject = obj;
        setDisplayName(obj.getName());
     }
    /**
     * Display name 
     */
    public String getHtmlDisplayName() {
        
        return getOpenSimObject().getName() ;
    }

    /**
     * Action to be invoked on double clicking.
     */
    public Action getPreferredAction() {
       if (getValidDisplayOptions().size()==0)  // Nothing to show or hide.
           return null;

         // Collect objects from children and obtain their visibility status
         Children children = getChildren();
         Node[] theNodes = children.getNodes();
         int collectiveStatus = 3;  // unknown;
         for(int i=0; i<theNodes.length; i++){
            Node node = theNodes[i];
            if (!(node instanceof OpenSimObjectNode))
               continue;
            // Cycle thru children and get their display status, 
            int currentStatus=ViewDB.getInstance().getDisplayStatus(((OpenSimObjectNode) node).getOpenSimObject());
            if (collectiveStatus==3){
               collectiveStatus=currentStatus;
            }
            else {
               if (currentStatus!=collectiveStatus)
                  collectiveStatus=2;  // mixed;
            }
         }
         if (collectiveStatus==3)   
            return null;
         if (collectiveStatus==2) collectiveStatus=0;  // Assume hidden if mixed
         
         try {
            // 2 for mixed, some shown some hidden, pick show
            return ((ObjectDisplaySelectAction) ObjectDisplaySelectAction.findObject(
            (Class)Class.forName("org.opensim.view.actions.ObjectDisplaySelectAction"), true));
         } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
         }
            
         return null;
    }
          
    /**
     * Return the list of available actions.
     * Subclasses should user super.getActions() to use this
     */
    public Action[] getActions(boolean b) {
      Action[] objectNodeActions;
      try {
         objectNodeActions = new Action[]  {
                                          (ObjectSetDisplayMenuAction) ObjectSetDisplayMenuAction.findObject(
                 (Class)Class.forName("org.opensim.view.ObjectSetDisplayMenuAction"), true),
                 //SystemAction.get(NewAction.class)
         };
      } catch (ClassNotFoundException ex) {
         ex.printStackTrace();
         objectNodeActions = new Action[] {null};
      }
      return objectNodeActions;
    }
    /**
     * return the Object presented by this node
     */
    public OpenSimObject getOpenSimObject() {
        return openSimObject;
    }

    @Override
    public void updateSelfFromObject() {
        
       
    }

    
}
