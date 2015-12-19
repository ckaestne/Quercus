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

import com.caucho.quercus.QuercusRuntimeException;
import com.caucho.quercus.program.JavaClassDef;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * Represents a marshalled Map argument.
 */
public class JavaMapAdapter
  extends JavaAdapter
{
  private static final Logger log
    = Logger.getLogger(JavaMapAdapter.class.getName());
  
  private Map<Object,Object> _map;
  
  private long _nextAvailableIndex;

  public JavaMapAdapter(Env env, Map map)
  {
    this(map, env.getJavaClassDefinition(map.getClass()));
  }
  
  public JavaMapAdapter(Map map, JavaClassDef def)
  {
    super(map, def);
    
    _map = map;
    
    updateNextAvailableIndex();
  }

  /**
   * Clears the array
   */
  @Override
  public void clear()
  {
    _map.clear();
    
    _nextAvailableIndex = 0;
  }

  public int size()
  {
    return _map.size();
  }

  /**
   * Converts to a java object.
   */
  @Override
  public Object toJavaObject(Env env, Class type)
  {
    if (type.isAssignableFrom(_map.getClass())) {
      return _map;
    }
    else {
      env.warning(L.l("Can't assign {0} to {1}",
              _map.getClass().getName(), type.getName()));
    
      return null;
    }
  }

  /**
   * Copy for assignment.
   */
  @Override
  public Value copy()
  {
    try {
      return new JavaMapAdapter(_map, getClassDef());
    }
    catch (Exception e) {
      throw new QuercusRuntimeException(e);
    }
  }

  /**
   * Copy for serialization
   */
  @Override
  public Value copy(Env env, IdentityHashMap<Value, EnvVar> map)
  {
    return new JavaMapAdapter(_map, getClassDef());
  }
  
  /**
   * Returns the size.
   */
  @Override
  public int getSize()
  {
    return size();
  }

  /**
   * Gets a new value.
   */
  @Override
  public EnvVar get(Value key)
  {
    Object obj = _map.get(key.toJavaObject());
    
    if (obj != null)
      return EnvVar._gen(wrapJava(_map.get(key.toJavaObject())));
    else
      return EnvVar._gen(UnsetValue.UNSET);
  }
  
  /**
   * Removes a value.
   */
  @Override
  public V<? extends Value> remove(FeatureExpr ctx, Value key)
  {
    updateNextAvailableIndex();

    if (key.isLongConvertible() || key instanceof BooleanValue) {
    //if (key instanceof LongValue) {
      long pos = key.toLong();
      
      Object value = _map.remove(Long.valueOf(pos));
      
      if (value != null) {
        if (pos + 1 == _nextAvailableIndex)
          updateNextAvailableIndex();
        
        return V.one(wrapJava(value));
      }
    }
    else {
      Object value = _map.remove(key.toJavaObject());
      
      if (value != null)
        return V.one(wrapJava(value));
    }
    
    return V.one(UnsetValue.UNSET);
  }
  
  /**
   * Creatse a tail index.
   * @param ctx
   */
  @Override
  public V<? extends Value> createTailKey(FeatureExpr ctx)
  {
    updateNextAvailableIndex();
    return V.one(LongValue.create(_nextAvailableIndex));
  }
  
  /**
   * Adds a new value.
   */
  @Override
  public Value putImpl(Value key, Value value)
  {
    Object keyObject;
    
    if (key.isLongConvertible() || key instanceof BooleanValue) {
      keyObject = Long.valueOf(key.toLong());
    }
    else {
      keyObject = key.toJavaObject();
    }

    Value val = wrapJava(_map.put(keyObject, value.toJavaObject()));

    updateNextAvailableIndex(keyObject);
    
    return val;
  }

  /**
   * Returns the corresponding valeu if this array contains the given key
   *
   * @param key  the key to search for in the array
   *
   * @return the value if it is found in the array, NULL otherwise
   */
  @Override
  public V<? extends Value> containsKey(Value key)
  {
    return V.one(BooleanValue.create(_map.containsKey(key.toJavaObject())));
  }
  
  @Override
  public Iterator<Value> getKeyIterator(Env env)
  {
    return new KeyIterator();
  }

  @Override
  public Iterator<EnvVar> getValueIterator(Env env)
  {
    return new ValueIterator();
  }
  
  @Override
  public Iterator<VEntry> getIterator(Env env)
  {
    return new MapIterator();
  }

  /**
   * Returns a set of all the of the entries.
   */
  @Override
  public Set<VEntry> entrySet()
  {
    return new MapSet();
  }
  
  /**
   * Returns a collection of the values.
   */
  @Override
  public Set<Map.Entry<Object, Object>> objectEntrySet()
  {
    return _map.entrySet();
  }

  /**
   * Returns a collection of the values.
   */
  @Override
  public Collection<EnvVar> values()
  {
    return new ValueCollection();
  }

  /**
   * Updates _nextAvailableIndex on a remove of the highest value
   */
  private void updateNextAvailableIndex()
  {
    _nextAvailableIndex = 0;

    for (Object key : _map.keySet()) {
      updateNextAvailableIndex(key);
    }
  }
  
  /**
   * Updates _nextAvailableIndex.
   */
  private void updateNextAvailableIndex(Object objectKey)
  { 
    if (objectKey instanceof Long) {
      long key = ((Long)objectKey).longValue();
    
      if (_nextAvailableIndex <= key)
        _nextAvailableIndex = key + 1;
    }
  }

  public class MapSet
    extends AbstractSet<VEntry>
  {
    MapSet()
    {
    }

    @Override
    public int size()
    {
      return getSize();
    }

    @Override
    public Iterator<VEntry> iterator()
    {
      return new MapIterator();
    }
  }

  public class MapIterator
    implements Iterator<VEntry>
  {
    private Iterator<Map.Entry<Object,Object>> _iterator;

    public MapIterator()
    {
      _iterator = _map.entrySet().iterator();
    }

    @Override
    public boolean hasNext()
    {
      return _iterator.hasNext();
    }

    @Override
    public VEntry next()
    {
      Map.Entry entry = _iterator.next();
      
      Value key = wrapJava(entry.getKey());
      EnvVar value = EnvVar._gen(wrapJava(entry.getValue()));

      return new Entry(VHelper.noCtx(), key, value);
    }

    @Override
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }

  public class ValueCollection
    extends AbstractCollection<EnvVar>
  {
    ValueCollection()
    {
    }

    @Override
    public int size()
    {
      return getSize();
    }

    @Override
    public Iterator<EnvVar> iterator()
    {
      return new ValueIterator();
    }
  }

  public class ValueIterator
    implements Iterator<EnvVar>
  {
    private Iterator _iterator;

    public ValueIterator()
    {
      _iterator = _map.values().iterator();
    }

    @Override
    public boolean hasNext()
    {
      return _iterator.hasNext();
    }

    @Override
    public EnvVar next()
    {
      return EnvVar._gen(wrapJava(_iterator.next()));
    }

    @Override
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
  
  public class KeyIterator
    implements Iterator<Value>
  {
    private Iterator _iterator;

    public KeyIterator()
    {
      _iterator = _map.keySet().iterator();
    }

    @Override
    public boolean hasNext()
    {
      return _iterator.hasNext();
    }

    @Override
    public Value next()
    {
      return wrapJava(_iterator.next());
    }

    @Override
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
  @Override
  public <T> V<? extends T> foldRightUntil(V<? extends T> init, FeatureExpr ctx, Function4<FeatureExpr, Entry, T, V<? extends T>> op, Predicate<T> stopCriteria) {
    throw new UnimplementedVException();
//    return VList.foldRightUntil(new OptEntryIterator(_head), init, ctx, op, stopCriteria);
  }
}
