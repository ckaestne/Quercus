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

package com.caucho.quercus.function;

import com.caucho.quercus.env.*;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;

/**
 * Represents a function
 */
@SuppressWarnings("serial")
public class FunSpecialCall extends AbstractFunction {
  private AbstractFunction _call;
  private StringValue _name;

  public FunSpecialCall(AbstractFunction call, StringValue name)
  {
    _call = call;
    _name = name;
  }

  /**
   * Evaluates the function.
   */
  @Override
  public V<? extends Value> call(Env env, FeatureExpr ctx, Value[] args)
  {
    ArrayValueImpl arrayArgs = new ArrayValueImpl(args);

    return _call.call(env, ctx, _name, arrayArgs);
  }

  /**
   * Evaluates the function.
   */
  @Override
  public V<? extends Value> callMethod(Env env,  FeatureExpr ctx,
                          QuercusClass qClass,
                          Value qThis,
                          Value []args)
  {
    ArrayValueImpl arrayArgs = new ArrayValueImpl(args);

    return _call.callMethod(env, ctx, qClass, qThis,
                            _name, arrayArgs);
  }

  /**
   * Evaluates the function.
   */
  @Override
  public V<? extends Value> callMethodRef(Env env,  FeatureExpr ctx,
                             QuercusClass qClass,
                             Value qThis,
                             Value []args)
  {
    ArrayValueImpl arrayArgs = new ArrayValueImpl(args);

    return _call.callMethodRef(env, ctx, qClass, qThis,
                               _name, arrayArgs);
  }

  @Override
  public String toString()
  {
    return getClass().getSimpleName() + "[" + getName() + "]";
  }
}

