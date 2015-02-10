/*
 *
 * OneMuscleNode
 * Author(s): Ayman Habib
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
 */
package org.opensim.view.nodes;

import java.awt.Image;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.modeling.CustomJoint;
import org.opensim.modeling.Joint;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.SpatialTransform;
import org.opensim.modeling.TransformAxis;

/**
 *
 * @author Ayman Habib
 */
public class OneJointNode extends OpenSimObjectNode {

	private static ResourceBundle bundle = NbBundle.getBundle(OneJointNode.class);
	/** Creates a new instance of OneMuscleNode */
	public OneJointNode(OpenSimObject jnt) {
		super(jnt);
		setShortDescription(bundle.getString("HINT_JointNode"));
		Joint joint = Joint.safeDownCast(jnt);
		CustomJoint cj = CustomJoint.safeDownCast(joint);
		if (cj != null) {
			SpatialTransform spt = cj.getSpatialTransform();
			for (int i = 0; i < 6; i++) {
				TransformAxis ta = spt.getTransformAxis(i);
				getChildren().add(new Node[] { new OneDofNode(ta) });
			}
		} else {
			setChildren(Children.LEAF);
		}
	}
	public Image getIcon(int i) {
		URL imageURL = this.getClass().getResource("icons/jointNode.png");
		if (imageURL != null) {
			return new ImageIcon(imageURL, "Joint").getImage();
		} else {
			return null;
		}
	}

    public Action[] getActions(boolean b) {
        Action[] superActions = (Action[]) super.getActions(b);        
        // Arrays are fixed size, onvert to a List
        List<Action> actions = java.util.Arrays.asList(superActions);
        // Create new Array of proper size
        Action[] retActions = new Action[actions.size()+2];
        
        actions.toArray(retActions);
        
        try {
            // append new command to the end of the list of actions
            retActions[actions.size()] = (JointToggleChildFrameAction) JointToggleChildFrameAction.findObject(
                     (Class)Class.forName("org.opensim.view.nodes.JointToggleChildFrameAction"), true);
            retActions[actions.size()+1] = (JointToggleParentFrameAction) JointToggleParentFrameAction.findObject(
                     (Class)Class.forName("org.opensim.view.nodes.JointToggleParentFrameAction"), true);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return retActions;
    }    
}