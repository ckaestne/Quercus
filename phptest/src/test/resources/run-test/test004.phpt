--TEST--
INI section allows '='   --BROKEN
--INI--
arg_separator.input==
--FILE--
<?php
var_dump(ini_get('arg_separator.input'));
?>
--EXPECT--
string(1) "="