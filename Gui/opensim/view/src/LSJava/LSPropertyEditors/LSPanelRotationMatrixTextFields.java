//--------------------------------------------------------------------------
// File:     LSPanelRotationMatrixTextFields.java
// Parents:  LSPanelMatrix3x3TextFields -> LSPanel -> Panel -> Container -> Component -> Object
// Purpose:  Holds rotation matrix information.
// Authors:  Paul Mitiguy, 2011-2012.
//--------------------------------------------------------------------------
// This work is dedicated to the public domain.
// To the maximum extent possible under law, the author(s) and contributor(s) have
// dedicated all copyright and related and neighboring rights to this software
// to the public domain worldwide. This software is distributed without warranty.
//--------------------------------------------------------------------------
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR   
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,     
// FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE 
// AUTHORS OR CONTRIBUTORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, 
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR 
// IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//--------------------------------------------------------------------------
package LSJava.LSPropertyEditors;
import  java.awt.*;
import  LSJava.LSComponents.*;
import  LSJava.LSUtility.*;
import  LSJava.LSPropertyEditors.LSPanelMatrix3x3TextFields;


//--------------------------------------------------------------------------
public class LSPanelRotationMatrixTextFields extends LSPanelMatrix3x3TextFields
{
   // Constructor ---------------------------------------------------------
   public LSPanelRotationMatrixTextFields( LSContainer container, int gridWidth, int gridHeight )  
   {
      super( container, gridWidth, gridHeight );
   }


   // ---------------------------------------------------------------------
   public void  SetRotationMatrixTextFieldsFromBodyXYZAngles( double qX, double qY, double qZ )
   {
      // Numerical values for rotation matrix elements.
      double cosqX = Math.cos(qX);
      double cosqY = Math.cos(qY);
      double cosqZ = Math.cos(qZ);
      double sinqX = Math.sin(qX);
      double sinqY = Math.sin(qY);
      double sinqZ = Math.sin(qZ);
      double Rxx = cosqY * cosqZ;
      double Rxy = sinqZ * cosqX + sinqX * sinqY * cosqZ;
      double Rxz = sinqX * sinqZ - sinqY * cosqX * cosqZ;
      double Ryx = -sinqZ * cosqY;
      double Ryy = cosqX * cosqZ - sinqX * sinqY * sinqZ;
      double Ryz = sinqX * cosqZ + sinqY * sinqZ * cosqX;
      double Rzx = sinqY;
      double Rzy = -sinqX * cosqY;
      double Rzz = cosqX * cosqY;

      // Get rid of round-off errors and set the text fields.
      double epsilonToRoundDoubleToNearestInteger = 1.0E-15;
      super.SetTextFieldsFromDoublesWithRoundOffToNearestInteger( Rxx, Rxy, Rxz, Ryx, Ryy, Ryz, Rzx, Rzy, Rzz, epsilonToRoundDoubleToNearestInteger );
   }

}
