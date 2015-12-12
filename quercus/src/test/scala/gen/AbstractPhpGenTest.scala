package edu.cmu.cs.varex.gen

import de.fosd.typechef.featureexpr.FeatureExprFactory
import edu.cmu.cs.varex.AbstractPhpTest

/**
  * Created by ckaestne on 12/11/2015.
  */
trait AbstractPhpGenTest extends AbstractPhpTest {

    val fA = FeatureExprFactory.createDefinedExternal("A")
    val fB = FeatureExprFactory.createDefinedExternal("B")
    val fC = FeatureExprFactory.createDefinedExternal("C")
    val fD = FeatureExprFactory.createDefinedExternal("D")
    val True = FeatureExprFactory.True

    override def eval(s: String) = {

        Eval(s.
            replace("@A", "create_conditional('A')").
            replace("@B", "create_conditional('B')").
            replace("@C", "create_conditional('C')").
            replace("@D", "create_conditional('D')")
        )
    }

}
