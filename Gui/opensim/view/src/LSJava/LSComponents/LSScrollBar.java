//--------------------------------------------------------------------------
// File:     LSScrollBar.java
// Parent:   Scrollbar
// Purpose:  Scrollbars for user interface
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
import  java.awt.event.*;
import  java.awt.*;


//--------------------------------------------------------------------------
public class LSScrollBar extends Scrollbar
{
   // Constructor ---------------------------------------------------------
   public LSScrollBar( int horizontalOrVertical, int initialValue, int widthOfScrollThumbPieceThatUserSlides, int minimumValue, int maximumValue, LSContainer container, int gridWidth, int gridHeight, AdjustmentListener adjustmentListener )
   {
      // horizontalOrVertical should be set to  Scrollbar.HORIZONTAL  or  Scrollbar.VERTICAL
      // initialValue is the initial location of the scroll thumb (should be between minimumValue and maximumValue)
      // widthOfScrollThumbPieceThatUserSlides is how large the scroll thumb is.
      // minimumValue is the smallest value on the scroll thumb, e.g., 0.
      // maximumValue is the largest value on the scroll thumb, e.g., 100.
      super( horizontalOrVertical, initialValue, widthOfScrollThumbPieceThatUserSlides, minimumValue, maximumValue );

      container.AddComponentToLayout( this, gridWidth, gridHeight );

      if( adjustmentListener != null )
         super.addAdjustmentListener( adjustmentListener );
   }

   // ---------------------------------------------------------------------
   public String  GetScrollbarValueAsString( )                    { return LSString.GetStringFromInteger( this.GetScrollbarValueAsInteger() ); }
   public int     GetScrollbarValueAsInteger( )                   { return super.getValue(); }
   public void    SetScrollbarValueFromInteger( int x )           { super.setValue(x); }
   public void    SetScrollbarUnitIncrementFromInteger( int x )   { super.setUnitIncrement(x); }   // When click on arrow on either side of scroll bar
   public void    SetScrollbarBlockIncrementFromInteger( int x )  { super.setBlockIncrement(x); }  // When click on area between the arrow and the slider

}
