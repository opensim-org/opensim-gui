/*
 * FrameActor.java
 *
 * Created on April 5, 2010, 4:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package vtk;

/**
 *
 * @author ayman
 */
public class FrameActor extends vtkActor{
    
    private double defaultScale=.1;
    private double defaultRadius=.001;
    private static int Resolution=8;
    private boolean symmetric=false;
    private vtkAxes axesGeometry;
    private vtkTubeFilter dFilter;
    /** Creates a new instance of FrameActor */
    public FrameActor() {
        axesGeometry= new vtkAxes();
        axesGeometry.Update();
        vtkDataArray dArray=axesGeometry.GetOutput().GetPointData().GetScalars();
        dArray.SetTuple1(2, 0.5);
        dArray.SetTuple1(3, 0.5);
        dArray.SetTuple1(4, 1.0);
        dArray.SetTuple1(5, 1.0);
        axesGeometry.GetOutput().GetPointData().SetScalars(dArray);
        axesGeometry.SetOrigin(0., 0., 0.);
        axesGeometry.SetScaleFactor(defaultScale);
        dFilter = new vtkTubeFilter();
        dFilter.SetInput(axesGeometry.GetOutput());
        dFilter.SetRadius(defaultRadius);
        dFilter.SetNumberOfSides(getResolution());
        vtkPolyDataMapper axesMapper = new vtkPolyDataMapper();
        axesMapper.SetInputConnection(dFilter.GetOutputPort());
        SetMapper(axesMapper);
    }

    public double getRadius() {
        return defaultRadius;
    }

    public void setRadius(double aRadius) {
        dFilter.SetRadius(aRadius);
        Modified();
    }
    public static int getResolution() {
        return Resolution;
    }

    public static void setResolution(int aResolution) {
        Resolution = aResolution;
    }

    public boolean isSymmetric() {
        return symmetric;
    }

    public void setSymmetric(boolean symmetric) {
        this.symmetric = symmetric;
        if (symmetric) 
            axesGeometry.SymmetricOn();
        else
            axesGeometry.SymmetricOff();
    }
    
    
}
