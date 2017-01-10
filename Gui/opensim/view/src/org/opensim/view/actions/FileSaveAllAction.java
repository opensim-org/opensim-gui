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
package org.opensim.view.actions;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.nodes.OneModelNode;
import org.opensim.view.nodes.ModelCloseSelectedAction;
import org.opensim.view.nodes.ModelSaveSelectedAction;
import org.opensim.view.pub.OpenSimDB;

public final class FileSaveAllAction extends CallableSystemAction {

   public void performAction() {
         Node root = ExplorerTopComponent.findInstance().getExplorerManager().getRootContext();
         if (root == null) return; // No models
         Children ch=root.getChildren();
         int numChildren = ch.getNodesCount();
         if (numChildren==0) return;
         ArrayList<Node> modelsToSave = new ArrayList<Node>();
         for(int i=0; i < numChildren; i++){
             if (ch.getNodeAt(i) instanceof OneModelNode){
                OneModelNode modelNode = (OneModelNode) ch.getNodeAt(i);
                modelsToSave.add(modelNode);
            }
         }
         if (modelsToSave.size()==0) return;
         Node[] modelNodes = new Node[modelsToSave.size()];
         for(int j=0; j<modelsToSave.size(); j++){
             modelNodes[j] = modelsToSave.get(j);
         }
        try {
            ExplorerTopComponent.findInstance().getExplorerManager().setSelectedNodes(modelNodes);
            ModelSaveSelectedAction saveSelectedAction = (ModelSaveSelectedAction) ModelCloseSelectedAction.findObject(
                     (Class)Class.forName("org.opensim.view.nodes.ModelSaveSelectedAction"), true);
            saveSelectedAction.performAction();
            ExplorerTopComponent.findInstance().getExplorerManager().setSelectedNodes(new Node[]{});
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }
   }
   
   public String getName() {
      return NbBundle.getMessage(FileSaveAllAction.class, "CTL_FileSaveAllAction");
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
   
   public boolean isEnabled() {
       return OpenSimDB.getInstance().getCurrentModel()!=null;
   }
   
}
