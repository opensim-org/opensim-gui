/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eclipse.jetty;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 *
 * @author Ayman
 */
@WebSocket(maxTextMessageSize = 64 * 1024, maxIdleTime=10000000)
public class VisWebSocket extends Observable { // Socket to handle incoming traffic from Browser
    private static final Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
    
    public VisWebSocket(){
        // Register socket with WebSocketDB so it can be called back
        WebSocketDB.getInstance().registerNewSocket(this);
    }
    
    @OnWebSocketConnect
    public void onOpen (Session peer) {
        if (!peers.contains(peer)){
            peers.add(peer);
            System.out.println("Connected...");
        }
    }
    
    @OnWebSocketClose
    public void onClose (Session peer, int in, String cause) {
        peers.remove(peer);
        System.out.println("onClose");
        WebSocketDB.getInstance().unRegisterSocket(this);
    }
    
    @OnWebSocketError
     public void onError (Session peer, Throwable er) {
        System.out.println("onError");
    }        
    // Receive message from Visualizer
    @OnWebSocketMessage
    public void visMessage(String stringToParse) {
        try {
            //System.out.println("visMessage: " + stringToParse);
            JSONParser parser = new JSONParser();
            JSONObject jsonObj = (JSONObject) parser.parse(stringToParse);
            setChanged();
            notifyObservers(jsonObj);
        } catch (ParseException ex) {
            Logger.getLogger(VisWebSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void sendVisualizerMessage(JSONObject selected) {
        for (Session peer:peers){          
            try {
                peer.getRemote().sendString(selected.toJSONString());
                
            } catch (IOException ex) {
                Logger.getLogger(VisWebSocket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
     }
    
}
