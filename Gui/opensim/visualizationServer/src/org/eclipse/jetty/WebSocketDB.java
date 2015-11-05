/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eclipse.jetty;

import java.util.Collections;
import java.util.HashSet;
import java.util.Observer;
import java.util.Set;
import org.json.simple.JSONObject;

/**
 *
 * @author Ayman
 */
public class WebSocketDB {
    static WebSocketDB instance;
    private static Set<VisWebSocket> sockets = Collections.synchronizedSet(new HashSet<VisWebSocket>());
    private static Observer observer;
    
    /** Creates a new instance of WebSocketDB */
    private WebSocketDB() {
        instance = this;
    }
    
    static void registerNewSocket(VisWebSocket socket) {
        sockets.add(socket);
        socket.addObserver(observer);
    }
    
    static void unRegisterSocket(VisWebSocket socket) {
        sockets.remove(socket);
    }
    
    static public WebSocketDB getInstance() {
        if (instance == null)
            instance = new WebSocketDB();
        
        return instance;
    }

    /**
     * @param observer the observer to set
     */
    public void setObserver(Observer observer) {
        this.observer = observer;
    }
    
    public void broadcastMessageJson(JSONObject selected)
    {
        /*for (VisWebSocket sock : sockets){
            sock.sendSelection(selected);
        }*/
        sockets.iterator().next().sendSelection(selected);
    }
}
