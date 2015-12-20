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
