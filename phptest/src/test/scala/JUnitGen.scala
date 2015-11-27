import java.io.{File, FileWriter}

import scala.io.Source

/**
  * Created by ckaestne on 11/22/2015.
  */
object JUnitGen extends App {

    val resourceDir = new File("phptest/src/test/resources")
    assert(resourceDir.exists())

    private def getTestDirs(root: File): List[File] =
        root.listFiles().filter(_.isDirectory).toList

    private def getFiles(dir: File): List[File] =
        if (dir.isDirectory) dir.listFiles().toList.flatMap(getFiles)
        else if (dir.isFile && (dir.getName endsWith ".phpt")) List(dir)
        else Nil


    var i = 0;
    for (dir <- getTestDirs(resourceDir)) {

        val dirName = dir.getName.replace('-', '_')

        val w = new FileWriter(s"phptest/src/test/scala/PHPTest_$dirName.scala")

        w.write(
            s"""
               |package edu.cmu.cs.varex
               |
              |
              |import org.junit.{Ignore, Test}
               |
              |class PHPTest_$dirName extends AbstractPHPTest {
               |
            """.stripMargin)

        for (v <- getFiles(dir)) {
            val content = try Source.fromFile(v).getLines().mkString("\n") catch {
                case e: Exception => ""
            }
            if ((content contains "for (;;) {}"))
                w.write("@Ignore(\"ignore infinite loop\")")
            else if ((content contains "--STDIN--"))
                w.write("@Ignore(\"requires STDIN\")")
            else if ((content contains "--ARGS--"))
                w.write("@Ignore(\"requires ARGS\")")
            else if ((content contains "--COOKIE--"))
                w.write("@Ignore(\"cookies not correctly supported\")")
            else if ((content contains "--IGNORE"))
                w.write("@Ignore(\"marked to ignore\")")
            else if ((content contains "--SKIPIF--") && !(content contains "skip ZendEngine 2 needed"))
                w.write("@Ignore(\"SKIPIF not supported\")")
            else
            if (new File(v.getParentFile,v.getName+".diff.html").exists())
                w.write("@Ignore(\"FAILING: This test is failing with Quercus baseline\")")
            i += 1
            val name = v.getName.dropRight(5).replace("-", "_").replace(".", "_")
            val funName = if (content contains "BROKEN") "broken" else "test"
            w.write("@Test def %s%d_%s() { testFile(\"%s\") }\n  ".format(funName, i, name, v.getPath.replace("\\", "/")))
        }

        w.write("}")
        w.close()
    }


}


