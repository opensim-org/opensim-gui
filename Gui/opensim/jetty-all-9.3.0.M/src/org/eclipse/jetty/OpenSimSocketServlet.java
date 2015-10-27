/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eclipse.jetty;

import javax.servlet.annotation.WebServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 *
 * @author Ayman
 */
@WebServlet(name = "OpenSim WebSocket Servlet", urlPatterns = { "/visEndpoint" })
public class OpenSimSocketServlet extends WebSocketServlet {
 
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.getPolicy().setIdleTimeout(10000);
        factory.register(VisWebSocket.class);
    }
}