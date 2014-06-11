/*
 *
 * CamerasMenu
 * Author(s): Ayman Habib & Jeff Reinbolt
 * Copyright (c)  2005-2006, Stanford University, Ayman Habib & Jeff Reinbolt
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
package org.opensim.view.base;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import vtk.vtkCamera;
import vtk.vtkLightCollection;

/**
 *
 * @author Ayman & Jeff Reinbolt
 *
 * A class to create a list of cameras with standard views.
 */
public class CamerasMenu extends JMenu {
    
    OpenSimBaseCanvas dCanvas;
    static vtkCamera minusXCamera, minusZCamera, minusYCamera;
    static vtkCamera plusXCamera, plusZCamera, plusYCamera;
    
    static ArrayList<vtkCamera> availableCameras = new ArrayList<vtkCamera>();
    static ArrayList<String> availableCameraNames = new ArrayList<String>();
    static ArrayList<JMenuItem> availableCameraItems = new ArrayList<JMenuItem>();
    static boolean initialzed = false;

   /** Creates a new instance of CamerasMenu */
    public CamerasMenu(OpenSimBaseCanvas aCanvas) {
        super("Cameras");
        dCanvas = aCanvas;
        if (!initialzed){
            createDefaultCameras();
            createCameraActions();
            for( int i=0; i < availableCameras.size(); i++ )
                add(availableCameraItems.get(i));
            initialzed = true;
        }
    }
    
    private void createDefaultCameras()
    {
        plusXCamera = new vtkCamera(); // +x
        plusXCamera.Azimuth(-90);
        plusXCamera.Elevation(0);
        plusXCamera.Roll(0);
        plusXCamera.SetViewAngle(30);
        plusXCamera.SetFocalPoint(0, 0, 0);
        plusXCamera.SetViewUp(0, 1, 0);
        
        minusXCamera = new vtkCamera(); // -x
        minusXCamera.Azimuth(90);
        minusXCamera.Elevation(0);
        minusXCamera.Roll(0);
        minusXCamera.SetViewAngle(30);
        minusXCamera.SetFocalPoint(0, 0, 0);
        minusXCamera.SetViewUp(0, 1, 0);

        plusZCamera = new vtkCamera(); // +z
        plusZCamera.Azimuth(180);
        plusZCamera.Elevation(0);
        plusZCamera.Roll(0);
        plusZCamera.SetViewAngle(30);
        plusZCamera.SetFocalPoint(0, 0, 0);
        plusZCamera.SetViewUp(0, 1, 0);
        
        minusZCamera = new vtkCamera(); // -z
        minusZCamera.Azimuth(0);
        minusZCamera.Elevation(0);
        minusZCamera.Roll(0);
        minusZCamera.SetViewAngle(30);
        minusZCamera.SetFocalPoint(0, 0, 0);
        minusZCamera.SetViewUp(0, 1, 0);
        
        plusYCamera = new vtkCamera(); // +y
        plusYCamera.SetViewUp(1, 0, 0);
        plusYCamera.Azimuth(90);
        plusYCamera.Elevation(0);
        plusYCamera.Roll(0);
        plusYCamera.SetViewAngle(30);
        plusYCamera.SetFocalPoint(0, 0, 0);
         
        minusYCamera = new vtkCamera(); // -y
        minusYCamera.SetViewUp(-1, 0, 0);
        minusYCamera.Azimuth(90);
        minusYCamera.Elevation(0);
        minusYCamera.Roll(0);
        minusYCamera.SetViewAngle(30);
        minusYCamera.SetFocalPoint(0, 0, 0);

        availableCameras.add(plusXCamera);
        availableCameraNames.add("Back");
        availableCameras.add(minusXCamera);
        availableCameraNames.add("Front");
        availableCameras.add(plusZCamera);
        availableCameraNames.add("Left");
        availableCameras.add(minusZCamera);
        availableCameraNames.add("Right");
        availableCameras.add(plusYCamera);
        availableCameraNames.add("Bottom");
        availableCameras.add(minusYCamera);
        availableCameraNames.add("Top");
    }

    private void createCameraActions()
    {
        for(int i=0; i < availableCameras.size(); i++){
            setPickCameraAction cameraAction = new setPickCameraAction();
            JMenuItem cameraItem = new JMenuItem(cameraAction);
            cameraItem.setActionCommand(availableCameraNames.get(i));
            cameraItem.setText(availableCameraNames.get(i));
            availableCameraItems.add(cameraItem);
        }
        // Add a new Camera definition option
        
        
    }
    /**
     * Pick one of the standard Front, Top, Side Cameras using fullname.
     * Eventually the user will be able to add some and assocate a keyboard binding with it
     * if not already in use.
     */
    static public vtkCamera pickStandardCamera(String camName)
    {
        int index = availableCameraNames.indexOf((String)camName);
        if (index != -1){
            return availableCameras.get(index);
        }
        return null;
    }

    /**
     * Handle button push of Camera Actions
     **/
   private class setPickCameraAction extends AbstractAction {

    /**
     * actionPerformed
     *
     * @param e ActionEvent
     */
    public void actionPerformed(ActionEvent e) {
      String cameraName = e.getActionCommand();
      int cameraIndex = availableCameraNames.indexOf((Object) cameraName);
        vtkCamera selectedCamera = availableCameras.get(cameraIndex);
        CamerasMenu.this.dCanvas.applyCamera(selectedCamera);
      }
    }
  }

