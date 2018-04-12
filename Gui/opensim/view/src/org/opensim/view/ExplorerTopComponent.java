/* -------------------------------------------------------------------------- *
 * OpenSim: ExplorerTopComponent.java                                         *
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

package org.opensim.view;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.Object;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openide.ErrorManager;
import org.openide.awt.UndoRedo;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Mutex;
import org.openide.util.MutexException;
import org.opensim.modeling.GeometryPath;
import org.opensim.modeling.Marker;
import org.opensim.modeling.Model;
import org.opensim.modeling.Muscle;
import org.opensim.view.experimentaldata.ModelForExperimentalData;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PathPoint;
import org.opensim.utils.ErrorDialog;
import org.opensim.view.experimentaldata.ExperimentalDataNode;
import org.opensim.view.nodes.OneModelNode;
import org.opensim.view.experimentaldata.ExperimentalDataTopNode;
import org.opensim.view.nodes.MarkersNode;
import org.opensim.view.nodes.OneMarkerNode;
import org.opensim.view.nodes.OpenSimNode;
import org.opensim.view.nodes.OpenSimObjectNode;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

/**
 * Top component which displays something.
 */
final public class ExplorerTopComponent extends TopComponent
        implements Observer, ExplorerManager.Provider {
   
   private static final long serialVersionUID = 1L;
   
   private static ExplorerTopComponent instance;
   /** path to the icon used by the component and its open action */
   //    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
   
   private static final String PREFERRED_ID = "ExplorerTopComponent";

   private final ExplorerManager manager = new ExplorerManager();
   private final BeanTreeView modelTree = new BeanTreeView();
   
   private UndoRedo.Manager undoRedoManager = new UndoRedoManager();

   private ExplorerTopComponent() {
      initComponents();
      SwingUtilities.invokeLater(new Runnable(){

            public void run() {
                setName(NbBundle.getMessage(ExplorerTopComponent.class, "CTL_ExplorerTopComponent"));
            }
        });
      //setToolTipText(NbBundle.getMessage(ExplorerTopComponent.class, "HINT_ExplorerTopComponent"));
      // Add explorer as observer of the database
      OpenSimDB.getInstance().addObserver(this);
      ViewDB.getInstance().addObserver(this);
      //        setIcon(Utilities.loadImage(ICON_PATH, true));
      setLayout(new BorderLayout());
      add(modelTree, BorderLayout.CENTER);
      modelTree.setRootVisible(false);
      OpenSimNode root = new OpenSimNode.RootNode();
      manager.setRootContext(root);
      getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "handleDelete");
      //getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "handleDelete");
      //getActionMap().put("handleDelete", new handleDelete());
      associateLookup(ExplorerUtils.createLookup(manager, getActionMap()));
      /*
      this.addComponentListener(new ComponentAdapter(){
          public void componentResized(ComponentEvent e) {
              WindowManager.getDefault().getMainWindow().validate();
          }
      });*/
   }

   BeanTreeView getTree() {
      return modelTree;
   }
   
   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
   private void initComponents() {

      org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(0, 400, Short.MAX_VALUE)
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(0, 300, Short.MAX_VALUE)
      );
   }// </editor-fold>//GEN-END:initComponents
        
        
   // Variables declaration - do not modify//GEN-BEGIN:variables
   // End of variables declaration//GEN-END:variables
        
        /**
         * Gets default instance. Do not use directly: reserved for *.settings files only,
         * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
         * To obtain the singleton instance, use {@link findInstance}.
         */
        public static synchronized ExplorerTopComponent getDefault() {
           if (instance == null) {
              instance = new ExplorerTopComponent();
           }
           return instance;
        }
        
        /**
         * Obtain the ExplorerTopComponent instance. Never call {@link #getDefault} directly!
         */
        public static synchronized ExplorerTopComponent findInstance() {
           TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
           if (win == null) {
              ErrorManager.getDefault().log(ErrorManager.WARNING, "Cannot find Explorer component. It will not be located properly in the window system.");
              return getDefault();
           }
           if (win instanceof ExplorerTopComponent) {
              return (ExplorerTopComponent)win;
           }
           ErrorManager.getDefault().log(ErrorManager.WARNING, "There seem to be multiple components with the '" + PREFERRED_ID + "' ID. That is a potential source of errors and unexpected behavior.");
           return getDefault();
        }
        
        public int getPersistenceType() {
           return TopComponent.PERSISTENCE_ALWAYS;
        }
        // It is good idea to switch all listeners on and off when the
        // component is shown or hidden. In the case of TopComponent use:
        protected void componentActivated() {
           ExplorerUtils.activateActions(getExplorerManager(), true);
        }
        protected void componentDeactivated() {
           ExplorerUtils.activateActions(getExplorerManager(), false);
        }
        
        public void componentOpened() {
           redisplay();
        }
        
        public void componentClosed() {
        }
                
        /** replaces this in object stream */
        public Object writeReplace() {
           return new ResolvableHelper();
        }
        
        protected String preferredID() {
           return PREFERRED_ID;
        }
        
        public void update(Observable o, Object arg) {
           // Observable is OpenSimDB
         if (o instanceof ViewDB) {
            if (arg instanceof ObjectSelectedEvent) {
                ObjectSelectedEvent ev = (ObjectSelectedEvent)arg;
                Node selectedObjectNode = null;
                selectNodeForSelectedObject(ev.getSelectedObject());
            }
            return;
         }
           if (arg instanceof ObjectsAddedEvent) {
              final ObjectsAddedEvent evnt = (ObjectsAddedEvent)arg;
              final Vector<OpenSimObject> objs = evnt.getObjects();
              for (int i=0; i<objs.size(); i++) {
                 if (objs.get(i) instanceof Model) {
                    final int index = i;
                    // Add the model to the Tree window.
                      ExplorerTopComponent tree = ExplorerTopComponent.findInstance();
                      Node rootNode = tree.getExplorerManager().getRootContext();
                      Model newModel = (Model)objs.get(index);
                      OneModelNode newModelNode = new OneModelNode(newModel);
                      rootNode.getChildren().add(new Node[] { newModelNode});
                      updateCurrentModelNode(newModel);

                 } else if (objs.get(i) instanceof Marker) {
                    Marker marker = (Marker)objs.get(i);
                    Model model = evnt.getModel();
                    OneModelNode modelNode = getModelNode(model);
                    Children children = modelNode.getChildren();
                    Node[] nodes = children.getNodes();
                    String markersNodeName = NbBundle.getMessage(MarkersNode.class, "CTL_Markers");
                    for (int j=0; j < nodes.length; j++) {
                       if (nodes[j].getDisplayName().equals(markersNodeName)) {
                          OneMarkerNode newMarkerNode = new OneMarkerNode(marker);
                          nodes[j].getChildren().add(new Node[] {newMarkerNode});
                          break;
                       }
                    }
                 }
              }
           } else if (arg instanceof ObjectsDeletedEvent) {
              final ObjectsDeletedEvent evnt = (ObjectsDeletedEvent)arg;
              final Vector<OpenSimObject> objs = evnt.getObjects();
              for (int i=0; i<objs.size(); i++) {
                 if (objs.get(i) instanceof Marker) {
                    Marker marker = (Marker)objs.get(i);
                    Model model = evnt.getModel();
                    OneModelNode modelNode = getModelNode(model);
                    Children children = modelNode.getChildren();
                    Node[] nodes = children.getNodes();
                    String markersNodeName = NbBundle.getMessage(MarkersNode.class, "CTL_Markers");
                    for (int j=0; j < nodes.length; j++) {
                       if (nodes[j].getDisplayName().equals(markersNodeName)) {
                          children = nodes[j].getChildren();
                          nodes = children.getNodes();
                          for (int k=0; k<nodes.length; k++) {
                             if (nodes[k] instanceof OneMarkerNode) {
                                OneMarkerNode omn = (OneMarkerNode)nodes[k];
                                if (Marker.getCPtr(omn.getOpenSimObject()) == Marker.getCPtr(marker)) {
                                   try {
                                      children.remove(new Node[] {nodes[k]});
                                      if (nodes[k] != null)
                                         nodes[k].destroy();
                                   } catch (IOException ex) {
                                      ErrorDialog.displayExceptionDialog(ex);
                                   }
                                }
                             }
                          }
                          break;
                       }
                    }
                 }
              }
           } else if (arg instanceof ObjectSetCurrentEvent) {
              ObjectSetCurrentEvent evnt = (ObjectSetCurrentEvent)arg;
              Vector<OpenSimObject> objs = evnt.getObjects();
              for (int i=0; i<objs.size(); i++) {
                 if (objs.get(i) instanceof Model) {
                    final Model currentModel = (Model)objs.get(i);
                    SwingUtilities.invokeLater(new Runnable() {
                       public void run() {
                          updateCurrentModelNode(currentModel);
                       }
                    }
                    );
                    break;
                 }
              }
           } else if (arg instanceof ModelEvent) {
              final ModelEvent evnt = (ModelEvent)arg;
              // Add the model to the Tree window.
                    ExplorerTopComponent tree = ExplorerTopComponent.findInstance();
                    
                    Node rootNode = tree.getExplorerManager().getRootContext();
                    switch(evnt.getOperation()) {
                       case Open :
                       {
                          Model newModel = evnt.getModel();
                          OneModelNode newModelNode;
                          if (newModel instanceof ModelForExperimentalData)
                              newModelNode = new ExperimentalDataTopNode(newModel);
                          else
                            newModelNode = new OneModelNode(newModel);
                          rootNode.getChildren().add(new Node[] { newModelNode});
                          updateCurrentModelNode(newModel);
                          break;
                       }
                       case Close:
                       {
                          Model closingModel = evnt.getModel();
                          OneModelNode modelNode = getModelNode(closingModel);
                          try {
                             rootNode.getChildren().remove(new Node[] {modelNode});
                             if(modelNode != null) modelNode.destroy();
                             updateCurrentModelNode(null);
                          } catch (IOException ex) {
                             ErrorDialog.displayExceptionDialog(ex);
                          }
                          break;
                       }
                       case SetCurrent :
                       {
                          updateCurrentModelNode(evnt.getModel());
                          break;
                       }
                    }
                 
              
           } else if (arg instanceof ObjectsRenamedEvent) {
              final ObjectsRenamedEvent ev = (ObjectsRenamedEvent)arg;
              Vector<OpenSimObject> objs = ev.getObjects();
              for (int i=0; i<objs.size(); i++) {
                 ExplorerTopComponent tree = ExplorerTopComponent.findInstance();
                 Node rootNode = tree.getExplorerManager().getRootContext();
                 Node[] nodes = rootNode.getChildren().getNodes();
                 for (int j=0; j<nodes.length; j++) {
                    if (nodes[j] instanceof OpenSimNode) {
                       ((OpenSimNode)nodes[j]).renameObjectNode(objs.get(i), objs.get(i).getName());
                    }
                 }
              }
           }
        }
        
        public ExplorerManager getExplorerManager() {
           return manager;
        }

    public void selectNodeForSelectedObject(SelectedObject selectedObject) {
        //Node ret = null;
        OpenSimObject oObject = selectedObject.getOpenSimObject();
        selectNodeForObject(oObject );
    }
    public void selectNodeForObject(OpenSimObject oObject) {
        Children children = getExplorerManager().getRootContext().getChildren();
        Node[] nodes = children.getNodes();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] instanceof OneModelNode) {
                OneModelNode modelNode = ((OneModelNode) (nodes[i]));
                Node objectNode = findObjectNode(modelNode, oObject);
                // Hack to select Muscle based on selected PathPoint
                if (oObject instanceof PathPoint){
                    PathPoint ppt = (PathPoint)oObject;
                    GeometryPath ppath = GeometryPath.safeDownCast(ppt.getOwner());
                    OpenSimObject pathOwner = ppath.getOwner();
                    objectNode = findObjectNode(modelNode,pathOwner);
                    
                }
                if (objectNode != null) {
                    try {
//                        Node[] previouslySelectedNodes = findInstance().getExplorerManager().getSelectedNodes();
//                        Node[] newSelectedNodes = new Node[previouslySelectedNodes.length+1];
//                        System.arraycopy(previouslySelectedNodes, 0, newSelectedNodes, 0, previouslySelectedNodes.length);
//                        newSelectedNodes[previouslySelectedNodes.length] = objectNode;
                        findInstance().getExplorerManager().setSelectedNodes(new Node[]{objectNode});
                        this.setActivatedNodes(new Node[]{objectNode});
                        ((OpenSimNode)objectNode).refreshNode();
                        requestActive();
                    } catch (PropertyVetoException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    break;
                }
            }
        }
        //return ret;
    }

    private Node findObjectNode(Node topNode, OpenSimObject openSimObject) {
        if (topNode instanceof OpenSimObjectNode){
            if (((OpenSimObjectNode)topNode).getOpenSimObject().equals(openSimObject))
                return topNode;
        }
        if (topNode instanceof ExperimentalDataNode){
            ExperimentalDataNode edn = (ExperimentalDataNode) topNode;
            if (openSimObject.equals(edn.getDataObject()))
                return topNode;
        }
        if (topNode.isLeaf()) return null;
        boolean found = false;
        Children chden= topNode.getChildren();
        Node fndNode=null;
        for(int ch=0; ch < chden.getNodesCount() && !found; ch++){
            fndNode = findObjectNode(chden.getNodeAt(ch), openSimObject);
            found = (fndNode != null);
        }
        return fndNode;
    }
        
        final static class ResolvableHelper implements Serializable {
           private static final long serialVersionUID = 1L;
           public Object readResolve() {
              return ExplorerTopComponent.getDefault();
           }
        }
        
        public void redisplay() {
           // Sync. up the tree with the OpenSimDB
           OpenSimDB db = OpenSimDB.getInstance();
           
           
           Node rootNode = getExplorerManager().getRootContext();
           
           Object[] models = db.getAllModels();
           rootNode.getChildren().remove(rootNode.getChildren().getNodes());
           for(int i=0; i < models.length; i++)
              rootNode.getChildren().add(new Node[] { new OneModelNode((Model)models[i]) });
           
        }
        /**
         * Update marker of current model in navigator view
         *
         * @param new currentModel, null if not explicitly specified
         */
        public void updateCurrentModelNode(Model currentModel) {
           Children children = getExplorerManager().getRootContext().getChildren();
           Node[] nodes = children.getNodes();
           for(int i=0; i < nodes.length; i++) {
              if (nodes[i] instanceof OneModelNode) {
                 OneModelNode node = ((OneModelNode)(nodes[i]));
                 node.setName(node.getModel().getName());
              }
           }
           
           
        }
        /**
         * Get a reference to the navigator/explorer node represtning the passed in model
         */
        public OneModelNode getModelNode(final Model abstractModel) {
           Node rootNode = getExplorerManager().getRootContext();
           for(Node child : rootNode.getChildren().getNodes())
              if((child instanceof OneModelNode) && ((OneModelNode)child).getModel().equals(abstractModel))
                 return (OneModelNode)child;
           return null;
        }

        /**
         * Disallow closing the explorer view as we depend on it everywhere
         *
        public boolean canClose() {
           return false;
        }*/
        
        // Undo Support
        public UndoRedo getUndoRedo(){
            return getUndoRedoManager();
}
        public UndoRedo.Manager getUndoRedoManager() {
            if (undoRedoManager == null) {
                undoRedoManager = new UndoRedoManager();
                undoRedoManager.setLimit(100);
            }
            return undoRedoManager;
        }
        // [Undo manager performing undo/redo in AWT event thread 
        static class UndoRedoManager extends UndoRedo.Manager {
            private Mutex.ExceptionAction runUndo = new Mutex.ExceptionAction() {
                public Object run() throws Exception {
                    superUndo();
                    return null;
                }
            };
            private Mutex.ExceptionAction runRedo = new Mutex.ExceptionAction() {
                public Object run() throws Exception {
                    superRedo();
                    return null;
                }
            };
            public void superUndo() throws CannotUndoException {
                super.undo();
            }
            public void superRedo() throws CannotRedoException {
                super.redo();
            }
            public void undo() throws CannotUndoException {
                if (java.awt.EventQueue.isDispatchThread()) {
                    superUndo();
                } else {
                    try {
                        Mutex.EVENT.readAccess(runUndo);
                    } catch (MutexException ex) {
                        Exception e = ex.getException();
                        if (e instanceof CannotUndoException)
                            throw (CannotUndoException) e;
                        else // should not happen, ignore
                            e.printStackTrace();
                    }
                }
            }
            public void redo() throws CannotRedoException {
                if (java.awt.EventQueue.isDispatchThread()) {
                    superRedo();
                } else {
                    try {
                        Mutex.EVENT.readAccess(runRedo);
                    } catch (MutexException ex) {
                        Exception e = ex.getException();
                        if (e instanceof CannotRedoException)
                            throw (CannotRedoException) e;
                        else // should not happen, ignore
                            e.printStackTrace();
                    }
                }
            }
        }
        
        public static void addUndoableEdit(AbstractUndoableEdit aUndoableEdit)
        {
            getDefault().getUndoRedoManager().addEdit(aUndoableEdit);
            TopComponent tc = ViewDB.getCurrentModelWindow();
            if (tc==null){ // No gfx window
                tc = getDefault();
            } 
            if (tc==null) return;   // No tc to piggyback on
            final TopComponent tcf = tc;
            if (java.awt.EventQueue.isDispatchThread()) {
                tcf.requestActive();
            }
            else {
                SwingUtilities.invokeLater(new Runnable(){

                @Override
                public void run() {
                    tcf.requestActive();
                }
            });
            }
         }
        public static void addFinalEdit()
        {
            getDefault().getUndoRedoManager().discardAllEdits();
        }
}
