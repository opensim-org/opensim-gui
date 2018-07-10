/* -------------------------------------------------------------------------- *
 * OpenSim: DisplayGeometryDisplayer.java                                     *
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
 * DisplayGeometryDisplayer.java
 *
 * Created on April 3, 2010, 1:28 AM
 *
 * Author(s): Ayman Habib
 */
package org.opensim.view;

import java.awt.Color;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.opensim.modeling.DecorativeGeometry;
import org.opensim.modeling.Geometry;
import org.opensim.modeling.Mesh;
import org.opensim.modeling.Vec3;
import org.opensim.view.pub.GeometryFileLocator;
import org.opensim.view.pub.ViewDB;
import vtk.vtkActor;
import vtk.vtkTransform;

/**
 *
 * @author ayman
 *
 * The visual Representation of one body (bones, base-frame, ..
 */
public class DisplayGeometryDisplayer extends vtkActor 
//        implements ColorableInterface, HidableInterface
{
    Geometry displayGeometry;
    private Color color=Color.WHITE;    // Property
    String modelFilePath;
    //private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    /**
     * Creates a new instance of BodyDisplayer
     */
    public DisplayGeometryDisplayer(Geometry displayGeometry, String modelFilePath) {
        this.displayGeometry = displayGeometry;
        this.modelFilePath=modelFilePath;
        Mesh msh = Mesh.safeDownCast(displayGeometry);
        if (msh !=null){
            String fileName = msh.getGeometryFilename();
            String boneFile = GeometryFileLocator.getInstance().getFullname(modelFilePath,fileName, false);
            GeometryFactory.populatePolyDataFromFile(boneFile, this);
        }
        applyAttributesAndTransformToActor();        
    }
    
    public void setHidden(boolean toHide) {
        ViewDB.getInstance().toggleObjectDisplay(displayGeometry, !toHide);
        Modified();
        ViewDB.getInstance().renderAll();
    }
    
    public boolean isHidden() {
        return (ViewDB.getInstance().getDisplayStatus(displayGeometry)!=1);
    }
    
    public void setShading(int shading) {
        Modified();
        ViewDB.getInstance().renderAll();
    }
    
    public int getShading() {
        return ViewDB.getInstance().getDisplayStatus(displayGeometry);
    }
    
    public void setColorGUI(Color newColor) {
        float[] colorComp = new float[3];
        newColor.getRGBColorComponents(colorComp);
        final double[] colorCompDbl = new double[3];
        for(int i=0;i<3;i++) colorCompDbl[i]=colorComp[i];
        ViewDB.getInstance().setObjectColor(displayGeometry, colorCompDbl);
        float[] oldColorCompFloat = new float[]{1.f, 1.f, 1.f};
        color.getRGBColorComponents(oldColorCompFloat);
        final double[] oldColorCompDbl = new double[]{(double)oldColorCompFloat[0], 
                                                      (double)oldColorCompFloat[1], (double)oldColorCompFloat[2]};
        AbstractUndoableEdit auEdit = new AbstractUndoableEdit(){
           public boolean canUndo() {
               return true;
           }
           public boolean canRedo() {
               return true;
           }
           public void undo() throws CannotUndoException {
               super.undo();
               ViewDB.getInstance().applyColor(oldColorCompDbl, DisplayGeometryDisplayer.this, false);
               for (int i=0; i<3; i++) 
               //displayGeometry.setColor(oldColorCompDbl);
               assignColor(oldColorCompDbl);
               ExplorerTopComponent.getDefault().requestActive();
           }

            private void assignColor(final double[] colorComponentDbl) {
                color = new Color((float)colorComponentDbl[0], (float)colorComponentDbl[1], (float)colorComponentDbl[2]);
            }
           public void redo() throws CannotRedoException {
               super.redo();
               ViewDB.getInstance().applyColor(colorCompDbl, DisplayGeometryDisplayer.this, false);
               //displayGeometry.setColor(colorCompDbl);
               assignColor(colorCompDbl);
               ExplorerTopComponent.getDefault().requestActive();
           }
            public String getPresentationName() {
                return "Color Change";
            }
           
       };
        ExplorerTopComponent.addUndoableEdit(auEdit);
        color = newColor;
    }
    
    public Color getColor() {
        return color;
    }
    
    public void applyAttributesAndTransformToActor() {        
        // Apply texture if any
        /*
        String textureFile = displayGeometry.getTextureFile();
        if (textureFile!=null && !textureFile.equalsIgnoreCase("")){
            // Get full path
            textureFile = GeometryFileLocator.getInstance().getFullname(modelFilePath,displayGeometry.getTextureFile(), false);
            vtkTexture texture = new vtkTexture();
            vtkImageReader2 textureReader=null;
            if (textureFile!=null){
            if (textureFile.toLowerCase().endsWith(".bmp")){
                textureReader = new vtkBMPReader();
                textureReader.SetFileName(textureFile);
                textureReader.Update();
                texture.SetInputConnection(textureReader.GetOutputPort());
            } else if (textureFile.toLowerCase().endsWith(".jpg")){
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
                SetTexture(texture);
            }
            }
        } else */
        { // We assume if there's texture then it includes color as well, otherwise we read it in'
            // Color
            double[] dColor = new double[]{1., 1., 1.};
            Vec3 clr = displayGeometry.getColor();
            GetProperty().SetColor(dColor);
            color = new Color((float)dColor[0], (float)dColor[1], (float)dColor[2]);
        }
        /*
         * Scale
         */
        double[] scales = new double[]{1., 1., 1.};
        Vec3 sf = displayGeometry.get_scale_factors();
        SetScale(sf.get(0), sf.get(1), sf.get(2));
        // Transform
        double[] rotationsAndTranslations = new double[6];
        //displayGeometry.getRotationsAndTranslationsAsArray6(rotationsAndTranslations);
        vtkTransform xform = new vtkTransform();
        BodyDisplayer.setTransformFromArray6(rotationsAndTranslations, xform);
        SetUserTransform(xform);
        /**
         * Representation
         */
        applyDisplayPreferenceToActor();
        /**
         * Opacity
         */
        GetProperty().SetOpacity(displayGeometry.getOpacity());
    }
        
    public void applyDisplayPreferenceToActor() {
        
        switch(displayGeometry.getRepresentation().swigValue()) {
            case 0:
                SetVisibility(0);
                break;
            case 1:
                SetVisibility(1);
                GetProperty().SetRepresentationToWireframe();
                break;
            case 2:
            case 3:
                SetVisibility(1);
                GetProperty().SetRepresentationToSurface();
                GetProperty().SetInterpolationToFlat();
                break;
            case 4:
            default:
                SetVisibility(1);
                GetProperty().SetRepresentationToSurface();
                GetProperty().SetInterpolationToGouraud();
        }
    }
    
    public void setOpacity(double newOpacity) {
        displayGeometry.setOpacity(newOpacity);
    }
    
    public double getOpacity() {
        return displayGeometry.getOpacity();
    }

    public DecorativeGeometry.Representation getDisplayPreference() {
        return displayGeometry.getRepresentation();
    }

    public void setDisplayPreference(DecorativeGeometry.Representation newPref) {
        setDisplayPreferenceGUI(newPref, true);
    }
    public void setDisplayPreferenceGUI(final DecorativeGeometry.Representation newPref, boolean allowUndo) {
        if (allowUndo){
            final DecorativeGeometry.Representation oldPref = displayGeometry.getRepresentation();
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit(){
               public boolean canUndo() {
                   return true;
               }
               public boolean canRedo() {
                   return true;
               }
               public void undo() throws CannotUndoException {
                   super.undo();
                   // Change display
                   setDisplayPreferenceGUI(oldPref, false);
                   // Change model
               }
               public void redo() throws CannotRedoException {
                   super.redo();
                   // Change display
                   setDisplayPreferenceGUI(newPref, false);
                   // Change model
               }
                public String getPresentationName() {
                    return "Display Preference Change";
                }

           };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }
        displayGeometry.setRepresentation(newPref);
        applyDisplayPreferenceToActor();
        Modified();
        ViewDB.getInstance().renderAll();
    }
    public void setLocation(Vec3 loc){
        System.out.println("Displayer:setLocation");
        setLocationGUI(loc, true);
    }
    public void setLocationGUI(final Vec3 loc, boolean allowUndo){
        double[] rotationsAndTranslations = new double[6];
        //displayGeometry.getRotationsAndTranslationsAsArray6(rotationsAndTranslations);
        final Vec3 oldLoc = new Vec3(rotationsAndTranslations[3], 
        rotationsAndTranslations[4], rotationsAndTranslations[5]);
        for(int i=0; i<3;i++) rotationsAndTranslations[i+3]=loc.get(i);
        //displayGeometry.setRotationsAndTRanslations(rotationsAndTranslations);
        vtkTransform xform = new vtkTransform();
        BodyDisplayer.setTransformFromArray6(rotationsAndTranslations, xform);
        SetUserTransform(xform);
        Modified();
        if (allowUndo){
            AbstractUndoableEdit auEdit = new AbstractUndoableEdit(){
               public boolean canUndo() {
                   return true;
               }
               public boolean canRedo() {
                   return true;
               }
               public void undo() throws CannotUndoException {
                   super.undo();
                   // Change display
                   setLocationGUI(oldLoc, false);
                   // Change model
               }
               public void redo() throws CannotRedoException {
                   super.redo();
                   // Change display
                   setLocationGUI(loc, true);
                   // Change model
               }
                public String getPresentationName() {
                    return "Location Change";
                }

           };
            ExplorerTopComponent.addUndoableEdit(auEdit);
        }
        ViewDB.getInstance().renderAll();
    }
    public Vec3 getLocation() {
        double[] rotationsAndTranslations = new double[6];
        //displayGeometry.getRotationsAndTranslationsAsArray6(rotationsAndTranslations);
        return new Vec3(rotationsAndTranslations[3], rotationsAndTranslations[4], rotationsAndTranslations[5]);
    }
    public void setOrientation(Vec3 loc){
        double[] rotationsAndTranslations = new double[6];
        //displayGeometry.getRotationsAndTranslationsAsArray6(rotationsAndTranslations);
        for(int i=0; i<3;i++) rotationsAndTranslations[i]=Math.toRadians(loc.get(i));
        //displayGeometry.setRotationsAndTRanslations(rotationsAndTranslations);
        vtkTransform xform = new vtkTransform();
        BodyDisplayer.setTransformFromArray6(rotationsAndTranslations, xform);
        SetUserTransform(xform);
        Modified();
        ViewDB.getInstance().renderAll();
    }
    public Vec3 getOrientation() {
        double[] rotationsAndTranslations = new double[6];
        //displayGeometry.getRotationsAndTranslationsAsArray6(rotationsAndTranslations);
        for(int i=0; i<3; i++) rotationsAndTranslations[i]=Math.toDegrees(rotationsAndTranslations[i]);
        return new Vec3(rotationsAndTranslations[0], rotationsAndTranslations[1], rotationsAndTranslations[2]);
    }
    
    public void updateFromProperties() {
        applyAttributesAndTransformToActor();
        applyDisplayPreferenceToActor();
    }
}
