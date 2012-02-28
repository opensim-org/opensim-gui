//--------------------------------------------------------------------------
// File:     LSTextField.java
// Parent:   TextField
// Purpose:  TextFields for user interface
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
import  java.awt.event.*;
import  java.awt.*;


//--------------------------------------------------------------------------
public class LSTextField extends TextField
{
   // Constructor ---------------------------------------------------------
   public LSTextField( int    initialValue,  int textWidth, boolean isEditable, LSContainer container, int gridWidth, int gridHeight, ActionListener actionListenerOrNull, FocusListener focusListenerOrNull, KeyListener keyListenerOrNull )   { this( LSString.GetStringFromInteger(initialValue), textWidth==0 ? LSInteger.GetNumberOfDigits(initialValue) + 2 : textWidth, isEditable, container, gridWidth, gridHeight, actionListenerOrNull, focusListenerOrNull, keyListenerOrNull ); }
   public LSTextField( double initialValue,  int textWidth, boolean isEditable, LSContainer container, int gridWidth, int gridHeight, ActionListener actionListenerOrNull, FocusListener focusListenerOrNull, KeyListener keyListenerOrNull )   { this( LSString.GetStringFromDouble(initialValue),  textWidth==0 ? LSDouble.GetMaxNumberOfCharsInTextFieldsForDoublePrecisionNumber() : textWidth, isEditable, container, gridWidth, gridHeight, actionListenerOrNull, focusListenerOrNull, keyListenerOrNull ); }
   public LSTextField( String initialString, int textWidth, boolean isEditable, LSContainer container, int gridWidth, int gridHeight, ActionListener actionListenerOrNull, FocusListener focusListenerOrNull, KeyListener keyListenerOrNull )
   {
      super( initialString, textWidth );

      this.SetTextFieldEditableAndColorOfTextField( isEditable );

      // Keep a copy of the constraints for adding and removing this component to frames, dialogs, panels, ...
      myConstraintsWhenCreated = container.GetConstraintsClone();
      myConstraintsWhenCreated.gridwidth  = gridWidth;
      myConstraintsWhenCreated.gridheight = gridHeight;
      container.AddComponentToLayout( this, myConstraintsWhenCreated );

      // ActionEvent occurs whenever the user hits the ENTER key.
      if( actionListenerOrNull != null )
         super.addActionListener( actionListenerOrNull );

      // FocusEvent occurs whenever the user hits the TAB key or leaves the field to edit a different field.
      if( focusListenerOrNull != null )
         super.addFocusListener( focusListenerOrNull );

      // Respond to user typing certain keys, e.g., pressing OK or ESCAPE.
      if( keyListenerOrNull != null )
         super.addKeyListener( keyListenerOrNull );
   }


   // ------------------------------------------------------------------------
   public void  RequestFocus( )  { super.requestFocus(); }

   // ------------------------------------------------------------------------
   public void  SetTextFieldFont( Font font )                               { if( font != null ) super.setFont(font); }
   public void  SetTextFieldSameFontExceptBoldOrUnbold( boolean makeBold )  { Font font = LSFont.GetSameFontExceptBoldOrUnbold( super.getFont(), makeBold );  this.SetTextFieldFont(font); }

   //-------------------------------------------------------------------------
   public String  GetTextFieldAsString( )                                              { return super.getText(); }
   public int     GetTextFieldAsInteger()                                              { return LSInteger.GetIntegerFromString( this.GetTextFieldAsString() ); }
   public int     GetTextFieldValidIntegerOrAlternate( int alternate )                 { return LSInteger.GetValidIntegerFromString( this.GetTextFieldAsString(), alternate ); }
   public int     GetTextFieldValidIntegerPositiveOrAlternate( int alternate )         { return LSInteger.GetValidIntegerPositive( this.GetTextFieldValidIntegerOrAlternate(alternate), alternate); }
   public int     GetTextFieldValidIntegerNonNegativeOrAlternate( int alternate )      { return LSInteger.GetValidIntegerNonNegative( this.GetTextFieldValidIntegerOrAlternate(alternate), alternate); }

   public boolean  IsTextFieldValidDouble()                                            { return LSDouble.IsValidDoubleFromString(            this.GetTextFieldAsString() ); }
   public boolean  IsTextFieldValidDoubleNonNegative()                                 { return LSDouble.IsValidDoubleNonNegativeFromString( this.GetTextFieldAsString() ); }
   public boolean  IsTextFieldValidDoubleNonPositive()                                 { return LSDouble.IsValidDoubleNonPositiveFromString( this.GetTextFieldAsString() ); }
   public boolean  IsTextFieldValidDoublePositive()                                    { return LSDouble.IsValidDoublePositiveFromString(    this.GetTextFieldAsString() ); }
   public boolean  IsTextFieldValidDoubleNegative()                                    { return LSDouble.IsValidDoubleNegativeFromString(    this.GetTextFieldAsString() ); }

   public double  GetTextFieldAsDouble()                                               { return LSDouble.GetDoubleFromString( this.GetTextFieldAsString() ); }
   public double  GetTextFieldAsValidDoubleOrAlternate( double alternate )             { return LSDouble.GetValidDoubleFromString( this.GetTextFieldAsString(), alternate ); }
   public double  GetTextFieldAsValidDoublePositiveOrAlternate( double alternate )     { return LSDouble.GetValidDoublePositive( this.GetTextFieldAsValidDoubleOrAlternate(alternate), alternate); }
   public double  GetTextFieldAsValidDoubleNonNegativeOrAlternate( double alternate )  { return LSDouble.GetValidDoubleNonNegative( this.GetTextFieldAsValidDoubleOrAlternate(alternate), alternate); }

   public boolean  IssueErrorMessageIfTextFieldIsBadDoublePrecisionNumber( Window ownerWindowOrNullForCurrentWindow ) { boolean isError = LSMessageDialog.IssueErrorMessageIfStringIsBadDoublePrecisionNumber( ownerWindowOrNullForCurrentWindow, this.GetTextFieldAsString() );  return this.SetTextFieldBackgroundColorSuggestingErrorTrueOrOKFalse(isError); }
   public boolean  IssueWarningMessageIfTextFieldIsNegativeNumber( Window ownerWindowOrNullForCurrentWindow )         { boolean isError = LSMessageDialog.IssueWarningMessageIfStringIsNegativeNumber( ownerWindowOrNullForCurrentWindow, this.GetTextFieldAsString() );          return this.SetTextFieldBackgroundColorSuggestingErrorTrueOrOKFalse(isError); }

   //-------------------------------------------------------------------------
   public void  SetTextFieldFromString( String s )  { super.setText(s); }
   public void  SetTextFieldFromInteger( int x )    { this.SetTextFieldFromString( LSString.GetStringFromInteger(x) ); }
   public void  SetTextFieldFromDouble( double d )  { this.SetTextFieldFromString( LSString.GetStringFromDouble(d)  ); }
   public void  SetTextFieldWithDoubleRoundedToNearestInteger( double d, double epsilonToRoundDoubleToNearestIntegerOr0 ) { this.SetTextFieldFromDouble( epsilonToRoundDoubleToNearestIntegerOr0 <= 0 ? d : LSDouble.RoundToNearestIntegerIfWithinEpsilon(d,epsilonToRoundDoubleToNearestIntegerOr0) ); }

   //-------------------------------------------------------------------------
   private void     SetTextFieldBackgroundColor( Color backgroundColor )                               { super.setBackground( backgroundColor ); }
   public  boolean  SetTextFieldBackgroundColorSuggestingErrorTrueOrOKFalse( boolean isError )         { this.SetTextFieldBackgroundColor( isError ? LSColor.BackgroundColorSuggestingError : LSColor.BackgroundColorSuggestingOK );  return isError; }
   private void     SetTextFieldEditable( boolean editable )                                           { super.setEditable(editable); }
   public  void     SetTextFieldEditableAndColorOfTextField( boolean enable )                          { this.SetTextFieldEditableAndBackgroundColor( enable,  enable ? LSColor.white : LSColor.PaulGray ); }
   public  void     SetTextFieldEditableAndBackgroundColor( boolean editable, Color backgroundColor )  { this.SetTextFieldEditable( editable );  this.SetTextFieldBackgroundColor( backgroundColor ); }

   // ------------------------------------------------------------------------
   public GridBagConstraints  GetConstraintsWhenCreated( )  { return myConstraintsWhenCreated; }

   // Class variables --------------------------------------------------------
   private GridBagConstraints  myConstraintsWhenCreated;
}
