/* -------------------------------------------------------------------------- *
 * OpenSim: VisWebSocket.java                                                 *
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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.Collections;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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
@WebSocket(maxTextMessageSize = 64 * 4096, maxIdleTime=10000000)
public class VisWebSocket extends Observable { // Socket to handle incoming traffic from Browser
    private static final Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    
    public VisWebSocket(){
        // Register socket with WebSocketDB so it can be called back
        WebSocketDB.getInstance().registerNewSocket(this);
    }
    
    @OnWebSocketConnect
    public void onOpen (Session peer) {
        if (!peers.contains(peer)){
            peers.add(peer);
            System.out.println("Connected... adding peer");
            this.setChanged();
            this.notifyObservers();
        }
        executorService.scheduleAtFixedRate(() -> {
                    try {
                        String data = "Ping";
                        ByteBuffer payload = ByteBuffer.wrap(data.getBytes());
                        peer.getRemote().sendPing(payload);
                        System.out.println("Sending ping to peer.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                },
                1, 1, TimeUnit.MINUTES);        
        
    }
    
    @OnWebSocketClose
    public void onClose (Session peer, int in, String cause) {
        peers.remove(peer);
        System.out.println("onClose: cause="+cause);
        WebSocketDB.getInstance().unRegisterSocket(this);
    }
    
    @OnWebSocketError
     public void onError (Session peer, Throwable er) {
        System.out.println("onError:"+er.getMessage());
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
           peer.getRemote().sendStringByFuture(selected.toJSONString());
        }
     }
    
}
