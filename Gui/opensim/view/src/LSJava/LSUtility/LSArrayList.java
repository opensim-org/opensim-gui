//--------------------------------------------------------------------------
// File:     LSArrayList.java
// Parent:   ArrayList
// Purpose:  Resizable array of objects
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
import  java.util.ArrayList;


//--------------------------------------------------------------------------
// Since Java 1.6, @SuppressWarnings suppresses warning associated with ArrayList which is now a "generics" class.
// @SuppressWarnings( "unchecked" ) 
//--------------------------------------------------------------------------
public class LSArrayList extends ArrayList
{
   // Constructor ---------------------------------------------------------
   public LSArrayList( int initialCapacity )                     { super(initialCapacity); }
   public LSArrayList( Object a, Object b )                      { super(2);  this.AddObjectToArrayIfNotNull(a);  this.AddObjectToArrayIfNotNull(b); }
   public LSArrayList( Object a, Object b, Object c )            { super(3);  this.AddObjectToArrayIfNotNull(a);  this.AddObjectToArrayIfNotNull(b);  this.AddObjectToArrayIfNotNull(c); }
   public LSArrayList( Object a, Object b, Object c, Object d )  { super(4);  this.AddObjectToArrayIfNotNull(a);  this.AddObjectToArrayIfNotNull(b);  this.AddObjectToArrayIfNotNull(c);  this.AddObjectToArrayIfNotNull(d); }

   // Constructor ---------------------------------------------------------
   public LSArrayList( Object array[] )
   {
      super( array.length );
      for( int i=0;  i<array.length;  i++ )
         this.AddObjectToArrayIfNotNull( array[i] );
   }


   //-------------------------------------------------------------------------
   // public void     Clear()                                                          { super.clear(); }
   public boolean     IsIndexInRange( int i )                                          { return i >= 0  &&  i < this.GetSizeOfArrayList(); }
   public void        AddObjectToArray( Object x )                                     { super.add(x); }
   public void        AddObjectToArrayIfNotNull( Object x )                            { if( x != null ) this.AddObjectToArray(x); }
   public void        AddObjectToArrayIfNotExistsAndNotNull( Object x )                { if( !this.IsObjectContainedInArray(x) ) this.AddObjectToArrayIfNotNull(x); }
   public void        AddToLocationIfObjectNotNull( int location, Object x )           { if( x != null ) super.add(location,x); }
   public boolean     IsObjectContainedInArray( Object x )                             { return super.contains(x); }
   public void        EnsureArrayCapacity( int newCapacity )                           { super.ensureCapacity( newCapacity ); }
   public int         GetSizeOfArrayList( )                                            { return super.size(); }
   public static int  GetSizeOfArrayList( LSArrayList x )                              { return x==null ? 0 : x.GetSizeOfArrayList(); }
   public Object      GetObjectAtIndex( int i )                                        { return this.IsIndexInRange(i) ? super.get(i) : null; }
   public Object      GetObjectAtIndexOrReturnOther( int i, Object returnIfNotFound )  { Object x = GetObjectAtIndex(i);  return x==null ? returnIfNotFound : x; }
   public void        SetObjectAtIndex( int i, Object x )                              { if( this.IsIndexInRange(i) )  super.set(i,x); }
   public boolean     RemoveObjectAtIndex( int i )                                     { if( this.IsIndexInRange(i) )  { super.remove(i); return true; }  return false; }
   public boolean     RemoveFirstOccurenceOfObject( Object x )                         { int index = this.GetFirstIndexOfObjectElseNegative1(x);  return index >= 0 ? this.RemoveObjectAtIndex(index) : false; }
   public void        RemoveAllOccurencesOfObject( Object x )                          { while( this.RemoveFirstOccurenceOfObject(x) ) {} }
   public int         GetFirstIndexOfObjectElseNegative1( Object x )                   { return super.indexOf(x); }
   public int         GetLastIndexOfObjectElseNegative1( Object x )                    { return super.lastIndexOf(x); }
   public Object      GetObjectIfContainedInArrayOtherwiseNull( Object x )             { return this.GetFirstIndexOfObjectElseNegative1(x) >= 0 ? x : null; }
}

