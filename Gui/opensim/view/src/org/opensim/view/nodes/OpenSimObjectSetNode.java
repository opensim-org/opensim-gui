/*
 *
 * OpenSimObjectSetNode
 * Author(s): Ayman Habib
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
