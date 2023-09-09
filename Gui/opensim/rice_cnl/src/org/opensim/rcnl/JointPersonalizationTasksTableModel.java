/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.rcnl;

import java.util.Observable;
import java.util.Observer;
import javax.swing.table.AbstractTableModel;
import org.opensim.modeling.OpenSimObject;

/**
 *
 * @author Ayman-NMBL
 */
public class JointPersonalizationTasksTableModel extends AbstractTableModel implements Observer {

    OpenSimObject jointPersonalizationTaskList;
    private String[] columnNames = new String[]{"Enabled", "Name", "Marker File"};

    @Override
    public int getRowCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int i, int i1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(Observable o, Object o1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public JointPersonalizationTasksTableModel(OpenSimObject jointPersonalizationTaskList){
        this.jointPersonalizationTaskList = jointPersonalizationTaskList;
    }
}
