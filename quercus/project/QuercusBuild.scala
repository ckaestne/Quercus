//
////runtime @NonNull null checking with AOP -- remove if assertion checking is not desired in release:
//package quercus
//
//import sbt._
//import sbt.Keys._
//import com.typesafe.sbt.SbtAspectj.{ Aspectj, aspectjSettings, compiledClasses }
//import com.typesafe.sbt.SbtAspectj.AspectjKeys.{ inputs, weave }
//
//object QuercusBuild extends Build {
//    lazy val quercus = Project(
//        id = "quercus",
//        base = file("."),
//        settings = Defaults.coreDefaultSettings ++ aspectjSettings ++ Seq(
////            organization := "com.typesafe.sbt.aspectj",
////            version := "0.1-SNAPSHOT",
////            scalaVersion := "2.11.7",
//            // add compiled classes as an input to aspectj
//            inputs in Aspectj <+= compiledClasses,
//
//            // use the results of aspectj weaving
//            products in Compile <<= products in Aspectj,
//            products in Runtime <<= products in Compile
//        )
//    )
//}