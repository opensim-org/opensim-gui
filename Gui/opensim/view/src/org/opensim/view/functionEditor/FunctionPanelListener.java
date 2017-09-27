/* -------------------------------------------------------------------------- *
 * OpenSim: FunctionPanelListener.java                                        *
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
 * FunctionPanelListener.java
 *
 * Created on November 16, 2007, 3:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view.functionEditor;

import java.util.EventListener;
import org.opensim.modeling.ArrayInt;

/**
 * The interface that must be supported by classes that wish to receive 
 * notification of function change events.
 *
 */
public interface FunctionPanelListener extends EventListener {

   /**
    * Receives notification of a modification to a function.
    *
    * @param event  the event.
    */
   public void clearSelectedNodes();
   public void toggleSelectedNode(int series, int node);
   public void replaceSelectedNode(int series, int node);
   public void addNode(int series, double x, double y);
   public boolean deleteNode(int series, int node);
   public boolean deleteNodes(int series, ArrayInt nodes);
   public void duplicateNode(int series, int node);
   public void dragSelectedNodes(int series, int node, double dragVector[]);

}
