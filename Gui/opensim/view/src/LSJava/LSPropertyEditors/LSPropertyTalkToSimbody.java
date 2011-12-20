//-----------------------------------------------------------------------------
// File:     LSPropertyTalkToSimbody.java
// Class:    LSPropertyTalkToSimbody
// Parents:  None
// Purpose:  Helpful class that easily communicates changes made in Java classes
//           (e.g., components such as text fields, sliders, and buttons)
//           to the Model (in Model/View/Controller) that is stored in the
//           OpenSim API which is written in C++.  In other words, simplifies 
//           communication Java to fields in OpenSimObjectModel.
// Authors:  Paul Mitiguy.  Contributors: Ayman Habib, Michael Sherman, Scott Delp.   
//--------------------------------------------------------------------------
// License TBD.
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
import  org.opensim.view.nodes.OpenSimObjectNode;  


//-----------------------------------------------------------------------------
public class LSPropertyTalkToSimbody  
{ 
   //-----------------------------------------------------------------------------
   public  LSPropertyTalkToSimbody()  {;}

   //-----------------------------------------------------------------------------
   public  LSPropertyTalkToSimbody( OpenSimObjectNode obj )
   { 
      this.SetOpenSimObjectNodeIssueErrorIfNull( obj );
   } 

   //-----------------------------------------------------------------------------
   public  double   GetModelValueDouble( ) {return 0.0;}
   public  boolean  SetModelValueDouble( double valueToSet ) {return true;}
   
   //-----------------------------------------------------------------------------
   public  OpenSimObjectNode  GetOpenSimObjectNodeIssueErrorIfNull( )                        { this.IssueErrorIfOpenSimObjectNodeIsNull();  return myOpenSimObjectNode; }
   public  boolean            SetOpenSimObjectNodeIssueErrorIfNull( OpenSimObjectNode obj )  { myOpenSimObjectNode = obj;  return this.IssueErrorIfOpenSimObjectNodeIsNull() == false; }


   //-----------------------------------------------------------------------------
   public  boolean  IssueErrorIfOpenSimObjectNodeIsNull( )
   { 
      if( myOpenSimObjectNode == null ) { LSMessageDialog.DebugMessage( "myOpenSimObjectNode is Null in class LSPropertyTalkToSimbody" );  return true; }
      return false;
   } 


   //-----------------------------------------------------------------------------
   // Class data
   private OpenSimObjectNode  myOpenSimObjectNode;     

}



