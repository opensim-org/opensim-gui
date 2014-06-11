//--------------------------------------------------------------------------
// File:     LSDirFileName.java
// Parent:   File
// Purpose:  Contains filename and directory name
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
import  java.io.IOException;
import  java.io.File;
import  java.io.FilenameFilter;


//--------------------------------------------------------------------------
public class LSDirFileName extends File
{
   // Default constructor is private (disabled) since one should never be constructed.
   // private LSDirFileName( )   {;}

   // Quasi constructor -----------------------------------------------------
   public static LSDirFileName  NewDirFileName( String fullPathToFile, boolean checkIfFileExists, boolean warnIfFileDoesNotExist )
   {
      LSDirFileName dirFileName = new LSDirFileName( null, fullPathToFile, warnIfFileDoesNotExist );
      return ( checkIfFileExists && !dirFileName.Exists() ) ? null : dirFileName;
   }

   // Quasi constructor -----------------------------------------------------
   public static LSDirFileName  NewDirFileName( String directoryName, String shortFileName, boolean checkIfFileExists, boolean warnIfFileDoesNotExist )
   {
      LSDirFileName dirFileName = new LSDirFileName( directoryName, shortFileName, warnIfFileDoesNotExist );
      return ( checkIfFileExists && !dirFileName.Exists() ) ? null : dirFileName;
   }

   // Quasi constructor -----------------------------------------------------
   public static LSDirFileName  NewDirFileNameMaybeCurrentWorkingDirectory( String directoryName, String shortFileName, boolean checkIfFileExists, boolean warnIfFileDoesNotExist )
   {
      if( directoryName==null ) directoryName = LSSystem.SystemGetUserCurrentWorkingDirectory();
      LSDirFileName dirFileName = new LSDirFileName( directoryName, shortFileName, warnIfFileDoesNotExist );
      return ( checkIfFileExists && !dirFileName.Exists() ) ? null : dirFileName;
   }

   // Private constructor -------------------------------------------------
   private LSDirFileName( String directoryName, String shortFileName, boolean warnIfFileDoesNotExist )
   {
      super( directoryName, shortFileName );
      if( warnIfFileDoesNotExist ) this.IssueErrorIfFileDoesNotExist( directoryName, shortFileName );
   }


   //------------------------------------------------------------------------
   public static String  ConvertDirFileNameForUnix( String dirFileName )
   {
      StringBuffer fileNameBuffer = LSStringBuffer.GetStringBufferFromString( dirFileName );
      char fileNameSeparator = LSSystem.GetFileSeparatorCharacter();
      LSStringBuffer.ReplaceCharacterWithCharacterInStringBuffer( fileNameBuffer, fileNameSeparator, '/' );
      return LSString.GetStringFromStringBuffer( fileNameBuffer );
   }


   //------------------------------------------------------------------------
   public  String  GetDirectoryNameEndWithFileSeparator( )    { return this.GetDirectoryName( true ); }
   public  String  GetDirectoryNameNoEndWithFileSeparator( )  { return this.GetDirectoryName( false); }
   private String  GetDirectoryName( boolean endWithFileSeparator )
   {
      String fullPathToFile = this.GetAbsolutePath();
      char fileSeparatorCharacter = LSSystem.GetFileSeparatorCharacter();
      int indexOfFileSeparator = LSString.GetLastIndexOfCharacterInString( fullPathToFile, fileSeparatorCharacter );
      if( indexOfFileSeparator < 0 ) return null;
      String directoryName = LSString.GetSubStringFromStartIndexToEndIndex( fullPathToFile, 0, indexOfFileSeparator+1 );
      return endWithFileSeparator ? directoryName + fileSeparatorCharacter : directoryName;
   }


   //------------------------------------------------------------------------
   public  String  GetFileNameForUnixShort( )      { return LSDirFileName.ConvertDirFileNameForUnix( this.GetFileNameShort() ); }
   public  String  GetFileNameForUnixLong( )       { return LSDirFileName.ConvertDirFileNameForUnix( this.GetFileNameLongAbsolutePath() );  }
   public  String  GetFileNameLongAbsolutePath( )  { return this.GetAbsolutePath(); }
   public  String  GetFileNameShort( ) 
   {
      String fullPathToFile = this.GetAbsolutePath();
      char fileSeparatorCharacter = LSSystem.GetFileSeparatorCharacter();
      int indexOfFileSeparator = LSString.GetLastIndexOfCharacterInString( fullPathToFile, fileSeparatorCharacter );
      int stringLength = LSString.GetStringLength( fullPathToFile );
      if( indexOfFileSeparator < 0 || indexOfFileSeparator > stringLength-2 ) return null;
      return LSString.GetSubStringFromStartIndexToEndIndex( fullPathToFile, indexOfFileSeparator+1, stringLength );
   }


   //------------------------------------------------------------------------
   public  String  GetFileNameNoSuffixShort( )  { return this.GetFileNameNoSuffix(true);  }
   public  String  GetFileNameNoSuffixLong( )   { return this.GetFileNameNoSuffix(false); }
   private String  GetFileNameNoSuffix( boolean justShortFileName )
   {
      String fileName = justShortFileName ? this.GetFileNameShort() : this.GetFileNameLongAbsolutePath();
      int indexOfSuffixSeparator = LSString.GetLastIndexOfCharacterInString( fileName, '.' );
      return indexOfSuffixSeparator <=0 ? null : LSString.GetSubStringFromStartIndexToEndIndex( fileName, 0, indexOfSuffixSeparator );
   }


   //------------------------------------------------------------------------
   public String  GetFileNameSuffix( )
   {
      String fileName = this.GetFileNameShort();
      int indexOfSuffixSeparator = LSString.GetLastIndexOfCharacterInString( fileName, '.' );
      int stringLength = LSString.GetStringLength( fileName );
      if( indexOfSuffixSeparator < 0 || indexOfSuffixSeparator > stringLength-2) return null;
      return LSString.GetSubStringFromStartIndexToEndIndex( fileName, indexOfSuffixSeparator+1, stringLength );
   }


   //------------------------------------------------------------------------
   public boolean  IssueWarningIfCannotWriteFile( )
   {
      if( !this.Exists() || this.CanWrite() ) return false;
      String msgA = "Error: cannot write to file  " + this.GetFileNameLongAbsolutePath();
      String msgB = "File may be read only or write protected.";
      LSMessageDialog.NewUserMessageDialog( msgA, msgB );
      return true;
   }


   //------------------------------------------------------------------------
   static public void  IssueErrorFileDoesNotExist( String pathToFile )
   {
      String message = "Error: the file " + pathToFile + " does not exist";
      LSMessageDialog.NewUserMessageDialog( message );
      // LSMessageDialog.NewUserMessageDialog( " absolute path = "    + this.GetAbsolutePath() );
      // LSMessageDialog.NewUserMessageDialog( " current working directory = " + LSSystem.GetUserCurrentWorkingDirectory() );
   }

   //------------------------------------------------------------------------
   public boolean  IssueErrorIfFileDoesNotExist( String directoryName, String shortFilename )
   {
      if( !this.Exists() )
      {
         String fullPathToFile = this.GetAbsolutePath();
         if( LSString.IsStringEmptyOrNull( fullPathToFile ) ) fullPathToFile = shortFilename;
         LSDirFileName.IssueErrorFileDoesNotExist( fullPathToFile );
         return true;
      }
      return false;
   }


   //------------------------------------------------------------------------
   public boolean   Equals( File otherFile )        { return super.equals( otherFile ); }
   public String    GetAbsolutePath( )              { return super.getAbsolutePath(); }
   public String    GetParentDirectory( )           { return super.getParent(); }
   public boolean   IsFile( )                       { return super.isFile(); }
   public boolean   IsDirectory( )                  { return super.isDirectory(); }
   public boolean   IsAbsoluteFilename( )           { return super.isAbsolute(); }
   public boolean   IsHidden( )                     { return super.isHidden(); }
   public long      GetLastModified( )              { return super.lastModified(); }
   public long      GetLengthInBytes( )             { return super.length(); }
   public boolean   Exists( )                       { return super.exists(); }
   public boolean   CanWrite( )                     { return super.canWrite(); }
   public boolean   CanRead( )                      { return super.canRead(); }
   public boolean   Rename( File newName )          { return super.renameTo(newName); }
   public boolean   SetReadOnly( )                  { return super.setReadOnly(); }
   public boolean   Delete( )                       { return super.delete(); }
   public void      DeleteFileOnExit( )             { super.deleteOnExit(); }
   public boolean   CreateDirectory( )              { return super.mkdir(); }
   public boolean   CreateDirectories( )            { return super.mkdirs(); }
   public File[]    ListOfRootDirectories( )        { return super.listRoots(); }
   public File[]    ListOfFilesInDirectory( FilenameFilter  filter )       { return filter==null ? super.listFiles() : super.listFiles(filter); }
   public String[]  ListOfFilesNamesInDirectory( FilenameFilter filter )   { return filter==null ? super.list()      : super.list(filter); }


   //------------------------------------------------------------------------
   private boolean  CreateNewFileIfNotExists( boolean warnIfUnableToCreate )
   {
      boolean fileCreated = false;
      try
      {
         fileCreated = super.createNewFile();
      }
      catch( IOException e )
      {
         if( warnIfUnableToCreate )
         {
            String msgB = "Error: while trying to create file " + this.GetFileNameLongAbsolutePath();
            LSMessageDialog.NewUserMessageDialog( e.getMessage(), msgB );
         }
      }
      return fileCreated;
   }


   //------------------------------------------------------------------------
   private static File  CreateTempFile( String prefixA, String prefixB, boolean deleteOnExit, boolean warnIfUnableToCreate )
   {
      File fileCreated = null;
      try
      {
         fileCreated = File.createTempFile(prefixA,prefixB);
         if( deleteOnExit ) fileCreated.deleteOnExit();
      }
      catch( IOException e )
      {
         if( warnIfUnableToCreate )
         {
            String msgB = "Error: while trying to create temporary file " + prefixA + prefixB;
            LSMessageDialog.NewUserMessageDialog( e.getMessage(), msgB );
         }
      }
      return fileCreated;
   }
}
