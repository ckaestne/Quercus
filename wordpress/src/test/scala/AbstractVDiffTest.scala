package edu.cmu.cs.varex

import java.io.{File, FileWriter}

import com.caucho.quercus.TQuercus
import edu.cmu.cs.varex.vio.VWriteStreamImpl
import net.liftweb.mocks.MockHttpServletRequest
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch

import scala.collection.JavaConversions._
import scala.sys.process._

/**
  * infrastructure to test variational execution with Varex against brute-force execution with the native PHP interpreter.
  */
trait AbstractVDiffTest {


    val externalPHPexecutable = "\\php\\php.exe"
    val phpExecutable = if (new File(externalPHPexecutable).exists()) externalPHPexecutable else "php"

    def testFile(phpFile: String, options: List[String]): Unit = {
        assert(options.size < 8, "too many options for brute-force execution; consider a sampling strategy")
        val file = new File(phpFile)
        assert(file.exists(), s"file $file does not exist")



        var quercusVResult = executeVariational(file, options)

        for ((enabled, disabled) <- explode(options).reverse) {
            val zendResult = executeOne(file, enabled).
                replace("\r\n", "\n").trim

            val quercusResult = quercusVResult.
                filter(_.getCondition.evaluate(enabled.toSet)).map(_.getValue).mkString.
                replace("\r\n", "\n").trim

            assert(zendResult == quercusResult,
                explainResult(enabled, zendResult, quercusResult, file))
            println("> config " + enabled.mkString("[", ",", "]") + " passed")
        }


    }


    def executeOne(file: File, enabledOptions: List[String]): String = {
        (phpExecutable + " " + file.getPath + " --args " + enabledOptions.map("_VA_" + _).mkString(" ")) !!
    }


    def executeVariational(file: File, options: List[String]): List[Opt[String]] = {
        val request = new MockHttpServletRequest()
        request.parameters ++= options.map(o => ("_V_" + o, "_V_" + o))
        val out = new VWriteStreamImpl()
        new TQuercus().executeFile(file, out, request, VHelper.True())
        out.getConditionalOutput.toList
    }

    def explainResult(enabledOptions: List[String], expected: String, actual: String, testedFile: File): String = {
        val diff = new DiffMatchPatch()
        diff.patchMargin = 100
        val diffs = diff.diffMain(expected, actual, true)
        val patch = diff.patchMake(diffs)

        val txt = diff.patchToText(patch)

        writeFile(testedFile, ".diff.html", diff.diffPrettyHtml(diffs))
        val config = enabledOptions.mkString("[", ",", "]")

        s"mismatch between expected output and actual output in config $config: \nEXPECTED:\n$expected\nFOUND:\n$actual\nDIFF:\n$txt"
    }

    def writeFile(file: File, ext: String, content: String): Unit = {
        writeFile(new File(file.getParent, file.getName + ext), content)
    }

    def writeFile(file: File, content: String): Unit = {
        val writer = new FileWriter(file)
        writer.write(content)
        writer.close()
    }


    type Feature = String
    type Config = (List[Feature], List[Feature])

    def explode(fs: List[Feature]): List[Config] = {
        if (fs.isEmpty) List((Nil, Nil))
        else if (fs.size == 1) List((List(fs.head), Nil), (Nil, List(fs.head)))
        else {
            val r = explode(fs.tail)
            r.map(x => (fs.head :: x._1, x._2)) ++ r.map(x => (x._1, fs.head :: x._2))
        }
    }
}
