//--------------------------------------------------------------------------
// File:     LSMenu.java
// Parent:   Menu
// Purpose:  Abstract class for handling menus
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


//--------------------------------------------------------------------------
abstract public class LSMenu extends Menu
{

   // Constructor ----------------------------------------------------------
   public LSMenu( String menuName, LSFrame ownerFrame )
   {
      super( menuName );
      myOwnerFrame = ownerFrame;
   }

   // ----------------------------------------------------------------------
   protected void  AddSeparator()  { super.addSeparator(); }


   // ----------------------------------------------------------------------
   abstract public    void  ProcessMenuAction( LSMenuItem menuItem );
   abstract protected void  AddMenuItems( );


   // ----------------------------------------------------------------------
   protected LSMenuItem  CreateMenuItem( String itemName )  { return new LSMenuItem(itemName,this,myOwnerFrame); }


   //-----------------------------------------------------------------------
   // Frame which created the menuBar to which this menu is attached
   protected LSFrame  myOwnerFrame;

}
