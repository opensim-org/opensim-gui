/* -------------------------------------------------------------------------- *
 * OpenSim: EditorRegistry.java                                               *
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
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensim.view.nodes;

import java.beans.PropertyEditorSupport;
import java.util.HashMap;

/**
 *
 * @author Ayman
 */
public final class EditorRegistry {
    static HashMap<String, PropertyEditorSupport> registry = new HashMap<String, PropertyEditorSupport>();
    
    static public PropertyEditorSupport getEditor(String clazzName) {
        //System.out.println("get editor for type:"+clazzName);
        return registry.get(clazzName);
    }
    
    static public void addEditor(String clazzName, PropertyEditorSupport pes) {
        registry.put(clazzName, pes);
    }
    
}
