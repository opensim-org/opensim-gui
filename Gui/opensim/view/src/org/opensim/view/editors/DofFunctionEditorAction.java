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
package org.opensim.view.editors;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.opensim.modeling.Coordinate;
import org.opensim.modeling.TransformAxis;
import org.opensim.modeling.Function;
import org.opensim.modeling.Model;
import org.opensim.modeling.Units;
import org.opensim.view.ExplorerTopComponent;
import org.opensim.view.functionEditor.FunctionEditorTopComponent;
import org.opensim.view.functionEditor.FunctionEditorTopComponent.FunctionEditorOptions;
import org.opensim.view.nodes.DofFunctionNode;
import org.opensim.view.pub.OpenSimDB;

/**
 * Action which shows FunctionEditor component.
 */
public class DofFunctionEditorAction extends AbstractAction {

	private TransformAxis dof = null;

	public DofFunctionEditorAction(TransformAxis dof) {
		super(NbBundle.getMessage(DofFunctionEditorAction.class, "CTL_FunctionEditorAction"));
		this.dof = dof;
		//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(FunctionEditorTopComponent.ICON_PATH, true)));
	}

	public void actionPerformed(ActionEvent evt) {
		FunctionEditorTopComponent functionEditor = (FunctionEditorTopComponent)FunctionEditorTopComponent.findInstance();
		Node[] selected = ExplorerTopComponent.findInstance().getExplorerManager().getSelectedNodes();
		if (selected.length > 0 && selected[0] instanceof DofFunctionNode && dof != null) {
			DofFunctionNode funcNode = (DofFunctionNode)selected[0];
			Function currentFunction = functionEditor.getFunction();
			Model currentModel = OpenSimDB.getInstance().getCurrentModel();
			Function newFunction = Function.safeDownCast(funcNode.getOpenSimObject());
			Model newModel = dof.getJoint().getModel();
			if (newFunction != null && Function.getCPtr(newFunction) != Function.getCPtr(currentFunction)) {
				if (Model.getCPtr(newModel) != Model.getCPtr(currentModel)) {
					Object[] options = { "OK" };
					int answer = JOptionPane.showOptionDialog(functionEditor,
							"You can only edit functions that are in the current model.",
							"Function Editor",
							JOptionPane.DEFAULT_OPTION,
							JOptionPane.WARNING_MESSAGE,
							null,
							options,
							options[0]);
				} else {
				    functionEditor.addChangeListener(new DofFunctionEventListener());
				    FunctionEditorOptions options = new FunctionEditorOptions();
				    options.title = dof.getJoint().getName();
                                    
				    if (dof.getCoordinateNamesInArray().getSize() > 0) {
					//TODO: For now, just get the first coordinate name.
					String coordName = dof.getCoordinateNamesInArray().getitem(0);
					Coordinate coord = Coordinate.safeDownCast(dof.getJoint().getComponent(coordName));
					if (coord != null) {
					    // Determine the units of the X axis
					    if (coord.getMotionType() == Coordinate.MotionType.Rotational) {
						options.XUnits = new Units(Units.UnitType.Radians);
						options.XDisplayUnits = new Units(Units.UnitType.Degrees);
						options.XLabel = coord.getName() + " (deg)";
					    } else {
						options.XUnits = new Units(Units.UnitType.Meters);
						options.XDisplayUnits = options.XUnits;
						options.XLabel = coord.getName() + " (m)";
					    }
					    // Determine the units of the Y axis
					    if (dof.getName().startsWith("translation")) {
						options.YUnits = new Units(Units.UnitType.Meters);
						options.YDisplayUnits = options.YUnits;
					    } else {
						options.YUnits = new Units(Units.UnitType.Radians);
						options.YDisplayUnits = new Units(Units.UnitType.Degrees);
					    }
					    options.YLabel = options.YDisplayUnits.getLabel();
					}
				    }
				    functionEditor.open(newModel, dof, null, newFunction, options);
				    functionEditor.requestActive();
				}
			}
		}
	}
}