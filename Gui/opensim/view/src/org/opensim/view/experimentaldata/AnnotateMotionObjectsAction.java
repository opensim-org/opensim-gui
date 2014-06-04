package org.opensim.view.experimentaldata;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.pub.ViewDB;
import vtk.vtkCaptionActor2D;

public final class AnnotateMotionObjectsAction extends CallableSystemAction {
    
    public void performAction() {
        /*
      Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
      for(int i=0; i < selected.length; i++){
         if(selected[i] instanceof ExperimentalDataNode) {
             ExperimentalDataNode node= (ExperimentalDataNode) selected[i];
             ExperimentalDataObject obj=node.getDataObject();
             // Tell MotionDisplayer to hide it
             obj.getMyGlyph().show(obj.getGlyphIndex());
             
             double[] location = new double[3];
             obj.getMyGlyph().getLocation(obj.getGlyphIndex(), location);
             vtkCaptionActor2D theCaption = new vtkCaptionActor2D();
             theCaption.SetAttachmentPoint(location);
             theCaption.SetCaption(obj.getBaseName());
             ViewDB.getInstance().getCurrentModelWindow().getCanvas().GetRenderer().AddActor2D(theCaption);
             obj.setDisplayed(true);
             obj.getMyGlyph().setModified();
         }
      } 
      ViewDB.getInstance().setQuery(!ViewDB.getInstance().isQuery()); //Toggle */
    }
    
    public String getName() {
        return NbBundle.getMessage(AnnotateMotionObjectsAction.class, "CTL_AnnotateMotionObjectsAction");
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
