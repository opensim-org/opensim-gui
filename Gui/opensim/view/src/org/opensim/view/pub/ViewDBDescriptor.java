/*
 * ViewDBDescriptor.java
 *
 * Created on May 2, 2008, 9:55 AM
 *
 * Copyright (c)  2005-2008, Stanford University, Ayman Habib
 * Use of the OpenSim software in source form is permitted provided that the following
 * conditions are met:
 * 	1. The software is used only for non-commercial research and education. It may not
 *     be used in relation to any commercial activity.
 * 	2. The software is not distributed or redistributed.  Software distribution is allowed
 *     only through https://simtk.org/home/opensim.
 * 	3. Use of the OpenSim software or derivatives must be acknowledged in all publications,
 *      presentations, or documents describing work in which OpenSim or derivatives are used.
 * 	4. Credits to developers may not be removed from executables
 *     created from modifications of the source.
 * 	5. Modifications of source code must retain the above copyright notice, this list of
 *     conditions and the following disclaimer.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR BUSINESS INTERRUPTION) OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 *  WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.opensim.view.pub;

import java.io.Serializable;
import java.util.ArrayList;
import org.opensim.view.ModelWindowVTKTopComponent;
import org.opensim.view.SingleModelVisuals;
import vtk.vtkMatrix4x4;

/**
 *
 * @author Ayman
 *
 * This class contains a short, more "textual" representation of ViewDB for serialization on entry/exit.
 * If we want to serialize to an XML file this class should implement Externalizable instead and follow Beans convention.
 *
 * Want to serialize: Camera position for each 3DWiew window
 *                    Model display offset for each model on display
 */
public class ViewDBDescriptor implements Serializable {
    
    private ArrayList<String> viewNames=new ArrayList<String>(5);
    private ArrayList<double[]> cameraAttributes=new ArrayList<double[]>(5);
    private ArrayList<double[]> offsetsList=new ArrayList<double[]>(5);
    private static final long serialVersionUID = 1L;
    /** Creates a new instance of ViewDBDescriptor */
    public ViewDBDescriptor() {
    }
    
    public ViewDBDescriptor(ViewDB viewDB) {
        Object[] views = viewDB.getOpenWindows();
        for (int i=0; i< views.length; i++){
            if (!(views[i] instanceof ModelWindowVTKTopComponent))
                continue;
            ModelWindowVTKTopComponent view = (ModelWindowVTKTopComponent) views[i];
            getViewNames().add(view.getDisplayName());
            getCameraAttributes().add(view.getCameraAttributes());
         }
        // Save model offsets
        ArrayList<SingleModelVisuals> visuals = viewDB.getModelVisuals();
        vtkMatrix4x4 offset;
        double[] zeroPoint = new double[]{0., 0., 0., 1.};
        for(int i=0; i<visuals.size(); i++){
            offset= ViewDB.getInstance().getModelVisualsTransform(visuals.get(i));
            double[] xlation=offset.MultiplyPoint(zeroPoint);
            getOffsetsList().add(new double[]{xlation[0], xlation[1], xlation[2]});
        }
    }

    public ArrayList<String> getViewNames() {
        return viewNames;
    }

    public void setViewNames(ArrayList<String> viewNames) {
        this.viewNames = viewNames;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
    class CameraAttributes {
        double[] attributes = new double[6];
    }

    public ArrayList<double[]> getCameraAttributes() {
        return cameraAttributes;
    }

    public void setCameraAttributes(ArrayList<double[]> cameraAttributes) {
        this.cameraAttributes = cameraAttributes;
    }

    public ArrayList<double[]> getOffsetsList() {
        return offsetsList;
    }

    public void setOffsetsList(ArrayList<double[]> offsetsList) {
        this.offsetsList = offsetsList;
    }

}
