/* -------------------------------------------------------------------------- *
 * OpenSim: BodyToggleCOMAction.java                                          *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Paul Mitiguy                                       *
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
package org.opensim.view.nodes;

import java.awt.event.ActionEvent;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.BooleanStateAction;
import org.opensim.modeling.Model;
import org.opensim.modeling.Muscle;
//import org.opensim.view.FrameToggleVisibilityAction;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.pub.ViewDB;

public final class GeometryPathTogglePointsAction extends BooleanStateAction {
    
    public GeometryPathTogglePointsAction() {        
        super.setBooleanState( false );
    }
    
    public void actionPerformed(ActionEvent actionEvent) {
        // TODO implement action body
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // TODO implement action body
        boolean newState = !super.getBooleanState();
        for( int i=0; i<selected.length; i++ ){
            OneMuscleNode omn = (OneMuscleNode) selected[0];
            Muscle msl = Muscle.safeDownCast(omn.getOpenSimObject());
            ViewDB.getInstance().togglePathPointDisplay(msl, newState);
        }
        super.setBooleanState( newState );
   }
    
    public String getName() {
        return NbBundle.getMessage(GeometryPathTogglePointsAction.class, "CTL_PathTogglePointsAction");
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        super.putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
    public boolean isEnabled() {
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        OneMuscleNode omn = (OneMuscleNode) selected[0];
        Muscle msl = Muscle.safeDownCast(omn.getOpenSimObject());
        boolean current = ViewDB.getInstance().getModelVisualizationJson(msl.getModel()).getPathPointDisplayStatus(msl.getGeometryPath());
        super.setBooleanState( current );
        return (selected.length==1);
    }
    

}
