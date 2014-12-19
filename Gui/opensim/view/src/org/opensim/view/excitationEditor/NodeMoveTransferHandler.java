
package org.opensim.view.excitationEditor;
/*****************************************************************************************************
* NodeMoveTransferHandler.java
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