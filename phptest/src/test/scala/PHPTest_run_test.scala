
package edu.cmu.cs.varex


import org.junit.{Ignore, Test}

class PHPTest_run_test extends AbstractPHPTest {

            @Test def test666_test001() { testFile("phptest/src/test/resources/run-test/test001.phpt") }
  @Test def test667_test002() { testFile("phptest/src/test/resources/run-test/test002.phpt") }
  @Test def test668_test003() { testFile("phptest/src/test/resources/run-test/test003.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def broken669_test004() { testFile("phptest/src/test/resources/run-test/test004.phpt") }
  @Ignore("SKIPIF not supported")@Test def test670_test005() { testFile("phptest/src/test/resources/run-test/test005.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def broken671_test006() { testFile("phptest/src/test/resources/run-test/test006.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def broken672_test007() { testFile("phptest/src/test/resources/run-test/test007.phpt") }
  @Ignore("SKIPIF not supported")@Test def test673_test008() { testFile("phptest/src/test/resources/run-test/test008.phpt") }
  @Ignore("SKIPIF not supported")@Test def test674_test008a() { testFile("phptest/src/test/resources/run-test/test008a.phpt") }
  @Test def test675_test009() { testFile("phptest/src/test/resources/run-test/test009.phpt") }
  @Ignore("requires STDIN")@Test def test676_test010() { testFile("phptest/src/test/resources/run-test/test010.phpt") }
  }