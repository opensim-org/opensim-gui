/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 4.0.2
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 * A Socket formalizes the dependency between a Component and another object<br>
 * (typically another Component) without owning that object. While Components<br>
 * can be composites (of multiple components) they often depend on unrelated<br>
 * objects/components that are defined and owned elsewhere. The object that<br>
 * satisfies the requirements of the Socket we term the "connectee". When a<br>
 * Socket is satisfied by a connectee we have a successful "connection" or is<br>
 * said to be connected.<br>
 * <br>
 * The purpose of a Socket is to specify: 1) the connectee type that the<br>
 * Component is dependent on, 2) by when (what stage) the socket must be<br>
 * connected in order for the component to function, 3) the name of a connectee<br>
 * that can be found at run-time to satisfy the socket, and 4) whether or<br>
 * not it is connected. A Socket maintains a reference to the instance<br>
 * (connectee) until it is disconnected.<br>
 * <br>
 * For example, a Joint has two Sockets for the parent and child Bodies that<br>
 * it joins. The type for the socket is a PhysicalFrame and any attempt to <br>
 * connect to a non-Body (or frame rigidly attached to a Body) will throw an<br>
 * exception. The connectAt Stage is Topology. That is, the Joint's connection <br>
 * to a Body must be performed at the Topology system stage, and any attempt to<br>
 * change the connection status will invalidate that Stage and above.<br>
 * <br>
 * Other Components like a Marker or a Probe that do not change the system<br>
 * topology or add new states could potentially be connected at later stages<br>
 * like Model or Instance.<br>
 * <br>
 * Programmatically, the connectee can be specified as an object reference<br>
 * or via a connectee path:<br>
 * <br>
 * {@code 
// Specifying the connectee using an object reference.
socket.connect(myConnectee);
// Specifying the connectee via a path.
socket.setConnecteePath("/path/to/connectee");
}<br>
 * <br>
 * Use finalizeConnection() to synchronize the object reference and connectee<br>
 * name. It is preferable to use connect() instead of setConnecteePath().<br>
 * If *both* are set, then the object reference overrides the connectee path.<br>
 * <br>
 * The connectee path appears in XML files and is how a connection is maintained<br>
 * across serialization (writing to an XML file) and deserialization (reading<br>
 * from an XML file).<br>
 * <br>
 * @author Ajay Seth
 */
public class AbstractSocket {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  public AbstractSocket(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  public static long getCPtr(AbstractSocket obj) {
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
        opensimCommonJNI.delete_AbstractSocket(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  /**
   *  Create a dynamically-allocated copy. You must manage the memory<br>
   *  for the returned pointer.<br>
   *  This function exists to facilitate the use of<br>
   *  SimTK::ClonePtr&lt;AbstractSocket&gt;.
   */
  public AbstractSocket clone() {
    long cPtr = opensimCommonJNI.AbstractSocket_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new AbstractSocket(cPtr, true);
  }

  public String getName() {
    return opensimCommonJNI.AbstractSocket_getName(swigCPtr, this);
  }

  /**
   *  Get the system Stage when the connection should be made. 
   */
  public Stage getConnectAtStage() {
    return new Stage(opensimCommonJNI.AbstractSocket_getConnectAtStage(swigCPtr, this), true);
  }

  /**
   *  Can this Socket have more than one connectee? 
   */
  public boolean isListSocket() {
    return opensimCommonJNI.AbstractSocket_isListSocket(swigCPtr, this);
  }

  /**
   *  The number of slots to fill in order to satisfy this socket.<br>
   * This is 1 for a non-list socket. This is the number of elements in the<br>
   * connectee path property; to sync this with the number of connectee<br>
   * objects, call finalizeConnection(). 
   */
  public long getNumConnectees() {
    return opensimCommonJNI.AbstractSocket_getNumConnectees(swigCPtr, this);
  }

  /**
   *  Derived classes must satisfy this Interface  Is the Socket connected to its connectee(s)? For a list socket,<br>
   *     this is only true if this socket is connected to all its connectees.
   */
  public boolean isConnected() {
    return opensimCommonJNI.AbstractSocket_isConnected(swigCPtr, this);
  }

  /**
   *  Get the type of object this socket connects to. 
   */
  public String getConnecteeTypeName() {
    return opensimCommonJNI.AbstractSocket_getConnecteeTypeName(swigCPtr, this);
  }

  /**
   *  Generic access to the connectee. Not all sockets support this method<br>
   * (e.g., the connectee for an Input is not an Object). 
   */
  public OpenSimObject getConnecteeAsObject() {
    return new OpenSimObject(opensimCommonJNI.AbstractSocket_getConnecteeAsObject(swigCPtr, this), false);
  }

  /**
   *  Returns `true` if the socket can connect to the object (i.e. because<br>
   *         the object is a matching type for the socket) 
   */
  public boolean canConnectTo(OpenSimObject arg0) {
    return opensimCommonJNI.AbstractSocket_canConnectTo(swigCPtr, this, OpenSimObject.getCPtr(arg0), arg0);
  }

  /**
   *  Connect this %Socket to the provided connectee object. If this is a<br>
   *         list socket, the connectee is appended to the list of connectees;<br>
   *         otherwise, the provided connectee replaces the single connectee. 
   */
  public void connect(OpenSimObject connectee) {
    opensimCommonJNI.AbstractSocket_connect(swigCPtr, this, OpenSimObject.getCPtr(connectee), connectee);
  }

  /**
   *  Find the connectee using a search with a partial path. Use this if you<br>
   * do not want to specify an exact path (maybe you don't quite know where<br>
   * the connectee is located).
   */
  public void findAndConnect(SWIGTYPE_p_ComponentPath connectee) {
    opensimCommonJNI.AbstractSocket_findAndConnect__SWIG_0(swigCPtr, this, SWIGTYPE_p_ComponentPath.getCPtr(connectee));
  }

  /**
   *  Same as findAndConnect(const ComponentPath&amp;), but using a string<br>
   * argument. 
   */
  public void findAndConnect(String connectee) {
    opensimCommonJNI.AbstractSocket_findAndConnect__SWIG_1(swigCPtr, this, connectee);
  }

  /**
   *  Connect this %Socket according to its connectee path property<br>
   *         given a root %Component to search its subcomponents for the connect_to<br>
   *         Component. 
   */
  public void finalizeConnection(Component root) {
    opensimCommonJNI.AbstractSocket_finalizeConnection(swigCPtr, this, Component.getCPtr(root), root);
  }

  /**
   *  Clear references to connectees. The connectee path property is not<br>
   * affected. Calling finalizeConnection() will use the connectee path<br>
   * property to satisfy the socket. 
   */
  public void disconnect() {
    opensimCommonJNI.AbstractSocket_disconnect(swigCPtr, this);
  }

  /**
   *  %Set connectee path. This function can only be used if this socket is<br>
   * not a list socket. If a connectee reference is set (with connect()) the<br>
   * connectee path is ignored; call disconnect() if you want the socket to be<br>
   * connected using the connectee path.<br>
   * <br>
   * It is preferable to use connect() instead of this function. 
   */
  public void setConnecteePath(String name) {
    opensimCommonJNI.AbstractSocket_setConnecteePath__SWIG_0(swigCPtr, this, name);
  }

  /**
   *  %Set connectee path of a connectee among a list of connectees. This<br>
   * function is used if this socket is a list socket. If a connectee<br>
   * reference is set (with connect()) the connectee path is ignored; call<br>
   * disconnect() if you want the socket to be connected using the connectee<br>
   * name.<br>
   * <br>
   * It is preferable to use connect() instead of this function. 
   */
  public void setConnecteePath(String name, long ix) {
    opensimCommonJNI.AbstractSocket_setConnecteePath__SWIG_1(swigCPtr, this, name, ix);
  }

  /**
   *  Get connectee path. This function can only be used if this socket is<br>
   *     not a list socket.                                                     
   */
  public String getConnecteePath() {
    return opensimCommonJNI.AbstractSocket_getConnecteePath__SWIG_0(swigCPtr, this);
  }

  /**
   *  Get connectee path of a connectee among a list of connectees.         
   */
  public String getConnecteePath(long ix) {
    return opensimCommonJNI.AbstractSocket_getConnecteePath__SWIG_1(swigCPtr, this, ix);
  }

  public void appendConnecteePath(String name) {
    opensimCommonJNI.AbstractSocket_appendConnecteePath(swigCPtr, this, name);
  }

  /**
   *  Clear all connectee paths in the connectee path property. 
   */
  public void clearConnecteePath() {
    opensimCommonJNI.AbstractSocket_clearConnecteePath(swigCPtr, this);
  }

  /**
   *  Get owner component of this socket 
   */
  public Component getOwner() {
    return new Component(opensimCommonJNI.AbstractSocket_getOwner(swigCPtr, this), false);
  }

}
