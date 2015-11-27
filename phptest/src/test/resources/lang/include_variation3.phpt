--TEST--
Including a file in the current script directory from eval'd code     --BROKEN
--FILE--
<?php
require_once 'include_files/eval.inc';
?>
--EXPECT--
Included!