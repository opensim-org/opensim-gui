/* -------------------------------------------------------------------------- *
 * OpenSim: ContactGeometriesNode.java                                        *
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
 * ContactGeometriesNode
 * Author(s): Ayman Habib
 */
package org.opensim.view.nodes;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import java.util.ResourceBundle;
import javax.swing.Action;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.modeling.ContactGeometry;
import org.opensim.modeling.ContactGeometrySet;
import org.opensim.modeling.OpenSimObjectSet;

/**
 * 
 * 
 * @author Ayman Habib 
 * 
 * Top level ContactGeometriesNode node in Navigator view
 */
public class ContactGeometriesNode extends OpenSimObjectSetNode {
   
   private static ResourceBundle bundle = NbBundle.getBundle(ContactGeometriesNode.class);
   /**
     * Creates a new instance of ContactGeometriesNode
     */
   public ContactGeometriesNode(OpenSimObjectSet as) {
       super(as);
       setDisplayName(NbBundle.getMessage(ContactGeometriesNode.class, "CTL_ContactGeometries"));
       Children children = getChildren();
       for (int i=0; i<as.getSize(); i++) {
           //System.out.println(as.get(i).getType());
           ContactGeometry cg = ContactGeometry.safeDownCast(as.get(i));
           OneContactGeometryNode node = new OneContactGeometryNode(cg);
           Node[] arrNodes = new Node[1];
           arrNodes[0] = node;
           children.add(arrNodes);
       }
      if (getChildren().getNodesCount()==0) setChildren(Children.LEAF);
      addDisplayOption(displayOption.Isolatable);
      addDisplayOption(displayOption.Showable);
      addDisplayOption(displayOption.Colorable);       
   }
   /**
    * Display name
    */
   public String getHtmlDisplayName() {
      
      return NbBundle.getMessage(ContactGeometriesNode.class, "CTL_ContactGeometries");
   }
   
      public Image getIcon(int i) {
      URL imageURL=null;
      try {
         imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/actuatorsNode.png");
      } catch (ClassNotFoundException ex) {
         ex.printStackTrace();
      }
      if (imageURL != null) {
         return new ImageIcon(imageURL, "").getImage();
      } else {
         return null;
      }
   }
   
   public Image getOpenedIcon(int i) {
      URL imageURL=null;
      try {
         imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/actuatorsNode.png");
      } catch (ClassNotFoundException ex) {
         ex.printStackTrace();
      }
      if (imageURL != null) {
         return new ImageIcon(imageURL, "").getImage();
      } else {
         return null;
      }
   }
      
}
