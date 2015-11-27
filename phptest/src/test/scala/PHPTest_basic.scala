
package edu.cmu.cs.varex


import org.junit.{Ignore, Test}

class PHPTest_basic extends AbstractPHPTest {

            @Test def test1_001() { testFile("phptest/src/test/resources/basic/001.phpt") }
  @Test def test2_002() { testFile("phptest/src/test/resources/basic/002.phpt") }
  @Test def test3_003() { testFile("phptest/src/test/resources/basic/003.phpt") }
  @Test def test4_004() { testFile("phptest/src/test/resources/basic/004.phpt") }
  @Test def test5_005() { testFile("phptest/src/test/resources/basic/005.phpt") }
  @Test def test6_006() { testFile("phptest/src/test/resources/basic/006.phpt") }
  @Test def test7_007() { testFile("phptest/src/test/resources/basic/007.phpt") }
  @Test def test8_008() { testFile("phptest/src/test/resources/basic/008.phpt") }
  @Test def test9_009() { testFile("phptest/src/test/resources/basic/009.phpt") }
  @Test def test10_010() { testFile("phptest/src/test/resources/basic/010.phpt") }
  @Test def test11_011() { testFile("phptest/src/test/resources/basic/011.phpt") }
  @Ignore("requires ARGS")@Test def test12_012() { testFile("phptest/src/test/resources/basic/012.phpt") }
  @Test def test13_013() { testFile("phptest/src/test/resources/basic/013.phpt") }
  @Test def test14_014() { testFile("phptest/src/test/resources/basic/014.phpt") }
  @Test def test15_015() { testFile("phptest/src/test/resources/basic/015.phpt") }
  @Test def test16_016() { testFile("phptest/src/test/resources/basic/016.phpt") }
  @Test def test17_017() { testFile("phptest/src/test/resources/basic/017.phpt") }
  @Test def test18_018() { testFile("phptest/src/test/resources/basic/018.phpt") }
  @Test def test19_019() { testFile("phptest/src/test/resources/basic/019.phpt") }
  @Test def test20_020() { testFile("phptest/src/test/resources/basic/020.phpt") }
  @Test def broken21_021() { testFile("phptest/src/test/resources/basic/021.phpt") }
  @Ignore("cookies not correctly supported")@Test def test22_022() { testFile("phptest/src/test/resources/basic/022.phpt") }
  @Ignore("cookies not correctly supported")@Test def test23_023() { testFile("phptest/src/test/resources/basic/023.phpt") }
  @Test def test24_024() { testFile("phptest/src/test/resources/basic/024.phpt") }
  @Ignore("marked to ignore")@Test def test25_025() { testFile("phptest/src/test/resources/basic/025.phpt") }
  @Test def test26_026() { testFile("phptest/src/test/resources/basic/026.phpt") }
  @Ignore("marked to ignore")@Test def test27_027() { testFile("phptest/src/test/resources/basic/027.phpt") }
  @Ignore("marked to ignore")@Test def test28_bug20539() { testFile("phptest/src/test/resources/basic/bug20539.phpt") }
  @Test def test29_bug29971() { testFile("phptest/src/test/resources/basic/bug29971.phpt") }
  @Ignore("marked to ignore")@Test def test30_bug45986() { testFile("phptest/src/test/resources/basic/bug45986.phpt") }
  @Ignore("SKIPIF not supported")@Test def test31_bug46313_win() { testFile("phptest/src/test/resources/basic/bug46313-win.phpt") }
  @Ignore("SKIPIF not supported")@Test def test32_bug46313() { testFile("phptest/src/test/resources/basic/bug46313.phpt") }
  @Test def test33_bug46759() { testFile("phptest/src/test/resources/basic/bug46759.phpt") }
  @Ignore("marked to ignore")@Test def test34_php_egg_logo_guid() { testFile("phptest/src/test/resources/basic/php_egg_logo_guid.phpt") }
  @Ignore("marked to ignore")@Test def test35_php_logo_guid() { testFile("phptest/src/test/resources/basic/php_logo_guid.phpt") }
  @Ignore("marked to ignore")@Test def test36_php_real_logo_guid() { testFile("phptest/src/test/resources/basic/php_real_logo_guid.phpt") }
  @Test def broken37_rfc1867_anonymous_upload() { testFile("phptest/src/test/resources/basic/rfc1867_anonymous_upload.phpt") }
  @Test def broken38_rfc1867_array_upload() { testFile("phptest/src/test/resources/basic/rfc1867_array_upload.phpt") }
  @Test def test39_rfc1867_boundary_1() { testFile("phptest/src/test/resources/basic/rfc1867_boundary_1.phpt") }
  @Test def test40_rfc1867_boundary_2() { testFile("phptest/src/test/resources/basic/rfc1867_boundary_2.phpt") }
  @Test def broken41_rfc1867_empty_upload() { testFile("phptest/src/test/resources/basic/rfc1867_empty_upload.phpt") }
  @Test def test42_rfc1867_file_upload_disabled() { testFile("phptest/src/test/resources/basic/rfc1867_file_upload_disabled.phpt") }
  @Ignore("marked to ignore")@Test def test43_rfc1867_garbled_mime_headers() { testFile("phptest/src/test/resources/basic/rfc1867_garbled_mime_headers.phpt") }
  @Ignore("marked to ignore")@Test def test44_rfc1867_invalid_boundary() { testFile("phptest/src/test/resources/basic/rfc1867_invalid_boundary.phpt") }
  @Test def test45_rfc1867_malicious_input() { testFile("phptest/src/test/resources/basic/rfc1867_malicious_input.phpt") }
  @Ignore("marked to ignore")@Test def test46_rfc1867_max_file_size() { testFile("phptest/src/test/resources/basic/rfc1867_max_file_size.phpt") }
  @Ignore("marked to ignore")@Test def test47_rfc1867_missing_boundary() { testFile("phptest/src/test/resources/basic/rfc1867_missing_boundary.phpt") }
  @Ignore("marked to ignore")@Test def test48_rfc1867_missing_boundary_2() { testFile("phptest/src/test/resources/basic/rfc1867_missing_boundary_2.phpt") }
  @Ignore("marked to ignore")@Test def test49_rfc1867_post_max_filesize() { testFile("phptest/src/test/resources/basic/rfc1867_post_max_filesize.phpt") }
  @Ignore("marked to ignore")@Test def test50_rfc1867_post_max_size() { testFile("phptest/src/test/resources/basic/rfc1867_post_max_size.phpt") }
  @Ignore("marked to ignore")@Test def test51_zend_logo_guid() { testFile("phptest/src/test/resources/basic/zend_logo_guid.phpt") }
  }