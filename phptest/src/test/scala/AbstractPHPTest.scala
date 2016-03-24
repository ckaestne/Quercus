package edu.cmu.cs.varex

import java.io.{File, FileWriter}
import java.util
import javax.servlet._
import javax.servlet.http.{Cookie, HttpServletRequest, HttpServletResponse, Part}

import com.caucho.quercus.TQuercus
import com.openbrace.obmimic.mimic.servlet.http.HttpServletRequestMimic
import com.openbrace.obmimic.support.servlet.{EndPoint, URLEncodedRequestParameters}
import de.fosd.typechef.featureexpr.FeatureExprFactory
import edu.cmu.cs.varex.vio.VWriteStreamImpl
import net.liftweb.mocks.MockHttpServletRequest
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch

import scala.collection.JavaConversions._
import scala.io.Source

class AbstractPHPTest {

    val phpRootDir = new File(".")
    assert(phpRootDir.exists(), s"folder $phpRootDir not found")

    val featureCtx = FeatureExprFactory.createDefinedExternal("CTX")

    def testFile(phptFile: String): Unit = {
        val file = new File(phptFile)
        assert(file.exists(), s"file $file does not exist")
        _testFile(file)
    }

    def splitSections(lines: Iterator[String]): Map[String, String] = {
        var section: String = ""
        var sectionContent = new StringBuilder()
        var result = Map[String, String]()

        def newSection(sectionName: String): Unit = {
            result += (section -> sectionContent.toString())
            sectionContent = new StringBuilder
            section = sectionName
        }

        for (line <- lines) {
            if ((line startsWith "--") && (line endsWith "--") && Character.isUpperCase(line.charAt(2)))
                newSection(line.drop(2).dropRight(2))
            else
                sectionContent.append(line).append("\n")
        }
        newSection("")

        result
    }

    def findContentType(s: String): String = s.split("\n").find(_ startsWith "Content-Type: ").map(_.drop(14)).getOrElse("")

    def _testFile(phptFile: File): Unit = {
        assert(phptFile.exists(), s"file $phptFile not found")
        val content = splitSections(Source.fromFile(phptFile).getLines())


        if (content contains "TEST")
            println(" -- " + content("TEST"))

        val request = createRequest(content)





        val ini = for (line <- content.getOrElse("INI", "").split("\n");
                       if line.split("=").length == 2) yield (line.split("=")(0) -> line.split("=")(1))

        assert((content contains "FILE") || (content contains "FILEEOF"), "no FILE section in test")
        val testedFile = new File(phptFile.getParent, phptFile.getName.dropRight(1))
        val writer = new FileWriter(testedFile)
        writer.write(content.getOrElse("FILE", ""))
        writer.write(content.getOrElse("FILEEOF", "").replaceAll("[\\r\\n]+$", ""))
        writer.close()

        assert((content contains "EXPECT") || (content contains "EXPECTF") || (content contains "EXPECTREGEX"), "no EXPECT section in test")
        val expectedResult = content.getOrElse("EXPECT", content.getOrElse("EXPECTF", content.getOrElse("EXPECTREGEX", ""))).trim.
            replaceAll("\n?Notice: [^\\n]*\n", "").
            replaceAll("\n?Deprecated: [^\\n]*\n", "").
            replaceAll("\n?Strict Standards:  [^\\n]*\n", "")

//        val out = new VWriteStreamImpl()
//        new TQuercus(ini.toMap[String, String]).executeFile(testedFile, out, request, VHelper.True())
//        val phpResult = out.getPlainOutput.trim
//
//
//        assert(matchResult(expectedResult, phpResult, content contains "EXPECTF", content contains "EXPECTREGEX"),
//            explainResult(expectedResult, phpResult, phptFile))

        //additionally execute under specific context. expect to get a choice between that the previous output (under the given condition) and nothing
        val cout = new VWriteStreamImpl()
        new TQuercus(ini.toMap[String, String]).executeFile(testedFile, cout, request, featureCtx)
        val cphpResult = cout.getConditionalOutput.filter(_.getCondition.evaluate(Set(featureCtx.feature))).map(_.getValue).mkString.trim
        val otherphpResult = cout.getConditionalOutput.filter(_.getCondition.evaluate(Set())).map(_.getValue).mkString.trim
        assert(matchResult(expectedResult, cphpResult, content contains "EXPECTF", content contains "EXPECTREGEX"),
            explainResult(expectedResult, cphpResult, phptFile))
        assert(otherphpResult == "", "found unexpected output under condition "+featureCtx.not()+":\n"+otherphpResult)


        testedFile.deleteOnExit()
    }

    def createRequest(content: Map[String, String]): HttpServletRequest = {

        val request = new  MyMockHttpServletRequest()
        request.localAddr = "/test.php"
        //        state.getSessionDetails().setSessionIdRequestedByURL("123456789");
        if (content contains "GET") {
            request queryString_= content("GET").trim
        }
        if (content contains "POST") {
            request body = content("POST").trim
            request.headers += ("Content-Type"->List("application/x-www-form-urlencoded"))
            request.contentType = "iso-8859-1"
            request.method = "POST"
        }
        if (content contains "POST_RAW") {
            request body = content("POST_RAW").split("\n").filterNot(_ startsWith "Content-Type:").mkString("\n").trim
            request.contentType = "iso-8859-1"
            request.headers += ("Content-Type"->List(findContentType(content("POST_RAW"))))
            request.method = "POST"
        }
        if (content contains "COOKIE") {
            request.cookies = for (c <- content("COOKIE").trim.split(";").toList; if c.split("=").length == 2) yield
                new Cookie(c.split("=")(0).trim.replace(" ", "_"), c.split("=")(1))
        }

        request

    }

    def _createRequest(content: Map[String, String]): HttpServletRequest = {
        val request: HttpServletRequestMimic = new HttpServletRequestMimic()
        val state = request.getMimicState
        state.setFirstLineURLEndPoint(
            new EndPoint("www.abc.def.com", null, 80))
        state.setServerEndPoint(
            new EndPoint("www.ghi.jkl.com", null, 80))
        //        state.getRelativeURI().setContextPath("/context");
        //        state.getRelativeURI().setServletPath("/myservlet");
        //        state.getRelativeURI().setPathInfo("/test.php");
        state.setURIFromContextRelativePath(
            "/test.php")
        //        state.getSessionDetails().setSessionIdRequestedByURL("123456789");
        if (content contains "POST") {
            request.getMimicState.setBodyContent(content("POST").trim, "iso-8859-1")
            request.getMimicState.setHttpMethodName("POST")
            request.getMimicState.setContentTypeMimeType("application/x-www-form-urlencoded")
        }
        if (content contains "POST_RAW") {
            request.getMimicState.setBodyContent(content("POST_RAW").split("\n").filterNot(_ startsWith "Content-Type:").mkString("\n").trim, "iso-8859-1")
            request.getMimicState.setHttpMethodName("POST")
            request.getMimicState.setContentTypeMimeType(findContentType(content("POST_RAW")))
        }
        if (content contains "GET") {
            val params = new URLEncodedRequestParameters(content("GET").trim, "iso-8859-1")
            request.getMimicState.getRequestParameters.append(params)
        }
        if (content contains "COOKIE") {
            for (c <- content("COOKIE").trim.split(";"); if c.split("=").length == 2)
                request.getMimicState.getCookies.add(new Cookie(c.split("=")(0).trim.replace(" ", "_"), c.split("=")(1)))
        }
        request
    }

    def matchResult(expected: String, actual: String, isRegExF: Boolean, isRegEx: Boolean): Boolean = if (!isRegEx && !isRegExF) expected == actual
    else {

        val expectedPattern = if (isRegExF)
            Preg_quote.preg_quote2(expected).
                replace("%e", "\\\\" + File.pathSeparatorChar).
                replace("%s", "[^\\r\\n]+").
                replace("%S", "[^\\r\\n]*").
                replace("%a", ".+").
                replace("%A", ".*").
                replace("%w", "\\s*").
                replace("%i", "[+-]?\\d+").
                replace("%d", "\\d+").
                replace("%x", "[0-9a-fA-F]+").
                replace("%f", "[+-]?\\.?\\d+\\.?\\d*(?:[Ee][+-]?\\d+)?").
                replace("%c", ".").
                replace("%unicode\\|string%", "string").
                replace("%unicode_string_optional%", "string").
                replace("%binary_string_optional%", "string").
                replace("%u\\|b%", "")
        else expected

        actual.matches("^" + expectedPattern + "$")
    }

    def explainResult(expected: String, actual: String, testedFile: File): String = {
        val diff = new DiffMatchPatch()
        diff.patchMargin = 100
        val diffs = diff.diffMain(expected, actual, true)
        val patch = diff.patchMake(diffs)

        val txt = diff.patchToText(patch)

        val writer = new FileWriter(new File(testedFile.getParent, testedFile.getName + ".diff.html"))
        writer.write(diff.diffPrettyHtml(diffs))
        writer.close()

        s"mismatch between expected output and actual output: \nEXPECTED:\n$expected\nFOUND:\n$actual\nDIFF:\n$txt"
    }


}


class MyMockHttpServletRequest() extends MockHttpServletRequest() {
    override def queryString : String =
        if (!parameters.isEmpty) {
            parameters.map{ case (k,v) => k + "=" + v }.mkString("&")
        } else {
            null
        }

    override def getParts: util.Collection[Part] = ???

    override def getPart(name: String): Part = ???

    override def authenticate(response: HttpServletResponse): Boolean = ???

    override def logout(): Unit = ???

    override def login(username: String, password: String): Unit = ???

    override def isAsyncStarted: Boolean = ???

    override def startAsync(): AsyncContext = ???

    override def startAsync(servletRequest: ServletRequest, servletResponse: ServletResponse): AsyncContext = ???

    override def getAsyncContext: AsyncContext = ???

    override def getDispatcherType: DispatcherType = ???

    override def isAsyncSupported: Boolean = ???

    override def getServletContext: ServletContext = ???


}
/*

 */