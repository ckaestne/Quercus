package edu.cmu.cs.varex

import com.caucho.quercus.env.{EnvVarImpl, StringValue, Value, Var}
import de.fosd.typechef.featureexpr.FeatureExprFactory
import org.junit.Assert._
import org.junit.Test

import scala.compat.java8.FunctionConverters._

/**
  * Created by ckaestne on 11/27/2015.
  */
class VEnvVarImplTest extends AbstractPhpTest {

    val foo = FeatureExprFactory.createDefinedExternal("foo")
    val bar = FeatureExprFactory.createDefinedExternal("bar")
    var t = FeatureExprFactory.True

    @Test def testEnvVar(): Unit = {

        val v = new Var()
        val x = StringValue.create("x")
        val y = StringValue.create("y")
        v.set(x)
        //e and f are variables
        val e = new EnvVarImpl(V.one(v))
        val f = new EnvVarImpl(V.one(new Var()))
        f.setRef(t, e.getVar(t))

        println(e.get(t))

        assert(e.getVar(t) == V.one(v))
        assert(e.get(t) == V.one(x))
        assert(f.get(t) == V.one(x))
        assert(e.get(t).when(asJavaPredicate((e: Value) => e.toString() == "x")).isTautology())


        //assign to new value
        e.set(t, V.one(y))
        println(e.get(t))

        assert(e.getVar(t) == V.one(v))
        assert(e.get(t) != V.one(x))
        assert(e.get(t) == V.one(y))
        assert(f.get(t) == V.one(y))
        assert(e.get(t).when(asJavaPredicate((e: Value) => e.toString() == "y")).isTautology())

        //assign to new var
        val v2 = new Var()
        v2.set(x)
        e.setVar(t, V.one(v2))

        assertEquals(V.one(v2), e.getVar(t))
        assert(e.getVar(t) != V.one(v))
        assert(e.get(t) == V.one(x))
        //keep old value
        assert(f.get(t) == V.one(y))

    }

    @Test def testVEnvVar(): Unit = {

        val v = new Var()
        val x = StringValue.create("x")
        val y = StringValue.create("y")
        v.set(x)
        val e = new EnvVarImpl(V.one(v))

        e.set(foo, V.one(y))

        println(e.get(t))

        assertEquals(V.one(y), e.getVar(foo))
        assertEquals(V.one(x), e.getVar(foo.not()))


    }

}
