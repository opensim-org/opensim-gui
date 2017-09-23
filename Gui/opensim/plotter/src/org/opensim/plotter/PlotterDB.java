/* -------------------------------------------------------------------------- *
 * OpenSim: PlotterDB.java                                                    *
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
        f.pack();
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
