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
 * @author Sam
 */

package com.caucho.quercus.lib.spl;

import com.caucho.quercus.annotation.Name;
import com.caucho.quercus.annotation.Optional;
import com.caucho.quercus.env.*;
import com.caucho.quercus.lib.ArrayModule;
import com.caucho.util.L10N;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.VHelper;
import edu.cmu.cs.varex.VWriteStream;

import java.io.IOException;
import java.util.IdentityHashMap;

public class ArrayObject
  implements ArrayAccess,
             Countable,
             IteratorAggregate,
             Traversable
{
  private static L10N L = new L10N(ArrayObject.class);

  public static final int STD_PROP_LIST = 0x00000001;
  public static final int ARRAY_AS_PROPS = 0x00000002;

  private final Env _env;
  private Value _value;
  private int _flags;
  private QuercusClass _iteratorClass;

  @Name("__construct")
  public ArrayObject(Env env,
                     @Optional Value value,
                     @Optional int flags,
                     @Optional("ArrayIterator") String iteratorClassName)
  {
    if (value.isNull()) {
      value = new ArrayValueImpl();
    }

    _env = env;
    _value = value.toValue();
    _flags = flags;

    QuercusClass iteratorClass = _env.findClass(iteratorClassName);

    if (iteratorClass == null || ! iteratorClass.isA(env, "Iterator")) {
      throw new IllegalArgumentException(L.l("A class that implements Iterator must be specified"));
    }

    _iteratorClass = iteratorClass;
  }

  public void append(Value value)
  {
    _value.put(VHelper.noCtx(), value);
  }

  public void asort(@Optional long sortFlag)
  {
    sortFlag = 0; // qa/4a46

    if (_value instanceof ArrayValue)
      ArrayModule.asort(_env, Var.create(_value), sortFlag);
  }

  @Override
  public int count(Env env)
  {
    return _value.getCount(env).getOne();
  }

  public Value exchangeArray(ArrayValue array)
  {
    Value oldValue = _value;

    _value = array;

    return oldValue;
  }

  public Value getArrayCopy()
  {
    return _value.copy();
  }

  public int getFlags()
  {
    return _flags;
  }

  @Override
  public ObjectValue getIterator()
  {
    Value[] args = new Value[] { _value, LongValue.create(_flags) };

    return (ObjectValue) _iteratorClass.callNew(_env, VHelper.toVArray(args));
  }

  public String getIteratorClass()
  {
    return _iteratorClass.getName();
  }

  public void ksort(@Optional long sortFlag)
  {
    if (_value instanceof ArrayValue)
      ArrayModule.ksort(_env, Var.create(_value), sortFlag);
  }

  public void natcasesort()
  {
    if (_value instanceof ArrayValue)
      ArrayModule.natcasesort(_env, Var.create(_value));
  }

  public void natsort()
  {
    if (_value instanceof ArrayValue)
      ArrayModule.natsort(_env, Var.create(_value));
  }

  @Override
  public boolean offsetExists(Env env, Value offset)
  {
    return _value.get(offset).getOne().isset();
  }

  @Override
  public Value offsetGet(Env env, Value offset)
  {
    return _value.get(offset).getOne();
  }

  @Override
  public Value offsetSet(Env env, Value offset, Value value)
  {
    return _value.put(offset, value);
  }

  @Override
  public Value offsetUnset(Env env, Value offset)
  {
    return _value.remove(VHelper.noCtx(), offset).getOne();
  }

  public void setFlags(Value flags)
  {
    _flags = flags.toInt();
  }

  public void setIteratorClass(String iteratorClass)
  {
    _iteratorClass = _env.findClass(iteratorClass);
  }

  public void uasort(Callable func)
  {
    if (_value instanceof ArrayValue)
      ArrayModule.uasort(_env, Var.create(_value), func,  0);
  }

  public void uksort(Callable func, @Optional long sortFlag)
  {
    if (_value instanceof ArrayValue)
      ArrayModule.uksort(_env, Var.create(_value), func, sortFlag);
  }

  public Value __getField(StringValue key)
  {
    if ((_flags & ARRAY_AS_PROPS) != 0)
      return _value.get(key).getOne();
    else
      return UnsetValue.UNSET;
  }

  static private void printDepth(FeatureExpr ctx, VWriteStream out, int depth)
    throws java.io.IOException
  {
    for (int i = depth; i > 0; i--)
      out.print(VHelper.noCtx(), ' ');
  }

  public void printRImpl(Env env, FeatureExpr ctx,
                         VWriteStream out,
                         int depth,
                         IdentityHashMap<Value, String> valueSet)
    throws IOException
  {

    if ((_flags & STD_PROP_LIST) != 0) {
      // XXX:
      out.print(ctx, "ArrayObject");
      out.print(ctx, ' ');
      out.println(ctx, "Object");
      printDepth(ctx, out, 4 * depth);
      out.println(ctx, "(");
      out.print(ctx, ")");
    }
    else {
      out.print(ctx, "ArrayObject");
      out.print(ctx, ' ');
      out.println(ctx, "Object");
      printDepth(ctx, out, 4 * depth);
      out.println(ctx, "(");

      depth++;


      java.util.Iterator<VEntry> iterator
        = _value.getIterator(env);

      while (iterator.hasNext()) {
        VEntry entry = iterator.next();
        FeatureExpr innerCtx = ctx.and(entry.getCondition());

        Value key = entry.getKey();
        EnvVar value = entry.getEnvVar();

        printDepth(innerCtx, out, 4 * depth);

        out.print(innerCtx, "[" + key + "] => ");

        final int d = depth;
        value.getValue().sforeach(innerCtx, (c, a) -> a.printR(env, c, out, d + 1, valueSet));

        out.println(innerCtx);
      }

      depth--;

      printDepth(ctx, out, 4 * depth);
      out.println(ctx, ")");
    }
  }

  public void varDumpImpl(Env env, FeatureExpr ctx,
                          Value object,
                          VWriteStream out,
                          int depth,
                          IdentityHashMap<Value, String> valueSet)
    throws IOException
  {
    String name = object.getClassName();

    if ((_flags & STD_PROP_LIST) != 0) {
      // XXX:
      out.println(ctx, "object(" + name + ") (0) {");
      out.print(ctx, "}");

    }
    else {
      out.println(ctx, "object(" + name + ") (" + _value.getSize() + ") {");

      depth++;

      java.util.Iterator<VEntry> iterator
        = _value.getIterator(env);

      while (iterator.hasNext()) {
        VEntry entry = iterator.next();
        FeatureExpr innerCtx = ctx.and(entry.getCondition());

        Value key = entry.getKey();
        EnvVar value = entry.getEnvVar();

        printDepth(innerCtx, out, 2 * depth);

        out.print(innerCtx, "[");

        if (key instanceof StringValue)
          out.print(innerCtx, "\"" + key + "\"");
        else
          out.print(innerCtx, key);

        out.println(innerCtx, "]=>");

        printDepth(innerCtx, out, 2 * depth);

        final int d = depth;
        value.getValue().sforeach(innerCtx, (c, a) -> a.varDump(env, c, out, d, valueSet));

        out.println(innerCtx);
      }

      depth--;

      printDepth(ctx, out, 2 * depth);

      out.print(ctx, "}");
    }
  }
}
