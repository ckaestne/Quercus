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

import com.caucho.quercus.annotation.Optional;
import com.caucho.quercus.annotation.This;
import com.caucho.quercus.env.*;
import com.caucho.quercus.lib.ArrayModule;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.VHelper;
import edu.cmu.cs.varex.VWriteStream;

import java.io.IOException;
import java.util.IdentityHashMap;

public class ArrayIterator
  implements SeekableIterator,
             ArrayAccess,
             Countable
{
  public static final int STD_PROP_LIST = 0x00000001;
  public static final int ARRAY_AS_PROPS = 0x00000002;

  private Env _env;
  private Value _qThis;
  private Value _value = NullValue.NULL;
  private int _flags;

  private java.util.Iterator<VEntry> _iterator;
  private VEntry _current;

  public ArrayIterator(Env env,
                       @This Value qThis,
                       @Optional Value value,
                       @Optional int flags)
  {
    _env = env;
    _qThis = qThis;

    if (value == null || value.isNull()) {
      value = new ArrayValueImpl();
    }

    _value = value;
    _flags = flags;

    rewindJava(env);
  }

  public void append(Value value)
  {
    _value.put(VHelper.noCtx(), value);
  }

  public void asort(ArrayValue array, @Optional long sortFlag)
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

  @Override
  public Value current(Env env)
  {
    return _current == null ? UnsetValue.UNSET : _current.getEnvVar().getOne();
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
  public Value key(Env env)
  {
    return _current == null ? UnsetValue.UNSET : _current.getKey();
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
  public void next(Env env)
  {
    if (_iterator == null)
      rewind(env);

    if (_iterator.hasNext())
      _current = _iterator.next();
    else
      _current = null;
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

  public void rewindJava(Env env)
  {
    if (_qThis != null) {
      _qThis.callMethod(env, VHelper.noCtx(), env.createString("rewind"));
    }
    else {
      rewind(env);
    }
  }

  @Override
  public void rewind(Env env)
  {
    // php/4as8
    _iterator = _value.getBaseIterator(_env);

    if (_iterator.hasNext())
      _current = _iterator.next();
    else
      _current = null;
  }

  public void setFlags(Value flags)
  {
    _flags = flags.toInt();
  }

  @Override
  public void seek(Env env, int index)
  {
    rewindJava(env);

    for (int i = 0; i < index; i++) {
      if (! _iterator.hasNext()) {
        _current = null;
        break;
      }

      _current = _iterator.next();
    }
  }

  public void uasort(Callback func, @Optional long sortFlag)
  {
    if (_value instanceof ArrayValue)
      ArrayModule.uasort(_env, Var.create(_value), func, sortFlag);
  }

  public void uksort(Callback func, @Optional long sortFlag)
  {
    if (_value instanceof ArrayValue)
      ArrayModule.uksort(_env, Var.create(_value), func, sortFlag);
  }

  @Override
  public boolean valid(Env env)
  {
    if (_iterator == null)
      rewind(env);

    return _current != null;
  }


  private static void printDepth(FeatureExpr ctx, VWriteStream out, int depth)
    throws java.io.IOException
  {
    for (int i = depth; i > 0; i--)
      out.print(ctx, ' ');
  }

  public void varDumpImpl(Env env, FeatureExpr ctx,
                          Value obj,
                          VWriteStream out,
                          int depth,
                          IdentityHashMap<Value, String> valueSet)
    throws IOException
  {
    String name = "ArrayIterator";

    if (obj != null)
      name = obj.getClassName();

    if ((_flags & STD_PROP_LIST) != 0) {
      // XXX:  env.getThis().varDumpObject(env, out, depth, valueSet);
    }
    else {
      Value arrayValue = _value;

      out.println(ctx, "object(" + name + ") (" + arrayValue.getCount(env) + ") {");

      depth++;

      java.util.Iterator<VEntry> iterator
        = arrayValue.getIterator(env);

      while (iterator.hasNext()) {
        VEntry entry = iterator.next();
        FeatureExpr innerCtx = ctx.and(entry.getCondition());

        Value key = entry.getKey();
        EnvVar value = entry.getEnvVar();

        printDepth(innerCtx, out, 2 * depth);

        out.print(innerCtx, "[");

        if (key.isString())
          out.print(innerCtx, "\"" + key + "\"");
        else
          out.print(innerCtx, key);

        out.println(innerCtx, "]=>");

        printDepth(innerCtx, out, 2 * depth);

        value.getOne(innerCtx).varDump(env, innerCtx, out, depth, valueSet);

        out.println(innerCtx);
      }

      depth--;

      printDepth(ctx, out, 2 * depth);

      out.print(ctx, "}");
    }
  }
}
