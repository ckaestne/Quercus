package edu.cmu.cs.varex.assertion;

import edu.cmu.cs.varex.V;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by ckaestne on 12/5/2015.
 */
public class NullTestClass {

  @Nonnull
  private Object field;
  @Nullable
  private Object fieldN;

  public V<? extends Object> vfield;
  @Nullable
  public V<? extends Object> vfieldn;

  public void testParameter(@Nullable Object o) {
    testParameterFun(o);
  }

  private void testParameterFun(@Nonnull Object o) {
    System.out.println(o);
  }

  public static void testParameterStatic(@Nonnull Object o) {
    System.out.println(o);
  }

  public void testField(@Nullable Object o) {
    this.field = o;
  }
  public void testFieldNullable(@Nullable Object o) {
    this.fieldN = o;
  }

  @Nonnull
  public Object testReturn(@Nullable Object o) {
    return o;
  }


  public @Nonnull Object testReturn2(@Nullable Object o) {
    return o;
  }

  public V<? extends Object> testVReturn(@Nullable V<? extends Object> o) { return null; }

  public V<? extends Object> testVReturnNullable(@Nullable V<? extends Object> o) { return V.one(o); }

  public V<? extends Object> testVReturn2(@Nonnull V<? extends Object> o) { return V.one(o); }

  public void testVFieldN(@Nullable V<?> o) {
    vfieldn=o;
  }
  public void testVField(@Nullable V<?> o) {
    vfield=o;
  }
  public static class Inner {
    public Inner(@Nonnull Object o) {
    }
  }
}
