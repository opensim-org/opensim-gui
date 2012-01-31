//-----------------------------------------------------------------------------
// File:     LSPropertyEditorTabbedAbstract.java
// Class:    LSPropertyEditorTabbedAbstract
// Parents:  LSDialog
// Purpose:  Abstract class containing generic methods for property editing.
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
import  javax.swing.JTabbedPane; 
import  javax.swing.ImageIcon; 
import  javax.swing.JColorChooser;
import  javax.swing.event.ChangeListener;
import  javax.swing.event.ChangeEvent;

import  org.opensim.view.ModelWindowVTKTopComponent;
import  org.opensim.view.editors.ObjectEditDialogMaker;
import  org.opensim.view.nodes.OpenSimObjectNode;
import  org.opensim.view.nodes.OpenSimNode;
import  org.opensim.view.ObjectDisplayColorAction;
import  org.opensim.view.ObjectDisplayShowHideBaseAction;
import  org.opensim.view.ExplorerTopComponent;
import  org.opensim.view.pub.ViewDB;
import  org.opensim.utils.TheApp;

import  org.opensim.modeling.OpenSimObject;





//-----------------------------------------------------------------------------
public abstract class LSPropertyEditorTabbedAbstract extends LSDialog implements ChangeListener   
{ 
   // Constructor ----------------------------------------------------------------
   protected  LSPropertyEditorTabbedAbstract( OpenSimObject openSimObjectToConstructorShouldNotBeNull, OpenSimObjectNode openSimObjectNodeToConstructorMayBeNull, ModelWindowVTKTopComponent ownerWindowPassedToConstructor, String filenameForDialogIconImage, String nameOfTypeOfObjectEgRigidBodyOrJoint, int xNumberOfPixelsOfTabbedPaneMin, int yNumberOfPixelsOfTabbedPaneMin )  
   { 
      // Arguments: ownerFrame, dialogTitle, isModal, isResizeable.
      super( TheApp.getAppFrame(), null, false, true );

      // Connect this object to its corresponding OpenSim object.
      // Note: Some of the connection occurs in both parent and child constructors.
      myPropertyTalkToSimbody = new LSPropertyTalkToSimbody( openSimObjectToConstructorShouldNotBeNull, openSimObjectNodeToConstructorMayBeNull );
      myOpenSimModelWindowVTKTopComponentOwnerWindow = ownerWindowPassedToConstructor;
            
      // Display the dialog with an appropriate name and icon.
      myNameOfTypeOfObjectEgRigidBodyOrJoint = nameOfTypeOfObjectEgRigidBodyOrJoint;
      this.SetPropertyEditorIconImage( filenameForDialogIconImage );
      this.SetPropertyEditorDialogTitle(); 

      // Set the background color to light yellow
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

      // Default location to issue warning messages.
      LSMessageDialog.SetCurrentWindowToIssueMessages( (Window)this );

      // Add a listener to hear when the tabs change. 
      myJTabbedPane.addChangeListener( this );
   } 
   

   //----------------------------------------------------------------------------- 
   public void stateChanged( ChangeEvent changeEvent )  
   { 
       if( changeEvent.getSource() == myJTabbedPane )
       {  
           // First time stateChanged is called is when class is initially constructed.
           if( myStateChangeCalledByUserChangingPanesNotClassConstructor ) this.SetPriorUserSelectedTabbedPaneIndexVirtualFunction(); 
           else myStateChangeCalledByUserChangingPanesNotClassConstructor = true; 
       }
   }


   //-----------------------------------------------------------------------------
   protected void  AddTabToPropertyEditor( String nameOnTab, ImageIcon iconOnTabOrNull, Component component, String toolTipStringOrNullForNameOnTab )  
   { 
      if( myJTabbedPane != null ) myJTabbedPane.addTab( nameOnTab, iconOnTabOrNull, component, toolTipStringOrNullForNameOnTab==null ? nameOnTab : toolTipStringOrNullForNameOnTab );
   } 


   //-----------------------------------------------------------------------------
   protected void  AddTabbedPanelToThisWithDesignatedLayoutPolicy( int tabLayoutPolicyType )  
   { 
      // Add tabbed pane to this frame. 
      this.GetDialogAsContainer().AddComponent( myJTabbedPane ); 
        
      // Tab layout defaults to JTabbedPane.WRAP_TAB_LAYOUT.  Otherwise use JTabbedPane.SCROLL_TAB_LAYOUT. 
      myJTabbedPane.setTabLayoutPolicy( tabLayoutPolicyType ); 
   } 


   //----------------------------------------------------------------------------- 
   protected  LSPropertyTalkToSimbody  GetPropertyTalkToSimbody()                 { return myPropertyTalkToSimbody; }
   protected  OpenSimObject            GetAssociatedOpenSimObject()               { return myPropertyTalkToSimbody.GetOpenSimObject(); }
   protected  String                   GetOpenSimObjectName( )                    { return myPropertyTalkToSimbody.GetOpenSimObjectName(); } 
   protected  int                      GetTextFieldWidthForOpenSimObjectName( )   { return myPropertyTalkToSimbody.GetTextFieldWidthForOpenSimObjectName(); }
   protected  OpenSimObjectNode        GetAssociatedOpenSimObjectNodeOrNull()     { return myPropertyTalkToSimbody.GetOpenSimObjectNodeOrNull(); }

 
   //-------------------------------------------------------------------------
   protected int  GetObjectOpacityFromOpenSimWithRangeFrom0To100( ) 
   {
      double opacityWithRangeFrom0To1 = ViewDB.getObjectOpacityInRangeFrom0To1( this.GetAssociatedOpenSimObject() );
      return (int)(opacityWithRangeFrom0To1 * 100.0);
   }

   //-------------------------------------------------------------------------
   protected void  SetObjectOpacity( int newOpacityRangeFrom0To100 ) 
   {
      if(      newOpacityRangeFrom0To100 > 100 ) newOpacityRangeFrom0To100 = 100; 
      else if( newOpacityRangeFrom0To100 < 0   ) newOpacityRangeFrom0To100 = 0; 
      double newOpacityRangeFrom0To1 = ((double)newOpacityRangeFrom0To100) / 100.0;
      ViewDB.setObjectOpacity( this.GetAssociatedOpenSimObject(), newOpacityRangeFrom0To1 );
   }
   
 
   //-------------------------------------------------------------------------
   protected void  SetObjectRepresentationPointsWireFrameOrSurfaceAndSurfaceShading( int points0WireFrame1Surface2, int surfaceShading0Flat1GouraudSmooth )
   {
      ViewDB.getInstance().setObjectRepresentation( this.GetAssociatedOpenSimObject(), points0WireFrame1Surface2, surfaceShading0Flat1GouraudSmooth );
   } 



   //-----------------------------------------------------------------------------
   public void  SetOpenSimObjectNameAndPropertyEditorDialogTitle( String newName )    
   { 
      if( myPropertyTalkToSimbody.SetOpenSimObjectName(newName) )
	 this.SetPropertyEditorDialogTitle();
   } 


   //-----------------------------------------------------------------------------
   protected void  SetPropertyEditorDialogTitle() 
   { 
      String titleOfDialogBox = LSString.StringConcatenateWithSpacesBetweenIfNotNull( myNameOfTypeOfObjectEgRigidBodyOrJoint, "Editor:", this.GetOpenSimObjectName() );
      super.setTitle( titleOfDialogBox ); 
   } 
   
   
   //-------------------------------------------------------------------------
   protected int[]  GetObjectRGBColorIn3IntegersWithRangeFrom0To255( ) 
   {							      
      int currentRGBColorWithRangeFrom0To255[] = { 255, 255, 255 };
      double currentRGBColorWithRangeFrom0To1[] = ViewDB.getObjectRGBColorIn3DoublesWithRangeFrom0To1( this.GetAssociatedOpenSimObject() );
      int shouldBeThreeElements = currentRGBColorWithRangeFrom0To1==null ? 0 : currentRGBColorWithRangeFrom0To1.length;
      for( int i=0;  i<shouldBeThreeElements; i++ )
         currentRGBColorWithRangeFrom0To255[i] = (int)( currentRGBColorWithRangeFrom0To1[i] * 255 );
      return currentRGBColorWithRangeFrom0To255;  // Currently this just returns White.  May be due to VTK being bunches of pieces - and no real color available.
   }


   //-------------------------------------------------------------------------
   protected static void  ChangeObjectColor( OpenSimObjectNode associatedOpenSimObjectNodeOrNull, Color newColor )
   {
      if( newColor == null ) return;
      if( associatedOpenSimObjectNodeOrNull != null ) ObjectDisplayColorAction.ChangeUserSelectedNodeColor( associatedOpenSimObjectNodeOrNull, newColor, true );
      else                                            ObjectDisplayColorAction.ChangeUserSelectedNodesColor( newColor );
   }

   
   //-------------------------------------------------------------------------
   protected void  CreateColorDialogBoxToChangeUserSelectedColor( )
   {
      OpenSimObjectNode associatedOpenSimObjectNodeOrNull = this.GetAssociatedOpenSimObjectNodeOrNull();
      if( associatedOpenSimObjectNodeOrNull != null )
      {
         int[] currentRGBColorWithRangeFrom0To255 = this.GetObjectRGBColorIn3IntegersWithRangeFrom0To255();
         int currentRed   = currentRGBColorWithRangeFrom0To255[ 0 ];
         int currentGreen = currentRGBColorWithRangeFrom0To255[ 1 ];
         int currentBlue  = currentRGBColorWithRangeFrom0To255[ 2 ];
         Color initialColorInDialog = new Color( currentRed, currentGreen, currentBlue );
         String titleOfColorDialogBox = LSString.StringConcatenateWithSpacesBetweenIfNotNull( "Color of", myNameOfTypeOfObjectEgRigidBodyOrJoint, this.GetOpenSimObjectName() );
         Color newColor = JColorChooser.showDialog( this, titleOfColorDialogBox, initialColorInDialog );
         LSPropertyEditorTabbedAbstract.ChangeObjectColor( associatedOpenSimObjectNodeOrNull, newColor );
      }
   }
    

   //-------------------------------------------------------------------------
   protected void  ProcessEventClickButtonToOpenOldPropertyViewerTable( )
   {
      boolean allowEdit = false;
      ObjectEditDialogMaker editorDialog = new ObjectEditDialogMaker( this.GetAssociatedOpenSimObject(), myOpenSimModelWindowVTKTopComponentOwnerWindow, allowEdit, "OK" ); 
      editorDialog.process();
   }


   //-----------------------------------------------------------------------------
   private void  SetPropertyEditorIconImage( String filenameForDialogIconImage )
   { 
      ImageIcon jointIcon = LSJava.LSResources.LSImageResource.GetImageIconFromLSResourcesFileName( filenameForDialogIconImage );  
      Image imageToLeftOfDialogTitle = (jointIcon == null) ? null : jointIcon.getImage();
      if( imageToLeftOfDialogTitle != null ) super.setIconImage( imageToLeftOfDialogTitle );
   }


   //-----------------------------------------------------------------------------
   protected void  ShowOrHideObject( boolean showOrHide )
   {
      OpenSimObjectNode associatedOpenSimObjectNodeOrNull = this.GetAssociatedOpenSimObjectNodeOrNull();
      if( associatedOpenSimObjectNodeOrNull != null )
         ObjectDisplayShowHideBaseAction.ApplyOperationToNodeWithShowHide( associatedOpenSimObjectNodeOrNull, showOrHide, true ); 
   }


   //-----------------------------------------------------------------------------
   // When this property editor is re-displayed, show tab that was last viewed by the user.
   private   int            GetNumberOfTabbedPaneTabs( )                    { return myJTabbedPane == null ?  0 : myJTabbedPane.getTabCount(); }
   private   boolean        IsTabbedPaneIndexInRange( int tabIndex )        { return myJTabbedPane == null ? false : tabIndex < this.GetNumberOfTabbedPaneTabs(); }
   protected int            GetSelectedTabbedPaneIndex( )                   { return myJTabbedPane == null ? -1 : myJTabbedPane.getSelectedIndex(); }
   private   boolean        SetSelectedTabbedPaneFromIndex( int tabIndex )  { boolean okIndex = this.IsTabbedPaneIndexInRange(tabIndex);  if(okIndex) myJTabbedPane.setSelectedIndex(tabIndex);  return okIndex; } 
   protected boolean        SetSelectedTabbedPaneFromPriorUserSelection( )  { return this.SetSelectedTabbedPaneFromIndex( this.GetPriorUserSelectedTabbedPaneIndexVirtualFunction() ); } 
   protected abstract int   GetPriorUserSelectedTabbedPaneIndexVirtualFunction();
   protected abstract void  SetPriorUserSelectedTabbedPaneIndexVirtualFunction();
   private   boolean        myStateChangeCalledByUserChangingPanesNotClassConstructor = false;

   //-----------------------------------------------------------------------------
   // Class data
   private   LSPropertyTalkToSimbody	 myPropertyTalkToSimbody;
   private   String                      myNameOfTypeOfObjectEgRigidBodyOrJoint;
   private   ModelWindowVTKTopComponent  myOpenSimModelWindowVTKTopComponentOwnerWindow;
   protected JTabbedPane                 myJTabbedPane; 
}



