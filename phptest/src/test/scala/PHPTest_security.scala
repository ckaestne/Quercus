
package edu.cmu.cs.varex


import org.junit.{Ignore, Test}

class PHPTest_security extends AbstractPHPTest {

            @Test def test677_magic_quotes_gpc() { testFile("phptest/src/test/resources/security/magic_quotes_gpc.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test678_open_basedir_chdir() { testFile("phptest/src/test/resources/security/open_basedir_chdir.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test679_open_basedir_chmod() { testFile("phptest/src/test/resources/security/open_basedir_chmod.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test680_open_basedir_copy() { testFile("phptest/src/test/resources/security/open_basedir_copy.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test681_open_basedir_copy_variation1() { testFile("phptest/src/test/resources/security/open_basedir_copy_variation1.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test682_open_basedir_dir() { testFile("phptest/src/test/resources/security/open_basedir_dir.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test683_open_basedir_disk_free_space() { testFile("phptest/src/test/resources/security/open_basedir_disk_free_space.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test684_open_basedir_error_log() { testFile("phptest/src/test/resources/security/open_basedir_error_log.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test685_open_basedir_error_log_variation() { testFile("phptest/src/test/resources/security/open_basedir_error_log_variation.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test686_open_basedir_file() { testFile("phptest/src/test/resources/security/open_basedir_file.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test687_open_basedir_fileatime() { testFile("phptest/src/test/resources/security/open_basedir_fileatime.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test688_open_basedir_filectime() { testFile("phptest/src/test/resources/security/open_basedir_filectime.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test689_open_basedir_filegroup() { testFile("phptest/src/test/resources/security/open_basedir_filegroup.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test690_open_basedir_fileinode() { testFile("phptest/src/test/resources/security/open_basedir_fileinode.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test691_open_basedir_filemtime() { testFile("phptest/src/test/resources/security/open_basedir_filemtime.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test692_open_basedir_fileowner() { testFile("phptest/src/test/resources/security/open_basedir_fileowner.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test693_open_basedir_fileperms() { testFile("phptest/src/test/resources/security/open_basedir_fileperms.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test694_open_basedir_filesize() { testFile("phptest/src/test/resources/security/open_basedir_filesize.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test695_open_basedir_filetype() { testFile("phptest/src/test/resources/security/open_basedir_filetype.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test696_open_basedir_file_exists() { testFile("phptest/src/test/resources/security/open_basedir_file_exists.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test697_open_basedir_file_get_contents() { testFile("phptest/src/test/resources/security/open_basedir_file_get_contents.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test698_open_basedir_file_put_contents() { testFile("phptest/src/test/resources/security/open_basedir_file_put_contents.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test699_open_basedir_fopen() { testFile("phptest/src/test/resources/security/open_basedir_fopen.phpt") }
  @Ignore("SKIPIF not supported")@Test def test700_open_basedir_glob_win32() { testFile("phptest/src/test/resources/security/open_basedir_glob-win32.phpt") }
  @Ignore("SKIPIF not supported")@Test def test701_open_basedir_glob() { testFile("phptest/src/test/resources/security/open_basedir_glob.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test702_open_basedir_glob_variation() { testFile("phptest/src/test/resources/security/open_basedir_glob_variation.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test703_open_basedir_is_dir() { testFile("phptest/src/test/resources/security/open_basedir_is_dir.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test704_open_basedir_is_executable() { testFile("phptest/src/test/resources/security/open_basedir_is_executable.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test705_open_basedir_is_file() { testFile("phptest/src/test/resources/security/open_basedir_is_file.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test706_open_basedir_is_link() { testFile("phptest/src/test/resources/security/open_basedir_is_link.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test707_open_basedir_is_readable() { testFile("phptest/src/test/resources/security/open_basedir_is_readable.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test708_open_basedir_is_writable() { testFile("phptest/src/test/resources/security/open_basedir_is_writable.phpt") }
  @Ignore("SKIPIF not supported")@Test def test709_open_basedir_link() { testFile("phptest/src/test/resources/security/open_basedir_link.phpt") }
  @Ignore("SKIPIF not supported")@Test def test710_open_basedir_linkinfo() { testFile("phptest/src/test/resources/security/open_basedir_linkinfo.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test711_open_basedir_lstat() { testFile("phptest/src/test/resources/security/open_basedir_lstat.phpt") }
  @Ignore("SKIPIF not supported")@Test def test712_open_basedir_mkdir() { testFile("phptest/src/test/resources/security/open_basedir_mkdir.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test713_open_basedir_opendir() { testFile("phptest/src/test/resources/security/open_basedir_opendir.phpt") }
  @Ignore("SKIPIF not supported")@Test def test714_open_basedir_parse_ini_file() { testFile("phptest/src/test/resources/security/open_basedir_parse_ini_file.phpt") }
  @Ignore("SKIPIF not supported")@Test def test715_open_basedir_readlink() { testFile("phptest/src/test/resources/security/open_basedir_readlink.phpt") }
  @Ignore("SKIPIF not supported")@Test def test716_open_basedir_realpath() { testFile("phptest/src/test/resources/security/open_basedir_realpath.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test717_open_basedir_rename() { testFile("phptest/src/test/resources/security/open_basedir_rename.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test718_open_basedir_rmdir() { testFile("phptest/src/test/resources/security/open_basedir_rmdir.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test719_open_basedir_scandir() { testFile("phptest/src/test/resources/security/open_basedir_scandir.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test720_open_basedir_stat() { testFile("phptest/src/test/resources/security/open_basedir_stat.phpt") }
  @Ignore("SKIPIF not supported")@Test def test721_open_basedir_symlink() { testFile("phptest/src/test/resources/security/open_basedir_symlink.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test722_open_basedir_tempnam() { testFile("phptest/src/test/resources/security/open_basedir_tempnam.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test723_open_basedir_touch() { testFile("phptest/src/test/resources/security/open_basedir_touch.phpt") }
  @Ignore("FAILING: This test is failing with Quercus baseline")@Test def test724_open_basedir_unlink() { testFile("phptest/src/test/resources/security/open_basedir_unlink.phpt") }
  }