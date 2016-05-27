/*
 *
 * JPlotterQuantitySelectorPopupGridLayout
 * Author(s): Ayman Habib
 * Copyright (c)  2005-2006, Stanford University, Ayman Habib
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
