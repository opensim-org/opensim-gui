//--------------------------------------------------------------------------
// File:     LSColor.java
// Parent:   Color
// Purpose:  Color information is stored here
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
import  java.awt.*;
import  java.io.*;


//--------------------------------------------------------------------------
public class LSColor extends Color
{
   // Constructor ---------------------------------------------------------
   // red, green, and blue are integers from 0 to 255.
   // If all are 0,   the color is black.
   // If all are 255, the color is white.
   public LSColor( int red, int green, int blue )  { super(red,green,blue); }

   // ---------------------------------------------------------------------
   public int  GetRed( )    { return super.getRed(); }
   public int  GetBlue( )   { return super.getBlue(); }
   public int  GetGreen( )  { return super.getGreen(); }

   //-------------------------------------------------------------------------
   static public LSColor GetUserBackgroundColor( )    { return myUserBackgroundColor; }
   static public LSColor GetUserForegroundColor( )    { return myUserForegroundColor; }
   static public LSColor GetBackgroundColorDialog( )  { return myBackgroundColorDialog; }

   //-------------------------------------------------------------------------
   static public void  SetUserBackgroundColor( LSColor color )  { myUserBackgroundColor = color; }
   static public void  SetUserForegroundColor( LSColor color )  { myUserForegroundColor = color; }

   //-------------------------------------------------------------------------
   public static void  SetUserBackgroundColorFromUserSettings( String userRedName, String userGreenName, String userBlueName )
   {
      LSColor color = LSColor.GetColorFromUserSettings( userRedName, userGreenName, userBlueName );
      if( color != null ) LSColor.SetUserBackgroundColor( color );
   }

   //-------------------------------------------------------------------------
   public static void  SetUserForegroundColorFromUserSettings( String userRedName, String userGreenName, String userBlueName )
   {
      LSColor color = LSColor.GetColorFromUserSettings( userRedName, userGreenName, userBlueName );
      if( color != null ) LSColor.SetUserForegroundColor( color );
   }

   //-------------------------------------------------------------------------
   private static LSColor  GetColorFromUserSettings( String userRedName, String userGreenName, String userBlueName )
   {
      int red   = LSColor.GetRGBInteger( userRedName   );
      int green = LSColor.GetRGBInteger( userGreenName );
      int blue  = LSColor.GetRGBInteger( userBlueName  );
      return (red < 0  ||  green < 0  ||  blue < 0) ? null : new LSColor(red, green, blue);
   }


   //-------------------------------------------------------------------------
   private static int  GetRGBInteger( String userRGBName )
   {
      Integer stringAsInteger = Integer.valueOf( userRGBName );
      int stringAsInt = stringAsInteger.intValue();
      if( stringAsInt >= 0  &&  stringAsInt <= 255 ) return stringAsInt;
      return -1;
   }


   //-------------------------------------------------------------------------
   // Default background color is light blue/green, e.g., RGB=200,220,255.
   // Default foreground color is very dark blue,   e.g., RGB=  0,  0,100. 
   public  final static int myDefaultBackgroundColorRed   = 200;
   public  final static int myDefaultBackgroundColorGreen = 220;
   public  final static int myDefaultBackgroundColorBlue  = 255;
   public  final static int myDefaultForegroundColorRed   = 0;
   public  final static int myDefaultForegroundColorGreen = 0;
   public  final static int myDefaultForegroundColorBlue  = 100;
   public  final static LSColor AllRed      = new LSColor( 255,   0,   0 );
   public  final static LSColor DarkRed     = new LSColor( 140,   0,   0 );
   public  final static LSColor LightRed    = new LSColor( 255, 100, 100 );
   public  final static LSColor AllGreen    = new LSColor(   0, 255,   0 );
   public  final static LSColor DarkGreen   = new LSColor(   0, 140,   0 );
   public  final static LSColor LightGreen  = new LSColor( 100, 255, 100 );
   public  final static LSColor AllBlue     = new LSColor(   0,   0, 255 );
   public  final static LSColor DarkBlue    = new LSColor(   0,   0, 140 );
   public  final static LSColor LightBlue   = new LSColor( 100, 100, 255 );
   public  final static LSColor LightYellow = new LSColor( 255, 255, 125 );
   public  final static LSColor AllYellow   = new LSColor( 255, 255,   0 );
   public  final static LSColor DarkYellow  = new LSColor( 170, 170,   0 );
   public  final static LSColor LighterGray = new LSColor( 240, 240, 240 );
   public  final static LSColor LightGray   = new LSColor( 200, 200, 200 ); // In Java    lightGray = 192
   public  final static LSColor AllGray     = new LSColor( 150, 150, 150 ); // In Java, regularGray = 128
   public  final static LSColor DarkGray    = new LSColor( 100, 100, 100 ); // In Java,    darkGray = 64
   public  final static LSColor Black       = new LSColor(   0,   0,   0 );
   public  final static LSColor White       = new LSColor( 255, 255, 255 );
   public  final static LSColor ForegroundColorSuggestingError   = new LSColor( 150,   0,   0 );  // Darkish red.
   public  final static LSColor BackgroundColorSuggestingError   = new LSColor( 255, 175, 255 );  // Pink
   public  final static LSColor BackgroundColorSuggestingWarning = new LSColor( 255, 255, 100 );  // Lightish yellow 

   private static LSColor myUserBackgroundColor = new LSColor( myDefaultBackgroundColorRed, myDefaultBackgroundColorGreen, myDefaultBackgroundColorBlue );
   private static LSColor myUserForegroundColor = new LSColor( myDefaultForegroundColorRed, myDefaultForegroundColorGreen, myDefaultForegroundColorBlue );
   private final static LSColor myBackgroundColorDialog = new LSColor(255, 200, 150);
}
