/* -------------------------------------------------------------------------- *
 * OpenSim: Vec3.java                                                         *
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

package org.opensim.utils;

public class Vec3
{
   private double[] vec = new double[]{0,0,0};

   public Vec3() { }
   public Vec3(double x, double y, double z) { vec[0] = x; vec[1] = y; vec[2] = z; }
   public Vec3(double[] vec) { this.vec = vec.clone(); }

   public double[] get() { return vec; }
   public double get(int i) { return vec[i]; }

   public void scale(double a) { vec[0]*=a; vec[1]*=a; vec[2]*=a; }

   public double magnitudeSquared() { return dot(this,this); }
   public double magnitude() { return Math.sqrt(magnitudeSquared()); }

   public void normalize() {
      double magSqr = magnitudeSquared();
      if(magSqr > 1e-8) scale(1/Math.sqrt(magSqr));
      else { vec[0]=vec[1]=vec[2]=0.0; }
   }

   public static double dot(Vec3 v1, Vec3 v2) { return v1.vec[0]*v2.vec[0] + v1.vec[1]*v2.vec[1] + v1.vec[2]*v2.vec[2]; }
   public static Vec3 add(double a, Vec3 v1, double b, Vec3 v2) { 
      return new Vec3(a*v1.vec[0]+b*v2.vec[0],
                      a*v1.vec[1]+b*v2.vec[1],
                      a*v1.vec[2]+b*v2.vec[2]);
   }

   public String toString() { return String.format("%.5f,%.5f,%.5f",vec[0],vec[1],vec[2]); }
}
