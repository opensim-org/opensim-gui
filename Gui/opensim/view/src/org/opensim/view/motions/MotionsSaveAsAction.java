/* -------------------------------------------------------------------------- *
 * OpenSim: MotionsSaveAsAction.java                                          *
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
                fileName=FileUtils.getInstance().browseForFilenameToSave(FileUtils.TrcFileFilter, true, "experimental_data");
             else
                fileName=FileUtils.getInstance().browseForFilenameToSave(FileUtils.MotionFileFilter, true, "experimental_data");                 
         }
         else
            fileName=FileUtils.getInstance().browseForFilenameToSave(FileUtils.MotionFileFilter, true, "motion");
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
