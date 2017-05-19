/*
 *
 * OneForceNode
 * Author(s): Peter Loan
 * Copyright (c)  2009, Stanford University, Peter Loan
* Use of the OpenSim software in source form is permitted provided that the following
* conditions are met:
* 	1. The software is used only for non-commercial research and education. It may not
*     be used in relation to any commercial activity.
* 	2. The software is not distributed or redistributed.  Software distribution is allowed
*     only through https://simtk.org/home/opensim.
* 	3. Use of the OpenSim software or derivatives must be acknowledged in all publications,
*      presentations, or documents describing work in which OpenSim or derivatives are used.
* 	4. Credits to developers may not be removed from executables
*     created from modifications of the source.
* 	5. Modifications of source code must retain the above copyright notice, this list of
*     conditions and the following disclaimer.
*
*  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
*  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
*  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
*  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
*  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
*  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
*  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
*  OR BUSINESS INTERRUPTION) OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
*  WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.opensim.view.nodes;

import java.awt.Color;
import java.awt.Image;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.opensim.modeling.GeometryPath;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.Vec3;
import org.opensim.view.ColorableInterface;

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
        hasPath = force.hasProperty("GeometryPath");
        if (hasPath){
            pathObject =  GeometryPath.safeDownCast(force.getPropertyByName("GeometryPath").getValueAsObject());
         }


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
