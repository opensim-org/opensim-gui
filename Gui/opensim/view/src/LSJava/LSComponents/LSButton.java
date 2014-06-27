//--------------------------------------------------------------------------
// File:     LSButton.java
// Parent:   Button
// Purpose:  Buttons for user interface
// Authors:  John Mitiguy and Paul Mitiguy (2001-2010).
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
package LSJava.LSComponents;
import  LSJava.LSUtility.*;
import  java.awt.*;
import  java.awt.event.*;


//--------------------------------------------------------------------------
public class LSButton extends Button
{
   // Constructor ---------------------------------------------------------
   public LSButton( String buttonLabel, LSContainer container, int gridWidth, int gridHeight, ActionListener actionListenerOrNull, KeyListener keyListenerOrNull )
   {
      super( buttonLabel );

      // Keep a copy of the constraints for adding and removing this component to frames, dialogs, panels, ...
      myConstraintsWhenCreated = container.GetConstraintsClone();
      myConstraintsWhenCreated.gridwidth  = gridWidth;
      myConstraintsWhenCreated.gridheight = gridHeight;
      container.AddComponentToLayout( this, myConstraintsWhenCreated );

      // Responds to mouse click on the button
      if( actionListenerOrNull != null )
         super.addActionListener( actionListenerOrNull );

      // Respond to user typing certain keys, e.g., pressing OK or ESCAPE
      if( keyListenerOrNull != null )
         super.addKeyListener( keyListenerOrNull );
   }


   //-------------------------------------------------------------------------
   public void    SetLabelFromString( String s )   { super.setLabel(s); }
   public void    SetLabelFromInteger( int x )     { this.SetLabelFromString( LSString.GetStringFromInteger(x) ); }
   public void    SetLabelFromDouble( double x )   { this.SetLabelFromString( LSString.GetStringFromDouble(x)  ); }


   //-------------------------------------------------------------------------
   public void    SetFont( Font f )  { super.setFont(f); }

   // ------------------------------------------------------------------------
   public GridBagConstraints  GetConstraintsWhenCreated( )  { return myConstraintsWhenCreated; }


   // Class variables --------------------------------------------------------
   private GridBagConstraints  myConstraintsWhenCreated;
}
