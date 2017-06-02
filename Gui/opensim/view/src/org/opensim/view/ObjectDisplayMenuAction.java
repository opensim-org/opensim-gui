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

import java.awt.event.ActionEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;
import org.openide.util.actions.Presenter;
import org.opensim.view.nodes.OpenSimObjectNode;
import org.opensim.view.nodes.OpenSimObjectNode.displayOption;

public final class ObjectDisplayMenuAction extends CallableSystemAction implements Presenter.Popup {
    
    public boolean isEnabled() {
        return true;
    }

    public void performAction() {
    }
    
    public String getName() {
        return "Display";
    }
        
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }

   public void actionPerformed(ActionEvent e) {
   }

   public JMenuItem getPopupPresenter() {
      JMenu displayMenu = new JMenu("Display");
      boolean showShadingOptions=false;
      // Check that selected objects support the operations
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // If any selected object is hidden (or any selected group is mixed), return false.
        for(int i=0; i < selected.length; i++){
           if (! (selected[i] instanceof OpenSimObjectNode))
              continue;
            OpenSimObjectNode objectNode = (OpenSimObjectNode) selected[i];
            if (objectNode.getValidDisplayOptions().contains(displayOption.Colorable))
               showShadingOptions=true;
        }

      try {      
         displayMenu.add(new JMenuItem(
                 (ObjectDisplayShowAction) ObjectDisplayShowAction.findObject(
                 (Class)Class.forName("org.opensim.view.ObjectDisplayShowAction"), true)));
         displayMenu.add(new JMenuItem(
                 (ObjectDisplayShowOnlyAction) ObjectDisplayShowOnlyAction.findObject(
                 (Class)Class.forName("org.opensim.view.ObjectDisplayShowOnlyAction"), true)));
         displayMenu.add(new JMenuItem(
                 (ObjectDisplayHideAction) ObjectDisplayHideAction.findObject(
                 (Class)Class.forName("org.opensim.view.ObjectDisplayHideAction"), true)));
         if (showShadingOptions){
            displayMenu.addSeparator();
            displayMenu.add(new JMenuItem(
                    (ObjectDisplaySurfaceGouraudAction) ObjectDisplaySurfaceGouraudAction.findObject(
                    (Class)Class.forName("org.opensim.view.ObjectDisplaySurfaceGouraudAction"), true)));
            displayMenu.add(new JMenuItem(
                    (ObjectDisplayWireframeAction) ObjectDisplayWireframeAction.findObject(
                    (Class)Class.forName("org.opensim.view.ObjectDisplayWireframeAction"), true)));
            displayMenu.add(new JMenuItem(
                    (ObjectDisplayColorAction) ObjectDisplayColorAction.findObject(
                    (Class)Class.forName("org.opensim.view.ObjectDisplayColorAction"), true)));
            displayMenu.add(new JMenuItem(
                    (ObjectDisplayOpacityAction) ObjectDisplayOpacityAction.findObject(
                    (Class)Class.forName("org.opensim.view.ObjectDisplayOpacityAction"), true)));
         }
       } catch (ClassNotFoundException ex) {
         ex.printStackTrace();
      }     
         
      return displayMenu;
   }
    
}
