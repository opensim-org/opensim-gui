//--------------------------------------------------------------------------
// File:     LSInteger.java
// Parent:   None
// Purpose:  Useful functions for integer numbers
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
import  java.math.*;


//--------------------------------------------------------------------------
public class LSInteger
{
   // Default constructor is private (disabled) since one should never be constructed.
   private LSInteger( )   {;}

   //-------------------------------------------------------------------------
   public static int  Abs( int x )                              { return Math.abs(x); }
   public static int  Min( int x, int y )                       { return Math.min(x,y); }
   public static int  Max( int x, int y )                       { return Math.max(x,y); }
   public static int  Random( int lowerBound, int upperBound )  { return (int)(upperBound*Math.random()) + lowerBound; }

   //-------------------------------------------------------------------------
   public static boolean  IsNaN( int x )                        { return x == LSInteger.GetNaN(); }
   public static boolean  IsMaxValue( int x )                   { return x == LSInteger.GetMaxValue(); }
   public static boolean  IsMinValue( int x )                   { return x == LSInteger.GetMinValue(); }
   public static boolean  IsValidInteger( int x )               { return LSInteger.IsNaN(x) ? false : true; }
   public static boolean  IsInRange( int x, int min, int max )  { return x>min && x<max; }
   public static boolean  IsValidIntegerFromString( String s )  { return LSInteger.IsValidInteger( GetIntegerFromString(s) ); }

   //-------------------------------------------------------------------------
   public static int  GetNaN( )                                 { return LSInteger.GetMinValue() + 1; }
   public static int  GetMaxValue( )                            { return Integer.MAX_VALUE; }
   public static int  GetMinValue( )                            { return Integer.MIN_VALUE; }
   public static int  GetValidInteger(           int x,    int alternate )                    { return LSInteger.IsValidInteger(x) ? x : alternate;}
   public static int  GetValidInteger(           int x,    int alternate, int min, int max )  { return LSInteger.IsValidInteger(x) && LSInteger.IsInRange(x,min,max) ? x : alternate;}
   public static int  GetValidIntegerFromString( String s, int alternate )                    { return LSInteger.GetValidInteger( LSInteger.GetIntegerFromString(s), alternate); }

   public static int  GetValidIntegerPositive(              int x,    int alternate )  { return LSInteger.GetValidIntegerGreaterThan(x, alternate, 0); }
   public static int  GetValidIntegerNegative(              int x,    int alternate )  { return LSInteger.GetValidIntegerLessThan(   x, alternate, 0); }
   public static int  GetValidIntegerNonNegative(           int x,    int alternate )  { return LSInteger.GetValidIntegerGreaterThanEquals(x, alternate, 0); }
   public static int  GetValidIntegerNonPositive(           int x,    int alternate )  { return LSInteger.GetValidIntegerLessThanEquals(   x, alternate, 0); }
   public static int  GetValidIntegerPositiveFromString(    String s, int alternate )  { return LSInteger.GetValidIntegerPositive(    LSInteger.GetValidIntegerFromString(s,alternate), alternate); }
   public static int  GetValidIntegerNegativeFromString(    String s, int alternate )  { return LSInteger.GetValidIntegerNegative(    LSInteger.GetValidIntegerFromString(s,alternate), alternate); }
   public static int  GetValidIntegerNonNegativeFromString( String s, int alternate )  { return LSInteger.GetValidIntegerNonNegative( LSInteger.GetValidIntegerFromString(s,alternate), alternate); }
   public static int  GetValidIntegerNonPositiveFromString( String s, int alternate )  { return LSInteger.GetValidIntegerNonPositive( LSInteger.GetValidIntegerFromString(s,alternate), alternate); }

   public static int  GetValidIntegerGreaterThan(       int x, int alternate, int min )  { return LSInteger.IsValidInteger(x) && x> min ? x : alternate;}
   public static int  GetValidIntegerGreaterThanEquals( int x, int alternate, int min )  { return LSInteger.IsValidInteger(x) && x>=min ? x : alternate;}
   public static int  GetValidIntegerLessThan(          int x, int alternate, int max )  { return LSInteger.IsValidInteger(x) && x< max ? x : alternate;}
   public static int  GetValidIntegerLessThanEquals(    int x, int alternate, int max )  { return LSInteger.IsValidInteger(x) && x<=max ? x : alternate;}

   public static int  GetValidIntegerGreaterThanLessThan( int x, int alternate, int min, int max )              { return LSInteger.IsValidInteger(x) && x> min && x< max ? x : alternate;}
   public static int  GetValidIntegerGreaterThanEqualsLessThan( int x, int alternate, int min, int max )        { return LSInteger.IsValidInteger(x) && x>=min && x< max ? x : alternate;}
   public static int  GetValidIntegerGreaterThanLessThanEquals( int x, int alternate, int min, int max )        { return LSInteger.IsValidInteger(x) && x> min && x<=max ? x : alternate;}
   public static int  GetValidIntegerGreaterThanEqualsLessThanEquals( int x, int alternate, int min, int max )  { return LSInteger.IsValidInteger(x) && x>=min && x<=max ? x : alternate;}

   //-------------------------------------------------------------------------
   public static int  GetNumberOfDigits( int x )   { x = x==0 ? 1 : LSInteger.Abs(x);  double xPlusEpsilon = 0.1 + (double)x;  return (int)LSDouble.Ceil( LSDouble.Log10(xPlusEpsilon) ); }

   //-------------------------------------------------------------------------
   public static int  GetIntegerFromString( String s )
   {
      int x = GetNaN();
      if( !LSString.IsStringEmptyOrNull(s) )
      {
         try
         {
            Integer sAsInteger = Integer.valueOf( s );
            x = sAsInteger.intValue();
         }
         catch( NumberFormatException e )
         {
            return x;
         }
      }
      return x;
   }

}
