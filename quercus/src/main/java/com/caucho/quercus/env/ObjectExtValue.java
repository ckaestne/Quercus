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
import com.caucho.quercus.program.ClassField;
import com.caucho.util.CurrentTime;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.UnimplementedVException;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;
import edu.cmu.cs.varex.VWriteStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * Represents a PHP object value.
 *
 * Variational design:
 * for now we assume that classes do not have any structural variation.
 * Methods cannot be added at runtime, thus the method map is not variational,
 * neither is the pointer to the class structure or any content in the class
 * structure (pointers in ObjectValue not here).
 * The thing that can be added and manipulated at runtime are fields and
 * those are variational.
 *
 * We use a variational map internally, but map this to VEntry entries for outside
 * users through iterators (for compatibility with arrays).
 *
 */
@SuppressWarnings("serial")
public class ObjectExtValue extends ObjectValue
  implements Serializable
{
  @Nonnull
  private MethodMap<AbstractFunction> _methodMap;

  /**
   * additional invariant here: EnvVar entries in fieldmap are never null
   * and neither are the values in those EnvVar. They are all initialized with
   * UnsetValue in all configurations even if those configurations are not
   * triggered yet
   */
  @Nonnull
  private Map<StringValue, EnvVar> _fieldMap = new HashMap<>();

  @Nonnull
  private Map<StringValue, EnvVar> _protectedFieldMap = new HashMap<>();

  private boolean _isFieldInit;

  public ObjectExtValue(Env env, QuercusClass cl)
  {
    super(env, cl);

    _methodMap = cl.getMethodMap();
  }

  public ObjectExtValue(Env env, ObjectExtValue copy, CopyRoot root)
  {
    super(env, copy.getQuercusClass());

    root.putCopy(copy, this);

    _methodMap = copy._methodMap;

    _isFieldInit = copy._isFieldInit;

    for (Map.Entry<StringValue, EnvVar> entry : copy._fieldMap.entrySet()) {
      EnvVar entryCopy = _copyTree(entry.getValue(), env, root);

      _fieldMap.put(entry.getKey(), entryCopy);
    }

    _incompleteObjectName = copy._incompleteObjectName;
  }

  public ObjectExtValue(Env env,
                        IdentityHashMap<Value,EnvVar> copyMap,
                        ObjectExtValue copy)
  {
    super(env, copy.getQuercusClass());
    throw new UnimplementedVException();
//
//    _methodMap = copy._methodMap;
//
//    _isFieldInit = copy._isFieldInit;
//
//    for (Map.Entry<StringValue, V<? extends EnvVar>> entry : copy._fieldMap.entrySet()) {
//      Entry entryCopy = new Entry(env, copyMap, entry.getValue());
//
//      _fieldMap.put(entry.getKey(), entryCopy);
//    }
//
//    _incompleteObjectName = copy._incompleteObjectName;
  }

  private void init()
  {
    _fieldMap = new HashMap<>();
  }

  @Override
  public void setQuercusClass(QuercusClass cl)
  {
    super.setQuercusClass(cl);

    _methodMap = cl.getMethodMap();
  }

  /**
   * Initializes the incomplete class.
   */
  @Override
  public void initObject(Env env, FeatureExpr ctx, QuercusClass cls)
  {
    setQuercusClass(cls);
    _incompleteObjectName = null;

    Map<StringValue, EnvVar> existingFields = _fieldMap;
    _fieldMap = new HashMap<>();

    cls.initObject(env, this);

    Iterator<Map.Entry<StringValue, EnvVar>> iter = existingFields.entrySet().iterator();

    while (iter.hasNext()) {
      Map.Entry<StringValue, EnvVar> newField = iter.next();

      setFieldValue_init(ctx, newField.getKey(), newField.getValue());

      /*
      Entry entry = getThisEntry(newField._key);

      if (entry != null) {
        entry._value = newField._value;
      }
      else {
        putThisField(env, newField._key, newField._value);
      }
      */
    }
  }


  /**
   * Returns the number of entries.
   */
  @Override
  public int getSize()
  {
    throw new UnimplementedVException("need variational implementation returning only not unset fields in each configuration");
//    return _fieldMap.size();
  }

  /**
   * Gets a field value.
   */
  @Override
  public final V<? extends Value> getField(Env env, StringValue name)
  {
    EnvVar returnValue = getFieldExt(env, name);

    //V: this looks entirely redundant; didn't getField try that already?
//    if (returnValue == UnsetValue.UNSET) {
//      // __get didn't work, lets look in the class itself
//      EnvVar entry = _fieldMap.get(name);
//
//      if (entry != null) {
//        // php/09ks vs php/091m
//        return entry.getValue()._value.getOne().toValue();
//      }
//    }

    return returnValue.getValue();
  }

  /**
   * Gets a field value.
   */
  @Override
  public V<? extends Value> getThisField(Env env, StringValue name)
  {
    EnvVar entry = getThisEntry(name);
    if (entry == null) entry = EnvVar.fromValue(null);

    return entry.getValue().<Value>flatMap(v -> {
      if (v != null) {
        return V.one(v);
      }

      return getFieldExt(env, name).getValue();
    });


  }

  /**
   * Returns fields not explicitly specified by this value.
   */
  protected @Nonnull EnvVar getFieldExt(Env env, StringValue name)
  {
    EnvVar entry = getEntry(env, name);
    if (entry==null) entry = EnvVar.fromValue(UnsetValue.UNSET);
    FeatureExpr hasValue = entry.getValue().when(v -> v == null || v == UnsetValue.UNSET || v == NullValue.NULL).not();
    return new EnvVarImpl(V.choice(hasValue,
            entry.getVar(),
            V.one(Var.create(_quercusClass.getField(env, VHelper.noCtx().and(hasValue.not()), this, name)))));
  }

  /**
   * Returns the array ref.
   */
  @Override
  public V<? extends Var> getFieldVar(Env env, StringValue name)
  {
    return getFieldExt(env, name).getVar();
  }

  /**
   * Returns the array ref.
   */
  @Override
  public V<? extends Var> getThisFieldVar(Env env, StringValue name)
  {
    EnvVar entry = getThisEntry(name);
    if (entry==null) entry = EnvVar.fromValue(UnsetValue.UNSET);
    FeatureExpr hasValue = entry.getValue().when(v -> v == null || v == UnsetValue.UNSET || v == NullValue.NULL).not();
    return V.choice(hasValue,
            entry.getVar(),
            getFieldExt(env, name).getVar());
  }

  /**
   * Returns the value as an argument which may be a reference.
   */
  @Override
  public V<? extends Var> getFieldArg(Env env, StringValue name, boolean isTop)
  {
    EnvVar entry = getFieldExt(env, name);
    return entry.getVar();

    //TODO V incomplete
//    if (entry != null) {
//      EnvVar value = entry.getEnvVar();
//
//      if (isTop || ! value.getOne().isset())
//        return entry.toArg().getOne();
//      else
//        return value.getVar().getOne();
//    }
//
//    Value value = getFieldExt(env, name);
//
//    if (value != UnsetValue.UNSET)
//      return value.makeVar();
//
//    return new ArgGetFieldValue(env, this, name).makeVar();
  }

  /**
   * Returns the value as an argument which may be a reference.
   */
  @Override
  public V<? extends Var> getThisFieldArg(Env env, StringValue name)
  {
    EnvVar value = getFieldExt(env, name);

    FeatureExpr whenUnset = value.getValue().when(k -> k == UnsetValue.UNSET);
    return V.choice(whenUnset, V.one(new ArgGetFieldValue(env, this, name)), value.getVar());
  }

  /**
   * Returns the value as an argument which may be a reference.
   */
  @Override
  public V<? extends Var> getFieldArgRef(Env env, StringValue name)
  {
    return getThisFieldArg(env, name);
  }

  /**
   * Returns the value as an argument which may be a reference.
   */
  @Override
  public V<? extends Var> getThisFieldArgRef(Env env, StringValue name)
  {
    return getThisFieldArg(env, name);

  }

  /**
   * Adds a new value.
   */
  @Override
  public V<? extends Value> putField(Env env, FeatureExpr ctx, StringValue name, V<? extends ValueOrVar> value)
  {
    @Nullable EnvVar entry = getEntry(env, name);

    // XXX: php/09ks, need visibility check
    if (entry == null) {
      @Nullable V<? extends Value> oldValue = putFieldExt(env, ctx, name, value);

      //TODO V
      if (oldValue != null)
        return oldValue;

      if (! _isFieldInit) {
        AbstractFunction fieldSet = _quercusClass.getFieldSet();

        if (fieldSet != null) {
          _isFieldInit = true;
          Value retVal = _quercusClass.setField(env, this, name, value.getOne().toValue());
          _isFieldInit = false;
          if(retVal != UnsetValue.UNSET)
            return V.one(retVal);
        }
      }

      entry = setFieldValue_init(ctx, name, EnvVar.fromValue(UnsetValue.UNSET));
    }

    entry.setRef(ctx, value);

    return entry.getValue();
  }

  /**
   * Sets/adds field to this object.
   */
  @Override
  public V<? extends Value> putThisField(Env env, FeatureExpr ctx, StringValue name, V<? extends ValueOrVar> value)
  {
    EnvVar entry = getThisEntry(name);
    
    if (entry == null) {
      V<? extends Value> oldValue = putFieldExt(env, ctx, name, value);

      //TODO V
      if (oldValue != null)
        return oldValue;

      if (! _isFieldInit) {
        AbstractFunction fieldSet = _quercusClass.getFieldSet();

        if (fieldSet != null) {
          //php/09k7
          _isFieldInit = true;
          V<? extends Value> retValue = V.one(NullValue.NULL);

          try {
            retValue = fieldSet.callMethod(env, ctx,
                                           _quercusClass,
                                           this,
                    V.one(name),
                    value).map((a)->a.toValue());
          } finally {
            _isFieldInit = false;
          }

          return retValue;
        }
      }

      entry = setFieldValue_init(ctx, name, EnvVar.fromValue(UnsetValue.UNSET));
    }

    entry.setRef(ctx, value);

    return entry.getValue();
  }

  @Nullable
  protected V<? extends Value> putFieldExt(Env env, FeatureExpr ctx, StringValue name, V<? extends ValueOrVar> value)
  {
    return null;
  }

  @Override
  public void setFieldInit(boolean isInit)
  {
    _isFieldInit = isInit;
  }

  /**
   * Returns true if the object is in a __set() method call.
   * Prevents infinite recursion.
   */
  @Override
  public boolean isFieldInit()
  {
    return _isFieldInit;
  }

  /**
   * Adds a new value to the object.
   */
  @Override
  public void initField(Env env,
                        FeatureExpr ctx, StringValue name,
                        StringValue canonicalName,
                        V<? extends Value> value)
  {
    setFieldValue_init(ctx, name, canonicalName, EnvVar.fromValues(value));
  }

  /**
   * Removes a value.
   */
  @Override
  public void unsetField(FeatureExpr ctx, StringValue name)
  {
    Value returnValue = _quercusClass.unsetField(Env.getCurrent(),this,name);
    if(returnValue == UnsetValue.UNSET || returnValue == NullValue.NULL) {
      // __unset didn't work, lets look in the class itself
      EnvVar val = _fieldMap.get(name);
      val.set(ctx, V.one(UnsetValue.UNSET));
      cleanFieldIfEmpty(name, val);
    }

    return;
  }

  private void cleanFieldIfEmpty(StringValue name, EnvVar val) {
    if (val.getValue().when(v -> v == null || v == UnsetValue.UNSET || v == NullValue.NULL).isTautology())
      _fieldMap.remove(name);
  }


  /**
   * Removes the field array ref.
   */
  @Override
  public void unsetArray(Env env, FeatureExpr ctx, StringValue name, Value index)
  {
    // php/022b
    if (_quercusClass.getFieldGet() != null)
      return;

    V<? extends Value> entry = getField(env, name);

    // XXX
    //if (entry._visibility == FieldVisibility.PRIVATE)
      //return;

    entry.vforeach(ctx, (c, e) -> e.toValue().remove(c, index));
  }

  /**
   * Removes the field array ref.
   */
  @Override
  public void unsetThisArray(Env env, FeatureExpr ctx, StringValue name, Value index)
  {
    unsetArray(env, ctx, name, index);
  }

  /**
   * Gets a new value.
   */
  private
  @Nullable
  EnvVar getEntry(Env env, StringValue name)
  {
    @Nullable EnvVar entry = _fieldMap.get(name);

    if (entry == null) {
      entry = getThisProtectedEntry(name);
    }

    if (entry == null) {
      return null;
    }


    if (ClassField.isPrivate(name)) {
      QuercusClass cls = env.getCallingClass();

      // XXX: this really only checks access from outside of class scope
      // php/091m
      if (cls != _quercusClass) {
        return null;
      }
      /* nam: 2012-04-29 this doesn't work, commented out for drupal-7.12
      else if (entry._visibility == FieldVisibility.PROTECTED) {
        QuercusClass cls = env.getCallingClass();

        if (cls == null || (cls != _quercusClass && ! cls.isA(_quercusClass.getName()))) {
            env.notice(L.l("Can't access protected field '{0}::${1}'",
                           _quercusClass.getName(), name));

            return null;
        }
      }
      */
    }

    return entry;
  }

  /**
   * Gets a new value.
   */
  private
  @Nullable
  EnvVar getThisEntry(StringValue name)
  {
    EnvVar entry = _fieldMap.get(name);
    
    if (entry == null) {
      entry = getThisProtectedEntry(name);
    }

    return entry;
  }

  /**
   * Returns the field with protected visibility.
   */
  private
  @Nullable
  EnvVar getThisProtectedEntry(StringValue name)
  {
    if (_protectedFieldMap == null) {
      return null;
    }

    return _protectedFieldMap.get(name);
  }


  private EnvVar setFieldValue_init(FeatureExpr ctx, StringValue canonicalName, EnvVar value) {
    StringValue name = ClassField.getOrdinaryName(canonicalName);

    return setFieldValue_init(ctx, name, canonicalName, value);
  }

  private EnvVar setFieldValue_init(FeatureExpr ctx, StringValue name,
                                    StringValue canonicalName, EnvVar value) {
    EnvVar entry = _fieldMap.get(canonicalName);

    if (entry == null) {
      entry = EnvVar.fromValue(UnsetValue.UNSET);
      _fieldMap.put(canonicalName, entry);

      if (ClassField.isProtected(canonicalName)) {
        if (_protectedFieldMap == null) {
          _protectedFieldMap = new HashMap<>();
        }

        _protectedFieldMap.put(name, entry);
      }
    }
    entry.setRef(ctx, value.getVar());
    return entry;
  }

//  /**
//   * Creates the entry for a key.
//   */
//  private Entry createEntry(StringValue canonicalName)
//  {
//    Entry entry = _fieldMap.get(canonicalName);
//
//    if (entry == null) {
//      entry = new Entry(canonicalName);
//      _fieldMap.put(canonicalName, entry);
//    }
//
//    return entry;
//  }

  //
  // Foreach/Traversable functions
  //

  /**
   * Returns an iterator for the key => value pairs.
   */
  @Override
  public Iterator<VEntry> getIterator(Env env)
  {
    TraversableDelegate delegate = _quercusClass.getTraversableDelegate();

    if (delegate != null)
      return delegate.getIterator(env, this);
    else
      return getBaseIterator(env);
  }

  /**
   * Returns an iterator for the key => value pairs.
   */
  @Override
  public Iterator<VEntry> getBaseIterator(Env env)
  {
    return new KeyValueIterator(_fieldMap.entrySet().iterator());
  }

  /**
   * Returns an iterator for the keys.
   */
  @Override
  public Iterator<Value> getKeyIterator(Env env)
  {
    TraversableDelegate delegate = _quercusClass.getTraversableDelegate();

    if (delegate != null)
      return delegate.getKeyIterator(env, this);

    return new KeyIterator(_fieldMap.keySet().iterator());
  }

  /**
   * Returns an iterator for the values.
   */
  @Override
  public Iterator<EnvVar> getValueIterator(Env env)
  {
    TraversableDelegate delegate = _quercusClass.getTraversableDelegate();

    if (delegate != null)
      return delegate.getValueIterator(env, this);

    return Collections.unmodifiableCollection(_fieldMap.values()).iterator();
  }

  //
  // method calls
  //

  /**
   * Evaluates a method.
   */
  @Override
  public V<? extends ValueOrVar> callMethod(Env env, FeatureExpr ctx, StringValue methodName, int hash,
                                            V<? extends ValueOrVar>[] args)
  {
    AbstractFunction fun = _methodMap.get(methodName, hash);

    return fun.callMethod(env, ctx, _quercusClass, this, args);
  }

  /**
   * Evaluates a method.
   */
  @Override
  public @Nonnull
  V<? extends ValueOrVar> callMethod(Env env, FeatureExpr ctx, StringValue methodName, int hash)
  {
    AbstractFunction fun = _methodMap.get(methodName, hash);

    return fun.callMethod(env, ctx, _quercusClass, this);
  }


  /**
   * Evaluates a method.
   */
  @Override
  public @Nonnull V<? extends ValueOrVar> callMethodRef(Env env, FeatureExpr ctx, StringValue methodName, int hash,
                                                   V<? extends ValueOrVar>[] args)
  {
    AbstractFunction fun = _methodMap.get(methodName, hash);

    return fun.callMethodRef(env, ctx, _quercusClass, this, args);
  }

  /**
   * Evaluates a method.
   */
  @Override
  public @Nonnull
  V<? extends ValueOrVar> callMethodRef(Env env, FeatureExpr ctx, StringValue methodName, int hash)
  {
    AbstractFunction fun = _methodMap.get(methodName, hash);

    return fun.callMethodRef(env, ctx, _quercusClass, this);
  }


  /**
   * Evaluates a method.
   */
  /*
  @Override
  public Value callClassMethod(Env env, AbstractFunction fun, Value []args)
  {
    return fun.callMethod(env, this, args);
  }
  */

  /**
   * Returns the value for the variable, creating an object if the var
   * is unset.
   */
  @Override
  public Value getObject(Env env)
  {
    return this;
  }

  /*
  @Override
  public Value getObject(Env env, Value index)
  {
    // php/3d92

    env.error(L.l("Can't use object '{0}' as array", getName()));

    return NullValue.NULL;
  }
  */

  /**
   * Copy for assignment.
   */
  @Override
  public Value copy()
  {
    return this;
  }

  /**
   * Copy for serialization
   */
  @Override
  public Value copy(Env env, IdentityHashMap<Value,EnvVar> map)
  {
    EnvVar oldValue = map.get(this);

    if (oldValue != null)
      return oldValue.getOne();

    // php/4048 - needs to be deep copy

    return new ObjectExtValue(env, map, this);
  }

  /**
   * Copy for serialization
   */
  @Override
  public Value copyTree(Env env, CopyRoot root)
  {
    // php/420c

    Value copy = root.getCopy(this);

    if (copy != null)
      return copy;
    else
      return new CopyObjectExtValue(env, this, root);
  }

  /**
   * Clone the object
   */
  @Override
  public Value clone(Env env)
  {
    ObjectExtValue newObject = new ObjectExtValue(env, _quercusClass);

    clone(env, newObject);

    return newObject;
  }

  protected void clone(Env env, ObjectExtValue obj) {
    _quercusClass.initObject(env, obj);

    Iterator<Map.Entry<StringValue, EnvVar>> iter = _fieldMap.entrySet().iterator();

    while (iter.hasNext()) {
      Map.Entry<StringValue, EnvVar> entry = iter.next();

      StringValue canonicalName = entry.getKey();
      V<? extends Value> value = entry.getValue().getValue().map((a) -> a.copy());

      obj.initField(env, VHelper.noCtx(), canonicalName, value);
    }
  }

  // XXX: need to check the other copy, e.g. for sessions

  /**
   * Serializes the value.
   *
   * @param sb holds result of serialization
   * @param serializeMap holds reference indexes
   */
  @Override
  public void serialize(Env env,
                        StringBuilder sb,
                        SerializeMap serializeMap)
  {
    Integer index = serializeMap.get(this);

    if (index != null) {
      sb.append("r:");
      sb.append(index);
      sb.append(";");

      return;
    }

    serializeMap.put(this);
    serializeMap.incrementIndex();

    QuercusClass qClass = getQuercusClass();
    AbstractFunction fun = qClass.getSerialize();

    if (fun != null) {
      sb.append("C:");
      sb.append(_className.length());
      sb.append(":");

      sb.append('"');
      sb.append(_className);
      sb.append('"');
      sb.append(':');

      StringValue value = fun.callMethod(env, VHelper.noCtx(), qClass, this).getOne().toValue().toStringValue(env);

      sb.append(value.length());
      sb.append(':');

      sb.append("{");
      sb.append(value);
      sb.append("}");

      return;
    }

    sb.append("O:");
    sb.append(_className.length());
    sb.append(":\"");
    sb.append(_className);
    sb.append("\":");
    sb.append(getSize());
    sb.append(":{");

    Iterator<Map.Entry<StringValue, EnvVar>> iter = _fieldMap.entrySet().iterator();

    while (iter.hasNext()) {
      Map.Entry<StringValue, EnvVar> entry = iter.next();

      sb.append("s:");

      Value key = entry.getKey();
      int len = key.length();

      sb.append(len);
      sb.append(':');

      sb.append('"');
      sb.append(key);
      sb.append('"');

      sb.append(';');

      Value value = entry.getValue().getOne();

      value.serialize(env, sb, serializeMap);
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

    sb.append(getName());
    sb.append("::__set_state(array(\n");

    for (VEntry entry : entrySet()) {
      Value key = entry.getKey();
      Value value = entry.getEnvVar().getOne();

      for (int i = 0; i < level; i++) {
        sb.append("  ");
      }

      sb.append("   ");

      key.varExportImpl(sb, level + 1);

      sb.append(" => ");

      value.varExportImpl(sb, level + 1);
      sb.append(",\n");
    }

    for (int i = 0; i < level; i++) {
      sb.append("  ");
    }

    sb.append("))");
  }

  /**
   * Converts to a string builder
   */
  @Override
  public StringValue toStringBuilder(Env env)
  {
    return toString(env).toStringBuilder(env);
  }

  /**
   * Converts to a java String object.
   */
  @Override
  public String toJavaString()
  {
    return toString(Env.getInstance()).toString();
  }

  /**
   * Converts to a string.
   * @param env
   */
  @Override
  public StringValue toString(Env env)
  {
    AbstractFunction toString = _quercusClass.getToString();

    if (toString != null)
      return toString.callMethod(env, VHelper.noCtx(), _quercusClass, this).getOne().toValue().toStringValue();
    else
      return env.createString(_className + "[]");
  }

  /**
   * Converts to a string.
   * @param env
   * @param ctx
   */
  @Override
  public void print(Env env, FeatureExpr ctx)
  {
    env.print(VHelper.noCtx(),toString(env));
  }

  /**
   * Converts to an array.
   */
  @Override
  public ArrayValue toArray()
  {
    ArrayValue array = new ArrayValueImpl();

    for (VEntry entry : entrySet()) {
      array.put(entry.getKey(), entry.getEnvVar());
    }

    return array;
  }

  /**
   * Converts to an object.
   */
  @Override
  public Value toObject(Env env)
  {
    return this;
  }

  /**
   * Converts to an object.
   */
  @Override
  public Object toJavaObject()
  {
    return this;
  }

  @Override
  public Set<? extends VEntry> entrySet()
  {
    return new EntrySet();
  }

  //
  // debugging
  //

  //XXX: push up to super, and use varDumpObject
  @Override
  public void varDumpImpl(Env env,
                          VWriteStream out,
                          int depth,
                          IdentityHashMap<Value, String> valueSet) {
    int size = getSize();

    if (isIncompleteObject())
      size++;

    out.println(VHelper.noCtx(), "object(" + getName() + ") (" + size + ") {");

    if (isIncompleteObject()) {
      printDepth(out, 2 * (depth + 1));
      out.println(VHelper.noCtx(), "[\"__Quercus_Incomplete_Class_name\"]=>");

      printDepth(out, 2 * (depth + 1));

      Value value = env.createString(getIncompleteObjectName());

      value.varDump(env, out, depth + 1, valueSet);

      out.println(VHelper.noCtx());
    }

    for (VEntry mapEntry : entrySet()) {

      entryVarDumpImpl(env, mapEntry, out, depth + 1, valueSet);
    }

    printDepth(out, 2 * depth);

    out.print(VHelper.noCtx(), "}");
  }

  @Override
  protected void printRImpl(Env env,
                            VWriteStream out,
                            int depth,
                            IdentityHashMap<Value, String> valueSet) {
    out.print(VHelper.noCtx(), getName());
    out.print(VHelper.noCtx(), ' ');
    out.println(VHelper.noCtx(), "Object");
    printDepth(out, 4 * depth);
    out.println(VHelper.noCtx(), "(");

    for (VEntry mapEntry : entrySet()) {

      entryPrintRImpl(env, mapEntry, out, depth + 1, valueSet);
    }

    printDepth(out, 4 * depth);
    out.println(VHelper.noCtx(), ")");
  }

  //
  // Java Serialization
  //

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeObject(_className);

    out.writeInt(_fieldMap.size());

    for (VEntry entry : entrySet()) {
      out.writeObject(entry.getKey());
      out.writeObject(entry.getEnvVar());
    }
  }

  /**
   * Encodes the value in JSON.
   */
  @Override
  public void jsonEncode(Env env, JsonEncodeContext context, StringValue sb)
  {
    if (true) {
      super.jsonEncode(env, context, sb);

      return;
    }

    sb.append('{');

    int length = 0;

    Iterator<Map.Entry<StringValue, EnvVar>> iter = _fieldMap.entrySet().iterator();

    while (iter.hasNext()) {
      Map.Entry<StringValue, EnvVar> entry = iter.next();

      if (! isPublic(entry.getKey())) {
        continue;
      }

      if (length > 0) {
        sb.append(',');
      }

      entry.getKey().toStringValue(env).jsonEncode(env, context, sb);
      sb.append(':');
      entry.getValue().getOne().jsonEncode(env, context, sb);
      length++;
    }

    sb.append('}');
  }

  private void readObject(ObjectInputStream in)
    throws ClassNotFoundException, IOException
  {
    Env env = Env.getInstance();
    String name = (String) in.readObject();

    QuercusClass cl = env.findClass(name);

    init();

    if (cl != null) {
      setQuercusClass(cl);
    }
    else {
      cl = env.getQuercus().getStdClass();

      setQuercusClass(cl);

      setIncompleteObjectName(name);
    }

    int size = in.readInt();

    for (int i = 0; i < size; i++) {
      putThisField(env,
              VHelper.noCtx(), (StringValue) in.readObject(),
                   V.one((Value) in.readObject()));
    }
  }

  @Override
  public V<? extends Boolean> issetField(Env env, StringValue name)
  {
    EnvVar entry = getThisEntry(name);

    if (entry != null && isPublic(name)) {
      return entry.getValue().map(v->v.isset());
    }

    return V.one(getQuercusClass().issetField(env, this, name));
  }

  @Override
  public boolean isFieldExists(Env env, StringValue name)
  {
    //TODO V
    EnvVar entry = getThisEntry(name);

    return entry != null;
  }

  @Override
  public String toString()
  {
    if (CurrentTime.isTest())
      return getClass().getSimpleName() +  "[" + _className + "]";
    else
      return getClass().getSimpleName()
             + "@" + System.identityHashCode(this)
             + "[" + _className + "]";
  }

  public class EntrySet extends AbstractSet<VEntry> {
    EntrySet()
    {
    }

    @Override
    public int size()
    {
      return ObjectExtValue.this.getSize();
    }

    @Override
    public Iterator<VEntry> iterator()
    {
      return new KeyValueIterator(_fieldMap.entrySet().iterator());
    }
  }

  public static class KeyValueIterator
    implements Iterator<VEntry>
  {
    private final Iterator<Map.Entry<StringValue, EnvVar>> _iter;

    KeyValueIterator(Iterator<Map.Entry<StringValue, EnvVar>> iter)
    {
      _iter = iter;
    }

    @Override
    public boolean hasNext()
    {
      return _iter.hasNext();
    }

    @Override
    public VEntry next()
    {
      return new EntryWrapper(_iter.next());
    }

    @Override
    public void remove()
    {
      throw new UnsupportedOperationException();
    }

    private static class EntryWrapper implements VEntry {
      private final Map.Entry<StringValue, EnvVar> e;

      private EntryWrapper(Map.Entry<StringValue, EnvVar> e) {
        this.e = e;
      }

      @Override
      public EnvVar getEnvVar() {
        return e.getValue();
      }

      @Override
      public EnvVar setEnvVar(EnvVar value) {
        e.getValue().setVar(VHelper.noCtx(), value.getVar());
        return value;
      }

      @Override
      public Value getKey() {
        return e.getKey();
      }

      @Override
      public FeatureExpr getCondition() {
        return e.getValue().getValue().when(v -> v == null || v == UnsetValue.UNSET || v == NullValue.NULL).not();
      }
    }
  }



  public static class KeyIterator
    implements Iterator<Value>
  {
    private final Iterator<StringValue> _iter;

    KeyIterator(Iterator<StringValue> iter)
    {
      _iter = iter;
    }

    @Override
    public boolean hasNext()
    {
      return _iter.hasNext();
    }

    @Override
    public Value next()
    {
      return _iter.next();
    }

    @Override
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }

//  public final static class Entry
//    implements VEntry,
//               Comparable<VEntry>
//  {
//    private final StringValue _key;
//
//    private EnvVar _value;
//
//    public Entry(StringValue key)
//    {
//      _key = key;
//      _value = EnvVar._gen(UnsetValue.UNSET);
//    }
//
//    public Entry(StringValue key, EnvVar value)
//    {
//      _key = key;
//      _value = value;
//    }
//
//    public Entry(Env env, IdentityHashMap<Value,EnvVar> map, Entry entry)
//    {
//      _key = entry._key;
//
//      throw new UnimplementedVException();
////      _value = entry._value.copy(env, map);
//    }
//
//    @Override
//    public EnvVar getEnvVar()
//    {
//      return _value;
//    }
//
//
//    public EnvVar getRawValue()
//    {
//      return _value;
//    }
//
//    @Override
//    public StringValue getKey()
//    {
//      return _key;
//    }
//
//    @Override
//    public FeatureExpr getCondition() {
//      return VHelper.noCtx();
//    }
//
//    public boolean isPublic()
//    {
//      return ! isPrivate() && ! isProtected();
//    }
//
//    public boolean isProtected()
//    {
//      return ClassField.isProtected(_key);
//    }
//
//    public boolean isPrivate()
//    {
//      return ClassField.isPrivate(_key);
//    }
//
//    public V<? extends Value> toValue()
//    {
//      // The value may be a var
//      // XXX: need test
//      return _value.getValue();
//    }
//
////    /**
////     * Argument used/declared as a ref.
////     */
////    public Var toRefVar()
////    {
////      Var var = _value.toLocalVarDeclAsRef();
////
////      _value = var;
////
////      return var;
////    }
//
////    /**
////     * Converts to an argument value.
////     */
////    public Value toArgValue()
////    {
////      return _value.toValue();
////    }
////
//    @Override
//    public EnvVar setEnvVar(EnvVar value)
//    {
//      EnvVar oldValue = _value;//TODO V was: toValue();
//
//      _value = value;
//
//      return oldValue;
//    }
//
////    /**
////     * Converts to a variable reference (for function arguments)
////     */
////    public Value toRef()
////    {
////      Value value = _value;
////
////      if (value instanceof Var)
////        return new ArgRef((Var) value);
////      else {
////        Var var = new VarImpl(_value);
////
////        _value = var;
////
////        return new ArgRef(var);
////      }
////    }
//
////    /**
////     * Converts to a variable reference (for function  arguments)
////     */
////    public Value toArgRef()
////    {
////      Value value = _value;
////
////      if (value instanceof Var)
////        return new ArgRef((Var) value);
////      else {
////        Var var = new VarImpl(_value);
////
////        _value = var;
////
////        return new ArgRef(var);
////      }
////    }
//
//    public V<? extends Var> toArg()
//    {
//      return _value.getVar();
//    }
//
//    Entry copyTree(Env env, CopyRoot root)
//    {
//      Value copy = root.getCopy(_value.getOne());
//
//      if (copy == null) {
//        copy = _value.getOne().copyTree(env, root);
//      }
//
//      return new Entry(_key, EnvVar._gen(copy));
//    }
//
//    @Override
//    public int compareTo(VEntry other)
//    {
//      if (other == null)
//        return 1;
//
//      Value thisKey = getKey();
//      Value otherKey = other.getKey();
//
//      if (thisKey == null)
//        return otherKey == null ? 0 : -1;
//
//      if (otherKey == null)
//        return 1;
//
//      return thisKey.cmp(otherKey);
//    }
//
    public void entryVarDumpImpl(Env env,
                                 VEntry entry, VWriteStream out,
                                 int depth,
                                 IdentityHashMap<Value, String> valueSet)
    {
      StringValue key = entry.getKey().toStringValue();
      StringValue name = ClassField.getOrdinaryName(key);
      String suffix = "";

      if (isProtected(key)) {
        suffix = ":protected";
      }
      else if (isPrivate(key)) {
        suffix = ":private";
      }

      printDepth(out, 2 * depth);
      out.println(VHelper.noCtx(), "[\"" + name + suffix + "\"]=>");

      printDepth(out, 2 * depth);

      entry.getEnvVar().getValue().getOne().varDump(env, out, depth, valueSet);

      out.println(VHelper.noCtx());
    }
//
    protected void entryPrintRImpl(Env env,
                                   VEntry entry, VWriteStream out,
                                   int depth,
                                   IdentityHashMap<Value, String> valueSet)
    {
      StringValue key = entry.getKey().toStringValue();
      StringValue name = ClassField.getOrdinaryName(key);
      String suffix = "";

      if (isProtected(key)) {
        suffix = ":protected";
      }
      else if (isPrivate(key)) {
        suffix = ":private";
      }

      printDepth(out, 4 * depth);
      out.print(VHelper.noCtx(), "[" + name + suffix + "] => ");

      entry.getEnvVar().getOne().printR(env, out, depth + 1, valueSet);

      out.println(VHelper.noCtx());
    }
//
//    private void printDepth(VWriteStream out, int depth)
//    {
//      for (int i = 0; i < depth; i++)
//        out.print(VHelper.noCtx(), ' ');
//    }
//
//    @Override
//    public String toString()
//    {
//      return "ObjectExtValue.Entry[" + getKey() + "]";
//    }
//  }


  static private
  @Nonnull
  EnvVar _copyTree(@Nonnull EnvVar _value, Env env, CopyRoot root) {
    return EnvVar.fromValues(_value.getValue().map(a -> {
      Value copy = root.getCopy(a);
      if (copy == null) {
        copy = a.copyTree(env, root);
      }
      return copy;
    }));
  }


  static boolean isPublic(StringValue _key) {
    return !isPrivate(_key) && !isProtected(_key);
  }

  static boolean isProtected(StringValue _key) {
    return ClassField.isProtected(_key);
  }

  static boolean isPrivate(StringValue _key) {
    return ClassField.isPrivate(_key);
  }

}

