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

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.view.nodes.OpenSimObjectNode;
import org.opensim.view.nodes.PropertyEditorAdaptor;
import org.opensim.view.pub.ViewDB;

public class ObjectDisplayChangeRepAction extends CallableSystemAction {
    
    public boolean isEnabled() {
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // If any selected object is hidden (or any selected group is mixed), return false.
        /* Always enable the option since everything can be wireframe in new visualizer of 4.0
        for(int i=0; i < selected.length; i++){
            OpenSimObjectNode objectNode = (OpenSimObjectNode) selected[i];
            int displayStatus = ViewDB.getInstance().getDisplayStatus(objectNode.getOpenSimObject());
            if (displayStatus == 0 || displayStatus == 2)
               return false;
        }*/
        return true;
    }

    public void performAction() {
       // unimplemneted by design
      throw new UnsupportedOperationException("Method should never be called!");
    }
    /**
     * A variation of performAction that takes the desired representation and applies it to the model
     */
     public void performAction(int newRep, int newShading) {
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        for(int i=0; i < selected.length; i++){
            OpenSimObjectNode objectNode = (OpenSimObjectNode) selected[i];
            if (objectNode instanceof ColorableInterface) {
                ((ColorableInterface)objectNode).setDisplayPreference(newRep);
            }
            else {
                ViewDB.getInstance().setObjectRepresentation(objectNode.getOpenSimObject(), newRep, newShading);
                objectNode.refreshNode();
            }
        }
   }
  
    public String getName() {
        return "unused";
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

    private int mapRepAndShadingToPref(int newRep, int newShading) {
        if (newRep==1) return 1;    // Wireframe
        if (newRep==2){
           if (newShading==0) return 3;    // flat
           if (newShading==1 || newShading==2) return 4;    // flat
         }
        return 4;
    }
    
}
