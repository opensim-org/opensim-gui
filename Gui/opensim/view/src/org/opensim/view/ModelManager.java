/* -------------------------------------------------------------------------- *
 * OpenSim: ModelManager.java                                                 *
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
 * ModelManager.java
 *
 * Created on May 5, 2006, 1:17 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view;


import java.util.ArrayList;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.opensim.modeling.Model;

/**
 *
 * @author ken
 */
public final class ModelManager implements ExplorerManager.Provider {
    
    private static ModelManager _instance = null;
    
    private final ExplorerManager _manager = new ExplorerManager();
    
    private ArrayList<Model> _models = new ArrayList<Model>();
    
    /** Get the single instance of the ModelManger */
    public static synchronized ModelManager getDefault() {
        if (_instance == null)
            _instance = new ModelManager();
        return _instance;
    }
    
    /** Creates a new instance of ModelManager */
    public ModelManager() {
    }
    
    public ArrayList<Model> getModels() { // TODO: make this const
        return _models;
    }
    
    public void addModel(Model m) {
        _models.add(m);
    }

    public ExplorerManager getExplorerManager() {
        return _manager;
    }
    
} // class ModelManager
