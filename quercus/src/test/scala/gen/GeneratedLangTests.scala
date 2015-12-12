package edu.cmu.cs.varex.gen
import de.fosd.typechef.featureexpr.FeatureExprFactory
import org.junit.{Ignore, Test}

/** generated file, do not modify */
class GeneratedLangTests extends AbstractPhpGenTest {

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

	@Test def testAddition() {
		eval("""<?php 
		       |$x=@A+1;
		       |echo $x;""".stripMargin) to 
			c(fA, "2") ~
			c(fA.not, "1")
	}

	@Test def testIf() {
		eval("""<?php 
		       |$a=@A; 
		       |if($a==0) {	echo "x";} else {	echo "y";}""".stripMargin) to 
			c(fA, "y") ~
			c(fA.not, "x")
	}

	@Test def testSwitch() {
		eval("""<?php 
		       |$a=0+@A; 
		       |if (@B) $a++;
		       |switch($a) {
		       |	case 0:
		       |		echo "x";	
		       |		break;
		       |	case 0:
		       |		echo "a";	
		       |		break;
		       |	case 1:
		       |		echo "y";
		       |	case 1:
		       |		echo "b";
		       |		break;
		       |	default:
		       |		echo "z";
		       |		break;
		       |}""".stripMargin) to 
			c(fA and fB, "z") ~
			c(fA.not and fB, "yb") ~
			c(fB.not and fA, "yb") ~
			c(fA.not and fB.not, "x")
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

	@Test def testFor1() {
		eval("""<?php 
		       |$a=2+@A;
		       |if (@B) $a++;
		       |for ($i=$a;$i>0;$i--)
		       |  echo $i;""".stripMargin) to 
			c(fA and fB, "4321") ~
			c(fA.not and fB, "321") ~
			c(fB.not and fA, "321") ~
			c(fA.not and fB.not, "21")
	}

	@Test def testFor2() {
		eval("""<?php 
		       |$a=2+@A;
		       |$inc=1+@B;
		       |for ($i=$a;$i<10;$i=$i+$inc)
		       |  echo $i;""".stripMargin) to 
			c(fA and fB, "3579") ~
			c(fA.not and fB, "2468") ~
			c(fB.not and fA, "3456789") ~
			c(fA.not and fB.not, "23456789")
	}

	@Test def testFor3() {
		eval("""<?php 
		       |$a=0+@A;
		       |$up=5+@B;
		       |for ($i=$a;$i<$up;$i++)
		       |  echo $i;""".stripMargin) to 
			c(fA and fB, "12345") ~
			c(fA.not and fB, "012345") ~
			c(fB.not and fA, "1234") ~
			c(fA.not and fB.not, "01234")
	}

	@Test def testReturnRef() {
		eval("""<?php 
		       |class foo {
		       |    public $value = 42;
		       |
		       |    public function &getValue() {
		       |        return $this->value;
		       |    }
		       |}
		       |
		       |$obj = new foo;
		       |$myValue = &$obj->getValue(); // $myValue is a reference to $obj->value, which is 42.
		       |echo $myValue;
		       |$obj->value = 2;
		       |echo $myValue;                // prints the new value of $obj->value, i.e. 2.""".stripMargin) to 
			c(True, "422")
	}

	@Test def testClass1() {
		eval("""<?php 
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

	@Test def testVstringconcat() {
		eval("""<?php 
		       |$a="x".@A;
		       |echo $a;""".stripMargin) to 
			c(fA, "x1") ~
			c(fA.not, "x")
	}

	@Test def testVstringconcat2() {
		eval("""<?php 
		       |$a="x";
		       |$b="y";
		       |$c=$a.$b;
		       |echo $c;
		       |$d=$c.@A;
		       |$e=$d.(1+@B);
		       |echo $e;""".stripMargin) to 
			c(fA and fB, "xyxy12") ~
			c(fA.not and fB, "xyxy2") ~
			c(fB.not and fA, "xyxy11") ~
			c(fA.not and fB.not, "xyxy1")
	}

	@Test def testArgvalue() {
		eval("""<?php 
		       |function foo(&$var) {  $var++; }
		       |function bar($var) {  $var++; }
		       |$a=5;
		       |foo($a);
		       |echo $a;
		       |bar($a);
		       |echo $a;""".stripMargin) to 
			c(True, "66")
	}

	@Test def testArgvalue_dyn() {
		eval("""<?php 
		       |function foo(&$var) {  $var++; }
		       |function bar($var) {  $var++; }
		       |$fun = "foo";
		       |$a=5;
		       |$fun($a);
		       |echo $a;
		       |$fun = "bar";
		       |$fun($a);
		       |echo $a;""".stripMargin) to 
			c(True, "66")
	}

}
