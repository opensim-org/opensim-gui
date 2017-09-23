/* -------------------------------------------------------------------------- *
 * OpenSim: ExcitationObject.java                                             *
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
 * ExcitationObject.java
 *
 * Created on February 5, 2008, 5:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 *
 */

package org.opensim.view.excitationEditor;

import java.awt.BasicStroke;
import java.awt.Color;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jfree.chart.plot.Plot;

/**
 *
 * @author Ayman. A class made up to represent a node in the tree of excitations 
 * on the left of the Excitation Editor
 */
public class ExcitationObject extends DefaultMutableTreeNode{
    
    ExcitationPanel excitationPanel;
    String name;
    /**
     * Creates a new instance of ExcitationObject
     */
    public ExcitationObject(ExcitationPanel aPlot, String name) {
        this.excitationPanel = aPlot;
        this.name = name;
    }
    
    public String toString()
    {
       String filtered = name;
       if (filtered.contains(".")){
           filtered = filtered.substring(0, name.indexOf('.')); 
       }
       return filtered;
    }
    
    ExcitationPanel getPlotPanel()
    {
        return excitationPanel;
    }
    
    void markSelected(Boolean select)
    {
        Plot plot = excitationPanel.getChart().getPlot();
        if (select){            
            plot.setOutlinePaint(Color.RED);
            plot.setOutlineStroke(new BasicStroke(4.0f));
        }
        else {
            plot.setOutlinePaint(Color.GRAY);
            plot.setOutlineStroke(new BasicStroke(1.0f));
           
        }
    }
}
