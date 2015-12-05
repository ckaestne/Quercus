import com.typesafe.sbt.SbtAspectj.{ Aspectj, aspectjSettings, compiledClasses }
import com.typesafe.sbt.SbtAspectj.AspectjKeys.{ inputs, weave }

scalaVersion := "2.11.7"

testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v")

libraryDependencies += "junit" % "junit" % "4.12" % "test"

libraryDependencies += "javax.jms" % "jms-api" % "1.1-rev-1"

libraryDependencies += "com.google.appengine" % "appengine-api-1.0-sdk" % "1.9.17a"

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.13"

libraryDependencies += "de.fosd.typechef" % "featureexprlib_2.11" % "0.3.7"

libraryDependencies += "de.fosd.typechef" % "conditionallib_2.11" % "0.3.7" % "test"

libraryDependencies += "net.liftweb" %% "lift-testkit" % "2.6.2" % "test"

libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"

libraryDependencies += "org.bitbucket.cowwoc.diff-match-patch" % "diff-match-patch" % "1.0" % "test"

libraryDependencies += "org.scala-lang.modules" % "scala-java8-compat_2.11" % "0.7.0" % "test"

libraryDependencies += "org.checkerframework" % "checker-qual" % "1.9.8"

libraryDependencies += "com.google.code.findbugs" % "jsr305" % "3.0.1"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"



jacoco.settings

jacoco.reportFormats in jacoco.Config := Seq(
  de.johoop.jacoco4sbt.XMLReport(encoding = "utf-8"))

//libraryDependencies += "org.checkerframework" % "checker" % "1.9.8"
//
//libraryDependencies += "org.checkerframework" % "jdk8" % "1.9.8"
//
//javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint", "-implicit:class", "-processor",
//    "org.checkerframework.checker.nullness.NullnessChecker", "-AprintErrorStack",
//        "-Xbootclasspath/p:checker/dist/jdk8.jar")



javacOptions ++= Seq("-g")

inputs in Aspectj <+= compiledClasses

// use the results of aspectj weaving
products in Compile <<= products in Aspectj

products in Runtime <<= products in Compile