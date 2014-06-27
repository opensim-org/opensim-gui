//-----------------------------------------------------------------------------
// File:     LSPropertyEditorJoint.java
// Parents:  LSDialog
// Purpose:  Displays/edits properties for joints (connections between rigid bodies).
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
import  LSJava.LSComponents.*;
import  LSJava.LSResources.*;
import  java.awt.*; 
import  java.awt.event.*;
import  javax.swing.event.ChangeEvent;
import  javax.swing.JTabbedPane; 
import  javax.swing.JLabel; 

import  org.opensim.view.ModelWindowVTKTopComponent;
import  org.opensim.modeling.OpenSimObject;
import  org.opensim.view.nodes.OpenSimObjectNode;
import  org.opensim.view.nodes.OneJointNode;


//-----------------------------------------------------------------------------
public class LSPropertyEditorJoint extends LSPropertyEditorTabbedAbstract implements ActionListener, FocusListener, ItemListener, KeyListener 
{ 
   // Constructor ---------------------------------------------------------------- 
   public LSPropertyEditorJoint( OneJointNode jointNodePassedToConstructor, ModelWindowVTKTopComponent ownerWindowPassedToConstructor )  
   { 
       this( jointNodePassedToConstructor.getOpenSimObject(), jointNodePassedToConstructor, ownerWindowPassedToConstructor ); 
   }

   // Constructor ----------------------------------------------------------------
   public  LSPropertyEditorJoint( OpenSimObject openSimObjectPassedToConstructor, OneJointNode jointNodePassedToConstructor, ModelWindowVTKTopComponent ownerWindowPassedToConstructor  )  
   { 
      super( openSimObjectPassedToConstructor, jointNodePassedToConstructor, ownerWindowPassedToConstructor, "jointNode.png", "Joint", 600, 480 );  

      // Add panels, choose the previously selected panel to show, and display the window. 
      this.AddPanelsToFrame(); 
      super.SetSelectedTabbedPaneFromPriorUserSelection();
      this.GetDialogAsContainer().PackLocateShow();
   } 
   
   //-------------------------------------------------------------------------
   public void  actionPerformed( ActionEvent actionEvent )  { this.CheckActionEventTarget( actionEvent.getSource() ); }
   public void  focusLost(   FocusEvent focusEvent )        { this.CheckFocusLostEventTarget(  focusEvent.getSource() ); }
   public void  focusGained( FocusEvent focusEvent )        { this.CheckFocusGainedEventTarget( focusEvent.getSource() ); } 

   //-------------------------------------------------------------------------
   public void  itemStateChanged( ItemEvent itemEvent )     
   {
      Object eventTarget = itemEvent.getSource();
      if( eventTarget == myShowJointCheckbox ) super.ShowOrHideObject( myShowJointCheckbox.GetCheckboxState() );
   }
  
   //-------------------------------------------------------------------------
   public void  stateChanged( ChangeEvent changeEvent ) 
   {
      Object eventTarget = changeEvent.getSource();
   }


   //-------------------------------------------------------------------------
   public void  keyPressed( KeyEvent keyEvent )             {;}
   public void  keyReleased( KeyEvent keyEvent )            {;}
   public void  keyTyped( KeyEvent keyEvent )
   {
      switch( keyEvent.getKeyChar() )
      {
         case KeyEvent.VK_ESCAPE:
         case KeyEvent.VK_ENTER:   this.CheckFocusLostEventTarget( keyEvent.getSource() ); break;
      }
   }


   //-------------------------------------------------------------------------
   private void  CheckActionEventTarget( Object eventTarget )
   {
      if(      eventTarget == myButtonToOpenOldPropertyViewerTable ) super.ProcessEventClickButtonToOpenOldPropertyViewerTable();
      else if( eventTarget == myButtonToOpenColorChooser )           super.CreateColorDialogBoxToChangeUserSelectedColor();
      else if( eventTarget == myButtonToOpenJointFrameInboard )      this.CreateSeparateDialogForJointInboardOrOutboardFrame( true  );
      else if( eventTarget == myButtonToOpenJointFrameOutboard )     this.CreateSeparateDialogForJointInboardOrOutboardFrame( false );
   }
   
   //-------------------------------------------------------------------------
   private void  CheckFocusLostEventTarget( Object eventTarget )
   {
      if(  eventTarget==myNameTextField )  super.SetOpenSimObjectNameAndPropertyEditorDialogTitle( myNameTextField.GetTextFieldAsString() );
 
      // Update rotation matrix if numbers are good.
      if( eventTarget==myCoordAOrientationFromBodyAqXTextField || eventTarget==myCoordAOrientationFromBodyAqYTextField || eventTarget==myCoordAOrientationFromBodyAqZTextField )
      {
         LSTextField eventTargetAsTextField = (LSTextField)eventTarget;
         boolean isValidDouble = eventTargetAsTextField.IsTextFieldValidDouble();
         if( isValidDouble ) this.ProcessEventJointOrientationRelativeToParentChanged();
      } 
   }
   

   //-------------------------------------------------------------------------
   private void  CheckFocusGainedEventTarget( Object eventTarget )
   {
   }

   //-----------------------------------------------------------------------------
   private void  AddPanelsToFrame() 
   { 
      super.AddTabToPropertyEditor( "Appearance",                 null,  this.CreateTabComponentAppearanceJointProperty(),          null );
      super.AddTabToPropertyEditor( "Joint frames",               null,  this.CreateTabComponentJointFrameSelectorButtons(),        null );
      super.AddTabToPropertyEditor( "Location in parent",         null,  this.CreateTabComponentJointPositionFromParentOrigin(),    null );
      super.AddTabToPropertyEditor( "Orientation in parent",      null,  this.CreateTabComponentJointOrientationRelativeToParent(), null );
     
      // super.AddTabToPropertyEditor( "Free joint",                 null,  this.CreateTabComponentJointTypeTBD( 0 ),                  null );
      // super.AddTabToPropertyEditor( "Ball joint on curved slot",  null,  this.CreateTabComponentJointTypeTBD( 1 ),                  null );
      // super.AddTabToPropertyEditor( "Ball joint",                 null,  this.CreateTabComponentJointTypeTBD( 2 ),                  null );
      // super.AddTabToPropertyEditor( "Revolute on straight slot",  null,  this.CreateTabComponentJointTypeTBD( 3 ),                  null );
      // super.AddTabToPropertyEditor( "Prismatic joint",            null,  this.CreateTabComponentJointTypeTBD( 4 ),                  null );
      // super.AddTabToPropertyEditor( "Revolute joint",             null,  this.CreateTabComponentJointTypeTBD( 5 ),                  null );
      // super.AddTabToPropertyEditor( "Rigid joint on curved slot", null,  this.CreateTabComponentJointTypeTBD( 6 ),                  null );
      // super.AddTabToPropertyEditor( "Custom joint",               null,  this.CreateTabComponentJointTypeTBD( 0 ),                  null );
        
      // Note: The following create a shortcut of the user pressing ALT-1 to get the 0th panel added to tabbedPane, etc. 
      // tabbedPane.setMnemonicAt( 0, KeyEvent.VK_1 ); 
      // tabbedPane.setMnemonicAt( 1, KeyEvent.VK_2 ); 
      // tabbedPane.setMnemonicAt( 2, KeyEvent.VK_3 ); 
      // tabbedPane.setMnemonicAt( 3, KeyEvent.VK_4 ); 

      // Add tabbed pane to this frame.   Use layout policy JTabbedPane.WRAP_TAB_LAYOUT or JTabbedPane.SCROLL_TAB_LAYOUT. 
      super.AddTabbedPanelToThisWithDesignatedLayoutPolicy( JTabbedPane.WRAP_TAB_LAYOUT );  
   } 


   //-----------------------------------------------------------------------------
   private Component  CreateTabComponentAppearanceJointProperty( ) 
   { 
      LSPanel     tabComponentWithTwoPanels = new LSPanel(); 
      LSContainer tabComponentWithTwoPanelsContainer = tabComponentWithTwoPanels.GetPanelAsContainer();
      tabComponentWithTwoPanelsContainer.SetContainerBackgroundColor( Color.white );
      
      // Other possible images are: ImageIcon  openSimImageIcon = TheApp.getApplicationIcon()  and  Image openSimLogoImage = TheApp.getAppImage();
      LSPanel picturePanel = LSImageResource.GetLSPanelFromLSResourcesFileNameScaled( "OpenSimLogoNoWords.jpg", 0, 150, Color.white );
      tabComponentWithTwoPanelsContainer.AddComponentToLayoutColRemainder1Wide( picturePanel );

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
      myNameTextField  = new LSTextField( super.GetOpenSimObjectName(), super.GetTextFieldWidthForOpenSimObjectName(), true, tabContainer, GridBagConstraints.REMAINDER, 1, this, this, this );
      
      // Add show joint checkbox.
      tabContainer.AddBlankLabelToLayout1Wide1High();
      myShowJointCheckbox  = new LSCheckbox( "Show joint", true, null, tabContainer, GridBagConstraints.REMAINDER, 1, this );

      // Single blank line.
      tabContainer.AddBlankLabelToLayoutRowRemainder1High();

      // Create button that if invoked, opens up the color chooser.
      myButtonToOpenColorChooser = new LSButton( "Color", tabContainer, GridBagConstraints.REMAINDER, 1, this, null );

      return tabComponentWithTwoPanels;
   } 


   //-----------------------------------------------------------------------------
   private Component  CreateTabComponentJointFrameSelectorButtons( ) 
   { 
      LSPanel     tabComponent = new LSPanel(); 
      LSContainer tabContainer = tabComponent.GetPanelAsContainer();
      tabContainer.SetContainerBackgroundColor( Color.white );

      // Label describing what these buttons do.
      tabContainer.AddBlankLabelToLayoutRowRemainder1High();
      new LSLabel( "Edit position and/or orientation of Joint frames", LSLabel.CENTER, tabContainer, GridBagConstraints.REMAINDER );
      tabContainer.AddBlankLabelToLayoutRowRemainder1High();
      
      // Buttons to open Proximal and Distal frame editors.
      LSPanel     jointFrameButtonPanel = new LSPanel( tabContainer, GridBagConstraints.REMAINDER, 1 );
      LSContainer jointFrameButtonContainer = jointFrameButtonPanel.GetPanelAsContainer();

      myButtonToOpenJointFrameInboard  = new LSButton( "Joint Frame P", jointFrameButtonContainer, 1, 1, this, null );
      jointFrameButtonContainer.AddBlankLabelToLayout1Wide1High();
      myButtonToOpenJointFrameOutboard = new LSButton( "Joint Frame Q",  jointFrameButtonContainer, 1, 1, this, null );
      jointFrameButtonContainer.AddBlankLabelToLayoutRowRemainder1High();

      jointFrameButtonContainer.AddLabelToLayout1Wide1High( "Attached to Body A (pictured below)", LSLabel.CENTER );
      jointFrameButtonContainer.AddBlankLabelToLayout1Wide1High();
      jointFrameButtonContainer.AddLabelToLayout1Wide1High( "Attached to Body B (pictured below)", LSLabel.CENTER );
      jointFrameButtonContainer.AddBlankLabelToLayoutRowRemainder1High();

      jointFrameButtonContainer.AddLabelToLayout1Wide1High( "(i.e., " + this.GetJointConnectedToRigidBodyName(true)  + ")", LSLabel.CENTER );
      jointFrameButtonContainer.AddLabelToLayout1Wide1High( "    ", LSLabel.CENTER );
      jointFrameButtonContainer.AddLabelToLayout1Wide1High( "(i.e., " + this.GetJointConnectedToRigidBodyName(false) + ")", LSLabel.CENTER );
      jointFrameButtonContainer.AddBlankLabelToLayoutRowRemainder1High();

      // Add picture of a joint with inboard/outboard frames.
      tabContainer.AddBlankLabelToLayoutRowRemainder1High();
      JLabel labelWithPicture = LSJava.LSResources.LSImageResource.GetJLabelFromLSResourcesFileNameScaled( "JointInboardAndOutboardFrames.jpg", 0, 160 );
      if( labelWithPicture != null )  tabContainer.AddComponentToLayoutRowRemainder1High( labelWithPicture );
      
      return tabComponent;
   } 


   //-----------------------------------------------------------------------------
   private Component  CreateTabComponentJointPositionFromParentOrigin( ) 
   { 
      LSPanel     tabComponent = new LSPanel(); 
      LSContainer tabContainer = tabComponent.GetPanelAsContainer();
      tabContainer.SetContainerBackgroundColor( Color.white );

      // Create position labels and text fields.
      tabContainer.AddBlankLabelToLayoutRowRemainder1High();
      new LSLabel( "Position of Joint CoordA from BodyA origin (Ao)", LSLabel.CENTER, tabContainer, GridBagConstraints.REMAINDER );
         
      new LSLabel( "Position  =  ", LSLabel.RIGHT, tabContainer );
      myCoordAPositionFromBodyAOriginXTextField  = new LSTextFieldWithListenersForOpenSimArrayDouble( 0, super.GetPropertyTalkToSimbody(), "location_in_parent", 0, true, tabContainer, 1, 1, this, this, this );
      new LSLabel( " * bx", LSLabel.LEFT, tabContainer, GridBagConstraints.REMAINDER );
      
      new LSLabel( "  +  ", LSLabel.RIGHT, tabContainer );
      myCoordAPositionFromBodyAOriginYTextField  = new LSTextFieldWithListenersForOpenSimArrayDouble( 1, super.GetPropertyTalkToSimbody(), "location_in_parent", 0, true, tabContainer, 1, 1, this, this, this );
      new LSLabel( " * by", LSLabel.LEFT, tabContainer, GridBagConstraints.REMAINDER );

      new LSLabel( "  +  ", LSLabel.RIGHT, tabContainer );
      myCoordAPositionFromBodyAOriginZTextField  = new LSTextFieldWithListenersForOpenSimArrayDouble( 2, super.GetPropertyTalkToSimbody(), "location_in_parent", 0, true, tabContainer, 1, 1, this, this, this );
      new LSLabel( " * bz", LSLabel.LEFT, tabContainer, GridBagConstraints.REMAINDER );

      // Add picture of a joint.
      JLabel labelWithPicture = LSJava.LSResources.LSImageResource.GetJLabelFromLSResourcesFileNameScaled( "RigidBodyWithOriginCMAndBasisVectors.png", 0, 140 );
      if( labelWithPicture != null )  tabContainer.AddComponentToLayoutRowRemainder1High( labelWithPicture );
      
      return tabComponent;
   } 

   //-----------------------------------------------------------------------------
   private Component  CreateTabComponentJointOrientationRelativeToParent( ) 
   { 
      LSPanel     tabComponent = new LSPanel(); 
      LSContainer tabContainer = tabComponent.GetPanelAsContainer();
      tabContainer.SetContainerBackgroundColor( Color.white );

      // Create orientation labels and text fields.
      tabContainer.AddBlankLabelToLayoutRowRemainder1High();
      new LSLabel( "Body-fixed XYZ sequence of orientation/rotation angles", LSLabel.CENTER, tabContainer, GridBagConstraints.REMAINDER );
      tabContainer.AddBlankLabelToLayoutRowRemainder1High();
      
      new LSLabel( "qX = ", LSLabel.RIGHT, tabContainer );
      myCoordAOrientationFromBodyAqXTextField = new LSTextFieldWithListenersForOpenSimArrayDouble( 0, super.GetPropertyTalkToSimbody(), "orientation_in_parent", 0, true, tabContainer, 1, 1, this, this, this );
      new LSLabel( " (radians)", LSLabel.LEFT, tabContainer, GridBagConstraints.REMAINDER );
      
      new LSLabel( "qY = ", LSLabel.RIGHT, tabContainer );
      myCoordAOrientationFromBodyAqYTextField = new LSTextFieldWithListenersForOpenSimArrayDouble( 0, super.GetPropertyTalkToSimbody(), "orientation_in_parent", 0, true, tabContainer, 1, 1, this, this, this );
      new LSLabel( " (radians)", LSLabel.LEFT, tabContainer, GridBagConstraints.REMAINDER );

      new LSLabel( "qZ = ", LSLabel.RIGHT, tabContainer );
      myCoordAOrientationFromBodyAqZTextField = new LSTextFieldWithListenersForOpenSimArrayDouble( 0, super.GetPropertyTalkToSimbody(), "orientation_in_parent", 0, true, tabContainer, 1, 1, this, this, this );
      new LSLabel( " (radians)", LSLabel.LEFT, tabContainer, GridBagConstraints.REMAINDER );

      // Create rotation matrix labels and text fields.
      tabContainer.AddBlankLabelToLayoutRowRemainder1High();
      new LSLabel( "Rotation matrix relating joint CoordA to Parent Body", LSLabel.CENTER, tabContainer, GridBagConstraints.REMAINDER );
      myJointRotationMatrixToInboardParent = new LSPanelRotationMatrixTextFields( tabContainer, GridBagConstraints.REMAINDER, 1 );
      this.ProcessEventJointOrientationRelativeToParentChanged();

      // Add picture of a joint.
      JLabel labelWithPicture = LSJava.LSResources.LSImageResource.GetJLabelFromLSResourcesFileNameScaled( "RigidBodyWithCMAndBasisVectors.png", 0, 140 );
      if( labelWithPicture != null )  tabContainer.AddComponentToLayoutRowRemainder1High( labelWithPicture );

      return tabComponent;
   } 

   
   //-----------------------------------------------------------------------------
   private Component  CreateTabComponentJointTypeTBD( int pictureNumber ) 
   { 
      LSPanel     tabComponent = new LSPanel(); 
      LSContainer tabContainer = tabComponent.GetPanelAsContainer();

      new LSLabel( "TBD: Joint type (degrees of freedom and actuation)", LSLabel.CENTER, tabContainer, GridBagConstraints.REMAINDER );
      tabContainer.AddBlankLabelToLayoutRowRemainder1High();

      // Add picture of a joint.
      tabContainer.SetContainerBackgroundColor( Color.white );
      String graphicsFileName = "JointPictureMedResolutionBallAndSocket.jpg";
      switch( pictureNumber )
      {
          case 1:  graphicsFileName =  "JointPictureMedResolutionBallJointOnCurvedSlot.jpg";        break;
          case 2:  graphicsFileName =  "JointPictureMedResolutionBallAndSocket.jpg";	            break;
          case 3:  graphicsFileName =  "JointPictureMedResolutionRevoluteJointOnStraightSlot.jpg";  break;
          case 4:  graphicsFileName =  "JointPictureMedResolutionPrismaticJointOrSlider.jpg";       break;
          case 5:  graphicsFileName =  "JointPictureMedResolutionRevoluteJointOrPinOrHinge.jpg";    break;
          case 6:  graphicsFileName =  "JointPictureMedResolutionRigidJointOnSlot.jpg";             break;
      }
      JLabel labelWithPicture = LSJava.LSResources.LSImageResource.GetJLabelFromLSResourcesFileNameScaled( graphicsFileName, 0, 140 );
      if( labelWithPicture != null )  tabContainer.AddComponentToLayoutRowRemainder1High( labelWithPicture );

      return tabComponent;
   } 


   //-------------------------------------------------------------------------
   private void  CreateSeparateDialogForJointInboardOrOutboardFrame( boolean isInboardFrame )
   {
      String message = isInboardFrame ? "Create Proximal Frame Editor" : "Create Distal Frame Editor";
      LSMessageDialog.NewUserMessageDialog( this, message ); 
   }
   

   //-------------------------------------------------------------------------
   private String  GetJointConnectedToRigidBodyName( boolean isRigidBodyA )
   {
      String rigidBodyName = (isRigidBodyA==true) ? "Body A" : "Body B";
      return rigidBodyName;
   }
   
   //-------------------------------------------------------------------------
   private void  ProcessEventJointOrientationRelativeToParentChanged( )
   {
      double qX = myCoordAOrientationFromBodyAqXTextField.GetTextFieldAsValidDoubleOrAlternate( 0.0 );
      double qY = myCoordAOrientationFromBodyAqYTextField.GetTextFieldAsValidDoubleOrAlternate( 0.0 );
      double qZ = myCoordAOrientationFromBodyAqZTextField.GetTextFieldAsValidDoubleOrAlternate( 0.0 );
      myJointRotationMatrixToInboardParent.SetRotationMatrixTextFieldsFromBodyXYZAngles( qX, qY, qZ );
   }


   //----------------------------------------------------------------------------- 
   // Relevant class hierarchy: OneJointNode -> OpenSimObjectNode -> OpenSimNode -> AbstractNode -> Node 
   //----------------------------------------------------------------------------- 
   private OneJointNode  GetAssociatedOpenSimOneJointNodeOrNull( )
   {

      OpenSimObjectNode openSimObjectNode = super.GetAssociatedOpenSimObjectNodeOrNull();
      return (openSimObjectNode instanceof OneJointNode) ? (OneJointNode)openSimObjectNode : null;
   }


   //-----------------------------------------------------------------------------
   // Class data

   // Items appearing on the appearance tab.
   private LSButton       myButtonToOpenOldPropertyViewerTable;
   private LSTextField    myNameTextField; 
   private LSCheckbox     myShowJointCheckbox;
   private LSButton       myButtonToOpenColorChooser;

   // Items appearing on the Joint frame selector tab.
   private LSButton       myButtonToOpenJointFrameInboard;
   private LSButton       myButtonToOpenJointFrameOutboard;
   
   // Fields relating CoordA to parent body.
   private LSPanelRotationMatrixTextFields  myJointRotationMatrixToInboardParent; 
   private LSTextFieldWithListenersForOpenSimArrayDouble    myCoordAOrientationFromBodyAqXTextField;
   private LSTextFieldWithListenersForOpenSimArrayDouble    myCoordAOrientationFromBodyAqYTextField;
   private LSTextFieldWithListenersForOpenSimArrayDouble    myCoordAOrientationFromBodyAqZTextField;
   private LSTextFieldWithListenersForOpenSimArrayDouble    myCoordAPositionFromBodyAOriginXTextField;
   private LSTextFieldWithListenersForOpenSimArrayDouble    myCoordAPositionFromBodyAOriginYTextField;
   private LSTextFieldWithListenersForOpenSimArrayDouble    myCoordAPositionFromBodyAOriginZTextField;
   
   //-----------------------------------------------------------------------------
   // When this property editor is re-displayed, show tab that was last viewed by the user.
   protected int   GetPriorUserSelectedTabbedPaneIndexVirtualFunction()  { return myPriorUserSelectedTabIndex; }   
   protected void  SetPriorUserSelectedTabbedPaneIndexVirtualFunction()  { myPriorUserSelectedTabIndex = super.GetSelectedTabbedPaneIndex(); } 
   private static int   myPriorUserSelectedTabIndex = 0;   
}



