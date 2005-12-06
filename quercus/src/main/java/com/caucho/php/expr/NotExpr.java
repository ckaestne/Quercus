/*
 * Copyright (c) 1998-2004 Caucho Technology -- all rights reserved
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

package com.caucho.php.expr;

import java.io.IOException;

import java.util.HashSet;

import com.caucho.php.env.Env;
import com.caucho.php.env.Value;
import com.caucho.php.env.BooleanValue;

import com.caucho.php.gen.PhpWriter;

/**
 * Represents a PHP boolean negation
 */
public final class NotExpr extends UnaryExpr {
  public NotExpr(Expr expr)
  {
    super(expr);
  }

  /**
   * Return true as a boolean.
   */
  public boolean isBoolean()
  {
    return true;
  }

  /**
   * Evaluates the equality as a boolean.
   */
  public Value eval(Env env)
    throws Throwable
  {
    return _expr.evalBoolean(env) ? BooleanValue.FALSE : BooleanValue.TRUE;
  }

  /**
   * Evaluates the equality as a boolean.
   */
  public boolean evalBoolean(Env env)
    throws Throwable
  {
    return ! _expr.evalBoolean(env);
  }

  //
  // Java code generation
  //

  /**
   * Generates code to evaluate the expression
   *
   * @param out the writer to the Java source code.
   */
  public void generate(PhpWriter out)
    throws IOException
  {
    out.print("env.toValue(");
    generateBoolean(out);
    out.print(")");
  }

  /**
   * Generates code to evaluate the expression.
   *
   * @param out the writer to the Java source code.
   */
  public void generateBoolean(PhpWriter out)
    throws IOException
  {
    out.print("! ");
    _expr.generateBoolean(out);
  }

  /**
   * Generates code to recreate the expression.
   *
   * @param out the writer to the Java source code.
   */
  public void generateExpr(PhpWriter out)
    throws IOException
  {
    out.print("new com.caucho.php.expr.NotExpr(");
    _expr.generateExpr(out);
    out.print(")");
  }
  
  public String toString()
  {
    return "! " + _expr;
  }
}

