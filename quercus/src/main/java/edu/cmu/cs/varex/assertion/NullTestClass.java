package edu.cmu.cs.varex.assertion;

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

  public void testParameter(@Nullable Object o) {
    testParameterFun(o);
  }

  private void testParameterFun(@Nonnull Object o) {
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


}
