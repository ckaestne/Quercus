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
import edu.cmu.cs.varex.Function4;
import edu.cmu.cs.varex.UnimplementedVException;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;

import java.util.*;
import java.util.function.Predicate;

/**
 * Represents a marshalled Collection argument.
 */
public class JavaCollectionAdapter extends JavaAdapter
{
  private Collection<Object> _collection;

  public JavaCollectionAdapter(Collection<Object> coll, JavaClassDef def)
  {
    super(coll, def);

    _collection = coll;
  }

  /**
   * Clears the array
   */
  @Override
  public void clear()
  {
    _collection.clear();
  }

  //
  // Conversions
  //

  /**
   * Copy for assignment.
   */
  @Override
  public Value copy()
  {
    return new JavaCollectionAdapter(_collection, getClassDef());
  }

  /**
   * Copy for serialization
   */
  @Override
  public Value copy(Env env, IdentityHashMap<Value, EnvVar> map)
  {
    return new JavaCollectionAdapter(_collection, getClassDef());
  }

  /**
   * Returns the size.
   */
  @Override
  public int getSize()
  {
    return _collection.size();
  }

  /**
   * Creatse a tail index.
   * @param ctx
   */
  @Override
  public V<? extends Value> createTailKey(FeatureExpr ctx)
  {
    return V.one(LongValue.create(getSize()));
  }

  @Override
  public Value putImpl(Value key, Value value)
  {
    if (key.toInt() != getSize())
      throw new UnsupportedOperationException(
        "random assignment into Collection");

    _collection.add(value.toJavaObject());

    return value;
  }

  /**
   * Gets a new value.
   */
  @Override
  public EnvVar get(Value key)
  {
    int pos = key.toInt();

    if (pos < 0)
      return EnvVar._gen(UnsetValue.UNSET);

    for (Object obj : _collection) {
      if (pos-- > 0)
        continue;

      return EnvVar._gen(wrapJava(obj));
    }

    return EnvVar._gen(UnsetValue.UNSET);
  }

  /**
   * Removes a value.
   */
  @Override
  public V<? extends Value> remove(FeatureExpr ctx, Value key)
  {
    int pos = key.toInt();

    if (pos < 0)
      return V.one(UnsetValue.UNSET);

    for (Object obj : _collection) {
      if (pos-- > 0)
        continue;

      Value val = wrapJava(obj);

      _collection.remove(obj);
      return V.one(val);
    }

    return V.one(UnsetValue.UNSET);
  }

  /**
   * Returns a set of all the of the entries.
   */
  @Override
  public Set<VEntry> entrySet()
  {
    return new CollectionValueSet();
  }

  /**
   * Returns a collection of the values.
   */
  @Override
  public Set<Map.Entry<Object,Object>> objectEntrySet()
  {
    return new CollectionSet();
  }

  /**
   * Returns a collection of the values.
   */
  @Override
  public Collection<EnvVar> values()
  {
    return new ValueCollection();
  }

  @Override
  public Iterator<VEntry> getIterator(Env env)
  {
    return new CollectionValueIterator();
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

  public class CollectionSet
    extends AbstractSet<Map.Entry<Object,Object>>
  {
    CollectionSet()
    {
    }

    @Override
    public int size()
    {
      return getSize();
    }

    @Override
    public Iterator<Map.Entry<Object,Object>> iterator()
    {
      return new CollectionIterator();
    }
  }

  public class CollectionIterator
    implements Iterator<Map.Entry<Object,Object>>
  {
    private int _index;
    private Iterator _iterator;

    public CollectionIterator()
    {
      _index = 0;
      _iterator = _collection.iterator();
    }

    @Override
    public boolean hasNext()
    {
      return _iterator.hasNext();
    }

    @Override
    public Map.Entry<Object, Object> next()
    {
      return new CollectionEntry(_index++, _iterator.next());
    }

    @Override
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }

  public static class CollectionEntry
    implements Map.Entry<Object,Object>
  {
    private final int _key;
    private Object _value;

    public CollectionEntry(int key, Object value)
    {
      _key = key;
      _value = value;
    }

    @Override
    public Object getKey()
    {
      return _key;
    }

    @Override
    public Object getValue()
    {
      return _value;
    }

    @Override
    public Object setValue(Object value)
    {
      Object oldValue = _value;

      _value = value;

      return oldValue;
    }
  }

  public class CollectionValueSet
    extends AbstractSet<VEntry>
  {
    CollectionValueSet()
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
      return new CollectionValueIterator();
    }
  }

  public class CollectionValueIterator
    implements Iterator<VEntry>
  {
    private int _index;
    private Iterator _iterator;

    public CollectionValueIterator()
    {
      _index = 0;
      _iterator = _collection.iterator();
    }

    @Override
    public boolean hasNext()
    {
      return _iterator.hasNext();
    }

    @Override
    public VEntry next()
    {
       Value val = wrapJava(_iterator.next());

       return new Entry(VHelper.noCtx(), LongValue.create(_index++), EnvVar._gen(val));
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

  public class KeyIterator
    implements Iterator<Value>
  {
    private int _index;
    private Iterator _iterator;

    public KeyIterator()
    {
      _index = 0;
      _iterator = _collection.iterator();
    }

    @Override
    public boolean hasNext()
    {
      return _iterator.hasNext();
    }

    @Override
    public Value next()
    {
      _iterator.next();

      return LongValue.create(_index++);
    }

    @Override
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }

  public class ValueIterator
    implements Iterator<EnvVar>
  {
    private Iterator _iterator;

    public ValueIterator()
    {
      _iterator = _collection.iterator();
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
  @Override
  public <T> V<? extends T> foldRightUntil(V<? extends T> init, FeatureExpr ctx, Function4<FeatureExpr, Entry, T, V<? extends T>> op, Predicate<T> stopCriteria) {
    throw new UnimplementedVException();
//    return VList.foldRightUntil(new OptEntryIterator(_head), init, ctx, op, stopCriteria);
  }

}
