import javax.swing as swing
import org.opensim.modeling as modeling
import org.opensim.view.pub as view;
import org.opensim.plotter as plotter;

modeling.OpenSimObject.setDebugLevel(3)
	
plotterPanel = plotter.OpenSimPlotter.createPlottterPanel("Plot Example")
crv1 = plotter.OpenSimPlotter.addAnalysisCurve(plotterPanel, "fiber-length", "RF", "r_knee_angle")
crv1.setLegend("RF_Current")
crv2 = plotter.OpenSimPlotter.addAnalysisCurve(plotterPanel, "fiber-length", "VASINT", "r_knee_angle")
crv2.setLegend("VASINT_Current")
src = plotter.OpenSimPlotter.addDataSource(plotterPanel, "Scripts/testData/FiberLengthStd.sto")
crv3 = plotter.OpenSimPlotter.addCurve(plotterPanel, src, "time", "RF")
crv4 = plotter.OpenSimPlotter.addCurve(plotterPanel, src, "time", "VASINT")
crv3.setLegend("RF_std")
crv4.setLegend("VASINT_std")
plotter.OpenSimPlotter.setCurveColor(plotterPanel, 0, 0.0, 1.0, 0.0)

# The following line exports the data in the plotter window to a file
#plotter.OpenSimPlotter.exportData(plotterPanel, "cvs1.sto")
