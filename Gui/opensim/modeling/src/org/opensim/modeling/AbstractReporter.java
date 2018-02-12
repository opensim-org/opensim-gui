/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.opensim.modeling;

public class AbstractReporter extends Component {
  private transient long swigCPtr;

  protected AbstractReporter(long cPtr, boolean cMemoryOwn) {
    super(opensimCommonJNI.AbstractReporter_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(AbstractReporter obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
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

  public static AbstractReporter safeDownCast(OpenSimObject obj) {
    long cPtr = opensimCommonJNI.AbstractReporter_safeDownCast(OpenSimObject.getCPtr(obj), obj);
    return (cPtr == 0) ? null : new AbstractReporter(cPtr, false);
  }

  public void assign(OpenSimObject aObject) {
    opensimCommonJNI.AbstractReporter_assign(swigCPtr, this, OpenSimObject.getCPtr(aObject), aObject);
  }

  public static String getClassName() {
    return opensimCommonJNI.AbstractReporter_getClassName();
  }

  public OpenSimObject clone() {
    long cPtr = opensimCommonJNI.AbstractReporter_clone(swigCPtr, this);
    return (cPtr == 0) ? null : new AbstractReporter(cPtr, true);
  }

  public String getConcreteClassName() {
    return opensimCommonJNI.AbstractReporter_getConcreteClassName(swigCPtr, this);
  }

  public void copyProperty_report_time_interval(AbstractReporter source) {
    opensimCommonJNI.AbstractReporter_copyProperty_report_time_interval(swigCPtr, this, AbstractReporter.getCPtr(source), source);
  }

  public double get_report_time_interval(int i) {
    return opensimCommonJNI.AbstractReporter_get_report_time_interval__SWIG_0(swigCPtr, this, i);
  }

  public SWIGTYPE_p_double upd_report_time_interval(int i) {
    return new SWIGTYPE_p_double(opensimCommonJNI.AbstractReporter_upd_report_time_interval__SWIG_0(swigCPtr, this, i), false);
  }

  public void set_report_time_interval(int i, double value) {
    opensimCommonJNI.AbstractReporter_set_report_time_interval__SWIG_0(swigCPtr, this, i, value);
  }

  public int append_report_time_interval(double value) {
    return opensimCommonJNI.AbstractReporter_append_report_time_interval(swigCPtr, this, value);
  }

  public void constructProperty_report_time_interval(double initValue) {
    opensimCommonJNI.AbstractReporter_constructProperty_report_time_interval(swigCPtr, this, initValue);
  }

  public double get_report_time_interval() {
    return opensimCommonJNI.AbstractReporter_get_report_time_interval__SWIG_1(swigCPtr, this);
  }

  public SWIGTYPE_p_double upd_report_time_interval() {
    return new SWIGTYPE_p_double(opensimCommonJNI.AbstractReporter_upd_report_time_interval__SWIG_1(swigCPtr, this), false);
  }

  public void set_report_time_interval(double value) {
    opensimCommonJNI.AbstractReporter_set_report_time_interval__SWIG_1(swigCPtr, this, value);
  }

  public void report(State s) {
    opensimCommonJNI.AbstractReporter_report(swigCPtr, this, State.getCPtr(s), s);
  }

}
