/* -------------------------------------------------------------------------- *
 * OpenSim: JPlotterQuantitySelector.java                                     *
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
 * JPlotterQuantitySelectorPopupGridLayout
 * Author(s): Ayman Habib
 */
package org.opensim.plotter;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.regex.Pattern;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.Model;
import org.opensim.modeling.Storage;
import org.opensim.utils.DialogUtils;
import org.opensim.utils.OpenSimDialog;
import org.opensim.view.SingleModelGuiElements;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman, this class is responsible for showing and arranging 
 * the set of quantities to be displayed for user selection in the plotter
 * A popup with list of Files is shown ad on user pick a dialog is shown for 
 * picking quantities.
 */
public class JPlotterQuantitySelector  {
   private Storage storageToUse;
   private String columnToUse;
   private JTextField   selection;
   private double xMin, xMax;
   boolean isDomain=false;
   private JPlotterPanel plotterPanel;
   public final String COLUMN_SEPARATOR=", ";
   public final String INTERVAL_SEPARATOR="-";

   /**
    * Creates a new instance of JPlotterQuantitySelector
    */
   public JPlotterQuantitySelector(JTextField target, JPlotterPanel plotterPanel, boolean isDomain) {
      this.plotterPanel = plotterPanel;
      this.isDomain=isDomain;
      selection=target;
      setXMin(0.0);
      setXMax(1.0);
   }
   /**
    * Update the list of menu items in the popup based on current contents of the plotterModel
    * and a flag indicating where the sources to consider are files or Analyses 
    */
   public void updateList(PlotterModel plotterModel, PlotterSourceInterface aSource) {
       if (aSource instanceof PlotterSourceFile){ 
           ArrayList<PlotterSourceFile> usedFileSources = plotterModel.getLoadedFileSources();
           // Add a submenu for each loaded file
           for(int i=0; i<usedFileSources.size(); i++){
               final PlotterSourceFile source = usedFileSources.get(i);
               final Storage nextStorage = source.getStorage();
               JMenuItem selectedFileMenuItem = new JMenuItem(source.getDisplayName()+"...");
               selectedFileMenuItem.addActionListener(new ActionListener(){
                   public void actionPerformed(ActionEvent e) {
                       // Create panel then the dialog that contains it
                       QuantitySelectionPanel quantityPanel = new QuantitySelectionPanel(JPlotterQuantitySelector.this, source,
                               ".*", isDomain);
                       DialogDescriptor dlg = new DialogDescriptor(quantityPanel,"Select Quantity");
                       dlg.setModal(true);
                       DialogDisplayer.getDefault().createDialog(dlg).setVisible(true);
                       Object userInput = dlg.getValue();
                       if (((Integer)userInput).compareTo((Integer)DialogDescriptor.OK_OPTION)==0){
                           String[] columnNames=quantityPanel.getSelected();
                           if (columnNames==null)
                              return;  // Nothing was selected.
                           String columnNamesDisplayString="";
                           for(int sel=0; sel<columnNames.length; sel++){
                               columnNamesDisplayString += columnNames[sel];
                               if (sel < columnNames.length-1)
                                   columnNamesDisplayString+= COLUMN_SEPARATOR;
                           }
                           selection.setText(columnNamesDisplayString);
                       }
                       plotterPanel.updatePlotterWithSelection();
                       
                   }});
            }
       }
       else if (aSource instanceof PlotterSourceMotion){ 
           ArrayList<PlotterSourceMotion> useMotionSources = plotterModel.getLoadedMotionSources();
           // Add a submenu for each loaded file
           for(int i=0; i<useMotionSources.size(); i++){
               final PlotterSourceMotion source = useMotionSources.get(i);
               final Storage nextStorage = source.getStorage();
               JMenuItem selectedFileMenuItem = new JMenuItem(source.getDisplayName()+"...");
               selectedFileMenuItem.addActionListener(new ActionListener(){
                   public void actionPerformed(ActionEvent e) {
                       // Create panel then the dialog that contains it
                       QuantitySelectionPanel quantityPanel = new QuantitySelectionPanel(JPlotterQuantitySelector.this, source,
                               ".*", isDomain);
                       DialogDescriptor dlg = new DialogDescriptor(quantityPanel,"Select Quantity");
                       dlg.setModal(true);
                       DialogDisplayer.getDefault().createDialog(dlg).setVisible(true);
                       Object userInput = dlg.getValue();
                       if (((Integer)userInput).compareTo((Integer)DialogDescriptor.OK_OPTION)==0){
                           String[] columnNames=quantityPanel.getSelected();
                           if (columnNames==null)
                              return;  // Nothing was selected.
                           String columnNamesDisplayString="";
                           for(int sel=0; sel<columnNames.length; sel++){
                               columnNamesDisplayString += columnNames[sel];
                               if (sel < columnNames.length-1)
                                   columnNamesDisplayString+= COLUMN_SEPARATOR;
                           }
                           selection.setText(columnNamesDisplayString);
                       }
                       plotterPanel.updatePlotterWithSelection();
                       
                   }});
            }
       }
       else {   //Analysis source, create a list 
           // Get a dropdown of coordinate names
           Model currentModel = OpenSimDB.getInstance().getCurrentModel();
           SingleModelGuiElements guiElem = OpenSimDB.getInstance().getModelGuiElements(currentModel);
           String[] coordinateNames = guiElem.getCoordinateNames();
           for(int i=0; i<coordinateNames.length; i++){
               final String coordinateName = coordinateNames[i];
               JMenuItem selectedGCItem = new JMenuItem(coordinateName);
               selectedGCItem.addActionListener(new ActionListener(){
                   public void actionPerformed(ActionEvent e) {
                       selection.setText("Model:"+coordinateName);
                       plotterPanel.updatePlotterWithSelection();
                   }});
             }
           /*
           ArrayList<PlotterSourceAnalysis> usedAnalysisSources = plotterModel.getAnalysisSources();
           // Add a submenu for each loaded file
           for(int i=0; i<usedAnalysisSources.size(); i++){
               final PlotterSourceAnalysis source = usedAnalysisSources.get(i);
               final Storage nextStorage = source.getStorage();
               JMenuItem selectedFileMenuItem = new JMenuItem("Analysis:"+source.getDisplayName()+"...");
               selectedFileMenuItem.addActionListener(new ActionListener(){
                   public void actionPerformed(ActionEvent e) {
                       // Create panel then the dialog that contains it
                       QuantitySelectionPanel quantityPanel = new QuantitySelectionPanel(source,
                               plotterPanel.getQuantityFilterRegex(), isDomain);
                       DialogDescriptor dlg = new DialogDescriptor(quantityPanel,"Select Quantity");
                       dlg.setModal(true);
                       DialogDisplayer.getDefault().createDialog(dlg).setVisible(true);
                       Object userInput = dlg.getValue();
                       if (((Integer)userInput).compareTo((Integer)DialogDescriptor.OK_OPTION)==0){
                           String[] columnNames=quantityPanel.getSelected();
                           String columnNamesDisplayString="";
                           for(int sel=0; sel<columnNames.length; sel++){
                               columnNamesDisplayString += columnNames[sel];
                               if (sel < columnNames.length-1)
                                   columnNamesDisplayString+= COLUMN_SEPARATOR;
                           }
                           selection.setText(source.getDisplayName()+":"+columnNamesDisplayString);
                       }
                       plotterPanel.updatePlotterWithSelection();
                       
                   }});
                   add(selectedFileMenuItem);
           }*/
           
       }
   }
  
   public Storage getStorageToUse() {
      return storageToUse;
   }

   public void setStorageToUse(Storage storageToUse) {
      this.storageToUse = storageToUse;
   }

   public String getColumnToUse() {
      return columnToUse;
   }

   public void setColumnToUse(String columnToUse) {
      this.columnToUse = columnToUse;
   }

   public double getXMin() {
      return xMin;
   }

   public void setXMin(double xMin) {
      this.xMin = xMin;
   }

   public double getXMax() {
      return xMax;
   }

   public void setXMax(double xMax) {
      this.xMax = xMax;
   }

   void showSingleSelectionPanel(PlotterModel plotterModel, PlotterSourceInterface sourceX, Frame ownerFrame) {
      QuantitySelectionPanel singleSelectPanel = new QuantitySelectionPanel(this, sourceX, ".*", true);
      //DialogDescriptor dlg = new DialogDescriptor(singleSelectPanel,"Select X Quantity");
      //dlg.setModal(true);
      //DialogDisplayer.getDefault().createDialog(dlg).setVisible(true);
      OpenSimDialog selectionDlg=DialogUtils.createDialogForPanelWithParent(ownerFrame, singleSelectPanel, "Select X Quantity");
      DialogUtils.addStandardButtons(selectionDlg);
      selectionDlg.setModal(true);
      selectionDlg.setVisible(true);
      if (selectionDlg.getDialogReturnValue()==selectionDlg.OK_OPTION){
         String[] selected = singleSelectPanel.getSelected();
         if (selected!=null){
            selection.setText(selected[0]);
            setColumnToUse(selected[0]);
         }
      }
   }

}
