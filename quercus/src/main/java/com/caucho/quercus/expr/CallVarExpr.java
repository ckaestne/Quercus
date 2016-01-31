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
import com.caucho.quercus.env.Env;
import com.caucho.quercus.env.NullValue;
import com.caucho.quercus.env.Value;
import com.caucho.quercus.env.ValueOrVar;
import com.caucho.quercus.parser.QuercusParser;
import com.caucho.util.L10N;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;
import edu.cmu.cs.varex.VHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * A "$foo(...)" function call.
 */
public class CallVarExpr extends Expr {
  private static final L10N L = new L10N(CallExpr.class);
  
  protected final Expr _name;
  protected final Expr []_args;

  public CallVarExpr(Location location, Expr name, ArrayList<Expr> args)
  {
    super(location);
    _name = name;

    _args = new Expr[args.size()];
    args.toArray(_args);
  }

  public CallVarExpr(Location location, Expr name, Expr []args)
  {
    super(location);
    _name = name;

    _args = args;
  }

  public CallVarExpr(Expr name, ArrayList<Expr> args)
  {
    this(Location.UNKNOWN, name, args);
  }

  public CallVarExpr(Expr name, Expr []args)
  {
    this(Location.UNKNOWN, name, args);
  }

  /**
   * Returns the reference of the value.
   * @param location
   */
  @Override
  public Expr createRef(QuercusParser parser)
  {
    return parser.getFactory().createRef(this);
  }

  /**
   * Returns the copy of the value.
   * @param location
   */
  @Override
  public Expr createCopy(ExprFactory factory)
  {
    return this;
  }
  
  @Override
  @Nonnull protected V<? extends ValueOrVar> _eval(Env env, FeatureExpr ctx)
  {
    return evalImpl(env, ctx, false, false);
  }
  
  
  @Override
  public V<? extends ValueOrVar> evalRef(Env env, FeatureExpr ctx)
  {
    return evalImpl(env, ctx, true, false);
  }
  
  
  @Override
  public @Nonnull V<? extends Value> evalCopy(Env env, FeatureExpr ctx)
  {
    return evalImpl(env, ctx, false, true);
  }
  
  /**
   * Evaluates the expression.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public V<? extends Value> evalImpl(Env env, FeatureExpr ctx, boolean isRef, boolean isCopy)
  {
    V<? extends Value> value = VHelper._getValues(_name.eval(env, ctx));
    
    V<? extends ValueOrVar>[] args = evalArgs(env, _args, ctx);

    env.pushCall(this, NullValue.NULL, null);

    try {
      env.checkTimeout();
      
      if (isRef)
        return value.vflatMap(ctx, (c,a)->a.callRef(env, c, args).map((b)->b.toValue())); //TODO V
      else if (isCopy)
        return value.vflatMap(ctx, (c,a)->a.call(env, c, args).map((b)->b.toValue().copyReturn()));
      else
        return value.vflatMap(ctx, (c,a)->a.call(env, c, args).map((b)->b.toValue()));
    } finally {
      env.popCall();
    }
  }
  
  public String toString()
  {
    return _name + "()";
  }
}

