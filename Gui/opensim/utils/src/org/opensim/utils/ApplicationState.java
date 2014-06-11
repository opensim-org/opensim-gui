/*
 * ApplicationState.java
 *
 * Created on May 1, 2008, 10:59 AM
 *
 * Copyright (c)  2005-2008, Stanford University, Ayman Habib
 * Use of the OpenSim software in source form is permitted provided that the following
 * conditions are met:
 * 	1. The software is used only for non-commercial research and education. It may not
 *     be used in relation to any commercial activity.
 * 	2. The software is not distributed or redistributed.  Software distribution is allowed
 *     only through https://simtk.org/home/opensim.
 * 	3. Use of the OpenSim software or derivatives must be acknowledged in all publications,
 *      presentations, or documents describing work in which OpenSim or derivatives are used.
 * 	4. Credits to developers may not be removed from executables
 *     created from modifications of the source.
 * 	5. Modifications of source code must retain the above copyright notice, this list of
 *     conditions and the following disclaimer.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR BUSINESS INTERRUPTION) OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 *  WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
