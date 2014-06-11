//--------------------------------------------------------------------------
// File:     LSLabel.java
// Parent:   Label
// Purpose:  Labels for windows, panels, dialog boxes, frames, etc.
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
import  java.awt.Label;
import  java.awt.Container;
import  java.awt.GridBagConstraints;
import  java.awt.Color;
import  javax.swing.JLabel;


//--------------------------------------------------------------------------
public class LSLabel extends JLabel
{
   // Constructor ---------------------------------------------------------
   public LSLabel( String labelText, int labelLocation, LSContainer container )	                { this( labelText, labelLocation, container, 1, 1 ); } 
   public LSLabel( String labelText, int labelLocation, LSContainer container, int gridWidth )	{ this( labelText, labelLocation, container, gridWidth, 1 ); } 
   public LSLabel( String labelText, int labelLocation, LSContainer container, int gridWidth, int gridHeight )
   {
      super( labelText, labelLocation );
      this.SetLabelFont( LSFont.GetUserFont() );
      this.SetLabelForegroundColor( LSColor.GetUserForegroundColor() );  // MGPlot=LSColor.GetUserForegroundColor(), OpenSim=Color.Black

      // Keep a copy of the constraints for adding and removing this component to frames, dialogs, panels, ...
      myConstraintsWhenCreated = container.GetConstraintsClone();
      myConstraintsWhenCreated.gridwidth  = gridWidth;
      myConstraintsWhenCreated.gridheight = gridHeight;
      container.AddComponentToLayout( this, gridWidth, gridHeight );
   }

   // ---------------------------------------------------------------------
   public String   GetLabelString( )                       { return super.getText(); }
   public void     SetLabelString( String s )              { super.setText(s); }
   public void     SetLabelFont( LSFont font )             { if( font != null  ) super.setFont(font); }
   public void     SetLabelBackgroundColor( Color color )  { if( color != null ) super.setBackground(color); }
   public void     SetLabelForegroundColor( Color color )  { if( color != null ) super.setForeground(color); }
   public void     SetLabelVisible( boolean tf )           { super.setVisible(tf); }
   public boolean  IsLabelVisible( )                       { return super.isVisible(); }

   // ---------------------------------------------------------------------
   // Static label location information for use by either Label or JLabel.
   public final static int  LEFT   = JLabel.LEFT;     // Defined as Label.LEFT   or JLabel.LEFT.
   public final static int  CENTER = JLabel.CENTER;   // Defined as Label.CENTER or JLabel.CENTER.
   public final static int  RIGHT  = JLabel.RIGHT;    // Defined as Label.RIGHT  or JLabel.RIGHT.


   // ------------------------------------------------------------------------
   public GridBagConstraints  GetConstraintsWhenCreated( )  { return myConstraintsWhenCreated; }


   // Class variables --------------------------------------------------------
   private GridBagConstraints  myConstraintsWhenCreated;
}
