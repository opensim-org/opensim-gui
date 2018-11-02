/* -------------------------------------------------------------------------- *
 * OpenSim: JettyMain.java                                                    *
 * -------------------------------------------------------------------------- *
 * OpenSim is a toolkit for musculoskeletal modeling and simulation,          *
 * developed as an open source project by a worldwide community. Development  *
 * and support is coordinated from Stanford University, with funding from the *
 * U.S. NIH and DARPA. See http://opensim.stanford.edu and the README file    *
 * for more information including specific grant numbers.                     *
 *                                                                            *
 * Copyright (c) 2005-2017 Stanford University and the Authors                *
 * Author(s): Ayman Habib                                                     *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may    *
 * not use this file except in compliance with the License. You may obtain a  *
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0          *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 * -------------------------------------------------------------------------- */
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
    private static int SERVER_PORT = 8002;
    private static String serverWorkingDir = serverRootDir+"/threejs/editor/";
    
    // Use static block to adjust SERVER_PORT if specified
    static {
        if (System.getProperty("opensim.port") != null) {
            int port = Integer.parseInt(System.getProperty("opensim.port"));
            // Valid numbers are typycally between 81 and 8999
            if (port > 80 && port < 9000) {
                SERVER_PORT = port;
            }
            else
                System.out.println("Port number outside range (80 , 9000): port 8002 will be used");
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        startServer();
    }

    public static void startServer() {
        try {
            if (!serverup){
                
                // TODO code application logic here
                Server server = new Server(SERVER_PORT);

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
     * @return the SERVER_PORT
     */
    public static int getSERVER_PORT() {
        return SERVER_PORT;
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
