//import java.io.File
//import java.util.logging._
//
//import com.caucho.quercus.TQuercus
//import com.caucho.util.CharBuffer
//import com.openbrace.obmimic.mimic.servlet.http.HttpServletRequestMimic
//import org.springframework.mock.web.MockHttpServletRequest
//
//import scala.collection.JavaConversions._
//
///**
//  * Created by ckaestne on 11/26/2015.
//  */
//object TestDB extends App {
//
//    //   val php= """
//    //      |<?php
//    //      |$link = mysql_connect('feature.isri.cmu.edu:3306', 'wordpress_test', 'wp$215$Ux');
//    //      |if (!$link) {
//    //      |    die('Could not connect: ' . mysql_error());
//    //      |}
//    //      |echo 'Connected successfully';
//    //      |mysql_close($link);
//    //      |?>
//    //    """.stripMargin
//    //
//    //
//    //    TQuercus.mainScript(php, StdoutStream.create(), null, Map[String,String]())
//
//    val log =Logger.getLogger("com.caucho.quercus")
//    log.setLevel(Level.ALL)
//    val handler = new ConsoleHandler()
//    log.addHandler(handler)
//    handler.setLevel(Level.ALL)
//
//    log.fine("test")
//
//    //    Quercus.main(List("wordpress/src/main/webapp/wordpress-4.3.1/index.php").toArray)
//    //
//    //
////    val request = new MockHttpServletRequest()
//    val request: HttpServletRequestMimic = new HttpServletRequestMimic()
//    request.getMimicState.setURIFromContextRelativePath(
//        "/wordpress-4.3.1/")
//    val out = new com.caucho.vfs.StringWriter(new CharBuffer())
//    TQuercus.mainFile(new File("wordpress/src/main/webapp/wordpress-4.3.1/index.php"), out, request, Map[String, String]())
//    val phpResult = out.getString.trim
//    println(phpResult)
//}
