import javax.swing as swing
import java.lang as lang
import org.opensim.modeling as modeling
import org.opensim.view.pub as view;

win = swing.JFrame("Welcome")
model = view.OpenSimDB.getInstance().getCurrentModel()

dLabel = swing.JLabel(model.getName())
win.getContentPane().add(dLabel)

numBodiesString = "Number of bodies ="+lang.String.valueOf(model.getNumBodies())
bLabel = swing.JLabel(numBodiesString)
win.getContentPane().add(bLabel)

numJointsString = "Number of Joints ="+lang.String.valueOf(model.getNumJoints())
njLabel = swing.JLabel(numJointsString)
win.getContentPane().add(njLabel)

win.setTitle("Example Window")
win.size = (100, 100)
win.show()