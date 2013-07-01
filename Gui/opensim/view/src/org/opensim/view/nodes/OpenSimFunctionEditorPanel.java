
/*
 * OpenSimFunctionEditorPanel.java
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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.opensim.modeling.ArrayInt;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.Constant;
import org.opensim.modeling.Function;
import org.opensim.modeling.Model;
import org.opensim.modeling.MultiplierFunction;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.Units;
import org.opensim.modeling.XYFunctionInterface;
import org.opensim.view.ModelEvent;
import org.opensim.view.ObjectSetCurrentEvent;
import org.opensim.view.ObjectsChangedEvent;
import org.opensim.view.ObjectsDeletedEvent;
import org.opensim.view.ObjectsRenamedEvent;
import org.opensim.view.functionEditor.FunctionModifiedEvent;
import org.opensim.view.functionEditor.FunctionNode;
import org.opensim.view.functionEditor.FunctionPanel;
import org.opensim.view.functionEditor.FunctionPanelListener;
import org.opensim.view.functionEditor.FunctionPlot;
import org.opensim.view.functionEditor.FunctionRenderer;
import org.opensim.view.functionEditor.FunctionReplacedEvent;
import org.opensim.view.functionEditor.FunctionXYSeries;
import org.opensim.view.pub.OpenSimDB;

/**
 *
 * @author ayman
 */
public class OpenSimFunctionEditorPanel extends javax.swing.JPanel implements Observer, FunctionPanelListener {

    private void setPendingChanges(boolean b, boolean b0) {
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    private void notifyListeners(FunctionModifiedEvent functionModifiedEvent) {
        //throw new UnsupportedOperationException("Not yet implemented");
         }

    private String getFunctionTypeName(Function func) {
        MultiplierFunction mf = MultiplierFunction.safeDownCast(func);
        if (mf != null && mf.getFunction() != null) {
            return mf.getFunction().getConcreteClassName();
        } else {
            return func.getConcreteClassName();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof OpenSimDB) {
            // if current model is being switched due to open/close or change current then
            // update tool window
            if (arg instanceof ModelEvent) {
                final ModelEvent evt = (ModelEvent) arg;
                Model closedModel = evt.getModel();
                if (evt.getOperation() == ModelEvent.Operation.Close && Model.getCPtr(closedModel) == Model.getCPtr(this.model)) {
                    this.function = null;
                    this.xyFunction = null;
                    this.object = null;
                    this.relatedObjects = null;
                    this.model = null;
                    //backupFunction();
                    setPendingChanges(false, false);
                    setupComponent();
                }
            } else if (arg instanceof ObjectSetCurrentEvent) {
                ObjectSetCurrentEvent evt = (ObjectSetCurrentEvent) arg;
                Vector<OpenSimObject> objs = evt.getObjects();
                // If any of the event objects is a model, this means there is a new
                // current model. So update the panel.
                // Kluge: Handle model name change separately!
                if ((objs.size() == 1) && (objs.get(0) instanceof Model)) {
                    if (model != null && model.equals(objs.get(0))) {
                        // model was already current; don't do anything.
                        return;
                    }
                }
                for (int i = 0; i < objs.size(); i++) {
                    if (objs.get(i) instanceof Model) {
                        this.function = null;
                        this.xyFunction = null;
                        this.object = null;
                        this.relatedObjects = null;
                        this.model = null;
                        //backupFunction();
                        setPendingChanges(false, false);
                        setupComponent();
                        break;
                    }
                }
            } else if (arg instanceof ObjectsChangedEvent) {
                // Probably don't need to do anything here.
                // If an object changed such that the function
                // in the Function Editor should be closed,
                // then closeObject() or closeModel() should
                // be called.
            } else if (arg instanceof ObjectsRenamedEvent) {
                // Model and object are used to create the title,
                // so you don't care about relatedObjects here.
                ObjectsRenamedEvent evt = (ObjectsRenamedEvent) arg;
                if (model != null && object != null) {
                    Vector<OpenSimObject> objs = evt.getObjects();
                    for (int i = 0; i < objs.size(); i++) {
                        if (model.equals(objs.get(i)) || object.equals(objs.get(i))) {
                            updateFunctionTitle();
                        }
                    }
                }
            } else if (arg instanceof ObjectsDeletedEvent) {
                // If object or any of the relatedObjects is deleted,
                // clear the Function Editor.
                ObjectsDeletedEvent evt = (ObjectsDeletedEvent) arg;
                Vector<OpenSimObject> objs = evt.getObjects();
                if (anyObjectIsRelevant(evt.getObjects())) {
                    this.function = null;
                    this.xyFunction = null;
                    this.object = null;
                    this.relatedObjects = null;
                    this.model = null;
                    //backupFunction();
                    setPendingChanges(false, false);
                    setupComponent();
                }
            }
        }
    }

    public void toggleSelectedNode(int series, int node) {
        updateXYTextFields();
    }

    public void clearSelectedNodes() {
        updateXYTextFields();
    }

    public void replaceSelectedNode(int series, int node) {
        updateXYTextFields();
    }

    private void updateXYTextFields() {
        ArrayList<FunctionNode> selectedNodes = functionPanel.getSelectedNodes();

        if (selectedNodes.size() == 1) {
            double x = xyDataset.getXValue(selectedNodes.get(0).series, selectedNodes.get(0).node);
            double y = xyDataset.getYValue(selectedNodes.get(0).series, selectedNodes.get(0).node);
            xValueTextField.setText(coordinatesFormat.format(x));
            xValueTextField.setEnabled(true);
            yValueTextField.setText(coordinatesFormat.format(y));
        } else {
            xValueTextField.setText("");
            xValueTextField.setEnabled(false);
            yValueTextField.setText("");
        }
    }

    public boolean deleteNode(int series, int node) {
        if (function != null && node >= 0 && node < xyFunction.getNumberOfPoints()) {
            if (xyFunction.deletePoint(node)) {
                setPendingChanges(true, true);
                notifyListeners(new FunctionModifiedEvent(model, object, function));
                return true;
            }
        }
        return false;
    }

    public boolean deleteNodes(int series, ArrayInt nodes) {
        if (function == null || series != 0 || nodes.getSize() == 0) {
            return false;
        }

        if (xyFunction.deletePoints(nodes)) {
            setPendingChanges(true, true);
            notifyListeners(new FunctionModifiedEvent(model, object, function));
            return true;
        }
        return false;
    }

    public void addNode(int series, double x, double y) {
        if (function != null) {
            x *= options.XDisplayUnits.convertTo(options.XUnits);
            y *= options.YDisplayUnits.convertTo(options.YUnits);
            xyFunction.addPoint(x, y);
            setPendingChanges(true, true);
            notifyListeners(new FunctionModifiedEvent(model, object, function));
        }
    }

    public void duplicateNode(int series, int node) {
        if (function != null && node >= 0 && node < xyFunction.getNumberOfPoints()) {
            // Make a new point that is offset slightly in the X direction from
            // the point to be duplicated.
            double newX = xyFunction.getX(node) + 0.00001;
            double newY = xyFunction.getY(node);
            addNode(0, newX, newY);
        }
    }

    private void cropDragVector(double dragVector[]) {
        // Don't allow any dragged node to get within 'gapMargin' of either of its neighbors in the X dimension.
        double minGap = 99999999.9;
        double gapMargin = 0.000001;
        ArrayList<FunctionNode> selectedNodes = functionPanel.getSelectedNodes();
        if (dragVector[0] < 0.0) {  // dragging to the left
            for (int i = 0; i < selectedNodes.size(); i++) {
                int index = selectedNodes.get(i).node;
                double gap = minGap;
                if (index == 0) {  // there is no left neighbor, so the plot's left edge is the boundary
                    gap = xyFunction.getX(index) - xyPlot.getDomainAxis().getLowerBound() * options.XDisplayUnits.convertTo(options.XUnits);
                    // If the node is already outside the plot area, don't crop it against the plot edge
                    if (gap < 0.0) {
                        gap = minGap;
                    }
                } else if (!functionPanel.isNodeSelected(0, index - 1)) {  // left neighbor is not selected, so it is the boundary
                    gap = xyFunction.getX(index) - xyFunction.getX(index - 1);
                } else {  // left neighbor is selected, so there is no boundary for this node
                    continue;
                }
                if (gap < minGap) {
                    minGap = gap;
                }
            }
            // minGap is the smallest [positive] distance between a dragged node and its
            // left neighbor (if unselected). dragVector[0] can't be a larger negative
            // number than this value.
            minGap -= gapMargin;
            if (dragVector[0] < -minGap) {
                dragVector[0] = -minGap;
            }
        } else if (dragVector[0] > 0.0) {  // dragging to the right
            for (int i = 0; i < selectedNodes.size(); i++) {
                int index = selectedNodes.get(i).node;
                double gap = minGap;
                if (index == xyFunction.getNumberOfPoints() - 1) {  // there is no right neighbor, so the plot's right edge is the boundary
                    gap = xyPlot.getDomainAxis().getUpperBound() * options.XDisplayUnits.convertTo(options.XUnits) - xyFunction.getX(index);
                    // If the node is already outside the plot area, don't crop it against the plot edge
                    if (gap < 0.0) {
                        gap = minGap;
                    }
                } else if (!functionPanel.isNodeSelected(0, index + 1)) {  // right neighbor is not selected, so it is the boundary
                    gap = xyFunction.getX(index + 1) - xyFunction.getX(index);
                } else {  // right neighbor is selected, so there is no boundary for this node
                    continue;
                }
                if (gap < minGap) {
                    minGap = gap;
                }
            }
            // minGap is the smallest [positive] distance between a dragged node and its
            // right neighbor (if unselected). dragVector[0] can't be a larger positive
            // number than this value.
            minGap -= gapMargin;
            if (dragVector[0] > minGap) {
                dragVector[0] = minGap;
            }
        }
    }

    public void dragSelectedNodes(int series, int node, double dragVector[]) {
        dragVector[0] *= options.XDisplayUnits.convertTo(options.XUnits);
        dragVector[1] *= options.YDisplayUnits.convertTo(options.YUnits);
        cropDragVector(dragVector);
        // Now move all the function points by dragVector.
        ArrayList<FunctionNode> selectedNodes = functionPanel.getSelectedNodes();
        for (int i = 0; i < selectedNodes.size(); i++) {
            int index = selectedNodes.get(i).node;
            double newX = xyFunction.getX(index) + dragVector[0];
            double newY = xyFunction.getY(index) + dragVector[1];
            xyFunction.setX(index, newX);
            xyFunction.setY(index, newY);
            newX *= options.XUnits.convertTo(options.XDisplayUnits);
            newY *= options.YUnits.convertTo(options.YDisplayUnits);
            xySeries.updateByIndex(index, newX, newY);
        }
        updateXYTextFields();
        setPendingChanges(true, true);
        notifyListeners(new FunctionModifiedEvent(model, object, function));
    }

    public static class FunctionEditorOptions {

        public FunctionEditorOptions() {
            title = "";
            XLabel = "";
            YLabel = "";
            XUnits = new Units(Units.UnitType.Radians);
            XDisplayUnits = new Units(Units.UnitType.Radians);
            YUnits = new Units(Units.UnitType.Meters);
            YDisplayUnits = new Units(Units.UnitType.Meters);
        }
        public String title;         // title of function
        public String XLabel;        // label of X axis
        public String YLabel;        // label of Y axis
        public Units XUnits;         // units of X array passed into function editor
        public Units XDisplayUnits;  // units of X array to display to user
        public Units YUnits;         // units of Y array passed into function editor
        public Units YDisplayUnits;  // units of Y array to display to user
    }
    private JFreeChart chart = null;
    private FunctionPanel functionPanel = null;
    private FunctionRenderer renderer = null;
    private Function function = null;
    private XYFunctionInterface xyFunction = null;
    private FunctionEditorOptions options = new FunctionEditorOptions();
    private Function savedFunction = null;
    private Model model = null; // the model this function is from
    private Vector<OpenSimObject> relatedObjects = null; // usually, parents of 'object'
    private OpenSimObject object = null; // the object (muscle, joint, etc.) this function is from
    private FunctionXYSeries xySeries = null;
    private FunctionPlot xyPlot = null;
    private XYDataset xyDataset = null;
    private NumberFormat coordinatesFormat = new DecimalFormat("0.00000");
    private Paint highlightPaint = Color.BLUE;
    private boolean pendingChanges = false;
    private String[] functionTypeNames;

    public OpenSimFunctionEditorPanel(Function functionToEdit) {
        function = functionToEdit;
        xyFunction = new XYFunctionInterface(function);
        savedFunction = Function.safeDownCast(functionToEdit.clone());
        initComponents();
        ArrayStr functionClassNames = OpenSimObject.getFunctionClassNames();
        functionTypeNames = new String[functionClassNames.size()];
        for (int i = 0; i < functionClassNames.size(); i++) {
            functionTypeNames[i] = functionClassNames.getitem(i);
        }
        typeComboBox.setModel(new javax.swing.DefaultComboBoxModel(functionTypeNames));
        setupComponent();

    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        FunctionEditorScrollPane = new javax.swing.JScrollPane();
        functionDescriptionLabel = new javax.swing.JLabel();
        FunctionEditorPanel = new javax.swing.JPanel();
        xValueTextField = new javax.swing.JTextField();
        functionJPanel = new javax.swing.JPanel();
        xValueLabel = new javax.swing.JLabel();
        yValueLabel = new javax.swing.JLabel();
        yValueTextField = new javax.swing.JTextField();
        typeLabel = new javax.swing.JLabel();
        typeComboBox = new javax.swing.JComboBox();
        crosshairsCheckBox = new javax.swing.JCheckBox();
        PropertiesButton = new javax.swing.JButton();
        restoreFunctionButton = new javax.swing.JButton();

        functionDescriptionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        functionDescriptionLabel.setText(org.openide.util.NbBundle.getMessage(OpenSimFunctionEditorPanel.class, "OpenSimFunctionEditorPanel.functionDescriptionLabel.text")); // NOI18N

        xValueTextField.setMaximumSize(new java.awt.Dimension(100, 21));
        xValueTextField.setMinimumSize(new java.awt.Dimension(100, 21));
        xValueTextField.setPreferredSize(new java.awt.Dimension(100, 21));
        xValueTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xValueTextFieldxValueActionPerformed(evt);
            }
        });
        xValueTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                xValueTextFieldxValueFocusLost(evt);
            }
        });

        functionJPanel.setMinimumSize(new java.awt.Dimension(25, 25));

        javax.swing.GroupLayout functionJPanelLayout = new javax.swing.GroupLayout(functionJPanel);
        functionJPanel.setLayout(functionJPanelLayout);
        functionJPanelLayout.setHorizontalGroup(
            functionJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 508, Short.MAX_VALUE)
        );
        functionJPanelLayout.setVerticalGroup(
            functionJPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 290, Short.MAX_VALUE)
        );

        xValueLabel.setText(org.openide.util.NbBundle.getMessage(OpenSimFunctionEditorPanel.class, "OpenSimFunctionEditorPanel.xValueLabel.text")); // NOI18N

        yValueLabel.setText(org.openide.util.NbBundle.getMessage(OpenSimFunctionEditorPanel.class, "OpenSimFunctionEditorPanel.yValueLabel.text")); // NOI18N

        yValueTextField.setMaximumSize(new java.awt.Dimension(100, 21));
        yValueTextField.setMinimumSize(new java.awt.Dimension(100, 21));
        yValueTextField.setPreferredSize(new java.awt.Dimension(100, 21));
        yValueTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yValueTextFieldyValueActionPerformed(evt);
            }
        });
        yValueTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                yValueTextFieldyValueFocusLost(evt);
            }
        });

        typeLabel.setText(org.openide.util.NbBundle.getMessage(OpenSimFunctionEditorPanel.class, "OpenSimFunctionEditorPanel.typeLabel.text")); // NOI18N

        typeComboBox.setEnabled(false);
        typeComboBox.setMaximumSize(new java.awt.Dimension(145, 24));
        typeComboBox.setMinimumSize(new java.awt.Dimension(145, 24));
        typeComboBox.setPreferredSize(new java.awt.Dimension(145, 24));

        crosshairsCheckBox.setText(org.openide.util.NbBundle.getMessage(OpenSimFunctionEditorPanel.class, "OpenSimFunctionEditorPanel.crosshairsCheckBox.text")); // NOI18N
        crosshairsCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        crosshairsCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        crosshairsCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                crosshairsCheckBoxStateChanged(evt);
            }
        });

        PropertiesButton.setText(org.openide.util.NbBundle.getMessage(OpenSimFunctionEditorPanel.class, "OpenSimFunctionEditorPanel.PropertiesButton.text")); // NOI18N
        PropertiesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PropertiesButtonActionPerformed(evt);
            }
        });

        restoreFunctionButton.setText(org.openide.util.NbBundle.getMessage(OpenSimFunctionEditorPanel.class, "OpenSimFunctionEditorPanel.restoreFunctionButton.text")); // NOI18N
        restoreFunctionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restoreFunctionButtonrestoreFunctionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout FunctionEditorPanelLayout = new javax.swing.GroupLayout(FunctionEditorPanel);
        FunctionEditorPanel.setLayout(FunctionEditorPanelLayout);
        FunctionEditorPanelLayout.setHorizontalGroup(
            FunctionEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FunctionEditorPanelLayout.createSequentialGroup()
                .addGroup(FunctionEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(FunctionEditorPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(functionJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(FunctionEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, FunctionEditorPanelLayout.createSequentialGroup()
                            .addGap(22, 22, 22)
                            .addComponent(xValueLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(xValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(21, 21, 21)
                            .addComponent(yValueLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(yValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(28, 28, 28)
                            .addComponent(crosshairsCheckBox)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(PropertiesButton))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, FunctionEditorPanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(typeLabel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(restoreFunctionButton))))
                .addContainerGap())
        );
        FunctionEditorPanelLayout.setVerticalGroup(
            FunctionEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FunctionEditorPanelLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(functionJPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(17, 17, 17)
                .addGroup(FunctionEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(xValueLabel)
                    .addComponent(xValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(yValueLabel)
                    .addComponent(yValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(crosshairsCheckBox)
                    .addComponent(PropertiesButton))
                .addGap(26, 26, 26)
                .addGroup(FunctionEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(typeLabel)
                    .addComponent(restoreFunctionButton))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(FunctionEditorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(FunctionEditorPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void xValueTextFieldxValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xValueTextFieldxValueActionPerformed

        xValueEntered((javax.swing.JTextField) evt.getSource());     }//GEN-LAST:event_xValueTextFieldxValueActionPerformed

    private void xValueTextFieldxValueFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_xValueTextFieldxValueFocusLost

        if (!evt.isTemporary()) {             xValueEntered((javax.swing.JTextField) evt.getSource());         }     }//GEN-LAST:event_xValueTextFieldxValueFocusLost

    private void yValueTextFieldyValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yValueTextFieldyValueActionPerformed

        yValueEntered((javax.swing.JTextField) evt.getSource());     }//GEN-LAST:event_yValueTextFieldyValueActionPerformed

    private void yValueTextFieldyValueFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_yValueTextFieldyValueFocusLost

        if (!evt.isTemporary()) {             yValueEntered((javax.swing.JTextField) evt.getSource());         }     }//GEN-LAST:event_yValueTextFieldyValueFocusLost

        private void crosshairsCheckBoxStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_crosshairsCheckBoxStateChanged

            if (function != null && functionPanel != null) {
                if (evt.getStateChange() == ItemEvent.SELECTED) {
                    functionPanel.setMandatoryCrosshairs(true);
                } else {
                    functionPanel.setMandatoryCrosshairs(false);
        }         }     }//GEN-LAST:event_crosshairsCheckBoxStateChanged

    private void PropertiesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PropertiesButtonActionPerformed

        if (functionPanel != null) {             functionPanel.doEditChartProperties();         }     }//GEN-LAST:event_PropertiesButtonActionPerformed

private void restoreFunctionButtonrestoreFunctionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restoreFunctionButtonrestoreFunctionActionPerformed
      Function func = function;
      // function must be set to null before calling replaceFunction, but I'm not sure why.
      //function = null;
      //notifyListeners(new FunctionReplacedEvent(model, object, func, savedFunction));
      restoreFunction();
      setupComponent();
      setPendingChanges(false, true);
      functionJPanel.validate();
      this.repaint();
}//GEN-LAST:event_restoreFunctionButtonrestoreFunctionActionPerformed

    private void xValueEntered(javax.swing.JTextField field) {
        // TODO: check old value of each selected point to see if anything really changes
        if (field.getText().length() > 0) {
            double newValue;
            try {
                newValue = coordinatesFormat.parse(field.getText()).doubleValue();
            } catch (ParseException ex) {
                Toolkit.getDefaultToolkit().beep();
                field.setText("");
                return;
            }
            field.setText(coordinatesFormat.format(newValue));
            ArrayList<FunctionNode> selectedNodes = functionPanel.getSelectedNodes();
            for (int i = 0; i < selectedNodes.size(); i++) {
                int index = selectedNodes.get(i).node;
                xySeries.updateByIndex(index, newValue, xyFunction.getY(index));
                newValue *= options.XDisplayUnits.convertTo(options.XUnits);
                xyFunction.setX(index, newValue);
            }
            setPendingChanges(true, true);
            notifyListeners(new FunctionModifiedEvent(model, object, function));
        }
    }

    private void yValueEntered(javax.swing.JTextField field) {
        // TODO: check old value of each selected point to see if anything really changes
        if (field.getText().length() > 0) {
            double newValue;
            try {
                newValue = coordinatesFormat.parse(field.getText()).doubleValue();
            } catch (ParseException ex) {
                Toolkit.getDefaultToolkit().beep();
                field.setText("");
                return;
            }
            field.setText(coordinatesFormat.format(newValue));
            ArrayList<FunctionNode> selectedNodes = functionPanel.getSelectedNodes();
            for (int i = 0; i < selectedNodes.size(); i++) {
                int index = selectedNodes.get(i).node;
                xySeries.updateByIndex(index, newValue);
                newValue *= options.YDisplayUnits.convertTo(options.YUnits);
                xyFunction.setY(index, newValue);
            }
            setPendingChanges(true, true);
            notifyListeners(new FunctionModifiedEvent(model, object, function));
        }
    }

    public void setupComponent() {
        // Remove the prior FunctionPanel, if any
        functionJPanel.removeAll();

        if (function != null) {
            Constant constant = Constant.safeDownCast(function);
            if (constant != null) {
                //typeComboBox.setEnabled(true);
                typeComboBox.setSelectedIndex(findElement(functionTypeNames, constant.getConcreteClassName()));
                javax.swing.JLabel valueLabel = new javax.swing.JLabel();
                valueLabel.setText("value");
                javax.swing.JTextField valueField = new javax.swing.JTextField();
                valueField.setText(coordinatesFormat.format(constant.getValue()));
                xValueTextField.setEnabled(false);
                yValueTextField.setEnabled(false);
                crosshairsCheckBox.setEnabled(false);
                PropertiesButton.setEnabled(false);
                valueField.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        constantValueActionPerformed(evt);
                    }
                });
                valueField.addFocusListener(new java.awt.event.FocusAdapter() {

                    public void focusLost(java.awt.event.FocusEvent evt) {
                        constantValueFocusLost(evt);
                    }
                });
                org.jdesktop.layout.GroupLayout constantLayout = new org.jdesktop.layout.GroupLayout(functionJPanel);
                functionJPanel.setLayout(constantLayout);
                constantLayout.setHorizontalGroup(
                        constantLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(constantLayout.createSequentialGroup().add(121, 121, 121).add(valueLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(valueField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap(317, Short.MAX_VALUE)));
                constantLayout.setVerticalGroup(
                        constantLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(org.jdesktop.layout.GroupLayout.TRAILING, constantLayout.createSequentialGroup().addContainerGap(118, Short.MAX_VALUE).add(constantLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE).add(valueLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(valueField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)).add(118, 118, 118)));
            } else {
                XYSeriesCollection seriesCollection = new XYSeriesCollection();
                xySeries = new FunctionXYSeries("function");
                // The following mysterious line has the side effect of recomputing coefficients that are internal to the function
                // and that are not part of the state and not updated by setting X,Y vectors or createSystem
                xyFunction.setX(0, xyFunction.getX(0));
                
                for (int i = 0; i < xyFunction.getNumberOfPoints(); i++) {
                    xySeries.add(new XYDataItem(xyFunction.getX(i) * (options.XUnits.convertTo(options.XDisplayUnits)),
                            xyFunction.getY(i) * (options.YUnits.convertTo(options.YDisplayUnits))));
                }
                seriesCollection.addSeries(xySeries);
                chart = FunctionPanel.createFunctionChart(
                        "", options.XLabel, options.YLabel, seriesCollection,
                        true, true);
                xyPlot = (FunctionPlot) chart.getXYPlot();
                xyDataset = xyPlot.getDataset();
                renderer = new FunctionRenderer(xyFunction);
                Shape circle = new Ellipse2D.Float(-3, -3, 6, 6);
                renderer.setBaseShapesVisible(true);
                renderer.setBaseShapesFilled(true);
                renderer.setSeriesShape(0, circle);
                renderer.setBaseSeriesVisibleInLegend(false);
                renderer.setDrawOutlines(true);
                renderer.setUseFillPaint(true);
                renderer.setSeriesStroke(0, new BasicStroke(1.5f));
                renderer.setSeriesOutlineStroke(0, new BasicStroke(1.0f));
                renderer.setFunctionPaint(0, Color.BLUE);
                renderer.setFunctionDefaultFillPaint(0, Color.WHITE);
                renderer.setFunctionHighlightFillPaint(0, highlightPaint);
                renderer.setXUnits(options.XUnits);
                renderer.setXDisplayUnits(options.XDisplayUnits);
                renderer.setYUnits(options.YUnits);
                renderer.setYDisplayUnits(options.YDisplayUnits);
                ValueAxis va = xyPlot.getRangeAxis();
                if (va instanceof NumberAxis) {
                    NumberAxis na = (NumberAxis) va;
                    na.setAutoRangeIncludesZero(false);
                    na.setAutoRangeStickyZero(false);
                    xyPlot.setRangeAxis(na);
                }
                xyPlot.setRenderer(renderer);
                functionPanel = new FunctionPanel(chart);
                functionJPanel.setLayout(new BorderLayout());
                functionJPanel.add(functionPanel);
                functionPanel.addFunctionPanelListener(this);
                functionJPanel.setEnabled(xyFunction.isSpecifiedByControlPoints());
                //typeComboBox.setEnabled(true);
                typeComboBox.setSelectedIndex(findElement(functionTypeNames, getFunctionTypeName(function)));
                xValueTextField.setEnabled(true);
                yValueTextField.setEnabled(true);
                crosshairsCheckBox.setEnabled(true);
                PropertiesButton.setEnabled(true);
                // Update the crosshair state, to handle cases like:
                // 1. while editing function, turned crosshairs on
                // 2. switched type to Constant
                // 3. switched type back to a function (crosshair checkbox is still checked)
                functionPanel.setMandatoryCrosshairs(crosshairsCheckBox.isSelected());
            }
            updateFunctionTitle();
        } else {
            //typeComboBox.setEnabled(false);
            xValueTextField.setEnabled(false);
            yValueTextField.setEnabled(false);
            crosshairsCheckBox.setEnabled(false);
            PropertiesButton.setEnabled(false);
            functionDescriptionLabel.setText("");
            //clearChangeListenerList();
        }
        /*functionDescriptionLabel.setText(functionDescriptionLabel.getText()+
                "editable="+String.valueOf(xyFunction.isSpecifiedByControlPoints()));*/
        Dimension d = new Dimension(500, 430);
        FunctionEditorPanel.setPreferredSize(d);
    }

    private void updateFunctionTitle() {
        String modelName, objectName;
        if (model == null) {
            modelName = "";
        } else {
            modelName = model.getName();
        }
        if (object == null) {
            objectName = "";
        } else {
            objectName = object.getName();
        }
        functionDescriptionLabel.setText(modelName + ": " + objectName + ": " + options.title);
    }

    private int findElement(String[] nameList, String name) {
        int i;
        for (i = 0; i < nameList.length; i++) {
            if (nameList[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private void constantValueFocusLost(java.awt.event.FocusEvent evt) {
        if (!evt.isTemporary()) {
            constantValueEntered((javax.swing.JTextField) evt.getSource());
        }
    }

    private void constantValueActionPerformed(java.awt.event.ActionEvent evt) {
        constantValueEntered((javax.swing.JTextField) evt.getSource());
    }

    private void constantValueEntered(javax.swing.JTextField field) {
        Constant constant = Constant.safeDownCast(function);
        if (constant != null) {
            if (field.getText().length() > 0) {
                double oldValue = constant.getValue() * options.YUnits.convertTo(options.YDisplayUnits);
                double newValue;
                try {
                    newValue = coordinatesFormat.parse(field.getText()).doubleValue();
                } catch (ParseException ex) {
                    Toolkit.getDefaultToolkit().beep();
                    newValue = oldValue;
                }
                field.setText(coordinatesFormat.format(newValue));
                if (newValue != oldValue) {
                    newValue *= options.YDisplayUnits.convertTo(options.YUnits);
                    constant.setValue(newValue);
                    setPendingChanges(true, true);
                    notifyListeners(new FunctionModifiedEvent(model, object, function));
                }
            }
        }
    }

    private boolean anyObjectIsRelevant(Vector<OpenSimObject> objects) {
        for (int i = 0; i < objects.size(); i++) {
            if (model != null && model.equals(objects.get(i))) {
                return true;
            }
            if (object != null && object.equals(objects.get(i))) {
                return true;
            }
            if (relatedObjects != null) {
                for (int j = 0; j < relatedObjects.size(); j++) {
                    if (objects.get(i).equals(relatedObjects.get(j))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private void restoreFunction() {
      if (savedFunction != null) {
         function.assign(savedFunction);
         xyFunction = new XYFunctionInterface(function);
         // make a new backup copy
         //backupFunction();
      }
   }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel FunctionEditorPanel;
    private javax.swing.JScrollPane FunctionEditorScrollPane;
    private javax.swing.JButton PropertiesButton;
    private javax.swing.JCheckBox crosshairsCheckBox;
    private javax.swing.JLabel functionDescriptionLabel;
    private javax.swing.JPanel functionJPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton restoreFunctionButton;
    private javax.swing.JComboBox typeComboBox;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JLabel xValueLabel;
    private javax.swing.JTextField xValueTextField;
    private javax.swing.JLabel yValueLabel;
    private javax.swing.JTextField yValueTextField;
    // End of variables declaration//GEN-END:variables
}
