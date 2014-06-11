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
