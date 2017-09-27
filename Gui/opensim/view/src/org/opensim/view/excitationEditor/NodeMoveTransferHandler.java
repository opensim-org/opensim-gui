/* -------------------------------------------------------------------------- *
 * OpenSim: NodeMoveTransferHandler.java                                      *
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

package org.opensim.view.excitationEditor;
/*****************************************************************************************************
* NodeMoveTransferHandler.java
*  
*
* After http://www.java-forum.org/ & http://articles.lightdev.com/tree/tree_article.pdf
*
* The NodeMoveTransferHandler extends the class "TransferHandler" and
* overrides the method "createTransferable"
* where we state what happens at the beginning of the drag operation.
* There we simply test if the component is a tree
* and all selected tree paths are passed in an array to the "GenericTransferable".
* Additionally we call the methode "createDragImage"
* in order to create a semi transparent image of the nodes being moved.
*
* As soon as the user releases the mouse key, Swing automatically calls the method "exportDone".
* There we test if a node is selected, and this one serves as target node.
* Then we get all tree paths from the Transferable and each is moved to the new
* parent node (addNodes), respectively inserted between two nodes (insertNodes).
*
* The method "createDragImage" uses the cell renderer of the tree to get the components
* by which the nodes being moved are represented (usually JLabels).
* It then creates a semi transparent BufferedImage and draws the Components (JLabels) on them.
* The image created is returned using the method "getDragImage".
* ("getDragImage" is called by "paintImage" in "TreeDropTarget".)
****************************************************************************************************/

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import javax.swing.tree.*;
public abstract class NodeMoveTransferHandler extends TransferHandler {
    public NodeMoveTransferHandler() {
        super();
    }
//    @Override
    protected Transferable createTransferable(JComponent c) {
        Transferable t = null;
        if(c instanceof JTree) {
            JTree tree = (JTree) c;
            t = new GenericTransferable(tree.getSelectionPaths());
            dragPath = tree.getSelectionPaths();
            createDragImage(tree);
        }
        return t;
    }
//    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.MOVE;
    }
    @Override
    public BufferedImage getDragImage() {
        return image[0];
    }
    private void createDragImage(JTree tree) {
        if (dragPath != null) {
            try {
                image = new BufferedImage[dragPath.length];
                for (int i = 0; i < dragPath.length; i++) {
                    Rectangle pathBounds = tree.getPathBounds(dragPath[i]);
                    TreeCellRenderer r = tree.getCellRenderer();
                    DefaultTreeModel m = (DefaultTreeModel)tree.getModel();
                    boolean nIsLeaf = m.isLeaf(dragPath[i].getLastPathComponent());
                    MutableTreeNode draggedNode = (MutableTreeNode) dragPath[i].getLastPathComponent();
                    JComponent lbl = (JComponent)r.getTreeCellRendererComponent(tree, draggedNode, false ,
                            tree.isExpanded(dragPath[i]),nIsLeaf, 0,false);
                    lbl.setBounds(pathBounds);
                    BufferedImage img = new BufferedImage(lbl.getWidth(), lbl.getHeight(),
                            BufferedImage.TYPE_INT_ARGB_PRE);
                    Graphics2D graphics = img.createGraphics();
                    graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    lbl.setOpaque(false);
                    lbl.paint(graphics);
                    graphics.dispose();
                    image[i] = img;
                }
            } catch (RuntimeException re) {}
        }
    }

    protected void exportDone(JComponent source, Transferable data, int action) {
        dragPath = null;
        super.exportDone(source, data, action);
    }

    //abstract public void handlePathMove(TreePath currentPath, DefaultTreeModel model, Transferable data) ;
    //abstract public void handlePathRearrange(TreePath currentPath, DefaultTreeModel model, Transferable data) ;
    
    private TreePath[] dragPath;
    private BufferedImage[] image;
}