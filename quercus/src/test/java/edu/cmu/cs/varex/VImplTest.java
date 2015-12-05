package edu.cmu.cs.varex;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ckaestne on 12/4/2015.
 */
public class VImplTest {

  FeatureExpr foo = FeatureExprFactory.createDefinedExternal("foo");
  FeatureExpr bar = FeatureExprFactory.createDefinedExternal("bar");
  FeatureExpr t = FeatureExprFactory.True();

  V<? extends Integer> c0 = V.one(0);
  V<? extends Integer> c1 = V.one(1);
  V<? extends Integer> c2 = V.one(2);
  V<? extends Integer> c3 = V.one(3);
  V<? extends Integer> c21 = V.choice(foo, c2, c1);
  V<? extends Integer> c32 = V.choice(foo, c3, c2);
  V<? extends Integer> c31 = V.choice(foo, c3, c1);


  @Test
  public void testSimplification() {
    Assert.assertEquals(V.one(1), V.choice(foo, V.one(1), V.one(1)));
    Assert.assertEquals(V.choice(foo, V.one(2), V.one(1)), V.choice(foo, V.choice(foo, V.one(2), V.one(1)), V.one(1)));
  }

  @Test
  public void testFlatMap() {
    Assert.assertEquals(c2, c1.map(k -> k + 1));
    Assert.assertEquals(c2, c1.flatMap(k -> V.one(k + 1)));
    Assert.assertEquals(c21, c1.flatMap(k -> V.choice(foo, k + 1, k)));
    Assert.assertEquals(c21, c21.flatMap(k -> V.choice(foo, k, k)));
    Assert.assertEquals(c31, c21.flatMap(k -> V.choice(foo, k + 1, k)));

    Assert.assertEquals(c2, c1.vflatMap(foo, (c, k) -> V.one(k + 1)));
    Assert.assertEquals(c21, c1.vflatMap(foo, (c, k) -> V.choice(foo, k + 1, k)));
  }

  private Iterator<Opt<Integer>> list(Opt<Integer>... elements) {
    List<Opt<Integer>> l = new ArrayList<>();
    for (int i = 0; i < elements.length; i++)
      l.add(elements[i]);
    return l.iterator();
  }

  @Test
  public void testVFold() {

    Assert.assertEquals(c3, VList.foldRight(
            list(Opt.create(t, 1), Opt.create(t, 2)), c0, t,
            (c, a, b) -> V.one(a + b)
    ));

    Assert.assertEquals(c31, VList.foldRight(
            list(Opt.create(t, 1), Opt.create(foo, 2)), c0, t,
            (c, a, b) -> V.one(a + b)
    ));

    Assert.assertEquals(V.choice(foo, V.choice(bar, 4, 3), V.choice(bar, 2, 1)), VList.foldRight(
            list(Opt.create(bar, 1), Opt.create(t, 1), Opt.create(foo, 2)), c0, t,
            (c, a, b) -> V.one(a + b)
    ));


  }

  @Test
  public void testVFoldUntil() {

    Assert.assertEquals(c3, VList.foldRightUntil(
            list(Opt.create(t, 1), Opt.create(t, 2)), c0, t,
            (c, a, b) -> V.one(a + b)      ,
            x-> x>2
    ));

    Assert.assertEquals(c1, VList.foldRightUntil(
            list(Opt.create(t, 1), Opt.create(foo, 2)), c0, t,
            (c, a, b) -> V.one(a + b),
            x-> x>=1
    ));

    Assert.assertEquals(V.choice(foo, 13, 1), VList.foldRightUntil(
            list(Opt.create(t, 1), Opt.create(foo, 2), Opt.create(foo, 10)), c0, t,
            (c, a, b) -> V.one(a + b),
            x-> x>1
    ));


    Assert.assertEquals(V.choice(foo, 3, 11), VList.foldRightUntil(
            list(Opt.create(t, 1), Opt.create(foo, 2), Opt.create(foo.not(), 10), Opt.create(t, 100)), c0, t,
            (c, a, b) -> V.one(a + b),
            x-> x>1
    ));




  }


}
