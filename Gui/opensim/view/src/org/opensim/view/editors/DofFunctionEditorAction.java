/* -------------------------------------------------------------------------- *
 * OpenSim: DofFunctionEditorAction.java                                      *
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
					Coordinate coord = dof.getJoint().get_coordinates(0);
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