//--------------------------------------------------------------------------
// File:     LSString.java
// Parent:   None
// Purpose:  Useful functions for strings
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
public class LSString
{
   // Default constructor is private (disabled) since one should never be constructed.
   private LSString( )  {;}

   //-------------------------------------------------------------------------
   public static char     GetCharacterAtIndex( String s, int i )                { return IsIndexInRange(s,i) ? s.charAt(i) : '\0'; }
   public static String   GetStringFromDouble( double x )                       { return String.valueOf(x); }
   public static String   GetStringFromInteger( int x )                         { return String.valueOf(x); }
   public static String   GetStringWidth( int x, int minWidth )                 { return GetStringFromStringBuffer( LSStringBuffer.GetStringBufferIntegerMinWidthOrFrontPaddedZeros(x,minWidth) ); }
   public static String   GetStringFromStringBuffer( StringBuffer s )           { return s==null ? null : s.toString(); }
   public static int      GetStringLength( String s )                           { return s==null ? 0 : s.length(); }
   public static boolean  IsStringEmpty( String s )                             { return s.length() == 0; }
   public static boolean  IsStringEmptyOrNull( String s )                       { return s==null || IsStringEmpty(s); }
   public static boolean  IsIndexInRange( String s, int i )                     { return s!=null  &&  i >= 0  &&  i < GetStringLength(s); }
   public static boolean  IsCharacterInString( String s, char c )               { return GetFirstIndexOfCharacterInString(s,c) >= 0; }
   public static boolean  IsCharacterInString( char c, String s )               { return GetFirstIndexOfCharacterInString(s,c) >= 0; }
   public static int      GetFirstIndexOfCharacterInString( String s, char c )  { return IsStringEmptyOrNull(s) ? -1 : s.indexOf(c); }
   public static int      GetLastIndexOfCharacterInString( String s, char c )   { return IsStringEmptyOrNull(s) ? -1 : s.lastIndexOf(c); }
   public static String   StringToLowerCase( String s )                         { return s.toLowerCase(); }
   public static String   StringToUpperCase( String s )                         { return s.toUpperCase(); }

   //-------------------------------------------------------------------------
   public static String   GetSubStringStartingFromIndex( String s, int startIndex )                       { return IsIndexInRange(s,startIndex) ? s.substring(startIndex) : null; }
   public static String   GetSubStringFromStartIndexToEndIndex( String s, int startIndex, int endIndex )  { return IsIndexInRange(s,startIndex) && IsIndexInRange(s,endIndex-1) && startIndex<endIndex ? s.substring(startIndex,endIndex) : null; }

   //-------------------------------------------------------------------------
   public static String   ReplaceCharacterInStringWithNewCharacter( String s, char oldChar, char newChar )   { return IsStringEmptyOrNull(s) ? null : s.replace(oldChar,newChar); }
   public static String   ReplaceCharacterInStringWithNewString( String s, char oldChar, String newString )  { StringBuffer sb = LSStringBuffer.GetStringBufferFromString(s);  LSStringBuffer.ReplaceCharacterWithString(sb,oldChar,newString);  return GetStringFromStringBuffer(sb); }
   public static String   ReplaceCaretInStringWithDoubleAsterisk( String s )                                 { return ReplaceCharacterInStringWithNewString( s, '^', "**" ); }

   //-------------------------------------------------------------------------
   public static int      StringCompareCaseSensitive(    String a, String b )  { return (a==null || b==null) ? NULL_STRING_INTEGER__ : a.compareTo(b); }
   public static boolean  IsStringsEqualCaseSensitive(   String a, String b )  { return StringCompareCaseSensitive(a,b) == 0; }
   public static boolean  IsStringsEqualCaseInsensitive( String a, String b )  { return (a==null || b==null) ? false : a.equalsIgnoreCase(b); }
   public static boolean  IsStringsEqualForNumberOfCharacters( String a, String b, int numberOfCharacters, boolean ignoreCase )  { return (a==null || b==null) ? false : a.regionMatches(ignoreCase, 0, b, 0, numberOfCharacters); }
   public static boolean  IsStringsEqualCaseInsensitiveForNumberOfCharacters( String a, String b, int numberOfCharacters )       { return IsStringsEqualForNumberOfCharacters(a, b, numberOfCharacters, true ); }
   public static boolean  IsStringsEqualCaseSensitiveForNumberOfCharacters(   String a, String b, int numberOfCharacters )       { return IsStringsEqualForNumberOfCharacters(a, b, numberOfCharacters, false); }

   //-------------------------------------------------------------------------
   public static String   StringConcatenateWithSpacesBetweenIfNotNull( String a, String b )                      { if( a==null ) return b;    return b==null ? a : (a + " " + b); }
   public static String   StringConcatenateWithSpacesBetweenIfNotNull( String a, String b, String c )            { return StringConcatenateWithSpacesBetweenIfNotNull( a, StringConcatenateWithSpacesBetweenIfNotNull(b,c) ); }
   public static String   StringConcatenateWithSpacesBetweenIfNotNull( String a, String b, String c, String d )  { return StringConcatenateWithSpacesBetweenIfNotNull( a, StringConcatenateWithSpacesBetweenIfNotNull(b,c,d) ); }

   //-------------------------------------------------------------------------
   public static boolean  IsStringInString( boolean ignoreCase, String seekString, String s )
   {
      int sLength = GetStringLength( s );
      int seekStringLength = GetStringLength( seekString );
      if( sLength==0 || seekStringLength==0 ) return false;
      for( int i=0;  i<sLength;  i++ )
      {
         if( seekStringLength > sLength-i ) return false;  // "abcdefg" cannot be found in "abcde"
         if( s.regionMatches(ignoreCase, i, seekString, 0, seekStringLength) )  return true;
      }
      return false;
    }


   //-------------------------------------------------------------------------
   public static int  GetFirstIndexOfStringInStringArray( String seekString, String stringArray[], boolean caseSensitive, int returnValueIfNotFound )
   {
      if( seekString != null && stringArray != null )
      {
         int numberOfObjects = stringArray.length;
         for( int i=0;  i<numberOfObjects;  i++ )
         {
            String si = stringArray[i];
            if( si != null )
            {
              if( (caseSensitive==true && si.equals(seekString)) || (caseSensitive==false && si.equalsIgnoreCase(seekString)) )
                return i;
            }
         }
      }
      return returnValueIfNotFound;
   }


   //-------------------------------------------------------------------------
   public static int  CountNumberOfNewlinesInString( String blockOfText )
   {
      int stringLength = GetStringLength( blockOfText );
      if( stringLength == 0 ) return 0;
      int numberOfLines = 0;
      for( int i=0;  i<stringLength;  i++ )
         if( GetCharacterAtIndex(blockOfText,i) == '\n' )
            numberOfLines++;
      return numberOfLines + 1;
   }


   //-------------------------------------------------------------------------
   public static int  GetMaximumWidthFromStringPossiblyWithNewlines( String blockOfText )
   {
      int stringLength = GetStringLength( blockOfText );
      if( stringLength == 0 ) return 0;

      int maxWidth = 0;
      int currentWidth = 0;
      for( int i=0;  i<stringLength;  i++ )
      {
         char chari = GetCharacterAtIndex( blockOfText, i );
         if( chari == '\n' || chari == '\r' )
         {
            if( currentWidth > maxWidth ) maxWidth = currentWidth;
            currentWidth = 0;
         }
         else currentWidth++;  
      }
      return maxWidth;
   }
 
 
   //-------------------------------------------------------------------------
   public static String  GetYearMonthDayAsString( int year, int month, int day )
   {
      // Determine the year, month, and day and length of required string
      String yearAsString  =  year  >= 1                ? LSString.GetStringWidth(year,  4) : null;
      String monthAsString =  month >= 1 && month <= 12 ? LSString.GetStringWidth(month, 2) : null;
      String dayAsString   =  day   >= 1 && day   <= 31 ? LSString.GetStringWidth(day,   2) : null;

      // Set aside enough room and create the date string
      int lengthOfFullDateString = LSString.GetStringLength(yearAsString) + LSString.GetStringLength(monthAsString) + LSString.GetStringLength(dayAsString);
      StringBuffer sb = new StringBuffer( lengthOfFullDateString );
      if( yearAsString  != null )  LSStringBuffer.AppendStringToStringBuffer( sb, yearAsString  );  
      if( monthAsString != null )  LSStringBuffer.AppendStringToStringBuffer( sb, monthAsString );  
      if( dayAsString   != null )  LSStringBuffer.AppendStringToStringBuffer( sb, dayAsString   );  
      return LSString.GetStringFromStringBuffer( sb );
   }

   //-------------------------------------------------------------------------
   private static final int  NULL_STRING_INTEGER__ = -2147483648;

}
