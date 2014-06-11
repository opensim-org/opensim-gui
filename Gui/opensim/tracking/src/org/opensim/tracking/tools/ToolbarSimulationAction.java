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
