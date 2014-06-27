package org.opensim.annotationtool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.modeling.Model;
import org.opensim.view.SingleModelVisuals;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;
import vtk.vtkFollower;
import vtk.vtkPolyDataMapper;
import vtk.vtkVectorText;
/**
 * A sample plugin/tool that's used to mark the current model by displaying its
 * name and hiding all other models for 5 seconds.
 * Using text rather than colors or an arrow to identify model.
 */
public final class AnnotationAction extends CallableSystemAction {
    
    public void performAction() {
        // Create a Text actor and associate it with th
        final Model mdl = OpenSimDB.getInstance().getCurrentModel();
        if (mdl ==null)
            return;
        final SingleModelVisuals vis =ViewDB.getInstance().getModelVisuals(mdl);
        vtkVectorText atext = new vtkVectorText();
        atext.SetText(mdl.getName());
        vtkPolyDataMapper textMapper = new vtkPolyDataMapper();
        textMapper.SetInput(atext.GetOutput());
        final vtkFollower textActor = new vtkFollower();
        textActor.SetMapper(textMapper);
        textActor.SetScale(0.1, 0.1, 0.1);
        textActor.AddPosition(0., 0., 0.);
        vis.addUserObject(textActor);
        //ViewDB.getInstance().hideOthers(mdl, true);
        ViewDB.getInstance().repaintAll();
        // Create timer that will undo the change in 5000 msec.
        Timer restoreTimer = new Timer(5000, new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                vis.removeUserObject(textActor);
                //ViewDB.getInstance().hideOthers(mdl, false);
                ViewDB.getInstance().repaintAll();
            }});
        restoreTimer.setRepeats(false);
        restoreTimer.start();
        
    }
    
    public String getName() {
        return NbBundle.getMessage(AnnotationAction.class, "CTL_AnnotationAction");
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
