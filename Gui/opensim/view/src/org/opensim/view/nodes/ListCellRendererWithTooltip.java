/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view.nodes;

import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 *
 * @author ayman
 */
public class ListCellRendererWithTooltip extends BasicComboBoxRenderer {
    JComboBox combox;
    public ListCellRendererWithTooltip(JComboBox comboBox){
        this.combox = comboBox;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); //To change body of generated methods, choose Tools | Template
        ((JLabel) comp).setToolTipText(value.toString());
        return comp;
    }
    
}
