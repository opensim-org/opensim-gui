//--------------------------------------------------------------------------
// File:     LSTextFieldWithListenersForOpenSimAbstract.java
// Parent:   LSTextFieldWithListenersAbstract
// Purpose:  LSTextFields with listeners for event handling and connection to OpenSimObject properties.
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
package LSJava.LSPropertyEditors;
import  LSJava.LSComponents.*;
import  LSJava.LSUtility.*;
import  java.awt.event.*;
import  java.awt.*;


//--------------------------------------------------------------------------
public abstract class LSTextFieldWithListenersForOpenSimAbstract extends LSTextFieldWithListenersAbstract
{
   // Constructor ---------------------------------------------------------
   public LSTextFieldWithListenersForOpenSimAbstract( LSPropertyTalkToSimbody propertyToTalkToSimbody, String openSimPropertyName, String initialString, int textWidth, boolean isEditable, LSContainer container, int gridWidth, int gridHeight, ActionListener actionListenerOrNull, FocusListener focusListenerOrNull, KeyListener keyListenerOrNull )
   {
      super( LSColor.BackgroundColorSuggestingError, initialString, textWidth, isEditable, container, gridWidth, gridHeight, actionListenerOrNull, focusListenerOrNull, keyListenerOrNull );
      myPropertyToTalkToSimbody = propertyToTalkToSimbody;
      myOpenSimPropertyName = openSimPropertyName;
   }


   //-------------------------------------------------------------------------
   abstract protected String  EventActionOrFocusLostOrKeyEventReturnErrorStringVirtual( );


   //-------------------------------------------------------------------------
   public LSPropertyTalkToSimbody  GetPropertyToTalkToSimbody( )  { return myPropertyToTalkToSimbody; }   
   public String  GetAssociatedOpenSimPropertyName( )             { return myOpenSimPropertyName; }

   // Class variables --------------------------------------------------------
   private LSPropertyTalkToSimbody  myPropertyToTalkToSimbody;
   private String  myOpenSimPropertyName;

}
