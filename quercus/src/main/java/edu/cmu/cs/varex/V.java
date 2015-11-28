package edu.cmu.cs.varex;

import de.fosd.typechef.featureexpr.FeatureExpr;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * central abstraction for conditional/variational values
 *
 * @param <T>
 */
public interface V<T> {

    @Deprecated
    T getOne();


    <U> V<U> map(Function<T, U> fun);

    <U> V<U> flatMap(Function<T, V<U>> fun);

    void foreach(Consumer<T> fun);

    static <U> V<U> one(U v) {
        return new One(v);
    }
    static <U> V<U> choice(FeatureExpr condition, U a, U b) {
        return VImpl.choice(condition, a, b);
    }
    static <U> V<U> choice(FeatureExpr condition, V<U> a, V<U> b) {
        return VImpl.choice(condition, a, b);
    }
}
