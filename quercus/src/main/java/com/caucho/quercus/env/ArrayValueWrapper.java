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
import edu.cmu.cs.varex.Function4;
import edu.cmu.cs.varex.UnimplementedVException;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * Represents a PHP array value.
 */
public class ArrayValueWrapper extends ArrayValue {
  private ArrayValue _array;

  protected ArrayValueWrapper(ArrayValue array)
  {
    _array = array;
  }

  /**
   * Returns the wrapped array.
   */
  @Override
  public ArrayValue getArray()
  {
    return _array;
  }
  
  /**
   * Copy for assignment.
   */
  @Override
  public Value copy()
  {
    return _array.copy();
  }
  
  /**
   * Copy for assignment.
   */
  @Override
  public Value copySaveFunArg()
  {
    return _array.copySaveFunArg();
  }
  
  /**
   * Copy for serialization
   */
  @Override
  public Value copy(Env env, IdentityHashMap<Value, EnvVar> map)
  {
    return _array.copy(env, map);
  }

  /**
   * Returns the size.
   */
  @Override
  public int getSize()
  {
    return _array.getSize();
  }

  /**
   * Clears the array
   */
  @Override
  public void clear()
  {
    _array.clear();
  }
  
  /**
   * Adds a new value.
   */
  @Override
  public V<? extends ValueOrVar> put(FeatureExpr ctx, Value key, V<? extends ValueOrVar> value)
  {
    return _array.put(ctx, key, value);
  }
  
  /**
   * Adds a new value.
   */
  @Override
  public Value append(FeatureExpr ctx, Value index, V<? extends ValueOrVar> value)
  {
    return _array.append(ctx, index, value);
  }

  /**
   * Add
   */
  @Override
  public V<? extends ValueOrVar> put(FeatureExpr ctx, V<? extends ValueOrVar> value)
  {
    return _array.put(VHelper.noCtx(), value);
  }

  /**
   * Add to front.
   */
  @Override
  public ArrayValue unshift(Value value)
  {
    return _array.unshift(value);
  }

  /**
   * Splices values
   */
  @Override
  public ArrayValue splice(int start, int end, ArrayValue replace)
  {
    return _array.splice(start, end, replace);
  }

  /**
   * Returns the value as an argument which may be a reference.
   */
  @Override
  public EnvVar getArg(Value index, boolean isTop)
  {
    return _array.getArg(index, isTop);
  }

  /**
   * Sets the array ref.
   * @param ctx
   */
  @Override
  public V<? extends Var> putVar(FeatureExpr ctx)
  {
    return _array.putVar(ctx);
  }

  /**
   * Creatse a tail index.
   * @param ctx
   */
  @Override
  public V<? extends Value> createTailKey(FeatureExpr ctx)
  {
    return _array.createTailKey(VHelper.noCtx());
  }

  /**
   * Gets a new value.
   */
  @Override
  public EnvVar get(Value key)
  {
    return _array.get(key);
  }

  /**
   * Removes a value.
   */
  @Override
  public V<? extends Value> remove(FeatureExpr ctx, Value key)
  {
    return _array.remove(ctx, key);
  }

  @Override
  public ArrayValue append(Value key, EnvVar value) {
    throw new UnimplementedVException();
  }

  /**
   * Returns true if the index isset().
   */
  @Override
  public boolean isset(Value key)
  {
    return _array.isset(key);
  }

  /**
   * Returns the array ref.
   */
  @Override
  public EnvVar getVar(Value index)
  {
    return _array.getVar(index);
  }
  
  /**
   * Pops the top value.
   */
  @Override
  public V<? extends Value> pop(Env env, FeatureExpr ctx)
  {
    return _array.pop(env, ctx);
  }

  /**
   * Shuffles the array
   */
  @Override
  public Value shuffle()
  {
    return _array.shuffle();
  }

  /**
   * Returns the head.
   */
  @Override
  public Entry getHead()
  {
    return _array.getHead();
  }

  /**
   * Returns the tail.
   */
  @Override
  protected Entry getTail()
  {
    return _array.getTail();
  }
  
  /**
   * Returns the current value.
   */
  @Override
  public V<? extends Value> current()
  {
    return _array.current();
  }

  /**
   * Returns the current key
   */
  @Override
  public V<? extends Value> key()
  {
    return _array.key();
  }

  /**
   * Returns true if there are more elements.
   */
  @Override
  public V<? extends Boolean> hasCurrent()
  {
    return _array.hasCurrent();
  }

  /**
   * Returns the next value.
   * @param ctx
   */
  @Override
  public V<? extends Value> next(FeatureExpr ctx)
  {
    return _array.next(ctx);
  }

  /**
   * Returns the previous value.
   * @param ctx
   */
  @Override
  public V<? extends Value> prev(FeatureExpr ctx)
  {
    return _array.prev(ctx);
  }

  /**
   * The each iterator
   */
  @Override
  public Value each()
  {
    return _array.each();
  }

  /**
   * Returns the first value.
   * @param ctx
   */
  @Override
  public V<? extends Value> reset(FeatureExpr ctx)
  {
    return _array.reset(ctx);
  }

  /**
   * Returns the last value.
   * @param ctx
   */
  @Override
  public V<? extends Value> end(FeatureExpr ctx)
  {
    return _array.end(ctx);
  }
  
  /**
   * Returns the corresponding key if this array contains the given value
   *
   * @param key to search for in the array
   *
   * @return the key if it is found in the array, NULL otherwise
   */
  @Override
  public V<? extends Value> contains(Value key)
  {
    return _array.contains(key);
  }
  
  /**
   * Returns the corresponding key if this array contains the given value
   *
   * @param key to search for in the array
   *
   * @return the key if it is found in the array, NULL otherwise
   */
  @Override
  public V<? extends Value> containsStrict(Value key)
  {
    return _array.containsStrict(key);
  }
  
  /**
   * Returns the corresponding value if this array contains the given key
   * 
   * @param key to search for in the array
   *
   * @return the value if it is found in the array, NULL otherwise
   */
  @Override
  public V<? extends Value> containsKey(Value key)
  {
    return _array.containsKey(key);
  }

  @Override
  public Value add(Value rValue)
  {
    return _array.add(rValue);
  }

  @Override
  public Iterator<VEntry> getIterator(Env env)
  {
    return _array.getIterator(env);
  }

  @Override
  public Iterator<Value> getKeyIterator(Env env)
  {
    return _array.getKeyIterator(env);
  }

  @Override
  public Iterator<EnvVar> getValueIterator(Env env)
  {
    return _array.getValueIterator(env);
  }

  @Override
  public <T> V<? extends T> foldRightUntil(V<? extends T> init, FeatureExpr ctx, Function4<FeatureExpr, Entry, T, V<? extends T>> op, Predicate<T> stopCriteria) {
    return _array.foldRightUntil(init, ctx, op, stopCriteria);
  }
}

