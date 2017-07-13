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
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.resource.Resource;
import org.openide.modules.Places;
import org.opensim.utils.TheApp;

/**
 *
 * @author Ayman
 */
public class JettyMain {
    private static boolean serverup = false;
    private static final String serverRootDir = TheApp.getInstallDir();
    private static final String pathToStartPage = "/threejs/editor/";
    private static final int serverPort = 8002;
    private static final int modelJsonServerPort = 8003;
    private static String serverWorkingDir = serverRootDir+"/threejs/editor/";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        startServer();
    }

    public static void startServer() {
        try {
            if (!serverup){
                // This server contains JSON files loaded by three.js.
                // However, three.js lives within the application, and we cannot
                // write files to the three.js folder.
                Server modelJsonServer = new Server(modelJsonServerPort);
                // TODO must create the VisualizerJSONServer folder if it doesn't
                // exist (I created it manually).
                URI modelJsonWebRootUri =
                        new File(Places.getUserDirectory()+"/VisualizerJSONServer/").toURI();

                ResourceHandler staticResource = new ResourceHandler();
                // Allow viewing a directory list of the files on the server
                // from a web browser (useful for debugging).
                staticResource.setDirectoriesListed(true);
                staticResource.setResourceBase(Places.getUserDirectory()+"/VisualizerJSONServer/");

                
                /*ContextHandler staticContextHandler = new ContextHandler();
                staticContextHandler.setContextPath("/*");
                staticContextHandler.setHandler(staticResource);
                */
                ServletContextHandler modelJsonContext = new ServletContextHandler();
                modelJsonContext.setContextPath("/*");
                modelJsonContext.setHandler(staticResource);
                //modelJsonContext.setInitParameter("org.eclipse.jetty.servlets.CrossOriginFilter", "/*");

                
                FilterHolder filterHolder = new FilterHolder(CrossOriginFilter.class);
                filterHolder.setInitParameter("allowedOrigins", "*");
                filterHolder.setInitParameter("allowedMethods", "GET, POST");

                //ServletContextHandler servletContextHandler;
                //servletContextHandler = new ServletContextHandler(modelJsonServer, "/", ServletContextHandler.SESSIONS);
                modelJsonContext.addFilter(filterHolder, "/*", null);
                
                // https://stackoverflow.com/questions/29969996/set-access-control-allow-origin-in-jetty/29970226#29970226
                /*FilterHolder filterHolder = new FilterHolder(CrossOriginFilter.class);
                filterHolder.setInitParameter("allowedOrigins", "*");
                filterHolder.setInitParameter("allowedMethods", "GET, POST, HEAD");*/
                // https://stackoverflow.com/questions/28190198/cross-origin-filter-with-embedded-jetty
                /*FilterHolder holder = new FilterHolder(CrossOriginFilter.class);
                holder.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
                holder.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*"); // should be http://localhost:8002
                holder.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
                holder.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");
                holder.setName("cross-origin");
                FilterMapping fm = new FilterMapping();
                fm.setFilterName("cross-origin");
                fm.setPathSpec("*");
                //handler.addFilter(holder, fm );
                //modelJsonContext.addFilter(filterHolder, "/*", null);
                modelJsonContext.getServletHandler().addFilter(holder, fm);
                */
             
                modelJsonServer.setHandler(modelJsonContext);
                modelJsonServer.start();

                // TODO code application logic here
                Server server = new Server(serverPort);
                

                String appDir = serverRootDir;
                File fp = new File(appDir);
                if (!fp.exists()){
                    // Try adding leading "/"
                    fp = new File("/"+appDir);
                    if (fp.exists())
                        appDir = "/"+appDir;
                    // else should abort
                }
                serverWorkingDir = appDir+"/threejs/editor/";
                URI webRootUri = new File(appDir).toURI();
                System.out.println("Web Root URI: %s%n"+webRootUri);

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
                // TODO is this necessary? modelJsonServer.join();

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
