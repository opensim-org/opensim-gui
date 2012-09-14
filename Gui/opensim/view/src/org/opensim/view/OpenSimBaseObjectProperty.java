/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view;

import org.openide.nodes.PropertySupport.ReadOnly;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.nodes.OpenSimObjectNode;

/**
 *
 * @author Ayman
 */
public abstract class OpenSimBaseObjectProperty extends ReadOnly<OpenSimObject> {

    public OpenSimBaseObjectProperty(String name, String displayName, String shortDescription) {
        super(name, OpenSimObject.class, displayName, shortDescription);
    }

    @Override
    public String getDisplayName() {
        return super.getDisplayName();
    }

    @Override
    public String getShortDescription() {
        return super.getShortDescription();
    }
    OpenSimObjectNode parentNode;
    AbstractProperty ap;
   
}
