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
package org.opensim.view.motions;

import org.openide.awt.StatusDisplayer;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction; 
import org.opensim.modeling.Model;
import org.opensim.modeling.Storage;
import org.opensim.utils.FileUtils;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.experimentaldata.AnnotatedMotion;
import org.opensim.view.experimentaldata.ClassifyDataJPanel;

public final class MotionsSaveAsAction extends CallableSystemAction {
  
   public static void saveMotion(Model model, Storage motion, String fileName) {
      StatusDisplayer.getDefault().setStatusText("Saving motion...");
      // Needs to be converted to degrees, therefore we need to make a copy of it first
      if (motion instanceof AnnotatedMotion){
          // This must come from a file, either trc or mot
          if (!ClassifyDataJPanel.saveTransformedMotion((AnnotatedMotion)motion, fileName))
              return;
      }
      else {
      Storage motionCopy = new Storage(motion);
      model.getSimbodyEngine().convertRadiansToDegrees(motionCopy);
      String extension = FileUtils.getExtension(fileName);
      if (extension==null)
         fileName += ".mot";    // if no extension use .mot since we're in degrees now'
      if(FileUtils.getExtension(fileName).toLowerCase().equals("mot"))
         motionCopy.setWriteSIMMHeader(true); // Write SIMM header for SIMM compatibility
      // TODO: set precision?
      motionCopy.print(fileName);
      MotionsDB.getInstance().saveStorageFileName(motion, fileName);
      MotionsDB.getInstance().setMotionModified(motion,false); 
      }
      StatusDisplayer.getDefault().setStatusText("Saved motion "+motion.getName()+" to "+fileName);
   }

   public void performAction() {
      Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
      if(selected.length==1 && (selected[0] instanceof OneMotionNode)) {
         OneMotionNode node = (OneMotionNode)selected[0];
         // if AnnotatedMotion make sure we keep extension same as original
         String fileName = "";
         if (node.getMotion() instanceof AnnotatedMotion){
             AnnotatedMotion dMotion = (AnnotatedMotion)node.getMotion();
             String currentExtension = dMotion.getName().substring(dMotion.getName().lastIndexOf("."));
             if (currentExtension.endsWith("trc"))
                fileName=FileUtils.getInstance().browseForFilenameToSave(FileUtils.TrcFileFilter, true, "");
             else
                fileName=FileUtils.getInstance().browseForFilenameToSave(FileUtils.MotionFileFilter, true, "");                 
         }
         else
            fileName=FileUtils.getInstance().browseForFilenameToSave(FileUtils.MotionFileFilter, true, "");
         if(fileName!=null) saveMotion(node.getModel(), node.getMotion(), fileName);
      }
   }

   public String getName() {
      return NbBundle.getMessage(MotionsSaveAsAction.class, "CTL_MotionsSaveAsAction");
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
      return (selected.length==1 && (selected[0] instanceof OneMotionNode));
   }
}
