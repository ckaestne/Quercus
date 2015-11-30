package edu.cmu.cs.varex;

import de.fosd.typechef.featureexpr.FeatureExpr;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.*;

/**
 * central abstraction for conditional/variational values
 *
 * @param <T>
 */
@NonNull
public interface V<T> {

    @Deprecated
    T getOne();
    @Deprecated
    T getOne(FeatureExpr ctx);


    <U> V<? extends U> map(Function<? super T, ? extends U> fun);
    <U> V<? extends U> vmap(FeatureExpr ctx, BiFunction<FeatureExpr, ? super T, ? extends U> fun);

    <U> V<? extends U> flatMap(Function<? super T, V<? extends U>> fun);
    <U> V<? extends U> vflatMap(FeatureExpr ctx, BiFunction<FeatureExpr, ? super T, V<? extends U>> fun);

    void foreach(Consumer<T> fun);
    void vforeach(FeatureExpr ctx, BiConsumer<FeatureExpr, T> fun);

    FeatureExpr when(Predicate<T> condition);

    static <U> V<U> one(U v) {
        return new One(v);
    }
    static <U> V<U> choice(FeatureExpr condition, U a, U b) {
        return VImpl.choice(condition, a, b);
    }
    static <U> V<? extends U> choice(FeatureExpr condition, V<? extends U> a, V<? extends U> b) {
        return VImpl.choice(condition, a, b);
    }
}
