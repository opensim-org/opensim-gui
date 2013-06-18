/*
 * Created on May 15, 2007, 10:16 AM
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
package org.opensim.coordinateviewer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.opensim.modeling.Coordinate;
import org.opensim.modeling.CoordinateSet;
import org.opensim.view.ModelSettings;
import org.opensim.view.ModelPose;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.modeling.Model;
import org.opensim.view.experimentaldata.ModelForExperimentalData;
import org.opensim.modeling.ObjectGroup;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.motions.MotionTimeChangeEvent;
import org.opensim.view.motions.MotionsDB;
import org.opensim.view.ModelEvent;
import org.opensim.view.ObjectSetCurrentEvent;
import org.opensim.view.ObjectsRenamedEvent;
import org.opensim.view.pub.ViewDB;
/**
 * Top component which displays something.
 */
final class CoordinateViewerTopComponent extends TopComponent implements Observer, CoordinateChangeListener {
   
   private static CoordinateViewerTopComponent instance;
   private Model aModel;
   private OpenSimContext openSimContext;
   private CoordinateSet coords;
   javax.swing.JPanel CoordinatesPanel;
   ModelSettings prefs;//=new ModelGUIPrefs();
   private boolean hasModel=false;
   private boolean modelHasFile=false;
   
   private ComboBoxModel groupsComboBoxModel;
   ObjectGroup currentGroup;
   Hashtable<Coordinate, CoordinateSliderWithBox> mapCoordinates2Sliders =
           new Hashtable<Coordinate, CoordinateSliderWithBox>(4);
   static final String DEFAULT_POSE_NAME="Default";
   
   /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
   
   private static final String PREFERRED_ID = "CoordinateViewerTopComponent";
   
   private CoordinateViewerTopComponent() {
      initComponents();

      jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.Y_AXIS));
      setName(NbBundle.getMessage(CoordinateViewerTopComponent.class, "CTL_CoordinateViewerTopComponent"));
      setToolTipText(NbBundle.getMessage(CoordinateViewerTopComponent.class, "HINT_CoordinateViewerTopComponent"));
      // Don't know if this should be here or moved to component opened, then we'll need to remove observer in componentClosed'
      OpenSimDB.getInstance().addObserver(this);
      // The following line enables coordinate sliders to update while playing back motion
      // Currently too slow
      MotionsDB.getInstance().addObserver(this);
      
      // Populate list of cooridnate groups from model
      jCoordinateGroupsComboBox.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox)e.getSource();
            //currentGroup = coords.getGroup((String)cb.getSelectedItem());
            updateDisplayGroup();
         }});
   }
   
   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        jCoordinateGroupsComboBox = new javax.swing.JComboBox();
        jPosesPopupMenu = new javax.swing.JPopupMenu();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jRestorePoseButton = new javax.swing.JButton();
        jSavePoseButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jDeletePoseButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jModelNameLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jPosesButton = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, "Group");

        jCoordinateGroupsComboBox.setEnabled(false);
        jCoordinateGroupsComboBox.setFocusable(false);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        org.openide.awt.Mnemonics.setLocalizedText(jRestorePoseButton, "Restore...");
        jRestorePoseButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jRestorePoseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRestorePoseButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jSavePoseButton, "Save...");
        jSavePoseButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jSavePoseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSavePoseButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, "Poses:");

        org.openide.awt.Mnemonics.setLocalizedText(jDeletePoseButton, "Delete...");
        jDeletePoseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDeletePoseButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, "Model:");

        org.openide.awt.Mnemonics.setLocalizedText(jModelNameLabel, "model name");
        jModelNameLabel.setAutoscrolls(true);
        jModelNameLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jModelNameLabel.setMaximumSize(new java.awt.Dimension(421, 14));
        jModelNameLabel.setMinimumSize(new java.awt.Dimension(0, 0));
        jModelNameLabel.setPreferredSize(new java.awt.Dimension(400, 14));

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 327, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 442, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanel1);

        org.openide.awt.Mnemonics.setLocalizedText(jPosesButton, "Poses >");
        jPosesButton.setComponentPopupMenu(jPosesPopupMenu);
        jPosesButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPosesButtonMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPosesButtonMouseReleased(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jModelNameLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPosesButton)
                .addContainerGap())
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jPosesButton)
                    .add(jLabel1)
                    .add(jModelNameLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

   private void jPosesButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPosesButtonMousePressed
      if (hasModel)
          jPosesPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
   }//GEN-LAST:event_jPosesButtonMousePressed

   private void jPosesButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPosesButtonMouseReleased
      // Used to also show the popup menu, but seems nicer to only do it on mouse press
   }//GEN-LAST:event_jPosesButtonMouseReleased

   private void jDeletePoseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDeletePoseButtonActionPerformed
      PoseSelectionJPanel p = new PoseSelectionJPanel();
      
      Vector<ModelPose> existing = new Vector<ModelPose>(prefs.getPoses());
      for(ModelPose pose:existing)
        if (pose.getPoseName().equalsIgnoreCase(DEFAULT_POSE_NAME)){
          existing.remove(pose);
          break;
        }
            
      p.setPoses(existing);
      p.allowMultipleSelection();
      DialogDescriptor dlg = new DialogDescriptor(p, "Delete pose");
      DialogDisplayer.getDefault().createDialog(dlg).setVisible(true);
      Object userInput = dlg.getValue();
      if (((Integer)userInput).compareTo((Integer)DialogDescriptor.OK_OPTION)==0){
         Vector<ModelPose> poses = p.getSelectedPoses();
         prefs.deletePoses(poses);
      }
      updateAvailability();
// TODO add your handling code here:
   }//GEN-LAST:event_jDeletePoseButtonActionPerformed
   
   private void jRestorePoseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRestorePoseButtonActionPerformed
// TODO add your handling code here:
      PoseSelectionJPanel p = new PoseSelectionJPanel();
      p.setPoses(prefs.getPoses());
      DialogDescriptor dlg = new DialogDescriptor(p, "Restore to pose");
      DialogDisplayer.getDefault().createDialog(dlg).setVisible(true);
      Object userInput = dlg.getValue();
      if (((Integer)userInput).compareTo((Integer)DialogDescriptor.OK_OPTION)==0){
         Vector<ModelPose> poses = p.getSelectedPoses();
         applyPose(poses.get(0));
         // update display only once at the end
         ViewDB.getInstance().updateModelDisplay(OpenSimDB.getInstance().getCurrentModel());      
      }
   }//GEN-LAST:event_jRestorePoseButtonActionPerformed
   
   private void jSavePoseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSavePoseButtonActionPerformed
// TODO add your handling code here:
      prefs = ViewDB.getInstance().getModelSavedSettings(aModel).getPrefs();
      // Query for name
      int savedPosesCount=prefs.getNumPoses();
      String defaultName= "Pose"+String.valueOf(savedPosesCount+1);
      NotifyDescriptor.InputLine dlg = new NotifyDescriptor.InputLine("Enter Pose Name", "<Pose Name>");
      dlg.setInputText(defaultName);
      if(DialogDisplayer.getDefault().notify(dlg)==NotifyDescriptor.OK_OPTION){
         String newName = dlg.getInputText();
         if (!prefs.containsPose(newName))
              prefs.addPose(new ModelPose(coords,newName));
         else
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                    NbBundle.getMessage(CoordinateViewerTopComponent.class, 
                        "CTL_DuplicatePoseName")));
      };
      updateAvailability();     
   }//GEN-LAST:event_jSavePoseButtonActionPerformed
     
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jCoordinateGroupsComboBox;
    private javax.swing.JButton jDeletePoseButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jModelNameLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton jPosesButton;
    private javax.swing.JPopupMenu jPosesPopupMenu;
    private javax.swing.JButton jRestorePoseButton;
    private javax.swing.JButton jSavePoseButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
   
   /**
    * Gets default instance. Do not use directly: reserved for *.settings files only,
    * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
    * To obtain the singleton instance, use {@link findInstance}.
    */
   public static synchronized CoordinateViewerTopComponent getDefault() {
      if (instance == null) {
         instance = new CoordinateViewerTopComponent();
      }
      return instance;
   }
   
   /**
    * Obtain the CoordinateViewerTopComponent instance. Never call {@link #getDefault} directly!
    */
   public static synchronized CoordinateViewerTopComponent findInstance() {
      TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
      if (win == null) {
         ErrorManager.getDefault().log(ErrorManager.WARNING, "Cannot find CoordinateViewer component. It will not be located properly in the window system.");
         return getDefault();
      }
      if (win instanceof CoordinateViewerTopComponent) {
         return (CoordinateViewerTopComponent)win;
      }
      ErrorManager.getDefault().log(ErrorManager.WARNING, "There seem to be multiple components with the '" + PREFERRED_ID + "' ID. That is a potential source of errors and unexpected behavior.");
      return getDefault();
   }
   
   public int getPersistenceType() {
      return TopComponent.PERSISTENCE_ALWAYS;
   }
   
   public void componentOpened() {
      // TODO add custom code on component opening
      aModel = OpenSimDB.getInstance().getCurrentModel();
      if (aModel==null){
         jModelNameLabel.setText("No Models");
         hasModel=false;
         coords = null;    // Don't keep reference to old model's coordinates to avoid memory leak'
         mapCoordinates2Sliders.clear();
         updateAvailability();
         updatePosesPopup();
         return;
      }
      else if (aModel instanceof ModelForExperimentalData){
          jModelNameLabel.setText("");
         coords = null;    // Don't keep reference to old model's coordinates to avoid memory leak'
         mapCoordinates2Sliders.clear();
         hasModel = false;
         updateAvailability();
         updatePosesPopup();
         return;
      }
      openSimContext = OpenSimDB.getInstance().getContext(aModel);
      hasModel=true;
      if (ViewDB.getInstance().getModelSavedSettings(aModel)==null)
         modelHasFile = false;
      else{
         modelHasFile=true;
         prefs=ViewDB.getInstance().getModelSavedSettings(aModel).getPrefs();
      }
      jModelNameLabel.setText(aModel.getName());
      if (aModel.getNumCoordinates()==0) return;
      coords = aModel.getCoordinateSet();
      updateAvailability();
      Vector<String> coordinateGroupNames = new Vector<String>();
      for(int i=0; i<coords.getNumGroups(); i++)
         coordinateGroupNames.add(coords.getGroup(i).getName());
      groupsComboBoxModel = new DefaultComboBoxModel(coordinateGroupNames);
      jCoordinateGroupsComboBox.setModel(groupsComboBoxModel);
      String currentGroupName= (String)groupsComboBoxModel.getSelectedItem();
      //currentGroup = coords.getGroup(currentGroupName);
      // Create CoordinateSliderWithBox for each coordinate and add them to the ScrollPane
      updateDisplayGroup();
      ViewDB.getInstance().updateModelDisplay(OpenSimDB.getInstance().getCurrentModel());
      updatePosesPopup();
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
   
   public void update(Observable o, Object arg) {
      // Update based on current model.
      if (o instanceof OpenSimDB){
         // if current model is being switched due to open/close or change current then
         // update list of coordinates
         if (arg instanceof ModelEvent) {
            final ModelEvent evt = (ModelEvent)arg;
            if (evt.getOperation()==ModelEvent.Operation.SetCurrent ||
                    (evt.getOperation()==ModelEvent.Operation.Close &&
                    OpenSimDB.getInstance().getCurrentModel()==null)){
               jPanel1.removeAll();
               componentOpened();
               repaint();
            }
            /*else if (evt.getOperation()==ModelEvent.Operation.Open){
               // Create a Default pose here since coordinates will change on us later
               aModel = evt.getModel();
               coords = aModel.getCoordinateSet();
               ViewDB.getInstance().processSavedSettings(aModel);
               prefs=ViewDB.getInstance().getModelSavedSettings(aModel).getPrefs();
               createDefaultPoseIfNeeded(prefs.getPoses());
            }*/
            // Do we need to handle close separately or should we be called with SetCurrent of null model?
            // save may trigger saving poses and open may trigger loading poses.
         } else if (arg instanceof ObjectSetCurrentEvent) {
            ObjectSetCurrentEvent evt = (ObjectSetCurrentEvent)arg;
            Vector<OpenSimObject> objs = evt.getObjects();
            // If any of the event objects is a model not equal to the current one, this means there is a new
            // current model. So update the panel.
            for (int i=0; i<objs.size(); i++) {
               if (objs.get(i) instanceof Model) {
                  if (aModel == null || !aModel.equals(objs.get(i))) {
                     jPanel1.removeAll();
                     componentOpened();
                     repaint();
                     break;
                  }
               }
            }
         } else if (arg instanceof ObjectsRenamedEvent) {
            ObjectsRenamedEvent ev = (ObjectsRenamedEvent)arg;
            Vector<OpenSimObject> objs = ev.getObjects();
            for (int i=0; i<objs.size(); i++) {
               if (objs.get(i) instanceof Model) {
                  if (aModel != null && aModel.equals(objs.get(i))) {
                     jModelNameLabel.setText(objs.get(i).getName());
                  }
               }
            }
         }
      }
      else if (o instanceof MotionsDB){
         if (arg instanceof MotionTimeChangeEvent) {
            // Get current model, displayed group and update their sliders, text boxes
            Enumeration<CoordinateSliderWithBox> displayedSliders=mapCoordinates2Sliders.elements();
            while(displayedSliders.hasMoreElements()){
               displayedSliders.nextElement().updateValueSelfOnly();
            }
         }        
      }
   }
   /**
    * Make buttons that rely on an existing model on/off
    */
   private void updateAvailability() {
      //jSelectGroupButton.setEnabled(hasModel);
      jSavePoseButton.setEnabled(hasModel);
      jRestorePoseButton.setEnabled(hasModel);
      jDeletePoseButton.setEnabled(hasModel);
      jCoordinateGroupsComboBox.setEnabled(hasModel);
      jPosesButton.setEnabled(hasModel);
      // Further enable based on availability of poses
      if (hasModel){
         jRestorePoseButton.setEnabled(modelHasFile && prefs.getNumPoses()>0);
         jDeletePoseButton.setEnabled(modelHasFile && prefs.getNumPoses()>0);
      }
          
   }
   
   private void updateDisplayGroup() {
      jPanel1.removeAll();
      mapCoordinates2Sliders.clear();
      
      jPanel1.add(Box.createRigidArea(new Dimension(10,10)));
      
      JLabel nameLabel = new JLabel("    Name");
      JLabel valueLabel = new JLabel("                      Value");
      JLabel speedLabel = new JLabel("Speed         ");
      speedLabel.setToolTipText("Speed m/s, deg/s");
      JPanel labelPanel = new JPanel(new BorderLayout());
      labelPanel.add(nameLabel, BorderLayout.WEST);
      labelPanel.add(valueLabel,  BorderLayout.CENTER);
      labelPanel.add(speedLabel, BorderLayout.EAST);
      labelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,25));  // fixing height
      labelPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
      
      jPanel1.add(labelPanel); 

      for(int i=0; i<coords.getSize(); i++){
         Coordinate coord = coords.get(i);
         boolean constrained = openSimContext.isConstrained(coord);
         //if (constrained)
         //   System.out.println("Coordinate "+coord.getName()+" will have no slider");
         if (!constrained){
            CoordinateSliderWithBox sliderPanel = new CoordinateSliderWithBox(coord);
            mapCoordinates2Sliders.put(coord, sliderPanel);
            sliderPanel.registerCoordChangeListener(this);
            sliderPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            sliderPanel.updateValue();
            jPanel1.add(sliderPanel);
         }
      }
      jPanel1.add(Box.createVerticalGlue());
      jPanel1.add(Box.createRigidArea(new Dimension(10,10)));
      jPanel1.validate();
   }

    public void valueChanged(Coordinate coord, double newValue, boolean setText, boolean setSlider, boolean setCoordinate, boolean updateDisplay) {
        
        CoordinateSliderWithBox currSlider = mapCoordinates2Sliders.get(coord);
        currSlider.setTheValue(newValue, setText, setSlider, setCoordinate, setCoordinate);
        
        if(aModel.getConstraintSet().getSize() > 0 ){
            for(Coordinate c : mapCoordinates2Sliders.keySet()) {
                if(!c.getName().equals(coord.getName())){
                    double val = mapCoordinates2Sliders.get(c).isRotational()? openSimContext.getValue(c) * (180.0/Math.PI) : openSimContext.getValue(c);
                    mapCoordinates2Sliders.get(c).setTheValue(val
                        ,true, true, false, false);
                }
            }
        }
    }
    
   final static class ResolvableHelper implements Serializable {
      private static final long serialVersionUID = 1L;
      public Object readResolve() {
         return CoordinateViewerTopComponent.getDefault();
      }
   }
   
   private void applyPose(final ModelPose pose) {
      Vector<String> coordinateNames=pose.getCoordinateNames();
      Vector coordinateValues=pose.getCoordinateValues();
      if (coordinateNames.size() != coordinateNames.size()){
         // Some coordinates may not have values, probably because of model changes in this case
         // show error and return;
          DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                    "Number of coordinates in saved pose does not match the model... Aborting pose setting."));
      }
      boolean realize=false;
      int which = -1;
      openSimContext.resetStateToDefault();
      boolean isDefault =  (pose.getPoseName().equalsIgnoreCase(DEFAULT_POSE_NAME));
      for(int i=0;i<coordinateNames.size();i++){
         // Values in file
         String name=coordinateNames.get(i);
         double storedValue = (Double)coordinateValues.get(i);
         
         Coordinate coord=coords.get(name);
         if (isDefault) storedValue = coord.getDefaultValue();
         if (coord !=null){
            openSimContext.setValue(coord, storedValue, false);
            realize=true;
            which = i;
            CoordinateSliderWithBox coordinateSlider = mapCoordinates2Sliders.get(coord);
            if (coordinateSlider!=null)
                coordinateSlider.updateValue();
         }
         if (i==coordinateNames.size()-1){  // One realize per pose
            name=coordinateNames.get(which);
            storedValue = (Double)coordinateValues.get(which);
            coord=coords.get(name);
            if (isDefault) storedValue = coord.getDefaultValue();
            openSimContext.setValue(coord, storedValue, true);             
         }
      }
      openSimContext.realizeVelocity();
   }

   private void updatePosesPopup() {
         jPosesPopupMenu.removeAll();
         if (!hasModel || prefs==null) return;
         // Add items to select poses with callback to apply them
         createDefaultPoseIfNeeded(prefs.getPoses());
         final Vector<ModelPose> poses =prefs.getPoses();
         for(int i=0; i<poses.size(); i++){
            final ModelPose p=poses.get(i);
            JMenuItem item=new JMenuItem(p.getPoseName());
            jPosesPopupMenu.add(item);
            item.addActionListener(new ActionListener(){
               public void actionPerformed(ActionEvent e) {
                  applyPose(p);
                  ViewDB.getInstance().updateModelDisplay(OpenSimDB.getInstance().getCurrentModel());      
               }});
         }
         if (poses.size()>0)
            jPosesPopupMenu.addSeparator();

         JMenuItem newItem = new JMenuItem("New...");
         newItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
               jSavePoseButtonActionPerformed(e);
               updatePosesPopup();
            }});
         jPosesPopupMenu.add(newItem);
         JMenuItem setDefaultItem = new JMenuItem("Set Default");
         setDefaultItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                // Make a pose from currentState
                Model mdl = OpenSimDB.getInstance().getCurrentModel();
                ModelPose newDefaultPose = new ModelPose(mdl.getCoordinateSet(),DEFAULT_POSE_NAME, false);
                newDefaultPose.useAsDefaultForModel(mdl);
               // Show a dialog warning that this change is persistent if model is saved
            }});
         jPosesPopupMenu.add(setDefaultItem);
         if (poses.size()>0){
            JMenuItem deleteItem=new JMenuItem("Delete...");
            deleteItem.addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent e) {
                           jDeletePoseButtonActionPerformed(e);
                           updatePosesPopup();
                        }});         
            jPosesPopupMenu.add(deleteItem);
         }
   }
   /**
    * This works we just need to invoke it when the model is loaded first time
    **/
   private void createDefaultPoseIfNeeded(Vector<ModelPose> savedPoses) {
      boolean found=false;
      for(int i=0; i<savedPoses.size() && !found; i++){
         ModelPose p=savedPoses.get(i);
         if (p.getPoseName().compareTo(DEFAULT_POSE_NAME)==0){
            //savedPoses.set(i, newDefaultPose);
            found=true;
            break;
         }
      } 
      if (!found){
         ModelPose newDefaultPose = new ModelPose(coords,DEFAULT_POSE_NAME, true);
         savedPoses.add(0, newDefaultPose);
      }
   }

    void rebuild(CoordinateViewerDescriptor mDesc) {
        // Apply poses to all models
        Object[] models = OpenSimDB.getInstance().getAllModels();
        for (int i=0; i< models.length; i++){
            Model mdl = (Model) models[i];
            openSimContext = OpenSimDB.getInstance().getContext(mdl);
            ModelPose pose = mDesc.getPoses().get(i);
            if (pose.getPoseName().equals(mdl.getName())){
                applyPoseToModel(mdl, pose);
                ViewDB.getInstance().updateModelDisplay(mdl);
            }
        }
    }

    private void applyPoseToModel(Model mdl, ModelPose pose) {
        Vector<String> coordinateNames=pose.getCoordinateNames();
        Vector coordinateValues=pose.getCoordinateValues();
        openSimContext = OpenSimDB.getInstance().getContext(mdl);
        openSimContext.resetStateToDefault();
        for(int i=0;i<coordinateNames.size();i++){
            // Values in file
            String name=coordinateNames.get(i);
            double storedValue = (Double)coordinateValues.get(i);
            
            Coordinate coord=mdl.getCoordinateSet().get(name);
            if (coord !=null){
                openSimContext.setValue(coord, storedValue);
            }
        }
    }
}
