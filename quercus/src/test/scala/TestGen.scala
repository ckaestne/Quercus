package edu.cmu.cs.varex

import java.io.{File, FileWriter}

import scala.io.Source


/**
  * this class takes the PHP snippets in resources/testgen/.. and
  * generates JUnit tests for them. The expected test outputs
  * are created through brute forcing them with a plain php interpreter.
  *
  * Notice that this test generator will need an executable PHP interpreter
  * (preferably actually a quercus implementation not a Zend PHP
  * interpreter), but the generated test cases will not.
  */
object TestGen extends App {
    val externalPHPexecutable = "\\php\\php.exe"
    val phpExecutable = if (new File(externalPHPexecutable).exists()) externalPHPexecutable else "php"

    val features = List("A", "B", "C", "D")

    val resourceRoot = new File("quercus/src/test/resources/testgen")
    val targetDir = new File("quercus/src/test/scala/gen")
    assert(resourceRoot.exists(), "resource directory not found")
    assert(targetDir.exists(), "target directory not found")


    //expand the  resourceRoot/misc.phpt file into multiple test files
    val miscFragments = Source.fromFile(new File(resourceRoot, "misc.phpt")).mkString.split("====").map(_.trim).filter(_.size > 0)
    var frags = Set[String]()
    for (frag <- miscFragments) {
        val firstBreak = frag.indexOf("\n")
        val name = frag.take(firstBreak-1)
        val writer = new FileWriter(new File(resourceRoot, "misc/" + name + ".php"))
        writer.write("<?php \n")
        writer.write(frag.drop(firstBreak+1))
        writer.close
        assert(!frags.contains(name),s"fragment $name redefined")
        frags += name
    }



    for (dir <- resourceRoot.listFiles(); if dir.isDirectory) {
        val classname = "Generated" + dir.getName.capitalize + "Tests"
        val writer = new FileWriter(new File(targetDir, classname + ".scala"))
        writer.append(
            s"""package edu.cmu.cs.varex.gen
                |import de.fosd.typechef.featureexpr.FeatureExprFactory
                |import org.junit.{Ignore, Test}
                |
              |/** generated file, do not modify */
                |class $classname extends AbstractPhpGenTest {
                |""".stripMargin)

        for (phpFile <- dir.listFiles(); if phpFile.getName endsWith ".php") {

            val testname = phpFile.getName.dropRight(4).capitalize
            val phpLines = Source.fromFile(phpFile).getLines().toList
            val phpContent = phpLines.mkString("\n")

            writer.append(s"\n\t@Test def test$testname() {")
            writer.append("\n\t\teval(\"\"\"" + phpLines.head)
            for (line <- phpLines.tail)
                writer.append("\n\t\t       |" + line)
            writer.append("\"\"\".stripMargin) to ")

            val tmpFile = File.createTempFile("test" + testname, ".php")
            tmpFile.deleteOnExit()

            var first = true
            for (config <- explode(findOptions(phpContent))) {
                var content = phpContent
                var fexprList: List[String] = Nil
                for (f<-config._1) {
                    content = content.replace("@" + f, "True")
                    fexprList ::= "f"+f
                }
                for (f<-config._2) {
                    content = content.replace("@" + f, "False")
                    fexprList ::= "f"+f+".not"
                }
                if (fexprList.isEmpty) fexprList ::= "True"
                val fexpr = fexprList.mkString(" and ")

                val tmpWriter = new FileWriter(tmpFile)
                tmpWriter.write(content.replace("@A", "1"))
                tmpWriter.close()

                import scala.sys.process._

//                println(tmpFile.getAbsolutePath)
                val output = (phpExecutable+" "+tmpFile.getAbsolutePath).!!

                if (!first)
                    writer.append(" ~")
                writer.append("\n\t\t\tc("+fexpr+", \""+output.trim.replace("\"","\\\"").replace("\r","").replace("\n","\\n")+"\")")


                first = false
            }

            writer.append(s"\n\t}\n")
            writer.flush()

        }

        writer.append("\n}\n")
        writer.close()


    }


    def findOptions(s: String): List[Feature] = {
        var r: List[Feature] = Nil
        for (f <- features)
            if (s contains ("@" + f)) r ::= f
        r
    }

    type Feature = String
    type Config = (List[Feature], List[Feature])

    def explode(fs: List[Feature]): List[Config] = {
        if (fs.isEmpty) List((Nil,Nil))
        else if (fs.size == 1) List((List(fs.head), Nil), (Nil, List(fs.head)))
        else {
            val r = explode(fs.tail)
            r.map(x => (fs.head :: x._1, x._2)) ++ r.map(x => (x._1, fs.head :: x._2))
        }
    }


}
