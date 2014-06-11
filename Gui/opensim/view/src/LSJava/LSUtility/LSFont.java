//--------------------------------------------------------------------------
// File:     LSFont.java
// Parent:   Font
// Purpose:  Font information is stored here
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
import  javax.swing.UIManager; 


//--------------------------------------------------------------------------
public class LSFont extends Font
{
   // Constructor ---------------------------------------------------------
   public LSFont( String fontName, int fontStyle, int fontSize )  { super( fontName, fontStyle, fontSize ); }
   public LSFont( Font font )                                     { super( font.getName(), font.getStyle(), font.getSize() ); }

   // ---------------------------------------------------------------------
   public String  GetFontName( )       { return super.getName(); }
   public int     GetFontStyle( )      { return super.getStyle(); }
   public String  GetFontStyleName( )  { return myListOfFontStyleNames.GetStringAtIndex( this.GetFontStyle() ); }
   public int     GetFontSize( )       { return super.getSize(); }

   //-------------------------------------------------------------------------
   public static LSStringList  GetDisplayableLocalFontFamilyNames( )  { return LSFont.GetDisplayableFontFamilyNamesFromLocalGraphicsEnvironment(); }
   public static LSStringList  GetJavaBuiltInFontFamilyNames()        { return myListOfJavaBuiltInFontFamilyNames; }
   public static LSStringList  GetFontStyleNames()                    { return myListOfFontStyleNames; }
   public static LSStringList  GetFontSizeNames()                     { return myListOfFontSizeNames; }

   //-------------------------------------------------------------------------
   public static int  GetNumberOfLocalFontFamilyNames( )         { return LSFont.GetDisplayableLocalFontFamilyNames().GetSizeOfArrayList(); }
   public static int  GetNumberOfJavaBuitInFontFamilyNames( )    { return myListOfJavaBuiltInFontFamilyNames.GetSizeOfArrayList(); }
   public static int  GetNumberOfFontStyleNames( )               { return myListOfFontStyleNames.GetSizeOfArrayList(); }
   public static int  GetNumberOfFontSizeNames( )                { return myListOfFontSizeNames.GetSizeOfArrayList(); }
	
   //-------------------------------------------------------------------------
   public static String  GetLocalBuiltInFontFamilyName( int i )  { return LSFont.GetDisplayableLocalFontFamilyNames().GetStringAtIndex(i); }
   public static String  GetJavaBuiltInFontFamilyName( int i )   { return myListOfJavaBuiltInFontFamilyNames.GetStringAtIndex(i); }
   public static String  GetFontStyleName( int i )               { return myListOfFontStyleNames.GetStringAtIndex(i); }
   public static String  GetFontSizeName( int i )                { return myListOfFontSizeNames.GetStringAtIndex(i); }

   //-------------------------------------------------------------------------
   public static String  GetUserFontName( )                    { return myUserFont.GetFontName(); }
   public static int     GetUserFontStyle( )                   { return myUserFont.GetFontStyle(); }
   public static int     GetUserFontSize( )                    { return myUserFont.GetFontSize(); }
   public static LSFont  GetUserFont( )                        { return myUserFont; }
   public static void    SetUserFont( LSFont font )            { myUserFont = font; }


   //-------------------------------------------------------------------------
   public static void  SetUserFontFromUserSettings( )
   {
      // Check to see if there is a default font file.
      LSDirFileName dirFileName = LSDirFileName.NewDirFileName( "UserFontInformation.txt", true, false );
      if( dirFileName == null ) return;

      // The buffer size is 1024 because this is a small file
      LSFileReader inputFile = new LSFileReader( dirFileName, 1024, false );
      if( inputFile.DidFileOpen() == false ) return;
      StringBuffer stringBuffer = new StringBuffer( 512 );
      while( inputFile.ReadLineFromFile(stringBuffer,false) == true )
      {
         LSStringBuffer.DeleteExtraWhiteSpaceInStringBuffer( stringBuffer, true, true, true );
         LSStringList stringList = LSStringBuffer.GetTokenizedStringsFromStringBuffer( stringBuffer, " ," );
         if( stringList.GetSizeOfArrayList() == 4 )
         {
            String firstKeyword = stringList.GetStringAtIndex(0);
            if( LSString.IsStringsEqualCaseInsensitive( firstKeyword, "FontFamilyStyleSize" ) == true )
            {
               String userFontFamilyName = stringList.GetStringAtIndex(1);
               String userFontStyleName  = stringList.GetStringAtIndex(2);
               String userFontSizeName   = stringList.GetStringAtIndex(3);
               String fontFamilyName = LSFont.GetJavaFontFamilyNameOrReturnAlternate( userFontFamilyName, myDefaultFontFamilyName );
               int fontStyle = LSFont.GetFontStyleFromStringOrReturnAlternate( userFontStyleName,  myDefaultFontStyle );
               int fontSize  = LSFont.GetFontSizeFromStringOrReturnAlternate(  userFontSizeName,   myDefaultFontSize  );
               LSFont.SetUserFont( new LSFont(fontFamilyName, fontStyle, fontSize) );
            }
         }
      }
      inputFile.FileClose();
   }


   //-------------------------------------------------------------------------
   public static String  GetJavaFontFamilyNameOrReturnAlternate( String userFontFamilyName, String returnValueIfNotFound )
   {
      int indexOfUserFontFamilyName = myListOfJavaBuiltInFontFamilyNames.GetFirstIndexOfStringInStringListOrReturnAlternateValueCaseInsensitive( userFontFamilyName, -1 );
      return myListOfJavaBuiltInFontFamilyNames.GetStringAtIndexOrReturnOther( indexOfUserFontFamilyName, returnValueIfNotFound );
   }

   //-------------------------------------------------------------------------
   public static int  GetFontStyleFromStringOrReturnAlternate( String userFontStyleName, int returnValueIfNotFound )
   {
      int indexOfUserFontStyle = myListOfFontStyleNames.GetFirstIndexOfStringInStringListOrReturnAlternateValueCaseInsensitive( userFontStyleName, -1 );
      return LSFont.GetFontStyleForIndexOrReturnAlternate( indexOfUserFontStyle, returnValueIfNotFound );
   }

   //-------------------------------------------------------------------------
   public static int  GetFontSizeFromStringOrReturnAlternate( String userFontSizeName, int returnValueIfNotFound )
   {
      int indexOfUserFontSize = myListOfFontSizeNames.GetFirstIndexOfStringInStringListOrReturnAlternateValueCaseInsensitive( userFontSizeName, -1 );
      return LSFont.GetFontSizeForIndexOrReturnAlternate( indexOfUserFontSize, returnValueIfNotFound );
   }

   //-------------------------------------------------------------------------
   public static int  GetIndexOfFontStyleNameFromStringOrReturnAlternate( String fontStyleName, int returnValueIfNotFound )
   { 
      return myListOfFontStyleNames.GetFirstIndexOfStringInStringListOrReturnAlternateValueCaseInsensitive( fontStyleName, returnValueIfNotFound ); 
   }


   //-------------------------------------------------------------------------
   public static int  GetIndexOfFontStyleNameFromIntegerOrReturnAlternate( int fontStyle, int returnValueIfNotFound )
   {
      switch( fontStyle )
      {
         case LSFont.PLAIN:                 return 0;
         case LSFont.BOLD:                  return 1;
         case LSFont.ITALIC:                return 2;
         case LSFont.BOLD | LSFont.ITALIC:  return 3;
      }
      return returnValueIfNotFound;
   }

   //-------------------------------------------------------------------------
   public static int  GetFontStyleForIndexOrReturnAlternate( int index, int returnValueIfNotFound )
   {
      switch( index )
      {
         case  0:  return LSFont.PLAIN;
         case  1:  return LSFont.BOLD;
         case  2:  return LSFont.ITALIC;
         case  3:  return LSFont.BOLD | LSFont.ITALIC;
      }
      return returnValueIfNotFound;
   }

   //-------------------------------------------------------------------------
   public static int   GetFontStyleBoldOrUnbold( int fontStyleInitial, boolean makeBold )
   {
      int retValue = fontStyleInitial;
      switch( fontStyleInitial )
      {
         case LSFont.PLAIN:                 if( makeBold == true  ) retValue = LSFont.BOLD;                   break;
         case LSFont.BOLD:                  if( makeBold == false ) retValue = LSFont.PLAIN;                  break;
         case LSFont.ITALIC:                if( makeBold == true  ) retValue = LSFont.BOLD | LSFont.ITALIC;   break;
         case LSFont.BOLD | LSFont.ITALIC:  if( makeBold == false ) retValue = LSFont.ITALIC;                 break;
      }
      return retValue;
   }

   //-------------------------------------------------------------------------
   public static Font  GetSameFontExceptBoldOrUnbold( Font fontToBoldOrUnbold, boolean makeBold )
   {
      if( fontToBoldOrUnbold == null ) return null;
      int fontStyleInitial = fontToBoldOrUnbold.getStyle();
      int fontStyleFinal   = LSFont.GetFontStyleBoldOrUnbold( fontStyleInitial, makeBold );
      return new Font( fontToBoldOrUnbold.getName(), fontStyleFinal, fontToBoldOrUnbold.getSize() );
   }


   //-------------------------------------------------------------------------
   public static int  GetFontSizeForIndexOrReturnAlternate( int index, int returnValueIfNotFound )
   {
      String fontSizeAsString = myListOfFontSizeNames.GetStringAtIndex( index );
      return LSInteger.GetValidIntegerFromString( fontSizeAsString, returnValueIfNotFound );
   }


   //-------------------------------------------------------------------------
   public static LSStringList  GetDisplayableFontFamilyNamesFromLocalGraphicsEnvironment( )
   {
      return LSFont.GetAllAvailableFontNames1FromLocalGraphicsEnvironment();

      /* GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
      String availableFontFamilyNames[] = graphicsEnvironment.getAvailableFontFamilyNames();
      int numberOfAvailableFontFamilyNames = availableFontFamilyNames == null ? 0 : availableFontFamilyNames.length;
      LSStringList listOfNamesToReturn = new LSStringList( numberOfAvailableFontFamilyNames );
      for( int i=0;  i<numberOfAvailableFontFamilyNames; i++ )
      {
         String fontFamilyNamei = availableFontFamilyNames[i];
         if( LSFont.CanFontFamilyNameDisplayAllReasonableAsciiCharacters(fontFamilyNamei) )
            listOfNamesToReturn.AddToStringListIfNameDoesNotExist( fontFamilyNamei, false );
      }
      return listOfNamesToReturn; */
   }

   //-------------------------------------------------------------------------
   private static  LSStringList  GetAllAvailableFontNames0FromLocalGraphicsEnvironment( )   { return LSFont.GetAllAvailableFontsFromLocalGraphicsEnvironmentHelper(0); }
   private static  LSStringList  GetAllAvailableFontNames1FromLocalGraphicsEnvironment( )   { return LSFont.GetAllAvailableFontsFromLocalGraphicsEnvironmentHelper(1); }
   private static  LSStringList  GetAllAvailableFontNames2FromLocalGraphicsEnvironment( )   { return LSFont.GetAllAvailableFontsFromLocalGraphicsEnvironmentHelper(2); }
   //-------------------------------------------------------------------------
   private static  LSStringList  GetAllAvailableFontsFromLocalGraphicsEnvironmentHelper( int zeroOneTwo )
   {
      GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
      Font[] allFonts = graphicsEnvironment.getAllFonts();
      int numberOfAvailableFonts = allFonts==null ? 0 : allFonts.length;

      LSStringList listOfNamesToReturn = new LSStringList( numberOfAvailableFonts );
      for( int i=0;  i < numberOfAvailableFonts;  i++ )
      { 
         Font fonti = allFonts[i];
         String fontNamePossiblyWithStyle = null;
         switch( zeroOneTwo )
         {
             case 0:  fontNamePossiblyWithStyle = fonti.getFontName();   break;
             case 1:  fontNamePossiblyWithStyle = fonti.getFamily();     break;
             case 2:  fontNamePossiblyWithStyle = fonti.getName();       break;
         }
         String fontNamei = LSFont.GetFontNameIfDoesNotHavePlainBoldItalicEtc( fontNamePossiblyWithStyle );
         if( fontNamei != null && LSFont.CanFontDisplayAllReasonableAsciiCharacters(fonti) ) 
            listOfNamesToReturn.AddToStringListIfNameDoesNotExist( fontNamei, false );
      }
      return listOfNamesToReturn;
   }

   //-------------------------------------------------------------------------
   private static boolean  CanFontFamilyNameDisplayAllReasonableAsciiCharacters( String fontFamilyName )
   {
      LSFont fonti = (fontFamilyName == null) ? null : new LSFont( fontFamilyName, LSFont.PLAIN, 12 );
      return LSFont.CanFontDisplayAllReasonableAsciiCharacters( fonti ); 
   }

   //-------------------------------------------------------------------------
   private static boolean  CanFontDisplayAllReasonableAsciiCharacters( Font fonti )
   {
      if( fonti == null ) return false;
      char firstChar = LSCharacter.GetFirstDisplayableKeyboardCharacterOnAsciiChart();
      char lastChar  = LSCharacter.GetLastDisplayableKeyboardCharacterOnAsciiChart();
      for( char c = firstChar;  c <= lastChar;  c++ )
         if( fonti.canDisplay(c) == false ) return false; 
      return true;
   }


   //-------------------------------------------------------------------------
   private static String  GetFontNameIfDoesNotHavePlainBoldItalicEtc( String fontNamePossiblyWithStyle )
   {
      return ( LSString.IsStringInString( true, "Plain",   fontNamePossiblyWithStyle ) ||
               LSString.IsStringInString( true, "Bold",    fontNamePossiblyWithStyle ) ||
               LSString.IsStringInString( true, "Italic",  fontNamePossiblyWithStyle )  )  ? null : fontNamePossiblyWithStyle;
   }


   //-------------------------------------------------------------------------
   // Static font information uses either Font or JFont.
   public final static int  PLAIN  = Font.PLAIN;    // Usually defined as 0 in Java.
   public final static int  BOLD   = Font.BOLD;	    // Usually defined as 1 in Java.
   public final static int  ITALIC = Font.ITALIC;   // Usually defined as 2 in Java.

   // The default font size is 14 because 13 is too small, 16 is better for some dialogue boxes,
   // but messes up the dialogue box displayed after user clicks the Function button.
   public final static String   myDefaultFontFamilyName = "Serif";       // MGPlot="Serif",     OpenSim="Tahoma"
   public final static int      myDefaultFontStyle      = LSFont.BOLD;   // MGPlot=LSFont.BOLD, OpenSim=LSFont.PLAIN
   public final static int      myDefaultFontSize       = 14;            // MGPlot=14,          OpenSim=12
// private static LSFont myUserFont = new LSFont( myDefaultFontFamilyName, myDefaultFontStyle, myDefaultFontSize );
   private static LSFont myUserFont = new LSFont( UIManager.getFont("Label.font") ); 

   // Available font families, styles, and sizes
   // Font families guaranteed to be available by Java.  "Symbol" is also possible but not sensible.
   private static final String   myStaticListOfJavaBuiltInFontFamilyNames[] = { "Dialog", "DialogInput", "Monospaced", "SansSerif", "Serif" };  
   private static final String   myStaticListOfFontStyleNames[] = { "Plain", "Bold", "Italic", "Bold italic" };
   private static final String   myStaticListOfFontSizeNames[]  = { "8", "9", "10", "11", "12", "13", "14", "16", "18", "20", "22", "24", "28", "32" };
   private static  LSStringList  myListOfJavaBuiltInFontFamilyNames = new LSStringList( myStaticListOfJavaBuiltInFontFamilyNames );
   private static  LSStringList  myListOfFontStyleNames             = new LSStringList( myStaticListOfFontStyleNames );
   private static  LSStringList  myListOfFontSizeNames              = new LSStringList( myStaticListOfFontSizeNames );

}
