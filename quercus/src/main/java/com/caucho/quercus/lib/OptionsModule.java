/*
 * Copyright (c) 1998-2006 Caucho Technology -- all rights reserved
 *
 * This file is part of Resin(R) Open Source
 *
 * Each copy or derived work must preserve the copyright notice and this
 * notice unmodified.
 *
 * Resin Open Source is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Resin Open Source is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, or any warranty
 * of NON-INFRINGEMENT.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Resin Open Source; if not, write to the
 *
 *   Free Software Foundation, Inc.
 *   59 Temple Place, Suite 330
 *   Boston, MA 02111-1307  USA
 *
 * @author Scott Ferguson
 */

package com.caucho.quercus.lib;

import com.caucho.quercus.Quercus;
import com.caucho.quercus.QuercusModuleException;
import com.caucho.quercus.annotation.Optional;
import com.caucho.quercus.annotation.UsesSymbolTable;
import com.caucho.quercus.env.*;
import com.caucho.quercus.lib.file.FileModule;
import com.caucho.quercus.module.AbstractQuercusModule;
import com.caucho.quercus.program.QuercusProgram;
import com.caucho.util.L10N;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * PHP options
 */
public class OptionsModule extends AbstractQuercusModule {
  private static final L10N L = new L10N(OptionsModule.class);

  // php/1a0q (phpMyAdmin)
  public static final String PHP_OS
    = System.getProperty("os.name").toUpperCase();

  public static final int ASSERT_ACTIVE = 1;
  public static final int ASSERT_CALLBACK = 2;
  public static final int ASSERT_BAIL = 3;
  public static final int ASSERT_WARNING = 4;
  public static final int ASSERT_QUIET_EVAL = 5;

  public static final int CREDITS_GROUP = 1;
  public static final int CREDITS_GENERAL = 2;
  public static final int CREDITS_SAPI = 4;
  public static final int CREDITS_MODULES = 8;
  public static final int CREDITS_DOCS = 16;
  public static final int CREDITS_FULLPAGE = 32;
  public static final int CREDITS_QA = 64;
  public static final int CREDITS_ALL = -1;


  public static final int INFO_GENERAL = 1;
  public static final int INFO_CREDITS = 2;
  public static final int INFO_CONFIGURATION = 4;
  public static final int INFO_MODULES = 8;
  public static final int INFO_ENVIRONMENT = 16;
  public static final int INFO_VARIABLES = 32;
  public static final int INFO_LICENSE = 64;
  public static final int INFO_ALL = -1;

  private static final HashMap<String,StringValue> _iniMap
    = new HashMap<String,StringValue>();

  /**
   * Returns the default quercus.ini values.
   */
  public Map<String,StringValue> getDefaultIni()
  {
    return _iniMap;
  }

  /**
   * Checks the assertion
   */
  @UsesSymbolTable
  public static Value quercus_assert(Env env, String code)
  {
    try {
      Quercus quercus = env.getQuercus();
      
      QuercusProgram program = quercus.parseCode(code);

      program = program.createExprReturn();
      
      Value value = program.execute(env);

      boolean result = value.toBoolean();

      if (!result) {
        env.warning(L.l("Assertion \"{0}\" failed", code));
        return NullValue.NULL;
      }

      return BooleanValue.TRUE;
      
    } catch (IOException e) {
      throw new QuercusModuleException(e);
    }
  }

  /**
   * Checks the assertion
   */
  public static Value assert_options(Env env,
				     int code,
				     @Optional("-1") Value value)
  {
    Value result = null;
    Value longResult = null;
    
    if (value.equals(LongValue.MINUS_ONE)) {
      switch (code) {
      case ASSERT_ACTIVE:
	longResult = env.getIni("assert.active");
	break;
      case ASSERT_WARNING:
	longResult = env.getIni("assert.warning");
	break;
      case ASSERT_BAIL:
	longResult = env.getIni("assert.bail");
	break;
      case ASSERT_QUIET_EVAL:
	longResult = env.getIni("assert.quiet_eval");
	break;
      case ASSERT_CALLBACK:
	result = env.getIni("assert.callback");
	break;
      default:
        env.warning(L.l("unknown value {0}", code));
        result = BooleanValue.FALSE;
	break;
      }
    }
    else {
      switch (code) {
      case ASSERT_ACTIVE:
	longResult = env.setIni("assert.active", value.toString());
	break;
      case ASSERT_WARNING:
	longResult = env.setIni("assert.warning", value.toString());
	break;
      case ASSERT_BAIL:
	longResult = env.setIni("assert.bail", value.toString());
	break;
      case ASSERT_QUIET_EVAL:
	longResult = env.setIni("assert.quiet_eval", value.toString());
	break;
      case ASSERT_CALLBACK:
	result = env.setIni("assert.callback", value.toString());
	break;
      default:
        env.warning(L.l("unknown value {0}", code));
	result = BooleanValue.FALSE;
	break;
      }
    }

    if (longResult != null)
      return longResult.toLongValue();
    else if (result != null)
      return result;
    else
      return NullValue.NULL;
  }

  /**
   * Returns true if the given extension is loaded
   */
  public static boolean extension_loaded(Env env, String ext)
  {
    return env.isExtensionLoaded(ext);
  }

  /**
   * Returns true if the given extension is loaded
   */
  public static Value get_loaded_extensions(Env env)
  {
    ArrayValue value = new ArrayValueImpl();

    for (String ext : env.getLoadedExtensions()) {
      value.put(ext);
    }

    return value;
  }

  /**
   * Stubs the dl.
   */
  public static boolean dl(Env env, String dl)
  {
    env.stub("dl is stubbed for dl(" + dl + ")");

    return false;
  }

  /**
   * Sets an environment name/value pair.
   */
  public static boolean putenv(Env env, StringValue settings)
  {
    int eqIndex = settings.indexOf('=');

    if (eqIndex < 0)
      return false;

    StringValue key = settings.substring(0, eqIndex);
    StringValue val = settings.substring(eqIndex + 1);

    env.getQuercus().setServerEnv(key, val);

    return true;
  }

  /**
   * Gets an environment value.
   */
  public static Value getenv(Env env, StringValue key)
  {
    Value val = env.getQuercus().getServerEnv(key);

    if (val == null) {
      ArrayValue serverVars = env.getGlobalVar("_SERVER").toArrayValue(env);
      val = serverVars.get(key);
    }

    if (val == null || ! val.isset())
      return BooleanValue.FALSE;

    return val;
  }

  /**
   * Returns the configuration value of a configuration.
   */
  public static Value get_cfg_var(Env env, String name)
  {
    Value value = env.getConfigVar(name);

    if (value != null)
      return value;
    else
      return NullValue.NULL;
  }

  /**
   * Returns the owner of the current script.
   */
  public static String get_current_user(Env env)
  {
    env.stub("get_current_user");
    
    return String.valueOf(env.getSelfPath().getOwner());
  }

  /**
   * Returns the constants as an array
   */
  public static Value get_defined_constants(Env env)
  {
    return env.getDefinedConstants();
  }

  /**
   * Returns the include path
   */
  public static Value get_include_path(Env env)
  {
    Value value = env.getIni("include_path");

    if (value != null)
      return value;
    else
      return StringValue.EMPTY;
  }

  /**
   * Returns extension function with a given name.
   */
  public static Value get_extension_funcs(Env env, String name)
  {
    return env.getExtensionFuncs(name);
  }

  /**
   * Gets the magic quotes value.
   */
  public static Value get_magic_quotes_gpc(Env env)
  {
    return (env.getIniBoolean("magic_quotes_gpc")
	    ? BooleanValue.TRUE
	    : BooleanValue.FALSE);
  }

  /**
   * Gets the magic quotes runtime value.
   */
  public static Value get_magic_quotes_runtime(Env env)
  {
    return BooleanValue.FALSE; // PHP 6 removes, so we don't support
  }

  /**
   * Gets the magic quotes value.
   */
  public static Value magic_quotes_runtime(Env env)
  {
    return BooleanValue.FALSE; // PHP 6 removes, so we don't support
  }

  /**
   * Returns the gid for the script path.
   */
  public static Value getlastmod(Env env)
  {
    return FileModule.filemtime(env, env.getSelfPath());
  }

  /**
   * Returns the gid for the script path.
   */
  public static Value getmygid(Env env)
  {
    return FileModule.filegroup(env, env.getSelfPath());
  }

  /**
   * Returns the inode for the script path.
   */
  public static Value getmyinode(Env env)
  {
    return FileModule.fileinode(env, env.getSelfPath());
  }

  /**
   * Returns the uid for the script path.
   */
  public static Value getmyuid(Env env)
  {
    return FileModule.fileowner(env, env.getSelfPath());
  }

  /**
   * Returns the thread for the script.
   */
  public static long getmypid(Env env)
  {
    return Thread.currentThread().getId();
  }


  /**
   * Sets an initialization value.
   */
  public static Value ini_alter(Env env, String varName, String value)
  {
    return ini_set(env, varName, value);
  }

  /**
   * Returns an initialization value.
   */
  public static String ini_get(Env env, String varName)
  {
    StringValue v = env.getIni(varName);

    if (v != null)
      return v.toString();
    else
      return "";
  }

  /**
   * Returns all initialization values.
   * XXX: access levels dependent on PHP_INI, PHP_INI_PERDIR, PHP_INI_SYSTEM.
   *
   * @param extension assumes ini values are prefixed by extension names.
   */
  public static Value ini_get_all(Env env,
                       @Optional() String extension)
  {
    if (extension.length() > 0) {
      if (! env.isExtensionLoaded(extension)) {
        env.warning(L.l("extension '" + extension + "' not loaded."));
        return BooleanValue.FALSE;
      }
      extension += ".";
    }
  
    return getAllDirectives(env, extension);
  }

  private static Value getAllDirectives(Env env, String prefix)
  {
    ArrayValue directives = new ArrayValueImpl();

    Value global = new StringValueImpl("global_value");
    Value local = new StringValueImpl("local_value");
    Value access = new StringValueImpl("access");

    Value level = new LongValue(7);

    HashMap<String, StringValue> iniMap =
        env.getQuercus().getIniAll(prefix);

    for (Map.Entry<String,StringValue> entry : iniMap.entrySet()) {
      ArrayValue inner = new ArrayValueImpl();
      
      String key = entry.getKey();
      Value globalVal = entry.getValue();

      if (globalVal == null)
        inner.put(NullValue.NULL);
      else {
        globalVal = formatIniValue(globalVal);
        inner.put(global, globalVal);
      }

      Value localVal = env.getIni(key);
      if (localVal == null)
        inner.put(local, globalVal);
      else
        inner.put(local, formatIniValue(localVal));

      inner.put(access, level);
      directives.put(new StringValueImpl(key), inner);
    }

    return directives;
  }
  
  private static Value formatIniValue(Value val)
  {
    String string = val.toString().toLowerCase();
    if ("on".equals(string))
      return new StringValueImpl("1");
    else if ("off".equals(string))
      return StringValue.EMPTY;

    return val;
  }

  /**
   * Restore the initial configuration value
   */
  public static Value ini_restore(Env env, String name)
  {
    Value value = env.getConfigVar(name);

    if (value != null)
      env.setIni(name, value.toString());

    return NullValue.NULL;
  }

  /**
   * Sets an initialization value.
   */
  public static StringValue ini_set(Env env, String varName, String value)
  {
    StringValue oldValue = env.setIni(varName, value);

    if (oldValue != null)
      return oldValue;
    else
      return StringValue.EMPTY;
  }

  /**
   * Returns the sapi type.
   */
  public static String php_sapi_name()
  {
    return "apache";
  }

  public static void phpinfo(Env env, @Optional("-1") int what)
  {
    if ((what & INFO_GENERAL) != 0)
      phpinfoGeneral(env);
    if ((what & INFO_VARIABLES) != 0)
      phpinfoVariables(env);
  }

  private static void phpinfoGeneral(Env env)
  {
    if (env.hasRequest())
      env.println("<h1>Quercus</h1>");
    else
      env.println("Quercus");

    if (env.hasRequest()) {
      env.println("<pre>");
    }
    
    env.println("PHP Version => " + phpversion(env, "std"));
    env.println("System => " + System.getProperty("os.name") + " "
	      + System.getProperty("os.version") + " "
	      + System.getProperty("os.arch"));
    env.println("Build Date => " + env.getQuercus().getVersionDate());
    env.println("Configure Command => n/a");
    env.println("Server API => CGI");
    env.println("Virtual Directory Support => disabled");
    env.println("Configuration File (php.ini) Path => WEB-INF/php.ini");
    env.println("PHP API => 20031224");
    env.println("PHP Extension => 20041030");
    env.println("Debug Build => no");
    env.println("Thread Safety => enabled");
    env.println("Registered PHP Streams => php, file, http, https");
    
    if (env.hasRequest()) {
      env.print("</pre>");
    }
  }
  
  private static void phpinfoVariables(Env env)
  {
    if (env.hasRequest()) {
      env.println("<h2>PHP Variables</h2");
      env.println("<table>");
      env.println("<tr><th>Variable</th><th>Value</th></tr>");
    }
    else {
      env.println("Variable => Value");
    }
    
    if (env.hasRequest()) {
      phpinfoVariable(env, "_REQUEST", env.getSpecialRef("_REQUEST"));
      phpinfoVariable(env, "_GET", env.getSpecialRef("_GET"));
      phpinfoVariable(env, "_POST", env.getSpecialRef("_POST"));
    }

    phpinfoVariable(env, "_SERVER", env.getSpecialRef("_SERVER"));
    
    if (env.hasRequest())
      env.print("</table>");
    
    env.println();
  }
  
  private static void phpinfoVariable(Env env, String name, Value value)
  {
    if (value.isArray()) {
      ArrayValue array = value.toArrayValue(env);
      
      for (Map.Entry<Value,Value> entry : array.entrySet()) {
        Value key = escape(env, entry.getKey());
        
        if (env.hasRequest())
          env.print("<tr><td>");
        
        env.print(name + "[\"" + key + "\"]");
        
        if (env.hasRequest())
          env.println("</td><td>");
        else
          env.print(" => ");
        
        phpinfoVariable(env, entry.getValue());
        
        if (env.hasRequest())
          env.println("</td></tr>");
      }
    }
    else {
      if (env.hasRequest())
        env.println("<tr><td>" + name + "</td><td>");
      
      phpinfoVariable(env, value);
      
      if (env.hasRequest())
        env.println("</td></tr>");
    }
  }
  
  private static void phpinfoVariable(Env env, Value value)
  {
    if (value.isString()) {
      env.println(escape(env, value).toString());
    }
    else {
      if (env.hasRequest())
        env.print("<pre>");
      
      VariableModule.var_dump(env, escape(env, value));
      
      if (env.hasRequest())
        env.print("</pre>");
    }
  }
  
  private static Value escape(Env env, Value value)
  {
    if (value.isArray()) {
      ArrayValue array = value.toArrayValue(env);
      
      ArrayValue result = new ArrayValueImpl();
      
      for (Map.Entry<Value,Value> entry : array.entrySet()) {
        Value key = escape(env, entry.getKey());
        Value val = escape(env, entry.getValue());
        
        result.put(key, value);
      }
      
      return result;
    }
    else if (value.isObject()) {
      ObjectValue obj = (ObjectValue)value.toObject(env);
      
      ObjectValue result = new ObjectExtValue(obj.getQuercusClass());
      
      for (Map.Entry<String,Value> entry : obj.entrySet()) {
        Value key = escape(env, StringValue.create(entry.getKey()));
        Value val = escape(env, entry.getValue());
        
        result.putField(env, key.toString(), val);
      }
      
      return result;
    }
    else {
      return HtmlModule.htmlspecialchars(env,
                                         value,
                                         DefaultValue.DEFAULT,
                                         DefaultValue.DEFAULT);
    }
  }

  /**
   * Returns system information
   */
  public static String php_uname(@Optional("'a'") String mode)
  {
    // XXX: stubbed

    if (mode == null || mode.equals(""))
      mode = "a";

    switch (mode.charAt(0)) {
    case 's':
      return PHP_OS;

    case 'n':
      return "localhost";

    case 'r':
      return "2.4.0";

    case 'v':
      return "Version 2.4.0";

    case 'm':
      return "i386";

    case 'a':
    default:
      return (php_uname("s") + " " +
              php_uname("n") + " " +
              php_uname("r") + " " +
              php_uname("v") + " " +
              php_uname("m"));
    }
  }

  /**
   * Returns the quercus version.
   */
  public static String phpversion(Env env, @Optional String module)
  {
    return env.getQuercus().getPhpVersion();
  }

  /**
   * Sets the include path
   */
  public static String set_include_path(Env env, String includePath)
  {
    return env.setIncludePath(includePath);
  }

  /**
   * Sets the include path
   */
  public static Value restore_include_path(Env env)
  {
    env.restoreIncludePath();

    return NullValue.NULL;
  }

  /**
   * Sets the magic quotes value.
   */
  public static Value set_magic_quotes_runtime(Env env, Value value)
  {
    return BooleanValue.FALSE; // PHP 6 removes magic_quotes
  }

  /**
   * Sets the time limit
   */
  public static Value set_time_limit(Env env, long seconds)
  {
    env.setTimeLimit(seconds * 1000L);

    return NullValue.NULL;
  }

  /**
   * Compares versions
   */
  public static Value version_compare(String version1,
                                      String version2,
                                      @Optional("cmp") String op)
  {
    ArrayList<Value> expanded1 = expandVersion(version1);
    ArrayList<Value> expanded2 = expandVersion(version2);

    int cmp = compareTo(expanded1, expanded2);

    if ("eq".equals(op) || "==".equals(op) || "=".equals(op))
      return cmp == 0 ? BooleanValue.TRUE : BooleanValue.FALSE;
    else if ("ne".equals(op) || "!=".equals(op) || "<>".equals(op))
      return cmp != 0 ? BooleanValue.TRUE : BooleanValue.FALSE;
    else if ("lt".equals(op) || "<".equals(op))
      return cmp < 0 ? BooleanValue.TRUE : BooleanValue.FALSE;
    else if ("le".equals(op) || "<=".equals(op))
      return cmp <= 0 ? BooleanValue.TRUE : BooleanValue.FALSE;
    else if ("gt".equals(op) || ">".equals(op))
      return cmp > 0 ? BooleanValue.TRUE : BooleanValue.FALSE;
    else if ("ge".equals(op) || ">=".equals(op))
      return cmp >= 0 ? BooleanValue.TRUE : BooleanValue.FALSE;
    else {
      if (cmp == 0)
        return new LongValue(0);
      else if (cmp < 0)
        return new LongValue(-1);
      else
        return new LongValue(1);
    }
  }

  public static String quercus_quercus_version(Env env)
  {
    return env.getQuercus().getVersion();
  }

  public static String zend_version()
  {
    return "2.0.4";
  }

  private static ArrayList<Value> expandVersion(String version)
  {
    ArrayList<Value> expand = new ArrayList<Value>();

    int len = version.length();
    int i = 0;

    while (i < len) {
      char ch = version.charAt(i);

      if ('0' <= ch && ch <= '9') {
        int value = 0;

        for (; i < len && '0' <= (ch = version.charAt(i)) && ch <= '9'; i++) {
          value = 10 * value + ch - '0';
        }

        expand.add(new LongValue(value));
      }
      else if (Character.isLetter((char) ch)) {
        StringBuilder sb = new StringBuilder();

        for (; i < len && Character.isLetter(version.charAt(i)); i++) {
          sb.append((char) ch);
        }

        String s = sb.toString();

        if (s.equals("dev"))
          s = "a";
        else if (s.equals("alpha") || s.equals("a"))
          s = "b";
        else if (s.equals("beta") || s.equals("b"))
          s = "c";
        else if (s.equals("RC"))
          s = "d";
        else if (s.equals("pl"))
          s = "e";
        else
          s = "z" + s;

        expand.add(new StringValueImpl(s));
      }
      else
        i++;
    }

    return expand;
  }

  private static int compareTo(ArrayList<Value> a, ArrayList<Value> b)
  {
    int i = 0;

    while (true) {
      if (a.size() <= i && b.size() <= i)
        return 0;
      else if (a.size() <= i)
        return -1;
      else if (b.size() <= i)
        return 1;

      int cmp = compareTo(a.get(i), b.get(i));

      if (cmp != 0)
        return cmp;

      i++;
    }
  }

  private static int compareTo(Value a, Value b)
  {
    if (a.equals(b))
      return 0;
    else if (a.isLongConvertible() && ! b.isLongConvertible())
      return -1;
    else if (b.isLongConvertible() && ! a.isLongConvertible())
      return 1;
    else if (a.lt(b))
      return -1;
    else
      return 1;
  }

  static {
    addIni(_iniMap, "assert.active", "1", PHP_INI_ALL);
    addIni(_iniMap, "assert.bail", "0", PHP_INI_ALL);
    addIni(_iniMap, "assert.warning", "1", PHP_INI_ALL);
    addIni(_iniMap, "assert.callback", null, PHP_INI_ALL);
    addIni(_iniMap, "assert.quiet_eval", "0", PHP_INI_ALL);
    addIni(_iniMap, "enable_dl", "1", PHP_INI_SYSTEM);
    addIni(_iniMap, "max_execution_time", "30", PHP_INI_ALL);
    addIni(_iniMap, "max_input_time", "-1", PHP_INI_PERDIR);
    addIni(_iniMap, "magic_quotes_gpc", "1", PHP_INI_PERDIR);
    // magic_quotes is ignored in PHP 6
    addIni(_iniMap, "magic_quotes_runtime", "0", PHP_INI_ALL);

    // basic
    addIni(_iniMap, "track_vars", "On", PHP_INI_ALL);
    addIni(_iniMap, "arg_separator.output", "&", PHP_INI_ALL);
    addIni(_iniMap, "arg_separator.input", "&", PHP_INI_ALL);
    addIni(_iniMap, "variables_order", "EGPCS", PHP_INI_ALL);
    addIni(_iniMap, "auto_globals_jit", "1", PHP_INI_ALL);
    // register_globals is ignored in PHP 6
    addIni(_iniMap, "register_globals", "0", PHP_INI_ALL);
    addIni(_iniMap, "register_argc_argv", "1", PHP_INI_ALL);
    addIni(_iniMap, "register_long_arrays", "1", PHP_INI_ALL);
    addIni(_iniMap, "post_max_size", "8M", PHP_INI_ALL);
    addIni(_iniMap, "gpc_order", "GPC", PHP_INI_ALL);
    addIni(_iniMap, "auto_prepend_file", null, PHP_INI_ALL);
    addIni(_iniMap, "auto_append_file", null, PHP_INI_ALL);
    addIni(_iniMap, "default_mimetype", "text/html", PHP_INI_ALL);
    addIni(_iniMap, "default_charset", "", PHP_INI_ALL);
    addIni(_iniMap, "always_populate_raw_post_data", "0", PHP_INI_ALL);
    addIni(_iniMap, "allow_webdav_methods", "0", PHP_INI_ALL);

    addIni(_iniMap, "memory_limit", "-1", PHP_INI_ALL);
  }

  //@todo mixed   assert_options(int what [, mixed value])
  //@todo boolean assert(mixed assertion)
  //@todo int     dl(string library)
  //@todo boolean extension_loaded(string name)
  //@todo string  get_cfg_var(string varname)
  //@todo string  get_current_user()
  //@todo array   get_extension_funcs(string module_name)
  //@todo string  get_include_path()
  //@todo array   get_included_files()
  //@todo array   get_loaded_extensions()
  //@todo int     get_magic_quotes_runtime()
  //@todo array   get_required_files() ALIAS of get_included_files
  //@todo int     getlastmod()
  //@todo int     getmygid()
  //@todo int     getmyinode()
  //@todo int     getmypid()
  //@todo int     getmyuid()
  //@todo array   getopt(string options [,array longopts])
  //@todo array   getrusage([int who])
  //@todo string  ini_alter(string varname, string newvalue) ALIAS of ini_set
  //@todo array   ini_get_all([string extension])
  //@todo void    ini_restore(string varname)
  //XXX main is Dummy for main()
  //@todo int     memory_get_usage()
  //@todo string  quercus_ini_scanned_files()
  //@todo string  quercus_logo_guid()
  //@todo string  quercus_uname([string mode])
  //@todo boolean quercuscredits([int flag])
  //@todo boolean quercusinfo([int what])
  //@todo boolean pupenv(string setting)
  //@todo void    restore_include_path()
  //@todo string  set_include_path(string new_include_path)
  //@todo string  zend_logo_guid()


}

