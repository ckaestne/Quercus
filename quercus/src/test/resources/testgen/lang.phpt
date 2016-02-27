==== booleans
echo True;
echo ".";
echo TRUE;
echo ".";
echo 1;
echo ".";
echo False;
echo ".";
echo FALSE;
echo ".";
echo 0;
echo ".";
echo @A;

==== addition
$x=@A+1;
echo $x;

==== if
$a=@A; 
if($a==0) {	echo "x";} else {	echo "y";}

==== switch
$a=0+@A; 
if (@B) $a++;
switch($a) {
	case 0:
		echo "x";	
		break;
	case 0:
		echo "a";	
		break;
	case 1:
		echo "y";
	case 1:
		echo "b";
		break;
	default:
		echo "z";
		break;
}
==== while1
$a=@A+1; 
while ($a<10) {
	echo $a;
	$a++;
}
==== ifelseif1
$a=@A+@B+@C; 

if($a==0) {
	echo "x";
} elseif($a==3) {
	echo "y";
} else {
	echo "z";
}
==== ifelseif2
$a=@A+0;
$b=@B+0;

if($a==0) {
	echo "x";
} elseif($a==3) {
	echo "xx";
} else {
	if($b==1) {
		echo "z";
	} elseif($b==2){
		echo "yz";
	} else {
		echo "yy";
	}
}
==== while2
$a=5;
if (@A) $a=10;
while ($a>0) echo $a--;
==== while_break
$a=5;
if (@A) $a=10;
$i=0;
while (1) {
  if ($a==$i) break;
  echo $i++;
}
==== while_continue
$a=5;
if (@A) $a=10;
$i=0;
while ($i<15) {
  echo "x".$i++;
  if ($a>$i) continue;
  echo "y".$i++;
  echo "z".$i++;
}
==== do2
$a=5;
if (@A) $a=10;
do { echo $a--; } while ($a>0);
==== do_break
$a=5;
if (@A) $a=10;
$i=0;
do {
  if ($a==$i) break;
  echo $i++;
} while (1);
==== do_continue
$a=5;
if (@A) $a=10;
$i=0;
do {
  echo "x".$i++;
  if ($a>$i) continue;
  echo "y".$i++;
  echo "z".$i++;
} while ($i<15);
==== for1
$a=2+@A;
if (@B) $a++;
for ($i=$a;$i>0;$i--)
  echo $i;
==== for2
$a=2+@A;
$inc=1+@B;
for ($i=$a;$i<10;$i=$i+$inc)
  echo $i;
==== for3
$a=0+@A;
$up=5+@B;
for ($i=$a;$i<$up;$i++)
  echo $i;

==== foreach1
$a = array("a", "b");
if (@A)
    $a[] = "c";
if (@B && @A)
    $a[] = "d";
$a[] = "e";
foreach ($a as $b)
  echo $b;

==== foreach2key
$a = array("a", "b");
if (@A)
    $a[] = "c";
if (@B && @A)
    $a[] = "d";
$a[] = "e";
foreach ($a as $k=>$b)
  echo "$k -> $b; ";


==== foreach3refshort
$a = array();
if (@A)
    $a[] = "c";
$a[] = "e";
foreach ($a as $k=>&$x)
  $x = "[$x]";
foreach ($a as $k=>$b)
  echo "$k -> $b; ";


==== foreach3ref
$a = array("a", "b");
if (@A)
    $a[] = "c";
if (@B && @A)
    $a[] = "d";
$a[] = "e";
foreach ($a as $k=>&$x)
  $x = "[$x]";
foreach ($a as $k=>$b)
  echo "$k -> $b; ";

==== foreach4vref
$a = array("a");
if (@A)
    $a[] = "c";
$a[] = "e";
if (@B)
    foreach ($a as $k=>&$x)
        $x = "[$x]";
foreach ($a as $k=>$b)
    echo "$k -> $b; ";

==== foreach5varray
$a = array("a");
if (@A)
    $a = array("b", "c");
if (@B)
    $a[] = "e";
foreach ($a as $k=>$b)
    echo "$k -> $b; ";


==== returnRef
class foo {
    public $value = 42;

    public function &getValue() {
        return $this->value;
    }
}

$obj = new foo;
$myValue = &$obj->getValue(); // $myValue is a reference to $obj->value, which is 42.
echo $myValue;
$obj->value = 2;
echo $myValue;                // prints the new value of $obj->value, i.e. 2.

==== class1
class F{
    public $x = 0;
}
$f=new F();
if (@A)
    $f->x=2;
if (@B)
    echo $f->x++;
if (@C)
    echo ++$f->x;
echo $f->x;

==== vstringconcat
$a="x".@A;
echo $a;

==== vstringconcat2
$a="x";
$b="y";
$c=$a.$b;
echo $c;
$d=$c.@A;
$e=$d.(1+@B);
echo $e;

==== argvar
function foo(&$var) {  $var++; }
function bar($var) {  $var++; }
$a=5;
foo($a);
echo $a;
bar($a);
echo $a;

==== argvar_dyn
function foo(&$var) {  $var++; }
function bar($var) {  $var++; }
$fun = "foo";
$a=5;
$fun($a);
echo $a;
$fun = "bar";
$fun($a);
echo $a;

==== arggetvalue
$a=array(1=>2);
function foo(&$var) {  $var++; }
function bar($var) {  $var++; }
$fun = "foo";
$fun($a[1]);
echo $a[1];
$fun = "bar";
$fun($a[1]);
echo $a[1];

==== varggetvalue
$a=array(1=>2);
if (@A)
    $a[1] = 3;
function foo(&$var) {  $var++; }
function bar($var) {  $var++; }
$fun = "foo";
$fun($a[1]);
echo $a[1];
$fun = "bar";
$fun($a[1]);
echo $a[1];

==== varggetfieldvalue
class F{
    public $x = 0;
}
$f=new F();
if (@A)
    $f->x=2;
function foo(&$var) {  $var++; }
function bar($var) {  $var++; }
$fun = "foo";
$fun($f->x);
echo $f->x;
$fun = "bar";
$fun($f->x);
echo $f->x;


==== varggetfieldvalue_this
function foo(&$var) {  $var++; }
function bar($var) {  $var++; }

class F{
    public $x = 0;
    function foo() {
        $fun = "foo";
        $fun($this->x);
        echo $this->x;
        $fun = "bar";
        $fun($this->x);
        echo $this->x;
    }
}
$f=new F();
if (@A)
    $f->x=2;
$f->foo();
echo $f->x;

==== globalvar
global $x;
$x = 1;
function foo() {
  global $x;
  echo $x;
  if (@A)
    $x++;
}
function bar() {
  global $x;
  echo $x;
  $x++;
}
echo $x;
foo();
echo $x;
bar();
echo $x;

==== globalvar2
$GLOBALS['x'] = 1;
function foo() {
  global $x;
  echo $x;
  if (@A)
    $x++;
}
function bar() {
  global $x;
  echo $x;
  $x++;
}
foo();
bar();

==== globalvar3
$GLOBALS['x'] = 1;
function foo() {
  echo $GLOBALS['x'];
  if (@A)
    $GLOBALS['x']++;
}
function bar() {
  echo $GLOBALS['x'];
  $GLOBALS['x']++;
}
foo();
bar();

==== function_exists
function foo() { echo "x"; }
echo function_exists("foo");
echo function_exists("bar");
foo();

==== var_vs_val_parameter
function foo($a, $b)
{
    $b = 20;
	var_dump($a);
	var_dump($b);
}
foo(1, 2);

==== passbyreference
function f($arg1, &$arg2)
{
	echo $arg1++;
	echo $arg2++;
}

function g (&$arg1, &$arg2)
{
	echo $arg2;
}
$a = 7;
$b = 15;

f($a, $b);

echo $a;
echo $b;

$c=array(1);
g($c,$c[0]);

echo $c[0];

==== stringtemplates
$a = 1;
$b = "f$a";
echo $b;

==== vstringtemplates
$a = 1;
if (@A) $a=2;
$b = "f$a";
echo $b;

==== arrayvar
$a = array(1, 2, 5);
$b = &$a;
$a[] = 10;
echo implode("-",$a);
echo implode("-",$b);

==== arrayvar2
$a = array(1, 2, 5);
$b = $a;
$a[] = 10;
echo implode("-",$a);
echo implode("-",$b);

==== arrayinit
function __($a) { return "..".$a; }
$defaults = array(
		'show_option_all' => '', 'show_option_none' => __('No categories'),
		'hierarchical' => true, 'title_li' => __( 'Categories' ),
		'echo' => 1
	);
foreach ($defaults as $k => $v)
  echo $k."->".$v."; ";

==== call_user_func_array_var
class X {
        function foo(&$v, $a) {
                $v = $a;
        }
}
$x = new X();
$v = "x";
$a = array( &$v, "y");
print_r($a);
call_user_func_array(array($x, "foo"), $a);
print_r($a);

==== array_merge_call_user_func_array_var
class X {
        function foo(&$v, $a) {
                $v = $a;
        }
}
$x = new X();
$v = "x";
$vv = array(1, 2);
$a = array_merge(array( &$v, "y"), $vv);
print_r($a);
call_user_func_array(array($x, "foo"), $a);
print_r($a);

==== call_user_func_array_var2
class X {
        function foo(&$v, $a) {
                $v = $a;
        }
        function bar($a, &$v) {
                $args = array(&$v, $a);
                call_user_func_array(array($this, "foo"), $args);
        }
}
$x = new X();
$v = "x";
$a = array( "y", &$v);
print_r($a);
call_user_func_array(array($x, "bar"), $a);
print_r($a);

==== call_user_func_array_var3
class X {
        function foo(&$v, $a, $b) {
                $v = $a;
                echo $b;
        }
        function bar($a, &$v, $b) {
                $args = array_merge(array(&$v, $a), $b);
                call_user_func_array(array($this, "foo"), $args);
        }
}
$x = new X();
$v = "x";
$b = array(5);
$a = array( "y", &$v, $b);
print_r($a);
call_user_func_array(array($x, "bar"), $a);
print_r($a);

==== callbacks_phpdoc
function my_callback_function() {
    echo 'hello1world!';
}
class MyClass {
    static function myCallbackMethod() {
        echo 'Hello2World!';
    }
}
call_user_func('my_callback_function');
call_user_func(array('MyClass', 'myCallbackMethod'));
$obj = new MyClass();
call_user_func(array($obj, 'myCallbackMethod'));
call_user_func('MyClass::myCallbackMethod');
class A {
    public static function who() {
        echo "A\n";
    }
}
class B extends A {
    public static function who() {
        echo "B\n";
    }
}
call_user_func(array('B', 'who'));
//TODO not supported in quercus:
//call_user_func(array('B', 'parent::who'));
class C {
    public function __invoke($name) {
        echo 'Hello3', $name, "\n";
    }
}
$c = new C();
call_user_func($c, 'PHP!');

==== namespaces
namespace my\name; // see "Defining Namespaces" section
class MyClass {
    function foo() { echo "A;"; }
}
function myfunction() {}
const MYCONST = 1;
$a = new MyClass;
echo $a->foo().".";
$c = new \my\name\MyClass; // see "Global Space" section
echo $c->foo().".";
$a = strlen('hi');
echo $a.".";
//$d = namespace\MYCONST;
//echo $d.".";
$d = __NAMESPACE__ . '\MYCONST';
//echo $d.".";
echo constant($d);

==== condAssignByRef
$x = 1;
if (@A) {
  $a = 2;
  $x = &$a;
  $a = 3;
  echo $x;
}
if (@B) {
  $a = 4;
  $x = &$a;
  $a = 5;
  if (@A)
    $a = 6;
  echo $x;
}
echo $x;