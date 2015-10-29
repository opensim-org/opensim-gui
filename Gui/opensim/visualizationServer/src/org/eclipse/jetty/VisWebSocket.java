/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eclipse.jetty;

import java.util.Collections;
import java.util.HashSet;
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
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Ayman
 */
@WebSocket(maxTextMessageSize = 64 * 1024, maxIdleTime=10000000)
public class VisWebSocket {
    private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
        
    public VisWebSocket(){
        // Register socket with WebSocketDB so it can be called back
        WebSocketDB.registerNewSocket(this);
    }
    
    @OnWebSocketConnect
    public void onOpen (Session peer) {
        peers.add(peer);
        System.out.println("Connected...");
    }
    
    @OnWebSocketClose
    public void onClose (Session peer, int in, String cause) {
        peers.remove(peer);
        System.out.println("onClose");
    }
    
    @OnWebSocketError
     public void onError (Session peer, Throwable er) {
        System.out.println("onError");
    }        
    @OnWebSocketMessage
    public void visMessage(String stringToParse) {
        try {
            System.out.println("visMessage: " + stringToParse);
            JSONParser parser = new JSONParser();
            JSONObject jsonObj = (JSONObject) parser.parse(stringToParse);
            System.out.println(jsonObj.get("uuid"));
            System.out.println(jsonObj.get("name"));
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Selected uuid:"+jsonObj.get("uuid")
                    +" name:"+jsonObj.get("name")));
            for (Session peer : peers) {
                //if (!peer.equals(session)) {
                //    peer.getBasicRemote().sendObject(objJson);
                //}
                int x=0;
            }
        } catch (ParseException ex) {
            Logger.getLogger(VisWebSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
