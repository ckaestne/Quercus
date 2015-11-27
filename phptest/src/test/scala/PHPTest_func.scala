
package edu.cmu.cs.varex


import org.junit.{Ignore, Test}

class PHPTest_func extends AbstractPHPTest {

            @Test def test324_001() { testFile("phptest/src/test/resources/func/001.phpt") }
  @Test def test325_002() { testFile("phptest/src/test/resources/func/002.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test326_003() { testFile("phptest/src/test/resources/func/003.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test327_004() { testFile("phptest/src/test/resources/func/004.phpt") }
  @Test def test328_005() { testFile("phptest/src/test/resources/func/005.phpt") }
  @Ignore("ignore infinite loop")@Test def test329_005a() { testFile("phptest/src/test/resources/func/005a.phpt") }
  @Test def test330_006() { testFile("phptest/src/test/resources/func/006.phpt") }
  @Test def test331_007() { testFile("phptest/src/test/resources/func/007.phpt") }
  @Test def test332_008() { testFile("phptest/src/test/resources/func/008.phpt") }
  @Test def test333_009() { testFile("phptest/src/test/resources/func/009.phpt") }
  @Test def test334_010() { testFile("phptest/src/test/resources/func/010.phpt") }
  @Test def test335_ini_alter() { testFile("phptest/src/test/resources/func/ini_alter.phpt") }
  }