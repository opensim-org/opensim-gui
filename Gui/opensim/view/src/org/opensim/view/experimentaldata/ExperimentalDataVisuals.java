/* -------------------------------------------------------------------------- *
 * OpenSim: ExperimentalDataVisuals.java                                      *
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
/*
 * ExperimentalDataVisuals.java
 *
 * Created on February 25, 2009, 11:34 AM
 *
 *
 *
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
           addGeometryForComponent(obj, mdl);
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
