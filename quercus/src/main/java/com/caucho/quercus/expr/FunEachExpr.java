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
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;

/**
 * Represents a PHP each expression.
 */
public class FunEachExpr extends AbstractUnaryExpr {
  private final L10N L = new L10N(FunEachExpr.class);
  
  private boolean _isVar;
  
  public FunEachExpr(Location location, Expr expr)
    throws IOException
  {
    super(location, expr);
    
    _isVar = expr.isVar();
  }

  public FunEachExpr(Expr expr)
  {
    super(expr);
    
    _isVar = expr.isVar();
  }

  /**
   * Evaluates the expression.
   *
   * @param env the calling environment.
   *
   * @param ctx
   * @return the expression value.
   */
  public @NonNull V<? extends Value> eval(Env env, FeatureExpr ctx)
  {
    if (! _isVar) {
      env.error(L.l("each() argument must be a variable at '{0}'", getExpr()));
      
      return VHelper.toV(NullValue.NULL);
    }

    ValueOrVar var = getExpr().evalRef(env, ctx).getOne();
    Value value = var.toValue();

    if (value instanceof ArrayValue) {
      ArrayValue array = (ArrayValue) value;

      return VHelper.toV(array.each());
    }
    else {
      env.warning(L.l("each() argument must be an array at '{0}'",
                      value.getClass().getSimpleName()));
    
      return VHelper.toV(BooleanValue.FALSE);
    }
  }
}

