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
import com.caucho.quercus.env.StringValue;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.env.Var;
import com.caucho.quercus.parser.QuercusParser;
import com.caucho.util.L10N;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Represents a PHP field reference.
 */
public class ObjectFieldExpr extends AbstractVarExpr {
  private static final L10N L = new L10N(ObjectFieldExpr.class);

  protected final Expr _objExpr;
  protected final StringValue _name;

  public ObjectFieldExpr(Location location, Expr objExpr, StringValue name)
  {
    super(location);
    _objExpr = objExpr;

    _name = name;
  }

  public ObjectFieldExpr(Expr objExpr, StringValue name)
  {
    _objExpr = objExpr;

    _name = name;
  }

  //
  // function call creation
  //

  /**
   * Creates a function call expression
   */
  @Override
  public Expr createCall(QuercusParser parser,
                         Location location,
                         ArrayList<Expr> args)
    throws IOException
  {
    ExprFactory factory = parser.getExprFactory();

    return factory.createMethodCall(location, _objExpr, _name, args);
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
    V<? extends Value> obj = _objExpr.eval(env, VHelper.noCtx());
    return obj.map((a)->a.getField(env, _name));
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
  public V<? extends Var> evalVar(Env env, FeatureExpr ctx)
  {
    V<? extends Value> obj = _objExpr.evalObject(env, VHelper.noCtx());

    return obj.map((a)->a.getFieldVar(env, _name));
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
    V<? extends Value> value = _objExpr.evalArg(env, VHelper.noCtx(), false);

    return value.map((a)->a.getFieldArg(env, _name, isTop));
  }

  @Override
  public @NonNull V<? extends Value> evalDirty(Env env, FeatureExpr ctx)
  {
    // php/0228
    V<? extends Value> obj = _objExpr.eval(env, VHelper.noCtx());

    return obj.map((a)->a.getFieldVar(env, _name).toValue());
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
    V<? extends Value> obj = _objExpr.evalObject(env, VHelper.noCtx());

    obj.map((a)->a.putField(env, _name, value.getOne()));

    return value;
  }

  /**
   * Handles post increments.
   */
  @Override
  public V<? extends Value> evalPostIncrement(Env env, FeatureExpr ctx, int incr)
  {
    // php/09kp

    V<? extends Value> obj = _objExpr.evalObject(env, VHelper.noCtx());
    final V<? extends Value> value = obj.map((a)->a.getField(env, _name))
      .map((a)->a.postincr(incr));
    obj.map((a)->a.putField(env, _name, value.getOne()));

    return value;
  }

  /**
   * Handles post increments.
   */
  @Override
  public V<? extends Value> evalPreIncrement(Env env, FeatureExpr ctx, int incr)
  {
    // php/09kq

    V<? extends Value> obj = _objExpr.evalObject(env, VHelper.noCtx());
    V<? extends Value> value = obj.map((a)->a.getField(env, _name))
            .map((a)->a.preincr(incr));
    obj.map((a)->a.putField(env, _name, value.getOne()));

    return value;
  }

  /**
   * Evaluates the expression, creating an array if the field is unset.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  @Override
  public @NonNull V<? extends Value> evalArray(Env env, FeatureExpr ctx)
  {
    V<? extends Value> obj = _objExpr.evalObject(env, VHelper.noCtx());

    return obj.map((a)->a.getFieldArray(env, _name));
  }

  /**
   * Evaluates the expression, creating an object if the field is unset.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  @Override
  public @NonNull V<? extends Value> evalObject(Env env, FeatureExpr ctx)
  {
    V<? extends Value> obj = _objExpr.evalObject(env, VHelper.noCtx());

    // php/0a6f
    return obj.map((a)->a.getFieldObject(env, _name));
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
    V<? extends Value> obj = _objExpr.eval(env, VHelper.noCtx());
    obj.foreach((a)->a.unsetField(_name));
  }

  /**
   * Evaluates the expression as an array index unset
   */
  @Override
  public void evalUnsetArray(Env env, FeatureExpr ctx, Expr indexExpr)
  {
    V<? extends Value> obj = _objExpr.eval(env, VHelper.noCtx());
    V<? extends Value> index = indexExpr.eval(env, VHelper.noCtx());

    obj.foreach((a)->a.unsetArray(env, _name, index.getOne()));
  }

  @Override
  public String toString()
  {
    return _objExpr + "->" + _name;
  }

  @Override
  public V<? extends Boolean> evalIsset(Env env, FeatureExpr ctx)
  {
    V<? extends Value> object = _objExpr.eval(env, VHelper.noCtx());

    return object.map((a)->a.issetField(env, _name));
  }
}

