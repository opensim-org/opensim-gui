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
package org.opensim.simmsupport;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.StatusDisplayer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.Model;
//import org.opensim.modeling.SimmFileWriter;
import org.opensim.modeling.SimmFileWriter;
import org.opensim.view.pub.OpenSimDB;

/**
 * A Class represnting the Action of exporting an OpenSim model to SIMM's jnt format.
 * The exported model is the "Current" model in the GUI as indicated by the explorer window.
 */
public final class FileExportSIMMJntAction extends CallableSystemAction {
    
    class ExportDlgListener implements ActionListener, PropertyChangeListener
    {
        DialogDescriptor dlgOfInterest;
        ExportDlgListener(DialogDescriptor aDialog)
        {
            super();
            dlgOfInterest=aDialog;
            dlgOfInterest.addPropertyChangeListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            dlgOfInterest.setValid(!dlgOfInterest.isValid());
        }

        public void propertyChange(PropertyChangeEvent evt) {
            String name=evt.getPropertyName();
            int i=0;
        }
    }
    public void performAction() {
        // TODO implement action body
        Model mdl = OpenSimDB.getInstance().getCurrentModel();
        if (mdl != null){
            OpenSimToSIMMOptionsJPanel exportPanel = new OpenSimToSIMMOptionsJPanel();
            DialogDescriptor dlg = new DialogDescriptor(exportPanel, "Export SIMM Model");
            dlg.setValid(false);
            dlg.setButtonListener(new ExportDlgListener(dlg));
            DialogDisplayer.getDefault().createDialog(dlg).setVisible(true);
            Object userInput = dlg.getValue();
            if (((Integer)userInput).compareTo((Integer)DialogDescriptor.OK_OPTION)==0){
                String jntfileName = exportPanel.getJointFilename();
                if (jntfileName==null)
                    return;
                // Make sure we have the right extension.
                if (!jntfileName.endsWith(".jnt"))
                    jntfileName = jntfileName+".jnt";
                SimmFileWriter modelWriter=new SimmFileWriter(mdl);
                modelWriter.writeJointFile(jntfileName);

                // Now the muscles
                String mslfileName = exportPanel.getMslFilename();
                if (mslfileName==null || mslfileName.equalsIgnoreCase(""))
                    return;
                // Make sure we have the right extension.
                if (!mslfileName.endsWith(".msl"))
                    mslfileName = mslfileName+".msl";
                modelWriter.writeMuscleFile(mslfileName);

                StatusDisplayer.getDefault().setStatusText("Exported SIMM jnt & muscle files for model " + mdl.getName()+".");
            }
        }
    }
    
    public String getName() {
        return NbBundle.getMessage(FileExportSIMMJntAction.class, "CTL_ExportSIMMJntAction");
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
       return OpenSimDB.getInstance().getCurrentModel()!=null;
   }

}
