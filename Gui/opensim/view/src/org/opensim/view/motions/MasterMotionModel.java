/* -------------------------------------------------------------------------- *
 * OpenSim: MasterMotionModel.java                                            *
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

package org.opensim.view.motions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.opensim.modeling.Model;
import org.opensim.modeling.Storage;
import org.opensim.view.pub.ViewDB;

// End of variables declaration                   

public class MasterMotionModel {

   // Info specific to a motion/model combination is pushed down to the displayer object.
   List<MotionDisplayer> displayers=new ArrayList<MotionDisplayer>(10);  
   private boolean wrapMotion=false;

   double currentTime=0; // this is the primary indicator of the current position
   Vector<Double> superMotionTimes = new Vector<Double>(100);

   private int cachedIndexClosestToCurrentTime=0;

   // -----------------------------------------------------------------------
   // Change listener stuff taken from DefaultColorSelectionModel.java
   // -----------------------------------------------------------------------
   protected transient ChangeEvent changeEvent = null;
   protected EventListenerList listenerList = new EventListenerList();
   public void addChangeListener(ChangeListener l) { listenerList.add(ChangeListener.class, l); }
   public void removeChangeListener(ChangeListener l) { listenerList.remove(ChangeListener.class, l); }
   protected void fireStateChanged()
   {
      Object[] listeners = listenerList.getListenerList();
      for (int i = listeners.length - 2; i >= 0; i -=2 ) {
         if (listeners[i] == ChangeListener.class) {
            if (changeEvent == null) {
               changeEvent = new ChangeEvent(this);
            }
            ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
         }
      }
   }
   // -----------------------------------------------------------------------

   MasterMotionModel() {
   }

   private void doUpdateAndRepaint() 
   {
      ViewDB.getInstance().applyTimeToViews(getCurrentTime());
      for(int i=0; i<displayers.size(); i++) {
         MotionDisplayer disp = displayers.get(i);
         Model dModel = disp.getModel();
         // Force rendering only for the last displayer to avoid multiple render calls per frame
         ViewDB.getInstance().updateModelDisplayNoRepaint(dModel, true, i==(displayers.size()-1));
      }
      MotionsDB motionsDB = MotionsDB.getInstance();
      motionsDB.reportTimeChange(getCurrentTime());
   }

   public void applyTime() 
   {
      if(displayers.size() == 0) return;

      for(int i=0; i<displayers.size(); i++) {
         displayers.get(i).applyTimeToModel(getCurrentTime());
      }
      
      // If in event dispatch thrread then execute synchronously
      if (SwingUtilities.isEventDispatchThread()){
         doUpdateAndRepaint();
      }
      else {
         try {
            SwingUtilities.invokeAndWait(new Runnable(){ public void run(){ doUpdateAndRepaint(); } });
         } catch (InvocationTargetException ex) {
            ex.printStackTrace();
         } catch (InterruptedException ex) {
            ex.printStackTrace();
         }
      }
   }

   private int getIndexClosestToCurrentTime()
   {
      int idx=cachedIndexClosestToCurrentTime; // shorter name for local variable...
      if(idx < 0 || idx >= superMotionTimes.size()) idx=0;
      if(currentTime >= superMotionTimes.get(idx).doubleValue()) {
         // search to the right
         int i=idx;
         while(i<superMotionTimes.size()-1 && currentTime>=superMotionTimes.get(i+1).doubleValue()) i++;
         if(i<superMotionTimes.size()-1) { 
            double left=superMotionTimes.get(i).doubleValue(), right=superMotionTimes.get(i+1).doubleValue();
            idx=(right-currentTime<currentTime-left)?i+1:i;
         } else idx=i;
      } else {
         // search to the left
         int i=idx;
         while(i>0 && currentTime<=superMotionTimes.get(i-1).doubleValue()) i--;
         if(i>0) {
            double left=superMotionTimes.get(i-1).doubleValue(), right=superMotionTimes.get(i).doubleValue();
            idx=(right-currentTime<currentTime-left)?i:i-1;
         } else idx=i;
      }
      cachedIndexClosestToCurrentTime=idx;
      return idx;
   }

   public boolean isWrapMotion() {
      return wrapMotion;
   }

   public void setWrapMotion(boolean wrapMotion) {
      this.wrapMotion = wrapMotion;
   }

   public boolean finished(int direction) {
      //int index = getIndexClosestToCurrentTime();
      //if (index==superMotionTimes.size()-1 && direction==1 || index==0 && direction==-1)
      if (currentTime==getEndTime() && direction==1 || currentTime==getStartTime() && direction==-1)
         return !wrapMotion;
      else
         return false;
   }

   public double getCurrentTime() {
      return currentTime;
   }

   // -----------------------------------------------------------------------
   // Utilities for building the super motion
   // -----------------------------------------------------------------------
   void add(Model model, Storage simmMotionData) {
      
      MotionDisplayer displayer = MotionsDB.getInstance().getDisplayerForMotion(simmMotionData);
      if (displayer == null){
          displayer = new MotionDisplayer(simmMotionData, model);
          MotionsDB.getInstance().addMotionDisplayer(simmMotionData, displayer);
      }
      displayers.add(displayer);
       buildSuperMotion(model, simmMotionData);
   }
   void add(MotionsDB.ModelMotionPair pair) {
      add(pair.model, pair.motion);
   }

   void clear() {
     // unload previously loaded motion of the same model
     for(int i=0; i<displayers.size(); i++)
        displayers.get(i).cleanupDisplay();
     displayers.clear();
     superMotionTimes.clear();  
     setTime(0);
     ViewDB.getInstance().repaintAll();
   }

   // TODO: get rid of third argument
   private void buildSuperMotion(Model model, Storage mot) {
      // Merge vector of valid times with times from passed in storage
      int numFrames = mot.getSize();
      Vector<Double> mergedTimes=new Vector<Double>(numFrames+superMotionTimes.size());
      int j=0;
      for(int i=0; i<numFrames; i++){
         double time = mot.getStateVector(i).getTime();
         while(j<superMotionTimes.size() && superMotionTimes.get(j).doubleValue()<=time) {
            mergedTimes.add(superMotionTimes.get(j));
            j++;
         }
         // avoid duplicate time values
         // TODO: use some threshold?
         if(mergedTimes.size()==0 || mergedTimes.lastElement().doubleValue()!=time) mergedTimes.add(time);
      }
      for(; j<superMotionTimes.size(); j++) 
         mergedTimes.add(superMotionTimes.get(j));
      superMotionTimes = mergedTimes;
   }

   /**
    * How many motions currently playing/ready to play
    */
   int getNumMotions() {
      return displayers.size();
   }

   MotionDisplayer getDisplayer(int index) {
      if (index < 0 || index >= displayers.size())
         return null;
      return displayers.get(index);
   }

   public MotionDisplayer getDisplayer(Storage simmMotionData){
       MotionDisplayer displayer=null;
       boolean found = false;
       for(int i=0; i<displayers.size() && !found ; i++) {
         displayer = displayers.get(i);
         Storage dStorage = displayer.getSimmMotionData();
         if (simmMotionData==dStorage){
             return displayer;
         }
      }
       return null;

   }
   String getDisplayName() {
      if (getNumMotions()==0)
         return "None";
      if (getNumMotions()==1)
         return displayers.get(0).getSimmMotionData().getName();
      else 
         return "Multiple";
   }

   // -----------------------------------------------------------------------
   // Time range
   // -----------------------------------------------------------------------
   double getStartTime()
   {
      return superMotionTimes.size()>0 ? superMotionTimes.firstElement() : 0.0;
   }

   double getEndTime()
   {
      return superMotionTimes.size()>0 ? superMotionTimes.lastElement() : 0.0;
   }

   double clampedTime(double time)
   {
      return (time < getStartTime()) ? getStartTime() : (time > getEndTime()) ? getEndTime() : time;
   }

   // -----------------------------------------------------------------------
   // Main interface functions (for modifying time)
   // -----------------------------------------------------------------------
   public void back() {
      if (superMotionTimes.size() == 0) return;
      int index = getIndexClosestToCurrentTime();
      if (index > 0){
         index--;
      }
      else{
         index = (wrapMotion) ? superMotionTimes.size()-1 : index;
      }
      setTime(superMotionTimes.get(index));
   }
   
   public void advance() {
      if (superMotionTimes.size() == 0) return;
      int index = getIndexClosestToCurrentTime();
      if (index<superMotionTimes.size()-1){
         index++;
      }
      else{
         index = (wrapMotion) ? 0 : index;
      }
      setTime(superMotionTimes.get(index));
   }
  
   public void setTime(double userTime) {
      currentTime = clampedTime(userTime);
      applyTime();
      fireStateChanged();
   }

   public void advanceTime(double dt) {
      double time = getCurrentTime()+dt;
      if(wrapMotion) {
         double range=getEndTime()-getStartTime();
         if(range > 0) { // it may be that range==0 in case this is a single-frame motion
            while(time > getEndTime()) time-=range;
            while(time < getStartTime()) time+=range;
         }
      }
      setTime(time);
   }
}
