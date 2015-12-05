package edu.cmu.cs.varex

import com.caucho.quercus.env._
import de.fosd.typechef.featureexpr.FeatureExprFactory
import org.scalatest.FlatSpec

/**
  * Created by ckaestne on 11/27/2015.
  */
class VArrayImplTest extends FlatSpec  with AbstractPhpTest{

    FeatureExprFactory.setDefault(FeatureExprFactory.bdd)

    val foo = FeatureExprFactory.createDefinedExternal("foo")
    val bar = FeatureExprFactory.createDefinedExternal("bar")
    var t = FeatureExprFactory.True

    val x = StringValue.create("x")
    val y = StringValue.create("y")

    def eval_a(c:String) = eval("$a = array();"+c+"; foreach ($a as $k=>$v) echo \"$k->$v;\";")

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
        eval_a("if ($FOO) $a[1]=2;") to c(foo,"1->2;")

    }

}
