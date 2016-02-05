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
import edu.cmu.cs.varex.VHelper;
import edu.cmu.cs.varex.annotation.VDeprecated;

/**
 * Encapsulates an environment entry for a variable.  The EnvVar is a
 * container for Vars.
 *
 * Varex Design Notes:
 *
 *  - A value represents exactly one value (not variational, except for inner variations
 *    in objects and arrays)
 *
 *  - A Var holds one or more values (variational); it is essentially a pointer
 *    for pass by reference assignments. If a variable is passed by reference,
 *    all values are passed. A var needs to be variational such that a value
 *    can be partially changed on a shared reference.
 *
 *  - A EnvVar represents the value of one variable or field (there is a map from
 *    variable names to EnvVar in Env and EnvVar are now the structures returned by
 *    arrays [and field (FIX)] access) and this can point to alternative variables
 *    in different configurations.
 *
 * One per name, usually no need for V<EnvVar>
 */
abstract public class EnvVar
{
  /**
   * Returns the current value.
   */
  abstract public V<? extends Value> getValue();

  @Deprecated@VDeprecated
  public final Value getOne() { return getValue().getOne(); }
  @Deprecated@VDeprecated
  public final Value getOne(FeatureExpr ctx) { return getValue().getOne(ctx); }

  /**
   * Sets the current value.
   */
  abstract public V<? extends Value> set(FeatureExpr ctx, V<? extends Value> value);

  /**
   * Returns the current Var.
   * @param ctx
   */
  abstract public V<? extends Var> getVar();

  /**
   * Sets the var.
   */
  abstract public V<? extends Var> setVar(FeatureExpr ctx, V<? extends Var> var);

  /**
   * Sets the value as a reference. If the value is a Var, it replaces
   * the current Var, otherwise it sets the value. 
   */
  public V<? extends Var> setRef(FeatureExpr ctx, V<? extends ValueOrVar> value)
  {
    value.vforeach(ctx, (c, v) -> {
      if (v.isVar())
        setVar(c, V.one(v._var()));
      else
        set(c, V.one(v._value()));
    });
    
    return getVar();
  }

  @Deprecated@VDeprecated//replace new EnvVarImpl() if intended
  public static EnvVar _gen(Value v) {
    return new EnvVarImpl(V.one(new VarImpl(V.one(v))));
  }
  public static EnvVar  fromValue(Value v) {
    return new EnvVarImpl(V.one(new VarImpl(V.one(v))));
  }
  public static EnvVar fromValues(V<? extends Value> v) {
    return new EnvVarImpl(V.one(new VarImpl(v)));
  }
  public static EnvVar fromValuesOrVar(V<? extends ValueOrVar> v) {
    EnvVar result = EnvVar.fromValue(NullValue.NULL);
    result.setRef(VHelper.noCtx(), v);
    return result;
  }

  public abstract EnvVar copy();
}

