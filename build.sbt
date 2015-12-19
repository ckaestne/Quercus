


scalaVersion := "2.11.7"

name := "Varex"

version := "0.1"

lazy val hessian = project

lazy val kernel = project.dependsOn(hessian)

lazy val quercus = project.dependsOn(kernel).
    settings(aspectjSettings: _*)

lazy val phptest = project.dependsOn(quercus % "test->test;compile->compile")

lazy val wordpress = project.dependsOn(quercus % "test->test;compile->compile")

scalaVersion in ThisBuild := "2.11.7"

libraryDependencies in ThisBuild += "junit" % "junit" % "4.12" % "test"

libraryDependencies in ThisBuild += "com.novocode" % "junit-interface" % "0.11" % "test"

libraryDependencies in ThisBuild += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"

testOptions in ThisBuild += Tests.Argument(TestFrameworks.JUnit, "-q", "-v")

javacOptions in ThisBuild ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

jacoco.reportFormats in jacoco.Config in ThisBuild := Seq(
    de.johoop.jacoco4sbt.XMLReport(encoding = "utf-8"))

//libraryDependencies += "org.checkerframework" % "checker" % "1.9.8"
//
//libraryDependencies += "org.checkerframework" % "jdk8" % "1.9.8"
//
//javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint", "-implicit:class", "-processor",
//    "org.checkerframework.checker.nullness.NullnessChecker", "-AprintErrorStack",
//        "-Xbootclasspath/p:checker/dist/jdk8.jar")


parallelExecution in Test in ThisBuild  := false

parallelExecution in jacoco.Config in ThisBuild  := false


initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8")
    sys.error("Java 8 is required for this project.")
}

//generate typechef.sh file with full classpath
TaskKey[File]("mkrun") <<= (baseDirectory, fullClasspath in Runtime, mainClass in Runtime) map {
    (base, cp, main) =>
        val template = """#!/bin/sh
java -ea -Xmx3G -Xms128m -Xss10m -classpath "%s" %s "$@"
                       """
        val contents = template.format(cp.files.absString, "")
        val out = base / "run.sh"
        IO.write(out, contents)
        out.setExecutable(true)
        out
}
