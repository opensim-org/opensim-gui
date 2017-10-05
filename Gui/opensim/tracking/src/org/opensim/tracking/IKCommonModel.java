/* -------------------------------------------------------------------------- *
 * OpenSim: IKCommonModel.java                                                *
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

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import org.opensim.modeling.ArrayDouble;
import org.opensim.modeling.InverseKinematicsTool;
import org.opensim.modeling.MarkerData;
import org.opensim.modeling.MarkerPlacer;
import org.opensim.modeling.Model;
import org.opensim.modeling.Storage;

//===========================================================================
// IKCommonModel
//===========================================================================
// Group together some functionality common to scale (MarkerPlacer) and IK tools
public class IKCommonModel extends Observable implements Observer {
   private Model model;

   private String markerDataFileName = "";
   private MarkerData markerData = null;

   private OptionalFile coordinateDataFile = new OptionalFile();
   private Storage coordinateData = null;

   private double[] timeRange = new double[]{-1,-1};

   private IKMarkerTasksModel ikMarkerTasksModel;
   private IKCoordinateTasksModel ikCoordinateTasksModel;

   public IKCommonModel(Model model) {
      this.model = model;
      ikMarkerTasksModel = new IKMarkerTasksModel(model);
      ikCoordinateTasksModel = new IKCoordinateTasksModel(model);
      ikMarkerTasksModel.addObserver(this);
      ikCoordinateTasksModel.addObserver(this);
   }

   public IKMarkerTasksModel getIKMarkerTasksModel() { return ikMarkerTasksModel; }
   public IKCoordinateTasksModel getIKCoordinateTasksModel() { return ikCoordinateTasksModel; }

   public void update(Observable observable, Object obj) {
      setModified();
   }

   private void setModified() {
      setChanged();
      notifyObservers(null);
   }

   //------------------------------------------------------------------------
   // Validation
   //------------------------------------------------------------------------

   public boolean isValid() {
      return getMarkerDataValid() && (!getCoordinateDataEnabled() || getCoordinateDataValid())
             && ikMarkerTasksModel.isValid() && ikCoordinateTasksModel.isValid();
   }

   //------------------------------------------------------------------------
   // Filename effectively null if it's null, or "", or "Unassigned"
   //------------------------------------------------------------------------
   private boolean fileNameEffectivelyNull(String fileName) {
      return fileName==null || fileName.equals("") || fileName.equalsIgnoreCase("Unassigned");
   }
   private String getFileName(String fileName) {
      return fileNameEffectivelyNull(fileName) ? "" : fileName;
   }

   //------------------------------------------------------------------------
   // Marker data
   //------------------------------------------------------------------------
   public boolean loadMarkerData() {
      String fileName = getMarkerDataFileName();
      boolean success = true;
      markerData = null; 
      if(!fileNameEffectivelyNull(fileName)) {
         try {
            markerData = new MarkerData(fileName);
            // Note: MarkerPlacer C++ code averages before convertToUnits rather than after.  But as long as we don't actually use the
            // values from this file in the GUI it shouldn't matter that we're doing convertToUnits first here.
            markerData.convertToUnits(model.getLengthUnits());
         } catch (IOException ex) {
            markerData = null;
            success = false;
         }
      }
      ikMarkerTasksModel.markerDataChanged(markerData);
      return success;
   }

   public boolean setMarkerDataFileName(String fileName) {
      markerDataFileName = getFileName(fileName);
      boolean success = loadMarkerData();
      // If timeRange is still at its default value, try to update it with the max range for this file, otherwise
      // reclamp it to this file
      if(markerData!=null && timeRange[0]==-1 && timeRange[1]==-1) setTimeRange(markerData.getTimeRange());
      else clampTimeRangeAgainstMarkerData(timeRange);
      setModified();
      return success;
   }

   public String getMarkerDataFileName() { return markerDataFileName; }
   public MarkerData getMarkerData() { return markerData; }
   public boolean getMarkerDataValid() { return markerData!=null; }

   //------------------------------------------------------------------------
   // Coordinate data
   //------------------------------------------------------------------------
   public boolean loadCoordinateData() {
      boolean success = true;
      coordinateData = null;
      if(coordinateDataFile.isValid()) {
         try {
            coordinateData = new Storage(coordinateDataFile.fileName);
            // Possibly not really needed, since we don't really use the contents of this file
            model.getSimbodyEngine().convertDegreesToRadians(coordinateData);
         } catch (IOException ex) {
            coordinateData = null;
            success = false;
         }
      }
      ikCoordinateTasksModel.coordinateDataChanged(coordinateData);
      return success;
   }

   public boolean setCoordinateDataFileName(String fileName) {
      coordinateDataFile.fileName = fileName;
      boolean success = loadCoordinateData();
      setModified();
      return success;
   }
   public String getCoordinateDataFileName() { return coordinateDataFile.fileName; }

   public void setCoordinateDataEnabled(boolean enabled) {
      if(coordinateDataFile.enabled != enabled) {
         coordinateDataFile.enabled = enabled;
         loadCoordinateData();
         setModified();
      }
   }
   public boolean getCoordinateDataEnabled() { return coordinateDataFile.enabled; }

   public Storage getCoordinateData() { return coordinateData; }
   public boolean getCoordinateDataValid() { return coordinateData!=null; }

   //------------------------------------------------------------------------
   // Time range
   //------------------------------------------------------------------------
   public void setTimeRange(double[] newTimeRange) {
      clampTimeRangeAgainstMarkerData(newTimeRange);
      if(timeRange[0] != newTimeRange[0] || timeRange[1] != newTimeRange[1]) {
         timeRange = newTimeRange;
         setModified();
      }
   }
   public double[] getTimeRange() { return timeRange; }

   private boolean clampTimeRangeAgainstMarkerData(double[] timeRange) {
      boolean clamped = false;
      if(markerData!=null) {
         if(timeRange[0] < markerData.getStartFrameTime()) { timeRange[0] = markerData.getStartFrameTime(); clamped = true; }
         if(timeRange[1] > markerData.getLastFrameTime()) { timeRange[1] = markerData.getLastFrameTime(); clamped = true; }
      }
      return clamped;
   }

   //------------------------------------------------------------------------
   // Transfer data from/to the property structures
   //------------------------------------------------------------------------
   public void fromMarkerPlacer(MarkerPlacer markerPlacer) {
      // Marker data file
      markerData = null;
      markerDataFileName = markerPlacer.getStaticPoseFileName();
      loadMarkerData();

      // Coordinate data file
      coordinateData = null;
      coordinateDataFile.fromProperty(markerPlacer.getCoordinateFileName());
      loadCoordinateData();

      // Time range
      ArrayDouble array = markerPlacer.getTimeRange();
      timeRange = new double[]{array.getitem(0), array.getitem(1)};

      // IK task set
      ikMarkerTasksModel.fromTaskSet(markerPlacer.getIKTaskSet());
      ikCoordinateTasksModel.fromTaskSet(markerPlacer.getIKTaskSet());
   }

   public void toMarkerPlacer(MarkerPlacer markerPlacer) {
      // Marker data file
      markerPlacer.setStaticPoseFileName(markerDataFileName);

      // Coordinate data file
      markerPlacer.setCoordinateFileName(coordinateDataFile.toProperty());

      // Time range
      ArrayDouble array = new ArrayDouble();
      array.append(timeRange[0]);
      array.append(timeRange[1]);
      markerPlacer.setTimeRange(array);

      // IK task set
      ikMarkerTasksModel.toTaskSet(markerPlacer.getIKTaskSet());
      ikCoordinateTasksModel.toTaskSet(markerPlacer.getIKTaskSet());
   }

   public void fromIKTool(InverseKinematicsTool ikTool) {
      markerData = null;
      coordinateData = null;
         // Marker data file
         markerDataFileName = ikTool.getMarkerDataFileName();
         loadMarkerData();

         // Coordinate data file
         coordinateDataFile.fromProperty(ikTool.getCoordinateFileName());
         loadCoordinateData();

         // Time range
         timeRange = new double[]{ikTool.getStartTime(), ikTool.getEndTime()};
      setTimeRange(timeRange);

      // IK task set
      ikMarkerTasksModel.fromTaskSet(ikTool.getIKTaskSet());
      ikCoordinateTasksModel.fromTaskSet(ikTool.getIKTaskSet());
   }

   public void toInverseKinematicsTool(InverseKinematicsTool ikTool) {
     ikTool.setMarkerDataFileName(markerDataFileName);
     ikTool.setCoordinateFileName(coordinateDataFile.toProperty()); 
     ikTool.setStartTime(timeRange[0]);
     ikTool.setEndTime(timeRange[1]);     

      // IK task set
      ikMarkerTasksModel.toTaskSet(ikTool.getIKTaskSet());
      ikCoordinateTasksModel.toTaskSet(ikTool.getIKTaskSet());
   }
}
