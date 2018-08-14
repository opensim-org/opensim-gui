/* -------------------------------------------------------------------------- *
 * OpenSim: MarkersDisplayer.java                                             *
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
 * MarkersDisplayer.java
 *
 * Created on July 12, 2011, 3:19 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view;

import java.awt.Color;
import java.util.Hashtable;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.opensim.modeling.Body;
import org.opensim.modeling.Marker;
import org.opensim.modeling.Model;
import org.opensim.modeling.OpenSimContext;
import org.opensim.modeling.SimbodyEngine;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;

/**
 *
 * @author Ayman
 */
public class MarkersDisplayer extends OpenSimvtkGlyphCloud {
    
    private double[] defaultMarkerColor = new double[]{1.0, 0.6, 0.8};
    protected Hashtable<Marker, Integer> mapMarkers2Glyphs = new Hashtable<Marker, Integer>(50);
    
    /** Creates a new instance of MarkersDisplayer */
    public MarkersDisplayer() {
        super(false);
       // Markers
       //defaultMarkerColor = ViewDB.getInstance().getDefaultMarkersColor();
       setColorRange(defaultMarkerColor, defaultMarkerColor);
       setSelectedColor(SelectedObject.defaultSelectedColor);
       setShapeName("marker");
       scaleByVectorComponents();
}

    public void addMarker(Marker marker) {
        int index = addLocation(marker);
        setVectorDataAtLocation(index,1,1,1);
        mapMarkers2Glyphs.put(marker, new Integer(index));
    }
    
    public void updateMarkerGeometry(Marker marker) {
      // Convert marker offset to global offset, and update the marker's glyph
      // location and the line to its parent body.
      double[] offset = new double[3];
      double[] gOffset = new double[3];
      double[] origin = {0.0, 0.0, 0.0};
      double[] gOrigin = new double[3];
      int index = mapMarkers2Glyphs.get(marker).intValue();
      SimbodyEngine de = marker.getModel().getSimbodyEngine();
      Model model =marker.getModel();
      /*marker.getOffset(offset);
      OpenSimContext context=OpenSimDB.getInstance().getContext(model);
      context.transformPosition(marker.getBody(), offset, gOffset);
      context.transformPosition(marker.getBody(), origin, gOrigin);*/
      setLocation(index, gOffset);
      setModified();
   }
    
   public void removeMarkerGeometry(Marker marker){
      int glyphID = mapMarkers2Glyphs.get(marker);
      remove(glyphID);
      mapMarkers2Glyphs.remove(marker);
   }
   public void setMarkerVisibility(Marker marker, boolean visible) {
      int index = (mapMarkers2Glyphs.get(marker)).intValue();
      //vtkLineSource markerLine = mapMarkers2Lines.get(marker);
      if (visible) {
         show(index);
         // Set the endpoints of the marker line, because the only other
         // place this is done is updateModelDisplay(), which is not called
         // in this case.
         double[] pos = new double[3];
         double[] gPos = new double[3];
         double[] origin = {0.0, 0.0, 0.0};
         double[] gOrigin = new double[3];
         /*
         Body body = marker.getBody();
         marker.getOffset(pos);
         OpenSimContext context=OpenSimDB.getInstance().getContext(body.getModel());
         context.transformPosition(body, origin, gOrigin);
         context.transformPosition(body, pos, gPos);*/
         //markerLine.SetPoint2(gPos);
      } else {
         hide(index);
         //markerLine.SetPoint1(0.0, 0.0, 0.0);
         //markerLine.SetPoint2(0.0, 0.0, 0.0);
      }
      setModified();
   }
}
