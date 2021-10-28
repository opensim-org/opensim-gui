/* -------------------------------------------------------------------------- *
 * OpenSim: BodyToggleCOMAction.java                                          *
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
package org.opensim.view.nodes;

import java.awt.event.ActionEvent;
import org.json.simple.JSONObject;
import org.openide.nodes.Node;
import org.openide.util.*;
import org.openide.util.actions.BooleanStateAction;
import org.opensim.modeling.Body;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.threejs.BodyVisualizationJson;
import org.opensim.threejs.ModelVisualizationJson;
//import org.opensim.view.FrameToggleVisibilityAction;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.pub.ViewDB;

public final class BodyToggleCOMAction extends BooleanStateAction {
    
    public BodyToggleCOMAction() {        
    }
    
    public void actionPerformed(ActionEvent actionEvent) {
        // TODO implement action body
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        // TODO implement action body
        boolean newState = !super.getBooleanState();
        for( int i=0; i<selected.length; i++ ){
            Node selectedNode = selected[i];
            if( selectedNode instanceof OneBodyNode )
                this.ShowCMForOneBodyNode( (OneBodyNode)selectedNode, newState, false );
            else if( selectedNode instanceof BodiesNode ){
                Model model=((BodiesNode)selectedNode).getModelForNode();
                ModelVisualizationJson modelVis = ViewDB.getInstance().getModelVisualizationJson(model);
                modelVis.setShowCom(newState);
                JSONObject command = modelVis.createSetVisibilityCommandForUUID(newState, modelVis.getComObjectUUID());
                ViewDB.getInstance().sendVisualizerCommand(command);
            }
        }
        super.setBooleanState( newState );
        ViewDB.renderAll();
   }
    
   //-------------------------------------------------------------------------
   public static boolean  IsShowCMForBody( OpenSimObject openSimObjectAssociatedWithBody )
   {
      //BodyDisplayer rep = BodyToggleFrameAction.GetBodyDisplayerForBody( openSimObjectAssociatedWithBody );  
      return false;//rep==null ? false : rep.isShowCOM();
   }
    
    
   //----------------------------------------------------------------------------- 
   static public void  ShowCMForOneBodyNode( OneBodyNode oneBodyNode, boolean showCMIsTrueHideIsFalse, boolean renderAll )
   {
      if( oneBodyNode == null ) return; 
      BodyToggleCOMAction.ShowCMForBody( oneBodyNode.getOpenSimObject(), showCMIsTrueHideIsFalse, renderAll );
   }
   
   
   //-------------------------------------------------------------------------
   public static void  ShowCMForBody( OpenSimObject openSimObjectAssociatedWithBody, boolean showCMIsTrueHideIsFalse, boolean renderAll )
   {
      Body bdy = Body.safeDownCast(openSimObjectAssociatedWithBody);
      ModelVisualizationJson modelVis = ViewDB.getInstance().getModelVisualizationJson(bdy.getModel());
      BodyVisualizationJson rep = modelVis.getBodyRep(bdy);
      if( rep != null ){
          rep.setShowCom(showCMIsTrueHideIsFalse ); 
          JSONObject command = modelVis.createSetVisibilityCommandForUUID(showCMIsTrueHideIsFalse, rep.getComObjectUUID());
          ViewDB.getInstance().sendVisualizerCommand(command);
          if( renderAll ) ViewDB.renderAll();
      }    
      
   }
    
    
    public String getName() {
        return NbBundle.getMessage(BodyToggleCOMAction.class, "CTL_BodyToggleCOMAction");
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        super.putValue("noIconInMenu", Boolean.TRUE);
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
    public boolean isEnabled() {
        Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
        if(selected.length!=1) return false; // only if a single item is selected
        // Action shouldn't be available otherwise
        Model model=((OpenSimNode)selected[0]).getModelForNode();
        if( selected[0] instanceof OneBodyNode ){
            OneBodyNode dNode = (OneBodyNode)selected[0];
            BodyVisualizationJson rep = ViewDB.getInstance().getModelVisualizationJson(model).getBodyRep(dNode.comp);
            if( rep != null ) super.setBooleanState( rep.isShowCom());
        }
        else if (selected[0] instanceof BodiesNode){
                super.setBooleanState( ViewDB.getInstance().getModelVisualizationJson(model).isShowCom());
        }
        return true;
    }
    

}
