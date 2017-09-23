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
    private Set<VisWebSocket> sockets = Collections.synchronizedSet(new HashSet<VisWebSocket>());
    private Observer observer;
    public static boolean debug = false;
    /** Creates a new instance of WebSocketDB */
    private WebSocketDB() {
        instance = this;
    }
    
    public void registerNewSocket(VisWebSocket socket) {
        if (debug) System.out.println("Register new Socket");
        if (!sockets.contains(socket))
            sockets.add(socket);
        if (debug) System.out.println("Socket count ="+sockets.size());
        socket.addObserver(observer);
        observer.update(socket, null);
    }
    
    public void unRegisterSocket(VisWebSocket socket) {
        if (debug) System.out.println("unRegister Socket");
        sockets.remove(socket);
        if (debug) System.out.println("Socket count ="+sockets.size());
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
        this.observer = observer; // ViewDB observer so it can sync new connections
    }
    
    public void broadcastMessageJson(JSONObject msg, VisWebSocket specificSocket)
    {
        if (specificSocket != null){
            specificSocket.sendVisualizerMessage(msg);
            return;
        }
        int i=0;
        for (VisWebSocket sock : sockets){
            if (i==1) continue;
            if (debug) System.out.println("Broadcast:"+msg.toJSONString()+"\n");
            sock.sendVisualizerMessage(msg);
            i++;
        }
    }
}
