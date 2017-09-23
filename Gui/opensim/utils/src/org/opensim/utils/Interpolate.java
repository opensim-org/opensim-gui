/* -------------------------------------------------------------------------- *
 * OpenSim: Interpolate.java                                                  *
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

public class Interpolate
{
   public static double linear(double v1, double v2, double t) { return v1 + t*(v2-v1); }

   public static Vec3 linear(Vec3 v1, Vec3 v2, double t) {
      return new Vec3(v1.get(0)+t*(v2.get(0)-v1.get(0)),
                      v1.get(1)+t*(v2.get(1)-v1.get(1)),
                      v1.get(2)+t*(v2.get(2)-v1.get(2)));
   }

   public static Vec3 spherical(Vec3 v1, Vec3 v2, double t) {
      double dot = Vec3.dot(v1,v2);
      double dot_threshold = 0.9995;
      if(dot > dot_threshold) {
         // Vectors are already close - linearly interpolate and normalize
         Vec3 result = linear(v1,v2,t);
         result.normalize();
         return result;
      }
      if(dot<-1) dot=-1; else if(dot>1) dot=1;
      double theta = Math.acos(dot);
      double st = Math.sin(theta);
      double stt = Math.sin(t*theta);
      double somtt = Math.sin((1-t)*theta);
      return Vec3.add(somtt/st, v1, stt/st, v2);
   }
}
