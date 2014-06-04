/*
 * Copyright (c)  2005-2008, Stanford University
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
/*
 * PlotterDB.java
 *
 * Created on June 15, 2006, 2:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.plotter;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JFrame;
import org.opensim.utils.DialogUtils;
import org.opensim.view.motions.MotionsDB;
import org.opensim.view.pub.OpenSimDB;


/**
 *
 * @author Ayman
 * 
 * Registry of opened Plotter windows so scripting users can access these windows
 * from Script to add or modify curves.
 */
public class PlotterDB {
    
    static PlotterDB instance;
    
    static ArrayList<JPlotterPanel>  plotters = new ArrayList<JPlotterPanel>();
    
    /** Creates a new instance of OpenSimDB */
    private PlotterDB() {
        instance = this;
    }
    
    public static synchronized PlotterDB getInstance() {
        if (instance == null) {
             instance = new PlotterDB();
             
        }
        return instance;
    }

    public void registerPlotterPanel(JPlotterPanel newPanel){
        plotters.add(newPanel);
    }
    
    public void unRegisterPlotterPanel(JPlotterPanel newPanel){
        plotters.remove(newPanel);
    }
    
    public JPlotterPanel getLastPlotterPanel() {
        if (plotters.size()>0) return plotters.get(plotters.size()-1);
        return null;
    }
    public JPlotterPanel createPlotterWindow() {
        final JPlotterPanel plotterPanel = new JPlotterPanel();
        
        JFrame f= DialogUtils.createFrameForPanel(plotterPanel, "Plotter");
        plotterPanel.setFrame(f);
        f.setVisible(true);
        //registerPlotterPanel(plotterPanel);
        
        f.addWindowListener(new WindowAdapter(){
            @Override
           public void windowOpened(WindowEvent e) {
              MotionsDB.getInstance().addObserver(plotterPanel);
              OpenSimDB.getInstance().addObserver(plotterPanel);   // Make sure current model does not change under us
           }

            @Override
           public void windowClosing(WindowEvent e) {
              MotionsDB.getInstance().deleteObserver(plotterPanel);
              OpenSimDB.getInstance().deleteObserver(plotterPanel); 
              unRegisterPlotterPanel(plotterPanel);
              plotterPanel.cleanup();
           }

            @Override
           public void windowClosed(WindowEvent e) {
              MotionsDB.getInstance().deleteObserver(plotterPanel);
              OpenSimDB.getInstance().deleteObserver(plotterPanel); 
              unRegisterPlotterPanel(plotterPanel);
              plotterPanel.cleanup();
          }

      });
      return  plotterPanel;
    }
 }
