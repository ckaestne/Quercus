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
import edu.cmu.cs.varex.UnimplementedVException;
import edu.cmu.cs.varex.V;
import javax.annotation.Nonnull;

/**
 * Represents a call to a function.
 */
@SuppressWarnings("serial")
abstract public class Callback extends Value implements Callable {

  @Override
  public Callable toCallable(Env env, boolean isOptional)
  {
    return this;
  }

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
  @Override
  final public @Nonnull V<? extends Value> callArray(Env env, FeatureExpr ctx,
                               ArrayValue array,
                               Value key,
                                                     V<? extends ValueOrVar> a1)
  {
    throw new UnimplementedVException();
//    // php/1740
//
//    V<? extends Value> result;
//
//    if (a1.isVar()) {
//      a1 = new ArgRef(a1._var());
//
//      result = call(env, ctx, a1._value());
//    }
//    else {
//      Var aVar = new Var(V.one(a1._value()));
//
//      result = call(env, ctx, aVar.toValue()); //TODO check V
//
//      Value aNew = aVar.toValue();
//
//      if (aNew != a1)
//        array.put(key, aNew);
//    }
//
//    return result;
  }

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
  @Override
  final public @Nonnull V<? extends Value> callArray(Env env, FeatureExpr ctx,
                               ArrayValue array,
                               Value key,
                                                     V<? extends ValueOrVar> a1,
                                                     V<? extends ValueOrVar> a2)
  {
    throw new UnimplementedVException();
//    // php/1740
//
//    V<? extends Value> result;
//
//    if (a1.isVar()) {
//      a1 = new ArgRef(a1._var());
//
//      result = call(env, ctx, a1, a2);
//    }
//    else {
//      Value aVar = new Var(V.one(a1)).toValue();
//
//      result = call(env, ctx, aVar, a2);
//
//      Value aNew = aVar.toValue();
//
//      if (aNew != a1)
//        array.put(key, aNew);
//    }
//
//    return result;
  }

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
  @Override
  final public @Nonnull V<? extends Value> callArray(Env env, FeatureExpr ctx,
                               ArrayValue array,
                               Value key,
                                                     V<? extends ValueOrVar> a1,
                                                     V<? extends ValueOrVar> a2,
                                                     V<? extends ValueOrVar> a3)
  {
    // php/1740
throw new UnimplementedVException();
//    V<? extends Value> result;
//
//    if (a1.getOne().isVar()) {
//      a1 = a1.map(a-> new ArgRef(a._var()));
//
//      result = call(env, ctx, a1, a2, a3);
//    }
//    else {
//      V<? extends Var> aVar = a1.map(a-> new Var(V.one(a)).toValue());
//
//      result = call(env, ctx, aVar, a2, a3);
//
//      V<? extends Value> aNew = aVar.map(a->a.toValue());
//
//      if (aNew != a1)
//        array.put(key, aNew.getOne());
//    }
//
//    return result;
  }

  /**
   * Evaluates the callback with variable arguments.
   * @param env the calling environment
   * @param ctx
   * @param args
   */
  abstract public V<? extends ValueOrVar> call(Env env, FeatureExpr ctx, V<? extends ValueOrVar>[] args);

  /**
   *
   * @return true if this is an invalid callback reference
   */
  @Override
  abstract public boolean isValid(Env env);

  /**
   * Returns the name of the callback.
   *
   */
  abstract public String getCallbackName();

  /**
   * Returns true if this callback is implemented internally (i.e. in Java).
   *
   */
  abstract public boolean isInternal(Env env);

  public String toString()
  {
    return getClass().getSimpleName() + "[" + getCallbackName() + "]";
  }
}

