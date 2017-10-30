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
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.BooleanStateAction;
import org.opensim.modeling.Body;
import org.opensim.modeling.Frame;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.nodes.OneFrameNode;
import org.opensim.view.pub.ViewDB;

public final class FrameToggleVisibilityAction extends BooleanStateAction {
    
    public FrameToggleVisibilityAction(){
    }
    
    public void performAction() {
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // TODO implement action body
        OneFrameNode dNode = (OneFrameNode)selected[0];
        boolean newState = super.getBooleanState();
        FrameToggleVisibilityAction.ShowAxesForBody( dNode.getOpenSimObject(), newState, false );
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
//            super.setBooleanState( b.getDisplayer().getShowAxes() );
            return true;
        }
        return false;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        //super.actionPerformed(actionEvent);
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // TODO implement action body
        boolean newState = !super.getBooleanState();
        for( int i=0;  i<selected.length;  i++ ){
            Node selectedNode = selected[i];
            if( selectedNode instanceof OneFrameNode )
                 FrameToggleVisibilityAction.ShowAxesForOneBodyNode( (OneFrameNode)selectedNode, newState, false );
        }
        super.setBooleanState( newState );
        ViewDB.ViewDBGetInstanceRenderAll();
    }
  
     
    //-------------------------------------------------------------------------
    public static boolean  IsShowAxesForBody( OpenSimObject openSimObjectAssociatedWithBody )
    {
        return false;
        /*
       BodyDisplayer rep = FrameToggleVisibilityAction.GetBodyDisplayerForBody( openSimObjectAssociatedWithBody );  
       return rep==null ? false : rep.isShowAxes();*/
    }
    
    
    //----------------------------------------------------------------------------- 
    static public void  ShowAxesForOneBodyNode( OneFrameNode oneBodyNode, boolean showAxesTrueHideIsFalse, boolean renderAll )
    {
       if( oneBodyNode == null ) return; 
       FrameToggleVisibilityAction.ShowAxesForBody( oneBodyNode.getOpenSimObject(), showAxesTrueHideIsFalse, renderAll );
    }
    
    //-------------------------------------------------------------------------
    public static void  ShowAxesForBody( OpenSimObject openSimObjectAssociatedWithBody, boolean showAxesTrueHideIsFalse, boolean renderAll )
    {
        /*
       BodyDisplayer rep = FrameToggleVisibilityAction.GetBodyDisplayerForBody( openSimObjectAssociatedWithBody );
       if( rep != null )
       {
           rep.setShowAxes( showAxesTrueHideIsFalse );
           if( renderAll ) ViewDB.ViewDBGetInstanceRenderAll();
       }    */ 
    }
    
    
    //-------------------------------------------------------------------------
    /*
    public static BodyDisplayer  GetBodyDisplayerForBody( OpenSimObject openSimObjectAssociatedWithBody )
    {
       Body b = Body.safeDownCast( openSimObjectAssociatedWithBody );
       SingleModelVisuals viz = ViewDB.getInstance().getModelVisuals( b.getModel() );
       if (viz == null) return null;
       vtkProp3D visuals = viz.getVtkRepForObject(b);
       return ( visuals instanceof BodyDisplayer ) ? (BodyDisplayer)visuals : null;
    }
    */
}
