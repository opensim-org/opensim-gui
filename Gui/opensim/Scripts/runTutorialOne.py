import javax.swing as swing
import org.opensim.modeling as modeling


modeling.OpenSimObject.setDebugLevel(3)
addModel(getScriptsDir()+"/testData/BothLegs.osim")

loadMotion(getScriptsDir()+"/testData/BothLegsWalk.mot")

#default pose
myModel = getCurrentModel()
for i in range(myModel.getCoordinateSet().getSize()):
	coord = myModel.getCoordinateSet().get(i)
	setCoordinateValue(coord, coord.getDefaultValue())
	
plotterPanel = createPlottterPanel("Plot ")
crv1 = addAnalysisCurve(plotterPanel, "fiber-length", "RF", "r_knee_angle")
setCurveLegend(crv1, "RF")
crv2 = addAnalysisCurve(plotterPanel, "fiber-length", "VASINT", "r_knee_angle")
setCurveLegend(crv2, "VASINT")
exportData(plotterPanel, getScriptsDir()+"/testData/cvs1.sto")
