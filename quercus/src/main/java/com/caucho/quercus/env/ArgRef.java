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
import edu.cmu.cs.varex.VWriteStream;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents a reference to a PHP variable in a function call.
 */
public class ArgRef extends Value
  implements Serializable
{
  private Var _var;

  public ArgRef(Var var)
  {
    _var = var;
  }

  @Override
  public boolean hasCurrent()
  {
    return _var.getValue().getOne().hasCurrent();
  }

  /**
   * Returns true for an implementation of a class
   */
  @Override
  public boolean isA(Env env, String name)
  {
    return _var.getValue().getOne().isA(env, name);
  }

  /**
   * True for a long
   */
  @Override
  public boolean isLongConvertible()
  {
    return _var.getValue().getOne().isLongConvertible();
  }

  /**
   * True to a double.
   */
  @Override
  public boolean isDoubleConvertible()
  {
    return _var.getValue().getOne().isDoubleConvertible();
  }

  /**
   * True for a number
   */
  @Override
  public boolean isNumberConvertible()
  {
    return _var.getValue().getOne().isNumberConvertible();
  }

  /**
   * Returns true for a long-value.
   */
  public boolean isLong()
  {
    return _var.getValue().getOne().isLong();
  }

  /**
   * Returns true for a long-value.
   */
  public boolean isDouble()
  {
    return _var.getValue().getOne().isDouble();
  }

  @Override
  public ArrayValue toArrayValue(Env env)
  {
    // php/3co1
    return _var.getValue().getOne().toArrayValue(env);
  }

  /**
   * Converts to a boolean.
   */
  @Override
  public boolean toBoolean()
  {
    return _var.getValue().getOne().toBoolean();
  }

  /**
   * Converts to a long.
   */
  @Override
  public long toLong()
  {
    return _var.getValue().getOne().toLong();
  }

  /**
   * Converts to a double.
   */
  @Override
  public double toDouble()
  {
    return _var.getValue().getOne().toDouble();
  }

  /**
   * Converts to a string.
   * @param env
   */
  @Override
  public StringValue toString(Env env)
  {
    return _var.getValue().getOne().toString(env);
  }

  /**
   * Converts to an object.
   */
  @Override
  public Value toObject(Env env)
  {
    return _var.getValue().getOne().toObject(env);
  }

  /**
   * Converts to an object.
   */
  @Override
  public Object toJavaObject()
  {
    return _var.getValue().getOne().toJavaObject();
  }

  /**
   * Converts to a raw value.
   */
  @Override
  public Value toValue()
  {
    return _var.getValue().getOne().toValue();
  }

  /**
   * Returns true for an object.
   */
  @Override
  public boolean isObject()
  {
    return _var.getValue().getOne().isObject();
  }

  /**
   * Returns true for an array.
   */
  @Override
  public boolean isArray()
  {
    return _var.getValue().getOne().isArray();
  }

  /**
   * Copy the value.
   */
  @Override
  public Value copy()
  {
    // quercus/0d05
    return this;
  }

  /**
   * Converts to an argument value.
   */
  @Override
  public Value toLocalValueReadOnly()
  {
    return _var.getValue().getOne();
  }

  /**
   * Converts to an argument value.
   */
  @Override
  public Value toLocalValue()
  {
    // php/0471, php/3d4a
    return _var.getValue().getOne().toLocalValue();
  }

  /**
   * Converts to an argument value.
   */
  @Override
  public Value toLocalRef()
  {
    return _var.getValue().getOne();
  }

  /**
   * Converts to an argument value.
   */
  @Override
  public Var toLocalVar()
  {
    return _var;
  }

  /**
   * Converts to an argument value.
   */
  @Override
  public Value toRefValue()
  {
    return _var.getValue().getOne();
  }

  /**
   * Converts to a variable
   */
  @Override
  public Var toVar()
  {
    return _var;
  }

  /**
   * Converts to a reference variable
   */
  @Override
  public Var toLocalVarDeclAsRef()
  {
    return _var;
  }

  @Override
  public StringValue toStringValue()
  {
    return _var.getValue().getOne().toStringValue();
  }

  @Override
  public StringValue toBinaryValue(Env env)
  {
    return _var.getValue().getOne().toBinaryValue(env);
  }

  @Override
  public StringValue toUnicodeValue(Env env)
  {
    return _var.getValue().getOne().toUnicodeValue(env);
  }

  @Override
  public StringValue toStringBuilder()
  {
    return _var.getValue().getOne().toStringBuilder();
  }

  @Override
  public StringValue toStringBuilder(Env env)
  {
    return _var.getValue().getOne().toStringBuilder(env);
  }

  @Override
  public java.io.InputStream toInputStream()
  {
    return _var.getValue().getOne().toInputStream();
  }

  @Override
  public Value append(Value index, Value value)
  {
    return _var.getValue().getOne().append(index, value);
  }

  @Override
  public Value containsKey(Value key)
  {
    return _var.getValue().getOne().containsKey(key);
  }

  @Override
  public Value copyArrayItem()
  {
    return _var.getValue().getOne().copyArrayItem();
  }

  @Override
  public Value current()
  {
    return _var.getValue().getOne().current();
  }

  @Override
  public Value getArray()
  {
    return _var.getValue().getOne().getArray();
  }

  @Override
  public Value getArray(Value index)
  {
    return _var.getValue().getOne().getArray(index);
  }

  @Override
  public int getCount(Env env)
  {
    return _var.getValue().getOne().getCount(env);
  }

  @Override
  public Value[] getKeyArray(Env env)
  {
    return _var.getValue().getOne().getKeyArray(env);
  }

  @Override
  public Value key()
  {
    return _var.getValue().getOne().key();
  }

  @Override
  public Value next()
  {
    return _var.getValue().getOne().next();
  }

  @Override
  public ArrayValue toArray()
  {
    return _var.getValue().getOne().toArray();
  }

  @Override
  public Value toAutoArray()
  {
    return _var.getValue().getOne().toAutoArray();
  }

  /**
   * Negates the value.
   */
  @Override
  public Value neg()
  {
    return _var.getValue().getOne().neg();
  }

  /**
   * Adds to the following value.
   */
  @Override
  public Value add(Value rValue)
  {
    return _var.getValue().getOne().add(rValue);
  }

  /**
   * Adds to the following value.
   */
  @Override
  public Value add(long rValue)
  {
    return _var.getValue().getOne().add(rValue);
  }

//  /**
//   * Pre-increment the following value.
//   */
//  @Override
//  public Value preincr(int incr)
//  {
//    return _var.getValue().getOne().preincr(incr);
//  }
//
//  /**
//   * Post-increment the following value.
//   */
//  @Override
//  public Value postincr(int incr)
//  {
//    return _var.getValue().getOne().postincr(incr);
//  }

  /**
   * Increment the following value.
   */
  @Override
  public Value increment(int incr)
  {
    return _var.getValue().getOne().increment(incr);
  }

  /**
   * Subtracts to the following value.
   */
  @Override
  public Value sub(Value rValue)
  {
    return _var.getValue().getOne().sub(rValue);
  }

  /**
   * Subtracts to the following value.
   */
  @Override
  public Value sub(long rValue)
  {
    return _var.getValue().getOne().sub(rValue);
  }

  /**
   * Multiplies to the following value.
   */
  @Override
  public Value mul(Value rValue)
  {
    return _var.getValue().getOne().mul(rValue);
  }

  /**
   * Multiplies to the following value.
   */
  @Override
  public Value mul(long lValue)
  {
    return _var.getValue().getOne().mul(lValue);
  }

  /**
   * Divides the following value.
   */
  @Override
  public Value div(Value rValue)
  {
    return _var.getValue().getOne().div(rValue);
  }

  /**
   * Shifts left by the value.
   */
  @Override
  public Value lshift(Value rValue)
  {
    return _var.getValue().getOne().lshift(rValue);
  }

  /**
   * Shifts right by the value.
   */
  @Override
  public Value rshift(Value rValue)
  {
    return _var.getValue().getOne().rshift(rValue);
  }

  /**
   * Absolute value.
   */
  public Value abs()
  {
    return _var.getValue().getOne().abs();
  }

  /**
   * Returns true for equality
   */
  @Override
  public boolean eql(Value rValue)
  {
    return _var.getValue().getOne().eql(rValue);
  }

  /**
   * Returns the array/object size
   */
  @Override
  public int getSize()
  {
    return _var.getValue().getOne().getSize();
  }

  @Override
  public Iterator<Map.Entry<Value, EnvVar>> getIterator(Env env)
  {
    return _var.getValue().getOne().getIterator(env);
  }

  @Override
  public Iterator<Value> getKeyIterator(Env env)
  {
    return _var.getValue().getOne().getKeyIterator(env);
  }

  @Override
  public Iterator<EnvVar> getValueIterator(Env env)
  {
    return _var.getValue().getOne().getValueIterator(env);
  }

  /**
   * Returns the array ref.
   */
  @Override
  public EnvVar get(Value index)
  {
    return _var.getValue().getOne().get(index);
  }

  /**
   * Returns the array ref.
   */
  @Override
  public EnvVar getVar(Value index)
  {
    return _var.getValue().getOne().getVar(index);
  }

  /**
   * Returns the array ref.
   */
  @Override
  public Value put(Value index, Value value)
  {
    return _var.getValue().getOne().put(index, value);
  }

  /**
   * Returns the array ref.
   */
  @Override
  public Value put(Value value)
  {
    return _var.getValue().getOne().put(value);
  }

  /**
   * Returns the character at an index
   */
  /* XXX: need test first
  public Value charAt(long index)
  {
    return _ref.charAt(index);
  }
  */

  /**
   * Evaluates a method.
   */
  @Override
  public V<? extends Value> callMethod(Env env,
                                       FeatureExpr ctx, StringValue methodName, int hash,
                                       V<? extends ValueOrVar>[] args)
  {
    return _var.getValue().getOne().callMethod(env, ctx, methodName, hash, args);
  }

  /**
   * Evaluates a method.
   */
  @Override
  public @NonNull V<? extends Value> callMethod(Env env, FeatureExpr ctx, StringValue methodName, int hash)
  {
    return _var.getValue().getOne().callMethod(env, ctx, methodName, hash);
  }

  /**
   * Evaluates a method.
   */
  @Override
  public @NonNull V<? extends Value> callMethodRef(Env env,
                                FeatureExpr ctx, StringValue methodName, int hash,
                                                   V<? extends ValueOrVar>[] args)
  {
    return _var.getValue().getOne().callMethodRef(env, ctx, methodName, hash, args);
  }

  /**
   * Evaluates a method.
   */
  @Override
  public @NonNull V<? extends Value> callMethodRef(Env env, FeatureExpr ctx, StringValue methodName, int hash)
  {
    return _var.getValue().getOne().callMethodRef(env, ctx, methodName, hash);
  }


  /**
   * Evaluates a method.
   */
  /*
  @Override
  public Value callClassMethod(Env env, AbstractFunction fun, Value []args)
  {
    return _var.getValue().getOne().callClassMethod(env, fun, args);
  }
  */

  /**
   * Serializes the value.
   */
  public void serialize(Env env, StringBuilder sb)
  {
    _var.getValue().getOne().serialize(env, sb);
  }

  /*
   * Serializes the value.
   *
   * @param sb holds result of serialization
   * @param serializeMap holds reference indexes
   */
  public void serialize(Env env, StringBuilder sb, SerializeMap serializeMap)
  {
    _var.getValue().getOne().serialize(env, sb, serializeMap);
  }

  /**
   * Prints the value.
   * @param env
   * @param ctx
   */
  @Override
  public void print(Env env, FeatureExpr ctx)
  {
    _var.getValue().getOne().print(env, VHelper.noCtx());
  }

  @Override
  public void varDumpImpl(Env env,
                          VWriteStream out,
                          int depth,
                          IdentityHashMap<Value,String> valueSet)
    throws IOException
  {
    out.print(VHelper.noCtx(), "&");
    toValue().varDumpImpl(env, out, depth, valueSet);
  }

  @Override
  protected void printRImpl(Env env,
                            VWriteStream out,
                            int depth,
                            IdentityHashMap<Value, String> valueSet)
    throws IOException
  {
    toValue().printRImpl(env, out, depth, valueSet);
  }

  //
  // Java Serialization
  //

  public Object writeReplace()
  {
    return _var;
  }
}

