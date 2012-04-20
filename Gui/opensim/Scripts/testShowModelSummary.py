import javax.swing as swing

win = swing.JFrame("Welcome")
model = getCurrentModel()

if not model:
    win.getContentPane().add(swing.JLabel("No models"))
    win.setTitle("Example Window")
else:
    win.getContentPane().setLayout(swing.BoxLayout(win.getContentPane(), 1))
    dLabel = swing.JLabel("Model name: "+model.getName())
    win.add(dLabel)
    numBodiesString = "Number of bodies ="+lang.String.valueOf(model.getNumBodies())
    bLabel = swing.JLabel(numBodiesString)
    win.add(bLabel)
    numJointsString = "Number of Joints ="+lang.String.valueOf(model.getNumJoints())
    njLabel = swing.JLabel(numJointsString)
    win.add(njLabel)


win.setTitle("Example Window")
win.size = (100, 100)
win.show()