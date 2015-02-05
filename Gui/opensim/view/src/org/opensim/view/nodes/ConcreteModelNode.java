package org.opensim.view.nodes;

import javax.swing.Action;
import org.openide.nodes.Node;
import org.opensim.modeling.Model;
import org.opensim.view.experimentaldata.ModelForExperimentalData;
import org.opensim.view.*;
import org.opensim.view.motions.FileLoadMotionAction;
import org.opensim.view.nodes.OpenSimObjectNode.displayOption;
import org.opensim.view.pub.ViewDB;

/**
 * Node class to wrap Model objects
 */
public class ConcreteModelNode extends OpenSimObjectNode {
	boolean isDataHolderOnly = false;
    public ConcreteModelNode(Model m) {
        super(m);
	isDataHolderOnly = (m instanceof ModelForExperimentalData);
	if (!isDataHolderOnly)
	{   // Data Import model has no engine or Actuators
            getChildren().add(new Node[] {new FramesNode(m)});
            getChildren().add(new Node[] {new BodiesNode(m.getBodySet())});
            getChildren().add(new Node[] {new JointsNode(m.getJointSet())});
            getChildren().add(new Node[] {new ConstraintsNode(m.getConstraintSet())});        
            getChildren().add(new Node[] {new ContactGeometriesNode(m.getContactGeometrySet())});
            getChildren().add(new Node[] {new AllForcesNode(m.getForceSet())});
            getChildren().add(new Node[] {new MarkersNode(m.getMarkerSet(), m)});
            getChildren().add(new Node[] {new ControllersNode(m.getControllerSet())});
            getChildren().add(new Node[] {new ProbesNode(m.getProbeSet())});
            getChildren().add(new Node[] {new OtherComponentsNode(m.getMiscModelComponentSet())});
            
        }
        addDisplayOption(displayOption.Isolatable);
        addDisplayOption(displayOption.Showable);
    }
    public Model getModel() {
        return (Model) getOpenSimObject();
    }
    /**
     * Actions available in model node popup.
     * @todo replace new'ing with the findObject substitute to avoid runtime "informational" exceptions
     */
    public Action[] getActions(boolean b) {
        Action[] classSpecificActions=null;
        try {
			if (isDataHolderOnly)
			{
				classSpecificActions = new Action[]{
					(ModelDisplayMenuAction) ModelDisplayMenuAction.findObject(
							(Class)Class.forName("org.opensim.view.ModelDisplayMenuAction"), true),
					(ModelCloseSelectedAction) ModelCloseSelectedAction.findObject(
							(Class)Class.forName("org.opensim.view.nodes.ModelCloseSelectedAction"), true)
				};
			}
			else 
                classSpecificActions = new Action[]{
                (ModelMakeCurrentAction) ModelMakeCurrentAction.findObject(
                        (Class)Class.forName("org.opensim.view.nodes.ModelMakeCurrentAction"), true),
                (ModelRenameAction) ModelRenameAction.findObject(
                        (Class)Class.forName("org.opensim.view.ModelRenameAction"), true),
                (ModelDisplayMenuAction) ModelDisplayMenuAction.findObject(
                        (Class)Class.forName("org.opensim.view.ModelDisplayMenuAction"), true),
                (ModelInfoAction) ModelInfoAction.findObject(
                        (Class)Class.forName("org.opensim.view.nodes.ModelInfoAction"), true),
                null,
		(ModelSaveSelectedAction) ModelSaveSelectedAction.findObject(
			(Class)Class.forName("org.opensim.view.nodes.ModelSaveSelectedAction"), true),
  		(ModelSaveAsSelectedAction) ModelSaveSelectedAction.findObject(
			(Class)Class.forName("org.opensim.view.nodes.ModelSaveAsSelectedAction"), true),
                null,
                (ModelCloseSelectedAction) ModelCloseSelectedAction.findObject(
                        (Class)Class.forName("org.opensim.view.nodes.ModelCloseSelectedAction"), true),
                null,
                (FileLoadMotionAction) FileLoadMotionAction.findObject(
                        (Class)Class.forName("org.opensim.view.motions.FileLoadMotionAction"), true)
                
            };
      } catch(ClassNotFoundException e){
            
        }
        return classSpecificActions;
    }

    public String getHtmlDisplayName() {
        String retValue;
        
        retValue = super.getHtmlDisplayName();
        if (getModel()==ViewDB.getCurrentModel())
            retValue="<b>"+retValue+"</b>";
            
        return retValue;
    }

   public Action getPreferredAction() {
      Action act=null;
      try {
         act =(ModelMakeCurrentAction) ModelMakeCurrentAction.findObject(
                    (Class)Class.forName("org.opensim.view.nodes.ModelMakeCurrentAction"), true);
      } catch(ClassNotFoundException e){
         
 }
      return act;
   }

    @Override
    public void updateSelfFromObject() {
        for(int i=0; i<getChildren().getNodesCount(); i++){
            Node node = getChildren().getNodeAt(i);
            if (node instanceof OpenSimObjectSetNode){
                OpenSimObjectSetNode setNode = (OpenSimObjectSetNode) node; 
                setNode.updateSelfFromObject();
            }
        }        
    }
}
