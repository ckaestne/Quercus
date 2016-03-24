package edu.cmu.cs.varex;

import com.caucho.quercus.env.*;
import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;
import edu.cmu.cs.varex.annotation.VDeprecated;

import javax.annotation.Nonnull;
import java.lang.reflect.Array;
import java.util.Iterator;
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

  public static FeatureExpr False() {
    return FeatureExprFactory.False();
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

  public static <A, B, R> V<? extends R> smapAll(FeatureExpr ctx, V<? extends A> a, V<? extends B> b, Function4<FeatureExpr, A, B, R> fun) {
    return a.sflatMap(ctx, (c, aa) ->
            b.smap(c, (cc, bb) -> fun.apply(cc, aa, bb)));
  }

  public static <A, B, R> V<? extends R> smapAll(FeatureExpr ctx, V<? extends A> a, V<? extends B> b, Function3<A, B, R> fun) {
    return a.sflatMap(ctx, (c, aa) ->
            b.smap(c, bb -> fun.apply(aa, bb)));
  }


  public static <A, B, C, R> V<? extends R> smapAll(FeatureExpr ctx, V<? extends A> a, V<? extends B> b, V<? extends C> c, Function5<FeatureExpr, A, B, C, R> fun) {
    return a.<R>sflatMap(ctx, (cctx, aa) ->
            b.<R>sflatMap(cctx, (ccctx, bb) ->
                    c.<R>smap(ccctx, (cccctx, cc) -> fun.apply(cccctx, aa, bb, cc))));
  }

  public static <A, B, R> V<? extends R> sflatMapAll(FeatureExpr ctx, V<? extends A> a, V<? extends B> b, Function4<FeatureExpr, A, B, V<? extends R>> fun) {
    return a.sflatMap(ctx, (c, aa) ->
            b.sflatMap(c, (cc, bb) -> fun.apply(cc, aa, bb)));
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
  @Nonnull
  public static <T>  V<? extends T> vifTry(Supplier<V<? extends T>> solution1,
                                                   Supplier<V<? extends T>> solution2,
                                                   Predicate<T> predicate) {
    @Nonnull V<? extends T> result = solution1.get();
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

  public static V<? extends Value> getValues(@Nonnull V<? extends ValueOrVar> v) {
    return v.flatMap(a -> a == null ? V.one(null) : (a.isVar() ? a._var()._getValues() : V.one(a._value())));
  }
  @Deprecated //use when further investigation is required
  public static V<? extends Value> _getValues(@Nonnull V<? extends ValueOrVar> v) {
    return getValues(v);
  }

  @Deprecated//don't use permanently
  public static Var[] valArrayToVar(Value[] args) {
    return VHelper.mapArray(Var.class, args, a->Var.create(a));
  }

  public static <T> boolean vequal(V<? extends T> a, V<? extends T> b) {
    return smapAll(True(), a, b, (c, aa, bb) -> c.isContradiction() || aa.equals(bb)).when(x -> x).isTautology();
  }

  public static void assertTrue(FeatureExpr ctx) {
    if (!ctx.isTautology())
      throw new UnimplementedVException("incomplete V transformation, cannot be called in context " + ctx);
  }

  public static void assertNonVariational(ArrayValue array) {
    Iterator<VEntry> it = array.getIterator();
    while (it.hasNext()) {
      assertTrue(it.next().getCondition());
    }
  }

  public static FeatureExpr vToFExpr(V<? extends FeatureExpr> v) {
    FeatureExpr result = FeatureExprFactory.False();
    for (Opt<FeatureExpr> o : VList.flatten(v))
      result = result.or(o.getCondition().and(o.getValue()));
    return result;
  }

  @VDeprecated
  @Deprecated
  public static boolean isTrue(FeatureExpr c) {
    if (c.isTautology()) return true;
    if (c.isContradiction()) return false;
    throw new UnimplementedVException("incomplete V transformation, cannot translate " + c + " to boolean");
  }


  public static V<? extends Boolean> fexprToVBoolean(V<? extends FeatureExpr> v) {
    return V.choice(vToFExpr(v), V.one(true), V.one(false));
  }

  /**
   * equivalent of a conditional if statement that executes both branches
   * under corresponding contexts (if satisfiable) depending on a variational
   * boolean value
   */
  public static <T> V<? extends T> vif(FeatureExpr ctx, V<? extends Boolean> condition, Function<FeatureExpr, V<? extends T>> thenSupplier, Function<FeatureExpr, V<? extends T>> elseSupplier) {
    return vif(ctx, condition.when(b -> b), thenSupplier, elseSupplier);
  }

  public static <T> V<? extends T> vif(FeatureExpr ctx, FeatureExpr condition, Function<FeatureExpr, V<? extends T>> thenSupplier, Function<FeatureExpr, V<? extends T>> elseSupplier) {
    if (ctx.and(condition).isContradiction())
      return elseSupplier.apply(ctx);
    else if (ctx.andNot(condition).isContradiction())
      return thenSupplier.apply(ctx);
    return V.choice(condition,
            thenSupplier.apply(ctx.and(condition)),
            elseSupplier.apply(ctx.andNot(condition))
    );
  }

}
