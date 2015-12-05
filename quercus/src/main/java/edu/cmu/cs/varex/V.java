package edu.cmu.cs.varex;

import de.fosd.typechef.featureexpr.FeatureExpr;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.function.*;

/**
 * central abstraction for conditional/variational values
 *
 * @param <T>
 */
@Nonnull
public interface V<T> {

    @Deprecated
    T getOne();
    @Deprecated
    T getOne(@Nonnull FeatureExpr ctx);


    <U> V<? extends U> map(@Nonnull Function<? super T, ? extends U> fun);
    <U> V<? extends U> vmap(@Nonnull FeatureExpr ctx, @Nonnull BiFunction<FeatureExpr, ? super T, ? extends U> fun);

    <U> V<? extends U> flatMap(@Nonnull Function<? super T, V<? extends U>> fun);
    <U> V<? extends U> vflatMap(@Nonnull FeatureExpr ctx, @Nonnull BiFunction<FeatureExpr, ? super T, V<? extends U>> fun);

    void foreach(@Nonnull Consumer<T> fun);
    void vforeach(@Nonnull FeatureExpr ctx, @Nonnull BiConsumer<FeatureExpr, T> fun);

    FeatureExpr when(@Nonnull Predicate<T> condition);

    static <U> V<U> one(@Nullable U v) {
        return new One(v);
    }
    static <U> V<? extends U> choice(@Nonnull FeatureExpr condition, @Nullable U a, @Nullable U b) {
        return VImpl.choice(condition, a, b);
    }
    static <U> V<? extends U> choice(@Nonnull FeatureExpr condition, @Nonnull V<? extends U> a, @Nonnull V<? extends U> b) {
        return VImpl.choice(condition, a, b);
    }

}
