/* -------------------------------------------------------------------------- *
 * OpenSim: PlotCurve.java                                                    *
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
 * PlotCurve
 * Author(s): Ayman Habib
 */
package org.opensim.plotter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Vector;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.opensim.modeling.Coordinate;
import org.opensim.modeling.ArrayDouble;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.CoordinateSet;
import org.opensim.modeling.Storage;
import org.opensim.view.pub.OpenSimDB;

/**
 *
 * @author Ayman
 */
public class PlotCurve {
   
   private XYSeries    curveSeries=null;
   private boolean      timeDependent=false;
   private PlotCurveSettings    settings;
   // Source for domain, range needed so that display name (including file can be reconstructed)
   private PlotterSourceInterface domainSource;
   private PlotterSourceInterface rangeSource;
   //private int domainStorageIndex;
   //private int rangeStorageIndex;
   private String domainName;
   private String[] rangeNames;
   private boolean muscleCurve=false;
   private String xLabel;
   private String yLabel;
   
   /**
    * Creates a new instance of PlotCurve
    */
   public PlotCurve(PlotCurveSettings plotCurveSettings, 
           PlotterSourceInterface sourcex, String stringx, 
           PlotterSourceInterface sourcey, String stringy) {
      settings = plotCurveSettings;
      domainSource=sourcex;
      rangeSource=sourcey;
      Storage domainStorage=sourcex.getStorage();
      Storage rangeStorage=sourcey.getStorage();
      String localX = stringx;
      String localY = stringy;
      
      if (stringx.contains(":")){   // Strip qualifiers if any
         String[] names=stringx.split(":",-1);
         localX=names[names.length-1];
      }
      // If motion and stringx  = motion name we really mean time
      if (domainSource instanceof PlotterSourceMotion &&
              domainSource.getDisplayName().startsWith(localX)){    // We'really plotting against time'
          localX="time";
      }
      if (stringy.contains(":")){   // Strip qualifiers if any
         String[] names=stringy.split(":",-1);
         localY=names[names.length-1];
      }
      // The following code assumes x, y are parrallel arrays of the same size
      // which should be enforced by the GUI.
      // In case this restriction is removed, rangeStorage will need to be sampled
      // at domain sample values (e.g. plot quantities against time coming from another storage
       ArrayDouble xArray = getDataArrayFromStorage(domainStorage, localX, true, sourcex.convertAngularUnits());
       ArrayDouble yArray = getDataArrayFromStorage(rangeStorage, localY, false, sourcey.convertAngularUnits());
        
       int xSize = xArray.getSize();
       int ySize = yArray.getSize();
       if (xSize != ySize)
          throw new UnsupportedOperationException("Domain and range sizes are different"+xSize+" vs."+ySize+". An internal bug.");
       
       int size = xArray.getSize();
       // find range of values to display based on minx, maxx
       // this assumes some ordering on x values so that the set of xValues plotted
       // are those at or higher than the first occurance of the value xMin
       // and less than the last occurance of the value xMax.
       
       int startIndex=xArray.findIndex(plotCurveSettings.getXMin());
       if (startIndex ==-1) // Cut to bounds with data
           startIndex=0;
       int endIndex=xArray.findIndex(plotCurveSettings.getXMax());
       if (endIndex ==-1) // Cut to bounds with data
           endIndex=xArray.getSize()-1;
       double[] yFiltered = applyFilters(plotCurveSettings.getFilters(), yArray, startIndex, endIndex);
       // Make an XYSeries to hold the data and keep a ref to it with the curve
       XYSeries curveSeries = new XYSeries(plotCurveSettings.getName(), false, true);
       setCurveSeries(curveSeries);   // user named curves
       for (int i = startIndex;i<= endIndex;i++){
           getCurveSeries().add(xArray.getitem(i),yFiltered[i-startIndex]) ;//add the computed values to the series
       }
       //getCurveSeries().setKey(stringy);  // do not overwrite users specified name
       domainName = new String(stringx);
       String[] names=stringy.split("\\+");
       rangeNames = new String[names.length];
       System.arraycopy(names, 0, rangeNames, 0, names.length);
       // Make description here after everything is set
       curveSeries.setDescription(makeDescription());
    }
   public PlotCurve(PlotCurveSettings plotCurveSettings, 
           ArrayList xValues, 
           ArrayList yValues, String stringy) {
       XYSeries curveSeries = new XYSeries(plotCurveSettings.getName(), false, true);
       setCurveSeries(curveSeries);   // user named curves
       for (int i = 0; i<xValues.size();i++){
           getCurveSeries().add((Double)xValues.get(i), (Double)yValues.get(i));//add the computed values to the series
       }
       curveSeries.setDescription(plotCurveSettings.getName());
       rangeNames = new String[]{stringy};
   }
   private ArrayDouble getDataArrayFromStorage(final Storage storage, final String colName, boolean isDomain, boolean convertAnglesToDegrees ) {
      ArrayDouble Array = new ArrayDouble(storage.getSize());
      if (colName.equalsIgnoreCase("time")){
         storage.getTimeColumnWithStartTime(Array, storage.getFirstTime());
      }
      else{
         String[] colNames=colName.trim().split("\\+",-1);
         ArrayDouble tempArray=null;
         if (colNames.length>1)
              tempArray = new ArrayDouble(storage.getSize());
         for(int i=0; i<colNames.length; i++){
            colNames[i]=colNames[i].trim();
             if (i==0){
                storage.getDataColumn(colNames[i], Array, storage.getFirstTime());
                 // have to do this before clamoing since clamping is done in degrees
                if (convertAnglesToDegrees)
                    convertAnglesToDegreesIfNeeded(colNames[i], Array);
                if (settings.isClamp() && !isDomain)
                  clampDataArray(Array);
             }
             else { // get data into temporary array and then add it in place
                 tempArray = new ArrayDouble(storage.getSize());
                 storage.getDataColumn(colNames[i], tempArray);
                 if (convertAnglesToDegrees){
                    convertAnglesToDegreesIfNeeded(colNames[i], tempArray);
                 }
                 if (settings.isClamp() && !isDomain){
                   clampDataArray(tempArray);
                 }
                 for(int row=0;row<storage.getSize();row++){
                     double newValue = Array.getitem(row)+tempArray.getitem(row);
                     Array.set(row, newValue);
                 }
                 
             }
        }
      }
      return Array;
   }

   public XYSeries getCurveSeries() {
      return curveSeries;
   }

   public void setCurveSeries(XYSeries curveSeries) {
      this.curveSeries = curveSeries;
   }

   public String getLegend() {
      return (String) curveSeries.getKey();
   }

   public void setLegend(String legend) {
      curveSeries.setKey(legend);
   }
   
   /**
    * Apply sequence of filters to passed in yArray in the range between start & end inclusive
    */
   private double[] applyFilters(Vector<PlotDataFilter> filters, ArrayDouble yArray, int start, int end) {
      double[] returnValues = new double[end-start+1];
      
      // Copy portion of interest from yArray into returnValues
      for(int i=start; i<=end; i++){
         returnValues[i-start]=yArray.getitem(i);
      }
      
      // Now apply the filters
      for(int i=0; i<filters.size(); i++)
         returnValues = filters.get(i).convertData(returnValues);
      
      return returnValues;
   }

   public PlotCurveSettings getSettings() {
      return settings;
   }

   public Storage getDomainStorage() {
      return domainSource.getStorage();
   }

   public Storage getRangeStorage() {
      return rangeSource.getStorage();
   }
/*
   public int getDomainStorageIndex() {
      return domainStorageIndex;
   }

   public int getRangeStorageIndex() {
      return rangeStorageIndex;
   }
*/
   void update(String title, 
           PlotCurveSettings plotCurveSettings, 
           PlotterSourceInterface sourcex, String namex, 
           PlotterSourceInterface sourcey, String namey){
      settings = plotCurveSettings;
      domainSource=sourcex;
      rangeSource=sourcey;
      Storage domainStorage=domainSource.getStorage();
      Storage rangeStorage=rangeSource.getStorage();
      ArrayDouble xArray = getDataArrayFromStorage(domainStorage, namex, true, domainSource.convertAngularUnits());
      ArrayDouble yArray = getDataArrayFromStorage(rangeStorage, namey, false, rangeSource.convertAngularUnits());
      
      // Make an XYSeries to hold the data
      setLegend(plotCurveSettings.getName());
      int size = xArray.getSize();
      // find range of values to display based on minx, maxx
      int startIndex=xArray.findIndex(plotCurveSettings.getXMin());
      if (startIndex ==-1) // Cut to bounds with data
         startIndex=0;
      int endIndex=xArray.findIndex(plotCurveSettings.getXMax());
      if (endIndex ==-1) // Cut to bounds with data
         endIndex=yArray.getSize()-1;
      
      //System.out.println("Pre-filtering data first,last="+yArray.getitem(startIndex)+", "+yArray.getitem(endIndex));
      double[] yFiltered = applyFilters(plotCurveSettings.getFilters(), yArray, startIndex, endIndex);
      getCurveSeries().clear();
      for (int i = startIndex;i< endIndex;i++){
           getCurveSeries().add(xArray.getitem(i),yFiltered[i-startIndex]) ;//add the computed values to the series
       }
   }

   PlotterSourceInterface getDomainSource() {
      return domainSource;
   }
   
   PlotterSourceInterface getRangeSource() {
      return rangeSource;
   }
   
   public void addDataPoint(double x, double y) {
      double yFiltered = applyFilters(settings.getFilters(), y);
      getCurveSeries().add(x, yFiltered) ;//add the computed values to the series
   }
   /**
    * Apply filters to one value instead of an array
    **/
    private double applyFilters(Vector<PlotDataFilter> filters, double y) {
      double[] yArray = new double[]{y};
      double[] returnValues = new double[1];
      returnValues[0]=yArray[0];
      for(int i=0; i<filters.size(); i++)
         returnValues = filters.get(i).convertData(yArray);
        return returnValues[0];
    }
    
    public String getDomainName()
    {        
        return domainName;
        /*ArrayStr labels = domainSource.getStorage().getColumnLabels();
        int sz=labels.getSize();
        return labels.getitem(domainStorageIndex+1);*/
    }
    
    public String[] getRangeNames() // To support sum of items (over muscles for example)
    {
        return rangeNames;
        /*
        ArrayStr labels = rangeSource.getStorage().getColumnLabels();
        return labels.getitem(rangeStorageIndex+1);*/
    }

   private void clampDataArray(ArrayDouble Array) {
      double dmax=settings.getYmax();
      double dmin=settings.getYmin();
      for (int i = 0;i< Array.getSize();i++){
         if (Array.getitem(i)> dmax)
            Array.setitem(i, dmax);
         else if (Array.getitem(i)< dmin)
            Array.setitem(i, dmin);
       }
      
   }

    private void convertAnglesToDegreesIfNeeded(String string, ArrayDouble tempArray) {
        // Check if name is a rotational degree of freedom in current model if so convert
        // every entry in the pased in array of data values, otherwise do nothing.
        CoordinateSet coords= OpenSimDB.getInstance().getCurrentModel().getCoordinateSet();
        ArrayStr coordinateNames = new ArrayStr();
        coords.getNames(coordinateNames);
        if (coordinateNames.findIndex(string)!=-1){
            // Check if rotational rather than translational
            Coordinate coord = coords.get(string);
            if (coord.getMotionType() == Coordinate.MotionType.Rotational){
                double conversion = Math.toDegrees(1.0);
                for(int i=0; i<tempArray.getSize(); i++){
                    tempArray.setitem(i, conversion*tempArray.getitem(i));
                }
            }
            else
                return;
        }
    }

   private String makeDescription() {
      String ret="";
      String rangeNameString = "";
      if (rangeNames.length==1){
         rangeNameString=rangeNames[0];
      }
      else {
         rangeNameString = "sum of ";
         for (int i=0; i<rangeNames.length; i++){
            rangeNameString += rangeNames[i];
            if (i != rangeNames.length-1)
               rangeNameString += "+";
         }
      }
      if (settings.isMusclePlot()){
         // both Y, X are storage files produced by analysis tool
         ret = "Plot "+rangeSource.getDisplayName();
         ret += " for "+rangeNameString;
         ret += " vs. "+domainName;
      }
      else {
         if (rangeSource instanceof PlotterSourceMotion){
            ret = "Plot data from Motion "+rangeSource.getDisplayName()+"\n";
            ret += rangeNameString;
            ret += " vs. "+domainName;
         }
         else if (rangeSource instanceof PlotterSourceFile){
            ret = "Plot data from data file "+rangeSource.getDisplayName()+"\n";
            ret += rangeNameString;
            ret += " vs. "+domainName;
         }
      }
      return ret;
   }

   public String getXLabel() {
      return xLabel;
   }

   public void setXLabel(String xLabel) {
      this.xLabel = xLabel;
   }

   public String getYLabel() {
      return yLabel;
   }

   public void setYLabel(String yLabel) {
      this.yLabel = yLabel;
   }
 
}
