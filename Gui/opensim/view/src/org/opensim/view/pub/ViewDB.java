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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import java.util.Vector;
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
import org.opensim.swingui.SwingWorker;
import org.opensim.threejs.JSONMessageHandler;
import org.opensim.threejs.JSONUtilities;
import org.opensim.threejs.ModelVisualizationJson;
import org.opensim.utils.ErrorDialog;
import org.opensim.utils.TheApp;
import org.opensim.view.*;
import vtk.FrameActor;
import vtk.vtkActor;
import vtk.vtkActor2D;
import vtk.vtkAssembly;
import vtk.vtkProp3D;
import vtk.vtkProp3DCollection;


/**
 *
 * @author Ayman Habib
 *
 * A Database of Displayed models
 */
public final class ViewDB extends Observable implements Observer, LookupListener {

   // List of models currently available in all views
   private static ArrayList<Boolean> saveStatus = new ArrayList<Boolean>(4);
   // One single vtAssemby for the whole Scene
   private static vtkAssembly sceneAssembly;
   private static WebSocketDB websocketdb;
   private static JSONObject jsondb;
   private SelectedObject selectInVisualizer = null;
   private static int frameRate = 30;
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
                websocketdb.broadcastMessageJson(currentJson.createFrameMessageJson(false, true), null);
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

    public void updateModelOffsets() {
        // This will send message to visualizer asking for offsets,
        // JSONMessageHandler will set this on ModelVisualizationJson(s) accordingly
        if (websocketdb!=null){
             JSONObject msg = new JSONObject();
             msg.put("Op", "getOffsets");
             websocketdb.broadcastMessageJson(msg, null);
        }

    }

    public int getFrameTime() {
        String saved = TheApp.getCurrentVersionPreferences().get("Internal.FrameRate", String.valueOf(frameRate));
        if (saved!= null)
            frameRate = Integer.parseInt(saved);
        TheApp.getCurrentVersionPreferences().put("Internal.FrameRate", String.valueOf(frameRate));
        return frameRate; // 30 FPS default
    }
    /**
     * Toggle display of user editable pathpoints to enable visual editing/dragging
     * @param msl 
     */
    public void togglePathPointDisplay(Muscle msl, boolean newState) {
        if (websocketdb!=null){
            JSONObject msg = new JSONObject();
             msg.put("Op", "TogglePathPoints");
             ModelVisualizationJson modelVis = getInstance().getModelVisualizationJson(msl.getModel());
             ArrayList<UUID> uuidList = modelVis.findUUIDForObject(msl);
             msg.put("uuid", uuidList.get(0).toString());
             msg.put("newState", newState);
             websocketdb.broadcastMessageJson(msg, null);
             modelVis.setPathPointDisplayStatus(msl.getGeometryPath(), newState);
        }
    }

    public void removePathDisplay(GeometryPath currentPath) {
        if (websocketdb != null) {
            togglePathPointDisplay(Muscle.safeDownCast(currentPath.getOwner()), false);
            ModelVisualizationJson modelVis = getInstance().getModelVisualizationJson(currentPath.getModel());
            ArrayList<UUID> uuids2Remove = modelVis.removePathVisualization(currentPath);
            // Create MuliCmd
            JSONObject topMsg = new JSONObject();
            JSONObject msgMulti = new JSONObject();
            msgMulti.put("type", "MultiCmdsCommand");
            topMsg.put("Op", "execute");
            topMsg.put("command", msgMulti);
            JSONArray commands = new JSONArray();
            for (UUID uuid:uuids2Remove){
                commands.add(modelVis.createRemoveObjectByUuidCommand(uuid, 
                        UUID.fromString((String) modelVis.getModelGroundJson().get("uuid"))).get("command"));
            }
            msgMulti.put("cmds", commands);
            websocketdb.broadcastMessageJson(topMsg, null);
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
                if (Muscle.safeDownCast(appChange.mc)!= null){
                    // create commands to handle PathPoints and add here as well
                    modelJson.propagateGeometryPathCommandsToPathPoints(Muscle.safeDownCast(appChange.mc), appChange.prop, commands);
                }
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
   private Hashtable<Model, ModelVisualizationJson> mapModelsToJsons =
           new Hashtable<Model, ModelVisualizationJson>();

   private Hashtable<Model, ModelSettingsSerializer> mapModelsToSettings =
           new Hashtable<Model, ModelSettingsSerializer>();
   private Hashtable<Model, Double> modelOpacities = new Hashtable<Model, Double>();
   
   static ViewDB instance=null;
  
   // Flag indicating whether new models are open in a new window or in the same window
   static boolean openModelInNewWindow=true;
   
   static boolean useImmediateModeRendering = false; // Use Render instead of paint
   private ArrayList<SelectedObject> selectedObjects = new ArrayList<SelectedObject>(0);
   private Hashtable<ModelVisualizationJson, Path> modelVisToJsonFilesMap = new Hashtable<ModelVisualizationJson, Path>();
   
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
  
   /**
    * update Method is called whenever a model is added, removed and/or moved in the GUI
    * Observable should be of type OpenSimDB.
    */
   public void update(Observable o, Object arg) {
      if (arg instanceof JSONObject){
          handleJson((JSONObject) arg);
          return;
      }
      if (o instanceof VisWebSocket){
          // Sync. socket with current ViweDB
          if (arg != null && arg instanceof JSONObject)
              return; // info message. no need to sync again
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
               repaintAll();
               //rc = newModelVisual.getModelDisplayAssembly().GetReferenceCount();
            } else if (ev.getOperation()==ModelEvent.Operation.Close){
               Model dModel = ev.getModel();
               mapModelsToSettings.remove(dModel);

               // Remove model-associated objects from selection list!
               removeObjectsBelongingToModelFromSelection(dModel);
               //rc = visModel.getModelDisplayAssembly().GetReferenceCount();
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
   /**
    * Helper function to implement model hide/show.
    *
    */
   public void toggleModelDisplay(Model model, boolean onOff) {
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
   }

   public static void renderAll() {
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
   }
   
   //-----------------------------------------------------------------------------
   private static void applyOpacity( final double newOpacity, final vtkProp3D asm ) {
      ViewDB.ApplyFunctionToActors( asm, new ActorFunctionApplier() {
         public void apply(vtkActor actor) { actor.GetProperty().SetOpacity(newOpacity); }});
      
   }
   /**
    * Remove items from selection list which belong to the given model
    */
   private void removeObjectsBelongingToModelFromSelection(Model model)
   {
      boolean modified = false;
      for(int i=selectedObjects.size()-1; i>=0; i--) {
         Model ownerModel = selectedObjects.get(i).getOwnerModel();
         if (ownerModel==null){ // Ophan'd component, just remove and continue
             selectedObjects.remove(i);
             continue;
         }
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
      

      if (websocketdb != null){
          Model model = selectedObject.getOwnerModel();
          ModelVisualizationJson modelJson = getModelVisualizationJson(model);
          ArrayList<UUID> uuidList = modelJson.findUUIDForObject(selectedObject.getOpenSimObject());
          if (uuidList==null) return;
          if (highlight){
              if (selectInVisualizer != null && 
                      !selectInVisualizer.equals(selectedObject.getOpenSimObject())){
                    websocketdb.broadcastMessageJson(modelJson.createSelectionJson(selectedObject.getOpenSimObject()), null);
              }
              else if (selectInVisualizer == null){
                    websocketdb.broadcastMessageJson(modelJson.createSelectionJson(selectedObject.getOpenSimObject()), null);                  
              }
              selectInVisualizer = selectedObject;
          }
          else {
              if (selectInVisualizer!=null){
                websocketdb.broadcastMessageJson(modelJson.createDeselectionJson(), null);
                selectInVisualizer = null;
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
         selectedObjects.clear();
         selectInVisualizer = null;
         //selectedObjects.add(selectedObject);
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

    public void setModelOffset(ModelVisualizationJson modelJson, Vec3 offsetVec3) {        
        if (websocketdb!=null){
            JSONObject guiJson = new JSONObject();
            guiJson.put("Op", "execute");
            JSONObject commandJson =  modelJson.createSetPositionCommand(modelJson.getModelUUID(), offsetVec3);
            guiJson.put("command", commandJson);
            websocketdb.broadcastMessageJson(guiJson, null);
        }
    }
    public Vec3 getModelOffset(ModelVisualizationJson modelJson) {
        Vec3 offset = modelJson.getTransformWRTScene().p();
        return offset;
    }
   /**
    * Get a box around the whole scene. Used to fill an intial guess of the bounds for the model display
    */
   public double[] getSceneBounds() {
      double[] sceneBounds = new double[6];
       int numModels = mapModelsToJsons.size();
       for (int i = 0; i < 3; i++) {
           sceneBounds[i * 2] = -2 * numModels;
           sceneBounds[i * 2 + 1] = 2 * numModels;
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

   /**
    * createScene is invoked once to create the assembly representing the scene
    * that models attach to. If all windows are closed then this function will not be called again.
    */
   private void createScene() {
      sceneAssembly = new vtkAssembly();
   }
   
   
   /**
    * Search the list of displayed models for the passed in model and if found return
    * its visuals, otherwise return null
    */
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
                sendGeometryUpdateMessageIfNeeded(mc, modelJson);
                JSONObject msg = modelJson.createAppearanceMessage(mc, prop);
                websocketdb.broadcastMessageJson(msg, null);
           }
           else {
               // Add entry to pendingAppearanceChanges
               pendingAppearanceChanges.add(new AppearanceChange(model, mc, prop));
           }
               
       }
   }

    private void sendGeometryUpdateMessageIfNeeded(Component mc, ModelVisualizationJson modelJson) {
        if (componentHasAnalyticGeometry(mc)){
            JSONObject msg = new JSONObject();
            if (modelJson.createReplaceGeometryMessage(mc, msg))
                websocketdb.broadcastMessageJson(msg, null);
        }
    }

    private static boolean componentHasAnalyticGeometry(Component mc) {
        return (Geometry.safeDownCast(mc)!=null && Mesh.safeDownCast(mc)==null)||
                ContactGeometry.safeDownCast(mc)!=null ||
                WrapObject.safeDownCast(mc)!=null;
    }

   public void applyTimeToViews(double time) {
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
      if (websocketdb != null && currentJson != null && applyAppearanceChange){
        // Make xforms JSON
        websocketdb.broadcastMessageJson(currentJson.createFrameMessageJson(false, true), null);
      }
   }
   
   public void updateModelDisplayNoRepaint(Model aModel, boolean colorByState, boolean refresh) {
      if (websocketdb != null){
        ModelVisualizationJson cJson = mapModelsToJsons.get(aModel);
        websocketdb.broadcastMessageJson(cJson.createFrameMessageJson(colorByState, refresh), null);
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

    @Override
    public void resultChanged(LookupEvent ev) {
        Lookup.Result r = (Lookup.Result) ev.getSource();
        Collection c = r.allInstances();
        if (!c.isEmpty()) {
            Object lup = c.iterator().next();
            int x=0;
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
         return nonCurrentModelOpacity;
   }

   public void setNonCurrentModelOpacity(double nonCurrentModelOpacity) {
      this.nonCurrentModelOpacity = nonCurrentModelOpacity;
      TheApp.getCurrentVersionPreferences().get("Internal.NonCurrentModelOpacity", String.valueOf(nonCurrentModelOpacity));
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
         return color;
   }
   
   public double[] getDefaultTextColor() {
         String textColorStr = NbBundle.getMessage(ViewDB.class,"CTL_TextColorRGB");
         double[] color = new double[]{1.0, 1.0, 1.0};
         return color;
   }
   
    public double getMuscleDisplayRadius() {
        return muscleDisplayRadius;
    }

    public void setMuscleDisplayRadius(double muscleDisplayRadius) {
        this.muscleDisplayRadius = muscleDisplayRadius;
    }

    public double getMarkerDisplayRadius() {
        return markerDisplayRadius;
    }

    public void setMarkerDisplayRadius(double markerDisplayRadius) {
        this.markerDisplayRadius = markerDisplayRadius;
    }
    
    public double getExperimentalMarkerDisplayScale() {
         String experimentalMarkerDisplayScaleStr="1.0";
         String saved=TheApp.getCurrentVersionPreferences().get("Experimental Marker Radius (mm)", experimentalMarkerDisplayScaleStr);
         if (saved != null) 
             return (Double.parseDouble(saved));
         else 
             return 1.0;     
    }


    public void rebuild(ViewDBDescriptor desc) {
        // Create a new window per view and give it the specified name
    }

    public void addModelVisuals(Model model, ModelVisualizationJson modelJson) {
        if (!mapModelsToJsons.containsKey(model))
            mapModelsToJsons.put(model, modelJson);
    }
    // Change orientation based on passed in 3 Rotations, used to visualize mocap data
    public void setOrientation(Model model, Vec3 rotVec3) {
        if (websocketdb!=null){
            ModelVisualizationJson modelJson = getModelVisualizationJson(model);
            JSONObject guiJson = new JSONObject();
            guiJson.put("Op", "execute");
            JSONObject commandJson =  modelJson.createSetRotationCommand(modelJson.getModelUUID(), rotVec3);
            guiJson.put("command", commandJson);
            websocketdb.broadcastMessageJson(guiJson, null);
        }          
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
                       //getModelVisuals(ev.getModel()).removeGeometry(obj);
                   }
                   repaint = true;
               }
               else {   // Other forces
                    repaint = true;
              }
           }
        }
        if (repaint)
           repaintAll();        
    }


    public static boolean isVtkGraphicsAvailable() {
        return false;
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
            // wait for model to be ready 
            boolean wait = true;
            while (websocketdb.isPending(currentJson.getModelUUID()) && wait){
                try {
                    // Because of delays in communication, the visualizer may take a bit of time to initialize, acknowledge model open
                    // but we can't wait indefinitely as something fatal may happen.
                    // Adding objects to Model that hasn't been initialized causes problems downstream
                    // This scenario happens exclusively when previewing data so the time spent reading /parsing dominates anyway
                    // TODO: explore more robust mechanism to regulate communication with low overhead, -Ayman 07/18
                    Thread.sleep(500); 
                    wait = false;
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                    wait = false;
                }
            }
            websocketdb.broadcastMessageJson(currentJson.createAddObjectCommand(jsonObject, bounds), null);
        }
    }

    public void setObjectTranslationInParent(Model model, Marker marker, Vec3 location) {
        if (websocketdb!=null){
            ModelVisualizationJson vis = ViewDB.getInstance().getModelVisualizationJson(model);
            websocketdb.broadcastMessageJson(vis.createTranslateObjectCommand(marker, marker.get_location()), null);
        }
    }
    public void setObjectTranslationInParentByUuid(UUID objectUuid, Vec3 newlocation) {
        if (websocketdb!=null){
            websocketdb.broadcastMessageJson(
                    ModelVisualizationJson.createTranslateObjectByUuidCommand(objectUuid, newlocation), null);
        }
    }
    
    public void applyColorToObjectByUUID(Model model, UUID objectUUID, Vec3 newColor) {
        if (websocketdb!=null){
            ModelVisualizationJson vis = ViewDB.getInstance().getModelVisualizationJson(model);
            websocketdb.broadcastMessageJson(vis.createSetMaterialColorCommand(objectUUID, newColor), null);
        }        
    }
    
    public void resizeGeometryOfObjectByUUID(Model model, UUID objectUUID, double newSize) {
        if (websocketdb!=null){
            ModelVisualizationJson vis = ViewDB.getInstance().getModelVisualizationJson(model);
            websocketdb.broadcastMessageJson(vis.createResizeGeometryCommand(objectUUID, newSize), null);
        }        
        
    }
    // find OpenSimObject corresponding to passed in UUID or null if not found
    public OpenSimObject getObjectFromUUID(UUID objUuid) {
        Collection<ModelVisualizationJson> values = getInstance().mapModelsToJsons.values();
       Iterator<ModelVisualizationJson> iterator = values.iterator();
        while(iterator.hasNext()){
            OpenSimObject obj = iterator.next().findObjectForUUID(objUuid.toString());
            if (obj !=null) return obj;
        }
        return null;
    }
       // Callback, invoked when a command is received from visualizer
    // this operates only on currentJson
    private void handleJson(JSONObject jsonObject) {
        String msgType = (String)jsonObject.get("type");
        if (msgType != null) {
            if (msgType.equalsIgnoreCase("info")) {
                if (jsonObject.get("renderTime")!=null){
                    double frameRenderTimeInMillis = JSONMessageHandler.convertObjectFromJsonToDouble(jsonObject.get("renderTime"));
                    //System.out.println("renderTime"+frameRenderTimeInMillis);
                    int frameRate = (int) (frameRenderTimeInMillis*1.5);
                    if (frameRate > 30)
                        TheApp.getCurrentVersionPreferences().put("Internal.FrameRate", String.valueOf(frameRate));
                    return;
                }
                if (debugLevel > 1) {
                    String msg = "Rendered " + jsonObject.get("numFrames") + " frames in " + jsonObject.get("totalTime") + " ms.";
                    double rendertimeAverage = ((Double) jsonObject.get("totalTime")) / ((Long) jsonObject.get("numFrames"));
                    OpenSimLogger.logMessage(msg + "FPS: " + (int) 1000 / rendertimeAverage + "\n", OpenSimLogger.INFO);
                }
                return;
            }
            if (msgType.equalsIgnoreCase("transforms")) {
                String objType = (String) jsonObject.get("ObjectType");
                if (objType!= null && objType.equalsIgnoreCase("Model")) {
                    JSONArray uuids = (JSONArray) jsonObject.get("uuids");
                    JSONArray positions = (JSONArray) jsonObject.get("positions");
                    Enumeration<ModelVisualizationJson> modelJsons = mapModelsToJsons.elements();
                    while (modelJsons.hasMoreElements()) {
                        // Find model corresponding to uuid
                        ModelVisualizationJson nextModelJson = modelJsons.nextElement();
                        String uuidDtring = nextModelJson.getModelUUID().toString();
                        int index = uuids.indexOf(uuidDtring);
                        // Be defensive in case model hasn't been found
                        if (index >=0){
                            JSONObject offsetObj = (JSONObject) positions.get(index);
                            // The following call returns Vec3 in model units, no need to convert
                            Vec3 offsetAsVec3 = JSONMessageHandler.convertJsonXYZToVec3(offsetObj);
                            for (int vindex = 0; vindex < 3; vindex++) {
                                nextModelJson.getTransformWRTScene().p().set(vindex,
                                        offsetAsVec3.get(vindex));
                            }
                        }
                    }

                }

                return;
            }
            if (msgType.equalsIgnoreCase("acknowledge")){
                WebSocketDB.getInstance().finishPendingMessage((String) jsonObject.get("uuid"));
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
    private Vec3 extractVec3FromJsonXYZ(JSONObject offsetObj) {
        Object xString = offsetObj.get("x");
        double xValue = JSONMessageHandler.convertObjectFromJsonToDouble(xString);
        Object yString = offsetObj.get("y");
        double yValue = JSONMessageHandler.convertObjectFromJsonToDouble(yString);
        Object zString = offsetObj.get("z");
        double zValue = JSONMessageHandler.convertObjectFromJsonToDouble(zString);
        Vec3 offsetAsVec3 = new Vec3(xValue, yValue, zValue);
        return offsetAsVec3;
    }
}
