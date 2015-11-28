package edu.cmu.cs.varex;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * internal implementation of V
 */
class VImpl<T> implements V<T> {

    static <U> V<U> choice(FeatureExpr condition, U a, U b) {
        Map<FeatureExpr, U> result = new HashMap<>(2);
        if (condition.isSatisfiable())
            result.put(condition, a);
        else return V.one(b);
        if (condition.not().isSatisfiable())
            result.put(condition.not(), b);
        else return V.one(a);

        return new VImpl<>(result);
    }

    static <U> V<U> choice(FeatureExpr condition, V<U> a, V<U> b) {
        Map<FeatureExpr, U> result = new HashMap<>(2);
        if (condition.isSatisfiable())
            addVToMap(result, condition, a);
        else return b;
        if (condition.not().isSatisfiable())
            addVToMap(result, condition.not(), a);
        else return a;

        return new VImpl<>(result);
    }

    private VImpl(Map<FeatureExpr, T> values) {
        this.values = values;
        checkInvariant();
    }

    //invariant: nonempty, all FeatureExpr together yield true
    private final Map<FeatureExpr, T> values;

    private boolean checkInvariant() {
        assert !values.isEmpty() : "empty V";
        FeatureExpr conditions = FeatureExprFactory.False();
        for (FeatureExpr cond : values.keySet()) {
            assert conditions.and(cond).isContradiction() : "condition overlaps with previous condition";
            conditions = conditions.or(cond);
        }
        assert conditions.isTautology() : "conditions together not a tautology";

        return true;
    }


    @Override
    public T getOne() {
        checkInvariant();
        return values.values().iterator().next();
    }

    @Override
    public <U> V<U> map(Function<T, U> fun) {
        Map<FeatureExpr, U> result = new HashMap<>(values.size());
        for (HashMap.Entry<FeatureExpr, T> e : values.entrySet())
            result.put(e.getKey(), fun.apply(e.getValue()));
        return new VImpl<>(result);
    }

    @Override
    public <U> V<U> flatMap(Function<T, V<U>> fun) {
        Map<FeatureExpr, U> result = new HashMap<>(values.size());
        for (HashMap.Entry<FeatureExpr, T> e : values.entrySet()) {
            V<U> u = fun.apply(e.getValue());
            addVToMap(result, e.getKey(), u);
        }
        return new VImpl<>(result);
    }

    private static <U> void addVToMap(Map<FeatureExpr, U> result, FeatureExpr ctx, V<U> u) {
        assert (u instanceof One) || (u instanceof VImpl);
        if (u instanceof One)
            result.put(ctx, ((One<U>) u).value);
        else
            for (HashMap.Entry<FeatureExpr, U> ee : ((VImpl<U>) u).values.entrySet()) {
                FeatureExpr cond = ctx.and(ee.getKey());
                if (cond.isSatisfiable())
                    result.put(cond, ee.getValue());
            }
    }

    @Override
    public void foreach(Consumer<T> fun) {
        values.values().forEach(fun::accept);

    }


}


