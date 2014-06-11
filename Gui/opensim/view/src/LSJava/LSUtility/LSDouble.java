//--------------------------------------------------------------------------
// File:     LSDouble.java
// Parent:   None
// Purpose:  Useful functions for double precision numbers
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
public class LSDouble
{
   // Default constructor is private (disabled) since one should never be constructed.
   private LSDouble( )   {;}

   //-------------------------------------------------------------------------
   public static final double Pi = java.lang.Math.PI;
   public static double  Ceil(  double x )  { return java.lang.Math.ceil(x);  }
   public static double  Floor( double x )  { return java.lang.Math.floor(x); }
   public static double  Abs(   double x )  { return java.lang.Math.abs(x);   }
   public static double  Sqrt(  double x )  { return java.lang.Math.sqrt(x);  }
   public static double  Log(   double x )  { return java.lang.Math.log(x);   }
   public static double  Log10( double x )  { return LSDouble.Log(x) / LSDouble.Log(10); }
   public static double  Sin(   double x )  { return java.lang.Math.sin(x);   }
   public static double  Cos(   double x )  { return java.lang.Math.cos(x);   }
   public static double  Tan(   double x )  { return java.lang.Math.tan(x);   }
   public static double  Asin(  double x )  { return java.lang.Math.asin(x);  }
   public static double  Acos(  double x )  { return java.lang.Math.acos(x);  }
   public static double  Atan(  double x )  { return java.lang.Math.atan(x);  }
   public static double  Atan2( double y, double x )       { return java.lang.Math.atan2(y,x);    }
   public static double  Max(   double a, double b )       { return java.lang.Math.max(a,b);      }
   public static double  Min(   double a, double b )       { return java.lang.Math.min(a,b);      }
   public static double  DegreesToRadians(  double x )     { return java.lang.Math.toRadians(x);  }
   public static double  RadiansToDegrees(  double x )     { return java.lang.Math.toDegrees(x);  }
   public static double  RoundToIntegerOrEvenIfPoint5( double x )  { return java.lang.Math.rint(x); }
   public static double  Pow( double base, double exponent )       { return java.lang.Math.pow(base,exponent); }
   public static double  RandomNumberBetween0And1(  )              { return java.lang.Math.random(); }


   //-------------------------------------------------------------------------
   public static boolean  IsNaN( double x )                               { return Double.isNaN(x); }
   public static boolean  IsMaxValue( double x )                          { return x == LSDouble.GetMaxValue(); }
   public static boolean  IsMinValue( double x )                          { return x == LSDouble.GetMinValue(); }
   public static boolean  IsNegativeInfinity( double x )                  { return x == LSDouble.GetNegativeInfinity(); }
   public static boolean  IsPositiveInfinity( double x )                  { return x == LSDouble.GetPositiveInfinity(); }
   public static boolean  IsInfinite( double x )                          { return Double.isInfinite(x); }
   public static boolean  IsValidDouble( double x )                       { return (LSDouble.IsNaN(x) || LSDouble.IsInfinite(x)) ? false : true; }
   public static boolean  IsValidDoubleNonNegative( double x )            { return LSDouble.IsValidDouble(x) && x>=0; }
   public static boolean  IsValidDoubleNonPositive( double x )            { return LSDouble.IsValidDouble(x) && x<=0; }
   public static boolean  IsValidDoublePositive( double x )               { return LSDouble.IsValidDouble(x) && x>0;  }
   public static boolean  IsValidDoubleNegative( double x )               { return LSDouble.IsValidDouble(x) && x<0;  }
   public static boolean  IsInRange( double x, double min, double max )   { return x > min  &&  x < max; }
   public static boolean  IsValidDoubleFromString( String s )             { return LSDouble.IsValidDouble(            LSDouble.GetDoubleFromString(s) ); }
   public static boolean  IsValidDoubleNonNegativeFromString( String s )  { return LSDouble.IsValidDoubleNonNegative( LSDouble.GetDoubleFromString(s) ); }
   public static boolean  IsValidDoubleNonPositiveFromString( String s )  { return LSDouble.IsValidDoubleNonPositive( LSDouble.GetDoubleFromString(s) ); }
   public static boolean  IsValidDoublePositiveFromString( String s )     { return LSDouble.IsValidDoublePositive(    LSDouble.GetDoubleFromString(s) ); }
   public static boolean  IsValidDoubleNegativeFromString( String s )     { return LSDouble.IsValidDoubleNegative(    LSDouble.GetDoubleFromString(s) ); }

   //-------------------------------------------------------------------------
   public static int     GetMaxNumberOfCharsInTextFieldsForDoublePrecisionNumber()  { return 20; }

   //-------------------------------------------------------------------------
   public static double  GetNaN( )               { return Double.NaN; }
   public static double  GetMaxValue( )          { return Double.MAX_VALUE; }
   public static double  GetMinValue( )          { return Double.MIN_VALUE; }
   public static double  GetNegativeInfinity( )  { return Double.NEGATIVE_INFINITY; }
   public static double  GetPositiveInfinity( )  { return Double.POSITIVE_INFINITY; }
   public static double  GetValidDouble(           double x, double alternate )  { return LSDouble.IsValidDouble(x) ? x : alternate; }
   public static double  GetValidDoubleFromString( String s, double alternate )  { return LSDouble.GetValidDouble( LSDouble.GetDoubleFromString(s), alternate); }

   public static double  GetValidDoublePositive(              double x, double alternate )  { return LSDouble.GetValidDoubleGreaterThan(      x, alternate, 0.0); }
   public static double  GetValidDoubleNegative(              double x, double alternate )  { return LSDouble.GetValidDoubleLessThan(         x, alternate, 0.0); }
   public static double  GetValidDoubleNonNegative(           double x, double alternate )  { return LSDouble.GetValidDoubleGreaterThanEquals(x, alternate, 0.0); }
   public static double  GetValidDoubleNonPositive(           double x, double alternate )  { return LSDouble.GetValidDoubleLessThanEquals(   x, alternate, 0.0); }
   public static double  GetValidDoublePositiveFromString(    String s, double alternate )  { return LSDouble.GetValidDoublePositive(    LSDouble.GetValidDoubleFromString(s,alternate), alternate); }
   public static double  GetValidDoubleNegativeFromString(    String s, double alternate )  { return LSDouble.GetValidDoubleNegative(    LSDouble.GetValidDoubleFromString(s,alternate), alternate); }
   public static double  GetValidDoubleNonNegativeFromString( String s, double alternate )  { return LSDouble.GetValidDoubleNonNegative( LSDouble.GetValidDoubleFromString(s,alternate), alternate); }
   public static double  GetValidDoubleNonPositiveFromString( String s, double alternate )  { return LSDouble.GetValidDoubleNonPositive( LSDouble.GetValidDoubleFromString(s,alternate), alternate); }

   public static double  GetValidDoubleGreaterThan(       double x, double alternate, double min )  { return LSDouble.IsValidDouble(x) && x> min ? x : alternate;}
   public static double  GetValidDoubleGreaterThanEquals( double x, double alternate, double min )  { return LSDouble.IsValidDouble(x) && x>=min ? x : alternate;}
   public static double  GetValidDoubleLessThan(          double x, double alternate, double max )  { return LSDouble.IsValidDouble(x) && x< max ? x : alternate;}
   public static double  GetValidDoubleLessThanEquals(    double x, double alternate, double max )  { return LSDouble.IsValidDouble(x) && x<=max ? x : alternate;}

   public static double  GetValidDoubleGreaterThanLessThan( double x, double alternate, double min, double max )              { return LSDouble.IsValidDouble(x) && x> min && x< max ? x : alternate;}
   public static double  GetValidDoubleGreaterThanEqualsLessThan( double x, double alternate, double min, double max )        { return LSDouble.IsValidDouble(x) && x>=min && x< max ? x : alternate;}
   public static double  GetValidDoubleGreaterThanLessThanEquals( double x, double alternate, double min, double max )        { return LSDouble.IsValidDouble(x) && x> min && x<=max ? x : alternate;}
   public static double  GetValidDoubleGreaterThanEqualsLessThanEquals( double x, double alternate, double min, double max )  { return LSDouble.IsValidDouble(x) && x>=min && x<=max ? x : alternate;}

   //-------------------------------------------------------------------------
   public static double  RoundToNearestIntegerIfWithinEpsilon( double x, double nonNegativeEpsilon )
   {
      double absX = Abs( x );
      double floorX = Floor( absX );
      double ceilX  = Ceil(  absX );
      double diffWithFloor = absX  - floorX;
      double diffWithCeil  = ceilX - absX;
      boolean useCeilToCompare = diffWithCeil <= diffWithFloor;

      double signX = x < 0 ? -1 : 1;
      if( useCeilToCompare  &&  absX + nonNegativeEpsilon >= ceilX ) x = signX *  ceilX;
      else if( floorX + nonNegativeEpsilon >= absX )                 x = signX * floorX;

      return x;
   }


   //-------------------------------------------------------------------------
   public static double  GetDoubleFromString( String s )
   {
      double x = GetNaN();
      if( !LSString.IsStringEmptyOrNull(s) )
      {
         try
         {
            Double sAsDouble = Double.valueOf( s );
            x = sAsDouble.doubleValue();
         }
         catch( NumberFormatException e )
         {
            return x;
         }
      }
      return x;
   }

}
