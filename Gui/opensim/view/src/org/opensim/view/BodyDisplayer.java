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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.prefs.Preferences;
import org.opensim.modeling.Body;
import org.opensim.modeling.Geometry;
import org.opensim.modeling.OpenSimObject;
import org.opensim.modeling.Vec3;
import org.opensim.utils.TheApp;
import org.opensim.view.pub.GeometryFileLocator;
import org.opensim.view.pub.ViewDB;
import vtk.FrameActor;
import vtk.vtkActor;
import vtk.vtkAppendPolyData;
import vtk.vtkAssembly;
import vtk.vtkBMPReader;
import vtk.vtkImageReader2;
import vtk.vtkJPEGReader;
import vtk.vtkMatrix4x4;
import vtk.vtkOutlineFilter;
import vtk.vtkPNGReader;
import vtk.vtkPolyDataMapper;
import vtk.vtkProp3D;
import vtk.vtkProp3DCollection;
import vtk.vtkSphereSource;
import vtk.vtkTexture;
import vtk.vtkTransform;
import vtk.vtkTransformPolyDataFilter;

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
    private vtkAssembly displayGeometryAssembly = new vtkAssembly();
    private boolean showAxes = false;
    private boolean showJointBFrame = false;
    protected Hashtable<Body, FrameActor> mapChildren2Frames = new Hashtable<Body, FrameActor>(2);
    protected Hashtable<OpenSimObject, vtkActor> mapGeometryToVtkObjects = new Hashtable<OpenSimObject, vtkActor>();
    private boolean showCOM = false;
    private vtkActor centerOfMassActor = new vtkActor();
    private vtkSphereSource myCMSphereSourceVTK = new vtkSphereSource();
    private double[] bodyBounds = new double[]{.1, -.1, .1, -.1, .1, -.1};
    // Create bounding box for selection and size estimate
    vtkOutlineFilter outlineFilter = new vtkOutlineFilter();
    vtkActor outlineActor = new vtkActor();
    vtkPolyDataMapper outlineMapper = new vtkPolyDataMapper();

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
      
      this.SetCMToGreenSphereWhoseSizeDependsOnMarkerRadius();

      //jointBFrame.GetProperty().SetLineStipplePattern(1);
      //VisibleObject bodyVisibleObject = body.getDisplayer();

      // Also optionally add outlineActor to this
      outlineActor.SetMapper(outlineMapper);
      outlineMapper.AddInputConnection(outlineFilter.GetOutputPort());
      boolean hasGeometry=false;
      vtkAppendPolyData boundingBoxPolyData = new vtkAppendPolyData();
/*
      // For each bone in the current body.
      for (int k = 0; k < bodyVisibleObject.getNumGeometryFiles(); ++k) {
          GeometrySet gSet = bodyVisibleObject.getGeometrySet();
          Geometry gPiece = gSet.get(k);
          // Make sure we have a valid geometry before creating DisplayGeometryDisplayer
          String fullFileName = GeometryFileLocator.getInstance().getFullname(modelFilePath,gPiece.getGeometryFile(), false);
          if (fullFileName==null) continue;
          vtkActor boneActor=new DisplayGeometryDisplayer(gPiece, modelFilePath);
          if (boneActor!=null){
              // To get tight bounding box we need to xform the polyData output by applying scale/translation
              // then pass the output to 
            vtkTransformPolyDataFilter xformDataFilter = new vtkTransformPolyDataFilter();
            hasGeometry=true;
            displayGeometryAssembly.AddPart(boneActor);
            mapGeometryToVtkObjects.put(gPiece, boneActor);
            // Now make a bounding box, transform using sam ematrix as Actor
            vtkPolyDataMapper mapper = (vtkPolyDataMapper) boneActor.GetMapper();
            vtkMatrix4x4 v44 = new vtkMatrix4x4();
            boneActor.GetMatrix(v44);
            //System.out.println("Geometry"+gPiece.getGeometryFile()+"Matrix44"+v44.Print());
            xformDataFilter.SetInput(mapper.GetInput());
            vtkTransform xform = new vtkTransform();
            xform.Concatenate(v44);
            //System.out.println("Geometry"+gPiece.getGeometryFile()+"xformDataFilter:xform="+xform.Print());
            xformDataFilter.SetTransform(xform);
            boundingBoxPolyData.AddInput(xformDataFilter.GetOutput());
            boundingBoxPolyData.Update();
          }
      }
      outlineFilter.AddInput(boundingBoxPolyData.GetOutput());
      this.AddPart(displayGeometryAssembly);
      applyVisibleObjectScaleAndTransform(bodyVisibleObject, displayGeometryAssembly);

      if( bodyVisibleObject.getShowAxes() )
          AddPart( getBodyAxes() );
      
      this.SetCMLocationFromPropertyTable( false );
      
       if (hasGeometry){
         applyVisibleObjectScaleAndTransform(body.getDisplayer(), outlineActor);
         bodyBounds = outlineActor.GetBounds();
         //AddPart(outlineActor);
      }

      centerOfMassActor.GetProperty().SetLineStipplePattern(0xF0F0);
      if (showCOM) AddPart(centerOfMassActor);
       */
      updateMapsToSupportPicking(body, mapObject2VtkObjects, mapVtkObjects2Objects);
      modelAssembly.AddPart(this);
    }


    public void applyPositionAndOrientation(FrameActor frame, Vec3 orientation, Vec3 location) {
        frame.SetOrientation(0., 0., 0.);
        frame.RotateX(orientation.get(0));
        frame.RotateY(orientation.get(1));
        frame.RotateZ(orientation.get(2));
        frame.SetPosition(new double[]{location.get(0), location.get(1), location.get(2)});
    }
         
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
        //body.getDisplayer().setShowAxes(showAxes);
    }

    public boolean isShowJointBFrame() {
        return showJointBFrame;
    }

    private void updateJointPFrame(Body body, FrameActor jointPFrame) {
        Vec3 location = new Vec3();
        Vec3 orientation = new Vec3();
        //body.getJoint().getLocationInParent(location);
        //jointPFrame.SetPosition(location);
        //body.getJoint().getOrientationInParent(orientation);
        for (int i=0; i<3; i++) orientation.set(i,Math.toDegrees(orientation.get(i)));
        applyPositionAndOrientation(jointPFrame, orientation, location);
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

    private void applyAttributesToActor(vtkActor boneActor, Geometry gPiece) {
        if (boneActor==null) return;    // Nothing to be done
        
        // Apply texture if any
        String textureFile = gPiece.get_Appearance().get_texture_file();
        if (textureFile!=null && !textureFile.equalsIgnoreCase("")){
            // Get full path
            textureFile = GeometryFileLocator.getInstance().getFullname(modelFilePath,gPiece.get_Appearance().get_texture_file(), false);
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
            Vec3 dColor = gPiece.getColor();
            boneActor.GetProperty().SetColor(dColor.get(0), dColor.get(1), dColor.get(2));
        }
        // Transform
        double[] rotationsAndTranslations = new double[6];
        //gPiece.getRotationsAndTranslationsAsArray6(rotationsAndTranslations);
        vtkTransform xform = new vtkTransform();
        setTransformFromArray6(rotationsAndTranslations, xform);
        boneActor.SetUserTransform(xform);
        /*
         * Scale
         */
        Vec3 scales = gPiece.get_scale_factors();
        boneActor.SetScale(scales.get(0), scales.get(1), scales.get(2));
        /**
         * Representation
         */
        Geometry.Representation pref=gPiece.getRepresentation();
        applyDisplayPreferenceToActor(boneActor, pref);
        /**
         * Opacity
         */
        double opacity = gPiece.getOpacity();
        boneActor.GetProperty().SetOpacity(gPiece.getOpacity());
    }

    static public void setTransformFromArray6(final double[] rotationsAndTranslations, final vtkTransform xform) {
        xform.RotateX(Math.toDegrees(rotationsAndTranslations[0]));
        xform.RotateY(Math.toDegrees(rotationsAndTranslations[1]));
        xform.RotateZ(Math.toDegrees(rotationsAndTranslations[2]));
        xform.PostMultiply();
        xform.Translate(rotationsAndTranslations[3], rotationsAndTranslations[4], rotationsAndTranslations[5]);
    }

    protected void applyDisplayPreferenceToActor(final vtkActor boneActor, final Geometry.Representation pref) {
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
      /*VisibleObject bodyVisibleObject = body.getDisplayer();
      // For each bone in the current body.
      GeometrySet gSet = bodyVisibleObject.getGeometrySet();
      for (int k = 0; k < gSet.getSize(); ++k) {
          Geometry gPiece = gSet.get(k);
          gPiece.setColor(colorComponents);
      }*/
    }
    
    //---------------------------------------------------------------
    public double[]  GetColorOrReturnNull(  )
    {
       double[] colorOfAllPiecesOrNullIfColorOfPiecesDiffer = null; 
       /*
       VisibleObject bodyVisibleObject = body.getDisplayer();
       GeometrySet bodyDisplayerGeometrySet = bodyVisibleObject.getGeometrySet();
       int numberOfPieces = bodyDisplayerGeometrySet==null ? 0 : bodyDisplayerGeometrySet.getSize();
       for( int i=0;  i < numberOfPieces;  i++) 
       {
          Geometry gi = bodyDisplayerGeometrySet.get( i );
          double[] colorOfCurrentPiece = {-1, -1, -1 };
          gi.getColor( colorOfCurrentPiece );
          for( int j=0;  j<3;  j++ ) { if( colorOfCurrentPiece[i] < 0 || colorOfCurrentPiece[i] > 1 ) return null; }
          if( colorOfAllPiecesOrNullIfColorOfPiecesDiffer == null ) colorOfAllPiecesOrNullIfColorOfPiecesDiffer = colorOfCurrentPiece;
          else for( int k=0;  k<3;  k++ ) { if( colorOfCurrentPiece[i] != colorOfAllPiecesOrNullIfColorOfPiecesDiffer[i] ) return null; }
       }      
       */
       return colorOfAllPiecesOrNullIfColorOfPiecesDiffer; 
    }

    public void setOpacity(double newOpacity) {
       /*
       VisibleObject bodyVisibleObject = body.getDisplayer();
      // For each bone in the current body.
      GeometrySet gSet = bodyVisibleObject.getGeometrySet();
      for (int k = 0; k < gSet.getSize(); ++k) {
          Geometry gPiece = gSet.get(k);
          gPiece.setOpacity(newOpacity);
      }*/
    }
    
     public Geometry.Representation getDisplayPreference() {
         return Geometry.Representation.DrawSurface; //body.getDisplayer().getDisplayPreference();
     }

    public void setDisplayPreference(Geometry.Representation newPref) {
        //body.getDisplayer().setDisplayPreference(newPref);
    }

    void applyColorsFromModel() {
        /*
      VisibleObject bodyVisibleObject = body.getDisplayer();
      GeometrySet gSet = bodyVisibleObject.getGeometrySet();
      for (int k = 0; k < gSet.getSize(); ++k) {
          Geometry gPiece = gSet.get(k);
          double[] colorOnFile = new double[3];
          gPiece.getColor(colorOnFile);
          //gPiece.setColor(colorOnFile);
          DisplayGeometryDisplayer gd = (DisplayGeometryDisplayer) mapGeometryToVtkObjects.get(gPiece);
          if (gd==null) continue;
          gd.GetProperty().SetColor(colorOnFile);
          gd.Modified();
      }*/
    }
    
    private void updateMapsToSupportPicking(final Body body, 
            Hashtable<OpenSimObject, vtkProp3D> mapObject2VtkObjects,
            Hashtable<vtkProp3D, OpenSimObject> mapVtkObjects2Objects) {

        // Fill the maps between objects and display to support picking, highlighting, etc..
        // The reverse map takes an actor to an Object and is filled as actors are created.
        mapObject2VtkObjects.put(body, this);
        
        // Picker picks Actors only, put those in reverseMap instead of BodyDisplayer
        vtkProp3DCollection props = displayGeometryAssembly.GetParts();
        props.InitTraversal();
        ArrayList<vtkActor> actors = new ArrayList<vtkActor>();
        int idx=0;
        /*
        GeometrySet gSet = body.getDisplayer().getGeometrySet();
        for(int act=0; act < props.GetNumberOfItems(); act++){
             vtkProp3D nextActor = props.GetNextProp3D();
             mapVtkObjects2Objects.put(nextActor, body);
             if (nextActor instanceof vtkActor)
                 actors.add((vtkActor)nextActor);
             if (nextActor instanceof FrameActor || nextActor == centerOfMassActor /*||
                     nextActor == outlineActor) continue;
             mapObject2VtkObjects.put(gSet.get(idx), nextActor);
             idx++;
        }
    */
    }

    public boolean isShowCOM() { return showCOM; }

    public void setShowCOM( boolean showCM ) {
        if( showCM ) { super.AddPart(centerOfMassActor);  this.SetCMSphereColorToGreen(); }
        else super.RemovePart(centerOfMassActor);
        this.showCOM = showCM;
    }
    
    //--------------------------------------------------------------------------
    public void  SetCMSphereColorToGreen( ) { centerOfMassActor.GetProperty().SetColor( 0.0, 1.0, 0.0 ); } // Green COM for now 
    
    //--------------------------------------------------------------------------
    public void  SetCMLocationFromPropertyTable( boolean updateView )
    {
       Vec3 cmLocationToFill = body.getMassCenter();
       myCMSphereSourceVTK.SetCenter( cmLocationToFill.get(0),  cmLocationToFill.get(1), cmLocationToFill.get(2));
       if( updateView )
       {
          super.Modified();
          ViewDB.getInstance().repaintAll();
       }
    }
      
      
    //--------------------------------------------------------------------------
    public void  SetCMToGreenSphereWhoseSizeDependsOnMarkerRadius(  )
    {
       myCMSphereSourceVTK.SetRadius( ViewDB.getInstance().getMarkerDisplayRadius()*2 );
       int resolutionPhi = 16, resolutionTheta = 16;
       myCMSphereSourceVTK.LatLongTessellationOn(); 
       myCMSphereSourceVTK.SetPhiResolution( resolutionPhi );
       myCMSphereSourceVTK.SetThetaResolution( resolutionTheta );
       // myCMSphereSourceVTK.SetEndPhi(90);
       // myCMSphereSourceVTK.SetEndTheta(90);       
       
       vtkPolyDataMapper comMapper = new vtkPolyDataMapper();
       comMapper.SetInput( myCMSphereSourceVTK.GetOutput() );
       centerOfMassActor.SetMapper( comMapper );
 
       this.SetCMSphereColorToGreen(); 
       this.SetCMSphereToEllipsoidWhoseAverageScaleIsOne( 1.0, 1.0, 1.0, false );
    }
      
      
    //--------------------------------------------------------------------------
    public void  SetCMSphereToEllipsoidFromMomentsOfInertiaOnly( double Ixx, double Iyy, double Izz, boolean updateView ) 
    { 
       if( Ixx <= 0.0 ) Ixx = 0.0;
       if( Iyy <= 0.0 ) Iyy = 0.0;
       if( Izz <= 0.0 ) Izz = 0.0;
       double xSum = Iyy + Izz - Ixx;
       double ySum = Izz + Ixx - Iyy;
       double zSum = Ixx + Iyy - Izz;
       double a = xSum > 0 ? java.lang.Math.sqrt( xSum ) : 0.0;
       double b = ySum > 0 ? java.lang.Math.sqrt( ySum ) : 0.0;
       double c = zSum > 0 ? java.lang.Math.sqrt( zSum ) : 0.0;
       this.SetCMSphereToEllipsoidWhoseAverageScaleIsOne( a, b, c, updateView );
    }
      
    
    //--------------------------------------------------------------------------
    private void  SetCMSphereToEllipsoidWhoseAverageScaleIsOne( double scaleX, double scaleY, double scaleZ, boolean updateView ) 
    { 
       /*
       if( scaleX < 0.0 ) scaleX = 0.0;   
       if( scaleY < 0.0 ) scaleY = 0.0;  
       if( scaleZ < 0.0 ) scaleZ = 0.0;  
       double sumScale = scaleX + scaleY + scaleZ;
       if( sumScale == 0.0 ) scaleX = scaleY = scaleZ = 1.0;
       else
       {
          double oneOverAverageScaleFactor = 3.0 / sumScale;
          if( (scaleX *= oneOverAverageScaleFactor) < 0.5 ) scaleX = 0.5;
          if( (scaleY *= oneOverAverageScaleFactor) < 0.5 ) scaleY = 0.5;
          if( (scaleZ *= oneOverAverageScaleFactor) < 0.5 ) scaleZ = 0.5;
       }           

       // vtk method SetScale scales all the vertices (which means scaling depends on position).
       // To counteract this, tried various things including translating the centerOfMassActor to origin and then
       // scaling and then re-translating to original position (but this did not work).  FIX? 
       double[] positionAsReference = centerOfMassActor.GetPosition();
       double[] positionCopy = { positionAsReference[0], positionAsReference[1], positionAsReference[2] };
       centerOfMassActor.SetPosition( 0.0, 0.0, 0.0 );
       centerOfMassActor.SetScale( scaleX, scaleY, scaleZ );
       centerOfMassActor.SetPosition( positionCopy[0], positionCopy[1], positionCopy[2] );
       // void vtkProp3D::SetUserTransform  ( vtkLinearTransform *  transform ) 
       */
        
       if( updateView )
       {
          super.Modified();
          ViewDB.getInstance().repaintAll();
       } 
    }
      
      
    /**
     * @return the bodyBounds
     */
    public double[] getBodyBounds() {
        return bodyBounds;
    }
    /**
     * Cycle thru geometry and apply preferences including vis of joint frames, colors, COM
     */
    public void applyDisplayPreferences() 
    {
        /*
       VisibleObject bodyVisibleObject = body.getDisplayer();
       GeometrySet gSet = bodyVisibleObject.getGeometrySet();
        // Cycle thru GeometrySet and apply preferences
       for(int i=0; i<gSet.getSize(); i++) {
           Geometry gPiece=gSet.get(i);
           DisplayGeometryDisplayer gActor = (DisplayGeometryDisplayer) mapGeometryToVtkObjects.get(gPiece);
           if (gActor==null) continue;
           gActor.applyDisplayPreferenceToActor();
       }
        */
       // Now frames and COM
    }

    /**
     * Update display of Body to correspond to latest Properties
     */
   void updateFromProperties() {
        //applyVisibleObjectScaleAndTransform(body.getDisplayer(), displayGeometryAssembly);
        applyColorsFromModel();
        applyDisplayPreferences();
        // update Joint frames
        Enumeration<Body> childBodies = mapChildren2Frames.keys();
        while(childBodies.hasMoreElements()){
            Body child = childBodies.nextElement();

            if (mapChildren2Frames.containsKey(child)){
                //setShowJointPFrame(child, false);
                //setShowJointPFrame(child, true);
            }
        }
        SetCMLocationFromPropertyTable(true);
    }

    /**
     * @return the displayGeometryAssembly
     */
    public vtkAssembly getDisplayGeometryAssembly() {
        return displayGeometryAssembly;
    }
}
