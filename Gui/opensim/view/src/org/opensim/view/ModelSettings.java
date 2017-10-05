/* -------------------------------------------------------------------------- *
 * OpenSim: ModelSettings.java                                                *
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
 * ModelGUIPrefs
 * Author(s): Ayman Habib
 */
package org.opensim.view;

import java.util.Vector;

/**
 *
 * @author Ayman. A class to encapsulate GUI preferences for a specific model that are not saved with 
 * the model's .osim file
 */

public class ModelSettings {
   private Vector<ModelPose> poses=new Vector<ModelPose>();
   
   public ModelSettings()
   {
   }
   public void addPose(ModelPose newPose)
   {
      getPoses().add(newPose);
      System.out.println("Adding pose:"+newPose.getPoseName());
   }
   public ModelPose getPose(String name)
   {
      for(int i=0; i<getPoses().size(); i++){
         if (name.compareToIgnoreCase(getPoses().get(i).getPoseName())==0){
            return getPoses().get(i);
         }
      }
      return null;
   }
   public int getNumPoses()
   {
      //System.out.println("Found"+ getPoses().size()+"poses");      
      return getPoses().size();
   }

   public Vector<ModelPose> getPoses() {
      return poses;
   }

   public void setPoses(Vector<ModelPose> poses) {
      this.poses = poses;
   }

   public boolean containsPose(String newName) {
      boolean found=false;
      for(int i=0; i<poses.size() && !found; i++){
         found= (poses.get(i).getPoseName().equalsIgnoreCase(newName));
      }
      return found;
   }

   public void deletePoses(Vector<ModelPose> posesToDelete) {
      poses.removeAll(posesToDelete);
   }
}
