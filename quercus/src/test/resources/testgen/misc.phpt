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
==== if
$a=@A; 
if($a==0) {	echo "x";} else {	echo "y";}
==== switch
$a=@A; 
switch($a) {
	case 0:
		echo "x";	
		break;
	case 1:
		echo "y";
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
