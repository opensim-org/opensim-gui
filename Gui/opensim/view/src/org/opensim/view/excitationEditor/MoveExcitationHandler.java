/* -------------------------------------------------------------------------- *
 * OpenSim: MoveExcitationHandler.java                                        *
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
 * MoveExcitationHandler.java
 *
 * Created on March 11, 2008, 2:52 PM
 *
 */

package org.opensim.view.excitationEditor;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import org.opensim.utils.ErrorDialog;

/**
 *
 * @author Ayman
 */
public class MoveExcitationHandler extends NodeMoveTransferHandler{
    ExcitationsGridJPanel topPanel;
    /** Creates a new instance of MoveExcitationHandler */
    public MoveExcitationHandler(ExcitationsGridJPanel topPanel) {
        this.topPanel = topPanel;
    }

//    @Override 
    protected void exportDone(JComponent source, Transferable data, int action) {
        if(source instanceof JTree) {
            JTree tree = (JTree) source;
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            TreePath currentPath = tree.getSelectionPath();
            ///System.out.println("Destination node="+currentPath.getLastPathComponent().toString());
            if(currentPath != null) { // Into a subnode
                //System.out.println("addNodes");
                DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) currentPath.getLastPathComponent();
                // Allow insertion only into columns
                if (!(targetNode.getUserObject() instanceof ExcitationColumnJPanel))
                    return;
                addNodes(currentPath, model, data);
                //handlePathMove(currentPath, model, data);
            } else {    // Same parent ok
                //System.out.println("insertNodes");
                insertNodes(tree, model, data);
                //Point location = ((TreeDropTarget) tree.getDropTarget()).getMostRecentDragLocation();
                //TreePath path = tree.getClosestPathForLocation(location.x, location.y);
                //handlePathRearrange(path, model, data);
           }
        }
        super.exportDone(source, data, action);
    }
    /**
     * Move nodes out from their parent to the currentPath
     */
    private void addNodes(TreePath currentPath, DefaultTreeModel model, Transferable data) {
        System.out.println("ADD NODES");
        MutableTreeNode targetNode = (MutableTreeNode) currentPath.getLastPathComponent();
        try {
            TreePath[] movedPaths = (TreePath[]) data.getTransferData(DataFlavor.stringFlavor);
            for(int i = 0; i < movedPaths.length; i++) {
                DefaultMutableTreeNode moveNode = (DefaultMutableTreeNode) movedPaths[i].getLastPathComponent();
                if(!moveNode.equals(targetNode)) {
                    DefaultMutableTreeNode oldParent = (DefaultMutableTreeNode) moveNode.getParent();
                    int oldIndex = model.getIndexOfChild(oldParent, moveNode);
                    if(oldParent == null) return ;
                    Object test = (((DefaultMutableTreeNode) moveNode.getParent()).getUserObject());
                    if (! (test instanceof ExcitationColumnJPanel))
                        continue;
                    ExcitationColumnJPanel sourcePanel = (ExcitationColumnJPanel) test;
                    test =(((DefaultMutableTreeNode) targetNode).getUserObject());
                    if (! (test instanceof ExcitationColumnJPanel))
                        continue;
                    ExcitationColumnJPanel targetPanel = (ExcitationColumnJPanel)test;
                    ExcitationPanel panel2Move = ((ExcitationObject)moveNode.getUserObject()).getPlotPanel();
                    sourcePanel.removePanel(panel2Move);
                    model.removeNodeFromParent(moveNode);                   
                    try{
                        targetPanel.appendPanel(panel2Move);
                        model.insertNodeInto(moveNode, targetNode, targetNode.getChildCount());
                        
                    }catch(IllegalArgumentException ex){
                        model.insertNodeInto(moveNode, oldParent, oldIndex);
                    }
                    topPanel.validate();
                }
            }
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void handlePathMove(TreePath currentPath, DefaultTreeModel model, Transferable data) {
        DefaultMutableTreeNode TargetNode = (DefaultMutableTreeNode) currentPath.getLastPathComponent();
        // Type must be correct
        Object targetObject = TargetNode.getUserObject();
        if (!(targetObject instanceof ExcitationColumnJPanel))
            return;
        ExcitationColumnJPanel targetPanel = (ExcitationColumnJPanel)targetObject;
        TreePath[] movedPaths;
        try {
            movedPaths = (TreePath[]) data.getTransferData(DataFlavor.stringFlavor);
            for(int i = 0; i < movedPaths.length; i++) {
                DefaultMutableTreeNode moveNode = (DefaultMutableTreeNode) movedPaths[i].getLastPathComponent();
                Object object2Move = moveNode.getUserObject();
                if (object2Move instanceof ExcitationObject){
                    // get Parent and remove Panel
                   ExcitationColumnJPanel sourcePanel = (ExcitationColumnJPanel)(((DefaultMutableTreeNode) moveNode.getParent()).getUserObject());
                   ExcitationObject eObject = (ExcitationObject)object2Move;
                   sourcePanel.removePanel(eObject.getPlotPanel());
                   targetPanel.appendPanel(eObject.getPlotPanel());
                }
            }
            topPanel.validate();
       } catch (IOException ex) {
            ErrorDialog.displayExceptionDialog(ex);
        } catch (UnsupportedFlavorException ex) {
            ex.printStackTrace();
        }
    }

    public void handlePathRearrange(TreePath currentPath, DefaultTreeModel model, Transferable data) {
        DefaultMutableTreeNode TargetNode = (DefaultMutableTreeNode) currentPath.getLastPathComponent();
        // Type must be correct
        Object targetObject = TargetNode.getUserObject();
        if (!(targetObject instanceof ExcitationObject))
            return;
        ExcitationColumnJPanel parentPanel = (ExcitationColumnJPanel)((DefaultMutableTreeNode)TargetNode.getParent()).getUserObject();
        TreePath[] movedPaths;
        try {
            movedPaths = (TreePath[]) data.getTransferData(DataFlavor.stringFlavor);
            for(int i = 0; i < movedPaths.length; i++) {
                DefaultMutableTreeNode moveNode = (DefaultMutableTreeNode) movedPaths[i].getLastPathComponent();
                int targetNdx=TargetNode.getParent().getIndex(moveNode);
                Object object2Move = moveNode.getUserObject();
                if (object2Move instanceof ExcitationObject){
                    // get Parent and remove Panel
                   ExcitationObject eObject=(ExcitationObject) object2Move;
                   parentPanel.removePanel(eObject.getPlotPanel());
                   parentPanel.addPanel(eObject.getPlotPanel(), targetNdx);
                   System.out.println("Moving panel to new position "+targetNdx);
                }
            }
            topPanel.validate();
       } catch (IOException ex) {
            ErrorDialog.displayExceptionDialog(ex);
        } catch (UnsupportedFlavorException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Change the order of children in same parent
     */
    private void insertNodes(JTree tree, DefaultTreeModel model, Transferable data) {
        System.out.println("INSERT NODES");
        Point location = ((TreeDropTarget) tree.getDropTarget()).getMostRecentDragLocation();
        TreePath path = tree.getClosestPathForLocation(location.x, location.y);
        DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) targetNode.getParent();
        ExcitationColumnJPanel targetPanel=(ExcitationColumnJPanel)(((DefaultMutableTreeNode) parent).getUserObject());
        try {
            TreePath[] movedPaths = (TreePath[]) data.getTransferData(DataFlavor.stringFlavor);
            for(int i = 0; i < movedPaths.length; i++) {
                MutableTreeNode moveNode = (MutableTreeNode) movedPaths[i].getLastPathComponent();
                if(!moveNode.equals(targetNode)) {
                    MutableTreeNode oldParent = (MutableTreeNode) moveNode.getParent();
                    int oldIndex = model.getIndexOfChild(oldParent, moveNode);
                    //if(oldParent == null) return ;
                    // Get parent before modifying tree
                    ExcitationColumnJPanel sourcePanel = (ExcitationColumnJPanel)(((DefaultMutableTreeNode) moveNode.getParent()).getUserObject());
                    model.removeNodeFromParent(moveNode);
                    Object obj=((DefaultMutableTreeNode) moveNode).getUserObject();
                    int destIndex = model.getIndexOfChild(parent, targetNode);
                    if (obj instanceof ExcitationObject){
                        sourcePanel.removePanel(((ExcitationObject)obj).getPlotPanel());
                    }                    
                    try{ 
                        targetPanel.addPanel(((ExcitationObject)obj).getPlotPanel(), destIndex);  
                        model.insertNodeInto(moveNode, parent, model.getIndexOfChild(parent, targetNode));
                    }catch(IllegalArgumentException ex){
                        targetPanel.addPanel(((ExcitationObject)obj).getPlotPanel(),destIndex);
                        model.insertNodeInto(moveNode, oldParent, oldIndex);
                    }
                }
            }
            topPanel.validate();

        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
