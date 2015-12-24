package edu.cmu.cs.varex.gen
import de.fosd.typechef.featureexpr.FeatureExprFactory
import org.junit.{Ignore, Test}

/** generated file, do not modify */
class GeneratedLibTests extends AbstractPhpGenTest {

	@Test def testIsset() {
		eval("""<?php 
		       |$a = 3+@B;
		       |echo isset($a);
		       |echo ".";
		       |echo isset($b);
		       |echo ".";
		       |if (@A)
		       |  $b=1;
		       |echo isset($b);""".stripMargin) to 
			c(fA and fB, "1..1") ~
			c(fA.not and fB, "1..") ~
			c(fB.not and fA, "1..1") ~
			c(fA.not and fB.not, "1..")
	}

	@Test def testDefine() {
		eval("""<?php 
		       |define("CONSTANT", "x");
		       |echo CONSTANT;""".stripMargin) to 
			c(True, "x")
	}

	@Test def testVdefine() {
		eval("""<?php 
		       |//define("CONSTANT", "x".(@A+1));
		       |//echo CONSTANT;""".stripMargin) to 
			c(fA, "") ~
			c(fA.not, "")
	}

	@Test def testVvdefine() {
		eval("""<?php 
		       |//if (@B) {
		       |//  define("X", 2);
		       |//  echo X;
		       |//}
		       |
		       |//echo defined("X");""".stripMargin) to 
			c(fB, "") ~
			c(fB.not, "")
	}

	@Test def testDebug_backtrace() {
		eval("""<?php 
		       |function printStack($s) {
		       |  foreach ($s as $nr => $row) {
		       |    echo $nr.":";
		       |    echo $row["function"];
		       |    echo "\n";
		       |  }
		       |}
		       |function foo() { bar(); }
		       |function bar() {
		       |  $a = debug_backtrace();
		       |  printStack($a);
		       |}
		       |foo();""".stripMargin) to 
			c(True, "0:bar\n1:foo")
	}

}
