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
package org.opensim.view.editors;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import org.openide.ErrorManager;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.ArrayPathPoint;
import org.opensim.modeling.ArrayPtrsPropertyGroup;
import org.opensim.modeling.Body;
import org.opensim.modeling.BodySet;
import org.opensim.modeling.ConditionalPathPoint;
import org.opensim.modeling.Constant;
import org.opensim.modeling.Coordinate;
import org.opensim.modeling.CoordinateSet;
import org.opensim.modeling.ForceSet;
import org.opensim.modeling.Function;
import org.opensim.modeling.Geometry;
import org.opensim.modeling.DecorativeGeometry.Representation;
import org.opensim.modeling.Model;
import org.opensim.modeling.MovingPathPoint;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PathActuator;
import org.opensim.modeling.PathPoint;
import org.opensim.modeling.PathPointSet;
import org.opensim.modeling.PathWrap;
import org.opensim.modeling.PhysicalFrame;
import org.opensim.modeling.PropertyGroup;
import org.opensim.modeling.PropertyHelper;
import org.opensim.modeling.SetPathWrap;
import org.opensim.modeling.SetWrapObject;
import org.opensim.modeling.SimbodyEngine;
import org.opensim.modeling.Units;
import org.opensim.modeling.WrapEllipsoid;
import org.opensim.modeling.WrapObject;
import org.opensim.view.ClearSelectedObjectsEvent;
import org.opensim.view.DragObjectsEvent;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.ModelEvent;
import org.opensim.view.ObjectSelectedEvent;
import org.opensim.view.ObjectSetCurrentEvent;
import org.opensim.view.ObjectsChangedEvent;
import org.opensim.view.ObjectsDeletedEvent;
import org.opensim.view.ObjectsRenamedEvent;
import org.opensim.view.OpenSimEvent;
import org.opensim.view.Selectable;
import org.opensim.view.SingleModelGuiElements;
import org.opensim.view.SingleModelVisuals;
import org.opensim.view.functionEditor.FunctionEditorTopComponent;
import org.opensim.view.functionEditor.FunctionEditorTopComponent.FunctionEditorOptions;
import org.opensim.view.nodes.OneMuscleNode;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

/**
 * Top component which displays the PathActuator Editor window.
 *
 * TODO: fix potential memory leak -- savedAct is a C++-side copy of the actuator.  This is necessary
 * for the case where we end up calling replaceActuator(currentAct, savedAct), but if we don't end up
 * doing this we end up leaking memory since this C++-side copy won't be used or deleted.
 */
final public class MuscleEditorTopComponent extends TopComponent implements Observer {
   
   private static MuscleEditorTopComponent instance;
   private Model currentModel = null;
   private PathActuator currentAct = null; // the actuator that is currently shown in the PathActuator Editor window
   private static final String[] wrapMethodNames = {"hybrid", "midpoint", "axial"};
   private static final String[] musclePointTypeNames = {"fixed", "via", "moving"};
   private static final String[] musclePointClassNames = {"PathPoint", "ConditionalPathPoint", "MovingPathPoint"};
   private javax.swing.JScrollPane AttachmentsTab = null;
   private javax.swing.JScrollPane WrapTab = null;
   private javax.swing.JScrollPane CurrentPathTab = null;
   private String selectedTabName = null;
   private javax.swing.JCheckBox attachmentSelectBox[] = null; // array of checkboxes for selecting attachment points
   private String[] wrapObjectNames = null;

   private NumberFormat doublePropFormat = NumberFormat.getInstance();
   private NumberFormat intPropFormat = NumberFormat.getIntegerInstance();
   private NumberFormat positionFormat = NumberFormat.getInstance();
   private NumberFormat angleFormat = NumberFormat.getInstance();
   private OpenSimContext openSimContext;
   /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
   
   private static final String PREFERRED_ID = "MuscleEditorTopComponent";
   
   private MuscleEditorTopComponent() {
      doublePropFormat.setMinimumFractionDigits(6);
      if (doublePropFormat instanceof DecimalFormat) {
        ((DecimalFormat) doublePropFormat).applyPattern("#,##0.#########");
      }
      positionFormat.setMinimumFractionDigits(5);
      angleFormat.setMinimumFractionDigits(2);

      initComponents();
      setName(NbBundle.getMessage(MuscleEditorTopComponent.class, "CTL_MuscleEditorTopComponent"));
      setToolTipText(NbBundle.getMessage(MuscleEditorTopComponent.class, "HINT_MuscleEditorTopComponent"));
//        setIcon(Utilities.loadImage(ICON_PATH, true));
      ViewDB.getInstance().addObserver(this);
      OpenSimDB.getInstance().addObserver(this);
      setupComponent(null);
   }

   
   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        MuscleEditorScrollPane = new javax.swing.JScrollPane();
        MuscleEditorPanel = new javax.swing.JPanel();
        BackupButton = new javax.swing.JButton();
        BackupAllButton = new javax.swing.JButton();
        RestoreButton = new javax.swing.JButton();
        RestoreAllButton = new javax.swing.JButton();
        MuscleNameTextField = new javax.swing.JTextField();
        ParametersTabbedPanel = new javax.swing.JTabbedPane();
        MuscleNameLabel = new javax.swing.JLabel();
        MuscleTypeLabel = new javax.swing.JLabel();
        ModelNameLabel = new javax.swing.JLabel();
        MuscleSelectLabel = new javax.swing.JLabel();
        MuscleComboBox = new javax.swing.JComboBox();

        MuscleEditorScrollPane.setBorder(null);
        MuscleEditorPanel.setMinimumSize(new java.awt.Dimension(5, 5));
        MuscleEditorPanel.setPreferredSize(new java.awt.Dimension(5, 5));
        org.openide.awt.Mnemonics.setLocalizedText(BackupButton, "Backup current");
        BackupButton.setToolTipText("Make a backup copy of the current actuator. Click \"Restore current\" or \"Restore all\" to return to these values.");
        BackupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackupButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(BackupAllButton, "Backup all");
        BackupAllButton.setToolTipText("Make backup copies of all actuators in the current model. Click \"Restore all\" or \"Restore current\" to return to these values.");
        BackupAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackupAllButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(RestoreButton, "Restore current");
        RestoreButton.setToolTipText("Restore the current actuator to the backup copy made when \"Backup\" or \"Backup all\" was pressed (or when the model was first loaded).");
        RestoreButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RestoreButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(RestoreAllButton, "Restore all");
        RestoreAllButton.setToolTipText("Restore all actuators to their backup copies made when \"Backup\" or \"Backup all\" was pressed (or when the model was first loaded).");
        RestoreAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RestoreAllButtonActionPerformed(evt);
            }
        });

        MuscleNameTextField.setText("<actuator name>");
        MuscleNameTextField.setToolTipText("The name of this actuator");
        MuscleNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MuscleNameTextFieldActionPerformed(evt);
            }
        });
        MuscleNameTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                MuscleNameTextFieldFocusLost(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(MuscleNameLabel, "Name");

        MuscleTypeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        org.openide.awt.Mnemonics.setLocalizedText(MuscleTypeLabel, "Type: <class>");
        MuscleTypeLabel.setToolTipText("The type of this actuator");

        org.openide.awt.Mnemonics.setLocalizedText(ModelNameLabel, "Model: <name> ");
        ModelNameLabel.setToolTipText("The name of the current model");

        org.openide.awt.Mnemonics.setLocalizedText(MuscleSelectLabel, "Muscle");

        MuscleComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<a muscle>", "Item 2", "Item 3", "Item 4" }));
        MuscleComboBox.setToolTipText("Select the actuator to edit");
        MuscleComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MuscleComboBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout MuscleEditorPanelLayout = new org.jdesktop.layout.GroupLayout(MuscleEditorPanel);
        MuscleEditorPanel.setLayout(MuscleEditorPanelLayout);
        MuscleEditorPanelLayout.setHorizontalGroup(
            MuscleEditorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(MuscleEditorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(MuscleEditorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(MuscleEditorPanelLayout.createSequentialGroup()
                        .add(MuscleEditorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(ParametersTabbedPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
                            .add(ModelNameLabel)
                            .add(MuscleEditorPanelLayout.createSequentialGroup()
                                .add(MuscleSelectLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(MuscleComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 147, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(14, 14, 14)
                                .add(MuscleNameLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(MuscleNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 147, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(15, 15, 15)
                                .add(MuscleTypeLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 157, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .add(MuscleEditorPanelLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(RestoreButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(RestoreAllButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 111, Short.MAX_VALUE)
                        .add(BackupButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(BackupAllButton)
                        .add(27, 27, 27))))
        );
        MuscleEditorPanelLayout.setVerticalGroup(
            MuscleEditorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(MuscleEditorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(ModelNameLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(MuscleEditorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(MuscleSelectLabel)
                    .add(MuscleComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(MuscleNameLabel)
                    .add(MuscleNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(MuscleTypeLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ParametersTabbedPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(MuscleEditorPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(RestoreButton)
                    .add(RestoreAllButton)
                    .add(BackupAllButton)
                    .add(BackupButton))
                .addContainerGap())
        );
        MuscleEditorScrollPane.setViewportView(MuscleEditorPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(MuscleEditorScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(MuscleEditorScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BackupAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackupAllButtonActionPerformed
       backupActuators();
    }//GEN-LAST:event_BackupAllButtonActionPerformed

    private void RestoreAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RestoreAllButtonActionPerformed
       //restoreActuators();
    }//GEN-LAST:event_RestoreAllButtonActionPerformed

   private void MuscleComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MuscleComboBoxActionPerformed
      if (currentModel == null)
         return;
      Object selected = MuscleComboBox.getSelectedItem();
      if (selected == null) return; // Nothing is selected
      String nameOfNewAct = MuscleComboBox.getSelectedItem().toString();
      PathActuator newAct = PathActuator.safeDownCast(currentModel.getForceSet().get(nameOfNewAct));
      if (newAct != null && PathActuator.getCPtr(newAct) != PathActuator.getCPtr(currentAct)) {
         currentAct = newAct;
         //updateBackupRestoreButtons();
         setupComponent(currentAct);
      }
   }//GEN-LAST:event_MuscleComboBoxActionPerformed
   
   private boolean validName(String actName)
   {
      if (currentModel != null && currentAct != null) {
         if (actName.length() < 1) {
            MuscleNameTextField.setText(currentAct.getName());
            return false;
         }

         PathActuator existingAct = null;
         if (currentModel.getForceSet().contains(actName))
             existingAct = PathActuator.safeDownCast(currentModel.getForceSet().get(actName));
         if (existingAct != null && PathActuator.getCPtr(existingAct) != PathActuator.getCPtr(currentAct)) {
            MuscleNameTextField.setText(currentAct.getName());
            Object[] options = {"OK"};
            String message = "The name \"" + actName + "\" is already being used. Please choose a different actuator name";
            int answer = JOptionPane.showOptionDialog(this,
                         message,
                         "PathActuator Editor",
                         JOptionPane.OK_OPTION,
                         JOptionPane.WARNING_MESSAGE,
                         null,
                         options,
                         options[0]);
            return false;
         } else {
            return true;
         }
      }

      return false;
   }

   private void MuscleNameTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_MuscleNameTextFieldFocusLost
      if (!evt.isTemporary())
          MuscleNameEntered();
   }//GEN-LAST:event_MuscleNameTextFieldFocusLost

   private void MuscleNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MuscleNameTextFieldActionPerformed
      MuscleNameEntered();
   }//GEN-LAST:event_MuscleNameTextFieldActionPerformed

   private void MuscleNameEntered() {
      if (currentAct == null || currentAct.getName().equals(MuscleNameTextField.getText()) == true)
         return;
      if (validName(MuscleNameTextField.getText()) == false)
         return;
      currentAct.setName(MuscleNameTextField.getText());
      OpenSimDB.getInstance().getModelGuiElements(currentModel).updateActuatorNames();
      // Update the muscle list in the ViewDB and then generate an event
      // so other tools can update accordingly.
      Vector<OpenSimObject> objs = new Vector<OpenSimObject>(1);
      objs.add(currentAct);
      ObjectsRenamedEvent evnt = new ObjectsRenamedEvent(this, currentModel, objs);
      OpenSimDB.getInstance().setChanged();
      OpenSimDB.getInstance().notifyObservers(evnt);

      // If the actuator is an PathActuator, then when it is renamed
      // all of its attachment points will be renamed as well. You need
      // to generate events for all of these name changes, because
      // attachment point names can be displayed in the function editor,
      // text bar (during selection), etc.
      PathActuator asm = PathActuator.safeDownCast(currentAct);
      if (asm != null) {
         // Set the name again, this time using PathActuator's custom method
         asm.setName(MuscleNameTextField.getText());
         PathPointSet pathPoints = asm.getGeometryPath().getPathPointSet();
         Vector<OpenSimObject> mpobjs = new Vector<OpenSimObject>(pathPoints.getSize());
         for (int i=0; i<pathPoints.getSize(); i++) {
            mpobjs.add(pathPoints.get(i));
         }
         ObjectsRenamedEvent ev = new ObjectsRenamedEvent(this, currentModel, mpobjs);
         OpenSimDB.getInstance().setChanged();
         OpenSimDB.getInstance().notifyObservers(ev);
      }
   }

   // Called from update(), this function handles name changes to any actuator in
   // the current model. It assumes that the actuator's name has already been
   // changed, so only the muscle editor needs to be updated.
   private void updateActuatorName(PathActuator act) {
      SingleModelGuiElements guiElem = OpenSimDB.getInstance().getModelGuiElements(currentModel);
      String [] actNames = guiElem.getActuatorNames();
      MuscleComboBox.setModel(new javax.swing.DefaultComboBoxModel(actNames));
      if (currentAct != null) {
         MuscleComboBox.setSelectedIndex(findElement(actNames, currentAct.getName()));
         if (currentAct.equals(act))
            MuscleNameTextField.setText(currentAct.getName());
      }
      //setPendingChanges(true, act, true);
   }

   private void RestoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RestoreButtonActionPerformed
      //restoreActuator();
   }//GEN-LAST:event_RestoreButtonActionPerformed
   
   private void BackupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackupButtonActionPerformed
      //backupActuator();
   }//GEN-LAST:event_BackupButtonActionPerformed
   

   /* This is called when the user clicks on the "Backup All" button.
    * It backs up only the actuators that have been modified since they
    * were last backed up.
    */
   private void backupActuators() {
      // Should never be null, but just in case...

   }

   /* This is called when the current model is closed or switched. It deletes
    * all backed up actuators, and then if there is a current model, it makes a
    * new set of backups for that model.
    */
   private void backupAllActuators() {
   }

   /** restoreActuator
    *  Because the type of the actuator may have changed, you have to remove the current
    *  one (act) and replace it with the saved one (savedAct).
    */
   private void restoreActuatorReplace() {
    }


   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JButton BackupAllButton;
   private javax.swing.JButton BackupButton;
   private javax.swing.JLabel ModelNameLabel;
   private javax.swing.JComboBox MuscleComboBox;
   private javax.swing.JPanel MuscleEditorPanel;
   private javax.swing.JScrollPane MuscleEditorScrollPane;
   private javax.swing.JLabel MuscleNameLabel;
   private javax.swing.JTextField MuscleNameTextField;
   private javax.swing.JLabel MuscleSelectLabel;
   private javax.swing.JLabel MuscleTypeLabel;
   private javax.swing.JTabbedPane ParametersTabbedPanel;
   private javax.swing.JButton RestoreAllButton;
   private javax.swing.JButton RestoreButton;
   // End of variables declaration//GEN-END:variables
   
   /**
    * Gets default instance. Do not use directly: reserved for *.settings files only,
    * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
    * To obtain the singleton instance, use {@link findInstance}.
    */
   public static synchronized MuscleEditorTopComponent getDefault() {
      if (instance == null) {
         instance = new MuscleEditorTopComponent();
      }
      return instance;
   }
   
   /**
    * Obtain the MuscleEditorTopComponent instance. Never call {@link #getDefault} directly!
    */
   public static synchronized MuscleEditorTopComponent findInstance() {
      TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
      if (win == null) {
         ErrorManager.getDefault().log(ErrorManager.WARNING, "Cannot find MuscleEditor component. It will not be located properly in the window system.");
         return getDefault();
      }
      if (win instanceof MuscleEditorTopComponent) {
         return (MuscleEditorTopComponent)win;
      }
      ErrorManager.getDefault().log(ErrorManager.WARNING, "There seem to be multiple components with the '" + PREFERRED_ID + "' ID. That is a potential source of errors and unexpected behavior.");
      return getDefault();
   }
   
   public int getPersistenceType() {
      return TopComponent.PERSISTENCE_ALWAYS;
   }
   
   public void AttachmentPointEntered(javax.swing.JTextField field, int attachmentNum, int coordNum) {
      PathActuator asm = PathActuator.safeDownCast(currentAct);
      PathPointSet pathPoints = asm.getGeometryPath().getPathPointSet();
      double newValue, oldValue = pathPoints.get(attachmentNum).getLocationCoord(coordNum);
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
         Model model = asm.getModel();
         OpenSimContext context = OpenSimDB.getInstance().getContext(model);
         context.setLocation(pathPoints.get(attachmentNum), coordNum, newValue);
         //setPendingChanges(true, currentAct, true);
         // tell the ViewDB to redraw the model
         SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model);
         vis.updateActuatorGeometry(asm, true);
         ViewDB.getInstance().repaintAll();
         // update the current path panel
         updateCurrentPathPanel(asm);
      }
   }
   
   public void AttachmentBodyChosen(javax.swing.JComboBox bodyComboBox, int attachmentNum) {
      PathActuator asm = PathActuator.safeDownCast(currentAct);
      PathPointSet pathPoints = asm.getGeometryPath().getPathPointSet();
      PhysicalFrame oldBody = pathPoints.get(attachmentNum).getBody();
      Model model = asm.getModel();
      BodySet bodies = model.getBodySet();
      Body newBody = bodies.get(bodyComboBox.getSelectedIndex());
      if (Body.getCPtr(newBody) != Body.getCPtr(oldBody)) {
         OpenSimContext context=OpenSimDB.getInstance().getContext(model);
         context.setBody(pathPoints.get(attachmentNum), newBody);
         //setPendingChanges(true, currentAct, true);
         // tell the ViewDB to redraw the model
         SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model);
         vis.updateActuatorGeometry(asm, true);
         ViewDB.getInstance().repaintAll();
         // update the panels
         //updateAttachmentPanel(asm);
         updateCurrentPathPanel(asm);
      }
   }
   
   public void AttachmentSelected(javax.swing.JCheckBox attachmentSelBox, int attachmentNum) {
      PathActuator asm = PathActuator.safeDownCast(currentAct);
      PathPointSet pathPoints = asm.getGeometryPath().getPathPointSet();
      PathPoint point = pathPoints.get(attachmentNum);
      ViewDB.getInstance().toggleAddSelectedObject(point);
   }
   
   public void ViaCoordinateChosen(javax.swing.JComboBox coordComboBox, int attachmentNum) {
      PathActuator asm = PathActuator.safeDownCast(currentAct);
      PathPointSet pathPoints = asm.getGeometryPath().getPathPointSet();
      ConditionalPathPoint via = ConditionalPathPoint.safeDownCast(pathPoints.get(attachmentNum));
      Coordinate oldCoord = via.getCoordinate();
      Model model = asm.getModel();
      CoordinateSet coords = model.getCoordinateSet();
      Coordinate newCoord = coords.get(coordComboBox.getSelectedIndex());
      OpenSimContext context=OpenSimDB.getInstance().getContext(model);
      if (Coordinate.getCPtr(newCoord) != Coordinate.getCPtr(oldCoord)) {
         context.setCoordinate(via, newCoord);
         // make sure the range min and range max are valid for this new coordinate
         double rangeMin = via.getRange().getitem(0);
         double rangeMax = via.getRange().getitem(1);
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
         //setPendingChanges(true, currentAct, true);
         if (needsUpdating) {
            //updateAttachmentPanel(asm);
         }
         ParametersTabbedPanel.setSelectedComponent(AttachmentsTab);
         // tell the ViewDB to redraw the model
         SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model);
         vis.updateActuatorGeometry(asm, true);
         ViewDB.getInstance().repaintAll();
         // update the current path panel
         updateCurrentPathPanel(asm);
      }
   }

   public void MovingPathPointCoordinateChosen(javax.swing.JComboBox coordComboBox, int attachmentNum, int xyz) {
      PathActuator asm = PathActuator.safeDownCast(currentAct);
      PathPointSet pathPoints = asm.getGeometryPath().getPathPointSet();
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
      Model model = asm.getModel();
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
         //setPendingChanges(true, currentAct, true);
         ParametersTabbedPanel.setSelectedComponent(AttachmentsTab);
         // tell the ViewDB to redraw the model
         SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model);
         vis.updateActuatorGeometry(asm, true);
         ViewDB.getInstance().repaintAll();
         // update the current path panel
         updateCurrentPathPanel(asm);
      }
   }

   public void PathPointTypeChosen(javax.swing.JComboBox musclePointTypeComboBox, int attachmentNum) {
      PathActuator asm = PathActuator.safeDownCast(currentAct);
      PathPoint mp = asm.getGeometryPath().getPathPointSet().get(attachmentNum);
      ConditionalPathPoint via = ConditionalPathPoint.safeDownCast(mp);
      MovingPathPoint mmp = MovingPathPoint.safeDownCast(mp);

      int oldType = 0;
      if (via != null)
         oldType = 1;
      else if (mmp != null)
         oldType = 2;
      int newType = musclePointTypeComboBox.getSelectedIndex();
      if (newType != oldType) {
         PathPoint newPoint = PathPoint.makePathPointOfType(mp, musclePointClassNames[newType]);
         OpenSimContext context=OpenSimDB.getInstance().getContext(asm.getModel());
         boolean result = context.replacePathPoint(asm.getGeometryPath(), mp, newPoint);
         if (result == false) {
            // Reset the combo box state without triggering an event
            musclePointTypeComboBox.setEnabled(false);
            musclePointTypeComboBox.setSelectedIndex(oldType);
            musclePointTypeComboBox.setEnabled(true);
            Object[] options = {"OK"};
            int answer = JOptionPane.showOptionDialog(this,
               "A muscle must contain at least 2 attachment points that are not via points.",
               "PathActuator Editor",
               JOptionPane.OK_OPTION,
               JOptionPane.WARNING_MESSAGE,
               null,
               options,
               options[0]);
            PathPoint.deletePathPoint(newPoint);
            return;
         }
         //setPendingChanges(true, currentAct, true);
         ParametersTabbedPanel.setSelectedComponent(AttachmentsTab);
         // tell the ViewDB to redraw the model
         SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(currentModel);
         vis.updateActuatorGeometry(asm, true);
         ViewDB.getInstance().repaintAll();
         // update the panels
         //updateAttachmentPanel(asm);
         updateCurrentPathPanel(asm);
      }
   }

   public void RangeMinEntered(javax.swing.JTextField field, int attachmentNum) {
      PathActuator asm = PathActuator.safeDownCast(currentAct);
      PathPointSet pathPoints = asm.getGeometryPath().getPathPointSet();
      ConditionalPathPoint via = ConditionalPathPoint.safeDownCast(pathPoints.get(attachmentNum));

      // Conversions between radians and degrees
      double conversion = 180.0/Math.PI;
      NumberFormat nf = angleFormat;
      Coordinate coordinate = via.getCoordinate();
      if (coordinate != null && coordinate.getMotionType() == Coordinate.MotionType.Translational) {
         conversion = 1.0;
         nf = positionFormat;
      }

      double newValue, oldValue = via.getRange().getitem(0)*conversion;
      double biggestAllowed = via.getRange().getitem(1)*conversion;
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
            Model model = asm.getModel();
            OpenSimContext context = OpenSimDB.getInstance().getContext(model);
            context.setRangeMin(via, newValue/conversion);
            //setPendingChanges(true, currentAct, true);
            // tell the ViewDB to redraw the model
            SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model);
            vis.updateActuatorGeometry(asm, true);
            ViewDB.getInstance().repaintAll();
            // update the current path panel
            updateCurrentPathPanel(asm);
         }
      }
   }
   
   public void RangeMaxEntered(javax.swing.JTextField field, int attachmentNum) {
      PathActuator asm = PathActuator.safeDownCast(currentAct);
      PathPointSet pathPoints = asm.getGeometryPath().getPathPointSet();
      ConditionalPathPoint via = ConditionalPathPoint.safeDownCast(pathPoints.get(attachmentNum));

      // Conversions between radians and degrees
      double conversion = 180.0/Math.PI;
      NumberFormat nf = angleFormat;
      Coordinate coordinate = via.getCoordinate();
      if (coordinate != null && coordinate.getMotionType() == Coordinate.MotionType.Translational) {
         conversion = 1.0;
         nf = positionFormat;
      }

      double newValue, oldValue = via.getRange().getitem(1)*conversion;
      double smallestAllowed = via.getRange().getitem(0)*conversion;
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
            //setPendingChanges(true, currentAct, true);
            // tell the ViewDB to redraw the model
            Model model = asm.getModel();
            SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model);
            vis.updateActuatorGeometry(asm, true);
            ViewDB.getInstance().repaintAll();
            // update the current path panel
            updateCurrentPathPanel(asm);
         }
      }
   }
   
   public void setWrapStartRange(javax.swing.JComboBox wrapStartComboBox, int wrapNum) {
      PathActuator asm = PathActuator.safeDownCast(currentAct);
      PathWrap mw = asm.getGeometryPath().getWrapSet().get(wrapNum);
      int oldStartPt = mw.getStartPoint();
      int newStartPt = wrapStartComboBox.getSelectedIndex();
      if (newStartPt < 1)
         newStartPt = -1;
      if (newStartPt != oldStartPt) {
         openSimContext.setStartPoint(mw, newStartPt);
         //setPendingChanges(true, currentAct, true);
         Model model = asm.getModel();
         // tell the ViewDB to redraw the model
         SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model);
         vis.updateActuatorGeometry(asm, true);
         ViewDB.getInstance().repaintAll();
         // update the current path panel
         updateCurrentPathPanel(asm);
      }
   }
   
   public void setWrapEndRange(javax.swing.JComboBox wrapEndComboBox, int wrapNum) {
      PathActuator asm = PathActuator.safeDownCast(currentAct);
      PathWrap mw = asm.getGeometryPath().getWrapSet().get(wrapNum);
      int oldEndPt = mw.getEndPoint();
      int newEndPt = wrapEndComboBox.getSelectedIndex();
      if (newEndPt == wrapEndComboBox.getItemCount()-1)
         newEndPt = -1;
      else
         newEndPt++;
      if (newEndPt != oldEndPt) {
         Model model = asm.getModel();
         OpenSimContext context = OpenSimDB.getInstance().getContext(model);
         context.setEndPoint(mw, newEndPt);
         //setPendingChanges(true, currentAct, true);
         // tell the ViewDB to redraw the model
         SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model);
         vis.updateActuatorGeometry(asm, true);
         ViewDB.getInstance().repaintAll();
         // update the current path panel
         updateCurrentPathPanel(asm);
      }
   }
   
   public void addPathWrap(int menuChoice) {
      PathActuator asm = PathActuator.safeDownCast(currentAct);
      WrapObject awo = null;//asm.getModel().getSimbodyEngine().getWrapObject(wrapObjectNames[menuChoice]);
      OpenSimContext context =OpenSimDB.getInstance().getContext(asm.getModel());
      context.addPathWrap(asm.getGeometryPath(), awo);
      //setPendingChanges(true, currentAct, true);
      setupComponent(currentAct);
      Model model = asm.getModel();
      SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model);
      vis.updateActuatorGeometry(asm, true);
      ViewDB.getInstance().repaintAll();
   }
   
   public void moveUpPathWrap(int num) {
      PathActuator asm = PathActuator.safeDownCast(currentAct);
      OpenSimContext context =OpenSimDB.getInstance().getContext(asm.getModel());
      context.moveUpPathWrap(asm.getGeometryPath(), num);
      //setPendingChanges(true, currentAct, true);
      setupComponent(currentAct);
      Model model = asm.getModel();
      SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model);
      vis.updateActuatorGeometry(asm, true);
      ViewDB.getInstance().repaintAll();
   }
   
   public void moveDownPathWrap(int num) {
      PathActuator asm = PathActuator.safeDownCast(currentAct);
      OpenSimContext context =OpenSimDB.getInstance().getContext(asm.getModel());
      context.moveDownPathWrap(asm.getGeometryPath(), num);
      //setPendingChanges(true, currentAct, true);
      setupComponent(currentAct);
      Model model = asm.getModel();
      SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model);
      vis.updateActuatorGeometry(asm, true);
      ViewDB.getInstance().repaintAll();
   }
   
   public void deletePathWrap(int num) {
      PathActuator asm = PathActuator.safeDownCast(currentAct);
      OpenSimContext context =OpenSimDB.getInstance().getContext(asm.getModel());
      context.deletePathWrap(asm.getGeometryPath(), num);
      //setPendingChanges(true, currentAct, true);
      setupComponent(currentAct);
      Model model = asm.getModel();
      SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model);
      vis.updateActuatorGeometry(asm, true);
      ViewDB.getInstance().repaintAll();
   }
   
   public void setWrapMethod(javax.swing.JComboBox wrapMethodComboBox, int num) {
      PathActuator asm = PathActuator.safeDownCast(currentAct);
      PathWrap mw = asm.getGeometryPath().getWrapSet().get(num);
      int methodInt = wrapMethodComboBox.getSelectedIndex();
      //TODO: there must be a better way to relate selected index to WrapMethod enum
      if (methodInt == 0)
         mw.setMethod(PathWrap.WrapMethod.hybrid);
      else if (methodInt == 1)
         mw.setMethod(PathWrap.WrapMethod.midpoint);
      else if (methodInt == 2)
         mw.setMethod(PathWrap.WrapMethod.axial);
      //setPendingChanges(true, currentAct, true);
      setupComponent(currentAct);
      Model model = asm.getModel();
      SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model);
      vis.updateActuatorGeometry(asm, true);
      ViewDB.getInstance().repaintAll();
   }
      
   public void DoublePropertyEntered(javax.swing.JTextField field, int propertyNum) {
      AbstractProperty prop = currentAct.getPropertyByIndex(propertyNum);

      if (prop != null) {
         double newValue, oldValue = PropertyHelper.getValueDouble(prop);
         try {
            newValue = doublePropFormat.parse(field.getText()).doubleValue();
         } catch (ParseException ex) {
            Toolkit.getDefaultToolkit().beep();
            field.setText(doublePropFormat.format(oldValue));
            return;
         }
         // format the number and write it back into the text field
         field.setText(doublePropFormat.format(newValue));

         if (newValue != oldValue) {
            PropertyHelper.setValueDouble(newValue, prop);
            ////setPendingChanges(true, currentAct, true);
            // TODO generate an event for this??
         }
      }
   }

   public void IntPropertyEntered(javax.swing.JTextField field, int propertyNum) {
     AbstractProperty prop = currentAct.getPropertyByIndex(propertyNum);
     
      if (prop != null) {
         int newValue, oldValue = PropertyHelper.getValueInt(prop);
         try {
            newValue = intPropFormat.parse(field.getText()).intValue();
         } catch (ParseException ex) {
            Toolkit.getDefaultToolkit().beep();
            field.setText(intPropFormat.format(oldValue));
            return;
         }
         // write the value back into the text field (for consistent formatting)
         field.setText(intPropFormat.format(newValue));

         if (newValue != oldValue) {
            PropertyHelper.setValueInt(newValue, prop);
            //setPendingChanges(true, currentAct, true);
            // TODO generate an event for this??
         }
      }
   }

   public void EditPropertyFunction(javax.swing.JButton button, int propertyNum) {
      AbstractProperty prop = currentAct.getPropertyByIndex(propertyNum);
 
      if (prop != null) {
         OpenSimObject obj = prop.getValueAsObject();
         Function func = Function.safeDownCast(obj);
         FunctionEditorTopComponent win = FunctionEditorTopComponent.findInstance();
         win.addChangeListener(new MusclePropertyFunctionEventListener());
         FunctionEditorOptions options = new FunctionEditorOptions();
         options.title = prop.getName();
         options.XUnits = new Units(Units.UnitType.Meters);
         options.XDisplayUnits = options.XUnits;
         options.YUnits = new Units(Units.UnitType.Newtons);
         options.YDisplayUnits = options.YUnits;
         options.XLabel = "norm length";
         options.YLabel = "norm force";
         win.open(currentModel, currentAct, null, func, options);
      }
   }

   public void EditPathPointFunction(javax.swing.JButton button, int attachmentNum, int xyz) {
      PathActuator asm = PathActuator.safeDownCast(currentAct);
      if (asm != null) {
         MovingPathPoint mmp = MovingPathPoint.safeDownCast(asm.getGeometryPath().getPathPointSet().get(attachmentNum));
         if (mmp != null) {
            Function function = null;
            Coordinate coordinate = null;
            FunctionEditorOptions options = new FunctionEditorOptions();
            if (xyz == 0) {
               function = mmp.getXFunction();
               coordinate = mmp.getXCoordinate();
               options.title = "X offset";
            } else if (xyz == 1) {
               function = mmp.getYFunction();
               coordinate = mmp.getYCoordinate();
               options.title = "Y offset";
            } else if (xyz == 2) {
               function = mmp.getZFunction();
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
            objects.add(currentAct);
            win.open(currentModel, mmp, objects, function, options);
         }
      }
   }

   public void addAttachmentPerformed(int menuChoice) {
      PathActuator asm = PathActuator.safeDownCast(currentAct);
      PathPointSet pathPoints = asm.getGeometryPath().getPathPointSet();
      int index = menuChoice;
      if (index > pathPoints.getSize() - 1)
         index = pathPoints.getSize() - 1;

      // ideally we'd like to just deselect the point we're deleting but the muscle displayer doesn't
      // deal well with maintaining the right glyph colors when the attachment set changes.
      // TODO: send some event that the muscle displayer can listen for and know to deselect the point
      // and make sure the rest of the points maintain correct selection status
      ViewDB.getInstance().removeObjectsBelongingToMuscleFromSelection(PathActuator.safeDownCast(currentAct));

      PathPoint closestPoint = pathPoints.get(index);
      OpenSimContext context =OpenSimDB.getInstance().getContext(asm.getModel());
      context.addPathPoint(asm.getGeometryPath(), menuChoice, closestPoint.getBody());
      //setPendingChanges(true, currentAct, false);
      setupComponent(currentAct);
      Model model = asm.getModel();
      SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model);
      vis.updateActuatorGeometry(asm, true);
      ViewDB.getInstance().repaintAll();
   }

   public void deleteAttachmentPerformed(int menuChoice) {
      PathActuator asm = PathActuator.safeDownCast(currentAct);
      // The point may not be deleted, but save a reference to it so that if it is deleted
      // you can fire an ObjectsDeletedEvent later.
      PathPoint mp = asm.getGeometryPath().getPathPointSet().get(menuChoice);
      ViewDB.getInstance().removeObjectsBelongingToMuscleFromSelection(PathActuator.safeDownCast(currentAct));
      OpenSimContext context =OpenSimDB.getInstance().getContext(asm.getModel());
      
      boolean result = context.deletePathPoint(asm.getGeometryPath(), menuChoice);
      if (result == false) {
         Object[] options = {"OK"};
         int answer = JOptionPane.showOptionDialog(this,
                 "A muscle must contain at least 2 attachment points that are not via points.",
                 "PathActuator Editor",
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
         ViewDB.getInstance().removeObjectsBelongingToMuscleFromSelection(PathActuator.safeDownCast(currentAct));
         //setPendingChanges(true, currentAct, false);
         setupComponent(currentAct);
         Model model = asm.getModel();
         // Fire an ObjectsDeletedEvent.
         Vector<OpenSimObject> objs = new Vector<OpenSimObject>(1);
         objs.add(mp);
         ObjectsDeletedEvent evnt = new ObjectsDeletedEvent(this, model, objs);
         OpenSimDB.getInstance().setChanged();
         OpenSimDB.getInstance().notifyObservers(evnt);
         // Update the display.
         SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model);
         vis.updateActuatorGeometry(asm, true);
         ViewDB.getInstance().repaintAll();
      }
   }
   
   public void componentOpened() {
   }
   
   public void setupCurrentPathPanel(PathActuator asm) {
      if (CurrentPathTab != null)
         ParametersTabbedPanel.remove(CurrentPathTab);
      CurrentPathTab = new javax.swing.JScrollPane();
      CurrentPathTab.setName("Current Path");
      ParametersTabbedPanel.insertTab("Current Path", null, CurrentPathTab, "Current path of the muscle", ParametersTabbedPanel.getTabCount());

      updateCurrentPathPanel(asm);
   }

   private void updateCurrentPathPanel(PathActuator asm) {
      //CurrentPathTab.removeAll();
      javax.swing.JPanel CurrentPathPanel = new javax.swing.JPanel();
      CurrentPathPanel.setLayout(null);
      //CurrentPathPanel.setBackground(new java.awt.Color(200, 200, 255));
      CurrentPathTab.setViewportView(CurrentPathPanel);

      // Put the points in the current path in the CurrentPath tab
      ArrayPathPoint asmp = null;//openSimContext.getCurrentPath(asm);
      int X = 30;
      int Y = 40;
      
      // Set up the muscle-independent labels
      javax.swing.JLabel currentPathXLabel = new javax.swing.JLabel();
      currentPathXLabel.setText("X");
      currentPathXLabel.setBounds(X + 35, Y - 30, 8, 16);
      javax.swing.JLabel currentPathYLabel = new javax.swing.JLabel();
      currentPathYLabel.setText("Y");
      currentPathYLabel.setBounds(X + 95, Y - 30, 8, 16);
      javax.swing.JLabel currentPathZLabel = new javax.swing.JLabel();
      currentPathZLabel.setText("Z");
      currentPathZLabel.setBounds(X + 155, Y - 30, 8, 16);
      javax.swing.JLabel currentPathBodyLabel = new javax.swing.JLabel();
      currentPathBodyLabel.setText("Body");
      currentPathBodyLabel.setBounds(X + 210, Y - 30, 30, 16);
      javax.swing.JLabel currentPathSelLabel = new javax.swing.JLabel();
      currentPathSelLabel.setText("Type");
      currentPathSelLabel.setBounds(X + 290, Y - 30, 30, 16);
      CurrentPathPanel.add(currentPathXLabel);
      CurrentPathPanel.add(currentPathYLabel);
      CurrentPathPanel.add(currentPathZLabel);
      CurrentPathPanel.add(currentPathBodyLabel);
      CurrentPathPanel.add(currentPathSelLabel);
      
      for (int i = 0; i < asmp.getSize(); i++) {
         javax.swing.JLabel indexLabel = new javax.swing.JLabel();
         javax.swing.JLabel xField = new javax.swing.JLabel();
         xField.setHorizontalAlignment(SwingConstants.TRAILING);
         javax.swing.JLabel yField = new javax.swing.JLabel();
         yField.setHorizontalAlignment(SwingConstants.TRAILING);
         javax.swing.JLabel zField = new javax.swing.JLabel();
         zField.setHorizontalAlignment(SwingConstants.TRAILING);
         javax.swing.JLabel bodyLabel = new javax.swing.JLabel();
         javax.swing.JLabel typeLabel = new javax.swing.JLabel();
         indexLabel.setText(intPropFormat.format(i+1) + ".");
         xField.setText(positionFormat.format(asmp.getitem(i).getLocationCoord(0)));
         yField.setText(positionFormat.format(asmp.getitem(i).getLocationCoord(1)));
         zField.setText(positionFormat.format(asmp.getitem(i).getLocationCoord(2)));
         bodyLabel.setText(asmp.getitem(i).getBodyName());
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
         bodyLabel.setBounds(X + 3*width + 10, height, 90, 21);
         typeLabel.setBounds(X + 3*width + 110, height, 120, 21);
         CurrentPathPanel.add(indexLabel);
         CurrentPathPanel.add(xField);
         CurrentPathPanel.add(yField);
         CurrentPathPanel.add(zField);
         CurrentPathPanel.add(bodyLabel);
         CurrentPathPanel.add(typeLabel);
      }
      Dimension d = new Dimension(400, Y + asmp.getSize() * 22);
      CurrentPathPanel.setPreferredSize(d);
   }
   
   public void setupWrapPanel(PathActuator asm) {
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
      int numAttachments = asm.getGeometryPath().getPathPointSet().getSize();
      int X = 80;
      int Y = 40;
      BodySet bodies = asm.getModel().getBodySet();
      
      SetPathWrap smw = asm.getGeometryPath().getWrapSet();
      String[] startPointNames = new String[numAttachments + 1];
      startPointNames[0] = new String("first");
      String[] endPointNames = new String[numAttachments + 1];
      endPointNames[numAttachments] = new String("last");
      for (i = 0; i < numAttachments; i++) {
         startPointNames[i+1] = String.valueOf(i+1);
         endPointNames[i] = String.valueOf(i+1);
      }
      
      // Count the number of wrap objects not currently assigned to this muscle.
      int numWrapObjects = 0;
      for (i = 0; i < bodies.getSize(); i++) {
         numWrapObjects += bodies.get(i).getWrapObjectSet().getSize();
      }
      numWrapObjects -= smw.getSize();
      
      // Create an array of names of all of the model's wrap objects
      // that are not currently assigned to this muscle. These will be
      // used to make a comboBox that appears when the user clicks the
      // "add" button.
      wrapObjectNames = new String[numWrapObjects];
      for (i = 0; i < bodies.getSize(); i++) {
         SetWrapObject wrapObjects = bodies.get(i).getWrapObjectSet();
         for (j = 0; j < wrapObjects.getSize(); j++) {
            for (k = 0; k < smw.getSize(); k++) {
               if (WrapObject.getCPtr(wrapObjects.get(j)) == WrapObject.getCPtr(smw.get(k).getWrapObject()))
                  break;
            }
            if (k == smw.getSize())
               wrapObjectNames[wCount++] = new String(wrapObjects.get(j).getName());
         }
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
   
   public void setupAttachmentPanel(PathActuator asm) {
      if (AttachmentsTab != null)
         ParametersTabbedPanel.remove(AttachmentsTab);
      AttachmentsTab = new javax.swing.JScrollPane();
      AttachmentsTab.setName("Attachments");
      ParametersTabbedPanel.insertTab("Attachments", null, AttachmentsTab, "Attachment points", 0);

      //updateAttachmentPanel(asm);
   }
   
   public void setupComponent(PathActuator newAct) {
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
      if (currentModel != null && newAct == null && currentModel.getForceSet().getSize() > 0) {
          for (int i=0; i<currentModel.getForceSet().getSize(); i++) {
             if ((newAct = PathActuator.safeDownCast(currentModel.getForceSet().get(i))) != null)
                 break;
          }
      }
      // Save the currently selected GUI tab.
      if (currentAct != null) {
         Component comp = ParametersTabbedPanel.getSelectedComponent();
         if (comp != null)
            selectedTabName = comp.getName();
         else
            selectedTabName = null;
      }
      ParametersTabbedPanel.removeAll();
      AttachmentsTab = null;
      WrapTab = null;
      CurrentPathTab = null;

      // Set the current actuator to the newly selected one (should only be null
      // if the model is null or if the model has no actuators).
      currentAct = newAct;

      if (currentModel != null) {
         guiElem = OpenSimDB.getInstance().getModelGuiElements(currentModel);
         ModelNameLabel.setText("Model: " + currentModel.getName());
         if (currentModel.getForceSet().getSize() > 0) {
            MuscleComboBox.setEnabled(true);
            MuscleComboBox.setModel(new javax.swing.DefaultComboBoxModel(guiElem.getActuatorNames()));
         } else {
            MuscleComboBox.setEnabled(false);
         }
      } else {
         ModelNameLabel.setText("Model: No models");
         MuscleNameTextField.setText("");
         MuscleNameTextField.setEnabled(false);
         MuscleTypeLabel.setText("");
         //MuscleTypeComboBox.setEnabled(false);
         MuscleComboBox.setEnabled(false);
         BackupButton.setEnabled(false);
         RestoreButton.setEnabled(false);
         BackupAllButton.setEnabled(false);
         RestoreAllButton.setEnabled(false);
         return;
      }
      
      if (currentAct == null) {
         MuscleTypeLabel.setText("");
         MuscleNameTextField.setText("");
         MuscleNameTextField.setEnabled(false);
         //MuscleTypeComboBox.setEnabled(false);
         BackupButton.setEnabled(false);
         RestoreButton.setEnabled(false);
         BackupAllButton.setEnabled(false);
         RestoreAllButton.setEnabled(false);
         return;
      } else {
         MuscleTypeLabel.setText("Type: " + currentAct.getConcreteClassName());
         MuscleNameTextField.setEnabled(true);
         //MuscleTypeComboBox.setEnabled(true);
         //BackupButton.setEnabled(true);
         //BackupAllButton.setEnabled(true);
         MuscleComboBox.setSelectedIndex(findElement(guiElem.getActuatorNames(), currentAct.getName()));
      }
      
      // Add the attachment panel first so it will always have index=0
      PathActuator asm = PathActuator.safeDownCast(currentAct);
      if (asm != null)
         setupAttachmentPanel(asm);
      
      int i, j;
      MuscleNameTextField.setText(currentAct.getName());
      
      // Create the panels to hold the properties.
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
      /*
      // Loop through the properties, adding each one to the appropriate panel.
      for (i = 0; i < ps.getSize(); i++) {
         Property_Deprecated p;
         try {
            p = ps.get(i);
            final int num = i;
            int groupNum = ps.getGroupIndexContaining(p);
            if (groupNum < 0)
               groupNum = numGroups; // this is the index of the "other" panel
            if (p.getType() == org.opensim.modeling.Property_Deprecated.PropertyType.Dbl ||
                    p.getType() == org.opensim.modeling.Property_Deprecated.PropertyType.Int) {
               javax.swing.JLabel propLabel = new javax.swing.JLabel();
               propLabel.setText(p.getName());
               propLabel.setHorizontalAlignment(SwingConstants.RIGHT);
               propLabel.setBounds(20, 22 + tabPropertyCount[groupNum] * 22, 180, 16);
               propLabel.setToolTipText(p.getComment());
               javax.swing.JTextField propField = new javax.swing.JTextField();
               propField.setBounds(210, 20 + tabPropertyCount[groupNum] * 22, 120, 21);
               propField.setHorizontalAlignment(SwingConstants.TRAILING);
               if (p.getType() == org.opensim.modeling.Property_Deprecated.PropertyType.Dbl)
                  propField.setText(doublePropFormat.format(p.getValueDbl()));
               else
                  propField.setText(p.toString());
               propField.setToolTipText(p.getComment());
               propPanel[groupNum].add(propLabel);
               propPanel[groupNum].add(propField);
               tabPropertyCount[groupNum]++;
               if (p.getType() == org.opensim.modeling.Property_Deprecated.PropertyType.Dbl) {
                  propField.addActionListener(new java.awt.event.ActionListener() {
                     public void actionPerformed(java.awt.event.ActionEvent evt) {
                        DoublePropertyEntered(((javax.swing.JTextField)evt.getSource()), num);
                     }
                  });
                  propField.addFocusListener(new java.awt.event.FocusAdapter() {
                     public void focusLost(java.awt.event.FocusEvent evt) {
                        if (!evt.isTemporary())
                           DoublePropertyEntered(((javax.swing.JTextField)evt.getSource()), num);
                     }
                  });
               } else if (p.getType() == org.opensim.modeling.Property_Deprecated.PropertyType.Int) {
                  propField.addActionListener(new java.awt.event.ActionListener() {
                     public void actionPerformed(java.awt.event.ActionEvent evt) {
                        IntPropertyEntered(((javax.swing.JTextField)evt.getSource()), num);
                     }
                  });
                  propField.addFocusListener(new java.awt.event.FocusAdapter() {
                     public void focusLost(java.awt.event.FocusEvent evt) {
                        if (!evt.isTemporary())
                           IntPropertyEntered(((javax.swing.JTextField)evt.getSource()), num);
                     }
                  });
               }
            } else if (p.getType() == org.opensim.modeling.Property_Deprecated.PropertyType.ObjPtr) {
               OpenSimObject obj = p.getValueObjPtr();
               Function func = Function.safeDownCast(obj);
               if (func != null) {
                  javax.swing.JLabel propLabel = new javax.swing.JLabel();
                  propLabel.setText(p.getName());
                  propLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                  propLabel.setBounds(20, 22 + tabPropertyCount[groupNum] * 22, 200, 16);
                  propLabel.setToolTipText(p.getComment());
                  javax.swing.JButton propButton = new javax.swing.JButton();
                  propButton.setBounds(230, 20 + tabPropertyCount[groupNum] * 22, 65, 21);
                  propButton.setText("Edit");
                  propButton.setEnabled(true);
                  propButton.setToolTipText("Edit the function controlling this property");
                  propButton.addActionListener(new java.awt.event.ActionListener() {
                     public void actionPerformed(java.awt.event.ActionEvent evt) {
                        EditPropertyFunction(((javax.swing.JButton)evt.getSource()), num);
                     }
                  });
                  propPanel[groupNum].add(propLabel);
                  propPanel[groupNum].add(propButton);
                  tabPropertyCount[groupNum]++;
               }
            }
         } catch (IOException ex) {
            ex.printStackTrace();
         }
      }
      
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
      */
      // Add the wrap and current panels last
      if (asm != null) {
         setupWrapPanel(asm);
         setupCurrentPathPanel(asm);
      }
      
      // Enable/disable the backup and restore buttons
      //updateBackupRestoreButtons();
      
      // Set the selected tab in the ParametersTabbedPanel to the
      // one whose name matches selectedTabName.
      Component[] components = ParametersTabbedPanel.getComponents();
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
      
      //Dimension windowSize = MuscleEditorScrollPane.getParent().getSize();
      Dimension d = new Dimension(565, 358);
      MuscleEditorPanel.setPreferredSize(d);
      
      this.revalidate();
      this.repaint();
   }
   
   public void componentClosed() {
      // TODO add custom code on component closing
   }
   
   /** replaces this in object stream */
   public Object writeReplace() {
      return new ResolvableHelper();
   }
   
   protected String preferredID() {
      return PREFERRED_ID;
   }
   
   private int findElement(String[] nameList, String name) {
      int i;
      for (i = 0; i < nameList.length; i++)
         if (nameList[i].equals(name))
            return i;
      return -1;
   }
   
   public void open() {
      PathActuator newAct = null;
      Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
      if (selected.length > 0 && selected[0] instanceof OneMuscleNode) {
         OneMuscleNode muscleNode = (OneMuscleNode) selected[0];
         newAct = PathActuator.safeDownCast(muscleNode.getOpenSimObject());
         if (newAct != null && PathActuator.getCPtr(newAct) != PathActuator.getCPtr(currentAct)) {
            Model newModel = newAct.getModel();
            if (Model.getCPtr(newModel) != Model.getCPtr(currentModel)) {
               Object[] options = {"OK"};
               int answer = JOptionPane.showOptionDialog(this,
                       "You can only edit muscles that are in the current model.",
                       "PathActuator Editor",
                       JOptionPane.DEFAULT_OPTION,
                       JOptionPane.WARNING_MESSAGE,
                       null,
                       options,
                       options[0]);
            } else {
               currentAct = newAct;
               AttachmentsTab = null;
               WrapTab = null;
               CurrentPathTab = null;
               selectedTabName = null;
               //setPendingChanges(false, currentAct, false);
               setupComponent(currentAct);
            }
         }
      } else {
         //setAllPendingChanges(false);
         setupComponent(null);
      }
      super.open();
      this.requestActive();
   }

   private boolean needToHandleEvent(Observable o, Object arg) {
      if (o instanceof ViewDB) {
         // We only care about ViewDB events if there is a current actuator
         // and the event's model is the muscle editor's current model (unless
         // it's a DragObjectsEvent).
         if (arg instanceof DragObjectsEvent)
            return true;
         if (currentAct == null)
            return false;
         if (arg instanceof OpenSimEvent) {
            OpenSimEvent ev = (OpenSimEvent)arg;
            if (Model.getCPtr(ev.getModel()) != Model.getCPtr(currentModel))
               return false;
         }
         return true;
      } else if (o instanceof OpenSimDB) {
         if (arg instanceof ModelEvent) {
            final ModelEvent evt = (ModelEvent)arg;
            if (evt.getOperation() == ModelEvent.Operation.SetCurrent ||
                    (evt.getOperation() == ModelEvent.Operation.Close &&
                    OpenSimDB.getInstance().getCurrentModel() == null)) {
               return true;
            }
            return false;
         } else if (arg instanceof ObjectSetCurrentEvent) {
            ObjectSetCurrentEvent evt = (ObjectSetCurrentEvent)arg;
            Vector<OpenSimObject> objs = evt.getObjects();
            // If any of the event objects is a model not equal to the current model, this means there is a new
            // current model, so we need to handle the event.
            for (int i=0; i<objs.size(); i++) {
               if (objs.get(i) instanceof Model) {
                  if (currentModel == null || !currentModel.equals(objs.get(i))) {
                     return true;
                  }
               }
            }
            return false;
         } else if (arg instanceof ObjectsChangedEvent) {
            if (currentAct == null)
               return false;
            ObjectsChangedEvent evt = (ObjectsChangedEvent)arg;
            if (Model.getCPtr(evt.getModel()) == Model.getCPtr(currentModel))
               return true;
            return false;
         } else if (arg instanceof ObjectsRenamedEvent) {
            ObjectsRenamedEvent evt = (ObjectsRenamedEvent)arg;
            Vector<OpenSimObject> objs = evt.getObjects();
            for (int i=0; i<objs.size(); i++) {
               if (objs.get(i) instanceof Model) {
                  if (currentModel != null && currentModel.equals(objs.get(i)))
                     return true;
               } else if (objs.get(i) instanceof PathActuator) {
                  PathActuator act = (PathActuator)objs.get(i);
                  if (currentModel != null && currentModel.equals(act.getModel()))
                     return true;
               }
            }
            return false;
         }
      }
      return true;
   }
   /**
    * Entry point to handle dragging of PathPoints should handle Points on Any object that has GeometryPath
    * @param o
    * @param arg 
    */
   public void update(Observable o, Object arg) {
      if (needToHandleEvent(o, arg) == false)
         return;
      if (o instanceof ViewDB) {
         if (arg instanceof ObjectSelectedEvent) {
            ObjectSelectedEvent ev = (ObjectSelectedEvent)arg;
            updateAttachmentSelections(ev.getSelectedObject(), ev.getState());
         } else if (arg instanceof ClearSelectedObjectsEvent) {
            if (currentAct != null) {
               PathActuator asm = PathActuator.safeDownCast(currentAct);
               if (asm != null) {
                  PathPointSet pathPoints = asm.getGeometryPath().getPathPointSet();
                  for (int i = 0; i < pathPoints.getSize(); i++) {
                     attachmentSelectBox[i].setSelected(false);
                  }
                  this.repaint();
               }
            }
         } else if (arg instanceof DragObjectsEvent) {
            dragPathPoints((DragObjectsEvent)arg);
         }
      } else if (o instanceof OpenSimDB) {
         // if current model is being switched due to open/close or change current then
         // update tool window
         if (arg instanceof ModelEvent) {
            final ModelEvent evt = (ModelEvent)arg;
            if (evt.getOperation() == ModelEvent.Operation.Close && OpenSimDB.getInstance().getCurrentModel() == null) {
               currentModel = null;
               currentAct = null;
               backupAllActuators();
               //setAllPendingChanges(false);
               setupComponent(null);
            }
            // Do we need to handle close separately or should we be called with SetCurrent of null model?
         } else if (arg instanceof ObjectSetCurrentEvent) {
            ObjectSetCurrentEvent evt = (ObjectSetCurrentEvent)arg;
            Vector<OpenSimObject> objs = evt.getObjects();
            // If any of the event objects is a model not equal to the current model, this means there is a new
            // current model. So clear out the panel.
            for (int i=0; i<objs.size(); i++) {
               if (objs.get(i) instanceof Model) {
                  if (currentModel == null || !currentModel.equals(objs.get(i))) {
                     currentModel = (Model)objs.get(i);
                     openSimContext = OpenSimDB.getInstance().getContext(currentModel);
                     currentAct = null;
                     //backupAllActuators();
                     //setAllPendingChanges(false);
                     //setupComponent(null);
                     break;
                  }
               }
            }
         } else if (arg instanceof ObjectsChangedEvent) {
            ObjectsChangedEvent evt = (ObjectsChangedEvent)arg;
            Vector<OpenSimObject> objs = evt.getObjects();
            // If any of the event objects is a coordinate, update the muscle path.
            for (int i=0; i<objs.size(); i++) {
               if (objs.get(i) instanceof Coordinate) {
                  if (currentAct != null) {
                     PathActuator asm = PathActuator.safeDownCast(currentAct);
                     if (asm != null)
                        updateCurrentPathPanel(asm);
                  }
                  break;
               }
            }
         } else if (arg instanceof ObjectsRenamedEvent) {
            ObjectsRenamedEvent evt = (ObjectsRenamedEvent)arg;
            Vector<OpenSimObject> objs = evt.getObjects();
            for (int i=0; i<objs.size(); i++) {
               if (objs.get(i) instanceof Model) {
                  if (currentModel != null && currentModel.equals(objs.get(i)))
                     ModelNameLabel.setText("Model: " + currentModel.getName());
               } else if (objs.get(i) instanceof PathActuator) {
                  PathActuator act = (PathActuator)objs.get(i);
                  if (currentModel != null && currentModel.equals(act.getModel()))
                     updateActuatorName(act);
               }
            }
         }
      }
   }

   private void updateAttachmentSelections(Selectable selectedObject, boolean state) {
      if (currentAct != null) {
         OpenSimObject obj = selectedObject.getOpenSimObject();
         PathActuator asm = PathActuator.safeDownCast(currentAct);
         if (asm != null && obj!=null) {
            PathPointSet pathPoints = asm.getGeometryPath().getPathPointSet();
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
   
   private void dragPathPoints(DragObjectsEvent ev) {
      ArrayList<Selectable> selectedObjects = ViewDB.getInstance().getSelectedObjects();
      PathActuator m = null;
      //PathActuator currentMuscle = PathActuator.safeDownCast(currentAct);
      boolean currentMuscleMoved = false;
      for (int i = 0; i < selectedObjects.size(); i++) {
         OpenSimObject obj = selectedObjects.get(i).getOpenSimObject();
         PathPoint mp = PathPoint.safeDownCast(obj);
         if (mp != null) {
            SimbodyEngine engine = mp.getBody().getModel().getSimbodyEngine();
            PhysicalFrame body = mp.getBody();
            PhysicalFrame ground = mp.getBody().getModel().getGround();
            double dragVectorBody[] = new double[3];
            Model model=mp.getBody().getModel();
            OpenSimContext context=OpenSimDB.getInstance().getContext(model);
            context.transform(ground, ev.getDragVector(), body, dragVectorBody);
            context.setLocation(mp, 0, mp.getLocationCoord(0) + dragVectorBody[0]);
            context.setLocation(mp, 1, mp.getLocationCoord(1) + dragVectorBody[1]);
            context.setLocation(mp, 2, mp.getLocationCoord(2) + dragVectorBody[2]);
            currentMuscleMoved = true;
            
            // Update the geometry of the muscle.
            SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(mp.getPath().getModel());
            //vis.updateMuscleOrForceAlongPathGeometry(mp.getPath().getOwner(), true);
         }
      }
      // If m is not null, then at least one selected object is a muscle point
      // (that was just dragged). So redraw the model.
      //if (m != null) {
         SingleModelGuiElements guiElem = OpenSimDB.getInstance().getModelGuiElements(currentModel);
         guiElem.setUnsavedChangesFlag(true);
         // If the current muscle moved, update the necessary panels.
         //if (currentMuscleMoved) {
            //updateAttachmentPanel(currentMuscle);
            //updateCurrentPathPanel(currentMuscle);
         //}
         ViewDB.getInstance().renderAll();
      //}
   }

   /* This function is called by the PathPointFunctionEventListener when a moving muscle
    * point's X, Y, or Z function has been changed.
    */
   public void movingPointMoved(Model model, PathActuator muscle, MovingPathPoint point) {
      //setPendingChanges(true, muscle, true);
      // tell the ViewDB to redraw the model
      SingleModelVisuals vis = ViewDB.getInstance().getModelVisuals(model);
      vis.updateActuatorGeometry(muscle, true);
      ViewDB.getInstance().repaintAll();
      // update the current path panel
      updateCurrentPathPanel(muscle);
   }

   /* This function is called by the MusclePropertyFunctionEventListener when a muscle
    * property that's a function has been changed.
    */
   public void propertyFunctionChanged(Model model, PathActuator act) {
      //setPendingChanges(true, act, true);
   }

   final static class ResolvableHelper implements Serializable {
      private static final long serialVersionUID = 1L;
      public Object readResolve() {
         return MuscleEditorTopComponent.getDefault();
      }
   }
   
}
