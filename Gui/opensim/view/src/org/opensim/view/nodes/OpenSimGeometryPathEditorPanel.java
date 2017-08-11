
/*
 * OpenSimGeometryPathEditorPanel.java
 *
 * Created on May 17, 2012, 2:49:13 PM
 * Copyright (c)  2005-2012, Stanford University, Ayman Habib
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
package org.opensim.view.nodes;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import org.opensim.modeling.AbstractPathPoint;
import org.opensim.modeling.ArrayPathPoint;
import org.opensim.modeling.Component;
import org.opensim.modeling.ConditionalPathPoint;
import org.opensim.modeling.Constant;
import org.opensim.modeling.Coordinate;
import org.opensim.modeling.CoordinateSet;
import org.opensim.modeling.FrameIterator;
import org.opensim.modeling.FrameList;
import org.opensim.modeling.Function;
import org.opensim.modeling.GeometryPath;
import org.opensim.modeling.Model;
import org.opensim.modeling.MovingPathPoint;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PathPoint;
import org.opensim.modeling.PathPointSet;
import org.opensim.modeling.PathWrap;
import org.opensim.modeling.PhysicalFrame;
import org.opensim.modeling.SetPathWrap;
import org.opensim.modeling.SetWrapObject;
import org.opensim.modeling.State;
import org.opensim.modeling.Units;
import org.opensim.modeling.WrapEllipsoid;
import org.opensim.modeling.WrapObject;
import org.opensim.view.ObjectsDeletedEvent;
import org.opensim.view.Selectable;
import org.opensim.view.SingleModelGuiElements;
import org.opensim.view.SingleModelVisuals;
import org.opensim.view.editors.MusclePointFunctionEventListener;
import org.opensim.view.functionEditor.FunctionEditorTopComponent;
import org.opensim.view.functionEditor.FunctionEditorTopComponent.FunctionEditorOptions;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;
import static org.opensim.view.pub.ViewDB.isVtkGraphicsAvailable;

/**
 *
 * @author ayman
 */
public class OpenSimGeometryPathEditorPanel extends javax.swing.JPanel {

   private javax.swing.JScrollPane WrapTab = null;
   //private javax.swing.JScrollPane CurrentPathTab = null;
   private String selectedTabName = null;
   private javax.swing.JCheckBox attachmentSelectBox[] = null; // array of checkboxes for selecting attachment points
   private String[] wrapObjectNames = null;
   private static final String[] wrapMethodNames = {"hybrid", "midpoint", "axial"};
   private static final String[] musclePointTypeNames = {"fixed", "via", "moving"};
   private static final String[] musclePointClassNames = {"PathPoint", "ConditionalPathPoint", "MovingPathPoint"};
   private NumberFormat doublePropFormat = NumberFormat.getInstance();
   private NumberFormat intPropFormat = NumberFormat.getIntegerInstance();
   private NumberFormat positionFormat = NumberFormat.getInstance();
   private NumberFormat angleFormat = NumberFormat.getInstance();
   private javax.swing.JScrollPane AttachmentsTab = null;
   private OpenSimContext openSimContext;
   private Model currentModel;
   private OpenSimObject objectWithPath = null; // the actuator that is currently shown in the Muscle Editor window
   private GeometryPath savePath;
   private GeometryPath currentPath;
    private JButton RestoreButton;
   /** Creates new form OpenSimGeometryPathEditorPanel */
    public OpenSimGeometryPathEditorPanel(GeometryPath pathToEdit) {
        currentModel = pathToEdit.getModel();
        savePath = GeometryPath.safeDownCast(pathToEdit.clone());
        currentPath = pathToEdit;
        openSimContext = OpenSimDB.getInstance().getContext(currentModel);
        initComponents();
        objectWithPath = pathToEdit.getOwner();
        setupComponent(objectWithPath);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        MuscleEditorScrollPane = new javax.swing.JScrollPane();
        MuscleEditorPanel = new javax.swing.JPanel();
        ParametersTabbedPanel = new javax.swing.JTabbedPane();

        MuscleEditorScrollPane.setBorder(null);

        MuscleEditorPanel.setMinimumSize(new java.awt.Dimension(5, 5));

        javax.swing.GroupLayout MuscleEditorPanelLayout = new javax.swing.GroupLayout(MuscleEditorPanel);
        MuscleEditorPanel.setLayout(MuscleEditorPanelLayout);
        MuscleEditorPanelLayout.setHorizontalGroup(
            MuscleEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 702, Short.MAX_VALUE)
            .addGroup(MuscleEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(MuscleEditorPanelLayout.createSequentialGroup()
                    .addComponent(ParametersTabbedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 692, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        MuscleEditorPanelLayout.setVerticalGroup(
            MuscleEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 272, Short.MAX_VALUE)
            .addGroup(MuscleEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(ParametersTabbedPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE))
        );

        MuscleEditorScrollPane.setViewportView(MuscleEditorPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MuscleEditorScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MuscleEditorScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
   public void setupComponent(OpenSimObject newAct) {
      // Remove all previous GUI components and their panels.
      // If the type of actuator changed since the last time
      // the panels were set up, there could be different
      // numbers and types of components and panels.
      // But before they are removed, store the name of the
      // currently selected tab (if any) so that you can try
      // to restore this selection (e.g., after the "restore"
      // button is pressed.
      // Since MuscleComboBox was added to the GUI, the muscle
      // editor now always has a current actuator (as long as there
      // is a current model). So if 'act' is passed in as null,
      // make the model's first actuator the current one.

      //currentModel = OpenSimDB.getInstance().getCurrentModel();
      SingleModelGuiElements guiElem = null;

      // Save the currently selected GUI tab.
      if (objectWithPath != null) {
         java.awt.Component comp = ParametersTabbedPanel.getSelectedComponent();
         if (comp != null)
            selectedTabName = comp.getName();
         else
            selectedTabName = null;
      }
      ParametersTabbedPanel.removeAll();
      AttachmentsTab = null;
      WrapTab = null;
      //CurrentPathTab = null;

      // Set the current actuator to the newly selected one (should only be null
      // if the model is null or if the model has no actuators).
      objectWithPath = newAct;
      
      // Add the attachment panel first so it will always have index=0
      setupAttachmentPanel();
      
      int i, j;
      int numGroups = 0;
      javax.swing.JScrollPane propTab[] = new javax.swing.JScrollPane[numGroups + 1];
      javax.swing.JPanel propPanel[] = new javax.swing.JPanel[numGroups + 1];
      int tabPropertyCount[] = new int[numGroups + 1];

      // Create the "other" panel to hold properties that are not in a group.
      propTab[numGroups] = new javax.swing.JScrollPane();
      propPanel[numGroups] = new javax.swing.JPanel();
      propPanel[numGroups].setLayout(null);
      //propPanel[numGroups].setBackground(new java.awt.Color(200, 200, 255));
      propTab[numGroups].setViewportView(propPanel[numGroups]);
      propTab[numGroups].setName("Other");
      ParametersTabbedPanel.addTab("Other", null, propTab[numGroups], "other parameters");
      tabPropertyCount[numGroups] = 0;
      
      // Set the preferred sizes of the property tabs.
      // If any of them have no properties, remove them
      // from the window.
      for (i = 0; i <= numGroups; i++) {
         if (tabPropertyCount[i] == 0) {
            ParametersTabbedPanel.remove(propTab[i]);
         } else {
            Dimension d = new Dimension(350, 30 + tabPropertyCount[i] * 22);
            propPanel[i].setPreferredSize(d);
         }
      }
      
      // Add the wrap and current panels last
      setupWrapPanel();
            
      // Set the selected tab in the ParametersTabbedPanel to the
      // one whose name matches selectedTabName.
      java.awt.Component[] components = ParametersTabbedPanel.getComponents();
      if (selectedTabName == null) {
         ParametersTabbedPanel.setSelectedComponent(components[0]);
      } else {
         for (i = 0; i < components.length; i++) {
            if (components[i].getName().equals(selectedTabName)) {
               ParametersTabbedPanel.setSelectedComponent(components[i]);
               break;
            }
         }
      }
      
      //Dimension windowSize = MuscleEditorScrollPane.getOwner().getSize();
      Dimension d = new Dimension(565, 358);
      MuscleEditorPanel.setPreferredSize(d);
      
      this.revalidate();
      this.repaint();
   }
   
   public void EditPathPointFunction(javax.swing.JButton button, int attachmentNum, int xyz) {
      //Muscle asm = Muscle.safeDownCast(objectWithPath);
      //if (asm != null) {
         MovingPathPoint mmp = MovingPathPoint.safeDownCast(currentPath.getPathPointSet().get(attachmentNum));
         if (mmp != null) {
            Function function = null;
            Coordinate coordinate = null;
            FunctionEditorOptions options = new FunctionEditorOptions();
            if (xyz == 0) {
               function = mmp.get_x_location();
               coordinate = mmp.getXCoordinate();
               options.title = "X offset";
            } else if (xyz == 1) {
               function = mmp.get_y_location();
               coordinate = mmp.getYCoordinate();
               options.title = "Y offset";
            } else if (xyz == 2) {
               function = mmp.get_z_location();
               coordinate = mmp.getZCoordinate();
               options.title = "Z offset";
            }
            if (coordinate != null) {
               if (coordinate.getMotionType() == Coordinate.MotionType.Rotational) {
                  options.XUnits = new Units(Units.UnitType.Radians);
                  options.XDisplayUnits = new Units(Units.UnitType.Degrees);
               } else {
                  options.XUnits = new Units(Units.UnitType.Meters);
                  options.XDisplayUnits = options.XUnits;
               }
               options.XLabel = coordinate.getName() + " (deg)";
            }
            options.YUnits = new Units(Units.UnitType.Meters);
            options.YDisplayUnits = options.YUnits;
            FunctionEditorTopComponent win = FunctionEditorTopComponent.findInstance();
            win.addChangeListener(new MusclePointFunctionEventListener());
            options.YLabel = options.YDisplayUnits.getLabel();
            Vector<OpenSimObject> objects = new Vector<OpenSimObject>(1);
            objects.add(objectWithPath);
            win.open(currentModel, mmp, objects, function, options);
         }
      //}
   }

   public void addAttachmentPerformed(int menuChoice) {
      //Muscle asm = Muscle.safeDownCast(objectWithPath);
      PathPointSet pathPoints = currentPath.getPathPointSet();
      int index = menuChoice;
      if (index > pathPoints.getSize() - 1)
         index = pathPoints.getSize() - 1;

      // ideally we'd like to just deselect the point we're deleting but the muscle displayer doesn't
      // deal well with maintaining the right glyph colors when the attachment set changes.
      // TODO: send some event that the muscle displayer can listen for and know to deselect the point
      // and make sure the rest of the points maintain correct selection status
      ViewDB.getInstance().removeObjectsBelongingToMuscleFromSelection(objectWithPath);
      
      AbstractPathPoint closestPoint = pathPoints.get(index);
      OpenSimContext context =OpenSimDB.getInstance().getContext(currentPath.getModel());
      // FIX40 fails because needs full initSystem rather than invalidating stage
      context.addPathPoint(currentPath, menuChoice, closestPoint.getBody());
      
      setupComponent(objectWithPath);
        //updateDisplay();
   }

   public void deleteAttachmentPerformed(int menuChoice) {
      //Muscle asm = Muscle.safeDownCast(objectWithPath);
      // The point may not be deleted, but save a reference to it so that if it is deleted
      // you can fire an ObjectsDeletedEvent later.
      AbstractPathPoint mp = currentPath.getPathPointSet().get(menuChoice);
      ViewDB.getInstance().removeObjectsBelongingToMuscleFromSelection(objectWithPath);
      OpenSimContext context =OpenSimDB.getInstance().getContext(currentPath.getModel());
      
      boolean result = context.deletePathPoint(currentPath, menuChoice);
      if (result == false) {
         Object[] options = {"OK"};
         int answer = JOptionPane.showOptionDialog(this,
                 "A muscle must contain at least 2 attachment points that are not via points.",
                 "Muscle Editor",
                 JOptionPane.OK_OPTION,
                 JOptionPane.WARNING_MESSAGE,
                 null,
                 options,
                 options[0]);
          ViewDB.getInstance().setSelectedObject(mp);

      } else {
         // ideally we'd like to just deselect the point we're deleting but the muscle displayer doesn't
         // deal well with maintaining the right glyph colors when the attachment set changes.
         // TODO: send some event that the muscle displayer can listen for and know to deselect the point
         // and make sure the rest of the points maintain correct selection status
         ViewDB.getInstance().removeObjectsBelongingToMuscleFromSelection(objectWithPath);
         
         setupComponent(objectWithPath);
         Model model = currentPath.getModel();
         // Fire an ObjectsDeletedEvent.
         Vector<OpenSimObject> objs = new Vector<OpenSimObject>(1);
         objs.add(mp);
         ObjectsDeletedEvent evnt = new ObjectsDeletedEvent(this, model, objs);
         OpenSimDB.getInstance().setChanged();
         OpenSimDB.getInstance().notifyObservers(evnt);
         updatePathDisplay(model);
      }
   }
   
  public void setupAttachmentPanel() {
      if (AttachmentsTab != null)
         ParametersTabbedPanel.remove(AttachmentsTab);
      AttachmentsTab = new javax.swing.JScrollPane();
      AttachmentsTab.setName("Attachments");
      ParametersTabbedPanel.insertTab("Attachments", null, AttachmentsTab, "Attachment points", 0);

      updateAttachmentPanel();
   }

     private void updateAttachmentPanel() {
      javax.swing.JPanel AttachmentsPanel = new javax.swing.JPanel();
      AttachmentsPanel.setLayout(null);
      //AttachmentsPanel.setBackground(new java.awt.Color(200, 200, 255));
      AttachmentsTab.setViewportView(AttachmentsPanel);
      
      // Put the attachment points in the attachments tab
      PathPointSet pathPoints = currentPath.getPathPointSet();
      int aCount = 0;
      int X = 30;
      int Y = 40;
      
      // Set up the muscle-independent labels
      boolean anyViaPoints = false;
      javax.swing.JLabel attachmentSelLabel = new javax.swing.JLabel();
      attachmentSelLabel.setText("Sel");
      attachmentSelLabel.setBounds(X + 3, Y - 30, 25, 16);
      javax.swing.JLabel attachmentTypeLabel = new javax.swing.JLabel();
      attachmentTypeLabel.setText("Type");
      attachmentTypeLabel.setBounds(X + 25, Y - 30, 40, 16);
      javax.swing.JLabel attachmentXLabel = new javax.swing.JLabel();
      attachmentXLabel.setText("X");
      attachmentXLabel.setBounds(X + 120, Y - 30, 10, 16);
      javax.swing.JLabel attachmentYLabel = new javax.swing.JLabel();
      attachmentYLabel.setText("Y");
      attachmentYLabel.setBounds(X + 180, Y - 30, 10, 16);
      javax.swing.JLabel attachmentZLabel = new javax.swing.JLabel();
      attachmentZLabel.setText("Z");
      attachmentZLabel.setBounds(X + 240, Y - 30, 10, 16);
      javax.swing.JLabel attachmentFrameLabel = new javax.swing.JLabel();
      attachmentFrameLabel.setText("Frame");
      attachmentFrameLabel.setBounds(X + 300, Y - 30, 30, 16);
      javax.swing.JLabel coordLabel = new javax.swing.JLabel();
      coordLabel.setText("Coordinate");
      coordLabel.setBounds(X + 400, Y - 30, 90, 16);
      javax.swing.JLabel rangeMinLabel = new javax.swing.JLabel();
      rangeMinLabel.setText("Min");
      rangeMinLabel.setBounds(X + 530, Y - 30, 60, 16);
      javax.swing.JLabel rangeMaxLabel = new javax.swing.JLabel();
      rangeMaxLabel.setText("Max");
      rangeMaxLabel.setBounds(X + 590, Y - 30, 60, 16);
      AttachmentsPanel.add(attachmentSelLabel);
      AttachmentsPanel.add(attachmentTypeLabel);
      AttachmentsPanel.add(attachmentXLabel);
      AttachmentsPanel.add(attachmentYLabel);
      AttachmentsPanel.add(attachmentZLabel);
      AttachmentsPanel.add(attachmentFrameLabel);

      SingleModelGuiElements guiElem = OpenSimDB.getInstance().getModelGuiElements(currentPath.getModel());
      String[] physicalFrameNames = guiElem.getPhysicalFrameNames();
      String[] coordinateNames = guiElem.getCoordinateNames();
      int numGuiLines = 0; // after for loop, will = numPoints + numMovingPathPoints

      attachmentSelectBox = new javax.swing.JCheckBox[pathPoints.getSize()];
      
      for (int i = 0; i < pathPoints.getSize(); i++, numGuiLines++) {
         ConditionalPathPoint via = ConditionalPathPoint.safeDownCast(pathPoints.get(i));
         MovingPathPoint mmp = MovingPathPoint.safeDownCast(pathPoints.get(i));

         int height = Y + numGuiLines * 25;
         int width = 60;
         int x = X;
         int y = Y;
         final int num = i;

         // The number label for the point
         javax.swing.JLabel indexLabel = null;
         indexLabel = new javax.swing.JLabel();
         indexLabel.setText(intPropFormat.format(num+1) + ".");
         indexLabel.setBounds(X - 20, height, 20, 21);
         AttachmentsPanel.add(indexLabel);

         // The checkbox for selecting/unselecting the point for editing
         attachmentSelectBox[i] = new javax.swing.JCheckBox();
         attachmentSelectBox[i].setBounds(x, height, 21, 21);
         attachmentSelectBox[i].setToolTipText("Click to select/unselect this attachment point");
         attachmentSelectBox[i].addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               AttachmentSelected(((javax.swing.JCheckBox)evt.getSource()), num);
            }
         });
         AttachmentsPanel.add(attachmentSelectBox[i]);
         if (mmp != null)
            attachmentSelectBox[i].setEnabled(false);
         x += 30;

         // The combo box specifying the type of the point
         javax.swing.JComboBox pointTypeComboBox = new javax.swing.JComboBox();
         pointTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(musclePointTypeNames));
         pointTypeComboBox.setBounds(x, height, 65, 21);
         pointTypeComboBox.setToolTipText("The type of this attachment point");
         pointTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               PathPointTypeChosen(((javax.swing.JComboBox)evt.getSource()), num);
            }
         });
         int typeIndex = 0;
         if (via != null)
            typeIndex = 1;
         else if (mmp != null)
            typeIndex = 2;
         pointTypeComboBox.setSelectedIndex(typeIndex);
         AttachmentsPanel.add(pointTypeComboBox);
         x += 70;

         State state = openSimContext.getCurrentStateRef();
         // The X coordinate of the point
         if (mmp == null) {
            javax.swing.JTextField xField = new javax.swing.JTextField();
            xField.setHorizontalAlignment(SwingConstants.TRAILING);
            xField.setBounds(x, height, width - 5, 21);
            xField.setText(positionFormat.format(pathPoints.get(i).getLocation(state).get(0)));
            xField.setToolTipText("X coordinate of the attachment point");
            xField.addActionListener(new java.awt.event.ActionListener() {
               public void actionPerformed(java.awt.event.ActionEvent evt) {
                  AttachmentPointEntered(((javax.swing.JTextField)evt.getSource()), num, 0);
               }
            });
            xField.addFocusListener(new java.awt.event.FocusAdapter() {
               public void focusLost(java.awt.event.FocusEvent evt) {
                  if (!evt.isTemporary())
                     AttachmentPointEntered(((javax.swing.JTextField)evt.getSource()), num, 0);
               }
            });
            AttachmentsPanel.add(xField);
         } else {
            numGuiLines++;
            javax.swing.JButton editXButton = new javax.swing.JButton();
            editXButton.setBounds(x, height, width - 5, 21);
            editXButton.setText("Edit");
            editXButton.setEnabled(true);
            editXButton.setToolTipText("Edit the function controlling this X coordinate");
            editXButton.addActionListener(new java.awt.event.ActionListener() {
               public void actionPerformed(java.awt.event.ActionEvent evt) {
                  EditPathPointFunction(((javax.swing.JButton)evt.getSource()), num, 0);
               }
            });
            AttachmentsPanel.add(editXButton);
            // Combo box for changing the coordinate. If the function is a Constant,
            // disable the combo box.
            javax.swing.JComboBox XCoordComboBox = new javax.swing.JComboBox();
            XCoordComboBox.setModel(new javax.swing.DefaultComboBoxModel(coordinateNames));
            XCoordComboBox.setBounds(x, height + 22, width - 5, 21);
            XCoordComboBox.setToolTipText("The coordinate that controls the X offset for this attachment point");
            XCoordComboBox.addActionListener(new java.awt.event.ActionListener() {
               public void actionPerformed(java.awt.event.ActionEvent evt) {
                  MovingPathPointCoordinateChosen(((javax.swing.JComboBox)evt.getSource()), num, 0);
               }
            });
            Function Xfunction = mmp.get_x_location();
            if (Xfunction != null && Xfunction instanceof Constant) {
               XCoordComboBox.setEnabled(false);
            } else {
               XCoordComboBox.setSelectedIndex(findElement(coordinateNames, mmp.getXCoordinate().getName()));
            }
            AttachmentsPanel.add(XCoordComboBox);
         }
         x += width;

         // The Y coordinate of the point
         if (mmp == null) {
            javax.swing.JTextField yField = new javax.swing.JTextField();
            yField.setHorizontalAlignment(SwingConstants.TRAILING);
            yField.setBounds(x, height, width - 5, 21);
            yField.setText(positionFormat.format(pathPoints.get(i).getLocation(state).get(1)));
            yField.setToolTipText("Y coordinate of the attachment point");
            yField.addActionListener(new java.awt.event.ActionListener() {
               public void actionPerformed(java.awt.event.ActionEvent evt) {
                  AttachmentPointEntered(((javax.swing.JTextField)evt.getSource()), num, 1);
               }
            });
            yField.addFocusListener(new java.awt.event.FocusAdapter() {
               public void focusLost(java.awt.event.FocusEvent evt) {
                  if (!evt.isTemporary())
                     AttachmentPointEntered(((javax.swing.JTextField)evt.getSource()), num, 1);
               }
            });
            AttachmentsPanel.add(yField);
         } else {
            javax.swing.JButton editYButton = new javax.swing.JButton();
            editYButton.setBounds(x, height, width - 5, 21);
            editYButton.setText("Edit");
            editYButton.setEnabled(true);
            editYButton.setToolTipText("Edit the function controlling this Y coordinate");
            editYButton.addActionListener(new java.awt.event.ActionListener() {
               public void actionPerformed(java.awt.event.ActionEvent evt) {
                  EditPathPointFunction(((javax.swing.JButton)evt.getSource()), num, 1);
               }
            });
            AttachmentsPanel.add(editYButton);
            // Combo box for changing the coordinate. If the function is a Constant,
            // disable the combo box.
            javax.swing.JComboBox YCoordComboBox = new javax.swing.JComboBox();
            YCoordComboBox.setModel(new javax.swing.DefaultComboBoxModel(coordinateNames));
            YCoordComboBox.setBounds(x, height + 22, width - 5, 21);
            YCoordComboBox.setToolTipText("The coordinate that controls the Y offset for this attachment point");
            YCoordComboBox.addActionListener(new java.awt.event.ActionListener() {
               public void actionPerformed(java.awt.event.ActionEvent evt) {
                  MovingPathPointCoordinateChosen(((javax.swing.JComboBox)evt.getSource()), num, 1);
               }
            });
            Function Yfunction = mmp.get_y_location();
            if (Yfunction != null && Yfunction instanceof Constant) {
               YCoordComboBox.setEnabled(false);
            } else {
               YCoordComboBox.setSelectedIndex(findElement(coordinateNames, mmp.getYCoordinate().getName()));
            }
            AttachmentsPanel.add(YCoordComboBox);
         }
         x += width;

         // The Z coordinate of the point
         if (mmp == null) {
            javax.swing.JTextField zField = new javax.swing.JTextField();
            zField.setHorizontalAlignment(SwingConstants.TRAILING);
            zField.setBounds(x, height, width - 5, 21);
            zField.setText(positionFormat.format(pathPoints.get(i).getLocation(state).get(2)));
            zField.setToolTipText("Z coordinate of the attachment point");
            zField.addActionListener(new java.awt.event.ActionListener() {
               public void actionPerformed(java.awt.event.ActionEvent evt) {
                  AttachmentPointEntered(((javax.swing.JTextField)evt.getSource()), num, 2);
               }
            });
            zField.addFocusListener(new java.awt.event.FocusAdapter() {
               public void focusLost(java.awt.event.FocusEvent evt) {
                  if (!evt.isTemporary())
                     AttachmentPointEntered(((javax.swing.JTextField)evt.getSource()), num, 2);
               }
            });
            AttachmentsPanel.add(zField);
         } else {
            javax.swing.JButton editZButton = new javax.swing.JButton();
            editZButton.setBounds(x, height, width - 5, 21);
            editZButton.setText("Edit");
            editZButton.setEnabled(true);
            editZButton.setToolTipText("Edit the function controlling this Z coordinate");
            editZButton.addActionListener(new java.awt.event.ActionListener() {
               public void actionPerformed(java.awt.event.ActionEvent evt) {
                  EditPathPointFunction(((javax.swing.JButton)evt.getSource()), num, 2);
               }
            });
            AttachmentsPanel.add(editZButton);
            // Combo box for changing the coordinate. If the function is a Constant,
            // disable the combo box.
            javax.swing.JComboBox ZCoordComboBox = new javax.swing.JComboBox();
            ZCoordComboBox.setModel(new javax.swing.DefaultComboBoxModel(coordinateNames));
            ZCoordComboBox.setBounds(x, height + 22, width - 5, 21);
            ZCoordComboBox.setToolTipText("The coordinate that controls the Z offset for this attachment point");
            ZCoordComboBox.addActionListener(new java.awt.event.ActionListener() {
               public void actionPerformed(java.awt.event.ActionEvent evt) {
                  MovingPathPointCoordinateChosen(((javax.swing.JComboBox)evt.getSource()), num, 2);
               }
            });
            Function Zfunction = mmp.get_z_location();
            if (Zfunction != null && Zfunction instanceof Constant) {
               ZCoordComboBox.setEnabled(false);
            } else {
               ZCoordComboBox.setSelectedIndex(findElement(coordinateNames, mmp.getZCoordinate().getName()));
            }
            AttachmentsPanel.add(ZCoordComboBox);
         }
         x += width;

         // The combo box containing the body the point is attached to
         javax.swing.JComboBox comboBox = new javax.swing.JComboBox();
         comboBox.setModel(new javax.swing.DefaultComboBoxModel(physicalFrameNames));
         comboBox.setSelectedIndex(findElement(physicalFrameNames, pathPoints.get(i).getBodyName()));
         comboBox.setBounds(x, height, 90, 21);
         comboBox.setToolTipText("Frame the attachment point is fixed to");
         comboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               AttachmentFrameChosen(((javax.swing.JComboBox)evt.getSource()), num);
            }
         });
         AttachmentsPanel.add(comboBox);
         x += 100;
         
         // GUI items for via points (coord combo box, min range field, max range field)
         if (via != null) {
            anyViaPoints = true;
            double conversion = 1.0;
            NumberFormat nf =  angleFormat;
            Coordinate coordinate = via.getCoordinate();
            if (coordinate != null) {
               if (coordinate.getMotionType() == Coordinate.MotionType.Rotational) {
                  conversion = 180.0/Math.PI;
                  nf = angleFormat;
               } else {
                  nf = positionFormat;
               }
            }

            // The combo box containing the coordinate for the via point
            javax.swing.JComboBox coordComboBox = new javax.swing.JComboBox();
            coordComboBox.setModel(new javax.swing.DefaultComboBoxModel(coordinateNames));
            coordComboBox.setSelectedIndex(findElement(coordinateNames, via.getCoordinate().getName()));
            coordComboBox.setBounds(x, height, 130, 21);
            coordComboBox.addActionListener(new java.awt.event.ActionListener() {
               public void actionPerformed(java.awt.event.ActionEvent evt) {
                  ViaCoordinateChosen(((javax.swing.JComboBox)evt.getSource()), num);
               }
            });
            AttachmentsPanel.add(coordComboBox);
            x += 140;

            // The min range of the coordinate range
            javax.swing.JTextField rangeMinField = new javax.swing.JTextField();
            rangeMinField.setText(nf.format(via.get_range(0)*conversion));
            rangeMinField.setBounds(x, height, 60, 21);
            rangeMinField.addActionListener(new java.awt.event.ActionListener() {
               public void actionPerformed(java.awt.event.ActionEvent evt) {
                  RangeMinEntered(((javax.swing.JTextField)evt.getSource()), num);
               }
            });
            rangeMinField.addFocusListener(new java.awt.event.FocusAdapter() {
               public void focusLost(java.awt.event.FocusEvent evt) {
                  if (!evt.isTemporary())
                     RangeMinEntered(((javax.swing.JTextField)evt.getSource()), num);
               }
            });
            AttachmentsPanel.add(rangeMinField);
            x += 65;

            // The max range of the coordinate range
            javax.swing.JTextField rangeMaxField = new javax.swing.JTextField();
            rangeMaxField.setText(nf.format(via.get_range(1)*conversion));
            rangeMaxField.setBounds(x, height, 60, 21);
            rangeMaxField.addActionListener(new java.awt.event.ActionListener() {
               public void actionPerformed(java.awt.event.ActionEvent evt) {
                  RangeMaxEntered(((javax.swing.JTextField)evt.getSource()), num);
               }
            });
            rangeMaxField.addFocusListener(new java.awt.event.FocusAdapter() {
               public void focusLost(java.awt.event.FocusEvent evt) {
                  if (!evt.isTemporary())
                     RangeMaxEntered(((javax.swing.JTextField)evt.getSource()), num);
               }
            });
            AttachmentsPanel.add(rangeMaxField);
         }
      }
      
      // The add menu
      final javax.swing.JPopupMenu addMenu = new javax.swing.JPopupMenu();
      javax.swing.JMenuItem firstMenuItem = new JMenuItem("before 1");
      firstMenuItem.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            addAttachmentPerformed(0);
         }
      });
      addMenu.add(firstMenuItem);
      for (int i = 0; i < pathPoints.getSize() - 1; i++) {
         javax.swing.JMenuItem menuItem = new JMenuItem("between "+String.valueOf(i+1)+" and "+String.valueOf(i+2));
         final int index = i + 1;
         menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               addAttachmentPerformed(index);
            }
         });
         addMenu.add(menuItem);
      }
      javax.swing.JMenuItem lastMenuItem = new JMenuItem("after "+String.valueOf(pathPoints.getSize()));
      final int index = pathPoints.getSize();
      lastMenuItem.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            addAttachmentPerformed(index);
         }
      });
      addMenu.add(lastMenuItem);
      
      // Add the "add" button
      javax.swing.JButton addButton = new javax.swing.JButton();
      addButton.setText("Add");
      addButton.setToolTipText("Add an attachment point");
      addButton.setBounds(X + 100, Y + 20 + numGuiLines * 25, 70, 21);
      AttachmentsPanel.add(addButton);
      RestoreButton = new javax.swing.JButton();
      RestoreButton.setText("Restore");
      RestoreButton.setBounds(X + 300, Y + 20 + numGuiLines * 25, 70, 21);
      RestoreButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                RestoreButtonActionPerformed(null);
            }
        });
     AttachmentsPanel.add(RestoreButton);
      
      class PopupListener extends MouseAdapter {
         public void mousePressed(MouseEvent e) {
            //maybeShowPopup(e);
            addMenu.show(e.getComponent(),
                    e.getX(), e.getY());
         }
         
         public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
         }
         
         private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
               addMenu.show(e.getComponent(),
                       e.getX(), e.getY());
            }
         }
      }
      
      MouseListener popupListener = new PopupListener();
      addButton.addMouseListener(popupListener);
      
      // the "delete" menu
      final javax.swing.JPopupMenu deleteMenu = new javax.swing.JPopupMenu();
      for (int i = 0; i < pathPoints.getSize(); i++) {
         javax.swing.JMenuItem menuItem = new JMenuItem(String.valueOf(i+1));
         final int deleteIndex = i;
         menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               deleteAttachmentPerformed(deleteIndex);
            }
         });
         deleteMenu.add(menuItem);
      }
      
      // Add the "delete" button
      javax.swing.JButton deleteButton = new javax.swing.JButton();
      deleteButton.setText("Delete");
      deleteButton.setToolTipText("Delete an attachment point");
      deleteButton.setBounds(X + 200, Y + 20 + numGuiLines * 25, 70, 21);
      AttachmentsPanel.add(deleteButton);
      
      class PopupListenerDelete extends MouseAdapter {
         public void mousePressed(MouseEvent e) {
            //maybeShowPopup(e);
            deleteMenu.show(e.getComponent(),
                    e.getX(), e.getY());
         }
         
         public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
         }
         
         private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
               deleteMenu.show(e.getComponent(),
                       e.getX(), e.getY());
            }
         }
      }
      
      MouseListener popupListenerDelete = new PopupListenerDelete();
      deleteButton.addMouseListener(popupListenerDelete);
      
      Dimension d = new Dimension(640, Y + 45 + numGuiLines * 22);
      if (anyViaPoints) {
         d.width = 920;
         AttachmentsPanel.add(coordLabel);
         AttachmentsPanel.add(rangeMinLabel);
         AttachmentsPanel.add(rangeMaxLabel);
      }
      AttachmentsPanel.setPreferredSize(d);
      
      // Update the checked/unchecked state of the selected checkboxes
      ArrayList<Selectable> selectedObjects = ViewDB.getInstance().getSelectedObjects();
      for (int i = 0; i < selectedObjects.size(); i++)
         updateAttachmentSelections(selectedObjects.get(i), true);
   }

   private void updateAttachmentSelections(Selectable selectedObject, boolean state) {
      if (objectWithPath != null) {
         OpenSimObject obj = selectedObject.getOpenSimObject();
         //Muscle asm = Muscle.safeDownCast(objectWithPath);
         if (obj!=null) {
            PathPointSet pathPoints = currentPath.getPathPointSet();
            for (int i = 0; i < pathPoints.getSize(); i++) {
               if (OpenSimObject.getCPtr(obj) == PathPoint.getCPtr(pathPoints.get(i))) {
                  attachmentSelectBox[i].setSelected(state);
                  this.repaint();
                  break;
               }
            }
         }
      }
   }

   public void setupWrapPanel() {
      if (WrapTab != null)
         ParametersTabbedPanel.remove(WrapTab);
      javax.swing.JScrollPane WrapTab = new javax.swing.JScrollPane();
      javax.swing.JPanel WrapPanel = new javax.swing.JPanel();
      WrapPanel.setLayout(null);
      //WrapPanel.setBackground(new java.awt.Color(200, 200, 255));
      WrapTab.setViewportView(WrapPanel);
      WrapTab.setName("Wrapping");
      ParametersTabbedPanel.insertTab("Wrapping", null, WrapTab, "Wrapping parameters", ParametersTabbedPanel.getTabCount());
      
      // Set up the Wrap Panel
      int i, j, k, wCount = 0;
      int numAttachments = currentPath.getPathPointSet().getSize();
      int X = 80;
      int Y = 40;
      
      SetPathWrap smw = currentPath.getWrapSet();
      String[] startPointNames = new String[numAttachments + 1];
      startPointNames[0] = new String("first");
      String[] endPointNames = new String[numAttachments + 1];
      endPointNames[numAttachments] = new String("last");
      for (i = 0; i < numAttachments; i++) {
         startPointNames[i+1] = String.valueOf(i+1);
         endPointNames[i] = String.valueOf(i+1);
      }
      
      // Count the number of wrap objects not currently assigned to this muscle.
      FrameList frames = currentModel.getFrameList();
      FrameIterator fIter = frames.begin();
      int numWrapObjects = 0;
      while (!fIter.equals(frames.end())) {
          PhysicalFrame phFrame = PhysicalFrame.safeDownCast(fIter.__deref__());
          if (phFrame !=null)
            numWrapObjects += phFrame.getWrapObjectSet().getSize();
         fIter.next();
      }
      numWrapObjects -= smw.getSize();
      
      // Create an array of names of all of the model's wrap objects
      // that are not currently assigned to this muscle. These will be
      // used to make a comboBox that appears when the user clicks the
      // "add" button.
      wrapObjectNames = new String[numWrapObjects];
      fIter = frames.begin();
      while (!fIter.equals(frames.end())) {
         PhysicalFrame phFrame = PhysicalFrame.safeDownCast(fIter.__deref__());
         if (phFrame != null) {
             SetWrapObject wrapObjects = phFrame.getWrapObjectSet();
             for (j = 0; j < wrapObjects.getSize(); j++) {
                 for (k = 0; k < smw.getSize(); k++) {
                     if (WrapObject.getCPtr(wrapObjects.get(j)) == WrapObject.getCPtr(smw.get(k).getWrapObject())) {
                         break;
                     }
                 }
                 if (k == smw.getSize()) {
                     wrapObjectNames[wCount++] = new String(wrapObjects.get(j).getName());
                 }
             }
         }
         fIter.next();
      }
      
      // Set up the muscle-independent labels
      javax.swing.JLabel wrapObjectLabel = new javax.swing.JLabel();
      wrapObjectLabel.setText("Object");
      wrapObjectLabel.setBounds(X + 30, 10, 50, 16);
      WrapPanel.add(wrapObjectLabel);
      javax.swing.JLabel wrapMethodLabel = new javax.swing.JLabel();
      wrapMethodLabel.setText("Method");
      wrapMethodLabel.setBounds(X + 150, 10, 50, 16);
      WrapPanel.add(wrapMethodLabel);
      javax.swing.JLabel wrapStartLabel = new javax.swing.JLabel();
      wrapStartLabel.setText("Start Pt");
      wrapStartLabel.setBounds(X + 223, 10, 80, 16);
      WrapPanel.add(wrapStartLabel);
      javax.swing.JLabel wrapEndLabel = new javax.swing.JLabel();
      wrapEndLabel.setText("End Pt");
      wrapEndLabel.setBounds(X + 285, 10, 80, 16);
      WrapPanel.add(wrapEndLabel);
      //javax.swing.JLabel editLabel = new javax.swing.JLabel();
      //editLabel.setText("Edit");
      //editLabel.setBounds(X + 355, 10, 40, 16);
      //WrapPanel.add(editLabel);
      
      for (i = 0; i < smw.getSize(); i++) {
         final int num = i;
         boolean isEllipsoid = false;
         WrapObject awo = smw.get(i).getWrapObject();
         WrapEllipsoid we = WrapEllipsoid.safeDownCast(awo);
         if (we != null)
            isEllipsoid = true;
         javax.swing.JLabel indexLabel = new javax.swing.JLabel();
         javax.swing.JComboBox methodComboBox = new javax.swing.JComboBox();
         if (isEllipsoid == false)
            methodComboBox.setEnabled(false);
         javax.swing.JComboBox startComboBox = new javax.swing.JComboBox();
         javax.swing.JComboBox endComboBox = new javax.swing.JComboBox();
         indexLabel.setText(intPropFormat.format(i+1) + ". " + awo.getName());
         if (isEllipsoid == true) {
            methodComboBox.setModel(new javax.swing.DefaultComboBoxModel(wrapMethodNames));
            methodComboBox.setSelectedIndex(findElement(wrapMethodNames, smw.get(i).getMethodName()));
            methodComboBox.addActionListener(new java.awt.event.ActionListener() {
               public void actionPerformed(java.awt.event.ActionEvent evt) {
                  setWrapMethod(((javax.swing.JComboBox)evt.getSource()), num);
               }
            });
         }
         
         startComboBox.setModel(new javax.swing.DefaultComboBoxModel(startPointNames));
         int start = smw.get(i).getStartPoint();
         if (start < 0)
            startComboBox.setSelectedIndex(0);
         else
            startComboBox.setSelectedIndex(findElement(startPointNames, String.valueOf(start)));
         startComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               setWrapStartRange(((javax.swing.JComboBox)evt.getSource()), num);
            }
         });
         
         endComboBox.setModel(new javax.swing.DefaultComboBoxModel(endPointNames));
         int end = smw.get(i).getEndPoint();
         if (end < 0)
            endComboBox.setSelectedIndex(endComboBox.getItemCount()-1);
         else
            endComboBox.setSelectedIndex(findElement(endPointNames, String.valueOf(end)));
         endComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               setWrapEndRange(((javax.swing.JComboBox)evt.getSource()), num);
            }
         });
         
         indexLabel.setBounds(X - 20, Y + i * 22, 200, 21);
         methodComboBox.setBounds(X + 130, Y + i * 22, 80, 21);
         startComboBox.setBounds(X + 220, Y + i * 22, 50, 21);
         endComboBox.setBounds(X + 280, Y + i * 22, 50, 21);
         WrapPanel.add(indexLabel);
         WrapPanel.add(methodComboBox);
         WrapPanel.add(startComboBox);
         WrapPanel.add(endComboBox);
         
         javax.swing.JButton upButton = new javax.swing.JButton();
         upButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/icons/upArrow.png")));
         upButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/icons/upArrow_selected.png")));
         upButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/icons/upArrow_rollover.png")));
         upButton.setToolTipText("Move this wrap object up in the order");
         upButton.setBorder(null);
         upButton.setBorderPainted(false);
         upButton.setContentAreaFilled(false);
         upButton.setMargin(new java.awt.Insets(1, 1, 1, 1));
         upButton.setBounds(X - 75, Y + 3 + i * 22, 15, 15);
         if (i > 0)
            upButton.setEnabled(true);
         else
            upButton.setEnabled(false);
         upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               moveUpPathWrap(num);
            }
         });
         WrapPanel.add(upButton);
         
         javax.swing.JButton downButton = new javax.swing.JButton();
         downButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/icons/downArrow.png")));
         downButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/icons/downArrow_selected.png")));
         downButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/icons/downArrow_rollover.png")));
         downButton.setToolTipText("Move this wrap object down in the order");
         downButton.setBorder(null);
         downButton.setBorderPainted(false);
         downButton.setContentAreaFilled(false);
         downButton.setMargin(new java.awt.Insets(1, 1, 1, 1));
         downButton.setBounds(X - 58, Y + 3 + i * 22, 15, 15);
         if (smw.getSize() > 1 && i < smw.getSize() - 1)
            downButton.setEnabled(true);
         else
            downButton.setEnabled(false);
         downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               moveDownPathWrap(num);
            }
         });
         WrapPanel.add(downButton);
         
         //C:\\SimTK\\OpenSim\\Applications\\Gui\\opensim\\view\\src\\org\\opensim\\view\\editors\\
         javax.swing.JButton deleteButton = new javax.swing.JButton();
         deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/icons/delete.png")));
         deleteButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/icons/delete_selected.png")));
         deleteButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/icons/delete_rollover.png")));
         deleteButton.setToolTipText("Delete this wrap object from this muscle");
         deleteButton.setBorder(null);
         deleteButton.setBorderPainted(false);
         deleteButton.setContentAreaFilled(false);
         deleteButton.setBounds(X - 38, Y + 3 + i * 22, 15, 15);
         deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               deletePathWrap(num);
            }
         });
         WrapPanel.add(deleteButton);
      }
      
      // The add menu
      final javax.swing.JPopupMenu addMenu = new javax.swing.JPopupMenu();
      for (i = 0; i < numWrapObjects; i++) {
         javax.swing.JMenuItem menuItem = new JMenuItem(wrapObjectNames[i]);
         final int index = i;
         menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               addPathWrap(index);
            }
         });
         addMenu.add(menuItem);
      }
      
      // Add the "add" line
      javax.swing.JButton upButton = new javax.swing.JButton();
      upButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/icons/upArrow.png")));
      upButton.setBorder(null);
      upButton.setBorderPainted(false);
      upButton.setContentAreaFilled(false);
      upButton.setMargin(new java.awt.Insets(1, 1, 1, 1));
      upButton.setBounds(X - 75, Y + 3 + smw.getSize() * 22, 15, 15);
      upButton.setEnabled(false);
      WrapPanel.add(upButton);
      
      javax.swing.JButton downButton = new javax.swing.JButton();
      downButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/icons/downArrow.png")));
      downButton.setBorder(null);
      downButton.setBorderPainted(false);
      downButton.setContentAreaFilled(false);
      downButton.setMargin(new java.awt.Insets(1, 1, 1, 1));
      downButton.setBounds(X - 58, Y + 3 + smw.getSize() * 22, 15, 15);
      downButton.setEnabled(false);
      WrapPanel.add(downButton);
      
      javax.swing.JButton addButton = new javax.swing.JButton();
      addButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/icons/add.png")));
      addButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/icons/add_selected.png")));
      addButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/icons/add_rollover.png")));
      addButton.setBorder(null);
      addButton.setBorderPainted(false);
      addButton.setContentAreaFilled(false);
      addButton.setToolTipText("Add an existing wrap object to this muscle");
      addButton.setBounds(X - 38, Y + 3 + smw.getSize() * 22, 15, 15);
      WrapPanel.add(addButton);
      
      javax.swing.JLabel indexLabel = new javax.swing.JLabel();
      indexLabel.setText(intPropFormat.format(smw.getSize()+1) + ". existing wrap object");
      indexLabel.setEnabled(false);
      indexLabel.setBounds(X - 20, Y + smw.getSize() * 22, 200, 21);
      WrapPanel.add(indexLabel);
      
      javax.swing.JComboBox methodComboBox = new javax.swing.JComboBox();
      methodComboBox.setEnabled(false);
      methodComboBox.setBounds(X + 130, Y + smw.getSize() * 22, 80, 21);
      WrapPanel.add(methodComboBox);
      
      javax.swing.JComboBox startComboBox = new javax.swing.JComboBox();
      javax.swing.JComboBox endComboBox = new javax.swing.JComboBox();
      startComboBox.setEnabled(false);
      endComboBox.setEnabled(false);
      startComboBox.setBounds(X + 220, Y + smw.getSize() * 22, 50, 21);
      endComboBox.setBounds(X + 280, Y + smw.getSize() * 22, 50, 21);
      WrapPanel.add(startComboBox);
      WrapPanel.add(endComboBox);
      
      class PopupListener extends MouseAdapter {
         public void mousePressed(MouseEvent e) {
            //maybeShowPopup(e);
            addMenu.show(e.getComponent(),
                    e.getX(), e.getY());
         }
         
         public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
         }
         
         private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
               addMenu.show(e.getComponent(),
                       e.getX(), e.getY());
            }
         }
      }
      
      MouseListener popupListener = new PopupListener();
      addButton.addMouseListener(popupListener);
      
      Dimension d = new Dimension(350, Y + 20 + smw.getSize() * 22);
      WrapPanel.setPreferredSize(d);
   }
   public void addPathWrap(int menuChoice) {
       /* FIX40
      //Muscle asm = Muscle.safeDownCast(objectWithPath);
      WrapObject awo = currentPath.getModel().getSimbodyEngine().getWrapObject(wrapObjectNames[menuChoice]);
      OpenSimContext context =OpenSimDB.getInstance().getContext(currentPath.getModel());
      context.addPathWrap(currentPath, awo);
      
      setupComponent(objectWithPath);
               */
      updateDisplay();
   }
   
   public void moveUpPathWrap(int num) {
      //Muscle asm = Muscle.safeDownCast(objectWithPath);
      OpenSimContext context =OpenSimDB.getInstance().getContext(currentPath.getModel());
      context.moveUpPathWrap(currentPath, num);
      
      setupComponent(objectWithPath);
      updateDisplay();
   }
   
   public void moveDownPathWrap(int num) {
      //Muscle asm = Muscle.safeDownCast(objectWithPath);
      OpenSimContext context =OpenSimDB.getInstance().getContext(currentPath.getModel());
      context.moveDownPathWrap(currentPath, num);
      
      setupComponent(objectWithPath);
      updateDisplay();
   }

   private void updateDisplay() {
        Model model = currentPath.getModel();
        updatePathDisplay(model);
   }
   
   public void deletePathWrap(int num) {
      //Muscle asm = Muscle.safeDownCast(objectWithPath);
      OpenSimContext context =OpenSimDB.getInstance().getContext(currentPath.getModel());
      context.deletePathWrap(currentPath, num);
      
      setupComponent(objectWithPath);
      updateDisplay();
   }
  
   public void setWrapMethod(javax.swing.JComboBox wrapMethodComboBox, int num) {
      //Muscle asm = Muscle.safeDownCast(objectWithPath);
      PathWrap mw = currentPath.getWrapSet().get(num);
      int methodInt = wrapMethodComboBox.getSelectedIndex();
      //TODO: there must be a better way to relate selected index to WrapMethod enum
      if (methodInt == 0)
         mw.setMethod(PathWrap.WrapMethod.hybrid);
      else if (methodInt == 1)
         mw.setMethod(PathWrap.WrapMethod.midpoint);
      else if (methodInt == 2)
         mw.setMethod(PathWrap.WrapMethod.axial);
      
      setupComponent(objectWithPath);
      updateDisplay();

   }
   private int findElement(String[] nameList, String name) {
      int i;
      for (i = 0; i < nameList.length; i++)
         if (nameList[i].equals(name))
            return i;
      return -1;
   }
   public void setWrapStartRange(javax.swing.JComboBox wrapStartComboBox, int wrapNum) {
      //Muscle asm = Muscle.safeDownCast(objectWithPath);
      PathWrap mw = currentPath.getWrapSet().get(wrapNum);
      int oldStartPt = mw.getStartPoint();
      int newStartPt = wrapStartComboBox.getSelectedIndex();
      if (newStartPt < 1)
         newStartPt = -1;
      if (newStartPt != oldStartPt) {
         openSimContext.setStartPoint(mw, newStartPt);
         
         Model model = currentPath.getModel();
            updatePathDisplay(model);
         // update the current path panel
         updateCurrentPathPanel();
      }
   }
   
   public void setWrapEndRange(javax.swing.JComboBox wrapEndComboBox, int wrapNum) {
      //Muscle asm = Muscle.safeDownCast(objectWithPath);
      PathWrap mw = currentPath.getWrapSet().get(wrapNum);
      int oldEndPt = mw.getEndPoint();
      int newEndPt = wrapEndComboBox.getSelectedIndex();
      if (newEndPt == wrapEndComboBox.getItemCount()-1)
         newEndPt = -1;
      else
         newEndPt++;
      if (newEndPt != oldEndPt) {
         Model model = currentPath.getModel();
         OpenSimContext context = OpenSimDB.getInstance().getContext(model);
         context.setEndPoint(mw, newEndPt);
         
         updatePathDisplay(model);
         // update the current path panel
         updateCurrentPathPanel();
      }
   }
  private void updateCurrentPathPanel() {
      //CurrentPathTab.removeAll();
      javax.swing.JPanel CurrentPathPanel = new javax.swing.JPanel();
      CurrentPathPanel.setLayout(null);
      //CurrentPathPanel.setBackground(new java.awt.Color(200, 200, 255));
      //CurrentPathTab.setViewportView(CurrentPathPanel);

      // Put the points in the current path in the CurrentPath tab
      ArrayPathPoint asmp = currentPath.getCurrentPath(openSimContext.getCurrentStateRef());
      int X = 30;
      int Y = 40;
      
      // Set up the muscle-independent labels
      javax.swing.JLabel currentPathXLabel = new javax.swing.JLabel();
      currentPathXLabel.setText("X");
      currentPathXLabel.setBounds(X + 35, Y - 30, 6, 16);
      javax.swing.JLabel currentPathYLabel = new javax.swing.JLabel();
      currentPathYLabel.setText("Y");
      currentPathYLabel.setBounds(X + 95, Y - 30, 6, 16);
      javax.swing.JLabel currentPathZLabel = new javax.swing.JLabel();
      currentPathZLabel.setText("Z");
      currentPathZLabel.setBounds(X + 155, Y - 30, 6, 16);
      javax.swing.JLabel currentPathFrameLabel = new javax.swing.JLabel();
      currentPathFrameLabel.setText("Frame");
      currentPathFrameLabel.setBounds(X + 210, Y - 30, 30, 16);
      javax.swing.JLabel currentPathSelLabel = new javax.swing.JLabel();
      currentPathSelLabel.setText("Type");
      currentPathSelLabel.setBounds(X + 290, Y - 30, 30, 16);
      CurrentPathPanel.add(currentPathXLabel);
      CurrentPathPanel.add(currentPathYLabel);
      CurrentPathPanel.add(currentPathZLabel);
      CurrentPathPanel.add(currentPathFrameLabel);
      CurrentPathPanel.add(currentPathSelLabel);
      
      State state = openSimContext.getCurrentStateRef();
      for (int i = 0; i < asmp.getSize(); i++) {
         javax.swing.JLabel indexLabel = new javax.swing.JLabel();
         javax.swing.JLabel xField = new javax.swing.JLabel();
         xField.setHorizontalAlignment(SwingConstants.TRAILING);
         javax.swing.JLabel yField = new javax.swing.JLabel();
         yField.setHorizontalAlignment(SwingConstants.TRAILING);
         javax.swing.JLabel zField = new javax.swing.JLabel();
         zField.setHorizontalAlignment(SwingConstants.TRAILING);
         javax.swing.JLabel frameLabel = new javax.swing.JLabel();
         javax.swing.JLabel typeLabel = new javax.swing.JLabel();
         indexLabel.setText(intPropFormat.format(i+1) + ".");
         xField.setText(positionFormat.format(asmp.getitem(i).getLocation(state).get(0)));
         yField.setText(positionFormat.format(asmp.getitem(i).getLocation(state).get(1)));
         zField.setText(positionFormat.format(asmp.getitem(i).getLocation(state).get(2)));
         frameLabel.setText(asmp.getitem(i).getBodyName());
         if (asmp.getitem(i).getWrapObject() != null)
            typeLabel.setText("wrap" + " (" + asmp.getitem(i).getWrapObject().getName() + ")");
         else if (ConditionalPathPoint.safeDownCast(asmp.getitem(i)) != null)
            typeLabel.setText("via");
         else if (MovingPathPoint.safeDownCast(asmp.getitem(i)) != null)
            typeLabel.setText("moving");
         else
            typeLabel.setText("fixed");

         int height = Y + i * 22;
         int width = 60;
         indexLabel.setBounds(X - 20, height, 20, 21);
         xField.setBounds(X, height, width, 21);
         yField.setBounds(X + width + 1, height, width, 21);
         zField.setBounds(X + 2*width + 2, height, width, 21);
         frameLabel.setBounds(X + 3*width + 10, height, 90, 21);
         typeLabel.setBounds(X + 3*width + 110, height, 120, 21);
         CurrentPathPanel.add(indexLabel);
         CurrentPathPanel.add(xField);
         CurrentPathPanel.add(yField);
         CurrentPathPanel.add(zField);
         CurrentPathPanel.add(frameLabel);
         CurrentPathPanel.add(typeLabel);
      }
      Dimension d = new Dimension(400, Y + asmp.getSize() * 22);
      CurrentPathPanel.setPreferredSize(d);
   }
   
   public void AttachmentSelected(javax.swing.JCheckBox attachmentSelBox, int attachmentNum) {
      //Muscle asm = Muscle.safeDownCast(objectWithPath);
      PathPointSet pathPoints = currentPath.getPathPointSet();
      AbstractPathPoint point = pathPoints.get(attachmentNum);
      ViewDB.getInstance().toggleAddSelectedObject(point);
   }
   
   public void ViaCoordinateChosen(javax.swing.JComboBox coordComboBox, int attachmentNum) {
      //Muscle asm = Muscle.safeDownCast(objectWithPath);
      PathPointSet pathPoints = currentPath.getPathPointSet();
      ConditionalPathPoint via = ConditionalPathPoint.safeDownCast(pathPoints.get(attachmentNum));
      Coordinate oldCoord = via.getCoordinate();
      Model model = currentPath.getModel();
      CoordinateSet coords = model.getCoordinateSet();
      Coordinate newCoord = coords.get(coordComboBox.getSelectedIndex());
      OpenSimContext context=OpenSimDB.getInstance().getContext(model);
      if (Coordinate.getCPtr(newCoord) != Coordinate.getCPtr(oldCoord)) {
         context.setCoordinate(via, newCoord);
         // make sure the range min and range max are valid for this new coordinate
         double rangeMin = via.get_range(0);
         double rangeMax = via.get_range(1);
         boolean needsUpdating = false;
         if (rangeMin > newCoord.getRangeMax() || rangeMax < newCoord.getRangeMin()) {
            // If there is no overlap between the old range and the new range, use new range
            context.setRangeMin(via, newCoord.getRangeMin());
            context.setRangeMax(via, newCoord.getRangeMax());
            needsUpdating = true;
         } else {
            // It's OK if the range from the old coordinate is bigger than
            // the range of the new. Don't trim it down to size. JPL 08/08/07
            // Adjust the min and max to fit in the new range
            //if (rangeMin < newCoord.getRangeMin()) {
            //   via.setRangeMin(newCoord.getRangeMin());
            //   needsUpdating = true;
            //}
            //if (rangeMax > newCoord.getRangeMax()) {
            //   via.setRangeMax(newCoord.getRangeMax());
            //   needsUpdating = true;
            //}
         }
         
         if (needsUpdating) {
            updateAttachmentPanel();
         }
         ParametersTabbedPanel.setSelectedComponent(AttachmentsTab);
         // tell the ViewDB to redraw the model
         SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model); //vtk
         //FIXME vis.updateMuscleOrForceAlongPathGeometry(asm, true);
         ViewDB.getInstance().repaintAll();
         // update the current path panel
         updateCurrentPathPanel();
      }
   }

   public void MovingPathPointCoordinateChosen(javax.swing.JComboBox coordComboBox, int attachmentNum, int xyz) {
      //Muscle asm = Muscle.safeDownCast(objectWithPath);
      PathPointSet pathPoints = currentPath.getPathPointSet();
      MovingPathPoint mmp = MovingPathPoint.safeDownCast(pathPoints.get(attachmentNum));
      if (mmp == null || coordComboBox.getSelectedIndex() < 0)
         return;
      Coordinate oldCoord = null;
      if (xyz == 0)
         oldCoord = mmp.getXCoordinate();
      else if (xyz == 1)
         oldCoord = mmp.getYCoordinate();
      else if (xyz == 2)
         oldCoord = mmp.getZCoordinate();
      Model model = currentPath.getModel();
      OpenSimContext context=OpenSimDB.getInstance().getContext(model);
      CoordinateSet coords = model.getCoordinateSet();
      Coordinate newCoord = coords.get(coordComboBox.getSelectedIndex());
      if (Coordinate.getCPtr(newCoord) != Coordinate.getCPtr(oldCoord)) {
         if (xyz == 0)
            context.setXCoordinate(mmp, newCoord);
         else if (xyz == 1)
            context.setYCoordinate(mmp, newCoord);
         else if (xyz == 2)
            context.setZCoordinate(mmp, newCoord);
         
         ParametersTabbedPanel.setSelectedComponent(AttachmentsTab);
         // tell the ViewDB to redraw the model
         SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model); //vtk
         // FIXME vis.updateMuscleOrForceAlongPathGeometry(asm, true);
         ViewDB.getInstance().repaintAll();
         // update the current path panel
         updateCurrentPathPanel();
      }
   }

   public void PathPointTypeChosen(javax.swing.JComboBox musclePointTypeComboBox, int attachmentNum) {
      //Muscle asm = Muscle.safeDownCast(objectWithPath);
      AbstractPathPoint mp = currentPath.getPathPointSet().get(attachmentNum);
      ConditionalPathPoint via = ConditionalPathPoint.safeDownCast(mp);
      MovingPathPoint mmp = MovingPathPoint.safeDownCast(mp);

      int oldType = 0;
      if (via != null)
         oldType = 1;
      else if (mmp != null)
         oldType = 2;
      int newType = musclePointTypeComboBox.getSelectedIndex();
      
      OpenSimContext context=OpenSimDB.getInstance().getContext(currentPath.getModel());
      if (newType != oldType) {
         AbstractPathPoint newPoint = null;
         switch(newType){
             case 1:
                ConditionalPathPoint typedPoint = ConditionalPathPoint.safeDownCast(
                        OpenSimObject.newInstanceOfType(musclePointClassNames[newType]));
                typedPoint.setCoordinate(currentModel.getCoordinateSet().get(0));
                typedPoint.setLocation(mp.getLocation(context.getCurrentStateRef()));
                newPoint = typedPoint;
                break;
             case 2:
                MovingPathPoint mTtypedPoint = MovingPathPoint.safeDownCast(OpenSimObject.newInstanceOfType(musclePointClassNames[newType]));
                newPoint = mTtypedPoint;
                break;
             default:
                 newPoint = PathPoint.safeDownCast(OpenSimObject.newInstanceOfType(musclePointClassNames[newType]));
                 break;
         }
         newPoint.setParentFrame(mp.getParentFrame());
         context.realizeVelocity();
         boolean result = context.replacePathPoint(currentPath, mp, newPoint);
         if (result == false) {
            // Reset the combo box state without triggering an event
            musclePointTypeComboBox.setEnabled(false);
            musclePointTypeComboBox.setSelectedIndex(oldType);
            musclePointTypeComboBox.setEnabled(true);
            Object[] options = {"OK"};
            int answer = JOptionPane.showOptionDialog(this,
               "A muscle must contain at least 2 attachment points that are not via points.",
               "Muscle Editor",
               JOptionPane.OK_OPTION,
               JOptionPane.WARNING_MESSAGE,
               null,
               options,
               options[0]);
            PathPoint.deletePathPoint(newPoint);
            return;
         }
         
         ParametersTabbedPanel.setSelectedComponent(AttachmentsTab);
         // tell the ViewDB to redraw the model
         SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(currentModel);
         //FIXME ewDB.getInstance().repaintAll();
         // update the panels
         updateAttachmentPanel();
         updateCurrentPathPanel();
      }
   }
   public void AttachmentPointEntered(javax.swing.JTextField field, int attachmentNum, int coordNum) {
      //Muscle asm = Muscle.safeDownCast(objectWithPath);
      PathPointSet pathPoints = currentPath.getPathPointSet();
      State state = openSimContext.getCurrentStateRef();
      double newValue, oldValue = pathPoints.get(attachmentNum).getLocation(state).get(coordNum);
      try {
         newValue = positionFormat.parse(field.getText()).doubleValue();
      } catch (ParseException ex) {
         Toolkit.getDefaultToolkit().beep();
         field.setText(positionFormat.format(oldValue));
         return;
      }
      // format the number and write it back into the text field
      field.setText(positionFormat.format(newValue));
      // update the model if the number has changed
      if (oldValue != newValue) {
         Model model = currentPath.getModel();
         OpenSimContext context = OpenSimDB.getInstance().getContext(model);
         context.setLocation(PathPoint.safeDownCast(pathPoints.get(attachmentNum)), coordNum, newValue);
         
         updatePathDisplay(model);
         // update the current path panel
         updateCurrentPathPanel();
      }
   }
   
   public void AttachmentFrameChosen(javax.swing.JComboBox frameComboBox, int attachmentNum) {
      //Muscle asm = Muscle.safeDownCast(objectWithPath);
      
      PathPointSet pathPoints = currentPath.getPathPointSet();
      PhysicalFrame oldFrame = pathPoints.get(attachmentNum).getBody();
      Model model = currentPath.getModel();
      Component frameAsComponent = model.getComponent((String) frameComboBox.getSelectedItem());
      PhysicalFrame newFrame = PhysicalFrame.safeDownCast(frameAsComponent);
      if (PhysicalFrame.getCPtr(newFrame) != PhysicalFrame.getCPtr(oldFrame)) {
         OpenSimContext context=OpenSimDB.getInstance().getContext(model);
         context.setBody(pathPoints.get(attachmentNum), newFrame);

         updatePathDisplay(model);
         // update the panels
         updateAttachmentPanel();
         updateCurrentPathPanel();
      }
   }

   public void RangeMinEntered(javax.swing.JTextField field, int attachmentNum) {
      //Muscle asm = Muscle.safeDownCast(objectWithPath);
      PathPointSet pathPoints = currentPath.getPathPointSet();
      ConditionalPathPoint via = ConditionalPathPoint.safeDownCast(pathPoints.get(attachmentNum));

      // Conversions between radians and degrees
      double conversion = 180.0/Math.PI;
      NumberFormat nf = angleFormat;
      Coordinate coordinate = via.getCoordinate();
      if (coordinate != null && coordinate.getMotionType() == Coordinate.MotionType.Translational) {
         conversion = 1.0;
         nf = positionFormat;
      }

      double newValue, oldValue = via.get_range(0)*conversion;
      double biggestAllowed = via.get_range(1)*conversion;
      try {
         newValue = nf.parse(field.getText()).doubleValue();
      } catch (ParseException ex) {
         Toolkit.getDefaultToolkit().beep();
         newValue = Double.MAX_VALUE; // to force the field to update itself with the old value
      }

      if (newValue > biggestAllowed) {
         // user entered min that is greater than max, ignore it
         field.setText(nf.format(oldValue));
      } else {
         // format the number and write it back into the text field
         field.setText(nf.format(newValue));
         // update the model if the number has changed
         if (newValue != oldValue) {
            Model model = currentPath.getModel();
            OpenSimContext context = OpenSimDB.getInstance().getContext(model);
            context.setRangeMin(via, newValue/conversion);
            
            updatePathDisplay(model);
            // update the current path panel
            updateCurrentPathPanel();
         }
      }
   }
   
   public void RangeMaxEntered(javax.swing.JTextField field, int attachmentNum) {
      //Muscle asm = Muscle.safeDownCast(objectWithPath);
      PathPointSet pathPoints = currentPath.getPathPointSet();
      ConditionalPathPoint via = ConditionalPathPoint.safeDownCast(pathPoints.get(attachmentNum));

      // Conversions between radians and degrees
      double conversion = 180.0/Math.PI;
      NumberFormat nf = angleFormat;
      Coordinate coordinate = via.getCoordinate();
      if (coordinate != null && coordinate.getMotionType() == Coordinate.MotionType.Translational) {
         conversion = 1.0;
         nf = positionFormat;
      }

      double newValue, oldValue = via.get_range(1)*conversion;
      double smallestAllowed = via.get_range(0)*conversion;
      try {
         newValue = nf.parse(field.getText()).doubleValue();
      } catch (ParseException ex) {
         Toolkit.getDefaultToolkit().beep();
         newValue = -Double.MAX_VALUE; // to force the field to update itself with the old value
      }

      if (newValue < smallestAllowed) {
         // user entered max that is less than min, ignore it
         field.setText(nf.format(oldValue));
      } else {
         // format the number and write it back into the text field
         field.setText(nf.format(newValue));
         // update the model if the number has changed
         if (newValue != oldValue) {
            openSimContext.setRangeMax(via, newValue/conversion);
            
            // tell the ViewDB to redraw the model
            Model model = currentPath.getModel();
            updatePathDisplay(model);
            // update the current path panel
            updateCurrentPathPanel();
         }
      }
   }

    private void updatePathDisplay(Model model) {
        if (isVtkGraphicsAvailable()){
            SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model);
            vis.upateDisplay(Component.safeDownCast(objectWithPath));
            ViewDB.getInstance().repaintAll();
        }
        //ViewDB.getInstance().updatePathDisplay(model, currentPath);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel MuscleEditorPanel;
    private javax.swing.JScrollPane MuscleEditorScrollPane;
    private javax.swing.JTabbedPane ParametersTabbedPanel;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
    
    void RestoreButtonActionPerformed(ActionEvent evt)
    {
        OpenSimObject pathObject =  objectWithPath.getPropertyByName("GeometryPath").getValueAsObject();
        GeometryPath gp = GeometryPath.safeDownCast(pathObject);
        gp.assign(savePath);
        openSimContext.recreateSystemKeepStage();
        setupComponent(objectWithPath);
        //Muscle asm = Muscle.safeDownCast(objectWithPath);
        updatePathDisplay(currentModel);
   }
}
