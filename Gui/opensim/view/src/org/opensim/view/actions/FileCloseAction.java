/* -------------------------------------------------------------------------- *
 * OpenSim: FileCloseAction.java                                              *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib                                                     *
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

package org.opensim.view.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.Model;
import org.opensim.view.FileSaveModelAction;
import org.opensim.view.SingleModelGuiElements;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.OpenSimDB.CloseModelDefaultAction;
import org.opensim.view.pub.ViewDB;

public final class FileCloseAction extends CallableSystemAction {
   static boolean noGFX=false;

/* Close the model. If it's locked, tell the user and abort. If it's
 * modified, ask the user to save the changes, with the option to cancel
 * the close. If the model is not closed, return false.
 */
public static boolean closeModel(Model model) {
   
   return closeModel(model, false);
}

        public static boolean closeModel(Model model, boolean firstOfMany) {
        if(model==null) return true;


        SingleModelGuiElements guiElem = OpenSimDB.getInstance().getModelGuiElements(model);
        
        // Do not allow the model to be closed if it is locked.
        if (guiElem != null && guiElem.isLocked()) {
           NotifyDescriptor dlg = new NotifyDescriptor.Message(model.getName() + " is currently in use by " +
                   guiElem.getLockOwner() + " and cannot be closed.", NotifyDescriptor.INFORMATION_MESSAGE);
           DialogDisplayer.getDefault().notify(dlg);
           return false;
        }
        
        // Confirm closing
        if (guiElem != null && guiElem.getUnsavedChangesFlag()) {
           CloseModelDefaultAction curAction = OpenSimDB.getCurrentCloseModelDefaultAction();
           if (curAction==CloseModelDefaultAction.PROMPT && saveAndConfirmClose(model, firstOfMany) == false)
              return false;
           else if (curAction==CloseModelDefaultAction.SAVE)
               FileSaveModelAction.saveOrSaveAsModel(model, false);
        }
        OpenSimDB.getInstance().removeModel(model);
        
        return true;
    }

   private static boolean saveAndConfirmClose(final Model model, boolean firstOfMany)
   {
       final ConfirmSaveDiscardJPanel confirmPanel = new ConfirmSaveDiscardJPanel(firstOfMany);
       JButton cancelButton = new JButton("Cancel");
       DialogDescriptor confirmDialog = 
                    new DialogDescriptor(confirmPanel, 
                        "Confirm Save",
                        true,
                        new Object[]{new JButton("Save"), new JButton("Discard"), cancelButton},
                        cancelButton,
                        0, null, new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                String cmd = e.getActionCommand();
                boolean remember = confirmPanel.rememberUserChoice();
                if (remember){
                    if (cmd.equalsIgnoreCase("Save")){
                        OpenSimDB.setCurrentCloseModelDefaultAction(CloseModelDefaultAction.SAVE);
                        FileSaveModelAction.saveOrSaveAsModel(model, false);
                    }
                    else if (cmd.equalsIgnoreCase("Discard")){
                        OpenSimDB.setCurrentCloseModelDefaultAction(CloseModelDefaultAction.DISCARD);                       
                    }
                 } // Even if user doesn't want to remember decision, save Model
                else if (cmd.equalsIgnoreCase("Save")){
                    FileSaveModelAction.saveOrSaveAsModel(model, false);
                }
            }
        });
       confirmDialog.setClosingOptions(null);
       DialogDisplayer.getDefault().createDialog(confirmDialog).setVisible(true);
       Object dlgReturn = confirmDialog.getValue();
       // We'll get here after user closes the dialog.
       boolean closed = ((dlgReturn instanceof Integer) && (((Integer)dlgReturn).intValue()==-1));
       return (!closed && ! cancelButton.equals(dlgReturn));
       /*
       NotifyDescriptor dlg = new NotifyDescriptor.Confirmation("Do you want to save the changes to " + model.getName() + "?", "Save model?");
      Object userSelection = DialogDisplayer.getDefault().notify(dlg);
      if (((Integer)userSelection).intValue() == ((Integer)NotifyDescriptor.OK_OPTION).intValue()) {
         return FileSaveModelAction.saveOrSaveAsModel(model);
      } else if (((Integer)userSelection).intValue() == ((Integer)NotifyDescriptor.NO_OPTION).intValue()) {
         return true;
      } else {
         return false;
      }*/
   }

   public void performAction() {
      closeModel(OpenSimDB.getInstance().getCurrentModel());      
   }
   
   public String getName() {
      return NbBundle.getMessage(FileCloseAction.class, "CTL_FileCloseAction");
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
       return OpenSimDB.getInstance().getCurrentModel()!=null;
   }
   
}
