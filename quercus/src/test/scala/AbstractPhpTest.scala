package edu.cmu.cs.varex

import java.util.logging.{ConsoleHandler, Level, Logger}

import com.caucho.quercus.TQuercus
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch

/**
  * Created by ckaestne on 11/27/2015.
  */
trait AbstractPhpTest {

    val log =Logger.getLogger("com.caucho.quercus")
    log.setLevel(Level.ALL)
    val handler = new ConsoleHandler()
    log.addHandler(handler)
    handler.setLevel(Level.WARNING)

    log.fine("test")

    case class Eval(code: String) {
        def to(expected: String): Unit = {
            val result = TQuercus.executeScript(code)
            assert(expected.trim==result.trim, explainResult(expected, result))
        }
    }

    def eval(code: String) = Eval("<?php "+code)

    def explainResult(expected: String, actual: String): String = {
        val diff = new DiffMatchPatch()
        diff.patchMargin = 100
        val diffs = diff.diffMain(expected, actual, true)
        val patch = diff.patchMake(diffs)

        val txt = diff.patchToText(patch)

        s"mismatch between expected output and actual output: \nEXPECTED:\n$expected\nFOUND:\n$actual\nDIFF:\n$txt"
    }
}
