//-----------------------------------------------------------------------------
// File:     LSPropertyEditorRigidBody.java
// Class:    LSPropertyEditorRigidBody
// Parents:  LSFrame
// Purpose:  Displays/edits properties for rigid bodies.
// Authors:  Paul Mitiguy, 2011.   
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
import  java.awt.image.BufferedImage; 
import  javax.swing.JTabbedPane; 
import  javax.swing.ImageIcon; 
import  javax.swing.JLabel; 
import  javax.swing.JColorChooser;

import  org.opensim.view.ModelWindowVTKTopComponent;
import  org.opensim.view.editors.ObjectEditDialogMaker;
import  org.opensim.view.pub.ViewDB;
import  org.opensim.modeling.OpenSimObject;
import  org.opensim.view.nodes.OneBodyNode;
import  org.opensim.view.nodes.OpenSimObjectNode;
import  org.opensim.view.nodes.OpenSimNode;
import  org.opensim.view.ObjectDisplayColorAction;
import  org.opensim.view.ExplorerTopComponent;
import  org.opensim.view.ObjectDisplayShowHideBaseAction;
import  org.opensim.utils.TheApp;

import  org.openide.nodes.Node;

//-----------------------------------------------------------------------------
public class LSPropertyEditorRigidBody extends LSDialog implements ActionListener, FocusListener, ItemListener, KeyListener 
{ 
   // Constructor ---------------------------------------------------------------- 
   public LSPropertyEditorRigidBody( OneBodyNode oneBodyNodePassedToConstructor, ModelWindowVTKTopComponent ownerWindowPassedToConstructor )  
   { 
       this( oneBodyNodePassedToConstructor.getOpenSimObject(), ownerWindowPassedToConstructor ); 
       myAssociatedOpenSimOneBodyNodeOrNull = oneBodyNodePassedToConstructor;
   }

   // Constructor ----------------------------------------------------------------
   public  LSPropertyEditorRigidBody( OpenSimObject openSimObjectPassedToConstructor, ModelWindowVTKTopComponent ownerWindowPassedToConstructor  )  
   { 
      // Arguments: ownerFrame, dialogTitle, isModal, isResizeable.
      super( TheApp.getAppFrame(), null, false, true );

      // Connect this object to the corresponding OpenSim object.
      myAssociatedOpenSimObject = openSimObjectPassedToConstructor;
      myOpenSimModelWindowVTKTopComponentOwnerWindow = ownerWindowPassedToConstructor;
      this.SetAssociatedOpenSimOneBodyNodeOrNullIfNotAlreadySet();
            
      // Display the frame with an appropriate name and icon.
      this.SetRigidBodyDialogTitle(); 
      this.SetRigidBodyDialogIconImage();

      // Set the background color to light yellow
      int xNumberOfPixelsOfTabbedPaneMin = 560;
      int yNumberOfPixelsOfTabbedPaneMin = 400;
      int xNumberOfPixelsThisDialogMin = xNumberOfPixelsOfTabbedPaneMin + 40;
      int yNumberOfPixelsThisDialogMin = yNumberOfPixelsOfTabbedPaneMin + 40;
      super.setBackground( LSColor.BackgroundColorVeryLightGray );
      super.setMinimumSize(   new Dimension(xNumberOfPixelsThisDialogMin, yNumberOfPixelsThisDialogMin) );
      super.setPreferredSize( new Dimension(xNumberOfPixelsThisDialogMin, yNumberOfPixelsThisDialogMin) );
      super.setMaximumSize(   new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE ) );

      // Add each tab in a tabbed pane the same size as the property editor
      // JTabbedPane is a GUI element that has built-in listeners and event handlers.
      // A. User can use the mouse to click to switch to a specific tab.
      // B. User can use keyboard arrows to switch from tab to next tab.
      // C. If enabled, user can use Mnemonics (which may not make sense here).
      myJTabbedPane = new JTabbedPane(); 
      myJTabbedPane.setMinimumSize(   new Dimension(xNumberOfPixelsOfTabbedPaneMin, yNumberOfPixelsOfTabbedPaneMin) );
      myJTabbedPane.setPreferredSize( new Dimension(xNumberOfPixelsOfTabbedPaneMin, yNumberOfPixelsOfTabbedPaneMin) );
      myJTabbedPane.setMaximumSize(   new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE) );
      this.AddPanelsToFrame(); 

      // Default location to issue warning messages.
      LSMessageDialog.SetCurrentWindowToIssueMessages( (Window)this );
        
      // If first time one of these created, set the default tab to open.
      // Otherwise, open the user's last selected tab component.
      this.SetCurrentUserSelectedTabIndex( LSPropertyEditorRigidBody.GetPriorUserSelectedTabComponent() );

      // Display the window. 
      GetDialogAsContainer().PackLocateShow();
   } 
   
   //-------------------------------------------------------------------------
   public void  actionPerformed( ActionEvent actionEvent )  { this.CheckActionEventTarget( actionEvent.getSource() ); }
   public void  focusLost(   FocusEvent focusEvent )        { this.CheckFocusLostEventTarget(  focusEvent.getSource() ); }
   public void  focusGained( FocusEvent focusEvent )        { this.CheckFocusGainedEventTarget( focusEvent.getSource() ); } 

   //-------------------------------------------------------------------------
   public void  itemStateChanged( ItemEvent itemEvent )     
   {
      if( itemEvent.getSource() == myShowRigidBodyCheckbox ) this.ProcessEventShowOrHideRigidBody( myShowRigidBodyCheckbox.GetCheckboxState() );
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
      if(      eventTarget == myButtonToOpenOldTableOfRigidProperties ) this.ProcessEventClickButtonToOpenTableOfRigidBodyProperties();
      else if( eventTarget == myButtonToOpenColorChooser )              this.ProcessEventClickButtonToOpenColorChooser(); 
   }
   
   //-------------------------------------------------------------------------
   private boolean  CheckFocusLostEventTarget( Object eventTarget )
   {
      boolean requestFocusBack = false;
      Window ownerWindowOrThis = super.GetLSDialogOwnerWindowOrThis(); 
      if(      eventTarget==myNameTextField )   this.ProcessEventRigidBodyNameChanged();
      else if( eventTarget==myMassTextField )  {if( (requestFocusBack = myMassTextField.IssueErrorMessageIfTextFieldIsBadDoublePrecisionNumber(ownerWindowOrThis)) != true ) myMassTextField.IssueWarningMessageIfTextFieldIsNegativeNumber(ownerWindowOrThis); }
      else if( eventTarget==myXcmTextField  )        requestFocusBack =  myXcmTextField.IssueErrorMessageIfTextFieldIsBadDoublePrecisionNumber(ownerWindowOrThis);
      else if( eventTarget==myYcmTextField  )        requestFocusBack =  myYcmTextField.IssueErrorMessageIfTextFieldIsBadDoublePrecisionNumber(ownerWindowOrThis);
      else if( eventTarget==myZcmTextField  )        requestFocusBack =  myZcmTextField.IssueErrorMessageIfTextFieldIsBadDoublePrecisionNumber(ownerWindowOrThis);
      else if( eventTarget==myIxxTextField || eventTarget==myIyyTextField || eventTarget==myIzzTextField )
      {
         LSTextField eventTargetAsTextField = (LSTextField)eventTarget;
         if( (requestFocusBack = eventTargetAsTextField.IssueErrorMessageIfTextFieldIsBadDoublePrecisionNumber(ownerWindowOrThis)) != true )
	    eventTargetAsTextField.IssueWarningMessageIfTextFieldIsNegativeNumber(ownerWindowOrThis); 
         this.CheckInertiaMatrixAndGiveVisualFeedbackOnWhetherOrNotItIsPhysicallyPossible();
      }
      else if( eventTarget == myIxyTextField ) requestFocusBack = this.ProcessEventProductOfInertiaChanged( (LSTextField)eventTarget, myIyxTextField );
      else if( eventTarget == myIxzTextField ) requestFocusBack = this.ProcessEventProductOfInertiaChanged( (LSTextField)eventTarget, myIzxTextField );
      else if( eventTarget == myIyzTextField ) requestFocusBack = this.ProcessEventProductOfInertiaChanged( (LSTextField)eventTarget, myIzyTextField );
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
      this.AddTabToRigidBodyProperties( "Appearance",               null,  this.CreateTabComponentAppearanceRigidBodyProperty(),             "Appearance" );
      this.AddTabToRigidBodyProperties( "Mass and Center of Mass",  null,  this.CreateTabComponentMassAndCenterOfMassRigidBodyProperty(),    "Mass and Center of Mass" );
      this.AddTabToRigidBodyProperties( "Inertia Properties",       null,  this.CreateTabComponentInertiaPropertiesRigidBodyProperty(),      "Inertia Properties" );
      this.AddTabToRigidBodyProperties( "Position and Orientation", null,  this.CreateTabComponentPositionAndOrientationRigidBodyProperty(), "Position and Orientation" );
        
      // Note: The following create a shortcut of the user pressing ALT-1 to get the 0th panel added to tabbedPane, etc. 
      // tabbedPane.setMnemonicAt( 0, KeyEvent.VK_1 ); 
      // tabbedPane.setMnemonicAt( 1, KeyEvent.VK_2 ); 
      // tabbedPane.setMnemonicAt( 2, KeyEvent.VK_3 ); 
      // tabbedPane.setMnemonicAt( 3, KeyEvent.VK_4 ); 

      // Add tabbed pane to this frame. 
      this.GetDialogAsContainer().AddComponent( myJTabbedPane ); 
        
      // Tab layout defaults to JTabbedPane.WRAP_TAB_LAYOUT.  Otherwise use JTabbedPane.SCROLL_TAB_LAYOUT. 
      myJTabbedPane.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT ); 
   } 

   //-----------------------------------------------------------------------------
   private void  AddTabToRigidBodyProperties( String nameOnTab, ImageIcon iconOnTabOrNull, Component component, String toolTipStringOrNull )  
   { 
      if( myJTabbedPane != null )  myJTabbedPane.addTab( nameOnTab, iconOnTabOrNull, component, toolTipStringOrNull ); 
   } 

   //-----------------------------------------------------------------------------
   private Component  CreateTabComponentAppearanceRigidBodyProperty( ) 
   { 
      LSPanel     tabComponentWithTwoPanels = new LSPanel(); 
      LSContainer tabComponentWithTwoPanelsContainer = tabComponentWithTwoPanels.GetPanelAsContainer();
      tabComponentWithTwoPanelsContainer.SetContainerBackgroundColor( Color.white );
      
      // Add an image to the left-hand-side of the panel, then put in some blank white space.
      // Other possible images are: ImageIcon  openSimImageIcon = TheApp.getApplicationIcon()  and  Image openSimLogoImage = TheApp.getAppImage();
      LSPanel picturePanel = new LSPanel( tabComponentWithTwoPanelsContainer, 1, GridBagConstraints.REMAINDER );
      LSContainer picturePanelContainer = picturePanel.GetPanelAsContainer();
      picturePanelContainer.SetContainerBackgroundColor( Color.white );
      JLabel labelWithPicture = LSJava.LSResources.LSImageResource.GetJLabelFromLSResourcesFileNameScaled( "OpenSimLogoNoWords.jpg", 0, 150 );
      if( labelWithPicture != null )  picturePanel.GetPanelAsContainer().AddComponentToLayout( labelWithPicture, 1, GridBagConstraints.REMAINDER );
      new LSLabel( " ", Label.CENTER, picturePanelContainer, 1, GridBagConstraints.REMAINDER );

      // Add a panel with relevant buttons, textboxes, etc.
      LSPanel tabComponent = new LSPanel( tabComponentWithTwoPanelsContainer, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER ); 
      LSContainer tabContainer = tabComponent.GetPanelAsContainer();
      tabContainer.SetContainerBackgroundColor( Color.white );
      
      // Create button to open up the old property viewer table.
      myButtonToOpenOldTableOfRigidProperties = new LSButton( "Property Viewer", tabContainer, GridBagConstraints.REMAINDER, 1, this, null );

      // Single blank line.
      tabContainer.AddBlankLabelToLayoutRowRemainder();

      // Create text field  to change the name of the object.
      String nameOfRigidBody = this.GetNameOfRigidBody();
      int  lengthOfNameOfRigidBody = LSString.GetStringLength( nameOfRigidBody );
      int  textFieldWidthForNameOfRigidBody = (int)( 1.5*( lengthOfNameOfRigidBody < 12 ? 12 : lengthOfNameOfRigidBody ) );
      new LSLabel( "Name = ", Label.RIGHT, tabContainer, 1 );
      myNameTextField  = new LSTextField( nameOfRigidBody, textFieldWidthForNameOfRigidBody, true, tabContainer, GridBagConstraints.REMAINDER, 1, this, this, this );
      
      // Add show rigid body checkbox.
      tabContainer.AddBlankLabelToLayoutRow(1);
      myShowRigidBodyCheckbox  = new LSCheckbox( "Show rigid body", true, null, tabContainer, GridBagConstraints.REMAINDER, 1, this );

      // Add show center of mass of rigid body checkbox.
      tabContainer.AddBlankLabelToLayoutRow(1); 
      myShowCMCheckbox  = new LSCheckbox( "Show center of mass", false, null, tabContainer, GridBagConstraints.REMAINDER, 1, this );

      // Single blank line.
      tabContainer.AddBlankLabelToLayoutRowRemainder();

      // Create button that if invoked, opens up the color chooser.
      myButtonToOpenColorChooser = new LSButton( "Color", tabContainer, GridBagConstraints.REMAINDER, 1, this, null );

      return tabComponentWithTwoPanels;
   } 

   //-----------------------------------------------------------------------------
   private Component  CreateTabComponentMassAndCenterOfMassRigidBodyProperty( ) 
   { 
      LSPanel     tabComponent = new LSPanel(); 
      LSContainer tabContainer = tabComponent.GetPanelAsContainer();
      tabContainer.SetContainerBackgroundColor( Color.white );

      // Create mass label and text field.
      LSLabel massLabel = new LSLabel( "Mass = ", Label.RIGHT, tabContainer, 1 );
      myMassTextField = new LSTextField( 3.0E-5, true, tabContainer, GridBagConstraints.REMAINDER, 1, this, this, this );

      // Create center of mass labels and text fields.
      tabContainer.AddBlankLabelToLayoutRowRemainder();
      new LSLabel( "Position of Body center of mass (Bcm) from Body origin (Bo)", Label.CENTER, tabContainer, GridBagConstraints.REMAINDER );
      
      new LSLabel( "Position  =  ", Label.RIGHT, tabContainer, 1 );
      myXcmTextField  = new LSTextField( 0.0, true, tabContainer, 1, 1, this, this, this );
      new LSLabel( " * bx", Label.LEFT, tabContainer, GridBagConstraints.REMAINDER );
      
      new LSLabel( "  +  ", Label.RIGHT, tabContainer, 1 );
      myYcmTextField  = new LSTextField( 0.0, true, tabContainer, 1, 1, this, this, this );
      new LSLabel( " * by", Label.LEFT, tabContainer, GridBagConstraints.REMAINDER );

      new LSLabel( "  +  ", Label.RIGHT, tabContainer, 1 );
      myZcmTextField  = new LSTextField( 0.0, true, tabContainer, 1, 1, this, this, this );
      new LSLabel( " * bz", Label.LEFT, tabContainer, GridBagConstraints.REMAINDER );

      // Add picture of a rigid body with Bcm (Body center of mass) and Bo (Body origin).
      JLabel labelWithPicture = LSJava.LSResources.LSImageResource.GetJLabelFromLSResourcesFileNameScaled( "RigidBodyWithOriginCMAndBasisVectors.jpg", 0, 140 );
      if( labelWithPicture != null )  tabContainer.AddComponentToLayout( labelWithPicture, GridBagConstraints.REMAINDER, 1 );
      
      return tabComponent;
   } 

   //-----------------------------------------------------------------------------
   private Component  CreateTabComponentInertiaPropertiesRigidBodyProperty( ) 
   { 
      LSPanel     tabComponent = new LSPanel(); 
      LSContainer tabContainer = tabComponent.GetPanelAsContainer();
      tabContainer.SetContainerBackgroundColor( Color.white );

      // Create inertia properties labels and text fields.
      tabContainer.AddBlankLabelToLayoutRowRemainder();
      myInertiaIncorrectLabel = new LSLabel( "Error: INVALID Inertia matrix", Label.CENTER, tabContainer, GridBagConstraints.REMAINDER );
      myInertiaIncorrectLabel.SetLabelForegroundColor( LSColor.BackgroundColorSuggestingError );
      myInertiaIncorrectLabel.SetLabelVisible( true );
 
      // Create inertia properties labels and text fields.
      tabContainer.AddBlankLabelToLayoutRowRemainder();
      new LSLabel( "Inertia matrix about Body center of mass (Bcm) for bx, by, bz", Label.CENTER, tabContainer, GridBagConstraints.REMAINDER );
      tabContainer.AddComponentToLayout( this.CreatePanelWithInertiaMatrix(), GridBagConstraints.REMAINDER, 1 );

      // Add picture of a rigid body with Bcm (Body center of mass) and Bo (Body origin).
      JLabel labelWithPicture = LSJava.LSResources.LSImageResource.GetJLabelFromLSResourcesFileNameScaled( "RigidBodyWithCMAndBasisVectors.jpg", 0, 140 );
      if( labelWithPicture != null )  tabContainer.AddComponentToLayout( labelWithPicture, GridBagConstraints.REMAINDER, 1 );

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
      myIxxTextField = new LSTextField( 1.0, true,  tabContainer, 1, 1, this, this, this );
      myIxyTextField = new LSTextField( 0.0, true,  tabContainer, 1, 1, this, this, this );
      myIxzTextField = new LSTextField( 0.0, true,  tabContainer, GridBagConstraints.REMAINDER, 1, this, this, this );
      myIyxTextField = new LSTextField( 0.0, false, tabContainer, 1, 1, null, this, null );
      myIyyTextField = new LSTextField( 2.0, true,  tabContainer, 1, 1, this, this, this );
      myIyzTextField = new LSTextField( 0.0, true,  tabContainer, GridBagConstraints.REMAINDER, 1, this, this, this );
      myIzxTextField = new LSTextField( 0.0, false, tabContainer, 1, 1, null, this, null );
      myIzyTextField = new LSTextField( 0.0, false, tabContainer, 1, 1, null, this, null );
      myIzzTextField = new LSTextField( 3.0, true,  tabContainer, GridBagConstraints.REMAINDER, 1, this, this, this );
      
      return myInertiaMatrixPanel;
   } 


   //-----------------------------------------------------------------------------
   private Component  CreateTabComponentPositionAndOrientationRigidBodyProperty( ) 
   { 
      LSPanel     tabComponent = new LSPanel(); 
      LSContainer tabContainer = tabComponent.GetPanelAsContainer();

      new LSLabel( "TBD: Position and Orientation (with degrees/radians conversion)", Label.CENTER, tabContainer, GridBagConstraints.REMAINDER );
      return tabComponent;
   } 

   
   //-----------------------------------------------------------------------------
   private String  GetNameOfRigidBody( )  { return myAssociatedOpenSimObject.getName(); } 
   private void    SetNameOfRigidBody( String newName )    
   { 
      myAssociatedOpenSimObject.setName( newName );
      if( myAssociatedOpenSimOneBodyNodeOrNull != null )
         myAssociatedOpenSimOneBodyNodeOrNull.renameObjectNode( myAssociatedOpenSimObject, newName );
   } 

   
   //----------------------------------------------------------------------------- 
   private void  SetAssociatedOpenSimOneBodyNodeOrNullIfNotAlreadySet( )
   {
      if( myAssociatedOpenSimOneBodyNodeOrNull == null )
      {
         ExplorerTopComponent explorerTopComponentTree = ExplorerTopComponent.findInstance();
         Node rootNode = explorerTopComponentTree.getExplorerManager().getRootContext();
         if( rootNode instanceof OpenSimNode )
         {
             OpenSimNode rootNodeAsOpenSimNode = (OpenSimNode)rootNode;
             Object objectToMatch = myAssociatedOpenSimObject; 
             OpenSimObjectNode matchingObjectNode = rootNodeAsOpenSimNode.findChild( objectToMatch );
             if( matchingObjectNode instanceof OneBodyNode )
                myAssociatedOpenSimOneBodyNodeOrNull = (OneBodyNode)matchingObjectNode;
         }
      }
   }
       
   
   //-----------------------------------------------------------------------------
   private void  SetRigidBodyDialogTitle( ) 
   { 
      String rigidBodyName = this.GetNameOfRigidBody();
      String frameTitle = rigidBodyName == null ? "Rigid Body Properties" : ("Rigid Body Properties for " + rigidBodyName);
      super.setTitle( frameTitle ); 
   } 
   
   
   //-----------------------------------------------------------------------------
   private void  SetRigidBodyDialogIconImage( ) 
   { 
      ImageIcon rigidBodyIcon = LSJava.LSResources.LSImageResource.GetImageIconFromLSResourcesFileName( "BodyCMPicture.png" );  
      Image imageToLeftOfDialogTitle = (rigidBodyIcon == null) ? null : rigidBodyIcon.getImage();
      if( imageToLeftOfDialogTitle != null ) super.setIconImage( imageToLeftOfDialogTitle );
   }

   
   //-------------------------------------------------------------------------
   private void  ProcessEventRigidBodyNameChanged( )
   {
       String newName = myNameTextField.GetTextFieldAsString();
       String oldName = this.GetNameOfRigidBody();
       if( newName != null && LSString.IsStringsEqualCaseSensitive(newName,oldName) == false )
       {
          this.SetNameOfRigidBody( newName );
	  this.SetRigidBodyDialogTitle();
       }
   }


   //-------------------------------------------------------------------------
   private void  ProcessEventClickButtonToOpenTableOfRigidBodyProperties( )
   {
      boolean allowEdit = false;
      ObjectEditDialogMaker editorDialog = new ObjectEditDialogMaker( myAssociatedOpenSimObject, myOpenSimModelWindowVTKTopComponentOwnerWindow, allowEdit, "OK" ); 
      editorDialog.process();
   }


   //-------------------------------------------------------------------------
   private void  ProcessEventClickButtonToOpenColorChooser( )
   {
      Color newColor = JColorChooser.showDialog( this, "Color of rigid body " + this.GetNameOfRigidBody(), Color.WHITE );
      if( myAssociatedOpenSimOneBodyNodeOrNull != null ) ObjectDisplayColorAction.ChangeUserSelectedNodeColor( myAssociatedOpenSimOneBodyNodeOrNull, newColor, true );
      else                                               ObjectDisplayColorAction.ChangeUserSelectedNodesColor( newColor );
   }


   //-----------------------------------------------------------------------------
   private void  ProcessEventShowOrHideRigidBody( boolean showOrHide )
   {
      if( myAssociatedOpenSimOneBodyNodeOrNull != null )
         ObjectDisplayShowHideBaseAction.ApplyOperationToNodeWithShowHide( myAssociatedOpenSimOneBodyNodeOrNull, showOrHide, true ); 
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
   private int          GetNumberOfTabs( )                                { return myJTabbedPane == null ? 0 : myJTabbedPane.getTabCount(); }
   private boolean      IsTabIndexInRange( int tabIndex )                 { return myJTabbedPane == null ? false : tabIndex < GetNumberOfTabs(); }
   private boolean      SetCurrentUserSelectedTabIndex( int tabIndex )    { if( !IsTabIndexInRange(tabIndex) ) return false;   myJTabbedPane.setSelectedIndex( tabIndex );   return true; } 
   private static int   GetPriorUserSelectedTabComponent()                { return myPriorUserSelectedTabInitialized ? myPriorUserSelectedTabIndex : 0; }   
   private static void  SetPriorUserSelectedTabComponent( int tabIndex )  { myPriorUserSelectedTabInitialized = true;   myPriorUserSelectedTabIndex = tabIndex; }   


   //-----------------------------------------------------------------------------
   // Class data
   private OpenSimObject  myAssociatedOpenSimObject;  
   private OneBodyNode    myAssociatedOpenSimOneBodyNodeOrNull;  // Relevant class hierarchy: OneBodyNode -> OpenSimObjectNode -> OpenSimNode -> AbstractNode -> Node
   private ModelWindowVTKTopComponent  myOpenSimModelWindowVTKTopComponentOwnerWindow;
   
   private JTabbedPane    myJTabbedPane; 
   private LSButton       myButtonToOpenOldTableOfRigidProperties;
   private LSTextField    myNameTextField; 
   private LSTextField    myMassTextField;     
   private LSTextField    myXcmTextField,  myYcmTextField,  myZcmTextField;   
   private LSCheckbox     myShowCMCheckbox;
   private LSCheckbox     myShowRigidBodyCheckbox;
   
   // Determine which LSTextField last had focus to determine if the one that regains focus is the same one.
   private LSTextField    myPreviousTextFieldThatHadFocus;
           
   // Inertia matrix fields.
   private LSTextField    myIxxTextField,  myIxyTextField,  myIxzTextField;
   private LSTextField    myIyxTextField,  myIyyTextField,  myIyzTextField;
   private LSTextField    myIzxTextField,  myIzyTextField,  myIzzTextField;
   private LSPanel        myInertiaMatrixPanel; 
   private LSLabel        myInertiaIncorrectLabel;
   
   // Object color
   private LSButton       myButtonToOpenColorChooser;

   // When this is redisplayed, show the tab that was last viewed by the user.
   private static boolean  myPriorUserSelectedTabInitialized;   
   private static int      myPriorUserSelectedTabIndex;   

}



