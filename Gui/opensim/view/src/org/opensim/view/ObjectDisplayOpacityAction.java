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

import java.util.Vector;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.view.nodes.OneComponentNode;
import org.opensim.view.nodes.OpenSimObjectNode;
import org.opensim.view.nodes.OpenSimObjectNode.displayOption;
import org.opensim.view.nodes.OpenSimObjectSetNode;

public final class ObjectDisplayOpacityAction extends CallableSystemAction {
   
   public void performAction() {
      Vector<OneComponentNode> objects = CollectAffectedComponentNodes();
      ObjectDisplayOpacityPanel.showDialog(objects);
   }

    protected Vector<OneComponentNode> CollectAffectedComponentNodes() {
        Vector<OneComponentNode> objects = new Vector<OneComponentNode>();
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        for(int i=0; i<selected.length; i++) {
            if(selected[i] instanceof OneComponentNode)
                collectDescendentNodes((OneComponentNode)selected[i], objects);
            else if (selected[i] instanceof OpenSimObjectSetNode){
                // Get children nd descend if instanceof OneComponentNode
                Children ch = ((OpenSimObjectSetNode) selected[i]).getChildren();
                for (int chNum=0; chNum < ch.getNodesCount(); chNum++){
                    if (ch.getNodeAt(chNum) instanceof OneComponentNode)
                        collectDescendentNodes((OneComponentNode)ch.getNodeAt(chNum), objects);
                }
            }
        } 
        return objects;
    }
   // node could be a Group or a list of objects not backed by OpenSim objects 
    protected void collectDescendentNodes(OpenSimObjectNode node, Vector<OneComponentNode> descendents) {
        if (node instanceof OneComponentNode) {
            descendents.add((OneComponentNode)node);
        }
        Children ch = node.getChildren();
        // process children
        for (Node childNode : ch.getNodes()) {
            if (childNode instanceof OpenSimObjectNode) {
                collectDescendentNodes((OpenSimObjectNode) childNode, descendents);
            }
        }

    }
   
   public String getName() {
      return NbBundle.getMessage(ObjectDisplayOpacityAction.class, "CTL_ObjectDisplayOpacityAction");
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

   // Make it available only if selected objects have representation and belong to same model
    public boolean isEnabled() {
       // The "hide" option is enabled unless every selected node is hidden.
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        boolean isColorable=true;
        for(int i=0; i<selected.length && isColorable; i++){
            isColorable = (selected[i] instanceof OpenSimObjectNode);
            if (isColorable){
                OpenSimObjectNode objectNode = (OpenSimObjectNode) selected[i];
                isColorable=objectNode.getValidDisplayOptions().contains(displayOption.Colorable);
            }
        }
        return isColorable;
    }
}
