/* -------------------------------------------------------------------------- *
 * OpenSim: CameraAction.java                                                 *
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

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.view.base.CamerasMenu;

public final class CameraAction extends CallableSystemAction 
        implements LookupListener{
    
    Lookup.Template tpl = new Lookup.Template (ModelWindowVTKTopComponent.class);
    private Lookup.Result result = Utilities.actionsGlobalContext().lookup(tpl);
    ModelWindowVTKTopComponent   tc;
    
    public CameraAction()
    {
        result.addLookupListener (this);

    }
    public void performAction() {
        // TODO implement action body
    }
    
    public String getName() {
        return NbBundle.getMessage(CameraAction.class, "CTL_CameraAction");
    }
    
    protected String iconResource() {
        return "org/opensim/view/camera.gif";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }

    public Component getToolbarPresenter() {
        
        final Component toolbarButton= new JButton("Camera >");
        toolbarButton.addMouseListener(new MouseAdapter(){  
              public void mousePressed(MouseEvent e) { showSettingsPopup(e); }
               private void showSettingsPopup(MouseEvent e) {
                   if (tc==null)
                       return;
                  JPopupMenu settingsMenu = new JPopupMenu();
                  settingsMenu.add(new CamerasMenu(tc.getCanvas()));
                  settingsMenu.show(toolbarButton, e.getX(), e.getY());
              }
        });
         return toolbarButton;
    }

    public void resultChanged(LookupEvent lookupEvent) {
        Lookup.Result r = (Lookup.Result) lookupEvent.getSource();
        Collection c = r.allInstances();
        if (!c.isEmpty()) {
            tc = (ModelWindowVTKTopComponent) c.iterator().next();
        }
    }
    
}
