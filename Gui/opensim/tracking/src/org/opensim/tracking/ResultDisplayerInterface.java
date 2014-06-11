/*
 * ResultDisplayerInterface.java
 *
 * Created on August 18, 2010, 5:21 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.tracking;

import org.opensim.modeling.Analysis;
import org.opensim.modeling.Model;

/**
 *
 * @author ayman
 */
public interface ResultDisplayerInterface {
    Analysis createAnalysis(Model model);    
    
    void removeAnalysis(Model model);

}
