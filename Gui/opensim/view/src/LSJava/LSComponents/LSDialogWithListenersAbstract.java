//--------------------------------------------------------------------------
// File:     LSDialogWithListenersAbstract.java
// Parent:   LSDialog
// Purpose:  Dialog box with listeners for user clicking on buttons or typing ENTER or ESCAPE.
// Authors:  Paul Mitiguy (2011-2012).
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
public abstract class LSDialogWithListenersAbstract extends LSDialog implements ActionListener, KeyListener
{
   //-------------------------------------------------------------------------
   protected LSDialogWithListenersAbstract( Frame  ownerFrame,  String dialogTitle, boolean isModal, boolean isResizeable )  { super( ownerFrame,  dialogTitle, isModal, isResizeable );  this.LSDialogWithListenersAbstractConstructorHelper(); }
   protected LSDialogWithListenersAbstract( Dialog ownerDialog, String dialogTitle, boolean isModal, boolean isResizeable )  { super( ownerDialog, dialogTitle, isModal, isResizeable );  this.LSDialogWithListenersAbstractConstructorHelper(); }

   // Constructor helper  --------------------------------------------------
   private void  LSDialogWithListenersAbstractConstructorHelper( )
   {
      // Respond to user typing certain keys, e.g., pressing OK or ESCAPE.
      // super.addKeyListener( this );
   }


   //-------------------------------------------------------------------------
   public void  actionPerformed( ActionEvent actionEvent )  { this.CheckActionOrFocusLostOrKeyEventTarget( actionEvent.getSource() ); }
  
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
            case KeyEvent.VK_ENTER:   
            case KeyEvent.VK_ESCAPE:  this.CheckActionOrFocusLostOrKeyEventTarget( eventTarget ); 
                                      break;
         }
      }
   }

   //-------------------------------------------------------------------------
   abstract protected void  CheckActionOrFocusLostOrKeyEventTarget( Object eventTarget );

}
