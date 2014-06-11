/*
 *
 * Plot
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
         ex.printStackTrace();   // Show error dialog that file could not be written
      }
   }
   
   private boolean sameDomain(XYSeries series, XYSeries xYSeries) {
      int count = series.getItemCount();
      if (count!=xYSeries.getItemCount())
         return false;
      // same count, check first and last entry
      if (!(series.getX(0).equals(xYSeries.getX(0)))) return false;
      if (!(series.getX(count-1).equals(xYSeries.getX(count-1)))) return false;
      // We need more checking to probe intermediate X values.
      return true;
   }

    public void setOwnerFrame(Frame ownerFrame) {
        this.ownerFrame = ownerFrame;
    }
   
}
