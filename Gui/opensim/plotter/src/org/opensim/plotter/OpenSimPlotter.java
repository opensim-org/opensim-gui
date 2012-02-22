/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.plotter;

import java.io.IOException;
import javax.swing.JFrame;
import org.openide.util.Exceptions;
import org.opensim.utils.DialogUtils;

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
    static public JPlotterPanel createPlottterPanel(String title){
        JPlotterPanel plotterPanel = new JPlotterPanel();
        JFrame f= DialogUtils.createFrameForPanel(plotterPanel, title);
        plotterPanel.setFrame(f);
        f.setVisible(true);
        return plotterPanel;
    }
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
    static public PlotCurve addCurve(JPlotterPanel panel, PlotterSourceFile src, 
            String domain, String range){
          return panel.plotDataFromSource(src, domain, range);
    }
    static public PlotCurve addAnalysisCurve(JPlotterPanel panel, String qName, 
                String muscleName, String genCoordName) {
        return panel.showAnalysisCurve(qName, muscleName, genCoordName);
    }
    static public void setCurveLegend(PlotCurve cv, String legend) {
        cv.setLegend(legend);
    }
    static public void setCurveColor(JPlotterPanel panel, int series, float r, float g, float b){
        panel.getPlotterModel().setColorRGB(series, r, g, b);
    }    
    static public void setStroke(JPlotterPanel panel, float thickness){
        panel.getPlotterModel().setStroke(thickness);
    }
    static public void exportData(JPlotterPanel panel, String fileName){
        panel.getPlotterModel().getCurrentPlot().exportData(fileName);
    }
}