/*
 * ExperimentalDataVisuals.java
 *
 * Created on February 25, 2009, 11:34 AM
 *
 *
 *
 * Copyright (c)  2009, Stanford University and Ayman Habib. All rights reserved.
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

package org.opensim.view.experimentaldata;

import java.util.Hashtable;
import java.util.Vector;
import org.opensim.modeling.ArrayDouble;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimObject;
import org.opensim.view.*;
import org.opensim.view.pub.ViewDB;
import vtk.vtkActor;
import vtk.vtkAppendPolyData;
import vtk.vtkCubeSource;
import vtk.vtkLineSource;
import vtk.vtkPolyDataMapper;
import vtk.vtkProp3D;
import vtk.vtkProp3DCollection;

/**
 *
 * @author ayman. Intention ofr this class is to short-circuit/work-around the standard model visualization stuff
 * so we don't have to create fake entities and have something to hang our hats on just in case.
 */
public class ExperimentalDataVisuals extends SingleModelVisuals{
    ModelForExperimentalData model;    // Fake model used to visualize the data
    protected Hashtable<OpenSimObject, vtkProp3D> objectTrails = new Hashtable<OpenSimObject, vtkProp3D>();
    vtkCubeSource bbox;
    /**
     * Creates a new instance of ExperimentalDataVisuals
     */
    public ExperimentalDataVisuals(Model mdl) {
        super(mdl);
        
        bbox = new vtkCubeSource();
        model = (ModelForExperimentalData) mdl;
        AnnotatedMotion mot = model.getMotionData();
        for (ExperimentalDataObject obj:mot.getClassified()){
           addGeometryForModelComponent(obj, mdl);
        }
        if (mot.isBoundingBoxComputed()){
            double[] bounds = mot.getBoundingBox();

            bbox.SetBounds(bounds);
            ViewDB.getInstance().setObjectRepresentation(model.getGround(), 1, 0);
            BodyDisplayer groundRep = (BodyDisplayer) getVtkRepForObject(model.getGround());

            //groundRep.SetScale(bounds[3]-bounds[0], bounds[4]-bounds[1], bounds[5]-bounds[2]);
            groundRep.SetPosition(bounds[0], bounds[1], bounds[2]);
        }

    }

    public void toggleTraceDisplay(OpenSimObject openSimObject) {
        vtkProp3D prop=objectTrails.get(openSimObject);
        if (prop!=null){    // Created visuals already at some point
            prop.SetVisibility(1-prop.GetVisibility());
            return;
        }
        // Create the visuals
        AnnotatedMotion mot = model.getMotionData();
        Vector<String> markerNames=mot.getMarkerNames();
        int foundPosition=markerNames.indexOf(openSimObject.getName());
        ArrayDouble xCoord = new ArrayDouble();
        ArrayDouble yCoord = new ArrayDouble();
        ArrayDouble zCoord = new ArrayDouble();
        mot.getDataColumn(3*foundPosition, xCoord);
        mot.getDataColumn(3*foundPosition+1, yCoord);
        mot.getDataColumn(3*foundPosition+2, zCoord);
        vtkAppendPolyData traceLinePolyData = new vtkAppendPolyData();
        int numPoints = xCoord.getSize();
        double unitConversion = mot.getUnitConversion();
        for(int i=0;i<numPoints-1;i++){
            vtkLineSource nextLine = new vtkLineSource();
            nextLine.SetPoint1(xCoord.getitem(i)/unitConversion, yCoord.getitem(i)/unitConversion, zCoord.getitem(i)/unitConversion);
            nextLine.SetPoint2(xCoord.getitem(i+1)/unitConversion, yCoord.getitem(i+1)/unitConversion, zCoord.getitem(i+1)/unitConversion);
            //System.out.println("Line "+nextLine.GetPoint1()+" to "+nextLine.GetPoint2());
            traceLinePolyData.AddInput(nextLine.GetOutput());
        }
        vtkPolyDataMapper traceLineMapper = new vtkPolyDataMapper();
        traceLineMapper.SetInput(traceLinePolyData.GetOutput());
        vtkActor traceLineActor = new vtkActor();
        traceLineActor.SetMapper(traceLineMapper);
        objectTrails.put(openSimObject, traceLineActor);
        modelDisplayAssembly.AddPart(traceLineActor);
        traceLineActor.Modified();
    }
}
