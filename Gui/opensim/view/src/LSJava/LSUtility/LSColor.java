//--------------------------------------------------------------------------
// File:     LSColor.java
// Class:    LSColor
// Parent:   Color
// Children: None
// Purpose:  Color information is stored here
// Authors:  John Mitiguy and Paul Mitiguy, 2001-2010.
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
      int red   = GetRGBInteger( userRedName   );
      int green = GetRGBInteger( userGreenName );
      int blue  = GetRGBInteger( userBlueName  );
      return ( red<0 || green<0 || blue<0 ) ? null : new LSColor(red, green, blue);
   }


   //-------------------------------------------------------------------------
   private static int  GetRGBInteger( String userRGBName )
   {
      Integer stringAsInteger = Integer.valueOf( userRGBName );
      int stringAsInt = stringAsInteger.intValue();
      if( stringAsInt>=0 && stringAsInt <=255 ) return stringAsInt;
      return -1;
   }


   //-------------------------------------------------------------------------

   // Default background color is light blue/green, e.g., RGB=200,220,255.
   // Default foreground color is  dark blue,       e.g., RGB=  0,  0,100. 
   // Default background color for dialogs associated with plotting is light yellow, 
   // e.g., when the user is plotting and clicks the HELP or FUNCTION button 
   public  final static int myDefaultBackgroundColorRed   = 200;
   public  final static int myDefaultBackgroundColorGreen = 220;
   public  final static int myDefaultBackgroundColorBlue  = 255;
   public  final static int myDefaultForegroundColorRed   = 0;
   public  final static int myDefaultForegroundColorGreen = 0;
   public  final static int myDefaultForegroundColorBlue  = 100;
   private final static int myDefaultGrayColor = 150; // In Java, lightGray=192, regularGray=128, darkGray = 64;
   public  final static LSColor PaulGray = new LSColor( myDefaultGrayColor, myDefaultGrayColor, myDefaultGrayColor );
   public  final static LSColor BackgroundColorSuggestingError = new LSColor( 255, 175, 255 );
   public  final static LSColor BackgroundColorSuggestingOK    = new LSColor( 255, 255, 255 );
   public  final static LSColor BackgroundColorVeryLightGray   = new LSColor( 240, 240, 240 );

   private static LSColor myUserBackgroundColor = new LSColor( myDefaultBackgroundColorRed, myDefaultBackgroundColorGreen, myDefaultBackgroundColorBlue );
   private static LSColor myUserForegroundColor = new LSColor( myDefaultForegroundColorRed, myDefaultForegroundColorGreen, myDefaultForegroundColorBlue );
   private final static LSColor myBackgroundColorDialog = new LSColor(255, 200, 150);
}
