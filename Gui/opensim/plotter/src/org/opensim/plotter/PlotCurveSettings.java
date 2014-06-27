/*
 *
 * PlotCurveSettings
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
