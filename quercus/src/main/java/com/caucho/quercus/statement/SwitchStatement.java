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
import edu.cmu.cs.varex.VHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * Represents a switch statement.
 */
public class SwitchStatement extends Statement {
  protected final Expr _value;

  protected final Expr[][] _cases;
  protected final BlockStatement[] _blocks;

  protected final Statement _defaultBlock;
  protected final String _label;

  public SwitchStatement(Location location,
                         Expr value,
                         ArrayList<Expr[]> caseList,
                         ArrayList<BlockStatement> blockList,
                         Statement defaultBlock,
                         String label)
  {
    super(location);

    _value = value;

    _cases = new Expr[caseList.size()][];
    caseList.toArray(_cases);

    _blocks = new BlockStatement[blockList.size()];
    blockList.toArray(_blocks);

    _defaultBlock = defaultBlock;
    
    for (int i = 0; i < _blocks.length; i++) {
      _blocks[i].setParent(this);
    }
    
    if (_defaultBlock != null)
      _defaultBlock.setParent(this);
    
    _label = label;
  }

  /**
   * Executes the 'switch' statement, returning any value.
   */
  @Override
  public
  @Nonnull
  V<? extends ValueOrVar> execute(Env env, FeatureExpr ctx) {
    V<? extends ValueOrVar> result = V.one(null);
    try {
      V<? extends Value> vtestValue = _value.eval(env, ctx);

      int len = _cases.length;

      for (int i = 0; i < len; i++) {
        final int caseIdx = i;
        Expr[] values = _cases[i];

        for (int j = 0; j < values.length; j++)
          if (ctx.isSatisfiable()) {
            V<? extends ValueOrVar> vcaseValue = values[j].eval(env, ctx);

            vcaseValue = VHelper.<Value, ValueOrVar, ValueOrVar>sflatMapAll(ctx, vtestValue, vcaseValue, (c, testValue, caseValue) -> {
              if (testValue.eq(caseValue.toValue())) {
                V<? extends ValueOrVar> vretValue = _blocks[caseIdx].execute(env, c);

                return vretValue.<ValueOrVar>map(retValue -> {
                  if (retValue instanceof BreakValue) {
                    BreakValue breakValue = (BreakValue) retValue;

                    int target = breakValue.getTarget();

                    return new BreakValue(target - 1);
                  } else if (retValue instanceof ContinueValue) {
                    ContinueValue conValue = (ContinueValue) retValue;

                    int target = conValue.getTarget();

                    if (target > 1)
                      return new ContinueValue(target - 1);
                    else
                      return null;
                  } else
                    return retValue;
                });
              }
              return V.one(null);
            });
            result = V.choice(ctx, vcaseValue, result);
            ctx = ctx.and(result.when(k -> k == null));
          }
      }

      if (_defaultBlock != null && ctx.isSatisfiable()) {
        V<? extends ValueOrVar> retValue = _defaultBlock.execute(env, ctx).
                map(v -> v instanceof BreakValue ? null : v);
        result = V.choice(ctx, retValue, result);
      }

    } catch (RuntimeException e) {
      rethrow(e, RuntimeException.class);
    }

    return result.map(x->
            (x instanceof BreakValue)&&(((BreakValue)x).getTarget()<=0)?null:x);
  }

  /**
   * Returns true if control can go past the statement.
   */
  @Override
  public int fallThrough()
  {
    return FALL_THROUGH;
    /* php/367t, php/367u
    if (_defaultBlock == null)
      return FALL_THROUGH;

    int fallThrough = _defaultBlock.fallThrough();

    for (int i = 0; i < _blocks.length; i++) {
      fallThrough &= _blocks[i].fallThrough();
    }

    if (fallThrough == BREAK_FALL_THROUGH)
      return 0;
    else
      return fallThrough;
    */
  }
}

