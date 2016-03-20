/*
 * Copyright (c)  2005-2008, Stanford University
 * Use of the OpenSim software in source form is permitted provided that the following
 * conditions are met:
 * 	1. The software is used only for non-commercial research and education. It may not
 *     be used in relation to any commercial activity.
 * 	2. The software is not distributed or redistributed.  Software distribution is allowed 
 *     only through https://simtk.org/home/opensim.
 * 	3. Use of the OpenSim software or derivatives must be acknowledged in all publications,
 *      presentations, or documents describing work in which OpenSim or derivatives are used.
 * 	4. Credits to developers may not be removed from executables
 *     created from modifications of the source.
 * 	5. Modifications of source code must retain the above copyright notice, this list of
 *     conditions and the following disclaimer. 
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 *  SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 *  TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR BUSINESS INTERRUPTION) OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 *  WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/*
 * SingleModelVisuals.java
 *
 * Created on November 14, 2006, 5:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.opensim.view;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import org.opensim.modeling.*;
import org.opensim.threejs.DecorativeGeometryImplementationJS;
import org.opensim.view.pub.ModelVisualsVtk;
import org.opensim.view.pub.OpenSimDB;
import org.opensim.view.pub.ViewDB;
import vtk.vtkActor;
import vtk.vtkAssembly;
import vtk.vtkAssemblyNode;
import vtk.vtkAssemblyPath;
import vtk.vtkCylinderSource;
import vtk.vtkLinearTransform;
import vtk.vtkMatrix4x4;
import vtk.vtkPolyDataAlgorithm;
import vtk.vtkProp3D;
import vtk.vtkProp3DCollection;
import vtk.vtkSphereSource;
import vtk.vtkTransform;
import vtk.vtkTransformPolyDataFilter;
import vtk.vtkLineSource;
import vtk.vtkProp;

/**
 *
 * @author Ayman. A class representing the visuals of one model.
 * This class does not actually display the model. Instead it builds the
 * data structures (vtkAssembly) needed to display the model and also
 * maintains the maps for finding an object based on selection and vice versa.
 *
 * Sources of slow down:
 * 1. Too many assemblies (1 per muscle).
 * 2. Too many actors (markers, muscle points) these should replaced with Glyph3D or TensorGlyphs
 * 3. Selection is slow because hashing vtkProp3D apparently doesn't destribute objects evenly use 
 *       some form of Id instead (e.g. vtkId).
 */
public class SingleModelVisuals implements ModelVisualsVtk {
    
    protected vtkAssembly modelDisplayAssembly;   // assembly representing the model
    protected vtkLinearTransform modelDisplayTransform; // extra transform to shift, rotate model
    private double opacity;
    private double[] bounds = new double[]{-.1, .1, -.1, .1, -.1, .1};
    private boolean visible;

    private double[] inactiveMuscleColor = new double[]{0.0, 0.0, 1.0};
    //private double[] forceAlongPathColor = new double[]{0.0, 1.0, 0.0};
    private double[] defaultMuscleColor = new double[]{0.8, 0.1, 0.1};
    private double[] defaultMusclePointColor = new double[]{1.0, 0.0, 0.0};
    //private double[] defaultWrapObjectColor = new double[]{0.0, 1.0, 1.0};
    private boolean useCylinderMuscles=true;
    
    // Maps between objects and vtkProp3D for going from Actor to Object and vice versa
    // Objects are mapped to vtkProp3D in general, but some are known to be assemblies
    // e.g. Muscles, Models
    protected Hashtable<OpenSimObject, vtkProp3D> mapObject2VtkObjects = new Hashtable<OpenSimObject, vtkProp3D>();
    protected Hashtable<vtkProp3D, OpenSimObject> mapVtkObjects2Objects = new Hashtable<vtkProp3D, OpenSimObject>(50);
   
    //private Hashtable<OpenSimObject, LineSegmentMuscleDisplayer> mapActuator2Displayer = new Hashtable<OpenSimObject, LineSegmentMuscleDisplayer>();
    //private Hashtable<OpenSimObject, LineSegmentForceDisplayer> mapPathForces2Displayer = new Hashtable<OpenSimObject, LineSegmentForceDisplayer>();
    private Hashtable<Force, ObjectDisplayerInterface> mapNoPathForces2Displayer = new Hashtable<Force, ObjectDisplayerInterface>();

    protected Hashtable<OpenSimObject, vtkLineSource> mapMarkers2Lines = new Hashtable<OpenSimObject, vtkLineSource>(50);
     
    // Markers and muscle points are represented as Glyphs for performance
    //private MarkersDisplayer markersRep=new MarkersDisplayer();

    private vtkProp3DCollection  userObjects = new vtkProp3DCollection();
    private vtkProp3DCollection  bodiesCollection = new vtkProp3DCollection();
    private ModelComDisplayer comDisplayer;
    private boolean showCOM=false;
    private MuscleColoringFunction defaultColoringFunction;
    private MuscleColoringFunction noColoringFunction;
    private MuscleColoringFunction currentColoringFunction;
    
    // Following declarations support the new Vis
    ModelDisplayHints mdh = new ModelDisplayHints();
    private DecorativeGeometryImplementationGUI dgi = new DecorativeGeometryImplementationGUI();
    ArrayList<Component> modelComponents = new ArrayList<Component>();
    protected HashMap<Integer, BodyDisplayer> mapBodyIndicesToDisplayers = new HashMap<Integer, BodyDisplayer>();
    /**
     * Creates a new instance of SingleModelVisuals
     */
    public SingleModelVisuals(Model aModel) {
        //initDefaultShapesAndColors();
        buildComponentList(aModel);
        modelDisplayAssembly = createModelAssembly(aModel);
        OpenSimContext context=OpenSimDB.getInstance().getContext(aModel);
        defaultColoringFunction = new MuscleColorByActivationFunction(context);
        noColoringFunction = new MuscleNoColoringFunction(context);
        setVisible(true);
    }

    public void addGeometryForComponent(Component mc, Model model) {
        //System.out.println("Process object:"+mc.getConcreteClassName()+":"+mc.getName()+" "+mc.isObjectUpToDateWithProperties());
        ArrayDecorativeGeometry adg = new ArrayDecorativeGeometry();
        mc.generateDecorations(true, mdh, model.getWorkingState(), adg);
        dgi.setCurrentComponent(mc);
        if (adg.size()>0){  // Component has some geometry
            DecorativeGeometry dg;
            for (int idx=0; idx <adg.size(); idx++){
                dg = adg.getElt(idx);
                 dg.implementGeometry(dgi);
            }
        }
        ArrayDecorativeGeometry avdg = new ArrayDecorativeGeometry();
        mc.generateDecorations(false, mdh, model.getWorkingState(), avdg);
        if (avdg.size()>0){  // Component has some variable geometry
            dgi.startVariableGeometry();
            DecorativeGeometry dg;
            for (int idx=0; idx <avdg.size(); idx++){
                dg = avdg.getElt(idx);
                //System.out.println("Variable Visuals for "+mc.getName()+ "index:"+idx+"is");
                dg.implementGeometry(dgi);
            }
            dgi.finishVariableGeometry();
        }
        dgi.finishCurrentComponent(mc);
    }
 
    /**
     * Find the vtkProp3D for the passed in object
     */
    public vtkProp3D getVtkRepForObject(OpenSimObject obj)
    {
        return mapObject2VtkObjects.get(obj);
    }
    /**
     * find the Object for passed in vtkProp3D
     */
    public OpenSimObject getObjectForVtkRep(vtkProp3D prop)
    {
        return mapVtkObjects2Objects.get(prop);
    }

    /**************************************************************
     * First Creation of Model Visuals
     **************************************************************/
    /**
     * Create one vtkAssembly representing the model and return it.
     */
    private vtkAssembly createModelAssembly(Model model)
    {           
        //int bid0 = dg.getBodyId();
        vtkAssembly modelAssembly = new vtkAssembly();
        // Keep track of ground body to avoid recomputation
        BodyList bodies = model.getBodyList();
        BodyIterator body = bodies.begin();
        // Handle Ground separately since it's a PhysicalFrame not a Body
        BodyDisplayer groundRep = new BodyDisplayer(modelAssembly, model.getGround(),
                       mapObject2VtkObjects, mapVtkObjects2Objects);
        mapBodyIndicesToDisplayers.put(0, groundRep);
        while (!body.equals(bodies.end())) {
            PhysicalFrame physicalFrame = PhysicalFrame.safeDownCast(body.__deref__());
            if (physicalFrame!=null){
                int id = physicalFrame.getMobilizedBodyIndex();
               // Body actor
               BodyDisplayer bodyRep = new BodyDisplayer(modelAssembly, physicalFrame,
                       mapObject2VtkObjects, mapVtkObjects2Objects);
               mapBodyIndicesToDisplayers.put(id, bodyRep);
               }
            body.next();
        }
        dgi.setModelAssembly(modelAssembly, mapBodyIndicesToDisplayers, model, mdh);
        ComponentsList mcList = model.getComponentsList();
        ComponentIterator mcIter = mcList.begin();
        while (!mcIter.equals(mcList.end())){
            //System.out.println("In createModelAssembly Type, name:"+mcIter.__deref__().getConcreteClassName()+mcIter.__deref__().getName());
            addGeometryForComponent(mcIter.__deref__(), model);
            mcIter.next();
        }

        //comDisplayer = new ModelComDisplayer(model);
        if (isShowCOM())
            modelAssembly.AddPart(comDisplayer.getVtkActor());

        // Now the muscles and other actuators
        //addGeometryForForces(model, modelAssembly);

        // Add whole model assembly to object map
        mapObject2VtkObjects.put(model, modelAssembly);
        mapVtkObjects2Objects.put(modelAssembly, model);

        // Now call update to actually set all the positions/transforms of the actors and glyphs
        updateModelDisplay(model);

        return modelAssembly;
    }


    /**
     * updateModelDisplay with new transforms cached in animationCallback.
     * This method must be optimized since it's invoked during live animation
     * of simulations and/or analyses (ala IK).
     */
   public void updateModelDisplay(Model model) {
       
      // Cycle thru bodies and update their transforms from the kinematics engine
       OpenSimContext context=OpenSimDB.getInstance().getContext(model);
        context.realizePosition();
        BodyList bodies = model.getBodyList();
        BodyIterator bodyIter = bodies.begin();
        while (!bodyIter.equals(bodies.end())) {
            Body body = bodyIter.__deref__();
            // Fill the maps between objects and display to support picking, highlighting, etc..
            // The reverse map takes an actor to an Object and is filled as actors are created.
            BodyDisplayer bodyRep= (BodyDisplayer) mapObject2VtkObjects.get(body);
            vtkMatrix4x4 bodyVtkTransform= getBodyTransform(model, body);
            if (bodyRep!=null)
               bodyRep.SetUserMatrix(bodyVtkTransform);

            //bodyRep.applyRepresentations();
            bodyIter.next();
        }
        ComponentsList mcList = model.getComponentsList();
        ComponentIterator mcIter = mcList.begin();
        while (!mcIter.equals(mcList.end())){
            //System.out.println("In updateModelDisplay Type, name:"+mcIter.__deref__().getConcreteClassName()+" "+mcIter.__deref__().getName());
            if (dgi.isNewComponent(mcIter.__deref__())){
                System.out.println("New Component "+mcIter.__deref__().getName());
                addGeometryForComponent(mcIter.__deref__(), model);
            }
            else
                dgi.updateDecorations(mcIter.__deref__());
            mcIter.next();
        }
       
        updateVariableGeometry(model);
        //updateMarkersGeometry(model.getMarkerSet());
        //updateForceGeometry(model);
        //comDisplayer.updateCOMLocation();
        //updateUserObjects();
   }

   private void updateMarkersGeometry(MarkerSet markers) {
      for (int i=0; i<markers.getSize(); i++)
         getMarkersRep().updateMarkerGeometry(markers.get(i));
   }

   /**
    * Functions for dealing with actuator geometry
    */

   public void updateActuatorGeometry(Actuator act, boolean callUpdateDisplayer) {
      /*
       LineSegmentMuscleDisplayer disp = null;//mapActuator2Displayer.get(act);
      if(disp != null) {
         disp.updateGeometry(callUpdateDisplayer);

      }
       */
   }

   public void setApplyMuscleColors(boolean enabled) {
      /*
      Iterator<LineSegmentMuscleDisplayer> dispIter = mapActuator2Displayer.values().iterator();
      while(dispIter.hasNext()) dispIter.next().setApplyColoringFunction(currentColoringFunction);
       */
   }

     /**
      * Get the vtkTransform matrix between ground and a body body,
      */
     vtkMatrix4x4 getBodyTransform(Model model, Body body)
     {
            double[] flattenedXform = new double[16];
            OpenSimContext dContext = OpenSimDB.getInstance().getContext(model);
            dContext.getTransformAsDouble16(body.getGroundTransform(dContext.getCurrentStateRef()), flattenedXform);
            return convertTransformToVtkMatrix4x4(flattenedXform);
     }
    /**
     * return a reference to the vtkAssembly representing the model
     */
    public vtkAssembly getModelDisplayAssembly() {
        return modelDisplayAssembly;
    }
    
    public OpenSimObject pickObject(vtkAssemblyPath asmPath) {
        if (asmPath != null) {
         vtkAssemblyNode pickedAsm = asmPath.GetLastNode();
         vtkProp p = pickedAsm.GetViewProp();
         return dgi.pickObject((vtkActor)p);
        }  
        return null;    // No selection
    }

    /* Make the entire model pickable or not pickable. modelDisplayAssembly.setPickable()
     * does not seem to do anything, so the pickable flag for each actor in the model
     * needs to be set.
     */
    public void setPickable(boolean pickable) {
       if (pickable) {
          modelDisplayAssembly.SetPickable(1);
          Enumeration<vtkProp3D> props = mapObject2VtkObjects.elements();
          while (props.hasMoreElements()) {
             props.nextElement().SetPickable(1);
          }
       } else {
          modelDisplayAssembly.SetPickable(0);
          Enumeration<vtkProp3D> props = mapObject2VtkObjects.elements();
          while (props.hasMoreElements()) {
             props.nextElement().SetPickable(0);
          }
       }
       //getMarkersRep().setPickable(pickable);
    }
 
    public vtkLinearTransform getModelDisplayTransform() {
        return modelDisplayTransform;
    }

    /**
     * Since there's no global model opacity, we'll take the opacity of the first object
     * we find and use it as value for the Opacity of he model. This has the advantage that
     * if Opacities are the same for all objects then the behavior is as expected.
     */
    public double getOpacity() {
        vtkProp3D prop = modelDisplayAssembly.GetParts().GetLastProp3D();  
        
        if (prop instanceof vtkAssembly){   //recur
            opacity = getAssemblyOpacity((vtkAssembly)prop);            
        }
        else if (prop instanceof vtkActor){ // Could be Actor or whatelse?
            opacity = ((vtkActor)prop).GetProperty().GetOpacity();
        }

        return opacity;
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    /**
     * Compute bounding box for model as this can be useful for initial placement
     *  This is supposed to be a ballpark rather than an accurate estimate so that minor changes to
     * model do not cause overlap, but the bboox is not intended to be kept up-to-date
     * unused and turned out to be very slow for some reason
    private void computeModelBoundingbox() {
        modelDisplayAssembly.GetBounds(bounds);
    }*/

    // NOTE: these functions are necessary in order to deal with the model offsets...
    // Not the most general solution (e.g. if the hierarchy changed and there was more than one user matrix
    // modifying the model then this would not work).  But should work for now.
    public void transformModelToWorldPoint(double[] point) {
      vtkMatrix4x4 body2world = modelDisplayAssembly.GetUserMatrix();
      if(body2world != null) {
         double[] point4 = body2world.MultiplyPoint(new double[]{point[0],point[1],point[2],1.0});
         for(int i=0; i<3; i++) point[i]=point4[i];
      }
    }

    public void transformModelToWorldBounds(double[] bounds) {
      vtkMatrix4x4 body2world = modelDisplayAssembly.GetUserMatrix();
      if(body2world != null) {
         double[] minCorner = body2world.MultiplyPoint(new double[]{bounds[0],bounds[2],bounds[4],1.0});
         double[] width = new double[]{bounds[1]-bounds[0], bounds[3]-bounds[2], bounds[5]-bounds[4]};
         for(int i=0; i<3; i++) { bounds[2*i]=bounds[2*i+1]=minCorner[i]; } // initialize as min corner
         for(int i=0; i<3; i++) for(int j=0; j<3; j++) {
            if(body2world.GetElement(i,j)<0) bounds[2*i]+=width[j]*body2world.GetElement(i,j);
            else bounds[2*i+1]+=width[j]*body2world.GetElement(i,j);
         }
      }
    }

    @Override
   public double[] getBoundsBodiesOnly() {
      double[] bounds = getBounds();
      /*bodiesCollection.InitTraversal();
      for(;;) {
         vtkProp3D prop = bodiesCollection.GetNextProp3D();
         if(prop==null) break;
         if(prop.GetVisibility()!=0) bounds = ViewDB.boundsUnion(bounds, prop.GetBounds());
      }*/
      if (bounds!=null)
         transformModelToWorldBounds(bounds);
      return bounds;
   }

    /**
     * A flag indicating if the model assembly is shown or not for global visibility control
     */
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean onOff) {
        this.visible = onOff;
    }

    double[] getOffset() {
        return modelDisplayAssembly.GetPosition();
    }

    private double getAssemblyOpacity(vtkAssembly anAssembly) {
        vtkProp3DCollection parts = anAssembly.GetParts();
        parts.InitTraversal();
        int n =parts.GetNumberOfItems();
        for(int i=0; i<n; i++){
            vtkProp3D prop = parts.GetNextProp3D();
            if (prop instanceof vtkAssembly){   
                return getAssemblyOpacity((vtkAssembly)prop);
                // Should continue traversal here
            }
            else if (prop instanceof vtkActor){ 
                return ((vtkActor)prop).GetProperty().GetOpacity();
            }
        }
        return 0;
    }
   /**
    * Utility to convert Transform as defined by OpenSim to the form used by vtk
    */
   static vtkMatrix4x4 convertTransformToVtkMatrix4x4(double[] xformAsVector) {
      vtkMatrix4x4 m = new vtkMatrix4x4();    // This should be moved out for performance
      // Transpose the rotation part of the matrix per Pete!!
      for (int row = 0; row < 3; row++){
          for (int col = row; col < 4; col++){
             double swap = xformAsVector[row*4+col];
             xformAsVector[row*4+col]=xformAsVector[col*4+row];
             xformAsVector[col*4+row]=swap;
          }
      }
      m.DeepCopy(xformAsVector);
      return m;
   }

   /**
    * Method used to update display of motion objects during animation 
    */
   private void updateUserObjects() {
      //System.out.println("updateUserObjects");
      ViewDB.getInstance().updateAnnotationAnchors();
   }
   
   public void addUserObject(vtkActor userObj){
      if (getUserObjects().IsItemPresent(userObj)==0){
        getUserObjects().AddItem(userObj);
        modelDisplayAssembly.AddPart(userObj);
      }
   }
   
   public void removeUserObject(vtkActor userObj){
      getUserObjects().RemoveItem(userObj);
      modelDisplayAssembly.RemovePart(userObj);
   }

   /**
    * Functions to deal with glyphs for markers and muscle points/segments
    */

    private void createMuscleSegmentRep(OpenSimvtkOrientedGlyphCloud aMuscleSegmentsRep) {
      
        // Muscle segments 
        vtkPolyDataAlgorithm muscleSgt;  //Either cylinder or ellipsoid for now
        if (useCylinderMuscles) {
            vtkCylinderSource muscleSegment=new vtkCylinderSource();
            muscleSegment.SetRadius(ViewDB.getInstance().getMuscleDisplayRadius());
            muscleSegment.SetHeight(1.0);
            muscleSegment.SetCenter(0.0, 0.0, 0.0);
            muscleSegment.CappingOff();
            aMuscleSegmentsRep.setShape(muscleSegment.GetOutput());
        }
        else {
             vtkSphereSource muscleSegment=new vtkSphereSource();
             muscleSegment.SetRadius(ViewDB.getInstance().getMuscleDisplayRadius());
             vtkTransformPolyDataFilter stretcher = new vtkTransformPolyDataFilter();
             vtkTransform stretcherTransform = new vtkTransform();
             stretcherTransform.Scale(20.0, 0.5/ViewDB.getInstance().getMuscleDisplayRadius(), 20.0);
             muscleSegment.SetCenter(0.0, 0.0, 0.0);
             stretcher.SetInput(muscleSegment.GetOutput());
             stretcher.SetTransform(stretcherTransform);
             aMuscleSegmentsRep.setShape(stretcher.GetOutput());
        }
        aMuscleSegmentsRep.setColorRange(inactiveMuscleColor, defaultMuscleColor);
    }

    private void createMusclePointRep(OpenSimvtkGlyphCloud aMusclePointsRep) {

        // Muscle points
        //vtkSphereSource viaPoint=new vtkSphereSource();
        //viaPoint.SetRadius(ViewDB.getInstance().getMuscleDisplayRadius());
        //viaPoint.SetCenter(0., 0., 0.);
        //getMusclePointsRep().setColors(defaultMusclePointColor, SelectedObject.defaultSelectedColor);
        aMusclePointsRep.setColorRange(inactiveMuscleColor, defaultMusclePointColor);
        aMusclePointsRep.setSelectedColor(SelectedObject.defaultSelectedColor);
        //vtkStripper strip2 = new vtkStripper();
        //strip2.SetInput(viaPoint.GetOutput());
        aMusclePointsRep.setShapeName("musclepoint");
        aMusclePointsRep.setScaleFactor(ViewDB.getInstance().getMuscleDisplayRadius());
        aMusclePointsRep.scaleByVectorComponents();
    }

    public OpenSimvtkGlyphCloud getGlyphObjectForActor(vtkActor act) {
        if (getMarkersRep().getVtkActor()==act)
            return getMarkersRep();
        else 
           return null;
    }

    public MarkersDisplayer getMarkersRep() {
        return null;
    }
    // Remmove dead references to help garbage collector.
    public void cleanup() {
        int rc = modelDisplayAssembly.GetReferenceCount();
        vtkProp3DCollection parts = modelDisplayAssembly.GetParts();
        for(int i=parts.GetNumberOfItems()-1; i>=0; i--){
            modelDisplayAssembly.RemovePart(parts.GetLastProp3D());
        }
        getUserObjects().RemoveAllItems();
        userObjects=null;
        bodiesCollection.RemoveAllItems();
        bodiesCollection=null;
                
        modelDisplayAssembly=null;
        mapObject2VtkObjects=null;
        mapVtkObjects2Objects=null;

        
    }
    public vtkProp3DCollection getUserObjects() {
        return userObjects;
    }
    // Springs and other joints
    private void addNonPathForceGeometry(vtkAssembly modelAssembly, OpenSimObject fObject) {
        Force f = Force.safeDownCast(fObject);
        OpenSimContext context=OpenSimDB.getInstance().getContext(f.getModel());
        if (context.isDisabled(f)) return;

    }

    public boolean isShowCOM() {
        return showCOM;
    }

    public void setShowCOM(boolean showCOM) {
        if (showCOM) modelDisplayAssembly.AddPart(comDisplayer.getVtkActor());
        else modelDisplayAssembly.RemovePart(comDisplayer.getVtkActor());
        this.showCOM = showCOM;
    }

    public void removeGeometry(OpenSimObject object) {
            dgi.removeGeometry((Actuator) object);
            
    }

    private void removePathForceGeometry(Force f) {
        /*
        if (f.getDisplayer()!=null){
            f.getDisplayer().setDisplayPreference(DisplayPreference.None);
            mapPathForces2Displayer.get(f).updateGeometry(false);
        }*/
    }

    private void removeNonPathForceGeometry(Force f) {
        /*
        if (f.getDisplayer()!=null){
            f.getDisplayer().setDisplayPreference(DisplayPreference.None);
            mapNoPathForces2Displayer.get(f).updateGeometry();
        }*/
    }

    public void updateForceGeometry(Force f, boolean visible) {
        if (!visible){  // turning off
            removeGeometry(f);
        }
        else{/*
            if (mapPathForces2Displayer.get(f)!=null){
                mapPathForces2Displayer.get(f).updateGeometry(true);
                LineSegmentForceDisplayer disp = mapPathForces2Displayer.get(f);

            }
            else if (mapNoPathForces2Displayer.get(f)!=null){
                mapNoPathForces2Displayer.get(f).updateGeometry();
            }*/
        }
    }

    /**
     * @return the bounds
     */
    public double[] getBounds() {
        return bounds;
    }


    public void updateObjectDisplay(OpenSimObject specificObject) {
        vtkProp3D prop3D = mapObject2VtkObjects.get(specificObject);
        if (prop3D!= null){

          if (prop3D instanceof DecorativeGeometryDisplayer ){
                DecorativeGeometryDisplayer dgd = (DecorativeGeometryDisplayer)prop3D;
                dgd.updateDecorativeGeometryFromObject();
                ((DecorativeGeometryDisplayer)prop3D).updateDisplayFromDecorativeGeometry();
          }
        }
    }

    public void setMuscleColoringFunction(MuscleColoringFunction mcf) {
        if (mcf == null)
            currentColoringFunction = defaultColoringFunction;
        else
            currentColoringFunction = mcf;
        //pushColoringFunctionToMuscles();
    }

    private void buildComponentList(Model model) {
        ComponentsList mcList = model.getComponentsList();
        ComponentIterator mcIter = mcList.begin();
        while (!mcIter.equals(mcList.end())){
            //System.out.println("Object:Type,Name:"+ mcIter.getConcreteClassName()+","+mcIter.getName());
            modelComponents.add(mcIter.__deref__());
            mcIter.next();
        }
   }

    public void selectObject(OpenSimObject openSimObject) {
        dgi.selectObject(openSimObject); // Delegate call to DecorativeGeometryImplmentation for now
    }

    void highLightObject(OpenSimObject object, boolean highlight) {
        if (highlight)
            dgi.selectObject(object);
        else {
            // Call method on owner ModelCompoent
            System.out.println("Trying to unhighlight "+object.getName());
            // Assume Component
            Component mc = Component.safeDownCast(object);
            if (mc != null){
                dgi.updateDecorations(mc);
            }
        }
    }

    public void setObjectColor(OpenSimObject object, double[] color) {
        Component mc = Component.safeDownCast(object);
        if (mc != null){
            dgi.setObjectColor(mc, color);
        }
    }
    
    public void upateDisplay(Component mc) {
        dgi.updateDecorations(mc);
    }
    /**
     * Cycle thru components and if they have VariableGeometry update it.
     * @param model 
     */
    private void updateVariableGeometry(Model model) {
        dgi.startVariableGeometry();
        for (int mcIndex = 0; mcIndex < modelComponents.size(); mcIndex++){
            Component mc = modelComponents.get(mcIndex);
            //System.out.print("Processing model component "+mc.getConcreteClassName()+":"+mc.getName());
            ArrayDecorativeGeometry avdg = new ArrayDecorativeGeometry();
            mc.generateDecorations(false, mdh, model.getWorkingState(), avdg);
            //System.out.println("Size var ="+avdg.size());
            dgi.setCurrentComponent(mc);
            if (avdg.size()>0){  // Component has some variable geometry
                dgi.updateDecorations(mc, true);
             }
            //System.out.println("...Finished");
        };
        dgi.finishVariableGeometry();
    }

    public void addGeometry(Marker marker) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * @return the dgi
     */
    public DecorativeGeometryImplementationGUI getDecorativeGeometryImplmentation() {
        return dgi;
    }
}

