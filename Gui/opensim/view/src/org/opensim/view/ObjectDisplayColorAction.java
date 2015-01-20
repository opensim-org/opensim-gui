/*
 * Copyright (c)  2005-2008, Stanford University
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
package org.opensim.view;

import java.awt.Color;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.WindowManager;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.nodes.OneBodyNode;
import org.opensim.view.nodes.OpenSimObjectNode;
import org.opensim.view.nodes.OpenSimObjectNode.displayOption;
import org.opensim.view.nodes.PropertyEditorAdaptor;
import org.opensim.view.pub.ViewDB;

public final class ObjectDisplayColorAction extends CallableSystemAction {
   
   //--------------------------------------------------------------------------
    public void performAction() 
    { 
      JColorChooser objectColorChooser = new JColorChooser();
      Color newColor = objectColorChooser.showDialog( (JFrame)WindowManager.getDefault().getMainWindow(), "Select new color", Color.WHITE );  
      ObjectDisplayColorAction.ChangeUserSelectedNodesColor( newColor ); 
    }
   
    //--------------------------------------------------------------------------
    public static void ChangeUserSelectedNodesColor(Color newColor) {
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        for (int i = 0; i < selected.length; i++) {
            if (!(selected[i] instanceof OpenSimObjectNode)) {
                continue;
            }
            OpenSimObjectNode objectNode = (OpenSimObjectNode) selected[i];
            ObjectDisplayColorAction.ChangeUserSelectedNodeColor(objectNode, newColor, false);
        }
        ViewDB.getInstance().repaintAll();
    }

    //--------------------------------------------------------------------------
    public static void ChangeUserSelectedNodeColor(OpenSimObjectNode objectNode, Color newColor, boolean repaintViewDB) {
        if (objectNode == null || newColor == null) {
            return;
        }
        float[] newColorComponentsAsFloatArray = newColor.getRGBComponents(null);
        double[] newColorComponentsAsDoubleArray = {newColorComponentsAsFloatArray[0], newColorComponentsAsFloatArray[1], newColorComponentsAsFloatArray[2]};
        ObjectDisplayColorAction.applyOperationToNode(objectNode, newColorComponentsAsDoubleArray);
        if (repaintViewDB) {
            ViewDB.getInstance().repaintAll();
        }
    }

    //--------------------------------------------------------------------------
    private static void applyOperationToNode(final OpenSimObjectNode objectNode, double[] newColorComponents) {
        OpenSimObject obj = objectNode.getOpenSimObject();
        Children ch = objectNode.getChildren();
        if (ch.getNodesCount() > 0 ) {
            // apply action recursively
            Node[] childNodes = ch.getNodes();
            for (int child = 0; child < childNodes.length; child++) {
                if (!(childNodes[child] instanceof OpenSimObjectNode)) {
                    continue;
                }
                OpenSimObjectNode childNode = (OpenSimObjectNode) childNodes[child];
                ObjectDisplayColorAction.applyOperationToNode(childNode, newColorComponents);
            }
        } else {
            /*
             * The following is cleaner and more maintainable but fails for
             * objects that don't have "color" as property
             */
            boolean hasColor = (objectNode instanceof ColorableInterface);
            if (hasColor) {
                //PropertyEditorAdaptor pea = new PropertyEditorAdaptor("color", objectNode);
                Color newColor = new Color((float) newColorComponents[0],
                        (float) newColorComponents[1], (float) newColorComponents[2]);
                ((ColorableInterface)objectNode).setColor(newColor);
            } else {

                ViewDB.getInstance().setObjectColor(obj, newColorComponents);
            }
            objectNode.refreshNode();
            ViewDB.getInstance().updateDisplay(objectNode.getModelForNode(), 
                    ((OpenSimObjectNode)objectNode).getOwnerModelComponent());

        }
        // If objectNode is OneBodyNode, do not change color of center of mass.
        /*
        if (objectNode instanceof OneBodyNode) {
            BodyDisplayer rep = BodyToggleFrameAction.GetBodyDisplayerForBody(obj);
            rep.SetCMSphereColorToGreen();
        }*/
    }

    // Make it available only if selected objects have representation and belong to same model
    public boolean isEnabled() {
       // The "hide" option is enabled unless every selected node is hidden.
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        boolean isColorable=true;
        for(int i=0; i<selected.length && isColorable; i++){
            isColorable = (selected[i] instanceof OpenSimObjectNode);
            if (isColorable){
                OpenSimObjectNode objectNode = (OpenSimObjectNode) selected[i];
                isColorable=objectNode.getValidDisplayOptions().contains(displayOption.Colorable);
            }
        }
        return isColorable;
   }
   
   public String getName() {
      return NbBundle.getMessage(ObjectDisplayColorAction.class, "CTL_ObjectDisplayColorAction");
   }
   
   protected void initialize() {
      super.initialize();
      // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
      putValue("noIconInMenu", Boolean.TRUE);
   }
   
   public HelpCtx getHelpCtx() {
      return HelpCtx.DEFAULT_HELP;
   }
   
   protected boolean asynchronous() {
      return false;
   }
   
}
