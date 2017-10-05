/* -------------------------------------------------------------------------- *
 * OpenSim: AxesActor.java                                                    *
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
package vtk;

import vtk.vtkActor;
import vtk.vtkAssembly;
import vtk.vtkAxes;
import vtk.vtkPolyDataMapper;
import vtk.vtkTubeFilter;

public class AxesActor extends vtkAssembly {

  private double axisLength = 0.5;
  private double axisTextLength = 0.1;
  private vtkTextActor xactor, yactor, zactor;
  private vtkFollower xtextActor, ytextActor, ztextActor;
  public AxesActor() {
    super();
    createAxes(1.0, true, true);
  }

   public AxesActor(double scale, boolean showTips, boolean showText) {
       createAxes(scale, showTips, showText);
   }
   
   public AxesActor(double scale) {
    super();
    createAxes(scale, true, true);
  }

  protected void createAxes(double scale, boolean includeArrowTips, boolean includeText) {
/*
    vtkAxes axes = new vtkAxes();
    axes.SetOrigin(0, 0, 0);
    axes.SetScaleFactor(axisLength);
*/
      if (includeText) {
          xactor = new vtkTextActor();
          yactor = new vtkTextActor();
          zactor = new vtkTextActor();
          
          xactor.SetInput("X");
          yactor.SetInput("Y");
          zactor.SetInput("Z");
          
          //xactor.ScaledTextOn();
          //yactor.ScaledTextOn();
          //zactor.ScaledTextOn();
          
          xactor.GetPositionCoordinate().SetCoordinateSystemToWorld();
          yactor.GetPositionCoordinate().SetCoordinateSystemToWorld();
          zactor.GetPositionCoordinate().SetCoordinateSystemToWorld();
          
          xactor.GetPositionCoordinate().SetValue(axisLength, 0.0, 0.0);
          yactor.GetPositionCoordinate().SetValue(0.0, axisLength, 0.0);
          zactor.GetPositionCoordinate().SetValue(0.0, 0.0, axisLength);
          
          xactor.GetTextProperty().SetColor(1.0, 1.0, 1.0);
          xactor.GetTextProperty().ShadowOn();
          xactor.GetTextProperty().ItalicOn();
          xactor.GetTextProperty().BoldOff();
          
          yactor.GetTextProperty().SetColor(1.0, 1.0, 1.0);
          yactor.GetTextProperty().ShadowOn();
          yactor.GetTextProperty().ItalicOn();
          yactor.GetTextProperty().BoldOff();
          
          zactor.GetTextProperty().SetColor(1.0, 1.0, 1.0);
          zactor.GetTextProperty().ShadowOn();
          zactor.GetTextProperty().ItalicOn();
          zactor.GetTextProperty().BoldOff();
          
          xactor.SetMaximumLineHeight(0.25);
          yactor.SetMaximumLineHeight(0.25);
          zactor.SetMaximumLineHeight(0.25);
      }
  
    int cylRes = 36;
    double cylRadius = 0.01*scale;
    double cylAmbient = 0.5;
    double cylOpacity = 0.5;
    
    //--- x-Cylinder
    vtkCylinderSource xcyl = new vtkCylinderSource();
    xcyl.SetRadius(cylRadius);
    xcyl.SetHeight(axisLength);
    xcyl.CappingOn();
    xcyl.SetResolution(cylRes);
    vtkPolyDataMapper xcylMapper = new vtkPolyDataMapper();
    xcylMapper.SetInput(xcyl.GetOutput());
    vtkActor xcylActor = new vtkActor();
    xcylActor.SetMapper(xcylMapper);
    xcylActor.GetProperty().SetColor(1,0,0);
    xcylActor.RotateZ(-90);
    xcylActor.SetPosition(axisLength/2, 0.0, 0.0);
    xcylActor.GetProperty().SetAmbient(cylAmbient);
    xcylActor.GetProperty().SetOpacity(cylOpacity);

    //--- y-Cylinder
    vtkCylinderSource ycyl = new vtkCylinderSource();
    ycyl.SetRadius(cylRadius);
    ycyl.SetHeight(axisLength);
    ycyl.CappingOn();
    ycyl.SetResolution(cylRes);
    vtkPolyDataMapper ycylMapper = new vtkPolyDataMapper();
    ycylMapper.SetInput(ycyl.GetOutput());
    vtkActor ycylActor = new vtkActor();
    ycylActor.SetMapper(ycylMapper);
    ycylActor.GetProperty().SetColor(1,1,0);
    ycylActor.SetPosition(0.0, axisLength/2, 0.0);
    ycylActor.GetProperty().SetAmbient(cylAmbient);
    ycylActor.GetProperty().SetOpacity(cylOpacity);

    //--- z-Cylinder
    vtkCylinderSource zcyl = new vtkCylinderSource();
    zcyl.SetRadius(cylRadius);
    zcyl.SetHeight(axisLength);
    zcyl.CappingOn();
    zcyl.SetResolution(cylRes);
    vtkPolyDataMapper zcylMapper = new vtkPolyDataMapper();
    zcylMapper.SetInput(zcyl.GetOutput());
    vtkActor zcylActor = new vtkActor();
    zcylActor.SetMapper(zcylMapper);
    zcylActor.GetProperty().SetColor(0,1,0);
    zcylActor.RotateX(90);
    zcylActor.SetPosition(0.0, 0.0, axisLength/2);
    zcylActor.GetProperty().SetAmbient(cylAmbient);
    zcylActor.GetProperty().SetOpacity(cylOpacity);

    if (includeArrowTips) {
        int coneRes = 36;
        double coneScale = 0.075;
        double coneAmbient = 0.5;
        double coneOpacity = .5;
        
        vtkConeSource xcone = new vtkConeSource();
        xcone.SetResolution(coneRes);
        vtkPolyDataMapper xconeMapper = new vtkPolyDataMapper();
        xconeMapper.SetInput(xcone.GetOutput());
        vtkActor xconeActor = new vtkActor();
        xconeActor.SetMapper(xconeMapper);
        xconeActor.GetProperty().SetColor(1,0,0);
        xconeActor.SetScale(coneScale, coneScale, coneScale);
        xconeActor.SetPosition(axisLength+(coneScale/2), 0.0, 0.0);
        xconeActor.GetProperty().SetAmbient(coneAmbient);
        xconeActor.GetProperty().SetOpacity(coneOpacity);
        
        //--- y-Cone
        vtkConeSource ycone = new vtkConeSource();
        ycone.SetResolution(coneRes);
        vtkPolyDataMapper yconeMapper = new vtkPolyDataMapper();
        yconeMapper.SetInput(ycone.GetOutput());
        vtkActor yconeActor = new vtkActor();
        yconeActor.SetMapper(yconeMapper);
        yconeActor.GetProperty().SetColor(1,1,0);
        yconeActor.RotateZ(90);
        yconeActor.SetScale(coneScale, coneScale, coneScale);
        yconeActor.SetPosition(0.0, axisLength+(coneScale/2), 0.0);
        yconeActor.GetProperty().SetAmbient(coneAmbient);
        yconeActor.GetProperty().SetOpacity(coneOpacity);
        
        //--- z-Cone
        vtkConeSource zcone = new vtkConeSource();
        zcone.SetResolution(coneRes);
        vtkPolyDataMapper zconeMapper = new vtkPolyDataMapper();
        zconeMapper.SetInput(zcone.GetOutput());
        vtkActor zconeActor = new vtkActor();
        zconeActor.SetMapper(zconeMapper);
        zconeActor.GetProperty().SetColor(0,1,0);
        zconeActor.RotateY(-90);
        zconeActor.SetScale(coneScale, coneScale, coneScale);
        zconeActor.SetPosition(0.0, 0.0, axisLength+(coneScale/2));
        yconeActor.GetProperty().SetAmbient(coneAmbient);
        yconeActor.GetProperty().SetOpacity(coneOpacity);
        
        this.AddPart(xconeActor);
        this.AddPart(yconeActor);
        this.AddPart(zconeActor);
    }
    if (includeText) {
        //--- x-Label
        vtkVectorText xtext = new vtkVectorText();
        xtext.SetText("X");
        vtkPolyDataMapper xtextMapper = new vtkPolyDataMapper();
        xtextMapper.SetInput(xtext.GetOutput());
        xtextActor = new vtkFollower();
        xtextActor.SetMapper(xtextMapper);
        xtextActor.SetScale(axisTextLength, axisTextLength, axisTextLength);
        xtextActor.GetProperty().SetColor(1, 0, 0);
        xtextActor.SetPosition(axisLength, 0.0, 0.0);
        
        //--- y-Label
        vtkVectorText ytext = new vtkVectorText();
        ytext.SetText("Y");
        vtkPolyDataMapper ytextMapper = new vtkPolyDataMapper();
        ytextMapper.SetInput(ytext.GetOutput());
        ytextActor = new vtkFollower();
        ytextActor.SetMapper(ytextMapper);
        ytextActor.SetScale(axisTextLength, axisTextLength, axisTextLength);
        ytextActor.GetProperty().SetColor(1, 1, 0);
        ytextActor.SetPosition(0.0, axisLength, 0.0);
        
        //--- z-Label
        vtkVectorText ztext = new vtkVectorText();
        ztext.SetText("Z");
        vtkPolyDataMapper ztextMapper = new vtkPolyDataMapper();
        ztextMapper.SetInput(ztext.GetOutput());
        ztextActor = new vtkFollower();
        ztextActor.SetMapper(ztextMapper);
        ztextActor.SetScale(axisTextLength, axisTextLength, axisTextLength);
        ztextActor.GetProperty().SetColor(0, 1, 0);
        ztextActor.SetPosition(0.0, 0.0, axisLength);
        
        this.AddPart(xtextActor);
        this.AddPart(ytextActor);
        this.AddPart(ztextActor);
    }
    //this.AddPart(tubeActor);
    this.AddPart(xcylActor);
    this.AddPart(ycylActor);
    this.AddPart(zcylActor);

  }

  public void setAxesVisibility(boolean ison) {
    this.SetVisibility(ison ? 1 : 0);

    xactor.SetVisibility(ison ? 1 : 0);
    yactor.SetVisibility(ison ? 1 : 0);
    zactor.SetVisibility(ison ? 1 : 0);
  }

  public void setCamera(vtkCamera camera) {
      xtextActor.SetCamera(camera);
      ytextActor.SetCamera(camera);
      ztextActor.SetCamera(camera);
  }
}
