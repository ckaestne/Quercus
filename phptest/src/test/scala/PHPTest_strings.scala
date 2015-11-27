
package edu.cmu.cs.varex


import org.junit.{Ignore, Test}

class PHPTest_strings extends AbstractPHPTest {

            @Test def test725_001() { testFile("phptest/src/test/resources/strings/001.phpt") }
  @Ignore("FAILING: This test is crashing with Quercus baseline")  @Test def broken726_002() { testFile("phptest/src/test/resources/strings/002.phpt") }
  @Test def test727_004() { testFile("phptest/src/test/resources/strings/004.phpt") }
  @Test def test728_bug22592() { testFile("phptest/src/test/resources/strings/bug22592.phpt") }
  @Ignore("marked to ignore")@Test def broken729_bug26703() { testFile("phptest/src/test/resources/strings/bug26703.phpt") }
  }