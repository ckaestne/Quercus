/*
 * Copyright (c) 1998-2012 Caucho Technology -- all rights reserved
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

import com.caucho.quercus.Location;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.env.Var;
import com.caucho.quercus.parser.QuercusParser;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents a PHP reference argument.
 */
public class UnaryRefExpr extends AbstractUnaryExpr {
  public UnaryRefExpr(Location location, Expr expr)
  {
    super(location, expr);
  }

  public UnaryRefExpr(Expr expr)
  {
    super(expr);
  }

  /**
   * Returns true for a reference.
   */
  public boolean isRef()
  {
    return true;
  }
  
  /**
   * Creates an assignment using this value as the right hand side.
   */
  @Override
  public Expr createAssignFrom(QuercusParser parser,
                               AbstractVarExpr leftHandSide)
  {
    ExprFactory factory = parser.getExprFactory();
    
    return factory.createAssignRef(leftHandSide, _expr);
  }

  /**
   * Evaluates the expression.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public @NonNull V<? extends Value> eval(Env env, FeatureExpr ctx)
  {
    // quercus/0d28
    V<? extends Var> value = getExpr().evalVar(env, VHelper.noCtx());
    
    return value.map((a)->a.toRef());
  }

  /**
   * Evaluates the expression.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  @Override
  public @NonNull V<? extends Value> evalArg(Env env, FeatureExpr ctx, boolean isTop)
  {
    V<? extends Var> value = getExpr().evalVar(env, VHelper.noCtx());
    
    return value.map((a)->a.toArgRef());
  }

  /**
   * Evaluates the expression.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public V<? extends Var> evalVar(Env env, FeatureExpr ctx)
  {
    V<? extends Var> value = getExpr().evalVar(env, VHelper.noCtx());

    // php/112d
    return value;
    /*
    if (value instanceof Var)
      return new RefVar((Var) value);
    else
      return value;
    */
  }

  public String toString()
  {
    return _expr.toString();
  }
}

