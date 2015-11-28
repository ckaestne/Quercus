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
import com.caucho.quercus.program.InterpretedClassDef;
import com.caucho.util.L10N;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents the 'this' expression.
 */
public class ThisExpr extends AbstractVarExpr {
  private static final L10N L = new L10N(Expr.class);

  protected final InterpretedClassDef _classDef;

  public ThisExpr(InterpretedClassDef classDef)
  {
    _classDef = classDef;
  }

  public InterpretedClassDef getClassDef()
  {
    return _classDef;
  }

  /**
   * Creates a field ref
   */
  @Override
  public Expr createFieldGet(ExprFactory factory,
                             Location location,
                             StringValue name)
  {
    return factory.createThisField(location, this, name);
  }

  /**
   * Creates a field ref
   */
  @Override
  public Expr createFieldGet(ExprFactory factory,
                             Location location,
                             Expr name)
  {
    return factory.createThisField(location, this, name);
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
    return VHelper.toV(env.getThis());
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
    return VHelper.toV(env.getThis());
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
    return VHelper.toV(env.getThis().toVar());
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
    env.error(L.l("can't assign $this"), getLocation());

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
  public @NonNull V<? extends Value> evalAssignRef(Env env, FeatureExpr ctx, V<? extends Value> value)
  {
    env.error(L.l("can't assign $this"), getLocation());

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
    env.error(L.l("can't unset $this"), getLocation());
  }

  /**
   * Evaluates as a QuercusClass.
   */
  public V<? extends QuercusClass> evalQuercusClass(Env env, FeatureExpr ctx)
  {
    return VHelper.toV(env.getThis().getQuercusClass());
  }

  public String toString()
  {
    return "$this";
  }
}

