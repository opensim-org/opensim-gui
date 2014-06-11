/*
 * SelectionListener.java
 *
 * Created on April 9, 2009, 10:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view;

import vtk.vtkAssemblyPath;

/**
 *
 * @author ayman
 */
public interface SelectionListener {
    void pickUserObject(vtkAssemblyPath asmPath, int cellId);
    
}
