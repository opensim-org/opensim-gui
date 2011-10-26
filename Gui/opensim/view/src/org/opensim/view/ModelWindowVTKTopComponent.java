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
package org.opensim.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.UndoRedo;
import org.openide.util.NbBundle;
import org.openide.util.SharedClassObject;
import org.openide.windows.TopComponent;
import org.opensim.utils.FileUtils;
import org.opensim.utils.Prefs;
import org.opensim.utils.TheApp;
import org.opensim.view.pub.ViewDB;
import vtk.vtkCamera;
import vtk.vtkFileOutputWindow;
import vtk.vtkMatrix4x4;

/**
 * Top component which displays something.
 */
public class ModelWindowVTKTopComponent extends TopComponent 
        implements ChangeListener {  // For sliders
    
   class SetCameraAction extends AbstractAction {
      private Camera camera;
      public SetCameraAction(Camera camera) {
         super((camera!=null) ? camera.getName() : "none");
         this.camera = camera;
      }
      public void actionPerformed(ActionEvent evt) {
         getCanvas().setCamera(camera);
      }
   }

   class CameraEditorAction extends AbstractAction {
      public CameraEditorAction() {
         super("Edit Cameras...");
      }
      public void actionPerformed(ActionEvent evt) {
         CameraEditorPanel panel = new CameraEditorPanel();
         DialogDescriptor dlg = new DialogDescriptor(panel, "Camera Editor");
         dlg.setModal(false);
         dlg.setOptions(new Object[]{DialogDescriptor.OK_OPTION});
         Dialog dialog = DialogDisplayer.getDefault().createDialog(dlg);
         dialog.setVisible(true);
      }
   }
   
    private boolean internalTrigger=false;
    private static final long serialVersionUID = 1L;
    private static int ct = 0; //A counter used to provide names for new models
    private String tabDisplayName;
    Preferences prefs;
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    public ModelWindowVTKTopComponent() {
        initComponents();
        setComponentZOrder(toolBarPanel1, 0);
        setComponentZOrder(navigationPanel1, 1);
        setComponentZOrder(navigationPanel2, 2);
        setComponentZOrder(navigationPanel3, 3);
        setComponentZOrder(openSimCanvas1, 4);
        
        openSimCanvas1.addChangeListener(this);
        jHorizontalSlider1.addChangeListener(this); 
        jJoystickSlider.addChangeListener(this); 
        
        setTabDisplayName(NbBundle.getMessage(
                        ModelWindowVTKTopComponent.class,
                        "UnsavedModelNameFormat",
                        new Object[] { new Integer(ct++) }
                ));
        /*OpenSim20
        WindowManager.getDefault().invokeWhenUIReady(new Runnable(){
            public void run() {
                 setName(tabDisplayName);
            }});
        */
        // Set preferred directory for the TopComponent (to be used for all saving, loading, ...
        prefs = Preferences.userNodeForPackage(TheApp.class);
        
        synchronizeBackgroundColor();
                
        vtkFileOutputWindow fow = new vtkFileOutputWindow();
        fow.SetFileName("vtklog.log");
        if (fow != null)
           fow.SetInstance(fow);
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jRefitModelButton = new javax.swing.JButton();
        navigationPanel3 = new java.awt.Panel();
        jJoystickSlider = new javax.swing.JSlider();
        jJoystickSlider.setUI(new org.opensim.view.OpenSimJoystickSliderUI(jJoystickSlider));
        jPanButtonPanel = new javax.swing.JPanel();
        jPanButton1 = new javax.swing.JButton();
        jPanButton2 = new javax.swing.JButton();
        jPanButton3 = new javax.swing.JButton();
        jPanButton4 = new javax.swing.JButton();
        jPanButton5 = new javax.swing.JButton();
        jPanButton6 = new javax.swing.JButton();
        jPanButton7 = new javax.swing.JButton();
        jPanButton8 = new javax.swing.JButton();
        jPanButton9 = new javax.swing.JButton();
        navigationPanel2 = new java.awt.Panel();
        jHorizontalSlider1 = new javax.swing.JSlider();
        jHorizontalSlider1.setUI(new org.opensim.view.OpenSimRoundSliderUI(jHorizontalSlider1));
        navigationPanel1 = new java.awt.Panel();
        jSphereButton1 = new javax.swing.JButton();
        jSphereButton2 = new javax.swing.JButton();
        jSphereButton3 = new javax.swing.JButton();
        jSphereButton4 = new javax.swing.JButton();
        jSphereButton5 = new javax.swing.JButton();
        jSphereButton6 = new javax.swing.JButton();
        jSphereButton7 = new javax.swing.JButton();
        jSphereButton8 = new javax.swing.JButton();
        jSphereButton9 = new javax.swing.JButton();
        openSimCanvas1 = new org.opensim.view.OpenSimCanvas();
        toolBarPanel1 = new java.awt.Panel();
        jModelWindowToolBar = new javax.swing.JToolBar();
        jBackgroundColorButton = new javax.swing.JButton();
        jAxesToggleButton = new javax.swing.JToggleButton();
        jMinusXViewButton = new javax.swing.JButton();
        jPlusXViewButton = new javax.swing.JButton();
        jMinusYViewButton = new javax.swing.JButton();
        jPlusYViewButton = new javax.swing.JButton();
        jMinusZViewButton = new javax.swing.JButton();
        jPlusZViewButton = new javax.swing.JButton();
        jTakeSnapshotButton = new javax.swing.JButton();
        jStartStopMovieToggleButton = new javax.swing.JToggleButton();
        cameraEditorButton = new javax.swing.JButton();
        jAnnotateToggleButton = new javax.swing.JToggleButton();

        org.openide.awt.Mnemonics.setLocalizedText(jRefitModelButton, "Refit");
        jRefitModelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRefitModelButtonActionPerformed(evt);
            }
        });

        setMinimumSize(new java.awt.Dimension(20, 20));
        setLayout(new java.awt.GridBagLayout());

        navigationPanel3.setBackground(new java.awt.Color(224, 223, 227));
        navigationPanel3.setVisible(false);
        navigationPanel3.setLayout(new java.awt.GridBagLayout());

        jJoystickSlider.setMaximum(180);
        jJoystickSlider.setMinimum(-180);
        jJoystickSlider.setValue(0);
        jJoystickSlider.setMaximumSize(new java.awt.Dimension(144, 144));
        jJoystickSlider.setMinimumSize(new java.awt.Dimension(144, 144));
        jJoystickSlider.setOpaque(false);
        jJoystickSlider.setPreferredSize(new java.awt.Dimension(144, 144));
        jJoystickSlider.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jJoystickSliderMouseMoved(evt);
            }
        });
        jJoystickSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jJoystickSliderMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jJoystickSliderMouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        navigationPanel3.add(jJoystickSlider, gridBagConstraints);

        jPanButtonPanel.setMaximumSize(new java.awt.Dimension(120, 120));
        jPanButtonPanel.setMinimumSize(new java.awt.Dimension(120, 120));
        jPanButtonPanel.setPreferredSize(new java.awt.Dimension(120, 120));
        jPanButtonPanel.setVerifyInputWhenFocusTarget(false);
        jPanButtonPanel.setLayout(new java.awt.GridLayout(3, 3));

        jPanButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton1.png"))); // NOI18N
        jPanButton1.setToolTipText("Pan Northwest");
        jPanButton1.setAlignmentX(0.5F);
        jPanButton1.setBorderPainted(false);
        jPanButton1.setContentAreaFilled(false);
        jPanButton1.setDefaultCapable(false);
        jPanButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jPanButton1.setMaximumSize(new java.awt.Dimension(40, 40));
        jPanButton1.setMinimumSize(new java.awt.Dimension(40, 40));
        jPanButton1.setPreferredSize(new java.awt.Dimension(40, 40));
        jPanButton1.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton1_pressed.png"))); // NOI18N
        jPanButton1.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton1_rollover.png"))); // NOI18N
        jPanButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPanButton1ActionPerformed(evt);
            }
        });
        jPanButtonPanel.add(jPanButton1);

        jPanButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton2.png"))); // NOI18N
        jPanButton2.setToolTipText("Pan North");
        jPanButton2.setAlignmentX(0.5F);
        jPanButton2.setBorderPainted(false);
        jPanButton2.setContentAreaFilled(false);
        jPanButton2.setDefaultCapable(false);
        jPanButton2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jPanButton2.setMaximumSize(new java.awt.Dimension(40, 40));
        jPanButton2.setMinimumSize(new java.awt.Dimension(40, 40));
        jPanButton2.setPreferredSize(new java.awt.Dimension(40, 40));
        jPanButton2.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton2_pressed.png"))); // NOI18N
        jPanButton2.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton2_rollover.png"))); // NOI18N
        jPanButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPanButton2ActionPerformed(evt);
            }
        });
        jPanButtonPanel.add(jPanButton2);

        jPanButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton3.png"))); // NOI18N
        jPanButton3.setToolTipText("Pan Northeast");
        jPanButton3.setAlignmentX(0.5F);
        jPanButton3.setBorderPainted(false);
        jPanButton3.setContentAreaFilled(false);
        jPanButton3.setDefaultCapable(false);
        jPanButton3.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jPanButton3.setMaximumSize(new java.awt.Dimension(40, 40));
        jPanButton3.setMinimumSize(new java.awt.Dimension(40, 40));
        jPanButton3.setPreferredSize(new java.awt.Dimension(40, 40));
        jPanButton3.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton3_pressed.png"))); // NOI18N
        jPanButton3.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton3_rollover.png"))); // NOI18N
        jPanButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPanButton3ActionPerformed(evt);
            }
        });
        jPanButtonPanel.add(jPanButton3);

        jPanButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton4.png"))); // NOI18N
        jPanButton4.setToolTipText("Pan West");
        jPanButton4.setAlignmentX(0.5F);
        jPanButton4.setBorderPainted(false);
        jPanButton4.setContentAreaFilled(false);
        jPanButton4.setDefaultCapable(false);
        jPanButton4.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jPanButton4.setMaximumSize(new java.awt.Dimension(40, 40));
        jPanButton4.setMinimumSize(new java.awt.Dimension(40, 40));
        jPanButton4.setPreferredSize(new java.awt.Dimension(40, 40));
        jPanButton4.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton4_pressed.png"))); // NOI18N
        jPanButton4.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton4_rollover.png"))); // NOI18N
        jPanButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPanButton4ActionPerformed(evt);
            }
        });
        jPanButtonPanel.add(jPanButton4);

        jPanButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton5.png"))); // NOI18N
        jPanButton5.setToolTipText("No Pan");
        jPanButton5.setAlignmentX(0.5F);
        jPanButton5.setBorderPainted(false);
        jPanButton5.setContentAreaFilled(false);
        jPanButton5.setDefaultCapable(false);
        jPanButton5.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jPanButton5.setMaximumSize(new java.awt.Dimension(40, 40));
        jPanButton5.setMinimumSize(new java.awt.Dimension(40, 40));
        jPanButton5.setPreferredSize(new java.awt.Dimension(40, 40));
        jPanButtonPanel.add(jPanButton5);

        jPanButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton6.png"))); // NOI18N
        jPanButton6.setToolTipText("Pan East");
        jPanButton6.setAlignmentX(0.5F);
        jPanButton6.setBorderPainted(false);
        jPanButton6.setContentAreaFilled(false);
        jPanButton6.setDefaultCapable(false);
        jPanButton6.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jPanButton6.setMaximumSize(new java.awt.Dimension(40, 40));
        jPanButton6.setMinimumSize(new java.awt.Dimension(40, 40));
        jPanButton6.setPreferredSize(new java.awt.Dimension(40, 40));
        jPanButton6.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton6_pressed.png"))); // NOI18N
        jPanButton6.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton6_rollover.png"))); // NOI18N
        jPanButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPanButton6ActionPerformed(evt);
            }
        });
        jPanButtonPanel.add(jPanButton6);

        jPanButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton7.png"))); // NOI18N
        jPanButton7.setToolTipText("Pan Southwest");
        jPanButton7.setAlignmentX(0.5F);
        jPanButton7.setBorderPainted(false);
        jPanButton7.setContentAreaFilled(false);
        jPanButton7.setDefaultCapable(false);
        jPanButton7.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jPanButton7.setMaximumSize(new java.awt.Dimension(40, 40));
        jPanButton7.setMinimumSize(new java.awt.Dimension(40, 40));
        jPanButton7.setPreferredSize(new java.awt.Dimension(40, 40));
        jPanButton7.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton7_pressed.png"))); // NOI18N
        jPanButton7.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton7_rollover.png"))); // NOI18N
        jPanButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPanButton7ActionPerformed(evt);
            }
        });
        jPanButtonPanel.add(jPanButton7);

        jPanButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton8.png"))); // NOI18N
        jPanButton8.setToolTipText("Pan South");
        jPanButton8.setAlignmentX(0.5F);
        jPanButton8.setBorderPainted(false);
        jPanButton8.setContentAreaFilled(false);
        jPanButton8.setDefaultCapable(false);
        jPanButton8.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jPanButton8.setMaximumSize(new java.awt.Dimension(40, 40));
        jPanButton8.setMinimumSize(new java.awt.Dimension(40, 40));
        jPanButton8.setPreferredSize(new java.awt.Dimension(40, 40));
        jPanButton8.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton8_pressed.png"))); // NOI18N
        jPanButton8.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton8_rollover.png"))); // NOI18N
        jPanButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPanButton8ActionPerformed(evt);
            }
        });
        jPanButtonPanel.add(jPanButton8);

        jPanButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton9.png"))); // NOI18N
        jPanButton9.setToolTipText("Pan Southeast");
        jPanButton9.setAlignmentX(0.5F);
        jPanButton9.setBorderPainted(false);
        jPanButton9.setContentAreaFilled(false);
        jPanButton9.setDefaultCapable(false);
        jPanButton9.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jPanButton9.setMaximumSize(new java.awt.Dimension(40, 40));
        jPanButton9.setMinimumSize(new java.awt.Dimension(40, 40));
        jPanButton9.setPreferredSize(new java.awt.Dimension(40, 40));
        jPanButton9.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton9_pressed.png"))); // NOI18N
        jPanButton9.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/panButton9_rollover.png"))); // NOI18N
        jPanButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPanButton9ActionPerformed(evt);
            }
        });
        jPanButtonPanel.add(jPanButton9);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        navigationPanel3.add(jPanButtonPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        add(navigationPanel3, gridBagConstraints);

        navigationPanel2.setBackground(new java.awt.Color(224, 223, 227));
        navigationPanel2.setVisible(false);
        navigationPanel2.setLayout(new java.awt.BorderLayout());

        jHorizontalSlider1.setMaximum(180);
        jHorizontalSlider1.setMinimum(-180);
        jHorizontalSlider1.setValue(0);
        jHorizontalSlider1.setFocusable(false);
        jHorizontalSlider1.setMinimumSize(new java.awt.Dimension(144, 144));
        jHorizontalSlider1.setOpaque(false);
        jHorizontalSlider1.setPreferredSize(new java.awt.Dimension(144, 144));
        navigationPanel2.add(jHorizontalSlider1, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        add(navigationPanel2, gridBagConstraints);

        navigationPanel1.setBackground(new java.awt.Color(224, 223, 227));
        navigationPanel1.setVisible(false);
        navigationPanel1.setLayout(new java.awt.GridLayout(3, 3));

        jSphereButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_centered_1.png"))); // NOI18N
        jSphereButton1.setToolTipText("");
        jSphereButton1.setAlignmentX(0.5F);
        jSphereButton1.setBorderPainted(false);
        jSphereButton1.setContentAreaFilled(false);
        jSphereButton1.setFocusPainted(false);
        jSphereButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jSphereButton1.setMaximumSize(new java.awt.Dimension(48, 48));
        jSphereButton1.setMinimumSize(new java.awt.Dimension(48, 48));
        jSphereButton1.setPreferredSize(new java.awt.Dimension(48, 48));
        jSphereButton1.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_topleft_1.png"))); // NOI18N
        jSphereButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jSphereButton1MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSphereButton1MouseReleased(evt);
            }
        });
        navigationPanel1.add(jSphereButton1);

        jSphereButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_centered_2.png"))); // NOI18N
        jSphereButton2.setToolTipText("Set view background color");
        jSphereButton2.setAlignmentX(0.5F);
        jSphereButton2.setBorderPainted(false);
        jSphereButton2.setContentAreaFilled(false);
        jSphereButton2.setFocusPainted(false);
        jSphereButton2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jSphereButton2.setMaximumSize(new java.awt.Dimension(48, 48));
        jSphereButton2.setMinimumSize(new java.awt.Dimension(48, 48));
        jSphereButton2.setPreferredSize(new java.awt.Dimension(48, 48));
        jSphereButton2.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_top_2.png"))); // NOI18N
        jSphereButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jSphereButton2MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSphereButton2MouseReleased(evt);
            }
        });
        navigationPanel1.add(jSphereButton2);

        jSphereButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_centered_3.png"))); // NOI18N
        jSphereButton3.setToolTipText("Set view background color");
        jSphereButton3.setAlignmentX(0.5F);
        jSphereButton3.setBorderPainted(false);
        jSphereButton3.setContentAreaFilled(false);
        jSphereButton3.setFocusPainted(false);
        jSphereButton3.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jSphereButton3.setMaximumSize(new java.awt.Dimension(48, 48));
        jSphereButton3.setMinimumSize(new java.awt.Dimension(48, 48));
        jSphereButton3.setPreferredSize(new java.awt.Dimension(48, 48));
        jSphereButton3.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_topright_3.png"))); // NOI18N
        jSphereButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jSphereButton3MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSphereButton3MouseReleased(evt);
            }
        });
        navigationPanel1.add(jSphereButton3);

        jSphereButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_centered_4.png"))); // NOI18N
        jSphereButton4.setToolTipText("Set view background color");
        jSphereButton4.setAlignmentX(0.5F);
        jSphereButton4.setBorderPainted(false);
        jSphereButton4.setContentAreaFilled(false);
        jSphereButton4.setFocusPainted(false);
        jSphereButton4.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jSphereButton4.setMaximumSize(new java.awt.Dimension(48, 48));
        jSphereButton4.setMinimumSize(new java.awt.Dimension(48, 48));
        jSphereButton4.setPreferredSize(new java.awt.Dimension(48, 48));
        jSphereButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSphereButton4ActionPerformed(evt);
            }
        });
        navigationPanel1.add(jSphereButton4);

        jSphereButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_centered_5.png"))); // NOI18N
        jSphereButton5.setToolTipText("Set view background color");
        jSphereButton5.setAlignmentX(0.5F);
        jSphereButton5.setBorderPainted(false);
        jSphereButton5.setContentAreaFilled(false);
        jSphereButton5.setFocusPainted(false);
        jSphereButton5.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jSphereButton5.setMaximumSize(new java.awt.Dimension(48, 48));
        jSphereButton5.setMinimumSize(new java.awt.Dimension(48, 48));
        jSphereButton5.setPreferredSize(new java.awt.Dimension(48, 48));
        jSphereButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jSphereButton5MousePressed(evt);
            }
        });
        navigationPanel1.add(jSphereButton5);

        jSphereButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_centered_6.png"))); // NOI18N
        jSphereButton6.setToolTipText("Set view background color");
        jSphereButton6.setAlignmentX(0.5F);
        jSphereButton6.setBorderPainted(false);
        jSphereButton6.setContentAreaFilled(false);
        jSphereButton6.setFocusPainted(false);
        jSphereButton6.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jSphereButton6.setMaximumSize(new java.awt.Dimension(48, 48));
        jSphereButton6.setMinimumSize(new java.awt.Dimension(48, 48));
        jSphereButton6.setPreferredSize(new java.awt.Dimension(48, 48));
        jSphereButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSphereButton6ActionPerformed(evt);
            }
        });
        navigationPanel1.add(jSphereButton6);

        jSphereButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_centered_7.png"))); // NOI18N
        jSphereButton7.setToolTipText("Set view background color");
        jSphereButton7.setAlignmentX(0.5F);
        jSphereButton7.setBorderPainted(false);
        jSphereButton7.setContentAreaFilled(false);
        jSphereButton7.setFocusPainted(false);
        jSphereButton7.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jSphereButton7.setMaximumSize(new java.awt.Dimension(48, 48));
        jSphereButton7.setMinimumSize(new java.awt.Dimension(48, 48));
        jSphereButton7.setPreferredSize(new java.awt.Dimension(48, 48));
        jSphereButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSphereButton7ActionPerformed(evt);
            }
        });
        navigationPanel1.add(jSphereButton7);

        jSphereButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_centered_8.png"))); // NOI18N
        jSphereButton8.setToolTipText("Set view background color");
        jSphereButton8.setAlignmentX(0.5F);
        jSphereButton8.setBorderPainted(false);
        jSphereButton8.setContentAreaFilled(false);
        jSphereButton8.setFocusPainted(false);
        jSphereButton8.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jSphereButton8.setMaximumSize(new java.awt.Dimension(48, 48));
        jSphereButton8.setMinimumSize(new java.awt.Dimension(48, 48));
        jSphereButton8.setPreferredSize(new java.awt.Dimension(48, 48));
        jSphereButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSphereButton8ActionPerformed(evt);
            }
        });
        navigationPanel1.add(jSphereButton8);

        jSphereButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_centered_9.png"))); // NOI18N
        jSphereButton9.setToolTipText("Set view background color");
        jSphereButton9.setAlignmentX(0.5F);
        jSphereButton9.setBorderPainted(false);
        jSphereButton9.setContentAreaFilled(false);
        jSphereButton9.setFocusPainted(false);
        jSphereButton9.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jSphereButton9.setMaximumSize(new java.awt.Dimension(48, 48));
        jSphereButton9.setMinimumSize(new java.awt.Dimension(48, 48));
        jSphereButton9.setPreferredSize(new java.awt.Dimension(48, 48));
        jSphereButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSphereButton9ActionPerformed(evt);
            }
        });
        navigationPanel1.add(jSphereButton9);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(navigationPanel1, gridBagConstraints);

        openSimCanvas1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                openSimCanvas1MousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(openSimCanvas1, gridBagConstraints);

        toolBarPanel1.setBackground(new java.awt.Color(224, 223, 227));

        jModelWindowToolBar.setBorder(null);
        jModelWindowToolBar.setFloatable(false);
        jModelWindowToolBar.setOrientation(1);
        jModelWindowToolBar.setToolTipText("Drag to Preferred Edge or Float");
        jModelWindowToolBar.setMaximumSize(new java.awt.Dimension(20, 20));
        jModelWindowToolBar.setMinimumSize(new java.awt.Dimension(20, 20));
        jModelWindowToolBar.setOpaque(false);
        jModelWindowToolBar.setPreferredSize(new java.awt.Dimension(20, 220));

        jBackgroundColorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/backgroundColor.png"))); // NOI18N
        jBackgroundColorButton.setToolTipText("Set view background color");
        jBackgroundColorButton.setAlignmentX(0.5F);
        jBackgroundColorButton.setBorderPainted(false);
        jBackgroundColorButton.setContentAreaFilled(false);
        jBackgroundColorButton.setFocusPainted(false);
        jBackgroundColorButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jBackgroundColorButton.setMaximumSize(new java.awt.Dimension(20, 20));
        jBackgroundColorButton.setMinimumSize(new java.awt.Dimension(20, 20));
        jBackgroundColorButton.setPreferredSize(new java.awt.Dimension(20, 20));
        jBackgroundColorButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/backgroundColor_selected.png"))); // NOI18N
        jBackgroundColorButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/backgroundColor_rollover.png"))); // NOI18N
        jBackgroundColorButton.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/backgroundColor_rollover_selected.png"))); // NOI18N
        jBackgroundColorButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/backgroundColor_selected.png"))); // NOI18N
        jBackgroundColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBackgroundColorButtonActionPerformed(evt);
            }
        });
        jModelWindowToolBar.add(jBackgroundColorButton);

        jAxesToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/axes.png"))); // NOI18N
        jAxesToggleButton.setToolTipText("Show/Hide view axes");
        jAxesToggleButton.setAlignmentX(0.5F);
        jAxesToggleButton.setBorderPainted(false);
        jAxesToggleButton.setContentAreaFilled(false);
        jAxesToggleButton.setFocusPainted(false);
        jAxesToggleButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jAxesToggleButton.setMaximumSize(new java.awt.Dimension(20, 20));
        jAxesToggleButton.setMinimumSize(new java.awt.Dimension(20, 20));
        jAxesToggleButton.setPreferredSize(new java.awt.Dimension(20, 20));
        jAxesToggleButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/axes_selected.png"))); // NOI18N
        jAxesToggleButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/axes_rollover.png"))); // NOI18N
        jAxesToggleButton.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/axes_rollover_selected.png"))); // NOI18N
        jAxesToggleButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/axes_selected.png"))); // NOI18N
        jAxesToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAxesToggleButtonActionPerformed(evt);
            }
        });
        jModelWindowToolBar.add(jAxesToggleButton);

        jMinusXViewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/frontView_axes.png"))); // NOI18N
        jMinusXViewButton.setToolTipText("-X view");
        jMinusXViewButton.setAlignmentX(0.5F);
        jMinusXViewButton.setBorderPainted(false);
        jMinusXViewButton.setContentAreaFilled(false);
        jMinusXViewButton.setFocusPainted(false);
        jMinusXViewButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jMinusXViewButton.setMaximumSize(new java.awt.Dimension(20, 20));
        jMinusXViewButton.setMinimumSize(new java.awt.Dimension(20, 20));
        jMinusXViewButton.setPreferredSize(new java.awt.Dimension(20, 20));
        jMinusXViewButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/frontView_axes_selected.png"))); // NOI18N
        jMinusXViewButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/frontView_axes_rollover.png"))); // NOI18N
        jMinusXViewButton.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/frontView_axes_rollover_selected.png"))); // NOI18N
        jMinusXViewButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/frontView_axes_selected.png"))); // NOI18N
        jMinusXViewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMinusXViewButtonActionPerformed(evt);
            }
        });
        jModelWindowToolBar.add(jMinusXViewButton);

        jPlusXViewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/backView_axes.png"))); // NOI18N
        jPlusXViewButton.setToolTipText("+X view");
        jPlusXViewButton.setAlignmentX(0.5F);
        jPlusXViewButton.setBorderPainted(false);
        jPlusXViewButton.setContentAreaFilled(false);
        jPlusXViewButton.setFocusPainted(false);
        jPlusXViewButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jPlusXViewButton.setMaximumSize(new java.awt.Dimension(20, 20));
        jPlusXViewButton.setMinimumSize(new java.awt.Dimension(20, 20));
        jPlusXViewButton.setPreferredSize(new java.awt.Dimension(20, 20));
        jPlusXViewButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/backView_axes_selected.png"))); // NOI18N
        jPlusXViewButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/backView_axes_rollover.png"))); // NOI18N
        jPlusXViewButton.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/backView_axes_rollover_selected.png"))); // NOI18N
        jPlusXViewButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/backView_axes_selected.png"))); // NOI18N
        jPlusXViewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPlusXViewButtonActionPerformed(evt);
            }
        });
        jModelWindowToolBar.add(jPlusXViewButton);

        jMinusYViewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/topView_axes.png"))); // NOI18N
        jMinusYViewButton.setToolTipText("-Y view");
        jMinusYViewButton.setAlignmentX(0.5F);
        jMinusYViewButton.setBorderPainted(false);
        jMinusYViewButton.setContentAreaFilled(false);
        jMinusYViewButton.setFocusPainted(false);
        jMinusYViewButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jMinusYViewButton.setMaximumSize(new java.awt.Dimension(20, 20));
        jMinusYViewButton.setMinimumSize(new java.awt.Dimension(20, 20));
        jMinusYViewButton.setPreferredSize(new java.awt.Dimension(20, 20));
        jMinusYViewButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/topView_axes_selected.png"))); // NOI18N
        jMinusYViewButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/topView_axes_rollover.png"))); // NOI18N
        jMinusYViewButton.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/topView_axes_rollover_selected.png"))); // NOI18N
        jMinusYViewButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/topView_axes_selected.png"))); // NOI18N
        jMinusYViewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMinusYViewButtonActionPerformed(evt);
            }
        });
        jModelWindowToolBar.add(jMinusYViewButton);

        jPlusYViewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/bottomView_axes.png"))); // NOI18N
        jPlusYViewButton.setToolTipText("+Y view");
        jPlusYViewButton.setAlignmentX(0.5F);
        jPlusYViewButton.setBorderPainted(false);
        jPlusYViewButton.setContentAreaFilled(false);
        jPlusYViewButton.setFocusPainted(false);
        jPlusYViewButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jPlusYViewButton.setMaximumSize(new java.awt.Dimension(20, 20));
        jPlusYViewButton.setMinimumSize(new java.awt.Dimension(20, 20));
        jPlusYViewButton.setPreferredSize(new java.awt.Dimension(20, 20));
        jPlusYViewButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/bottomView_axes_selected.png"))); // NOI18N
        jPlusYViewButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/bottomView_axes_rollover.png"))); // NOI18N
        jPlusYViewButton.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/bottomView_axes_rollover_selected.png"))); // NOI18N
        jPlusYViewButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/bottomView_axes_selected.png"))); // NOI18N
        jPlusYViewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPlusYViewButtonActionPerformed(evt);
            }
        });
        jModelWindowToolBar.add(jPlusYViewButton);

        jMinusZViewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/rightView_axes.png"))); // NOI18N
        jMinusZViewButton.setToolTipText("-Z view");
        jMinusZViewButton.setAlignmentX(0.5F);
        jMinusZViewButton.setBorderPainted(false);
        jMinusZViewButton.setContentAreaFilled(false);
        jMinusZViewButton.setFocusPainted(false);
        jMinusZViewButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jMinusZViewButton.setMaximumSize(new java.awt.Dimension(20, 20));
        jMinusZViewButton.setMinimumSize(new java.awt.Dimension(20, 20));
        jMinusZViewButton.setPreferredSize(new java.awt.Dimension(20, 20));
        jMinusZViewButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/rightView_axes_selected.png"))); // NOI18N
        jMinusZViewButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/rightView_axes_rollover.png"))); // NOI18N
        jMinusZViewButton.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/rightView_axes_rollover_selected.png"))); // NOI18N
        jMinusZViewButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/rightView_axes_selected.png"))); // NOI18N
        jMinusZViewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMinusZViewButtonActionPerformed(evt);
            }
        });
        jModelWindowToolBar.add(jMinusZViewButton);

        jPlusZViewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/leftView_axes.png"))); // NOI18N
        jPlusZViewButton.setToolTipText("+Z view");
        jPlusZViewButton.setAlignmentX(0.5F);
        jPlusZViewButton.setBorderPainted(false);
        jPlusZViewButton.setContentAreaFilled(false);
        jPlusZViewButton.setFocusPainted(false);
        jPlusZViewButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jPlusZViewButton.setMaximumSize(new java.awt.Dimension(20, 20));
        jPlusZViewButton.setMinimumSize(new java.awt.Dimension(20, 20));
        jPlusZViewButton.setPreferredSize(new java.awt.Dimension(20, 20));
        jPlusZViewButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/leftView_axes_selected.png"))); // NOI18N
        jPlusZViewButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/leftView_axes_rollover.png"))); // NOI18N
        jPlusZViewButton.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/leftView_axes_rollover_selected.png"))); // NOI18N
        jPlusZViewButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/leftView_axes_selected.png"))); // NOI18N
        jPlusZViewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPlusZViewButtonActionPerformed(evt);
            }
        });
        jModelWindowToolBar.add(jPlusZViewButton);

        jTakeSnapshotButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/stillCamera.png"))); // NOI18N
        jTakeSnapshotButton.setToolTipText("Save view as image");
        jTakeSnapshotButton.setAlignmentX(0.5F);
        jTakeSnapshotButton.setBorderPainted(false);
        jTakeSnapshotButton.setContentAreaFilled(false);
        jTakeSnapshotButton.setFocusPainted(false);
        jTakeSnapshotButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jTakeSnapshotButton.setMaximumSize(new java.awt.Dimension(20, 20));
        jTakeSnapshotButton.setMinimumSize(new java.awt.Dimension(20, 20));
        jTakeSnapshotButton.setPreferredSize(new java.awt.Dimension(20, 20));
        jTakeSnapshotButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/stillCamera_selected.png"))); // NOI18N
        jTakeSnapshotButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/stillCamera_rollover.png"))); // NOI18N
        jTakeSnapshotButton.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/stillCamera_rollover_selected.png"))); // NOI18N
        jTakeSnapshotButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/stillCamera_selected.png"))); // NOI18N
        jTakeSnapshotButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTakeSnapshotButtonActionPerformed(evt);
            }
        });
        jModelWindowToolBar.add(jTakeSnapshotButton);

        jStartStopMovieToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/movieCamera.png"))); // NOI18N
        jStartStopMovieToggleButton.setToolTipText("Toggle save motion as movie");
        jStartStopMovieToggleButton.setAlignmentX(0.5F);
        jStartStopMovieToggleButton.setBorderPainted(false);
        jStartStopMovieToggleButton.setContentAreaFilled(false);
        jStartStopMovieToggleButton.setFocusPainted(false);
        jStartStopMovieToggleButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jStartStopMovieToggleButton.setMaximumSize(new java.awt.Dimension(20, 20));
        jStartStopMovieToggleButton.setMinimumSize(new java.awt.Dimension(20, 20));
        jStartStopMovieToggleButton.setPreferredSize(new java.awt.Dimension(20, 20));
        jStartStopMovieToggleButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/movieCamera_selected.png"))); // NOI18N
        jStartStopMovieToggleButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/movieCamera_rollover.png"))); // NOI18N
        jStartStopMovieToggleButton.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/movieCamera_rollover_selected.png"))); // NOI18N
        jStartStopMovieToggleButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/movieCamera_selected.png"))); // NOI18N
        jStartStopMovieToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jStartStopMovieToggleButtonActionPerformed(evt);
                jToggleButton1ActionPerformed(evt);
            }
        });
        jModelWindowToolBar.add(jStartStopMovieToggleButton);

        cameraEditorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/cameraDolly.png"))); // NOI18N
        cameraEditorButton.setToolTipText("Camera Dolly");
        cameraEditorButton.setAlignmentX(0.5F);
        cameraEditorButton.setBorderPainted(false);
        cameraEditorButton.setContentAreaFilled(false);
        cameraEditorButton.setFocusPainted(false);
        cameraEditorButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cameraEditorButton.setMaximumSize(new java.awt.Dimension(20, 20));
        cameraEditorButton.setMinimumSize(new java.awt.Dimension(20, 20));
        cameraEditorButton.setPreferredSize(new java.awt.Dimension(20, 20));
        cameraEditorButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/leftView_axes_selected.png"))); // NOI18N
        cameraEditorButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/cameraDolly_rollover.png"))); // NOI18N
        cameraEditorButton.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/cameraDolly_rollover_selected.png"))); // NOI18N
        cameraEditorButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/cameraDolly_selected.png"))); // NOI18N
        cameraEditorButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                cameraEditorButtonMousePressed(evt);
            }
        });
        jModelWindowToolBar.add(cameraEditorButton);

        jAnnotateToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/annotateDisarmed.png"))); // NOI18N
        jAnnotateToggleButton.setToolTipText("Annotate selection");
        jAnnotateToggleButton.setAlignmentX(0.5F);
        jAnnotateToggleButton.setBorderPainted(false);
        jAnnotateToggleButton.setContentAreaFilled(false);
        jAnnotateToggleButton.setFocusPainted(false);
        jAnnotateToggleButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jAnnotateToggleButton.setMaximumSize(new java.awt.Dimension(20, 20));
        jAnnotateToggleButton.setMinimumSize(new java.awt.Dimension(20, 20));
        jAnnotateToggleButton.setPreferredSize(new java.awt.Dimension(20, 20));
        jAnnotateToggleButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/annotateArmed.png"))); // NOI18N
        jAnnotateToggleButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/annotateArmed.png"))); // NOI18N
        jAnnotateToggleButton.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/annotateDisarmed.png"))); // NOI18N
        jAnnotateToggleButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/annotateArmed.png"))); // NOI18N
        jAnnotateToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAnnotateToggleButtonActionPerformed(evt);
                jAnnotateButtonActionPerformed(evt);
            }
        });
        jModelWindowToolBar.add(jAnnotateToggleButton);

        org.jdesktop.layout.GroupLayout toolBarPanel1Layout = new org.jdesktop.layout.GroupLayout(toolBarPanel1);
        toolBarPanel1.setLayout(toolBarPanel1Layout);
        toolBarPanel1Layout.setHorizontalGroup(
            toolBarPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jModelWindowToolBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        toolBarPanel1Layout.setVerticalGroup(
            toolBarPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(toolBarPanel1Layout.createSequentialGroup()
                .add(0, 0, 0)
                .add(jModelWindowToolBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 244, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        add(toolBarPanel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jAnnotateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAnnotateButtonActionPerformed
// TODO add your handling code here:
        javax.swing.JToggleButton src = (javax.swing.JToggleButton)evt.getSource();
        ViewDB.getInstance().setQuery(src.isSelected());
    }//GEN-LAST:event_jAnnotateButtonActionPerformed

    private void jAnnotateToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAnnotateToggleButtonActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_jAnnotateToggleButtonActionPerformed

    private void jJoystickSliderMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jJoystickSliderMouseReleased
// TODO add your handling code here:
        if(evt.getX()<50 && evt.getY()<50) {
            jPanButton1.dispatchEvent(SwingUtilities.convertMouseEvent(evt.getComponent(), evt, jPanButton1));
        }
    }//GEN-LAST:event_jJoystickSliderMouseReleased

    private void jJoystickSliderMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jJoystickSliderMousePressed
// TODO add your handling code here:
        if(evt.getX()<50 && evt.getY()<50) {
            jPanButton1.dispatchEvent(SwingUtilities.convertMouseEvent(evt.getComponent(), evt, jPanButton1));
        }
    }//GEN-LAST:event_jJoystickSliderMousePressed

    private void jJoystickSliderMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jJoystickSliderMouseMoved
// TODO add your handling code here:
        System.out.println("evt.getX() = "+evt.getX()+", evt.getY() = "+evt.getY());
        if(evt.getX()<50 && evt.getY()<50) {
            jPanButton1.dispatchEvent(SwingUtilities.convertMouseEvent(evt.getComponent(), evt, jPanButton1));
        }
    }//GEN-LAST:event_jJoystickSliderMouseMoved

    private void jPanButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPanButton9ActionPerformed
// TODO add your handling code here:
        openSimCanvas1.panCamera(5.0,5.0);
        openSimCanvas1.repaint();
    }//GEN-LAST:event_jPanButton9ActionPerformed

    private void jPanButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPanButton8ActionPerformed
// TODO add your handling code here:
        openSimCanvas1.panCamera(0.0,5.0);
        openSimCanvas1.repaint();
    }//GEN-LAST:event_jPanButton8ActionPerformed

    private void jPanButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPanButton7ActionPerformed
// TODO add your handling code here:
        openSimCanvas1.panCamera(-5.0,5.0);
        openSimCanvas1.repaint();
    }//GEN-LAST:event_jPanButton7ActionPerformed

    private void jPanButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPanButton6ActionPerformed
// TODO add your handling code here:
        openSimCanvas1.panCamera(5.0,0.0);
        openSimCanvas1.repaint();
    }//GEN-LAST:event_jPanButton6ActionPerformed

    private void jPanButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPanButton4ActionPerformed
// TODO add your handling code here:
        openSimCanvas1.panCamera(-5.0,0.0);
        openSimCanvas1.repaint();
    }//GEN-LAST:event_jPanButton4ActionPerformed

    private void jPanButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPanButton3ActionPerformed
// TODO add your handling code here:
        openSimCanvas1.panCamera(5.0,-5.0);
        openSimCanvas1.repaint();
    }//GEN-LAST:event_jPanButton3ActionPerformed

    private void jPanButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPanButton2ActionPerformed
// TODO add your handling code here:
        openSimCanvas1.panCamera(0.0,-5.0);
        openSimCanvas1.repaint();
    }//GEN-LAST:event_jPanButton2ActionPerformed

    private void jPanButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPanButton1ActionPerformed
// TODO add your handling code here:
        openSimCanvas1.panCamera(-5.0,-5.0);
        openSimCanvas1.repaint();
    }//GEN-LAST:event_jPanButton1ActionPerformed

    private void openSimCanvas1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_openSimCanvas1MousePressed
// TODO add your handling code here:
        if ((evt.getModifiers() == (InputEvent.BUTTON1_MASK))) {
            deselectViewButtons();
        }
    }//GEN-LAST:event_openSimCanvas1MousePressed

    private void jSphereButton5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSphereButton5MousePressed
// TODO add your handling code here:
        recenterSphere();
    }//GEN-LAST:event_jSphereButton5MousePressed

    private void jSphereButton3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSphereButton3MousePressed
// TODO add your handling code here:
        jSphereButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_topright_1.png")));
        jSphereButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_topright_2.png")));
        //jSphereButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_topright_3.png")));
        jSphereButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_topright_4.png")));
        jSphereButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_topright_5.png")));
        jSphereButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_topright_6.png")));
        jSphereButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_topright_7.png")));
        jSphereButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_topright_8.png")));
        jSphereButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_topright_9.png")));
    }//GEN-LAST:event_jSphereButton3MousePressed

    private void jSphereButton2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSphereButton2MousePressed
// TODO add your handling code here:
        jSphereButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_top_1.png")));
        //jSphereButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_top_2.png")));
        jSphereButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_top_3.png")));
        jSphereButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_top_4.png")));
        jSphereButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_top_5.png")));
        jSphereButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_top_6.png")));
        jSphereButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_top_7.png")));
        jSphereButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_top_8.png")));
        jSphereButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_top_9.png")));
    }//GEN-LAST:event_jSphereButton2MousePressed

    private void jSphereButton1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSphereButton1MousePressed
// TODO add your handling code here:
        //jSphereButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_topleft_1.png")));
        jSphereButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_topleft_2.png")));
        jSphereButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_topleft_3.png")));
        jSphereButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_topleft_4.png")));
        jSphereButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_topleft_5.png")));
        jSphereButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_topleft_6.png")));
        jSphereButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_topleft_7.png")));
        jSphereButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_topleft_8.png")));
        jSphereButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_topleft_9.png")));
    }//GEN-LAST:event_jSphereButton1MousePressed

    private void jSphereButton3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSphereButton3MouseReleased
// TODO add your handling code here:
        //recenterSphere();
    }//GEN-LAST:event_jSphereButton3MouseReleased

    private void jSphereButton2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSphereButton2MouseReleased
// TODO add your handling code here:
        //recenterSphere();
    }//GEN-LAST:event_jSphereButton2MouseReleased

    private void jSphereButton1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSphereButton1MouseReleased
// TODO add your handling code here:
        //recenterSphere();
    }//GEN-LAST:event_jSphereButton1MouseReleased

    private void jSphereButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSphereButton9ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_jSphereButton9ActionPerformed

    private void jSphereButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSphereButton8ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_jSphereButton8ActionPerformed

    private void jSphereButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSphereButton7ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_jSphereButton7ActionPerformed

    private void jSphereButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSphereButton6ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_jSphereButton6ActionPerformed

    private void jSphereButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSphereButton4ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_jSphereButton4ActionPerformed

    private void cameraEditorButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cameraEditorButtonMousePressed
      JPopupMenu cameraPopup = new JPopupMenu();
      JRadioButtonMenuItem item = null;

      // null camera
      item = new JRadioButtonMenuItem(new SetCameraAction(null));
      if(getCanvas().getCamera()==null) item.setSelected(true);
      cameraPopup.add(item);

      for(int i=0; i<CameraDB.getInstance().getNumCameras(); i++) {
         Camera camera = CameraDB.getInstance().getCamera(i);
         item = new JRadioButtonMenuItem(new SetCameraAction(camera));
         if(camera.equals(getCanvas().getCamera())) item.setSelected(true);
         cameraPopup.add(item);
      }

      cameraPopup.addSeparator();
      cameraPopup.add(new JMenuItem(new CameraEditorAction()));
      cameraPopup.show(evt.getComponent(), evt.getX(), evt.getY());
    }//GEN-LAST:event_cameraEditorButtonMousePressed

    private void jBackgroundColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBackgroundColorButtonActionPerformed
// TODO add your handling code here:
        JColorChooser backgroundColorChooser = new JColorChooser();
        OpenSimCanvas dCanvas = ViewDB.getInstance().getCurrentModelWindow().getCanvas();
        Color newColor = backgroundColorChooser.showDialog(dCanvas, "Select new background color", dCanvas.getBackground());
        if (newColor != null){
             float[] colorComponents = newColor.getRGBComponents(null);
             dCanvas.GetRenderer().SetBackground(colorComponents[0], colorComponents[1], colorComponents[2]);
             String defaultBackgroundColor=String.valueOf(colorComponents[0])+", "+
                     String.valueOf(colorComponents[1])+", "+
                     String.valueOf(colorComponents[2]);
             Preferences.userNodeForPackage(TheApp.class).put("BackgroundColor", defaultBackgroundColor);
             synchronizeBackgroundColor();
             dCanvas.repaint();
        }
    }//GEN-LAST:event_jBackgroundColorButtonActionPerformed

    private void jAxesToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAxesToggleButtonActionPerformed
// TODO add your handling code here:
        if (ViewDB.getInstance().isAxesDisplayed()) {
            ViewDB.getInstance().showAxes(false);
            // correct selected mode
            jAxesToggleButton.setSelected(false);
        }
        else {
            ViewDB.getInstance().showAxes(true);
            // correct selected mode
            jAxesToggleButton.setSelected(true);
        }
    }//GEN-LAST:event_jAxesToggleButtonActionPerformed

    private void jPlusXViewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPlusXViewButtonActionPerformed
// TODO add your handling code here:
        openSimCanvas1.applyCameraPlusX();
        // correct selected modes
        deselectViewButtons();
        jPlusXViewButton.setSelected(true);
    }//GEN-LAST:event_jPlusXViewButtonActionPerformed

    private void jPlusYViewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPlusYViewButtonActionPerformed
// TODO add your handling code here:
        openSimCanvas1.applyCameraPlusY();
        // correct selected modes
        deselectViewButtons();
        jPlusYViewButton.setSelected(true);
    }//GEN-LAST:event_jPlusYViewButtonActionPerformed

    private void jMinusZViewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMinusZViewButtonActionPerformed
// TODO add your handling code here:
        openSimCanvas1.applyCameraMinusZ();
        // correct selected modes
        deselectViewButtons();
        jMinusZViewButton.setSelected(true);       
    }//GEN-LAST:event_jMinusZViewButtonActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jStartStopMovieToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jStartStopMovieToggleButtonActionPerformed
// TODO add your handling code here:
        javax.swing.JToggleButton btn = (javax.swing.JToggleButton)(evt.getSource());
        if (btn.getModel().isSelected()){
            String fileName = FileUtils.getInstance().browseForFilename(".avi", "Movie file to create", false);
            //System.out.println("Create movie to file"+fileName);
            if (fileName!=null){
                // Append .avi to the end if not done by user
                if (!fileName.endsWith(".avi"))
                    fileName = fileName+".avi";
                getCanvas().createMovie(fileName);
               // correct selected mode
               jStartStopMovieToggleButton.setSelected(true);
            }
            else {
                btn.getModel().setSelected(false);
                btn.getModel().setArmed(false);
            }
        }
        else {
            getCanvas().finishMovie();
            //System.out.println("Finish movie");
            // correct selected mode
            jStartStopMovieToggleButton.setSelected(false);
        }
    }//GEN-LAST:event_jStartStopMovieToggleButtonActionPerformed

    private void jPlusZViewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPlusZViewButtonActionPerformed
// TODO add your handling code here:
        openSimCanvas1.applyCameraPlusZ();
        // correct selected modes
        deselectViewButtons();
        jPlusZViewButton.setSelected(true);
    }//GEN-LAST:event_jPlusZViewButtonActionPerformed

    private void jMinusXViewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMinusXViewButtonActionPerformed
// TODO add your handling code here:
        openSimCanvas1.applyCameraMinusX();
        // correct selected modes
        deselectViewButtons();
        jMinusXViewButton.setSelected(true);      
    }//GEN-LAST:event_jMinusXViewButtonActionPerformed

   private void jMinusYViewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMinusYViewButtonActionPerformed
// TODO add your handling code here:
        openSimCanvas1.applyCameraMinusY();
        // correct selected modes
        deselectViewButtons();
        jMinusYViewButton.setSelected(true);    
   }//GEN-LAST:event_jMinusYViewButtonActionPerformed

    private void jTakeSnapshotButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTakeSnapshotButtonActionPerformed
// TODO add your handling code here:
       String defaultDir="";
       defaultDir = Preferences.userNodeForPackage(TheApp.class).get("WorkDirectory", defaultDir);
        final JFileChooser dlog = new JFileChooser(defaultDir);
        
        if (dlog.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            String fullPath = dlog.getSelectedFile().getAbsolutePath();
            if (! fullPath.toLowerCase().endsWith(".tiff")){
                fullPath = dlog.getSelectedFile().getAbsolutePath()+".tiff";
            }
            getCanvas().HardCopy(fullPath, 1);
        }
    }//GEN-LAST:event_jTakeSnapshotButtonActionPerformed

    private void jRefitModelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRefitModelButtonActionPerformed
// TODO add your handling code here:
        getCanvas().resetCamera();
        getCanvas().Render();
    }//GEN-LAST:event_jRefitModelButtonActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cameraEditorButton;
    private javax.swing.JToggleButton jAnnotateToggleButton;
    private javax.swing.JToggleButton jAxesToggleButton;
    private javax.swing.JButton jBackgroundColorButton;
    private javax.swing.JSlider jHorizontalSlider1;
    private javax.swing.JSlider jJoystickSlider;
    private javax.swing.JButton jMinusXViewButton;
    private javax.swing.JButton jMinusYViewButton;
    private javax.swing.JButton jMinusZViewButton;
    private javax.swing.JToolBar jModelWindowToolBar;
    private javax.swing.JButton jPanButton1;
    private javax.swing.JButton jPanButton2;
    private javax.swing.JButton jPanButton3;
    private javax.swing.JButton jPanButton4;
    private javax.swing.JButton jPanButton5;
    private javax.swing.JButton jPanButton6;
    private javax.swing.JButton jPanButton7;
    private javax.swing.JButton jPanButton8;
    private javax.swing.JButton jPanButton9;
    private javax.swing.JPanel jPanButtonPanel;
    private javax.swing.JButton jPlusXViewButton;
    private javax.swing.JButton jPlusYViewButton;
    private javax.swing.JButton jPlusZViewButton;
    private javax.swing.JButton jRefitModelButton;
    private javax.swing.JButton jSphereButton1;
    private javax.swing.JButton jSphereButton2;
    private javax.swing.JButton jSphereButton3;
    private javax.swing.JButton jSphereButton4;
    private javax.swing.JButton jSphereButton5;
    private javax.swing.JButton jSphereButton6;
    private javax.swing.JButton jSphereButton7;
    private javax.swing.JButton jSphereButton8;
    private javax.swing.JButton jSphereButton9;
    private javax.swing.JToggleButton jStartStopMovieToggleButton;
    private javax.swing.JButton jTakeSnapshotButton;
    private java.awt.Panel navigationPanel1;
    private java.awt.Panel navigationPanel2;
    private java.awt.Panel navigationPanel3;
    private org.opensim.view.OpenSimCanvas openSimCanvas1;
    private java.awt.Panel toolBarPanel1;
    // End of variables declaration//GEN-END:variables
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }
        
    public String preferredID() {
        return "Model";
    }     
    
    public String getDisplayName()
    {
        return tabDisplayName;
    }
    
    /**
     * Potentially there could be multiple canvases inserted into this top component,
     * Use an accessor method just incase 
     */
    public org.opensim.view.OpenSimCanvas getCanvas() {
        return openSimCanvas1;
    }
    
    public Action[] getActions(){
        // Note: seem to need to do it this way rather than just new ViewReplicateAction(), etc. because java complains otherwise
        // about creating multiple instances of a shared object.
        ViewReplicateAction act1 = (ViewReplicateAction) SharedClassObject.findObject(ViewReplicateAction.class, true);   // New...
        ViewEditAction act2 = (ViewEditAction) SharedClassObject.findObject(ViewEditAction.class, true); //Edit...
                         
        return (new Action[]{act1,act2});
    };

    protected void componentActivated() {
        super.componentActivated();
        ViewDB.getInstance().setCurrentModelWindow(this);
    }

    /**
     * Window closing, remove self from ViewDB
     **/
    protected void componentClosed() {
        super.componentClosed();
        ViewDB.getInstance().removeWindow(this);
    }

    public void setTabDisplayName(String tabDisplayName) {
        this.tabDisplayName = tabDisplayName;
    }
    
    public void deselectViewButtons() 
    {
        jMinusZViewButton.setSelected(false);
        jPlusZViewButton.setSelected(false);
        jPlusYViewButton.setSelected(false);
        jMinusYViewButton.setSelected(false);
        jPlusXViewButton.setSelected(false);
        jMinusXViewButton.setSelected(false);
    }

    public void synchronizeBackgroundColor()
    {
        // Get userBackgroundColor
        String userBackgroundColor="0.0, 0.0, 0.0";
        userBackgroundColor = prefs.get("BackgroundColor", userBackgroundColor);
        double[] background = Prefs.parseColor(userBackgroundColor);
        
        // Set toolBarPanel1 background color
        toolBarPanel1.setBackground(new java.awt.Color((int) (background[0]*255), (int) (background[1]*255), (int) (background[2]*255)));
        
        // Set navigationPanel1 background color
        navigationPanel1.setBackground(new java.awt.Color((int) (background[0]*255), (int) (background[1]*255), (int) (background[2]*255)));
        
        // Set navigationPanel2 background color
        navigationPanel2.setBackground(new java.awt.Color((int) (background[0]*255), (int) (background[1]*255), (int) (background[2]*255)));

        // Set navigationPanel2 background color
        navigationPanel3.setBackground(new java.awt.Color((int) (background[0]*255), (int) (background[1]*255), (int) (background[2]*255)));
        
        // Set jPanButtonPanel background color
        jPanButtonPanel.setBackground(new java.awt.Color((int) (background[0]*255), (int) (background[1]*255), (int) (background[2]*255)));
    }
    
    public void recenterSphere()
    {
        jSphereButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_centered_1.png")));
        jSphereButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_centered_2.png")));
        jSphereButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_centered_3.png")));
        jSphereButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_centered_4.png")));
        jSphereButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_centered_5.png")));
        jSphereButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_centered_6.png")));
        jSphereButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_centered_7.png")));
        jSphereButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_centered_8.png")));
        jSphereButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensim/view/sphere_centered_9.png")));
    }

    public void stateChanged(ChangeEvent e) 
    {
        if (e.getSource().equals(openSimCanvas1) && !internalTrigger){
             internalTrigger=true;
             vtkCamera cam = ViewDB.getInstance().getCurrentModelWindow().getCanvas().GetRenderer().GetActiveCamera();
             double rollAngle = cam.GetRoll();
//             jHorizontalSlider1.setValue((int) rollAngle);
//             jJoystickSlider.setValue((int) rollAngle);
             jAnnotateToggleButton.setSelected(ViewDB.getInstance().isQuery());
             internalTrigger=false;
        } else if (e.getSource().equals(jHorizontalSlider1) && !internalTrigger){
            internalTrigger=true;
            int sliderValue = jHorizontalSlider1.getValue();
            vtkCamera cam = ViewDB.getInstance().getCurrentModelWindow().getCanvas().GetRenderer().GetActiveCamera();
            double rollAngle = cam.GetRoll() + (double) sliderValue;
            cam.SetRoll((double) sliderValue);
            ViewDB.getInstance().getCurrentModelWindow().getCanvas().repaint();
            internalTrigger=false;
        } else if (e.getSource().equals(jJoystickSlider) && !internalTrigger){
            internalTrigger=true;
            deselectViewButtons();
            OpenSimJoystickSliderUI jJoystickSliderUI = (OpenSimJoystickSliderUI) jJoystickSlider.getUI();
            Component source = ViewDB.getInstance().getCurrentModelWindow().getCanvas().getComponentAt(0,0);
            int id = 506;
            long when = System.currentTimeMillis();
            int modifiers = 1 << 4;
            int x = jJoystickSliderUI.getChangeX()+openSimCanvas1.getLastX();
            int y = jJoystickSliderUI.getChangeY()+openSimCanvas1.getLastY();
            int clickCount = 0;
            boolean popupTrigger = false;
            int button = 0;
            MouseEvent me = new MouseEvent(source, id, when, modifiers, x, y, clickCount, popupTrigger, button);
            openSimCanvas1.InteractionModeRotate();
            openSimCanvas1.mouseDragged(me);
            internalTrigger=false;
        }
    }
    public double[] getCameraAttributes() {
        double[] attributes = new double[13];
        vtkCamera dCamera=getCanvas().GetRenderer().GetActiveCamera();
        double[] temp = dCamera.GetPosition();
        for(int i=0; i<3; i++)
            attributes[i]=temp[i];
        temp = dCamera.GetFocalPoint();
        for(int i=0; i<3; i++)
            attributes[3+i]=temp[i];
        temp = dCamera.GetViewUp();
        for(int i=0; i<3; i++)
            attributes[6+i]=temp[i];
        temp = dCamera.GetViewPlaneNormal();
        for(int i=0; i<3; i++)
            attributes[9+i]=temp[i];
        attributes[12]=dCamera.GetViewAngle();
        vtkMatrix4x4 orientation = dCamera.GetViewTransformMatrix();
        return attributes;
    }
    
    public void applyCameraAttributes(double[] cameraAttributes) {
        vtkCamera dCamera=getCanvas().GetRenderer().GetActiveCamera();
        dCamera.SetPosition(cameraAttributes[0], cameraAttributes[1], cameraAttributes[2]);
        dCamera.SetFocalPoint(cameraAttributes[3], cameraAttributes[4], cameraAttributes[5]);
        dCamera.SetViewUp(cameraAttributes[6], cameraAttributes[7], cameraAttributes[8]);
        dCamera.SetViewPlaneNormal(cameraAttributes[9], cameraAttributes[10], cameraAttributes[11]);
        dCamera.SetViewAngle(cameraAttributes[12]);
        dCamera.Modified();
        getCanvas().GetRenderer().ResetCameraClippingRange();
        //vtkLightCollection lights = getCanvas().GetRenderer().GetLights();
        //lights.RemoveAllItems();
        //getCanvas().GetRenderer().CreateLight();
    }

    public UndoRedo getUndoRedo() {
        return ExplorerTopComponent.getDefault().getUndoRedo();
    }
}
