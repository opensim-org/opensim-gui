/* -------------------------------------------------------------------------- *
 * OpenSim: FileSaveModelAction.java                                          *
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

package org.opensim.view;

import javax.swing.JPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.Model;
import org.opensim.view.pub.ViewDB;

public final class FileSaveModelAction extends CallableSystemAction {
   
    public static boolean saveOrSaveAsModel(Model model, boolean forceSaveAs) {
      if(model==null) return false;
      if (model.getDocumentFileVersion()!= -1 &&  model.getDocumentFileVersion() <30500){
          // Show dialog warn new format and give optopn to saveAs
        javax.swing.JTextArea jTextArea1 = new javax.swing.JTextArea();
        //jTextArea1.setColumns(20);
        jTextArea1.setFont(jTextArea1.getFont());
        String message = "Model originated from an earlier version of OpenSim. If you save, \n" +
                         "OpenSim will overwrite the model file, and you will not be able to use\n" +
                         "the model with earlier versions of OpenSim.\n\n" +
                         "Choose 'OK' to overwrite or 'Cancel' to abort ( use Save-As instead).";
        jTextArea1.setText(message);
        //jTextArea1.setRows(4);
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setEnabled(true);
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new javax.swing.BoxLayout(containerPanel, javax.swing.BoxLayout.Y_AXIS));
        containerPanel.add(jTextArea1);
        DialogDescriptor dd = new DialogDescriptor(containerPanel, "Overwrite old model file?");
        DialogDisplayer.getDefault().createDialog(dd).setVisible(true);
        Object userInput = dd.getValue();
        if (((Integer)userInput).compareTo((Integer)DialogDescriptor.CANCEL_OPTION)==0){
                return false;
        }
      }
      if(!model.getInputFileName().equals("") && !forceSaveAs) {
         FileSaveAsModelAction.saveModel(model, model.getInputFileName());
         return true;
      }
      else return FileSaveAsModelAction.saveAsModel(model);
    }

    public void performAction() {
      Model model = ViewDB.getInstance().getCurrentModel();
      if (model != null) saveOrSaveAsModel(model, false);
    }
    
    public String getName() {
        return NbBundle.getMessage(FileSaveModelAction.class, "CTL_SaveModelAction");
    }
    
    protected void initialize() {
        super.initialize();
        // see org.openide.util.actions.SystemAction.iconResource() javadoc for more details
        putValue("noIconInMenu", Boolean.TRUE);
        //setEnabled(false);
        //ViewDB.getInstance().registerModelCommand(this);
        
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }

   public boolean isEnabled() {
      return ViewDB.getInstance().getCurrentModel()!=null;
   }
    
}
