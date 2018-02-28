/* -------------------------------------------------------------------------- *
 * OpenSim: OpenSimPlotter.java                                               *
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
import org.opensim.utils.DialogUtils;
import org.opensim.utils.ErrorDialog;
import org.opensim.view.motions.FileLoadMotionAction;
import org.opensim.view.motions.MotionsDB;
import org.opensim.view.pub.OpenSimDB;
/**
 *
 * @author Ayman
 * 
 * OpenSimPlotter is top level class to plotting functionality for GUI scripting
 * 
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
            ErrorDialog.displayExceptionDialog(ex);
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
     * @param panel PlotterPanel to render the curve on
     * @param src data source used to obtain data
     * @param domain column label corresponding to domain
     * @param range column label corresponding to range
     * @return handle to the created curve (range vs. domain) curve is rendered as a side-effect
     */
    static public PlotCurve addCurve(JPlotterPanel panel, PlotterSourceFile src, 
            String domain, String range){
        if (src.isValidName(domain) && src.isValidName(range))
          return panel.plotDataFromSource(src, domain, range);
        else {
            ErrorDialog.showMessageDialog("Invalid specification of domain and range: "+domain+", "+range+" please check and retry.");
            return null;
        }
    }
    /**
     * Create a new curve by running the MuscleAnalysis to plot built in quantities. 
     * 
     * @param panel PlotterPanel to render the curve on
     * @param qName quantity to plot, vaid options are: 
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
     * @param muscleName name of a muscle of interest as it appears in the model
     * @param genCoordName name of generalized coordinate as it appears in model
     * @return handle to PlotCurve
     */
    static public PlotCurve addAnalysisCurve(JPlotterPanel panel, String qName, 
                String muscleName, String genCoordName) {
            return panel.showAnalysisCurve(qName, muscleName, genCoordName);
    }
    /**
     * Create a curve representing momentArm of passed in PathActuator relative
     * to a specific Coordinate
     * @since 3.1
     * 
     * @param panel to draw on
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
        Coordinate.MotionType mtype = coord.getMotionType();
        if (mtype == Coordinate.MotionType.Rotational) 
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
     * @param qName quantity name vaid options are: 
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
     * @param muscleName name of a muscle of interest as it appears in the model
     * @return handle to PlotCurve
     */
    //  sourceY=(new PlotterSourceAnalysis(currentModel, plotterModel.getStorage(internalName+coordinateName, currentModel), qName));

    static public PlotCurve addMotionCurve(JPlotterPanel panel, String qName, 
                String muscleName, PlotterSourceMotion dataSource) {
        return panel.showMotionCurve(qName, muscleName, dataSource);
    }
    /**
     * Create a new curve representing an arbitrary OpenSim::Function and add it to the passed in plotter panel
     * If panel contains plots then first one is used to obtain domain bounds, otherwise getMinX()-getMaxX is assumed.
     * 
     * @since 3.1
     * 
     * @param panel panel to add the curve to
     * @param function Any OpenSim function
     * @return a handle to the created curve after adding it to the plot window
     */
    static public PlotCurve addFunctionCurve(JPlotterPanel panel, Function function) {
        return panel.showFunctionCurve(function);
    }
    /**
     * Set the Legend for the passed in curve
     * @param cv : handle to PlotCurve
     * @param legend : new String for legend
     */
    static public void setCurveLegend(PlotCurve cv, String legend) {
        cv.setLegend(legend);
    }
    /**
     * Set the color to use for the passed in curve as RGB
     * @param panel : Plotter panel
     * @param series : Curve number
     * @param r :red color component 0,1
     * @param g : green color component 0,1
     * @param b : blue color component 0, 1
     */
    static public void setCurveColor(JPlotterPanel panel, int series, float r, float g, float b){
        panel.getPlotterModel().setColorRGB(series, r, g, b);
    }    
    /**
     * Export all the data on the plotter window to an sto file
     * 
     * @param panel reference to the PlotterPanel whose curves are being exported.
     * @param fileName : Name of output file name to export data to.
     */
    static public void exportData(JPlotterPanel panel, String fileName){
        panel.getPlotterModel().getCurrentPlot().exportData(fileName);
    }
    /**
     * Get a handle to the last opened plotter window to modify it.
     * This is useful to add curves using scripts to a plot window created using the GUI Tools->Plot
     * 
     * @since 3.1
     * 
     * @return handle to last opened plotterPanel
     */
    static public JPlotterPanel getLastPlotterPanel() {
        return PlotterDB.getInstance().getLastPlotterPanel();
    }
   
}