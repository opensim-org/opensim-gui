//--------------------------------------------------------------------------
// File:     LSDialog.java
// Parents:  Dialog -> Window -> Container -> Component -> Object
//           Has a WindowAdapter that acts as a WindowListener.
// Purpose:  Holds generic data and methods for a dialog box
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
//import  javax.swing.JDialog;


//--------------------------------------------------------------------------
public abstract class LSDialog extends Dialog
{
   // Constructors ---------------------------------------------------------
   protected LSDialog( Frame ownerFrame,   String dialogTitle, boolean isModal, boolean isResizeable )  { super(ownerFrame, dialogTitle,isModal);  this.LSDialogConstructorHelper(isResizeable); }
   protected LSDialog( Dialog ownerDialog, String dialogTitle, boolean isModal, boolean isResizeable )  { super(ownerDialog,dialogTitle,isModal);  this.LSDialogConstructorHelper(isResizeable); }

   // Constructor helper  --------------------------------------------------
   private void  LSDialogConstructorHelper( boolean isResizeable )
   {
      // Layout manager, constraints, and set default values (color and font)
      myContainer = new LSContainer( this );
      
      // Put in a window listener to respond to closing events, minimizing, etc.
      myWindowAdapter = new LSWindowAdapter( this, LSWindowAdapter.DISPOSE_ON_CLOSE );

      this.SetDialogResizable( isResizeable );
   }

   //-------------------------------------------------------------------------
   public  LSContainer  GetDialogAsContainer( )               { return myContainer; }
   public  void         Dispose( )                            { super.dispose(); }
   public  void         RequestFocus( )                       { super.requestFocus(); }
   public  void         SetDialogVisible( boolean tf )        { super.setVisible( tf ); }
   public  void         SetDialogTitle( String dialogTitle )  { super.setTitle( dialogTitle ); }
   private void         SetDialogResizable( boolean onOff )   { super.setResizable( onOff ); }

   public boolean  IsDialogOwnerWindowFrame( )        { return this.GetLSDialogOwnerWindowOrNull() instanceof Frame;  }
   public boolean  IsDialogOwnerWindowDialog( )       { return this.GetLSDialogOwnerWindowOrNull() instanceof Dialog; }
   public Window   GetLSDialogOwnerWindowOrNull( )    { return super.getOwner(); }
   public Window   GetLSDialogOwnerWindowOrThis( )    { Window retValue = this.GetLSDialogOwnerWindowOrNull();  return retValue != null ? retValue : this; }
   public Frame    GetLSDialogOwnerWindowAsFrame( )   { return this.IsDialogOwnerWindowFrame()  ?  (Frame)this.GetLSDialogOwnerWindowOrNull() : null; }
   public Dialog   GetLSDialogOwnerWindowAsDialog( )  { return this.IsDialogOwnerWindowDialog() ? (Dialog)this.GetLSDialogOwnerWindowOrNull() : null; }


   // Class variables --------------------------------------------------------
   protected LSContainer     myContainer;
   private   LSWindowAdapter myWindowAdapter;
}
