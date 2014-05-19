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
package org.opensim.view;

import java.awt.Color;
import org.opensim.modeling.*;
import org.opensim.view.pub.GeometryFileLocator;
import vtk.vtkActor;
import vtk.vtkPolyData;
import vtk.vtkPolyDataMapper;

public class DecorativeMeshFileDisplayer extends DecorativeGeometryDisplayer {
    private DecorativeMeshFile ag;
    private String modelFilePath;
    //protected OpenSimObject obj;
    /** 
     * Displayer for Wrap Geometry
     * @param ag
     * @param object 
     */
    DecorativeMeshFileDisplayer(DecorativeMeshFile ag, 
            String modelFilePath, OpenSimObject object) {
        super(object);
        this.ag = ag;
        this.modelFilePath = modelFilePath;
     }

    /**
     * Convert DecorativeGeometry object passed in to the corresponding vtk polyhedral representation.
     * Transform is passed in as well since the way it applies to PolyData depends on source
     */
    private vtkPolyData getPolyData(DecorativeMeshFile ag) {
        String boneFile = GeometryFileLocator.getInstance().getFullname(modelFilePath,ag.getMeshFile(), false);
        if (boneFile==null) return null;
        return GeometryFactory.populatePolyDatarFromFile(boneFile, this);
    }

    @Override
    void updateDisplayFromDecorativeGeometry() {
         setXformAndAttributesFromDecorativeGeometry(ag);
    }

    private void createDisplayFromDecorativeGeometry() {
        vtkPolyData polyData = getPolyData(ag);
        //updatePropertiesForPolyData(polyData);
        createAndConnectMapper(polyData);
    }

    
    public void setColorGUI(Color newColor) {
        float[] floats = new float[3];
        newColor.getRGBColorComponents(floats);
        // Push change to object

    }

    public Color getColor() {
        Vec3 clr = ag.getColor();
        return new Color((float)clr.get(0), (float)clr.get(1), (float)clr.get(2));
    }

    @Override
    vtkActor getVisuals() {
        createDisplayFromDecorativeGeometry();
        updateDisplayFromDecorativeGeometry();
        return this;
    }

    int getBodyId() {
        return ag.getBodyId();
    }
    int getIndexOnBody() {
        return ag.getIndexOnBody();
    }

}
