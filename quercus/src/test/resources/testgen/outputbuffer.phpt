==== basic
ob_start();
echo "foo";
ob_end_flush();
echo "bar";

==== basic2
ob_start();
echo "foo";
ob_end_clean();
echo "bar";

==== basic3
ob_start();
echo "foo";

==== vbasic
ob_start();
if (@A)
    echo "foo";
ob_end_flush();
echo "bar";

==== flushorclean
//ob_start();
//echo "foo";
//if (@A)
//    ob_end_flush();
//else
//    ob_end_clean();
//echo "bar";
