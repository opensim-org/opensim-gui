/* -------------------------------------------------------------------------- *
 * OpenSim: OpenSimBaseCanvas.java                                            *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib, Kevin Xu, Paul Mitiguy, Jeff Reinbolt              *
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
 *
 * OpenSimBaseCanvas
 * Author(s): Ayman Habib & Jeff Reinbolt
 */
package org.opensim.view.base;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.opensim.utils.Prefs;
import org.opensim.utils.TheApp;
import org.opensim.view.Camera;
import org.opensim.view.pub.GeometryFileLocator;
import org.opensim.view.pub.ViewDB;
import vtk.vtkActor2D;
import vtk.vtkCamera;
import vtk.vtkImageData;
import vtk.vtkImageMapper;
import vtk.vtkLightCollection;
import vtk.vtkPNGReader;
import vtk.vtkPanel;

/**
 *
 * @author Ayman Habib
 *
 * Base class for the Canvas to collect all the properties to be shared by OpenSim based
 * applications and to enforce behvior (e.g colors, camera, mouse interaction) that's not specific
 * to OpenSim's Top Gui Application.
 */
public class OpenSimBaseCanvas extends vtkPanel
        implements KeyListener, MouseListener, MouseWheelListener {
   
   String defaultBackgroundColor="0.15, 0.15, 0.15";
   int    numAAFrames=0;
   JPopupMenu settingsMenu = new JPopupMenu();
   CamerasMenu camerasMenu;

   double currentTime = 0;
   Camera camera = null;
   vtkImageMapper logoMapper = new vtkImageMapper();
   // Enable opoups to display on top of heavy weight component/canvas
   static {
      JPopupMenu.setDefaultLightWeightPopupEnabled(false);
      javax.swing.ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
   }
   /** Creates a new instance of OpenSimBaseCanvas */
   public OpenSimBaseCanvas() {
      //defaultBackgroundColor = TheApp.getCurrentVersionPreferences().get("BackgroundColor", defaultBackgroundColor);
      double[] background = Prefs.parseColor(defaultBackgroundColor);
      GetRenderer().SetBackground(background);
      //GetRenderer().EraseOff();
      //createSettingsMenu();
      camerasMenu = new CamerasMenu(this);
      addKeyListener(this);
      addMouseWheelListener(this);
      addLogo();
      // AntiAliasing
      int desiredAAFrames = 0;//TheApp.getCurrentVersionPreferences().getInt("AntiAliasingFrames", numAAFrames);
      if (desiredAAFrames >=0 && desiredAAFrames<=10){
         numAAFrames=desiredAAFrames;
      }
      GetRenderWindow().SetAAFrames(numAAFrames);
   }
   
   public void mousePressed(MouseEvent e) {
      super.mousePressed(e);
      // Disabled popup for now
      /*
      // Show popup if right mouse and Shift key, otherwise pass along to super implementation
      if ((e.getModifiers() == (InputEvent.BUTTON3_MASK | InputEvent.SHIFT_MASK))) {
         settingsMenu.show(this, e.getX(), e.getY());
      } else {
         super.mousePressed(e);
      }
      */
   }
   
   public void mouseDragged(MouseEvent e) {
      super.mouseDragged(e);
//      lastX = e.getX();
//      lastY = e.getY();
      //System.out.println("mouseMoved "+lastX+" "+lastY); 
//      fireStateChanged();
      // Disabled popup for now
      /*
      // do nothing (handled by settingsMenu) if right mouse and Shift, otherwise pass along to super implementation
      if ((e.getModifiers() == (InputEvent.BUTTON3_MASK | InputEvent.SHIFT_MASK))) {
      } else {
         super.mouseDragged(e);
      }
      */
   }
   
   public void mouseWheelMoved(MouseWheelEvent e) {
       if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
           int rotation  = e.getUnitsToScroll();
           if (rotation > 0) {
               GetRenderer().GetActiveCamera().Zoom(1.05);
           } else {
               GetRenderer().GetActiveCamera().Zoom(0.95);
           }
           repaint();
       }
   }
   
   /**
    * Handle keys for default cameras, otherwise pass on to super
    * to get default vtkPanel behavior.
    */
   public void keyPressed(KeyEvent e) {
      char keyChar = e.getKeyChar();
      int keyCode = e.getKeyCode();
      
      if ('x' == keyChar) {
         applyCameraMinusX();
      } else if ('y' == keyChar) {
         applyCameraMinusY();
      } else if ('z' == keyChar) {
         applyCameraMinusZ();
      } else if ('i' == keyChar) {
         GetRenderer().GetActiveCamera().Zoom(1.05);
         repaint();
      } else if ('o' == keyChar) {
         GetRenderer().GetActiveCamera().Zoom(0.95);
         repaint();
      } else if (KeyEvent.VK_LEFT == keyCode) {
         panCamera(-5.0, 0.0);
         repaint();
      } else if (KeyEvent.VK_RIGHT == keyCode) {
         panCamera(5.0, 0.0);
         repaint();
      } else if (KeyEvent.VK_UP == keyCode) {
         panCamera(0.0, -5.0);
         repaint();
      } else if (KeyEvent.VK_DOWN == keyCode) {
         panCamera(0.0, 5.0);
         repaint();
      }else if (KeyEvent.VK_J == keyCode) {
         rotateCamera(5.0);
         repaint();
      }else if (KeyEvent.VK_K == keyCode) {
         rotateCamera(-5.0);
         repaint();
      }
      fireStateChanged();
      super.keyPressed(e);
   }
   
   public void applyCameraPlusY() {
      applyCamera(camerasMenu.pickStandardCamera("Bottom"));
   }
   
   public void applyCameraMinusY() {
      applyCamera(camerasMenu.pickStandardCamera("Top"));
   }
   
   public void applyCameraPlusZ() {
      applyCamera(camerasMenu.pickStandardCamera("Left"));
   }
   
   public void applyCameraMinusZ() {
      applyCamera(camerasMenu.pickStandardCamera("Right"));
   }
   
   public void applyCameraPlusX() {
      applyCamera(camerasMenu.pickStandardCamera("Back"));
   }
   
   public void applyCameraMinusX() {
      applyCamera(camerasMenu.pickStandardCamera("Front"));
   }

   // Overrides vtkPanel.resetCamera() to reset camera on selected objects (if any)
   public void resetCamera() {
      Lock();
      UnLock();
   } 
      
   public void processKey(char keyChar){
      if ('x' == keyChar) {
         applyCameraMinusX();
      } else if ('y' == keyChar) {
         applyCameraMinusY();
      } else if ('z' == keyChar) {
         applyCameraMinusZ();
      } else if ('i' == keyChar) {
         GetRenderer().GetActiveCamera().Zoom(1.05);
         repaint();
      } else if ('o' == keyChar) {
         GetRenderer().GetActiveCamera().Zoom(0.95);
         repaint();
      } else if ('j' == keyChar) {
         rotateCamera(5.0);
         repaint();
      }else if ('k' == keyChar) {
         rotateCamera(-5.0);
         repaint();
      }
   }
   /**
    * A method to apply a prespecified Camera (selectedCamera) to the current Canvas
    */
   public void applyCamera(vtkCamera selectedCamera) {
      applyOrientation(selectedCamera);
      
      vtkLightCollection lights = GetRenderer().GetLights();
      lights.RemoveAllItems();
      GetRenderer().CreateLight();

      resetCamera();

      //GetRenderer().Render();
      repaint();
   }

    public void applyOrientation(final vtkCamera selectedCamera) {
        vtkCamera currentCamera = GetRenderer().GetActiveCamera();
        currentCamera.SetPosition(selectedCamera.GetPosition());
        currentCamera.SetFocalPoint(selectedCamera.GetFocalPoint());
        currentCamera.SetViewAngle(selectedCamera.GetViewAngle());
        currentCamera.SetDistance(selectedCamera.GetDistance());
        currentCamera.SetClippingRange(selectedCamera.GetClippingRange());
        currentCamera.SetViewUp(selectedCamera.GetViewUp());
        currentCamera.SetParallelScale(selectedCamera.GetParallelScale());
    }

   //========================================================================
   // Methods for new (animatable) Camera class
   //========================================================================

   public void setCamera(Camera camera) {
      System.out.println("OpenSimBaseCanvas.setCamera "+((camera!=null)?camera.getName():""));
      this.camera = camera;
      applyTime(currentTime);
   }
   public Camera getCamera() { return camera; }

   // TODO: really we should be querying the motion controller for the current time, but it's inaccessible from this package
   public double getCurrentTime() { return currentTime; }

   public void applyCameraConfiguration(Camera.Configuration config, boolean doRepaint) {
      config.applyToView(this);
      vtkLightCollection lights = GetRenderer().GetLights();
      lights.RemoveAllItems();
      GetRenderer().CreateLight();
      if(doRepaint) repaint();
   }
  
   public void applyTime(double time) {
      // Cache time since we don't have direct access to the motion controller
      currentTime = time;

      // if camera not null, apply modified camera
      // if not animated camera, don't need to do anything (assume that when user switched to that
      // nonanimated camera, the proper configuration was selected and that was good enough
      if(camera!=null && camera.getNumKeyFrames()>0) {
         applyCameraConfiguration(camera.getConfiguration(currentTime), false);
      }
   } 

   // mouseEntered and being a MouseListener is done for the specific purpose
   // of not calling super.mouseEnetered as it requests focus which throws modeless windows 
   // on top of the vtk window to the back. Alternatively we can change vtkPanel (but 
   // that's messy since it's a part of the standard VTK distribution).
    public void mouseEntered(MouseEvent e) {
        // super.mouseEntered(e);
    }
    
    public void panCamera(double x, double y){
        ren = GetRenderer();
        cam = ren.GetActiveCamera();
        rw = ren.GetRenderWindow();
        double  FPoint[];
        double  PPoint[];
        double  APoint[] = new double[3];
        double  RPoint[];
        double focalDepth;
        
        // get the current focal point and position
        FPoint = cam.GetFocalPoint();
        PPoint = cam.GetPosition();
        
        // calculate the focal depth since we'll be using it a lot
        ren.SetWorldPoint(FPoint[0],FPoint[1],FPoint[2],1.0);
        ren.WorldToDisplay();
        focalDepth = ren.GetDisplayPoint()[2];
        
        APoint[0] = rw.GetSize()[0]/2.0 + (x);
        APoint[1] = rw.GetSize()[1]/2.0 - (y);
        APoint[2] = focalDepth;
        ren.SetDisplayPoint(APoint);
        ren.DisplayToWorld();
        RPoint = ren.GetWorldPoint();
        if (RPoint[3] != 0.0)
          {
            RPoint[0] = RPoint[0]/RPoint[3];
            RPoint[1] = RPoint[1]/RPoint[3];
            RPoint[2] = RPoint[2]/RPoint[3];
          }
        
        /*
         * Compute a translation vector, moving everything 1/2 
         * the distance to the cursor. (Arbitrary scale factor)
         */
        cam.SetFocalPoint(
                          (FPoint[0]-RPoint[0])/2.0 + FPoint[0],
                          (FPoint[1]-RPoint[1])/2.0 + FPoint[1],
                          (FPoint[2]-RPoint[2])/2.0 + FPoint[2]);
        cam.SetPosition(
                        (FPoint[0]-RPoint[0])/2.0 + PPoint[0],
                        (FPoint[1]-RPoint[1])/2.0 + PPoint[1],
                        (FPoint[2]-RPoint[2])/2.0 + PPoint[2]);
        resetCameraClippingRange();
    }
    
   // -----------------------------------------------------------------------
   // Change listener stuff taken from DefaultColorSelectionModel.java
   // -----------------------------------------------------------------------
   protected transient ChangeEvent changeEvent = null;
   protected EventListenerList listenerList = new EventListenerList();
   public void addChangeListener(ChangeListener l) { listenerList.add(ChangeListener.class, l); }
   public void removeChangeListener(ChangeListener l) { listenerList.remove(ChangeListener.class, l); }
   protected void fireStateChanged()
   {
      Object[] listeners = listenerList.getListenerList();
      for (int i = listeners.length - 2; i >= 0; i -=2 ) {
         //System.out.println(listeners.length);
         if (listeners[i] == ChangeListener.class) {
            if (changeEvent == null) {
               changeEvent = new ChangeEvent(this);
            }
            ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
         }
      }
   }
   // -----------------------------------------------------------------------

    private void rotateCamera(double d) {
       cam = ren.GetActiveCamera();
       cam.Azimuth(d);
       vtkLightCollection lights = GetRenderer().GetLights();
      lights.RemoveAllItems();
      GetRenderer().CreateLight();
      repaint();
    }

    public void addLogo() {

        vtkPNGReader imageReader=new vtkPNGReader();
        String fullFileName= TheApp.getApplicationLogoFileName();
        if (fullFileName==null) return;
        if (!(new File(fullFileName).exists())) return;
        imageReader.SetFileName(fullFileName);
        imageReader.UpdateWholeExtent();
        imageReader.Update();
        int[] ext = imageReader.GetDataExtent();
        vtkImageData imageData = imageReader.GetOutput();
        
        logoMapper.SetInput(imageData);
        
        updateLogoForBackgoundColor();
        // use 1000 for light bgnd, 
        //System.out.println("Color Level = "+logoMapper.GetColorLevel());
        addLogoActor(logoMapper, 0, 10, 10, 0.05, 0.05); 
        //repaint();
   }
    
    private void addLogoActor(vtkImageMapper mapper, int isPickable, int dispPosX, int dispPosY, double width, double height) {
        vtkActor2D logoActor = new vtkActor2D();
        logoActor.SetMapper(mapper);
        logoActor.SetPickable(isPickable);
        logoActor.SetDisplayPosition(dispPosX, dispPosY);
        logoActor.SetWidth(width);
        logoActor.SetHeight(height);
        GetRenderer().AddActor2D(logoActor);
    }
    /**
     * Compute what level 0-1000 to use 1000 works best on light background, 0 on dark
     * @return 
     */
    private double[] computeColorLevelAndWindowForBackgound() {
        double[] bgnd = GetRenderer().GetBackground();
        double avg = 0.0; 
        for (int i=0; i<3; i++) {
            avg += bgnd[i];
        }
        avg /= 3;
            
        // If intensity >= 0.21 use one setting for ColorWindow and ColorLevel, else use another
        double[] colorWindowAndLevel = (avg >= 0.21) ? new double[]{1000, 500} : new double[]{250,  175};
        
        return colorWindowAndLevel;
        
    }

    public void updateLogoForBackgoundColor() {
       
       double[] colorWindowAndLevel = computeColorLevelAndWindowForBackgound();
               
       logoMapper.SetColorWindow(colorWindowAndLevel[0]);
       logoMapper.SetColorLevel(colorWindowAndLevel[1]);
 
    }
 }
