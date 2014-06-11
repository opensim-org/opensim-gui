/*
 * ForceListModel.java
 *
 * Created on January 28, 2010, 3:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view.experimentaldata;

import javax.swing.DefaultListModel;

/**
 *
 * @author ayman
 */
public class MotionObjectsListModel extends DefaultListModel {
    AnnotatedMotion  dMotion;
    /** Creates a new instance of ForceListModel */
    public MotionObjectsListModel(AnnotatedMotion  motion) {
        super();
        this.dMotion = motion;
        initModel();
    }

    private void initModel() {
        clear();
        
        for (int i=0; i<dMotion.getClassified().size(); i++){
            ExperimentalDataObject f= dMotion.getClassified().get(i);
            // No points/markers or moments for now
            if (f instanceof MotionObjectPointForce)
                addElement(f);
        }
    }
    
}
