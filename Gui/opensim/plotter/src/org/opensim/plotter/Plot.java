/* -------------------------------------------------------------------------- *
 * OpenSim: Plot.java                                                         *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Christopher Dembia                                 *
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
 * Plot
 * Author(s): Ayman Habib
 */
package org.opensim.plotter;

import java.awt.BasicStroke;
import java.awt.Frame;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.SeriesRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.StateVector;
import org.opensim.modeling.Storage;
import org.opensim.utils.ErrorDialog;
import org.opensim.utils.FileUtils;

/**
 *
 * @author Ayman
 */
public class Plot {
   
   XYSeriesCollection    seriesCollection=null; // All data plotted in this figure
   private ChartPanel chartPanel=null;
   String         xLabel, yLabel;
   JFreeChart     dChart=null;
   private boolean        enableToolTips; // Normally on but for dynamic plots while updating
   Paint nextPaint;
   private ArrayList<PlotCurve> curvesList = new ArrayList<PlotCurve>(4);
   private Frame ownerFrame;
   /**
    * Creates a new instance of Plot
    */
   public Plot(String title, String xLabel, String yLabel) {
      // Make blank plot
      this.xLabel=xLabel;
      this.yLabel=yLabel;
      
      seriesCollection =new XYSeriesCollection();
      dChart = ChartFactory.createXYLineChart(
              title,
              xLabel,
              yLabel,
              seriesCollection,
              org.jfree.chart.plot.PlotOrientation.VERTICAL,
              true,
              true,
              false);
      
      chartPanel = new ChartPanel(dChart);
      // Move Legend
      LegendTitle legendTitle=dChart.getLegend();
      legendTitle.setPosition(RectangleEdge.RIGHT);

      XYPlot plot = (XYPlot) dChart.getPlot();
      plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
      plot.setSeriesRenderingOrder(SeriesRenderingOrder.FORWARD);
      plot.setDomainCrosshairVisible(false);
      plot.setRangeCrosshairVisible(false);
      ((NumberAxis)plot.getRangeAxis()).setAutoRangeIncludesZero(false);
      
      XYItemRenderer r = plot.getRenderer();
      if (r instanceof XYLineAndShapeRenderer) {
         XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
         renderer.setBaseShapesVisible(false);
         renderer.setBaseShapesFilled(true);
         renderer.setStroke(new BasicStroke(2.0f));
      }
      new JOpenSimChartMouseListener(chartPanel);
      chartPanel.setDisplayToolTips(true);
      chartPanel.setInitialDelay(0);
      // Prevent text (axis labels, title) from stretching when enlarging
      // the plot window.
      chartPanel.setMaximumDrawWidth(3000);
      chartPanel.setMaximumDrawHeight(3000);
      
      // Add Export Data option
      JPopupMenu stdPopup = chartPanel.getPopupMenu();
      stdPopup.addSeparator();
      JMenuItem exportDataMenuitem = new JMenuItem("Export Data...");
      exportDataMenuitem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                // Browse for file name 
                exportDataToFile(ownerFrame);
            }
      });
      stdPopup.add(exportDataMenuitem);
   }

   void add(PlotCurve newCurve) {      
      seriesCollection.addSeries(newCurve.getCurveSeries());
      curvesList.add(newCurve);
   }

   public ChartPanel getChartPanel() {
      return chartPanel;
   }
   
   /**
    * Display name for the figure
    */
   public String getTitle() {
      return dChart.getTitle().getText();
   }

   public void setTitle(String title) {
      dChart.setTitle(title);
   }

   void deleteCurve(PlotCurve cvToDelete) {
      curvesList.remove(cvToDelete);
      seriesCollection.removeSeries(cvToDelete.getCurveSeries());
   }

   public boolean isEnableToolTips() {
      return enableToolTips;
   }

   public void setEnableToolTips(boolean enableToolTips) {
      this.enableToolTips = enableToolTips;
   }

   void setDomainCrosshair(double time) {
       if (!chartPanel.getChart().getXYPlot().isRangeCrosshairVisible())
           chartPanel.getChart().getXYPlot().setDomainCrosshairVisible(true);
      chartPanel.getChart().getXYPlot().setDomainCrosshairValue(time);
   }
      
   void exportDataToFile(Frame parentFrame) {
       String filename=FileUtils.getInstance().browseForFilenameToSave(FileUtils.MotionFileFilter, true, "", parentFrame);
       if (filename==null)
          return;
        exportData(filename);
       
    }
    
   public void exportData(String filename) {
      if (seriesCollection==null)
         return;     // Nothing to export
      try {
      BufferedWriter out = new BufferedWriter(new FileWriter(filename, true));  
      // Create a list to represent which curves have common X
      Vector<Integer> curveGroup = new Vector<Integer>(10);
      int numDistinctDomains=0;
      for (int i=0; i< curvesList.size(); i++){
         XYSeries series = seriesCollection.getSeries(i);
         String desc1=series.getDescription();
         boolean found=false;
         for(int j=0; j<i && !found; j++){
            // Check series i against series j if same domain set entry of curveGroup[i]=j and continue
            String desc2=seriesCollection.getSeries(j).getDescription();
            String temp = desc1 + "\t"+ desc2;
            if (sameDomain(series, seriesCollection.getSeries(j))){
               curveGroup.add(j);
               found=true;
            }
         }
         // i was not found, add it.
         if (!found){
            curveGroup.add(i);
            numDistinctDomains++;
         }
      }
      // e.g. curveGroup contents at this point is:
      // X1 vs Y1, X2 vs Y5, X2 vs Y1, X1 vs. Y5  would result curveGroups
      // {{0}, {1}, {0}, {1}}
      //if (numDistinctDomains != 1){
      // For now punt, later either exprt to different files using some convention or
      // append to same file with separator.
      //return;
      //}
      // {0, 1, 2, 3} -> {0, 0, 2, 2} ->{{0,0},{0,1},{2,2},{2,3}}
      if (numDistinctDomains>1){
          DialogDisplayer.getDefault().notify(new 
                  NotifyDescriptor.Message("Exported curves have different domains. Creating "+numDistinctDomains+" files."));

      }
      Vector<Integer> distinctX = new Vector<Integer>(4);
      for (int i=0; i<curveGroup.size(); i++){
         if (!distinctX.contains(curveGroup.get(i))) 
            distinctX.add((Integer)curveGroup.get(i));
      }
      //DecimalFormat numberFormat = new DecimalFormat("0.000E0");
      String origFilename = filename;
      // Assumption is that series indices are packed always 0, 1, ...
      for (int si=0; si< distinctX.size(); si++){
         if (si>0){
            int locationOfDot = filename.lastIndexOf('.');
            filename = origFilename.substring(0, locationOfDot);   // Prepend index (should append to fielename -extension""
            filename += "_"+String.valueOf(si);
            filename += origFilename.substring(locationOfDot);
         }
         Storage newStorage = new Storage();
         
         // Domain is "si", Range is "seriesCollection.getSeries(j)" where curveGroup.get(j)==distinctX.get(si);
         int currentSeriesIndex=(Integer)distinctX.get(si);
         ArrayStr columnLabels = new ArrayStr();
         columnLabels.append("time");
         XYSeries series = seriesCollection.getSeries(currentSeriesIndex);
         String domain = curvesList.get(currentSeriesIndex).getXLabel();
         if (domain==null) domain = "Domain_"+String.valueOf(si);
         columnLabels.append(domain);
         for (int j=0; j< curveGroup.size(); j++){
            if (curveGroup.get(j)==currentSeriesIndex){
               XYSeries ySeries = seriesCollection.getSeries(j);
               String nextRange = curvesList.get(j).getLegend();
               columnLabels.append(nextRange);
            }
         }
      
         newStorage.setColumnLabels(columnLabels);
         
         
         currentSeriesIndex=(Integer)distinctX.get(si);
         series = seriesCollection.getSeries(currentSeriesIndex);
         double[] [] values = series.toArray();
         for (int i=0; i<series.getItemCount(); i++){   //LAST POINT?
            StateVector nextRow = new StateVector();
            nextRow.setTime((double)i);
            nextRow.getData().append(values[0][i]);
            //out.write((values[0][i])+"\t");
            for (int j=0; j< curveGroup.size(); j++){
               if (curveGroup.get(j)==currentSeriesIndex){
                  XYSeries ySeries = seriesCollection.getSeries(j);
                  //out.write((ySeries.getY(i))+"\t");
                  nextRow.getData().append(ySeries.getY(i).doubleValue());
               }
            }
            //out.write("\n");
            newStorage.append(nextRow);
         }
         //out.write("\n\n");
         newStorage.print(filename);
      }
        // out.close();
      } catch (IOException ex) {
         ErrorDialog.displayExceptionDialog(ex);
      }
   }
   
   private boolean sameDomain(XYSeries series, XYSeries xYSeries) {
      int count = series.getItemCount();
      if (count!=xYSeries.getItemCount())
         return false;
      // same count, check first and last entry
      if (Math.abs(series.getX(0).doubleValue() - xYSeries.getX(0).doubleValue())>1E-5) {
          return false;
       }
      if (Math.abs(series.getX(count-1).doubleValue()- xYSeries.getX(count-1).doubleValue())>1E-5)
          return false;
      // We need more checking to probe intermediate X values.
      return true;
   }

    public void setOwnerFrame(Frame ownerFrame) {
        this.ownerFrame = ownerFrame;
    }
   
}
