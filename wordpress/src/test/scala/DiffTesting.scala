package edu.cmu.cs.varex

import org.junit.Test



class DiffTesting extends AbstractDiffTest {

    @Test
    def testWPmain() {
        testFile("wordpress/src/main/webapp/wordpress-4.3.1/index.php")
    }

    @Test
    def testWPadminmain() {
        testFile("wordpress/src/main/webapp/wordpress-4.3.1/wp-admin/index.php")
    }

    @Test
    def testWPlogin() {
        testFile("wordpress/src/main/webapp/wordpress-4.3.1/wp-login.php")
    }

}
