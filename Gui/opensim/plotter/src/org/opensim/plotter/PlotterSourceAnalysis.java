/* -------------------------------------------------------------------------- *
 * OpenSim: PlotterSourceAnalysis.java                                        *
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
/*
 *
 * PlotterSourceAnalysis
 * Author(s): Ayman Habib
 */
package org.opensim.plotter;

import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.opensim.modeling.*;

/**
 *
 * @author Ayman. A class representing the Source=Model/Analysis radio button
 */
public class PlotterSourceAnalysis implements PlotterSourceInterface {
   
   private String[] allAvailable;
   private boolean[] selectionStatus;
   private String displayName;
   private Storage   storage;
   private Model model;
   private boolean live;    // Flag indicating whether the storage underneath is live 
   private String storageName;
   /** Creates a new instance of PlotterSourceAnalysis */
   public PlotterSourceAnalysis(Model aModel, Storage storage, String modelAnalysisString) {
      model = aModel;
      this.storage = storage;
      live=true;
      displayName = modelAnalysisString;
      storageName = storage.getName();
      
      if (storage.getName().startsWith("MomentArm_"))
         displayName = storage.getName().substring(10)+" "+displayName;
      else if (storage.getName().startsWith("Moment_"))
         displayName = storage.getName().substring(7)+" "+displayName;
      
      ArrayStr labels = storage.getColumnLabels();
      allAvailable = new String[labels.getSize()];
      selectionStatus = new boolean[labels.getSize()];
      for(int i=0; i<labels.getSize(); i++){
         allAvailable[i]=labels.getitem(i);
         selectionStatus[i]=false;
      }
      //System.out.println("Created PlotterSourceAnalysis DisplayName="+displayName);
   }

   public String[] getAllQuantities() {
      return allAvailable;
   }

   public Vector<String> getSelectedQuantities() {
      int countSelected = 0;
      Vector<String> selectedVec = new Vector<String>(4);
      for(int i=0; i<selectionStatus.length ;i++){
         if (selectionStatus[i]){
               countSelected += 1;
               selectedVec.add(allAvailable[i]);
         }
      }
      return selectedVec;
   }

   public boolean[] filterByRegularExprssion(String regex) throws PatternSyntaxException {
       Pattern p = Pattern.compile(regex);
       
      for(int i=0; i<allAvailable.length ;i++)
       selectionStatus[i] = p.matcher(allAvailable[i]).matches();
       
       return selectionStatus;
   }

   public boolean[] toggleItemSelection(String item) {      
      for(int i=0; i<allAvailable.length ;i++){
         if (allAvailable[i].compareTo(item)==0)
            selectionStatus[i] = !selectionStatus[i];
      }
      return getSelectionStatus();
   }

   public boolean[] getSelectionStatus() {
      return selectionStatus;
   }

   public void setSelectionStatus(boolean[] selectionStatus) {
      this.selectionStatus = selectionStatus;
   }

   public String getDisplayName() {
      return displayName;
   }

   public Storage getStorage() {
      return storage;
   }

    public void clearSelectionStatus() {
    }

    public boolean isStorageLive() {
        return live;
    }

    public void setStorageLive(boolean b) {
        live=b;
        if (b==false)
            storage=null;
    }
    public String toString() {
        return storage.getName();
    }

   Model getModel() {
      return model;
   }

   public double getDefaultMin(String domainName) {
      Coordinate coord = model.getCoordinateSet().get(domainName);
      if (coord==null)
         return 0.0;
      double min = coord.getRangeMin();
      if (coord.getMotionType() == Coordinate.MotionType.Rotational){
         min = Math.toDegrees(min);
      }
      return min;
   }

   public double getDefaultMax(String domainName) {
      Coordinate coord = model.getCoordinateSet().get(domainName);
      if (coord==null)
         return 1.0;
      double max = coord.getRangeMax();
      if (coord.getMotionType() == Coordinate.MotionType.Rotational){
         max = Math.toDegrees(max);
      }
      return max;
   }

   public boolean isValidName(String columnName) {
      for(int i=0;i<allAvailable.length;i++){
         if (allAvailable[i].compareTo(columnName)==0)
            return true;
      }
      return false;
   }

    public boolean convertAngularUnits() {
        return false;
    }

    public boolean hasFullState(Model model) {
        return false;
    }
    void updateStorage(Analysis muscleAnalysis) {
        storage = muscleAnalysis.getStorageList().get(storageName);
    }
}
