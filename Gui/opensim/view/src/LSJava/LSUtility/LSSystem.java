//--------------------------------------------------------------------------
// File:     LSSystem.java
// Parent:   None
// Purpose:  Handles various system level commands and functions
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
import  LSJava.LSComponents.*;
import  java.io.*;
import  java.util.Properties;


//--------------------------------------------------------------------------
public class LSSystem
{
   // Default constructor is private (disabled) since one should never be constructed.
   private LSSystem()  {;}

   // ----------------------------------------------------------------------
   // Fast and efficient way to copy arrays
   // ----------------------------------------------------------------------
   public static void  ArrayCopyFastEfficient( Object source, int sourceIndex, Object dest, int destIndex, int length )  { System.arraycopy( source, sourceIndex, dest, destIndex, length ); }
   public static void  ArrayCopyFastEfficient( Object source, Object dest, int length )                                  { LSSystem.ArrayCopyFastEfficient( source, 0, dest, 0, length ); }


   // ----------------------------------------------------------------------
   // Execute a system command
   //-----------------------------------------------------------------------
   public static Process  ExecuteSystemCommand( String systemCommand, boolean warnIfUnableToExecuteCommand, boolean waitForProcessToComplete )
   {
      Process p = null;
      try
      {
         if( LSString.IsStringEmptyOrNull(systemCommand) )
            systemCommand = "cmd.exe";
         Runtime runTime = GetRuntime();
         p = runTime.exec( systemCommand );
         if( waitForProcessToComplete ) p.waitFor();
      }
      catch( Exception e )
      {
         if( warnIfUnableToExecuteCommand )
         {
            String msgB = "Unable to execute the following system command:";
            LSMessageDialog.NewUserMessageDialog( e.getMessage(), msgB, systemCommand );
         }
      }
      return p;
   }

   // ----------------------------------------------------------------------
   // Kill all threads and exit from the Java Virtual machine
   // ----------------------------------------------------------------------
   public static void  SystemExit( int exitCode )  { System.exit(exitCode); }

   // ----------------------------------------------------------------------
   // Force garbage collection and collect unused memory
   // ----------------------------------------------------------------------
   public static void  SystemMemoryGarbageCollection( )  { System.gc(); }

   // ----------------------------------------------------------------------
   // Current time in milliseconds since January 1, 1970 (use java.util.Data for current date and time)
   // ----------------------------------------------------------------------
   public static long  SystemGetCurrentTimeInMilliSeconds( )  { return System.currentTimeMillis(); }

   // ----------------------------------------------------------------------
   // Get amount of memoty that is free to use and how much memory is available to the Java Virtual Machine
   // ----------------------------------------------------------------------
   public static long  SystemGetFreeMemory()   { return GetRuntime().freeMemory(); }
   public static long  SystemGetTotalMemory()  { return GetRuntime().totalMemory(); }

   // ----------------------------------------------------------------------
   // Get system properties, e.g., PATH
   // ----------------------------------------------------------------------
   public static String  SystemGetProperty( String propertyName )  { return System.getProperty(propertyName); }
   public static String  SystemGetJavaVersionNumber( )             { return LSSystem.SystemGetProperty("java.version"); }
   public static String  SystemGetOSName( )                        { return LSSystem.SystemGetProperty("os.name"); }
   public static String  SystemGetOSArchitecture( )                { return LSSystem.SystemGetProperty("os.arch"); }
   public static String  SystemGetOSVersion( )                     { return LSSystem.SystemGetProperty("os.version"); }
   public static String  SystemGetUserAccountName( )               { return LSSystem.SystemGetProperty("user.name"); }
   public static String  SystemGetUserHomeDirectory( )             { return LSSystem.SystemGetProperty("user.home"); }
   public static String  SystemGetUserCurrentWorkingDirectory( )   { return LSSystem.SystemGetProperty("user.dir"); }

   public static String  GetFileSeparator( )                       { return LSSystem.SystemGetProperty("file.separator"); }  // "/"  on UNIX,  "\"    on PC
   public static char    GetFileSeparatorCharacter( )              { return LSString.GetCharacterAtIndex( LSSystem.GetFileSeparator(), 0 ); } // return '\\';
   public static String  GetPathSeparator( )                       { return LSSystem.SystemGetProperty("path.separator"); }  // ":"  on UNIX,  ";"    on PC
   public static String  GetLineSeparator( )                       { return LSSystem.SystemGetProperty("line.separator"); }  // "\n" on UNIX,  "\r\n" on PC
   public static boolean IsFileSeparator( char c )                 { return c==LSSystem.GetFileSeparatorCharacter() || c=='/'; } // File separator on PC can be \ or /

   // ----------------------------------------------------------------------
   // Get runtime
   // ----------------------------------------------------------------------
   public static Runtime  GetRuntime( )  { return Runtime.getRuntime(); }

   // ----------------------------------------------------------------------
   // These streams are associated with a Process (which may have been spawned by exec)
   // Input/Output from MotionGenesis can be redirected to these streams
   // ----------------------------------------------------------------------
   public static InputStream   GetStandardInputStream( Process p )     { return p.getInputStream(); }
   public static InputStream   GetStandardErrorStream( Process p )     { return p.getErrorStream(); }
   public static OutputStream  GetStandardOutputStream( Process p)     { return p.getOutputStream(); }

   // ----------------------------------------------------------------------
   // These streams are associated with the current System Java Virtual Machine.
   // Each browser interprets these differently, so them may be of limited use
   // ----------------------------------------------------------------------
   public static InputStream  GetStandardInputStream( )                 { return System.in; }
   public static PrintStream  GetStandardOutputStream( )                { return System.out; }
   public static PrintStream  GetStandardErrorStream( )                 { return System.err; }
   public static void         SetStandardInputStream( InputStream x )   { System.setIn(x); }
   public static void         SetStandardErrorStream( PrintStream x )   { System.setErr(x); }
   public static void         SetStandardOutputStream( PrintStream x )  { System.setOut(x); }

   // ----------------------------------------------------------------------
   // Load dynamic libraries (for native methods).  LoadLibrary searches the system's path.
   // ----------------------------------------------------------------------
   public static void  SystemLoadLibary( String libraryName )       { GetRuntime().loadLibrary( libraryName ); }
   public static void  SystemLoad( String fullPathToLibraryName )   { GetRuntime().load( fullPathToLibraryName ); }


   // ----------------------------------------------------------------------
   private static Properties  SystemGetProperties( )
   {
      Properties p = null;
      try
      {
         p = System.getProperties();
      }
      catch( SecurityException e )
      {
        LSMessageDialog.NewUserMessageDialog( e.getMessage(), "Error while getting system properties." );
      }
      return p;
   }


   // ----------------------------------------------------------------------
   // Sleep for a specified number of milliseconds
   // ----------------------------------------------------------------------
   public static void  SystemSleepSuspendExecution( long milliSecondsToSleep )
   {
      try
      { 
         if( milliSecondsToSleep > 0 )
            java.lang.Thread.sleep( milliSecondsToSleep );
      }
      catch( InterruptedException e )
      {
         LSMessageDialog.NewUserMessageDialog( e.getMessage(), "Error while trying to sleep." );
      }
	}

}


