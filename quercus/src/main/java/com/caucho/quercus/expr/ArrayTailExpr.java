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
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents a PHP array[] reference expression.
 */
public class ArrayTailExpr extends AbstractVarExpr {
  protected final Expr _expr;

  public ArrayTailExpr(Location location, Expr expr)
  {
    super(location);
    _expr = expr;
  }

  public ArrayTailExpr(Expr expr)
  {
    _expr = expr;
  }

  /**
   * Returns true for an expression that can be read (only $a[] uses this)
   */
  public boolean canRead()
  {
    return false;
  }

  /**
   * Returns the expr.
   */
  public Expr getExpr()
  {
    return _expr;
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
    return VHelper.toV(env.error("Cannot use [] as a read-value.", getLocation()));
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
    if (isTop) {
      V<? extends Value> obj = _expr.evalArray(env, ctx);

      return obj.map((a)->a.putVar());
    }
    else {
      // php/0d4e need to do a toValue()
      V<? extends Value> obj = _expr.evalArray(env, ctx).map((a)->a.toValue());

      return obj.map((a)->a.getArgTail(env, isTop));
    }
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
    V<? extends Var> obj = _expr.evalVar(env, ctx);

    return obj.map((a)->a.putVar());
  }

  /**
   * Evaluates the expression, setting an array if unset..
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public @NonNull V<? extends Value> evalArray(Env env, FeatureExpr ctx)
  {
    V<? extends Value> obj = _expr.evalArray(env, VHelper.noCtx());

    return obj.map((a)->a.putArray(env));
  }

  /**
   * Evaluates the expression, assigning an object if unset..
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public @NonNull V<? extends Value> evalObject(Env env, FeatureExpr ctx)
  {
    V<? extends Value> array = _expr.evalArray(env, VHelper.noCtx());

    Value value = env.createObject();

    array.map((a)->a.put(value));

    return VHelper.toV(value);
  }

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
  public @NonNull V<? extends Value> evalAssignValue(Env env, FeatureExpr ctx, V<? extends Value> value)
  {
    /*
    Value array = _expr.evalVar(env);

    array = array.toAutoArray();

    array.put(value);

    return value;
    */

    // php/048b
    V<? extends Value> array = _expr.evalArrayAssignTail(env, ctx, value);

    return array;
  }

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
  public @NonNull V<? extends Value> evalAssignRef(Env env, FeatureExpr ctx, V<? extends Value> value)
  {
    V<? extends Value> array = _expr.evalArray(env, ctx);

    array.map((a)->a.put(value.getOne()));

    return value;
  }

  /**
   * Evaluates the expression.
   *
   * @param env the calling environment.
   *
   * @return the expression value.
   */
  public void evalUnset(Env env)
  {
    throw new UnsupportedOperationException();
  }

  public String toString()
  {
    return _expr + "[]";
  }
}

