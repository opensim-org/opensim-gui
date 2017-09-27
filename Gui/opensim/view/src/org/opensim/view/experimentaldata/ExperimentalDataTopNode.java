/* -------------------------------------------------------------------------- *
 * OpenSim: ExperimentalDataTopNode.java                                      *
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
 * ExperimentalDataTopNode
 * Author(s): Ayman Habib
 */
package org.opensim.view.experimentaldata;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.modeling.Model;
import org.opensim.view.motions.MotionEvent;
import org.opensim.view.motions.MotionsDB;
import org.opensim.view.nodes.*;

/**
 *
 * @author Ayman Habib
 *
 * Top level external(=experimental) data node in Navigator view. 
 * This node implements Observer so that it can make the motion underneath it uncurrent
 */
public class ExperimentalDataTopNode extends OneModelNode implements Observer {
   
   private static ResourceBundle bundle = NbBundle.getBundle(ExperimentalDataTopNode.class);
   
   /** Creates a new instance of MotionsNode */
   public ExperimentalDataTopNode(Model m) {
       super(m);
       String nodeName=bundle.getString("EXPERIMENTALDATA_NODE_ID");
       setDisplayName(nodeName);
       setName(nodeName); // To be used by findNode();
       MotionsDB.getInstance().addObserver(this);
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

    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static void setBundle(ResourceBundle aBundle) {
        bundle = aBundle;
    }

   public void update(Observable o, Object arg) {
      if (o instanceof MotionsDB && arg instanceof MotionEvent) {
         // No matter what set the names of the naodes so that the explorer view is updated 
         // with what's current in bold'
         Node[] nodes = this.getChildren().getNodes();
         for(int i=0; i< nodes.length; i++){
            nodes[i].setName(nodes[i].getName());
         }
      }
   }

    public void destroy() throws IOException {
        MotionsDB.getInstance().deleteObserver(this);
        super.destroy();
    }

}
