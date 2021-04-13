/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  This is the handle class for the hidden State implementation.<br>
 * <br>
 * This object is intended to contain all state information for a SimTK::System, <br>
 * except topological information which is stored in the system itself. A system <br>
 * is "const" after its topology has been constructed and realized.<br>
 * <br>
 * Systems contain a set of Subsystem objects, and %State supports that concept by <br>
 * allowing per-subsystem partitioning of the total system state. This allows <br>
 * subsystems to have their own private state variables, while permitting the <br>
 * system to allow shared access to state among the subsystems when necessary.<br>
 * <br>
 * The %State provides services reflecting the structure of the equations it <br>
 * expects to find in the System. Three different views of the same state <br>
 * information are supported to accommodate three different users:<br>
 *    - the system as a whole<br>
 *    - Subsystems contained in the system<br>
 *    - numerical methods operating on the state<br>
 * <br>
 * Typically numerical methods have a much less nuanced view of the state<br>
 * than do the system or subsystems.<br>
 * <br>
 * The system is expected to be a "hybrid DAE", that is, a mixture of continuous <br>
 * and discrete dynamic equations, and algebraic constraints. There is an <br>
 * independent variable t, continuous state variables y, and discrete state <br>
 * variables d.<br>
 * <br>
 * The continuous part is an ODE-on-a-manifold system suitable for solution via <br>
 * coordinate projection, structured like this for the view taken by numerical <br>
 * methods:<br>
 * <pre><br>
 *      (1)  y' = f(d;t,y)         differential equations<br>
 *      (2)  c  = c(d;t,y)         algebraic equations (manifold is c=0)<br>
 *      (3)  e  = e(d;t,y)         event triggers (watch for zero crossings)<br>
 * </pre><br>
 * with initial conditions t0,y0,d0 such that c=0. The discrete variables d are <br>
 * updated upon occurence of specific events. When those events are functions of <br>
 * time or state, they are detected using the set of scalar-valued event trigger <br>
 * functions e (3).<br>
 * <br>
 * In the more detailed view as seen from the System, we consider y={q,u,z} to <br>
 * be partitioned into position variables q, velocity variables u, and auxiliary <br>
 * variables z. There will be algebraic constraints involving q, u, and u's time <br>
 * derivatives udot. The system is now assumed to look like this:<br>
 * <pre><br>
 *      (4) qdot    = N(q) u<br>
 *      (5) zdot    = zdot(d;t,q,u,z)<br>
 * <br>
 *      (6) M(q) udot + ~G(q) mult = f(d;t,q,u,z)<br>
 *          G(q) udot              = b(d;t,q,u)<br>
 * <br>
 *                    [ pdotdot(d;t,q,u,udot) ]<br>
 *      (7) udotErr = [ vdot(d;t,q,u,udot)    ] = 0<br>
 *                    [ a(d;t,q,u,udot)       ] <br>
 * <br>
 *      (8) uErr    = [ pdot(d;t,q,u) ]         = 0<br>
 *                    [ v(d;t,q,u)    ]<br>
 * <br>
 *      (9) qErr    = [ p(d;t,q) ]              = 0<br>
 *                    [ n(q)     ]<br>
 * </pre><br>
 * The q's can also be dealt with directly as second order variables via<br>
 * <pre><br>
 *     (10) qdotdot = Ndot(q,qdot) u + N(q) udot<br>
 * </pre><br>
 * <br>
 * Here G = [P;V;A] with A(q) being the coefficient matrix for constraints<br>
 * appearing only at the acceleration level, and V(q)=partial(v)/partial(u)<br>
 * the coefficient matrix for the velocity (nonholonomic) constraints, and<br>
 * P(q)=partial(pdot)/partial(u) is the coefficient matrix of the first<br>
 * time derivatives of the position (holonomic) constraints.<br>
 * Note that uErr in Eq 8 is assumed to include equations resulting from<br>
 * differentiation of p() in Eq 9, as well as ones first introduced at the<br>
 * velocity level (nonholonomic constraints), and udotErr is similarly <br>
 * built from acceleration-only constraints a() and derivatives of higher-level<br>
 * constraints.<br>
 * <br>
 * If a system allocates nq q's, nu u's, and nz z's the State will also allocate <br>
 * matching cache variables qdot, qdotdot, udot, and zdot. If mp position <br>
 * (holonomic) constraints (9), mpv velocity constraints (8) and mpva acceleration<br>
 * constraints (7) are allocated, the state creates cache entries of like sizes <br>
 * qErr, uErr, udotErr. In addition room for the mpva Lagrange multipliers 'mult' <br>
 * is allocated in the cache.<br>
 * <br>
 * In the final view, the Subsystem view, the same variables and cache entries <br>
 * exist, but only the ones allocated by that Subsystem are visible. All of a <br>
 * Subsystem's q's are consecutive in memory, as are its u's, uErr's, etc., but <br>
 * the q's are not adjacent to the u's as they are for the System's view.<br>
 * <br>
 * The default constructor creates a %State containing no state variables and with <br>
 * its realization cache stage set to Stage::Empty. During subsystem construction,<br>
 * variables and cache entries for any stage can be allocated, however <i>all</i> <br>
 * Model stage variables must be allocated during this time. At the end of <br>
 * construction, call advanceSubsystemToStage(Topology) which will put the <br>
 * subsystem at Stage::Topology. Then the subsystems realize their Model stages, <br>
 * during which variables at any stage &gt; Model, and cache entries at any stage<br>
 * &gt;= Model can be allocated. After that call advanceSubsystemToStage(Model)<br>
 * which sets the stage to Stage::Model and disallows further state allocation.<br>
 * <br>
 * Note that there is a global Stage for the state as a whole, and individual<br>
 * Stages for each subsystem. The global stage can never be higher than<br>
 * the lowest subsystem stage. Global state resources are allocated when the<br>
 * global Stage advances to "Model" and tossed out if that stage is<br>
 * invalidated. Similarly, cache resources are allocated at stage Instance<br>
 * and forgotten when Instance is invalidated. Note that subsystems will<br>
 * "register" their use of the global variable pools during their own modeling<br>
 * stages, but that the actual global resources won't exist until the <i>system</i> <br>
 * has been advanced to Model or Instance stage. *
 */
public class State {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  public State(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(State obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  @SuppressWarnings("deprecation")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        opensimSimbodyJNI.delete_State(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  /**
   *  Create an empty State.
   */
  public State() {
    this(opensimSimbodyJNI.new_State__SWIG_0(), true);
  }

  /**
   *  Restore State to default-constructed condition.
   */
  public void clear() {
    opensimSimbodyJNI.State_clear(swigCPtr, this);
  }

  /**
   *  Set the number of subsystems in this state. This is done during<br>
   *  initialization of the State by a System; it completely wipes out<br>
   *  anything that used to be in the State so use cautiously!
   */
  public void setNumSubsystems(int i) {
    opensimSimbodyJNI.State_setNumSubsystems(swigCPtr, this, i);
  }

  /**
   *  Make the current State a copy of the source state, copying only<br>
   *  state variables and not the cache. If the source state hasn't<br>
   *  been realized to Model stage, then we don't copy its state<br>
   *  variables either, except those associated with the Topology stage.
   */
  public State(State arg0) {
    this(opensimSimbodyJNI.new_State__SWIG_1(State.getCPtr(arg0), arg0), true);
  }

  public int getNumSubsystems() {
    return opensimSimbodyJNI.State_getNumSubsystems(swigCPtr, this);
  }

  /**
   *  This returns the *global* stage for this State.
   */
  public Stage getSystemStage() {
    return new Stage(opensimSimbodyJNI.State_getSystemStage(swigCPtr, this), false);
  }

  /**
   *  If any subsystem or the system stage is currently at or<br>
   *  higher than the passed-in one, back up to the stage just prior;<br>
   *  otherwise do nothing. This is for use if you have write<br>
   *  access to the state and can invalidate even Topology and Model<br>
   *  stages which may destroy state variables. "All" here refers to<br>
   *  all Subysystems.
   */
  public void invalidateAll(Stage arg0) {
    opensimSimbodyJNI.State_invalidateAll(swigCPtr, this, Stage.getCPtr(arg0), arg0);
  }

  /**
   *  If any subsystem or the system stage is currently at or<br>
   *  higher than the passed-in one, back up to the stage just prior;<br>
   *  otherwise do nothing. This const method can only be used to<br>
   *  invalidate Stage::Instance or higher. To invalidate Model or <br>
   *  Topology stage you must have write access to the state because<br>
   *  invalidating those stages can destroy state variables in addition <br>
   *  to cache entries. "All" here refers to all Subsystems.
   */
  public void invalidateAllCacheAtOrAbove(Stage arg0) {
    opensimSimbodyJNI.State_invalidateAllCacheAtOrAbove(swigCPtr, this, Stage.getCPtr(arg0), arg0);
  }

  /**
   *  These are the dimensions of the global shared state and cache resources,<br>
   *  as well as the dimensions of the per-Subsystem partioning of those<br>
   *  resources. State resource dimensions (including cache resources directly<br>
   *  related to state variables) are known after the System has been<br>
   *  realized to Model stage. Other cache resource dimensions are known after<br>
   *  the System has been realized to Instance stage. Access to the actual data arrays<br>
   *  may have stricter requirements (for example, you can't ask to look at UErr<br>
   *  arrays until Velocity stage). Hence it is better to use these explicit <br>
   *  dimension-providing methods than to get a reference to a Vector and ask<br>
   *  for its size().<br>
   * <br>
   *  @see Per-Subsystem Dimensions group @see Global-to-Subsystem Maps group  Get the total number ny=nq+nu+nz of shared continuous state variables. This is also the number of state derivatives in the cache entry ydot. Callable at Model stage.
   */
  public int getNY() {
    return opensimSimbodyJNI.State_getNY(swigCPtr, this);
  }

  /**
   *  Get total number of shared q's (generalized coordinates; second order<br>
   *  state variables). This is also the number of first and second q time<br>
   *  derivatives in the cache entries qdot and qdotdot.<br>
   *  Callable at Model stage.
   */
  public int getNQ() {
    return opensimSimbodyJNI.State_getNQ__SWIG_0(swigCPtr, this);
  }

  /**
   *  Returns the y index at which the q's begin. Callable at Model stage.
   */
  public int getQStart() {
    return opensimSimbodyJNI.State_getQStart__SWIG_0(swigCPtr, this);
  }

  /**
   *  Get total number of shared u's (generalized speeds; mobilities). <br>
   *  This is also the number of u time derivatives in the cache entry udot.<br>
   *  Callable at Model stage.
   */
  public int getNU() {
    return opensimSimbodyJNI.State_getNU__SWIG_0(swigCPtr, this);
  }

  /**
   *  Returns the y index at which the u's begin. Callable at Model stage.
   */
  public int getUStart() {
    return opensimSimbodyJNI.State_getUStart__SWIG_0(swigCPtr, this);
  }

  /**
   *  Get total number of shared z's (auxiliary state variables). <br>
   *  This is also the number of z time derivatives in the cache entry zdot.<br>
   *  Callable at Model stage.
   */
  public int getNZ() {
    return opensimSimbodyJNI.State_getNZ__SWIG_0(swigCPtr, this);
  }

  /**
   *  Returns the y index at which the z's begin. Callable at Model stage.
   */
  public int getZStart() {
    return opensimSimbodyJNI.State_getZStart__SWIG_0(swigCPtr, this);
  }

  /**
   *  Get the total number nyerr=nqerr+nuerr of shared cache entries for<br>
   *  position-level and velocity-level constraint errors.<br>
   *  Callable at Instance stage.
   */
  public int getNYErr() {
    return opensimSimbodyJNI.State_getNYErr(swigCPtr, this);
  }

  /**
   *  Return the total number nqerr=mp+nQuaternions of cache entries for<br>
   *  position-level constraint errors. Callable at Instance stage.
   */
  public int getNQErr() {
    return opensimSimbodyJNI.State_getNQErr__SWIG_0(swigCPtr, this);
  }

  /**
   *  Returns the yErr index at which the qErr's begin. Callable at Instance stage.
   */
  public int getQErrStart() {
    return opensimSimbodyJNI.State_getQErrStart__SWIG_0(swigCPtr, this);
  }

  /**
   *  Return the total number nuerr=mp+mv of cache entries for<br>
   *  velocity-level constraint errors (including also errors in the <br>
   *  time derivatives of position-level constraints). Callable at Instance stage.
   */
  public int getNUErr() {
    return opensimSimbodyJNI.State_getNUErr__SWIG_0(swigCPtr, this);
  }

  /**
   *  Returns the yErr index at which the uErr's begin. Callable at Instance stage.
   */
  public int getUErrStart() {
    return opensimSimbodyJNI.State_getUErrStart__SWIG_0(swigCPtr, this);
  }

  /**
   *  Return the total number nudotErr=mp+mv+ma of cache entries for<br>
   *  acceleration-level constraint errors (including also errors in the <br>
   *  second time derivatives of position-level constraints and the first<br>
   *  time derivatives of velocity-level constraints). Callable at Instance stage.
   */
  public int getNUDotErr() {
    return opensimSimbodyJNI.State_getNUDotErr__SWIG_0(swigCPtr, this);
  }

  /**
   *  Return the total number of constraint multipliers; necessarily the same<br>
   *  as the number of acceleration-level constraint errors nUDotErr. Callable<br>
   *  at Instance stage.<br>
   *  @see getNUDotErr()
   */
  public int getNMultipliers() {
    return opensimSimbodyJNI.State_getNMultipliers(swigCPtr, this);
  }

  /**
   *  Return the total number of event trigger function slots in the cache.<br>
   *  Callable at Instance stage.
   */
  public int getNEventTriggers() {
    return opensimSimbodyJNI.State_getNEventTriggers(swigCPtr, this);
  }

  /**
   *  Return the size of the partition of event trigger functions which are <br>
   *  evaluated at a given Stage. Callable at Instance stage.
   */
  public int getNEventTriggersByStage(Stage arg0) {
    return opensimSimbodyJNI.State_getNEventTriggersByStage(swigCPtr, this, Stage.getCPtr(arg0), arg0);
  }

  /**
   *  Return the index within the global event trigger array at which the<br>
   *  first of the event triggers associated with a particular Stage are stored;<br>
   *  the rest follow contiguously. Callable at Instance stage.
   */
  public SWIGTYPE_p_SystemEventTriggerIndex getEventTriggerStartByStage(Stage arg0) {
    return new SWIGTYPE_p_SystemEventTriggerIndex(opensimSimbodyJNI.State_getEventTriggerStartByStage(swigCPtr, this, Stage.getCPtr(arg0), arg0), true);
  }

  /**
   *  <br>
   * <br>
   *  These are the dimensions and locations within the global resource arrays<br>
   *  of state and cache resources allocated to a particular Subsystem. Note<br>
   *  that a Subsystem has contiguous q's, contiguous u's, and contiguous z's<br>
   *  but that the q-, u-, and z-partitions are not contiguous. Hence there is<br>
   *  no Subsystem equivalent of the global y vector.<br>
   * <br>
   *  These serve as a mapping from Subsystem-local indices for the various<br>
   *  shared resources to their global resource indices.<br>
   * <br>
   *  @see Global Resource Dimensions  
   */
  public int getQStart(int arg0) {
    return opensimSimbodyJNI.State_getQStart__SWIG_1(swigCPtr, this, arg0);
  }

  public int getNQ(int arg0) {
    return opensimSimbodyJNI.State_getNQ__SWIG_1(swigCPtr, this, arg0);
  }

  public int getUStart(int arg0) {
    return opensimSimbodyJNI.State_getUStart__SWIG_1(swigCPtr, this, arg0);
  }

  public int getNU(int arg0) {
    return opensimSimbodyJNI.State_getNU__SWIG_1(swigCPtr, this, arg0);
  }

  public int getZStart(int arg0) {
    return opensimSimbodyJNI.State_getZStart__SWIG_1(swigCPtr, this, arg0);
  }

  public int getNZ(int arg0) {
    return opensimSimbodyJNI.State_getNZ__SWIG_1(swigCPtr, this, arg0);
  }

  public int getQErrStart(int arg0) {
    return opensimSimbodyJNI.State_getQErrStart__SWIG_1(swigCPtr, this, arg0);
  }

  public int getNQErr(int arg0) {
    return opensimSimbodyJNI.State_getNQErr__SWIG_1(swigCPtr, this, arg0);
  }

  public int getUErrStart(int arg0) {
    return opensimSimbodyJNI.State_getUErrStart__SWIG_1(swigCPtr, this, arg0);
  }

  public int getNUErr(int arg0) {
    return opensimSimbodyJNI.State_getNUErr__SWIG_1(swigCPtr, this, arg0);
  }

  public int getUDotErrStart(int arg0) {
    return opensimSimbodyJNI.State_getUDotErrStart(swigCPtr, this, arg0);
  }

  public int getNUDotErr(int arg0) {
    return opensimSimbodyJNI.State_getNUDotErr__SWIG_1(swigCPtr, this, arg0);
  }

  /**
   *  
   */
  public Vector getEventTriggers() {
    return new Vector(opensimSimbodyJNI.State_getEventTriggers(swigCPtr, this), false);
  }

  public Vector getEventTriggersByStage(Stage arg0) {
    return new Vector(opensimSimbodyJNI.State_getEventTriggersByStage__SWIG_0(swigCPtr, this, Stage.getCPtr(arg0), arg0), false);
  }

  public Vector getEventTriggersByStage(int arg0, Stage arg1) {
    return new Vector(opensimSimbodyJNI.State_getEventTriggersByStage__SWIG_1(swigCPtr, this, arg0, Stage.getCPtr(arg1), arg1), false);
  }

  public Vector updEventTriggers() {
    return new Vector(opensimSimbodyJNI.State_updEventTriggers(swigCPtr, this), false);
  }

  public Vector updEventTriggersByStage(Stage arg0) {
    return new Vector(opensimSimbodyJNI.State_updEventTriggersByStage__SWIG_0(swigCPtr, this, Stage.getCPtr(arg0), arg0), false);
  }

  public Vector updEventTriggersByStage(int arg0, Stage arg1) {
    return new Vector(opensimSimbodyJNI.State_updEventTriggersByStage__SWIG_1(swigCPtr, this, arg0, Stage.getCPtr(arg1), arg1), false);
  }

  /**
   *  Per-subsystem access to the global shared variables.
   */
  public Vector getQ(int arg0) {
    return new Vector(opensimSimbodyJNI.State_getQ__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector getU(int arg0) {
    return new Vector(opensimSimbodyJNI.State_getU__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector getZ(int arg0) {
    return new Vector(opensimSimbodyJNI.State_getZ__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector getUWeights(int arg0) {
    return new Vector(opensimSimbodyJNI.State_getUWeights__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector getZWeights(int arg0) {
    return new Vector(opensimSimbodyJNI.State_getZWeights__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector updQ(int arg0) {
    return new Vector(opensimSimbodyJNI.State_updQ__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector updU(int arg0) {
    return new Vector(opensimSimbodyJNI.State_updU__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector updZ(int arg0) {
    return new Vector(opensimSimbodyJNI.State_updZ__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector updUWeights(int arg0) {
    return new Vector(opensimSimbodyJNI.State_updUWeights__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector updZWeights(int arg0) {
    return new Vector(opensimSimbodyJNI.State_updZWeights__SWIG_0(swigCPtr, this, arg0), false);
  }

  /**
   *  Per-subsystem access to the shared cache entries.
   */
  public Vector getQDot(int arg0) {
    return new Vector(opensimSimbodyJNI.State_getQDot__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector getUDot(int arg0) {
    return new Vector(opensimSimbodyJNI.State_getUDot__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector getZDot(int arg0) {
    return new Vector(opensimSimbodyJNI.State_getZDot__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector getQDotDot(int arg0) {
    return new Vector(opensimSimbodyJNI.State_getQDotDot__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector updQDot(int arg0) {
    return new Vector(opensimSimbodyJNI.State_updQDot__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector updUDot(int arg0) {
    return new Vector(opensimSimbodyJNI.State_updUDot__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector updZDot(int arg0) {
    return new Vector(opensimSimbodyJNI.State_updZDot__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector updQDotDot(int arg0) {
    return new Vector(opensimSimbodyJNI.State_updQDotDot__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector getQErr(int arg0) {
    return new Vector(opensimSimbodyJNI.State_getQErr__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector getUErr(int arg0) {
    return new Vector(opensimSimbodyJNI.State_getUErr__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector getUDotErr(int arg0) {
    return new Vector(opensimSimbodyJNI.State_getUDotErr__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector getMultipliers(int arg0) {
    return new Vector(opensimSimbodyJNI.State_getMultipliers__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector getQErrWeights(int arg0) {
    return new Vector(opensimSimbodyJNI.State_getQErrWeights__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector getUErrWeights(int arg0) {
    return new Vector(opensimSimbodyJNI.State_getUErrWeights__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector updQErr(int arg0) {
    return new Vector(opensimSimbodyJNI.State_updQErr__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector updUErr(int arg0) {
    return new Vector(opensimSimbodyJNI.State_updUErr__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector updUDotErr(int arg0) {
    return new Vector(opensimSimbodyJNI.State_updUDotErr__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector updMultipliers(int arg0) {
    return new Vector(opensimSimbodyJNI.State_updMultipliers__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector updQErrWeights(int arg0) {
    return new Vector(opensimSimbodyJNI.State_updQErrWeights__SWIG_0(swigCPtr, this, arg0), false);
  }

  public Vector updUErrWeights(int arg0) {
    return new Vector(opensimSimbodyJNI.State_updUErrWeights__SWIG_0(swigCPtr, this, arg0), false);
  }

  /**
   *  You can call these as long as *system* stage &gt;= Model.
   */
  public double getTime() {
    return opensimSimbodyJNI.State_getTime(swigCPtr, this);
  }

  public Vector getY() {
    return new Vector(opensimSimbodyJNI.State_getY(swigCPtr, this), false);
  }

  /**
   *  These are just views into Y.
   */
  public Vector getQ() {
    return new Vector(opensimSimbodyJNI.State_getQ__SWIG_1(swigCPtr, this), false);
  }

  public Vector getU() {
    return new Vector(opensimSimbodyJNI.State_getU__SWIG_1(swigCPtr, this), false);
  }

  public Vector getZ() {
    return new Vector(opensimSimbodyJNI.State_getZ__SWIG_1(swigCPtr, this), false);
  }

  /**
   *  Get a unit weighting (1/unit change) for each u that can be used to <br>
   * weight a vector du so that the disparate elements are comparable in physical<br>
   * effect. This permits mixing of generalized speeds<br>
   * that have different units, and scaling of generalized speeds that have<br>
   * differing amounts of leverage due to their positions in the multibody tree.<br>
   * This can be used to create a scaled norm that represents the overall<br>
   * significance of a change du to u.<br>
   * <br>
   * Define a unit change di for each ui such that a change<br>
   * ui+eps*di to each generalized speed in turn produces a physical velocity change<br>
   * of roughly equal significance. Then a diagonal matrix Wu=diag(1/di) is <br>
   * a weighting matrix such that wdu=Wu*du is a vector in which each element wdu_i<br>
   * has units of "unit change" for its corresponding ui. This method returns a<br>
   * vector which is the diagonal of Wu.<br>
   * <br>
   * These same weights on u also determine the scaling of the generalized<br>
   * coordinates q, because q and u are related via qdot=N*u. For cases where<br>
   * qdot_i=u_i, the numerical value of the unit change to q_i is just di because <br>
   * dP/dq_i == dV/du_i. Otherwise, they are related by Wq = N*Wu*pinv(N) where <br>
   * Wq is the weighting matrix for dq (block diagonal), and pinv() is the<br>
   * pseudoinverse.<br>
   * <br>
   * For example, say you define unit scaling for an angle coordinate to be 1 radian<br>
   * (about 57 degrees), meaning that a 1 radian change of coordinate produces<br>
   * (roughly) one length unit of meaningful position change. Then if a generalized<br>
   * coordinate is measured in radians, its unit scale would be 1. If instead you <br>
   * created a generalized coordinate with units of degrees, its unit scale would <br>
   * be 57 degrees. That would allow mixing of such coordinates in the same system <br>
   * by bringing the coordinates into a physically-meaningful basis.<br>
   * Scaling is defined in the u basis where each variable is independent;<br>
   * the N matrix couples variables in the q basis. So here the units would actually<br>
   * be 1 radian/time unit and 57 degrees/time unit (numerically identical).<br>
   * <br>
   * This is allocated and set to 1 at the end of realize(Model). *
   */
  public Vector getUWeights() {
    return new Vector(opensimSimbodyJNI.State_getUWeights__SWIG_1(swigCPtr, this), false);
  }

  /**
   *  Get a unit weighting (1/unit change) for each z that can be used to <br>
   * weight a vector dz so that the disparate elements are comparable in physical<br>
   * effect. This defines a weighting matrix Wz=diag(1/unitchange_zi) such<br>
   * that wdz=Wz*dz is a vector in which each element wdz_i has units of<br>
   * "unit change" for its corresponding zi.  This method returns a<br>
   * vector which is the diagonal of Wz. *
   */
  public Vector getZWeights() {
    return new Vector(opensimSimbodyJNI.State_getZWeights__SWIG_1(swigCPtr, this), false);
  }

  /**
   *  Set u weights (and q weights indirectly). You can call this after Model <br>
   * stage has been realized. This will invalidate just Report stage because it is <br>
   * not used in calculating udots. *
   */
  public Vector updUWeights() {
    return new Vector(opensimSimbodyJNI.State_updUWeights__SWIG_1(swigCPtr, this), false);
  }

  /**
   *  Set z weights. You can call this after Model stage has been realized. This<br>
   * will invalidate just Report stage because it is not used in calculating <br>
   * zdots. *
   */
  public Vector updZWeights() {
    return new Vector(opensimSimbodyJNI.State_updZWeights__SWIG_1(swigCPtr, this), false);
  }

  /**
   *  You can call these as long as System stage &gt;= Model, but the<br>
   *  stage will be backed up if necessary to the indicated stage.
   */
  public SWIGTYPE_p_double updTime() {
    return new SWIGTYPE_p_double(opensimSimbodyJNI.State_updTime(swigCPtr, this), false);
  }

  public Vector updY() {
    return new Vector(opensimSimbodyJNI.State_updY(swigCPtr, this), false);
  }

  /**
   *  An alternate syntax equivalent to updTime() and updY().
   */
  public void setTime(double t) {
    opensimSimbodyJNI.State_setTime(swigCPtr, this, t);
  }

  public void setY(Vector y) {
    opensimSimbodyJNI.State_setY(swigCPtr, this, Vector.getCPtr(y), y);
  }

  /**
   *  These are just views into Y.
   */
  public Vector updQ() {
    return new Vector(opensimSimbodyJNI.State_updQ__SWIG_1(swigCPtr, this), false);
  }

  public Vector updU() {
    return new Vector(opensimSimbodyJNI.State_updU__SWIG_1(swigCPtr, this), false);
  }

  public Vector updZ() {
    return new Vector(opensimSimbodyJNI.State_updZ__SWIG_1(swigCPtr, this), false);
  }

  /**
   *  Alternate interface.
   */
  public void setQ(Vector q) {
    opensimSimbodyJNI.State_setQ(swigCPtr, this, Vector.getCPtr(q), q);
  }

  public void setU(Vector u) {
    opensimSimbodyJNI.State_setU(swigCPtr, this, Vector.getCPtr(u), u);
  }

  public void setZ(Vector z) {
    opensimSimbodyJNI.State_setZ(swigCPtr, this, Vector.getCPtr(z), z);
  }

  public Vector getYDot() {
    return new Vector(opensimSimbodyJNI.State_getYDot(swigCPtr, this), false);
  }

  /**
   *  These are just views into YDot.
   */
  public Vector getQDot() {
    return new Vector(opensimSimbodyJNI.State_getQDot__SWIG_1(swigCPtr, this), false);
  }

  public Vector getZDot() {
    return new Vector(opensimSimbodyJNI.State_getZDot__SWIG_1(swigCPtr, this), false);
  }

  public Vector getUDot() {
    return new Vector(opensimSimbodyJNI.State_getUDot__SWIG_1(swigCPtr, this), false);
  }

  /**
   *  This has its own space, not a view.
   */
  public Vector getQDotDot() {
    return new Vector(opensimSimbodyJNI.State_getQDotDot__SWIG_1(swigCPtr, this), false);
  }

  /**
   *  These are mutable
   */
  public Vector updYDot() {
    return new Vector(opensimSimbodyJNI.State_updYDot(swigCPtr, this), false);
  }

  public Vector updQDot() {
    return new Vector(opensimSimbodyJNI.State_updQDot__SWIG_1(swigCPtr, this), false);
  }

  public Vector updZDot() {
    return new Vector(opensimSimbodyJNI.State_updZDot__SWIG_1(swigCPtr, this), false);
  }

  public Vector updUDot() {
    return new Vector(opensimSimbodyJNI.State_updUDot__SWIG_1(swigCPtr, this), false);
  }

  /**
   *  This is a separate shared cache entry, not part of YDot. If you<br>
   *  have a direct 2nd order integrator you can integrate QDotDot<br>
   *  (twice) to get Q.
   */
  public Vector updQDotDot() {
    return new Vector(opensimSimbodyJNI.State_updQDotDot__SWIG_1(swigCPtr, this), false);
  }

  /**
   *  Return the current constraint errors for all constraints. This<br>
   *  is {QErr,UErr} packed and in that order.
   */
  public Vector getYErr() {
    return new Vector(opensimSimbodyJNI.State_getYErr(swigCPtr, this), false);
  }

  /**
   *  These are just views into YErr.
   */
  public Vector getQErr() {
    return new Vector(opensimSimbodyJNI.State_getQErr__SWIG_1(swigCPtr, this), false);
  }

  public Vector getUErr() {
    return new Vector(opensimSimbodyJNI.State_getUErr__SWIG_1(swigCPtr, this), false);
  }

  /**
   *  These have their own space, they are not views.
   */
  public Vector getUDotErr() {
    return new Vector(opensimSimbodyJNI.State_getUDotErr__SWIG_1(swigCPtr, this), false);
  }

  public Vector getMultipliers() {
    return new Vector(opensimSimbodyJNI.State_getMultipliers__SWIG_1(swigCPtr, this), false);
  }

  /**
   *  Get the unit weighting (1/unit error) for each of the mp+mquat position <br>
   * constraints equations. Allocated and initialized to 1 on realize(Instance). *
   */
  public Vector getQErrWeights() {
    return new Vector(opensimSimbodyJNI.State_getQErrWeights__SWIG_1(swigCPtr, this), false);
  }

  /**
   *  Get the unit weighting (1/unit error) for each of the mp+mv velocity-level <br>
   * constraint equations, meaning mp time derivatives of position (holonomic) <br>
   * constraint equations followed by mv velocity (nonholonomic) constraints.<br>
   * Typically the weight of position constraint derivatives is just the<br>
   * position constraint weight times the System's characteristic time scale. <br>
   * <br>
   * There is no entry corresponding to quaternions here since they do not <br>
   * produce velocity-level constraints in Simbody's forumulation.<br>
   * <br>
   * This is allocated and initialized to 1 on realize(Instance). *
   */
  public Vector getUErrWeights() {
    return new Vector(opensimSimbodyJNI.State_getUErrWeights__SWIG_1(swigCPtr, this), false);
  }

  /**
   *  Set the unit weighting (1/unit error) for each of the mp+mquat position <br>
   * constraint equations. You can call this after the weight variable is allocated <br>
   * at the end of Instance stage. Position stage is invalidated to force <br>
   * recalculation of weighted position constraint errors. *
   */
  public Vector updQErrWeights() {
    return new Vector(opensimSimbodyJNI.State_updQErrWeights__SWIG_1(swigCPtr, this), false);
  }

  /**
   *  Set the unit weighting (1/unit error) for each of the mp+mv velocity-level<br>
   * constraints. You can call this after the weight variable is allocated at the <br>
   * end of Instance stage. Velocity stage is invalidated to force recalculation of <br>
   * weighted velocity-level constraint errors. *
   */
  public Vector updUErrWeights() {
    return new Vector(opensimSimbodyJNI.State_updUErrWeights__SWIG_1(swigCPtr, this), false);
  }

  /**
   *  These are mutable
   */
  public Vector updYErr() {
    return new Vector(opensimSimbodyJNI.State_updYErr(swigCPtr, this), false);
  }

  public Vector updQErr() {
    return new Vector(opensimSimbodyJNI.State_updQErr__SWIG_1(swigCPtr, this), false);
  }

  public Vector updUErr() {
    return new Vector(opensimSimbodyJNI.State_updUErr__SWIG_1(swigCPtr, this), false);
  }

  public Vector updUDotErr() {
    return new Vector(opensimSimbodyJNI.State_updUDotErr__SWIG_1(swigCPtr, this), false);
  }

  public Vector updMultipliers() {
    return new Vector(opensimSimbodyJNI.State_updMultipliers__SWIG_1(swigCPtr, this), false);
  }

  /**
   *  (Advanced) Record the current version numbers of each valid System-level <br>
   * stage. This can be used to unambiguously determine what stages have been <br>
   * changed by some opaque operation, even if that operation realized the stages <br>
   * after modifying them. This is particularly useful for event handlers as a way <br>
   * for a time stepper to know how much damage may have been done by a handler, and<br>
   * thus how much reinitialization is required before continuing on.<br>
   * @see getLowestSystemStageDifference() *
   */
  public void getSystemStageVersions(SimTKArrayInt versions) {
    opensimSimbodyJNI.State_getSystemStageVersions(swigCPtr, this, SimTKArrayInt.getCPtr(versions), versions);
  }

  /**
   *  (Advanced) Given a list of per-stage version numbers extracted by an <br>
   * earlier call to getSystemStageVersions(), note the lowest system stage in the <br>
   * current State whose version number differs from the corresponding previous <br>
   * version number. Returns Stage::Infinity if all the stages present in <br>
   * <i>prevVersions</i> are valid and have identical versions now, even if there are <br>
   * additional valid stages now, since nothing the caller cared about before has <br>
   * been changed. If the current State is not realized as far as the previous one, <br>
   * then the first unrealized stage is returned if all the lower versions match.<br>
   * @see getSystemStageVersions() *
   */
  public Stage getLowestSystemStageDifference(SimTKArrayInt prevVersions) {
    return new Stage(opensimSimbodyJNI.State_getLowestSystemStageDifference(swigCPtr, this, SimTKArrayInt.getCPtr(prevVersions), prevVersions), true);
  }

  /**
   *  (Advanced) This explicitly modifies the Topology stage version; don't<br>
   * use this method unless you know what you're doing! This can be used to force<br>
   * compatibility with a System that has had Topology changes since this %State<br>
   * was created. This has no effect on the realization level.<br>
   * @see getSystemTopologyStageVersion(), System::getSystemTopologyCacheVersion()
   */
  public void setSystemTopologyStageVersion(int topoVersion) {
    opensimSimbodyJNI.State_setSystemTopologyStageVersion(swigCPtr, this, topoVersion);
  }

  /**
   *  (Advanced) This is called at the beginning of every integration step to set<br>
   * the values of auto-update discrete variables from the values stored in their <br>
   * associated cache entries. *
   */
  public void autoUpdateDiscreteVariables() {
    opensimSimbodyJNI.State_autoUpdateDiscreteVariables(swigCPtr, this);
  }

  public SWIGTYPE_p_String cacheToString() {
    return new SWIGTYPE_p_String(opensimSimbodyJNI.State_cacheToString(swigCPtr, this), true);
  }

}
