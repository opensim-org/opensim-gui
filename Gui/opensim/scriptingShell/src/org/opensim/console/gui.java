
package org.opensim.console;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import org.openide.ErrorManager;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.customGui.InstructionsTopComponent;
import org.opensim.customGui.ParametersTopComponent;
import org.opensim.modeling.Body;
import org.opensim.modeling.Constraint;
import org.opensim.modeling.Coordinate;
import org.opensim.modeling.Force;
import org.opensim.modeling.Joint;
import org.opensim.modeling.Marker;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.Probe;
import org.opensim.modeling.State;
import org.opensim.modeling.Storage;
import org.opensim.tracking.tools.ToolbarSimulationAction;
import org.opensim.utils.TheApp;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.motions.MotionsDB;
import org.opensim.view.nodes.ConcreteModelNode;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

/**
 *  gui is an implementation of the Facade design pattern to 
 * 
 * 1. Shield the user from the various classes used by the GUI
 * 2. Provide convenience methods that update the GUI if needed
 * 
 * Methods in this class can be used without specifying "gui." for convenience.
 * 
 * @author Ayman
 */
public final class gui {
    /**
     * getCurrentModel() gets a reference to the model that is current in the OpenSim application.
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
     * dumpModelState() dumps the vector of continuous state variables that backs aModel
     * as a String (SimTK::State.getY())
     */
    static public String dumpModelState(Model aModel) {
        State stateRef = OpenSimDB.getInstance().getContext(aModel).getCurrentStateRef();
        return stateRef.getY().toString();
    }

    /**
     * getStateRef() returns a reference to the instance of SimTK::State that backs aModel
     */
    static public State getStateRef(Model aModel) {
        State stateRef = OpenSimDB.getInstance().getContext(aModel).getCurrentStateRef();
        return stateRef;
    }
    /**
     * getCurrentMotion() gets a reference to the motion that is current in the OpenSim application.
     * 
     * @return current motion as a Storage object. If none or multiple (sync.) it returns null
     */
    static public Storage getCurrentMotion(){
        if (MotionsDB.getInstance().getNumCurrentMotions()!=1) return null;
        return MotionsDB.getInstance().getCurrentMotion(0).motion;
    }
 
    /**
     * getCoordinate ()
     * 
     * @param aModel
     * @param name
     * @return a reference to the coordinate with passed in name in the model "aModel"
     */
    static public Coordinate getCoordinate(Model aModel, String name){
        return aModel.getCoordinateSet().get(name);
    }
    /**
     * setCoordinateValue() allows the user to set the value of the passed in Coordinate to the specified newValue
     *  This call, also updates the Graphics window if needed.
     *  rotational coordinates should be specified in radians
     * 
     * @param coordinate
     * @param newValue 
     */
    static public void setCoordinateValue(final Coordinate coordinate, final double newValue){
        OpenSimContext context=OpenSimDB.getInstance().getContext(coordinate.getModel());
        coordinate.setValue(context.getCurrentStateRef(), newValue);
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                ViewDB.getInstance().updateModelDisplay(coordinate.getModel());
                MotionsDB.getInstance().reportTimeChange(0);
            }
        });
     }
    /**
     * setCoordinateValueDegrees allows the user to set the value of the passed in Coordinate to the specified newValue
     *  This call, also updates the Graphics window if needed.
     * 
     * @param coordinate
     * @param newValue specified in degrees (rather than radians)
     */
    static public void setCoordinateValueDegrees(final Coordinate coordinate, final double newValue){
        double valueInDegrees = newValue*Math.PI/180.;
        setCoordinateValue(coordinate, valueInDegrees);
     }
    /**
     * addModel() creates a new OpenSim model from the passed in fileName and loads this model 
     * into the OpenSim application. This is equivalent to "File->Open Model..."
     * 
     * @param fileName to construct the model from
     * 
     * @Deprecated use loadModel instead
     */
    static public void addModel(String fileName){
        loadModel(fileName);
    }
    /**
     * loadModel() creates a new OpenSim model from the passed in fileName and loads this model 
     * into the OpenSim application. This is equivalent to "File->Open Model..."
     * 
     * @param fileName to construct the model from
     * 
     * 
     */
    static public void loadModel(String fileName){
        try {
            Model aModel = new Model(fileName);
            OpenSimDB.getInstance().addModel(aModel);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    /**
     * loadModel() loads the passed in model into the OpenSim application. 
     * The application takes ownership of the passed in model
     * 
     * @param aModel is the model to be loaded. 
     * 
     * 
     */
    static public void loadModel(Model aModel){
        try {
            aModel.markAdopted();
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
                    break;
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
     * @return Object in the model with specified type and name 
     * @throws IOException if the object couldn't be located
     */
    static public OpenSimObject findObject(Model aModel, String type, String name) throws IOException
    {
        OpenSimObject rawObject =  aModel.getObjectByTypeAndName(type, name);
        // Will try to upcast to type "type" if possible. Ideally there's a generic mechanism
        // for now we'll go with ugly if else since RTTI info is not carried over across SWIG
        if (type.equalsIgnoreCase("Body"))
            return Body.safeDownCast(rawObject);
        else if (type.equalsIgnoreCase("Force"))
            return Force.safeDownCast(rawObject);
        else if (type.equalsIgnoreCase("Constraint"))
            return Constraint.safeDownCast(rawObject);
        else if (type.equalsIgnoreCase("Coordinate"))
            return Coordinate.safeDownCast(rawObject);
        else if (type.equalsIgnoreCase("Marker"))
            return Marker.safeDownCast(rawObject);
        else if (type.equalsIgnoreCase("Joint"))
            return Joint.safeDownCast(rawObject);
        else if (type.equalsIgnoreCase("Probe"))
            return Probe.safeDownCast(rawObject);
        return rawObject;
    }
    /**
     * sleectObject marks the passed in object as selected in the application if it has a visual representation
     * 
     * @param obj 
     */
    static public void selectObject(final OpenSimObject obj){
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                ViewDB.getInstance().setSelectedObject(obj);
            }
        });    
    }
    /**
     * setObjectColor marks the passed in object if it has a visual representation with passed in color
     * 
     * @param obj 
     */
    static public void setObjectColor(final OpenSimObject obj,  final double[] colorComponents){
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                ViewDB.getInstance().setObjectColor(obj, colorComponents);
            }
        });
        
    }
    /**
     * Turn on/off the display of the passed in object according to the passed in flag
     * Valid only for objects can be shown/hidden from the navigator
     * 
     * @param obj
     * @param onOrOff 
     */
    static public void toggleObjectDisplay(final OpenSimObject obj,  final boolean onOrOff){
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                ViewDB.getInstance().toggleObjectsDisplay(obj, onOrOff);
            }
        });
    }
    /**
     * Set the opacity of an object. Valid only for objects that allow that operation, 
     * specifically those that have visual representation and athat have individual control over
     * their opacity
     * 
     * @param obj The object to set the opacity of
     * @param newOpacity0To1 
     */
    static public void setObjectOpacity(final OpenSimObject obj, final double newOpacity0To1) {
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                ViewDB.getInstance().setObjectOpacity(obj, newOpacity0To1);
            }});
    }
    /**
     * Perform operation in current graphics window equivalent to pressing c while the window has focus
     * @param c 
     */
    static public void gfxWindowSendKey(final char c){
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                ViewDB.getCurrentModelWindow().processKey(c);
            }});
    }
    /**
     * Get the full name of the directory used as a root for the Scripts.
     * @return string that represents the path to the Scripts directory
     */
    static public String getScriptsDir()
    {
        String relativePath= Preferences.userNodeForPackage(TheApp.class).get("Scripts Path", "Scripts");
        if (relativePath.equals(null)) return null;
        return new File(relativePath).getAbsolutePath();
    }
    /**
     * Get the full name of the directory used as a root for OpenSim installation.
     * @return string that represents the path to the installation directory
     */
    static public String getInstallDir()
    {
        return TheApp.getInstallDir();
    }
    
    /**
     * getClassName() returns the full qualified name of the Class that obj is an instance of
     * @param obj
     * @return class name as a string, fully qualified.
     */
    static public String getClassName(Object obj)
    {
        return obj.getClass().getName();
    }
    /**
     * methodsview() Shows in a standalone modal dialog the methods available for the passed in object
     * 
     * @param obj
     * 
     */
    static public void methodsview(Object obj)
    {
        methodsview(obj.getClass());
    }
    /**
     * Show in a standalone modal dialog the methods available for the passed in class
     * Class name is fully qualified with package name etc.
     * @param classObj 
     */
    static public void methodsview(Class classObj)
    {
        String methodsList = ListMethods.listMethodsForClass(classObj.getName());
        // Make a scroll pane and stick methodsList in it.
        JFrame methodsFrame = new JFrame();
        JScrollPane scrollPane = new JScrollPane();
        JTextArea textArea = new JTextArea(methodsList);
        scrollPane.setViewportView(textArea);
        methodsFrame.getContentPane().add(scrollPane);
        methodsFrame.setSize(400, 700);
        methodsFrame.setVisible(true);
        return ;
    }
    /**
     * getOpenModels()
     * 
     * @return list of models currently loaded in the application
     */
    static public Model[] getOpenModels() 
    {
        Object[] modelsAsObjects = OpenSimDB.getAllModels();
        Model[] models = new Model[modelsAsObjects.length];
        for(int i=0; i<modelsAsObjects.length; i++)
            models[i] = (Model) modelsAsObjects[i];
        return models;
    } 
     
    /**
     * updateDisplay to be in sync. with model edits done from scripting shell. 
     * As of now works on Navigator for Probes only, will need to update Coordinates and Gfx window
     * and support other object types as well
     */
    static public void updateDisplay() {
        ViewDB.getInstance().updateModelDisplay(getCurrentModel());
    }
    /**
     * createParametersWindow Creates and open a window for custom GUI, the window is a singleton type (1 per application)
     * and is normally docked. You can close() and open() it as needed. It should be treated
     * as a JComponent (in Java Swing terms)
     * 
     * @return handle to the opened window
     */
    static public ParametersTopComponent createParametersWindow()
    {
        ParametersTopComponent win = ParametersTopComponent.findInstance();
        win.open();
        return win;
    }
        
    /**
     * createInstructionsWindow Creates and open a window for Instructions, the window is a singleton type (1 per application)
     * and is normally docked. You can close() and open() it as needed. It should be treated
     * as a JComponent (in Java Swing terms)
     * The function takes in a full path name for an html document to display on opening.
     * 
     * @return handle to the opened window
     */
    static public InstructionsTopComponent createInstructionsWindow(String fileName)
    {
        InstructionsTopComponent win = InstructionsTopComponent.findInstance();
        win.setDocument(fileName);
        win.open();
        return win;
    }
    static public void setSimulationToolBarVisibility(boolean show) {
        try {
            ToolbarSimulationAction simAct = (ToolbarSimulationAction) ToolbarSimulationAction.findObject(
                        (Class)Class.forName("org.opensim.tracking.tools.ToolbarSimulationAction"), false);
            simAct.setToolbarVisiblity(show);
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
