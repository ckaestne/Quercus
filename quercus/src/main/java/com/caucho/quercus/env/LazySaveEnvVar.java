///*
// * Copyright (c) 1998-2012 Caucho Technology -- all rights reserved
// *
// * This file is part of Resin(R) Open Source
// *
// * Each copy or derived work must preserve the copyright notice and this
// * notice unmodified.
// *
// * Resin Open Source is free software; you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation; either version 2 of the License, or
// * (at your option) any later version.
// *
// * Resin Open Source is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, or any warranty
// * of NON-INFRINGEMENT.  See the GNU General Public License for more
// * details.
// *
// * You should have received a copy of the GNU General Public License
// * along with Resin Open Source; if not, write to the
// *
// *   Free Software Foundation, Inc.
// *   59 Temple Place, Suite 330
// *   Boston, MA 02111-1307  USA
// *
// * @author Scott Ferguson
// */
//
//package com.caucho.quercus.env;
//
//import de.fosd.typechef.featureexpr.FeatureExpr;
//import edu.cmu.cs.varex.V;
//import edu.cmu.cs.varex.VHelper;
//
///**
// * Encapsulates an environment entry for a variable.  The EnvVar is a
// * container for Vars.
// */
//public class LazySaveEnvVar extends EnvVar
//{
//  private int _id;
//  private Value _value;
//
//  public LazySaveEnvVar(int id, Value value)
//  {
//    _id = id;
//    _value = value;
//  }
//
//  /**
//   * Returns the current value.
//   * @param ctx
//   */
//  public V<? extends Value> get(FeatureExpr ctx)
//  {
//    return getEnvVar().get(ctx);
//  }
//
//  /**
//   * Sets the current value.
//   */
//  public V<? extends Value> set(FeatureExpr ctx, V<? extends Value> value)
//  {
//    return getEnvVar().set(ctx, value);
//  }
//
//  /**
//   * Returns the current Var.
//   * @param ctx
//   */
//  public V<? extends Var> getVar(FeatureExpr ctx)
//  {
//    return getEnvVar().getVar(ctx);
//  }
//
//  /**
//   * Sets the var.
//   */
//  public V<? extends Var> setVar(FeatureExpr ctx, V<? extends Var> var)
//  {
//    return getEnvVar().setVar(ctx, var);
//  }
//
//  private EnvVar getEnvVar()
//  {
//    Env env = Env.getCurrent();
//    EnvVar []globals = env.getGlobalList();
//
//    if (globals[_id] == this) {
//      EnvVar var = new EnvVarImpl(V.one(new Var()));
//
//      var.set(VHelper.noCtx(), V.one(_value.copy(env)));
//
//      globals[_id] = var;
//    }
//
//    return globals[_id];
//  }
//}
//
