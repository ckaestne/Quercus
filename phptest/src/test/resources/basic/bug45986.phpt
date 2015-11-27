--TEST--
Bug #45986 (wrong error messag for a non existant file on rename)         --IGNORE different error msg
--CREDITS--
Sebastian Schürmann
sebs@php.net
Testfest 2009 Munich 
--FILE--
<?php
rename('foo', 'bar');
?>
--EXPECTREGEX--
.*No such.*
