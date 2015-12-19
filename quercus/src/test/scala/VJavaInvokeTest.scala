package edu.cmu.cs.varex

import com.caucho.quercus.env._
import de.fosd.typechef.featureexpr.FeatureExprFactory
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FlatSpec, Matchers}

/**
  * Tests for correct interaction of the base code with modules/libraries
  * written in Java
  */
@RunWith(classOf[JUnitRunner])
class VJavaInvokeTest extends FlatSpec with Matchers with AbstractPhpTest {

    val foo = FeatureExprFactory.createDefinedExternal("foo")
    val bar = FeatureExprFactory.createDefinedExternal("bar")
    var t = FeatureExprFactory.True

    val x = StringValue.create("x")
    val y = StringValue.create("y")
    val z = StringValue.create("z")

    "VModule.vtest_add" should "work without variation" in {
        eval(
            """$a=1;
              |$b=vtest_add($a, 1);
              |echo $a;
              |echo $b;
              | """.stripMargin) to "12"
    }

    it should "work with variation" in {
        eval(
            """$a=1+$FOO;
              |$b=vtest_add($a, 1);
              |echo $a;
              |echo $b;
              | """
                .stripMargin) to c(foo, "23") ~ c(foo.not, "12")
    }

    it should "work with two variations" in {
        eval(
            """$a=1+$FOO;
              |$b=1+$BAR;
              |$b=vtest_add($a, $b);
              |echo $a;
              |echo $b;
              | """
                .stripMargin) to
            c(foo and bar, "24") ~ c(foo.not and bar, "13") ~
                c(foo and bar.not, "23") ~ c(foo.not and bar.not(), "12")
    }

    "VModule.vtest_unlifted" should "work without variation" in {
        eval(
            """$a=1;
              |$b=vtest_unlifted($a, 1);
              |echo $a;
              |echo $b;
              | """.stripMargin) to "12"
    }

    it should "fail with variation" in {
        eval(
            """$a=1+$FOO;
              |$b=vtest_unlifted($a, 1);
              |echo $a;
              |echo $b;
              | """
                .stripMargin) toError()
    }


    "VModule.vtest_addandprint" should "work without variation" in {
        eval(
            """$a=1;
              |$b=vtest_addandprint($a, 1);
              |echo $a;
              |echo $b;
              | """.stripMargin) to "212"
    }

    it should "work with variation" in {
        eval(
            """$a=1+$FOO;
              |$b=vtest_addandprint($a, 1);
              |echo $a;
              |echo $b;
              | """
                .stripMargin) to c(foo, "323") ~ c(foo.not, "212")
    }

    it should "work with variation under condition" in {
        eval(
            """$a=1+$FOO;
              |$b=8;
              |if ($BAR)
              |  $b=vtest_addandprint($a, $b);
              |echo $a;
              |echo $b;
              | """
                .stripMargin) to
            c(foo and bar, "10210") ~ c(foo.not and bar, "919") ~
                c(foo and bar.not, "28") ~ c(foo.not and bar.not(), "18")
    }

}