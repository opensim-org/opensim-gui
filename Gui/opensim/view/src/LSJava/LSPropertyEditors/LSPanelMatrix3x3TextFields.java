//--------------------------------------------------------------------------
// File:     LSPanelMatrix3x3TextFields.java
// Parents:  LSPanel -> Panel -> Container -> Component -> Object
// Purpose:  Holds 3x3 matrix with optional title and picture.
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


//--------------------------------------------------------------------------
public class LSPanelMatrix3x3TextFields extends LSPanel
{
   // Constructor ---------------------------------------------------------
   public LSPanelMatrix3x3TextFields( LSContainer container, int gridWidth, int gridHeight )  { this(container,gridWidth,gridHeight,null,null); }
   public LSPanelMatrix3x3TextFields( LSContainer container, int gridWidth, int gridHeight, String optionalTitle, String optionalLSResourcesGraphicsFile )  
   {
      super( container, gridWidth, gridHeight );
      LSContainer tabContainer = this.GetPanelAsContainer();

      // Extra padding (spacing) between components in inertia matrix.
      tabContainer.SetConstraintInsets( 4, 4, 4, 4 );

      // Create matrix properties labels and text fields.
      myRxxTextField = new LSTextField( 0.0, 0, false, tabContainer, 1,                            1, null, null, null );
      myRxyTextField = new LSTextField( 0.0, 0, false, tabContainer, 1,                            1, null, null, null );
      myRxzTextField = new LSTextField( 0.0, 0, false, tabContainer, GridBagConstraints.REMAINDER, 1, null, null, null );
      myRyxTextField = new LSTextField( 0.0, 0, false, tabContainer, 1,                            1, null, null, null );
      myRyyTextField = new LSTextField( 0.0, 0, false, tabContainer, 1,                            1, null, null, null );
      myRyzTextField = new LSTextField( 0.0, 0, false, tabContainer, GridBagConstraints.REMAINDER, 1, null, null, null );
      myRzxTextField = new LSTextField( 0.0, 0, false, tabContainer, 1,                            1, null, null, null );
      myRzyTextField = new LSTextField( 0.0, 0, false, tabContainer, 1,                            1, null, null, null );
      myRzzTextField = new LSTextField( 0.0, 0, false, tabContainer, GridBagConstraints.REMAINDER, 1, null, null, null );
   }


   // ---------------------------------------------------------------------
   public void  SetTextFieldsFromDoublesWithRoundOffToNearestInteger( double Rxx, double Rxy, double Rxz, double Ryx, double Ryy, double Ryz, double Rzx, double Rzy, double Rzz, double epsilonToRoundDoubleToNearestIntegerOr0 )
   {
      if( epsilonToRoundDoubleToNearestIntegerOr0 > 0 )
      {
         Rxx = LSDouble.RoundToNearestIntegerIfWithinEpsilon( Rxx, epsilonToRoundDoubleToNearestIntegerOr0 );
         Rxy = LSDouble.RoundToNearestIntegerIfWithinEpsilon( Rxy, epsilonToRoundDoubleToNearestIntegerOr0 );
         Rxz = LSDouble.RoundToNearestIntegerIfWithinEpsilon( Rxz, epsilonToRoundDoubleToNearestIntegerOr0 );
         Ryx = LSDouble.RoundToNearestIntegerIfWithinEpsilon( Ryx, epsilonToRoundDoubleToNearestIntegerOr0 );
         Ryy = LSDouble.RoundToNearestIntegerIfWithinEpsilon( Ryy, epsilonToRoundDoubleToNearestIntegerOr0 );
         Ryz = LSDouble.RoundToNearestIntegerIfWithinEpsilon( Ryz, epsilonToRoundDoubleToNearestIntegerOr0 );
         Rzx = LSDouble.RoundToNearestIntegerIfWithinEpsilon( Rzx, epsilonToRoundDoubleToNearestIntegerOr0 );
         Rzy = LSDouble.RoundToNearestIntegerIfWithinEpsilon( Rzy, epsilonToRoundDoubleToNearestIntegerOr0 );
         Rzz = LSDouble.RoundToNearestIntegerIfWithinEpsilon( Rzz, epsilonToRoundDoubleToNearestIntegerOr0 );
      }
      this.SetTextFieldsFromDoubles( Rxx, Rxy, Rxz, Ryx, Ryy, Ryz, Rzx, Rzy, Rzz );
   }


   // ---------------------------------------------------------------------
   public void  SetTextFieldsFromDoubles( double Rxx, double Rxy, double Rxz, double Ryx, double Ryy, double Ryz, double Rzx, double Rzy, double Rzz )
   {
      myRxxTextField.SetTextFieldFromDouble( Rxx );
      myRxyTextField.SetTextFieldFromDouble( Rxy );
      myRxzTextField.SetTextFieldFromDouble( Rxz );
      myRyxTextField.SetTextFieldFromDouble( Ryx );
      myRyyTextField.SetTextFieldFromDouble( Ryy );
      myRyzTextField.SetTextFieldFromDouble( Ryz );
      myRzxTextField.SetTextFieldFromDouble( Rzx );
      myRzyTextField.SetTextFieldFromDouble( Rzy );
      myRzzTextField.SetTextFieldFromDouble( Rzz );
   }

   // ---------------------------------------------------------------------
   public void  SetXXTextField( double d, double epsilonToRoundDoubleToNearestIntegerOr0 )  { myRxxTextField.SetTextFieldWithDoubleRoundedToNearestInteger( d, epsilonToRoundDoubleToNearestIntegerOr0 ); }
   public void  SetXYTextField( double d, double epsilonToRoundDoubleToNearestIntegerOr0 )  { myRxyTextField.SetTextFieldWithDoubleRoundedToNearestInteger( d, epsilonToRoundDoubleToNearestIntegerOr0 ); }
   public void  SetXZTextField( double d, double epsilonToRoundDoubleToNearestIntegerOr0 )  { myRxzTextField.SetTextFieldWithDoubleRoundedToNearestInteger( d, epsilonToRoundDoubleToNearestIntegerOr0 ); }
   public void  SetYXTextField( double d, double epsilonToRoundDoubleToNearestIntegerOr0 )  { myRyxTextField.SetTextFieldWithDoubleRoundedToNearestInteger( d, epsilonToRoundDoubleToNearestIntegerOr0 ); }
   public void  SetYYTextField( double d, double epsilonToRoundDoubleToNearestIntegerOr0 )  { myRyyTextField.SetTextFieldWithDoubleRoundedToNearestInteger( d, epsilonToRoundDoubleToNearestIntegerOr0 ); }
   public void  SetYZTextField( double d, double epsilonToRoundDoubleToNearestIntegerOr0 )  { myRyzTextField.SetTextFieldWithDoubleRoundedToNearestInteger( d, epsilonToRoundDoubleToNearestIntegerOr0 ); }
   public void  SetZXTextField( double d, double epsilonToRoundDoubleToNearestIntegerOr0 )  { myRzxTextField.SetTextFieldWithDoubleRoundedToNearestInteger( d, epsilonToRoundDoubleToNearestIntegerOr0 ); }
   public void  SetZYTextField( double d, double epsilonToRoundDoubleToNearestIntegerOr0 )  { myRzyTextField.SetTextFieldWithDoubleRoundedToNearestInteger( d, epsilonToRoundDoubleToNearestIntegerOr0 ); }
   public void  SetZZTextField( double d, double epsilonToRoundDoubleToNearestIntegerOr0 )  { myRzzTextField.SetTextFieldWithDoubleRoundedToNearestInteger( d, epsilonToRoundDoubleToNearestIntegerOr0 ); }

   // ---------------------------------------------------------------------
   public void  SetXXTextField( double d )  { myRxxTextField.SetTextFieldFromDouble( d ); }
   public void  SetXYTextField( double d )  { myRxyTextField.SetTextFieldFromDouble( d ); }
   public void  SetXZTextField( double d )  { myRxzTextField.SetTextFieldFromDouble( d ); }
   public void  SetYXTextField( double d )  { myRyxTextField.SetTextFieldFromDouble( d ); }
   public void  SetYYTextField( double d )  { myRyyTextField.SetTextFieldFromDouble( d ); }
   public void  SetYZTextField( double d )  { myRyzTextField.SetTextFieldFromDouble( d ); }
   public void  SetZXTextField( double d )  { myRzxTextField.SetTextFieldFromDouble( d ); }
   public void  SetZYTextField( double d )  { myRzyTextField.SetTextFieldFromDouble( d ); }
   public void  SetZZTextField( double d )  { myRzzTextField.SetTextFieldFromDouble( d ); }

   // ---------------------------------------------------------------------
   // Class data.
   private LSTextField  myRxxTextField, myRxyTextField, myRxzTextField;
   private LSTextField  myRyxTextField, myRyyTextField, myRyzTextField;
   private LSTextField  myRzxTextField, myRzyTextField, myRzzTextField;

}
