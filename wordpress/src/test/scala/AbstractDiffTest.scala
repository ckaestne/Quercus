//package edu.cmu.cs.varex
//
//import java.io.{BufferedReader, File, FileWriter}
//import java.security.Principal
//import java.util
//import java.util.logging.{SimpleFormatter, StreamHandler, Level, Logger}
//import java.util.{Collections, Locale}
//import javax.servlet._
//import javax.servlet.http._
//
//import com.caucho.quercus.TQuercus
//import com.caucho.util.CharBuffer
//import com.openbrace.obmimic.mimic.servlet.http.HttpServletRequestMimic
//import com.openbrace.obmimic.support.servlet.{EndPoint, URLEncodedRequestParameters}
//import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch
//
//import scala.sys.process._
//
//import scala.collection.JavaConversions._
//import scala.io.Source
//
//class AbstractDiffTest {
//
//    val externalPHPexecutable = "\\php\\php.exe"
//
//    def testFile(phpFile: String): Unit = {
//        val file = new File(phpFile)
//        assert(file.exists(), s"file $file does not exist")
//
//
//
//        val request: HttpServletRequestMimic = new HttpServletRequestMimic()
//        request.getMimicState.setURIFromContextRelativePath(
//            "/wordpress-4.3.1/index.php")
//
//        val out = new com.caucho.vfs.StringWriter(new CharBuffer())
//        TQuercus.mainFile(file, out, request, Map[String, String]())
//        var quercusResult = out.getString.trim
//        quercusResult = quercusResult.replace("\r\n","\n")
//
//
//        var zendResult = (externalPHPexecutable + " " + file.getPath) !!
//
//        zendResult = zendResult.replace("\r\n","\n")
//
//        assert(zendResult == quercusResult,
//            explainResult(zendResult, quercusResult, file))
//    }
//
//    def explainResult(expected: String, actual: String, testedFile: File): String = {
//        val diff = new DiffMatchPatch()
//        diff.patchMargin = 100
//        val diffs = diff.diffMain(expected, actual, true)
//        val patch = diff.patchMake(diffs)
//
//        val txt = diff.patchToText(patch)
//
//        val writer = new FileWriter(new File(testedFile.getParent, testedFile.getName + ".diff.html"))
//        writer.write(diff.diffPrettyHtml(diffs))
//        writer.close()
//
//        s"mismatch between expected output and actual output: \nEXPECTED:\n$expected\nFOUND:\n$actual\nDIFF:\n$txt"
//    }
//
//
//}
