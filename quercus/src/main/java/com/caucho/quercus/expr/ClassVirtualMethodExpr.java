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

package com.caucho.quercus.expr;

import com.caucho.quercus.Location;
import com.caucho.quercus.env.*;
import com.caucho.util.L10N;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;

import java.util.ArrayList;

/**
 * Represents a PHP parent:: method call expression.
 * XXX: better name?
 */
public class ClassVirtualMethodExpr extends Expr {
  private static final L10N L = new L10N(ClassVirtualMethodExpr.class);

  protected final StringValue _methodName;

  private final int _hash;
  protected final Expr []_args;

  protected boolean _isMethod;

  public ClassVirtualMethodExpr(Location location,
                                StringValue methodName,
                                ArrayList<Expr> args)
  {
    super(location);

    _methodName = methodName;
    _hash = methodName.hashCodeCaseInsensitive();

    _args = new Expr[args.size()];
    args.toArray(_args);
  }

  public ClassVirtualMethodExpr(Location location,
                                StringValue name,
                                Expr []args)
  {
    super(location);

    _methodName =  name;
    _hash = name.hashCodeCaseInsensitive();

    _args = args;
  }

  /**
   * Evaluates the expression.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public V<? extends Value> eval(Env env, FeatureExpr ctx)
  {
    Value qThis = env.getThis();

    QuercusClass cls = qThis.getQuercusClass();

    if (cls == null) {
      env.error(L.l("no calling class found"), getLocation());

      return VHelper.toV(NullValue.NULL);
    }

    Value []values = evalArgs(env, _args, VHelper.noCtx()).getOne();

    env.pushCall(this, cls, values);

    try {
      env.checkTimeout();

      return cls.callMethod(env, ctx, qThis, _methodName, _hash, values);
    } finally {
      env.popCall();
    }
  }

  public String toString()
  {
    return "static::" + _methodName + "()";
  }
}

