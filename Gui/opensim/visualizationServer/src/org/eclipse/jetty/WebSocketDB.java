/* -------------------------------------------------------------------------- *
 * OpenSim: WebSocketDB.java                                                  *
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
            if (i==1){
                i++;
                continue;
            }
            if (debug) System.out.println("Broadcast:"+msg.toJSONString()+"\n");
            sock.sendVisualizerMessage(msg);
            i++;
        }
    }
}
