==== implode
$a = array('1', '2', '3');
echo implode( ',', $a );

==== str_replace
$a = "foo";
$count = 1;
$b = str_replace("o", "a", $a, $count);
echo "$b - $count;";
$b = str_replace("o", "a", $b, $count);
echo "$b - $count;";