package edu.cmu.cs.varex

import com.caucho.quercus.env._
import de.fosd.typechef.featureexpr.FeatureExprFactory
import org.scalatest.{FlatSpec, Matchers}

/**
  * Tests for variations in the PHP object system
  */
class VObjectImplTest extends FlatSpec with Matchers with AbstractPhpTest {

    FeatureExprFactory.setDefault(FeatureExprFactory.bdd)

    val foo = FeatureExprFactory.createDefinedExternal("foo")
    val bar = FeatureExprFactory.createDefinedExternal("bar")
    var t = FeatureExprFactory.True

    val x = StringValue.create("x")
    val y = StringValue.create("y")
    val z = StringValue.create("z")

    "Class" should "support basic mechanisms" in {
        eval("class F{}") to ""
        eval("class F{} $f=new F();") to ""
        eval("class F{ public $x; } $f=new F(); $f->x=2; echo $f->x;") to "2"
        eval("class F{ public $x; } $f=new F(); if (1) $f->x=2; echo $f->x;") to "2"
        eval("class F{ } $f=new F(); $f->y=2; echo $f->y;") to "2"
        eval("$f=new stdClass; $f->y=2; echo $f->y;") to "2"
        eval("$f=new stdClass; $f->y=function(){echo 'x';}; echo call_user_func($f->y);") to "x"
        eval("class F{ function x() { echo 'x'; } } $f=new F(); $f->x();") to "x"
    }

    it should "support the wikipedia example" in {
        eval(
            """
              |class Person
              |{
              |    public $firstName;
              |    public $lastName;
              |
              |    public function __construct($firstName, $lastName = '') { // optional second argument
              |        $this->firstName = $firstName;
              |        $this->lastName  = $lastName;
              |    }
              |
              |    public function greet() {
              |        return 'Hello, my name is ' . $this->firstName .
              |               (($this->lastName != '') ? (' ' . $this->lastName) : '') . '.';
              |    }
              |
              |    public static function staticGreet($firstName, $lastName) {
              |        return 'Hello, my name is ' . $firstName . ' ' . $lastName . '.';
              |    }
              |}
              |
              |$he    = new Person('John', 'Smith');
              |$she   = new Person('Sally', 'Davis');
              |$other = new Person('iAmine');
              |
              |echo $he->greet(); // prints "Hello, my name is John Smith."
              |echo '<br />';
              |
              |echo $she->greet(); // prints "Hello, my name is Sally Davis."
              |echo '<br />';
              |
              |echo $other->greet(); // prints "Hello, my name is iAmine."
              |echo '<br />';
              |
              |echo Person::staticGreet('Jane', 'Doe'); // prints "Hello, my name is Jane Doe."
            """.stripMargin) to "Hello, my name is John Smith.<br />Hello, my name is Sally Davis.<br />Hello, my name is iAmine.<br />Hello, my name is Jane Doe."
    }

    "VClass" should "support variational fields" in {
        eval("class F{ public $x = 0; } $f=new F(); if ($FOO) $f->x=2; echo $f->x;") to c(foo, "2") ~ c(foo.not, "0")

    }

}
