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
import com.caucho.quercus.env.BooleanValue;
import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.ValueOrVar;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;

import javax.annotation.Nonnull;

/**
 * Represents a logical and expression.
 */
public class BinaryAndExpr extends AbstractBinaryExpr {
  public BinaryAndExpr(Location location, Expr left, Expr right)
  {
    super(location, left, right);
  }

  public BinaryAndExpr(Expr left, Expr right)
  {
    super(left, right);
  }

  /**
   * Returns true for a boolean.
   */
  @Override
  public boolean isBoolean()
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
  @Nonnull protected V<? extends ValueOrVar> _eval(Env env, FeatureExpr ctx)
  {
    return evalBoolean(env, ctx).map(v -> v ? BooleanValue.TRUE : BooleanValue.FALSE);
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
  public V<? extends Boolean> evalBoolean(Env env, FeatureExpr ctx)
  {
    return _left.evalBoolean(env, ctx).vflatMap(ctx, (c, l) ->
            l ? _right.evalBoolean(env, c) : V.one(false));
  }

  public String toString()
  {
    return "(" + _left + " && " + _right + ")";
  }
}

