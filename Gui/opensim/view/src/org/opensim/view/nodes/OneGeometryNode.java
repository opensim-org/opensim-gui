/* -------------------------------------------------------------------------- *
 * OpenSim: OneGeometryNode.java                                              *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib                                                     *
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
 * OneContactGeometryNode
 * Author(s): Ayman Habib
 */
package org.opensim.view.nodes;

import java.awt.Image;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import static org.openide.nodes.Sheet.createExpertSet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.opensim.modeling.AnalyticGeometry;
import org.opensim.modeling.Component;
import org.opensim.modeling.Cone;
import org.opensim.modeling.Frame;
import org.opensim.modeling.Geometry;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.PhysicalOffsetFrame;
import org.opensim.modeling.State;
import org.opensim.modeling.Transform;
import org.opensim.view.nodes.OpenSimObjectNode.displayOption;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman Habib
 */
public class OneGeometryNode extends OneComponentWithGeometryNode {
    
    private static ResourceBundle bundle = NbBundle.getBundle(OneGeometryNode.class);
    /**
    * Creates a new instance of OneContactForceNode
    */
    public OneGeometryNode(Geometry cg) {
        super(cg);
        String gName=cg.getName();
        if (cg.getName().equalsIgnoreCase("")){
            gName=cg.getName();
            setDisplayName(cg.getAbsolutePathString());
            setName(cg.getName());
        }
        setChildren(Children.LEAF);
        addDisplayOption(displayOption.Colorable);
        addDisplayOption(displayOption.Isolatable);
        addDisplayOption(displayOption.Showable);
    }
    public Image getIcon(int i) {
        URL imageURL = this.getClass().getResource("icons/displayGeometryNode.png");
        if (imageURL != null) {
            return new ImageIcon(imageURL, "Display Geometry").getImage();
        } else {
            return null;
        }
    }
   public Image getOpenedIcon(int i) {
        return getIcon(i);
    }

    public Action[] getActions(boolean b) {
        // Get actions from parent (generic object menu for review, display)
        Action[] superActions = (Action[]) super.getActions(b);
        return superActions;
    }

    public String getHtmlDisplayName() {
        String retValue;
        
        retValue = super.getHtmlDisplayName();
        if (retValue.equalsIgnoreCase("")){
            retValue = ((Geometry)getOpenSimObject()).getName();
        }
        return retValue;
    }
    public Sheet createSheet() {
        Sheet sheet;
        sheet = super.createSheet();
        Sheet.Set set = sheet.get(Sheet.PROPERTIES);
        // Remove Properties for origin, direction as not supported 
        if (Cone.safeDownCast(comp)!=null){
            set.remove("origin");
            set.remove("direction");
        }
        if (AnalyticGeometry.safeDownCast(comp)!=null){
            set.remove("quadrants");
        }
        set.remove("scale_factors");
        return sheet;
    }
    
    public String getFrameName() {
        Geometry g = Geometry.safeDownCast(comp);
        return g.getFrame().getName();
    }
    public void setFrameName(String frame) {
        Geometry g = Geometry.safeDownCast(comp);
        //Component frameComponent = m.getComponent(frame);
        //g.connectSocket_frame(frameComponent);
    }
}
