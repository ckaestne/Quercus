package edu.cmu.cs.varex.gen
import org.junit.Test

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

	@Test def testForeach1() {
		eval("""<?php 
		       |$a = array("a", "b");
		       |if (@A)
		       |    $a[] = "c";
		       |if (@B && @A)
		       |    $a[] = "d";
		       |$a[] = "e";
		       |foreach ($a as $b)
		       |  echo $b;""".stripMargin) to 
			c(fA and fB, "abcde") ~
			c(fA.not and fB, "abe") ~
			c(fB.not and fA, "abce") ~
			c(fA.not and fB.not, "abe")
	}

	@Test def testForeach2key() {
		eval("""<?php 
		       |$a = array("a", "b");
		       |if (@A)
		       |    $a[] = "c";
		       |if (@B && @A)
		       |    $a[] = "d";
		       |$a[] = "e";
		       |foreach ($a as $k=>$b)
		       |  echo "$k -> $b; ";""".stripMargin) to 
			c(fA and fB, "0 -> a; 1 -> b; 2 -> c; 3 -> d; 4 -> e;") ~
			c(fA.not and fB, "0 -> a; 1 -> b; 2 -> e;") ~
			c(fB.not and fA, "0 -> a; 1 -> b; 2 -> c; 3 -> e;") ~
			c(fA.not and fB.not, "0 -> a; 1 -> b; 2 -> e;")
	}

	@Test def testForeach3refshort() {
		eval("""<?php 
		       |$a = array();
		       |if (@A)
		       |    $a[] = "c";
		       |$a[] = "e";
		       |foreach ($a as $k=>&$x)
		       |  $x = "[$x]";
		       |foreach ($a as $k=>$b)
		       |  echo "$k -> $b; ";""".stripMargin) to 
			c(fA, "0 -> [c]; 1 -> [e];") ~
			c(fA.not, "0 -> [e];")
	}

	@Test def testForeach3ref() {
		eval("""<?php 
		       |$a = array("a", "b");
		       |if (@A)
		       |    $a[] = "c";
		       |if (@B && @A)
		       |    $a[] = "d";
		       |$a[] = "e";
		       |foreach ($a as $k=>&$x)
		       |  $x = "[$x]";
		       |foreach ($a as $k=>$b)
		       |  echo "$k -> $b; ";""".stripMargin) to 
			c(fA and fB, "0 -> [a]; 1 -> [b]; 2 -> [c]; 3 -> [d]; 4 -> [e];") ~
			c(fA.not and fB, "0 -> [a]; 1 -> [b]; 2 -> [e];") ~
			c(fB.not and fA, "0 -> [a]; 1 -> [b]; 2 -> [c]; 3 -> [e];") ~
			c(fA.not and fB.not, "0 -> [a]; 1 -> [b]; 2 -> [e];")
	}

	@Test def testForeach4vref() {
		eval("""<?php 
		       |$a = array("a");
		       |if (@A)
		       |    $a[] = "c";
		       |$a[] = "e";
		       |if (@B)
		       |    foreach ($a as $k=>&$x)
		       |        $x = "[$x]";
		       |foreach ($a as $k=>$b)
		       |    echo "$k -> $b; ";""".stripMargin) to 
			c(fA and fB, "0 -> [a]; 1 -> [c]; 2 -> [e];") ~
			c(fA.not and fB, "0 -> [a]; 1 -> [e];") ~
			c(fB.not and fA, "0 -> a; 1 -> c; 2 -> e;") ~
			c(fA.not and fB.not, "0 -> a; 1 -> e;")
	}

	@Test def testForeach5varray() {
		eval("""<?php 
		       |$a = array("a");
		       |if (@A)
		       |    $a = array("b", "c");
		       |if (@B)
		       |    $a[] = "e";
		       |foreach ($a as $k=>$b)
		       |    echo "$k -> $b; ";""".stripMargin) to 
			c(fA and fB, "0 -> b; 1 -> c; 2 -> e;") ~
			c(fA.not and fB, "0 -> a; 1 -> e;") ~
			c(fB.not and fA, "0 -> b; 1 -> c;") ~
			c(fA.not and fB.not, "0 -> a;")
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

	@Test def testArgvar() {
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

	@Test def testArgvar_dyn() {
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

	@Test def testArggetvalue() {
		eval("""<?php 
		       |$a=array(1=>2);
		       |function foo(&$var) {  $var++; }
		       |function bar($var) {  $var++; }
		       |$fun = "foo";
		       |$fun($a[1]);
		       |echo $a[1];
		       |$fun = "bar";
		       |$fun($a[1]);
		       |echo $a[1];""".stripMargin) to 
			c(True, "33")
	}

	@Test def testVarggetvalue() {
		eval("""<?php 
		       |$a=array(1=>2);
		       |if (@A)
		       |    $a[1] = 3;
		       |function foo(&$var) {  $var++; }
		       |function bar($var) {  $var++; }
		       |$fun = "foo";
		       |$fun($a[1]);
		       |echo $a[1];
		       |$fun = "bar";
		       |$fun($a[1]);
		       |echo $a[1];""".stripMargin) to 
			c(fA, "44") ~
			c(fA.not, "33")
	}

	@Test def testVarggetfieldvalue() {
		eval("""<?php 
		       |class F{
		       |    public $x = 0;
		       |}
		       |$f=new F();
		       |if (@A)
		       |    $f->x=2;
		       |function foo(&$var) {  $var++; }
		       |function bar($var) {  $var++; }
		       |$fun = "foo";
		       |$fun($f->x);
		       |echo $f->x;
		       |$fun = "bar";
		       |$fun($f->x);
		       |echo $f->x;""".stripMargin) to 
			c(fA, "33") ~
			c(fA.not, "11")
	}

	@Test def testVarggetfieldvalue_this() {
		eval("""<?php 
		       |function foo(&$var) {  $var++; }
		       |function bar($var) {  $var++; }
		       |
		       |class F{
		       |    public $x = 0;
		       |    function foo() {
		       |        $fun = "foo";
		       |        $fun($this->x);
		       |        echo $this->x;
		       |        $fun = "bar";
		       |        $fun($this->x);
		       |        echo $this->x;
		       |    }
		       |}
		       |$f=new F();
		       |if (@A)
		       |    $f->x=2;
		       |$f->foo();
		       |echo $f->x;""".stripMargin) to 
			c(fA, "333") ~
			c(fA.not, "111")
	}

	@Test def testGlobalvar() {
		eval("""<?php 
		       |global $x;
		       |$x = 1;
		       |function foo() {
		       |  global $x;
		       |  echo $x;
		       |  if (@A)
		       |    $x++;
		       |}
		       |function bar() {
		       |  global $x;
		       |  echo $x;
		       |  $x++;
		       |}
		       |echo $x;
		       |foo();
		       |echo $x;
		       |bar();
		       |echo $x;""".stripMargin) to 
			c(fA, "11223") ~
			c(fA.not, "11112")
	}

	@Test def testGlobalvar2() {
		eval("""<?php 
		       |$GLOBALS['x'] = 1;
		       |function foo() {
		       |  global $x;
		       |  echo $x;
		       |  if (@A)
		       |    $x++;
		       |}
		       |function bar() {
		       |  global $x;
		       |  echo $x;
		       |  $x++;
		       |}
		       |foo();
		       |bar();""".stripMargin) to 
			c(fA, "12") ~
			c(fA.not, "11")
	}

	@Test def testGlobalvar3() {
		eval("""<?php 
		       |$GLOBALS['x'] = 1;
		       |function foo() {
		       |  echo $GLOBALS['x'];
		       |  if (@A)
		       |    $GLOBALS['x']++;
		       |}
		       |function bar() {
		       |  echo $GLOBALS['x'];
		       |  $GLOBALS['x']++;
		       |}
		       |foo();
		       |bar();""".stripMargin) to 
			c(fA, "12") ~
			c(fA.not, "11")
	}

	@Test def testFunction_exists() {
		eval("""<?php 
		       |function foo() { echo "x"; }
		       |echo function_exists("foo");
		       |echo function_exists("bar");
		       |foo();""".stripMargin) to 
			c(True, "1x")
	}

	@Test def testVar_vs_val_parameter() {
		eval("""<?php 
		       |function foo($a, $b)
		       |{
		       |    $b = 20;
		       |	var_dump($a);
		       |	var_dump($b);
		       |}
		       |foo(1, 2);""".stripMargin) to 
			c(True, "int(1)\nint(20)")
	}

	@Test def testPassbyreference() {
		eval("""<?php 
		       |function f($arg1, &$arg2)
		       |{
		       |	echo $arg1++;
		       |	echo $arg2++;
		       |}
		       |
		       |function g (&$arg1, &$arg2)
		       |{
		       |	echo $arg2;
		       |}
		       |$a = 7;
		       |$b = 15;
		       |
		       |f($a, $b);
		       |
		       |echo $a;
		       |echo $b;
		       |
		       |$c=array(1);
		       |g($c,$c[0]);
		       |
		       |echo $c[0];""".stripMargin) to 
			c(True, "71571611")
	}

	@Test def testStringtemplates() {
		eval("""<?php 
		       |$a = 1;
		       |$b = "f$a";
		       |echo $b;""".stripMargin) to 
			c(True, "f1")
	}

	@Test def testVstringtemplates() {
		eval("""<?php 
		       |$a = 1;
		       |if (@A) $a=2;
		       |$b = "f$a";
		       |echo $b;""".stripMargin) to 
			c(fA, "f2") ~
			c(fA.not, "f1")
	}

	@Test def testArrayvar() {
		eval("""<?php 
		       |$a = array(1, 2, 5);
		       |$b = &$a;
		       |$a[] = 10;
		       |echo implode("-",$a);
		       |echo implode("-",$b);""".stripMargin) to 
			c(True, "1-2-5-101-2-5-10")
	}

	@Test def testArrayvar2() {
		eval("""<?php 
		       |$a = array(1, 2, 5);
		       |$b = $a;
		       |$a[] = 10;
		       |echo implode("-",$a);
		       |echo implode("-",$b);""".stripMargin) to 
			c(True, "1-2-5-101-2-5")
	}

	@Test def testArrayinit() {
		eval("""<?php 
		       |function __($a) { return "..".$a; }
		       |$defaults = array(
		       |		'show_option_all' => '', 'show_option_none' => __('No categories'),
		       |		'hierarchical' => true, 'title_li' => __( 'Categories' ),
		       |		'echo' => 1
		       |	);
		       |foreach ($defaults as $k => $v)
		       |  echo $k."->".$v."; ";""".stripMargin) to 
			c(True, "show_option_all->; show_option_none->..No categories; hierarchical->1; title_li->..Categories; echo->1;")
	}

	@Test def testCall_user_func_array_var() {
		eval("""<?php 
		       |class X {
		       |        function foo(&$v, $a) {
		       |                $v = $a;
		       |        }
		       |}
		       |$x = new X();
		       |$v = "x";
		       |$a = array( &$v, "y");
		       |print_r($a);
		       |call_user_func_array(array($x, "foo"), $a);
		       |print_r($a);""".stripMargin) to 
			c(True, "Array\n(\n    [0] => x\n    [1] => y\n)\nArray\n(\n    [0] => y\n    [1] => y\n)")
	}

	@Test def testArray_merge_call_user_func_array_var() {
		eval("""<?php 
		       |class X {
		       |        function foo(&$v, $a) {
		       |                $v = $a;
		       |        }
		       |}
		       |$x = new X();
		       |$v = "x";
		       |$vv = array(1, 2);
		       |$a = array_merge(array( &$v, "y"), $vv);
		       |print_r($a);
		       |call_user_func_array(array($x, "foo"), $a);
		       |print_r($a);""".stripMargin) to 
			c(True, "Array\n(\n    [0] => x\n    [1] => y\n    [2] => 1\n    [3] => 2\n)\nArray\n(\n    [0] => y\n    [1] => y\n    [2] => 1\n    [3] => 2\n)")
	}

	@Test def testCall_user_func_array_var2() {
		eval("""<?php 
		       |class X {
		       |        function foo(&$v, $a) {
		       |                $v = $a;
		       |        }
		       |        function bar($a, &$v) {
		       |                $args = array(&$v, $a);
		       |                call_user_func_array(array($this, "foo"), $args);
		       |        }
		       |}
		       |$x = new X();
		       |$v = "x";
		       |$a = array( "y", &$v);
		       |print_r($a);
		       |call_user_func_array(array($x, "bar"), $a);
		       |print_r($a);""".stripMargin) to 
			c(True, "Array\n(\n    [0] => y\n    [1] => x\n)\nArray\n(\n    [0] => y\n    [1] => y\n)")
	}

	@Test def testCall_user_func_array_var3() {
		eval("""<?php 
		       |class X {
		       |        function foo(&$v, $a, $b) {
		       |                $v = $a;
		       |                echo $b;
		       |        }
		       |        function bar($a, &$v, $b) {
		       |                $args = array_merge(array(&$v, $a), $b);
		       |                call_user_func_array(array($this, "foo"), $args);
		       |        }
		       |}
		       |$x = new X();
		       |$v = "x";
		       |$b = array(5);
		       |$a = array( "y", &$v, $b);
		       |print_r($a);
		       |call_user_func_array(array($x, "bar"), $a);
		       |print_r($a);""".stripMargin) to 
			c(True, "Array\n(\n    [0] => y\n    [1] => x\n    [2] => Array\n        (\n            [0] => 5\n        )\n\n)\n5Array\n(\n    [0] => y\n    [1] => y\n    [2] => Array\n        (\n            [0] => 5\n        )\n\n)")
	}

	@Test def testCallbacks_phpdoc() {
		eval("""<?php 
		       |function my_callback_function() {
		       |    echo 'hello1world!';
		       |}
		       |class MyClass {
		       |    static function myCallbackMethod() {
		       |        echo 'Hello2World!';
		       |    }
		       |}
		       |call_user_func('my_callback_function');
		       |call_user_func(array('MyClass', 'myCallbackMethod'));
		       |$obj = new MyClass();
		       |call_user_func(array($obj, 'myCallbackMethod'));
		       |call_user_func('MyClass::myCallbackMethod');
		       |class A {
		       |    public static function who() {
		       |        echo "A\n";
		       |    }
		       |}
		       |class B extends A {
		       |    public static function who() {
		       |        echo "B\n";
		       |    }
		       |}
		       |call_user_func(array('B', 'who'));
		       |//TODO not supported in quercus:
		       |//call_user_func(array('B', 'parent::who'));
		       |class C {
		       |    public function __invoke($name) {
		       |        echo 'Hello3', $name, "\n";
		       |    }
		       |}
		       |$c = new C();
		       |call_user_func($c, 'PHP!');""".stripMargin) to 
			c(True, "hello1world!Hello2World!Hello2World!Hello2World!B\nHello3PHP!")
	}

	@Test def testNamespaces() {
		eval("""<?php 
		       |namespace my\name; // see "Defining Namespaces" section
		       |class MyClass {
		       |    function foo() { echo "A;"; }
		       |}
		       |function myfunction() {}
		       |const MYCONST = 1;
		       |$a = new MyClass;
		       |echo $a->foo().".";
		       |$c = new \my\name\MyClass; // see "Global Space" section
		       |echo $c->foo().".";
		       |$a = strlen('hi');
		       |echo $a.".";
		       |//$d = namespace\MYCONST;
		       |//echo $d.".";
		       |$d = __NAMESPACE__ . '\MYCONST';
		       |//echo $d.".";
		       |echo constant($d);""".stripMargin) to 
			c(True, "A;.A;.2.1")
	}

	@Test def testCondAssignByRef() {
		eval("""<?php 
		       |$x = 1;
		       |if (@A) {
		       |  $a = 2;
		       |  $x = &$a;
		       |  $a = 3;
		       |  echo $x;
		       |}
		       |if (@B) {
		       |  $a = 4;
		       |  $x = &$a;
		       |  $a = 5;
		       |  if (@A)
		       |    $a = 6;
		       |  echo $x;
		       |}
		       |echo $x;""".stripMargin) to 
			c(fA and fB, "366") ~
			c(fA.not and fB, "55") ~
			c(fB.not and fA, "33") ~
			c(fA.not and fB.not, "1")
	}

}
