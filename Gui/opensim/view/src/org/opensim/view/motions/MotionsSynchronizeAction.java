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
package org.opensim.view.motions;

import java.util.Hashtable;
import java.util.Vector;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.view.ExplorerTopComponent;

public final class MotionsSynchronizeAction extends CallableSystemAction {
    
    public boolean isEnabled() {
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        Hashtable <Node,Boolean> usedMotionNodes = new Hashtable<Node,Boolean>(selected.length);
        for(int i=0; i<selected.length; i++) {
           if(!(selected[i] instanceof OneMotionNode)) return false; // one of the nodes is not a OneMotionNode
           Node parent = selected[i].getParentNode();
           if(usedMotionNodes.containsKey(parent)) return false; // trying to pick multiple motions for the same model
           usedMotionNodes.put(parent, true);
        }
        return (selected.length>1);
    }
    /**
     * Action works by flushing all motions and then adding selected motions one at a time
     */
    public void performAction() {
       Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
       Vector<MotionsDB.ModelMotionPair> motions = new Vector<MotionsDB.ModelMotionPair>(selected.length);
       for(int i=0; i<selected.length; i++) {
            OneMotionNode node =((OneMotionNode)selected[i]);
            motions.add(new MotionsDB.ModelMotionPair(node.getModel(), node.getMotion()));
       }
       MotionsDB.getInstance().setCurrent(motions);
    }

    public String getName() {
        return NbBundle.getMessage(MotionsSynchronizeAction.class, "CTL_MotionsSynchronizeAction");
    }
    
    protected String iconResource() {
        return "org/opensim/view/camera.gif";
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
}
