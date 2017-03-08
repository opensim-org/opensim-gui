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
/*
 * JPlotterPanel.java
 *
 * Created on April 9, 2007, 1:39 PM
 */

package org.opensim.plotter;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.text.NumberFormatter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYDataset;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.opensim.modeling.*;
import org.opensim.utils.*;
import org.opensim.view.motions.MotionEvent;
import org.opensim.view.motions.MotionTimeChangeEvent;
import org.opensim.view.motions.MotionsDB;
import org.opensim.view.ModelEvent;
import org.opensim.view.ObjectSetCurrentEvent;
import org.opensim.view.ObjectsRenamedEvent;
import org.opensim.view.SingleModelGuiElements;
import org.opensim.view.experimentaldata.ModelForExperimentalData;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;
/**
 *
 * @author  Ayman
 */
public class JPlotterPanel extends javax.swing.JPanel
        implements java.awt.event.ActionListener, javax.swing.event.TreeSelectionListener, java.awt.event.FocusListener, java.util.Observer, java.beans.PropertyChangeListener, java.awt.event.InputMethodListener, java.awt.event.MouseListener {
   
    private Frame frame;   // We keep track of frame so that child windows can use it as parent.
   // PlotterModel kkeps track of two things
   // 1. For motions and free files, a PlotterSourceInterface is created and maintained so that
   //      files don't need to be re read/parsed'
   // 2. It serves as the model backing the tree of plots/figures.
   private PlotterModel plotterModel = new PlotterModel();

   private HashSet<String> qNameSet = new HashSet<String>();
   
    public void setTitle(String title) {
        getChartPanel().getChart().setTitle(title);
    }

    public PlotCurve showMotionCurve(String qName, String muscleName, PlotterSourceMotion dataSource) {
       PlotCurve plotCurve=null;
       openSimContext = OpenSimDB.getInstance().getContext(currentModel);
       if (qName.toLowerCase().startsWith("moment")){
           String coordName = qName.substring(qName.indexOf(".")+1);
           // Could be a moment or momentArm plot
           useMuscles(true);
           if (!currentModel.getCoordinateSet().contains(coordName)){
               ErrorDialog.showMessageDialog("Invalid coordinate specification: "+coordName+", please check and retry.");
               return null;
           }               
           if (qName.toLowerCase().startsWith("momentarm")){
               sourceY=(new PlotterSourceAnalysis(currentModel, plotterModel.getStorage("MomentArm_"+coordName, currentModel), "moment arm"));
           }
           else {   // Just Moment
               sourceY=(new PlotterSourceAnalysis(currentModel, plotterModel.getStorage("Moment_"+coordName, currentModel), "moment"));               
           }
           if (!currentModel.getMuscles().contains(muscleName)){
               ErrorDialog.showMessageDialog("Invalid specification of muscle: "+muscleName+" please check and retry.");
                return null;
           }
       }
       else if (currentModel.getMuscles().contains(qName) || qNameSet.contains(qName.toLowerCase()))
            populateYQty(qName);
       else {
            ErrorDialog.showMessageDialog("Invalid specification of quantity and/or muscle: "+qName+", "+muscleName+" please check and retry.");
            return null;
       }
       sourceX = dataSource;
       domainName = dataSource.getDisplayName();
       //populateXQty(dataSource.getDisplayName());
       rangeNames = new String[]{muscleName};
       jPlotterAddCurveButtonActionPerformed(null);
       plotCurve = currentCurve;
       refreshPanel(plotCurve, muscleName);
       return plotCurve;
    }

    private void refreshPanel(PlotCurve plotCurve, String muscleName) {
        plotCurve.setLegend(muscleName);
        plotCurve.getCurveSeries().fireSeriesChanged();
        plotterModel.fireChangeEvent(getNodeForCurve(plotCurve));
    }

    public PlotCurve showFunctionCurve(Function opensimFunction) {
        // see if we have andy data sets, if so use first one to get domain bounds otherwise 0-1
        int numDataSets = getChartPanel().getChart().getXYPlot().getDatasetCount();
        double dmin= getMinX();
        double dmax = getMaxX();
        if (numDataSets > 0){
             XYDataset dataSet0 = getChartPanel().getChart().getXYPlot().getDataset(0);
             if (dataSet0.getSeriesCount()>0){
                dmin = dataSet0.getX(0, 0).doubleValue();
                dmax = dataSet0.getX(0, dataSet0.getItemCount(0) -1).doubleValue();
             }
        }
        //double dUMargin = dAxis.getUpperMargin();
        ArrayList xValues = new ArrayList();
        ArrayList yValues = new ArrayList();
        //XYSeries curveSeries = new XYSeries("F="+opensimFunction.getName(), false, true);
        for (int i=0; i<=100; i++){
            double xValue = dmin + i*0.01*(dmax-dmin);
            org.opensim.modeling.Vector parms = new org.opensim.modeling.Vector(1, xValue);
            double yValue = opensimFunction.calcValue(parms);
            xValues.add(xValue);
            yValues.add(yValue);
        }
        PlotCurveSettings settings = new PlotCurveSettings(this);
        settings.setName(opensimFunction.getName());
        PlotCurve cv = new PlotCurve(settings, xValues, yValues, opensimFunction.getName());
        plotterModel.getCurrentPlot().add(cv);
        plotterModel.getPlotTreeModel().addPlotCurveToTree(cv);
        return cv;
    }
    
    
    public void setXAxisLabel(String label) {
        plotterModel.getCurrentPlot().getChartPanel().getChart().getXYPlot().getDomainAxis().setLabel(label);
    }
    
    public void setYAxisLabel(String label) {
        plotterModel.getCurrentPlot().getChartPanel().getChart().getXYPlot().getRangeAxis().setLabel(label);
    }
    
    public void setAxesLabels(String xAxis, String yAxis) {
        setXAxisLabel(xAxis);
        setYAxisLabel(yAxis);
    }
    
    // cleanup method intended to release resource and cleanup plotterModel
    //  supporting the panel. Possibly removing circular references if any
    void cleanup() {
        frame = null;
        sourceX = null;
        sourceY = null;
        if (jPlotsTree != null){
            jPlotsTree.removeTreeSelectionListener(this);
            jPlotsTree = null;
        }
        plotterModel.cleanup();
    }

    private void populateQNameSet() {
      qNameSet.add("length");
      qNameSet.add("fiberlength");
      qNameSet.add("tendonlength");
      qNameSet.add("normalizedfiberlength");
      qNameSet.add("tendonforce");
      qNameSet.add("activefiberforce");
      qNameSet.add("passivefiberforce");
      qNameSet.add("fiberforce");
    }

    public void hideControlPanel(boolean b) {
        jPlotControlPanel.setVisible(!b);
     }


   public enum PlotDataSource {FileSource, MotionSource, AnalysisSource};
   JPlotterQuantitySelector xSelector = null;
   String currentCurveTitle="";
   private boolean autoGeneratedCurveTitle=true;
   Vector<TreePath> selectedPathsVector = new Vector<TreePath>(4);   // Cache used to accumulate user selection of the tree
   PlotCurve currentCurve=null;     //used for update
   // We keep track of the following (as a state for the dialog/panel:
   PlotterSourceInterface  sourceX; // either a motion column, storage column or a gencoord
   PlotterSourceInterface sourceY; // Range source
   private String      domainName;  // single domain name
   private String[]    rangeNames;  // many range names
   private boolean     builtinMuscleCurve=false;   // Whether muscle selection is required for specified Y qty
   boolean sumCurve=false;
   Dialog dFilterDlg=null;
   boolean muscleDialogUp=false;
   NumberFormat    domainFormat=NumberFormat.getNumberInstance();
   NumberFormatter domainFormatter= new NumberFormatter(domainFormat);
   NumberFormat    rangeFormat=NumberFormat.getNumberInstance();
   NumberFormatter rangeFormatter= new NumberFormatter(rangeFormat);

   
   // Plotting from a motion or storage has obvious domain, sourceX values
   //       range names are for multiple curves or one sum curve. Single sourceY
   // MotionCurve: domainName: motion-time
   //              rangeNames: motion-columns
   //              sourceX: motion
   //              sourceY: motion.
   //              in case of sum, rangeNames[] are summed.
   // Plotting builtin muscle curves requires running an analysis that can produce one
   // or many storages->sources. 
   // If not plotting against a motion
   // The options should be as follows (FiberLength and MomentArm used as example):
   // FiberLength: domainName: Gencoord
   //              rangeNames: selectedMuscles
   //              sourceX: statesStorage (built on the fly or from a motion file)
   //              sourceY: single analysis result storage.(name=FiberLength).
   //              in case of sum, rangeNames[] are summed.
   //              ylabel=sourceY[0].getDisplayName();
   // MomentArm: domainName: GencoordToVary
   //              rangeNames: selectedMuscles
   //              sourceX: statesStorage (built on the fly or from a motion file)
   //              sourceY: single analysis result storage.(name=MomentArm_GencoordAbout).   
   //              in case of sum, rangeNames[] are summed.
   // 
   // 
   Model currentModel = OpenSimDB.getInstance().getCurrentModel();
   OpenSimContext openSimContext;
   private Storage statesStorage;
   private boolean clamp=false;
   double  yMin, yMax;
   private boolean activationOverride=false;
   double activationValue;
   private boolean modelChanged=true;

   /**
    * Creates new form JPlotterPanel
    */
   public JPlotterPanel() {
      initComponents();
 
      domainFormatter.setValueClass(java.lang.Double.class);
      rangeFormatter.setValueClass(java.lang.Double.class);
      jDomainStartTextField.setValue(0.0);   jDomainStartTextField.addActionListener(new handleReturnAction(jDomainStartTextField));
      jDomainEndTextField.setValue(1.0);   jDomainEndTextField.addActionListener(new handleReturnAction(jDomainEndTextField));
      jFormattedTextFieldYmin.setValue(-10000.0);   jFormattedTextFieldYmin.addActionListener(new handleReturnAction(jFormattedTextFieldYmin));
      jFormattedTextFieldYmax.setValue(10000.0);   jFormattedTextFieldYmax.addActionListener(new handleReturnAction(jFormattedTextFieldYmax));
      jActivationValueFormattedTextField.setValue(1.0);   jActivationValueFormattedTextField.addActionListener(new handleReturnAction(jActivationValueFormattedTextField));

      jTopChartingPanel.setLayout(new BorderLayout());
      xSelector = new JPlotterQuantitySelector(jXQtyTextField, this, true);
      jPlotsTree.addTreeSelectionListener(this);
      jPlotsTree.setEditable(true);
      jPlotsTree.setCellEditor(new UserTreeCellEditor(jPlotsTree, new DefaultTreeCellRenderer()));
      jPlotterDeletePlotButton.setEnabled(false);
      jPlotterAddPlotButton.setEnabled(validateXY());
      // Add in blank figure by default
      jTopChartingPanel.add(plotterModel.getCurrentPlot().getChartPanel());
      jPropertiesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                plotterModel.getCurrentPlot().getChartPanel().doEditChartProperties();
            }
      });
      processCurrentModel();
      useMuscles(false);
      jPlotsTree.setRootVisible(false);
      //printPlotDescriptor();
      PlotterDB.getInstance().registerPlotterPanel(this);
      
      populateQNameSet();
    }
   
   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPlotterMenuBar = new javax.swing.JMenuBar();
        jPlotterFileMenu = new javax.swing.JMenu();
        jLoadFileToPlotterMenuItem = new javax.swing.JMenuItem();
        jSourcePopupMenu = new javax.swing.JPopupMenu();
        jXPopupMenu = new javax.swing.JPopupMenu();
        jMusclePopupMenu = new javax.swing.JPopupMenu();
        jAdvancedPanel = new javax.swing.JPanel();
        jClampCheckBox = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jRectifyCheckBox = new javax.swing.JCheckBox();
        jLabel9 = new javax.swing.JLabel();
        jDomainStartTextField = new JFormattedTextField(domainFormatter);
        jLabel10 = new javax.swing.JLabel();
        jDomainEndTextField = new JFormattedTextField(domainFormatter);
        jFormattedTextFieldYmin = new JFormattedTextField(rangeFormatter);
        jFormattedTextFieldYmax = new JFormattedTextField(rangeFormatter);
        jActivationOverrideCheckBox = new javax.swing.JCheckBox();
        jActivationLabel = new javax.swing.JLabel();
        jActivationValueFormattedTextField = new javax.swing.JFormattedTextField();
        jPlotTitlePanel = new javax.swing.JPanel();
        jPlotLabelJLabel = new javax.swing.JLabel();
        jPlotNameTextField = new javax.swing.JTextField();
        jPlotPropertiesPopupMenu = new javax.swing.JPopupMenu();
        jSplitPane1 = new javax.swing.JSplitPane();
        jTopChartingPanel = new javax.swing.JPanel();
        jPlotControlPanel = new javax.swing.JPanel();
        jPlotNavigationPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPlotsTree = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        jPlotterAddPlotButton = new javax.swing.JButton();
        jPlotterDeletePlotButton = new javax.swing.JButton();
        jPropertiesButton = new javax.swing.JButton();
        jPlotSpecPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jCurveLegendTextField = new javax.swing.JTextField();
        yQuantityButton = new javax.swing.JButton();
        jMuscleSelectButton = new javax.swing.JButton();
        xQuantityButton = new javax.swing.JButton();
        jAdvancedOptionsButton = new javax.swing.JButton();
        jYQtyTextField = new javax.swing.JTextField();
        jSelectedMusclesTextField = new javax.swing.JTextField();
        jXQtyTextField = new javax.swing.JTextField();
        jSummaryAdvancedTextField = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        jPlotterFileMenu.setText("File");
        jPlotterFileMenu.setToolTipText("load into or save data from plotter.");

        jLoadFileToPlotterMenuItem.setText("Load File...");
        jLoadFileToPlotterMenuItem.setToolTipText("File with data to plot");
        jLoadFileToPlotterMenuItem.addActionListener(this);
        jPlotterFileMenu.add(jLoadFileToPlotterMenuItem);

        jPlotterMenuBar.add(jPlotterFileMenu);

        jClampCheckBox.setText("Clamp");
        jClampCheckBox.setToolTipText("Clamp Y value to limits between from, to");
        jClampCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jClampCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jClampCheckBox.addActionListener(this);

        jLabel1.setText("ymin");

        jLabel3.setText("ymax");

        jRectifyCheckBox.setText("Rectify");
        jRectifyCheckBox.setToolTipText("Plot absolute Y value");
        jRectifyCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRectifyCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRectifyCheckBox.addActionListener(this);

        jLabel9.setText("xmin");

        jDomainStartTextField.setToolTipText("domain start");
        jDomainStartTextField.addActionListener(this);
        jDomainStartTextField.addPropertyChangeListener(this);

        jLabel10.setText("xmax");

        jDomainEndTextField.setToolTipText("domain end");
        jDomainEndTextField.addActionListener(this);
        jDomainEndTextField.addPropertyChangeListener(this);

        jFormattedTextFieldYmin.setEditable(false);
        jFormattedTextFieldYmin.setToolTipText("clamp from value");
        jFormattedTextFieldYmin.setEnabled(false);
        jFormattedTextFieldYmin.addActionListener(this);

        jFormattedTextFieldYmax.setEditable(false);
        jFormattedTextFieldYmax.setToolTipText("clamp to value");
        jFormattedTextFieldYmax.setEnabled(false);

        jActivationOverrideCheckBox.setText("Activation override");
        jActivationOverrideCheckBox.setToolTipText("use activation specified below for all muscles");
        jActivationOverrideCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jActivationOverrideCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jActivationOverrideCheckBox.addActionListener(this);

        jActivationLabel.setText("Activation");

        jActivationValueFormattedTextField.setEditable(false);
        jActivationValueFormattedTextField.setText("1.0");
        jActivationValueFormattedTextField.setToolTipText("activation value for all muscles");
        jActivationValueFormattedTextField.setEnabled(false);

        org.jdesktop.layout.GroupLayout jAdvancedPanelLayout = new org.jdesktop.layout.GroupLayout(jAdvancedPanel);
        jAdvancedPanel.setLayout(jAdvancedPanelLayout);
        jAdvancedPanelLayout.setHorizontalGroup(
            jAdvancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jAdvancedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jAdvancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jRectifyCheckBox)
                    .add(jAdvancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jClampCheckBox)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jAdvancedPanelLayout.createSequentialGroup()
                            .add(jLabel9)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jDomainStartTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 74, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jAdvancedPanelLayout.createSequentialGroup()
                            .add(jLabel1)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jFormattedTextFieldYmin))))
                .add(jAdvancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jAdvancedPanelLayout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 19, Short.MAX_VALUE)
                        .add(jAdvancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jAdvancedPanelLayout.createSequentialGroup()
                                .add(jLabel10)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jDomainEndTextField))
                            .add(jAdvancedPanelLayout.createSequentialGroup()
                                .add(jLabel3)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jFormattedTextFieldYmax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 73, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                    .add(jAdvancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jAdvancedPanelLayout.createSequentialGroup()
                            .add(jActivationLabel)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(jActivationValueFormattedTextField, 0, 1, Short.MAX_VALUE))
                        .add(org.jdesktop.layout.GroupLayout.LEADING, jActivationOverrideCheckBox)))
                .addContainerGap())
        );
        jAdvancedPanelLayout.setVerticalGroup(
            jAdvancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jAdvancedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jAdvancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jAdvancedPanelLayout.createSequentialGroup()
                        .add(jAdvancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jRectifyCheckBox)
                            .add(jActivationOverrideCheckBox))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jAdvancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jClampCheckBox)
                            .add(jActivationLabel)
                            .add(jActivationValueFormattedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 25, Short.MAX_VALUE)
                        .add(jAdvancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel1)
                            .add(jFormattedTextFieldYmin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jAdvancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel9)
                            .add(jDomainStartTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jAdvancedPanelLayout.createSequentialGroup()
                        .add(jAdvancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel3)
                            .add(jFormattedTextFieldYmax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jAdvancedPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel10)
                            .add(jDomainEndTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jPlotTitlePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Headers"));

        jPlotLabelJLabel.setText("Plot Title");

        jPlotNameTextField.setToolTipText("Plot Title");

        org.jdesktop.layout.GroupLayout jPlotTitlePanelLayout = new org.jdesktop.layout.GroupLayout(jPlotTitlePanel);
        jPlotTitlePanel.setLayout(jPlotTitlePanelLayout);
        jPlotTitlePanelLayout.setHorizontalGroup(
            jPlotTitlePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPlotTitlePanelLayout.createSequentialGroup()
                .add(jPlotLabelJLabel)
                .add(22, 22, 22)
                .add(jPlotNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPlotTitlePanelLayout.setVerticalGroup(
            jPlotTitlePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPlotTitlePanelLayout.createSequentialGroup()
                .add(jPlotTitlePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jPlotLabelJLabel)
                    .add(jPlotNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jSplitPane1.setDividerLocation(300);
        jSplitPane1.setDividerSize(7);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(1.0);
        jSplitPane1.setAutoscrolls(true);
        jSplitPane1.setDoubleBuffered(true);
        jSplitPane1.setOneTouchExpandable(true);

        org.jdesktop.layout.GroupLayout jTopChartingPanelLayout = new org.jdesktop.layout.GroupLayout(jTopChartingPanel);
        jTopChartingPanel.setLayout(jTopChartingPanelLayout);
        jTopChartingPanelLayout.setHorizontalGroup(
            jTopChartingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 610, Short.MAX_VALUE)
        );
        jTopChartingPanelLayout.setVerticalGroup(
            jTopChartingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 299, Short.MAX_VALUE)
        );

        jSplitPane1.setLeftComponent(jTopChartingPanel);

        jPlotControlPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPlotNavigationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Curves List"));

        jScrollPane1.setAutoscrolls(true);

        jPlotsTree.setModel(plotterModel.getPlotTreeModel());
        jPlotsTree.addMouseListener(this);
        jScrollPane1.setViewportView(jPlotsTree);

        jPlotterAddPlotButton.setText("Add");
        jPlotterAddPlotButton.addActionListener(this);

        jPlotterDeletePlotButton.setText("Delete");
        jPlotterDeletePlotButton.addActionListener(this);

        jPropertiesButton.setText("Properties...");
        jPropertiesButton.setComponentPopupMenu(jPlotPropertiesPopupMenu);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPlotterAddPlotButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPlotterDeletePlotButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPropertiesButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jPlotterAddPlotButton)
                    .add(jPlotterDeletePlotButton)
                    .add(jPropertiesButton))
                .add(6, 6, 6))
        );

        org.jdesktop.layout.GroupLayout jPlotNavigationPanelLayout = new org.jdesktop.layout.GroupLayout(jPlotNavigationPanel);
        jPlotNavigationPanel.setLayout(jPlotNavigationPanelLayout);
        jPlotNavigationPanelLayout.setHorizontalGroup(
            jPlotNavigationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPlotNavigationPanelLayout.createSequentialGroup()
                .add(jPlotNavigationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPlotNavigationPanelLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPlotNavigationPanelLayout.setVerticalGroup(
            jPlotNavigationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPlotNavigationPanelLayout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jPlotSpecPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Curve Add"));
        jPlotSpecPanel.setToolTipText("");

        jLabel2.setText("Curve Name");

        jCurveLegendTextField.setToolTipText("Name of curve, to show in legend, Curves List.");
        jCurveLegendTextField.addActionListener(this);

        yQuantityButton.setText("Y-Quantity...");
        yQuantityButton.addMouseListener(this);

        jMuscleSelectButton.setText("Muscles...");
        jMuscleSelectButton.addMouseListener(this);

        xQuantityButton.setText("X-Quantity...");
        xQuantityButton.addMouseListener(this);

        jAdvancedOptionsButton.setText("Advanced...");
        jAdvancedOptionsButton.addActionListener(this);

        jYQtyTextField.setEditable(false);
        jYQtyTextField.addActionListener(this);
        jYQtyTextField.addFocusListener(this);

        jSelectedMusclesTextField.setEditable(false);

        jXQtyTextField.setEditable(false);
        jXQtyTextField.addActionListener(this);
        jXQtyTextField.addPropertyChangeListener(this);
        jXQtyTextField.addFocusListener(this);
        jXQtyTextField.addInputMethodListener(this);

        jSummaryAdvancedTextField.setEditable(false);
        jSummaryAdvancedTextField.setToolTipText("summary of advanced options");

        org.jdesktop.layout.GroupLayout jPlotSpecPanelLayout = new org.jdesktop.layout.GroupLayout(jPlotSpecPanel);
        jPlotSpecPanel.setLayout(jPlotSpecPanelLayout);
        jPlotSpecPanelLayout.setHorizontalGroup(
            jPlotSpecPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPlotSpecPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPlotSpecPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                    .add(jMuscleSelectButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jAdvancedOptionsButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                    .add(xQuantityButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(yQuantityButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPlotSpecPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jCurveLegendTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jSummaryAdvancedTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                    .add(jSelectedMusclesTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jXQtyTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jYQtyTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPlotSpecPanelLayout.setVerticalGroup(
            jPlotSpecPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPlotSpecPanelLayout.createSequentialGroup()
                .add(jPlotSpecPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jCurveLegendTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .add(10, 10, 10)
                .add(jPlotSpecPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(yQuantityButton)
                    .add(jYQtyTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPlotSpecPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jSelectedMusclesTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jMuscleSelectButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPlotSpecPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jXQtyTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(xQuantityButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPlotSpecPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jSummaryAdvancedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jAdvancedOptionsButton))
                .add(0, 0, Short.MAX_VALUE))
        );

        jButton1.setText("Help");
        jButton1.addActionListener(this);

        org.jdesktop.layout.GroupLayout jPlotControlPanelLayout = new org.jdesktop.layout.GroupLayout(jPlotControlPanel);
        jPlotControlPanel.setLayout(jPlotControlPanelLayout);
        jPlotControlPanelLayout.setHorizontalGroup(
            jPlotControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPlotControlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPlotControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPlotControlPanelLayout.createSequentialGroup()
                        .add(jPlotSpecPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPlotNavigationPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jButton1))
                .addContainerGap())
        );
        jPlotControlPanelLayout.setVerticalGroup(
            jPlotControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPlotControlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPlotControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPlotNavigationPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPlotSpecPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton1))
        );

        jSplitPane1.setRightComponent(jPlotControlPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSplitPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
        );
    }

    // Code for dispatching events from components to event handlers.

    public void actionPerformed(java.awt.event.ActionEvent evt) {
        if (evt.getSource() == jPlotterAddPlotButton) {
            JPlotterPanel.this.jPlotterAddCurveButtonActionPerformed(evt);
        }
        else if (evt.getSource() == jPlotterDeletePlotButton) {
            JPlotterPanel.this.jPlotterDeletePlotButtonActionPerformed(evt);
        }
        else if (evt.getSource() == jCurveLegendTextField) {
            JPlotterPanel.this.jCurveLegendTextFieldActionPerformed(evt);
        }
        else if (evt.getSource() == jAdvancedOptionsButton) {
            JPlotterPanel.this.jAdvancedOptionsButtonActionPerformed(evt);
        }
        else if (evt.getSource() == jYQtyTextField) {
            JPlotterPanel.this.jYQtyTextFieldActionPerformed(evt);
        }
        else if (evt.getSource() == jXQtyTextField) {
            JPlotterPanel.this.jXQtyTextFieldActionPerformed(evt);
        }
        else if (evt.getSource() == jLoadFileToPlotterMenuItem) {
            JPlotterPanel.this.jLoadFileToPlotterMenuItemActionPerformed(evt);
        }
        else if (evt.getSource() == jClampCheckBox) {
            JPlotterPanel.this.jClampCheckBoxActionPerformed(evt);
        }
        else if (evt.getSource() == jRectifyCheckBox) {
            JPlotterPanel.this.jRectifyCheckBoxActionPerformed(evt);
        }
        else if (evt.getSource() == jDomainStartTextField) {
            JPlotterPanel.this.jDomainStartTextFieldActionPerformed(evt);
        }
        else if (evt.getSource() == jDomainEndTextField) {
            JPlotterPanel.this.jDomainEndTextFieldActionPerformed(evt);
        }
        else if (evt.getSource() == jFormattedTextFieldYmin) {
            JPlotterPanel.this.jFormattedTextFieldYminActionPerformed(evt);
        }
        else if (evt.getSource() == jActivationOverrideCheckBox) {
            JPlotterPanel.this.jActivationOverrideCheckBoxActionPerformed(evt);
        }
        else if (evt.getSource() == jButton1) {
            JPlotterPanel.this.jButton1ActionPerformed(evt);
        }
    }

    public void focusGained(java.awt.event.FocusEvent evt) {
    }

    public void focusLost(java.awt.event.FocusEvent evt) {
        if (evt.getSource() == jYQtyTextField) {
            JPlotterPanel.this.jYQtyTextFieldFocusLost(evt);
        }
        else if (evt.getSource() == jXQtyTextField) {
            JPlotterPanel.this.jXQtyTextFieldFocusLost(evt);
        }
    }

    public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
        if (evt.getSource() == jXQtyTextField) {
            JPlotterPanel.this.jXQtyTextFieldCaretPositionChanged(evt);
        }
    }

    public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
    }

    public void mouseClicked(java.awt.event.MouseEvent evt) {
    }

    public void mouseEntered(java.awt.event.MouseEvent evt) {
    }

    public void mouseExited(java.awt.event.MouseEvent evt) {
    }

    public void mousePressed(java.awt.event.MouseEvent evt) {
        if (evt.getSource() == jPlotsTree) {
            JPlotterPanel.this.jPlotsTreeMousePressed(evt);
        }
        else if (evt.getSource() == yQuantityButton) {
            JPlotterPanel.this.yQuantityButtonMousePressed(evt);
        }
        else if (evt.getSource() == jMuscleSelectButton) {
            JPlotterPanel.this.jMuscleSelectButtonMousePressed(evt);
        }
        else if (evt.getSource() == xQuantityButton) {
            JPlotterPanel.this.xQuantityButtonMousePressed(evt);
        }
    }

    public void mouseReleased(java.awt.event.MouseEvent evt) {
        if (evt.getSource() == jPlotsTree) {
            JPlotterPanel.this.jPlotsTreeMouseReleased(evt);
        }
        else if (evt.getSource() == yQuantityButton) {
            JPlotterPanel.this.yQuantityButtonMouseReleased(evt);
        }
        else if (evt.getSource() == jMuscleSelectButton) {
            JPlotterPanel.this.jMuscleSelectButtonMouseReleased(evt);
        }
        else if (evt.getSource() == xQuantityButton) {
            JPlotterPanel.this.xQuantityButtonMouseReleased(evt);
        }
    }

    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        if (evt.getSource() == jXQtyTextField) {
            JPlotterPanel.this.jXQtyTextFieldPropertyChange(evt);
        }
        else if (evt.getSource() == jDomainStartTextField) {
            JPlotterPanel.this.jDomainStartTextFieldPropertyChange(evt);
        }
        else if (evt.getSource() == jDomainEndTextField) {
            JPlotterPanel.this.jDomainEndTextFieldPropertyChange(evt);
        }
    }// </editor-fold>//GEN-END:initComponents

   private void jActivationOverrideCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jActivationOverrideCheckBoxActionPerformed
       JCheckBox override = (JCheckBox)evt.getSource();
       jActivationValueFormattedTextField.setEnabled(override.isSelected());
       jActivationValueFormattedTextField.setEditable(override.isSelected());
   }//GEN-LAST:event_jActivationOverrideCheckBoxActionPerformed

   private void jDomainEndTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDomainEndTextFieldActionPerformed
      jDomainEndTextField.setValue(getMaxX()); // to reformat the entered value
   }//GEN-LAST:event_jDomainEndTextFieldActionPerformed

   private void jPlotsTreeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPlotsTreeMouseReleased
     if (evt.isPopupTrigger())
       invokeTreePopupIfNeeded(evt.getX(), evt.getY());
   }//GEN-LAST:event_jPlotsTreeMouseReleased

   private void jPlotsTreeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPlotsTreeMousePressed
      if (evt.isPopupTrigger())
       invokeTreePopupIfNeeded(evt.getX(), evt.getY());
   }//GEN-LAST:event_jPlotsTreeMousePressed

   private void jFormattedTextFieldYminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFormattedTextFieldYminActionPerformed
   }//GEN-LAST:event_jFormattedTextFieldYminActionPerformed

   private void jDomainStartTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDomainStartTextFieldActionPerformed
      jDomainStartTextField.setValue(getMinX()); // to reformat the entered value
   }//GEN-LAST:event_jDomainStartTextFieldActionPerformed

   private void jRectifyCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRectifyCheckBoxActionPerformed
   }//GEN-LAST:event_jRectifyCheckBoxActionPerformed
   
    private void jAdvancedOptionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAdvancedOptionsButtonActionPerformed
       OpenSimDialog  advDlg=DialogUtils.createDialogForPanelWithParent(getFrame(), jAdvancedPanel, "Advanced Options");
       DialogUtils.addStandardButtons(advDlg);
       advDlg.setModal(true);
       advDlg.setVisible(true);
       if (advDlg.getDialogReturnValue()==OpenSimDialog.OK_OPTION){
           setClamp(jClampCheckBox.isSelected());
           setActivationOverride(jActivationOverrideCheckBox.isSelected());
           updateSummary();
       }
    }//GEN-LAST:event_jAdvancedOptionsButtonActionPerformed

   private void updateSummary() {
      NumberFormat numFormat = NumberFormat.getInstance();
      String summary = "Rectify ="+jRectifyCheckBox.isSelected()+
                       ", x:["+
                       numFormat.format(jDomainStartTextField.getValue())+", "+
                       numFormat.format(jDomainEndTextField.getValue())+"]";
      if (isClamp())
              summary=summary+" y:["+
                      numFormat.format(jFormattedTextFieldYmin.getValue())+", "+
                      numFormat.format(jFormattedTextFieldYmax.getValue())+"]";
      Object foo = jActivationValueFormattedTextField.getValue();
      Object foo2 = jFormattedTextFieldYmin.getValue();
      if (isActivationOverride())
         summary=summary+" A=" + numFormat.format(jActivationValueFormattedTextField.getValue());
      jSummaryAdvancedTextField.setText(summary);
   }
    
    private void jClampCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jClampCheckBoxActionPerformed
       JCheckBox clamped = (JCheckBox)evt.getSource();
       jFormattedTextFieldYmin.setEnabled(clamped.isSelected());
       jFormattedTextFieldYmin.setEditable(clamped.isSelected());
       jFormattedTextFieldYmax.setEditable(clamped.isSelected());
       jFormattedTextFieldYmax.setEnabled(clamped.isSelected());
    }//GEN-LAST:event_jClampCheckBoxActionPerformed
    
    private void jMuscleSelectButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMuscleSelectButtonMouseReleased
       //jMuscleSelectButtonMousePressed(evt);
    }//GEN-LAST:event_jMuscleSelectButtonMouseReleased
    
    private void jMuscleSelectButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMuscleSelectButtonMousePressed
        String preSelected=jSelectedMusclesTextField.getText();
        // Show muscle selection, multiple
        //XX2
        if (currentModel==null || builtinMuscleCurve==false)
            return;
        if (muscleDialogUp==true) // An instance is already up
            return;
        SingleModelGuiElements guiElem = OpenSimDB.getInstance().getModelGuiElements(currentModel);
        String[] muscleNames = guiElem.getActuatorNames();
        final QuantityNameFilterJPanel filterPanel = new QuantityNameFilterJPanel(muscleNames, preSelected);
        //DialogDescriptor filterDlg = new DialogDescriptor(filterPanel, "Select Muscles", false, null);
        //filterDlg.setOptions(new Object[]{new JButton("Close")});
        dFilterDlg=DialogUtils.createDialogForPanelWithParent(getFrame(), filterPanel, "Select Muscles");
        DialogUtils.addCloseButton(dFilterDlg, new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if (dFilterDlg!=null)
                    dFilterDlg.dispose();
                dFilterDlg = null;
                muscleDialogUp=false;                
            }}
        );
        dFilterDlg.addWindowListener(new WindowAdapter(){
            private void close() {
                if (dFilterDlg!=null)
                    dFilterDlg.dispose();
                dFilterDlg = null;
                muscleDialogUp=false;
            }
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                close();
            }
            
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                close();
            }});
        filterPanel.addSelectionChangeListener(new TableModelListener(){
            public void tableChanged(TableModelEvent e) {
                jSelectedMusclesTextField.setText(filterPanel.getSelectedAsString());
                sumCurve=filterPanel.isSumOnly();
                if (sumCurve)
                    rangeNames = filterPanel.getSelectedAsString().trim().split("\\+",-1);
                else
                    rangeNames = filterPanel.getSelectedAsString().trim().split(",",-1);
                for(int i=0;i<rangeNames.length;i++)
                    rangeNames[i]=rangeNames[i].trim();
                updateContextGuiElements();
                jPlotterAddPlotButton.setEnabled(validateXY());
            }});

        dFilterDlg.setVisible(true);
        muscleDialogUp=true;
                
    }//GEN-LAST:event_jMuscleSelectButtonMousePressed
    
    private void xQuantityButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_xQuantityButtonMouseReleased
       //xQuantityButtonMousePressed(evt);
    }//GEN-LAST:event_xQuantityButtonMouseReleased
    
    private void xQuantityButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_xQuantityButtonMousePressed
       //XX3
       if (isBuiltinMuscleCurve()){
          // May plot against motion curve or against a GC
          jXPopupMenu.removeAll();
          currentModel = OpenSimDB.getInstance().getCurrentModel();
          openSimContext = OpenSimDB.getInstance().getContext(currentModel);
          // Guard against all models being deleted while the dialog is up
          if (currentModel==null) return;
          SingleModelGuiElements guiElem = OpenSimDB.getInstance().getModelGuiElements(currentModel);
          String[] coordNames = guiElem.getUnconstrainedCoordinateNames();
          for(int i=0; i<coordNames.length; i++){
             final String coordinateName=coordNames[i];
             JMenuItem coordinateMenuItem = new JMenuItem(coordinateName);
             coordinateMenuItem.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                   populateXQty(coordinateName);
                   updateContextGuiElements();
                   jPlotterAddPlotButton.setEnabled(validateXY());
      //printPlotDescriptor();
                }
             });
             jXPopupMenu.add(coordinateMenuItem);
          }
          jXPopupMenu.addSeparator();
          // for motions make a cascade menu
          ArrayList<PlotterSourceMotion> motionSources=plotterModel.getLoadedMotionSources();
          for(int i=0; i<motionSources.size(); i++){
             final PlotterSourceMotion nextMotion = motionSources.get(i);
             JMenuItem motionMenuItem = new JMenuItem(nextMotion.getDisplayName());
             motionMenuItem.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                   setDomainName(nextMotion.getDisplayName());
                   jXQtyTextField.setText(nextMotion.getDisplayName());
                   jDomainStartTextField.setValue((double)nextMotion.getDefaultMin("time"));
                   jDomainEndTextField.setValue((double)nextMotion.getDefaultMax("time"));
                   sourceX=nextMotion;
                   jPlotterAddPlotButton.setEnabled(validateXY());
                   updateContextGuiElements();
      //printPlotDescriptor();
                }
             });
             jXPopupMenu.add(motionMenuItem);
          }
          jXPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
      //printPlotDescriptor();
       } else{    // Select X from a motion or a file
          if (plotterModel.countSources()==0 || sourceX==null)
             return;
          xSelector.showSingleSelectionPanel(plotterModel,  sourceX, frame);
          jDomainStartTextField.setValue((double)sourceX.getDefaultMin("time"));
          jDomainEndTextField.setValue((double)sourceX.getDefaultMax("time"));
          String dn = xSelector.getColumnToUse();
          setDomainName(dn);
          updateContextGuiElements();
          jPlotterAddPlotButton.setEnabled(validateXY());
      //printPlotDescriptor();
       }
       parseDomainOrRangeText(jXQtyTextField, true);
       
    }//GEN-LAST:event_xQuantityButtonMousePressed
    
    private void yQuantityButtonMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_yQuantityButtonMouseReleased
       //yQuantityButtonMousePressed(evt);
    }//GEN-LAST:event_yQuantityButtonMouseReleased
    
    private void yQuantityButtonMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_yQuantityButtonMousePressed
       jSourcePopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
    }//GEN-LAST:event_yQuantityButtonMousePressed
        
   private void jXQtyTextFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jXQtyTextFieldPropertyChange
   }//GEN-LAST:event_jXQtyTextFieldPropertyChange
   
   private void jXQtyTextFieldCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_jXQtyTextFieldCaretPositionChanged
   }//GEN-LAST:event_jXQtyTextFieldCaretPositionChanged
   
    private void jDomainEndTextFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDomainEndTextFieldPropertyChange
    }//GEN-LAST:event_jDomainEndTextFieldPropertyChange
    
    private void jDomainStartTextFieldPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jDomainStartTextFieldPropertyChange
    }//GEN-LAST:event_jDomainStartTextFieldPropertyChange
    // AnalysisPick
    private PlotterSourceAnalysis[] initAnalyses() {
       plotterModel.addModel(currentModel);
       xQuantityButton.setEnabled(true);
       ArrayList<PlotterSourceAnalysis> srcs=plotterModel.getAnalysisSources();
       // Add available quantities to jAvailableAnalysesComboBox
       PlotterSourceAnalysis[] availableAnalyses= new PlotterSourceAnalysis[srcs.size()];
       for(int i=0; i<srcs.size();i++)
          availableAnalyses[i] = srcs.get(i);
       return availableAnalyses;
    }
    
   private void jXQtyTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jXQtyTextFieldFocusLost
      jPlotterAddPlotButton.setEnabled(validateXY());
      
   }//GEN-LAST:event_jXQtyTextFieldFocusLost
   
   private void jXQtyTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXQtyTextFieldActionPerformed
      jPlotterAddPlotButton.setEnabled(validateXY());
      
   }//GEN-LAST:event_jXQtyTextFieldActionPerformed
   
   private void jYQtyTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jYQtyTextFieldFocusLost
      jPlotterAddPlotButton.setEnabled(validateXY());
      
   }//GEN-LAST:event_jYQtyTextFieldFocusLost
   
   private void jYQtyTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jYQtyTextFieldActionPerformed
      jPlotterAddPlotButton.setEnabled(validateXY());
      
   }//GEN-LAST:event_jYQtyTextFieldActionPerformed
   
   private void jPlotterDeletePlotButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPlotterDeletePlotButtonActionPerformed
      
      // Make a cache so that object deletion does not mess up the selections array
      // we're working on.
      Object[] cache = new Object[selectedPathsVector.size()];
      selectedPathsVector.copyInto(cache);
      for(int i=0;i<cache.length; i++){
         TreePath nextPath = (TreePath)cache[i]; // Since the array shrinks!
         Object[] pathObjects = nextPath.getPath();
         int depth =pathObjects.length-1;
         DefaultMutableTreeNode node = (DefaultMutableTreeNode)pathObjects[depth];
         if (node instanceof PlotNode){
            Plot figToDelete = ((Plot)node.getUserObject());
            plotterModel.deletePlot(figToDelete);
         } else if (node instanceof PlotCurveNode){
            PlotCurve cvToDelete = ((PlotCurve)node.getUserObject());
            plotterModel.deleteCurve(cvToDelete);
         } else
            JOptionPane.showMessageDialog(this, "Don't know what to delete!");
         
      }
   }//GEN-LAST:event_jPlotterDeletePlotButtonActionPerformed
         
   private void jCurveLegendTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCurveLegendTextFieldActionPerformed
      
      autoGeneratedCurveTitle=false;   // user modified the text field don't intervene!'
   }//GEN-LAST:event_jCurveLegendTextFieldActionPerformed
   /**
    * Function to be invoked when the Add button is pressed
    */
   //XX5
   private void jPlotterAddCurveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPlotterAddCurveButtonActionPerformed
       // The following should repeat for every single Y qty selected
       String title = jPlotNameTextField.getText();
       PlotCurve plotCurve=null;
       String domain = jXQtyTextField.getText();
       if (isBuiltinMuscleCurve()){
           // run AnalysisTool once since model can't change in between'
           runAnalysisTool((PlotterSourceAnalysis) sourceY, sourceX, rangeNames);
       }
       // get Storages from sources
       int numCurves = sumCurve?1:rangeNames.length;
       PlotCurveSettings settings  = getSettings();
       String nameFromSettings=settings.getName();
       String nameFromRanges=getRangeNamesAsString();
       autoGeneratedCurveTitle=(nameFromSettings.compareTo(nameFromRanges)==0);
       jPlotsTree.clearSelection();
       for(int curveIndex=0; curveIndex<numCurves; curveIndex++){
           if (rangeNames.length>1){ // if user specified append index
               if (autoGeneratedCurveTitle)
                   settings.setName(sumCurve?getRangeNamesAsString("+"):rangeNames[curveIndex]);
               else{
                   if (numCurves==1)
                        settings.setName(nameFromSettings);
                   else
                        settings.setName(nameFromSettings+" "+String.valueOf(curveIndex));
               }
               // Make one string representing "+" separated 
           }
           // Add name for y-axis here
           if (sumCurve)
                plotCurve = plotterModel.addCurveMultipleRangeNames(title, settings,
                   sourceX, getDomainName(), sourceY, rangeNames);
           else 
                plotCurve = plotterModel.addCurveSingleRangeName(title, settings,
                   sourceX, getDomainName(), sourceY, rangeNames[curveIndex]);
           
            makeCurveCurrent(plotCurve);
       }
       
       this.doLayout();
       repaint();
   }//GEN-LAST:event_jPlotterAddCurveButtonActionPerformed
   
   private void makeCurveCurrent(final PlotCurve plotCurve) {
      // Find node and make it selected
      PlotCurveNode cvnode=((PlotTreeModel)plotterModel.getPlotTreeModel()).findCurveNode(plotCurve);
      TreeNode[] nodes = ((PlotTreeModel)plotterModel.getPlotTreeModel()).getPathToRoot(cvnode);
      TreePath path = new TreePath(nodes);
      jPlotsTree.addSelectionPath(path);
      currentCurve = plotCurve;
   }
   
   private void jLoadFileToPlotterMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLoadFileToPlotterMenuItemActionPerformed
      
      // Browse for Storage or Motion file (for now) and preprocess the file if needed for plotting
      String dataFilename = FileUtils.getInstance().browseForFilename(".sto, .mot", "Files containing data to plot", true, this);
      if (dataFilename != null){
         try {
            PlotterSourceFile src = new PlotterSourceFile(dataFilename);
            getPlotterModel().addSource(src);
         } catch (IOException ex) {
            ex.printStackTrace();
         }
      }
   }//GEN-LAST:event_jLoadFileToPlotterMenuItemActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String path = BrowserLauncher.isConnected() ? "http://simtk-confluence.stanford.edu:8080/display/OpenSim30/Plotting" : TheApp.getUsersGuideDir() + "Plotting.html"; 
        BrowserLauncher.openURL(path);
    }//GEN-LAST:event_jButton1ActionPerformed
   
   public PlotterSourceFile loadFile(String dataFilename) {
        try {
            PlotterSourceFile src = new PlotterSourceFile(dataFilename);
            getPlotterModel().addSource(src);
            return src;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
   }
   
   public PlotterModel getPlotterModel() {
      return plotterModel;
   }
   
   private PlotCurveSettings getSettings() {
      return new PlotCurveSettings(this);
   }
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jActivationLabel;
    private javax.swing.JCheckBox jActivationOverrideCheckBox;
    private javax.swing.JFormattedTextField jActivationValueFormattedTextField;
    private javax.swing.JButton jAdvancedOptionsButton;
    private javax.swing.JPanel jAdvancedPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jClampCheckBox;
    private javax.swing.JTextField jCurveLegendTextField;
    private javax.swing.JFormattedTextField jDomainEndTextField;
    private javax.swing.JFormattedTextField jDomainStartTextField;
    private javax.swing.JFormattedTextField jFormattedTextFieldYmax;
    private javax.swing.JFormattedTextField jFormattedTextFieldYmin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuItem jLoadFileToPlotterMenuItem;
    private javax.swing.JPopupMenu jMusclePopupMenu;
    private javax.swing.JButton jMuscleSelectButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPlotControlPanel;
    private javax.swing.JLabel jPlotLabelJLabel;
    private javax.swing.JTextField jPlotNameTextField;
    private javax.swing.JPanel jPlotNavigationPanel;
    private javax.swing.JPopupMenu jPlotPropertiesPopupMenu;
    private javax.swing.JPanel jPlotSpecPanel;
    private javax.swing.JPanel jPlotTitlePanel;
    private javax.swing.JTree jPlotsTree;
    private javax.swing.JButton jPlotterAddPlotButton;
    private javax.swing.JButton jPlotterDeletePlotButton;
    private javax.swing.JMenu jPlotterFileMenu;
    private javax.swing.JMenuBar jPlotterMenuBar;
    private javax.swing.JButton jPropertiesButton;
    private javax.swing.JCheckBox jRectifyCheckBox;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jSelectedMusclesTextField;
    private javax.swing.JPopupMenu jSourcePopupMenu;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextField jSummaryAdvancedTextField;
    private javax.swing.JPanel jTopChartingPanel;
    private javax.swing.JPopupMenu jXPopupMenu;
    private javax.swing.JTextField jXQtyTextField;
    private javax.swing.JTextField jYQtyTextField;
    private javax.swing.JButton xQuantityButton;
    private javax.swing.JButton yQuantityButton;
    // End of variables declaration//GEN-END:variables
   
   public String getPlotName() {
      return jPlotNameTextField.getText();
   }

   private void updateContextGuiElements() {
      currentCurveTitle=getRangeNamesAsString();
      jCurveLegendTextField.setText(currentCurveTitle);
   }
   
   public boolean isAutoGeneratedCurveTitle() {
      return autoGeneratedCurveTitle;
   }
   
   public void setAutoGeneratedCurveTitle(boolean autoGeneratedCurveTitle) {
      this.autoGeneratedCurveTitle = autoGeneratedCurveTitle;
   }
   
   public String getCurveName() {
      return jCurveLegendTextField.getText();
   }
   /**
    * This method is to be invoked after X or Y selection either when the selection
    *  dialog is brought down or after user typing in data in the X,Y textfields
    * (and probably a few other situations)
    * to update
    *    - default curve name if not modified by user
    *    - X-bounds
    * Very important piece here is parseDomainOrRangeText see comments there.
    *
   public void updatePlotterWithSelection() {
      jPlotterAddPlotButton.setEnabled(validateXY());
      
   }*/
   /**
    * TreeSelectionListener implementation
    */
   public void valueChanged(TreeSelectionEvent e) {
      getCurrentTreeSelection(e);
      
      jPlotterDeletePlotButton.setEnabled(false);
      //jPlotterUpdatePlotButton.setEnabled(false);
      if (selectedPathsVector.size()==0)
         return;
      
      // Make sure we have a curve before enabling update
      if (selectedPathsVector.size()==1){
         TreeNode lastNode=(TreeNode)selectedPathsVector.get(0).getLastPathComponent();
         if (lastNode instanceof PlotCurveNode){
            // Populate dialog
            PlotCurve cv = (PlotCurve)((PlotCurveNode)lastNode).getUserObject();
            updatePanelFromCurve(cv);
            //jPlotterUpdatePlotButton.setEnabled(true);
            jPlotterDeletePlotButton.setEnabled(true);
         }
      } else {   // Enable delete if only curves are selected as we can't delete full plots now'
         boolean allCurves=true;
         for(int i=0; i<selectedPathsVector.size() && allCurves; i++){
            TreeNode lastNode=(TreeNode)selectedPathsVector.get(i).getLastPathComponent();
            allCurves =(lastNode instanceof PlotCurveNode);
         }
         jPlotterDeletePlotButton.setEnabled(allCurves);
      }
   }
   
   private void getCurrentTreeSelection(TreeSelectionEvent e) {
      TreePath[] selectedPaths = e.getPaths();
      for(int i=0;i<selectedPaths.length;i++){
         if (e.isAddedPath(i)){
            selectedPathsVector.add(selectedPaths[i]);
         } else {   //Removed
            selectedPathsVector.remove(selectedPaths[i]);
         }
      }
   }
   /**
    * Specify domain min bound
    * 
    * @param minX 
    */   
   public void setMinX(double minX) {
      jDomainStartTextField.setValue(minX);
   }
   /**
    * Retrieve domain min bound
    * 
    * @return domain min bound
    */
   public double getMinX() {
      return ((Double)jDomainStartTextField.getValue()).doubleValue();
   }
   /**
    * Specify domain max bound
    * 
    * @param maxX 
    */
   public void setMaxX(double maxX) {
      jDomainEndTextField.setValue(maxX);
   }
   /**
    * Retrieve domain max bound
    * 
    * @return domain max bound
    */
   public double getMaxX() {
      return ((Double)jDomainEndTextField.getValue()).doubleValue();
   }
   
   boolean getRectify() {
      return jRectifyCheckBox.isSelected();
   }
   /**
    * updatePanelFromCurve is invoked when selection on the tree changes.
    * need to make sure that the following are set
    * PlotterSourceInterface  sourceX, sourceY;
    * String      domainName;
    * String[]    rangeNames;
    */
   private void updatePanelFromCurve(PlotCurve cv) {
      // Populate Curve name, filters
       /*
      currentCurve=cv;
      jPlotNameTextField.setText(plotterModel.getPlotForCurve(cv).getTitle());
      sourceX=cv.getDomainSource();
      sourceY=cv.getRangeSource();
      setDomainName(cv.getDomainName());
      String[] curveRangeNames = cv.getRangeNames();
      rangeNames = new String[curveRangeNames.length];
      System.arraycopy(curveRangeNames, 0, rangeNames, 0, curveRangeNames.length);
      if (cv.getSettings().isMusclePlot())
         jSelectedMusclesTextField.setText(PlotterModel.makeSumString(rangeNames));
      else
         jSelectedMusclesTextField.setText("");
      // set title based on figurxe title
      jCurveLegendTextField.setText(cv.getLegend());
      setMinX(cv.getSettings().getXMin());
      setMaxX(cv.getSettings().getXMax());
      // Form domain label
      //ArrayStr arx = cv.getDomainStorage().getColumnLabels();
      String sourceString=cv.getDomainSource().getDisplayName();
      String colName =cv.getDomainName();
      jXQtyTextField.setText(domainName);
      // Form range label
      //ArrayStr ary = cv.getRangeStorage().getColumnLabels();
      sourceString=cv.getRangeSource().getDisplayName();
      colName =PlotterModel.makeSumString(cv.getRangeNames());
      if (cv.getSettings().isMusclePlot()){
         jYQtyTextField.setText(sourceY.getDisplayName());
      } else {
         jYQtyTextField.setText(colName);
      }
      // Now the filters
      jRectifyCheckBox.setSelected(false);
      Vector<PlotDataFilter> filters = cv.getSettings().getFilters();
      for(int i=0; i<filters.size(); i++){
         if (filters.get(i) instanceof PlotRectifyFilter)
            jRectifyCheckBox.setSelected(true);
         
      }*/
   }
   
   public void showTimeCurves(String title, String plot, Storage residualsStore, String[] names) {
   }
   
   /**
    * Popups have a single purpose, to populate the Text fields with valid values, but users can
    * type those in manually. The following parse functions try to recover the source storage, columns
    * from the Text fields for quantities. On successful parsing (names and sources local variables are set).
    * The syntax for File sources is File:<xxxx>:<yyyyy>
    **/
   private boolean parseDomainOrRangeText(JTextField jQtyTextField, boolean isDomain) {
      
      String text = jQtyTextField.getText();
      // Check for Empty
      if (text.length()==0)
         return false;
      
      // Check for qualifiers
      // We need to be forgiving in case the user types in the quantity manually
      String trimmed = text.trim();
      // Split around ":
      String columnNameList = trimmed;
      // If file doesn't exist or doesn't have column complain, otherwise
      // set Storage and Column
      String[] columns=columnNameList.trim().split(",",-1);
      if (isDomain){
         if (columns.length!=1){
            JOptionPane.showMessageDialog(this, "Can't have more than one column for domain");
            return false;
         } else{
            setDomainName(columns[0].trim());
            return true; // Should check coordinate exists
         }
      } else {   // range
         for(int i=0; i<columns.length; i++){
            columns[i]=columns[i].trim();
         }
         rangeNames = new String[columns.length];
         System.arraycopy(columns, 0, rangeNames, 0, columns.length);
         // set sourceY here after all error detection is done.
         return true;
      }
      
   }
   private String getRangeNamesAsString() {
       return getRangeNamesAsString(",");
   }
   /**
    * Get a string representing the list of column names
    */
  private String getRangeNamesAsString(String separator) {
      return getRangeNamesAsString(separator, rangeNames);
   }
   
   private String getRangeNamesAsString(String separator, String[] names) {
      String rep="";
       if (rangeNames!=null){
         for(int i=0; i<rangeNames.length; i++){
            rep = rep + rangeNames[i];
            if (i<rangeNames.length-1)
               rep = rep + separator;
         }
      }
      return rep;      
   }
   public PlotCurve plotDataFromSource(PlotterSourceInterface source, String domain, String range) {
        sourceX = source;
        sourceY = sourceX;
        setDomainName(domain);
        PlotCurveSettings settings  = getSettings();
        settings.setXMin(source.getStorage().getFirstTime());
        settings.setXMax(source.getStorage().getLastTime());
        PlotCurve plotCurve=null;
        settings.setName(range);
        plotCurve = plotterModel.addCurveSingleRangeName("Title", settings,
                                sourceX, getDomainName(), sourceY, range);
        makeCurveCurrent(plotCurve);
        return plotCurve;
   }
   
   public PlotCurve showAnalysisCurveAgainstTime(Model aModel, Storage s,
           String title,
           String curveLegend, String columnName,
           String xLabel, String yLabel) {
        
        sourceX = new PlotterSourceAnalysis(aModel, s, columnName);
        sourceY = sourceX;
        setDomainName("time");
        rangeNames = new String[]{columnName};
        jPlotNameTextField.setText(curveLegend);
        PlotCurveSettings settings  = getSettings();
        PlotCurve plotCurve=null;
        //settings.setXMin(s.getFirstTime());
        //settings.setXMax(s.getLastTime());
        settings.setName(rangeNames[0]);
        plotCurve = plotterModel.addCurveSingleRangeName(title, settings,
                sourceX, getDomainName(),                 sourceY, rangeNames[0]);
        makeCurveCurrent(plotCurve);
        plotterModel.fireChangeEvent(getNodeForCurve(plotCurve));
        // Observe motionsDB 
        //MotionsDB.getInstance().addObserver(this);
      return plotCurve;
   }
   /**
    * This will be triggered when motion time changes.
    * Then we need to update X-crosshairs to match time
    */
   public void update(Observable o, Object arg) {
      if (o instanceof MotionsDB) {
           if (arg instanceof MotionTimeChangeEvent) {
               MotionTimeChangeEvent motionTimeEvent = (MotionTimeChangeEvent) arg;
               final double time = motionTimeEvent.getTime();
               SwingUtilities.invokeLater(new Runnable() {
            // Should cast arg to proper event and set X-crosshairs

                   public void run() {
                       if (getDomainName().compareTo("time") == 0) {
                            addDomainTickmark(time);
            }
         }
               });
           }
         else if (arg instanceof MotionEvent){
            MotionEvent mev = (MotionEvent) arg;
            if (mev.getOperation()==mev.getOperation().Open && mev.getModel()==currentModel){
               plotterModel.addMotion(mev.getMotion());
               populateYPopup();
            }
            else if (mev.getOperation()==mev.getOperation().Close){  // Some motion is closed
               plotterModel.removeMotion(mev.getMotion());
               if (MotionsDB.getInstance().getNumCurrentMotions()==0)
                   
               populateYPopup();
            } else if (mev.getOperation() == mev.getOperation().Modified) {
               populateYPopup();
            }
         }
         else if (arg instanceof ObjectsRenamedEvent){
            ObjectsRenamedEvent evt = (ObjectsRenamedEvent)arg;
            Vector<OpenSimObject> objs = evt.getObjects();
            // If any of the event objects is a motion, update the panel.
            for (int i=0; i<objs.size(); i++) {
               if (objs.get(i) instanceof Storage) {
                  plotterModel.renameMotion((Storage)(objs.get(i)));
                  break;
               }
               }
         }
         
      }
      else if (o instanceof OpenSimDB){
         if (arg instanceof ObjectSetCurrentEvent) {
            ObjectSetCurrentEvent evt = (ObjectSetCurrentEvent)arg;
            Vector<OpenSimObject> objs = evt.getObjects();
            // If any of the event objects is a model, this means there is a new
            // current model. So update the Y menu.
            for (int i=0; i<objs.size(); i++) {
               if (objs.get(i) instanceof Model) {
                  currentModel = (Model)objs.get(i);
                  openSimContext = OpenSimDB.getInstance().getContext(currentModel);
                  processCurrentModel();
                  populateYPopup();           
                  if (dFilterDlg != null){
                     dFilterDlg.dispose();
                     dFilterDlg=null;
                     muscleDialogUp=false;
                  }
                  updatePlotterWithSelection();
                  break;
               }
            }
         } else if (arg instanceof ModelEvent) {
            ModelEvent evt = (ModelEvent)arg;
            if (evt.getModel()==currentModel && evt.getOperation()==ModelEvent.Operation.Close){
               currentModel=null;
               populateYPopup();
               if (dFilterDlg != null){
                  dFilterDlg.dispose();
                  dFilterDlg=null;
                  muscleDialogUp=false;
               }
               updatePlotterWithSelection();
            }
            else if (evt.getOperation()==ModelEvent.Operation.SetCurrent){
               currentModel=evt.getModel();
               openSimContext = OpenSimDB.getInstance().getContext(currentModel);
               populateYPopup();           
               if (dFilterDlg != null){
                  dFilterDlg.dispose();
                  dFilterDlg=null;
                  muscleDialogUp=false;
               }
               updatePlotterWithSelection();
           }
            // Handle model open so that an AnalyzeTool is created.
         }
      }
   }
   /**
    * Run an analysis to compute curves for muscle analyses operating on current model
    * domainName is assumed to be set. If starts with Motion: then we assume we're plotting
    * against a motion curve otherwise it's vs. Generalized coordinate of the current model
    *
    */
   void runAnalysisTool(PlotterSourceAnalysis source, PlotterSourceInterface motion, String[] ranges) {
      AnalyzeTool tool = plotterModel.getAnalyzeTool(currentModel);
      if (motion instanceof PlotterSourceMotion || motion instanceof PlotterSourceStorage)
          tool.setSolveForEquilibrium(!motion.hasFullState(currentModel));
      else 
            tool.setSolveForEquilibrium(true);
      PlotterSourceAnalysis analysisSource = (PlotterSourceAnalysis)source;
      plotterModel.configureAnalyses(tool, analysisSource, domainName, ranges);
      
      // Save the state before running the analysis so that we can restore the model afterwards
      int numStates = currentModel.getNumStateVariables();
      ArrayStr stateNames = currentModel.getStateVariableNames();
      // Save states for later restoration so that the GUI and model are in sync. after analysis
      //double[] saveStates = new double[numStates];
      // FIX40 openSimContext.getStates(saveStates);
      State saveState = openSimContext.getCurrentStateCopy();
      Storage extendedMotionStorage;
      int key = (int) (java.lang.Math.random()*100);
      if (motion != null && motion instanceof PlotterSourceMotion){
         tool.setStartTime( motion.getStorage().getFirstTime());
         tool.setFinalTime( motion.getStorage().getLastTime());
         statesStorage = buildStatesStorageFromMotion(motion.getStorage(), isActivationOverride(), getActivationValue());
         tool.setStatesStorage(statesStorage);
         //statesStorage.print(key+"statesFromMotion.sto");
      } else {
         // Recreate stateStorage
         StatesReporter reporter = new StatesReporter(currentModel);
         reporter.begin(openSimContext.getCurrentStateRef());
         reporter.step(saveState, key);
         statesStorage=reporter.getStatesStorage();
         statesStorage.print("StatesFromReporter.sto");
          // make states for analysis by setting fiberlength and activation and form complete storage
         StateVector statevec = statesStorage.getStateVector(0);
         ArrayDouble statesForAnalysis = new ArrayDouble(statevec.getData());
         //int sz = statesForAnalysis.size();
         setNonzeroDefaultValues(stateNames, statesForAnalysis, isActivationOverride(), getActivationValue());
         double NUM_STEPS=100.0;
         // Replace short coordinate name with fullpathname
         Coordinate coord = currentModel.getCoordinateSet().get(domainName);
         int xIndex = statesStorage.getStateIndex(getDomainName());
         
         double domStart=(Double)jDomainStartTextField.getValue();
         double domEnd=(Double)jDomainEndTextField.getValue();
         if (coord.getMotionType() == Coordinate.MotionType.Rotational){
            domStart=Math.toRadians(domStart);
            if (domStart < coord.getRangeMin())
               domStart = coord.getRangeMin();
            domEnd=Math.toRadians(domEnd);
            if (domEnd > coord.getRangeMax())
               domEnd = coord.getRangeMax();
         }
         // Make 100 steps along the way, varying the quantity on sourceX by 1/100 of the distance between domStart & domEnd
         //statesStorage.purge();
         
          for(int i=0; i<NUM_STEPS; i++){
            double time = (double)i;
            double increment = 1./(NUM_STEPS-1)*(domEnd-domStart);
            double val=domStart+increment*i;
            //double degVal = Math.toDegrees(val);
            //System.out.println("Step="+i+", val="+degVal);
            statesForAnalysis.set(xIndex, val);
            StateVector newVector = new StateVector();
            // FIX40 openSimContext.computeConstrainedCoordinates(statesForAnalysis);
            newVector.setStates(time, statesForAnalysis.getAsVector());            
            statesStorage.append(newVector);
         }
         tool.setStatesStorage(statesStorage);
         tool.setStartTime(0.);
         tool.setFinalTime(NUM_STEPS);
         sourceX=new PlotterSourceAnalysis(currentModel, statesStorage, "");
         //statesStorage.print("toolInput"/*+key*/+".sto");
         //tool.setStatesFileName("toolInput"/*+key*/+".sto");
         tool.setModelFilename(currentModel.getInputFileName());
      }
      tool.setPrintResultFiles(false);
      //analysisSource.getStorage().purge();
      //tool.print("PlotterTool.xml");
      try {
         tool.run(true);
      } catch (IOException ex) {
         ex.printStackTrace();
      }
      openSimContext.setState(saveState);
      int na = currentModel.getAnalysisSet().getSize();
      Analysis analysis = currentModel.getAnalysisSet().get("MuscleAnalysis");
      analysisSource.updateStorage(analysis);
      //analysisSource.getStorage().print("toolOutput"/*+key*/+".sto");
      currentModel.getSimbodyEngine().convertRadiansToDegrees(analysisSource.getStorage());
      currentModel.getSimbodyEngine().convertRadiansToDegrees(statesStorage);
       ArrayStr coordsArray = new ArrayStr();
       coordsArray.append("all"); 
       MuscleAnalysis.safeDownCast(analysis).setCoordinates(coordsArray);   
   }

    private void setNonzeroDefaultValues(final ArrayStr stateNames, final ArrayDouble statesForAnalysis, boolean activationOverride, double activationValue) {
        for(int i=0; i<statesForAnalysis.size(); i++){
           /*if (stateNames.getitem(i).endsWith(".fiber_length"))
              statesForAnalysis[i]=0.01;
           else */if (stateNames.getitem(i).endsWith("/activation")) {
              if (activationOverride)
                 statesForAnalysis.setitem(i, activationValue);
              else
                 statesForAnalysis.setitem(i,1.0);
        }
    }
    }
   
   private Storage createStateStorageWithHeader(final Model mdl) {
      // set tool attributes
      Storage statesStorage = new Storage();
      // Column labels for StateStorage
      ArrayStr stateNames = mdl.getStateVariableNames();
      stateNames.insert(0, "time");
      statesStorage.setColumnLabels(stateNames);
      return statesStorage;
   }
   
   private void resetXY() {
      jXQtyTextField.setText("");
      jYQtyTextField.setText("");
      jYQtyTextField.setToolTipText("");
      jDomainStartTextField.setText("");
      jDomainEndTextField.setText("");
      jSelectedMusclesTextField.setText("");
      jCurveLegendTextField.setText("");
      jMuscleSelectButton.setEnabled(false);
      sourceX=null;
      jPlotterAddPlotButton.setEnabled(validateXY());
   }
   
   private void processCurrentModel() {
      if (currentModel!=null && !(currentModel instanceof ModelForExperimentalData) ){
         initAnalyses();
         statesStorage=createStateStorageWithHeader(currentModel);
      }
      populateYPopup();
      // Clear up any left over X, Y, muscles as they need to be re-hooked
      resetXY();
   }
   /**
    * Populate the top level pop up for Y-qty to use.
    */
   private void populateYPopup() {
      jSourcePopupMenu.removeAll();
      // Add analyses
      // Add Motions
      // Add files
      if (currentModel!=null ){
         // Built in
         ////////////////////////////////////////////////////////////////////////
         // Analyses
         ////////////////////////////////////////////////////////////////////////

         boolean addedSomething=false;
         /// Builtin
         if (! (currentModel instanceof ModelForExperimentalData)){
         for (int i=0; i< plotterModel.getBuiltinQuantities().length; i++){
            final String qName = plotterModel.getBuiltinQuantities()[i];
            if (qName.startsWith("moment")){   // Need a cascade menu to select a GC
               final String internalName=(qName.equalsIgnoreCase("moment"))?"Moment_":"MomentArm_";
               JMenu gcMenu = new JMenu(qName);
               SingleModelGuiElements guiElem = OpenSimDB.getInstance().getModelGuiElements(currentModel);
               String[] coordNames = guiElem.getUnconstrainedCoordinateNames();
               for(int j=0; j<coordNames.length; j++){
                  final String coordinateName=coordNames[j];
                  JMenuItem coordinateMenuItem = new JMenuItem(coordinateName);
                  coordinateMenuItem.addActionListener(new ActionListener(){
                     public void actionPerformed(ActionEvent e) {
                        //XX4
                        useMuscles(true);
                        jYQtyTextField.setText(coordinateName +" "+qName);
                        updateContextGuiElements();
                        sourceY=(new PlotterSourceAnalysis(currentModel, plotterModel.getStorage(internalName+coordinateName, currentModel), qName));
                  //printPlotDescriptor();
                     }
                  });
                  gcMenu.add(coordinateMenuItem);
               }
               jSourcePopupMenu.add(gcMenu);
               continue;
            }
            JMenuItem quantityMenuItem = new JMenuItem(qName);
            jSourcePopupMenu.add(quantityMenuItem);
            quantityMenuItem.addActionListener(
                    new ActionListener(){
                        public void actionPerformed(ActionEvent e) {
                            populateYQty(qName);
                        }
                        
            });
         }
         }
         // Other Analyses
         /*
         for(int i=0;i<analyses.getSize();i++){
            final Analysis nextAnalysis = analyses.get(i);
            if (nextAnalysis.getName().equalsIgnoreCase("MuscleAnalysis"))
               continue;
            JMenu nextAnalysisSubmenu = new JMenu(nextAnalysis.getName());
            // Get storages and create a menu item for each
            ArrayStorage storages = nextAnalysis.getStorageList();
            for(int storageIndex=0; storageIndex<storages.getSize(); storageIndex++){
               final Storage resultStorage = storages.get(storageIndex);
               JMenuItem quantityMenuItem = new JMenuItem(resultStorage.getName());
               nextAnalysisSubmenu.add(quantityMenuItem);
               quantityMenuItem.addActionListener(
                       new ActionListener(){
                  public void actionPerformed(ActionEvent e) {
                     // Show multipleSelect dialog with all muscles
                     // Populate YQty text field with selection
                     //XX1
                     jYQtyTextField.setText(nextAnalysis.getName()+":"+resultStorage.getName());
                     useMuscles(true);
                     updateContextGuiElements();
                     sourceY=(new PlotterSourceAnalysis(currentModel, resultStorage, nextAnalysis.getName()+":"+resultStorage.getName()));
                     //printPlotDescriptor();
                  }
               });
            }
            jSourcePopupMenu.add(nextAnalysisSubmenu);
            addedSomething=true;
         }  // Current model's analyses'
          **/
         jSourcePopupMenu.addSeparator();
         // Now motions
         ////////////////////////////////////////////////////////////////////////
         // Motions
         ////////////////////////////////////////////////////////////////////////
         addedSomething=false;
         ArrayList<Storage> motions = MotionsDB.getInstance().getModelMotions(currentModel);
         getPlotterModel().removeAllMotions();
         if (motions!=null){
            for(int i=0; i<motions.size(); i++){
               Storage nextMotionStorage = motions.get(i);
               // Make a copy with correct units e.g. degrees here
               final PlotterSourceInterface nextMotion=getPlotterModel().addMotion(nextMotionStorage);
               JMenuItem motionMenuItem = new JMenuItem(nextMotion.getDisplayName()+"...");
               motionMenuItem.addActionListener(new MotionSelectionListener(nextMotion));
                  jSourcePopupMenu.add(motionMenuItem);
                  addedSomething=true;
            }
         }
         if (addedSomething)
            jSourcePopupMenu.addSeparator();
      }
      ////////////////////////////////////////////////////////////////////////
      // Free Files
      ////////////////////////////////////////////////////////////////////////
      // Now add free files
      ArrayList<PlotterSourceFile> fileSources=plotterModel.getLoadedFileSources();
      if (fileSources!=null && fileSources.size()>0){
         for(int i=0; i<fileSources.size(); i++){
            final PlotterSourceFile nextSource = fileSources.get(i);
            JMenuItem fileMenuItem = new JMenuItem(nextSource.getDisplayName()+"...");
            fileMenuItem.addActionListener(new FileSelectionListener(nextSource));
               jSourcePopupMenu.add(fileMenuItem);
         }
      }
      JMenuItem newfileMenuItem = new JMenuItem("Load file...");
      newfileMenuItem.addActionListener(new ActionListener(){
         public void actionPerformed(ActionEvent e) {
            // browse for file
            String dataFilename = FileUtils.getInstance().browseForFilename(".sto, .mot", "Files containing data to plot", true, JPlotterPanel.this);
            if (dataFilename != null){
               PlotterSourceFile src= getPlotterModel().addFile(dataFilename);
               if (src==null) {
                  NotifyDescriptor.Message dlg =
                          new NotifyDescriptor.Message("The file specified:"+dataFilename+" is corrupt or have no valid data. Ignoring...");
                  DialogDisplayer.getDefault().notify(dlg);
                  JPlotterPanel.this.requestFocus(true);
                  return;
               };
               xQuantityButton.setEnabled(getPlotterModel().countSources()>0);
               populateYPopup();
               // Proceed as if it's selected'
               new FileSelectionListener(src).actionPerformed(null);
            }
         }});
         jSourcePopupMenu.add(newfileMenuItem);
   }
   
   private void useMuscles(boolean onOff) {
      boolean oldState=isBuiltinMuscleCurve();
      setBuiltinMuscleCurve(onOff);
      jMuscleSelectButton.setEnabled(onOff);
      if (onOff==false)
         jSelectedMusclesTextField.setText("");
   }
   
   public String getDomainName() {
      if (domainName==null){
         return "select X";
      }
      // Coordinate name now changed to full path in states file for 4.0
      if (builtinMuscleCurve){
          Coordinate coord = currentModel.getCoordinateSet().get(domainName);
          return (coord.getRelativePathName(currentModel)+"/value");
      }
      return domainName;
   }
   
   public void setDomainName(String domainName) {
      this.domainName = domainName;
   }
   
   public boolean isBuiltinMuscleCurve() {
      return builtinMuscleCurve;
   }
   
   public void setBuiltinMuscleCurve(boolean muscleSpecific) {
      this.builtinMuscleCurve = muscleSpecific;
   }
   
   private boolean validateXY() {
       boolean validXY=false;
       if (sourceY==null)
           return false;
      // Same file source in both X, Y
       if (sourceY instanceof PlotterSourceFile){
           if ((sourceX!=null) &&
                   (sourceX instanceof PlotterSourceFile) &&
                   sourceX.getStorage()==sourceY.getStorage())
               validXY=sourceX.isValidName(jXQtyTextField.getText());  // Check some X is selected and is valid for file
           return validXY;
       }
       if (sourceY instanceof PlotterSourceMotion){
           if ((sourceX!=null) &&
                   (sourceX instanceof PlotterSourceMotion) &&
                   sourceX.getStorage()==sourceY.getStorage())
               validXY=sourceX.isValidName(jXQtyTextField.getText());
           return validXY;
       }
       if (builtinMuscleCurve){
           // Check that some muscles have been selected
           //String dom = getDomainName();
           // Todo check dom is valid so that Add is not enabled prematurely
           //
           return (validateMuscleNames(rangeNames) && domainName!= null);
           // Y is one of the built in quantities or a user's analysis'
           //if (sourceY!=null && sourceY.size()>0){   // Same file source in both X, Y
           //if (sourceY instanceof PlotterSourceAnalysis){
           // X could be either a motion, motion column or a gc
           // quantity should always be non-empty either a muscle or a group of muscles
           //int x=0;
           //}
           //}
       }
       return false;
   }

   public boolean isClamp() {
      return clamp;
   }

   public void setClamp(boolean clamp) {
      this.clamp = clamp;
   }

   double getMinY() {
      return ((Double)jFormattedTextFieldYmin.getValue()).doubleValue();
   }

   double getMaxY() {
      return ((Double)jFormattedTextFieldYmax.getValue()).doubleValue();
   }

   public boolean isActivationOverride() {
      return activationOverride;
   }

   public void setActivationOverride(boolean activationOverride) {
      this.activationOverride = activationOverride;
   }

   double getActivationValue() {
      return ((Double)jActivationValueFormattedTextField.getValue()).doubleValue();
   }

   class handleReturnAction extends AbstractAction {
        JFormattedTextField jFormattedTextField;
        public handleReturnAction(JFormattedTextField textfield)
        {
           jFormattedTextField = textfield;
        }
        public void actionPerformed(ActionEvent e) {
           if (!jFormattedTextField.isEditValid()) { //The text is invalid.
              String text = jFormattedTextField.getText();
              // Try to parse the text into a double as it could be out of range, in this case truncate
              try {
                 NumberFormat numFormat = NumberFormat.getInstance();
                 if (numFormat instanceof DecimalFormat) {
                    ((DecimalFormat) numFormat).applyPattern("#,##0.#########");
                 }
                 double valueFromTextField = numFormat.parse(text).doubleValue();
                 jFormattedTextField.setText(numFormat.format(valueFromTextField));
                 jFormattedTextField.commitEdit();
              }
              catch (NumberFormatException ex){
                 // Really invalid text for a double
              }
              catch (ParseException ex){
                 // Really invalid text for a double
              }
         }
        }
     }

   public boolean isModelChanged() {
      return modelChanged;
   }

   public void setModelChanged(boolean modelChanged) {
      this.modelChanged = modelChanged;
   }
   
   /*
    * Locate the node corresponding to the passed in PlotCurve
    */
   public DefaultMutableTreeNode getNodeForCurve(PlotCurve cv) {
      PlotCurveNode figNode =  ((PlotTreeModel)plotterModel.getPlotTreeModel()).findCurveNode(cv);
      return figNode;
   }
   /*
    * This function is responsible for populating and handling the callbacks for the context
    * menu of the tree of plots shown on the right in the plotter dialog.
    */
   private void invokeTreePopupIfNeeded(int evtX, int evtY) {
      final TreePath clickedElement = jPlotsTree.getPathForLocation(evtX, evtY);
       
      //  Display the name of the selected tree element in the selection field
      String clickedElementName;
      if (clickedElement != null){
         Object[] pathObjects = clickedElement.getPath();
         int depth =pathObjects.length-1;
         final DefaultMutableTreeNode node = (DefaultMutableTreeNode)pathObjects[depth];
         PlotCurve cv=null;
         Plot dPlot=null;
         if (node instanceof PlotNode){ // Chart properties
            dPlot = ((Plot)node.getUserObject());
         } else if (node instanceof PlotCurveNode){
            cv = ((PlotCurve)node.getUserObject());
         }
         final PlotCurve cv2=cv; // keep a final reference to object so action listeners are happy
         
         JPopupMenu curvePopup = new JPopupMenu();
         if (cv ==null){ // A whole plot is picked
            JMenuItem propMenuItem = new JMenuItem("Properties...");
            propMenuItem.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                       plotterModel.getCurrentPlot().getChartPanel().doEditChartProperties();
                }});
            curvePopup.add(propMenuItem);
            JMenuItem exportMenuItem = new JMenuItem("Export Data...");
            exportMenuItem.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                       plotterModel.getCurrentPlot().exportDataToFile(frame);
                }});
            curvePopup.add(exportMenuItem);            
            curvePopup.show(jPlotsTree, evtX, evtY);
            return;
         }
         JMenuItem infoMenuItem = new JMenuItem("Info...");
         // Info button
         infoMenuItem.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                   if (cv2!=null)
                     displayCurveInfo(cv2);
                }

               private void displayCurveInfo(final PlotCurve cv2) {
                     // Make label of curve name, domain name, range name
                  String displayRangeName = "";
                  if (cv2.getRangeNames().length>1){
                     displayRangeName = getRangeNamesAsString("+", cv2.getRangeNames());
                  }
                  else
                     displayRangeName = cv2.getRangeNames()[0];

                  NotifyDescriptor.Message dlg =
                          new NotifyDescriptor.Message(cv2.getCurveSeries().getDescription());
                  DialogDisplayer.getDefault().notify(dlg);
               }});
         curvePopup.add(infoMenuItem);
         if (cv2 !=null){  // Show rename only for individual curves
            JMenuItem renamMenuItem = new JMenuItem("Rename...");
            renamMenuItem.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    NotifyDescriptor.InputLine dlg =
                            new NotifyDescriptor.InputLine("Current Name: ", "Rename");
                    dlg.setInputText(cv2.getLegend());
                    if(DialogDisplayer.getDefault().notify(dlg)==NotifyDescriptor.OK_OPTION){
                        String newName = dlg.getInputText();
                        // Fix tree display by forcing repaint
                        cv2.setLegend(newName);
                        cv2.getCurveSeries().fireSeriesChanged();
                        // Invoke nodesChanged so that the renderer updates
                        plotterModel.fireChangeEvent(node);
                    }
                }
            });
            curvePopup.add(renamMenuItem);
         }
         curvePopup.show(jPlotsTree, evtX, evtY);
      }
      
   }

   /** 
    * Handle file selection pick in Quantity Y popup
    */
   private class FileSelectionListener implements ActionListener {

      private PlotterSourceFile nextSource;

      public FileSelectionListener(PlotterSourceFile nextSource) {
         super();
         this.nextSource = nextSource;
      }

      public void actionPerformed(ActionEvent e) {
         sourceY=nextSource;
         sourceX = nextSource;
         PlotterQuantityNameFilterJPanel filterpanel = new PlotterQuantityNameFilterJPanel(sourceX);
         String dialogTitle="";
         if (sourceX instanceof PlotterSourceMotion)
            dialogTitle="Select motion quantity";
         else
            dialogTitle="Select data column(s) to plot";
         
         OpenSimDialog selectionDlg=DialogUtils.createDialogForPanelWithParent(getFrame(), filterpanel, dialogTitle);
         DialogUtils.addStandardButtons(selectionDlg);
         selectionDlg.setModal(true);
         selectionDlg.setVisible(true);
         // Replace with std Dlg ok
         
         if (selectionDlg.getDialogReturnValue()==selectionDlg.OK_OPTION) {
            jYQtyTextField.setText(filterpanel.getSelectedAsString());
            rangeNames = new String[filterpanel.getNumSelected()];
            System.arraycopy(filterpanel.getSelected(), 0, rangeNames, 0, filterpanel.getNumSelected());
            useMuscles(false);
            updateContextGuiElements();
            jPlotterAddPlotButton.setEnabled(validateXY());
         }
         
      }
   }

   /** 
    * Handle motion pick in Quantity Y popup
    */
   private class MotionSelectionListener implements ActionListener {

      private PlotterSourceInterface nextMotion;

      public MotionSelectionListener(PlotterSourceInterface nextMotion) {
         super();
         this.nextMotion = nextMotion;
      }

      public void actionPerformed(ActionEvent e) {
         sourceY=(nextMotion);
         sourceX = sourceY;
         PlotterQuantityNameFilterJPanel filterpanel = new PlotterQuantityNameFilterJPanel(sourceY);
         
         OpenSimDialog selectionDlg=DialogUtils.createDialogForPanelWithParent(getFrame(), filterpanel, "Select Motion Quantity");
         DialogUtils.addStandardButtons(selectionDlg);
         selectionDlg.setModal(true);
         selectionDlg.setVisible(true);
         /*
         JDialog qtySelectionDialog = new JDialog(getOwner(), "Select Motion Quantity", true);
         qtySelectionDialog.getContentPane().setLayout(new BorderLayout());
         qtySelectionDialog.getContentPane().add(filterpanel);
         qtySelectionDialog.doLayout();
         qtySelectionDialog.setVisible(true);
         */
         if (selectionDlg.getDialogReturnValue() == selectionDlg.OK_OPTION) {
            jYQtyTextField.setText( filterpanel.getSelectedAsString());
            rangeNames = new String[filterpanel.getNumSelected()];
            System.arraycopy(filterpanel.getSelected(), 0, rangeNames, 0, filterpanel.getNumSelected());
            useMuscles(false);
            updateContextGuiElements();
            jPlotterAddPlotButton.setEnabled(validateXY());
          }
          
      }
   }

   void printPlotDescriptor()
   {
       System.out.println("================================");
       System.out.println("Domain name =["+domainName+"]");
       if (rangeNames==null)
            System.out.println("Range name =["+null+"]");
       else{
           System.out.println("Range names length =["+rangeNames.length+"]");
           for(int i=0; i<rangeNames.length; i++)
                System.out.println("Range names =["+rangeNames[i]+"]");
       }
       System.out.println("flags sum=["+sumCurve+"], muscle=["+builtinMuscleCurve+"]");
       if (sourceX == null)
            System.out.println("sourceX =["+null+"]");
       else
           System.out.println("sourceX =["+sourceX.getClass().getName()+":"+sourceX.getDisplayName()+"]");
       if (sourceY == null)
            System.out.println("sourceY =["+null+"]");
       else{
           System.out.println("sourceY =["+sourceY.getClass().getName()+":"+sourceY.getDisplayName()+"]");
       }
        System.out.println("================================");
          
   }
   private Storage buildStatesStorageFromMotion(Storage motionsStorage, boolean overrideActivation, double newActivation) {
      // Make a new Storage with correct size/labels
      Storage outputStorage = new Storage();
      int numStates = currentModel.getNumStateVariables();
      ArrayStr stateNames = currentModel.getStateVariableNames();
      ArrayStr stateNamesWithTime = new ArrayStr(stateNames);
      stateNamesWithTime.insert(0, "time");
      outputStorage.setColumnLabels(stateNames);
      // Cycle thru stateNames if name exists in motionsStorage then use it,
      // if activation orride with passed in value if desired
      ArrayStr motionStateNames = motionsStorage.getColumnLabels();
      ArrayList<Integer> mapColumns= new ArrayList<Integer>(stateNames.getSize());
      ArrayList<Boolean> activationColumns= new ArrayList<Boolean>(stateNames.getSize());
      int numRows = motionsStorage.getSize();
      for(int i=0; i<numStates;i++){
         String currentStateName=stateNames.getitem(i);
         int indexInMotionFile = motionStateNames.findIndex(currentStateName)-1; // account 4 time
         mapColumns.add(i, indexInMotionFile);
         activationColumns.add(i, currentStateName.endsWith(".activation"));
      }
      double[] buffer = new double[numStates];
      for(int i=0; i<numRows; i++){
         StateVector statesFromMotion = motionsStorage.getStateVector(i);
         int numColumnsInMotionFile = statesFromMotion.getSize();
         ArrayDouble dataFromMotion=statesFromMotion.getData();
         StateVector outputStateVector = new StateVector(numStates);
         for(int j=0; j<numStates;j++){
            if (mapColumns.get(j)!=-2){
               buffer[j]=dataFromMotion.getitem(mapColumns.get(j));
            }
            else if (activationColumns.get(j))
               buffer[j]=newActivation;
            else
               buffer[j]=1.0;
            if (activationColumns.get(j) && overrideActivation)
               buffer[j]=newActivation;
         }
         //FIX40 openSimContext.computeConstrainedCoordinates(buffer);
         //FIX40 outputStateVector.setStates(statesFromMotion.getTime(), numStates, buffer);
         outputStorage.append(outputStateVector);
      }
      //outputStorage.print("motion2State.sto");
      return outputStorage;
   }

   private boolean validateMuscleNames(String[] rangeNames) {
      boolean validMuscles = false;
      if (rangeNames == null || currentModel==null)   // Have to have a model
         return false;
      
      SingleModelGuiElements guiElem = OpenSimDB.getInstance().getModelGuiElements(currentModel);
      Vector<String> muscleNames = guiElem.getMuscleNames();
      validMuscles = true;
      for(int i=0; i< rangeNames.length && validMuscles; i++){
         validMuscles=(muscleNames.contains(rangeNames[i]));
      }
      return validMuscles;
   }

   void updatePlotterWithSelection() {
            jPlotterAddPlotButton.setEnabled(validateXY());
   }

    public Frame getFrame() {
        return frame;
    }

    public void setFrame(Frame frame) {
        this.frame = frame;
        plotterModel.getCurrentPlot().setOwnerFrame(frame);
    }
    
    public void collapseControlPanel() {
        jSplitPane1.setDividerLocation(1000);
    }
    
    public ChartPanel getChartPanel()
    {
        return plotterModel.getCurrentPlot().getChartPanel();
    
    }
    
    public PlotCurve showAnalysisCurve(String qName, String muscleName, String genCoordName) {
       PlotCurve plotCurve=null;
       openSimContext = OpenSimDB.getInstance().getContext(currentModel);
       String[] muscleNames = muscleName.split("\\+");
       if (muscleNames.length>1){
            sumCurve = true;
       }
       if (qName.toLowerCase().startsWith("moment")){
           String coordName = qName.substring(qName.indexOf(".")+1);
           // Could be a moment or momentArm plot
           useMuscles(true);
           if (qName.toLowerCase().startsWith("momentarm")){
               sourceY=(new PlotterSourceAnalysis(currentModel, plotterModel.getStorage("MomentArm_"+coordName, currentModel), "moment arm"));
           }
           else {   // Just Moment
               sourceY=(new PlotterSourceAnalysis(currentModel, plotterModel.getStorage("Moment_"+coordName, currentModel), "moment"));               
           }
       }
       else
            populateYQty(qName);
       populateXQty(genCoordName);
       rangeNames = (muscleNames.length>1)? muscleNames : new String[]{muscleName};
       jPlotterAddCurveButtonActionPerformed(null);
       plotCurve = currentCurve;
       refreshPanel(plotCurve, muscleName);
       sumCurve = false;
       return plotCurve;
    }
    private void populateYQty(final String qtyName) {
        // Show multipleSelect dialog with all muscles
        // Populate YQty text field with selection
        //XX1
        jYQtyTextField.setText(qtyName);
        jYQtyTextField.setToolTipText(qtyName+" is calculated by setting activation to 1.0, then equilibriating the muscle, unless a states file is specified.");
        useMuscles(true);
        updateContextGuiElements();
        sourceY=(new PlotterSourceAnalysis(currentModel, plotterModel.getStorage(qtyName, currentModel), qtyName));
    }
    private void populateXQty(final String coordinateName) {
        setDomainName(coordinateName);
        sourceX = null;
        jXQtyTextField.setText(coordinateName);
        CoordinateSet cs = currentModel.getCoordinateSet();
        Coordinate coord = cs.get(coordinateName);
        if (coord.getMotionType() == Coordinate.MotionType.Rotational) {
            double conversionToGuiUnits = Math.toDegrees(1.0);
            domainFormat.setMinimumFractionDigits(0);
            domainFormat.setMaximumFractionDigits(5);
            domainFormatter.setMinimum(conversionToGuiUnits * coord.getRangeMin());
            domainFormatter.setMaximum(conversionToGuiUnits * coord.getRangeMax());
            jDomainStartTextField.setValue(conversionToGuiUnits * coord.getRangeMin());
            jDomainEndTextField.setValue(conversionToGuiUnits * coord.getRangeMax());
        } else {
            domainFormat.setMinimumFractionDigits(3);
            domainFormat.setMaximumFractionDigits(5);
            domainFormatter.setMinimum(coord.getRangeMin());
            domainFormatter.setMaximum(coord.getRangeMax());
            jDomainStartTextField.setValue(coord.getRangeMin());
            jDomainEndTextField.setValue(coord.getRangeMax());
        }

    }
    
    public void addDomainTickmark(double domValue) {
        plotterModel.getCurrentPlot().setDomainCrosshair(domValue);
    }

}
