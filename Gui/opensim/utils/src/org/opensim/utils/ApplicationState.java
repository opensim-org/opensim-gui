/* -------------------------------------------------------------------------- *
 * OpenSim: ApplicationState.java                                             *
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
 * ApplicationState.java
 *
 * Created on May 1, 2008, 10:59 AM
 *
 */

package org.opensim.utils;

import java.io.Serializable;
import java.util.Hashtable;

/**
 *
 * @author Ayman. 
 * 
 * This class encapsulates all the state of the application. The state is maintained as 
 * a list of generic objects that get serialized/deserialized.
 * An instance of this class is created when a session of OpenSim is closed and is saved
 * to disk using the serialization mechanism. 
 * To make sure we don't run into problems if new objects are added to/removed from the list. 
 * Objects are placed into a hashmap that gets serialized.
 *  If we use Externalizable instead of Serializable we'll have to convert objects to use JavaBeans convention
 * for setters/getters.
 */
public class ApplicationState implements Serializable {
    
    static ApplicationState instance=null;
    
    private Hashtable<String, Object> stateObjects = new Hashtable<String, Object>(4);
    private static final long serialVersionUID = 1L;
    /** Creates a new instance of ApplicationState */
    public static ApplicationState getInstance() {
        if (instance == null) {
             instance = new ApplicationState();
             
        }
        return instance;
    }
    
    public ApplicationState() {
        instance = this;
    }
    
    public void addObject(String key, Object object){
        getStateObjects().put(key, object);
    }
    
    public Object getObject(String key){
        return getStateObjects().get(key);
    }

    public Hashtable<String, Object> getStateObjects() {
        return stateObjects;
    }

    public void setStateObjects(Hashtable<String, Object> stateObjects) {
        this.stateObjects = stateObjects;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
    
}
