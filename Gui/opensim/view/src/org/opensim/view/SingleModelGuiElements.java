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
/*
 * SingleModelVisuals.java
 *
 * Created on November 14, 2006, 5:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view;

import java.util.ArrayList;
import java.util.Vector;
import org.opensim.modeling.Force;
import org.opensim.modeling.Muscle;
import org.opensim.modeling.ForceSet;
import org.opensim.modeling.ArrayObjPtr;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.BodyIterator;
import org.opensim.modeling.Model;
import org.opensim.modeling.BodiesList;
import org.opensim.modeling.Coordinate;
import org.opensim.modeling.CoordinateSet;
import org.opensim.modeling.FrameIterator;
import org.opensim.modeling.FramesList;
import org.opensim.modeling.MarkerSet;
import org.opensim.modeling.ObjectGroup;
import org.opensim.modeling.OpenSimContext;
import org.opensim.view.pub.OpenSimDB;

/**
 *
 * @author Pete Loan & Ayman Habib
 */
public class SingleModelGuiElements {
    String preferredUnits; // Place holder for model specific Gui pref.
    Model model;   // model that Gui elements are created for
    
    private String[] bodyNames=null;
    private String[] frameNames=null;
    private String[] coordinateNames=null;
    private String[] unconstrainedCoordinateNames=null;
    private String[] actuatorClassNames=null;
    private String[] actuatorNames=null;
    private String[] markerNames=null;

    private boolean unsavedChanges = false;
    // Not a semaphore, just a flag to disallow the closing (and possibly editing)
    // of a model while in use.
    private boolean locked = false;
    private String lockOwner = null;
    OpenSimContext context;
    
    public SingleModelGuiElements(Model model)
    {
       this.model=model;
       context = OpenSimDB.getInstance().getContext(model);

    }

    /**
     * Turn on/off the flag for marking changes to the model.
     */
    public void setUnsavedChangesFlag(boolean state)
    {
       unsavedChanges = state;
    }

    /**
     * Get the flag for marking changes to the model.
     */
    public boolean getUnsavedChangesFlag()
    {
       return unsavedChanges;
    }

    /**
     * Get the flag for marking the model as locked.
     */
    public boolean isLocked()
    {
       return locked;
    }

    /**
     * Get the owner of the lock.
     */
    public String getLockOwner()
    {
       return lockOwner;
    }

    /**
     * Mark the model as locked. If it's already locked, return false.
     */
    public boolean lockModel(String owner)
    {
       if (locked == false) {
          locked = true;
          lockOwner = owner;
          return true;
       } else {
          return false;
       }
    }

    /**
     * Unlock the model.
     */
    public void unlockModel()
    {
       locked = false;
       lockOwner = "";
    }

    /**
     * Get a list of names for model frames
     */
    public String[] getBodyNames()
    {
        if (bodyNames == null) {
            BodiesList bodies = model.getBodiesList();
            BodyIterator bi = bodies.begin();
            ArrayList<String> bNames = new ArrayList<String>();
            while (!bi.equals(bodies.end())) {
                bNames.add(bi.getName());
                bi.next();
            }
            bodyNames = new String[bNames.size()];
            bNames.toArray(bodyNames);
        }
        return bodyNames;
   }
    
    public String[] getFrameNames()
    {
        if (frameNames == null) {
            FramesList frames = model.getFramesList();
            FrameIterator bi = frames.begin();
            ArrayList<String> bNames = new ArrayList<String>();
            while (!bi.equals(frames.end())) {
                bNames.add(bi.getName());
                bi.next();
            }
            frameNames = new String[bNames.size()];
            bNames.toArray(frameNames);
        }
        return frameNames;
   }
    
    /**
     * Get a list of names for model coordinates
     */
    public String[] getCoordinateNames()
    {
       if (coordinateNames==null){
         CoordinateSet coordinates = model.getCoordinateSet();
         coordinateNames = new String[coordinates.getSize()];
         for (int i = 0; i < coordinates.getSize(); i++)
            coordinateNames[i] = new String(coordinates.get(i).getName());
       }
       return coordinateNames;
    }
    /**
     * Get a list of names for model coordinates
     */
    public String[] getUnconstrainedCoordinateNames()
    {
       if (unconstrainedCoordinateNames==null){
         CoordinateSet coordinates = model.getCoordinateSet();
         Vector<String> coordinateNamesVec = new Vector<String>();
         for (int i = 0; i < coordinates.getSize(); i++){
            Coordinate coord = coordinates.get(i);
            boolean constrained = context.isConstrained(coord);
            if (!constrained)
                coordinateNamesVec.add(coord.getName());
         }
         unconstrainedCoordinateNames= new String[coordinateNamesVec.size()];
         coordinateNamesVec.toArray(unconstrainedCoordinateNames);
       }
       return unconstrainedCoordinateNames;
    }
    /**
     * Get a list of names for actuator groups in model
     */
    public Vector<String> getActuatorGroupNames()
    {
        Vector<String> ret=new Vector<String>(4);
        ForceSet actuators = model.getForceSet();
        if (actuators !=null){
            ArrayStr muscleGroupNames = new ArrayStr();
            actuators.getGroupNames(muscleGroupNames);
            for(int i=0; i<muscleGroupNames.getSize();i++){
                ret.add(muscleGroupNames.getitem(i));
                Force act = actuators.get(i);
            }
        }
       return ret;
    }
    /**
     * Get Actuators that belong to the passed in muscle group
     */
    public Vector<String> getActuatorNamesForGroup(String groupName)
    {
       Vector<String> ret = new Vector<String>(20);
        ForceSet actuators = model.getForceSet();
        if (actuators !=null){
           ObjectGroup group=actuators.getGroup(groupName);
           assert(group!=null);
           ArrayObjPtr objects = group.getMembers();
           for(int i=0; i<objects.getSize();i++){
                ret.add(objects.getitem(i).getName());
           }
        }
        return ret;
   }
    /**
     * Get a list of names for model actuators that are muscles
     */
    public Vector<String> getMuscleNames()
    {
       Vector<String> ret=new Vector<String>(20);
       ForceSet actuators = model.getForceSet();
       if (actuators !=null){
           for(int i=0; i<actuators.getSize();i++){
              if (Muscle.safeDownCast(actuators.get(i)) != null)
                 ret.add(actuators.get(i).getName());
           }
       }
       return ret;
    }

    /**
     * Get a list of names for the model's markers
     */
    public String[] getMarkerNames()
    {
       if (markerNames == null)
          updateMarkerNames();
       return markerNames;
    }

   public void updateMarkerNames()
   {
      ArrayList<String> namesList = new ArrayList<String>(4);
      MarkerSet markers = model.getMarkerSet();
      if (markers != null) {
         for (int i=0; i<markers.getSize(); i++)
            namesList.add(markers.get(i).getName());
      }
      markerNames = new String[namesList.size()];
      System.arraycopy(namesList.toArray(), 0, markerNames, 0, namesList.size());
      java.util.Arrays.sort(markerNames);
   }
 
   /**
    * Get names of actuators
    */
   public String[] getActuatorNames()
   {
      if (actuatorNames == null)
         updateActuatorNames();
      return actuatorNames;
   }

   public void updateActuatorNames()
   {
      ArrayList<String> namesList = new ArrayList<String>(4);
      ForceSet actuators = model.getForceSet();
      if (actuators != null) {
         for (int i=0; i<actuators.getSize(); i++) {
            Force act =actuators.get(i);
            Muscle muscle = Muscle.safeDownCast(act);
            if (muscle != null) {
               namesList.add(muscle.getName());
            }
          }
      }
      actuatorNames = new String[namesList.size()];
      System.arraycopy(namesList.toArray(), 0, actuatorNames, 0, namesList.size());
      java.util.Arrays.sort(actuatorNames);
   }
}
