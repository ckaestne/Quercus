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

    public static <A,B,C,R> V<R> mapAll(V<A> a, V<B> b, V<C> c, Function4<A, B, C, R> fun) {
        System.err.println("missing implementation");
        return toV(fun.apply(a.getOne(),b.getOne(),c.getOne()));
    }

    public static <A,B,R> V<R> mapAll(V<A> a, V<B> b, Function3<A, B, R> fun) {
        System.err.println("missing implementation");
        return toV(fun.apply(a.getOne(),b.getOne()));
    }

    public static <T> V<T> toV(T v) {
        System.err.println("missing implementation");
        return new One(v);
    }
}
