/* -------------------------------------------------------------------------- *
 * OpenSim: MotionControlJPanel.java                                          *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Christopher Dembia                                 *
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
 * MotionControlJPanel.java
 *
 * Created on January 12, 2007, 11:28 AM
 */

package org.opensim.view.motions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.opensim.modeling.Model;
import org.opensim.modeling.Storage;
import org.opensim.utils.TheApp;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.motions.MotionEvent.Operation;
import org.opensim.view.ObjectsRenamedEvent;
import org.opensim.view.motions.MotionsDB.ModelMotionPair;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author  Ayman
 * The panel for motion playback in main toolbar.
 * The panel will observer MotionsDB so that it can respond to changes in current model, events ...
 */
public class MotionControlJPanel extends javax.swing.JToolBar 
        implements ChangeListener,  // For motion slider
                   Observer {       // For MotionsDB
   
   Timer               animationTimer=null;
   SpinnerModel        smodel = new SpinnerNumberModel(1.0, 0.0, 10.0, 0.05);
   boolean             motionLoaded=false;
   private MasterMotionModel   masterMotion;
   private int         rangeResolution;
   private int         timerRate = 30;
   private DecimalFormat timeFormat = new DecimalFormat("0.000");

   private boolean internalTrigger=false;
   private static MotionControlJPanel instance=null;
   
   // ActionListener for the timer which is used to play motion forwards/backwards in such a way that
   // advances the model's time based on the real elapsed time (times the factor specified by the smodel spinner)
   private class RealTimePlayActionListener implements ActionListener {
      private boolean firstAction=true;
      private long lastActionTimeNano;
      private int direction;

      private double avgCost = 0;
      private int avgCostCount = 0;

      public RealTimePlayActionListener(int direction) { this.direction = direction; }

      public void actionPerformed(ActionEvent evt) {
         long currentTimeNano = System.nanoTime();
         if(firstAction) {
            firstAction = false;
            lastActionTimeNano = currentTimeNano;
            getMasterMotion().advanceTime(0);
         } else {
            double speed = (double)(((Double)smodel.getValue()).doubleValue());
            double factor = (double)direction*1e-9*speed;
            //System.out.println("Time since last call "+(currentTimeNano-lastActionTimeNano)+" ns");
            getMasterMotion().advanceTime(factor*(currentTimeNano-lastActionTimeNano));
            //System.out.println("             masterMotion current time = "+(masterMotion.getCurrentTime()));
            lastActionTimeNano = currentTimeNano;
         }

         // Kill self if done and wrapMotion is off
         if (getMasterMotion().finished(direction)){
            //animationTimer.stop();
            //animationTimer=null;
            jStopButtonActionPerformed(evt);
            ViewDB.getInstance().endAnimation();
            
         } else {
            /*
            double ms=1e-6*(System.nanoTime()-currentTimeNano);
            avgCost += ms; avgCostCount++;
            int elapsedTimeMS = (int)ms;
            //int nextDelay = (timerRate>elapsedTimeMS) ? timerRate - elapsedTimeMS : 0;
            //int nextDelay = 0;
            System.out.println("Current: "+ms+" Avg: "+avgCost/avgCostCount);
            //animationTimer.setDelay(nextDelay);
            */
         }
      }
   }
   
   /**
    * Creates new form MotionControlJPanel
    */
   public MotionControlJPanel() {
      masterMotion = new MasterMotionModel();   // initialize the object backing the GUI.

      initComponents();
      rangeResolution = jMotionSlider.getMaximum(); // assume resolution was set up as max value of slider in form designer

      getMasterMotion().addChangeListener(this);
      jMotionSlider.addChangeListener(this);             // listen to changes in the slider itself.
      MotionsDB.getInstance().addObserver(this);

      updatePanelDisplay();
      instance = this;
   }
   
   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPlaybackButtonsPanel = new javax.swing.JPanel();
        jRestartButton = new javax.swing.JButton();
        jBackButton = new javax.swing.JButton();
        jReverseButton = new javax.swing.JButton();
        jStopButton = new javax.swing.JButton();
        jPlayButton = new javax.swing.JButton();
        jAdvanceButton = new javax.swing.JButton();
        jFinishButton = new javax.swing.JButton();
        jMotionSlider = new javax.swing.JSlider();
        jTimeTextField = new javax.swing.JTextField();
        jSpeedSpinner = new javax.swing.JSpinner();
        jLabelForSpeedSpinner = new javax.swing.JLabel();
        jLabelForTimeTextField = new javax.swing.JLabel();
        jWrapToggleButton = new javax.swing.JToggleButton();
        jStartTimeTextField = new javax.swing.JTextField();
        jEndTimeTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jMotionNameLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        setFloatable(false);
        setForeground(new java.awt.Color(255, 255, 255));
        setMaximumSize(new java.awt.Dimension(32767, 50));
        setMinimumSize(new java.awt.Dimension(0, 50));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(700, 50));

        jPlaybackButtonsPanel.setToolTipText("Motion Controls");
        jPlaybackButtonsPanel.setMaximumSize(new java.awt.Dimension(32767, 16));
        jPlaybackButtonsPanel.setMinimumSize(new java.awt.Dimension(168, 16));
        jPlaybackButtonsPanel.setPreferredSize(new java.awt.Dimension(168, 16));
        jPlaybackButtonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));

        jRestartButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/rewindToStart_beveled.png"))); // NOI18N
        jRestartButton.setToolTipText("Go to start");
        jRestartButton.setBorder(null);
        jRestartButton.setBorderPainted(false);
        jRestartButton.setContentAreaFilled(false);
        jRestartButton.setFocusPainted(false);
        jRestartButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/rewindToStart_beveled_selected.png"))); // NOI18N
        jRestartButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/rewindToStart_beveled_rollover.png"))); // NOI18N
        jRestartButton.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/rewindToStart_beveled_rollover_selected.png"))); // NOI18N
        jRestartButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/rewindToStart_beveled_selected.png"))); // NOI18N
        jRestartButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRestartButtonActionPerformed(evt);
            }
        });
        jPlaybackButtonsPanel.add(jRestartButton);

        jBackButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/reverseStep_beveled.png"))); // NOI18N
        jBackButton.setToolTipText("Previous frame");
        jBackButton.setBorder(null);
        jBackButton.setBorderPainted(false);
        jBackButton.setContentAreaFilled(false);
        jBackButton.setFocusPainted(false);
        jBackButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/reverseStep_beveled.png"))); // NOI18N
        jBackButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/reverseStep_beveled_rollover.png"))); // NOI18N
        jBackButton.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/reverseStep_beveled_rollover_selected.png"))); // NOI18N
        jBackButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/reverseStep_beveled_selected.png"))); // NOI18N
        jBackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBackButtonActionPerformed(evt);
            }
        });
        jPlaybackButtonsPanel.add(jBackButton);

        jReverseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/reverse_beveled.png"))); // NOI18N
        jReverseButton.setToolTipText("Play backward");
        jReverseButton.setBorder(null);
        jReverseButton.setBorderPainted(false);
        jReverseButton.setContentAreaFilled(false);
        jReverseButton.setFocusPainted(false);
        jReverseButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/reverse_beveled_selected.png"))); // NOI18N
        jReverseButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/reverse_beveled_rollover.png"))); // NOI18N
        jReverseButton.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/reverse_beveled_rollover_selected.png"))); // NOI18N
        jReverseButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/reverse_beveled_selected.png"))); // NOI18N
        jReverseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPlayReverseButtonActionPerformed(evt);
            }
        });
        jPlaybackButtonsPanel.add(jReverseButton);

        jStopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/pause_beveled.png"))); // NOI18N
        jStopButton.setToolTipText("Pause");
        jStopButton.setBorder(null);
        jStopButton.setBorderPainted(false);
        jStopButton.setContentAreaFilled(false);
        jStopButton.setFocusPainted(false);
        jStopButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/pause_beveled_selected.png"))); // NOI18N
        jStopButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/pause_beveled_rollover.png"))); // NOI18N
        jStopButton.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/pause_beveled_rollover_selected.png"))); // NOI18N
        jStopButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/pause_beveled_selected.png"))); // NOI18N
        jStopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jStopButtonActionPerformed(evt);
            }
        });
        jPlaybackButtonsPanel.add(jStopButton);

        jPlayButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/play_beveled.png"))); // NOI18N
        jPlayButton.setToolTipText("Play forward");
        jPlayButton.setBorder(null);
        jPlayButton.setBorderPainted(false);
        jPlayButton.setContentAreaFilled(false);
        jPlayButton.setFocusPainted(false);
        jPlayButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/play_beveled_selected.png"))); // NOI18N
        jPlayButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/play_beveled_rollover.png"))); // NOI18N
        jPlayButton.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/play_beveled_rollover_selected.png"))); // NOI18N
        jPlayButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/play_beveled_selected.png"))); // NOI18N
        jPlayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPlayButtonActionPerformed(evt);
            }
        });
        jPlaybackButtonsPanel.add(jPlayButton);

        jAdvanceButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/step_beveled.png"))); // NOI18N
        jAdvanceButton.setToolTipText("Next frame");
        jAdvanceButton.setBorder(null);
        jAdvanceButton.setBorderPainted(false);
        jAdvanceButton.setContentAreaFilled(false);
        jAdvanceButton.setFocusPainted(false);
        jAdvanceButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/step_beveled_selected.png"))); // NOI18N
        jAdvanceButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/step_beveled_rollover.png"))); // NOI18N
        jAdvanceButton.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/step_beveled_rollover_selected.png"))); // NOI18N
        jAdvanceButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/step_beveled_selected.png"))); // NOI18N
        jAdvanceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAdvanceButtonActionPerformed(evt);
            }
        });
        jPlaybackButtonsPanel.add(jAdvanceButton);

        jFinishButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/fastforwardToEnd_beveled.png"))); // NOI18N
        jFinishButton.setToolTipText("Go to end");
        jFinishButton.setBorder(null);
        jFinishButton.setBorderPainted(false);
        jFinishButton.setContentAreaFilled(false);
        jFinishButton.setFocusPainted(false);
        jFinishButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/fastforwardToEnd_beveled_selected.png"))); // NOI18N
        jFinishButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/fastforwardToEnd_beveled_rollover.png"))); // NOI18N
        jFinishButton.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/fastforwardToEnd_beveled_rollover_selected.png"))); // NOI18N
        jFinishButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/fastforwardToEnd_beveled_selected.png"))); // NOI18N
        jFinishButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFinishButtonActionPerformed(evt);
            }
        });
        jPlaybackButtonsPanel.add(jFinishButton);

        jMotionSlider.setMajorTickSpacing(1000);
        jMotionSlider.setMaximum(10000);
        jMotionSlider.setMinorTickSpacing(200);
        jMotionSlider.setToolTipText("Seek");
        jMotionSlider.setValue(0);
        jMotionSlider.setFocusable(false);
        jMotionSlider.setMaximumSize(new java.awt.Dimension(32767, 22));
        jMotionSlider.setMinimumSize(new java.awt.Dimension(168, 22));
        jMotionSlider.setPreferredSize(new java.awt.Dimension(400, 22));

        jTimeTextField.setToolTipText("Current time");
        jTimeTextField.setMaximumSize(new java.awt.Dimension(80, 19));
        jTimeTextField.setMinimumSize(new java.awt.Dimension(50, 19));
        jTimeTextField.setPreferredSize(new java.awt.Dimension(75, 19));
        jTimeTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTimeTextFieldFocusLost(evt);
            }
        });
        jTimeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTimeTextFieldActionPerformed(evt);
            }
        });
        jTimeTextField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTimeTextFieldPropertyChange(evt);
            }
        });

        jSpeedSpinner.setModel(smodel);
        jSpeedSpinner.setToolTipText("Play speed");
        jSpeedSpinner.setMaximumSize(new java.awt.Dimension(50, 19));
        jSpeedSpinner.setMinimumSize(new java.awt.Dimension(50, 19));
        jSpeedSpinner.setPreferredSize(jTimeTextField.getPreferredSize());

        jLabelForSpeedSpinner.setLabelFor(jSpeedSpinner);
        jLabelForSpeedSpinner.setText("Speed");
        jLabelForSpeedSpinner.setMaximumSize(new java.awt.Dimension(50, 19));
        jLabelForSpeedSpinner.setMinimumSize(new java.awt.Dimension(45, 19));
        jLabelForSpeedSpinner.setPreferredSize(new java.awt.Dimension(40, 19));

        jLabelForTimeTextField.setLabelFor(jTimeTextField);
        jLabelForTimeTextField.setText("Time");
        jLabelForTimeTextField.setMinimumSize(new java.awt.Dimension(31, 19));
        jLabelForTimeTextField.setPreferredSize(new java.awt.Dimension(40, 19));

        jWrapToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/wrap_beveled.png"))); // NOI18N
        jWrapToggleButton.setToolTipText("Loop motion");
        jWrapToggleButton.setBorder(null);
        jWrapToggleButton.setBorderPainted(false);
        jWrapToggleButton.setContentAreaFilled(false);
        jWrapToggleButton.setFocusPainted(false);
        jWrapToggleButton.setFocusable(false);
        jWrapToggleButton.setIconTextGap(0);
        jWrapToggleButton.setMaximumSize(new java.awt.Dimension(24, 42));
        jWrapToggleButton.setMinimumSize(new java.awt.Dimension(24, 42));
        jWrapToggleButton.setPreferredSize(new java.awt.Dimension(24, 42));
        jWrapToggleButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/wrap_beveled_selected.png"))); // NOI18N
        jWrapToggleButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/wrap_beveled_rollover.png"))); // NOI18N
        jWrapToggleButton.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/wrap_beveled_rollover_selected.png"))); // NOI18N
        jWrapToggleButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/motions/images/wrap_beveled_selected.png"))); // NOI18N
        jWrapToggleButton.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jWrapToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jWrapToggleButtonActionPerformed(evt);
            }
        });

        jStartTimeTextField.setEditable(false);
        jStartTimeTextField.setBackground(new java.awt.Color(224, 223, 227));
        jStartTimeTextField.setText("0");
        jStartTimeTextField.setToolTipText("Initial time");
        jStartTimeTextField.setAutoscrolls(false);
        jStartTimeTextField.setBorder(null);
        jStartTimeTextField.setFocusable(false);
        jStartTimeTextField.setMaximumSize(new java.awt.Dimension(80, 16));
        jStartTimeTextField.setMinimumSize(new java.awt.Dimension(50, 16));
        jStartTimeTextField.setPreferredSize(new java.awt.Dimension(80, 16));

        jEndTimeTextField.setEditable(false);
        jEndTimeTextField.setBackground(new java.awt.Color(224, 223, 227));
        jEndTimeTextField.setText("100");
        jEndTimeTextField.setToolTipText("Final time");
        jEndTimeTextField.setAutoscrolls(false);
        jEndTimeTextField.setBorder(null);
        jEndTimeTextField.setFocusable(false);
        jEndTimeTextField.setMaximumSize(new java.awt.Dimension(80, 16));
        jEndTimeTextField.setMinimumSize(new java.awt.Dimension(50, 16));
        jEndTimeTextField.setPreferredSize(new java.awt.Dimension(80, 16));
        jEndTimeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jEndTimeTextFieldActionPerformed(evt);
            }
        });

        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel1.setText("   Motion: ");

        jMotionNameLabel.setText("No Motions             ");
        jMotionNameLabel.setToolTipText(jMotionNameLabel.getText());
        jMotionNameLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jMotionNameLabel.setMaximumSize(new java.awt.Dimension(130, 18));
        jMotionNameLabel.setMinimumSize(new java.awt.Dimension(130, 18));
        jMotionNameLabel.setPreferredSize(new java.awt.Dimension(130, 18));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jMotionNameLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabelForSpeedSpinner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabelForTimeTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jSpeedSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jStartTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPlaybackButtonsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jEndTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jMotionSlider, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jWrapToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jWrapToggleButton, 0, 0, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(jTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jLabelForTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jMotionSlider, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(0, 0, 0)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jStartTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jEndTimeTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel1)
                            .add(jMotionNameLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(layout.createSequentialGroup()
                        .add(22, 22, 22)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jSpeedSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabelForSpeedSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(layout.createSequentialGroup()
                        .add(22, 22, 22)
                        .add(jPlaybackButtonsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jFinishButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFinishButtonActionPerformed
// TODO add your handling code here:
      if (isMotionLoaded()) { 
          getMasterMotion().setTime(getMasterMotion().getEndTime());
          // correct selected modes
          if (jPlayButton.isSelected() && !jWrapToggleButton.isSelected()) {
              jStopButton.setSelected(true);
              jPlayButton.setSelected(false);
          }
      }
      else { jMotionSlider.setValue(0); }
    }//GEN-LAST:event_jFinishButtonActionPerformed

   private void jRestartButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRestartButtonActionPerformed
// TODO add your handling code here:
      if (isMotionLoaded()) { 
          getMasterMotion().setTime(getMasterMotion().getStartTime());
          // correct selected modes
          if (jReverseButton.isSelected() && !jWrapToggleButton.isSelected()) {
              jStopButton.setSelected(true);
              jReverseButton.setSelected(false);
          }
      }
      else { jMotionSlider.setValue(0); }
   }//GEN-LAST:event_jRestartButtonActionPerformed

   private void jTimeTextFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTimeTextFieldPropertyChange
   // Listen to "value" changes
      handleUserTimeChange();
   }//GEN-LAST:event_jTimeTextFieldPropertyChange
   
   private void jTimeTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTimeTextFieldFocusLost
      if (!evt.isTemporary())
         handleUserTimeChange();
   }//GEN-LAST:event_jTimeTextFieldFocusLost
   
   private void jTimeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTimeTextFieldActionPerformed
      handleUserTimeChange();
   }//GEN-LAST:event_jTimeTextFieldActionPerformed
   
   private void jBackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBackButtonActionPerformed
      if (isMotionLoaded()){
          getMasterMotion().back();
          // correct selected modes
          jStopButtonActionPerformed(evt);
      }
   }//GEN-LAST:event_jBackButtonActionPerformed
   
   private void jPlayReverseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPlayReverseButtonActionPerformed
      if (animationTimer!=null){
         animationTimer.stop();
         animationTimer=null;
      }
      if (isMotionLoaded() && animationTimer==null){
          if (getMasterMotion().finished(-1)){
              // reset motion if at end already
              getMasterMotion().setTime(getMasterMotion().getEndTime());
          }
          int timerRate = ViewDB.getInstance().getFrameTime();
          animationTimer = new Timer(timerRate, new RealTimePlayActionListener(-1));
          animationTimer.start();
          // correct selected modes
          deselectPlaybackButtons();
          jReverseButton.setSelected(true);
      }
   }//GEN-LAST:event_jPlayReverseButtonActionPerformed
   
    private void jStopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jStopButtonActionPerformed
       if (isMotionLoaded() && animationTimer != null){
          animationTimer.stop();
          animationTimer = null;
          // correct selected modes
          deselectPlaybackButtons();
          jStopButton.setSelected(true);
       }
    }//GEN-LAST:event_jStopButtonActionPerformed
    
    private void jWrapToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jWrapToggleButtonActionPerformed
       
       getMasterMotion().setWrapMotion(!getMasterMotion().isWrapMotion());
    }//GEN-LAST:event_jWrapToggleButtonActionPerformed
    
    private void jAdvanceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAdvanceButtonActionPerformed
       if (isMotionLoaded()){
          getMasterMotion().advance();
          // correct selected modes
          jStopButtonActionPerformed(evt);
       }
    }//GEN-LAST:event_jAdvanceButtonActionPerformed
    
    private void jPlayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPlayButtonActionPerformed
      if (animationTimer!=null){
         animationTimer.stop();
         animationTimer=null;
      }
      if (isMotionLoaded() && animationTimer==null){
          if (getMasterMotion().finished(1)){
              // reset motion if at end already
              getMasterMotion().setTime(getMasterMotion().getStartTime());
          }
          ViewDB.getInstance().startAnimation();
          int timerRate = ViewDB.getInstance().getFrameTime();

          animationTimer = new Timer(timerRate, new RealTimePlayActionListener(1));
          animationTimer.start();
          // correct selected modes
          deselectPlaybackButtons();
          jPlayButton.setSelected(true);
      }
    }//GEN-LAST:event_jPlayButtonActionPerformed

    private void jEndTimeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEndTimeTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jEndTimeTextFieldActionPerformed
    
    
    public void stateChanged(ChangeEvent e) 
    {
       if (isMotionLoaded()){
          if (e.getSource().equals(getMasterMotion()) && !internalTrigger){
             internalTrigger=true;
             jMotionSlider.setValue(getSliderValueForTime(getMasterMotion().getCurrentTime()));
             internalTrigger=false;
             setTimeTextField(getMasterMotion().getCurrentTime());
          } else if (e.getSource().equals(jMotionSlider) && !internalTrigger){
             internalTrigger=true;
             getMasterMotion().setTime(getTimeForSliderValue(jMotionSlider.getValue()));
             internalTrigger=false;
             setTimeTextField(getMasterMotion().getCurrentTime());
          }
       }
    }
    
    public boolean isMotionLoaded() {
       return motionLoaded;
    }
    
    public void handleUserTimeChange() {
       if (isMotionLoaded()){
          //get frame corresponding to enetered time
          try {
             getMasterMotion().setTime(timeFormat.parse(jTimeTextField.getText()).doubleValue());
          } catch (ParseException e){
             Toolkit.getDefaultToolkit().beep();
             jTimeTextField.setText(timeFormat.format(getMasterMotion().getCurrentTime()));
          }
       }
    }
    
    public void setUserTime(double time) {
         MotionControlJPanel.getInstance().setTimeTextField(time);
         MotionControlJPanel.getInstance().handleUserTimeChange();
    }
    
   Hashtable makeLabels(int n, double startTime, double endTime) {
      Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>(5);
      for(int i=0; i<n; i++){
         double alpha=(double)i/(n-1);
         String label = Double.toString(startTime+alpha*(endTime-startTime));
         if (label.length()>5) label=label.substring(0, 5);
         labels.put((int)(alpha*rangeResolution), new JLabel(label));
      }
      return labels;
   }

   /**
    * update the panel to reflect current motion's name, range, ...etc.
    */
   private void updatePanelDisplay() {
      if (getMasterMotion()==null || getMasterMotion().getNumMotions()==0){
         setTimeTextField(0);
         jStartTimeTextField.setText("");
         jEndTimeTextField.setText("");
         Hashtable labels = makeLabels(5, 0, 1);
         jMotionSlider.setLabelTable(labels);
         // disable components
         disableComponents();
         jMotionNameLabel.setText("No Motions             ");
      }
      else {
         if (getMasterMotion().getNumMotions()>=1){
            //this.setBorder(BorderFactory.createTitledBorder(null, getMasterMotion().getDisplayName(), TitledBorder.CENTER, TitledBorder.TOP));
            jMotionNameLabel.setText(getMasterMotion().getDisplayName());
            jMotionNameLabel.setToolTipText(getMasterMotion().getDisplayName());
            setTimeTextField(getMasterMotion().getCurrentTime());
            setStartTimeTextField(getMasterMotion().getStartTime());
            setEndTimeTextField(getMasterMotion().getEndTime());
            Hashtable labels = makeLabels(5, getMasterMotion().getStartTime(), getMasterMotion().getEndTime());
            jMotionSlider.setLabelTable(labels);
            // enable components
            enableComponents();
            // correct selected mode
            if (!jPlayButton.isSelected() && !jReverseButton.isSelected()){
                jStopButton.setSelected(true);
            }
         }
      }
   }
   
   // Observable is MotionsDB
   public void update(Observable o, Object arg) {
      // Recover motion info, update toolbar accordingly
      if (o instanceof MotionsDB){
         MotionsDB mdb = (MotionsDB)o;
         if (arg instanceof ObjectsRenamedEvent){
            updatePanelDisplay();
            return;
         }
         if (!(arg instanceof MotionEvent)) // time should be ignored here
             return;
         MotionEvent evt = (MotionEvent)arg;
         if (evt.getOperation() == Operation.CurrentMotionsChanged ||
                 evt.getOperation() == Operation.Close){
            // Current motion changed.  Update master motion
            double currentTime = getMasterMotion().getCurrentTime();
            getMasterMotion().clear();
            for(int i=0; i<mdb.getNumCurrentMotions(); i++){
               getMasterMotion().add(mdb.getCurrentMotion(i));
               ModelMotionPair currentModelMotionPair = mdb.getCurrentMotion(i);
               Storage mot = currentModelMotionPair.motion;
               MotionsDB.getInstance().getDisplayerForMotion(mot).setupMotionDisplay();
               //
               ArrayList<MotionDisplayer> associatedDisplayers = MotionsDB.getInstance().getDisplayerForMotion(mot).getAssociatedMotions();
               // Find associated motions as well and re-associate them
               for (int j=0; j<associatedDisplayers.size(); j++)
                   associatedDisplayers.get(j).setupMotionDisplay();
            }
            getMasterMotion().setTime(currentTime);
            
         } else if (evt.getOperation() == Operation.Modified) {
            Storage motion = evt.getMotion();
            Model model = evt.getModel();
            for (int i=0; i<getMasterMotion().getNumMotions(); i++) {
               Storage mot = getMasterMotion().getDisplayer(i).getSimmMotionData();
               Model mod = getMasterMotion().getDisplayer(i).getModel();
               if (mod.equals(model) && mot.equals(motion)) {
                  getMasterMotion().getDisplayer(i).setupMotionDisplay();
               }
            }
            getMasterMotion().setTime(getMasterMotion().getCurrentTime());

         } else if (evt.getOperation() == Operation.Assoc) {
             // Create MotionDisplayer and associate it to that of currentMotion
             MotionDisplayer newMotionDisplayer = new MotionDisplayer(evt.getMotion(), evt.getModel());
             MotionsDB.getInstance().addMotionDisplayer(evt.getMotion(), newMotionDisplayer);
             getMasterMotion().getDisplayer(0).getAssociatedMotions().add(newMotionDisplayer);
             newMotionDisplayer.setupMotionDisplay();
             getMasterMotion().setTime(getMasterMotion().getCurrentTime());

         }
         motionLoaded = (getMasterMotion().getNumMotions() > 0);
         updatePanelDisplay();
      }
   }
   
   public void setTimeTextField(double time)
   {
      jTimeTextField.setText(timeFormat.format(time));
   }
   
   private void setStartTimeTextField(double time)
   {
      jStartTimeTextField.setText(timeFormat.format(time));
   }

   private void setEndTimeTextField(double time)
   {
      jEndTimeTextField.setText(timeFormat.format(time));
   }
      
   private int getSliderValueForTime(double time)
   {
      if(getMasterMotion()==null || getMasterMotion().getStartTime() == getMasterMotion().getEndTime()) 
         return 0;
      else {
         double alpha = (time-getMasterMotion().getStartTime())/(getMasterMotion().getEndTime()-getMasterMotion().getStartTime());
         return (int)(alpha*rangeResolution);
      }
   }

   private double getTimeForSliderValue(int sliderValue)
   {
      if(getMasterMotion()==null) return 0;
      double alpha = (double)sliderValue/rangeResolution;
      return getMasterMotion().getStartTime() + alpha * (getMasterMotion().getEndTime()-getMasterMotion().getStartTime());
   }

   public void enableComponents() 
   {
        jAdvanceButton.setEnabled(true);
        jBackButton.setEnabled(true);
        jFinishButton.setEnabled(true);
        jLabelForSpeedSpinner.setEnabled(true);
        jLabelForTimeTextField.setEnabled(true);
        jMotionSlider.setEnabled(true);
        jPlayButton.setEnabled(true);
        jPlaybackButtonsPanel.setEnabled(true);
        jRestartButton.setEnabled(true);
        jReverseButton.setEnabled(true);
        jSpeedSpinner.setEnabled(true);
        jStopButton.setEnabled(true);
        jTimeTextField.setEnabled(true);
        jWrapToggleButton.setEnabled(true);
   }
   
   public void disableComponents() 
   {
        jAdvanceButton.setEnabled(false);
        jBackButton.setEnabled(false);
        jFinishButton.setEnabled(false);
        jLabelForSpeedSpinner.setEnabled(false);
        jLabelForTimeTextField.setEnabled(false);
        jMotionSlider.setEnabled(false);
        jPlayButton.setEnabled(false);
        jPlaybackButtonsPanel.setEnabled(false);
        jRestartButton.setEnabled(false);
        jReverseButton.setEnabled(false);
        jSpeedSpinner.setEnabled(false);
        jStopButton.setEnabled(false);
        jTimeTextField.setEnabled(false);
        jWrapToggleButton.setEnabled(false);
   }
   
   public void deselectPlaybackButtons() 
   {
       jAdvanceButton.setSelected(false);
       jBackButton.setSelected(false);
       jFinishButton.setSelected(false);
       jPlayButton.setSelected(false);
       jRestartButton.setSelected(false);
       jReverseButton.setSelected(false);
       jStopButton.setSelected(false);
   }

    public static MotionControlJPanel getInstance() {
        if (instance==null)
            instance = new MotionControlJPanel();
        return instance;
    }

    public MasterMotionModel getMasterMotion() {
        return masterMotion;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jAdvanceButton;
    private javax.swing.JButton jBackButton;
    private javax.swing.JTextField jEndTimeTextField;
    private javax.swing.JButton jFinishButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelForSpeedSpinner;
    private javax.swing.JLabel jLabelForTimeTextField;
    private javax.swing.JLabel jMotionNameLabel;
    private javax.swing.JSlider jMotionSlider;
    private javax.swing.JButton jPlayButton;
    private javax.swing.JPanel jPlaybackButtonsPanel;
    private javax.swing.JButton jRestartButton;
    private javax.swing.JButton jReverseButton;
    private javax.swing.JSpinner jSpeedSpinner;
    private javax.swing.JTextField jStartTimeTextField;
    private javax.swing.JButton jStopButton;
    private javax.swing.JTextField jTimeTextField;
    private javax.swing.JToggleButton jWrapToggleButton;
    // End of variables declaration//GEN-END:variables

}
