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

package com.caucho.quercus.env;

import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;

/**
 * Encapsulates an environment entry for a variable.  The EnvVar is a
 * container for Vars.
 */
public final class EnvVarImpl extends EnvVar
{
  private V<? extends Var> _var;

  public EnvVarImpl(V<? extends Var> var)
  {
    if (var == null)
      throw new NullPointerException();

    _var = var;
  }



  @Override
  public V<? extends Value> getValue() {
    return _var.flatMap(v->v.getValue());
  }

  /**
   * Sets the current value.
   */
  public V<? extends Value> set(FeatureExpr ctx, V<? extends Value> value)
  {
    V<? extends Value> oldVar = getValue();
    _var.vforeach(ctx, (c, a)->a.set(c, value));
    return oldVar;
  }

  /**
   * Returns the current Var.
   * @param ctx
   */
  @Override
  public V<? extends Var> getVar() {
    return _var;
  }

  /**
   * Sets the var.
   */
  public V<? extends Var> setVar(FeatureExpr ctx, V<? extends Var> var)
  {
    V<? extends Var> oldVar = _var;

    this._var = V.choice(ctx, var, _var);

    return oldVar;
  }

  @Override
  public EnvVar copy() {
    return null;
  }
}

