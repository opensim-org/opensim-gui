/*
 * FileLoadMotionAction.java
 *
 * Created on April 4, 2007, 8:35 AM
 *
 * Copyright (c)  2006, Stanford University and Ayman Habib. All rights reserved.
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

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.utils.FileUtils;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.nodes.ConcreteModelNode;
import org.opensim.view.pub.OpenSimDB;

public final class FileLoadMotionAction extends CallableSystemAction {
   
   public void performAction() {
      // TODO implement action body
      // Browse for a SIMM motion file
        String fileName = FileUtils.getInstance().browseForFilename(".mot,.sto", "Motion or storage file");
        if (fileName != null){
           // Load file and associate it to a model
           // File is loaded into a SimmMotionData object first then associated with a particular
           // model. This's done in OpenSimDB so that created SimmMotionData is not garbage collected early.
           // also because OpenSimDB has access to all models and as such can decide if it makes sense 
           // to associate the loaded motion with a prticular model.
            loadMotion(fileName);
        }
        
   }
   
   public String getName() {
      return NbBundle.getMessage(FileLoadMotionAction.class, "CTL_FileLoadMotionAction");
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
       Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
       if (selected.length!=1) return false;
       if (!(selected[0] instanceof ConcreteModelNode)) return false;
       ConcreteModelNode selectedModelNode = (ConcreteModelNode) selected[0];
       return (OpenSimDB.getInstance().getCurrentModel()==selectedModelNode.getModel());
   }

    static public void loadMotion(String fileName) {
           MotionsDB.getInstance().loadMotionFile(fileName, true);
    }
   
   
}
