package org.opensim.view.experimentaldata;



        
public enum ExperimentalDataItemType{                
    PointData(3),         
    MarkerData(3),         
    PointForceData(6),
    BodyForceData(3),
    Unknown(1);

    private final int numberOfColumns;
    
    ExperimentalDataItemType(int numColumns) {
        this.numberOfColumns = numColumns;
    }
    
    public int getNumberOfColumns() {
        return numberOfColumns;
    }
    
}