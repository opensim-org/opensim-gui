/*
 * Lab.java
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

