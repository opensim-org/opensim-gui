//--------------------------------------------------------------------------
// File:     LSSlider.java
// Parent:   LSScrollbar
// Purpose:  Slider for user interface
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
import  LSJava.LSUtility.LSString;
import  java.awt.*;
import  java.awt.event.*;
import  javax.swing.*;
import  javax.swing.event.*;
import  javax.swing.JSlider;


//--------------------------------------------------------------------------
public class LSSlider extends JSlider
{
   // Constructor ---------------------------------------------------------
   public LSSlider( int horizontalOrVertical, int minimumValue, int maximumValue, int initialValue, int majorTickSpacing, int minorTickSpacing, LSContainer container, int gridWidth, int gridHeight, ChangeListener changeListener )
   {
      // horizontalOrVertical should be set to  LSSlider.HORIZONTAL  or  LSSlider.VERTICAL
      // initialValue is the initial location of the scroll thumb (should be between minimumValue and maximumValue)
      // widthOfScrollThumbPieceThatUserSlides is how large the scroll thumb is.
      // minimumValue is the smallest value on the scroll thumb, e.g., 0.
      // maximumValue is the largest  value on the scroll thumb, e.g., 100.
      super( horizontalOrVertical, minimumValue, maximumValue, initialValue );

      // Possibly set the major and minor tick spacing (if they have sensible positive values).
      int differenceBetweenMaxAndMinValue = maximumValue - minimumValue;
      boolean setMajorTickmarks = ( majorTickSpacing > 0 && majorTickSpacing < differenceBetweenMaxAndMinValue );
      boolean setMinorTickmarks = ( minorTickSpacing > 0 && minorTickSpacing < differenceBetweenMaxAndMinValue );
      if( setMajorTickmarks ) super.setMajorTickSpacing( majorTickSpacing ); 
      if( setMinorTickmarks ) super.setMinorTickSpacing( minorTickSpacing ); 
      if( setMajorTickmarks || setMinorTickmarks )
      {
         super.setPaintTicks( true );
         super.setPaintLabels( true );
	 super.setPaintTrack( false );
         super.setSnapToTicks( true );
      }

      // Sub-classes may delay the adding of this component until they complete construction.
      if( container != null )
         container.AddComponentToLayout( this, gridWidth, gridHeight );

      if( changeListener != null )
         super.addChangeListener( changeListener );
   }

   // ---------------------------------------------------------------------
   public boolean  IsSliderHorizontal( )                        { return super.getOrientation() == LSSlider.HORIZONTAL; }
   public boolean  IsSliderVertical( )                          { return super.getOrientation() == LSSlider.VERTICAL; }
   public int      GetSliderValueAsInteger( )                   { return super.getValue(); }
   public int      GetSliderMinimumValueAsInteger( )            { return super.getMinimum(); }
   public int      GetSliderMaximumValueAsInteger( )            { return super.getMaximum(); }
   public void     SetSliderValueFromInteger( int x )           { super.setValue(x); }
// public void    SetSliderUnitIncrementFromInteger( int x )    { super.setUnitIncrement(x); }   // When click on arrow on either side of scroll bar
// public void    SetSliderBlockIncrementFromInteger( int x )   { super.setBlockIncrement(x); }  // When click on area between the arrow and the slider

   // ---------------------------------------------------------------------
   // Class data.
   public final static int HORIZONTAL = JSlider.HORIZONTAL;
   public final static int VERTICAL   = JSlider.VERTICAL;
}
