==== array_filter
$a = array('1', '2', '3', 4, 5);
function filter($x) {
  echo "[".$x."]";
  return $x>2;
}
$b = array_filter($a, "filter");
echo implode( ',', $b );

==== array_filter2
$a = array('1', '2', '3', 4, 5);
function filter($x) {
  echo "[".$x."]";
  $x++;
  return True;
}
$b = array_filter($a, "filter");
echo implode( ',', $b );

==== array_filter3
$a = array('1', '2', '3', 4, 5);
function filter(&$x) {
  echo "[".$x."]";
  $x++;
  return True;
}
$b = array_filter($a, "filter");
echo implode( ',', $b );

==== array_walk
$fruits = array("d" => "lemon", "a" => "orange", "b" => "banana", "c" => "apple");
function test_alter(&$item1, $key, $prefix){    $item1 = "$prefix: $item1";}
function test_print($item2, $key) { echo "$key. $item2;"; }
echo "Before ...:";
array_walk($fruits, 'test_print');
array_walk($fruits, 'test_alter', 'fruit');
echo "... and after:";
array_walk($fruits, 'test_print');

==== array_walk_recursive
$sweet = array('a' => 'apple', 'b' => 'banana');
$fruits = array('sweet' => $sweet, 'sour' => 'lemon');

function test_print($item, $key)
{
    echo "$key holds $item;";
}

array_walk_recursive($fruits, 'test_print');