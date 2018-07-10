/* -------------------------------------------------------------------------- *
 * OpenSim: OpenSimDB.java                                                    *
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
 * OpenSimDB.java
 *
 * Created on June 15, 2006, 2:58 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view.pub;

import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Vector;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.opensim.modeling.Constraint;
import org.opensim.modeling.ForceSet;
import org.opensim.modeling.CoordinateSet;
import org.opensim.modeling.Force;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.utils.ErrorDialog;
import org.opensim.utils.TheApp;
import org.opensim.view.*;
import vtk.vtkMatrix4x4;

/**
 *
 * @author Ayman
 */
public class OpenSimDB extends Observable implements Externalizable{
    
    static OpenSimDB instance;
    public enum Mode {Modeling, Analysis}; // Will keep track if we're modeling or simulating, notify listener when changing
    static Mode mode = OpenSimDB.Mode.Modeling;
    static ArrayList<Model>  models = new ArrayList<Model>();
    static private Hashtable<Model, OpenSimContext> mapModelsToContexts =
           new Hashtable<Model, OpenSimContext>();
    private Hashtable<Model, SingleModelGuiElements> mapModelsToGuiElements =
           new Hashtable<Model, SingleModelGuiElements>();
    static Model currentModel=null;

    /**
     * @return the currentCloseModelDefaultAction
     */
    static public CloseModelDefaultAction getCurrentCloseModelDefaultAction() {
        String defaultCloseActionString="Prompt";
        String saved = Preferences.userNodeForPackage(TheApp.class).get("DefaultCloseAction", defaultCloseActionString);
        // Parse saved to an int, use 0 (no debug) on failure
        if (saved.equalsIgnoreCase("discard")) currentCloseModelDefaultAction=CloseModelDefaultAction.DISCARD;
        else if (saved.equalsIgnoreCase("save")) currentCloseModelDefaultAction=CloseModelDefaultAction.SAVE;
        else currentCloseModelDefaultAction=CloseModelDefaultAction.PROMPT;
        
        return currentCloseModelDefaultAction;
    }

    /**
     * @param currentCloseModelDefaultAction the currentCloseModelDefaultAction to set
     */
    static public void setCurrentCloseModelDefaultAction(CloseModelDefaultAction newCloseModelDefaultAction) {
        currentCloseModelDefaultAction = newCloseModelDefaultAction;
        String closeAction = "prompt";
        if (currentCloseModelDefaultAction ==CloseModelDefaultAction.DISCARD)
            closeAction="discard";
        else if (currentCloseModelDefaultAction ==CloseModelDefaultAction.SAVE)
            closeAction="save";
        Preferences.userNodeForPackage(TheApp.class).put("DefaultCloseAction", closeAction);
    }

    public Model getModelByIndex(int i) {
        return models.get(i);
    }

    public boolean hasModels() {
        return getInstance().getCurrentModel()!= null;
    }
    public enum CloseModelDefaultAction {
        SAVE,
        DISCARD,
        PROMPT
    }
    static private CloseModelDefaultAction currentCloseModelDefaultAction = CloseModelDefaultAction.PROMPT;
    ///static UndoManager undoMgr=new UndoManager();
    
    /** Creates a new instance of OpenSimDB */
    private OpenSimDB() {
        instance = this;
    }
    
    // The setChanged() protected method must overridden to make it public
    public synchronized void setChanged() {
        super.setChanged();
    }
    
    public static synchronized OpenSimDB getInstance() {
        if (instance == null) {
             instance = new OpenSimDB();
             
        }
        return instance;
    }
    /*
     * addModel adds a model to the OpenSimDB. 
     * Would throw an exception if initSystem throws
     */
    public void addModel(Model aModel) throws IOException {
        addModel(aModel, null);
    }
    
    public void addModel(Model aModel, OpenSimContext context) throws IOException {
        //setupGroups(aModel); // initSystem could throw exception
        OpenSimContext dContext = context==null?new OpenSimContext(aModel.initSystem(), aModel):context;
        models.add(aModel);
        mapModelsToContexts.put(aModel, dContext);
        SingleModelGuiElements newModelGuiElements = new SingleModelGuiElements(aModel);
        mapModelsToGuiElements.put(aModel, newModelGuiElements);
        setChanged();
        ModelEvent evnt = new ModelEvent(aModel, ModelEvent.Operation.Open);
        notifyObservers(evnt); 
        setCurrentModel(aModel, false);
        //ExplorerTopComponent.addFinalEdit();
    }
    
    public void setContext(Model aModel, OpenSimContext context) {
        mapModelsToContexts.put(aModel, context);
    }

    public static Model getModel(String modelName)
    {
        for(int i=0; i<models.size(); i++){
            if (models.get(i).getName().equals(modelName))
                return models.get(i);
        }
        return null;
    }
    
    public static Object[] getAllModels()
    {
        return (Object[]) models.toArray();
    }
    /**
     * Number of models currently loaded.
     */
    public int getNumModels() {
        return models.size();
    }

    public void removeModel(Model model)
    {
        models.remove(model);
        //model.cleanup();
        if (models.size()>0){
             if (model==currentModel)
                setCurrentModel(models.get(0));
        }
        else
           currentModel=null;
        
        setChanged();
        ModelEvent evnt = new ModelEvent(model, ModelEvent.Operation.Close);
        notifyObservers(evnt);
        
        model.cleanup();    // Cleanup after removal 
        mapModelsToContexts.remove(model);
        mapModelsToGuiElements.remove(model);
        System.gc();
        ExplorerTopComponent.addFinalEdit();
    }

   // removes old model and adds new model, but also transfers over some display properties
   // currently used by scale tool, which can't re-scale in place so it creates a new model to replace the old one
    // Swap context objects as well
   
   public void replaceModel(Model oldModel, Model newModel, OpenSimContext newContext) {
      vtkMatrix4x4 offset=null;
      double opacity=1;
      OpenSimContext swap=null;
      if(oldModel!=null) {
         removeModel(oldModel);
      }
      if(newModel!=null) {
            try {
                addModel(newModel, newContext);
            } catch (IOException ex) {
                ErrorDialog.displayExceptionDialog(ex);
            }
         //mapModelsToContexts.put(newModel, newContext);
         /*
         SingleModelVisuals rep = ViewDB.getInstance().getModelVisuals(newModel);
         if(offset!=null) {
            ViewDB.getInstance().setModelVisualsTransform(rep, offset);
            ViewDB.getInstance().setObjectOpacity(newModel, opacity);
         }*/
      }
   }
   
   public void saveModel(Model model, String fileName) {
       model.print(fileName);
       model.setInputFileName(fileName); // update the source filename of the model
       SingleModelGuiElements guiElem = getInstance().getModelGuiElements(model);
       if(guiElem!=null) guiElem.setUnsavedChangesFlag(false);
       setChanged();
       ModelEvent evnt = new ModelEvent(model, ModelEvent.Operation.Save);
       notifyObservers(evnt);
       ExplorerTopComponent.addFinalEdit();
   }

    public void setCurrentModel(final Model aCurrentModel) {
        setCurrentModel(aCurrentModel, true);
    }

    /**
     * Set the current model to the new one and fire an event for the change.
     */
    public void setCurrentModel(final Model aCurrentModel, boolean allowUndo) {
        final Model saveCurrentModel = currentModel;
        currentModel = aCurrentModel;
        Vector<OpenSimObject> objs = new Vector<OpenSimObject>(1);
        objs.add(aCurrentModel);
        ObjectSetCurrentEvent evnt = new ObjectSetCurrentEvent(this, aCurrentModel, objs);
        setChanged();
        //ModelEvent evnt = new ModelEvent(aCurrentModel, ModelEvent.Operation.SetCurrent);
        notifyObservers(evnt);
        objs.clear();
        
        if (allowUndo){
            ExplorerTopComponent.addUndoableEdit(new AbstractUndoableEdit() {
                public void undo() throws CannotUndoException {
                    super.undo();
                    setCurrentModel(saveCurrentModel, false);
                }

                public void redo() throws CannotRedoException {
                    super.redo();
                    setCurrentModel(aCurrentModel, true);
                }

                public boolean canUndo() {
                    return true;
                }

                public boolean canRedo() {
                    return true;
                }

                @Override
                public String getRedoPresentationName() {
                    return "Redo Change Current Model";
                }

                @Override
                public String getUndoPresentationName() {
                    return "Undo Change Current Model";
                }
           });
    }
    }
    
    /**
     * Get current model (as indicated by bold name in the explorer view)
     * if none then the function returns null
     *
     * It's an error condition if this function returns null while the explorer view is nonempty
     **/
   public Model getCurrentModel() {
      return currentModel;
   }
   /**
    * hasModel checks if the passed in model is already loaded.
    **/
   public boolean hasModel(Model aModel) {
     for(int i=0; i<models.size(); i++){
         if (models.get(i).equals(aModel))
             return true;
     }
      return false;
   }

   public static Model selectModel(Model currentModel) {
      return currentModel;
   }

   private void setupGroups(Model aModel) {
      // Create default groups
      ForceSet acts = aModel.getForceSet();
      // Until we decide how best to handle the "all" group,
      // don't add one here.
      //int numGroups = acts.getNumGroups();
      //if (acts.getGroup("all")==null){
         //acts.addGroup("all");
         //for(int i=0; i<acts.getSize(); i++){
            //acts.addObjectToGroup("all", acts.get(i).getName());
         //}
      //}
      if (aModel.getSimbodyEngine()==null || acts==null) return;
      CoordinateSet coords = aModel.getCoordinateSet();
      //numGroups = coords.getNumGroups();
      if (coords.getGroup("all")==null){
         coords.addGroup("all");
         for(int i=0; i<coords.getSize(); i++){
            coords.addObjectToGroup("all", coords.get(i).getName());
         }
      }
   }
   
   /**
    * Common place to validate new object names to make sure
    * They start with a an alphanumeric
    * contain any combinaton of letters, numbers, _, ., - but no special chars.
    */
   static public boolean validateName(String proposedName, boolean allowSpace)
   {
      Pattern p;
      if (allowSpace)
          p = Pattern.compile("[A-Z][A-Z0-9_. -]*", Pattern.CASE_INSENSITIVE);
      else
           p = Pattern.compile("[A-Z][A-Z0-9_.-]*", Pattern.CASE_INSENSITIVE);
         
        return (p.matcher(proposedName).matches());

   }

   /**
    * This method loads actual models specified in the passed in OpenSimDBDescriptor
    */
    public void rebuild(OpenSimDBDescriptor descriptor) {
        ArrayList<String> files = descriptor.getFileNames();
        Model saveCurrentModel=null;
        for(int i=0; i<files.size(); i++){
            String nextFilename = files.get(i);                
            File nextFile = new File(nextFilename);
            if (nextFile.exists()){
               String absolutePath = nextFile.getAbsolutePath();
               try {
                    // Display original model
                    ((FileOpenOsimModelAction) FileOpenOsimModelAction.findObject(
                            (Class)Class.forName("org.opensim.view.FileOpenOsimModelAction"), true)).loadModel(absolutePath, true);
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    DialogDisplayer.getDefault().notify(
                            new NotifyDescriptor.Message("Error opening model file "+absolutePath));
                };
                if (i==descriptor.getCurrentModelIndex()){
                    saveCurrentModel=OpenSimDB.getInstance().getCurrentModel();
                }
            }
        }
        if (saveCurrentModel!=null)
            setCurrentModel(saveCurrentModel, false);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(new OpenSimDBDescriptor(instance));
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        OpenSimDBDescriptor desc = (OpenSimDBDescriptor)in.readObject();
        rebuild(desc);
    }
    /*
     * Get the context = SimTK::State holder for the passed in model
     */
    public OpenSimContext getContext(Model model) {
        OpenSimContext dContext = mapModelsToContexts.get(model);
        if(dContext==null)
            return createContext(model, false);
        return (dContext);
    }
    
    /**
     * Create a context to associate with the passed in model and add it to the global map.
     */
    public OpenSimContext createContext(Model aModel, boolean force) {
        OpenSimContext context = mapModelsToContexts.get(aModel);
        if (context ==null || force){
            OpenSimContext newContext;
            try {
                newContext = new OpenSimContext(aModel.initSystem(), aModel);
            } catch (IOException ex) {
                ErrorDialog.displayExceptionDialog(ex);
                return null;
            }
         mapModelsToContexts.put(aModel, newContext);
         context = newContext;
        }
         return context;
    }

    public void disableForce(OpenSimObject openSimObject, boolean disabled) {
        Force f = Force.safeDownCast(openSimObject);
        OpenSimContext context = getContext(f.getModel());
        boolean oldState = f.get_appliesForce();
        if (oldState != disabled){
            context.setAppliesForce(f, disabled);
            // Fire an event so that other interested parties (e.g. Opened tools, view can update)
            Vector<OpenSimObject> objs = new Vector<OpenSimObject>(1);
            objs.add(openSimObject);
            ObjectEnabledStateChangeEvent evnt = new ObjectEnabledStateChangeEvent(this, f.getModel(), objs);
            setChanged();
            notifyObservers(evnt);
       }
    }

    public void disableConstraint(OpenSimObject openSimObject, boolean disabled) {
        Constraint c = Constraint.safeDownCast(openSimObject);
        OpenSimContext context = getContext(c.getModel());
        boolean oldState = context.isEnforced(c);
        if (oldState != disabled){
            context.setIsEnforced(c, disabled);
            // Fire an event so that other interested parties (e.g. Opened tools, view can update)
            Vector<OpenSimObject> objs = new Vector<OpenSimObject>(1);
            objs.add(openSimObject);
            ObjectEnabledStateChangeEvent evnt = new ObjectEnabledStateChangeEvent(this, c.getModel(), objs);
            setChanged();
            notifyObservers(evnt);
       }
    }
    public void markObjectsChanged(Vector<OpenSimObject> objs, Model model) {
        ObjectsChangedEvent evnt = new ObjectsChangedEvent(this, model, objs);
        getInstance().setChanged();
        getInstance().notifyObservers(evnt);
    }
    
   /**
    * Get gui elements for passed in model
    */
   public SingleModelGuiElements getModelGuiElements(Model aModel) {
      return mapModelsToGuiElements.get(aModel);
   }
   /** Handle mode changes
    * 
    */
   public static void setMode(OpenSimDB.Mode newMode){
       if (newMode!= mode){
           mode = newMode;
           instance.setChanged();
            ModeChangeEvent evnt = new ModeChangeEvent(newMode);
            instance.notifyObservers(evnt);
       }
   }
   public void switchToModelingMode() {
       setMode(OpenSimDB.Mode.Modeling);
   }
   public void switchToAnalysisMode() {
       setMode(OpenSimDB.Mode.Analysis);
   }
   
 }
