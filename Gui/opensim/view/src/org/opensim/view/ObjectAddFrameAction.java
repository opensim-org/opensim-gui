/* -------------------------------------------------------------------------- *
 * OpenSim: ObjectAddFrameAction.java                                         *
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

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.Frame;
import org.opensim.modeling.FrameGeometry;
import org.opensim.modeling.Geometry;
import org.opensim.modeling.Model;
import org.opensim.modeling.ModelComponent;
import org.opensim.modeling.PhysicalFrame;
import org.opensim.modeling.Sphere;
import org.opensim.view.nodes.OneGeometryNode;
import org.opensim.view.nodes.OneModelComponentNode;
import org.opensim.view.nodes.PropertyEditorAdaptor;

/**
 * Action which adds a Sphere to component.
 */
public class ObjectAddFrameAction extends CallableSystemAction {

    public String getName() {
        return NbBundle.getMessage(ObjectAddFrameAction.class, "CTL_ObjectAddFrameAction");
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

    public void performAction() {
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        OneModelComponentNode ocn = (OneModelComponentNode) selected[0];
        ModelComponent mc = ocn.getModelComp();
        Model model = mc.getModel();
        FrameGeometry sp = new FrameGeometry(0.2);
        sp.get_Appearance().get_SurfaceProperties().set_representation(3);
        Frame frm = Frame.safeDownCast(mc);
        if (frm != null) {
            frm.attachGeometry(sp);
        }
        PropertyEditorAdaptor pea = new PropertyEditorAdaptor(model);
        pea.handleModelChange();
        ocn.getChildren().add(new Node[]{new OneGeometryNode(sp)});
    }

}
