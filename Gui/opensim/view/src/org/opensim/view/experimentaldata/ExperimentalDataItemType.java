package org.opensim.view.experimentaldata;



        
public enum ExperimentalDataItemType{                
    PointData(3),         
    VectorData(3),         
    MarkerData(3),         
    ForceAndPointData(6),         
    JointForceData(3),   
    BodyForceData(3),
    MomentData(3),         
    Unknown(1);

    private final int numberOfColumns;
    
    ExperimentalDataItemType(int numColumns) {
        this.numberOfColumns = numColumns;
    }
    
    public int getNumberOfColumns() {
        return numberOfColumns;
    }
    
}