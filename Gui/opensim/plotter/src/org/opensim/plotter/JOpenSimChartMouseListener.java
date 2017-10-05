/* -------------------------------------------------------------------------- *
 * OpenSim: JOpenSimChartMouseListener.java                                   *
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
 *
 * JOpenSimChartMouseListener
 * Author(s): Ayman Habib
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
