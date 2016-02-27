package edu.cmu.cs.varex;

import de.fosd.typechef.featureexpr.FeatureExpr;
import de.fosd.typechef.featureexpr.FeatureExprFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * Created by ckaestne on 12/4/2015.
 */
public class VImplTest {

  FeatureExpr foo = FeatureExprFactory.createDefinedExternal("foo");
  FeatureExpr bar = FeatureExprFactory.createDefinedExternal("bar");
  FeatureExpr t = FeatureExprFactory.True();

  V<? extends Integer> c0 = V.one(t, 0);
  V<? extends Integer> c1 = V.one(t, 1);
  V<? extends Integer> c2 = V.one(t, 2);
  V<? extends Integer> c3 = V.one(t, 3);
  V<? extends Integer> c21 = V.choice(foo, c2, c1);
  V<? extends Integer> c32 = V.choice(foo, c3, c2);
  V<? extends Integer> c31 = V.choice(foo, c3, c1);


  @Test
  public void testSimplification() {
    Assert.assertEquals(V.one(t, 1), V.choice(foo, V.one(t, 1), V.one(t, 1)));
    Assert.assertEquals(V.choice(foo, V.one(t, 2), V.one(t, 1)), V.choice(foo, V.choice(foo, V.one(t, 2), V.one(t, 1)), V.one(t, 1)));
  }

  @Test
  public void testFlatMap() {
    Assert.assertEquals(c2, c1.map(k -> k + 1));
    Assert.assertEquals(c2, c1.flatMap(k -> V.one(t, k + 1)));
    Assert.assertEquals(c21, c1.flatMap(k -> V.choice(foo, k + 1, k)));
    Assert.assertEquals(c21, c21.flatMap(k -> V.choice(foo, k, k)));
    Assert.assertEquals(c31, c21.flatMap(k -> V.choice(foo, k + 1, k)));
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
            (c, a, b) -> V.one(t, a + b)
    ));

    Assert.assertEquals(c31, VList.foldRight(
            list(Opt.create(t, 1), Opt.create(foo, 2)), c0, t,
            (c, a, b) -> V.one(t, a + b)
    ));

    Assert.assertEquals(V.choice(foo, V.choice(bar, 4, 3), V.choice(bar, 2, 1)), VList.foldRight(
            list(Opt.create(bar, 1), Opt.create(t, 1), Opt.create(foo, 2)), c0, t,
            (c, a, b) -> V.one(t, a + b)
    ));


  }

  @Test
  public void testVFoldUntil() {

    Assert.assertEquals(c3, VList.foldRightUntil(
            list(Opt.create(t, 1), Opt.create(t, 2)), c0, t,
            (c, a, b) -> V.one(t, a + b)      ,
            x-> x>2
    ));

    Assert.assertEquals(c1, VList.foldRightUntil(
            list(Opt.create(t, 1), Opt.create(foo, 2)), c0, t,
            (c, a, b) -> V.one(t, a + b),
            x-> x>=1
    ));

    Assert.assertEquals(V.choice(foo, 13, 1), VList.foldRightUntil(
            list(Opt.create(t, 1), Opt.create(foo, 2), Opt.create(foo, 10)), c0, t,
            (c, a, b) -> V.one(t, a + b),
            x-> x>1
    ));


    Assert.assertEquals(V.choice(foo, 3, 11), VList.foldRightUntil(
            list(Opt.create(t, 1), Opt.create(foo, 2), Opt.create(foo.not(), 10), Opt.create(t, 100)), c0, t,
            (c, a, b) -> V.one(t, a + b),
            x-> x>1
    ));

  }

  @Test
  public void testConfigSpaces() {
    Assert.assertTrue(V.one(foo, 1).getConfigSpace().equivalentTo(foo));
    Assert.assertTrue(V.choice(bar, V.one(foo, 1), V.one(foo, 2)).getConfigSpace().equivalentTo(foo));
    Assert.assertTrue(V.choice(bar, V.one(foo, 1), V.one(t, 2)).getConfigSpace().equivalentTo(foo.orNot(bar)));
    Assert.assertTrue(V.choice(bar, V.one(t, 1), V.one(t, 2)).getConfigSpace().equivalentTo(t));

    Assert.assertEquals(V.one(foo, 1), V.choice(foo, V.one(t, 1), V.one(t, 2)).select(foo));
    Assert.assertEquals(V.one(foo, 1), V.one(t, 1).select(foo));
    Assert.assertEquals(V.one(foo.and(bar), 1), V.one(t, 1).select(foo).select(foo.and(bar)));
    Assert.assertEquals(VEmpty.instance(), c21.select(FeatureExprFactory.False()));
    Assert.assertEquals(VEmpty.instance(), c21.select(foo).select(FeatureExprFactory.False()));
    Assert.assertEquals(VEmpty.instance(), V.one(t, 1).select(foo).select(FeatureExprFactory.False()));
    Assert.assertEquals("a", V.choice(foo, V.one(t, "a"), V.one(t, "b")).select(foo).getOne());
  }

  @Test
  public void testSMap() {
    Assert.assertEquals(V.one(foo, 2),
            V.choice(foo, 1, 2).smap(foo, p -> p + 1));
    Assert.assertEquals(V.choice(bar, V.one(foo, 2), V.one(foo, 3)),
            V.choice(bar, 1, 2).smap(foo, p -> p + 1));
    Assert.assertEquals(V.one(foo, 2),
            V.one(t, 1).smap(foo, p -> p + 1));
    Assert.assertEquals(V.one(foo, 2),
            V.choice(foo, 1, 2).smap(foo, (c, p) -> {
              assert c.equivalentTo(foo);
              return p + 1;
            }));
    Assert.assertEquals(V.one(foo, 2),
            V.one(t, 1).smap(foo, (c, p) -> {
              assert c.equivalentTo(foo);
              return p + 1;
            }));

    Assert.assertEquals(V.one(foo, 2),
            c1.sflatMap(foo, (c, k) -> V.one(t, k + 1)));
    Assert.assertEquals(V.choice(bar, V.one(foo, 1), V.one(foo, 2)),
            c1.sflatMap(foo, (c, k) -> V.choice(bar, k, k + 1)));
  }

  @Test
  public void testPMap() {
    Assert.assertEquals(V.choice(foo, 3, 2),
            V.choice(foo, 1, 2).pmap(foo, p -> p + 2, p -> p));
    Assert.assertEquals(V.choice(foo, 3, 2),
            V.choice(foo, 1, 2).pmap(foo, (Integer p) -> p + 2, Function.identity()));

    Assert.assertEquals(V.choice(foo, V.choice(bar, 2, 3), V.one(1)),
            c1.pflatMap(foo, v -> V.choice(bar, v + 1, v + 2), v -> V.one(v)));
  }

}
