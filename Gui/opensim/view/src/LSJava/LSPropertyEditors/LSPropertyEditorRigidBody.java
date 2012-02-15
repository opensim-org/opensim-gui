//-----------------------------------------------------------------------------
// File:     LSPropertyEditorRigidBody.java
// Parents:  LSPropertyEditorTabbedAbstract -> LSDialog -> Dialog -> Window -> Container -> Component -> Object
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
import  org.opensim.view.BodyDisplayer;
import  org.opensim.view.nodes.OpenSimObjectNode;
import  org.opensim.view.nodes.OneBodyNode;
import  org.opensim.view.nodes.BodyToggleCOMAction;
import  org.opensim.view.BodyToggleFrameAction;


//-----------------------------------------------------------------------------
public class LSPropertyEditorRigidBody extends LSPropertyEditorTabbedAbstract implements ActionListener, FocusListener, ItemListener, KeyListener 
{ 
   // Quasi constructor ----------------------------------------------------------
   public static LSPropertyEditorRigidBody  NewLSPropertyEditorRigidBody( OneBodyNode oneBodyNodePassedToConstructor, ModelWindowVTKTopComponent ownerWindowPassedToConstructor )
   {
     return LSPropertyEditorRigidBody.NewLSPropertyEditorRigidBody( oneBodyNodePassedToConstructor.getOpenSimObject(), oneBodyNodePassedToConstructor, ownerWindowPassedToConstructor ); 
   }  

   // Quasi constructor ----------------------------------------------------------
   public static LSPropertyEditorRigidBody  NewLSPropertyEditorRigidBody( OpenSimObject openSimObjectToConstructorShouldNotBeNull, OneBodyNode oneBodyNodeToConstructorMayBeNull, ModelWindowVTKTopComponent ownerWindowPassedToConstructor  )  
   {
      LSPropertyEditorTabbedAbstract propertyEditorAlreadyExists = LSPropertyEditorTabbedAbstract.IfIsExistingPropertyEditorForOpenSimObjectPushToFront( openSimObjectToConstructorShouldNotBeNull, oneBodyNodeToConstructorMayBeNull ); 
      if( propertyEditorAlreadyExists != null )  return (LSPropertyEditorRigidBody)propertyEditorAlreadyExists;
      return new LSPropertyEditorRigidBody( openSimObjectToConstructorShouldNotBeNull, oneBodyNodeToConstructorMayBeNull, ownerWindowPassedToConstructor );
   }  


   // Constructor ----------------------------------------------------------------
   private  LSPropertyEditorRigidBody( OpenSimObject openSimObjectToConstructorShouldNotBeNull, OneBodyNode oneBodyNodeToConstructorMayBeNull, ModelWindowVTKTopComponent ownerWindowPassedToConstructor  )  
   { 
      super( openSimObjectToConstructorShouldNotBeNull, oneBodyNodeToConstructorMayBeNull, ownerWindowPassedToConstructor, "BodyCMPicture.png", "Rigid Body", 560, 400 );  

      // Add panels, display the window, choose the previously selected panel to display. 
      this.AddPanelsToFrame(); 
      this.GetDialogAsContainer().PackLocateShow();
      super.SetSelectedTabbedPaneFromPriorUserSelection();
   } 
   
   //-------------------------------------------------------------------------
   public void  actionPerformed( ActionEvent actionEvent )  { Object eventTarget = actionEvent.getSource();  this.CheckActionEventTarget(eventTarget);      }
   public void  focusLost(   FocusEvent focusEvent )        { Object eventTarget =  focusEvent.getSource();  this.CheckFocusLostEventTarget(eventTarget);   }
   public void  focusGained( FocusEvent focusEvent )        { Object eventTarget =  focusEvent.getSource();  this.CheckFocusGainedEventTarget(eventTarget); } 

   //-------------------------------------------------------------------------
   public void  itemStateChanged( ItemEvent itemEvent )     
   {
      Object eventTarget = itemEvent.getSource();
      if(      eventTarget == myShowBodyCheckbox )            this.ProcessEventHideOrShow( true,  false, false, false ); // myShowBodyCheckbox.GetCheckboxState();
      else if( eventTarget == myHideBodyCheckbox )            this.ProcessEventHideOrShow( false, false, false, false );  // myHideBodyCheckbox.GetCheckboxState();
      else if( eventTarget == myShowCMCheckboxA ) 	      this.ProcessEventShowCMOrShowBodyAxes( eventTarget );
      else if( eventTarget == myShowCMCheckboxB ) 	      this.ProcessEventShowCMOrShowBodyAxes( eventTarget );
      else if( eventTarget == myShowBodyAxesCheckbox )	      this.ProcessEventShowCMOrShowBodyAxes( eventTarget );
      else if( eventTarget == myWireFrameCheckbox ) 	      { super.SetObjectRepresentationPointsWireFrameOrSurfaceAndSurfaceShading( 1, 0 );  this.ProcessEventHideOrShow(true,false,false,false); }
      else if( eventTarget == mySurfaceShadedSmoothCheckbox ) { super.SetObjectRepresentationPointsWireFrameOrSurfaceAndSurfaceShading( 2, 1 );  this.ProcessEventHideOrShow(true,false,false,false); }
   // else if( eventTarget == mySurfaceShadedFlatCheckbox )   { super.SetObjectRepresentationPointsWireFrameOrSurfaceAndSurfaceShading( 2, 0 );  this.ProcessEventHideOrShow(true,false,false,false); }
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
      if( eventTarget == myOpacitySlider ) { if( !myOpacitySlider.getValueIsAdjusting() )  super.SetObjectOpacityWithIntegerFrom0To100( myOpacitySlider.GetSliderValueAsInteger() ); } 
   }


   //-------------------------------------------------------------------------
   private void  SynchronizeShowCenterOfMassCheckboxes( Object eventTarget )
   {
      boolean isShowing = eventTarget == myShowCMCheckboxA ? myShowCMCheckboxA.GetCheckboxState() : myShowCMCheckboxB.GetCheckboxState();
      myShowCMCheckboxA.SetCheckboxState( isShowing );
      myShowCMCheckboxB.SetCheckboxState( isShowing );
   }


   //-------------------------------------------------------------------------
   private void  ProcessEventShowCMOrShowBodyAxes( Object eventTarget )
   {
      boolean isShow = false;

      // Ensure checkboxes for showing the center of mass stay synchronized.
      if( eventTarget == myShowCMCheckboxB )
      {
         this.SynchronizeShowCenterOfMassCheckboxes( eventTarget );
	 eventTarget = myShowCMCheckboxA;
      } 

      if( eventTarget == myShowCMCheckboxA ) 
      {	      
         this.SynchronizeShowCenterOfMassCheckboxes( eventTarget );
	 isShow = myShowCMCheckboxA.GetCheckboxState();
         BodyToggleCOMAction.ShowCMForOneBodyNode( this.GetAssociatedOpenSimOneBodyNodeOrNull(), isShow, true );
      }
      else if( eventTarget == myShowBodyAxesCheckbox )	
      {      
	 isShow = myShowBodyAxesCheckbox.GetCheckboxState();
         BodyToggleFrameAction.ShowAxesForBody( super.GetAssociatedOpenSimObject(), isShow, true );
      }

      // May have to do special things in order to see sub-geometry (considered a bug in the way OpenSim uses VTK now).
      if( isShow ) this.ProcessEventHideOrShow( isShow, true, false, false );
   }


   //-------------------------------------------------------------------------
   public void  ProcessEventChangedCMPositionEvent( )
   {
      if( myShowCMCheckboxA.GetCheckboxState() )
      {
         BodyDisplayer rep = BodyToggleFrameAction.GetBodyDisplayerForBody( super.GetAssociatedOpenSimObject() );
         if( rep != null ) rep.SetCMLocationFromPropertyTable( true );
         super.RepaintAllOpenGL();
      }
   } 


   //-------------------------------------------------------------------------
   public void  ProcessEventHideOrShow( boolean isShow, boolean userClickedShowCMOrBodyAxes, boolean userChangedOpacityValue, boolean userChangedColor )
   {
      // Show object and any sub-object geometry.
      super.ShowOrHideObject( isShow );

      // Determine low and high value of opacity if showing CM or BodyAxes.
      int currentValueOfOpacitySlider0To100 = myOpacitySlider.GetSliderValueAsInteger();
      int  lowValueOfOpacityIfShowCMOrAxes = (currentValueOfOpacitySlider0To100 < 30 || currentValueOfOpacitySlider0To100 > 70) ? 30 : currentValueOfOpacitySlider0To100;
      int highValueOfOpacityIfShowCMOrAxes = (currentValueOfOpacitySlider0To100 < 30 || currentValueOfOpacitySlider0To100 > 70) ? 70 : currentValueOfOpacitySlider0To100;

      // If body is hidden, it must be shown so user can see any other geometry (considered a bug in the way OpenSim uses VTK now).
      // If body is shown,  it must be made a little transparent to see other geometry.
      boolean checkBoxOnForCMOrBodyAxes = myShowCMCheckboxA.GetCheckboxState() || myShowBodyAxesCheckbox.GetCheckboxState();
      boolean isShowCMOrShowBodyAxes = (isShow==false && userClickedShowCMOrBodyAxes==false) ? false : checkBoxOnForCMOrBodyAxes;
      int opacityValue = isShow ? (isShowCMOrShowBodyAxes ? highValueOfOpacityIfShowCMOrAxes : 100) : 
                                  (isShowCMOrShowBodyAxes ? lowValueOfOpacityIfShowCMOrAxes  : 0);

      // If user just changed color and opacity is nonzero, leave opacity alone (at the current slider value).
      if( userChangedColor && currentValueOfOpacitySlider0To100 != 0 ) opacityValue = currentValueOfOpacitySlider0To100;

      // If user just changed opacity, just leave it alone.
      // Otherwise, change the slider which should fire an event in super class and change object's opacity.
      // If event does not occur, use  super.SetObjectOpacityWithIntegerFrom0To100( opacityValue );
      if( userChangedOpacityValue == false ) myOpacitySlider.SetSliderValueFromInteger( opacityValue );
      currentValueOfOpacitySlider0To100 = myOpacitySlider.GetSliderValueAsInteger();
      
      // If user clicked ShowCMOrBodyAxes==true, then uncheck the Hide checkbox.
      // If user clicked wireframe or surface,   then uncheck the Hide checkbox.
      // If user increased opacity,              then uncheck the Hide checkbox.
      // If user changed the color,              then uncheck the Hide checkbox.
      int objectHidden0Shown1Mixed2 = this.IsObjectCurrentlyHidden0Shown1Mixed2();
      if( (objectHidden0Shown1Mixed2 > 0) || userClickedShowCMOrBodyAxes || userChangedColor ) // || isShow
      {
         myShowBodyCheckbox.SetCheckboxState( true  );  // True must be done first or else Java ignores the next statement.
         myHideBodyCheckbox.SetCheckboxState( false );
      }
      else if( objectHidden0Shown1Mixed2==0 || currentValueOfOpacitySlider0To100==0 )
      { 
         myHideBodyCheckbox.SetCheckboxState( true  );	// True must be done first or else Java ignores the next statement.
         myShowBodyCheckbox.SetCheckboxState( false );  
      }
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
      else if( eventTarget==myXcmTextField || eventTarget==myYcmTextField || eventTarget==myZcmTextField ) this.ProcessEventChangedCMPositionEvent(); 
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
      
      // Add show center of mass of rigid body checkbox.
      tabContainer.AddBlankLabelToLayout1Wide1High(); 
      boolean initialStateOfShowCM = BodyToggleCOMAction.IsShowCMForBody( super.GetAssociatedOpenSimObject() );
      myShowCMCheckboxA = new LSCheckbox( "Show center of mass", initialStateOfShowCM, null, tabContainer, 2, 1, this );

      // Add picture of center of mass.
      JLabel labelWithPictureOfCM = LSImageResource.GetJLabelFromLSResourcesFileNameScaled( "BodyCMPictureGreenSphere.png", 0, 30 );
      tabContainer.AddComponentToLayoutRowRemainder1High( labelWithPictureOfCM );

      // Add show body axes checkbox.
      tabContainer.AddBlankLabelToLayout1Wide1High(); 
      boolean initialStateOfShowBodyAxes = BodyToggleFrameAction.IsShowAxesForBody( super.GetAssociatedOpenSimObject() );
      myShowBodyAxesCheckbox = new LSCheckbox( "Show body axes", initialStateOfShowBodyAxes, null, tabContainer, 2, 1, this );
      
      // Add picture of XYZ Basis Vectors to right of checkbox.
      JLabel labelWithPictureOfXYZBasisVectors = LSImageResource.GetJLabelFromLSResourcesFileNameScaled( "XRedYYellowZGreenBasisVectors.png", 0, 40 );
      tabContainer.AddComponentToLayoutRowRemainder1High( labelWithPictureOfXYZBasisVectors );

      // Maybe add show muscle wrapping surfaces checkbox.
      boolean oneOrMoreMuscleWrappingSurfacesExistsForThisBody = true;
      if( oneOrMoreMuscleWrappingSurfacesExistsForThisBody )
      {
         tabContainer.AddBlankLabelToLayout1Wide1High(); 
         boolean initialStateOfShowMuscleWrappingSurfaces = false; // BodyToggleFrameAction.IsShowAxesForBody( super.GetAssociatedOpenSimObject() );
         myShowMuscleWrappingSurfacesCheckbox = new LSCheckbox( "Show muscle-wrapping surfaces", initialStateOfShowMuscleWrappingSurfaces, null, tabContainer, 2, 1, this );
           
         // Add picture of muscle-wrapping surface - if any exist for this rigid body.
         JLabel labelWithPictureOfMuscleWrappingSurfaces = LSImageResource.GetJLabelFromLSResourcesFileNameScaled( "MuscleWrapSurface.png", 0, 30 );
         tabContainer.AddComponentToLayoutRowRemainder1High( labelWithPictureOfMuscleWrappingSurfaces );
      }
      
      // Add panel for wireframe/surface hide/show and translucent/opaque slider.
      this.CreatePanelTranslucentHideToOpaqueShow( tabContainer );

      // Single blank line.
      tabContainer.AddBlankLabelToLayoutRowRemainder1High();

      // Create button that if invoked, opens up the color chooser.
      myButtonToOpenColorChooser = new LSButton( "Color", tabContainer, GridBagConstraints.REMAINDER, 1, this, null );

      return tabComponentWithTwoPanels;
   } 


   //-----------------------------------------------------------------------------
   private void  CreatePanelTranslucentHideToOpaqueShow( LSContainer addNewPanelToThisContainer ) 
   { 
      LSPanel     panel = new LSPanel( addNewPanelToThisContainer, GridBagConstraints.REMAINDER, 1 ); 
      LSContainer panelContainer = panel.GetPanelAsContainer();
      panelContainer.SetContainerBackgroundColor( Color.white );

      // Add panel for wireframe or surface.
      this.CreateWireFrameOrSurfaceShadedPanel( panelContainer );

      // Add CheckboxGroup and panel for hide body.
      CheckboxGroup checkboxGroupForShowHide = new CheckboxGroup();
      this.CreateHideOrShowBodyPanels( panelContainer, checkboxGroupForShowHide, true );

      // Add opacity slider.
      int currentValueOfOpacity = super.GetObjectOpacityFromOpenSimWithRangeFrom0To100();
      myOpacitySlider = new LSSlider( LSSlider.HORIZONTAL, 0, 100, currentValueOfOpacity, 20, 5, panelContainer, 1, 1, this );

      // Add panel for show body.
      this.CreateHideOrShowBodyPanels( panelContainer, checkboxGroupForShowHide, false );
   } 


   //-----------------------------------------------------------------------------
   private void  CreateHideOrShowBodyPanels( LSContainer addNewPanelToThisContainer, CheckboxGroup checkboxGroupForShowHide, boolean isHidePanel )
   { 
      LSPanel     panel = new LSPanel( addNewPanelToThisContainer, (isHidePanel ? 1 : GridBagConstraints.REMAINDER), 1 ); 
      LSContainer panelContainer = panel.GetPanelAsContainer();
      panelContainer.SetContainerBackgroundColor( Color.white );
           
      // Note: Entire group is set to false, because as of now, there is no way to find out how object is currently displayed. 
      if( isHidePanel )
      {
         panelContainer.AddLabelToLayoutRowRemainder1High( "Translucent ", LSLabel.RIGHT );
         myHideBodyCheckbox = new LSCheckbox( "Hide", false, checkboxGroupForShowHide, panelContainer, GridBagConstraints.REMAINDER, 1, this );   
      }
      else
      {
         panelContainer.AddLabelToLayoutRowRemainder1High( " Opaque", LSLabel.LEFT );
         panelContainer.AddLabelToLayout1Wide1High( " ", LSLabel.LEFT );
         myShowBodyCheckbox = new LSCheckbox( "Show", false, checkboxGroupForShowHide, panelContainer, GridBagConstraints.REMAINDER, 1, this ); 
      }
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
      myMassTextField = new LSTextFieldWithListenersForOpenSimDoubleNonNegative( this, super.GetPropertyTalkToSimbody(), "mass", 0, true, tabContainer, 1, 1, this, this, this );   
      tabContainer.AddBlankLabelToLayoutRowRemainder1High();

      // Create center of mass labels and text fields.
      tabContainer.AddBlankLabelToLayoutRowRemainder1High();
      new LSLabel( "Position of Body center of mass (Bcm) from Body origin (Bo)", LSLabel.CENTER, tabContainer, GridBagConstraints.REMAINDER );
      
      new LSLabel( "Position  =  ", LSLabel.RIGHT, tabContainer );
      myXcmTextField  = new LSTextFieldWithListenersForOpenSimArrayDouble( 0, this, super.GetPropertyTalkToSimbody(), "mass_center", 0, true, tabContainer, 1, 1, this, this, this );   
      new LSLabel( " * bx", LSLabel.LEFT, tabContainer, GridBagConstraints.REMAINDER );
      
      new LSLabel( "  +  ", LSLabel.RIGHT, tabContainer );
      myYcmTextField  = new LSTextFieldWithListenersForOpenSimArrayDouble( 1, this, super.GetPropertyTalkToSimbody(), "mass_center", 0, true, tabContainer, 1, 1, this, this, this );   
      new LSLabel( " * by", LSLabel.LEFT, tabContainer, GridBagConstraints.REMAINDER );

      new LSLabel( "  +  ", LSLabel.RIGHT, tabContainer );
      myZcmTextField  = new LSTextFieldWithListenersForOpenSimArrayDouble( 2, this, super.GetPropertyTalkToSimbody(), "mass_center", 0, true, tabContainer, 1, 1, this, this, this );   
      new LSLabel( " * bz", LSLabel.LEFT, tabContainer, GridBagConstraints.REMAINDER );

      // Add show center of mass checkbox.
      tabContainer.AddBlankLabelToLayout1Wide1High(); 
      boolean initialStateOfShowCM = BodyToggleCOMAction.IsShowCMForBody( super.GetAssociatedOpenSimObject() );
      myShowCMCheckboxB = new LSCheckbox( "Show center of mass", initialStateOfShowCM, null, tabContainer, GridBagConstraints.REMAINDER, 1, this );


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
      myIxxTextField = new LSTextFieldWithListenersForOpenSimDoubleNonNegative( this, super.GetPropertyTalkToSimbody(), "inertia_xx", 0, true, tabContainer, 1, 1, this, this, this );   
      myIxyTextField = new LSTextFieldWithListenersForOpenSimDouble(            this, super.GetPropertyTalkToSimbody(), "inertia_xy", 0, true, tabContainer, 1, 1, this, this, this );   
      myIxzTextField = new LSTextFieldWithListenersForOpenSimDouble(            this, super.GetPropertyTalkToSimbody(), "inertia_xz", 0, true, tabContainer, GridBagConstraints.REMAINDER, 1, this, this, this );   
      myIyxTextField = new LSTextField( myIxyTextField.GetTextFieldAsString(), 0, false, tabContainer, 1, 1, null, this, null );
      myIyyTextField = new LSTextFieldWithListenersForOpenSimDoubleNonNegative( this, super.GetPropertyTalkToSimbody(), "inertia_yy", 0, true, tabContainer, 1, 1, this, this, this );   
      myIyzTextField = new LSTextFieldWithListenersForOpenSimDouble(            this, super.GetPropertyTalkToSimbody(), "inertia_yz", 0, true, tabContainer, GridBagConstraints.REMAINDER, 1, this, this, this );   
      myIzxTextField = new LSTextField( myIxzTextField.GetTextFieldAsString(), 0, false, tabContainer, 1, 1, null, this, null );
      myIzyTextField = new LSTextField( myIyzTextField.GetTextFieldAsString(), 0, false, tabContainer, 1, 1, null, this, null );
      myIzzTextField = new LSTextFieldWithListenersForOpenSimDoubleNonNegative( this, super.GetPropertyTalkToSimbody(), "inertia_zz", 0, true, tabContainer, GridBagConstraints.REMAINDER, 1, this, this, this );   
      
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
   private LSCheckbox     myShowCMCheckboxA;	   // Checkbox on appearance tab
   private LSCheckbox     myShowCMCheckboxB;	   // Checkbox on mass and center of mass tab
   private LSCheckbox     myShowBodyAxesCheckbox;
   private LSCheckbox     myShowMuscleWrappingSurfacesCheckbox;
   private LSButton       myButtonToOpenColorChooser;
   private LSSlider       myOpacitySlider;
   private LSCheckbox     myWireFrameCheckbox;
   private LSCheckbox     mySurfaceShadedSmoothCheckbox;
   private LSCheckbox     mySurfaceShadedFlatCheckbox;  
   // mySurfaceShadedFlatCheckbox is unimplemented VTK option since: (a) no difference to analytical geometry, (b) not much faster than smooth, (c) Confusing to give user extra options.
   
   // Center of mass fields.
   private LSTextFieldWithListenersForOpenSimArrayDouble  myXcmTextField,  myYcmTextField,  myZcmTextField;   
   
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



