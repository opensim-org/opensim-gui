//--------------------------------------------------------------------------
// File:     LSTextFieldDialog.java
// Parent:   LSDialog
// Purpose:  Dialog box for getting a single line of text from user
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
import  java.awt.*;
import  java.awt.event.*;


//--------------------------------------------------------------------------
public class LSTextFieldDialog extends LSDialog implements ActionListener, KeyListener
{
   // Constructor ---------------------------------------------------------
   public LSTextFieldDialog( Frame  ownerFrame,  String dialogTitle, String textFieldLabel, String initialString, int textWidth )  { super( ownerFrame,  dialogTitle, true, false );   this.LSTextFieldDialogConstructorHelper( textFieldLabel, initialString, textWidth ); }
   public LSTextFieldDialog( Dialog ownerDialog, String dialogTitle, String textFieldLabel, String initialString, int textWidth )  { super( ownerDialog, dialogTitle, true, false );   this.LSTextFieldDialogConstructorHelper( textFieldLabel, initialString, textWidth ); }

   // Constructor helper  --------------------------------------------------
   private void  LSTextFieldDialogConstructorHelper( String textFieldLabel, String initialString, int textWidth )
   {
      myContainer.SetConstraintFill( GridBagConstraints.BOTH ); // BOTH makes each component occupy full width of space
      myContainer.SetConstraintInsets( 8, 7, 8, 5 );            // Padding on top, left, bottom, right

      // Label then textfield
      myContainer.AddLabelToLayout1Wide1High( textFieldLabel, LSLabel.RIGHT );
      myTextField = new LSTextField( initialString, textWidth, true, myContainer, 1, 1, this, null, this );
      myContainer.AddBlankLabelToLayoutRowRemainder1High();

      // OK and Cancel buttons
      LSPanel panelOfButtons = new LSPanel( myContainer, GridBagConstraints.REMAINDER, 1 );
      this.CreatePanelOfButtons( panelOfButtons.GetPanelAsContainer() );

      // Display the window
      myContainer.PackLocateShow();

      // Make it so cursor is in the function dialog box
      myTextField.RequestFocus();
   }


   //-------------------------------------------------------------------------
   public void actionPerformed( ActionEvent actionEvent )
   {
      Object eventTarget = actionEvent.getSource();
      if(      eventTarget==myCancelButton )  EventCancelButton();
      else if( eventTarget==myOKButton     )  EventOKButton();
   // else if( eventTarget==myTextField    )  EventOKButton();
   }


   //-------------------------------------------------------------------------
   public void  keyPressed(  KeyEvent keyEvent )  { keyTyped(keyEvent); }
   public void  keyReleased( KeyEvent keyEvent )  {;}
   public void  keyTyped(    KeyEvent keyEvent )
   {
      switch( keyEvent.getKeyChar() )
      {
         case KeyEvent.VK_ESCAPE:  this.EventCancelButton();   break;
         case KeyEvent.VK_ENTER:   this.EventOKButton();       break;
      }
   }


   //-------------------------------------------------------------------------
   private void  EventOKButton( )       { myOKButtonPushed = true;   super.Dispose(); }
   private void  EventCancelButton( )   { myOKButtonPushed = false;  super.Dispose(); }


   //-------------------------------------------------------------------------
   public String GetTextFieldDialogAsString( )  { return myOKButtonPushed ? myTextField.GetTextFieldAsString() : null; }


   //-------------------------------------------------------------------------
   private void  CreatePanelOfButtons( LSContainer container )
   {
      container.SetConstraintFill( GridBagConstraints.VERTICAL );
      myOKButton     = new LSButton( "OK",     container, 1, 1, this, null );
      myCancelButton = new LSButton( "Cancel", container, 1, 1, this, null );
      container.SetConstraintFill( GridBagConstraints.BOTH );
   }


   // Class variables --------------------------------------------------------
   private LSTextField  myTextField;
   private LSButton     myOKButton;
   private LSButton     myCancelButton;
   private boolean      myOKButtonPushed;
}
