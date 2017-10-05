/* -------------------------------------------------------------------------- *
 * OpenSim: LabOutputAnnotation.java                                          *
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
 * LabOutputAnnotation.java
 *
 * Created on August 20, 2010, 6:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.customGui;

import java.text.DecimalFormat;
import org.opensim.modeling.Analysis;
import org.opensim.modeling.AnalysisSet;
import org.opensim.modeling.ArrayStorage;
import org.opensim.modeling.Model;
import org.opensim.modeling.Storage;
import org.opensim.view.pub.ViewDB;
import vtk.vtkActor;
import vtk.vtkActor2D;

/**
 *
 * @author ayman
 */
public abstract class LabOutputAnnotation implements LabOutputInterface{
    
    private String quantitySpecfication;
    private String textTemplate="result: ____";
    private Storage sourceStorage=null;
    private int columnIndex=-1;
    
    //private vtkActor2D cornerActor;
    
    /**
     * Creates a new instance of LabOutputAnnotation
     */
    public LabOutputAnnotation(LabOutputText labOutputText) {
        textTemplate=labOutputText.getTextTemplate();
        quantitySpecfication=labOutputText.getQuantitySpecfication();
    }

    public String getQuantitySpecfication() {
        return quantitySpecfication;
    }

    public void setQuantitySpecfication(String quantitySpecfication) {
        this.quantitySpecfication=quantitySpecfication;
    }

    public void updateDisplay(double aT) {
        DecimalFormat df= new DecimalFormat("#.###");
        int timeIndex = sourceStorage.findIndex(aT);
        double value = sourceStorage.getStateVector(timeIndex).getData().getitem(columnIndex);
        String newValue = df.format(value);
        String newLabel = textTemplate.replaceAll("____", newValue);
        updateText(newLabel);
    }

    public void connectQuantityToSource(Model model) {
        String[] specs=getQuantitySpecfication().split("\\.");
        AnalysisSet as = model.getAnalysisSet();
        boolean found = false;
        if (as!=null && as.getSize()>0){
            Analysis a= as.get(specs[0]);
            if (a != null && specs.length>=1){
                ArrayStorage storages=a.getStorageList();
                for(int idx=0; idx<storages.getSize(); idx++){
                    String dName = storages.get(idx).getName();
                }
                sourceStorage = storages.get(specs[1]);
                if (sourceStorage!=null && specs.length>=2){
                    columnIndex = sourceStorage.getColumnLabels().findIndex(specs[2])-1;
                    vtkActor2D act = getAnnotationActor();
                    updateText(textTemplate);
                }
            }
        }
         
    }

    public String getTextTemplate() {
        return textTemplate;
    }

    public void setTextTemplate(String textTemplate) {
        this.textTemplate = textTemplate;
    }
    
    
    abstract void updateText(final String newText);
    
    abstract vtkActor2D getAnnotationActor();
    
}
