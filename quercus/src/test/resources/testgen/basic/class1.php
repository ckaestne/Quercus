<?php

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