import javax.swing as swing
import org.opensim.modeling as modeling
import org.opensim.view.pub as view;
win = swing.JFrame("Welcome")
model = view.OpenSimDB.getInstance().getCurrentModel()
dLabel = swing.JLabel(model.getName())
win.getContentPane().add(dLabel)
win.setTitle("Example Window")
win.size = (100, 100)
win.show()