/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.console;

import java.io.IOException;
import javax.swing.JFrame;
import org.openide.util.Exceptions;
import org.opensim.modeling.Storage;
import org.opensim.plotter.JPlotterPanel;
import org.opensim.plotter.PlotCurve;
import org.opensim.plotter.PlotterSourceFile;
import org.opensim.plotter.PlotterSourceMotion;
import org.opensim.utils.DialogUtils;
import org.opensim.view.motions.FileLoadMotionAction;
import org.opensim.view.motions.MotionsDB;

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
     * Create a blank Plotter Window/Panel with specified Title
     * @param title
     * @return a reference to a new Plotter Window for later use
     */
    static public JPlotterPanel createPlotterPanel(String title){
        JPlotterPanel plotterPanel = new JPlotterPanel();
        JFrame f= DialogUtils.createFrameForPanel(plotterPanel, title);
        plotterPanel.setFrame(f);
        plotterPanel.setTitle(title);
        //plotterPanel.getChartPanel().getChart().getXYPlot().;
        f.setVisible(true);
        return plotterPanel;
    }
    /**
     * Add data source (e.g. File or motion to available sources of the passed in Plotter Panel
     * This allows for reusing the same data source to recreate multiple curves rather than loading it anew
     * 
     * @param plotter
     * @param dataFilename
     * @return 
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
    
    static public PlotterSourceMotion addMotionSource(JPlotterPanel plotter, String motionFilename) {
        FileLoadMotionAction.loadMotion(motionFilename);
        Storage mot = MotionsDB.getInstance().getCurrentMotion(0).motion;
        PlotterSourceMotion src = new PlotterSourceMotion(mot);
        plotter.getPlotterModel().addMotion(mot);
        return src;
    }
    /**
     * Create a new curve on the passed in Plotter panel, using domain and range specified
     * as column labels in the passed in data source
     * 
     * @param panel
     * @param src
     * @param domain
     * @param range
     * @return 
     */
    static public PlotCurve addCurve(JPlotterPanel panel, PlotterSourceFile src, 
            String domain, String range){
          return panel.plotDataFromSource(src, domain, range);
    }
    /**
     * Create a new curve by running the MuscleAnalysis to plot built in quantities 
     * 
     * @param panel
     * @param qName
     * @param muscleName
     * @param genCoordName
     * @return 
     */
    static public PlotCurve addAnalysisCurve(JPlotterPanel panel, String qName, 
                String muscleName, String genCoordName) {
        return panel.showAnalysisCurve(qName, muscleName, genCoordName);
    }
    /**
     * Create a new curve by running the MuscleAnalysis to plot built quantity against passed in motion file
     * 
     * @param panel
     * @param qName
     * @param muscleName
     * @param genCoordName
     * @return 
     */
    //  sourceY=(new PlotterSourceAnalysis(currentModel, plotterModel.getStorage(internalName+coordinateName, currentModel), qName));

    static public PlotCurve addMotionCurve(JPlotterPanel panel, String qName, 
                String muscleName, PlotterSourceMotion dataSource) {
        return panel.showMotionCurve(qName, muscleName, dataSource);
    }
    /**
     * Set the Legend for the passed in curve
     * @param cv
     * @param legend 
     */
    static public void setCurveLegend(PlotCurve cv, String legend) {
        cv.setLegend(legend);
    }
    /**
     * Set the color to use for the passed in curve as RGB
     * @param panel
     * @param series
     * @param r
     * @param g
     * @param b 
     */
    static public void setCurveColor(JPlotterPanel panel, int series, float r, float g, float b){
        panel.getPlotterModel().setColorRGB(series, r, g, b);
    }    
    /**
     * Export all the data on the plotter window to an sto file
     * 
     * @param panel
     * @param fileName 
     */
    static public void exportData(JPlotterPanel panel, String fileName){
        panel.getPlotterModel().getCurrentPlot().exportData(fileName);
    }

}