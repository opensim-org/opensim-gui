//--------------------------------------------------------------------------
// File:     LSMessageDialog.java
// Parent:   LSDialog
// Purpose:  Dialog box for displaying messages to user
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
public class LSMessageDialog extends LSDialogWithListenersAbstract 
{
   // Quasi Constructor ---------------------------------------------------
   public static LSMessageDialog  NewUserMessageDialog( String s )                                                                              { return LSMessageDialog.NewUserMessageDialog( null, new LSStringList(s), LSLabel.CENTER  ); }
   public static LSMessageDialog  NewUserMessageDialog( String s, int labelLocation )                                                           { return LSMessageDialog.NewUserMessageDialog( null, new LSStringList(s), labelLocation ); }
   public static LSMessageDialog  NewUserMessageDialog( String sA, String sB )                                                                  { return LSMessageDialog.NewUserMessageDialog( null, new LSStringList(sA,sB),       LSLabel.LEFT ); }
   public static LSMessageDialog  NewUserMessageDialog( String sA, String sB, String sC )                                                       { return LSMessageDialog.NewUserMessageDialog( null, new LSStringList(sA,sB,sC),    LSLabel.LEFT ); }
   public static LSMessageDialog  NewUserMessageDialog( String sA, String sB, String sC, String sD )                                            { return LSMessageDialog.NewUserMessageDialog( null, new LSStringList(sA,sB,sC,sD), LSLabel.LEFT ); }
   public static LSMessageDialog  NewUserMessageDialog( Window ownerWindowOrNullForCurrentWindow, String s )                                    { return LSMessageDialog.NewUserMessageDialog( ownerWindowOrNullForCurrentWindow, new LSStringList(s), LSLabel.CENTER  ); }
   public static LSMessageDialog  NewUserMessageDialog( Window ownerWindowOrNullForCurrentWindow, String s, int labelLocation )                 { return LSMessageDialog.NewUserMessageDialog( ownerWindowOrNullForCurrentWindow, new LSStringList(s), labelLocation ); }
   public static LSMessageDialog  NewUserMessageDialog( Window ownerWindowOrNullForCurrentWindow, String sA, String sB )                        { return LSMessageDialog.NewUserMessageDialog( ownerWindowOrNullForCurrentWindow, new LSStringList(sA,sB),       LSLabel.LEFT ); }
   public static LSMessageDialog  NewUserMessageDialog( Window ownerWindowOrNullForCurrentWindow, String sA, String sB, String sC )             { return LSMessageDialog.NewUserMessageDialog( ownerWindowOrNullForCurrentWindow, new LSStringList(sA,sB,sC),    LSLabel.LEFT ); }
   public static LSMessageDialog  NewUserMessageDialog( Window ownerWindowOrNullForCurrentWindow, String sA, String sB, String sC, String sD )  { return LSMessageDialog.NewUserMessageDialog( ownerWindowOrNullForCurrentWindow, new LSStringList(sA,sB,sC,sD), LSLabel.LEFT ); }
   public static LSMessageDialog  NewUserMessageDialog( Window ownerWindowOrNullForCurrentWindow, LSStringList sList, int labelLocation ) 
   {
      if( sList == null ) return null;  
      if( ownerWindowOrNullForCurrentWindow == null ) ownerWindowOrNullForCurrentWindow = LSMessageDialog.GetCurrentWindowToIssueMessages(); 
      boolean ownerWindowIsFrame  = (ownerWindowOrNullForCurrentWindow != null) && ownerWindowOrNullForCurrentWindow instanceof Frame;
      boolean ownerWindowIsDialog = (ownerWindowOrNullForCurrentWindow != null) && ownerWindowOrNullForCurrentWindow instanceof Dialog;
      if( !ownerWindowIsFrame && !ownerWindowIsDialog ) { LSMessageDialog.SystemStandardOutputMessage(sList);  return null; }  
      return ownerWindowIsFrame ? new LSMessageDialog(  (Frame)ownerWindowOrNullForCurrentWindow, sList, labelLocation ) :
                                  new LSMessageDialog( (Dialog)ownerWindowOrNullForCurrentWindow, sList, labelLocation ) ;
   }

   //-------------------------------------------------------------------------
   public static void  SystemStandardOutputMessage( String s )            { if( s != null ) System.out.println(s); }
   public static void  SystemStandardErrorMessage(  String s )            { if( s != null ) System.err.println(s); }
   public static void  SystemStandardOutputMessage( LSStringList sList )  { int numberOfStrings = sList==null ? 0 : sList.GetSizeOfArrayList();  for( int i=0;  i < numberOfStrings;  i++ ) LSMessageDialog.SystemStandardOutputMessage( sList.GetStringAtIndex(i) ); }
   public static void  SystemStandardErrorMessage(  LSStringList sList )  { int numberOfStrings = sList==null ? 0 : sList.GetSizeOfArrayList();  for( int i=0;  i < numberOfStrings;  i++ ) LSMessageDialog.SystemStandardErrorMessage(  sList.GetStringAtIndex(i) ); }
   public static void  DebugMessage( String s )                           { LSMessageDialog.NewUserMessageDialog(s);  LSMessageDialog.SystemStandardErrorMessage(s); }

   //-------------------------------------------------------------------------
   public static Window   GetCurrentWindowToIssueMessages( )                { return myCurrentWindowToIssueMessages; }
   public static void     SetCurrentWindowToIssueMessages( Window window )  { myCurrentWindowToIssueMessages = window; }

   //-------------------------------------------------------------------------
   protected void  CheckActionOrFocusLostOrKeyEventTarget( Object eventTarget )
   {
      boolean shouldDispose = false;
      if(      eventTarget == myOKButton )  shouldDispose = true;
      else if( eventTarget == this )        shouldDispose = true; // Key event.
      if( shouldDispose ) super.Dispose();
   }


   //-------------------------------------------------------------------------
   private void  CreateButtonPanelWithOKButtonAndListener( )
   {
      // Responds to user clicking OK or pressing Enter or Escape
      myContainer.SetConstraintFill( GridBagConstraints.VERTICAL ); // Do not stretch horizontally
      myOKButton = new LSButton( "OK", myContainer, GridBagConstraints.REMAINDER, 1, this, this );
   }


   // Constructor ---------------------------------------------------------
   private LSMessageDialog( Frame  ownerFrame,  LSStringList userMessages, int labelLocation )  { super( ownerFrame,  "User Message", true, false );  this.LSMessageDialogConstructorHelper( userMessages, labelLocation); }
   private LSMessageDialog( Dialog ownerDialog, LSStringList userMessages, int labelLocation )  { super( ownerDialog, "User Message", true, false );  this.LSMessageDialogConstructorHelper( userMessages, labelLocation); }

   // Constructor helper --------------------------------------------------
   private void  LSMessageDialogConstructorHelper( LSStringList sList, int labelLocation )
   {
      myContainer.SetConstraintFill( GridBagConstraints.BOTH ); // BOTH makes each component occupy full width of space
      myContainer.SetConstraintInsets( 5, 9, 5, 9 );            // Padding on top, left, bottom, right

      int numberOfMessages = sList == null ? 0 : sList.GetSizeOfArrayList();
      if( numberOfMessages == 1 )  myContainer.AddLabelToLayoutRowRemainder1High( sList.GetStringAtIndex(0), labelLocation );
      else if( numberOfMessages > 0 )  myContainer.AddStringListToContainerReturnPanel( sList, labelLocation, null );

      this.CreateButtonPanelWithOKButtonAndListener();

      // Display the window at the requested location
      myContainer.PackLocateShow();
   }


   // Class variables --------------------------------------------------------
   private LSButton myOKButton;

   // Frame that is currently associated with dialog boxes when messages are issued.
   static private Window  myCurrentWindowToIssueMessages;

}
