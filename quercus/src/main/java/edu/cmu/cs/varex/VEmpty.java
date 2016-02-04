package edu.cmu.cs.varex;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;

import java.util.function.*;


/**
 * represents a V for an empty configuration space without any values
 */
public class VEmpty<T> implements V<T> {

  public static <U> VEmpty<U> instance() {
    return new VEmpty<>();
  }

  @Override
  public String toString() {
    return "VEmpty()";
  }

  @Override
  public T getOne() {
    assert false : "getOne() on empty V";
    return null;
  }

  @Override
  public <U> V<? extends U> map(Function<? super T, ? extends U> fun) {
    return instance();
  }

  @Override
  public <U> V<? extends U> vmap(FeatureExpr ctx, BiFunction<FeatureExpr, ? super T, ? extends U> fun) {
    return instance();
  }

  @Override
  public <U> V<? extends U> flatMap(Function<? super T, V<? extends U>> fun) {
    return instance();
  }

  @Override
  public <U> V<? extends U> vflatMap(FeatureExpr ctx, BiFunction<FeatureExpr, ? super T, V<? extends U>> fun) {
    return instance();
  }

  @Override
  public void foreach(Consumer<T> fun) {
  }

  @Override
  public void vforeach(FeatureExpr ctx, BiConsumer<FeatureExpr, T> fun) {
  }

  @Override
  public FeatureExpr when(Predicate<T> condition) {
    return FeatureExprFactory.False();
  }

  @Override
  public V<T> select(FeatureExpr selectConfigSpace) {
    return this;
  }

  @Override
  public FeatureExpr getConfigSpace() {
    return FeatureExprFactory.False();
  }

  @Override
  public int hashCode() {
    return 17;
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof VEmpty) || super.equals(obj);
  }
}

