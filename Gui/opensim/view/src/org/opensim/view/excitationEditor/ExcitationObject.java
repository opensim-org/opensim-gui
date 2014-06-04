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
