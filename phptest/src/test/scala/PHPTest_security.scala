
package edu.cmu.cs.varex


import org.junit.{Ignore, Test}

@Ignore
class PHPTest_security extends AbstractPHPTest {

            @Test def test677_magic_quotes_gpc() { testFile("phptest/src/test/resources/security/magic_quotes_gpc.phpt") }
  @Test def test678_open_basedir_chdir() { testFile("phptest/src/test/resources/security/open_basedir_chdir.phpt") }
  @Test def test679_open_basedir_chmod() { testFile("phptest/src/test/resources/security/open_basedir_chmod.phpt") }
  @Test def test680_open_basedir_copy() { testFile("phptest/src/test/resources/security/open_basedir_copy.phpt") }
  @Test def test681_open_basedir_copy_variation1() { testFile("phptest/src/test/resources/security/open_basedir_copy_variation1.phpt") }
  @Test def test682_open_basedir_dir() { testFile("phptest/src/test/resources/security/open_basedir_dir.phpt") }
  @Test def test683_open_basedir_disk_free_space() { testFile("phptest/src/test/resources/security/open_basedir_disk_free_space.phpt") }
  @Test def test684_open_basedir_error_log() { testFile("phptest/src/test/resources/security/open_basedir_error_log.phpt") }
  @Test def test685_open_basedir_error_log_variation() { testFile("phptest/src/test/resources/security/open_basedir_error_log_variation.phpt") }
  @Test def test686_open_basedir_file() { testFile("phptest/src/test/resources/security/open_basedir_file.phpt") }
  @Test def test687_open_basedir_fileatime() { testFile("phptest/src/test/resources/security/open_basedir_fileatime.phpt") }
  @Test def test688_open_basedir_filectime() { testFile("phptest/src/test/resources/security/open_basedir_filectime.phpt") }
  @Test def test689_open_basedir_filegroup() { testFile("phptest/src/test/resources/security/open_basedir_filegroup.phpt") }
  @Test def test690_open_basedir_fileinode() { testFile("phptest/src/test/resources/security/open_basedir_fileinode.phpt") }
  @Test def test691_open_basedir_filemtime() { testFile("phptest/src/test/resources/security/open_basedir_filemtime.phpt") }
  @Test def test692_open_basedir_fileowner() { testFile("phptest/src/test/resources/security/open_basedir_fileowner.phpt") }
  @Test def test693_open_basedir_fileperms() { testFile("phptest/src/test/resources/security/open_basedir_fileperms.phpt") }
  @Test def test694_open_basedir_filesize() { testFile("phptest/src/test/resources/security/open_basedir_filesize.phpt") }
  @Test def test695_open_basedir_filetype() { testFile("phptest/src/test/resources/security/open_basedir_filetype.phpt") }
  @Test def test696_open_basedir_file_exists() { testFile("phptest/src/test/resources/security/open_basedir_file_exists.phpt") }
  @Test def test697_open_basedir_file_get_contents() { testFile("phptest/src/test/resources/security/open_basedir_file_get_contents.phpt") }
  @Test def test698_open_basedir_file_put_contents() { testFile("phptest/src/test/resources/security/open_basedir_file_put_contents.phpt") }
  @Test def test699_open_basedir_fopen() { testFile("phptest/src/test/resources/security/open_basedir_fopen.phpt") }
  @Ignore("SKIPIF not supported")@Test def test700_open_basedir_glob_win32() { testFile("phptest/src/test/resources/security/open_basedir_glob-win32.phpt") }
  @Ignore("SKIPIF not supported")@Test def test701_open_basedir_glob() { testFile("phptest/src/test/resources/security/open_basedir_glob.phpt") }
  @Test def test702_open_basedir_glob_variation() { testFile("phptest/src/test/resources/security/open_basedir_glob_variation.phpt") }
  @Test def test703_open_basedir_is_dir() { testFile("phptest/src/test/resources/security/open_basedir_is_dir.phpt") }
  @Test def test704_open_basedir_is_executable() { testFile("phptest/src/test/resources/security/open_basedir_is_executable.phpt") }
  @Test def test705_open_basedir_is_file() { testFile("phptest/src/test/resources/security/open_basedir_is_file.phpt") }
  @Test def test706_open_basedir_is_link() { testFile("phptest/src/test/resources/security/open_basedir_is_link.phpt") }
  @Test def test707_open_basedir_is_readable() { testFile("phptest/src/test/resources/security/open_basedir_is_readable.phpt") }
  @Test def test708_open_basedir_is_writable() { testFile("phptest/src/test/resources/security/open_basedir_is_writable.phpt") }
  @Ignore("SKIPIF not supported")@Test def test709_open_basedir_link() { testFile("phptest/src/test/resources/security/open_basedir_link.phpt") }
  @Ignore("SKIPIF not supported")@Test def test710_open_basedir_linkinfo() { testFile("phptest/src/test/resources/security/open_basedir_linkinfo.phpt") }
  @Test def test711_open_basedir_lstat() { testFile("phptest/src/test/resources/security/open_basedir_lstat.phpt") }
  @Ignore("SKIPIF not supported")@Test def test712_open_basedir_mkdir() { testFile("phptest/src/test/resources/security/open_basedir_mkdir.phpt") }
  @Test def test713_open_basedir_opendir() { testFile("phptest/src/test/resources/security/open_basedir_opendir.phpt") }
  @Ignore("SKIPIF not supported")@Test def test714_open_basedir_parse_ini_file() { testFile("phptest/src/test/resources/security/open_basedir_parse_ini_file.phpt") }
  @Ignore("SKIPIF not supported")@Test def test715_open_basedir_readlink() { testFile("phptest/src/test/resources/security/open_basedir_readlink.phpt") }
  @Ignore("SKIPIF not supported")@Test def test716_open_basedir_realpath() { testFile("phptest/src/test/resources/security/open_basedir_realpath.phpt") }
  @Test def test717_open_basedir_rename() { testFile("phptest/src/test/resources/security/open_basedir_rename.phpt") }
  @Test def test718_open_basedir_rmdir() { testFile("phptest/src/test/resources/security/open_basedir_rmdir.phpt") }
  @Test def test719_open_basedir_scandir() { testFile("phptest/src/test/resources/security/open_basedir_scandir.phpt") }
  @Test def test720_open_basedir_stat() { testFile("phptest/src/test/resources/security/open_basedir_stat.phpt") }
  @Ignore("SKIPIF not supported")@Test def test721_open_basedir_symlink() { testFile("phptest/src/test/resources/security/open_basedir_symlink.phpt") }
  @Test def test722_open_basedir_tempnam() { testFile("phptest/src/test/resources/security/open_basedir_tempnam.phpt") }
  @Test def test723_open_basedir_touch() { testFile("phptest/src/test/resources/security/open_basedir_touch.phpt") }
  @Test def test724_open_basedir_unlink() { testFile("phptest/src/test/resources/security/open_basedir_unlink.phpt") }
  }