# Varex

This is a reimplementation of the variational PHP interpreter Varex,
based on the PHP interpreter Quercus (see [VarexJ](https://github.com/meinicke/VarexJ) for a Java version and the original [Varex](https://github.com/git1997/VarExecution) implementation for a previous implementation for PHP). It allows to create variational
values in PHP code by calling `create_conditional` and then compute
with them.

A description of the idea ad the potential can be found in the ICSE'14
paper ["Exploring Variability-Aware Execution for Testing Plugin-Based Web Applications"](https://www.cs.cmu.edu/~ckaestne/pdf/icse14_varex.pdf).


The transition from a traditional to a variational interpreter is only 
partial. Not all language features and libraries are supported, but
supports gets better with newer versions. 

The project uses the `sbt` build system, build with
`sbt "project quercus" compile`. It is written in Java, but many
tests are written in Scala. For development, we recommend IntelliJ 
IDEA, which can directly import sbt projects. When using
Eclipse, you might want to consider using the
[sbt-eclipse](https://github.com/typesafehub/sbteclipse/) plugin.

The issue tracker is used for selecting and assigning implementation
tasks. The wiki contains additional technical information about
the project.


[Quercus]: <http://quercus.caucho.com/>
