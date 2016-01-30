package edu.cmu.cs.varex

import org.junit.Test



class PlainDiffTesting extends AbstractDiffTest {

    @Test
    def testWPmain() {
        testFile("wordpress/src/main/webapp/wordpress-4.3.1/index.php")
    }


    @Test
    def testWPmainWithPlugins() {
        testFile("wordpress/src/main/webapp/wordpress-4.3.1/index.php",
            List("hello.php","twitter/twitter.php",
                "slider-image/slider.php","spider-event-calendar/calendar.php"))
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
