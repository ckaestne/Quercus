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
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents a PHP assignment expression.
 */
public class BinaryAssignExpr extends Expr {
  protected final AbstractVarExpr _var;
  protected final Expr _value;

  public BinaryAssignExpr(Location location, AbstractVarExpr var, Expr value)
  {
    super(location);

    _var = var;
    _value = value;
  }

  public BinaryAssignExpr(AbstractVarExpr var, Expr value)
  {
    _var = var;
    _value = value;
  }

  /**
   * Creates a assignment
   * @param location
   */
  @Override
  public Expr createCopy(ExprFactory factory)
  {
    // quercus/3d9e
    return factory.createCopy(this);
  }

  /**
   * Returns true if a static false value.
   */
  @Override
  public boolean isAssign()
  {
    return true;
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
  public @NonNull V<? extends Value> eval(Env env, FeatureExpr ctx)
  {
    return _var.evalAssignValue(env, ctx, _value);
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
  public @NonNull V<? extends Value> evalCopy(Env env, FeatureExpr ctx)
  {
    // php/0d9e
    return eval(env, ctx).map((a)->a.copy());
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
  public V<? extends ValueOrVar> evalRef(Env env, FeatureExpr ctx)
  {
    V<? extends Value> value = _value.evalCopy(env, ctx);

    _var.evalAssignValue(env, ctx, value);

    // php/03d9, php/03mk
    return _var.eval(env, ctx);
  }

  public String toString()
  {
    return _var + "=" + _value;
  }
}

