/* -------------------------------------------------------------------------- *
 * OpenSim: OneDofNode.java                                                   *
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
 * OneDofNode
 * Author(s): Peter Loan
 */
package org.opensim.view.nodes;

import java.awt.Image;
import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.modeling.TransformAxis;
import org.opensim.modeling.Constant;
import org.opensim.modeling.Function;
import org.opensim.modeling.LinearFunction;
import org.opensim.modeling.OpenSimObject;

/**
 *
 * @author Peter Loan
 */
public class OneDofNode extends OpenSimObjectNode{
    
    private static ResourceBundle bundle = NbBundle.getBundle(OneDofNode.class);
    /** Creates a new instance of OneDofNode */
    public OneDofNode(OpenSimObject dof) {
        super(dof);
        Children children = getChildren();
        TransformAxis ad = TransformAxis.safeDownCast(dof);
        if (ad != null && ad.hasFunction()) {
           Function func = ad.getFunction();
           Constant cons = Constant.safeDownCast((OpenSimObject)func);
           LinearFunction lf = LinearFunction.safeDownCast((OpenSimObject)func);
           if (cons != null || lf != null) {
              setChildren(Children.LEAF);
           } else if (func != null ) {
              //TODO: for now, deal only with the first coordinate.
               
               if (ad.getCoordinateNamesInArray().getSize()>0){
                    String displayName = "f(" + ad.getCoordinateNamesInArray().getitem(0) + ")";
                    children.add(new Node[] { new DofFunctionNode(func, displayName, ad) });
               }
           }
        }
    }
    public Image getIcon(int i) {
        URL imageURL = this.getClass().getResource("icons/dofNode.png");
        if (imageURL != null) {
            return new ImageIcon(imageURL, "Dof").getImage();
        } else {
            return null;
        }
    }

}
