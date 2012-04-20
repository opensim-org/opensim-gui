import javax.swing as swing
import org.opensim.modeling as modeling
from org.opensim.console.OpenSimPlotter import *;
import org.opensim.utils as utils;

plotterPanel = createPlottterPanel("Created by Script")
src = addDataSource(plotterPanel, "Scripts/testData/SinglePin_states_degrees.mot")
crv = addCurve(plotterPanel, src, "time", "r1_z")
crv2 = addCurve(plotterPanel, src, "time", "r1_z_u")
crv.setLegend("my r1_z")
crv2.setLegend("Speed")
setCurveColor(plotterPanel, 0, 0.0, 1.0, 0.0)
