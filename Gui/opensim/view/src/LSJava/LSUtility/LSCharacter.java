//--------------------------------------------------------------------------
// File:     LSCharacter.java
// Parent:   None
// Purpose:  Useful functions for characters
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
package LSJava.LSUtility;


//--------------------------------------------------------------------------
public class LSCharacter
{
   // Default constructor is private (disabled) since one should never be constructed.
   private LSCharacter( )   {;}


   //-------------------------------------------------------------------------
   public static boolean  IsDelimiter( char c )
   {
      switch( c )
      {
         case '&':
         case ',':
         case ';':   return true;
      }
      return false;
   }


   //-------------------------------------------------------------------------
   public static boolean  IsLeftOrRightParen( char c )
   {
      switch( c )
      {
         case '(':
         case ')':   return true;
      }
      return false;
   }


   //-------------------------------------------------------------------------
   public static boolean  IsMathematicalOperator( char c )
   {
      switch( c )
      {
         case '+':
         case '*':
         case '/':
         case '^':   return true;
      }
      return false;
   }


   //-------------------------------------------------------------------------
   public static boolean  IsWhiteSpace( char c )
   {
      switch( c )
      {
         case '\0':
         case '\n':
         case '\r':
         case '\t':
         case ' ' :   return true;
      }
      return false;
   }


   //-------------------------------------------------------------------------
   public static boolean  IsWhiteSpaceMathematicalOperatorParenOrDelimiter( char c, boolean isMathematicalOperator, boolean isParen, boolean isDelimiter )
   {
      return LSCharacter.IsWhiteSpace(c) || (isMathematicalOperator && LSCharacter.IsMathematicalOperator(c)) || (isParen && LSCharacter.IsLeftOrRightParen(c)) || (isDelimiter && LSCharacter.IsDelimiter(c));
   }


   //-------------------------------------------------------------------------
   public static char  ToLowerCase( char c )  { return Character.toLowerCase(c); }
   public static char  ToUpperCase( char c )  { return Character.toUpperCase(c); }

   //-------------------------------------------------------------------------
   public static boolean  IsCommentCharacter( char c )  { return c=='#' || c=='%'; }
   public static boolean  IsDigit( char c )             { return Character.isDigit(c); }
   public static boolean  IsLetter( char c )            { return Character.isLetter(c); }
   public static boolean  IsLetterOrDigit( char c )     { return Character.isLetterOrDigit(c); }
   public static boolean  IsLetterDigitCommentWhiteSpaceMathematicalOperatorParenOrDelimiter( char c )  { return LSCharacter.IsLetterOrDigit(c) || LSCharacter.IsCommentCharacter(c) || LSCharacter.IsWhiteSpaceMathematicalOperatorParenOrDelimiter(c,true,true,true); }

   //-------------------------------------------------------------------------
   public static char     GetFirstDisplayableKeyboardCharacterOnAsciiChart( )   { return ' '; }
   public static char     GetLastDisplayableKeyboardCharacterOnAsciiChart()     { return '~'; }
   public static boolean  IsDisplayableKeyboardCharacteronAsciiChart( char c )  { return c >= LSCharacter.GetFirstDisplayableKeyboardCharacterOnAsciiChart() && c <= LSCharacter.GetLastDisplayableKeyboardCharacterOnAsciiChart(); } 

}
