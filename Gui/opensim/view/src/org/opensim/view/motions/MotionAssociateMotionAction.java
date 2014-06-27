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
               else if (fileName.endsWith(".trc")){
                    MarkerData markerData = new MarkerData(fileName);
                    Storage newStorage = new Storage();
                    markerData.makeRdStorage(newStorage);
                    AnnotatedMotion amot = new AnnotatedMotion(newStorage, markerData.getMarkerNames());
                    amot.setUnitConversion(1.0/(markerData.getUnits().convertTo(Units.UnitType.Meters)));
                    amot.setName(new File(fileName).getName());
                    amot.setDataRate(markerData.getDataRate());
                    amot.setCameraRate(markerData.getCameraRate());
                    storage = amot;
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
