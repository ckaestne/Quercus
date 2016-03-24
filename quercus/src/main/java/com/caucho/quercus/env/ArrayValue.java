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

import com.caucho.quercus.function.AbstractFunction;
import com.caucho.quercus.marshal.Marshal;
import com.caucho.quercus.marshal.MarshalFactory;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.*;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a PHP array value (one row!).
 */
abstract public class ArrayValue extends Value {
  private static final Logger log
    = Logger.getLogger(ArrayValue.class.getName());

  protected static final StringValue KEY = new ConstStringValue("key");
  protected static final StringValue VALUE = new ConstStringValue("value");

  public static final GetKey GET_KEY = new GetKey();
  public static final GetValue GET_VALUE = new GetValue();

  public static final StringValue ARRAY = new ConstStringValue("Array");

  private @Nonnull V<? extends Entry> _current = V.one(null);

  protected ArrayValue()
  {
  }

  /**
   * Returns the type.
   */
  @Override
  public String getType()
  {
    return "array";
  }

  /**
   * Returns the ValueType.
   */
  @Override
  public ValueType getValueType()
  {
    return ValueType.ARRAY;
  }

  //
  // marshal costs
  //

  /**
   * Cost to convert to a character
   */
  @Override
  public int toCharMarshalCost()
  {
    return Marshal.COST_INCOMPATIBLE;
  }

  /**
   * Cost to convert to a string
   */
  @Override
  public int toStringMarshalCost()
  {
    return Marshal.COST_INCOMPATIBLE;
  }

  /**
   * Cost to convert to a binary value
   */
  @Override
  public int toBinaryValueMarshalCost()
  {
    return Marshal.COST_INCOMPATIBLE;
  }

  /**
   * Cost to convert to a StringValue
   */
  @Override
  public int toStringValueMarshalCost()
  {
    return Marshal.COST_INCOMPATIBLE;
  }

  /**
   * Cost to convert to a UnicodeValue
   */
  @Override
  public int toUnicodeValueMarshalCost()
  {
    return Marshal.COST_INCOMPATIBLE;
  }

  /**
   * Converts to a boolean.
   */
  @Override
  public boolean toBoolean()
  {
    return getSize().getOne() != 0;
  }

  /**
   * Converts to a long.
   */
  @Override
  public long toLong()
  {
    if (getSize().getOne() > 0)
      return 1;
    else
      return 0;
  }

  /**
   * Converts to a double.
   */
  @Override
  public double toDouble()
  {
    return toLong();
  }

  /**
   * Converts to a string.
   */
  @Override
  public String toString()
  {
    return "Array";
  }

  /**
   * Converts to an object.
   */
  public Object toObject()
  {
    return null;
  }

  /**
   * Converts to an array if null.
   */
  @Override
  public Value toAutoArray()
  {
    return this;
  }

  /**
   * Converts to a java object.
   */
  @Override
  public Object toJavaObject()
  {
    return this;
  }

  protected V<? extends Entry> getCurrent()
  {
    return _current;
  }

  protected void setCurrent(FeatureExpr ctx, @Nonnull V<? extends Entry> entry)
  {
    _current = V.choice(ctx, entry, _current);
  }

  //
  // Conversions
  //

  /**
   * Converts to an object.
   */
  @Override
  public ArrayValue toArray()
  {
    return this;
  }

  /**
   * Converts to an array value
   */
  @Override
  public ArrayValue toArrayValue(Env env)
  {
    return this;
  }

  /**
   * Converts to an object.
   */
  @Override
  public Value toObject(Env env)
  {
    Value obj = env.createObject();


    Iterator<VEntry> iter = getIterator(env);
    while (iter.hasNext()) {
      VEntry entry = iter.next();
      Value key = entry.getKey();

      // php/03oe
      obj.putField(env, entry.getCondition(), key.toString(), entry.getEnvVar().getValue());
    }

    return obj;
  }

  /**
   * Converts to a java List object.
   */
  @Override
  public Collection toJavaCollection(Env env, Class type)
  {
    Collection coll = null;

    if (type.isAssignableFrom(HashSet.class)) {
      coll = new HashSet();
    }
    else if (type.isAssignableFrom(TreeSet.class)) {
      coll = new TreeSet();
    }
    else {
      try {
        coll = (Collection) type.newInstance();
      }
      catch (Throwable e) {
        log.log(Level.FINE, e.toString(), e);
        env.warning(L.l("Can't assign array to {0}", type.getName()));

        return null;
      }
    }

    for (Entry entry = getHead(); entry != null; entry = entry._next) {
      coll.add(entry.getEnvVar().getValue().getOne().toJavaObject());
    }

    return coll;
  }

  /**
   * Converts to a java List object.
   */
  @Override
  public List toJavaList(Env env, Class type)
  {
    List list = null;

    if (type.isAssignableFrom(ArrayList.class)) {
      list = new ArrayList();
    }
    else if (type.isAssignableFrom(LinkedList.class)) {
      list = new LinkedList();
    }
    else if (type.isAssignableFrom(Vector.class)) {
      list = new Vector();
    }
    else {
      try {
        list = (List) type.newInstance();
      }
      catch (Throwable e) {
        log.log(Level.FINE, e.toString(), e);
        env.warning(L.l("Can't assign array to {0}", type.getName()));

        return null;
      }
    }

    for (Entry entry = getHead(); entry != null; entry = entry._next) {
      list.add(entry.getEnvVar().getOne().toJavaObject());
    }

    return list;
  }

  /**
   * Converts to a java object.
   */
  @Override
  public Map toJavaMap(Env env, Class<?> type)
  {
    Map map = null;

    if (type.isAssignableFrom(TreeMap.class)) {
      map = new TreeMap();
    }
    else if (type.isAssignableFrom(LinkedHashMap.class)) {
      map = new LinkedHashMap();
    }
    else {
      try {
        map = (Map) type.newInstance();
      }
      catch (Throwable e) {
        log.log(Level.FINE, e.toString(), e);

        env.warning(L.l("Can't assign array to {0}",
                                    type.getName()));

        return null;
      }
    }

    for (Entry entry = getHead(); entry != null; entry = entry._next) {
      map.put(entry.getKey().toJavaObject(),
              entry.getEnvVar().getOne().toJavaObject());
    }

    return map;
  }

  @Override
  public boolean isCallable(Env env, boolean isCheckSyntaxOnly, Var nameRef)
  {
    //XXX: refactor to use toCallable()

    Value obj = get(LongValue.ZERO).getOne();
    Value nameV = get(LongValue.ONE).getOne();

    if (nameRef != null) {
      nameRef.set(NullValue.NULL);
    }

    if (! nameV.isString()) {
      return false;
    }
    else if (isCheckSyntaxOnly) {
      if (obj.isObject() || obj.isString()) {
        if (nameRef != null) {
          StringValue sb = env.createStringBuilder();

          if (obj.isObject()) {
            sb.append(obj.getClassName());
          }
          else {
            sb.append(VHelper.noCtx(), obj);
          }

          sb.append("::");
          sb.append(VHelper.noCtx(), nameV);

          nameRef.set(sb);
        }

        return true;
      }
      else {
        return false;
      }
    }

    AbstractFunction fun;

    if (obj.isObject()) {
      StringValue nameStr = nameV.toStringValue(env);

      int p = nameStr.indexOf("::");

      if (p > 0) {
        String name = nameStr.toString();

        String clsName = name.substring(0, p);
        name = name.substring(p + 2);

        QuercusClass cls = env.findClass(VHelper.noCtx(), clsName);

        if (cls == null) {
          return false;
        }
        else if (! obj.isA(env, cls)) {
          return false;
        }

        nameStr = env.createString(name);

        fun = cls.findFunction(nameStr);
      }
      else {
        fun = obj.findFunction(nameStr);
      }
    }
    else {
      String clsName = obj.toString();
      QuercusClass cls = env.findClass(VHelper.noCtx(), clsName);

      if (cls == null) {
        return false;
      }

      StringValue nameStr = nameV.toStringValue(env);
      fun = cls.findFunction(nameStr);
    }

    if (fun != null && fun.isPublic()) {
      if (nameRef != null) {
        StringValue sb = env.createStringBuilder();

        sb.append(fun.getDeclaringClass().getName());
        sb.append("::");
        sb.append(fun.getName());

        nameRef.set(sb);
      }

      return true;
    }
    else {
      return false;
    }
  }

  /**
   * Converts to a callable object.
   */
  @Override
  public Callable toCallable(Env env, FeatureExpr ctx, boolean isOptional)
  {
    Value obj = get(LongValue.ZERO).getOne(ctx);
    Value nameV = get(LongValue.ONE).getOne(ctx);

    if (! nameV.isString()) {
      env.warning(L.l("'{0}' ({1}) is an unknown callback name",
                      nameV, nameV.getClass().getSimpleName()));

      return super.toCallable(env, ctx, false);
    }

    String name = nameV.toString();

    if (obj.isObject()) {
      AbstractFunction fun;

      int p = name.indexOf("::");

      // php/09lf
      if (p > 0) {
        String clsName = name.substring(0, p);
        name = name.substring(p + 2);

        QuercusClass cls = env.findClass(ctx, clsName);

        if (cls == null) {
          env.warning(L.l("Callback: '{0}' is not a valid callback class for {1}",
                          clsName, name));

          return super.toCallable(env, ctx, false);
        }

        return new CallbackClassMethod(cls, env.createString(name), obj);
      }

      return new CallbackObjectMethod((ObjectValue) obj, env.createString(name));
    }
    else {
      QuercusClass cl = env.findClass(ctx, obj.toString());

      if (cl == null) {
        env.warning(L.l("Callback: '{0}' is not a valid callback string for {1}",
                        obj.toString(), obj));

        return super.toCallable(env, ctx, isOptional);
      }

      return new CallbackClassMethod(cl, env.createString(name), NullThisValue.NULL);
    }
  }

//  public final V<? extends Value> callCallback(Env env, FeatureExpr ctx, Callable callback, ValueOrVar key)
//  {
//    V<? extends Value> result;
//    Value value = getRaw(key.toValue()).getOne();
//
//    if (value.isVar()) {
//      value = new ArgRef(value._var());
//
//      result = call(env, ctx, value);
//    }
//    else {
//      Var aVar = value._value().toVar();
//
//      result = callback.call(env, ctx, aVar.getValue().getOne());
//
//      Value aNew = aVar.getValue().getOne();
//
//      if (aNew != value)
//        put(key.toValue(), EnvVar._gen(aNew));
//    }
//
//    return result;
//  }

//  public final V<? extends Value> callCallback(Env env, FeatureExpr ctx, Callable callback, Value key,
//                                  Value a2)
//  {
//    V<? extends Value> result;
//    Value value = getRaw(key);
//
//    if (value instanceof Var) {
//      value = new ArgRef((Var) value);
//
//      result = callback.call(env, ctx, value, a2);
//    }
//    else {
//      Value aVar = new VarImpl(value);
//
//      result = callback.call(env, ctx, aVar, a2);
//
//      Value aNew = aVar.toValue();
//
//      if (aNew != value)
//        put(key, aNew);
//    }
//
//    return result;
//  }
//
//  public final V<? extends Value> callCallback(Env env, FeatureExpr ctx, Callable callback, Value key,
//                                  Value a2, Value a3)
//  {
//    V<? extends Value> result;
//    Value value = getRaw(key);
//
//    if (value instanceof Var) {
//      value = new ArgRef((Var) value);
//
//      result = callback.call(env, ctx, value, a2, a3);
//    }
//    else {
//      Value aVar = new VarImpl(value);
//
//      result = callback.call(env,ctx,  aVar, a2, a3);
//
//      Value aNew = aVar.toValue();
//
//      if (aNew != value)
//        put(key, aNew);
//    }
//
//    return result;
//  }

  /**
   * Returns true for an array.
   */
  @Override
  public boolean isArray()
  {
    return true;
  }

  /**
   * Copy as a return value
   */
  @Override
  public Value copyReturn()
  {
    return copy(); // php/3a5e
  }

  /**
   * Copy for assignment.
   */
  @Override
  abstract public Value copy();

  @Override
  public V<? extends Value> toLocalRef()
  {
    return V.one(copy());
  }

  /**
   * Copy for serialization
   */
  @Override
  abstract public Value copy(Env env, IdentityHashMap<Value, EnvVar> map);

  /**
   * Returns the size.
   */
  @Override
  abstract public V<? extends Integer> getSize();

  /**
   * Returns the count().
   */
  @Override
  public V<? extends Integer> getCount(Env env)
  {
    return getSize();
  }

  /**
   * Returns the count().
   */
  @Override
  public V<? extends Integer> getCountRecursive(Env env)
  {
    V<? extends Integer> count = getCount(env);

    for (VEntry entry : entrySet()) {
      V<? extends Value> value = entry.getEnvVar().getValue();

      count = count.flatMap(c ->
              value.<Integer>flatMap(v -> {
                if (v.isArray())
                  return v.getCountRecursive(env).map(a -> a + c);
                else return V.one(c);
              }));
    }

    return count;
  }

  /**
   * Returns true if the value is empty
   */
  @Override
  public V<? extends Boolean> isEmpty()
  {
    return getSize().map(a -> a == 0);
  }

  @Override
  public V<? extends Boolean> isEmpty(Env env, Value key)
  {
    V<? extends Value> value = get(key).getValue();

    return value.flatMap(a -> a.isEmpty());
  }

  /**
   * Clears the array
   */
  abstract public void clear();

  @Override
  public int cmp(Value rValue)
  {
    return cmpImpl(rValue, 1);
  }

  private int cmpImpl(Value rValue, int resultIfKeyMissing)
  {
    // "if key from operand 1 is not found in operand 2 then
    // arrays are uncomparable, otherwise - compare value by value"

    // php/335h

    if (!rValue.isArray())
      return 1;

    int lSize = getSize().getOne();
    int rSize = rValue.toArray().getSize().getOne();

    if (lSize != rSize)
      return lSize < rSize ? -1 : 1;

    for (VEntry entry : entrySet()) {
      Value lElementValue = entry.getEnvVar().getOne();
      Value rElementValue = rValue.get(entry.getKey()).getOne();

      if (!rElementValue.isset())
        return resultIfKeyMissing;

      int cmp = lElementValue.cmp(rElementValue);

      if (cmp != 0)
        return cmp;
    }

    return 0;
  }

  /**
   * Returns true for less than
   */
  @Override
  public boolean lt(Value rValue)
  {
    // php/335h
    return cmpImpl(rValue, 1) < 0;
  }

  /**
   * Returns true for less than or equal to
   */
  @Override
  public boolean leq(Value rValue)
  {
    // php/335h
    return cmpImpl(rValue, 1) <= 0;
  }

  /**
   * Returns true for greater than
   */
  @Override
  public boolean gt(Value rValue)
  {
    // php/335h
    return cmpImpl(rValue, -1) > 0;
  }

  /**
   * Returns true for greater than or equal to
   */
  @Override
  public boolean geq(Value rValue)
  {
    // php/335h
    return cmpImpl(rValue, -1) >= 0;
  }

  /**
   * Adds a new value.
   */
  @Override
  public V<? extends ValueOrVar> put(FeatureExpr ctx, Value index, V<? extends ValueOrVar> value) {
    append(ctx, index, value);

    return value;
  }


  /**
   * Adds a new value.
   */
  public final void put(StringValue keyBinary,
                        StringValue keyUnicode,
                        Value value,
                        boolean isUnicode)
  {
    if (isUnicode)
      append(keyUnicode, value);
    else
      append(keyBinary, value);
  }

  /**
   * Add element to the end of an array
   */
  @Override
  abstract public V<? extends ValueOrVar> put(FeatureExpr ctx, V<? extends ValueOrVar> value);


  /**
   * Add to front.
   */
  abstract public ArrayValue unshift(Value value);

  /**
   * Splices.
   */
  abstract public ArrayValue splice(int begin, int end, ArrayValue replace);

  /**
   * Slices.
   */
  public ArrayValue slice(Env env, int start, int end, boolean isPreserveKeys)
  {
    throw new UnimplementedVException();
//    ArrayValueImpl array = new ArrayValueImpl();
//
//    Iterator<VEntry> iter = array.getIterator(env);
//
//    for (int i = 0; i < end && iter.hasNext(); i++) {
//      VEntry entry = iter.next();
//
//      if (start <= i) {
//        Value key = entry.getKey();
//
//        Value value = entry.getEnvVar().getOne();
//
//        if ((key.isString()) || isPreserveKeys)
//          array.put(key, value);
//        else
//          array.put(VHelper.noCtx(), value);
//      }
//    }
//
//    return array;
  }

  /**
   * Returns the value as an array.
   */
  @Override
  public V<? extends ValueOrVar> getArray(FeatureExpr ctx, Value index)
  {
    EnvVar value = get(index);

    V<? extends Var> array = value.getVar().smap(ctx, (c, a) -> {
      Var result = a.toAutoArray();
      if (result!=a)
        put(c, index, V.one(result));
      return result;
    });

    return value.getVar();
  }

  /**
   * Returns the value as an argument which may be a reference.
   */
//  @Override
//  abstract public Value getArg(Value index, boolean isTop);

  /**
   * Returns the field value, creating an object if it's unset.
   */
  @Override
  public V<? extends Value> getObject(Env env, FeatureExpr ctx, Value fieldName)
  {
    EnvVar value = get(fieldName);

    return value.getValue().map((Value v) -> {
      Value object = v.toAutoObject(env);
      if (v != object) {
        v = object;

        put(fieldName, v);
      }

      return v;
    });
  }

  /**
   * Sets the array ref.
   * @param ctx
   */
  @Override
  abstract public V<? extends Var> putVar(FeatureExpr ctx);

  /**
   * Creatse a tail index.
   * @param ctx
   */
  abstract public V<? extends Value> createTailKey(FeatureExpr ctx);

  /**
   * Returns a union of this array and the rValue as array.
   * If the rValue is not an array, the returned union contains the elements
   * of this array only.
   *
   * To append a value to this ArrayValue use the {@link Value#put(FeatureExpr, V)} method.
   */
  @Override
  public Value add(Value rValue)
  {
    rValue = rValue.toValue();

    if (! rValue.isArray())
      return copy();

    ArrayValue result = new ArrayValueImpl(this);

    for (VEntry entry : ((ArrayValue) rValue).entrySet()) {
      Value key = entry.getKey();

      if (result.get(key).getOne() == UnsetValue.UNSET) {
        // php/330c drupal disabled textarea
        result.put(key, entry.getEnvVar().copy());
      }
    }

    return result;
  }

  @Override
  public Iterator<VEntry> getBaseIterator(Env env)
  {
    return new EntryIterator(getHead());
  }

  public Iterator<VEntry> getIterator()
  {
    return new EntryIterator(getHead());
  }

  @Override
  public Iterator<Value> getKeyIterator(Env env)
  {
    return new KeyIterator(getHead());
  }

  @Override
  public Iterator<EnvVar> getValueIterator(Env env)
  {
    return new ValueIterator(getHead());
  }

  /**
   * Gets a new value.
   */
  @Override
  abstract public EnvVar get(Value key);

  /**
   * Returns the value in the array as-is.
   * (i.e. without calling toValue() on it).
   */
  public EnvVar getRaw(Value key)
  {
    return get(key);
  }

  /**
   * Returns true if the value is set.
   */
  @Override
  public boolean isset(Value key)
  {
    EnvVar value = get(key);

    // php/0d40
    return value != null && value.getValue().getOne().isset();
  }

  /**
   * Returns true if the key exists in the array.
   */
  @Override
  public boolean keyExists(Value key)
  {
    Value value = get(key).getValue().getOne();

    // php/173m
    return value != UnsetValue.UNSET;
  }

  /**
   * Removes a value.
   */
  @Override
  abstract public V<? extends Value> remove(FeatureExpr ctx, Value key);

  /**
   * Returns the array ref.
   */
//  @Override
//  abstract public Var getVar(Value index);

  /**
   * Returns an iterator of the entries.
   */
  public Set<Value> keySet()
  {
    return new KeySet();
  }

  /**
   * Returns a set of all the of the entries.
   */
  public Set<VEntry> entrySet()
  {
    return new EntrySet();
  }

  /**
   * Returns a collection of the values.
   */
  public Collection<EnvVar> values()
  {
    return new ValueCollection();
  }

  /**
   * Convenience for lib.
   */
  public void put(String key, String value)
  {
    // XXX: this needs an Env arg because of i18n
    // XXX: but some  modules have arrays that are static constants
    put(StringValue.create(key), StringValue.create(value));
  }

  /**
   * Convenience for lib.
   */
  public void put(Env env, String key, String value)
  {
    put(env.createString(key), env.createString(value));
  }

  /**
   * Convenience for lib.
   */
  public void put(String key, char value)
  {
    // XXX: this needs an Env arg because of i18n
    put(StringValue.create(key), StringValue.create(value));
  }

  /**
   * Convenience for lib.
   */
  public void put(String key, long value)
  {
    // XXX: this needs an Env arg because of i18n
    put(StringValue.create(key), LongValue.create(value));
  }

  /**
   * Convenience for lib.
   */
  public void put(Env env, String key, long value)
  {
    put(env.createString(key), LongValue.create(value));
  }

  /**
   * Convenience for lib.
   */
  public void put(String key, double value)
  {
    // XXX: this needs an Env arg because of i18n
    put(StringValue.create(key), new DoubleValue(value));
  }

  /**
   * Convenience for lib.
   */
  public void put(String key, boolean value)
  {
    // XXX: this needs an Env arg because of i18n
    put(StringValue.create(key),
        value ? BooleanValue.TRUE : BooleanValue.FALSE);
  }

  /**
   * Convenience for lib.
   */
  public void put(Env env, String key, boolean value)
  {
    put(env.createString(key),
        value ? BooleanValue.TRUE : BooleanValue.FALSE);
  }

  /**
   * Convenience for lib.
   */
  public void put(String value)
  {
    // XXX: this needs an Env arg because of i18n
    put(VHelper.noCtx(), V.one(StringValue.create(value)));
  }

  /**
   * Convenience for lib.
   */
  public void put(long value)
  {
    put(VHelper.noCtx(), V.one(LongValue.create(value)));
  }

  /**
   * Appends as an argument - only called from compiled code
   *
   * XXX: change name to appendArg
   */
  @Deprecated
  abstract public ArrayValue append(Value key, EnvVar value);

  @Deprecated//V transformation
  public ArrayValue append(Value value) {
    put(VHelper.noCtx(), V.one(value));

    return this;
  }

  /**
   * Appends as an argument - only called from compiled code
   *
   * XXX: change name to appendArg
   */
  public ArrayValue append(EnvVar value)
  {
    put(VHelper.noCtx(), value.getVar());

    return this;
  }

  /**
   * Puts all of the arg elements into this array.
   */
  public void putAll(ArrayValue array)
  {
    for (VEntry entry : array.entrySet())
      put(entry.getKey(), entry.getEnvVar());
  }

  /**
   * Convert to an array.
   */
  public static V<? extends ValueOrVar> toArray(Value value)
  {
    value = value.toValue();

    if (value instanceof ArrayValue)
      return V.one(value);
    else
      return new ArrayValueImpl().put(VHelper.noCtx(), V.one(value));
  }


  /**
   * Prints the value.
   * @param env
   * @param ctx
   */
  @Override
  public void print(Env env, FeatureExpr ctx)
  {
    env.print(ctx, "Array");
  }

  /**
   * Pops the top value.
   */
//  @Override
  abstract public V<? extends Value> pop(Env env, FeatureExpr ctx);

  /**
   * Shuffles the array
   */
  @Override
  abstract public Value shuffle();

  /**
   * Returns the head. (the first conditional element)
   */
  // XX: php/153v getHead needed by grep for getRawValue()
  abstract public Entry getHead();

  /**
   * Returns the head. (the first element in each condition)
   */
  public V<? extends Entry> getVHead() {
    return foldRightUntil(V.one(null), VHelper.True(),
            (c, entry, result) -> result != null ? V.one(result) : V.one(entry),
            r -> r != null);
  }

  protected abstract <T> V<? extends T> foldRightUntil(V<? extends T> init, FeatureExpr ctx, Function4<FeatureExpr, Entry, T, V<? extends T>> op, Predicate<T> stopCriteria);


  /**
   * Returns the tail.     (the last conditional element)
   */
  abstract protected Entry getTail();

  /**
   * Returns the tail.     (the last element in each condition)
   */
  public V<? extends Entry> getVTail() {
    return foldRightUntil(V.one(null), VHelper.True(),
            (c, entry, result) -> V.one(entry),
            r -> false);
  }

  /**
   * Returns the current value.
   */
  @Override
  public V<? extends Value> current() {
    return _current.flatMap(c ->
            c != null ? c.getEnvVar().getValue() : V.one(BooleanValue.FALSE));
  }

  /**
   * Returns the current key
   */
  @Override
  public V<? extends Value> key() {
    return _current.map(c ->
            c != null ? c.getKey() : NullValue.NULL);
  }

  /**
   * Returns true if there are more elements.
   */
  @Override
  public V<? extends Boolean> hasCurrent() {
    return _current.map((a) -> a != null);
  }

  /**
   * Returns the next value.
   * @param ctx
   */
  @Override
  public V<? extends Value> next(FeatureExpr ctx) {
    _current = moveCurrent(ctx, _current, false);

    return current();
  }

  private V<? extends Entry> moveCurrent(FeatureExpr ctx, @Nonnull V<? extends Entry> entry, boolean isPrev) {
    if (ctx.isContradiction()) return V.one(null);
    return entry.pflatMap(ctx,
            (c, e) -> {
              Entry next = isPrev ? e.getPrev() : e.getNext();
              return next == null ? V.one(c, null) : V.choice(next.getCondition(), V.one(next), moveCurrent(c.andNot(next.getCondition()), V.one(next), isPrev));
            },
            (c, a) -> V.one(c, a));
  }

  /**
   * Returns the previous value.
   * @param ctx
   */
  @Override
  public V<? extends Value> prev(FeatureExpr ctx) {
    _current = moveCurrent(ctx, _current, true);

    return current();
  }

  /**
   * The each iterator
   */
  public Value each()
  {
    if (_current == null)
      return BooleanValue.FALSE;

    ArrayValue result = new ArrayValueImpl();

    result.put(LongValue.ZERO, _current.getOne().getKey());
    result.put(KEY, _current.getOne().getKey());

    result.put(LongValue.ONE, _current.getOne().getEnvVar());
    result.put(VALUE, _current.getOne().getEnvVar());

    next(VHelper.noCtx());

    return result;
  }

  /**
   * Returns the first value.
   * @param ctx
   */
  @Override
  public V<? extends Value> reset(FeatureExpr ctx) {
    _current = V.choice(ctx, getVHead(), _current);

    return current();
  }

  /**
   * Returns the last value.
   * @param ctx
   */
  @Override
  public V<? extends Value> end(FeatureExpr ctx) {
    _current = V.choice(ctx, getVTail(), _current);

    return current();
  }

  /**
   * Returns the corresponding key if this array contains the given value
   *
   * @param value to search for in the array
   *
   * @return the key if it is found in the array, NULL otherwise
   */
  abstract public V<? extends Value> contains(Value value);

  /**
   * Returns the corresponding key if this array contains the given value
   *
   * @param value to search for in the array
   *
   * @return the key if it is found in the array, NULL otherwise
   */
  abstract public V<? extends Value> containsStrict(Value value);

  /**
   * Returns the corresponding value if this array contains the given key
   *
   * @param key to search for in the array
   *
   * @return the value if it is found in the array, NULL otherwise
   */
  @Override
  abstract public V<? extends Value> containsKey(Value key);

//  /**
//   * Returns an object array of this array.  This is a copy of this object's
//   * backing structure.  Null elements are not included.
//   *
//   * @return an object array of this array
//   */
//  public VEntry[] toEntryArray()
//  {
//    ArrayList<Map.Entry<Value, Value>> array
//      = new ArrayList<Map.Entry<Value, Value>>(getSize());
//
//    for (Entry entry = getHead(); entry != null; entry = entry._next)
//      array.add(entry);
//
//    VEntry[]result = new Entry[array.size()];
//
//    return array.toArray(result);
//  }

  /**
   * Sorts this array based using the passed Comparator
   *
   * @param comparator the comparator for sorting the array
   * @param resetKeys  true if the keys should not be preserved
   * @param strict  true if alphabetic keys should not be preserved
   */
  public void sort(Comparator<VEntry> comparator,
                   boolean resetKeys, boolean strict)
  {
    Entry []entries;

    entries = new Entry[getSize().getOne()];

    int i = 0;
    for (Entry entry = getHead(); entry != null; entry = entry._next) {
      entries[i++] = entry;
    }

    Arrays.sort(entries, comparator);

    clear();

    long base = 0;

    if (! resetKeys)
      strict = false;

    for (int j = 0; j < entries.length; j++) {
      Value key = entries[j].getKey();

      if (resetKeys && (! (key instanceof StringValue) || strict))
        put(LongValue.create(base++), entries[j].getEnvVar());
      else
        put(entries[j].getKey(), entries[j].getEnvVar());
    }
  }

  /**
   * Serializes the value.
   *
   * @param sb holds result of serialization
   * @param serializeMap holds reference indexes
   */
  @Override
  public void serialize(Env env, StringBuilder sb, SerializeMap serializeMap)
  {
    sb.append("a:");
    sb.append(getSize());
    sb.append(":{");

    serializeMap.incrementIndex();

    for (Entry entry = getHead(); entry != null; entry = entry._next) {
      entry.getKey().serialize(env, sb);
      entry.getRawValue().getOne().serialize(env, sb, serializeMap);
    }

    sb.append("}");
  }

  /**
   * Exports the value.
   */
  @Override
  protected void varExportImpl(StringValue sb, int level)
  {
    if (level != 0) {
      sb.append('\n');
    }

    for (int i = 0; i < level; i++) {
      sb.append("  ");
    }

    sb.append("array (");
    sb.append('\n');

    for (Entry entry = getHead(); entry != null; entry = entry._next) {
      Value key = entry.getKey();
      Value value = entry.getEnvVar().getOne();

      for (int i = 0; i < level + 1; i++) {
        sb.append("  ");
      }

      key.varExportImpl(sb, level + 1);
      sb.append(" => ");

      value.varExportImpl(sb, level + 1);
      sb.append(",\n");
    }

    for (int i = 0; i < level; i++) {
      sb.append("  ");
    }

    sb.append(")");
  }

  @Override
  public void jsonEncode(Env env, JsonEncodeContext context, StringValue sb)
  {
    long length = 0;

    Iterator<Value> keyIter = getKeyIterator(env);

    while (keyIter.hasNext()) {
      Value key = keyIter.next();

      if ((! key.isLongConvertible()) || key.toLong() != length) {
        jsonEncodeAssociative(env, context, sb);
        return;
      }

      length++;
    }

    sb.append('[');

    length = 0;
    for (EnvVar value : values()) {
      if (length > 0) {
        sb.append(',');
      }

      value.getOne().jsonEncode(env, context, sb);
      length++;
    }

    sb.append(']');
  }

  public void jsonEncodeAssociative(Env env,
                                    JsonEncodeContext context,
                                    StringValue sb)
  {
    sb.append('{');

    int length = 0;

    Iterator<VEntry> iter = getIterator(env);

    while (iter.hasNext()) {
      VEntry entry = iter.next();

      if (length > 0)
        sb.append(',');

      entry.getKey().toStringValue(env).jsonEncode(env, context, sb);
      sb.append(':');
      entry.getEnvVar().getValue().foreach((a) -> a.jsonEncode(env, context, sb));
      length++;
    }

    sb.append('}');
  }

  /**
   * Resets all numerical keys with the first index as base
   *
   * @param base  the initial index
   * @param strict  if true, string keys are also reset
   */
  public boolean keyReset(long base, boolean strict)
  {
    Entry []entries;

    entries = new Entry[getSize().getOne()];

    int i = 0;
    for (Entry entry = getHead(); entry != null; entry = entry._next) {
      entries[i++] = entry;
    }

    clear();

    for (int j = 0; j < entries.length; j++) {
      Value key = entries[j].getKey();

      if (! (key instanceof StringValue) || strict)
        put(LongValue.create(base++), entries[j].getEnvVar());
      else
        put(entries[j].getKey(), entries[j].getEnvVar());
    }

    return true;
  }

  /**
   * Test for equality
   *
   * @param rValue rhs ArrayValue to compare to
   *
   * @return true if this is equal to rValue, false otherwise
   */
  @Override
  public boolean eq(Value rValue)
  {
    if (rValue == this) {
      return true;
    }
    else if (rValue == null) {
      return false;
    }
    else if (rValue.isObject()) {
      // php/03q1
      return false;
    }
    else if (! rValue.isArray()) {
      return rValue.eq(this);
    } else if (getSize().getOne() != rValue.getSize().getOne()) {
      return false;
    }

    rValue = rValue.toValue();

    for (VEntry entry : entrySet()) {
      Value entryValue = entry.getEnvVar().getValue().getOne();

      Value entryKey = entry.getKey();

      Value rEntryValue = rValue.get(entryKey).getValue().getOne();

      if ((rEntryValue instanceof ArrayValue)
          && ! entryValue.eq(rEntryValue))
        return false;

      if (! entryValue.eq(rEntryValue))
        return false;
    }

    return true;
  }

  /**
   * Test for ===
   *
   * @param rValue rhs ArrayValue to compare to
   *
   * @return true if this is equal to rValue, false otherwise
   */
  @Override
  public boolean eql(Value rValue)
  {
    if (rValue == this) {
      return true;
    }
    else if (rValue == null) {
      return false;
    } else if (getSize().getOne() != rValue.getSize().getOne()) {
      return false;
    }

    rValue = rValue.toValue();

    if (rValue == this)
      return true;
    else if (! (rValue instanceof ArrayValue))
      return false;

    ArrayValue rArray = (ArrayValue) rValue;

    Iterator<VEntry> iterA = entrySet().iterator();
    Iterator<VEntry> iterB = rArray.entrySet().iterator();

    while (iterA.hasNext() && iterB.hasNext()) {
      VEntry entryA = iterA.next();
      VEntry entryB = iterB.next();

      if (! entryA.getKey().eql(entryB.getKey()))
        return false;

      if (!entryA.getEnvVar().getValue().getOne().eql(entryB.getEnvVar().getValue().getOne()))
        return false;
    }

    return !(iterA.hasNext() || iterB.hasNext());
  }

  /**
   * Converts to a key.
   */
  @Override
  public Value toKey()
  {
    return ARRAY;
  }

  @Override
  public void varDumpImpl(Env env, FeatureExpr ctx,
                          VWriteStream out,
                          int depth,
                          IdentityHashMap<Value, String> valueSet) {
    out.println(ctx, "array(" + getSize().getOne() + ") {");

    for (VEntry mapEntry : entrySet()) {
      varDumpEntry(env, ctx, out, depth + 1, valueSet, mapEntry);

      out.println(ctx);
    }

    printDepth(ctx, out, 2 * depth);

    out.print(ctx, "}");
  }

  protected void varDumpEntry(Env env, FeatureExpr ctx,
                              VWriteStream out,
                              int depth,
                              IdentityHashMap<Value, String> valueSet,
                              VEntry mapEntry) {
    Entry entry = (Entry) mapEntry;

    entry.varDumpImpl(env, ctx, out, depth, valueSet);
  }

  @Override
  protected void printRImpl(Env env,
                            FeatureExpr ctx, VWriteStream out,
                            int depth,
                            IdentityHashMap<Value, String> valueSet) {
    out.println(ctx, "Array");
    printDepth(ctx, out, 4 * depth);
    out.println(ctx, "(");

    for (VEntry mapEntry : entrySet()) {
      Entry entry = (Entry) mapEntry;

      entry.printRImpl(env, ctx, out, depth, valueSet);
    }

    printDepth(ctx, out, 4 * depth);
    out.println(ctx, ")");
  }

  protected void printREntry(Env env, FeatureExpr ctx,
                             VWriteStream out,
                             int depth,
                             IdentityHashMap<Value, String> valueSet,
                             VEntry mapEntry)
    throws IOException
  {
    Entry entry = (Entry) mapEntry;

    entry.printRImpl(env, ctx, out, depth, valueSet);
  }

  public static final class Entry
    implements /*VEntry,*/ Serializable, VEntry {
    private final Value _key;
    private final FeatureExpr _condition;

    private EnvVar _value;
    // Var _var;

    Entry _prev;
    private Entry _next;

//    private Entry _nextHash;

    public Entry(FeatureExpr condition, Value key)
    {
      _key = key;
      _value = EnvVar._gen(NullValue.NULL);
      _condition = condition;
    }

    public Entry(FeatureExpr condition, Value key, EnvVar value)
    {
      _key = key;
      _value = value;
      _condition = condition;
    }

    public Entry(Entry entry)
    {
      _key = entry._key;

      _value = entry._value.copy();
      _condition = entry._condition;
    }

    public final Entry getNext()
    {
      return _next;
    }

    public final void setNext(final Entry next)
    {
      _next = next;
    }

    public final Entry getPrev()
    {
      return _prev;
    }

    public final void setPrev(final Entry prev)
    {
      _prev = prev;
    }

//    public final Entry getNextHash()
//    {
//      return _nextHash;
//    }
//
//    public final void setNextHash(Entry next)
//    {
//      _nextHash = next;
//    }

    public EnvVar getRawValue()
    {
      // return _var != null ? _var : _value;
      return _value;
    }

    @Override
    public EnvVar getEnvVar()
    {
      // return _var != null ? _var.toValue() : _value;
      return _value;
    }

//    @Override
    @Override
    public EnvVar setEnvVar(EnvVar value) {
      EnvVar oldValue = _value;
      _value=value;
      return oldValue;
    }

    public V<? extends Value> getValue_()
    {
      // return _var != null ? _var.toValue() : _value;
      return _value.getValue();
    }

    @Override
    public Value getKey()
    {
      return _key;
    }

    @Override
    public FeatureExpr getCondition() {
      return _condition;
    }

    public V<? extends Value> toValue()
    {
      // The value may be a var
      // XXX: need test
      // return _var != null ? _var.toValue() : _value;

      return _value.getValue();
    }

    public V<? extends Var> toVar()
    {
      V<? extends Var> var = _value.getVar();

      return var;
    }

//    /**
//     * Argument used/declared as a ref.
//     */
//    public Var toRefVar()
//    {
//      // php/376a
//
//      Var var = _value.toVar();
//      _value = var;
//
//      return var;
//
//      /*
//      if (_var != null)
//        return _var;
//      else {
//        _var = new VarImpl(_value);
//
//        return _var;
//      }
//      */
//    }
//    /**
//     * Converts to an argument value.
//     */
//    public Value toArgValue()
//    {
//      // return _var != null ? _var.toValue() : _value;
//
//      return _value.toValue();
//    }
//
//    public Value setValue(Value value)
//    {
//      Value oldValue = _value;
//
//      _value = value;
//      // _var = null;
//
//      return oldValue;
//    }

    public V<? extends Value> set(FeatureExpr ctx, V<? extends ValueOrVar> value)
    {
      EnvVar oldValue = _value;

      // XXX: make OO
      /*
      if (value instanceof Var)
        _var = (Var) value;
      else if (_var != null)
        _var.set(value);
      else
        _value = value;
      */

      _value.setRef(ctx, value);
//      if (value.isVar())
//        _value.setVar(VHelper.noCtx(), value._var());
//      else {
//        _value = _value.set(value);
//      }

      return oldValue.getValue();
    }

    /**
     * Converts to a variable reference (for function  arguments)
     */
    public ArgRef toRef()
    {
      /*
      if (_var == null)
        _var = new VarImpl(_value);

        return new RefVar(_var);

      */

      Var var = _value.getVar().getOne();

      return new ArgRef(var);
    }

//    /**
//     * Converts to a variable reference (for function  arguments)
//     */
//    public Value toArgRef()
//    {
//      Var var = _value.toVar();
//
//      _value = var;
//
//      return new ArgRef(var);
//
//      /*
//      if (_var == null)
//        _var = new VarImpl(_value);
//
//      return new RefVar(_var);
//      */
//    }
//
//    public Value toArg()
//    {
//      Var var = _value.toVar();
//
//      _value = var;
//
//      // php/0d14
//      return var;
//      // return new RefVar(var);
//
//      /*
//      if (_var == null)
//        _var = new VarImpl(_value);
//
//      return _var;
//      */
//    }

    public void varDumpImpl(Env env, FeatureExpr ctx,
                            VWriteStream out,
                            int depth,
                            IdentityHashMap<Value, String> valueSet) {
      printDepth(ctx, out, 2 * depth);
      out.print(ctx, "[");

      if (_key instanceof StringValue)
        out.print(ctx, "\"" + _key + "\"");
      else
        out.print(ctx, _key);

      out.println(ctx, "]=>");

      printDepth(ctx, out, 2 * depth);

      getRawValue().getValue().foreach((a) -> {
        a.varDump(env, ctx, out, depth, valueSet);
      });
    }

    protected void printRImpl(Env env, FeatureExpr ctx,
                              VWriteStream out,
                              int depth,
                              IdentityHashMap<Value, String> valueSet) {
      printDepth(ctx, out, 4 * (depth + 1));
      out.print(ctx, "[");
      out.print(ctx, _key);
      out.print(ctx, "] => ");
      if (getRawValue() != null) {
        getRawValue().getValue().foreach((c, a) -> {
          a.printR(env, c, out, depth + 2, valueSet);
        });
      }

      out.println(ctx);
    }

    private void printDepth(FeatureExpr ctx, VWriteStream out, int depth) {
      for (int i = depth; i > 0; i--)
        out.print(ctx, ' ');
    }

    @Override
    public String toString()
    {
      return "VEntry[" + getKey() + "]";
    }
  }

  /**
   * Returns the field keys.
   */
  @Override
  public Value []getKeyArray(Env env)
  {
    int len = getSize().getOne();
    Value []keys = new Value[len];

    Iterator<Value> iter = getKeyIterator(env);

    for (int i = 0; i < len; i++) {
      keys[i] = iter.next();
    }

    return keys;
  }

  /**
   * Returns the field values.
   */
  @Override
  public Value []getValueArray(Env env)
  {
    int len = getSize().getOne();
    Value []values = new Value[len];

    Iterator<EnvVar> iter = getValueIterator(env);

    for (int i = 0; i < len; i++) {
      values[i] = iter.next().getOne();
    }

    return values;
  }

  /**
   * Takes the values of this array and puts them in a java array
   */
  public Value[] keysToArray()
  {
    Value[] values = new Value[getSize().getOne()];

    int i = 0;
    for (Entry ptr = getHead(); ptr != null; ptr = ptr.getNext()) {
      values[i++] = ptr.getKey();
    }

    return values;
  }

  /**
   * Takes the values of this array and puts them in a java array
   */
  public Value[] valuesToArray()
  {
    Value[] values = new Value[getSize().getOne()];

    int i = 0;
    for (Entry ptr = getHead(); ptr != null; ptr = ptr.getNext()) {
      values[i++] = ptr.getEnvVar().getValue().getOne();
    }

    return values;
  }

  /**
   * Returns the keys.
   */
  public Value getKeys()
  {
    return new ArrayValueImpl(keysToArray());
  }

  /**
   * Returns the keys.
   */
  public Value getValues()
  {
    return new ArrayValueImpl(valuesToArray());
  }

  /**
   * Takes the values of this array, unmarshals them to objects of type
   * <i>elementType</i>, and puts them in a java array.
   */
  @Override
  public Object valuesToArray(Env env, FeatureExpr ctx, Class elementType)
  {
    int size = getSize().getOne(ctx);

    Object array = Array.newInstance(elementType, size);

    MarshalFactory factory = env.getModuleContext().getMarshalFactory();
    Marshal elementMarshal = factory.create(elementType);

    int i = 0;

    for (Entry ptr = getHead(); ptr != null; ptr = ptr.getNext()) {
      Array.set(array, i++, elementMarshal.marshal(env,
              ctx, ptr.getEnvVar().getVar(),
                                                   elementType));
    }

    return array;
  }

  public class EntrySet extends AbstractSet<VEntry> {
    EntrySet()
    {
    }

    @Override
    public int size()
    {
      return ArrayValue.this.getSize().getOne();
    }

    @Override
    public Iterator<VEntry> iterator()
    {
      return new EntryIterator(getHead());
    }
  }

  public class KeySet extends AbstractSet<Value> {
    KeySet()
    {
    }

    @Override
    public int size()
    {
      return ArrayValue.this.getSize().getOne();
    }

    @Override
    public Iterator<Value> iterator()
    {
      return new KeyIterator(getHead());
    }
  }

  public class ValueCollection extends AbstractCollection<EnvVar> {
    ValueCollection()
    {
    }

    @Override
    public int size()
    {
      return ArrayValue.this.getSize().getOne();
    }

    @Override
    public Iterator<EnvVar> iterator()
    {
      return new ValueIterator(getHead());
    }
  }

  public static class EntryIterator
    implements Iterator<VEntry> {
    private Entry _current;

    EntryIterator(Entry head)
    {
      _current = head;
    }

    @Override
    public boolean hasNext()
    {
      return _current != null;
    }

    @Override
    public Entry next()
    {
      if (_current != null) {
        Entry next = _current;
        _current = _current._next;

        return next;
      }
      else
        return null;
    }

    @Override
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }

  public static class KeyIterator
    implements Iterator<Value> {
    private Entry _current;

    KeyIterator(Entry head)
    {
      _current = head;
    }

    @Override
    public boolean hasNext()
    {
      return _current != null;
    }

    @Override
    public Value next()
    {
      if (_current != null) {
        Value next = _current.getKey();
        _current = _current._next;

        return next;
      }
      else
        return null;
    }

    @Override
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }

  public static class ValueIterator
    implements Iterator<EnvVar> {
    private Entry _current;

    ValueIterator(Entry head)
    {
      _current = head;
    }

    @Override
    public boolean hasNext()
    {
      return _current != null;
    }

    @Override
    public EnvVar next()
    {
      if (_current != null) {
        EnvVar next = _current.getEnvVar();
        _current = _current._next;

        return next;
      }
      else
        return null;
    }

    @Override
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }

  public static class ValueComparator
    implements Comparator<VEntry>
  {
    public static final ValueComparator CMP = new ValueComparator();

    private ValueComparator()
    {
    }

    @Override
    public int compare(VEntry aEntry,
                       VEntry bEntry)
    {
      try {
        Value aValue = aEntry.getEnvVar().getValue().getOne();
        Value bValue = bEntry.getEnvVar().getValue().getOne();

        if (aValue.eq(bValue))
          return 0;
        else if (aValue.lt(bValue))
          return -1;
        else
          return 1;
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static class KeyComparator
    implements Comparator<VEntry>
  {
    public static final KeyComparator CMP = new KeyComparator();

    private KeyComparator()
    {
    }

    @Override
    public int compare(VEntry aEntry,
                       VEntry bEntry)
    {
      try {
        Value aKey = aEntry.getKey();
        Value bKey = bEntry.getKey();

        if (aKey.eq(bKey))
          return 0;
        else if (aKey.lt(bKey))
          return -1;
        else
          return 1;
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static abstract class AbstractGet {
    public abstract Value get(VEntry entry);
  }

  public static class GetKey extends AbstractGet
  {
    public static final GetKey GET = new GetKey();

    private GetKey()
    {
    }

    @Override
    public Value get(VEntry entry)
    {
      return entry.getKey();
    }
  }

  public static class GetValue extends AbstractGet {
    public static final GetValue GET = new GetValue();

    private GetValue()
    {
    }

    @Override
    public Value get(VEntry entry)
    {
      return entry.getEnvVar().getOne();
    }
  }


  protected static class OptEntryIterator
          implements Iterator<Opt<Entry>> {
    private Entry _current;

    OptEntryIterator(Entry head)
    {
      _current = head;
    }

    @Override
    public boolean hasNext()
    {
      return _current != null;
    }

    @Override
    public Opt<Entry> next()
    {
      if (_current != null) {
        Entry next = _current;
        _current = _current.getNext();

        return Opt.create(next.getCondition(),next);
      }
      else
        return null;
    }

    @Override
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
}


