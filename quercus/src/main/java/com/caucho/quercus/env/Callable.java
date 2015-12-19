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

import com.caucho.quercus.program.Arg;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;
import edu.cmu.cs.varex.annotation.VDeprecated;

import javax.annotation.Nonnull;

/**
 * Represents a call to a function.
 */
public interface Callable {
  /**
   * Returns the callable name, needed as special case for ob_*
   */
  String getCallbackName();

  /**
   * Checks for a valid callback.
   */
  boolean isValid(Env env);

  /**
   * Returns the name of the file where this is defined in.
   */
  String getDeclFileName(Env env);

  /**
   * Returns the start line in the file where this is defined in.
   */
  int getDeclStartLine(Env env);

  /**
   * Returns the end line in the file where this is defined in.
   */
  int getDeclEndLine(Env env);

  /**
   * Returns the comment in the file where this is defined in.
   */
  String getDeclComment(Env env);

  /**
   * Returns true if this returns a reference.
   */
  boolean isReturnsReference(Env env);

  /**
   * Returns the formal arguments.
   */
  Arg[] getArgs(Env env);

  V<? extends ValueOrVar> []NULL_ARG_VALUES = new V[0];

  /**
   * Evaluates the callback with no arguments.
   *
   * @param env the calling environment
   */
  default @Nonnull
  V<? extends ValueOrVar> call(Env env, FeatureExpr ctx) {
    return call(env, ctx, NULL_ARG_VALUES);
  }

  /**
   * Evaluates the callback with 1 arguments.
   *  @param env the calling environment
   * @param a1
   */
  default V<? extends ValueOrVar> call(Env env, FeatureExpr ctx, V<? extends ValueOrVar> a1) {
    return call(env, ctx, new V[] {a1});
  }

  @Deprecated@VDeprecated
  default @Nonnull V<? extends ValueOrVar> call(Env env, FeatureExpr ctx, Value a1, Value a2){
    return this.call(env, ctx, V.one(a1), V.one(a2));
  }
  @Deprecated@VDeprecated
  default @Nonnull V<? extends ValueOrVar> call(Env env, FeatureExpr ctx, Value a1){
    return this.call(env, ctx, V.one(a1));
  }
  @Deprecated@VDeprecated
  default @Nonnull V<? extends ValueOrVar> call(Env env, FeatureExpr ctx, Value[] args){
    return this.call(env, ctx, VHelper.toVArray(args));
  }
  @Deprecated@VDeprecated
  default @Nonnull V<? extends ValueOrVar> call(Env env, FeatureExpr ctx, Value a1, Value a2, Value a3){
    return this.call(env, ctx, V.one(a1), V.one(a2), V.one(a3));
  }
  @Deprecated@VDeprecated
  default @Nonnull V<? extends ValueOrVar> call(Env env, FeatureExpr ctx, Value a1, Value a2, Value a3, Value a4, Value a5){
    return this.call(env, ctx, V.one(a1), V.one(a2), V.one(a3), V.one(a4), V.one(a5));
  }
  /**
   * Evaluates the callback with 2 arguments.
   *
   * @param env the calling environment
   */
  default @Nonnull V<? extends ValueOrVar> call(Env env, FeatureExpr ctx, V<? extends ValueOrVar> a1, V<? extends ValueOrVar> a2){
    return call(env, ctx, new V[] {a1, a2});
  }

  /**
   * Evaluates the callback with 3 arguments.
   *
   * @param env the calling environment
   */
  default @Nonnull V<? extends ValueOrVar> call(Env env, FeatureExpr ctx, V<? extends ValueOrVar> a1, V<? extends ValueOrVar> a2, V<? extends ValueOrVar> a3){
    return call(env, ctx, new V[] {a1, a2, a3});
  }

  /**
   * Evaluates the callback with 4 arguments.
   *
   * @param env the calling environment
   */
  default @Nonnull V<? extends ValueOrVar> call(Env env, FeatureExpr ctx, V<? extends ValueOrVar> a1, V<? extends ValueOrVar> a2, V<? extends ValueOrVar> a3,
                             V<? extends ValueOrVar> a4){
    return call(env, ctx, new V[] {a1, a2, a3, a4});
  }

  /**
   * Evaluates the callback with 5 arguments.
   *
   * @param env the calling environment
   */
  default @Nonnull V<? extends ValueOrVar> call(Env env, FeatureExpr ctx, V<? extends ValueOrVar> a1, V<? extends ValueOrVar> a2, V<? extends ValueOrVar> a3,
                             V<? extends ValueOrVar> a4, V<? extends ValueOrVar> a5){
    return call(env, ctx, new V[] {a1, a2, a3, a4, a5});
  }

  /**
   * Evaluates the callback with variable arguments.
   *
   * @param env the calling environment
   */
  @Nonnull
  V<? extends ValueOrVar> call(Env env, FeatureExpr ctx, V<? extends ValueOrVar>[] args);

  /**
   * Evaluates a callback where the first argument is from an array.
   * The callback may be modifying that array element.
   * For ArrayModule.
   *
   * @param env
   * @param array from which a1 came from
   * @param key index of a1 in the array
   * @param a1 need to make a reference to this variable
   */
  @Nonnull V<? extends Value> callArray(Env env, FeatureExpr ctx,
                                        ArrayValue array,
                                        Value key,
                                        V<? extends ValueOrVar> a1);

  /**
   * Evaluates a callback where the first argument is from an array.
   * The callback may be modifying that array element.
   * For ArrayModule.
   *
   * @param env
   * @param array from which a1 came from
   * @param key index of a1 in the array
   * @param a1 need to make a reference to this variable
   * @param a2 additional argument to pass to the callback
   */
  @Nonnull V<? extends Value> callArray(Env env, FeatureExpr ctx,
                                        ArrayValue array,
                                        Value key,
                                        V<? extends ValueOrVar> a1,
                                        V<? extends ValueOrVar> a2);

  /**
   * Evaluates a callback where the first argument is from an array.
   * The callback may be modifying that array element.
   * For ArrayModule.
   *
   * @param env
   * @param array from which a1 came from
   * @param key index of a1 in the array
   * @param a1 need to make a reference to this variable
   * @param a2 additional argument to pass to the callback
   * @param a3 additional argument to pass to the callback
   */
  @Nonnull V<? extends Value> callArray(Env env, FeatureExpr ctx,
                                        ArrayValue array,
                                        Value key,
                                        V<? extends ValueOrVar> a1,
                                        V<? extends ValueOrVar> a2,
                                        V<? extends ValueOrVar> a3);
}

