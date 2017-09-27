/* -------------------------------------------------------------------------- *
 * OpenSim: Lab.java                                                          *
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
 * Lab.java
 *
 * Created on May 1, 2008, 10:59 AM
 *
 */

package org.opensim.customGui;

import java.io.IOException;
import java.io.Serializable;
import java.util.Hashtable;
import org.openide.awt.StatusDisplayer;
import org.opensim.swingui.SwingWorker;
import org.opensim.tools.serializers.ToolExecutor;
import org.opensim.tools.serializers.ToolSerializer;
import org.opensim.view.motions.MotionsDB;
import org.opensim.view.motions.MotionsDBDescriptor;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.OpenSimDBDescriptor;
import org.opensim.view.pub.PluginsDB;
import org.opensim.view.pub.ViewDB;
import org.opensim.view.pub.ViewDBDescriptor;

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
public class Lab implements Serializable {
    
    private Hashtable<String, Object> stateObjects = new Hashtable<String, Object>(4);
    private static final long serialVersionUID = 1L;
    public final static String LAB_NAME="labName";
    public final static String DB="OpenSimDB";
    public final static String INSTRUCTIONS="instructions_file";
    public final static String MOTIONS="MotionsDB";
    public final static String COORDS="Coords";
    public final static String TOOL="Tool";
    public final static String RUN_LABEL="RunLabel";
    public final static String INPUTS="Parameters";
    public final static String OUTPUTS="Outputs";
    public final static String VERSION="Version";
    public final static String DEFAULT_VIEW="ViewDB";
    
    private ToolExecutor toolExecutor;
    
    class LabWorker extends SwingWorker {
        public Object construct() {
            execute();
            return this;
        }
        
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
    
    void execute() { // Start another thread and use ivokeAndWait to force refresh of GUI
        
        displayStatusText("Setting up lab...");
        
        PluginsDB.getInstance().loadPlugins();
        // Open vtk window early as trying to display prematurely crashes
        if (ViewDB.getInstance().getCurrentModelWindow()==null)
            ViewDB.getInstance().addViewWindow();
        OpenSimDB.getInstance().rebuild((OpenSimDBDescriptor) getObject(DB));
        String instructionsFileName =  (String)getObject(INSTRUCTIONS);
        if (instructionsFileName !=null){
            InstructionsTopComponent instructionsTC = InstructionsTopComponent.getDefault();
            instructionsTC.open();
            instructionsTC.setDocument(instructionsFileName);
        }
        //displayStatusText("Loading motion ...");
        if (getObject("ViewDB")!=null){
            ViewDB.getInstance().rebuild((ViewDBDescriptor) getObject("ViewDB"));
            ViewDB.getInstance().renderAll();
        }
        if (getObject(MOTIONS)!=null){
            MotionsDB.getInstance().rebuild((MotionsDBDescriptor) getObject(MOTIONS));
            ViewDB.getInstance().renderAll();
        }
        ParametersTopComponent parametersTC = ParametersTopComponent.findInstance();
        parametersTC.open();
        parametersTC.requestActive();
        if (getObject(LAB_NAME)!= null)
            parametersTC.setDisplayName((String)getObject(LAB_NAME));
        
        LabParametersNode params = (LabParametersNode)getObject(INPUTS);
        if (params !=null){
            parametersTC.setInputs(params);
            if (getObject("ViewDB")!=null){
                ViewDBDescriptor vdb = (ViewDBDescriptor) getObject("ViewDB");
                parametersTC.setDefaultView(vdb.getCameraAttributes().get(0));
            }
        }
        //displayStatusText("Loading tool ...");
        ToolSerializer tool = (ToolSerializer) getObject(TOOL);
        if (tool !=null) {
           if (getObject(RUN_LABEL)!=null)  // Use provided name for run button, otherwise default to "Run >"
                parametersTC.setRunLabel((String)getObject(RUN_LABEL));
           try {
                toolExecutor = parametersTC.createExecutor(tool, OpenSimDB.getInstance().getCurrentModel());
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        LabOutputsNode outs = (LabOutputsNode)getObject(OUTPUTS);
        if (outs !=null){
            parametersTC.seOutnputs(outs);
        }
        parametersTC.createRunButton();
        parametersTC.createResetViewButton();
        ViewDB.getInstance().renderAll();
    }

    private void displayStatusText(final String text) {
         StatusDisplayer.getDefault().setStatusText(text);
         
    }
    
    public void executeInBackground() {
        LabWorker labWorker = new LabWorker();
        labWorker.start();
}

}

