/* -------------------------------------------------------------------------- *
 * OpenSim: DofFunctionNode.java                                              *
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
 * DofFunctionNode
 * Author(s): Peter Loan
 */
package org.opensim.view.nodes;

import java.awt.Image;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.openide.nodes.Children;
import org.openide.util.NbBundle;
import org.opensim.modeling.TransformAxis;
import org.opensim.view.editors.DofFunctionEditorAction;
import org.opensim.modeling.OpenSimObject;

/**
 *
 * @author Peter Loan
 */
public class DofFunctionNode extends OpenSimObjectNode{
   
   private static ResourceBundle bundle = NbBundle.getBundle(DofFunctionNode.class);
   private String displayName = null;
   private TransformAxis dof = null;
   
   /**
    * Creates a new instance of DofFunctionNode
    */
   public DofFunctionNode(OpenSimObject func, String name, TransformAxis dof) {
      super(func);
      this.displayName = name;
      this.dof = dof;
      setShortDescription(bundle.getString("HINT_FunctionNode"));
      setChildren(Children.LEAF);
   }
   public Image getIcon(int i) {
      URL imageURL = this.getClass().getResource("icons/functionNode.png");
      if (imageURL != null) {
         return new ImageIcon(imageURL, "Function").getImage();
      } else {
         return null;
      }
   }
   public Image getOpenedIcon(int i) {
      return getIcon(i);
   }
   
   /**
    * Display name
    */
   public String getHtmlDisplayName() {
      return displayName;
   }
   
}
