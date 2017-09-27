/* -------------------------------------------------------------------------- *
 * OpenSim: ExperimentalOtherDataSetNode.java                                 *
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
 * ExperimentalOtherDataSetNode.java
 *
 * Created on March 10, 2009, 10:56 AM
 *
 *
 *
 */
package org.opensim.view.experimentaldata;

import java.awt.Image;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.swing.ImageIcon;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.view.nodes.*;

/**
 *
 * @author ayman
 */
public class ExperimentalOtherDataSetNode extends OpenSimNode {
    private static ResourceBundle bundle = NbBundle.getBundle(ExperimentalOtherDataSetNode.class);
    private static String nodeName;
    AnnotatedMotion dMotion;
    /** Creates a new instance of ExperimentalForceNode */
    public ExperimentalOtherDataSetNode(AnnotatedMotion dMotion) {
        nodeName=bundle.getString("OtherDataSet_NODE_NAME");
        setName(nodeName);
        setDisplayName(nodeName);
        setShortDescription(bundle.getString("HINT_ExperimentalOtherDataSetNode"));
        this.dMotion=dMotion;
        createChildren();
    }
    
    public String getHtmlDisplayName() {
        
        return nodeName;
    }
   /**
    * Icon for the node, same as OpenSimNode
    **/
   public Image getIcon(int i) {
      URL imageURL=null;
      try {
         imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/motionsNode.png");
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
         imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/motionsNode.png");
      } catch (ClassNotFoundException ex) {
         ex.printStackTrace();
      }
      if (imageURL != null) {
         return new ImageIcon(imageURL, "").getImage();
      } else {
         return null;
      }
   }

    private void createChildren() {
        Vector<ExperimentalDataObject> allMotionObjects = dMotion.getClassified();
        // Create an ExperimentalForceNode for each 
        if (allMotionObjects==null) return;
        for(ExperimentalDataObject dObject:allMotionObjects){
            if (dObject.getObjectType()!=ExperimentalDataItemType.PointForceData &&
                    dObject.getObjectType()!=ExperimentalDataItemType.MarkerData){
                getChildren().add(new Node[]{new ExperimentalOtherDataNode(dObject, dMotion)});
            }
        }
    }

}
