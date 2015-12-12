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

package com.caucho.quercus.statement;

import com.caucho.quercus.Location;
import com.caucho.quercus.env.*;
import com.caucho.quercus.expr.Expr;
import de.fosd.typechef.featureexpr.FeatureExpr;
import edu.cmu.cs.varex.V;

import javax.annotation.Nonnull;

/**
 * Represents a while statement.
 */
public class WhileStatement extends Statement {
  protected final Expr _test;
  protected final Statement _block;
  protected final String _label;

  public WhileStatement(Location location,
                        Expr test,
                        Statement block,
                        String label)
  {
    super(location);

    _test = test;
    _block = block;
    _label = label;
    
    block.setParent(this);
  }

  @Override
  public boolean isLoop()
  {
    return true;
  }

  public @Nonnull
  V<? extends ValueOrVar> execute(Env env, FeatureExpr ctx)
  {
    V<? extends ValueOrVar> value = V.one(null);
    try {
      env.setLocation(getLocation());

      V<? extends Boolean> conditionValue = _test.evalBoolean(env, ctx);
      ctx = ctx.and(conditionValue.when(k -> k));
      while (ctx.isSatisfiable()) {
        env.checkTimeout();

        V<? extends ValueOrVar> vresult = _block.execute(env, ctx);
        vresult = vresult.vmap(ctx, (c,v)->{
          if (v instanceof BreakValue) {
            BreakValue breakValue = (BreakValue) v;

            int target = breakValue.getTarget();

            if (target > 1)
              return new BreakValue(target - 1);
            else
              return new BreakValue(target);
          }
          else if (v instanceof ContinueValue) {
            ContinueValue conValue = (ContinueValue) v;

            int target = conValue.getTarget();

            if (target > 1) {
              return new ContinueValue(target - 1);
            } else
              return null;
          }
          return v;
        });

        value = V.choice(ctx, vresult, value);

        ctx = ctx.and(value.when(x -> x == null));

        env.setLocation(getLocation());

        conditionValue = _test.evalBoolean(env, ctx);
        ctx = ctx.and(conditionValue.when(k -> k));
      }
    }
    catch (RuntimeException e) {
      rethrow(e, RuntimeException.class);
    }

    return value.map(x->
            (x instanceof BreakValue)&&(((BreakValue)x).getTarget()==0)?null:x);
  }
}

