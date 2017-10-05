/* -------------------------------------------------------------------------- *
 * OpenSim: PlotterSourceInterface.java                                       *
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
 * PlotterSourceInterface
 * Author(s): Ayman Habib
 */
package org.opensim.plotter;

import java.util.Vector;
import org.opensim.modeling.Model;
import org.opensim.modeling.Storage;

/**
 *
 * @author Ayman. A class respresenting a Source of data for plotting. This is either a file
 * in which case all columns in the file are available for selection or a model's Analysis
 */
public interface PlotterSourceInterface {
   
   public String[] getAllQuantities();
   
   public Vector<String> getSelectedQuantities();
   
   public boolean[] getSelectionStatus();
   
   public void clearSelectionStatus();

   public boolean[] filterByRegularExprssion(String regex);
   
   public boolean[] toggleItemSelection(String item);
   
   public String getDisplayName();

   public Storage getStorage();
   
   public boolean isStorageLive();
   
   public void setStorageLive(boolean b);

   public double getDefaultMin(String domainName);
   
   public double getDefaultMax(String domainName);
   
   public boolean isValidName(String columnName);
   
   public boolean convertAngularUnits();
   
   public boolean hasFullState(Model model);

}
