/* -------------------------------------------------------------------------- *
 * OpenSim: FilterableStringArray.java                                        *
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
 * FilterableStringArray
 * Author(s): Ayman Habib
 */
package org.opensim.view.excitationEditor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.Storage;

/**
 *
 * @author Ayman. A class representing the Source=File radio button. The file is parsed
 * and its contents cached in this object for quick reference later.
 */
public class FilterableStringArray implements FilterableInterface {
    // Data members
    public String[] allAvailable;

    public boolean[] selectionStatus;

   /** Creates a new instance of PlotterSourceFile */
   public FilterableStringArray(String[] names) {
      allAvailable = new String[names.length];
      System.arraycopy(names, 0, allAvailable, 0, names.length);
      selectionStatus = new boolean[names.length];
      for(int i=0; i<names.length; i++){
         selectionStatus[i]=false;
      }
      sortAlphabetically(allAvailable);
   }

    public FilterableStringArray(ArrayStr names) {
      allAvailable = new String[names.getSize()];
      selectionStatus = new boolean[names.getSize()];
      for(int i=0; i<names.getSize(); i++){
         selectionStatus[i]=false;
         allAvailable[i]=names.getitem(i);
      }
      sortAlphabetically(allAvailable);
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
      return "";
   }

   public Storage getStorage() {
      return null;
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
       throw new UnsupportedOperationException("Cant get min for names only source");
       // Unreachable
     }
    
   
    public double getDefaultMax(String domainName)
    {
       throw new UnsupportedOperationException("Cant get min for names only source");
       // Unreachable
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

    private void sortAlphabetically(String[] allAvailable) {
        ArrayList<String> sortedNames = new ArrayList<String>(allAvailable.length);
        for(int i=0; i<allAvailable.length; i++){
            sortedNames.add(allAvailable[i]);
        }
        Collections.sort(sortedNames);
        sortedNames.toArray(allAvailable);
    }

}
