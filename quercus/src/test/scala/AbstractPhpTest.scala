package edu.cmu.cs.varex

import java.util.logging.{ConsoleHandler, Level, Logger}

import com.caucho.quercus.TQuercus
import de.fosd.typechef.featureexpr.{FeatureExpr, FeatureExprFactory}
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch

/**
  * Created by ckaestne on 11/27/2015.
  */
trait AbstractPhpTest extends ConditionalOutputInfrastructure {

    val log = Logger.getLogger("com.caucho.quercus")
    log.setLevel(Level.ALL)
    val handler = new ConsoleHandler()
    log.addHandler(handler)
    handler.setLevel(Level.WARNING)

    log.fine("test")

    case class Eval(code: String) {
        def to(expected: ConditionalOutput): Unit = {
            val result = TQuercus.executeScript(code)
            assert(expected.toString.trim == result.trim, explainResult(expected.toString, result))
        }
    }


    def eval(code: String) = Eval("<?php " + code)

    def explainResult(expected: String, actual: String): String = {
        val diff = new DiffMatchPatch()
        diff.patchMargin = 100
        val diffs = diff.diffMain(expected, actual, true)
        val patch = diff.patchMake(diffs)

        val txt = diff.patchToText(patch)

        s"mismatch between expected output and actual output: \nEXPECTED:\n$expected\nFOUND:\n$actual\nDIFF:\n$txt"
    }
}

trait ConditionalOutputInfrastructure {

    sealed trait ConditionalOutput {
        def +(that: ConditionalOutput) = new ConcatOutput(this, that)
    }

    case class OptionalOutput(f: FeatureExpr, output: String) extends ConditionalOutput {
        override def toString = output
    }

    case class ConcatOutput(a: ConditionalOutput, b: ConditionalOutput) extends ConditionalOutput {
        override def toString = a.toString+b.toString
    }


    def c(f: FeatureExpr, output: String): ConditionalOutput = new OptionalOutput(f, output)

    def c(f: FeatureExpr, output: ConditionalOutput): ConditionalOutput = output match {
        case OptionalOutput(c, o) => new OptionalOutput(f and c, o)
        case ConcatOutput(a, b) => new ConcatOutput(c(f, a), c(f, b))
    }

    implicit def _toOutput(o: String): ConditionalOutput = new OptionalOutput(FeatureExprFactory.True, o)

}