/* -------------------------------------------------------------------------- *
 * OpenSim: ObjectEditDialogMaker.java                                        *
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
/*
 *
 * ObjectEditDialogMaker
 * Author(s): Ayman Habib
 */
package org.opensim.view.editors;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.ModelWindowVTKTopComponent;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman Habib
 */
public class ObjectEditDialogMaker {
    ObjectPropertyViewerPanel propertiesEditorPanel=null;
    AbstractEditorPanel       typeSpecificEditorPanel=null;
    DialogDescriptor          topDialog;
    OpenSimObject             objectToEdit;
    JButton                   confirmButton = new JButton("OK");
    /**
     * Creates a new instance of ObjectEditDialogMaker
     */
    public ObjectEditDialogMaker(OpenSimObject object, ModelWindowVTKTopComponent owner, boolean allowEdit, String confirmButtonText) {
        // If allowEdit is true,aAssume we're editing properties from a file, so we'll call it "Save" instead of "OK"
        confirmButton.setText(confirmButtonText);

        objectToEdit = object;
        propertiesEditorPanel = new ObjectPropertyViewerPanel(object, allowEdit);
        JPanel topDialogPanel = new JPanel();
        topDialogPanel.setLayout(new BoxLayout(topDialogPanel, BoxLayout.Y_AXIS));

        propertiesEditorPanel.setAlignmentY(0);
        propertiesEditorPanel.setAlignmentX(0);
        topDialogPanel.add(propertiesEditorPanel);

        // @todo Check that the object is visible first to avoid crashing downstream
        String name = allowEdit ? "Property Editor" : "Property Viewer";
        topDialog = new DialogDescriptor(topDialogPanel, name);
        topDialog.setOptions(new Object[]{confirmButton, DialogDescriptor.CANCEL_OPTION});
        // Make undoredo
        DialogDisplayer.getDefault().createDialog(topDialog).setVisible(true);
    }
    /**
     * Just review, no edit
     *
     * @todo handle the case of null owner (if no ModelWindowVTKTopComponent is open)
     */
    public ObjectEditDialogMaker(OpenSimObject object, ModelWindowVTKTopComponent owner) {
        this(object, owner, false, "OK");
    }
    
    /**
     * Non visible objects
     */
     public ObjectEditDialogMaker(OpenSimObject object, boolean allowEdit, String confirmButtonText) {
        this(object, null, allowEdit, confirmButtonText);
     }

     public ObjectEditDialogMaker(OpenSimObject object, boolean allowEdit) {
        this(object, null, allowEdit, "OK");
     }

     public static boolean editFile(String fileName) {
        OpenSimObject obj = OpenSimObject.makeObjectFromFile(fileName);
        if(obj!=null) {
           if(new ObjectEditDialogMaker(obj, true, "Save...").process()) {
              obj.print(fileName);
              return true;
           }
        }
        else DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Could not construct an object from the specified file."));
        return false;
     }
     
    /**
     * process handles the closing of the Editor dialog and calls corresponding AbstractEditor methods
     * It also return a boolean true for confirm, false otherwise
     */
    public boolean process() {
        Object userInput = topDialog.getValue();
        boolean confirm = (userInput==confirmButton);
        if(typeSpecificEditorPanel!=null) {
           if(confirm) typeSpecificEditorPanel.confirmEdit();
           else typeSpecificEditorPanel.cancelEdit();
        }
        return confirm;
    }

}
