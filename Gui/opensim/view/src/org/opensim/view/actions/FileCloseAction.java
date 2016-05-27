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
