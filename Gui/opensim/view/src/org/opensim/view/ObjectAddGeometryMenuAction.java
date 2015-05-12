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
import org.opensim.view.nodes.OneComponentNode;

public final class ObjectAddGeometryMenuAction extends CallableSystemAction implements Presenter.Popup {
    
    public boolean isEnabled() {
        return true;
    }

    public void performAction() {
    }
    
    public String getName() {
        return "Add Geometry";
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
      JMenu geometryMenu = new JMenu("Add Geometry");
      // Check that selected objects support the operations
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // If any selected object is hidden (or any selected group is mixed), return false.
        for(int i=0; i < selected.length; i++){
           if (! (selected[i] instanceof OneComponentNode))
              continue;
            OneComponentNode objectNode = (OneComponentNode) selected[i];

        }

      try {      
            geometryMenu.add(new JMenuItem(
                 (ObjectAddFrameAction) ObjectAddFrameAction.findObject(
                 (Class)Class.forName("org.opensim.view.ObjectAddFrameAction"), true)));
            geometryMenu.add(new JMenuItem(
                 (ObjectAddSphereAction) ObjectAddSphereAction.findObject(
                 (Class)Class.forName("org.opensim.view.ObjectAddSphereAction"), true)));
       } catch (ClassNotFoundException ex) {
         ex.printStackTrace();
      }     
         
      return geometryMenu;
   }
    
}
