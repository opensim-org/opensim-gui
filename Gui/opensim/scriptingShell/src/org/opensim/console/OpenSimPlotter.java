/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.console;

import java.io.IOException;
import org.openide.util.Exceptions;
import org.opensim.modeling.Coordinate;
import org.opensim.modeling.Function;
import org.opensim.modeling.Model;
import org.opensim.modeling.PathActuator;
import org.opensim.modeling.PiecewiseLinearFunction;
import org.opensim.modeling.State;
import org.opensim.modeling.Storage;
import org.opensim.plotter.JPlotterPanel;
import org.opensim.plotter.PlotCurve;
import org.opensim.plotter.PlotterDB;
import org.opensim.plotter.PlotterSourceFile;
import org.opensim.plotter.PlotterSourceMotion;
import org.opensim.view.motions.FileLoadMotionAction;
import org.opensim.view.motions.MotionsDB;
import org.opensim.view.pub.OpenSimDB;

/**
 *
 * @author ayman
 * Copyright (c)  2005-2012, Stanford University, Ayman Habib
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
public final class OpenSimPlotter {
    /**
     * Create a blank PlotterPanel with specified title
     * @param title
     * @return a reference to a new Plotter Window for later use
     */
    static public JPlotterPanel createPlotterPanel(String title){
        JPlotterPanel plotterPanel = PlotterDB.getInstance().createPlotterWindow();
        plotterPanel.setTitle(title);
        return plotterPanel;
    }
    /**
     * Add data source (File) to the lis of sources available to the passed in Plotter Panel
     * This allows for reusing the same data source to recreate multiple curves rather than loading it anew
     * for every curve.
     * 
     * @param plotter PlotterPanel to use
     * @param dataFilename Full path of the file name to be used as a data source (.sto or .mot are expected)
     * @return a reference to the datasouce that was opened for later use when plotting curves
     */
    static public PlotterSourceFile addDataSource(JPlotterPanel plotter, String dataFilename){
        try {
            PlotterSourceFile src = new PlotterSourceFile(dataFilename);
            plotter.getPlotterModel().addSource(src);
            return src;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }
    
    /**
     * Add notion source (.mot) to the list of sources available to the passed in Plotter Panel
     * This allows for reusing the same data source to create multiple curves rather than loading files anew
     * for every curve. File header indicates whether angular data is in radians or degrees
     * 
     * @param plotter PlotterPanel to use
     * @param motionFilename Full path of the file name to be used as a data source (.mot is expected)
     * @return a reference to the datasouce that was opened for later use when plotting curves
     */
    static public PlotterSourceMotion addMotionSource(JPlotterPanel plotter, String motionFilename) {
        FileLoadMotionAction.loadMotion(motionFilename);
        Storage mot = MotionsDB.getInstance().getCurrentMotion(0).motion;
        PlotterSourceMotion src = new PlotterSourceMotion(mot);
        plotter.getPlotterModel().addMotion(mot);
        return src;
    }
    /**
     * Create a new curve on the passed in Plotter panel, using domain and range specified
     * as column labels in the passed in data source. File header indicates whether angular data is in radians or degrees
     * 
     * @param panel: PlotterPanel to render the curve on
     * @param src: data source used to obtain data
     * @param domain: column label corresponding to domain
     * @param range: column label corresponding to range
     * @return handle to the created curve (range vs. domain) curve is rendered as a side-effect
     */
    static public PlotCurve addCurve(JPlotterPanel panel, PlotterSourceFile src, 
            String domain, String range){
          return panel.plotDataFromSource(src, domain, range);
    }
    /**
     * Create a new curve by running the MuscleAnalysis to plot built in quantities. 
     * 
     * @param panel: PlotterPanel to render the curve on
     * @param qName: quantity to plot, vaid options are: 
     * moment.${genCoordName}, 
     * momentarm.${genCoordName}, 
     * "Length",
     * "FiberLength",
     * "TendonLength",
     * "NormalizedFiberLength",
     * "TendonForce",
     * "ActiveFiberForce",
     * "PassiveFiberForce",
     * "FiberForce"
     * 
     * @param muscleName: name of a muscle of interest as it appears in the model
     * @param genCoordName: name of generalized coordinate as it appears in model
     * @return handle to PlotCurve
     */
    static public PlotCurve addAnalysisCurve(JPlotterPanel panel, String qName, 
                String muscleName, String genCoordName) {
        return panel.showAnalysisCurve(qName, muscleName, genCoordName);
    }
    /**
     * 
     * @param panel to draw on
     * @param qName curve name/legend
     * @param pathActuatorName PathActuator name
     * @param genCoordName Gencoord name
     * @return curve representing the moment arm of pathActuatorName wrt genCoordName
     */
     static public PlotCurve addMomentArmCurve(JPlotterPanel panel, 
                String pathActuatorName, String genCoordName) {
        Model mdl = OpenSimDB.getInstance().getCurrentModel();
        if (mdl == null) return null;
        Coordinate coord = mdl.getCoordinateSet().get(genCoordName);
        if (coord ==null) return null;
        double rmin = coord.getRangeMin();
        double rmax = coord.getRangeMax();
        double step = (rmax - rmin)/100.0;
        State s = OpenSimDB.getInstance().getContext(mdl).getCurrentStateCopy();
        PathActuator pa = PathActuator.safeDownCast(mdl.getForceSet().get(pathActuatorName));
        PiecewiseLinearFunction f = new PiecewiseLinearFunction();
        f.setName("MomentArm_"+pathActuatorName);
        double convToDegrees = 1.0;
        String mtype = coord.get_motion_type();
        if (coord.get_motion_type().equalsIgnoreCase("rotational")) 
            convToDegrees = 180.0/Math.PI;
        for(double g=rmin; g<rmax; g+=step){
            coord.setValue(s, g);
            double momentArm = pa.computeMomentArm(s, coord);
            //System.out.println("Coord ="+g*convToDegrees+" ma="+momentArm);
            f.addPoint(g*convToDegrees, momentArm);
        }
        return addFunctionCurve(panel, f);
    }
   /**
     * Create a new curve by running the MuscleAnalysis to plot built quantity against passed in motion file
     * 
     * @param panel
     * @param qName: quantity name vaid options are: 
     * moment.${genCoordName}, 
     * momentarm.${genCoordName}, 
     * "Length",
     * "FiberLength",
     * "TendonLength",
     * "NormalizedFiberLength",
     * "TendonForce",
     * "ActiveFiberForce",
     * "PassiveFiberForce",
     * "FiberForce"
     * @param muscleName: name of a muscle of interest as it appears in the model
     * @param genCoordName: name of generalized coordinate as it appears in model
     * @return handle to PlotCurve
     */
    //  sourceY=(new PlotterSourceAnalysis(currentModel, plotterModel.getStorage(internalName+coordinateName, currentModel), qName));

    static public PlotCurve addMotionCurve(JPlotterPanel panel, String qName, 
                String muscleName, PlotterSourceMotion dataSource) {
        return panel.showMotionCurve(qName, muscleName, dataSource);
    }
    /**
     * Create a new curve representing an arbitrary OpenSimFunction and add it to the passed in plotter panel
     * @param panel: panel to add the curve to
     * @param function: Any OpenSim function
     * @return a handle to the created curve 
     */
    static public PlotCurve addFunctionCurve(JPlotterPanel panel, Function function) {
        return panel.showFunctionCurve(function);
    }
    /**
     * Set the Legend for the passed in curve
     * @param cv : handle to PlotCurve
     * @param legend: new String for legend
     */
    static public void setCurveLegend(PlotCurve cv, String legend) {
        cv.setLegend(legend);
    }
    /**
     * Set the color to use for the passed in curve as RGB
     * @param panel: Plotter panel
     * @param series: Curve number
     * @param r :red color component 0,1
     * @param g: green color component 0,1
     * @param b: blue color component 0, 1
     */
    static public void setCurveColor(JPlotterPanel panel, int series, float r, float g, float b){
        panel.getPlotterModel().setColorRGB(series, r, g, b);
    }    
    /**
     * Export all the data on the plotter window to an sto file
     * 
     * @param panel: reference to the PlotterPanel whose curves are being exported.
     * @param fileName: Name of output file name to export data to.
     */
    static public void exportData(JPlotterPanel panel, String fileName){
        panel.getPlotterModel().getCurrentPlot().exportData(fileName);
    }
    /**
     * Get a handle to the last opened plotter window to modify it
     * @return handle to last opened plotterPanel
     */
    static public JPlotterPanel getLastPlotterPanel() {
        return PlotterDB.getInstance().getLastPlotterPanel();
    }
   
}