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
    protected boolean disabled = false;

    public DisablablModelComponentNode(OpenSimObject obj) {
        super(obj);
        updateDisabledFlag();
    }

    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(boolean disabled) {
        //OpenSimDB.getInstance().disableConstraint(getOpenSimObject(), disabled);
        this.disabled = disabled;
        if (disabled) {
            setIconBaseWithExtension("/org/opensim/view/nodes/icons/disabledNode.png");
        } 
    }

    private void updateDisabledFlag() {
        OpenSimObject c = getOpenSimObject();
        AbstractProperty ap = c.getPropertyByName("isDisabled");
        disabled = PropertyHelper.getValueBool(ap);
    }

    @Override
    public void refreshNode() {
        super.refreshNode();
        updateDisabledFlag();
        setDisabled(disabled);
    }

    @Override
    // return diabled icon if disabled is true else null
    public Image getIcon(int i) {
        URL imageURL;
        if (disabled){
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
