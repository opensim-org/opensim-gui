import javax.swing as swing
import org.opensim.modeling as modeling
import org.opensim.view.pub as view;
import org.opensim.plotter as plotter;
import org.opensim.utils as utils;

plotterPanel = plotter.JPlotterPanel()
f= utils.DialogUtils.createFrameForPanel(plotterPanel, "Plotter")
plotterPanel.setFrame(f)
f.setVisible(True)