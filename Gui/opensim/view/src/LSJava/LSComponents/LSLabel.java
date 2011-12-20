//--------------------------------------------------------------------------
// File:     LSLabel.java
// Class:    LSLabel
// Parent:   Label
// Children: None
// Purpose:  Labels for windows, panels, dialog boxes, frames, etc.
// Authors:  John Mitiguy and Paul Mitiguy, 2001-2010.
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
import  java.awt.Label;
import  java.awt.Container;
import  java.awt.GridBagConstraints;
import  java.awt.Color;
//import  javax.swing.JLabel;


//--------------------------------------------------------------------------
public class LSLabel extends Label
{
   // Constructor ---------------------------------------------------------
   public LSLabel( String labelText, int labelLocation, LSContainer container, int gridWidth )	{ this( labelText, labelLocation, container, gridWidth, 1 ); } 
   public LSLabel( String labelText, int labelLocation, LSContainer container, int gridWidth, int gridHeight )
   {
      super( labelText, labelLocation );

      // Keep a copy of the constraints for adding and removing this component to frames, dialogs, panels, ...
      myConstraintsWhenCreated = container.GetConstraintsClone();
      myConstraintsWhenCreated.gridwidth  = gridWidth;
      myConstraintsWhenCreated.gridheight = gridHeight;
      container.AddComponentToLayout( this, gridWidth, gridHeight );
   }

   // ---------------------------------------------------------------------
   public String   GetLabelString( )                       { return super.getText(); }
   public void     SetLabelString( String s )              { super.setText(s); }
   public void     SetLabelFont( LSFont f )                { if( f != null ) super.setFont(f); }
   public void     SetLabelBackgroundColor( Color color )  { super.setBackground(color); }
   public void     SetLabelForegroundColor( Color color )  { super.setForeground(color); }
   public void     SetLabelVisible( boolean tf )           { super.setVisible(tf); }
   public boolean  IsLabelVisible( )                       { return super.isVisible(); }


   // ------------------------------------------------------------------------
   public GridBagConstraints  GetConstraintsWhenCreated( )  { return myConstraintsWhenCreated; }


   // Class variables --------------------------------------------------------
   private GridBagConstraints  myConstraintsWhenCreated;
}
