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

import com.caucho.util.RandomUtil;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.IdentityHashMap;
import java.util.function.Predicate;

/**
 * Represents a PHP array value.
 * <p>
 * A variational array is implemented as a linked list of optional entries.
 * Every entry has a condition and exactly one key and a variational
 * value (modeled as EnvVar) for that key.
 * <p>
 * Invariant: Under all configurations
 * allowed by the configuration, EnvVar is assumed to hold a value.
 * <p>
 * Invariant: Keys are only unique across the configuration space, but there
 * can be multiple entries with the same key under different conditions
 * (this is necessary to allow different orderings in different configurations).
 * <p>
 * There is an additional vhashmap that looks up a set of elements. This is
 * only an additional structure for faster lookup, but otherwise not relevant
 * for the storage. The main storage all follows from _head.
 * <p>
 * The _size variable is also synchronized with the linked list to keep the
 * variational length of the list.
 */
public class ArrayValueImpl extends ArrayValue
        implements Serializable {
  private static final int DEFAULT_SIZE = 16;

  /**
   * _lookupMap is kept synchronous with the linked list
   * starting at _head; it is used for lookup operations
   */
  private @Nonnull VMap<Value, Entry> _lookupMap = new VHashMap<>();

  /**
   * _size is is kept syncronous with the linked list
   */
  private @Nonnull V<? extends Integer> _size = V.one(Integer.valueOf(0));

  /**
   * _nextAvailableIndex is null if it's not computed yet;
   * will be computed on demand
   */
  private
  @Nullable
  V<? extends Long> _nextAvailableIndex;
  private boolean _isDirty;

  /**
   * start and end of the linked list
   */
  private Entry _head;
  private Entry _tail;

//  private Entry[] _entries;
//  private int _hashMask;


  /**
   * possible delegation to other object if not null
   */
  private
  @Nullable
  ConstArrayValue _constSource;

  public ArrayValueImpl() {
    checkInvariants();
  }

  public ArrayValueImpl(int size) {
    _lookupMap = new VHashMap<>(size);
    checkInvariants();
  }

  public ArrayValueImpl(ArrayValue source) {
    // this(copy.getSize());

    for (Entry ptr = source.getHead(); ptr != null; ptr = ptr.getNext()) {
      // php/0662 for copy
      Entry entry = createNewEntry(ptr.getCondition(), ptr.getKey());

      /*
      if (ptr._var != null)
        entry._var = ptr._var;
      else
        entry._value = ptr._value.copyArrayItem();
      */
      entry.setEnvVar(ptr.getEnvVar().copy());
    }
    checkInvariants();
  }

  public ArrayValueImpl(ArrayValueImpl source) {
    copyFrom(source);
    checkInvariants();
  }

  protected void copyFrom(ArrayValueImpl source) {
    if (!source._isDirty)
      source._isDirty = true;

    _isDirty = true;

    _size = source._size;
    _lookupMap = source._lookupMap;
//    _entries = source._entries;
//    _hashMask = source._hashMask;

    _head = source._head;
    setCurrent(VHelper.True(), source.getCurrent());

    _tail = source._tail;
    _nextAvailableIndex = source._nextAvailableIndex;
  }

  public ArrayValueImpl(ConstArrayValue source) {
    throw new UnimplementedVException();
//    _constSource = source;
//
//    _isDirty = true;
//
//    _size = source.getSize();
//    _lookupMap = source._lookupMap;
//
//    _head = source.getHead();
//    setCurrent(source.getCurrent());
//    _tail = source.getTail();
//    _nextAvailableIndex = source.getNextAvailableIndex();
  }

//  public ArrayValueImpl(Env env,
//                        IdentityHashMap<Value,EnvVar> map,
//                        ArrayValue copy)
//  {
//    this();
//
//    map.put(copy, EnvVar._gen(this));
//
//    for (Entry ptr = copy.getHead(); ptr != null; ptr = ptr.getNext()) {
//      // Value value = ptr._var != null ? ptr._var.toValue() : ptr._value;
//      Value value = ptr.toValue().getOne();
//
//      append(ptr.getKey(), value.copy(env, map));
//    }
//  }

  /**
   * Copy for unserialization.
   * <p>
   * XXX: need to update for references
   */
  protected ArrayValueImpl(Env env, ArrayValue copy, CopyRoot root) {
    this();

    root.putCopy(copy, this);

    for (Entry ptr = copy.getHead(); ptr != null; ptr = ptr.getNext()) {
      // Value value = ptr._var != null ? ptr._var.toValue() : ptr._value;
      Value value = ptr.toValue().getOne();

      append(ptr.getKey(), value.copyTree(env, root));
    }
    checkInvariants();
  }

  public ArrayValueImpl(Value[] keys, Value[] values) {
    throw new UnimplementedVException();
//    this();
//
//    for (int i = 0; i < keys.length; i++) {
//      if (keys[i] != null)
//        append(keys[i], values[i]);
//      else
//        put(VHelper.noCtx(), values[i]);
//    }
  }

  public ArrayValueImpl(Value[] values) {
    this();

    for (int i = 0; i < values.length; i++) {
      put(VHelper.noCtx(), values[i]);
    }
  }

//  public ArrayValueImpl(Env env, ArrayValueComponent[] components)
//  {
//    for (int i = 0; i < components.length; i++) {
//      components[i].init(env);
//      components[i].addTo(this);
//    }
//  }
//
//  public ArrayValueImpl(ArrayValueComponent[] components)
//  {
//    for (int i = 0; i < components.length; i++) {
//      components[i].init();
//      components[i].addTo(this);
//    }
//  }


  protected V<? extends Long> getNextAvailableIndex() {
    return _nextAvailableIndex;
  }

  private void copyOnWrite() {
    if (!_isDirty)
      return;

    _constSource = null;

    _isDirty = false;

    _lookupMap = new VHashMap<>(_lookupMap.size());

    Entry prev = null;
    for (Entry ptr = _head; ptr != null; ptr = ptr.getNext()) {
      Entry ptrCopy = new Entry(ptr);

      addToLookupMap(ptrCopy);

      if (prev == null) {
        setCurrent(VHelper.noCtx(), V.one(ptrCopy));

        _head = ptrCopy;
      } else {
        prev.setNext(ptrCopy);
        ptrCopy.setPrev(prev);
      }

      prev = ptrCopy;
    }

    _tail = prev;
  }

  private void addToLookupMap(Entry e) {
    @Nonnull V<? extends Entry> entries = _lookupMap.getOrDefault(e.getKey(), V.one(null));
    checkEntryInvariant(entries);

    entries = V.choice(e.getCondition(), V.one(e), entries);
    _lookupMap.put(e.getKey(), entries);
  }

  /**
   * Returns the type.
   */
  public String getType() {
    return "array";
  }

  /**
   * Converts to a boolean.
   */
  public boolean toBoolean() {
    return _size.getOne() != 0;
  }

  /**
   * Converts to a string.
   *
   * @param env
   */
  public StringValue toString(Env env) {
    return env.createString("Array");
  }

  /**
   * Converts to an object.
   */
  public Object toObject() {
    return null;
  }

  /**
   * Copy the value.
   */
  @Override
  public Value copy() {
    // php/1704
    reset(VHelper.noCtx());

    Value copy = new ArrayValueImpl(this);
    // copy.reset();

    return copy;
  }

  /**
   * Copy for return.
   */
  @Override
  public Value copyReturn() {
    return new ArrayValueImpl(this);
  }

  /**
   * Copy for serialization
   */
  public Value copy(Env env, IdentityHashMap<Value, EnvVar> map) {
    throw new UnimplementedVException();
//    Value oldValue = map.get(this);
//
//    if (oldValue != null)
//      return oldValue;
//
//    return new ArrayValueImpl(env, map, this);
  }

  /**
   * Copy for serialization
   */
  @Override
  public Value copyTree(Env env, CopyRoot root) {
    // php/420d

    Value copy = root.getCopy(this);

    if (copy != null)
      return copy;
    else
      return new ArrayCopyValueImpl(env, this, root);
  }

  /**
   * Copy for saving a method's arguments.
   */
  public Value copySaveFunArg() {
    return new ArrayValueImpl(this);
  }

  /**
   * Convert to an argument value.
   */
  @Override
  public Value toLocalValue() {
    // php/1708

    Value copy = new ArrayValueImpl(this);
    copy.reset(VHelper.noCtx());

    return copy;
  }

  /**
   * Convert to an argument value.
   */
  @Override
  public V<? extends Value> toLocalRef() {
    // php/1708

    Value copy = new ArrayValueImpl(this);
    copy.reset(VHelper.noCtx());

    return V.one(copy);
  }

  /**
   * Convert to an argument declared as a reference
   */
  @Override
  public Value toRefValue() {
    return this;
  }

  /**
   * Returns the size.
   */
  public int size() {
    return _size.getOne();
  }

  /**
   * Returns the size.
   */
  public int getSize() {
    return size();
  }

  /**
   * Clears the array
   */
  public void clear() {
    if (_isDirty) {
      _isDirty = false;
    }

    _lookupMap = new VHashMap<>();

    _size = V.one(0);
    _head = _tail = null;
    setCurrent(VHelper.noCtx(), V.one(null));

    _nextAvailableIndex = V.one(Long.valueOf(0));
  }

  /**
   * Returns true for an array.
   */
  public boolean isArray() {
    return true;
  }

  /**
   * Adds a new value.
   */
  @Deprecated@Override
  public ArrayValue append(Value key, ValueOrVar value) {
    return this.append(VHelper.noCtx(), V.one(key), V.one(value));
  }

  @Override
  public ArrayValue append(FeatureExpr ctx, Value key, V<? extends ValueOrVar> value) {
    return this.append(ctx, V.one(key), value);
  }

  public ArrayValue append(FeatureExpr ctx, V<? extends Value> key, V<? extends ValueOrVar> value) {
    if (_isDirty) {
      copyOnWrite();
    }

    key = key.<Value>vflatMap(ctx, (c, k) -> k instanceof UnsetValue ? createTailKey(c) : V.one(k));

    key.vforeach(ctx, (c, k) -> {
      V<? extends Entry> entry = createEntry(c, k);

      // php/0434
      // Var oldVar = entry._var;

      entry.vforeach(c, (cc, a) -> {if (cc.isSatisfiable()) a.set(cc, value);});
    });
    checkInvariants();

    return this;
  }

  /**
   * Add to the beginning
   */
  public ArrayValue unshift(Value value) {
    throw new UnimplementedVException();

//    if (_isDirty)
//      copyOnWrite();
//
//    _size++;
//
//    Entry []entries = _entries;
//    if ((entries == null && _size >= MIN_HASH)
//        || (entries != null && entries.length <= 2 * _size)) {
//      expand();
//    }
//
//    Value key = createTailKey(VHelper.noCtx());
//
//    Entry entry = new Entry(key, EnvVar._gen(value.toLocalValue()));
//
//    addEntry(entry);
//
//    if (_head != null) {
//      _head._prev = entry;
//      entry.setNext(_head);
//      _head = entry;
//    }
//    else {
//      _head = _tail = entry;
//    }
//
//    return this;
  }

  /**
   * Replace a section of the array.
   */
  public ArrayValue splice(int start, int end, ArrayValue replace) {
    throw new UnimplementedVException();

//    if (_isDirty)
//      copyOnWrite();
//
//    int index = 0;
//
//    ArrayValueImpl result = new ArrayValueImpl();
//
//    Entry ptr = _head;
//    Entry nextPtr = null;
//    for (; ptr != null; ptr = nextPtr) {
//      nextPtr = ptr.getNext();
//
//      Value key = ptr.getKey();
//
//      if (index < start) {
//      }
//      else if (index < end) {
//        _size--;
//
//        Entry prev = ptr.getPrev();
//        Entry next = ptr.getNext();
//
//        if (prev != null)
//          prev.setNext(next);
//        else
//          _head = next;
//
//        if (next != null)
//          next.setPrev(prev);
//        else
//          _tail = prev;
//
//        if (key.isString())
//          result.put(key, ptr.getValue());
//        else
//          result.put(ptr.getValue().getOne());
//      }
//      else if (replace == null) {
//        return result;
//      }
//      else {
//        for (Entry replaceEntry = replace.getHead();
//             replaceEntry != null;
//             replaceEntry = replaceEntry.getNext()) {
//          _size++;
//
//          Entry []entries = _entries;
//          if ((entries == null && _size >= MIN_HASH)
//              || (entries != null && entries.length <= 2 * _size)) {
//            expand();
//          }
//
//          Entry entry = new Entry(createTailKey(VHelper.noCtx()), replaceEntry.getValue());
//
//          addEntry(entry);
//
//          Entry prev = ptr.getPrev();
//
//          entry.setNext(ptr);
//          entry.setPrev(prev);
//
//          if (prev != null)
//            prev.setNext(entry);
//          else
//            _head = entry;
//
//          ptr.setPrev(entry);
//        }
//
//        return result;
//      }
//
//      index++;
//    }
//
//    if (replace != null) {
//      for (Entry replaceEntry = replace.getHead();
//           replaceEntry != null;
//           replaceEntry = replaceEntry.getNext()) {
//        put(replaceEntry.getValue().getOne());
//      }
//    }
//
//    return result;
  }

  /**
   * Slices.
   */
  @Override
  public ArrayValue slice(Env env, int start, int end, boolean isPreserveKeys) {
    throw new UnimplementedVException();
//    ArrayValueImpl array = new ArrayValueImpl();
//
//    int i = 0;
//    for (Entry ptr = _head; i < end && ptr != null; ptr = ptr.getNext()) {
//      if (start > i++)
//        continue;
//
//      Value key = ptr.getKey();
//      Value value = ptr.getEnvVar().getOne();
//
//      if (isPreserveKeys || key.isString())
//        array.put(key, value);
//      else
//        array.put(value);
//    }
//
//    return array;
  }

  /**
   * Returns the value as an argument which may be a reference.
   */
  @Override
  public EnvVar getArg(Value index, boolean isTop) {
    if (_isDirty) // XXX: needed?
      copyOnWrite();

    // php/3d42
    //if (isTop)
    //return new ArgGetValue(this, index);

    V<? extends Entry> entries = getEntry(index);

    return new EnvVarImpl(entries.<Var>flatMap(entry -> {
      if (entry != null) {
        // php/3d48, php/39aj
        EnvVar value = entry.getEnvVar();

        // php/3d42
        return value.getValue().map(v -> {
          if (!isTop && v.isset())
            return new Var(V.one(v));
          else {
            // XXX: should probably have Entry extend ArgGetValue and return the Entry itself
            return new Var(V.one(new ArgGetValue(this, index))); // php/0d14, php/04b4
          }
        });
      } else {
        // php/3d49
        return V.one(new Var(V.one(new ArgGetValue(this, index))));
      }
    }));

  }

  /**
   * Returns the field value, creating an object if it's unset.
   */
  @Override
  public V<? extends Value> getObject(Env env, FeatureExpr ctx, Value fieldName) {
    EnvVar value = get(fieldName);

    return value.getValue().map((Value v)-> {
      if (!v.isset()) {
        v = env.createObject();

        put(fieldName, v);
      }

      return v;
    });
  }

  /**
   * Returns the value as an array.
   */
  @Override
  public V<? extends ValueOrVar> getArray(FeatureExpr ctx, Value index) {
    // php/3482, php/3483

    if (_isDirty)
      copyOnWrite();

    V<? extends Entry> entry = createEntry(ctx, index);

    return entry.<ValueOrVar>vflatMap(ctx, (cc, aentry) -> {
      V<? extends Var> var = aentry.getEnvVar().getVar();
      return var.<ValueOrVar>vmap(cc, (c, a) -> {
        Var result = a.toAutoArray();
        if (result != a) {
          aentry.set(c, V.one(a));
          return a;
        } else if (a.getValue().getOne().isString()) {
          // php/0482
          return aentry.toRef();
        } else {
          return a;
        }
      });
    });

  }

  /**
   * Returns the value as an array, using copy on write if necessary.
   */
  public V<? extends Value> getDirty(Value index) {
    if (_isDirty)
      copyOnWrite();

    return get(index).getValue();
  }


  @Deprecated@Override
  public EnvVar put(Value index, EnvVar value) {
    return EnvVar.fromValues(this.put(VHelper.noCtx(), index, value.getVar()).map((a)->a.toValue()));
  }

  /**
   * Add element to the end
   */
  @Override
  public V<? extends Value> put(FeatureExpr ctx, V<?extends Value> value) {

    if (_isDirty)
      copyOnWrite();

    V<? extends Value> key = createTailKey(ctx);

    append(ctx, key, value);
    checkInvariants();

    return value;
  }

  /**
   * Adds a new variable to the end of the array
   * @param ctx
   */
  @Override
  public V<? extends Var> putVar(FeatureExpr ctx) {
    if (_isDirty)
      copyOnWrite();

    // 0d0d
    V<? extends Value> tailKey = createTailKey(VHelper.noCtx());

    return tailKey.flatMap(key->getVar(key).getVar());
  }

  /**
   * Sets the array tail, returning a reference to the tail.
   */
  @Override
  public V<? extends Var> getArgTail(Env env, FeatureExpr ctx, boolean isTop) {
    if (_isDirty) {
      copyOnWrite();
    }

    V<? extends Value> tail = createTailKey(ctx);

    return tail.map(t->new Var(V.one(new ArgGetValue(this, t))));
  }

  /**
   * Creatse a tail index.
   *
   * @param ctx
   */
  public V<? extends Value> createTailKey(FeatureExpr ctx) {
    if (_nextAvailableIndex==null)
      updateNextAvailableIndex();

    return _nextAvailableIndex.map((a) -> LongValue.create(a));
  }

  /**
   * Gets a new value.
   *
   * If there are multiple conditional values/vars, they are merged into this EnvVar
   */
  @Override
  public EnvVar get(Value key) {
    return EnvVar.fromValues(this.getRaw(key).getValue());
  }

  /**
   * checking that multiple entries have nonoverlapping conditions
   *
   * also check that they have all the same key
   * @param entrySet
   */
  private void checkEntryInvariant(@Nonnull V<? extends Entry> entrySet) {
    assert entrySet != null : "entrySet is null";
    if (entrySet.equals(V.one(null)))
      return;

    Ref sharedKey = new Ref();
    entrySet.vforeach(VHelper.True(), (c, e) -> {
      if (e != null) {
        if (sharedKey.v == null)
          sharedKey.v = e.getKey();
        else
          assert sharedKey.v.equals(e.getKey()) : "entry set with different keys found";
        assert c.equivalentTo(e.getCondition()) : "entries with unexpected/inconsistent condition: entry has condition " + e.getCondition() + " but used in context " + c;
      }
    });
  }

  private static class Ref {
    Value v = null;
  }

  /**
   * Returns the value in the array as-is.
   * (i.e. without calling toValue() on it).
   */
  @Override
  public EnvVar getRaw(Value key) {
    key = key.toKey();

    V<? extends Entry> entries = _lookupMap.getOrDefault(key, V.one(null));
    checkEntryInvariant(entries);
    checkInvariants();

    V<? extends Var> v=entries.flatMap(e-> (e==null) ? V.one(new Var(V.one(UnsetValue.UNSET))) : e.getEnvVar().getVar()) ;
    return new EnvVarImpl(v);
  }

  /**
   * conditional fold over all entries in this array
   *
   * feature expression of the op function already includes the current context
   * of the entry
   */
  public <T> V<? extends T> foldRight(V<? extends T> init, FeatureExpr ctx, Function4<FeatureExpr, Entry, T, V<? extends T>> op) {
    return VList.foldRight(new OptEntryIterator(_head), init, ctx, op);
  }

  public <T> V<? extends T> foldRightUntil(V<? extends T> init, FeatureExpr ctx, Function4<FeatureExpr, Entry, T, V<? extends T>> op, Predicate<T> stopCriteria) {
    return VList.foldRightUntil(new OptEntryIterator(_head), init, ctx, op, stopCriteria);
  }







  /**
   * Returns the corresponding key if this array contains the given value
   *
   * @param value to search for in the array
   * @return the key if it is found in the array, NULL otherwise
   */
  @Override@Deprecated
  public V<? extends Value> contains(Value value) {
    return this.contains(V.one(value));
  }

  public V<? extends Value> contains(V<? extends Value> value) {
    return this.<Value>foldRightUntil(V.one(NullValue.NULL), VHelper.noCtx(), (c, entry, result) ->
                    result == NullValue.NULL ? VHelper.<Value, Value, Value>mapAll(entry.getEnvVar().getValue(), value, (v1, v2) ->
                            v1.eq(v2) ? entry.getKey() : result
                    ) : V.one(result),
            result -> result != NullValue.NULL
    );
  }

  /**
   * Returns the corresponding key if this array contains the given value
   *
   * @param value to search for in the array
   * @return the key if it is found in the array, NULL otherwise
   */
  @Deprecated
  @Override
  public V<? extends Value> containsStrict(Value value) {
    return this.containsStrict(V.one(value));
  }

  public V<? extends Value> containsStrict(V<? extends Value> value) {
    return this.<Value>foldRightUntil(V.one(NullValue.NULL), VHelper.noCtx(), (c, entry, result) ->
                    result == NullValue.NULL ? VHelper.<Value, Value, Value>mapAll(entry.getEnvVar().getValue(), value, (v1, v2) ->
                            v1.eql(v2) ? entry.getKey() : result
                    ) : V.one(result),
            result -> result != NullValue.NULL
    );
  }

  /**
   * Returns the corresponding value if this array contains the given key
   *
   * @param key to search for in the array
   * @return the value if it is found in the array, NULL otherwise
   */
  @Override
  public V<? extends Value> containsKey(Value key) {
    @Nonnull V<? extends Entry> entry = getEntry(key);

    return entry.flatMap((a) -> a == null ? V.one(null) : a.getEnvVar().getValue());

  }

  /**
   * Gets a new value.
   */
  private V<? extends Entry> getEntry(Value key) {
    return _lookupMap.getOrDefault(key, V.one(null));
  }

  /**
   * Removes a value.
   *
   * returns removed values
   */
  @Override
  public V<? extends Value> remove(FeatureExpr ctx, @Nonnull Value key) {
    if (_isDirty)
      copyOnWrite();

    key = key.toKey();

    //lookup doesn't really help, lookup is updated afterward

    V<? extends Entry> remainingEntries = V.one(null);
    V<? extends Value> removedValues = V.one(UnsetValue.UNSET);

    Entry entry = _head;
    Entry prevEntry = null;

    for (; entry != null; entry = entry.getNext()) {
      Value entryKey = entry.getKey();

      //key matches and in relevant configuration space?
      if (key == entryKey || key.equals(entryKey)) {
        if (ctx.and(entry.getCondition()).isSatisfiable()) {

          Entry next, prev;
          //remove in all configurations?
          if (entry.getCondition().implies(ctx).isTautology()) {
            //remove entry
            removedValues = V.choice(entry.getCondition(), entry.getEnvVar().getValue(), removedValues);
            next = entry.getNext();
            prev = prevEntry;
            incSize(entry.getCondition(), -1);
          } else {
            //replace entry with one with restricted condition
            removedValues = V.choice(entry.getCondition().and(ctx), entry.getEnvVar().getValue(), removedValues);
            incSize(entry.getCondition().and(ctx), -1);
            Entry newEntry = new Entry(entry.getCondition().andNot(ctx), entry.getKey(), entry.getEnvVar());
            newEntry.setNext(entry.getNext());
            newEntry.setPrev(entry.getPrev());
            remainingEntries = V.choice(newEntry.getCondition(), V.one(newEntry), remainingEntries);
            next = newEntry;
            prev = newEntry;
          }

          //update linked list
          if (prevEntry != null)
            prevEntry.setNext(next);
          else
            _head = next;
          if (entry.getNext() != null)
            entry.getNext().setPrev(prev);
          else
            _tail = prev;


        } else
          remainingEntries = V.choice(entry.getCondition(), V.one(entry), remainingEntries);
      }

      prevEntry = entry;
    }
    checkEntryInvariant(remainingEntries);
    _lookupMap.put(key, remainingEntries);
    _nextAvailableIndex = null;
    reset(ctx);
    checkInvariants();

    return removedValues;
  }

  private void checkInvariants() {
    // check size
    V<? extends Integer> expectedSize = foldRight(V.one(0), VHelper.True(), (c, e, v) -> V.choice(e.getCondition(), v + 1, v));
    assert _size.equals(expectedSize) : "stored size " + _size + " different from actual size " + expectedSize;


    //check lookup table and backward references
    Entry entry = _head;
    Entry last = null;
    for (; entry != null; entry = entry.getNext()) {
      assert entry.getCondition().isSatisfiable() : "entry with unsatisfiable condition";

      V<? extends Entry> entries = _lookupMap.get(entry.getKey());
      assert entries != null : "inconsistent lookup table, entry missing";
      assert entries.getOne(entry.getCondition()) == entry : "inconsistent lookup table, entry not found in lookup table";

      assert entry.getPrev() == last : "double linked list broken";
      last = entry;
    }
    assert _tail == last : "_tail pointer incorrect";


  }

  @Override
  public ArrayValue append(Value key, EnvVar value) {
    throw new UnimplementedVException();
  }


  /**
   * Returns the array ref.
   */
  @Override
  public EnvVar getVar(Value index) {
    if (_isDirty)
      copyOnWrite();

    V<? extends Entry> entry = createEntry(VHelper.noCtx(), index);
    // quercus/0431

    return new EnvVarImpl(entry.flatMap((a) -> a.toVar())); // _value.toSimpleVar();
  }

  /**
   * Returns the array ref.
   */
  @Override
  public EnvVar getRef(Value index) {
    return getVar(index);
//    if (_isDirty)
//      copyOnWrite();
//
//    Entry entry = createEntry(VHelper.noCtx(),index);
//    // quercus/0431
//
//    return new EnvVarImpl(entry.toVar()); // _value.toSimpleVar();
  }

  /**
   * Creates the entry for a key.
   *
   * looks up existing entries if they exist or creates a new one at the end if necessary
   */
  private V<? extends Entry> createEntry(FeatureExpr ctx, Value _key) {
//    throw new UnimplementedVException();
    // XXX: "A key may be either an integer or a string. If a key is
    //       the standard representation of an integer, it will be
    //       interpreted as such (i.e. "8" will be interpreted as 8,
    //       while "08" will be interpreted as "08")."
    //
    //            http://us3.php.net/types.array

    final Value key = _key.toKey();

    @Nonnull V<? extends Entry> existingEntries = getEntry(key);// _lookupMap.getOrDefault(key, V.one(null));


    return existingEntries.vflatMap(ctx, (c,e)->{

      if (e==null)
        return V.choice(c,createNewEntry(c, key), e);
        else return V.one(e);
    });
  }

  private void incSize(FeatureExpr ctx, int increment) {
    _size = _size.<Integer>flatMap((Integer s)->V.choice(ctx, increment+s, s));
  }

  /**
   * creates an empty new entry under that condition
   *
   * assuming that an entry does not already exist with the same
   * key (not checked)
   */
  private Entry createNewEntry(FeatureExpr ctx, Value _key) {
//    throw new UnimplementedVException();

    final Value key = _key.toKey();

    incSize(ctx, 1);

    Entry newEntry = new Entry(ctx, key);
    if (_nextAvailableIndex != null)
      _nextAvailableIndex = _nextAvailableIndex.<Long>flatMap(k->V.choice(ctx, key.nextIndex(k), k));
    _lookupMap.put(ctx, key, newEntry);

    if (_head == null) {
      newEntry._prev = null;
      newEntry.setNext(null);

      _head = newEntry;
      _tail = newEntry;
    }
    else {
      newEntry._prev = _tail;
      newEntry.setNext(null);

      _tail.setNext(newEntry);
      _tail = newEntry;
    }
    FeatureExpr noCurrent = getCurrent().when(e -> e == null);
    if (noCurrent.and(ctx).isSatisfiable())
      setCurrent(noCurrent.and(ctx), V.one(newEntry));
    checkInvariants();

    return newEntry;
  }

//

//  private void addEntry(Entry entry) {
//    Value key = entry.getKey();
//
//    Entry []entries = _entries;
//
//    if (entries != null) {
//      int hash = key.hashCode() & _hashMask;
//
//      Entry head = entries[hash];
//
//      entry.setNextHash(head);
//
//      entries[hash] = entry;
//    }
//
//    if (_nextAvailableIndex >= 0)
//      _nextAvailableIndex = key.nextIndex(_nextAvailableIndex);
//  }

  /**
   * Updates _nextAvailableIndex on a remove of the highest value
   */
  private void updateNextAvailableIndex() {

    _nextAvailableIndex = V.one(Long.valueOf(0));

    for (Entry entry = _head; entry != null; entry = entry.getNext()) {
      final Entry e = entry;
      _nextAvailableIndex = _nextAvailableIndex.flatMap(v->
        V.choice(e.getCondition(), e.getKey().nextIndex(v), v));
    }
  }

  /**
   * Pops the top value.
   */
  @Override
  public V<? extends Value> pop(Env env, FeatureExpr ctx) {
    throw new UnimplementedVException();
//    if (_isDirty)
//      copyOnWrite();
//
//    if (_tail != null)
//      return remove(ctx, _tail.getKey());
//    else
//      return V.one(NullValue.NULL);
  }

  public final Entry getHead() {
    return _head;
  }

  protected final Entry getTail() {
    return _tail;
  }

  /**
   * Shuffles the array
   */
  public Value shuffle() {
    if (_isDirty)
      copyOnWrite();

    Entry[] values = new Entry[size()];

    int length = values.length;

    if (length == 0)
      return BooleanValue.TRUE;

    int i = 0;
    for (Entry ptr = _head; ptr != null; ptr = ptr.getNext())
      values[i++] = ptr;

    for (i = 0; i < length; i++) {
      int rand = RandomUtil.nextInt(length);

      Entry temp = values[rand];
      values[rand] = values[i];
      values[i] = temp;
    }

    _head = values[0];
    _head._prev = null;

    _tail = values[values.length - 1];
    _tail.setNext(null);

    for (i = 0; i < length; i++) {
      if (i > 0)
        values[i]._prev = values[i - 1];
      if (i < length - 1)
        values[i].setNext(values[i + 1]);
    }

    reset(VHelper.noCtx());

    return BooleanValue.TRUE;
  }

  /**
   * Returns the array keys.
   */
  @Override
  public Value getKeys() {
    if (_constSource != null)
      return _constSource.getKeys();
    else
      return super.getKeys();
  }

  /**
   * Returns the array keys.
   */
  @Override
  public Value getValues() {
    if (_constSource != null)
      return _constSource.getValues();
    else
      return super.getValues();
  }

  //
  // Java serialization code
  //

  private void writeObject(ObjectOutputStream out)
          throws IOException {
    out.writeInt(_size.getOne());

    for (VEntry entry : entrySet()) {
      out.writeObject(entry.getKey());
      out.writeObject(entry.getEnvVar());
    }
  }

  private void readObject(ObjectInputStream in)
          throws ClassNotFoundException, IOException {
    throw new UnimplementedVException();
//    int size = in.readInt();
//
//    int capacity = DEFAULT_SIZE;
//
//    while (capacity < 4 * size) {
//      capacity *= 2;
//    }
//
//    _entries = new Entry[capacity];
//    _hashMask = _entries.length - 1;
//
//    for (int i = 0; i < size; i++) {
//      put((Value) in.readObject(), (Value) in.readObject());
//    }
  }

  //
  // Java generator code
  //

  /**
   * Generates code to recreate the expression.
   *
   * @param out the writer to the Java source code.
   */
  public void generate(PrintWriter out)
          throws IOException {
    out.print("new ConstArrayValue(");

//    if (getSize() < ArrayValueComponent.MAX_SIZE) {
    out.print("new Value[] {");

    for (Entry entry = getHead(); entry != null; entry = entry.getNext()) {
      if (entry != getHead())
        out.print(", ");

      if (entry.getKey() != null)
        entry.getKey().generate(out);
      else
        out.print("null");
    }

    out.print("}, new Value[] {");

    for (Entry entry = getHead(); entry != null; entry = entry.getNext()) {
      if (entry != getHead())
        out.print(", ");

      entry.getEnvVar().getOne().generate(out);
    }

    out.print("}");
//    }
//    else {
//      ArrayValueComponent.generate(out, this);
//    }

    out.print(")");
  }
}
