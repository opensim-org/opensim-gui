/* -------------------------------------------------------------------------- *
 * OpenSim: JMPTaskListModel.java                                               *
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
 * ForceListModel.java
 *
 * Created on January 28, 2010, 3:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.rcnl;

import java.util.Vector;
import javax.swing.DefaultListModel;
import org.opensim.modeling.ExternalLoads;
import org.opensim.modeling.Force;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PropertyObjectList;

/**
 *
 * @author ayman
 */
public class JMPJointListModel extends DefaultListModel {
    PropertyObjectList  jMPJointList;
    /** Creates a new instance of ForceListModel */
    public JMPJointListModel(PropertyObjectList jointList) {
        super();
        jMPJointList = jointList;
        initModel();
    }

    private void initModel() {
        clear();
        
        for (int i=0; i<jMPJointList.size(); i++){
            add(i, this.jMPJointList.getValue(i));
        }
    }
    
}
