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

import com.caucho.quercus.QuercusException;
import com.caucho.quercus.program.JavaClassDef;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;
import edu.cmu.cs.varex.VWriteStream;
import javax.annotation.Nonnull;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Represents a Quercus java value.
 */
public class JavaValue extends ObjectValue
  implements Serializable
{
  private static final Logger log
    = Logger.getLogger(JavaValue.class.getName());

  private JavaClassDef _classDef;
  protected Env _env;

  private Object _object;

  public JavaValue(Env env, Object object, JavaClassDef def)
  {
    super(env);

    setQuercusClass(env.createJavaQuercusClass(def));

    _classDef = def;
    _object = object;
  }

  public JavaValue(Env env, Object object, JavaClassDef def, QuercusClass qClass)
  {
    super(env);

    setQuercusClass(qClass);

    _classDef = def;
    _object = object;
  }

  /*
   * Returns the underlying Java class definition.
   */
  protected JavaClassDef getJavaClassDef()
  {
    return _classDef;
  }

  @Override
  public String getClassName()
  {
    return _classDef.getName();
  }

  /**
   * Converts to a double.
   */
  @Override
  public long toLong()
  {
    return StringValue.parseLong(toString(Env.getInstance()));
  }

  /**
   * Converts to a double.
   */
  @Override
  public double toDouble()
  {
    return toDouble(toString(Env.getInstance()).toString());
  }

  /**
   * Converts to a double.
   */
  public static double toDouble(String s)
  {
    int len = s.length();
    int i = 0;
    int ch = 0;

    if (i < len && ((ch = s.charAt(i)) == '+' || ch == '-')) {
      i++;
    }

    for (; i < len && '0' <= (ch = s.charAt(i)) && ch <= '9'; i++) {
    }

    if (ch == '.') {
      for (i++; i < len && '0' <= (ch = s.charAt(i)) && ch <= '9'; i++) {
      }
    }

    if (ch == 'e' || ch == 'E') {
      int e = i++;

      if (i < len && (ch = s.charAt(i)) == '+' || ch == '-') {
        i++;
      }

      for (; i < len && '0' <= (ch = s.charAt(i)) && ch <= '9'; i++) {
      }

      if (i == e + 1)
        i = e;
    }

    if (i != len)
      return 1;
    else
      return Double.parseDouble(s);
  }

  @Override
  public StringValue toString(Env env)
  {
    StringValue value = _classDef.toString(env, this);

    if (value == null) {
      value = env.createString(toString());
    }

    return value;
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

  @Override
  protected void printRImpl(Env env,
                            VWriteStream out,
                            int depth,
                            IdentityHashMap<Value, String> valueSet) {
    if (_classDef.printRImpl(env, _object, out, depth, valueSet)) {
      return;
    }

    Set<? extends VEntry> entrySet = entrySet();

    if (entrySet == null) {
      out.print(VHelper.noCtx(), "resource(" + toString(env) + ")"); // XXX:
      return;
    }

    out.print(VHelper.noCtx(), _classDef.getSimpleName());
    out.println(VHelper.noCtx(), " Object");
    printRDepth(out, depth);
    out.print(VHelper.noCtx(), "(");

    for (VEntry entry : entrySet) {
      out.println(VHelper.noCtx());
      printRDepth(out, depth);
      out.print(VHelper.noCtx(), "    [" + entry.getKey() + "] => ");

      entry.getEnvVar().getOne().printRImpl(env, out, depth + 1, valueSet);
    }

    out.println(VHelper.noCtx());
    printRDepth(out, depth);
    out.println(VHelper.noCtx(), ")");
  }

  @Override
  public void varDumpImpl(Env env,
                          VWriteStream out,
                          int depth,
                          IdentityHashMap<Value, String> valueSet) {
    Value oldThis = env.setThis(this);

    try {
      if (! _classDef.varDumpImpl(env, this, _object, out, depth, valueSet))
        out.print(VHelper.noCtx(), "resource(" + toString(env) + ")"); // XXX:
    }
    finally {
      env.setThis(oldThis);
    }
  }

  //
  // field routines
  //

  /**
   * Returns the field value.
   */
  @Override
  public V<? extends Value> getField(Env env, StringValue name)
  {
    Value value = _classDef.getField(env, this, name);

    if (value != null)
      return V.one(value);
    else
      return V.one(UnsetValue.NULL);
  }

  /**
   * Sets the field value.
   */
  @Override
  public V<? extends Value> putField(Env env, FeatureExpr ctx, StringValue name, V<? extends ValueOrVar> value)
  {
    Value oldValue = _classDef.putField(env, this, name, value.getOne().toValue());

    if (oldValue != null)
      return V.one(oldValue);
    else
      return V.one(NullValue.NULL);
  }

  @Override
  public Set<? extends VEntry> entrySet()
  {
    return _classDef.entrySet(_object);
  }

  /**
   * Converts to a key.
   */
  @Override
  public Value toKey()
  {
    return new LongValue(System.identityHashCode(this));
  }

  @Override
  public int cmpObject(ObjectValue rValue)
  {
    // php/172z

    if (rValue == this)
      return 0;

    if (!(rValue instanceof JavaValue))
      return -1;

    Object rObject = rValue.toJavaObject();

    return _classDef.cmpObject(_object,
                               rObject,
                               ((JavaValue) rValue)._classDef);
  }

  /**
   * Returns true for an object.
   */
  @Override
  public boolean isObject()
  {
    return true;
  }

  /**
   * Returns true for a resource.
   */
  @Override
  public boolean isResource()
  {
    return false;
  }

  /**
   * Returns the type.
   */
  @Override
  public String getType()
  {
    return "object";
  }

  /**
   * Returns the method.
   */
  /*
  @Override
  public AbstractFunction findFunction(String methodName)
  {
    return _classDef.findFunction(methodName);
  }
  */

  /**
   * Evaluates a method.
   */
  @Override
  public V<? extends ValueOrVar> callMethod(Env env,
                                            FeatureExpr ctx, StringValue methodName, int hash,
                                            V<? extends ValueOrVar>[] args)
  {
    return _classDef.callMethod(env, ctx, this, methodName, hash, args);
  }


  /**
   * Evaluates a method.
   */
  @Override
  public @Nonnull V<? extends ValueOrVar> callMethodRef(Env env, FeatureExpr ctx, StringValue methodName, int hash,
                                                   V<? extends ValueOrVar>[] args)
  {
    return _classDef.callMethod(env, ctx,  this, methodName, hash, args);
  }


  @Override
  public Value clone(Env env)
  {
    Object obj = null;

    if (_object != null) {
      if (! (_object instanceof Cloneable)) {
        return env.error(L.l("Java class {0} does not implement Cloneable",
                             _object.getClass().getName()));
      }

      Class<?> cls = _classDef.getType();

      try {
        Method method = cls.getMethod("clone");
        method.setAccessible(true);

        obj = method.invoke(_object);
      }
      catch (NoSuchMethodException e) {
        throw new QuercusException(e);
      }
      catch (InvocationTargetException e) {
        throw new QuercusException(e.getCause());
      }
      catch (IllegalAccessException e) {
        throw new QuercusException(e);
      }
    }

    return new JavaValue(env, obj, _classDef, getQuercusClass());
  }

  /**
   * Serializes the value.
   */
  @Override
  public void serialize(Env env, StringBuilder sb, SerializeMap map)
  {
    String name = _classDef.getSimpleName();

    Set<? extends VEntry> entrySet = entrySet();

    if (entrySet != null) {
      sb.append("O:");
      sb.append(name.length());
      sb.append(":\"");
      sb.append(name);
      sb.append("\":");
      sb.append(entrySet.size());
      sb.append(":{");

      for (VEntry entry : entrySet) {
        entry.getKey().serialize(env, sb);
        entry.getEnvVar().getOne().serialize(env, sb, map);
      }

      sb.append("}");
    }
    else {
      // php/121f
      sb.append("i:0;");
    }
  }

  /**
   * Encodes the value in JSON.
   */
  @Override
  public void jsonEncode(Env env, JsonEncodeContext context, StringValue sb)
  {
    if (_classDef.jsonEncode(env, _object, context, sb))
      return;
    else
      super.jsonEncode(env, context, sb);
  }

  /**
   * Converts to a string.
   */
  public String toString()
  {
    if (_object != null) {
      return String.valueOf(_object);
    }
    else {
      return String.valueOf(_classDef.getName());
    }
  }


  /**
   * Converts to an object.
   */
  @Override
  public Object toJavaObject()
  {
    return _object;
  }

  /**
   * Converts to a java object.
   */
  @Override
  public final Object toJavaObject(Env env, Class<?> type)
  {
    final Object object = _object;
    final Class<?> objectClass = _object.getClass();

    if (type == objectClass || type.isAssignableFrom(objectClass)) {
      return object;
    } else {
      env.warning(L.l("Can't assign {0} to {1}",
                      objectClass.getName(), type.getName()));

      return null;
    }
  }

  /**
   * Converts to a java object.
   */
  @Override
  public Object toJavaObjectNotNull(Env env, Class<?> type)
  {
    Class<?> objClass = _object.getClass();

    if (objClass == type || type.isAssignableFrom(objClass)) {
      return _object;
    } else {
      env.warning(L.l("Can't assign {0} to {1}",
                      objClass.getName(), type.getName()));

      return null;
    }
  }

  /**
   * Converts to a java object.
   */
  @Override
  public Map<?,?> toJavaMap(Env env, Class<?> type)
  {
    if (type.isAssignableFrom(_object.getClass())) {
      return (Map<?,?>) _object;
    } else {
      env.warning(L.l("Can't assign {0} to {1}",
                      _object.getClass().getName(), type.getName()));

      return null;
    }
  }

  /**
   * Converts to an object.
   */
  @Override
  public InputStream toInputStream()
  {
    if (_object instanceof InputStream)
      return (InputStream) _object;
    else if (_object instanceof File) {
      try {
        InputStream is = new FileInputStream((File) _object);

        Env.getCurrent().addCleanup(new EnvCloseable(is));

        return is;
      } catch (IOException e) {
        throw new QuercusException(e);
      }
    }
    else
      return super.toInputStream();
  }

  private static void printRDepth(VWriteStream out, int depth)
  {
    for (int i = 0; i < 8 * depth; i++)
      out.print(VHelper.noCtx(), ' ');
  }

  //
  // Java Serialization
  //

  private void writeObject(ObjectOutputStream out)
    throws IOException
  {
    out.writeObject(_classDef.getType().getCanonicalName());

    out.writeObject(_object);
  }

  private void readObject(ObjectInputStream in)
    throws ClassNotFoundException, IOException
  {
    _env = Env.getInstance();

    _classDef = _env.getJavaClassDefinition((String) in.readObject());

    int id = _env.getQuercus().getClassId(_classDef.getName());

    setQuercusClass(_env.createQuercusClass(id, _classDef, null));

    _object = in.readObject();
  }

  private static class EntryItem implements VEntry {
    private Value _key;
    private EnvVar _value;
    private boolean _isArray;

    EntryItem(Value key, EnvVar value)
    {
      _key = key;
      _value = value;
    }

    @Override
    public Value getKey()
    {
      return _key;
    }

    @Override
    public FeatureExpr getCondition() {
      return VHelper.noCtx();
    }

    @Override
    public EnvVar getEnvVar()
    {
      return _value;
    }

    @Override
    public EnvVar setEnvVar(EnvVar value)
    {
      return _value;
    }

//    void addValue(Value value)
//    {
//      ArrayValue array = null;
//
//      if (! _isArray) {
//        _isArray = true;
//        Value oldValue = _value;
//        _value = new ArrayValueImpl();
//        array = (ArrayValue) _value;
//        array.append(oldValue);
//      }
//      else {
//        array = (ArrayValue) _value;
//      }
//
//      array.append(value);
//    }
  }
}

