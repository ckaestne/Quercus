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
 * @author Nam Nguyen
 */

package com.caucho.quercus.env;

import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;

/**
 * For compiled code and error reporting.
 * XXX: Need to instantiate vars when they're used, not at the top of the method.
 */
public class LazyEnvVar extends EnvVar {
  private final StringValue _name;

  private EnvVar _var;

  public LazyEnvVar(StringValue name)
  {
    _name = name;
  }

  /**
   * Returns the current value.
   * @param ctx
   */
  public V<? extends Value> get(FeatureExpr ctx)
  {
    return getEnvVar(true).get(ctx);
  }

  /**
   * Sets the current value.
   */
  public V<? extends Value> set(FeatureExpr ctx, V<? extends Value> value)
  {
    return getEnvVar(false).set(ctx, value);
  }

  /**
   * Returns the current Var.
   * @param ctx
   */
  public V<? extends Var> getVar(FeatureExpr ctx)
  {
    return getEnvVar(false).getVar(ctx);
  }

  /**
   * Sets the var.
   */
  public V<? extends Var> setVar(FeatureExpr ctx, V<? extends Var> var)
  {
    return getEnvVar(false).setVar(ctx, var);
  }

  private EnvVar getEnvVar(boolean isOutputNotice)
  {
    if (_var == null) {
      _var = Env.getInstance().getEnvVar(_name, true, isOutputNotice);
    }

    return _var;
  }
}
