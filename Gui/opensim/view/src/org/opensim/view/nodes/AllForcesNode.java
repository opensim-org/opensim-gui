/*
 *
 * AllForcesNode
 * Author(s): Ayman Habib & Jeff Reinbolt
 * Copyright (c)  2005-2006, Stanford University, Ayman Habib & Jeff Reinbolt
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
import javax.swing.ImageIcon;
import java.util.ResourceBundle;
import javax.swing.Action;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.modeling.ForceSet;

/**
 *
 * @author Ayman Habib & Jeff Reinbolt
 *
 * Top level Actuators node in Navigator view
 */
public class AllForcesNode extends OpenSimObjectSetNode {

    private static ResourceBundle bundle = NbBundle.getBundle(AllForcesNode.class);

    /**
     * Creates a new instance of AllForcesNode
     */
    public AllForcesNode(ForceSet as) {
        super(as);
        setDisplayName(NbBundle.getMessage(AllForcesNode.class, "CTL_AllForces"));
        /*for (int i=0; i<as.getSize(); i++) {
        System.out.println(as.get(i).getType());
        }*/
        getChildren().add(new Node[]{new MusclesNode(as)});
        getChildren().add(new Node[]{new ActuatorsNode(as)});
        getChildren().add(new Node[]{new ContactForcesNode(as)});
        getChildren().add(new Node[]{new OtherForcesNode(as)});
        //getChildren().add(new Node[] {new ForcesNode(as)});
        //getChildren().add(new Node[] {new TorquesNode(as)});
        //getChildren().add(new Node[] {new GeneralizedForcesNode(as)});
    }

    /**
     * Display name
     */
    public String getHtmlDisplayName() {

        return NbBundle.getMessage(AllForcesNode.class, "CTL_AllForces");
    }

    public Image getIcon(int i) {
        URL imageURL = null;
        try {
            imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/actuatorsNode.png");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        if (imageURL != null) {
            return new ImageIcon(imageURL, "").getImage();
        } else {
            return null;
        }
    }

    public Image getOpenedIcon(int i) {
        URL imageURL = null;
        try {
            imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/actuatorsNode.png");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        if (imageURL != null) {
            return new ImageIcon(imageURL, "").getImage();
        } else {
            return null;
        }
    }
}
