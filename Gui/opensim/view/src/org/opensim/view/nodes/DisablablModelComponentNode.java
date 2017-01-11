/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view.nodes;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PropertyHelper;

/**
 *
 * @author Ayman
 */
public abstract class DisablablModelComponentNode extends OneModelComponentNode implements DisableableObject {
    protected boolean enabled = true;

    public DisablablModelComponentNode(OpenSimObject obj) {
        super(obj);
        updateEnabledFlag();
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        //OpenSimDB.getInstance().disableConstraint(getOpenSimObject(), enabled);
        this.enabled = enabled;
        if (!enabled) {
            setIconBaseWithExtension("/org/opensim/view/nodes/icons/disabledNode.png");
        } 
    }

    private void updateEnabledFlag() {
        OpenSimObject c = getOpenSimObject();
        AbstractProperty ap = c.getPropertyByName(getDisablePropertyName());
        enabled = PropertyHelper.getValueBool(ap);
    }

    @Override
    public void refreshNode() {
        super.refreshNode();
        updateEnabledFlag();
        setEnabled(enabled);
    }

    @Override
    // return diabled icon if enabled is true else null
    public Image getIcon(int i) {
        URL imageURL;
        if (!enabled){
            imageURL = this.getClass().getResource("icons/disabledNode.png");
        
            if (imageURL != null) { 
                return new ImageIcon(imageURL, "Controller").getImage();
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public void updateSelfFromObject() {
        super.updateSelfFromObject();
        refreshNode();
    }
    
}
