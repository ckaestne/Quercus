package edu.cmu.cs.varex.gen
import de.fosd.typechef.featureexpr.FeatureExprFactory
import org.junit.{Ignore, Test}

/** generated file, do not modify */
class GeneratedOutputbufferTests extends AbstractPhpGenTest {

	@Test def testBasic() {
		eval("""<?php 
		       |ob_start();
		       |echo "foo";
		       |ob_end_flush();
		       |echo "bar";""".stripMargin) to 
			c(True, "foobar")
	}

	@Test def testBasic2() {
		eval("""<?php 
		       |ob_start();
		       |echo "foo";
		       |ob_end_clean();
		       |echo "bar";""".stripMargin) to 
			c(True, "bar")
	}

	@Test def testBasic3() {
		eval("""<?php 
		       |ob_start();
		       |echo "foo";""".stripMargin) to 
			c(True, "foo")
	}

	@Test def testVbasic() {
		eval("""<?php 
		       |ob_start();
		       |if (@A)
		       |    echo "foo";
		       |ob_end_flush();
		       |echo "bar";""".stripMargin) to 
			c(fA, "foobar") ~
			c(fA.not, "bar")
	}

	@Test def testFlushorclean() {
		eval("""<?php 
		       |//ob_start();
		       |//echo "foo";
		       |//if (@A)
		       |//    ob_end_flush();
		       |//else
		       |//    ob_end_clean();
		       |//echo "bar";""".stripMargin) to 
			c(fA, "") ~
			c(fA.not, "")
	}

}
