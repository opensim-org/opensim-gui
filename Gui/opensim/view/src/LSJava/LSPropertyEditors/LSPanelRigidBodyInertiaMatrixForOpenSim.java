//-----------------------------------------------------------------------------
// File:     LSPanelRigidBodyInertiaMatrixForOpenSim.java
// Parents:  LSPanelRigidBodyInertiaMatrixForOpenSim -> LSPanel -> Panel -> Container -> Component -> Object
// Purpose:  Displays/edits moments/products of inertia for rigid bodies.
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
import  LSJava.LSResources.*;
import  java.awt.*; 
import  java.awt.event.*;
import  javax.swing.JLabel; 
import  javax.swing.event.ChangeEvent;

import  org.opensim.modeling.OpenSimObject;
import  org.opensim.view.BodyDisplayer;
import  org.opensim.view.BodyToggleFrameAction;


//-----------------------------------------------------------------------------
// ActionListener listens for ENTER key.  
// FocusListener  listens for TAB key or mouse event to different field.
// KeyListener    listens for OK or ESCAPE key.
//-----------------------------------------------------------------------------
public class LSPanelRigidBodyInertiaMatrixForOpenSim extends LSPanel implements ActionListener, FocusListener, KeyListener 
{ 
   // Constructor ----------------------------------------------------------------
   public LSPanelRigidBodyInertiaMatrixForOpenSim( LSPropertyEditorRigidBody rigidBodyPropertyEditor ) 
   {
      myAssociatedRigidBodyPropertyEditor = rigidBodyPropertyEditor;

      LSContainer tabContainer = this.GetPanelAsContainer();
      tabContainer.SetContainerBackgroundColor( Color.white );

      // Create warning message label (which is usually hidden).
      tabContainer.AddBlankLabelToLayoutRowRemainder1High();
      myInertiaInvalidLabel = new LSLabel( "Error: INVALID inertia matrix", LSLabel.CENTER, tabContainer, GridBagConstraints.REMAINDER );
      myInertiaInvalidLabel.SetLabelForegroundColor( LSColor.AllRed );
      myInertiaInvalidLabel.SetLabelVisible( true );
 
      // Create inertia properties labels and text fields.
      tabContainer.AddBlankLabelToLayoutRowRemainder1High();
      new LSLabel( "<html>Inertia matrix about body center of mass for the body's &nbsp; <b>x</b>, <b>y</b>, <b>z</b> &nbsp; unit vectors</html>", LSLabel.CENTER, tabContainer, GridBagConstraints.REMAINDER );
      tabContainer.AddComponentToLayoutRowRemainder1High( this.CreatePanelWithInertiaMatrix() );

      // Add blank line separator.
      tabContainer.AddBlankLabelToLayoutRowRemainder1High();

      // Add picture of symmetric inertia matrix.
      JLabel labelWithPictureA = LSJava.LSResources.LSImageResource.GetJLabelFromLSResourcesFileNameScaled( "InertiaMatrixIsSymmetric.png", 0, 110 );
      if( labelWithPictureA != null )  tabContainer.AddComponentToLayout1Wide1High( labelWithPictureA );

      // Add blank spaces between pictures.
      tabContainer.AddBlankLabelToLayoutXWide1High( 3 );

      // Add picture of a rigid body with Bcm (Body center of mass) and Bo (Body origin).
      JLabel labelWithPictureB = LSJava.LSResources.LSImageResource.GetJLabelFromLSResourcesFileNameScaled( "RigidBodyWithCMAndBasisVectors.png", 0, 130 );
      if( labelWithPictureB != null )  tabContainer.AddComponentToLayoutRowRemainder1High( labelWithPictureB );

      // After creating inertia matrix, provide visual feedback on whether or not it is possible.
      this.CheckInertiaMatrixAndGiveVisualFeedbackOnWhetherOrNotItIsPhysicallyPossible();
   } 

   
   //-------------------------------------------------------------------------
   public void  actionPerformed( ActionEvent actionEvent )  { Object eventTarget = actionEvent.getSource();  this.CheckActionOrFocusLostOrKeyEventTarget(eventTarget); }
   public void  focusLost(   FocusEvent focusEvent )        { Object eventTarget =  focusEvent.getSource();  this.CheckActionOrFocusLostOrKeyEventTarget(eventTarget); }
   public void  focusGained( FocusEvent focusEvent )        { Object eventTarget =  focusEvent.getSource();  this.CheckFocusGainedEventTarget(eventTarget); } 

   //-------------------------------------------------------------------------
   public void  keyPressed(  KeyEvent keyEvent )  {;}
   public void  keyReleased( KeyEvent keyEvent )  {;}
   public void  keyTyped(    KeyEvent keyEvent )
   {
      Object eventTarget = keyEvent.getSource();
      if( eventTarget == this )
      {
         switch( keyEvent.getKeyChar() )
         {
            case KeyEvent.VK_ENTER:   
            case KeyEvent.VK_ESCAPE:  this.CheckActionOrFocusLostOrKeyEventTarget( eventTarget ); 
                                      break;
         }
      }
   }

   //-------------------------------------------------------------------------
   private void  CheckActionOrFocusLostOrKeyEventTarget( Object eventTarget )
   {
      if(      eventTarget==myIxxTextField || eventTarget==myIyyTextField || eventTarget==myIzzTextField ) this.ProcessEventMomentOfInertiaChanged( (LSTextField)eventTarget );
      else if( eventTarget==myIxyTextField  ) this.ProcessEventProductOfInertiaChanged( (LSTextField)eventTarget, myIyxTextField );  
      else if( eventTarget==myIxzTextField  ) this.ProcessEventProductOfInertiaChanged( (LSTextField)eventTarget, myIzxTextField );  
      else if( eventTarget==myIyzTextField  ) this.ProcessEventProductOfInertiaChanged( (LSTextField)eventTarget, myIzyTextField );
   }
   
   //-------------------------------------------------------------------------
   private void  CheckFocusGainedEventTarget( Object eventTarget )
   {
      if( eventTarget instanceof LSTextField )
      {
         LSTextField eventTargetTextField = (LSTextField)eventTarget;
         LSTextField textFieldToGetFocus = null;
         if(      eventTarget == myIyxTextField  )                                  textFieldToGetFocus = myIyxTextField; 
         else if( eventTarget == myIzxTextField || eventTarget == myIzyTextField )  textFieldToGetFocus = myIzzTextField;
         if( textFieldToGetFocus != null && eventTargetTextField.IsTextFieldValidDouble() )  textFieldToGetFocus.RequestFocus();
      }
   }


   //-----------------------------------------------------------------------------
   private LSPanel  CreatePanelWithInertiaMatrix( ) 
   { 
      myInertiaMatrixPanel = new LSPanel(); 
      LSContainer tabContainer = myInertiaMatrixPanel.GetPanelAsContainer();
      tabContainer.SetContainerBackgroundColor( Color.white );

      // Extra padding (spacing) between components in inertia matrix.
      tabContainer.SetConstraintInsets( 4, 4, 4, 4 );

      // Get the property for the rigid body object that propagates changes.
      LSPropertyTalkToSimbody propertyToTalkToSimbody = myAssociatedRigidBodyPropertyEditor.GetPropertyTalkToSimbody();

      // Create inertia properties labels and text fields.
      myIxxTextField = new LSTextFieldWithListenersForOpenSimDoubleNonNegative( propertyToTalkToSimbody, "inertia_xx", 0, true, tabContainer, 1, 1, this, this, this );   
      myIxyTextField = new LSTextFieldWithListenersForOpenSimDouble(            propertyToTalkToSimbody, "inertia_xy", 0, true, tabContainer, 1, 1, this, this, this );   
      myIxzTextField = new LSTextFieldWithListenersForOpenSimDouble(            propertyToTalkToSimbody, "inertia_xz", 0, true, tabContainer, GridBagConstraints.REMAINDER, 1, this, this, this );   
      myIyxTextField = new LSTextField( myIxyTextField.GetTextFieldAsString(), 0, false, tabContainer, 1, 1, null, this, null );
      myIyyTextField = new LSTextFieldWithListenersForOpenSimDoubleNonNegative( propertyToTalkToSimbody, "inertia_yy", 0, true, tabContainer, 1, 1, this, this, this );   
      myIyzTextField = new LSTextFieldWithListenersForOpenSimDouble(            propertyToTalkToSimbody, "inertia_yz", 0, true, tabContainer, GridBagConstraints.REMAINDER, 1, this, this, this );   
      myIzxTextField = new LSTextField( myIxzTextField.GetTextFieldAsString(), 0, false, tabContainer, 1, 1, null, this, null );
      myIzyTextField = new LSTextField( myIyzTextField.GetTextFieldAsString(), 0, false, tabContainer, 1, 1, null, this, null );
      myIzzTextField = new LSTextFieldWithListenersForOpenSimDoubleNonNegative( propertyToTalkToSimbody, "inertia_zz", 0, true, tabContainer, GridBagConstraints.REMAINDER, 1, this, this, this );   
      return myInertiaMatrixPanel;
   } 


   //-------------------------------------------------------------------------
   private void  ProcessEventMomentOfInertiaChanged( LSTextField eventTarget )
   {
      this.CheckInertiaMatrixAndGiveVisualFeedbackOnWhetherOrNotItIsPhysicallyPossible();
   }


   //-------------------------------------------------------------------------
   private void  ProcessEventProductOfInertiaChanged( LSTextField eventTarget, LSTextField sameValueProductOfInertiaInertiaTextField )
   {
      if( eventTarget.IsTextFieldValidDouble() )
         sameValueProductOfInertiaInertiaTextField.SetTextFieldFromString( eventTarget.GetTextFieldAsString() );
      this.CheckInertiaMatrixAndGiveVisualFeedbackOnWhetherOrNotItIsPhysicallyPossible();
   }


   //-------------------------------------------------------------------------
   private String  GetInertiaMatrixIfAllFieldsAreValidDoublesWithMomentsBeingNonNegativeOtherwiseNull( double inertiaArrayToFill[] )
   { 
      for( int i=0;  i<6;  i++ ) inertiaArrayToFill[i] = 0.0;

      if( !myIxxTextField.IsTextFieldValidDoubleNonNegative() ) return "Unable to interpret  Ixx  as non-negative number";
      if( !myIyyTextField.IsTextFieldValidDoubleNonNegative() ) return "Unable to interpret  Iyy  as non-negative number";
      if( !myIzzTextField.IsTextFieldValidDoubleNonNegative() ) return "Unable to interpret  Izz  as non-negative number";
      if( !myIxyTextField.IsTextFieldValidDouble() )		return "Unable to interpret  Ixy  as real number";
      if( !myIxzTextField.IsTextFieldValidDouble() )		return "Unable to interpret  Ixz  as real number";
      if( !myIyzTextField.IsTextFieldValidDouble() )		return "Unable to interpret  Iyz  as real number";
      
      inertiaArrayToFill[0] = myIxxTextField.GetTextFieldAsDouble(); 
      inertiaArrayToFill[1] = myIyyTextField.GetTextFieldAsDouble(); 
      inertiaArrayToFill[2] = myIzzTextField.GetTextFieldAsDouble(); 
      inertiaArrayToFill[3] = myIxyTextField.GetTextFieldAsDouble(); 
      inertiaArrayToFill[4] = myIxzTextField.GetTextFieldAsDouble(); 
      inertiaArrayToFill[5] = myIyzTextField.GetTextFieldAsDouble();
      return null;
   }


   //-------------------------------------------------------------------------
   private String  IsValidInertiaMomentsTriangleInequalityReturnNullOtherwiseErrorString( double Ixx, double Iyy, double Izz )
   {
      double momentOfInertiaSum = Ixx + Iyy + Izz;
      double epsilonMomentOfInertia = 1.0E-7 * momentOfInertiaSum; 
      if( Ixx > Iyy + Izz + epsilonMomentOfInertia ) return "Ixx > Iyy + Izz";
      if( Iyy > Ixx + Izz + epsilonMomentOfInertia ) return "Iyy > Ixx + Izz";
      if( Izz > Ixx + Iyy + epsilonMomentOfInertia ) return "Izz > Ixx + Iyy";
      return null;
   }
   
   //-------------------------------------------------------------------------
   private String  IsValidInertiaProductsSizedComparableToMomentsOfInertiaReturnNullOtherwiseErrorString( double Ixx, double Iyy, double Izz, double Ixy, double Ixz, double Iyz, boolean[] isValidIxyIxzIyz )
   {
      double momentOfInertiaSum = Ixx + Iyy + Izz;
      double epsilonMomentOfInertia = 1.0E-7 * momentOfInertiaSum; 
      double IxxPlusEpsilon = Ixx + epsilonMomentOfInertia;
      double IyyPlusEpsilon = Iyy + epsilonMomentOfInertia;
      double IzzPlusEpsilon = Izz + epsilonMomentOfInertia;
      boolean IxyOK = Math.abs( 2*Ixy ) <= IzzPlusEpsilon; 
      boolean IxzOK = Math.abs( 2*Ixz ) <= IyyPlusEpsilon;
      boolean IyzOK = Math.abs( 2*Iyz ) <= IxxPlusEpsilon;
      
      // Fill array so calling function can know which comparisons were bad.
      if( isValidIxyIxzIyz != null )
      {
          isValidIxyIxzIyz[0] = IxyOK;
          isValidIxyIxzIyz[1] = IxzOK;
          isValidIxyIxzIyz[2] = IyzOK;
      }
      
      if( !IxyOK ) return "2 * abs(Ixy) > Izz";
      if( !IxzOK ) return "2 * abs(Ixz) > Iyy";
      if( !IyzOK ) return "2 * abs(Iyz) > Ixx";
      return null;
   } 
   
   //-------------------------------------------------------------------------
   private void  CheckInertiaMatrixAndGiveVisualFeedbackOnWhetherOrNotItIsPhysicallyPossible( )
   {
      // By default, inertia matrix is valid unless something makes it invalid.
      boolean isValidInertiaMatrix = true;

      // Get all the numbers in the inertia matrix - or null if there is an error in one of the fields. 
      double inertiaArray[] = { 0, 0, 0, 0, 0, 0 };
      String errorStringIfBadMomentsProductsOfInertia = this.GetInertiaMatrixIfAllFieldsAreValidDoublesWithMomentsBeingNonNegativeOtherwiseNull( inertiaArray );
      if( errorStringIfBadMomentsProductsOfInertia != null )
      {
         isValidInertiaMatrix = false;  
         myInertiaInvalidLabel.SetLabelString( "Error -- INVALID inertia matrix:  " + errorStringIfBadMomentsProductsOfInertia );  
      }
          
      // Set the moment of inertia textfield background colors to suggest errors if triangle inequality is invalid.
      String errorStringIfBadTriangleInequality = !isValidInertiaMatrix ? null : this.IsValidInertiaMomentsTriangleInequalityReturnNullOtherwiseErrorString( inertiaArray[0], inertiaArray[1], inertiaArray[2] );
      if( errorStringIfBadTriangleInequality != null )
      {
         isValidInertiaMatrix = false;  
         myInertiaInvalidLabel.SetLabelString( "Error -- INVALID inertia matrix:  " + errorStringIfBadTriangleInequality );  
      }
      myIxxTextField.SetTextFieldForegroundColorOKOrError( errorStringIfBadTriangleInequality == null );
      myIyyTextField.SetTextFieldForegroundColorOKOrError( errorStringIfBadTriangleInequality == null );
      myIzzTextField.SetTextFieldForegroundColorOKOrError( errorStringIfBadTriangleInequality == null );
      
      // Set the product of inertia textfield background colors to suggest errors if products are improperly sized relatived to moments of inertia.
      boolean[] isValidIxyIxzIyz = { true, true, true };
      String errorStringIfBadProductsRatio = !isValidInertiaMatrix ? null : this.IsValidInertiaProductsSizedComparableToMomentsOfInertiaReturnNullOtherwiseErrorString( inertiaArray[0], inertiaArray[1], inertiaArray[2], inertiaArray[3], inertiaArray[4], inertiaArray[5], isValidIxyIxzIyz );
      if( errorStringIfBadProductsRatio != null )
      {
         isValidInertiaMatrix = false; 
         myInertiaInvalidLabel.SetLabelString( "Error -- INVALID inertia matrix:  " + errorStringIfBadProductsRatio );
      }   
      myIxyTextField.SetTextFieldForegroundColorOKOrError( isValidIxyIxzIyz[0] );
      myIxzTextField.SetTextFieldForegroundColorOKOrError( isValidIxyIxzIyz[1] );
      myIyzTextField.SetTextFieldForegroundColorOKOrError( isValidIxyIxzIyz[2] );
      
      // Possibly show the label that announced the inertia matrix is invalid.
      myInertiaMatrixPanel.GetPanelAsContainer().SetContainerBackgroundColor( isValidInertiaMatrix ? LSColor.White : LSColor.BackgroundColorSuggestingError );    
      myInertiaInvalidLabel.SetLabelVisible( !isValidInertiaMatrix );
      myAssociatedRigidBodyPropertyEditor.GetDialogAsContainer().Repaint();
      
      // Show center of mass as an appropriately sized ellipsoid to help with visual debugging.
      if( isValidInertiaMatrix )
      {
          OpenSimObject openSimObject = myAssociatedRigidBodyPropertyEditor.GetAssociatedOpenSimObject();
          BodyDisplayer rep = BodyToggleFrameAction.GetBodyDisplayerForBody( openSimObject );
          if( rep != null )
          { 
             double Ixx = myIxxTextField.GetTextFieldAsDouble();
             double Iyy = myIyyTextField.GetTextFieldAsDouble();
             double Izz = myIzzTextField.GetTextFieldAsDouble();
             rep.SetCMSphereToEllipsoidFromMomentsOfInertiaOnly( Ixx, Iyy, Izz, true );
          }
      }
   }


   //-----------------------------------------------------------------------------
   // Class data
   private LSTextFieldWithListenersForOpenSimDoubleNonNegative  myIxxTextField;
   private LSTextFieldWithListenersForOpenSimDoubleNonNegative  myIyyTextField;
   private LSTextFieldWithListenersForOpenSimDoubleNonNegative  myIzzTextField;
   private LSTextFieldWithListenersForOpenSimDouble             myIxyTextField;
   private LSTextFieldWithListenersForOpenSimDouble             myIxzTextField;
   private LSTextFieldWithListenersForOpenSimDouble             myIyzTextField;
   private LSTextField                                          myIyxTextField;
   private LSTextField                                          myIzxTextField;
   private LSTextField                                          myIzyTextField;
   private LSLabel  myInertiaInvalidLabel;
   private LSPanel  myInertiaMatrixPanel;
   LSPropertyEditorRigidBody  myAssociatedRigidBodyPropertyEditor;
}



