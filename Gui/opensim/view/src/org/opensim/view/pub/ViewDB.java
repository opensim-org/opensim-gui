/* -------------------------------------------------------------------------- *
 * OpenSim: ViewDB.java                                                       *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Paul Mitiguy                                       *
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
 *
 * ViewDB
 * Author(s): Ayman Habib
 */
package org.opensim.view.pub;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.prefs.Preferences;
import javax.swing.SwingUtilities;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.eclipse.jetty.JettyMain;
import org.eclipse.jetty.VisWebSocket;
import org.eclipse.jetty.WebSocketDB;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.opensim.logger.OpenSimLogger;
import org.opensim.modeling.*;
import org.opensim.view.experimentaldata.ModelForExperimentalData;
import org.opensim.swingui.SwingWorker;
import org.opensim.threejs.JSONMessageHandler;
import org.opensim.threejs.JSONUtilities;
import org.opensim.threejs.ModelVisualizationJson;
import org.opensim.threejs.VisualizerWindowAction;
import org.opensim.utils.ErrorDialog;
import org.opensim.utils.Prefs;
import org.opensim.utils.TheApp;
import org.opensim.view.*;
import org.opensim.view.experimentaldata.ExperimentalDataVisuals;
import static org.opensim.view.pub.ViewDB.isVtkGraphicsAvailable;
import vtk.AxesActor;
import vtk.FrameActor;
import vtk.vtkActor;
import vtk.vtkActor2D;
import vtk.vtkAssembly;
import vtk.vtkAssemblyNode;
import vtk.vtkAssemblyPath;
import vtk.vtkCamera;
import vtk.vtkCaptionActor2D;
import vtk.vtkFollower;
import vtk.vtkMatrix4x4;
import vtk.vtkPolyDataMapper;
import vtk.vtkProp3D;
import vtk.vtkProp3DCollection;
import vtk.vtkProperty;
import vtk.vtkTextActor;
import vtk.vtkTextProperty;
import vtk.vtkTransform;
import vtk.vtkVectorText;

/**
 *
 * @author Ayman Habib
 *
 * A Database of Displayed models, and displayed windows ModelWindowVTKTopComponents
 * Also keeps track of currently activated model window ModelWindowVTKTopComponent
 */
public final class ViewDB extends Observable implements Observer, LookupListener {

   // List of view windows currently displayed
   static ArrayList<ModelWindowVTKTopComponent> openWindows = new ArrayList<ModelWindowVTKTopComponent>(4);
   // List of models currently available in all views
   private static ArrayList<SingleModelVisuals> modelVisuals = new ArrayList<SingleModelVisuals>(4);
   private static ArrayList<Boolean> saveStatus = new ArrayList<Boolean>(4);
   // One single vtAssemby for the whole Scene
   private static vtkAssembly sceneAssembly;
   private static WebSocketDB websocketdb;
   private static JSONObject jsondb;
   private SelectedObject selectInVisualizer = null;
   
   /* Following block handles buffering Appearance changes so they're sent once
    * as a message to visualizer.
   */
   private static boolean applyAppearanceChange = true;

    public void endAnimation() {
        JSONObject msg = new JSONObject();
        msg.put("Op", "endAnimation");
        websocketdb.broadcastMessageJson(msg, null);
        
    }

    public void startAnimation() {
        JSONObject msg = new JSONObject();
        msg.put("Op", "startAnimation");
        websocketdb.broadcastMessageJson(msg, null);
    }

    public void updateComponentVisuals(Model model, Component mc, Boolean frame) {
        if (websocketdb!=null){
            ModelVisualizationJson modelJson = getModelVisualizationJson(model);
            if (modelJson.componentHasVisuals(mc)){
                websocketdb.broadcastMessageJson(modelJson.updateComponentVisuals(mc, frame), null);
            }
            else // just send frame in case xforms change
                websocketdb.broadcastMessageJson(currentJson.createFrameMessageJson(false), null);
        }
    }
    /*
     * Update Decorations downstream from passed in Component.
    */
    public void updateDecorations(Model model, Component comp) {
        if (websocketdb!=null){
            ModelVisualizationJson modelJson = getModelVisualizationJson(model);
             websocketdb.broadcastMessageJson(currentJson.createUpdateDecorationsMessageJson(comp), null);
        }
    }
   
   class AppearanceChange {
       Model model;
       Component mc;
       AbstractProperty prop;
       
       AppearanceChange(Model model, Component mc, AbstractProperty prop){
           this.model = model; this.mc = mc; this.prop = prop;
       }
   }
   private static ArrayList<AppearanceChange> pendingAppearanceChanges = new ArrayList<AppearanceChange>();
    /**
     * @param aApplyAppearance the applyAppearance to set
     */
    public static void setApplyAppearanceChange(boolean aApplyAppearance) {
        applyAppearanceChange = aApplyAppearance;
        if (applyAppearanceChange){
            applyPendingAppearanceChanges();
        }
    }
 
    private static void applyPendingAppearanceChanges() {
        if (websocketdb!=null){
            // Create MuliCmd
            JSONObject topMsg = new JSONObject();
            JSONObject msgMulti = new JSONObject();
            msgMulti.put("type", "MultiCmdsCommand");
            topMsg.put("Op", "execute");
            topMsg.put("command", msgMulti);
            JSONArray commands = new JSONArray();
            for (AppearanceChange appChange:pendingAppearanceChanges){
                ModelVisualizationJson modelJson = getInstance().getModelVisualizationJson(appChange.model);
                JSONObject msg = modelJson.createAppearanceMessage(appChange.mc, appChange.prop);
                commands.add(msg.get("command"));
            }
            msgMulti.put("cmds", commands);
            websocketdb.broadcastMessageJson(topMsg, null);
        }
        pendingAppearanceChanges.clear();
    }
    // End AppearanceChange buffer handling
    
   /**
     * @return the myLookup
     */
    public static Lookup getLookup() {
        return myLookup;
    }

    /**
     * @return the jsondb
     */
    public static JSONObject getJsondb() {
        return jsondb;
    }

   // Map models to visuals
   private Hashtable<Model, SingleModelVisuals> mapModelsToVisuals =
           new Hashtable<Model, SingleModelVisuals>();
   
   private Hashtable<Model, ModelVisualizationJson> mapModelsToJsons =
           new Hashtable<Model, ModelVisualizationJson>();

   private Hashtable<Model, ModelSettingsSerializer> mapModelsToSettings =
           new Hashtable<Model, ModelSettingsSerializer>();
   private Hashtable<Model, Double> modelOpacities = new Hashtable<Model, Double>();
   
   static ViewDB instance=null;
   private static boolean graphicsAvailable = true;
   // Window currently designated as current.
   private static ModelWindowVTKTopComponent currentModelWindow=null;
   
   // Flag indicating whether new models are open in a new window or in the same window
   static boolean openModelInNewWindow=true;
   
   static boolean useImmediateModeRendering = false; // Use Render instead of paint
   private ArrayList<SelectedObject> selectedObjects = new ArrayList<SelectedObject>(0);
   private Hashtable<Selectable, vtkCaptionActor2D> selectedObjectsAnnotations = new Hashtable<Selectable, vtkCaptionActor2D>(0);
   private Hashtable<ModelVisualizationJson, Path> modelVisToJsonFilesMap = new Hashtable<ModelVisualizationJson, Path>();
   private AxesActor     axesAssembly=null;
   private boolean axesDisplayed=false;
   private vtkTextActor textActor=null; 
   
   private boolean picking = false;
   private boolean query = false;
   private boolean dragging = false;
   private double draggingZ = 0.0;
   private double nonCurrentModelOpacity = 0.4;
   private double muscleDisplayRadius = 0.005;
   private double markerDisplayRadius = .01;
   private int debugLevel=1;
   private NumberFormat numFormat = NumberFormat.getInstance();
   
    private final static InstanceContent lookupContents = new InstanceContent();
    private final static Lookup myLookup = new AbstractLookup (lookupContents);
    Lookup.Result<OpenSimObject> r;

    private ModelVisualizationJson currentJson;
   /** Creates a new instance of ViewDB */
   private ViewDB() {
        applyPreferences();
        r = myLookup.lookupResult(OpenSimObject.class);
        websocketdb = WebSocketDB.getInstance();
        jsondb = JSONUtilities.createTopLevelJson();
     }

    public void applyPreferences() {
        String debugLevelString="0";
        String saved = Preferences.userNodeForPackage(TheApp.class).get("Debug", debugLevelString);
        // Parse saved to an int, use 0 (no debug) on failure
        if (saved.equalsIgnoreCase("Off")) saved="0";
        debugLevel = Integer.parseInt(saved);
        GeometryFileLocator.updateGeometrySearchPathsFromPreferences();
    }
   
   /**
    * Enforce a singleton pattern
    */
   public static ViewDB getInstance() {
      if( instance==null ) {
          instance = new ViewDB();
          websocketdb.setObserver(instance);
      }
      return instance;
   }
   
   
   //--------------------------------------------------------------------------
   public static void  ViewDBGetInstanceRepaintAll(  )  
   { 
       ViewDB viewDBInstance = ViewDB.getInstance();
       if( viewDBInstance != null ) viewDBInstance.repaintAll(); 
   }
   
   //--------------------------------------------------------------------------
   public static void  ViewDBGetInstanceRenderAll(  )  
   { 
       ViewDB viewDBInstance = ViewDB.getInstance();
       if( viewDBInstance != null ) viewDBInstance.renderAll(); 
   } 
   
    
    // The setChanged() protected method must overridden to make it public
    public synchronized void setChanged() {
        super.setChanged();
    }
    
   public static ModelWindowVTKTopComponent getCurrentModelWindow() {
      return currentModelWindow;
   }
   /**
    * Keep track of current view window so that dialogs are cenetred on top of it if necessary.
    * Just a handy window to use.
    */
   public static void setCurrentModelWindow(ModelWindowVTKTopComponent aCurrentModelWindow) {
      currentModelWindow = aCurrentModelWindow;
   }
   
   /**
    * update Method is called whenever a model is added, removed and/or moved in the GUI
    * Observable should be of type OpenSimDB.
    */
   public void update(Observable o, Object arg) {
      //if (!isVtkGraphicsAvailable()) return;
      if (arg instanceof JSONObject){
          handleJson((JSONObject) arg);
      }
      if (o instanceof VisWebSocket){
          // Sync. socket with current ViweDB
          getInstance().sync((VisWebSocket) o);
          if (currentJson==null)
              setCurrentJson();
          return;
      }
      if (o instanceof OpenSimDB){
         if (arg instanceof ObjectsAddedEvent) {
            ObjectsAddedEvent ev = (ObjectsAddedEvent)arg;
            Vector<OpenSimObject> objs = ev.getObjects();
            for (int i=0; i<objs.size(); i++) {
               if (objs.get(i) instanceof Model) {
                    assert(false);
                     }
               if (objs.get(i) instanceof Marker) {
                  ModelVisualizationJson modelJson = getInstance().getModelVisualizationJson(ev.getModel());
                  JSONObject markerJson = modelJson.createMarkerJson(Marker.safeDownCast(objs.get(i)));
                  // Send message to add markerJson to live visualizer instances
                  addVisualizerObject(markerJson, null);
                  //repaintAll();
               } 
            }
         } else if (arg instanceof ObjectSetCurrentEvent) {
            // Current model has changed. For view purposes this affects available commands
            // Changes in the Tree view are handled by the Explorer View. Because only
            // objects in the current model can be selected and manipulated, clear all
            // currently selected objects.
            clearSelectedObjects();
            ObjectSetCurrentEvent ev = (ObjectSetCurrentEvent)arg;
            Vector<OpenSimObject> objs = ev.getObjects();
            for (int i=0; i<objs.size(); i++) {
               if (objs.get(i) instanceof Model) {
                  Model currentModel = (Model)objs.get(i);
                  // Apply opacity to other models
                  Enumeration<Model> models=mapModelsToVisuals.keys();
                  while(models.hasMoreElements()){
                     Model next = models.nextElement();
                     double nominalOpacity=modelOpacities.get(next);
                     SingleModelVisuals vis = mapModelsToVisuals.get(next);
                     if (next == currentModel){
                        setObjectOpacity(next, nominalOpacity);
                        vis.setPickable(true);
                        //displayText("Model Name:"+next.getName(),16);
                     } else {
                        setObjectOpacity(next, getNonCurrentModelOpacity()*nominalOpacity);
                        vis.setPickable(false);
                     }
                  }
                  setCurrentJson();
                  break;
               }
            }
         } else if (arg instanceof ObjectsDeletedEvent) {
              handleObjectsDeletedEvent(arg);
         } else if (arg instanceof ObjectEnabledStateChangeEvent) {
              handleObjectsEnabledStateChangeEvent(arg);
         }else if (arg instanceof ObjectsRenamedEvent){
            ObjectsRenamedEvent ev = (ObjectsRenamedEvent)arg;
            // The name change might be for one or more of the selected objects
            statusDisplaySelectedObjects();
            repaintAll();
            Vector<OpenSimObject> objs = ev.getObjects();
            for (int i=0; i<objs.size(); i++) {
               // if an actuator changed names, update the list of names in the model gui elements
               if (objs.get(i) instanceof Actuator) {
                  //Actuator act = (Actuator)ev.getObject();
                  //getModelGuiElements(act.getModel()).updateActuatorNames();
               } else if (objs.get(i) instanceof Marker) {
                  //Marker marker = (Marker)ev.getObject();
                  //getModelGuiElements(marker.getBody().get_model()).updateMarkerNames();
               }
            }
         } else if (arg instanceof ModelEvent ) {
            ModelEvent ev = (ModelEvent)arg;
            // We need to detect if this the first time anything is loaded into the app
            // (or new project) if so we'll open a window, otherwise we will
            // display the new Model in existing views
            if (ev.getOperation()==ModelEvent.Operation.Open){
               Model evModel = ev.getModel();
               processSavedSettings(evModel);
                try {
                 createNewViewWindowIfNeeded();
               }
               catch(UnsatisfiedLinkError e){
                   setGraphicsAvailable(false);
               }
               if (isVtkGraphicsAvailable()){
                    // Create visuals for the evModel
                    SingleModelVisuals newModelVisual = (evModel instanceof ModelForExperimentalData)?
                        new ExperimentalDataVisuals(evModel):
                        new SingleModelVisuals(evModel);
                    // add to map from models to modelVisuals so that it's accesisble
                    // thru tree picks
                    mapModelsToVisuals.put(evModel, newModelVisual);
                    // add to list of models
                    getModelVisuals().add(newModelVisual); //Too late??
                    modelOpacities.put(evModel, 1.0);
                    addVisObjectToAllViews();
                    // Compute placement so that evModel does not intersect others
                    vtkMatrix4x4 m= (evModel instanceof ModelForExperimentalData)?
                           new vtkMatrix4x4():
                           getInitialTransform(newModelVisual);
                    newModelVisual.getModelDisplayAssembly().SetUserMatrix(m);

                    sceneAssembly.AddPart(newModelVisual.getModelDisplayAssembly());
               }
               if (websocketdb!=null){
                   // create Json for model
                   //This will be invoked only when visualizer windows are already open
                   if (debugLevel > 1)
                        System.out.println("ModelVisualizationJson constructed in ViewDB.Update");
                   ModelVisualizationJson vizJson = getJsonForModel(jsondb, evModel);
                   exportModelJsonToVisualizer(vizJson, null);
                }
               else {
                   // Same as open visualizer window 
                   startVisualizationServer();
               }
              // Check if this refits scene into window
               // int rc = newModelVisual.getModelDisplayAssembly().GetReferenceCount();
               if(OpenSimDB.getInstance().getNumModels()==1) { 
                  Iterator<ModelWindowVTKTopComponent> windowIter = openWindows.iterator();
                  double[] bnds = new double[6];
                  while(windowIter.hasNext()){
                     bnds = computeSceneBounds();
                     //createBBox();
                     ModelWindowVTKTopComponent nextWindow = windowIter.next();
                     // This line may need to be enclosed in a Lock /UnLock pair per vtkPanel
                     lockDrawingSurfaces(true);
                     nextWindow.getCanvas().GetRenderer().ResetCamera(bnds);
                     lockDrawingSurfaces(false);
                  }
               }
               //rc = newModelVisual.getModelDisplayAssembly().GetReferenceCount();
               repaintAll();
               //rc = newModelVisual.getModelDisplayAssembly().GetReferenceCount();
            } else if (ev.getOperation()==ModelEvent.Operation.Close){
               Model dModel = ev.getModel();
               mapModelsToSettings.remove(dModel);

               // Remove model-associated objects from selection list!
               removeObjectsBelongingToModelFromSelection(dModel);
               SingleModelVisuals visModel = mapModelsToVisuals.get(dModel);
               // Remove from display
               //int rc = visModel.getModelDisplayAssembly().GetReferenceCount();
               if (visModel != null){
                    removeObjectFromScene(visModel.getModelDisplayAssembly());
                    //rc = visModel.getModelDisplayAssembly().GetReferenceCount();
                    // Remove from lists
                    modelVisuals.remove(visModel);
               }
               removeAnnotationObjects(dModel);
               mapModelsToVisuals.remove(dModel);
               modelOpacities.remove(dModel);
               //rc = visModel.getModelDisplayAssembly().GetReferenceCount();
               if (visModel != null) visModel.cleanup();
               if (websocketdb != null){
                    ModelVisualizationJson dJson = mapModelsToJsons.get(dModel);
                    if (dJson==null)
                        return;
                    JSONObject msg = dJson.createCloseModelJson();
                    websocketdb.broadcastMessageJson(msg, null);
                    if (debugLevel > 1)
                        System.out.println(msg.toJSONString());
                    mapModelsToJsons.remove(dModel);
                    try {
                       if (modelVisToJsonFilesMap.get(dJson)!=null)
                            Files.deleteIfExists(modelVisToJsonFilesMap.get(dJson));
                    } catch (IOException ex) {
                       ErrorDialog.displayExceptionDialog(ex);
                    }
                    modelVisToJsonFilesMap.remove(dJson);
                    if (currentJson == dJson) // Cleanup stale Json, will be set fresh by next current model
                        currentJson = null;
                }
               
            } else if (ev.getOperation()==ModelEvent.Operation.SetCurrent) {
               // Current model has changed. For view purposes this affects available commands
               // Changes in the Tree view are handled by the Explorer View

               // Apply opacity to other models
               Enumeration<Model> models=mapModelsToVisuals.keys();
               while(models.hasMoreElements()){
                  Model next = models.nextElement();
                  double nominalOpacity=modelOpacities.get(next);
                  SingleModelVisuals vis = mapModelsToVisuals.get(next);
                  if (next==ev.getModel()){
                       setObjectOpacity(next, nominalOpacity);
                  }
                  else{
                       setObjectOpacity(next, getNonCurrentModelOpacity()*nominalOpacity);
                  }
               }
               if (websocketdb != null){
                    Model cModel = ev.getModel();
                    currentJson = mapModelsToJsons.get(cModel);
                    JSONObject msg = currentJson.createSetCurrentModelJson();
                    websocketdb.broadcastMessageJson(msg, null);
                    if (debugLevel > 1)
                        System.out.println(msg.toJSONString());
                }

            } else if (ev.getOperation()==ModelEvent.Operation.Save) {
               // If a model is saved then its document filename has changed and we should update the settings file accordingly
               updateSettingsSerializer(ev.getModel());
            }
         }
      }
   }

    public void exportModelJsonToVisualizer(ModelVisualizationJson vizJson, VisWebSocket socket) {
        String fileName = JettyMain.getServerWorkingDir()+vizJson.getModelUUID().toString().substring(0, 8)+".json";
        //System.out.println("Json file path ="+fileName);
       try {
           // Write vizJson to file and send message to visualizer to open it
           JSONUtilities.writeJsonFile(vizJson, fileName);
           modelVisToJsonFilesMap.put(vizJson, Paths.get(fileName));
       } catch (IOException ex) {
           ErrorDialog.displayExceptionDialog(ex);
       }
        // send message to visualizer to load model from file
        websocketdb.broadcastMessageJson(vizJson.createOpenModelJson(), socket);
    }

    private void handleObjectsDeletedEvent(final Object arg) {
        ObjectsDeletedEvent ev = (ObjectsDeletedEvent)arg;
        Vector<OpenSimObject> objs = ev.getObjects();
        boolean repaint = false;
        boolean selectedDeleted = false;
        for (int i=0; i<objs.size(); i++) {
           OpenSimObject obj = objs.get(i);
           int j = findObjectInSelectedList(obj);
           if (j >= 0) {
              selectedObjects.remove(j);
              selectedDeleted = true;
           }
           if (obj instanceof Model) {
              // TODO: do same stuff as ModelEvent.Operation.Close event
           } else if (obj instanceof PathActuator) {
              removeObjectsBelongingToMuscleFromSelection(obj);
              getModelVisuals(ev.getModel()).removeGeometry((Actuator)obj);
              repaint = true;
           } else if (obj instanceof Marker) {
              ModelVisualizationJson vis = mapModelsToJsons.get(ev.getModel());
              String test = obj.getConcreteClassName();
              Marker marker = Marker.safeDownCast(obj);
              this.removeVisualizerObject(marker, marker.getParentFrame());
           }
        }
        if (selectedDeleted)
           statusDisplaySelectedObjects();
        if (repaint)
           repaintAll();
    }

    private void addVisObjectToAllViews() {
        //From here on we're adding things to display so we better lock
        
        if (sceneAssembly==null){
           createScene();
           // Add assembly to all views
           Iterator<ModelWindowVTKTopComponent> windowIter = openWindows.iterator();
           while(windowIter.hasNext()){
              ModelWindowVTKTopComponent nextWindow = windowIter.next();
              nextWindow.getCanvas().GetRenderer().AddViewProp(sceneAssembly);
           }
           //createXLabel();
        }
    }
   
   public static ArrayList<SingleModelVisuals> getModelVisuals() {
      return modelVisuals;
   }

   public OpenSimObject pickObject(vtkAssemblyPath asmPath) {
      Iterator<SingleModelVisuals> iter = modelVisuals.iterator();
      while(iter.hasNext()){
         SingleModelVisuals nextModel = iter.next();
         OpenSimObject obj = nextModel.pickObject(asmPath);
         if (obj != null){
            // Find corresponding tree node
            return obj;
         }
      }
      return null;
   }
   
   /**
    * Get the object corresponding to selected vtkAssemblyPath
    **/
   OpenSimObject getObjectForVtkRep(vtkAssembly prop) {
      Iterator<SingleModelVisuals> iter = modelVisuals.iterator();
      while(iter.hasNext()){
         SingleModelVisuals nextModel = iter.next();
         OpenSimObject obj = nextModel.getObjectForVtkRep(prop);
         if (obj != null)
            return obj;
      }
      return null;
   }
   
   /**
    * Get the vtk object corresponding to passed in opensim object
    **/
   public vtkProp3D getVtkRepForObject(OpenSimObject obj) {
      if (!isVtkGraphicsAvailable()) return null;
      Iterator<SingleModelVisuals> iter = modelVisuals.iterator();
      while(iter.hasNext()){
         SingleModelVisuals nextModel = iter.next();
         vtkProp3D objAssembly = nextModel.getVtkRepForObject(obj);
         if (objAssembly != null)
            return objAssembly;
      }
      return null;
   }
   /**
    * removeWindow removes the passed in window form the list of windows maintaiined
    * by ViewDB
    **/
   public void removeWindow(ModelWindowVTKTopComponent modelWindowVTKTopComponent) {
      openWindows.remove(modelWindowVTKTopComponent);
   }
   /**
    * Add a new Viewing. window
    * This executes in the Swing thread.
    */
   public void addViewWindow() {
       try {
        addViewWindow(null);
       }
       catch(UnsatisfiedLinkError e){
           setGraphicsAvailable(false);
       }
   }
   
   public ModelWindowVTKTopComponent addViewWindow(String desiredName) {
      // Create the window
      final ModelWindowVTKTopComponent win = new ModelWindowVTKTopComponent();
      // Fix name
      if (desiredName==null){
          int ct=0;
          while(!ViewDB.getInstance().checkValidViewName(win.getDisplayName(), win)){
             win.setTabDisplayName(NbBundle.getMessage(
                     ModelWindowVTKTopComponent.class,
                     "UnsavedModelNameFormat",
                     new Object[] { new Integer(ct++) }
             ));
          };
      }
      else
          win.setTabDisplayName(desiredName);
      
      if (getCurrentModelWindow()!=null){    // Copy camera from 
          vtkCamera lastCamera=getCurrentModelWindow().getCanvas().GetRenderer().GetActiveCamera();
          win.getCanvas().applyOrientation(lastCamera);
            getCurrentModelWindow().getCanvas().GetRenderer().AddActor2D(textActor);
      }
      if (SwingUtilities.isEventDispatchThread()){
          win.open();
          win.requestActive();
          setCurrentModelWindow(win);
          // Open it and make it active
          openWindows.add(win);
          win.getCanvas().GetRenderer().AddViewProp(sceneAssembly);
          repaintAll();
      }
      else {
          SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                  win.open();
                  win.requestActive();
                  setCurrentModelWindow(win);
                  // Open it and make it active
                  openWindows.add(win);
                  win.getCanvas().GetRenderer().AddViewProp(sceneAssembly);
                  repaintAll();
                }});
      }
      // If the user manually added a new view, we won't need to automatically create a new one when model is loaded.
      openModelInNewWindow=false;
      return win;
   }
   /**
    * Helper function to implement model hide/show.
    *
    */
   public void toggleModelDisplay(Model model, boolean onOff) {
    if (isVtkGraphicsAvailable()){  
      SingleModelVisuals modelVis = mapModelsToVisuals.get(model);
      if (!modelVis.isVisible() && onOff){
         sceneAssembly.AddPart(modelVis.getModelDisplayAssembly());
         modelVis.setVisible(true);
      }
      else if (modelVis.isVisible() && !onOff) {
         sceneAssembly.RemovePart(modelVis.getModelDisplayAssembly());
         modelVis.setVisible(false);
      }
      repaintAll();
    }
    if (websocketdb != null){
       ModelVisualizationJson vizJson = getInstance().mapModelsToJsons.get(model);
       websocketdb.broadcastMessageJson(vizJson.createToggleModelVisibilityCommand(onOff), null);
    }
   }
   /**
    * Decide if a new window is needed to be created. Right now this's done only first time the application
    * is started. This may need to be change when a new project is opened
    */
   private void createNewViewWindowIfNeeded() {
      if (openModelInNewWindow){
         boolean evt = SwingUtilities.isEventDispatchThread();
         if (isVtkGraphicsAvailable()){
            // The following line has the side effect of loading VTK libraries, can't move it to another thread'
            final ModelWindowVTKTopComponent win = new ModelWindowVTKTopComponent(); 
            openWindows.add(win);
            openModelInNewWindow=false;
            setCurrentModelWindow(win);
            // open window later rather than now to avoid having a rectangular blank patch appearing over the GUI while
            // the model is loading and the scene is set up
            if (SwingUtilities.isEventDispatchThread()){
                win.requestActive();
                win.open();
            }
            else 
            {
                       SwingUtilities.invokeLater(new Runnable(){ // Should change to WindowManager.getDefault().invokeWhenUIReady if/when we upgrade NB
                           public void run(){
                            win.open();
                            win.requestActive();
                           }});
             } 
         }

      }
   }
   /**
    * Add an arbitrary Object to the scene (all views)
    */
   public void addObjectToScene(vtkProp3D aProp) {
      sceneAssembly.AddPart(aProp);
      repaintAll();
   }
   
   /**
    * Remove an arbirary Object from the scene (all views)
    */
   public void removeObjectFromScene(vtkProp3D aProp) {
      sceneAssembly.RemovePart(aProp);
      repaintAll();
   }
   /**
    * Return a flag indicating whether the model is currently shown or hidden
    */
   public boolean getDisplayStatus(Model m) {
      return true;//mapModelsToVisuals.get(m).isVisible();
      
   }
   
   public static Model getCurrentModel() {
      return OpenSimDB.getInstance().getCurrentModel();
   }
   /**
    * Cycle through displayed windows and repaint them
    */
   public static void repaintAll() {
      Iterator<ModelWindowVTKTopComponent> windowIter = openWindows.iterator();
      while( windowIter.hasNext() ){
         if( useImmediateModeRendering )
            windowIter.next().getCanvas().Render();
         else
            windowIter.next().getCanvas().repaint();
      }
   }

   public static void renderAll() {
      Iterator<ModelWindowVTKTopComponent> windowIter = openWindows.iterator();
      while(windowIter.hasNext()){
         windowIter.next().getCanvas().Render();
      }
   }
   
   /**
    * Set the color of the passed in object.
    */
   public void setObjectColor(OpenSimObject object, double[] colorComponents) {

          Component mc = Component.safeDownCast(object);
          if (mc!=null){
              // FOX40 getModelVisuals(mc.getModel()).setObjectColor(object, colorComponents);
          }
      
      renderAll();
   }
   
   //----------------------------------------------------------------
   public static double[]  getObjectRGBColorIn3DoublesWithRangeFrom0To1( OpenSimObject object ) 
   {
      if( PathPoint.safeDownCast(object) != null ) return null;
      vtkProp3D asm = ViewDB.getInstance().getVtkRepForObject( object );
      if( asm == null ) return null; 

      if( object instanceof Body )
         return ((BodyDisplayer)asm).GetColorOrReturnNull();
      else if( object instanceof Geometry )
      {
         double[] colorToReturn = {-1, -1, -1};
         Vec3 vecColorToReturn =((Geometry)object).getColor();
         for (int i=0; i<3; i++) colorToReturn[i]=vecColorToReturn.get(i);
         return colorToReturn;
      }
      return null;
   }   
   
   //-----------------------------------------------------------------------------
   public static double[] getObjectRGBColorIn3DoublesWithRangeFrom0To1Deprecated( OpenSimObject object )
   {
      double objectColor[] = { 1.0, 1.0, 1.0 }; 
      if( object == null ) return objectColor; 
      vtkProperty propertyToFillInformation = new vtkProperty(); 
      ViewDB.getObjectProperties( object, propertyToFillInformation );
      return propertyToFillInformation.GetColor();  // This seems to always return 1,1,1.
      // Currently this just returns White.  May be due to VTK being bunches of pieces - and no real color available.
   }

   public void applyColor(final double[] colorComponents, final vtkProp3D asm, boolean allowUndo) {
       AbstractUndoableEdit auEdit = new AbstractUndoableEdit(){
           public boolean canUndo() {
               return true;
           }
           public boolean canRedo() {
               return true;
           }
           public void undo() throws CannotUndoException {
               super.undo();
               final double[] white=new double[]{1., 1., 1.};
               applyColor(white, asm, false);
           }
           public void redo() throws CannotRedoException {
               super.redo();
               applyColor(colorComponents, asm, true);
           }

           @Override
           public String getRedoPresentationName() {
               return "Redo Color change";
            }

           @Override
           public String getUndoPresentationName() {
               return "Undo Color change";
           }
           
       };
       if (allowUndo)
            ExplorerTopComponent.addUndoableEdit(auEdit);

       ApplyFunctionToActors(asm, new ActorFunctionApplier() {
         public void apply(vtkActor actor) { 
             if (!(actor instanceof FrameActor)){
                actor.GetProperty().SetColor(colorComponents);
                actor.Modified();
             }
         }});
      renderAll();
   }
   
   public void setNominalModelOpacity(OpenSimObject object, double newOpacity)
   {
      if (object instanceof Model){
         modelOpacities.put((Model)object, newOpacity);
         double vtkOpacity= newOpacity;
         if (object.equals(getCurrentModel())){
         }
         else {
            vtkOpacity *= getNonCurrentModelOpacity();
         }
         setObjectOpacity(object, vtkOpacity);            
      }
   }
   
   public double getNominalModelOpacity(Model modelObject)
   {
      return modelOpacities.get(modelObject);
   }

   /**
    * Set the Opacity of the passed in object to newOpacity
    */
   //-----------------------------------------------------------------------------
   public static void setObjectOpacity( OpenSimObject object, double newOpacity ) {
    if (object instanceof Geometry)
          ((Geometry)object).setOpacity(newOpacity);
    if (isVtkGraphicsAvailable()){
      vtkProp3D asm = ViewDB.getInstance().getVtkRepForObject(object);
      ViewDB.applyOpacity( newOpacity, asm );
      if( asm instanceof BodyDisplayer )
          ((BodyDisplayer) asm).setOpacity(newOpacity);
     }
    
   }
   
   //-----------------------------------------------------------------------------
   private static void applyOpacity( final double newOpacity, final vtkProp3D asm ) {
      ViewDB.ApplyFunctionToActors( asm, new ActorFunctionApplier() {
         public void apply(vtkActor actor) { actor.GetProperty().SetOpacity(newOpacity); }});
      ViewDB.repaintAll();
   }
   /**
    * Retrieve the display properties for the passed in object.
    */
   public static void getObjectProperties(OpenSimObject object, final vtkProperty saveProperty) {
      vtkProp3D asm = ViewDB.getInstance().getVtkRepForObject(object);
      ApplyFunctionToActors(asm, new ActorFunctionApplier() {
         public void apply(vtkActor actor) { 
            saveProperty.SetColor(actor.GetProperty().GetColor());
            saveProperty.SetOpacity(actor.GetProperty().GetOpacity()); }});
   }
   
   
   //-----------------------------------------------------------------------------
   public static double getObjectOpacityInRangeFrom0To1( OpenSimObject object )
   {
      if( object == null ) return 1; 
      vtkProperty propertyToFillInformation = new vtkProperty(); 
      ViewDB.getObjectProperties( object, propertyToFillInformation );
      return propertyToFillInformation.GetOpacity();
   }
   
   
   public void setObjectProperties(OpenSimObject object, vtkProperty saveProperty) {
      setObjectColor(object, saveProperty.GetColor());
      setObjectOpacity(object, saveProperty.GetOpacity());
      repaintAll();
   }
   /**
    * Selection related functions
    */

   /**
    * Remove items from selection list which belong to the given model
    */
   private void removeObjectsBelongingToModelFromSelection(Model model)
   {
      boolean modified = false;
      for(int i=selectedObjects.size()-1; i>=0; i--) {
         Model ownerModel = selectedObjects.get(i).getOwnerModel();
         if(Model.getCPtr(model) == Model.getCPtr(ownerModel)) {
            markSelected(selectedObjects.get(i), false, false, false);
            selectedObjects.remove(i);
            modified = true;
         }
      }
      if(modified) {
         statusDisplaySelectedObjects();
         repaintAll();
      }
   }
   
   /**
    * Remove items from selection list which belong to the given model
    */
   public void removeObjectsBelongingToMuscleFromSelection(OpenSimObject objectWithPath)
   {
      boolean modified = false;
      OpenSimObject pathObject =  objectWithPath.getPropertyByName("GeometryPath").getValueAsObject();
      GeometryPath gp = GeometryPath.safeDownCast(pathObject);
      for (int i=selectedObjects.size()-1; i>=0; i--) {
         // First see if the selected object is a objectWithPath.
          // markSelected(selectedObjects.get(i), false, false, false);
          // selectedObjects.remove(i);
          // modified = true;
           
         // Now see if the selected object is a objectWithPath point.
         PathPoint mp = PathPoint.safeDownCast(selectedObjects.get(i).getOpenSimObject());
         if (mp != null) {
            for (int j=0; j < gp.getPathPointSet().getSize(); j++) {
               if (PathPoint.getCPtr(mp) == PathPoint.getCPtr(gp.getPathPointSet().get(j))) {
                  markSelected(selectedObjects.get(i), false, false, false);
                  //System.out.println("removing " + mp.getName());
                  selectedObjects.remove(i);
                  modified = true;
                  break;
               }
            }
         }
      }
      if(modified) {
         statusDisplaySelectedObjects();
         repaintAll();
      }
   }

   /**
    * Mark an object as selected (on/off).
    *
    */
   public void markSelected(SelectedObject selectedObject, boolean highlight, boolean sendEvent, boolean updateStatusDisplayAndRepaint) {
      if (highlight)
          lookupContents.add(selectedObject.getOpenSimObject());
      else
          lookupContents.remove(selectedObject.getOpenSimObject());
      
      // Remaining code is vtk depenedent
      //if (!isVtkGraphicsAvailable()) return;
      //selectedObject.markSelected(highlight);
      if (websocketdb != null){
          Model model = selectedObject.getOwnerModel();
          if (highlight){
              if (selectInVisualizer != null && 
                      !selectInVisualizer.equals(selectedObject.getOpenSimObject())){
                    websocketdb.broadcastMessageJson(currentJson.createSelectionJson(selectedObject.getOpenSimObject()), null);
              }
              selectInVisualizer = selectedObject;
          }
          else {
              if (selectInVisualizer!=null){
                websocketdb.broadcastMessageJson(currentJson.createDeselectionJson(), null);
                selectInVisualizer = null;
              }
          }
      }
      if (ViewDB.getInstance().isQuery() && isVtkGraphicsAvailable()){
          if (highlight){
           // Add caption
             vtkCaptionActor2D theCaption = new vtkCaptionActor2D();
             double[] bounds=selectedObject.getBounds();
             theCaption.SetAttachmentPoint(new double[]{
                 (bounds[0]+bounds[1])/2.0,
                 (bounds[2]+bounds[3])/2.0,
                 (bounds[4]+bounds[5])/2.0,
             });
             theCaption.GetTextActor().ScaledTextOn();
             theCaption.SetHeight(.02);
             theCaption.BorderOff();
             if (selectedObject.getOpenSimObject()!=null){
                 theCaption.SetCaption(selectedObject.getOpenSimObject().getName());
                addAnnotationToViews(theCaption);
                 selectedObjectsAnnotations.put(selectedObject,theCaption);
             }
          }
          else {    // if annotationis on remove 
              vtkCaptionActor2D annotation = getAnnotation(selectedObject);
              if (annotation!=null){
                  removeAnnotationFromViews(annotation);
              }
          }
      }

      if(updateStatusDisplayAndRepaint) {
         statusDisplaySelectedObjects();
         repaintAll();
      }

      if(sendEvent) {
         ObjectSelectedEvent evnt = new ObjectSelectedEvent(this, selectedObject, highlight);
         setChanged();
         notifyObservers(evnt);
      }
   }
   
    public void addObjectAnnotationToViews(final vtkCaptionActor2D theCaption, OpenSimObject dObject) {
        addAnnotationToViews(theCaption);
        vtkProp3D prop=ViewDB.getInstance().getVtkRepForObject(dObject);
        theCaption.SetAttachmentPoint(prop.GetCenter());
        selectedObjectsAnnotations.put(new SelectedObject(dObject),theCaption);
    }
    public void addAnnotationToViews(final vtkActor2D theCaption) {
        // Add caption to all windows
        Iterator<ModelWindowVTKTopComponent> windowIter = openWindows.iterator();
        while(windowIter.hasNext()){
           ModelWindowVTKTopComponent nextWindow = windowIter.next();
           nextWindow.getCanvas().GetRenderer().AddActor2D(theCaption);
        }
    }
   
   public ArrayList<SelectedObject> getSelectedObjects() {
      return selectedObjects;
   }

   public void statusDisplaySelectedObjects() {
      String status="";
      for(int i=0; i<selectedObjects.size(); i++) {
         if(i>0) status += ", ";
         status += selectedObjects.get(i).getStatusText();
      }
      StatusDisplayer.getDefault().setStatusText(status, 1000);
   }
   public void statusDisplayApplicationMode() {
      String status="";
      StatusDisplayer.getDefault().setStatusText(status, 1000);
   }

   public void setSelectedObject(OpenSimObject obj) {
      if (findObjectInSelectedList(obj)!=-1)
          return;
      clearSelectedObjects();

      if (obj != null) {
         SelectedObject selectedObject = new SelectedObject(obj);
         selectedObjects.add(selectedObject);
         markSelected(selectedObject, true, true, true);
         ExplorerTopComponent.getDefault().selectNodeForSelectedObject(selectedObject);
         if (websocketdb != null){
             websocketdb.broadcastMessageJson(currentJson.createSelectionJson(obj), null);
         }
      } else { // this function should never be called with obj = null
         ClearSelectedObjectsEvent evnt = new ClearSelectedObjectsEvent(this);
         setChanged();
         notifyObservers(evnt);
      }
   }

   private int findObjectInSelectedList(OpenSimObject obj) {
      for (int i = 0; i < selectedObjects.size(); i++) 
         if (OpenSimObject.getCPtr(obj) == OpenSimObject.getCPtr(selectedObjects.get(i).getOpenSimObject()))
            return i;
      return -1;
   }

   public boolean removeObjectFromSelectedList(OpenSimObject obj) {
      int i = findObjectInSelectedList(obj);
      if(i >= 0) {
         // mark it as unselected
         markSelected(selectedObjects.get(i), false, true, false);
         // remove the object from the list of selected ones
         selectedObjects.remove(i);
         // markSelected can't properly update the statusDisplay because
         // the object is not removed from selectedObjects until after
         // markSelected is called.
         statusDisplaySelectedObjects();
         repaintAll();
         return true;
      }
      return false;
   }

   public void toggleAddSelectedObject(OpenSimObject obj) {
      // If the object is already in the list, remove it
      if (removeObjectFromSelectedList(obj) == false) {
         // If the object is not already in the list, add it
         SelectedObject selectedObject = new SelectedObject(obj);
         selectedObjects.add(selectedObject);
         // mark it as selected
         markSelected(selectedObject, true, true, true);
      }
   }

   public void replaceSelectedObject(OpenSimObject obj) {
      // If the object is already in the list of selected ones, do nothing (a la Illustrator)
      // If the object is not already in the list, make this object the only selected one
      if(!isSelected(obj)) setSelectedObject(obj);
   }

   public void clearSelectedObjects() {
      for (int i = 0; i < selectedObjects.size(); i++) {
         // mark it as unselected
         markSelected(selectedObjects.get(i), false, true, false);
      }
      selectedObjects.clear();
      statusDisplaySelectedObjects();
      repaintAll();
   }

   public boolean isSelected(OpenSimObject obj) {
      return (findObjectInSelectedList(obj) >= 0);
   }

   /**
    * Check if the name passed in is a valid name for a display window (no duplicates
    * for now, until a more restricted naming is needed
    *
    * returns true if newName is a valid name for the passed in view, false otherwise
    */
   public boolean checkValidViewName(String newName, ModelWindowVTKTopComponent view) {
      boolean valid = true;
      Iterator<ModelWindowVTKTopComponent> windowIter = openWindows.iterator();
      while(windowIter.hasNext() && valid){
         ModelWindowVTKTopComponent nextWindow = windowIter.next();
         String nextWindowName = nextWindow.getDisplayName();
         if(nextWindowName.equalsIgnoreCase(newName)){
            valid = (view.equals(nextWindow));
         }
      }
      return valid;
   }
   /**
    * Compute initial offset for a model when displayed.
    * To account for user prefs a property is made
    * just put the model somewhere so that it does not intersect other models.
    *
    * @todo should we allow for rotations as well?
    *
    */
   private vtkMatrix4x4 getInitialTransform(SingleModelVisuals newModelVisual) {
      vtkMatrix4x4 m = new vtkMatrix4x4();
      Iterator<SingleModelVisuals> iter = modelVisuals.iterator();
      // modelBounds are -1 to 1 and updated only after drawing
      double modelBounds[] = newModelVisual.getBounds();
      // If at least one model exists compute bounding box for the scene
      // and place the new model outside the box along z-axis.
      // This could be made into an option where models are placed along X, Y or Z
      // Also possible to define a default offset in any direction and reuse it.
      if (iter.hasNext()){
         double bounds[]= computeSceneBounds();
         String defaultOffsetDirection = NbBundle.getMessage(ViewDB.class,"CTL_DisplayOffsetDir");
         defaultOffsetDirection=Preferences.userNodeForPackage(TheApp.class).get("DisplayOffsetDir", defaultOffsetDirection);
         if (defaultOffsetDirection == null)
            defaultOffsetDirection="Z";
         if (defaultOffsetDirection.equalsIgnoreCase("X"))
            m.SetElement(0, 3, bounds[1]-modelBounds[0]);
         else if (defaultOffsetDirection.equalsIgnoreCase("Y"))
            m.SetElement(1, 3, bounds[3]-modelBounds[2]);
         else
            m.SetElement(2, 3, bounds[5]-modelBounds[4]);
      }
      return m;
   }

   /**
    * get method for the visualization transform (to place a model in a scene).
    */
   public vtkMatrix4x4 getModelVisualsTransform(SingleModelVisuals aModelVisual) {
      return aModelVisual.getModelDisplayAssembly().GetUserMatrix();
   }
   /**
    * set method for the visualization transform (to place a model in a scene).
    */
   public void setModelVisualsTransform(SingleModelVisuals aModelVisual, vtkMatrix4x4 newTransform) {
      aModelVisual.getModelDisplayAssembly().SetUserMatrix(newTransform);
      updateAnnotationAnchors();
   }
   
    public void setModelOffset(ModelVisualizationJson modelJson, Vec3 offsetVec3) {
        JSONObject msg =  modelJson.createTranslateObjectCommand(offsetVec3);
        if (websocketdb!=null){
             websocketdb.broadcastMessageJson(msg, null);
             Transform xform = modelJson.getTransformWRTScene();
             for (int i=0; i<3; i++)
                xform.p().set(i, offsetVec3.get(i));
             modelJson.setTransformWRTScene(xform);
        }
    }
    public Vec3 getModelOffset(ModelVisualizationJson modelJson) {
        if (websocketdb!=null){
             JSONObject msg = new JSONObject();
             msg.put("Op", "GetOffsets");
             websocketdb.broadcastMessageJson(msg, null);
        }
        Vec3 offset = modelJson.getTransformWRTScene().p();
        return offset;
    }
   /**
    * Get a box around the whole scene. Used to fill an intial guess of the bounds for the model display
    */
   public double[] getSceneBounds() {
      double[] sceneBounds = new double[6];
      if (isVtkGraphicsAvailable())
        sceneAssembly.GetBounds(sceneBounds);
      else {
          for (int i=0; i<3; i++){
            sceneBounds[i*2] = -2;
            sceneBounds[i*2+1] = 2;
          }
      }
      return sceneBounds;
   }

   public static double[] boundsUnion(double[] bounds1, double[] bounds2) {
      if(bounds1==null) return bounds2;
      else if(bounds2==null) return bounds1;
      else {
         double[] bounds = new double[6];
         bounds[0]=(bounds1[0]<bounds2[0])?bounds1[0]:bounds2[0];
         bounds[1]=(bounds1[1]>bounds2[1])?bounds1[1]:bounds2[1];
         bounds[2]=(bounds1[2]<bounds2[2])?bounds1[2]:bounds2[2];
         bounds[3]=(bounds1[3]>bounds2[3])?bounds1[3]:bounds2[3];
         bounds[4]=(bounds1[4]<bounds2[4])?bounds1[4]:bounds2[4];
         bounds[5]=(bounds1[5]>bounds2[5])?bounds1[5]:bounds2[5];
         return bounds;
      }
   }

   public double[] getSelectedObjectBounds() {
      double[] bounds = null;
      for(int i=0; i<selectedObjects.size(); i++) bounds = boundsUnion(bounds, selectedObjects.get(i).getBounds());
      return bounds;
   }

   // Don't include glyphs since they often also have glyphs at the origin which screws up the bounding box
   // Also don't include axes
   public double[] getSceneBoundsBodiesOnly() {
      double[] bounds = null;
      Iterator<SingleModelVisuals> iter = modelVisuals.iterator();
      while(iter.hasNext()) bounds = boundsUnion(bounds, iter.next().getBoundsBodiesOnly());
      return bounds;
   }

   /**
    * createScene is invoked once to create the assembly representing the scene
    * that models attach to. If all windows are closed then this function will not be called again.
    */
   private void createScene() {
      sceneAssembly = new vtkAssembly();
      axesAssembly = new AxesActor();
      textActor= new vtkTextActor();
   }
   
   public void showAxes(boolean trueFalse) {
      if (trueFalse)
         addObjectToScene(axesAssembly);
      else
         removeObjectFromScene(axesAssembly);
      setAxesDisplayed(trueFalse);
   }
   
   public void setTextCamera(vtkCamera camera) {
       if (isAxesDisplayed()){
           axesAssembly.setCamera(camera);
       }
   }
   
   public boolean isAxesDisplayed() {
      return axesDisplayed;
   }
   
   public void setAxesDisplayed(boolean axesDisplayed) {
      this.axesDisplayed = axesDisplayed;
   }
   
   /**
    * Search the list of displayed models for the passed in model and if found return
    * its visuals, otherwise return null
    */
   public SingleModelVisuals getModelVisuals(Model aModel) {
      return mapModelsToVisuals.get(aModel);
   }
   public ModelVisualizationJson getModelVisualizationJson(Model aModel) {
      return mapModelsToJsons.get(aModel);
   }
   
   public void sendVisualizerCommand(JSONObject jsonMessage){
       if (websocketdb != null){
           websocketdb.broadcastMessageJson(jsonMessage, null);
       }
   }
   
   public void updateComponentDisplay(Model model, Component mc, AbstractProperty prop) {
       if (websocketdb != null){
           if (applyAppearanceChange){
                ModelVisualizationJson modelJson = getInstance().getModelVisualizationJson(model);
                if (Geometry.safeDownCast(mc)!=null && Mesh.safeDownCast(mc)==null){
                    JSONObject msg = new JSONObject();
                    if (modelJson.createReplaceGeometryMessage(Geometry.safeDownCast(mc), msg))
                        websocketdb.broadcastMessageJson(msg, null);
                }
                JSONObject msg = modelJson.createAppearanceMessage(mc, prop);
                websocketdb.broadcastMessageJson(msg, null);
           }
           else {
               // Add entry to pendingAppearanceChanges
               pendingAppearanceChanges.add(new AppearanceChange(model, mc, prop));
           }
               
       }
   }

   public void applyTimeToViews(double time) {
      Iterator<ModelWindowVTKTopComponent> windowIter = openWindows.iterator();
      while(windowIter.hasNext()){
         ModelWindowVTKTopComponent nextWindow = windowIter.next();
         nextWindow.getCanvas().applyTime(time);
      }
   }

   /**
    * This function is called from a timer thread that runs parallel to the simulation thread
    * Obviously should run as fast as possible. 
    * We could get visModel from animationCallback using getModelVisuals(animationCallback.getModel())
    * but that's another map search.
    */
    public void updateModelDisplay(Model aModel) {
        updateModelDisplay(aModel, null);
    }
    /**
     * Update display of passed in model, optionally recreating display geometry and applying preferences
     * @param aModel
     * @param checkRecreateGeometry : whether to try to sync up geometry with OpenSim objects underneath
     *  This excludes transforms since these are obtained from the system on the fly.
     */
   public void updateModelDisplay(Model aModel, OpenSimObject specificObject) {
      if (isVtkGraphicsAvailable()){
        lockDrawingSurfaces(true);
        if (specificObject!=null)
              mapModelsToVisuals.get(aModel).updateObjectDisplay(specificObject);
        mapModelsToVisuals.get(aModel).updateModelDisplay(aModel);
        lockDrawingSurfaces(false);
        repaintAll();
      }
      if (websocketdb != null && currentJson != null && applyAppearanceChange){
        // Make xforms JSON
        websocketdb.broadcastMessageJson(currentJson.createFrameMessageJson(false), null);
      }
   }
   
   public void updateModelDisplayNoRepaint(Model aModel, boolean colorByState) {
      if (isVtkGraphicsAvailable()){
        lockDrawingSurfaces(true);
        mapModelsToVisuals.get(aModel).updateModelDisplay(aModel);
        lockDrawingSurfaces(false);
      }
      if (websocketdb != null){
        ModelVisualizationJson cJson = mapModelsToJsons.get(aModel);
        websocketdb.broadcastMessageJson(cJson.createFrameMessageJson(colorByState), null);
      }
   }

   /**
    * For a single OpenSimObject, toggle display hide/show
    */
   public void toggleObjectsDisplay(OpenSimObject openSimObject, boolean visible) {
      ObjectGroup group = ObjectGroup.safeDownCast(openSimObject);
      if (group != null) {
         ArrayConstObjPtr members = group.getMembers();
         for (int i = 0; i < members.getSize(); i++) {
            toggleObjectDisplay(members.getitem(i), visible);
         }
      } else {
         toggleObjectDisplay(openSimObject, visible);
      }
      updateAnnotationAnchors(); // in case object had annotations
   }

   public void toggleObjectDisplay(OpenSimObject openSimObject, boolean visible) {
      // use VisibleObject to hold on/off status, and
      // do not repaint the windows or update any geometry because
      // this is now handled by the functions that call toggleObjectDisplay().
      //System.out.println("Toggle object "+openSimObject.getName()+" "+ (visible?"On":"Off"));
       /*
      VisibleObject vo = openSimObject.getDisplayer();
      if (vo != null) {
         DisplayPreference dp = vo.getDisplayPreference();
         if (visible == true)
            vo.setDisplayPreference(DisplayPreference.GouraudShaded); // TODO: assumes gouraud is the default
         else
            vo.setDisplayPreference(DisplayPreference.None);
      }
      else if (openSimObject instanceof Geometry){
          ((Geometry)openSimObject).setDisplayPreference(visible? DisplayPreference.GouraudShaded:
              DisplayPreference.None); // TODO: assumes gouraud is the default
         
      }
      Marker marker = Marker.safeDownCast(openSimObject);
      if (marker != null) {
         SingleModelVisuals vis = getModelVisuals(marker.getBody().getModel());
         //vis.setMarkerVisibility(marker, visible);
         updateAnnotationAnchors(); // in case object had annotations
         return;
      }

      Actuator act = Actuator.safeDownCast(openSimObject);
      if (act != null) {
         SingleModelVisuals vis = getModelVisuals(act.getModel());
         vis.updateActuatorGeometry(act, visible); // call act.updateGeometry() if actuator is becoming visible
         updateAnnotationAnchors(); // in case object had annotations
         return;
      }
      Force f = Force.safeDownCast(openSimObject);
      if (f != null) {
         SingleModelVisuals vis = getModelVisuals(f.getModel());
         vis.updateForceGeometry(f, visible); // call act.updateGeometry() if actuator is becoming visible
         updateAnnotationAnchors(); // in case object had annotations
         return;
      }
      
      if (openSimObject instanceof ObjectGroup){
          ObjectGroup grp = (ObjectGroup) openSimObject;
          ArrayObjPtr members = grp.getMembers();
          for(int i=0;i<members.getSize();i++)
              toggleObjectDisplay(members.getitem(i), visible); // Recur
          return;
      }
      // If the object is a vtkAssembly or vtkActor, sets its visibility that way too.
      final int vtkVisible = visible ? 1 : 0;
      vtkProp3D asm = ViewDB.getInstance().getVtkRepForObject(openSimObject);
      ApplyFunctionToActors(asm, new ActorFunctionApplier() {
         public void apply(vtkActor actor) {
            actor.SetVisibility(vtkVisible);
            actor.SetPickable(vtkVisible);
         }});
       */
    if (websocketdb != null){
       
       ModelVisualizationJson vizJson = getInstance().mapModelsToJsons.get(getCurrentModel());
       websocketdb.broadcastMessageJson(
               vizJson.createToggleObjectVisibilityCommand(openSimObject, visible), null);
    }
   }
   /**
    * Return a flag indicating if an object is displayed or not
    **/
   public static int getDisplayStatus(OpenSimObject openSimObject) {
      int visible = 2;
      ObjectGroup group = ObjectGroup.safeDownCast(openSimObject);
      
      /*
      if (group != null) {
         boolean foundHidden = false;
         boolean foundShown = false;
         ArrayObjPtr members = group.getMembers();
         for (int i = 0; i < members.getSize(); i++) {
            VisibleObject vo = members.getitem(i).getDisplayer();
            if (vo != null) {
               DisplayPreference dp = vo.getDisplayPreference();
               if (dp == DisplayPreference.None)
                  foundHidden = true;
               else
                  foundShown = true;
            }
         }
         // If the group contains hidden members and shown members, return 2 (mixed).
         // If the group contains only hidden members, return 0 (hidden).
         // If the group contains only shown members, return 1 (shown).
         if (foundHidden == true && foundShown == true)
            return 2;
         else if (foundHidden == true)
            return 0;
         else
            return 1;
      } else {
         VisibleObject vo = openSimObject.getDisplayer();
         if (openSimObject.hasProperty("display_preference")){
             int pref = PropertyHelper.getValueInt(openSimObject.getPropertyByName("display_preference"));
             // 0 hidden){
             if (pref!= 0)
                visible = 1;
             else
                 visible = 0;
         }
         else if (vo != null) {
            DisplayPreference dp = vo.getDisplayPreference();
            if (dp != DisplayPreference.None)
               visible = 1;
         }
      }
      */
      return visible;
   }
   /**
    * Change representation of a visible object to the one passed in.
    * The encoding is from VTK and is defined as follows:
    * 0. Points
    * 1. Wireframe
    * 2. Surface
    *
    * Shading is defined similarly but only if representation is Surface(2)
    * 0. Flat
    * 1. Gouraud
    * 2. Phong
    * defined in vtkProperty.h
    */
   public static void setObjectRepresentation(final OpenSimObject object, final int rep, final int newShading) {
      // Set new rep in model so that it's persistent.'
      DecorativeGeometry.Representation newPref = DecorativeGeometry.Representation.DrawSurface;
      if (rep==1)  newPref = DecorativeGeometry.Representation.DrawWireframe;
      /*
      if (object.getDisplayer()!=null){
        final DecorativeGeometry.Representation oldPref = object.getDisplayer().getDisplayPreference();
        final DecorativeGeometry.Representation finalNewPref = newPref;
        object.getDisplayer().setDisplayPreference(newPref);
      }
      else if (object instanceof Geometry)
          ((Geometry)object).setDisplayPreference(newPref);
            */
      vtkProp3D asm = ViewDB.getInstance().getVtkRepForObject(object);
      ApplyFunctionToActors(asm, new ActorFunctionApplier() {
         public void apply(vtkActor actor) {
            actor.GetProperty().SetRepresentation(rep);
            if (rep==2){   // Surface shading
               actor.GetProperty().SetInterpolation(newShading);
            }
         }});
      repaintAll();
   }

   public boolean isPicking() {
      return picking;
   }

   public void setPicking(boolean picking) {
      this.picking = picking;
      if (picking)
         dragging = false;
   }

   public boolean isDragging() {
      return dragging;
   }

   public void setDragging(boolean dragging, OpenSimObject obj) {
      this.dragging = dragging;
      if (dragging) {
         // obj not currently used, but it points to the object that was
         // clicked on to initiate dragging
         picking = false;
      }
   }
   public void dragSelectedObjects(final OpenSimObject clickedObject, final double dragVector[]) {
       dragSelectedObjects(clickedObject, dragVector, true);
   }
   public void dragSelectedObjects(final OpenSimObject clickedObject, final double dragVector[], boolean supportUndo) {
      DragObjectsEvent evnt = new DragObjectsEvent(clickedObject, dragVector);
      //System.out.println("drg vec"+dragVector[0]+" "+dragVector[1]+" "+dragVector[2]);
      // undo is a drag in the opposite direction!
      AbstractUndoableEdit auEdit = new AbstractUndoableEdit(){
           public boolean canUndo() {
               return true;
           }
           public boolean canRedo() {
               return true;
           }
           public void undo() throws CannotUndoException {
               super.undo();
               final double[] negativeDrag=new double[3];
               for(int i=0;i<3;i++) negativeDrag[i]=-dragVector[i];
               dragSelectedObjects(clickedObject, negativeDrag, false);
           }
           public void redo() throws CannotRedoException {
               super.redo();
               dragSelectedObjects(clickedObject, dragVector, true);
           }

           @Override
           public String getRedoPresentationName() {
               return "Redo Drag object(s)";
            }

           @Override
           public String getUndoPresentationName() {
               return "Undo Drag object(s)";
           }
           
       };
      if (supportUndo)
        ExplorerTopComponent.addUndoableEdit(auEdit);
      setChanged();  
      notifyObservers(evnt);
   }

   private void lockDrawingSurfaces(boolean toLock) {
      Iterator<ModelWindowVTKTopComponent> windowIter = openWindows.iterator();
      while(windowIter.hasNext()){
         ModelWindowVTKTopComponent nextWindow = windowIter.next();
         nextWindow.getCanvas().lockDrawingSurface(toLock);
      }
   }

    public OpenSimObject getSelectedGlyphObject(int cellId, vtkActor glyphActor) {
        Iterator<SingleModelVisuals> iter = modelVisuals.iterator();
        while(iter.hasNext()){
            SingleModelVisuals nextModel = iter.next();
            OpenSimvtkGlyphCloud glyph = nextModel.getGlyphObjectForActor(glyphActor);
            if (glyph!=null)
                return glyph.getPickedObject(cellId);
        }
        return null;
    }
    
    /**
     * User Objects manipulation. Delegate to proper model.
     * 
     * Use carfully as you're responsible for any cleanup. Selection management is not supported.
     */
    public void addUserObjectToModel(Model model, vtkActor vtkActor) {
        SingleModelVisuals visModel = mapModelsToVisuals.get(model);
        visModel.addUserObject(vtkActor);
    }

    public void removeUserObjectFromModel(Model model, vtkActor vtkActor) {
       SingleModelVisuals visModel = mapModelsToVisuals.get(model);
       if (visModel != null){
            visModel.removeUserObject(vtkActor);
            //removeAnnotationObjects(model);
        }
    }
    
    public void addUserObjectToCurrentView(vtkActor vtkActor) {
         getCurrentModelWindow().getCanvas().GetRenderer().AddActor(vtkActor);
    }

/*
 * Functions to deal with saved "Settings"
 * processSavedSettings parses the [modelFileWithoutExtension]_settings.xml file
 */
   private String getDefaultSettingsFileName(Model model) {
      String modelFileName = model.getInputFileName(); // TODO: should we use DocumentFileName or InputFileName?
      if(modelFileName==null || modelFileName.length()==0) return null;
      else return modelFileName.substring(0, modelFileName.lastIndexOf("."))+"_settings.xml";
   }
   private void processSavedSettings(Model model) {
      // Read settings file if exist, should have file name =
      // [modelFileWithoutExtension]_settings.xml
      // Should make up a name, use it in emory and change it later per user request if needed.
      if (model.getFilePath().equalsIgnoreCase("")) return;
      ModelSettingsSerializer serializer = new ModelSettingsSerializer(getDefaultSettingsFileName(model), true);
      mapModelsToSettings.put(model, serializer);
   }
   private void updateSettingsSerializer(Model model) {
      ModelSettingsSerializer serializer = mapModelsToSettings.get(model);
      if(serializer != null) serializer.setFilename(getDefaultSettingsFileName(model));
   }
   /**
    * Write ettings to an xml file [model-file]_settings.xml
    */
    public void saveSettings(Model model) {
      mapModelsToSettings.get(model).confirmAndWrite(model);
   }
   public ModelSettingsSerializer getModelSavedSettings(Model model)
   {
      ModelSettingsSerializer exist = mapModelsToSettings.get(model);
      if (exist!=null)
         return exist;

      return null;
   }

    private double[] computeSceneBounds() {
     Iterator<SingleModelVisuals> iter = modelVisuals.iterator();
     double[] bounds = new double[]{-.1, .1, -.1, .1, -.1, .1};
     while(iter.hasNext()){
         SingleModelVisuals nextModel = iter.next();
         bounds = boundsUnion(bounds, nextModel.getBoundsBodiesOnly());
      }
      return bounds;
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        Lookup.Result r = (Lookup.Result) ev.getSource();
        Collection c = r.allInstances();
        if (!c.isEmpty()) {
            Object lup = c.iterator().next();
            int x=0;
        }

    }

    public void setSelectedObject(OpenSimObject openSimObject, Model modelForNode) {
        clearSelectedObjects();
        getModelVisuals(modelForNode).selectObject(openSimObject);
        repaintAll();
    }

    public void updateDisplay(Model model, Component ownerModelComponent) {
        if (isVtkGraphicsAvailable()){
            getModelVisuals(model).upateDisplay(ownerModelComponent);
        }
    }

    /**
     * @return the currentJson
     */
    public ModelVisualizationJson getCurrentJson() {
        return currentJson;
    }

    public void exportAllModelsToJson(VisWebSocket socket) {
        // if no models, then nothing to exportex
        Object[] models = OpenSimDB.getInstance().getAllModels();
        for (int i=0; i< models.length; i++){
            Model model = (Model) models[i];
            ModelVisualizationJson vizJson = null;
            if (debugLevel >1)
                System.out.println("ModelVisualizationJson constructed in exportAllModelsToJson");
            
            vizJson = getJsonForModel(jsondb, model);
            
            exportModelJsonToVisualizer(vizJson, socket);
        }
    }

    private void sync(VisWebSocket visWebSocket) {
        if (debugLevel >1)
            System.out.println("invoke ViewDB.sync");
        ViewDB.getInstance().exportAllModelsToJson(visWebSocket);
    }
    // Method is synchronized to avoid concurrent creation of Json from ViewDB.update and socket
    private synchronized ModelVisualizationJson getJsonForModel(JSONObject jsondb, Model model) {
        ModelVisualizationJson vizJson;
        if (getInstance().mapModelsToJsons.containsKey(model)){
                vizJson = getInstance().mapModelsToJsons.get(model);
                return vizJson;
        }
        vizJson = new ModelVisualizationJson(jsondb, model);
        getInstance().addModelVisuals(model, vizJson);
        mapModelsToJsons.put(model, vizJson);
        return vizJson;
    }
    // method to export GeometryPath to JSON format upon edit
    // operation is one of 0:refresh, 1:add, 2:remove
    public void updatePathDisplay(Model model, GeometryPath currentPath, int operation, int atIndex) {
        if (websocketdb!=null){
            ModelVisualizationJson vizJson = getInstance().mapModelsToJsons.get(model);
            websocketdb.broadcastMessageJson(vizJson.createPathUpdateJson(currentPath, operation, atIndex), null);
        }
    }

    public void objectMoved(Model model, OpenSimObject opensimObj) {
         Vector<OpenSimObject> objs = new Vector<OpenSimObject>(1);
         objs.add(opensimObj);
         ObjectsChangedEvent evnt = new ObjectsChangedEvent(this, model, objs);
         getInstance().setChanged();
         getInstance().notifyObservers(evnt);
    }

    public void removeVisualizerObject(JSONObject object2Remove, String parentUuid) {
        if (websocketdb!=null){
            websocketdb.broadcastMessageJson(currentJson.createRemoveObjectCommand(object2Remove, parentUuid), null);
        }
    }
    public void removeVisualizerObject(OpenSimObject object2Remove, OpenSimObject parentObject) {
        if (websocketdb!=null){
            websocketdb.broadcastMessageJson(currentJson.createRemoveObjectCommand(object2Remove, parentObject), null);
        }
    }

   /**
    * Utility: apply a function to given actor, or to all actors in assembly.
    */
   interface ActorFunctionApplier {
      public void apply(vtkActor actor);
   }
   public static void ApplyFunctionToActors(vtkProp3D asm, ActorFunctionApplier functionApplier)
   {
      if (asm==null) return;
      if (asm instanceof vtkAssembly){
         vtkProp3DCollection parts = ((vtkAssembly)asm).GetParts();
         parts.InitTraversal();
         for (;;) {
            vtkProp3D prop = parts.GetNextProp3D();
            if (prop==null) break;
            else ApplyFunctionToActors(prop, functionApplier); // recur on prop (may be nested assembly?)
         }
      } else if (asm instanceof vtkActor){
         functionApplier.apply((vtkActor)asm);
      }
   }

   public double getNonCurrentModelOpacity() {
         String nonCurrentModelOpacityStr = NbBundle.getMessage(ViewDB.class,"CTL_NonCurrentModelOpacity");
         nonCurrentModelOpacityStr=Preferences.userNodeForPackage(TheApp.class).get("NonCurrentModelOpacity", nonCurrentModelOpacityStr);
         if (nonCurrentModelOpacityStr != null)
            setNonCurrentModelOpacity(Double.valueOf(nonCurrentModelOpacityStr));
         return nonCurrentModelOpacity;
   }

   public void setNonCurrentModelOpacity(double nonCurrentModelOpacity) {
      this.nonCurrentModelOpacity = nonCurrentModelOpacity;
      Preferences.userNodeForPackage(TheApp.class).get("NonCurrentModelOpacity", String.valueOf(nonCurrentModelOpacity));
   }
   /**
    * Show only the passed in model and hide all others.
    */
   public void isolateModel(Model openSimModel) {
      Enumeration<Model> models=mapModelsToJsons.keys();
      while(models.hasMoreElements()){
         Model next = models.nextElement();
         toggleModelDisplay(next, (openSimModel==next));
      }
      repaintAll();
   }
   
   public double[] getDefaultMarkersColor() {
         String markersColorStr = NbBundle.getMessage(ViewDB.class,"CTL_MarkersColorRGB");
         double[] color = new double[]{1.0, 0.6, 0.8};
         markersColorStr =Preferences.userNodeForPackage(TheApp.class).get("Markers Color", markersColorStr);
         if (markersColorStr != null)
            color = Prefs.parseColor(markersColorStr);
         return color;
   }
   
   public double[] getDefaultTextColor() {
         String textColorStr = NbBundle.getMessage(ViewDB.class,"CTL_TextColorRGB");
         double[] color = new double[]{1.0, 1.0, 1.0};
         textColorStr =Preferences.userNodeForPackage(TheApp.class).get("Text Color", textColorStr);
         if (textColorStr != null)
            color = Prefs.parseColor(textColorStr);
         return color;
   }
   
    public double getMuscleDisplayRadius() {
         String muscleDisplayRadiusStr = NbBundle.getMessage(ViewDB.class,"CTL_MuscleRadius");
         muscleDisplayRadiusStr =Preferences.userNodeForPackage(TheApp.class).get("Muscle Display Radius", muscleDisplayRadiusStr);
         if (muscleDisplayRadiusStr != null) {
            try {
               muscleDisplayRadius = numFormat.parse(muscleDisplayRadiusStr).doubleValue();
            } catch (ParseException ex) {
               muscleDisplayRadius = 0.01;
            }
         }
        return muscleDisplayRadius;
    }

    public void setMuscleDisplayRadius(double muscleDisplayRadius) {
        this.muscleDisplayRadius = muscleDisplayRadius;
    }

    public double getMarkerDisplayRadius() {
         String markerDisplayRadiusStr = NbBundle.getMessage(ViewDB.class,"CTL_MarkerRadius");
         markerDisplayRadiusStr =Preferences.userNodeForPackage(TheApp.class).get("Marker Display Radius", markerDisplayRadiusStr);
         if (markerDisplayRadiusStr != null) {
            try {
               markerDisplayRadius = numFormat.parse(markerDisplayRadiusStr).doubleValue();
            } catch (ParseException ex) {
               markerDisplayRadius = 0.01;
            }
         }
        return markerDisplayRadius;
    }

    public void setMarkerDisplayRadius(double markerDisplayRadius) {
        this.markerDisplayRadius = markerDisplayRadius;
    }
    
    public double getExperimentalMarkerDisplayScale() {
         String experimentalMarkerDisplayScaleStr="1.0";
         String saved=Preferences.userNodeForPackage(TheApp.class).get("Experimental Marker Size", experimentalMarkerDisplayScaleStr);
         if (saved != null) 
             return (Double.parseDouble(saved));
         else 
             return 1.0;     
    }
    public Object[] getOpenWindows() {
        return openWindows.toArray();
    }

    public void rebuild(ViewDBDescriptor desc) {
        // Create a new window per view and give it the specified name
        int restoredNumViews = desc.getViewNames().size();
        if (restoredNumViews==0){
            ViewDB.getInstance().removeWindow(getInstance().getCurrentModelWindow());   // Could return here or try to salvage cameras and offsets.
            return;
        }
        for(int viewnum=0; viewnum<restoredNumViews; viewnum++){   // Reuse window we create by default for first model
            final String nm = desc.getViewNames().get(viewnum);
            if (viewnum >0){
                ModelWindowVTKTopComponent win = getInstance().addViewWindow(nm);
                win.applyCameraAttributes(desc.getCameraAttributes().get(viewnum));
            }
            else {  // We'll justrename existing view'
                final ModelWindowVTKTopComponent win = getInstance().getCurrentModelWindow();
                if (win ==null) continue;
                if (desc.getCameraAttributes().size()==0) continue;
                win.applyCameraAttributes(desc.getCameraAttributes().get(viewnum));
                SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                    win.setTabDisplayName(nm);
                }});
            }           
        }
        // If no windows were opened just quit this part since loading of VTK symbols
        // happens only when openning a window
        if (getInstance().getCurrentModelWindow()==null)
            return;
        for(int i=0; i<desc.getOffsetsList().size();i++){
            vtkMatrix4x4 m = new vtkMatrix4x4();
            double[] savedOffset = desc.getOffsetsList().get(i);
            for(int j=0; j<3; j++)  m.SetElement(j, 3, savedOffset[j]);
            if (i > modelVisuals.size()-1) break;   // CNo need to throw exception for that. Would happen if a model didn't have a file'
            ViewDB.getInstance().setModelVisualsTransform(modelVisuals.get(i), m);
        }
        repaintAll();
    }

    // Assume no display transform here
    public void addModelVisuals(Model model, SingleModelVisuals dataVisuals) {
        if (!modelVisuals.contains(dataVisuals))
            modelVisuals.add(dataVisuals);
        mapModelsToVisuals.put(model, dataVisuals);
        sceneAssembly.AddPart(dataVisuals.getModelDisplayAssembly());
    }

    public void addModelVisuals(Model model, ModelVisualizationJson modelJson) {
        if (!mapModelsToJsons.containsKey(model))
            mapModelsToJsons.put(model, modelJson);
    }
    
    public void displayText(String text, int forntSize){
        textActor.SetInput(text);
        vtkTextProperty tprop = textActor.GetTextProperty();
        tprop.SetFontFamilyToArial();
        tprop.SetLineSpacing(1.0);
        tprop.SetFontSize(forntSize);
        //System.out.println("Text Color="+getDefaultTextColor());
        tprop.SetColor(getDefaultTextColor());
        textActor.SetDisplayPosition( 50, 50 );
        Iterator<ModelWindowVTKTopComponent> windowIter = openWindows.iterator();
        while(windowIter.hasNext()){
            ModelWindowVTKTopComponent nextWindow = windowIter.next();
            nextWindow.getCanvas().GetRenderer().AddActor2D(textActor);
        }
    }
    
    public vtkVectorText createText3D(String text, double[] position, double[] color){
        vtkVectorText XText=new vtkVectorText();
        XText.SetText(text);
        vtkPolyDataMapper XTextMapper=new vtkPolyDataMapper();
        XTextMapper.SetInputConnection(XText.GetOutputPort());
        vtkFollower XActor=new vtkFollower();
        XActor.SetMapper(XTextMapper);
        XActor.SetScale(.05, .05, .05);
        XActor.SetPosition(position);
        XActor.GetProperty().SetColor(color);
        vtkCamera lastCamera=new vtkCamera();
        if (getCurrentModelWindow()!=null)    // need a camera for the follower! 
          lastCamera=getCurrentModelWindow().getCanvas().GetRenderer().GetActiveCamera();
        XActor.SetCamera(lastCamera);
        sceneAssembly.AddPart(XActor);
        return XText;
    }

    /*
    // Test function to show text billboarding
    private void createXLabel()
    {
        vtkVectorText XText=new vtkVectorText();
        XText.SetText("X");
        vtkPolyDataMapper XTextMapper=new vtkPolyDataMapper();
        XTextMapper.SetInputConnection(XText.GetOutputPort());
        vtkFollower XActor=new vtkFollower();
        XActor.SetMapper(XTextMapper);
        XActor.SetScale(.5, .5, .5);
        XActor.SetPosition(0.1, .2, .3);
        XActor.GetProperty().SetColor(.1, .2, .3);
        vtkCamera lastCamera=new vtkCamera();
        if (currentModelWindow!=null)    // need a camera for the follower! 
          lastCamera=currentModelWindow.getCanvas().GetRenderer().GetActiveCamera();
        XActor.SetCamera(lastCamera);
        sceneAssembly.AddPart(XActor);
    }*/

    public void setQuery(boolean enabled) {
        query=enabled;
        //System.out.println("Annotation "+(enabled?"On":"Off"));
        // remove captions if disabling
        if (!enabled){
            Iterator<vtkCaptionActor2D> captions=selectedObjectsAnnotations.values().iterator();
            vtkCaptionActor2D nextCaption;
            while (captions.hasNext()){
                nextCaption = captions.next();
                removeAnnotationFromViews(nextCaption);
            }
            selectedObjectsAnnotations.clear();
            getInstance().repaintAll();
        }
    }

    public void removeAnnotationFromViews(final vtkActor2D nextCaption) {
        Iterator<ModelWindowVTKTopComponent> windowIter = openWindows.iterator();
        while(windowIter.hasNext()){
           ModelWindowVTKTopComponent nextWindow = windowIter.next();
           nextWindow.getCanvas().GetRenderer().RemoveActor2D(nextCaption);
        }
    }

    public boolean isQuery() {
        return query;
    }

    public void updateAnnotationAnchors() {
        // For each Anotated object, update anchor point as needed
        Set<Selectable> annotated = selectedObjectsAnnotations.keySet();
        for(Selectable nextSelected:annotated){
            OpenSimObject dObject = nextSelected.getOpenSimObject();
            nextSelected.updateAnchor(selectedObjectsAnnotations.get(nextSelected));
        }
    }
    // Change orientation based on passed in 3 Rotations, used to visualize mocap data
    public void setOrientation(Model model, double[] d) {
        SingleModelVisuals visuals = getModelVisuals(model);
        vtkMatrix4x4 currenTransform=getModelVisualsTransform(visuals);
        // keep only translation
        for(int i=0; i<3; i++)
            for(int j=0; j<3; j++)
                currenTransform.SetElement(i, j, (i==j)?1.0:0.);
        vtkTransform newDisplayTransfrom = new vtkTransform(); //Identity
        newDisplayTransfrom.RotateX(d[0]);
        newDisplayTransfrom.RotateY(d[1]);
        newDisplayTransfrom.RotateZ(d[2]);
        newDisplayTransfrom.Concatenate(currenTransform);
        getInstance().setModelVisualsTransform(visuals, newDisplayTransfrom.GetMatrix());
  
    }

    private void removeAnnotationObjects(Model dModel) {
        Iterator<Selectable> selectedObjsIter = selectedObjectsAnnotations.keySet().iterator();
        while(selectedObjsIter.hasNext()){
            Selectable nextObject=  selectedObjsIter.next();
            
            if (nextObject.getOwnerModel().equals(dModel)){
                vtkCaptionActor2D caption=selectedObjectsAnnotations.get(nextObject);
                getCurrentModelWindow().getCanvas().GetRenderer().RemoveActor2D(caption);
                selectedObjsIter.remove();
            }            
        }
    }

    private vtkCaptionActor2D getAnnotation(Selectable selectedObject) {
        Iterator<Selectable> selectedObjsIter = selectedObjectsAnnotations.keySet().iterator();
        while(selectedObjsIter.hasNext()){
            Selectable nextObject=  selectedObjsIter.next();
            if (nextObject==selectedObject)
                return selectedObjectsAnnotations.get(selectedObject);
        }
        return null;
    }
    /** Debugging */
    public int getDebugLevel() {
        return debugLevel;
    }

    public void setDebugLevel(int debugLevel) {
        this.debugLevel = debugLevel;
    }
    // This handles disablement/enablement
    private void handleObjectsEnabledStateChangeEvent(Object arg) {
        ObjectEnabledStateChangeEvent ev = (ObjectEnabledStateChangeEvent)arg;
        Vector<OpenSimObject> objs = ev.getObjects();
        boolean repaint = false;
        boolean selectedDeleted = false;
        for (int i=0; i<objs.size(); i++) {
           OpenSimObject obj = objs.get(i);
           int j = findObjectInSelectedList(obj);
           if (j >= 0) {
              selectedObjects.remove(j);
              selectedDeleted = true;
           }
           // Forces now
           if (obj instanceof Force){
               Force f = Force.safeDownCast(obj);
               boolean newState = f.get_appliesForce();
               if (f instanceof Muscle) {
                   PathActuator m = PathActuator.safeDownCast(f);
                   if (!newState){
                       //getModelVisuals(m.getModel()).addPathActuatorGeometry(m, true);
                       toggleObjectsDisplay(m, true);
                   } else{  // turning off
                       removeObjectsBelongingToMuscleFromSelection(obj);
                       getModelVisuals(ev.getModel()).removeGeometry(obj);
                   }
                   repaint = true;
               }
               else {   // Other forces
                    if (!newState){ // turn on
                        //f.getDisplayer().setDisplayPreference(DisplayPreference.GouraudShaded);
                        getModelVisuals(ev.getModel()).upateDisplay(f);
                    }
                    else {  // turning off
                        getModelVisuals(ev.getModel()).removeGeometry(obj);
                    }
                    repaint = true;
              }
           }
        }
        if (repaint)
           repaintAll();        
    }

    public void removeObjectAnnotationFromViews(vtkCaptionActor2D caption) {
        // Cycle thru array and remove Caption if warranted
        Iterator<vtkCaptionActor2D> captions=selectedObjectsAnnotations.values().iterator();
        vtkCaptionActor2D nextCaption;
        while (captions.hasNext()){
            nextCaption = captions.next();
            if (nextCaption.equals(caption))
                removeAnnotationFromViews(nextCaption);
        }
    }

    public static boolean isVtkGraphicsAvailable() {
        return false;
    }

    public static void setGraphicsAvailable(boolean aGraphicsAvailable) {
        graphicsAvailable = aGraphicsAvailable;
        
    }
    public static void printBounds(String name, double[] bodyBounds) {
        System.out.print("Bounds for "+name+" are:[");
        for(int i=0; i<6; i++)
            System.out.print(bodyBounds[i]+" ");
        System.out.println("\n============");
    }

    public static void startVisualizationServer() {
        class VizWorker extends SwingWorker {
                public Object construct() {
                JettyMain.main(null);
                    return this;
                }
            }
        VizWorker viz = new VizWorker();
        viz.start();

    }

    public void setCurrentJson() {
        Model cModel = getCurrentModel();
        if (cModel != null && mapModelsToJsons.containsKey(cModel)){
            currentJson = mapModelsToJsons.get(cModel);
            JSONObject msg = currentJson.createSetCurrentModelJson();
            websocketdb.broadcastMessageJson(msg, null);
            if (debugLevel > 1)
                System.out.println(msg.toJSONString());
        }
    }
    public void addVisualizerObject(JSONObject jsonObject, double[] bounds) {
        if (websocketdb!=null){
            websocketdb.broadcastMessageJson(currentJson.createAddObjectCommand(jsonObject, bounds), null);
        }
    }

    public void translateObject(Model model, Marker marker, Vec3 location) {
        if (websocketdb!=null){
            ModelVisualizationJson vis = ViewDB.getInstance().getModelVisualizationJson(model);
            websocketdb.broadcastMessageJson(vis.createTranslateObjectCommand(marker, marker.get_location()), null);
        }
    }
    
    public void applyColorToObjectByUUID(Model model, UUID objectUUID, Vec3 newColor) {
        if (websocketdb!=null){
            ModelVisualizationJson vis = ViewDB.getInstance().getModelVisualizationJson(model);
            websocketdb.broadcastMessageJson(vis.createSetMaterialColorCommand(objectUUID, newColor), null);
        }        
    }
    
    public void scaleGeometryOfObjectByUUID(Model model, UUID objectUUID, double newScale) {
        if (websocketdb!=null){
            ModelVisualizationJson vis = ViewDB.getInstance().getModelVisualizationJson(model);
            websocketdb.broadcastMessageJson(vis.createScaleGeometryCommand(objectUUID, newScale), null);
        }        
        
    }
    // Callback, invoked when a command is received from visualizer
    // this operates only on currentJson
    private void handleJson(JSONObject jsonObject) {
       if (jsonObject.get("type") != null){
           if (((String)jsonObject.get("type")).equalsIgnoreCase("info")){
               if (debugLevel > 1){
                    String msg = "Rendered "+jsonObject.get("numFrames")+" frames in "+jsonObject.get("totalTime")+" ms.";
                    double rendertimeAverage = ((Double) jsonObject.get("totalTime"))/((Long)jsonObject.get("numFrames"));
                    OpenSimLogger.logMessage(msg + "FPS: "+(int)1000/rendertimeAverage+"\n", OpenSimLogger.INFO);
               }
               return;
           }
       }
       Object uuid = jsonObject.get("uuid");
       String uuidString = (String) uuid;
       if (uuidString.length()==0) return;
       final OpenSimObject selectedObject = currentJson.findObjectForUUID(uuidString);
       if (selectedObject == null) return; // Not OpenSim Object, not interested
       JSONMessageHandler.handleJSON(getCurrentModel(), selectedObject, jsonObject);
    }
}
