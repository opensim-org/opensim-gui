//--------------------------------------------------------------------------
// File:     LSStringBuffer.java
// Parent:   None
// Purpose:  Useful functions for a string buffer
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
public class LSStringBuffer
{
   // Default constructor is private (disabled) since one should never be constructed.
   private LSStringBuffer( )   {;}

   //-------------------------------------------------------------------------
   public static char          GetFirstCharacterInStringBuffer( StringBuffer sb )                { return LSStringBuffer.GetCharacterAtIndex(sb,0); }
   public static char          GetLastCharacterInStringBuffer( StringBuffer sb )                 { int length = LSStringBuffer.GetLengthOfStringBuffer(sb);  return length > 0 ? LSStringBuffer.GetCharacterAtIndex(sb,length-1) : '\0'; }
   public static char          GetCharacterAtIndex( StringBuffer sb, int i )                     { return LSStringBuffer.IsIndexInStringBufferCapacity(sb,i) ? sb.charAt(i) : '\0'; }
   public static void          SetCharacterAtIndex( StringBuffer sb, int i, char c )             { if( LSStringBuffer.IsIndexInStringBufferCapacity(sb,i) )  sb.setCharAt(i,c); }
   public static void          SetLengthOfStringBuffer( StringBuffer sb, int i )                 { if( sb != null ) sb.setLength(i); }
   public static int           GetLengthOfStringBuffer( StringBuffer sb )                        { return  sb==null ? 0 : sb.length(); }
   public static int           GetCapacityOfStringBuffer( StringBuffer sb )                      { return  sb==null ? 0 : sb.capacity(); }
   public static void          ExpandCapacityOfStringBuffer( StringBuffer sb, int newCapacity )  { sb.ensureCapacity(newCapacity); }
   public static void          SetStringBufferBlank( StringBuffer sb )                           { LSStringBuffer.SetLengthOfStringBuffer( sb, 0 ); }
   public static void          AppendStringToStringBuffer( StringBuffer sb, String s )           { if( !LSString.IsStringEmptyOrNull(s) ) sb.append(s); }
   public static boolean       IsStringBufferEmpty( StringBuffer sb )                            { return  sb.length() == 0; }
   public static boolean       IsStringBufferEmptyOrNull( StringBuffer sb )                      { return  sb==null || sb.length() == 0; }
   public static boolean       IsIndexInStringBufferCapacity( StringBuffer sb, int i )           { return sb!=null  &&  i>=0  && i < LSStringBuffer.GetCapacityOfStringBuffer(sb); }
   public static void          ChangeTabsToBlanksInStringBuffer( StringBuffer sb )               { LSStringBuffer.ReplaceCharacterWithCharacterInStringBuffer( sb, '\t', ' ' ); }
   public static StringBuffer  GetStringBufferFromString( String sb )                            { return sb==null ? null : new StringBuffer(sb); }
   public static StringBuffer  GetStringBufferOfLength( int length )                             { return new StringBuffer(length); }

   //-------------------------------------------------------------------------
   public static void  InsertStringIntoStringBufferPushRight( StringBuffer sb, int i, String s ) 
   { 
      int requiredCapacity = i + LSString.GetStringLength(s);  
      if( requiredCapacity < LSStringBuffer.GetCapacityOfStringBuffer(sb) ) 
         LSStringBuffer.ExpandCapacityOfStringBuffer( sb, requiredCapacity );  
      sb.insert(i,s); 
   }


   //-------------------------------------------------------------------------
   public static StringBuffer  GetStringBufferInitializedToCharacter( int numberOfCharacters, char c )
   {
      // Reserve enough space for all the blank characters
      StringBuffer sb = LSStringBuffer.GetStringBufferOfLength( numberOfCharacters );
      for( int i=0; i<numberOfCharacters; i++ ) sb.append(c);
      return sb;
   }


   //-------------------------------------------------------------------------
   public static StringBuffer  GetStringBufferWithNumberOfBlankWhiteSpaces( int numberOfCharacters )
   {
      return LSStringBuffer.GetStringBufferInitializedToCharacter( numberOfCharacters, ' ' );
   }


   //-------------------------------------------------------------------------
   public static boolean IsStringBufferFilledWithJustBlankWhiteSpaces( StringBuffer sb )
   {
      int stringLength = LSStringBuffer.GetLengthOfStringBuffer( sb );
      for( int i=0;   i<stringLength;  i++ )
      {
         char c = LSStringBuffer.GetCharacterAtIndex( sb, i );
         if( LSCharacter.IsWhiteSpace(c) == false ) return false;
      }
      return true;
   }


   //-------------------------------------------------------------------------
   public static void  ReplaceCharacterWithCharacterInStringBuffer( StringBuffer sb, char oldChar, char newChar )
   {
      int stringLength = LSStringBuffer.GetLengthOfStringBuffer(sb);
      for( int i=0;  i<stringLength;  i++ )
         if( LSStringBuffer.GetCharacterAtIndex(sb,i) == oldChar )  
            LSStringBuffer.SetCharacterAtIndex( sb, i, newChar );
   }


   //-------------------------------------------------------------------------
   public static void  ReplaceCharacterWithString( StringBuffer sb, char oldChar, String newString )
   {
      int sbLength = LSStringBuffer.GetLengthOfStringBuffer(sb);
      for( int i=0;  i<sbLength;  i++ )
      {
         if( LSStringBuffer.GetCharacterAtIndex(sb,i) == oldChar )
         {
            LSStringBuffer.DeleteCharacterAtIndex( sb, i );
            LSStringBuffer.InsertStringIntoStringBufferPushRight(sb,i,newString);
            sbLength = LSStringBuffer.GetLengthOfStringBuffer(sb);  // If newString is longer than oldChar, the length of sb increases.
         }
      }
   }


   //-------------------------------------------------------------------------
   public static void  DeleteCharacterAtIndex( StringBuffer sb, int i )
   {
      if( LSStringBuffer.IsIndexInStringBufferCapacity(sb,i) )
      {
         sb.deleteCharAt( i ); // Java 2
         // for( int j=i;  j<stringLength;  j++ )
         //    LSStringBuffer.SetCharacterAtIndex( s, j, LSStringBuffer.GetCharacterAtIndex(s,j+1) );
         // LSStringBuffer.SetLengthOfStringBuffer( s, stringLength - 1 );
      }
   }


   //-------------------------------------------------------------------------
   public static void  DeleteExtraWhiteSpaceInStringBuffer( StringBuffer sb, boolean aroundMathOperators, boolean aroundParens, boolean aroundDelimiters )
   {
      LSStringBuffer.ChangeTabsToBlanksInStringBuffer( sb );
      LSStringBuffer.DeleteLeadingWhiteSpaceInStringBuffer( sb );
      LSStringBuffer.DeleteTrailingWhiteSpaceInStringBuffer( sb );
      int sbLength = LSStringBuffer.GetLengthOfStringBuffer( sb );
      for( int i=0, j=0;  i<sbLength;  i++ )
      {
         boolean delChar = LSStringBuffer.IsDeletableWhiteSpaceAtCharacterIndexInStringBuffer(sb,j,aroundMathOperators,aroundParens,aroundDelimiters);
         if( delChar )  LSStringBuffer.DeleteCharacterAtIndex( sb, j );
         else j++;
      }
   }


   //-------------------------------------------------------------------------
   public static void  DeleteLeadingWhiteSpaceInStringBuffer( StringBuffer sb )
   {
      int sbLength = LSStringBuffer.GetLengthOfStringBuffer( sb );
      for( int i=0;   i<sbLength;  i++ )
      {
         char c = LSStringBuffer.GetCharacterAtIndex( sb, 0 );
         if( LSCharacter.IsWhiteSpace(c) )  LSStringBuffer.DeleteCharacterAtIndex( sb, 0 );
         else break;
      }
   }


   //-------------------------------------------------------------------------
   public static void  DeleteLeadingCommentCharactersInStringBuffer( StringBuffer sb )
   {
      int sbLength = LSStringBuffer.GetLengthOfStringBuffer( sb );
      for( int i=0;   i<sbLength;  i++ )
      {
         char c = LSStringBuffer.GetCharacterAtIndex( sb, 0 );
         if( LSCharacter.IsCommentCharacter(c) )  LSStringBuffer.DeleteCharacterAtIndex( sb, 0 );
         else break;
      }
   }


   //-------------------------------------------------------------------------
   public static void  DeleteTrailingWhiteSpaceInStringBuffer( StringBuffer sb )
   {
      int sbLength = LSStringBuffer.GetLengthOfStringBuffer( sb );
      for( int i=sbLength-1;  i>=0;  i-- )
      {
         char c = LSStringBuffer.GetCharacterAtIndex( sb, i );
         if( LSCharacter.IsWhiteSpace(c) )  LSStringBuffer.DeleteCharacterAtIndex( sb, i );
         else break;
      }
   }


   //-------------------------------------------------------------------------
   public static boolean  IsDeletableWhiteSpaceAtCharacterIndexInStringBuffer( StringBuffer sb, int i, boolean aroundMathOperators, boolean aroundParens, boolean aroundDelimiters )
   {
      // Only check characters that are within the string buffer and are a whitespace
      int sbLength = LSStringBuffer.GetLengthOfStringBuffer( sb );
      
      // Only delete if LSStringBuffer.GetCharacterAtIndex(sb,i) is whitespace.
      if( LSStringBuffer.IsIndexInStringBufferCapacity(sb,i)  &&  LSCharacter.IsWhiteSpace(LSStringBuffer.GetCharacterAtIndex(sb,i))  )
      {
         // If last character is whitespace, its ok to delete
         if( i == sbLength-1  ) return true;  

         // Decision on whether or not to delete depends on next character
         // For example, delete this whitespace if next character is whitespace.
         // Delete this whitespace if next character is mathematical operator +,*,^,/
         // 
         char cNext = LSStringBuffer.GetCharacterAtIndex(sb,i+1);
         if( LSCharacter.IsWhiteSpaceMathematicalOperatorParenOrDelimiter(cNext,aroundMathOperators,aroundParens,aroundDelimiters) ) return true;

         // If this is the first character, there is no previous character.
         if( i == 0 ) return false;

         // Otherwise, only delete if previous character is mathematical operator
         char cPrevious = LSStringBuffer.GetCharacterAtIndex(sb,i-1);
         if( LSCharacter.IsLeftOrRightParen(cPrevious) ) return false;
         if( LSCharacter.IsWhiteSpaceMathematicalOperatorParenOrDelimiter(cPrevious,aroundMathOperators,aroundParens,aroundDelimiters) ) return true;
      }
      return false;
   }


   //--------------------------------------------------------------------------
   public static LSStringList  GetTokenizedStringsFromStringBuffer( StringBuffer sb, String tokenList )
   {
      // Return null if string is null or empty
      int sbLength = LSStringBuffer.GetLengthOfStringBuffer( sb );
      if( sbLength == 0 ) return null;

      // The initial capacity could be calculated before allocating space.  For now, use 10
      LSStringList stringList = new LSStringList( 10 );
      int endIndex = 0;
      while( true )
      {
         int startIndex = LSStringBuffer.GetIndexOfNextNonTokenInStringBuffer( sb, endIndex, tokenList );
         if( startIndex >= sbLength ) break;  // No more buffer to tokenize
         endIndex = LSStringBuffer.GetIndexOfNextTokenInStringBuffer( sb, startIndex, tokenList );
         String subString = sb.substring( startIndex, endIndex++ );
         stringList.AddObjectToArray( subString );
      }
      return stringList;
   }


   //-------------------------------------------------------------------------
   public static int  GetIndexOfNextTokenInStringBuffer( StringBuffer sb, int start, String tokenList )
   {
      int sbLength = LSStringBuffer.GetLengthOfStringBuffer( sb );
      int i = start;
      for( ;  i<sbLength;  i++ )
      {
         char si = LSStringBuffer.GetCharacterAtIndex( sb, i );
         if( LSString.IsCharacterInString(si,tokenList) )  break;
      }
      return i;
   }


   //-------------------------------------------------------------------------
   public static int  GetIndexOfNextNonTokenInStringBuffer( StringBuffer sb, int start, String tokenList)
   {
      int sbLength = LSStringBuffer.GetLengthOfStringBuffer( sb );
      int i = start;
      for( ;  i<sbLength;  i++ )
      {
         char si = LSStringBuffer.GetCharacterAtIndex( sb, i );
         if( LSString.IsCharacterInString(si,tokenList) == false )  break;
      }
      return i;
   }


   //-------------------------------------------------------------------------
   public static int[]  ParseStringBufferToMatrixArrayOfPositiveIntegers( StringBuffer sb )
   {
      if( LSStringBuffer.GetFirstCharacterInStringBuffer(sb) == '['  &&  LSStringBuffer.GetLastCharacterInStringBuffer(sb) == ']' )
      {
         // Delete the first and last character and then try to get comma-separated integers
         int lastCharacterIndex = LSStringBuffer.GetLengthOfStringBuffer(sb) - 1; 
         LSStringBuffer.DeleteCharacterAtIndex( sb, lastCharacterIndex );
         LSStringBuffer.DeleteCharacterAtIndex( sb, 0 );
         LSStringList stringList = LSStringBuffer.GetTokenizedStringsFromStringBuffer( sb, "," );
         if( stringList != null )
         {
            // Create an equally sized array of integers
            int numberOfObjects = stringList.GetSizeOfArrayList();
            int arrayOfIntegers[] = new int[numberOfObjects];
            for( int i=0;  i<numberOfObjects;  i++ )
            {
               String si = stringList.GetStringAtIndex(i);
               arrayOfIntegers[i] = LSInteger.GetValidIntegerPositiveFromString( si, 0 );
            }
            return arrayOfIntegers;
         }
      }
      return null;
   }


   //-------------------------------------------------------------------------
   public static StringBuffer  GetStringBufferIntegerMinWidthOrFrontPaddedZeros( int x, int minimumStringWidth )
   {
      // Determine the length of the string that corresponds to x 
      String s = LSString.GetStringFromInteger( x );
      int lengthS = LSString.GetStringLength( s );

      // Must have at least minimumStringWidth number of characters - but may be longer, depending on the value of x
      int numberOfCharacters = lengthS > minimumStringWidth ? lengthS : minimumStringWidth;
      int numberOfPaddedZeros = minimumStringWidth - lengthS;
      if( numberOfPaddedZeros < 0 ) numberOfPaddedZeros = 0;
      
      // Reserve enough space for all the characters and initialize all to 0
      StringBuffer sb = LSStringBuffer.GetStringBufferOfLength( numberOfCharacters );

      // Put in the leading zeros
      for( int i=0;  i<numberOfPaddedZeros; i++ )  sb.append( '0' );

      // Append the default string s at correct location
      LSStringBuffer.AppendStringToStringBuffer( sb, s );
      return sb;
   }


}

