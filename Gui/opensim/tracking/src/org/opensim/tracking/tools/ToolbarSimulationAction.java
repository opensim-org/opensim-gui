/* -------------------------------------------------------------------------- *
 * OpenSim: ToolbarSimulationAction.java                                      *
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

package org.opensim.tracking.tools;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.DropDownButtonFactory;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

public final class ToolbarSimulationAction extends CallableSystemAction {
   JPanel tb;
   public void performAction() {
      // TODO implement action body
   }
   
   public String getName() {
      return NbBundle.getMessage(ToolbarSimulationAction.class, "CTL_ShowMotionSliderAction");
   }
   
   protected void initialize() {
      super.initialize();
      // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
      putValue("noIconInMenu", Boolean.TRUE);
   }
   
   public HelpCtx getHelpCtx() {
      return HelpCtx.DEFAULT_HELP;
   }
   
   protected boolean asynchronous() {
      return false;
   }

   public Component getToolbarPresenter() {
      tb = new JPanel();
      tb.setLayout(new FlowLayout());
      tb.setPreferredSize(new Dimension(230, 50));
      tb.setMaximumSize(new Dimension(230, 50));
      //tb.setFloatable(true);
      //tb.setBorder(BorderFactory.createTitledBorder(null, "Simulate", TitledBorder.CENTER, TitledBorder.TOP));
      tb.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
      JPopupMenu popup = new JPopupMenu();
      JMenuItem endTimeMenuitem = new JMenuItem("End time...");
      popup.add(endTimeMenuitem);
      //tb.addSeparator(new Dimension(10,40));
        try {
            JLabel jLabel1 = new JLabel("Simulate");
            jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11));
            tb.add(jLabel1);
            ImageIcon icon = new ImageIcon(getClass().getResource("/org/opensim/tracking/tools/run.png"));
            JButton dropdownButton = DropDownButtonFactory.createDropDownButton(icon, popup);
            dropdownButton.setPreferredSize(new Dimension(60,32));
            tb.add(Box.createHorizontalStrut(2));
            final ToolbarRunForwardAction runFD = (ToolbarRunForwardAction) ToolbarRunForwardAction.findObject((Class)Class.forName("org.opensim.tracking.tools.ToolbarRunForwardAction"), true);
            //JButton fdButton = new JButton(runFD);
            endTimeMenuitem.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent ae) {
                SpecifyFinalTimeJPanel endTimePanel = new SpecifyFinalTimeJPanel(runFD.getFinalTime());
                DialogDescriptor dlg = new DialogDescriptor(endTimePanel,"Specify end time");
                dlg.setModal(true);
                DialogDisplayer.getDefault().createDialog(dlg).setVisible(true);
                Object userInput = dlg.getValue();
                if (((Integer)userInput).compareTo((Integer)DialogDescriptor.OK_OPTION)==0){
                    runFD.setFinalTime( endTimePanel.getEndtime());
                }
            }
        });

      tb.add(dropdownButton);
            dropdownButton.setAction(runFD);
            dropdownButton.setText("Run");
            tb.add(Box.createHorizontalStrut(5));
            ToolbarStopForwardAction stopFD = (ToolbarStopForwardAction) ToolbarStopForwardAction.findObject((Class)Class.forName("org.opensim.tracking.tools.ToolbarStopForwardAction"), true);
            JButton stopBtn = new JButton(stopFD);
            stopBtn.setPreferredSize(new Dimension(60,32));
            tb.add(stopBtn);
            tb.add(Box.createHorizontalStrut(3));
            //tb.add(new JLabel("simulate "+SimulationDB.getSimulationTime()+" sec."));
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
      //tb.addSeparator(new Dimension(10,40));
      return tb;
   }
   public void setToolbarVisiblity(boolean show)
   {
       tb.setVisible(show);
   }
   
}
