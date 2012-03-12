//-----------------------------------------------------------------------------
// File:     LSPanelRigidBodyMassAndCMForOpenSim.java
// Parents:  LSPanelRigidBodyMassAndCMForOpenSim -> LSPanel -> Panel -> Container -> Component -> Object
// Purpose:  Displays/edits mass and center of mass for rigid bodies.
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
import  java.awt.*; 
import  java.awt.event.*;
import  javax.swing.JLabel; 

import  org.opensim.modeling.OpenSimObject;
import  org.opensim.view.BodyDisplayer;
import  org.opensim.view.BodyToggleFrameAction;
import  org.opensim.view.nodes.BodyToggleCOMAction;


//-----------------------------------------------------------------------------
// ActionListener listens for ENTER key.  
// FocusListener  listens for TAB key or mouse event to different field.
// KeyListener    listens for OK or ESCAPE key.
//-----------------------------------------------------------------------------
public class LSPanelRigidBodyMassAndCMForOpenSim extends LSPanel implements ActionListener, FocusListener, KeyListener, ItemListener 
{ 
   // Constructor ----------------------------------------------------------------
   public LSPanelRigidBodyMassAndCMForOpenSim( LSPropertyEditorRigidBody rigidBodyPropertyEditor ) 
   {
      // Get the property for the rigid body object that propagates changes.
      myAssociatedRigidBodyPropertyEditor = rigidBodyPropertyEditor;
      LSPropertyTalkToSimbody propertyToTalkToSimbody = myAssociatedRigidBodyPropertyEditor.GetPropertyTalkToSimbody();

      LSContainer tabContainer = this.GetPanelAsContainer();
      tabContainer.SetContainerBackgroundColor( Color.white );

      // Create warning message label (which is usually hidden).
      myMassPropertiesInvalidLabel = new LSLabel( "Error: Invalid mass properties", LSLabel.CENTER, tabContainer, GridBagConstraints.REMAINDER );
      myMassPropertiesInvalidLabel.SetLabelForegroundColor( LSColor.AllRed );
      myMassPropertiesInvalidLabel.SetLabelVisible( true );
 
      // Create mass label and text field.
      tabContainer.AddBlankLabelToLayoutRowRemainder1High();
      LSLabel massLabel = new LSLabel( "Mass  =  ", LSLabel.RIGHT, tabContainer );
      myMassTextField = new LSTextFieldWithListenersForOpenSimDoubleNonNegative( propertyToTalkToSimbody, "mass", 0, true, tabContainer, 1, 1, this, this, this );   
      tabContainer.AddBlankLabelToLayoutRowRemainder1High();

      // Create center of mass labels and text fields.
      tabContainer.AddBlankLabelToLayoutRowRemainder1High();
      new LSLabel( "Position of rigid Body center of mass (Bcm) from rigid Body origin (Bo)", LSLabel.CENTER, tabContainer, GridBagConstraints.REMAINDER );
      
      new LSLabel( "Position  =  ", LSLabel.RIGHT, tabContainer );
      myXcmTextField  = new LSTextFieldWithListenersForOpenSimArrayDouble( 0, propertyToTalkToSimbody, "mass_center", 0, true, tabContainer, 1, 1, this, this, this );   
      new LSLabel( " * bx", LSLabel.LEFT, tabContainer, GridBagConstraints.REMAINDER );
      
      new LSLabel( "  +  ", LSLabel.RIGHT, tabContainer );
      myYcmTextField  = new LSTextFieldWithListenersForOpenSimArrayDouble( 1, propertyToTalkToSimbody, "mass_center", 0, true, tabContainer, 1, 1, this, this, this );   
      new LSLabel( " * by", LSLabel.LEFT, tabContainer, GridBagConstraints.REMAINDER );

      new LSLabel( "  +  ", LSLabel.RIGHT, tabContainer );
      myZcmTextField  = new LSTextFieldWithListenersForOpenSimArrayDouble( 2, propertyToTalkToSimbody, "mass_center", 0, true, tabContainer, 1, 1, this, this, this );   
      new LSLabel( " * bz", LSLabel.LEFT, tabContainer, GridBagConstraints.REMAINDER );

      // Add show center of mass checkbox.
      tabContainer.AddBlankLabelToLayout1Wide1High(); 
      OpenSimObject openSimObject = myAssociatedRigidBodyPropertyEditor.GetAssociatedOpenSimObject();      
      boolean initialStateOfShowCM = BodyToggleCOMAction.IsShowCMForBody( openSimObject );
      myShowCMCheckboxB = new LSCheckbox( "Show center of mass", initialStateOfShowCM, null, tabContainer, GridBagConstraints.REMAINDER, 1, this );

      // Add picture of a rigid body with Bcm (Body center of mass) and Bo (Body origin).
      JLabel labelWithPicture = LSJava.LSResources.LSImageResource.GetJLabelFromLSResourcesFileNameScaled( "RigidBodyWithOriginCMAndBasisVectors.png", 0, 140 );
      if( labelWithPicture != null )  tabContainer.AddComponentToLayoutRowRemainder1High( labelWithPicture );

      // After creating mass properties, provide visual feedback on whether or not it is possible.
      this.CheckMassPropertiesAndGiveVisualFeedbackOnWhetherOrNotItIsPhysicallyPossible();
   } 

   
   //-------------------------------------------------------------------------
   public void  actionPerformed( ActionEvent actionEvent )  { Object eventTarget = actionEvent.getSource();  this.CheckActionOrFocusLostOrKeyEventTarget(eventTarget); }
   public void  focusLost(   FocusEvent focusEvent )        { Object eventTarget =  focusEvent.getSource();  this.CheckActionOrFocusLostOrKeyEventTarget(eventTarget); }
   public void  focusGained( FocusEvent focusEvent )        {;}

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
	    case KeyEvent.VK_TAB:
            case KeyEvent.VK_ENTER:   
            case KeyEvent.VK_ESCAPE:  this.CheckActionOrFocusLostOrKeyEventTarget( eventTarget ); 
                                      break;
         }
      }
   }


   //-------------------------------------------------------------------------
   public void  itemStateChanged( ItemEvent itemEvent )     
   {
      Object eventTarget = itemEvent.getSource();
      if( eventTarget == myShowCMCheckboxB ) 
         myAssociatedRigidBodyPropertyEditor.ProcessEventShowCMOrShowBodyAxes( eventTarget );
   }


   //-------------------------------------------------------------------------
   public LSCheckbox  GetShowCMCheckboxB( )  { return myShowCMCheckboxB; }

   
   //-------------------------------------------------------------------------
   private void  CheckActionOrFocusLostOrKeyEventTarget( Object eventTarget )
   {
      if( eventTarget==myXcmTextField || eventTarget==myYcmTextField || eventTarget==myZcmTextField )
      {
         this.CheckMassPropertiesAndGiveVisualFeedbackOnWhetherOrNotItIsPhysicallyPossible( );
         this.ProcessEventChangedCMPositionEvent(); 
      }
      else if( eventTarget == myMassTextField )
         this.CheckMassPropertiesAndGiveVisualFeedbackOnWhetherOrNotItIsPhysicallyPossible( );
   }
   

   //-------------------------------------------------------------------------
   private String  GetMassIsValidNonNegativeReturnNullOtherwiseErrorString( )
   { 
      return myMassTextField.IsTextFieldValidDoubleNonNegative() ? null : "Error: Unable to interpret  Mass  as non-negative number";
   }

   //-------------------------------------------------------------------------
   private String  GetAllCMTextFieldsAreValidDoublesReturnNullOtherwiseErrorString( )
   { 
      if( !myXcmTextField.IsTextFieldValidDouble() )  return "Error: Unable to interpret  xPosition  as real number";
      if( !myYcmTextField.IsTextFieldValidDouble() )  return "Error: Unable to interpret  yPosition  as real number";
      if( !myZcmTextField.IsTextFieldValidDouble() )  return "Error: Unable to interpret  zPosition  as real number";
      return null;
   }


   //-------------------------------------------------------------------------
   private void  CheckMassPropertiesAndGiveVisualFeedbackOnWhetherOrNotItIsPhysicallyPossible( )
   {
      // Check to see if any of the text fields in this panel returns an error message.
      String errorStringToDisplay = this.GetMassIsValidNonNegativeReturnNullOtherwiseErrorString();
      if( errorStringToDisplay == null ) errorStringToDisplay = this.GetAllCMTextFieldsAreValidDoublesReturnNullOtherwiseErrorString(); 
      boolean isValidMassAndCM = errorStringToDisplay == null;
      if( !isValidMassAndCM ) myMassPropertiesInvalidLabel.SetLabelString( errorStringToDisplay );  
          
      // Possibly show the label that announced the inertia matrix is invalid.
      myMassPropertiesInvalidLabel.SetLabelVisible( !isValidMassAndCM );
      myAssociatedRigidBodyPropertyEditor.GetDialogAsContainer().Repaint();
   }


   //-------------------------------------------------------------------------
   private void  ProcessEventChangedCMPositionEvent( )
   {
      if( myShowCMCheckboxB.GetCheckboxState() && this.GetAllCMTextFieldsAreValidDoublesReturnNullOtherwiseErrorString()==null )
      {
         OpenSimObject openSimObject = myAssociatedRigidBodyPropertyEditor.GetAssociatedOpenSimObject();
         BodyDisplayer rep = BodyToggleFrameAction.GetBodyDisplayerForBody( openSimObject );
         if( rep != null ) rep.SetCMLocationFromPropertyTable( true );
      }
   } 



   //-----------------------------------------------------------------------------
   // Class data.
   private LSCheckbox  myShowCMCheckboxB;              // Checkbox on mass and center of mass tab
   private LSLabel     myMassPropertiesInvalidLabel;   // Only visible if there is an error in this tab.
   private LSTextFieldWithListenersForOpenSimDoubleNonNegative  myMassTextField;  
   private LSTextFieldWithListenersForOpenSimArrayDouble        myXcmTextField,  myYcmTextField,  myZcmTextField;   
   LSPropertyEditorRigidBody  myAssociatedRigidBodyPropertyEditor;
}



