package edu.cmu.cs.varex

import java.io.{File, FileWriter}

import com.caucho.quercus.TQuercus
import edu.cmu.cs.varex.vio.VWriteStreamImpl
import net.liftweb.mocks.MockHttpServletRequest
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch

import scala.sys.process._

/**
  * infrastructure to test Varex against the native PHP interpreter.
  *
  * This test infrastructure does not consider variability at all. For testing across multiple
  * configurations, check AbstractVDiffTest
  */
trait AbstractDiffTest {


    val externalPHPexecutable = "\\php\\php.exe"
    val phpExecutable = if (new File(externalPHPexecutable).exists()) externalPHPexecutable else "php"

    def testFile(phpFile: String): Unit = {
        val file = new File(phpFile)
        assert(file.exists(), s"file $file does not exist")



        val request = new MockHttpServletRequest()
        val out = new VWriteStreamImpl()
        new TQuercus().executeFile(file, out, request)
        var quercusResult = out.getPlainOutput
        quercusResult = quercusResult.replace("\r\n","\n").trim
        writeFile(file, ".quercus.html", quercusResult)


        var zendResult = (phpExecutable + " " + file.getPath) !!

        zendResult = zendResult.replace("\r\n","\n").trim
        writeFile(file, ".zend.html", zendResult)

        assert(zendResult == quercusResult,
            explainResult(zendResult, quercusResult, file))
    }

    def explainResult(expected: String, actual: String, testedFile: File): String = {
        val diff = new DiffMatchPatch()
        diff.patchMargin = 100
        val diffs = diff.diffMain(expected, actual, true)
        val patch = diff.patchMake(diffs)

        val txt = diff.patchToText(patch)

        writeFile(testedFile, ".diff.html", diff.diffPrettyHtml(diffs))

        s"mismatch between expected output and actual output: \nEXPECTED:\n$expected\nFOUND:\n$actual\nDIFF:\n$txt"
    }

    def writeFile(file: File, ext: String, content: String): Unit = {
          writeFile(new File(file.getParent, file.getName + ext), content)
    }

        def writeFile(file: File, content: String): Unit = {
        val writer = new FileWriter(file)
        writer.write(content)
        writer.close()
    }

}
