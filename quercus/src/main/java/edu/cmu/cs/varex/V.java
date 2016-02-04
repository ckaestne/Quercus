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
    default T getOne(@Nonnull FeatureExpr ctx) { return select(ctx).getOne(); }


    <U> V<? extends U> map(@Nonnull Function<? super T, ? extends U> fun);
    <U> V<? extends U> vmap(@Nonnull FeatureExpr ctx, @Nonnull BiFunction<FeatureExpr, ? super T, ? extends U> fun);

    <U> V<? extends U> flatMap(@Nonnull Function<? super T, V<? extends U>> fun);
    <U> V<? extends U> vflatMap(@Nonnull FeatureExpr ctx, @Nonnull BiFunction<FeatureExpr, ? super T, V<? extends U>> fun);

    void foreach(@Nonnull Consumer<T> fun);
    void vforeach(@Nonnull FeatureExpr ctx, @Nonnull BiConsumer<FeatureExpr, T> fun);

    FeatureExpr when(@Nonnull Predicate<T> condition);

    V<T> select(FeatureExpr configSpace);

    FeatureExpr getConfigSpace();

    @Deprecated
    static <U> V<U> one(@Nullable U v) {
        return one(VHelper.True(), v);
    }

    static <U> V<U> one(FeatureExpr configSpace, @Nullable U v) {
        return new One(configSpace, v);
    }

    static <U> V<? extends U> choice(@Nonnull FeatureExpr condition, @Nullable U a, @Nullable U b) {
        if (condition.isContradiction())
            return one(b);
        else if (condition.isTautology())
            return one(a);
        else
            return VImpl.choice(condition, a, b);
    }

    static <U> V<? extends U> choice(@Nonnull FeatureExpr condition, Supplier<U> a, Supplier<U> b) {
        if (condition.isContradiction())
            return one(b.get());
        else if (condition.isTautology())
            return one(a.get());
        else
            return VImpl.choice(condition, a.get(), b.get());
    }
    static <U> V<? extends U> choice(@Nonnull FeatureExpr condition, @Nonnull V<? extends U> a, @Nonnull V<? extends U> b) {
        if (condition.isContradiction())
            return b;
        else if (condition.isTautology())
            return a;
        else
            return VImpl.choice(condition, a, b);
    }

}
