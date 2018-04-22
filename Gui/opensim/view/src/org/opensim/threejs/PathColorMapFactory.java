/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.threejs;

import java.util.HashMap;

/**
 *
 * @author Ayman-NMBL
 */
public class PathColorMapFactory {
    private static final HashMap<String, PathColorMap> mapNamesToColorMaps = new HashMap<String, PathColorMap>();
    static void registerPathColorMap(String name, PathColorMap colorMap){
        mapNamesToColorMaps.put(name, colorMap);
    }
    static PathColorMap getColorMap(String name){
        if (mapNamesToColorMaps.containsKey(name))
            return mapNamesToColorMaps.get(name);
        return new LegacyPathColorMap();
    }
}
