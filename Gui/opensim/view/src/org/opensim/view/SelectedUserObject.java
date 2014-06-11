/*
 *
 * SelectedUSerObject
 * Author(s): Ayman Habib
 * Copyright (c)  2005-2007, Stanford University, Eran Guendelman
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
package org.opensim.view;

import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.Model;
import vtk.vtkCaptionActor2D;

/**
 *
 * @author Ayman Habib
 *
 */
public abstract class SelectedUserObject implements Selectable {

   OpenSimObject object;
   Model model;
   
   public SelectedUserObject(OpenSimObject object, Model model) { 
      this.object = object; 
      this.model = model;
   }
   
   public OpenSimObject getOpenSimObject() {
      return object;
   }

   public String getStatusText() {
      return object.getConcreteClassName() + ":" + object.getName();
   }

    public Model getOwnerModel() {
        return model;
    }
    
    public void updateAnchor(vtkCaptionActor2D caption) {
             double[] bounds=getBounds();
             if (bounds == null){   // object currently invisible
                 caption.SetVisibility(0);
                 return;
             }
             else
                 caption.SetVisibility(1);
             caption.SetAttachmentPoint(new double[]{
                 (bounds[0]+bounds[1])/2.0,
                 (bounds[2]+bounds[3])/2.0,
                 (bounds[4]+bounds[5])/2.0,
             });        
    }


}
