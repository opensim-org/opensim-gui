/*
 * Copyright (c)  2005-2008, Stanford University
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
/*
 * AnnotatedMotion.java
 *
 * Created on February 19, 2009, 9:16 AM
 *
 */

package org.opensim.view.experimentaldata;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Vector;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.opensim.modeling.ArrayDouble;
import org.opensim.modeling.ArrayStr;
import org.opensim.modeling.StateVector;
import org.opensim.modeling.Storage;
import org.opensim.view.motions.MotionControlJPanel;
import org.opensim.view.motions.MotionDisplayer;
import org.opensim.view.motions.MotionsDB;
import vtk.vtkTransform;

/**
 *
 * @author ayman
 */
public class AnnotatedMotion extends Storage { // MotionDisplayer needs to know when the attributes have changed
    
    private Vector<String> markerNames=null;
    private Vector<String> forceNames=null;
    private static Vector<String[]> patterns = new Vector<String[]>(7);
    private static Vector<ExperimentalDataItemType> classifications = new Vector<ExperimentalDataItemType>(7);
    private Vector<ExperimentalDataObject> classified=new Vector<ExperimentalDataObject>(10);
    private double[] boundingBox = new double[]{1000., 10000., 10000., -1., -1., -1.};
    private double unitConversion = 1.0;
    private boolean boundingBoxComputed=false;
    private double[] currentRotations = new double[]{0., 0., 0.};
    private double frameRate=0.;
    private double cameraRate=0.;
    private double dataRate=0.;
    private MotionDisplayer motionDisplayer;
    private double displayForceScale = .001;
    private String displayForceShape = "arrow";
    /** Creates a new instance of AnnotatedMotion 
     * This constructor is called when a trc file is read (so we know it is 
     * Marker only data already.
     */
    public AnnotatedMotion(Storage storage, ArrayStr markerNames) {
        super(storage);
        setupPatterns();
        int rowSize = storage.getSmallestNumberOfStates();
        double[] mins = new double[rowSize];
        double[] maxs = new double[rowSize];
        for(int t=0;t<rowSize;t++){
            mins[t]=100000;
            maxs[t]=-100000;
        }
        getMinMax(mins, maxs);
        int minXIndex=-1;
        int maxXIndex=-1;
        for(int i=0; i<markerNames.getSize(); i++){
            String markerName=markerNames.getitem(i);
            ExperimentalDataObject markerObject = new MotionObjectBodyMarker(
                    ExperimentalDataItemType.MarkerData, markerName, i*3);
            classified.add(markerObject);
            for(int coord=0; coord<3; coord++){
                //System.out.println("checking index "+(i*3+coord)+"& "+(i*3+coord+3));
                if (getBoundingBox()[coord] > mins[i*3+coord]){
                    getBoundingBox()[coord] = mins[i*3+coord];
                    if (coord==1) minXIndex=i;
                }
                if (getBoundingBox()[coord+3] < maxs[i*3+coord]){
                    getBoundingBox()[coord+3] = maxs[i*3+coord];
                    if (coord==1) maxXIndex=i;
                }
            }
            //System.out.println("-------------------------");

        }
        System.out.println("minY, maxY are at markers "+minXIndex+", "+maxXIndex);
        System.out.println("Bounding box="+
                getBoundingBox()[0]+", "+
                getBoundingBox()[1]+", "+
                getBoundingBox()[2]+", "+
                getBoundingBox()[3]+", "+
                getBoundingBox()[4]+", "+
                getBoundingBox()[5]
                );
        for (int i=0; i<6; i++) getBoundingBox()[i]/= getUnitConversion();
        setBoundingBoxComputed(true);
    }

        /** Creates a new instance of AnnotatedMotion */
    public AnnotatedMotion(Storage storage) {
        super(storage);
        //MotionsDB.getInstance().saveStorageFileName(this, MotionsDB.getInstance().getStorageFileName(storage));
        setupPatterns();
        classified=classifyColumns();
    }

    public Vector<String> getMarkerNames() {
        Vector<String> markers= getNamesOfObjectsOfType(ExperimentalDataItemType.MarkerData);
        markers.addAll(getNamesOfObjectsOfType(ExperimentalDataItemType.PointData));
        return markers;
    }

    public void setMarkerNames(Vector<String> markerNames) {
        this.markerNames = markerNames;
    }

    public Vector<String> getForceNames() {
        Vector<String> forces =  getNamesOfObjectsOfType(ExperimentalDataItemType.PointForceData);
        forces.addAll(getNamesOfObjectsOfType(ExperimentalDataItemType.BodyForceData));
        return forces;
    }

    private void setupPatterns() {
        patterns.add(0,  new String[]{"_vx", "_vy", "_vz", "_px", "_py", "_pz"});
        patterns.add(1,  new String[]{"_vx", "_vy", "_vz"});
        patterns.add(2,  new String[]{"_tx", "_ty", "_tz"});
        patterns.add(3,  new String[]{"_px", "_py", "_pz"});
        patterns.add(4,  new String[]{"_1", "_2", "_3"});
        patterns.add(5,  new String[]{"_x", "_y", "_z"});
        patterns.add(6,  new String[]{"_fx", "_fy", "_fz"});
         classifications.add(0, ExperimentalDataItemType.PointForceData);
        classifications.add(1, ExperimentalDataItemType.PointData);
        classifications.add(2, ExperimentalDataItemType.PointData);
        classifications.add(3, ExperimentalDataItemType.PointData);
        classifications.add(4, ExperimentalDataItemType.PointData);
        classifications.add(5, ExperimentalDataItemType.PointData);
        classifications.add(6, ExperimentalDataItemType.BodyForceData);
         
    }
    public Vector<ExperimentalDataObject> classifyColumns() {
        ArrayStr labels=getColumnLabels();
        int labelsSz = labels.getSize();
        Vector<String> labelsVector = labels.toVector();
        Vector<ExperimentalDataObject> classified = new Vector<ExperimentalDataObject>(4);
        // Start at 1 since label[0] is time
        for (int i=1;i< labelsVector.size(); i++){
            boolean found =false;
            ExperimentalDataItemType columnType = ExperimentalDataItemType.Unknown;
            String label = labelsVector.get(i);
            String baseName="";
            for(int patternIdx=0; patternIdx <patterns.size() && !found; patternIdx++) {
                boolean foundPatternAtIdx=true;
                String nextLabel=label;
                // For the pattern (patterns[patternIdx]) check exact match
                for(int k=0; k<patterns.get(patternIdx).length; k++){
                    String testAgainst=patterns.get(patternIdx)[k];
                    baseName=label.substring(0, label.length()-testAgainst.length()); // Could be moved outside k loop
                    if((i+k) > (labelsVector.size()-1)){
                        foundPatternAtIdx=false;
                        break;               
                    }
                    label= labelsVector.get(i+k);
                    if (!(label.endsWith(testAgainst))){
                        foundPatternAtIdx=false;
                        break;
                    }
                }
                if (foundPatternAtIdx){
                    found=true;
                    columnType = classifications.get(patternIdx);
                    if (patternIdx ==0){
                        // Force _v? followed by Point _p?
                        MotionObjectPointForce f = new MotionObjectPointForce(columnType, baseName+"_v", i-1);
                        f.setPointIdentifier(baseName+"_p");
                        classified.add(f);
                    }
                    else {
                        String patternString  = patterns.get(patternIdx)[0];
                        String prefix = patternString.substring(0, patternString.length()-1);
                        switch(classifications.get(patternIdx)){
                            case PointForceData:
                                //classified.add(new MotionObjectBodyForceAtVarPoint(columnType, baseName+prefix, i-1));
                                assert(false);
                                break;
                            case PointData:
                            case MarkerData:
                                classified.add(new MotionObjectBodyMarker(columnType, baseName+prefix, i-1));
                                break;
                            case BodyForceData:
                                 classified.add(new MotionObjectPointForce(columnType, baseName+prefix, i-1));
                                break;
                        }                           
                            
                    }
                    //System.out.println("Found "+columnType.toString()+ " at index "+i);
                    i+=(columnType.getNumberOfColumns()-1);
                    break;
                }
            }
            if (!found){
                classified.add(new ExperimentalDataObject(columnType, label, i-1));
                //System.out.println("Column "+columnType.toString()+ " unclassified");
            }
        }
        return classified;
    }

    public Vector<ExperimentalDataObject> getClassified() {
        return classified;
    }

    public Vector<String> getOtherNames() {
        return getNamesOfObjectsOfType(ExperimentalDataItemType.Unknown);
    }

    private Vector<String> getNamesOfObjectsOfType(ExperimentalDataItemType aDataType) {
        Vector<String> results = new Vector<String>(4);
        if (classified !=null && classified.size()!=0){
            for(ExperimentalDataObject dataObject:classified){
                if (dataObject.getObjectType()==aDataType)
                    results.add(dataObject.getName());
            }     
        }
        return results;
    }

    void hideObject(ExperimentalDataObject dataObject) {
       dataObject.setDisplayed(false);
    }
    
    void showObject(ExperimentalDataObject dataObject) {
        dataObject.setDisplayed(true);
    }

    void toggleObjectTrail(ExperimentalDataObject obj) {
        // We'll create a separate table to keep track ofr trails. '
        MotionDisplayer displayer=MotionControlJPanel.getInstance().getMasterMotion().getDisplayer(this);
        if (displayer ==null) return;
        displayer.toggleTrail(obj);
        obj.setTrailDisplayed(!obj.isTrailDisplayed());
    }

    public final double[] getBoundingBox() {
        return boundingBox;
    }

    public double getUnitConversion() {
        return unitConversion;
    }

    public void setUnitConversion(double unitConversion) {
        this.unitConversion = unitConversion;
        for (int i=0; i<6; i++) getBoundingBox()[i]/= unitConversion;
    }

    public boolean isBoundingBoxComputed() {
        return boundingBoxComputed;
    }

    public void setBoundingBoxComputed(boolean boundingBoxComputed) {
        this.boundingBoxComputed = boundingBoxComputed;
    }
 
    public Storage applyTransform(vtkTransform vtktransform) {
        // Should know what data to apply xform to and how
        // for example mrkers are xformed as is but force vectors would not apply translations etc.
        // utilize the "clasified" table
        Storage motionCopy = new Storage(this);
        if (classified !=null && classified.size()!=0){
            for(ExperimentalDataObject dataObject:classified){
                if (dataObject.getObjectType()==ExperimentalDataItemType.PointData ||
                        dataObject.getObjectType()==ExperimentalDataItemType.MarkerData){
                    int startIndex = dataObject.getStartIndexInFileNotIncludingTime();
                    transformPointData(motionCopy, vtktransform, startIndex);
                } else if (dataObject.getObjectType()==ExperimentalDataItemType.PointForceData){
                    int startIndex = dataObject.getStartIndexInFileNotIncludingTime();
                    // First vector, then position
                    // If we allow for translations the first line may need to change
                    transformPointData(motionCopy, vtktransform, startIndex);
                    transformPointData(motionCopy, vtktransform, startIndex+3);
                }
                else {
                    System.out.println("bad type="+dataObject.getObjectType());
                    //throw new UnsupportedOperationException("Not yet implemented");
                }
          }
        }
        return motionCopy;
    }

    private void transformPointData(final Storage motionCopy, final vtkTransform vtktransform, final int startIndex) {
        double[] point3 = new double[]{0., 0., 0. };
        for (int rowNumber=0; rowNumber<getSize(); rowNumber++){
            StateVector row = motionCopy.getStateVector(rowNumber);
            //if (rowNumber==0) System.out.println("Start index="+startIndex+" row size="+row.getSize());
            for(int coord=0; coord <3; coord++) {
                point3[coord] = row.getData().getitem(startIndex+coord);
            }
            double[] xformed = vtktransform.TransformDoublePoint(point3); //inplace
            for(int coord=0; coord <3; coord++) {
                row.getData().setitem(startIndex+coord, xformed[coord]);
            }
            
        }
    }

    void saveRotations(double[] d) {
        for(int i=0; i<3; i++)
            getCurrentRotations()[i]=d[i];
    }

    public double[] getCurrentRotations() {
        return currentRotations;
    }

    public void setCurrentRotations(double[] currentRotations) {
        this.currentRotations = currentRotations;
    }
    
    
    public void getMinMax(double[] mins, double[] maxs) {
      StateVector sv = getStateVector(0);
      int rowSize = sv.getSize();
      for(int t=0; t<rowSize;t++){
          mins[t] =Double.POSITIVE_INFINITY;
          maxs[t] =Double.NEGATIVE_INFINITY;
      }
      for(int i=0; i< getSize(); i++){
          StateVector vec = getStateVector(i);
          // Timestamp
          // Dataitem j
          ArrayDouble vecData = vec.getData();
          for(int j=0; j< rowSize; j++){
              double dValue = vecData.getitem(j);
              if (Double.isNaN(dValue)) // Gaps
                  continue;
              if ( dValue< mins[j]) 
                  mins[j]=dValue;
              if (dValue > maxs[j]) 
                  maxs[j]=dValue;
          }
      }
  }

  public void saveAs(String newFile, vtkTransform transform) throws FileNotFoundException, IOException {
          if (newFile.endsWith(".trc"))
             saveAsTRC(newFile, transform);
          else if (newFile.endsWith(".mot"))
              saveAsMot(newFile, transform);
          else 
              DialogDisplayer.getDefault().notify(
                      new NotifyDescriptor.Message("Please specify either .trc or .mot file extension"));
  }

    private void saveAsMot(String newFile, vtkTransform transform) {
        // Make a full copy of the motion then xform in place.
        Storage xformed = applyTransform(transform);
        xformed.print(newFile);
    }
    
   private void saveAsTRC(String newFile, vtkTransform transform) throws FileNotFoundException, IOException {
        OutputStream ostream = new FileOutputStream(newFile);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ostream));
        writer.write("PathFileType        4      (X/Y/Z)      " + newFile + "         ");
        writer.newLine();
        writer.write("DataRate\tCameraRate\tNumFrames\tNumMarkers\tUnits\tOrigDataRate\tOrigDataStartFrame\tOrigNumFrames");
        writer.newLine();
        Vector<String> markerNames = getMarkerNames();
        /*
         *   ok = readDoubleFromString(line, &aSMD._dataRate);
             ok = ok && readDoubleFromString(line, &aSMD._cameraRate);
             ok = ok && readIntegerFromString(line, &aSMD._numFrames);
             ok = ok && readIntegerFromString(line, &aSMD._numMarkers);
             ok = ok && readStringFromString(line, buffer);
   */
        writer.write(getDataRate()+"\t"+
                getCameraRate()+"\t"+
                getSize()+"\t"+markerNames.size()+"\tmm\t"+
                getDataRate()+"\t"+ "1\t"+getSize());
        writer.newLine();
        writer.write("Frame#      Time ");
        String headerLine = "   ";
        for (int i = 0; i < markerNames.size(); i++) {
            writer.write(markerNames.get(i) + "\t\t\t");
            headerLine = headerLine.concat("X" + (i+1) + "\t" + "Y" + (i+1) + "\t" + "Z" + (i+1) + "\t");
        }
        writer.newLine();
        writer.write(headerLine);
        writer.newLine();
        Storage xformed = applyTransform(transform);
        for (int row=0; row < xformed.getSize(); row++){
            StateVector rowData = xformed.getStateVector(row);
            writer.write((row+1)+"\t"+rowData.getTime()+"\t");
            ArrayDouble data = rowData.getData();
            for(int col=0; col <data.getSize(); col++){
                if (Double.isNaN(data.getitem(col)))
                    writer.write("\t");
                else
                    writer.write(data.getitem(col)+"\t");
            }
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }

    public void setDataRate(double d) {
        this.dataRate = d;
    }

    public void setCameraRate(double d) {
        this.cameraRate = d;
    }

    public double getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(double frameRate) {
        this.frameRate = frameRate;
    }

    public double getCameraRate() {
        return cameraRate;
    }

    public double getDataRate() {
        return dataRate;
    }

    /**
     * @param motionDisplayer the motionDisplayer to set
     */
    public void setMotionDisplayer(MotionDisplayer motionDisplayer) {
        this.motionDisplayer = motionDisplayer;
    }
    
    public void updateMotionDisplayer() {
        getMotionDisplayer().updateMotionObjects();
    }

    /**
     * @return the displayForceScale
     */
    public double getDisplayForceScale() {
        return displayForceScale;
    }

    /**
     * @param displayForceScale the displayForceScale to set
     */
    public void setDisplayForceScale(double displayScale) {
        this.displayForceScale = displayScale;
        updateMotionDisplayer();
    }

    public String getDisplayForceShape() {
        return displayForceShape;
    }

    /**
     * @param displayForceShape the displayForceShape to set
     */
    public void setDisplayForceShape(String displayForceShape) {
        this.displayForceShape = displayForceShape;
        updateMotionDisplayer();
    }

    /**
     * @return the motionDisplayer
     */
    public MotionDisplayer getMotionDisplayer() {
        return motionDisplayer;
    }

    public void updateComponentGeometry(ArrayDouble interpolatedStates) {
        if (classified !=null && classified.size()!=0){
            for(ExperimentalDataObject dataObject:classified){
                dataObject.updateGeometry(interpolatedStates);
            }
        }
    }
    
}
