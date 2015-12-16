package edu.cmu.cs.varex

import de.fosd.typechef.featureexpr.FeatureExprFactory
import org.junit.{Ignore, Test}

/**
  * Various tests of variational execution
  */
class VTest extends AbstractPhpTest {

    val foo = FeatureExprFactory.createDefinedExternal("foo")
    val bar = FeatureExprFactory.createDefinedExternal("bar")

    val fooresult1 = c(foo, "2") ~ c(foo.not(), "1")
    val fooresult = c(foo, "1")

    @Test
    def testBasicValues() {
        eval("") to "";

        eval("echo 'foo';") to "foo";


    }

    @Test
    def testCreateV() {
        eval("echo create_conditional('foo');") to c(foo, "1")
        eval("echo 1+create_conditional('foo');") to c(foo, "2") ~ c(foo.not(), "1")
    }

    @Test
    def testCreate2V() {
        eval("echo create_conditional('foo'); echo create_conditional('bar');") to c(foo, "1") ~ c(bar, "1")
        eval("echo 1+create_conditional('foo') + create_conditional('bar');") to c(foo.xor(bar), "2") ~ c(foo.and(bar), "3") ~ c(foo.not().and(bar.not()), "1")
    }

    @Test
    def testVConditionalExpr() {
        eval("echo create_conditional('foo')?'x':'y';") to c(foo, "x") ~ c(foo.not(), "y")
    }

    @Test
    def testVIf() {
        eval("if (create_conditional('foo')) echo 'x'; else echo 'y'; echo 'z';") to c(foo, "x") ~ c(foo.not(), "y") ~ "z"
        eval("if (create_conditional('foo')) echo 'x'; else " +
            "if (create_conditional('bar')) echo 'y'; echo 'z';") to c(foo, "x") ~ c(foo.not().and(bar), "y") ~ "z"
        eval("if (create_conditional('foo')) echo create_conditional('foo');") to c(foo, "1")
        eval("if (create_conditional('foo')) echo 1+create_conditional('bar');") to c(foo.and(bar), "2") ~ c(foo.andNot(bar), "1")
    }

    @Test
    def testVVar() {
        eval("$x = 1+create_conditional('foo'); echo 1+$x;") to c(foo, "3") ~ c(foo.not(), "2")
        eval("$x = 1+create_conditional('foo'); " +
            "$y = create_conditional('bar');" +
            "echo $x + $y;") to c(foo.xor(bar), "2") ~ c(foo.and(bar), "3") ~ c(foo.not().and(bar.not()), "1")
    }

    @Test
    def testVAssignment() {
        eval("if (create_conditional('foo')) $x=1; else $x=2; echo $x;") to c(foo, "1") ~ c(foo.not(), "2")
        eval("$x=2; if (create_conditional('foo')) $x=1; echo $x;") to c(foo, "1") ~ c(foo.not(), "2")
    }

    @Test
    def testVAssignmentByRef() {
        eval("$x = 1; $y = $x; $x=2; echo $x; echo $y;") to "21"
        eval("$x = 1; $y = &$x; $x=2; echo $x; echo $y;") to "22"
        eval("$x = 1; $y = $x; " +
            "if ($FOO) $y=&$x; " +
            "$x = 2; echo $x; echo $y;") to "2" ~ c(foo, "2") ~ c(foo.not(), "1")
        eval("$x = 1; $y = $x; " +
            "if ($FOO) $y=&$x; " +
            "if ($BAR) $x = 2; " +
            "echo $x; echo $y;") to c(bar, "2") ~ c(bar.not(), "1") ~ c(foo and bar, "2") ~ c(foo andNot bar, "1") ~ c(foo.not(), "1")
    }

    @Test
    def testVFunctionDef() {
        eval("if ($FOO) { function fun() { echo 1; }} else { function fun() { echo 2; }} fun();")  to c(foo, "1") ~ c(foo.not(), "2")

    }

    @Test
    def testFunction() {
        val fun = "function fun($p) { echo $p; return $p+1; }; "
        val fun2 = "function fun2($p) { echo $p; return $p+1; }; "

        eval(fun + "echo fun(2);") to "23"
        eval(fun + fun2 + "echo fun(2); echo fun2(3);") to "2334"
        eval(fun + "echo fun(2);" + fun2 + " echo fun2(3);") to "2334"

        eval(fun + "$f = 'fun'; echo $f(2);") to "23"
    }



    @Test
    def testVFunctionCall() {
        val fun = "function fun($p) { echo $p; return $p+1; }; "
        val fun2 = "function fun2($p) { echo $p; return $p+1; }; "

        eval(fun + "if ($FOO) echo fun(2);" + fun2 + " if ($BAR) echo fun2(3);") to c(foo,"23")~c(bar,"34")
    }

    @Test
    def testFunctionVReturn() {
        eval("function fun() { return 1+create_conditional('foo'); }; echo fun();") to c(foo, "2") ~ c(foo.not(), "1")
    }


    @Test
    def testFunctionVParameter() {
        val fun = "function fun($p) { echo $p; return $p+1; }; "
        val x = "$x = 1+$FOO;"

        eval(fun + x + "echo fun($x);") to c(foo, "2") ~ c(foo.not(), "1") ~ c(foo, "3") ~ c(foo.not(), "2")

    }

    @Test
    def testDynamicFunctionCallVTarget() {
        val fun = "function fun($p) { echo $p; return $p+1; }; "
        val fun2 = "function fun2($p) { echo $p+1; return $p+2; }; "

        eval(fun + fun2 + "if ($FOO) $f = 'fun'; else $f = 'fun2'; echo $f(2);") to c(foo,"23") ~ c(foo.not(), "34")
    }


    @Test
    def testVDefaultValue() {
        eval("function foo($var=$FOO) {  echo $var; } foo(1); foo();") to "1"~c(foo,"1")
    }

    @Test
    def testVInc() {
        eval("$i=1; $i++; echo $i;") to "2"
        eval("$i=$FOO; echo ++$i;") to fooresult1
        eval("$i=$FOO; echo $i++; echo $i") to fooresult ~ fooresult1
        eval("$i=$FOO; $i++; echo $i;") to fooresult1
        eval("$i=1; if ($FOO) $i++; echo $i;") to fooresult1
    }

    @Test
    def testCallByReference(): Unit = {
        eval("function foo(&$var) {  $var++; } $a=5; foo($a); echo $a;") to "6"
        eval("function foo(&$var) {  $var++; } $a=$FOO; foo($a); echo $a;") to fooresult1
        eval("function foo(&$var) {  if (create_conditional('bar')) $var++; } $a=$FOO; foo($a); echo $a;") to
         c(foo and bar, "2") ~ c(foo andNot bar, "1") ~ c(foo.not and bar,"1") ~c(foo.not andNot bar,"")
    }

    @Test
    def testArgValue(): Unit = {
        eval("function foo(&$var) {  $var++; } function bar($var) {  $var++; } $a=5; foo($a); echo $a; bar($a); echo $a;") to "66"

    }



}
