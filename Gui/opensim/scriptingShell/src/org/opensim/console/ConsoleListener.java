/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.console;

import java.awt.event.KeyEvent;

/**
 * Listener interface for all scripting shell console actions
 * @author Kevin Xu
 */
public interface ConsoleListener {                  
    
    public void onExecution(String commands);
    public void onKeyPressed(KeyEvent e);
}
