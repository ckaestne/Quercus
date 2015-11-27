scalaVersion := "2.11.7"

enablePlugins(JettyPlugin)

libraryDependencies += "junit" % "junit" % "4.11" % Test

libraryDependencies += "org.bitbucket.cowwoc.diff-match-patch" % "diff-match-patch" % "1.0"

libraryDependencies += "org.springframework" % "spring" % "2.0.8" % "test"

libraryDependencies += "org.springframework" % "spring-mock" % "2.0.8" % "test"
