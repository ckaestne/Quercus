package edu.cmu.cs.varex.gen
import de.fosd.typechef.featureexpr.FeatureExprFactory
import org.junit.{Ignore, Test}

/** generated file, do not modify */
class GeneratedArraymoduleTests extends AbstractPhpGenTest {

	@Test def testArray_filter() {
		eval("""<?php 
		       |$a = array('1', '2', '3', 4, 5);
		       |function filter($x) {
		       |  echo "[".$x."]";
		       |  return $x>2;
		       |}
		       |$b = array_filter($a, "filter");
		       |echo implode( ',', $b );""".stripMargin) to 
			c(True, "[1][2][3][4][5]3,4,5")
	}

	@Test def testArray_filter2() {
		eval("""<?php 
		       |$a = array('1', '2', '3', 4, 5);
		       |function filter($x) {
		       |  echo "[".$x."]";
		       |  $x++;
		       |  return True;
		       |}
		       |$b = array_filter($a, "filter");
		       |echo implode( ',', $b );""".stripMargin) to 
			c(True, "[1][2][3][4][5]1,2,3,4,5")
	}

	@Test def testArray_filter3() {
		eval("""<?php 
		       |$a = array('1', '2', '3', 4, 5);
		       |function filter(&$x) {
		       |  echo "[".$x."]";
		       |  $x++;
		       |  return True;
		       |}
		       |$b = array_filter($a, "filter");
		       |echo implode( ',', $b );""".stripMargin) to 
			c(True, "[1][2][3][4][5]2,3,4,5,6")
	}

	@Test def testArray_walk() {
		eval("""<?php 
		       |$fruits = array("d" => "lemon", "a" => "orange", "b" => "banana", "c" => "apple");
		       |function test_alter(&$item1, $key, $prefix){    $item1 = "$prefix: $item1";}
		       |function test_print($item2, $key) { echo "$key. $item2;"; }
		       |echo "Before ...:";
		       |array_walk($fruits, 'test_print');
		       |array_walk($fruits, 'test_alter', 'fruit');
		       |echo "... and after:";
		       |array_walk($fruits, 'test_print');""".stripMargin) to 
			c(True, "Before ...:d. lemon;a. orange;b. banana;c. apple;... and after:d. fruit: lemon;a. fruit: orange;b. fruit: banana;c. fruit: apple;")
	}

	@Test def testArray_walk_recursive() {
		eval("""<?php 
		       |$sweet = array('a' => 'apple', 'b' => 'banana');
		       |$fruits = array('sweet' => $sweet, 'sour' => 'lemon');
		       |
		       |function test_print($item, $key)
		       |{
		       |    echo "$key holds $item;";
		       |}
		       |
		       |array_walk_recursive($fruits, 'test_print');""".stripMargin) to 
			c(True, "a holds apple;b holds banana;sour holds lemon;")
	}

}
