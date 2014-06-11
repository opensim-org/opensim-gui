//--------------------------------------------------------------------------
// File:     LSImageResource.java
// Parent:   None
// Purpose:  Easy-to-use and commonly used methods for getting images, resizing, etc.
// Author:   Paul Mitiguy, 2011-2012. 
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
package LSJava.LSResources;
import  LSJava.LSUtility.*;
import  LSJava.LSComponents.*;
import  java.io.*;
import  java.awt.*; 
import  java.awt.Image;
import  java.awt.image.BufferedImage;
import  javax.imageio.ImageIO;
import  javax.swing.ImageIcon;
import  javax.swing.JLabel;
import  java.net.URL;

//--------------------------------------------------------------------------
// Java supports the following standard image format plugins: JPEG, PNG, GIF, BMP and WBMP.
// GIF: Supports animation, and transparent pixels -- but only 256 colors and no translucency.
// PNG: Better than GIF or JPG for high color images and supports translucency -- but no animation 
// JPG: Great for photographic images -- but some loss in compression, not good for text, or where the original image must be preserved exactly. 
//--------------------------------------------------------------------------
public class LSImageResource
{
   //-----------------------------------------------------------------------------
   // Default constructor is private (disabled) since one should never be constructed.
   private LSImageResource( )   {;}

   //-----------------------------------------------------------------------------
   public static Image  GetImageFromImageIcon( ImageIcon imageIcon )  { return imageIcon==null ? null : imageIcon.getImage(); }                                   
   public static Image  GetScaledImageFromImage(               Image image,                  int newWidthInPixelsOr0, int newHeightInPixelsOr0 )  { return LSImageResource.GetScaledImageFromImageIcon( LSImageResource.GetImageIconFromImage(image),                               newWidthInPixelsOr0, newHeightInPixelsOr0 ); }                               
   public static Image  GetScaledImageFromLSResourcesFileName( String shortGraphicsFileName, int newWidthInPixelsOr0, int newHeightInPixelsOr0 )  { return LSImageResource.GetScaledImageFromImageIcon( LSImageResource.GetImageIconFromLSResourcesFileName(shortGraphicsFileName), newWidthInPixelsOr0, newHeightInPixelsOr0 ); }
   public static Image  GetScaledImageFromImageIcon(           ImageIcon imageIcon,          int newWidthInPixelsOr0, int newHeightInPixelsOr0 )  
   { 
      Image image = LSImageResource.GetImageFromImageIcon( imageIcon );
      int originalPixelWidth  = LSImageResource.GetImageIconWidthInPixels(  imageIcon );
      int originalPixelHeight = LSImageResource.GetImageIconHeightInPixels( imageIcon );
      int[] newPixelWidthHeight = LSImageResource.GetScaledImageWidthHeightOrNullIfError( originalPixelWidth, originalPixelHeight, newWidthInPixelsOr0, newHeightInPixelsOr0 );
      if( newPixelWidthHeight != null )   
      { 
         int imageResamplingAlgorithmHint = Image.SCALE_SMOOTH;  // Recommend imageResamplingAlgorithmHint = Image.SCALE_SMOOTH;
         image = image.getScaledInstance( newPixelWidthHeight[0], newPixelWidthHeight[1], imageResamplingAlgorithmHint );
      }
      return image;
   }

   //-----------------------------------------------------------------------------
   public static ImageIcon  GetImageIconFromImage( Image image )                                              { return image ==null ? null : new ImageIcon(image);  }                                   
   public static ImageIcon  GetImageIconFromURL( URL imgURL )                                                 { return imgURL==null ? null : new ImageIcon(imgURL); }                                   
   public static ImageIcon  GetImageIconFromBufferedImage( BufferedImage bufferedImage )                      { return bufferedImage==null ? null : new ImageIcon(bufferedImage); }
   public static ImageIcon  GetImageIconFromFileName( String imageFileName )                                  { return imageFileName==null ? null : new ImageIcon(imageFileName); }
   public static ImageIcon  GetImageIconFromLSResourcesFileName( String shortGraphicsFileName )               { return LSImageResource.GetImageIconFromURL( LSImageResource.GetURLFromLSResources(shortGraphicsFileName) ); }
   public static ImageIcon  GetImageIconFromLSResourcesFileNameBufferedImage( String shortGraphicsFileName )  { return LSImageResource.GetImageIconFromBufferedImage( LSImageResource.GetBufferedImageFromLSResourcesFileName(shortGraphicsFileName) ); }

   //-----------------------------------------------------------------------------
   public static JLabel  GetJLabelFromImageIcon( ImageIcon imageIcon )                                                                  { return imageIcon==null ? null : new JLabel(imageIcon); }
   public static JLabel  GetJLabelFromImage( Image image )                                                                              { return LSImageResource.GetJLabelFromImageIcon( LSImageResource.GetImageIconFromImage(image) ); }
   public static JLabel  GetJLabelFromScaledImage( Image image, int widthInPixels, int heightInPixels )                                 { return LSImageResource.GetJLabelFromImage(     LSImageResource.GetScaledImageFromImage(image,widthInPixels,heightInPixels) ); }
   public static JLabel  GetJLabelFromLSResourcesFileName( String shortGraphicsFileName )                                               { return LSImageResource.GetJLabelFromImageIcon( LSImageResource.GetImageIconFromLSResourcesFileName(shortGraphicsFileName) ); }
   public static JLabel  GetJLabelFromLSResourcesFileNameBufferedImage( String shortGraphicsFileName )                                  { return LSImageResource.GetJLabelFromImageIcon( LSImageResource.GetImageIconFromLSResourcesFileNameBufferedImage(shortGraphicsFileName) ); }
   public static JLabel  GetJLabelFromLSResourcesFileNameScaled( String shortGraphicsFileName, int widthInPixels, int heightInPixels )  { return LSImageResource.GetJLabelFromImage( LSImageResource.GetScaledImageFromLSResourcesFileName(shortGraphicsFileName,widthInPixels,heightInPixels) ); }
   
   //-----------------------------------------------------------------------------
   public static LSPanel  GetLSPanelFromLSResourcesFileNameScaled( String shortGraphicsFileName, int widthInPixels, int heightInPixels, Color backgroundColorOrNullForWhite )  
   {
      // Add image to the left-hand-side of the panel, then maybe put in some blank white space.
      LSPanel     panel = new LSPanel();
      LSContainer panelContainer = panel.GetPanelAsContainer();
      panelContainer.SetContainerBackgroundColor( backgroundColorOrNullForWhite==null ? Color.white : backgroundColorOrNullForWhite );
      JLabel labelWithPicture = LSJava.LSResources.LSImageResource.GetJLabelFromLSResourcesFileNameScaled( shortGraphicsFileName, widthInPixels, heightInPixels );
      if( labelWithPicture != null )  panelContainer.AddComponentToLayoutColRemainder1Wide( labelWithPicture );
      new LSLabel( " ", LSLabel.CENTER, panelContainer, 1, GridBagConstraints.REMAINDER );
      return panel;
   }


   //-----------------------------------------------------------------------------
   public static int  GetImageIconWidthInPixels(  ImageIcon imageIcon )  { return imageIcon==null ? 0 : imageIcon.getIconWidth();  }
   public static int  GetImageIconHeightInPixels( ImageIcon imageIcon )  { return imageIcon==null ? 0 : imageIcon.getIconHeight(); }


   //------------------------------------------------------------------------
   public static BufferedImage  GetBufferedImageFromLSResourcesFileName( String shortGraphicsFileName )
   {
      java.net.URL imgURL = LSImageResource.GetURLFromLSResources( shortGraphicsFileName ); 
      try{
         return ImageIO.read( imgURL );   // returns picture as BufferedImage
      }
      catch( IOException exception )
      {
         String resourcePathToGraphicsFileName = LSImageResource.GetResourceDirectoryName() + shortGraphicsFileName;
         LSImageResource.IssueErrorUnableToFindGraphicsFile( resourcePathToGraphicsFileName ); 
      } 
      return null;
   }


   //------------------------------------------------------------------------
   public static BufferedImage  GetBufferedImageFromFile( LSDirFileName dirFileName )
   {
      if( dirFileName==null || dirFileName.Exists()==false ) return null;
      try{
         return ImageIO.read( (File)dirFileName );  // returns picture as BufferedImage.
      }
      catch( IOException exception )
      {
         LSImageResource.IssueErrorUnableToFindGraphicsFile( dirFileName.GetFileNameLongAbsolutePath() ); 
      } 
      return null;
   }


   //------------------------------------------------------------------------
   public static java.net.URL  GetURLFromLSResources( String shortGraphicsFileName ) 
   { 
      if( shortGraphicsFileName == null ) return null;
      String resourcePathToGraphicsFileName = LSImageResource.GetResourceDirectoryName() + shortGraphicsFileName;
      java.net.URL imgURL = LSImageResource.class.getResource( resourcePathToGraphicsFileName );
      if( imgURL == null ) LSImageResource.IssueErrorUnableToFindGraphicsFile( resourcePathToGraphicsFileName ); 
      return imgURL;
   } 


   //------------------------------------------------------------------------
   public static int[]  GetScaledImageWidthHeightOrNullIfError( int originalWidth, int originalHeight, int newWidth, int newHeight )
   { 
      // Check to see if the input arguments make sense.  If not, return null.
      if( originalWidth <= 0 || originalHeight <= 0 || newWidth < 0 || newHeight < 0 )  return null;

      // If newWidth is 0, scale proportionally based on height.
      if( newWidth==0 && newHeight > 0 )
      {
         double scaleFactor = ((double)newHeight) / ((double)originalHeight);
	 newWidth = (int)( scaleFactor * (double)originalWidth );
      }
      // If newHeight is 0, scale proportionally based on width.
      else if( newWidth > 0 && newHeight==0 )
      {
         double scaleFactor = ((double)newWidth) / ((double) originalWidth);
	 newHeight = (int)( scaleFactor * (double)originalHeight );
      }
      // If both newWidth and newHeight are zero, no scaling.
      else if( newWidth==0 && newHeight==0 )
      {
         newWidth  = originalWidth;
	 newHeight = originalHeight;
      }

      // If both newWidth and newHeight are non-zero, just return them.
      int[] retValue = new int[2];
      retValue[0] = newWidth;
      retValue[1] = newHeight;
      return retValue;
   }


   	                               

   //------------------------------------------------------------------------
   public static String  GetResourceDirectoryName()  { return "/LSJava/LSResources/"; }

   //-----------------------------------------------------------------------------
   private static void  IssueErrorUnableToFindGraphicsFile( String graphicsFilename )   { LSMessageDialog.NewUserMessageDialog( "Error: Unable to find the graphics image file", graphicsFilename ); } 
    
}
