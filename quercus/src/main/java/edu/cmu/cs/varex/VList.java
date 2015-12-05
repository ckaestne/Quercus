package edu.cmu.cs.varex;

import de.fosd.typechef.featureexpr.FeatureExpr;

import java.util.Iterator;

/**
 * helper functions for lists with optional entries
 */
public class VList {

  /**
   * conditional fold over all entries in this array
   * <p>
   * feature expression of the op function already includes the current context
   * of the entry
   */
  public static <T, E> V<? extends T> foldRight(Iterator<Opt<E>> list, V<? extends T> init, FeatureExpr ctx, Function4<FeatureExpr, E, T, V<? extends T>> op) {
    V<? extends T> result = init;

    while (list.hasNext()) {
      final Opt<E> current = list.next();

      result = result.vflatMap(ctx, (c, r) ->
              V.choice(current.getCondition(),
                      op.apply(c.and(current.getCondition()), current.getValue(), r),
                      V.one(r)));
    }

    return result;
  }

}
