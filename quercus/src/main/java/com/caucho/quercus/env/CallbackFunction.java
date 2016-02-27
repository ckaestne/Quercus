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
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * Represents a call to a function.
 */
@SuppressWarnings("serial")
public class CallbackFunction extends Callback {
  // public static final CallbackFunction INVALID_CALLBACK
  // = new CallbackFunction(null, "Invalid Callback");

  private StringValue _funName;
  private @Nonnull V<? extends AbstractFunction> _fun = V.one(null);

  public CallbackFunction(Env env, StringValue funName)
  {
    _funName = funName;
  }

  public CallbackFunction(AbstractFunction fun)
  {
    _fun = V.one(fun);
  }

  public CallbackFunction(AbstractFunction fun, StringValue funName)
  {
    _fun = V.one(fun);
    _funName = funName;
  }

  /**
   * Allow subclasses to set the abstract function directly.
   */
  protected void setFunction(AbstractFunction fun)
  {
    _fun = V.one(fun);
  }

  @Override
  public boolean isValid(Env env)
  {
    if (_fun.getOne() != null) {
      return true;
    }

    _fun = env.findFunction(_funName);

    return _fun.getOne() != null;
  }

  /**
   * Serializes the value.
   */
  @Override
  public void serialize(Env env, StringBuilder sb)
  {
    CharSequence name;

    if (_fun.getOne() != null)
      name = _fun.getOne().getName();
    else
      name = _funName;

    sb.append("S:");
    sb.append(name.length());
    sb.append(":\"");
    sb.append(name);
    sb.append("\";");
  }

  /**
   * Evaluates the callback with no arguments.
   *  @param env the calling environment
   * @param ctx
   */
  @Override
  public @Nonnull
  V<? extends ValueOrVar> call(Env env, FeatureExpr ctx)
  {
    return getFunction(env, ctx).getOne().call(env, ctx);
  }


  @Override
  public V<? extends ValueOrVar> call(Env env, FeatureExpr ctx, V<? extends ValueOrVar>[] args)
  {
    return getFunction(env, ctx).getOne().call(env, ctx, args);
  }

  @Override
  public String getCallbackName()
  {
    return _funName.toString();
  }

  public V<? extends AbstractFunction> getFunction(Env env, FeatureExpr ctx)
  {
    if (_fun==null || _fun.when(f->f==null).isSatisfiable())
      _fun = _fun.pflatMap(ctx, (c, f) -> f == null ? env.getFunction(c, _funName) : V.one(f), Function.identity());

    return _fun;
  }

  @Override
  public boolean isInternal(Env env)
  {
    return getFunction(env, VHelper.noCtx()).getOne() instanceof JavaInvoker;
  }

  @Override
  public String getDeclFileName(Env env)
  {
    return getFunction(env, VHelper.noCtx()).getOne().getDeclFileName(env);
  }

  @Override
  public int getDeclStartLine(Env env)
  {
    return getFunction(env, VHelper.noCtx()).getOne().getDeclStartLine(env);
  }

  @Override
  public int getDeclEndLine(Env env)
  {
    return getFunction(env, VHelper.noCtx()).getOne().getDeclEndLine(env);
  }

  @Override
  public String getDeclComment(Env env)
  {
    return getFunction(env, VHelper.noCtx()).getOne().getDeclComment(env);
  }

  @Override
  public boolean isReturnsReference(Env env)
  {
    return getFunction(env, VHelper.noCtx()).getOne().isReturnsReference(env);
  }

  @Override
  public Arg []getArgs(Env env)
  {
    return getFunction(env, VHelper.noCtx()).getOne().getArgs(env);
  }

  /**
   * Exports the value.
   */
  @Override
  protected void varExportImpl(StringValue sb, int level)
  {
    sb.append("'' . \"\\0\" . '" + _funName.substring(1) + "'");
  }

  public String toString()
  {
    return getClass().getName() + '[' + _funName + ']';
  }
}
