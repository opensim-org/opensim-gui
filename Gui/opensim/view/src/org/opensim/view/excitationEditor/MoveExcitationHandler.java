/*
 * MoveExcitationHandler.java
 *
 * Created on March 11, 2008, 2:52 PM
 *
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
            ex.printStackTrace();
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
            ex.printStackTrace();
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
