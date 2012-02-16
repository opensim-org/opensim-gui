
package org.opensim.view.pub;

import java.io.IOException;
import org.openide.ErrorManager;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.Coordinate;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.motions.MotionsDB;

/**
 *
 * @author ayman
 * Copyright (c)  2005-2012, Stanford University, Ayman Habib
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

/**
 *  giu is an implementation of the Facade design pattern to 
 * 1. Shield the user from the various classes used by the GUI
 * 2. Provide convenience methods that update the GUI if needed
 * 
 * @author ayman
 */
public final class gui {
    /**
     * get a reference to the model that is current in the OpenSim application.
     * 
     * @return current model
     */
    static public Model getCurrentModel(){
        return OpenSimDB.getInstance().getCurrentModel();
    }
    /**
     * setCurrentModel, makes a model that is loaded in the OpenSim application current.
     * 
     * @param aModel to be made current
     */
    static public void setCurrentModel(Model aModel){
        OpenSimDB.getInstance().setCurrentModel(aModel);
    }
    /**
     * get a reference to the state of the passed in model. Every model is backed by an instance of the 
     * class OpenSimContext that serves as a container of the current state of the model.
     * 
     * @param aModel
     * @return reference to the OpenSimContext corrsponding to passed in model
     */
    static public OpenSimContext getModelState(Model aModel){
        return OpenSimDB.getInstance().getContext(aModel);
    }
    
    /**
     * getCooridnate 
     * @param aModel
     * @param name
     * @return a reference to the coordinate with passed in name in the model "aModel"
     */
    static public Coordinate getCoordinate(Model aModel, String name){
        return aModel.getCoordinateSet().get(name);
    }
    /**
     * setCoordinateValue allows the user to set the value of the passed in Coordinate to the specified newValue
     *  This call, also updates the Graphics window if needed.
     * 
     * @param coordinate
     * @param newValue 
     */
    static public void setCoordinateValue(Coordinate coordinate, double newValue){
        getModelState(coordinate.getModel()).setValue(coordinate, newValue);
        ViewDB.getInstance().updateModelDisplay(coordinate.getModel());
    }
    /**
     * addModel creates a new OpenSim model from the passed in fileName and loads this model 
     * into the OpenSim application. This is equivalent to "File->Open Model..."
     * 
     * @param fileName to construct the model from
     */
    static public void addModel(String fileName){
        try {
            Model aModel = new Model(fileName);
            OpenSimDB.getInstance().addModel(aModel);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    /**
     * loadMotion constructs a motion out of the passed in fileName and loads it into the application, 
     * associating it with the current model if possible. Equivalent to "File->Load Motion..."
     * 
     * @param fileName to construct the motion from
     */
    static public void loadMotion(String fileName){
        MotionsDB.getInstance().loadMotionFile(fileName, true);
    }
    /**
     * Generic method to invoke commands available from the menu bar. The actionName passed in is usually
     * the concatentaion of the words making the menu items:
     * e.g. performAction("FileOpen") is equivalent to picking the cascade menu File->Open Model
     * 
     * @param actionName
     */
    static public void performAction(String actionName) {
        FileObject myActionsFolder = FileUtil.getConfigFile("Actions/Edit");
        FileObject[] myActionsFolderKids = myActionsFolder.getChildren();
        for (FileObject fileObject : myActionsFolderKids) {
            //Probably want to make this more robust,
            //but the point is that here we find a particular Action:
            if (fileObject.getName().contains(actionName)) {
                try {
                    DataObject dob = DataObject.find(fileObject);
                    InstanceCookie ic = dob.getLookup().lookup(InstanceCookie.class);
                    if (ic != null) {
                        Object instance = ic.instanceCreate();
                        if (instance instanceof CallableSystemAction) {
                            CallableSystemAction a = (CallableSystemAction) instance;
                            a.performAction();
                        }
                    }
                } catch (Exception e) {
                    ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
                 }
            }
        }

    }
    /**
     * findObject locates an Object in the model and returns a reference to it.
     * Beware, since this gives unguarded access to the objects in the model, changes made the object
     * maynot propagate throughout the model and or application. USe at your own risk.
     * 
     * @param aModel
     * @param type : one of Body, Joint, Force (or any force producing object e.g. muscle), Controller
     * @param name : name of the object of the specified type 
     * @return
     * @throws IOException if the object couldn't be located
     */
    static public OpenSimObject findObject(Model aModel, String type, String name) throws IOException
    {
        return aModel.getObjectByTypeAndName(type, name);
    }
    /**
     * sleectObject marks the passed in object as selected in the application if it has a visual representation
     * 
     * @param obj 
     */
    static public void selectObject(OpenSimObject obj){
        ViewDB.getInstance().setSelectedObject(obj);
    }
}
