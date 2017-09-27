
/////////////////////////////////////////////////////////
//  Bare Bones Browser Launch                          //
//  Version 1.5                                        //
//  December 10, 2005                                  //
//  Supports: Mac OS X, GNU/Linux, Unix, Windows XP    //
//  Example Usage:                                     //
//     String url = "http://simtk.org/";       //
//     BareBonesBrowserLaunch.openURL(url);            //
//  Public Domain Software -- Free to Use as You Like  //
/////////////////////////////////////////////////////////
package org.opensim.utils;

import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JOptionPane;

public final class BrowserLauncher {

   private static final String errMsg = "Error attempting to launch web browser";

   public static void openURL(String url) {
      String osName = System.getProperty("os.name");
      try {
         if (osName.startsWith("Mac OS")) {
            Class fileMgr = Class.forName("com.apple.eio.FileManager");
            Method openURL = fileMgr.getDeclaredMethod("openURL",
               new Class[] {String.class});
            openURL.invoke(null, new Object[] {url});
            }
         else if (osName.startsWith("Windows"))
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
         else { //assume Unix or Linux
            String[] browsers = {
               "chrome", "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
            String browser = null;
            for (int count = 0; count < browsers.length && browser == null; count++)
               if (Runtime.getRuntime().exec(
                     new String[] {"which", browsers[count]}).waitFor() == 0)
                  browser = browsers[count];
            if (browser == null)
               throw new Exception("Could not find web browser");
            else
               Runtime.getRuntime().exec(new String[] {browser, url});
            }
         }
      catch (Exception e) {
         JOptionPane.showMessageDialog(null, errMsg + ":\n" + e.getLocalizedMessage());
         }
      }

   /*
    * Check for internet connection
    * by pinging a couple of reliable sites. Yet to find better way to do this.
    */
    public static boolean isConnected() {

        boolean rc1 = true, rc2 = true;
        
        try {
            URL url = new URL("http://www.stanford.edu");
            HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();
            urlConnect.setUseCaches(false);
            Object objData = urlConnect.getContent();
            if(objData == null) {
                rc1 = false;
            }            
        } catch (Exception e) {
            e.printStackTrace();
            rc1 = false;  
        }

        try {
            URL url = new URL("http://www.google.com");
            HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();
            urlConnect.setUseCaches(false);
            Object objData = urlConnect.getContent();
            if(objData == null) {
                rc2 = false;
            }            
        } catch (Exception e) {
            e.printStackTrace();
            rc2 = false;  
        }
        
        System.out.println("RC1: " + rc1 + " RC2: " + rc2);
        return rc1 | rc2;
    }
   }
