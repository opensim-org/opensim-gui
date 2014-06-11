//--------------------------------------------------------------------------
// File:     LSWindowAdapter.java
// Parent:   WindowAdapter -> Object  (WindowAdapter is an abstract class)
// Purpose:  Handles window opening, closing, iconifying, etc.
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
import  javax.swing.WindowConstants;


//--------------------------------------------------------------------------
public class LSWindowAdapter extends WindowAdapter
{
   // Constructor ----------------------------------------------------------
   public LSWindowAdapter( Window window, int whatToDoWhenWindowCloses )  
   { 
      if( window != null )
      {
         myWindow = window;   
         myWhatToDoWhenWindowCloses = whatToDoWhenWindowCloses; 
         window.addWindowListener( this );
         LSWindowAdapter.AddToArrayOfExistingWindowsIfNotExists( window );
      }
   }

   // Invoked when a window has been opened --------------------------------
   public void  windowOpened( WindowEvent windowEvent ) {}

   // Invoked when a window is in the process of being closed of has been closed 
   public void  windowClosing( WindowEvent windowEvent ) { this.ProcessWindowClosedEvent(windowEvent); }
   public void  windowClosed(  WindowEvent windowEvent ) { this.ProcessWindowClosedEvent(windowEvent); }

   // Invoked when a window is iconified or de-iconified -------------------
   public void  windowIconified(   WindowEvent windowEvent ) {}
   public void  windowDeiconified( WindowEvent windowEvent ) {}

   // Invoked when a window is activiated or de-activated ------------------
   public void  windowActivated(   WindowEvent windowEvent ) {}
   public void  windowDeactivated( WindowEvent windowEvent ) {}
   
   // ----------------------------------------------------------------------
   private void  ProcessWindowClosedEvent( WindowEvent windowEvent )  
   { 
      // Do nothing once the window has been disposed or system exit is called.
      // All resources associated with myWindow should be gone and myWindow should be null. 
      if( myWindow == null )  return;
      
      Object eventTarget = windowEvent.getSource();
      if( eventTarget == myWindow ) 
      { 
         switch( myWhatToDoWhenWindowCloses )
         {
            case LSWindowAdapter.DO_NOTHING_ON_CLOSE: break;
            case LSWindowAdapter.HIDE_ON_CLOSE:       myWindow.setVisible( false ); 
                                                      break;
            case LSWindowAdapter.DISPOSE_ON_CLOSE:    this.DisposeWindowAndSetMyWindowToNull();
                                                      break;
            case LSWindowAdapter.EXIT_ON_CLOSE:       this.DisposeWindowAndSetMyWindowToNull();
                                                      LSSystem.SystemExit(0);    
                                                      break;
         }     
      }  
   }


   // ----------------------------------------------------------------------
   private void  DisposeWindowAndSetMyWindowToNull( )  
   { 
      if( myWindow != null ) 
      {
         LSWindowAdapter.RemoveWindowFromArrayOfExistingWindows( myWindow );
         myWindow.dispose();
         myWindow = null;
      }
   }


   //-----------------------------------------------------------------------------
   public static int     GetNumberOfExistingWindows( )                       { return myArrayListOfExistingWindows.GetSizeOfArrayList(); }
   public static Window  GetExistingWindowOrNull( int i )                    { Object x = myArrayListOfExistingWindows.GetObjectAtIndex(i);  return x==null ? null : (Window)x; }
   public static void    AddToArrayOfExistingWindowsIfNotExists( Window w )  { myArrayListOfExistingWindows.AddObjectToArrayIfNotExistsAndNotNull( w ); }
   public static void    RemoveWindowFromArrayOfExistingWindows( Window w )  { myArrayListOfExistingWindows.RemoveAllOccurencesOfObject( w ); }


   // Class variables ------------------------------------------------------
   private Window  myWindow;
   private int     myWhatToDoWhenWindowCloses;

   private static final LSArrayList  myArrayListOfExistingWindows = new LSArrayList( 30 );

   public final static int  DO_NOTHING_ON_CLOSE = WindowConstants.DO_NOTHING_ON_CLOSE;  // Usually defined as 0 in Java.
   public final static int  HIDE_ON_CLOSE       = WindowConstants.HIDE_ON_CLOSE;        // Usually defined as 1 in Java.
   public final static int  DISPOSE_ON_CLOSE    = WindowConstants.DISPOSE_ON_CLOSE;     // Usually defined as 2 in Java.
   public final static int  EXIT_ON_CLOSE       = WindowConstants.EXIT_ON_CLOSE;        // Usually defined as 3 in Java.
}
