//--------------------------------------------------------------------------
// File:     LSFrame.java
// Parents:  Frame -> Window -> Container -> Component -> Object
//           Has a WindowAdapter that acts as a WindowListener.
// Purpose:  Holds generic data and methods for a frame (part of a frame or window)
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
//import  javax.swing.JFrame;


//--------------------------------------------------------------------------
abstract public class LSFrame extends Frame implements ActionListener
{
   // Constructor ---------------------------------------------------------
   protected LSFrame( String title, boolean exitOnWindowClose )
   {
      super( title );

      // Layout manager, constraints, and set default values (color and font)
      myContainer = new LSContainer( this );

      // Warning and dialog boxes should be associated with a window.
      if( exitOnWindowClose && LSMessageDialog.GetCurrentWindowToIssueMessages() == null )
         LSMessageDialog.SetCurrentWindowToIssueMessages( this );

      // Put in a window listener to respond to closing events, minimizing, etc.
      myWindowAdapter = new LSWindowAdapter( this, exitOnWindowClose ? LSWindowAdapter.EXIT_ON_CLOSE : LSWindowAdapter.DISPOSE_ON_CLOSE );

      // The normal default for JFrame is to just hide the window when it is closed.
      // This behavior is overridden in here to to dispose of resources when window is closed.
      // May override in subclasses with: DO_NOTHING_ON_CLOSE, HIDE_ON_CLOSE, DISPOSE_ON_CLOSE, EXIT_ON_CLOSE.
      // super.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE ); 
   }


   //-------------------------------------------------------------------------
   public LSContainer  GetFrameAsContainer( )  { return myContainer; }


   // Class variables --------------------------------------------------------
   protected LSContainer      myContainer;
   private   LSWindowAdapter  myWindowAdapter;
}


