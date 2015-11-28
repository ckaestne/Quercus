package edu.cmu.cs.varex;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;

/**
 * Created by ckaestne on 11/27/2015.
 */
public class VHelper {
    public static FeatureExpr noCtx() {
        System.err.println("missing context");
        return FeatureExprFactory.True();
    }

    public static <A, B, C, R> V<R> mapAll(V<A> a, V<B> b, V<C> c, Function4<A, B, C, R> fun) {
        return a.flatMap((aa) ->
                b.flatMap((bb) ->
                        c.map((cc) -> fun.apply(aa, bb, cc))));
    }

    public static <A, B, R> V<R> mapAll(V<A> a, V<B> b, Function3<A, B, R> fun) {
        return a.flatMap((aa) ->
                b.map((bb) -> fun.apply(aa, bb)));
    }

    public static <T> V<T> toV(T v) {
        return V.one(v);
    }
}
