//--------------------------------------------------------------------------
// File:     LSFrame.java
// Class:    LSFrame
// Parents:  Frame -> Window -> Container -> Component -> Object
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


//--------------------------------------------------------------------------
abstract public class LSFrame extends Frame implements ActionListener
{
   // Constructor ---------------------------------------------------------
   protected LSFrame( String title, boolean exitOnWindowClose )
   {
      super( title );

      // Layout manager, constraints, and set default values (color and font)
      myContainer = new LSContainer( this );

      // Warning and dialog boxes must be associated with some frame
      if( exitOnWindowClose && LSMessageDialog.GetCurrentWindowToIssueMessages() == null )
         LSMessageDialog.SetCurrentWindowToIssueMessages( this );

      // Put in a window listener to respond to closing events, minimizing, etc.
      myWindowAdapter = new LSWindowAdapter( this, exitOnWindowClose );
      addWindowListener( myWindowAdapter );
   }


   //-------------------------------------------------------------------------
   public LSContainer  GetFrameAsContainer( )  { return myContainer; }


   // Class variables --------------------------------------------------------
   protected LSContainer      myContainer;
   private   LSWindowAdapter  myWindowAdapter;
}


