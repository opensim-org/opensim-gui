//--------------------------------------------------------------------------
// File:     LSPanel.java
// Parents:  Panel -> Container -> Component -> Object
// Purpose:  Holds generic data and methods for a panel (part of a frame or window)
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
//import  javax.swing.JPanel;


//--------------------------------------------------------------------------
public class LSPanel extends Panel
{
   // Constructor ---------------------------------------------------------
   public LSPanel( )  { this( null, 0, 0 ); }
   public LSPanel( LSContainer container, int gridWidth, int gridHeight )
   {
      // May be used as a generic Panel with LSContainer that does not immediately get added to a frame or window.
      if( container != null )
      {
         // Keep a copy of the constraints for adding and removing this component to frames, dialogs, panels, ...
         myConstraintsWhenCreated = container.GetConstraintsClone();
         myConstraintsWhenCreated.gridwidth  = gridWidth;
         myConstraintsWhenCreated.gridheight = gridHeight;

         // This panel is part of another container (frame, dialog, ...)
         container.AddComponentToLayout( this, myConstraintsWhenCreated );
      }

      // Layout manager, constraints, and set default values (color and font)
      myContainer = new LSContainer( this );
   }



   //-------------------------------------------------------------------------
   public LSContainer         GetPanelAsContainer( )        { return myContainer; }
   public GridBagConstraints  GetConstraintsWhenCreated( )  { return myConstraintsWhenCreated; }

   // Class variables --------------------------------------------------------
   private   GridBagConstraints  myConstraintsWhenCreated;
   protected LSContainer         myContainer;
}
