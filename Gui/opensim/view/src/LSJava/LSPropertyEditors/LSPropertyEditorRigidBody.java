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
// ActionListener listens for ENTER key.  
// FocusListener  listens for TAB key or mouse event to different field.
// KeyListener    listens for OK or ESCAPE key.
// ItemListener   listens for clicks on checkboxes, etc.
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
      super( openSimObjectToConstructorShouldNotBeNull, oneBodyNodeToConstructorMayBeNull, ownerWindowPassedToConstructor, "BodyCMPicture.png", "Rigid Body", 560, 380 );  
      this.AddPanelsToFrame(); 
      this.GetDialogAsContainer().PackLocateShow();
      super.SetSelectedTabbedPaneFromPriorUserSelection();
   } 


   //-------------------------------------------------------------------------
   public void  actionPerformed( ActionEvent actionEvent )  { Object eventTarget = actionEvent.getSource();  this.CheckActionOrFocusLostOrKeyEventTarget(eventTarget); }
   public void  focusLost(   FocusEvent focusEvent )        { Object eventTarget =  focusEvent.getSource();  this.CheckActionOrFocusLostOrKeyEventTarget(eventTarget); }
   public void  focusGained( FocusEvent focusEvent )        { ; } 

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
   public void  itemStateChanged( ItemEvent itemEvent )     
   {
      Object eventTarget = itemEvent.getSource();
      if(      eventTarget == myShowBodyCheckbox )            this.ProcessEventHideOrShow( true,  false, false, false ); // myShowBodyCheckbox.GetCheckboxState();
      else if( eventTarget == myHideBodyCheckbox )            this.ProcessEventHideOrShow( false, false, false, false );  // myHideBodyCheckbox.GetCheckboxState();
      else if( eventTarget == myShowCMCheckboxA ) 	      this.ProcessEventShowCMOrShowBodyAxes( eventTarget );
      else if( eventTarget == myShowBodyAxesCheckbox )	      this.ProcessEventShowCMOrShowBodyAxes( eventTarget );
      else if( eventTarget == myWireFrameCheckbox ) 	      { super.SetObjectRepresentationPointsWireFrameOrSurfaceAndSurfaceShading( 1, 0 );  this.ProcessEventHideOrShow(true,false,false,false); }
      else if( eventTarget == mySurfaceShadedSmoothCheckbox ) { super.SetObjectRepresentationPointsWireFrameOrSurfaceAndSurfaceShading( 2, 1 );  this.ProcessEventHideOrShow(true,false,false,false); }
   // else if( eventTarget == mySurfaceShadedFlatCheckbox )   { super.SetObjectRepresentationPointsWireFrameOrSurfaceAndSurfaceShading( 2, 0 );  this.ProcessEventHideOrShow(true,false,false,false); }
   }

   //-------------------------------------------------------------------------
   public void  stateChanged( ChangeEvent changeEvent ) 
   {
      Object eventTarget = changeEvent.getSource();
      if( eventTarget == myOpacitySlider ) { if( !myOpacitySlider.getValueIsAdjusting() )  super.SetObjectOpacityWithIntegerFrom0To100( myOpacitySlider.GetSliderValueAsInteger() ); } 
   }


   //-------------------------------------------------------------------------
   private void  CheckActionOrFocusLostOrKeyEventTarget( Object eventTarget )
   {
      if(      eventTarget == myButtonToOpenOldPropertyViewerTable ) super.ProcessEventClickButtonToOpenOldPropertyViewerTable();
      else if( eventTarget == myButtonToOpenColorChooser )           super.CreateColorDialogBoxToChangeUserSelectedColor();
      else if( eventTarget == myNameTextField )                      super.SetOpenSimObjectNameAndPropertyEditorDialogTitle( myNameTextField.GetTextFieldAsString() );
   }
   

   //-------------------------------------------------------------------------
   public void  ProcessEventShowCMOrShowBodyAxes( Object eventTarget )
   {
      boolean isShow = false;

      // Ensure checkboxes for showing the center of mass stay synchronized.
      LSCheckbox showCMCheckboxB = myMassAndCMPanel.GetShowCMCheckboxB();
      if( eventTarget == showCMCheckboxB )
      {
         boolean checkBoxStateB = showCMCheckboxB.GetCheckboxState();
         myShowCMCheckboxA.SetCheckboxState( checkBoxStateB );
	 eventTarget = myShowCMCheckboxA;
      } 

      // Do not use an else statement here.
      if( eventTarget == myShowCMCheckboxA ) 
      {	      
	 isShow = myShowCMCheckboxA.GetCheckboxState();
	 showCMCheckboxB.SetCheckboxState( isShow );
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
                                  (isShowCMOrShowBodyAxes ? lowValueOfOpacityIfShowCMOrAxes  : currentValueOfOpacitySlider0To100);

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
      else if( objectHidden0Shown1Mixed2 == 0 ) // || currentValueOfOpacitySlider0To100==0 )
      { 
         myHideBodyCheckbox.SetCheckboxState( true  );	// True must be done first or else Java ignores the next statement.
         myShowBodyCheckbox.SetCheckboxState( false );  
      }
   }


   //-----------------------------------------------------------------------------
   private void  AddPanelsToFrame() 
   { 
      super.AddTabToPropertyEditor( "Appearance",               null,  this.CreateTabComponentAppearanceRigidBodyProperty(),             null );

      myMassAndCMPanel = new LSPanelRigidBodyMassAndCMForOpenSim( this );
      super.AddTabToPropertyEditor( "Mass and Center of Mass",  null,  myMassAndCMPanel,     null );

      myInertiaMatrixPanel = new LSPanelRigidBodyInertiaMatrixForOpenSim( this );
      super.AddTabToPropertyEditor( "Inertia Properties",       null,  myInertiaMatrixPanel, null );
        
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
      // Add extra spacing between components.
      LSPanel tabComponent = new LSPanel( tabComponentWithTwoPanelsContainer, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER ); 
      LSContainer tabContainer = tabComponent.GetPanelAsContainer();
      tabContainer.SetContainerBackgroundColor( Color.white );
      tabContainer.SetConstraintInsets( 3, 0, 3, 0 );

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
      myShowBodyAxesCheckbox = new LSCheckbox( "Show rigid body axes", initialStateOfShowBodyAxes, null, tabContainer, 2, 1, this );
      
      // Add picture of XYZ Basis Vectors to right of checkbox.
      JLabel labelWithPictureOfXYZBasisVectors = LSImageResource.GetJLabelFromLSResourcesFileNameScaled( "XRedYYellowZGreenBasisVectors.png", 0, 40 );
      tabContainer.AddComponentToLayoutRowRemainder1High( labelWithPictureOfXYZBasisVectors );

      // Maybe add show muscle wrapping surfaces checkbox.
      int numberOfMuscleWrappingSurfacesForThisBody = OneBodyNode.GetNumberOfWrapObjectsForBody( super.GetAssociatedOpenSimObject() );
      if( numberOfMuscleWrappingSurfacesForThisBody > 0 )
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
   // Relevant class hierarchy: OneBodyNode -> OpenSimObjectNode -> OpenSimNode -> AbstractNode -> Node
   //----------------------------------------------------------------------------- 
   private OneBodyNode  GetAssociatedOpenSimOneBodyNodeOrNull( )
   {

      OpenSimObjectNode openSimObjectNode = super.GetAssociatedOpenSimObjectNodeOrNull();
      return (openSimObjectNode instanceof OneBodyNode) ? (OneBodyNode)openSimObjectNode : null;
   }



   //-----------------------------------------------------------------------------
   // Class data
   
   // Items appearing on the appearance tab.
   private LSButton       myButtonToOpenOldPropertyViewerTable;
   private LSTextField    myNameTextField; 
   private LSCheckbox     myShowBodyCheckbox, myHideBodyCheckbox;
   private LSCheckbox     myShowCMCheckboxA;	   // Checkbox on appearance tab
   private LSCheckbox     myShowBodyAxesCheckbox;
   private LSCheckbox     myShowMuscleWrappingSurfacesCheckbox;
   private LSButton       myButtonToOpenColorChooser;
   private LSSlider       myOpacitySlider;
   private LSCheckbox     myWireFrameCheckbox;
   private LSCheckbox     mySurfaceShadedSmoothCheckbox;
   private LSCheckbox     mySurfaceShadedFlatCheckbox;  
   // mySurfaceShadedFlatCheckbox is unimplemented VTK option since: (a) no difference to analytical geometry, (b) not much faster than smooth, (c) Confusing to give user extra options.
   
   // Mass/CM and inertia matrix panels.
   private LSPanelRigidBodyMassAndCMForOpenSim      myMassAndCMPanel;
   private LSPanelRigidBodyInertiaMatrixForOpenSim  myInertiaMatrixPanel;
   
   //-----------------------------------------------------------------------------
   // When this property editor is re-displayed, show tab that was last viewed by the user.
   protected int   GetPriorUserSelectedTabbedPaneIndexVirtualFunction()  { return myPriorUserSelectedTabIndex; }   
   protected void  SetPriorUserSelectedTabbedPaneIndexVirtualFunction()  { myPriorUserSelectedTabIndex = super.GetSelectedTabbedPaneIndex(); } 
   private static int   myPriorUserSelectedTabIndex = 0;   
}



