package edu.cmu.cs.varex;

import com.caucho.quercus.env.Value;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Array;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by ckaestne on 11/27/2015.
 */
public class VHelper {

  public static FeatureExpr True() {
    return FeatureExprFactory.True();
  }

  @Deprecated//use only as placeholder, use True instead if intentionally general context intended
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

  public static <A, B, R> V<? extends R> vmapAll(FeatureExpr ctx, V<? extends A> a, V<? extends B> b, Function4<FeatureExpr, A, B, R> fun) {
    return a.vflatMap(ctx, (c, aa) ->
            b.vmap(c, (cc, bb) -> fun.apply(cc, aa, bb)));
  }

  public static <A, B, R> V<? extends R> vflatMapAll(FeatureExpr ctx, V<? extends A> a, V<? extends B> b, Function4<FeatureExpr, A, B, V<? extends R>> fun) {
    return a.vflatMap(ctx, (c, aa) ->
            b.vflatMap(c, (cc, bb) -> fun.apply(cc, aa, bb)));
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
  public static <T> @NonNull V<? extends T> vifTry(Supplier<@NonNull V<? extends T>> solution1,
                                                   Supplier<@NonNull V<? extends T>> solution2,
                                                   Predicate<T> predicate) {
    @NonNull V<? extends T> result = solution1.get();
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

    public static Value[] arrayToOne(V<? extends Value>[] a) {
        return mapArray(Value.class, a, x->x.getOne());
    }
    public static <T, U> U[] mapArray(Class<U> klass, T[] a, Function<T, U> f) {
        U[] result = (U[]) Array.newInstance(klass, a.length);
        for (int i=0; i<a.length;i++)
            result[i]=f.apply(a[i]);
        return result;


    }
    public static <T, U> V<? extends T> [] mapVArray(V<? extends U>[] a, Function<U, T> f) {
        return mapArray(V.class, a, x->x.map(y->f.apply(y)));
    }


    public static <T> V<? extends T>[] toVArray(T[] a) {
        return mapArray(V.class, a, x->V.one(x));
    }


}
