/* -------------------------------------------------------------------------- *
 * OpenSim: DecorativeLineDisplayer.java                                      *
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

package org.opensim.view;

import org.opensim.modeling.*;
import vtk.vtkActor;
import vtk.vtkLineSource;
import vtk.vtkPolyData;
import vtk.vtkTubeFilter;

public class DecorativeLineDisplayer extends DecorativeGeometryDisplayer {

    private final DecorativeLine agLocal;
    private vtkLineSource line = new vtkLineSource();
    private final vtkTubeFilter dFilter = new vtkTubeFilter();
   //protected OpenSimObject obj;
    /** 
     * Displayer for Wrap Geometry
     * @param ag
     * @param object 
     */
    DecorativeLineDisplayer(DecorativeLine ag) {
        agLocal = ag.clone();
        //if (ag.hasUserRef()) setObj(ag.getUserRefAsObject());

      }

    /**
     * Convert DecorativeGeometry object passed in to the corresponding vtk polyhedral representation.
     * Transform is passed in as well since the way it applies to PolyData depends on source
     */
    private vtkPolyData getPolyData() {
        //Geometry.GeometryType analyticType = ag.
        Vec3 p1 = agLocal.getPoint1();
        Vec3 p2 = agLocal.getPoint2();
        line.SetPoint1(p1.get(0), p1.get(1), p1.get(2));
        line.SetPoint2(p2.get(0), p2.get(1), p2.get(2));
        line.Modified();
        dFilter.SetInput(line.GetOutput());
        dFilter.SetRadius(.005);           
        return dFilter.GetOutput();
    }

 
    @Override
    void updateDisplayFromDecorativeGeometry() {
        vtkPolyData polyData = getPolyData();
        createAndConnectMapper(polyData);
        setXformAndAttributesFromDecorativeGeometry(agLocal);
    }

    @Override
    vtkActor getVisuals() {
       updateDisplayFromDecorativeGeometry();
       return this;
    }

    @Override
    int getBodyId() {
        return agLocal.getBodyId();
    }
    @Override
    int getIndexOnBody() {
        return agLocal.getIndexOnBody();
    }
/*
    @Override
    void copyAttributesFromDecorativeGeometry(DecorativeGeometry arg0) {
        DecorativeLine newLine = (DecorativeLine) arg0;
        setPoint1(newLine.getPoint1());
        setPoint2(newLine.getPoint2());
        line.SetPoint1(getPoint1().get(0),getPoint1().get(1),getPoint1().get(2));
        line.SetPoint2(getPoint2().get(0),getPoint2().get(1),getPoint2().get(2));
        line.Modified();
        super.copyAttributesFromDecorativeGeometry(arg0);
    }
*/
    @Override
    DecorativeGeometry getDecorativeGeometry() {
        return agLocal;
    }

    @Override
    void updateGeometry(DecorativeGeometry arg) {
        DecorativeLine arg0 = (DecorativeLine) arg;
        agLocal.setPoint1(new Vec3(arg0.getPoint1()));
        agLocal.setPoint2(new Vec3(arg0.getPoint2()));
        //System.out.println("NewLine"+agLocal.getPoint1().toString()+","+agLocal.getPoint2().toString());
        updateDisplayFromDecorativeGeometry();    
    }
    
}
