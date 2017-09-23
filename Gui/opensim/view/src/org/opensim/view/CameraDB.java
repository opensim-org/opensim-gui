/* -------------------------------------------------------------------------- *
 * OpenSim: CameraDB.java                                                     *
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
