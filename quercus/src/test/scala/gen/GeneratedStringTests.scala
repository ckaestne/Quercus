package edu.cmu.cs.varex.gen
import de.fosd.typechef.featureexpr.FeatureExprFactory
import org.junit.{Ignore, Test}

/** generated file, do not modify */
class GeneratedStringTests extends AbstractPhpGenTest {

	@Test def testImplode() {
		eval("""<?php 
		       |$a = array('1', '2', '3');
		       |echo implode( ',', $a );""".stripMargin) to 
			c(True, "1,2,3")
	}

	@Test def testStr_replace() {
		eval("""<?php 
		       |$a = "foo";
		       |$count = 1;
		       |$b = str_replace("o", "a", $a, $count);
		       |echo "$b - $count;";
		       |$b = str_replace("o", "a", $b, $count);
		       |echo "$b - $count;";""".stripMargin) to 
			c(True, "faa - 2;faa - 0;")
	}

}
