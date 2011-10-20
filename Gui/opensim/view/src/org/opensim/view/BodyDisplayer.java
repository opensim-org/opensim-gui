/*
 * BodyDisplayer.java
 *
 * Created on April 3, 2010, 1:28 AM
 *
* Author(s): Ayman Habib
 * Copyright (c)  2005-2010, Stanford University, Ayman Habib
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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.prefs.Preferences;
import org.opensim.modeling.Body;
import org.opensim.modeling.DisplayGeometry;
import org.opensim.modeling.GeometrySet;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.VisibleObject;
import org.opensim.utils.TheApp;
import org.opensim.view.pub.GeometryFileLocator;
import org.opensim.view.pub.ViewDB;
import vtk.FrameActor;
import vtk.vtkActor;
import vtk.vtkAssembly;
import vtk.vtkBMPReader;
import vtk.vtkImageReader2;
import vtk.vtkJPEGReader;
import vtk.vtkPNGReader;
import vtk.vtkPolyDataMapper;
import vtk.vtkProp3D;
import vtk.vtkProp3DCollection;
import vtk.vtkSphereSource;
import vtk.vtkTexture;
import vtk.vtkTransform;

/**
 *
 * @author ayman
 *
 * The visual Representation of one body (bones, base-frame, ..
 */
public class BodyDisplayer extends vtkAssembly 
        //implements OrientableInterface, ColorableInterface, HidableInterface 
{
    
    private FrameActor bodyAxes = new FrameActor();
    private FrameActor jointBFrame = new FrameActor();
    private boolean showAxes = false;
    private boolean showJointBFrame = false;
    protected Hashtable<Body, FrameActor> mapChildren2Frames = new Hashtable<Body, FrameActor>(2);
    protected Hashtable<OpenSimObject, vtkActor> mapGeometryToVtkObjects = new Hashtable<OpenSimObject, vtkActor>();
    private boolean showCOM = false;
    private vtkActor centerOfMassActor = new vtkActor();
    private vtkSphereSource comSource = new vtkSphereSource();

    private double bFrameScale =1.2;
    private double bFrameRadius =0.005;
    private double pFrameScale = 1.6;
    private double pFrameRadius = .003;
    
    private Body body;
    private String modelFilePath;
    private Color color=Color.WHITE;
    /**
     * Creates a new instance of BodyDisplayer
     */
    public BodyDisplayer(vtkAssembly modelAssembly, Body body, 
            Hashtable<OpenSimObject, vtkProp3D> mapObject2VtkObjects,
            Hashtable<vtkProp3D, OpenSimObject> mapVtkObjects2Objects)
   {
      String modelFilePath=body.getModel().getFilePath();
      String defaultSize = "1.0";
      defaultSize = Preferences.userNodeForPackage(TheApp.class).get("Joint Frame Scale", defaultSize);
      double userScale = Double.parseDouble(defaultSize);
      this.body = body;
      this.modelFilePath = modelFilePath;
      jointBFrame.SetScale(bFrameScale*userScale);
      jointBFrame.setRadius(bFrameRadius*userScale);
      jointBFrame.GetProperty().SetOpacity(0.5);
      bodyAxes.SetScale(2.);
      bodyAxes.setRadius(0.001);
      
      vtkPolyDataMapper comMapper = new vtkPolyDataMapper();

      comSource.SetRadius(ViewDB.getInstance().getMarkerDisplayRadius()*2);
      comMapper.SetInput(comSource.GetOutput());
      centerOfMassActor.SetMapper(comMapper);
      centerOfMassActor.GetProperty().SetColor(0.0, 1.0, 0.0); // Green COM for now

      //jointBFrame.GetProperty().SetLineStipplePattern(1);
      VisibleObject bodyDisplayer = body.getDisplayer();

      // Scale
      double[] bodyScales = new double[3];
      bodyDisplayer.getScaleFactors(bodyScales);
      double[] bodyRotTrans = new double[6];
      bodyDisplayer.getRotationsAndTranslationsAsArray6(bodyRotTrans);
      // For each bone in the current body.
      for (int k = 0; k < bodyDisplayer.getNumGeometryFiles(); ++k) {
          GeometrySet gSet = bodyDisplayer.getGeometrySet();
          DisplayGeometry gPiece = gSet.get(k);
          // Make sure we have a valid geometry before creating DisplayGeometryDisplayer
          String fullFileName = GeometryFileLocator.getInstance().getFullname(modelFilePath,gPiece.getGeometryFile(), false);
          if (fullFileName==null) continue;
          vtkActor boneActor=new DisplayGeometryDisplayer(gPiece, modelFilePath);
          double[] currentScales=boneActor.GetScale();
          for(int i=0; i<3; i++)
              currentScales[i]*=bodyScales[i];
          boneActor.SetScale(currentScales);
          //setTransformFromArray6(bodyRotTrans, (vtkTransform) boneActor.GetUserTransform());
          if (boneActor!=null){
            AddPart(boneActor);
            mapGeometryToVtkObjects.put(gPiece, boneActor);
          }
      }
      
      if (bodyDisplayer.getShowAxes()){
          AddPart(getBodyAxes());
      }
      double[] dCom= new double[3];
      body.getCenterOfMass(dCom);
      comSource.SetCenter(dCom);
      //comSource.SetEndPhi(90);
      //comSource.SetEndTheta(90);
      centerOfMassActor.GetProperty().SetLineStipplePattern(0xF0F0);
      if (showCOM) AddPart(centerOfMassActor);
      updateMapsToSupportPicking(body, mapObject2VtkObjects, mapVtkObjects2Objects);
      modelAssembly.AddPart(this);
    }
/*
    private vtkActor createOneDisplayGeometry(final String modelFilePath, final double[] bodyScales, final double[] bodyRotTrans, final DisplayGeometry gPiece) {
        String boneFile = GeometryFileLocator.getInstance().getFullname(modelFilePath,gPiece.getGeometryFile(), false);
        vtkActor boneActor = null;
        if (boneFile==null)
           return boneActor;
        boneActor = GeometryFactory.populateActorFromFile(boneFile);
        if (boneActor != null){
          applyAttributesToActor(boneActor, gPiece);
          double[] currentScales=boneActor.GetScale();
          for(int i=0; i<3; i++)
              currentScales[i]*=bodyScales[i];
          boneActor.SetScale(currentScales);
          setTransformFromArray6(bodyRotTrans, (vtkTransform) boneActor.GetUserTransform());
          // Compose 
        }
        return boneActor;
    }
*/
    public vtkActor getBodyAxes() {
        return bodyAxes;
    }

    public boolean isShowAxes() {
        return showAxes;
    }

    public void setShowAxes(boolean showAxes) {
        if (showAxes==false)
            RemovePart(bodyAxes);
        else {
            AddPart(getBodyAxes());
        }
        this.showAxes = showAxes;
        body.getDisplayer().setShowAxes(showAxes);
    }

    public boolean isShowJointBFrame() {
        return showJointBFrame;
    }

    public void setShowJointBFrame(boolean showJointBFrame) {
        this.showJointBFrame = showJointBFrame;
        // if turning On, add jointBFrame Part, else remove it
        if (showJointBFrame==false){
            RemovePart(jointBFrame);
        }
        else {
            if (!body.hasJoint()) return;
            double[] location = new double[3];
            double[] orientation = new double[3];
            body.getJoint().getLocationInChild(location);
            jointBFrame.SetPosition(location);
            body.getJoint().getOrientationInChild(orientation);
            jointBFrame.SetOrientation(orientation);
            AddPart(jointBFrame);
            Modified();
        }
    }

    public void setShowJointPFrame(Body body, boolean state){
        if (state){
            FrameActor jointPFrame = new FrameActor();
            String defaultSize = "1.0";
            defaultSize = Preferences.userNodeForPackage(TheApp.class).get("Joint Frame Scale", defaultSize);
            double userScale = Double.parseDouble(defaultSize);
            jointPFrame.SetScale(pFrameScale*userScale); //.2
            jointPFrame.setRadius(pFrameRadius*userScale); //.02
            double[] location = new double[3];
            double[] orientation = new double[3];
            body.getJoint().getLocationInParent(location);
            jointPFrame.SetPosition(location);
            body.getJoint().getOrientationInParent(orientation);
            jointPFrame.SetOrientation(orientation);
            jointPFrame.GetProperty().SetOpacity(0.75);
            jointPFrame.GetProperty().SetLineStipplePattern(2);
            AddPart(jointPFrame);
            Modified();
            mapChildren2Frames.put(body, jointPFrame);
        } else {  // Removing
            FrameActor jointPFrame = mapChildren2Frames.get(body);
            RemovePart(jointPFrame);
            Modified();
            mapChildren2Frames.remove(body);
        }
    }

    public boolean isShowJointPFrame(Body body){
        return (mapChildren2Frames.get(body)!=null);
    }

    public void setHidden(boolean toHide) {
        ViewDB.getInstance().toggleObjectDisplay(body, !toHide);
        Modified();
        ViewDB.getInstance().repaintAll();
    }

    public boolean isHidden() {
        return (ViewDB.getInstance().getDisplayStatus(body)!=1);
    }

    public void setShading(int shading) {
        ViewDB.getInstance().setObjectRepresentation(body, shading, shading);
        Modified();
        ViewDB.getInstance().repaintAll();
    }

    public int getShading() {
        return ViewDB.getInstance().getDisplayStatus(body);
    }

    public void setColor(Color newColor) {
      float[] colorComp = new float[3];
      newColor.getRGBColorComponents(colorComp);
      double[] colorCompDbl = new double[3];
      for(int i=0;i<3;i++) colorCompDbl[i]=colorComp[i];
      ViewDB.getInstance().setObjectColor(body, colorCompDbl);
      color = newColor;
    }

    public Color getColor() {
        return color;
    }

    private void applyAttributesToActor(vtkActor boneActor, DisplayGeometry gPiece) {
        if (boneActor==null) return;    // Nothing to be done
        
        // Apply texture if any
        String textureFile = gPiece.getTextureFile();
        if (textureFile!=null && !textureFile.equalsIgnoreCase("")){
            // Get full path
            textureFile = GeometryFileLocator.getInstance().getFullname(modelFilePath,gPiece.getTextureFile(), false);
            vtkTexture texture = new vtkTexture();
            vtkImageReader2 textureReader=null;
            if (textureFile.toLowerCase().endsWith(".bmp")){
                textureReader = new vtkBMPReader();
                textureReader.SetFileName(textureFile);
                textureReader.Update();
                texture.SetInputConnection(textureReader.GetOutputPort());
            }
            else if (textureFile.toLowerCase().endsWith(".jpg")){
                textureReader = new vtkJPEGReader();
                textureReader.SetFileName(textureFile);
                textureReader.Update();
                texture.SetInputConnection(textureReader.GetOutputPort());
            }  else if (textureFile.toLowerCase().endsWith(".png")){
                textureReader = new vtkPNGReader();
                textureReader.SetFileName(textureFile);
                textureReader.Update();
                texture.SetInputConnection(textureReader.GetOutputPort());
            }
            if (textureReader!=null){
                texture.InterpolateOn();
                boneActor.SetTexture(texture);
            }
        }
        else { // We assume if there's texture then it includes color as well, otherwise we read it in'
            // Color
            double[] dColor = new double[]{1., 1., 1.};
            gPiece.getColor(dColor);
            boneActor.GetProperty().SetColor(dColor);
        }
        // Transform
        double[] rotationsAndTranslations = new double[6];
        gPiece.getRotationsAndTranslationsAsArray6(rotationsAndTranslations);
        vtkTransform xform = new vtkTransform();
        setTransformFromArray6(rotationsAndTranslations, xform);
        boneActor.SetUserTransform(xform);
        /*
         * Scale
         */
        double[] scales = new double[]{1., 1., 1.};
        gPiece.getScaleFactors(scales);
        boneActor.SetScale(scales);
        /**
         * Representation
         */
        DisplayGeometry.DisplayPreference pref=gPiece.getDisplayPreference();
        applyDisplayPreferenceToActor(boneActor, pref);
        /**
         * Opacity
         */
        double opacity = gPiece.getOpacity();
        boneActor.GetProperty().SetOpacity(gPiece.getOpacity());
    }

    private void setTransformFromArray6(final double[] rotationsAndTranslations, final vtkTransform xform) {
        xform.RotateX(Math.toDegrees(rotationsAndTranslations[0]));
        xform.RotateY(Math.toDegrees(rotationsAndTranslations[1]));
        xform.RotateZ(Math.toDegrees(rotationsAndTranslations[2]));
        xform.Translate(rotationsAndTranslations[3], rotationsAndTranslations[4], rotationsAndTranslations[5]);
    }

    protected void applyDisplayPreferenceToActor(final vtkActor boneActor, final DisplayGeometry.DisplayPreference pref) {
        switch(pref.swigValue()) {
            case 0:
                boneActor.SetVisibility(0);
                break;
            case 1:
                boneActor.GetProperty().SetRepresentationToWireframe();
                break;
            case 2:
            case 3:
                boneActor.GetProperty().SetRepresentationToSurface();
                boneActor.GetProperty().SetInterpolationToFlat();
                break;
            case 4:
            default:
                boneActor.GetProperty().SetRepresentationToSurface();
                boneActor.GetProperty().SetInterpolationToGouraud();
        }
    }

    public void setColor(double[] colorComponents) {
        // Cycle thru Pieces and set their Color accordingly
      VisibleObject bodyDisplayer = body.getDisplayer();
      // For each bone in the current body.
      GeometrySet gSet = bodyDisplayer.getGeometrySet();
      for (int k = 0; k < gSet.getSize(); ++k) {
          DisplayGeometry gPiece = gSet.get(k);
          gPiece.setColor(colorComponents);
      }
    }

    public void setOpacity(double newOpacity) {
       VisibleObject bodyDisplayer = body.getDisplayer();
      // For each bone in the current body.
      GeometrySet gSet = bodyDisplayer.getGeometrySet();
      for (int k = 0; k < gSet.getSize(); ++k) {
          DisplayGeometry gPiece = gSet.get(k);
          gPiece.setOpacity(newOpacity);
      }
    }
    
     public DisplayGeometry.DisplayPreference getDisplayPreference() {
         return body.getDisplayer().getDisplayPreference();
     }

    public void setDisplayPreference(DisplayGeometry.DisplayPreference newPref) {
        body.getDisplayer().setDisplayPreference(newPref);
    }

    void applyColorsFromModel() {
      VisibleObject bodyDisplayer = body.getDisplayer();
      GeometrySet gSet = bodyDisplayer.getGeometrySet();
      for (int k = 0; k < gSet.getSize(); ++k) {
          DisplayGeometry gPiece = gSet.get(k);
          double[] colorOnFile = new double[3];
          gPiece.getColor(colorOnFile);
          gPiece.setColor(colorOnFile);
      }
    }
    private void updateMapsToSupportPicking(final Body body, 
            Hashtable<OpenSimObject, vtkProp3D> mapObject2VtkObjects,
            Hashtable<vtkProp3D, OpenSimObject> mapVtkObjects2Objects) {

        // Fill the maps between objects and display to support picking, highlighting, etc..
        // The reverse map takes an actor to an Object and is filled as actors are created.
        mapObject2VtkObjects.put(body, this);
        
        // Picker picks Actors only, put those in reverseMap instead of BodyDisplayer
        vtkProp3DCollection props = GetParts();
        props.InitTraversal();
        ArrayList<vtkActor> actors = new ArrayList<vtkActor>();
        int idx=0;
        GeometrySet gSet = body.getDisplayer().getGeometrySet();
        for(int act=0; act < props.GetNumberOfItems(); act++){
             vtkProp3D nextActor = props.GetNextProp3D();
             mapVtkObjects2Objects.put(nextActor, body);
             if (nextActor instanceof vtkActor)
                 actors.add((vtkActor)nextActor);
             if (nextActor instanceof FrameActor || nextActor == centerOfMassActor) continue;
             mapObject2VtkObjects.put(gSet.get(idx), nextActor);
             idx++;
        }
    }

    public boolean isShowCOM() {
        return showCOM;
    }

    public void setShowCOM(boolean showCOM) {
        if (showCOM) AddPart(centerOfMassActor);
        else RemovePart(centerOfMassActor);
        this.showCOM = showCOM;
    }
}
