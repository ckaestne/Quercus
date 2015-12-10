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
public class ThisFieldVarExpr extends AbstractVarExpr {
  protected final ThisExpr _qThis;
  protected final Expr _nameExpr;

  public ThisFieldVarExpr(Location location, ThisExpr qThis, Expr nameExpr)
  {
    super(location);

    _qThis = qThis;
    _nameExpr = nameExpr;
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

    return factory.createThisMethod(location, _qThis, _nameExpr, args);
  }

  private StringValue evalName(Env env)
  {
    StringValue name = _nameExpr.evalStringValue(env, VHelper.noCtx()).getOne();

    ClassField entry = _qThis.getClassDef().getField(name);

    if (entry != null) {
      name = entry.getCanonicalName();
    }

    return name;
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
    Value value = env.getThis();

    return value.getThisFieldArg(env, evalName(env)).map((a)->a.makeValue());
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
    // quercus/0d1k
    Value value = env.getThis();

    return value.getThisFieldVar(env, evalName(env));
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
  public @Nonnull V<? extends Value> eval(Env env, FeatureExpr ctx)
  {
    Value obj = env.getThis();

    return obj.getThisField(env, evalName(env));
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
    Value obj = env.getThis();

    obj.putThisField(env, VHelper.noCtx(), evalName(env), value);

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
    Value obj = env.getThis();

    obj.putThisField(env, VHelper.noCtx(), evalName(env), value.map((a)->a.toValue()));

    return value;
  }

  /**
   * Evaluates as an array index assign ($a[index] = value).
   */
  @Override
  public @Nonnull V<? extends Value> evalArrayAssign(Env env, FeatureExpr ctx, Expr indexExpr, Expr valueExpr)
  {
    Value obj = env.getThis();

    StringValue name = evalName(env);

    V<? extends Value> fieldVar = obj.getThisFieldArray(env, VHelper.noCtx(), name);
    Value index = indexExpr.eval(env, VHelper.noCtx()).getOne();

    Value value = valueExpr.evalCopy(env, VHelper.noCtx()).getOne();

    // php/03mn
    return fieldVar.map((a)->a.putThisFieldArray(env, obj, name, index, value));
  }

  /**
   * Evaluates as an array index assign ($a[index] = &value).
   */
  @Override
  public V<? extends ValueOrVar> evalArrayAssignRef(Env env, FeatureExpr ctx, Expr indexExpr, Expr valueExpr)
  {
    Value obj = env.getThis();

    StringValue name = evalName(env);

    V<? extends Value> fieldVar = obj.getThisFieldArray(env, VHelper.noCtx(), name);
    Value index = indexExpr.eval(env, ctx).getOne();

    Value value = valueExpr.evalRef(env, ctx).getOne().toValue();

    // php/03mn
    return fieldVar.map((a)->a.putThisFieldArray(env, obj, name, index, value));
  }

  /**
   * Evaluates the expression, creating an array if the field is unset.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public @Nonnull V<? extends Value> evalArray(Env env, FeatureExpr ctx)
  {
    Value obj = env.getThis();

    return obj.getThisFieldArray(env, VHelper.noCtx(), evalName(env));
  }

  /**
   * Evaluates the expression, creating an object if the field is unset.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public @Nonnull V<? extends Value> evalObject(Env env, FeatureExpr ctx)
  {
    Value obj = env.getThis();

    return obj.getThisFieldObject(env, VHelper.noCtx(), evalName(env));
  }

  /**
   * Evaluates the expression.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public void evalUnset(Env env, FeatureExpr ctx)
  {
    Value obj = env.getThis();

    obj.unsetThisField(evalName(env));
  }

  public String toString()
  {
    return "$this->{" + _nameExpr + "}";
  }
}

