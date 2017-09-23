/* -------------------------------------------------------------------------- *
 * OpenSim: PlotCurveSettings.java                                            *
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
 * PlotCurveSettings
 * Author(s): Ayman Habib
 */
package org.opensim.plotter;

import java.util.Vector;
import org.opensim.modeling.ArrayDouble;
import org.opensim.plotter.JPlotterPanel;
/**
 *
 * @author Ayman
 */
public class PlotCurveSettings {
   private String name;
   private double xMin=0.0;
   private double xMax=1.0;
   private double scale=1.0;
   private double offset=0.0;
   private boolean rectify=false;
   private boolean clamp=false;
   private double ymin=0.0;
   private double ymax=1.0;
   private boolean activationOverride=false;
   private double activationValue=1.0;
   private String xLabel, yLabel;
   private Vector<PlotDataFilter> filters = new Vector<PlotDataFilter>(4);
   private boolean musclePlot=false;
   
   /** Creates a new instance of PlotCurveSettings */
   public PlotCurveSettings(JPlotterPanel plotterFrame) {
      setName(plotterFrame.getCurveName());
      setXMin(plotterFrame.getMinX());
      setXMax(plotterFrame.getMaxX());
      boolean rectify = plotterFrame.getRectify();
       
      if (rectify)
         filters.add(new PlotRectifyFilter());
      if (plotterFrame.isClamp()){
         ymin=plotterFrame.getMinY();
         ymax=plotterFrame.getMaxY();
         clamp=true;
      }
      if (plotterFrame.isActivationOverride()){
         activationValue=plotterFrame.getActivationValue();
         activationOverride=true;
      }
      setMusclePlot(plotterFrame.isBuiltinMuscleCurve());
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }


   public double getXMin() {
      return xMin;
   }

   public void setXMin(double xMin) {
      this.xMin = xMin;
   }

   public double getXMax() {
      return xMax;
   }

   public void setXMax(double xMax) {
      this.xMax = xMax;
   }

   public double getScale() {
      return scale;
   }

   public void setScale(double scale) {
      this.scale = scale;
   }

   public double getOffset() {
      return offset;
   }

   public void setOffset(double offset) {
      this.offset = offset;
   }

   public boolean isRectify() {
      return rectify;
   }

   public void setRectify(boolean rectify) {
      this.rectify = rectify;
   }

   String getXLabel() {
      return xLabel;
   }

   String getYLabel() {
      return yLabel;
   }

   public void setXLabel(String xLabel) {
      this.xLabel = xLabel;
   }

   public void setYLabel(String yLabel) {
      this.yLabel = yLabel;
   }

   Vector<PlotDataFilter> getFilters() {
      return filters;
   }

    public boolean isMusclePlot() {
        return musclePlot;
    }

    public void setMusclePlot(boolean musclePlot) {
        this.musclePlot = musclePlot;
    }

   public boolean isClamp() {
      return clamp;
   }

   public double getYmin() {
      return ymin;
   }

   public double getYmax() {
      return ymax;
   }

   public boolean isActivationOverride() {
      return activationOverride;
   }

   public double getActivationValue() {
      return activationValue;
   }
}
