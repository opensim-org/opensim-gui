/*
 *
 * PlotterModel
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
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.opensim.modeling.Analysis;
import org.opensim.modeling.AnalysisSet;
import org.opensim.modeling.AnalyzeTool;
import org.opensim.modeling.ArrayStorage;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.Model;
import org.opensim.modeling.MuscleAnalysis;
import org.opensim.modeling.Storage;
import org.opensim.view.experimentaldata.ModelForExperimentalData;

/**
 *
 * @author Ayman. This class represents the data structure backing the Plotter dialog.
 * Initially it maps to a single JFree.ChartPanel but eventually will correspond to a list 
 * of those.
 */
public class PlotterModel {

    /**
     * @param aFigureNumber the figureNumber to set
     */
    public static void setFigureNumber(int aFigureNumber) {
        figureNumber = aFigureNumber;
    }
   // All sources (Storages) are kept here as a linear list
   private ArrayList<PlotterSourceInterface>    sources = new ArrayList<PlotterSourceInterface>(4); // Data loaded from files
   ArrayList<String>     availableQuantities = new ArrayList<String>(50);
   ArrayList<Plot>     availablePlots = new ArrayList<Plot>(2);
   private int currentPlotIndex=0;
   private PlotTreeModel              plotTreeModel = new PlotTreeModel();   // Tree on the right
   private Vector<Model> loadedModels = new Vector<Model>(4);
   Hashtable<Model, AnalyzeTool> models2AnalyzeToolInstances = new Hashtable<Model, AnalyzeTool>(4);
   private static int figureNumber = 1;
    private static String[] builtinQuantities= new String[] {
        "moment arm",
        "moment",
        "muscle-tendon length",
        "fiber-length",
        "tendon-length",
        "normalized fiber-length",
        "tendon force",
        "active fiber-force",
        "passive fiber-force",
        "total fiber-force"
    };

    private static String[] builtinQuantitiesAnalysis= new String[] {
        "MuscleAnalysis",
        "MuscleAnalysis",
        "MuscleAnalysis",
        "MuscleAnalysis",
        "MuscleAnalysis",
        "MuscleAnalysis",
        "MuscleAnalysis",
        "MuscleAnalysis",
        "MuscleAnalysis",
        "MuscleAnalysis"
    };
    private static String[] builtinQuantitiesStorageName= new String[] {
        "MomentArm",
        "Moment",
        "Length",
        "FiberLength",
        "TendonLength",
        "NormalizedFiberLength",
        "TendonForce",
        "ActiveFiberForce",
        "PassiveFiberForce",
        "FiberForce"
    };
   private final String DEFAULT_X_LABEL="x-label";
   private final String DEFAULT_Y_LABEL="y-label";
   /** Creates a new instance of PlotterModel */
   public PlotterModel() {
         Plot figure = new Plot("Figure "+String.valueOf(figureNumber), DEFAULT_X_LABEL, DEFAULT_Y_LABEL);
         availablePlots.add(0, figure);
         plotTreeModel.addPlot(figure);
         figureNumber++;
   }
   
   public PlotterSourceFile addFile(String filename)
   {
      try {
         PlotterSourceFile newSource = new PlotterSourceFile(filename);
         sources.add(newSource);
         return newSource;
      } catch (IOException ex) {
         ex.printStackTrace();
         return null;
      }
   }
   
    public void addModel(Model aModel)
    {
        if (loadedModels.contains(aModel)|| aModel instanceof ModelForExperimentalData)
            return;
        // Create AnalyzeTool with built in muscle and moment analyses
        AnalyzeTool tool = new AnalyzeTool(aModel);
        AnalysisSet analyses=aModel.getAnalysisSet();
                
        for(int i=0; i<analyses.getSize(); i++){
            Analysis a = analyses.get(i);
            ArrayStorage storages = a.getStorageList();
            for(int j=0; j<storages.getSize(); j++){
               Storage str = storages.get(j);
               sources.add(new PlotterSourceAnalysis(aModel, storages.get(j), a.getName()+"."+str.getName()));
            }
        }
        //tool.setSolveForEquilibriumForAuxiliaryStates(true);
        models2AnalyzeToolInstances.put(aModel, tool);
    }
  /**
    * Get available quantities to use as a Domain variable
    * source is a boolean used to specify if Analysis or File radio button 
    * have been selected.
    */

   public ArrayList<PlotterSourceMotion> getLoadedMotionSources() {
      ArrayList<PlotterSourceMotion> motionSources = new ArrayList<PlotterSourceMotion>();
      for(int i=0; i<sources.size(); i++){
         if (sources.get(i) instanceof PlotterSourceMotion)
            motionSources.add((PlotterSourceMotion) sources.get(i));
      }
      return motionSources;
   }

   public ArrayList<PlotterSourceFile> getLoadedFileSources() {
      ArrayList<PlotterSourceFile> fileSources = new ArrayList<PlotterSourceFile>();
      for(int i=0; i<sources.size(); i++){
         if (sources.get(i) instanceof PlotterSourceFile)
            fileSources.add((PlotterSourceFile) sources.get(i));
      }
      return fileSources;
   }

   /**
    * Add a curve to the PlotterModel. If a new figure is created as a side effect of this it's returned
    * otherwise null is returned to indicate that no new Panel was created. The actual addition should be
    * done by the caller JPlotterFrame
    */
   PlotCurve addCurveSingleRangeName(String title, PlotCurveSettings plotCurveSettings, 
           PlotterSourceInterface source1, String string1, 
           PlotterSourceInterface source2, String string2) {

      Plot currentPlot = availablePlots.get(currentPlotIndex);
      boolean motionCurve=false;
      if (source1 instanceof PlotterSourceMotion || source1 instanceof PlotterSourceFile){  
          motionCurve=true;
      }
      PlotCurve newCurve = new PlotCurve(plotCurveSettings, source1, string1, source2, string2);
      currentPlot.add(newCurve);
      
      plotTreeModel.addPlotCurveToTree(newCurve);
      //currentPlot.setTitle(title);
      // if motionCurve xlabel is motion name, ylabel string2
      //System.out.println("Curve domain, range=["+string1+"],["+string2+"]");
      if (motionCurve){
         if(string1.equalsIgnoreCase(source1.getDisplayName())){
            newCurve.setXLabel(source1.getDisplayName());
            if (source2 instanceof PlotterSourceAnalysis)
               newCurve.setYLabel(source2.getDisplayName());
            else
               newCurve.setYLabel(string2);
         }
         else{
            newCurve.setXLabel(string1);
            newCurve.setYLabel(string2);
        }
      }
      else{
         newCurve.setXLabel(string1);
         newCurve.setYLabel(source2.getDisplayName());
      }
      updatePlotXYLabels(currentPlot, newCurve);
      return newCurve;
   }

    private void updatePlotXYLabels(final Plot currentPlot, final String newDomainName, String addYLabel) {
             
       //System.out.println("Adding labels ["+newDomainName+"], ["+addYLabel+"] to plot");
        XYPlot dPlot = currentPlot.getChartPanel().getChart().getXYPlot();
        String oldLabel = dPlot.getDomainAxis().getLabel();
        String newLabel=oldLabel;
        if (oldLabel.equalsIgnoreCase("")||oldLabel.equalsIgnoreCase(getDefaulAxisLabel(true)))
            newLabel=newDomainName;
        else if (!oldLabel.contains(newDomainName))
            newLabel += ", "+newDomainName;
        
        dPlot.getDomainAxis().setLabel(newLabel);
        // Now Y
        oldLabel=dPlot.getRangeAxis().getLabel();
        newLabel=oldLabel;
        if (oldLabel.equalsIgnoreCase("")||oldLabel.equalsIgnoreCase(getDefaulAxisLabel(false)))   // First curve
            newLabel=addYLabel;
        else if (!oldLabel.contains(addYLabel))
            newLabel=oldLabel+", "+addYLabel;
        
        dPlot.getRangeAxis().setLabel(newLabel);
    }

   PlotTreeModel getPlotTreeModel() {
      return plotTreeModel;
   }
   /**
    * Delete all curves from a figure 
    * Delete the panel too?
    */
   void deletePlot(Plot figToDelete) {
      PlotNode figNode = plotTreeModel.findPlotNode(figToDelete);
      figNode.removeFromParent();
      
   }

   void deleteCurve(PlotCurve cvToDelete) {
      PlotNode figNode = plotTreeModel.findPlotNode(cvToDelete);
      if (figNode ==null)  // Owner figure might have been deleted already
         return;
      
      // Actual object deletion
      ((Plot)figNode.getUserObject()).deleteCurve(cvToDelete);
      // Removal from tree
      PlotCurveNode cNode = plotTreeModel.findCurveNode(cvToDelete);
      figNode.remove(figNode.getIndex(cNode));
      plotTreeModel.reload((TreeNode) figNode);
      
      XYPlot dPlot = getCurrentPlot().getChartPanel().getChart().getXYPlot();
      dPlot.getDomainAxis().setLabel("");
      dPlot.getRangeAxis().setLabel("");
      // update plot labels with left over curves
      int numChildren = figNode.getChildCount();
      for(int i=0; i< numChildren; i++){
         // Find curve for node and append if not included already
         PlotCurveNode curveNode = (PlotCurveNode)figNode.getChildAt(i);
         updatePlotXYLabels( (Plot)figNode.getUserObject(),
                 (PlotCurve)curveNode.getUserObject());       
      }
   }

   public Plot getCurrentPlot()
   {
      return availablePlots.get(currentPlotIndex);
   }

   void updateCurve(PlotCurve currentCurve, String title, 
           PlotCurveSettings plotCurveSettings, 
           PlotterSourceInterface domainSource, String domainColumnName, 
           PlotterSourceInterface rangeSource, String rangeColumnName){
      
      Plot currentPlot = availablePlots.get(currentPlotIndex);
      //currentPlot.setTitle(title);
      currentCurve.update(title, plotCurveSettings, domainSource, domainColumnName, rangeSource, rangeColumnName);
      // Find path and mark it as changed to update the tree 
       TreeNode[] path=plotTreeModel.getPathToRoot((TreeNode) plotTreeModel.getRoot());
       PlotCurveNode cNode = plotTreeModel.findCurveNode(currentCurve);
       PlotNode fNode = (PlotNode)((TreeNode)cNode).getParent();
       plotTreeModel.reload(fNode);
   }

   Plot getPlotForCurve(PlotCurve cv) {
      PlotCurveNode cNode = plotTreeModel.findCurveNode(cv);
      PlotNode fNode = (PlotNode)((TreeNode)cNode).getParent();
      return ((Plot)fNode.getUserObject());
   }

   public void addSource(PlotterSourceInterface src) {
      sources.add(src);
   }

   PlotterSourceFile getSource(String fileName, String columnName) {
      // Cycle thru availabe sources, check for their names and if found check name exists
      for(int i=0; i<sources.size(); i++){
         if (sources.get(i) instanceof PlotterSourceFile){
            PlotterSourceFile src = (PlotterSourceFile) sources.get(i);
            
            if (src.getDisplayName().compareTo(fileName)==0){
               // Make a storage and check that columnName is valid
               if (columnName.compareTo("time")==0)
                  return src;
               if(src.getStorage().getStateIndex(columnName)!=-1)
                  return src;
               else
                  return null;
            }
         }
      }
      return null;
   }
   
   int countSources() {
       return sources.size();
   }

    ArrayList<PlotterSourceAnalysis> getAnalysisSources() {
      ArrayList<PlotterSourceAnalysis> analysisSources = new ArrayList<PlotterSourceAnalysis>();
      for(int i=0; i<sources.size(); i++){
         if (sources.get(i) instanceof PlotterSourceAnalysis)
            analysisSources.add((PlotterSourceAnalysis) sources.get(i));
      }
      return analysisSources;
    }
    /**
     * find a source by its name. Used from analysis customizers.
     */ 
    PlotterSourceInterface findSource(String displayName)
    {
      for(int i=0; i<sources.size(); i++){
         if (sources.get(i).getDisplayName().equals(displayName))
            return sources.get(i);
      }
      return null;
    }
    public AnalyzeTool getAnalyzeTool(Model model)
    {
       return models2AnalyzeToolInstances.get(model);
    }

   public PlotterSourceInterface addMotion(Storage nextMotion) {
      ArrayList<PlotterSourceMotion> loadedMotions=getLoadedMotionSources();
      for(int i=0; i<loadedMotions.size(); i++){
         if (loadedMotions.get(i).getStorage().equals(nextMotion))
            return loadedMotions.get(i);
      }
      PlotterSourceMotion newMotion = new PlotterSourceMotion(nextMotion);
      sources.add(newMotion);
      return newMotion;
   }

    Storage getStorage(String qName, Model model) {
        AnalyzeTool tool=getAnalyzeTool(model);
        AnalysisSet analyses=model.getAnalysisSet();
        String[] qs=getBuiltinQuantities();
        for(int i=0; i<qs.length; i++){
            if (qName.equalsIgnoreCase(qs[i])){
                return analyses.get(builtinQuantitiesAnalysis[i]).getStorageList().get(builtinQuantitiesStorageName[i]);
            }            
        }
        // Should get here only if moment arm
        int numStorages = analyses.get("MuscleAnalysis").getStorageList().getSize();
        if (analyses.get("MuscleAnalysis").getStorageList().getIndex(qName)==-1){
            //String anXml = analyses.get("MuscleAnalysis").dump();
            MuscleAnalysis.safeDownCast(analyses.get("MuscleAnalysis")).setComputeMoments(true);
            MuscleAnalysis.safeDownCast(analyses.get("MuscleAnalysis")).allocateStorageObjects();
        }
        /*ArrayStorage ast = analyses.get("MuscleAnalysis").getStorageList();
        for(int i=0;i<ast.getSize();i++)
            System.out.println(ast.get(i).getName());
        System.out.println("----");*/
        return analyses.get("MuscleAnalysis").getStorageList().get(qName);
    }

    void configureAnalyses(AnalyzeTool tool, PlotterSourceAnalysis analysisSource, String domainName, String[] rangeNames) {
        AnalysisSet analyses=tool.getModel().getAnalysisSet();
        String shortName = analysisSource.toString();
        String[] qs=getBuiltinQuantities();
        ArrayStr muscleNames= new ArrayStr();
        for(int l=0;l<rangeNames.length;l++){
           muscleNames.append(rangeNames[l]);
        }
        boolean computeMoments= (analysisSource.getDisplayName().toLowerCase().contains("moment"));

        for(int i=0; i<qs.length; i++){
             Analysis an = analyses.get(builtinQuantitiesAnalysis[i]);
             MuscleAnalysis man = MuscleAnalysis.safeDownCast(an);
             if (man!=null){
                man.setMuscles(muscleNames);
                man.setComputeMoments(computeMoments);
             }
             an.setOn(true);
             //an.print("ma.xml");
             break;
        }
        if (shortName.startsWith("MomentArm") || shortName.startsWith("Moment")){
                Analysis an = analyses.get("MuscleAnalysis");
                an.setOn(true);
                String coordinateY=shortName.substring(shortName.indexOf('_')+1);
                ArrayStr coordsArray = new ArrayStr();
                coordsArray.append(coordinateY);  // This's bad and will slow things down but is a workaround MuscleAnalysis change that creates storages too late for us
                if (MuscleAnalysis.safeDownCast(an)!=null)
                   ((MuscleAnalysis)MuscleAnalysis.safeDownCast(an)).setCoordinates(coordsArray);   
        }
    }
    
    static public String[] getBuiltinQuantities() {
        return builtinQuantities;
    }
    /**
     * string is an internal name for a Stroage that maps into a built in quantity
     */
    String getQuantityDisplayName(String string) {
        for (int i=0; i<builtinQuantitiesStorageName.length; i++){
            if (builtinQuantitiesStorageName[i].compareTo(string)==0)
                return builtinQuantities[i];
        }
        return null;
    }
    private String getDefaulAxisLabel(boolean isDomain)
    {
        if (isDomain)
            return DEFAULT_X_LABEL;
        else
            return DEFAULT_Y_LABEL;
    }

    PlotCurve addCurveMultipleRangeNames(String title, PlotCurveSettings settings, PlotterSourceInterface sourceX, String string, 
            PlotterSourceInterface sourceY, String[] rangeNames) {
      Plot currentPlot = availablePlots.get(currentPlotIndex);
      String sumString = makeSumString(rangeNames);
      PlotCurve newCurve = new PlotCurve(settings, sourceX, string, sourceY, sumString);
      currentPlot.add(newCurve);
      
      plotTreeModel.addPlotCurveToTree(newCurve);
      //currentPlot.setTitle(title);
      newCurve.setXLabel(string);
      newCurve.setYLabel(sourceY.getDisplayName());
      updatePlotXYLabels(currentPlot, newCurve);

     return newCurve;
    }

    public static String makeSumString(final String[] rangeNames) {
        String sumString="";
        for(int i=0; i<rangeNames.length; i++){
            if (i>0)
               sumString +="+";
            sumString += rangeNames[i];
        }
        return sumString;
    }

   void fireChangeEvent(DefaultMutableTreeNode node) {
      plotTreeModel.nodeChanged(node);
   }
   /**
    * When a model is changed, motions from other models should not be available to plot against.
    * Remove all motions here and then they are added one by one.
    */
   void removeAllMotions() {
      for(int i=sources.size()-1; i>=0; i--){
         if (sources.get(i) instanceof PlotterSourceMotion)
            sources.remove(i);
      }
      
   }

   private void updatePlotXYLabels(Plot currentPlot, PlotCurve newCurve) {
      updatePlotXYLabels(currentPlot, newCurve.getXLabel(), newCurve.getYLabel());
   }

   void renameMotion(Storage storage) {
       ArrayList<PlotterSourceMotion> motionSources=getLoadedMotionSources();
       for(int i=0; i<motionSources.size(); i++){
         if (motionSources.get(i).getStorage().equals(storage)){
            motionSources.get(i).updateMotionName();
            return;
         }
      }
       
   }

   void removeMotion(Storage storage) {
      for(int i=0; i<sources.size(); i++){
         if (sources.get(i) instanceof PlotterSourceMotion &&
                 sources.get(i).getStorage().equals(storage)){
            sources.remove(i);
            break;
         }
      }
   }
     public void setColorRGB(int series, float r, float g, float b){
       Plot currentPlot = availablePlots.get(currentPlotIndex);
       XYPlot dPlot = currentPlot.getChartPanel().getChart().getXYPlot();
       XYItemRenderer renderer = dPlot.getRenderer();
       renderer.setSeriesPaint(series, new Color(r, g, b));
       //renderer.setBaseStroke(new BasicStroke(3.0f));
   }
   
   public void setStroke(float size){
       Plot currentPlot = availablePlots.get(currentPlotIndex);
       XYPlot dPlot = currentPlot.getChartPanel().getChart().getXYPlot();
       XYItemRenderer renderer = dPlot.getRenderer();
       //renderer.setSeriesPaint(0, new Color(r, g, b));
       renderer.setBaseStroke(new BasicStroke(size));
   }
   
   // cleanup method intended to release resource and attempts to force gc
    void cleanup() {
        sources.clear();
        availablePlots.clear();
        models2AnalyzeToolInstances.clear();
        plotTreeModel = null;
        System.gc();
    }
}
