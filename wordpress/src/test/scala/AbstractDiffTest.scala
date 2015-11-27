package edu.cmu.cs.varex

import java.io.{File, FileWriter}

import com.caucho.quercus.TQuercus
import com.caucho.util.CharBuffer
import net.liftweb.mocks.MockHttpServletRequest
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch

import scala.collection.JavaConversions._
import scala.sys.process._

class AbstractDiffTest {

    val externalPHPexecutable = "\\php\\php.exe"

    def testFile(phpFile: String): Unit = {
        val file = new File(phpFile)
        assert(file.exists(), s"file $file does not exist")



        val request = new MockHttpServletRequest()
        val out = new com.caucho.vfs.StringWriter(new CharBuffer())
        out.openWrite()
        TQuercus.mainFile(file, out, request, Map[String, String]())
        var quercusResult = out.getString.trim
        quercusResult = quercusResult.replace("\r\n","\n")
        writeFile(file, ".quercus.html", quercusResult)


        var zendResult = (externalPHPexecutable + " " + file.getPath) !!

        zendResult = zendResult.replace("\r\n","\n")
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
