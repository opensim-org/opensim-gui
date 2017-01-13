/*
 * DisableableObject.java
 *
 * Created on August 4, 2010, 1:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view.nodes;

/**
 *
 * @author ayman
 */
public interface DisableableObject {
    public boolean isEnabled();

    public void setEnabled(boolean disabled);
    
    public String getDisablePropertyName();
    
}
