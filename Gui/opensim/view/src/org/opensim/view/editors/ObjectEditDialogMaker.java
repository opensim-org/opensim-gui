/*
 *
 * ObjectEditDialogMaker
 * Author(s): Ayman Habib
 * Copyright (c)  2005-2006, Stanford University, Ayman Habib
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
