//--------------------------------------------------------------------------
// File:     LSTextFieldWithListenersAbstract.java
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
   protected LSTextFieldWithListenersAbstract( Color colorForInvalidTextfieldOrNull, int    initialValue,  int textWidth, boolean isEditable, LSContainer container, int gridWidth, int gridHeight, ActionListener actionListenerOrNull, FocusListener focusListenerOrNull, KeyListener keyListenerOrNull )   { this( colorForInvalidTextfieldOrNull, LSString.GetStringFromInteger(initialValue), textWidth==0 ? LSInteger.GetNumberOfDigits(initialValue) + 2 : textWidth, isEditable, container, gridWidth, gridHeight, actionListenerOrNull, focusListenerOrNull, keyListenerOrNull ); }
   protected LSTextFieldWithListenersAbstract( Color colorForInvalidTextfieldOrNull, double initialValue,  int textWidth, boolean isEditable, LSContainer container, int gridWidth, int gridHeight, ActionListener actionListenerOrNull, FocusListener focusListenerOrNull, KeyListener keyListenerOrNull )   { this( colorForInvalidTextfieldOrNull, LSString.GetStringFromDouble(initialValue),  textWidth==0 ? LSDouble.GetMaxNumberOfCharsInTextFieldsForDoublePrecisionNumber() : textWidth, isEditable, container, gridWidth, gridHeight, actionListenerOrNull, focusListenerOrNull, keyListenerOrNull ); }
   protected LSTextFieldWithListenersAbstract( Color colorForInvalidTextfieldOrNull, String initialString, int textWidth, boolean isEditable, LSContainer container, int gridWidth, int gridHeight, ActionListener actionListenerOrNull, FocusListener focusListenerOrNull, KeyListener keyListenerOrNull )
   {
      super( initialString, textWidth, isEditable, container, gridWidth, gridHeight, actionListenerOrNull, focusListenerOrNull, keyListenerOrNull );

      // If pass in non-null argument, change textfield background color on event if subclass returns error message, otherwise change it to LSColor.White.
      myColorForInvalidTextfieldOrNull = colorForInvalidTextfieldOrNull;

      // An ActionEvent occurs whenever the user hits the ENTER key.
      super.addActionListener( this );

      // FocusEvent occurs whenever the user hits the TAB key or leaves the field to edit a different field.
      super.addFocusListener( this );

      // Respond to user typing certain keys, e.g., pressing OK or ESCAPE.
      super.addKeyListener( this );
   }


   //-------------------------------------------------------------------------
   public void  actionPerformed( ActionEvent actionEvent )  { this.CheckActionOrFocusLostOrKeyEventTarget( actionEvent.getSource() ); }
   public void  focusLost(   FocusEvent focusEvent )        { this.CheckActionOrFocusLostOrKeyEventTarget(  focusEvent.getSource() ); }
   public void  focusGained( FocusEvent focusEvent )        {;} 
  
   //-------------------------------------------------------------------------
   public void  keyPressed(  KeyEvent keyEvent )  {;}
   public void  keyReleased( KeyEvent keyEvent )  {;}
   public void  keyTyped(    KeyEvent keyEvent )
   {
      Object eventTarget = keyEvent.getSource();
      if( eventTarget == this )
      {
         switch( keyEvent.getKeyChar() )
         {
            case KeyEvent.VK_TAB:   
            case KeyEvent.VK_ENTER:   
            case KeyEvent.VK_ESCAPE:  this.CheckActionOrFocusLostOrKeyEventTarget( eventTarget ); 
                                      break;
         }
      }
   }


   //-------------------------------------------------------------------------
   protected void  CheckActionOrFocusLostOrKeyEventTarget( Object eventTarget )
   {
      if( eventTarget == this ) 
      {
         String errorMessageFromSubclass = this.EventActionOrFocusLostOrKeyEventReturnErrorStringVirtual();

	 // Only display error dialog (and request focus) is this component is showing.
         // Display dialog with error from subclass - unless just trying to use background color to warn user of possible error.
         if( this.isShowing()  &&  errorMessageFromSubclass != null  &&  myColorForInvalidTextfieldOrNull != LSColor.BackgroundColorSuggestingWarning )
         {
            long currentSystemTimeInMilliseconds = LSSystem.SystemGetCurrentTimeInMilliSeconds();
            long differenceInTimeSinceLastMessage = currentSystemTimeInMilliseconds - myLastMillisecTimeThisTextfieldGeneratedErrorString;
            myLastMillisecTimeThisTextfieldGeneratedErrorString = currentSystemTimeInMilliseconds;
            if( differenceInTimeSinceLastMessage > 800 ) 
            {
	       // No reason to show error message if the parent window or this textfield are not showing.
               Window parentWindowForThisTextField = LSContainer.GetParentWindowOrNull( super.GetParentContainer() );
               LSMessageDialog messageDialog = LSMessageDialog.NewUserMessageDialog( parentWindowForThisTextField, errorMessageFromSubclass );

               // Keep track of when user dismisses message dialog to avoid focus-lost event thread.
               myLastMillisecTimeThisTextfieldGeneratedErrorString = LSSystem.SystemGetCurrentTimeInMilliSeconds();
            }

            // Regardless of whether error dialog is displayed, reset focus on error (regardless of validity).
            super.RequestFocus();
         }

         // Regardless of whether error dialog is displayed, possibly change text field background color.
         if( myColorForInvalidTextfieldOrNull != null )
            super.SetTextFieldBackgroundColorOKOrColor( errorMessageFromSubclass==null, myColorForInvalidTextfieldOrNull );             
      }
   }


   //-------------------------------------------------------------------------
   abstract protected String  EventActionOrFocusLostOrKeyEventReturnErrorStringVirtual( );


   // Class variables --------------------------------------------------------
   private long   myLastMillisecTimeThisTextfieldGeneratedErrorString;
   private Color  myColorForInvalidTextfieldOrNull; 


}
