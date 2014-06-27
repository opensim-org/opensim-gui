/*
 * LabOutputInterface.java
 *
 * Created on August 19, 2010, 3:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.k12;

import org.opensim.modeling.Model;

/**
 *
 * @author ayman
 */
public interface LabOutputInterface {
    String getQuantitySpecfication();

    void setQuantitySpecfication(String quantitySpecfication);

    void updateDisplay(double aT);

    void connectQuantityToSource(Model model);

    void cleanup();
    
}
