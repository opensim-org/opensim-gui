/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  Class StatesDocument provides a means of writing (serializing) and<br>
 * reading (deserializing) a complete time history of model states to and from<br>
 * a file. This capability is key when analyzing model behavior, visualizing<br>
 * simulation results, and conducting a variety of computationally demanding<br>
 * tasks (e.g., fitting a model to experimental data, solving optimal<br>
 * control problems, etc.).<br>
 * <br>
 * The states of an OpenSim::Model consist of all the independent variables that<br>
 * change (or can change) during a simulation. At each time step during a<br>
 * simulation, the underlying SimTK infrastructure captures the states in a<br>
 * SimTK::State object. A state variable falls into one of the following<br>
 * categories:<br>
 * <br>
 *         1) Continuous Variables (aka OpenSim::StateVariable%s)<br>
 *         2) Discrete Variables<br>
 *         3) Modeling Options<br>
 * <br>
 * Continuous Variables are governed by differential equations. They are<br>
 * numerically integrated during a simulation based on the values of their<br>
 * derivatives. Examples include joint coordinates, joint speeds, and muscle<br>
 * activations. In OpenSim, because Continuous Variables are the most commonly<br>
 * encountered kind of state, they are simply referred to as State Variables. All<br>
 * concrete instances of Continuous Variables in OpenSim are derived from the<br>
 * abstract class OpenSim::StateVariable.<br>
 * <br>
 * Discrete Variable are not governed by differential equations and so can change<br>
 * discontinuously during a simulation. Examples can include inputs to a<br>
 * simulation, like muscle excitations, coefficients of friction, and torque<br>
 * motor voltages. Examples can also include outputs to a simulation, like points<br>
 * of contact between colliding bodies and whether those bodies are experiencing<br>
 * static or kinetic frictional conditions. Such output discrete variables are<br>
 * updated at each time step during numerical integration. Unlike continuous<br>
 * states, however, they are updated based on closed-form algebraic expressions<br>
 * rather than based on their derivatives. In the underlying SimTK infrastructure,<br>
 * an output discrete variable is implemented as a specialized kind of<br>
 * discrete variable called an Auto-Update Discrete Variable.<br>
 * <br>
 * Modeling Options are flags, usually of type int, that are used to choose<br>
 * between viable ways to model a SimTK::System or whether or not to apply a<br>
 * constraint. Examples include a flag that specifies whether Euler angles or<br>
 * quaternions are used to represent rotation or a flag that specifies whether a<br>
 * particular joint coordinate is locked. When a Modeling Option is changed,<br>
 * low-level aspects of the System must be reconstituted or, in SimTK<br>
 * terminology, re-realized through SimTK::Stage::Model.<br>
 * <br>
 * Prior to the introduction of this class, only Continuous Variables (i.e.,<br>
 * OpenSim::StateVariable%s) were routinely and systematically serialized,<br>
 * most commonly via the OpenSim::Manager as an OpenSim::Storage file<br>
 * or via class OpenSim::StatesTrajectory as an OpenSim::TimeSeriesTable.<br>
 * Discrete Variables and Modeling Options, if serialized, had to be stored in<br>
 * separate files or handled as OpenSim::Property objects. In addition, prior to<br>
 * this class, all Discrete Variables in OpenSim were assumed to be type double,<br>
 * which is not a requirement of the underlying SimTK infrastructure.<br>
 * <br>
 * With the introduction of this class, all state variables {i.e., Continuous<br>
 * Variables (OpenSim::StateVariable%s), Discrete Variables, and Modeling Options}<br>
 * can be serialized in a single file, which by convention has the `.ostates`<br>
 * file name exention. In addition, a variety of types (e.g., bool, int, double,<br>
 * Vec3, Vec4, etc.) are supported for Discrete Variables. Continuous States are<br>
 * still assumed to be type double, and Modeling Options are still assumed to be<br>
 * type `int`. Note, however, that the `.ostates` file format has the<br>
 * flexibility to relax these assumptions and include other types if needed.<br>
 * <br>
 * Note: A point of clarification about Data Cache Variables...<br>
 * By definition, state variables are independent. That is, the value of one<br>
 * cannot be determined from the values of others. If a quantity of interest can<br>
 * be computed from values of state variables, particularly if that quantity is<br>
 * needed frequently, that quantity is often formalized as a Data Cache Variable.<br>
 * The value of a Data Cach Variable is computed at each time step of a simulation<br>
 * and stored in the SimTK::State. However, because a Data Cache Variable can<br>
 * always be computed from the Continuous Variables, Discrete Variables, and<br>
 * Modeling Options, they are not serialized.<br>
 * <br>
 *         SimTK::State Contents    | Serialized in `.ostates`?<br>
 *         ------------------------ | -----------------------<br>
 *         Continuous Variables     | yes<br>
 *         Discrete Variables       | yes<br>
 *         Modeling Options         | yes<br>
 *         Data Cache Variables     | no<br>
 * <br>
 * <br>
 * -----------------<br>
 * Design Notes<br>
 * -----------------<br>
 * <br>
 * ### Dependencies<br>
 * Most operations in class StatesDocument rely on underlying SimTK classes,<br>
 * most notably SimTK::String, SimTK::Array&lt;T&gt;, SimTK::State, and SimTK::Xml.<br>
 * <br>
 * StatesDocument has just one key OpenSim dependency: OpenSim::Model.<br>
 * OpenSim::Model brings with it all the methods it inherits from class<br>
 * OpenSim::Component, which are essential for getting and setting state<br>
 * information in OpenSim. StatesDocument does not know about classes like<br>
 * OpenSim::Storage, OpenSim::TimeSeriesTable, OpenSim::StatesTrajectory, or<br>
 * OpenSim::Manager.<br>
 * <br>
 * Exchanges of state information between class StatesDocument and the rest of<br>
 * OpenSim are accomplished via objects of type SimTK::Array_&lt;SimTK::State&gt;, or<br>
 * alternatively std::vector&lt;SimTK::State&gt;, which are informally referred to as<br>
 * state trajectories (see directly below).<br>
 * <br>
 * ### Trajectories<br>
 * In many methods of this class, as well as in related classes, you will<br>
 * encounter the term 'trajectory'. In these contexts, the term connotes a<br>
 * time-ordered sequence, or a time-history, of values.<br>
 * <br>
 * An array of knee angles (-10.0, -2.3, 4.5, 6.2, 7.1) would be termed a knee<br>
 * angle trajectory if those knee angles were recorded sequentially during a<br>
 * simulation. Similarly, an array of SimTK::State objects, if time ordered,<br>
 * would be called a states trajectory.<br>
 * <br>
 * Because of the flexibility and computational speed of the SimTK::Array_&lt;T&gt;<br>
 * container class, you will often see trajectories passed in argument lists as<br>
 * SimTK::Array_&lt;T&gt;%s. SimTK::Array_&lt;double&gt; might represent the trajectory of a<br>
 * knee angle. SimTK::Array_&lt;SimTK::Vec3&gt; might represent the trajectory of the<br>
 * center of pressure between a foot and the floor during a walking motion.<br>
 * SimTK::Array_&lt;SimTK::State&gt; is used to capture the full trajectory of states<br>
 * (continuous variables, discrete variables, and modeling options) recorded<br>
 * during a simulation.<br>
 * <br>
 * Note: SimTK::Array_&lt;SimTK::State&gt; is preferred over std::vector&lt;SimTK::State&gt;<br>
 * for reasons of performance, binary compatibility with Simbody libraries, and<br>
 * consistency with Simbody's underlying code base. For a variety of common<br>
 * operations, like indexing through an array, SimTK::Array_&lt;T&gt; is about<br>
 * twice as fast as std::vector_&lt;T&gt; on Windows systems. Such speed differences<br>
 * may not be as large on Mac or Ubuntu systems, but it is safe to assume that<br>
 * SimTK::Array_&lt;T&gt; will be just as fast or have a speed advantage.<br>
 * <br>
 * Th `StatesDocument` class relies heavily on a few trjectory-centric methods<br>
 * available in the OpenSim::Component class. A few examples follow.<br>
 * <br>
 * ```<br>
 *         template&lt;class T&gt;<br>
 *         Component::getDiscreteVariableTrajectory(<br>
 *                         const std::string&amp; path,<br>
 *                         const SimTK::Array_&lt;SimTK::State&gt;&amp; input,<br>
 *                         SimTK::Array_&lt;T&gt;&amp; output) const<br>
 * ```<br>
 * A call to the above method first finds a Discrete Variable in the component<br>
 * hierarchy based on the specifed path (`path`). Then, from the input states<br>
 * trajectory (`input`), the method extracts the values of the specified<br>
 * Discrete Variable and returns its trajectory as the output (`output`).<br>
 * Notice that the type of the Discrete Variable can be specified by the caller<br>
 * (i.e., T = int, double, Vec3, Vec4, etc.).<br>
 * <br>
 * ```<br>
 *         template&lt;class T&gt;<br>
 *         void setDiscreteVariableTrajectory(<br>
 *                         const std::string&amp; path,<br>
 *                         const SimTK::Array_&lt;T&gt;&amp; input,<br>
 *                         SimTK::Array_&lt;SimTK::State&gt;&amp; output) const<br>
 * ```<br>
 * On the other hand, based on the input trajectory of a specified Discrete<br>
 * Variable (`input`), a call to the above method sets the appropriate<br>
 * element in each of the SimTK::State objects held in the states trajectory<br>
 * (`output`). Notice again that the type T of the Discrete Variable can be<br>
 * specified by the caller.<br>
 * <br>
 * ### Complete and Constant XML Document upon Construction<br>
 * Upon construction, a StatesDocument instance always contains a complete<br>
 * internal XML document that represents a complete serialization of a specific<br>
 * model's state trajectory. Moreover, that internal XML document cannot be<br>
 * altered after construction!<br>
 * <br>
 * If a model is changed (e.g., a muscle or contact element is added) or<br>
 * a change has occurred in its state trajectory, the intended way to generate<br>
 * an XML document that reflects those changes is to construct a new<br>
 * StatesDocument instance. Constructing a new instance is the most reliable<br>
 * approach for ensuring an accurate serialization. This approach also greatly<br>
 * simplifies the implementation of the StatesDocument class, as methods for<br>
 * selectively editing aspects of the internal XML document are consequently<br>
 * unnecessary.<br>
 * <br>
 * ### Output Precision<br>
 * The precision with which numbers are serialized to a `.ostates` file can be<br>
 * specified at the time of construction. The `precision` parameter specifies<br>
 * the maximum number of significant digits used to represent numbers. If a<br>
 * number can be represented without data loss with fewer digits, fewer digits<br>
 * are used. In other words, trailing zeros are not written to file, thus<br>
 * reducing file size. For example, if `precision` = 5, the number<br>
 * 1.50000000000000000000 would be represented in a `.ostates` file<br>
 * as '1.5'; however, π would be represented as '3.1415'.<br>
 * <br>
 * By default, the `precision` parameter of a `StatesDocument` is set to the<br>
 * constant `SimTK::LosslessNumDigitsReal`, which results in lossless<br>
 * serialization. When `precision` = `SimTK::LosslessNumDigitsReal`, the<br>
 * `SimTK::State` can be serialized and deserialized repeatedly without loss<br>
 * of information. `SimTK::LosslessNumDigitsReal` is platform dependent but<br>
 * typically has a value of about `20`. In applications where exact values of the<br>
 * states are needed, lossless precision should be used. In applications where<br>
 * exact values of the states are not needed, a smaller number of digits can be<br>
 * used (e.g., `precsion = 6`) as a means of reducing the size of a `.ostates`<br>
 * file or simplifying some types of post analysis (e.g., plotting where the extra<br>
 * significant figures would go unnoticed).<br>
 * <br>
 * <br>
 * -------------------<br>
 * .ostate File Format<br>
 * -------------------<br>
 * XML is used as the organizing framework for `.ostates` files<br>
 * (see SimTK::Xml), allowing them to be viewed and edited with a text editor.<br>
 * Internet browsers can be also be used to view a `.ostate` file but may<br>
 * require a `.xml` file extension to be added to the file name for the<br>
 * XML format to be recognized.<br>
 * <br>
 * ### Sample `.ostates` File<br>
 * ```<br>
 * &lt;?xml version="1.0" encoding="UTF-8" ?&gt;<br>
 * &lt;!--OpenSim States Document (Version 40000)--&gt;<br>
 * &lt;ostates model="BouncingBlock" nTime="51" precision="3" date="Tue May 30 2023 03:42:40"&gt;<br>
 *   &lt;time&gt;(0,0.1, ...)&lt;time&gt;<br>
 *   &lt;continuous&gt;<br>
 *     &lt;variable path="/jointset/free/free_coord_0/value" type="double"&gt;(0,7.14, ...)&lt;variable&gt;<br>
 *     &lt;variable path="/jointset/free/free_coord_0/speed" type="double"&gt;(0,7.81, ...)&lt;variable&gt;<br>
 *     ...<br>
 *   &lt;continuous&gt;<br>
 *   &lt;discrete&gt;<br>
 *     &lt;variable path="/forceset/EC0/anchor" type="Vec3"&gt;(~[2.1,-1.1,0],~[1.82,-1.1,0], ...)&lt;variable&gt;<br>
 *     &lt;variable path="/forceset/EC0/mu_kinetic" type="double"&gt;(0.5,0.5, ...)&lt;variable&gt;<br>
 *     &lt;variable path="/forceset/EC0/mu_static" type="double"&gt;(0.7,0.7, ...)&lt;variable&gt;<br>
 *     &lt;variable path="/forceset/EC0/sliding" type="double"&gt;(1,1, ...)&lt;variable&gt;<br>
 *     ...<br>
 *   &lt;discrete&gt;<br>
 *   &lt;modeling&gt;<br>
 *     &lt;option path="/jointset/free/free_coord_0/is_clamped" type="int"&gt;(0,0, ...)&lt;option&gt;<br>
 *     &lt;option path="/jointset/free/free_coord_1/is_clamped" type="int"&gt;(0,0, ...)&lt;option&gt;<br>
 *     ...<br>
 *   &lt;modeling&gt;<br>
 * &lt;ostates&gt;<br>
 * ```<br>
 * <br>
 * ### Deserialization Requirements<br>
 * Successful deserialization of a .ostates file and full initialization of a<br>
 * states trajectory for an OpenSim::Model requires the following:<br>
 * <br>
 *     1) The name of the `OpenSim::Model` must match the value of the<br>
 *     `model` attribute of the top-level `ostates` element.<br>
 * <br>
 *     2) The number of continuous variables, discrete variables, and modeling<br>
 *     options in the .ostates file must match the corresponding numbers in the<br>
 *     OpenSim::Model.<br>
 * <br>
 *     3) The number of values recorded for each `variable` and each<br>
 *     `option` in the `.ostates` file must be equal to the value of the<br>
 *     `nTime` attribute of the top-level `ostates` element.<br>
 * <br>
 *     4) All `variable` and `option` paths must be found in the model<br>
 *     OpenSim::Component heirarchy.<br>
 * <br>
 *     5) The type must be supported. As of September 2024, the following types<br>
 *     are supported:<br>
 * <br>
 *             SimTK::State Category    | Supported Type(s)<br>
 *             ------------------------ | -----------------------<br>
 *             Continuous Variables     | double<br>
 *                                      |<br>
 *             Discrete Variables       | bool, int, float, double,<br>
 *                                      | Vec2, Vec3, Vec4, Vec5, Vec6<br>
 *                                      |<br>
 *             Modeling Options         | int<br>
 * <br>
 * <br>
 * --------------------------<br>
 * Using Class StatesDocument<br>
 * --------------------------<br>
 * Below are some code snippets that show how the StatesDocument class can be<br>
 * used. Example 1 shows how to obtain a states trajectory from a simulation and<br>
 * then serialize those states to file. Example 2 shows how to follow up and<br>
 * deserialize those same states and use them to accomplish a few basic things.<br>
 * <br>
 * ### Example 1: Serializing Simulated States<br>
 * ```<br>
 * ---------------<br>
 * Build the Model<br>
 * ---------------<br>
 * Building a model can be done in many ways. The most common approach is<br>
 * to construct a model from an OpenSim model file. Here, an empty model is<br>
 * constructed with place holders for components that are typically added.<br>
 *     OpenSim::Model model();<br>
 *     model.setGravity( Vec3(0.0,-9.8,0.0) );<br>
 *     model.setName("BouncingBlock");<br>
 * Add bodies...<br>
 * Add joints...<br>
 * Add actuators &amp; contact elements...<br>
 * <br>
 * -------------------------------<br>
 * Add a StatesTrajectory Reporter<br>
 * -------------------------------<br>
 * The reporter records the SimTK::State in an std::vector&lt;&gt; at a<br>
 * specified time interval.<br>
 *     OpenSim::StatesTrajectoryReporter* reporter =<br>
 *         new StatesTrajectoryReporter();<br>
 *     reporter-&gt;setName("states_reporter");<br>
 *     double interval = 0.01;<br>
 *     reporter-&gt;set_report_time_interval(interval);<br>
 *     model-&gt;addComponent(reporter);<br>
 * <br>
 * -----------------------------------------<br>
 * Build the System and Initialize the State<br>
 * -----------------------------------------<br>
 *     model.buildSystem();<br>
 *     SimTK::State&amp; state = model.initializeState();<br>
 * <br>
 * ---------<br>
 * Integrate<br>
 * ---------<br>
 *     Manager manager(*model);<br>
 *     manager.getIntegrator().setMaximumStepSize(0.01);<br>
 *     manager.setIntegratorAccuracy(1.0e-5);<br>
 *     double ti = 0.0;<br>
 *     double tf = 5.0;<br>
 *     state.setTime(ti);<br>
 *     manager.initialize(state);<br>
 *     state = manager.integrate(tf);<br>
 * <br>
 * -----------------------<br>
 * Create a StatesDocument<br>
 * -----------------------<br>
 * The reporter that was added to the system collects the states in an<br>
 * OpenSim::StatesTrajectory object. Underneath the covers, the states are<br>
 * accumulated in a private array of state objects (i.e., vector&lt;State&gt;).<br>
 * The StatesTrajectory class knows how to export a StatesDocument based<br>
 * on those states. This "export" functionality is what is used below.<br>
 *     const StatesTrajectory&amp; trajectory = reporter-&gt;getStates();<br>
 *     StatesDocument doc = trajectory.exportToStatesDocument(model);<br>
 * <br>
 * Alternatively, a read-only reference to the underlying state array<br>
 * can be obtained from the reporter and used to construct a<br>
 * StatesDocument directly:<br>
 *     const std::vector&lt;SimTK::State&gt;&amp; traj = reporter-&gt;getVectorOfStateObjects();<br>
 *     StatesDocument doc(model, traj);<br>
 * <br>
 * ----------------------------<br>
 * Serialize the States to File<br>
 * ----------------------------<br>
 * The file name (see below), can be any string supported by the file<br>
 * system. The recommended convention is for the file name to carry the<br>
 * suffix ".ostates". Below, the suffix ".ostates" is simply added to<br>
 * the name of the model, and the document is saved to the current working<br>
 * directory. The file name can also incorporate a valid system path (e.g.,<br>
 * "C:/Users/smith/Documents/Work/BouncingBlock.ostates").<br>
 *     SimTK::String statesFileName = model.getName() + ".ostates";<br>
 *     doc.serializeToFile(statesFileName);<br>
 * <br>
 * ----------------------<br>
 * Save the Model to File<br>
 * ----------------------<br>
 *     SimTK::String modelFileName = model.getName() + ".osim";<br>
 *     model-&gt;print(modelFileName);<br>
 * <br>
 * ```<br>
 * <br>
 * ### Example 2: Deserializing States<br>
 * ```<br>
 * -----------------------------<br>
 * Construct the Model from File<br>
 * -----------------------------<br>
 *     SimTK::String name = "BouncingBlock";<br>
 *     SimTK::String modelFileName = name + ".osim";<br>
 *     OpenSim::Model model(modelFileName);<br>
 *     model.buildSystem();<br>
 *     SimTK::State&amp; initState = model-&gt;initializeState();<br>
 * <br>
 * -----------------------------------------------<br>
 * Construct the StatesDocument Instance from File<br>
 * -----------------------------------------------<br>
 *     SimTK::String statesFileName = name + ".ostates";<br>
 *     StatesDocument doc(statesFileName);<br>
 * <br>
 * ----------------------<br>
 * Deserialize the States<br>
 * ----------------------<br>
 * Note that model and document must be entirely consistent with each<br>
 * other for the deserialization to be successful.<br>
 * See StatesDocument::deserialize() for details.<br>
 *     SimTK::Array_&lt;SimTK::State&gt; traj;  // Or, std::vector&lt;SimTK::State&gt; traj;<br>
 *     doc.deserialize(model, traj);<br>
 * <br>
 * Below are some things that can be done once a deserialized state<br>
 * trajectory has been obtained.<br>
 * <br>
 * ---------------------------------------------------<br>
 * Iterate through the State Trajectory Getting Values<br>
 * ---------------------------------------------------<br>
 *     std::string path;<br>
 *     const SimTK::State* iter;<br>
 *     for(iter = traj.cbegin(); iter!=traj.cend(); ++iter) {<br>
 * <br>
 * Get time<br>
 *         double t = iter-&gt;getTime();<br>
 * <br>
 * Get the value of a continuous state<br>
 *         path = "/jointset/free/free_coord_0/value";<br>
 *         double x = model.getStateVariableValue(*iter, path);<br>
 * <br>
 * Get the value of a discrete state of type double<br>
 *         path = "/forceset/EC0/sliding";<br>
 *         double sliding = model.getDiscreteVariableValue(*iter, path);<br>
 * <br>
 * Get the value of a discrete state of type Vec3<br>
 *         path = "/forceset/EC0/anchor"<br>
 *         const SimTK::AbstractValue&amp; valAbs =<br>
 *             model.getDiscreteVariableAbstractValue(*iter, path);<br>
 *         SimTK::Value&lt;Vec3&gt; valVec3 = SimTK::Value&lt;Vec3&gt;::downcast( valAbs );<br>
 *         Vec3 anchor = valVec3.get();<br>
 * <br>
 * Get the value of a modeling option<br>
 *         path = "/jointset/free/free_coord_0/is_clamped";<br>
 *         int clamped = model.getModelingOption(*iter, path);<br>
 * <br>
 * Access the value of a data cache variable. Note that this will<br>
 * require state realization at the appropriate stage.<br>
 *         system.realize(*iter, SimTK::Stage::Dynamics);<br>
 *         Vec3 force = forces.getContactElement(0)-&gt;getForce();<br>
 *     }<br>
 * <br>
 * ----------------------------------------------------<br>
 * Extract a Complete Trajectory for a Particular State<br>
 * ----------------------------------------------------<br>
 * Continuous (double)<br>
 *     path = "/jointset/free/free_coord_0/value";<br>
 *     SimTK::Array_&lt;double&gt; xTraj;<br>
 *     model.getStateVariableTrajectory&lt;double&gt;(path, traj, xTraj);<br>
 * <br>
 * Discrete (Vec3)<br>
 *     path = "/forceset/EC0/anchor";<br>
 *     SimTK::Array_&lt;Vec3&gt; anchorTraj;<br>
 *     model.getDiscreteVariableTrajectory&lt;Vec3&gt;(path, traj, anchorTraj);<br>
 * <br>
 * Modeling (int)<br>
 *     path = "/jointset/free/free_coord_0/is_clamped";<br>
 *     SimTK::Array_&lt;int&gt; clampedTraj;<br>
 *     model.getModelingOptionTrajectory&lt;int&gt;(path, traj, clampedTraj);<br>
 * <br>
 * ----------------------<br>
 * Form a TimeSeriesTable<br>
 * ----------------------<br>
 * Note that the table will only include the continuous states.<br>
 * This might be done for plotting, post analysis, etc.<br>
 *     StatesTrajectory trajectory(model, doc);<br>
 *     OpenSim::TimesSeriesTable table = traj.exportToTable(model);<br>
 * <br>
 * ```<br>
 * <br>
 * ### A Final Note<br>
 * Because Storage files (*.sto) and TimeSeriesTable files (*.tst) typically<br>
 * capture only the continuous states of a system, using these files as the basis<br>
 * for deserialization runs the risk of leaving discrete variables and modeling<br>
 * options in the SimTK::State uninitialized. In such an approach, additional<br>
 * steps may be needed to properly initialize all variables in the SimTK::State<br>
 * (e.g., by relying on OpenSim::Properties and/or on supplemental input files).<br>
 * <br>
 * In contrast, the StatesDocument class can be relied upon to yield a complete<br>
 * serialization and deserialization of the SimTK::State. If the StatesDocument<br>
 * class is used to serialize and then deserialize a state trajectory that was<br>
 * recorded during a simulation, all state variables in the State (continuous,<br>
 * discrete, and modeling) will be saved to a single file during serizaliztion<br>
 * and initialized upon deserialization of the document.<br>
 * <br>
 * @author F. C. Anderson *
 */
public class StatesDocument {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  public StatesDocument(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(StatesDocument obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(StatesDocument obj) {
    long ptr = 0;
    if (obj != null) {
      if (!obj.swigCMemOwn)
        throw new RuntimeException("Cannot release ownership as memory is not owned");
      ptr = obj.swigCPtr;
      obj.swigCMemOwn = false;
      obj.delete();
    }
    return ptr;
  }

  @SuppressWarnings("deprecation")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimSimulationJNI.delete_StatesDocument(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  /**
   *  The default constructor serves no purpose other than satisfying a<br>
   *     compiler demand. 
   */
  public StatesDocument() {
    this(opensimSimulationJNI.new_StatesDocument__SWIG_0(), true);
  }

  /**
   *  Construct a StatesDocument instance from an XML file in preparation<br>
   *     for deserialzing the states into a states trajectory. Once constructed,<br>
   *     the document is not designed to be modified; it is a fixed snapshot of the<br>
   *     states stored by the file at the time of construction. If the XML file<br>
   *     changes, the intended mechanism for obtaining a document that is<br>
   *     consistent with the modifed XML file is simply to construct a new document.<br>
   *     By convention (and not requirement), a StatesDocument filename has<br>
   *     ".ostates" as its suffix. To deserialize the states, call<br>
   *     StatesDocument::deserialize() on the constructed document. Note that the<br>
   *     validity of the XML file is not tested until StatesDocument::deserialize()<br>
   *     is called.<br>
   * <br>
   *     @param filename The name of the file, which may be prepended by the system<br>
   *     path at which the file resides (e.g., "C:/Documents/block.ostates"). 
   */
  public StatesDocument(SWIGTYPE_p_SimTK__String filename) {
    this(opensimSimulationJNI.new_StatesDocument__SWIG_1(SWIGTYPE_p_SimTK__String.getCPtr(filename)), true);
  }

  /**
   *  Construct a StatesDocument instance from a states trajectory in<br>
   *     preparation for serializing the trajectory to file. Once constructed, the<br>
   *     document is not designed to be modified; it is a fixed snapshot of the<br>
   *     states trajectory at the time of construction. The intended mechanism for<br>
   *     obtaining a document that is consistent with a modified or new states<br>
   *     trajectory is simply to construct a new document. To serialize the<br>
   *     constructed document to file, call StatesDocument::serialize().<br>
   * <br>
   *     @param model The OpenSim::Model to which the states belong.<br>
   *     @param trajectory An array containing the time-ordered sequence of<br>
   *     SimTK::State objects.<br>
   *     @param note Annotation note for this states document. By default, the note<br>
   *     is an empty string.<br>
   *     @param precision The number of significant figures with which numerical<br>
   *     values are converted to strings. The default value is<br>
   *     SimTK:LosslessNumDigitsReal (about 20), which allows for lossless<br>
   *     reproduction of state. 
   */
  public StatesDocument(Model model, SWIGTYPE_p_SimTK__Array_T_SimTK__State_unsigned_int_t trajectory, SWIGTYPE_p_SimTK__String note, int precision) {
    this(opensimSimulationJNI.new_StatesDocument__SWIG_2(Model.getCPtr(model), model, SWIGTYPE_p_SimTK__Array_T_SimTK__State_unsigned_int_t.getCPtr(trajectory), SWIGTYPE_p_SimTK__String.getCPtr(note), precision), true);
  }

  /**
   *  Construct a StatesDocument instance from a states trajectory in<br>
   *     preparation for serializing the trajectory to file. Once constructed, the<br>
   *     document is not designed to be modified; it is a fixed snapshot of the<br>
   *     states trajectory at the time of construction. The intended mechanism for<br>
   *     obtaining a document that is consistent with a modified or new states<br>
   *     trajectory is simply to construct a new document. To serialize the<br>
   *     constructed document to file, call StatesDocument::serialize().<br>
   * <br>
   *     @param model The OpenSim::Model to which the states belong.<br>
   *     @param trajectory An array containing the time-ordered sequence of<br>
   *     SimTK::State objects.<br>
   *     @param note Annotation note for this states document. By default, the note<br>
   *     is an empty string.<br>
   *     
   */
  public StatesDocument(Model model, SWIGTYPE_p_SimTK__Array_T_SimTK__State_unsigned_int_t trajectory, SWIGTYPE_p_SimTK__String note) {
    this(opensimSimulationJNI.new_StatesDocument__SWIG_3(Model.getCPtr(model), model, SWIGTYPE_p_SimTK__Array_T_SimTK__State_unsigned_int_t.getCPtr(trajectory), SWIGTYPE_p_SimTK__String.getCPtr(note)), true);
  }

  /**
   *  Construct a StatesDocument instance from a states trajectory in<br>
   *     preparation for serializing the trajectory to file. Once constructed, the<br>
   *     document is not designed to be modified; it is a fixed snapshot of the<br>
   *     states trajectory at the time of construction. The intended mechanism for<br>
   *     obtaining a document that is consistent with a modified or new states<br>
   *     trajectory is simply to construct a new document. To serialize the<br>
   *     constructed document to file, call StatesDocument::serialize().<br>
   * <br>
   *     @param model The OpenSim::Model to which the states belong.<br>
   *     @param trajectory An array containing the time-ordered sequence of<br>
   *     SimTK::State objects.<br>
   *     
   */
  public StatesDocument(Model model, SWIGTYPE_p_SimTK__Array_T_SimTK__State_unsigned_int_t trajectory) {
    this(opensimSimulationJNI.new_StatesDocument__SWIG_4(Model.getCPtr(model), model, SWIGTYPE_p_SimTK__Array_T_SimTK__State_unsigned_int_t.getCPtr(trajectory)), true);
  }

  /**
   *  Construct a StatesDocument instance from a states trajectory in<br>
   *     preparation for serializing the trajectory to file. Once constructed, the<br>
   *     document is not designed to be modified; it is a fixed snapshot of the<br>
   *     states trajectory at the time of construction. The intended mechanism for<br>
   *     obtaining a document that is consistent with a modified or new states<br>
   *     trajectory is simply to construct a new document. To serialize the<br>
   *     constructed document to file, call StatesDocument::serialize().<br>
   * <br>
   *     @param model The OpenSim::Model to which the states belong.<br>
   *     @param trajectory An array containing the time-ordered sequence of<br>
   *     SimTK::State objects.<br>
   *     @param note Annotation note for this states document. By default, the note<br>
   *     is an empty string.<br>
   *     @param precision The number of significant figures with which numerical<br>
   *     values are converted to strings. The default value is<br>
   *     SimTK:LosslessNumDigitsReal (about 20), which allows for lossless<br>
   *     reproduction of state. 
   */
  public StatesDocument(Model model, StdVectorState trajectory, SWIGTYPE_p_SimTK__String note, int precision) {
    this(opensimSimulationJNI.new_StatesDocument__SWIG_5(Model.getCPtr(model), model, StdVectorState.getCPtr(trajectory), trajectory, SWIGTYPE_p_SimTK__String.getCPtr(note), precision), true);
  }

  /**
   *  Construct a StatesDocument instance from a states trajectory in<br>
   *     preparation for serializing the trajectory to file. Once constructed, the<br>
   *     document is not designed to be modified; it is a fixed snapshot of the<br>
   *     states trajectory at the time of construction. The intended mechanism for<br>
   *     obtaining a document that is consistent with a modified or new states<br>
   *     trajectory is simply to construct a new document. To serialize the<br>
   *     constructed document to file, call StatesDocument::serialize().<br>
   * <br>
   *     @param model The OpenSim::Model to which the states belong.<br>
   *     @param trajectory An array containing the time-ordered sequence of<br>
   *     SimTK::State objects.<br>
   *     @param note Annotation note for this states document. By default, the note<br>
   *     is an empty string.<br>
   *     
   */
  public StatesDocument(Model model, StdVectorState trajectory, SWIGTYPE_p_SimTK__String note) {
    this(opensimSimulationJNI.new_StatesDocument__SWIG_6(Model.getCPtr(model), model, StdVectorState.getCPtr(trajectory), trajectory, SWIGTYPE_p_SimTK__String.getCPtr(note)), true);
  }

  /**
   *  Construct a StatesDocument instance from a states trajectory in<br>
   *     preparation for serializing the trajectory to file. Once constructed, the<br>
   *     document is not designed to be modified; it is a fixed snapshot of the<br>
   *     states trajectory at the time of construction. The intended mechanism for<br>
   *     obtaining a document that is consistent with a modified or new states<br>
   *     trajectory is simply to construct a new document. To serialize the<br>
   *     constructed document to file, call StatesDocument::serialize().<br>
   * <br>
   *     @param model The OpenSim::Model to which the states belong.<br>
   *     @param trajectory An array containing the time-ordered sequence of<br>
   *     SimTK::State objects.<br>
   *     
   */
  public StatesDocument(Model model, StdVectorState trajectory) {
    this(opensimSimulationJNI.new_StatesDocument__SWIG_7(Model.getCPtr(model), model, StdVectorState.getCPtr(trajectory), trajectory), true);
  }

  /**
   *  Get the annotation note for this states document. 
   */
  public SWIGTYPE_p_SimTK__String getNote() {
    return new SWIGTYPE_p_SimTK__String(opensimSimulationJNI.StatesDocument_getNote(swigCPtr, this), false);
  }

  /**
   *  Get the precision for this states document. 
   */
  public int getPrecision() {
    return opensimSimulationJNI.StatesDocument_getPrecision(swigCPtr, this);
  }

  /**
   *  Serialize the document to file. By convention (and not requirement),<br>
   *     a StatesDocument filename has ".ostates" as its suffix.<br>
   * <br>
   *     @param filename The name of the file, which may include the file system<br>
   *     path at which to write the file (e.g., "C:/Documents/block.ostates"). 
   */
  public void serialize(SWIGTYPE_p_SimTK__String filename) {
    opensimSimulationJNI.StatesDocument_serialize(swigCPtr, this, SWIGTYPE_p_SimTK__String.getCPtr(filename));
  }

  /**
   *  Deserialize the states held by this document into a states trajectory.<br>
   *     If deserialization fails, an exception describing the reason for the<br>
   *     failure is thrown. For details, see the section called "Deserialization<br>
   *     Requirements" in the introductory documentation for this class.<br>
   *     Note: This method is overloaded to allow users the flexibility to use<br>
   *     either `SimTK::Array_&lt;&gt;` or `std::vector` as the trajectory container.<br>
   * <br>
   *     @param model The OpenSim::Model with which the states are to be associated.<br>
   *     @param trajectory The array into which the time-ordered sequence of<br>
   *     SimTK::State objects will be deserialized.<br>
   *     @throws SimTK::Exception 
   */
  public void deserialize(Model model, SWIGTYPE_p_SimTK__Array_T_SimTK__State_unsigned_int_t trajectory) {
    opensimSimulationJNI.StatesDocument_deserialize__SWIG_0(swigCPtr, this, Model.getCPtr(model), model, SWIGTYPE_p_SimTK__Array_T_SimTK__State_unsigned_int_t.getCPtr(trajectory));
  }

  /**
   *  Deserialize the states held by this document into a states trajectory.<br>
   *     If deserialization fails, an exception describing the reason for the<br>
   *     failure is thrown. For details, see the section called "Deserialization<br>
   *     Requirements" in the introductory documentation for this class.<br>
   *     Note: This method is overloaded to allow users the flexibility to use<br>
   *     either `SimTK::Array_&lt;&gt;` or `std::vector` as the trajectory container.<br>
   * <br>
   *     @param model The OpenSim::Model with which the states are to be associated.<br>
   *     @param trajectory The array into which the time-ordered sequence of<br>
   *     SimTK::State objects will be deserialized.<br>
   *     @throws SimTK::Exception 
   */
  public void deserialize(Model model, StdVectorState trajectory) {
    opensimSimulationJNI.StatesDocument_deserialize__SWIG_1(swigCPtr, this, Model.getCPtr(model), model, StdVectorState.getCPtr(trajectory), trajectory);
  }

}
