package edu.cmu.cs.varex;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;

import java.util.function.Predicate;
import java.util.function.Supplier;

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

    public static <A, B, R> V<? extends R> flatMapAll(V<? extends A> a, V<? extends B> b, Function3<A, B, V<? extends R>> fun) {
        return a.flatMap((aa) ->
                b.flatMap((bb) -> fun.apply(aa, bb)));
    }


    public static <T> V<T> toV(T v) {
        return V.one(v);
    }

    public static <T> V<T> nonNull(V<T> t) {
        assert(t!=null);
        return t;
    }

  /**
   * get a value from producer1, producer2, or producer3
   * until predicate X is fulfilled (first solution is used)
   *
   * roughly
   *   x=solution1
   *   if (!predicate())
   *     x=solution2
   *   if (!predicate())
   *     x=solution3
   *   return x;
   *
   */
  public static <T> V<? extends T> vifTry(Supplier<V<? extends T>> solution1,
                                          Supplier<V<? extends T>> solution2,
                                          Predicate<T> predicate) {
    V<? extends T> result = solution1.get();
    return result.flatMap(r1 ->
            predicate.test(r1) ?
                    V.one(r1) :
                    solution2.get()
    );

  }

  public static <T> V<? extends T> vifTry3(Supplier<V<? extends T>> solution1,
                                           Supplier<V<? extends T>> solution2,
                                           Supplier<V<? extends T>> solution3,
                                           Predicate<T> predicate) {
    return vifTry(() -> vifTry(solution1, solution2, predicate), solution3, predicate);
  }

  }
