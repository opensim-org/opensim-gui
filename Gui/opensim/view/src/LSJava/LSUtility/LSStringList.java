//--------------------------------------------------------------------------
// File:     LSStringList.java
// Purpose:  Resizable array of strings
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
import  java.util.ArrayList;


//--------------------------------------------------------------------------
public class LSStringList extends LSArrayList
{
   // Constructors ---------------------------------------------------------
   public LSStringList( int initialCapacity )                     { super( initialCapacity ); }
   public LSStringList( String array[] )                          { super( array ); }
   public LSStringList( String a, String b )                      { super( a, b ); }
   public LSStringList( String a, String b, String c )            { super( a, b, c ); }
   public LSStringList( String a, String b, String c, String d )  { super( a, b, c, d ); }

   // Constructor ---------------------------------------------------------
   public LSStringList( String blockOfTextWithNewlines )
   {
      super( LSString.CountNumberOfNewlinesInString(blockOfTextWithNewlines) );
      int stringLength = LSString.GetStringLength( blockOfTextWithNewlines );
      if( stringLength == 0 ) return;
      int startIndex = 0;
      for( int i=0;  i<stringLength;  i++ )
      {
         if( LSString.GetCharacterAtIndex(blockOfTextWithNewlines,i) == '\n' )
         {
            String subString = LSString.GetSubStringFromStartIndexToEndIndex( blockOfTextWithNewlines, startIndex, i );
            super.AddObjectToArray( subString );
            startIndex = i + 1;
         }
      }
      String subString = LSString.GetSubStringFromStartIndexToEndIndex( blockOfTextWithNewlines, startIndex, stringLength );
      super.AddObjectToArray( subString );
   }


   //-------------------------------------------------------------------------
   public boolean  AddToStringListIfNameDoesNotExist( String s, boolean caseSensitive )
   {
      if( LSString.IsStringEmptyOrNull(s) ) return false;
      if( this.GetFirstIndexOfStringInStringListOrReturnAlternateValue(s, caseSensitive, -1) == -1)
      { 
         super.AddObjectToArrayIfNotNull( s );  
         return true; 
      }
      return false;
   }


   //-------------------------------------------------------------------------
   public boolean  AreAllStringsInStringListDouble( )
   {
      int numberOfObjects = super.GetSizeOfArrayList();
      for( int i=0;  i<numberOfObjects;  i++ )
      {
         String s = this.GetStringAtIndex( i );
         double d = LSDouble.GetDoubleFromString( s );
         if( LSDouble.IsValidDouble(d) == false ) return false;
      }
      return true;
   }


   //-------------------------------------------------------------------------
   public boolean  AreAllNamesInStringListValid( )
   {
      int numberOfObjects = super.GetSizeOfArrayList();
      for( int i=0;  i<numberOfObjects;  i++ )
      {
         String s = this.GetStringAtIndex( i );
         char leadChar = s.charAt(0);
         if( leadChar != '_'  &&  !LSCharacter.IsLetter(leadChar) ) return false;
      }
      return true;
   }


   //-------------------------------------------------------------------------
   public String  GetStringAtIndex( int i )                                        { return (String)super.GetObjectAtIndex(i); }
   public String  GetStringAtIndexOrReturnOther( int i, String returnIfNotFound )  { String x = this.GetStringAtIndex(i);  return x==null ? returnIfNotFound : x; }

   //-------------------------------------------------------------------------
   public  int  GetFirstIndexOfStringInStringListOrReturnAlternateValueCaseSensitive(   String seekString, int returnValueIfNotFound )  { return this.GetFirstIndexOfStringInStringListOrReturnAlternateValue( seekString, true,  returnValueIfNotFound ); }
   public  int  GetFirstIndexOfStringInStringListOrReturnAlternateValueCaseInsensitive( String seekString, int returnValueIfNotFound )  { return this.GetFirstIndexOfStringInStringListOrReturnAlternateValue( seekString, false, returnValueIfNotFound ); }
   //-------------------------------------------------------------------------
   private int  GetFirstIndexOfStringInStringListOrReturnAlternateValue( String seekString, boolean caseSensitive, int returnValueIfNotFound )
   {
      if( seekString != null )
      {
         int numberOfObjects = super.GetSizeOfArrayList();
         for( int i=0;  i<numberOfObjects;  i++ )
         {
            String si = this.GetStringAtIndex(i);
            if( si != null )
            {
              if( (caseSensitive==true && si.equals(seekString)) || (caseSensitive==false && si.equalsIgnoreCase(seekString)) )
                return i;
            }
         }
      }
      return returnValueIfNotFound;
   }

}

