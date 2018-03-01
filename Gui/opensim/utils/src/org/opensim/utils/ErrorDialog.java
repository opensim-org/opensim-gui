/* -------------------------------------------------------------------------- *
 * OpenSim: ErrorDialog.java                                                  *
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

package org.opensim.utils;

import java.awt.Dialog;
import java.io.IOException;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

public class ErrorDialog {
    
    /**
     * Display error dialog with contents from IOException
     * 
     * @param title dialog title
     * @param message text message explaining the exception
     * @param ex IOException
     */
   public static void displayIOExceptionDialog(String title, String message, IOException ex) {
      ErrorPanel panel = new ErrorPanel();
      panel.setText(message, ex.getMessage());
      DialogDescriptor dlg = new DialogDescriptor(panel, title);
      dlg.setOptions(new Object[]{DialogDescriptor.OK_OPTION});
      Dialog dialog = DialogDisplayer.getDefault().createDialog(dlg);
      dialog.setVisible(true);
   }
    /**
     * Popup a dialog showing the passed in message
     * @param message 
     */
    public static void showMessageDialog(String message) {
        DialogDisplayer.getDefault().notify(
                new NotifyDescriptor.Message(message));
    }
    
    /*
    * Display Exceptionerror message ina friendly dialog
    */
    public static void displayExceptionDialog(IOException ex){
        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(ex.getMessage()));
        ex.printStackTrace();
    }
}
