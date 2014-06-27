//--------------------------------------------------------------------------
// File:     LSFileDialog.java
// Parent:   FileDialog
// Purpose:  Handles file dialog boxes
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
package LSJava.LSComponents;
import  LSJava.LSUtility.*;
import  java.awt.*;


//--------------------------------------------------------------------------
public class LSFileDialog extends FileDialog
{
   // Constructor ---------------------------------------------------------
   public LSFileDialog( Frame ownerFrame, String dialogTitle, int mode, LSDirFileName dirFileName, LSFileNameFilter fileNameFilter )
   {
      super( ownerFrame, dialogTitle, mode );   // mode is either FileDialog.SAVE or FileDialog.LOAD
      this.LSFileDialogConstructorHelper( dirFileName, fileNameFilter );
   }

   // Constructor helper  --------------------------------------------------
   private void  LSFileDialogConstructorHelper( LSDirFileName dirFileName, LSFileNameFilter fileNameFilter )
   {
      // Pre-select the default directory and the default file
      if( dirFileName != null )
      {
         String defaultDirectoryName = dirFileName.GetDirectoryNameNoEndWithFileSeparator();
         String defaultShortFileName = dirFileName.GetFileNameShort();
         if( !LSString.IsStringEmptyOrNull( defaultDirectoryName) )  this.SetDirectoryName( defaultDirectoryName );
         if( !LSString.IsStringEmptyOrNull( defaultShortFileName) )  this.SetFileName( defaultShortFileName );
      }

      // May want to filter which files are shown
      if( fileNameFilter != null ) super.setFilenameFilter(fileNameFilter);

      // Display the window
      super.pack();
      super.setVisible(true);
   }


   // ---------------------------------------------------------------------
   private String  GetDirectoryName( )                       { return super.getDirectory(); }
   private String  GetFileName( )                            { return super.getFile(); }  // returns the short file name
   private void    SetDirectoryName( String directoryName )  { super.setDirectory(directoryName); }
   private void    SetFileName( String fileName )            { super.setFile(fileName); }

   // ---------------------------------------------------------------------
   public LSDirFileName  GetDirFileName( )
   {
      String dirName = this.GetDirectoryName();
      String shortFileName = this.GetFileName();
      return LSString.IsStringEmptyOrNull(shortFileName) ? null : LSDirFileName.NewDirFileName(dirName, shortFileName, false, false);
   }
}
