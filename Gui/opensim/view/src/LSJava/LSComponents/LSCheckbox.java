//--------------------------------------------------------------------------
// File:     LSCheckbox.java
// Parent:   Checkbox
// Purpose:  Checkbox for user interface
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
import  java.awt.event.*;
import  java.awt.*;


//--------------------------------------------------------------------------
public class LSCheckbox extends Checkbox
{
   // Constructor ---------------------------------------------------------
   public LSCheckbox( String checkboxLabel, boolean isBoxTrue, CheckboxGroup checkboxGroup, LSContainer container, int gridWidth, int gridHeight, ItemListener itemListener )
   {
      super( checkboxLabel, isBoxTrue, checkboxGroup );  // checkboxGroup is only used if only ONE checkBox can be true at any given time.

      container.AddComponentToLayout( this, gridWidth, gridHeight );

      // Respond to clicking the button.
      if( itemListener != null )  
         super.addItemListener( itemListener );
   }

   // ---------------------------------------------------------------------
   public boolean  GetCheckboxState( )                        { return super.getState(); }
   public void     SetCheckboxState( boolean checked )        { super.setState( checked ); }
   public void     SetCheckboxBackgroundColor( Color color )  { super.setBackground( color ); }

}
