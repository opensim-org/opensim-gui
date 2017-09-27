/* -------------------------------------------------------------------------- *
 * OpenSim: MeasurementSetPanel.java                                          *
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

package org.opensim.tracking;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import org.opensim.modeling.MarkerPair;
import org.opensim.modeling.MarkerSet;
import org.opensim.modeling.Measurement;
import org.opensim.modeling.MeasurementSet;

/////////////////////////////////////////////////////////////////////////////
// MeasurementSetScrollPane
/////////////////////////////////////////////////////////////////////////////

class MeasurementSetScrollPane extends JScrollPane implements Observer, ActionListener {
   private static final int HEIGHT = 16;
   private static final int HEIGHT_INSET = 2;
   private static final int BUTTON_WIDTH = 15;
   private static final int MEASUREMENT_NAME_WIDTH = 120;
   private static final int MARKER_NAME_WIDTH = 90;
   private static final String DEFAULT_MEASUREMENT_NAME = "Unnamed";
   private static final Dimension buttonDim = new Dimension(BUTTON_WIDTH, HEIGHT);
   private static final Color invalidColor = new Color(255,102,102);
   private final Icon addIcon = new ImageIcon(getClass().getResource("/org/opensim/swingui/add.png"));
   private final Icon addRolloverIcon = new ImageIcon(getClass().getResource("/org/opensim/swingui/add_rollover.png"));
   private final Icon addSelectedIcon = new ImageIcon(getClass().getResource("/org/opensim/swingui/add_selected.png"));
   private final Icon removeIcon = new ImageIcon(getClass().getResource("/org/opensim/swingui/delete.png"));
   private final Icon removeRolloverIcon = new ImageIcon(getClass().getResource("/org/opensim/swingui/delete_rollover.png"));
   private final Icon removeSelectedIcon = new ImageIcon(getClass().getResource("/org/opensim/swingui/delete_selected.png"));

   private static final Border measurementControlsBorder = BorderFactory.createLineBorder(Color.black); //BorderFactory.createBevelBorder(BevelBorder.LOWERED);
   private static final Border measurementControlsInnerBorder = BorderFactory.createMatteBorder(0,0,0,1,Color.lightGray);
   private static final Border markerPairControlsBorder = BorderFactory.createLineBorder(Color.blue); //BorderFactory.createBevelBorder(BevelBorder.RAISED);
   private static final Border markerInnerBorder = BorderFactory.createMatteBorder(0,0,0,1,Color.lightGray);

   private JPanel markerPairsPanel;
   private JPanel rowHeader;

   private ScaleToolModel measurementSetModel;
   private Vector<String> markerNames = new Vector<String>(10);

   //------------------------------------------------------------------------
   // Actions
   //------------------------------------------------------------------------
   class RemoveMeasurementAction extends AbstractAction {
      int index;
      public RemoveMeasurementAction(int index) { 
         super("", removeIcon);
         this.index = index; 
         putValue(Action.SHORT_DESCRIPTION, "Remove this measurement");
      }
      public void actionPerformed(ActionEvent evt) { measurementSetModel.removeMeasurement(index); }
   }

   class AddMarkerPairAction extends AbstractAction {
      int measurementIndex;
      public AddMarkerPairAction(int measurementIndex) { 
         super("", addIcon); 
         this.measurementIndex = measurementIndex; 
         putValue(Action.SHORT_DESCRIPTION, "Add a new marker pair to this measurement");
      }
      public void actionPerformed(ActionEvent evt) { measurementSetModel.addMarkerPair(measurementIndex); }
   }

   class RemoveMarkerPairAction extends AbstractAction {
      int measurementIndex;
      int markerPairIndex;
      public RemoveMarkerPairAction(int measurementIndex, int markerPairIndex) { 
         super("", removeIcon); 
         this.measurementIndex = measurementIndex;
         this.markerPairIndex = markerPairIndex;
         putValue(Action.SHORT_DESCRIPTION, "Remove this marker pair from this measurement");
      }
      public void actionPerformed(ActionEvent evt) { measurementSetModel.removeMarkerPair(measurementIndex, markerPairIndex); }
   }

   class AddMeasurementAction extends AbstractAction {
      public AddMeasurementAction() { 
         super("", addIcon); 
         putValue(Action.SHORT_DESCRIPTION, "Add a new measurement");
      }
      public void actionPerformed(ActionEvent evt) { 
         measurementSetModel.addMeasurement(DEFAULT_MEASUREMENT_NAME); 

         // See comments in EditMeasurementNameActionAndFocusListener below...
         int lastIndex = measurementSetModel.getMeasurementSet().getSize()-1;
         JTextField textField = getMeasurementNameTextField(lastIndex);
         textField.requestFocus();
         textField.selectAll();
      }
   }

   class EditMeasurementNameActionAndFocusListener extends AbstractAction implements FocusListener {
      int index; // -1 represents the text field for the add new measurement section of the GUI
      public EditMeasurementNameActionAndFocusListener(int index) { 
         this.index = index;
      }
      public void actionPerformed(ActionEvent evt) {
         // User pressed ENTER, commit changed name
         if(index >= 0) measurementSetModel.setMeasurementName(index, ((JTextField)evt.getSource()).getText());
      }
      public void focusGained(FocusEvent evt) {
         if(index < 0) {
            // User clicked in the name field for the add measurement button
            // In response, we add a new measurement and select the name field for the new measurement
            // NOTE: After a new measurement is added, we use getMeasurementNameTextField() to get the JTextField for the newly added measurement's name field... it's
            // different from evt.getSource()
            measurementSetModel.addMeasurement(DEFAULT_MEASUREMENT_NAME);
            int lastIndex = measurementSetModel.getMeasurementSet().getSize()-1;
            JTextField textField = getMeasurementNameTextField(lastIndex);
            textField.requestFocus();
            textField.selectAll();
         } else {
            JTextField textField = (JTextField)evt.getSource();
            textField.selectAll();
         }
      }
      public void focusLost(FocusEvent evt) {
         // Commit changed name when focus is lost
         if(index >= 0) measurementSetModel.setMeasurementName(index, ((JTextField)evt.getSource()).getText());
      }
   }

   class ChangeMarkerPairMarkerAction extends AbstractAction {
      int measurementIndex;
      int markerPairIndex;
      int whichMarker; // 0 or 1
      public ChangeMarkerPairMarkerAction(String newMarkerName, int measurementIndex, int markerPairIndex, int whichMarker) {
         super(newMarkerName);
         this.measurementIndex = measurementIndex;
         this.markerPairIndex = markerPairIndex;
         this.whichMarker = whichMarker;
      }
      public void actionPerformed(ActionEvent evt) {
         measurementSetModel.setMarkerPairMarker(measurementIndex, markerPairIndex, whichMarker, (String)getValue(Action.NAME));
      }
   }

   public MeasurementSetScrollPane(ScaleToolModel measurementSetModel) {
      this.measurementSetModel = measurementSetModel;

      markerPairsPanel = new JPanel();
      markerPairsPanel.setLayout(new BoxLayout(markerPairsPanel, BoxLayout.Y_AXIS));
      markerPairsPanel.setAlignmentX(0);
      setViewportView(markerPairsPanel);

      rowHeader = new JPanel();
      rowHeader.setLayout(new BoxLayout(rowHeader, BoxLayout.Y_AXIS));
      setRowHeaderView(rowHeader);

      updateMarkerNames();
      reset();

      measurementSetModel.addObserver(this);
   }

   private void updateMarkerNames() {
      MarkerSet markerSet = measurementSetModel.getMarkerSet();
      if(markerSet!=null) {
         markerNames.setSize(markerSet.getSize());
         for(int i=0; i<markerSet.getSize(); i++) markerNames.set(i, markerSet.get(i).getName());
      } else {
         markerNames.setSize(0);
      }
   }

   //------------------------------------------------------------------------
   // Titles
   //------------------------------------------------------------------------
   public static JPanel getTitleLabel() {
      JPanel titlePanel = new JPanel();
      titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));

      titlePanel.add(Box.createRigidArea(new Dimension(1,HEIGHT)));

      JLabel measurementsLabel = new JLabel("Measurements");
      measurementsLabel.setHorizontalAlignment(SwingConstants.CENTER);
      measurementsLabel.setBorder(measurementControlsBorder);
      Dimension dim = new Dimension(BUTTON_WIDTH+MEASUREMENT_NAME_WIDTH+2,HEIGHT);
      measurementsLabel.setMinimumSize(dim);
      measurementsLabel.setMaximumSize(dim);
      measurementsLabel.setPreferredSize(dim);
      titlePanel.add(measurementsLabel);

      JLabel markerPairsLabel = new JLabel("Marker Pairs");
      markerPairsLabel.setHorizontalAlignment(SwingConstants.CENTER);
      markerPairsLabel.setBorder(markerPairControlsBorder);
      markerPairsLabel.setMinimumSize(new Dimension(0,HEIGHT));
      markerPairsLabel.setMaximumSize(new Dimension(9999,HEIGHT));
      markerPairsLabel.setPreferredSize(dim);
      titlePanel.add(markerPairsLabel);

      titlePanel.add(Box.createRigidArea(new Dimension(1,HEIGHT)));

      return titlePanel;
   }

   //------------------------------------------------------------------------
   // Row header (measurement controls)
   //------------------------------------------------------------------------
   public JPanel getRowHeader(Measurement measurement, int index) {
      // Measurement controls panel
      JPanel measurementControlsPanel = new JPanel();
      measurementControlsPanel.setLayout(new BoxLayout(measurementControlsPanel, BoxLayout.X_AXIS));
      measurementControlsPanel.setBorder(measurementControlsBorder);
      measurementControlsPanel.setAlignmentX(0);

      // Add/Remove measurement button
      JButton addRemoveMeasurementButton = null;
      if(measurement!=null) {
         addRemoveMeasurementButton = new JButton(new RemoveMeasurementAction(index));
         addRemoveMeasurementButton.setRolloverIcon(removeRolloverIcon);
      } else {
         addRemoveMeasurementButton = new JButton(new AddMeasurementAction());
         addRemoveMeasurementButton.setRolloverIcon(addRolloverIcon);
      }
      addRemoveMeasurementButton.setMargin(new Insets(0,0,0,0));
      addRemoveMeasurementButton.setMinimumSize(buttonDim);
      addRemoveMeasurementButton.setMaximumSize(buttonDim);
      addRemoveMeasurementButton.setPreferredSize(buttonDim);
      addRemoveMeasurementButton.setBorder(measurementControlsInnerBorder);
      addRemoveMeasurementButton.setContentAreaFilled(false);
      addRemoveMeasurementButton.setOpaque(true);
      addRemoveMeasurementButton.setBackground(Color.white);
      measurementControlsPanel.add(addRemoveMeasurementButton);

      // Measurement name
      JTextField nameTextField = (measurement!=null) ? new JTextField(measurement.getName()) : new JTextField(DEFAULT_MEASUREMENT_NAME);
      if(measurement==null) {
         EditMeasurementNameActionAndFocusListener action = new EditMeasurementNameActionAndFocusListener(-1);
         nameTextField.setAction(action);
         nameTextField.addFocusListener(action);
         nameTextField.setForeground(Color.lightGray);
      } else {
         EditMeasurementNameActionAndFocusListener action = new EditMeasurementNameActionAndFocusListener(index);
         nameTextField.setAction(action);
         nameTextField.addFocusListener(action);
      }
      Dimension dim = new Dimension(MEASUREMENT_NAME_WIDTH, HEIGHT);
      nameTextField.setMargin(new Insets(0,0,0,0));
      nameTextField.setMinimumSize(dim);
      nameTextField.setMaximumSize(dim);
      nameTextField.setPreferredSize(dim);
      nameTextField.setBorder(null);
      measurementControlsPanel.add(nameTextField);

      // Marker pair controls panel
      JPanel markerPairControlsPanel = new JPanel();
      markerPairControlsPanel.setLayout(new BoxLayout(markerPairControlsPanel, BoxLayout.X_AXIS));
      markerPairControlsPanel.setBorder(markerPairControlsBorder);
      markerPairControlsPanel.setAlignmentX(0);

      // Add marker pair button
      JButton addMarkerPairButton = null;
      if(measurement!=null) {
         addMarkerPairButton = new JButton(new AddMarkerPairAction(index));
         addMarkerPairButton.setRolloverIcon(addRolloverIcon);
      } else {
         addMarkerPairButton = new JButton("");
      }
      if(measurement==null) addMarkerPairButton.setEnabled(false);
      addMarkerPairButton.setMargin(new Insets(0,0,0,0));
      addMarkerPairButton.setMinimumSize(buttonDim);
      addMarkerPairButton.setMaximumSize(buttonDim);
      addMarkerPairButton.setPreferredSize(buttonDim);
      addMarkerPairButton.setBorder(null);
      addMarkerPairButton.setContentAreaFilled(false);
      addMarkerPairButton.setOpaque(true);
      addMarkerPairButton.setBackground(Color.white);
      markerPairControlsPanel.add(addMarkerPairButton);

      // Put everything in a panel
      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
      panel.add(measurementControlsPanel);
      panel.add(markerPairControlsPanel);
      panel.setAlignmentX(0);
      panel.setBorder(null);

      return panel;
   }

   public void updateRowHeader() {
      rowHeader.removeAll();
      for(int i=0; i<measurementSetModel.getMeasurementSet().getSize(); i++)
         rowHeader.add(getRowHeader(measurementSetModel.getMeasurementSet().get(i),i));
      rowHeader.add(getRowHeader(null,-1));
   }

   private JTextField getMeasurementNameTextField(int i) {
      return (JTextField)((Container)((Container)rowHeader.getComponent(i)).getComponent(0)).getComponent(1);
   }

   //------------------------------------------------------------------------
   // Content (marker pairs)
   //------------------------------------------------------------------------
   public JComponent getMarkerComponent(final String name, final int measurementIndex, final int markerPairIndex, final int index) {
      Dimension dim = new Dimension(MARKER_NAME_WIDTH,HEIGHT);
      JTextField markerButton = new JTextField(name);
      markerButton.setEditable(false);
      markerButton.setHorizontalAlignment(SwingConstants.CENTER);
      // Indicate marker does not exist in model's marker set with red color (though the measurement may still be invalid
      // if this marker is not found in the marker data passed to the model scaler)
      boolean markerInModel = measurementSetModel.getMarkerExistsInModel(name);
      boolean markerInMeasurementTrial = measurementSetModel.getMarkerExistsInMeasurementTrial(name);
      if(!markerInModel || !markerInMeasurementTrial) {
         markerButton.setBackground(invalidColor);
         if(!markerInModel && !markerInMeasurementTrial) markerButton.setToolTipText("Marker not in model or measurement marker data!");
         else if(!markerInModel) markerButton.setToolTipText("Marker not in model!");
         else markerButton.setToolTipText("Marker not in measurement marker data!");
      } else {
         markerButton.setBackground(Color.white);
         markerButton.setToolTipText(null);
      }
      markerButton.setMinimumSize(dim);
      markerButton.setMaximumSize(dim);
      markerButton.setPreferredSize(dim);
      markerButton.setBorder(markerInnerBorder);
      markerButton.addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent evt) {
            JPopupMenu popup = new JPopupMenu();
            for(int i=0; i<markerNames.size(); i++) {
               JRadioButtonMenuItem item = new JRadioButtonMenuItem(new ChangeMarkerPairMarkerAction(markerNames.get(i), measurementIndex, markerPairIndex, index));
               if(markerNames.get(i).equals(name)) item.setSelected(true);
               popup.add(item);
            }
            popup.setLayout(new GridLayout(25,markerNames.size()/25+1));
            popup.show(evt.getComponent(),evt.getX(),evt.getY());
         }
      });

      return markerButton;
   }

   public JPanel getMarkerPairComponent(MarkerPair markerPair, int measurementIndex, int markerPairIndex) {
      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
      panel.setBorder(markerPairControlsBorder);
      panel.setAlignmentX(0);

      // Markers
      panel.add(getMarkerComponent(markerPair.getMarkerName(0), measurementIndex, markerPairIndex, 0));
      panel.add(getMarkerComponent(markerPair.getMarkerName(1), measurementIndex, markerPairIndex, 1));

      // Delete marker pair button
      JButton removeMarkerPairButton = new JButton(new RemoveMarkerPairAction(measurementIndex, markerPairIndex));
      removeMarkerPairButton.setRolloverIcon(removeRolloverIcon);
      removeMarkerPairButton.setMargin(new Insets(0,0,0,0));
      removeMarkerPairButton.setMinimumSize(buttonDim);
      removeMarkerPairButton.setMaximumSize(buttonDim);
      removeMarkerPairButton.setPreferredSize(buttonDim);
      removeMarkerPairButton.setBorder(null);
      removeMarkerPairButton.setContentAreaFilled(false);
      removeMarkerPairButton.setOpaque(true);
      removeMarkerPairButton.setBackground(Color.white);
      panel.add(removeMarkerPairButton);

      return panel;
   }

   public JPanel getMarkerPairsRow(Measurement measurement, int measurementIndex) {
      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
      panel.setAlignmentX(0);

      if(measurement!=null && measurement.getMarkerPairSet().getSize()>0) {
         for(int i=0; i<measurement.getMarkerPairSet().getSize(); i++)
            panel.add(getMarkerPairComponent(measurement.getMarkerPairSet().get(i),measurementIndex,i));
      } else {
         panel.add(Box.createRigidArea(new Dimension(HEIGHT+HEIGHT_INSET,HEIGHT+HEIGHT_INSET)));
      }

      return panel;
   }

   public void updateMarkerPairsPanel() {
      markerPairsPanel.removeAll();
      for(int i=0; i<measurementSetModel.getMeasurementSet().getSize(); i++)
         markerPairsPanel.add(getMarkerPairsRow(measurementSetModel.getMeasurementSet().get(i),i));
      markerPairsPanel.add(getMarkerPairsRow(null,-1));
   }

   //------------------------------------------------------------------------
   // Only update content
   //------------------------------------------------------------------------
   public void updateContent() {
      // Currently only supports updating names
      for(int i=0; i<measurementSetModel.getMeasurementSet().getSize(); i++) {
         JTextField textField = getMeasurementNameTextField(i);
         textField.setName(measurementSetModel.getMeasurementSet().get(i).getName());
      }
   }

   //------------------------------------------------------------------------
   // Reset panels
   //------------------------------------------------------------------------
   public void reset() {
      updateRowHeader();
      updateMarkerPairsPanel();
      revalidate();
      repaint();
   }

   //------------------------------------------------------------------------
   // Event handling
   //------------------------------------------------------------------------
   public void actionPerformed(ActionEvent e) {
   }

   public void update(Observable observable, Object obj) {
      // TODO: Special handling for pure renaming... don't re-create components (just call updateContent) because we don't 
      // want to lose the focus we may have in the text fields (measurement names)
      updateMarkerNames();
      reset();
   }
}

/////////////////////////////////////////////////////////////////////////////
// Testing Text Area
/////////////////////////////////////////////////////////////////////////////

class MyTextArea extends JTextArea implements Observer {
   private ScaleToolModel scaleToolModel;

   public MyTextArea(ScaleToolModel scaleToolModel) {
      this.scaleToolModel = scaleToolModel;
      scaleToolModel.addObserver(this);
      reset();
   }

   private void reset() {
      String str = new String();
      MeasurementSet measurementSet = scaleToolModel.getMeasurementSet();
      for(int i=0; i<measurementSet.getSize(); i++) {
         str += "[" + measurementSet.get(i).getName() + "]";
         for(int j=0; j<measurementSet.get(i).getMarkerPairSet().getSize(); j++)
            str += " (" + measurementSet.get(i).getMarkerPairSet().get(j).getMarkerName(0) + "," + measurementSet.get(i).getMarkerPairSet().get(j).getMarkerName(1) + ")";
         str += "\n";
      }
      setText(str);
   }

   public void update(Observable observable, Object obj) {
      reset();
   }
}

/////////////////////////////////////////////////////////////////////////////
// Main Panel
/////////////////////////////////////////////////////////////////////////////

public class MeasurementSetPanel extends javax.swing.JPanel {
   private ScaleToolModel scaleToolModel;
   private JScrollPane measurementSetScrollPane;
   
   /** Creates new form MeasurementSetPanel */
   public MeasurementSetPanel(ScaleToolModel scaleToolModel) {
      this.scaleToolModel = scaleToolModel;

      initComponents();

      measurementSetScrollPane = new MeasurementSetScrollPane(scaleToolModel);

      // Title
      JPanel title = MeasurementSetScrollPane.getTitleLabel();
      title.setAlignmentX(0);
      measurementSetScrollPane.setAlignmentX(0);
      measurementSetWithTitlePanel.add(title);
      measurementSetWithTitlePanel.add(measurementSetScrollPane);
   }
   
   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        measurementSetWithTitlePanel = new javax.swing.JPanel();

        measurementSetWithTitlePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        measurementSetWithTitlePanel.setLayout(new javax.swing.BoxLayout(measurementSetWithTitlePanel, javax.swing.BoxLayout.Y_AXIS));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(measurementSetWithTitlePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(measurementSetWithTitlePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
   
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel measurementSetWithTitlePanel;
    // End of variables declaration//GEN-END:variables
   
}
