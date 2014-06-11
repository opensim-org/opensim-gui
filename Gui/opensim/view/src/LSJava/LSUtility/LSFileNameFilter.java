//--------------------------------------------------------------------------
// File:     LSFileNameFilter.java
// Parent:   FileNameFilter
// Purpose:  Filters file names for file dialog boxes
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
import  java.io.File;
import  java.io.FilenameFilter;


//--------------------------------------------------------------------------
public class LSFileNameFilter implements FilenameFilter
{
   // Constructor ---------------------------------------------------------
   public LSFileNameFilter( LSStringList acceptableSuffixes )
   {
      myAcceptableSuffixes = acceptableSuffixes;
   }


   // ---------------------------------------------------------------------
   public boolean  accept( File file, String name )
   {
      int numberOfSuffixes = LSStringList.GetSizeOfArrayList( myAcceptableSuffixes );
      for( int i=0;  i<numberOfSuffixes;  i++ )
      {
         String suffixi = myAcceptableSuffixes.GetStringAtIndex(i);
         if( name.endsWith(suffixi) ) return true;
      }
      return false;
   }


   // ---------------------------------------------------------------------
   public static LSFileNameFilter  CreateFileNameFilter( String suffix )
   {
      LSStringList acceptableSuffixes = new LSStringList(1);
      acceptableSuffixes.AddObjectToArrayIfNotNull( suffix );
      return new LSFileNameFilter( acceptableSuffixes );
   }


   // Class variables -----------------------------------------------------
   LSStringList myAcceptableSuffixes;
}
