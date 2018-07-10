/* -------------------------------------------------------------------------- *
 * OpenSim: OpenSimCanvas.java                                                *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Paul Mitiguy                                       *
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
 * OpenSimCanvas.java
 *
 * Created on June 14, 2006, 11:58 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view;

import java.awt.Cursor;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.JPopupMenu;
import org.opensim.logger.OpenSimLogger;
import org.opensim.modeling.MovingPathPoint;
import org.opensim.modeling.OpenSimObject;
import org.opensim.utils.TheApp;
import org.opensim.view.base.OpenSimBaseCanvas;
import org.opensim.view.editors.ObjectEditDialogMaker;
import org.opensim.view.pub.ViewDB;
import vtk.vtkAVIWriter;
import vtk.vtkActor;
import vtk.vtkAssembly;
import vtk.vtkAssemblyNode;
import vtk.vtkAssemblyPath;
import vtk.vtkCellPicker;
import vtk.vtkMatrix4x4;
import vtk.vtkPropPicker;
import vtk.vtkWindowToImageFilter;
import vtk.vtkWorldPointPicker;

/**
 *
 * @author Ayman Habib
 * A wrapper around vtkPanel that provides common behavior beyond OpenSimBaseCanvas.
 * 
 */
public class OpenSimCanvas extends OpenSimBaseCanvas implements MouseWheelListener {
        
    JPopupMenu visibilityMenu = new JPopupMenu();

    // The object that was clicked on to initiate dragging
    OpenSimObject dragObject = null;
    double[] dragPtOld = null; // XYZ coords in world frame of point under cursor during last drag event
    double dragStartZ = 0.0; // Z value in camera frame of point under cursor when dragging began
    int lastLeftButtonClickCount = 0;
    OpenSimObject lastLeftButtonClickObject = null;
    boolean profile=false;
    
    /** Creates a new instance of OpenSimCanvas */
    public OpenSimCanvas() {
        addMouseWheelListener(this);
        //profile=(OpenSimObject.getDebugLevel()>=2);
    }
    
    public int getLastX() {
        return lastX;
    }
    
    public int getLastY() {
        return lastY;
    }
    /**
     * Event handler to handle mousePressed
     * Should delegate the call to motion displayers as well to enable selection of motion objects
     */
   public void mousePressed(MouseEvent e)
   {
      lastX = e.getX();
      lastY = e.getY();

      int keyMods = e.getModifiers();
      double[] worldPos = new double[3];
      OpenSimObject leftClickobj = null;
      if ((keyMods & InputEvent.BUTTON1_MASK) > 0) {
         leftClickobj = findObjectAt(e.getX(), e.getY(), worldPos);

         // Some code to handle double clicking on an object, but which does so in a way that the sequence
         // CTRL-Click and Click does not count as a double click.  This avoids
         // treating as double click the case where the user selects an object
         // (CTRL-Click) and quickly starts dragging (Click & Drag) 
         if (leftClickobj != null && !ViewDB.getInstance().isPicking()) {
            if (e.getClickCount() == lastLeftButtonClickCount+1 && leftClickobj == lastLeftButtonClickObject) {
               ViewDB.getInstance().setSelectedObject(leftClickobj);
               return; 
            } else {
               lastLeftButtonClickCount = e.getClickCount();
               lastLeftButtonClickObject = leftClickobj;
            } 
         } else {
            lastLeftButtonClickCount = -1;
            lastLeftButtonClickObject = null;
         }
      }

      if (ViewDB.getInstance().isPicking() == true && (keyMods & InputEvent.BUTTON1_MASK) > 0) {
         if ((keyMods & InputEvent.SHIFT_MASK) > 0) {
            if (leftClickobj == null) {
               // do nothing, a la Illustrator
            } else {
               ViewDB.getInstance().toggleAddSelectedObject(leftClickobj);
            }
         } else {
            if (leftClickobj == null) {
               ViewDB.getInstance().clearSelectedObjects();
            } else {
               ViewDB.getInstance().replaceSelectedObject(leftClickobj);
            }
         }
      } else if ((leftClickobj != null) && ViewDB.getInstance().isSelected(leftClickobj) == true) {
         //System.out.println("dragging on");
         dragObject = leftClickobj;
         double[] dragZ = new double[4];
         dragPtOld = new double[4];
         dragPtOld[0] = worldPos[0];
         dragPtOld[1] = worldPos[1];
         dragPtOld[2] = worldPos[2];
         dragPtOld[3] = 1.0;
         vtkMatrix4x4 vtm = cam.GetViewTransformMatrix();
         vtm.MultiplyPoint(dragPtOld, dragZ);
         dragStartZ = dragZ[2];
         //System.out.println("Old Drag init="+dragPtOld[0]+", "+dragPtOld[1]+", "+dragPtOld[2]);
         ViewDB.getInstance().setDragging(true, dragObject);
      } else {
         super.mousePressed(e);
      }
   }

    public void mouseReleased(MouseEvent e)
    {
       int keyMods = e.getModifiers();
       if (ViewDB.getInstance().isPicking() == true && (keyMods & InputEvent.BUTTON1_MASK) > 0) {
          // do nothing; you're still in picking mode
       } else if (ViewDB.getInstance().isDragging() == true && (keyMods & InputEvent.BUTTON1_MASK) > 0) {
          //System.out.println("dragging off");
          dragObject = null;
          dragPtOld = null;
          dragStartZ = 0.0;
          ViewDB.getInstance().setDragging(false, dragObject);
       } else {
          super.mouseReleased(e);
       }
    }

   // Callback invoked when the user doubleclicks an object in the graphics window
   //--------------------------------------------------------------------------
   void handleDoubleClick( OpenSimObject osimObject ) {
       
       ModelWindowVTKTopComponent ownerWindow = ViewDB.getInstance().getCurrentModelWindow();
                
       // If this is a rigid body, open the easy-to-use rigid body property editor (also provides the older table version).
       /* Body bodyThatTalksToCpp = (osimObject == null) ? null : Body.safeDownCast(osimObject);
       if( bodyThatTalksToCpp != null ) 
           LSJava.LSPropertyEditors.LSPropertyEditorRigidBody.NewLSPropertyEditorRigidBody( osimObject, null, ownerWindow );     
       else{ */
          ObjectEditDialogMaker editorDialog = new ObjectEditDialogMaker( osimObject, ownerWindow );
          editorDialog.process();
          ViewDB.getInstance().statusDisplaySelectedObjects();
       //}
   }

   private void setPicking(boolean enabled) {
      ViewDB.getInstance().setPicking(enabled);
      if(enabled) setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
      else setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
   }

   public void keyPressed(KeyEvent e) {
      if (e.getKeyCode() == KeyEvent.VK_CONTROL) setPicking(true);
      else super.keyPressed(e);
   }

   public void keyReleased(KeyEvent e) {
      if (e.getKeyCode() == KeyEvent.VK_CONTROL){ 
          setPicking(false);
          //if (ViewDB.getInstance().isQuery()){
          //    ViewDB.getInstance().setQuery(false);
          //    fireStateChanged(); // to toggle off the annotations button
          //}
      }
      else super.keyReleased(e);
   }

   // Update picking status on mouse enter because CTRL may have been 
   // pressed/released while we were not in focus
   public void mouseEntered(MouseEvent e) { 
      super.mouseEntered(e); 
      setPicking((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0);
   }

   public void mouseDragged(MouseEvent e) {
       
//       System.out.println("e.getComponent() = "+e.getComponent());
//       System.out.println("e.getID() = "+e.getID());
//       System.out.println("e.getWhen() = "+e.getWhen());
//       System.out.println("e.getModifiers() = "+e.getModifiers());
//       System.out.println("e.getX() = "+e.getX());
//       System.out.println("e.getY() = "+e.getY());
//       System.out.println("e.getClickCount() = "+e.getClickCount());
//       System.out.println("e.isPopupTrigger() = "+e.isPopupTrigger());
//       System.out.println("e.getButton() = "+e.getButton());
       
      int keyMods = e.getModifiers();
      if (ViewDB.getInstance().isPicking() == true && (keyMods & InputEvent.BUTTON1_MASK) > 0) {
         // do nothing; you're still in picking mode
         lastX = e.getX();
         lastY = e.getY();
      } else if (ViewDB.getInstance().isDragging() == true && (keyMods & InputEvent.BUTTON1_MASK) > 0) {
         if (e.getX() != lastX || e.getY() != lastY) {
            if (dragObject != null && dragPtOld != null) {
               double viewAngle = cam.GetViewAngle();
               double x = e.getX();
               double y = e.getY();
               double w = getWidth();
               double h = getHeight();
               double aspectRatio = w / h;
               double angle = Math.toRadians(cam.GetViewAngle() * 0.5);
               double y_half = -dragStartZ * Math.tan(angle);
               double x_half = y_half * aspectRatio;
               double x_percent = x / w;
               double y_percent = (h - y) / h;
               // dragPtNew starts out as the XYZ position in the camera frame
               // of the point under the cursor
               double dragPtNew[] = new double[4];
               dragPtNew[0] = -x_half + x_percent * 2.0 * x_half;
               dragPtNew[1] = -y_half + y_percent * 2.0 * y_half;
               dragPtNew[2] = dragStartZ;
               dragPtNew[3] = 1.0;
               vtkMatrix4x4 vtm = cam.GetViewTransformMatrix();
               vtkMatrix4x4 vtmInverse = new vtkMatrix4x4();
               vtmInverse.DeepCopy(vtm);
               vtmInverse.Invert();
               // Now transform dragPtNew so it is in the world frame
               vtmInverse.MultiplyPoint(dragPtNew, dragPtNew);
               // the amount to drag the objects is the distance between dragPtOld and dragPtNew
               double dragVector[] = new double[3];
               dragVector[0] = dragPtNew[0] - dragPtOld[0];
               dragVector[1] = dragPtNew[1] - dragPtOld[1];
               dragVector[2] = dragPtNew[2] - dragPtOld[2];
               // drag the selected objects
               ViewDB.getInstance().dragSelectedObjects(dragObject, dragVector);
               // store the new point as the old, for use next time
               dragPtOld[0] = dragPtNew[0];
               dragPtOld[1] = dragPtNew[1];
               dragPtOld[2] = dragPtNew[2];
            }
         }
         lastX = e.getX();
         lastY = e.getY();
      } else {
         super.mouseDragged(e);
      }
   }

    /**
     *  Utility method to locate an OpenSimObject located atscreen coordinate  x, y.
     *  Delegates the call to modelVisuals. PRimarily used for the side-effects of picking
     */
    OpenSimObject findObjectAt(int x, int y, double[] worldPosition)
    {
        vtkPropPicker picker = new vtkPropPicker();

        Lock();
        picker.Pick(x, rw.GetSize()[1] - y, 0, ren);
        UnLock();

        return null;
    }
 
    /*
    void addNewModelVisuals(SingleModelVisuals newModelVisual) {
        vtkAssembly assembly = newModelVisual.getModelDisplayAssembly();
        GetRenderer().AddViewProp(assembly);
        repaint();
    }
    /**
     * Temporarily hide a model from the current view
     */

   public void lockDrawingSurface(boolean toLock) {
      if (toLock)
         Lock();
      else
         UnLock();
   }
   
   // support for writing AVI movies.
   vtkAVIWriter movieWriter=null;
   String movieFilePath = "";
   int frameCounter = 1;
   static public boolean movieWriterReady=false; // static to represent if movie is being written in any view
   vtkWindowToImageFilter imageFilter=null;
   boolean saveFramesLocal = false;
   /**
    * Create a movie with the specified filename
    */
   public void createMovie(String fileName, boolean saveFramesOnly) {
       // Workaround failure to create movies on 64 bit windows
       saveFramesLocal = saveFramesOnly;
       if (saveFramesOnly){
            movieFilePath = fileName+File.separator;    
            frameCounter = 1;           
       }
       else {
           movieWriter = new vtkAVIWriter();
           movieWriter.SetFileName(fileName);
           imageFilter = new vtkWindowToImageFilter();
           imageFilter.SetMagnification(1);
           imageFilter.SetInput(rw);
           imageFilter.ReadFrontBufferOff();
           imageFilter.Update();

           movieWriter.SetInputConnection(imageFilter.GetOutputPort());
           movieWriter.Start();
       }
       movieWriterReady=true;
   }

    public void Render() {
        long before = 0;
        if (profile) before=System.nanoTime();
        //ViewDB.getInstance().setTextCamera(GetRenderer().GetActiveCamera());
        super.Render();
        if (profile) {
          long after=System.nanoTime();
          OpenSimLogger.logMessage("Render time: "+1e-6*(after-before)+" ms.\n", OpenSimLogger.INFO);
        }
        if (movieWriterReady){
            if (saveFramesLocal){
                String fullPath = movieFilePath.concat("Frame"+String.valueOf(frameCounter))+".tiff";
                HardCopy(fullPath, 1);
                frameCounter++;
            } else{
                if (movieWriter!=null){
                    imageFilter.Modified();
                    imageFilter.Update();
                    movieWriter.Write();
                }
            }
        }
    }
    
    public void finishMovie(boolean saveFramesOnly) {
        if (!saveFramesOnly){
            movieWriter.End();
        }
        movieWriter=null;
        movieWriterReady=false;
    }
    
    public void mouseWheelMoved(MouseWheelEvent e) {
        String message;
        int notches = e.getWheelRotation();
        GetRenderer().GetActiveCamera().Zoom(1.+.02*notches);
        repaint();
    }

}
