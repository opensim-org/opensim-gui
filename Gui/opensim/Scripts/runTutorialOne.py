import javax.swing as swing
import org.opensim.modeling as modeling
import org.opensim.view.pub as view;
import org.opensim.plotter as plotter;

modeling.OpenSimObject.setDebugLevel(3)
view.gui.addModel("../../../Models/Tutorials/BothLegs/BothLegs.osim")

view.gui.loadMotion("../../../Models/Tutorials/BothLegs/BothLegsWalk.mot")

#default pose
myModel = view.gui.getCurrentModel()
for i in range(myModel.getCoordinateSet().getSize()):
	coord = myModel.getCoordinateSet().get(i)
	view.gui.setCoordinate(coord, coord.getDefaultValue())
	
plotterPanel = plotter.OpenSimPlotter.createPlottterPanel("Plot ")
crv1 = plotter.OpenSimPlotter.addAnalysisCurve(plotterPanel, "fiber-length", "RF", "r_knee_angle")
plotter.OpenSimPlotter.setCurveLegend(crv1, "RF")
crv2 = plotter.OpenSimPlotter.addAnalysisCurve(plotterPanel, "fiber-length", "VASINT", "r_knee_angle")
plotter.OpenSimPlotter.setCurveLegend(crv2, "VASINT")
plotter.OpenSimPlotter.exportData(plotterPanel, "../../../Models/Tutorials/BothLegs/cvs1.sto")
