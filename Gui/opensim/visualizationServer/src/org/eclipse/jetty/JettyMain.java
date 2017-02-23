/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eclipse.jetty;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.opensim.utils.TheApp;

/**
 *
 * @author Ayman
 */
public class JettyMain {
    private static boolean serverup = false;
    private static final String serverRootDir = TheApp.getInstallDir();
    private static final String pathToStartPage = "/threejs/editor/";
    private static final int serverPort = 8001;
    private static final String serverWorkingDir = serverRootDir+"/threejs/editor/";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            if (!serverup){
                // TODO code application logic here
                Server server = new Server(serverPort);

                String appDir = serverRootDir;
                URI webRootUri = new File(appDir).toURI();
                System.err.printf("Web Root URI: %s%n",webRootUri);

                ServletContextHandler context = new ServletContextHandler();
                context.setContextPath("/");
                context.setBaseResource(Resource.newResource(webRootUri));
                context.setWelcomeFiles(new String[] { pathToStartPage+"index.html" });

                context.getMimeTypes().addMimeMapping("txt","text/plain;charset=utf-8");
                context.addServlet(DefaultServlet.class,"/");
                context.addServlet(OpenSimSocketServlet.class, "/visEndpoint");
                server.setHandler(context);

                server.start();
                serverup = true;
                server.join();

            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(JettyMain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(JettyMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the serverRootDir
     */
    public static String getServerRootDir() {
        return serverRootDir;
    }

    /**
     * @return the serverPort
     */
    public static int getServerPort() {
        return serverPort;
    }

    /**
     * @return the pathToStartPage
     */
    public static String getPathToStartPage() {
        return pathToStartPage;
    }

    /**
     * @return the serverup
     */
    public static boolean isServerup() {
        return serverup;
    }

    /**
     * @return the serverWorkingDir
     */
    public static String getServerWorkingDir() {
        return serverWorkingDir;
    }

}
