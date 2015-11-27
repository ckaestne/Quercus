package edu.cmu.cs.varex

import com.caucho.quercus.TQuercus
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch

/**
  * Created by ckaestne on 11/27/2015.
  */
trait AbstractPhpTest {

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
