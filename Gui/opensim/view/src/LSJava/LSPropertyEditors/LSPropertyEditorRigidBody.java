//-----------------------------------------------------------------------------
// File:     LSPropertyEditorRigidBody.java
// Class:    LSPropertyEditorRigidBody
// Parents:  LSDialog
// Purpose:  Displays/edits properties for rigid bodies.
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
import  javax.swing.JTabbedPane; 
import  javax.swing.JLabel; 
import  javax.swing.event.ChangeEvent;

import  org.opensim.view.ModelWindowVTKTopComponent;
import  org.opensim.modeling.OpenSimObject;
import  org.opensim.view.nodes.OpenSimObjectNode;
import  org.opensim.view.nodes.OneBodyNode;
import  org.opensim.view.nodes.BodyToggleCOMAction;
import  org.opensim.view.BodyToggleFrameAction;


//-----------------------------------------------------------------------------
public class LSPropertyEditorRigidBody extends LSPropertyEditorTabbedAbstract implements ActionListener, FocusListener, ItemListener, KeyListener 
{ 
   // Constructor ---------------------------------------------------------------- 
   public LSPropertyEditorRigidBody( OneBodyNode oneBodyNodePassedToConstructor, ModelWindowVTKTopComponent ownerWindowPassedToConstructor )  
   { 
       this( oneBodyNodePassedToConstructor.getOpenSimObject(), oneBodyNodePassedToConstructor, ownerWindowPassedToConstructor ); 
   }

   // Constructor ----------------------------------------------------------------
   public  LSPropertyEditorRigidBody( OpenSimObject openSimObjectToConstructorShouldNotBeNull, OneBodyNode oneBodyNodeToConstructorMayBeNull, ModelWindowVTKTopComponent ownerWindowPassedToConstructor  )  
   { 
      super( openSimObjectToConstructorShouldNotBeNull, oneBodyNodeToConstructorMayBeNull, ownerWindowPassedToConstructor, "BodyCMPicture.png", "Rigid Body", 560, 400 );  

      // Add panels, choose the previously selected panel to show, and display the window. 
      this.AddPanelsToFrame(); 
      super.SetSelectedTabbedPaneFromPriorUserSelection();
      this.GetDialogAsContainer().PackLocateShow();
   } 
   
   //-------------------------------------------------------------------------
   public void  actionPerformed( ActionEvent actionEvent )  { this.CheckActionEventTarget(     actionEvent.getSource() ); }
   public void  focusLost(   FocusEvent focusEvent )        { this.CheckFocusLostEventTarget(   focusEvent.getSource() ); }
   public void  focusGained( FocusEvent focusEvent )        { this.CheckFocusGainedEventTarget( focusEvent.getSource() ); } 

   //-------------------------------------------------------------------------
   public void  itemStateChanged( ItemEvent itemEvent )     
   {
      Object eventTarget = itemEvent.getSource();
      if(      eventTarget == myShowBodyCheckbox )            super.ShowOrHideObject( true  ); // myShowBodyCheckbox.GetCheckboxState();
      else if( eventTarget == myHideBodyCheckbox )            super.ShowOrHideObject( false );
      else if( eventTarget == myShowCMCheckbox ) 	      BodyToggleCOMAction.ShowCMForOneBodyNode( this.GetAssociatedOpenSimOneBodyNodeOrNull(), myShowCMCheckbox.GetCheckboxState(), true );
      else if( eventTarget == myShowBodyAxesCheckbox )	      BodyToggleFrameAction.ShowAxesForBody( super.GetAssociatedOpenSimObject(), myShowBodyAxesCheckbox.GetCheckboxState(), true );
      else if( eventTarget == myWireFrameCheckbox ) 	      { super.SetObjectRepresentationPointsWireFrameOrSurfaceAndSurfaceShading( 1, 0 );  super.ShowOrHideObject( true  );  myHideBodyCheckbox.SetCheckboxState(false); }
      else if( eventTarget == mySurfaceShadedSmoothCheckbox ) { super.SetObjectRepresentationPointsWireFrameOrSurfaceAndSurfaceShading( 2, 1 );  super.ShowOrHideObject( true  );  myHideBodyCheckbox.SetCheckboxState(false); }
   // else if( eventTarget == mySurfaceShadedFlatCheckbox )   { super.SetObjectRepresentationPointsWireFrameOrSurfaceAndSurfaceShading( 2, 0 );  super.ShowOrHideObject( true  );  myHideBodyCheckbox.SetCheckboxState(false); }
   }

  
   //-------------------------------------------------------------------------
   public void  keyPressed(  KeyEvent keyEvent )  {;}
   public void  keyReleased( KeyEvent keyEvent )  {;}
   public void  keyTyped(    KeyEvent keyEvent )
   {
      switch( keyEvent.getKeyChar() )
      {
         case KeyEvent.VK_ESCAPE:
         case KeyEvent.VK_ENTER:   this.CheckFocusLostEventTarget( keyEvent.getSource() ); break;
      }
   }

   //-------------------------------------------------------------------------
   public void  stateChanged( ChangeEvent changeEvent ) 
   {
      Object eventTarget = changeEvent.getSource();
      if( eventTarget == myOpacitySlider ) { if( !myOpacitySlider.getValueIsAdjusting() )  super.SetObjectOpacity( myOpacitySlider.GetSliderValueAsInteger() ); } 
   }


   //-------------------------------------------------------------------------
   private void  CheckActionEventTarget( Object eventTarget )
   {
      if(      eventTarget == myButtonToOpenOldPropertyViewerTable ) super.ProcessEventClickButtonToOpenOldPropertyViewerTable();
      else if( eventTarget == myButtonToOpenColorChooser )           super.CreateColorDialogBoxToChangeUserSelectedColor();
   }
   
   //-------------------------------------------------------------------------
   private boolean  CheckFocusLostEventTarget( Object eventTarget )
   {
      boolean requestFocusBack = false;
      Window ownerWindowOrThis = super.GetLSDialogOwnerWindowOrThis(); 
      if(      eventTarget==myNameTextField )  super.SetOpenSimObjectNameAndPropertyEditorDialogTitle( myNameTextField.GetTextFieldAsString() );
      else if( eventTarget==myXcmTextField  )  requestFocusBack = myXcmTextField.IssueErrorMessageIfTextFieldIsBadDoublePrecisionNumber(ownerWindowOrThis);
      else if( eventTarget==myYcmTextField  )  requestFocusBack = myYcmTextField.IssueErrorMessageIfTextFieldIsBadDoublePrecisionNumber(ownerWindowOrThis);
      else if( eventTarget==myZcmTextField  )  requestFocusBack = myZcmTextField.IssueErrorMessageIfTextFieldIsBadDoublePrecisionNumber(ownerWindowOrThis);
      else if( eventTarget==myIxxTextField || eventTarget==myIyyTextField || eventTarget==myIzzTextField ) this.CheckInertiaMatrixAndGiveVisualFeedbackOnWhetherOrNotItIsPhysicallyPossible();
      else if( eventTarget==myIxyTextField  )  requestFocusBack = this.ProcessEventProductOfInertiaChanged( (LSTextField)eventTarget, myIyxTextField );
      else if( eventTarget==myIxzTextField  )  requestFocusBack = this.ProcessEventProductOfInertiaChanged( (LSTextField)eventTarget, myIzxTextField );
      else if( eventTarget==myIyzTextField  )  requestFocusBack = this.ProcessEventProductOfInertiaChanged( (LSTextField)eventTarget, myIzyTextField );
      if( requestFocusBack && eventTarget instanceof LSTextField ) { LSTextField eventTargetLSTextField = (LSTextField)eventTarget;  eventTargetLSTextField.RequestFocus(); }
      return requestFocusBack;
   }
   
   //-------------------------------------------------------------------------
   private void  CheckFocusGainedEventTarget( Object eventTarget )
   {
      if(      eventTarget == myIyxTextField  )   myIyxTextField.RequestFocus(); 
      else if( eventTarget == myIzxTextField || eventTarget == myIzyTextField   )   myIzzTextField.RequestFocus();
      
      // Change the target to bold-face font.
      // if( eventTarget instanceOf LSTextField ) BLAH;
   }

   //-----------------------------------------------------------------------------
   private void  AddPanelsToFrame() 
   { 
      super.AddTabToPropertyEditor( "Appearance",               null,  this.CreateTabComponentAppearanceRigidBodyProperty(),             null );
      super.AddTabToPropertyEditor( "Mass and Center of Mass",  null,  this.CreateTabComponentMassAndCenterOfMassRigidBodyProperty(),    null );
      super.AddTabToPropertyEditor( "Inertia Properties",       null,  this.CreateTabComponentInertiaPropertiesRigidBodyProperty(),      null );
        
      // Note: The following create a shortcut of the user pressing ALT-1 to get the 0th panel added to tabbedPane, etc. 
      // tabbedPane.setMnemonicAt( 0, KeyEvent.VK_1 ); 
      // tabbedPane.setMnemonicAt( 1, KeyEvent.VK_2 ); 
      // tabbedPane.setMnemonicAt( 2, KeyEvent.VK_3 ); 
      // tabbedPane.setMnemonicAt( 3, KeyEvent.VK_4 ); 

      // Add tabbed pane to this frame.   Use layout policy JTabbedPane.WRAP_TAB_LAYOUT or JTabbedPane.SCROLL_TAB_LAYOUT. 
      super.AddTabbedPanelToThisWithDesignatedLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT );  
   } 


   //-----------------------------------------------------------------------------
   private Component  CreateTabComponentAppearanceRigidBodyProperty( ) 
   { 
      LSPanel     tabComponentWithTwoPanels = new LSPanel(); 
      LSContainer tabComponentWithTwoPanelsContainer = tabComponentWithTwoPanels.GetPanelAsContainer();
      tabComponentWithTwoPanelsContainer.SetContainerBackgroundColor( Color.white );
      
      // Other possible images are: ImageIcon  openSimImageIcon = TheApp.getApplicationIcon()  and  Image openSimLogoImage = TheApp.getAppImage();
      LSPanel picturePanel = LSImageResource.GetLSPanelFromLSResourcesFileNameScaled( "OpenSimLogoNoWords.jpg", 0, 150, Color.white );
      tabComponentWithTwoPanelsContainer.AddComponentToLayoutColRemainder1Wide( picturePanel );
      tabComponentWithTwoPanelsContainer.AddLabelToLayout( "  ", LSLabel.CENTER, 1, GridBagConstraints.REMAINDER );

      // Add a panel with relevant buttons, textboxes, etc.
      LSPanel tabComponent = new LSPanel( tabComponentWithTwoPanelsContainer, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER ); 
      LSContainer tabContainer = tabComponent.GetPanelAsContainer();
      tabContainer.SetContainerBackgroundColor( Color.white );
      
      // Create button to open up the old property viewer table.
      myButtonToOpenOldPropertyViewerTable = new LSButton( "Property Viewer", tabContainer, GridBagConstraints.REMAINDER, 1, this, null );

      // Single blank line.
      tabContainer.AddBlankLabelToLayoutRowRemainder1High();

      // Create text field  to change the name of the object.
      new LSLabel( "Name = ", LSLabel.RIGHT, tabContainer );
      myNameTextField = new LSTextField( super.GetOpenSimObjectName(), super.GetTextFieldWidthForOpenSimObjectName(), true, tabContainer, GridBagConstraints.REMAINDER, 1, this, this, this );
      
       // Add panel for show/hide body.
      tabContainer.AddBlankLabelToLayout1Wide1High();
      this.CreateShowHideBodyPanel( tabContainer );

      // Add show center of mass of rigid body checkbox.
      tabContainer.AddBlankLabelToLayout1Wide1High(); 
      boolean initialStateOfShowCM = BodyToggleCOMAction.IsShowCMForBody( super.GetAssociatedOpenSimObject() );
      myShowCMCheckbox = new LSCheckbox( "Show center of mass", initialStateOfShowCM, null, tabContainer, GridBagConstraints.REMAINDER, 1, this );

      // Add show body axes checkbox.
      tabContainer.AddBlankLabelToLayout1Wide1High(); 
      boolean initialStateOfShowBodyAxes = BodyToggleFrameAction.IsShowAxesForBody( super.GetAssociatedOpenSimObject() );
      myShowBodyAxesCheckbox = new LSCheckbox( "Show body axes (toggle)", initialStateOfShowBodyAxes, null, tabContainer, GridBagConstraints.REMAINDER, 1, this );

      // Add panel for wireframe or surface.
      this.CreateWireFrameOrSurfaceShadedPanel( tabContainer );

      // Add opacity slider.
      int currentValueOfOpacity = super.GetObjectOpacityFromOpenSimWithRangeFrom0To100();
      new LSLabel( "Opacity: ", LSLabel.RIGHT, tabContainer );
      myOpacitySlider = new LSSlider( LSSlider.HORIZONTAL, 0, 100, currentValueOfOpacity, 20, 5, tabContainer, GridBagConstraints.REMAINDER, 1, this );

      // Single blank line.
      tabContainer.AddBlankLabelToLayoutRowRemainder1High();

      // Create button that if invoked, opens up the color chooser.
      myButtonToOpenColorChooser = new LSButton( "Color", tabContainer, GridBagConstraints.REMAINDER, 1, this, null );

      return tabComponentWithTwoPanels;
   } 


   //-----------------------------------------------------------------------------
   private void  CreateShowHideBodyPanel( LSContainer tabContainer )
   { 
      LSPanel     panel = new LSPanel( tabContainer, GridBagConstraints.REMAINDER, 1 ); 
      LSContainer panelContainer = panel.GetPanelAsContainer();
      panelContainer.SetContainerBackgroundColor( Color.white );
           
      // Note: Entire group is set to false, because as of now, there is no way to find out how object is currently displayed. 
      CheckboxGroup checkboxGroup = new CheckboxGroup();
      myShowBodyCheckbox = new LSCheckbox( "Show body", false, checkboxGroup, panelContainer, 1, 1, this ); 
      panelContainer.AddBlankLabelToLayout1Wide1High();  
      myHideBodyCheckbox = new LSCheckbox( "Hide body", false, checkboxGroup, panelContainer, 1, 1, this );   
      panelContainer.AddBlankLabelToLayoutRowRemainder1High(); 
   }
   
   
   //-----------------------------------------------------------------------------
   private void  CreateWireFrameOrSurfaceShadedPanel( LSContainer tabContainer )
   { 
      LSPanel     panel = new LSPanel( tabContainer, GridBagConstraints.REMAINDER, 1 ); 
      LSContainer panelContainer = panel.GetPanelAsContainer();
      panelContainer.SetContainerBackgroundColor( Color.white );

      // Note: Entire group is set to false, because as of now, there is no way to find out how object is currently displayed. 
      CheckboxGroup checkboxGroup = new CheckboxGroup();
      myWireFrameCheckbox =	      new LSCheckbox( "Wireframe",     false, checkboxGroup, panelContainer, 1, 1, this ); 
      panelContainer.AddBlankLabelToLayout1Wide1High();  
      mySurfaceShadedSmoothCheckbox = new LSCheckbox( "Smooth-Shaded", false, checkboxGroup, panelContainer, GridBagConstraints.REMAINDER, 1, this );    
      // panelContainer.AddBlankLabelToLayout1Wide1High();  
      // mySurfaceShadedFlatCheckbox =   new LSCheckbox( "Flat-Shaded",   false, checkboxGroup, panelContainer, GridBagConstraints.REMAINDER, 1, this );    
      // mySurfaceShadedFlatCheckbox is unimplemented VTK option since: (a) no difference to analytical geometry, (b) not much faster than smooth, (c) Confusing to give user extra options.
   }
    

   //-----------------------------------------------------------------------------
   private Component  CreateTabComponentMassAndCenterOfMassRigidBodyProperty( ) 
   { 
      LSPanel     tabComponent = new LSPanel(); 
      LSContainer tabContainer = tabComponent.GetPanelAsContainer();
      tabContainer.SetContainerBackgroundColor( Color.white );

      // Create mass label and text field.
      LSLabel massLabel = new LSLabel( "Mass  =  ", LSLabel.RIGHT, tabContainer );
      myMassTextField = new LSTextFieldWithListenersForOpenSimDoubleNonNegative( this, super.GetPropertyTalkToSimbody(), "mass", 0.0, 0, true, tabContainer, 1, 1, this, this, this );   
      tabContainer.AddBlankLabelToLayoutRowRemainder1High();

      // Create center of mass labels and text fields.
      tabContainer.AddBlankLabelToLayoutRowRemainder1High();
      new LSLabel( "Position of Body center of mass (Bcm) from Body origin (Bo)", LSLabel.CENTER, tabContainer, GridBagConstraints.REMAINDER );
      
      double cmXYZValues[] = super.GetPropertyTalkToSimbody().GetOpenSimObjectPropertyValueAsArrayDouble3FromPropertyName( "mass_center" );
      new LSLabel( "Position  =  ", LSLabel.RIGHT, tabContainer );
      myXcmTextField  = new LSTextField( cmXYZValues[0], 0, true, tabContainer, 1, 1, this, this, this );
      new LSLabel( " * bx", LSLabel.LEFT, tabContainer, GridBagConstraints.REMAINDER );
      
      new LSLabel( "  +  ", LSLabel.RIGHT, tabContainer );
      myYcmTextField  = new LSTextField( cmXYZValues[1], 0, true, tabContainer, 1, 1, this, this, this );
      new LSLabel( " * by", LSLabel.LEFT, tabContainer, GridBagConstraints.REMAINDER );

      new LSLabel( "  +  ", LSLabel.RIGHT, tabContainer );
      myZcmTextField  = new LSTextField( cmXYZValues[2], 0, true, tabContainer, 1, 1, this, this, this );
      new LSLabel( " * bz", LSLabel.LEFT, tabContainer, GridBagConstraints.REMAINDER );

      // Add picture of a rigid body with Bcm (Body center of mass) and Bo (Body origin).
      JLabel labelWithPicture = LSJava.LSResources.LSImageResource.GetJLabelFromLSResourcesFileNameScaled( "RigidBodyWithOriginCMAndBasisVectors.jpg", 0, 140 );
      if( labelWithPicture != null )  tabContainer.AddComponentToLayoutRowRemainder1High( labelWithPicture );
      
      return tabComponent;
   } 

   //-----------------------------------------------------------------------------
   private Component  CreateTabComponentInertiaPropertiesRigidBodyProperty( ) 
   { 
      LSPanel     tabComponent = new LSPanel(); 
      LSContainer tabContainer = tabComponent.GetPanelAsContainer();
      tabContainer.SetContainerBackgroundColor( Color.white );

      // Create inertia properties labels and text fields.
      tabContainer.AddBlankLabelToLayoutRowRemainder1High();
      myInertiaIncorrectLabel = new LSLabel( "Error: INVALID Inertia matrix", LSLabel.CENTER, tabContainer, GridBagConstraints.REMAINDER );
      myInertiaIncorrectLabel.SetLabelForegroundColor( LSColor.BackgroundColorSuggestingError );
      myInertiaIncorrectLabel.SetLabelVisible( true );
 
      // Create inertia properties labels and text fields.
      tabContainer.AddBlankLabelToLayoutRowRemainder1High();
      new LSLabel( "Inertia matrix about Body center of mass (Bcm) for bx, by, bz", LSLabel.CENTER, tabContainer, GridBagConstraints.REMAINDER );
      tabContainer.AddComponentToLayoutRowRemainder1High( this.CreatePanelWithInertiaMatrix() );

      // Add picture of a rigid body with Bcm (Body center of mass) and Bo (Body origin).
      JLabel labelWithPicture = LSJava.LSResources.LSImageResource.GetJLabelFromLSResourcesFileNameScaled( "RigidBodyWithCMAndBasisVectors.jpg", 0, 140 );
      if( labelWithPicture != null )  tabContainer.AddComponentToLayoutRowRemainder1High( labelWithPicture );

      // After creating the matrix, provide visual feedback on whether or not it is possible.
      this.CheckInertiaMatrixAndGiveVisualFeedbackOnWhetherOrNotItIsPhysicallyPossible();
      
      return tabComponent;
   } 


   //-----------------------------------------------------------------------------
   private LSPanel  CreatePanelWithInertiaMatrix( ) 
   { 
      myInertiaMatrixPanel = new LSPanel(); 
      LSContainer tabContainer = myInertiaMatrixPanel.GetPanelAsContainer();

      // Extra padding (spacing) between components in inertia matrix.
      tabContainer.SetConstraintInsets( 4, 4, 4, 4 );

      // Create inertia properties labels and text fields.
      myIxxTextField = new LSTextFieldWithListenersForOpenSimDoubleNonNegative( this, super.GetPropertyTalkToSimbody(), "inertia_xx", 0.0, 0, true, tabContainer, 1, 1, this, this, this );   
      myIxyTextField = new LSTextFieldWithListenersForOpenSimDouble(            this, super.GetPropertyTalkToSimbody(), "inertia_xy", 0.0, 0, true, tabContainer, 1, 1, this, this, this );   
      myIxzTextField = new LSTextFieldWithListenersForOpenSimDouble(            this, super.GetPropertyTalkToSimbody(), "inertia_xz", 0.0, 0, true, tabContainer, GridBagConstraints.REMAINDER, 1, this, this, this );   
      myIyxTextField = new LSTextField( myIxyTextField.GetTextFieldAsString(), 0, false, tabContainer, 1, 1, null, this, null );
      myIyyTextField = new LSTextFieldWithListenersForOpenSimDoubleNonNegative( this, super.GetPropertyTalkToSimbody(), "inertia_yy", 0.0, 0, true, tabContainer, 1, 1, this, this, this );   
      myIyzTextField = new LSTextFieldWithListenersForOpenSimDouble(            this, super.GetPropertyTalkToSimbody(), "inertia_yz", 0.0, 0, true, tabContainer, GridBagConstraints.REMAINDER, 1, this, this, this );   
      myIzxTextField = new LSTextField( myIxzTextField.GetTextFieldAsString(), 0, false, tabContainer, 1, 1, null, this, null );
      myIzyTextField = new LSTextField( myIyzTextField.GetTextFieldAsString(), 0, false, tabContainer, 1, 1, null, this, null );
      myIzzTextField = new LSTextFieldWithListenersForOpenSimDoubleNonNegative( this, super.GetPropertyTalkToSimbody(), "inertia_zz", 0.0, 0, true, tabContainer, GridBagConstraints.REMAINDER, 1, this, this, this );   
      
      return myInertiaMatrixPanel;
   } 


   //----------------------------------------------------------------------------- 
   // Relevant class hierarchy: OneBodyNode -> OpenSimObjectNode -> OpenSimNode -> AbstractNode -> Node
   //----------------------------------------------------------------------------- 
   private OneBodyNode  GetAssociatedOpenSimOneBodyNodeOrNull( )
   {

      OpenSimObjectNode openSimObjectNode = super.GetAssociatedOpenSimObjectNodeOrNull();
      return (openSimObjectNode instanceof OneBodyNode) ? (OneBodyNode)openSimObjectNode : null;
   }


   //-------------------------------------------------------------------------
   private boolean  ProcessEventProductOfInertiaChanged( LSTextField eventTarget, LSTextField sameValueProductOfInertiaInertiaTextField )
   {
      Window ownerWindowOrThis = super.GetLSDialogOwnerWindowOrThis();   
      boolean isBadField = eventTarget.IssueErrorMessageIfTextFieldIsBadDoublePrecisionNumber( ownerWindowOrThis );
      sameValueProductOfInertiaInertiaTextField.SetTextFieldFromString( eventTarget.GetTextFieldAsString() );
      this.CheckInertiaMatrixAndGiveVisualFeedbackOnWhetherOrNotItIsPhysicallyPossible();
      return isBadField;
   }


   //-------------------------------------------------------------------------
   private double[]  GetInertiaMatrixIfAllFieldsAreValidDoublesWithMomentsBeingNonNegativeOtherwiseNull( )
   { 
      if( myIxxTextField.IsTextFieldValidDoubleNonNegative() && 
          myIyyTextField.IsTextFieldValidDoubleNonNegative() && 
          myIzzTextField.IsTextFieldValidDoubleNonNegative() && 
       	  myIxyTextField.IsTextFieldValidDouble() &&
       	  myIxzTextField.IsTextFieldValidDouble() &&
       	  myIyzTextField.IsTextFieldValidDouble() )
      {
         double Ixx = myIxxTextField.GetTextFieldAsDouble(); 
         double Iyy = myIyyTextField.GetTextFieldAsDouble(); 
         double Izz = myIzzTextField.GetTextFieldAsDouble(); 
         double Ixy = myIxyTextField.GetTextFieldAsDouble(); 
         double Ixz = myIxzTextField.GetTextFieldAsDouble(); 
         double Iyz = myIyzTextField.GetTextFieldAsDouble();
         double retValue[] = { Ixx, Iyy, Izz, Ixy, Ixz, Iyz };
         return retValue;
      }
      return null;
   }


   //-------------------------------------------------------------------------
   private boolean  IsValidInertiaMomentsTriangleInequality( double Ixx, double Iyy, double Izz )
   {
      double momentOfInertiaSum = Ixx + Iyy + Izz;
      double epsilonMomentOfInertia = 1.0E-7 * momentOfInertiaSum; 
      return ( Ixx <= Iyy + Izz + epsilonMomentOfInertia ) 
          && ( Iyy <= Ixx + Izz + epsilonMomentOfInertia )
          && ( Izz <= Ixx + Iyy + epsilonMomentOfInertia );
   }
   
   //-------------------------------------------------------------------------
   private boolean  IsValidInertiaProductsSizedComparableToMomentsOfInertia( double Ixx, double Iyy, double Izz, double Ixy, double Ixz, double Iyz, boolean[] isValidIxyIxzIyz )
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
      
      return IyzOK && IxzOK && IxyOK;
   } 
   
   //-------------------------------------------------------------------------
   private boolean  CheckInertiaMatrixAndGiveVisualFeedbackOnWhetherOrNotItIsPhysicallyPossible( )
   {
      // Get all the numbers in the inertia matrix - or null if there is an error in one of the fields. 
      double inertiaArray[] = this.GetInertiaMatrixIfAllFieldsAreValidDoublesWithMomentsBeingNonNegativeOtherwiseNull();
          
      // Set the moment of inertia textfield background colors to suggest errors if triangle inequality is invalid.
      boolean isValidMomentsOfInertiaTriangleEquality = inertiaArray != null && this.IsValidInertiaMomentsTriangleInequality( inertiaArray[0], inertiaArray[1], inertiaArray[2] );
      myIxxTextField.SetTextFieldBackgroundColorSuggestingErrorTrueOrOKFalse( !isValidMomentsOfInertiaTriangleEquality );
      myIyyTextField.SetTextFieldBackgroundColorSuggestingErrorTrueOrOKFalse( !isValidMomentsOfInertiaTriangleEquality );
      myIzzTextField.SetTextFieldBackgroundColorSuggestingErrorTrueOrOKFalse( !isValidMomentsOfInertiaTriangleEquality );

      // Set the product of inertia textfield background colors to suggest errors if triangle inequality is invalid.
      boolean[] isValidIxyIxzIyz = { true, true, true };
      boolean isValidInertiaMatrix = isValidMomentsOfInertiaTriangleEquality && this.IsValidInertiaProductsSizedComparableToMomentsOfInertia( inertiaArray[0], inertiaArray[1], inertiaArray[2], inertiaArray[3], inertiaArray[4], inertiaArray[5], isValidIxyIxzIyz );
      myIxyTextField.SetTextFieldBackgroundColorSuggestingErrorTrueOrOKFalse( !isValidIxyIxzIyz[0] );
      myIxzTextField.SetTextFieldBackgroundColorSuggestingErrorTrueOrOKFalse( !isValidIxyIxzIyz[1] );
      myIyzTextField.SetTextFieldBackgroundColorSuggestingErrorTrueOrOKFalse( !isValidIxyIxzIyz[2] );
      
      // Possibly show the label that announced the inertia matrix is invalid.
      myInertiaMatrixPanel.GetPanelAsContainer().SetContainerBackgroundColor( isValidInertiaMatrix ? LSColor.BackgroundColorSuggestingOK : LSColor.BackgroundColorSuggestingError );    
      myInertiaIncorrectLabel.SetLabelVisible( !isValidInertiaMatrix );
      
      this.GetDialogAsContainer().Repaint();
      return isValidInertiaMatrix;
   }


   //-----------------------------------------------------------------------------
   // Class data
   
   // Items appearing on the appearance tab.
   private LSButton       myButtonToOpenOldPropertyViewerTable;
   private LSTextField    myNameTextField; 
   private LSCheckbox     myShowBodyCheckbox, myHideBodyCheckbox;
   private LSCheckbox     myShowCMCheckbox;
   private LSCheckbox     myShowBodyAxesCheckbox;
   private LSButton       myButtonToOpenColorChooser;
   private LSSlider       myOpacitySlider;
   private LSCheckbox     myWireFrameCheckbox;
   private LSCheckbox     mySurfaceShadedSmoothCheckbox;
   private LSCheckbox     mySurfaceShadedFlatCheckbox;  
   // mySurfaceShadedFlatCheckbox is unimplemented VTK option since: (a) no difference to analytical geometry, (b) not much faster than smooth, (c) Confusing to give user extra options.
   
   // Mass and center of mass fields.
   private LSTextField    myXcmTextField,  myYcmTextField,  myZcmTextField;   
   
   // Mass and inertia matrix fields.
   private LSTextFieldWithListenersForOpenSimDoubleNonNegative  myMassTextField;  
   private LSTextFieldWithListenersForOpenSimDoubleNonNegative  myIxxTextField;
   private LSTextFieldWithListenersForOpenSimDoubleNonNegative  myIyyTextField;
   private LSTextFieldWithListenersForOpenSimDoubleNonNegative  myIzzTextField;
   private LSTextFieldWithListenersForOpenSimDouble             myIxyTextField;
   private LSTextFieldWithListenersForOpenSimDouble             myIxzTextField;
   private LSTextFieldWithListenersForOpenSimDouble             myIyzTextField;
   private LSTextField                                          myIyxTextField;
   private LSTextField                                          myIzxTextField;
   private LSTextField                                          myIzyTextField;
   private LSPanel  myInertiaMatrixPanel; 
   private LSLabel  myInertiaIncorrectLabel;
   
   // Determine which LSTextField last had focus to determine if the one that regains focus is the same one.
   private LSTextField    myPreviousTextFieldThatHadFocus;

   //-----------------------------------------------------------------------------
   // When this property editor is re-displayed, show tab that was last viewed by the user.
   protected int   GetPriorUserSelectedTabbedPaneIndexVirtualFunction()  { return myPriorUserSelectedTabIndex; }   
   protected void  SetPriorUserSelectedTabbedPaneIndexVirtualFunction()  { myPriorUserSelectedTabIndex = super.GetSelectedTabbedPaneIndex(); } 
   private static int   myPriorUserSelectedTabIndex = 0;   
}



