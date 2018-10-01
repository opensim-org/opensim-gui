/* -------------------------------------------------------------------------- *
 * OpenSim: AnnotationAction.java                                             *
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
package org.opensim.annotationtool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.Model;
import org.opensim.view.SingleModelVisuals;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;
import vtk.vtkFollower;
import vtk.vtkPolyDataMapper;
import vtk.vtkVectorText;
/**
 * A sample plugin/tool that's used to mark the current model by displaying its
 * name and hiding all other models for 5 seconds.
 * Using text rather than colors or an arrow to identify model.
 */
public final class AnnotationAction extends CallableSystemAction {
    
    public void performAction() {
        // Create a Text actor and associate it with th
        final Model mdl = OpenSimDB.getInstance().getCurrentModel();
        if (mdl ==null)
            return;
        final SingleModelVisuals vis =ViewDB.getInstance().getModelVisuals(mdl);
        vtkVectorText atext = new vtkVectorText();
        atext.SetText(mdl.getName());
        vtkPolyDataMapper textMapper = new vtkPolyDataMapper();
        textMapper.SetInput(atext.GetOutput());
        final vtkFollower textActor = new vtkFollower();
        textActor.SetMapper(textMapper);
        textActor.SetScale(0.1, 0.1, 0.1);
        textActor.AddPosition(0., 0., 0.);
        vis.addUserObject(textActor);
        //ViewDB.getInstance().hideOthers(mdl, true);
        
        // Create timer that will undo the change in 5000 msec.
        Timer restoreTimer = new Timer(5000, new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                vis.removeUserObject(textActor);
                //ViewDB.getInstance().hideOthers(mdl, false);
                
            }});
        restoreTimer.setRepeats(false);
        restoreTimer.start();
        
    }
    
    public String getName() {
        return NbBundle.getMessage(AnnotationAction.class, "CTL_AnnotationAction");
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
    
    public boolean isEnabled() {
       return OpenSimDB.getInstance().getCurrentModel()!=null;
   }
}
