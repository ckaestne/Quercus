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

package com.caucho.quercus.expr;

import java.io.IOException;

import java.util.ArrayList;

import com.caucho.quercus.gen.PhpWriter;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.env.QuercusClass;

import com.caucho.quercus.program.AbstractFunction;
import com.caucho.quercus.program.AnalyzeInfo;
import com.caucho.quercus.Location;

import com.caucho.util.L10N;

/**
 * Represents a PHP static method expression.
 */
public class StaticMethodExpr extends Expr {
  private static final L10N L = new L10N(StaticMethodExpr.class);
  private final String _className;
  private final String _name;
  private final Expr []_args;

  private Expr []_fullArgs;

  private AbstractFunction _fun;
  private boolean _isMethod;

  public StaticMethodExpr(Location location, String className, String name, ArrayList<Expr> args)
  {
    super(location);
    _className = className.intern();
    _name = name.intern();

    _args = new Expr[args.size()];
    args.toArray(_args);
  }

  public StaticMethodExpr(Location location, String className, String name, Expr []args)
  {
    super(location);
    _className = className.intern();
    _name = name.intern();

    _args = args;
  }


  public StaticMethodExpr(String className, String name, ArrayList<Expr> args)
  {
    this(Location.UNKNOWN, className, name, args);
  }

  public StaticMethodExpr(String className, String name, Expr []args)
  {
    this(Location.UNKNOWN, className, name, args);
  }

  /**
   * Returns the reference of the value.
   * @param location
   */
  public Expr createRef(Location location)
  {
    return new RefExpr(location, this);
  }

  /**
   * Returns the copy of the value.
   * @param location
   */
  public Expr createCopy(Location location)
  {
    return new CopyExpr(location, this);
  }
  
  /**
   * Evaluates the expression.
   *
   * @param env the calling environment.
   *
   * @return the expression value.
   */
  public Value eval(Env env)
    throws Throwable
  {
    QuercusClass cl = env.findClass(_className);

    if (cl == null) {
      // XXX: change exception
      throw new Exception(L.l("no matching class {0}", _className));
    }

    // qa/0954 - what appears to be a static call may be a call to a super constructor
    Value thisValue = env.getThis();

    return cl.evalMethod(env, thisValue, _name, _args);
  }

  //
  // Java code generation
  //
  
  /**
   * Analyzes the function.
   */
  public void analyze(AnalyzeInfo info)
  {
    _isMethod = info.getFunction().isMethod();
    
    for (int i = 0; i < _args.length; i++) {
      _args[i].analyze(info);
    }
  }

  private boolean isMethod()
  {
    return _isMethod;
  }

  /**
   * Generates code to recreate the expression.
   *
   * @param out the writer to the Java source code.
   */
  public void generate(PhpWriter out)
    throws IOException
  {
    /*
    PhpProgram program = out.getProgram();
    QuercusClass cl = program.findClass(_className);
    AbstractFunction fun = null;

    if (cl != null)
      fun = cl.findFunction(_name);

    if (fun != null) {
      // XXX: check that the null value is okay
      
      out.print("__quercus_class_" + _className + "." + _name + "(env, null");

      try {
	args = fun.bindArguments(null, null, _args);
      } catch (IOException e) {
	throw e;
      } catch (Exception e) {
	throw new IOException(e.toString());
      }
    }
    else {
    */
    Expr []args = _args;

    out.print("env.getClass(\"");
    out.printJavaString(_className);
    out.print("\").evalMethod(env, ");

    if (isMethod())
      out.print("q_this");
    else
      out.print("NullThisValue.NULL");

    out.print(", \"");
    out.printJavaString(_name);
    out.print("\"");

    if (args.length <= 5) {
      // XXX: check variable args
      
      for (int i = 0; i < args.length; i++) {
	out.print(", ");
      
	args[i].generateArg(out);
      }

      out.print(")");
    }
    else {
      out.print(", new Value[] {");

      for (int i = 0; i < args.length; i++) {
	if (i != 0)
	  out.print(", ");
      
	args[i].generateArg(out);
      }
      
      out.print("})");
    }
  }

  public void generateCopy(PhpWriter out)
    throws IOException
  {
    generate(out);
    out.print(".copyReturn()"); // php/3a5y
  }
  
  public String toString()
  {
    return _name + "()";
  }
}

