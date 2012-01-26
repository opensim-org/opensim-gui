//--------------------------------------------------------------------------
// File:     LSTextFieldWithListenersAbstract.java
// Class:    LSTextFieldWithListenersAbstract
// Parent:   LSTextField
// Purpose:  Abstract class with LSTextFields with listeners for event handling.
// Authors:  John Mitiguy and Paul Mitiguy (2001-2012).
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
import  java.awt.event.*;
import  java.awt.*;


//--------------------------------------------------------------------------
public abstract class LSTextFieldWithListenersAbstract extends LSTextField implements ActionListener, FocusListener, KeyListener
{
   // Constructor ---------------------------------------------------------
   public LSTextFieldWithListenersAbstract( int    initialValue,  int textWidth, boolean isEditable, LSContainer container, int gridWidth, int gridHeight, ActionListener actionListenerOrNull, FocusListener focusListenerOrNull, KeyListener keyListenerOrNull )   { this( LSString.GetStringFromInteger(initialValue), textWidth==0 ? LSInteger.GetNumberOfDigits(initialValue) + 2 : textWidth, isEditable, container, gridWidth, gridHeight, actionListenerOrNull, focusListenerOrNull, keyListenerOrNull ); }
   public LSTextFieldWithListenersAbstract( double initialValue,  int textWidth, boolean isEditable, LSContainer container, int gridWidth, int gridHeight, ActionListener actionListenerOrNull, FocusListener focusListenerOrNull, KeyListener keyListenerOrNull )   { this( LSString.GetStringFromDouble(initialValue),  textWidth==0 ? LSDouble.GetMaxNumberOfCharsInTextFieldsForDoublePrecisionNumber() : textWidth, isEditable, container, gridWidth, gridHeight, actionListenerOrNull, focusListenerOrNull, keyListenerOrNull ); }
   public LSTextFieldWithListenersAbstract( String initialString, int textWidth, boolean isEditable, LSContainer container, int gridWidth, int gridHeight, ActionListener actionListenerOrNull, FocusListener focusListenerOrNull, KeyListener keyListenerOrNull )
   {
      super( initialString, textWidth, isEditable, container, gridWidth, gridHeight, actionListenerOrNull, focusListenerOrNull, keyListenerOrNull );

      // An ActionEvent occurs whenever the user hits the ENTER key.
      super.addActionListener( this );

      // FocusEvent occurs whenever the user hits the TAB key or leaves the field to edit a different field.
      super.addFocusListener( this );

      // Respond to user typing certain keys, e.g., pressing OK or ESCAPE.
      super.addKeyListener( this );
   }


   //-------------------------------------------------------------------------
   public void  actionPerformed( ActionEvent actionEvent )  { this.CheckActionOrFocusOrKeyEventTarget( actionEvent.getSource() ); }
   public void  focusLost(   FocusEvent focusEvent )        { this.CheckActionOrFocusOrKeyEventTarget(  focusEvent.getSource() ); }
   public void  focusGained( FocusEvent focusEvent )        {;} 
  
   //-------------------------------------------------------------------------
   public void  keyPressed(  KeyEvent keyEvent )  {;}
   public void  keyReleased( KeyEvent keyEvent )  {;}
   public void  keyTyped(    KeyEvent keyEvent )
   {
      switch( keyEvent.getKeyChar() )
      {
//       case KeyEvent.VK_ENTER:   break;
         case KeyEvent.VK_ESCAPE:  this.CheckActionOrFocusOrKeyEventTarget( keyEvent.getSource() ); 
	                           break;
      }
   }


   //-------------------------------------------------------------------------
   protected void  CheckActionOrFocusOrKeyEventTarget( Object eventTarget )
   {
      if( eventTarget == this ) this.EventActionOrFocusOrKeyEventOnThisObjectVirtual();
   }


   //-------------------------------------------------------------------------
   abstract protected void  EventActionOrFocusOrKeyEventOnThisObjectVirtual( );

}
