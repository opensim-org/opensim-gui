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
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.nodes.ConcreteModelNode;
import org.opensim.view.nodes.OpenSimObjectNode;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

public final class ModelRenameAction extends CallableSystemAction {
   
   public boolean isEnabled() {
      Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
      return selected.length==1;
   }
   
   public void performAction() {
      Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
      if (selected.length == 1){
         OpenSimObjectNode objectNode = (OpenSimObjectNode) selected[0];
         NotifyDescriptor.InputLine dlg =
                 new NotifyDescriptor.InputLine("Model Name: ", "Rename ");
         dlg.setInputText(objectNode.getOpenSimObject().getName());
         if(DialogDisplayer.getDefault().notify(dlg)==NotifyDescriptor.OK_OPTION){
             String newName = dlg.getInputText();
             if (OpenSimDB.getInstance().validateName(newName, true)){
                 objectNode.getOpenSimObject().setName(newName);
                 objectNode.setName(newName);  // Force navigator window update
                 // Create event to tell everyone else
                 Vector<OpenSimObject> objs = new Vector<OpenSimObject>(1);
                 objs.add(objectNode.getOpenSimObject());
                 ObjectsRenamedEvent evnt = new ObjectsRenamedEvent(this, null, objs);
                 OpenSimDB.getInstance().setChanged();
                 OpenSimDB.getInstance().notifyObservers(evnt);
                 // The following is specific to renaming a model since
                 // other windows may display currentModel's name
                 // A more generic scheme using events should be used.
                 if (objectNode instanceof ConcreteModelNode) {
                    Model dModel = ((ConcreteModelNode)objectNode).getModel();
                    if (dModel==OpenSimDB.getInstance().getCurrentModel())
                       OpenSimDB.getInstance().setCurrentModel(dModel);   // Need to do this so that model dropdown updates
                    // Mark the model as dirty
                    SingleModelGuiElements guiElem = ViewDB.getInstance().getModelGuiElements(dModel);
                    guiElem.setUnsavedChangesFlag(true);
                 }
                 objectNode.refreshNode();
             } else
                 DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Provided name "+newName+" is not valid"));
         }
    
      } else { // Should never happen
         DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Rename of multiple objects is not supported."));
      }
   }
   
   public String getName() {
      return NbBundle.getMessage(ModelRenameAction.class, "CTL_ObjectRenameAction");
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
