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
import com.caucho.quercus.env.ValueOrVar;
import com.caucho.quercus.env.Var;
import com.caucho.quercus.parser.QuercusParser;
import com.caucho.quercus.statement.Statement;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;
import javax.annotation.Nonnull;

/**
 * Represents an expression that is assignable
 */
abstract public class AbstractVarExpr extends Expr {
  public AbstractVarExpr(Location location)
  {
    super(location);
  }

  public AbstractVarExpr()
  {
  }


  /**
   * Returns true if the expression is a var/left-hand-side.
   */
  @Override
  public boolean isVar()
  {
    return true;
  }

  /**
   * Marks the value as assigned
   */
  public void assign(QuercusParser parser)
  {
    // XXX: used by list, e.g. quercus/03l8.  need further tests
  }

  /**
   * Creates the assignment.
   */
  @Override
  public Expr createAssign(QuercusParser parser, Expr value)
  {
    return value.createAssignFrom(parser, this);
  }

  /**
   * Creates the assignment.
   */
  @Override
  public Expr createAssignRef(QuercusParser parser,
                              Expr value)
  {
    return parser.getExprFactory().createAssignRef(this, value);
  }

  /**
   * Creates the reference
   * @param location
   */
  @Override
  public Expr createRef(QuercusParser parser)
  {
    return parser.getExprFactory().createRef(this);
  }

  /**
   * Creates the copy.
   * @param location
   */
  @Override
  public Expr createCopy(ExprFactory factory)
  {
    return factory.createCopy(this);
  }

  /**
   * Creates the assignment.
   */
  @Override
  public Statement createUnset(ExprFactory factory, Location location)
  {
    return factory.createExpr(location, factory.createUnsetVar(this));
  }

  /**
   * Evaluates the expression, returning a Value.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  @Override
  abstract public @Nonnull V<? extends Value> eval(Env env, FeatureExpr ctx);

  /**
   * Evaluates the expression as a reference (by RefExpr).
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  @Override
  abstract public V<? extends Var> evalVar(Env env, FeatureExpr ctx);

  /**
   * Evaluates the expression as a reference when possible.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  @Override
  public V<? extends ValueOrVar> evalRef(Env env, FeatureExpr ctx)
  {
    return evalVar(env, ctx);
  }

  /**
   * Evaluates the expression as an argument.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  @Override
  public V<? extends ValueOrVar> evalArg(Env env, FeatureExpr ctx, boolean isTop)
  {
    return evalVar(env, ctx).map((a)->a.makeValue());
  }

  /**
   * Evaluates the expression and copies the result for an assignment.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  @Override
  public @Nonnull V<? extends Value> evalCopy(Env env, FeatureExpr ctx)
  {
    return eval(env, ctx).map((a)->a.copy());
  }

  /**
   * Evaluates the expression as an array.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  @Override
  public @Nonnull V<? extends Value> evalArray(Env env, FeatureExpr ctx)
  {
    return evalVar(env, ctx).map((a)->a.makeValue().toAutoArray());
  }

  /**
   * Evaluates the expression as an object.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  @Override
  public @Nonnull V<? extends Value> evalObject(Env env, FeatureExpr ctx)
  {
    return evalVar(env, ctx).map((a)->a.makeValue().toObject(env));
  }

  /**
   * Evaluates the expression as an argument.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  abstract public void evalUnset(Env env, FeatureExpr ctx);

  /**
   * Evaluates the expression.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @param value
   * @return the expression value.
   */
  @Override
  public @Nonnull V<? extends Value> evalAssignValue(Env env, FeatureExpr ctx, V<? extends Value> value)
  {
    return evalAssignRef(env, ctx, value).map(v->v.toValue());
  }

  /**
   * Assign the variable with a new reference value.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @param value
   * @return the expression value.
   */
  abstract public V<? extends ValueOrVar> evalAssignRef(Env env, FeatureExpr ctx, V<? extends ValueOrVar> value);
}

