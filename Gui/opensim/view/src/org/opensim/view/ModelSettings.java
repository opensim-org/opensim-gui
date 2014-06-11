/*
 *
 * ModelGUIPrefs
 * Author(s): Ayman Habib
 * Copyright (c)  2005-2006, Stanford University, Ayman Habib
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
