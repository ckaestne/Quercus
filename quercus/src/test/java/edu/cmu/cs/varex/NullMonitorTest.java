package edu.cmu.cs.varex;

import edu.cmu.cs.varex.assertion.NullTestClass;
import org.junit.Test;

/**
 * the tests may fail when executed in the IDE without weaving in the NullMonitor aspect.
 * run tests with sbt
 */
public class NullMonitorTest {

  @Test(expected = NullPointerException.class)
  public void testNullCheckParameter() {
    new NullTestClass().testParameter(null);
  }

  @Test
  public void testNullCheckParameterOk() {
    new NullTestClass().testParameter("foo");
  }

  @Test(expected = NullPointerException.class)
  public void testNullCheckParameterStatic() {
    NullTestClass.testParameterStatic(null);
  }

  @Test
  public void testNullCheckParameterStaticOk() {
    NullTestClass.testParameterStatic("foo");
  }

  @Test(expected = NullPointerException.class)
  public void testNullCheckField() {
    new NullTestClass().testField(null);
  }

  @Test()
  public void testNullableField() {
    new NullTestClass().testFieldNullable(null);
  }


  @Test(expected = NullPointerException.class)
  public void testNullCheckReturn() {
    new NullTestClass().testReturn(null);
  }


  @Test(expected = NullPointerException.class)
  public void testNullCheckReturn2() {
    new NullTestClass().testReturn2(null);
  }

  @Test()
  public void testNullCheckReturnOk() {
    new NullTestClass().testReturn("foo");
  }

  @Test()
  public void testNullCheckReturn2Ok() {
    new NullTestClass().testReturn2("foo");
  }

  @Test(expected = NullPointerException.class)
  public void testNullCheckConstructor() {
    new NullTestClass.Inner(null);
  }

  @Test()
  public void testNullCheckConstructorOk() {
    new NullTestClass.Inner("foo");
  }

}
