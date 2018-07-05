/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view.nodes;

import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.opensim.modeling.Body;
import org.opensim.modeling.ComponentIterator;
import org.opensim.modeling.ComponentsList;
import org.opensim.modeling.Component;
import org.opensim.modeling.Frame;

/**
 * Utility class to help share node creating functions between OpenSimNode subclasses
 * that do not have common ancestor. All methods are public static
 * 
 * @author Ayman
 */
public class OpenSimNodeHelper {
   /* Utility function to create child nodes for descendent frames
    */
    static public void createFrameNodes(Children children, Component comp) {
        // Find Frames and make nodes for them (PhysicalOffsetFrames)
        ComponentsList descendents = comp.getComponentsList();
        ComponentIterator compIter = descendents.begin();
        while (!compIter.equals(descendents.end())) {
            Frame frame = Frame.safeDownCast(compIter.__deref__());
            if (Body.safeDownCast(frame)==null && frame !=null) {
                OneFrameNode node = new OneFrameNode(frame);
                Node[] arrNodes = new Node[1];
                arrNodes[0] = node;
                children.add(arrNodes);
            }
            compIter.next();
        }
    }
}
