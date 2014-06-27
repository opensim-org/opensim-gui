//--------------------------------------------------------------------------
// File:     LSList.java
// Parent:   List
// Purpose:  Lists for user interface
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
import  java.awt.event.*;
import  java.awt.*;


//--------------------------------------------------------------------------
public class LSList extends List
{
   // Constructor ---------------------------------------------------------
   public LSList( int numberOfVisibleRowsInList, boolean allowMultipleItemsToBeSelectedSimultaneously, LSContainer container, int gridWidth, int gridHeight, ActionListener actionListener, ItemListener itemListener )
   {
      super( numberOfVisibleRowsInList, allowMultipleItemsToBeSelectedSimultaneously );

      container.AddComponentToLayout( this, gridWidth, gridHeight );

      if( actionListener != null )      // Listens for a double click on an item
         this.AddActionListener( actionListener );

      if( itemListener != null )        // Listens for single click on an item
         this.AddItemListener( itemListener );
   }

   // ---------------------------------------------------------------------
   public void      AddStringToList( String s )                             { super.add(s); }
   public void      RemoveIndex( int index )                                { super.remove( index ); }
   public void      RemoveAll( )                                            { super.removeAll(); }
   public void      ReplaceItemInIndexWithString( String sNew, int index )  { super.replaceItem( sNew, index ); }
   public String    GetItemAtIndex( int index )                             { return super.getItem( index ); }
   public int       GetSelectedIndex()                                      { return super.getSelectedIndex(); }
   public int[]     GetSelectedIndexes()                                    { return super.getSelectedIndexes(); }
   public String    GetSelectedItemAsString()                               { return super.getSelectedItem(); }
   public String[]  GetSelectedItemsAsStringArray()                         { return super.getSelectedItems(); }
   public boolean   IsIndexSelected( int index )                            { return super.isIndexSelected( index ); }
   public void      SelectIndex( int index )                                { super.select( index ); }
   public void      DeselectIndex( int index )                              { super.deselect( index ); }
   public void      SetMultipleMode( boolean b )                            { super.setMultipleMode(b); }
   public boolean   IsMultipleMode( )                                       { return super.isMultipleMode(); }
   public void      MakeListVisible( int index )                            { super.makeVisible( index ); }
   public void      AddActionListener( ActionListener a )                   { super.addActionListener( a ); }  // listens for a double click on an item
   public void      RemoveActionListener( ActionListener a )                { super.removeActionListener( a ); }
   public void      AddItemListener( ItemListener i )                       { super.addItemListener( i ); }    // listens for a single click on an item
   public void      RemoveItemListener( ItemListener i )                    { super.removeItemListener( i ); }

}
