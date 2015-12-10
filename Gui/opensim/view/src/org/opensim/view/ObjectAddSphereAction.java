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

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
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
public class ObjectAddSphereAction extends CallableSystemAction {

    public String getName() {
        return NbBundle.getMessage(ObjectAddSphereAction.class, "CTL_ObjectAddSphereAction");
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
        Sphere sp = new Sphere(0.2);
        sp.get_Appearance().get_surface_properties().set_representation(3);
        sp.markAdopted();
        if (PhysicalFrame.safeDownCast(mc) != null) {
            sp.setFrameName(mc.getName());
        } else {
            sp.setFrameName("ground");
        }
        mc.addGeometry(sp);
        PropertyEditorAdaptor pea = new PropertyEditorAdaptor(model);
        pea.handleModelChange();
        Geometry g = mc.get_geometry(mc.getNumGeometry()-1);
        ocn.getChildren().add(new Node[]{new OneGeometryNode(g)});
    }

}
