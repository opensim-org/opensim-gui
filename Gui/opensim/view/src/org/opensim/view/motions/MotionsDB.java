/*
 *
 * MotionsDB
 * Author(s): Ayman Habib
 * Copyright (c)  2005-2006, Stanford University, Ayman Habib
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
package org.opensim.view.motions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.JButton;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.Node;
import org.opensim.modeling.Model;
import org.opensim.modeling.ArrayStr;
import org.opensim.view.experimentaldata.ModelForExperimentalData;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.Storage;
import org.opensim.utils.FileUtils;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.ModelEvent;
import org.opensim.view.ObjectsRenamedEvent;
import org.opensim.view.actions.ConfirmSaveDiscardJPanel;
import org.opensim.view.experimentaldata.AnnotatedMotion;
import org.opensim.view.nodes.OneModelNode;
import org.opensim.view.pub.*;

/**
 *
 * @author Ayman
 */
public class MotionsDB extends Observable // Observed by other entities in motionviewer
        implements Observer {

    public enum CloseMotionDefaultAction {
        SAVE,
        DISCARD,
        PROMPT
    }
    static private CloseMotionDefaultAction currentCloseMotionDefaultAction = CloseMotionDefaultAction.PROMPT;

    void addMotionDisplayer(Storage simmMotionData, MotionDisplayer displayer) {
        mapMotions2Displayers.put(simmMotionData, displayer);
    }
    MotionDisplayer getDisplayerForMotion(Storage mot){
        return mapMotions2Displayers.get(mot);
    }
    void removeMotionDisplayer(Storage mot) {
        mapMotions2Displayers.remove(mot);
    }

 // Observer OpenSimDB to sync. up when models are deleted
   
   public static class ModelMotionPair {
      public Model model;
      public Storage motion;
      public ModelMotionPair(Model model, Storage motion) { this.model = model; this.motion = motion; }
   }

   static MotionsDB instance;

   // Map model to an ArrayList of Motions linked with it
   Hashtable<Model, ArrayList<Storage>> mapModels2Motions =
           new Hashtable<Model, ArrayList<Storage>>(4);
   
   Hashtable<Storage, MotionDisplayer> mapMotions2Displayers =
           new Hashtable<Storage, MotionDisplayer>(4);
   // Remember for each storage object, the file name it came from. Useful for saving/restoring application state
   // Caveats: many motions are created on the fly and have no files associated with them
   //        : If a file name is reused this info may not be current
   Hashtable<Storage, String> storageFilenames = new Hashtable<Storage, String>(4);
   
   // Map motion to a BitSet for storing dirty bit, etc.
   Hashtable<Storage, BitSet> mapMotion2BitSet = new Hashtable<Storage, BitSet>(1);
   private final static int numBits = 4;
   private final static int modifiedBit = 0;

   // More than one motion may be current if synchronizing motion
   Vector<ModelMotionPair> currentMotions = new Vector<ModelMotionPair>();
      
   /** Creates a new instance of MotionsDB */
   private MotionsDB() {
       OpenSimDB.getInstance().addObserver(this);
       addObserver(this); // Listen to our own events (used to update the explorer)
   }
   
   public static synchronized MotionsDB getInstance() {
      if (instance == null) {
         instance = new MotionsDB();
         
      }
      return instance;
   }

   /**
    * Load a motion file, and associate it with a model.
    * We try to associate the motion with current model first if something doesn't look
    * right (e.g. no coordinates or markers match, warn and ask user either to select another model
    * or abort loading.
    * A side effect of changing the model associated with a loaded motion is that the new model becomes
    * current.
    */
   public void loadMotionFile(String fileName, boolean primary) {
      Storage storage = null;
      try {
         storage = new Storage(fileName);
      } catch (IOException ex) {
         ex.printStackTrace();
         DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Could not read motion file "+fileName));
         return;
      }
      //saveStorageFileName(storage, fileName);
      loadMotionStorage(storage, primary, fileName);
   }
   
   public void loadMotionStorage(Storage newMotion, boolean primary, String filePath)
   {
      if (primary){
          boolean associated = false;
          while(!associated){
             Model modelForMotion = OpenSimDB.getInstance().selectModel(OpenSimDB.getInstance().getCurrentModel());
             if (modelForMotion == null){ // user cancelled
                break;
             }
             // user selected a model, try to associate it
             if(MotionsDB.getInstance().motionAssociationPossible(modelForMotion, newMotion)){
                addMotion(modelForMotion, newMotion, null);
                 StatusDisplayer.getDefault().setStatusText("Associated motion: "+newMotion.getName()+" to model: "+modelForMotion.getName());
                associated = true;
             } else { // Show error that motion couldn't be associated and repeat'
                DialogDisplayer.getDefault().notify( 
                        new NotifyDescriptor.Message("Could not associate motion to current model."));
                break;
             }
          }
      }
      else{ // There's already a model associated with current motion, use it
          Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
          if (selected.length==1 && selected[0] instanceof OneMotionNode){
              OneMotionNode parentMotionNode = (OneMotionNode) selected[0];
              Model modelForMotion = parentMotionNode.getModelForNode();
              if (newMotion instanceof AnnotatedMotion)
                addMotion(modelForMotion, newMotion, parentMotionNode.getMotion());
              else {
                AnnotatedMotion aMot = new AnnotatedMotion(newMotion);
                addMotion(modelForMotion, aMot, parentMotionNode.getMotion());
                MotionsDB.getInstance().saveStorageFileName(aMot, filePath);
              }
           }
      }
   }
   
   /**
     * Criteria for associating motionto a model:
     * At least one genccord or marker (_tx?) in motion file/Storage
     */
   boolean motionAssociationPossible(Model modelForMotion, Storage newMotion) {
      
      ArrayStr coordinateNames = new ArrayStr();
      modelForMotion.getCoordinateSet().getNames(coordinateNames);
      int numCoordinates = coordinateNames.getSize();
      int numUsedColumns = 0;    // Keep track of how many columns correspond to Coords or Markers
      for(int i=0; i<numCoordinates; i++){
         if (newMotion.getStateIndex(coordinateNames.getitem(i))!=-1)
            numUsedColumns++;
      }
      ArrayStr markerNames = new ArrayStr();
      modelForMotion.getMarkerSet().getNames(markerNames);
      for(int i=0; i<markerNames.getSize(); i++){
         if ((newMotion.getStateIndex(markerNames.getitem(i)+"_tx")!=-1) ||
                 (newMotion.getStateIndex(markerNames.getitem(i)+"_Tx")!=-1) ||
                 (newMotion.getStateIndex(markerNames.getitem(i)+"_TX")!=-1))
            numUsedColumns++;
      }
      
      return (numUsedColumns>=1);  // At least one column makes sense
   }

   public void addMotion(Model model, Storage motion, Storage parentMotion) {
      if (! (model instanceof ModelForExperimentalData)){
          boolean convertAngles = motion.isInDegrees();
          if(convertAngles) model.getSimbodyEngine().convertDegreesToRadians(motion);
      }
      // Add to mapModels2Motion
      ArrayList<Storage> motions = mapModels2Motions.get(model);
      if(motions==null) { // First motion for model
         motions = new ArrayList<Storage>(4);
         mapModels2Motions.put(model, motions);
      }

      motions.add(motion);
      
      // Add to mapMotion2BitSet
      BitSet motionBits = new BitSet(numBits);
      mapMotion2BitSet.put(motion, motionBits);

      MotionEvent evt = new MotionEvent(this, model, motion, 
              (parentMotion==null)?MotionEvent.Operation.Open:MotionEvent.Operation.Assoc);
      setChanged();
      notifyObservers(evt);

      if (parentMotion==null) setCurrent(model, motion); // Also make it current (a separate event is sent out)
   }

   public void setMotionModified(Storage motion, boolean state) {
      BitSet motionBits = mapMotion2BitSet.get(motion);
      if (motionBits != null)
         motionBits.set(modifiedBit, state);
   }

   public boolean getMotionModified(Storage motion) {
      BitSet motionBits = mapMotion2BitSet.get(motion);
      if (motionBits != null)
         return motionBits.get(modifiedBit);
      return false;
   }


   public void clearCurrent() {
      clearCurrentMotions();
      MotionEvent evt = new MotionEvent(this, MotionEvent.Operation.CurrentMotionsChanged);
      setChanged();
      notifyObservers(evt);
   }

   public void setCurrent(Model model, Storage motion) {
      setCurrentMotion(new ModelMotionPair(model, motion));
      MotionEvent evt = new MotionEvent(this, MotionEvent.Operation.CurrentMotionsChanged);
      evt.setModel(model);
      setChanged();
      notifyObservers(evt);
   }

   public void setCurrent(Vector<ModelMotionPair> motions) {
      setCurrentMotions(motions);
      MotionEvent evt = new MotionEvent(this, MotionEvent.Operation.CurrentMotionsChanged);
      setChanged();
      notifyObservers(evt);
   }

   public void closeMotion(Model model, Storage simmMotionData, boolean allowCancel) {
      closeMotion(model, simmMotionData, true, allowCancel);
   }

   public void closeMotion(Model model, Storage simmMotionData, boolean confirmCloseIfModified, boolean allowCancel) {
      // Prompt user to confirm the close and possibly save the motion if it has been modified.
      if (confirmCloseIfModified && getMotionModified(simmMotionData)) {
         if (!confirmCloseMotion(model, simmMotionData, allowCancel))
            return;
      }

      ArrayList<Storage> motions = mapModels2Motions.get(model);
      if(motions!=null) { // Shouldn't be null, but just in case...
         motions.remove(simmMotionData);
      }

      // Remove from mapMotion2BitSet
      mapMotion2BitSet.remove(simmMotionData);

      boolean removed = removeFromCurrentMotions(new ModelMotionPair(model, simmMotionData));
      Node motionNode = getMotionNode(model, simmMotionData);
      Node parentOrTopMotionsNode = null;
      if(motionNode!=null) {
           parentOrTopMotionsNode = motionNode.getParentNode();
           // get displayer for parnet motion and remove evnt.getMotion() from associated motions
           if (motionNode instanceof OneAssociatedMotionNode){
               Storage parentMotion = ((OneMotionNode) parentOrTopMotionsNode).getMotion();
               MotionDisplayer currentDisplayer = getDisplayerForMotion(simmMotionData);
               currentDisplayer.cleanupDisplay();
               getDisplayerForMotion(parentMotion).getAssociatedMotions().remove(currentDisplayer);
           }
      }
      MotionEvent evt = new MotionEvent(this, model, simmMotionData, MotionEvent.Operation.Close);
      setChanged();
      notifyObservers(evt);
      removeMotionDisplayer(simmMotionData);

      if(removed) {
         evt = new MotionEvent(this, MotionEvent.Operation.CurrentMotionsChanged);
         setChanged();
         notifyObservers(evt);
      }
   }

   private boolean confirmCloseMotion(Model model, Storage simmMotionData, boolean allowCancel) {
      int dialogType = NotifyDescriptor.YES_NO_OPTION;
      if (allowCancel)
         dialogType = NotifyDescriptor.YES_NO_CANCEL_OPTION;
      NotifyDescriptor dlg = new NotifyDescriptor.Confirmation("Do you want to save the changes to motion '" + simmMotionData.getName() + "'?",
              "Save Modified Motion?", dialogType);
      Object userSelection = DialogDisplayer.getDefault().notify(dlg);
      if (((Integer)userSelection).intValue() == ((Integer)NotifyDescriptor.OK_OPTION).intValue()) {
         String fileName = FileUtils.getInstance().browseForFilenameToSave(FileUtils.MotionFileFilter, true, "");
         if (fileName != null) {
            MotionsSaveAsAction.saveMotion((model), simmMotionData, fileName);
            saveStorageFileName(simmMotionData, fileName);
            return true;
         } else {
            // The user cancelled out of saving the file, which we interpret as wanting to cancel
            // the closing of the motion. But if allowCancel == false then we have to return true
            // so the close is not cancelled.
            return !allowCancel;
         }
      } else if (((Integer)userSelection).intValue() == ((Integer)NotifyDescriptor.NO_OPTION).intValue()) {
         return true;
      }
      return false;
   }

   public void update(Observable o, Object arg) {
      if (o instanceof OpenSimDB && arg instanceof ModelEvent){
         ModelEvent evnt = (ModelEvent) arg;
         if (evnt.getOperation()==ModelEvent.Operation.Close){

            Model model = evnt.getModel();
            closeMotionsForModel(model);
         }
         else if (evnt.getOperation()==ModelEvent.Operation.SetCurrent){
              if (evnt.getModel() instanceof ModelForExperimentalData){
                  // Make corresponding motion current as well
                  ArrayList<Storage> motions = getModelMotions(evnt.getModel());
                  assert(motions.size()==1);
                  setCurrent(evnt.getModel(), motions.get(0));
              }
         }
      } else if (o instanceof MotionsDB && arg instanceof MotionEvent) {
         final MotionEvent evnt = (MotionEvent) arg;
         
               // Update tree display on event thread
               switch(evnt.getOperation()) {
                  case Open:
                  {
                     Node modelNode = ExplorerTopComponent.findInstance().getModelNode(evnt.getModel());
                     if (modelNode==null) return;
                     Node newMotionNode; 
                     if (evnt.getModel() instanceof ModelForExperimentalData){
                         newMotionNode= new OneMotionDataNode(evnt.getMotion());
                         modelNode.getChildren().add(new Node[]{newMotionNode});
                     }
                     else {
                         newMotionNode= new OneMotionNode(evnt.getMotion());
                         MotionsNode motionsNode = (MotionsNode) modelNode.getChildren().findChild("Motions");
                         if(motionsNode==null) {
                             motionsNode = new MotionsNode();
                             getInstance().addObserver(motionsNode);
                             modelNode.getChildren().add(new Node[]{motionsNode});
                         }
                         motionsNode.getChildren().add(new Node[]{newMotionNode});
                         Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
                         ExplorerTopComponent.findInstance().getExplorerManager().setExploredContext(newMotionNode, selected);
                     }
                     break;
                  }
                  case Close:
                  {
                     try { // destroy() may throw IOException
                        Node motionNode = getMotionNode(evnt.getModel(), evnt.getMotion());
                        Node parentOrTopMotionsNode = null;
                        if(motionNode!=null) {
                           parentOrTopMotionsNode = motionNode.getParentNode();
                           // get displayer for parnet motion and remove evnt.getMotion() from associated motions
                           if (motionNode instanceof OneAssociatedMotionNode){
                               Storage parentMotion = ((OneMotionNode) parentOrTopMotionsNode).getMotion();
                               MotionDisplayer currentDisplayer = getDisplayerForMotion(evnt.getMotion());
                               getDisplayerForMotion(parentMotion).getAssociatedMotions().remove(currentDisplayer);
                           }
                            motionNode.destroy();
                       }
                        // Delete the "Motions" container node if no more motions left
                        if(parentOrTopMotionsNode!=null && parentOrTopMotionsNode.getChildren().getNodesCount()==0 && parentOrTopMotionsNode.getName().equals("Motions"))
                           parentOrTopMotionsNode.destroy();
                     } catch (IOException ex) {
                        ex.printStackTrace();
                     }
                     break;
                  }
                  case Assoc:
                  {
                      Node[] motionNode = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
                      if (motionNode.length!=1) return;
                      Node newMotionNode = new OneAssociatedMotionNode((AnnotatedMotion)evnt.getMotion());
                      motionNode[0].getChildren().add(new Node[]{newMotionNode});
                      break;
                  }
               }
            
         
      }
   }

    public void reportTimeChange(double newTime) {
        MotionTimeChangeEvent evt = new MotionTimeChangeEvent(instance,newTime);
        setChanged();
        instance.notifyObservers(evt);
    }

    public void reportModifiedMotion(Storage simmMotionData, Model model) {
      MotionEvent evt = new MotionEvent(this, model, simmMotionData, MotionEvent.Operation.Modified);
      setChanged();
      notifyObservers(evt);
    }

    public ArrayList<Storage> getModelMotions(Model aModel)
    {
       return mapModels2Motions.get(aModel);
    }

   // Get the motion node from the explorer
   public OneMotionNode getMotionNode(final Model model, final Storage motion) {
      OneModelNode modelNode = ExplorerTopComponent.findInstance().getModelNode(model);
      if(modelNode!=null) {
         Node motionsNode = modelNode.getChildren().findChild("Motions");
         if(motionsNode!=null) {
             return getMotionNode(motionsNode, motion);
         }
         else { // Preview motion
             if (model instanceof ModelForExperimentalData)
                 return getMotionNode(modelNode, motion);
         }
      }
      return null;
   }

   //------------------------------------------------------------------------
   // Utilities for currentMotions
   //------------------------------------------------------------------------
   // TODO: add an equals() method to ModelMotionPair so that we can use more standard Vector routines
   // in place of these... but then I think we'll have to implement hashCode() and other such base functions.
   private void clearCurrentMotions() { currentMotions.setSize(0); }
   private void addToCurrentMotions(ModelMotionPair pair) { currentMotions.add(pair); }
   private void setCurrentMotion(ModelMotionPair pair) {
      currentMotions.setSize(1);
      currentMotions.set(0, pair);
   }
   private void setCurrentMotions(Vector<ModelMotionPair> motions) {
      currentMotions = motions; // TODO: hopefully it's safe to just use = here
   }
   private boolean removeFromCurrentMotions(ModelMotionPair pair) {
      for(int i=0; i<currentMotions.size(); i++) {
         if(currentMotions.get(i).model == pair.model && currentMotions.get(i).motion == pair.motion) {
            currentMotions.remove(i);
            return true;
         }
      }
      return false;
   }

   public int getNumCurrentMotions() { 
       return currentMotions.size(); 
   }
   public ModelMotionPair getCurrentMotion(int i) { return currentMotions.get(i); }

   boolean isModelMotionPairCurrent(ModelMotionPair pair) {
      for(int i=0; i<currentMotions.size(); i++) {
         if(currentMotions.get(i).model == pair.model && currentMotions.get(i).motion == pair.motion) 
            return true;
      }
      return false;
   }

   void renameMotion(OpenSimObject openSimObject, String newName) {
      // Object has been renamed already; fire an ObjectsRenamedEvent.
      Vector<OpenSimObject> objs = new Vector<OpenSimObject>(1);
      objs.add(openSimObject);
      ObjectsRenamedEvent evnt = new ObjectsRenamedEvent(this, null, objs);
      setMotionModified((Storage)openSimObject, true);
      setChanged();
      getInstance().notifyObservers(evnt);
   }

    public void setCurrentTime(double d) {
        reportTimeChange(d);
    }
    
    public void refreshDisplay()
    {
        double currentTime = MotionControlJPanel.getInstance().getMasterMotion().getCurrentTime();
        setCurrentTime(currentTime);
    }

    public void saveStorageFileName(Storage newMotion, String fileName) {
        storageFilenames.put(newMotion, fileName);
    }
    public String getStorageFileName(Storage aMotion){
        return storageFilenames.get(aMotion);
    }

    public void rebuild(MotionsDBDescriptor motionsDBDescriptor) {
        clearCurrentMotions();
        ArrayList<String> modelFiles = motionsDBDescriptor.getModelFileNames();
        ArrayList<String> motionFiles = motionsDBDescriptor.getMotionFileNames();
        Object[] models =OpenSimDB.getInstance().getAllModels();
        for(int i=0; i< models.length; i++){
            Model candidateModel = ((Model)models[i]);
            int index=0;
            if (candidateModel.getInputFileName().equalsIgnoreCase(modelFiles.get(index))){
                // Create motion from corresponding motionFiles[index]
                MotionsDB.getInstance().loadMotionFile(motionFiles.get(index), true);
            }
        }
    }
    
       private OneMotionNode getMotionNode(Node motionsNode, Storage motion) {
              Node[] children = motionsNode.getChildren().getNodes();
            for(Node child : motionsNode.getChildren().getNodes()){
               if(child instanceof OneMotionNode){
                   OneMotionNode motionNode = (OneMotionNode) child;
                   if (motionNode.getMotion() == motion)
                       return motionNode;
                   for(Node associatedMotionNode:motionNode.getChildren().getNodes()){
                        if ((associatedMotionNode instanceof OneAssociatedMotionNode) &&
                                ((OneAssociatedMotionNode)associatedMotionNode).getMotion() == motion)
                            return (OneMotionNode)associatedMotionNode;
                   }
               }               
            }
            return null;
     }
       
    public void closeMotionsForModel(Model model) {
        // Send motion close events for all motions associated with this model
        ArrayList<Storage> motionsForModel = mapModels2Motions.get(model);
        if(motionsForModel != null) {
           int numMotions = motionsForModel.size();
           if (numMotions>1){
            currentCloseMotionDefaultAction = CloseMotionDefaultAction.SAVE;
            final ConfirmSaveDiscardJPanel confirmPanel = new ConfirmSaveDiscardJPanel(numMotions>1);
            confirmPanel.setConformationText("Do you want to save results for "+model.getName());
            JButton saveButton = new JButton("Save");
            DialogDescriptor confirmDialog = 
                    new DialogDescriptor(confirmPanel, 
                        "Confirm Save",
                        true,
                        new Object[]{saveButton, new JButton("Discard")},
                        saveButton,
                        0, null, new ActionListener(){

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String cmd = e.getActionCommand();
                        boolean remember = confirmPanel.rememberUserChoice();
                        if (remember) {
                            if (cmd.equalsIgnoreCase("Save")) {
                                currentCloseMotionDefaultAction = CloseMotionDefaultAction.SAVE;
                                //FileSaveModelAction.saveOrSaveAsModel(model, false);
                            } else if (cmd.equalsIgnoreCase("Discard")) {
                                currentCloseMotionDefaultAction = CloseMotionDefaultAction.DISCARD;                       
                            }
                        }
                    }
                });
                confirmDialog.setClosingOptions(null);
                DialogDisplayer.getDefault().createDialog(confirmDialog).setVisible(true);
                Object dlgReturn = confirmDialog.getValue();
                int x=0;
                for(int i=numMotions-1; i>=0; i--){
                    closeMotion(model, motionsForModel.get(i), currentCloseMotionDefaultAction == CloseMotionDefaultAction.SAVE, false);
                }
                currentCloseMotionDefaultAction = CloseMotionDefaultAction.SAVE;
            }
           else if (numMotions==1)
               closeMotion(model, motionsForModel.get(0), false);
           /*
           */
        }
    }
}
