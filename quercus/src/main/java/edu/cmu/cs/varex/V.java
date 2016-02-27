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


    /**
     * maps over a V describing a (possibly partial) configuration space.
     * all entries have satisfiable conditions.
     * result is another V describing the same (partial) configuration space
     * <p>
     * overloaded to access the condition of each entry if desired
     */
    <U> V<? extends U> map(@Nonnull Function<? super T, ? extends U> fun);

    <U> V<? extends U> map(@Nonnull BiFunction<FeatureExpr, ? super T, ? extends U> fun);

    /**
     * select map: shorthand for x.select(ctx).map(fun)
     * <p>
     * restricts the configuration space by ctx before applying map. removes all
     * entries that are not valid within ctx. result is at most defined in
     * configuration space ctx.
     */
    default <U> V<? extends U> smap(@Nonnull FeatureExpr ctx, @Nonnull Function<? super T, ? extends U> fun) {
        return this.select(ctx).map(fun);
    }

    default <U> V<? extends U> smap(@Nonnull FeatureExpr ctx, @Nonnull BiFunction<FeatureExpr, ? super T, ? extends U> fun) {
        return this.select(ctx).map(fun);
    }

    /**
     * partially map: apply function fun to all values inside a restricted configuration space and apply function altFun
     * to all values outside the restricted configuration space. Overloaded for the common case where
     * altFun is the identify function.
     */
    default <U> V<? extends U> pmap(@Nonnull FeatureExpr ctx, @Nonnull Function<? super T, ? extends U> fun, @Nonnull Function<? super T, ? extends U> altFun) {
        return V.choice(ctx, this.select(ctx).map(fun), this.select(ctx.not()).map(altFun));
    }

    default <U> V<? extends U> pmap(@Nonnull FeatureExpr ctx, @Nonnull BiFunction<FeatureExpr, ? super T, ? extends U> fun, @Nonnull BiFunction<FeatureExpr, ? super T, ? extends U> altFun) {
        return V.choice(ctx, this.select(ctx).map(fun), this.select(ctx.not()).map(altFun));
    }

    default <U> V<? extends U> pmap(@Nonnull FeatureExpr ctx, @Nonnull BiFunction<FeatureExpr, ? super T, ? extends U> fun, @Nonnull Function<? super T, ? extends U> altFun) {
        return V.choice(ctx, this.select(ctx).map(fun), this.select(ctx.not()).map(altFun));
    }

    <U> V<? extends U> flatMap(@Nonnull Function<? super T, V<? extends U>> fun);
    <U> V<? extends U> flatMap(@Nonnull BiFunction<FeatureExpr, ? super T, V<? extends U>> fun);

    /**
     * see smap
     */
    default <U> V<? extends U> sflatMap(@Nonnull FeatureExpr ctx, @Nonnull Function<? super T, V<? extends U>> fun) {
        return this.select(ctx).flatMap(fun);
    }

    default <U> V<? extends U> sflatMap(@Nonnull FeatureExpr ctx, @Nonnull BiFunction<FeatureExpr, ? super T, V<? extends U>> fun) {
        return this.select(ctx).flatMap(fun);
    }

    /**
     * see pmap
     */
    default <U> V<? extends U> pflatMap(@Nonnull FeatureExpr ctx, @Nonnull Function<? super T, V<? extends U>> fun, @Nonnull Function<? super T, V<? extends U>> altFun) {
        return V.choice(ctx, this.select(ctx).flatMap(fun), this.select(ctx.not()).flatMap(altFun));
    }

    default <U> V<? extends U> pflatMap(@Nonnull FeatureExpr ctx, @Nonnull BiFunction<FeatureExpr, ? super T, V<? extends U>> fun, @Nonnull Function<? super T, ? extends U> altFun) {
        return V.choice(ctx, this.select(ctx).flatMap(fun), this.select(ctx.not()).map(altFun));
    }

    default <U> V<? extends U> pflatMap(@Nonnull FeatureExpr ctx, @Nonnull BiFunction<FeatureExpr, ? super T, V<? extends U>> fun, @Nonnull BiFunction<FeatureExpr, ? super T, V<? extends U>> altFun) {
        return V.choice(ctx, this.select(ctx).flatMap(fun), this.select(ctx.not()).flatMap(altFun));
    }

    void foreach(@Nonnull Consumer<T> fun);
    void foreach(@Nonnull BiConsumer<FeatureExpr, T> fun);

    default void sforeach(@Nonnull FeatureExpr ctx, @Nonnull Consumer<T> fun) {
        this.select(ctx).foreach(fun);
    }

    default void sforeach(@Nonnull FeatureExpr ctx, @Nonnull BiConsumer<FeatureExpr, T> fun) {
        this.select(ctx).foreach(fun);
    }

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
