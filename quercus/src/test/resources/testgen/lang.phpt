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

==== argvalue
function foo(&$var) {  $var++; }
function bar($var) {  $var++; }
$a=5;
foo($a);
echo $a;
bar($a);
echo $a;

==== argvalue_dyn
function foo(&$var) {  $var++; }
function bar($var) {  $var++; }
$fun = "foo";
$a=5;
$fun($a);
echo $a;
$fun = "bar";
$fun($a);
echo $a;