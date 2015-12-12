//package edu.cmu.cs.varex.gen
//import de.fosd.typechef.featureexpr.FeatureExprFactory
//import org.junit.{Ignore, Test}
//
///** generated file, do not modify */
//class GeneratedLibTests extends AbstractPhpGenTest {
//
//	@Test def testIsset() {
//		eval("""<?php
//		       |$a = 3+@B;
//		       |echo isset($a);
//		       |echo ".";
//		       |echo isset($b);
//		       |echo ".";
//		       |if (@A)
//		       |  $b=1;
//		       |echo isset($b);""".stripMargin) to
//			c(fA and fB, "1..1") ~
//			c(fA.not and fB, "1..") ~
//			c(fB.not and fA, "1..1") ~
//			c(fA.not and fB.not, "1..")
//	}
