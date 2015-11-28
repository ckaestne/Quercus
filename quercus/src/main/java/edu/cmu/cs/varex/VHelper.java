package edu.cmu.cs.varex;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;

/**
 * Created by ckaestne on 11/27/2015.
 */
public class VHelper {
    public static FeatureExpr noCtx() {
//        System.err.println("missing context");
        return FeatureExprFactory.True();
    }

    public static <A, B, C, R> V<? extends R> mapAll(V<? extends A> a, V<? extends B> b, V<? extends C> c, Function4<A, B, C, R> fun) {
        return a.flatMap((aa) ->
                b.flatMap((bb) ->
                        c.<R>map((cc) -> fun.apply(aa, bb, cc))));
    }

    public static <A, B, R> V<? extends R> mapAll(V<? extends A> a, V<? extends B> b, Function3<A, B, R> fun) {
        return a.flatMap((aa) ->
                b.map((bb) -> fun.apply(aa, bb)));
    }

    public static <T> V<T> toV(T v) {
        return V.one(v);
    }

    public static <T> V<T> nonNull(V<T> t) {
        assert(t!=null);
        return t;
    }

}
