/*
 * Copyright (c) 1998-2014 Caucho Technology -- all rights reserved
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
import com.caucho.quercus.parser.QuercusParser;
import com.caucho.quercus.program.ClassField;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;
import javax.annotation.Nonnull;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Represents a PHP field reference.
 */
public class ThisFieldExpr extends AbstractVarExpr {
  protected final ThisExpr _qThis;

  protected StringValue _name;

  protected boolean _isInit;

  public ThisFieldExpr(Location location,
                       ThisExpr qThis,
                       StringValue name)
  {
    super(location);

    _qThis = qThis;
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

    return factory.createThisMethod(location,
                                    _qThis, _name, args);
  }

  public void init()
  {
    /// XXX: have this called by QuercusParser after class parsing

    if (! _isInit) {
      _isInit = true;

      ClassField entry = _qThis.getClassDef().getField(_name);

      if (entry != null) {
        _name = entry.getCanonicalName();
      }
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
  @Override
  @Nonnull protected V<? extends ValueOrVar> _eval(Env env, FeatureExpr ctx)
  {
    init();

    Value obj = env.getThis();

    if (obj.isNull()) {
      return VHelper.toV(env.thisError(getLocation()));
    }

    return obj.getThisField(env, _name);
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
  public @Nonnull V<? extends Value> evalCopy(Env env, FeatureExpr ctx)
  {
    init();

    Value obj = env.getThis();

    if (obj.isNull()) {
      return VHelper.toV(env.thisError(getLocation()));
    }

    return obj.getThisField(env, _name).map((a)->a.copy());
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
    init();

    Value obj = env.getThis();

    if (obj.isNull()) {
      env.thisError(getLocation());

      return VHelper.toV(new VarImpl());
    }

    return obj.getThisFieldVar(env, _name);
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
  public V<? extends ValueOrVar> evalArg(Env env, FeatureExpr ctx, boolean isTop)
  {
    init();

    Value obj = env.getThis();

    if (obj.isNull()) {
      return VHelper.toV(env.thisError(getLocation()));
    }

    return obj.getThisFieldArg(env, _name);
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
  public @Nonnull V<? extends Value> evalAssignValue(Env env, FeatureExpr ctx, V<? extends Value> value)
  {
    init();

    Value obj = env.getThis();

    if (obj.isNull()) {
      return VHelper.toV(env.thisError(getLocation()));
    }

    obj.putThisField(env, VHelper.noCtx(), _name, value);

    return value;
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
  public V<? extends ValueOrVar> evalAssignRef(Env env, FeatureExpr ctx, V<? extends ValueOrVar> value)
  {
    init();

    Value obj = env.getThis();

    if (obj.isNull()) {
      return VHelper.toV(env.thisError(getLocation()));
    }

    obj.putThisField(env, VHelper.noCtx(), _name, value.map((a)->a.toValue()));

    return value;
  }

  /**
   * Evaluates as an array index assign ($a[index] = value).
   */
  @Override
  public @Nonnull V<? extends Value> evalArrayAssign(Env env, FeatureExpr ctx, Expr indexExpr, Expr valueExpr)
  {
    init();

    Value obj = env.getThis();

    if (obj.isNull()) {
      return VHelper.toV(env.thisError(getLocation()));
    }

    // php/044i
    V<? extends Value> fieldVar = obj.getThisFieldArray(env, ctx, _name);
    V<? extends Value> index = indexExpr.eval(env, VHelper.noCtx());

    V<? extends Value> value = valueExpr.evalCopy(env, VHelper.noCtx());

    return VHelper.mapAll(index, value, fieldVar, (i,v, fv)-> fv.putThisFieldArray(env, obj, _name, i, v));
  }

  /**
   * Evaluates as an array index assign ($a[index] = value).
   */
  @Override
  public V<? extends ValueOrVar> evalArrayAssignRef(Env env, FeatureExpr ctx, Expr indexExpr, Expr valueExpr)
  {
    init();

    Value obj = env.getThis();

    if (obj.isNull()) {
      return VHelper.toV(env.thisError(getLocation()));
    }

    // php/044i
    V<? extends Value> fieldVar = obj.getThisFieldArray(env, VHelper.noCtx(), _name);
    V<? extends Value> index = indexExpr.eval(env, VHelper.noCtx());

    V<? extends ValueOrVar> value = valueExpr.evalRef(env, VHelper.noCtx());

    return  VHelper.mapAll(index, value, fieldVar, (i,v, fv)-> fv.putThisFieldArray(env, obj, _name, i, v.toValue()));
  }

  /**
   * Evaluates the expression, creating an array if the value is unset..
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  @Override
  public @Nonnull
  V<? extends ValueOrVar> evalArray(Env env, FeatureExpr ctx)
  {
    init();

    Value obj = env.getThis();

    if (obj.isNull()) {
      return VHelper.toV(env.thisError(getLocation()));
    }

    return obj.getThisFieldArray(env, VHelper.noCtx(), _name);
  }

  /**
   * Evaluates the expression, creating an array if the value is unset..
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  @Override
  public @Nonnull V<? extends Value> evalObject(Env env, FeatureExpr ctx)
  {
    init();

    Value obj = env.getThis();

    if (obj.isNull()) {
      return VHelper.toV(env.thisError(getLocation()));
    }

    return obj.getThisFieldObject(env, VHelper.noCtx(), _name);
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
  public void evalUnset(Env env, FeatureExpr ctx)
  {
    init();

    Value obj = env.getThis();

    if (obj.isNull()) {
      env.thisError(getLocation());
    }

    obj.unsetThisField(_name);
  }

  public String toString()
  {
    return "$this->" + _name;
  }
}

