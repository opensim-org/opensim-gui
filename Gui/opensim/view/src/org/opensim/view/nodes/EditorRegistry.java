/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.opensim.view.nodes;

import java.beans.PropertyEditorSupport;
import java.util.HashMap;

/**
 *
 * @author Ayman
 */
public final class EditorRegistry {
    static HashMap<String, PropertyEditorSupport> registry = new HashMap<String, PropertyEditorSupport>();
    
    static public PropertyEditorSupport getEditor(String clazzName) {
        System.out.println("get editor for type:"+clazzName);
        return registry.get(clazzName);
    }
    
    static public void addEditor(String clazzName, PropertyEditorSupport pes) {
        registry.put(clazzName, pes);
    }
    
}
