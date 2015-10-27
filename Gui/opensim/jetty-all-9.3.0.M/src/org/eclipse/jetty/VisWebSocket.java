/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eclipse.jetty;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.EncodeException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

/**
 *
 * @author Ayman
 */
@WebSocket(maxTextMessageSize = 64 * 1024)
public class VisWebSocket {
    private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
        
    @OnWebSocketConnect
    public void onOpen (Session peer) {
        peers.add(peer);
        System.out.println("Connected...");
    }

    public void onClose (Session peer) {
        peers.remove(peer);
        System.out.println("onClose");
    }
    
    @OnWebSocketMessage
    public void visMessage(String objJson) {
        System.out.println("visMessage: " + objJson);
        for (Session peer : peers) {
            //if (!peer.equals(session)) {
            //    peer.getBasicRemote().sendObject(objJson);
            //}
            int x=0;
        }
    }
}
