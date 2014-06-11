//--------------------------------------------------------------------------
// File:     LSContainer.java
// Parent:   None
// Purpose:  Generic container class for adding, removing, packing, painting, color, and font for components
// Authors:  John Mitiguy and Paul Mitiguy (2001-2010).
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
package LSJava.LSComponents;
import  LSJava.LSUtility.*;
import  java.awt.*;


//--------------------------------------------------------------------------
public class LSContainer
{
   // Constructor ---------------------------------------------------------
   public LSContainer( Container container )
   {
      if( container == null ) LSMessageDialog.DebugMessage( "Invalid argument  null container  passed to LSContainer constructor" );
      myContainer = container;
      myGridBagLayout = new GridBagLayout();
      myGridBagConstraints = new GridBagConstraints();
      myGridBagConstraints.fill = GridBagConstraints.BOTH;     // BOTH makes each component occupy full width of space
      myGridBagConstraints.anchor = GridBagConstraints.CENTER; // options are NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST,, NORTHWEST
      myGridBagConstraints.gridwidth  = 1;
      myGridBagConstraints.gridheight = 1;
      myContainer.setLayout( myGridBagLayout );
      this.SetContainerFontDefault();
      this.SetContainerBackgroundColorDefault();
      this.SetContainerForegroundColorDefault();
   }


   //-------------------------------------------------------------------------
   public Container  GetContainer( )  { return myContainer; }
   public boolean    IsShowing( )     { return myContainer.isShowing(); }
   public boolean    IsDialog( )      { return myContainer instanceof Dialog; }
   public boolean    IsFrame( )       { return myContainer instanceof Frame; }
   public boolean    IsPanel( )       { return myContainer instanceof Panel; }
   public boolean    IsWindow( )      { return myContainer instanceof Window; }

   //-------------------------------------------------------------------------
   private Window  GetDialogOwnerOrNull( )  { return this.IsDialog() ? ((Dialog)myContainer).getOwner() : null; }
   private Window  GetWindowOwnerOrNull( )  { return this.IsWindow() ? ((Window)myContainer).getOwner() : null; }

   //----------------------------------------------------------------------
   public Window  GetParentWindowOrNull( )  { return LSContainer.GetParentWindowOrNull(myContainer); }
   public Dialog  GetParentDialogOrNull( )  { return LSContainer.GetParentDialogOrNull(myContainer); }
   public Frame   GetParentFrameOrNull( )   { return LSContainer.GetParentFrameOrNull( myContainer); }
   
   //---------------------------------------------------------------------- 
   public static Window  GetParentWindowOrNull( Container c )  { while( c != null  &&  (c instanceof Window) == false ) c = c.getParent();  return c==null ? null : (Window)c; }
   public static Dialog  GetParentDialogOrNull( Container c )  { while( c != null  &&  (c instanceof Dialog) == false ) c = c.getParent();  return c==null ? null : (Dialog)c; }
   public static Frame   GetParentFrameOrNull(  Container c )  { while( c != null  &&  (c instanceof Frame)  == false ) c = c.getParent();  return c==null ? null :  (Frame)c; }


   //-------------------------------------------------------------------------
   private boolean  IsDialogOwnerShowing( )      { Window w = this.GetDialogOwnerOrNull();  return w!=null && w.isShowing(); }
   private int      GetWindowOwnerWidth( )       { Window w = this.GetWindowOwnerOrNull();  return w!=null ? w.getWidth() : 0; }
   private int      GetWindowOwnerHeight( )      { Window w = this.GetWindowOwnerOrNull();  return w!=null ? w.getHeight() : 0; }
   private int      GetDialogOwnerWidthOrDefault(  int defaultX )             { return this.IsDialogOwnerShowing() ? this.GetWindowOwnerWidth()  : defaultX; }
   private int      GetDialogOwnerHeightOrDefault( int defaultY )             { return this.IsDialogOwnerShowing() ? this.GetWindowOwnerHeight() : defaultY; } 
   private int      GetDialogOwnerXLocationOnScreenOrDefault( int defaultX )  { return this.IsDialogOwnerShowing() ? this.GetDialogOwnerOrNull().getLocationOnScreen().x : defaultX; }
   private int      GetDialogOwnerYLocationOnScreenOrDefault( int defaultY )  { return this.IsDialogOwnerShowing() ? this.GetDialogOwnerOrNull().getLocationOnScreen().y : defaultY; }
   
   //-------------------------------------------------------------------------
   public void  SetConstraintInsets( int top, int left, int bottom, int right )  { myGridBagConstraints.insets = new Insets(top,left,bottom,right); }
   public void  SetConstraintAnchor( int location )                              { myGridBagConstraints.anchor = location; }
   public void  SetConstraintFill( int fillType )                                { myGridBagConstraints.fill = fillType; }
   public void  SetConstraintGridWidth(  int gridWidth )                         { myGridBagConstraints.gridwidth  = gridWidth;  }
   public void  SetConstraintGridHeight( int gridHeight )                        { myGridBagConstraints.gridheight = gridHeight; }
   public void  SetConstraintWeightX( double weightX )                           { myGridBagConstraints.weightx = weightX; }
   public void  SetConstraintWeightY( double weightY )                           { myGridBagConstraints.weighty = weightY; }
   public void  SetConstraints( Component component )                            { myGridBagLayout.setConstraints( component, myGridBagConstraints ); }
   public void  SetConstraintPadXWidthExtraPixels(  int numberOfPixelsToPadX )   { myGridBagConstraints.ipadx = numberOfPixelsToPadX; }
   public void  SetConstraintPadYHeightExtraPixels( int numberOfPixelsToPadY )   { myGridBagConstraints.ipady = numberOfPixelsToPadY; }

   //-------------------------------------------------------------------------
   public int                 GetConstraintGridX( )      { return myGridBagConstraints.gridx; }
   public int                 GetConstraintGridY( )      { return myGridBagConstraints.gridy; }
   public Insets              GetConstraintInsets( )     { return myGridBagConstraints.insets; }
   public int                 GetConstraintAnchor( )     { return myGridBagConstraints.anchor; }
   public int                 GetConstraintFill( )       { return myGridBagConstraints.fill; }
   public int                 GetConstraintGridWidth( )  { return myGridBagConstraints.gridwidth; }
   public int                 GetConstraintGridHeight( ) { return myGridBagConstraints.gridheight; }
   public int                 GetConstraintPadX( )       { return myGridBagConstraints.ipadx; }
   public int                 GetConstraintPadY( )       { return myGridBagConstraints.ipady; }
   public double              GetConstraintWeightX( )    { return myGridBagConstraints.weightx; }
   public double              GetConstraintWeightY( )    { return myGridBagConstraints.weighty; }
   public GridBagConstraints  GetConstraintsClone( )     { return (GridBagConstraints)(myGridBagConstraints.clone()); }

   // ---------------------------------------------------------------------
   public int  GetContainerWidth( )                { return myContainer.getWidth(); }
   public int  GetContainerHeight( )               { return myContainer.getHeight(); }
   public int  GetContainerXLocationOnScreen( )    { return this.IsShowing() ? myContainer.getLocationOnScreen().x : 0; }
   public int  GetContainerYLocationOnScreen( )    { return this.IsShowing() ? myContainer.getLocationOnScreen().y : 0; }

   // ---------------------------------------------------------------------
   // Note: Take into account that Windows 95/98/2000/XP has toolbars (usually at bottom) when getting screen height (or width)
   // ---------------------------------------------------------------------
   static public int  GetScreenWidth(  double scaleX )  { return (int)(( java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth()  ) * scaleX); }
   static public int  GetScreenHeight( double scaleY )  { return (int)(( java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight() ) * scaleY); }

   //----------------------------------------------------------------------
   private void  SetContainerFont( Font font )               { if( font != null )  myContainer.setFont( font ); }
   public  void  SetContainerBackgroundColor( Color color )  { if( color != null ) myContainer.setBackground( color ); }
   private void  SetContainerForegroundColor( Color color )  { if( color != null ) myContainer.setForeground( color ); }
   private void  SetContainerFontDefault( )                  { this.SetContainerFont( LSFont.GetUserFont() ); }
   private void  SetContainerBackgroundColorDefault( )       { this.SetContainerBackgroundColor( this.GetParentDialogOrNull() != null ? LSColor.GetBackgroundColorDialog() : LSColor.GetUserBackgroundColor() ); }
   private void  SetContainerForegroundColorDefault( )       { this.SetContainerForegroundColor( LSColor.GetUserForegroundColor() ); }

   //----------------------------------------------------------------------
   public boolean  AddComponent( Component component )       { if( component == null ) return false;   myContainer.add(component); return true; }
   public boolean  AddComponentIfNew( Component component )  { return this.IsComponentInContainer(component) ? false : this.AddComponent(component); }
   public void     RemoveComponent( Component component )    { if( component != null )  myContainer.remove(component); }
   public void     RemoveAllComponents( )                    { myContainer.removeAll(); }

   // ---------------------------------------------------------------------
   public void   Repaint( )                  { myContainer.repaint(); }
   public void   PackShow( )                 { this.Pack();  this.Show(); }
   public void   PackLocateShow( )           { this.Pack();  this.SetLocationAlignment(0.5,0.25);  this.Show(); }
   public void   PackLocateShowOrRepaint( )  { if( !this.IsShowing() ) this.PackLocateShow();  else { this.Pack(); this.SetLocationAlignmentBeforeRepaint(); this.Repaint();}  }

   // ---------------------------------------------------------------------
   public void  Pack( )  // Sets the window to the preferred size
   {
      if(      this.IsDialog() )  { ((Dialog)myContainer).pack(); }
      else if( this.IsFrame()  )  {  ((Frame)myContainer).pack(); }
   }

   // ---------------------------------------------------------------------
   public void  Show( )
   {
      if(      this.IsDialog() )  { ((Dialog)myContainer).setVisible(true); }
      else if( this.IsFrame()  )  {  ((Frame)myContainer).setVisible(true); }
   }


   // ---------------------------------------------------------------------
   public int        GetNumberOfComponents( )                       { return myContainer.getComponentCount(); }
   public Component  GetComponent( int i )                          { return myContainer.getComponent(i); }
   public boolean    IsComponentInContainer( Component component )  { return this.GetComponentNumber(component) >= 0; }


   // ---------------------------------------------------------------------
   private int   GetComponentNumber( Component component )
   {
      int numberOfComponents = this.GetNumberOfComponents();
      for( int i=0;  i<numberOfComponents;  i++ )
         if( component == this.GetComponent(i) ) return i;
      return -1;
   }

   //-------------------------------------------------------------------------
   public void  AddComponentToLayoutRowRemainder1High( Component component ) { this.AddComponentToLayout( component, GridBagConstraints.REMAINDER, 1 ); }
   public void  AddComponentToLayoutColRemainder1Wide( Component component ) { this.AddComponentToLayout( component, 1, GridBagConstraints.REMAINDER ); } 
   public void  AddComponentToLayout1Wide1High( Component component )        { this.AddComponentToLayout( component, 1, 1 ); } 
   public void  AddComponentToLayout( Component component, int gridWidth, int gridHeight )
   {
      this.SetConstraintGridWidth(  gridWidth  );  // Note: Use gridWidth  = GridBagConstraints.REMAINDER  for remainder of row.
      this.SetConstraintGridHeight( gridHeight );  // Note: Use gridHeight = GridBagConstraints.REMAINDER  for remainder of column.
      this.AddComponentToLayout( component, myGridBagConstraints );
   }


   // ---------------------------------------------------------------------
   public void  AddComponentToLayout( Component component, GridBagConstraints newConstraints )
   {
      GridBagConstraints temp = myGridBagConstraints;
      myGridBagConstraints = newConstraints;
      this.SetConstraints( component );
      this.AddComponent( component );
      myGridBagConstraints = temp;
   }

   //-------------------------------------------------------------------------
   public  LSLabel  AddLabelToLayout( String labelText, int labelLocation, int gridWidth, int gridHeight )  { return new LSLabel( labelText, labelLocation, this, gridWidth, gridHeight ); }
   public  LSLabel  AddLabelToLayoutRow( String labelText, int labelLocation, int gridWidth )               { return this.AddLabelToLayout( labelText, labelLocation, gridWidth, 1 ); }
   public  LSLabel  AddLabelToLayout1Wide1High( String labelText, int labelLocation )                       { return this.AddLabelToLayout( labelText, labelLocation, 1, 1 ); }
   public  LSLabel  AddLabelToLayoutRowRemainder1High( String labelText, int labelLocation )                { return this.AddLabelToLayout( labelText, labelLocation, GridBagConstraints.REMAINDER, 1); }
   public  LSLabel  AddBlankLabelToLayout1Wide1High( )                                                      { return this.AddLabelToLayout(       " ", LSLabel.LEFT,           1,           1 ); }
   public  LSLabel  AddBlankLabelToLayout( int gridWidth, int gridHeight )                                  { return this.AddLabelToLayout(       " ", LSLabel.LEFT,    gridWidth, gridHeight ); }
   public  LSLabel  AddBlankLabelToLayoutRowRemainder1High( )                                               { return this.AddLabelToLayoutRowRemainder1High( " ", LSLabel.LEFT ); }
   public  void     AddBlankLabelToLayoutXWide1High( int numberOfBlankLabels )                              { for( int i=0;  i< numberOfBlankLabels;  i++ )  this.AddBlankLabelToLayout1Wide1High(); }
   public  void     AddDividerLine( int numberOfCharacters )                                                { this.AddDividerLineWithSpecifiedCharacter( numberOfCharacters,      '-' ); }
   public  void     AddBlankLineAsString( int numberOfBlankCharacters )                                     { this.AddDividerLineWithSpecifiedCharacter( numberOfBlankCharacters, ' ' ); }
   public  void     AddBlankLine( int numberOfLines )                                                       { for( int i=0;  i<numberOfLines;  i++ )  this.AddBlankLine(); }
   private void     AddBlankLine( )                                                                         { this.AddLabelToLayoutRowRemainder1High( " ", LSLabel.CENTER ); }


   //-------------------------------------------------------------------------
   public  void  AddBlankLinesDividerLineBlankLines( int precedeBlankLines, int numberOfCharacters, int followBlankLines )  
   { 
      this.AddBlankLine( precedeBlankLines );  
      this.AddDividerLine(numberOfCharacters);  
      this.AddBlankLine( followBlankLines ); 
   }


   //-------------------------------------------------------------------------
   public void  AddDividerLineWithSpecifiedCharacter( int numberOfCharacters, char dividerCharacter )
   {
      StringBuffer dividerLineAsStringBuffer =  LSStringBuffer.GetStringBufferInitializedToCharacter( numberOfCharacters, dividerCharacter );
      String dividerLineAsString = LSString.GetStringFromStringBuffer( dividerLineAsStringBuffer );
      LSLabel labelAdded = this.AddLabelToLayoutRowRemainder1High( dividerLineAsString, LSLabel.CENTER );
      LSFont labelAddedFont = new LSFont( "Monospaced", LSFont.PLAIN, LSFont.GetUserFontSize() );
      labelAdded.SetLabelFont( labelAddedFont );
   }
   

   //-------------------------------------------------------------------------
   public LSPanel  AddStringToContainerReturnPanel( String blockOfText, int labelLocation, LSFont font )
   {
      if( LSString.IsStringEmptyOrNull(blockOfText) ) return null;
      LSStringList messages = new LSStringList( blockOfText );
      return this.AddStringListToContainerReturnPanel( messages, labelLocation, font );
   }


   //-------------------------------------------------------------------------
   public LSPanel  AddStringListToContainerReturnPanel( LSStringList messages, int labelLocation, LSFont font )
   {
      if( messages.GetSizeOfArrayList() == 0 ) return null;
      LSPanel panel = new LSPanel( this, GridBagConstraints.REMAINDER, 1 );
      LSContainer container = panel.GetPanelAsContainer();
      container.AddStringListAsLabelsToContainer( messages, labelLocation, font );
      return panel;
   }


   //-------------------------------------------------------------------------
   private void  AddStringListAsLabelsToContainer( LSStringList messages, int labelLocation, LSFont font )
   {
      this.SetConstraintInsets( 0, 0, 0, 0 );  // Padding on top, left, bottom, right
      this.SetConstraintFill( GridBagConstraints.HORIZONTAL );
      int numberOfMessages = messages.GetSizeOfArrayList();
      for( int i=0;  i<numberOfMessages;  i++ )
      {
         String msgi = messages.GetStringAtIndex(i);
         if( LSString.IsStringEmptyOrNull(msgi) )  this.AddBlankLine(1);
         else
         {
            LSLabel label = this.AddLabelToLayoutRowRemainder1High( msgi, labelLocation );
            label.SetLabelFont( font );
         }
      }
   }


   //-------------------------------------------------------------------------
   private void SetLocationOnScreen( int xLocationOnScreen, int yLocationOnScreen )
   {
      if( false && this.IsDialogOwnerShowing() )
      {
         int ownerXLocationOnScreen = this.GetDialogOwnerXLocationOnScreenOrDefault( 0 );
         int ownerYLocationOnScreen = this.GetDialogOwnerYLocationOnScreenOrDefault( 0 );
         xLocationOnScreen -= ownerXLocationOnScreen;
         yLocationOnScreen -= ownerYLocationOnScreen;
      }
      myContainer.setLocation( xLocationOnScreen, yLocationOnScreen );
   }


   //-------------------------------------------------------------------------
   private void  SetLocationAlignment( double xAlignment, double yAlignment )
   {
      // Dimensions of this container and user's computer screen
      int childWidth   = this.GetContainerWidth();
      int childHeight  = this.GetContainerHeight();
      int screenWidth  = this.GetScreenWidth(  1.00 );   // Width  of user's computer screen (no toolbars on side)
      int screenHeight = this.GetScreenHeight( 0.95 );   // Height of user's computer screen (accounts for toolbars at bottom)

      // Default values locate this at the top-left hand corner of the user's computer screen
      // Try to align dialog boxes within the parent's frame
      int ownerWidth  = this.GetDialogOwnerWidthOrDefault(  screenWidth );
      int ownerHeight = this.GetDialogOwnerHeightOrDefault( screenHeight );
      int ownerXLocationOnScreen = this.GetDialogOwnerXLocationOnScreenOrDefault( 0 );
      int ownerYLocationOnScreen = this.GetDialogOwnerYLocationOnScreenOrDefault( 0 );

      int xTopLeftPixel = this.GetTopLeftPixelLocation( childWidth,  screenWidth,  xAlignment, ownerXLocationOnScreen, ownerWidth );
      int yTopLeftPixel = this.GetTopLeftPixelLocation( childHeight, screenHeight, yAlignment, ownerYLocationOnScreen, ownerHeight );
      this.SetLocationOnScreen( xTopLeftPixel, yTopLeftPixel );
   }


   //-------------------------------------------------------------------------
   private void  SetLocationAlignmentBeforeRepaint( )
   {
      // This method should not be called if it is currently showing
      if( this.IsShowing() == false ) return;

      // If child exceeds width of screen, center on Dialog owner (if showing)
      int screenWidth = this.GetScreenWidth(1.00);   // Width of user's computer screen (no toolbars on side)
      int childWidth  = this.GetContainerWidth();   
      int childXLocationOnScreen = this.GetContainerXLocationOnScreen();
      boolean tooFarRight = ( childXLocationOnScreen + childWidth > screenWidth );
      if( childXLocationOnScreen < 0 )   { if( !tooFarRight ) childXLocationOnScreen = 0; }
      else if( tooFarRight )             childXLocationOnScreen = screenWidth - childWidth;
   
      // If child exceeds height of screen, put near top of Dialog owner (if showing)
      int screenHeight = this.GetScreenHeight(0.95); // Height of user's computer screen (accounts for toolbars at bottom)
      int childHeight  = this.GetContainerHeight();  
      int childYLocationOnScreen = this.GetContainerYLocationOnScreen();
      boolean tooFarDown = ( childYLocationOnScreen + childHeight > screenHeight );
      if( childYLocationOnScreen < 0 )   { if( !tooFarDown ) childYLocationOnScreen = 0; }
      else if( tooFarDown )              childYLocationOnScreen = screenHeight - childHeight; 
   
      // Only reset location is dialog was too wide or high
      if( childXLocationOnScreen != this.GetContainerXLocationOnScreen() || childYLocationOnScreen != this.GetContainerYLocationOnScreen() )
         this.SetLocationOnScreen( childXLocationOnScreen, childYLocationOnScreen );
   }


   //-------------------------------------------------------------------------
   private static int  GetTopLeftPixelLocation( int childDimension, int screenDimension, double alignment, int ownerLocation, int ownerDimension )
   {
      // If child is wider/higher than screen, put it to far left/top
      if( childDimension > screenDimension ) return 0;

      // Try to center the child within the parent
      int paddingForBothSides = ownerDimension - childDimension;
      int indent = (int)(alignment * paddingForBothSides);

      // Check to ensure that parent is not located too far left initially
      if( ownerLocation + indent <= 0 ) return 0;

      // Check to ensure that parent is not located too far right initially
      if( ownerLocation + indent + childDimension >= screenDimension ) return screenDimension - childDimension;

      return ownerLocation + indent;
   }


   //-------------------------------------------------------------------------
   public void  SetEditableAndBackgroundColorOfTextFields( boolean enable, LSTextField textFields[] )
   {
      for( int i=0;  i < textFields.length;  i++ )  textFields[i].SetTextFieldEditableAndBackgroundColorOfTextField( enable );
   }


   //-------------------------------------------------------------------------
   public void  RepaintWindowAndAllComponentsWithNewColorAndFonts( LSColor backgroundColor, LSColor foregroundColor, LSFont font, boolean resize )
   {
      // First change the font and the back/foreground color on the current window
      this.SetContainerFont( font );
      this.SetContainerBackgroundColor( backgroundColor );
      this.SetContainerForegroundColor( foregroundColor );
      this.RepaintAllComponentsWithColorAndFont( backgroundColor, foregroundColor, font );
      if( resize == true ) this.Pack();
      this.Repaint();
   }


   //-------------------------------------------------------------------------
   private void  RepaintAllComponentsWithColorAndFont( LSColor backgroundColor, LSColor foregroundColor, LSFont font )
   {
      // Change the font and the back/foreground color of each component in the window
      int numberOfComponents = this.GetNumberOfComponents();
      for( int i=0;  i<numberOfComponents;  i++ )
      {
         Component component = this.GetComponent( i );
         component.setFont( font );
         if( component instanceof LSLabel )
            component.setBackground( backgroundColor );
         else if( component instanceof LSPanel )
         {
            // Recursively paint all the components in sub-containers.
            component.setBackground( backgroundColor ); 
            LSPanel subPanel = (LSPanel)component;
            LSContainer subPanelContainer = subPanel.GetPanelAsContainer();
            subPanelContainer.RepaintAllComponentsWithColorAndFont( backgroundColor, foregroundColor, font );
         }
         component.setForeground( foregroundColor );
      }
   }


   // Class variables -----------------------------------------------------
   private  Container          myContainer;
   private  GridBagLayout      myGridBagLayout;
   private  GridBagConstraints myGridBagConstraints;

}
