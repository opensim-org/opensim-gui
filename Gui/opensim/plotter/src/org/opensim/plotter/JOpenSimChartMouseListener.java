/*
 *
 * JOpenSimChartMouseListener
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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.annotations.XYTextAnnotation;

/**
 *
 * @author Ayman
 */
public class JOpenSimChartMouseListener implements MouseListener {
   ChartPanel chartPanel;
   /** Creates a new instance of JOpenSimChartMouseListener */
   public JOpenSimChartMouseListener(ChartPanel chartPanel) {
      this.chartPanel=chartPanel;
      chartPanel.addMouseListener(this);
   }

   public void mouseClicked(MouseEvent e) {
      int x=e.getX();
      int y=e.getY();
      // handleClick only serves to locate the Corosshair Values to convert from 
      // Java2D space to data space.
      int mask=e.getModifiers();
      if ((mask & e.CTRL_MASK) !=0){    // Show query only if CTRL button is held down
          chartPanel.getChart().handleClick(x, y, chartPanel.getChartRenderingInfo());
          double dataX = chartPanel.getChart().getXYPlot().getDomainCrosshairValue();
          double dataY = chartPanel.getChart().getXYPlot().getRangeCrosshairValue();
          //%[argument_index$][flags][width][.precision]conversion
          String annotationText = String.format("(%1$f, %2$f)", dataX, dataY);
          chartPanel.getChart().getXYPlot().addAnnotation(new XYTextAnnotation(annotationText, dataX, dataY));
      }
   }

   public void mousePressed(MouseEvent e) {
   }

   public void mouseReleased(MouseEvent e) {
   }

   public void mouseEntered(MouseEvent e) {
   }

   public void mouseExited(MouseEvent e) {
   }
   
}
