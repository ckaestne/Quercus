package edu.cmu.cs.varex

import com.caucho.quercus.env._
import de.fosd.typechef.featureexpr.FeatureExprFactory
import org.scalatest.{FlatSpec, Matchers}

/**
  * Tests for variational arrays and their internals
  */
class VArrayImplTest extends FlatSpec with Matchers with AbstractPhpTest {

    FeatureExprFactory.setDefault(FeatureExprFactory.bdd)

    val foo = FeatureExprFactory.createDefinedExternal("foo")
    val bar = FeatureExprFactory.createDefinedExternal("bar")
    var t = FeatureExprFactory.bdd.True

    val x = StringValue.create("x")
    val y = StringValue.create("y")
    val z = StringValue.create("z")

    def eval_a(c: String) = eval("$a = array();" + c + "; foreach ($a as $k=>$v) echo \"$k->$v;\";")

    "ArrayValue" should "support basic addition without variation" in {
        eval_a("$a = array();") to ""
        eval_a("$a = array(1=>2);") to "1->2;"
        eval_a("$a[1]=2;") to "1->2;"
        eval_a("$a['a']='b';") to "a->b;"
        eval_a("$a[1]=2;$a[2]=3") to "1->2;2->3;"
        eval_a("$a[]=5") to "0->5;"
        eval_a("$a[]=5;$a[]=1") to "0->5;1->1;"
        eval_a("$a[5]=5;$a[]=1") to "5->5;6->1;"
        eval_a("$a['a']=5;$a[]=1") to "a->5;0->1;"
    }

    "VArray" should "hold conditional elements" in {
        eval_a("if ($FOO) $a[1]=2;") to c(foo, "1->2;")
        eval_a("if ($FOO) $a[1]=2; if ($BAR) $a[2]=3;") to c(foo, "1->2;") ~ c(bar, "2->3;")
        eval_a("if ($FOO) $a[]=2; if ($BAR) $a[]=3;") to c(foo, "0->2;") ~ c(bar andNot foo, "0->3;")  ~ c(bar and foo, "1->3;")
    }

    "VArray" should "manipulate conditional elements" in {
        eval("if (1) $a[]=2; if (1) $a[]=3; echo $a[0]; echo $a[1];") to "23"
        eval("if ($FOO) $a[]=2; if ($BAR) $a[]=3; echo $a[0]; echo $a[1];") to c(foo, "2") ~ c(bar andNot foo, "3")  ~ c(bar and foo, "3")

    }

    "ArrayValueImpl" should "support basic append" in {
        val a = new ArrayValueImpl()
        a.append(t, V.one(x), V.one(y))
        a.get(x).getValue should equal(V.one(y))
    }

    it should "support conditional append" in {
        val a = new ArrayValueImpl()
        a.append(t, V.one(x), V.one(y))
        a.get(x).getValue should equal(V.one(y))
        a.append(foo, V.one(x), V.one(z))
        a.get(x).getValue should equal(V.choice(foo, z, y))
    }

    it should "support conditional append to empty array" in {
        val a = new ArrayValueImpl()
        a.append(foo, V.one(x), V.one(z))
        a.get(x).getValue should equal(V.choice(foo, z, UnsetValue.UNSET))
        a.append(bar, V.one(x), V.one(z))
        a.get(x).getValue should equal(V.choice(foo or bar, z, UnsetValue.UNSET))
    }

    it should "support conditional contains" in {
        val a = new ArrayValueImpl()
        a.append(foo, V.one(x), V.one(z))
        a.contains(V.one(z)) should equal(V.choice(foo, x, NullValue.NULL))

        a.append(foo, V.one(y), V.one(z))
        a.contains(V.one(z)) should equal(V.choice(foo, x, NullValue.NULL))

        a.append(bar, V.one(y), V.one(z))
        a.contains(V.one(z)) should equal(V.choice(foo, V.one(x), V.choice(bar, y, NullValue.NULL)))

        a.append(t, V.one(z), V.one(z))
        a.contains(V.one(z)) should equal(V.choice(foo, V.one(x), V.choice(bar, y, z)))
    }

    it should "support conditional containsKey" in {
        val a = new ArrayValueImpl()
        a.containsKey(x) should equal(V.one(null))

        a.append(foo, V.one(x), V.one(z))
        a.containsKey(x) should equal(V.choice(foo, z, null))

        a.append(bar, V.one(x), V.one(y))
        a.containsKey(x) should equal(V.choice(bar, V.one(y), V.choice(foo, z, null)))
    }

    it should "support conditional lookup with get" in {
        val a = new ArrayValueImpl()
        a.get(x).getValue should equal(V.one(UnsetValue.UNSET))

        a.append(foo, V.one(x), V.one(z))
        a.get(x).getValue should equal(V.choice(foo, z, UnsetValue.UNSET))

        a.append(bar, V.one(x), V.one(y))
        a.get(x).getValue should equal(V.choice(bar, V.one(y), V.choice(foo, z, UnsetValue.UNSET)))
    }

    it should "support conditional append operations" in {
        val a = new ArrayValueImpl()
        a.get(x).getValue should equal(V.one(UnsetValue.UNSET))

        a.put(t, V.one(z))
        a.get(0).getValue should equal(V.one(z))

        a.put(foo, V.one(x))
        a.get(1).getValue should equal(V.choice(foo, x, UnsetValue.UNSET))

        a.put(bar, V.one(y))
        a.get(1).getValue should equal(V.choice(foo, V.one(x), V.choice(bar, y, UnsetValue.UNSET)))
        a.get(2).getValue should equal(V.choice(foo and bar, y, UnsetValue.UNSET))

        a.put(t, V.one(z))
        a.get(1).getValue should equal(V.choice(foo, V.one(x), V.choice(bar, y, z)))
        a.get(2).getValue should equal(V.choice(foo.not andNot bar, V.one(UnsetValue.UNSET), V.choice(foo and bar, y, z)))
        a.get(3).getValue should equal(V.choice(foo and bar, z, UnsetValue.UNSET))


        a.append(t, LongValue.create(5), V.one(z))
        a.get(5).getValue should equal(V.one(z))

        a.put(t, V.one(x))
        a.get(6).getValue should equal(V.one(x))

        a.put(foo, V.one(x))
        a.get(7).getValue should equal(V.choice(foo, x, UnsetValue.UNSET))

        a.append(foo, LongValue.create(10), V.one(z))
        a.get(10).getValue should equal(V.choice(foo, z, UnsetValue.UNSET))

        a.put(t, V.one(x))
        a.get(11).getValue should equal(V.choice(foo, x, UnsetValue.UNSET))
        a.get(7).getValue should equal(V.one(x))
    }

    it should "support conditional remove operations" in {
        val a = new ArrayValueImpl()
        a.remove(t, x) should equal(V.one(UnsetValue.UNSET))

        a.append(t, x, V.one(z))
        a.remove(t, x) should equal(V.one(z))

        a.append(t, x, V.one(z))
        a.remove(foo, x) should equal(V.choice(foo, z, UnsetValue.UNSET))
        a.get(x).getValue should equal(V.choice(foo, UnsetValue.UNSET, z))

        a.clear()
        a.append(foo, x, V.one(y))
        a.append(t, y, V.one(x))
        a.append(bar, x, V.one(z))
        a.remove(t, x) should equal(V.choice(bar, V.one(z), V.choice(foo, y, UnsetValue.UNSET)))

        a.clear()
        a.append(foo, x, V.one(y))
        a.append(t, y, V.one(x))
        a.append(bar, x, V.one(z))
        a.get(x).getValue should equal(V.choice(bar, V.one(z), V.choice(foo, y, UnsetValue.UNSET)))
        a.remove(bar, x) should equal(V.choice(bar, z, UnsetValue.UNSET))
        a.get(x).getValue should equal(V.choice(foo andNot bar, y, UnsetValue.UNSET))
    }

    it should "support basic cursors" in {
        val a = new ArrayValueImpl()
        a.put(t, V.one(x))
        a.put(t, V.one(y))
        a.put(t, V.one(z))
        a.current() should equal(V.one(x))
        a.getHead.getEnvVar.getValue should equal(V.one(x))
        a.next(t) should equal(V.one(y))
        a.next(t) should equal(V.one(z))
        a.key() should equal(V.one(LongValue.create(2)))
        a.next(t) should equal(V.one(BooleanValue.FALSE))
        a.getVTail().getOne().getEnvVar.getValue should equal(V.one(z))
        a.reset(t) should equal(V.one(x))
        a.current() should equal(V.one(x))
        a.next(t) should equal(V.one(y))
        a.prev(t) should equal(V.one(x))
        a.prev(t) should equal(V.one(BooleanValue.FALSE))
        a.end(t) should equal(V.one(z))
        a.put(t, V.one(x))
        a.next(t) should equal(V.one(x))
    }

    it should "support conditional cursor movement" in {
        val a = new ArrayValueImpl()
        a.put(t, V.one(x))
        a.put(t, V.one(y))
        a.put(t, V.one(z))
        a.current() should equal(V.one(x))
        a.next(foo) should equal(V.choice(foo, y, x))
        a.next(t) should equal(V.choice(foo, z, y))
        a.prev(t) should equal(V.choice(foo, y, x))
        a.next(foo) should equal(V.choice(foo, z, x))
        a.next(bar) should equal(V.choice(foo, V.choice(bar, BooleanValue.FALSE, z), V.choice(bar, y, x)))
        a.reset(bar) should equal(V.choice(bar, V.one(x), V.choice(foo, z, x)))
        a.end(foo) should equal(V.choice(bar, V.choice(foo, z, x), V.choice(foo, z, x)))
    }
    it should "support cursor movement on conditional entries" in {
        val a = new ArrayValueImpl()
        a.put(foo, V.one(x))
        a.put(bar, V.one(y))
        a.put(t, V.one(z))
        a.put(foo, V.one(x))
        a.current() should equal(V.choice(foo, V.one(x), V.choice(bar, y, z)))
        a.next(foo) should equal(V.choice(bar, y, z))
        a.end(t) should equal(V.choice(foo, x, z))

    }

}
