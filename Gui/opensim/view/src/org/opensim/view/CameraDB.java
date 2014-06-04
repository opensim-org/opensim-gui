/*
 * Copyright (c)  2005-2008, Stanford University
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Vector;
import java.util.Observable;

public final class CameraDB extends Observable {
   static CameraDB instance = null;
   private Vector<Camera> cameras = new Vector<Camera>();
   private boolean suppressEvents = false;

   // Singleton pattern
   private CameraDB() {}
   public static CameraDB getInstance() {
      if(instance==null) instance = new CameraDB();
      return instance;
   }

   public int getNumCameras() { return cameras.size(); }
   public Camera getCamera(int i) { return cameras.get(i); }
   
   public Camera createCamera(String name) {
      Camera camera = new Camera();
      camera.setName(name);
      cameras.add(camera);

      setChanged();
      notifyObservers(new CameraEvent(this, camera, CameraEvent.Operation.CameraAdded));
      
      return camera;
   }

      public void removeCamera(int i) {
      Camera camera = cameras.get(i);
      cameras.remove(camera);

      setChanged();
      notifyObservers(new CameraEvent(this, camera, CameraEvent.Operation.CameraRemoved));
      
      return;
   }
   
   public void fireEvent(CameraEvent event) {
      if(!suppressEvents) {
         setChanged();
         notifyObservers(event);
      }
   }

   // Loading/saving
   public boolean loadCameras(String fileName) {
      suppressEvents = true;

      try {
         InputStream ist = new FileInputStream(fileName);
         BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
         int numCameras = Integer.parseInt(reader.readLine());
         cameras.setSize(0);
         for(int i=0; i<numCameras; i++) {
            Camera camera = new Camera();
            camera.read(reader);
            cameras.add(camera);
         }
      } catch (IOException ex) {
          ex.printStackTrace();
      }

      suppressEvents = false;
      fireEvent(new CameraEvent(this,null,CameraEvent.Operation.AllDataChanged));

      return true;
   }

   public void saveCameras(String fileName) {
      try {
         OutputStream ost = new FileOutputStream(fileName);
         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ost));

         writer.write(cameras.size()+"\n");
         for(int i=0; i<cameras.size(); i++) cameras.get(i).write(writer);
         writer.flush();
         writer.close();
      } catch (IOException ex) {
          ex.printStackTrace();
      }
   }
}
