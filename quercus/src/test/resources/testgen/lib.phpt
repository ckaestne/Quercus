==== isset
$a = 3+@B;
echo isset($a);
echo ".";
echo isset($b);
echo ".";
if (@A)
  $b=1;
echo isset($b);


==== define
define("CONSTANT", "x");
echo CONSTANT;


==== vdefine
//define("CONSTANT", "x".(@A+1));
//echo CONSTANT;

==== vvdefine
//if (@B) {
//  define("X", 2);
//  echo X;
//}

//echo defined("X");

==== debug_backtrace
function printStack($s) {
  foreach ($s as $nr => $row) {
    echo $nr.":";
    echo $row["function"];
    echo "\n";
  }
}
function foo() { bar(); }
function bar() {
  $a = debug_backtrace();
  printStack($a);
}
foo();