//--------------------------------------------------------------------------
// File:     LSTextArea.java
// Parent:   TextArea
// Purpose:  TextAreas for user interface
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
public class LSTextArea extends TextArea
{
   // Constructor ---------------------------------------------------------
   public LSTextArea( String initialString, int textHeightNumberOfLines, int textWidthNumberOfCharacters, int scrollBarType, boolean isEditable, LSContainer container, int gridWidth, int gridHeight )
   {
      // scrollBarType is SCROLLBARS_BOTH, SCROLLBARS_VERTICAL_ONLY, SCROLLBARS_HORIZONTAL_ONLY, or SCROLLBARS_NONE
      super( initialString, textHeightNumberOfLines, textWidthNumberOfCharacters, scrollBarType );
      this.SetTextAreaEditable( isEditable );
      container.AddComponentToLayout( this, gridWidth, gridHeight );
   }

   // Public methods ------------------------------------------------------
   public int     GetTextAreaNumberOfCharacters( )           { return LSString.GetStringLength( this.GetTextAreaString() ); }
   public void    SetTextAreaString( String text )           { super.setText( text ); }
   public String  GetTextAreaString( )                       { return super.getText(); }
   public void    AppendStringToTextArea( String text )      { super.append(text); }
   public void    SetTextAreaEditable( boolean isEditable )  { super.setEditable(isEditable); }
   public void    SetTextAreaFont( LSFont f )                { super.setFont( f ); }

   //-----------------------------------------------------------------------
   public final static int  GetScrollBarTypeForTextArea( boolean isTextTooHigh, boolean isTextTooWide )
   {
      int scrollBarType = SCROLLBARS_NONE;
      if( isTextTooHigh && isTextTooWide ) scrollBarType = SCROLLBARS_BOTH;
      else if( isTextTooHigh )             scrollBarType = SCROLLBARS_VERTICAL_ONLY;
      else if( isTextTooWide )             scrollBarType = SCROLLBARS_HORIZONTAL_ONLY;
      return scrollBarType;
   }
}
