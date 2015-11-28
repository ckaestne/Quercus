package edu.cmu.cs.varex;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;

import java.util.*;
import java.util.function.*;

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

    static <U> V<? extends U> choice(FeatureExpr condition, V<? extends U> a, V<? extends U> b) {
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
        assert checkInvariant(): "invariants violated";
    }

    //invariant: nonempty, all FeatureExpr together yield true
    private final Map<FeatureExpr, T> values;

    private boolean checkInvariant() {
        if (values.isEmpty()) return false;// : "empty V";
        FeatureExpr conditions = FeatureExprFactory.False();
        for (FeatureExpr cond : values.keySet()) {
            if (!conditions.and(cond).isContradiction()) return false;// : "condition overlaps with previous condition";
            conditions = conditions.or(cond);
        }
        if (!conditions.isTautology()) return false;// : "conditions together not a tautology";

        return true;
    }


    @Override
    public T getOne() {
        assert false : "getOne called on Choice: " + this;
        return values.values().iterator().next();
    }

    @Override
    public <U> V<? extends U> map(Function<? super T, ? extends U> fun) {
        Map<FeatureExpr, U> result = new HashMap<>(values.size());
        for (HashMap.Entry<FeatureExpr, T> e : values.entrySet())
            result.put(e.getKey(), fun.apply(e.getValue()));
        return new VImpl<>(result);
    }

    @Override
    public <U> V<? extends U> vmap(FeatureExpr ctx, BiFunction<FeatureExpr, ? super T, ? extends U> fun) {
        Map<FeatureExpr, U> result = new HashMap<>(values.size());
        for (HashMap.Entry<FeatureExpr, T> e : values.entrySet())
            result.put(e.getKey(), fun.apply(ctx.and(e.getKey()), e.getValue()));
        return new VImpl<>(result);
    }

    @Override
    public <U> V<? extends U> flatMap(Function<? super T, V<? extends U>> fun) {
        Map<FeatureExpr, U> result = new HashMap<>(values.size());
        for (HashMap.Entry<FeatureExpr, T> e : values.entrySet()) {
            V<? extends U> u = fun.apply(e.getValue());
            addVToMap(result, e.getKey(), u);
        }
        return new VImpl<>(result);
    }

    @Override
    public <U> V<? extends U> vflatMap(FeatureExpr ctx, BiFunction<FeatureExpr, ? super T, V<? extends U>> fun) {
        Map<FeatureExpr, U> result = new HashMap<>(values.size());
        for (HashMap.Entry<FeatureExpr, T> e : values.entrySet()) {
            V<? extends U> u = fun.apply(ctx.and(e.getKey()), e.getValue());
            addVToMap(result, e.getKey(), u);
        }
        return new VImpl<>(result);
    }

    private static <U> void addVToMap(Map<FeatureExpr, U> result, FeatureExpr ctx, V<? extends U> u) {
        assert (u instanceof One) || (u instanceof VImpl) : "unexpected V value: " + u;
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

    @Override
    public void vforeach(FeatureExpr ctx, BiConsumer<FeatureExpr, T> fun) {
        for (HashMap.Entry<FeatureExpr, T> e : values.entrySet())
            fun.accept(ctx.and(e.getKey()), e.getValue());
    }

    @Override
    public FeatureExpr when(Predicate<T> condition) {
        FeatureExpr result = FeatureExprFactory.False();
        for (HashMap.Entry<FeatureExpr, T> e : values.entrySet())
            if (condition.test(e.getValue()))
                result = result.or(e.getKey());
        return result;
    }

    @Override
    public String toString() {
        StringBuffer out = new StringBuffer();
        List<String> entries = new ArrayList<>(values.size());
        for (HashMap.Entry<FeatureExpr, T> e : values.entrySet())
            entries.add(e.getValue() + "<-" + e.getKey().toTextExpr());
        Collections.sort(entries);
        out.append("CHOICE(");
        for (String e : entries) {
            out.append(e);
            out.append("; ");
        }
        out.delete(out.length() - 2, out.length());
        out.append(")");


        return out.toString();
    }
}


