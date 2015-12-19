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
import com.caucho.quercus.program.Arg;
import com.caucho.util.L10N;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;
import edu.cmu.cs.varex.VWriteStream;

import java.util.IdentityHashMap;

/**
 * Represents a call to an object's method
 */
@SuppressWarnings("serial")
public class CallbackClassMethod extends Callback {
  private static final L10N L = new L10N(CallbackClassMethod.class);

  private final QuercusClass _qClass;

  private final StringValue _methodName;
  private final int _hash;

  private final Value _qThis;

  public CallbackClassMethod(QuercusClass qClass,
                             StringValue methodName,
                             Value qThis)
  {
    _qClass = qClass;

    _methodName = methodName;

    _hash = methodName.hashCodeCaseInsensitive();

    _qThis = qThis;
  }

  public CallbackClassMethod(QuercusClass qClass,
                             StringValue methodName)
  {
    this(qClass, methodName, qClass);
  }



  @Override
  public V<? extends ValueOrVar> call(Env env, FeatureExpr ctx, V<? extends ValueOrVar>[] args)
  {
    return _qClass.callMethod(env, ctx, _qThis, _methodName, _hash, args);
  }

  @Override
  public void varDumpImpl(Env env,
                          VWriteStream out,
                          int depth,
                          IdentityHashMap<Value, String> valueSet) {
    out.print(VHelper.noCtx(), getClass().getName());
    out.print(VHelper.noCtx(), '[');
    out.print(VHelper.noCtx(), _qClass.getName());
    out.print(VHelper.noCtx(), ", ");
    out.print(VHelper.noCtx(), _methodName);
    out.print(VHelper.noCtx(), ']');
  }

  @Override
  public boolean isValid(Env env)
  {
    return true;
  }

  @Override
  public String getCallbackName()
  {
    return _qClass.getName() + "::" +  _methodName.toString();
  }

  @Override
  public String getDeclFileName(Env env)
  {
    return getMethod().getDeclFileName(env);
  }

  @Override
  public int getDeclStartLine(Env env)
  {
    return getMethod().getDeclStartLine(env);
  }

  @Override
  public int getDeclEndLine(Env env)
  {
    return getMethod().getDeclEndLine(env);
  }

  @Override
  public String getDeclComment(Env env)
  {
    return getMethod().getDeclComment(env);
  }

  @Override
  public boolean isReturnsReference(Env env)
  {
    return getMethod().isReturnsReference(env);
  }

  @Override
  public Arg []getArgs(Env env)
  {
    return getMethod().getArgs(env);
  }

  private AbstractFunction getMethod()
  {
    return _qClass.getFunction(_methodName);
  }

  @Override
  public boolean isInternal(Env env)
  {
    // return _fun instanceof JavaInvoker;
    return false;
  }

  private Value error(Env env)
  {
    env.warning(L.l("{0}::{1}() is an invalid callback method",
                    _qClass.getName(), _methodName));

    return NullValue.NULL;
  }
}
