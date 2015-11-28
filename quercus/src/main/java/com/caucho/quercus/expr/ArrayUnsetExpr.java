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
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;

/**
 * Represents a PHP array unset expression.
 */
public class ArrayUnsetExpr extends Expr {
  protected final Expr _expr;
  protected final Expr _index;

  public ArrayUnsetExpr(Location location, Expr expr, Expr index)
  {
    super(location);
    _expr = expr;
    _index = index;
  }

  public ArrayUnsetExpr(Expr expr, Expr index)
  {
    _expr = expr;
    _index = index;
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
    V<? extends Value> array = _expr.eval(env, VHelper.noCtx());
    V<? extends Value> index = _index.eval(env, VHelper.noCtx());

    return VHelper.mapAll(array, index, (a,i)-> a.remove(i));
  }

  public String toString()
  {
    return "unset(" + _expr + "[" + _index + "])";
  }
}

