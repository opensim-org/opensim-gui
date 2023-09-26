/* -------------------------------------------------------------------------- *
 * OpenSim: OneForceNode.java                                                 *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Peter Loan                                         *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain a  *
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0          *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 * -------------------------------------------------------------------------- */
/*
 *
 * OneForceNode
 * Author(s): Peter Loan
 */
package org.opensim.view.nodes;

import java.awt.Color;
import java.awt.Image;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.opensim.modeling.Force;
import org.opensim.modeling.GeometryPath;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.Vec3;
import org.opensim.view.ColorableInterface;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman Habib
 */
public class OneForceNode extends DisablablModelComponentNode implements ColorableInterface {

    private static ResourceBundle bundle = NbBundle.getBundle(OneForceNode.class);
    private boolean hasPath = false;
    private GeometryPath pathObject = null;
    /**
     * Creates a new instance of OneForceNode
     */
    public OneForceNode(OpenSimObject force) {
        super(force);
        Force typedForce = Force.safeDownCast(force);
        hasPath = typedForce.hasVisualPath();
        if (hasPath){
            pathObject =  GeometryPath.safeDownCast(ViewDB.obtainPathPropertyAsObject(force));
         }

        setChildren(Children.LEAF);
        addDisplayOption(displayOption.Showable);
        addDisplayOption(displayOption.Isolatable);
        addDisplayOption(displayOption.Colorable);
//        if (f.getDisplayer()!=null){
//            addDisplayOption(displayOption.Showable);
//            if (!f.hasGeometryPath())
//                addDisplayOption(displayOption.Colorable);
//        }
        //addDisplayOption(displayOption.Isolatable);
    }
    @Override
    public Image getIcon(int i) {
        URL imageURL;
        if (!enabled)
            return super.getIcon(i);
        
        imageURL = this.getClass().getResource("icons/forceNode.png");
        if (imageURL != null) { 
            return new ImageIcon(imageURL, "Force").getImage();
        } else {
            return null;
        }
    }


    @Override
    public void setEnabled(boolean enabled) {
        //OpenSimDB.getInstance().disableForce(getOpenSimObject(), enabled);
        super.setEnabled(enabled);
        if (enabled)
            setIconBaseWithExtension("/org/opensim/view/nodes/icons/muscleNode.png");
        //refreshNode();

    }

    public Action[] getActions(boolean b) {
        Action[] superActions = (Action[]) super.getActions(b);        
        // Arrays are fixed size, onvert to a List
        List<Action> actions = java.util.Arrays.asList(superActions);
        // Create new Array of proper size
        Action[] retActions = new Action[actions.size()+1];
        actions.toArray(retActions);
        /* No need to take out Display Options
        if (enabled){  // take out display menu ObjectDisplayMenuAction
            for (int i=0; i< retActions.length; i++){
                if (retActions[i] instanceof ObjectDisplayMenuAction){
                    retActions[i] = null; 
                    break;
                }
            }
        } */
        try {
            ToggleEnabledStateAction act =(ToggleEnabledStateAction) ToggleEnabledStateAction.findObject(
                    (Class)Class.forName("org.opensim.view.nodes.ToggleEnabledStateAction"), true);
            retActions[actions.size()]=act;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return retActions;
    }

    @Override
    public String getDisablePropertyName() {
        return ("appliesForce");
    }   

    @Override
    public Boolean getVisible() {
        if (hasPath)
            return pathObject.get_Appearance().get_visible();
        return false; // Need example Force e.g. Bushing that has Geometry other than GeometryPath
    }

    @Override
    public void setVisible(Boolean newValue) {
        if (hasPath){
            AppearanceHelper helper = new AppearanceHelper(getModelForNode(), this, pathObject.get_Appearance());
            helper.setAppearanceVisilibityProperty(newValue);
            pathObject.getPropertyByName("Appearance").setValueIsDefault(false);
        }
    }

    @Override
    public Color getColor() {
        if (hasPath){
            Vec3 c3 = pathObject.get_Appearance().get_color();
            return new Color((float) c3.get(0), (float) c3.get(1), (float) c3.get(2));
        }
        else
            return new Color(1, 1, 1);
    }

    @Override
    public void setColor(Color newColor) {
        if (hasPath){
            AppearanceHelper helper = new AppearanceHelper(getModelForNode(), this, pathObject.get_Appearance());
            helper.setAppearanceColorProperty(newColor);
            pathObject.getPropertyByName("Appearance").setValueIsDefault(false);
        }

    }

    @Override
    public int getDisplayPreference() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDisplayPreference(int newPref) {
         if (hasPath){
            AppearanceHelper helper = new AppearanceHelper(getModelForNode(), this, pathObject.get_Appearance());
            helper.setAppearanceDisplayPrefProperty(newPref);
            pathObject.getPropertyByName("Appearance").setValueIsDefault(false);
        }
    }

    @Override
    public double getOpacity() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setOpacity(double opacity) {
          if (hasPath){
            AppearanceHelper helper = new AppearanceHelper(getModelForNode(), this, pathObject.get_Appearance());
            helper.setAppearanceOpacityProperty(opacity);
            pathObject.getPropertyByName("Appearance").setValueIsDefault(false);
        }
   }
}
