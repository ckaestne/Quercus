package edu.cmu.cs.varex

import de.fosd.typechef.featureexpr.FeatureExprFactory
import org.junit.Test

/**
  * Created by ckaestne on 11/27/2015.
  */
class CreateConditionalTest extends AbstractPhpTest {

    val foo = FeatureExprFactory.createDefinedExternal("foo")

    @Test
    def testCreateConditionalBoolean() {
        eval("") to "";

        eval("echo 'foo';") to "foo";


        eval("echo create_conditional('foo');") to c(foo.not, "1")
    }

    @Test
    def testCreateConditionalBoolean2() {
        eval("echo 1+create_conditional('foo');") to c(foo, "1")+c(foo.not(), "2")
    }

}
