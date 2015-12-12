package edu.cmu.cs.varex.gen
import de.fosd.typechef.featureexpr.FeatureExprFactory
import org.junit.{Ignore, Test}

/** generated file, do not modify */
class GeneratedBasicTests extends AbstractPhpGenTest {

	@Test def testClass1() {
		eval("""<?php
		       |
		       |class F{
		       |    public $x = 0;
		       |}
		       |$f=new F();
		       |if (@A)
		       |    $f->x=2;
		       |if (@B)
		       |    echo $f->x++;
		       |if (@C)
		       |    echo ++$f->x;
		       |echo $f->x;""".stripMargin) to 
			c(fA and fB and fC, "244") ~
			c(fA.not and fB and fC, "022") ~
			c(fB.not and fA and fC, "33") ~
			c(fA.not and fB.not and fC, "11") ~
			c(fC.not and fA and fB, "23") ~
			c(fA.not and fC.not and fB, "01") ~
			c(fB.not and fC.not and fA, "2") ~
			c(fA.not and fB.not and fC.not, "0")
	}

	@Test def testSimple() {
		eval("""<head>
		       |<?php
		       |$x=@A+1;
		       |echo $x;
		       |?>
		       |</head>""".stripMargin) to 
			c(fA, "<head>\n2</head>") ~
			c(fA.not, "<head>\n1</head>")
	}

}
