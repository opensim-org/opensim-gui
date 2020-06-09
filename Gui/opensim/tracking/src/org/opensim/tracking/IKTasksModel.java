/* -------------------------------------------------------------------------- *
 * OpenSim: IKTasksModel.java                                                 *
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

package org.opensim.tracking;

import java.util.Hashtable;
import java.util.Observable;
import java.util.Vector;
import org.opensim.modeling.Coordinate;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.CoordinateSet;
import org.opensim.modeling.IKCoordinateTask;
import org.opensim.modeling.IKMarkerTask;
import org.opensim.modeling.IKTask;
import org.opensim.modeling.IKTaskSet;
import org.opensim.modeling.MarkerData;
import org.opensim.modeling.MarkerSet;
import org.opensim.modeling.Model;
import org.opensim.modeling.Storage;
import org.opensim.tracking.IKTasksModel.ValueType;
import org.opensim.view.pub.OpenSimDB;

//==================================================================
// IKTasksModel
//==================================================================
public abstract class IKTasksModel extends Observable {
   public enum ValueType { DefaultValue, ManualValue, FromFile };

   protected Model model;
   protected Vector<IKTask> tasks = new Vector<IKTask>();

   public IKTasksModel(Model model) {
      this.model = model;
   }

   //------------------------------------------------------------------------
   // Populating values
   //------------------------------------------------------------------------
   public abstract void fromTaskSet(IKTaskSet fullTaskSet);
   public abstract void toTaskSet(IKTaskSet fullTaskSet);

   //------------------------------------------------------------------------
   // Get/Set
   //------------------------------------------------------------------------
   public int size() { return tasks.size(); }

   public String getName(int i) { return tasks.get(i).getName(); }
   public void setName(int i,String name) { tasks.get(i).setName(name); setModified(i); }

   public boolean getEnabled(int i) { return tasks.get(i).getApply(); }
   public void setEnabled(int i,boolean enabled) { if(getEnabled(i)!=enabled) { tasks.get(i).setApply(enabled); setModified(i); } }
  
   public abstract ValueType getValueType(int i);
   public abstract void setValueType(int i,ValueType type);
   public abstract double getValue(int i);
   public abstract double getDefaultValue(int i);
   public abstract double getManualValue(int i);
   public abstract void setManualValue(int i,double value);

   public abstract boolean isLocked(int i);

   public double getWeight(int i) { return isLocked(i) ? 0 : tasks.get(i).getWeight(); }
   public void setWeight(int i,double weight) { if(!isLocked(i) && getWeight(i)!=weight) { tasks.get(i).setWeight(weight); setModified(i); } }

   //------------------------------------------------------------------------
   // Validation
   //------------------------------------------------------------------------

   public abstract boolean isValidValue(int i);

   public boolean isValid(int i) { return !getEnabled(i) || isValidValue(i); }

   public boolean isValid() {
      for(int i=0; i<size(); i++) if(!isValid(i)) return false;
      return true;
   }

   //------------------------------------------------------------------------
   // Observer functions
   //------------------------------------------------------------------------
   protected void setModified() {
      setChanged(); 
      notifyObservers(new IKTasksModelEvent(IKTasksModelEvent.Operation.AllChanged));
   }
   protected void setModified(int i) {
      setChanged(); 
      notifyObservers(new IKTasksModelEvent(IKTasksModelEvent.Operation.TaskChanged, i));
   }
}

//==================================================================
// IKMarkerTasksModel
//==================================================================
class IKMarkerTasksModel extends IKTasksModel {
   private Hashtable<String,Boolean> markerExistsInData = new Hashtable<String,Boolean>();

   public IKMarkerTasksModel(Model model) {
      super(model);
      reset(true,1);
      setModified();
   }   

   public void reset(boolean defaultApply, double defaultWeight) {
      MarkerSet markerSet = model.getMarkerSet();
      tasks.setSize(markerSet.getSize());
      for(int i=0; i<tasks.size(); i++) {
         IKMarkerTask markerTask = new IKMarkerTask();
         markerTask.setName(markerSet.get(i).getName());
         markerTask.setApply(defaultApply);
         markerTask.setWeight(defaultWeight);
         tasks.set(i, markerTask);
      }
   }

   public void markerSetChanged() {
      Vector<IKTask> oldTasks = new Vector<IKTask>(tasks);
      reset(true,1);
      // Copy over old task values that have same marker name
      for(int i=0; i<tasks.size(); i++)
         for(int j=0; j<oldTasks.size(); j++)
            if(oldTasks.get(j).getName().equals(tasks.get(i).getName())) {
               tasks.set(i, oldTasks.get(j));
               break;
            }
      setModified();
   }

   public void markerDataChanged(MarkerData markerData) {
      markerExistsInData.clear();
      if(markerData!=null) {
         for(int i=0; i<markerData.getMarkerNames().getSize(); i++) {
            markerExistsInData.put(markerData.getMarkerNames().getitem(i),(Boolean)true);
         }
      }
   }

   public void fromTaskSet(IKTaskSet fullTaskSet) {
      reset(false,0);
      for(int i=0; i<fullTaskSet.getSize(); i++) {
         if(IKMarkerTask.safeDownCast(fullTaskSet.get(i))!=null) {
            int index = model.getMarkerSet().getIndex(fullTaskSet.get(i).getName());
            if(index >= 0) tasks.set(index, IKMarkerTask.safeDownCast(fullTaskSet.get(i).clone()));
         }
      }
      setModified();
   }

   public void toTaskSet(IKTaskSet fullTaskSet) {
      // Remove existing IKMarkerTasks
      for(int i=fullTaskSet.getSize()-1; i>=0; i--)
         if(IKMarkerTask.safeDownCast(fullTaskSet.get(i))!=null)
            fullTaskSet.remove(i);
      // Append copies of our tasks
      for(int i=0; i<tasks.size(); i++) {
         // Write it out if it's applied or if it has nonzero weight (even if not applied)
         // the latter case is so that user can recover their settings next time they load it in.
         if(tasks.get(i).getApply() || tasks.get(i).getWeight()!=0)
            fullTaskSet.cloneAndAppend(tasks.get(i)); // C++-side copy
      } 
   }

   private IKMarkerTask get(int i) { return (IKMarkerTask)tasks.get(i); }
  
   public ValueType getValueType(int i) { return ValueType.FromFile; }
   public void setValueType(int i,ValueType type) { assert(false); }
   public double getValue(int i) { return 0; }
   public double getDefaultValue(int i) { return 0; }
   public double getManualValue(int i) { return 0; }
   public void setManualValue(int i,double value) { assert(false); }

   public boolean isLocked(int i) { return false; }

   public boolean isValidValue(int i) {
      return markerExistsInData.get(getName(i))!=null;
   }
}

//==================================================================
// IKCoordinateTasksModel
//==================================================================
class IKCoordinateTasksModel extends IKTasksModel {
   private Hashtable<String,Boolean> coordinateExistsInData = new Hashtable<String,Boolean>();
   protected Vector<Double> conversion = new Vector<Double>();
   private OpenSimContext openSimContext;

   public IKCoordinateTasksModel(Model model) {
      super(model);
      openSimContext = OpenSimDB.getInstance().getContext(model);
      reset(false,0);
      setModified();
   }

   public void reset(boolean defaultApply, double defaultWeight) {
      CoordinateSet coordinateSet = model.getCoordinateSet();
      tasks.setSize(coordinateSet.getSize());
      conversion.setSize(coordinateSet.getSize());;
      for(int i=0; i<tasks.size(); i++) {
         IKCoordinateTask coordinateTask = new IKCoordinateTask();
         coordinateTask.setName(coordinateSet.get(i).getName());
         coordinateTask.setApply(defaultApply);
         coordinateTask.setWeight(defaultWeight);
         coordinateTask.setValueType(IKCoordinateTask.ValueType.DefaultValue);
         tasks.set(i, coordinateTask);
         conversion.set(i, (getMotionType(i)==Coordinate.MotionType.Rotational) ? 180.0/Math.PI : 1);
      }
   }

   public void coordinateDataChanged(Storage coordinateData) {
      coordinateExistsInData.clear();
      if(coordinateData!=null) {
         for(int i=0; i<coordinateData.getColumnLabels().getSize(); i++) {
            coordinateExistsInData.put(coordinateData.getColumnLabels().getitem(i),(Boolean)true);
         }
      }
   }

   public void fromTaskSet(IKTaskSet fullTaskSet) {
      reset(false,0);
      for(int i=0; i<fullTaskSet.getSize(); i++) {
         if(IKCoordinateTask.safeDownCast(fullTaskSet.get(i))!=null) {
            int index = model.getCoordinateSet().getIndex(fullTaskSet.get(i).getName());
            if(index >= 0) tasks.set(index, new IKCoordinateTask(IKCoordinateTask.safeDownCast(fullTaskSet.get(i)))); // Java-side copy
         }
      }
      setModified();
   }

   public void toTaskSet(IKTaskSet fullTaskSet) {
      // Remove existing IKCoordinateTasks
      for(int i=fullTaskSet.getSize()-1; i>=0; i--)
         if(IKCoordinateTask.safeDownCast(fullTaskSet.get(i))!=null)
            fullTaskSet.remove(i);
      // Append copies of our tasks
      for(int i=0; i<tasks.size(); i++) {
         if(tasks.get(i).getApply() || tasks.get(i).getWeight()!=0) {
            IKCoordinateTask task = (IKCoordinateTask)tasks.get(i);
            //IKCoordinateTask taskCopy = IKCoordinateTask.safeDownCast(task.clone()); // C++-side copy
            fullTaskSet.cloneAndAppend(task);
         }
      }
   }

   private IKCoordinateTask get(int i) { return (IKCoordinateTask)tasks.get(i); }
   private Coordinate.MotionType getMotionType(int i) { return model.getCoordinateSet().get(i).getMotionType(); }
  
   public ValueType getValueType(int i) {
      IKCoordinateTask.ValueType taskValueType = get(i).getValueType();
      if(taskValueType == IKCoordinateTask.ValueType.DefaultValue) return ValueType.DefaultValue;
      else if(taskValueType == IKCoordinateTask.ValueType.ManualValue) return ValueType.ManualValue;
      else return ValueType.FromFile;
   }
   public void setValueType(int i,ValueType type) {
      if(getValueType(i)!=type) {
         switch(type) {
            case DefaultValue: get(i).setValueType(IKCoordinateTask.ValueType.DefaultValue); break;
            case ManualValue: get(i).setValueType(IKCoordinateTask.ValueType.ManualValue); break;
            case FromFile: get(i).setValueType(IKCoordinateTask.ValueType.FromFile); break;
         }
         setModified(i);
      }
   }
   public double getValue(int i) {
      switch(getValueType(i)) {
         case FromFile:       return 0;
         case DefaultValue:   return getDefaultValue(i);
         case ManualValue:    return getManualValue(i);
      }
      return 0;
   }
   public double getDefaultValue(int i) {
      return conversion.get(i) * model.getCoordinateSet().get(i).getDefaultValue();
   }
   public double getManualValue(int i) {
      return conversion.get(i) * get(i).getValue();
   }
   public void setManualValue(int i, double value) {
      if(getValueType(i)!=ValueType.ManualValue || getValue(i)!=value/conversion.get(i)) {
         setValueType(i, ValueType.ManualValue);
         get(i).setValue(value/conversion.get(i));
         setModified(i);
      }
   }

   public boolean isLocked(int i) { return openSimContext.getLocked(model.getCoordinateSet().get(i)); }

   public boolean isValidValue(int i) {
      return getValueType(i)!=ValueType.FromFile || coordinateExistsInData.get(getName(i))!=null;
   }
}
