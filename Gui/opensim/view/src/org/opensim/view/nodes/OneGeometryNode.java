/*
 *
 * OneContactGeometryNode
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

import java.awt.Color;
import java.awt.Image;
import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import static org.openide.nodes.Sheet.createExpertSet;
import org.openide.util.NbBundle;
import org.opensim.modeling.AbstractProperty;
import org.opensim.modeling.Appearance;
import org.opensim.modeling.DecorativeGeometry;
import org.opensim.modeling.Geometry;
import org.opensim.modeling.Model;
import org.opensim.modeling.SurfaceProperties;
import org.opensim.modeling.Vec3;
import org.opensim.view.ColorableInterface;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.editors.DisplayPreferenceEditor;
import org.opensim.view.nodes.OpenSimObjectNode.displayOption;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman Habib
 */
public class OneGeometryNode extends OneComponentWithGeometryNode {
    
    private static ResourceBundle bundle = NbBundle.getBundle(OneGeometryNode.class);
    /**
    * Creates a new instance of OneContactForceNode
    */
    public OneGeometryNode(Geometry cg) {
        super(cg);
        String gName=cg.getName();
        if (cg.getName().equalsIgnoreCase("")){
            gName=cg.getName();
            setDisplayName(cg.getAbsolutePathName());
            setName(cg.getName());
        }
        setChildren(Children.LEAF);
        addDisplayOption(displayOption.Colorable);
        addDisplayOption(displayOption.Isolatable);
        addDisplayOption(displayOption.Showable);
    }
    public Image getIcon(int i) {
        URL imageURL = this.getClass().getResource("icons/displayGeometryNode.png");
        if (imageURL != null) {
            return new ImageIcon(imageURL, "Display Geometry").getImage();
        } else {
            return null;
        }
    }
   public Image getOpenedIcon(int i) {
        return getIcon(i);
    }

    public Action[] getActions(boolean b) {
        // Get actions from parent (generic object menu for review, display)
        Action[] superActions = (Action[]) super.getActions(b);
        return superActions;
    }

    public String getHtmlDisplayName() {
        String retValue;
        
        retValue = super.getHtmlDisplayName();
        if (retValue.equalsIgnoreCase("")){
            retValue = ((Geometry)getOpenSimObject()).getName();
        }
        return retValue;
    }
    
}
