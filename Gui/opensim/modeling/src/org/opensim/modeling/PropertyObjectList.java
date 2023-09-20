/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

/**
 *  A %Property&lt;T&gt; is a serializable (name, list-of-values) pair, where each <br>
 * value is of type T. The number of values allowed in the list is an attribute of<br>
 * the property; often it is just a single value. Properties are owned by classes<br>
 * that derive from %OpenSim's serializable Object base class. The documentation <br>
 * here is most useful for developers who are interested in creating a new <br>
 * Component, ModelComponent, or other serializable class derived from Object.<br>
 * <br>
 * A property's contained type T must be a serializable type. Serializable types <br>
 * come in two flavors:<br>
 *   - simple types (like int or string) for which serialization instructions <br>
 *     have been provided, and<br>
 *   - object types, in which case type T derives from Object and knows<br>
 *     how to serialize itself.<br>
 * <br>
 * When T is a simple type we'll write T=S and refer to a %Property&lt;S&gt; as a <br>
 * "simple property". When T is an object type, we'll write T=O and<br>
 * refer to a %Property&lt;O&gt; as an "object property".<br>
 * <br>
 * In case type O is a still-abstract Object-derived type like Function or <br>
 * Controller, a %Property&lt;O&gt; can hold a mix of any concrete objects derived from <br>
 * O (e.g., any Object that can be dynamic_cast to a Function can be<br>
 * held by a %Property&lt;Function&gt;).<br>
 * <br>
 * The objects in an object property will themselves have<br>
 * properties so a %Property&lt;O&gt; can be viewed as a node in the tree of objects<br>
 * that constitute an %OpenSim Model. Simple properties %Property&lt;S&gt; can be <br>
 * viewed as the terminal nodes of that tree. Properties are thus an integral part<br>
 * of the structure of an %OpenSim Model; anything contained in a property is <br>
 * owned by that property; deleting the property deletes its contained objects. If<br>
 * you want to <i>reference</i> another Object from within a property, use a string <br>
 * property to reference it by name; the result is a simple property. It is not<br>
 * permitted for type T to be a pointer or reference.<br>
 * <br>
 * <h3>XML file representation of properties</h3><br>
 * <br>
 * The general representation for a %Property&lt;T&gt; with name "prop_name" is<br>
 * {@code 
    <prop_name> T T ... T </prop_name>
}<br>
 * where "T" is the XML representation for objects of type T. Note that if T is<br>
 * an object type O, its representation follows the pattern<br>
 * {@code 
    <OTypeName> OContents </OTypeName>
}<br>
 * where <code>OTypeName</code> stands for the name of the concrete, Object-derived class<br>
 * being serialized, and <code>OContents</code> is the representation generated by that<br>
 * class when asked to serialize itself.<br>
 * <br>
 * A %Property&lt;O&gt; that is restricted to holding <em>exactly one</em> object of <br>
 * type O is called a "one-object property". It could be represented in XML as<br>
 * {@code 
    <prop_name> <OTypeName> OContents </OTypeName> </prop_name>
}<br>
 * but we allow a more compact representation for one-object properties:<br>
 * {@code 
    <OTypeName name="prop_name"> OContents </OTypeName>
}<br>
 * In the one-object case it is also permissible for the property to be unnamed, <br>
 * in which case it may be referenced as though its name were the same as the <br>
 * object type name, and there is no separate "name" attribute. The XML <br>
 * representation for an unnamed property is just:<br>
 * {@code 
    <OTypeName> OContents </OTypeName>
}<br>
 * On input, if a name attribute is seen for an unnamed property it is ignored; <br>
 * only the object type name tag matters in the unnamed case. Note that only <br>
 * one-object properties can be unnamed, and no single %OpenSim object can have <br>
 * more than one unnamed property of the same type.<br>
 * <br>
 * <h3>%Property attributes</h3><br>
 * <br>
 * In addition to the name and list of values, every property has the following<br>
 * attributes:<br>
 *   - A comment string, provided at the time the property is created.<br>
 *   - The minimum and maximum number of values allowed.<br>
 *   - A "used default value" flag.<br>
 * <br>
 * The "used default value" flag specifies that the value stored with this <br>
 * property was taken from a default object and not subsequently changed. A <br>
 * property with this flag set is not written out when a model is serialized. <br>
 * <br>
 * <h3>How to declare properties in your class declaration</h3><br>
 * <br>
 * Properties are maintained in a PropertyTable by %OpenSim's Object base class <br>
 * that is used for all serializable objects. Do not create %Property objects <br>
 * directly; instead, use the provided macros to declare them in the class<br>
 * declarations for objects derived from Object. These macros should appear in the <br>
 * header file near the top of your class declaration. Comments that should appear<br>
 * in the generated Doxygen documentation as well as in XML files should be in the<br>
 * comment string; if you have a comment that should appear in Doxygen<br>
 * documentation but not in XML, then you can place it in a Doxygen comment just<br>
 * above the line where you declare your property.<br>
 * <br>
 * <b>Naming conventions:</b> %OpenSim property names should use lower case letters<br>
 * with <code>words_separated_by_underscores</code>. In contrast, %OpenSim object types <br>
 * begin with a capital letter and use camel case, that is, <br>
 * <code>MixedUpperAndLowerLikeThis</code>. This prevents any possible collisions between <br>
 * property names and object types, allowing both to be used as XML tag<br>
 * identifiers with no conflicts.<br>
 * <br>
 * These are the most common forms of property declaration.  Click on the macro<br>
 * names below for more information.<br>
 * {@code 
Exactly one value required; this is the basic property type.
    OpenSim_DECLARE_PROPERTY(name, T, "property description");
Zero or one value only.
    OpenSim_DECLARE_OPTIONAL_PROPERTY(name, T, "property description");
Zero or more values.
    OpenSim_DECLARE_LIST_PROPERTY(name, T, "property description");
}<br>
 * In the above, T may be a simple type S or object type O. In the case of a <br>
 * single-value property where type T is a type derived from<br>
 * Object (i.e., T=O), you can declare the property to be unnamed and instead use <br>
 * the class name of the object type O to identify the property:<br>
 * {@code 
Exactly one value of object type O required.
    OpenSim_DECLARE_UNNAMED_PROPERTY(O, "property description");
}<br>
 * Only one unnamed property of a particular object type O may be declared in<br>
 * any given Object.<br>
 * <br>
 * Finally, for list properties you can declare restrictions on the allowable<br>
 * list length:<br>
 * {@code 
List must contain exactly listSize (> 0) elements.
    OpenSim_DECLARE_LIST_PROPERTY_SIZE(name, T, listSize, 
                                       "property description");
List must contain at least minSize (> 0) elements.
    OpenSim_DECLARE_LIST_PROPERTY_ATLEAST(name, T, minSize, 
                                          "property description");
List must contain at most maxSize (> 0) elements.
    OpenSim_DECLARE_LIST_PROPERTY_ATMOST(name, T, maxSize, 
                                         "property description");
List must contain between minSize (> 0) and maxSize (>minSize) elements.
    OpenSim_DECLARE_LIST_PROPERTY_RANGE(name, T, minSize, maxSize, 
                                        "property description");
}<br>
 * Here is an example of an object declaring two properties: *<br>
 * {@code 
  class ActuatorWorkMeter : public ModelComponent {
  OpenSim_DECLARE_CONCRETE_OBJECT(ActuatorWorkMeter, ModelComponent);
  public:
=======================================================================
PROPERTIES
=======================================================================
      OpenSim_DECLARE_PROPERTY(actuator_name, std::string,
          "The name of the actuator whose work use will be calculated.");
The value for this property is used for reporting purposes. 
      OpenSim_DECLARE_PROPERTY(initial_actuator_work, double,
          "Initial value for work; normally zero.");
=======================================================================
PUBLIC METHODS
=======================================================================
      ...
  };
}<br>
 * <h3>How to construct properties in your constructors</h3><br>
 * <br>
 * The constructors for your Object-derived class are required to construct and<br>
 * initialize the properties to whatever default values you want them to have.<br>
 * The above macros will have generated for each property a method for this<br>
 * purpose. If your property is named <em>prop_name</em>, then the method will be <br>
 * called constructProperty_<em>prop_name</em>(). (In the case of unnamed <br>
 * properties, the object type serves as <em>prop_name</em>.) The initial value is <br>
 * provided as an argument, which is optional for those properties that are <br>
 * allowed to contain a zero-length value list. Here are the various types of <br>
 * generated construction methods:<br>
 * {@code 
Construct and initialize a single-valued property containing type T.
    void constructProperty_prop_name(const T& value);
Construct a property with a zero-length value list. 
    void constructProperty_prop_name();
Construct a list property, initializing from a container.
    template <template <class> class Container>
    void constructProperty_prop_name(const Container<T>& valueList);
}<br>
 * The first form above is generated for basic, optional, and unnamed properties.<br>
 * The second, uninitialized form is generated for optional, unrestricted list,<br>
 * and list "atmost" properties, since those can accept a zero-element value list.<br>
 * The last form is generated for all list properties, regardless of size<br>
 * restriction; a runtime check verifies that size restrictions are met. That <br>
 * form accepts any container type that supports a %size() method and random access<br>
 * element selection with operator[], such as std::vector&lt;T&gt;, <br>
 * OpenSim::Array&lt;T&gt;, or SimTK::Array_&lt;T&gt;.<br>
 * <br>
 * The above methods are conventionally collected into a private method of each<br>
 * object class called <code>constructProperties()</code>. This method is then invoked from<br>
 * every constructor, <i>except</i> the copy constructor (which you normally should<br>
 * let the compiler generate, but see below).<br>
 * <br>
 * &lt;h4&gt;Copy constructor and copy assignment operator&lt;h4&gt;<br>
 * <br>
 * Your best bet is to use the compiler-generated default copy constructor and<br>
 * default copy assignment operator that you get whenever you leave these methods<br>
 * undefined. If you do that, all your properties and their associated local<br>
 * data will be copied automatically. It is worth some effort to design your<br>
 * objects so that their data members can copy and assign themselves correctly;<br>
 * you might find SimTK::ReferencePtr&lt;T&gt; and SimTK::ClonePtr&lt;T&gt; useful for getting<br>
 * pointer members to behave themselves properly.<br>
 * <br>
 * However, if you do have to write your own copy constructor and copy assignment<br>
 * operator (and if you write one you must write the other also), the <br>
 * property table will still have been copied properly by your superclass, it is <br>
 * only the local property indices that you have to deal with. For that, each <br>
 * property has defined a method like:<br>
 * {@code 
Copy the local data member associated with property prop_name.
    void copyProperty_prop_name(const Self& source);
}<br>
 * In the above, <code>Self</code> is the type of the object being defined and <code>source</code> is<br>
 * the argument that was passed to the containing copy constructor or copy <br>
 * assignment operator.<br>
 * <br>
 * <h3>Runtime access to property values</h3><br>
 * <br>
 * The property declaration macros also generate per-property methods for getting<br>
 * access to property values or the %Property objects themselves. These inline<br>
 * methods are very fast and can be used whenever you need access to a property <br>
 * value. The following are generated for single-valued property types, including<br>
 * the basic, optional, and unnamed properties:<br>
 * {@code 
Get a const reference to the value of a single-valued property 
named "prop_name" (basic, optional, unnamed properties only).
    const T& get_prop_name() const;
Same, but returns a writable reference.
    T& upd_prop_name();
Set the value of a single-valued property.
    void set_prop_name(const T& value);
}<br>
 * <br>
 * Additional methods are generated for list properties:<br>
 * {@code 
Get a const reference to the i'th element in a list property's value 
list.
    const T& get_prop_name(int i) const;
Same, but returns a writable reference.
    T& upd_prop_name(int i);
Set the i'th element of a list property to the given value. Only 
allowed if the list currently has at least i elements, so no gaps can
be created with this method.
    void set_prop_name(int i, const T& value);
Use this to append one element to a list property's value list; the
assigned index is returned.
    int append_prop_name(const T& value);
Use this to set all the values of a list-valued property.
    template <template <class> class Container>
    void set_prop_name(const Container<T>& valueList);
}<br>
 * The last form accepts any container that has a %size() method and allows<br>
 * element access using operator[]. Runtime checks verify that the list length<br>
 * is within the allowable range for the property. Note that every property is <br>
 * considered to have a value list (even when restricted to one element) so the <br>
 * indexed forms above can also be used with single-valued properties as long as<br>
 * the index is zero.<br>
 * <br>
 * To get access to the %Property object rather than one of its values, the<br>
 * following methods are provided:<br>
 * {@code 
Get a const reference to the Property<T> object for "prop_name".
    const Property<T>& getProperty_prop_name() const;
Same, but returns a writable reference.
    Property<T>& updProperty_prop_name();
}<br>
 * <br>
 * The %Property&lt;T&gt; class acts as a container of values, and has the usual <br>
 * %size(), %empty(), and operator[] methods available so you can use <br>
 * getProperty...() above to get access to those methods. For example, to write <br>
 * out all the values of any property:<br>
 * {@code 
Assumes type T can be written to a stream with operator<<.
    for (int i=0; i < getProperty_prop_name().size(); ++i)
        std::cout << get_prop_name(i) << std::endl;
}<br>
 * <br>
 * <br>
 * @see OpenSim#Object, OpenSim::AbstractProperty @author Michael Sherman
 */
public class PropertyObjectList extends AbstractProperty {
  private transient long swigCPtr;

  public PropertyObjectList(long cPtr, boolean cMemoryOwn) {
    super(opensimCommonJNI.PropertyObjectList_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  public static long getCPtr(PropertyObjectList obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public static long swigRelease(PropertyObjectList obj) {
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

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        throw new UnsupportedOperationException("C++ destructor does not have public access");
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  /**
   *  Make a new, deep copy (clone) of this concrete property and return<br>
   *     a pointer to the heap space. Caller must delete the returned object when<br>
   *     done with it. *
   */
  public AbstractProperty clone() {
    long cPtr = opensimCommonJNI.PropertyObjectList_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new PropertyObjectList(cPtr, true);
  }

  /**
   *  Use TypeHelper's getTypeName() to satisfy this pure virtual. *
   */
  public String getTypeName() {
    return opensimCommonJNI.PropertyObjectList_getTypeName(swigCPtr, this);
  }

  /**
   *  Replace the i'th value list element with a copy of the given <i>value</i>.<br>
   *     The index i must be between 0 and the current list length, meaning it is<br>
   *     OK to refer one element past the last element. In that case the new <br>
   *     <i>value</i> is appended to the list using appendValue(), which will throw an<br>
   *     exception if the list is already at its maximum allowable size. In the case<br>
   *     where index i refers to an existing element, a simple property <br>
   *     will assign a new value to the existing element but an object property <br>
   *     will delete the old object and replace it with a clone() of the new one <br>
   *     -- it will <i>not</i> invoke the old object's assignment operator. That means<br>
   *     that the concrete object type may be changed by this operation, provided<br>
   *     it is still a type derived from object type T. If you want to invoke the <br>
   *     existing value's assignment operator, use updValue(i) rather than <br>
   *     setValue(i). *
   */
  public void setValue(int i, OpenSimObject value) {
    opensimCommonJNI.PropertyObjectList_setValue__SWIG_0(swigCPtr, this, i, OpenSimObject.getCPtr(value), value);
  }

  /**
   *  Provide a new value for a single-valued<br>
   *     property. The current value (if any) is replaced, and %size()==1 <br>
   *     afterwards. An exception is thrown if this is a list property. *
   */
  public void setValue(OpenSimObject value) {
    opensimCommonJNI.PropertyObjectList_setValue__SWIG_1(swigCPtr, this, OpenSimObject.getCPtr(value), value);
  }

  /**
   *  Return a const reference to the selected value from this property's <br>
   *     value list. If the property is at most single valued then the <i>index</i> is <br>
   *     optional and we'll behave as though index=0 were supplied. You can use<br>
   *     the square bracket operator property[index] instead. *
   */
  public OpenSimObject getValue(int index) {
    return new OpenSimObject(opensimCommonJNI.PropertyObjectList_getValue__SWIG_0(swigCPtr, this, index), false);
  }

  /**
   *  Return a const reference to the selected value from this property's <br>
   *     value list. If the property is at most single valued then the <i>index</i> is <br>
   *     optional and we'll behave as though index=0 were supplied. You can use<br>
   *     the square bracket operator property[index] instead. *
   */
  public OpenSimObject getValue() {
    return new OpenSimObject(opensimCommonJNI.PropertyObjectList_getValue__SWIG_1(swigCPtr, this), false);
  }

  /**
   *  Return a writable reference to the selected value from this property's <br>
   *     value list. If the property is at most single valued then the <i>index</i> is <br>
   *     optional and we'll behave as though index=0 were supplied.  You can use<br>
   *     the square bracket operator property[index] instead. *
   */
  public OpenSimObject updValue(int index) {
    return new OpenSimObject(opensimCommonJNI.PropertyObjectList_updValue__SWIG_0(swigCPtr, this, index), false);
  }

  /**
   *  Return a writable reference to the selected value from this property's <br>
   *     value list. If the property is at most single valued then the <i>index</i> is <br>
   *     optional and we'll behave as though index=0 were supplied.  You can use<br>
   *     the square bracket operator property[index] instead. *
   */
  public OpenSimObject updValue() {
    return new OpenSimObject(opensimCommonJNI.PropertyObjectList_updValue__SWIG_1(swigCPtr, this), false);
  }

  /**
   *  Append a copy of the supplied <i>value</i> to the end of this property's <br>
   *     value list. An exception is thrown if the property can't hold any more <br>
   *     values. The index assigned to this value is returned. *
   */
  public int appendValue(OpenSimObject value) {
    return opensimCommonJNI.PropertyObjectList_appendValue__SWIG_0(swigCPtr, this, OpenSimObject.getCPtr(value), value);
  }

  /**
   *  Add a new value to the end of this property's value list, taking over<br>
   *     ownership of the supplied heap-allocated object. An exception<br>
   *     is thrown if the property can't hold any more values. The index assigned<br>
   *     to this value is returned. *
   */
  public int adoptAndAppendValue(OpenSimObject value) {
    return opensimCommonJNI.PropertyObjectList_adoptAndAppendValue(swigCPtr, this, OpenSimObject.getCPtr(value), value);
  }

  /**
   *  Remove specific entry of the list at index *
   */
  public void removeValueAtIndex(int index) {
    opensimCommonJNI.PropertyObjectList_removeValueAtIndex(swigCPtr, this, index);
  }

  /**
   *  Search the value list for an element that has the given <i>value</i> and<br>
   *     return its index if found, otherwise -1. This requires only that the <br>
   *     template type T supports operator==(). This is a linear search so will <br>
   *     take time proportional to the length of the value list. *
   */
  public int findIndex(OpenSimObject value) {
    return opensimCommonJNI.PropertyObjectList_findIndex(swigCPtr, this, OpenSimObject.getCPtr(value), value);
  }

  /**
   *  Return index of passed in name if the Property contains objects that are<br>
   *     derived from OpenSim::Object, and -1 if no such Object is found. Throws an <br>
   *     Exception if the List doesn't contain OpenSim Objects (e.g. primitive types)<br>
   *     since these are not named. When a search is performed, it's a linear search. 
   */
  public int findIndexForName(SWIGTYPE_p_SimTK__String name) {
    return opensimCommonJNI.PropertyObjectList_findIndexForName(swigCPtr, this, SWIGTYPE_p_SimTK__String.getCPtr(name));
  }

  /**
   *  Return true if the given AbstractProperty references a concrete<br>
   *     property of this type (%Property&lt;T&gt;). Note that for this to return true,<br>
   *     the type T must be exactly the type used when the concrete property was<br>
   *     allocated; it is not sufficient for T to be a more general base type from<br>
   *     which the actual type was derived. *
   */
  public static boolean isA(AbstractProperty prop) {
    return opensimCommonJNI.PropertyObjectList_isA(AbstractProperty.getCPtr(prop), prop);
  }

  /**
   *  Downcast the given AbstractProperty to a concrete<br>
   *     property of this type (%Property&lt;T&gt;). An exception is thrown if<br>
   *     this is not the right type only in DEBUG mode; see isA() if you need to <br>
   *     check first. *
   */
  public static PropertyObjectList getAs(AbstractProperty prop) {
    return new PropertyObjectList(opensimCommonJNI.PropertyObjectList_getAs(AbstractProperty.getCPtr(prop), prop), false);
  }

  /**
   *  Downcast the given AbstractProperty to a writable concrete<br>
   *     property of this type (%Property&lt;T&gt;). An exception is thrown if<br>
   *     this is not the right type only in DEBUG mode; see isA() if you need to <br>
   *     check first. *
   */
  public static PropertyObjectList updAs(AbstractProperty prop) {
    return new PropertyObjectList(opensimCommonJNI.PropertyObjectList_updAs(AbstractProperty.getCPtr(prop), prop), false);
  }

}
