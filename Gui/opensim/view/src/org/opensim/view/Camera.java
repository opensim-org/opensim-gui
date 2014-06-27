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
import java.io.IOException;
import java.util.Vector;
import org.opensim.utils.Interpolate;
import org.opensim.utils.Vec3;
import vtk.vtkCamera;
import vtk.vtkPanel;
   
// TODO: generalize concept of animatable quantities

public class Camera 
{
   public static class Configuration {
      public double roll = 0;
      public double distance = 1;
      public double viewAngle = 30;
      public Vec3 position = new Vec3();
      public Vec3 focalPoint = new Vec3();
      public Vec3 direction = new Vec3();
      public Vec3 viewUp = new Vec3();
      public double[] clippingRange = new double[]{0, 1000};

      public static Configuration interpolate(
            Configuration config1, Configuration config2, double t) 
      {
         Configuration config = new Configuration();
         config.roll = Interpolate.linear(config1.roll, config2.roll, t);
         config.distance = Interpolate.linear(config1.distance, config2.distance, t);
         config.viewAngle = Interpolate.linear(config1.viewAngle, config2.viewAngle, t);

         config.focalPoint = Interpolate.linear(config1.focalPoint, config2.focalPoint, t);
         //config.position = Interpolate.linear(config1.position, config2.position, t);
         Vec3 direction = Interpolate.spherical(config1.direction, config2.direction, t);
         config.position = Vec3.add(1, config.focalPoint, -config.distance, direction);

         for(int i=0; i<2; i++) config.clippingRange[i] = Interpolate.linear(config1.clippingRange[i], config2.clippingRange[i], t);

         return config;
      }

      public String toString() {
         return String.format("rl=%.3f dist=%.3f pos=%s foc=%s dir=%s nea=%.3f far=%.3f ang=%.3f",
               roll,distance,position.toString(),focalPoint.toString(),direction.toString(),
               clippingRange[0],clippingRange[1],viewAngle);
      }

      public static Configuration getFromView(vtkPanel panel) {
         vtkCamera currentCamera = panel.GetRenderer().GetActiveCamera();
         Configuration config = new Configuration();
         config.roll = currentCamera.GetRoll();
         config.distance = currentCamera.GetDistance();
         config.viewAngle = currentCamera.GetViewAngle();
         config.position = new Vec3(currentCamera.GetPosition());
         config.focalPoint = new Vec3(currentCamera.GetFocalPoint());
         config.direction = new Vec3(currentCamera.GetDirectionOfProjection());
         config.viewUp = new Vec3(currentCamera.GetViewUp());
         config.clippingRange = currentCamera.GetClippingRange().clone();
         return config;
      }

      public void applyToView(vtkPanel panel) {
         vtkCamera currentCamera = panel.GetRenderer().GetActiveCamera();
         currentCamera.SetPosition(position.get());
         currentCamera.SetFocalPoint(focalPoint.get());
         currentCamera.SetViewAngle(viewAngle);
         currentCamera.SetRoll(roll);
         //currentCamera.SetDistance(distance);
         //currentCamera.SetViewUp(viewUp);
         currentCamera.SetClippingRange(clippingRange);
      }

      public void read(BufferedReader reader) throws IOException {
         // TODO: camera parameters are always serialized in English:UnitedStates
         // locale, right? So NumberFormat.parse() is not needed to handle
         // numbers like 0,284
         roll = Double.parseDouble(reader.readLine());
         distance = Double.parseDouble(reader.readLine());
         viewAngle = Double.parseDouble(reader.readLine());
         for(int i=0; i<3; i++) position.get()[i] = Double.parseDouble(reader.readLine());
         for(int i=0; i<3; i++) focalPoint.get()[i] = Double.parseDouble(reader.readLine());
         for(int i=0; i<3; i++) direction.get()[i] = Double.parseDouble(reader.readLine());
         for(int i=0; i<3; i++) viewUp.get()[i] = Double.parseDouble(reader.readLine());
         for(int i=0; i<2; i++) clippingRange[i] = Double.parseDouble(reader.readLine());
      }

      public void write(BufferedWriter writer) throws IOException {
         writer.write(roll+"\n");
         writer.write(distance+"\n");
         writer.write(viewAngle+"\n");
         for(int i=0; i<3; i++) writer.write(position.get(i)+"\n");
         for(int i=0; i<3; i++) writer.write(focalPoint.get(i)+"\n");
         for(int i=0; i<3; i++) writer.write(direction.get(i)+"\n");
         for(int i=0; i<3; i++) writer.write(viewUp.get(i)+"\n");
         for(int i=0; i<2; i++) writer.write(clippingRange[i]+"\n");
      }
   }

   private class ConfigurationKeyFrame {
      public double time;
      public Configuration config;
      public ConfigurationKeyFrame(double time, Configuration config) { 
         this.time = time; this.config = config;
      }
   }

   private String name;
   private Vector<ConfigurationKeyFrame> configurationKeyFrames = new Vector<ConfigurationKeyFrame>();

   public String getName() { return name; }
   public void setName(String name) { 
      this.name = name; 
      CameraDB.getInstance().fireEvent(new CameraEvent(this, this, CameraEvent.Operation.CameraRenamed));
   }

   public int getNumKeyFrames() { return configurationKeyFrames.size(); }
   public double getKeyFrameTime(int i) { return configurationKeyFrames.get(i).time; }
   public void setKeyFrameTime(int i, double time) { 
      if(configurationKeyFrames.get(i).time != time) {
         configurationKeyFrames.get(i).time = time;
         CameraDB.getInstance().fireEvent(new CameraEvent(this, this, CameraEvent.Operation.CameraKeyFrameModified));
      }
   }

   public Configuration getKeyFrameConfiguration(int i) { return configurationKeyFrames.get(i).config; }
   public void setKeyFrameConfiguration(int i, Configuration config) {
      configurationKeyFrames.get(i).config = config; 
      CameraDB.getInstance().fireEvent(new CameraEvent(this, this, CameraEvent.Operation.CameraKeyFrameModified));
   }

   public void addKeyFrame(double time, Configuration config) {
      boolean inserted = false;
      for(int i=0; i<configurationKeyFrames.size();i++) {
         if(configurationKeyFrames.get(i).time>time) {
            configurationKeyFrames.add(i,new ConfigurationKeyFrame(time,config));
            inserted = true;
            break;
         }
      }
      if(!inserted) { configurationKeyFrames.add(new ConfigurationKeyFrame(time,config)); }
      CameraDB.getInstance().fireEvent(new CameraEvent(this, this, CameraEvent.Operation.CameraKeyFrameAdded));
   }
   public void removeKeyFrame(int i) {
      configurationKeyFrames.remove(i);
      CameraDB.getInstance().fireEvent(new CameraEvent(this, this, CameraEvent.Operation.CameraKeyFrameRemoved));
   }

   private double transitionFunction(double t) { 
      return 0.5+0.5*Math.sin((t-0.5)*Math.PI);
      //return t; 
   }

   public Configuration getConfiguration(double time) {
      if(getNumKeyFrames()==0) return null;
      if(time < getKeyFrameTime(0)) return getKeyFrameConfiguration(0);
      for(int i=0; i<getNumKeyFrames()-1; i++)
         if(getKeyFrameTime(i+1)>time) {
            double t0 = getKeyFrameTime(i);
            double t1 = getKeyFrameTime(i+1);
            return Configuration.interpolate(
                  getKeyFrameConfiguration(i),
                  getKeyFrameConfiguration(i+1),
                  transitionFunction((time-t0)/(t1-t0)));
         }
      return getKeyFrameConfiguration(getNumKeyFrames()-1);
   }

   public void read(BufferedReader reader) throws IOException{
      name = reader.readLine();
      int n = Integer.parseInt(reader.readLine());
      configurationKeyFrames.setSize(0);
      for(int i=0; i<n; i++) {
         double time = Double.parseDouble(reader.readLine());
         Configuration config = new Configuration();
         config.read(reader);
         configurationKeyFrames.add(new ConfigurationKeyFrame(time,config));
      }
   }

   public void write(BufferedWriter writer) throws IOException {
      writer.write(name+"\n");
      writer.write(getNumKeyFrames()+"\n");
      for(int i=0; i<getNumKeyFrames(); i++) {
         writer.write(getKeyFrameTime(i)+"\n");
         getKeyFrameConfiguration(i).write(writer);
      }
   }
}
