/*
 * Copyright (c)  2005-2008, Stanford University
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
package org.opensim.view;

import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.view.nodes.ConcreteModelNode;
import org.opensim.view.nodes.OpenSimObjectNode;
import org.opensim.view.nodes.OpenSimObjectSetNode;
import org.opensim.view.pub.ViewDB;

public final class ObjectDisplayShowOnlyAction extends CallableSystemAction {
   
   public boolean isEnabled() {
      Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
      if (selected.length==0) return false;
      Object  parent = selected[0].getParentNode();
      boolean sameParent=true;
      for(int i=0; i < selected.length && sameParent; i++){
          sameParent = (selected[i].getParentNode().equals(parent));
      }
      return sameParent;
   }
   
   public void performAction() {
      // TODO implement action body
      Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
      // The general scenario is to hide parent then show node and children
      // one exception is if the parent node is a model since hiding it in this case
      // makes it impossible to show selected node. This sitution is handled by overriding the behavior
      // in appropriate nodes.
      for(int i=0; i < selected.length; i++){
         if (!(selected[i] instanceof OpenSimObjectNode))
                continue;
         
         OpenSimObjectNode objectNode = (OpenSimObjectNode) selected[i];
         Object  parent = objectNode.getParentNode();
         if (parent instanceof OpenSimObjectNode){
            OpenSimObjectNode parentNode = (OpenSimObjectNode) parent;
            // For Actuators we want to go up to the ActuatorSet node rather than the group node
            // A cleaner solution would be to make nodes hold pointer to which node would
            // show only be relative to.
            while (!(parentNode instanceof OpenSimObjectSetNode) && 
                    !(parentNode instanceof ConcreteModelNode))
                parentNode = (OpenSimObjectNode) (parentNode.getParentNode());
            
            if (parentNode instanceof OpenSimObjectSetNode){
                OpenSimObjectSetNode setNode=(OpenSimObjectSetNode) parentNode;
                Children children=setNode.getChildren();
                Node[] childNodes = children.getNodes();
                for(int j=0; j<childNodes.length; j++){
                    OpenSimObjectNode n = (OpenSimObjectNode) childNodes[j];
                    ViewDB.getInstance().toggleObjectsDisplay(n.getOpenSimObject(), false);                    
                }
            }
            else
                ViewDB.getInstance().toggleObjectsDisplay(parentNode.getOpenSimObject(), false);
         }
      }
      for(int i=0; i < selected.length; i++){
         if (!(selected[i] instanceof OpenSimObjectNode))
                continue;
         OpenSimObjectNode objectNode = (OpenSimObjectNode) selected[i];
         ViewDB.getInstance().toggleObjectDisplay(objectNode.getOpenSimObject(), true);
      }
      ViewDB.getInstance().repaintAll();
   }
   
   public String getName() {
      return NbBundle.getMessage(ObjectDisplayShowOnlyAction.class, "CTL_ObjectDisplayShowOnlyAction");
   }
   
   protected void initialize() {
      super.initialize();
      // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
      putValue("noIconInMenu", Boolean.TRUE);
   }
   
   public HelpCtx getHelpCtx() {
      return HelpCtx.DEFAULT_HELP;
   }
   
   protected boolean asynchronous() {
      return false;
   }
   
}
