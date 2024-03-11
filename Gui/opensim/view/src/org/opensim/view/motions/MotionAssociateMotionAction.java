/* -------------------------------------------------------------------------- *
 * OpenSim: MotionAssociateMotionAction.java                                  *
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

import java.io.File;
import java.io.IOException;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.CoordinateSet;
import org.opensim.modeling.MarkerData;
import org.opensim.modeling.MarkerSet;
import org.opensim.modeling.Model;
import org.opensim.modeling.Storage;
import org.opensim.modeling.Units;
import org.opensim.utils.FileUtils;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.experimentaldata.AnnotatedMotion;

public final class MotionAssociateMotionAction extends CallableSystemAction {

   public void performAction() {
      Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
      if (selected.length == 1 && (selected[0] instanceof OneMotionNode)) {
         OneMotionNode node = (OneMotionNode)selected[0];
         String fileName = FileUtils.getInstance().browseForFilename(".mot, .sto, .trc", "Motion or Storage file", ExplorerTopComponent.findInstance());
         if (fileName != null) {
            Storage motion = node.getMotion();
            Storage storage = null;
            try {
               if (fileName.toLowerCase().endsWith(".sto") || fileName.toLowerCase().endsWith(".mot"))
                storage = new Storage(fileName);
               else if (fileName.toLowerCase().endsWith(".trc")){
                    MarkerData markerData = new MarkerData(fileName);
                    Storage newStorage = new Storage();
                    markerData.makeRdStorage(newStorage);
                    AnnotatedMotion amot = new AnnotatedMotion(newStorage, markerData.getMarkerNames());
                    amot.setUnitConversion(markerData.getUnits().convertTo(Units.UnitType.Meters));
                    amot.setName(new File(fileName).getName());
                    amot.setDataRate((int)markerData.getDataRate());
                    amot.setCameraRate((int)markerData.getCameraRate());
                    storage = amot;
                    amot.setModel(node.getModelForNode());
               }
            } catch (IOException ex) {
               ex.printStackTrace();
               DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Could not read motion file " + fileName));
               return;
            }
            Storage newMotion = storage;
            if (columnNamesOverlap(motion, newMotion, node.getModel()) == true) {
               DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(fileName +
                       " cannot be associated to " + motion.getName() + "-- duplicate column labels encountered."));
               return;
            }
            MotionsDB.getInstance().loadMotionStorage(newMotion, false, fileName);
            MotionsDB.getInstance().reportModifiedMotion(motion, node.getModel());
         }
      }
   }

   /* The time frames match if motion1 contains all of the time frames in motion2
    * (motion1 can contain more).
    */
   private boolean timeFramesMatch(Storage motion1, Storage motion2) {
      for (int i=0; i<motion2.getSize(); i++) {
         int index = motion1.findIndex(i, motion2.getStateVector(i).getTime());
         double time1 = motion1.getStateVector(index).getTime();
         double time2 = motion2.getStateVector(i).getTime();
         if (time1 != time2) //equality of doubles!!
            return false;
      }
      return true;
   }

   /* When merging two motions, column names are allowed to overlap as long as
    * they are not coordinate or marker columns.
    */
   private boolean columnNamesOverlap(Storage motion1, Storage motion2, Model model) {
      ArrayStr motion1labels = motion1.getColumnLabels();
      ArrayStr motion2labels = motion2.getColumnLabels();
      for (int i=0; i<motion1labels.getSize(); i++) {
         for (int j=0; j<motion2labels.getSize(); j++) {
            String label1 = motion1labels.getitem(i);
            String label2 = motion2labels.getitem(j);
            if (label1.equals(label2) &&
                (labelStartsWithCoordinateName(label1, model) || labelStartsWithMarkerName(label1, model)))
               return true;
         }
      }
      return false;
   }

   private boolean labelStartsWithCoordinateName(String label, Model model) {
      CoordinateSet coordset = model.getCoordinateSet();
      for (int i=0; i<coordset.getSize(); i++) {
         if (label.startsWith(coordset.get(i).getName()))
            return true;
      }
      return false;
   }

   private boolean labelStartsWithMarkerName(String label, Model model) {
      MarkerSet markerset = model.getMarkerSet();
      for (int i=0; i<markerset.getSize(); i++) {
         if (label.startsWith(markerset.get(i).getName() + "_"))
            return true;
      }
      return false;
   }

   public String getName() {
      return NbBundle.getMessage(MotionAssociateMotionAction.class, "CTL_MotionAssociateMotionAction");
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
      for(int i=0; i<selected.length; i++) if(!(selected[i] instanceof OneMotionNode)) return false; // one of the nodes is not a OneMotionNode
      return (selected.length==1);
   }

}
