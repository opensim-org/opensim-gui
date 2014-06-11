//--------------------------------------------------------------------------
// File:     LSFileReader.java
// Parent:   None
// Purpose:  Reads text files, including ASCII and Unicode
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


//--------------------------------------------------------------------------
public class LSFileReader
{
   // Constructor ---------------------------------------------------------
   public LSFileReader( LSDirFileName dirFileName, int sizeOfBuffer, boolean warnIfFileNotFound )
   {
      this.FileOpen( dirFileName, sizeOfBuffer, warnIfFileNotFound );
   }


   //-------------------------------------------------------------------------
   public boolean        DidFileOpen( )     { return myInputFile != null; }
   public boolean        FileClose( )       { return LSFileReader.FileClose( myInputFile, myDirFileName.GetFileNameLongAbsolutePath()); }
   public LSDirFileName  GetDirFileName( )  { return myDirFileName; }

   //-------------------------------------------------------------------------
   public boolean  ReadLineFromFile( StringBuffer s, boolean returnEmptyLineOK )
   {
      if( s != null )
      {
         LSStringBuffer.SetStringBufferBlank(s);
         while( true )
         {
            int intRead = this.ReadSingleIntegerCharacterFromFile();
            if( intRead == -1 )  return false;  // End of file (EOF) or error occurred
            char charRead = (char)intRead;
            switch( charRead )
            {
               default:    s.append( charRead );   break;
               case '\n':  if( returnEmptyLineOK || LSStringBuffer.IsStringBufferEmpty(s)==false ) return true;
               case '\r':  break;
            }
         }
      }
      return false;
   }


   //-------------------------------------------------------------------------
   // All information read from the file comes through here
   //-------------------------------------------------------------------------
   private int  ReadSingleIntegerCharacterFromFile( )
   {
      int readInt = -1;
      try
      {
         readInt = myInputFile.read();
      }
      catch( IOException e )
      {
         String msgB = "Error while reading file " + myDirFileName.GetFileNameLongAbsolutePath();
         LSMessageDialog.NewUserMessageDialog( e.getMessage(), msgB );
      }
      return readInt;
   }


   //-------------------------------------------------------------------------
   private static boolean  FileClose( FileReader fileReader, String fileName )
   {
      if( fileReader == null ) return false;
      boolean fileClosed = false;
      try
      {
         fileReader.close();
         fileClosed = true;
      }
      catch( IOException e )
      {
         String msgB = "Error: unable to close file " + fileName;
         LSMessageDialog.NewUserMessageDialog( e.getMessage(), msgB );
      }
      return fileClosed;
   }


   //-------------------------------------------------------------------------
   private static boolean  FileClose( BufferedReader inputFile, String fileName )
   {
      if( inputFile == null ) return false;
      boolean fileClosed = false;
      try
      {
         inputFile.close();
         fileClosed = true;
      }
      catch( IOException e )
      {
         String msgB = "Error: unable to close file " + fileName;
         LSMessageDialog.NewUserMessageDialog( e.getMessage(), msgB );
      }
      return fileClosed;
   }


   //-------------------------------------------------------------------------
   private static FileReader  FileOpen( String fullFileName, boolean warnIfFileNotFound )
   {
      if( LSString.IsStringEmptyOrNull(fullFileName) ) return null;
      FileReader fileReader = null;
      try
      {
         fileReader = new FileReader( fullFileName );
      }
      catch( FileNotFoundException e )
      {
         if( warnIfFileNotFound )
         {
            String msgB = "Error: unable to open file " + fullFileName;
            LSMessageDialog.NewUserMessageDialog( e.getMessage(), msgB );
         }
      }
      return fileReader;
   }


   //-------------------------------------------------------------------------
   private void  FileOpen( LSDirFileName dirFileName, int sizeOfBuffer, boolean warnIfFileNotFound )
   {
      String fullFileName = dirFileName == null ? null : dirFileName.GetFileNameLongAbsolutePath();
      if( LSString.IsStringEmptyOrNull(fullFileName) ) return;
      FileReader fileReader = FileOpen( fullFileName, warnIfFileNotFound );
      if( fileReader != null )
      {
         myInputFile = new BufferedReader( fileReader, sizeOfBuffer );
         myDirFileName = dirFileName;
      }
   }


   //-------------------------------------------------------------------------
   private LSDirFileName   myDirFileName;
   private BufferedReader  myInputFile;

}
