/* -------------------------------------------------------------------------- *
 * OpenSim: ObjectGenericReviewAction.java                                    *
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

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.view.editors.ObjectEditDialogMaker;
import org.opensim.view.nodes.OpenSimObjectNode;
import org.opensim.view.pub.ViewDB;


public final class ObjectGenericReviewAction  extends CallableSystemAction {
   
    @Override
   public boolean isEnabled() {
      Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
      return selected.length==1;
   }
   
    @Override
   public void performAction() {
      Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
      if( selected.length == 1 ) {
         OpenSimObjectNode osimObjectNode = (OpenSimObjectNode) selected[0];
         ModelWindowVTKTopComponent ownerWindow = ViewDB.getInstance().getCurrentModelWindow();
         /* move to a separate edit command rather than hijacking current  command
         // If osimObjectNode is a rigid body, open the easy-to-use rigid body property editor (also provides the older table version). 
         if( osimObjectNode instanceof OneBodyNode )
           LSJava.LSPropertyEditors.LSPropertyEditorRigidBody.NewLSPropertyEditorRigidBody( (OneBodyNode)osimObjectNode, ownerWindow );
         
         // If osimObjectNode is a joint (connection), open the easy-to-use joint property editor (also provides the older table version). 
         else if( osimObjectNode instanceof OneJointNode )
           new LSJava.LSPropertyEditors.LSPropertyEditorJoint( (OneJointNode)osimObjectNode, ownerWindow );

         // Otherwise create older editor window to edit the properties (this is opened from user's selection of Navigator window).
         else {
          * 
          */
            boolean allowEdit = false;
            ObjectEditDialogMaker editorDialog = new ObjectEditDialogMaker( osimObjectNode.getOpenSimObject(), ownerWindow, allowEdit, "OK" ); 
            editorDialog.process();
         //}

      } 
      else { // Should never happen
         DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Review of multiple objects is not supported."));
      }
   }
   
   public String getName() {
      return "Property Viewer";
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
   
}
