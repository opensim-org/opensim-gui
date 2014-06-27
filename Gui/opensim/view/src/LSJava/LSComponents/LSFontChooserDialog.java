//--------------------------------------------------------------------------
// File:     LSFontChooserDialog.java
// Parent:   LSDialog
// Purpose:  Dialog box for font family, font style, and font size
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
import  java.awt.event.*;


//--------------------------------------------------------------------------
public class LSFontChooserDialog extends LSDialog implements ActionListener, ItemListener
{
   // Constructor ---------------------------------------------------------
   public LSFontChooserDialog( Frame ownerFrame, boolean trueForAllDisplaybleFontFamilysFalseForJavaBuiltInsOnly, boolean redrawDialogWhenFontChanges )
   {
      super( ownerFrame, "Font", true, false );
      this.LSFontChooserDialogConstructorHelper( trueForAllDisplaybleFontFamilysFalseForJavaBuiltInsOnly ? LSFont.GetDisplayableLocalFontFamilyNames() : LSFont.GetJavaBuiltInFontFamilyNames(), LSFont.GetFontStyleNames(), LSFont.GetFontSizeNames(), LSFont.GetUserFont(), redrawDialogWhenFontChanges );
      // this.LSFontChooserDialogConstructorHelper( LSFont.GetJavaBuiltInFontFamilyNames(), LSFont.GetFontStyleNames(), LSFont.GetFontSizeNames(), LSFont.GetUserFont(), redrawDialogWhenFontChanges );
   }

   // Constructor ---------------------------------------------------------
   public LSFontChooserDialog( Frame ownerFrame, LSStringList fontFamilyNames, LSStringList fontStyleNames, LSStringList fontSizeNames, LSFont fontSelected, boolean redrawDialogWhenFontChanges )
   {
      super( ownerFrame, "Font", true, false );
      this.LSFontChooserDialogConstructorHelper( fontFamilyNames, fontStyleNames, fontSizeNames, fontSelected, redrawDialogWhenFontChanges );
   }

   // Constructor ---------------------------------------------------------
   public LSFontChooserDialog( Dialog ownerDialog, LSStringList fontFamilyNames, LSStringList fontStyleNames, LSStringList fontSizeNames, LSFont fontSelected, boolean redrawDialogWhenFontChanges )
   {
      super( ownerDialog, "Font", true, false );
      this.LSFontChooserDialogConstructorHelper( fontFamilyNames, fontStyleNames, fontSizeNames, fontSelected, redrawDialogWhenFontChanges );
   }

   // Constructor helper  --------------------------------------------------
   private void  LSFontChooserDialogConstructorHelper( LSStringList fontFamilyNames, LSStringList fontStyleNames, LSStringList fontSizeNames, LSFont fontSelected, boolean redrawDialogWhenFontChanges )
   {
      myContainer.SetConstraintFill( GridBagConstraints.BOTH ); // BOTH makes each component occupy full width of space
      myContainer.SetConstraintInsets( 4, 6, 2, 6 );            // padding on top, left, bottom, right

      // Titles, e.g., Font family, font style, font size,
      myContainer.AddLabelToLayout1Wide1High( "Font Family", LSLabel.LEFT );
      myContainer.AddLabelToLayout1Wide1High( "Font Style",  LSLabel.LEFT );
      myContainer.AddLabelToLayoutRowRemainder1High( "Font Size", LSLabel.LEFT );

      // Layout.
      myContainer.SetConstraintWeightX( 1.0 );
      myContainer.SetConstraintWeightY( 1.0 );
      myContainer.SetConstraintWeightX( 0.0 );
      myContainer.SetConstraintWeightY( 0.0 );

      // Number of visible strings in each list (good idea to set it to maximum number of possible font sizes).
      int numberOfVisibleRowsInList = 14;

      // Font Family Name, e.g., Courier, Helvetica, Ariel
      myListOfFontFamilyNames = new LSList( numberOfVisibleRowsInList, false, myContainer, 1, 1, this, this );
      int numberOfFontFamilyNames = fontFamilyNames.GetSizeOfArrayList();
      for( int i=0;  i<numberOfFontFamilyNames;  i++ )
         myListOfFontFamilyNames.AddStringToList( fontFamilyNames.GetStringAtIndex(i) );
      int selectedFontFamilyItem = fontFamilyNames.GetFirstIndexOfStringInStringListOrReturnAlternateValueCaseInsensitive( fontSelected.GetFontName(), 0 );
      myListOfFontFamilyNames.SelectIndex( selectedFontFamilyItem );

      // Font style, e.g, Plain or Bold
      myListOfFontStyles = new LSList( numberOfVisibleRowsInList, false, myContainer, 1, 1, this, this );
      int numberOfFontStyleNames = fontStyleNames.GetSizeOfArrayList();
      for( int i=0;  i<numberOfFontStyleNames;  i++ )
         myListOfFontStyles.AddStringToList( fontStyleNames.GetStringAtIndex(i) );
      int selectedFontStyleItem = LSFont.GetIndexOfFontStyleNameFromIntegerOrReturnAlternate( fontSelected.GetFontStyle(), 0 );
      myListOfFontStyles.SelectIndex( selectedFontStyleItem );

      // Font sizes, e.g, 8pt, 9pt, 10pt, 11pt, 12pt, 14pt, ...
      myListOfFontSizes = new LSList( numberOfVisibleRowsInList, false, myContainer,GridBagConstraints.REMAINDER, 1, this, this );
      int numberOfFontSizeNames = fontSizeNames.GetSizeOfArrayList();
      for( int i=0;  i<numberOfFontSizeNames;  i++ )
         myListOfFontSizes.AddStringToList( fontSizeNames.GetStringAtIndex(i) );
      String userFontSizeAsString =  LSString.GetStringFromInteger(  fontSelected.GetFontSize()  );
      int selectedFontSizeItem = fontSizeNames.GetFirstIndexOfStringInStringListOrReturnAlternateValueCaseInsensitive( userFontSizeAsString, 5 );
      myListOfFontSizes.SelectIndex( selectedFontSizeItem );

      // Apply and Cancel buttons
      LSPanel panelOfButtons = new LSPanel( myContainer, GridBagConstraints.REMAINDER, 1 );
      this.CreateButtonPanel( panelOfButtons.GetPanelAsContainer() );

      // Decide whether or not to redraw dialog when the user changes font selection
      myRedrawDialogWhenFontChanges = redrawDialogWhenFontChanges;

      // Display the window
      myContainer.PackLocateShow();
   }

   //-------------------------------------------------------------------------
   public void  actionPerformed( ActionEvent actionEvent )  {  this.EventProcess( actionEvent.getSource() );  }
   public void  itemStateChanged( ItemEvent itemEvent )     {  this.EventProcess( itemEvent.getSource() );  }


   //-------------------------------------------------------------------------
   public LSFont  GetFontOrNullFromFontChooserDialog( )  { return myUserChosenFontOrNull; }


   //-------------------------------------------------------------------------
   private void  EventProcess( Object eventTarget )
   {
      if(      eventTarget == myApplyButton )   { myUserChosenFontOrNull = this.GetFontFromDialog();  super.Dispose(); }
      else if( eventTarget == myCancelButton )  { super.Dispose(); }
      else if( myRedrawDialogWhenFontChanges )
      {
         boolean needToResizeWindow = (eventTarget == myListOfFontSizes);
         myContainer.RepaintWindowAndAllComponentsWithNewColorAndFonts( LSColor.GetUserBackgroundColor(), LSColor.GetUserForegroundColor(), this.GetFontFromDialog(), needToResizeWindow );
      }
   }


   //-------------------------------------------------------------------------
   private LSFont  GetFontFromDialog( )
   {
      String fontFamilyName = myListOfFontFamilyNames.GetSelectedItemAsString();
      int fontStyle =  this.GetSelectedFontStyleAsInteger();
      int fontSize = LSInteger.GetIntegerFromString( myListOfFontSizes.GetSelectedItemAsString() );
      return new LSFont( fontFamilyName, fontStyle, fontSize );
   }

   //-------------------------------------------------------------------------
   private int  GetSelectedFontStyleAsInteger( )
   {
      int fontStyleIndex = myListOfFontStyles.GetSelectedIndex();
      return LSFont.GetFontStyleForIndexOrReturnAlternate( fontStyleIndex, LSFont.myDefaultFontStyle );
   }


   //-------------------------------------------------------------------------
   private void  CreateButtonPanel( LSContainer container )
   {
      container.SetConstraintFill( GridBagConstraints.VERTICAL );
      myApplyButton  = new LSButton( "Apply",  container, 1, 1, this, null );
      myCancelButton = new LSButton( "Cancel", container, 1, 1, this, null );
      container.SetConstraintFill( GridBagConstraints.BOTH );
   }


   // Class variables --------------------------------------------------------
   // Buttons at the bottom of screen
   private LSButton  myApplyButton;
   private LSButton  myCancelButton;

    // Font information
   private LSList  myListOfFontFamilyNames; // Courier, Helvetica, Ariel, etc.
   private LSList  myListOfFontStyles;      // PLAIN, BOLD, ITALIC or BOLD&ITALIC
   private LSList  myListOfFontSizes;       // Font size, e.g., 10pt, 12pt, 14pt, etc.
   private LSFont  myUserChosenFontOrNull;  // returned quantity (or null)
   private boolean myRedrawDialogWhenFontChanges;
}
