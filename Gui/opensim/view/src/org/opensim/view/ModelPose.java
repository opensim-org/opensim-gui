/* -------------------------------------------------------------------------- *
 * OpenSim: ModelPose.java                                                    *
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
 * ModelPose
 * Author(s): Ayman Habib
 */
package org.opensim.view;

import java.util.Vector;
import org.opensim.modeling.*;
import org.opensim.view.pub.OpenSimDB;

/**
 *
 * @author Ayman. A class representing a model pose (set of coordinates)
 */
public class ModelPose {
   private String poseName;
   /*
    * Using parallel arrays instead of one array of <name,value> pairs to minimize
    * overhead but this decision can be revisited if a generic nameValue pairing is needed.
    */
   private Vector<String> coordinateNames = new Vector<String>(4);
   private Vector<Double> coordinateValues = new Vector<Double>(4);
    /** Creates a new instance of ModelPose */
   public ModelPose() {
   }

   public ModelPose(CoordinateSet coords, String name) {
      this(coords, name, false);
   }
   // Create a pose from the current configuration of the model. This includes only coorindtae values
   // that is, speeds and other state variables are not maintained
   public ModelPose(String poseName, Model aModel){
       this(aModel.getCoordinateSet(), poseName);
   }

   public ModelPose(CoordinateSet coords, String name, boolean isDefault) {
      setPoseName(name);
      Model model=null;
      OpenSimContext context=null;
      if (coords.getSize()>0){
          Coordinate coord = coords.get(0);
          model=coord.getModel();
          context = OpenSimDB.getInstance().getContext(model);
      }
      for(int i=0; i< coords.getSize(); i++){
         Coordinate coord = coords.get(i);
        
         getCoordinateNames().add(coord.getName());
         if (isDefault){
            getCoordinateValues().add(coord.getDefaultValue());
         }
         else {
            getCoordinateValues().add(context.getValue(coord));
          }
      }
   }
   public Vector<Double> getCoordinateValues() {
      return coordinateValues;
   }

   public Vector<String> getCoordinateNames() {
      return coordinateNames;
   }

   public String getPoseName() {
      return poseName;
   }

   public void setPoseName(String poseName) {
      this.poseName = poseName;
   }

   public void setCoordinateNames(Vector<String> coordinateNames) {
      this.coordinateNames = coordinateNames;
   }

   public void setCoordinateValues(Vector<Double> coordinateValues) {
      this.coordinateValues = coordinateValues;
   }

   /** 
    * toString function useful for displaying ModelPose objects in a list
    */
   public String toString() {
      return getPoseName();
   }

   
   public void useAsDefaultForModel(Model aModel){
      CoordinateSet coords = aModel.getCoordinateSet();

      for(int i=0;i<coordinateNames.size();i++){
         // Values in file
         String name=coordinateNames.get(i);
         double storedValue = (Double)coordinateValues.get(i);
         if (coords.contains(name)){
            coords.get(name).setDefaultValue(storedValue);
          }
      }
   }
 }
