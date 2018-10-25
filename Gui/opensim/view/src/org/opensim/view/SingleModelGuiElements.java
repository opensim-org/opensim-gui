/* -------------------------------------------------------------------------- *
 * OpenSim: SingleModelGuiElements.java                                       *
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
import org.opensim.modeling.ArrayConstObjPtr;
import org.opensim.modeling.Force;
import org.opensim.modeling.Muscle;
import org.opensim.modeling.ForceSet;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.BodyIterator;
import org.opensim.modeling.BodyList;
import org.opensim.modeling.Model;
import org.opensim.modeling.Coordinate;
import org.opensim.modeling.CoordinateSet;
import org.opensim.modeling.FrameIterator;
import org.opensim.modeling.FrameList;
import org.opensim.modeling.MarkerSet;
import org.opensim.modeling.ObjectGroup;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.PhysicalFrame;
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
    private String[] physicalFrameNames=null;
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
    NavigatorByTypeModel navigatorByTypeModel;
    
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
            BodyList bodies = model.getBodyList();
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
    // This method populates frameNames and physicalFrameNames for later use
    public String[] getFrameNames()
    {
        if (frameNames == null) {
            FrameList frames = model.getFrameList();
            FrameIterator bi = frames.begin();
            ArrayList<String> bNames = new ArrayList<String>();
            ArrayList<String> phFrameNames = new ArrayList<String>();
            while (!bi.equals(frames.end())) {
                bNames.add(bi.getAbsolutePathString());
                if (PhysicalFrame.safeDownCast(bi.__deref__())!= null)
                    phFrameNames.add(bi.getAbsolutePathString());
                bi.next();
            }
            frameNames = new String[bNames.size()];
            bNames.toArray(frameNames);
            physicalFrameNames = new String[phFrameNames.size()];
            phFrameNames.toArray(getPhysicalFrameNames());
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
           ArrayConstObjPtr objects = group.getMembers();
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
   
    /**
     * @return the physicalFrameNames
     */
    public String[] getPhysicalFrameNames() {
        if (frameNames == null) {
            getFrameNames(); // For the side effect of populating both frameNames and physicalFrameNames
        }
        return physicalFrameNames;
    }
}
