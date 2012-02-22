import javax.swing as swing
import org.opensim.modeling as modeling
import org.opensim.view.pub as view;
import org.opensim.plotter as plotter;
import org.opensim.utils as utils;

plotterPanel = plotter.OpenSimPlotter.createPlottterPanel("Created by Script")
src = plotter.OpenSimPlotter.addDataSource(plotterPanel, "Scripts/testData/SinglePin_states_degrees.mot")
crv = plotter.OpenSimPlotter.addCurve(plotterPanel, src, "time", "r1_z")
crv2 = plotter.OpenSimPlotter.addCurve(plotterPanel, src, "time", "r1_z_u")
crv.setLegend("my r1_z")
crv2.setLegend("Speed")
plotter.OpenSimPlotter.setCurveColor(plotterPanel, 0, 0.0, 1.0, 0.0)
