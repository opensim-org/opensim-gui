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
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.opensim.modeling.Marker;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.editors.BodyNameEditor;
import org.opensim.view.nodes.OpenSimObjectNode.displayOption;

/**
 *
 * @author Ayman Habib
 */
public class OneMuscleNode extends OneForceNode {
    
    private static ResourceBundle bundle = NbBundle.getBundle(OneMuscleNode.class);
    /** Creates a new instance of OneMuscleNode */
    public OneMuscleNode(OpenSimObject actuator) {
        super(actuator);
        setChildren(Children.LEAF);
        addDisplayOption(displayOption.Showable);
        addDisplayOption(displayOption.Isolatable);
    }
    public Image getIcon(int i) {
        URL imageURL;
        if (!enabled)
            imageURL = this.getClass().getResource("icons/disabledNode.png");
        else
            imageURL = this.getClass().getResource("icons/muscleNode.png");
        if (imageURL != null) { 
            return new ImageIcon(imageURL, "Actuator").getImage();
        } else {
            return null;
        }
    }
   public Image getOpenedIcon(int i) {
        return getIcon(i);
    }
    /** override createSheet to remove optimal_force property
    * 
    * @return 
    */
    @Override
    public Sheet createSheet() {
        Sheet sheet;

        sheet = super.createSheet();
        Sheet.Set set = sheet.get(Sheet.PROPERTIES);
        set.remove("optimal_force");
        return sheet;
    }

}
