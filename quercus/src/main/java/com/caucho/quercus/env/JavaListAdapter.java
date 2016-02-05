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

import com.caucho.quercus.program.JavaClassDef;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.UnimplementedVException;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a marshalled Collection argument.
 */
public class JavaListAdapter
  extends JavaCollectionAdapter
{
  private static final Logger log
    = Logger.getLogger(JavaListAdapter.class.getName());

  //XXX: parameterized type
  private List _list;
  
  private int _next = 0;

  public JavaListAdapter(Env env, List list)
  {
    this(list, env.getJavaClassDefinition(list.getClass()));
  }
  
  public JavaListAdapter(List list, JavaClassDef def)
  {
    super(list, def);
    _list = list;
  }

  /**
   * Adds a new value.
   */
  @Override
  public Value putImpl(Value key, Value value)
  {
    int pos = key.toInt();
    int size = getSize().getOne();

    if (0 <= pos && pos <= size) {
      if (pos < size) {
        _list.set(pos, value.toJavaObject());
      }
      else
        _list.add(pos, value.toJavaObject());

      return value;
    }
    else {
      getEnv().warning(L.l("index {0} is out of range", pos));
      log.log(Level.FINE, L.l("index {0} is out of range", pos));
 
      return UnsetValue.UNSET; 
    }
  }
  
  /**
   * Gets a new value.
   */
  @Override
  public EnvVar get(Value key)
  { 
    int pos = key.toInt();
    
    if (0 <= pos && pos < getSize().getOne())
      return EnvVar._gen( wrapJava(_list.get(pos)));
    else
      return EnvVar._gen(UnsetValue.UNSET);
  }

  /**
   * Removes a value.
   */
  @Override
  public V<? extends Value> remove(FeatureExpr ctx, Value key)
  {
    int pos = key.toInt();
    
    if (0 <= pos && pos < getSize().getOne())
      return V.one(wrapJava(_list.remove(pos)));
    else
      return V.one(UnsetValue.UNSET);
  }

  /**
   * Pops the top value.
   */
  @Override
  public V<? extends Value> pop(Env env, FeatureExpr ctx)
  {    
    if (getSize().getOne() == 0)
      return V.one(NullValue.NULL);
    
    return V.one(wrapJava(_list.remove(0)));
  }
  
  /**
   * Returns the corresponding key if this array contains the given value
   *
   * @param value  the value to search for in the array
   *
   * @return the key if it is found in the array, NULL otherwise
   *
   * @throws NullPointerException
   */
  @Override
  public V<? extends Value> contains(Value value)
  {
    throw new UnimplementedVException();
//    for (VEntry entry : entrySet()) {
//      if (entry.getEnvVar().equals(value))
//        return entry.getKey();
//    }
//
//    return NullValue.NULL;
  }
  
  /**
   * Returns the current value.
   */
  @Override
  public V<? extends Value> current()
  {
    if (_next < _list.size())
      return V.one(wrapJava(_list.get(_next)));
    else
      return V.one(BooleanValue.FALSE);
  }

  /**
   * Returns the current key
   */
  @Override
  public V<? extends Value> key()
  {    
    if (_next < _list.size())
      return V.one(LongValue.create(_next));
    else
      return V.one(NullValue.NULL);
  }

  /**
   * Returns true if there are more elements.
   */
  @Override
  public V<? extends Boolean> hasCurrent()
  {
    return V.one(_next < _list.size());
  }

  /**
   * Returns the next value.
   * @param ctx
   */
  @Override
  public V<? extends Value> next(FeatureExpr ctx)
  {
    if (_next < _list.size())
      return V.one(wrapJava(_list.get(_next++)));
    else
      return V.one(BooleanValue.FALSE);
  }

  /**
   * Returns the previous value.
   * @param ctx
   */
  @Override
  public V<? extends Value> prev(FeatureExpr ctx)
  {
    if (_next > 0)
      return V.one(wrapJava(_list.get(_next--)));
    else
      return V.one(BooleanValue.FALSE);
  }

  /**
   * The each iterator
   */
  @Override
  public Value each()
  {
    if (_next < _list.size())
    {
      ArrayValue result = new ArrayValueImpl();

      result.put(VHelper.noCtx(),LongValue.ZERO, key());
      result.put(VHelper.noCtx(),KEY, key());

      result.put(VHelper.noCtx(),LongValue.ONE, current());
      result.put(VHelper.noCtx(),VALUE, current());

      _next++;

      return result;
    }
    else
      return NullValue.NULL;
  }

  /**
   * Returns the first value.
   * @param ctx
   */
  @Override
  public V<? extends Value> reset(FeatureExpr ctx)
  {
    _next = 0;

    return current();
  }

  /**
   * Returns the last value.
   * @param ctx
   */
  @Override
  public V<? extends Value> end(FeatureExpr ctx)
  {
    _next = _list.size();
    
    return current();
  }
  

  /**
   * Copy for assignment.
   */
  @Override
  public Value copy()
  {
    return new JavaListAdapter(_list, getClassDef());
  }

  /**
   * Copy for serialization
   */
  @Override
  public Value copy(Env env, IdentityHashMap<Value, EnvVar> map)
  {
    return new JavaListAdapter(_list, getClassDef());
  }
}
