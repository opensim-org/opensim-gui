//--------------------------------------------------------------------------
// File:     LSChoice.java
// Parent:   Choice
// Purpose:  Choices for user interface
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
public class LSChoice extends Choice          // AWT
// public class LSChoice extends JComboBox    // SWING
{

   // Constructor ---------------------------------------------------------
   public LSChoice( int numberOfVisibleRowsInList, LSContainer container, int gridWidth, int gridHeight, ItemListener itemListener )
   {
      // Keep a copy of the constraints for adding and removing this component to frames, dialogs, panels, ...
      myConstraintsWhenCreated = container.GetConstraintsClone();
      myConstraintsWhenCreated.gridwidth  = gridWidth;
      myConstraintsWhenCreated.gridheight = gridHeight;
      container.AddComponentToLayout( this, myConstraintsWhenCreated );

      // Listen for single click on an item
      if( itemListener != null ) this.AddItemListener( itemListener );

      // Show a certain number of the choices, but not necessarily all of them
      this.SetNumberOfVisibleRowsInList( numberOfVisibleRowsInList );
   }


   // ---------------------------------------------------------------------
   // Methods for AWT and Swing
   // ---------------------------------------------------------------------
   public void     AddItemListener( ItemListener i )         { super.addItemListener( i ); }    // listens for a single click on an item
   public void     RemoveItemListener( ItemListener i )      { super.removeItemListener( i ); }


   // ---------------------------------------------------------------------
   // Methods for AWT
   // ---------------------------------------------------------------------
   public boolean  IsIndexInRange( int index )                 { return index < this.GetItemCount(); }
   public void     AddString( String s )                       { super.add(s); }
   public void     InsertStringAtIndex( String s, int index )  { super.insert(s,index); }
   public void     RemoveIndex( int index )                    { if( IsIndexInRange(index) ) super.remove(index); }
   public void     RemoveString( String s )                    { super.remove(s); }
   public void     RemoveAll( )                                { super.removeAll(); }
   public String   GetItemAsString( int index )                { return super.getItem( index ); }
   public int      GetSelectedIndex( )                         { return super.getSelectedIndex(); }
   public String   GetSelectedItemAsString( )                  { return super.getSelectedItem(); }
   public int      GetItemCount(  )                            { return super.getItemCount(); }
   public void     SelectIndex( int index )                    { super.select( index ); }
   public void     SelectString( String s )                    { super.select( s ); }

   // ---------------------------------------------------------------------
   public void  SetNumberOfVisibleRowsInList( int numberOfVisibleRowsInList )
   {
      // This routine does NOT work
      // Dimension d = getSize();
      // int widthInPixels = d.width;
      // int heightInPixels = d.height;
      // int newHeightInPixels = numberOfVisibleItemsInList/8 * heightInPixels;
      // setSize( widthInPixels, newHeightInPixels );
   }

/* // ---------------------------------------------------------------------
   // Methods for Swing
   // ---------------------------------------------------------------------
   public void     AddString( Object s )                       { super.addItem(s); }
   public void     InsertStringAtIndex( Object s, int index )  { super.insertItemAt(s,index); }
   public void     RemoveIndex( int index )                    { super.removeItemAt(index); }
   public void     RemoveObject( Object s )                    { super.removeItem(s); }
   public void     RemoveAll( )                                { super.removeAllItems(); }
   public String   GetItemAsString( int index )                { return (String)super.getItemAt( index ); }
   public int      GetSelectedIndex( )                         { return super.getSelectedIndex(); }
   public String   GetSelectedItemAsString( )                  { return (String)super.getSelectedItem(); }
   public int      GetItemCount(  )                            { return super.getItemCount(); }
   public void     SelectIndex( int index )                    { super.setSelectedIndex( index ); }
   public void     SelectString( Object s )                    { super.setSelectedItem( s ); }
   public void     SetNumberOfVisibleRowsInList( int x )       { super.setMaximumRowCount( x ); } */

   // ------------------------------------------------------------------------
   public GridBagConstraints  GetConstraintsWhenCreated( )  { return myConstraintsWhenCreated; }


   // Class variables --------------------------------------------------------
   private GridBagConstraints  myConstraintsWhenCreated;
}
