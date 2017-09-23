/* -------------------------------------------------------------------------- *
 * OpenSim: JointToggleChildFrameAction.java                                  *
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
package org.opensim.view.nodes;

import java.awt.event.ActionEvent;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.BooleanStateAction;
import org.opensim.modeling.ComponentIterator;
import org.opensim.modeling.ComponentsList;
import org.opensim.modeling.DecorativeGeometry;
import org.opensim.modeling.FrameGeometry;
import org.opensim.modeling.Joint;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.PhysicalFrame;
import org.opensim.view.ExplorerTopComponent;

public final class JointToggleChildFrameAction extends BooleanStateAction {
    
    public JointToggleChildFrameAction() {        
    }
    
    public void actionPerformed(ActionEvent actionEvent) {
        // TODO implement action body
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // TODO implement action body
        boolean newState = !getBooleanState();
        for(int i=0;i<selected.length;i++){
            Node selectedNode = selected[i];
            if (selectedNode instanceof OneJointNode){
                OpenSimObject object=((OneJointNode) selectedNode).getOpenSimObject();
                Joint jnt = Joint.safeDownCast(object);
                PhysicalFrame b = jnt.getChildFrame();
                ComponentsList clist = b.getComponentsList();
                ComponentIterator mcIter = clist.begin();
                boolean found = false;
                while (!mcIter.equals(clist.end())&&!found){
                    if (mcIter.__deref__() instanceof FrameGeometry){
                        found = true;
                        FrameGeometry fg = ((FrameGeometry) mcIter.__deref__());
                        DecorativeGeometry.Representation oldRep = fg.getRepresentation();
                        if (oldRep == DecorativeGeometry.Representation.Hide)
                            fg.setRepresentation(DecorativeGeometry.Representation.DrawSurface);
                        else
                            fg.setRepresentation(DecorativeGeometry.Representation.Hide);
                    }
                    mcIter.next(); 
                }
                setBooleanState(newState);
                PropertyEditorAdaptor pea = new PropertyEditorAdaptor(jnt.getModel());
                pea.handleModelChange();
            }
        }
   }
    
    public String getName() {
        return NbBundle.getMessage(JointToggleChildFrameAction.class, "CTL_JointToggleChildFrameAction");
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
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        //if(selected.length!=1) return false; // only if a single item is selected
        // Action shouldn't be available otherwise
        /*
        if (selected[0] instanceof OneJointNode){
            OneJointNode dNode = (OneJointNode)selected[0];
            Joint jnt = Joint.safeDownCast(dNode.getOpenSimObject());
            Body b = jnt.getChildBody();
            vtkProp3D visuals=ViewDB.getInstance().getModelVisuals(b.getModel()).getVtkRepForObject(b);
            if (visuals instanceof BodyDisplayer){
               BodyDisplayer rep = (BodyDisplayer) visuals;
               setBooleanState(rep.isShowJointBFrame());
            }
            return true;
        }*/
        return false;
    }
}
