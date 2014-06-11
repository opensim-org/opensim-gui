//--------------------------------------------------------------------------
// File:     LSFileWriter.java
// Parent:   None
// Purpose:  Writes text files, including ASCII and Unicode
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
public class LSFileWriter
{
   // Constructor ---------------------------------------------------------
   public LSFileWriter( LSDirFileName dirFileName, int sizeOfBuffer, boolean append )
   {
      this.FileOpen( dirFileName, sizeOfBuffer, append );
   }


   //------------------------------------------------------------------------
   public void           FileClose( )        { this.FileClose( myOutputFile, myDirFileName ); }
   public LSDirFileName  GetDirFileName( )   { return myDirFileName; }
   public boolean        DidFileOpen( )      { return myOutputFile != null; }

   //-------------------------------------------------------------------------
   public void  WriteIntegerToFile( int x   )  { this.WriteStringToFile( LSString.GetStringFromInteger(x) ); }
   public void  WriteDoubleToFile( double x )  { this.WriteStringToFile( LSString.GetStringFromDouble(x) ); }

   //-------------------------------------------------------------------------
   public void  WriteStringToFile( String s )
   {
      int stringLength = LSString.GetStringLength(s);
      for( int i=0;  i<stringLength;  i++ )
      {
         char c = s.charAt(i);
         if( c == '\n' ) this.WriteNewlineToFile();
         else            this.WriteSingleIntegerCharacterToFile( (int)c );
      }
   }


   //-------------------------------------------------------------------------
   // All information written to the file goes through here
   //-------------------------------------------------------------------------
   private void  WriteSingleIntegerCharacterToFile( int i )
   {
      try
      {
         myOutputFile.write(i);
      }
      catch( IOException e )
      {
         String msgB = "Error while writing to file " + myDirFileName.GetFileNameLongAbsolutePath();
         LSMessageDialog.NewUserMessageDialog( e.getMessage(), msgB );
      }
   }


   //-------------------------------------------------------------------------
   private void  WriteNewlineToFile( )
   {
      try
      {
         myOutputFile.newLine();
      }
      catch( IOException e )
      {
         String msgB = "Error while writing to file " + myDirFileName.GetFileNameLongAbsolutePath();      
         LSMessageDialog.NewUserMessageDialog( e.getMessage(), msgB );
      }
   }

   //-------------------------------------------------------------------------
   private void  FileClose( BufferedWriter outputFile, LSDirFileName dirFileName )
   {
      if( outputFile == null ) return;
      try
      {
         outputFile.close();
      }
      catch( IOException e )
      {
         String msgB = "Error while closing file " + dirFileName.GetFileNameLongAbsolutePath();     
         LSMessageDialog.NewUserMessageDialog( e.getMessage(), msgB );
      }
   }


   //-------------------------------------------------------------------------
   private void  FileOpen( LSDirFileName dirFileName, int sizeOfBuffer, boolean append )
   {
      String fullFileName = dirFileName == null ? null : dirFileName.GetFileNameLongAbsolutePath();
      if( LSString.IsStringEmptyOrNull(fullFileName) ) return;
      try
      {
         FileWriter fileWriter = new FileWriter( fullFileName, append );
         myOutputFile = new BufferedWriter( fileWriter, sizeOfBuffer );
         myDirFileName = dirFileName;
      }
      catch( FileNotFoundException e )
      {
         String msgB = "Error: file not found " + dirFileName.GetFileNameLongAbsolutePath();      
         LSMessageDialog.NewUserMessageDialog( e.getMessage(), msgB );
      }
      catch( IOException e )
      {
         String msgB = "Error while trying to open file " + dirFileName.GetFileNameLongAbsolutePath();
         LSMessageDialog.NewUserMessageDialog( e.getMessage(), msgB );
      }
   }


   //-------------------------------------------------------------------------
   private LSDirFileName   myDirFileName;
   private BufferedWriter  myOutputFile;

}
