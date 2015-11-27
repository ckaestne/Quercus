package edu.cmu.cs.varex

import org.junit.Test

/**
  * Created by ckaestne on 11/27/2015.
  */
class CreateConditionalTest extends AbstractPhpTest {

    @Test
    def testCreateConditionalBoolean() {
        eval("") to "";

        eval("echo 'foo';") to "foo";


        eval("echo create_conditional('foo');") to "CHOICE(definedEx(foo) ? 1 : )"
    }


}
