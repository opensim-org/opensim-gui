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
/*
 * DofFunctionEventListener.java
 *
 * Created on January 10, 2008, 2:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view.editors;

import org.opensim.modeling.Coordinate;
import org.opensim.modeling.TransformAxis;
import org.opensim.modeling.Function;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.SingleModelGuiElements;
import org.opensim.view.functionEditor.FunctionEvent;
import org.opensim.view.functionEditor.FunctionEventListener;
import org.opensim.view.functionEditor.FunctionModifiedEvent;
import org.opensim.view.functionEditor.FunctionReplacedEvent;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Peter Loan
 */
public class DofFunctionEventListener implements FunctionEventListener {

   /**
    * Creates a new instance of DofFunctionEventListener
    */
   public DofFunctionEventListener() {
   }

   public void handleFunctionEvent(FunctionEvent event) {
      OpenSimObject object = event.getObject();
      TransformAxis dof = TransformAxis.safeDownCast(object);

      if (dof != null) {
         Model model = OpenSimDB.getInstance().getCurrentModel();
         SingleModelGuiElements guiElem = OpenSimDB.getInstance().getModelGuiElements(model);
         OpenSimContext openSimContext = OpenSimDB.getInstance().getContext(model);

         if (event instanceof FunctionReplacedEvent) {
            FunctionReplacedEvent fre = (FunctionReplacedEvent) event;
            Function oldFunction = fre.getFunction();
            Function newFunction = fre.getReplacementFunction();
            if (Function.getCPtr(oldFunction) != Function.getCPtr(newFunction)) {
               openSimContext.replaceTransformAxisFunction(dof, newFunction);
               ViewDB.getInstance().updateModelDisplayNoRepaint(model);
               ViewDB.getInstance().renderAll();
               guiElem.setUnsavedChangesFlag(true);
            }
         } else if (event instanceof FunctionModifiedEvent) {
             
            if (dof.getCoordinateNamesInArray().getSize() > 0) {
               //TODO: not a good way to force a joint recalculation!!
               // TODO: for now, deal only with the first coordinate.
               String coordName = dof.getCoordinateNamesInArray().getitem(0);
               Coordinate coord = Coordinate.safeDownCast(dof.getJoint().getComponent(coordName));
               openSimContext.setValue(coord, openSimContext.getValue(coord));
               ViewDB.getInstance().updateModelDisplayNoRepaint(model);
               ViewDB.getInstance().renderAll();
               guiElem.setUnsavedChangesFlag(true);
            }
         }
      }
   }
}
