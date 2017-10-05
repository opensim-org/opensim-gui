/* -------------------------------------------------------------------------- *
 * OpenSim: PlotterSourceStorage.java                                         *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Tom Uchida                                         *
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
 * PlotterSourceMotion
 * Author(s): Ayman Habib
 */
package org.opensim.plotter;

import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.opensim.logger.OpenSimLogger;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.Model;
import org.opensim.modeling.Storage;

/**
 *
 * @author Ayman. A class representing the Source=File radio button. The file is parsed
 * and its contents cached in this object for quick reference later.
 */
public class PlotterSourceStorage implements PlotterSourceInterface {
    // Data members
    public String[] allAvailable;

    public String displayName;

    public boolean[] selectionStatus;

    public Storage storage;
    
    boolean isStatesStorage;
   /** Creates a new instance of PlotterSourceFile */
   public PlotterSourceStorage(Storage aStorage) {
      storage=aStorage;
      storage.makeStorageLabelsUnique();
      displayName = "Storage:"+aStorage.getName();
      ArrayStr labels = storage.getColumnLabels();
      allAvailable = new String[labels.getSize()];
      selectionStatus = new boolean[labels.getSize()];
      for(int i=0; i<labels.getSize(); i++){
         allAvailable[i]=labels.getitem(i);
         selectionStatus[i]=false;
      }
      isStatesStorage = false;
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
      return displayName+"("+(storage.isInDegrees()?"Deg.":"Rad.")+")";
   }

   public Storage getStorage() {
      return storage;
   }

    public void clearSelectionStatus() {
       for(int i=0; i<selectionStatus.length ;i++)
        selectionStatus[i]=false;
    }

    public boolean isStorageLive() {
        return true;
    }

    public void setStorageLive(boolean b) {
    }
    
    public double getDefaultMin(String domainName)
    {
       if (domainName.equalsIgnoreCase("time")){
          return storage.getFirstTime();
       }
       else
          return 0.0;
    }
    
   
    public double getDefaultMax(String domainName)
    {
       if (domainName.equalsIgnoreCase("time")){
          return storage.getLastTime();
       }
       else
          return 1.0;
    }

   public boolean isValidName(String columnName) {
      String stripName=columnName.substring(columnName.lastIndexOf(":")+1);
      for(int i=0;i<allAvailable.length;i++){
         if (allAvailable[i].compareTo(stripName)==0)
            return true;
      }
      return false;
   }

    public boolean convertAngularUnits() {
        return false;
    }

    public boolean hasFullState(Model model) {
        ArrayStr stateNames = model.getStateVariableNames();
        stateNames.insert(0, "time");
        //outStringArray(stateNames);
        ArrayStr storageLabels = storage.getColumnLabels();
        //outStringArray(storageLabels);
        isStatesStorage = storageLabels.arrayEquals(stateNames);
        //for (int i=0; i< stateNames.getSize(); i++)
        //    if (storageLabels.getitem(i).equals(stateNames.getitem(i)))
        //        System.out.println("Index "+i+" matches");
        
        if (isStatesStorage) 
            OpenSimLogger.logMessage("Using states from input/states file\n", OpenSimLogger.INFO);
        else
            OpenSimLogger.logMessage("Computing equilibrium for muscle states\n", OpenSimLogger.INFO);            
        return isStatesStorage;
    }

    private void outStringArray(ArrayStr stateNames) {
       System.out.println("Printing state names:");
       System.out.println("Size:"+stateNames.getSize());
        for(int i=0; i< stateNames.getSize(); i++)
            System.out.println(stateNames.getitem(i));
          System.out.println("========");
    }

}
