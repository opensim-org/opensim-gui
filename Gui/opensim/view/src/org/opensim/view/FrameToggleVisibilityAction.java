/* -------------------------------------------------------------------------- *
 * OpenSim: FrameToggleVisibilityAction.java                                        *
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
package org.opensim.view;

import java.awt.event.ActionEvent;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.BooleanStateAction;
import org.opensim.modeling.Body;
import org.opensim.modeling.Frame;
import org.opensim.modeling.FrameGeometry;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.threejs.ModelVisualizationJson;
import static org.opensim.view.nodes.BodyToggleCOMAction.ShowCMForOneBodyNode;
import org.opensim.view.nodes.OneBodyNode;
import org.opensim.view.nodes.OneFrameNode;
import org.opensim.view.pub.ViewDB;

public final class FrameToggleVisibilityAction extends BooleanStateAction {
    
    public FrameToggleVisibilityAction(){
    }
    
    public void performAction() {
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // TODO implement action body
        OneFrameNode dNode = (OneFrameNode)selected[0];
        boolean newState = getBooleanState();
        FrameToggleVisibilityAction.ShowFrame( Frame.safeDownCast(dNode.getOpenSimObject()),
                dNode.getModelForNode(), newState);
    }
    
    
    public String getName() {
        return NbBundle.getMessage(FrameToggleVisibilityAction.class, "CTL_BodyToggleFrameAction");
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
        if( selected[0] instanceof OneFrameNode ){
            OneFrameNode dNode = (OneFrameNode)selected[0];
            Frame b = Frame.safeDownCast( dNode.getOpenSimObject() );
            Boolean curStatus = ViewDB.getInstance().getModelVisualizationJson(dNode.getModelForNode()).getFrameVisibility(b);
            super.setBooleanState(curStatus );
            return true;
        }
        return false;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        //super.actionPerformed(actionEvent);
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // TODO implement action body
        final boolean newState = !(super.getBooleanState());
        for( int i=0;  i<selected.length;  i++ ){
            Node selectedNode = selected[i];
            if( selectedNode instanceof OneFrameNode ){
                final OneFrameNode frameNode = (OneFrameNode)selectedNode;
                ToggleFrameVisisbility(frameNode, newState);
                AbstractUndoableEdit auEdit = new AbstractUndoableEdit() {
                    public boolean canUndo() {
                        return true;
                    }

                    public boolean canRedo() {
                        return true;
                    }

                    public void undo() throws CannotUndoException {
                        super.undo();
                        ToggleFrameVisisbility(frameNode, !newState);
                    }

                    public void redo() throws CannotRedoException {
                        super.redo();
                        ToggleFrameVisisbility(frameNode, newState);
                    }

                    @Override
                    public String getRedoPresentationName() {
                        return "Redo Axis visibility change";
                    }

                    @Override
                    public String getUndoPresentationName() {
                        return "Undo Axis visibility change";
                    }

                };
                ExplorerTopComponent.addUndoableEdit(auEdit);

            }
        }
        super.setBooleanState( newState );
        ViewDB.ViewDBGetInstanceRenderAll();
    }

    private void ToggleFrameVisisbility(OneFrameNode frameNode, boolean newState) {
        Model frameModel = frameNode.getModelForNode();
        FrameToggleVisibilityAction.setFrameVisibility(frameNode, newState);
    }
    
    //----------------------------------------------------------------------------- 
    static public void  setFrameVisibility( OneFrameNode frameNode, boolean showAxesTrueHideIsFalse)
    {
       if( frameNode == null ) return; 
       FrameToggleVisibilityAction.ShowFrame(Frame.safeDownCast(frameNode.getOpenSimObject()), 
               frameNode.getModelForNode(),
               showAxesTrueHideIsFalse);
    }
    
    //-------------------------------------------------------------------------
    public static void  ShowFrame( Frame frame, 
            Model model, 
            boolean showAxesTrueHideIsFalse)
    {
        ModelVisualizationJson viz = ViewDB.getInstance().getModelVisualizationJson(model);
        viz.setFrameVisibility(frame, showAxesTrueHideIsFalse);
        FrameGeometry fg = viz.getGeometryForFrame(frame);
        ViewDB.getInstance().toggleObjectDisplay(fg, showAxesTrueHideIsFalse);
    }

}
