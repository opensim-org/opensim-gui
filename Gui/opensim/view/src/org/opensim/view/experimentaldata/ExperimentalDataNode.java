/*
 * ExperimentalDataNode.java
 *
 * Created on March 11, 2009, 2:25 PM
 *
 *
 *
 * Copyright (c)  2009, Stanford University and Ayman Habib. All rights reserved.
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

package org.opensim.view.experimentaldata;

import java.awt.Image;
import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.openide.util.NbBundle;
import org.opensim.view.nodes.OpenSimNode;

/**
 *
 * @author ayman
 */
public class ExperimentalDataNode extends OpenSimNode{
    
    /** Creates a new instance of ExperimentalDataNode */
    public ExperimentalDataNode() {
    }

    protected static ResourceBundle bundle = NbBundle.getBundle(ExperimentalForceNode.class);

    protected AnnotatedMotion dMotion;
    protected ExperimentalDataObject dataObject;
    /**
     * Icon for the node, same as OpenSimNode
     **/
    public Image getIcon(int i) {
        URL imageURL = null;
        try {
            imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/motionsNode.png");
        }  catch (ClassNotFoundException ex) {
           ex.printStackTrace();
        }
        if (imageURL != null) {
            return new ImageIcon(imageURL, "").getImage();
        }  else {
           return null;
        }
    }

    
    public Image getOpenedIcon(int i) {
        URL imageURL = null;
        try {
            imageURL = Class.forName("org.opensim.view.nodes.OpenSimNode").getResource("/org/opensim/view/nodes/icons/motionsNode.png");
        }  catch (ClassNotFoundException ex) {
           ex.printStackTrace();
        }
        if (imageURL != null) {
            return new ImageIcon(imageURL, "").getImage();
        }  else {
           return null;
        }
    }
    public Action[] getActions(boolean b) {
        Action[] nodeActions=null;
            try {
                nodeActions = new Action[] {
                           (ExperimentalObjectDisplayShowAction) ExperimentalObjectDisplayShowAction.findObject(
                           (Class<org.opensim.view.experimentaldata.ExperimentalObjectDisplayShowAction>)Class.forName("org.opensim.view.experimentaldata.ExperimentalObjectDisplayShowAction"), 
                                   true),
                           (ExperimentalObjectDisplayShowOnlyAction) ExperimentalObjectDisplayShowOnlyAction.findObject(
                           (Class<org.opensim.view.experimentaldata.ExperimentalObjectDisplayShowOnlyAction>)Class.forName("org.opensim.view.experimentaldata.ExperimentalObjectDisplayShowOnlyAction"), 
                                   true),
                           (ExperimentalObjectDisplayHideAction) ExperimentalObjectDisplayHideAction.findObject(
                           (Class<org.opensim.view.experimentaldata.ExperimentalObjectDisplayHideAction>)Class.forName("org.opensim.view.experimentaldata.ExperimentalObjectDisplayHideAction"), 
                                   true),
                            null,
                            (ExperimentalObjectDisplayShowTrailAction) ExperimentalObjectDisplayShowTrailAction.findObject(
                           (Class<org.opensim.view.experimentaldata.ExperimentalObjectDisplayShowTrailAction>)Class.forName("org.opensim.view.experimentaldata.ExperimentalObjectDisplayShowTrailAction"), 
                                   true),
                };
                
                
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        return nodeActions;
    }

    public ExperimentalDataObject getDataObject() {
        return dataObject;
    }

    public void setDataObject(ExperimentalDataObject dataObject) {
        this.dataObject = dataObject;
    }

    public AnnotatedMotion getDMotion() {
        return dMotion;
    }
}
