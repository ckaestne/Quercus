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
import com.caucho.util.L10N;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;
import javax.annotation.Nonnull;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Represents a variable class field reference $class::${"b"}.
 */
public class ClassVarFieldVarExpr extends AbstractVarExpr {
  private static final L10N L = new L10N(ClassVarFieldVarExpr.class);

  protected final Expr _className;
  protected final Expr _varName;

  public ClassVarFieldVarExpr(Expr className, Expr varName)
  {
    _className = className;

    _varName = varName;
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

    return factory.createClassMethodCall(location, _className, _varName, args);
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
    String className = _className.evalString(env, VHelper.noCtx()).getOne();

    QuercusClass qClass = env.getClass(className);

    StringValue varName = _varName.evalStringValue(env, VHelper.noCtx()).getOne();

    return qClass.getStaticFieldValue(env, varName);
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
    String className = _className.evalString(env, VHelper.noCtx()).getOne();

    QuercusClass qClass = env.getClass(className);

    StringValue varName = _varName.evalStringValue(env, VHelper.noCtx()).getOne();

    return qClass.getStaticFieldVar(env, varName);
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
    String className = _className.evalString(env, VHelper.noCtx()).getOne();

    QuercusClass qClass = env.getClass(className);

    StringValue varName = _varName.evalStringValue(env, VHelper.noCtx()).getOne();

    return qClass.setStaticFieldRef(env, VHelper.noCtx(), varName, value).map((a)->a.makeValue());
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
    env.error(L.l("{0}::${1}: Cannot unset class variables.", _className, _varName),
              getLocation());
  }

  public String toString()
  {
    return _className + "::$" + _varName;
  }
}

