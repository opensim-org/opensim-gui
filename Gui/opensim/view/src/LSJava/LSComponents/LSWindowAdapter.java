//--------------------------------------------------------------------------
// File:     LSWindowAdapter.java
// Class:    LSWindowAdapter
// Parent:   WindowAdapter (which is an abstract class)
// Children: Several
// Purpose:  Handles window opening, closing, iconifying, etc.
// Authors:  John Mitiguy and Paul Mitiguy, 2001-2010.
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
public class LSWindowAdapter extends WindowAdapter
{
   // Constructor ----------------------------------------------------------
   public LSWindowAdapter( Window window, boolean exitOnWindowClose )  { myWindow = window;   myExitOnWindowClose = exitOnWindowClose; }

   // Invoked when a window has been opened --------------------------------
   public void windowOpened(WindowEvent e)  {}

   // Invoked when a window is in the process of being closed --------------
   public void windowClosing( WindowEvent windowEvent )                { if( myExitOnWindowClose == true ) LSSystem.SystemExit(0);  else myWindow.dispose(); }

   // Invoked when a window has been closed --------------------------------
   public void windowClosed(WindowEvent e)  {}

   // Invoked when a window is iconified -----------------------------------
   public void windowIconified(WindowEvent e) {}

   // Invoked when a window is de-iconified --------------------------------
   public void windowDeiconified(WindowEvent e) {}

   // Invoked when a window is activiated ----------------------------------
   public void windowActivated(WindowEvent e)  {}

   // Invoked when a window is de-activiated -------------------------------
   public void windowDeactivated(WindowEvent e) {}


   // Class variables ------------------------------------------------------
   private Window   myWindow;
   private boolean  myExitOnWindowClose;
}
