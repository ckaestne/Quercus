package edu.cmu.cs.varex.gen
import de.fosd.typechef.featureexpr.FeatureExprFactory
import org.junit.{Ignore, Test}

/** generated file, do not modify */
class GeneratedMiscTests extends AbstractPhpGenTest {

	@Test def testBooleans() {
		eval("""<?php 
		       |echo True;
		       |echo ".";
		       |echo TRUE;
		       |echo ".";
		       |echo 1;
		       |echo ".";
		       |echo False;
		       |echo ".";
		       |echo FALSE;
		       |echo ".";
		       |echo 0;
		       |echo ".";
		       |echo @A;""".stripMargin) to 
			c(fA, "1.1.1...0.1") ~
			c(fA.not, "1.1.1...0.")
	}

	@Test def testDo2() {
		eval("""<?php 
		       |$a=5;
		       |if (@A) $a=10;
		       |do { echo $a--; } while ($a>0);""".stripMargin) to 
			c(fA, "10987654321") ~
			c(fA.not, "54321")
	}

	@Test def testDo_break() {
		eval("""<?php 
		       |$a=5;
		       |if (@A) $a=10;
		       |$i=0;
		       |do {
		       |  if ($a==$i) break;
		       |  echo $i++;
		       |} while (1);""".stripMargin) to 
			c(fA, "0123456789") ~
			c(fA.not, "01234")
	}

	@Test def testDo_continue() {
		eval("""<?php 
		       |$a=5;
		       |if (@A) $a=10;
		       |$i=0;
		       |do {
		       |  echo "x".$i++;
		       |  if ($a>$i) continue;
		       |  echo "y".$i++;
		       |  echo "z".$i++;
		       |} while ($i<15);""".stripMargin) to 
			c(fA, "x0x1x2x3x4x5x6x7x8x9y10z11x12y13z14") ~
			c(fA.not, "x0x1x2x3x4y5z6x7y8z9x10y11z12x13y14z15")
	}

	@Test def testIf() {
		eval("""<?php 
		       |$a=@A; 
		       |if($a==0) {	echo "x";} else {	echo "y";}""".stripMargin) to 
			c(fA, "y") ~
			c(fA.not, "x")
	}

	@Test def testIfelseif1() {
		eval("""<?php 
		       |$a=@A+@B+@C; 
		       |
		       |if($a==0) {
		       |	echo "x";
		       |} elseif($a==3) {
		       |	echo "y";
		       |} else {
		       |	echo "z";
		       |}""".stripMargin) to 
			c(fA and fB and fC, "y") ~
			c(fA.not and fB and fC, "z") ~
			c(fB.not and fA and fC, "z") ~
			c(fA.not and fB.not and fC, "z") ~
			c(fC.not and fA and fB, "z") ~
			c(fA.not and fC.not and fB, "z") ~
			c(fB.not and fC.not and fA, "z") ~
			c(fA.not and fB.not and fC.not, "x")
	}

	@Test def testIfelseif2() {
		eval("""<?php 
		       |$a=@A+0;
		       |$b=@B+0;
		       |
		       |if($a==0) {
		       |	echo "x";
		       |} elseif($a==3) {
		       |	echo "xx";
		       |} else {
		       |	if($b==1) {
		       |		echo "z";
		       |	} elseif($b==2){
		       |		echo "yz";
		       |	} else {
		       |		echo "yy";
		       |	}
		       |}""".stripMargin) to 
			c(fA and fB, "z") ~
			c(fA.not and fB, "x") ~
			c(fB.not and fA, "yy") ~
			c(fA.not and fB.not, "x")
	}

	@Ignore
	@Test def testSwitch() {
		eval("""<?php 
		       |$a=@A; 
		       |switch($a) {
		       |	case 0:
		       |		echo "x";	
		       |		break;
		       |	case 1:
		       |		echo "y";
		       |		break;
		       |	default:
		       |		echo "z";
		       |		break;
		       |}""".stripMargin) to 
			c(fA, "y") ~
			c(fA.not, "x")
	}

	@Test def testWhile1() {
		eval("""<?php 
		       |$a=@A+1; 
		       |while ($a<10) {
		       |	echo $a;
		       |	$a++;
		       |}""".stripMargin) to 
			c(fA, "23456789") ~
			c(fA.not, "123456789")
	}

	@Test def testWhile2() {
		eval("""<?php 
		       |$a=5;
		       |if (@A) $a=10;
		       |while ($a>0) echo $a--;""".stripMargin) to 
			c(fA, "10987654321") ~
			c(fA.not, "54321")
	}

	@Test def testWhile_break() {
		eval("""<?php 
		       |$a=5;
		       |if (@A) $a=10;
		       |$i=0;
		       |while (1) {
		       |  if ($a==$i) break;
		       |  echo $i++;
		       |}""".stripMargin) to 
			c(fA, "0123456789") ~
			c(fA.not, "01234")
	}

	@Test def testWhile_continue() {
		eval("""<?php 
		       |$a=5;
		       |if (@A) $a=10;
		       |$i=0;
		       |while ($i<15) {
		       |  echo "x".$i++;
		       |  if ($a>$i) continue;
		       |  echo "y".$i++;
		       |  echo "z".$i++;
		       |}""".stripMargin) to 
			c(fA, "x0x1x2x3x4x5x6x7x8x9y10z11x12y13z14") ~
			c(fA.not, "x0x1x2x3x4y5z6x7y8z9x10y11z12x13y14z15")
	}

}
