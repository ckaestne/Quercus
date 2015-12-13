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

import java.io.Serializable;

/**
 * Represents a PHP variable value.
 */
@SuppressWarnings("serial")
public class Var 
  implements Serializable, ValueOrVar
{
  private V<? extends Value> _value;

  public Var()
  {
    _value = V.one(NullValue.NULL);
  }

  @Deprecated
  public static Var create(ValueOrVar value) {
    return create(V.one(value));
  }
  public static Var create(V<? extends ValueOrVar> value)
  {
    return new Var(value.flatMap(v->v._getValues()));
  }

  public Var(V<? extends Value> value)
  {
    _value = value;

//    checkVar(value);
  }

  /**
   * Sets the value.
   */

  @Deprecated//("workaround for V migration, avoid")
  public Var set_(FeatureExpr ctx, V<? extends ValueOrVar> value) {
      return set(ctx, value.flatMap((v)-> v.isVar() ? v._var().getValue() : V.one(v._value())));
  }

  @Deprecated//("workaround for V migration, avoid")
  public Var set(Value value) { return set_(value);}
  @Deprecated//("workaround for V migration, avoid")
  public Var set_(Value value) {
    return set(VHelper.noCtx(), V.one(value));
  }

  public Var set(FeatureExpr ctx, V<? extends Value> value)
  {
    this._value = V.choice(ctx, value, _value);

    return this;
  }

//  private void checkVar(Value value)
//  {
//    //assert(! (value instanceof Var));
//  }

  public boolean isVar()
  {
    return true;
  }

  @Override
  public Value _value() {
    throw new UnsupportedOperationException("called _value on a Var");
  }

  @Override
  public Var _var() {
    return this;
  }

  public V<? extends Value> getValue() {
    return _value;
  }

//  /**
//   * Sets the value, possibly replacing if a var and returning the resulting var
//   *
//   * $a =& (...).
//   */
//  public Var setRef(Value value)
//  {
//    // php/078d-f
//
////    if (value.isVar())
////      return (Var) value;
////    else {
////      // XXX:
////
////      _value = value;
////
////      checkVar(value);
////
////      return this;
////    }
//    return null;
//  }

//  /**
//   * Sets the value.
//   */
//  protected Var setRaw(Value value)
//  {
//    // quercus/0431
//    _value = value;
//
//    checkVar(value);
//
//    return this;
//  }

//  /**
//   * Returns the type.
//   */
//
//  public String getType()
//  {
//    return _value.getType();
//  }
//
//  /**
//   * Returns the SPL object hash.
//   */
//
//  public StringValue getObjectHash(Env env)
//  {
//    return _value.getObjectHash(env);
//  }
//
//  /**
//   * Returns the type of the resource.
//   */
//
//  public String getResourceType()
//  {
//    return _value.getResourceType();
//  }
//
//  /**
//   * Returns the ValueType.
//   */
//
//  public ValueType getValueType()
//  {
//    return _value.getValueType();
//  }
//
//  /**
//   * Returns the class name.
//   */
//
//  public String getClassName()
//  {
//    return _value.getClassName();
//  }
//
//
//  public QuercusClass getQuercusClass()
//  {
//    return _value.getQuercusClass();
//  }
//
//
//  public QuercusClass findQuercusClass(Env env)
//  {
//    return _value.findQuercusClass(env);
//  }
//
//  /**
//   * Returns true for an object.
//   */
//
//  public boolean isObject()
//  {
//    return _value.isObject();
//  }
//
//  /*
//   * Returns true for a resource.
//   */
//
//  public boolean isResource()
//  {
//    return _value.isResource();
//  }
//
//  /**
//   * Returns true for an implementation of a class
//   */
//
//  public boolean isA(Env env, String name)
//  {
//    return _value.isA(env, name);
//  }
//
//  /**
//   * True for a long
//   */
//
//  public boolean isLongConvertible()
//  {
//    return _value.isLongConvertible();
//  }
//
//  /**
//   * True to a double.
//   */
//
//  public boolean isDoubleConvertible()
//  {
//    return _value.isDoubleConvertible();
//  }
//
//  /**
//   * True for a number
//   */
//
//  public boolean isNumberConvertible()
//  {
//    return _value.isNumberConvertible();
//  }
//
//  /**
//   * Returns true for a long-value.
//   */
//
//  public boolean isLong()
//  {
//    return _value.isLong();
//  }
//
//  /**
//   * Returns true for a long-value.
//   */
//
//  public boolean isDouble()
//  {
//    return _value.isDouble();
//  }
//
//  /**
//   * Returns true for is_numeric
//   */
//
//  public boolean isNumeric()
//  {
//    return _value.isNumeric();
//  }
//
//  /**
//   * Returns true for a scalar
//   */
//  /*
//  public boolean isScalar()
//  {
//    return _value.isScalar();
//  }
//  */
//
//  /**
//   * Returns true for a StringValue.
//   */
//
//  public boolean isString()
//  {
//    return _value.isString();
//  }
//
//  /**
//   * Returns true for a BinaryValue.
//   */
//
//  public boolean isBinary()
//  {
//    return _value.isBinary();
//  }
//
//  /**
//   * Returns true for a UnicodeValue.
//   */
//
//  public boolean isUnicode()
//  {
//    return _value.isUnicode();
//  }
//
//  /**
//   * Returns true for a BooleanValue
//   */
//
//  public boolean isBoolean()
//  {
//    return _value.isBoolean();
//  }
//
//  /**
//   * Returns true for a DefaultValue
//   */
//
//  public boolean isDefault()
//  {
//    return _value.isDefault();
//  }
//
//  /**
//   * Returns true if the value is set
//   */
//
//  public boolean isset()
//  {
//    return _value.isset();
//  }
//
//  /**
//   * Returns true if the value is empty
//   */
//
//  public boolean isEmpty()
//  {
//    return _value.isEmpty();
//  }
//
//  /**
//   * Returns true if the value is empty
//   */
//
//  public boolean isEmpty(Env env, Value index)
//  {
//    return _value.isEmpty(env, index);
//  }
//
//  /**
//   * True if the object is null
//   */
//
//  public boolean isNull()
//  {
//    return _value.isNull();
//  }
//
//  //
//  // Conversions
//  //
//
//
//  public String toString()
//  {
//    return _value.toString();
//  }
//
//  /**
//   * Converts to a boolean.
//   */
//
//  public boolean toBoolean()
//  {
//    return _value.toBoolean();
//  }
//
//  /**
//   * Converts to a long.
//   */
//
//  public long toLong()
//  {
//    return _value.toLong();
//  }
//
//  /**
//   * Converts to a double.
//   */
//
//  public double toDouble()
//  {
//    return _value.toDouble();
//  }
//
//  /**
//   * Converts to a long.
//   */
//
//  public LongValue toLongValue()
//  {
//    return _value.toLongValue();
//  }
//
//  /**
//   * Converts to a double.
//   */
//
//  public DoubleValue toDoubleValue()
//  {
//    return _value.toDoubleValue();
//  }
//
//  /**
//   * Converts to a string.
//   * @param env
//   */
//
//  public StringValue toString(Env env)
//  {
//    return _value.toString(env);
//  }
//
//  /**
//   * Converts to a java boolean object.
//   */
//
//  public Boolean toJavaBoolean()
//  {
//    return _value.toJavaBoolean();
//  }
//
//  /**
//   * Converts to a java byte object.
//   */
//
//  public Byte toJavaByte()
//  {
//    return _value.toJavaByte();
//  }
//
//  /**
//   * Converts to a java short object.
//   */
//
//  public Short toJavaShort()
//  {
//    return _value.toJavaShort();
//  }
//
//  /**
//   * Converts to a java Integer object.
//   */
//
//  public Integer toJavaInteger()
//  {
//    return _value.toJavaInteger();
//  }
//
//  /**
//   * Converts to a java Long object.
//   */
//
//  public Long toJavaLong()
//  {
//    return _value.toJavaLong();
//  }
//
//  /**
//   * Converts to a java Float object.
//   */
//
//  public Float toJavaFloat()
//  {
//    return _value.toJavaFloat();
//  }
//
//  /**
//   * Converts to a java Double object.
//   */
//  public Double toJavaDouble()
//  {
//    return _value.toJavaDouble();
//  }
//
//  /**
//   * Converts to a java Character object.
//   */
//  public Character toJavaCharacter()
//  {
//    return _value.toJavaCharacter();
//  }
//
//  /**
//   * Converts to a java String object.
//   */
//
//  public String toJavaString()
//  {
//    if (_value.isObject())
//      return toString(Env.getInstance()).toString();
//    else
//      return toString();
//  }
//
//  /**
//   * Converts to an object.
//   */
//
//  public Object toJavaObject()
//  {
//    return _value.toJavaObject();
//  }
//
//  /**
//   * Converts to an object.
//   */
//
//  public Object toJavaObject(Env env, Class type)
//  {
//    return _value.toJavaObject(env, type);
//  }
//
//  /**
//   * Converts to an object.
//   */
//
//  public Object toJavaObjectNotNull(Env env, Class type)
//  {
//    return _value.toJavaObjectNotNull(env, type);
//  }
//
//  /**
//   * Converts to a java Collection object.
//   */
//
//  public Collection toJavaCollection(Env env, Class type)
//  {
//    return _value.toJavaCollection(env, type);
//  }
//
//  /**
//   * Converts to a java List object.
//   */
//
//  public List toJavaList(Env env, Class type)
//  {
//    return _value.toJavaList(env, type);
//  }
//
//  /**
//   * Converts to a java map.
//   */
//
//  public Map toJavaMap(Env env, Class type)
//  {
//    return _value.toJavaMap(env, type);
//  }
//
//  /**
//   * Converts to a Java Calendar.
//   */
//
//  public Calendar toJavaCalendar()
//  {
//    return _value.toJavaCalendar();
//  }
//
//  /**
//   * Converts to a Java Date.
//   */
//
//  public Date toJavaDate()
//  {
//    return _value.toJavaDate();
//  }
//
//  /**
//   * Converts to a Java URL.
//   */
//
//  public URL toJavaURL(Env env)
//  {
//    return _value.toJavaURL(env);
//  }
//
//  /**
//   * Converts to a Java Enum.
//   */
//
//  public Enum toJavaEnum(Env env, Class cls)
//  {
//    return _value.toJavaEnum(env, cls);
//  }
//
//  /**
//   * Converts to a Java BigDecimal.
//   */
//
//  public BigDecimal toBigDecimal()
//  {
//    return _value.toBigDecimal();
//  }
//
//  /**
//   * Converts to a Java BigInteger.
//   */
//
//  public BigInteger toBigInteger()
//  {
//    return _value.toBigInteger();
//  }
//
//  /**
//   * Converts to an array
//   */
//
//  public ArrayValue toArray()
//  {
//    return _value.toArray();
//  }
//
//  /**
//   * Converts to an array
//   */
//
//  public ArrayValue toArrayValue(Env env)
//  {
//    return _value.toArrayValue(env);
//  }
//
  /**
   * Converts to an array
   */

  public Var toAutoArray()
  {
    _value = _value.map((a)->a.toAutoArray());

    // php/03mg

    return this;
  }
//
//  /**
//   * Converts to an object.
//   */
//
//  public Value toObject(Env env)
//  {
//    return _value.toObject(env);
//  }
//
//  //
//  // marshal costs
//  //
//
//  /**
//   * Cost to convert to a boolean
//   */
//
//  public int toBooleanMarshalCost()
//  {
//    return _value.toBooleanMarshalCost();
//  }
//
//  /**
//   * Cost to convert to a byte
//   */
//
//  public int toByteMarshalCost()
//  {
//    return _value.toByteMarshalCost();
//  }
//
//  /**
//   * Cost to convert to a short
//   */
//
//  public int toShortMarshalCost()
//  {
//    return _value.toShortMarshalCost();
//  }
//
//  /**
//   * Cost to convert to an integer
//   */
//
//  public int toIntegerMarshalCost()
//  {
//    return _value.toIntegerMarshalCost();
//  }
//
//  /**
//   * Cost to convert to a long
//   */
//
//  public int toLongMarshalCost()
//  {
//    return _value.toLongMarshalCost();
//  }
//
//  /**
//   * Cost to convert to a double
//   */
//
//  public int toDoubleMarshalCost()
//  {
//    return _value.toDoubleMarshalCost();
//  }
//
//  /**
//   * Cost to convert to a float
//   */
//
//  public int toFloatMarshalCost()
//  {
//    return _value.toFloatMarshalCost();
//  }
//
//  /**
//   * Cost to convert to a character
//   */
//
//  public int toCharMarshalCost()
//  {
//    return _value.toCharMarshalCost();
//  }
//
//  /**
//   * Cost to convert to a string
//   */
//
//  public int toStringMarshalCost()
//  {
//    return _value.toStringMarshalCost();
//  }
//
//  /**
//   * Cost to convert to a byte[]
//   */
//
//  public int toByteArrayMarshalCost()
//  {
//    return _value.toByteArrayMarshalCost();
//  }
//
//  /**
//   * Cost to convert to a char[]
//   */
//
//  public int toCharArrayMarshalCost()
//  {
//    return _value.toCharArrayMarshalCost();
//  }
//
//  /**
//   * Cost to convert to a Java object
//   */
//
//  public int toJavaObjectMarshalCost()
//  {
//    return _value.toJavaObjectMarshalCost();
//  }
//
//  /**
//   * Cost to convert to a binary value
//   */
//
//  public int toBinaryValueMarshalCost()
//  {
//    return _value.toBinaryValueMarshalCost();
//  }
//
//  /**
//   * Cost to convert to a StringValue
//   */
//
//  public int toStringValueMarshalCost()
//  {
//    return _value.toStringValueMarshalCost();
//  }
//
//  /**
//   * Cost to convert to a UnicdeValue
//   */
//
//  public int toUnicodeValueMarshalCost()
//  {
//    return _value.toUnicodeValueMarshalCost();
//  }
//
//  /**
//   * Append to a unicode builder.
//   */
//
//  public StringValue appendTo(UnicodeBuilderValue sb)
//  {
//    return _value.appendTo(sb);
//  }
//
//  /**
//   * Append to a binary builder.
//   */
//
//  public StringValue appendTo(BinaryBuilderValue sb)
//  {
//    return _value.appendTo(sb);
//  }
//
//  /**
//   * Append to a string builder.
//   */
//
//  public StringValue appendTo(StringBuilderValue sb)
//  {
//    return _value.appendTo(sb);
//  }
//
//  /**
//   * Append to a string builder.
//   */
//
//  public StringValue appendTo(LargeStringBuilderValue sb)
//  {
//    return _value.appendTo(sb);
//  }
//
//  /**
//   * Returns to the value value.
//   */
//  public final Value getRawValue()
//  {
//    return _value;
//  }
//
//  /**
//   * Converts to a raw value.
//   */
//
//
  public final Value toValue()
  {
    return _value.getOne();
  }

  @Deprecated // call only when necessary to make refactoring work; likely broke things
  public Value makeValue() {
    return toValue();
  }
//
//  /**
//   * Converts to a function argument value that is never assigned or modified.
//   */
//
//  public Value toLocalValueReadOnly()
//  {
//    return _value;
//  }
//
//  /**
//   * Converts to a raw value.
//   */
//
//  public Value toLocalValue()
//  {
//    return _value.copy();
//  }
//
//  /**
//   * Convert to a function argument value, e.g. for
//   *
//   * function foo($a)
//   *
//   * where $a may be assigned.
//   */
//
//  public Value toLocalRef()
//  {
//    return _value;
//  }
//
//  /**
//   * Converts to a function argument ref value, i.e. an argument
//   * declared as a reference, but not assigned
//   */
//
//  public Value toRefValue()
//  {
//    // php/344r
//    return _value.toRefValue();
//  }
//
//  /**
//   * Converts to a variable
//   */
//
//  public Var toVar()
//  {
//    // php/3d04
//    // return new Var(_value.toArgValue());
//    return this;
//  }
//
//  /**
//   * Converts to a local argument variable
//   */
//
//  public Var toLocalVar()
//  {
//    return new Var(_value.toLocalValue());
//  }
//
//  /**
//   * Converts to a reference variable
//   */
//
//  public Var toLocalVarDeclAsRef()
//  {
//    return this;
//  }
//
//  /**
//   * Converts to a reference variable
//   */
//
//  public Value toArgRef()
//  {
//    return new ArgRef(this);
//  }
//
//  /**
//   * Converts to a key.
//   */
//
//  public Value toKey()
//  {
//    return _value.toKey();
//  }
//
//
//  public StringValue toStringValue()
//  {
//    return _value.toStringValue();
//  }
//
//
//  public StringValue toStringValue(Env env)
//  {
//    return _value.toStringValue(env);
//  }
//
//
//  public StringValue toBinaryValue(Env env)
//  {
//    return _value.toBinaryValue(env);
//  }
//
//
//  public StringValue toUnicode(Env env)
//  {
//    return _value.toUnicode(env);
//  }
//
//
//  public StringValue toUnicodeValue(Env env)
//  {
//    return _value.toUnicodeValue(env);
//  }
//
//
//  public StringValue toStringBuilder()
//  {
//    return _value.toStringBuilder();
//  }
//
//
//  public StringValue toStringBuilder(Env env)
//  {
//    return _value.toStringBuilder(env);
//  }
//
//  /**
//   * Converts to a string builder
//   */
//
//  public StringValue toStringBuilder(Env env, Value value)
//  {
//    return _value.toStringBuilder(env, value);
//  }
//
//  /**
//   * Converts to a string builder
//   */
//  public StringValue toStringBuilder(Env env, StringValue value)
//  {
//    return _value.toStringBuilder(env, value);
//  }
//
//
//  public java.io.InputStream toInputStream()
//  {
//    return _value.toInputStream();
//  }
//
//
//  public Callable toCallable(Env env, boolean isOptional)
//  {
//    return _value.toCallable(env, isOptional);
//  }
//
//  //
//  // Operations
//  //
//
//  /**
//   * Copy the value.
//   */
//
//  public Value copy()
//  {
//    // php/041d
//    return _value.copy();
//  }
//
//  /**
//   * Copy for serialization
//   */
//  public Value copyTree(Env env, CopyRoot root)
//  {
//    return _value.copyTree(env, root);
//  }
//
//  /**
//   * Clone for the clone keyword
//   */
//
//  public Value clone(Env env)
//  {
//    return _value.clone(env);
//  }
//
//  /**
//   * Copy the value as an array item.
//   */
//
////  public Value copyArrayItem()
////  {
////    // php/041d, php/041k, php/041l
////    return this;
////  }
//
//  /**
//   * Copy the value as a return value.
//   */
//
//  public Value copyReturn()
//  {
//    return _value.copy();
//  }
//
//  /**
//   * Converts to a variable reference (for function  arguments)
//   */
//
////  public Value toRef()
////  {
////    // return new ArgRef(this);
////    return this;
////  }
//
//  /**
//   * Returns true for an array.
//   */
//
//  public boolean isArray()
//  {
//    return _value.isArray();
//  }
//
//  /**
//   * Negates the value.
//   */
//
//  public Value neg()
//  {
//    return _value.neg();
//  }
//
//  /**
//   * Adds to the following value.
//   */
//
//  public Value add(Value rValue)
//  {
//    return _value.add(rValue);
//  }
//
//  /**
//   * Adds to the following value.
//   */
//
//  public Value add(long rValue)
//  {
//    return _value.add(rValue);
//  }
//
//  /**
//   * Pre-increment the following value.
//   */
//
  public V<? extends Value> preincr(FeatureExpr ctx, int incr)
  {
    _value = _value.vflatMap(ctx, (c,x)->V.choice(c,x.increment(incr),x));

//    checkVar(_value);

    return _value;
  }
//
  /**
   * Post-increment the following value.
   */

  public V<? extends Value> postincr(FeatureExpr ctx, int incr)
  {
    V<? extends Value> value = _value;

    _value = value.vflatMap(ctx, (c,x)->V.choice(c,x.increment(incr),x));

//    checkVar(_value);

    return value;
  }
//
//  /**
//   * Pre-increment the following value.
//   */
//
//  public Value addOne()
//  {
//    return _value.addOne();
//  }
//
//  /**
//   * Pre-increment the following value.
//   */
//
//  public Value subOne()
//  {
//    return _value.subOne();
//  }
//
//  /**
//   * Pre-increment the following value.
//   */
//
//  public Value preincr()
//  {
//    _value = _value.preincr();
//
//    checkVar(_value);
//
//    return _value;
//  }
//
//  /**
//   * Pre-increment the following value.
//   */
//
//  public Value predecr()
//  {
//    _value = _value.predecr();
//
//    checkVar(_value);
//
//    return _value;
//  }
//
//  /**
//   * Post-increment the following value.
//   */
//
//  public Value postincr()
//  {
//    Value value = _value;
//
//    _value = value.postincr();
//
//    checkVar(_value);
//
//    return value;
//  }
//
//  /**
//   * Post-increment the following value.
//   */
//
//  public Value postdecr()
//  {
//    Value value = _value;
//
//    _value = value.postdecr();
//
//    checkVar(_value);
//
//    return value;
//  }
//
//  /**
//   * Increment the following value.
//   */
//
//  public Value increment(int incr)
//  {
//    return _value.increment(incr);
//  }
//
//  /**
//   * Subtracts to the following value.
//   */
//
//  public Value sub(Value rValue)
//  {
//    return _value.sub(rValue);
//  }
//
//  /**
//   * Subtracts to the following value.
//   */
//
//  public Value sub(long rValue)
//  {
//    return _value.sub(rValue);
//  }
//
//  /**
//   * Multiplies to the following value.
//   */
//
//  public Value mul(Value rValue)
//  {
//    return _value.mul(rValue);
//  }
//
//  /**
//   * Multiplies to the following value.
//   */
//
//  public Value mul(long lValue)
//  {
//    return _value.mul(lValue);
//  }
//
//  /**
//   * Divides the following value.
//   */
//
//  public Value div(Value rValue)
//  {
//    return _value.div(rValue);
//  }
//
//  /**
//   * Shifts left by the value.
//   */
//
//  public Value lshift(Value rValue)
//  {
//    return _value.lshift(rValue);
//  }
//
//  /**
//   * Shifts right by the value.
//   */
//
//  public Value rshift(Value rValue)
//  {
//    return _value.rshift(rValue);
//  }
//
//  /**
//   * Binary And.
//   */
//  public Value bitAnd(Value rValue)
//  {
//    return _value.bitAnd(rValue);
//  }
//
//  /**
//   * Binary or.
//   */
//  public Value bitOr(Value rValue)
//  {
//    return _value.bitOr(rValue);
//  }
//
//  /**
//   * Binary xor.
//   */
//
//  public Value bitXor(Value rValue)
//  {
//    return _value.bitXor(rValue);
//  }
//
//  /**
//   * Absolute value.
//   */
//  public Value abs()
//  {
//    return _value.abs();
//  }
//
//  /**
//   * Returns true for equality
//   */
//
//  public boolean eq(Value rValue)
//  {
//    return _value.eq(rValue);
//  }
//
//  /**
//   * Returns true for equality
//   */
//
//  public boolean eql(Value rValue)
//  {
//    return _value.eql(rValue);
//  }
//
//  /**
//   * Compares the two values
//   */
//
//  public int cmp(Value rValue)
//  {
//    return _value.cmp(rValue);
//  }
//
//  /**
//   * Returns true for less than
//   */
//
//  public boolean lt(Value rValue)
//  {
//    // php/335h
//    return _value.lt(rValue);
//  }
//
//  /**
//   * Returns true for less than or equal to
//   */
//
//  public boolean leq(Value rValue)
//  {
//    // php/335h
//    return _value.leq(rValue);
//  }
//
//  /**
//   * Returns true for greater than
//   */
//
//  public boolean gt(Value rValue)
//  {
//    // php/335h
//    return _value.gt(rValue);
//  }
//
//  /**
//   * Returns true for greater than or equal to
//   */
//
//  public boolean geq(Value rValue)
//  {
//    // php/335h
//    return _value.geq(rValue);
//  }
//
//  /**
//   * Returns the length as a string.
//   */
//
//  public int length()
//  {
//    return _value.length();
//  }
//
//  /**
//   * Returns the array/object size
//   */
//
//  public int getSize()
//  {
//    return _value.getSize();
//  }
//
//  /**
//   * Returns the count, as returned by the global php count() function
//   */
//  public int getCount(Env env)
//  {
//    return _value.getCount(env);
//  }
//
//  /**
//   * Returns the count, as returned by the global php count() function
//   */
//  public int getCountRecursive(Env env)
//  {
//    return _value.getCountRecursive(env);
//  }
//
//
//  public Iterator<Map.Entry<Value, Value>> getIterator(Env env)
//  {
//    return _value.getIterator(env);
//  }
//
//
//  public Iterator<Value> getKeyIterator(Env env)
//  {
//    return _value.getKeyIterator(env);
//  }
//
//
//  public Iterator<Value> getValueIterator(Env env)
//  {
//    return _value.getValueIterator(env);
//  }
//
//  /**
//   * Returns the array ref.
//   */
//
//  public Value getArray()
//  {
//    if (! _value.isset()) {
//      _value = new ArrayValueImpl();
//    }
//
//    checkVar(_value);
//
//    return _value;
//  }
//
//  /**
//   * Returns the value, creating an object if unset.
//   */
//
//  public Value getObject(Env env)
//  {
//    if (! _value.isset()) {
//      _value = env.createObject();
//    }
//
//    checkVar(_value);
//
//    return _value;
//  }
//
//  /**
//   * Returns the array ref.
//   */
//
//  public Value get(Value index)
//  {
//    return _value.get(index);
//  }
//
//  /**
//   * Returns a reference to the array value.
//   */
//
//  public Value getRef(Value index)
//  {
//    // php/066z
//    return _value.getRef(index);
//  }
//
//  /**
//   * Returns the array ref.
//   */
//
//  public Var getVar(Value index)
//  {
//    // php/3d1a
//    // php/34ab
//    if (! _value.toBoolean()) {
//      _value = new ArrayValueImpl();
//    }
//
//    checkVar(_value);
//
//    return _value.getVar(index);
//  }
//
//  /**
//   * Returns the array ref.
//   */
//
////  public Value getArg(Value index, boolean isTop)
////  {
////    // php/0921, php/3921
////
////    if (_value.isset())
////      return _value.getArg(index, isTop);
////    else
////      return new ArgGetValue(this, index); // php/3d2p
////  }
//
//  /**
//   * Returns the value, creating an object if unset.
//   */
//
//  public Value getArray(Value index)
//  {
//    // php/3d11
//    _value = _value.toAutoArray();
//
//    checkVar(_value);
//
//    return _value.getArray(index);
//  }
//
//  /**
//   * Returns the value, doing a copy-on-write if needed.
//   */
//
//  public Value getDirty(Value index)
//  {
//    return _value.getDirty(index);
//  }
//
//  /**
//   * Returns the value, creating an object if unset.
//   */
//
//  public Value getObject(Env env, Value index)
//  {
//    // php/3d2p
//    _value = _value.toAutoArray();
//
//    checkVar(_value);
//
//    return _value.getObject(env, index);
//  }
//
//  /**
//   * Returns the array ref.
//   */
//
//  public Value put(Value index, Value value)
//  {
//    // php/33m{g,h}
//    // _value = _value.toAutoArray().append(index, value);
//    _value = _value.append(index, value);
//
//    checkVar(_value);
//
//    // this is slow, but ok because put() is only used for slow ops
//    if (_value.isArray() || _value.isObject()) {
//      return value;
//    }
//    else {
//      // for strings
//      return _value.get(index);
//    }
//  }
//
//  /**
//   * Sets the array value, returning the new array, e.g. to handle
//   * string update ($a[0] = 'A').
//   */
//
//  public Value append(Value index, Value value)
//  {
//    // php/323g
//    _value = _value.append(index, value);
//
//    checkVar(_value);
//
//    return _value;
//  }
//
//  /**
//   * Returns the array ref.
//   */
//
//  public Value put(Value value)
//  {
//    _value = _value.toAutoArray();
//
//    checkVar(_value);
//
//    return _value.put(value);
//  }
//
//  /**
//   * Returns the array ref.
//   */
//
//  public Var putVar()
//  {
//    _value = _value.toAutoArray();
//
//    checkVar(_value);
//
//    return _value.putVar();
//  }
//
//  /**
//   * Return true if the array value is set
//   */
//
//  public boolean isset(Value index)
//  {
//    return _value.isset(index);
//  }
//
//  /**
//   * Return unset the value.
//   */
//
//  public Value remove(Value index)
//  {
//    return _value.remove(index);
//  }
//
//  //
//  // Field references
//  //
//
//  /**
//   * Returns the field value.
//   */
//
//  public Value getField(Env env, StringValue name)
//  {
//    return _value.getField(env, name);
//  }
//
//  /**
//   * Returns the field ref.
//   */
//
//  public Var getFieldVar(Env env, StringValue name)
//  {
//    // php/3a0r
//    _value = _value.toAutoObject(env);
//
//    checkVar(_value);
//
//    return _value.getFieldVar(env, name);
//  }
//
//  /**
//   * Returns the array ref.
//   */
//
////  public Value getFieldArg(Env env, StringValue name, boolean isTop)
////  {
////    if (_value.isset())
////      return _value.getFieldArg(env, name, isTop);
////    else {
////      // php/3d1q
////      return new ArgGetFieldValue(env, this, name);
////    }
////  }
//
//  /**
//   * Returns the field value as an array
//   */
//
//  public Value getFieldArray(Env env, StringValue name)
//  {
//    // php/3d1q
//    _value = _value.toAutoObject(env);
//
//    checkVar(_value);
//
//    return _value.getFieldArray(env, name);
//  }
//
//  /**
//   * Returns the field value as an object
//   */
//
//  public Value getFieldObject(Env env, StringValue name)
//  {
//    _value = _value.toAutoObject(env);
//
//    checkVar(_value);
//
//    return _value.getFieldObject(env, name);
//  }
//
//  /**
//   * Sets the field.
//   */
//
//  public Value putField(Env env, StringValue name, Value value)
//  {
//    // php/3a0s
//    _value = _value.toAutoObject(env);
//
//    checkVar(_value);
//
//    return _value.putField(env, name, value);
//  }
//
//  /**
//   * Returns true if the object has this field.
//   */
//
//  public boolean isFieldExists(Env env, StringValue name) {
//    return _value.isFieldExists(env, name);
//  }
//
//  /**
//   * Returns true if the field is set.
//   */
//
//  public boolean issetField(Env env, StringValue name)
//  {
//    return _value.issetField(env, name);
//  }
//
//  /**
//   * Unsets the field.
//   */
//
//  public void unsetField(StringValue name)
//  {
//    _value.unsetField(name);
//  }
//
//  /**
//   * Returns the field value.
//   */
//
//  public Value getThisField(Env env, StringValue name)
//  {
//    return _value.getThisField(env, name);
//  }
//
//  /**
//   * Returns the field ref.
//   */
//
//  public Var getThisFieldVar(Env env, StringValue name)
//  {
//    return _value.getThisFieldVar(env, name);
//  }
//
//  /**
//   * Returns the array ref.
//   */
//
//  public Value getThisFieldArg(Env env, StringValue name)
//  {
//    return _value.getThisFieldArg(env, name);
//  }
//
//  /**
//   * Returns the field value as an array
//   */
//
//  public Value getThisFieldArray(Env env, StringValue name)
//  {
//    return _value.getThisFieldArray(env, name);
//  }
//
//  /**
//   * Appends a value to an array that is a field of an object.
//   */
//
//  public Value putThisFieldArray(Env env,
//                                 Value obj,
//                                 StringValue fieldName,
//                                 Value index,
//                                 Value value)
//  {
//    return _value.putThisFieldArray(env, obj, fieldName, index, value);
//  }
//
//  /**
//   * Returns the field value as an object
//   */
//
//  public Value getThisFieldObject(Env env, StringValue name)
//  {
//    return _value.getThisFieldObject(env, name);
//  }
//
//  /**
//   * Initializes a new field, does not call __set if it is defined.
//   */
//
//  public void initField(Env env,
//                        StringValue name,
//                        StringValue canonicalName,
//                        Value value)
//  {
//    _value.initField(env, name, canonicalName, value);
//  }
//
//  /**
//   * Sets the field.
//   */
//
//  public Value putThisField(Env env, StringValue name, Value value)
//  {
//    return _value.putThisField(env, name, value);
//  }
//
//  /**
//   * Returns true if the field is set.
//   */
//
//  public boolean issetThisField(Env env, StringValue name)
//  {
//    return _value.issetThisField(env, name);
//  }
//
//  /**
//   * Unsets the field.
//   */
//
//  public void unsetThisField(StringValue name)
//  {
//    _value.unsetThisField(name);
//  }
//
//  /**
//   * Unsets the field.
//   */
//
//  public void unsetThisPrivateField(String className, StringValue name)
//  {
//    _value.unsetThisPrivateField(className, name);
//  }
//
//  /**
//   * Returns the static field.
//   */
//
//  public Value getStaticFieldValue(Env env, StringValue name)
//  {
//    return _value.getStaticFieldValue(env, name);
//  }
//
//  /**
//  * Returns the static field reference.
//  */
//
//  public Var getStaticFieldVar(Env env, StringValue name)
//  {
//    return _value.getStaticFieldVar(env, name);
//  }
//
//  /**
//   * Sets the static field.
//   */
//
//  public Value setStaticFieldRef(Env env, StringValue name, Value value)
//  {
//    return _value.setStaticFieldRef(env, name, value);
//  }
//
//  //
//  // array routines
//  //
//
//  /**
//   * Takes the values of this array, unmarshalls them to objects of type
//   * <i>elementType</i>, and puts them in a java array.
//   */
//
//  public Object valuesToArray(Env env, Class elementType)
//  {
//    return _value.valuesToArray(env, elementType);
//  }
//
//  /**
//   * Returns the character at an index
//   */
//
//  public Value charValueAt(long index)
//  {
//    return _value.charValueAt(index);
//  }
//
//  /**
//   * Sets the character at an index
//   */
//
//  public Value setCharValueAt(long index, Value value)
//  {
//    // php/03mg
//
//    _value = _value.setCharValueAt(index, value);
//
//    checkVar(_value);
//
//    return _value;
//  }
//
//  /**
//   * Returns true if there are more elements.
//   */
//
//  public boolean hasCurrent()
//  {
//    return _value.hasCurrent();
//  }
//
//  /**
//   * Returns the current key
//   */
//
//  public Value key()
//  {
//    return _value.key();
//  }
//
//  /**
//   * Returns the current value
//   */
//
//  public Value current()
//  {
//    return _value.current();
//  }
//
//  /**
//   * Returns the current value
//   */
//
//  public Value next()
//  {
//    return _value.next();
//  }
//
//  /**
//   * Returns the previous value
//   */
//
//  public Value prev()
//  {
//    return _value.prev();
//  }
//
//  /**
//   * Returns the end value.
//   */
//
//  public Value end()
//  {
//    return _value.end();
//  }
//
//  /**
//   * Returns the array pointer.
//   */
//
//  public Value reset()
//  {
//    return _value.reset();
//  }
//
//  /**
//   * Shuffles the array.
//   */
//
//  public Value shuffle()
//  {
//    return _value.shuffle();
//  }
//
//  /**
//   * Pops the top array element.
//   */
//
//  public Value pop(Env env)
//  {
//    return _value.pop(env);
//  }
//
//  //
//  // function calls
//  //
//
//  /**
//   * Evaluates the function.
//   */
//  public @Nonnull V<? extends Value> call(Env env, FeatureExpr ctx, Value[] args)
//  {
//    return _value.call(env, VHelper.noCtx(), args);
//  }
//
//  /**
//   * Evaluates the function, returning a reference.
//   */
//  public @Nonnull V<? extends Value> callRef(Env env, FeatureExpr ctx, Value[] args)
//  {
//    return _value.callRef(env, VHelper.noCtx(), args);
//  }
//
//  /**
//   * Evaluates the function, returning a copy
//   */
//  public @Nonnull V<? extends Value> callCopy(Env env, FeatureExpr ctx, Value[] args)
//  {
//    return _value.callCopy(env, VHelper.noCtx(), args);
//  }
//
//  /**
//   * Evaluates the function.
//   */
//
//  public @Nonnull V<? extends Value> call(Env env, FeatureExpr ctx)
//  {
//    return _value.call(env, VHelper.noCtx());
//  }
//
//  /**
//   * Evaluates the function.
//   */
//
//  public @Nonnull V<? extends Value> callRef(Env env, FeatureExpr ctx)
//  {
//    return _value.callRef(env, VHelper.noCtx());
//  }
//
//  /**
//   * Evaluates the function with an argument .
//   */
//
//  public @Nonnull V<? extends Value> call(Env env, FeatureExpr ctx, Value a1)
//  {
//    return _value.call(env, VHelper.noCtx(), a1);
//  }
//
//  /**
//   * Evaluates the function with an argument .
//   */
//
//  public @Nonnull V<? extends Value> callRef(Env env, FeatureExpr ctx,  Value a1)
//  {
//    return _value.callRef(env, ctx, a1);
//  }
//
//  /**
//   * Evaluates the function with arguments
//   */
//
//  public @Nonnull V<? extends Value> call(Env env, FeatureExpr ctx,  Value a1, Value a2)
//  {
//    return _value.call(env, ctx, a1, a2);
//  }
//
//  /**
//   * Evaluates the function with arguments
//   */
//
//  public @Nonnull V<? extends Value> callRef(Env env, FeatureExpr ctx,  Value a1, Value a2)
//  {
//    return _value.callRef(env, ctx, a1, a2);
//  }
//
//  /**
//   * Evaluates the function with arguments
//   */
//
//  public @Nonnull V<? extends Value> call(Env env, FeatureExpr ctx,  Value a1, Value a2, Value a3)
//  {
//    return _value.call(env, ctx, a1, a2, a3);
//  }
//
//  /**
//   * Evaluates the function with arguments
//   */
//
//  public @Nonnull V<? extends Value> callRef(Env env, FeatureExpr ctx,  Value a1, Value a2, Value a3)
//  {
//    return _value.callRef(env, ctx, a1, a2, a3);
//  }
//
//  /**
//   * Evaluates the function with arguments
//   */
//
//  public @Nonnull V<? extends Value> call(Env env, FeatureExpr ctx,  Value a1, Value a2, Value a3, Value a4)
//  {
//    return _value.call(env, ctx, a1, a2, a3, a4);
//  }
//
//  /**
//   * Evaluates the function with arguments
//   */
//
//  public @Nonnull V<? extends Value> callRef(Env env, FeatureExpr ctx,  Value a1, Value a2, Value a3, Value a4)
//  {
//    return _value.callRef(env, ctx, a1, a2, a3, a4);
//  }
//
//  /**
//   * Evaluates the function with arguments
//   */
//
//  public @Nonnull V<? extends Value> call(Env env, FeatureExpr ctx,  Value a1, Value a2, Value a3, Value a4, Value a5)
//  {
//    return _value.call(env, ctx, a1, a2, a3, a4, a5);
//  }
//
//  /**
//   * Evaluates the function with arguments
//   */
//
//  public @Nonnull V<? extends Value> callRef(Env env, FeatureExpr ctx,
//                       Value a1, Value a2, Value a3, Value a4, Value a5)
//  {
//    return _value.callRef(env, ctx, a1, a2, a3, a4, a5);
//  }
//
//  //
//  // method calls
//  //
//
//  /**
//   * Evaluates a method.
//   */
//
//  public @Nonnull V<? extends Value> callMethod(Env env, FeatureExpr ctx, StringValue methodName, int hash,
//                             Value[] args)
//  {
//    return _value.callMethod(env, ctx, methodName, hash, args);
//  }
//
//  /**
//   * Evaluates a method.
//   */
//
//  public @Nonnull V<? extends Value> callMethodRef(Env env, FeatureExpr ctx, StringValue methodName, int hash,
//                                Value[] args)
//  {
//    return _value.callMethodRef(env, ctx, methodName, hash, args);
//  }
//
//  /**
//   * Evaluates a method.
//   */
//
//  public @Nonnull V<? extends Value> callMethod(Env env, FeatureExpr ctx, StringValue methodName, int hash)
//  {
//    return _value.callMethod(env, ctx, methodName, hash);
//  }
//
//  /**
//   * Evaluates a method.
//   */
//
//  public @Nonnull V<? extends Value> callMethodRef(Env env, FeatureExpr ctx, StringValue methodName, int hash)
//  {
//    return _value.callMethodRef(env, ctx, methodName, hash);
//  }
//
//  /**
//   * Evaluates a method.
//   */
//
//  public @Nonnull V<? extends Value> callMethod(Env env,
//                             FeatureExpr ctx, StringValue methodName, int hash,
//                             Value a1)
//  {
//    return _value.callMethod(env, ctx, methodName, hash, a1);
//  }
//
//  /**
//   * Evaluates a method.
//   */
//
//  public @Nonnull V<? extends Value> callMethodRef(Env env,
//                                FeatureExpr ctx, StringValue methodName, int hash,
//                                Value a1)
//  {
//    return _value.callMethodRef(env, ctx, methodName, hash, a1);
//  }
//
//  /**
//   * Evaluates a method.
//   */
//
//  public @Nonnull V<? extends Value> callMethod(Env env,
//                             FeatureExpr ctx, StringValue methodName, int hash,
//                             Value a1, Value a2)
//  {
//    return _value.callMethod(env, ctx, methodName, hash,
//                             a1, a2);
//  }
//
//  /**
//   * Evaluates a method.
//   */
//
//  public @Nonnull V<? extends Value> callMethodRef(Env env,
//                                FeatureExpr ctx, StringValue methodName, int hash,
//                                Value a1, Value a2)
//  {
//    return _value.callMethodRef(env, ctx, methodName, hash,
//                                a1, a2);
//  }
//
//  /**
//   * Evaluates a method with 3 args.
//   */
//
//  public @Nonnull V<? extends Value> callMethod(Env env,
//                             FeatureExpr ctx, StringValue methodName, int hash,
//                             Value a1, Value a2, Value a3)
//  {
//    return _value.callMethod(env, ctx, methodName, hash,
//                             a1, a2, a3);
//  }
//
//  /**
//   * Evaluates a method with 3 args.
//   */
//
//  public @Nonnull V<? extends Value> callMethodRef(Env env,
//                                FeatureExpr ctx, StringValue methodName, int hash,
//                                Value a1, Value a2, Value a3)
//  {
//    return _value.callMethodRef(env, ctx, methodName, hash,
//                                a1, a2, a3);
//  }
//
//  /**
//   * Evaluates a method with 4 args.
//   */
//
//  public @Nonnull V<? extends Value> callMethod(Env env,
//                             FeatureExpr ctx, StringValue methodName, int hash,
//                             Value a1, Value a2, Value a3, Value a4)
//  {
//    return _value.callMethod(env, ctx, methodName, hash, a1, a2, a3, a4);
//  }
//
//  /**
//   * Evaluates a method with 4 args.
//   */
//
//  public @Nonnull V<? extends Value> callMethodRef(Env env,
//                                FeatureExpr ctx, StringValue methodName, int hash,
//                                Value a1, Value a2, Value a3, Value a4)
//  {
//    return _value.callMethodRef(env, ctx, methodName, hash, a1, a2, a3, a4);
//  }
//
//  /**
//   * Evaluates a method with 5 args.
//   */
//
//  public @Nonnull V<? extends Value> callMethod(Env env,
//                             FeatureExpr ctx, StringValue methodName, int hash,
//                             Value a1, Value a2, Value a3, Value a4, Value a5)
//  {
//    return _value.callMethod(env, ctx, methodName, hash,
//                             a1, a2, a3, a4, a5);
//  }
//  /**
//   * Evaluates a method with 5 args.
//   */
//
//  public @Nonnull V<? extends Value> callMethodRef(Env env,
//                                          FeatureExpr ctx, StringValue methodName, int hash,
//                                          Value a1, Value a2, Value a3, Value a4, Value a5)
//  {
//    return _value.callMethodRef(env, ctx, methodName, hash,
//                                a1, a2, a3, a4, a5);
//  }
//
//  /**
//   * Evaluates a method.
//   */
//  /*
//
//  public Value callClassMethod(Env env, AbstractFunction fun, Value []args)
//  {
//    return _value.callClassMethod(env, fun, args);
//  }
//  */
//
//  /**
//   * Prints the value.
//   * @param env
//   * @param ctx
//   */
//
//  public void print(Env env, FeatureExpr ctx)
//  {
//    _value.print(env, VHelper.noCtx());
//  }
//
//  /**
//   * Prints the value.
//   * @param env
//   * @param out
//   */
//
//  public void print(Env env, VWriteStream out)
//  {
//    _value.print(env, out);
//  }
//
//  /**
//   * Serializes the value.
//   */
//
//  public void serialize(Env env, StringBuilder sb)
//  {
//    _value.serialize(env, sb);
//  }
//
//  /*
//   * Serializes the value.
//   *
//   * @param sb holds result of serialization
//   * @param serializeMap holds reference indexes
//   */
////
////  public void serialize(Env env,
////                        StringBuilder sb, SerializeMap serializeMap)
////  {
////    Integer index = serializeMap.get(this);
////
////    if (index != null) {
////      sb.append("R:");
////      sb.append(index);
////      sb.append(";");
////    }
////    else {
////      serializeMap.put(this);
////
////      _value.serialize(env, sb, serializeMap);
////    }
////  }
//
//  /**
//   * Encodes the value in JSON.
//   */
//
//  public void jsonEncode(Env env, JsonEncodeContext context, StringValue sb)
//  {
//    _value.jsonEncode(env, context, sb);
//  }
//
//
//  public void varDumpImpl(Env env,
//                          VWriteStream out,
//                          int depth,
//                          IdentityHashMap<Value, String> valueSet)
//    throws IOException
//  {
//    out.print(VHelper.noCtx(), "&");
//    _value.varDump(env, out, depth, valueSet);
//  }
//
//  protected void printRImpl(Env env,
//                            VWriteStream out,
//                            int depth,
//                            IdentityHashMap<Value, String> valueSet)
//    throws IOException
//  {
//    _value.printRImpl(env, out, depth, valueSet);
//  }
//
//  //
//  // Java Serialization
//  //
//
//  public Object writeReplace()
//  {
//    return _value;
//  }
}

