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

package com.caucho.quercus.marshal;

import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.NullValue;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.expr.Expr;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.VHelper;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Code for marshaling (PHP to Java) and unmarshaling (Java to PHP) arguments.
 */
public class ClassMarshal extends Marshal {
  private static final Logger log
    = Logger.getLogger(ClassMarshal.class.getName());

  public static final ClassMarshal MARSHAL = new ClassMarshal();

  @Override
  public boolean isString()
  {
    return true;
  }

  @Override
  public boolean isReadOnly()
  {
    return true;
  }

  @Override
  public Object marshal(Env env, FeatureExpr ctx, Expr expr, Class expectedClass)
  {
    return marshalValue(env, ctx, expr.eval(env, VHelper.noCtx()).getOne(), expectedClass);
  }

  @Override
  public Object marshalValue(Env env, FeatureExpr ctx, Value value, Class expectedClass)
  {
    Object obj = value.toJavaObject();

    if (obj instanceof Class)
      return obj;
    else {
      Thread thread = Thread.currentThread();
      ClassLoader loader = thread.getContextClassLoader();

      try {
        String className = value.toJavaString();

        if (className == null)
          return null;

        return Class.forName(className, false, loader);
      } catch (ClassNotFoundException e) {
        log.log(Level.FINE, e.toString(), e);

        env.warning("class argument is an unknown class: " + e);

        return null;
      }
    }
  }

  @Override
  public Value unmarshal(Env env, FeatureExpr ctx, Object value)
  {
    if (value == null)
      return NullValue.NULL;
    else
      return env.wrapJava(value);
  }

  @Override
  protected int getMarshalingCostImpl(Value argValue)
  {
    Object javaValue = argValue.toJavaObject();

    if (Class.class.equals(javaValue))
      return Marshal.COST_IDENTICAL;
    else
      return argValue.toStringMarshalCost() + 1;

    /*
    if (argValue.isString()) {
      if (argValue.isUnicode())
        return Marshal.UNICODE_STRING_COST;
      else if (argValue.isBinary())
        return Marshal.BINARY_STRING_COST;
      else
        return Marshal.PHP5_STRING_COST;
    }
    else if (! (argValue.isArray() || argValue.isObject()))
      return Marshal.THREE;
    else
      return Marshal.FOUR;
    */
  }

  @Override
  public int getMarshalingCost(Expr expr)
  {
    if (expr.isString())
      return Marshal.ZERO;
    else
      return Marshal.FOUR;
  }

  @Override
  public Class getExpectedClass()
  {
    return Class.class;
  }
}
