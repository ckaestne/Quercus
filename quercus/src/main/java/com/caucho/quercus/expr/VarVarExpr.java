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
import com.caucho.quercus.env.*;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;

/**
 * Represents a PHP variable expression.
 */
public class VarVarExpr extends AbstractVarExpr {
  protected final Expr _var;

  public VarVarExpr(Location location, Expr var)
  {
    super(location);
    _var = var;
  }

  public VarVarExpr(Expr var)
  {
    _var = var;
  }

  public Expr getExpr()
  {
    return _var;
  }

  /**
   * Evaluates the expression.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public V<? extends Value> eval(Env env, FeatureExpr ctx)
  {
    V<StringValue> varName = _var.evalStringValue(env, VHelper.noCtx());

    V<Value> value = varName.map((vn)->env.getValue(vn));

    return value.map((v)-> {
      if (v != null)
        return v;
      else
        return NullValue.NULL;
    });
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
  public V<? extends Value> evalAssignRef(Env env, FeatureExpr ctx, V<? extends Value> value)
  {
    V<StringValue> varName = _var.evalStringValue(env, VHelper.noCtx());

    // php/0d63
    env.setRef(varName.getOne(), value.getOne());

    return value;
  }

  /**
   * Evaluates the expression.
   *
   * @param env the calling environment.
   *
   * @return the expression value.
   */
  @Override
  public void evalUnset(Env env)
  {
    StringValue varName = _var.evalStringValue(env, VHelper.noCtx()).getOne();

    env.unsetVar(varName);
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
  public V<Var> evalVar(Env env, FeatureExpr ctx)
  {
    V<StringValue> varName = _var.evalStringValue(env, VHelper.noCtx());

    return varName.map((vn)-> env.getVar(vn));
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
  public V<? extends Value> evalArg(Env env, FeatureExpr ctx, boolean isTop)
  {
    V<StringValue> varName = _var.evalStringValue(env, VHelper.noCtx());

    V<Value> value = varName.map((vn)-> env.getVar(vn));

    return value.map((v)-> {
      if (v != null)
        return v;
      else
        return NullValue.NULL;
    });
  }

  /**
   * Evaluates the expression, converting to an array if necessary.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  @Override
  public V<? extends Value> evalArray(Env env, FeatureExpr ctx)
  {
    V<StringValue> varName = _var.evalStringValue(env, VHelper.noCtx());

    V<Value> value = varName.map((vn)-> env.getVar(vn));

    return value.map((a)->a.toAutoArray());
  }

  public String toString()
  {
    return "$" + _var;
  }
}

