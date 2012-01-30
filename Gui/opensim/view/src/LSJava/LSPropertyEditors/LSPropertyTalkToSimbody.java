//-----------------------------------------------------------------------------
// File:     LSPropertyTalkToSimbody.java
// Class:    LSPropertyTalkToSimbody
// Parents:  None
// Purpose:  Helpful class that easily communicates changes made in Java classes
//           (e.g., components such as text fields, sliders, and buttons)
//           to the Model (in Model/View/Controller) that is stored in the
//           OpenSim API which is written in C++.  In other words, simplifies 
//           communication Java to fields in OpenSimObjectModel.
// Authors:  Paul Mitiguy, 2011-2012.   
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
import  LSJava.LSUtility.*;
import  LSJava.LSComponents.*;
import  java.io.IOException;

import  org.opensim.modeling.Model;
import  org.opensim.modeling.OpenSimObject;
import  org.opensim.modeling.PropertySet;
import  org.opensim.modeling.Property;
import  org.opensim.modeling.ArrayDouble;
import  org.opensim.view.nodes.OpenSimObjectNode;  
import  org.opensim.view.nodes.OpenSimNode;
import  org.opensim.view.pub.OpenSimDB;


//-----------------------------------------------------------------------------
public class LSPropertyTalkToSimbody  
{ 
   //-----------------------------------------------------------------------------
   public  LSPropertyTalkToSimbody( OpenSimObject openSimObjectPassedToConstructorShouldNotBeNull )
   { 
      // Connect this object to its corresponding OpenSim object.
      // Note: Some of the connection occurs in other constructors.
      myOpenSimObject = openSimObjectPassedToConstructorShouldNotBeNull;
   } 


   //-----------------------------------------------------------------------------
   public OpenSimObject  GetOpenSimObject()       { return myOpenSimObject; }  
   public String         GetOpenSimObjectName( )  { return this.GetOpenSimObject().getName(); } 


   //-----------------------------------------------------------------------------
   public int  GetTextFieldWidthForOpenSimObjectName( )  
   { 
      String objectName = this.GetOpenSimObjectName();
      int  lengthOfObjectName = LSString.GetStringLength( objectName );
      double textFieldWidthForObjectName = 1.5 * ( lengthOfObjectName < 12 ? 12 : lengthOfObjectName );
      return (int)textFieldWidthForObjectName;
   }
   

   //-------------------------------------------------------------------------
   public Property  GetOpenSimObjectPropertyValueFromPropertyName( String propertyName ) 
   {
      PropertySet propertySet = this.GetOpenSimObject().getPropertySet();
      if( propertySet != null )
      {
         try{ return propertySet.get( propertyName );
         } catch( IOException ex ) { ex.printStackTrace(); }
      }
      return null;
   }
   
   //-------------------------------------------------------------------------
   public double  GetOpenSimObjectPropertyValueAsDoubleFromPropertyName( String propertyName ) 
   {
      Property openSimObjectProperty = this.GetOpenSimObjectPropertyValueFromPropertyName( propertyName );
      return openSimObjectProperty==null ? 0.0 : openSimObjectProperty.getValueDbl();
   }
   
   //-------------------------------------------------------------------------
   private ArrayDouble  GetOpenSimObjectPropertyValueAsArrayDoubleFromPropertyName( String propertyName ) 
   {
      Property openSimObjectProperty = this.GetOpenSimObjectPropertyValueFromPropertyName( propertyName );
      return openSimObjectProperty==null ? null : null; // openSimObjectProperty.getValueDblArray();
      // TODO: There is some type mis-match between Java/C++ double arrays that crashes the GUI completely and without warning.
   }

   //-------------------------------------------------------------------------
   public double[]  GetOpenSimObjectPropertyValueAsArrayDoubleNFromPropertyName( String propertyName, int expectedNumberOfElements ) 
   {
      ArrayDouble arrayDouble = this.GetOpenSimObjectPropertyValueAsArrayDoubleFromPropertyName( propertyName ); 
      boolean isValid = (arrayDouble!=null && arrayDouble.getSize() == expectedNumberOfElements);
      double retValue[] = new double[ expectedNumberOfElements ];
      for( int i=0;  i<expectedNumberOfElements; i++ ) retValue[i] = isValid ? arrayDouble.getitem(i) : 0.0;
      return retValue;
   }
      
   //-------------------------------------------------------------------------
   public double[]  GetOpenSimObjectPropertyValueAsArrayDouble3FromPropertyName( String propertyName )  { return this.GetOpenSimObjectPropertyValueAsArrayDoubleNFromPropertyName( propertyName, 3 ); }  
      

   //-------------------------------------------------------------------------
   public void  SetOpenSimObjectPropertyValueAsDoubleForPropertyName( String propertyName, double valueToSet ) 
   {
      Property openSimObjectProperty = this.GetOpenSimObjectPropertyValueFromPropertyName( propertyName );
      if( openSimObjectProperty != null ) openSimObjectProperty.setValueDbl( valueToSet );
      this.InitializeSystemForTheModelAssociatedWithThisProperty();
   }


   //-------------------------------------------------------------------------
   private void  InitializeSystemForTheModelAssociatedWithThisProperty( )
   { 
      // Paul: Ask Ayman if there is better way to ensure that we find the model that goes with this property.
      Model currentModel = OpenSimDB.getInstance().getCurrentModel();
      LSPropertyTalkToSimbody.InitializeSystemForModel( currentModel );
   }


   //-------------------------------------------------------------------------
   private static void  InitializeSystemForModel( Model aModel )  
   { 
      try { aModel.initSystem(); } 
      catch (IOException ex) { ex.printStackTrace(); }
   }


   //-----------------------------------------------------------------------------
   public boolean  SetOpenSimObjectName( String newName, OpenSimNode associatedOpenSimNodeOrNull )    
   { 
      String oldName = this.GetOpenSimObjectName();
      if( newName != null && LSString.IsStringsEqualCaseSensitive(newName,oldName) == false )
      {
         this.GetOpenSimObject().setName( newName );
         if( associatedOpenSimNodeOrNull != null )
            associatedOpenSimNodeOrNull.renameObjectNode( this.GetOpenSimObject(), newName );
         return true;
      }
      return false; // No name change.
   } 


   //-----------------------------------------------------------------------------
   // Class data
   private   OpenSimObject  myOpenSimObject;  

}



